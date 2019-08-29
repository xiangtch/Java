/*     */ package sun.net.httpserver;
/*     */ 
/*     */ import com.sun.net.httpserver.Filter.Chain;
/*     */ import com.sun.net.httpserver.Headers;
/*     */ import com.sun.net.httpserver.HttpContext;
/*     */ import com.sun.net.httpserver.HttpExchange;
/*     */ import com.sun.net.httpserver.HttpHandler;
/*     */ import com.sun.net.httpserver.HttpServer;
/*     */ import com.sun.net.httpserver.HttpsConfigurator;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.BindException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ServerImpl
/*     */   implements TimeSource
/*     */ {
/*     */   private String protocol;
/*     */   private boolean https;
/*     */   private Executor executor;
/*     */   private HttpsConfigurator httpsConfig;
/*     */   private SSLContext sslContext;
/*     */   private ContextList contexts;
/*     */   private InetSocketAddress address;
/*     */   private ServerSocketChannel schan;
/*     */   private Selector selector;
/*     */   private SelectionKey listenerKey;
/*     */   private Set<HttpConnection> idleConnections;
/*     */   private Set<HttpConnection> allConnections;
/*     */   private Set<HttpConnection> reqConnections;
/*     */   private Set<HttpConnection> rspConnections;
/*     */   private List<Event> events;
/*  65 */   private Object lolock = new Object();
/*  66 */   private volatile boolean finished = false;
/*  67 */   private volatile boolean terminating = false;
/*  68 */   private boolean bound = false;
/*  69 */   private boolean started = false;
/*     */   private volatile long time;
/*  71 */   private volatile long subticks = 0L;
/*     */   
/*     */   private volatile long ticks;
/*     */   private HttpServer wrapper;
/*  75 */   static final int CLOCK_TICK = ServerConfig.getClockTick();
/*  76 */   static final long IDLE_INTERVAL = ServerConfig.getIdleInterval();
/*  77 */   static final int MAX_IDLE_CONNECTIONS = ServerConfig.getMaxIdleConnections();
/*  78 */   static final long TIMER_MILLIS = ServerConfig.getTimerMillis();
/*  79 */   static final long MAX_REQ_TIME = getTimeMillis(ServerConfig.getMaxReqTime());
/*  80 */   static final long MAX_RSP_TIME = getTimeMillis(ServerConfig.getMaxRspTime());
/*  81 */   static final boolean timer1Enabled = (MAX_REQ_TIME != -1L) || (MAX_RSP_TIME != -1L);
/*     */   private Timer timer;
/*     */   private Timer timer1;
/*     */   private Logger logger;
/*     */   Dispatcher dispatcher;
/*     */   
/*     */   ServerImpl(HttpServer paramHttpServer, String paramString, InetSocketAddress paramInetSocketAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/*  90 */     this.protocol = paramString;
/*  91 */     this.wrapper = paramHttpServer;
/*  92 */     this.logger = Logger.getLogger("com.sun.net.httpserver");
/*  93 */     ServerConfig.checkLegacyProperties(this.logger);
/*  94 */     this.https = paramString.equalsIgnoreCase("https");
/*  95 */     this.address = paramInetSocketAddress;
/*  96 */     this.contexts = new ContextList();
/*  97 */     this.schan = ServerSocketChannel.open();
/*  98 */     if (paramInetSocketAddress != null) {
/*  99 */       ServerSocket localServerSocket = this.schan.socket();
/* 100 */       localServerSocket.bind(paramInetSocketAddress, paramInt);
/* 101 */       this.bound = true;
/*     */     }
/* 103 */     this.selector = Selector.open();
/* 104 */     this.schan.configureBlocking(false);
/* 105 */     this.listenerKey = this.schan.register(this.selector, 16);
/* 106 */     this.dispatcher = new Dispatcher();
/* 107 */     this.idleConnections = Collections.synchronizedSet(new HashSet());
/* 108 */     this.allConnections = Collections.synchronizedSet(new HashSet());
/* 109 */     this.reqConnections = Collections.synchronizedSet(new HashSet());
/* 110 */     this.rspConnections = Collections.synchronizedSet(new HashSet());
/* 111 */     this.time = System.currentTimeMillis();
/* 112 */     this.timer = new Timer("server-timer", true);
/* 113 */     this.timer.schedule(new ServerTimerTask(), CLOCK_TICK, CLOCK_TICK);
/* 114 */     if (timer1Enabled) {
/* 115 */       this.timer1 = new Timer("server-timer1", true);
/* 116 */       this.timer1.schedule(new ServerTimerTask1(), TIMER_MILLIS, TIMER_MILLIS);
/* 117 */       this.logger.config("HttpServer timer1 enabled period in ms:  " + TIMER_MILLIS);
/* 118 */       this.logger.config("MAX_REQ_TIME:  " + MAX_REQ_TIME);
/* 119 */       this.logger.config("MAX_RSP_TIME:  " + MAX_RSP_TIME);
/*     */     }
/* 121 */     this.events = new LinkedList();
/* 122 */     this.logger.config("HttpServer created " + paramString + " " + paramInetSocketAddress);
/*     */   }
/*     */   
/*     */   public void bind(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException {
/* 126 */     if (this.bound) {
/* 127 */       throw new BindException("HttpServer already bound");
/*     */     }
/* 129 */     if (paramInetSocketAddress == null) {
/* 130 */       throw new NullPointerException("null address");
/*     */     }
/* 132 */     ServerSocket localServerSocket = this.schan.socket();
/* 133 */     localServerSocket.bind(paramInetSocketAddress, paramInt);
/* 134 */     this.bound = true;
/*     */   }
/*     */   
/*     */   public void start() {
/* 138 */     if ((!this.bound) || (this.started) || (this.finished)) {
/* 139 */       throw new IllegalStateException("server in wrong state");
/*     */     }
/* 141 */     if (this.executor == null) {
/* 142 */       this.executor = new DefaultExecutor(null);
/*     */     }
/* 144 */     Thread localThread = new Thread(this.dispatcher);
/* 145 */     this.started = true;
/* 146 */     localThread.start();
/*     */   }
/*     */   
/*     */   public void setExecutor(Executor paramExecutor) {
/* 150 */     if (this.started) {
/* 151 */       throw new IllegalStateException("server already started");
/*     */     }
/* 153 */     this.executor = paramExecutor;
/*     */   }
/*     */   
/*     */   private static class DefaultExecutor implements Executor {
/*     */     public void execute(Runnable paramRunnable) {
/* 158 */       paramRunnable.run();
/*     */     }
/*     */   }
/*     */   
/*     */   public Executor getExecutor() {
/* 163 */     return this.executor;
/*     */   }
/*     */   
/*     */   public void setHttpsConfigurator(HttpsConfigurator paramHttpsConfigurator) {
/* 167 */     if (paramHttpsConfigurator == null) {
/* 168 */       throw new NullPointerException("null HttpsConfigurator");
/*     */     }
/* 170 */     if (this.started) {
/* 171 */       throw new IllegalStateException("server already started");
/*     */     }
/* 173 */     this.httpsConfig = paramHttpsConfigurator;
/* 174 */     this.sslContext = paramHttpsConfigurator.getSSLContext();
/*     */   }
/*     */   
/*     */   public HttpsConfigurator getHttpsConfigurator() {
/* 178 */     return this.httpsConfig;
/*     */   }
/*     */   
/*     */   public void stop(int paramInt) {
/* 182 */     if (paramInt < 0) {
/* 183 */       throw new IllegalArgumentException("negative delay parameter");
/*     */     }
/* 185 */     this.terminating = true;
/* 186 */     try { this.schan.close(); } catch (IOException localIOException) {}
/* 187 */     this.selector.wakeup();
/* 188 */     long l = System.currentTimeMillis() + paramInt * 1000;
/* 189 */     while (System.currentTimeMillis() < l) {
/* 190 */       delay();
/* 191 */       if (this.finished) {
/*     */         break;
/*     */       }
/*     */     }
/* 195 */     this.finished = true;
/* 196 */     this.selector.wakeup();
/* 197 */     synchronized (this.allConnections) {
/* 198 */       for (HttpConnection localHttpConnection : this.allConnections) {
/* 199 */         localHttpConnection.close();
/*     */       }
/*     */     }
/* 202 */     this.allConnections.clear();
/* 203 */     this.idleConnections.clear();
/* 204 */     this.timer.cancel();
/* 205 */     if (timer1Enabled) {
/* 206 */       this.timer1.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized HttpContextImpl createContext(String paramString, HttpHandler paramHttpHandler)
/*     */   {
/* 213 */     if ((paramHttpHandler == null) || (paramString == null)) {
/* 214 */       throw new NullPointerException("null handler, or path parameter");
/*     */     }
/* 216 */     HttpContextImpl localHttpContextImpl = new HttpContextImpl(this.protocol, paramString, paramHttpHandler, this);
/* 217 */     this.contexts.add(localHttpContextImpl);
/* 218 */     this.logger.config("context created: " + paramString);
/* 219 */     return localHttpContextImpl;
/*     */   }
/*     */   
/*     */   public synchronized HttpContextImpl createContext(String paramString) {
/* 223 */     if (paramString == null) {
/* 224 */       throw new NullPointerException("null path parameter");
/*     */     }
/* 226 */     HttpContextImpl localHttpContextImpl = new HttpContextImpl(this.protocol, paramString, null, this);
/* 227 */     this.contexts.add(localHttpContextImpl);
/* 228 */     this.logger.config("context created: " + paramString);
/* 229 */     return localHttpContextImpl;
/*     */   }
/*     */   
/*     */   public synchronized void removeContext(String paramString) throws IllegalArgumentException {
/* 233 */     if (paramString == null) {
/* 234 */       throw new NullPointerException("null path parameter");
/*     */     }
/* 236 */     this.contexts.remove(this.protocol, paramString);
/* 237 */     this.logger.config("context removed: " + paramString);
/*     */   }
/*     */   
/*     */   public synchronized void removeContext(HttpContext paramHttpContext) throws IllegalArgumentException {
/* 241 */     if (!(paramHttpContext instanceof HttpContextImpl)) {
/* 242 */       throw new IllegalArgumentException("wrong HttpContext type");
/*     */     }
/* 244 */     this.contexts.remove((HttpContextImpl)paramHttpContext);
/* 245 */     this.logger.config("context removed: " + paramHttpContext.getPath());
/*     */   }
/*     */   
/*     */   public InetSocketAddress getAddress() {
/* 249 */     (InetSocketAddress)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public InetSocketAddress run() {
/* 252 */         return 
/*     */         
/* 254 */           (InetSocketAddress)ServerImpl.this.schan.socket().getLocalSocketAddress();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   Selector getSelector() {
/* 260 */     return this.selector;
/*     */   }
/*     */   
/*     */   void addEvent(Event paramEvent) {
/* 264 */     synchronized (this.lolock) {
/* 265 */       this.events.add(paramEvent);
/* 266 */       this.selector.wakeup();
/*     */     }
/*     */   }
/*     */   
/*     */   class Dispatcher implements Runnable
/*     */   {
/*     */     Dispatcher() {}
/*     */     
/*     */     private void handleEvent(Event paramEvent) {
/* 275 */       ExchangeImpl localExchangeImpl = paramEvent.exchange;
/* 276 */       HttpConnection localHttpConnection = localExchangeImpl.getConnection();
/*     */       try {
/* 278 */         if ((paramEvent instanceof WriteFinishedEvent))
/*     */         {
/* 280 */           int i = ServerImpl.this.endExchange();
/* 281 */           if ((ServerImpl.this.terminating) && (i == 0)) {
/* 282 */             ServerImpl.this.finished = true;
/*     */           }
/* 284 */           ServerImpl.this.responseCompleted(localHttpConnection);
/* 285 */           LeftOverInputStream localLeftOverInputStream = localExchangeImpl.getOriginalInputStream();
/* 286 */           if (!localLeftOverInputStream.isEOF()) {
/* 287 */             localExchangeImpl.close = true;
/*     */           }
/* 289 */           if ((localExchangeImpl.close) || (ServerImpl.this.idleConnections.size() >= ServerImpl.MAX_IDLE_CONNECTIONS)) {
/* 290 */             localHttpConnection.close();
/* 291 */             ServerImpl.this.allConnections.remove(localHttpConnection);
/*     */           }
/* 293 */           else if (localLeftOverInputStream.isDataBuffered())
/*     */           {
/* 295 */             ServerImpl.this.requestStarted(localHttpConnection);
/* 296 */             handle(localHttpConnection.getChannel(), localHttpConnection);
/*     */           } else {
/* 298 */             this.connsToRegister.add(localHttpConnection);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException) {
/* 303 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher (1)", localIOException);
/*     */         
/*     */ 
/* 306 */         localHttpConnection.close();
/*     */       }
/*     */     }
/*     */     
/* 310 */     final LinkedList<HttpConnection> connsToRegister = new LinkedList();
/*     */     
/*     */     void reRegister(HttpConnection paramHttpConnection)
/*     */     {
/*     */       try
/*     */       {
/* 316 */         SocketChannel localSocketChannel = paramHttpConnection.getChannel();
/* 317 */         localSocketChannel.configureBlocking(false);
/* 318 */         SelectionKey localSelectionKey = localSocketChannel.register(ServerImpl.this.selector, 1);
/* 319 */         localSelectionKey.attach(paramHttpConnection);
/* 320 */         paramHttpConnection.selectionKey = localSelectionKey;
/* 321 */         paramHttpConnection.time = (ServerImpl.this.getTime() + ServerImpl.IDLE_INTERVAL);
/* 322 */         ServerImpl.this.idleConnections.add(paramHttpConnection);
/*     */       } catch (IOException localIOException) {
/* 324 */         ServerImpl.dprint(localIOException);
/* 325 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher(8)", localIOException);
/* 326 */         paramHttpConnection.close();
/*     */       }
/*     */     }
/*     */     
/*     */     public void run() {
/* 331 */       while (!ServerImpl.this.finished)
/*     */         try {
/* 333 */           List localList = null;
/* 334 */           synchronized (ServerImpl.this.lolock) {
/* 335 */             if (ServerImpl.this.events.size() > 0) {
/* 336 */               localList = ServerImpl.this.events;
/* 337 */               ServerImpl.this.events = new LinkedList();
/*     */             }
/*     */           }
/*     */           
/* 341 */           if (localList != null) {
/* 342 */             for (??? = localList.iterator(); ((Iterator)???).hasNext();) { localObject2 = (Event)((Iterator)???).next();
/* 343 */               handleEvent((Event)localObject2);
/*     */             }
/*     */           }
/*     */           
/* 347 */           for (??? = this.connsToRegister.iterator(); ((Iterator)???).hasNext();) { localObject2 = (HttpConnection)((Iterator)???).next();
/* 348 */             reRegister((HttpConnection)localObject2);
/*     */           }
/* 350 */           this.connsToRegister.clear();
/*     */           
/* 352 */           ServerImpl.this.selector.select(1000L);
/*     */           
/*     */ 
/* 355 */           ??? = ServerImpl.this.selector.selectedKeys();
/* 356 */           Object localObject2 = ((Set)???).iterator();
/* 357 */           while (((Iterator)localObject2).hasNext()) {
/* 358 */             SelectionKey localSelectionKey = (SelectionKey)((Iterator)localObject2).next();
/* 359 */             ((Iterator)localObject2).remove();
/* 360 */             Object localObject3; HttpConnection localHttpConnection; if (localSelectionKey.equals(ServerImpl.this.listenerKey)) {
/* 361 */               if (!ServerImpl.this.terminating)
/*     */               {
/*     */ 
/* 364 */                 SocketChannel localSocketChannel = ServerImpl.this.schan.accept();
/*     */                 
/*     */ 
/* 367 */                 if (ServerConfig.noDelay()) {
/* 368 */                   localSocketChannel.socket().setTcpNoDelay(true);
/*     */                 }
/*     */                 
/* 371 */                 if (localSocketChannel != null)
/*     */                 {
/*     */ 
/* 374 */                   localSocketChannel.configureBlocking(false);
/* 375 */                   localObject3 = localSocketChannel.register(ServerImpl.this.selector, 1);
/* 376 */                   localHttpConnection = new HttpConnection();
/* 377 */                   localHttpConnection.selectionKey = ((SelectionKey)localObject3);
/* 378 */                   localHttpConnection.setChannel(localSocketChannel);
/* 379 */                   ((SelectionKey)localObject3).attach(localHttpConnection);
/* 380 */                   ServerImpl.this.requestStarted(localHttpConnection);
/* 381 */                   ServerImpl.this.allConnections.add(localHttpConnection);
/*     */                 }
/*     */               }
/* 384 */             } else { try { if (localSelectionKey.isReadable())
/*     */                 {
/* 386 */                   localObject3 = (SocketChannel)localSelectionKey.channel();
/* 387 */                   localHttpConnection = (HttpConnection)localSelectionKey.attachment();
/*     */                   
/* 389 */                   localSelectionKey.cancel();
/* 390 */                   ((SocketChannel)localObject3).configureBlocking(true);
/* 391 */                   if (ServerImpl.this.idleConnections.remove(localHttpConnection))
/*     */                   {
/*     */ 
/* 394 */                     ServerImpl.this.requestStarted(localHttpConnection);
/*     */                   }
/* 396 */                   handle((SocketChannel)localObject3, localHttpConnection);
/*     */                 }
/* 398 */                 else if (!$assertionsDisabled) { throw new AssertionError();
/*     */                 }
/*     */               } catch (CancelledKeyException localCancelledKeyException) {
/* 401 */                 handleException(localSelectionKey, null);
/*     */               } catch (IOException localIOException2) {
/* 403 */                 handleException(localSelectionKey, localIOException2);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 408 */           ServerImpl.this.selector.selectNow();
/*     */         } catch (IOException localIOException1) {
/* 410 */           ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", localIOException1);
/*     */         } catch (Exception localException1) {
/* 412 */           ServerImpl.this.logger.log(Level.FINER, "Dispatcher (7)", localException1);
/*     */         }
/*     */       try {
/* 415 */         ServerImpl.this.selector.close();
/*     */       } catch (Exception localException2) {}
/*     */     }
/*     */     
/* 419 */     private void handleException(SelectionKey paramSelectionKey, Exception paramException) { HttpConnection localHttpConnection = (HttpConnection)paramSelectionKey.attachment();
/* 420 */       if (paramException != null) {
/* 421 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher (2)", paramException);
/*     */       }
/* 423 */       ServerImpl.this.closeConnection(localHttpConnection);
/*     */     }
/*     */     
/*     */     public void handle(SocketChannel paramSocketChannel, HttpConnection paramHttpConnection) throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 430 */         Exchange localExchange = new Exchange(ServerImpl.this, paramSocketChannel, ServerImpl.this.protocol, paramHttpConnection);
/* 431 */         ServerImpl.this.executor.execute(localExchange);
/*     */       } catch (HttpError localHttpError) {
/* 433 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", localHttpError);
/* 434 */         ServerImpl.this.closeConnection(paramHttpConnection);
/*     */       } catch (IOException localIOException) {
/* 436 */         ServerImpl.this.logger.log(Level.FINER, "Dispatcher (5)", localIOException);
/* 437 */         ServerImpl.this.closeConnection(paramHttpConnection);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 442 */   static boolean debug = ServerConfig.debugEnabled();
/*     */   
/*     */   static synchronized void dprint(String paramString) {
/* 445 */     if (debug) {
/* 446 */       System.out.println(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */   static synchronized void dprint(Exception paramException) {
/* 451 */     if (debug) {
/* 452 */       System.out.println(paramException);
/* 453 */       paramException.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   Logger getLogger() {
/* 458 */     return this.logger;
/*     */   }
/*     */   
/*     */   private void closeConnection(HttpConnection paramHttpConnection) {
/* 462 */     paramHttpConnection.close();
/* 463 */     this.allConnections.remove(paramHttpConnection);
/* 464 */     switch (paramHttpConnection.getState()) {
/*     */     case REQUEST: 
/* 466 */       this.reqConnections.remove(paramHttpConnection);
/* 467 */       break;
/*     */     case RESPONSE: 
/* 469 */       this.rspConnections.remove(paramHttpConnection);
/* 470 */       break;
/*     */     case IDLE: 
/* 472 */       this.idleConnections.remove(paramHttpConnection);
/*     */     }
/*     */     
/* 475 */     assert (!this.reqConnections.remove(paramHttpConnection));
/* 476 */     assert (!this.rspConnections.remove(paramHttpConnection));
/* 477 */     assert (!this.idleConnections.remove(paramHttpConnection));
/*     */   }
/*     */   
/*     */   class Exchange
/*     */     implements Runnable
/*     */   {
/*     */     SocketChannel chan;
/*     */     HttpConnection connection;
/*     */     HttpContextImpl context;
/*     */     InputStream rawin;
/*     */     OutputStream rawout;
/*     */     String protocol;
/*     */     ExchangeImpl tx;
/*     */     HttpContextImpl ctx;
/* 491 */     boolean rejected = false;
/*     */     
/*     */     Exchange(SocketChannel paramSocketChannel, String paramString, HttpConnection paramHttpConnection) throws IOException {
/* 494 */       this.chan = paramSocketChannel;
/* 495 */       this.connection = paramHttpConnection;
/* 496 */       this.protocol = paramString;
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/* 501 */       this.context = this.connection.getHttpContext();
/*     */       
/* 503 */       SSLEngine localSSLEngine = null;
/* 504 */       String str1 = null;
/* 505 */       SSLStreams localSSLStreams = null;
/*     */       try { int i;
/* 507 */         if (this.context != null) {
/* 508 */           this.rawin = this.connection.getInputStream();
/* 509 */           this.rawout = this.connection.getRawOutputStream();
/* 510 */           i = 0;
/*     */         }
/*     */         else {
/* 513 */           i = 1;
/* 514 */           if (ServerImpl.this.https) {
/* 515 */             if (ServerImpl.this.sslContext == null) {
/* 516 */               ServerImpl.this.logger.warning("SSL connection received. No https contxt created");
/* 517 */               throw new HttpError("No SSL context established");
/*     */             }
/* 519 */             localSSLStreams = new SSLStreams(ServerImpl.this, ServerImpl.this.sslContext, this.chan);
/* 520 */             this.rawin = localSSLStreams.getInputStream();
/* 521 */             this.rawout = localSSLStreams.getOutputStream();
/* 522 */             localSSLEngine = localSSLStreams.getSSLEngine();
/* 523 */             this.connection.sslStreams = localSSLStreams;
/*     */           } else {
/* 525 */             this.rawin = new BufferedInputStream(new Request.ReadStream(ServerImpl.this, this.chan));
/*     */             
/*     */ 
/*     */ 
/* 529 */             this.rawout = new Request.WriteStream(ServerImpl.this, this.chan);
/*     */           }
/*     */           
/*     */ 
/* 533 */           this.connection.raw = this.rawin;
/* 534 */           this.connection.rawout = this.rawout;
/*     */         }
/* 536 */         Request localRequest = new Request(this.rawin, this.rawout);
/* 537 */         str1 = localRequest.requestLine();
/* 538 */         if (str1 == null)
/*     */         {
/* 540 */           ServerImpl.this.closeConnection(this.connection);
/* 541 */           return;
/*     */         }
/* 543 */         int j = str1.indexOf(' ');
/* 544 */         if (j == -1) {
/* 545 */           reject(400, str1, "Bad request line");
/*     */           
/* 547 */           return;
/*     */         }
/* 549 */         String str2 = str1.substring(0, j);
/* 550 */         int k = j + 1;
/* 551 */         j = str1.indexOf(' ', k);
/* 552 */         if (j == -1) {
/* 553 */           reject(400, str1, "Bad request line");
/*     */           
/* 555 */           return;
/*     */         }
/* 557 */         String str3 = str1.substring(k, j);
/* 558 */         URI localURI = new URI(str3);
/* 559 */         k = j + 1;
/* 560 */         String str4 = str1.substring(k);
/* 561 */         Headers localHeaders1 = localRequest.headers();
/* 562 */         String str5 = localHeaders1.getFirst("Transfer-encoding");
/* 563 */         long l = 0L;
/* 564 */         if ((str5 != null) && (str5.equalsIgnoreCase("chunked"))) {
/* 565 */           l = -1L;
/*     */         } else {
/* 567 */           str5 = localHeaders1.getFirst("Content-Length");
/* 568 */           if (str5 != null) {
/* 569 */             l = Long.parseLong(str5);
/*     */           }
/* 571 */           if (l == 0L) {
/* 572 */             ServerImpl.this.requestCompleted(this.connection);
/*     */           }
/*     */         }
/* 575 */         this.ctx = ServerImpl.this.contexts.findContext(this.protocol, localURI.getPath());
/* 576 */         if (this.ctx == null) {
/* 577 */           reject(404, str1, "No context found for request");
/*     */           
/* 579 */           return;
/*     */         }
/* 581 */         this.connection.setContext(this.ctx);
/* 582 */         if (this.ctx.getHandler() == null) {
/* 583 */           reject(500, str1, "No handler for context");
/*     */           
/* 585 */           return;
/*     */         }
/* 587 */         this.tx = new ExchangeImpl(str2, localURI, localRequest, l, this.connection);
/*     */         
/*     */ 
/* 590 */         String str6 = localHeaders1.getFirst("Connection");
/* 591 */         Headers localHeaders2 = this.tx.getResponseHeaders();
/*     */         
/* 593 */         if ((str6 != null) && (str6.equalsIgnoreCase("close"))) {
/* 594 */           this.tx.close = true;
/*     */         }
/* 596 */         if (str4.equalsIgnoreCase("http/1.0")) {
/* 597 */           this.tx.http10 = true;
/* 598 */           if (str6 == null) {
/* 599 */             this.tx.close = true;
/* 600 */             localHeaders2.set("Connection", "close");
/* 601 */           } else if (str6.equalsIgnoreCase("keep-alive")) {
/* 602 */             localHeaders2.set("Connection", "keep-alive");
/* 603 */             int m = (int)(ServerConfig.getIdleInterval() / 1000L);
/* 604 */             int n = ServerConfig.getMaxIdleConnections();
/* 605 */             localObject = "timeout=" + m + ", max=" + n;
/* 606 */             localHeaders2.set("Keep-Alive", (String)localObject);
/*     */           }
/*     */         }
/*     */         
/* 610 */         if (i != 0) {
/* 611 */           this.connection.setParameters(this.rawin, this.rawout, this.chan, localSSLEngine, localSSLStreams, 
/*     */           
/* 613 */             ServerImpl.this.sslContext, this.protocol, this.ctx, this.rawin);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 621 */         String str7 = localHeaders1.getFirst("Expect");
/* 622 */         if ((str7 != null) && (str7.equalsIgnoreCase("100-continue"))) {
/* 623 */           ServerImpl.this.logReply(100, str1, null);
/* 624 */           sendReply(100, false, null);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 635 */         List localList = this.ctx.getSystemFilters();
/* 636 */         Object localObject = this.ctx.getFilters();
/*     */         
/* 638 */         Filter.Chain localChain1 = new Filter.Chain(localList, this.ctx.getHandler());
/* 639 */         Filter.Chain localChain2 = new Filter.Chain((List)localObject, new LinkHandler(localChain1));
/*     */         
/*     */ 
/* 642 */         this.tx.getRequestBody();
/* 643 */         this.tx.getResponseBody();
/* 644 */         if (ServerImpl.this.https) {
/* 645 */           localChain2.doFilter(new HttpsExchangeImpl(this.tx));
/*     */         } else {
/* 647 */           localChain2.doFilter(new HttpExchangeImpl(this.tx));
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException) {
/* 651 */         ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (1)", localIOException);
/* 652 */         ServerImpl.this.closeConnection(this.connection);
/*     */       } catch (NumberFormatException localNumberFormatException) {
/* 654 */         reject(400, str1, "NumberFormatException thrown");
/*     */       }
/*     */       catch (URISyntaxException localURISyntaxException) {
/* 657 */         reject(400, str1, "URISyntaxException thrown");
/*     */       }
/*     */       catch (Exception localException) {
/* 660 */         ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (2)", localException);
/* 661 */         ServerImpl.this.closeConnection(this.connection);
/*     */       }
/*     */     }
/*     */     
/*     */     class LinkHandler implements HttpHandler
/*     */     {
/*     */       Filter.Chain nextChain;
/*     */       
/*     */       LinkHandler(Filter.Chain paramChain)
/*     */       {
/* 671 */         this.nextChain = paramChain;
/*     */       }
/*     */       
/*     */       public void handle(HttpExchange paramHttpExchange) throws IOException {
/* 675 */         this.nextChain.doFilter(paramHttpExchange);
/*     */       }
/*     */     }
/*     */     
/*     */     void reject(int paramInt, String paramString1, String paramString2) {
/* 680 */       this.rejected = true;
/* 681 */       ServerImpl.this.logReply(paramInt, paramString1, paramString2);
/* 682 */       sendReply(paramInt, false, "<h1>" + paramInt + 
/* 683 */         Code.msg(paramInt) + "</h1>" + paramString2);
/*     */       
/* 685 */       ServerImpl.this.closeConnection(this.connection);
/*     */     }
/*     */     
/*     */     void sendReply(int paramInt, boolean paramBoolean, String paramString)
/*     */     {
/*     */       try
/*     */       {
/* 692 */         StringBuilder localStringBuilder = new StringBuilder(512);
/* 693 */         localStringBuilder.append("HTTP/1.1 ")
/* 694 */           .append(paramInt).append(Code.msg(paramInt)).append("\r\n");
/*     */         
/* 696 */         if ((paramString != null) && (paramString.length() != 0))
/*     */         {
/*     */ 
/* 699 */           localStringBuilder.append("Content-Length: ").append(paramString.length()).append("\r\n").append("Content-Type: text/html\r\n");
/*     */         } else {
/* 701 */           localStringBuilder.append("Content-Length: 0\r\n");
/* 702 */           paramString = "";
/*     */         }
/* 704 */         if (paramBoolean) {
/* 705 */           localStringBuilder.append("Connection: close\r\n");
/*     */         }
/* 707 */         localStringBuilder.append("\r\n").append(paramString);
/* 708 */         String str = localStringBuilder.toString();
/* 709 */         byte[] arrayOfByte = str.getBytes("ISO8859_1");
/* 710 */         this.rawout.write(arrayOfByte);
/* 711 */         this.rawout.flush();
/* 712 */         if (paramBoolean) {
/* 713 */           ServerImpl.this.closeConnection(this.connection);
/*     */         }
/*     */       } catch (IOException localIOException) {
/* 716 */         ServerImpl.this.logger.log(Level.FINER, "ServerImpl.sendReply", localIOException);
/* 717 */         ServerImpl.this.closeConnection(this.connection);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   void logReply(int paramInt, String paramString1, String paramString2)
/*     */   {
/* 724 */     if (!this.logger.isLoggable(Level.FINE)) {
/* 725 */       return;
/*     */     }
/* 727 */     if (paramString2 == null) {
/* 728 */       paramString2 = "";
/*     */     }
/*     */     String str1;
/* 731 */     if (paramString1.length() > 80) {
/* 732 */       str1 = paramString1.substring(0, 80) + "<TRUNCATED>";
/*     */     } else {
/* 734 */       str1 = paramString1;
/*     */     }
/*     */     
/* 737 */     String str2 = str1 + " [" + paramInt + " " + Code.msg(paramInt) + "] (" + paramString2 + ")";
/* 738 */     this.logger.fine(str2);
/*     */   }
/*     */   
/*     */   long getTicks() {
/* 742 */     return this.ticks;
/*     */   }
/*     */   
/*     */   public long getTime() {
/* 746 */     return this.time;
/*     */   }
/*     */   
/*     */   void delay() {
/*     */     
/*     */     try {
/* 752 */       Thread.sleep(200L);
/*     */     } catch (InterruptedException localInterruptedException) {}
/*     */   }
/*     */   
/* 756 */   private int exchangeCount = 0;
/*     */   
/*     */   synchronized void startExchange() {
/* 759 */     this.exchangeCount += 1;
/*     */   }
/*     */   
/*     */   synchronized int endExchange() {
/* 763 */     this.exchangeCount -= 1;
/* 764 */     assert (this.exchangeCount >= 0);
/* 765 */     return this.exchangeCount;
/*     */   }
/*     */   
/*     */   HttpServer getWrapper() {
/* 769 */     return this.wrapper;
/*     */   }
/*     */   
/*     */   void requestStarted(HttpConnection paramHttpConnection) {
/* 773 */     paramHttpConnection.creationTime = getTime();
/* 774 */     paramHttpConnection.setState(HttpConnection.State.REQUEST);
/* 775 */     this.reqConnections.add(paramHttpConnection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void requestCompleted(HttpConnection paramHttpConnection)
/*     */   {
/* 786 */     assert (paramHttpConnection.getState() == HttpConnection.State.REQUEST);
/* 787 */     this.reqConnections.remove(paramHttpConnection);
/* 788 */     paramHttpConnection.rspStartedTime = getTime();
/* 789 */     this.rspConnections.add(paramHttpConnection);
/* 790 */     paramHttpConnection.setState(HttpConnection.State.RESPONSE);
/*     */   }
/*     */   
/*     */   void responseCompleted(HttpConnection paramHttpConnection)
/*     */   {
/* 795 */     assert (paramHttpConnection.getState() == HttpConnection.State.RESPONSE);
/* 796 */     this.rspConnections.remove(paramHttpConnection);
/* 797 */     paramHttpConnection.setState(HttpConnection.State.IDLE);
/*     */   }
/*     */   
/*     */   class ServerTimerTask extends TimerTask
/*     */   {
/*     */     ServerTimerTask() {}
/*     */     
/*     */     public void run() {
/* 805 */       LinkedList localLinkedList = new LinkedList();
/* 806 */       ServerImpl.this.time = System.currentTimeMillis();
/* 807 */       ServerImpl.access$1808(ServerImpl.this);
/* 808 */       Iterator localIterator; HttpConnection localHttpConnection; synchronized (ServerImpl.this.idleConnections) {
/* 809 */         for (localIterator = ServerImpl.this.idleConnections.iterator(); localIterator.hasNext();) { localHttpConnection = (HttpConnection)localIterator.next();
/* 810 */           if (localHttpConnection.time <= ServerImpl.this.time) {
/* 811 */             localLinkedList.add(localHttpConnection);
/*     */           }
/*     */         }
/* 814 */         for (localIterator = localLinkedList.iterator(); localIterator.hasNext();) { localHttpConnection = (HttpConnection)localIterator.next();
/* 815 */           ServerImpl.this.idleConnections.remove(localHttpConnection);
/* 816 */           ServerImpl.this.allConnections.remove(localHttpConnection);
/* 817 */           localHttpConnection.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   class ServerTimerTask1 extends TimerTask {
/*     */     ServerTimerTask1() {}
/*     */     
/*     */     public void run() {
/* 827 */       LinkedList localLinkedList = new LinkedList();
/* 828 */       ServerImpl.this.time = System.currentTimeMillis();
/* 829 */       Iterator localIterator; HttpConnection localHttpConnection; synchronized (ServerImpl.this.reqConnections) {
/* 830 */         if (ServerImpl.MAX_REQ_TIME != -1L) {
/* 831 */           for (localIterator = ServerImpl.this.reqConnections.iterator(); localIterator.hasNext();) { localHttpConnection = (HttpConnection)localIterator.next();
/* 832 */             if (localHttpConnection.creationTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_REQ_TIME <= ServerImpl.this.time) {
/* 833 */               localLinkedList.add(localHttpConnection);
/*     */             }
/*     */           }
/* 836 */           for (localIterator = localLinkedList.iterator(); localIterator.hasNext();) { localHttpConnection = (HttpConnection)localIterator.next();
/* 837 */             ServerImpl.this.logger.log(Level.FINE, "closing: no request: " + localHttpConnection);
/* 838 */             ServerImpl.this.reqConnections.remove(localHttpConnection);
/* 839 */             ServerImpl.this.allConnections.remove(localHttpConnection);
/* 840 */             localHttpConnection.close();
/*     */           }
/*     */         }
/*     */       }
/* 844 */       localLinkedList = new LinkedList();
/* 845 */       synchronized (ServerImpl.this.rspConnections) {
/* 846 */         if (ServerImpl.MAX_RSP_TIME != -1L) {
/* 847 */           for (localIterator = ServerImpl.this.rspConnections.iterator(); localIterator.hasNext();) { localHttpConnection = (HttpConnection)localIterator.next();
/* 848 */             if (localHttpConnection.rspStartedTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_RSP_TIME <= ServerImpl.this.time) {
/* 849 */               localLinkedList.add(localHttpConnection);
/*     */             }
/*     */           }
/* 852 */           for (localIterator = localLinkedList.iterator(); localIterator.hasNext();) { localHttpConnection = (HttpConnection)localIterator.next();
/* 853 */             ServerImpl.this.logger.log(Level.FINE, "closing: no response: " + localHttpConnection);
/* 854 */             ServerImpl.this.rspConnections.remove(localHttpConnection);
/* 855 */             ServerImpl.this.allConnections.remove(localHttpConnection);
/* 856 */             localHttpConnection.close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   void logStackTrace(String paramString) {
/* 864 */     this.logger.finest(paramString);
/* 865 */     StringBuilder localStringBuilder = new StringBuilder();
/* 866 */     StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
/* 867 */     for (int i = 0; i < arrayOfStackTraceElement.length; i++) {
/* 868 */       localStringBuilder.append(arrayOfStackTraceElement[i].toString()).append("\n");
/*     */     }
/* 870 */     this.logger.finest(localStringBuilder.toString());
/*     */   }
/*     */   
/*     */   static long getTimeMillis(long paramLong) {
/* 874 */     if (paramLong == -1L) {
/* 875 */       return -1L;
/*     */     }
/* 877 */     return paramLong * 1000L;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\httpserver\ServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
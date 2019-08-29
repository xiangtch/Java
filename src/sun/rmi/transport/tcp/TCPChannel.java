/*     */ package sun.rmi.transport.tcp;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.net.Socket;
/*     */ import java.rmi.ConnectIOException;
/*     */ import java.rmi.RemoteException;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.runtime.RuntimeUtil;
/*     */ import sun.rmi.runtime.RuntimeUtil.GetInstanceAction;
/*     */ import sun.rmi.transport.Channel;
/*     */ import sun.rmi.transport.Connection;
/*     */ import sun.rmi.transport.Endpoint;
/*     */ import sun.security.action.GetIntegerAction;
/*     */ import sun.security.action.GetLongAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TCPChannel
/*     */   implements Channel
/*     */ {
/*     */   private final TCPEndpoint ep;
/*     */   private final TCPTransport tr;
/*  66 */   private final List<TCPConnection> freeList = new ArrayList();
/*     */   
/*     */ 
/*  69 */   private Future<?> reaper = null;
/*     */   
/*     */ 
/*  72 */   private boolean usingMultiplexer = false;
/*     */   
/*  74 */   private ConnectionMultiplexer multiplexer = null;
/*     */   
/*     */ 
/*     */   private ConnectionAcceptor acceptor;
/*     */   
/*     */ 
/*     */   private AccessControlContext okContext;
/*     */   
/*     */ 
/*     */   private WeakHashMap<AccessControlContext, Reference<AccessControlContext>> authcache;
/*     */   
/*     */ 
/*  86 */   private SecurityManager cacheSecurityManager = null;
/*     */   
/*     */ 
/*     */ 
/*  90 */   private static final long idleTimeout = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.connectionTimeout", 15000L))).longValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  95 */   private static final int handshakeTimeout = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.handshakeTimeout", 60000))).intValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */   private static final int responseTimeout = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.responseTimeout", 0))).intValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 106 */   private static final ScheduledExecutorService scheduler = ((RuntimeUtil)AccessController.doPrivileged(new GetInstanceAction()))
/* 107 */     .getScheduler();
/*     */   
/*     */ 
/*     */ 
/*     */   TCPChannel(TCPTransport paramTCPTransport, TCPEndpoint paramTCPEndpoint)
/*     */   {
/* 113 */     this.tr = paramTCPTransport;
/* 114 */     this.ep = paramTCPEndpoint;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Endpoint getEndpoint()
/*     */   {
/* 121 */     return this.ep;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkConnectPermission()
/*     */     throws SecurityException
/*     */   {
/* 131 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 132 */     if (localSecurityManager == null) {
/* 133 */       return;
/*     */     }
/* 135 */     if (localSecurityManager != this.cacheSecurityManager)
/*     */     {
/* 137 */       this.okContext = null;
/* 138 */       this.authcache = new WeakHashMap();
/*     */       
/* 140 */       this.cacheSecurityManager = localSecurityManager;
/*     */     }
/*     */     
/* 143 */     AccessControlContext localAccessControlContext = AccessController.getContext();
/*     */     
/*     */ 
/*     */ 
/* 147 */     if ((this.okContext == null) || (
/* 148 */       (!this.okContext.equals(localAccessControlContext)) && (!this.authcache.containsKey(localAccessControlContext))))
/*     */     {
/* 150 */       localSecurityManager.checkConnect(this.ep.getHost(), this.ep.getPort());
/* 151 */       this.authcache.put(localAccessControlContext, new SoftReference(localAccessControlContext));
/*     */     }
/*     */     
/*     */ 
/* 155 */     this.okContext = localAccessControlContext;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Connection newConnection()
/*     */     throws RemoteException
/*     */   {
/*     */     TCPConnection localTCPConnection;
/*     */     
/*     */ 
/*     */ 
/*     */     do
/*     */     {
/* 170 */       localTCPConnection = null;
/*     */       
/* 172 */       synchronized (this.freeList) {
/* 173 */         int i = this.freeList.size() - 1;
/*     */         
/* 175 */         if (i >= 0)
/*     */         {
/*     */ 
/*     */ 
/* 179 */           checkConnectPermission();
/* 180 */           localTCPConnection = (TCPConnection)this.freeList.get(i);
/* 181 */           this.freeList.remove(i);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 189 */       if (localTCPConnection != null)
/*     */       {
/* 191 */         if (!localTCPConnection.isDead()) {
/* 192 */           TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
/* 193 */           return localTCPConnection;
/*     */         }
/*     */         
/*     */ 
/* 197 */         free(localTCPConnection, false);
/*     */       }
/* 199 */     } while (localTCPConnection != null);
/*     */     
/*     */ 
/* 202 */     return createConnection();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Connection createConnection()
/*     */     throws RemoteException
/*     */   {
/* 213 */     TCPTransport.tcpLog.log(Log.BRIEF, "create connection");
/*     */     TCPConnection localTCPConnection;
/* 215 */     if (!this.usingMultiplexer) {
/* 216 */       Socket localSocket = this.ep.newSocket();
/* 217 */       localTCPConnection = new TCPConnection(this, localSocket);
/*     */       
/*     */       try
/*     */       {
/* 221 */         DataOutputStream localDataOutputStream = new DataOutputStream(localTCPConnection.getOutputStream());
/* 222 */         writeTransportHeader(localDataOutputStream);
/*     */         
/*     */ 
/* 225 */         if (!localTCPConnection.isReusable()) {
/* 226 */           localDataOutputStream.writeByte(76);
/*     */         } else {
/* 228 */           localDataOutputStream.writeByte(75);
/* 229 */           localDataOutputStream.flush();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */           int i = 0;
/*     */           try {
/* 238 */             i = localSocket.getSoTimeout();
/* 239 */             localSocket.setSoTimeout(handshakeTimeout);
/*     */           }
/*     */           catch (Exception localException2) {}
/*     */           
/*     */ 
/*     */ 
/* 245 */           DataInputStream localDataInputStream = new DataInputStream(localTCPConnection.getInputStream());
/* 246 */           int j = localDataInputStream.readByte();
/* 247 */           if (j != 78) {
/* 248 */             throw new ConnectIOException(j == 79 ? "JRMP StreamProtocol not supported by server" : "non-JRMP server at remote endpoint");
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 254 */           String str = localDataInputStream.readUTF();
/* 255 */           int k = localDataInputStream.readInt();
/* 256 */           if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
/* 257 */             TCPTransport.tcpLog.log(Log.VERBOSE, "server suggested " + str + ":" + k);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 263 */           TCPEndpoint.setLocalHost(str);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 270 */           TCPEndpoint localTCPEndpoint = TCPEndpoint.getLocalEndpoint(0, null, null);
/* 271 */           localDataOutputStream.writeUTF(localTCPEndpoint.getHost());
/* 272 */           localDataOutputStream.writeInt(localTCPEndpoint.getPort());
/* 273 */           if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
/* 274 */             TCPTransport.tcpLog.log(Log.VERBOSE, "using " + localTCPEndpoint
/* 275 */               .getHost() + ":" + localTCPEndpoint.getPort());
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 291 */             localSocket.setSoTimeout(i != 0 ? i : responseTimeout);
/*     */           }
/*     */           catch (Exception localException3) {}
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 298 */           localDataOutputStream.flush();
/*     */         }
/*     */       } catch (IOException localIOException2) {
/*     */         try {
/* 302 */           localTCPConnection.close();
/*     */         } catch (Exception localException1) {}
/* 304 */         if ((localIOException2 instanceof RemoteException)) {
/* 305 */           throw ((RemoteException)localIOException2);
/*     */         }
/* 307 */         throw new ConnectIOException("error during JRMP connection establishment", localIOException2);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       try {
/* 313 */         localTCPConnection = this.multiplexer.openConnection();
/*     */       } catch (IOException localIOException1) {
/* 315 */         synchronized (this) {
/* 316 */           this.usingMultiplexer = false;
/* 317 */           this.multiplexer = null;
/*     */         }
/* 319 */         throw new ConnectIOException("error opening virtual connection over multiplexed connection", localIOException1);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 324 */     return localTCPConnection;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void free(Connection paramConnection, boolean paramBoolean)
/*     */   {
/* 334 */     if (paramConnection == null) { return;
/*     */     }
/* 336 */     if ((paramBoolean) && (paramConnection.isReusable())) {
/* 337 */       long l = System.currentTimeMillis();
/* 338 */       TCPConnection localTCPConnection = (TCPConnection)paramConnection;
/*     */       
/* 340 */       TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 346 */       synchronized (this.freeList) {
/* 347 */         this.freeList.add(localTCPConnection);
/* 348 */         if (this.reaper == null) {
/* 349 */           TCPTransport.tcpLog.log(Log.BRIEF, "create reaper");
/*     */           
/* 351 */           this.reaper = scheduler.scheduleWithFixedDelay(new Runnable()
/*     */           {
/*     */             public void run() {
/* 354 */               TCPTransport.tcpLog.log(Log.VERBOSE, "wake up");
/*     */               
/* 356 */               TCPChannel.this.freeCachedConnections(); } }, idleTimeout, idleTimeout, TimeUnit.MILLISECONDS);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 362 */       localTCPConnection.setLastUseTime(l);
/* 363 */       localTCPConnection.setExpiration(l + idleTimeout);
/*     */     } else {
/* 365 */       TCPTransport.tcpLog.log(Log.BRIEF, "close connection");
/*     */       try
/*     */       {
/* 368 */         paramConnection.close();
/*     */       }
/*     */       catch (IOException localIOException) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void writeTransportHeader(DataOutputStream paramDataOutputStream)
/*     */     throws RemoteException
/*     */   {
/*     */     try
/*     */     {
/* 382 */       DataOutputStream localDataOutputStream = new DataOutputStream(paramDataOutputStream);
/*     */       
/* 384 */       localDataOutputStream.writeInt(1246907721);
/* 385 */       localDataOutputStream.writeShort(2);
/*     */     } catch (IOException localIOException) {
/* 387 */       throw new ConnectIOException("error writing JRMP transport header", localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void useMultiplexer(ConnectionMultiplexer paramConnectionMultiplexer)
/*     */   {
/* 398 */     this.multiplexer = paramConnectionMultiplexer;
/*     */     
/* 400 */     this.usingMultiplexer = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void acceptMultiplexConnection(Connection paramConnection)
/*     */   {
/* 407 */     if (this.acceptor == null) {
/* 408 */       this.acceptor = new ConnectionAcceptor(this.tr);
/* 409 */       this.acceptor.startNewAcceptor();
/*     */     }
/* 411 */     this.acceptor.accept(paramConnection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void shedCache()
/*     */   {
/*     */     Connection[] arrayOfConnection;
/*     */     
/*     */ 
/* 421 */     synchronized (this.freeList) {
/* 422 */       arrayOfConnection = (Connection[])this.freeList.toArray(new Connection[this.freeList.size()]);
/* 423 */       this.freeList.clear();
/*     */     }
/*     */     
/*     */ 
/* 427 */     int i = arrayOfConnection.length; for (;;) { i--; if (i < 0) break;
/* 428 */       Connection localConnection = arrayOfConnection[i];
/* 429 */       arrayOfConnection[i] = null;
/*     */       try {
/* 431 */         localConnection.close();
/*     */       }
/*     */       catch (IOException localIOException) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void freeCachedConnections()
/*     */   {
/* 442 */     synchronized (this.freeList) {
/* 443 */       int i = this.freeList.size();
/*     */       
/* 445 */       if (i > 0) {
/* 446 */         long l = System.currentTimeMillis();
/* 447 */         ListIterator localListIterator = this.freeList.listIterator(i);
/*     */         
/* 449 */         while (localListIterator.hasPrevious()) {
/* 450 */           TCPConnection localTCPConnection = (TCPConnection)localListIterator.previous();
/* 451 */           if (localTCPConnection.expired(l)) {
/* 452 */             TCPTransport.tcpLog.log(Log.VERBOSE, "connection timeout expired");
/*     */             
/*     */             try
/*     */             {
/* 456 */               localTCPConnection.close();
/*     */             }
/*     */             catch (IOException localIOException) {}
/*     */             
/* 460 */             localListIterator.remove();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 465 */       if (this.freeList.isEmpty()) {
/* 466 */         this.reaper.cancel(false);
/* 467 */         this.reaper = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\tcp\TCPChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
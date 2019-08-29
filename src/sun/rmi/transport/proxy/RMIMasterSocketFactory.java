/*     */ package sun.rmi.transport.proxy;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.NoRouteToHostException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.rmi.server.LogStream;
/*     */ import java.rmi.server.RMISocketFactory;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.runtime.NewThreadAction;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.action.GetLongAction;
/*     */ import sun.security.action.GetPropertyAction;
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
/*     */ public class RMIMasterSocketFactory
/*     */   extends RMISocketFactory
/*     */ {
/*  52 */   static int logLevel = LogStream.parseLevel(getLogLevel());
/*     */   
/*     */   private static String getLogLevel() {
/*  55 */     return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.proxy.logLevel"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  61 */   static final Log proxyLog = Log.getLog("sun.rmi.transport.tcp.proxy", "transport", logLevel);
/*     */   
/*     */ 
/*     */ 
/*  65 */   private static long connectTimeout = getConnectTimeout();
/*     */   
/*     */   private static long getConnectTimeout() {
/*  68 */     return 
/*     */     
/*  70 */       ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.proxy.connectTimeout", 15000L))).longValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  75 */   private static final boolean eagerHttpFallback = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.transport.proxy.eagerHttpFallback")))
/*  76 */     .booleanValue();
/*     */   
/*     */ 
/*  79 */   private Hashtable<String, RMISocketFactory> successTable = new Hashtable();
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int MaxRememberedHosts = 64;
/*     */   
/*     */ 
/*  86 */   private Vector<String> hostList = new Vector(64);
/*     */   
/*     */ 
/*  89 */   protected RMISocketFactory initialFactory = new RMIDirectSocketFactory();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Vector<RMISocketFactory> altFactoryList;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RMIMasterSocketFactory()
/*     */   {
/* 101 */     this.altFactoryList = new Vector(2);
/* 102 */     int i = 0;
/*     */     
/*     */     try
/*     */     {
/* 106 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("http.proxyHost"));
/*     */       
/*     */ 
/* 109 */       if (str == null) {
/* 110 */         str = (String)AccessController.doPrivileged(new GetPropertyAction("proxyHost"));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 115 */       boolean bool = ((String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.disableHttp", "true"))).equalsIgnoreCase("true");
/*     */       
/* 117 */       if ((!bool) && (str != null) && (str.length() > 0)) {
/* 118 */         i = 1;
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {}
/*     */     
/*     */ 
/* 124 */     if (i != 0) {
/* 125 */       this.altFactoryList.addElement(new RMIHttpToPortSocketFactory());
/* 126 */       this.altFactoryList.addElement(new RMIHttpToCGISocketFactory());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Socket createSocket(String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 139 */     if (proxyLog.isLoggable(Log.BRIEF)) {
/* 140 */       proxyLog.log(Log.BRIEF, "host: " + paramString + ", port: " + paramInt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 147 */     if (this.altFactoryList.size() == 0) {
/* 148 */       return this.initialFactory.createSocket(paramString, paramInt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 157 */     RMISocketFactory localRMISocketFactory = (RMISocketFactory)this.successTable.get(paramString);
/* 158 */     if (localRMISocketFactory != null) {
/* 159 */       if (proxyLog.isLoggable(Log.BRIEF)) {
/* 160 */         proxyLog.log(Log.BRIEF, "previously successful factory found: " + localRMISocketFactory);
/*     */       }
/*     */       
/* 163 */       return localRMISocketFactory.createSocket(paramString, paramInt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */     Socket localSocket1 = null;
/* 172 */     Socket localSocket2 = null;
/*     */     
/*     */ 
/* 175 */     AsyncConnector localAsyncConnector = new AsyncConnector(this.initialFactory, paramString, paramInt, AccessController.getContext());
/*     */     
/*     */ 
/* 178 */     Object localObject1 = null;
/*     */     try
/*     */     {
/* 181 */       synchronized (localAsyncConnector)
/*     */       {
/* 183 */         Thread localThread = (Thread)AccessController.doPrivileged(new NewThreadAction(localAsyncConnector, "AsyncConnector", true));
/*     */         
/* 185 */         localThread.start();
/*     */         try
/*     */         {
/* 188 */           long l1 = System.currentTimeMillis();
/* 189 */           long l2 = l1 + connectTimeout;
/*     */           do {
/* 191 */             localAsyncConnector.wait(l2 - l1);
/* 192 */             localSocket1 = checkConnector(localAsyncConnector);
/* 193 */             if (localSocket1 != null)
/*     */               break;
/* 195 */             l1 = System.currentTimeMillis();
/* 196 */           } while (l1 < l2);
/*     */         } catch (InterruptedException localInterruptedException) {
/* 198 */           throw new InterruptedIOException("interrupted while waiting for connector");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 204 */       if (localSocket1 == null) {
/* 205 */         throw new NoRouteToHostException("connect timed out: " + paramString);
/*     */       }
/*     */       
/* 208 */       proxyLog.log(Log.BRIEF, "direct socket connection successful");
/*     */       int i;
/* 210 */       Socket localSocket5; InputStream localInputStream2; int k; return localSocket1;
/*     */     } catch (UnknownHostException|NoRouteToHostException ???) {
/*     */       Object localObject3;
/* 213 */       localObject1 = ???;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 221 */       if (localObject1 != null)
/*     */       {
/* 223 */         if (proxyLog.isLoggable(Log.BRIEF)) {
/* 224 */           proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 229 */         for (??? = 0; ??? < this.altFactoryList.size(); ???++) {
/* 230 */           localRMISocketFactory = (RMISocketFactory)this.altFactoryList.elementAt(???);
/* 231 */           if (proxyLog.isLoggable(Log.BRIEF)) {
/* 232 */             proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
/*     */           }
/*     */           try
/*     */           {
/* 236 */             Socket localSocket3 = localRMISocketFactory.createSocket(paramString, paramInt);localObject2 = null;
/*     */             
/*     */ 
/*     */ 
/*     */             try
/*     */             {
/* 242 */               localObject3 = localSocket3.getInputStream();
/* 243 */               j = ((InputStream)localObject3).read();
/*     */             }
/*     */             catch (Throwable localThrowable2)
/*     */             {
/* 235 */               localObject2 = localThrowable2; throw localThrowable2;
/*     */ 
/*     */ 
/*     */ 
/*     */             }
/*     */             finally
/*     */             {
/*     */ 
/*     */ 
/* 244 */               if (localSocket3 != null) if (localObject2 != null) try { localSocket3.close(); } catch (Throwable localThrowable8) { ((Throwable)localObject2).addSuppressed(localThrowable8); } else localSocket3.close();
/* 245 */             } } catch (IOException localIOException1) { if (proxyLog.isLoggable(Log.BRIEF)) {
/* 246 */               proxyLog.log(Log.BRIEF, "factory failed: ", localIOException1);
/*     */             }
/*     */             
/* 249 */             continue;
/*     */           }
/* 251 */           proxyLog.log(Log.BRIEF, "factory succeeded");
/*     */           
/*     */           try
/*     */           {
/* 255 */             localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
/*     */           }
/*     */           catch (IOException localIOException2) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (SocketException ???)
/*     */     {
/*     */       Object localObject2;
/*     */       int j;
/* 215 */       if (eagerHttpFallback) {
/* 216 */         localObject1 = ???;
/*     */       } else {
/* 218 */         throw ((Throwable)???);
/*     */       }
/*     */       
/* 221 */       if (localObject1 != null)
/*     */       {
/* 223 */         if (proxyLog.isLoggable(Log.BRIEF)) {
/* 224 */           proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 229 */         for (??? = 0; ??? < this.altFactoryList.size(); ???++) {
/* 230 */           localRMISocketFactory = (RMISocketFactory)this.altFactoryList.elementAt(???);
/* 231 */           if (proxyLog.isLoggable(Log.BRIEF)) {
/* 232 */             proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
/*     */           }
/*     */           try
/*     */           {
/* 236 */             Socket localSocket4 = localRMISocketFactory.createSocket(paramString, paramInt);localObject2 = null;
/*     */             
/*     */ 
/*     */ 
/*     */             try
/*     */             {
/* 242 */               InputStream localInputStream1 = localSocket4.getInputStream();
/* 243 */               j = localInputStream1.read();
/*     */             }
/*     */             catch (Throwable localThrowable4)
/*     */             {
/* 235 */               localObject2 = localThrowable4; throw localThrowable4;
/*     */ 
/*     */ 
/*     */ 
/*     */             }
/*     */             finally
/*     */             {
/*     */ 
/*     */ 
/* 244 */               if (localSocket4 != null) if (localObject2 != null) try { localSocket4.close(); } catch (Throwable localThrowable9) { ((Throwable)localObject2).addSuppressed(localThrowable9); } else localSocket4.close();
/* 245 */             } } catch (IOException localIOException3) { if (proxyLog.isLoggable(Log.BRIEF)) {
/* 246 */               proxyLog.log(Log.BRIEF, "factory failed: ", localIOException3);
/*     */             }
/*     */             
/* 249 */             continue;
/*     */           }
/* 251 */           proxyLog.log(Log.BRIEF, "factory succeeded");
/*     */           
/*     */           try
/*     */           {
/* 255 */             localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
/*     */           }
/*     */           catch (IOException localIOException4) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 221 */       if (localObject1 != null)
/*     */       {
/* 223 */         if (proxyLog.isLoggable(Log.BRIEF)) {
/* 224 */           proxyLog.log(Log.BRIEF, "direct socket connection failed: ", (Throwable)localObject1);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 229 */         for (int m = 0; m < this.altFactoryList.size(); m++) {
/* 230 */           localRMISocketFactory = (RMISocketFactory)this.altFactoryList.elementAt(m);
/* 231 */           if (proxyLog.isLoggable(Log.BRIEF)) {
/* 232 */             proxyLog.log(Log.BRIEF, "trying with factory: " + localRMISocketFactory);
/*     */           }
/*     */           try
/*     */           {
/* 236 */             Socket localSocket6 = localRMISocketFactory.createSocket(paramString, paramInt);Object localObject9 = null;
/*     */             
/*     */ 
/*     */ 
/*     */             try
/*     */             {
/* 242 */               InputStream localInputStream3 = localSocket6.getInputStream();
/* 243 */               int n = localInputStream3.read();
/*     */             }
/*     */             catch (Throwable localThrowable11)
/*     */             {
/* 235 */               localObject9 = localThrowable11;throw localThrowable11;
/*     */ 
/*     */ 
/*     */ 
/*     */             }
/*     */             finally
/*     */             {
/*     */ 
/*     */ 
/* 244 */               if (localSocket6 != null) if (localObject9 != null) try { localSocket6.close(); } catch (Throwable localThrowable12) { ((Throwable)localObject9).addSuppressed(localThrowable12); } else localSocket6.close();
/* 245 */             } } catch (IOException localIOException7) { if (proxyLog.isLoggable(Log.BRIEF)) {
/* 246 */               proxyLog.log(Log.BRIEF, "factory failed: ", localIOException7);
/*     */             }
/*     */             
/* 249 */             continue;
/*     */           }
/* 251 */           proxyLog.log(Log.BRIEF, "factory succeeded");
/*     */           
/*     */           try
/*     */           {
/* 255 */             localSocket2 = localRMISocketFactory.createSocket(paramString, paramInt);
/*     */           }
/*     */           catch (IOException localIOException8) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 263 */     synchronized (this.successTable)
/*     */     {
/*     */       try {
/* 266 */         synchronized (localAsyncConnector) {
/* 267 */           localSocket1 = checkConnector(localAsyncConnector);
/*     */         }
/* 269 */         if (localSocket1 != null)
/*     */         {
/* 271 */           if (localSocket2 != null)
/* 272 */             localSocket2.close();
/* 273 */           return localSocket1;
/*     */         }
/*     */         
/* 276 */         localAsyncConnector.notUsed();
/*     */       } catch (UnknownHostException|NoRouteToHostException localUnknownHostException) {
/* 278 */         localObject1 = localUnknownHostException;
/*     */       } catch (SocketException localSocketException) {
/* 280 */         if (eagerHttpFallback) {
/* 281 */           localObject1 = localSocketException;
/*     */         } else {
/* 283 */           throw localSocketException;
/*     */         }
/*     */       }
/*     */       
/* 287 */       if (localSocket2 != null)
/*     */       {
/* 289 */         rememberFactory(paramString, localRMISocketFactory);
/* 290 */         return localSocket2;
/*     */       }
/* 292 */       throw ((Throwable)localObject1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void rememberFactory(String paramString, RMISocketFactory paramRMISocketFactory)
/*     */   {
/* 302 */     synchronized (this.successTable) {
/* 303 */       while (this.hostList.size() >= 64) {
/* 304 */         this.successTable.remove(this.hostList.elementAt(0));
/* 305 */         this.hostList.removeElementAt(0);
/*     */       }
/* 307 */       this.hostList.addElement(paramString);
/* 308 */       this.successTable.put(paramString, paramRMISocketFactory);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Socket checkConnector(AsyncConnector paramAsyncConnector)
/*     */     throws IOException
/*     */   {
/* 319 */     Exception localException = paramAsyncConnector.getException();
/* 320 */     if (localException != null) {
/* 321 */       localException.fillInStackTrace();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 328 */       if ((localException instanceof IOException))
/* 329 */         throw ((IOException)localException);
/* 330 */       if ((localException instanceof RuntimeException)) {
/* 331 */         throw ((RuntimeException)localException);
/*     */       }
/*     */       
/* 334 */       throw new Error("internal error: unexpected checked exception: " + localException.toString());
/*     */     }
/*     */     
/* 337 */     return paramAsyncConnector.getSocket();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ServerSocket createServerSocket(int paramInt)
/*     */     throws IOException
/*     */   {
/* 345 */     return this.initialFactory.createServerSocket(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private class AsyncConnector
/*     */     implements Runnable
/*     */   {
/*     */     private RMISocketFactory factory;
/*     */     
/*     */ 
/*     */ 
/*     */     private String host;
/*     */     
/*     */ 
/*     */ 
/*     */     private int port;
/*     */     
/*     */ 
/*     */ 
/*     */     private AccessControlContext acc;
/*     */     
/*     */ 
/* 369 */     private Exception exception = null;
/*     */     
/*     */ 
/* 372 */     private Socket socket = null;
/*     */     
/*     */ 
/* 375 */     private boolean cleanUp = false;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     AsyncConnector(RMISocketFactory paramRMISocketFactory, String paramString, int paramInt, AccessControlContext paramAccessControlContext)
/*     */     {
/* 383 */       this.factory = paramRMISocketFactory;
/* 384 */       this.host = paramString;
/* 385 */       this.port = paramInt;
/* 386 */       this.acc = paramAccessControlContext;
/* 387 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 388 */       if (localSecurityManager != null) {
/* 389 */         localSecurityManager.checkConnect(paramString, paramInt);
/*     */       }
/*     */     }
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
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/* 409 */         Socket localSocket = this.factory.createSocket(this.host, this.port);
/* 410 */         synchronized (this) {
/* 411 */           this.socket = localSocket;
/* 412 */           notify();
/*     */         }
/* 414 */         RMIMasterSocketFactory.this.rememberFactory(this.host, this.factory);
/* 415 */         synchronized (this) {
/* 416 */           if (this.cleanUp) {
/*     */             try {
/* 418 */               this.socket.close();
/*     */ 
/*     */             }
/*     */             catch (IOException localIOException) {}
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 427 */         synchronized (this) {
/* 428 */           this.exception = localException;
/* 429 */           notify();
/*     */         }
/*     */       }
/*     */     }
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
/*     */     private synchronized Exception getException()
/*     */     {
/* 448 */       return this.exception;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private synchronized Socket getSocket()
/*     */     {
/* 455 */       return this.socket;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     synchronized void notUsed()
/*     */     {
/* 463 */       if (this.socket != null) {
/*     */         try {
/* 465 */           this.socket.close();
/*     */         }
/*     */         catch (IOException localIOException) {}
/*     */       }
/* 469 */       this.cleanUp = true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\proxy\RMIMasterSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
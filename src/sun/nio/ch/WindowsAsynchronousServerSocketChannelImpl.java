/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.channels.AcceptPendingException;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.AsynchronousSocketChannel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.nio.channels.NotYetBoundException;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import sun.misc.Unsafe;
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
/*     */ class WindowsAsynchronousServerSocketChannelImpl
/*     */   extends AsynchronousServerSocketChannelImpl
/*     */   implements Iocp.OverlappedChannel
/*     */ {
/*  45 */   private static final Unsafe unsafe = ;
/*     */   
/*     */ 
/*     */   private static final int DATA_BUFFER_SIZE = 88;
/*     */   
/*     */ 
/*     */   private final long handle;
/*     */   
/*     */ 
/*     */   private final int completionKey;
/*     */   
/*     */ 
/*     */   private final Iocp iocp;
/*     */   
/*     */ 
/*     */   private final PendingIoCache ioCache;
/*     */   
/*     */ 
/*     */   private final long dataBuffer;
/*     */   
/*  65 */   private AtomicBoolean accepting = new AtomicBoolean();
/*     */   
/*     */   WindowsAsynchronousServerSocketChannelImpl(Iocp paramIocp) throws IOException
/*     */   {
/*  69 */     super(paramIocp);
/*     */     
/*     */ 
/*  72 */     long l = IOUtil.fdVal(this.fd);
/*     */     int i;
/*     */     try {
/*  75 */       i = paramIocp.associate(this, l);
/*     */     } catch (IOException localIOException) {
/*  77 */       closesocket0(l);
/*  78 */       throw localIOException;
/*     */     }
/*     */     
/*  81 */     this.handle = l;
/*  82 */     this.completionKey = i;
/*  83 */     this.iocp = paramIocp;
/*  84 */     this.ioCache = new PendingIoCache();
/*  85 */     this.dataBuffer = unsafe.allocateMemory(88L);
/*     */   }
/*     */   
/*     */   public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong)
/*     */   {
/*  90 */     return this.ioCache.remove(paramLong);
/*     */   }
/*     */   
/*     */   void implClose()
/*     */     throws IOException
/*     */   {
/*  96 */     closesocket0(this.handle);
/*     */     
/*     */ 
/*  99 */     this.ioCache.close();
/*     */     
/*     */ 
/* 102 */     this.iocp.disassociate(this.completionKey);
/*     */     
/*     */ 
/* 105 */     unsafe.freeMemory(this.dataBuffer);
/*     */   }
/*     */   
/*     */   public AsynchronousChannelGroupImpl group()
/*     */   {
/* 110 */     return this.iocp;
/*     */   }
/*     */   
/*     */ 
/*     */   private class AcceptTask
/*     */     implements Runnable, Iocp.ResultHandler
/*     */   {
/*     */     private final WindowsAsynchronousSocketChannelImpl channel;
/*     */     
/*     */     private final AccessControlContext acc;
/*     */     
/*     */     private final PendingFuture<AsynchronousSocketChannel, Object> result;
/*     */     
/*     */     AcceptTask(AccessControlContext paramAccessControlContext, PendingFuture<AsynchronousSocketChannel, Object> paramPendingFuture)
/*     */     {
/* 125 */       this.channel = paramAccessControlContext;
/* 126 */       this.acc = paramPendingFuture;
/* 127 */       PendingFuture localPendingFuture; this.result = localPendingFuture;
/*     */     }
/*     */     
/*     */     void enableAccept() {
/* 131 */       WindowsAsynchronousServerSocketChannelImpl.this.accepting.set(false);
/*     */     }
/*     */     
/*     */     void closeChildChannel() {
/*     */       try {
/* 136 */         this.channel.close();
/*     */       }
/*     */       catch (IOException localIOException) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     void finishAccept()
/*     */       throws IOException
/*     */     {
/* 147 */       WindowsAsynchronousServerSocketChannelImpl.updateAcceptContext(WindowsAsynchronousServerSocketChannelImpl.this.handle, this.channel.handle());
/*     */       
/* 149 */       InetSocketAddress localInetSocketAddress1 = Net.localAddress(this.channel.fd);
/* 150 */       final InetSocketAddress localInetSocketAddress2 = Net.remoteAddress(this.channel.fd);
/* 151 */       this.channel.setConnected(localInetSocketAddress1, localInetSocketAddress2);
/*     */       
/*     */ 
/* 154 */       if (this.acc != null) {
/* 155 */         AccessController.doPrivileged(new PrivilegedAction() {
/*     */           public Void run() {
/* 157 */             SecurityManager localSecurityManager = System.getSecurityManager();
/* 158 */             localSecurityManager.checkAccept(localInetSocketAddress2.getAddress().getHostAddress(), localInetSocketAddress2
/* 159 */               .getPort());
/* 160 */             return null; } }, this.acc);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void run()
/*     */     {
/* 171 */       long l = 0L;
/*     */       
/*     */       try
/*     */       {
/* 175 */         WindowsAsynchronousServerSocketChannelImpl.this.begin();
/*     */         
/*     */ 
/*     */         try
/*     */         {
/* 180 */           this.channel.begin();
/*     */           
/* 182 */           synchronized (this.result) {
/* 183 */             l = WindowsAsynchronousServerSocketChannelImpl.this.ioCache.add(this.result);
/*     */             
/* 185 */             int i = WindowsAsynchronousServerSocketChannelImpl.accept0(WindowsAsynchronousServerSocketChannelImpl.this.handle, this.channel.handle(), l, WindowsAsynchronousServerSocketChannelImpl.this.dataBuffer);
/* 186 */             if (i == -2)
/*     */             {
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
/* 199 */               this.channel.end();return;
/*     */             }
/* 191 */             finishAccept();
/*     */             
/*     */ 
/* 194 */             enableAccept();
/* 195 */             this.result.setResult(this.channel);
/*     */           }
/*     */         }
/*     */         finally {
/* 199 */           this.channel.end();
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable) {
/* 203 */         if (l != 0L)
/* 204 */           WindowsAsynchronousServerSocketChannelImpl.this.ioCache.remove(l);
/* 205 */         closeChildChannel();
/* 206 */         Object localObject1; if ((localThrowable instanceof ClosedChannelException))
/* 207 */           localObject1 = new AsynchronousCloseException();
/* 208 */         if ((!(localObject1 instanceof IOException)) && (!(localObject1 instanceof SecurityException)))
/* 209 */           localObject1 = new IOException((Throwable)localObject1);
/* 210 */         enableAccept();
/* 211 */         this.result.setFailure((Throwable)localObject1);
/*     */       }
/*     */       finally {
/* 214 */         WindowsAsynchronousServerSocketChannelImpl.this.end();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 220 */       if (this.result.isCancelled()) {
/* 221 */         closeChildChannel();
/*     */       }
/*     */       
/*     */ 
/* 225 */       Invoker.invokeIndirectly(this.result);
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void completed(int paramInt, boolean paramBoolean)
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 152	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:this$0	Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;
/*     */       //   4: invokestatic 178	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl:access$600	(Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;)Lsun/nio/ch/Iocp;
/*     */       //   7: invokevirtual 162	sun/nio/ch/Iocp:isShutdown	()Z
/*     */       //   10: ifeq +18 -> 28
/*     */       //   13: new 73	java/io/IOException
/*     */       //   16: dup
/*     */       //   17: new 81	java/nio/channels/ShutdownChannelGroupException
/*     */       //   20: dup
/*     */       //   21: invokespecial 158	java/nio/channels/ShutdownChannelGroupException:<init>	()V
/*     */       //   24: invokespecial 155	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
/*     */       //   27: athrow
/*     */       //   28: aload_0
/*     */       //   29: getfield 152	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:this$0	Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;
/*     */       //   32: invokevirtual 170	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl:begin	()V
/*     */       //   35: aload_0
/*     */       //   36: getfield 153	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:channel	Lsun/nio/ch/WindowsAsynchronousSocketChannelImpl;
/*     */       //   39: invokevirtual 185	sun/nio/ch/WindowsAsynchronousSocketChannelImpl:begin	()V
/*     */       //   42: aload_0
/*     */       //   43: invokevirtual 182	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:finishAccept	()V
/*     */       //   46: aload_0
/*     */       //   47: getfield 153	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:channel	Lsun/nio/ch/WindowsAsynchronousSocketChannelImpl;
/*     */       //   50: invokevirtual 187	sun/nio/ch/WindowsAsynchronousSocketChannelImpl:end	()V
/*     */       //   53: goto +13 -> 66
/*     */       //   56: astore_3
/*     */       //   57: aload_0
/*     */       //   58: getfield 153	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:channel	Lsun/nio/ch/WindowsAsynchronousSocketChannelImpl;
/*     */       //   61: invokevirtual 187	sun/nio/ch/WindowsAsynchronousSocketChannelImpl:end	()V
/*     */       //   64: aload_3
/*     */       //   65: athrow
/*     */       //   66: aload_0
/*     */       //   67: getfield 152	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:this$0	Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;
/*     */       //   70: invokevirtual 171	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl:end	()V
/*     */       //   73: goto +15 -> 88
/*     */       //   76: astore 4
/*     */       //   78: aload_0
/*     */       //   79: getfield 152	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:this$0	Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;
/*     */       //   82: invokevirtual 171	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl:end	()V
/*     */       //   85: aload 4
/*     */       //   87: athrow
/*     */       //   88: aload_0
/*     */       //   89: invokevirtual 181	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:enableAccept	()V
/*     */       //   92: aload_0
/*     */       //   93: getfield 151	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:result	Lsun/nio/ch/PendingFuture;
/*     */       //   96: aload_0
/*     */       //   97: getfield 153	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:channel	Lsun/nio/ch/WindowsAsynchronousSocketChannelImpl;
/*     */       //   100: invokevirtual 166	sun/nio/ch/PendingFuture:setResult	(Ljava/lang/Object;)V
/*     */       //   103: goto +58 -> 161
/*     */       //   106: astore_3
/*     */       //   107: aload_0
/*     */       //   108: invokevirtual 181	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:enableAccept	()V
/*     */       //   111: aload_0
/*     */       //   112: invokevirtual 180	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:closeChildChannel	()V
/*     */       //   115: aload_3
/*     */       //   116: instanceof 80
/*     */       //   119: ifeq +11 -> 130
/*     */       //   122: new 79	java/nio/channels/AsynchronousCloseException
/*     */       //   125: dup
/*     */       //   126: invokespecial 157	java/nio/channels/AsynchronousCloseException:<init>	()V
/*     */       //   129: astore_3
/*     */       //   130: aload_3
/*     */       //   131: instanceof 73
/*     */       //   134: ifne +19 -> 153
/*     */       //   137: aload_3
/*     */       //   138: instanceof 76
/*     */       //   141: ifne +12 -> 153
/*     */       //   144: new 73	java/io/IOException
/*     */       //   147: dup
/*     */       //   148: aload_3
/*     */       //   149: invokespecial 155	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
/*     */       //   152: astore_3
/*     */       //   153: aload_0
/*     */       //   154: getfield 151	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:result	Lsun/nio/ch/PendingFuture;
/*     */       //   157: aload_3
/*     */       //   158: invokevirtual 167	sun/nio/ch/PendingFuture:setFailure	(Ljava/lang/Throwable;)V
/*     */       //   161: aload_0
/*     */       //   162: getfield 151	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:result	Lsun/nio/ch/PendingFuture;
/*     */       //   165: invokevirtual 165	sun/nio/ch/PendingFuture:isCancelled	()Z
/*     */       //   168: ifeq +7 -> 175
/*     */       //   171: aload_0
/*     */       //   172: invokevirtual 180	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:closeChildChannel	()V
/*     */       //   175: aload_0
/*     */       //   176: getfield 151	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:result	Lsun/nio/ch/PendingFuture;
/*     */       //   179: invokestatic 161	sun/nio/ch/Invoker:invokeIndirectly	(Lsun/nio/ch/PendingFuture;)V
/*     */       //   182: return
/*     */       // Line number table:
/*     */       //   Java source line #235	-> byte code offset #0
/*     */       //   Java source line #236	-> byte code offset #13
/*     */       //   Java source line #241	-> byte code offset #28
/*     */       //   Java source line #243	-> byte code offset #35
/*     */       //   Java source line #244	-> byte code offset #42
/*     */       //   Java source line #246	-> byte code offset #46
/*     */       //   Java source line #247	-> byte code offset #53
/*     */       //   Java source line #246	-> byte code offset #56
/*     */       //   Java source line #247	-> byte code offset #64
/*     */       //   Java source line #249	-> byte code offset #66
/*     */       //   Java source line #250	-> byte code offset #73
/*     */       //   Java source line #249	-> byte code offset #76
/*     */       //   Java source line #250	-> byte code offset #85
/*     */       //   Java source line #253	-> byte code offset #88
/*     */       //   Java source line #254	-> byte code offset #92
/*     */       //   Java source line #263	-> byte code offset #103
/*     */       //   Java source line #255	-> byte code offset #106
/*     */       //   Java source line #256	-> byte code offset #107
/*     */       //   Java source line #257	-> byte code offset #111
/*     */       //   Java source line #258	-> byte code offset #115
/*     */       //   Java source line #259	-> byte code offset #122
/*     */       //   Java source line #260	-> byte code offset #130
/*     */       //   Java source line #261	-> byte code offset #144
/*     */       //   Java source line #262	-> byte code offset #153
/*     */       //   Java source line #267	-> byte code offset #161
/*     */       //   Java source line #268	-> byte code offset #171
/*     */       //   Java source line #272	-> byte code offset #175
/*     */       //   Java source line #273	-> byte code offset #182
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	183	0	this	AcceptTask
/*     */       //   0	183	1	paramInt	int
/*     */       //   0	183	2	paramBoolean	boolean
/*     */       //   56	9	3	localObject1	Object
/*     */       //   106	10	3	localThrowable	Throwable
/*     */       //   129	29	3	localObject2	Object
/*     */       //   76	10	4	localObject3	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   35	46	56	finally
/*     */       //   28	66	76	finally
/*     */       //   76	78	76	finally
/*     */       //   0	103	106	java/lang/Throwable
/*     */     }
/*     */     
/*     */     public void failed(int paramInt, IOException paramIOException)
/*     */     {
/* 277 */       enableAccept();
/* 278 */       closeChildChannel();
/*     */       
/*     */ 
/* 281 */       if (WindowsAsynchronousServerSocketChannelImpl.this.isOpen()) {
/* 282 */         this.result.setFailure(paramIOException);
/*     */       } else {
/* 284 */         this.result.setFailure(new AsynchronousCloseException());
/*     */       }
/* 286 */       Invoker.invokeIndirectly(this.result);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   Future<AsynchronousSocketChannel> implAccept(Object paramObject, CompletionHandler<AsynchronousSocketChannel, Object> paramCompletionHandler)
/*     */   {
/* 294 */     if (!isOpen()) {
/* 295 */       localObject1 = new ClosedChannelException();
/* 296 */       if (paramCompletionHandler == null)
/* 297 */         return CompletedFuture.withFailure((Throwable)localObject1);
/* 298 */       Invoker.invokeIndirectly(this, paramCompletionHandler, paramObject, null, (Throwable)localObject1);
/* 299 */       return null;
/*     */     }
/* 301 */     if (isAcceptKilled()) {
/* 302 */       throw new RuntimeException("Accept not allowed due to cancellation");
/*     */     }
/*     */     
/* 305 */     if (this.localAddress == null) {
/* 306 */       throw new NotYetBoundException();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 312 */     Object localObject1 = null;
/* 313 */     Object localObject2 = null;
/*     */     try {
/* 315 */       begin();
/* 316 */       localObject1 = new WindowsAsynchronousSocketChannelImpl(this.iocp, false);
/*     */     } catch (IOException localIOException) {
/* 318 */       localObject2 = localIOException;
/*     */     } finally {
/* 320 */       end();
/*     */     }
/* 322 */     if (localObject2 != null) {
/* 323 */       if (paramCompletionHandler == null)
/* 324 */         return CompletedFuture.withFailure((Throwable)localObject2);
/* 325 */       Invoker.invokeIndirectly(this, paramCompletionHandler, paramObject, null, (Throwable)localObject2);
/* 326 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 333 */     AccessControlContext localAccessControlContext = System.getSecurityManager() == null ? null : AccessController.getContext();
/*     */     
/* 335 */     PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramObject);
/*     */     
/* 337 */     AcceptTask localAcceptTask = new AcceptTask((WindowsAsynchronousSocketChannelImpl)localObject1, localAccessControlContext, localPendingFuture);
/* 338 */     localPendingFuture.setContext(localAcceptTask);
/*     */     
/*     */ 
/* 341 */     if (!this.accepting.compareAndSet(false, true)) {
/* 342 */       throw new AcceptPendingException();
/*     */     }
/*     */     
/* 345 */     if (Iocp.supportsThreadAgnosticIo()) {
/* 346 */       localAcceptTask.run();
/*     */     } else {
/* 348 */       Invoker.invokeOnThreadInThreadPool(this, localAcceptTask);
/*     */     }
/* 350 */     return localPendingFuture;
/*     */   }
/*     */   
/*     */   private static native void initIDs();
/*     */   
/*     */   private static native int accept0(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
/*     */     throws IOException;
/*     */   
/*     */   private static native void updateAcceptContext(long paramLong1, long paramLong2)
/*     */     throws IOException;
/*     */   
/*     */   private static native void closesocket0(long paramLong)
/*     */     throws IOException;
/*     */   
/*     */   static
/*     */   {
/* 366 */     IOUtil.load();
/* 367 */     initIDs();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\WindowsAsynchronousServerSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
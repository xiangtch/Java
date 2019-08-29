/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.BufferOverflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AlreadyConnectedException;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.nio.channels.ConnectionPendingException;
/*     */ import java.nio.channels.InterruptedByTimeoutException;
/*     */ import java.nio.channels.ShutdownChannelGroupException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ class WindowsAsynchronousSocketChannelImpl
/*     */   extends AsynchronousSocketChannelImpl
/*     */   implements Iocp.OverlappedChannel
/*     */ {
/*  46 */   private static final Unsafe unsafe = ;
/*  47 */   private static int addressSize = unsafe.addressSize();
/*     */   
/*     */   private static int dependsArch(int paramInt1, int paramInt2) {
/*  50 */     return addressSize == 4 ? paramInt1 : paramInt2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  59 */   private static final int SIZEOF_WSABUF = dependsArch(8, 16);
/*     */   private static final int OFFSETOF_LEN = 0;
/*  61 */   private static final int OFFSETOF_BUF = dependsArch(4, 8);
/*     */   
/*     */ 
/*     */   private static final int MAX_WSABUF = 16;
/*     */   
/*  66 */   private static final int SIZEOF_WSABUFARRAY = 16 * SIZEOF_WSABUF;
/*     */   
/*     */ 
/*     */ 
/*     */   final long handle;
/*     */   
/*     */ 
/*     */   private final Iocp iocp;
/*     */   
/*     */ 
/*     */   private final int completionKey;
/*     */   
/*     */ 
/*     */   private final PendingIoCache ioCache;
/*     */   
/*     */ 
/*     */   private final long readBufferArray;
/*     */   
/*     */ 
/*     */   private final long writeBufferArray;
/*     */   
/*     */ 
/*     */ 
/*     */   WindowsAsynchronousSocketChannelImpl(Iocp paramIocp, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  92 */     super(paramIocp);
/*     */     
/*     */ 
/*  95 */     long l = IOUtil.fdVal(this.fd);
/*  96 */     int i = 0;
/*     */     try {
/*  98 */       i = paramIocp.associate(this, l);
/*     */     } catch (ShutdownChannelGroupException localShutdownChannelGroupException) {
/* 100 */       if (paramBoolean) {
/* 101 */         closesocket0(l);
/* 102 */         throw localShutdownChannelGroupException;
/*     */       }
/*     */     } catch (IOException localIOException) {
/* 105 */       closesocket0(l);
/* 106 */       throw localIOException;
/*     */     }
/*     */     
/* 109 */     this.handle = l;
/* 110 */     this.iocp = paramIocp;
/* 111 */     this.completionKey = i;
/* 112 */     this.ioCache = new PendingIoCache();
/*     */     
/*     */ 
/* 115 */     this.readBufferArray = unsafe.allocateMemory(SIZEOF_WSABUFARRAY);
/* 116 */     this.writeBufferArray = unsafe.allocateMemory(SIZEOF_WSABUFARRAY);
/*     */   }
/*     */   
/*     */   WindowsAsynchronousSocketChannelImpl(Iocp paramIocp) throws IOException {
/* 120 */     this(paramIocp, true);
/*     */   }
/*     */   
/*     */   public AsynchronousChannelGroupImpl group()
/*     */   {
/* 125 */     return this.iocp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong)
/*     */   {
/* 133 */     return this.ioCache.remove(paramLong);
/*     */   }
/*     */   
/*     */   long handle()
/*     */   {
/* 138 */     return this.handle;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void setConnected(InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2)
/*     */   {
/* 146 */     synchronized (this.stateLock) {
/* 147 */       this.state = 2;
/* 148 */       this.localAddress = paramInetSocketAddress1;
/* 149 */       this.remoteAddress = paramInetSocketAddress2;
/*     */     }
/*     */   }
/*     */   
/*     */   void implClose()
/*     */     throws IOException
/*     */   {
/* 156 */     closesocket0(this.handle);
/*     */     
/*     */ 
/* 159 */     this.ioCache.close();
/*     */     
/*     */ 
/* 162 */     unsafe.freeMemory(this.readBufferArray);
/* 163 */     unsafe.freeMemory(this.writeBufferArray);
/*     */     
/*     */ 
/*     */ 
/* 167 */     if (this.completionKey != 0) {
/* 168 */       this.iocp.disassociate(this.completionKey);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onCancel(PendingFuture<?, ?> paramPendingFuture) {
/* 173 */     if ((paramPendingFuture.getContext() instanceof ConnectTask))
/* 174 */       killConnect();
/* 175 */     if ((paramPendingFuture.getContext() instanceof ReadTask))
/* 176 */       killReading();
/* 177 */     if ((paramPendingFuture.getContext() instanceof WriteTask)) {
/* 178 */       killWriting();
/*     */     }
/*     */   }
/*     */   
/*     */   private class ConnectTask<A>
/*     */     implements Runnable, Iocp.ResultHandler
/*     */   {
/*     */     private final InetSocketAddress remote;
/*     */     private final PendingFuture<Void, A> result;
/*     */     
/*     */     ConnectTask(PendingFuture<Void, A> paramPendingFuture)
/*     */     {
/* 190 */       this.remote = paramPendingFuture;
/* 191 */       PendingFuture localPendingFuture; this.result = localPendingFuture;
/*     */     }
/*     */     
/*     */     private void closeChannel() {
/*     */       try {
/* 196 */         WindowsAsynchronousSocketChannelImpl.this.close();
/*     */       } catch (IOException localIOException) {}
/*     */     }
/*     */     
/*     */     private IOException toIOException(Throwable paramThrowable) {
/* 201 */       if ((paramThrowable instanceof IOException)) {
/* 202 */         if ((paramThrowable instanceof ClosedChannelException))
/* 203 */           paramThrowable = new AsynchronousCloseException();
/* 204 */         return (IOException)paramThrowable;
/*     */       }
/* 206 */       return new IOException(paramThrowable);
/*     */     }
/*     */     
/*     */ 
/*     */     private void afterConnect()
/*     */       throws IOException
/*     */     {
/* 213 */       WindowsAsynchronousSocketChannelImpl.updateConnectContext(WindowsAsynchronousSocketChannelImpl.this.handle);
/* 214 */       synchronized (WindowsAsynchronousSocketChannelImpl.this.stateLock) {
/* 215 */         WindowsAsynchronousSocketChannelImpl.this.state = 2;
/* 216 */         WindowsAsynchronousSocketChannelImpl.this.remoteAddress = this.remote;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void run()
/*     */     {
/* 225 */       long l = 0L;
/* 226 */       Object localObject1 = null;
/*     */       try {
/* 228 */         WindowsAsynchronousSocketChannelImpl.this.begin();
/*     */         
/*     */ 
/*     */ 
/* 232 */         synchronized (this.result) {
/* 233 */           l = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
/*     */           
/* 235 */           int i = WindowsAsynchronousSocketChannelImpl.connect0(WindowsAsynchronousSocketChannelImpl.this.handle, Net.isIPv6Available(), this.remote.getAddress(), this.remote
/* 236 */             .getPort(), l);
/* 237 */           if (i == -2)
/*     */           {
/* 239 */             return;
/*     */           }
/*     */           
/*     */ 
/* 243 */           afterConnect();
/* 244 */           this.result.setResult(null);
/*     */         }
/*     */       } catch (Throwable localThrowable) {
/* 247 */         if (l != 0L)
/* 248 */           WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(l);
/* 249 */         localObject1 = localThrowable;
/*     */       } finally {
/* 251 */         WindowsAsynchronousSocketChannelImpl.this.end();
/*     */       }
/*     */       
/* 254 */       if (localObject1 != null) {
/* 255 */         closeChannel();
/* 256 */         this.result.setFailure(toIOException((Throwable)localObject1));
/*     */       }
/* 258 */       Invoker.invoke(this.result);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void completed(int paramInt, boolean paramBoolean)
/*     */     {
/* 266 */       Object localObject1 = null;
/*     */       try {
/* 268 */         WindowsAsynchronousSocketChannelImpl.this.begin();
/* 269 */         afterConnect();
/* 270 */         this.result.setResult(null);
/*     */       }
/*     */       catch (Throwable localThrowable) {
/* 273 */         localObject1 = localThrowable;
/*     */       } finally {
/* 275 */         WindowsAsynchronousSocketChannelImpl.this.end();
/*     */       }
/*     */       
/*     */ 
/* 279 */       if (localObject1 != null) {
/* 280 */         closeChannel();
/* 281 */         this.result.setFailure(toIOException((Throwable)localObject1));
/*     */       }
/*     */       
/* 284 */       if (paramBoolean) {
/* 285 */         Invoker.invokeUnchecked(this.result);
/*     */       } else {
/* 287 */         Invoker.invoke(this.result);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void failed(int paramInt, IOException paramIOException)
/*     */     {
/* 296 */       if (WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
/* 297 */         closeChannel();
/* 298 */         this.result.setFailure(paramIOException);
/*     */       } else {
/* 300 */         this.result.setFailure(new AsynchronousCloseException());
/*     */       }
/* 302 */       Invoker.invoke(this.result);
/*     */     }
/*     */   }
/*     */   
/*     */   private void doPrivilegedBind(final SocketAddress paramSocketAddress) throws IOException {
/*     */     try {
/* 308 */       AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */         public Void run() throws IOException {
/* 310 */           WindowsAsynchronousSocketChannelImpl.this.bind(paramSocketAddress);
/* 311 */           return null;
/*     */         }
/*     */       });
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 315 */       throw ((IOException)localPrivilegedActionException.getException());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler)
/*     */   {
/* 324 */     if (!isOpen()) {
/* 325 */       localObject1 = new ClosedChannelException();
/* 326 */       if (paramCompletionHandler == null)
/* 327 */         return CompletedFuture.withFailure((Throwable)localObject1);
/* 328 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)localObject1);
/* 329 */       return null;
/*     */     }
/*     */     
/* 332 */     Object localObject1 = Net.checkAddress(paramSocketAddress);
/*     */     
/*     */ 
/* 335 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 336 */     if (localSecurityManager != null) {
/* 337 */       localSecurityManager.checkConnect(((InetSocketAddress)localObject1).getAddress().getHostAddress(), ((InetSocketAddress)localObject1).getPort());
/*     */     }
/*     */     
/*     */ 
/* 341 */     Object localObject2 = null;
/* 342 */     synchronized (this.stateLock) {
/* 343 */       if (this.state == 2)
/* 344 */         throw new AlreadyConnectedException();
/* 345 */       if (this.state == 1)
/* 346 */         throw new ConnectionPendingException();
/* 347 */       if (this.localAddress == null) {
/*     */         try {
/* 349 */           InetSocketAddress localInetSocketAddress = new InetSocketAddress(0);
/* 350 */           if (localSecurityManager == null) {
/* 351 */             bind(localInetSocketAddress);
/*     */           } else {
/* 353 */             doPrivilegedBind(localInetSocketAddress);
/*     */           }
/*     */         } catch (IOException localIOException2) {
/* 356 */           localObject2 = localIOException2;
/*     */         }
/*     */       }
/* 359 */       if (localObject2 == null) {
/* 360 */         this.state = 1;
/*     */       }
/*     */     }
/*     */     
/* 364 */     if (localObject2 != null) {
/*     */       try {
/* 366 */         close();
/*     */       } catch (IOException localIOException1) {}
/* 368 */       if (paramCompletionHandler == null)
/* 369 */         return CompletedFuture.withFailure((Throwable)localObject2);
/* 370 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)localObject2);
/* 371 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 375 */     PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
/*     */     
/* 377 */     ConnectTask localConnectTask = new ConnectTask((InetSocketAddress)localObject1, localPendingFuture);
/* 378 */     localPendingFuture.setContext(localConnectTask);
/*     */     
/*     */ 
/* 381 */     if (Iocp.supportsThreadAgnosticIo()) {
/* 382 */       localConnectTask.run();
/*     */     } else {
/* 384 */       Invoker.invokeOnThreadInThreadPool(this, localConnectTask);
/*     */     }
/* 386 */     return localPendingFuture;
/*     */   }
/*     */   
/*     */ 
/*     */   private class ReadTask<V, A>
/*     */     implements Runnable, Iocp.ResultHandler
/*     */   {
/*     */     private final ByteBuffer[] bufs;
/*     */     
/*     */     private final int numBufs;
/*     */     
/*     */     private final boolean scatteringRead;
/*     */     
/*     */     private final PendingFuture<V, A> result;
/*     */     
/*     */     private ByteBuffer[] shadow;
/*     */     
/*     */ 
/*     */     ReadTask(boolean paramBoolean, PendingFuture<V, A> paramPendingFuture)
/*     */     {
/* 406 */       this.bufs = paramBoolean;
/* 407 */       this.numBufs = (paramBoolean.length > 16 ? 16 : paramBoolean.length);
/* 408 */       this.scatteringRead = paramPendingFuture;
/* 409 */       PendingFuture localPendingFuture; this.result = localPendingFuture;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     void prepareBuffers()
/*     */     {
/* 417 */       this.shadow = new ByteBuffer[this.numBufs];
/* 418 */       long l1 = WindowsAsynchronousSocketChannelImpl.this.readBufferArray;
/* 419 */       for (int i = 0; i < this.numBufs; i++) {
/* 420 */         ByteBuffer localByteBuffer1 = this.bufs[i];
/* 421 */         int j = localByteBuffer1.position();
/* 422 */         int k = localByteBuffer1.limit();
/* 423 */         assert (j <= k);
/* 424 */         int m = j <= k ? k - j : 0;
/*     */         long l2;
/* 426 */         if (!(localByteBuffer1 instanceof DirectBuffer))
/*     */         {
/* 428 */           ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(m);
/* 429 */           this.shadow[i] = localByteBuffer2;
/* 430 */           l2 = ((DirectBuffer)localByteBuffer2).address();
/*     */         } else {
/* 432 */           this.shadow[i] = localByteBuffer1;
/* 433 */           l2 = ((DirectBuffer)localByteBuffer1).address() + j;
/*     */         }
/* 435 */         WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(l1 + WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, l2);
/* 436 */         WindowsAsynchronousSocketChannelImpl.unsafe.putInt(l1 + 0L, m);
/* 437 */         l1 += WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     void updateBuffers(int paramInt)
/*     */     {
/* 446 */       for (int i = 0; i < this.numBufs; i++) {
/* 447 */         ByteBuffer localByteBuffer = this.shadow[i];
/* 448 */         int j = localByteBuffer.position();
/* 449 */         int k = localByteBuffer.remaining();
/* 450 */         int m; if (paramInt >= k) {
/* 451 */           paramInt -= k;
/* 452 */           m = j + k;
/*     */           try {
/* 454 */             localByteBuffer.position(m);
/*     */           }
/*     */           catch (IllegalArgumentException localIllegalArgumentException1) {}
/*     */         }
/*     */         else {
/* 459 */           if (paramInt <= 0) break;
/* 460 */           assert (j + paramInt < 2147483647L);
/* 461 */           m = j + paramInt;
/*     */           try {
/* 463 */             localByteBuffer.position(m);
/*     */           }
/*     */           catch (IllegalArgumentException localIllegalArgumentException2) {}
/*     */           
/* 467 */           break;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 473 */       for (i = 0; i < this.numBufs; i++) {
/* 474 */         if (!(this.bufs[i] instanceof DirectBuffer)) {
/* 475 */           this.shadow[i].flip();
/*     */           try {
/* 477 */             this.bufs[i].put(this.shadow[i]);
/*     */           }
/*     */           catch (BufferOverflowException localBufferOverflowException) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     void releaseBuffers()
/*     */     {
/* 486 */       for (int i = 0; i < this.numBufs; i++) {
/* 487 */         if (!(this.bufs[i] instanceof DirectBuffer)) {
/* 488 */           Util.releaseTemporaryDirectBuffer(this.shadow[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 496 */       long l = 0L;
/* 497 */       int i = 0;
/* 498 */       int j = 0;
/*     */       try
/*     */       {
/* 501 */         WindowsAsynchronousSocketChannelImpl.this.begin();
/*     */         
/*     */ 
/* 504 */         prepareBuffers();
/* 505 */         i = 1;
/*     */         
/*     */ 
/* 508 */         l = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
/*     */         
/*     */ 
/* 511 */         int k = WindowsAsynchronousSocketChannelImpl.read0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.readBufferArray, l);
/* 512 */         if (k == -2)
/*     */         {
/* 514 */           j = 1;
/* 515 */           return;
/*     */         }
/* 517 */         if (k == -1)
/*     */         {
/* 519 */           WindowsAsynchronousSocketChannelImpl.this.enableReading();
/* 520 */           if (this.scatteringRead) {
/* 521 */             this.result.setResult(Long.valueOf(-1L));
/*     */           } else {
/* 523 */             this.result.setResult(Integer.valueOf(-1));
/*     */           }
/*     */         } else {
/* 526 */           throw new InternalError("Read completed immediately");
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/* 531 */         WindowsAsynchronousSocketChannelImpl.this.enableReading();
/* 532 */         Object localObject1; if ((localThrowable instanceof ClosedChannelException))
/* 533 */           localObject1 = new AsynchronousCloseException();
/* 534 */         if (!(localObject1 instanceof IOException))
/* 535 */           localObject1 = new IOException((Throwable)localObject1);
/* 536 */         this.result.setFailure((Throwable)localObject1);
/*     */       }
/*     */       finally {
/* 539 */         if (j == 0) {
/* 540 */           if (l != 0L)
/* 541 */             WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(l);
/* 542 */           if (i != 0)
/* 543 */             releaseBuffers();
/*     */         }
/* 545 */         WindowsAsynchronousSocketChannelImpl.this.end();
/*     */       }
/*     */       
/*     */ 
/* 549 */       Invoker.invoke(this.result);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void completed(int paramInt, boolean paramBoolean)
/*     */     {
/* 558 */       if (paramInt == 0) {
/* 559 */         paramInt = -1;
/*     */       } else {
/* 561 */         updateBuffers(paramInt);
/*     */       }
/*     */       
/*     */ 
/* 565 */       releaseBuffers();
/*     */       
/*     */ 
/* 568 */       synchronized (this.result) {
/* 569 */         if (this.result.isDone())
/* 570 */           return;
/* 571 */         WindowsAsynchronousSocketChannelImpl.this.enableReading();
/* 572 */         if (this.scatteringRead) {
/* 573 */           this.result.setResult(Long.valueOf(paramInt));
/*     */         } else {
/* 575 */           this.result.setResult(Integer.valueOf(paramInt));
/*     */         }
/*     */       }
/* 578 */       if (paramBoolean) {
/* 579 */         Invoker.invokeUnchecked(this.result);
/*     */       } else {
/* 581 */         Invoker.invoke(this.result);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(int paramInt, IOException paramIOException)
/*     */     {
/* 588 */       releaseBuffers();
/*     */       
/*     */ 
/* 591 */       if (!WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
/* 592 */         paramIOException = new AsynchronousCloseException();
/*     */       }
/* 594 */       synchronized (this.result) {
/* 595 */         if (this.result.isDone())
/* 596 */           return;
/* 597 */         WindowsAsynchronousSocketChannelImpl.this.enableReading();
/* 598 */         this.result.setFailure(paramIOException);
/*     */       }
/* 600 */       Invoker.invoke(this.result);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     void timeout()
/*     */     {
/* 608 */       synchronized (this.result) {
/* 609 */         if (this.result.isDone()) {
/* 610 */           return;
/*     */         }
/*     */         
/* 613 */         WindowsAsynchronousSocketChannelImpl.this.enableReading(true);
/* 614 */         this.result.setFailure(new InterruptedByTimeoutException());
/*     */       }
/*     */       
/*     */ 
/* 618 */       Invoker.invoke(this.result);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
/*     */   {
/* 632 */     PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
/*     */     
/*     */     ByteBuffer[] arrayOfByteBuffer;
/* 635 */     if (paramBoolean) {
/* 636 */       arrayOfByteBuffer = paramArrayOfByteBuffer;
/*     */     } else {
/* 638 */       arrayOfByteBuffer = new ByteBuffer[1];
/* 639 */       arrayOfByteBuffer[0] = paramByteBuffer;
/*     */     }
/* 641 */     final ReadTask localReadTask = new ReadTask(arrayOfByteBuffer, paramBoolean, localPendingFuture);
/*     */     
/* 643 */     localPendingFuture.setContext(localReadTask);
/*     */     
/*     */ 
/* 646 */     if (paramLong > 0L) {
/* 647 */       Future localFuture = this.iocp.schedule(new Runnable()
/*     */       {
/* 649 */         public void run() { localReadTask.timeout(); } }, paramLong, paramTimeUnit);
/*     */       
/*     */ 
/* 652 */       localPendingFuture.setTimeoutTask(localFuture);
/*     */     }
/*     */     
/*     */ 
/* 656 */     if (Iocp.supportsThreadAgnosticIo()) {
/* 657 */       localReadTask.run();
/*     */     } else {
/* 659 */       Invoker.invokeOnThreadInThreadPool(this, localReadTask);
/*     */     }
/* 661 */     return localPendingFuture;
/*     */   }
/*     */   
/*     */ 
/*     */   private class WriteTask<V, A>
/*     */     implements Runnable, Iocp.ResultHandler
/*     */   {
/*     */     private final ByteBuffer[] bufs;
/*     */     
/*     */     private final int numBufs;
/*     */     
/*     */     private final boolean gatheringWrite;
/*     */     
/*     */     private final PendingFuture<V, A> result;
/*     */     
/*     */     private ByteBuffer[] shadow;
/*     */     
/*     */ 
/*     */     WriteTask(boolean paramBoolean, PendingFuture<V, A> paramPendingFuture)
/*     */     {
/* 681 */       this.bufs = paramBoolean;
/* 682 */       this.numBufs = (paramBoolean.length > 16 ? 16 : paramBoolean.length);
/* 683 */       this.gatheringWrite = paramPendingFuture;
/* 684 */       PendingFuture localPendingFuture; this.result = localPendingFuture;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     void prepareBuffers()
/*     */     {
/* 692 */       this.shadow = new ByteBuffer[this.numBufs];
/* 693 */       long l1 = WindowsAsynchronousSocketChannelImpl.this.writeBufferArray;
/* 694 */       for (int i = 0; i < this.numBufs; i++) {
/* 695 */         ByteBuffer localByteBuffer1 = this.bufs[i];
/* 696 */         int j = localByteBuffer1.position();
/* 697 */         int k = localByteBuffer1.limit();
/* 698 */         assert (j <= k);
/* 699 */         int m = j <= k ? k - j : 0;
/*     */         long l2;
/* 701 */         if (!(localByteBuffer1 instanceof DirectBuffer))
/*     */         {
/* 703 */           ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(m);
/* 704 */           localByteBuffer2.put(localByteBuffer1);
/* 705 */           localByteBuffer2.flip();
/* 706 */           localByteBuffer1.position(j);
/* 707 */           this.shadow[i] = localByteBuffer2;
/* 708 */           l2 = ((DirectBuffer)localByteBuffer2).address();
/*     */         } else {
/* 710 */           this.shadow[i] = localByteBuffer1;
/* 711 */           l2 = ((DirectBuffer)localByteBuffer1).address() + j;
/*     */         }
/* 713 */         WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(l1 + WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, l2);
/* 714 */         WindowsAsynchronousSocketChannelImpl.unsafe.putInt(l1 + 0L, m);
/* 715 */         l1 += WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     void updateBuffers(int paramInt)
/*     */     {
/* 725 */       for (int i = 0; i < this.numBufs; i++) {
/* 726 */         ByteBuffer localByteBuffer = this.bufs[i];
/* 727 */         int j = localByteBuffer.position();
/* 728 */         int k = localByteBuffer.limit();
/* 729 */         int m = j <= k ? k - j : k;
/* 730 */         int n; if (paramInt >= m) {
/* 731 */           paramInt -= m;
/* 732 */           n = j + m;
/*     */           try {
/* 734 */             localByteBuffer.position(n);
/*     */           }
/*     */           catch (IllegalArgumentException localIllegalArgumentException1) {}
/*     */         }
/*     */         else {
/* 739 */           if (paramInt <= 0) break;
/* 740 */           assert (j + paramInt < 2147483647L);
/* 741 */           n = j + paramInt;
/*     */           try {
/* 743 */             localByteBuffer.position(n);
/*     */           }
/*     */           catch (IllegalArgumentException localIllegalArgumentException2) {}
/*     */           
/* 747 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     void releaseBuffers()
/*     */     {
/* 754 */       for (int i = 0; i < this.numBufs; i++) {
/* 755 */         if (!(this.bufs[i] instanceof DirectBuffer)) {
/* 756 */           Util.releaseTemporaryDirectBuffer(this.shadow[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 764 */       long l = 0L;
/* 765 */       int i = 0;
/* 766 */       int j = 0;
/* 767 */       int k = 0;
/*     */       try
/*     */       {
/* 770 */         WindowsAsynchronousSocketChannelImpl.this.begin();
/*     */         
/*     */ 
/* 773 */         prepareBuffers();
/* 774 */         i = 1;
/*     */         
/*     */ 
/* 777 */         l = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
/* 778 */         int m = WindowsAsynchronousSocketChannelImpl.write0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.writeBufferArray, l);
/* 779 */         if (m == -2)
/*     */         {
/* 781 */           j = 1;
/* 782 */           return;
/*     */         }
/* 784 */         if (m == -1)
/*     */         {
/* 786 */           k = 1;
/* 787 */           throw new ClosedChannelException();
/*     */         }
/*     */         
/* 790 */         throw new InternalError("Write completed immediately");
/*     */       }
/*     */       catch (Throwable localThrowable) {
/* 793 */         WindowsAsynchronousSocketChannelImpl.this.enableWriting();
/* 794 */         Object localObject1; if ((k == 0) && ((localThrowable instanceof ClosedChannelException)))
/* 795 */           localObject1 = new AsynchronousCloseException();
/* 796 */         if (!(localObject1 instanceof IOException))
/* 797 */           localObject1 = new IOException((Throwable)localObject1);
/* 798 */         this.result.setFailure((Throwable)localObject1);
/*     */       }
/*     */       finally {
/* 801 */         if (j == 0) {
/* 802 */           if (l != 0L)
/* 803 */             WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(l);
/* 804 */           if (i != 0)
/* 805 */             releaseBuffers();
/*     */         }
/* 807 */         WindowsAsynchronousSocketChannelImpl.this.end();
/*     */       }
/*     */       
/*     */ 
/* 811 */       Invoker.invoke(this.result);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void completed(int paramInt, boolean paramBoolean)
/*     */     {
/* 820 */       updateBuffers(paramInt);
/*     */       
/*     */ 
/* 823 */       releaseBuffers();
/*     */       
/*     */ 
/* 826 */       synchronized (this.result) {
/* 827 */         if (this.result.isDone())
/* 828 */           return;
/* 829 */         WindowsAsynchronousSocketChannelImpl.this.enableWriting();
/* 830 */         if (this.gatheringWrite) {
/* 831 */           this.result.setResult(Long.valueOf(paramInt));
/*     */         } else {
/* 833 */           this.result.setResult(Integer.valueOf(paramInt));
/*     */         }
/*     */       }
/* 836 */       if (paramBoolean) {
/* 837 */         Invoker.invokeUnchecked(this.result);
/*     */       } else {
/* 839 */         Invoker.invoke(this.result);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(int paramInt, IOException paramIOException)
/*     */     {
/* 846 */       releaseBuffers();
/*     */       
/*     */ 
/* 849 */       if (!WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
/* 850 */         paramIOException = new AsynchronousCloseException();
/*     */       }
/* 852 */       synchronized (this.result) {
/* 853 */         if (this.result.isDone())
/* 854 */           return;
/* 855 */         WindowsAsynchronousSocketChannelImpl.this.enableWriting();
/* 856 */         this.result.setFailure(paramIOException);
/*     */       }
/* 858 */       Invoker.invoke(this.result);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     void timeout()
/*     */     {
/* 866 */       synchronized (this.result) {
/* 867 */         if (this.result.isDone()) {
/* 868 */           return;
/*     */         }
/*     */         
/* 871 */         WindowsAsynchronousSocketChannelImpl.this.enableWriting(true);
/* 872 */         this.result.setFailure(new InterruptedByTimeoutException());
/*     */       }
/*     */       
/*     */ 
/* 876 */       Invoker.invoke(this.result);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
/*     */   {
/* 890 */     PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
/*     */     
/*     */     ByteBuffer[] arrayOfByteBuffer;
/* 893 */     if (paramBoolean) {
/* 894 */       arrayOfByteBuffer = paramArrayOfByteBuffer;
/*     */     } else {
/* 896 */       arrayOfByteBuffer = new ByteBuffer[1];
/* 897 */       arrayOfByteBuffer[0] = paramByteBuffer;
/*     */     }
/* 899 */     final WriteTask localWriteTask = new WriteTask(arrayOfByteBuffer, paramBoolean, localPendingFuture);
/*     */     
/* 901 */     localPendingFuture.setContext(localWriteTask);
/*     */     
/*     */ 
/* 904 */     if (paramLong > 0L) {
/* 905 */       Future localFuture = this.iocp.schedule(new Runnable()
/*     */       {
/* 907 */         public void run() { localWriteTask.timeout(); } }, paramLong, paramTimeUnit);
/*     */       
/*     */ 
/* 910 */       localPendingFuture.setTimeoutTask(localFuture);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 915 */     if (Iocp.supportsThreadAgnosticIo()) {
/* 916 */       localWriteTask.run();
/*     */     } else {
/* 918 */       Invoker.invokeOnThreadInThreadPool(this, localWriteTask);
/*     */     }
/* 920 */     return localPendingFuture;
/*     */   }
/*     */   
/*     */   private static native void initIDs();
/*     */   
/*     */   private static native int connect0(long paramLong1, boolean paramBoolean, InetAddress paramInetAddress, int paramInt, long paramLong2)
/*     */     throws IOException;
/*     */   
/*     */   private static native void updateConnectContext(long paramLong)
/*     */     throws IOException;
/*     */   
/*     */   private static native int read0(long paramLong1, int paramInt, long paramLong2, long paramLong3)
/*     */     throws IOException;
/*     */   
/*     */   private static native int write0(long paramLong1, int paramInt, long paramLong2, long paramLong3)
/*     */     throws IOException;
/*     */   
/*     */   private static native void shutdown0(long paramLong, int paramInt) throws IOException;
/*     */   
/*     */   private static native void closesocket0(long paramLong) throws IOException;
/*     */   
/*     */   static
/*     */   {
/* 943 */     IOUtil.load();
/* 944 */     initIDs();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\WindowsAsynchronousSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
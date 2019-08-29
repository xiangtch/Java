/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.BufferOverflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.AsynchronousFileChannel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.NonReadableChannelException;
/*     */ import java.nio.channels.NonWritableChannelException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import sun.misc.JavaIOFileDescriptorAccess;
/*     */ import sun.misc.SharedSecrets;
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
/*     */ public class WindowsAsynchronousFileChannelImpl
/*     */   extends AsynchronousFileChannelImpl
/*     */   implements Iocp.OverlappedChannel, Groupable
/*     */ {
/*     */   private static final JavaIOFileDescriptorAccess fdAccess;
/*     */   private static final int ERROR_HANDLE_EOF = 38;
/*     */   private static final FileDispatcher nd;
/*     */   private final long handle;
/*     */   private final int completionKey;
/*     */   private final Iocp iocp;
/*     */   private final boolean isDefaultIocp;
/*     */   private final PendingIoCache ioCache;
/*     */   static final int NO_LOCK = -1;
/*     */   static final int LOCKED = 0;
/*     */   
/*     */   private static class DefaultIocpHolder
/*     */   {
/*  53 */     static final Iocp defaultIocp = ;
/*     */     
/*     */     private static Iocp defaultIocp() {
/*  56 */       try { return new Iocp(null, ThreadPool.createDefault()).start();
/*     */       } catch (IOException localIOException) {
/*  58 */         throw new InternalError(localIOException);
/*     */       }
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
/*     */   private WindowsAsynchronousFileChannelImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, Iocp paramIocp, boolean paramBoolean3)
/*     */     throws IOException
/*     */   {
/*  88 */     super(paramFileDescriptor, paramBoolean1, paramBoolean2, paramIocp.executor());
/*  89 */     this.handle = fdAccess.getHandle(paramFileDescriptor);
/*  90 */     this.iocp = paramIocp;
/*  91 */     this.isDefaultIocp = paramBoolean3;
/*  92 */     this.ioCache = new PendingIoCache();
/*  93 */     this.completionKey = paramIocp.associate(this, this.handle);
/*     */   }
/*     */   
/*     */ 
/*     */   public static AsynchronousFileChannel open(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ThreadPool paramThreadPool)
/*     */     throws IOException
/*     */   {
/*     */     Iocp localIocp;
/*     */     
/*     */     boolean bool;
/*     */     
/* 104 */     if (paramThreadPool == null) {
/* 105 */       localIocp = DefaultIocpHolder.defaultIocp;
/* 106 */       bool = true;
/*     */     } else {
/* 108 */       localIocp = new Iocp(null, paramThreadPool).start();
/* 109 */       bool = false;
/*     */     }
/*     */     try {
/* 112 */       return new WindowsAsynchronousFileChannelImpl(paramFileDescriptor, paramBoolean1, paramBoolean2, localIocp, bool);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 116 */       if (!bool)
/* 117 */         localIocp.implClose();
/* 118 */       throw localIOException;
/*     */     }
/*     */   }
/*     */   
/*     */   public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong)
/*     */   {
/* 124 */     return this.ioCache.remove(paramLong);
/*     */   }
/*     */   
/*     */   public void close() throws IOException
/*     */   {
/* 129 */     this.closeLock.writeLock().lock();
/*     */     try {
/* 131 */       if (this.closed)
/* 132 */         return;
/* 133 */       this.closed = true;
/*     */     } finally {
/* 135 */       this.closeLock.writeLock().unlock();
/*     */     }
/*     */     
/*     */ 
/* 139 */     invalidateAllLocks();
/*     */     
/*     */ 
/* 142 */     close0(this.handle);
/*     */     
/*     */ 
/* 145 */     this.ioCache.close();
/*     */     
/*     */ 
/* 148 */     this.iocp.disassociate(this.completionKey);
/*     */     
/*     */ 
/* 151 */     if (!this.isDefaultIocp) {
/* 152 */       this.iocp.detachFromThreadPool();
/*     */     }
/*     */   }
/*     */   
/*     */   public AsynchronousChannelGroupImpl group() {
/* 157 */     return this.iocp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static IOException toIOException(Throwable paramThrowable)
/*     */   {
/* 164 */     if ((paramThrowable instanceof IOException)) {
/* 165 */       if ((paramThrowable instanceof ClosedChannelException))
/* 166 */         paramThrowable = new AsynchronousCloseException();
/* 167 */       return (IOException)paramThrowable;
/*     */     }
/* 169 */     return new IOException(paramThrowable);
/*     */   }
/*     */   
/*     */   public long size() throws IOException
/*     */   {
/*     */     try {
/* 175 */       begin();
/* 176 */       return nd.size(this.fdObj);
/*     */     } finally {
/* 178 */       end();
/*     */     }
/*     */   }
/*     */   
/*     */   public AsynchronousFileChannel truncate(long paramLong) throws IOException
/*     */   {
/* 184 */     if (paramLong < 0L)
/* 185 */       throw new IllegalArgumentException("Negative size");
/* 186 */     if (!this.writing)
/* 187 */       throw new NonWritableChannelException();
/*     */     try {
/* 189 */       begin();
/* 190 */       if (paramLong > nd.size(this.fdObj))
/* 191 */         return this;
/* 192 */       nd.truncate(this.fdObj, paramLong);
/*     */     } finally {
/* 194 */       end();
/*     */     }
/* 196 */     return this;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void force(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 340	sun/nio/ch/WindowsAsynchronousFileChannelImpl:begin	()V
/*     */     //   4: getstatic 296	sun/nio/ch/WindowsAsynchronousFileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
/*     */     //   7: aload_0
/*     */     //   8: getfield 293	sun/nio/ch/WindowsAsynchronousFileChannelImpl:fdObj	Ljava/io/FileDescriptor;
/*     */     //   11: iload_1
/*     */     //   12: invokevirtual 319	sun/nio/ch/FileDispatcher:force	(Ljava/io/FileDescriptor;Z)I
/*     */     //   15: pop
/*     */     //   16: aload_0
/*     */     //   17: invokevirtual 341	sun/nio/ch/WindowsAsynchronousFileChannelImpl:end	()V
/*     */     //   20: goto +10 -> 30
/*     */     //   23: astore_2
/*     */     //   24: aload_0
/*     */     //   25: invokevirtual 341	sun/nio/ch/WindowsAsynchronousFileChannelImpl:end	()V
/*     */     //   28: aload_2
/*     */     //   29: athrow
/*     */     //   30: return
/*     */     // Line number table:
/*     */     //   Java source line #202	-> byte code offset #0
/*     */     //   Java source line #203	-> byte code offset #4
/*     */     //   Java source line #205	-> byte code offset #16
/*     */     //   Java source line #206	-> byte code offset #20
/*     */     //   Java source line #205	-> byte code offset #23
/*     */     //   Java source line #206	-> byte code offset #28
/*     */     //   Java source line #207	-> byte code offset #30
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	31	0	this	WindowsAsynchronousFileChannelImpl
/*     */     //   0	31	1	paramBoolean	boolean
/*     */     //   23	6	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	16	23	finally
/*     */   }
/*     */   
/*     */   private class LockTask<A>
/*     */     implements Runnable, Iocp.ResultHandler
/*     */   {
/*     */     private final long position;
/*     */     private final FileLockImpl fli;
/*     */     private final PendingFuture<FileLock, A> result;
/*     */     
/*     */     LockTask(FileLockImpl paramFileLockImpl, PendingFuture<FileLock, A> paramPendingFuture)
/*     */     {
/* 223 */       this.position = ???;
/* 224 */       this.fli = paramPendingFuture;
/* 225 */       PendingFuture localPendingFuture; this.result = localPendingFuture;
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/* 230 */       long l = 0L;
/* 231 */       int i = 0;
/*     */       try {
/* 233 */         WindowsAsynchronousFileChannelImpl.this.begin();
/*     */         
/*     */ 
/* 236 */         l = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
/*     */         
/*     */ 
/*     */ 
/* 240 */         synchronized (this.result) {
/* 241 */           int j = WindowsAsynchronousFileChannelImpl.lockFile(WindowsAsynchronousFileChannelImpl.this.handle, this.position, this.fli.size(), this.fli.isShared(), l);
/*     */           
/* 243 */           if (j == -2)
/*     */           {
/* 245 */             i = 1;
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
/* 257 */             if ((i == 0) && (l != 0L))
/* 258 */               WindowsAsynchronousFileChannelImpl.this.ioCache.remove(l);
/* 259 */             WindowsAsynchronousFileChannelImpl.this.end();return;
/*     */           }
/* 249 */           this.result.setResult(this.fli);
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/* 254 */         WindowsAsynchronousFileChannelImpl.this.removeFromFileLockTable(this.fli);
/* 255 */         this.result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(localThrowable));
/*     */       } finally {
/* 257 */         if ((i == 0) && (l != 0L))
/* 258 */           WindowsAsynchronousFileChannelImpl.this.ioCache.remove(l);
/* 259 */         WindowsAsynchronousFileChannelImpl.this.end();
/*     */       }
/*     */       
/*     */ 
/* 263 */       Invoker.invoke(this.result);
/*     */     }
/*     */     
/*     */ 
/*     */     public void completed(int paramInt, boolean paramBoolean)
/*     */     {
/* 269 */       this.result.setResult(this.fli);
/* 270 */       if (paramBoolean) {
/* 271 */         Invoker.invokeUnchecked(this.result);
/*     */       } else {
/* 273 */         Invoker.invoke(this.result);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(int paramInt, IOException paramIOException)
/*     */     {
/* 280 */       WindowsAsynchronousFileChannelImpl.this.removeFromFileLockTable(this.fli);
/*     */       
/*     */ 
/* 283 */       if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
/* 284 */         this.result.setFailure(paramIOException);
/*     */       } else {
/* 286 */         this.result.setFailure(new AsynchronousCloseException());
/*     */       }
/* 288 */       Invoker.invoke(this.result);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   <A> Future<FileLock> implLock(long paramLong1, long paramLong2, boolean paramBoolean, A paramA, CompletionHandler<FileLock, ? super A> paramCompletionHandler)
/*     */   {
/* 299 */     if ((paramBoolean) && (!this.reading))
/* 300 */       throw new NonReadableChannelException();
/* 301 */     if ((!paramBoolean) && (!this.writing)) {
/* 302 */       throw new NonWritableChannelException();
/*     */     }
/*     */     
/* 305 */     FileLockImpl localFileLockImpl = addToFileLockTable(paramLong1, paramLong2, paramBoolean);
/* 306 */     if (localFileLockImpl == null) {
/* 307 */       localObject1 = new ClosedChannelException();
/* 308 */       if (paramCompletionHandler == null)
/* 309 */         return CompletedFuture.withFailure((Throwable)localObject1);
/* 310 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)localObject1);
/* 311 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 315 */     Object localObject1 = new PendingFuture(this, paramCompletionHandler, paramA);
/*     */     
/* 317 */     LockTask localLockTask = new LockTask(paramLong1, localFileLockImpl, (PendingFuture)localObject1);
/* 318 */     ((PendingFuture)localObject1).setContext(localLockTask);
/*     */     
/*     */ 
/* 321 */     if (Iocp.supportsThreadAgnosticIo()) {
/* 322 */       localLockTask.run();
/*     */     } else {
/* 324 */       int i = 0;
/*     */       try {
/* 326 */         Invoker.invokeOnThreadInThreadPool(this, localLockTask);
/* 327 */         i = 1;
/*     */       } finally {
/* 329 */         if (i == 0)
/*     */         {
/* 331 */           removeFromFileLockTable(localFileLockImpl);
/*     */         }
/*     */       }
/*     */     }
/* 335 */     return (Future<FileLock>)localObject1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 345 */     if ((paramBoolean) && (!this.reading))
/* 346 */       throw new NonReadableChannelException();
/* 347 */     if ((!paramBoolean) && (!this.writing)) {
/* 348 */       throw new NonWritableChannelException();
/*     */     }
/*     */     
/* 351 */     FileLockImpl localFileLockImpl = addToFileLockTable(paramLong1, paramLong2, paramBoolean);
/* 352 */     if (localFileLockImpl == null) {
/* 353 */       throw new ClosedChannelException();
/*     */     }
/* 355 */     int i = 0;
/*     */     try {
/* 357 */       begin();
/*     */       
/* 359 */       int j = nd.lock(this.fdObj, false, paramLong1, paramLong2, paramBoolean);
/* 360 */       Object localObject1; if (j == -1)
/* 361 */         return null;
/* 362 */       i = 1;
/* 363 */       return localFileLockImpl;
/*     */     } finally {
/* 365 */       if (i == 0)
/* 366 */         removeFromFileLockTable(localFileLockImpl);
/* 367 */       end();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void implRelease(FileLockImpl paramFileLockImpl) throws IOException
/*     */   {
/* 373 */     nd.release(this.fdObj, paramFileLockImpl.position(), paramFileLockImpl.size());
/*     */   }
/*     */   
/*     */ 
/*     */   private class ReadTask<A>
/*     */     implements Runnable, Iocp.ResultHandler
/*     */   {
/*     */     private final ByteBuffer dst;
/*     */     
/*     */     private final int pos;
/*     */     
/*     */     private final int rem;
/*     */     
/*     */     private final long position;
/*     */     
/*     */     private final PendingFuture<Integer, A> result;
/*     */     
/*     */     private volatile ByteBuffer buf;
/*     */     
/*     */     ReadTask(int paramInt1, int paramInt2, long paramLong, PendingFuture<Integer, A> paramPendingFuture)
/*     */     {
/* 394 */       this.dst = paramInt1;
/* 395 */       this.pos = paramInt2;
/* 396 */       this.rem = paramLong;
/* 397 */       this.position = ???;
/* 398 */       PendingFuture localPendingFuture; this.result = localPendingFuture;
/*     */     }
/*     */     
/*     */     void releaseBufferIfSubstituted() {
/* 402 */       if (this.buf != this.dst) {
/* 403 */         Util.releaseTemporaryDirectBuffer(this.buf);
/*     */       }
/*     */     }
/*     */     
/*     */     void updatePosition(int paramInt) {
/* 408 */       if (paramInt > 0) {
/* 409 */         if (this.buf == this.dst) {
/*     */           try {
/* 411 */             this.dst.position(this.pos + paramInt);
/*     */           }
/*     */           catch (IllegalArgumentException localIllegalArgumentException) {}
/*     */         }
/*     */         else
/*     */         {
/* 417 */           this.buf.position(paramInt).flip();
/*     */           try {
/* 419 */             this.dst.put(this.buf);
/*     */           }
/*     */           catch (BufferOverflowException localBufferOverflowException) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 429 */       int i = -1;
/* 430 */       long l1 = 0L;
/*     */       
/*     */       long l2;
/*     */       
/* 434 */       if ((this.dst instanceof DirectBuffer)) {
/* 435 */         this.buf = this.dst;
/* 436 */         l2 = ((DirectBuffer)this.dst).address() + this.pos;
/*     */       } else {
/* 438 */         this.buf = Util.getTemporaryDirectBuffer(this.rem);
/* 439 */         l2 = ((DirectBuffer)this.buf).address();
/*     */       }
/*     */       
/* 442 */       int j = 0;
/*     */       try {
/* 444 */         WindowsAsynchronousFileChannelImpl.this.begin();
/*     */         
/*     */ 
/* 447 */         l1 = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
/*     */         
/*     */ 
/* 450 */         i = WindowsAsynchronousFileChannelImpl.readFile(WindowsAsynchronousFileChannelImpl.this.handle, l2, this.rem, this.position, l1);
/* 451 */         if (i == -2)
/*     */         {
/* 453 */           j = 1;
/* 454 */           return; }
/* 455 */         if (i == -1) {
/* 456 */           this.result.setResult(Integer.valueOf(i));
/*     */         } else {
/* 458 */           throw new InternalError("Unexpected result: " + i);
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/* 463 */         this.result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(localThrowable));
/*     */       } finally {
/* 465 */         if (j == 0)
/*     */         {
/* 467 */           if (l1 != 0L)
/* 468 */             WindowsAsynchronousFileChannelImpl.this.ioCache.remove(l1);
/* 469 */           releaseBufferIfSubstituted();
/*     */         }
/* 471 */         WindowsAsynchronousFileChannelImpl.this.end();
/*     */       }
/*     */       
/*     */ 
/* 475 */       Invoker.invoke(this.result);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void completed(int paramInt, boolean paramBoolean)
/*     */     {
/* 483 */       updatePosition(paramInt);
/*     */       
/*     */ 
/* 486 */       releaseBufferIfSubstituted();
/*     */       
/*     */ 
/* 489 */       this.result.setResult(Integer.valueOf(paramInt));
/* 490 */       if (paramBoolean) {
/* 491 */         Invoker.invokeUnchecked(this.result);
/*     */       } else {
/* 493 */         Invoker.invoke(this.result);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(int paramInt, IOException paramIOException)
/*     */     {
/* 500 */       if (paramInt == 38) {
/* 501 */         completed(-1, false);
/*     */       }
/*     */       else {
/* 504 */         releaseBufferIfSubstituted();
/*     */         
/*     */ 
/* 507 */         if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
/* 508 */           this.result.setFailure(paramIOException);
/*     */         } else {
/* 510 */           this.result.setFailure(new AsynchronousCloseException());
/*     */         }
/* 512 */         Invoker.invoke(this.result);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   <A> Future<Integer> implRead(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
/*     */   {
/* 523 */     if (!this.reading)
/* 524 */       throw new NonReadableChannelException();
/* 525 */     if (paramLong < 0L)
/* 526 */       throw new IllegalArgumentException("Negative position");
/* 527 */     if (paramByteBuffer.isReadOnly()) {
/* 528 */       throw new IllegalArgumentException("Read-only buffer");
/*     */     }
/*     */     
/* 531 */     if (!isOpen()) {
/* 532 */       ClosedChannelException localClosedChannelException = new ClosedChannelException();
/* 533 */       if (paramCompletionHandler == null)
/* 534 */         return CompletedFuture.withFailure(localClosedChannelException);
/* 535 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, localClosedChannelException);
/* 536 */       return null;
/*     */     }
/*     */     
/* 539 */     int i = paramByteBuffer.position();
/* 540 */     int j = paramByteBuffer.limit();
/* 541 */     assert (i <= j);
/* 542 */     int k = i <= j ? j - i : 0;
/*     */     
/*     */ 
/* 545 */     if (k == 0) {
/* 546 */       if (paramCompletionHandler == null)
/* 547 */         return CompletedFuture.withResult(Integer.valueOf(0));
/* 548 */       Invoker.invoke(this, paramCompletionHandler, paramA, Integer.valueOf(0), null);
/* 549 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 553 */     PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
/*     */     
/* 555 */     ReadTask localReadTask = new ReadTask(paramByteBuffer, i, k, paramLong, localPendingFuture);
/* 556 */     localPendingFuture.setContext(localReadTask);
/*     */     
/*     */ 
/* 559 */     if (Iocp.supportsThreadAgnosticIo()) {
/* 560 */       localReadTask.run();
/*     */     } else {
/* 562 */       Invoker.invokeOnThreadInThreadPool(this, localReadTask);
/*     */     }
/* 564 */     return localPendingFuture;
/*     */   }
/*     */   
/*     */ 
/*     */   private class WriteTask<A>
/*     */     implements Runnable, Iocp.ResultHandler
/*     */   {
/*     */     private final ByteBuffer src;
/*     */     
/*     */     private final int pos;
/*     */     
/*     */     private final int rem;
/*     */     
/*     */     private final long position;
/*     */     
/*     */     private final PendingFuture<Integer, A> result;
/*     */     
/*     */     private volatile ByteBuffer buf;
/*     */     
/*     */     WriteTask(int paramInt1, int paramInt2, long paramLong, PendingFuture<Integer, A> paramPendingFuture)
/*     */     {
/* 585 */       this.src = paramInt1;
/* 586 */       this.pos = paramInt2;
/* 587 */       this.rem = paramLong;
/* 588 */       this.position = ???;
/* 589 */       PendingFuture localPendingFuture; this.result = localPendingFuture;
/*     */     }
/*     */     
/*     */     void releaseBufferIfSubstituted() {
/* 593 */       if (this.buf != this.src) {
/* 594 */         Util.releaseTemporaryDirectBuffer(this.buf);
/*     */       }
/*     */     }
/*     */     
/*     */     void updatePosition(int paramInt) {
/* 599 */       if (paramInt > 0) {
/*     */         try {
/* 601 */           this.src.position(this.pos + paramInt);
/*     */         }
/*     */         catch (IllegalArgumentException localIllegalArgumentException) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 610 */       int i = -1;
/* 611 */       long l1 = 0L;
/*     */       
/*     */       long l2;
/*     */       
/* 615 */       if ((this.src instanceof DirectBuffer)) {
/* 616 */         this.buf = this.src;
/* 617 */         l2 = ((DirectBuffer)this.src).address() + this.pos;
/*     */       } else {
/* 619 */         this.buf = Util.getTemporaryDirectBuffer(this.rem);
/* 620 */         this.buf.put(this.src);
/* 621 */         this.buf.flip();
/*     */         
/*     */ 
/* 624 */         this.src.position(this.pos);
/* 625 */         l2 = ((DirectBuffer)this.buf).address();
/*     */       }
/*     */       try
/*     */       {
/* 629 */         WindowsAsynchronousFileChannelImpl.this.begin();
/*     */         
/*     */ 
/* 632 */         l1 = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
/*     */         
/*     */ 
/* 635 */         i = WindowsAsynchronousFileChannelImpl.writeFile(WindowsAsynchronousFileChannelImpl.this.handle, l2, this.rem, this.position, l1);
/* 636 */         if (i == -2)
/*     */         {
/* 638 */           return;
/*     */         }
/* 640 */         throw new InternalError("Unexpected result: " + i);
/*     */ 
/*     */       }
/*     */       catch (Throwable localThrowable)
/*     */       {
/* 645 */         this.result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(localThrowable));
/*     */         
/*     */ 
/* 648 */         if (l1 != 0L)
/* 649 */           WindowsAsynchronousFileChannelImpl.this.ioCache.remove(l1);
/* 650 */         releaseBufferIfSubstituted();
/*     */       }
/*     */       finally {
/* 653 */         WindowsAsynchronousFileChannelImpl.this.end();
/*     */       }
/*     */       
/*     */ 
/* 657 */       Invoker.invoke(this.result);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void completed(int paramInt, boolean paramBoolean)
/*     */     {
/* 665 */       updatePosition(paramInt);
/*     */       
/*     */ 
/* 668 */       releaseBufferIfSubstituted();
/*     */       
/*     */ 
/* 671 */       this.result.setResult(Integer.valueOf(paramInt));
/* 672 */       if (paramBoolean) {
/* 673 */         Invoker.invokeUnchecked(this.result);
/*     */       } else {
/* 675 */         Invoker.invoke(this.result);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(int paramInt, IOException paramIOException)
/*     */     {
/* 682 */       releaseBufferIfSubstituted();
/*     */       
/*     */ 
/* 685 */       if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
/* 686 */         this.result.setFailure(paramIOException);
/*     */       } else {
/* 688 */         this.result.setFailure(new AsynchronousCloseException());
/*     */       }
/* 690 */       Invoker.invoke(this.result);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   <A> Future<Integer> implWrite(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
/*     */   {
/* 699 */     if (!this.writing)
/* 700 */       throw new NonWritableChannelException();
/* 701 */     if (paramLong < 0L) {
/* 702 */       throw new IllegalArgumentException("Negative position");
/*     */     }
/*     */     
/* 705 */     if (!isOpen()) {
/* 706 */       ClosedChannelException localClosedChannelException = new ClosedChannelException();
/* 707 */       if (paramCompletionHandler == null)
/* 708 */         return CompletedFuture.withFailure(localClosedChannelException);
/* 709 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, localClosedChannelException);
/* 710 */       return null;
/*     */     }
/*     */     
/* 713 */     int i = paramByteBuffer.position();
/* 714 */     int j = paramByteBuffer.limit();
/* 715 */     assert (i <= j);
/* 716 */     int k = i <= j ? j - i : 0;
/*     */     
/*     */ 
/* 719 */     if (k == 0) {
/* 720 */       if (paramCompletionHandler == null)
/* 721 */         return CompletedFuture.withResult(Integer.valueOf(0));
/* 722 */       Invoker.invoke(this, paramCompletionHandler, paramA, Integer.valueOf(0), null);
/* 723 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 727 */     PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
/*     */     
/* 729 */     WriteTask localWriteTask = new WriteTask(paramByteBuffer, i, k, paramLong, localPendingFuture);
/* 730 */     localPendingFuture.setContext(localWriteTask);
/*     */     
/*     */ 
/* 733 */     if (Iocp.supportsThreadAgnosticIo()) {
/* 734 */       localWriteTask.run();
/*     */     } else {
/* 736 */       Invoker.invokeOnThreadInThreadPool(this, localWriteTask);
/*     */     }
/* 738 */     return localPendingFuture;
/*     */   }
/*     */   
/*     */   private static native int readFile(long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4)
/*     */     throws IOException;
/*     */   
/*     */   private static native int writeFile(long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4)
/*     */     throws IOException;
/*     */   
/*     */   private static native int lockFile(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4)
/*     */     throws IOException;
/*     */   
/*     */   private static native void close0(long paramLong);
/*     */   
/*     */   static
/*     */   {
/*  46 */     fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
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
/*  64 */     nd = new FileDispatcherImpl();
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
/* 755 */     IOUtil.load();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\WindowsAsynchronousFileChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
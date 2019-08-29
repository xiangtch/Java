/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AlreadyBoundException;
/*     */ import java.nio.channels.AsynchronousSocketChannel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.nio.channels.ConnectionPendingException;
/*     */ import java.nio.channels.NotYetConnectedException;
/*     */ import java.nio.channels.ReadPendingException;
/*     */ import java.nio.channels.WritePendingException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import jdk.net.ExtendedSocketOptions;
/*     */ import sun.net.ExtendedOptionsImpl;
/*     */ import sun.net.NetHooks;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class AsynchronousSocketChannelImpl
/*     */   extends AsynchronousSocketChannel
/*     */   implements Cancellable, Groupable
/*     */ {
/*     */   protected final FileDescriptor fd;
/*  55 */   protected final Object stateLock = new Object();
/*     */   
/*  57 */   protected volatile InetSocketAddress localAddress = null;
/*  58 */   protected volatile InetSocketAddress remoteAddress = null;
/*     */   
/*     */   static final int ST_UNINITIALIZED = -1;
/*     */   
/*     */   static final int ST_UNCONNECTED = 0;
/*     */   static final int ST_PENDING = 1;
/*     */   static final int ST_CONNECTED = 2;
/*  65 */   protected volatile int state = -1;
/*     */   
/*     */ 
/*  68 */   private final Object readLock = new Object();
/*     */   
/*     */   private boolean reading;
/*     */   
/*     */   private boolean readShutdown;
/*     */   private boolean readKilled;
/*  74 */   private final Object writeLock = new Object();
/*     */   
/*     */   private boolean writing;
/*     */   
/*     */   private boolean writeShutdown;
/*     */   private boolean writeKilled;
/*  80 */   private final ReadWriteLock closeLock = new ReentrantReadWriteLock();
/*  81 */   private volatile boolean open = true;
/*     */   
/*     */   private boolean isReuseAddress;
/*     */   
/*     */ 
/*     */   AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
/*     */     throws IOException
/*     */   {
/*  89 */     super(paramAsynchronousChannelGroupImpl.provider());
/*  90 */     this.fd = Net.socket(true);
/*  91 */     this.state = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress)
/*     */     throws IOException
/*     */   {
/* 100 */     super(paramAsynchronousChannelGroupImpl.provider());
/* 101 */     this.fd = paramFileDescriptor;
/* 102 */     this.state = 2;
/* 103 */     this.localAddress = Net.localAddress(paramFileDescriptor);
/* 104 */     this.remoteAddress = paramInetSocketAddress;
/*     */   }
/*     */   
/*     */   public final boolean isOpen()
/*     */   {
/* 109 */     return this.open;
/*     */   }
/*     */   
/*     */ 
/*     */   final void begin()
/*     */     throws IOException
/*     */   {
/* 116 */     this.closeLock.readLock().lock();
/* 117 */     if (!isOpen()) {
/* 118 */       throw new ClosedChannelException();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   final void end()
/*     */   {
/* 125 */     this.closeLock.readLock().unlock();
/*     */   }
/*     */   
/*     */ 
/*     */   abstract void implClose()
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */   public final void close()
/*     */     throws IOException
/*     */   {
/* 136 */     this.closeLock.writeLock().lock();
/*     */     try {
/* 138 */       if (!this.open)
/* 139 */         return;
/* 140 */       this.open = false;
/*     */     } finally {
/* 142 */       this.closeLock.writeLock().unlock();
/*     */     }
/* 144 */     implClose();
/*     */   }
/*     */   
/*     */   final void enableReading(boolean paramBoolean) {
/* 148 */     synchronized (this.readLock) {
/* 149 */       this.reading = false;
/* 150 */       if (paramBoolean)
/* 151 */         this.readKilled = true;
/*     */     }
/*     */   }
/*     */   
/*     */   final void enableReading() {
/* 156 */     enableReading(false);
/*     */   }
/*     */   
/*     */   final void enableWriting(boolean paramBoolean) {
/* 160 */     synchronized (this.writeLock) {
/* 161 */       this.writing = false;
/* 162 */       if (paramBoolean)
/* 163 */         this.writeKilled = true;
/*     */     }
/*     */   }
/*     */   
/*     */   final void enableWriting() {
/* 168 */     enableWriting(false);
/*     */   }
/*     */   
/*     */   final void killReading() {
/* 172 */     synchronized (this.readLock) {
/* 173 */       this.readKilled = true;
/*     */     }
/*     */   }
/*     */   
/*     */   final void killWriting() {
/* 178 */     synchronized (this.writeLock) {
/* 179 */       this.writeKilled = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   final void killConnect()
/*     */   {
/* 186 */     killReading();
/* 187 */     killWriting();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler);
/*     */   
/*     */ 
/*     */ 
/*     */   public final Future<Void> connect(SocketAddress paramSocketAddress)
/*     */   {
/* 199 */     return implConnect(paramSocketAddress, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final <A> void connect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler)
/*     */   {
/* 207 */     if (paramCompletionHandler == null)
/* 208 */       throw new NullPointerException("'handler' is null");
/* 209 */     implConnect(paramSocketAddress, paramA, paramCompletionHandler);
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
/*     */   abstract <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private <V extends Number, A> Future<V> read(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
/*     */   {
/* 232 */     if (!isOpen()) {
/* 233 */       ClosedChannelException localClosedChannelException = new ClosedChannelException();
/* 234 */       if (paramCompletionHandler == null)
/* 235 */         return CompletedFuture.withFailure(localClosedChannelException);
/* 236 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, localClosedChannelException);
/* 237 */       return null;
/*     */     }
/*     */     
/* 240 */     if (this.remoteAddress == null) {
/* 241 */       throw new NotYetConnectedException();
/*     */     }
/* 243 */     int i = (paramBoolean) || (paramByteBuffer.hasRemaining()) ? 1 : 0;
/* 244 */     int j = 0;
/*     */     
/*     */ 
/* 247 */     synchronized (this.readLock) {
/* 248 */       if (this.readKilled)
/* 249 */         throw new IllegalStateException("Reading not allowed due to timeout or cancellation");
/* 250 */       if (this.reading)
/* 251 */         throw new ReadPendingException();
/* 252 */       if (this.readShutdown) {
/* 253 */         j = 1;
/*     */       }
/* 255 */       else if (i != 0) {
/* 256 */         this.reading = true;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 263 */     if ((j != 0) || (i == 0))
/*     */     {
/* 265 */       if (paramBoolean) {
/* 266 */         ??? = j != 0 ? Long.valueOf(-1L) : Long.valueOf(0L);
/*     */       } else {
/* 268 */         ??? = Integer.valueOf(j != 0 ? -1 : 0);
/*     */       }
/* 270 */       if (paramCompletionHandler == null)
/* 271 */         return CompletedFuture.withResult(???);
/* 272 */       Invoker.invoke(this, paramCompletionHandler, paramA, ???, null);
/* 273 */       return null;
/*     */     }
/*     */     
/* 276 */     return implRead(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */   
/*     */   public final Future<Integer> read(ByteBuffer paramByteBuffer)
/*     */   {
/* 281 */     if (paramByteBuffer.isReadOnly())
/* 282 */       throw new IllegalArgumentException("Read-only buffer");
/* 283 */     return read(false, paramByteBuffer, null, 0L, TimeUnit.MILLISECONDS, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final <A> void read(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
/*     */   {
/* 293 */     if (paramCompletionHandler == null)
/* 294 */       throw new NullPointerException("'handler' is null");
/* 295 */     if (paramByteBuffer.isReadOnly())
/* 296 */       throw new IllegalArgumentException("Read-only buffer");
/* 297 */     read(false, paramByteBuffer, null, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final <A> void read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler)
/*     */   {
/* 309 */     if (paramCompletionHandler == null)
/* 310 */       throw new NullPointerException("'handler' is null");
/* 311 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/* 312 */       throw new IndexOutOfBoundsException();
/* 313 */     ByteBuffer[] arrayOfByteBuffer = Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2);
/* 314 */     for (int i = 0; i < arrayOfByteBuffer.length; i++) {
/* 315 */       if (arrayOfByteBuffer[i].isReadOnly())
/* 316 */         throw new IllegalArgumentException("Read-only buffer");
/*     */     }
/* 318 */     read(true, null, arrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
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
/*     */   abstract <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private <V extends Number, A> Future<V> write(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
/*     */   {
/* 341 */     int i = (paramBoolean) || (paramByteBuffer.hasRemaining()) ? 1 : 0;
/*     */     
/* 343 */     int j = 0;
/* 344 */     if (isOpen()) {
/* 345 */       if (this.remoteAddress == null) {
/* 346 */         throw new NotYetConnectedException();
/*     */       }
/* 348 */       synchronized (this.writeLock) {
/* 349 */         if (this.writeKilled)
/* 350 */           throw new IllegalStateException("Writing not allowed due to timeout or cancellation");
/* 351 */         if (this.writing)
/* 352 */           throw new WritePendingException();
/* 353 */         if (this.writeShutdown) {
/* 354 */           j = 1;
/*     */         }
/* 356 */         else if (i != 0) {
/* 357 */           this.writing = true;
/*     */         }
/*     */       }
/*     */     } else {
/* 361 */       j = 1;
/*     */     }
/*     */     
/*     */ 
/* 365 */     if (j != 0) {
/* 366 */       ??? = new ClosedChannelException();
/* 367 */       if (paramCompletionHandler == null)
/* 368 */         return CompletedFuture.withFailure((Throwable)???);
/* 369 */       Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)???);
/* 370 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 374 */     if (i == 0) {
/* 375 */       ??? = paramBoolean ? Long.valueOf(0L) : Integer.valueOf(0);
/* 376 */       if (paramCompletionHandler == null)
/* 377 */         return CompletedFuture.withResult(???);
/* 378 */       Invoker.invoke(this, paramCompletionHandler, paramA, ???, null);
/* 379 */       return null;
/*     */     }
/*     */     
/* 382 */     return implWrite(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */   
/*     */   public final Future<Integer> write(ByteBuffer paramByteBuffer)
/*     */   {
/* 387 */     return write(false, paramByteBuffer, null, 0L, TimeUnit.MILLISECONDS, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final <A> void write(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
/*     */   {
/* 397 */     if (paramCompletionHandler == null)
/* 398 */       throw new NullPointerException("'handler' is null");
/* 399 */     write(false, paramByteBuffer, null, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final <A> void write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler)
/*     */   {
/* 411 */     if (paramCompletionHandler == null)
/* 412 */       throw new NullPointerException("'handler' is null");
/* 413 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/* 414 */       throw new IndexOutOfBoundsException();
/* 415 */     paramArrayOfByteBuffer = Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2);
/* 416 */     write(true, null, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
/*     */   }
/*     */   
/*     */   public final AsynchronousSocketChannel bind(SocketAddress paramSocketAddress)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 424 */       begin();
/* 425 */       synchronized (this.stateLock) {
/* 426 */         if (this.state == 1)
/* 427 */           throw new ConnectionPendingException();
/* 428 */         if (this.localAddress != null) {
/* 429 */           throw new AlreadyBoundException();
/*     */         }
/* 431 */         InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/* 432 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 433 */         if (localSecurityManager != null) {
/* 434 */           localSecurityManager.checkListen(localInetSocketAddress.getPort());
/*     */         }
/* 436 */         NetHooks.beforeTcpBind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 437 */         Net.bind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 438 */         this.localAddress = Net.localAddress(this.fd);
/*     */       }
/*     */     } finally {
/* 441 */       end();
/*     */     }
/* 443 */     return this;
/*     */   }
/*     */   
/*     */   public final SocketAddress getLocalAddress() throws IOException
/*     */   {
/* 448 */     if (!isOpen())
/* 449 */       throw new ClosedChannelException();
/* 450 */     return Net.getRevealedLocalAddress(this.localAddress);
/*     */   }
/*     */   
/*     */ 
/*     */   public final <T> AsynchronousSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
/*     */     throws IOException
/*     */   {
/* 457 */     if (paramSocketOption == null)
/* 458 */       throw new NullPointerException();
/* 459 */     if (!supportedOptions().contains(paramSocketOption)) {
/* 460 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     }
/*     */     try {
/* 463 */       begin();
/* 464 */       if (this.writeShutdown)
/* 465 */         throw new IOException("Connection has been shutdown for writing");
/* 466 */       if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && 
/* 467 */         (Net.useExclusiveBind()))
/*     */       {
/*     */ 
/* 470 */         this.isReuseAddress = ((Boolean)paramT).booleanValue();
/*     */       } else {
/* 472 */         Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
/*     */       }
/* 474 */       return this;
/*     */     } finally {
/* 476 */       end();
/*     */     }
/*     */   }
/*     */   
/*     */   public final <T> T getOption(SocketOption<T> paramSocketOption)
/*     */     throws IOException
/*     */   {
/* 483 */     if (paramSocketOption == null)
/* 484 */       throw new NullPointerException();
/* 485 */     if (!supportedOptions().contains(paramSocketOption)) {
/* 486 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     }
/*     */     try {
/* 489 */       begin();
/* 490 */       Object localObject1; if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && 
/* 491 */         (Net.useExclusiveBind()))
/*     */       {
/*     */ 
/* 494 */         return Boolean.valueOf(this.isReuseAddress);
/*     */       }
/* 496 */       return (T)Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
/*     */     } finally {
/* 498 */       end();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class DefaultOptionsHolder {
/* 503 */     static final Set<SocketOption<?>> defaultOptions = ;
/*     */     
/*     */     private static Set<SocketOption<?>> defaultOptions() {
/* 506 */       HashSet localHashSet = new HashSet(5);
/* 507 */       localHashSet.add(StandardSocketOptions.SO_SNDBUF);
/* 508 */       localHashSet.add(StandardSocketOptions.SO_RCVBUF);
/* 509 */       localHashSet.add(StandardSocketOptions.SO_KEEPALIVE);
/* 510 */       localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
/* 511 */       localHashSet.add(StandardSocketOptions.TCP_NODELAY);
/* 512 */       if (ExtendedOptionsImpl.flowSupported()) {
/* 513 */         localHashSet.add(ExtendedSocketOptions.SO_FLOW_SLA);
/*     */       }
/* 515 */       return Collections.unmodifiableSet(localHashSet);
/*     */     }
/*     */   }
/*     */   
/*     */   public final Set<SocketOption<?>> supportedOptions()
/*     */   {
/* 521 */     return DefaultOptionsHolder.defaultOptions;
/*     */   }
/*     */   
/*     */   public final SocketAddress getRemoteAddress() throws IOException
/*     */   {
/* 526 */     if (!isOpen())
/* 527 */       throw new ClosedChannelException();
/* 528 */     return this.remoteAddress;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public final AsynchronousSocketChannel shutdownInput()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 413	sun/nio/ch/AsynchronousSocketChannelImpl:begin	()V
/*     */     //   4: aload_0
/*     */     //   5: getfield 372	sun/nio/ch/AsynchronousSocketChannelImpl:remoteAddress	Ljava/net/InetSocketAddress;
/*     */     //   8: ifnonnull +11 -> 19
/*     */     //   11: new 202	java/nio/channels/NotYetConnectedException
/*     */     //   14: dup
/*     */     //   15: invokespecial 407	java/nio/channels/NotYetConnectedException:<init>	()V
/*     */     //   18: athrow
/*     */     //   19: aload_0
/*     */     //   20: getfield 368	sun/nio/ch/AsynchronousSocketChannelImpl:readLock	Ljava/lang/Object;
/*     */     //   23: dup
/*     */     //   24: astore_1
/*     */     //   25: monitorenter
/*     */     //   26: aload_0
/*     */     //   27: getfield 362	sun/nio/ch/AsynchronousSocketChannelImpl:readShutdown	Z
/*     */     //   30: ifne +16 -> 46
/*     */     //   33: aload_0
/*     */     //   34: getfield 367	sun/nio/ch/AsynchronousSocketChannelImpl:fd	Ljava/io/FileDescriptor;
/*     */     //   37: iconst_0
/*     */     //   38: invokestatic 433	sun/nio/ch/Net:shutdown	(Ljava/io/FileDescriptor;I)V
/*     */     //   41: aload_0
/*     */     //   42: iconst_1
/*     */     //   43: putfield 362	sun/nio/ch/AsynchronousSocketChannelImpl:readShutdown	Z
/*     */     //   46: aload_1
/*     */     //   47: monitorexit
/*     */     //   48: goto +8 -> 56
/*     */     //   51: astore_2
/*     */     //   52: aload_1
/*     */     //   53: monitorexit
/*     */     //   54: aload_2
/*     */     //   55: athrow
/*     */     //   56: aload_0
/*     */     //   57: invokevirtual 414	sun/nio/ch/AsynchronousSocketChannelImpl:end	()V
/*     */     //   60: goto +10 -> 70
/*     */     //   63: astore_3
/*     */     //   64: aload_0
/*     */     //   65: invokevirtual 414	sun/nio/ch/AsynchronousSocketChannelImpl:end	()V
/*     */     //   68: aload_3
/*     */     //   69: athrow
/*     */     //   70: aload_0
/*     */     //   71: areturn
/*     */     // Line number table:
/*     */     //   Java source line #534	-> byte code offset #0
/*     */     //   Java source line #535	-> byte code offset #4
/*     */     //   Java source line #536	-> byte code offset #11
/*     */     //   Java source line #537	-> byte code offset #19
/*     */     //   Java source line #538	-> byte code offset #26
/*     */     //   Java source line #539	-> byte code offset #33
/*     */     //   Java source line #540	-> byte code offset #41
/*     */     //   Java source line #542	-> byte code offset #46
/*     */     //   Java source line #544	-> byte code offset #56
/*     */     //   Java source line #545	-> byte code offset #60
/*     */     //   Java source line #544	-> byte code offset #63
/*     */     //   Java source line #545	-> byte code offset #68
/*     */     //   Java source line #546	-> byte code offset #70
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	72	0	this	AsynchronousSocketChannelImpl
/*     */     //   51	4	2	localObject1	Object
/*     */     //   63	6	3	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   26	48	51	finally
/*     */     //   51	54	51	finally
/*     */     //   0	56	63	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public final AsynchronousSocketChannel shutdownOutput()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 413	sun/nio/ch/AsynchronousSocketChannelImpl:begin	()V
/*     */     //   4: aload_0
/*     */     //   5: getfield 372	sun/nio/ch/AsynchronousSocketChannelImpl:remoteAddress	Ljava/net/InetSocketAddress;
/*     */     //   8: ifnonnull +11 -> 19
/*     */     //   11: new 202	java/nio/channels/NotYetConnectedException
/*     */     //   14: dup
/*     */     //   15: invokespecial 407	java/nio/channels/NotYetConnectedException:<init>	()V
/*     */     //   18: athrow
/*     */     //   19: aload_0
/*     */     //   20: getfield 370	sun/nio/ch/AsynchronousSocketChannelImpl:writeLock	Ljava/lang/Object;
/*     */     //   23: dup
/*     */     //   24: astore_1
/*     */     //   25: monitorenter
/*     */     //   26: aload_0
/*     */     //   27: getfield 365	sun/nio/ch/AsynchronousSocketChannelImpl:writeShutdown	Z
/*     */     //   30: ifne +16 -> 46
/*     */     //   33: aload_0
/*     */     //   34: getfield 367	sun/nio/ch/AsynchronousSocketChannelImpl:fd	Ljava/io/FileDescriptor;
/*     */     //   37: iconst_1
/*     */     //   38: invokestatic 433	sun/nio/ch/Net:shutdown	(Ljava/io/FileDescriptor;I)V
/*     */     //   41: aload_0
/*     */     //   42: iconst_1
/*     */     //   43: putfield 365	sun/nio/ch/AsynchronousSocketChannelImpl:writeShutdown	Z
/*     */     //   46: aload_1
/*     */     //   47: monitorexit
/*     */     //   48: goto +8 -> 56
/*     */     //   51: astore_2
/*     */     //   52: aload_1
/*     */     //   53: monitorexit
/*     */     //   54: aload_2
/*     */     //   55: athrow
/*     */     //   56: aload_0
/*     */     //   57: invokevirtual 414	sun/nio/ch/AsynchronousSocketChannelImpl:end	()V
/*     */     //   60: goto +10 -> 70
/*     */     //   63: astore_3
/*     */     //   64: aload_0
/*     */     //   65: invokevirtual 414	sun/nio/ch/AsynchronousSocketChannelImpl:end	()V
/*     */     //   68: aload_3
/*     */     //   69: athrow
/*     */     //   70: aload_0
/*     */     //   71: areturn
/*     */     // Line number table:
/*     */     //   Java source line #552	-> byte code offset #0
/*     */     //   Java source line #553	-> byte code offset #4
/*     */     //   Java source line #554	-> byte code offset #11
/*     */     //   Java source line #555	-> byte code offset #19
/*     */     //   Java source line #556	-> byte code offset #26
/*     */     //   Java source line #557	-> byte code offset #33
/*     */     //   Java source line #558	-> byte code offset #41
/*     */     //   Java source line #560	-> byte code offset #46
/*     */     //   Java source line #562	-> byte code offset #56
/*     */     //   Java source line #563	-> byte code offset #60
/*     */     //   Java source line #562	-> byte code offset #63
/*     */     //   Java source line #563	-> byte code offset #68
/*     */     //   Java source line #564	-> byte code offset #70
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	72	0	this	AsynchronousSocketChannelImpl
/*     */     //   51	4	2	localObject1	Object
/*     */     //   63	6	3	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   26	48	51	finally
/*     */     //   51	54	51	finally
/*     */     //   0	56	63	finally
/*     */   }
/*     */   
/*     */   public final String toString()
/*     */   {
/* 569 */     StringBuilder localStringBuilder = new StringBuilder();
/* 570 */     localStringBuilder.append(getClass().getName());
/* 571 */     localStringBuilder.append('[');
/* 572 */     synchronized (this.stateLock) {
/* 573 */       if (!isOpen()) {
/* 574 */         localStringBuilder.append("closed");
/*     */       } else {
/* 576 */         switch (this.state) {
/*     */         case 0: 
/* 578 */           localStringBuilder.append("unconnected");
/* 579 */           break;
/*     */         case 1: 
/* 581 */           localStringBuilder.append("connection-pending");
/* 582 */           break;
/*     */         case 2: 
/* 584 */           localStringBuilder.append("connected");
/* 585 */           if (this.readShutdown)
/* 586 */             localStringBuilder.append(" ishut");
/* 587 */           if (this.writeShutdown)
/* 588 */             localStringBuilder.append(" oshut");
/*     */           break;
/*     */         }
/* 591 */         if (this.localAddress != null) {
/* 592 */           localStringBuilder.append(" local=");
/* 593 */           localStringBuilder.append(
/* 594 */             Net.getRevealedLocalAddressAsString(this.localAddress));
/*     */         }
/* 596 */         if (this.remoteAddress != null) {
/* 597 */           localStringBuilder.append(" remote=");
/* 598 */           localStringBuilder.append(this.remoteAddress.toString());
/*     */         }
/*     */       }
/*     */     }
/* 602 */     localStringBuilder.append(']');
/* 603 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\AsynchronousSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
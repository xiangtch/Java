/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ShutdownChannelGroupException;
/*     */ import java.nio.channels.spi.AsynchronousChannelProvider;
/*     */ import java.security.AccessController;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import sun.misc.Unsafe;
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
/*     */ class Iocp
/*     */   extends AsynchronousChannelGroupImpl
/*     */ {
/*  47 */   private static final Unsafe unsafe = ;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  52 */   private final ReadWriteLock keyToChannelLock = new ReentrantReadWriteLock();
/*  53 */   private final Map<Integer, OverlappedChannel> keyToChannel = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  66 */   private final Set<Long> staleIoSet = new HashSet();
/*     */   
/*     */   Iocp(AsynchronousChannelProvider paramAsynchronousChannelProvider, ThreadPool paramThreadPool)
/*     */     throws IOException
/*     */   {
/*  71 */     super(paramAsynchronousChannelProvider, paramThreadPool);
/*     */     
/*  73 */     this.port = createIoCompletionPort(-1L, 0L, 0, fixedThreadCount());
/*  74 */     this.nextCompletionKey = 1;
/*     */   }
/*     */   
/*     */   Iocp start() {
/*  78 */     startThreads(new EventHandlerTask(null));
/*  79 */     return this;
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
/*     */   static boolean supportsThreadAgnosticIo()
/*     */   {
/*  97 */     return supportsThreadAgnosticIo;
/*     */   }
/*     */   
/*     */   void implClose()
/*     */   {
/* 102 */     synchronized (this) {
/* 103 */       if (this.closed)
/* 104 */         return;
/* 105 */       this.closed = true;
/*     */     }
/* 107 */     close0(this.port);
/* 108 */     synchronized (this.staleIoSet) {
/* 109 */       for (Long localLong : this.staleIoSet) {
/* 110 */         unsafe.freeMemory(localLong.longValue());
/*     */       }
/* 112 */       this.staleIoSet.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   boolean isEmpty()
/*     */   {
/* 118 */     this.keyToChannelLock.writeLock().lock();
/*     */     try {
/* 120 */       return this.keyToChannel.isEmpty();
/*     */     } finally {
/* 122 */       this.keyToChannelLock.writeLock().unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   final Object attachForeignChannel(final Channel paramChannel, FileDescriptor paramFileDescriptor)
/*     */     throws IOException
/*     */   {
/* 130 */     int i = associate(new OverlappedChannel() {
/*     */       public <V, A> PendingFuture<V, A> getByOverlapped(long paramAnonymousLong) {
/* 132 */         return null;
/*     */       }
/*     */       
/* 135 */       public void close() throws IOException { paramChannel.close(); } }, 0L);
/*     */     
/*     */ 
/* 138 */     return Integer.valueOf(i);
/*     */   }
/*     */   
/*     */   final void detachForeignChannel(Object paramObject)
/*     */   {
/* 143 */     disassociate(((Integer)paramObject).intValue());
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
/*     */   void closeAllChannels()
/*     */   {
/* 157 */     OverlappedChannel[] arrayOfOverlappedChannel = new OverlappedChannel[32];
/*     */     int i;
/*     */     do
/*     */     {
/* 161 */       this.keyToChannelLock.writeLock().lock();
/* 162 */       i = 0;
/*     */       try {
/* 164 */         for (Integer localInteger : this.keyToChannel.keySet()) {
/* 165 */           arrayOfOverlappedChannel[(i++)] = ((OverlappedChannel)this.keyToChannel.get(localInteger));
/* 166 */           if (i >= 32)
/*     */             break;
/*     */         }
/*     */       } finally {
/* 170 */         this.keyToChannelLock.writeLock().unlock();
/*     */       }
/*     */       
/*     */ 
/* 174 */       for (int j = 0; j < i; j++) {
/*     */         try {
/* 176 */           arrayOfOverlappedChannel[j].close();
/*     */         } catch (IOException localIOException) {}
/*     */       }
/* 179 */     } while (i > 0);
/*     */   }
/*     */   
/*     */   private void wakeup() {
/*     */     try {
/* 184 */       postQueuedCompletionStatus(this.port, 0);
/*     */     }
/*     */     catch (IOException localIOException) {
/* 187 */       throw new AssertionError(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   void executeOnHandlerTask(Runnable paramRunnable)
/*     */   {
/* 193 */     synchronized (this) {
/* 194 */       if (this.closed)
/* 195 */         throw new RejectedExecutionException();
/* 196 */       offerTask(paramRunnable);
/* 197 */       wakeup();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void shutdownHandlerTasks()
/*     */   {
/* 205 */     int i = threadCount();
/* 206 */     while (i-- > 0) {
/* 207 */       wakeup();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   int associate(OverlappedChannel paramOverlappedChannel, long paramLong)
/*     */     throws IOException
/*     */   {
/* 215 */     this.keyToChannelLock.writeLock().lock();
/*     */     
/*     */     int i;
/*     */     try
/*     */     {
/* 220 */       if (isShutdown()) {
/* 221 */         throw new ShutdownChannelGroupException();
/*     */       }
/*     */       do
/*     */       {
/* 225 */         i = this.nextCompletionKey++;
/* 226 */       } while ((i == 0) || (this.keyToChannel.containsKey(Integer.valueOf(i))));
/*     */       
/*     */ 
/* 229 */       if (paramLong != 0L) {
/* 230 */         createIoCompletionPort(paramLong, this.port, i, 0);
/*     */       }
/*     */       
/*     */ 
/* 234 */       this.keyToChannel.put(Integer.valueOf(i), paramOverlappedChannel);
/*     */     } finally {
/* 236 */       this.keyToChannelLock.writeLock().unlock();
/*     */     }
/* 238 */     return i;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   void disassociate(int paramInt)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: iconst_0
/*     */     //   1: istore_2
/*     */     //   2: aload_0
/*     */     //   3: getfield 264	sun/nio/ch/Iocp:keyToChannelLock	Ljava/util/concurrent/locks/ReadWriteLock;
/*     */     //   6: invokeinterface 322 1 0
/*     */     //   11: invokeinterface 320 1 0
/*     */     //   16: aload_0
/*     */     //   17: getfield 262	sun/nio/ch/Iocp:keyToChannel	Ljava/util/Map;
/*     */     //   20: iload_1
/*     */     //   21: invokestatic 269	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*     */     //   24: invokeinterface 314 2 0
/*     */     //   29: pop
/*     */     //   30: aload_0
/*     */     //   31: getfield 262	sun/nio/ch/Iocp:keyToChannel	Ljava/util/Map;
/*     */     //   34: invokeinterface 310 1 0
/*     */     //   39: ifeq +5 -> 44
/*     */     //   42: iconst_1
/*     */     //   43: istore_2
/*     */     //   44: aload_0
/*     */     //   45: getfield 264	sun/nio/ch/Iocp:keyToChannelLock	Ljava/util/concurrent/locks/ReadWriteLock;
/*     */     //   48: invokeinterface 322 1 0
/*     */     //   53: invokeinterface 321 1 0
/*     */     //   58: goto +20 -> 78
/*     */     //   61: astore_3
/*     */     //   62: aload_0
/*     */     //   63: getfield 264	sun/nio/ch/Iocp:keyToChannelLock	Ljava/util/concurrent/locks/ReadWriteLock;
/*     */     //   66: invokeinterface 322 1 0
/*     */     //   71: invokeinterface 321 1 0
/*     */     //   76: aload_3
/*     */     //   77: athrow
/*     */     //   78: iload_2
/*     */     //   79: ifeq +18 -> 97
/*     */     //   82: aload_0
/*     */     //   83: invokevirtual 293	sun/nio/ch/Iocp:isShutdown	()Z
/*     */     //   86: ifeq +11 -> 97
/*     */     //   89: aload_0
/*     */     //   90: invokevirtual 291	sun/nio/ch/Iocp:shutdownNow	()V
/*     */     //   93: goto +4 -> 97
/*     */     //   96: astore_3
/*     */     //   97: return
/*     */     // Line number table:
/*     */     //   Java source line #245	-> byte code offset #0
/*     */     //   Java source line #247	-> byte code offset #2
/*     */     //   Java source line #249	-> byte code offset #16
/*     */     //   Java source line #252	-> byte code offset #30
/*     */     //   Java source line #253	-> byte code offset #42
/*     */     //   Java source line #256	-> byte code offset #44
/*     */     //   Java source line #257	-> byte code offset #58
/*     */     //   Java source line #256	-> byte code offset #61
/*     */     //   Java source line #257	-> byte code offset #76
/*     */     //   Java source line #260	-> byte code offset #78
/*     */     //   Java source line #262	-> byte code offset #89
/*     */     //   Java source line #263	-> byte code offset #93
/*     */     //   Java source line #265	-> byte code offset #97
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	98	0	this	Iocp
/*     */     //   0	98	1	paramInt	int
/*     */     //   1	78	2	i	int
/*     */     //   61	16	3	localObject	Object
/*     */     //   96	1	3	localIOException	IOException
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   16	44	61	finally
/*     */     //   89	93	96	java/io/IOException
/*     */   }
/*     */   
/*     */   void makeStale(Long paramLong)
/*     */   {
/* 272 */     synchronized (this.staleIoSet) {
/* 273 */       this.staleIoSet.add(paramLong);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void checkIfStale(long paramLong)
/*     */   {
/* 281 */     synchronized (this.staleIoSet) {
/* 282 */       boolean bool = this.staleIoSet.remove(Long.valueOf(paramLong));
/* 283 */       if (bool) {
/* 284 */         unsafe.freeMemory(paramLong);
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
/*     */   private static IOException translateErrorToIOException(int paramInt)
/*     */   {
/* 306 */     String str = getErrorMessage(paramInt);
/* 307 */     if (str == null)
/* 308 */       str = "Unknown error: 0x0" + Integer.toHexString(paramInt);
/* 309 */     return new IOException(str);
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
/*     */   private static native void initIDs();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static native long createIoCompletionPort(long paramLong1, long paramLong2, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static native void close0(long paramLong);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static native void getQueuedCompletionStatus(long paramLong, CompletionStatus paramCompletionStatus)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static native void postQueuedCompletionStatus(long paramLong, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static native String getErrorMessage(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class CompletionStatus
/*     */   {
/*     */     private int error;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int bytesTransferred;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int completionKey;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private long overlapped;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 422 */     int error() { return this.error; }
/* 423 */     int bytesTransferred() { return this.bytesTransferred; }
/* 424 */     int completionKey() { return this.completionKey; }
/* 425 */     long overlapped() { return this.overlapped; }
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
/*     */   static
/*     */   {
/* 446 */     IOUtil.load();
/* 447 */     initIDs();
/*     */     
/*     */ 
/* 450 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.version"));
/*     */     
/* 452 */     String[] arrayOfString = str.split("\\."); }
/* 453 */   private static final boolean supportsThreadAgnosticIo = Integer.parseInt(arrayOfString[0]) >= 6;
/*     */   private static final long INVALID_HANDLE_VALUE = -1L;
/*     */   private int nextCompletionKey;
/*     */   private final long port;
/*     */   private boolean closed;
/*     */   
/*     */   private class EventHandlerTask
/*     */     implements Runnable
/*     */   {
/*     */     private EventHandlerTask() {}
/*     */     
/*     */     /* Error */
/*     */     public void run()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: invokestatic 131	sun/nio/ch/Invoker:getGroupAndInvokeCount	()Lsun/nio/ch/Invoker$GroupAndInvokeCount;
/*     */       //   3: astore_1
/*     */       //   4: aload_1
/*     */       //   5: ifnull +7 -> 12
/*     */       //   8: iconst_1
/*     */       //   9: goto +4 -> 13
/*     */       //   12: iconst_0
/*     */       //   13: istore_2
/*     */       //   14: new 73	sun/nio/ch/Iocp$CompletionStatus
/*     */       //   17: dup
/*     */       //   18: aconst_null
/*     */       //   19: invokespecial 147	sun/nio/ch/Iocp$CompletionStatus:<init>	(Lsun/nio/ch/Iocp$1;)V
/*     */       //   22: astore_3
/*     */       //   23: iconst_0
/*     */       //   24: istore 4
/*     */       //   26: aload_1
/*     */       //   27: ifnull +7 -> 34
/*     */       //   30: aload_1
/*     */       //   31: invokevirtual 132	sun/nio/ch/Invoker$GroupAndInvokeCount:resetInvokeCount	()V
/*     */       //   34: iconst_0
/*     */       //   35: istore 4
/*     */       //   37: aload_0
/*     */       //   38: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   41: invokestatic 138	sun/nio/ch/Iocp:access$200	(Lsun/nio/ch/Iocp;)J
/*     */       //   44: aload_3
/*     */       //   45: invokestatic 140	sun/nio/ch/Iocp:access$300	(JLsun/nio/ch/Iocp$CompletionStatus;)V
/*     */       //   48: goto +45 -> 93
/*     */       //   51: astore 5
/*     */       //   53: aload 5
/*     */       //   55: invokevirtual 128	java/io/IOException:printStackTrace	()V
/*     */       //   58: aload_0
/*     */       //   59: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   62: aload_0
/*     */       //   63: iload 4
/*     */       //   65: invokevirtual 137	sun/nio/ch/Iocp:threadExit	(Ljava/lang/Runnable;Z)I
/*     */       //   68: istore 6
/*     */       //   70: iload 6
/*     */       //   72: ifne +20 -> 92
/*     */       //   75: aload_0
/*     */       //   76: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   79: invokevirtual 134	sun/nio/ch/Iocp:isShutdown	()Z
/*     */       //   82: ifeq +10 -> 92
/*     */       //   85: aload_0
/*     */       //   86: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   89: invokevirtual 133	sun/nio/ch/Iocp:implClose	()V
/*     */       //   92: return
/*     */       //   93: aload_3
/*     */       //   94: invokevirtual 144	sun/nio/ch/Iocp$CompletionStatus:completionKey	()I
/*     */       //   97: ifne +74 -> 171
/*     */       //   100: aload_3
/*     */       //   101: invokevirtual 146	sun/nio/ch/Iocp$CompletionStatus:overlapped	()J
/*     */       //   104: lconst_0
/*     */       //   105: lcmp
/*     */       //   106: ifne +65 -> 171
/*     */       //   109: aload_0
/*     */       //   110: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   113: invokevirtual 136	sun/nio/ch/Iocp:pollTask	()Ljava/lang/Runnable;
/*     */       //   116: astore 5
/*     */       //   118: aload 5
/*     */       //   120: ifnonnull +38 -> 158
/*     */       //   123: aload_0
/*     */       //   124: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   127: aload_0
/*     */       //   128: iload 4
/*     */       //   130: invokevirtual 137	sun/nio/ch/Iocp:threadExit	(Ljava/lang/Runnable;Z)I
/*     */       //   133: istore 6
/*     */       //   135: iload 6
/*     */       //   137: ifne +20 -> 157
/*     */       //   140: aload_0
/*     */       //   141: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   144: invokevirtual 134	sun/nio/ch/Iocp:isShutdown	()Z
/*     */       //   147: ifeq +10 -> 157
/*     */       //   150: aload_0
/*     */       //   151: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   154: invokevirtual 133	sun/nio/ch/Iocp:implClose	()V
/*     */       //   157: return
/*     */       //   158: iconst_1
/*     */       //   159: istore 4
/*     */       //   161: aload 5
/*     */       //   163: invokeinterface 151 1 0
/*     */       //   168: goto -142 -> 26
/*     */       //   171: aconst_null
/*     */       //   172: astore 5
/*     */       //   174: aload_0
/*     */       //   175: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   178: invokestatic 142	sun/nio/ch/Iocp:access$400	(Lsun/nio/ch/Iocp;)Ljava/util/concurrent/locks/ReadWriteLock;
/*     */       //   181: invokeinterface 155 1 0
/*     */       //   186: invokeinterface 153 1 0
/*     */       //   191: aload_0
/*     */       //   192: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   195: invokestatic 141	sun/nio/ch/Iocp:access$500	(Lsun/nio/ch/Iocp;)Ljava/util/Map;
/*     */       //   198: aload_3
/*     */       //   199: invokevirtual 144	sun/nio/ch/Iocp$CompletionStatus:completionKey	()I
/*     */       //   202: invokestatic 129	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*     */       //   205: invokeinterface 152 2 0
/*     */       //   210: checkcast 75	sun/nio/ch/Iocp$OverlappedChannel
/*     */       //   213: astore 5
/*     */       //   215: aload 5
/*     */       //   217: ifnonnull +34 -> 251
/*     */       //   220: aload_0
/*     */       //   221: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   224: aload_3
/*     */       //   225: invokevirtual 146	sun/nio/ch/Iocp$CompletionStatus:overlapped	()J
/*     */       //   228: invokestatic 139	sun/nio/ch/Iocp:access$600	(Lsun/nio/ch/Iocp;J)V
/*     */       //   231: aload_0
/*     */       //   232: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   235: invokestatic 142	sun/nio/ch/Iocp:access$400	(Lsun/nio/ch/Iocp;)Ljava/util/concurrent/locks/ReadWriteLock;
/*     */       //   238: invokeinterface 155 1 0
/*     */       //   243: invokeinterface 154 1 0
/*     */       //   248: goto -222 -> 26
/*     */       //   251: aload_0
/*     */       //   252: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   255: invokestatic 142	sun/nio/ch/Iocp:access$400	(Lsun/nio/ch/Iocp;)Ljava/util/concurrent/locks/ReadWriteLock;
/*     */       //   258: invokeinterface 155 1 0
/*     */       //   263: invokeinterface 154 1 0
/*     */       //   268: goto +25 -> 293
/*     */       //   271: astore 7
/*     */       //   273: aload_0
/*     */       //   274: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   277: invokestatic 142	sun/nio/ch/Iocp:access$400	(Lsun/nio/ch/Iocp;)Ljava/util/concurrent/locks/ReadWriteLock;
/*     */       //   280: invokeinterface 155 1 0
/*     */       //   285: invokeinterface 154 1 0
/*     */       //   290: aload 7
/*     */       //   292: athrow
/*     */       //   293: aload 5
/*     */       //   295: aload_3
/*     */       //   296: invokevirtual 146	sun/nio/ch/Iocp$CompletionStatus:overlapped	()J
/*     */       //   299: invokeinterface 156 3 0
/*     */       //   304: astore 6
/*     */       //   306: aload 6
/*     */       //   308: ifnonnull +17 -> 325
/*     */       //   311: aload_0
/*     */       //   312: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   315: aload_3
/*     */       //   316: invokevirtual 146	sun/nio/ch/Iocp$CompletionStatus:overlapped	()J
/*     */       //   319: invokestatic 139	sun/nio/ch/Iocp:access$600	(Lsun/nio/ch/Iocp;J)V
/*     */       //   322: goto -296 -> 26
/*     */       //   325: aload 6
/*     */       //   327: dup
/*     */       //   328: astore 7
/*     */       //   330: monitorenter
/*     */       //   331: aload 6
/*     */       //   333: invokevirtual 149	sun/nio/ch/PendingFuture:isDone	()Z
/*     */       //   336: ifeq +9 -> 345
/*     */       //   339: aload 7
/*     */       //   341: monitorexit
/*     */       //   342: goto -316 -> 26
/*     */       //   345: aload 7
/*     */       //   347: monitorexit
/*     */       //   348: goto +11 -> 359
/*     */       //   351: astore 8
/*     */       //   353: aload 7
/*     */       //   355: monitorexit
/*     */       //   356: aload 8
/*     */       //   358: athrow
/*     */       //   359: aload_3
/*     */       //   360: invokevirtual 145	sun/nio/ch/Iocp$CompletionStatus:error	()I
/*     */       //   363: istore 7
/*     */       //   365: aload 6
/*     */       //   367: invokevirtual 150	sun/nio/ch/PendingFuture:getContext	()Ljava/lang/Object;
/*     */       //   370: checkcast 76	sun/nio/ch/Iocp$ResultHandler
/*     */       //   373: astore 8
/*     */       //   375: iconst_1
/*     */       //   376: istore 4
/*     */       //   378: iload 7
/*     */       //   380: ifne +18 -> 398
/*     */       //   383: aload 8
/*     */       //   385: aload_3
/*     */       //   386: invokevirtual 143	sun/nio/ch/Iocp$CompletionStatus:bytesTransferred	()I
/*     */       //   389: iload_2
/*     */       //   390: invokeinterface 157 3 0
/*     */       //   395: goto +17 -> 412
/*     */       //   398: aload 8
/*     */       //   400: iload 7
/*     */       //   402: iload 7
/*     */       //   404: invokestatic 135	sun/nio/ch/Iocp:access$700	(I)Ljava/io/IOException;
/*     */       //   407: invokeinterface 158 3 0
/*     */       //   412: goto -386 -> 26
/*     */       //   415: astore 9
/*     */       //   417: aload_0
/*     */       //   418: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   421: aload_0
/*     */       //   422: iload 4
/*     */       //   424: invokevirtual 137	sun/nio/ch/Iocp:threadExit	(Ljava/lang/Runnable;Z)I
/*     */       //   427: istore 10
/*     */       //   429: iload 10
/*     */       //   431: ifne +20 -> 451
/*     */       //   434: aload_0
/*     */       //   435: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   438: invokevirtual 134	sun/nio/ch/Iocp:isShutdown	()Z
/*     */       //   441: ifeq +10 -> 451
/*     */       //   444: aload_0
/*     */       //   445: getfield 127	sun/nio/ch/Iocp$EventHandlerTask:this$0	Lsun/nio/ch/Iocp;
/*     */       //   448: invokevirtual 133	sun/nio/ch/Iocp:implClose	()V
/*     */       //   451: aload 9
/*     */       //   453: athrow
/*     */       // Line number table:
/*     */       //   Java source line #318	-> byte code offset #0
/*     */       //   Java source line #319	-> byte code offset #4
/*     */       //   Java source line #320	-> byte code offset #14
/*     */       //   Java source line #321	-> byte code offset #23
/*     */       //   Java source line #326	-> byte code offset #26
/*     */       //   Java source line #327	-> byte code offset #30
/*     */       //   Java source line #331	-> byte code offset #34
/*     */       //   Java source line #333	-> byte code offset #37
/*     */       //   Java source line #338	-> byte code offset #48
/*     */       //   Java source line #334	-> byte code offset #51
/*     */       //   Java source line #336	-> byte code offset #53
/*     */       //   Java source line #404	-> byte code offset #58
/*     */       //   Java source line #405	-> byte code offset #70
/*     */       //   Java source line #406	-> byte code offset #85
/*     */       //   Java source line #337	-> byte code offset #92
/*     */       //   Java source line #341	-> byte code offset #93
/*     */       //   Java source line #342	-> byte code offset #101
/*     */       //   Java source line #344	-> byte code offset #109
/*     */       //   Java source line #345	-> byte code offset #118
/*     */       //   Java source line #404	-> byte code offset #123
/*     */       //   Java source line #405	-> byte code offset #135
/*     */       //   Java source line #406	-> byte code offset #150
/*     */       //   Java source line #347	-> byte code offset #157
/*     */       //   Java source line #352	-> byte code offset #158
/*     */       //   Java source line #353	-> byte code offset #161
/*     */       //   Java source line #354	-> byte code offset #168
/*     */       //   Java source line #358	-> byte code offset #171
/*     */       //   Java source line #359	-> byte code offset #174
/*     */       //   Java source line #361	-> byte code offset #191
/*     */       //   Java source line #362	-> byte code offset #215
/*     */       //   Java source line #363	-> byte code offset #220
/*     */       //   Java source line #367	-> byte code offset #231
/*     */       //   Java source line #368	-> byte code offset #268
/*     */       //   Java source line #367	-> byte code offset #271
/*     */       //   Java source line #368	-> byte code offset #290
/*     */       //   Java source line #371	-> byte code offset #293
/*     */       //   Java source line #372	-> byte code offset #306
/*     */       //   Java source line #379	-> byte code offset #311
/*     */       //   Java source line #380	-> byte code offset #322
/*     */       //   Java source line #385	-> byte code offset #325
/*     */       //   Java source line #386	-> byte code offset #331
/*     */       //   Java source line #387	-> byte code offset #339
/*     */       //   Java source line #390	-> byte code offset #345
/*     */       //   Java source line #393	-> byte code offset #359
/*     */       //   Java source line #394	-> byte code offset #365
/*     */       //   Java source line #395	-> byte code offset #375
/*     */       //   Java source line #396	-> byte code offset #378
/*     */       //   Java source line #397	-> byte code offset #383
/*     */       //   Java source line #399	-> byte code offset #398
/*     */       //   Java source line #401	-> byte code offset #412
/*     */       //   Java source line #404	-> byte code offset #415
/*     */       //   Java source line #405	-> byte code offset #429
/*     */       //   Java source line #406	-> byte code offset #444
/*     */       //   Java source line #408	-> byte code offset #451
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	454	0	this	EventHandlerTask
/*     */       //   3	28	1	localGroupAndInvokeCount	Invoker.GroupAndInvokeCount
/*     */       //   13	377	2	bool1	boolean
/*     */       //   22	364	3	localCompletionStatus	Iocp.CompletionStatus
/*     */       //   24	399	4	bool2	boolean
/*     */       //   51	3	5	localIOException	IOException
/*     */       //   116	178	5	localObject1	Object
/*     */       //   68	68	6	i	int
/*     */       //   304	62	6	localPendingFuture	PendingFuture
/*     */       //   271	20	7	localObject2	Object
/*     */       //   363	40	7	j	int
/*     */       //   351	6	8	localObject3	Object
/*     */       //   373	26	8	localResultHandler	Iocp.ResultHandler
/*     */       //   415	37	9	localObject4	Object
/*     */       //   427	3	10	k	int
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   37	48	51	java/io/IOException
/*     */       //   191	231	271	finally
/*     */       //   271	273	271	finally
/*     */       //   331	342	351	finally
/*     */       //   345	348	351	finally
/*     */       //   351	356	351	finally
/*     */       //   26	58	415	finally
/*     */       //   93	123	415	finally
/*     */       //   158	417	415	finally
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract interface OverlappedChannel
/*     */     extends Closeable
/*     */   {
/*     */     public abstract <V, A> PendingFuture<V, A> getByOverlapped(long paramLong);
/*     */   }
/*     */   
/*     */   static abstract interface ResultHandler
/*     */   {
/*     */     public abstract void completed(int paramInt, boolean paramBoolean);
/*     */     
/*     */     public abstract void failed(int paramInt, IOException paramIOException);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\Iocp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
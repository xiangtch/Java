/*     */ package sun.awt.image;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Vector;
/*     */ import sun.awt.AppContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ImageFetcher
/*     */   extends Thread
/*     */ {
/*     */   static final int HIGH_PRIORITY = 8;
/*     */   static final int LOW_PRIORITY = 3;
/*     */   static final int ANIM_PRIORITY = 2;
/*     */   static final int TIMEOUT = 5000;
/*     */   
/*     */   private ImageFetcher(ThreadGroup paramThreadGroup, int paramInt)
/*     */   {
/*  57 */     super(paramThreadGroup, "Image Fetcher " + paramInt);
/*  58 */     setDaemon(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean add(ImageFetchable paramImageFetchable)
/*     */   {
/*  68 */     FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
/*  69 */     synchronized (localFetcherInfo.waitList) {
/*  70 */       if (!localFetcherInfo.waitList.contains(paramImageFetchable)) {
/*  71 */         localFetcherInfo.waitList.addElement(paramImageFetchable);
/*  72 */         if ((localFetcherInfo.numWaiting == 0) && (localFetcherInfo.numFetchers < localFetcherInfo.fetchers.length))
/*     */         {
/*  74 */           createFetchers(localFetcherInfo);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  84 */         if (localFetcherInfo.numFetchers > 0) {
/*  85 */           localFetcherInfo.waitList.notify();
/*     */         } else {
/*  87 */           localFetcherInfo.waitList.removeElement(paramImageFetchable);
/*  88 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*  92 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void remove(ImageFetchable paramImageFetchable)
/*     */   {
/*  99 */     FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
/* 100 */     synchronized (localFetcherInfo.waitList) {
/* 101 */       if (localFetcherInfo.waitList.contains(paramImageFetchable)) {
/* 102 */         localFetcherInfo.waitList.removeElement(paramImageFetchable);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isFetcher(Thread paramThread)
/*     */   {
/* 111 */     FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
/* 112 */     synchronized (localFetcherInfo.waitList) {
/* 113 */       for (int i = 0; i < localFetcherInfo.fetchers.length; i++) {
/* 114 */         if (localFetcherInfo.fetchers[i] == paramThread) {
/* 115 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 119 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean amFetcher()
/*     */   {
/* 126 */     return isFetcher(Thread.currentThread());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static ImageFetchable nextImage()
/*     */   {
/* 135 */     FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
/* 136 */     synchronized (localFetcherInfo.waitList) {
/* 137 */       ImageFetchable localImageFetchable1 = null;
/* 138 */       long l1 = System.currentTimeMillis() + 5000L;
/* 139 */       while (localImageFetchable1 == null) {
/* 140 */         while (localFetcherInfo.waitList.size() == 0) {
/* 141 */           long l2 = System.currentTimeMillis();
/* 142 */           if (l2 >= l1) {
/* 143 */             return null;
/*     */           }
/*     */           try {
/* 146 */             localFetcherInfo.numWaiting += 1;
/* 147 */             localFetcherInfo.waitList.wait(l1 - l2);
/*     */           }
/*     */           catch (InterruptedException localInterruptedException) {
/* 150 */             ImageFetchable localImageFetchable2 = null;
/*     */             
/* 152 */             localFetcherInfo.numWaiting -= 1;return localImageFetchable2;
/*     */           } finally {
/* 152 */             localFetcherInfo.numWaiting -= 1;
/*     */           }
/*     */         }
/* 155 */         localImageFetchable1 = (ImageFetchable)localFetcherInfo.waitList.elementAt(0);
/* 156 */         localFetcherInfo.waitList.removeElement(localImageFetchable1);
/*     */       }
/* 158 */       return localImageFetchable1;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void run()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: invokestatic 199	sun/awt/image/FetcherInfo:getFetcherInfo	()Lsun/awt/image/FetcherInfo;
/*     */     //   3: astore_1
/*     */     //   4: aload_0
/*     */     //   5: invokespecial 200	sun/awt/image/ImageFetcher:fetchloop	()V
/*     */     //   8: aload_1
/*     */     //   9: getfield 174	sun/awt/image/FetcherInfo:waitList	Ljava/util/Vector;
/*     */     //   12: dup
/*     */     //   13: astore_2
/*     */     //   14: monitorenter
/*     */     //   15: invokestatic 187	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */     //   18: astore_3
/*     */     //   19: iconst_0
/*     */     //   20: istore 4
/*     */     //   22: iload 4
/*     */     //   24: aload_1
/*     */     //   25: getfield 173	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
/*     */     //   28: arraylength
/*     */     //   29: if_icmpge +38 -> 67
/*     */     //   32: aload_1
/*     */     //   33: getfield 173	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
/*     */     //   36: iload 4
/*     */     //   38: aaload
/*     */     //   39: aload_3
/*     */     //   40: if_acmpne +21 -> 61
/*     */     //   43: aload_1
/*     */     //   44: getfield 173	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
/*     */     //   47: iload 4
/*     */     //   49: aconst_null
/*     */     //   50: aastore
/*     */     //   51: aload_1
/*     */     //   52: dup
/*     */     //   53: getfield 171	sun/awt/image/FetcherInfo:numFetchers	I
/*     */     //   56: iconst_1
/*     */     //   57: isub
/*     */     //   58: putfield 171	sun/awt/image/FetcherInfo:numFetchers	I
/*     */     //   61: iinc 4 1
/*     */     //   64: goto -42 -> 22
/*     */     //   67: aload_2
/*     */     //   68: monitorexit
/*     */     //   69: goto +10 -> 79
/*     */     //   72: astore 5
/*     */     //   74: aload_2
/*     */     //   75: monitorexit
/*     */     //   76: aload 5
/*     */     //   78: athrow
/*     */     //   79: goto +163 -> 242
/*     */     //   82: astore_2
/*     */     //   83: aload_2
/*     */     //   84: invokevirtual 176	java/lang/Exception:printStackTrace	()V
/*     */     //   87: aload_1
/*     */     //   88: getfield 174	sun/awt/image/FetcherInfo:waitList	Ljava/util/Vector;
/*     */     //   91: dup
/*     */     //   92: astore_2
/*     */     //   93: monitorenter
/*     */     //   94: invokestatic 187	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */     //   97: astore_3
/*     */     //   98: iconst_0
/*     */     //   99: istore 4
/*     */     //   101: iload 4
/*     */     //   103: aload_1
/*     */     //   104: getfield 173	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
/*     */     //   107: arraylength
/*     */     //   108: if_icmpge +38 -> 146
/*     */     //   111: aload_1
/*     */     //   112: getfield 173	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
/*     */     //   115: iload 4
/*     */     //   117: aaload
/*     */     //   118: aload_3
/*     */     //   119: if_acmpne +21 -> 140
/*     */     //   122: aload_1
/*     */     //   123: getfield 173	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
/*     */     //   126: iload 4
/*     */     //   128: aconst_null
/*     */     //   129: aastore
/*     */     //   130: aload_1
/*     */     //   131: dup
/*     */     //   132: getfield 171	sun/awt/image/FetcherInfo:numFetchers	I
/*     */     //   135: iconst_1
/*     */     //   136: isub
/*     */     //   137: putfield 171	sun/awt/image/FetcherInfo:numFetchers	I
/*     */     //   140: iinc 4 1
/*     */     //   143: goto -42 -> 101
/*     */     //   146: aload_2
/*     */     //   147: monitorexit
/*     */     //   148: goto +10 -> 158
/*     */     //   151: astore 6
/*     */     //   153: aload_2
/*     */     //   154: monitorexit
/*     */     //   155: aload 6
/*     */     //   157: athrow
/*     */     //   158: goto +84 -> 242
/*     */     //   161: astore 7
/*     */     //   163: aload_1
/*     */     //   164: getfield 174	sun/awt/image/FetcherInfo:waitList	Ljava/util/Vector;
/*     */     //   167: dup
/*     */     //   168: astore 8
/*     */     //   170: monitorenter
/*     */     //   171: invokestatic 187	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */     //   174: astore 9
/*     */     //   176: iconst_0
/*     */     //   177: istore 10
/*     */     //   179: iload 10
/*     */     //   181: aload_1
/*     */     //   182: getfield 173	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
/*     */     //   185: arraylength
/*     */     //   186: if_icmpge +39 -> 225
/*     */     //   189: aload_1
/*     */     //   190: getfield 173	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
/*     */     //   193: iload 10
/*     */     //   195: aaload
/*     */     //   196: aload 9
/*     */     //   198: if_acmpne +21 -> 219
/*     */     //   201: aload_1
/*     */     //   202: getfield 173	sun/awt/image/FetcherInfo:fetchers	[Ljava/lang/Thread;
/*     */     //   205: iload 10
/*     */     //   207: aconst_null
/*     */     //   208: aastore
/*     */     //   209: aload_1
/*     */     //   210: dup
/*     */     //   211: getfield 171	sun/awt/image/FetcherInfo:numFetchers	I
/*     */     //   214: iconst_1
/*     */     //   215: isub
/*     */     //   216: putfield 171	sun/awt/image/FetcherInfo:numFetchers	I
/*     */     //   219: iinc 10 1
/*     */     //   222: goto -43 -> 179
/*     */     //   225: aload 8
/*     */     //   227: monitorexit
/*     */     //   228: goto +11 -> 239
/*     */     //   231: astore 11
/*     */     //   233: aload 8
/*     */     //   235: monitorexit
/*     */     //   236: aload 11
/*     */     //   238: athrow
/*     */     //   239: aload 7
/*     */     //   241: athrow
/*     */     //   242: return
/*     */     // Line number table:
/*     */     //   Java source line #167	-> byte code offset #0
/*     */     //   Java source line #169	-> byte code offset #4
/*     */     //   Java source line #173	-> byte code offset #8
/*     */     //   Java source line #174	-> byte code offset #15
/*     */     //   Java source line #175	-> byte code offset #19
/*     */     //   Java source line #176	-> byte code offset #32
/*     */     //   Java source line #177	-> byte code offset #43
/*     */     //   Java source line #178	-> byte code offset #51
/*     */     //   Java source line #175	-> byte code offset #61
/*     */     //   Java source line #181	-> byte code offset #67
/*     */     //   Java source line #182	-> byte code offset #79
/*     */     //   Java source line #170	-> byte code offset #82
/*     */     //   Java source line #171	-> byte code offset #83
/*     */     //   Java source line #173	-> byte code offset #87
/*     */     //   Java source line #174	-> byte code offset #94
/*     */     //   Java source line #175	-> byte code offset #98
/*     */     //   Java source line #176	-> byte code offset #111
/*     */     //   Java source line #177	-> byte code offset #122
/*     */     //   Java source line #178	-> byte code offset #130
/*     */     //   Java source line #175	-> byte code offset #140
/*     */     //   Java source line #181	-> byte code offset #146
/*     */     //   Java source line #182	-> byte code offset #158
/*     */     //   Java source line #173	-> byte code offset #161
/*     */     //   Java source line #174	-> byte code offset #171
/*     */     //   Java source line #175	-> byte code offset #176
/*     */     //   Java source line #176	-> byte code offset #189
/*     */     //   Java source line #177	-> byte code offset #201
/*     */     //   Java source line #178	-> byte code offset #209
/*     */     //   Java source line #175	-> byte code offset #219
/*     */     //   Java source line #181	-> byte code offset #225
/*     */     //   Java source line #182	-> byte code offset #239
/*     */     //   Java source line #183	-> byte code offset #242
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	243	0	this	ImageFetcher
/*     */     //   3	207	1	localFetcherInfo	FetcherInfo
/*     */     //   18	101	3	localThread1	Thread
/*     */     //   20	121	4	i	int
/*     */     //   72	5	5	localObject1	Object
/*     */     //   151	5	6	localObject2	Object
/*     */     //   161	79	7	localObject3	Object
/*     */     //   174	23	9	localThread2	Thread
/*     */     //   177	43	10	j	int
/*     */     //   231	6	11	localObject4	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   15	69	72	finally
/*     */     //   72	76	72	finally
/*     */     //   4	8	82	java/lang/Exception
/*     */     //   94	148	151	finally
/*     */     //   151	155	151	finally
/*     */     //   4	8	161	finally
/*     */     //   82	87	161	finally
/*     */     //   161	163	161	finally
/*     */     //   171	228	231	finally
/*     */     //   231	236	231	finally
/*     */   }
/*     */   
/*     */   private void fetchloop()
/*     */   {
/* 191 */     Thread localThread = Thread.currentThread();
/* 192 */     while (isFetcher(localThread))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 198 */       Thread.interrupted();
/* 199 */       localThread.setPriority(8);
/* 200 */       ImageFetchable localImageFetchable = nextImage();
/* 201 */       if (localImageFetchable == null) {
/* 202 */         return;
/*     */       }
/*     */       try {
/* 205 */         localImageFetchable.doFetch();
/*     */       } catch (Exception localException) {
/* 207 */         System.err.println("Uncaught error fetching image:");
/* 208 */         localException.printStackTrace();
/*     */       }
/* 210 */       stoppingAnimation(localThread);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void startingAnimation()
/*     */   {
/* 221 */     FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
/* 222 */     Thread localThread = Thread.currentThread();
/* 223 */     synchronized (localFetcherInfo.waitList) {
/* 224 */       for (int i = 0; i < localFetcherInfo.fetchers.length; i++) {
/* 225 */         if (localFetcherInfo.fetchers[i] == localThread) {
/* 226 */           localFetcherInfo.fetchers[i] = null;
/* 227 */           localFetcherInfo.numFetchers -= 1;
/* 228 */           localThread.setName("Image Animator " + i);
/* 229 */           if (localFetcherInfo.waitList.size() > localFetcherInfo.numWaiting) {
/* 230 */             createFetchers(localFetcherInfo);
/*     */           }
/* 232 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 236 */     localThread.setPriority(2);
/* 237 */     localThread.setName("Image Animator");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void stoppingAnimation(Thread paramThread)
/*     */   {
/* 249 */     FetcherInfo localFetcherInfo = FetcherInfo.getFetcherInfo();
/* 250 */     synchronized (localFetcherInfo.waitList) {
/* 251 */       int i = -1;
/* 252 */       for (int j = 0; j < localFetcherInfo.fetchers.length; j++) {
/* 253 */         if (localFetcherInfo.fetchers[j] == paramThread) {
/* 254 */           return;
/*     */         }
/* 256 */         if (localFetcherInfo.fetchers[j] == null) {
/* 257 */           i = j;
/*     */         }
/*     */       }
/* 260 */       if (i >= 0) {
/* 261 */         localFetcherInfo.fetchers[i] = paramThread;
/* 262 */         localFetcherInfo.numFetchers += 1;
/* 263 */         paramThread.setName("Image Fetcher " + i);
/* 264 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void createFetchers(FetcherInfo paramFetcherInfo)
/*     */   {
/* 276 */     AppContext localAppContext = AppContext.getAppContext();
/* 277 */     Object localObject1 = localAppContext.getThreadGroup();
/*     */     Object localObject2;
/*     */     try {
/* 280 */       if (((ThreadGroup)localObject1).getParent() != null)
/*     */       {
/* 282 */         localObject2 = localObject1;
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 289 */         localObject1 = Thread.currentThread().getThreadGroup();
/* 290 */         ThreadGroup localThreadGroup = ((ThreadGroup)localObject1).getParent();
/* 291 */         while ((localThreadGroup != null) && 
/* 292 */           (localThreadGroup.getParent() != null)) {
/* 293 */           localObject1 = localThreadGroup;
/* 294 */           localThreadGroup = ((ThreadGroup)localObject1).getParent();
/*     */         }
/* 296 */         localObject2 = localObject1;
/*     */       }
/*     */     }
/*     */     catch (SecurityException localSecurityException)
/*     */     {
/* 301 */       localObject2 = localAppContext.getThreadGroup();
/*     */     }
/* 303 */     final Object localObject3 = localObject2;
/*     */     
/* 305 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 308 */         for (int i = 0; i < this.val$info.fetchers.length; i++) {
/* 309 */           if (this.val$info.fetchers[i] == null) {
/* 310 */             ImageFetcher localImageFetcher = new ImageFetcher(localObject3, i, null);
/*     */             try
/*     */             {
/* 313 */               localImageFetcher.start();
/* 314 */               this.val$info.fetchers[i] = localImageFetcher;
/* 315 */               this.val$info.numFetchers += 1;
/*     */             }
/*     */             catch (Error localError) {}
/*     */           }
/*     */         }
/*     */         
/* 321 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\image\ImageFetcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
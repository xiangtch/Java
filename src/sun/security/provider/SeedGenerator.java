/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.URL;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.security.AccessController;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import java.util.Random;
/*     */ import sun.security.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class SeedGenerator
/*     */ {
/*     */   private static SeedGenerator instance;
/*  85 */   private static final Debug debug = Debug.getInstance("provider");
/*     */   
/*     */   static
/*     */   {
/*  89 */     String str = SunEntries.getSeedSource();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */     if ((str.equals("file:/dev/random")) || 
/* 103 */       (str.equals("file:/dev/urandom"))) {
/*     */       try {
/* 105 */         instance = new NativeSeedGenerator(str);
/* 106 */         if (debug != null) {
/* 107 */           debug.println("Using operating system seed generator" + str);
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException1) {
/* 111 */         if (debug != null) {
/* 112 */           debug.println("Failed to use operating system seed generator: " + localIOException1
/* 113 */             .toString());
/*     */         }
/*     */       }
/* 116 */     } else if (str.length() != 0) {
/*     */       try {
/* 118 */         instance = new URLSeedGenerator(str);
/* 119 */         if (debug != null) {
/* 120 */           debug.println("Using URL seed generator reading from " + str);
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException2) {
/* 124 */         if (debug != null) {
/* 125 */           debug.println("Failed to create seed generator with " + str + ": " + localIOException2
/* 126 */             .toString());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 132 */     if (instance == null) {
/* 133 */       if (debug != null) {
/* 134 */         debug.println("Using default threaded seed generator");
/*     */       }
/* 136 */       instance = new ThreadedSeedGenerator();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void generateSeed(byte[] paramArrayOfByte)
/*     */   {
/* 144 */     instance.getSeedBytes(paramArrayOfByte);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static byte[] getSystemEntropy()
/*     */   {
/*     */     MessageDigest localMessageDigest;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 157 */       localMessageDigest = MessageDigest.getInstance("SHA");
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 159 */       throw new InternalError("internal error: SHA-1 not available.", localNoSuchAlgorithmException);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 164 */     byte b = (byte)(int)System.currentTimeMillis();
/* 165 */     localMessageDigest.update(b);
/*     */     
/*     */ 
/* 168 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */ 
/*     */       public Void run()
/*     */       {
/*     */         try
/*     */         {
/* 174 */           localObject1 = System.getProperties();
/* 175 */           Enumeration localEnumeration = ((Properties)localObject1).propertyNames();
/* 176 */           while (localEnumeration.hasMoreElements()) {
/* 177 */             String str = (String)localEnumeration.nextElement();
/* 178 */             this.val$md.update(str.getBytes());
/* 179 */             this.val$md.update(((Properties)localObject1).getProperty(str).getBytes());
/*     */           }
/*     */           
/*     */ 
/* 183 */           SeedGenerator.addNetworkAdapterInfo(this.val$md);
/*     */           
/*     */ 
/* 186 */           File localFile = new File(((Properties)localObject1).getProperty("java.io.tmpdir"));
/* 187 */           int i = 0;
/*     */           
/*     */ 
/* 190 */           DirectoryStream localDirectoryStream = Files.newDirectoryStream(localFile.toPath());Object localObject2 = null;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 196 */             localRandom = new Random();
/* 197 */             for (Path localPath : localDirectoryStream) {
/* 198 */               if ((i < 512) || (localRandom.nextBoolean())) {
/* 199 */                 this.val$md.update(localPath.getFileName()
/* 200 */                   .toString().getBytes());
/*     */               }
/* 202 */               if (i++ > 1024) {
/*     */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable localThrowable2)
/*     */           {
/*     */             Random localRandom;
/* 188 */             localObject2 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           }
/*     */           finally
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 206 */             if (localDirectoryStream != null) if (localObject2 != null) try { localDirectoryStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject2).addSuppressed(localThrowable3); } else localDirectoryStream.close();
/*     */           }
/* 208 */         } catch (Exception localException) { this.val$md.update((byte)localException.hashCode());
/*     */         }
/*     */         
/*     */ 
/* 212 */         Runtime localRuntime = Runtime.getRuntime();
/* 213 */         Object localObject1 = SeedGenerator.longToByteArray(localRuntime.totalMemory());
/* 214 */         this.val$md.update((byte[])localObject1, 0, localObject1.length);
/* 215 */         localObject1 = SeedGenerator.longToByteArray(localRuntime.freeMemory());
/* 216 */         this.val$md.update((byte[])localObject1, 0, localObject1.length);
/*     */         
/* 218 */         return null;
/*     */       }
/* 220 */     });
/* 221 */     return localMessageDigest.digest();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void addNetworkAdapterInfo(MessageDigest paramMessageDigest)
/*     */   {
/*     */     try
/*     */     {
/* 233 */       Enumeration localEnumeration = NetworkInterface.getNetworkInterfaces();
/* 234 */       while (localEnumeration.hasMoreElements()) {
/* 235 */         NetworkInterface localNetworkInterface = (NetworkInterface)localEnumeration.nextElement();
/* 236 */         paramMessageDigest.update(localNetworkInterface.toString().getBytes());
/* 237 */         if (!localNetworkInterface.isVirtual()) {
/* 238 */           byte[] arrayOfByte = localNetworkInterface.getHardwareAddress();
/* 239 */           if (arrayOfByte != null) {
/* 240 */             paramMessageDigest.update(arrayOfByte);
/* 241 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static byte[] longToByteArray(long paramLong)
/*     */   {
/* 254 */     byte[] arrayOfByte = new byte[8];
/*     */     
/* 256 */     for (int i = 0; i < 8; i++) {
/* 257 */       arrayOfByte[i] = ((byte)(int)paramLong);
/* 258 */       paramLong >>= 8;
/*     */     }
/*     */     
/* 261 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   abstract void getSeedBytes(byte[] paramArrayOfByte);
/*     */   
/*     */ 
/*     */ 
/*     */   private static class ThreadedSeedGenerator
/*     */     extends SeedGenerator
/*     */     implements Runnable
/*     */   {
/*     */     private byte[] pool;
/*     */     
/*     */     private int start;
/*     */     
/*     */     private int end;
/*     */     
/*     */     private int count;
/*     */     
/*     */     ThreadGroup seedGroup;
/*     */     
/*     */ 
/*     */     ThreadedSeedGenerator()
/*     */     {
/* 287 */       this.pool = new byte[20];
/* 288 */       this.start = (this.end = 0);
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 293 */         MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
/*     */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 295 */         throw new InternalError("internal error: SHA-1 not available.", localNoSuchAlgorithmException);
/*     */       }
/*     */       
/*     */ 
/* 299 */       final ThreadGroup[] arrayOfThreadGroup = new ThreadGroup[1];
/*     */       
/* 301 */       Thread localThread = (Thread)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */ 
/*     */         public Thread run()
/*     */         {
/* 305 */           Object localObject = Thread.currentThread().getThreadGroup();
/* 306 */           ThreadGroup localThreadGroup; while ((localThreadGroup = ((ThreadGroup)localObject).getParent()) != null) {
/* 307 */             localObject = localThreadGroup;
/*     */           }
/* 309 */           arrayOfThreadGroup[0] = new ThreadGroup((ThreadGroup)localObject, "SeedGenerator ThreadGroup");
/*     */           
/* 311 */           Thread localThread = new Thread(arrayOfThreadGroup[0], ThreadedSeedGenerator.this, "SeedGenerator Thread");
/*     */           
/*     */ 
/* 314 */           localThread.setPriority(1);
/* 315 */           localThread.setDaemon(true);
/* 316 */           return localThread;
/*     */         }
/* 318 */       });
/* 319 */       this.seedGroup = arrayOfThreadGroup[0];
/* 320 */       localThread.start();
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public final void run()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: dup
/*     */       //   2: astore_1
/*     */       //   3: monitorenter
/*     */       //   4: aload_0
/*     */       //   5: getfield 98	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:count	I
/*     */       //   8: aload_0
/*     */       //   9: getfield 101	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:pool	[B
/*     */       //   12: arraylength
/*     */       //   13: if_icmplt +10 -> 23
/*     */       //   16: aload_0
/*     */       //   17: invokevirtual 106	java/lang/Object:wait	()V
/*     */       //   20: goto -16 -> 4
/*     */       //   23: aload_1
/*     */       //   24: monitorexit
/*     */       //   25: goto +8 -> 33
/*     */       //   28: astore_2
/*     */       //   29: aload_1
/*     */       //   30: monitorexit
/*     */       //   31: aload_2
/*     */       //   32: athrow
/*     */       //   33: iconst_0
/*     */       //   34: istore_3
/*     */       //   35: iconst_0
/*     */       //   36: dup
/*     */       //   37: istore_2
/*     */       //   38: istore_1
/*     */       //   39: iload_1
/*     */       //   40: ldc 1
/*     */       //   42: if_icmpge +129 -> 171
/*     */       //   45: iload_2
/*     */       //   46: bipush 6
/*     */       //   48: if_icmpge +123 -> 171
/*     */       //   51: new 72	sun/security/provider/SeedGenerator$ThreadedSeedGenerator$BogusThread
/*     */       //   54: dup
/*     */       //   55: aconst_null
/*     */       //   56: invokespecial 115	sun/security/provider/SeedGenerator$ThreadedSeedGenerator$BogusThread:<init>	(Lsun/security/provider/SeedGenerator$1;)V
/*     */       //   59: astore 4
/*     */       //   61: new 62	java/lang/Thread
/*     */       //   64: dup
/*     */       //   65: aload_0
/*     */       //   66: getfield 103	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:seedGroup	Ljava/lang/ThreadGroup;
/*     */       //   69: aload 4
/*     */       //   71: ldc 3
/*     */       //   73: invokespecial 109	java/lang/Thread:<init>	(Ljava/lang/ThreadGroup;Ljava/lang/Runnable;Ljava/lang/String;)V
/*     */       //   76: astore 5
/*     */       //   78: aload 5
/*     */       //   80: invokevirtual 108	java/lang/Thread:start	()V
/*     */       //   83: goto +17 -> 100
/*     */       //   86: astore 4
/*     */       //   88: new 58	java/lang/InternalError
/*     */       //   91: dup
/*     */       //   92: ldc 5
/*     */       //   94: aload 4
/*     */       //   96: invokespecial 104	java/lang/InternalError:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */       //   99: athrow
/*     */       //   100: iconst_0
/*     */       //   101: istore 4
/*     */       //   103: invokestatic 107	java/lang/System:currentTimeMillis	()J
/*     */       //   106: ldc2_w 55
/*     */       //   109: ladd
/*     */       //   110: lstore 5
/*     */       //   112: invokestatic 107	java/lang/System:currentTimeMillis	()J
/*     */       //   115: lload 5
/*     */       //   117: lcmp
/*     */       //   118: ifge +28 -> 146
/*     */       //   121: aload_0
/*     */       //   122: dup
/*     */       //   123: astore 7
/*     */       //   125: monitorenter
/*     */       //   126: aload 7
/*     */       //   128: monitorexit
/*     */       //   129: goto +11 -> 140
/*     */       //   132: astore 8
/*     */       //   134: aload 7
/*     */       //   136: monitorexit
/*     */       //   137: aload 8
/*     */       //   139: athrow
/*     */       //   140: iinc 4 1
/*     */       //   143: goto -31 -> 112
/*     */       //   146: iload_3
/*     */       //   147: getstatic 102	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:rndTab	[B
/*     */       //   150: iload 4
/*     */       //   152: sipush 255
/*     */       //   155: irem
/*     */       //   156: baload
/*     */       //   157: ixor
/*     */       //   158: i2b
/*     */       //   159: istore_3
/*     */       //   160: iload_1
/*     */       //   161: iload 4
/*     */       //   163: iadd
/*     */       //   164: istore_1
/*     */       //   165: iinc 2 1
/*     */       //   168: goto -129 -> 39
/*     */       //   171: aload_0
/*     */       //   172: dup
/*     */       //   173: astore 4
/*     */       //   175: monitorenter
/*     */       //   176: aload_0
/*     */       //   177: getfield 101	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:pool	[B
/*     */       //   180: aload_0
/*     */       //   181: getfield 99	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:end	I
/*     */       //   184: iload_3
/*     */       //   185: bastore
/*     */       //   186: aload_0
/*     */       //   187: dup
/*     */       //   188: getfield 99	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:end	I
/*     */       //   191: iconst_1
/*     */       //   192: iadd
/*     */       //   193: putfield 99	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:end	I
/*     */       //   196: aload_0
/*     */       //   197: dup
/*     */       //   198: getfield 98	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:count	I
/*     */       //   201: iconst_1
/*     */       //   202: iadd
/*     */       //   203: putfield 98	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:count	I
/*     */       //   206: aload_0
/*     */       //   207: getfield 99	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:end	I
/*     */       //   210: aload_0
/*     */       //   211: getfield 101	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:pool	[B
/*     */       //   214: arraylength
/*     */       //   215: if_icmplt +8 -> 223
/*     */       //   218: aload_0
/*     */       //   219: iconst_0
/*     */       //   220: putfield 99	sun/security/provider/SeedGenerator$ThreadedSeedGenerator:end	I
/*     */       //   223: aload_0
/*     */       //   224: invokevirtual 105	java/lang/Object:notifyAll	()V
/*     */       //   227: aload 4
/*     */       //   229: monitorexit
/*     */       //   230: goto +11 -> 241
/*     */       //   233: astore 9
/*     */       //   235: aload 4
/*     */       //   237: monitorexit
/*     */       //   238: aload 9
/*     */       //   240: athrow
/*     */       //   241: goto -241 -> 0
/*     */       //   244: astore_1
/*     */       //   245: new 58	java/lang/InternalError
/*     */       //   248: dup
/*     */       //   249: ldc 6
/*     */       //   251: aload_1
/*     */       //   252: invokespecial 104	java/lang/InternalError:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */       //   255: athrow
/*     */       // Line number table:
/*     */       //   Java source line #332	-> byte code offset #0
/*     */       //   Java source line #333	-> byte code offset #4
/*     */       //   Java source line #334	-> byte code offset #16
/*     */       //   Java source line #336	-> byte code offset #23
/*     */       //   Java source line #339	-> byte code offset #33
/*     */       //   Java source line #342	-> byte code offset #35
/*     */       //   Java source line #343	-> byte code offset #39
/*     */       //   Java source line #347	-> byte code offset #51
/*     */       //   Java source line #348	-> byte code offset #61
/*     */       //   Java source line #350	-> byte code offset #78
/*     */       //   Java source line #354	-> byte code offset #83
/*     */       //   Java source line #351	-> byte code offset #86
/*     */       //   Java source line #352	-> byte code offset #88
/*     */       //   Java source line #358	-> byte code offset #100
/*     */       //   Java source line #359	-> byte code offset #103
/*     */       //   Java source line #360	-> byte code offset #112
/*     */       //   Java source line #361	-> byte code offset #121
/*     */       //   Java source line #362	-> byte code offset #140
/*     */       //   Java source line #367	-> byte code offset #146
/*     */       //   Java source line #368	-> byte code offset #160
/*     */       //   Java source line #343	-> byte code offset #165
/*     */       //   Java source line #373	-> byte code offset #171
/*     */       //   Java source line #374	-> byte code offset #176
/*     */       //   Java source line #375	-> byte code offset #186
/*     */       //   Java source line #376	-> byte code offset #196
/*     */       //   Java source line #377	-> byte code offset #206
/*     */       //   Java source line #378	-> byte code offset #218
/*     */       //   Java source line #381	-> byte code offset #223
/*     */       //   Java source line #382	-> byte code offset #227
/*     */       //   Java source line #383	-> byte code offset #241
/*     */       //   Java source line #384	-> byte code offset #244
/*     */       //   Java source line #385	-> byte code offset #245
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	256	0	this	ThreadedSeedGenerator
/*     */       //   2	28	1	Ljava/lang/Object;	Object
/*     */       //   38	127	1	i	int
/*     */       //   244	8	1	localException1	Exception
/*     */       //   28	4	2	localObject1	Object
/*     */       //   37	129	2	j	int
/*     */       //   34	151	3	k	int
/*     */       //   59	11	4	localBogusThread	BogusThread
/*     */       //   86	9	4	localException2	Exception
/*     */       //   101	63	4	m	int
/*     */       //   76	3	5	localThread	Thread
/*     */       //   110	6	5	l	long
/*     */       //   123	12	7	Ljava/lang/Object;	Object
/*     */       //   132	6	8	localObject2	Object
/*     */       //   233	6	9	localObject3	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   4	25	28	finally
/*     */       //   28	31	28	finally
/*     */       //   51	83	86	java/lang/Exception
/*     */       //   126	129	132	finally
/*     */       //   132	137	132	finally
/*     */       //   176	230	233	finally
/*     */       //   233	238	233	finally
/*     */       //   0	244	244	java/lang/Exception
/*     */     }
/*     */     
/*     */     void getSeedBytes(byte[] paramArrayOfByte)
/*     */     {
/* 392 */       for (int i = 0; i < paramArrayOfByte.length; i++) {
/* 393 */         paramArrayOfByte[i] = getSeedByte();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     byte getSeedByte()
/*     */     {
/*     */       try
/*     */       {
/* 402 */         synchronized (this) {
/* 403 */           while (this.count <= 0) {
/* 404 */             wait();
/*     */           }
/*     */         }
/*     */       } catch (Exception ???) {
/* 408 */         if (this.count <= 0) {
/* 409 */           throw new InternalError("internal error: SeedGenerator thread generated an exception.", (Throwable)???);
/*     */         }
/*     */       }
/*     */       
/*     */       byte b;
/* 414 */       synchronized (this)
/*     */       {
/* 416 */         b = this.pool[this.start];
/* 417 */         this.pool[this.start] = 0;
/* 418 */         this.start += 1;
/* 419 */         this.count -= 1;
/* 420 */         if (this.start == this.pool.length) {
/* 421 */           this.start = 0;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 426 */         notifyAll();
/*     */       }
/*     */       
/* 429 */       return b;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 436 */     private static byte[] rndTab = { 56, 30, -107, -6, -86, 25, -83, 75, -12, -64, 5, Byte.MIN_VALUE, 78, 21, 16, 32, 70, -81, 37, -51, -43, -46, -108, 87, 29, 17, -55, 22, -11, -111, -115, 84, -100, 108, -45, -15, -98, 72, -33, -28, 31, -52, -37, -117, -97, -27, 93, -123, 47, 126, -80, -62, -93, -79, 61, -96, -65, -5, -47, -119, 14, 89, 81, -118, -88, 20, 67, -126, -113, 60, -102, 55, 110, 28, 85, 121, 122, -58, 2, 45, 43, 24, -9, 103, -13, 102, -68, -54, -101, -104, 19, 13, -39, -26, -103, 62, 77, 51, 44, 111, 73, 18, -127, -82, 4, -30, 11, -99, -74, 40, -89, 42, -76, -77, -94, -35, -69, 35, 120, 76, 33, -73, -7, 82, -25, -10, 88, 125, -112, 58, 83, 95, 6, 10, 98, -34, 80, 15, -91, 86, -19, 52, -17, 117, 49, -63, 118, -90, 36, -116, -40, -71, 97, -53, -109, -85, 109, -16, -3, 104, -95, 68, 54, 34, 26, 114, -1, 106, -121, 3, 66, 0, 100, -84, 57, 107, 119, -42, 112, -61, 1, 48, 38, 12, -56, -57, 39, -106, -72, 41, 7, 71, -29, -59, -8, -38, 79, -31, 124, -124, 8, 91, 116, 99, -4, 9, -36, -78, 63, -49, -67, -87, 59, 101, -32, 92, 94, 53, -41, 115, -66, -70, -122, 50, -50, -22, -20, -18, -21, 23, -2, -48, 96, 65, -105, 123, -14, -110, 69, -24, -120, -75, 74, Byte.MAX_VALUE, -60, 113, 90, -114, 105, 46, 27, -125, -23, -44, 64 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static class BogusThread
/*     */       implements Runnable
/*     */     {
/*     */       public final void run()
/*     */       {
/*     */         try
/*     */         {
/* 474 */           for (int i = 0; i < 5; i++) {
/* 475 */             Thread.sleep(50L);
/*     */           }
/*     */         }
/*     */         catch (Exception localException) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static class URLSeedGenerator
/*     */     extends SeedGenerator
/*     */   {
/*     */     private String deviceName;
/*     */     
/*     */     private InputStream seedStream;
/*     */     
/*     */ 
/*     */     URLSeedGenerator(String paramString)
/*     */       throws IOException
/*     */     {
/* 496 */       if (paramString == null) {
/* 497 */         throw new IOException("No random source specified");
/*     */       }
/* 499 */       this.deviceName = paramString;
/* 500 */       init();
/*     */     }
/*     */     
/*     */     private void init() throws IOException {
/* 504 */       final URL localURL = new URL(this.deviceName);
/*     */       try
/*     */       {
/* 507 */         this.seedStream = ((InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public InputStream run()
/*     */             throws IOException
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 518 */             if (localURL.getProtocol().equalsIgnoreCase("file"))
/*     */             {
/* 520 */               File localFile = SunEntries.getDeviceFile(localURL);
/* 521 */               return new FileInputStream(localFile);
/*     */             }
/* 523 */             return localURL.openStream();
/*     */           }
/*     */         }));
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 529 */         throw new IOException("Failed to open " + this.deviceName, localException.getCause());
/*     */       }
/*     */     }
/*     */     
/*     */     void getSeedBytes(byte[] paramArrayOfByte)
/*     */     {
/* 535 */       int i = paramArrayOfByte.length;
/* 536 */       int j = 0;
/*     */       try {
/* 538 */         while (j < i) {
/* 539 */           int k = this.seedStream.read(paramArrayOfByte, j, i - j);
/*     */           
/* 541 */           if (k < 0) {
/* 542 */             throw new InternalError("URLSeedGenerator " + this.deviceName + " reached end of file");
/*     */           }
/*     */           
/*     */ 
/* 546 */           j += k;
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException) {
/* 550 */         throw new InternalError("URLSeedGenerator " + this.deviceName + " generated exception: " + localIOException.getMessage(), localIOException);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\SeedGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
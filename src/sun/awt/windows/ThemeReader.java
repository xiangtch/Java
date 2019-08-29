/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ThemeReader
/*     */ {
/*  54 */   private static final Map<String, Long> widgetToTheme = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  59 */   private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
/*     */   
/*  61 */   private static final Lock readLock = readWriteLock.readLock();
/*  62 */   private static final Lock writeLock = readWriteLock.writeLock();
/*  63 */   private static volatile boolean valid = false;
/*     */   
/*     */   private static volatile boolean isThemed;
/*     */   
/*     */   static volatile boolean xpStyleEnabled;
/*     */   
/*     */   static void flush()
/*     */   {
/*  71 */     valid = false;
/*     */   }
/*     */   
/*     */   private static native boolean initThemes();
/*     */   
/*     */   public static boolean isThemed() {
/*  77 */     writeLock.lock();
/*     */     try {
/*  79 */       isThemed = initThemes();
/*  80 */       return isThemed;
/*     */     } finally {
/*  82 */       writeLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isXPStyleEnabled() {
/*  87 */     return xpStyleEnabled;
/*     */   }
/*     */   
/*     */   private static Long getThemeImpl(String paramString)
/*     */   {
/*  92 */     Long localLong = (Long)widgetToTheme.get(paramString);
/*  93 */     if (localLong == null) {
/*  94 */       int i = paramString.indexOf("::");
/*  95 */       if (i > 0)
/*     */       {
/*     */ 
/*  98 */         setWindowTheme(paramString.substring(0, i));
/*  99 */         localLong = Long.valueOf(openTheme(paramString.substring(i + 2)));
/* 100 */         setWindowTheme(null);
/*     */       } else {
/* 102 */         localLong = Long.valueOf(openTheme(paramString));
/*     */       }
/* 104 */       widgetToTheme.put(paramString, localLong);
/*     */     }
/* 106 */     return localLong;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private static Long getTheme(String paramString)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 170	sun/awt/windows/ThemeReader:isThemed	Z
/*     */     //   3: ifne +13 -> 16
/*     */     //   6: new 84	java/lang/IllegalStateException
/*     */     //   9: dup
/*     */     //   10: ldc 2
/*     */     //   12: invokespecial 177	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
/*     */     //   15: athrow
/*     */     //   16: getstatic 171	sun/awt/windows/ThemeReader:valid	Z
/*     */     //   19: ifne +118 -> 137
/*     */     //   22: getstatic 174	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   25: invokeinterface 213 1 0
/*     */     //   30: getstatic 175	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   33: invokeinterface 212 1 0
/*     */     //   38: getstatic 171	sun/awt/windows/ThemeReader:valid	Z
/*     */     //   41: ifne +58 -> 99
/*     */     //   44: getstatic 173	sun/awt/windows/ThemeReader:widgetToTheme	Ljava/util/Map;
/*     */     //   47: invokeinterface 209 1 0
/*     */     //   52: invokeinterface 205 1 0
/*     */     //   57: astore_1
/*     */     //   58: aload_1
/*     */     //   59: invokeinterface 206 1 0
/*     */     //   64: ifeq +23 -> 87
/*     */     //   67: aload_1
/*     */     //   68: invokeinterface 207 1 0
/*     */     //   73: checkcast 85	java/lang/Long
/*     */     //   76: astore_2
/*     */     //   77: aload_2
/*     */     //   78: invokevirtual 178	java/lang/Long:longValue	()J
/*     */     //   81: invokestatic 187	sun/awt/windows/ThemeReader:closeTheme	(J)V
/*     */     //   84: goto -26 -> 58
/*     */     //   87: getstatic 173	sun/awt/windows/ThemeReader:widgetToTheme	Ljava/util/Map;
/*     */     //   90: invokeinterface 208 1 0
/*     */     //   95: iconst_1
/*     */     //   96: putstatic 171	sun/awt/windows/ThemeReader:valid	Z
/*     */     //   99: getstatic 174	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   102: invokeinterface 212 1 0
/*     */     //   107: getstatic 175	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   110: invokeinterface 213 1 0
/*     */     //   115: goto +22 -> 137
/*     */     //   118: astore_3
/*     */     //   119: getstatic 174	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   122: invokeinterface 212 1 0
/*     */     //   127: getstatic 175	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   130: invokeinterface 213 1 0
/*     */     //   135: aload_3
/*     */     //   136: athrow
/*     */     //   137: getstatic 173	sun/awt/windows/ThemeReader:widgetToTheme	Ljava/util/Map;
/*     */     //   140: aload_0
/*     */     //   141: invokeinterface 210 2 0
/*     */     //   146: checkcast 85	java/lang/Long
/*     */     //   149: astore_1
/*     */     //   150: aload_1
/*     */     //   151: ifnonnull +64 -> 215
/*     */     //   154: getstatic 174	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   157: invokeinterface 213 1 0
/*     */     //   162: getstatic 175	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   165: invokeinterface 212 1 0
/*     */     //   170: aload_0
/*     */     //   171: invokestatic 204	sun/awt/windows/ThemeReader:getThemeImpl	(Ljava/lang/String;)Ljava/lang/Long;
/*     */     //   174: astore_1
/*     */     //   175: getstatic 174	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   178: invokeinterface 212 1 0
/*     */     //   183: getstatic 175	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   186: invokeinterface 213 1 0
/*     */     //   191: goto +24 -> 215
/*     */     //   194: astore 4
/*     */     //   196: getstatic 174	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   199: invokeinterface 212 1 0
/*     */     //   204: getstatic 175	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
/*     */     //   207: invokeinterface 213 1 0
/*     */     //   212: aload 4
/*     */     //   214: athrow
/*     */     //   215: aload_1
/*     */     //   216: areturn
/*     */     // Line number table:
/*     */     //   Java source line #112	-> byte code offset #0
/*     */     //   Java source line #113	-> byte code offset #6
/*     */     //   Java source line #115	-> byte code offset #16
/*     */     //   Java source line #116	-> byte code offset #22
/*     */     //   Java source line #117	-> byte code offset #30
/*     */     //   Java source line #119	-> byte code offset #38
/*     */     //   Java source line #121	-> byte code offset #44
/*     */     //   Java source line #122	-> byte code offset #77
/*     */     //   Java source line #123	-> byte code offset #84
/*     */     //   Java source line #124	-> byte code offset #87
/*     */     //   Java source line #125	-> byte code offset #95
/*     */     //   Java source line #128	-> byte code offset #99
/*     */     //   Java source line #129	-> byte code offset #107
/*     */     //   Java source line #130	-> byte code offset #115
/*     */     //   Java source line #128	-> byte code offset #118
/*     */     //   Java source line #129	-> byte code offset #127
/*     */     //   Java source line #130	-> byte code offset #135
/*     */     //   Java source line #134	-> byte code offset #137
/*     */     //   Java source line #135	-> byte code offset #150
/*     */     //   Java source line #136	-> byte code offset #154
/*     */     //   Java source line #137	-> byte code offset #162
/*     */     //   Java source line #139	-> byte code offset #170
/*     */     //   Java source line #141	-> byte code offset #175
/*     */     //   Java source line #142	-> byte code offset #183
/*     */     //   Java source line #143	-> byte code offset #191
/*     */     //   Java source line #141	-> byte code offset #194
/*     */     //   Java source line #142	-> byte code offset #204
/*     */     //   Java source line #143	-> byte code offset #212
/*     */     //   Java source line #145	-> byte code offset #215
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	217	0	paramString	String
/*     */     //   57	159	1	localObject1	Object
/*     */     //   76	2	2	localLong	Long
/*     */     //   118	18	3	localObject2	Object
/*     */     //   194	19	4	localObject3	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   38	99	118	finally
/*     */     //   170	175	194	finally
/*     */     //   194	196	194	finally
/*     */   }
/*     */   
/*     */   private static native void paintBackground(int[] paramArrayOfInt, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
/*     */   
/*     */   public static void paintBackground(int[] paramArrayOfInt, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */   {
/* 154 */     readLock.lock();
/*     */     try {
/* 156 */       paintBackground(paramArrayOfInt, getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*     */     } finally {
/* 158 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private static native Insets getThemeMargins(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   public static Insets getThemeMargins(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 166 */     readLock.lock();
/*     */     try {
/* 168 */       return getThemeMargins(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
/*     */     } finally {
/* 170 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private static native boolean isThemePartDefined(long paramLong, int paramInt1, int paramInt2);
/*     */   
/*     */   public static boolean isThemePartDefined(String paramString, int paramInt1, int paramInt2) {
/* 177 */     readLock.lock();
/*     */     try {
/* 179 */       return isThemePartDefined(getTheme(paramString).longValue(), paramInt1, paramInt2);
/*     */     } finally {
/* 181 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private static native Color getColor(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   public static Color getColor(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 189 */     readLock.lock();
/*     */     try {
/* 191 */       return getColor(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
/*     */     } finally {
/* 193 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private static native int getInt(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   public static int getInt(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 201 */     readLock.lock();
/*     */     try {
/* 203 */       return getInt(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
/*     */     } finally {
/* 205 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private static native int getEnum(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   public static int getEnum(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 213 */     readLock.lock();
/*     */     try {
/* 215 */       return getEnum(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
/*     */     } finally {
/* 217 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static native boolean getBoolean(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   public static boolean getBoolean(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 226 */     readLock.lock();
/*     */     try {
/* 228 */       return getBoolean(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
/*     */     } finally {
/* 230 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private static native boolean getSysBoolean(long paramLong, int paramInt);
/*     */   
/*     */   public static boolean getSysBoolean(String paramString, int paramInt) {
/* 237 */     readLock.lock();
/*     */     try {
/* 239 */       return getSysBoolean(getTheme(paramString).longValue(), paramInt);
/*     */     } finally {
/* 241 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private static native Point getPoint(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   public static Point getPoint(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 249 */     readLock.lock();
/*     */     try {
/* 251 */       return getPoint(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
/*     */     } finally {
/* 253 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static native Dimension getPosition(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   public static Dimension getPosition(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 262 */     readLock.lock();
/*     */     try {
/* 264 */       return getPosition(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
/*     */     } finally {
/* 266 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private static native Dimension getPartSize(long paramLong, int paramInt1, int paramInt2);
/*     */   
/*     */   public static Dimension getPartSize(String paramString, int paramInt1, int paramInt2)
/*     */   {
/* 274 */     readLock.lock();
/*     */     try {
/* 276 */       return getPartSize(getTheme(paramString).longValue(), paramInt1, paramInt2);
/*     */     } finally {
/* 278 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static native long openTheme(String paramString);
/*     */   
/*     */   private static native void closeTheme(long paramLong);
/*     */   
/*     */   private static native void setWindowTheme(String paramString);
/*     */   
/*     */   private static native long getThemeTransitionDuration(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */   public static long getThemeTransitionDuration(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 293 */     readLock.lock();
/*     */     try {
/* 295 */       return getThemeTransitionDuration(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */     finally {
/* 298 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static native boolean isGetThemeTransitionDurationDefined();
/*     */   
/*     */   private static native Insets getThemeBackgroundContentMargins(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */   public static Insets getThemeBackgroundContentMargins(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 309 */     readLock.lock();
/*     */     try {
/* 311 */       return getThemeBackgroundContentMargins(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */     finally {
/* 314 */       readLock.unlock();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\ThemeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
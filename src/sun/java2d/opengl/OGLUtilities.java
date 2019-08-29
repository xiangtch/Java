/*     */ package sun.java2d.opengl;
/*     */ 
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class OGLUtilities
/*     */ {
/*     */   public static final int UNDEFINED = 0;
/*     */   public static final int WINDOW = 1;
/*     */   public static final int PBUFFER = 2;
/*     */   public static final int TEXTURE = 3;
/*     */   public static final int FLIP_BACKBUFFER = 4;
/*     */   public static final int FBOBJECT = 5;
/*     */   
/*     */   public static boolean isQueueFlusherThread()
/*     */   {
/*  66 */     return OGLRenderQueue.isQueueFlusherThread();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean invokeWithOGLContextCurrent(Graphics paramGraphics, Runnable paramRunnable)
/*     */   {
/*  96 */     OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/*  97 */     localOGLRenderQueue.lock();
/*     */     try {
/*  99 */       if (paramGraphics != null) {
/* 100 */         if (!(paramGraphics instanceof SunGraphics2D)) {
/* 101 */           return false;
/*     */         }
/* 103 */         SurfaceData localSurfaceData = ((SunGraphics2D)paramGraphics).surfaceData;
/* 104 */         if (!(localSurfaceData instanceof OGLSurfaceData)) {
/* 105 */           return false;
/*     */         }
/*     */         
/*     */ 
/* 109 */         OGLContext.validateContext((OGLSurfaceData)localSurfaceData);
/*     */       }
/*     */       
/*     */ 
/* 113 */       localOGLRenderQueue.flushAndInvokeNow(paramRunnable);
/*     */       
/*     */ 
/*     */ 
/* 117 */       OGLContext.invalidateCurrentContext();
/*     */     } finally {
/* 119 */       localOGLRenderQueue.unlock();
/*     */     }
/*     */     
/* 122 */     return true;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static boolean invokeWithOGLSharedContextCurrent(java.awt.GraphicsConfiguration paramGraphicsConfiguration, Runnable paramRunnable)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: instanceof 73
/*     */     //   4: ifne +5 -> 9
/*     */     //   7: iconst_0
/*     */     //   8: ireturn
/*     */     //   9: invokestatic 129	sun/java2d/opengl/OGLRenderQueue:getInstance	()Lsun/java2d/opengl/OGLRenderQueue;
/*     */     //   12: astore_2
/*     */     //   13: aload_2
/*     */     //   14: invokevirtual 125	sun/java2d/opengl/OGLRenderQueue:lock	()V
/*     */     //   17: aload_0
/*     */     //   18: checkcast 73	sun/java2d/opengl/OGLGraphicsConfig
/*     */     //   21: invokestatic 123	sun/java2d/opengl/OGLContext:setScratchSurface	(Lsun/java2d/opengl/OGLGraphicsConfig;)V
/*     */     //   24: aload_2
/*     */     //   25: aload_1
/*     */     //   26: invokevirtual 128	sun/java2d/opengl/OGLRenderQueue:flushAndInvokeNow	(Ljava/lang/Runnable;)V
/*     */     //   29: invokestatic 122	sun/java2d/opengl/OGLContext:invalidateCurrentContext	()V
/*     */     //   32: aload_2
/*     */     //   33: invokevirtual 126	sun/java2d/opengl/OGLRenderQueue:unlock	()V
/*     */     //   36: goto +10 -> 46
/*     */     //   39: astore_3
/*     */     //   40: aload_2
/*     */     //   41: invokevirtual 126	sun/java2d/opengl/OGLRenderQueue:unlock	()V
/*     */     //   44: aload_3
/*     */     //   45: athrow
/*     */     //   46: iconst_1
/*     */     //   47: ireturn
/*     */     // Line number table:
/*     */     //   Java source line #152	-> byte code offset #0
/*     */     //   Java source line #153	-> byte code offset #7
/*     */     //   Java source line #156	-> byte code offset #9
/*     */     //   Java source line #157	-> byte code offset #13
/*     */     //   Java source line #160	-> byte code offset #17
/*     */     //   Java source line #163	-> byte code offset #24
/*     */     //   Java source line #167	-> byte code offset #29
/*     */     //   Java source line #169	-> byte code offset #32
/*     */     //   Java source line #170	-> byte code offset #36
/*     */     //   Java source line #169	-> byte code offset #39
/*     */     //   Java source line #170	-> byte code offset #44
/*     */     //   Java source line #172	-> byte code offset #46
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	48	0	paramGraphicsConfiguration	java.awt.GraphicsConfiguration
/*     */     //   0	48	1	paramRunnable	Runnable
/*     */     //   12	29	2	localOGLRenderQueue	OGLRenderQueue
/*     */     //   39	6	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   17	32	39	finally
/*     */   }
/*     */   
/*     */   public static Rectangle getOGLViewport(Graphics paramGraphics, int paramInt1, int paramInt2)
/*     */   {
/* 199 */     if (!(paramGraphics instanceof SunGraphics2D)) {
/* 200 */       return null;
/*     */     }
/*     */     
/* 203 */     SunGraphics2D localSunGraphics2D = (SunGraphics2D)paramGraphics;
/* 204 */     SurfaceData localSurfaceData = localSunGraphics2D.surfaceData;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 209 */     int i = localSunGraphics2D.transX;
/* 210 */     int j = localSunGraphics2D.transY;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 215 */     Rectangle localRectangle = localSurfaceData.getBounds();
/* 216 */     int k = i;
/* 217 */     int m = localRectangle.height - (j + paramInt2);
/*     */     
/* 219 */     return new Rectangle(k, m, paramInt1, paramInt2);
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
/*     */   public static Rectangle getOGLScissorBox(Graphics paramGraphics)
/*     */   {
/* 240 */     if (!(paramGraphics instanceof SunGraphics2D)) {
/* 241 */       return null;
/*     */     }
/*     */     
/* 244 */     SunGraphics2D localSunGraphics2D = (SunGraphics2D)paramGraphics;
/* 245 */     SurfaceData localSurfaceData = localSunGraphics2D.surfaceData;
/* 246 */     Region localRegion = localSunGraphics2D.getCompClip();
/* 247 */     if (!localRegion.isRectangular())
/*     */     {
/*     */ 
/*     */ 
/* 251 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 256 */     int i = localRegion.getLoX();
/* 257 */     int j = localRegion.getLoY();
/*     */     
/*     */ 
/* 260 */     int k = localRegion.getWidth();
/* 261 */     int m = localRegion.getHeight();
/*     */     
/*     */ 
/*     */ 
/* 265 */     Rectangle localRectangle = localSurfaceData.getBounds();
/* 266 */     int n = i;
/* 267 */     int i1 = localRectangle.height - (j + m);
/*     */     
/* 269 */     return new Rectangle(n, i1, k, m);
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
/*     */   public static Object getOGLSurfaceIdentifier(Graphics paramGraphics)
/*     */   {
/* 285 */     if (!(paramGraphics instanceof SunGraphics2D)) {
/* 286 */       return null;
/*     */     }
/* 288 */     return ((SunGraphics2D)paramGraphics).surfaceData;
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
/*     */   public static int getOGLSurfaceType(Graphics paramGraphics)
/*     */   {
/* 304 */     if (!(paramGraphics instanceof SunGraphics2D)) {
/* 305 */       return 0;
/*     */     }
/* 307 */     SurfaceData localSurfaceData = ((SunGraphics2D)paramGraphics).surfaceData;
/* 308 */     if (!(localSurfaceData instanceof OGLSurfaceData)) {
/* 309 */       return 0;
/*     */     }
/* 311 */     return ((OGLSurfaceData)localSurfaceData).getType();
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
/*     */   public static int getOGLTextureType(Graphics paramGraphics)
/*     */   {
/* 329 */     if (!(paramGraphics instanceof SunGraphics2D)) {
/* 330 */       return 0;
/*     */     }
/* 332 */     SurfaceData localSurfaceData = ((SunGraphics2D)paramGraphics).surfaceData;
/* 333 */     if (!(localSurfaceData instanceof OGLSurfaceData)) {
/* 334 */       return 0;
/*     */     }
/* 336 */     return ((OGLSurfaceData)localSurfaceData).getTextureTarget();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\opengl\OGLUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
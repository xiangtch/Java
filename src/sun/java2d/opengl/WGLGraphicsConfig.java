/*     */ package sun.java2d.opengl;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.BufferCapabilities;
/*     */ import java.awt.BufferCapabilities.FlipContents;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.ImageCapabilities;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.VolatileImage;
/*     */ import sun.awt.Win32GraphicsConfig;
/*     */ import sun.awt.Win32GraphicsDevice;
/*     */ import sun.awt.image.SunVolatileImage;
/*     */ import sun.awt.image.SurfaceManager;
/*     */ import sun.awt.windows.WComponentPeer;
/*     */ import sun.java2d.Disposer;
/*     */ import sun.java2d.DisposerRecord;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.Surface;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.pipe.hw.AccelDeviceEventListener;
/*     */ import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
/*     */ import sun.java2d.pipe.hw.AccelSurface;
/*     */ import sun.java2d.pipe.hw.AccelTypedVolatileImage;
/*     */ import sun.java2d.pipe.hw.ContextCapabilities;
/*     */ import sun.java2d.windows.GDIWindowSurfaceData;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WGLGraphicsConfig
/*     */   extends Win32GraphicsConfig
/*     */   implements OGLGraphicsConfig
/*     */ {
/*  67 */   private static ImageCapabilities imageCaps = new WGLImageCaps(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */   private Object disposerReferent = new Object();
/*     */   
/*     */ 
/*     */   public static native int getDefaultPixFmt(int paramInt);
/*     */   
/*     */ 
/*     */   private static native boolean initWGL();
/*     */   
/*  81 */   protected static boolean wglAvailable = initWGL();
/*     */   
/*     */   private static native long getWGLConfigInfo(int paramInt1, int paramInt2);
/*     */   
/*     */   private static native int getOGLCapabilities(long paramLong);
/*     */   
/*  87 */   protected WGLGraphicsConfig(Win32GraphicsDevice paramWin32GraphicsDevice, int paramInt, long paramLong, ContextCapabilities paramContextCapabilities) { super(paramWin32GraphicsDevice, paramInt);
/*  88 */     this.pConfigInfo = paramLong;
/*  89 */     this.oglCaps = paramContextCapabilities;
/*  90 */     this.context = new OGLContext(OGLRenderQueue.getInstance(), this);
/*     */     
/*     */ 
/*     */ 
/*  94 */     Disposer.addRecord(this.disposerReferent, new WGLGCDisposerRecord(this.pConfigInfo, paramWin32GraphicsDevice
/*     */     
/*  96 */       .getScreen()));
/*     */   }
/*     */   
/*     */   public Object getProxyKey() {
/* 100 */     return this;
/*     */   }
/*     */   
/*     */   public SurfaceData createManagedSurface(int paramInt1, int paramInt2, int paramInt3) {
/* 104 */     return WGLSurfaceData.createData(this, paramInt1, paramInt2, 
/* 105 */       getColorModel(paramInt3), null, 3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static WGLGraphicsConfig getConfig(Win32GraphicsDevice paramWin32GraphicsDevice, int paramInt)
/*     */   {
/* 113 */     if (!wglAvailable) {
/* 114 */       return null;
/*     */     }
/*     */     
/* 117 */     long l = 0L;
/* 118 */     String[] arrayOfString = new String[1];
/* 119 */     OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/* 120 */     localOGLRenderQueue.lock();
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 125 */       OGLContext.invalidateCurrentContext();
/*     */       
/* 127 */       WGLGetConfigInfo localWGLGetConfigInfo = new WGLGetConfigInfo(paramWin32GraphicsDevice.getScreen(), paramInt, null);
/* 128 */       localOGLRenderQueue.flushAndInvokeNow(localWGLGetConfigInfo);
/* 129 */       l = localWGLGetConfigInfo.getConfigInfo();
/* 130 */       if (l != 0L) {
/* 131 */         OGLContext.setScratchSurface(l);
/* 132 */         localOGLRenderQueue.flushAndInvokeNow(new Runnable() {
/*     */           public void run() {
/* 134 */             this.val$ids[0] = OGLContext.getOGLIdString();
/*     */           }
/*     */         });
/*     */       }
/*     */     } finally {
/* 139 */       localOGLRenderQueue.unlock();
/*     */     }
/* 141 */     if (l == 0L) {
/* 142 */       return null;
/*     */     }
/*     */     
/* 145 */     int i = getOGLCapabilities(l);
/* 146 */     OGLContext.OGLContextCaps localOGLContextCaps = new OGLContext.OGLContextCaps(i, arrayOfString[0]);
/*     */     
/* 148 */     return new WGLGraphicsConfig(paramWin32GraphicsDevice, paramInt, l, localOGLContextCaps);
/*     */   }
/*     */   
/*     */   private static class WGLGetConfigInfo
/*     */     implements Runnable
/*     */   {
/*     */     private int screen;
/*     */     private int pixfmt;
/*     */     private long cfginfo;
/*     */     
/*     */     private WGLGetConfigInfo(int paramInt1, int paramInt2)
/*     */     {
/* 160 */       this.screen = paramInt1;
/* 161 */       this.pixfmt = paramInt2;
/*     */     }
/*     */     
/* 164 */     public void run() { this.cfginfo = WGLGraphicsConfig.getWGLConfigInfo(this.screen, this.pixfmt); }
/*     */     
/*     */     public long getConfigInfo() {
/* 167 */       return this.cfginfo;
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isWGLAvailable() {
/* 172 */     return wglAvailable;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isCapPresent(int paramInt)
/*     */   {
/* 181 */     return (this.oglCaps.getCaps() & paramInt) != 0;
/*     */   }
/*     */   
/*     */   public final long getNativeConfigInfo()
/*     */   {
/* 186 */     return this.pConfigInfo;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final OGLContext getContext()
/*     */   {
/* 196 */     return this.context;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public synchronized void displayChanged()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokespecial 389	sun/awt/Win32GraphicsConfig:displayChanged	()V
/*     */     //   4: invokestatic 409	sun/java2d/opengl/OGLRenderQueue:getInstance	()Lsun/java2d/opengl/OGLRenderQueue;
/*     */     //   7: astore_1
/*     */     //   8: aload_1
/*     */     //   9: invokevirtual 406	sun/java2d/opengl/OGLRenderQueue:lock	()V
/*     */     //   12: invokestatic 402	sun/java2d/opengl/OGLContext:invalidateCurrentContext	()V
/*     */     //   15: aload_1
/*     */     //   16: invokevirtual 407	sun/java2d/opengl/OGLRenderQueue:unlock	()V
/*     */     //   19: goto +10 -> 29
/*     */     //   22: astore_2
/*     */     //   23: aload_1
/*     */     //   24: invokevirtual 407	sun/java2d/opengl/OGLRenderQueue:unlock	()V
/*     */     //   27: aload_2
/*     */     //   28: athrow
/*     */     //   29: return
/*     */     // Line number table:
/*     */     //   Java source line #231	-> byte code offset #0
/*     */     //   Java source line #235	-> byte code offset #4
/*     */     //   Java source line #236	-> byte code offset #8
/*     */     //   Java source line #238	-> byte code offset #12
/*     */     //   Java source line #240	-> byte code offset #15
/*     */     //   Java source line #241	-> byte code offset #19
/*     */     //   Java source line #240	-> byte code offset #22
/*     */     //   Java source line #241	-> byte code offset #27
/*     */     //   Java source line #242	-> byte code offset #29
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	30	0	this	WGLGraphicsConfig
/*     */     //   7	17	1	localOGLRenderQueue	OGLRenderQueue
/*     */     //   22	6	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   12	15	22	finally
/*     */   }
/*     */   
/*     */   private static class WGLGCDisposerRecord
/*     */     implements DisposerRecord
/*     */   {
/*     */     private long pCfgInfo;
/*     */     private int screen;
/*     */     
/*     */     public WGLGCDisposerRecord(long paramLong, int paramInt)
/*     */     {
/* 203 */       this.pCfgInfo = paramLong;
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void dispose()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: invokestatic 55	sun/java2d/opengl/OGLRenderQueue:getInstance	()Lsun/java2d/opengl/OGLRenderQueue;
/*     */       //   3: astore_1
/*     */       //   4: aload_1
/*     */       //   5: invokevirtual 51	sun/java2d/opengl/OGLRenderQueue:lock	()V
/*     */       //   8: aload_1
/*     */       //   9: new 34	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord$1
/*     */       //   12: dup
/*     */       //   13: aload_0
/*     */       //   14: invokespecial 56	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord$1:<init>	(Lsun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord;)V
/*     */       //   17: invokevirtual 54	sun/java2d/opengl/OGLRenderQueue:flushAndInvokeNow	(Ljava/lang/Runnable;)V
/*     */       //   20: aload_1
/*     */       //   21: invokevirtual 52	sun/java2d/opengl/OGLRenderQueue:unlock	()V
/*     */       //   24: goto +10 -> 34
/*     */       //   27: astore_2
/*     */       //   28: aload_1
/*     */       //   29: invokevirtual 52	sun/java2d/opengl/OGLRenderQueue:unlock	()V
/*     */       //   32: aload_2
/*     */       //   33: athrow
/*     */       //   34: aload_0
/*     */       //   35: getfield 49	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord:pCfgInfo	J
/*     */       //   38: lconst_0
/*     */       //   39: lcmp
/*     */       //   40: ifeq +15 -> 55
/*     */       //   43: aload_0
/*     */       //   44: getfield 49	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord:pCfgInfo	J
/*     */       //   47: invokestatic 53	sun/java2d/opengl/OGLRenderQueue:disposeGraphicsConfig	(J)V
/*     */       //   50: aload_0
/*     */       //   51: lconst_0
/*     */       //   52: putfield 49	sun/java2d/opengl/WGLGraphicsConfig$WGLGCDisposerRecord:pCfgInfo	J
/*     */       //   55: return
/*     */       // Line number table:
/*     */       //   Java source line #206	-> byte code offset #0
/*     */       //   Java source line #207	-> byte code offset #4
/*     */       //   Java source line #209	-> byte code offset #8
/*     */       //   Java source line #220	-> byte code offset #20
/*     */       //   Java source line #221	-> byte code offset #24
/*     */       //   Java source line #220	-> byte code offset #27
/*     */       //   Java source line #221	-> byte code offset #32
/*     */       //   Java source line #222	-> byte code offset #34
/*     */       //   Java source line #223	-> byte code offset #43
/*     */       //   Java source line #224	-> byte code offset #50
/*     */       //   Java source line #226	-> byte code offset #55
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	56	0	this	WGLGCDisposerRecord
/*     */       //   3	26	1	localOGLRenderQueue	OGLRenderQueue
/*     */       //   27	6	2	localObject	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   8	20	27	finally
/*     */     }
/*     */   }
/*     */   
/*     */   public ColorModel getColorModel(int paramInt)
/*     */   {
/* 246 */     switch (paramInt)
/*     */     {
/*     */ 
/*     */     case 1: 
/* 250 */       return new DirectColorModel(24, 16711680, 65280, 255);
/*     */     case 2: 
/* 252 */       return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
/*     */     case 3: 
/* 254 */       ColorSpace localColorSpace = ColorSpace.getInstance(1000);
/* 255 */       return new DirectColorModel(localColorSpace, 32, 16711680, 65280, 255, -16777216, true, 3);
/*     */     }
/*     */     
/*     */     
/* 259 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 265 */     return "WGLGraphicsConfig[dev=" + this.screen + ",pixfmt=" + this.visual + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private BufferCapabilities bufferCaps;
/*     */   
/*     */ 
/*     */   private long pConfigInfo;
/*     */   
/*     */ 
/*     */   private ContextCapabilities oglCaps;
/*     */   
/*     */ 
/*     */   private OGLContext context;
/*     */   
/*     */ 
/*     */   public SurfaceData createSurfaceData(WComponentPeer paramWComponentPeer, int paramInt)
/*     */   {
/* 284 */     Object localObject = WGLSurfaceData.createData(paramWComponentPeer);
/* 285 */     if (localObject == null) {
/* 286 */       localObject = GDIWindowSurfaceData.createData(paramWComponentPeer);
/*     */     }
/* 288 */     return (SurfaceData)localObject;
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
/*     */   public void assertOperationSupported(Component paramComponent, int paramInt, BufferCapabilities paramBufferCapabilities)
/*     */     throws AWTException
/*     */   {
/* 306 */     if (paramInt > 2) {
/* 307 */       throw new AWTException("Only double or single buffering is supported");
/*     */     }
/*     */     
/* 310 */     BufferCapabilities localBufferCapabilities = getBufferCapabilities();
/* 311 */     if (!localBufferCapabilities.isPageFlipping()) {
/* 312 */       throw new AWTException("Page flipping is not supported");
/*     */     }
/* 314 */     if (paramBufferCapabilities.getFlipContents() == FlipContents.PRIOR) {
/* 315 */       throw new AWTException("FlipContents.PRIOR is not supported");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public VolatileImage createBackBuffer(WComponentPeer paramWComponentPeer)
/*     */   {
/* 325 */     Component localComponent = (Component)paramWComponentPeer.getTarget();
/*     */     
/*     */ 
/* 328 */     int i = Math.max(1, localComponent.getWidth());
/* 329 */     int j = Math.max(1, localComponent.getHeight());
/* 330 */     return new SunVolatileImage(localComponent, i, j, Boolean.TRUE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void flip(WComponentPeer paramWComponentPeer, Component paramComponent, VolatileImage paramVolatileImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, FlipContents paramFlipContents)
/*     */   {
/*     */     Object localObject1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 344 */     if (paramFlipContents == FlipContents.COPIED) {
/* 345 */       localObject1 = SurfaceManager.getManager(paramVolatileImage);
/* 346 */       SurfaceData localSurfaceData1 = ((SurfaceManager)localObject1).getPrimarySurfaceData();
/*     */       Object localObject2;
/* 348 */       if ((localSurfaceData1 instanceof WGLSurfaceData.WGLVSyncOffScreenSurfaceData)) {
/* 349 */         localObject2 = (WGLSurfaceData.WGLVSyncOffScreenSurfaceData)localSurfaceData1;
/*     */         
/* 351 */         SurfaceData localSurfaceData2 = ((WGLSurfaceData.WGLVSyncOffScreenSurfaceData)localObject2).getFlipSurface();
/* 352 */         SunGraphics2D localSunGraphics2D = new SunGraphics2D(localSurfaceData2, Color.black, Color.white, null);
/*     */         try
/*     */         {
/* 355 */           localSunGraphics2D.drawImage(paramVolatileImage, 0, 0, null);
/*     */         } finally {
/* 357 */           localSunGraphics2D.dispose();
/*     */         }
/*     */       } else {
/* 360 */         localObject2 = paramWComponentPeer.getGraphics();
/*     */         try {
/* 362 */           ((Graphics)localObject2).drawImage(paramVolatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt3, paramInt4, null);
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/* 367 */           ((Graphics)localObject2).dispose();
/*     */         }
/* 369 */         return;
/*     */       }
/* 371 */     } else if (paramFlipContents == FlipContents.PRIOR)
/*     */     {
/* 373 */       return;
/*     */     }
/*     */     
/* 376 */     OGLSurfaceData.swapBuffers(paramWComponentPeer.getData());
/*     */     
/* 378 */     if (paramFlipContents == FlipContents.BACKGROUND) {
/* 379 */       localObject1 = paramVolatileImage.getGraphics();
/*     */       try {
/* 381 */         ((Graphics)localObject1).setColor(paramComponent.getBackground());
/* 382 */         ((Graphics)localObject1).fillRect(0, 0, paramVolatileImage
/* 383 */           .getWidth(), paramVolatileImage
/* 384 */           .getHeight());
/*     */       } finally {
/* 386 */         ((Graphics)localObject1).dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static class WGLBufferCaps extends BufferCapabilities {
/*     */     public WGLBufferCaps(boolean paramBoolean) {
/* 393 */       super(WGLGraphicsConfig.imageCaps, paramBoolean ? FlipContents.UNDEFINED : null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public BufferCapabilities getBufferCapabilities()
/*     */   {
/* 400 */     if (this.bufferCaps == null) {
/* 401 */       boolean bool = isCapPresent(65536);
/* 402 */       this.bufferCaps = new WGLBufferCaps(bool);
/*     */     }
/* 404 */     return this.bufferCaps;
/*     */   }
/*     */   
/*     */   private static class WGLImageCaps extends ImageCapabilities {
/*     */     private WGLImageCaps() {
/* 409 */       super();
/*     */     }
/*     */     
/* 412 */     public boolean isTrueVolatile() { return true; }
/*     */   }
/*     */   
/*     */ 
/*     */   public ImageCapabilities getImageCapabilities()
/*     */   {
/* 418 */     return imageCaps;
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
/*     */   public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 431 */     if ((paramInt4 == 4) || (paramInt4 == 1) || (paramInt4 == 0) || (paramInt3 == 2))
/*     */     {
/*     */ 
/* 434 */       return null;
/*     */     }
/*     */     
/* 437 */     if (paramInt4 == 5) {
/* 438 */       if (!isCapPresent(12)) {
/* 439 */         return null;
/*     */       }
/* 441 */     } else if (paramInt4 == 2) {
/* 442 */       int i = paramInt3 == 1 ? 1 : 0;
/* 443 */       if ((i == 0) && (!isCapPresent(2))) {
/* 444 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 448 */     AccelTypedVolatileImage localAccelTypedVolatileImage = new AccelTypedVolatileImage(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     
/* 450 */     Surface localSurface = localAccelTypedVolatileImage.getDestSurface();
/* 451 */     if ((!(localSurface instanceof AccelSurface)) || 
/* 452 */       (((AccelSurface)localSurface).getType() != paramInt4))
/*     */     {
/* 454 */       localAccelTypedVolatileImage.flush();
/* 455 */       localAccelTypedVolatileImage = null;
/*     */     }
/*     */     
/* 458 */     return localAccelTypedVolatileImage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ContextCapabilities getContextCapabilities()
/*     */   {
/* 468 */     return this.oglCaps;
/*     */   }
/*     */   
/*     */   public void addDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener)
/*     */   {
/* 473 */     AccelDeviceEventNotifier.addListener(paramAccelDeviceEventListener, this.screen.getScreen());
/*     */   }
/*     */   
/*     */   public void removeDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener)
/*     */   {
/* 478 */     AccelDeviceEventNotifier.removeListener(paramAccelDeviceEventListener);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\opengl\WGLGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
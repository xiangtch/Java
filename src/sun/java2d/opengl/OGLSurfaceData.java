/*     */ package sun.java2d.opengl;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Composite;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.security.AccessController;
/*     */ import sun.awt.image.PixelConverter.ArgbPre;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.SurfaceDataProxy;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.GraphicsPrimitive;
/*     */ import sun.java2d.loops.MaskFill;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.java2d.pipe.ParallelogramPipe;
/*     */ import sun.java2d.pipe.PixelToParallelogramConverter;
/*     */ import sun.java2d.pipe.RenderBuffer;
/*     */ import sun.java2d.pipe.TextPipe;
/*     */ import sun.java2d.pipe.hw.AccelSurface;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class OGLSurfaceData
/*     */   extends SurfaceData
/*     */   implements AccelSurface
/*     */ {
/*     */   public static final int PBUFFER = 2;
/*     */   public static final int FBOBJECT = 5;
/*     */   public static final int PF_INT_ARGB = 0;
/*     */   public static final int PF_INT_ARGB_PRE = 1;
/*     */   public static final int PF_INT_RGB = 2;
/*     */   public static final int PF_INT_RGBX = 3;
/*     */   public static final int PF_INT_BGR = 4;
/*     */   public static final int PF_INT_BGRX = 5;
/*     */   public static final int PF_USHORT_565_RGB = 6;
/*     */   public static final int PF_USHORT_555_RGB = 7;
/*     */   public static final int PF_USHORT_555_RGBX = 8;
/*     */   public static final int PF_BYTE_GRAY = 9;
/*     */   public static final int PF_USHORT_GRAY = 10;
/*     */   public static final int PF_3BYTE_BGR = 11;
/*     */   private static final String DESC_OPENGL_SURFACE = "OpenGL Surface";
/*     */   private static final String DESC_OPENGL_SURFACE_RTT = "OpenGL Surface (render-to-texture)";
/*     */   private static final String DESC_OPENGL_TEXTURE = "OpenGL Texture";
/* 134 */   static final SurfaceType OpenGLSurface = SurfaceType.Any
/* 135 */     .deriveSubType("OpenGL Surface", PixelConverter.ArgbPre.instance);
/*     */   
/* 137 */   static final SurfaceType OpenGLSurfaceRTT = OpenGLSurface
/* 138 */     .deriveSubType("OpenGL Surface (render-to-texture)");
/* 139 */   static final SurfaceType OpenGLTexture = SurfaceType.Any
/* 140 */     .deriveSubType("OpenGL Texture");
/*     */   
/*     */ 
/*     */   private static boolean isFBObjectEnabled;
/*     */   
/*     */ 
/*     */   private static boolean isLCDShaderEnabled;
/*     */   
/*     */ 
/*     */   private static boolean isBIOpShaderEnabled;
/*     */   
/*     */ 
/*     */   private static boolean isGradShaderEnabled;
/*     */   
/*     */ 
/*     */   private OGLGraphicsConfig graphicsConfig;
/*     */   
/*     */ 
/*     */   protected int type;
/*     */   
/*     */ 
/*     */   private int nativeWidth;
/*     */   
/*     */ 
/*     */   private int nativeHeight;
/*     */   
/*     */ 
/*     */   protected static OGLRenderer oglRenderPipe;
/*     */   
/*     */ 
/*     */   protected static PixelToParallelogramConverter oglTxRenderPipe;
/*     */   
/*     */ 
/*     */   protected static ParallelogramPipe oglAAPgramPipe;
/*     */   
/*     */ 
/*     */   protected static OGLTextRenderer oglTextPipe;
/*     */   
/*     */   protected static OGLDrawImage oglImagePipe;
/*     */   
/*     */ 
/*     */   static
/*     */   {
/* 183 */     if (!GraphicsEnvironment.isHeadless())
/*     */     {
/* 185 */       String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.fbobject"));
/*     */       
/*     */ 
/* 188 */       isFBObjectEnabled = !"false".equals(str1);
/*     */       
/*     */ 
/* 191 */       String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.lcdshader"));
/*     */       
/*     */ 
/* 194 */       isLCDShaderEnabled = !"false".equals(str2);
/*     */       
/*     */ 
/* 197 */       String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.biopshader"));
/*     */       
/*     */ 
/* 200 */       isBIOpShaderEnabled = !"false".equals(str3);
/*     */       
/*     */ 
/* 203 */       String str4 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.gradshader"));
/*     */       
/*     */ 
/* 206 */       isGradShaderEnabled = !"false".equals(str4);
/*     */       
/* 208 */       OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/* 209 */       oglImagePipe = new OGLDrawImage();
/* 210 */       oglTextPipe = new OGLTextRenderer(localOGLRenderQueue);
/* 211 */       oglRenderPipe = new OGLRenderer(localOGLRenderQueue);
/* 212 */       if (GraphicsPrimitive.tracingEnabled()) {
/* 213 */         oglTextPipe = oglTextPipe.traceWrap();
/*     */       }
/*     */       
/*     */ 
/* 217 */       oglAAPgramPipe = oglRenderPipe.getAAParallelogramPipe();
/* 218 */       oglTxRenderPipe = new PixelToParallelogramConverter(oglRenderPipe, oglRenderPipe, 1.0D, 0.25D, true);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 223 */       OGLBlitLoops.register();
/* 224 */       OGLMaskFill.register();
/* 225 */       OGLMaskBlit.register();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected OGLSurfaceData(OGLGraphicsConfig paramOGLGraphicsConfig, ColorModel paramColorModel, int paramInt)
/*     */   {
/* 232 */     super(getCustomSurfaceType(paramInt), paramColorModel);
/* 233 */     this.graphicsConfig = paramOGLGraphicsConfig;
/* 234 */     this.type = paramInt;
/* 235 */     setBlitProxyKey(paramOGLGraphicsConfig.getProxyKey());
/*     */   }
/*     */   
/*     */   public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData)
/*     */   {
/* 240 */     return OGLSurfaceDataProxy.createProxy(paramSurfaceData, this.graphicsConfig);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static SurfaceType getCustomSurfaceType(int paramInt)
/*     */   {
/* 248 */     switch (paramInt) {
/*     */     case 3: 
/* 250 */       return OpenGLTexture;
/*     */     case 5: 
/* 252 */       return OpenGLSurfaceRTT;
/*     */     }
/*     */     
/* 255 */     return OpenGLSurface;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initSurfaceNow(int paramInt1, int paramInt2)
/*     */   {
/* 265 */     boolean bool1 = getTransparency() == 1;
/* 266 */     boolean bool2 = false;
/*     */     
/* 268 */     switch (this.type) {
/*     */     case 2: 
/* 270 */       bool2 = initPbuffer(getNativeOps(), this.graphicsConfig
/* 271 */         .getNativeConfigInfo(), bool1, paramInt1, paramInt2);
/*     */       
/*     */ 
/* 274 */       break;
/*     */     
/*     */     case 3: 
/* 277 */       bool2 = initTexture(getNativeOps(), bool1, 
/* 278 */         isTexNonPow2Available(), 
/* 279 */         isTexRectAvailable(), paramInt1, paramInt2);
/*     */       
/* 281 */       break;
/*     */     
/*     */     case 5: 
/* 284 */       bool2 = initFBObject(getNativeOps(), bool1, 
/* 285 */         isTexNonPow2Available(), 
/* 286 */         isTexRectAvailable(), paramInt1, paramInt2);
/*     */       
/* 288 */       break;
/*     */     
/*     */     case 4: 
/* 291 */       bool2 = initFlipBackbuffer(getNativeOps());
/* 292 */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 298 */     if (!bool2) {
/* 299 */       throw new OutOfMemoryError("can't create offscreen surface");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void initSurface(final int paramInt1, final int paramInt2)
/*     */   {
/* 309 */     OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/* 310 */     localOGLRenderQueue.lock();
/*     */     try {
/* 312 */       switch (this.type)
/*     */       {
/*     */ 
/*     */       case 2: 
/*     */       case 3: 
/*     */       case 5: 
/* 318 */         OGLContext.setScratchSurface(this.graphicsConfig);
/* 319 */         break;
/*     */       }
/*     */       
/*     */       
/* 323 */       localOGLRenderQueue.flushAndInvokeNow(new Runnable() {
/*     */         public void run() {
/* 325 */           OGLSurfaceData.this.initSurfaceNow(paramInt1, paramInt2);
/*     */         }
/*     */       });
/*     */     } finally {
/* 329 */       localOGLRenderQueue.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final OGLContext getContext()
/*     */   {
/* 338 */     return this.graphicsConfig.getContext();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   final OGLGraphicsConfig getOGLGraphicsConfig()
/*     */   {
/* 345 */     return this.graphicsConfig;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final int getType()
/*     */   {
/* 352 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final int getTextureTarget()
/*     */   {
/* 361 */     return getTextureTarget(getNativeOps());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final int getTextureID()
/*     */   {
/* 370 */     return getTextureID(getNativeOps());
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
/*     */   public long getNativeResource(int paramInt)
/*     */   {
/* 391 */     if (paramInt == 3) {
/* 392 */       return getTextureID();
/*     */     }
/* 394 */     return 0L;
/*     */   }
/*     */   
/*     */   public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 398 */     throw new InternalError("not implemented yet");
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
/*     */   public boolean canRenderLCDText(SunGraphics2D paramSunGraphics2D)
/*     */   {
/* 414 */     if ((this.graphicsConfig.isCapPresent(131072)) && 
/* 415 */       (paramSunGraphics2D.surfaceData.getTransparency() == 1) && (paramSunGraphics2D.paintState <= 0)) if (paramSunGraphics2D.compositeState > 0) if (paramSunGraphics2D.compositeState > 1) {
/*     */           break label62;
/*     */         }
/*     */     label62:
/* 413 */     return 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 418 */       canHandleComposite(paramSunGraphics2D.composite);
/*     */   }
/*     */   
/*     */   private boolean canHandleComposite(Composite paramComposite) {
/* 422 */     if ((paramComposite instanceof AlphaComposite)) {
/* 423 */       AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
/*     */       
/* 425 */       return (localAlphaComposite.getRule() == 3) && (localAlphaComposite.getAlpha() >= 1.0F);
/*     */     }
/* 427 */     return false;
/*     */   }
/*     */   
/*     */   public void validatePipe(SunGraphics2D paramSunGraphics2D)
/*     */   {
/* 432 */     int i = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     Object localObject;
/*     */     
/*     */ 
/*     */ 
/* 441 */     if (((paramSunGraphics2D.compositeState <= 0) && (paramSunGraphics2D.paintState <= 1)) || ((paramSunGraphics2D.compositeState == 1) && (paramSunGraphics2D.paintState <= 1) && 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 448 */       (((AlphaComposite)paramSunGraphics2D.composite).getRule() == 3)) || ((paramSunGraphics2D.compositeState == 2) && (paramSunGraphics2D.paintState <= 1)))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 455 */       localObject = oglTextPipe;
/*     */     }
/*     */     else
/*     */     {
/* 459 */       super.validatePipe(paramSunGraphics2D);
/* 460 */       localObject = paramSunGraphics2D.textpipe;
/* 461 */       i = 1;
/*     */     }
/*     */     
/* 464 */     PixelToParallelogramConverter localPixelToParallelogramConverter1 = null;
/* 465 */     OGLRenderer localOGLRenderer = null;
/*     */     
/* 467 */     if (paramSunGraphics2D.antialiasHint != 2) {
/* 468 */       if (paramSunGraphics2D.paintState <= 1) {
/* 469 */         if (paramSunGraphics2D.compositeState <= 2) {
/* 470 */           localPixelToParallelogramConverter1 = oglTxRenderPipe;
/* 471 */           localOGLRenderer = oglRenderPipe;
/*     */         }
/* 473 */       } else if ((paramSunGraphics2D.compositeState <= 1) && 
/* 474 */         (OGLPaints.isValid(paramSunGraphics2D))) {
/* 475 */         localPixelToParallelogramConverter1 = oglTxRenderPipe;
/* 476 */         localOGLRenderer = oglRenderPipe;
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 481 */     else if (paramSunGraphics2D.paintState <= 1) {
/* 482 */       if ((this.graphicsConfig.isCapPresent(256)) && ((paramSunGraphics2D.imageComp == CompositeType.SrcOverNoEa) || (paramSunGraphics2D.imageComp == CompositeType.SrcOver)))
/*     */       {
/*     */ 
/*     */ 
/* 486 */         if (i == 0) {
/* 487 */           super.validatePipe(paramSunGraphics2D);
/* 488 */           i = 1;
/*     */         }
/* 490 */         PixelToParallelogramConverter localPixelToParallelogramConverter2 = new PixelToParallelogramConverter(paramSunGraphics2D.shapepipe, oglAAPgramPipe, 0.125D, 0.499D, false);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 495 */         paramSunGraphics2D.drawpipe = localPixelToParallelogramConverter2;
/* 496 */         paramSunGraphics2D.fillpipe = localPixelToParallelogramConverter2;
/* 497 */         paramSunGraphics2D.shapepipe = localPixelToParallelogramConverter2;
/* 498 */       } else if (paramSunGraphics2D.compositeState == 2)
/*     */       {
/* 500 */         localPixelToParallelogramConverter1 = oglTxRenderPipe;
/* 501 */         localOGLRenderer = oglRenderPipe;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 507 */     if (localPixelToParallelogramConverter1 != null) {
/* 508 */       if (paramSunGraphics2D.transformState >= 3) {
/* 509 */         paramSunGraphics2D.drawpipe = localPixelToParallelogramConverter1;
/* 510 */         paramSunGraphics2D.fillpipe = localPixelToParallelogramConverter1;
/* 511 */       } else if (paramSunGraphics2D.strokeState != 0) {
/* 512 */         paramSunGraphics2D.drawpipe = localPixelToParallelogramConverter1;
/* 513 */         paramSunGraphics2D.fillpipe = localOGLRenderer;
/*     */       } else {
/* 515 */         paramSunGraphics2D.drawpipe = localOGLRenderer;
/* 516 */         paramSunGraphics2D.fillpipe = localOGLRenderer;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 522 */       paramSunGraphics2D.shapepipe = localPixelToParallelogramConverter1;
/*     */     }
/* 524 */     else if (i == 0) {
/* 525 */       super.validatePipe(paramSunGraphics2D);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 530 */     paramSunGraphics2D.textpipe = ((TextPipe)localObject);
/*     */     
/*     */ 
/* 533 */     paramSunGraphics2D.imagepipe = oglImagePipe;
/*     */   }
/*     */   
/*     */   protected MaskFill getMaskFill(SunGraphics2D paramSunGraphics2D)
/*     */   {
/* 538 */     if (paramSunGraphics2D.paintState > 1)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 550 */       if ((!OGLPaints.isValid(paramSunGraphics2D)) || 
/* 551 */         (!this.graphicsConfig.isCapPresent(16)))
/*     */       {
/* 553 */         return null;
/*     */       }
/*     */     }
/* 556 */     return super.getMaskFill(paramSunGraphics2D);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 562 */     if ((paramSunGraphics2D.transformState < 3) && (paramSunGraphics2D.compositeState < 2))
/*     */     {
/*     */ 
/* 565 */       paramInt1 += paramSunGraphics2D.transX;
/* 566 */       paramInt2 += paramSunGraphics2D.transY;
/*     */       
/* 568 */       oglRenderPipe.copyArea(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */       
/* 570 */       return true;
/*     */     }
/* 572 */     return false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   static void dispose(long paramLong1, long paramLong2)
/*     */   {
/* 605 */     OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/* 606 */     localOGLRenderQueue.lock();
/*     */     
/*     */     try
/*     */     {
/* 610 */       OGLContext.setScratchSurface(paramLong2);
/*     */       
/* 612 */       RenderBuffer localRenderBuffer = localOGLRenderQueue.getBuffer();
/* 613 */       localOGLRenderQueue.ensureCapacityAndAlignment(12, 4);
/* 614 */       localRenderBuffer.putInt(73);
/* 615 */       localRenderBuffer.putLong(paramLong1);
/*     */       
/*     */ 
/* 618 */       localOGLRenderQueue.flushNow();
/*     */     } finally {
/* 620 */       localOGLRenderQueue.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   static void swapBuffers(long paramLong) {
/* 625 */     OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/* 626 */     localOGLRenderQueue.lock();
/*     */     try {
/* 628 */       RenderBuffer localRenderBuffer = localOGLRenderQueue.getBuffer();
/* 629 */       localOGLRenderQueue.ensureCapacityAndAlignment(12, 4);
/* 630 */       localRenderBuffer.putInt(80);
/* 631 */       localRenderBuffer.putLong(paramLong);
/* 632 */       localOGLRenderQueue.flushNow();
/*     */     } finally {
/* 634 */       localOGLRenderQueue.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean isTexNonPow2Available()
/*     */   {
/* 643 */     return this.graphicsConfig.isCapPresent(32);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean isTexRectAvailable()
/*     */   {
/* 652 */     return this.graphicsConfig.isCapPresent(1048576);
/*     */   }
/*     */   
/*     */   public Rectangle getNativeBounds() {
/* 656 */     OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/* 657 */     localOGLRenderQueue.lock();
/*     */     try {
/* 659 */       return new Rectangle(this.nativeWidth, this.nativeHeight);
/*     */     } finally {
/* 661 */       localOGLRenderQueue.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean isOnScreen()
/*     */   {
/* 672 */     return getType() == 1;
/*     */   }
/*     */   
/*     */   protected native boolean initTexture(long paramLong, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, int paramInt2);
/*     */   
/*     */   protected native boolean initFBObject(long paramLong, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, int paramInt2);
/*     */   
/*     */   protected native boolean initFlipBackbuffer(long paramLong);
/*     */   
/*     */   protected abstract boolean initPbuffer(long paramLong1, long paramLong2, boolean paramBoolean, int paramInt1, int paramInt2);
/*     */   
/*     */   private native int getTextureTarget(long paramLong);
/*     */   
/*     */   private native int getTextureID(long paramLong);
/*     */   
/*     */   /* Error */
/*     */   public void flush()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 454	sun/java2d/opengl/OGLSurfaceData:invalidate	()V
/*     */     //   4: invokestatic 445	sun/java2d/opengl/OGLRenderQueue:getInstance	()Lsun/java2d/opengl/OGLRenderQueue;
/*     */     //   7: astore_1
/*     */     //   8: aload_1
/*     */     //   9: invokevirtual 441	sun/java2d/opengl/OGLRenderQueue:lock	()V
/*     */     //   12: aload_0
/*     */     //   13: getfield 413	sun/java2d/opengl/OGLSurfaceData:graphicsConfig	Lsun/java2d/opengl/OGLGraphicsConfig;
/*     */     //   16: invokestatic 435	sun/java2d/opengl/OGLContext:setScratchSurface	(Lsun/java2d/opengl/OGLGraphicsConfig;)V
/*     */     //   19: aload_1
/*     */     //   20: invokevirtual 446	sun/java2d/opengl/OGLRenderQueue:getBuffer	()Lsun/java2d/pipe/RenderBuffer;
/*     */     //   23: astore_2
/*     */     //   24: aload_1
/*     */     //   25: bipush 12
/*     */     //   27: iconst_4
/*     */     //   28: invokevirtual 443	sun/java2d/opengl/OGLRenderQueue:ensureCapacityAndAlignment	(II)V
/*     */     //   31: aload_2
/*     */     //   32: bipush 72
/*     */     //   34: invokevirtual 473	sun/java2d/pipe/RenderBuffer:putInt	(I)Lsun/java2d/pipe/RenderBuffer;
/*     */     //   37: pop
/*     */     //   38: aload_2
/*     */     //   39: aload_0
/*     */     //   40: invokevirtual 453	sun/java2d/opengl/OGLSurfaceData:getNativeOps	()J
/*     */     //   43: invokevirtual 474	sun/java2d/pipe/RenderBuffer:putLong	(J)Lsun/java2d/pipe/RenderBuffer;
/*     */     //   46: pop
/*     */     //   47: aload_1
/*     */     //   48: invokevirtual 440	sun/java2d/opengl/OGLRenderQueue:flushNow	()V
/*     */     //   51: aload_1
/*     */     //   52: invokevirtual 442	sun/java2d/opengl/OGLRenderQueue:unlock	()V
/*     */     //   55: goto +10 -> 65
/*     */     //   58: astore_3
/*     */     //   59: aload_1
/*     */     //   60: invokevirtual 442	sun/java2d/opengl/OGLRenderQueue:unlock	()V
/*     */     //   63: aload_3
/*     */     //   64: athrow
/*     */     //   65: return
/*     */     // Line number table:
/*     */     //   Java source line #576	-> byte code offset #0
/*     */     //   Java source line #577	-> byte code offset #4
/*     */     //   Java source line #578	-> byte code offset #8
/*     */     //   Java source line #582	-> byte code offset #12
/*     */     //   Java source line #584	-> byte code offset #19
/*     */     //   Java source line #585	-> byte code offset #24
/*     */     //   Java source line #586	-> byte code offset #31
/*     */     //   Java source line #587	-> byte code offset #38
/*     */     //   Java source line #590	-> byte code offset #47
/*     */     //   Java source line #592	-> byte code offset #51
/*     */     //   Java source line #593	-> byte code offset #55
/*     */     //   Java source line #592	-> byte code offset #58
/*     */     //   Java source line #593	-> byte code offset #63
/*     */     //   Java source line #594	-> byte code offset #65
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	66	0	this	OGLSurfaceData
/*     */     //   7	53	1	localOGLRenderQueue	OGLRenderQueue
/*     */     //   23	16	2	localRenderBuffer	RenderBuffer
/*     */     //   58	6	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   12	51	58	finally
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\opengl\OGLSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.java2d.opengl;
/*     */ 
/*     */ import java.awt.BufferCapabilities.FlipContents;
/*     */ import java.awt.Component;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.image.ColorModel;
/*     */ import sun.awt.image.SunVolatileImage;
/*     */ import sun.awt.image.VolatileSurfaceManager;
/*     */ import sun.awt.windows.WComponentPeer;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
/*     */ import sun.java2d.pipe.hw.ExtendedBufferCapabilities.VSyncType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WGLVolatileSurfaceManager
/*     */   extends VolatileSurfaceManager
/*     */ {
/*     */   private boolean accelerationEnabled;
/*     */   
/*     */   public WGLVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
/*     */   {
/*  49 */     super(paramSunVolatileImage, paramObject);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  59 */     int i = paramSunVolatileImage.getTransparency();
/*  60 */     WGLGraphicsConfig localWGLGraphicsConfig = (WGLGraphicsConfig)paramSunVolatileImage.getGraphicsConfig();
/*  61 */     if (i != 1) if (i != 3) break label54; label54: 
/*     */     
/*     */ 
/*     */ 
/*  65 */       this.accelerationEnabled = ((localWGLGraphicsConfig.isCapPresent(12)) || (localWGLGraphicsConfig.isCapPresent(2)));
/*     */   }
/*     */   
/*     */   protected boolean isAccelerationEnabled() {
/*  69 */     return this.accelerationEnabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SurfaceData initAcceleratedSurface()
/*     */   {
/*  78 */     Component localComponent = this.vImg.getComponent();
/*     */     
/*  80 */     WComponentPeer localWComponentPeer = localComponent != null ? (WComponentPeer)localComponent.getPeer() : null;
/*     */     WGLSurfaceData.WGLOffScreenSurfaceData localWGLOffScreenSurfaceData;
/*     */     try {
/*  83 */       int i = 0;
/*  84 */       boolean bool = false;
/*  85 */       Object localObject1; Object localObject2; if ((this.context instanceof Boolean)) {
/*  86 */         bool = ((Boolean)this.context).booleanValue();
/*  87 */         if (bool) {
/*  88 */           localObject1 = localWComponentPeer.getBackBufferCaps();
/*  89 */           if ((localObject1 instanceof ExtendedBufferCapabilities)) {
/*  90 */             localObject2 = (ExtendedBufferCapabilities)localObject1;
/*     */             
/*  92 */             if ((((ExtendedBufferCapabilities)localObject2).getVSync() == VSyncType.VSYNC_ON) &&
/*  93 */               (((ExtendedBufferCapabilities)localObject2).getFlipContents() == BufferCapabilities.FlipContents.COPIED))
/*     */             {
/*  95 */               i = 1;
/*  96 */               bool = false;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 102 */       if (bool)
/*     */       {
/* 104 */         localWGLOffScreenSurfaceData = WGLSurfaceData.createData(localWComponentPeer, this.vImg, 4);
/*     */       }
/*     */       else {
/* 107 */         localObject1 = (WGLGraphicsConfig)this.vImg.getGraphicsConfig();
/* 108 */         localObject2 = ((WGLGraphicsConfig)localObject1).getColorModel(this.vImg.getTransparency());
/* 109 */         int j = this.vImg.getForcedAccelSurfaceType();
/*     */         
/*     */ 
/* 112 */         if (j == 0) {
/* 113 */           j = ((WGLGraphicsConfig)localObject1).isCapPresent(12) ? 5 : 2;
/*     */         }
/*     */         
/* 116 */         if (i != 0) {
/* 117 */           localWGLOffScreenSurfaceData = WGLSurfaceData.createData(localWComponentPeer, this.vImg, j);
/*     */         } else {
/* 119 */           localWGLOffScreenSurfaceData = WGLSurfaceData.createData((WGLGraphicsConfig)localObject1, this.vImg
/* 120 */             .getWidth(), this.vImg
/* 121 */             .getHeight(), (ColorModel)localObject2, this.vImg, j);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (NullPointerException localNullPointerException) {
/* 126 */       localWGLOffScreenSurfaceData = null;
/*     */     } catch (OutOfMemoryError localOutOfMemoryError) {
/* 128 */       localWGLOffScreenSurfaceData = null;
/*     */     }
/*     */     
/* 131 */     return localWGLOffScreenSurfaceData;
/*     */   }
/*     */   
/*     */   protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration)
/*     */   {
/* 136 */     if (paramGraphicsConfiguration != null) if (!(paramGraphicsConfiguration instanceof WGLGraphicsConfig)) break label26; label26: return paramGraphicsConfiguration == this.vImg
/*     */     
/* 138 */       .getGraphicsConfig();
/*     */   }
/*     */   
/*     */   public void initContents()
/*     */   {
/* 143 */     if (this.vImg.getForcedAccelSurfaceType() != 3) {
/* 144 */       super.initContents();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\opengl\WGLVolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
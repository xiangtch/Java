/*     */ package sun.java2d.d3d;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.ColorModel;
/*     */ import sun.awt.Win32GraphicsConfig;
/*     */ import sun.awt.image.SunVolatileImage;
/*     */ import sun.awt.image.SurfaceManager;
/*     */ import sun.awt.image.VolatileSurfaceManager;
/*     */ import sun.awt.windows.WComponentPeer;
/*     */ import sun.java2d.InvalidPipeException;
/*     */ import sun.java2d.SurfaceData;
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
/*     */ public class D3DVolatileSurfaceManager
/*     */   extends VolatileSurfaceManager
/*     */ {
/*     */   private boolean accelerationEnabled;
/*     */   private int restoreCountdown;
/*     */   
/*     */   public D3DVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
/*     */   {
/*  51 */     super(paramSunVolatileImage, paramObject);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  61 */     int i = paramSunVolatileImage.getTransparency();
/*     */     
/*  63 */     D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)paramSunVolatileImage.getGraphicsConfig().getDevice();
/*  64 */     if (i != 1) if (i != 3) break label56; label56: 
/*     */     
/*     */ 
/*     */ 
/*  68 */       this.accelerationEnabled = ((localD3DGraphicsDevice.isCapPresent(2)) || (localD3DGraphicsDevice.isCapPresent(4)));
/*     */   }
/*     */   
/*     */   protected boolean isAccelerationEnabled() {
/*  72 */     return this.accelerationEnabled;
/*     */   }
/*     */   
/*  75 */   public void setAccelerationEnabled(boolean paramBoolean) { this.accelerationEnabled = paramBoolean; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SurfaceData initAcceleratedSurface()
/*     */   {
/*  84 */     Component localComponent = this.vImg.getComponent();
/*     */     
/*  86 */     WComponentPeer localWComponentPeer = localComponent != null ? (WComponentPeer)localComponent.getPeer() : null;
/*     */     D3DSurfaceData localD3DSurfaceData;
/*     */     try {
/*  89 */       boolean bool = false;
/*  90 */       if ((this.context instanceof Boolean)) {
/*  91 */         bool = ((Boolean)this.context).booleanValue();
/*     */       }
/*     */       
/*  94 */       if (bool)
/*     */       {
/*  96 */         localD3DSurfaceData = D3DSurfaceData.createData(localWComponentPeer, this.vImg);
/*     */       }
/*     */       else {
/*  99 */         D3DGraphicsConfig localD3DGraphicsConfig = (D3DGraphicsConfig)this.vImg.getGraphicsConfig();
/* 100 */         ColorModel localColorModel = localD3DGraphicsConfig.getColorModel(this.vImg.getTransparency());
/* 101 */         int i = this.vImg.getForcedAccelSurfaceType();
/*     */         
/*     */ 
/* 104 */         if (i == 0) {
/* 105 */           i = 5;
/*     */         }
/* 107 */         localD3DSurfaceData = D3DSurfaceData.createData(localD3DGraphicsConfig, this.vImg
/* 108 */           .getWidth(), this.vImg
/* 109 */           .getHeight(), localColorModel, this.vImg, i);
/*     */       }
/*     */     }
/*     */     catch (NullPointerException localNullPointerException)
/*     */     {
/* 114 */       localD3DSurfaceData = null;
/*     */     } catch (OutOfMemoryError localOutOfMemoryError) {
/* 116 */       localD3DSurfaceData = null;
/*     */     } catch (InvalidPipeException localInvalidPipeException) {
/* 118 */       localD3DSurfaceData = null;
/*     */     }
/*     */     
/* 121 */     return localD3DSurfaceData;
/*     */   }
/*     */   
/*     */   protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration) {
/* 125 */     return (paramGraphicsConfiguration == null) || (paramGraphicsConfiguration == this.vImg.getGraphicsConfig());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void setRestoreCountdown(int paramInt)
/*     */   {
/* 136 */     this.restoreCountdown = paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void restoreAcceleratedSurface()
/*     */   {
/* 145 */     synchronized (this) {
/* 146 */       if (this.restoreCountdown > 0) {
/* 147 */         this.restoreCountdown -= 1;
/* 148 */         throw new InvalidPipeException("Will attempt to restore surface  in " + this.restoreCountdown);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 154 */     ??? = initAcceleratedSurface();
/* 155 */     if (??? != null) {
/* 156 */       this.sdAccel = ((SurfaceData)???);
/*     */     } else {
/* 158 */       throw new InvalidPipeException("could not restore surface");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SurfaceData restoreContents()
/*     */   {
/* 170 */     acceleratedSurfaceLost();
/* 171 */     return super.restoreContents();
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
/*     */   static void handleVItoScreenOp(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2)
/*     */   {
/* 194 */     if (((paramSurfaceData1 instanceof D3DSurfaceData)) && ((paramSurfaceData2 instanceof GDIWindowSurfaceData)))
/*     */     {
/*     */ 
/* 197 */       D3DSurfaceData localD3DSurfaceData = (D3DSurfaceData)paramSurfaceData1;
/*     */       
/* 199 */       SurfaceManager localSurfaceManager = SurfaceManager.getManager((Image)localD3DSurfaceData.getDestination());
/* 200 */       if ((localSurfaceManager instanceof D3DVolatileSurfaceManager)) {
/* 201 */         D3DVolatileSurfaceManager localD3DVolatileSurfaceManager = (D3DVolatileSurfaceManager)localSurfaceManager;
/* 202 */         if (localD3DVolatileSurfaceManager != null) {
/* 203 */           localD3DSurfaceData.setSurfaceLost(true);
/*     */           
/* 205 */           GDIWindowSurfaceData localGDIWindowSurfaceData = (GDIWindowSurfaceData)paramSurfaceData2;
/* 206 */           WComponentPeer localWComponentPeer = localGDIWindowSurfaceData.getPeer();
/* 207 */           if (D3DScreenUpdateManager.canUseD3DOnScreen(localWComponentPeer, 
/* 208 */             (Win32GraphicsConfig)localWComponentPeer.getGraphicsConfiguration(), localWComponentPeer
/* 209 */             .getBackBuffersNum()))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 215 */             localD3DVolatileSurfaceManager.setRestoreCountdown(10);
/*     */           } else {
/* 217 */             localD3DVolatileSurfaceManager.setAccelerationEnabled(false);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void initContents()
/*     */   {
/* 226 */     if (this.vImg.getForcedAccelSurfaceType() != 3) {
/* 227 */       super.initContents();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\d3d\D3DVolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
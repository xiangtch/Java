/*     */ package sun.java2d.d3d;
/*     */ 
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.BufferedImageOp;
/*     */ import java.awt.image.ConvolveOp;
/*     */ import java.awt.image.LookupOp;
/*     */ import java.awt.image.RescaleOp;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.pipe.BufferedBufImgOps;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class D3DBufImgOps
/*     */   extends BufferedBufImgOps
/*     */ {
/*     */   static boolean renderImageWithOp(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
/*     */   {
/*  55 */     if ((paramBufferedImageOp instanceof ConvolveOp)) {
/*  56 */       if (!isConvolveOpValid((ConvolveOp)paramBufferedImageOp)) {
/*  57 */         return false;
/*     */       }
/*  59 */     } else if ((paramBufferedImageOp instanceof RescaleOp)) {
/*  60 */       if (!isRescaleOpValid((RescaleOp)paramBufferedImageOp, paramBufferedImage)) {
/*  61 */         return false;
/*     */       }
/*  63 */     } else if ((paramBufferedImageOp instanceof LookupOp)) {
/*  64 */       if (!isLookupOpValid((LookupOp)paramBufferedImageOp, paramBufferedImage)) {
/*  65 */         return false;
/*     */       }
/*     */     }
/*     */     else {
/*  69 */       return false;
/*     */     }
/*     */     
/*  72 */     SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*  73 */     if ((!(localSurfaceData1 instanceof D3DSurfaceData)) || (paramSunGraphics2D.interpolationType == 3) || (paramSunGraphics2D.compositeState > 1))
/*     */     {
/*     */ 
/*     */ 
/*  77 */       return false;
/*     */     }
/*     */     
/*     */ 
/*  81 */     SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramBufferedImage, 0, CompositeType.SrcOver, null);
/*     */     
/*  83 */     if (!(localSurfaceData2 instanceof D3DSurfaceData))
/*     */     {
/*     */ 
/*  86 */       localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramBufferedImage, 0, CompositeType.SrcOver, null);
/*     */       
/*  88 */       if (!(localSurfaceData2 instanceof D3DSurfaceData)) {
/*  89 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  95 */     D3DSurfaceData localD3DSurfaceData = (D3DSurfaceData)localSurfaceData2;
/*     */     
/*  97 */     D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData.getDeviceConfiguration().getDevice();
/*  98 */     if ((localD3DSurfaceData.getType() != 3) || 
/*  99 */       (!localD3DGraphicsDevice.isCapPresent(65536)))
/*     */     {
/* 101 */       return false;
/*     */     }
/*     */     
/* 104 */     int i = paramBufferedImage.getWidth();
/* 105 */     int j = paramBufferedImage.getHeight();
/* 106 */     D3DBlitLoops.IsoBlit(localSurfaceData2, localSurfaceData1, paramBufferedImage, paramBufferedImageOp, paramSunGraphics2D.composite, paramSunGraphics2D
/*     */     
/* 108 */       .getCompClip(), paramSunGraphics2D.transform, paramSunGraphics2D.interpolationType, 0, 0, i, j, paramInt1, paramInt2, paramInt1 + i, paramInt2 + j, true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 114 */     return true;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\d3d\D3DBufImgOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
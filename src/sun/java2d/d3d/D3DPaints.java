/*     */ package sun.java2d.d3d;
/*     */ 
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.LinearGradientPaint;
/*     */ import java.awt.MultipleGradientPaint;
/*     */ import java.awt.MultipleGradientPaint.ColorSpaceType;
/*     */ import java.awt.MultipleGradientPaint.CycleMethod;
/*     */ import java.awt.TexturePaint;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class D3DPaints
/*     */ {
/*  48 */   private static Map<Integer, D3DPaints> impls = new HashMap(4, 1.0F);
/*     */   
/*     */   static
/*     */   {
/*  52 */     impls.put(Integer.valueOf(2), new Gradient(null));
/*  53 */     impls.put(Integer.valueOf(3), new LinearGradient(null));
/*  54 */     impls.put(Integer.valueOf(4), new RadialGradient(null));
/*  55 */     impls.put(Integer.valueOf(5), new Texture(null));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   abstract boolean isPaintValid(SunGraphics2D paramSunGraphics2D);
/*     */   
/*     */ 
/*     */ 
/*     */   static boolean isValid(SunGraphics2D paramSunGraphics2D)
/*     */   {
/*  66 */     D3DPaints localD3DPaints = (D3DPaints)impls.get(Integer.valueOf(paramSunGraphics2D.paintState));
/*  67 */     return (localD3DPaints != null) && (localD3DPaints.isPaintValid(paramSunGraphics2D));
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
/*     */   private static class Gradient
/*     */     extends D3DPaints
/*     */   {
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/*  90 */       D3DSurfaceData localD3DSurfaceData = (D3DSurfaceData)paramSunGraphics2D.surfaceData;
/*     */       
/*  92 */       D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData.getDeviceConfiguration().getDevice();
/*  93 */       return localD3DGraphicsDevice.isCapPresent(65536);
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
/*     */   private static class Texture
/*     */     extends D3DPaints
/*     */   {
/*     */     public boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 114 */       TexturePaint localTexturePaint = (TexturePaint)paramSunGraphics2D.paint;
/* 115 */       D3DSurfaceData localD3DSurfaceData1 = (D3DSurfaceData)paramSunGraphics2D.surfaceData;
/* 116 */       BufferedImage localBufferedImage = localTexturePaint.getImage();
/*     */       
/*     */ 
/*     */ 
/* 120 */       D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData1.getDeviceConfiguration().getDevice();
/* 121 */       int i = localBufferedImage.getWidth();
/* 122 */       int j = localBufferedImage.getHeight();
/* 123 */       if ((!localD3DGraphicsDevice.isCapPresent(32)) && (
/* 124 */         ((i & i - 1) != 0) || ((j & j - 1) != 0))) {
/* 125 */         return false;
/*     */       }
/*     */       
/*     */ 
/* 129 */       if ((!localD3DGraphicsDevice.isCapPresent(64)) && (i != j))
/*     */       {
/* 131 */         return false;
/*     */       }
/*     */       
/*     */ 
/* 135 */       SurfaceData localSurfaceData = localD3DSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
/*     */       
/* 137 */       if (!(localSurfaceData instanceof D3DSurfaceData))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 142 */         localSurfaceData = localD3DSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
/*     */         
/* 144 */         if (!(localSurfaceData instanceof D3DSurfaceData)) {
/* 145 */           return false;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 150 */       D3DSurfaceData localD3DSurfaceData2 = (D3DSurfaceData)localSurfaceData;
/* 151 */       if (localD3DSurfaceData2.getType() != 3) {
/* 152 */         return false;
/*     */       }
/*     */       
/* 155 */       return true;
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
/*     */   private static abstract class MultiGradient
/*     */     extends D3DPaints
/*     */   {
/*     */     public static final int MULTI_MAX_FRACTIONS_D3D = 8;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 185 */       MultipleGradientPaint localMultipleGradientPaint = (MultipleGradientPaint)paramSunGraphics2D.paint;
/*     */       
/*     */ 
/* 188 */       if (localMultipleGradientPaint.getFractions().length > 8) {
/* 189 */         return false;
/*     */       }
/*     */       
/* 192 */       D3DSurfaceData localD3DSurfaceData = (D3DSurfaceData)paramSunGraphics2D.surfaceData;
/*     */       
/* 194 */       D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData.getDeviceConfiguration().getDevice();
/* 195 */       if (!localD3DGraphicsDevice.isCapPresent(65536)) {
/* 196 */         return false;
/*     */       }
/* 198 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class LinearGradient
/*     */     extends MultiGradient
/*     */   {
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 209 */       LinearGradientPaint localLinearGradientPaint = (LinearGradientPaint)paramSunGraphics2D.paint;
/*     */       
/* 211 */       if ((localLinearGradientPaint.getFractions().length == 2) && 
/* 212 */         (localLinearGradientPaint.getCycleMethod() != CycleMethod.REPEAT) &&
/* 213 */         (localLinearGradientPaint.getColorSpace() != ColorSpaceType.LINEAR_RGB))
/*     */       {
/* 215 */         D3DSurfaceData localD3DSurfaceData = (D3DSurfaceData)paramSunGraphics2D.surfaceData;
/*     */         
/* 217 */         D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData.getDeviceConfiguration().getDevice();
/* 218 */         if (localD3DGraphicsDevice.isCapPresent(65536))
/*     */         {
/*     */ 
/* 221 */           return true;
/*     */         }
/*     */       }
/*     */       
/* 225 */       return super.isPaintValid(paramSunGraphics2D);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class RadialGradient
/*     */     extends MultiGradient
/*     */   {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\d3d\D3DPaints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
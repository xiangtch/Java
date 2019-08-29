/*     */ package sun.java2d.opengl;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class OGLPaints
/*     */ {
/*  50 */   private static Map<Integer, OGLPaints> impls = new HashMap(4, 1.0F);
/*     */   
/*     */   static
/*     */   {
/*  54 */     impls.put(Integer.valueOf(2), new Gradient(null));
/*  55 */     impls.put(Integer.valueOf(3), new LinearGradient(null));
/*  56 */     impls.put(Integer.valueOf(4), new RadialGradient(null));
/*  57 */     impls.put(Integer.valueOf(5), new Texture(null));
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
/*  68 */     OGLPaints localOGLPaints = (OGLPaints)impls.get(Integer.valueOf(paramSunGraphics2D.paintState));
/*  69 */     return (localOGLPaints != null) && (localOGLPaints.isPaintValid(paramSunGraphics2D));
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
/*     */   private static class Gradient
/*     */     extends OGLPaints
/*     */   {
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/*  90 */       return true;
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
/*     */   private static class Texture
/*     */     extends OGLPaints
/*     */   {
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 110 */       TexturePaint localTexturePaint = (TexturePaint)paramSunGraphics2D.paint;
/* 111 */       OGLSurfaceData localOGLSurfaceData1 = (OGLSurfaceData)paramSunGraphics2D.surfaceData;
/* 112 */       BufferedImage localBufferedImage = localTexturePaint.getImage();
/*     */       
/*     */ 
/* 115 */       if (!localOGLSurfaceData1.isTexNonPow2Available()) {
/* 116 */         int i = localBufferedImage.getWidth();
/* 117 */         int j = localBufferedImage.getHeight();
/*     */         
/*     */ 
/* 120 */         if (((i & i - 1) != 0) || ((j & j - 1) != 0)) {
/* 121 */           return false;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 126 */       SurfaceData localSurfaceData = localOGLSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
/*     */       
/*     */ 
/* 129 */       if (!(localSurfaceData instanceof OGLSurfaceData))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 134 */         localSurfaceData = localOGLSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
/*     */         
/*     */ 
/* 137 */         if (!(localSurfaceData instanceof OGLSurfaceData)) {
/* 138 */           return false;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 143 */       OGLSurfaceData localOGLSurfaceData2 = (OGLSurfaceData)localSurfaceData;
/* 144 */       if (localOGLSurfaceData2.getType() != 3) {
/* 145 */         return false;
/*     */       }
/*     */       
/* 148 */       return true;
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
/*     */     extends OGLPaints
/*     */   {
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 167 */       MultipleGradientPaint localMultipleGradientPaint = (MultipleGradientPaint)paramSunGraphics2D.paint;
/*     */       
/*     */ 
/* 170 */       if (localMultipleGradientPaint.getFractions().length > 12) {
/* 171 */         return false;
/*     */       }
/*     */       
/* 174 */       OGLSurfaceData localOGLSurfaceData = (OGLSurfaceData)paramSunGraphics2D.surfaceData;
/* 175 */       OGLGraphicsConfig localOGLGraphicsConfig = localOGLSurfaceData.getOGLGraphicsConfig();
/* 176 */       if (!localOGLGraphicsConfig.isCapPresent(524288)) {
/* 177 */         return false;
/*     */       }
/*     */       
/* 180 */       return true;
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
/* 191 */       LinearGradientPaint localLinearGradientPaint = (LinearGradientPaint)paramSunGraphics2D.paint;
/*     */       
/* 193 */       if ((localLinearGradientPaint.getFractions().length == 2) && 
/* 194 */         (localLinearGradientPaint.getCycleMethod() != CycleMethod.REPEAT) &&
/* 195 */         (localLinearGradientPaint.getColorSpace() != ColorSpaceType.LINEAR_RGB))
/*     */       {
/*     */ 
/*     */ 
/* 199 */         return true;
/*     */       }
/*     */       
/* 202 */       return super.isPaintValid(paramSunGraphics2D);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class RadialGradient
/*     */     extends MultiGradient
/*     */   {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\opengl\OGLPaints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
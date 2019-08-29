/*     */ package sun.java2d.pipe;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.LinearGradientPaint;
/*     */ import java.awt.MultipleGradientPaint.ColorSpaceType;
/*     */ import java.awt.MultipleGradientPaint.CycleMethod;
/*     */ import java.awt.Paint;
/*     */ import java.awt.RadialGradientPaint;
/*     */ import java.awt.TexturePaint;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.NoninvertibleTransformException;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import sun.awt.image.PixelConverter;
/*     */ import sun.awt.image.PixelConverter.ArgbPre;
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
/*     */ public class BufferedPaints
/*     */ {
/*     */   public static final int MULTI_MAX_FRACTIONS = 12;
/*     */   
/*     */   static void setPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, Paint paramPaint, int paramInt)
/*     */   {
/*  56 */     if (paramSunGraphics2D.paintState <= 1) {
/*  57 */       setColor(paramRenderQueue, paramSunGraphics2D.pixel);
/*     */     } else {
/*  59 */       boolean bool = (paramInt & 0x2) != 0;
/*  60 */       switch (paramSunGraphics2D.paintState) {
/*     */       case 2: 
/*  62 */         setGradientPaint(paramRenderQueue, paramSunGraphics2D, (GradientPaint)paramPaint, bool);
/*     */         
/*  64 */         break;
/*     */       case 3: 
/*  66 */         setLinearGradientPaint(paramRenderQueue, paramSunGraphics2D, (LinearGradientPaint)paramPaint, bool);
/*     */         
/*  68 */         break;
/*     */       case 4: 
/*  70 */         setRadialGradientPaint(paramRenderQueue, paramSunGraphics2D, (RadialGradientPaint)paramPaint, bool);
/*     */         
/*  72 */         break;
/*     */       case 5: 
/*  74 */         setTexturePaint(paramRenderQueue, paramSunGraphics2D, (TexturePaint)paramPaint, bool);
/*     */         
/*  76 */         break;
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static void resetPaint(RenderQueue paramRenderQueue)
/*     */   {
/*  85 */     paramRenderQueue.ensureCapacity(4);
/*  86 */     RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
/*  87 */     localRenderBuffer.putInt(100);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void setColor(RenderQueue paramRenderQueue, int paramInt)
/*     */   {
/*  94 */     paramRenderQueue.ensureCapacity(8);
/*  95 */     RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
/*  96 */     localRenderBuffer.putInt(101);
/*  97 */     localRenderBuffer.putInt(paramInt);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void setGradientPaint(RenderQueue paramRenderQueue, AffineTransform paramAffineTransform, Color paramColor1, Color paramColor2, Point2D paramPoint2D1, Point2D paramPoint2D2, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 164 */     PixelConverter localPixelConverter = ArgbPre.instance;
/* 165 */     int i = localPixelConverter.rgbToPixel(paramColor1.getRGB(), null);
/* 166 */     int j = localPixelConverter.rgbToPixel(paramColor2.getRGB(), null);
/*     */     
/*     */ 
/* 169 */     double d1 = paramPoint2D1.getX();
/* 170 */     double d2 = paramPoint2D1.getY();
/* 171 */     paramAffineTransform.translate(d1, d2);
/*     */     
/* 173 */     d1 = paramPoint2D2.getX() - d1;
/* 174 */     d2 = paramPoint2D2.getY() - d2;
/* 175 */     double d3 = Math.sqrt(d1 * d1 + d2 * d2);
/* 176 */     paramAffineTransform.rotate(d1, d2);
/*     */     
/* 178 */     paramAffineTransform.scale(2.0D * d3, 1.0D);
/*     */     
/* 180 */     paramAffineTransform.translate(-0.25D, 0.0D);
/*     */     double d4;
/*     */     double d5;
/*     */     double d6;
/*     */     try {
/* 185 */       paramAffineTransform.invert();
/* 186 */       d4 = paramAffineTransform.getScaleX();
/* 187 */       d5 = paramAffineTransform.getShearX();
/* 188 */       d6 = paramAffineTransform.getTranslateX();
/*     */     } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 190 */       d4 = d5 = d6 = 0.0D;
/*     */     }
/*     */     
/*     */ 
/* 194 */     paramRenderQueue.ensureCapacityAndAlignment(44, 12);
/* 195 */     RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
/* 196 */     localRenderBuffer.putInt(102);
/* 197 */     localRenderBuffer.putInt(paramBoolean2 ? 1 : 0);
/* 198 */     localRenderBuffer.putInt(paramBoolean1 ? 1 : 0);
/* 199 */     localRenderBuffer.putDouble(d4).putDouble(d5).putDouble(d6);
/* 200 */     localRenderBuffer.putInt(i).putInt(j);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void setGradientPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, GradientPaint paramGradientPaint, boolean paramBoolean)
/*     */   {
/* 208 */     setGradientPaint(paramRenderQueue, (AffineTransform)paramSunGraphics2D.transform.clone(), paramGradientPaint
/* 209 */       .getColor1(), paramGradientPaint.getColor2(), paramGradientPaint
/* 210 */       .getPoint1(), paramGradientPaint.getPoint2(), paramGradientPaint
/* 211 */       .isCyclic(), paramBoolean);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void setTexturePaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, TexturePaint paramTexturePaint, boolean paramBoolean)
/*     */   {
/* 249 */     BufferedImage localBufferedImage = paramTexturePaint.getImage();
/* 250 */     SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*     */     
/* 252 */     SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
/*     */     
/* 254 */     int i = paramSunGraphics2D.interpolationType != 1 ? 1 : 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 259 */     AffineTransform localAffineTransform = (AffineTransform)paramSunGraphics2D.transform.clone();
/* 260 */     Rectangle2D localRectangle2D = paramTexturePaint.getAnchorRect();
/* 261 */     localAffineTransform.translate(localRectangle2D.getX(), localRectangle2D.getY());
/* 262 */     localAffineTransform.scale(localRectangle2D.getWidth(), localRectangle2D.getHeight());
/*     */     double d1;
/*     */     double d2;
/*     */     double d3;
/* 266 */     double d4; double d5; double d6; try { localAffineTransform.invert();
/* 267 */       d1 = localAffineTransform.getScaleX();
/* 268 */       d2 = localAffineTransform.getShearX();
/* 269 */       d3 = localAffineTransform.getTranslateX();
/* 270 */       d4 = localAffineTransform.getShearY();
/* 271 */       d5 = localAffineTransform.getScaleY();
/* 272 */       d6 = localAffineTransform.getTranslateY();
/*     */     } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 274 */       d1 = d2 = d3 = d4 = d5 = d6 = 0.0D;
/*     */     }
/*     */     
/*     */ 
/* 278 */     paramRenderQueue.ensureCapacityAndAlignment(68, 12);
/* 279 */     RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
/* 280 */     localRenderBuffer.putInt(105);
/* 281 */     localRenderBuffer.putInt(paramBoolean ? 1 : 0);
/* 282 */     localRenderBuffer.putInt(i != 0 ? 1 : 0);
/* 283 */     localRenderBuffer.putLong(localSurfaceData2.getNativeOps());
/* 284 */     localRenderBuffer.putDouble(d1).putDouble(d2).putDouble(d3);
/* 285 */     localRenderBuffer.putDouble(d4).putDouble(d5).putDouble(d6);
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
/*     */   public static int convertSRGBtoLinearRGB(int paramInt)
/*     */   {
/* 315 */     float f1 = paramInt / 255.0F;
/* 316 */     float f2; if (f1 <= 0.04045F) {
/* 317 */       f2 = f1 / 12.92F;
/*     */     } else {
/* 319 */       f2 = (float)Math.pow((f1 + 0.055D) / 1.055D, 2.4D);
/*     */     }
/*     */     
/* 322 */     return Math.round(f2 * 255.0F);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int colorToIntArgbPrePixel(Color paramColor, boolean paramBoolean)
/*     */   {
/* 331 */     int i = paramColor.getRGB();
/* 332 */     if ((!paramBoolean) && (i >> 24 == -1)) {
/* 333 */       return i;
/*     */     }
/* 335 */     int j = i >>> 24;
/* 336 */     int k = i >> 16 & 0xFF;
/* 337 */     int m = i >> 8 & 0xFF;
/* 338 */     int n = i & 0xFF;
/* 339 */     if (paramBoolean) {
/* 340 */       k = convertSRGBtoLinearRGB(k);
/* 341 */       m = convertSRGBtoLinearRGB(m);
/* 342 */       n = convertSRGBtoLinearRGB(n);
/*     */     }
/* 344 */     int i1 = j + (j >> 7);
/* 345 */     k = k * i1 >> 8;
/* 346 */     m = m * i1 >> 8;
/* 347 */     n = n * i1 >> 8;
/* 348 */     return j << 24 | k << 16 | m << 8 | n;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int[] convertToIntArgbPrePixels(Color[] paramArrayOfColor, boolean paramBoolean)
/*     */   {
/* 360 */     int[] arrayOfInt = new int[paramArrayOfColor.length];
/* 361 */     for (int i = 0; i < paramArrayOfColor.length; i++) {
/* 362 */       arrayOfInt[i] = colorToIntArgbPrePixel(paramArrayOfColor[i], paramBoolean);
/*     */     }
/* 364 */     return arrayOfInt;
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
/*     */   private static void setLinearGradientPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, LinearGradientPaint paramLinearGradientPaint, boolean paramBoolean)
/*     */   {
/* 394 */     boolean bool1 = paramLinearGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB;
/* 395 */     Color[] arrayOfColor = paramLinearGradientPaint.getColors();
/* 396 */     int i = arrayOfColor.length;
/* 397 */     Point2D localPoint2D1 = paramLinearGradientPaint.getStartPoint();
/* 398 */     Point2D localPoint2D2 = paramLinearGradientPaint.getEndPoint();
/* 399 */     AffineTransform localAffineTransform = paramLinearGradientPaint.getTransform();
/* 400 */     localAffineTransform.preConcatenate(paramSunGraphics2D.transform);
/*     */     
/* 402 */     if ((!bool1) && (i == 2) && 
/* 403 */       (paramLinearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT))
/*     */     {
/*     */ 
/*     */ 
/* 407 */       boolean bool2 = paramLinearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.NO_CYCLE;
/* 408 */       setGradientPaint(paramRenderQueue, localAffineTransform, arrayOfColor[0], arrayOfColor[1], localPoint2D1, localPoint2D2, bool2, paramBoolean);
/*     */       
/*     */ 
/*     */ 
/* 412 */       return;
/*     */     }
/*     */     
/* 415 */     int j = paramLinearGradientPaint.getCycleMethod().ordinal();
/* 416 */     float[] arrayOfFloat = paramLinearGradientPaint.getFractions();
/* 417 */     int[] arrayOfInt = convertToIntArgbPrePixels(arrayOfColor, bool1);
/*     */     
/*     */ 
/* 420 */     double d1 = localPoint2D1.getX();
/* 421 */     double d2 = localPoint2D1.getY();
/* 422 */     localAffineTransform.translate(d1, d2);
/*     */     
/* 424 */     d1 = localPoint2D2.getX() - d1;
/* 425 */     d2 = localPoint2D2.getY() - d2;
/* 426 */     double d3 = Math.sqrt(d1 * d1 + d2 * d2);
/* 427 */     localAffineTransform.rotate(d1, d2);
/*     */     
/* 429 */     localAffineTransform.scale(d3, 1.0D);
/*     */     float f1;
/*     */     float f2;
/*     */     float f3;
/*     */     try {
/* 434 */       localAffineTransform.invert();
/* 435 */       f1 = (float)localAffineTransform.getScaleX();
/* 436 */       f2 = (float)localAffineTransform.getShearX();
/* 437 */       f3 = (float)localAffineTransform.getTranslateX();
/*     */     } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 439 */       f1 = f2 = f3 = 0.0F;
/*     */     }
/*     */     
/*     */ 
/* 443 */     paramRenderQueue.ensureCapacity(32 + i * 4 * 2);
/* 444 */     RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
/* 445 */     localRenderBuffer.putInt(103);
/* 446 */     localRenderBuffer.putInt(paramBoolean ? 1 : 0);
/* 447 */     localRenderBuffer.putInt(bool1 ? 1 : 0);
/* 448 */     localRenderBuffer.putInt(j);
/* 449 */     localRenderBuffer.putInt(i);
/* 450 */     localRenderBuffer.putFloat(f1);
/* 451 */     localRenderBuffer.putFloat(f2);
/* 452 */     localRenderBuffer.putFloat(f3);
/* 453 */     localRenderBuffer.put(arrayOfFloat);
/* 454 */     localRenderBuffer.put(arrayOfInt);
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
/*     */   private static void setRadialGradientPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, RadialGradientPaint paramRadialGradientPaint, boolean paramBoolean)
/*     */   {
/* 477 */     boolean bool = paramRadialGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB;
/* 478 */     int i = paramRadialGradientPaint.getCycleMethod().ordinal();
/* 479 */     float[] arrayOfFloat = paramRadialGradientPaint.getFractions();
/* 480 */     Color[] arrayOfColor = paramRadialGradientPaint.getColors();
/* 481 */     int j = arrayOfColor.length;
/* 482 */     int[] arrayOfInt = convertToIntArgbPrePixels(arrayOfColor, bool);
/* 483 */     Point2D localPoint2D1 = paramRadialGradientPaint.getCenterPoint();
/* 484 */     Point2D localPoint2D2 = paramRadialGradientPaint.getFocusPoint();
/* 485 */     float f = paramRadialGradientPaint.getRadius();
/*     */     
/*     */ 
/* 488 */     double d1 = localPoint2D1.getX();
/* 489 */     double d2 = localPoint2D1.getY();
/* 490 */     double d3 = localPoint2D2.getX();
/* 491 */     double d4 = localPoint2D2.getY();
/*     */     
/*     */ 
/* 494 */     AffineTransform localAffineTransform = paramRadialGradientPaint.getTransform();
/* 495 */     localAffineTransform.preConcatenate(paramSunGraphics2D.transform);
/* 496 */     localPoint2D2 = localAffineTransform.transform(localPoint2D2, localPoint2D2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 501 */     localAffineTransform.translate(d1, d2);
/* 502 */     localAffineTransform.rotate(d3 - d1, d4 - d2);
/* 503 */     localAffineTransform.scale(f, f);
/*     */     
/*     */     try
/*     */     {
/* 507 */       localAffineTransform.invert();
/*     */     } catch (Exception localException) {
/* 509 */       localAffineTransform.setToScale(0.0D, 0.0D);
/*     */     }
/* 511 */     localPoint2D2 = localAffineTransform.transform(localPoint2D2, localPoint2D2);
/*     */     
/*     */ 
/*     */ 
/* 515 */     d3 = Math.min(localPoint2D2.getX(), 0.99D);
/*     */     
/*     */ 
/* 518 */     paramRenderQueue.ensureCapacity(48 + j * 4 * 2);
/* 519 */     RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
/* 520 */     localRenderBuffer.putInt(104);
/* 521 */     localRenderBuffer.putInt(paramBoolean ? 1 : 0);
/* 522 */     localRenderBuffer.putInt(bool ? 1 : 0);
/* 523 */     localRenderBuffer.putInt(j);
/* 524 */     localRenderBuffer.putInt(i);
/* 525 */     localRenderBuffer.putFloat((float)localAffineTransform.getScaleX());
/* 526 */     localRenderBuffer.putFloat((float)localAffineTransform.getShearX());
/* 527 */     localRenderBuffer.putFloat((float)localAffineTransform.getTranslateX());
/* 528 */     localRenderBuffer.putFloat((float)localAffineTransform.getShearY());
/* 529 */     localRenderBuffer.putFloat((float)localAffineTransform.getScaleY());
/* 530 */     localRenderBuffer.putFloat((float)localAffineTransform.getTranslateY());
/* 531 */     localRenderBuffer.putFloat((float)d3);
/* 532 */     localRenderBuffer.put(arrayOfFloat);
/* 533 */     localRenderBuffer.put(arrayOfInt);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\BufferedPaints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*      */ package sun.java2d.pipe;
/*      */ 
/*      */ import java.awt.AlphaComposite;
/*      */ import java.awt.Color;
/*      */ import java.awt.Image;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.NoninvertibleTransformException;
/*      */ import java.awt.image.AffineTransformOp;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.BufferedImageOp;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.VolatileImage;
/*      */ import sun.awt.image.BytePackedRaster;
/*      */ import sun.awt.image.ImageRepresentation;
/*      */ import sun.awt.image.SurfaceManager;
/*      */ import sun.awt.image.ToolkitImage;
/*      */ import sun.java2d.InvalidPipeException;
/*      */ import sun.java2d.SunGraphics2D;
/*      */ import sun.java2d.SurfaceData;
/*      */ import sun.java2d.loops.Blit;
/*      */ import sun.java2d.loops.BlitBg;
/*      */ import sun.java2d.loops.CompositeType;
/*      */ import sun.java2d.loops.MaskBlit;
/*      */ import sun.java2d.loops.ScaledBlit;
/*      */ import sun.java2d.loops.SurfaceType;
/*      */ import sun.java2d.loops.TransformHelper;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DrawImage
/*      */   implements DrawImagePipe
/*      */ {
/*      */   private static final double MAX_TX_ERROR = 1.0E-4D;
/*      */   
/*      */   public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor)
/*      */   {
/*   64 */     int i = paramImage.getWidth(null);
/*   65 */     int j = paramImage.getHeight(null);
/*   66 */     if (isSimpleTranslate(paramSunGraphics2D)) {
/*   67 */       return renderImageCopy(paramSunGraphics2D, paramImage, paramColor, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, 0, 0, i, j);
/*      */     }
/*      */     
/*      */ 
/*   71 */     AffineTransform localAffineTransform = paramSunGraphics2D.transform;
/*   72 */     if ((paramInt1 | paramInt2) != 0) {
/*   73 */       localAffineTransform = new AffineTransform(localAffineTransform);
/*   74 */       localAffineTransform.translate(paramInt1, paramInt2);
/*      */     }
/*   76 */     transformImage(paramSunGraphics2D, paramImage, localAffineTransform, paramSunGraphics2D.interpolationType, 0, 0, i, j, paramColor);
/*      */     
/*   78 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor)
/*      */   {
/*   85 */     if (isSimpleTranslate(paramSunGraphics2D)) {
/*   86 */       return renderImageCopy(paramSunGraphics2D, paramImage, paramColor, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     }
/*      */     
/*      */ 
/*   90 */     scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4, paramInt3 + paramInt5, paramInt4 + paramInt6, paramColor);
/*      */     
/*   92 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
/*      */   {
/*   99 */     int i = paramImage.getWidth(null);
/*  100 */     int j = paramImage.getHeight(null);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  105 */     if ((paramInt3 > 0) && (paramInt4 > 0) && (isSimpleTranslate(paramSunGraphics2D))) {
/*  106 */       double d1 = paramInt1 + paramSunGraphics2D.transX;
/*  107 */       double d2 = paramInt2 + paramSunGraphics2D.transY;
/*  108 */       double d3 = d1 + paramInt3;
/*  109 */       double d4 = d2 + paramInt4;
/*  110 */       if (renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramSunGraphics2D.interpolationType, 0, 0, i, j, d1, d2, d3, d4))
/*      */       {
/*      */ 
/*      */ 
/*  114 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  118 */     AffineTransform localAffineTransform = paramSunGraphics2D.transform;
/*  119 */     if (((paramInt1 | paramInt2) != 0) || (paramInt3 != i) || (paramInt4 != j)) {
/*  120 */       localAffineTransform = new AffineTransform(localAffineTransform);
/*  121 */       localAffineTransform.translate(paramInt1, paramInt2);
/*  122 */       localAffineTransform.scale(paramInt3 / i, paramInt4 / j);
/*      */     }
/*  124 */     transformImage(paramSunGraphics2D, paramImage, localAffineTransform, paramSunGraphics2D.interpolationType, 0, 0, i, j, paramColor);
/*      */     
/*  126 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, AffineTransform paramAffineTransform, int paramInt3)
/*      */   {
/*  140 */     int i = paramAffineTransform.getType();
/*  141 */     int j = paramImage.getWidth(null);
/*  142 */     int k = paramImage.getHeight(null);
/*      */     
/*      */     int m;
/*  145 */     if ((paramSunGraphics2D.transformState <= 2) && ((i == 0) || (i == 1)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  152 */       double d1 = paramAffineTransform.getTranslateX();
/*  153 */       double d2 = paramAffineTransform.getTranslateY();
/*  154 */       d1 += paramSunGraphics2D.transform.getTranslateX();
/*  155 */       d2 += paramSunGraphics2D.transform.getTranslateY();
/*  156 */       int n = (int)Math.floor(d1 + 0.5D);
/*  157 */       int i1 = (int)Math.floor(d2 + 0.5D);
/*  158 */       if ((paramInt3 == 1) || (
/*  159 */         (closeToInteger(n, d1)) && (closeToInteger(i1, d2))))
/*      */       {
/*  161 */         renderImageCopy(paramSunGraphics2D, paramImage, null, paramInt1 + n, paramInt2 + i1, 0, 0, j, k);
/*  162 */         return;
/*      */       }
/*  164 */       m = 0;
/*  165 */     } else if ((paramSunGraphics2D.transformState <= 3) && ((i & 0x78) == 0))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  177 */       localObject = new double[] { 0.0D, 0.0D, j, k };
/*      */       
/*      */ 
/*  180 */       paramAffineTransform.transform((double[])localObject, 0, (double[])localObject, 0, 2);
/*  181 */       localObject[0] += paramInt1;
/*  182 */       localObject[1] += paramInt2;
/*  183 */       localObject[2] += paramInt1;
/*  184 */       localObject[3] += paramInt2;
/*  185 */       paramSunGraphics2D.transform.transform((double[])localObject, 0, (double[])localObject, 0, 2);
/*      */       
/*  187 */       if (tryCopyOrScale(paramSunGraphics2D, paramImage, 0, 0, j, k, null, paramInt3, (double[])localObject))
/*      */       {
/*      */ 
/*  190 */         return;
/*      */       }
/*  192 */       m = 0;
/*      */     } else {
/*  194 */       m = 1;
/*      */     }
/*      */     
/*      */ 
/*  198 */     Object localObject = new AffineTransform(paramSunGraphics2D.transform);
/*  199 */     ((AffineTransform)localObject).translate(paramInt1, paramInt2);
/*  200 */     ((AffineTransform)localObject).concatenate(paramAffineTransform);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  205 */     if (m != 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  210 */       transformImage(paramSunGraphics2D, paramImage, (AffineTransform)localObject, paramInt3, 0, 0, j, k, null);
/*      */     } else {
/*  212 */       renderImageXform(paramSunGraphics2D, paramImage, (AffineTransform)localObject, paramInt3, 0, 0, j, k, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor)
/*      */   {
/*  244 */     double[] arrayOfDouble = new double[6];
/*      */     
/*      */ 
/*  247 */     arrayOfDouble[2] = (paramInt4 - paramInt2);
/*  248 */     arrayOfDouble[3] = (arrayOfDouble[5] = paramInt5 - paramInt3);
/*  249 */     paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 3);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  256 */     if ((Math.abs(arrayOfDouble[0] - arrayOfDouble[4]) < 1.0E-4D) && 
/*  257 */       (Math.abs(arrayOfDouble[3] - arrayOfDouble[5]) < 1.0E-4D) && 
/*  258 */       (tryCopyOrScale(paramSunGraphics2D, paramImage, paramInt2, paramInt3, paramInt4, paramInt5, paramColor, paramInt1, arrayOfDouble)))
/*      */     {
/*      */ 
/*  261 */       return;
/*      */     }
/*      */     
/*  264 */     renderImageXform(paramSunGraphics2D, paramImage, paramAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramColor);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean tryCopyOrScale(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, int paramInt5, double[] paramArrayOfDouble)
/*      */   {
/*  281 */     double d1 = paramArrayOfDouble[0];
/*  282 */     double d2 = paramArrayOfDouble[1];
/*  283 */     double d3 = paramArrayOfDouble[2];
/*  284 */     double d4 = paramArrayOfDouble[3];
/*  285 */     double d5 = d3 - d1;
/*  286 */     double d6 = d4 - d2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  293 */     if ((d1 < -2.147483648E9D) || (d1 > 2.147483647E9D) || (d2 < -2.147483648E9D) || (d2 > 2.147483647E9D) || (d3 < -2.147483648E9D) || (d3 > 2.147483647E9D) || (d4 < -2.147483648E9D) || (d4 > 2.147483647E9D))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  298 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  302 */     if ((closeToInteger(paramInt3 - paramInt1, d5)) && (closeToInteger(paramInt4 - paramInt2, d6)))
/*      */     {
/*      */ 
/*  305 */       int i = (int)Math.floor(d1 + 0.5D);
/*  306 */       int j = (int)Math.floor(d2 + 0.5D);
/*  307 */       if ((paramInt5 == 1) || (
/*  308 */         (closeToInteger(i, d1)) && (closeToInteger(j, d2))))
/*      */       {
/*  310 */         renderImageCopy(paramSunGraphics2D, paramImage, paramColor, i, j, paramInt1, paramInt2, paramInt3 - paramInt1, paramInt4 - paramInt2);
/*      */         
/*      */ 
/*  313 */         return true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  318 */     if ((d5 > 0.0D) && (d6 > 0.0D) && 
/*  319 */       (renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramInt5, paramInt1, paramInt2, paramInt3, paramInt4, d1, d2, d3, d4)))
/*      */     {
/*      */ 
/*      */ 
/*  323 */       return true;
/*      */     }
/*      */     
/*  326 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   BufferedImage makeBufferedImage(Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  346 */     int i = paramInt4 - paramInt2;
/*  347 */     int j = paramInt5 - paramInt3;
/*  348 */     BufferedImage localBufferedImage = new BufferedImage(i, j, paramInt1);
/*  349 */     SunGraphics2D localSunGraphics2D = (SunGraphics2D)localBufferedImage.createGraphics();
/*  350 */     localSunGraphics2D.setComposite(AlphaComposite.Src);
/*  351 */     localBufferedImage.setAccelerationPriority(0.0F);
/*  352 */     if (paramColor != null) {
/*  353 */       localSunGraphics2D.setColor(paramColor);
/*  354 */       localSunGraphics2D.fillRect(0, 0, i, j);
/*  355 */       localSunGraphics2D.setComposite(AlphaComposite.SrcOver);
/*      */     }
/*  357 */     localSunGraphics2D.copyImage(paramImage, 0, 0, paramInt2, paramInt3, i, j, null, null);
/*  358 */     localSunGraphics2D.dispose();
/*  359 */     return localBufferedImage;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void renderImageXform(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor)
/*      */   {
/*      */     AffineTransform localAffineTransform;
/*      */     
/*      */     try
/*      */     {
/*  369 */       localAffineTransform = paramAffineTransform.createInverse();
/*      */     }
/*      */     catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/*  372 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  384 */     double[] arrayOfDouble = new double[8];
/*      */     
/*      */ 
/*      */ 
/*  388 */     arrayOfDouble[2] = (arrayOfDouble[6] = paramInt4 - paramInt2);
/*  389 */     arrayOfDouble[5] = (arrayOfDouble[7] = paramInt5 - paramInt3);
/*  390 */     paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
/*      */     double d3;
/*  392 */     double d1 = d3 = arrayOfDouble[0];
/*  393 */     double d4; double d2 = d4 = arrayOfDouble[1];
/*  394 */     for (int i = 2; i < arrayOfDouble.length; i += 2) {
/*  395 */       double d5 = arrayOfDouble[i];
/*  396 */       if (d1 > d5) { d1 = d5;
/*  397 */       } else if (d3 < d5) d3 = d5;
/*  398 */       d5 = arrayOfDouble[(i + 1)];
/*  399 */       if (d2 > d5) { d2 = d5;
/*  400 */       } else if (d4 < d5) { d4 = d5;
/*      */       }
/*      */     }
/*  403 */     Region localRegion1 = paramSunGraphics2D.getCompClip();
/*  404 */     int j = Math.max((int)Math.floor(d1), localRegion1.lox);
/*  405 */     int k = Math.max((int)Math.floor(d2), localRegion1.loy);
/*  406 */     int m = Math.min((int)Math.ceil(d3), localRegion1.hix);
/*  407 */     int n = Math.min((int)Math.ceil(d4), localRegion1.hiy);
/*  408 */     if ((m <= j) || (n <= k))
/*      */     {
/*  410 */       return;
/*      */     }
/*      */     
/*  413 */     SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*  414 */     SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  419 */     if (localSurfaceData2 == null) {
/*  420 */       paramImage = getBufferedImage(paramImage);
/*  421 */       localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
/*      */       
/*      */ 
/*      */ 
/*  425 */       if (localSurfaceData2 == null)
/*      */       {
/*  427 */         return;
/*      */       }
/*      */     }
/*      */     
/*  431 */     if (isBgOperation(localSurfaceData2, paramColor))
/*      */     {
/*      */ 
/*      */ 
/*  435 */       paramImage = makeBufferedImage(paramImage, paramColor, 1, paramInt2, paramInt3, paramInt4, paramInt5);
/*      */       
/*      */ 
/*  438 */       paramInt4 -= paramInt2;
/*  439 */       paramInt5 -= paramInt3;
/*  440 */       paramInt2 = paramInt3 = 0;
/*      */       
/*  442 */       localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  448 */     SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
/*  449 */     TransformHelper localTransformHelper = TransformHelper.getFromCache(localSurfaceType1);
/*      */     
/*  451 */     if (localTransformHelper == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  459 */       int i1 = localSurfaceData2.getTransparency() == 1 ? 1 : 2;
/*      */       
/*      */ 
/*  462 */       paramImage = makeBufferedImage(paramImage, null, i1, paramInt2, paramInt3, paramInt4, paramInt5);
/*      */       
/*  464 */       paramInt4 -= paramInt2;
/*  465 */       paramInt5 -= paramInt3;
/*  466 */       paramInt2 = paramInt3 = 0;
/*      */       
/*  468 */       localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, null);
/*      */       
/*      */ 
/*      */ 
/*  472 */       localSurfaceType1 = localSurfaceData2.getSurfaceType();
/*  473 */       localTransformHelper = TransformHelper.getFromCache(localSurfaceType1);
/*      */     }
/*      */     
/*      */ 
/*  477 */     SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
/*  478 */     if (paramSunGraphics2D.compositeState <= 1)
/*      */     {
/*      */ 
/*      */ 
/*  482 */       MaskBlit localMaskBlit1 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, paramSunGraphics2D.imageComp, localSurfaceType2);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  491 */       if (localMaskBlit1.getNativePrim() != 0L)
/*      */       {
/*  493 */         localTransformHelper.Transform(localMaskBlit1, localSurfaceData2, localSurfaceData1, paramSunGraphics2D.composite, localRegion1, localAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, j, k, m, n, null, 0, 0);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  499 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  505 */     int i2 = m - j;
/*  506 */     int i3 = n - k;
/*  507 */     BufferedImage localBufferedImage = new BufferedImage(i2, i3, 3);
/*      */     
/*  509 */     SurfaceData localSurfaceData3 = SurfaceData.getPrimarySurfaceData(localBufferedImage);
/*  510 */     SurfaceType localSurfaceType3 = localSurfaceData3.getSurfaceType();
/*  511 */     MaskBlit localMaskBlit2 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, CompositeType.SrcNoEa, localSurfaceType3);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  528 */     int[] arrayOfInt = new int[i3 * 2 + 2];
/*      */     
/*      */ 
/*      */ 
/*  532 */     localTransformHelper.Transform(localMaskBlit2, localSurfaceData2, localSurfaceData3, AlphaComposite.Src, null, localAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0, i2, i3, arrayOfInt, j, k);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  539 */     Region localRegion2 = Region.getInstance(j, k, m, n, arrayOfInt);
/*  540 */     localRegion1 = localRegion1.getIntersection(localRegion2);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  545 */     Blit localBlit = Blit.getFromCache(localSurfaceType3, paramSunGraphics2D.imageComp, localSurfaceType2);
/*  546 */     localBlit.Blit(localSurfaceData3, localSurfaceData1, paramSunGraphics2D.composite, localRegion1, 0, 0, j, k, i2, i3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean renderImageCopy(SunGraphics2D paramSunGraphics2D, Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*  557 */     Region localRegion = paramSunGraphics2D.getCompClip();
/*  558 */     SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*      */     
/*  560 */     int i = 0;
/*      */     
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/*  566 */       SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 0, paramSunGraphics2D.imageComp, paramColor);
/*      */       
/*      */ 
/*      */ 
/*  570 */       if (localSurfaceData2 == null) {
/*  571 */         return false;
/*      */       }
/*      */       try
/*      */       {
/*  575 */         SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
/*  576 */         SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
/*  577 */         blitSurfaceData(paramSunGraphics2D, localRegion, localSurfaceData2, localSurfaceData1, localSurfaceType1, localSurfaceType2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt5, paramInt6, paramColor);
/*      */         
/*      */ 
/*  580 */         return true;
/*      */       } catch (NullPointerException localNullPointerException) {
/*  582 */         if ((!SurfaceData.isNull(localSurfaceData1)) && 
/*  583 */           (!SurfaceData.isNull(localSurfaceData2)))
/*      */         {
/*      */ 
/*  586 */           throw localNullPointerException;
/*      */         }
/*  588 */         return false;
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException)
/*      */       {
/*      */ 
/*  594 */         i++;
/*  595 */         localRegion = paramSunGraphics2D.getCompClip();
/*  596 */         localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*  597 */         if ((SurfaceData.isNull(localSurfaceData1)) || 
/*  598 */           (SurfaceData.isNull(localSurfaceData2)) || (i > 1))
/*      */         {
/*  600 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean renderImageScale(SunGraphics2D paramSunGraphics2D, Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
/*      */   {
/*  616 */     if (paramInt1 != 1) {
/*  617 */       return false;
/*      */     }
/*      */     
/*  620 */     Region localRegion = paramSunGraphics2D.getCompClip();
/*  621 */     SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*      */     
/*  623 */     int i = 0;
/*      */     
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/*  629 */       SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 3, paramSunGraphics2D.imageComp, paramColor);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  634 */       if ((localSurfaceData2 == null) || (isBgOperation(localSurfaceData2, paramColor))) {
/*  635 */         return false;
/*      */       }
/*      */       try
/*      */       {
/*  639 */         SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
/*  640 */         SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
/*  641 */         return scaleSurfaceData(paramSunGraphics2D, localRegion, localSurfaceData2, localSurfaceData1, localSurfaceType1, localSurfaceType2, paramInt2, paramInt3, paramInt4, paramInt5, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
/*      */ 
/*      */       }
/*      */       catch (NullPointerException localNullPointerException)
/*      */       {
/*  646 */         if (!SurfaceData.isNull(localSurfaceData1))
/*      */         {
/*  648 */           throw localNullPointerException;
/*      */         }
/*  650 */         return false;
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException)
/*      */       {
/*      */ 
/*  656 */         i++;
/*  657 */         localRegion = paramSunGraphics2D.getCompClip();
/*  658 */         localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*  659 */         if ((SurfaceData.isNull(localSurfaceData1)) || 
/*  660 */           (SurfaceData.isNull(localSurfaceData2)) || (i > 1))
/*      */         {
/*  662 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor)
/*      */   {
/*  675 */     int i4 = 0;
/*  676 */     int i5 = 0;
/*  677 */     int i6 = 0;
/*  678 */     int i7 = 0;
/*      */     int i;
/*  680 */     int n; if (paramInt7 > paramInt5) {
/*  681 */       i = paramInt7 - paramInt5;
/*  682 */       n = paramInt5;
/*      */     } else {
/*  684 */       i4 = 1;
/*  685 */       i = paramInt5 - paramInt7;
/*  686 */       n = paramInt7; }
/*      */     int j;
/*  688 */     int i1; if (paramInt8 > paramInt6) {
/*  689 */       j = paramInt8 - paramInt6;
/*  690 */       i1 = paramInt6;
/*      */     } else {
/*  692 */       i5 = 1;
/*  693 */       j = paramInt6 - paramInt8;
/*  694 */       i1 = paramInt8; }
/*      */     int k;
/*  696 */     int i2; if (paramInt3 > paramInt1) {
/*  697 */       k = paramInt3 - paramInt1;
/*  698 */       i2 = paramInt1;
/*      */     } else {
/*  700 */       k = paramInt1 - paramInt3;
/*  701 */       i6 = 1;
/*  702 */       i2 = paramInt3; }
/*      */     int m;
/*  704 */     int i3; if (paramInt4 > paramInt2) {
/*  705 */       m = paramInt4 - paramInt2;
/*  706 */       i3 = paramInt2;
/*      */     } else {
/*  708 */       m = paramInt2 - paramInt4;
/*  709 */       i7 = 1;
/*  710 */       i3 = paramInt4;
/*      */     }
/*  712 */     if ((i <= 0) || (j <= 0)) {
/*  713 */       return true;
/*      */     }
/*      */     
/*  716 */     if ((i4 == i6) && (i5 == i7))
/*      */     {
/*  718 */       if (isSimpleTranslate(paramSunGraphics2D))
/*      */       {
/*  720 */         double d1 = i2 + paramSunGraphics2D.transX;
/*  721 */         double d3 = i3 + paramSunGraphics2D.transY;
/*  722 */         double d5 = d1 + k;
/*  723 */         double d6 = d3 + m;
/*  724 */         if (renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramSunGraphics2D.interpolationType, n, i1, n + i, i1 + j, d1, d3, d5, d6))
/*      */         {
/*      */ 
/*      */ 
/*  728 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  732 */     AffineTransform localAffineTransform = new AffineTransform(paramSunGraphics2D.transform);
/*  733 */     localAffineTransform.translate(paramInt1, paramInt2);
/*  734 */     double d2 = (paramInt3 - paramInt1) / (paramInt7 - paramInt5);
/*  735 */     double d4 = (paramInt4 - paramInt2) / (paramInt8 - paramInt6);
/*  736 */     localAffineTransform.scale(d2, d4);
/*  737 */     localAffineTransform.translate(n - paramInt5, i1 - paramInt6);
/*      */     
/*  739 */     int i8 = SurfaceManager.getImageScale(paramImage);
/*  740 */     int i9 = paramImage.getWidth(null) * i8;
/*  741 */     int i10 = paramImage.getHeight(null) * i8;
/*  742 */     i += n;
/*  743 */     j += i1;
/*      */     
/*  745 */     if (i > i9) {
/*  746 */       i = i9;
/*      */     }
/*  748 */     if (j > i10) {
/*  749 */       j = i10;
/*      */     }
/*  751 */     if (n < 0) {
/*  752 */       localAffineTransform.translate(-n, 0.0D);
/*  753 */       n = 0;
/*      */     }
/*  755 */     if (i1 < 0) {
/*  756 */       localAffineTransform.translate(0.0D, -i1);
/*  757 */       i1 = 0;
/*      */     }
/*  759 */     if ((n >= i) || (i1 >= j)) {
/*  760 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  770 */     transformImage(paramSunGraphics2D, paramImage, localAffineTransform, paramSunGraphics2D.interpolationType, n, i1, i, j, paramColor);
/*      */     
/*  772 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean closeToInteger(int paramInt, double paramDouble)
/*      */   {
/*  798 */     return Math.abs(paramDouble - paramInt) < 1.0E-4D;
/*      */   }
/*      */   
/*      */   public static boolean isSimpleTranslate(SunGraphics2D paramSunGraphics2D) {
/*  802 */     int i = paramSunGraphics2D.transformState;
/*  803 */     if (i <= 1)
/*      */     {
/*  805 */       return true;
/*      */     }
/*  807 */     if (i >= 3)
/*      */     {
/*  809 */       return false;
/*      */     }
/*      */     
/*  812 */     if (paramSunGraphics2D.interpolationType == 1) {
/*  813 */       return true;
/*      */     }
/*  815 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static boolean isBgOperation(SurfaceData paramSurfaceData, Color paramColor)
/*      */   {
/*  821 */     if (paramSurfaceData != null) if (paramColor == null) break label20; label20: return paramSurfaceData
/*      */     
/*  823 */       .getTransparency() != 1;
/*      */   }
/*      */   
/*      */   protected BufferedImage getBufferedImage(Image paramImage) {
/*  827 */     if ((paramImage instanceof BufferedImage)) {
/*  828 */       return (BufferedImage)paramImage;
/*      */     }
/*      */     
/*  831 */     return ((VolatileImage)paramImage).getSnapshot();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private ColorModel getTransformColorModel(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, AffineTransform paramAffineTransform)
/*      */   {
/*  841 */     ColorModel localColorModel = paramBufferedImage.getColorModel();
/*  842 */     Object localObject1 = localColorModel;
/*      */     
/*  844 */     if (paramAffineTransform.isIdentity()) {
/*  845 */       return (ColorModel)localObject1;
/*      */     }
/*  847 */     int i = paramAffineTransform.getType();
/*  848 */     int j = (i & 0x38) != 0 ? 1 : 0;
/*      */     
/*      */     Object localObject2;
/*  851 */     if ((j == 0) && (i != 1) && (i != 0))
/*      */     {
/*      */ 
/*      */ 
/*  855 */       localObject2 = new double[4];
/*  856 */       paramAffineTransform.getMatrix((double[])localObject2);
/*      */       
/*      */ 
/*  859 */       j = (localObject2[0] != (int)localObject2[0]) || (localObject2[3] != (int)localObject2[3]) ? 1 : 0;
/*      */     }
/*      */     
/*  862 */     if (paramSunGraphics2D.renderHint != 2) {
/*  863 */       if ((localColorModel instanceof IndexColorModel)) {
/*  864 */         localObject2 = paramBufferedImage.getRaster();
/*  865 */         IndexColorModel localIndexColorModel = (IndexColorModel)localColorModel;
/*      */         
/*  867 */         if ((j != 0) && (localColorModel.getTransparency() == 1))
/*      */         {
/*  869 */           if ((localObject2 instanceof BytePackedRaster)) {
/*  870 */             localObject1 = ColorModel.getRGBdefault();
/*      */           }
/*      */           else {
/*  873 */             double[] arrayOfDouble = new double[6];
/*  874 */             paramAffineTransform.getMatrix(arrayOfDouble);
/*  875 */             if ((arrayOfDouble[1] != 0.0D) || (arrayOfDouble[2] != 0.0D) || (arrayOfDouble[4] != 0.0D) || (arrayOfDouble[5] != 0.0D))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  880 */               int k = localIndexColorModel.getMapSize();
/*  881 */               if (k < 256) {
/*  882 */                 int[] arrayOfInt = new int[k + 1];
/*  883 */                 localIndexColorModel.getRGBs(arrayOfInt);
/*  884 */                 arrayOfInt[k] = 0;
/*      */                 
/*  886 */                 localObject1 = new IndexColorModel(localIndexColorModel.getPixelSize(), k + 1, arrayOfInt, 0, true, k, 0);
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*  892 */                 localObject1 = ColorModel.getRGBdefault();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  898 */       else if ((j != 0) && (localColorModel.getTransparency() == 1))
/*      */       {
/*      */ 
/*      */ 
/*  902 */         localObject1 = ColorModel.getRGBdefault();
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*  907 */     else if (((localColorModel instanceof IndexColorModel)) || ((j != 0) && 
/*  908 */       (localColorModel.getTransparency() == 1)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  913 */       localObject1 = ColorModel.getRGBdefault();
/*      */     }
/*      */     
/*      */ 
/*  917 */     return (ColorModel)localObject1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void blitSurfaceData(SunGraphics2D paramSunGraphics2D, Region paramRegion, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor)
/*      */   {
/*  930 */     if ((paramInt5 <= 0) || (paramInt6 <= 0))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  947 */       return;
/*      */     }
/*  949 */     CompositeType localCompositeType = paramSunGraphics2D.imageComp;
/*  950 */     if (CompositeType.SrcOverNoEa.equals(localCompositeType))
/*  951 */       if (paramSurfaceData1.getTransparency() != 1) { if (paramColor != null)
/*      */         {
/*  953 */           if (paramColor.getTransparency() != 1) {} }
/*      */       } else
/*  955 */         localCompositeType = CompositeType.SrcNoEa;
/*      */     Object localObject;
/*  957 */     if (!isBgOperation(paramSurfaceData1, paramColor)) {
/*  958 */       localObject = Blit.getFromCache(paramSurfaceType1, localCompositeType, paramSurfaceType2);
/*  959 */       ((Blit)localObject).Blit(paramSurfaceData1, paramSurfaceData2, paramSunGraphics2D.composite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     }
/*      */     else {
/*  962 */       localObject = BlitBg.getFromCache(paramSurfaceType1, localCompositeType, paramSurfaceType2);
/*  963 */       ((BlitBg)localObject).BlitBg(paramSurfaceData1, paramSurfaceData2, paramSunGraphics2D.composite, paramRegion, paramColor
/*  964 */         .getRGB(), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean scaleSurfaceData(SunGraphics2D paramSunGraphics2D, Region paramRegion, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
/*      */   {
/*  979 */     CompositeType localCompositeType = paramSunGraphics2D.imageComp;
/*  980 */     if ((CompositeType.SrcOverNoEa.equals(localCompositeType)) && 
/*  981 */       (paramSurfaceData1.getTransparency() == 1))
/*      */     {
/*  983 */       localCompositeType = CompositeType.SrcNoEa;
/*      */     }
/*      */     
/*  986 */     ScaledBlit localScaledBlit = ScaledBlit.getFromCache(paramSurfaceType1, localCompositeType, paramSurfaceType2);
/*  987 */     if (localScaledBlit != null) {
/*  988 */       localScaledBlit.Scale(paramSurfaceData1, paramSurfaceData2, paramSunGraphics2D.composite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
/*      */       
/*  990 */       return true;
/*      */     }
/*  992 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static boolean imageReady(ToolkitImage paramToolkitImage, ImageObserver paramImageObserver)
/*      */   {
/*  998 */     if (paramToolkitImage.hasError()) {
/*  999 */       if (paramImageObserver != null) {
/* 1000 */         paramImageObserver.imageUpdate(paramToolkitImage, 192, -1, -1, -1, -1);
/*      */       }
/*      */       
/*      */ 
/* 1004 */       return false;
/*      */     }
/* 1006 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 1013 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1014 */       return copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramColor);
/*      */     }
/* 1016 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1017 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1018 */       return false;
/*      */     }
/* 1020 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1021 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramColor, paramImageObserver);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 1029 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1030 */       return copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor);
/*      */     }
/* 1032 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1033 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1034 */       return false;
/*      */     }
/* 1036 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1037 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4, paramInt3 + paramInt5, paramInt4 + paramInt6, paramColor, paramImageObserver);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 1049 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1050 */       return scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor);
/*      */     }
/* 1052 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1053 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1054 */       return false;
/*      */     }
/* 1056 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1057 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 1067 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1068 */       return scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor);
/*      */     }
/*      */     
/* 1071 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1072 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1073 */       return false;
/*      */     }
/* 1075 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1076 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
/*      */   {
/* 1084 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1085 */       transformImage(paramSunGraphics2D, paramImage, 0, 0, paramAffineTransform, paramSunGraphics2D.interpolationType);
/* 1086 */       return true;
/*      */     }
/* 1088 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1089 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1090 */       return false;
/*      */     }
/* 1092 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1093 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramAffineTransform, paramImageObserver);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void transformImage(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
/*      */   {
/* 1100 */     if (paramBufferedImageOp != null) {
/* 1101 */       if ((paramBufferedImageOp instanceof AffineTransformOp)) {
/* 1102 */         AffineTransformOp localAffineTransformOp = (AffineTransformOp)paramBufferedImageOp;
/* 1103 */         transformImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, localAffineTransformOp
/* 1104 */           .getTransform(), localAffineTransformOp
/* 1105 */           .getInterpolationType());
/* 1106 */         return;
/*      */       }
/* 1108 */       paramBufferedImage = paramBufferedImageOp.filter(paramBufferedImage, null);
/*      */     }
/*      */     
/* 1111 */     copyImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, null);
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\DrawImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
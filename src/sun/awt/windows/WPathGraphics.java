/*      */ package sun.awt.windows;
/*      */ 
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Font;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Image;
/*      */ import java.awt.Shape;
/*      */ import java.awt.Stroke;
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.font.GlyphVector;
/*      */ import java.awt.font.TextLayout;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Line2D.Float;
/*      */ import java.awt.geom.NoninvertibleTransformException;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.geom.Point2D;
/*      */ import java.awt.geom.Point2D.Double;
/*      */ import java.awt.geom.Point2D.Float;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.Rectangle2D.Float;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ComponentSampleModel;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.MultiPixelPackedSampleModel;
/*      */ import java.awt.image.SampleModel;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.awt.print.PageFormat;
/*      */ import java.awt.print.Printable;
/*      */ import java.awt.print.PrinterException;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.security.AccessController;
/*      */ import java.util.Arrays;
/*      */ import sun.awt.image.ByteComponentRaster;
/*      */ import sun.awt.image.BytePackedRaster;
/*      */ import sun.font.CharToGlyphMapper;
/*      */ import sun.font.CompositeFont;
/*      */ import sun.font.Font2D;
/*      */ import sun.font.FontUtilities;
/*      */ import sun.font.PhysicalFont;
/*      */ import sun.font.TrueTypeFont;
/*      */ import sun.print.PathGraphics;
/*      */ import sun.print.ProxyGraphics2D;
/*      */ import sun.security.action.GetPropertyAction;
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
/*      */ final class WPathGraphics
/*      */   extends PathGraphics
/*      */ {
/*      */   private static final int DEFAULT_USER_RES = 72;
/*      */   private static final float MIN_DEVICE_LINEWIDTH = 1.2F;
/*      */   private static final float MAX_THINLINE_INCHES = 0.014F;
/*   93 */   private static boolean useGDITextLayout = true;
/*   94 */   private static boolean preferGDITextLayout = false;
/*      */   
/*      */   static {
/*   97 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.enableGDITextLayout"));
/*      */     
/*      */ 
/*      */ 
/*  101 */     if (str != null) {
/*  102 */       useGDITextLayout = Boolean.getBoolean(str);
/*  103 */       if ((!useGDITextLayout) && 
/*  104 */         (str.equalsIgnoreCase("prefer"))) {
/*  105 */         useGDITextLayout = true;
/*  106 */         preferGDITextLayout = true;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   WPathGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt, boolean paramBoolean)
/*      */   {
/*  115 */     super(paramGraphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, paramBoolean);
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
/*      */   public Graphics create()
/*      */   {
/*  128 */     return new WPathGraphics((Graphics2D)getDelegate().create(), 
/*  129 */       getPrinterJob(), 
/*  130 */       getPrintable(), 
/*  131 */       getPageFormat(), 
/*  132 */       getPageIndex(), 
/*  133 */       canDoRedraws());
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
/*      */   public void draw(Shape paramShape)
/*      */   {
/*  153 */     Stroke localStroke = getStroke();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  160 */     if ((localStroke instanceof BasicStroke))
/*      */     {
/*  162 */       BasicStroke localBasicStroke2 = null;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  170 */       BasicStroke localBasicStroke1 = (BasicStroke)localStroke;
/*  171 */       float f2 = localBasicStroke1.getLineWidth();
/*  172 */       Point2D.Float localFloat1 = new Point2D.Float(f2, f2);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  180 */       AffineTransform localAffineTransform1 = getTransform();
/*  181 */       localAffineTransform1.deltaTransform(localFloat1, localFloat1);
/*  182 */       float f1 = Math.min(Math.abs(localFloat1.x), 
/*  183 */         Math.abs(localFloat1.y));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  189 */       if (f1 < 1.2F)
/*      */       {
/*  191 */         Point2D.Float localFloat2 = new Point2D.Float(1.2F, 1.2F);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  202 */           AffineTransform localAffineTransform2 = localAffineTransform1.createInverse();
/*  203 */           localAffineTransform2.deltaTransform(localFloat2, localFloat2);
/*      */           
/*  205 */           float f3 = Math.max(Math.abs(localFloat2.x), 
/*  206 */             Math.abs(localFloat2.y));
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
/*  217 */           localBasicStroke2 = new BasicStroke(f3, localBasicStroke1.getEndCap(), localBasicStroke1.getLineJoin(), localBasicStroke1.getMiterLimit(), localBasicStroke1.getDashArray(), localBasicStroke1.getDashPhase());
/*  218 */           setStroke(localBasicStroke2);
/*      */         }
/*      */         catch (NoninvertibleTransformException localNoninvertibleTransformException) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  228 */       super.draw(paramShape);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  234 */       if (localBasicStroke2 != null) {
/*  235 */         setStroke(localBasicStroke1);
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  242 */       super.draw(paramShape);
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
/*      */   public void drawString(String paramString, int paramInt1, int paramInt2)
/*      */   {
/*  260 */     drawString(paramString, paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */   public void drawString(String paramString, float paramFloat1, float paramFloat2)
/*      */   {
/*  265 */     drawString(paramString, paramFloat1, paramFloat2, getFont(), getFontRenderContext(), 0.0F);
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
/*      */   protected int platformFontCount(Font paramFont, String paramString)
/*      */   {
/*  283 */     AffineTransform localAffineTransform1 = getTransform();
/*  284 */     AffineTransform localAffineTransform2 = new AffineTransform(localAffineTransform1);
/*  285 */     localAffineTransform2.concatenate(getFont().getTransform());
/*  286 */     int i = localAffineTransform2.getType();
/*      */     
/*      */ 
/*  289 */     int j = (i != 32) && ((i & 0x40) == 0) ? 1 : 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  294 */     if (j == 0) {
/*  295 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  305 */     Font2D localFont2D = FontUtilities.getFont2D(paramFont);
/*  306 */     if (((localFont2D instanceof CompositeFont)) || ((localFont2D instanceof TrueTypeFont)))
/*      */     {
/*  308 */       return 1;
/*      */     }
/*  310 */     return 0;
/*      */   }
/*      */   
/*      */   private static boolean isXP()
/*      */   {
/*  315 */     String str = System.getProperty("os.version");
/*  316 */     if (str != null) {
/*  317 */       Float localFloat = Float.valueOf(str);
/*  318 */       return localFloat.floatValue() >= 5.1F;
/*      */     }
/*  320 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean strNeedsTextLayout(String paramString, Font paramFont)
/*      */   {
/*  330 */     char[] arrayOfChar = paramString.toCharArray();
/*  331 */     boolean bool = FontUtilities.isComplexText(arrayOfChar, 0, arrayOfChar.length);
/*  332 */     if (!bool)
/*  333 */       return false;
/*  334 */     if (!useGDITextLayout) {
/*  335 */       return true;
/*      */     }
/*  337 */     if ((preferGDITextLayout) || (
/*  338 */       (isXP()) && (FontUtilities.textLayoutIsCompatible(paramFont)))) {
/*  339 */       return false;
/*      */     }
/*  341 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int getAngle(Double paramDouble)
/*      */   {
/*  351 */     double d = Math.toDegrees(Math.atan2(paramDouble.y, paramDouble.x));
/*  352 */     if (d < 0.0D) {
/*  353 */       d += 360.0D;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  361 */     if (d != 0.0D) {
/*  362 */       d = 360.0D - d;
/*      */     }
/*  364 */     return (int)Math.round(d * 10.0D);
/*      */   }
/*      */   
/*      */   private float getAwScale(double paramDouble1, double paramDouble2)
/*      */   {
/*  369 */     float f = (float)(paramDouble1 / paramDouble2);
/*      */     
/*  371 */     if ((f > 0.999F) && (f < 1.001F)) {
/*  372 */       f = 1.0F;
/*      */     }
/*  374 */     return f;
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
/*      */   public void drawString(String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3)
/*      */   {
/*  402 */     if (paramString.length() == 0) {
/*  403 */       return;
/*      */     }
/*      */     
/*  406 */     if (WPrinterJob.shapeTextProp) {
/*  407 */       super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
/*  408 */       return;
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
/*      */ 
/*      */ 
/*  422 */     boolean bool = strNeedsTextLayout(paramString, paramFont);
/*  423 */     if (((paramFont.hasLayoutAttributes()) || (bool)) && (!this.printingGlyphVector))
/*      */     {
/*  425 */       localObject = new TextLayout(paramString, paramFont, paramFontRenderContext);
/*  426 */       ((TextLayout)localObject).draw(this, paramFloat1, paramFloat2);
/*  427 */       return; }
/*  428 */     if (bool) {
/*  429 */       super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
/*  430 */       return;
/*      */     }
/*      */     
/*  433 */     Object localObject = getTransform();
/*  434 */     AffineTransform localAffineTransform1 = new AffineTransform((AffineTransform)localObject);
/*  435 */     localAffineTransform1.concatenate(paramFont.getTransform());
/*  436 */     int i = localAffineTransform1.getType();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  443 */     int j = (i != 32) && ((i & 0x40) == 0) ? 1 : 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  448 */     WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/*      */     try {
/*  450 */       localWPrinterJob.setTextColor((Color)getPaint());
/*      */     } catch (ClassCastException localClassCastException) {
/*  452 */       j = 0;
/*      */     }
/*      */     
/*  455 */     if (j == 0) {
/*  456 */       super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
/*  457 */       return;
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
/*  468 */     Point2D.Float localFloat1 = new Point2D.Float(paramFloat1, paramFloat2);
/*  469 */     Point2D.Float localFloat2 = new Point2D.Float();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  474 */     if (paramFont.isTransformed()) {
/*  475 */       AffineTransform localAffineTransform2 = paramFont.getTransform();
/*  476 */       float f2 = (float)localAffineTransform2.getTranslateX();
/*  477 */       float f3 = (float)localAffineTransform2.getTranslateY();
/*  478 */       if (Math.abs(f2) < 1.0E-5D) f2 = 0.0F;
/*  479 */       if (Math.abs(f3) < 1.0E-5D) f3 = 0.0F;
/*  480 */       localFloat1.x += f2;localFloat1.y += f3;
/*      */     }
/*  482 */     ((AffineTransform)localObject).transform(localFloat1, localFloat2);
/*      */     
/*  484 */     if (getClip() != null) {
/*  485 */       deviceClip(getClip().getPathIterator((AffineTransform)localObject));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  495 */     float f1 = paramFont.getSize2D();
/*      */     
/*  497 */     double d1 = localWPrinterJob.getXRes();
/*  498 */     double d2 = localWPrinterJob.getYRes();
/*      */     
/*  500 */     double d3 = d2 / 72.0D;
/*      */     
/*  502 */     int k = getPageFormat().getOrientation();
/*  503 */     if ((k == 0) || (k == 2))
/*      */     {
/*      */ 
/*  506 */       d4 = d1;
/*  507 */       d1 = d2;
/*  508 */       d2 = d4;
/*      */     }
/*      */     
/*  511 */     double d4 = d1 / 72.0D;
/*  512 */     double d5 = d2 / 72.0D;
/*  513 */     localAffineTransform1.scale(1.0D / d4, 1.0D / d5);
/*      */     
/*  515 */     Double localDouble1 = new Double(0.0D, 1.0D);
/*  516 */     localAffineTransform1.deltaTransform(localDouble1, localDouble1);
/*  517 */     double d6 = Math.sqrt(localDouble1.x * localDouble1.x + localDouble1.y * localDouble1.y);
/*  518 */     float f4 = (float)(f1 * d6 * d3);
/*      */     
/*  520 */     Double localDouble2 = new Double(1.0D, 0.0D);
/*  521 */     localAffineTransform1.deltaTransform(localDouble2, localDouble2);
/*  522 */     double d7 = Math.sqrt(localDouble2.x * localDouble2.x + localDouble2.y * localDouble2.y);
/*      */     
/*  524 */     float f5 = getAwScale(d7, d6);
/*  525 */     int m = getAngle(localDouble2);
/*      */     
/*  527 */     localDouble2 = new Double(1.0D, 0.0D);
/*  528 */     ((AffineTransform)localObject).deltaTransform(localDouble2, localDouble2);
/*  529 */     double d8 = Math.sqrt(localDouble2.x * localDouble2.x + localDouble2.y * localDouble2.y);
/*  530 */     localDouble1 = new Double(0.0D, 1.0D);
/*  531 */     ((AffineTransform)localObject).deltaTransform(localDouble1, localDouble1);
/*  532 */     double d9 = Math.sqrt(localDouble1.x * localDouble1.x + localDouble1.y * localDouble1.y);
/*      */     
/*  534 */     Font2D localFont2D = FontUtilities.getFont2D(paramFont);
/*  535 */     if ((localFont2D instanceof TrueTypeFont)) {
/*  536 */       textOut(paramString, paramFont, (TrueTypeFont)localFont2D, paramFontRenderContext, f4, m, f5, d8, d9, paramFloat1, paramFloat2, localFloat2.x, localFloat2.y, paramFloat3);
/*      */ 
/*      */ 
/*      */     }
/*  540 */     else if ((localFont2D instanceof CompositeFont))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  548 */       CompositeFont localCompositeFont = (CompositeFont)localFont2D;
/*  549 */       float f6 = paramFloat1;float f7 = paramFloat2;
/*  550 */       float f8 = localFloat2.x;float f9 = localFloat2.y;
/*  551 */       char[] arrayOfChar = paramString.toCharArray();
/*  552 */       int n = arrayOfChar.length;
/*  553 */       int[] arrayOfInt = new int[n];
/*  554 */       localCompositeFont.getMapper().charsToGlyphs(n, arrayOfChar, arrayOfInt);
/*      */       
/*  556 */       int i1 = 0;int i2 = 0;int i3 = 0;
/*  557 */       while (i2 < n)
/*      */       {
/*  559 */         i1 = i2;
/*  560 */         i3 = arrayOfInt[i1] >>> 24;
/*      */         
/*  562 */         while ((i2 < n) && (arrayOfInt[i2] >>> 24 == i3)) {
/*  563 */           i2++;
/*      */         }
/*  565 */         String str = new String(arrayOfChar, i1, i2 - i1);
/*  566 */         PhysicalFont localPhysicalFont = localCompositeFont.getSlotFont(i3);
/*  567 */         textOut(str, paramFont, localPhysicalFont, paramFontRenderContext, f4, m, f5, d8, d9, f6, f7, f8, f9, 0.0F);
/*      */         
/*      */ 
/*      */ 
/*  571 */         Rectangle2D localRectangle2D = paramFont.getStringBounds(str, paramFontRenderContext);
/*  572 */         float f10 = (float)localRectangle2D.getWidth();
/*  573 */         f6 += f10;
/*  574 */         localFloat1.x += f10;
/*  575 */         ((AffineTransform)localObject).transform(localFloat1, localFloat2);
/*  576 */         f8 = localFloat2.x;
/*  577 */         f9 = localFloat2.y;
/*      */       }
/*      */     } else {
/*  580 */       super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
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
/*      */   protected boolean printGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
/*      */   {
/*  593 */     if ((paramGlyphVector.getLayoutFlags() & 0x1) != 0) {
/*  594 */       return false;
/*      */     }
/*      */     
/*  597 */     if (paramGlyphVector.getNumGlyphs() == 0) {
/*  598 */       return true;
/*      */     }
/*      */     
/*  601 */     AffineTransform localAffineTransform1 = getTransform();
/*  602 */     AffineTransform localAffineTransform2 = new AffineTransform(localAffineTransform1);
/*  603 */     Font localFont = paramGlyphVector.getFont();
/*  604 */     localAffineTransform2.concatenate(localFont.getTransform());
/*  605 */     int i = localAffineTransform2.getType();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  612 */     int j = (i != 32) && ((i & 0x40) == 0) ? 1 : 0;
/*      */     
/*      */ 
/*      */ 
/*  616 */     WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/*      */     try {
/*  618 */       localWPrinterJob.setTextColor((Color)getPaint());
/*      */     } catch (ClassCastException localClassCastException) {
/*  620 */       j = 0;
/*      */     }
/*      */     
/*  623 */     if ((WPrinterJob.shapeTextProp) || (j == 0)) {
/*  624 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  629 */     Point2D.Float localFloat1 = new Point2D.Float(paramFloat1, paramFloat2);
/*      */     
/*  631 */     Point2D localPoint2D = paramGlyphVector.getGlyphPosition(0);
/*  632 */     localFloat1.x += (float)localPoint2D.getX();
/*  633 */     localFloat1.y += (float)localPoint2D.getY();
/*  634 */     Point2D.Float localFloat2 = new Point2D.Float();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  639 */     if (localFont.isTransformed()) {
/*  640 */       AffineTransform localAffineTransform3 = localFont.getTransform();
/*  641 */       float f2 = (float)localAffineTransform3.getTranslateX();
/*  642 */       float f3 = (float)localAffineTransform3.getTranslateY();
/*  643 */       if (Math.abs(f2) < 1.0E-5D) f2 = 0.0F;
/*  644 */       if (Math.abs(f3) < 1.0E-5D) f3 = 0.0F;
/*  645 */       localFloat1.x += f2;localFloat1.y += f3;
/*      */     }
/*  647 */     localAffineTransform1.transform(localFloat1, localFloat2);
/*      */     
/*  649 */     if (getClip() != null) {
/*  650 */       deviceClip(getClip().getPathIterator(localAffineTransform1));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  660 */     float f1 = localFont.getSize2D();
/*      */     
/*  662 */     double d1 = localWPrinterJob.getXRes();
/*  663 */     double d2 = localWPrinterJob.getYRes();
/*      */     
/*  665 */     double d3 = d2 / 72.0D;
/*      */     
/*  667 */     int k = getPageFormat().getOrientation();
/*  668 */     if ((k == 0) || (k == 2))
/*      */     {
/*      */ 
/*  671 */       d4 = d1;
/*  672 */       d1 = d2;
/*  673 */       d2 = d4;
/*      */     }
/*      */     
/*  676 */     double d4 = d1 / 72.0D;
/*  677 */     double d5 = d2 / 72.0D;
/*  678 */     localAffineTransform2.scale(1.0D / d4, 1.0D / d5);
/*      */     
/*  680 */     Double localDouble1 = new Double(0.0D, 1.0D);
/*  681 */     localAffineTransform2.deltaTransform(localDouble1, localDouble1);
/*  682 */     double d6 = Math.sqrt(localDouble1.x * localDouble1.x + localDouble1.y * localDouble1.y);
/*  683 */     float f4 = (float)(f1 * d6 * d3);
/*      */     
/*  685 */     Double localDouble2 = new Double(1.0D, 0.0D);
/*  686 */     localAffineTransform2.deltaTransform(localDouble2, localDouble2);
/*  687 */     double d7 = Math.sqrt(localDouble2.x * localDouble2.x + localDouble2.y * localDouble2.y);
/*      */     
/*  689 */     float f5 = getAwScale(d7, d6);
/*  690 */     int m = getAngle(localDouble2);
/*      */     
/*  692 */     localDouble2 = new Double(1.0D, 0.0D);
/*  693 */     localAffineTransform1.deltaTransform(localDouble2, localDouble2);
/*  694 */     double d8 = Math.sqrt(localDouble2.x * localDouble2.x + localDouble2.y * localDouble2.y);
/*  695 */     localDouble1 = new Double(0.0D, 1.0D);
/*  696 */     localAffineTransform1.deltaTransform(localDouble1, localDouble1);
/*  697 */     double d9 = Math.sqrt(localDouble1.x * localDouble1.x + localDouble1.y * localDouble1.y);
/*      */     
/*  699 */     int n = paramGlyphVector.getNumGlyphs();
/*  700 */     Object localObject1 = paramGlyphVector.getGlyphCodes(0, n, null);
/*  701 */     Object localObject2 = paramGlyphVector.getGlyphPositions(0, n, null);
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
/*  714 */     int i1 = 0;
/*  715 */     for (int i2 = 0; i2 < n; i2++) {
/*  716 */       if ((localObject1[i2] & 0xFFFF) >= 65534)
/*      */       {
/*  718 */         i1++; }
/*      */     }
/*      */     int i4;
/*  721 */     if (i1 > 0) {
/*  722 */       i2 = n - i1;
/*  723 */       localObject3 = new int[i2];
/*  724 */       localObject4 = new float[i2 * 2];
/*  725 */       int i3 = 0;
/*  726 */       for (i4 = 0; i4 < n; i4++) {
/*  727 */         if ((localObject1[i4] & 0xFFFF) < 65534)
/*      */         {
/*  729 */           localObject3[i3] = localObject1[i4];
/*  730 */           localObject4[(i3 * 2)] = localObject2[(i4 * 2)];
/*  731 */           localObject4[(i3 * 2 + 1)] = localObject2[(i4 * 2 + 1)];
/*  732 */           i3++;
/*      */         }
/*      */       }
/*  735 */       n = i2;
/*  736 */       localObject1 = localObject3;
/*  737 */       localObject2 = localObject4;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  756 */     AffineTransform localAffineTransform4 = AffineTransform.getScaleInstance(d8, d9);
/*  757 */     Object localObject3 = new float[localObject2.length];
/*      */     
/*  759 */     localAffineTransform4.transform((float[])localObject2, 0, (float[])localObject3, 0, localObject2.length / 2);
/*      */     
/*      */ 
/*      */ 
/*  763 */     Object localObject4 = FontUtilities.getFont2D(localFont);
/*  764 */     Object localObject5; if ((localObject4 instanceof TrueTypeFont)) {
/*  765 */       localObject5 = ((Font2D)localObject4).getFamilyName(null);
/*  766 */       i4 = localFont.getStyle() | ((Font2D)localObject4).getStyle();
/*  767 */       if (!localWPrinterJob.setFont((String)localObject5, f4, i4, m, f5))
/*      */       {
/*  769 */         return false;
/*      */       }
/*  771 */       localWPrinterJob.glyphsOut((int[])localObject1, localFloat2.x, localFloat2.y, (float[])localObject3);
/*      */     }
/*  773 */     else if ((localObject4 instanceof CompositeFont))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  781 */       localObject5 = (CompositeFont)localObject4;
/*  782 */       float f6 = paramFloat1;float f7 = paramFloat2;
/*  783 */       float f8 = localFloat2.x;float f9 = localFloat2.y;
/*      */       
/*  785 */       int i5 = 0;int i6 = 0;int i7 = 0;
/*  786 */       while (i6 < n)
/*      */       {
/*  788 */         i5 = i6;
/*  789 */         i7 = localObject1[i5] >>> 24;
/*      */         
/*  791 */         while ((i6 < n) && (localObject1[i6] >>> 24 == i7)) {
/*  792 */           i6++;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  800 */         PhysicalFont localPhysicalFont = ((CompositeFont)localObject5).getSlotFont(i7);
/*  801 */         if (!(localPhysicalFont instanceof TrueTypeFont)) {
/*  802 */           return false;
/*      */         }
/*  804 */         String str = localPhysicalFont.getFamilyName(null);
/*  805 */         int i8 = localFont.getStyle() | localPhysicalFont.getStyle();
/*  806 */         if (!localWPrinterJob.setFont(str, f4, i8, m, f5))
/*      */         {
/*  808 */           return false;
/*      */         }
/*      */         
/*  811 */         int[] arrayOfInt = Arrays.copyOfRange((int[])localObject1, i5, i6);
/*  812 */         float[] arrayOfFloat = Arrays.copyOfRange((float[])localObject3, i5 * 2, i6 * 2);
/*      */         
/*  814 */         if (i5 != 0) {
/*  815 */           Point2D.Float localFloat3 = new Point2D.Float(paramFloat1 + localObject2[(i5 * 2)], paramFloat2 + localObject2[(i5 * 2 + 1)]);
/*      */           
/*      */ 
/*  818 */           localAffineTransform1.transform(localFloat3, localFloat3);
/*  819 */           f8 = localFloat3.x;
/*  820 */           f9 = localFloat3.y;
/*      */         }
/*  822 */         localWPrinterJob.glyphsOut(arrayOfInt, f8, f9, arrayOfFloat);
/*      */       }
/*      */     } else {
/*  825 */       return false;
/*      */     }
/*  827 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void textOut(String paramString, Font paramFont, PhysicalFont paramPhysicalFont, FontRenderContext paramFontRenderContext, float paramFloat1, int paramInt, float paramFloat2, double paramDouble1, double paramDouble2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7)
/*      */   {
/*  838 */     String str = paramPhysicalFont.getFamilyName(null);
/*  839 */     int i = paramFont.getStyle() | paramPhysicalFont.getStyle();
/*  840 */     WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/*  841 */     boolean bool = localWPrinterJob.setFont(str, paramFloat1, i, paramInt, paramFloat2);
/*      */     
/*  843 */     if (!bool) {
/*  844 */       super.drawString(paramString, paramFloat3, paramFloat4, paramFont, paramFontRenderContext, paramFloat7);
/*  845 */       return;
/*      */     }
/*      */     
/*  848 */     Object localObject = null;
/*  849 */     if (!okGDIMetrics(paramString, paramFont, paramFontRenderContext, paramDouble1))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  857 */       paramString = localWPrinterJob.removeControlChars(paramString);
/*  858 */       char[] arrayOfChar = paramString.toCharArray();
/*  859 */       int j = arrayOfChar.length;
/*  860 */       GlyphVector localGlyphVector = null;
/*  861 */       if (!FontUtilities.isComplexText(arrayOfChar, 0, j)) {
/*  862 */         localGlyphVector = paramFont.createGlyphVector(paramFontRenderContext, paramString);
/*      */       }
/*  864 */       if (localGlyphVector == null) {
/*  865 */         super.drawString(paramString, paramFloat3, paramFloat4, paramFont, paramFontRenderContext, paramFloat7);
/*  866 */         return;
/*      */       }
/*  868 */       localObject = localGlyphVector.getGlyphPositions(0, j, null);
/*  869 */       Point2D localPoint2D = localGlyphVector.getGlyphPosition(localGlyphVector.getNumGlyphs());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  875 */       AffineTransform localAffineTransform = AffineTransform.getScaleInstance(paramDouble1, paramDouble2);
/*  876 */       float[] arrayOfFloat = new float[localObject.length];
/*      */       
/*  878 */       localAffineTransform.transform((float[])localObject, 0, arrayOfFloat, 0, localObject.length / 2);
/*      */       
/*      */ 
/*  881 */       localObject = arrayOfFloat;
/*      */     }
/*  883 */     localWPrinterJob.textOut(paramString, paramFloat5, paramFloat6, (float[])localObject);
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
/*      */   private boolean okGDIMetrics(String paramString, Font paramFont, FontRenderContext paramFontRenderContext, double paramDouble)
/*      */   {
/*  901 */     Rectangle2D localRectangle2D = paramFont.getStringBounds(paramString, paramFontRenderContext);
/*  902 */     double d1 = localRectangle2D.getWidth();
/*  903 */     d1 = Math.round(d1 * paramDouble);
/*  904 */     int i = ((WPrinterJob)getPrinterJob()).getGDIAdvance(paramString);
/*  905 */     if ((d1 > 0.0D) && (i > 0)) {
/*  906 */       double d2 = Math.abs(i - d1);
/*  907 */       double d3 = i / d1;
/*  908 */       if (d3 < 1.0D) {
/*  909 */         d3 = 1.0D / d3;
/*      */       }
/*  911 */       return (d2 <= 1.0D) || (d3 < 1.01D);
/*      */     }
/*  913 */     return true;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean drawImageToPlatform(Image paramImage, AffineTransform paramAffineTransform, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
/*      */   {
/*  951 */     BufferedImage localBufferedImage1 = getBufferedImage(paramImage);
/*  952 */     if (localBufferedImage1 == null) {
/*  953 */       return true;
/*      */     }
/*      */     
/*  956 */     WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  964 */     AffineTransform localAffineTransform1 = getTransform();
/*  965 */     if (paramAffineTransform == null) {
/*  966 */       paramAffineTransform = new AffineTransform();
/*      */     }
/*  968 */     localAffineTransform1.concatenate(paramAffineTransform);
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
/*  988 */     double[] arrayOfDouble = new double[6];
/*  989 */     localAffineTransform1.getMatrix(arrayOfDouble);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  999 */     Point2D.Float localFloat1 = new Point2D.Float(1.0F, 0.0F);
/* 1000 */     Point2D.Float localFloat2 = new Point2D.Float(0.0F, 1.0F);
/* 1001 */     localAffineTransform1.deltaTransform(localFloat1, localFloat1);
/* 1002 */     localAffineTransform1.deltaTransform(localFloat2, localFloat2);
/*      */     
/* 1004 */     Point2D.Float localFloat3 = new Point2D.Float(0.0F, 0.0F);
/* 1005 */     double d1 = localFloat1.distance(localFloat3);
/* 1006 */     double d2 = localFloat2.distance(localFloat3);
/*      */     
/* 1008 */     double d3 = localWPrinterJob.getXRes();
/* 1009 */     double d4 = localWPrinterJob.getYRes();
/* 1010 */     double d5 = d3 / 72.0D;
/* 1011 */     double d6 = d4 / 72.0D;
/*      */     
/*      */ 
/* 1014 */     int i = localAffineTransform1.getType();
/* 1015 */     int j = (i & 0x30) != 0 ? 1 : 0;
/*      */     
/*      */ 
/* 1018 */     if (j != 0) {
/* 1019 */       if (d1 > d5) d1 = d5;
/* 1020 */       if (d2 > d6) { d2 = d6;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1026 */     if ((d1 != 0.0D) && (d2 != 0.0D))
/*      */     {
/*      */ 
/*      */ 
/* 1030 */       AffineTransform localAffineTransform2 = new AffineTransform(arrayOfDouble[0] / d1, arrayOfDouble[1] / d2, arrayOfDouble[2] / d1, arrayOfDouble[3] / d2, arrayOfDouble[4] / d1, arrayOfDouble[5] / d2);
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
/* 1058 */       Rectangle2D.Float localFloat = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       
/*      */ 
/*      */ 
/* 1062 */       Shape localShape1 = localAffineTransform2.createTransformedShape(localFloat);
/* 1063 */       Rectangle2D localRectangle2D1 = localShape1.getBounds2D();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1069 */       localRectangle2D1.setRect(localRectangle2D1.getX(), localRectangle2D1.getY(), localRectangle2D1
/* 1070 */         .getWidth() + 0.001D, localRectangle2D1
/* 1071 */         .getHeight() + 0.001D);
/*      */       
/* 1073 */       int k = (int)localRectangle2D1.getWidth();
/* 1074 */       int m = (int)localRectangle2D1.getHeight();
/*      */       
/* 1076 */       if ((k > 0) && (m > 0))
/*      */       {
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
/* 1094 */         int n = 1;
/* 1095 */         if ((!paramBoolean) && (hasTransparentPixels(localBufferedImage1))) {
/* 1096 */           n = 0;
/* 1097 */           if (isBitmaskTransparency(localBufferedImage1)) {
/* 1098 */             if (paramColor == null) {
/* 1099 */               if (drawBitmaskImage(localBufferedImage1, paramAffineTransform, paramColor, paramInt1, paramInt2, paramInt3, paramInt4))
/*      */               {
/*      */ 
/*      */ 
/* 1103 */                 return true;
/*      */               }
/* 1105 */             } else if (paramColor.getTransparency() == 1)
/*      */             {
/* 1107 */               n = 1;
/*      */             }
/*      */           }
/* 1110 */           if (!canDoRedraws()) {
/* 1111 */             n = 1;
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/* 1117 */           paramColor = null;
/*      */         }
/*      */         
/*      */ 
/* 1121 */         if (((paramInt1 + paramInt3 > localBufferedImage1.getWidth(null)) || 
/* 1122 */           (paramInt2 + paramInt4 > localBufferedImage1.getHeight(null))) && 
/* 1123 */           (canDoRedraws()))
/* 1124 */           n = 0;
/*      */         int i5;
/* 1126 */         int i7; if (n == 0)
/*      */         {
/* 1128 */           localAffineTransform1.getMatrix(arrayOfDouble);
/* 1129 */           AffineTransform localAffineTransform3 = new AffineTransform(arrayOfDouble[0] / d5, arrayOfDouble[1] / d6, arrayOfDouble[2] / d5, arrayOfDouble[3] / d6, arrayOfDouble[4] / d5, arrayOfDouble[5] / d6);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1138 */           localObject1 = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */           
/*      */ 
/* 1141 */           localObject2 = localAffineTransform1.createTransformedShape((Shape)localObject1);
/*      */           
/*      */ 
/* 1144 */           Rectangle2D localRectangle2D2 = ((Shape)localObject2).getBounds2D();
/*      */           
/* 1146 */           localRectangle2D2.setRect(localRectangle2D2.getX(), localRectangle2D2.getY(), localRectangle2D2
/* 1147 */             .getWidth() + 0.001D, localRectangle2D2
/* 1148 */             .getHeight() + 0.001D);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1155 */           int i3 = (int)localRectangle2D2.getWidth();
/* 1156 */           i5 = (int)localRectangle2D2.getHeight();
/* 1157 */           i7 = i3 * i5 * 3;
/* 1158 */           i8 = 8388608;
/* 1159 */           double d7 = d3 < d4 ? d3 : d4;
/* 1160 */           int i9 = (int)d7;
/* 1161 */           double d8 = 1.0D;
/*      */           
/* 1163 */           double d9 = i3 / k;
/* 1164 */           double d10 = i5 / m;
/* 1165 */           double d11 = d9 > d10 ? d10 : d9;
/* 1166 */           int i13 = (int)(i9 / d11);
/* 1167 */           if (i13 < 72) { i13 = 72;
/*      */           }
/* 1169 */           while ((i7 > i8) && (i9 > i13)) {
/* 1170 */             d8 *= 2.0D;
/* 1171 */             i9 /= 2;
/* 1172 */             i7 /= 4;
/*      */           }
/* 1174 */           if (i9 < i13) {
/* 1175 */             d8 = d7 / i13;
/*      */           }
/*      */           
/* 1178 */           localRectangle2D2.setRect(localRectangle2D2.getX() / d8, localRectangle2D2
/* 1179 */             .getY() / d8, localRectangle2D2
/* 1180 */             .getWidth() / d8, localRectangle2D2
/* 1181 */             .getHeight() / d8);
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
/* 1192 */           localWPrinterJob.saveState(getTransform(), getClip(), localRectangle2D2, d8, d8);
/*      */           
/* 1194 */           return true;
/*      */         }
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
/* 1207 */         int i1 = 5;
/* 1208 */         Object localObject1 = null;
/*      */         
/* 1210 */         Object localObject2 = localBufferedImage1.getColorModel();
/* 1211 */         int i2 = localBufferedImage1.getType();
/* 1212 */         if (((localObject2 instanceof IndexColorModel)) && 
/* 1213 */           (((ColorModel)localObject2).getPixelSize() <= 8) && ((i2 == 12) || (i2 == 13)))
/*      */         {
/*      */ 
/* 1216 */           localObject1 = (IndexColorModel)localObject2;
/* 1217 */           i1 = i2;
/*      */           
/*      */ 
/*      */ 
/* 1221 */           if ((i2 == 12) && 
/* 1222 */             (((ColorModel)localObject2).getPixelSize() == 2))
/*      */           {
/* 1224 */             int[] arrayOfInt = new int[16];
/* 1225 */             ((IndexColorModel)localObject1).getRGBs(arrayOfInt);
/*      */             
/* 1227 */             i5 = ((IndexColorModel)localObject1).getTransparency() != 1 ? 1 : 0;
/* 1228 */             i7 = ((IndexColorModel)localObject1).getTransparentPixel();
/*      */             
/* 1230 */             localObject1 = new IndexColorModel(4, 16, arrayOfInt, 0, i5, i7, 0);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1237 */         int i4 = (int)localRectangle2D1.getWidth();
/* 1238 */         int i6 = (int)localRectangle2D1.getHeight();
/* 1239 */         BufferedImage localBufferedImage2 = null;
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
/* 1266 */         int i8 = 1;
/* 1267 */         if (i8 != 0) {
/* 1268 */           if (localObject1 == null) {
/* 1269 */             localBufferedImage2 = new BufferedImage(i4, i6, i1);
/*      */           } else {
/* 1271 */             localBufferedImage2 = new BufferedImage(i4, i6, i1, (IndexColorModel)localObject1);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1278 */           localObject3 = localBufferedImage2.createGraphics();
/* 1279 */           ((Graphics2D)localObject3).clipRect(0, 0, localBufferedImage2
/* 1280 */             .getWidth(), localBufferedImage2
/* 1281 */             .getHeight());
/*      */           
/* 1283 */           ((Graphics2D)localObject3).translate(-localRectangle2D1.getX(), 
/* 1284 */             -localRectangle2D1.getY());
/* 1285 */           ((Graphics2D)localObject3).transform(localAffineTransform2);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1290 */           if (paramColor == null) {
/* 1291 */             paramColor = Color.white;
/*      */           }
/*      */           
/* 1294 */           ((Graphics2D)localObject3).drawImage(localBufferedImage1, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramColor, null);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1302 */           ((Graphics2D)localObject3).dispose();
/*      */         } else {
/* 1304 */           localBufferedImage2 = localBufferedImage1;
/*      */         }
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
/* 1319 */         Object localObject3 = new Rectangle2D.Float((float)(localRectangle2D1.getX() * d1), (float)(localRectangle2D1.getY() * d2), (float)(localRectangle2D1.getWidth() * d1), (float)(localRectangle2D1.getHeight() * d2));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1324 */         WritableRaster localWritableRaster = localBufferedImage2.getRaster();
/*      */         byte[] arrayOfByte;
/* 1326 */         if ((localWritableRaster instanceof ByteComponentRaster)) {
/* 1327 */           arrayOfByte = ((ByteComponentRaster)localWritableRaster).getDataStorage();
/* 1328 */         } else if ((localWritableRaster instanceof BytePackedRaster)) {
/* 1329 */           arrayOfByte = ((BytePackedRaster)localWritableRaster).getDataStorage();
/*      */         } else {
/* 1331 */           return false;
/*      */         }
/*      */         
/* 1334 */         int i10 = 24;
/* 1335 */         SampleModel localSampleModel = localBufferedImage2.getSampleModel();
/* 1336 */         Object localObject4; if ((localSampleModel instanceof ComponentSampleModel)) {
/* 1337 */           localObject4 = (ComponentSampleModel)localSampleModel;
/* 1338 */           i10 = ((ComponentSampleModel)localObject4).getPixelStride() * 8;
/* 1339 */         } else if ((localSampleModel instanceof MultiPixelPackedSampleModel)) {
/* 1340 */           localObject4 = (MultiPixelPackedSampleModel)localSampleModel;
/*      */           
/* 1342 */           i10 = ((MultiPixelPackedSampleModel)localObject4).getPixelBitStride();
/*      */         }
/* 1344 */         else if (localObject1 != null) {
/* 1345 */           int i11 = localBufferedImage2.getWidth();
/* 1346 */           int i12 = localBufferedImage2.getHeight();
/* 1347 */           if ((i11 > 0) && (i12 > 0)) {
/* 1348 */             i10 = arrayOfByte.length * 8 / i11 / i12;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1360 */         Shape localShape2 = getClip();
/* 1361 */         clip(paramAffineTransform.createTransformedShape(localFloat));
/* 1362 */         deviceClip(getClip().getPathIterator(getTransform()));
/*      */         
/* 1364 */         localWPrinterJob
/* 1365 */           .drawDIBImage(arrayOfByte, ((Rectangle2D.Float)localObject3).x, ((Rectangle2D.Float)localObject3).y, 
/* 1366 */           (float)Math.rint(((Rectangle2D.Float)localObject3).width + 0.5D), 
/* 1367 */           (float)Math.rint(((Rectangle2D.Float)localObject3).height + 0.5D), 0.0F, 0.0F, localBufferedImage2
/*      */           
/* 1369 */           .getWidth(), localBufferedImage2.getHeight(), i10, (IndexColorModel)localObject1);
/*      */         
/*      */ 
/* 1372 */         setClip(localShape2);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1377 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void redrawRegion(Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2, Shape paramShape, AffineTransform paramAffineTransform)
/*      */     throws PrinterException
/*      */   {
/* 1389 */     WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/* 1390 */     Printable localPrintable = getPrintable();
/* 1391 */     PageFormat localPageFormat = getPageFormat();
/* 1392 */     int i = getPageIndex();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1399 */     BufferedImage localBufferedImage = new BufferedImage((int)paramRectangle2D.getWidth(), (int)paramRectangle2D.getHeight(), 5);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1408 */     Graphics2D localGraphics2D = localBufferedImage.createGraphics();
/* 1409 */     ProxyGraphics2D localProxyGraphics2D = new ProxyGraphics2D(localGraphics2D, localWPrinterJob);
/* 1410 */     localProxyGraphics2D.setColor(Color.white);
/* 1411 */     localProxyGraphics2D.fillRect(0, 0, localBufferedImage.getWidth(), localBufferedImage.getHeight());
/* 1412 */     localProxyGraphics2D.clipRect(0, 0, localBufferedImage.getWidth(), localBufferedImage.getHeight());
/*      */     
/* 1414 */     localProxyGraphics2D.translate(-paramRectangle2D.getX(), -paramRectangle2D.getY());
/*      */     
/*      */ 
/*      */ 
/* 1418 */     float f1 = (float)(localWPrinterJob.getXRes() / paramDouble1);
/* 1419 */     float f2 = (float)(localWPrinterJob.getYRes() / paramDouble2);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1425 */     localProxyGraphics2D.scale(f1 / 72.0F, f2 / 72.0F);
/*      */     
/*      */ 
/* 1428 */     localProxyGraphics2D.translate(
/* 1429 */       -localWPrinterJob.getPhysicalPrintableX(localPageFormat.getPaper()) / localWPrinterJob
/* 1430 */       .getXRes() * 72.0D, 
/* 1431 */       -localWPrinterJob.getPhysicalPrintableY(localPageFormat.getPaper()) / localWPrinterJob
/* 1432 */       .getYRes() * 72.0D);
/*      */     
/* 1434 */     localProxyGraphics2D.transform(new AffineTransform(getPageFormat().getMatrix()));
/* 1435 */     localProxyGraphics2D.setPaint(Color.black);
/*      */     
/* 1437 */     localPrintable.print(localProxyGraphics2D, localPageFormat, i);
/*      */     
/* 1439 */     localGraphics2D.dispose();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1448 */     if (paramShape != null) {
/* 1449 */       deviceClip(paramShape.getPathIterator(paramAffineTransform));
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
/*      */ 
/*      */ 
/*      */ 
/* 1464 */     Rectangle2D.Float localFloat = new Rectangle2D.Float((float)(paramRectangle2D.getX() * paramDouble1), (float)(paramRectangle2D.getY() * paramDouble2), (float)(paramRectangle2D.getWidth() * paramDouble1), (float)(paramRectangle2D.getHeight() * paramDouble2));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1470 */     ByteComponentRaster localByteComponentRaster = (ByteComponentRaster)localBufferedImage.getRaster();
/*      */     
/* 1472 */     localWPrinterJob.drawImage3ByteBGR(localByteComponentRaster.getDataStorage(), localFloat.x, localFloat.y, localFloat.width, localFloat.height, 0.0F, 0.0F, localBufferedImage
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1477 */       .getWidth(), localBufferedImage.getHeight());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void deviceFill(PathIterator paramPathIterator, Color paramColor)
/*      */   {
/* 1489 */     WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/*      */     
/* 1491 */     convertToWPath(paramPathIterator);
/* 1492 */     localWPrinterJob.selectSolidBrush(paramColor);
/* 1493 */     localWPrinterJob.fillPath();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void deviceClip(PathIterator paramPathIterator)
/*      */   {
/* 1504 */     WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/*      */     
/* 1506 */     convertToWPath(paramPathIterator);
/* 1507 */     localWPrinterJob.selectClipPath();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void deviceFrameRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
/*      */   {
/* 1517 */     AffineTransform localAffineTransform = getTransform();
/*      */     
/*      */ 
/* 1520 */     int i = localAffineTransform.getType();
/* 1521 */     int j = (i & 0x30) != 0 ? 1 : 0;
/*      */     
/*      */ 
/*      */ 
/* 1525 */     if (j != 0) {
/* 1526 */       draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
/* 1527 */       return;
/*      */     }
/*      */     
/* 1530 */     Stroke localStroke = getStroke();
/*      */     
/* 1532 */     if ((localStroke instanceof BasicStroke)) {
/* 1533 */       BasicStroke localBasicStroke = (BasicStroke)localStroke;
/*      */       
/* 1535 */       int k = localBasicStroke.getEndCap();
/* 1536 */       int m = localBasicStroke.getLineJoin();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1542 */       if ((k == 2) && (m == 0))
/*      */       {
/* 1544 */         if (localBasicStroke.getMiterLimit() == 10.0F)
/*      */         {
/* 1546 */           float f1 = localBasicStroke.getLineWidth();
/* 1547 */           Point2D.Float localFloat1 = new Point2D.Float(f1, f1);
/*      */           
/*      */ 
/* 1550 */           localAffineTransform.deltaTransform(localFloat1, localFloat1);
/* 1551 */           float f2 = Math.min(Math.abs(localFloat1.x), 
/* 1552 */             Math.abs(localFloat1.y));
/*      */           
/*      */ 
/* 1555 */           Point2D.Float localFloat2 = new Point2D.Float(paramInt1, paramInt2);
/* 1556 */           localAffineTransform.transform(localFloat2, localFloat2);
/*      */           
/*      */ 
/* 1559 */           Point2D.Float localFloat3 = new Point2D.Float(paramInt1 + paramInt3, paramInt2 + paramInt4);
/*      */           
/* 1561 */           localAffineTransform.transform(localFloat3, localFloat3);
/*      */           
/* 1563 */           float f3 = (float)(localFloat3.getX() - localFloat2.getX());
/* 1564 */           float f4 = (float)(localFloat3.getY() - localFloat2.getY());
/*      */           
/* 1566 */           WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/*      */           
/*      */ 
/* 1569 */           if (localWPrinterJob.selectStylePen(k, m, f2, paramColor) == true)
/*      */           {
/* 1571 */             localWPrinterJob.frameRect((float)localFloat2.getX(), 
/* 1572 */               (float)localFloat2.getY(), f3, f4);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 1577 */             double d = Math.min(localWPrinterJob.getXRes(), localWPrinterJob
/* 1578 */               .getYRes());
/*      */             
/* 1580 */             if (f2 / d < 0.014000000432133675D)
/*      */             {
/* 1582 */               localWPrinterJob.selectPen(f2, paramColor);
/* 1583 */               localWPrinterJob.frameRect((float)localFloat2.getX(), 
/* 1584 */                 (float)localFloat2.getY(), f3, f4);
/*      */             }
/*      */             else {
/* 1587 */               draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
/*      */             }
/*      */           }
/* 1590 */           return;
/*      */         } }
/* 1592 */       draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
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
/*      */   protected void deviceFillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
/*      */   {
/* 1609 */     AffineTransform localAffineTransform = getTransform();
/*      */     
/*      */ 
/* 1612 */     int i = localAffineTransform.getType();
/* 1613 */     int j = (i & 0x30) != 0 ? 1 : 0;
/*      */     
/*      */ 
/* 1616 */     if (j != 0) {
/* 1617 */       fill(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
/* 1618 */       return;
/*      */     }
/*      */     
/* 1621 */     Point2D.Float localFloat1 = new Point2D.Float(paramInt1, paramInt2);
/* 1622 */     localAffineTransform.transform(localFloat1, localFloat1);
/*      */     
/* 1624 */     Point2D.Float localFloat2 = new Point2D.Float(paramInt1 + paramInt3, paramInt2 + paramInt4);
/* 1625 */     localAffineTransform.transform(localFloat2, localFloat2);
/*      */     
/* 1627 */     float f1 = (float)(localFloat2.getX() - localFloat1.getX());
/* 1628 */     float f2 = (float)(localFloat2.getY() - localFloat1.getY());
/*      */     
/* 1630 */     WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/* 1631 */     localWPrinterJob.fillRect((float)localFloat1.getX(), (float)localFloat1.getY(), f1, f2, paramColor);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void deviceDrawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
/*      */   {
/* 1643 */     Stroke localStroke = getStroke();
/*      */     
/* 1645 */     if ((localStroke instanceof BasicStroke)) {
/* 1646 */       BasicStroke localBasicStroke = (BasicStroke)localStroke;
/*      */       
/* 1648 */       if (localBasicStroke.getDashArray() != null) {
/* 1649 */         draw(new Line2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
/* 1650 */         return;
/*      */       }
/*      */       
/* 1653 */       float f1 = localBasicStroke.getLineWidth();
/* 1654 */       Point2D.Float localFloat1 = new Point2D.Float(f1, f1);
/*      */       
/* 1656 */       AffineTransform localAffineTransform = getTransform();
/* 1657 */       localAffineTransform.deltaTransform(localFloat1, localFloat1);
/*      */       
/* 1659 */       float f2 = Math.min(Math.abs(localFloat1.x), 
/* 1660 */         Math.abs(localFloat1.y));
/*      */       
/* 1662 */       Point2D.Float localFloat2 = new Point2D.Float(paramInt1, paramInt2);
/* 1663 */       localAffineTransform.transform(localFloat2, localFloat2);
/*      */       
/* 1665 */       Point2D.Float localFloat3 = new Point2D.Float(paramInt3, paramInt4);
/* 1666 */       localAffineTransform.transform(localFloat3, localFloat3);
/*      */       
/* 1668 */       int i = localBasicStroke.getEndCap();
/* 1669 */       int j = localBasicStroke.getLineJoin();
/*      */       
/*      */ 
/* 1672 */       if ((localFloat3.getX() == localFloat2.getX()) && 
/* 1673 */         (localFloat3.getY() == localFloat2.getY()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1678 */         i = 1;
/*      */       }
/*      */       
/*      */ 
/* 1682 */       WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/*      */       
/*      */ 
/* 1685 */       if (localWPrinterJob.selectStylePen(i, j, f2, paramColor))
/*      */       {
/* 1687 */         localWPrinterJob.moveTo((float)localFloat2.getX(), 
/* 1688 */           (float)localFloat2.getY());
/* 1689 */         localWPrinterJob.lineTo((float)localFloat3.getX(), 
/* 1690 */           (float)localFloat3.getY());
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1700 */         double d = Math.min(localWPrinterJob.getXRes(), localWPrinterJob
/* 1701 */           .getYRes());
/*      */         
/* 1703 */         if ((i == 1) || (((paramInt1 == paramInt3) || (paramInt2 == paramInt4)) && (f2 / d < 0.014000000432133675D)))
/*      */         {
/*      */ 
/*      */ 
/* 1707 */           localWPrinterJob.selectPen(f2, paramColor);
/* 1708 */           localWPrinterJob.moveTo((float)localFloat2.getX(), 
/* 1709 */             (float)localFloat2.getY());
/* 1710 */           localWPrinterJob.lineTo((float)localFloat3.getX(), 
/* 1711 */             (float)localFloat3.getY());
/*      */         }
/*      */         else {
/* 1714 */           draw(new Line2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
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
/*      */   private void convertToWPath(PathIterator paramPathIterator)
/*      */   {
/* 1728 */     float[] arrayOfFloat = new float[6];
/*      */     
/*      */ 
/* 1731 */     WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
/*      */     
/*      */ 
/*      */     int j;
/*      */     
/*      */ 
/* 1737 */     if (paramPathIterator.getWindingRule() == 0) {
/* 1738 */       j = 1;
/*      */     } else {
/* 1740 */       j = 2;
/*      */     }
/* 1742 */     localWPrinterJob.setPolyFillMode(j);
/*      */     
/* 1744 */     localWPrinterJob.beginPath();
/*      */     
/* 1746 */     while (!paramPathIterator.isDone()) {
/* 1747 */       int i = paramPathIterator.currentSegment(arrayOfFloat);
/*      */       
/* 1749 */       switch (i) {
/*      */       case 0: 
/* 1751 */         localWPrinterJob.moveTo(arrayOfFloat[0], arrayOfFloat[1]);
/* 1752 */         break;
/*      */       
/*      */       case 1: 
/* 1755 */         localWPrinterJob.lineTo(arrayOfFloat[0], arrayOfFloat[1]);
/* 1756 */         break;
/*      */       
/*      */ 
/*      */ 
/*      */       case 2: 
/* 1761 */         int k = localWPrinterJob.getPenX();
/* 1762 */         int m = localWPrinterJob.getPenY();
/* 1763 */         float f1 = k + (arrayOfFloat[0] - k) * 2.0F / 3.0F;
/* 1764 */         float f2 = m + (arrayOfFloat[1] - m) * 2.0F / 3.0F;
/* 1765 */         float f3 = arrayOfFloat[2] - (arrayOfFloat[2] - arrayOfFloat[0]) * 2.0F / 3.0F;
/* 1766 */         float f4 = arrayOfFloat[3] - (arrayOfFloat[3] - arrayOfFloat[1]) * 2.0F / 3.0F;
/* 1767 */         localWPrinterJob.polyBezierTo(f1, f2, f3, f4, arrayOfFloat[2], arrayOfFloat[3]);
/*      */         
/*      */ 
/* 1770 */         break;
/*      */       
/*      */       case 3: 
/* 1773 */         localWPrinterJob.polyBezierTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
/*      */         
/*      */ 
/* 1776 */         break;
/*      */       
/*      */       case 4: 
/* 1779 */         localWPrinterJob.closeFigure();
/*      */       }
/*      */       
/*      */       
/*      */ 
/* 1784 */       paramPathIterator.next();
/*      */     }
/*      */     
/* 1787 */     localWPrinterJob.endPath();
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WPathGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
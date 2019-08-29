/*      */ package sun.java2d;
/*      */ 
/*      */ import java.awt.AlphaComposite;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Composite;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.GradientPaint;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.Image;
/*      */ import java.awt.LinearGradientPaint;
/*      */ import java.awt.Paint;
/*      */ import java.awt.RadialGradientPaint;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.RenderingHints.Key;
/*      */ import java.awt.Shape;
/*      */ import java.awt.Stroke;
/*      */ import java.awt.TexturePaint;
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.font.GlyphVector;
/*      */ import java.awt.font.TextLayout;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Area;
/*      */ import java.awt.geom.GeneralPath;
/*      */ import java.awt.geom.NoninvertibleTransformException;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.Rectangle2D.Double;
/*      */ import java.awt.geom.Rectangle2D.Float;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.BufferedImageOp;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.Raster;
/*      */ import java.awt.image.RenderedImage;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.awt.image.renderable.RenderContext;
/*      */ import java.awt.image.renderable.RenderableImage;
/*      */ import java.text.AttributedCharacterIterator;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import sun.awt.ConstrainableGraphics;
/*      */ import sun.awt.SunHints;
/*      */ import sun.awt.SunHints.Key;
/*      */ import sun.awt.SunHints.Value;
/*      */ import sun.awt.image.MultiResolutionImage;
/*      */ import sun.awt.image.MultiResolutionToolkitImage;
/*      */ import sun.awt.image.SurfaceManager;
/*      */ import sun.awt.image.ToolkitImage;
/*      */ import sun.font.Font2D;
/*      */ import sun.font.FontDesignMetrics;
/*      */ import sun.font.FontUtilities;
/*      */ import sun.java2d.loops.Blit;
/*      */ import sun.java2d.loops.CompositeType;
/*      */ import sun.java2d.loops.FontInfo;
/*      */ import sun.java2d.loops.MaskFill;
/*      */ import sun.java2d.loops.RenderLoops;
/*      */ import sun.java2d.loops.SurfaceType;
/*      */ import sun.java2d.loops.XORComposite;
/*      */ import sun.java2d.pipe.DrawImagePipe;
/*      */ import sun.java2d.pipe.LoopPipe;
/*      */ import sun.java2d.pipe.PixelDrawPipe;
/*      */ import sun.java2d.pipe.PixelFillPipe;
/*      */ import sun.java2d.pipe.Region;
/*      */ import sun.java2d.pipe.RenderingEngine;
/*      */ import sun.java2d.pipe.ShapeDrawPipe;
/*      */ import sun.java2d.pipe.ShapeSpanIterator;
/*      */ import sun.java2d.pipe.TextPipe;
/*      */ import sun.java2d.pipe.ValidatePipe;
/*      */ import sun.misc.PerformanceLogger;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class SunGraphics2D
/*      */   extends Graphics2D
/*      */   implements ConstrainableGraphics, Cloneable, DestSurfaceProvider
/*      */ {
/*      */   public static final int PAINT_CUSTOM = 6;
/*      */   public static final int PAINT_TEXTURE = 5;
/*      */   public static final int PAINT_RAD_GRADIENT = 4;
/*      */   public static final int PAINT_LIN_GRADIENT = 3;
/*      */   public static final int PAINT_GRADIENT = 2;
/*      */   public static final int PAINT_ALPHACOLOR = 1;
/*      */   public static final int PAINT_OPAQUECOLOR = 0;
/*      */   public static final int COMP_CUSTOM = 3;
/*      */   public static final int COMP_XOR = 2;
/*      */   public static final int COMP_ALPHA = 1;
/*      */   public static final int COMP_ISCOPY = 0;
/*      */   public static final int STROKE_CUSTOM = 3;
/*      */   public static final int STROKE_WIDE = 2;
/*      */   public static final int STROKE_THINDASHED = 1;
/*      */   public static final int STROKE_THIN = 0;
/*      */   public static final int TRANSFORM_GENERIC = 4;
/*      */   public static final int TRANSFORM_TRANSLATESCALE = 3;
/*      */   public static final int TRANSFORM_ANY_TRANSLATE = 2;
/*      */   public static final int TRANSFORM_INT_TRANSLATE = 1;
/*      */   public static final int TRANSFORM_ISIDENT = 0;
/*      */   public static final int CLIP_SHAPE = 2;
/*      */   public static final int CLIP_RECTANGULAR = 1;
/*      */   public static final int CLIP_DEVICE = 0;
/*      */   public int eargb;
/*      */   public int pixel;
/*      */   public SurfaceData surfaceData;
/*      */   public PixelDrawPipe drawpipe;
/*      */   public PixelFillPipe fillpipe;
/*      */   public DrawImagePipe imagepipe;
/*      */   public ShapeDrawPipe shapepipe;
/*      */   public TextPipe textpipe;
/*      */   public MaskFill alphafill;
/*      */   public RenderLoops loops;
/*      */   public CompositeType imageComp;
/*      */   public int paintState;
/*      */   public int compositeState;
/*      */   public int strokeState;
/*      */   public int transformState;
/*      */   public int clipState;
/*      */   public Color foregroundColor;
/*      */   public Color backgroundColor;
/*      */   public AffineTransform transform;
/*      */   public int transX;
/*      */   public int transY;
/*  211 */   protected static final Stroke defaultStroke = new BasicStroke();
/*  212 */   protected static final Composite defaultComposite = AlphaComposite.SrcOver;
/*  213 */   private static final Font defaultFont = new Font("Dialog", 0, 12);
/*      */   
/*      */   public Paint paint;
/*      */   
/*      */   public Stroke stroke;
/*      */   
/*      */   public Composite composite;
/*      */   
/*      */   protected Font font;
/*      */   
/*      */   protected FontMetrics fontMetrics;
/*      */   public int renderHint;
/*      */   public int antialiasHint;
/*      */   public int textAntialiasHint;
/*      */   protected int fractionalMetricsHint;
/*      */   public int lcdTextContrast;
/*  229 */   private static int lcdTextContrastDefaultValue = 140;
/*      */   
/*      */   private int interpolationHint;
/*      */   
/*      */   public int strokeHint;
/*      */   
/*      */   public int interpolationType;
/*      */   
/*      */   public RenderingHints hints;
/*      */   
/*      */   public Region constrainClip;
/*      */   
/*      */   public int constrainX;
/*      */   
/*      */   public int constrainY;
/*      */   
/*      */   public Region clipRegion;
/*      */   
/*      */   public Shape usrClip;
/*      */   
/*      */   protected Region devClip;
/*      */   private final int devScale;
/*      */   private int resolutionVariantHint;
/*      */   private boolean validFontInfo;
/*      */   private FontInfo fontInfo;
/*      */   private FontInfo glyphVectorFontInfo;
/*      */   private FontRenderContext glyphVectorFRC;
/*      */   private static final int slowTextTransformMask = 120;
/*      */   protected static ValidatePipe invalidpipe;
/*      */   private static final double[] IDENT_MATRIX;
/*      */   private static final AffineTransform IDENT_ATX;
/*      */   private static final int MINALLOCATED = 8;
/*      */   private static final int TEXTARRSIZE = 17;
/*      */   private static double[][] textTxArr;
/*      */   private static AffineTransform[] textAtArr;
/*      */   static final int NON_UNIFORM_SCALE_MASK = 36;
/*      */   
/*      */   public SunGraphics2D(SurfaceData paramSurfaceData, Color paramColor1, Color paramColor2, Font paramFont)
/*      */   {
/*  268 */     this.surfaceData = paramSurfaceData;
/*  269 */     this.foregroundColor = paramColor1;
/*  270 */     this.backgroundColor = paramColor2;
/*      */     
/*  272 */     this.transform = new AffineTransform();
/*  273 */     this.stroke = defaultStroke;
/*  274 */     this.composite = defaultComposite;
/*  275 */     this.paint = this.foregroundColor;
/*      */     
/*  277 */     this.imageComp = CompositeType.SrcOverNoEa;
/*      */     
/*  279 */     this.renderHint = 0;
/*  280 */     this.antialiasHint = 1;
/*  281 */     this.textAntialiasHint = 0;
/*  282 */     this.fractionalMetricsHint = 1;
/*  283 */     this.lcdTextContrast = lcdTextContrastDefaultValue;
/*  284 */     this.interpolationHint = -1;
/*  285 */     this.strokeHint = 0;
/*  286 */     this.resolutionVariantHint = 0;
/*      */     
/*  288 */     this.interpolationType = 1;
/*      */     
/*  290 */     validateColor();
/*      */     
/*  292 */     this.devScale = paramSurfaceData.getDefaultScale();
/*  293 */     if (this.devScale != 1) {
/*  294 */       this.transform.setToScale(this.devScale, this.devScale);
/*  295 */       invalidateTransform();
/*      */     }
/*      */     
/*  298 */     this.font = paramFont;
/*  299 */     if (this.font == null) {
/*  300 */       this.font = defaultFont;
/*      */     }
/*      */     
/*  303 */     setDevClip(paramSurfaceData.getBounds());
/*  304 */     invalidatePipe();
/*      */   }
/*      */   
/*      */   protected Object clone() {
/*      */     try {
/*  309 */       SunGraphics2D localSunGraphics2D = (SunGraphics2D)super.clone();
/*  310 */       localSunGraphics2D.transform = new AffineTransform(this.transform);
/*  311 */       if (this.hints != null) {
/*  312 */         localSunGraphics2D.hints = ((RenderingHints)this.hints.clone());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  320 */       if (this.fontInfo != null) {
/*  321 */         if (this.validFontInfo) {
/*  322 */           localSunGraphics2D.fontInfo = ((FontInfo)this.fontInfo.clone());
/*      */         } else {
/*  324 */           localSunGraphics2D.fontInfo = null;
/*      */         }
/*      */       }
/*  327 */       if (this.glyphVectorFontInfo != null)
/*      */       {
/*  329 */         localSunGraphics2D.glyphVectorFontInfo = ((FontInfo)this.glyphVectorFontInfo.clone());
/*  330 */         localSunGraphics2D.glyphVectorFRC = this.glyphVectorFRC;
/*      */       }
/*      */       
/*  333 */       return localSunGraphics2D;
/*      */     }
/*      */     catch (CloneNotSupportedException localCloneNotSupportedException) {}
/*  336 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Graphics create()
/*      */   {
/*  343 */     return (Graphics)clone();
/*      */   }
/*      */   
/*      */   public void setDevClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  347 */     Region localRegion = this.constrainClip;
/*  348 */     if (localRegion == null) {
/*  349 */       this.devClip = Region.getInstanceXYWH(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } else {
/*  351 */       this.devClip = localRegion.getIntersectionXYWH(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*  353 */     validateCompClip();
/*      */   }
/*      */   
/*      */   public void setDevClip(Rectangle paramRectangle) {
/*  357 */     setDevClip(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void constrain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Region paramRegion)
/*      */   {
/*  364 */     if ((paramInt1 | paramInt2) != 0) {
/*  365 */       translate(paramInt1, paramInt2);
/*      */     }
/*  367 */     if (this.transformState > 3) {
/*  368 */       clipRect(0, 0, paramInt3, paramInt4);
/*  369 */       return;
/*      */     }
/*      */     
/*  372 */     double d1 = this.transform.getScaleX();
/*  373 */     double d2 = this.transform.getScaleY();
/*  374 */     paramInt1 = this.constrainX = (int)this.transform.getTranslateX();
/*  375 */     paramInt2 = this.constrainY = (int)this.transform.getTranslateY();
/*  376 */     paramInt3 = Region.dimAdd(paramInt1, Region.clipScale(paramInt3, d1));
/*  377 */     paramInt4 = Region.dimAdd(paramInt2, Region.clipScale(paramInt4, d2));
/*      */     
/*  379 */     Region localRegion = this.constrainClip;
/*  380 */     if (localRegion == null) {
/*  381 */       localRegion = Region.getInstanceXYXY(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } else {
/*  383 */       localRegion = localRegion.getIntersectionXYXY(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*  385 */     if (paramRegion != null) {
/*  386 */       paramRegion = paramRegion.getScaledRegion(d1, d2);
/*  387 */       paramRegion = paramRegion.getTranslatedRegion(paramInt1, paramInt2);
/*  388 */       localRegion = localRegion.getIntersection(paramRegion);
/*      */     }
/*      */     
/*  391 */     if (localRegion == this.constrainClip)
/*      */     {
/*  393 */       return;
/*      */     }
/*      */     
/*  396 */     this.constrainClip = localRegion;
/*  397 */     if (!this.devClip.isInsideQuickCheck(localRegion)) {
/*  398 */       this.devClip = this.devClip.getIntersection(localRegion);
/*  399 */       validateCompClip();
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
/*      */   public void constrain(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  417 */     constrain(paramInt1, paramInt2, paramInt3, paramInt4, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void invalidatePipe()
/*      */   {
/*  426 */     this.drawpipe = invalidpipe;
/*  427 */     this.fillpipe = invalidpipe;
/*  428 */     this.shapepipe = invalidpipe;
/*  429 */     this.textpipe = invalidpipe;
/*  430 */     this.imagepipe = invalidpipe;
/*  431 */     this.loops = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void validatePipe()
/*      */   {
/*  442 */     if (!this.surfaceData.isValid()) {
/*  443 */       throw new InvalidPipeException("attempt to validate Pipe with invalid SurfaceData");
/*      */     }
/*      */     
/*  446 */     this.surfaceData.validatePipe(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   Shape intersectShapes(Shape paramShape1, Shape paramShape2, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  457 */     if (((paramShape1 instanceof Rectangle)) && ((paramShape2 instanceof Rectangle))) {
/*  458 */       return ((Rectangle)paramShape1).intersection((Rectangle)paramShape2);
/*      */     }
/*  460 */     if ((paramShape1 instanceof Rectangle2D))
/*  461 */       return intersectRectShape((Rectangle2D)paramShape1, paramShape2, paramBoolean1, paramBoolean2);
/*  462 */     if ((paramShape2 instanceof Rectangle2D)) {
/*  463 */       return intersectRectShape((Rectangle2D)paramShape2, paramShape1, paramBoolean2, paramBoolean1);
/*      */     }
/*  465 */     return intersectByArea(paramShape1, paramShape2, paramBoolean1, paramBoolean2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   Shape intersectRectShape(Rectangle2D paramRectangle2D, Shape paramShape, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  477 */     if ((paramShape instanceof Rectangle2D)) {
/*  478 */       Rectangle2D localRectangle2D = (Rectangle2D)paramShape;
/*      */       Object localObject;
/*  480 */       if (!paramBoolean1) {
/*  481 */         localObject = paramRectangle2D;
/*  482 */       } else if (!paramBoolean2) {
/*  483 */         localObject = localRectangle2D;
/*      */       } else {
/*  485 */         localObject = new Float();
/*      */       }
/*  487 */       double d1 = Math.max(paramRectangle2D.getX(), localRectangle2D.getX());
/*  488 */       double d2 = Math.min(paramRectangle2D.getX() + paramRectangle2D.getWidth(), localRectangle2D
/*  489 */         .getX() + localRectangle2D.getWidth());
/*  490 */       double d3 = Math.max(paramRectangle2D.getY(), localRectangle2D.getY());
/*  491 */       double d4 = Math.min(paramRectangle2D.getY() + paramRectangle2D.getHeight(), localRectangle2D
/*  492 */         .getY() + localRectangle2D.getHeight());
/*      */       
/*  494 */       if ((d2 - d1 < 0.0D) || (d4 - d3 < 0.0D))
/*      */       {
/*  496 */         ((Rectangle2D)localObject).setFrameFromDiagonal(0.0D, 0.0D, 0.0D, 0.0D);
/*      */       } else
/*  498 */         ((Rectangle2D)localObject).setFrameFromDiagonal(d1, d3, d2, d4);
/*  499 */       return (Shape)localObject;
/*      */     }
/*  501 */     if (paramRectangle2D.contains(paramShape.getBounds2D())) {
/*  502 */       if (paramBoolean2) {
/*  503 */         paramShape = cloneShape(paramShape);
/*      */       }
/*  505 */       return paramShape;
/*      */     }
/*  507 */     return intersectByArea(paramRectangle2D, paramShape, paramBoolean1, paramBoolean2);
/*      */   }
/*      */   
/*      */   protected static Shape cloneShape(Shape paramShape) {
/*  511 */     return new GeneralPath(paramShape);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   Shape intersectByArea(Shape paramShape1, Shape paramShape2, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*      */     Area localArea1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  528 */     if ((!paramBoolean1) && ((paramShape1 instanceof Area))) {
/*  529 */       localArea1 = (Area)paramShape1;
/*  530 */     } else if ((!paramBoolean2) && ((paramShape2 instanceof Area))) {
/*  531 */       localArea1 = (Area)paramShape2;
/*  532 */       paramShape2 = paramShape1;
/*      */     } else {
/*  534 */       localArea1 = new Area(paramShape1);
/*      */     }
/*      */     Area localArea2;
/*  537 */     if ((paramShape2 instanceof Area)) {
/*  538 */       localArea2 = (Area)paramShape2;
/*      */     } else {
/*  540 */       localArea2 = new Area(paramShape2);
/*      */     }
/*      */     
/*  543 */     localArea1.intersect(localArea2);
/*  544 */     if (localArea1.isRectangular()) {
/*  545 */       return localArea1.getBounds();
/*      */     }
/*      */     
/*  548 */     return localArea1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Region getCompClip()
/*      */   {
/*  556 */     if (!this.surfaceData.isValid())
/*      */     {
/*  558 */       revalidateAll();
/*      */     }
/*      */     
/*  561 */     return this.clipRegion;
/*      */   }
/*      */   
/*      */   public Font getFont() {
/*  565 */     if (this.font == null) {
/*  566 */       this.font = defaultFont;
/*      */     }
/*  568 */     return this.font;
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  262 */     if (PerformanceLogger.loggingEnabled()) {
/*  263 */       PerformanceLogger.setTime("SunGraphics2D static initialization");
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
/*  420 */     invalidpipe = new ValidatePipe();
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
/*  571 */     IDENT_MATRIX = new double[] { 1.0D, 0.0D, 0.0D, 1.0D };
/*  572 */     IDENT_ATX = new AffineTransform();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  577 */     textTxArr = new double[17][];
/*  578 */     textAtArr = new AffineTransform[17];
/*      */     
/*      */ 
/*      */ 
/*  582 */     for (int i = 8; i < 17; i++) {
/*  583 */       textTxArr[i] = { i, 0.0D, 0.0D, i };
/*  584 */       textAtArr[i] = new AffineTransform(textTxArr[i]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public FontInfo checkFontInfo(FontInfo paramFontInfo, Font paramFont, FontRenderContext paramFontRenderContext)
/*      */   {
/*  595 */     if (paramFontInfo == null) {
/*  596 */       paramFontInfo = new FontInfo();
/*      */     }
/*      */     
/*  599 */     float f = paramFont.getSize2D();
/*      */     
/*  601 */     AffineTransform localAffineTransform2 = null;
/*  602 */     int i; AffineTransform localAffineTransform1; double d3; if (paramFont.isTransformed()) {
/*  603 */       localAffineTransform2 = paramFont.getTransform();
/*  604 */       localAffineTransform2.scale(f, f);
/*  605 */       i = localAffineTransform2.getType();
/*  606 */       paramFontInfo.originX = ((float)localAffineTransform2.getTranslateX());
/*  607 */       paramFontInfo.originY = ((float)localAffineTransform2.getTranslateY());
/*  608 */       localAffineTransform2.translate(-paramFontInfo.originX, -paramFontInfo.originY);
/*  609 */       if (this.transformState >= 3) {
/*  610 */         this.transform.getMatrix(paramFontInfo.devTx = new double[4]);
/*  611 */         localAffineTransform1 = new AffineTransform(paramFontInfo.devTx);
/*  612 */         localAffineTransform2.preConcatenate(localAffineTransform1);
/*      */       } else {
/*  614 */         paramFontInfo.devTx = IDENT_MATRIX;
/*  615 */         localAffineTransform1 = IDENT_ATX;
/*      */       }
/*  617 */       localAffineTransform2.getMatrix(paramFontInfo.glyphTx = new double[4]);
/*  618 */       double d1 = localAffineTransform2.getShearX();
/*  619 */       d3 = localAffineTransform2.getScaleY();
/*  620 */       if (d1 != 0.0D) {
/*  621 */         d3 = Math.sqrt(d1 * d1 + d3 * d3);
/*      */       }
/*  623 */       paramFontInfo.pixelHeight = ((int)(Math.abs(d3) + 0.5D));
/*      */     } else {
/*  625 */       i = 0;
/*  626 */       paramFontInfo.originX = (paramFontInfo.originY = 0.0F);
/*  627 */       if (this.transformState >= 3) {
/*  628 */         this.transform.getMatrix(paramFontInfo.devTx = new double[4]);
/*  629 */         localAffineTransform1 = new AffineTransform(paramFontInfo.devTx);
/*  630 */         paramFontInfo.glyphTx = new double[4];
/*  631 */         for (int j = 0; j < 4; j++) {
/*  632 */           paramFontInfo.glyphTx[j] = (paramFontInfo.devTx[j] * f);
/*      */         }
/*  634 */         localAffineTransform2 = new AffineTransform(paramFontInfo.glyphTx);
/*  635 */         double d2 = this.transform.getShearX();
/*  636 */         d3 = this.transform.getScaleY();
/*  637 */         if (d2 != 0.0D) {
/*  638 */           d3 = Math.sqrt(d2 * d2 + d3 * d3);
/*      */         }
/*  640 */         paramFontInfo.pixelHeight = ((int)(Math.abs(d3 * f) + 0.5D));
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  650 */         k = (int)f;
/*  651 */         if ((f == k) && (k >= 8) && (k < 17))
/*      */         {
/*  653 */           paramFontInfo.glyphTx = textTxArr[k];
/*  654 */           localAffineTransform2 = textAtArr[k];
/*  655 */           paramFontInfo.pixelHeight = k;
/*      */         } else {
/*  657 */           paramFontInfo.pixelHeight = ((int)(f + 0.5D));
/*      */         }
/*  659 */         if (localAffineTransform2 == null) {
/*  660 */           paramFontInfo.glyphTx = new double[] { f, 0.0D, 0.0D, f };
/*  661 */           localAffineTransform2 = new AffineTransform(paramFontInfo.glyphTx);
/*      */         }
/*      */         
/*  664 */         paramFontInfo.devTx = IDENT_MATRIX;
/*  665 */         localAffineTransform1 = IDENT_ATX;
/*      */       }
/*      */     }
/*      */     
/*  669 */     paramFontInfo.font2D = FontUtilities.getFont2D(paramFont);
/*      */     
/*  671 */     int k = this.fractionalMetricsHint;
/*  672 */     if (k == 0) {
/*  673 */       k = 1;
/*      */     }
/*  675 */     paramFontInfo.lcdSubPixPos = false;
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
/*      */     int m;
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
/*  700 */     if (paramFontRenderContext == null) {
/*  701 */       m = this.textAntialiasHint;
/*      */     } else {
/*  703 */       m = ((Value)paramFontRenderContext.getAntiAliasingHint()).getIndex();
/*      */     }
/*  705 */     if (m == 0) {
/*  706 */       if (this.antialiasHint == 2) {
/*  707 */         m = 2;
/*      */       } else {
/*  709 */         m = 1;
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*  718 */     else if (m == 3) {
/*  719 */       if (paramFontInfo.font2D.useAAForPtSize(paramFontInfo.pixelHeight)) {
/*  720 */         m = 2;
/*      */       } else {
/*  722 */         m = 1;
/*      */       }
/*  724 */     } else if (m >= 4)
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
/*  741 */       if (!this.surfaceData.canRenderLCDText(this))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  746 */         m = 2;
/*      */       } else {
/*  748 */         paramFontInfo.lcdRGBOrder = true;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  756 */         if (m == 5) {
/*  757 */           m = 4;
/*  758 */           paramFontInfo.lcdRGBOrder = false;
/*  759 */         } else if (m == 7)
/*      */         {
/*  761 */           m = 6;
/*  762 */           paramFontInfo.lcdRGBOrder = false;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  767 */         paramFontInfo.lcdSubPixPos = ((k == 2) && (m == 4));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  773 */     paramFontInfo.aaHint = m;
/*  774 */     paramFontInfo.fontStrike = paramFontInfo.font2D.getStrike(paramFont, localAffineTransform1, localAffineTransform2, m, k);
/*      */     
/*  776 */     return paramFontInfo;
/*      */   }
/*      */   
/*      */   public static boolean isRotated(double[] paramArrayOfDouble) {
/*  780 */     if ((paramArrayOfDouble[0] == paramArrayOfDouble[3]) && (paramArrayOfDouble[1] == 0.0D) && (paramArrayOfDouble[2] == 0.0D) && (paramArrayOfDouble[0] > 0.0D))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  785 */       return false;
/*      */     }
/*      */     
/*  788 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFont(Font paramFont)
/*      */   {
/*  797 */     if ((paramFont != null) && (paramFont != this.font))
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
/*  812 */       if ((this.textAntialiasHint == 3) && (this.textpipe != invalidpipe)) { if (this.transformState <= 2)
/*      */         {
/*      */ 
/*  815 */           if ((!paramFont.isTransformed()) && (this.fontInfo != null))
/*      */           {
/*      */ 
/*      */ 
/*  819 */             if ((this.fontInfo.aaHint == 2) == FontUtilities.getFont2D(paramFont).useAAForPtSize(paramFont.getSize())) break label89; } }
/*  820 */         this.textpipe = invalidpipe; }
/*      */       label89:
/*  822 */       this.font = paramFont;
/*  823 */       this.fontMetrics = null;
/*  824 */       this.validFontInfo = false;
/*      */     }
/*      */   }
/*      */   
/*      */   public FontInfo getFontInfo() {
/*  829 */     if (!this.validFontInfo) {
/*  830 */       this.fontInfo = checkFontInfo(this.fontInfo, this.font, null);
/*  831 */       this.validFontInfo = true;
/*      */     }
/*  833 */     return this.fontInfo;
/*      */   }
/*      */   
/*      */   public FontInfo getGVFontInfo(Font paramFont, FontRenderContext paramFontRenderContext)
/*      */   {
/*  838 */     if ((this.glyphVectorFontInfo != null) && (this.glyphVectorFontInfo.font == paramFont) && (this.glyphVectorFRC == paramFontRenderContext))
/*      */     {
/*      */ 
/*  841 */       return this.glyphVectorFontInfo;
/*      */     }
/*  843 */     this.glyphVectorFRC = paramFontRenderContext;
/*  844 */     return 
/*  845 */       this.glyphVectorFontInfo = checkFontInfo(this.glyphVectorFontInfo, paramFont, paramFontRenderContext);
/*      */   }
/*      */   
/*      */   public FontMetrics getFontMetrics()
/*      */   {
/*  850 */     if (this.fontMetrics != null) {
/*  851 */       return this.fontMetrics;
/*      */     }
/*      */     
/*  854 */     return 
/*  855 */       this.fontMetrics = FontDesignMetrics.getMetrics(this.font, getFontRenderContext());
/*      */   }
/*      */   
/*      */   public FontMetrics getFontMetrics(Font paramFont) {
/*  859 */     if ((this.fontMetrics != null) && (paramFont == this.font)) {
/*  860 */       return this.fontMetrics;
/*      */     }
/*      */     
/*  863 */     FontDesignMetrics localFontDesignMetrics = FontDesignMetrics.getMetrics(paramFont, getFontRenderContext());
/*      */     
/*  865 */     if (this.font == paramFont) {
/*  866 */       this.fontMetrics = localFontDesignMetrics;
/*      */     }
/*  868 */     return localFontDesignMetrics;
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
/*      */   public boolean hit(Rectangle paramRectangle, Shape paramShape, boolean paramBoolean)
/*      */   {
/*  889 */     if (paramBoolean) {
/*  890 */       paramShape = this.stroke.createStrokedShape(paramShape);
/*      */     }
/*      */     
/*  893 */     paramShape = transformShape(paramShape);
/*  894 */     if ((this.constrainX | this.constrainY) != 0) {
/*  895 */       paramRectangle = new Rectangle(paramRectangle);
/*  896 */       paramRectangle.translate(this.constrainX, this.constrainY);
/*      */     }
/*      */     
/*  899 */     return paramShape.intersects(paramRectangle);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ColorModel getDeviceColorModel()
/*      */   {
/*  906 */     return this.surfaceData.getColorModel();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public GraphicsConfiguration getDeviceConfiguration()
/*      */   {
/*  913 */     return this.surfaceData.getDeviceConfiguration();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public final SurfaceData getSurfaceData()
/*      */   {
/*  921 */     return this.surfaceData;
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
/*      */   public void setComposite(Composite paramComposite)
/*      */   {
/*  935 */     if (this.composite == paramComposite) {
/*      */       return;
/*      */     }
/*      */     CompositeType localCompositeType;
/*      */     int i;
/*  940 */     if ((paramComposite instanceof AlphaComposite)) {
/*  941 */       AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
/*  942 */       localCompositeType = CompositeType.forAlphaComposite(localAlphaComposite);
/*  943 */       if (localCompositeType == CompositeType.SrcOverNoEa) {
/*  944 */         if (this.paintState != 0) { if (this.paintState > 1)
/*      */           {
/*  946 */             if (this.paint.getTransparency() != 1) {} }
/*      */         } else {
/*  948 */           i = 0;
/*      */           break label124; }
/*  950 */         i = 1;
/*      */       }
/*  952 */       else if ((localCompositeType == CompositeType.SrcNoEa) || (localCompositeType == CompositeType.Src) || (localCompositeType == CompositeType.Clear))
/*      */       {
/*      */ 
/*      */ 
/*  956 */         i = 0;
/*  957 */       } else if ((this.surfaceData.getTransparency() == 1) && (localCompositeType == CompositeType.SrcIn))
/*      */       {
/*      */ 
/*  960 */         i = 0;
/*      */       } else {
/*  962 */         i = 1;
/*      */       } } else { label124:
/*  964 */       if ((paramComposite instanceof XORComposite)) {
/*  965 */         i = 2;
/*  966 */         localCompositeType = CompositeType.Xor;
/*  967 */       } else { if (paramComposite == null) {
/*  968 */           throw new IllegalArgumentException("null Composite");
/*      */         }
/*  970 */         this.surfaceData.checkCustomComposite();
/*  971 */         i = 3;
/*  972 */         localCompositeType = CompositeType.General;
/*      */       } }
/*  974 */     if ((this.compositeState != i) || (this.imageComp != localCompositeType))
/*      */     {
/*      */ 
/*  977 */       this.compositeState = i;
/*  978 */       this.imageComp = localCompositeType;
/*  979 */       invalidatePipe();
/*  980 */       this.validFontInfo = false;
/*      */     }
/*  982 */     this.composite = paramComposite;
/*  983 */     if (this.paintState <= 1) {
/*  984 */       validateColor();
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
/*      */   public void setPaint(Paint paramPaint)
/*      */   {
/*  997 */     if ((paramPaint instanceof Color)) {
/*  998 */       setColor((Color)paramPaint);
/*  999 */       return;
/*      */     }
/* 1001 */     if ((paramPaint == null) || (this.paint == paramPaint)) {
/* 1002 */       return;
/*      */     }
/* 1004 */     this.paint = paramPaint;
/* 1005 */     if (this.imageComp == CompositeType.SrcOverNoEa)
/*      */     {
/* 1007 */       if (paramPaint.getTransparency() == 1) {
/* 1008 */         if (this.compositeState != 0) {
/* 1009 */           this.compositeState = 0;
/*      */         }
/*      */       }
/* 1012 */       else if (this.compositeState == 0) {
/* 1013 */         this.compositeState = 1;
/*      */       }
/*      */     }
/*      */     
/* 1017 */     Class localClass = paramPaint.getClass();
/* 1018 */     if (localClass == GradientPaint.class) {
/* 1019 */       this.paintState = 2;
/* 1020 */     } else if (localClass == LinearGradientPaint.class) {
/* 1021 */       this.paintState = 3;
/* 1022 */     } else if (localClass == RadialGradientPaint.class) {
/* 1023 */       this.paintState = 4;
/* 1024 */     } else if (localClass == TexturePaint.class) {
/* 1025 */       this.paintState = 5;
/*      */     } else {
/* 1027 */       this.paintState = 6;
/*      */     }
/* 1029 */     this.validFontInfo = false;
/* 1030 */     invalidatePipe();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1037 */   public static final double MinPenSizeAA = RenderingEngine.getInstance().getMinimumAAPenSize();
/* 1038 */   public static final double MinPenSizeAASquared = MinPenSizeAA * MinPenSizeAA;
/*      */   
/*      */   public static final double MinPenSizeSquared = 1.000000001D;
/*      */   static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;
/*      */   Blit lastCAblit;
/*      */   Composite lastCAcomp;
/*      */   private FontRenderContext cachedFRC;
/*      */   
/*      */   private void validateBasicStroke(BasicStroke paramBasicStroke)
/*      */   {
/* 1048 */     int i = this.antialiasHint == 2 ? 1 : 0;
/* 1049 */     if (this.transformState < 3) {
/* 1050 */       if (i != 0) {
/* 1051 */         if (paramBasicStroke.getLineWidth() <= MinPenSizeAA) {
/* 1052 */           if (paramBasicStroke.getDashArray() == null) {
/* 1053 */             this.strokeState = 0;
/*      */           } else {
/* 1055 */             this.strokeState = 1;
/*      */           }
/*      */         } else {
/* 1058 */           this.strokeState = 2;
/*      */         }
/*      */       }
/* 1061 */       else if (paramBasicStroke == defaultStroke) {
/* 1062 */         this.strokeState = 0;
/* 1063 */       } else if (paramBasicStroke.getLineWidth() <= 1.0F) {
/* 1064 */         if (paramBasicStroke.getDashArray() == null) {
/* 1065 */           this.strokeState = 0;
/*      */         } else {
/* 1067 */           this.strokeState = 1;
/*      */         }
/*      */       } else {
/* 1070 */         this.strokeState = 2;
/*      */       }
/*      */     }
/*      */     else {
/*      */       double d1;
/* 1075 */       if ((this.transform.getType() & 0x24) == 0)
/*      */       {
/* 1077 */         d1 = Math.abs(this.transform.getDeterminant());
/*      */       }
/*      */       else {
/* 1080 */         double d2 = this.transform.getScaleX();
/* 1081 */         double d3 = this.transform.getShearX();
/* 1082 */         double d4 = this.transform.getShearY();
/* 1083 */         double d5 = this.transform.getScaleY();
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
/* 1099 */         double d6 = d2 * d2 + d4 * d4;
/* 1100 */         double d7 = 2.0D * (d2 * d3 + d4 * d5);
/* 1101 */         double d8 = d3 * d3 + d5 * d5;
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
/* 1125 */         double d9 = Math.sqrt(d7 * d7 + (d6 - d8) * (d6 - d8));
/*      */         
/*      */ 
/* 1128 */         d1 = (d6 + d8 + d9) / 2.0D;
/*      */       }
/* 1130 */       if (paramBasicStroke != defaultStroke) {
/* 1131 */         d1 *= paramBasicStroke.getLineWidth() * paramBasicStroke.getLineWidth();
/*      */       }
/* 1133 */       if (d1 <= (i != 0 ? MinPenSizeAASquared : 1.000000001D))
/*      */       {
/*      */ 
/* 1136 */         if (paramBasicStroke.getDashArray() == null) {
/* 1137 */           this.strokeState = 0;
/*      */         } else {
/* 1139 */           this.strokeState = 1;
/*      */         }
/*      */       } else {
/* 1142 */         this.strokeState = 2;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setStroke(Stroke paramStroke)
/*      */   {
/* 1154 */     if (paramStroke == null) {
/* 1155 */       throw new IllegalArgumentException("null Stroke");
/*      */     }
/* 1157 */     int i = this.strokeState;
/* 1158 */     this.stroke = paramStroke;
/* 1159 */     if ((paramStroke instanceof BasicStroke)) {
/* 1160 */       validateBasicStroke((BasicStroke)paramStroke);
/*      */     } else {
/* 1162 */       this.strokeState = 3;
/*      */     }
/* 1164 */     if (this.strokeState != i) {
/* 1165 */       invalidatePipe();
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
/*      */   public void setRenderingHint(RenderingHints.Key paramKey, Object paramObject)
/*      */   {
/* 1186 */     if (!paramKey.isCompatibleValue(paramObject)) {
/* 1187 */       throw new IllegalArgumentException(paramObject + " is not compatible with " + paramKey);
/*      */     }
/*      */     
/* 1190 */     if ((paramKey instanceof SunHints.Key))
/*      */     {
/* 1192 */       int j = 0;
/* 1193 */       int k = 1;
/* 1194 */       SunHints.Key localKey = (SunHints.Key)paramKey;
/*      */       int m;
/* 1196 */       if (localKey == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) {
/* 1197 */         m = ((Integer)paramObject).intValue();
/*      */       } else
/* 1199 */         m = ((Value)paramObject).getIndex();
/*      */       int i;
/* 1201 */       switch (localKey.getIndex()) {
/*      */       case 0: 
/* 1203 */         i = this.renderHint != m ? 1 : 0;
/* 1204 */         if (i != 0) {
/* 1205 */           this.renderHint = m;
/* 1206 */           if (this.interpolationHint == -1) {
/* 1207 */             this.interpolationType = (m == 2 ? 2 : 1);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         break;
/*      */       case 1: 
/* 1215 */         i = this.antialiasHint != m ? 1 : 0;
/* 1216 */         this.antialiasHint = m;
/* 1217 */         if (i != 0) {
/* 1218 */           j = this.textAntialiasHint == 0 ? 1 : 0;
/*      */           
/*      */ 
/* 1221 */           if (this.strokeState != 3) {
/* 1222 */             validateBasicStroke((BasicStroke)this.stroke);
/*      */           }
/*      */         }
/*      */         break;
/*      */       case 2: 
/* 1227 */         i = this.textAntialiasHint != m ? 1 : 0;
/* 1228 */         j = i;
/* 1229 */         this.textAntialiasHint = m;
/* 1230 */         break;
/*      */       case 3: 
/* 1232 */         i = this.fractionalMetricsHint != m ? 1 : 0;
/* 1233 */         j = i;
/* 1234 */         this.fractionalMetricsHint = m;
/* 1235 */         break;
/*      */       case 100: 
/* 1237 */         i = 0;
/*      */         
/* 1239 */         this.lcdTextContrast = m;
/* 1240 */         break;
/*      */       case 5: 
/* 1242 */         this.interpolationHint = m;
/* 1243 */         switch (m) {
/*      */         case 2: 
/* 1245 */           m = 3;
/* 1246 */           break;
/*      */         case 1: 
/* 1248 */           m = 2;
/* 1249 */           break;
/*      */         case 0: 
/*      */         default: 
/* 1252 */           m = 1;
/*      */         }
/*      */         
/* 1255 */         i = this.interpolationType != m ? 1 : 0;
/* 1256 */         this.interpolationType = m;
/* 1257 */         break;
/*      */       case 8: 
/* 1259 */         i = this.strokeHint != m ? 1 : 0;
/* 1260 */         this.strokeHint = m;
/* 1261 */         break;
/*      */       case 9: 
/* 1263 */         i = this.resolutionVariantHint != m ? 1 : 0;
/* 1264 */         this.resolutionVariantHint = m;
/* 1265 */         break;
/*      */       default: 
/* 1267 */         k = 0;
/* 1268 */         i = 0;
/*      */       }
/*      */       
/* 1271 */       if (k != 0) {
/* 1272 */         if (i != 0) {
/* 1273 */           invalidatePipe();
/* 1274 */           if (j != 0) {
/* 1275 */             this.fontMetrics = null;
/* 1276 */             this.cachedFRC = null;
/* 1277 */             this.validFontInfo = false;
/* 1278 */             this.glyphVectorFontInfo = null;
/*      */           }
/*      */         }
/* 1281 */         if (this.hints != null) {
/* 1282 */           this.hints.put(paramKey, paramObject);
/*      */         }
/* 1284 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1288 */     if (this.hints == null) {
/* 1289 */       this.hints = makeHints(null);
/*      */     }
/* 1291 */     this.hints.put(paramKey, paramObject);
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
/*      */   public Object getRenderingHint(RenderingHints.Key paramKey)
/*      */   {
/* 1304 */     if (this.hints != null) {
/* 1305 */       return this.hints.get(paramKey);
/*      */     }
/* 1307 */     if (!(paramKey instanceof SunHints.Key)) {
/* 1308 */       return null;
/*      */     }
/* 1310 */     int i = ((SunHints.Key)paramKey).getIndex();
/* 1311 */     switch (i) {
/*      */     case 0: 
/* 1313 */       return Value.get(0, this.renderHint);
/*      */     
/*      */     case 1: 
/* 1316 */       return Value.get(1, this.antialiasHint);
/*      */     
/*      */     case 2: 
/* 1319 */       return Value.get(2, this.textAntialiasHint);
/*      */     
/*      */     case 3: 
/* 1322 */       return Value.get(3, this.fractionalMetricsHint);
/*      */     
/*      */     case 100: 
/* 1325 */       return new Integer(this.lcdTextContrast);
/*      */     case 5: 
/* 1327 */       switch (this.interpolationHint) {
/*      */       case 0: 
/* 1329 */         return SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
/*      */       case 1: 
/* 1331 */         return SunHints.VALUE_INTERPOLATION_BILINEAR;
/*      */       case 2: 
/* 1333 */         return SunHints.VALUE_INTERPOLATION_BICUBIC;
/*      */       }
/* 1335 */       return null;
/*      */     case 8: 
/* 1337 */       return Value.get(8, this.strokeHint);
/*      */     
/*      */     case 9: 
/* 1340 */       return Value.get(9, this.resolutionVariantHint);
/*      */     }
/*      */     
/* 1343 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRenderingHints(Map<?, ?> paramMap)
/*      */   {
/* 1354 */     this.hints = null;
/* 1355 */     this.renderHint = 0;
/* 1356 */     this.antialiasHint = 1;
/* 1357 */     this.textAntialiasHint = 0;
/* 1358 */     this.fractionalMetricsHint = 1;
/* 1359 */     this.lcdTextContrast = lcdTextContrastDefaultValue;
/* 1360 */     this.interpolationHint = -1;
/* 1361 */     this.interpolationType = 1;
/* 1362 */     int i = 0;
/* 1363 */     Iterator localIterator = paramMap.keySet().iterator();
/* 1364 */     while (localIterator.hasNext()) {
/* 1365 */       Object localObject = localIterator.next();
/* 1366 */       if ((localObject == SunHints.KEY_RENDERING) || (localObject == SunHints.KEY_ANTIALIASING) || (localObject == SunHints.KEY_TEXT_ANTIALIASING) || (localObject == SunHints.KEY_FRACTIONALMETRICS) || (localObject == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) || (localObject == SunHints.KEY_STROKE_CONTROL) || (localObject == SunHints.KEY_INTERPOLATION))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1374 */         setRenderingHint((RenderingHints.Key)localObject, paramMap.get(localObject));
/*      */       } else {
/* 1376 */         i = 1;
/*      */       }
/*      */     }
/* 1379 */     if (i != 0) {
/* 1380 */       this.hints = makeHints(paramMap);
/*      */     }
/* 1382 */     invalidatePipe();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addRenderingHints(Map<?, ?> paramMap)
/*      */   {
/* 1393 */     int i = 0;
/* 1394 */     Iterator localIterator = paramMap.keySet().iterator();
/* 1395 */     while (localIterator.hasNext()) {
/* 1396 */       Object localObject = localIterator.next();
/* 1397 */       if ((localObject == SunHints.KEY_RENDERING) || (localObject == SunHints.KEY_ANTIALIASING) || (localObject == SunHints.KEY_TEXT_ANTIALIASING) || (localObject == SunHints.KEY_FRACTIONALMETRICS) || (localObject == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) || (localObject == SunHints.KEY_STROKE_CONTROL) || (localObject == SunHints.KEY_INTERPOLATION))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1405 */         setRenderingHint((RenderingHints.Key)localObject, paramMap.get(localObject));
/*      */       } else {
/* 1407 */         i = 1;
/*      */       }
/*      */     }
/* 1410 */     if (i != 0) {
/* 1411 */       if (this.hints == null) {
/* 1412 */         this.hints = makeHints(paramMap);
/*      */       } else {
/* 1414 */         this.hints.putAll(paramMap);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public RenderingHints getRenderingHints()
/*      */   {
/* 1426 */     if (this.hints == null) {
/* 1427 */       return makeHints(null);
/*      */     }
/* 1429 */     return (RenderingHints)this.hints.clone();
/*      */   }
/*      */   
/*      */   RenderingHints makeHints(Map paramMap)
/*      */   {
/* 1434 */     RenderingHints localRenderingHints = new RenderingHints(paramMap);
/* 1435 */     localRenderingHints.put(SunHints.KEY_RENDERING, 
/* 1436 */       Value.get(0, this.renderHint));
/*      */     
/* 1438 */     localRenderingHints.put(SunHints.KEY_ANTIALIASING, 
/* 1439 */       Value.get(1, this.antialiasHint));
/*      */     
/* 1441 */     localRenderingHints.put(SunHints.KEY_TEXT_ANTIALIASING, 
/* 1442 */       Value.get(2, this.textAntialiasHint));
/*      */     
/* 1444 */     localRenderingHints.put(SunHints.KEY_FRACTIONALMETRICS, 
/* 1445 */       Value.get(3, this.fractionalMetricsHint));
/*      */     
/* 1447 */     localRenderingHints.put(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, 
/* 1448 */       Integer.valueOf(this.lcdTextContrast));
/*      */     Object localObject;
/* 1450 */     switch (this.interpolationHint) {
/*      */     case 0: 
/* 1452 */       localObject = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
/* 1453 */       break;
/*      */     case 1: 
/* 1455 */       localObject = SunHints.VALUE_INTERPOLATION_BILINEAR;
/* 1456 */       break;
/*      */     case 2: 
/* 1458 */       localObject = SunHints.VALUE_INTERPOLATION_BICUBIC;
/* 1459 */       break;
/*      */     default: 
/* 1461 */       localObject = null;
/*      */     }
/*      */     
/* 1464 */     if (localObject != null) {
/* 1465 */       localRenderingHints.put(SunHints.KEY_INTERPOLATION, localObject);
/*      */     }
/* 1467 */     localRenderingHints.put(SunHints.KEY_STROKE_CONTROL, 
/* 1468 */       Value.get(8, this.strokeHint));
/*      */     
/* 1470 */     return localRenderingHints;
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
/*      */   public void translate(double paramDouble1, double paramDouble2)
/*      */   {
/* 1485 */     this.transform.translate(paramDouble1, paramDouble2);
/* 1486 */     invalidateTransform();
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
/*      */   public void rotate(double paramDouble)
/*      */   {
/* 1504 */     this.transform.rotate(paramDouble);
/* 1505 */     invalidateTransform();
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
/*      */   public void rotate(double paramDouble1, double paramDouble2, double paramDouble3)
/*      */   {
/* 1524 */     this.transform.rotate(paramDouble1, paramDouble2, paramDouble3);
/* 1525 */     invalidateTransform();
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
/*      */   public void scale(double paramDouble1, double paramDouble2)
/*      */   {
/* 1540 */     this.transform.scale(paramDouble1, paramDouble2);
/* 1541 */     invalidateTransform();
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
/*      */   public void shear(double paramDouble1, double paramDouble2)
/*      */   {
/* 1560 */     this.transform.shear(paramDouble1, paramDouble2);
/* 1561 */     invalidateTransform();
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
/*      */   public void transform(AffineTransform paramAffineTransform)
/*      */   {
/* 1582 */     this.transform.concatenate(paramAffineTransform);
/* 1583 */     invalidateTransform();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void translate(int paramInt1, int paramInt2)
/*      */   {
/* 1590 */     this.transform.translate(paramInt1, paramInt2);
/* 1591 */     if (this.transformState <= 1) {
/* 1592 */       this.transX += paramInt1;
/* 1593 */       this.transY += paramInt2;
/* 1594 */       this.transformState = ((this.transX | this.transY) == 0 ? 0 : 1);
/*      */     }
/*      */     else {
/* 1597 */       invalidateTransform();
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
/*      */   public void setTransform(AffineTransform paramAffineTransform)
/*      */   {
/* 1610 */     if (((this.constrainX | this.constrainY) == 0) && (this.devScale == 1)) {
/* 1611 */       this.transform.setTransform(paramAffineTransform);
/*      */     } else {
/* 1613 */       this.transform.setTransform(this.devScale, 0.0D, 0.0D, this.devScale, this.constrainX, this.constrainY);
/*      */       
/* 1615 */       this.transform.concatenate(paramAffineTransform);
/*      */     }
/* 1617 */     invalidateTransform();
/*      */   }
/*      */   
/*      */   protected void invalidateTransform() {
/* 1621 */     int i = this.transform.getType();
/* 1622 */     int j = this.transformState;
/* 1623 */     if (i == 0) {
/* 1624 */       this.transformState = 0;
/* 1625 */       this.transX = (this.transY = 0);
/* 1626 */     } else if (i == 1) {
/* 1627 */       double d1 = this.transform.getTranslateX();
/* 1628 */       double d2 = this.transform.getTranslateY();
/* 1629 */       this.transX = ((int)Math.floor(d1 + 0.5D));
/* 1630 */       this.transY = ((int)Math.floor(d2 + 0.5D));
/* 1631 */       if ((d1 == this.transX) && (d2 == this.transY)) {
/* 1632 */         this.transformState = 1;
/*      */       } else {
/* 1634 */         this.transformState = 2;
/*      */       }
/* 1636 */     } else if ((i & 0x78) == 0)
/*      */     {
/*      */ 
/*      */ 
/* 1640 */       this.transformState = 3;
/* 1641 */       this.transX = (this.transY = 0);
/*      */     } else {
/* 1643 */       this.transformState = 4;
/* 1644 */       this.transX = (this.transY = 0);
/*      */     }
/*      */     
/* 1647 */     if ((this.transformState >= 3) || (j >= 3))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1653 */       this.cachedFRC = null;
/* 1654 */       this.validFontInfo = false;
/* 1655 */       this.fontMetrics = null;
/* 1656 */       this.glyphVectorFontInfo = null;
/*      */       
/* 1658 */       if (this.transformState != j) {
/* 1659 */         invalidatePipe();
/*      */       }
/*      */     }
/* 1662 */     if (this.strokeState != 3) {
/* 1663 */       validateBasicStroke((BasicStroke)this.stroke);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public AffineTransform getTransform()
/*      */   {
/* 1674 */     if (((this.constrainX | this.constrainY) == 0) && (this.devScale == 1)) {
/* 1675 */       return new AffineTransform(this.transform);
/*      */     }
/* 1677 */     double d = 1.0D / this.devScale;
/* 1678 */     AffineTransform localAffineTransform = new AffineTransform(d, 0.0D, 0.0D, d, -this.constrainX * d, -this.constrainY * d);
/*      */     
/*      */ 
/* 1681 */     localAffineTransform.concatenate(this.transform);
/* 1682 */     return localAffineTransform;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public AffineTransform cloneTransform()
/*      */   {
/* 1690 */     return new AffineTransform(this.transform);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Paint getPaint()
/*      */   {
/* 1699 */     return this.paint;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Composite getComposite()
/*      */   {
/* 1707 */     return this.composite;
/*      */   }
/*      */   
/*      */   public Color getColor() {
/* 1711 */     return this.foregroundColor;
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
/*      */   final void validateColor()
/*      */   {
/*      */     int i;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1736 */     if (this.imageComp == CompositeType.Clear) {
/* 1737 */       i = 0;
/*      */     } else {
/* 1739 */       i = this.foregroundColor.getRGB();
/* 1740 */       if ((this.compositeState <= 1) && (this.imageComp != CompositeType.SrcNoEa) && (this.imageComp != CompositeType.SrcOverNoEa))
/*      */       {
/*      */ 
/*      */ 
/* 1744 */         AlphaComposite localAlphaComposite = (AlphaComposite)this.composite;
/* 1745 */         int j = Math.round(localAlphaComposite.getAlpha() * (i >>> 24));
/* 1746 */         i = i & 0xFFFFFF | j << 24;
/*      */       }
/*      */     }
/* 1749 */     this.eargb = i;
/* 1750 */     this.pixel = this.surfaceData.pixelFor(i);
/*      */   }
/*      */   
/*      */   public void setColor(Color paramColor) {
/* 1754 */     if ((paramColor == null) || (paramColor == this.paint)) {
/* 1755 */       return;
/*      */     }
/* 1757 */     this.paint = (this.foregroundColor = paramColor);
/* 1758 */     validateColor();
/* 1759 */     if (this.eargb >> 24 == -1) {
/* 1760 */       if (this.paintState == 0) {
/* 1761 */         return;
/*      */       }
/* 1763 */       this.paintState = 0;
/* 1764 */       if (this.imageComp == CompositeType.SrcOverNoEa)
/*      */       {
/* 1766 */         this.compositeState = 0;
/*      */       }
/*      */     } else {
/* 1769 */       if (this.paintState == 1) {
/* 1770 */         return;
/*      */       }
/* 1772 */       this.paintState = 1;
/* 1773 */       if (this.imageComp == CompositeType.SrcOverNoEa)
/*      */       {
/* 1775 */         this.compositeState = 1;
/*      */       }
/*      */     }
/* 1778 */     this.validFontInfo = false;
/* 1779 */     invalidatePipe();
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
/*      */   public void setBackground(Color paramColor)
/*      */   {
/* 1795 */     this.backgroundColor = paramColor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Color getBackground()
/*      */   {
/* 1803 */     return this.backgroundColor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Stroke getStroke()
/*      */   {
/* 1811 */     return this.stroke;
/*      */   }
/*      */   
/*      */   public Rectangle getClipBounds() {
/* 1815 */     if (this.clipState == 0) {
/* 1816 */       return null;
/*      */     }
/* 1818 */     return getClipBounds(new Rectangle());
/*      */   }
/*      */   
/*      */   public Rectangle getClipBounds(Rectangle paramRectangle) {
/* 1822 */     if (this.clipState != 0) {
/* 1823 */       if (this.transformState <= 1) {
/* 1824 */         if ((this.usrClip instanceof Rectangle)) {
/* 1825 */           paramRectangle.setBounds((Rectangle)this.usrClip);
/*      */         } else {
/* 1827 */           paramRectangle.setFrame(this.usrClip.getBounds2D());
/*      */         }
/* 1829 */         paramRectangle.translate(-this.transX, -this.transY);
/*      */       } else {
/* 1831 */         paramRectangle.setFrame(getClip().getBounds2D());
/*      */       }
/* 1833 */     } else if (paramRectangle == null) {
/* 1834 */       throw new NullPointerException("null rectangle parameter");
/*      */     }
/* 1836 */     return paramRectangle;
/*      */   }
/*      */   
/*      */   public boolean hitClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 1840 */     if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
/* 1841 */       return false;
/*      */     }
/* 1843 */     if (this.transformState > 1)
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
/*      */ 
/* 1861 */       double[] arrayOfDouble = { paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2, paramInt1, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4 };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1867 */       this.transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
/* 1868 */       paramInt1 = (int)Math.floor(Math.min(Math.min(arrayOfDouble[0], arrayOfDouble[2]), 
/* 1869 */         Math.min(arrayOfDouble[4], arrayOfDouble[6])));
/* 1870 */       paramInt2 = (int)Math.floor(Math.min(Math.min(arrayOfDouble[1], arrayOfDouble[3]), 
/* 1871 */         Math.min(arrayOfDouble[5], arrayOfDouble[7])));
/* 1872 */       paramInt3 = (int)Math.ceil(Math.max(Math.max(arrayOfDouble[0], arrayOfDouble[2]), 
/* 1873 */         Math.max(arrayOfDouble[4], arrayOfDouble[6])));
/* 1874 */       paramInt4 = (int)Math.ceil(Math.max(Math.max(arrayOfDouble[1], arrayOfDouble[3]), 
/* 1875 */         Math.max(arrayOfDouble[5], arrayOfDouble[7])));
/*      */     } else {
/* 1877 */       paramInt1 += this.transX;
/* 1878 */       paramInt2 += this.transY;
/* 1879 */       paramInt3 += paramInt1;
/* 1880 */       paramInt4 += paramInt2;
/*      */     }
/*      */     try
/*      */     {
/* 1884 */       if (!getCompClip().intersectsQuickCheckXYXY(paramInt1, paramInt2, paramInt3, paramInt4)) {
/* 1885 */         return false;
/*      */       }
/*      */     } catch (InvalidPipeException localInvalidPipeException) {
/* 1888 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1895 */     return true;
/*      */   }
/*      */   
/*      */   protected void validateCompClip() {
/* 1899 */     int i = this.clipState;
/* 1900 */     if (this.usrClip == null) {
/* 1901 */       this.clipState = 0;
/* 1902 */       this.clipRegion = this.devClip;
/* 1903 */     } else if ((this.usrClip instanceof Rectangle2D)) {
/* 1904 */       this.clipState = 1;
/* 1905 */       if ((this.usrClip instanceof Rectangle)) {
/* 1906 */         this.clipRegion = this.devClip.getIntersection((Rectangle)this.usrClip);
/*      */       } else {
/* 1908 */         this.clipRegion = this.devClip.getIntersection(this.usrClip.getBounds());
/*      */       }
/*      */     } else {
/* 1911 */       PathIterator localPathIterator = this.usrClip.getPathIterator(null);
/* 1912 */       int[] arrayOfInt = new int[4];
/* 1913 */       ShapeSpanIterator localShapeSpanIterator = LoopPipe.getFillSSI(this);
/*      */       try {
/* 1915 */         localShapeSpanIterator.setOutputArea(this.devClip);
/* 1916 */         localShapeSpanIterator.appendPath(localPathIterator);
/* 1917 */         localShapeSpanIterator.getPathBox(arrayOfInt);
/* 1918 */         Region localRegion = Region.getInstance(arrayOfInt);
/* 1919 */         localRegion.appendSpans(localShapeSpanIterator);
/* 1920 */         this.clipRegion = localRegion;
/*      */         
/* 1922 */         this.clipState = (localRegion.isRectangular() ? 1 : 2);
/*      */       } finally {
/* 1924 */         localShapeSpanIterator.dispose();
/*      */       }
/*      */     }
/* 1927 */     if ((i != this.clipState) && ((this.clipState == 2) || (i == 2)))
/*      */     {
/*      */ 
/* 1930 */       this.validFontInfo = false;
/* 1931 */       invalidatePipe();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Shape transformShape(Shape paramShape)
/*      */   {
/* 1940 */     if (paramShape == null) {
/* 1941 */       return null;
/*      */     }
/* 1943 */     if (this.transformState > 1) {
/* 1944 */       return transformShape(this.transform, paramShape);
/*      */     }
/* 1946 */     return transformShape(this.transX, this.transY, paramShape);
/*      */   }
/*      */   
/*      */   public Shape untransformShape(Shape paramShape)
/*      */   {
/* 1951 */     if (paramShape == null) {
/* 1952 */       return null;
/*      */     }
/* 1954 */     if (this.transformState > 1) {
/*      */       try {
/* 1956 */         return transformShape(this.transform.createInverse(), paramShape);
/*      */       } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 1958 */         return null;
/*      */       }
/*      */     }
/* 1961 */     return transformShape(-this.transX, -this.transY, paramShape);
/*      */   }
/*      */   
/*      */   protected static Shape transformShape(int paramInt1, int paramInt2, Shape paramShape)
/*      */   {
/* 1966 */     if (paramShape == null) {
/* 1967 */       return null;
/*      */     }
/*      */     
/* 1970 */     if ((paramShape instanceof Rectangle)) {
/* 1971 */       localObject = paramShape.getBounds();
/* 1972 */       ((Rectangle)localObject).translate(paramInt1, paramInt2);
/* 1973 */       return (Shape)localObject;
/*      */     }
/* 1975 */     if ((paramShape instanceof Rectangle2D)) {
/* 1976 */       localObject = (Rectangle2D)paramShape;
/* 1977 */       return new Double(((Rectangle2D)localObject).getX() + paramInt1, ((Rectangle2D)localObject)
/* 1978 */         .getY() + paramInt2, ((Rectangle2D)localObject)
/* 1979 */         .getWidth(), ((Rectangle2D)localObject)
/* 1980 */         .getHeight());
/*      */     }
/*      */     
/* 1983 */     if ((paramInt1 == 0) && (paramInt2 == 0)) {
/* 1984 */       return cloneShape(paramShape);
/*      */     }
/*      */     
/* 1987 */     Object localObject = AffineTransform.getTranslateInstance(paramInt1, paramInt2);
/* 1988 */     return ((AffineTransform)localObject).createTransformedShape(paramShape);
/*      */   }
/*      */   
/*      */   protected static Shape transformShape(AffineTransform paramAffineTransform, Shape paramShape) {
/* 1992 */     if (paramShape == null) {
/* 1993 */       return null;
/*      */     }
/*      */     
/* 1996 */     if (((paramShape instanceof Rectangle2D)) && 
/* 1997 */       ((paramAffineTransform.getType() & 0x30) == 0))
/*      */     {
/* 1999 */       Rectangle2D localRectangle2D = (Rectangle2D)paramShape;
/* 2000 */       double[] arrayOfDouble = new double[4];
/* 2001 */       arrayOfDouble[0] = localRectangle2D.getX();
/* 2002 */       arrayOfDouble[1] = localRectangle2D.getY();
/* 2003 */       arrayOfDouble[2] = (arrayOfDouble[0] + localRectangle2D.getWidth());
/* 2004 */       arrayOfDouble[3] = (arrayOfDouble[1] + localRectangle2D.getHeight());
/* 2005 */       paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 2);
/* 2006 */       fixRectangleOrientation(arrayOfDouble, localRectangle2D);
/* 2007 */       return new Double(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2] - arrayOfDouble[0], arrayOfDouble[3] - arrayOfDouble[1]);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2012 */     if (paramAffineTransform.isIdentity()) {
/* 2013 */       return cloneShape(paramShape);
/*      */     }
/*      */     
/* 2016 */     return paramAffineTransform.createTransformedShape(paramShape);
/*      */   }
/*      */   
/*      */ 
/*      */   private static void fixRectangleOrientation(double[] paramArrayOfDouble, Rectangle2D paramRectangle2D)
/*      */   {
/*      */     double d;
/* 2023 */     if ((paramRectangle2D.getWidth() > 0.0D ? 1 : 0) != (paramArrayOfDouble[2] - paramArrayOfDouble[0] > 0.0D ? 1 : 0)) {
/* 2024 */       d = paramArrayOfDouble[0];
/* 2025 */       paramArrayOfDouble[0] = paramArrayOfDouble[2];
/* 2026 */       paramArrayOfDouble[2] = d;
/*      */     }
/* 2028 */     if ((paramRectangle2D.getHeight() > 0.0D ? 1 : 0) != (paramArrayOfDouble[3] - paramArrayOfDouble[1] > 0.0D ? 1 : 0)) {
/* 2029 */       d = paramArrayOfDouble[1];
/* 2030 */       paramArrayOfDouble[1] = paramArrayOfDouble[3];
/* 2031 */       paramArrayOfDouble[3] = d;
/*      */     }
/*      */   }
/*      */   
/*      */   public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 2036 */     clip(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
/*      */   }
/*      */   
/*      */   public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 2040 */     setClip(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
/*      */   }
/*      */   
/*      */   public Shape getClip() {
/* 2044 */     return untransformShape(this.usrClip);
/*      */   }
/*      */   
/*      */   public void setClip(Shape paramShape) {
/* 2048 */     this.usrClip = transformShape(paramShape);
/* 2049 */     validateCompClip();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clip(Shape paramShape)
/*      */   {
/* 2061 */     paramShape = transformShape(paramShape);
/* 2062 */     if (this.usrClip != null) {
/* 2063 */       paramShape = intersectShapes(this.usrClip, paramShape, true, true);
/*      */     }
/* 2065 */     this.usrClip = paramShape;
/* 2066 */     validateCompClip();
/*      */   }
/*      */   
/*      */   public void setPaintMode() {
/* 2070 */     setComposite(AlphaComposite.SrcOver);
/*      */   }
/*      */   
/*      */   public void setXORMode(Color paramColor) {
/* 2074 */     if (paramColor == null) {
/* 2075 */       throw new IllegalArgumentException("null XORColor");
/*      */     }
/* 2077 */     setComposite(new XORComposite(paramColor, this.surfaceData));
/*      */   }
/*      */   
/*      */ 
/*      */   public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*      */     try
/*      */     {
/* 2085 */       doCopyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2088 */         revalidateAll();
/* 2089 */         doCopyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2096 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   private void doCopyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/* 2101 */     if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
/* 2102 */       return;
/*      */     }
/* 2104 */     SurfaceData localSurfaceData = this.surfaceData;
/* 2105 */     if (localSurfaceData.copyArea(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)) {
/* 2106 */       return;
/*      */     }
/* 2108 */     if (this.transformState > 3) {
/* 2109 */       throw new InternalError("transformed copyArea not implemented yet");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2114 */     Region localRegion = getCompClip();
/*      */     
/* 2116 */     Composite localComposite = this.composite;
/* 2117 */     if (this.lastCAcomp != localComposite) {
/* 2118 */       localObject1 = localSurfaceData.getSurfaceType();
/* 2119 */       localObject2 = this.imageComp;
/* 2120 */       if ((CompositeType.SrcOverNoEa.equals(localObject2)) && 
/* 2121 */         (localSurfaceData.getTransparency() == 1))
/*      */       {
/* 2123 */         localObject2 = CompositeType.SrcNoEa;
/*      */       }
/* 2125 */       this.lastCAblit = Blit.locate((SurfaceType)localObject1, (CompositeType)localObject2, (SurfaceType)localObject1);
/* 2126 */       this.lastCAcomp = localComposite;
/*      */     }
/*      */     
/* 2129 */     Object localObject1 = { paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramInt1 + paramInt5, paramInt2 + paramInt6 };
/* 2130 */     this.transform.transform((double[])localObject1, 0, (double[])localObject1, 0, 3);
/*      */     
/* 2132 */     paramInt1 = (int)Math.ceil(localObject1[0] - 0.5D);
/* 2133 */     paramInt2 = (int)Math.ceil(localObject1[1] - 0.5D);
/* 2134 */     paramInt3 = (int)Math.ceil(localObject1[2] - 0.5D) - paramInt1;
/* 2135 */     paramInt4 = (int)Math.ceil(localObject1[3] - 0.5D) - paramInt2;
/* 2136 */     paramInt5 = (int)Math.ceil(localObject1[4] - 0.5D) - paramInt1;
/* 2137 */     paramInt6 = (int)Math.ceil(localObject1[5] - 0.5D) - paramInt2;
/*      */     
/*      */ 
/* 2140 */     if (paramInt3 < 0) {
/* 2141 */       paramInt3 *= -1;
/* 2142 */       paramInt1 -= paramInt3;
/*      */     }
/* 2144 */     if (paramInt4 < 0) {
/* 2145 */       paramInt4 *= -1;
/* 2146 */       paramInt2 -= paramInt4;
/*      */     }
/*      */     
/* 2149 */     Object localObject2 = this.lastCAblit;
/* 2150 */     int i; int j; if ((paramInt6 == 0) && (paramInt5 > 0) && (paramInt5 < paramInt3)) {
/* 2151 */       while (paramInt3 > 0) {
/* 2152 */         i = Math.min(paramInt3, paramInt5);
/* 2153 */         paramInt3 -= i;
/* 2154 */         j = paramInt1 + paramInt3;
/* 2155 */         ((Blit)localObject2).Blit(localSurfaceData, localSurfaceData, localComposite, localRegion, j, paramInt2, j + paramInt5, paramInt2 + paramInt6, i, paramInt4);
/*      */       }
/*      */       
/* 2158 */       return;
/*      */     }
/* 2160 */     if ((paramInt6 > 0) && (paramInt6 < paramInt4) && (paramInt5 > -paramInt3) && (paramInt5 < paramInt3)) {
/* 2161 */       while (paramInt4 > 0) {
/* 2162 */         i = Math.min(paramInt4, paramInt6);
/* 2163 */         paramInt4 -= i;
/* 2164 */         j = paramInt2 + paramInt4;
/* 2165 */         ((Blit)localObject2).Blit(localSurfaceData, localSurfaceData, localComposite, localRegion, paramInt1, j, paramInt1 + paramInt5, j + paramInt6, paramInt3, i);
/*      */       }
/*      */       
/* 2168 */       return;
/*      */     }
/* 2170 */     ((Blit)localObject2).Blit(localSurfaceData, localSurfaceData, localComposite, localRegion, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4);
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
/*      */   public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*      */     try
/*      */     {
/* 2234 */       this.drawpipe.drawLine(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2237 */         revalidateAll();
/* 2238 */         this.drawpipe.drawLine(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2245 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/*      */     try {
/* 2251 */       this.drawpipe.drawRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2254 */         revalidateAll();
/* 2255 */         this.drawpipe.drawRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2262 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/*      */     try {
/* 2268 */       this.fillpipe.fillRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2271 */         revalidateAll();
/* 2272 */         this.fillpipe.fillRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2279 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*      */     try {
/* 2285 */       this.drawpipe.drawOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2288 */         revalidateAll();
/* 2289 */         this.drawpipe.drawOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2296 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*      */     try {
/* 2302 */       this.fillpipe.fillOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2305 */         revalidateAll();
/* 2306 */         this.fillpipe.fillOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2313 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*      */     try {
/* 2320 */       this.drawpipe.drawArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2323 */         revalidateAll();
/* 2324 */         this.drawpipe.drawArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2331 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*      */     try {
/* 2338 */       this.fillpipe.fillArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2341 */         revalidateAll();
/* 2342 */         this.fillpipe.fillArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2349 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
/*      */     try {
/* 2355 */       this.drawpipe.drawPolyline(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2358 */         revalidateAll();
/* 2359 */         this.drawpipe.drawPolyline(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2366 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
/*      */     try {
/* 2372 */       this.drawpipe.drawPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2375 */         revalidateAll();
/* 2376 */         this.drawpipe.drawPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2383 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
/*      */     try {
/* 2389 */       this.fillpipe.fillPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2392 */         revalidateAll();
/* 2393 */         this.fillpipe.fillPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2400 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*      */     try {
/* 2406 */       this.drawpipe.drawRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2409 */         revalidateAll();
/* 2410 */         this.drawpipe.drawRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2417 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*      */     try {
/* 2423 */       this.fillpipe.fillRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2426 */         revalidateAll();
/* 2427 */         this.fillpipe.fillRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2434 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void revalidateAll()
/*      */   {
/* 2445 */     this.surfaceData = this.surfaceData.getReplacement();
/* 2446 */     if (this.surfaceData == null) {
/* 2447 */       this.surfaceData = NullSurfaceData.theInstance;
/*      */     }
/*      */     
/* 2450 */     invalidatePipe();
/*      */     
/*      */ 
/* 2453 */     setDevClip(this.surfaceData.getBounds());
/*      */     
/* 2455 */     if (this.paintState <= 1) {
/* 2456 */       validateColor();
/*      */     }
/* 2458 */     if ((this.composite instanceof XORComposite)) {
/* 2459 */       Color localColor = ((XORComposite)this.composite).getXorColor();
/* 2460 */       setComposite(new XORComposite(localColor, this.surfaceData));
/*      */     }
/* 2462 */     validatePipe();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 2472 */     Composite localComposite = this.composite;
/* 2473 */     Paint localPaint = this.paint;
/* 2474 */     setComposite(AlphaComposite.Src);
/* 2475 */     setColor(getBackground());
/* 2476 */     fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
/* 2477 */     setPaint(localPaint);
/* 2478 */     setComposite(localComposite);
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
/*      */   public void draw(Shape paramShape)
/*      */   {
/*      */     try
/*      */     {
/* 2497 */       this.shapepipe.draw(this, paramShape);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2500 */         revalidateAll();
/* 2501 */         this.shapepipe.draw(this, paramShape);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2508 */       this.surfaceData.markDirty();
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
/*      */   public void fill(Shape paramShape)
/*      */   {
/*      */     try
/*      */     {
/* 2527 */       this.shapepipe.fill(this, paramShape);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2530 */         revalidateAll();
/* 2531 */         this.shapepipe.fill(this, paramShape);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2538 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean isIntegerTranslation(AffineTransform paramAffineTransform)
/*      */   {
/* 2547 */     if (paramAffineTransform.isIdentity()) {
/* 2548 */       return true;
/*      */     }
/* 2550 */     if (paramAffineTransform.getType() == 1) {
/* 2551 */       double d1 = paramAffineTransform.getTranslateX();
/* 2552 */       double d2 = paramAffineTransform.getTranslateY();
/* 2553 */       return (d1 == (int)d1) && (d2 == (int)d2);
/*      */     }
/* 2555 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int getTileIndex(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 2563 */     paramInt1 -= paramInt2;
/* 2564 */     if (paramInt1 < 0) {
/* 2565 */       paramInt1 += 1 - paramInt3;
/*      */     }
/* 2567 */     return paramInt1 / paramInt3;
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
/*      */   private static Rectangle getImageRegion(RenderedImage paramRenderedImage, Region paramRegion, AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2, int paramInt1, int paramInt2)
/*      */   {
/* 2584 */     Rectangle localRectangle1 = new Rectangle(paramRenderedImage.getMinX(), paramRenderedImage.getMinY(), paramRenderedImage.getWidth(), paramRenderedImage.getHeight());
/*      */     
/* 2586 */     Rectangle localRectangle2 = null;
/*      */     try {
/* 2588 */       double[] arrayOfDouble = new double[8];
/* 2589 */       arrayOfDouble[0] = (arrayOfDouble[2] = paramRegion.getLoX());
/* 2590 */       arrayOfDouble[4] = (arrayOfDouble[6] = paramRegion.getHiX());
/* 2591 */       arrayOfDouble[1] = (arrayOfDouble[5] = paramRegion.getLoY());
/* 2592 */       arrayOfDouble[3] = (arrayOfDouble[7] = paramRegion.getHiY());
/*      */       
/*      */ 
/* 2595 */       paramAffineTransform1.inverseTransform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
/* 2596 */       paramAffineTransform2.inverseTransform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
/*      */       
/*      */       double d2;
/*      */       
/* 2600 */       double d1 = d2 = arrayOfDouble[0];
/* 2601 */       double d4; double d3 = d4 = arrayOfDouble[1];
/*      */       
/* 2603 */       for (int i = 2; i < 8;) {
/* 2604 */         double d5 = arrayOfDouble[(i++)];
/* 2605 */         if (d5 < d1) {
/* 2606 */           d1 = d5;
/* 2607 */         } else if (d5 > d2) {
/* 2608 */           d2 = d5;
/*      */         }
/* 2610 */         d5 = arrayOfDouble[(i++)];
/* 2611 */         if (d5 < d3) {
/* 2612 */           d3 = d5;
/* 2613 */         } else if (d5 > d4) {
/* 2614 */           d4 = d5;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2620 */       i = (int)d1 - paramInt1;
/* 2621 */       int j = (int)(d2 - d1 + 2 * paramInt1);
/* 2622 */       int k = (int)d3 - paramInt2;
/* 2623 */       int m = (int)(d4 - d3 + 2 * paramInt2);
/*      */       
/* 2625 */       Rectangle localRectangle3 = new Rectangle(i, k, j, m);
/* 2626 */       localRectangle2 = localRectangle3.intersection(localRectangle1);
/*      */     }
/*      */     catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 2629 */       localRectangle2 = localRectangle1;
/*      */     }
/*      */     
/* 2632 */     return localRectangle2;
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
/*      */   public void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform)
/*      */   {
/* 2656 */     if (paramRenderedImage == null) {
/* 2657 */       return;
/*      */     }
/*      */     
/*      */ 
/* 2661 */     if ((paramRenderedImage instanceof BufferedImage)) {
/* 2662 */       BufferedImage localBufferedImage1 = (BufferedImage)paramRenderedImage;
/* 2663 */       drawImage(localBufferedImage1, paramAffineTransform, null);
/* 2664 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2670 */     if (this.transformState <= 1) {}
/*      */     
/* 2672 */     int i = isIntegerTranslation(paramAffineTransform) ? 1 : 0;
/*      */     
/*      */ 
/* 2675 */     int j = i != 0 ? 0 : 3;
/*      */     Region localRegion;
/*      */     try
/*      */     {
/* 2679 */       localRegion = getCompClip();
/*      */     } catch (InvalidPipeException localInvalidPipeException) {
/* 2681 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2686 */     Rectangle localRectangle = getImageRegion(paramRenderedImage, localRegion, this.transform, paramAffineTransform, j, j);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2691 */     if ((localRectangle.width <= 0) || (localRectangle.height <= 0)) {
/* 2692 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2700 */     if (i != 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2707 */       drawTranslatedRenderedImage(paramRenderedImage, localRectangle, 
/* 2708 */         (int)paramAffineTransform.getTranslateX(), 
/* 2709 */         (int)paramAffineTransform.getTranslateY());
/* 2710 */       return;
/*      */     }
/*      */     
/*      */ 
/* 2714 */     Raster localRaster = paramRenderedImage.getData(localRectangle);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2720 */     WritableRaster localWritableRaster = Raster.createWritableRaster(localRaster.getSampleModel(), localRaster
/* 2721 */       .getDataBuffer(), null);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2729 */     int k = localRaster.getMinX();
/* 2730 */     int m = localRaster.getMinY();
/* 2731 */     int n = localRaster.getWidth();
/* 2732 */     int i1 = localRaster.getHeight();
/* 2733 */     int i2 = k - localRaster.getSampleModelTranslateX();
/* 2734 */     int i3 = m - localRaster.getSampleModelTranslateY();
/* 2735 */     if ((i2 != 0) || (i3 != 0) || (n != localWritableRaster.getWidth()) || 
/* 2736 */       (i1 != localWritableRaster.getHeight()))
/*      */     {
/* 2738 */       localWritableRaster = localWritableRaster.createWritableChild(i2, i3, n, i1, 0, 0, null);
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
/* 2750 */     AffineTransform localAffineTransform = (AffineTransform)paramAffineTransform.clone();
/* 2751 */     localAffineTransform.translate(k, m);
/*      */     
/* 2753 */     ColorModel localColorModel = paramRenderedImage.getColorModel();
/*      */     
/*      */ 
/* 2756 */     BufferedImage localBufferedImage2 = new BufferedImage(localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied(), null);
/*      */     
/* 2758 */     drawImage(localBufferedImage2, localAffineTransform, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean clipTo(Rectangle paramRectangle1, Rectangle paramRectangle2)
/*      */   {
/* 2767 */     int i = Math.max(paramRectangle1.x, paramRectangle2.x);
/* 2768 */     int j = Math.min(paramRectangle1.x + paramRectangle1.width, paramRectangle2.x + paramRectangle2.width);
/* 2769 */     int k = Math.max(paramRectangle1.y, paramRectangle2.y);
/* 2770 */     int m = Math.min(paramRectangle1.y + paramRectangle1.height, paramRectangle2.y + paramRectangle2.height);
/* 2771 */     if ((j - i < 0) || (m - k < 0)) {
/* 2772 */       paramRectangle1.width = -1;
/* 2773 */       paramRectangle1.height = -1;
/* 2774 */       return false;
/*      */     }
/* 2776 */     paramRectangle1.x = i;
/* 2777 */     paramRectangle1.y = k;
/* 2778 */     paramRectangle1.width = (j - i);
/* 2779 */     paramRectangle1.height = (m - k);
/* 2780 */     return true;
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
/*      */   private void drawTranslatedRenderedImage(RenderedImage paramRenderedImage, Rectangle paramRectangle, int paramInt1, int paramInt2)
/*      */   {
/* 2794 */     int i = paramRenderedImage.getTileGridXOffset();
/* 2795 */     int j = paramRenderedImage.getTileGridYOffset();
/* 2796 */     int k = paramRenderedImage.getTileWidth();
/* 2797 */     int m = paramRenderedImage.getTileHeight();
/*      */     
/*      */ 
/*      */ 
/* 2801 */     int n = getTileIndex(paramRectangle.x, i, k);
/*      */     
/* 2803 */     int i1 = getTileIndex(paramRectangle.y, j, m);
/*      */     
/* 2805 */     int i2 = getTileIndex(paramRectangle.x + paramRectangle.width - 1, i, k);
/*      */     
/*      */ 
/* 2808 */     int i3 = getTileIndex(paramRectangle.y + paramRectangle.height - 1, j, m);
/*      */     
/*      */ 
/*      */ 
/* 2812 */     ColorModel localColorModel = paramRenderedImage.getColorModel();
/*      */     
/*      */ 
/* 2815 */     Rectangle localRectangle = new Rectangle();
/*      */     
/* 2817 */     for (int i4 = i1; i4 <= i3; i4++) {
/* 2818 */       for (int i5 = n; i5 <= i2; i5++)
/*      */       {
/* 2820 */         Raster localRaster = paramRenderedImage.getTile(i5, i4);
/*      */         
/*      */ 
/* 2823 */         localRectangle.x = (i5 * k + i);
/* 2824 */         localRectangle.y = (i4 * m + j);
/* 2825 */         localRectangle.width = k;
/* 2826 */         localRectangle.height = m;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2831 */         clipTo(localRectangle, paramRectangle);
/*      */         
/*      */ 
/* 2834 */         WritableRaster localWritableRaster = null;
/* 2835 */         if ((localRaster instanceof WritableRaster)) {
/* 2836 */           localWritableRaster = (WritableRaster)localRaster;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 2841 */           localWritableRaster = Raster.createWritableRaster(localRaster.getSampleModel(), localRaster
/* 2842 */             .getDataBuffer(), null);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2848 */         localWritableRaster = localWritableRaster.createWritableChild(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, 0, 0, null);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2858 */         BufferedImage localBufferedImage = new BufferedImage(localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied(), null);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2866 */         copyImage(localBufferedImage, localRectangle.x + paramInt1, localRectangle.y + paramInt2, 0, 0, localRectangle.width, localRectangle.height, null, null);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void drawRenderableImage(RenderableImage paramRenderableImage, AffineTransform paramAffineTransform)
/*      */   {
/* 2876 */     if (paramRenderableImage == null) {
/* 2877 */       return;
/*      */     }
/*      */     
/* 2880 */     AffineTransform localAffineTransform1 = this.transform;
/* 2881 */     AffineTransform localAffineTransform2 = new AffineTransform(paramAffineTransform);
/* 2882 */     localAffineTransform2.concatenate(localAffineTransform1);
/*      */     
/*      */ 
/* 2885 */     RenderContext localRenderContext = new RenderContext(localAffineTransform2);
/*      */     AffineTransform localAffineTransform3;
/*      */     try {
/* 2888 */       localAffineTransform3 = localAffineTransform1.createInverse();
/*      */     } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 2890 */       localRenderContext = new RenderContext(localAffineTransform1);
/* 2891 */       localAffineTransform3 = new AffineTransform();
/*      */     }
/*      */     
/* 2894 */     RenderedImage localRenderedImage = paramRenderableImage.createRendering(localRenderContext);
/* 2895 */     drawRenderedImage(localRenderedImage, localAffineTransform3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Rectangle transformBounds(Rectangle paramRectangle, AffineTransform paramAffineTransform)
/*      */   {
/* 2905 */     if (paramAffineTransform.isIdentity()) {
/* 2906 */       return paramRectangle;
/*      */     }
/*      */     
/* 2909 */     Shape localShape = transformShape(paramAffineTransform, paramRectangle);
/* 2910 */     return localShape.getBounds();
/*      */   }
/*      */   
/*      */   public void drawString(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 2915 */     if (paramString == null) {
/* 2916 */       throw new NullPointerException("String is null");
/*      */     }
/*      */     
/* 2919 */     if (this.font.hasLayoutAttributes()) {
/* 2920 */       if (paramString.length() == 0) {
/* 2921 */         return;
/*      */       }
/* 2923 */       new TextLayout(paramString, this.font, getFontRenderContext()).draw(this, paramInt1, paramInt2);
/* 2924 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2928 */       this.textpipe.drawString(this, paramString, paramInt1, paramInt2);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2931 */         revalidateAll();
/* 2932 */         this.textpipe.drawString(this, paramString, paramInt1, paramInt2);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2939 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawString(String paramString, float paramFloat1, float paramFloat2) {
/* 2944 */     if (paramString == null) {
/* 2945 */       throw new NullPointerException("String is null");
/*      */     }
/*      */     
/* 2948 */     if (this.font.hasLayoutAttributes()) {
/* 2949 */       if (paramString.length() == 0) {
/* 2950 */         return;
/*      */       }
/* 2952 */       new TextLayout(paramString, this.font, getFontRenderContext()).draw(this, paramFloat1, paramFloat2);
/* 2953 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2957 */       this.textpipe.drawString(this, paramString, paramFloat1, paramFloat2);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 2960 */         revalidateAll();
/* 2961 */         this.textpipe.drawString(this, paramString, paramFloat1, paramFloat2);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 2968 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
/*      */   {
/* 2974 */     if (paramAttributedCharacterIterator == null) {
/* 2975 */       throw new NullPointerException("AttributedCharacterIterator is null");
/*      */     }
/* 2977 */     if (paramAttributedCharacterIterator.getBeginIndex() == paramAttributedCharacterIterator.getEndIndex()) {
/* 2978 */       return;
/*      */     }
/* 2980 */     TextLayout localTextLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
/* 2981 */     localTextLayout.draw(this, paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */   public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2)
/*      */   {
/* 2986 */     if (paramAttributedCharacterIterator == null) {
/* 2987 */       throw new NullPointerException("AttributedCharacterIterator is null");
/*      */     }
/* 2989 */     if (paramAttributedCharacterIterator.getBeginIndex() == paramAttributedCharacterIterator.getEndIndex()) {
/* 2990 */       return;
/*      */     }
/* 2992 */     TextLayout localTextLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
/* 2993 */     localTextLayout.draw(this, paramFloat1, paramFloat2);
/*      */   }
/*      */   
/*      */   public void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
/*      */   {
/* 2998 */     if (paramGlyphVector == null) {
/* 2999 */       throw new NullPointerException("GlyphVector is null");
/*      */     }
/*      */     try
/*      */     {
/* 3003 */       this.textpipe.drawGlyphVector(this, paramGlyphVector, paramFloat1, paramFloat2);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3006 */         revalidateAll();
/* 3007 */         this.textpipe.drawGlyphVector(this, paramGlyphVector, paramFloat1, paramFloat2);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 3014 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 3020 */     if (paramArrayOfChar == null) {
/* 3021 */       throw new NullPointerException("char data is null");
/*      */     }
/* 3023 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length)) {
/* 3024 */       throw new ArrayIndexOutOfBoundsException("bad offset/length");
/*      */     }
/* 3026 */     if (this.font.hasLayoutAttributes()) {
/* 3027 */       if (paramArrayOfChar.length == 0) {
/* 3028 */         return;
/*      */       }
/*      */       
/* 3031 */       new TextLayout(new String(paramArrayOfChar, paramInt1, paramInt2), this.font, getFontRenderContext()).draw(this, paramInt3, paramInt4);
/* 3032 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 3036 */       this.textpipe.drawChars(this, paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3039 */         revalidateAll();
/* 3040 */         this.textpipe.drawChars(this, paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 3047 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   public void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 3052 */     if (paramArrayOfByte == null) {
/* 3053 */       throw new NullPointerException("byte data is null");
/*      */     }
/* 3055 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length)) {
/* 3056 */       throw new ArrayIndexOutOfBoundsException("bad offset/length");
/*      */     }
/*      */     
/* 3059 */     char[] arrayOfChar = new char[paramInt2];
/* 3060 */     for (int i = paramInt2; i-- > 0;) {
/* 3061 */       arrayOfChar[i] = ((char)(paramArrayOfByte[(i + paramInt1)] & 0xFF));
/*      */     }
/* 3063 */     if (this.font.hasLayoutAttributes()) {
/* 3064 */       if (paramArrayOfByte.length == 0) {
/* 3065 */         return;
/*      */       }
/*      */       
/* 3068 */       new TextLayout(new String(arrayOfChar), this.font, getFontRenderContext()).draw(this, paramInt3, paramInt4);
/* 3069 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 3073 */       this.textpipe.drawChars(this, arrayOfChar, 0, paramInt2, paramInt3, paramInt4);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3076 */         revalidateAll();
/* 3077 */         this.textpipe.drawChars(this, arrayOfChar, 0, paramInt2, paramInt3, paramInt4);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 3084 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean isHiDPIImage(Image paramImage)
/*      */   {
/* 3090 */     return (SurfaceManager.getImageScale(paramImage) != 1) || ((this.resolutionVariantHint != 1) && ((paramImage instanceof MultiResolutionImage)));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean drawHiDPIImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/*      */     int i;
/*      */     
/* 3099 */     if (SurfaceManager.getImageScale(paramImage) != 1) {
/* 3100 */       i = SurfaceManager.getImageScale(paramImage);
/* 3101 */       paramInt5 = Region.clipScale(paramInt5, i);
/* 3102 */       paramInt7 = Region.clipScale(paramInt7, i);
/* 3103 */       paramInt6 = Region.clipScale(paramInt6, i);
/* 3104 */       paramInt8 = Region.clipScale(paramInt8, i);
/* 3105 */     } else if ((paramImage instanceof MultiResolutionImage))
/*      */     {
/*      */ 
/* 3108 */       i = paramImage.getWidth(paramImageObserver);
/* 3109 */       int j = paramImage.getHeight(paramImageObserver);
/*      */       
/* 3111 */       Image localImage = getResolutionVariant((MultiResolutionImage)paramImage, i, j, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8);
/*      */       
/*      */ 
/*      */ 
/* 3115 */       if ((localImage != paramImage) && (localImage != null))
/*      */       {
/*      */ 
/*      */ 
/* 3119 */         ImageObserver localImageObserver = MultiResolutionToolkitImage.getResolutionVariantObserver(paramImage, paramImageObserver, i, j, -1, -1);
/*      */         
/*      */ 
/* 3122 */         int k = localImage.getWidth(localImageObserver);
/* 3123 */         int m = localImage.getHeight(localImageObserver);
/*      */         
/* 3125 */         if ((0 < i) && (0 < j) && (0 < k) && (0 < m))
/*      */         {
/* 3127 */           float f1 = k / i;
/* 3128 */           float f2 = m / j;
/*      */           
/* 3130 */           paramInt5 = Region.clipScale(paramInt5, f1);
/* 3131 */           paramInt6 = Region.clipScale(paramInt6, f2);
/* 3132 */           paramInt7 = Region.clipScale(paramInt7, f1);
/* 3133 */           paramInt8 = Region.clipScale(paramInt8, f2);
/*      */           
/* 3135 */           paramImageObserver = localImageObserver;
/* 3136 */           paramImage = localImage;
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 3142 */       return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
/*      */     }
/*      */     catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3146 */         revalidateAll();
/* 3147 */         return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */ 
/* 3153 */         return false;
/*      */       }
/*      */     } finally {
/* 3156 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Image getResolutionVariant(MultiResolutionImage paramMultiResolutionImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
/*      */   {
/* 3164 */     if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
/* 3165 */       return null;
/*      */     }
/*      */     
/* 3168 */     int i = paramInt9 - paramInt7;
/* 3169 */     int j = paramInt10 - paramInt8;
/*      */     
/* 3171 */     if ((i == 0) || (j == 0)) {
/* 3172 */       return null;
/*      */     }
/*      */     
/* 3175 */     int k = this.transform.getType();
/* 3176 */     int m = paramInt5 - paramInt3;
/* 3177 */     int n = paramInt6 - paramInt4;
/*      */     
/*      */     double d1;
/*      */     double d2;
/* 3181 */     if ((k & 0xFFFFFFBE) == 0) {
/* 3182 */       d1 = m;
/* 3183 */       d2 = n;
/* 3184 */     } else if ((k & 0xFFFFFFB8) == 0) {
/* 3185 */       d1 = m * this.transform.getScaleX();
/* 3186 */       d2 = n * this.transform.getScaleY();
/*      */     } else {
/* 3188 */       d1 = m * Math.hypot(this.transform
/* 3189 */         .getScaleX(), this.transform.getShearY());
/* 3190 */       d2 = n * Math.hypot(this.transform
/* 3191 */         .getShearX(), this.transform.getScaleY());
/*      */     }
/*      */     
/* 3194 */     int i1 = (int)Math.abs(paramInt1 * d1 / i);
/* 3195 */     int i2 = (int)Math.abs(paramInt2 * d2 / j);
/*      */     
/*      */ 
/* 3198 */     Image localImage = paramMultiResolutionImage.getResolutionVariant(i1, i2);
/*      */     
/* 3200 */     if (((localImage instanceof ToolkitImage)) && 
/* 3201 */       (((ToolkitImage)localImage).hasError())) {
/* 3202 */       return null;
/*      */     }
/*      */     
/* 3205 */     return localImage;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver)
/*      */   {
/* 3214 */     return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, null, paramImageObserver);
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
/*      */   public boolean copyImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/*      */     try
/*      */     {
/* 3229 */       return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor, paramImageObserver);
/*      */     }
/*      */     catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3233 */         revalidateAll();
/* 3234 */         return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor, paramImageObserver);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */ 
/* 3240 */         return false;
/*      */       }
/*      */     } finally {
/* 3243 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 3254 */     if (paramImage == null) {
/* 3255 */       return true;
/*      */     }
/*      */     
/* 3258 */     if ((paramInt3 == 0) || (paramInt4 == 0)) {
/* 3259 */       return true;
/*      */     }
/*      */     
/* 3262 */     int i = paramImage.getWidth(null);
/* 3263 */     int j = paramImage.getHeight(null);
/* 3264 */     if (isHiDPIImage(paramImage)) {
/* 3265 */       return drawHiDPIImage(paramImage, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, 0, 0, i, j, paramColor, paramImageObserver);
/*      */     }
/*      */     
/*      */ 
/* 3269 */     if ((paramInt3 == i) && (paramInt4 == j)) {
/* 3270 */       return copyImage(paramImage, paramInt1, paramInt2, 0, 0, paramInt3, paramInt4, paramColor, paramImageObserver);
/*      */     }
/*      */     try
/*      */     {
/* 3274 */       return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
/*      */     }
/*      */     catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3278 */         revalidateAll();
/* 3279 */         return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */ 
/* 3285 */         return false;
/*      */       }
/*      */     } finally {
/* 3288 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*      */   {
/* 3296 */     return drawImage(paramImage, paramInt1, paramInt2, null, paramImageObserver);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 3306 */     if (paramImage == null) {
/* 3307 */       return true;
/*      */     }
/*      */     
/* 3310 */     if (isHiDPIImage(paramImage)) {
/* 3311 */       int i = paramImage.getWidth(null);
/* 3312 */       int j = paramImage.getHeight(null);
/* 3313 */       return drawHiDPIImage(paramImage, paramInt1, paramInt2, paramInt1 + i, paramInt2 + j, 0, 0, i, j, paramColor, paramImageObserver);
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 3318 */       return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3321 */         revalidateAll();
/* 3322 */         return this.imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/* 3327 */         return false;
/*      */       }
/*      */     } finally {
/* 3330 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver)
/*      */   {
/* 3342 */     return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, null, paramImageObserver);
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
/*      */   public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 3355 */     if (paramImage == null) {
/* 3356 */       return true;
/*      */     }
/*      */     
/* 3359 */     if ((paramInt1 == paramInt3) || (paramInt2 == paramInt4) || (paramInt5 == paramInt7) || (paramInt6 == paramInt8))
/*      */     {
/*      */ 
/* 3362 */       return true;
/*      */     }
/*      */     
/* 3365 */     if (isHiDPIImage(paramImage)) {
/* 3366 */       return drawHiDPIImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
/*      */     }
/*      */     
/*      */     int k;
/* 3370 */     if ((paramInt7 - paramInt5 == paramInt3 - paramInt1) && (paramInt8 - paramInt6 == paramInt4 - paramInt2))
/*      */     {
/*      */       int n;
/*      */       
/*      */       int i;
/* 3375 */       if (paramInt7 > paramInt5) {
/* 3376 */         n = paramInt7 - paramInt5;
/* 3377 */         i = paramInt5;
/* 3378 */         k = paramInt1;
/*      */       } else {
/* 3380 */         n = paramInt5 - paramInt7;
/* 3381 */         i = paramInt7;
/* 3382 */         k = paramInt3; }
/*      */       int i1;
/* 3384 */       int j; int m; if (paramInt8 > paramInt6) {
/* 3385 */         i1 = paramInt8 - paramInt6;
/* 3386 */         j = paramInt6;
/* 3387 */         m = paramInt2;
/*      */       } else {
/* 3389 */         i1 = paramInt6 - paramInt8;
/* 3390 */         j = paramInt8;
/* 3391 */         m = paramInt4;
/*      */       }
/* 3393 */       return copyImage(paramImage, k, m, i, j, n, i1, paramColor, paramImageObserver);
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 3398 */       return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
/*      */     }
/*      */     catch (InvalidPipeException localInvalidPipeException1)
/*      */     {
/*      */       try {
/* 3403 */         revalidateAll();
/* 3404 */         return this.imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/*      */ 
/* 3411 */         return 0;
/*      */       }
/*      */     } finally {
/* 3414 */       this.surfaceData.markDirty();
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
/*      */   public boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
/*      */   {
/* 3440 */     if (paramImage == null) {
/* 3441 */       return true;
/*      */     }
/*      */     
/* 3444 */     if ((paramAffineTransform == null) || (paramAffineTransform.isIdentity())) {
/* 3445 */       return drawImage(paramImage, 0, 0, null, paramImageObserver);
/*      */     }
/*      */     
/* 3448 */     if (isHiDPIImage(paramImage)) {
/* 3449 */       int i = paramImage.getWidth(null);
/* 3450 */       int j = paramImage.getHeight(null);
/* 3451 */       AffineTransform localAffineTransform = new AffineTransform(this.transform);
/* 3452 */       transform(paramAffineTransform);
/* 3453 */       boolean bool4 = drawHiDPIImage(paramImage, 0, 0, i, j, 0, 0, i, j, null, paramImageObserver);
/*      */       
/* 3455 */       this.transform.setTransform(localAffineTransform);
/* 3456 */       invalidateTransform();
/* 3457 */       return bool4;
/*      */     }
/*      */     try
/*      */     {
/* 3461 */       return this.imagepipe.transformImage(this, paramImage, paramAffineTransform, paramImageObserver);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3464 */         revalidateAll();
/* 3465 */         return this.imagepipe.transformImage(this, paramImage, paramAffineTransform, paramImageObserver);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2)
/*      */       {
/* 3470 */         return false;
/*      */       }
/*      */     } finally {
/* 3473 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
/*      */   {
/* 3482 */     if (paramBufferedImage == null) {
/* 3483 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 3487 */       this.imagepipe.transformImage(this, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2);
/*      */     } catch (InvalidPipeException localInvalidPipeException1) {
/*      */       try {
/* 3490 */         revalidateAll();
/* 3491 */         this.imagepipe.transformImage(this, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2);
/*      */ 
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException2) {}
/*      */     }
/*      */     finally
/*      */     {
/* 3498 */       this.surfaceData.markDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public FontRenderContext getFontRenderContext()
/*      */   {
/* 3507 */     if (this.cachedFRC == null) {
/* 3508 */       int i = this.textAntialiasHint;
/* 3509 */       if ((i == 0) && (this.antialiasHint == 2))
/*      */       {
/* 3511 */         i = 2;
/*      */       }
/*      */       
/* 3514 */       AffineTransform localAffineTransform = null;
/* 3515 */       if (this.transformState >= 3) {
/* 3516 */         if ((this.transform.getTranslateX() == 0.0D) && 
/* 3517 */           (this.transform.getTranslateY() == 0.0D)) {
/* 3518 */           localAffineTransform = this.transform;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 3523 */           localAffineTransform = new AffineTransform(this.transform.getScaleX(), this.transform.getShearY(), this.transform.getShearX(), this.transform.getScaleY(), 0.0D, 0.0D);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3529 */       this.cachedFRC = new FontRenderContext(localAffineTransform, Value.get(2, i), Value.get(3, this.fractionalMetricsHint));
/*      */     }
/*      */     
/* 3532 */     return this.cachedFRC;
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
/*      */   public void dispose()
/*      */   {
/* 3546 */     this.surfaceData = NullSurfaceData.theInstance;
/* 3547 */     invalidatePipe();
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
/*      */   public Object getDestination()
/*      */   {
/* 3571 */     return this.surfaceData.getDestination();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Surface getDestSurface()
/*      */   {
/* 3581 */     return this.surfaceData;
/*      */   }
/*      */   
/*      */   public void finalize() {}
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\SunGraphics2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
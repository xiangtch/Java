/*      */ package sun.font;
/*      */ 
/*      */ import java.awt.Font;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.Shape;
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.font.GlyphJustificationInfo;
/*      */ import java.awt.font.GlyphMetrics;
/*      */ import java.awt.font.GlyphVector;
/*      */ import java.awt.font.LineMetrics;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.GeneralPath;
/*      */ import java.awt.geom.NoninvertibleTransformException;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.geom.Point2D;
/*      */ import java.awt.geom.Point2D.Float;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.Rectangle2D.Float;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.text.CharacterIterator;
/*      */ import sun.java2d.loops.FontInfo;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class StandardGlyphVector
/*      */   extends GlyphVector
/*      */ {
/*      */   private Font font;
/*      */   private FontRenderContext frc;
/*      */   private int[] glyphs;
/*      */   private int[] userGlyphs;
/*      */   private float[] positions;
/*      */   private int[] charIndices;
/*      */   private int flags;
/*      */   private static final int UNINITIALIZED_FLAGS = -1;
/*      */   private GlyphTransformInfo gti;
/*      */   private AffineTransform ftx;
/*      */   private AffineTransform dtx;
/*      */   private AffineTransform invdtx;
/*      */   private AffineTransform frctx;
/*      */   private Font2D font2D;
/*      */   private SoftReference fsref;
/*      */   private SoftReference lbcacheRef;
/*      */   private SoftReference vbcacheRef;
/*      */   public static final int FLAG_USES_VERTICAL_BASELINE = 128;
/*      */   public static final int FLAG_USES_VERTICAL_METRICS = 256;
/*      */   public static final int FLAG_USES_ALTERNATE_ORIENTATION = 512;
/*      */   
/*      */   public StandardGlyphVector(Font paramFont, String paramString, FontRenderContext paramFontRenderContext)
/*      */   {
/*  163 */     init(paramFont, paramString.toCharArray(), 0, paramString.length(), paramFontRenderContext, -1);
/*      */   }
/*      */   
/*      */   public StandardGlyphVector(Font paramFont, char[] paramArrayOfChar, FontRenderContext paramFontRenderContext) {
/*  167 */     init(paramFont, paramArrayOfChar, 0, paramArrayOfChar.length, paramFontRenderContext, -1);
/*      */   }
/*      */   
/*      */   public StandardGlyphVector(Font paramFont, char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
/*      */   {
/*  172 */     init(paramFont, paramArrayOfChar, paramInt1, paramInt2, paramFontRenderContext, -1);
/*      */   }
/*      */   
/*      */   private float getTracking(Font paramFont) {
/*  176 */     if (paramFont.hasLayoutAttributes()) {
/*  177 */       AttributeValues localAttributeValues = ((AttributeMap)paramFont.getAttributes()).getValues();
/*  178 */       return localAttributeValues.getTracking();
/*      */     }
/*  180 */     return 0.0F;
/*      */   }
/*      */   
/*      */ 
/*      */   public StandardGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, int[] paramArrayOfInt1, float[] paramArrayOfFloat, int[] paramArrayOfInt2, int paramInt)
/*      */   {
/*  186 */     initGlyphVector(paramFont, paramFontRenderContext, paramArrayOfInt1, paramArrayOfFloat, paramArrayOfInt2, paramInt);
/*      */     
/*      */ 
/*  189 */     float f1 = getTracking(paramFont);
/*  190 */     if (f1 != 0.0F) {
/*  191 */       f1 *= paramFont.getSize2D();
/*  192 */       Point2D.Float localFloat = new Point2D.Float(f1, 0.0F);
/*  193 */       if (paramFont.isTransformed()) {
/*  194 */         localObject = paramFont.getTransform();
/*  195 */         ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  200 */       Object localObject = FontUtilities.getFont2D(paramFont);
/*  201 */       FontStrike localFontStrike = ((Font2D)localObject).getStrike(paramFont, paramFontRenderContext);
/*      */       
/*  203 */       float[] arrayOfFloat = { localFloat.x, localFloat.y };
/*  204 */       for (int i = 0; i < arrayOfFloat.length; i++) {
/*  205 */         float f2 = arrayOfFloat[i];
/*  206 */         if (f2 != 0.0F) {
/*  207 */           float f3 = 0.0F;
/*  208 */           int j = i; for (int k = 0; k < paramArrayOfInt1.length; j += 2) {
/*  209 */             if (localFontStrike.getGlyphAdvance(paramArrayOfInt1[(k++)]) != 0.0F) {
/*  210 */               paramArrayOfFloat[j] += f3;
/*  211 */               f3 += f2;
/*      */             }
/*      */           }
/*  214 */           paramArrayOfFloat[(paramArrayOfFloat.length - 2 + i)] += f3;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void initGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, int[] paramArrayOfInt1, float[] paramArrayOfFloat, int[] paramArrayOfInt2, int paramInt)
/*      */   {
/*  222 */     this.font = paramFont;
/*  223 */     this.frc = paramFontRenderContext;
/*  224 */     this.glyphs = paramArrayOfInt1;
/*  225 */     this.userGlyphs = paramArrayOfInt1;
/*  226 */     this.positions = paramArrayOfFloat;
/*  227 */     this.charIndices = paramArrayOfInt2;
/*  228 */     this.flags = paramInt;
/*      */     
/*  230 */     initFontData();
/*      */   }
/*      */   
/*      */   public StandardGlyphVector(Font paramFont, CharacterIterator paramCharacterIterator, FontRenderContext paramFontRenderContext) {
/*  234 */     int i = paramCharacterIterator.getBeginIndex();
/*  235 */     char[] arrayOfChar = new char[paramCharacterIterator.getEndIndex() - i];
/*  236 */     for (int j = paramCharacterIterator.first(); 
/*  237 */         j != 65535; 
/*  238 */         j = paramCharacterIterator.next()) {
/*  239 */       arrayOfChar[(paramCharacterIterator.getIndex() - i)] = j;
/*      */     }
/*  241 */     init(paramFont, arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext, -1);
/*      */   }
/*      */   
/*      */ 
/*      */   public StandardGlyphVector(Font paramFont, int[] paramArrayOfInt, FontRenderContext paramFontRenderContext)
/*      */   {
/*  247 */     this.font = paramFont;
/*  248 */     this.frc = paramFontRenderContext;
/*  249 */     this.flags = -1;
/*      */     
/*  251 */     initFontData();
/*  252 */     this.userGlyphs = paramArrayOfInt;
/*  253 */     this.glyphs = getValidatedGlyphs(this.userGlyphs);
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
/*      */   public static StandardGlyphVector getStandardGV(GlyphVector paramGlyphVector, FontInfo paramFontInfo)
/*      */   {
/*  268 */     if (paramFontInfo.aaHint == 2) {
/*  269 */       Object localObject = paramGlyphVector.getFontRenderContext().getAntiAliasingHint();
/*  270 */       if ((localObject != RenderingHints.VALUE_TEXT_ANTIALIAS_ON) && (localObject != RenderingHints.VALUE_TEXT_ANTIALIAS_GASP))
/*      */       {
/*      */ 
/*  273 */         FontRenderContext localFontRenderContext = paramGlyphVector.getFontRenderContext();
/*      */         
/*      */ 
/*  276 */         localFontRenderContext = new FontRenderContext(localFontRenderContext.getTransform(), RenderingHints.VALUE_TEXT_ANTIALIAS_ON, localFontRenderContext.getFractionalMetricsHint());
/*  277 */         return new StandardGlyphVector(paramGlyphVector, localFontRenderContext);
/*      */       }
/*      */     }
/*  280 */     if ((paramGlyphVector instanceof StandardGlyphVector)) {
/*  281 */       return (StandardGlyphVector)paramGlyphVector;
/*      */     }
/*  283 */     return new StandardGlyphVector(paramGlyphVector, paramGlyphVector.getFontRenderContext());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Font getFont()
/*      */   {
/*  291 */     return this.font;
/*      */   }
/*      */   
/*      */   public FontRenderContext getFontRenderContext() {
/*  295 */     return this.frc;
/*      */   }
/*      */   
/*      */   public void performDefaultLayout() {
/*  299 */     this.positions = null;
/*  300 */     if (getTracking(this.font) == 0.0F) {
/*  301 */       clearFlags(2);
/*      */     }
/*      */   }
/*      */   
/*      */   public int getNumGlyphs() {
/*  306 */     return this.glyphs.length;
/*      */   }
/*      */   
/*      */   public int getGlyphCode(int paramInt) {
/*  310 */     return this.userGlyphs[paramInt];
/*      */   }
/*      */   
/*      */   public int[] getGlyphCodes(int paramInt1, int paramInt2, int[] paramArrayOfInt) {
/*  314 */     if (paramInt2 < 0) {
/*  315 */       throw new IllegalArgumentException("count = " + paramInt2);
/*      */     }
/*  317 */     if (paramInt1 < 0) {
/*  318 */       throw new IndexOutOfBoundsException("start = " + paramInt1);
/*      */     }
/*  320 */     if (paramInt1 > this.glyphs.length - paramInt2) {
/*  321 */       throw new IndexOutOfBoundsException("start + count = " + (paramInt1 + paramInt2));
/*      */     }
/*      */     
/*  324 */     if (paramArrayOfInt == null) {
/*  325 */       paramArrayOfInt = new int[paramInt2];
/*      */     }
/*      */     
/*      */ 
/*  329 */     for (int i = 0; i < paramInt2; i++) {
/*  330 */       paramArrayOfInt[i] = this.userGlyphs[(i + paramInt1)];
/*      */     }
/*      */     
/*  333 */     return paramArrayOfInt;
/*      */   }
/*      */   
/*      */   public int getGlyphCharIndex(int paramInt) {
/*  337 */     if ((paramInt < 0) && (paramInt >= this.glyphs.length)) {
/*  338 */       throw new IndexOutOfBoundsException("" + paramInt);
/*      */     }
/*  340 */     if (this.charIndices == null) {
/*  341 */       if ((getLayoutFlags() & 0x4) != 0) {
/*  342 */         return this.glyphs.length - 1 - paramInt;
/*      */       }
/*  344 */       return paramInt;
/*      */     }
/*  346 */     return this.charIndices[paramInt];
/*      */   }
/*      */   
/*      */   public int[] getGlyphCharIndices(int paramInt1, int paramInt2, int[] paramArrayOfInt) {
/*  350 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > this.glyphs.length - paramInt1)) {
/*  351 */       throw new IndexOutOfBoundsException("" + paramInt1 + ", " + paramInt2);
/*      */     }
/*  353 */     if (paramArrayOfInt == null)
/*  354 */       paramArrayOfInt = new int[paramInt2];
/*      */     int i;
/*  356 */     if (this.charIndices == null) { int j;
/*  357 */       if ((getLayoutFlags() & 0x4) != 0) {
/*  358 */         i = 0; for (j = this.glyphs.length - 1 - paramInt1; 
/*  359 */             i < paramInt2; j--) {
/*  360 */           paramArrayOfInt[i] = j;i++;
/*      */         }
/*      */       } else {
/*  363 */         i = 0; for (j = paramInt1; i < paramInt2; j++) {
/*  364 */           paramArrayOfInt[i] = j;i++;
/*      */         }
/*      */       }
/*      */     } else {
/*  368 */       for (i = 0; i < paramInt2; i++) {
/*  369 */         paramArrayOfInt[i] = this.charIndices[(i + paramInt1)];
/*      */       }
/*      */     }
/*  372 */     return paramArrayOfInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Rectangle2D getLogicalBounds()
/*      */   {
/*  379 */     setFRCTX();
/*  380 */     initPositions();
/*      */     
/*  382 */     LineMetrics localLineMetrics = this.font.getLineMetrics("", this.frc);
/*      */     
/*      */ 
/*      */ 
/*  386 */     float f1 = 0.0F;
/*  387 */     float f2 = -localLineMetrics.getAscent();
/*  388 */     float f3 = 0.0F;
/*  389 */     float f4 = localLineMetrics.getDescent() + localLineMetrics.getLeading();
/*  390 */     if (this.glyphs.length > 0) {
/*  391 */       f3 = this.positions[(this.positions.length - 2)];
/*      */     }
/*      */     
/*  394 */     return new Rectangle2D.Float(f1, f2, f3 - f1, f4 - f2);
/*      */   }
/*      */   
/*      */   public Rectangle2D getVisualBounds()
/*      */   {
/*  399 */     Object localObject = null;
/*  400 */     for (int i = 0; i < this.glyphs.length; i++) {
/*  401 */       Rectangle2D localRectangle2D = getGlyphVisualBounds(i).getBounds2D();
/*  402 */       if (!localRectangle2D.isEmpty()) {
/*  403 */         if (localObject == null) {
/*  404 */           localObject = localRectangle2D;
/*      */         } else {
/*  406 */           Rectangle2D.union((Rectangle2D)localObject, localRectangle2D, (Rectangle2D)localObject);
/*      */         }
/*      */       }
/*      */     }
/*  410 */     if (localObject == null) {
/*  411 */       localObject = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
/*      */     }
/*  413 */     return (Rectangle2D)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */   public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2)
/*      */   {
/*  419 */     return getGlyphsPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2, 0, this.glyphs.length);
/*      */   }
/*      */   
/*      */   public Shape getOutline() {
/*  423 */     return getGlyphsOutline(0, this.glyphs.length, 0.0F, 0.0F);
/*      */   }
/*      */   
/*      */   public Shape getOutline(float paramFloat1, float paramFloat2) {
/*  427 */     return getGlyphsOutline(0, this.glyphs.length, paramFloat1, paramFloat2);
/*      */   }
/*      */   
/*      */   public Shape getGlyphOutline(int paramInt)
/*      */   {
/*  432 */     return getGlyphsOutline(paramInt, 1, 0.0F, 0.0F);
/*      */   }
/*      */   
/*      */   public Shape getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2)
/*      */   {
/*  437 */     return getGlyphsOutline(paramInt, 1, paramFloat1, paramFloat2);
/*      */   }
/*      */   
/*      */   public Point2D getGlyphPosition(int paramInt) {
/*  441 */     initPositions();
/*      */     
/*  443 */     paramInt *= 2;
/*  444 */     return new Point2D.Float(this.positions[paramInt], this.positions[(paramInt + 1)]);
/*      */   }
/*      */   
/*      */   public void setGlyphPosition(int paramInt, Point2D paramPoint2D) {
/*  448 */     initPositions();
/*      */     
/*  450 */     int i = paramInt << 1;
/*  451 */     this.positions[i] = ((float)paramPoint2D.getX());
/*  452 */     this.positions[(i + 1)] = ((float)paramPoint2D.getY());
/*      */     
/*  454 */     clearCaches(paramInt);
/*  455 */     addFlags(2);
/*      */   }
/*      */   
/*      */   public AffineTransform getGlyphTransform(int paramInt) {
/*  459 */     if ((paramInt < 0) || (paramInt >= this.glyphs.length)) {
/*  460 */       throw new IndexOutOfBoundsException("ix = " + paramInt);
/*      */     }
/*  462 */     if (this.gti != null) {
/*  463 */       return this.gti.getGlyphTransform(paramInt);
/*      */     }
/*  465 */     return null;
/*      */   }
/*      */   
/*      */   public void setGlyphTransform(int paramInt, AffineTransform paramAffineTransform) {
/*  469 */     if ((paramInt < 0) || (paramInt >= this.glyphs.length)) {
/*  470 */       throw new IndexOutOfBoundsException("ix = " + paramInt);
/*      */     }
/*      */     
/*  473 */     if (this.gti == null) {
/*  474 */       if ((paramAffineTransform == null) || (paramAffineTransform.isIdentity())) {
/*  475 */         return;
/*      */       }
/*  477 */       this.gti = new GlyphTransformInfo(this);
/*      */     }
/*  479 */     this.gti.setGlyphTransform(paramInt, paramAffineTransform);
/*  480 */     if (this.gti.transformCount() == 0) {
/*  481 */       this.gti = null;
/*      */     }
/*      */   }
/*      */   
/*      */   public int getLayoutFlags() {
/*  486 */     if (this.flags == -1) {
/*  487 */       this.flags = 0;
/*      */       
/*  489 */       if ((this.charIndices != null) && (this.glyphs.length > 1)) {
/*  490 */         int i = 1;
/*  491 */         int j = 1;
/*      */         
/*  493 */         int k = this.charIndices.length;
/*  494 */         for (int m = 0; (m < this.charIndices.length) && ((i != 0) || (j != 0)); m++) {
/*  495 */           int n = this.charIndices[m];
/*      */           
/*  497 */           i = (i != 0) && (n == m) ? 1 : 0;
/*  498 */           j = (j != 0) && (n == --k) ? 1 : 0;
/*      */         }
/*      */         
/*  501 */         if (j != 0) this.flags |= 0x4;
/*  502 */         if ((j == 0) && (i == 0)) { this.flags |= 0x8;
/*      */         }
/*      */       }
/*      */     }
/*  506 */     return this.flags;
/*      */   }
/*      */   
/*      */   public float[] getGlyphPositions(int paramInt1, int paramInt2, float[] paramArrayOfFloat) {
/*  510 */     if (paramInt2 < 0) {
/*  511 */       throw new IllegalArgumentException("count = " + paramInt2);
/*      */     }
/*  513 */     if (paramInt1 < 0) {
/*  514 */       throw new IndexOutOfBoundsException("start = " + paramInt1);
/*      */     }
/*  516 */     if (paramInt1 > this.glyphs.length + 1 - paramInt2) {
/*  517 */       throw new IndexOutOfBoundsException("start + count = " + (paramInt1 + paramInt2));
/*      */     }
/*      */     
/*  520 */     return internalGetGlyphPositions(paramInt1, paramInt2, 0, paramArrayOfFloat);
/*      */   }
/*      */   
/*      */   public Shape getGlyphLogicalBounds(int paramInt) {
/*  524 */     if ((paramInt < 0) || (paramInt >= this.glyphs.length)) {
/*  525 */       throw new IndexOutOfBoundsException("ix = " + paramInt);
/*      */     }
/*      */     
/*      */     Shape[] arrayOfShape;
/*  529 */     if ((this.lbcacheRef == null) || ((arrayOfShape = (Shape[])this.lbcacheRef.get()) == null)) {
/*  530 */       arrayOfShape = new Shape[this.glyphs.length];
/*  531 */       this.lbcacheRef = new SoftReference(arrayOfShape);
/*      */     }
/*      */     
/*  534 */     Object localObject = arrayOfShape[paramInt];
/*  535 */     if (localObject == null) {
/*  536 */       setFRCTX();
/*  537 */       initPositions();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  545 */       ADL localADL = new ADL();
/*  546 */       GlyphStrike localGlyphStrike = getGlyphStrike(paramInt);
/*  547 */       localGlyphStrike.getADL(localADL);
/*      */       
/*  549 */       Point2D.Float localFloat = localGlyphStrike.strike.getGlyphMetrics(this.glyphs[paramInt]);
/*      */       
/*  551 */       float f1 = localFloat.x;
/*  552 */       float f2 = localFloat.y;
/*  553 */       float f3 = localADL.descentX + localADL.leadingX + localADL.ascentX;
/*  554 */       float f4 = localADL.descentY + localADL.leadingY + localADL.ascentY;
/*  555 */       float f5 = this.positions[(paramInt * 2)] + localGlyphStrike.dx - localADL.ascentX;
/*  556 */       float f6 = this.positions[(paramInt * 2 + 1)] + localGlyphStrike.dy - localADL.ascentY;
/*      */       
/*  558 */       GeneralPath localGeneralPath = new GeneralPath();
/*  559 */       localGeneralPath.moveTo(f5, f6);
/*  560 */       localGeneralPath.lineTo(f5 + f1, f6 + f2);
/*  561 */       localGeneralPath.lineTo(f5 + f1 + f3, f6 + f2 + f4);
/*  562 */       localGeneralPath.lineTo(f5 + f3, f6 + f4);
/*  563 */       localGeneralPath.closePath();
/*      */       
/*  565 */       localObject = new DelegatingShape(localGeneralPath);
/*  566 */       arrayOfShape[paramInt] = localObject;
/*      */     }
/*      */     
/*  569 */     return (Shape)localObject;
/*      */   }
/*      */   
/*      */   public Shape getGlyphVisualBounds(int paramInt)
/*      */   {
/*  574 */     if ((paramInt < 0) || (paramInt >= this.glyphs.length)) {
/*  575 */       throw new IndexOutOfBoundsException("ix = " + paramInt);
/*      */     }
/*      */     
/*      */     Shape[] arrayOfShape;
/*  579 */     if ((this.vbcacheRef == null) || ((arrayOfShape = (Shape[])this.vbcacheRef.get()) == null)) {
/*  580 */       arrayOfShape = new Shape[this.glyphs.length];
/*  581 */       this.vbcacheRef = new SoftReference(arrayOfShape);
/*      */     }
/*      */     
/*  584 */     Object localObject = arrayOfShape[paramInt];
/*  585 */     if (localObject == null) {
/*  586 */       localObject = new DelegatingShape(getGlyphOutlineBounds(paramInt));
/*  587 */       arrayOfShape[paramInt] = localObject;
/*      */     }
/*      */     
/*  590 */     return (Shape)localObject;
/*      */   }
/*      */   
/*      */   public Rectangle getGlyphPixelBounds(int paramInt, FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2)
/*      */   {
/*  595 */     return getGlyphsPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2, paramInt, 1);
/*      */   }
/*      */   
/*      */   public GlyphMetrics getGlyphMetrics(int paramInt) {
/*  599 */     if ((paramInt < 0) || (paramInt >= this.glyphs.length)) {
/*  600 */       throw new IndexOutOfBoundsException("ix = " + paramInt);
/*      */     }
/*      */     
/*  603 */     Rectangle2D localRectangle2D = getGlyphVisualBounds(paramInt).getBounds2D();
/*  604 */     Point2D localPoint2D = getGlyphPosition(paramInt);
/*  605 */     localRectangle2D.setRect(localRectangle2D.getMinX() - localPoint2D.getX(), localRectangle2D
/*  606 */       .getMinY() - localPoint2D.getY(), localRectangle2D
/*  607 */       .getWidth(), localRectangle2D
/*  608 */       .getHeight());
/*      */     
/*  610 */     Point2D.Float localFloat = getGlyphStrike(paramInt).strike.getGlyphMetrics(this.glyphs[paramInt]);
/*  611 */     GlyphMetrics localGlyphMetrics = new GlyphMetrics(true, localFloat.x, localFloat.y, localRectangle2D, (byte)0);
/*      */     
/*      */ 
/*  614 */     return localGlyphMetrics;
/*      */   }
/*      */   
/*      */   public GlyphJustificationInfo getGlyphJustificationInfo(int paramInt) {
/*  618 */     if ((paramInt < 0) || (paramInt >= this.glyphs.length)) {
/*  619 */       throw new IndexOutOfBoundsException("ix = " + paramInt);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  627 */     return null;
/*      */   }
/*      */   
/*      */   public boolean equals(GlyphVector paramGlyphVector) {
/*  631 */     if (this == paramGlyphVector) {
/*  632 */       return true;
/*      */     }
/*  634 */     if (paramGlyphVector == null) {
/*  635 */       return false;
/*      */     }
/*      */     try
/*      */     {
/*  639 */       StandardGlyphVector localStandardGlyphVector = (StandardGlyphVector)paramGlyphVector;
/*      */       
/*  641 */       if (this.glyphs.length != localStandardGlyphVector.glyphs.length) {
/*  642 */         return false;
/*      */       }
/*      */       
/*  645 */       for (int i = 0; i < this.glyphs.length; i++) {
/*  646 */         if (this.glyphs[i] != localStandardGlyphVector.glyphs[i]) {
/*  647 */           return false;
/*      */         }
/*      */       }
/*      */       
/*  651 */       if (!this.font.equals(localStandardGlyphVector.font)) {
/*  652 */         return false;
/*      */       }
/*      */       
/*  655 */       if (!this.frc.equals(localStandardGlyphVector.frc)) {
/*  656 */         return false;
/*      */       }
/*      */       
/*  659 */       if ((localStandardGlyphVector.positions == null ? 1 : 0) != (this.positions == null ? 1 : 0)) {
/*  660 */         if (this.positions == null) {
/*  661 */           initPositions();
/*      */         } else {
/*  663 */           localStandardGlyphVector.initPositions();
/*      */         }
/*      */       }
/*      */       
/*  667 */       if (this.positions != null) {
/*  668 */         for (i = 0; i < this.positions.length; i++) {
/*  669 */           if (this.positions[i] != localStandardGlyphVector.positions[i]) {
/*  670 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  675 */       if (this.gti == null) {
/*  676 */         return localStandardGlyphVector.gti == null;
/*      */       }
/*  678 */       return this.gti.equals(localStandardGlyphVector.gti);
/*      */     }
/*      */     catch (ClassCastException localClassCastException) {}
/*      */     
/*      */ 
/*      */ 
/*  684 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  693 */     return this.font.hashCode() ^ this.glyphs.length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*      */     try
/*      */     {
/*  703 */       return equals((GlyphVector)paramObject);
/*      */     }
/*      */     catch (ClassCastException localClassCastException) {}
/*  706 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public StandardGlyphVector copy()
/*      */   {
/*  714 */     return (StandardGlyphVector)clone();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object clone()
/*      */   {
/*      */     try
/*      */     {
/*  725 */       StandardGlyphVector localStandardGlyphVector = (StandardGlyphVector)super.clone();
/*      */       
/*  727 */       localStandardGlyphVector.clearCaches();
/*      */       
/*  729 */       if (this.positions != null) {
/*  730 */         localStandardGlyphVector.positions = ((float[])this.positions.clone());
/*      */       }
/*      */       
/*  733 */       if (this.gti != null) {
/*  734 */         localStandardGlyphVector.gti = new GlyphTransformInfo(localStandardGlyphVector, this.gti);
/*      */       }
/*      */       
/*  737 */       return localStandardGlyphVector;
/*      */     }
/*      */     catch (CloneNotSupportedException localCloneNotSupportedException) {}
/*      */     
/*      */ 
/*  742 */     return this;
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
/*      */   public void setGlyphPositions(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  755 */     if (paramInt3 < 0) {
/*  756 */       throw new IllegalArgumentException("count = " + paramInt3);
/*      */     }
/*      */     
/*  759 */     initPositions();
/*  760 */     int i = paramInt2 * 2;int j = i + paramInt3 * 2; for (int k = paramInt1; i < j; k++) {
/*  761 */       this.positions[i] = paramArrayOfFloat[k];i++;
/*      */     }
/*      */     
/*  764 */     clearCaches();
/*  765 */     addFlags(2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setGlyphPositions(float[] paramArrayOfFloat)
/*      */   {
/*  773 */     int i = this.glyphs.length * 2 + 2;
/*  774 */     if (paramArrayOfFloat.length != i) {
/*  775 */       throw new IllegalArgumentException("srcPositions.length != " + i);
/*      */     }
/*      */     
/*  778 */     this.positions = ((float[])paramArrayOfFloat.clone());
/*      */     
/*  780 */     clearCaches();
/*  781 */     addFlags(2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public float[] getGlyphPositions(float[] paramArrayOfFloat)
/*      */   {
/*  790 */     return internalGetGlyphPositions(0, this.glyphs.length + 1, 0, paramArrayOfFloat);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public AffineTransform[] getGlyphTransforms(int paramInt1, int paramInt2, AffineTransform[] paramArrayOfAffineTransform)
/*      */   {
/*  801 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > this.glyphs.length)) {
/*  802 */       throw new IllegalArgumentException("start: " + paramInt1 + " count: " + paramInt2);
/*      */     }
/*      */     
/*  805 */     if (this.gti == null) {
/*  806 */       return null;
/*      */     }
/*      */     
/*  809 */     if (paramArrayOfAffineTransform == null) {
/*  810 */       paramArrayOfAffineTransform = new AffineTransform[paramInt2];
/*      */     }
/*      */     
/*  813 */     for (int i = 0; i < paramInt2; paramInt1++) {
/*  814 */       paramArrayOfAffineTransform[i] = this.gti.getGlyphTransform(paramInt1);i++;
/*      */     }
/*      */     
/*  817 */     return paramArrayOfAffineTransform;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public AffineTransform[] getGlyphTransforms()
/*      */   {
/*  824 */     return getGlyphTransforms(0, this.glyphs.length, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setGlyphTransforms(AffineTransform[] paramArrayOfAffineTransform, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  833 */     int i = paramInt2; for (int j = paramInt2 + paramInt3; i < j; i++) {
/*  834 */       setGlyphTransform(i, paramArrayOfAffineTransform[(paramInt1 + i)]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setGlyphTransforms(AffineTransform[] paramArrayOfAffineTransform)
/*      */   {
/*  842 */     setGlyphTransforms(paramArrayOfAffineTransform, 0, 0, this.glyphs.length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public float[] getGlyphInfo()
/*      */   {
/*  849 */     setFRCTX();
/*  850 */     initPositions();
/*  851 */     float[] arrayOfFloat = new float[this.glyphs.length * 8];
/*  852 */     int i = 0; for (int j = 0; i < this.glyphs.length; j += 8) {
/*  853 */       float f1 = this.positions[(i * 2)];
/*  854 */       float f2 = this.positions[(i * 2 + 1)];
/*  855 */       arrayOfFloat[j] = f1;
/*  856 */       arrayOfFloat[(j + 1)] = f2;
/*      */       
/*  858 */       int k = this.glyphs[i];
/*  859 */       GlyphStrike localGlyphStrike = getGlyphStrike(i);
/*  860 */       Point2D.Float localFloat = localGlyphStrike.strike.getGlyphMetrics(k);
/*  861 */       arrayOfFloat[(j + 2)] = localFloat.x;
/*  862 */       arrayOfFloat[(j + 3)] = localFloat.y;
/*      */       
/*  864 */       Rectangle2D localRectangle2D = getGlyphVisualBounds(i).getBounds2D();
/*  865 */       arrayOfFloat[(j + 4)] = ((float)localRectangle2D.getMinX());
/*  866 */       arrayOfFloat[(j + 5)] = ((float)localRectangle2D.getMinY());
/*  867 */       arrayOfFloat[(j + 6)] = ((float)localRectangle2D.getWidth());
/*  868 */       arrayOfFloat[(j + 7)] = ((float)localRectangle2D.getHeight());i++;
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
/*  870 */     return arrayOfFloat;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void pixellate(FontRenderContext paramFontRenderContext, Point2D paramPoint2D, Point paramPoint)
/*      */   {
/*  877 */     if (paramFontRenderContext == null) {
/*  878 */       paramFontRenderContext = this.frc;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  883 */     AffineTransform localAffineTransform = paramFontRenderContext.getTransform();
/*  884 */     localAffineTransform.transform(paramPoint2D, paramPoint2D);
/*  885 */     paramPoint.x = ((int)paramPoint2D.getX());
/*  886 */     paramPoint.y = ((int)paramPoint2D.getY());
/*  887 */     paramPoint2D.setLocation(paramPoint.x, paramPoint.y);
/*      */     try {
/*  889 */       localAffineTransform.inverseTransform(paramPoint2D, paramPoint2D);
/*      */     }
/*      */     catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/*  892 */       throw new IllegalArgumentException("must be able to invert frc transform");
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
/*      */   boolean needsPositions(double[] paramArrayOfDouble)
/*      */   {
/*  905 */     return (this.gti != null) || 
/*  906 */       ((getLayoutFlags() & 0x2) != 0) || 
/*  907 */       (!matchTX(paramArrayOfDouble, this.frctx));
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
/*      */   Object setupGlyphImages(long[] paramArrayOfLong, float[] paramArrayOfFloat, double[] paramArrayOfDouble)
/*      */   {
/*  930 */     initPositions();
/*  931 */     setRenderTransform(paramArrayOfDouble);
/*      */     
/*  933 */     if (this.gti != null) {
/*  934 */       return this.gti.setupGlyphImages(paramArrayOfLong, paramArrayOfFloat, this.dtx);
/*      */     }
/*      */     
/*  937 */     GlyphStrike localGlyphStrike = getDefaultStrike();
/*  938 */     localGlyphStrike.strike.getGlyphImagePtrs(this.glyphs, paramArrayOfLong, this.glyphs.length);
/*      */     
/*  940 */     if (paramArrayOfFloat != null) {
/*  941 */       if (this.dtx.isIdentity()) {
/*  942 */         System.arraycopy(this.positions, 0, paramArrayOfFloat, 0, this.glyphs.length * 2);
/*      */       } else {
/*  944 */         this.dtx.transform(this.positions, 0, paramArrayOfFloat, 0, this.glyphs.length);
/*      */       }
/*      */     }
/*      */     
/*  948 */     return localGlyphStrike;
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
/*      */   private static boolean matchTX(double[] paramArrayOfDouble, AffineTransform paramAffineTransform)
/*      */   {
/*  962 */     return 
/*  963 */       (paramArrayOfDouble[0] == paramAffineTransform.getScaleX()) && 
/*  964 */       (paramArrayOfDouble[1] == paramAffineTransform.getShearY()) && 
/*  965 */       (paramArrayOfDouble[2] == paramAffineTransform.getShearX()) && 
/*  966 */       (paramArrayOfDouble[3] == paramAffineTransform.getScaleY());
/*      */   }
/*      */   
/*      */   private static AffineTransform getNonTranslateTX(AffineTransform paramAffineTransform)
/*      */   {
/*  971 */     if ((paramAffineTransform.getTranslateX() != 0.0D) || (paramAffineTransform.getTranslateY() != 0.0D))
/*      */     {
/*  973 */       paramAffineTransform = new AffineTransform(paramAffineTransform.getScaleX(), paramAffineTransform.getShearY(), paramAffineTransform.getShearX(), paramAffineTransform.getScaleY(), 0.0D, 0.0D);
/*      */     }
/*      */     
/*  976 */     return paramAffineTransform;
/*      */   }
/*      */   
/*      */   private static boolean equalNonTranslateTX(AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2) {
/*  980 */     return (paramAffineTransform1.getScaleX() == paramAffineTransform2.getScaleX()) && 
/*  981 */       (paramAffineTransform1.getShearY() == paramAffineTransform2.getShearY()) && 
/*  982 */       (paramAffineTransform1.getShearX() == paramAffineTransform2.getShearX()) && 
/*  983 */       (paramAffineTransform1.getScaleY() == paramAffineTransform2.getScaleY());
/*      */   }
/*      */   
/*      */   private void setRenderTransform(double[] paramArrayOfDouble)
/*      */   {
/*  988 */     assert (paramArrayOfDouble.length == 4);
/*  989 */     if (!matchTX(paramArrayOfDouble, this.dtx)) {
/*  990 */       resetDTX(new AffineTransform(paramArrayOfDouble));
/*      */     }
/*      */   }
/*      */   
/*      */   private final void setDTX(AffineTransform paramAffineTransform)
/*      */   {
/*  996 */     if (!equalNonTranslateTX(this.dtx, paramAffineTransform)) {
/*  997 */       resetDTX(getNonTranslateTX(paramAffineTransform));
/*      */     }
/*      */   }
/*      */   
/*      */   private final void setFRCTX()
/*      */   {
/* 1003 */     if (!equalNonTranslateTX(this.frctx, this.dtx)) {
/* 1004 */       resetDTX(getNonTranslateTX(this.frctx));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void resetDTX(AffineTransform paramAffineTransform)
/*      */   {
/* 1014 */     this.fsref = null;
/* 1015 */     this.dtx = paramAffineTransform;
/* 1016 */     this.invdtx = null;
/* 1017 */     if (!this.dtx.isIdentity()) {
/*      */       try {
/* 1019 */         this.invdtx = this.dtx.createInverse();
/*      */       }
/*      */       catch (NoninvertibleTransformException localNoninvertibleTransformException) {}
/*      */     }
/*      */     
/*      */ 
/* 1025 */     if (this.gti != null) {
/* 1026 */       this.gti.strikesRef = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private StandardGlyphVector(GlyphVector paramGlyphVector, FontRenderContext paramFontRenderContext)
/*      */   {
/* 1037 */     this.font = paramGlyphVector.getFont();
/* 1038 */     this.frc = paramFontRenderContext;
/* 1039 */     initFontData();
/*      */     
/* 1041 */     int i = paramGlyphVector.getNumGlyphs();
/* 1042 */     this.userGlyphs = paramGlyphVector.getGlyphCodes(0, i, null);
/* 1043 */     if ((paramGlyphVector instanceof StandardGlyphVector))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1050 */       this.glyphs = this.userGlyphs;
/*      */     } else {
/* 1052 */       this.glyphs = getValidatedGlyphs(this.userGlyphs);
/*      */     }
/* 1054 */     this.flags = (paramGlyphVector.getLayoutFlags() & 0xF);
/*      */     
/* 1056 */     if ((this.flags & 0x2) != 0) {
/* 1057 */       this.positions = paramGlyphVector.getGlyphPositions(0, i + 1, null);
/*      */     }
/*      */     
/* 1060 */     if ((this.flags & 0x8) != 0) {
/* 1061 */       this.charIndices = paramGlyphVector.getGlyphCharIndices(0, i, null);
/*      */     }
/*      */     
/* 1064 */     if ((this.flags & 0x1) != 0) {
/* 1065 */       AffineTransform[] arrayOfAffineTransform = new AffineTransform[i];
/* 1066 */       for (int j = 0; j < i; j++) {
/* 1067 */         arrayOfAffineTransform[j] = paramGlyphVector.getGlyphTransform(j);
/*      */       }
/*      */       
/* 1070 */       setGlyphTransforms(arrayOfAffineTransform);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   int[] getValidatedGlyphs(int[] paramArrayOfInt)
/*      */   {
/* 1080 */     int i = paramArrayOfInt.length;
/* 1081 */     int[] arrayOfInt = new int[i];
/* 1082 */     for (int j = 0; j < i; j++) {
/* 1083 */       if ((paramArrayOfInt[j] == 65534) || (paramArrayOfInt[j] == 65535)) {
/* 1084 */         arrayOfInt[j] = paramArrayOfInt[j];
/*      */       } else {
/* 1086 */         arrayOfInt[j] = this.font2D.getValidatedGlyphCode(paramArrayOfInt[j]);
/*      */       }
/*      */     }
/* 1089 */     return arrayOfInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void init(Font paramFont, char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext, int paramInt3)
/*      */   {
/* 1096 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length)) {
/* 1097 */       throw new ArrayIndexOutOfBoundsException("start or count out of bounds");
/*      */     }
/*      */     
/* 1100 */     this.font = paramFont;
/* 1101 */     this.frc = paramFontRenderContext;
/* 1102 */     this.flags = paramInt3;
/*      */     
/* 1104 */     if (getTracking(paramFont) != 0.0F) {
/* 1105 */       addFlags(2);
/*      */     }
/*      */     
/*      */ 
/* 1109 */     if (paramInt1 != 0) {
/* 1110 */       char[] arrayOfChar = new char[paramInt2];
/* 1111 */       System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, paramInt2);
/* 1112 */       paramArrayOfChar = arrayOfChar;
/*      */     }
/*      */     
/* 1115 */     initFontData();
/*      */     
/*      */ 
/*      */ 
/* 1119 */     this.glyphs = new int[paramInt2];
/*      */     
/* 1121 */     this.userGlyphs = this.glyphs;
/* 1122 */     this.font2D.getMapper().charsToGlyphs(paramInt2, paramArrayOfChar, this.glyphs);
/*      */   }
/*      */   
/*      */   private void initFontData() {
/* 1126 */     this.font2D = FontUtilities.getFont2D(this.font);
/* 1127 */     if ((this.font2D instanceof FontSubstitution)) {
/* 1128 */       this.font2D = ((FontSubstitution)this.font2D).getCompositeFont2D();
/*      */     }
/* 1130 */     float f = this.font.getSize2D();
/* 1131 */     if (this.font.isTransformed()) {
/* 1132 */       this.ftx = this.font.getTransform();
/* 1133 */       if ((this.ftx.getTranslateX() != 0.0D) || (this.ftx.getTranslateY() != 0.0D)) {
/* 1134 */         addFlags(2);
/*      */       }
/* 1136 */       this.ftx.setTransform(this.ftx.getScaleX(), this.ftx.getShearY(), this.ftx.getShearX(), this.ftx.getScaleY(), 0.0D, 0.0D);
/* 1137 */       this.ftx.scale(f, f);
/*      */     } else {
/* 1139 */       this.ftx = AffineTransform.getScaleInstance(f, f);
/*      */     }
/*      */     
/* 1142 */     this.frctx = this.frc.getTransform();
/* 1143 */     resetDTX(getNonTranslateTX(this.frctx));
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
/*      */   private float[] internalGetGlyphPositions(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat)
/*      */   {
/* 1160 */     if (paramArrayOfFloat == null) {
/* 1161 */       paramArrayOfFloat = new float[paramInt3 + paramInt2 * 2];
/*      */     }
/*      */     
/* 1164 */     initPositions();
/*      */     
/*      */ 
/* 1167 */     int i = paramInt3;int j = paramInt3 + paramInt2 * 2; for (int k = paramInt1 * 2; i < j; k++) {
/* 1168 */       paramArrayOfFloat[i] = this.positions[k];i++;
/*      */     }
/*      */     
/* 1171 */     return paramArrayOfFloat;
/*      */   }
/*      */   
/*      */   private Rectangle2D getGlyphOutlineBounds(int paramInt) {
/* 1175 */     setFRCTX();
/* 1176 */     initPositions();
/* 1177 */     return getGlyphStrike(paramInt).getGlyphOutlineBounds(this.glyphs[paramInt], this.positions[(paramInt * 2)], this.positions[(paramInt * 2 + 1)]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Shape getGlyphsOutline(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
/*      */   {
/* 1184 */     setFRCTX();
/* 1185 */     initPositions();
/*      */     
/* 1187 */     GeneralPath localGeneralPath = new GeneralPath(1);
/* 1188 */     int i = paramInt1;int j = paramInt1 + paramInt2; for (int k = paramInt1 * 2; i < j; k += 2) {
/* 1189 */       float f1 = paramFloat1 + this.positions[k];
/* 1190 */       float f2 = paramFloat2 + this.positions[(k + 1)];
/*      */       
/* 1192 */       getGlyphStrike(i).appendGlyphOutline(this.glyphs[i], localGeneralPath, f1, f2);i++;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1195 */     return localGeneralPath;
/*      */   }
/*      */   
/*      */   private Rectangle getGlyphsPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2) {
/* 1199 */     initPositions();
/*      */     
/* 1201 */     AffineTransform localAffineTransform = null;
/* 1202 */     if ((paramFontRenderContext == null) || (paramFontRenderContext.equals(this.frc))) {
/* 1203 */       localAffineTransform = this.frctx;
/*      */     } else {
/* 1205 */       localAffineTransform = paramFontRenderContext.getTransform();
/*      */     }
/* 1207 */     setDTX(localAffineTransform);
/*      */     
/* 1209 */     if (this.gti != null) {
/* 1210 */       return this.gti.getGlyphsPixelBounds(localAffineTransform, paramFloat1, paramFloat2, paramInt1, paramInt2);
/*      */     }
/*      */     
/* 1213 */     FontStrike localFontStrike = getDefaultStrike().strike;
/* 1214 */     Rectangle localRectangle1 = null;
/* 1215 */     Rectangle localRectangle2 = new Rectangle();
/* 1216 */     Point2D.Float localFloat = new Point2D.Float();
/* 1217 */     int i = paramInt1 * 2;
/* 1218 */     for (;;) { paramInt2--; if (paramInt2 < 0) break;
/* 1219 */       localFloat.x = (paramFloat1 + this.positions[(i++)]);
/* 1220 */       localFloat.y = (paramFloat2 + this.positions[(i++)]);
/* 1221 */       localAffineTransform.transform(localFloat, localFloat);
/* 1222 */       localFontStrike.getGlyphImageBounds(this.glyphs[(paramInt1++)], localFloat, localRectangle2);
/* 1223 */       if (!localRectangle2.isEmpty()) {
/* 1224 */         if (localRectangle1 == null) {
/* 1225 */           localRectangle1 = new Rectangle(localRectangle2);
/*      */         } else {
/* 1227 */           localRectangle1.add(localRectangle2);
/*      */         }
/*      */       }
/*      */     }
/* 1231 */     return localRectangle1 != null ? localRectangle1 : localRectangle2;
/*      */   }
/*      */   
/*      */   private void clearCaches(int paramInt) { Shape[] arrayOfShape;
/* 1235 */     if (this.lbcacheRef != null) {
/* 1236 */       arrayOfShape = (Shape[])this.lbcacheRef.get();
/* 1237 */       if (arrayOfShape != null) {
/* 1238 */         arrayOfShape[paramInt] = null;
/*      */       }
/*      */     }
/*      */     
/* 1242 */     if (this.vbcacheRef != null) {
/* 1243 */       arrayOfShape = (Shape[])this.vbcacheRef.get();
/* 1244 */       if (arrayOfShape != null) {
/* 1245 */         arrayOfShape[paramInt] = null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void clearCaches() {
/* 1251 */     this.lbcacheRef = null;
/* 1252 */     this.vbcacheRef = null;
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
/*      */   private void initPositions()
/*      */   {
/* 1299 */     if (this.positions == null) {
/* 1300 */       setFRCTX();
/*      */       
/* 1302 */       this.positions = new float[this.glyphs.length * 2 + 2];
/*      */       
/* 1304 */       Point2D.Float localFloat1 = null;
/* 1305 */       float f = getTracking(this.font);
/* 1306 */       if (f != 0.0F) {
/* 1307 */         f *= this.font.getSize2D();
/* 1308 */         localFloat1 = new Point2D.Float(f, 0.0F);
/*      */       }
/*      */       
/* 1311 */       Point2D.Float localFloat2 = new Point2D.Float(0.0F, 0.0F);
/* 1312 */       if (this.font.isTransformed()) {
/* 1313 */         AffineTransform localAffineTransform = this.font.getTransform();
/* 1314 */         localAffineTransform.transform(localFloat2, localFloat2);
/* 1315 */         this.positions[0] = localFloat2.x;
/* 1316 */         this.positions[1] = localFloat2.y;
/*      */         
/* 1318 */         if (localFloat1 != null) {
/* 1319 */           localAffineTransform.deltaTransform(localFloat1, localFloat1);
/*      */         }
/*      */       }
/* 1322 */       int i = 0; for (int j = 2; i < this.glyphs.length; j += 2) {
/* 1323 */         getGlyphStrike(i).addDefaultGlyphAdvance(this.glyphs[i], localFloat2);
/* 1324 */         if (localFloat1 != null) {
/* 1325 */           localFloat2.x += localFloat1.x;
/* 1326 */           localFloat2.y += localFloat1.y;
/*      */         }
/* 1328 */         this.positions[j] = localFloat2.x;
/* 1329 */         this.positions[(j + 1)] = localFloat2.y;i++;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addFlags(int paramInt)
/*      */   {
/* 1338 */     this.flags = (getLayoutFlags() | paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void clearFlags(int paramInt)
/*      */   {
/* 1345 */     this.flags = (getLayoutFlags() & (paramInt ^ 0xFFFFFFFF));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private GlyphStrike getGlyphStrike(int paramInt)
/*      */   {
/* 1352 */     if (this.gti == null) {
/* 1353 */       return getDefaultStrike();
/*      */     }
/* 1355 */     return this.gti.getStrike(paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */   private GlyphStrike getDefaultStrike()
/*      */   {
/* 1361 */     GlyphStrike localGlyphStrike = null;
/* 1362 */     if (this.fsref != null) {
/* 1363 */       localGlyphStrike = (GlyphStrike)this.fsref.get();
/*      */     }
/* 1365 */     if (localGlyphStrike == null) {
/* 1366 */       localGlyphStrike = GlyphStrike.create(this, this.dtx, null);
/* 1367 */       this.fsref = new SoftReference(localGlyphStrike);
/*      */     }
/* 1369 */     return localGlyphStrike;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static final class GlyphTransformInfo
/*      */   {
/*      */     StandardGlyphVector sgv;
/*      */     
/*      */ 
/*      */     int[] indices;
/*      */     
/*      */     double[] transforms;
/*      */     
/*      */     SoftReference strikesRef;
/*      */     
/*      */     boolean haveAllStrikes;
/*      */     
/*      */ 
/*      */     GlyphTransformInfo(StandardGlyphVector paramStandardGlyphVector)
/*      */     {
/* 1390 */       this.sgv = paramStandardGlyphVector;
/*      */     }
/*      */     
/*      */     GlyphTransformInfo(StandardGlyphVector paramStandardGlyphVector, GlyphTransformInfo paramGlyphTransformInfo)
/*      */     {
/* 1395 */       this.sgv = paramStandardGlyphVector;
/*      */       
/* 1397 */       this.indices = (paramGlyphTransformInfo.indices == null ? null : (int[])paramGlyphTransformInfo.indices.clone());
/* 1398 */       this.transforms = (paramGlyphTransformInfo.transforms == null ? null : (double[])paramGlyphTransformInfo.transforms.clone());
/* 1399 */       this.strikesRef = null;
/*      */     }
/*      */     
/*      */     public boolean equals(GlyphTransformInfo paramGlyphTransformInfo)
/*      */     {
/* 1404 */       if (paramGlyphTransformInfo == null) {
/* 1405 */         return false;
/*      */       }
/* 1407 */       if (paramGlyphTransformInfo == this) {
/* 1408 */         return true;
/*      */       }
/* 1410 */       if (this.indices.length != paramGlyphTransformInfo.indices.length) {
/* 1411 */         return false;
/*      */       }
/* 1413 */       if (this.transforms.length != paramGlyphTransformInfo.transforms.length) {
/* 1414 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1421 */       for (int i = 0; i < this.indices.length; i++) {
/* 1422 */         int j = this.indices[i];
/* 1423 */         int k = paramGlyphTransformInfo.indices[i];
/* 1424 */         if ((j == 0 ? 1 : 0) != (k == 0 ? 1 : 0)) {
/* 1425 */           return false;
/*      */         }
/* 1427 */         if (j != 0) {
/* 1428 */           j *= 6;
/* 1429 */           k *= 6;
/* 1430 */           for (int m = 6; m > 0; m--) {
/* 1431 */             if (this.indices[(--j)] != paramGlyphTransformInfo.indices[(--k)]) {
/* 1432 */               return false;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1437 */       return true;
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
/*      */     void setGlyphTransform(int paramInt, AffineTransform paramAffineTransform)
/*      */     {
/* 1453 */       double[] arrayOfDouble1 = new double[6];
/* 1454 */       int i = 1;
/* 1455 */       if ((paramAffineTransform == null) || (paramAffineTransform.isIdentity()))
/*      */       {
/* 1457 */         arrayOfDouble1[0] = (arrayOfDouble1[3] = 1.0D);
/*      */       }
/*      */       else {
/* 1460 */         i = 0;
/* 1461 */         paramAffineTransform.getMatrix(arrayOfDouble1);
/*      */       }
/*      */       
/* 1464 */       if (this.indices == null) {
/* 1465 */         if (i != 0) {
/* 1466 */           return;
/*      */         }
/*      */         
/* 1469 */         this.indices = new int[this.sgv.glyphs.length];
/* 1470 */         this.indices[paramInt] = 1;
/* 1471 */         this.transforms = arrayOfDouble1;
/*      */       } else {
/* 1473 */         int j = 0;
/* 1474 */         int k = -1;
/* 1475 */         int n; if (i != 0) {
/* 1476 */           k = 0;
/*      */         } else {
/* 1478 */           j = 1;
/*      */           
/*      */           label156:
/* 1481 */           for (m = 0; m < this.transforms.length; m += 6) {
/* 1482 */             for (n = 0; n < 6; n++) {
/* 1483 */               if (this.transforms[(m + n)] != arrayOfDouble1[n]) {
/*      */                 break label156;
/*      */               }
/*      */             }
/* 1487 */             j = 0;
/* 1488 */             break;
/*      */           }
/* 1490 */           k = m / 6 + 1;
/*      */         }
/*      */         
/*      */ 
/* 1494 */         int m = this.indices[paramInt];
/* 1495 */         if (k != m)
/*      */         {
/* 1497 */           n = 0;
/* 1498 */           if (m != 0) {
/* 1499 */             n = 1;
/* 1500 */             for (int i1 = 0; i1 < this.indices.length; i1++) {
/* 1501 */               if ((this.indices[i1] == m) && (i1 != paramInt)) {
/* 1502 */                 n = 0;
/* 1503 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1508 */           if ((n != 0) && (j != 0)) {
/* 1509 */             k = m;
/* 1510 */             System.arraycopy(arrayOfDouble1, 0, this.transforms, (k - 1) * 6, 6); } else { double[] arrayOfDouble2;
/* 1511 */             if (n != 0) {
/* 1512 */               if (this.transforms.length == 6) {
/* 1513 */                 this.indices = null;
/* 1514 */                 this.transforms = null;
/*      */                 
/* 1516 */                 this.sgv.clearCaches(paramInt);
/* 1517 */                 this.sgv.clearFlags(1);
/* 1518 */                 this.strikesRef = null;
/*      */                 
/* 1520 */                 return;
/*      */               }
/*      */               
/* 1523 */               arrayOfDouble2 = new double[this.transforms.length - 6];
/* 1524 */               System.arraycopy(this.transforms, 0, arrayOfDouble2, 0, (m - 1) * 6);
/* 1525 */               System.arraycopy(this.transforms, m * 6, arrayOfDouble2, (m - 1) * 6, this.transforms.length - m * 6);
/*      */               
/* 1527 */               this.transforms = arrayOfDouble2;
/*      */               
/*      */ 
/* 1530 */               for (int i2 = 0; i2 < this.indices.length; i2++) {
/* 1531 */                 if (this.indices[i2] > m) {
/* 1532 */                   this.indices[i2] -= 1;
/*      */                 }
/*      */               }
/* 1535 */               if (k > m) {
/* 1536 */                 k--;
/*      */               }
/* 1538 */             } else if (j != 0) {
/* 1539 */               arrayOfDouble2 = new double[this.transforms.length + 6];
/* 1540 */               System.arraycopy(this.transforms, 0, arrayOfDouble2, 0, this.transforms.length);
/* 1541 */               System.arraycopy(arrayOfDouble1, 0, arrayOfDouble2, this.transforms.length, 6);
/* 1542 */               this.transforms = arrayOfDouble2;
/*      */             }
/*      */           }
/* 1545 */           this.indices[paramInt] = k;
/*      */         }
/*      */       }
/*      */       
/* 1549 */       this.sgv.clearCaches(paramInt);
/* 1550 */       this.sgv.addFlags(1);
/* 1551 */       this.strikesRef = null;
/*      */     }
/*      */     
/*      */     AffineTransform getGlyphTransform(int paramInt)
/*      */     {
/* 1556 */       int i = this.indices[paramInt];
/* 1557 */       if (i == 0) {
/* 1558 */         return null;
/*      */       }
/*      */       
/* 1561 */       int j = (i - 1) * 6;
/* 1562 */       return new AffineTransform(this.transforms[(j + 0)], this.transforms[(j + 1)], this.transforms[(j + 2)], this.transforms[(j + 3)], this.transforms[(j + 4)], this.transforms[(j + 5)]);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     int transformCount()
/*      */     {
/* 1571 */       if (this.transforms == null) {
/* 1572 */         return 0;
/*      */       }
/* 1574 */       return this.transforms.length / 6;
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
/*      */     Object setupGlyphImages(long[] paramArrayOfLong, float[] paramArrayOfFloat, AffineTransform paramAffineTransform)
/*      */     {
/* 1595 */       int i = this.sgv.glyphs.length;
/*      */       
/* 1597 */       GlyphStrike[] arrayOfGlyphStrike = getAllStrikes();
/* 1598 */       for (int j = 0; j < i; j++) {
/* 1599 */         GlyphStrike localGlyphStrike = arrayOfGlyphStrike[this.indices[j]];
/* 1600 */         int k = this.sgv.glyphs[j];
/* 1601 */         paramArrayOfLong[j] = localGlyphStrike.strike.getGlyphImagePtr(k);
/*      */         
/* 1603 */         localGlyphStrike.getGlyphPosition(k, j * 2, this.sgv.positions, paramArrayOfFloat);
/*      */       }
/* 1605 */       paramAffineTransform.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, i);
/*      */       
/* 1607 */       return arrayOfGlyphStrike;
/*      */     }
/*      */     
/*      */     Rectangle getGlyphsPixelBounds(AffineTransform paramAffineTransform, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2) {
/* 1611 */       Rectangle localRectangle1 = null;
/* 1612 */       Rectangle localRectangle2 = new Rectangle();
/* 1613 */       Point2D.Float localFloat = new Point2D.Float();
/* 1614 */       int i = paramInt1 * 2;
/* 1615 */       for (;;) { paramInt2--; if (paramInt2 < 0) break;
/* 1616 */         GlyphStrike localGlyphStrike = getStrike(paramInt1);
/* 1617 */         localFloat.x = (paramFloat1 + this.sgv.positions[(i++)] + localGlyphStrike.dx);
/* 1618 */         localFloat.y = (paramFloat2 + this.sgv.positions[(i++)] + localGlyphStrike.dy);
/* 1619 */         paramAffineTransform.transform(localFloat, localFloat);
/* 1620 */         localGlyphStrike.strike.getGlyphImageBounds(this.sgv.glyphs[(paramInt1++)], localFloat, localRectangle2);
/* 1621 */         if (!localRectangle2.isEmpty()) {
/* 1622 */           if (localRectangle1 == null) {
/* 1623 */             localRectangle1 = new Rectangle(localRectangle2);
/*      */           } else {
/* 1625 */             localRectangle1.add(localRectangle2);
/*      */           }
/*      */         }
/*      */       }
/* 1629 */       return localRectangle1 != null ? localRectangle1 : localRectangle2;
/*      */     }
/*      */     
/*      */     GlyphStrike getStrike(int paramInt) {
/* 1633 */       if (this.indices != null) {
/* 1634 */         GlyphStrike[] arrayOfGlyphStrike = getStrikeArray();
/* 1635 */         return getStrikeAtIndex(arrayOfGlyphStrike, this.indices[paramInt]);
/*      */       }
/* 1637 */       return this.sgv.getDefaultStrike();
/*      */     }
/*      */     
/*      */     private GlyphStrike[] getAllStrikes() {
/* 1641 */       if (this.indices == null) {
/* 1642 */         return null;
/*      */       }
/*      */       
/* 1645 */       GlyphStrike[] arrayOfGlyphStrike = getStrikeArray();
/* 1646 */       if (!this.haveAllStrikes) {
/* 1647 */         for (int i = 0; i < arrayOfGlyphStrike.length; i++) {
/* 1648 */           getStrikeAtIndex(arrayOfGlyphStrike, i);
/*      */         }
/* 1650 */         this.haveAllStrikes = true;
/*      */       }
/*      */       
/* 1653 */       return arrayOfGlyphStrike;
/*      */     }
/*      */     
/*      */     private GlyphStrike[] getStrikeArray() {
/* 1657 */       GlyphStrike[] arrayOfGlyphStrike = null;
/* 1658 */       if (this.strikesRef != null) {
/* 1659 */         arrayOfGlyphStrike = (GlyphStrike[])this.strikesRef.get();
/*      */       }
/* 1661 */       if (arrayOfGlyphStrike == null) {
/* 1662 */         this.haveAllStrikes = false;
/* 1663 */         arrayOfGlyphStrike = new GlyphStrike[transformCount() + 1];
/* 1664 */         this.strikesRef = new SoftReference(arrayOfGlyphStrike);
/*      */       }
/*      */       
/* 1667 */       return arrayOfGlyphStrike;
/*      */     }
/*      */     
/*      */     private GlyphStrike getStrikeAtIndex(GlyphStrike[] paramArrayOfGlyphStrike, int paramInt) {
/* 1671 */       GlyphStrike localGlyphStrike = paramArrayOfGlyphStrike[paramInt];
/* 1672 */       if (localGlyphStrike == null) {
/* 1673 */         if (paramInt == 0) {
/* 1674 */           localGlyphStrike = this.sgv.getDefaultStrike();
/*      */         } else {
/* 1676 */           int i = (paramInt - 1) * 6;
/* 1677 */           AffineTransform localAffineTransform = new AffineTransform(this.transforms[i], this.transforms[(i + 1)], this.transforms[(i + 2)], this.transforms[(i + 3)], this.transforms[(i + 4)], this.transforms[(i + 5)]);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1684 */           localGlyphStrike = GlyphStrike.create(this.sgv, this.sgv.dtx, localAffineTransform);
/*      */         }
/* 1686 */         paramArrayOfGlyphStrike[paramInt] = localGlyphStrike;
/*      */       }
/* 1688 */       return localGlyphStrike;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static final class GlyphStrike
/*      */   {
/*      */     StandardGlyphVector sgv;
/*      */     
/*      */     FontStrike strike;
/*      */     
/*      */     float dx;
/*      */     float dy;
/*      */     
/*      */     static GlyphStrike create(StandardGlyphVector paramStandardGlyphVector, AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2)
/*      */     {
/* 1704 */       float f1 = 0.0F;
/* 1705 */       float f2 = 0.0F;
/*      */       
/* 1707 */       AffineTransform localAffineTransform = paramStandardGlyphVector.ftx;
/* 1708 */       if ((!paramAffineTransform1.isIdentity()) || (paramAffineTransform2 != null)) {
/* 1709 */         localAffineTransform = new AffineTransform(paramStandardGlyphVector.ftx);
/* 1710 */         if (paramAffineTransform2 != null) {
/* 1711 */           localAffineTransform.preConcatenate(paramAffineTransform2);
/* 1712 */           f1 = (float)localAffineTransform.getTranslateX();
/* 1713 */           f2 = (float)localAffineTransform.getTranslateY();
/*      */         }
/* 1715 */         if (!paramAffineTransform1.isIdentity()) {
/* 1716 */           localAffineTransform.preConcatenate(paramAffineTransform1);
/*      */         }
/*      */       }
/*      */       
/* 1720 */       int i = 1;
/* 1721 */       Object localObject1 = paramStandardGlyphVector.frc.getAntiAliasingHint();
/* 1722 */       if (localObject1 == RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1727 */         if ((!localAffineTransform.isIdentity()) && 
/* 1728 */           ((localAffineTransform.getType() & 0xFFFFFFFE) != 0)) {
/* 1729 */           double d1 = localAffineTransform.getShearX();
/* 1730 */           if (d1 != 0.0D) {
/* 1731 */             double d2 = localAffineTransform.getScaleY();
/*      */             
/* 1733 */             i = (int)Math.sqrt(d1 * d1 + d2 * d2);
/*      */           } else {
/* 1735 */             i = (int)Math.abs(localAffineTransform.getScaleY());
/*      */           }
/*      */         }
/*      */       }
/* 1739 */       int j = FontStrikeDesc.getAAHintIntVal(localObject1, paramStandardGlyphVector.font2D, i);
/*      */       
/* 1741 */       int k = FontStrikeDesc.getFMHintIntVal(paramStandardGlyphVector.frc.getFractionalMetricsHint());
/*      */       
/*      */ 
/* 1744 */       FontStrikeDesc localFontStrikeDesc = new FontStrikeDesc(paramAffineTransform1, localAffineTransform, paramStandardGlyphVector.font.getStyle(), j, k);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1749 */       Object localObject2 = paramStandardGlyphVector.font2D;
/* 1750 */       if ((localObject2 instanceof FontSubstitution)) {
/* 1751 */         localObject2 = ((FontSubstitution)localObject2).getCompositeFont2D();
/*      */       }
/* 1753 */       FontStrike localFontStrike = ((Font2D)localObject2).handle.font2D.getStrike(localFontStrikeDesc);
/*      */       
/* 1755 */       return new GlyphStrike(paramStandardGlyphVector, localFontStrike, f1, f2);
/*      */     }
/*      */     
/*      */     private GlyphStrike(StandardGlyphVector paramStandardGlyphVector, FontStrike paramFontStrike, float paramFloat1, float paramFloat2) {
/* 1759 */       this.sgv = paramStandardGlyphVector;
/* 1760 */       this.strike = paramFontStrike;
/* 1761 */       this.dx = paramFloat1;
/* 1762 */       this.dy = paramFloat2;
/*      */     }
/*      */     
/*      */     void getADL(ADL paramADL) {
/* 1766 */       StrikeMetrics localStrikeMetrics = this.strike.getFontMetrics();
/* 1767 */       Point2D.Float localFloat = null;
/* 1768 */       if (this.sgv.font.isTransformed()) {
/* 1769 */         localFloat = new Point2D.Float();
/* 1770 */         localFloat.x = ((float)this.sgv.font.getTransform().getTranslateX());
/* 1771 */         localFloat.y = ((float)this.sgv.font.getTransform().getTranslateY());
/*      */       }
/*      */       
/* 1774 */       paramADL.ascentX = (-localStrikeMetrics.ascentX);
/* 1775 */       paramADL.ascentY = (-localStrikeMetrics.ascentY);
/* 1776 */       paramADL.descentX = localStrikeMetrics.descentX;
/* 1777 */       paramADL.descentY = localStrikeMetrics.descentY;
/* 1778 */       paramADL.leadingX = localStrikeMetrics.leadingX;
/* 1779 */       paramADL.leadingY = localStrikeMetrics.leadingY;
/*      */     }
/*      */     
/*      */     void getGlyphPosition(int paramInt1, int paramInt2, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
/* 1783 */       paramArrayOfFloat1[paramInt2] += this.dx;
/* 1784 */       paramInt2++;
/* 1785 */       paramArrayOfFloat1[paramInt2] += this.dy;
/*      */     }
/*      */     
/*      */ 
/*      */     void addDefaultGlyphAdvance(int paramInt, Point2D.Float paramFloat)
/*      */     {
/* 1791 */       Point2D.Float localFloat = this.strike.getGlyphMetrics(paramInt);
/* 1792 */       paramFloat.x += localFloat.x + this.dx;
/* 1793 */       paramFloat.y += localFloat.y + this.dy;
/*      */     }
/*      */     
/*      */     Rectangle2D getGlyphOutlineBounds(int paramInt, float paramFloat1, float paramFloat2) {
/* 1797 */       Object localObject = null;
/* 1798 */       if (this.sgv.invdtx == null) {
/* 1799 */         localObject = new Rectangle2D.Float();
/* 1800 */         ((Rectangle2D)localObject).setRect(this.strike.getGlyphOutlineBounds(paramInt));
/*      */       } else {
/* 1802 */         GeneralPath localGeneralPath = this.strike.getGlyphOutline(paramInt, 0.0F, 0.0F);
/* 1803 */         localGeneralPath.transform(this.sgv.invdtx);
/* 1804 */         localObject = localGeneralPath.getBounds2D();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1814 */       if (!((Rectangle2D)localObject).isEmpty()) {
/* 1815 */         ((Rectangle2D)localObject).setRect(((Rectangle2D)localObject).getMinX() + paramFloat1 + this.dx, ((Rectangle2D)localObject)
/* 1816 */           .getMinY() + paramFloat2 + this.dy, ((Rectangle2D)localObject)
/* 1817 */           .getWidth(), ((Rectangle2D)localObject).getHeight());
/*      */       }
/* 1819 */       return (Rectangle2D)localObject;
/*      */     }
/*      */     
/*      */     void appendGlyphOutline(int paramInt, GeneralPath paramGeneralPath, float paramFloat1, float paramFloat2)
/*      */     {
/* 1824 */       GeneralPath localGeneralPath = null;
/* 1825 */       if (this.sgv.invdtx == null) {
/* 1826 */         localGeneralPath = this.strike.getGlyphOutline(paramInt, paramFloat1 + this.dx, paramFloat2 + this.dy);
/*      */       } else {
/* 1828 */         localGeneralPath = this.strike.getGlyphOutline(paramInt, 0.0F, 0.0F);
/* 1829 */         localGeneralPath.transform(this.sgv.invdtx);
/* 1830 */         localGeneralPath.transform(AffineTransform.getTranslateInstance(paramFloat1 + this.dx, paramFloat2 + this.dy));
/*      */       }
/* 1832 */       PathIterator localPathIterator = localGeneralPath.getPathIterator(null);
/* 1833 */       paramGeneralPath.append(localPathIterator, false);
/*      */     }
/*      */   }
/*      */   
/*      */   public String toString() {
/* 1838 */     return appendString(null).toString();
/*      */   }
/*      */   
/*      */   StringBuffer appendString(StringBuffer paramStringBuffer) {
/* 1842 */     if (paramStringBuffer == null) {
/* 1843 */       paramStringBuffer = new StringBuffer();
/*      */     }
/*      */     try {
/* 1846 */       paramStringBuffer.append("SGV{font: ");
/* 1847 */       paramStringBuffer.append(this.font.toString());
/* 1848 */       paramStringBuffer.append(", frc: ");
/* 1849 */       paramStringBuffer.append(this.frc.toString());
/* 1850 */       paramStringBuffer.append(", glyphs: (");
/* 1851 */       paramStringBuffer.append(this.glyphs.length);
/* 1852 */       paramStringBuffer.append(")[");
/* 1853 */       for (int i = 0; i < this.glyphs.length; i++) {
/* 1854 */         if (i > 0) {
/* 1855 */           paramStringBuffer.append(", ");
/*      */         }
/* 1857 */         paramStringBuffer.append(Integer.toHexString(this.glyphs[i]));
/*      */       }
/* 1859 */       paramStringBuffer.append("]");
/* 1860 */       if (this.positions != null) {
/* 1861 */         paramStringBuffer.append(", positions: (");
/* 1862 */         paramStringBuffer.append(this.positions.length);
/* 1863 */         paramStringBuffer.append(")[");
/* 1864 */         for (i = 0; i < this.positions.length; i += 2) {
/* 1865 */           if (i > 0) {
/* 1866 */             paramStringBuffer.append(", ");
/*      */           }
/* 1868 */           paramStringBuffer.append(this.positions[i]);
/* 1869 */           paramStringBuffer.append("@");
/* 1870 */           paramStringBuffer.append(this.positions[(i + 1)]);
/*      */         }
/* 1872 */         paramStringBuffer.append("]");
/*      */       }
/* 1874 */       if (this.charIndices != null) {
/* 1875 */         paramStringBuffer.append(", indices: (");
/* 1876 */         paramStringBuffer.append(this.charIndices.length);
/* 1877 */         paramStringBuffer.append(")[");
/* 1878 */         for (i = 0; i < this.charIndices.length; i++) {
/* 1879 */           if (i > 0) {
/* 1880 */             paramStringBuffer.append(", ");
/*      */           }
/* 1882 */           paramStringBuffer.append(this.charIndices[i]);
/*      */         }
/* 1884 */         paramStringBuffer.append("]");
/*      */       }
/* 1886 */       paramStringBuffer.append(", flags:");
/* 1887 */       if (getLayoutFlags() == 0) {
/* 1888 */         paramStringBuffer.append(" default");
/*      */       } else {
/* 1890 */         if ((this.flags & 0x1) != 0) {
/* 1891 */           paramStringBuffer.append(" tx");
/*      */         }
/* 1893 */         if ((this.flags & 0x2) != 0) {
/* 1894 */           paramStringBuffer.append(" pos");
/*      */         }
/* 1896 */         if ((this.flags & 0x4) != 0) {
/* 1897 */           paramStringBuffer.append(" rtl");
/*      */         }
/* 1899 */         if ((this.flags & 0x8) != 0) {
/* 1900 */           paramStringBuffer.append(" complex");
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception localException) {
/* 1905 */       paramStringBuffer.append(" " + localException.getMessage());
/*      */     }
/* 1907 */     paramStringBuffer.append("}");
/*      */     
/* 1909 */     return paramStringBuffer;
/*      */   }
/*      */   
/*      */   static class ADL {
/*      */     public float ascentX;
/*      */     public float ascentY;
/*      */     public float descentX;
/*      */     public float descentY;
/*      */     public float leadingX;
/*      */     public float leadingY;
/*      */     
/*      */     public String toString() {
/* 1921 */       return toStringBuffer(null).toString();
/*      */     }
/*      */     
/*      */     protected StringBuffer toStringBuffer(StringBuffer paramStringBuffer) {
/* 1925 */       if (paramStringBuffer == null) {
/* 1926 */         paramStringBuffer = new StringBuffer();
/*      */       }
/* 1928 */       paramStringBuffer.append("ax: ");
/* 1929 */       paramStringBuffer.append(this.ascentX);
/* 1930 */       paramStringBuffer.append(" ay: ");
/* 1931 */       paramStringBuffer.append(this.ascentY);
/* 1932 */       paramStringBuffer.append(" dx: ");
/* 1933 */       paramStringBuffer.append(this.descentX);
/* 1934 */       paramStringBuffer.append(" dy: ");
/* 1935 */       paramStringBuffer.append(this.descentY);
/* 1936 */       paramStringBuffer.append(" lx: ");
/* 1937 */       paramStringBuffer.append(this.leadingX);
/* 1938 */       paramStringBuffer.append(" ly: ");
/* 1939 */       paramStringBuffer.append(this.leadingY);
/*      */       
/* 1941 */       return paramStringBuffer;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\font\StandardGlyphVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
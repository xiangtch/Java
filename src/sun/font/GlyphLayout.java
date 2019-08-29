/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.NoninvertibleTransformException;
/*     */ import java.awt.geom.Point2D.Float;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class GlyphLayout
/*     */ {
/*     */   private GVData _gvdata;
/*     */   private static volatile GlyphLayout cache;
/*     */   private LayoutEngineFactory _lef;
/*     */   private TextRecord _textRecord;
/*     */   private ScriptRun _scriptRuns;
/*     */   private FontRunIterator _fontRuns;
/*     */   private int _ercount;
/*     */   private ArrayList _erecords;
/*     */   private Point2D.Float _pt;
/*     */   private FontStrikeDesc _sd;
/*     */   private float[] _mat;
/*     */   private int _typo_flags;
/*     */   private int _offset;
/*     */   
/*     */   public static final class LayoutEngineKey
/*     */   {
/*     */     private Font2D font;
/*     */     private int script;
/*     */     private int lang;
/*     */     
/*     */     LayoutEngineKey() {}
/*     */     
/*     */     LayoutEngineKey(Font2D paramFont2D, int paramInt1, int paramInt2)
/*     */     {
/* 111 */       init(paramFont2D, paramInt1, paramInt2);
/*     */     }
/*     */     
/*     */     void init(Font2D paramFont2D, int paramInt1, int paramInt2) {
/* 115 */       this.font = paramFont2D;
/* 116 */       this.script = paramInt1;
/* 117 */       this.lang = paramInt2;
/*     */     }
/*     */     
/*     */     LayoutEngineKey copy() {
/* 121 */       return new LayoutEngineKey(this.font, this.script, this.lang);
/*     */     }
/*     */     
/*     */     Font2D font() {
/* 125 */       return this.font;
/*     */     }
/*     */     
/*     */     int script() {
/* 129 */       return this.script;
/*     */     }
/*     */     
/*     */     int lang() {
/* 133 */       return this.lang;
/*     */     }
/*     */     
/*     */     public boolean equals(Object paramObject) {
/* 137 */       if (this == paramObject) return true;
/* 138 */       if (paramObject == null) return false;
/*     */       try {
/* 140 */         LayoutEngineKey localLayoutEngineKey = (LayoutEngineKey)paramObject;
/* 141 */         if ((this.script == localLayoutEngineKey.script) && (this.lang == localLayoutEngineKey.lang)) {} return 
/*     */         
/* 143 */           this.font.equals(localLayoutEngineKey.font);
/*     */       }
/*     */       catch (ClassCastException localClassCastException) {}
/* 146 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 151 */       return this.script ^ this.lang ^ this.font.hashCode();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static GlyphLayout get(LayoutEngineFactory paramLayoutEngineFactory)
/*     */   {
/* 184 */     if (paramLayoutEngineFactory == null) {
/* 185 */       paramLayoutEngineFactory = SunLayoutEngine.instance();
/*     */     }
/* 187 */     GlyphLayout localGlyphLayout = null;
/* 188 */     synchronized (GlyphLayout.class) {
/* 189 */       if (cache != null) {
/* 190 */         localGlyphLayout = cache;
/* 191 */         cache = null;
/*     */       }
/*     */     }
/* 194 */     if (localGlyphLayout == null) {
/* 195 */       localGlyphLayout = new GlyphLayout();
/*     */     }
/* 197 */     localGlyphLayout._lef = paramLayoutEngineFactory;
/* 198 */     return localGlyphLayout;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void done(GlyphLayout paramGlyphLayout)
/*     */   {
/* 206 */     paramGlyphLayout._lef = null;
/* 207 */     cache = paramGlyphLayout;
/*     */   }
/*     */   
/*     */   private static final class SDCache
/*     */   {
/*     */     public Font key_font;
/*     */     public FontRenderContext key_frc;
/*     */     public AffineTransform dtx;
/*     */     public AffineTransform invdtx;
/*     */     public AffineTransform gtx;
/*     */     public Point2D.Float delta;
/*     */     public FontStrikeDesc sd;
/*     */     
/*     */     private SDCache(Font paramFont, FontRenderContext paramFontRenderContext) {
/* 221 */       this.key_font = paramFont;
/* 222 */       this.key_frc = paramFontRenderContext;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 227 */       this.dtx = paramFontRenderContext.getTransform();
/* 228 */       this.dtx.setTransform(this.dtx.getScaleX(), this.dtx.getShearY(), this.dtx
/* 229 */         .getShearX(), this.dtx.getScaleY(), 0.0D, 0.0D);
/*     */       
/* 231 */       if (!this.dtx.isIdentity()) {
/*     */         try {
/* 233 */           this.invdtx = this.dtx.createInverse();
/*     */         }
/*     */         catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 236 */           throw new InternalError(localNoninvertibleTransformException);
/*     */         }
/*     */       }
/*     */       
/* 240 */       float f = paramFont.getSize2D();
/* 241 */       if (paramFont.isTransformed()) {
/* 242 */         this.gtx = paramFont.getTransform();
/* 243 */         this.gtx.scale(f, f);
/*     */         
/* 245 */         this.delta = new Point2D.Float((float)this.gtx.getTranslateX(), (float)this.gtx.getTranslateY());
/* 246 */         this.gtx.setTransform(this.gtx.getScaleX(), this.gtx.getShearY(), this.gtx
/* 247 */           .getShearX(), this.gtx.getScaleY(), 0.0D, 0.0D);
/*     */         
/* 249 */         this.gtx.preConcatenate(this.dtx);
/*     */       } else {
/* 251 */         this.delta = ZERO_DELTA;
/* 252 */         this.gtx = new AffineTransform(this.dtx);
/* 253 */         this.gtx.scale(f, f);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 261 */       int i = FontStrikeDesc.getAAHintIntVal(paramFontRenderContext.getAntiAliasingHint(), 
/* 262 */         FontUtilities.getFont2D(paramFont), 
/* 263 */         (int)Math.abs(f));
/*     */       
/* 265 */       int j = FontStrikeDesc.getFMHintIntVal(paramFontRenderContext.getFractionalMetricsHint());
/* 266 */       this.sd = new FontStrikeDesc(this.dtx, this.gtx, paramFont.getStyle(), i, j);
/*     */     }
/*     */     
/* 269 */     private static final Point2D.Float ZERO_DELTA = new Point2D.Float();
/*     */     private static SoftReference<ConcurrentHashMap<SDKey, SDCache>> cacheRef;
/*     */     
/*     */     private static final class SDKey
/*     */     {
/*     */       private final Font font;
/*     */       private final FontRenderContext frc;
/*     */       private final int hash;
/*     */       
/*     */       SDKey(Font paramFont, FontRenderContext paramFontRenderContext)
/*     */       {
/* 280 */         this.font = paramFont;
/* 281 */         this.frc = paramFontRenderContext;
/* 282 */         this.hash = (paramFont.hashCode() ^ paramFontRenderContext.hashCode());
/*     */       }
/*     */       
/*     */       public int hashCode() {
/* 286 */         return this.hash;
/*     */       }
/*     */       
/*     */       public boolean equals(Object paramObject) {
/*     */         try {
/* 291 */           SDKey localSDKey = (SDKey)paramObject;
/* 292 */           if (this.hash == localSDKey.hash) {} return 
/*     */           
/* 294 */             (this.font.equals(localSDKey.font)) && 
/* 295 */             (this.frc.equals(localSDKey.frc));
/*     */         }
/*     */         catch (ClassCastException localClassCastException) {}
/*     */         
/* 299 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public static SDCache get(Font paramFont, FontRenderContext paramFontRenderContext)
/*     */     {
/* 310 */       if (paramFontRenderContext.isTransformed()) {
/* 311 */         localObject = paramFontRenderContext.getTransform();
/* 312 */         if ((((AffineTransform)localObject).getTranslateX() != 0.0D) || 
/* 313 */           (((AffineTransform)localObject).getTranslateY() != 0.0D))
/*     */         {
/*     */ 
/*     */ 
/* 317 */           localObject = new AffineTransform(((AffineTransform)localObject).getScaleX(), ((AffineTransform)localObject).getShearY(), ((AffineTransform)localObject).getShearX(), ((AffineTransform)localObject).getScaleY(), 0.0D, 0.0D);
/*     */           
/*     */ 
/*     */ 
/* 321 */           paramFontRenderContext = new FontRenderContext((AffineTransform)localObject, paramFontRenderContext.getAntiAliasingHint(), paramFontRenderContext.getFractionalMetricsHint());
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 326 */       Object localObject = new SDKey(paramFont, paramFontRenderContext);
/* 327 */       ConcurrentHashMap localConcurrentHashMap = null;
/* 328 */       SDCache localSDCache = null;
/* 329 */       if (cacheRef != null) {
/* 330 */         localConcurrentHashMap = (ConcurrentHashMap)cacheRef.get();
/* 331 */         if (localConcurrentHashMap != null) {
/* 332 */           localSDCache = (SDCache)localConcurrentHashMap.get(localObject);
/*     */         }
/*     */       }
/* 335 */       if (localSDCache == null) {
/* 336 */         localSDCache = new SDCache(paramFont, paramFontRenderContext);
/* 337 */         if (localConcurrentHashMap == null) {
/* 338 */           localConcurrentHashMap = new ConcurrentHashMap(10);
/* 339 */           cacheRef = new SoftReference(localConcurrentHashMap);
/*     */         }
/* 341 */         else if (localConcurrentHashMap.size() >= 512) {
/* 342 */           localConcurrentHashMap.clear();
/*     */         }
/* 344 */         localConcurrentHashMap.put(localObject, localSDCache);
/*     */       }
/* 346 */       return localSDCache;
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
/*     */ 
/*     */   public StandardGlyphVector layout(Font paramFont, FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, StandardGlyphVector paramStandardGlyphVector)
/*     */   {
/* 365 */     if ((paramArrayOfChar == null) || (paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfChar.length - paramInt1)) {
/* 366 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/* 369 */     init(paramInt2);
/*     */     
/*     */ 
/*     */ 
/* 373 */     if (paramFont.hasLayoutAttributes()) {
/* 374 */       localObject1 = ((AttributeMap)paramFont.getAttributes()).getValues();
/* 375 */       if (((AttributeValues)localObject1).getKerning() != 0) this._typo_flags |= 0x1;
/* 376 */       if (((AttributeValues)localObject1).getLigatures() != 0) { this._typo_flags |= 0x2;
/*     */       }
/*     */     }
/* 379 */     this._offset = paramInt1;
/*     */     
/*     */ 
/*     */ 
/* 383 */     Object localObject1 = SDCache.get(paramFont, paramFontRenderContext);
/* 384 */     this._mat[0] = ((float)((SDCache)localObject1).gtx.getScaleX());
/* 385 */     this._mat[1] = ((float)((SDCache)localObject1).gtx.getShearY());
/* 386 */     this._mat[2] = ((float)((SDCache)localObject1).gtx.getShearX());
/* 387 */     this._mat[3] = ((float)((SDCache)localObject1).gtx.getScaleY());
/* 388 */     this._pt.setLocation(((SDCache)localObject1).delta);
/*     */     
/* 390 */     int i = paramInt1 + paramInt2;
/*     */     
/* 392 */     int j = 0;
/* 393 */     int k = paramArrayOfChar.length;
/* 394 */     if (paramInt3 != 0) {
/* 395 */       if ((paramInt3 & 0x1) != 0) {
/* 396 */         this._typo_flags |= 0x80000000;
/*     */       }
/*     */       
/* 399 */       if ((paramInt3 & 0x2) != 0) {
/* 400 */         j = paramInt1;
/*     */       }
/*     */       
/* 403 */       if ((paramInt3 & 0x4) != 0) {
/* 404 */         k = i;
/*     */       }
/*     */     }
/*     */     
/* 408 */     int m = -1;
/*     */     
/* 410 */     Object localObject2 = FontUtilities.getFont2D(paramFont);
/* 411 */     if ((localObject2 instanceof FontSubstitution)) {
/* 412 */       localObject2 = ((FontSubstitution)localObject2).getCompositeFont2D();
/*     */     }
/*     */     
/* 415 */     this._textRecord.init(paramArrayOfChar, paramInt1, i, j, k);
/* 416 */     int n = paramInt1;
/* 417 */     if ((localObject2 instanceof CompositeFont)) {
/* 418 */       this._scriptRuns.init(paramArrayOfChar, paramInt1, paramInt2);
/* 419 */       this._fontRuns.init((CompositeFont)localObject2, paramArrayOfChar, paramInt1, i);
/* 420 */       while (this._scriptRuns.next()) {
/* 421 */         i1 = this._scriptRuns.getScriptLimit();
/* 422 */         i2 = this._scriptRuns.getScriptCode();
/* 423 */         while (this._fontRuns.next(i2, i1)) {
/* 424 */           PhysicalFont localPhysicalFont = this._fontRuns.getFont();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 431 */           if ((localPhysicalFont instanceof NativeFont)) {
/* 432 */             localPhysicalFont = ((NativeFont)localPhysicalFont).getDelegateFont();
/*     */           }
/* 434 */           int i4 = this._fontRuns.getGlyphMask();
/* 435 */           int i5 = this._fontRuns.getPos();
/* 436 */           nextEngineRecord(n, i5, i2, m, localPhysicalFont, i4);
/* 437 */           n = i5;
/*     */         }
/*     */       }
/*     */     }
/* 441 */     this._scriptRuns.init(paramArrayOfChar, paramInt1, paramInt2);
/* 442 */     while (this._scriptRuns.next()) {
/* 443 */       i1 = this._scriptRuns.getScriptLimit();
/* 444 */       i2 = this._scriptRuns.getScriptCode();
/* 445 */       nextEngineRecord(n, i1, i2, m, (Font2D)localObject2, 0);
/* 446 */       n = i1;
/*     */     }
/*     */     
/*     */ 
/* 450 */     int i1 = 0;
/* 451 */     int i2 = this._ercount;
/* 452 */     int i3 = 1;
/*     */     
/* 454 */     if (this._typo_flags < 0) {
/* 455 */       i1 = i2 - 1;
/* 456 */       i2 = -1;
/* 457 */       i3 = -1;
/*     */     }
/*     */     
/*     */ 
/* 461 */     this._sd = ((SDCache)localObject1).sd;
/* 462 */     Object localObject3; for (; i1 != i2; i1 += i3) {
/* 463 */       localObject3 = (EngineRecord)this._erecords.get(i1);
/*     */       for (;;) {
/*     */         try {
/* 466 */           ((EngineRecord)localObject3).layout();
/*     */         }
/*     */         catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
/*     */         {
/* 470 */           if (this._gvdata._count >= 0) {
/* 471 */             this._gvdata.grow();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 476 */       if (this._gvdata._count < 0) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 489 */     if (this._gvdata._count < 0) {
/* 490 */       localObject3 = new StandardGlyphVector(paramFont, paramArrayOfChar, paramInt1, paramInt2, paramFontRenderContext);
/* 491 */       if (FontUtilities.debugFonts()) {
/* 492 */         FontUtilities.getLogger().warning("OpenType layout failed on font: " + paramFont);
/*     */       }
/*     */     }
/*     */     else {
/* 496 */       localObject3 = this._gvdata.createGlyphVector(paramFont, paramFontRenderContext, paramStandardGlyphVector);
/*     */     }
/*     */     
/* 499 */     return (StandardGlyphVector)localObject3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private GlyphLayout()
/*     */   {
/* 507 */     this._gvdata = new GVData();
/* 508 */     this._textRecord = new TextRecord();
/* 509 */     this._scriptRuns = new ScriptRun();
/* 510 */     this._fontRuns = new FontRunIterator();
/* 511 */     this._erecords = new ArrayList(10);
/* 512 */     this._pt = new Point2D.Float();
/* 513 */     this._sd = new FontStrikeDesc();
/* 514 */     this._mat = new float[4];
/*     */   }
/*     */   
/*     */   private void init(int paramInt) {
/* 518 */     this._typo_flags = 0;
/* 519 */     this._ercount = 0;
/* 520 */     this._gvdata.init(paramInt);
/*     */   }
/*     */   
/*     */   private void nextEngineRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Font2D paramFont2D, int paramInt5) {
/* 524 */     EngineRecord localEngineRecord = null;
/* 525 */     if (this._ercount == this._erecords.size()) {
/* 526 */       localEngineRecord = new EngineRecord();
/* 527 */       this._erecords.add(localEngineRecord);
/*     */     } else {
/* 529 */       localEngineRecord = (EngineRecord)this._erecords.get(this._ercount);
/*     */     }
/* 531 */     localEngineRecord.init(paramInt1, paramInt2, paramFont2D, paramInt3, paramInt4, paramInt5);
/* 532 */     this._ercount += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public static final class GVData
/*     */   {
/*     */     public int _count;
/*     */     
/*     */     public int _flags;
/*     */     public int[] _glyphs;
/*     */     public float[] _positions;
/*     */     public int[] _indices;
/*     */     private static final int UNINITIALIZED_FLAGS = -1;
/*     */     
/*     */     public void init(int paramInt)
/*     */     {
/* 548 */       this._count = 0;
/* 549 */       this._flags = -1;
/*     */       
/* 551 */       if ((this._glyphs == null) || (this._glyphs.length < paramInt)) {
/* 552 */         if (paramInt < 20) {
/* 553 */           paramInt = 20;
/*     */         }
/* 555 */         this._glyphs = new int[paramInt];
/* 556 */         this._positions = new float[paramInt * 2 + 2];
/* 557 */         this._indices = new int[paramInt];
/*     */       }
/*     */     }
/*     */     
/*     */     public void grow() {
/* 562 */       grow(this._glyphs.length / 4);
/*     */     }
/*     */     
/*     */     public void grow(int paramInt) {
/* 566 */       int i = this._glyphs.length + paramInt;
/* 567 */       int[] arrayOfInt1 = new int[i];
/* 568 */       System.arraycopy(this._glyphs, 0, arrayOfInt1, 0, this._count);
/* 569 */       this._glyphs = arrayOfInt1;
/*     */       
/* 571 */       float[] arrayOfFloat = new float[i * 2 + 2];
/* 572 */       System.arraycopy(this._positions, 0, arrayOfFloat, 0, this._count * 2 + 2);
/* 573 */       this._positions = arrayOfFloat;
/*     */       
/* 575 */       int[] arrayOfInt2 = new int[i];
/* 576 */       System.arraycopy(this._indices, 0, arrayOfInt2, 0, this._count);
/* 577 */       this._indices = arrayOfInt2;
/*     */     }
/*     */     
/*     */     public void adjustPositions(AffineTransform paramAffineTransform) {
/* 581 */       paramAffineTransform.transform(this._positions, 0, this._positions, 0, this._count);
/*     */     }
/*     */     
/*     */ 
/*     */     public StandardGlyphVector createGlyphVector(Font paramFont, FontRenderContext paramFontRenderContext, StandardGlyphVector paramStandardGlyphVector)
/*     */     {
/* 587 */       if (this._flags == -1) {
/* 588 */         this._flags = 0;
/*     */         
/* 590 */         if (this._count > 1) {
/* 591 */           int i = 1;
/* 592 */           int j = 1;
/*     */           
/* 594 */           int k = this._count;
/* 595 */           for (int m = 0; (m < this._count) && ((i != 0) || (j != 0)); m++) {
/* 596 */             int n = this._indices[m];
/*     */             
/* 598 */             i = (i != 0) && (n == m) ? 1 : 0;
/* 599 */             j = (j != 0) && (n == --k) ? 1 : 0;
/*     */           }
/*     */           
/* 602 */           if (j != 0) this._flags |= 0x4;
/* 603 */           if ((j == 0) && (i == 0)) { this._flags |= 0x8;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 609 */         this._flags |= 0x2;
/*     */       }
/*     */       
/* 612 */       int[] arrayOfInt1 = new int[this._count];
/* 613 */       System.arraycopy(this._glyphs, 0, arrayOfInt1, 0, this._count);
/*     */       
/* 615 */       float[] arrayOfFloat = null;
/* 616 */       if ((this._flags & 0x2) != 0) {
/* 617 */         arrayOfFloat = new float[this._count * 2 + 2];
/* 618 */         System.arraycopy(this._positions, 0, arrayOfFloat, 0, arrayOfFloat.length);
/*     */       }
/*     */       
/* 621 */       int[] arrayOfInt2 = null;
/* 622 */       if ((this._flags & 0x8) != 0) {
/* 623 */         arrayOfInt2 = new int[this._count];
/* 624 */         System.arraycopy(this._indices, 0, arrayOfInt2, 0, this._count);
/*     */       }
/*     */       
/* 627 */       if (paramStandardGlyphVector == null) {
/* 628 */         paramStandardGlyphVector = new StandardGlyphVector(paramFont, paramFontRenderContext, arrayOfInt1, arrayOfFloat, arrayOfInt2, this._flags);
/*     */       } else {
/* 630 */         paramStandardGlyphVector.initGlyphVector(paramFont, paramFontRenderContext, arrayOfInt1, arrayOfFloat, arrayOfInt2, this._flags);
/*     */       }
/*     */       
/* 633 */       return paramStandardGlyphVector;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private final class EngineRecord
/*     */   {
/*     */     private int start;
/*     */     
/*     */     private int limit;
/*     */     private int gmask;
/*     */     private int eflags;
/*     */     private LayoutEngineKey key;
/*     */     private LayoutEngine engine;
/*     */     
/*     */     EngineRecord()
/*     */     {
/* 650 */       this.key = new LayoutEngineKey();
/*     */     }
/*     */     
/*     */     void init(int paramInt1, int paramInt2, Font2D paramFont2D, int paramInt3, int paramInt4, int paramInt5) {
/* 654 */       this.start = paramInt1;
/* 655 */       this.limit = paramInt2;
/* 656 */       this.gmask = paramInt5;
/* 657 */       this.key.init(paramFont2D, paramInt3, paramInt4);
/* 658 */       this.eflags = 0;
/*     */       
/*     */ 
/* 661 */       for (int i = paramInt1; i < paramInt2; i++) {
/* 662 */         int j = GlyphLayout.this._textRecord.text[i];
/* 663 */         if ((Character.isHighSurrogate((char)j)) && (i < paramInt2 - 1))
/*     */         {
/* 665 */           if (Character.isLowSurrogate(GlyphLayout.this._textRecord.text[(i + 1)]))
/*     */           {
/* 667 */             j = Character.toCodePoint((char)j, GlyphLayout.this._textRecord.text[(++i)]); }
/*     */         }
/* 669 */         int k = Character.getType(j);
/* 670 */         if ((k == 6) || (k == 7) || (k == 8))
/*     */         {
/*     */ 
/*     */ 
/* 674 */           this.eflags = 4;
/* 675 */           break;
/*     */         }
/*     */       }
/*     */       
/* 679 */       this.engine = GlyphLayout.this._lef.getEngine(this.key);
/*     */     }
/*     */     
/*     */     void layout() {
/* 683 */       GlyphLayout.this._textRecord.start = this.start;
/* 684 */       GlyphLayout.this._textRecord.limit = this.limit;
/* 685 */       this.engine.layout(GlyphLayout.this._sd, GlyphLayout.this._mat, this.gmask, this.start - GlyphLayout.this._offset, GlyphLayout.this._textRecord, 
/* 686 */         GlyphLayout.this._typo_flags | this.eflags, GlyphLayout.this._pt, GlyphLayout.this._gvdata);
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface LayoutEngine
/*     */   {
/*     */     public abstract void layout(FontStrikeDesc paramFontStrikeDesc, float[] paramArrayOfFloat, int paramInt1, int paramInt2, TextRecord paramTextRecord, int paramInt3, Point2D.Float paramFloat, GVData paramGVData);
/*     */   }
/*     */   
/*     */   public static abstract interface LayoutEngineFactory
/*     */   {
/*     */     public abstract LayoutEngine getEngine(Font2D paramFont2D, int paramInt1, int paramInt2);
/*     */     
/*     */     public abstract LayoutEngine getEngine(LayoutEngineKey paramLayoutEngineKey);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\GlyphLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
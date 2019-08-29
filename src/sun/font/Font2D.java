/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Font2D
/*     */ {
/*     */   public static final int FONT_CONFIG_RANK = 2;
/*     */   public static final int JRE_RANK = 2;
/*     */   public static final int TTF_RANK = 3;
/*     */   public static final int TYPE1_RANK = 4;
/*     */   public static final int NATIVE_RANK = 5;
/*     */   public static final int UNKNOWN_RANK = 6;
/*     */   public static final int DEFAULT_RANK = 4;
/*  57 */   private static final String[] boldNames = { "bold", "demibold", "demi-bold", "demi bold", "negreta", "demi" };
/*     */   
/*     */ 
/*  60 */   private static final String[] italicNames = { "italic", "cursiva", "oblique", "inclined" };
/*     */   
/*     */ 
/*  63 */   private static final String[] boldItalicNames = { "bolditalic", "bold-italic", "bold italic", "boldoblique", "bold-oblique", "bold oblique", "demibold italic", "negreta cursiva", "demi oblique" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  68 */   private static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);
/*     */   
/*     */   public Font2DHandle handle;
/*     */   
/*     */   protected String familyName;
/*     */   protected String fullName;
/*  74 */   protected int style = 0;
/*     */   protected FontFamily family;
/*  76 */   protected int fontRank = 4;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CharToGlyphMapper mapper;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  94 */   protected ConcurrentHashMap<FontStrikeDesc, Reference> strikeCache = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 108 */   protected Reference lastFontStrike = new SoftReference(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final int FWIDTH_NORMAL = 5;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final int FWEIGHT_NORMAL = 400;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final int FWEIGHT_BOLD = 700;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getStyle()
/*     */   {
/* 132 */     return this.style;
/*     */   }
/*     */   
/*     */   protected void setStyle() {
/* 136 */     String str = this.fullName.toLowerCase();
/*     */     
/* 138 */     for (int i = 0; i < boldItalicNames.length; i++) {
/* 139 */       if (str.indexOf(boldItalicNames[i]) != -1) {
/* 140 */         this.style = 3;
/* 141 */         return;
/*     */       }
/*     */     }
/*     */     
/* 145 */     for (i = 0; i < italicNames.length; i++) {
/* 146 */       if (str.indexOf(italicNames[i]) != -1) {
/* 147 */         this.style = 2;
/* 148 */         return;
/*     */       }
/*     */     }
/*     */     
/* 152 */     for (i = 0; i < boldNames.length; i++) {
/* 153 */       if (str.indexOf(boldNames[i]) != -1) {
/* 154 */         this.style = 1;
/* 155 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getWidth()
/*     */   {
/* 165 */     return 5;
/*     */   }
/*     */   
/*     */   public int getWeight() {
/* 169 */     if ((this.style & 0x1) != 0) {
/* 170 */       return 700;
/*     */     }
/* 172 */     return 400;
/*     */   }
/*     */   
/*     */   int getRank()
/*     */   {
/* 177 */     return this.fontRank;
/*     */   }
/*     */   
/*     */   void setRank(int paramInt) {
/* 181 */     this.fontRank = paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract CharToGlyphMapper getMapper();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getValidatedGlyphCode(int paramInt)
/*     */   {
/* 194 */     if ((paramInt < 0) || (paramInt >= getMapper().getNumGlyphs())) {
/* 195 */       paramInt = getMapper().getMissingGlyphCode();
/*     */     }
/* 197 */     return paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FontStrike getStrike(Font paramFont)
/*     */   {
/* 213 */     FontStrike localFontStrike = (FontStrike)this.lastFontStrike.get();
/* 214 */     if (localFontStrike != null) {
/* 215 */       return localFontStrike;
/*     */     }
/* 217 */     return getStrike(paramFont, DEFAULT_FRC);
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
/*     */   public FontStrike getStrike(Font paramFont, AffineTransform paramAffineTransform, int paramInt1, int paramInt2)
/*     */   {
/* 253 */     double d = paramFont.getSize2D();
/* 254 */     AffineTransform localAffineTransform = (AffineTransform)paramAffineTransform.clone();
/* 255 */     localAffineTransform.scale(d, d);
/* 256 */     if (paramFont.isTransformed()) {
/* 257 */       localAffineTransform.concatenate(paramFont.getTransform());
/*     */     }
/* 259 */     if ((localAffineTransform.getTranslateX() != 0.0D) || (localAffineTransform.getTranslateY() != 0.0D)) {
/* 260 */       localAffineTransform.setTransform(localAffineTransform.getScaleX(), localAffineTransform
/* 261 */         .getShearY(), localAffineTransform
/* 262 */         .getShearX(), localAffineTransform
/* 263 */         .getScaleY(), 0.0D, 0.0D);
/*     */     }
/*     */     
/*     */ 
/* 267 */     FontStrikeDesc localFontStrikeDesc = new FontStrikeDesc(paramAffineTransform, localAffineTransform, paramFont.getStyle(), paramInt1, paramInt2);
/* 268 */     return getStrike(localFontStrikeDesc, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FontStrike getStrike(Font paramFont, AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2, int paramInt1, int paramInt2)
/*     */   {
/* 280 */     FontStrikeDesc localFontStrikeDesc = new FontStrikeDesc(paramAffineTransform1, paramAffineTransform2, paramFont.getStyle(), paramInt1, paramInt2);
/* 281 */     return getStrike(localFontStrikeDesc, false);
/*     */   }
/*     */   
/*     */   public FontStrike getStrike(Font paramFont, FontRenderContext paramFontRenderContext)
/*     */   {
/* 286 */     AffineTransform localAffineTransform = paramFontRenderContext.getTransform();
/* 287 */     double d = paramFont.getSize2D();
/* 288 */     localAffineTransform.scale(d, d);
/* 289 */     if (paramFont.isTransformed()) {
/* 290 */       localAffineTransform.concatenate(paramFont.getTransform());
/* 291 */       if ((localAffineTransform.getTranslateX() != 0.0D) || (localAffineTransform.getTranslateY() != 0.0D)) {
/* 292 */         localAffineTransform.setTransform(localAffineTransform.getScaleX(), localAffineTransform
/* 293 */           .getShearY(), localAffineTransform
/* 294 */           .getShearX(), localAffineTransform
/* 295 */           .getScaleY(), 0.0D, 0.0D);
/*     */       }
/*     */     }
/*     */     
/* 299 */     int i = FontStrikeDesc.getAAHintIntVal(this, paramFont, paramFontRenderContext);
/* 300 */     int j = FontStrikeDesc.getFMHintIntVal(paramFontRenderContext.getFractionalMetricsHint());
/*     */     
/* 302 */     FontStrikeDesc localFontStrikeDesc = new FontStrikeDesc(paramFontRenderContext.getTransform(), localAffineTransform, paramFont.getStyle(), i, j);
/*     */     
/* 304 */     return getStrike(localFontStrikeDesc, false);
/*     */   }
/*     */   
/*     */   FontStrike getStrike(FontStrikeDesc paramFontStrikeDesc) {
/* 308 */     return getStrike(paramFontStrikeDesc, true);
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
/*     */   private FontStrike getStrike(FontStrikeDesc paramFontStrikeDesc, boolean paramBoolean)
/*     */   {
/* 325 */     FontStrike localFontStrike = (FontStrike)this.lastFontStrike.get();
/* 326 */     if ((localFontStrike != null) && (paramFontStrikeDesc.equals(localFontStrike.desc)))
/*     */     {
/* 328 */       return localFontStrike;
/*     */     }
/* 330 */     Reference localReference = (Reference)this.strikeCache.get(paramFontStrikeDesc);
/* 331 */     if (localReference != null) {
/* 332 */       localFontStrike = (FontStrike)localReference.get();
/* 333 */       if (localFontStrike != null)
/*     */       {
/* 335 */         this.lastFontStrike = new SoftReference(localFontStrike);
/* 336 */         StrikeCache.refStrike(localFontStrike);
/* 337 */         return localFontStrike;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 356 */     if (paramBoolean) {
/* 357 */       paramFontStrikeDesc = new FontStrikeDesc(paramFontStrikeDesc);
/*     */     }
/* 359 */     localFontStrike = createStrike(paramFontStrikeDesc);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 368 */     int i = paramFontStrikeDesc.glyphTx.getType();
/* 369 */     if (i != 32) { if ((i & 0x10) != 0)
/*     */       {
/* 371 */         if (this.strikeCache.size() <= 10) {} }
/* 372 */     } else { localReference = StrikeCache.getStrikeRef(localFontStrike, true);
/*     */       break label148; }
/* 374 */     localReference = StrikeCache.getStrikeRef(localFontStrike);
/*     */     label148:
/* 376 */     this.strikeCache.put(paramFontStrikeDesc, localReference);
/*     */     
/* 378 */     this.lastFontStrike = new SoftReference(localFontStrike);
/* 379 */     StrikeCache.refStrike(localFontStrike);
/* 380 */     return localFontStrike;
/*     */   }
/*     */   
/*     */   void removeFromCache(FontStrikeDesc paramFontStrikeDesc)
/*     */   {
/* 385 */     Reference localReference = (Reference)this.strikeCache.get(paramFontStrikeDesc);
/* 386 */     if (localReference != null) {
/* 387 */       Object localObject = localReference.get();
/* 388 */       if (localObject == null) {
/* 389 */         this.strikeCache.remove(paramFontStrikeDesc);
/*     */       }
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
/*     */   public void getFontMetrics(Font paramFont, AffineTransform paramAffineTransform, Object paramObject1, Object paramObject2, float[] paramArrayOfFloat)
/*     */   {
/* 412 */     int i = FontStrikeDesc.getAAHintIntVal(paramObject1, this, paramFont.getSize());
/* 413 */     int j = FontStrikeDesc.getFMHintIntVal(paramObject2);
/* 414 */     FontStrike localFontStrike = getStrike(paramFont, paramAffineTransform, i, j);
/* 415 */     StrikeMetrics localStrikeMetrics = localFontStrike.getFontMetrics();
/* 416 */     paramArrayOfFloat[0] = localStrikeMetrics.getAscent();
/* 417 */     paramArrayOfFloat[1] = localStrikeMetrics.getDescent();
/* 418 */     paramArrayOfFloat[2] = localStrikeMetrics.getLeading();
/* 419 */     paramArrayOfFloat[3] = localStrikeMetrics.getMaxAdvance();
/*     */     
/* 421 */     getStyleMetrics(paramFont.getSize2D(), paramArrayOfFloat, 4);
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
/*     */   public void getStyleMetrics(float paramFloat, float[] paramArrayOfFloat, int paramInt)
/*     */   {
/* 437 */     paramArrayOfFloat[paramInt] = (-paramArrayOfFloat[0] / 2.5F);
/* 438 */     paramArrayOfFloat[(paramInt + 1)] = (paramFloat / 12.0F);
/* 439 */     paramArrayOfFloat[(paramInt + 2)] = (paramArrayOfFloat[(paramInt + 1)] / 1.5F);
/* 440 */     paramArrayOfFloat[(paramInt + 3)] = paramArrayOfFloat[(paramInt + 1)];
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
/*     */   public void getFontMetrics(Font paramFont, FontRenderContext paramFontRenderContext, float[] paramArrayOfFloat)
/*     */   {
/* 453 */     StrikeMetrics localStrikeMetrics = getStrike(paramFont, paramFontRenderContext).getFontMetrics();
/* 454 */     paramArrayOfFloat[0] = localStrikeMetrics.getAscent();
/* 455 */     paramArrayOfFloat[1] = localStrikeMetrics.getDescent();
/* 456 */     paramArrayOfFloat[2] = localStrikeMetrics.getLeading();
/* 457 */     paramArrayOfFloat[3] = localStrikeMetrics.getMaxAdvance();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] getTableBytes(int paramInt)
/*     */   {
/* 465 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected long getLayoutTableCache()
/*     */   {
/* 472 */     return 0L;
/*     */   }
/*     */   
/*     */   protected long getUnitsPerEm()
/*     */   {
/* 477 */     return 2048L;
/*     */   }
/*     */   
/*     */   boolean supportsEncoding(String paramString) {
/* 481 */     return false;
/*     */   }
/*     */   
/*     */   public boolean canDoStyle(int paramInt) {
/* 485 */     return paramInt == this.style;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean useAAForPtSize(int paramInt)
/*     */   {
/* 493 */     return true;
/*     */   }
/*     */   
/*     */   public boolean hasSupplementaryChars() {
/* 497 */     return false;
/*     */   }
/*     */   
/*     */   public String getPostscriptName()
/*     */   {
/* 502 */     return this.fullName;
/*     */   }
/*     */   
/*     */   public String getFontName(Locale paramLocale) {
/* 506 */     return this.fullName;
/*     */   }
/*     */   
/*     */   public String getFamilyName(Locale paramLocale) {
/* 510 */     return this.familyName;
/*     */   }
/*     */   
/*     */   public int getNumGlyphs() {
/* 514 */     return getMapper().getNumGlyphs();
/*     */   }
/*     */   
/*     */   public int charToGlyph(int paramInt) {
/* 518 */     return getMapper().charToGlyph(paramInt);
/*     */   }
/*     */   
/*     */   public int getMissingGlyphCode() {
/* 522 */     return getMapper().getMissingGlyphCode();
/*     */   }
/*     */   
/*     */   public boolean canDisplay(char paramChar) {
/* 526 */     return getMapper().canDisplay(paramChar);
/*     */   }
/*     */   
/*     */   public boolean canDisplay(int paramInt) {
/* 530 */     return getMapper().canDisplay(paramInt);
/*     */   }
/*     */   
/*     */   public byte getBaselineFor(char paramChar) {
/* 534 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public float getItalicAngle(Font paramFont, AffineTransform paramAffineTransform, Object paramObject1, Object paramObject2)
/*     */   {
/* 542 */     int i = FontStrikeDesc.getAAHintIntVal(paramObject1, this, 12);
/* 543 */     int j = FontStrikeDesc.getFMHintIntVal(paramObject2);
/* 544 */     FontStrike localFontStrike = getStrike(paramFont, paramAffineTransform, i, j);
/* 545 */     StrikeMetrics localStrikeMetrics = localFontStrike.getFontMetrics();
/* 546 */     if ((localStrikeMetrics.ascentY == 0.0F) || (localStrikeMetrics.ascentX == 0.0F)) {
/* 547 */       return 0.0F;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 552 */     return localStrikeMetrics.ascentX / -localStrikeMetrics.ascentY;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\Font2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
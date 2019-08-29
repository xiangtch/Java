/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Point2D.Float;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CompositeStrike
/*     */   extends FontStrike
/*     */ {
/*     */   static final int SLOTMASK = 16777215;
/*     */   private CompositeFont compFont;
/*     */   private PhysicalStrike[] strikes;
/*  48 */   int numGlyphs = 0;
/*     */   
/*     */   CompositeStrike(CompositeFont paramCompositeFont, FontStrikeDesc paramFontStrikeDesc) {
/*  51 */     this.compFont = paramCompositeFont;
/*  52 */     this.desc = paramFontStrikeDesc;
/*  53 */     this.disposer = new FontStrikeDisposer(this.compFont, paramFontStrikeDesc);
/*  54 */     if (paramFontStrikeDesc.style != this.compFont.style) {
/*  55 */       this.algoStyle = true;
/*  56 */       if (((paramFontStrikeDesc.style & 0x1) == 1) && ((this.compFont.style & 0x1) == 0))
/*     */       {
/*  58 */         this.boldness = 1.33F;
/*     */       }
/*  60 */       if (((paramFontStrikeDesc.style & 0x2) == 2) && ((this.compFont.style & 0x2) == 0))
/*     */       {
/*  62 */         this.italic = 0.7F;
/*     */       }
/*     */     }
/*  65 */     this.strikes = new PhysicalStrike[this.compFont.numSlots];
/*     */   }
/*     */   
/*     */   PhysicalStrike getStrikeForGlyph(int paramInt)
/*     */   {
/*  70 */     return getStrikeForSlot(paramInt >>> 24);
/*     */   }
/*     */   
/*     */   PhysicalStrike getStrikeForSlot(int paramInt)
/*     */   {
/*  75 */     if (paramInt >= this.strikes.length) {
/*  76 */       paramInt = 0;
/*     */     }
/*     */     
/*  79 */     PhysicalStrike localPhysicalStrike = this.strikes[paramInt];
/*  80 */     if (localPhysicalStrike == null)
/*     */     {
/*  82 */       localPhysicalStrike = (PhysicalStrike)this.compFont.getSlotFont(paramInt).getStrike(this.desc);
/*     */       
/*  84 */       this.strikes[paramInt] = localPhysicalStrike;
/*     */     }
/*  86 */     return localPhysicalStrike;
/*     */   }
/*     */   
/*     */   public int getNumGlyphs() {
/*  90 */     return this.compFont.getNumGlyphs();
/*     */   }
/*     */   
/*     */   StrikeMetrics getFontMetrics() {
/*  94 */     if (this.strikeMetrics == null) {
/*  95 */       StrikeMetrics localStrikeMetrics = new StrikeMetrics();
/*  96 */       for (int i = 0; i < this.compFont.numMetricsSlots; i++) {
/*  97 */         localStrikeMetrics.merge(getStrikeForSlot(i).getFontMetrics());
/*     */       }
/*  99 */       this.strikeMetrics = localStrikeMetrics;
/*     */     }
/* 101 */     return this.strikeMetrics;
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
/*     */   void getGlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt)
/*     */   {
/* 118 */     PhysicalStrike localPhysicalStrike = getStrikeForSlot(0);
/* 119 */     int i = localPhysicalStrike.getSlot0GlyphImagePtrs(paramArrayOfInt, paramArrayOfLong, paramInt);
/* 120 */     if (i == paramInt) {
/* 121 */       return;
/*     */     }
/* 123 */     for (int j = i; j < paramInt; j++) {
/* 124 */       localPhysicalStrike = getStrikeForGlyph(paramArrayOfInt[j]);
/* 125 */       paramArrayOfLong[j] = localPhysicalStrike.getGlyphImagePtr(paramArrayOfInt[j] & 0xFFFFFF);
/*     */     }
/*     */   }
/*     */   
/*     */   long getGlyphImagePtr(int paramInt)
/*     */   {
/* 131 */     PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
/* 132 */     return localPhysicalStrike.getGlyphImagePtr(paramInt & 0xFFFFFF);
/*     */   }
/*     */   
/*     */   void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle) {
/* 136 */     PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
/* 137 */     localPhysicalStrike.getGlyphImageBounds(paramInt & 0xFFFFFF, paramFloat, paramRectangle);
/*     */   }
/*     */   
/*     */   Point2D.Float getGlyphMetrics(int paramInt) {
/* 141 */     PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
/* 142 */     return localPhysicalStrike.getGlyphMetrics(paramInt & 0xFFFFFF);
/*     */   }
/*     */   
/*     */   Point2D.Float getCharMetrics(char paramChar) {
/* 146 */     return getGlyphMetrics(this.compFont.getMapper().charToGlyph(paramChar));
/*     */   }
/*     */   
/*     */   float getGlyphAdvance(int paramInt) {
/* 150 */     PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
/* 151 */     return localPhysicalStrike.getGlyphAdvance(paramInt & 0xFFFFFF);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   float getCodePointAdvance(int paramInt)
/*     */   {
/* 162 */     return getGlyphAdvance(this.compFont.getMapper().charToGlyph(paramInt));
/*     */   }
/*     */   
/*     */   Rectangle2D.Float getGlyphOutlineBounds(int paramInt) {
/* 166 */     PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
/* 167 */     return localPhysicalStrike.getGlyphOutlineBounds(paramInt & 0xFFFFFF);
/*     */   }
/*     */   
/*     */   GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2)
/*     */   {
/* 172 */     PhysicalStrike localPhysicalStrike = getStrikeForGlyph(paramInt);
/* 173 */     GeneralPath localGeneralPath = localPhysicalStrike.getGlyphOutline(paramInt & 0xFFFFFF, paramFloat1, paramFloat2);
/* 174 */     if (localGeneralPath == null) {
/* 175 */       return new GeneralPath();
/*     */     }
/* 177 */     return localGeneralPath;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   GeneralPath getGlyphVectorOutline(int[] paramArrayOfInt, float paramFloat1, float paramFloat2)
/*     */   {
/* 189 */     Object localObject = null;
/*     */     
/* 191 */     int i = 0;
/*     */     
/*     */ 
/* 194 */     while (i < paramArrayOfInt.length) {
/* 195 */       int j = i;
/* 196 */       int k = paramArrayOfInt[i] >>> 24;
/* 197 */       while ((i < paramArrayOfInt.length) && (paramArrayOfInt[(i + 1)] >>> 24 == k))
/*     */       {
/* 199 */         i++;
/*     */       }
/* 201 */       int m = i - j + 1;
/* 202 */       int[] arrayOfInt = new int[m];
/* 203 */       for (int n = 0; n < m; n++) {
/* 204 */         paramArrayOfInt[n] &= 0xFFFFFF;
/*     */       }
/* 206 */       GeneralPath localGeneralPath = getStrikeForSlot(k).getGlyphVectorOutline(arrayOfInt, paramFloat1, paramFloat2);
/* 207 */       if (localObject == null) {
/* 208 */         localObject = localGeneralPath;
/* 209 */       } else if (localGeneralPath != null) {
/* 210 */         ((GeneralPath)localObject).append(localGeneralPath, false);
/*     */       }
/*     */     }
/* 213 */     if (localObject == null) {
/* 214 */       return new GeneralPath();
/*     */     }
/* 216 */     return (GeneralPath)localObject;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\CompositeStrike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
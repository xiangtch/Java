/*     */ package sun.font;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Locale;
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
/*     */ public class TrueTypeGlyphMapper
/*     */   extends CharToGlyphMapper
/*     */ {
/*     */   static final char REVERSE_SOLIDUS = '\\';
/*     */   static final char JA_YEN = '¥';
/*     */   static final char JA_FULLWIDTH_TILDE_CHAR = '～';
/*     */   static final char JA_WAVE_DASH_CHAR = '〜';
/*  42 */   static final boolean isJAlocale = Locale.JAPAN.equals(Locale.getDefault());
/*     */   private final boolean needsJAremapping;
/*     */   private boolean remapJAWaveDash;
/*     */   TrueTypeFont font;
/*     */   CMap cmap;
/*     */   int numGlyphs;
/*     */   
/*     */   public TrueTypeGlyphMapper(TrueTypeFont paramTrueTypeFont)
/*     */   {
/*  51 */     this.font = paramTrueTypeFont;
/*     */     try {
/*  53 */       this.cmap = CMap.initialize(paramTrueTypeFont);
/*     */     } catch (Exception localException) {
/*  55 */       this.cmap = null;
/*     */     }
/*  57 */     if (this.cmap == null) {
/*  58 */       handleBadCMAP();
/*     */     }
/*  60 */     this.missingGlyph = 0;
/*  61 */     ByteBuffer localByteBuffer = paramTrueTypeFont.getTableBuffer(1835104368);
/*  62 */     if ((localByteBuffer != null) && (localByteBuffer.capacity() >= 6)) {
/*  63 */       this.numGlyphs = localByteBuffer.getChar(4);
/*     */     } else {
/*  65 */       handleBadCMAP();
/*     */     }
/*  67 */     if ((FontUtilities.isSolaris) && (isJAlocale) && (paramTrueTypeFont.supportsJA())) {
/*  68 */       this.needsJAremapping = true;
/*  69 */       if ((FontUtilities.isSolaris8) && 
/*  70 */         (getGlyphFromCMAP(12316) == this.missingGlyph)) {
/*  71 */         this.remapJAWaveDash = true;
/*     */       }
/*     */     } else {
/*  74 */       this.needsJAremapping = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNumGlyphs() {
/*  79 */     return this.numGlyphs;
/*     */   }
/*     */   
/*     */   private char getGlyphFromCMAP(int paramInt) {
/*     */     try {
/*  84 */       int i = this.cmap.getGlyph(paramInt);
/*  85 */       if ((i < this.numGlyphs) || (i >= 65534))
/*     */       {
/*  87 */         return i;
/*     */       }
/*  89 */       if (FontUtilities.isLogging())
/*     */       {
/*  91 */         FontUtilities.getLogger().warning(this.font + " out of range glyph id=" + 
/*  92 */           Integer.toHexString(i) + " for char " + 
/*  93 */           Integer.toHexString(paramInt));
/*     */       }
/*  95 */       return (char)this.missingGlyph;
/*     */     }
/*     */     catch (Exception localException) {
/*  98 */       handleBadCMAP(); }
/*  99 */     return (char)this.missingGlyph;
/*     */   }
/*     */   
/*     */   private void handleBadCMAP()
/*     */   {
/* 104 */     if (FontUtilities.isLogging()) {
/* 105 */       FontUtilities.getLogger().severe("Null Cmap for " + this.font + "substituting for this font");
/*     */     }
/*     */     
/* 108 */     SunFontManager.getInstance().deRegisterBadFont(this.font);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 113 */     this.cmap = CMap.theNullCmap;
/*     */   }
/*     */   
/*     */   private final char remapJAChar(char paramChar) {
/* 117 */     switch (paramChar) {
/*     */     case '\\': 
/* 119 */       return '¥';
/*     */     
/*     */ 
/*     */ 
/*     */     case '〜': 
/* 124 */       if (this.remapJAWaveDash)
/* 125 */         return 65374;
/*     */       break; }
/* 127 */     return paramChar;
/*     */   }
/*     */   
/*     */   private final int remapJAIntChar(int paramInt) {
/* 131 */     switch (paramInt) {
/*     */     case 92: 
/* 133 */       return 165;
/*     */     
/*     */ 
/*     */ 
/*     */     case 12316: 
/* 138 */       if (this.remapJAWaveDash)
/* 139 */         return 65374;
/*     */       break; }
/* 141 */     return paramInt;
/*     */   }
/*     */   
/*     */   public int charToGlyph(char paramChar)
/*     */   {
/* 146 */     if (this.needsJAremapping) {
/* 147 */       paramChar = remapJAChar(paramChar);
/*     */     }
/* 149 */     int i = getGlyphFromCMAP(paramChar);
/* 150 */     if ((this.font.checkUseNatives()) && (i < this.font.glyphToCharMap.length)) {
/* 151 */       this.font.glyphToCharMap[i] = paramChar;
/*     */     }
/* 153 */     return i;
/*     */   }
/*     */   
/*     */   public int charToGlyph(int paramInt) {
/* 157 */     if (this.needsJAremapping) {
/* 158 */       paramInt = remapJAIntChar(paramInt);
/*     */     }
/* 160 */     int i = getGlyphFromCMAP(paramInt);
/* 161 */     if ((this.font.checkUseNatives()) && (i < this.font.glyphToCharMap.length)) {
/* 162 */       this.font.glyphToCharMap[i] = ((char)paramInt);
/*     */     }
/* 164 */     return i;
/*     */   }
/*     */   
/*     */   public void charsToGlyphs(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
/* 168 */     for (int i = 0; i < paramInt; i++) {
/* 169 */       if (this.needsJAremapping) {
/* 170 */         paramArrayOfInt2[i] = getGlyphFromCMAP(remapJAIntChar(paramArrayOfInt1[i]));
/*     */       } else {
/* 172 */         paramArrayOfInt2[i] = getGlyphFromCMAP(paramArrayOfInt1[i]);
/*     */       }
/* 174 */       if ((this.font.checkUseNatives()) && (paramArrayOfInt2[i] < this.font.glyphToCharMap.length))
/*     */       {
/* 176 */         this.font.glyphToCharMap[paramArrayOfInt2[i]] = ((char)paramArrayOfInt1[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void charsToGlyphs(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
/*     */   {
/* 183 */     for (int i = 0; i < paramInt; i++) {
/*     */       int j;
/* 185 */       if (this.needsJAremapping) {
/* 186 */         j = remapJAChar(paramArrayOfChar[i]);
/*     */       } else {
/* 188 */         j = paramArrayOfChar[i];
/*     */       }
/*     */       
/* 191 */       if ((j >= 55296) && (j <= 56319) && (i < paramInt - 1))
/*     */       {
/* 193 */         int k = paramArrayOfChar[(i + 1)];
/*     */         
/* 195 */         if ((k >= 56320) && (k <= 57343))
/*     */         {
/* 197 */           j = (j - 55296) * 1024 + k - 56320 + 65536;
/*     */           
/*     */ 
/* 200 */           paramArrayOfInt[i] = getGlyphFromCMAP(j);
/* 201 */           i++;
/* 202 */           paramArrayOfInt[i] = 65535;
/* 203 */           continue;
/*     */         }
/*     */       }
/* 206 */       paramArrayOfInt[i] = getGlyphFromCMAP(j);
/*     */       
/* 208 */       if ((this.font.checkUseNatives()) && (paramArrayOfInt[i] < this.font.glyphToCharMap.length))
/*     */       {
/* 210 */         this.font.glyphToCharMap[paramArrayOfInt[i]] = ((char)j);
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
/*     */   public boolean charsToGlyphsNS(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
/*     */   {
/* 223 */     for (int i = 0; i < paramInt; i++) {
/*     */       int j;
/* 225 */       if (this.needsJAremapping) {
/* 226 */         j = remapJAChar(paramArrayOfChar[i]);
/*     */       } else {
/* 228 */         j = paramArrayOfChar[i];
/*     */       }
/*     */       
/* 231 */       if ((j >= 55296) && (j <= 56319) && (i < paramInt - 1))
/*     */       {
/* 233 */         int k = paramArrayOfChar[(i + 1)];
/*     */         
/* 235 */         if ((k >= 56320) && (k <= 57343))
/*     */         {
/* 237 */           j = (j - 55296) * 1024 + k - 56320 + 65536;
/*     */           
/* 239 */           paramArrayOfInt[(i + 1)] = 65535;
/*     */         }
/*     */       }
/*     */       
/* 243 */       paramArrayOfInt[i] = getGlyphFromCMAP(j);
/* 244 */       if ((this.font.checkUseNatives()) && (paramArrayOfInt[i] < this.font.glyphToCharMap.length))
/*     */       {
/* 246 */         this.font.glyphToCharMap[paramArrayOfInt[i]] = ((char)j);
/*     */       }
/*     */       
/* 249 */       if (j >= 768)
/*     */       {
/*     */ 
/* 252 */         if (FontUtilities.isComplexCharCode(j)) {
/* 253 */           return true;
/*     */         }
/* 255 */         if (j >= 65536) {
/* 256 */           i++;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 261 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   boolean hasSupplementaryChars()
/*     */   {
/* 268 */     return ((this.cmap instanceof CMap.CMapFormat8)) || ((this.cmap instanceof CMap.CMapFormat10)) || ((this.cmap instanceof CMap.CMapFormat12));
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\TrueTypeGlyphMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
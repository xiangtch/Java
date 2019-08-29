/*     */ package sun.font;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CompositeFont
/*     */   extends Font2D
/*     */ {
/*     */   private boolean[] deferredInitialisation;
/*     */   String[] componentFileNames;
/*     */   String[] componentNames;
/*     */   private PhysicalFont[] components;
/*     */   int numSlots;
/*     */   int numMetricsSlots;
/*     */   int[] exclusionRanges;
/*     */   int[] maxIndices;
/*  54 */   int numGlyphs = 0;
/*  55 */   int localeSlot = -1;
/*     */   
/*     */ 
/*  58 */   boolean isStdComposite = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CompositeFont(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean, SunFontManager paramSunFontManager)
/*     */   {
/*  65 */     this.handle = new Font2DHandle(this);
/*  66 */     this.fullName = paramString;
/*  67 */     this.componentFileNames = paramArrayOfString1;
/*  68 */     this.componentNames = paramArrayOfString2;
/*  69 */     if (paramArrayOfString2 == null) {
/*  70 */       this.numSlots = this.componentFileNames.length;
/*     */     } else {
/*  72 */       this.numSlots = this.componentNames.length;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  80 */     this.numSlots = (this.numSlots <= 254 ? this.numSlots : 254);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  85 */     this.numMetricsSlots = paramInt;
/*  86 */     this.exclusionRanges = paramArrayOfInt1;
/*  87 */     this.maxIndices = paramArrayOfInt2;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  95 */     if (paramSunFontManager.getEUDCFont() != null) {
/*  96 */       i = this.numMetricsSlots;
/*  97 */       int j = this.numSlots - i;
/*  98 */       this.numSlots += 1;
/*  99 */       if (this.componentNames != null) {
/* 100 */         this.componentNames = new String[this.numSlots];
/* 101 */         System.arraycopy(paramArrayOfString2, 0, this.componentNames, 0, i);
/* 102 */         this.componentNames[i] = paramSunFontManager.getEUDCFont().getFontName(null);
/* 103 */         System.arraycopy(paramArrayOfString2, i, this.componentNames, i + 1, j);
/*     */       }
/*     */       
/* 106 */       if (this.componentFileNames != null) {
/* 107 */         this.componentFileNames = new String[this.numSlots];
/* 108 */         System.arraycopy(paramArrayOfString1, 0, this.componentFileNames, 0, i);
/*     */         
/* 110 */         System.arraycopy(paramArrayOfString1, i, this.componentFileNames, i + 1, j);
/*     */       }
/*     */       
/* 113 */       this.components = new PhysicalFont[this.numSlots];
/* 114 */       this.components[i] = paramSunFontManager.getEUDCFont();
/* 115 */       this.deferredInitialisation = new boolean[this.numSlots];
/* 116 */       if (paramBoolean) {
/* 117 */         for (int k = 0; k < this.numSlots - 1; k++) {
/* 118 */           this.deferredInitialisation[k] = true;
/*     */         }
/*     */       }
/*     */     } else {
/* 122 */       this.components = new PhysicalFont[this.numSlots];
/* 123 */       this.deferredInitialisation = new boolean[this.numSlots];
/* 124 */       if (paramBoolean) {
/* 125 */         for (i = 0; i < this.numSlots; i++) {
/* 126 */           this.deferredInitialisation[i] = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 131 */     this.fontRank = 2;
/*     */     
/* 133 */     int i = this.fullName.indexOf('.');
/* 134 */     if (i > 0) {
/* 135 */       this.familyName = this.fullName.substring(0, i);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 140 */       if (i + 1 < this.fullName.length()) {
/* 141 */         String str = this.fullName.substring(i + 1);
/* 142 */         if ("plain".equals(str)) {
/* 143 */           this.style = 0;
/* 144 */         } else if ("bold".equals(str)) {
/* 145 */           this.style = 1;
/* 146 */         } else if ("italic".equals(str)) {
/* 147 */           this.style = 2;
/* 148 */         } else if ("bolditalic".equals(str)) {
/* 149 */           this.style = 3;
/*     */         }
/*     */       }
/*     */     } else {
/* 153 */       this.familyName = this.fullName;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   CompositeFont(PhysicalFont[] paramArrayOfPhysicalFont)
/*     */   {
/* 162 */     this.isStdComposite = false;
/* 163 */     this.handle = new Font2DHandle(this);
/* 164 */     this.fullName = paramArrayOfPhysicalFont[0].fullName;
/* 165 */     this.familyName = paramArrayOfPhysicalFont[0].familyName;
/* 166 */     this.style = paramArrayOfPhysicalFont[0].style;
/*     */     
/* 168 */     this.numMetricsSlots = 1;
/* 169 */     this.numSlots = paramArrayOfPhysicalFont.length;
/*     */     
/* 171 */     this.components = new PhysicalFont[this.numSlots];
/* 172 */     System.arraycopy(paramArrayOfPhysicalFont, 0, this.components, 0, this.numSlots);
/* 173 */     this.deferredInitialisation = new boolean[this.numSlots];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   CompositeFont(PhysicalFont paramPhysicalFont, CompositeFont paramCompositeFont)
/*     */   {
/* 183 */     this.isStdComposite = false;
/* 184 */     this.handle = new Font2DHandle(this);
/* 185 */     this.fullName = paramPhysicalFont.fullName;
/* 186 */     this.familyName = paramPhysicalFont.familyName;
/* 187 */     this.style = paramPhysicalFont.style;
/*     */     
/* 189 */     this.numMetricsSlots = 1;
/* 190 */     paramCompositeFont.numSlots += 1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 199 */     synchronized (FontManagerFactory.getInstance()) {
/* 200 */       this.components = new PhysicalFont[this.numSlots];
/* 201 */       this.components[0] = paramPhysicalFont;
/* 202 */       System.arraycopy(paramCompositeFont.components, 0, this.components, 1, paramCompositeFont.numSlots);
/*     */       
/*     */ 
/* 205 */       if (paramCompositeFont.componentNames != null) {
/* 206 */         this.componentNames = new String[this.numSlots];
/* 207 */         this.componentNames[0] = paramPhysicalFont.fullName;
/* 208 */         System.arraycopy(paramCompositeFont.componentNames, 0, this.componentNames, 1, paramCompositeFont.numSlots);
/*     */       }
/*     */       
/* 211 */       if (paramCompositeFont.componentFileNames != null) {
/* 212 */         this.componentFileNames = new String[this.numSlots];
/* 213 */         this.componentFileNames[0] = null;
/* 214 */         System.arraycopy(paramCompositeFont.componentFileNames, 0, this.componentFileNames, 1, paramCompositeFont.numSlots);
/*     */       }
/*     */       
/* 217 */       this.deferredInitialisation = new boolean[this.numSlots];
/* 218 */       this.deferredInitialisation[0] = false;
/* 219 */       System.arraycopy(paramCompositeFont.deferredInitialisation, 0, this.deferredInitialisation, 1, paramCompositeFont.numSlots);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void doDeferredInitialisation(int paramInt)
/*     */   {
/* 260 */     if (this.deferredInitialisation[paramInt] == 0) {
/* 261 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 269 */     SunFontManager localSunFontManager = SunFontManager.getInstance();
/* 270 */     synchronized (localSunFontManager) {
/* 271 */       if (this.componentNames == null) {
/* 272 */         this.componentNames = new String[this.numSlots];
/*     */       }
/* 274 */       if (this.components[paramInt] == null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 284 */         if ((this.componentFileNames != null) && (this.componentFileNames[paramInt] != null))
/*     */         {
/*     */ 
/* 287 */           this.components[paramInt] = localSunFontManager.initialiseDeferredFont(this.componentFileNames[paramInt]);
/*     */         }
/*     */         
/* 290 */         if (this.components[paramInt] == null) {
/* 291 */           this.components[paramInt] = localSunFontManager.getDefaultPhysicalFont();
/*     */         }
/* 293 */         String str = this.components[paramInt].getFontName(null);
/* 294 */         if (this.componentNames[paramInt] == null) {
/* 295 */           this.componentNames[paramInt] = str;
/* 296 */         } else if (!this.componentNames[paramInt].equalsIgnoreCase(str))
/*     */         {
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 305 */             this.components[paramInt] = ((PhysicalFont)localSunFontManager.findFont2D(this.componentNames[paramInt], this.style, 1));
/*     */ 
/*     */           }
/*     */           catch (ClassCastException localClassCastException)
/*     */           {
/* 310 */             this.components[paramInt] = localSunFontManager.getDefaultPhysicalFont();
/*     */           }
/*     */         }
/*     */       }
/* 314 */       this.deferredInitialisation[paramInt] = false;
/*     */     }
/*     */   }
/*     */   
/*     */   void replaceComponentFont(PhysicalFont paramPhysicalFont1, PhysicalFont paramPhysicalFont2)
/*     */   {
/* 320 */     if (this.components == null) {
/* 321 */       return;
/*     */     }
/* 323 */     for (int i = 0; i < this.numSlots; i++) {
/* 324 */       if (this.components[i] == paramPhysicalFont1) {
/* 325 */         this.components[i] = paramPhysicalFont2;
/* 326 */         if (this.componentNames != null) {
/* 327 */           this.componentNames[i] = paramPhysicalFont2.getFontName(null);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isExcludedChar(int paramInt1, int paramInt2)
/*     */   {
/* 335 */     if ((this.exclusionRanges == null) || (this.maxIndices == null) || (paramInt1 >= this.numMetricsSlots))
/*     */     {
/* 337 */       return false;
/*     */     }
/*     */     
/* 340 */     int i = 0;
/* 341 */     int j = this.maxIndices[paramInt1];
/* 342 */     if (paramInt1 > 0) {
/* 343 */       i = this.maxIndices[(paramInt1 - 1)];
/*     */     }
/* 345 */     int k = i;
/* 346 */     while (j > k) {
/* 347 */       if ((paramInt2 >= this.exclusionRanges[k]) && (paramInt2 <= this.exclusionRanges[(k + 1)]))
/*     */       {
/* 349 */         return true;
/*     */       }
/* 351 */       k += 2;
/*     */     }
/* 353 */     return false;
/*     */   }
/*     */   
/*     */   public void getStyleMetrics(float paramFloat, float[] paramArrayOfFloat, int paramInt) {
/* 357 */     PhysicalFont localPhysicalFont = getSlotFont(0);
/* 358 */     if (localPhysicalFont == null) {
/* 359 */       super.getStyleMetrics(paramFloat, paramArrayOfFloat, paramInt);
/*     */     } else {
/* 361 */       localPhysicalFont.getStyleMetrics(paramFloat, paramArrayOfFloat, paramInt);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNumSlots() {
/* 366 */     return this.numSlots;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PhysicalFont getSlotFont(int paramInt)
/*     */   {
/* 375 */     if (this.deferredInitialisation[paramInt] != 0) {
/* 376 */       doDeferredInitialisation(paramInt);
/*     */     }
/* 378 */     SunFontManager localSunFontManager = SunFontManager.getInstance();
/*     */     try {
/* 380 */       PhysicalFont localPhysicalFont = this.components[paramInt];
/* 381 */       if (localPhysicalFont == null) {
/*     */         try
/*     */         {
/* 384 */           localPhysicalFont = (PhysicalFont)localSunFontManager.findFont2D(this.componentNames[paramInt], this.style, 1);
/*     */           
/* 386 */           this.components[paramInt] = localPhysicalFont;
/*     */         } catch (ClassCastException localClassCastException) {
/* 388 */           localPhysicalFont = localSunFontManager.getDefaultPhysicalFont();
/*     */         }
/*     */       }
/* 391 */       return localPhysicalFont;
/*     */     } catch (Exception localException) {}
/* 393 */     return localSunFontManager.getDefaultPhysicalFont();
/*     */   }
/*     */   
/*     */   FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc)
/*     */   {
/* 398 */     return new CompositeStrike(this, paramFontStrikeDesc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isStdComposite()
/*     */   {
/* 407 */     return this.isStdComposite;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getValidatedGlyphCode(int paramInt)
/*     */   {
/* 416 */     int i = paramInt >>> 24;
/* 417 */     if (i >= this.numSlots) {
/* 418 */       return getMapper().getMissingGlyphCode();
/*     */     }
/*     */     
/* 421 */     int j = paramInt & 0xFFFFFF;
/* 422 */     PhysicalFont localPhysicalFont = getSlotFont(i);
/*     */     
/* 424 */     if (localPhysicalFont.getValidatedGlyphCode(j) == localPhysicalFont.getMissingGlyphCode()) {
/* 425 */       return getMapper().getMissingGlyphCode();
/*     */     }
/* 427 */     return paramInt;
/*     */   }
/*     */   
/*     */   public CharToGlyphMapper getMapper()
/*     */   {
/* 432 */     if (this.mapper == null) {
/* 433 */       this.mapper = new CompositeGlyphMapper(this);
/*     */     }
/* 435 */     return this.mapper;
/*     */   }
/*     */   
/*     */   public boolean hasSupplementaryChars() {
/* 439 */     for (int i = 0; i < this.numSlots; i++) {
/* 440 */       if (getSlotFont(i).hasSupplementaryChars()) {
/* 441 */         return true;
/*     */       }
/*     */     }
/* 444 */     return false;
/*     */   }
/*     */   
/*     */   public int getNumGlyphs() {
/* 448 */     if (this.numGlyphs == 0) {
/* 449 */       this.numGlyphs = getMapper().getNumGlyphs();
/*     */     }
/* 451 */     return this.numGlyphs;
/*     */   }
/*     */   
/*     */   public int getMissingGlyphCode() {
/* 455 */     return getMapper().getMissingGlyphCode();
/*     */   }
/*     */   
/*     */   public boolean canDisplay(char paramChar) {
/* 459 */     return getMapper().canDisplay(paramChar);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean useAAForPtSize(int paramInt)
/*     */   {
/* 469 */     if (this.localeSlot == -1)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 474 */       int i = this.numMetricsSlots;
/* 475 */       if ((i == 1) && (!isStdComposite())) {
/* 476 */         i = this.numSlots;
/*     */       }
/* 478 */       for (int j = 0; j < i; j++) {
/* 479 */         if (getSlotFont(j).supportsEncoding(null)) {
/* 480 */           this.localeSlot = j;
/* 481 */           break;
/*     */         }
/*     */       }
/* 484 */       if (this.localeSlot == -1) {
/* 485 */         this.localeSlot = 0;
/*     */       }
/*     */     }
/* 488 */     return getSlotFont(this.localeSlot).useAAForPtSize(paramInt);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 492 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
/*     */     
/* 494 */     String str2 = "";
/* 495 */     for (int i = 0; i < this.numSlots; i++) {
/* 496 */       str2 = str2 + "    Slot[" + i + "]=" + getSlotFont(i) + str1;
/*     */     }
/* 498 */     return "** Composite Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + str1 + str2;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\CompositeFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
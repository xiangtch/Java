/*     */ package sun.font;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
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
/*     */ public class FontFamily
/*     */ {
/*  39 */   private static ConcurrentHashMap<String, FontFamily> familyNameMap = new ConcurrentHashMap();
/*     */   
/*     */   private static HashMap<String, FontFamily> allLocaleNames;
/*     */   protected String familyName;
/*     */   protected Font2D plain;
/*     */   protected Font2D bold;
/*     */   protected Font2D italic;
/*     */   protected Font2D bolditalic;
/*  47 */   protected boolean logicalFont = false;
/*     */   protected int familyRank;
/*     */   
/*     */   public static FontFamily getFamily(String paramString) {
/*  51 */     return (FontFamily)familyNameMap.get(paramString.toLowerCase(Locale.ENGLISH));
/*     */   }
/*     */   
/*     */   public static String[] getAllFamilyNames() {
/*  55 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void remove(Font2D paramFont2D)
/*     */   {
/*  64 */     String str = paramFont2D.getFamilyName(Locale.ENGLISH);
/*  65 */     FontFamily localFontFamily = getFamily(str);
/*  66 */     if (localFontFamily == null) {
/*  67 */       return;
/*     */     }
/*  69 */     if (localFontFamily.plain == paramFont2D) {
/*  70 */       localFontFamily.plain = null;
/*     */     }
/*  72 */     if (localFontFamily.bold == paramFont2D) {
/*  73 */       localFontFamily.bold = null;
/*     */     }
/*  75 */     if (localFontFamily.italic == paramFont2D) {
/*  76 */       localFontFamily.italic = null;
/*     */     }
/*  78 */     if (localFontFamily.bolditalic == paramFont2D) {
/*  79 */       localFontFamily.bolditalic = null;
/*     */     }
/*  81 */     if ((localFontFamily.plain == null) && (localFontFamily.bold == null) && (localFontFamily.plain == null) && (localFontFamily.bold == null))
/*     */     {
/*  83 */       familyNameMap.remove(str);
/*     */     }
/*     */   }
/*     */   
/*     */   public FontFamily(String paramString, boolean paramBoolean, int paramInt) {
/*  88 */     this.logicalFont = paramBoolean;
/*  89 */     this.familyName = paramString;
/*  90 */     this.familyRank = paramInt;
/*  91 */     familyNameMap.put(paramString.toLowerCase(Locale.ENGLISH), this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   FontFamily(String paramString)
/*     */   {
/*  98 */     this.logicalFont = false;
/*  99 */     this.familyName = paramString;
/* 100 */     this.familyRank = 4;
/*     */   }
/*     */   
/*     */   public String getFamilyName() {
/* 104 */     return this.familyName;
/*     */   }
/*     */   
/*     */   public int getRank() {
/* 108 */     return this.familyRank;
/*     */   }
/*     */   
/*     */   private boolean isFromSameSource(Font2D paramFont2D) {
/* 112 */     if (!(paramFont2D instanceof FileFont)) {
/* 113 */       return false;
/*     */     }
/*     */     
/* 116 */     FileFont localFileFont1 = null;
/* 117 */     if ((this.plain instanceof FileFont)) {
/* 118 */       localFileFont1 = (FileFont)this.plain;
/* 119 */     } else if ((this.bold instanceof FileFont)) {
/* 120 */       localFileFont1 = (FileFont)this.bold;
/* 121 */     } else if ((this.italic instanceof FileFont)) {
/* 122 */       localFileFont1 = (FileFont)this.italic;
/* 123 */     } else if ((this.bolditalic instanceof FileFont)) {
/* 124 */       localFileFont1 = (FileFont)this.bolditalic;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 129 */     if (localFileFont1 == null) {
/* 130 */       return false;
/*     */     }
/* 132 */     File localFile1 = new File(localFileFont1.platName).getParentFile();
/*     */     
/* 134 */     FileFont localFileFont2 = (FileFont)paramFont2D;
/* 135 */     File localFile2 = new File(localFileFont2.platName).getParentFile();
/* 136 */     if (localFile1 != null) {
/*     */       try {
/* 138 */         localFile1 = localFile1.getCanonicalFile();
/*     */       } catch (IOException localIOException1) {}
/*     */     }
/* 141 */     if (localFile2 != null) {
/*     */       try {
/* 143 */         localFile2 = localFile2.getCanonicalFile();
/*     */       } catch (IOException localIOException2) {}
/*     */     }
/* 146 */     return Objects.equals(localFile2, localFile1);
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
/* 166 */   private int familyWidth = 0;
/*     */   
/*     */   private boolean preferredWidth(Font2D paramFont2D) {
/* 169 */     int i = paramFont2D.getWidth();
/*     */     
/* 171 */     if (this.familyWidth == 0) {
/* 172 */       this.familyWidth = i;
/* 173 */       return true;
/*     */     }
/*     */     
/* 176 */     if (i == this.familyWidth) {
/* 177 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 181 */     if (Math.abs(5 - i) < Math.abs(5 - this.familyWidth))
/*     */     {
/* 183 */       if (FontUtilities.debugFonts()) {
/* 184 */         FontUtilities.getLogger().info("Found more preferred width. New width = " + i + " Old width = " + this.familyWidth + " in font " + paramFont2D + " nulling out fonts plain: " + this.plain + " bold: " + this.bold + " italic: " + this.italic + " bolditalic: " + this.bolditalic);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 190 */       this.familyWidth = i;
/* 191 */       this.plain = (this.bold = this.italic = this.bolditalic = null);
/* 192 */       return true; }
/* 193 */     if (FontUtilities.debugFonts()) {
/* 194 */       FontUtilities.getLogger().info("Family rejecting font " + paramFont2D + " of less preferred width " + i);
/*     */     }
/*     */     
/*     */ 
/* 198 */     return false;
/*     */   }
/*     */   
/*     */   private boolean closerWeight(Font2D paramFont2D1, Font2D paramFont2D2, int paramInt) {
/* 202 */     if (this.familyWidth != paramFont2D2.getWidth()) {
/* 203 */       return false;
/*     */     }
/*     */     
/* 206 */     if (paramFont2D1 == null) {
/* 207 */       return true;
/*     */     }
/*     */     
/* 210 */     if (FontUtilities.debugFonts()) {
/* 211 */       FontUtilities.getLogger().info("New weight for style " + paramInt + ". Curr.font=" + paramFont2D1 + " New font=" + paramFont2D2 + " Curr.weight=" + paramFont2D1
/*     */       
/* 213 */         .getWeight() + " New weight=" + paramFont2D2
/* 214 */         .getWeight());
/*     */     }
/*     */     
/* 217 */     int i = paramFont2D2.getWeight();
/* 218 */     switch (paramInt) {
/*     */     case 0: 
/*     */     case 2: 
/* 221 */       return (i <= 400) && 
/* 222 */         (i > paramFont2D1.getWeight());
/*     */     
/*     */     case 1: 
/*     */     case 3: 
/* 226 */       return 
/* 227 */         Math.abs(i - 700) < Math.abs(paramFont2D1.getWeight() - 700);
/*     */     }
/*     */     
/* 230 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setFont(Font2D paramFont2D, int paramInt)
/*     */   {
/* 236 */     if (FontUtilities.isLogging()) {
/*     */       String str;
/* 238 */       if ((paramFont2D instanceof CompositeFont)) {
/* 239 */         str = "Request to add " + paramFont2D.getFamilyName(null) + " with style " + paramInt + " to family " + this.familyName;
/*     */       }
/*     */       else {
/* 242 */         str = "Request to add " + paramFont2D + " with style " + paramInt + " to family " + this;
/*     */       }
/*     */       
/* 245 */       FontUtilities.getLogger().info(str);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 250 */     if ((paramFont2D.getRank() > this.familyRank) && (!isFromSameSource(paramFont2D))) {
/* 251 */       if (FontUtilities.isLogging())
/*     */       {
/* 253 */         FontUtilities.getLogger().warning("Rejecting adding " + paramFont2D + " of lower rank " + paramFont2D
/* 254 */           .getRank() + " to family " + this + " of rank " + this.familyRank);
/*     */       }
/*     */       
/*     */ 
/* 258 */       return;
/*     */     }
/*     */     
/* 261 */     switch (paramInt)
/*     */     {
/*     */     case 0: 
/* 264 */       if ((preferredWidth(paramFont2D)) && (closerWeight(this.plain, paramFont2D, paramInt))) {
/* 265 */         this.plain = paramFont2D;
/*     */       }
/*     */       
/*     */       break;
/*     */     case 1: 
/* 270 */       if ((preferredWidth(paramFont2D)) && (closerWeight(this.bold, paramFont2D, paramInt))) {
/* 271 */         this.bold = paramFont2D;
/*     */       }
/*     */       
/*     */       break;
/*     */     case 2: 
/* 276 */       if ((preferredWidth(paramFont2D)) && (closerWeight(this.italic, paramFont2D, paramInt))) {
/* 277 */         this.italic = paramFont2D;
/*     */       }
/*     */       
/*     */       break;
/*     */     case 3: 
/* 282 */       if ((preferredWidth(paramFont2D)) && (closerWeight(this.bolditalic, paramFont2D, paramInt))) {
/* 283 */         this.bolditalic = paramFont2D;
/*     */       }
/*     */       
/*     */       break;
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */   public Font2D getFontWithExactStyleMatch(int paramInt)
/*     */   {
/* 294 */     switch (paramInt)
/*     */     {
/*     */     case 0: 
/* 297 */       return this.plain;
/*     */     
/*     */     case 1: 
/* 300 */       return this.bold;
/*     */     
/*     */     case 2: 
/* 303 */       return this.italic;
/*     */     
/*     */     case 3: 
/* 306 */       return this.bolditalic;
/*     */     }
/*     */     
/* 309 */     return null;
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
/*     */   public Font2D getFont(int paramInt)
/*     */   {
/* 323 */     switch (paramInt)
/*     */     {
/*     */     case 0: 
/* 326 */       return this.plain;
/*     */     
/*     */     case 1: 
/* 329 */       if (this.bold != null)
/* 330 */         return this.bold;
/* 331 */       if ((this.plain != null) && (this.plain.canDoStyle(paramInt))) {
/* 332 */         return this.plain;
/*     */       }
/* 334 */       return null;
/*     */     
/*     */ 
/*     */     case 2: 
/* 338 */       if (this.italic != null)
/* 339 */         return this.italic;
/* 340 */       if ((this.plain != null) && (this.plain.canDoStyle(paramInt))) {
/* 341 */         return this.plain;
/*     */       }
/* 343 */       return null;
/*     */     
/*     */ 
/*     */     case 3: 
/* 347 */       if (this.bolditalic != null)
/* 348 */         return this.bolditalic;
/* 349 */       if ((this.bold != null) && (this.bold.canDoStyle(paramInt)))
/* 350 */         return this.bold;
/* 351 */       if ((this.italic != null) && (this.italic.canDoStyle(paramInt)))
/* 352 */         return this.italic;
/* 353 */       if ((this.plain != null) && (this.plain.canDoStyle(paramInt))) {
/* 354 */         return this.plain;
/*     */       }
/* 356 */       return null;
/*     */     }
/*     */     
/* 359 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Font2D getClosestStyle(int paramInt)
/*     */   {
/* 371 */     switch (paramInt)
/*     */     {
/*     */ 
/*     */     case 0: 
/* 375 */       if (this.bold != null)
/* 376 */         return this.bold;
/* 377 */       if (this.italic != null) {
/* 378 */         return this.italic;
/*     */       }
/* 380 */       return this.bolditalic;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     case 1: 
/* 386 */       if (this.plain != null)
/* 387 */         return this.plain;
/* 388 */       if (this.bolditalic != null) {
/* 389 */         return this.bolditalic;
/*     */       }
/* 391 */       return this.italic;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     case 2: 
/* 397 */       if (this.bolditalic != null)
/* 398 */         return this.bolditalic;
/* 399 */       if (this.plain != null) {
/* 400 */         return this.plain;
/*     */       }
/* 402 */       return this.bold;
/*     */     
/*     */ 
/*     */     case 3: 
/* 406 */       if (this.italic != null)
/* 407 */         return this.italic;
/* 408 */       if (this.bold != null) {
/* 409 */         return this.bold;
/*     */       }
/* 411 */       return this.plain;
/*     */     }
/*     */     
/* 414 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static synchronized void addLocaleNames(FontFamily paramFontFamily, String[] paramArrayOfString)
/*     */   {
/* 421 */     if (allLocaleNames == null) {
/* 422 */       allLocaleNames = new HashMap();
/*     */     }
/* 424 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 425 */       allLocaleNames.put(paramArrayOfString[i].toLowerCase(), paramFontFamily);
/*     */     }
/*     */   }
/*     */   
/*     */   public static synchronized FontFamily getLocaleFamily(String paramString) {
/* 430 */     if (allLocaleNames == null) {
/* 431 */       return null;
/*     */     }
/* 433 */     return (FontFamily)allLocaleNames.get(paramString.toLowerCase());
/*     */   }
/*     */   
/*     */   public static FontFamily[] getAllFontFamilies() {
/* 437 */     Collection localCollection = familyNameMap.values();
/* 438 */     return (FontFamily[])localCollection.toArray(new FontFamily[0]);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 442 */     return "Font family: " + this.familyName + " plain=" + this.plain + " bold=" + this.bold + " italic=" + this.italic + " bolditalic=" + this.bolditalic;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\FontFamily.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
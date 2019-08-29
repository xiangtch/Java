/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.NoninvertibleTransformException;
/*     */ import java.awt.geom.Point2D.Float;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import sun.misc.Unsafe;
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
/*     */ public class FileFontStrike
/*     */   extends PhysicalStrike
/*     */ {
/*     */   static final int INVISIBLE_GLYPHS = 65534;
/*     */   private FileFont fileFont;
/*     */   private static final int UNINITIALISED = 0;
/*     */   private static final int INTARRAY = 1;
/*     */   private static final int LONGARRAY = 2;
/*     */   private static final int SEGINTARRAY = 3;
/*     */   private static final int SEGLONGARRAY = 4;
/*  61 */   private volatile int glyphCacheFormat = 0;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int SEGSHIFT = 5;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int SEGSIZE = 32;
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean segmentedCache;
/*     */   
/*     */ 
/*     */ 
/*     */   private int[][] segIntGlyphImages;
/*     */   
/*     */ 
/*     */ 
/*     */   private long[][] segLongGlyphImages;
/*     */   
/*     */ 
/*     */ 
/*     */   private float[] horizontalAdvances;
/*     */   
/*     */ 
/*     */ 
/*     */   private float[][] segHorizontalAdvances;
/*     */   
/*     */ 
/*     */ 
/*     */   ConcurrentHashMap<Integer, Rectangle2D.Float> boundsMap;
/*     */   
/*     */ 
/*     */ 
/*     */   SoftReference<ConcurrentHashMap<Integer, Point2D.Float>> glyphMetricsMapRef;
/*     */   
/*     */ 
/*     */ 
/*     */   AffineTransform invertDevTx;
/*     */   
/*     */ 
/*     */ 
/*     */   boolean useNatives;
/*     */   
/*     */ 
/*     */ 
/*     */   NativeStrike[] nativeStrikes;
/*     */   
/*     */ 
/*     */   private int intPtSize;
/*     */   
/*     */ 
/* 115 */   private static boolean isXPorLater = false;
/*     */   
/* 117 */   static { if ((FontUtilities.isWindows) && (!FontUtilities.useT2K) && 
/* 118 */       (!GraphicsEnvironment.isHeadless())) {
/* 119 */       isXPorLater = initNative();
/*     */     }
/*     */   }
/*     */   
/*     */   FileFontStrike(FileFont paramFileFont, FontStrikeDesc paramFontStrikeDesc) {
/* 124 */     super(paramFileFont, paramFontStrikeDesc);
/* 125 */     this.fileFont = paramFileFont;
/*     */     
/* 127 */     if (paramFontStrikeDesc.style != paramFileFont.style)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 132 */       if (((paramFontStrikeDesc.style & 0x2) == 2) && ((paramFileFont.style & 0x2) == 0))
/*     */       {
/* 134 */         this.algoStyle = true;
/* 135 */         this.italic = 0.7F;
/*     */       }
/* 137 */       if (((paramFontStrikeDesc.style & 0x1) == 1) && ((paramFileFont.style & 0x1) == 0))
/*     */       {
/* 139 */         this.algoStyle = true;
/* 140 */         this.boldness = 1.33F;
/*     */       }
/*     */     }
/* 143 */     double[] arrayOfDouble = new double[4];
/* 144 */     AffineTransform localAffineTransform = paramFontStrikeDesc.glyphTx;
/* 145 */     localAffineTransform.getMatrix(arrayOfDouble);
/* 146 */     if ((!paramFontStrikeDesc.devTx.isIdentity()) && 
/* 147 */       (paramFontStrikeDesc.devTx.getType() != 1)) {
/*     */       try {
/* 149 */         this.invertDevTx = paramFontStrikeDesc.devTx.createInverse();
/*     */       }
/*     */       catch (NoninvertibleTransformException localNoninvertibleTransformException) {}
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
/* 169 */     boolean bool = (paramFontStrikeDesc.aaHint != 1) && (paramFileFont.familyName.startsWith("Amble"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 177 */     if ((Double.isNaN(arrayOfDouble[0])) || (Double.isNaN(arrayOfDouble[1])) || 
/* 178 */       (Double.isNaN(arrayOfDouble[2])) || (Double.isNaN(arrayOfDouble[3])) || 
/* 179 */       (paramFileFont.getScaler() == null)) {
/* 180 */       this.pScalerContext = NullFontScaler.getNullScalerContext();
/*     */     } else {
/* 182 */       this.pScalerContext = paramFileFont.getScaler().createScalerContext(arrayOfDouble, paramFontStrikeDesc.aaHint, paramFontStrikeDesc.fmHint, this.boldness, this.italic, bool);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 187 */     this.mapper = paramFileFont.getMapper();
/* 188 */     int i = this.mapper.getNumGlyphs();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 198 */     float f = (float)arrayOfDouble[3];
/* 199 */     int j = this.intPtSize = (int)f;
/* 200 */     int k = (localAffineTransform.getType() & 0x7C) == 0 ? 1 : 0;
/* 201 */     this.segmentedCache = ((i > 256) || ((i > 64) && ((k == 0) || (f != j) || (j < 6) || (j > 36))));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */     if (this.pScalerContext == 0L)
/*     */     {
/*     */ 
/*     */ 
/* 217 */       this.disposer = new FontStrikeDisposer(paramFileFont, paramFontStrikeDesc);
/* 218 */       initGlyphCache();
/* 219 */       this.pScalerContext = NullFontScaler.getNullScalerContext();
/* 220 */       SunFontManager.getInstance().deRegisterBadFont(paramFileFont);
/* 221 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 230 */     if ((FontUtilities.isWindows) && (isXPorLater) && (!FontUtilities.useT2K))
/*     */     {
/* 232 */       if ((!GraphicsEnvironment.isHeadless()) && (!paramFileFont.useJavaRasterizer) && ((paramFontStrikeDesc.aaHint == 4) || (paramFontStrikeDesc.aaHint == 5)) && (arrayOfDouble[1] == 0.0D) && (arrayOfDouble[2] == 0.0D) && (arrayOfDouble[0] == arrayOfDouble[3]) && (arrayOfDouble[0] >= 3.0D) && (arrayOfDouble[0] <= 100.0D))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 239 */         if (!((TrueTypeFont)paramFileFont).useEmbeddedBitmapsForSize(this.intPtSize)) {
/* 240 */           this.useNatives = true;
/*     */           break label636; } } }
/* 242 */     if ((paramFileFont.checkUseNatives()) && (paramFontStrikeDesc.aaHint == 0) && (!this.algoStyle))
/*     */     {
/*     */ 
/* 245 */       if ((arrayOfDouble[1] == 0.0D) && (arrayOfDouble[2] == 0.0D) && (arrayOfDouble[0] >= 6.0D) && (arrayOfDouble[0] <= 36.0D) && (arrayOfDouble[0] == arrayOfDouble[3]))
/*     */       {
/*     */ 
/* 248 */         this.useNatives = true;
/* 249 */         int m = paramFileFont.nativeFonts.length;
/* 250 */         this.nativeStrikes = new NativeStrike[m];
/*     */         
/*     */ 
/*     */ 
/* 254 */         for (int n = 0; n < m; n++) {
/* 255 */           this.nativeStrikes[n] = new NativeStrike(paramFileFont.nativeFonts[n], paramFontStrikeDesc, false);
/*     */         }
/*     */       }
/*     */     }
/*     */     label636:
/* 260 */     if ((FontUtilities.isLogging()) && (FontUtilities.isWindows))
/*     */     {
/* 262 */       FontUtilities.getLogger().info("Strike for " + paramFileFont + " at size = " + this.intPtSize + " use natives = " + this.useNatives + " useJavaRasteriser = " + paramFileFont.useJavaRasterizer + " AAHint = " + paramFontStrikeDesc.aaHint + " Has Embedded bitmaps = " + ((TrueTypeFont)paramFileFont)
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 268 */         .useEmbeddedBitmapsForSize(this.intPtSize));
/*     */     }
/* 270 */     this.disposer = new FontStrikeDisposer(paramFileFont, paramFontStrikeDesc, this.pScalerContext);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 278 */     double d = 48.0D;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 283 */     this.getImageWithAdvance = ((Math.abs(localAffineTransform.getScaleX()) <= d) && (Math.abs(localAffineTransform.getScaleY()) <= d) && (Math.abs(localAffineTransform.getShearX()) <= d) && (Math.abs(localAffineTransform.getShearY()) <= d));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 294 */     if (!this.getImageWithAdvance) { int i1;
/* 295 */       if (!this.segmentedCache) {
/* 296 */         this.horizontalAdvances = new float[i];
/*     */         
/* 298 */         for (i1 = 0; i1 < i; i1++) {
/* 299 */           this.horizontalAdvances[i1] = Float.MAX_VALUE;
/*     */         }
/*     */       } else {
/* 302 */         i1 = (i + 32 - 1) / 32;
/* 303 */         this.segHorizontalAdvances = new float[i1][];
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getNumGlyphs()
/*     */   {
/* 313 */     return this.fileFont.getNumGlyphs();
/*     */   }
/*     */   
/*     */   long getGlyphImageFromNative(int paramInt) {
/* 317 */     if (FontUtilities.isWindows) {
/* 318 */       return getGlyphImageFromWindows(paramInt);
/*     */     }
/* 320 */     return getGlyphImageFromX11(paramInt);
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
/*     */   long getGlyphImageFromWindows(int paramInt)
/*     */   {
/* 334 */     String str = this.fileFont.getFamilyName(null);
/*     */     
/* 336 */     int i = this.desc.style & 0x1 | this.desc.style & 0x2 | this.fileFont.getStyle();
/* 337 */     int j = this.intPtSize;
/*     */     
/* 339 */     long l = _getGlyphImageFromWindows(str, i, j, paramInt, this.desc.fmHint == 2);
/*     */     
/* 341 */     if (l != 0L)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 349 */       float f = getGlyphAdvance(paramInt, false);
/* 350 */       StrikeCache.unsafe.putFloat(l + StrikeCache.xAdvanceOffset, f);
/*     */       
/* 352 */       return l;
/*     */     }
/* 354 */     return this.fileFont.getGlyphImage(this.pScalerContext, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   long getGlyphImageFromX11(int paramInt)
/*     */   {
/* 361 */     char c = this.fileFont.glyphToCharMap[paramInt];
/* 362 */     for (int i = 0; i < this.nativeStrikes.length; i++) {
/* 363 */       CharToGlyphMapper localCharToGlyphMapper = this.fileFont.nativeFonts[i].getMapper();
/* 364 */       int j = localCharToGlyphMapper.charToGlyph(c) & 0xFFFF;
/* 365 */       if (j != localCharToGlyphMapper.getMissingGlyphCode()) {
/* 366 */         long l = this.nativeStrikes[i].getGlyphImagePtrNoCache(j);
/* 367 */         if (l != 0L) {
/* 368 */           return l;
/*     */         }
/*     */       }
/*     */     }
/* 372 */     return this.fileFont.getGlyphImage(this.pScalerContext, paramInt);
/*     */   }
/*     */   
/*     */   long getGlyphImagePtr(int paramInt) {
/* 376 */     if (paramInt >= 65534) {
/* 377 */       return StrikeCache.invisibleGlyphPtr;
/*     */     }
/* 379 */     long l = 0L;
/* 380 */     if ((l = getCachedGlyphPtr(paramInt)) != 0L) {
/* 381 */       return l;
/*     */     }
/* 383 */     if (this.useNatives) {
/* 384 */       l = getGlyphImageFromNative(paramInt);
/* 385 */       if ((l == 0L) && (FontUtilities.isLogging()))
/*     */       {
/* 387 */         FontUtilities.getLogger().info("Strike for " + this.fileFont + " at size = " + this.intPtSize + " couldn't get native glyph for code = " + paramInt);
/*     */       }
/*     */     }
/*     */     
/* 391 */     if (l == 0L) {
/* 392 */       l = this.fileFont.getGlyphImage(this.pScalerContext, paramInt);
/*     */     }
/*     */     
/* 395 */     return setCachedGlyphPtr(paramInt, l);
/*     */   }
/*     */   
/*     */ 
/*     */   void getGlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt)
/*     */   {
/* 401 */     for (int i = 0; i < paramInt; i++) {
/* 402 */       int j = paramArrayOfInt[i];
/* 403 */       if (j >= 65534) {
/* 404 */         paramArrayOfLong[i] = StrikeCache.invisibleGlyphPtr;
/*     */       }
/* 406 */       else if ((paramArrayOfLong[i] = getCachedGlyphPtr(j)) == 0L)
/*     */       {
/*     */ 
/* 409 */         long l = 0L;
/* 410 */         if (this.useNatives)
/* 411 */           l = getGlyphImageFromNative(j);
/* 412 */         if (l == 0L) {
/* 413 */           l = this.fileFont.getGlyphImage(this.pScalerContext, j);
/*     */         }
/*     */         
/* 416 */         paramArrayOfLong[i] = setCachedGlyphPtr(j, l);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   int getSlot0GlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt)
/*     */   {
/* 425 */     int i = 0;
/*     */     
/* 427 */     for (int j = 0; j < paramInt; j++) {
/* 428 */       int k = paramArrayOfInt[j];
/* 429 */       if (k >>> 24 != 0) {
/* 430 */         return i;
/*     */       }
/* 432 */       i++;
/*     */       
/* 434 */       if (k >= 65534) {
/* 435 */         paramArrayOfLong[j] = StrikeCache.invisibleGlyphPtr;
/*     */       }
/* 437 */       else if ((paramArrayOfLong[j] = getCachedGlyphPtr(k)) == 0L)
/*     */       {
/*     */ 
/* 440 */         long l = 0L;
/* 441 */         if (this.useNatives) {
/* 442 */           l = getGlyphImageFromNative(k);
/*     */         }
/* 444 */         if (l == 0L) {
/* 445 */           l = this.fileFont.getGlyphImage(this.pScalerContext, k);
/*     */         }
/*     */         
/* 448 */         paramArrayOfLong[j] = setCachedGlyphPtr(k, l);
/*     */       }
/*     */     }
/* 451 */     return i;
/*     */   }
/*     */   
/*     */   long getCachedGlyphPtr(int paramInt)
/*     */   {
/*     */     try {
/* 457 */       return getCachedGlyphPtrInternal(paramInt);
/*     */     }
/*     */     catch (Exception localException) {
/* 460 */       NullFontScaler localNullFontScaler = (NullFontScaler)FontScaler.getNullScaler();
/* 461 */       long l = NullFontScaler.getNullScalerContext();
/* 462 */       return localNullFontScaler.getGlyphImage(l, paramInt);
/*     */     } }
/*     */   
/*     */   private long getCachedGlyphPtrInternal(int paramInt) { int i;
/*     */     int j;
/* 467 */     switch (this.glyphCacheFormat) {
/*     */     case 1: 
/* 469 */       return this.intGlyphImages[paramInt] & 0xFFFFFFFF;
/*     */     case 3: 
/* 471 */       i = paramInt >> 5;
/* 472 */       if (this.segIntGlyphImages[i] != null) {
/* 473 */         j = paramInt % 32;
/* 474 */         return this.segIntGlyphImages[i][j] & 0xFFFFFFFF;
/*     */       }
/* 476 */       return 0L;
/*     */     
/*     */     case 2: 
/* 479 */       return this.longGlyphImages[paramInt];
/*     */     case 4: 
/* 481 */       i = paramInt >> 5;
/* 482 */       if (this.segLongGlyphImages[i] != null) {
/* 483 */         j = paramInt % 32;
/* 484 */         return this.segLongGlyphImages[i][j];
/*     */       }
/* 486 */       return 0L;
/*     */     }
/*     */     
/*     */     
/* 490 */     return 0L;
/*     */   }
/*     */   
/*     */   private synchronized long setCachedGlyphPtr(int paramInt, long paramLong) {
/*     */     try {
/* 495 */       return setCachedGlyphPtrInternal(paramInt, paramLong);
/*     */     } catch (Exception localException) {
/* 497 */       switch (this.glyphCacheFormat) {
/*     */       case 1: 
/*     */       case 3: 
/* 500 */         StrikeCache.freeIntPointer((int)paramLong);
/* 501 */         break;
/*     */       case 2: 
/*     */       case 4: 
/* 504 */         StrikeCache.freeLongPointer(paramLong);
/*     */       }
/*     */       
/*     */       
/* 508 */       NullFontScaler localNullFontScaler = (NullFontScaler)FontScaler.getNullScaler();
/* 509 */       long l = NullFontScaler.getNullScalerContext();
/* 510 */       return localNullFontScaler.getGlyphImage(l, paramInt);
/*     */     } }
/*     */   
/*     */   private long setCachedGlyphPtrInternal(int paramInt, long paramLong) { int i;
/*     */     int j;
/* 515 */     switch (this.glyphCacheFormat) {
/*     */     case 1: 
/* 517 */       if (this.intGlyphImages[paramInt] == 0) {
/* 518 */         this.intGlyphImages[paramInt] = ((int)paramLong);
/* 519 */         return paramLong;
/*     */       }
/* 521 */       StrikeCache.freeIntPointer((int)paramLong);
/* 522 */       return this.intGlyphImages[paramInt] & 0xFFFFFFFF;
/*     */     
/*     */ 
/*     */     case 3: 
/* 526 */       i = paramInt >> 5;
/* 527 */       j = paramInt % 32;
/* 528 */       if (this.segIntGlyphImages[i] == null) {
/* 529 */         this.segIntGlyphImages[i] = new int[32];
/*     */       }
/* 531 */       if (this.segIntGlyphImages[i][j] == 0) {
/* 532 */         this.segIntGlyphImages[i][j] = ((int)paramLong);
/* 533 */         return paramLong;
/*     */       }
/* 535 */       StrikeCache.freeIntPointer((int)paramLong);
/* 536 */       return this.segIntGlyphImages[i][j] & 0xFFFFFFFF;
/*     */     
/*     */ 
/*     */     case 2: 
/* 540 */       if (this.longGlyphImages[paramInt] == 0L) {
/* 541 */         this.longGlyphImages[paramInt] = paramLong;
/* 542 */         return paramLong;
/*     */       }
/* 544 */       StrikeCache.freeLongPointer(paramLong);
/* 545 */       return this.longGlyphImages[paramInt];
/*     */     
/*     */ 
/*     */     case 4: 
/* 549 */       i = paramInt >> 5;
/* 550 */       j = paramInt % 32;
/* 551 */       if (this.segLongGlyphImages[i] == null) {
/* 552 */         this.segLongGlyphImages[i] = new long[32];
/*     */       }
/* 554 */       if (this.segLongGlyphImages[i][j] == 0L) {
/* 555 */         this.segLongGlyphImages[i][j] = paramLong;
/* 556 */         return paramLong;
/*     */       }
/* 558 */       StrikeCache.freeLongPointer(paramLong);
/* 559 */       return this.segLongGlyphImages[i][j];
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 567 */     initGlyphCache();
/* 568 */     return setCachedGlyphPtr(paramInt, paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */   private synchronized void initGlyphCache()
/*     */   {
/* 574 */     int i = this.mapper.getNumGlyphs();
/* 575 */     int j = 0;
/* 576 */     if (this.segmentedCache) {
/* 577 */       int k = (i + 32 - 1) / 32;
/* 578 */       if (longAddresses) {
/* 579 */         j = 4;
/* 580 */         this.segLongGlyphImages = new long[k][];
/* 581 */         this.disposer.segLongGlyphImages = this.segLongGlyphImages;
/*     */       } else {
/* 583 */         j = 3;
/* 584 */         this.segIntGlyphImages = new int[k][];
/* 585 */         this.disposer.segIntGlyphImages = this.segIntGlyphImages;
/*     */       }
/*     */     }
/* 588 */     else if (longAddresses) {
/* 589 */       j = 2;
/* 590 */       this.longGlyphImages = new long[i];
/* 591 */       this.disposer.longGlyphImages = this.longGlyphImages;
/*     */     } else {
/* 593 */       j = 1;
/* 594 */       this.intGlyphImages = new int[i];
/* 595 */       this.disposer.intGlyphImages = this.intGlyphImages;
/*     */     }
/*     */     
/* 598 */     this.glyphCacheFormat = j;
/*     */   }
/*     */   
/*     */   float getGlyphAdvance(int paramInt) {
/* 602 */     return getGlyphAdvance(paramInt, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private float getGlyphAdvance(int paramInt, boolean paramBoolean)
/*     */   {
/* 613 */     if (paramInt >= 65534) {
/* 614 */       return 0.0F;
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
/*     */     float f;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 644 */     if (this.horizontalAdvances != null) {
/* 645 */       f = this.horizontalAdvances[paramInt];
/* 646 */       if (f != Float.MAX_VALUE) {
/* 647 */         if ((!paramBoolean) && (this.invertDevTx != null)) {
/* 648 */           Point2D.Float localFloat1 = new Point2D.Float(f, 0.0F);
/* 649 */           this.desc.devTx.deltaTransform(localFloat1, localFloat1);
/* 650 */           return localFloat1.x;
/*     */         }
/* 652 */         return f;
/*     */       }
/*     */     }
/* 655 */     else if ((this.segmentedCache) && (this.segHorizontalAdvances != null)) {
/* 656 */       int i = paramInt >> 5;
/* 657 */       float[] arrayOfFloat = this.segHorizontalAdvances[i];
/* 658 */       if (arrayOfFloat != null) {
/* 659 */         f = arrayOfFloat[(paramInt % 32)];
/* 660 */         if (f != Float.MAX_VALUE) {
/* 661 */           if ((!paramBoolean) && (this.invertDevTx != null)) {
/* 662 */             Point2D.Float localFloat3 = new Point2D.Float(f, 0.0F);
/* 663 */             this.desc.devTx.deltaTransform(localFloat3, localFloat3);
/* 664 */             return localFloat3.x;
/*     */           }
/* 666 */           return f;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 672 */     if ((!paramBoolean) && (this.invertDevTx != null)) {
/* 673 */       Point2D.Float localFloat2 = new Point2D.Float();
/* 674 */       this.fileFont.getGlyphMetrics(this.pScalerContext, paramInt, localFloat2);
/* 675 */       return localFloat2.x;
/*     */     }
/*     */     
/* 678 */     if ((this.invertDevTx != null) || (!paramBoolean))
/*     */     {
/*     */ 
/*     */ 
/* 682 */       f = getGlyphMetrics(paramInt, paramBoolean).x;
/*     */     } else {
/*     */       long l;
/* 685 */       if (this.getImageWithAdvance)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 691 */         l = getGlyphImagePtr(paramInt);
/*     */       } else {
/* 693 */         l = getCachedGlyphPtr(paramInt);
/*     */       }
/* 695 */       if (l != 0L)
/*     */       {
/* 697 */         f = StrikeCache.unsafe.getFloat(l + StrikeCache.xAdvanceOffset);
/*     */       }
/*     */       else {
/* 700 */         f = this.fileFont.getGlyphAdvance(this.pScalerContext, paramInt);
/*     */       }
/*     */     }
/*     */     
/* 704 */     if (this.horizontalAdvances != null) {
/* 705 */       this.horizontalAdvances[paramInt] = f;
/* 706 */     } else if ((this.segmentedCache) && (this.segHorizontalAdvances != null)) {
/* 707 */       int j = paramInt >> 5;
/* 708 */       int k = paramInt % 32;
/* 709 */       if (this.segHorizontalAdvances[j] == null) {
/* 710 */         this.segHorizontalAdvances[j] = new float[32];
/* 711 */         for (int m = 0; m < 32; m++) {
/* 712 */           this.segHorizontalAdvances[j][m] = Float.MAX_VALUE;
/*     */         }
/*     */       }
/* 715 */       this.segHorizontalAdvances[j][k] = f;
/*     */     }
/* 717 */     return f;
/*     */   }
/*     */   
/*     */   float getCodePointAdvance(int paramInt) {
/* 721 */     return getGlyphAdvance(this.mapper.charToGlyph(paramInt));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle)
/*     */   {
/* 730 */     long l = getGlyphImagePtr(paramInt);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 736 */     if (l == 0L) {
/* 737 */       paramRectangle.x = ((int)Math.floor(paramFloat.x));
/* 738 */       paramRectangle.y = ((int)Math.floor(paramFloat.y));
/* 739 */       paramRectangle.width = (paramRectangle.height = 0);
/* 740 */       return;
/*     */     }
/*     */     
/* 743 */     float f1 = StrikeCache.unsafe.getFloat(l + StrikeCache.topLeftXOffset);
/* 744 */     float f2 = StrikeCache.unsafe.getFloat(l + StrikeCache.topLeftYOffset);
/*     */     
/* 746 */     paramRectangle.x = ((int)Math.floor(paramFloat.x + f1));
/* 747 */     paramRectangle.y = ((int)Math.floor(paramFloat.y + f2));
/*     */     
/* 749 */     paramRectangle.width = (StrikeCache.unsafe.getShort(l + StrikeCache.widthOffset) & 0xFFFF);
/*     */     
/* 751 */     paramRectangle.height = (StrikeCache.unsafe.getShort(l + StrikeCache.heightOffset) & 0xFFFF);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 759 */     if (((this.desc.aaHint == 4) || (this.desc.aaHint == 5)) && (f1 <= -2.0F))
/*     */     {
/*     */ 
/* 762 */       int i = getGlyphImageMinX(l, paramRectangle.x);
/* 763 */       if (i > paramRectangle.x) {
/* 764 */         paramRectangle.x += 1;
/* 765 */         paramRectangle.width -= 1;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private int getGlyphImageMinX(long paramLong, int paramInt)
/*     */   {
/* 772 */     int i = StrikeCache.unsafe.getChar(paramLong + StrikeCache.widthOffset);
/* 773 */     int j = StrikeCache.unsafe.getChar(paramLong + StrikeCache.heightOffset);
/*     */     
/* 775 */     int k = StrikeCache.unsafe.getChar(paramLong + StrikeCache.rowBytesOffset);
/*     */     
/* 777 */     if (k == i) {
/* 778 */       return paramInt;
/*     */     }
/*     */     
/*     */ 
/* 782 */     long l = StrikeCache.unsafe.getAddress(paramLong + StrikeCache.pixelDataOffset);
/*     */     
/* 784 */     if (l == 0L) {
/* 785 */       return paramInt;
/*     */     }
/*     */     
/* 788 */     for (int m = 0; m < j; m++) {
/* 789 */       for (int n = 0; n < 3; n++) {
/* 790 */         if (StrikeCache.unsafe.getByte(l + m * k + n) != 0) {
/* 791 */           return paramInt;
/*     */         }
/*     */       }
/*     */     }
/* 795 */     return paramInt + 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   StrikeMetrics getFontMetrics()
/*     */   {
/* 802 */     if (this.strikeMetrics == null)
/*     */     {
/* 804 */       this.strikeMetrics = this.fileFont.getFontMetrics(this.pScalerContext);
/* 805 */       if (this.invertDevTx != null) {
/* 806 */         this.strikeMetrics.convertToUserSpace(this.invertDevTx);
/*     */       }
/*     */     }
/* 809 */     return this.strikeMetrics;
/*     */   }
/*     */   
/*     */   Point2D.Float getGlyphMetrics(int paramInt) {
/* 813 */     return getGlyphMetrics(paramInt, true);
/*     */   }
/*     */   
/*     */   private Point2D.Float getGlyphMetrics(int paramInt, boolean paramBoolean) {
/* 817 */     Point2D.Float localFloat1 = new Point2D.Float();
/*     */     
/*     */ 
/* 820 */     if (paramInt >= 65534) {
/* 821 */       return localFloat1;
/*     */     }
/*     */     long l;
/* 824 */     if ((this.getImageWithAdvance) && (paramBoolean))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 830 */       l = getGlyphImagePtr(paramInt);
/*     */     } else {
/* 832 */       l = getCachedGlyphPtr(paramInt);
/*     */     }
/* 834 */     if (l != 0L) {
/* 835 */       localFloat1 = new Point2D.Float();
/*     */       
/* 837 */       localFloat1.x = StrikeCache.unsafe.getFloat(l + StrikeCache.xAdvanceOffset);
/*     */       
/* 839 */       localFloat1.y = StrikeCache.unsafe.getFloat(l + StrikeCache.yAdvanceOffset);
/*     */       
/*     */ 
/*     */ 
/* 843 */       if (this.invertDevTx != null) {
/* 844 */         this.invertDevTx.deltaTransform(localFloat1, localFloat1);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 854 */       Integer localInteger = Integer.valueOf(paramInt);
/* 855 */       Point2D.Float localFloat2 = null;
/* 856 */       ConcurrentHashMap localConcurrentHashMap = null;
/* 857 */       if (this.glyphMetricsMapRef != null) {
/* 858 */         localConcurrentHashMap = (ConcurrentHashMap)this.glyphMetricsMapRef.get();
/*     */       }
/* 860 */       if (localConcurrentHashMap != null) {
/* 861 */         localFloat2 = (Point2D.Float)localConcurrentHashMap.get(localInteger);
/* 862 */         if (localFloat2 != null) {
/* 863 */           localFloat1.x = localFloat2.x;
/* 864 */           localFloat1.y = localFloat2.y;
/*     */           
/* 866 */           return localFloat1;
/*     */         }
/*     */       }
/* 869 */       if (localFloat2 == null) {
/* 870 */         this.fileFont.getGlyphMetrics(this.pScalerContext, paramInt, localFloat1);
/*     */         
/*     */ 
/*     */ 
/* 874 */         if (this.invertDevTx != null) {
/* 875 */           this.invertDevTx.deltaTransform(localFloat1, localFloat1);
/*     */         }
/* 877 */         localFloat2 = new Point2D.Float(localFloat1.x, localFloat1.y);
/*     */         
/*     */ 
/*     */ 
/* 881 */         if (localConcurrentHashMap == null) {
/* 882 */           localConcurrentHashMap = new ConcurrentHashMap();
/*     */           
/* 884 */           this.glyphMetricsMapRef = new SoftReference(localConcurrentHashMap);
/*     */         }
/*     */         
/*     */ 
/* 888 */         localConcurrentHashMap.put(localInteger, localFloat2);
/*     */       }
/*     */     }
/* 891 */     return localFloat1;
/*     */   }
/*     */   
/*     */   Point2D.Float getCharMetrics(char paramChar) {
/* 895 */     return getGlyphMetrics(this.mapper.charToGlyph(paramChar));
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
/*     */   private WeakReference<ConcurrentHashMap<Integer, GeneralPath>> outlineMapRef;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Rectangle2D.Float getGlyphOutlineBounds(int paramInt)
/*     */   {
/* 917 */     if (this.boundsMap == null) {
/* 918 */       this.boundsMap = new ConcurrentHashMap();
/*     */     }
/*     */     
/* 921 */     Integer localInteger = Integer.valueOf(paramInt);
/* 922 */     Rectangle2D.Float localFloat = (Rectangle2D.Float)this.boundsMap.get(localInteger);
/*     */     
/* 924 */     if (localFloat == null) {
/* 925 */       localFloat = this.fileFont.getGlyphOutlineBounds(this.pScalerContext, paramInt);
/* 926 */       this.boundsMap.put(localInteger, localFloat);
/*     */     }
/* 928 */     return localFloat;
/*     */   }
/*     */   
/*     */   public Rectangle2D getOutlineBounds(int paramInt) {
/* 932 */     return this.fileFont.getGlyphOutlineBounds(this.pScalerContext, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2)
/*     */   {
/* 940 */     GeneralPath localGeneralPath = null;
/* 941 */     ConcurrentHashMap localConcurrentHashMap = null;
/*     */     
/* 943 */     if (this.outlineMapRef != null) {
/* 944 */       localConcurrentHashMap = (ConcurrentHashMap)this.outlineMapRef.get();
/* 945 */       if (localConcurrentHashMap != null) {
/* 946 */         localGeneralPath = (GeneralPath)localConcurrentHashMap.get(Integer.valueOf(paramInt));
/*     */       }
/*     */     }
/*     */     
/* 950 */     if (localGeneralPath == null) {
/* 951 */       localGeneralPath = this.fileFont.getGlyphOutline(this.pScalerContext, paramInt, 0.0F, 0.0F);
/* 952 */       if (localConcurrentHashMap == null) {
/* 953 */         localConcurrentHashMap = new ConcurrentHashMap();
/* 954 */         this.outlineMapRef = new WeakReference(localConcurrentHashMap);
/*     */       }
/*     */       
/*     */ 
/* 958 */       localConcurrentHashMap.put(Integer.valueOf(paramInt), localGeneralPath);
/*     */     }
/* 960 */     localGeneralPath = (GeneralPath)localGeneralPath.clone();
/* 961 */     if ((paramFloat1 != 0.0F) || (paramFloat2 != 0.0F)) {
/* 962 */       localGeneralPath.transform(AffineTransform.getTranslateInstance(paramFloat1, paramFloat2));
/*     */     }
/* 964 */     return localGeneralPath;
/*     */   }
/*     */   
/*     */   GeneralPath getGlyphVectorOutline(int[] paramArrayOfInt, float paramFloat1, float paramFloat2) {
/* 968 */     return this.fileFont.getGlyphVectorOutline(this.pScalerContext, paramArrayOfInt, paramArrayOfInt.length, paramFloat1, paramFloat2);
/*     */   }
/*     */   
/*     */   protected void adjustPoint(Point2D.Float paramFloat)
/*     */   {
/* 973 */     if (this.invertDevTx != null) {
/* 974 */       this.invertDevTx.deltaTransform(paramFloat, paramFloat);
/*     */     }
/*     */   }
/*     */   
/*     */   private static native boolean initNative();
/*     */   
/*     */   private native long _getGlyphImageFromWindows(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\FileFontStrike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
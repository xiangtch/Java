/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.FontFormatException;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileChannel.MapMode;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import sun.java2d.Disposer;
/*     */ import sun.java2d.DisposerRecord;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Type1Font
/*     */   extends FileFont
/*     */ {
/*     */   private static class T1DisposerRecord
/*     */     implements DisposerRecord
/*     */   {
/*  81 */     String fileName = null;
/*     */     
/*     */     T1DisposerRecord(String paramString) {
/*  84 */       this.fileName = paramString;
/*     */     }
/*     */     
/*     */     public synchronized void dispose() {
/*  88 */       AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Object run()
/*     */         {
/*  92 */           if (T1DisposerRecord.this.fileName != null) {
/*  93 */             new File(T1DisposerRecord.this.fileName).delete();
/*     */           }
/*  95 */           return null;
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/* 101 */   WeakReference bufferRef = new WeakReference(null);
/*     */   
/* 103 */   private String psName = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 109 */   private static HashMap styleAbbreviationsMapping = new HashMap();
/* 110 */   private static HashSet styleNameTokes = new HashSet();
/*     */   private static final int PSEOFTOKEN = 0;
/*     */   private static final int PSNAMETOKEN = 1;
/*     */   private static final int PSSTRINGTOKEN = 2;
/*     */   
/*     */   static
/*     */   {
/* 117 */     String[] arrayOfString1 = { "Black", "Bold", "Book", "Demi", "Heavy", "Light", "Meduium", "Nord", "Poster", "Regular", "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", "Oblique", "Upright", "Sloped", "Semi", "Ultra", "Extra", "Alternate", "Alternate", "Deutsche Fraktur", "Expert", "Inline", "Ornaments", "Outline", "Roman", "Rounded", "Script", "Shaded", "Swash", "Titling", "Typewriter" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 124 */     String[] arrayOfString2 = { "Blk", "Bd", "Bk", "Dm", "Hv", "Lt", "Md", "Nd", "Po", "Rg", "Su", "Th", "Cm", "Cn", "Ct", "Ex", "Nr", "Ic", "It", "Ks", "Obl", "Up", "Sl", "Sm", "Ult", "X", "A", "Alt", "Dfr", "Exp", "In", "Or", "Ou", "Rm", "Rd", "Scr", "Sh", "Sw", "Ti", "Typ" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 134 */     String[] arrayOfString3 = { "Black", "Bold", "Book", "Demi", "Heavy", "Light", "Medium", "Nord", "Poster", "Regular", "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", "Oblique", "Upright", "Sloped", "Slanted", "Semi", "Ultra", "Extra" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 140 */     for (int i = 0; i < arrayOfString1.length; i++) {
/* 141 */       styleAbbreviationsMapping.put(arrayOfString2[i], arrayOfString1[i]);
/*     */     }
/* 143 */     for (i = 0; i < arrayOfString3.length; i++) {
/* 144 */       styleNameTokes.add(arrayOfString3[i]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Type1Font(String paramString, Object paramObject)
/*     */     throws FontFormatException
/*     */   {
/* 157 */     this(paramString, paramObject, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Type1Font(String paramString, Object paramObject, boolean paramBoolean)
/*     */     throws FontFormatException
/*     */   {
/* 169 */     super(paramString, paramObject);
/* 170 */     this.fontRank = 4;
/* 171 */     this.checkedNatives = true;
/*     */     try {
/* 173 */       verify();
/*     */     } catch (Throwable localThrowable) {
/* 175 */       if (paramBoolean) {
/* 176 */         T1DisposerRecord localT1DisposerRecord = new T1DisposerRecord(paramString);
/* 177 */         Disposer.addObjectRecord(this.bufferRef, localT1DisposerRecord);
/* 178 */         this.bufferRef = null;
/*     */       }
/* 180 */       if ((localThrowable instanceof FontFormatException)) {
/* 181 */         throw ((FontFormatException)localThrowable);
/*     */       }
/* 183 */       throw new FontFormatException("Unexpected runtime exception.");
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized ByteBuffer getBuffer() throws FontFormatException
/*     */   {
/* 189 */     MappedByteBuffer localMappedByteBuffer = (MappedByteBuffer)this.bufferRef.get();
/* 190 */     if (localMappedByteBuffer == null)
/*     */     {
/*     */       try
/*     */       {
/* 194 */         RandomAccessFile localRandomAccessFile = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/*     */             try {
/* 198 */               return new RandomAccessFile(Type1Font.this.platName, "r");
/*     */             }
/*     */             catch (FileNotFoundException localFileNotFoundException) {}
/* 201 */             return null;
/*     */           }
/* 203 */         });
/* 204 */         FileChannel localFileChannel = localRandomAccessFile.getChannel();
/* 205 */         this.fileSize = ((int)localFileChannel.size());
/* 206 */         localMappedByteBuffer = localFileChannel.map(MapMode.READ_ONLY, 0L, this.fileSize);
/* 207 */         localMappedByteBuffer.position(0);
/* 208 */         this.bufferRef = new WeakReference(localMappedByteBuffer);
/* 209 */         localFileChannel.close();
/*     */       } catch (NullPointerException localNullPointerException) {
/* 211 */         throw new FontFormatException(localNullPointerException.toString());
/*     */ 
/*     */       }
/*     */       catch (ClosedChannelException localClosedChannelException)
/*     */       {
/* 216 */         Thread.interrupted();
/* 217 */         return getBuffer();
/*     */       } catch (IOException localIOException) {
/* 219 */         throw new FontFormatException(localIOException.toString());
/*     */       }
/*     */     }
/* 222 */     return localMappedByteBuffer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void readFile(ByteBuffer paramByteBuffer)
/*     */   {
/* 230 */     RandomAccessFile localRandomAccessFile = null;
/*     */     
/*     */     try
/*     */     {
/* 234 */       localRandomAccessFile = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Object run() {
/*     */           try {
/* 238 */             return new RandomAccessFile(Type1Font.this.platName, "r");
/*     */           }
/*     */           catch (FileNotFoundException localFileNotFoundException) {}
/* 241 */           return null;
/*     */         }
/* 243 */       });
/* 244 */       FileChannel localFileChannel = localRandomAccessFile.getChannel();
/* 245 */       while ((paramByteBuffer.remaining() > 0) && (localFileChannel.read(paramByteBuffer) != -1)) {}
/*     */       return;
/*     */     } catch (NullPointerException localNullPointerException) {}catch (ClosedChannelException localClosedChannelException) {
/*     */       try {
/* 249 */         if (localRandomAccessFile != null) {
/* 250 */           localRandomAccessFile.close();
/* 251 */           localRandomAccessFile = null;
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException6) {}
/*     */       
/*     */ 
/*     */ 
/* 258 */       Thread.interrupted();
/* 259 */       readFile(paramByteBuffer);
/*     */     }
/*     */     catch (IOException localIOException4) {}finally {
/* 262 */       if (localRandomAccessFile != null) {
/*     */         try {
/* 264 */           localRandomAccessFile.close();
/*     */         }
/*     */         catch (IOException localIOException7) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized ByteBuffer readBlock(int paramInt1, int paramInt2) {
/* 272 */     ByteBuffer localByteBuffer = null;
/*     */     try {
/* 274 */       localByteBuffer = getBuffer();
/* 275 */       if (paramInt1 > this.fileSize) {
/* 276 */         paramInt1 = this.fileSize;
/*     */       }
/* 278 */       localByteBuffer.position(paramInt1);
/* 279 */       return localByteBuffer.slice();
/*     */     } catch (FontFormatException localFontFormatException) {}
/* 281 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void verify()
/*     */     throws FontFormatException
/*     */   {
/* 291 */     ByteBuffer localByteBuffer = getBuffer();
/* 292 */     if (localByteBuffer.capacity() < 6) {
/* 293 */       throw new FontFormatException("short file");
/*     */     }
/* 295 */     int i = localByteBuffer.get(0) & 0xFF;
/* 296 */     if ((localByteBuffer.get(0) & 0xFF) == 128) {
/* 297 */       verifyPFB(localByteBuffer);
/* 298 */       localByteBuffer.position(6);
/*     */     } else {
/* 300 */       verifyPFA(localByteBuffer);
/* 301 */       localByteBuffer.position(0);
/*     */     }
/* 303 */     initNames(localByteBuffer);
/* 304 */     if ((this.familyName == null) || (this.fullName == null)) {
/* 305 */       throw new FontFormatException("Font name not found");
/*     */     }
/* 307 */     setStyle();
/*     */   }
/*     */   
/*     */   public int getFileSize() {
/* 311 */     if (this.fileSize == 0) {
/*     */       try {
/* 313 */         getBuffer();
/*     */       }
/*     */       catch (FontFormatException localFontFormatException) {}
/*     */     }
/* 317 */     return this.fileSize;
/*     */   }
/*     */   
/*     */   private void verifyPFA(ByteBuffer paramByteBuffer) throws FontFormatException {
/* 321 */     if (paramByteBuffer.getShort() != 9505) {
/* 322 */       throw new FontFormatException("bad pfa font");
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
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initNames(ByteBuffer paramByteBuffer)
/*     */     throws FontFormatException
/*     */   {
/* 367 */     int i = 0;
/* 368 */     Object localObject = null;
/*     */     
/*     */     try
/*     */     {
/* 372 */       while (((this.fullName == null) || (this.familyName == null) || (this.psName == null) || (localObject == null)) && (i == 0)) {
/* 373 */         int j = nextTokenType(paramByteBuffer);
/* 374 */         if (j == 1) {
/* 375 */           int k = paramByteBuffer.position();
/* 376 */           if (paramByteBuffer.get(k) == 70) {
/* 377 */             String str2 = getSimpleToken(paramByteBuffer);
/* 378 */             if ("FullName".equals(str2)) {
/* 379 */               if (nextTokenType(paramByteBuffer) == 2) {
/* 380 */                 this.fullName = getString(paramByteBuffer);
/*     */               }
/* 382 */             } else if ("FamilyName".equals(str2)) {
/* 383 */               if (nextTokenType(paramByteBuffer) == 2) {
/* 384 */                 this.familyName = getString(paramByteBuffer);
/*     */               }
/* 386 */             } else if ("FontName".equals(str2)) {
/* 387 */               if (nextTokenType(paramByteBuffer) == 1) {
/* 388 */                 this.psName = getSimpleToken(paramByteBuffer);
/*     */               }
/* 390 */             } else if ("FontType".equals(str2))
/*     */             {
/*     */ 
/*     */ 
/* 394 */               String str3 = getSimpleToken(paramByteBuffer);
/* 395 */               if ("def".equals(getSimpleToken(paramByteBuffer))) {
/* 396 */                 localObject = str3;
/*     */               }
/*     */             }
/*     */           } else {
/* 400 */             while (paramByteBuffer.get() > 32) {}
/*     */           }
/* 402 */         } else if (j == 0) {
/* 403 */           i = 1;
/*     */         }
/*     */       }
/*     */     } catch (Exception localException) {
/* 407 */       throw new FontFormatException(localException.toString());
/*     */     }
/*     */     
/*     */ 
/* 411 */     if (!"1".equals(localObject)) {
/* 412 */       throw new FontFormatException("Unsupported font type");
/*     */     }
/*     */     
/* 415 */     if (this.psName == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 421 */       paramByteBuffer.position(0);
/* 422 */       if (paramByteBuffer.getShort() != 9505)
/*     */       {
/* 424 */         paramByteBuffer.position(8);
/*     */       }
/*     */       
/*     */ 
/* 428 */       String str1 = getSimpleToken(paramByteBuffer);
/* 429 */       if ((!str1.startsWith("FontType1-")) && (!str1.startsWith("PS-AdobeFont-"))) {
/* 430 */         throw new FontFormatException("Unsupported font format [" + str1 + "]");
/*     */       }
/* 432 */       this.psName = getSimpleToken(paramByteBuffer);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 438 */     if (i != 0)
/*     */     {
/* 440 */       if (this.fullName != null) {
/* 441 */         this.familyName = fullName2FamilyName(this.fullName);
/* 442 */       } else if (this.familyName != null) {
/* 443 */         this.fullName = this.familyName;
/*     */       } else {
/* 445 */         this.fullName = psName2FullName(this.psName);
/* 446 */         this.familyName = psName2FamilyName(this.psName);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String fullName2FamilyName(String paramString)
/*     */   {
/* 458 */     int j = paramString.length();
/*     */     
/* 460 */     while (j > 0) {
/* 461 */       int i = j - 1;
/* 462 */       while ((i > 0) && (paramString.charAt(i) != ' ')) {
/* 463 */         i--;
/*     */       }
/*     */       
/* 466 */       if (!isStyleToken(paramString.substring(i + 1, j))) {
/* 467 */         return paramString.substring(0, j);
/*     */       }
/* 469 */       j = i;
/*     */     }
/*     */     
/* 472 */     return paramString;
/*     */   }
/*     */   
/*     */   private String expandAbbreviation(String paramString) {
/* 476 */     if (styleAbbreviationsMapping.containsKey(paramString))
/* 477 */       return (String)styleAbbreviationsMapping.get(paramString);
/* 478 */     return paramString;
/*     */   }
/*     */   
/*     */   private boolean isStyleToken(String paramString) {
/* 482 */     return styleNameTokes.contains(paramString);
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
/*     */   private String psName2FullName(String paramString)
/*     */   {
/* 495 */     int i = paramString.indexOf("-");
/* 496 */     String str; if (i >= 0) {
/* 497 */       str = expandName(paramString.substring(0, i), false);
/* 498 */       str = str + " " + expandName(paramString.substring(i + 1), true);
/*     */     } else {
/* 500 */       str = expandName(paramString, false);
/*     */     }
/*     */     
/* 503 */     return str;
/*     */   }
/*     */   
/*     */   private String psName2FamilyName(String paramString) {
/* 507 */     String str = paramString;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 516 */     if (str.indexOf("-") > 0) {
/* 517 */       str = str.substring(0, str.indexOf("-"));
/*     */     }
/*     */     
/* 520 */     return expandName(str, false);
/*     */   }
/*     */   
/*     */   private int nextCapitalLetter(String paramString, int paramInt) {
/* 524 */     for (; (paramInt >= 0) && (paramInt < paramString.length()); paramInt++) {
/* 525 */       if ((paramString.charAt(paramInt) >= 'A') && (paramString.charAt(paramInt) <= 'Z'))
/* 526 */         return paramInt;
/*     */     }
/* 528 */     return -1;
/*     */   }
/*     */   
/*     */   private String expandName(String paramString, boolean paramBoolean) {
/* 532 */     StringBuffer localStringBuffer = new StringBuffer(paramString.length() + 10);
/* 533 */     int i = 0;
/*     */     
/* 535 */     while (i < paramString.length()) {
/* 536 */       int j = nextCapitalLetter(paramString, i + 1);
/* 537 */       if (j < 0) {
/* 538 */         j = paramString.length();
/*     */       }
/*     */       
/* 541 */       if (i != 0) {
/* 542 */         localStringBuffer.append(" ");
/*     */       }
/*     */       
/* 545 */       if (paramBoolean) {
/* 546 */         localStringBuffer.append(expandAbbreviation(paramString.substring(i, j)));
/*     */       } else {
/* 548 */         localStringBuffer.append(paramString.substring(i, j));
/*     */       }
/* 550 */       i = j;
/*     */     }
/*     */     
/* 553 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */   private byte skip(ByteBuffer paramByteBuffer)
/*     */   {
/* 558 */     byte b = paramByteBuffer.get();
/* 559 */     while (b == 37) {
/*     */       do {
/* 561 */         b = paramByteBuffer.get();
/* 562 */         if (b == 13) break; } while (b != 10);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 567 */     while (b <= 32) {
/* 568 */       b = paramByteBuffer.get();
/*     */     }
/* 570 */     return b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int nextTokenType(ByteBuffer paramByteBuffer)
/*     */   {
/*     */     try
/*     */     {
/* 581 */       int i = skip(paramByteBuffer);
/*     */       for (;;)
/*     */       {
/* 584 */         if (i == 47)
/* 585 */           return 1;
/* 586 */         if (i == 40)
/* 587 */           return 2;
/* 588 */         if ((i == 13) || (i == 10)) {
/* 589 */           i = skip(paramByteBuffer);
/*     */         } else {
/* 591 */           i = paramByteBuffer.get();
/*     */         }
/*     */       }
/*     */       
/* 595 */       return 0;
/*     */     }
/*     */     catch (BufferUnderflowException localBufferUnderflowException) {}
/*     */   }
/*     */   
/*     */ 
/*     */   private String getSimpleToken(ByteBuffer paramByteBuffer)
/*     */   {
/* 603 */     while (paramByteBuffer.get() <= 32) {}
/* 604 */     int i = paramByteBuffer.position() - 1;
/* 605 */     while (paramByteBuffer.get() > 32) {}
/* 606 */     int j = paramByteBuffer.position();
/* 607 */     byte[] arrayOfByte = new byte[j - i - 1];
/* 608 */     paramByteBuffer.position(i);
/* 609 */     paramByteBuffer.get(arrayOfByte);
/*     */     try {
/* 611 */       return new String(arrayOfByte, "US-ASCII");
/*     */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
/* 613 */     return new String(arrayOfByte);
/*     */   }
/*     */   
/*     */   private String getString(ByteBuffer paramByteBuffer)
/*     */   {
/* 618 */     int i = paramByteBuffer.position();
/* 619 */     while (paramByteBuffer.get() != 41) {}
/* 620 */     int j = paramByteBuffer.position();
/* 621 */     byte[] arrayOfByte = new byte[j - i - 1];
/* 622 */     paramByteBuffer.position(i);
/* 623 */     paramByteBuffer.get(arrayOfByte);
/*     */     try {
/* 625 */       return new String(arrayOfByte, "US-ASCII");
/*     */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
/* 627 */     return new String(arrayOfByte);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPostscriptName()
/*     */   {
/* 633 */     return this.psName;
/*     */   }
/*     */   
/*     */   protected synchronized FontScaler getScaler() {
/* 637 */     if (this.scaler == null) {
/* 638 */       this.scaler = FontScaler.getScaler(this, 0, false, this.fileSize);
/*     */     }
/*     */     
/* 641 */     return this.scaler;
/*     */   }
/*     */   
/*     */   CharToGlyphMapper getMapper() {
/* 645 */     if (this.mapper == null) {
/* 646 */       this.mapper = new Type1GlyphMapper(this);
/*     */     }
/* 648 */     return this.mapper;
/*     */   }
/*     */   
/*     */   public int getNumGlyphs() {
/*     */     try {
/* 653 */       return getScaler().getNumGlyphs();
/*     */     } catch (FontScalerException localFontScalerException) {
/* 655 */       this.scaler = FontScaler.getNullScaler(); }
/* 656 */     return getNumGlyphs();
/*     */   }
/*     */   
/*     */   public int getMissingGlyphCode()
/*     */   {
/*     */     try {
/* 662 */       return getScaler().getMissingGlyphCode();
/*     */     } catch (FontScalerException localFontScalerException) {
/* 664 */       this.scaler = FontScaler.getNullScaler(); }
/* 665 */     return getMissingGlyphCode();
/*     */   }
/*     */   
/*     */   public int getGlyphCode(char paramChar)
/*     */   {
/*     */     try {
/* 671 */       return getScaler().getGlyphCode(paramChar);
/*     */     } catch (FontScalerException localFontScalerException) {
/* 673 */       this.scaler = FontScaler.getNullScaler(); }
/* 674 */     return getGlyphCode(paramChar);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 679 */     return 
/* 680 */       "** Type1 Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " fileName=" + getPublicFileName();
/*     */   }
/*     */   
/*     */   protected void close() {}
/*     */   
/*     */   /* Error */
/*     */   private void verifyPFB(ByteBuffer paramByteBuffer)
/*     */     throws FontFormatException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: iconst_0
/*     */     //   1: istore_2
/*     */     //   2: aload_1
/*     */     //   3: iload_2
/*     */     //   4: invokevirtual 563	java/nio/ByteBuffer:getShort	(I)S
/*     */     //   7: ldc 4
/*     */     //   9: iand
/*     */     //   10: istore_3
/*     */     //   11: iload_3
/*     */     //   12: ldc 1
/*     */     //   14: if_icmpeq +9 -> 23
/*     */     //   17: iload_3
/*     */     //   18: ldc 2
/*     */     //   20: if_icmpne +54 -> 74
/*     */     //   23: aload_1
/*     */     //   24: getstatic 516	java/nio/ByteOrder:LITTLE_ENDIAN	Ljava/nio/ByteOrder;
/*     */     //   27: invokevirtual 567	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
/*     */     //   30: pop
/*     */     //   31: aload_1
/*     */     //   32: iload_2
/*     */     //   33: iconst_2
/*     */     //   34: iadd
/*     */     //   35: invokevirtual 562	java/nio/ByteBuffer:getInt	(I)I
/*     */     //   38: istore 4
/*     */     //   40: aload_1
/*     */     //   41: getstatic 515	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
/*     */     //   44: invokevirtual 567	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
/*     */     //   47: pop
/*     */     //   48: iload 4
/*     */     //   50: ifgt +13 -> 63
/*     */     //   53: new 364	java/awt/FontFormatException
/*     */     //   56: dup
/*     */     //   57: ldc 107
/*     */     //   59: invokespecial 530	java/awt/FontFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   62: athrow
/*     */     //   63: iload_2
/*     */     //   64: iload 4
/*     */     //   66: bipush 6
/*     */     //   68: iadd
/*     */     //   69: iadd
/*     */     //   70: istore_2
/*     */     //   71: goto +20 -> 91
/*     */     //   74: iload_3
/*     */     //   75: ldc 3
/*     */     //   77: if_icmpne +4 -> 81
/*     */     //   80: return
/*     */     //   81: new 364	java/awt/FontFormatException
/*     */     //   84: dup
/*     */     //   85: ldc 106
/*     */     //   87: invokespecial 530	java/awt/FontFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   90: athrow
/*     */     //   91: goto -89 -> 2
/*     */     //   94: astore_3
/*     */     //   95: new 364	java/awt/FontFormatException
/*     */     //   98: dup
/*     */     //   99: aload_3
/*     */     //   100: invokevirtual 555	java/nio/BufferUnderflowException:toString	()Ljava/lang/String;
/*     */     //   103: invokespecial 530	java/awt/FontFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   106: athrow
/*     */     //   107: astore_3
/*     */     //   108: new 364	java/awt/FontFormatException
/*     */     //   111: dup
/*     */     //   112: aload_3
/*     */     //   113: invokevirtual 534	java/lang/Exception:toString	()Ljava/lang/String;
/*     */     //   116: invokespecial 530	java/awt/FontFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   119: athrow
/*     */     // Line number table:
/*     */     //   Java source line #329	-> byte code offset #0
/*     */     //   Java source line #332	-> byte code offset #2
/*     */     //   Java source line #333	-> byte code offset #11
/*     */     //   Java source line #334	-> byte code offset #23
/*     */     //   Java source line #335	-> byte code offset #31
/*     */     //   Java source line #336	-> byte code offset #40
/*     */     //   Java source line #337	-> byte code offset #48
/*     */     //   Java source line #338	-> byte code offset #53
/*     */     //   Java source line #340	-> byte code offset #63
/*     */     //   Java source line #341	-> byte code offset #71
/*     */     //   Java source line #342	-> byte code offset #80
/*     */     //   Java source line #344	-> byte code offset #81
/*     */     //   Java source line #350	-> byte code offset #91
/*     */     //   Java source line #346	-> byte code offset #94
/*     */     //   Java source line #347	-> byte code offset #95
/*     */     //   Java source line #348	-> byte code offset #107
/*     */     //   Java source line #349	-> byte code offset #108
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	120	0	this	Type1Font
/*     */     //   0	120	1	paramByteBuffer	ByteBuffer
/*     */     //   1	70	2	i	int
/*     */     //   10	68	3	j	int
/*     */     //   94	6	3	localBufferUnderflowException	BufferUnderflowException
/*     */     //   107	6	3	localException	Exception
/*     */     //   38	31	4	k	int
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   2	80	94	java/nio/BufferUnderflowException
/*     */     //   81	91	94	java/nio/BufferUnderflowException
/*     */     //   2	80	107	java/lang/Exception
/*     */     //   81	91	107	java/lang/Exception
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\Type1Font.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
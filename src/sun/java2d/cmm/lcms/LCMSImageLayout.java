/*     */ package sun.java2d.cmm.lcms;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ComponentColorModel;
/*     */ import java.awt.image.ComponentSampleModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.SampleModel;
/*     */ import sun.awt.image.ByteComponentRaster;
/*     */ import sun.awt.image.IntegerComponentRaster;
/*     */ import sun.awt.image.ShortComponentRaster;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class LCMSImageLayout
/*     */ {
/*     */   public static final int SWAPFIRST = 16384;
/*     */   public static final int DOSWAP = 1024;
/*     */   
/*     */   public static int BYTES_SH(int paramInt)
/*     */   {
/*  41 */     return paramInt;
/*     */   }
/*     */   
/*     */   public static int EXTRA_SH(int paramInt) {
/*  45 */     return paramInt << 7;
/*     */   }
/*     */   
/*     */   public static int CHANNELS_SH(int paramInt) {
/*  49 */     return paramInt << 3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  54 */   public static final int PT_RGB_8 = CHANNELS_SH(3) | BYTES_SH(1);
/*     */   
/*  56 */   public static final int PT_GRAY_8 = CHANNELS_SH(1) | BYTES_SH(1);
/*     */   
/*  58 */   public static final int PT_GRAY_16 = CHANNELS_SH(1) | BYTES_SH(2);
/*     */   
/*  60 */   public static final int PT_RGBA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
/*     */   
/*  62 */   public static final int PT_ARGB_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x4000;
/*  63 */   public static final int PT_BGR_8 = 0x400 | 
/*  64 */     CHANNELS_SH(3) | BYTES_SH(1);
/*  65 */   public static final int PT_ABGR_8 = 0x400 | 
/*  66 */     EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
/*  67 */   public static final int PT_BGRA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | 
/*  68 */     BYTES_SH(1) | 0x400 | 0x4000;
/*     */   public static final int DT_BYTE = 0;
/*     */   public static final int DT_SHORT = 1;
/*     */   public static final int DT_INT = 2;
/*     */   public static final int DT_DOUBLE = 3;
/*  73 */   boolean isIntPacked = false;
/*     */   
/*     */   int pixelType;
/*     */   
/*     */   int dataType;
/*     */   
/*     */   int width;
/*     */   
/*     */   int height;
/*     */   
/*     */   int nextRowOffset;
/*     */   private int nextPixelOffset;
/*     */   int offset;
/*  86 */   private boolean imageAtOnce = false;
/*     */   
/*     */   Object dataArray;
/*     */   private int dataArrayLength;
/*     */   
/*     */   private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3)
/*     */     throws ImageLayoutException
/*     */   {
/*  94 */     this.pixelType = paramInt2;
/*  95 */     this.width = paramInt1;
/*  96 */     this.height = 1;
/*  97 */     this.nextPixelOffset = paramInt3;
/*  98 */     this.nextRowOffset = safeMult(paramInt3, paramInt1);
/*  99 */     this.offset = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws ImageLayoutException
/*     */   {
/* 106 */     this.pixelType = paramInt3;
/* 107 */     this.width = paramInt1;
/* 108 */     this.height = paramInt2;
/* 109 */     this.nextPixelOffset = paramInt4;
/* 110 */     this.nextRowOffset = safeMult(paramInt4, paramInt1);
/* 111 */     this.offset = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public LCMSImageLayout(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws ImageLayoutException
/*     */   {
/* 118 */     this(paramInt1, paramInt2, paramInt3);
/* 119 */     this.dataType = 0;
/* 120 */     this.dataArray = paramArrayOfByte;
/* 121 */     this.dataArrayLength = paramArrayOfByte.length;
/*     */     
/* 123 */     verify();
/*     */   }
/*     */   
/*     */   public LCMSImageLayout(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws ImageLayoutException
/*     */   {
/* 129 */     this(paramInt1, paramInt2, paramInt3);
/* 130 */     this.dataType = 1;
/* 131 */     this.dataArray = paramArrayOfShort;
/* 132 */     this.dataArrayLength = (2 * paramArrayOfShort.length);
/*     */     
/* 134 */     verify();
/*     */   }
/*     */   
/*     */   public LCMSImageLayout(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws ImageLayoutException
/*     */   {
/* 140 */     this(paramInt1, paramInt2, paramInt3);
/* 141 */     this.dataType = 2;
/* 142 */     this.dataArray = paramArrayOfInt;
/* 143 */     this.dataArrayLength = (4 * paramArrayOfInt.length);
/*     */     
/* 145 */     verify();
/*     */   }
/*     */   
/*     */   public LCMSImageLayout(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws ImageLayoutException
/*     */   {
/* 151 */     this(paramInt1, paramInt2, paramInt3);
/* 152 */     this.dataType = 3;
/* 153 */     this.dataArray = paramArrayOfDouble;
/* 154 */     this.dataArrayLength = (8 * paramArrayOfDouble.length);
/*     */     
/* 156 */     verify();
/*     */   }
/*     */   
/*     */ 
/*     */   private LCMSImageLayout() {}
/*     */   
/*     */ 
/*     */   public static LCMSImageLayout createImageLayout(BufferedImage paramBufferedImage)
/*     */     throws ImageLayoutException
/*     */   {
/* 166 */     LCMSImageLayout localLCMSImageLayout = new LCMSImageLayout();
/*     */     Object localObject;
/* 168 */     switch (paramBufferedImage.getType()) {
/*     */     case 1: 
/* 170 */       localLCMSImageLayout.pixelType = PT_ARGB_8;
/* 171 */       localLCMSImageLayout.isIntPacked = true;
/* 172 */       break;
/*     */     case 2: 
/* 174 */       localLCMSImageLayout.pixelType = PT_ARGB_8;
/* 175 */       localLCMSImageLayout.isIntPacked = true;
/* 176 */       break;
/*     */     case 4: 
/* 178 */       localLCMSImageLayout.pixelType = PT_ABGR_8;
/* 179 */       localLCMSImageLayout.isIntPacked = true;
/* 180 */       break;
/*     */     case 5: 
/* 182 */       localLCMSImageLayout.pixelType = PT_BGR_8;
/* 183 */       break;
/*     */     case 6: 
/* 185 */       localLCMSImageLayout.pixelType = PT_ABGR_8;
/* 186 */       break;
/*     */     case 10: 
/* 188 */       localLCMSImageLayout.pixelType = PT_GRAY_8;
/* 189 */       break;
/*     */     case 11: 
/* 191 */       localLCMSImageLayout.pixelType = PT_GRAY_16;
/* 192 */       break;
/*     */     case 3: 
/*     */     case 7: 
/*     */     case 8: 
/*     */     case 9: 
/*     */     default: 
/* 198 */       localObject = paramBufferedImage.getColorModel();
/* 199 */       if ((localObject instanceof ComponentColorModel)) {
/* 200 */         ComponentColorModel localComponentColorModel = (ComponentColorModel)localObject;
/*     */         
/*     */ 
/* 203 */         int[] arrayOfInt1 = localComponentColorModel.getComponentSize();
/* 204 */         for (int m : arrayOfInt1) {
/* 205 */           if (m != 8) {
/* 206 */             return null;
/*     */           }
/*     */         }
/*     */         
/* 210 */         return createImageLayout(paramBufferedImage.getRaster());
/*     */       }
/*     */       
/* 213 */       return null;
/*     */     }
/*     */     
/* 216 */     localLCMSImageLayout.width = paramBufferedImage.getWidth();
/* 217 */     localLCMSImageLayout.height = paramBufferedImage.getHeight();
/*     */     
/* 219 */     switch (paramBufferedImage.getType())
/*     */     {
/*     */ 
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 4: 
/* 225 */       localObject = (IntegerComponentRaster)paramBufferedImage.getRaster();
/* 226 */       localLCMSImageLayout.nextRowOffset = safeMult(4, ((IntegerComponentRaster)localObject).getScanlineStride());
/* 227 */       localLCMSImageLayout.nextPixelOffset = safeMult(4, ((IntegerComponentRaster)localObject).getPixelStride());
/* 228 */       localLCMSImageLayout.offset = safeMult(4, ((IntegerComponentRaster)localObject).getDataOffset(0));
/* 229 */       localLCMSImageLayout.dataArray = ((IntegerComponentRaster)localObject).getDataStorage();
/* 230 */       localLCMSImageLayout.dataArrayLength = (4 * ((IntegerComponentRaster)localObject).getDataStorage().length);
/* 231 */       localLCMSImageLayout.dataType = 2;
/*     */       
/* 233 */       if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * 4 * ((IntegerComponentRaster)localObject).getPixelStride()) {
/* 234 */         localLCMSImageLayout.imageAtOnce = true;
/*     */       }
/*     */       
/* 237 */       break;
/*     */     
/*     */ 
/*     */ 
/*     */     case 5: 
/*     */     case 6: 
/* 243 */       localObject = (ByteComponentRaster)paramBufferedImage.getRaster();
/* 244 */       localLCMSImageLayout.nextRowOffset = ((ByteComponentRaster)localObject).getScanlineStride();
/* 245 */       localLCMSImageLayout.nextPixelOffset = ((ByteComponentRaster)localObject).getPixelStride();
/*     */       
/* 247 */       int i = paramBufferedImage.getSampleModel().getNumBands() - 1;
/* 248 */       localLCMSImageLayout.offset = ((ByteComponentRaster)localObject).getDataOffset(i);
/* 249 */       localLCMSImageLayout.dataArray = ((ByteComponentRaster)localObject).getDataStorage();
/* 250 */       localLCMSImageLayout.dataArrayLength = ((ByteComponentRaster)localObject).getDataStorage().length;
/* 251 */       localLCMSImageLayout.dataType = 0;
/* 252 */       if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * ((ByteComponentRaster)localObject).getPixelStride()) {
/* 253 */         localLCMSImageLayout.imageAtOnce = true;
/*     */       }
/*     */       
/* 256 */       break;
/*     */     
/*     */ 
/*     */ 
/*     */     case 10: 
/* 261 */       localObject = (ByteComponentRaster)paramBufferedImage.getRaster();
/* 262 */       localLCMSImageLayout.nextRowOffset = ((ByteComponentRaster)localObject).getScanlineStride();
/* 263 */       localLCMSImageLayout.nextPixelOffset = ((ByteComponentRaster)localObject).getPixelStride();
/*     */       
/* 265 */       localLCMSImageLayout.dataArrayLength = ((ByteComponentRaster)localObject).getDataStorage().length;
/* 266 */       localLCMSImageLayout.offset = ((ByteComponentRaster)localObject).getDataOffset(0);
/* 267 */       localLCMSImageLayout.dataArray = ((ByteComponentRaster)localObject).getDataStorage();
/* 268 */       localLCMSImageLayout.dataType = 0;
/*     */       
/* 270 */       if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * ((ByteComponentRaster)localObject).getPixelStride()) {
/* 271 */         localLCMSImageLayout.imageAtOnce = true;
/*     */       }
/*     */       
/* 274 */       break;
/*     */     
/*     */ 
/*     */ 
/*     */     case 11: 
/* 279 */       localObject = (ShortComponentRaster)paramBufferedImage.getRaster();
/* 280 */       localLCMSImageLayout.nextRowOffset = safeMult(2, ((ShortComponentRaster)localObject).getScanlineStride());
/* 281 */       localLCMSImageLayout.nextPixelOffset = safeMult(2, ((ShortComponentRaster)localObject).getPixelStride());
/*     */       
/* 283 */       localLCMSImageLayout.offset = safeMult(2, ((ShortComponentRaster)localObject).getDataOffset(0));
/* 284 */       localLCMSImageLayout.dataArray = ((ShortComponentRaster)localObject).getDataStorage();
/* 285 */       localLCMSImageLayout.dataArrayLength = (2 * ((ShortComponentRaster)localObject).getDataStorage().length);
/* 286 */       localLCMSImageLayout.dataType = 1;
/*     */       
/* 288 */       if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * 2 * ((ShortComponentRaster)localObject).getPixelStride()) {
/* 289 */         localLCMSImageLayout.imageAtOnce = true;
/*     */       }
/*     */       
/* 292 */       break;
/*     */     case 3: case 7: case 8: case 9: default: 
/* 294 */       return null; }
/*     */     
/* 296 */     localLCMSImageLayout.verify();
/* 297 */     return localLCMSImageLayout;
/*     */   }
/*     */   
/*     */   private static enum BandOrder {
/* 301 */     DIRECT, 
/* 302 */     INVERTED, 
/* 303 */     ARBITRARY, 
/* 304 */     UNKNOWN;
/*     */     
/*     */     private BandOrder() {}
/* 307 */     public static BandOrder getBandOrder(int[] paramArrayOfInt) { BandOrder localBandOrder = UNKNOWN;
/*     */       
/* 309 */       int i = paramArrayOfInt.length;
/*     */       
/* 311 */       for (int j = 0; (localBandOrder != ARBITRARY) && (j < paramArrayOfInt.length); j++) {
/* 312 */         switch (LCMSImageLayout.1.$SwitchMap$sun$java2d$cmm$lcms$LCMSImageLayout$BandOrder[localBandOrder.ordinal()]) {
/*     */         case 1: 
/* 314 */           if (paramArrayOfInt[j] == j) {
/* 315 */             localBandOrder = DIRECT;
/* 316 */           } else if (paramArrayOfInt[j] == i - 1 - j) {
/* 317 */             localBandOrder = INVERTED;
/*     */           } else {
/* 319 */             localBandOrder = ARBITRARY;
/*     */           }
/* 321 */           break;
/*     */         case 2: 
/* 323 */           if (paramArrayOfInt[j] != j) {
/* 324 */             localBandOrder = ARBITRARY;
/*     */           }
/*     */           break;
/*     */         case 3: 
/* 328 */           if (paramArrayOfInt[j] != i - 1 - j) {
/* 329 */             localBandOrder = ARBITRARY;
/*     */           }
/*     */           break;
/*     */         }
/*     */       }
/* 334 */       return localBandOrder;
/*     */     }
/*     */   }
/*     */   
/*     */   private void verify() throws ImageLayoutException
/*     */   {
/* 340 */     if ((this.offset < 0) || (this.offset >= this.dataArrayLength)) {
/* 341 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/*     */     
/* 344 */     if (this.nextPixelOffset != getBytesPerPixel(this.pixelType)) {
/* 345 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/*     */     
/* 348 */     int i = safeMult(this.nextRowOffset, this.height - 1);
/*     */     
/* 350 */     int j = safeMult(this.nextPixelOffset, this.width - 1);
/*     */     
/* 352 */     j = safeAdd(j, i);
/*     */     
/* 354 */     int k = safeAdd(this.offset, j);
/*     */     
/* 356 */     if ((k < 0) || (k >= this.dataArrayLength)) {
/* 357 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/*     */   }
/*     */   
/*     */   static int safeAdd(int paramInt1, int paramInt2) throws ImageLayoutException {
/* 362 */     long l = paramInt1;
/* 363 */     l += paramInt2;
/* 364 */     if ((l < -2147483648L) || (l > 2147483647L)) {
/* 365 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/* 367 */     return (int)l;
/*     */   }
/*     */   
/*     */   static int safeMult(int paramInt1, int paramInt2) throws ImageLayoutException {
/* 371 */     long l = paramInt1;
/* 372 */     l *= paramInt2;
/* 373 */     if ((l < -2147483648L) || (l > 2147483647L)) {
/* 374 */       throw new ImageLayoutException("Invalid image layout");
/*     */     }
/* 376 */     return (int)l;
/*     */   }
/*     */   
/*     */   public static class ImageLayoutException
/*     */     extends Exception {
/* 381 */     public ImageLayoutException(String paramString) { super(); }
/*     */   }
/*     */   
/*     */   public static LCMSImageLayout createImageLayout(Raster paramRaster) {
/* 385 */     LCMSImageLayout localLCMSImageLayout = new LCMSImageLayout();
/* 386 */     if (((paramRaster instanceof ByteComponentRaster)) && 
/* 387 */       ((paramRaster.getSampleModel() instanceof ComponentSampleModel))) {
/* 388 */       ByteComponentRaster localByteComponentRaster = (ByteComponentRaster)paramRaster;
/*     */       
/* 390 */       ComponentSampleModel localComponentSampleModel = (ComponentSampleModel)paramRaster.getSampleModel();
/*     */       
/* 392 */       localLCMSImageLayout.pixelType = (CHANNELS_SH(localByteComponentRaster.getNumBands()) | BYTES_SH(1));
/*     */       
/* 394 */       int[] arrayOfInt = localComponentSampleModel.getBandOffsets();
/* 395 */       BandOrder localBandOrder = BandOrder.getBandOrder(arrayOfInt);
/*     */       
/* 397 */       int i = 0;
/* 398 */       switch (localBandOrder) {
/*     */       case INVERTED: 
/* 400 */         localLCMSImageLayout.pixelType |= 0x400;
/* 401 */         i = localComponentSampleModel.getNumBands() - 1;
/* 402 */         break;
/*     */       
/*     */       case DIRECT: 
/*     */         break;
/*     */       
/*     */       default: 
/* 408 */         return null;
/*     */       }
/*     */       
/* 411 */       localLCMSImageLayout.nextRowOffset = localByteComponentRaster.getScanlineStride();
/* 412 */       localLCMSImageLayout.nextPixelOffset = localByteComponentRaster.getPixelStride();
/*     */       
/* 414 */       localLCMSImageLayout.offset = localByteComponentRaster.getDataOffset(i);
/* 415 */       localLCMSImageLayout.dataArray = localByteComponentRaster.getDataStorage();
/* 416 */       localLCMSImageLayout.dataType = 0;
/*     */       
/* 418 */       localLCMSImageLayout.width = localByteComponentRaster.getWidth();
/* 419 */       localLCMSImageLayout.height = localByteComponentRaster.getHeight();
/*     */       
/* 421 */       if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * localByteComponentRaster.getPixelStride()) {
/* 422 */         localLCMSImageLayout.imageAtOnce = true;
/*     */       }
/* 424 */       return localLCMSImageLayout;
/*     */     }
/* 426 */     return null;
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
/*     */   private static int getBytesPerPixel(int paramInt)
/*     */   {
/* 443 */     int i = 0x7 & paramInt;
/* 444 */     int j = 0xF & paramInt >> 3;
/* 445 */     int k = 0x7 & paramInt >> 7;
/*     */     
/* 447 */     return i * (j + k);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\cmm\lcms\LCMSImageLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
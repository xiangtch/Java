/*     */ package sun.awt.image.codec;
/*     */ 
/*     */ import com.sun.image.codec.jpeg.ImageFormatException;
/*     */ import com.sun.image.codec.jpeg.JPEGDecodeParam;
/*     */ import com.sun.image.codec.jpeg.JPEGEncodeParam;
/*     */ import com.sun.image.codec.jpeg.JPEGImageEncoder;
/*     */ import java.awt.Point;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentColorModel;
/*     */ import java.awt.image.ComponentSampleModel;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.IndexColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.RescaleOp;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.SinglePixelPackedSampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JPEGImageEncoderImpl
/*     */   implements JPEGImageEncoder
/*     */ {
/*  62 */   private OutputStream outStream = null;
/*  63 */   private JPEGParam param = null;
/*  64 */   private boolean pack = false;
/*     */   
/*  66 */   private static final Class OutputStreamClass = OutputStream.class;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  73 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/*  76 */         System.loadLibrary("jpeg");
/*  77 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JPEGImageEncoderImpl(OutputStream paramOutputStream)
/*     */   {
/*  88 */     if (paramOutputStream == null) {
/*  89 */       throw new IllegalArgumentException("OutputStream is null.");
/*     */     }
/*  91 */     this.outStream = paramOutputStream;
/*  92 */     initEncoder(OutputStreamClass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JPEGImageEncoderImpl(OutputStream paramOutputStream, JPEGEncodeParam paramJPEGEncodeParam)
/*     */   {
/* 102 */     this(paramOutputStream);
/* 103 */     setJPEGEncodeParam(paramJPEGEncodeParam);
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
/*     */   public int getDefaultColorId(ColorModel paramColorModel)
/*     */   {
/* 118 */     boolean bool = paramColorModel.hasAlpha();
/* 119 */     ColorSpace localColorSpace1 = paramColorModel.getColorSpace();
/* 120 */     ColorSpace localColorSpace2 = null;
/* 121 */     switch (localColorSpace1.getType()) {
/*     */     case 6: 
/* 123 */       return 1;
/*     */     
/*     */     case 5: 
/* 126 */       if (bool) {
/* 127 */         return 7;
/*     */       }
/* 129 */       return 3;
/*     */     
/*     */     case 3: 
/* 132 */       if (localColorSpace2 == null) {
/*     */         try {
/* 134 */           localColorSpace2 = ColorSpace.getInstance(1002);
/*     */         }
/*     */         catch (IllegalArgumentException localIllegalArgumentException) {}
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 141 */       if (localColorSpace1 == localColorSpace2) {
/* 142 */         return bool ? 10 : 5;
/*     */       }
/*     */       
/*     */ 
/* 146 */       return bool ? 7 : 3;
/*     */     
/*     */ 
/*     */ 
/*     */     case 9: 
/* 151 */       return 4;
/*     */     }
/* 153 */     return 0;
/*     */   }
/*     */   
/*     */   public synchronized OutputStream getOutputStream()
/*     */   {
/* 158 */     return this.outStream;
/*     */   }
/*     */   
/*     */   public synchronized void setJPEGEncodeParam(JPEGEncodeParam paramJPEGEncodeParam) {
/* 162 */     this.param = new JPEGParam(paramJPEGEncodeParam);
/*     */   }
/*     */   
/* 165 */   public synchronized JPEGEncodeParam getJPEGEncodeParam() { return (JPEGEncodeParam)this.param.clone(); }
/*     */   
/*     */   public JPEGEncodeParam getDefaultJPEGEncodeParam(Raster paramRaster, int paramInt)
/*     */   {
/* 169 */     JPEGParam localJPEGParam = new JPEGParam(paramInt, paramRaster.getNumBands());
/* 170 */     localJPEGParam.setWidth(paramRaster.getWidth());
/* 171 */     localJPEGParam.setHeight(paramRaster.getHeight());
/*     */     
/* 173 */     return localJPEGParam;
/*     */   }
/*     */   
/*     */   public JPEGEncodeParam getDefaultJPEGEncodeParam(BufferedImage paramBufferedImage)
/*     */   {
/* 178 */     ColorModel localColorModel = paramBufferedImage.getColorModel();
/* 179 */     int i = getDefaultColorId(localColorModel);
/*     */     
/* 181 */     if (!(localColorModel instanceof IndexColorModel)) {
/* 182 */       return getDefaultJPEGEncodeParam(paramBufferedImage.getRaster(), i);
/*     */     }
/*     */     
/*     */     JPEGParam localJPEGParam;
/*     */     
/* 187 */     if (localColorModel.hasAlpha()) localJPEGParam = new JPEGParam(i, 4); else {
/* 188 */       localJPEGParam = new JPEGParam(i, 3);
/*     */     }
/* 190 */     localJPEGParam.setWidth(paramBufferedImage.getWidth());
/* 191 */     localJPEGParam.setHeight(paramBufferedImage.getHeight());
/* 192 */     return localJPEGParam;
/*     */   }
/*     */   
/*     */   public JPEGEncodeParam getDefaultJPEGEncodeParam(int paramInt1, int paramInt2)
/*     */   {
/* 197 */     return new JPEGParam(paramInt2, paramInt1);
/*     */   }
/*     */   
/*     */   public JPEGEncodeParam getDefaultJPEGEncodeParam(JPEGDecodeParam paramJPEGDecodeParam) throws ImageFormatException
/*     */   {
/* 202 */     return new JPEGParam(paramJPEGDecodeParam);
/*     */   }
/*     */   
/*     */   public synchronized void encode(BufferedImage paramBufferedImage)
/*     */     throws IOException, ImageFormatException
/*     */   {
/* 208 */     if (this.param == null) {
/* 209 */       setJPEGEncodeParam(getDefaultJPEGEncodeParam(paramBufferedImage));
/*     */     }
/* 211 */     if ((paramBufferedImage.getWidth() != this.param.getWidth()) || 
/* 212 */       (paramBufferedImage.getHeight() != this.param.getHeight())) {
/* 213 */       throw new ImageFormatException("Param block's width/height doesn't match the BufferedImage");
/*     */     }
/*     */     
/*     */ 
/* 217 */     if (this.param.getEncodedColorID() != getDefaultColorId(paramBufferedImage.getColorModel())) {
/* 218 */       throw new ImageFormatException("The encoded COLOR_ID doesn't match the BufferedImage");
/*     */     }
/*     */     
/* 221 */     WritableRaster localWritableRaster = paramBufferedImage.getRaster();
/* 222 */     ColorModel localColorModel = paramBufferedImage.getColorModel();
/*     */     
/*     */ 
/* 225 */     if ((localColorModel instanceof IndexColorModel)) {
/* 226 */       IndexColorModel localIndexColorModel = (IndexColorModel)localColorModel;
/* 227 */       paramBufferedImage = localIndexColorModel.convertToIntDiscrete(localWritableRaster, false);
/* 228 */       localWritableRaster = paramBufferedImage.getRaster();
/* 229 */       localColorModel = paramBufferedImage.getColorModel();
/*     */     }
/*     */     
/* 232 */     encode(localWritableRaster, localColorModel);
/*     */   }
/*     */   
/*     */   public synchronized void encode(BufferedImage paramBufferedImage, JPEGEncodeParam paramJPEGEncodeParam)
/*     */     throws IOException, ImageFormatException
/*     */   {
/* 238 */     setJPEGEncodeParam(paramJPEGEncodeParam);
/* 239 */     encode(paramBufferedImage);
/*     */   }
/*     */   
/*     */   public void encode(Raster paramRaster)
/*     */     throws IOException, ImageFormatException
/*     */   {
/* 245 */     if (this.param == null) {
/* 246 */       setJPEGEncodeParam(
/* 247 */         getDefaultJPEGEncodeParam(paramRaster, 0));
/*     */     }
/* 249 */     if (paramRaster.getNumBands() != paramRaster.getSampleModel().getNumBands()) {
/* 250 */       throw new ImageFormatException("Raster's number of bands doesn't match the SampleModel");
/*     */     }
/*     */     
/* 253 */     if ((paramRaster.getWidth() != this.param.getWidth()) || 
/* 254 */       (paramRaster.getHeight() != this.param.getHeight())) {
/* 255 */       throw new ImageFormatException("Param block's width/height doesn't match the Raster");
/*     */     }
/*     */     
/* 258 */     if ((this.param.getEncodedColorID() != 0) && 
/* 259 */       (this.param.getNumComponents() != paramRaster.getNumBands())) {
/* 260 */       throw new ImageFormatException("Param block's COLOR_ID doesn't match the Raster.");
/*     */     }
/*     */     
/* 263 */     encode(paramRaster, (ColorModel)null);
/*     */   }
/*     */   
/*     */   public void encode(Raster paramRaster, JPEGEncodeParam paramJPEGEncodeParam)
/*     */     throws IOException, ImageFormatException
/*     */   {
/* 269 */     setJPEGEncodeParam(paramJPEGEncodeParam);
/* 270 */     encode(paramRaster);
/*     */   }
/*     */   
/*     */   private boolean useGiven(Raster paramRaster) {
/* 274 */     SampleModel localSampleModel = paramRaster.getSampleModel();
/* 275 */     if (localSampleModel.getDataType() != 0) {
/* 276 */       return false;
/*     */     }
/* 278 */     if (!(localSampleModel instanceof ComponentSampleModel)) {
/* 279 */       return false;
/*     */     }
/* 281 */     ComponentSampleModel localComponentSampleModel = (ComponentSampleModel)localSampleModel;
/* 282 */     if (localComponentSampleModel.getPixelStride() != localSampleModel.getNumBands())
/* 283 */       return false;
/* 284 */     int[] arrayOfInt = localComponentSampleModel.getBandOffsets();
/* 285 */     for (int i = 0; i < arrayOfInt.length; i++) {
/* 286 */       if (arrayOfInt[i] != i) return false;
/*     */     }
/* 288 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean canPack(Raster paramRaster)
/*     */   {
/* 294 */     SampleModel localSampleModel = paramRaster.getSampleModel();
/* 295 */     if (localSampleModel.getDataType() != 3) {
/* 296 */       return false;
/*     */     }
/* 298 */     if (!(localSampleModel instanceof SinglePixelPackedSampleModel)) {
/* 299 */       return false;
/*     */     }
/* 301 */     SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)localSampleModel;
/*     */     
/*     */ 
/* 304 */     int[] arrayOfInt1 = { 16711680, 65280, 255, -16777216 };
/* 305 */     int[] arrayOfInt2 = localSinglePixelPackedSampleModel.getBitMasks();
/* 306 */     if ((arrayOfInt2.length != 3) && (arrayOfInt2.length != 4)) {
/* 307 */       return false;
/*     */     }
/* 309 */     for (int i = 0; i < arrayOfInt2.length; i++) {
/* 310 */       if (arrayOfInt2[i] != arrayOfInt1[i]) return false;
/*     */     }
/* 312 */     return true;
/*     */   }
/*     */   
/*     */   private void encode(Raster paramRaster, ColorModel paramColorModel)
/*     */     throws IOException, ImageFormatException
/*     */   {
/* 318 */     SampleModel localSampleModel = paramRaster.getSampleModel();
/*     */     
/* 320 */     int j = localSampleModel.getNumBands();
/* 321 */     int i; if (paramColorModel == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 326 */       for (i = 0; i < j; i++) {
/* 327 */         if (localSampleModel.getSampleSize(i) > 8) {
/* 328 */           throw new ImageFormatException("JPEG encoder can only accept 8 bit data.");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 334 */     int k = this.param.getEncodedColorID();
/* 335 */     switch (this.param.getNumComponents())
/*     */     {
/*     */     case 1: 
/* 338 */       if ((k != 1) && (k != 0))
/*     */       {
/* 340 */         if (this.param.findAPP0() != null) {
/* 341 */           throw new ImageFormatException("1 band JFIF files imply Y or unknown encoding.\nParam block indicates alternate encoding.");
/*     */         }
/*     */       }
/*     */       break;
/*     */     case 3: 
/* 346 */       if ((k != 3) && 
/* 347 */         (this.param.findAPP0() != null)) {
/* 348 */         throw new ImageFormatException("3 band JFIF files imply YCbCr encoding.\nParam block indicates alternate encoding.");
/*     */       }
/*     */       
/*     */       break;
/*     */     case 4: 
/* 353 */       if ((k != 4) && 
/* 354 */         (this.param.findAPP0() != null)) {
/* 355 */         throw new ImageFormatException("4 band JFIF files imply CMYK encoding.\nParam block indicates alternate encoding.");
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 364 */     if (!this.param.isImageInfoValid())
/*     */     {
/* 366 */       writeJPEGStream(this.param, paramColorModel, this.outStream, null, 0, 0);
/* 367 */       return;
/*     */     }
/*     */     
/* 370 */     DataBuffer localDataBuffer = paramRaster.getDataBuffer();
/*     */     
/*     */ 
/*     */ 
/* 374 */     int i1 = 0;
/* 375 */     int i2 = 1;
/* 376 */     int[] arrayOfInt1 = null;
/*     */     
/* 378 */     if (paramColorModel != null) {
/* 379 */       if ((paramColorModel.hasAlpha()) && (paramColorModel.isAlphaPremultiplied())) {
/* 380 */         i1 = 1;
/* 381 */         i2 = 0;
/*     */       }
/* 383 */       arrayOfInt1 = paramColorModel.getComponentSize();
/* 384 */       for (i = 0; i < j; i++) {
/* 385 */         if (arrayOfInt1[i] != 8) {
/* 386 */           i2 = 0;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 391 */     this.pack = false;
/* 392 */     Object localObject2; int n; int m; Object localObject1; if ((i2 != 0) && (useGiven(paramRaster))) {
/* 393 */       localObject2 = (ComponentSampleModel)localSampleModel;
/*     */       
/*     */ 
/* 396 */       n = localDataBuffer.getOffset() + ((ComponentSampleModel)localObject2).getOffset(paramRaster
/* 397 */         .getMinX() - paramRaster.getSampleModelTranslateX(), paramRaster
/* 398 */         .getMinY() - paramRaster.getSampleModelTranslateY());
/*     */       
/* 400 */       m = ((ComponentSampleModel)localObject2).getScanlineStride();
/* 401 */       localObject1 = ((DataBufferByte)localDataBuffer).getData();
/*     */     }
/* 403 */     else if ((i2 != 0) && (canPack(paramRaster)))
/*     */     {
/* 405 */       localObject2 = (SinglePixelPackedSampleModel)localSampleModel;
/*     */       
/*     */ 
/* 408 */       n = localDataBuffer.getOffset() + ((SinglePixelPackedSampleModel)localObject2).getOffset(paramRaster
/* 409 */         .getMinX() - paramRaster.getSampleModelTranslateX(), paramRaster
/* 410 */         .getMinY() - paramRaster.getSampleModelTranslateY());
/*     */       
/* 412 */       m = ((SinglePixelPackedSampleModel)localObject2).getScanlineStride();
/* 413 */       localObject1 = ((DataBufferInt)localDataBuffer).getData();
/* 414 */       this.pack = true;
/*     */     }
/*     */     else {
/* 417 */       int[] arrayOfInt2 = new int[j];
/* 418 */       float[] arrayOfFloat1 = new float[j];
/* 419 */       for (i = 0; i < j; i++) {
/* 420 */         arrayOfInt2[i] = i;
/* 421 */         if (i2 == 0)
/*     */         {
/*     */ 
/* 424 */           if (arrayOfInt1[i] != 8) {
/* 425 */             arrayOfFloat1[i] = (255.0F / ((1 << arrayOfInt1[i]) - 1));
/*     */           }
/*     */           else {
/* 428 */             arrayOfFloat1[i] = 1.0F;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 433 */       localObject2 = new ComponentSampleModel(0, paramRaster.getWidth(), paramRaster.getHeight(), j, j * paramRaster.getWidth(), arrayOfInt2);
/*     */       
/* 435 */       WritableRaster localWritableRaster = Raster.createWritableRaster((SampleModel)localObject2, new Point(paramRaster
/* 436 */         .getMinX(), paramRaster.getMinY()));
/*     */       
/* 438 */       if (i2 != 0) {
/* 439 */         localWritableRaster.setRect(paramRaster);
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/* 448 */         float[] arrayOfFloat2 = new float[j];
/*     */         
/* 450 */         RescaleOp localRescaleOp = new RescaleOp(arrayOfFloat1, arrayOfFloat2, null);
/*     */         
/* 452 */         localRescaleOp.filter(paramRaster, localWritableRaster);
/* 453 */         if (i1 != 0) {
/* 454 */           int[] arrayOfInt3 = new int[j];
/* 455 */           for (i = 0; i < j; i++) {
/* 456 */             arrayOfInt3[i] = 8;
/*     */           }
/*     */           
/*     */ 
/* 460 */           ComponentColorModel localComponentColorModel = new ComponentColorModel(paramColorModel.getColorSpace(), arrayOfInt3, true, true, 3, 0);
/*     */           
/*     */ 
/* 463 */           localComponentColorModel.coerceData(localWritableRaster, false);
/*     */         }
/*     */       }
/*     */       
/* 467 */       localDataBuffer = localWritableRaster.getDataBuffer();
/* 468 */       localObject1 = ((DataBufferByte)localDataBuffer).getData();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 475 */       n = localDataBuffer.getOffset() + ((ComponentSampleModel)localObject2).getOffset(0, 0);
/* 476 */       m = ((ComponentSampleModel)localObject2).getScanlineStride();
/*     */     }
/*     */     
/* 479 */     verify(n, m, localDataBuffer.getSize());
/*     */     
/* 481 */     writeJPEGStream(this.param, paramColorModel, this.outStream, localObject1, n, m);
/*     */   }
/*     */   
/*     */   private void verify(int paramInt1, int paramInt2, int paramInt3)
/*     */     throws ImageFormatException
/*     */   {
/* 487 */     int i = this.param.getWidth();
/* 488 */     int j = this.param.getHeight();
/*     */     
/*     */ 
/* 491 */     int k = this.pack ? 1 : this.param.getNumComponents();
/*     */     
/* 493 */     if ((i <= 0) || (j <= 0) || (j > Integer.MAX_VALUE / i))
/*     */     {
/* 495 */       throw new ImageFormatException("Invalid image dimensions");
/*     */     }
/*     */     
/* 498 */     if ((paramInt2 < 0) || (paramInt2 > Integer.MAX_VALUE / j) || (paramInt2 > paramInt3))
/*     */     {
/*     */ 
/* 501 */       throw new ImageFormatException("Invalid scanline stride: " + paramInt2);
/*     */     }
/*     */     
/*     */ 
/* 505 */     int m = (j - 1) * paramInt2;
/*     */     
/* 507 */     if ((k < 0) || (k > Integer.MAX_VALUE / i) || (k > paramInt3) || (k * i > paramInt2))
/*     */     {
/*     */ 
/*     */ 
/* 511 */       throw new ImageFormatException("Invalid pixel stride: " + k);
/*     */     }
/*     */     
/*     */ 
/* 515 */     int n = i * k;
/* 516 */     if (n > Integer.MAX_VALUE - m) {
/* 517 */       throw new ImageFormatException("Invalid raster attributes");
/*     */     }
/*     */     
/* 520 */     int i1 = m + n;
/* 521 */     if ((paramInt1 < 0) || (paramInt1 > Integer.MAX_VALUE - i1)) {
/* 522 */       throw new ImageFormatException("Invalid data offset");
/*     */     }
/*     */     
/* 525 */     int i2 = paramInt1 + i1;
/* 526 */     if (i2 > paramInt3) {
/* 527 */       throw new ImageFormatException("Computed buffer size doesn't match DataBuffer");
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
/*     */   private int getNearestColorId(ColorModel paramColorModel)
/*     */   {
/* 544 */     ColorSpace localColorSpace = paramColorModel.getColorSpace();
/* 545 */     switch (localColorSpace.getType()) {
/*     */     case 5: 
/* 547 */       if (paramColorModel.hasAlpha()) return 6;
/* 548 */       return 2;
/*     */     }
/*     */     
/* 551 */     return getDefaultColorId(paramColorModel);
/*     */   }
/*     */   
/*     */   private native void initEncoder(Class paramClass);
/*     */   
/*     */   private synchronized native void writeJPEGStream(JPEGEncodeParam paramJPEGEncodeParam, ColorModel paramColorModel, OutputStream paramOutputStream, Object paramObject, int paramInt1, int paramInt2)
/*     */     throws IOException, ImageFormatException;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\image\codec\JPEGImageEncoderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
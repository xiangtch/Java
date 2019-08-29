/*     */ package sun.java2d.cmm.lcms;
/*     */ 
/*     */ import java.awt.color.CMMException;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.color.ICC_Profile;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.SampleModel;
/*     */ import java.awt.image.WritableRaster;
/*     */ import sun.java2d.cmm.ColorTransform;
/*     */ import sun.java2d.cmm.ProfileDeferralMgr;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LCMSTransform
/*     */   implements ColorTransform
/*     */ {
/*     */   long ID;
/*  59 */   private int inFormatter = 0;
/*  60 */   private boolean isInIntPacked = false;
/*  61 */   private int outFormatter = 0;
/*  62 */   private boolean isOutIntPacked = false;
/*     */   
/*     */   ICC_Profile[] profiles;
/*     */   
/*     */   LCMSProfile[] lcmsProfiles;
/*     */   int renderType;
/*     */   int transformType;
/*  69 */   private int numInComponents = -1;
/*  70 */   private int numOutComponents = -1;
/*     */   
/*  72 */   private Object disposerReferent = new Object();
/*     */   
/*     */   static
/*     */   {
/*  76 */     if (ProfileDeferralMgr.deferring) {
/*  77 */       ProfileDeferralMgr.activateProfiles();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public LCMSTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2)
/*     */   {
/*  85 */     this.profiles = new ICC_Profile[1];
/*  86 */     this.profiles[0] = paramICC_Profile;
/*  87 */     this.lcmsProfiles = new LCMSProfile[1];
/*  88 */     this.lcmsProfiles[0] = LCMS.getProfileID(paramICC_Profile);
/*  89 */     this.renderType = (paramInt1 == -1 ? 0 : paramInt1);
/*     */     
/*  91 */     this.transformType = paramInt2;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  98 */     this.numInComponents = this.profiles[0].getNumComponents();
/*  99 */     this.numOutComponents = this.profiles[(this.profiles.length - 1)].getNumComponents();
/*     */   }
/*     */   
/*     */   public LCMSTransform(ColorTransform[] paramArrayOfColorTransform) {
/* 103 */     int i = 0;
/* 104 */     for (int j = 0; j < paramArrayOfColorTransform.length; j++) {
/* 105 */       i += ((LCMSTransform)paramArrayOfColorTransform[j]).profiles.length;
/*     */     }
/* 107 */     this.profiles = new ICC_Profile[i];
/* 108 */     this.lcmsProfiles = new LCMSProfile[i];
/* 109 */     j = 0;
/* 110 */     for (int k = 0; k < paramArrayOfColorTransform.length; k++) {
/* 111 */       LCMSTransform localLCMSTransform = (LCMSTransform)paramArrayOfColorTransform[k];
/* 112 */       System.arraycopy(localLCMSTransform.profiles, 0, this.profiles, j, localLCMSTransform.profiles.length);
/*     */       
/* 114 */       System.arraycopy(localLCMSTransform.lcmsProfiles, 0, this.lcmsProfiles, j, localLCMSTransform.lcmsProfiles.length);
/*     */       
/* 116 */       j += localLCMSTransform.profiles.length;
/*     */     }
/* 118 */     this.renderType = ((LCMSTransform)paramArrayOfColorTransform[0]).renderType;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 125 */     this.numInComponents = this.profiles[0].getNumComponents();
/* 126 */     this.numOutComponents = this.profiles[(this.profiles.length - 1)].getNumComponents();
/*     */   }
/*     */   
/*     */   public int getNumInComponents() {
/* 130 */     return this.numInComponents;
/*     */   }
/*     */   
/*     */   public int getNumOutComponents() {
/* 134 */     return this.numOutComponents;
/*     */   }
/*     */   
/*     */ 
/*     */   private synchronized void doTransform(LCMSImageLayout paramLCMSImageLayout1, LCMSImageLayout paramLCMSImageLayout2)
/*     */   {
/* 140 */     if ((this.ID == 0L) || (this.inFormatter != paramLCMSImageLayout1.pixelType) || (this.isInIntPacked != paramLCMSImageLayout1.isIntPacked) || (this.outFormatter != paramLCMSImageLayout2.pixelType) || (this.isOutIntPacked != paramLCMSImageLayout2.isIntPacked))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 145 */       if (this.ID != 0L)
/*     */       {
/* 147 */         this.disposerReferent = new Object();
/*     */       }
/* 149 */       this.inFormatter = paramLCMSImageLayout1.pixelType;
/* 150 */       this.isInIntPacked = paramLCMSImageLayout1.isIntPacked;
/*     */       
/* 152 */       this.outFormatter = paramLCMSImageLayout2.pixelType;
/* 153 */       this.isOutIntPacked = paramLCMSImageLayout2.isIntPacked;
/*     */       
/* 155 */       this.ID = LCMS.createTransform(this.lcmsProfiles, this.renderType, this.inFormatter, this.isInIntPacked, this.outFormatter, this.isOutIntPacked, this.disposerReferent);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 161 */     LCMS.colorConvert(this, paramLCMSImageLayout1, paramLCMSImageLayout2);
/*     */   }
/*     */   
/*     */   public void colorConvert(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
/*     */     LCMSImageLayout localLCMSImageLayout2;
/*     */     LCMSImageLayout localLCMSImageLayout1;
/* 167 */     try { if (!paramBufferedImage2.getColorModel().hasAlpha()) {
/* 168 */         localLCMSImageLayout2 = LCMSImageLayout.createImageLayout(paramBufferedImage2);
/*     */         
/* 170 */         if (localLCMSImageLayout2 != null) {
/* 171 */           localLCMSImageLayout1 = LCMSImageLayout.createImageLayout(paramBufferedImage1);
/* 172 */           if (localLCMSImageLayout1 != null) {
/* 173 */             doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
/* 174 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (LCMSImageLayout.ImageLayoutException localImageLayoutException1) {
/* 179 */       throw new CMMException("Unable to convert images");
/*     */     }
/*     */     
/* 182 */     WritableRaster localWritableRaster1 = paramBufferedImage1.getRaster();
/* 183 */     WritableRaster localWritableRaster2 = paramBufferedImage2.getRaster();
/* 184 */     ColorModel localColorModel1 = paramBufferedImage1.getColorModel();
/* 185 */     ColorModel localColorModel2 = paramBufferedImage2.getColorModel();
/* 186 */     int i = paramBufferedImage1.getWidth();
/* 187 */     int j = paramBufferedImage1.getHeight();
/* 188 */     int k = localColorModel1.getNumColorComponents();
/* 189 */     int m = localColorModel2.getNumColorComponents();
/* 190 */     int n = 8;
/* 191 */     float f = 255.0F;
/* 192 */     for (int i1 = 0; i1 < k; i1++) {
/* 193 */       if (localColorModel1.getComponentSize(i1) > 8) {
/* 194 */         n = 16;
/* 195 */         f = 65535.0F;
/*     */       }
/*     */     }
/* 198 */     for (i1 = 0; i1 < m; i1++) {
/* 199 */       if (localColorModel2.getComponentSize(i1) > 8) {
/* 200 */         n = 16;
/* 201 */         f = 65535.0F;
/*     */       }
/*     */     }
/* 204 */     float[] arrayOfFloat1 = new float[k];
/* 205 */     float[] arrayOfFloat2 = new float[k];
/* 206 */     ColorSpace localColorSpace = localColorModel1.getColorSpace();
/* 207 */     for (int i2 = 0; i2 < k; i2++) {
/* 208 */       arrayOfFloat1[i2] = localColorSpace.getMinValue(i2);
/* 209 */       arrayOfFloat2[i2] = (f / (localColorSpace.getMaxValue(i2) - arrayOfFloat1[i2]));
/*     */     }
/* 211 */     localColorSpace = localColorModel2.getColorSpace();
/* 212 */     float[] arrayOfFloat3 = new float[m];
/* 213 */     float[] arrayOfFloat4 = new float[m];
/* 214 */     for (int i3 = 0; i3 < m; i3++) {
/* 215 */       arrayOfFloat3[i3] = localColorSpace.getMinValue(i3);
/* 216 */       arrayOfFloat4[i3] = ((localColorSpace.getMaxValue(i3) - arrayOfFloat3[i3]) / f);
/*     */     }
/* 218 */     boolean bool = localColorModel2.hasAlpha();
/* 219 */     int i4 = (localColorModel1.hasAlpha()) && (bool) ? 1 : 0;
/*     */     float[] arrayOfFloat5;
/* 221 */     if (bool) {
/* 222 */       arrayOfFloat5 = new float[m + 1];
/*     */     } else
/* 224 */       arrayOfFloat5 = new float[m];
/*     */     Object localObject1;
/* 226 */     Object localObject2; float[] arrayOfFloat7; Object localObject3; float[] arrayOfFloat6; int i5; int i8; int i9; if (n == 8) {
/* 227 */       localObject1 = new byte[i * k];
/* 228 */       localObject2 = new byte[i * m];
/*     */       
/*     */ 
/* 231 */       arrayOfFloat7 = null;
/* 232 */       if (i4 != 0) {
/* 233 */         arrayOfFloat7 = new float[i];
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 241 */         localLCMSImageLayout1 = new LCMSImageLayout((byte[])localObject1, localObject1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), getNumInComponents());
/*     */         
/*     */ 
/*     */ 
/* 245 */         localLCMSImageLayout2 = new LCMSImageLayout((byte[])localObject2, localObject2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), getNumOutComponents());
/*     */       } catch (LCMSImageLayout.ImageLayoutException localImageLayoutException2) {
/* 247 */         throw new CMMException("Unable to convert images");
/*     */       }
/*     */       
/* 250 */       for (int i6 = 0; i6 < j; i6++)
/*     */       {
/* 252 */         localObject3 = null;
/* 253 */         arrayOfFloat6 = null;
/* 254 */         i5 = 0;
/* 255 */         for (i8 = 0; i8 < i; i8++) {
/* 256 */           localObject3 = localWritableRaster1.getDataElements(i8, i6, localObject3);
/* 257 */           arrayOfFloat6 = localColorModel1.getNormalizedComponents(localObject3, arrayOfFloat6, 0);
/* 258 */           for (i9 = 0; i9 < k; i9++) {
/* 259 */             localObject1[(i5++)] = ((byte)(int)((arrayOfFloat6[i9] - arrayOfFloat1[i9]) * arrayOfFloat2[i9] + 0.5F));
/*     */           }
/*     */           
/*     */ 
/* 263 */           if (i4 != 0) {
/* 264 */             arrayOfFloat7[i8] = arrayOfFloat6[k];
/*     */           }
/*     */         }
/*     */         
/* 268 */         doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
/*     */         
/*     */ 
/* 271 */         localObject3 = null;
/* 272 */         i5 = 0;
/* 273 */         for (i8 = 0; i8 < i; i8++) {
/* 274 */           for (i9 = 0; i9 < m; i9++) {
/* 275 */             arrayOfFloat5[i9] = ((localObject2[(i5++)] & 0xFF) * arrayOfFloat4[i9] + arrayOfFloat3[i9]);
/*     */           }
/*     */           
/* 278 */           if (i4 != 0) {
/* 279 */             arrayOfFloat5[m] = arrayOfFloat7[i8];
/* 280 */           } else if (bool) {
/* 281 */             arrayOfFloat5[m] = 1.0F;
/*     */           }
/* 283 */           localObject3 = localColorModel2.getDataElements(arrayOfFloat5, 0, localObject3);
/* 284 */           localWritableRaster2.setDataElements(i8, i6, localObject3);
/*     */         }
/*     */       }
/*     */     } else {
/* 288 */       localObject1 = new short[i * k];
/* 289 */       localObject2 = new short[i * m];
/*     */       
/*     */ 
/* 292 */       arrayOfFloat7 = null;
/* 293 */       if (i4 != 0) {
/* 294 */         arrayOfFloat7 = new float[i];
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 301 */         localLCMSImageLayout1 = new LCMSImageLayout((short[])localObject1, localObject1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 306 */         localLCMSImageLayout2 = new LCMSImageLayout((short[])localObject2, localObject2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
/*     */       } catch (LCMSImageLayout.ImageLayoutException localImageLayoutException3) {
/* 308 */         throw new CMMException("Unable to convert images");
/*     */       }
/*     */       
/* 311 */       for (int i7 = 0; i7 < j; i7++)
/*     */       {
/* 313 */         localObject3 = null;
/* 314 */         arrayOfFloat6 = null;
/* 315 */         i5 = 0;
/* 316 */         for (i8 = 0; i8 < i; i8++) {
/* 317 */           localObject3 = localWritableRaster1.getDataElements(i8, i7, localObject3);
/* 318 */           arrayOfFloat6 = localColorModel1.getNormalizedComponents(localObject3, arrayOfFloat6, 0);
/* 319 */           for (i9 = 0; i9 < k; i9++) {
/* 320 */             localObject1[(i5++)] = ((short)(int)((arrayOfFloat6[i9] - arrayOfFloat1[i9]) * arrayOfFloat2[i9] + 0.5F));
/*     */           }
/*     */           
/*     */ 
/* 324 */           if (i4 != 0) {
/* 325 */             arrayOfFloat7[i8] = arrayOfFloat6[k];
/*     */           }
/*     */         }
/*     */         
/* 329 */         doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
/*     */         
/*     */ 
/* 332 */         localObject3 = null;
/* 333 */         i5 = 0;
/* 334 */         for (i8 = 0; i8 < i; i8++) {
/* 335 */           for (i9 = 0; i9 < m; i9++) {
/* 336 */             arrayOfFloat5[i9] = ((localObject2[(i5++)] & 0xFFFF) * arrayOfFloat4[i9] + arrayOfFloat3[i9]);
/*     */           }
/*     */           
/* 339 */           if (i4 != 0) {
/* 340 */             arrayOfFloat5[m] = arrayOfFloat7[i8];
/* 341 */           } else if (bool) {
/* 342 */             arrayOfFloat5[m] = 1.0F;
/*     */           }
/* 344 */           localObject3 = localColorModel2.getDataElements(arrayOfFloat5, 0, localObject3);
/* 345 */           localWritableRaster2.setDataElements(i8, i7, localObject3);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4)
/*     */   {
/* 357 */     SampleModel localSampleModel1 = paramRaster.getSampleModel();
/* 358 */     SampleModel localSampleModel2 = paramWritableRaster.getSampleModel();
/* 359 */     int i = paramRaster.getTransferType();
/* 360 */     int j = paramWritableRaster.getTransferType();
/*     */     int k;
/* 362 */     if ((i == 4) || (i == 5))
/*     */     {
/* 364 */       k = 1;
/*     */     } else
/* 366 */       k = 0;
/*     */     int m;
/* 368 */     if ((j == 4) || (j == 5))
/*     */     {
/* 370 */       m = 1;
/*     */     } else {
/* 372 */       m = 0;
/*     */     }
/* 374 */     int n = paramRaster.getWidth();
/* 375 */     int i1 = paramRaster.getHeight();
/* 376 */     int i2 = paramRaster.getNumBands();
/* 377 */     int i3 = paramWritableRaster.getNumBands();
/* 378 */     float[] arrayOfFloat1 = new float[i2];
/* 379 */     float[] arrayOfFloat2 = new float[i3];
/* 380 */     float[] arrayOfFloat3 = new float[i2];
/* 381 */     float[] arrayOfFloat4 = new float[i3];
/* 382 */     for (int i4 = 0; i4 < i2; i4++) {
/* 383 */       if (k != 0) {
/* 384 */         arrayOfFloat1[i4] = (65535.0F / (paramArrayOfFloat2[i4] - paramArrayOfFloat1[i4]));
/* 385 */         arrayOfFloat3[i4] = paramArrayOfFloat1[i4];
/*     */       } else {
/* 387 */         if (i == 2) {
/* 388 */           arrayOfFloat1[i4] = 2.0000305F;
/*     */         }
/*     */         else {
/* 391 */           arrayOfFloat1[i4] = (65535.0F / ((1 << localSampleModel1.getSampleSize(i4)) - 1));
/*     */         }
/* 393 */         arrayOfFloat3[i4] = 0.0F;
/*     */       }
/*     */     }
/* 396 */     for (i4 = 0; i4 < i3; i4++) {
/* 397 */       if (m != 0) {
/* 398 */         arrayOfFloat2[i4] = ((paramArrayOfFloat4[i4] - paramArrayOfFloat3[i4]) / 65535.0F);
/* 399 */         arrayOfFloat4[i4] = paramArrayOfFloat3[i4];
/*     */       } else {
/* 401 */         if (j == 2) {
/* 402 */           arrayOfFloat2[i4] = 0.49999237F;
/*     */         }
/*     */         else {
/* 405 */           arrayOfFloat2[i4] = (((1 << localSampleModel2.getSampleSize(i4)) - 1) / 65535.0F);
/*     */         }
/*     */         
/* 408 */         arrayOfFloat4[i4] = 0.0F;
/*     */       }
/*     */     }
/* 411 */     i4 = paramRaster.getMinY();
/* 412 */     int i5 = paramWritableRaster.getMinY();
/*     */     
/*     */ 
/* 415 */     short[] arrayOfShort1 = new short[n * i2];
/* 416 */     short[] arrayOfShort2 = new short[n * i3];
/*     */     
/*     */     LCMSImageLayout localLCMSImageLayout1;
/*     */     LCMSImageLayout localLCMSImageLayout2;
/*     */     try
/*     */     {
/* 422 */       localLCMSImageLayout1 = new LCMSImageLayout(arrayOfShort1, arrayOfShort1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 427 */       localLCMSImageLayout2 = new LCMSImageLayout(arrayOfShort2, arrayOfShort2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
/*     */     } catch (LCMSImageLayout.ImageLayoutException localImageLayoutException) {
/* 429 */       throw new CMMException("Unable to convert rasters");
/*     */     }
/*     */     
/* 432 */     for (int i9 = 0; i9 < i1; i5++)
/*     */     {
/* 434 */       int i6 = paramRaster.getMinX();
/* 435 */       int i8 = 0;
/* 436 */       int i11; float f; for (int i10 = 0; i10 < n; i6++) {
/* 437 */         for (i11 = 0; i11 < i2; i11++) {
/* 438 */           f = paramRaster.getSampleFloat(i6, i4, i11);
/* 439 */           arrayOfShort1[(i8++)] = ((short)(int)((f - arrayOfFloat3[i11]) * arrayOfFloat1[i11] + 0.5F));
/*     */         }
/* 436 */         i10++;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 445 */       doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
/*     */       
/*     */ 
/* 448 */       int i7 = paramWritableRaster.getMinX();
/* 449 */       i8 = 0;
/* 450 */       for (i10 = 0; i10 < n; i7++) {
/* 451 */         for (i11 = 0; i11 < i3; i11++) {
/* 452 */           f = (arrayOfShort2[(i8++)] & 0xFFFF) * arrayOfFloat2[i11] + arrayOfFloat4[i11];
/*     */           
/* 454 */           paramWritableRaster.setSample(i7, i5, i11, f);
/*     */         }
/* 450 */         i10++;
/*     */       }
/* 432 */       i9++;i4++;
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
/*     */   public void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster)
/*     */   {
/* 463 */     LCMSImageLayout localLCMSImageLayout2 = LCMSImageLayout.createImageLayout(paramWritableRaster);
/* 464 */     LCMSImageLayout localLCMSImageLayout1; if (localLCMSImageLayout2 != null) {
/* 465 */       localLCMSImageLayout1 = LCMSImageLayout.createImageLayout(paramRaster);
/* 466 */       if (localLCMSImageLayout1 != null) {
/* 467 */         doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
/* 468 */         return;
/*     */       }
/*     */     }
/*     */     
/* 472 */     SampleModel localSampleModel1 = paramRaster.getSampleModel();
/* 473 */     SampleModel localSampleModel2 = paramWritableRaster.getSampleModel();
/* 474 */     int i = paramRaster.getTransferType();
/* 475 */     int j = paramWritableRaster.getTransferType();
/* 476 */     int k = paramRaster.getWidth();
/* 477 */     int m = paramRaster.getHeight();
/* 478 */     int n = paramRaster.getNumBands();
/* 479 */     int i1 = paramWritableRaster.getNumBands();
/* 480 */     int i2 = 8;
/* 481 */     float f = 255.0F;
/* 482 */     for (int i3 = 0; i3 < n; i3++) {
/* 483 */       if (localSampleModel1.getSampleSize(i3) > 8) {
/* 484 */         i2 = 16;
/* 485 */         f = 65535.0F;
/*     */       }
/*     */     }
/* 488 */     for (i3 = 0; i3 < i1; i3++) {
/* 489 */       if (localSampleModel2.getSampleSize(i3) > 8) {
/* 490 */         i2 = 16;
/* 491 */         f = 65535.0F;
/*     */       }
/*     */     }
/* 494 */     float[] arrayOfFloat1 = new float[n];
/* 495 */     float[] arrayOfFloat2 = new float[i1];
/* 496 */     for (int i4 = 0; i4 < n; i4++) {
/* 497 */       if (i == 2) {
/* 498 */         arrayOfFloat1[i4] = (f / 32767.0F);
/*     */       }
/*     */       else {
/* 501 */         arrayOfFloat1[i4] = (f / ((1 << localSampleModel1.getSampleSize(i4)) - 1));
/*     */       }
/*     */     }
/* 504 */     for (i4 = 0; i4 < i1; i4++) {
/* 505 */       if (j == 2) {
/* 506 */         arrayOfFloat2[i4] = (32767.0F / f);
/*     */       }
/*     */       else {
/* 509 */         arrayOfFloat2[i4] = (((1 << localSampleModel2.getSampleSize(i4)) - 1) / f);
/*     */       }
/*     */     }
/* 512 */     i4 = paramRaster.getMinY();
/* 513 */     int i5 = paramWritableRaster.getMinY();
/*     */     Object localObject1;
/*     */     Object localObject2;
/* 516 */     int i6; int i9; int i12; int i13; int i8; int i7; if (i2 == 8) {
/* 517 */       localObject1 = new byte[k * n];
/* 518 */       localObject2 = new byte[k * i1];
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 525 */         localLCMSImageLayout1 = new LCMSImageLayout((byte[])localObject1, localObject1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), getNumInComponents());
/*     */         
/*     */ 
/*     */ 
/* 529 */         localLCMSImageLayout2 = new LCMSImageLayout((byte[])localObject2, localObject2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), getNumOutComponents());
/*     */       } catch (LCMSImageLayout.ImageLayoutException localImageLayoutException1) {
/* 531 */         throw new CMMException("Unable to convert rasters");
/*     */       }
/*     */       
/* 534 */       for (int i10 = 0; i10 < m; i5++)
/*     */       {
/* 536 */         i6 = paramRaster.getMinX();
/* 537 */         i9 = 0;
/* 538 */         for (i12 = 0; i12 < k; i6++) {
/* 539 */           for (i13 = 0; i13 < n; i13++) {
/* 540 */             i8 = paramRaster.getSample(i6, i4, i13);
/* 541 */             localObject1[(i9++)] = ((byte)(int)(i8 * arrayOfFloat1[i13] + 0.5F));
/*     */           }
/* 538 */           i12++;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 547 */         doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
/*     */         
/*     */ 
/* 550 */         i7 = paramWritableRaster.getMinX();
/* 551 */         i9 = 0;
/* 552 */         for (i12 = 0; i12 < k; i7++) {
/* 553 */           for (i13 = 0; i13 < i1; i13++) {
/* 554 */             i8 = (int)((localObject2[(i9++)] & 0xFF) * arrayOfFloat2[i13] + 0.5F);
/*     */             
/* 556 */             paramWritableRaster.setSample(i7, i5, i13, i8);
/*     */           }
/* 552 */           i12++;
/*     */         }
/* 534 */         i10++;i4++;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 561 */       localObject1 = new short[k * n];
/* 562 */       localObject2 = new short[k * i1];
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 569 */         localLCMSImageLayout1 = new LCMSImageLayout((short[])localObject1, localObject1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 574 */         localLCMSImageLayout2 = new LCMSImageLayout((short[])localObject2, localObject2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
/*     */       } catch (LCMSImageLayout.ImageLayoutException localImageLayoutException2) {
/* 576 */         throw new CMMException("Unable to convert rasters");
/*     */       }
/*     */       
/* 579 */       for (int i11 = 0; i11 < m; i5++)
/*     */       {
/* 581 */         i6 = paramRaster.getMinX();
/* 582 */         i9 = 0;
/* 583 */         for (i12 = 0; i12 < k; i6++) {
/* 584 */           for (i13 = 0; i13 < n; i13++) {
/* 585 */             i8 = paramRaster.getSample(i6, i4, i13);
/* 586 */             localObject1[(i9++)] = ((short)(int)(i8 * arrayOfFloat1[i13] + 0.5F));
/*     */           }
/* 583 */           i12++;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 592 */         doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
/*     */         
/*     */ 
/* 595 */         i7 = paramWritableRaster.getMinX();
/* 596 */         i9 = 0;
/* 597 */         for (i12 = 0; i12 < k; i7++) {
/* 598 */           for (i13 = 0; i13 < i1; i13++) {
/* 599 */             i8 = (int)((localObject2[(i9++)] & 0xFFFF) * arrayOfFloat2[i13] + 0.5F);
/*     */             
/* 601 */             paramWritableRaster.setSample(i7, i5, i13, i8);
/*     */           }
/* 597 */           i12++;
/*     */         }
/* 579 */         i11++;i4++;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public short[] colorConvert(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
/*     */   {
/* 614 */     if (paramArrayOfShort2 == null) {
/* 615 */       paramArrayOfShort2 = new short[paramArrayOfShort1.length / getNumInComponents() * getNumOutComponents()];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 622 */       LCMSImageLayout localLCMSImageLayout1 = new LCMSImageLayout(paramArrayOfShort1, paramArrayOfShort1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 627 */       LCMSImageLayout localLCMSImageLayout2 = new LCMSImageLayout(paramArrayOfShort2, paramArrayOfShort2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
/*     */       
/* 629 */       doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
/*     */       
/* 631 */       return paramArrayOfShort2;
/*     */     } catch (LCMSImageLayout.ImageLayoutException localImageLayoutException) {
/* 633 */       throw new CMMException("Unable to convert data");
/*     */     }
/*     */   }
/*     */   
/*     */   public byte[] colorConvert(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
/* 638 */     if (paramArrayOfByte2 == null) {
/* 639 */       paramArrayOfByte2 = new byte[paramArrayOfByte1.length / getNumInComponents() * getNumOutComponents()];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 646 */       LCMSImageLayout localLCMSImageLayout1 = new LCMSImageLayout(paramArrayOfByte1, paramArrayOfByte1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), getNumInComponents());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 651 */       LCMSImageLayout localLCMSImageLayout2 = new LCMSImageLayout(paramArrayOfByte2, paramArrayOfByte2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), getNumOutComponents());
/*     */       
/* 653 */       doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
/*     */       
/* 655 */       return paramArrayOfByte2;
/*     */     } catch (LCMSImageLayout.ImageLayoutException localImageLayoutException) {
/* 657 */       throw new CMMException("Unable to convert data");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\cmm\lcms\LCMSTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
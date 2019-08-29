/*     */ package sun.security.rsa;
/*     */ 
/*     */ import java.security.DigestException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.MGF1ParameterSpec;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.crypto.BadPaddingException;
/*     */ import javax.crypto.spec.OAEPParameterSpec;
/*     */ import javax.crypto.spec.PSource;
/*     */ import javax.crypto.spec.PSource.PSpecified;
/*     */ import sun.security.jca.JCAUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RSAPadding
/*     */ {
/*     */   public static final int PAD_BLOCKTYPE_1 = 1;
/*     */   public static final int PAD_BLOCKTYPE_2 = 2;
/*     */   public static final int PAD_NONE = 3;
/*     */   public static final int PAD_OAEP_MGF1 = 4;
/*     */   private final int type;
/*     */   private final int paddedSize;
/*     */   private SecureRandom random;
/*     */   private final int maxDataSize;
/*     */   private MessageDigest md;
/*     */   private MessageDigest mgfMd;
/*     */   private byte[] lHash;
/*     */   
/*     */   public static RSAPadding getInstance(int paramInt1, int paramInt2)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 123 */     return new RSAPadding(paramInt1, paramInt2, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static RSAPadding getInstance(int paramInt1, int paramInt2, SecureRandom paramSecureRandom)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 133 */     return new RSAPadding(paramInt1, paramInt2, paramSecureRandom, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static RSAPadding getInstance(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, OAEPParameterSpec paramOAEPParameterSpec)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 143 */     return new RSAPadding(paramInt1, paramInt2, paramSecureRandom, paramOAEPParameterSpec);
/*     */   }
/*     */   
/*     */ 
/*     */   private RSAPadding(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, OAEPParameterSpec paramOAEPParameterSpec)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 150 */     this.type = paramInt1;
/* 151 */     this.paddedSize = paramInt2;
/* 152 */     this.random = paramSecureRandom;
/* 153 */     if (paramInt2 < 64)
/*     */     {
/* 155 */       throw new InvalidKeyException("Padded size must be at least 64");
/*     */     }
/* 157 */     switch (paramInt1) {
/*     */     case 1: 
/*     */     case 2: 
/* 160 */       this.maxDataSize = (paramInt2 - 11);
/* 161 */       break;
/*     */     case 3: 
/* 163 */       this.maxDataSize = paramInt2;
/* 164 */       break;
/*     */     case 4: 
/* 166 */       String str1 = "SHA-1";
/* 167 */       String str2 = "SHA-1";
/* 168 */       byte[] arrayOfByte = null;
/*     */       try {
/* 170 */         if (paramOAEPParameterSpec != null) {
/* 171 */           str1 = paramOAEPParameterSpec.getDigestAlgorithm();
/* 172 */           String str3 = paramOAEPParameterSpec.getMGFAlgorithm();
/* 173 */           if (!str3.equalsIgnoreCase("MGF1")) {
/* 174 */             throw new InvalidAlgorithmParameterException("Unsupported MGF algo: " + str3);
/*     */           }
/*     */           
/*     */ 
/* 178 */           str2 = ((MGF1ParameterSpec)paramOAEPParameterSpec.getMGFParameters()).getDigestAlgorithm();
/* 179 */           PSource localPSource = paramOAEPParameterSpec.getPSource();
/* 180 */           String str4 = localPSource.getAlgorithm();
/* 181 */           if (!str4.equalsIgnoreCase("PSpecified")) {
/* 182 */             throw new InvalidAlgorithmParameterException("Unsupported pSource algo: " + str4);
/*     */           }
/*     */           
/* 185 */           arrayOfByte = ((PSource.PSpecified)localPSource).getValue();
/*     */         }
/* 187 */         this.md = MessageDigest.getInstance(str1);
/* 188 */         this.mgfMd = MessageDigest.getInstance(str2);
/*     */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 190 */         throw new InvalidKeyException("Digest " + str1 + " not available", localNoSuchAlgorithmException);
/*     */       }
/*     */       
/* 193 */       this.lHash = getInitialHash(this.md, arrayOfByte);
/* 194 */       int i = this.lHash.length;
/* 195 */       this.maxDataSize = (paramInt2 - 2 - 2 * i);
/* 196 */       if (this.maxDataSize <= 0) {
/* 197 */         throw new InvalidKeyException("Key is too short for encryption using OAEPPadding with " + str1 + " and MGF1" + str2);
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     default: 
/* 203 */       throw new InvalidKeyException("Invalid padding: " + paramInt1);
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/* 209 */   private static final Map<String, byte[]> emptyHashes = Collections.synchronizedMap(new HashMap());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static byte[] getInitialHash(MessageDigest paramMessageDigest, byte[] paramArrayOfByte)
/*     */   {
/*     */     byte[] arrayOfByte;
/*     */     
/*     */ 
/*     */ 
/* 221 */     if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
/* 222 */       String str = paramMessageDigest.getAlgorithm();
/* 223 */       arrayOfByte = (byte[])emptyHashes.get(str);
/* 224 */       if (arrayOfByte == null) {
/* 225 */         arrayOfByte = paramMessageDigest.digest();
/* 226 */         emptyHashes.put(str, arrayOfByte);
/*     */       }
/*     */     } else {
/* 229 */       arrayOfByte = paramMessageDigest.digest(paramArrayOfByte);
/*     */     }
/* 231 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaxDataSize()
/*     */   {
/* 239 */     return this.maxDataSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] pad(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws BadPaddingException
/*     */   {
/* 247 */     return pad(RSACore.convert(paramArrayOfByte, paramInt1, paramInt2));
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] pad(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 254 */     if (paramArrayOfByte.length > this.maxDataSize) {
/* 255 */       throw new BadPaddingException("Data must be shorter than " + (this.maxDataSize + 1) + " bytes but received " + paramArrayOfByte.length + " bytes.");
/*     */     }
/*     */     
/*     */ 
/* 259 */     switch (this.type) {
/*     */     case 3: 
/* 261 */       return paramArrayOfByte;
/*     */     case 1: 
/*     */     case 2: 
/* 264 */       return padV15(paramArrayOfByte);
/*     */     case 4: 
/* 266 */       return padOAEP(paramArrayOfByte);
/*     */     }
/* 268 */     throw new AssertionError();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] unpad(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws BadPaddingException
/*     */   {
/* 277 */     return unpad(RSACore.convert(paramArrayOfByte, paramInt1, paramInt2));
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] unpad(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 284 */     if (paramArrayOfByte.length != this.paddedSize) {
/* 285 */       throw new BadPaddingException("Decryption error.The padded array length (" + paramArrayOfByte.length + ") is not the specified padded size (" + this.paddedSize + ")");
/*     */     }
/*     */     
/*     */ 
/* 289 */     switch (this.type) {
/*     */     case 3: 
/* 291 */       return paramArrayOfByte;
/*     */     case 1: 
/*     */     case 2: 
/* 294 */       return unpadV15(paramArrayOfByte);
/*     */     case 4: 
/* 296 */       return unpadOAEP(paramArrayOfByte);
/*     */     }
/* 298 */     throw new AssertionError();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private byte[] padV15(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 306 */     byte[] arrayOfByte1 = new byte[this.paddedSize];
/* 307 */     System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, this.paddedSize - paramArrayOfByte.length, paramArrayOfByte.length);
/*     */     
/* 309 */     int i = this.paddedSize - 3 - paramArrayOfByte.length;
/* 310 */     int j = 0;
/* 311 */     arrayOfByte1[(j++)] = 0;
/* 312 */     arrayOfByte1[(j++)] = ((byte)this.type);
/* 313 */     if (this.type == 1)
/*     */     {
/* 315 */       while (i-- > 0) {
/* 316 */         arrayOfByte1[(j++)] = -1;
/*     */       }
/*     */     }
/*     */     
/* 320 */     if (this.random == null) {
/* 321 */       this.random = JCAUtil.getSecureRandom();
/*     */     }
/*     */     
/*     */ 
/* 325 */     byte[] arrayOfByte2 = new byte[64];
/* 326 */     int k = -1;
/* 327 */     while (i-- > 0) {
/*     */       int m;
/*     */       do {
/* 330 */         if (k < 0) {
/* 331 */           this.random.nextBytes(arrayOfByte2);
/* 332 */           k = arrayOfByte2.length - 1;
/*     */         }
/* 334 */         m = arrayOfByte2[(k--)] & 0xFF;
/* 335 */       } while (m == 0);
/* 336 */       arrayOfByte1[(j++)] = ((byte)m);
/*     */     }
/*     */     
/* 339 */     return arrayOfByte1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] unpadV15(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 348 */     int i = 0;
/* 349 */     int j = 0;
/*     */     
/* 351 */     if (paramArrayOfByte[(i++)] != 0) {
/* 352 */       j = 1;
/*     */     }
/* 354 */     if (paramArrayOfByte[(i++)] != this.type) {
/* 355 */       j = 1;
/*     */     }
/* 357 */     int k = 0;
/* 358 */     while (i < paramArrayOfByte.length) {
/* 359 */       m = paramArrayOfByte[(i++)] & 0xFF;
/* 360 */       if ((m == 0) && (k == 0)) {
/* 361 */         k = i;
/*     */       }
/* 363 */       if ((i == paramArrayOfByte.length) && (k == 0)) {
/* 364 */         j = 1;
/*     */       }
/* 366 */       if ((this.type == 1) && (m != 255) && (k == 0))
/*     */       {
/* 368 */         j = 1;
/*     */       }
/*     */     }
/* 371 */     int m = paramArrayOfByte.length - k;
/* 372 */     if (m > this.maxDataSize) {
/* 373 */       j = 1;
/*     */     }
/*     */     
/*     */ 
/* 377 */     byte[] arrayOfByte1 = new byte[k];
/* 378 */     System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, k);
/*     */     
/* 380 */     byte[] arrayOfByte2 = new byte[m];
/* 381 */     System.arraycopy(paramArrayOfByte, k, arrayOfByte2, 0, m);
/*     */     
/* 383 */     BadPaddingException localBadPaddingException = new BadPaddingException("Decryption error");
/*     */     
/* 385 */     if (j != 0) {
/* 386 */       throw localBadPaddingException;
/*     */     }
/* 388 */     return arrayOfByte2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] padOAEP(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 397 */     if (this.random == null) {
/* 398 */       this.random = JCAUtil.getSecureRandom();
/*     */     }
/* 400 */     int i = this.lHash.length;
/*     */     
/*     */ 
/*     */ 
/* 404 */     byte[] arrayOfByte1 = new byte[i];
/* 405 */     this.random.nextBytes(arrayOfByte1);
/*     */     
/*     */ 
/* 408 */     byte[] arrayOfByte2 = new byte[this.paddedSize];
/*     */     
/*     */ 
/* 411 */     int j = 1;
/* 412 */     int k = i;
/*     */     
/*     */ 
/* 415 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, j, k);
/*     */     
/*     */ 
/*     */ 
/* 419 */     int m = i + 1;
/* 420 */     int n = arrayOfByte2.length - m;
/*     */     
/*     */ 
/* 423 */     int i1 = this.paddedSize - paramArrayOfByte.length;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 430 */     System.arraycopy(this.lHash, 0, arrayOfByte2, m, i);
/* 431 */     arrayOfByte2[(i1 - 1)] = 1;
/* 432 */     System.arraycopy(paramArrayOfByte, 0, arrayOfByte2, i1, paramArrayOfByte.length);
/*     */     
/*     */ 
/* 435 */     mgf1(arrayOfByte2, j, k, arrayOfByte2, m, n);
/*     */     
/*     */ 
/* 438 */     mgf1(arrayOfByte2, m, n, arrayOfByte2, j, k);
/*     */     
/* 440 */     return arrayOfByte2;
/*     */   }
/*     */   
/*     */ 
/*     */   private byte[] unpadOAEP(byte[] paramArrayOfByte)
/*     */     throws BadPaddingException
/*     */   {
/* 447 */     byte[] arrayOfByte1 = paramArrayOfByte;
/* 448 */     int i = 0;
/* 449 */     int j = this.lHash.length;
/*     */     
/* 451 */     if (arrayOfByte1[0] != 0) {
/* 452 */       i = 1;
/*     */     }
/*     */     
/* 455 */     int k = 1;
/* 456 */     int m = j;
/*     */     
/* 458 */     int n = j + 1;
/* 459 */     int i1 = arrayOfByte1.length - n;
/*     */     
/* 461 */     mgf1(arrayOfByte1, n, i1, arrayOfByte1, k, m);
/* 462 */     mgf1(arrayOfByte1, k, m, arrayOfByte1, n, i1);
/*     */     
/*     */ 
/* 465 */     for (int i2 = 0; i2 < j; i2++) {
/* 466 */       if (this.lHash[i2] != arrayOfByte1[(n + i2)]) {
/* 467 */         i = 1;
/*     */       }
/*     */     }
/*     */     
/* 471 */     i2 = n + j;
/* 472 */     int i3 = -1;
/*     */     
/* 474 */     for (int i4 = i2; i4 < arrayOfByte1.length; i4++) {
/* 475 */       int i5 = arrayOfByte1[i4];
/* 476 */       if ((i3 == -1) && 
/* 477 */         (i5 != 0))
/*     */       {
/* 479 */         if (i5 == 1) {
/* 480 */           i3 = i4;
/*     */         } else {
/* 482 */           i = 1;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 488 */     if (i3 == -1) {
/* 489 */       i = 1;
/* 490 */       i3 = arrayOfByte1.length - 1;
/*     */     }
/*     */     
/* 493 */     i4 = i3 + 1;
/*     */     
/*     */ 
/* 496 */     byte[] arrayOfByte2 = new byte[i4 - i2];
/* 497 */     System.arraycopy(arrayOfByte1, i2, arrayOfByte2, 0, arrayOfByte2.length);
/*     */     
/* 499 */     byte[] arrayOfByte3 = new byte[arrayOfByte1.length - i4];
/* 500 */     System.arraycopy(arrayOfByte1, i4, arrayOfByte3, 0, arrayOfByte3.length);
/*     */     
/* 502 */     BadPaddingException localBadPaddingException = new BadPaddingException("Decryption error");
/*     */     
/* 504 */     if (i != 0) {
/* 505 */       throw localBadPaddingException;
/*     */     }
/* 507 */     return arrayOfByte3;
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
/*     */   private void mgf1(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
/*     */     throws BadPaddingException
/*     */   {
/* 521 */     byte[] arrayOfByte1 = new byte[4];
/* 522 */     byte[] arrayOfByte2 = new byte[this.mgfMd.getDigestLength()];
/* 523 */     while (paramInt4 > 0) {
/* 524 */       this.mgfMd.update(paramArrayOfByte1, paramInt1, paramInt2);
/* 525 */       this.mgfMd.update(arrayOfByte1);
/*     */       try {
/* 527 */         this.mgfMd.digest(arrayOfByte2, 0, arrayOfByte2.length);
/*     */       }
/*     */       catch (DigestException localDigestException) {
/* 530 */         throw new BadPaddingException(localDigestException.toString());
/*     */       }
/* 532 */       for (int i = 0; (i < arrayOfByte2.length) && (paramInt4 > 0); paramInt4--) {
/* 533 */         int tmp95_92 = (paramInt3++); byte[] tmp95_88 = paramArrayOfByte2;tmp95_88[tmp95_92] = ((byte)(tmp95_88[tmp95_92] ^ arrayOfByte2[(i++)]));
/*     */       }
/* 535 */       if (paramInt4 > 0)
/*     */       {
/* 537 */         for (i = arrayOfByte1.length - 1;; i--) { int tmp130_128 = i;arrayOfByte1;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 537 */           if (((tmp130_126[tmp130_128] = (byte)(tmp130_126[tmp130_128] + 1)) != 0) || (i <= 0)) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\rsa\RSAPadding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
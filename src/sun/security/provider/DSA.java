/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.DigestException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.InvalidParameterException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.SignatureException;
/*     */ import java.security.SignatureSpi;
/*     */ import java.security.interfaces.DSAParams;
/*     */ import java.security.interfaces.DSAPrivateKey;
/*     */ import java.security.interfaces.DSAPublicKey;
/*     */ import java.util.Arrays;
/*     */ import sun.security.jca.JCAUtil;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class DSA
/*     */   extends SignatureSpi
/*     */ {
/*     */   private static final boolean debug = false;
/*     */   private static final int BLINDING_BITS = 7;
/*  75 */   private static final BigInteger BLINDING_CONSTANT = BigInteger.valueOf(128L);
/*     */   
/*     */ 
/*     */   private DSAParams params;
/*     */   
/*     */ 
/*     */   private BigInteger presetP;
/*     */   
/*     */ 
/*     */   private BigInteger presetQ;
/*     */   
/*     */ 
/*     */   private BigInteger presetG;
/*     */   
/*     */ 
/*     */   private BigInteger presetY;
/*     */   
/*     */   private BigInteger presetX;
/*     */   
/*     */   private SecureRandom signingRandom;
/*     */   
/*     */   private final MessageDigest md;
/*     */   
/*     */ 
/*     */   DSA(MessageDigest paramMessageDigest)
/*     */   {
/* 101 */     this.md = paramMessageDigest;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void checkKey(DSAParams paramDSAParams, int paramInt, String paramString)
/*     */     throws InvalidKeyException
/*     */   {
/* 109 */     int i = paramDSAParams.getQ().bitLength();
/* 110 */     if (i > paramInt) {
/* 111 */       throw new InvalidKeyException("The security strength of " + paramString + " digest algorithm is not sufficient for this key size");
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
/*     */   protected void engineInitSign(PrivateKey paramPrivateKey)
/*     */     throws InvalidKeyException
/*     */   {
/* 126 */     if (!(paramPrivateKey instanceof DSAPrivateKey)) {
/* 127 */       throw new InvalidKeyException("not a DSA private key: " + paramPrivateKey);
/*     */     }
/*     */     
/*     */ 
/* 131 */     DSAPrivateKey localDSAPrivateKey = (DSAPrivateKey)paramPrivateKey;
/*     */     
/*     */ 
/*     */ 
/* 135 */     DSAParams localDSAParams = localDSAPrivateKey.getParams();
/* 136 */     if (localDSAParams == null) {
/* 137 */       throw new InvalidKeyException("DSA private key lacks parameters");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 142 */     if (this.md.getAlgorithm() != "NullDigest20") {
/* 143 */       checkKey(localDSAParams, this.md.getDigestLength() * 8, this.md.getAlgorithm());
/*     */     }
/*     */     
/* 146 */     this.params = localDSAParams;
/* 147 */     this.presetX = localDSAPrivateKey.getX();
/* 148 */     this.presetY = null;
/* 149 */     this.presetP = localDSAParams.getP();
/* 150 */     this.presetQ = localDSAParams.getQ();
/* 151 */     this.presetG = localDSAParams.getG();
/* 152 */     this.md.reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void engineInitVerify(PublicKey paramPublicKey)
/*     */     throws InvalidKeyException
/*     */   {
/* 164 */     if (!(paramPublicKey instanceof DSAPublicKey)) {
/* 165 */       throw new InvalidKeyException("not a DSA public key: " + paramPublicKey);
/*     */     }
/*     */     
/* 168 */     DSAPublicKey localDSAPublicKey = (DSAPublicKey)paramPublicKey;
/*     */     
/*     */ 
/*     */ 
/* 172 */     DSAParams localDSAParams = localDSAPublicKey.getParams();
/* 173 */     if (localDSAParams == null) {
/* 174 */       throw new InvalidKeyException("DSA public key lacks parameters");
/*     */     }
/* 176 */     this.params = localDSAParams;
/* 177 */     this.presetY = localDSAPublicKey.getY();
/* 178 */     this.presetX = null;
/* 179 */     this.presetP = localDSAParams.getP();
/* 180 */     this.presetQ = localDSAParams.getQ();
/* 181 */     this.presetG = localDSAParams.getG();
/* 182 */     this.md.reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void engineUpdate(byte paramByte)
/*     */   {
/* 189 */     this.md.update(paramByte);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 196 */     this.md.update(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */   protected void engineUpdate(ByteBuffer paramByteBuffer) {
/* 200 */     this.md.update(paramByteBuffer);
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
/*     */   protected byte[] engineSign()
/*     */     throws SignatureException
/*     */   {
/* 219 */     BigInteger localBigInteger1 = generateK(this.presetQ);
/* 220 */     BigInteger localBigInteger2 = generateR(this.presetP, this.presetQ, this.presetG, localBigInteger1);
/* 221 */     BigInteger localBigInteger3 = generateS(this.presetX, this.presetQ, localBigInteger2, localBigInteger1);
/*     */     try
/*     */     {
/* 224 */       DerOutputStream localDerOutputStream = new DerOutputStream(100);
/* 225 */       localDerOutputStream.putInteger(localBigInteger2);
/* 226 */       localDerOutputStream.putInteger(localBigInteger3);
/*     */       
/* 228 */       DerValue localDerValue = new DerValue((byte)48, localDerOutputStream.toByteArray());
/*     */       
/* 230 */       return localDerValue.toByteArray();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 233 */       throw new SignatureException("error encoding signature");
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
/*     */   protected boolean engineVerify(byte[] paramArrayOfByte)
/*     */     throws SignatureException
/*     */   {
/* 251 */     return engineVerify(paramArrayOfByte, 0, paramArrayOfByte.length);
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
/*     */   protected boolean engineVerify(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws SignatureException
/*     */   {
/* 273 */     BigInteger localBigInteger1 = null;
/* 274 */     BigInteger localBigInteger2 = null;
/*     */     Object localObject;
/*     */     try
/*     */     {
/* 278 */       DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte, paramInt1, paramInt2, false);
/*     */       
/* 280 */       localObject = localDerInputStream.getSequence(2);
/*     */       
/*     */ 
/*     */ 
/* 284 */       if ((localObject.length != 2) || (localDerInputStream.available() != 0)) {
/* 285 */         throw new IOException("Invalid encoding for signature");
/*     */       }
/* 287 */       localBigInteger1 = localObject[0].getBigInteger();
/* 288 */       localBigInteger2 = localObject[1].getBigInteger();
/*     */     } catch (IOException localIOException) {
/* 290 */       throw new SignatureException("Invalid encoding for signature", localIOException);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 296 */     if (localBigInteger1.signum() < 0) {
/* 297 */       localBigInteger1 = new BigInteger(1, localBigInteger1.toByteArray());
/*     */     }
/* 299 */     if (localBigInteger2.signum() < 0) {
/* 300 */       localBigInteger2 = new BigInteger(1, localBigInteger2.toByteArray());
/*     */     }
/*     */     
/* 303 */     if ((localBigInteger1.compareTo(this.presetQ) == -1) && (localBigInteger2.compareTo(this.presetQ) == -1)) {
/* 304 */       BigInteger localBigInteger3 = generateW(this.presetP, this.presetQ, this.presetG, localBigInteger2);
/* 305 */       localObject = generateV(this.presetY, this.presetP, this.presetQ, this.presetG, localBigInteger3, localBigInteger1);
/* 306 */       return ((BigInteger)localObject).equals(localBigInteger1);
/*     */     }
/* 308 */     throw new SignatureException("invalid signature: out of range values");
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected void engineSetParameter(String paramString, Object paramObject)
/*     */   {
/* 314 */     throw new InvalidParameterException("No parameter accepted");
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected Object engineGetParameter(String paramString) {
/* 319 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private BigInteger generateR(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
/*     */   {
/* 327 */     SecureRandom localSecureRandom = getSigningRandom();
/*     */     
/* 329 */     BigInteger localBigInteger1 = new BigInteger(7, localSecureRandom);
/*     */     
/* 331 */     localBigInteger1 = localBigInteger1.add(BLINDING_CONSTANT);
/*     */     
/* 333 */     paramBigInteger4 = paramBigInteger4.add(paramBigInteger2.multiply(localBigInteger1));
/*     */     
/* 335 */     BigInteger localBigInteger2 = paramBigInteger3.modPow(paramBigInteger4, paramBigInteger1);
/* 336 */     return localBigInteger2.mod(paramBigInteger2);
/*     */   }
/*     */   
/*     */   private BigInteger generateS(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) throws SignatureException
/*     */   {
/*     */     byte[] arrayOfByte;
/*     */     try
/*     */     {
/* 344 */       arrayOfByte = this.md.digest();
/*     */     }
/*     */     catch (RuntimeException localRuntimeException) {
/* 347 */       throw new SignatureException(localRuntimeException.getMessage());
/*     */     }
/*     */     
/* 350 */     int i = paramBigInteger2.bitLength() / 8;
/* 351 */     if (i < arrayOfByte.length) {
/* 352 */       arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i);
/*     */     }
/* 354 */     BigInteger localBigInteger1 = new BigInteger(1, arrayOfByte);
/* 355 */     BigInteger localBigInteger2 = paramBigInteger4.modInverse(paramBigInteger2);
/*     */     
/* 357 */     return paramBigInteger1.multiply(paramBigInteger3).add(localBigInteger1).multiply(localBigInteger2).mod(paramBigInteger2);
/*     */   }
/*     */   
/*     */   private BigInteger generateW(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
/*     */   {
/* 362 */     return paramBigInteger4.modInverse(paramBigInteger2);
/*     */   }
/*     */   
/*     */   private BigInteger generateV(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6)
/*     */     throws SignatureException
/*     */   {
/*     */     byte[] arrayOfByte;
/*     */     try
/*     */     {
/* 371 */       arrayOfByte = this.md.digest();
/*     */     }
/*     */     catch (RuntimeException localRuntimeException) {
/* 374 */       throw new SignatureException(localRuntimeException.getMessage());
/*     */     }
/*     */     
/* 377 */     int i = paramBigInteger3.bitLength() / 8;
/* 378 */     if (i < arrayOfByte.length) {
/* 379 */       arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i);
/*     */     }
/* 381 */     BigInteger localBigInteger1 = new BigInteger(1, arrayOfByte);
/*     */     
/* 383 */     BigInteger localBigInteger2 = localBigInteger1.multiply(paramBigInteger5).mod(paramBigInteger3);
/* 384 */     BigInteger localBigInteger3 = paramBigInteger6.multiply(paramBigInteger5).mod(paramBigInteger3);
/*     */     
/* 386 */     BigInteger localBigInteger4 = paramBigInteger4.modPow(localBigInteger2, paramBigInteger2);
/* 387 */     BigInteger localBigInteger5 = paramBigInteger1.modPow(localBigInteger3, paramBigInteger2);
/* 388 */     BigInteger localBigInteger6 = localBigInteger4.multiply(localBigInteger5);
/* 389 */     BigInteger localBigInteger7 = localBigInteger6.mod(paramBigInteger2);
/* 390 */     return localBigInteger7.mod(paramBigInteger3);
/*     */   }
/*     */   
/*     */   protected BigInteger generateK(BigInteger paramBigInteger)
/*     */   {
/* 395 */     SecureRandom localSecureRandom = getSigningRandom();
/* 396 */     byte[] arrayOfByte = new byte[(paramBigInteger.bitLength() + 7) / 8 + 8];
/*     */     
/* 398 */     localSecureRandom.nextBytes(arrayOfByte);
/* 399 */     return new BigInteger(1, arrayOfByte).mod(paramBigInteger
/* 400 */       .subtract(BigInteger.ONE)).add(BigInteger.ONE);
/*     */   }
/*     */   
/*     */ 
/*     */   protected SecureRandom getSigningRandom()
/*     */   {
/* 406 */     if (this.signingRandom == null) {
/* 407 */       if (this.appRandom != null) {
/* 408 */         this.signingRandom = this.appRandom;
/*     */       } else {
/* 410 */         this.signingRandom = JCAUtil.getSecureRandom();
/*     */       }
/*     */     }
/* 413 */     return this.signingRandom;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 420 */     String str = "DSA Signature";
/* 421 */     if ((this.presetP != null) && (this.presetQ != null) && (this.presetG != null)) {
/* 422 */       str = str + "\n\tp: " + Debug.toHexString(this.presetP);
/* 423 */       str = str + "\n\tq: " + Debug.toHexString(this.presetQ);
/* 424 */       str = str + "\n\tg: " + Debug.toHexString(this.presetG);
/*     */     } else {
/* 426 */       str = str + "\n\t P, Q or G not initialized.";
/*     */     }
/* 428 */     if (this.presetY != null) {
/* 429 */       str = str + "\n\ty: " + Debug.toHexString(this.presetY);
/*     */     }
/* 431 */     if ((this.presetY == null) && (this.presetX == null)) {
/* 432 */       str = str + "\n\tUNINIIALIZED";
/*     */     }
/* 434 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void debug(Exception paramException) {}
/*     */   
/*     */ 
/*     */ 
/*     */   private static void debug(String paramString) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public static final class SHA224withDSA
/*     */     extends DSA
/*     */   {
/*     */     public SHA224withDSA()
/*     */       throws NoSuchAlgorithmException
/*     */     {
/* 454 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class SHA256withDSA
/*     */     extends DSA
/*     */   {
/*     */     public SHA256withDSA() throws NoSuchAlgorithmException
/*     */     {
/* 463 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class SHA1withDSA
/*     */     extends DSA
/*     */   {
/*     */     public SHA1withDSA() throws NoSuchAlgorithmException
/*     */     {
/* 472 */       super();
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
/*     */   public static final class RawDSA
/*     */     extends DSA
/*     */   {
/*     */     public static final class NullDigest20
/*     */       extends MessageDigest
/*     */     {
/* 489 */       private final byte[] digestBuffer = new byte[20];
/*     */       
/*     */ 
/*     */ 
/* 493 */       private int ofs = 0;
/*     */       
/*     */ 
/* 496 */       protected NullDigest20() { super(); }
/*     */       
/*     */       protected void engineUpdate(byte paramByte) {
/* 499 */         if (this.ofs == this.digestBuffer.length) {
/* 500 */           this.ofs = Integer.MAX_VALUE;
/*     */         } else
/* 502 */           this.digestBuffer[(this.ofs++)] = paramByte;
/*     */       }
/*     */       
/*     */       protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/* 506 */         if (this.ofs + paramInt2 > this.digestBuffer.length) {
/* 507 */           this.ofs = Integer.MAX_VALUE;
/*     */         } else {
/* 509 */           System.arraycopy(paramArrayOfByte, paramInt1, this.digestBuffer, this.ofs, paramInt2);
/* 510 */           this.ofs += paramInt2;
/*     */         }
/*     */       }
/*     */       
/* 514 */       protected final void engineUpdate(ByteBuffer paramByteBuffer) { int i = paramByteBuffer.remaining();
/* 515 */         if (this.ofs + i > this.digestBuffer.length) {
/* 516 */           this.ofs = Integer.MAX_VALUE;
/*     */         } else {
/* 518 */           paramByteBuffer.get(this.digestBuffer, this.ofs, i);
/* 519 */           this.ofs += i;
/*     */         }
/*     */       }
/*     */       
/* 523 */       protected byte[] engineDigest() throws RuntimeException { if (this.ofs != this.digestBuffer.length) {
/* 524 */           throw new RuntimeException("Data for RawDSA must be exactly 20 bytes long");
/*     */         }
/*     */         
/* 527 */         reset();
/* 528 */         return this.digestBuffer;
/*     */       }
/*     */       
/*     */       protected int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws DigestException {
/* 532 */         if (this.ofs != this.digestBuffer.length) {
/* 533 */           throw new DigestException("Data for RawDSA must be exactly 20 bytes long");
/*     */         }
/*     */         
/* 536 */         if (paramInt2 < this.digestBuffer.length) {
/* 537 */           throw new DigestException("Output buffer too small; must be at least 20 bytes");
/*     */         }
/*     */         
/* 540 */         System.arraycopy(this.digestBuffer, 0, paramArrayOfByte, paramInt1, this.digestBuffer.length);
/* 541 */         reset();
/* 542 */         return this.digestBuffer.length;
/*     */       }
/*     */       
/*     */       protected void engineReset() {
/* 546 */         this.ofs = 0;
/*     */       }
/*     */       
/* 549 */       protected final int engineGetDigestLength() { return this.digestBuffer.length; }
/*     */     }
/*     */     
/*     */     public RawDSA() throws NoSuchAlgorithmException
/*     */     {
/* 554 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\DSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
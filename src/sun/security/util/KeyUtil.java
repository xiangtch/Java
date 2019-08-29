/*     */ package sun.security.util;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.AlgorithmParameters;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.interfaces.DSAKey;
/*     */ import java.security.interfaces.DSAParams;
/*     */ import java.security.interfaces.ECKey;
/*     */ import java.security.interfaces.RSAKey;
/*     */ import java.security.spec.ECParameterSpec;
/*     */ import java.security.spec.InvalidParameterSpecException;
/*     */ import java.security.spec.KeySpec;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.interfaces.DHKey;
/*     */ import javax.crypto.interfaces.DHPublicKey;
/*     */ import javax.crypto.spec.DHParameterSpec;
/*     */ import javax.crypto.spec.DHPublicKeySpec;
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
/*     */ public final class KeyUtil
/*     */ {
/*     */   public static final int getKeySize(Key paramKey)
/*     */   {
/*  63 */     int i = -1;
/*     */     
/*  65 */     if ((paramKey instanceof Length)) {
/*     */       try {
/*  67 */         Length localLength = (Length)paramKey;
/*  68 */         i = localLength.length();
/*     */       }
/*     */       catch (UnsupportedOperationException localUnsupportedOperationException) {}
/*     */       
/*     */ 
/*  73 */       if (i >= 0) {
/*  74 */         return i;
/*     */       }
/*     */     }
/*     */     Object localObject1;
/*     */     Object localObject2;
/*  79 */     if ((paramKey instanceof SecretKey)) {
/*  80 */       localObject1 = (SecretKey)paramKey;
/*  81 */       localObject2 = ((SecretKey)localObject1).getFormat();
/*  82 */       if (("RAW".equals(localObject2)) && (((SecretKey)localObject1).getEncoded() != null)) {
/*  83 */         i = ((SecretKey)localObject1).getEncoded().length * 8;
/*     */       }
/*     */     }
/*  86 */     else if ((paramKey instanceof RSAKey)) {
/*  87 */       localObject1 = (RSAKey)paramKey;
/*  88 */       i = ((RSAKey)localObject1).getModulus().bitLength();
/*  89 */     } else if ((paramKey instanceof ECKey)) {
/*  90 */       localObject1 = (ECKey)paramKey;
/*  91 */       i = ((ECKey)localObject1).getParams().getOrder().bitLength();
/*  92 */     } else if ((paramKey instanceof DSAKey)) {
/*  93 */       localObject1 = (DSAKey)paramKey;
/*  94 */       localObject2 = ((DSAKey)localObject1).getParams();
/*  95 */       i = localObject2 != null ? ((DSAParams)localObject2).getP().bitLength() : -1;
/*  96 */     } else if ((paramKey instanceof DHKey)) {
/*  97 */       localObject1 = (DHKey)paramKey;
/*  98 */       i = ((DHKey)localObject1).getParams().getP().bitLength();
/*     */     }
/*     */     
/*     */ 
/* 102 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final int getKeySize(AlgorithmParameters paramAlgorithmParameters)
/*     */   {
/* 114 */     String str1 = paramAlgorithmParameters.getAlgorithm();
/* 115 */     switch (str1) {
/*     */     case "EC": 
/*     */       try {
/* 118 */         ECKeySizeParameterSpec localECKeySizeParameterSpec = (ECKeySizeParameterSpec)paramAlgorithmParameters.getParameterSpec(ECKeySizeParameterSpec.class);
/*     */         
/* 120 */         if (localECKeySizeParameterSpec != null) {
/* 121 */           return localECKeySizeParameterSpec.getKeySize();
/*     */         }
/*     */       }
/*     */       catch (InvalidParameterSpecException localInvalidParameterSpecException1) {}
/*     */       
/*     */       try
/*     */       {
/* 128 */         ECParameterSpec localECParameterSpec = (ECParameterSpec)paramAlgorithmParameters.getParameterSpec(ECParameterSpec.class);
/*     */         
/* 130 */         if (localECParameterSpec != null) {
/* 131 */           return localECParameterSpec.getOrder().bitLength();
/*     */         }
/*     */       }
/*     */       catch (InvalidParameterSpecException localInvalidParameterSpecException2) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     case "DiffieHellman": 
/*     */       try
/*     */       {
/* 144 */         DHParameterSpec localDHParameterSpec = (DHParameterSpec)paramAlgorithmParameters.getParameterSpec(DHParameterSpec.class);
/*     */         
/* 146 */         if (localDHParameterSpec != null) {
/* 147 */           return localDHParameterSpec.getP().bitLength();
/*     */         }
/*     */       }
/*     */       catch (InvalidParameterSpecException localInvalidParameterSpecException3) {}
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 157 */     return -1;
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
/*     */   public static final void validate(Key paramKey)
/*     */     throws InvalidKeyException
/*     */   {
/* 173 */     if (paramKey == null) {
/* 174 */       throw new NullPointerException("The key to be validated cannot be null");
/*     */     }
/*     */     
/*     */ 
/* 178 */     if ((paramKey instanceof DHPublicKey)) {
/* 179 */       validateDHPublicKey((DHPublicKey)paramKey);
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
/*     */   public static final void validate(KeySpec paramKeySpec)
/*     */     throws InvalidKeyException
/*     */   {
/* 197 */     if (paramKeySpec == null) {
/* 198 */       throw new NullPointerException("The key spec to be validated cannot be null");
/*     */     }
/*     */     
/*     */ 
/* 202 */     if ((paramKeySpec instanceof DHPublicKeySpec)) {
/* 203 */       validateDHPublicKey((DHPublicKeySpec)paramKeySpec);
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
/*     */   public static final boolean isOracleJCEProvider(String paramString)
/*     */   {
/* 216 */     return (paramString != null) && (
/* 217 */       (paramString.equals("SunJCE")) || 
/* 218 */       (paramString.equals("SunMSCAPI")) || 
/* 219 */       (paramString.equals("OracleUcrypto")) || 
/* 220 */       (paramString.startsWith("SunPKCS11")));
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
/*     */ 
/*     */   public static byte[] checkTlsPreMasterSecretKey(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, byte[] paramArrayOfByte, boolean paramBoolean)
/*     */   {
/* 264 */     if (paramSecureRandom == null) {
/* 265 */       paramSecureRandom = JCAUtil.getSecureRandom();
/*     */     }
/* 267 */     byte[] arrayOfByte = new byte[48];
/* 268 */     paramSecureRandom.nextBytes(arrayOfByte);
/*     */     
/* 270 */     if ((!paramBoolean) && (paramArrayOfByte != null))
/*     */     {
/* 272 */       if (paramArrayOfByte.length != 48)
/*     */       {
/* 274 */         return arrayOfByte;
/*     */       }
/*     */       
/* 277 */       int i = (paramArrayOfByte[0] & 0xFF) << 8 | paramArrayOfByte[1] & 0xFF;
/*     */       
/* 279 */       if ((paramInt1 != i) && (
/* 280 */         (paramInt1 > 769) || (paramInt2 != i)))
/*     */       {
/* 282 */         paramArrayOfByte = arrayOfByte;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 289 */       return paramArrayOfByte;
/*     */     }
/*     */     
/*     */ 
/* 293 */     return arrayOfByte;
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
/*     */   private static void validateDHPublicKey(DHPublicKey paramDHPublicKey)
/*     */     throws InvalidKeyException
/*     */   {
/* 308 */     DHParameterSpec localDHParameterSpec = paramDHPublicKey.getParams();
/*     */     
/* 310 */     BigInteger localBigInteger1 = localDHParameterSpec.getP();
/* 311 */     BigInteger localBigInteger2 = localDHParameterSpec.getG();
/* 312 */     BigInteger localBigInteger3 = paramDHPublicKey.getY();
/*     */     
/* 314 */     validateDHPublicKey(localBigInteger1, localBigInteger2, localBigInteger3);
/*     */   }
/*     */   
/*     */   private static void validateDHPublicKey(DHPublicKeySpec paramDHPublicKeySpec) throws InvalidKeyException
/*     */   {
/* 319 */     validateDHPublicKey(paramDHPublicKeySpec.getP(), paramDHPublicKeySpec
/* 320 */       .getG(), paramDHPublicKeySpec.getY());
/*     */   }
/*     */   
/*     */ 
/*     */   private static void validateDHPublicKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
/*     */     throws InvalidKeyException
/*     */   {
/* 327 */     BigInteger localBigInteger1 = BigInteger.ONE;
/* 328 */     BigInteger localBigInteger2 = paramBigInteger1.subtract(BigInteger.ONE);
/* 329 */     if (paramBigInteger3.compareTo(localBigInteger1) <= 0) {
/* 330 */       throw new InvalidKeyException("Diffie-Hellman public key is too small");
/*     */     }
/*     */     
/* 333 */     if (paramBigInteger3.compareTo(localBigInteger2) >= 0) {
/* 334 */       throw new InvalidKeyException("Diffie-Hellman public key is too large");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 344 */     BigInteger localBigInteger3 = paramBigInteger1.remainder(paramBigInteger3);
/* 345 */     if (localBigInteger3.equals(BigInteger.ZERO)) {
/* 346 */       throw new InvalidKeyException("Invalid Diffie-Hellman parameters");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] trimZeroes(byte[] paramArrayOfByte)
/*     */   {
/* 356 */     int i = 0;
/* 357 */     while ((i < paramArrayOfByte.length - 1) && (paramArrayOfByte[i] == 0)) {
/* 358 */       i++;
/*     */     }
/* 360 */     if (i == 0) {
/* 361 */       return paramArrayOfByte;
/*     */     }
/* 363 */     byte[] arrayOfByte = new byte[paramArrayOfByte.length - i];
/* 364 */     System.arraycopy(paramArrayOfByte, i, arrayOfByte, 0, arrayOfByte.length);
/* 365 */     return arrayOfByte;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\KeyUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
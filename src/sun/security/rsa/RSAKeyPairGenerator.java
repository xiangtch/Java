/*     */ package sun.security.rsa;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.InvalidParameterException;
/*     */ import java.security.KeyPair;
/*     */ import java.security.KeyPairGeneratorSpi;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.security.spec.RSAKeyGenParameterSpec;
/*     */ import sun.security.jca.JCAUtil;
/*     */ import sun.security.util.SecurityProviderConstants;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RSAKeyPairGenerator
/*     */   extends KeyPairGeneratorSpi
/*     */ {
/*     */   private BigInteger publicExponent;
/*     */   private int keySize;
/*     */   private SecureRandom random;
/*     */   
/*     */   public RSAKeyPairGenerator()
/*     */   {
/*  59 */     initialize(SecurityProviderConstants.DEF_RSA_KEY_SIZE, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(int paramInt, SecureRandom paramSecureRandom)
/*     */   {
/*     */     try
/*     */     {
/*  68 */       RSAKeyFactory.checkKeyLengths(paramInt, RSAKeyGenParameterSpec.F4, 512, 65536);
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/*  71 */       throw new InvalidParameterException(localInvalidKeyException.getMessage());
/*     */     }
/*     */     
/*  74 */     this.keySize = paramInt;
/*  75 */     this.random = paramSecureRandom;
/*  76 */     this.publicExponent = RSAKeyGenParameterSpec.F4;
/*     */   }
/*     */   
/*     */ 
/*     */   public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
/*     */     throws InvalidAlgorithmParameterException
/*     */   {
/*  83 */     if (!(paramAlgorithmParameterSpec instanceof RSAKeyGenParameterSpec)) {
/*  84 */       throw new InvalidAlgorithmParameterException("Params must be instance of RSAKeyGenParameterSpec");
/*     */     }
/*     */     
/*     */ 
/*  88 */     RSAKeyGenParameterSpec localRSAKeyGenParameterSpec = (RSAKeyGenParameterSpec)paramAlgorithmParameterSpec;
/*  89 */     int i = localRSAKeyGenParameterSpec.getKeysize();
/*  90 */     BigInteger localBigInteger = localRSAKeyGenParameterSpec.getPublicExponent();
/*     */     
/*  92 */     if (localBigInteger == null) {
/*  93 */       localBigInteger = RSAKeyGenParameterSpec.F4;
/*     */     } else {
/*  95 */       if (localBigInteger.compareTo(RSAKeyGenParameterSpec.F0) < 0) {
/*  96 */         throw new InvalidAlgorithmParameterException("Public exponent must be 3 or larger");
/*     */       }
/*     */       
/*  99 */       if (localBigInteger.bitLength() > i) {
/* 100 */         throw new InvalidAlgorithmParameterException("Public exponent must be smaller than key size");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 107 */       RSAKeyFactory.checkKeyLengths(i, localBigInteger, 512, 65536);
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/* 110 */       throw new InvalidAlgorithmParameterException("Invalid key sizes", localInvalidKeyException);
/*     */     }
/*     */     
/*     */ 
/* 114 */     this.keySize = i;
/* 115 */     this.publicExponent = localBigInteger;
/* 116 */     this.random = paramSecureRandom;
/*     */   }
/*     */   
/*     */ 
/*     */   public KeyPair generateKeyPair()
/*     */   {
/* 122 */     int i = this.keySize + 1 >> 1;
/* 123 */     int j = this.keySize - i;
/* 124 */     if (this.random == null) {
/* 125 */       this.random = JCAUtil.getSecureRandom();
/*     */     }
/* 127 */     BigInteger localBigInteger1 = this.publicExponent;
/*     */     Object localObject1;
/*     */     Object localObject2;
/* 130 */     Object localObject3; BigInteger localBigInteger2; BigInteger localBigInteger3; BigInteger localBigInteger4; do { localObject1 = BigInteger.probablePrime(i, this.random);
/*     */       do
/*     */       {
/* 133 */         localObject2 = BigInteger.probablePrime(j, this.random);
/*     */         
/* 135 */         if (((BigInteger)localObject1).compareTo((BigInteger)localObject2) < 0) {
/* 136 */           localObject3 = localObject1;
/* 137 */           localObject1 = localObject2;
/* 138 */           localObject2 = localObject3;
/*     */         }
/*     */         
/* 141 */         localBigInteger2 = ((BigInteger)localObject1).multiply((BigInteger)localObject2);
/*     */ 
/*     */       }
/* 144 */       while (localBigInteger2.bitLength() < this.keySize);
/*     */       
/*     */ 
/*     */ 
/* 148 */       localObject3 = ((BigInteger)localObject1).subtract(BigInteger.ONE);
/* 149 */       localBigInteger3 = ((BigInteger)localObject2).subtract(BigInteger.ONE);
/* 150 */       localBigInteger4 = ((BigInteger)localObject3).multiply(localBigInteger3);
/*     */ 
/*     */     }
/* 153 */     while (!localBigInteger1.gcd(localBigInteger4).equals(BigInteger.ONE));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 158 */     BigInteger localBigInteger5 = localBigInteger1.modInverse(localBigInteger4);
/*     */     
/*     */ 
/* 161 */     BigInteger localBigInteger6 = localBigInteger5.mod((BigInteger)localObject3);
/*     */     
/* 163 */     BigInteger localBigInteger7 = localBigInteger5.mod(localBigInteger3);
/*     */     
/*     */ 
/* 166 */     BigInteger localBigInteger8 = ((BigInteger)localObject2).modInverse((BigInteger)localObject1);
/*     */     try
/*     */     {
/* 169 */       RSAPublicKeyImpl localRSAPublicKeyImpl = new RSAPublicKeyImpl(localBigInteger2, localBigInteger1);
/* 170 */       RSAPrivateCrtKeyImpl localRSAPrivateCrtKeyImpl = new RSAPrivateCrtKeyImpl(localBigInteger2, localBigInteger1, localBigInteger5, (BigInteger)localObject1, (BigInteger)localObject2, localBigInteger6, localBigInteger7, localBigInteger8);
/*     */       
/* 172 */       return new KeyPair(localRSAPublicKeyImpl, localRSAPrivateCrtKeyImpl);
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException)
/*     */     {
/* 176 */       throw new RuntimeException(localInvalidKeyException);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\rsa\RSAKeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
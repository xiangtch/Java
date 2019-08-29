/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.InvalidParameterException;
/*     */ import java.security.KeyPair;
/*     */ import java.security.KeyPairGenerator;
/*     */ import java.security.ProviderException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.interfaces.DSAParams;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.security.spec.DSAParameterSpec;
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
/*     */ 
/*     */ class DSAKeyPairGenerator
/*     */   extends KeyPairGenerator
/*     */ {
/*     */   private int plen;
/*     */   private int qlen;
/*     */   boolean forceNewParameters;
/*     */   private DSAParameterSpec params;
/*     */   private SecureRandom random;
/*     */   
/*     */   DSAKeyPairGenerator(int paramInt)
/*     */   {
/*  66 */     super("DSA");
/*  67 */     initialize(paramInt, null);
/*     */   }
/*     */   
/*     */   private static void checkStrength(int paramInt1, int paramInt2) {
/*  71 */     if ((paramInt1 < 512) || (paramInt1 > 1024) || (paramInt1 % 64 != 0) || (paramInt2 != 160))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  76 */       if ((paramInt1 != 2048) || ((paramInt2 != 224) && (paramInt2 != 256)))
/*     */       {
/*  78 */         if ((paramInt1 != 3072) || (paramInt2 != 256))
/*     */         {
/*     */ 
/*  81 */           throw new InvalidParameterException("Unsupported prime and subprime size combination: " + paramInt1 + ", " + paramInt2);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void initialize(int paramInt, SecureRandom paramSecureRandom) {
/*  88 */     init(paramInt, paramSecureRandom, false);
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
/*     */   public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
/*     */     throws InvalidAlgorithmParameterException
/*     */   {
/* 103 */     if (!(paramAlgorithmParameterSpec instanceof DSAParameterSpec)) {
/* 104 */       throw new InvalidAlgorithmParameterException("Inappropriate parameter");
/*     */     }
/*     */     
/* 107 */     init((DSAParameterSpec)paramAlgorithmParameterSpec, paramSecureRandom, false);
/*     */   }
/*     */   
/*     */   void init(int paramInt, SecureRandom paramSecureRandom, boolean paramBoolean) {
/* 111 */     int i = SecurityProviderConstants.getDefDSASubprimeSize(paramInt);
/* 112 */     checkStrength(paramInt, i);
/* 113 */     this.plen = paramInt;
/* 114 */     this.qlen = i;
/* 115 */     this.params = null;
/* 116 */     this.random = paramSecureRandom;
/* 117 */     this.forceNewParameters = paramBoolean;
/*     */   }
/*     */   
/*     */   void init(DSAParameterSpec paramDSAParameterSpec, SecureRandom paramSecureRandom, boolean paramBoolean)
/*     */   {
/* 122 */     int i = paramDSAParameterSpec.getP().bitLength();
/* 123 */     int j = paramDSAParameterSpec.getQ().bitLength();
/* 124 */     checkStrength(i, j);
/* 125 */     this.plen = i;
/* 126 */     this.qlen = j;
/* 127 */     this.params = paramDSAParameterSpec;
/* 128 */     this.random = paramSecureRandom;
/* 129 */     this.forceNewParameters = paramBoolean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public KeyPair generateKeyPair()
/*     */   {
/* 137 */     if (this.random == null) {
/* 138 */       this.random = JCAUtil.getSecureRandom();
/*     */     }
/*     */     DSAParameterSpec localDSAParameterSpec;
/*     */     try {
/* 142 */       if (this.forceNewParameters)
/*     */       {
/* 144 */         localDSAParameterSpec = ParameterCache.getNewDSAParameterSpec(this.plen, this.qlen, this.random);
/*     */       } else {
/* 146 */         if (this.params == null)
/*     */         {
/* 148 */           this.params = ParameterCache.getDSAParameterSpec(this.plen, this.qlen, this.random);
/*     */         }
/* 150 */         localDSAParameterSpec = this.params;
/*     */       }
/*     */     } catch (GeneralSecurityException localGeneralSecurityException) {
/* 153 */       throw new ProviderException(localGeneralSecurityException);
/*     */     }
/* 155 */     return generateKeyPair(localDSAParameterSpec.getP(), localDSAParameterSpec.getQ(), localDSAParameterSpec.getG(), this.random);
/*     */   }
/*     */   
/*     */ 
/*     */   private KeyPair generateKeyPair(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, SecureRandom paramSecureRandom)
/*     */   {
/* 161 */     BigInteger localBigInteger1 = generateX(paramSecureRandom, paramBigInteger2);
/* 162 */     BigInteger localBigInteger2 = generateY(localBigInteger1, paramBigInteger1, paramBigInteger3);
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*     */       Object localObject;
/*     */       
/* 169 */       if (DSAKeyFactory.SERIAL_INTEROP) {
/* 170 */         localObject = new DSAPublicKey(localBigInteger2, paramBigInteger1, paramBigInteger2, paramBigInteger3);
/*     */       } else {
/* 172 */         localObject = new DSAPublicKeyImpl(localBigInteger2, paramBigInteger1, paramBigInteger2, paramBigInteger3);
/*     */       }
/* 174 */       DSAPrivateKey localDSAPrivateKey = new DSAPrivateKey(localBigInteger1, paramBigInteger1, paramBigInteger2, paramBigInteger3);
/*     */       
/* 176 */       return new KeyPair((PublicKey)localObject, localDSAPrivateKey);
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/* 179 */       throw new ProviderException(localInvalidKeyException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private BigInteger generateX(SecureRandom paramSecureRandom, BigInteger paramBigInteger)
/*     */   {
/* 190 */     BigInteger localBigInteger = null;
/* 191 */     byte[] arrayOfByte = new byte[this.qlen];
/*     */     do {
/* 193 */       paramSecureRandom.nextBytes(arrayOfByte);
/* 194 */       localBigInteger = new BigInteger(1, arrayOfByte).mod(paramBigInteger);
/* 195 */     } while ((localBigInteger.signum() <= 0) || (localBigInteger.compareTo(paramBigInteger) >= 0));
/* 196 */     return localBigInteger;
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
/*     */   BigInteger generateY(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
/*     */   {
/* 209 */     BigInteger localBigInteger = paramBigInteger3.modPow(paramBigInteger1, paramBigInteger2);
/* 210 */     return localBigInteger;
/*     */   }
/*     */   
/*     */   public static final class Current extends DSAKeyPairGenerator {
/*     */     public Current() {
/* 215 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class Legacy extends DSAKeyPairGenerator implements java.security.interfaces.DSAKeyPairGenerator
/*     */   {
/*     */     public Legacy()
/*     */     {
/* 223 */       super();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void initialize(int paramInt, boolean paramBoolean, SecureRandom paramSecureRandom)
/*     */       throws InvalidParameterException
/*     */     {
/* 233 */       if (paramBoolean) {
/* 234 */         super.init(paramInt, paramSecureRandom, true);
/*     */       }
/*     */       else {
/* 237 */         DSAParameterSpec localDSAParameterSpec = ParameterCache.getCachedDSAParameterSpec(paramInt, 
/* 238 */           SecurityProviderConstants.getDefDSASubprimeSize(paramInt));
/* 239 */         if (localDSAParameterSpec == null) {
/* 240 */           throw new InvalidParameterException("No precomputed parameters for requested modulus size available");
/*     */         }
/*     */         
/*     */ 
/* 244 */         super.init(localDSAParameterSpec, paramSecureRandom, false);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void initialize(DSAParams paramDSAParams, SecureRandom paramSecureRandom)
/*     */       throws InvalidParameterException
/*     */     {
/* 256 */       if (paramDSAParams == null) {
/* 257 */         throw new InvalidParameterException("Params must not be null");
/*     */       }
/*     */       
/* 260 */       DSAParameterSpec localDSAParameterSpec = new DSAParameterSpec(paramDSAParams.getP(), paramDSAParams.getQ(), paramDSAParams.getG());
/* 261 */       super.init(localDSAParameterSpec, paramSecureRandom, false);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\DSAKeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
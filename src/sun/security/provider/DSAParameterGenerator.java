/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.AlgorithmParameterGeneratorSpi;
/*     */ import java.security.AlgorithmParameters;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidParameterException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.ProviderException;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.security.spec.DSAGenParameterSpec;
/*     */ import java.security.spec.DSAParameterSpec;
/*     */ import java.security.spec.InvalidParameterSpecException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DSAParameterGenerator
/*     */   extends AlgorithmParameterGeneratorSpi
/*     */ {
/*  63 */   private int valueL = -1;
/*  64 */   private int valueN = -1;
/*  65 */   private int seedLen = -1;
/*     */   
/*     */ 
/*     */   private SecureRandom random;
/*     */   
/*     */ 
/*  71 */   private static final BigInteger TWO = BigInteger.valueOf(2L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void engineInit(int paramInt, SecureRandom paramSecureRandom)
/*     */   {
/*  85 */     if ((paramInt != 2048) && (paramInt != 3072) && ((paramInt < 512) || (paramInt > 1024) || (paramInt % 64 != 0)))
/*     */     {
/*  87 */       throw new InvalidParameterException("Unexpected strength (size of prime): " + paramInt + ". Prime size should be 512-1024, 2048, or 3072");
/*     */     }
/*     */     
/*     */ 
/*  91 */     this.valueL = paramInt;
/*  92 */     this.valueN = SecurityProviderConstants.getDefDSASubprimeSize(paramInt);
/*  93 */     this.seedLen = this.valueN;
/*  94 */     this.random = paramSecureRandom;
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
/*     */   protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
/*     */     throws InvalidAlgorithmParameterException
/*     */   {
/* 111 */     if (!(paramAlgorithmParameterSpec instanceof DSAGenParameterSpec)) {
/* 112 */       throw new InvalidAlgorithmParameterException("Invalid parameter");
/*     */     }
/* 114 */     DSAGenParameterSpec localDSAGenParameterSpec = (DSAGenParameterSpec)paramAlgorithmParameterSpec;
/*     */     
/*     */ 
/* 117 */     this.valueL = localDSAGenParameterSpec.getPrimePLength();
/* 118 */     this.valueN = localDSAGenParameterSpec.getSubprimeQLength();
/* 119 */     this.seedLen = localDSAGenParameterSpec.getSeedLength();
/* 120 */     this.random = paramSecureRandom;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected AlgorithmParameters engineGenerateParameters()
/*     */   {
/* 130 */     AlgorithmParameters localAlgorithmParameters = null;
/*     */     try {
/* 132 */       if (this.random == null) {
/* 133 */         this.random = new SecureRandom();
/*     */       }
/* 135 */       if (this.valueL == -1) {
/* 136 */         engineInit(SecurityProviderConstants.DEF_DSA_KEY_SIZE, this.random);
/*     */       }
/* 138 */       BigInteger[] arrayOfBigInteger = generatePandQ(this.random, this.valueL, this.valueN, this.seedLen);
/*     */       
/* 140 */       BigInteger localBigInteger1 = arrayOfBigInteger[0];
/* 141 */       BigInteger localBigInteger2 = arrayOfBigInteger[1];
/* 142 */       BigInteger localBigInteger3 = generateG(localBigInteger1, localBigInteger2);
/*     */       
/* 144 */       DSAParameterSpec localDSAParameterSpec = new DSAParameterSpec(localBigInteger1, localBigInteger2, localBigInteger3);
/*     */       
/* 146 */       localAlgorithmParameters = AlgorithmParameters.getInstance("DSA", "SUN");
/* 147 */       localAlgorithmParameters.init(localDSAParameterSpec);
/*     */     }
/*     */     catch (InvalidParameterSpecException localInvalidParameterSpecException) {
/* 150 */       throw new RuntimeException(localInvalidParameterSpecException.getMessage());
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 153 */       throw new RuntimeException(localNoSuchAlgorithmException.getMessage());
/*     */     }
/*     */     catch (NoSuchProviderException localNoSuchProviderException) {
/* 156 */       throw new RuntimeException(localNoSuchProviderException.getMessage());
/*     */     }
/*     */     
/* 159 */     return localAlgorithmParameters;
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
/*     */   private static BigInteger[] generatePandQ(SecureRandom paramSecureRandom, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 180 */     String str = null;
/* 181 */     if (paramInt2 == 160) {
/* 182 */       str = "SHA";
/* 183 */     } else if (paramInt2 == 224) {
/* 184 */       str = "SHA-224";
/* 185 */     } else if (paramInt2 == 256) {
/* 186 */       str = "SHA-256";
/*     */     }
/* 188 */     MessageDigest localMessageDigest = null;
/*     */     try {
/* 190 */       localMessageDigest = MessageDigest.getInstance(str);
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 193 */       localNoSuchAlgorithmException.printStackTrace();
/*     */     }
/*     */     
/*     */ 
/* 197 */     int i = localMessageDigest.getDigestLength() * 8;
/* 198 */     int j = (paramInt1 - 1) / i;
/* 199 */     int k = (paramInt1 - 1) % i;
/* 200 */     byte[] arrayOfByte = new byte[paramInt3 / 8];
/* 201 */     BigInteger localBigInteger1 = TWO.pow(paramInt3);
/* 202 */     int m = -1;
/* 203 */     if (paramInt1 <= 1024) {
/* 204 */       m = 80;
/* 205 */     } else if (paramInt1 == 2048) {
/* 206 */       m = 112;
/* 207 */     } else if (paramInt1 == 3072) {
/* 208 */       m = 128;
/*     */     }
/* 210 */     if (m < 0) {
/* 211 */       throw new ProviderException("Invalid valueL: " + paramInt1);
/*     */     }
/* 213 */     BigInteger localBigInteger4 = null;
/*     */     
/*     */ 
/*     */     for (;;)
/*     */     {
/* 218 */       paramSecureRandom.nextBytes(arrayOfByte);
/* 219 */       localBigInteger4 = new BigInteger(1, arrayOfByte);
/*     */       
/*     */ 
/*     */ 
/* 223 */       BigInteger localBigInteger5 = new BigInteger(1, localMessageDigest.digest(arrayOfByte)).mod(TWO.pow(paramInt2 - 1));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 229 */       BigInteger localBigInteger3 = TWO.pow(paramInt2 - 1).add(localBigInteger5).add(BigInteger.ONE).subtract(localBigInteger5.mod(TWO));
/* 230 */       if (localBigInteger3.isProbablePrime(m))
/*     */       {
/*     */ 
/* 233 */         localBigInteger5 = BigInteger.ONE;
/*     */         
/* 235 */         for (int n = 0; n < 4 * paramInt1; n++) {
/* 236 */           BigInteger[] arrayOfBigInteger1 = new BigInteger[j + 1];
/*     */           
/* 238 */           for (int i1 = 0; i1 <= j; i1++) {
/* 239 */             BigInteger localBigInteger7 = BigInteger.valueOf(i1);
/* 240 */             localBigInteger9 = localBigInteger4.add(localBigInteger5).add(localBigInteger7).mod(localBigInteger1);
/* 241 */             localObject = localMessageDigest.digest(toByteArray(localBigInteger9));
/* 242 */             arrayOfBigInteger1[i1] = new BigInteger(1, (byte[])localObject);
/*     */           }
/*     */           
/* 245 */           BigInteger localBigInteger6 = arrayOfBigInteger1[0];
/* 246 */           for (int i2 = 1; i2 < j; i2++) {
/* 247 */             localBigInteger6 = localBigInteger6.add(arrayOfBigInteger1[i2].multiply(TWO.pow(i2 * i)));
/*     */           }
/* 249 */           localBigInteger6 = localBigInteger6.add(arrayOfBigInteger1[j].mod(TWO.pow(k))
/* 250 */             .multiply(TWO.pow(j * i)));
/*     */           
/* 252 */           BigInteger localBigInteger8 = TWO.pow(paramInt1 - 1);
/* 253 */           BigInteger localBigInteger9 = localBigInteger6.add(localBigInteger8);
/*     */           
/* 255 */           Object localObject = localBigInteger9.mod(localBigInteger3.multiply(TWO));
/* 256 */           BigInteger localBigInteger2 = localBigInteger9.subtract(((BigInteger)localObject).subtract(BigInteger.ONE));
/*     */           
/* 258 */           if ((localBigInteger2.compareTo(localBigInteger8) > -1) && 
/* 259 */             (localBigInteger2.isProbablePrime(m)))
/*     */           {
/*     */ 
/* 262 */             BigInteger[] arrayOfBigInteger2 = { localBigInteger2, localBigInteger3, localBigInteger4, BigInteger.valueOf(n) };
/* 263 */             return arrayOfBigInteger2;
/*     */           }
/*     */           
/* 266 */           localBigInteger5 = localBigInteger5.add(BigInteger.valueOf(j)).add(BigInteger.ONE);
/*     */         }
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
/*     */   private static BigInteger generateG(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
/*     */   {
/* 281 */     BigInteger localBigInteger1 = BigInteger.ONE;
/*     */     
/* 283 */     BigInteger localBigInteger2 = paramBigInteger1.subtract(BigInteger.ONE).divide(paramBigInteger2);
/* 284 */     BigInteger localBigInteger3 = BigInteger.ONE;
/* 285 */     while (localBigInteger3.compareTo(TWO) < 0)
/*     */     {
/* 287 */       localBigInteger3 = localBigInteger1.modPow(localBigInteger2, paramBigInteger1);
/* 288 */       localBigInteger1 = localBigInteger1.add(BigInteger.ONE);
/*     */     }
/* 290 */     return localBigInteger3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static byte[] toByteArray(BigInteger paramBigInteger)
/*     */   {
/* 298 */     Object localObject = paramBigInteger.toByteArray();
/* 299 */     if (localObject[0] == 0) {
/* 300 */       byte[] arrayOfByte = new byte[localObject.length - 1];
/* 301 */       System.arraycopy(localObject, 1, arrayOfByte, 0, arrayOfByte.length);
/* 302 */       localObject = arrayOfByte;
/*     */     }
/* 304 */     return (byte[])localObject;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\DSAParameterGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
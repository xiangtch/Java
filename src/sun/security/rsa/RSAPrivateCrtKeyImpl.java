/*     */ package sun.security.rsa;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.interfaces.RSAPrivateCrtKey;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import sun.security.pkcs.PKCS8Key;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RSAPrivateCrtKeyImpl
/*     */   extends PKCS8Key
/*     */   implements RSAPrivateCrtKey
/*     */ {
/*     */   private static final long serialVersionUID = -1326088454257084918L;
/*     */   private BigInteger n;
/*     */   private BigInteger e;
/*     */   private BigInteger d;
/*     */   private BigInteger p;
/*     */   private BigInteger q;
/*     */   private BigInteger pe;
/*     */   private BigInteger qe;
/*     */   private BigInteger coeff;
/*  66 */   static final AlgorithmId rsaId = new AlgorithmId(AlgorithmId.RSAEncryption_oid);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static RSAPrivateKey newKey(byte[] paramArrayOfByte)
/*     */     throws InvalidKeyException
/*     */   {
/*  75 */     RSAPrivateCrtKeyImpl localRSAPrivateCrtKeyImpl = new RSAPrivateCrtKeyImpl(paramArrayOfByte);
/*  76 */     if (localRSAPrivateCrtKeyImpl.getPublicExponent().signum() == 0)
/*     */     {
/*  78 */       return new RSAPrivateKeyImpl(localRSAPrivateCrtKeyImpl
/*  79 */         .getModulus(), localRSAPrivateCrtKeyImpl
/*  80 */         .getPrivateExponent());
/*     */     }
/*     */     
/*  83 */     return localRSAPrivateCrtKeyImpl;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   RSAPrivateCrtKeyImpl(byte[] paramArrayOfByte)
/*     */     throws InvalidKeyException
/*     */   {
/*  91 */     decode(paramArrayOfByte);
/*  92 */     RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   RSAPrivateCrtKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6, BigInteger paramBigInteger7, BigInteger paramBigInteger8)
/*     */     throws InvalidKeyException
/*     */   {
/* 102 */     this.n = paramBigInteger1;
/* 103 */     this.e = paramBigInteger2;
/* 104 */     this.d = paramBigInteger3;
/* 105 */     this.p = paramBigInteger4;
/* 106 */     this.q = paramBigInteger5;
/* 107 */     this.pe = paramBigInteger6;
/* 108 */     this.qe = paramBigInteger7;
/* 109 */     this.coeff = paramBigInteger8;
/* 110 */     RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), paramBigInteger2);
/*     */     
/*     */ 
/* 113 */     this.algid = rsaId;
/*     */     try {
/* 115 */       DerOutputStream localDerOutputStream = new DerOutputStream();
/* 116 */       localDerOutputStream.putInteger(0);
/* 117 */       localDerOutputStream.putInteger(paramBigInteger1);
/* 118 */       localDerOutputStream.putInteger(paramBigInteger2);
/* 119 */       localDerOutputStream.putInteger(paramBigInteger3);
/* 120 */       localDerOutputStream.putInteger(paramBigInteger4);
/* 121 */       localDerOutputStream.putInteger(paramBigInteger5);
/* 122 */       localDerOutputStream.putInteger(paramBigInteger6);
/* 123 */       localDerOutputStream.putInteger(paramBigInteger7);
/* 124 */       localDerOutputStream.putInteger(paramBigInteger8);
/*     */       
/* 126 */       DerValue localDerValue = new DerValue((byte)48, localDerOutputStream.toByteArray());
/* 127 */       this.key = localDerValue.toByteArray();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 130 */       throw new InvalidKeyException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getAlgorithm()
/*     */   {
/* 136 */     return "RSA";
/*     */   }
/*     */   
/*     */   public BigInteger getModulus()
/*     */   {
/* 141 */     return this.n;
/*     */   }
/*     */   
/*     */   public BigInteger getPublicExponent()
/*     */   {
/* 146 */     return this.e;
/*     */   }
/*     */   
/*     */   public BigInteger getPrivateExponent()
/*     */   {
/* 151 */     return this.d;
/*     */   }
/*     */   
/*     */   public BigInteger getPrimeP()
/*     */   {
/* 156 */     return this.p;
/*     */   }
/*     */   
/*     */   public BigInteger getPrimeQ()
/*     */   {
/* 161 */     return this.q;
/*     */   }
/*     */   
/*     */   public BigInteger getPrimeExponentP()
/*     */   {
/* 166 */     return this.pe;
/*     */   }
/*     */   
/*     */   public BigInteger getPrimeExponentQ()
/*     */   {
/* 171 */     return this.qe;
/*     */   }
/*     */   
/*     */   public BigInteger getCrtCoefficient()
/*     */   {
/* 176 */     return this.coeff;
/*     */   }
/*     */   
/*     */   protected void parseKeyBits()
/*     */     throws InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/* 184 */       DerInputStream localDerInputStream1 = new DerInputStream(this.key);
/* 185 */       DerValue localDerValue = localDerInputStream1.getDerValue();
/* 186 */       if (localDerValue.tag != 48) {
/* 187 */         throw new IOException("Not a SEQUENCE");
/*     */       }
/* 189 */       DerInputStream localDerInputStream2 = localDerValue.data;
/* 190 */       int i = localDerInputStream2.getInteger();
/* 191 */       if (i != 0) {
/* 192 */         throw new IOException("Version must be 0");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 202 */       this.n = localDerInputStream2.getPositiveBigInteger();
/* 203 */       this.e = localDerInputStream2.getPositiveBigInteger();
/* 204 */       this.d = localDerInputStream2.getPositiveBigInteger();
/* 205 */       this.p = localDerInputStream2.getPositiveBigInteger();
/* 206 */       this.q = localDerInputStream2.getPositiveBigInteger();
/* 207 */       this.pe = localDerInputStream2.getPositiveBigInteger();
/* 208 */       this.qe = localDerInputStream2.getPositiveBigInteger();
/* 209 */       this.coeff = localDerInputStream2.getPositiveBigInteger();
/* 210 */       if (localDerValue.data.available() != 0) {
/* 211 */         throw new IOException("Extra data available");
/*     */       }
/*     */     } catch (IOException localIOException) {
/* 214 */       throw new InvalidKeyException("Invalid RSA private key", localIOException);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\rsa\RSAPrivateCrtKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
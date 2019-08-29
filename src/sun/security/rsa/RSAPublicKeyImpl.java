/*     */ package sun.security.rsa;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.KeyRep;
/*     */ import java.security.KeyRep.Type;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import sun.security.util.BitArray;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.x509.X509Key;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RSAPublicKeyImpl
/*     */   extends X509Key
/*     */   implements RSAPublicKey
/*     */ {
/*     */   private static final long serialVersionUID = 2644735423591199609L;
/*  51 */   private static final BigInteger THREE = BigInteger.valueOf(3L);
/*     */   
/*     */ 
/*     */   private BigInteger n;
/*     */   
/*     */   private BigInteger e;
/*     */   
/*     */ 
/*     */   public RSAPublicKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
/*     */     throws InvalidKeyException
/*     */   {
/*  62 */     this.n = paramBigInteger1;
/*  63 */     this.e = paramBigInteger2;
/*  64 */     RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), paramBigInteger2);
/*  65 */     checkExponentRange();
/*     */     
/*  67 */     this.algid = RSAPrivateCrtKeyImpl.rsaId;
/*     */     try {
/*  69 */       DerOutputStream localDerOutputStream = new DerOutputStream();
/*  70 */       localDerOutputStream.putInteger(paramBigInteger1);
/*  71 */       localDerOutputStream.putInteger(paramBigInteger2);
/*     */       
/*     */ 
/*  74 */       byte[] arrayOfByte = new DerValue((byte)48, localDerOutputStream.toByteArray()).toByteArray();
/*  75 */       setKey(new BitArray(arrayOfByte.length * 8, arrayOfByte));
/*     */     }
/*     */     catch (IOException localIOException) {
/*  78 */       throw new InvalidKeyException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public RSAPublicKeyImpl(byte[] paramArrayOfByte)
/*     */     throws InvalidKeyException
/*     */   {
/*  86 */     decode(paramArrayOfByte);
/*  87 */     RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
/*  88 */     checkExponentRange();
/*     */   }
/*     */   
/*     */   private void checkExponentRange() throws InvalidKeyException
/*     */   {
/*  93 */     if (this.e.compareTo(this.n) >= 0) {
/*  94 */       throw new InvalidKeyException("exponent is larger than modulus");
/*     */     }
/*     */     
/*     */ 
/*  98 */     if (this.e.compareTo(THREE) < 0) {
/*  99 */       throw new InvalidKeyException("exponent is smaller than 3");
/*     */     }
/*     */   }
/*     */   
/*     */   public String getAlgorithm()
/*     */   {
/* 105 */     return "RSA";
/*     */   }
/*     */   
/*     */   public BigInteger getModulus()
/*     */   {
/* 110 */     return this.n;
/*     */   }
/*     */   
/*     */   public BigInteger getPublicExponent()
/*     */   {
/* 115 */     return this.e;
/*     */   }
/*     */   
/*     */   protected void parseKeyBits()
/*     */     throws InvalidKeyException
/*     */   {
/*     */     try
/*     */     {
/* 123 */       DerInputStream localDerInputStream1 = new DerInputStream(getKey().toByteArray());
/* 124 */       DerValue localDerValue = localDerInputStream1.getDerValue();
/* 125 */       if (localDerValue.tag != 48) {
/* 126 */         throw new IOException("Not a SEQUENCE");
/*     */       }
/* 128 */       DerInputStream localDerInputStream2 = localDerValue.data;
/* 129 */       this.n = localDerInputStream2.getPositiveBigInteger();
/* 130 */       this.e = localDerInputStream2.getPositiveBigInteger();
/* 131 */       if (localDerValue.data.available() != 0) {
/* 132 */         throw new IOException("Extra data available");
/*     */       }
/*     */     } catch (IOException localIOException) {
/* 135 */       throw new InvalidKeyException("Invalid RSA public key", localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 141 */     return "Sun RSA public key, " + this.n.bitLength() + " bits\n  modulus: " + this.n + "\n  public exponent: " + this.e;
/*     */   }
/*     */   
/*     */   protected Object writeReplace() throws ObjectStreamException
/*     */   {
/* 146 */     return new KeyRep(Type.PUBLIC,
/* 147 */       getAlgorithm(), 
/* 148 */       getFormat(), 
/* 149 */       getEncoded());
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\rsa\RSAPublicKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
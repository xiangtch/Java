/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigInteger;
/*     */ import java.security.AlgorithmParameters;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.interfaces.DSAParams;
/*     */ import java.security.spec.DSAParameterSpec;
/*     */ import java.security.spec.InvalidParameterSpecException;
/*     */ import sun.security.util.BitArray;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.x509.AlgIdDSA;
/*     */ import sun.security.x509.AlgorithmId;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DSAPublicKey
/*     */   extends X509Key
/*     */   implements java.security.interfaces.DSAPublicKey, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -2994193307391104133L;
/*     */   private BigInteger y;
/*     */   
/*     */   public DSAPublicKey() {}
/*     */   
/*     */   public DSAPublicKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
/*     */     throws InvalidKeyException
/*     */   {
/*  88 */     this.y = paramBigInteger1;
/*  89 */     this.algid = new AlgIdDSA(paramBigInteger2, paramBigInteger3, paramBigInteger4);
/*     */     
/*     */     try
/*     */     {
/*  93 */       byte[] arrayOfByte = new DerValue((byte)2, paramBigInteger1.toByteArray()).toByteArray();
/*  94 */       setKey(new BitArray(arrayOfByte.length * 8, arrayOfByte));
/*  95 */       encode();
/*     */     }
/*     */     catch (IOException localIOException) {
/*  98 */       throw new InvalidKeyException("could not DER encode y: " + localIOException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public DSAPublicKey(byte[] paramArrayOfByte)
/*     */     throws InvalidKeyException
/*     */   {
/* 106 */     decode(paramArrayOfByte);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DSAParams getParams()
/*     */   {
/*     */     try
/*     */     {
/* 115 */       if ((this.algid instanceof DSAParams)) {
/* 116 */         return (DSAParams)this.algid;
/*     */       }
/*     */       
/* 119 */       AlgorithmParameters localAlgorithmParameters = this.algid.getParameters();
/* 120 */       if (localAlgorithmParameters == null) {
/* 121 */         return null;
/*     */       }
/* 123 */       return (DSAParameterSpec)localAlgorithmParameters.getParameterSpec(DSAParameterSpec.class);
/*     */     }
/*     */     catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
/*     */     
/* 127 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getY()
/*     */   {
/* 137 */     return this.y;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 141 */     return 
/* 142 */       "Sun DSA Public Key\n    Parameters:" + this.algid + "\n  y:\n" + Debug.toHexString(this.y) + "\n";
/*     */   }
/*     */   
/*     */   protected void parseKeyBits() throws InvalidKeyException {
/*     */     try {
/* 147 */       DerInputStream localDerInputStream = new DerInputStream(getKey().toByteArray());
/* 148 */       this.y = localDerInputStream.getBigInteger();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 151 */       throw new InvalidKeyException("Invalid key: y value\n" + localIOException.getMessage());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\DSAPublicKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
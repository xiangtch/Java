/*     */ package sun.security.timestamp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimestampToken
/*     */ {
/*     */   private int version;
/*     */   private ObjectIdentifier policy;
/*     */   private BigInteger serialNumber;
/*     */   private AlgorithmId hashAlgorithm;
/*     */   private byte[] hashedMessage;
/*     */   private Date genTime;
/*     */   private BigInteger nonce;
/*     */   
/*     */   public TimestampToken(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  90 */     if (paramArrayOfByte == null) {
/*  91 */       throw new IOException("No timestamp token info");
/*     */     }
/*  93 */     parse(paramArrayOfByte);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date getDate()
/*     */   {
/* 102 */     return this.genTime;
/*     */   }
/*     */   
/*     */   public AlgorithmId getHashAlgorithm() {
/* 106 */     return this.hashAlgorithm;
/*     */   }
/*     */   
/*     */   public byte[] getHashedMessage()
/*     */   {
/* 111 */     return this.hashedMessage;
/*     */   }
/*     */   
/*     */   public BigInteger getNonce() {
/* 115 */     return this.nonce;
/*     */   }
/*     */   
/*     */   public String getPolicyID() {
/* 119 */     return this.policy.toString();
/*     */   }
/*     */   
/*     */   public BigInteger getSerialNumber() {
/* 123 */     return this.serialNumber;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void parse(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 136 */     DerValue localDerValue1 = new DerValue(paramArrayOfByte);
/* 137 */     if (localDerValue1.tag != 48) {
/* 138 */       throw new IOException("Bad encoding for timestamp token info");
/*     */     }
/*     */     
/* 141 */     this.version = localDerValue1.data.getInteger();
/*     */     
/*     */ 
/* 144 */     this.policy = localDerValue1.data.getOID();
/*     */     
/*     */ 
/* 147 */     DerValue localDerValue2 = localDerValue1.data.getDerValue();
/* 148 */     this.hashAlgorithm = AlgorithmId.parse(localDerValue2.data.getDerValue());
/* 149 */     this.hashedMessage = localDerValue2.data.getOctetString();
/*     */     
/*     */ 
/* 152 */     this.serialNumber = localDerValue1.data.getBigInteger();
/*     */     
/*     */ 
/* 155 */     this.genTime = localDerValue1.data.getGeneralizedTime();
/*     */     
/*     */ 
/* 158 */     while (localDerValue1.data.available() > 0) {
/* 159 */       DerValue localDerValue3 = localDerValue1.data.getDerValue();
/* 160 */       if (localDerValue3.tag == 2) {
/* 161 */         this.nonce = localDerValue3.getBigInteger();
/* 162 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\timestamp\TimestampToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
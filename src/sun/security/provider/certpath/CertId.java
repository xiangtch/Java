/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ import sun.security.x509.SerialNumber;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CertId
/*     */ {
/*     */   private static final boolean debug = false;
/*  61 */   private static final AlgorithmId SHA1_ALGID = new AlgorithmId(AlgorithmId.SHA_oid);
/*     */   
/*     */   private final AlgorithmId hashAlgId;
/*     */   private final byte[] issuerNameHash;
/*     */   private final byte[] issuerKeyHash;
/*     */   private final SerialNumber certSerialNumber;
/*  67 */   private int myhash = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertId(X509Certificate paramX509Certificate, SerialNumber paramSerialNumber)
/*     */     throws IOException
/*     */   {
/*  75 */     this(paramX509Certificate.getSubjectX500Principal(), paramX509Certificate
/*  76 */       .getPublicKey(), paramSerialNumber);
/*     */   }
/*     */   
/*     */ 
/*     */   public CertId(X500Principal paramX500Principal, PublicKey paramPublicKey, SerialNumber paramSerialNumber)
/*     */     throws IOException
/*     */   {
/*  83 */     MessageDigest localMessageDigest = null;
/*     */     try {
/*  85 */       localMessageDigest = MessageDigest.getInstance("SHA1");
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/*  87 */       throw new IOException("Unable to create CertId", localNoSuchAlgorithmException);
/*     */     }
/*  89 */     this.hashAlgId = SHA1_ALGID;
/*  90 */     localMessageDigest.update(paramX500Principal.getEncoded());
/*  91 */     this.issuerNameHash = localMessageDigest.digest();
/*     */     
/*     */ 
/*  94 */     byte[] arrayOfByte1 = paramPublicKey.getEncoded();
/*  95 */     DerValue localDerValue = new DerValue(arrayOfByte1);
/*  96 */     DerValue[] arrayOfDerValue = new DerValue[2];
/*  97 */     arrayOfDerValue[0] = localDerValue.data.getDerValue();
/*  98 */     arrayOfDerValue[1] = localDerValue.data.getDerValue();
/*  99 */     byte[] arrayOfByte2 = arrayOfDerValue[1].getBitString();
/* 100 */     localMessageDigest.update(arrayOfByte2);
/* 101 */     this.issuerKeyHash = localMessageDigest.digest();
/* 102 */     this.certSerialNumber = paramSerialNumber;
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
/*     */   public CertId(DerInputStream paramDerInputStream)
/*     */     throws IOException
/*     */   {
/* 119 */     this.hashAlgId = AlgorithmId.parse(paramDerInputStream.getDerValue());
/* 120 */     this.issuerNameHash = paramDerInputStream.getOctetString();
/* 121 */     this.issuerKeyHash = paramDerInputStream.getOctetString();
/* 122 */     this.certSerialNumber = new SerialNumber(paramDerInputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AlgorithmId getHashAlgorithm()
/*     */   {
/* 129 */     return this.hashAlgId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getIssuerNameHash()
/*     */   {
/* 136 */     return this.issuerNameHash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getIssuerKeyHash()
/*     */   {
/* 143 */     return this.issuerKeyHash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BigInteger getSerialNumber()
/*     */   {
/* 150 */     return this.certSerialNumber.getNumber();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 159 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 160 */     this.hashAlgId.encode(localDerOutputStream);
/* 161 */     localDerOutputStream.putOctetString(this.issuerNameHash);
/* 162 */     localDerOutputStream.putOctetString(this.issuerKeyHash);
/* 163 */     this.certSerialNumber.encode(localDerOutputStream);
/* 164 */     paramDerOutputStream.write((byte)48, localDerOutputStream);
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
/*     */   public int hashCode()
/*     */   {
/* 179 */     if (this.myhash == -1) {
/* 180 */       this.myhash = this.hashAlgId.hashCode();
/* 181 */       for (int i = 0; i < this.issuerNameHash.length; i++) {
/* 182 */         this.myhash += this.issuerNameHash[i] * i;
/*     */       }
/* 184 */       for (i = 0; i < this.issuerKeyHash.length; i++) {
/* 185 */         this.myhash += this.issuerKeyHash[i] * i;
/*     */       }
/* 187 */       this.myhash += this.certSerialNumber.getNumber().hashCode();
/*     */     }
/* 189 */     return this.myhash;
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 202 */     if (this == paramObject) {
/* 203 */       return true;
/*     */     }
/* 205 */     if ((paramObject == null) || (!(paramObject instanceof CertId))) {
/* 206 */       return false;
/*     */     }
/*     */     
/* 209 */     CertId localCertId = (CertId)paramObject;
/* 210 */     if ((this.hashAlgId.equals(localCertId.getHashAlgorithm())) && 
/* 211 */       (Arrays.equals(this.issuerNameHash, localCertId.getIssuerNameHash())) && 
/* 212 */       (Arrays.equals(this.issuerKeyHash, localCertId.getIssuerKeyHash())) && 
/* 213 */       (this.certSerialNumber.getNumber().equals(localCertId.getSerialNumber()))) {
/* 214 */       return true;
/*     */     }
/* 216 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 224 */     StringBuilder localStringBuilder = new StringBuilder();
/* 225 */     localStringBuilder.append("CertId \n");
/* 226 */     localStringBuilder.append("Algorithm: " + this.hashAlgId.toString() + "\n");
/* 227 */     localStringBuilder.append("issuerNameHash \n");
/* 228 */     HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/* 229 */     localStringBuilder.append(localHexDumpEncoder.encode(this.issuerNameHash));
/* 230 */     localStringBuilder.append("\nissuerKeyHash: \n");
/* 231 */     localStringBuilder.append(localHexDumpEncoder.encode(this.issuerKeyHash));
/* 232 */     localStringBuilder.append("\n" + this.certSerialNumber.toString());
/* 233 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\CertId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
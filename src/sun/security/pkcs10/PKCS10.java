/*     */ package sun.security.pkcs10;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Base64;
/*     */ import java.util.Base64.Encoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ import sun.security.x509.X500Name;
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
/*     */ public class PKCS10
/*     */ {
/*     */   private X500Name subject;
/*     */   private PublicKey subjectPublicKeyInfo;
/*     */   private String sigAlg;
/*     */   private PKCS10Attributes attributeSet;
/*     */   private byte[] encoded;
/*     */   
/*     */   public PKCS10(PublicKey paramPublicKey)
/*     */   {
/*  88 */     this.subjectPublicKeyInfo = paramPublicKey;
/*  89 */     this.attributeSet = new PKCS10Attributes();
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
/*     */   public PKCS10(PublicKey paramPublicKey, PKCS10Attributes paramPKCS10Attributes)
/*     */   {
/* 103 */     this.subjectPublicKeyInfo = paramPublicKey;
/* 104 */     this.attributeSet = paramPKCS10Attributes;
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
/*     */   public PKCS10(byte[] paramArrayOfByte)
/*     */     throws IOException, SignatureException, NoSuchAlgorithmException
/*     */   {
/* 127 */     this.encoded = paramArrayOfByte;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 133 */     DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte);
/* 134 */     DerValue[] arrayOfDerValue = localDerInputStream.getSequence(3);
/*     */     
/* 136 */     if (arrayOfDerValue.length != 3) {
/* 137 */       throw new IllegalArgumentException("not a PKCS #10 request");
/*     */     }
/* 139 */     paramArrayOfByte = arrayOfDerValue[0].toByteArray();
/* 140 */     AlgorithmId localAlgorithmId = AlgorithmId.parse(arrayOfDerValue[1]);
/* 141 */     byte[] arrayOfByte = arrayOfDerValue[2].getBitString();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 149 */     BigInteger localBigInteger = arrayOfDerValue[0].data.getBigInteger();
/* 150 */     if (!localBigInteger.equals(BigInteger.ZERO)) {
/* 151 */       throw new IllegalArgumentException("not PKCS #10 v1");
/*     */     }
/* 153 */     this.subject = new X500Name(arrayOfDerValue[0].data);
/* 154 */     this.subjectPublicKeyInfo = X509Key.parse(arrayOfDerValue[0].data.getDerValue());
/*     */     
/*     */ 
/* 157 */     if (arrayOfDerValue[0].data.available() != 0) {
/* 158 */       this.attributeSet = new PKCS10Attributes(arrayOfDerValue[0].data);
/*     */     } else {
/* 160 */       this.attributeSet = new PKCS10Attributes();
/*     */     }
/* 162 */     if (arrayOfDerValue[0].data.available() != 0) {
/* 163 */       throw new IllegalArgumentException("illegal PKCS #10 data");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 170 */       this.sigAlg = localAlgorithmId.getName();
/* 171 */       Signature localSignature = Signature.getInstance(this.sigAlg);
/* 172 */       localSignature.initVerify(this.subjectPublicKeyInfo);
/* 173 */       localSignature.update(paramArrayOfByte);
/* 174 */       if (!localSignature.verify(arrayOfByte))
/* 175 */         throw new SignatureException("Invalid PKCS #10 signature");
/*     */     } catch (InvalidKeyException localInvalidKeyException) {
/* 177 */       throw new SignatureException("invalid key");
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
/*     */ 
/*     */ 
/*     */   public void encodeAndSign(X500Name paramX500Name, Signature paramSignature)
/*     */     throws CertificateException, IOException, SignatureException
/*     */   {
/* 197 */     if (this.encoded != null) {
/* 198 */       throw new SignatureException("request is already signed");
/*     */     }
/* 200 */     this.subject = paramX500Name;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 205 */     Object localObject = new DerOutputStream();
/* 206 */     ((DerOutputStream)localObject).putInteger(BigInteger.ZERO);
/* 207 */     paramX500Name.encode((DerOutputStream)localObject);
/* 208 */     ((DerOutputStream)localObject).write(this.subjectPublicKeyInfo.getEncoded());
/* 209 */     this.attributeSet.encode((OutputStream)localObject);
/*     */     
/* 211 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 212 */     localDerOutputStream.write((byte)48, (DerOutputStream)localObject);
/* 213 */     byte[] arrayOfByte1 = localDerOutputStream.toByteArray();
/* 214 */     localObject = localDerOutputStream;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 219 */     paramSignature.update(arrayOfByte1, 0, arrayOfByte1.length);
/*     */     
/* 221 */     byte[] arrayOfByte2 = paramSignature.sign();
/* 222 */     this.sigAlg = paramSignature.getAlgorithm();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 227 */     AlgorithmId localAlgorithmId = null;
/*     */     try {
/* 229 */       localAlgorithmId = AlgorithmId.get(paramSignature.getAlgorithm());
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 231 */       throw new SignatureException(localNoSuchAlgorithmException);
/*     */     }
/* 233 */     localAlgorithmId.encode((DerOutputStream)localObject);
/* 234 */     ((DerOutputStream)localObject).putBitString(arrayOfByte2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 239 */     localDerOutputStream = new DerOutputStream();
/* 240 */     localDerOutputStream.write((byte)48, (DerOutputStream)localObject);
/* 241 */     this.encoded = localDerOutputStream.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public X500Name getSubjectName()
/*     */   {
/* 247 */     return this.subject;
/*     */   }
/*     */   
/*     */ 
/*     */   public PublicKey getSubjectPublicKeyInfo()
/*     */   {
/* 253 */     return this.subjectPublicKeyInfo;
/*     */   }
/*     */   
/*     */   public String getSigAlg()
/*     */   {
/* 258 */     return this.sigAlg;
/*     */   }
/*     */   
/*     */ 
/*     */   public PKCS10Attributes getAttributes()
/*     */   {
/* 264 */     return this.attributeSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */   {
/* 274 */     if (this.encoded != null) {
/* 275 */       return (byte[])this.encoded.clone();
/*     */     }
/* 277 */     return null;
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
/*     */   public void print(PrintStream paramPrintStream)
/*     */     throws IOException, SignatureException
/*     */   {
/* 296 */     if (this.encoded == null) {
/* 297 */       throw new SignatureException("Cert request was not signed");
/*     */     }
/*     */     
/* 300 */     byte[] arrayOfByte = { 13, 10 };
/* 301 */     paramPrintStream.println("-----BEGIN NEW CERTIFICATE REQUEST-----");
/* 302 */     paramPrintStream.println(Base64.getMimeEncoder(64, arrayOfByte).encodeToString(this.encoded));
/* 303 */     paramPrintStream.println("-----END NEW CERTIFICATE REQUEST-----");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 310 */     return 
/*     */     
/*     */ 
/* 313 */       "[PKCS #10 certificate request:\n" + this.subjectPublicKeyInfo.toString() + " subject: <" + this.subject + ">\n attributes: " + this.attributeSet.toString() + "\n]";
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 329 */     if (this == paramObject)
/* 330 */       return true;
/* 331 */     if (!(paramObject instanceof PKCS10))
/* 332 */       return false;
/* 333 */     if (this.encoded == null)
/* 334 */       return false;
/* 335 */     byte[] arrayOfByte = ((PKCS10)paramObject).getEncoded();
/* 336 */     if (arrayOfByte == null) {
/* 337 */       return false;
/*     */     }
/* 339 */     return Arrays.equals(this.encoded, arrayOfByte);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 349 */     int i = 0;
/* 350 */     if (this.encoded != null)
/* 351 */       for (int j = 1; j < this.encoded.length; j++)
/* 352 */         i += this.encoded[j] * j;
/* 353 */     return i;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\pkcs10\PKCS10.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.security.tools.keytool;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.KeyPair;
/*     */ import java.security.KeyPairGenerator;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Date;
/*     */ import java.util.Random;
/*     */ import sun.security.pkcs10.PKCS10;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ import sun.security.x509.CertificateAlgorithmId;
/*     */ import sun.security.x509.CertificateExtensions;
/*     */ import sun.security.x509.CertificateSerialNumber;
/*     */ import sun.security.x509.CertificateValidity;
/*     */ import sun.security.x509.CertificateVersion;
/*     */ import sun.security.x509.CertificateX509Key;
/*     */ import sun.security.x509.X500Name;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ import sun.security.x509.X509CertInfo;
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
/*     */ public final class CertAndKeyGen
/*     */ {
/*     */   private SecureRandom prng;
/*     */   private String sigAlg;
/*     */   private KeyPairGenerator keyGen;
/*     */   private PublicKey publicKey;
/*     */   private PrivateKey privateKey;
/*     */   
/*     */   public CertAndKeyGen(String paramString1, String paramString2)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/*  76 */     this.keyGen = KeyPairGenerator.getInstance(paramString1);
/*  77 */     this.sigAlg = paramString2;
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
/*     */   public CertAndKeyGen(String paramString1, String paramString2, String paramString3)
/*     */     throws NoSuchAlgorithmException, NoSuchProviderException
/*     */   {
/*  94 */     if (paramString3 == null) {
/*  95 */       this.keyGen = KeyPairGenerator.getInstance(paramString1);
/*     */     } else {
/*     */       try {
/*  98 */         this.keyGen = KeyPairGenerator.getInstance(paramString1, paramString3);
/*     */       }
/*     */       catch (Exception localException) {
/* 101 */         this.keyGen = KeyPairGenerator.getInstance(paramString1);
/*     */       }
/*     */     }
/* 104 */     this.sigAlg = paramString2;
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
/*     */   public void setRandom(SecureRandom paramSecureRandom)
/*     */   {
/* 117 */     this.prng = paramSecureRandom;
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
/*     */   public void generate(int paramInt)
/*     */     throws InvalidKeyException
/*     */   {
/*     */     KeyPair localKeyPair;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 145 */       if (this.prng == null) {
/* 146 */         this.prng = new SecureRandom();
/*     */       }
/* 148 */       this.keyGen.initialize(paramInt, this.prng);
/* 149 */       localKeyPair = this.keyGen.generateKeyPair();
/*     */     }
/*     */     catch (Exception localException) {
/* 152 */       throw new IllegalArgumentException(localException.getMessage());
/*     */     }
/*     */     
/* 155 */     this.publicKey = localKeyPair.getPublic();
/* 156 */     this.privateKey = localKeyPair.getPrivate();
/*     */     
/*     */ 
/*     */ 
/* 160 */     if (!"X.509".equalsIgnoreCase(this.publicKey.getFormat()))
/*     */     {
/* 162 */       throw new IllegalArgumentException("publicKey's is not X.509, but " + this.publicKey.getFormat());
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
/*     */   public X509Key getPublicKey()
/*     */   {
/* 179 */     if (!(this.publicKey instanceof X509Key)) {
/* 180 */       return null;
/*     */     }
/* 182 */     return (X509Key)this.publicKey;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PublicKey getPublicKeyAnyway()
/*     */   {
/* 193 */     return this.publicKey;
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
/*     */   public PrivateKey getPrivateKey()
/*     */   {
/* 206 */     return this.privateKey;
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
/*     */   public X509Certificate getSelfCertificate(X500Name paramX500Name, Date paramDate, long paramLong)
/*     */     throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException
/*     */   {
/* 233 */     return getSelfCertificate(paramX500Name, paramDate, paramLong, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate getSelfCertificate(X500Name paramX500Name, Date paramDate, long paramLong, CertificateExtensions paramCertificateExtensions)
/*     */     throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException
/*     */   {
/*     */     try
/*     */     {
/* 246 */       Date localDate = new Date();
/* 247 */       localDate.setTime(paramDate.getTime() + paramLong * 1000L);
/*     */       
/* 249 */       CertificateValidity localCertificateValidity = new CertificateValidity(paramDate, localDate);
/*     */       
/*     */ 
/* 252 */       X509CertInfo localX509CertInfo = new X509CertInfo();
/*     */       
/* 254 */       localX509CertInfo.set("version", new CertificateVersion(2));
/*     */       
/* 256 */       localX509CertInfo.set("serialNumber", new CertificateSerialNumber(new Random()
/* 257 */         .nextInt() & 0x7FFFFFFF));
/* 258 */       AlgorithmId localAlgorithmId = AlgorithmId.get(this.sigAlg);
/* 259 */       localX509CertInfo.set("algorithmID", new CertificateAlgorithmId(localAlgorithmId));
/*     */       
/* 261 */       localX509CertInfo.set("subject", paramX500Name);
/* 262 */       localX509CertInfo.set("key", new CertificateX509Key(this.publicKey));
/* 263 */       localX509CertInfo.set("validity", localCertificateValidity);
/* 264 */       localX509CertInfo.set("issuer", paramX500Name);
/* 265 */       if (paramCertificateExtensions != null) { localX509CertInfo.set("extensions", paramCertificateExtensions);
/*     */       }
/* 267 */       X509CertImpl localX509CertImpl = new X509CertImpl(localX509CertInfo);
/* 268 */       localX509CertImpl.sign(this.privateKey, this.sigAlg);
/*     */       
/* 270 */       return localX509CertImpl;
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 274 */       throw new CertificateEncodingException("getSelfCert: " + localIOException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public X509Certificate getSelfCertificate(X500Name paramX500Name, long paramLong)
/*     */     throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException
/*     */   {
/* 283 */     return getSelfCertificate(paramX500Name, new Date(), paramLong);
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
/*     */   public PKCS10 getCertRequest(X500Name paramX500Name)
/*     */     throws InvalidKeyException, SignatureException
/*     */   {
/* 303 */     PKCS10 localPKCS10 = new PKCS10(this.publicKey);
/*     */     try
/*     */     {
/* 306 */       Signature localSignature = Signature.getInstance(this.sigAlg);
/* 307 */       localSignature.initSign(this.privateKey);
/* 308 */       localPKCS10.encodeAndSign(paramX500Name, localSignature);
/*     */     }
/*     */     catch (CertificateException localCertificateException) {
/* 311 */       throw new SignatureException(this.sigAlg + " CertificateException");
/*     */     }
/*     */     catch (IOException localIOException) {
/* 314 */       throw new SignatureException(this.sigAlg + " IOException");
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
/*     */     {
/* 318 */       throw new SignatureException(this.sigAlg + " unavailable?");
/*     */     }
/* 320 */     return localPKCS10;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\tools\keytool\CertAndKeyGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
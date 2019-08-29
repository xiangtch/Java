/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertPathValidatorException.BasicReason;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateExpiredException;
/*     */ import java.security.cert.CertificateNotYetValidException;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXReason;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.security.interfaces.DSAParams;
/*     */ import java.security.interfaces.DSAPublicKey;
/*     */ import java.security.spec.DSAPublicKeySpec;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.x509.X500Name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class BasicChecker
/*     */   extends PKIXCertPathChecker
/*     */ {
/*  62 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */   private final PublicKey trustedPubKey;
/*     */   
/*     */ 
/*     */   private final X500Principal caName;
/*     */   
/*     */   private final Date date;
/*     */   
/*     */   private final String sigProvider;
/*     */   
/*     */   private final boolean sigOnly;
/*     */   
/*     */   private X500Principal prevSubject;
/*     */   
/*     */   private PublicKey prevPubKey;
/*     */   
/*     */ 
/*     */   BasicChecker(TrustAnchor paramTrustAnchor, Date paramDate, String paramString, boolean paramBoolean)
/*     */   {
/*  83 */     if (paramTrustAnchor.getTrustedCert() != null) {
/*  84 */       this.trustedPubKey = paramTrustAnchor.getTrustedCert().getPublicKey();
/*  85 */       this.caName = paramTrustAnchor.getTrustedCert().getSubjectX500Principal();
/*     */     } else {
/*  87 */       this.trustedPubKey = paramTrustAnchor.getCAPublicKey();
/*  88 */       this.caName = paramTrustAnchor.getCA();
/*     */     }
/*  90 */     this.date = paramDate;
/*  91 */     this.sigProvider = paramString;
/*  92 */     this.sigOnly = paramBoolean;
/*  93 */     this.prevPubKey = this.trustedPubKey;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(boolean paramBoolean)
/*     */     throws CertPathValidatorException
/*     */   {
/* 102 */     if (!paramBoolean) {
/* 103 */       this.prevPubKey = this.trustedPubKey;
/* 104 */       if (PKIX.isDSAPublicKeyWithoutParams(this.prevPubKey))
/*     */       {
/*     */ 
/*     */ 
/* 108 */         throw new CertPathValidatorException("Key parameters missing");
/*     */       }
/* 110 */       this.prevSubject = this.caName;
/*     */     } else {
/* 112 */       throw new CertPathValidatorException("forward checking not supported");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isForwardCheckingSupported()
/*     */   {
/* 119 */     return false;
/*     */   }
/*     */   
/*     */   public Set<String> getSupportedExtensions()
/*     */   {
/* 124 */     return null;
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
/*     */   public void check(Certificate paramCertificate, Collection<String> paramCollection)
/*     */     throws CertPathValidatorException
/*     */   {
/* 141 */     X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
/*     */     
/* 143 */     if (!this.sigOnly) {
/* 144 */       verifyValidity(localX509Certificate);
/* 145 */       verifyNameChaining(localX509Certificate);
/*     */     }
/* 147 */     verifySignature(localX509Certificate);
/*     */     
/* 149 */     updateState(localX509Certificate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void verifySignature(X509Certificate paramX509Certificate)
/*     */     throws CertPathValidatorException
/*     */   {
/* 161 */     String str = "signature";
/* 162 */     if (debug != null) {
/* 163 */       debug.println("---checking " + str + "...");
/*     */     }
/*     */     try {
/* 166 */       paramX509Certificate.verify(this.prevPubKey, this.sigProvider);
/*     */     } catch (SignatureException localSignatureException) {
/* 168 */       throw new CertPathValidatorException(str + " check failed", localSignatureException, null, -1, BasicReason.INVALID_SIGNATURE);
/*     */     }
/*     */     catch (GeneralSecurityException localGeneralSecurityException)
/*     */     {
/* 172 */       throw new CertPathValidatorException(str + " check failed", localGeneralSecurityException);
/*     */     }
/*     */     
/* 175 */     if (debug != null) {
/* 176 */       debug.println(str + " verified.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void verifyValidity(X509Certificate paramX509Certificate)
/*     */     throws CertPathValidatorException
/*     */   {
/* 185 */     String str = "validity";
/* 186 */     if (debug != null) {
/* 187 */       debug.println("---checking " + str + ":" + this.date.toString() + "...");
/*     */     }
/*     */     try {
/* 190 */       paramX509Certificate.checkValidity(this.date);
/*     */     } catch (CertificateExpiredException localCertificateExpiredException) {
/* 192 */       throw new CertPathValidatorException(str + " check failed", localCertificateExpiredException, null, -1, BasicReason.EXPIRED);
/*     */     }
/*     */     catch (CertificateNotYetValidException localCertificateNotYetValidException) {
/* 195 */       throw new CertPathValidatorException(str + " check failed", localCertificateNotYetValidException, null, -1, BasicReason.NOT_YET_VALID);
/*     */     }
/*     */     
/*     */ 
/* 199 */     if (debug != null) {
/* 200 */       debug.println(str + " verified.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void verifyNameChaining(X509Certificate paramX509Certificate)
/*     */     throws CertPathValidatorException
/*     */   {
/* 209 */     if (this.prevSubject != null)
/*     */     {
/* 211 */       String str = "subject/issuer name chaining";
/* 212 */       if (debug != null) {
/* 213 */         debug.println("---checking " + str + "...");
/*     */       }
/* 215 */       X500Principal localX500Principal = paramX509Certificate.getIssuerX500Principal();
/*     */       
/*     */ 
/* 218 */       if (X500Name.asX500Name(localX500Principal).isEmpty()) {
/* 219 */         throw new CertPathValidatorException(str + " check failed: empty/null issuer DN in certificate is invalid", null, null, -1, PKIXReason.NAME_CHAINING);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 225 */       if (!localX500Principal.equals(this.prevSubject)) {
/* 226 */         throw new CertPathValidatorException(str + " check failed", null, null, -1, PKIXReason.NAME_CHAINING);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 231 */       if (debug != null) {
/* 232 */         debug.println(str + " verified.");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void updateState(X509Certificate paramX509Certificate)
/*     */     throws CertPathValidatorException
/*     */   {
/* 242 */     PublicKey localPublicKey = paramX509Certificate.getPublicKey();
/* 243 */     if (debug != null) {
/* 244 */       debug.println("BasicChecker.updateState issuer: " + paramX509Certificate
/* 245 */         .getIssuerX500Principal().toString() + "; subject: " + paramX509Certificate
/* 246 */         .getSubjectX500Principal() + "; serial#: " + paramX509Certificate
/* 247 */         .getSerialNumber().toString());
/*     */     }
/* 249 */     if (PKIX.isDSAPublicKeyWithoutParams(localPublicKey))
/*     */     {
/* 251 */       localPublicKey = makeInheritedParamsKey(localPublicKey, this.prevPubKey);
/* 252 */       if (debug != null) { debug.println("BasicChecker.updateState Made key with inherited params");
/*     */       }
/*     */     }
/* 255 */     this.prevPubKey = localPublicKey;
/* 256 */     this.prevSubject = paramX509Certificate.getSubjectX500Principal();
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
/*     */   static PublicKey makeInheritedParamsKey(PublicKey paramPublicKey1, PublicKey paramPublicKey2)
/*     */     throws CertPathValidatorException
/*     */   {
/* 271 */     if ((!(paramPublicKey1 instanceof DSAPublicKey)) || (!(paramPublicKey2 instanceof DSAPublicKey)))
/*     */     {
/* 273 */       throw new CertPathValidatorException("Input key is not appropriate type for inheriting parameters");
/*     */     }
/*     */     
/* 276 */     DSAParams localDSAParams = ((DSAPublicKey)paramPublicKey2).getParams();
/* 277 */     if (localDSAParams == null)
/* 278 */       throw new CertPathValidatorException("Key parameters missing");
/*     */     try {
/* 280 */       BigInteger localBigInteger = ((DSAPublicKey)paramPublicKey1).getY();
/* 281 */       KeyFactory localKeyFactory = KeyFactory.getInstance("DSA");
/*     */       
/*     */ 
/*     */ 
/* 285 */       DSAPublicKeySpec localDSAPublicKeySpec = new DSAPublicKeySpec(localBigInteger, localDSAParams.getP(), localDSAParams.getQ(), localDSAParams.getG());
/* 286 */       return localKeyFactory.generatePublic(localDSAPublicKeySpec);
/*     */     }
/*     */     catch (GeneralSecurityException localGeneralSecurityException)
/*     */     {
/* 290 */       throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + localGeneralSecurityException.getMessage(), localGeneralSecurityException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   PublicKey getPublicKey()
/*     */   {
/* 300 */     return this.prevPubKey;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\BasicChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
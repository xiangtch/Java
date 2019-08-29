/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.Timestamp;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertPathChecker;
/*     */ import java.security.cert.CertPathParameters;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertPathValidatorResult;
/*     */ import java.security.cert.CertPathValidatorSpi;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXCertPathValidatorResult;
/*     */ import java.security.cert.PKIXReason;
/*     */ import java.security.cert.PKIXRevocationChecker;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PKIXCertPathValidator
/*     */   extends CertPathValidatorSpi
/*     */ {
/*  49 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertPathChecker engineGetRevocationChecker()
/*     */   {
/*  58 */     return new RevocationChecker();
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
/*     */   public CertPathValidatorResult engineValidate(CertPath paramCertPath, CertPathParameters paramCertPathParameters)
/*     */     throws CertPathValidatorException, InvalidAlgorithmParameterException
/*     */   {
/*  79 */     PKIX.ValidatorParams localValidatorParams = PKIX.checkParams(paramCertPath, paramCertPathParameters);
/*  80 */     return validate(localValidatorParams);
/*     */   }
/*     */   
/*     */   private static PKIXCertPathValidatorResult validate(PKIX.ValidatorParams paramValidatorParams)
/*     */     throws CertPathValidatorException
/*     */   {
/*  86 */     if (debug != null) {
/*  87 */       debug.println("PKIXCertPathValidator.engineValidate()...");
/*     */     }
/*     */     
/*     */ 
/*  91 */     AdaptableX509CertSelector localAdaptableX509CertSelector = null;
/*  92 */     List localList = paramValidatorParams.certificates();
/*  93 */     if (!localList.isEmpty()) {
/*  94 */       localAdaptableX509CertSelector = new AdaptableX509CertSelector();
/*  95 */       localObject = (X509Certificate)localList.get(0);
/*     */       
/*  97 */       localAdaptableX509CertSelector.setSubject(((X509Certificate)localObject).getIssuerX500Principal());
/*     */       
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 103 */         X509CertImpl localX509CertImpl = X509CertImpl.toImpl((X509Certificate)localObject);
/* 104 */         localAdaptableX509CertSelector.setSkiAndSerialNumber(localX509CertImpl
/* 105 */           .getAuthorityKeyIdentifierExtension());
/*     */       }
/*     */       catch (CertificateException|IOException localCertificateException) {}
/*     */     }
/*     */     
/*     */ 
/* 111 */     Object localObject = null;
/*     */     
/*     */ 
/*     */ 
/* 115 */     for (TrustAnchor localTrustAnchor : paramValidatorParams.trustAnchors()) {
/* 116 */       X509Certificate localX509Certificate = localTrustAnchor.getTrustedCert();
/* 117 */       if (localX509Certificate != null)
/*     */       {
/*     */ 
/* 120 */         if ((localAdaptableX509CertSelector != null) && (!localAdaptableX509CertSelector.match(localX509Certificate))) {
/* 121 */           if (debug == null) continue;
/* 122 */           debug.println("NO - don't try this trustedCert"); continue;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 127 */         if (debug != null) {
/* 128 */           debug.println("YES - try this trustedCert");
/* 129 */           debug.println("anchor.getTrustedCert().getSubjectX500Principal() = " + localX509Certificate
/*     */           
/* 131 */             .getSubjectX500Principal());
/*     */         }
/*     */       }
/* 134 */       else if (debug != null) {
/* 135 */         debug.println("PKIXCertPathValidator.engineValidate(): anchor.getTrustedCert() == null");
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 141 */         return validate(localTrustAnchor, paramValidatorParams);
/*     */       }
/*     */       catch (CertPathValidatorException localCertPathValidatorException) {
/* 144 */         localObject = localCertPathValidatorException;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 150 */     if (localObject != null) {
/* 151 */       throw ((Throwable)localObject);
/*     */     }
/*     */     
/* 154 */     throw new CertPathValidatorException("Path does not chain with any of the trust anchors", null, null, -1, PKIXReason.NO_TRUST_ANCHOR);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static PKIXCertPathValidatorResult validate(TrustAnchor paramTrustAnchor, PKIX.ValidatorParams paramValidatorParams)
/*     */     throws CertPathValidatorException
/*     */   {
/* 164 */     UntrustedChecker localUntrustedChecker = new UntrustedChecker();
/* 165 */     X509Certificate localX509Certificate = paramTrustAnchor.getTrustedCert();
/* 166 */     if (localX509Certificate != null) {
/* 167 */       localUntrustedChecker.check(localX509Certificate);
/*     */     }
/*     */     
/* 170 */     int i = paramValidatorParams.certificates().size();
/*     */     
/*     */ 
/* 173 */     ArrayList localArrayList = new ArrayList();
/*     */     
/* 175 */     localArrayList.add(localUntrustedChecker);
/* 176 */     localArrayList.add(new AlgorithmChecker(paramTrustAnchor, null, paramValidatorParams.date(), paramValidatorParams
/* 177 */       .timestamp(), paramValidatorParams.variant()));
/* 178 */     localArrayList.add(new KeyChecker(i, paramValidatorParams
/* 179 */       .targetCertConstraints()));
/* 180 */     localArrayList.add(new ConstraintsChecker(i));
/*     */     
/*     */ 
/* 183 */     PolicyNodeImpl localPolicyNodeImpl = new PolicyNodeImpl(null, "2.5.29.32.0", null, false, Collections.singleton("2.5.29.32.0"), false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 190 */     PolicyChecker localPolicyChecker = new PolicyChecker(paramValidatorParams.initialPolicies(), i, paramValidatorParams.explicitPolicyRequired(), paramValidatorParams.policyMappingInhibited(), paramValidatorParams.anyPolicyInhibited(), paramValidatorParams.policyQualifiersRejected(), localPolicyNodeImpl);
/*     */     
/* 192 */     localArrayList.add(localPolicyChecker);
/*     */     
/*     */ 
/*     */ 
/* 196 */     Date localDate = null;
/*     */     
/*     */ 
/* 199 */     if (((paramValidatorParams.variant() == "code signing") || 
/* 200 */       (paramValidatorParams.variant() == "plugin code signing")) && 
/* 201 */       (paramValidatorParams.timestamp() != null)) {
/* 202 */       localDate = paramValidatorParams.timestamp().getTimestamp();
/*     */     } else {
/* 204 */       localDate = paramValidatorParams.date();
/*     */     }
/*     */     
/* 207 */     BasicChecker localBasicChecker = new BasicChecker(paramTrustAnchor, localDate, paramValidatorParams.sigProvider(), false);
/* 208 */     localArrayList.add(localBasicChecker);
/*     */     
/* 210 */     int j = 0;
/* 211 */     List localList = paramValidatorParams.certPathCheckers();
/* 212 */     for (PKIXCertPathChecker localPKIXCertPathChecker : localList) {
/* 213 */       if ((localPKIXCertPathChecker instanceof PKIXRevocationChecker)) {
/* 214 */         if (j != 0) {
/* 215 */           throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified");
/*     */         }
/*     */         
/* 218 */         j = 1;
/*     */         
/* 220 */         if ((localPKIXCertPathChecker instanceof RevocationChecker)) {
/* 221 */           ((RevocationChecker)localPKIXCertPathChecker).init(paramTrustAnchor, paramValidatorParams);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 227 */     if ((paramValidatorParams.revocationEnabled()) && (j == 0)) {
/* 228 */       localArrayList.add(new RevocationChecker(paramTrustAnchor, paramValidatorParams));
/*     */     }
/*     */     
/* 231 */     localArrayList.addAll(localList);
/*     */     
/* 233 */     PKIXMasterCertPathValidator.validate(paramValidatorParams.certPath(), paramValidatorParams
/* 234 */       .certificates(), localArrayList);
/*     */     
/*     */ 
/* 237 */     return new PKIXCertPathValidatorResult(paramTrustAnchor, localPolicyChecker.getPolicyTree(), localBasicChecker
/* 238 */       .getPublicKey());
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\PKIXCertPathValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
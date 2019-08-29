/*     */ package sun.security.validator;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.AlgorithmConstraints;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Timestamp;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertPathBuilder;
/*     */ import java.security.cert.CertPathValidator;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.CollectionCertStoreParameters;
/*     */ import java.security.cert.PKIXBuilderParameters;
/*     */ import java.security.cert.PKIXCertPathBuilderResult;
/*     */ import java.security.cert.PKIXCertPathValidatorResult;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.provider.certpath.AlgorithmChecker;
/*     */ import sun.security.provider.certpath.PKIXExtendedParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PKIXValidator
/*     */   extends Validator
/*     */ {
/*  62 */   private static final boolean checkTLSRevocation = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.net.ssl.checkRevocation"))).booleanValue();
/*     */   
/*     */   private static final boolean TRY_VALIDATOR = true;
/*     */   
/*     */   private final Set<X509Certificate> trustedCerts;
/*     */   
/*     */   private final PKIXBuilderParameters parameterTemplate;
/*  69 */   private int certPathLength = -1;
/*     */   
/*     */   private final Map<X500Principal, List<PublicKey>> trustedSubjects;
/*     */   
/*     */   private final CertificateFactory factory;
/*     */   private final boolean plugin;
/*     */   
/*     */   PKIXValidator(String paramString, Collection<X509Certificate> paramCollection)
/*     */   {
/*  78 */     super("PKIX", paramString);
/*  79 */     if ((paramCollection instanceof Set)) {
/*  80 */       this.trustedCerts = ((Set)paramCollection);
/*     */     } else {
/*  82 */       this.trustedCerts = new HashSet(paramCollection);
/*     */     }
/*  84 */     HashSet localHashSet = new HashSet();
/*  85 */     for (Iterator localIterator1 = paramCollection.iterator(); localIterator1.hasNext();) { localX509Certificate = (X509Certificate)localIterator1.next();
/*  86 */       localHashSet.add(new TrustAnchor(localX509Certificate, null));
/*     */     }
/*     */     X509Certificate localX509Certificate;
/*  89 */     try { this.parameterTemplate = new PKIXBuilderParameters(localHashSet, null);
/*     */     } catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException) {
/*  91 */       throw new RuntimeException("Unexpected error: " + localInvalidAlgorithmParameterException.toString(), localInvalidAlgorithmParameterException);
/*     */     }
/*  93 */     setDefaultParameters(paramString);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 100 */     this.trustedSubjects = new HashMap();
/* 101 */     for (Iterator localIterator2 = paramCollection.iterator(); localIterator2.hasNext();) { localX509Certificate = (X509Certificate)localIterator2.next();
/* 102 */       X500Principal localX500Principal = localX509Certificate.getSubjectX500Principal();
/*     */       Object localObject;
/* 104 */       if (this.trustedSubjects.containsKey(localX500Principal)) {
/* 105 */         localObject = (List)this.trustedSubjects.get(localX500Principal);
/*     */       } else {
/* 107 */         localObject = new ArrayList();
/* 108 */         this.trustedSubjects.put(localX500Principal, localObject);
/*     */       }
/* 110 */       ((List)localObject).add(localX509Certificate.getPublicKey());
/*     */     }
/*     */     try {
/* 113 */       this.factory = CertificateFactory.getInstance("X.509");
/*     */     } catch (CertificateException localCertificateException) {
/* 115 */       throw new RuntimeException("Internal error", localCertificateException);
/*     */     }
/* 117 */     this.plugin = paramString.equals("plugin code signing");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   PKIXValidator(String paramString, PKIXBuilderParameters paramPKIXBuilderParameters)
/*     */   {
/* 124 */     super("PKIX", paramString);
/* 125 */     this.trustedCerts = new HashSet();
/* 126 */     for (Iterator localIterator = paramPKIXBuilderParameters.getTrustAnchors().iterator(); localIterator.hasNext();) { localObject1 = (TrustAnchor)localIterator.next();
/* 127 */       localObject2 = ((TrustAnchor)localObject1).getTrustedCert();
/* 128 */       if (localObject2 != null)
/* 129 */         this.trustedCerts.add(localObject2); }
/*     */     Object localObject1;
/*     */     Object localObject2;
/* 132 */     this.parameterTemplate = paramPKIXBuilderParameters;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 139 */     this.trustedSubjects = new HashMap();
/* 140 */     for (localIterator = this.trustedCerts.iterator(); localIterator.hasNext();) { localObject1 = (X509Certificate)localIterator.next();
/* 141 */       localObject2 = ((X509Certificate)localObject1).getSubjectX500Principal();
/*     */       Object localObject3;
/* 143 */       if (this.trustedSubjects.containsKey(localObject2)) {
/* 144 */         localObject3 = (List)this.trustedSubjects.get(localObject2);
/*     */       } else {
/* 146 */         localObject3 = new ArrayList();
/* 147 */         this.trustedSubjects.put(localObject2, localObject3);
/*     */       }
/* 149 */       ((List)localObject3).add(((X509Certificate)localObject1).getPublicKey());
/*     */     }
/*     */     try {
/* 152 */       this.factory = CertificateFactory.getInstance("X.509");
/*     */     } catch (CertificateException localCertificateException) {
/* 154 */       throw new RuntimeException("Internal error", localCertificateException);
/*     */     }
/* 156 */     this.plugin = paramString.equals("plugin code signing");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Collection<X509Certificate> getTrustedCertificates()
/*     */   {
/* 163 */     return this.trustedCerts;
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
/*     */   public int getCertPathLength()
/*     */   {
/* 177 */     return this.certPathLength;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setDefaultParameters(String paramString)
/*     */   {
/* 185 */     if ((paramString == "tls server") || (paramString == "tls client"))
/*     */     {
/* 187 */       this.parameterTemplate.setRevocationEnabled(checkTLSRevocation);
/*     */     } else {
/* 189 */       this.parameterTemplate.setRevocationEnabled(false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKIXBuilderParameters getParameters()
/*     */   {
/* 199 */     return this.parameterTemplate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   X509Certificate[] engineValidate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, AlgorithmConstraints paramAlgorithmConstraints, Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 207 */     if ((paramArrayOfX509Certificate == null) || (paramArrayOfX509Certificate.length == 0)) {
/* 208 */       throw new CertificateException("null or zero-length certificate chain");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 213 */     PKIXExtendedParameters localPKIXExtendedParameters = null;
/*     */     try
/*     */     {
/* 216 */       localPKIXExtendedParameters = new PKIXExtendedParameters((PKIXBuilderParameters)this.parameterTemplate.clone(), (paramObject instanceof Timestamp) ? (Timestamp)paramObject : null, this.variant);
/*     */     }
/*     */     catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException1) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 225 */     if (paramAlgorithmConstraints != null) {
/* 226 */       localPKIXExtendedParameters.addCertPathChecker(new AlgorithmChecker(paramAlgorithmConstraints, null, this.variant));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 233 */     Object localObject1 = null;
/* 234 */     X509Certificate[] arrayOfX509Certificate; for (int i = 0; i < paramArrayOfX509Certificate.length; i++) {
/* 235 */       localObject2 = paramArrayOfX509Certificate[i];
/* 236 */       localX500Principal = ((X509Certificate)localObject2).getSubjectX500Principal();
/* 237 */       if ((i != 0) && 
/* 238 */         (!localX500Principal.equals(localObject1)))
/*     */       {
/* 240 */         return doBuild(paramArrayOfX509Certificate, paramCollection, localPKIXExtendedParameters);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 249 */       if ((this.trustedCerts.contains(localObject2)) || (
/* 250 */         (this.trustedSubjects.containsKey(localX500Principal)) && 
/* 251 */         (((List)this.trustedSubjects.get(localX500Principal)).contains(((X509Certificate)localObject2)
/* 252 */         .getPublicKey())))) {
/* 253 */         if (i == 0) {
/* 254 */           return new X509Certificate[] { paramArrayOfX509Certificate[0] };
/*     */         }
/*     */         
/* 257 */         arrayOfX509Certificate = new X509Certificate[i];
/* 258 */         System.arraycopy(paramArrayOfX509Certificate, 0, arrayOfX509Certificate, 0, i);
/* 259 */         return doValidate(arrayOfX509Certificate, localPKIXExtendedParameters);
/*     */       }
/* 261 */       localObject1 = ((X509Certificate)localObject2).getIssuerX500Principal();
/*     */     }
/*     */     
/*     */ 
/* 265 */     X509Certificate localX509Certificate = paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)];
/* 266 */     Object localObject2 = localX509Certificate.getIssuerX500Principal();
/* 267 */     X500Principal localX500Principal = localX509Certificate.getSubjectX500Principal();
/* 268 */     if ((this.trustedSubjects.containsKey(localObject2)) && 
/* 269 */       (isSignatureValid((List)this.trustedSubjects.get(localObject2), localX509Certificate))) {
/* 270 */       return doValidate(paramArrayOfX509Certificate, localPKIXExtendedParameters);
/*     */     }
/*     */     
/*     */ 
/* 274 */     if (this.plugin)
/*     */     {
/*     */ 
/*     */ 
/* 278 */       if (paramArrayOfX509Certificate.length > 1) {
/* 279 */         arrayOfX509Certificate = new X509Certificate[paramArrayOfX509Certificate.length - 1];
/*     */         
/* 281 */         System.arraycopy(paramArrayOfX509Certificate, 0, arrayOfX509Certificate, 0, arrayOfX509Certificate.length);
/*     */         
/*     */ 
/*     */         try
/*     */         {
/* 286 */           localPKIXExtendedParameters.setTrustAnchors(Collections.singleton(new TrustAnchor(paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)], null)));
/*     */         }
/*     */         catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException2)
/*     */         {
/* 290 */           throw new CertificateException(localInvalidAlgorithmParameterException2);
/*     */         }
/* 292 */         doValidate(arrayOfX509Certificate, localPKIXExtendedParameters);
/*     */       }
/*     */       
/*     */ 
/* 296 */       throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 302 */     return doBuild(paramArrayOfX509Certificate, paramCollection, localPKIXExtendedParameters);
/*     */   }
/*     */   
/*     */   private boolean isSignatureValid(List<PublicKey> paramList, X509Certificate paramX509Certificate)
/*     */   {
/* 307 */     if (this.plugin) {
/* 308 */       for (PublicKey localPublicKey : paramList) {
/*     */         try {
/* 310 */           paramX509Certificate.verify(localPublicKey);
/* 311 */           return true;
/*     */         }
/*     */         catch (Exception localException) {}
/*     */       }
/*     */       
/* 316 */       return false;
/*     */     }
/* 318 */     return true;
/*     */   }
/*     */   
/*     */   private static X509Certificate[] toArray(CertPath paramCertPath, TrustAnchor paramTrustAnchor)
/*     */     throws CertificateException
/*     */   {
/* 324 */     List localList = paramCertPath.getCertificates();
/* 325 */     X509Certificate[] arrayOfX509Certificate = new X509Certificate[localList.size() + 1];
/* 326 */     localList.toArray(arrayOfX509Certificate);
/* 327 */     X509Certificate localX509Certificate = paramTrustAnchor.getTrustedCert();
/* 328 */     if (localX509Certificate == null) {
/* 329 */       throw new ValidatorException("TrustAnchor must be specified as certificate");
/*     */     }
/*     */     
/* 332 */     arrayOfX509Certificate[(arrayOfX509Certificate.length - 1)] = localX509Certificate;
/* 333 */     return arrayOfX509Certificate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setDate(PKIXBuilderParameters paramPKIXBuilderParameters)
/*     */   {
/* 341 */     Date localDate = this.validationDate;
/* 342 */     if (localDate != null) {
/* 343 */       paramPKIXBuilderParameters.setDate(localDate);
/*     */     }
/*     */   }
/*     */   
/*     */   private X509Certificate[] doValidate(X509Certificate[] paramArrayOfX509Certificate, PKIXBuilderParameters paramPKIXBuilderParameters) throws CertificateException
/*     */   {
/*     */     try {
/* 350 */       setDate(paramPKIXBuilderParameters);
/*     */       
/*     */ 
/* 353 */       CertPathValidator localCertPathValidator = CertPathValidator.getInstance("PKIX");
/* 354 */       CertPath localCertPath = this.factory.generateCertPath(Arrays.asList(paramArrayOfX509Certificate));
/* 355 */       this.certPathLength = paramArrayOfX509Certificate.length;
/*     */       
/* 357 */       PKIXCertPathValidatorResult localPKIXCertPathValidatorResult = (PKIXCertPathValidatorResult)localCertPathValidator.validate(localCertPath, paramPKIXBuilderParameters);
/*     */       
/* 359 */       return toArray(localCertPath, localPKIXCertPathValidatorResult.getTrustAnchor());
/*     */     }
/*     */     catch (GeneralSecurityException localGeneralSecurityException) {
/* 362 */       throw new ValidatorException("PKIX path validation failed: " + localGeneralSecurityException.toString(), localGeneralSecurityException);
/*     */     }
/*     */   }
/*     */   
/*     */   private X509Certificate[] doBuild(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, PKIXBuilderParameters paramPKIXBuilderParameters)
/*     */     throws CertificateException
/*     */   {
/*     */     try
/*     */     {
/* 371 */       setDate(paramPKIXBuilderParameters);
/*     */       
/*     */ 
/* 374 */       X509CertSelector localX509CertSelector = new X509CertSelector();
/* 375 */       localX509CertSelector.setCertificate(paramArrayOfX509Certificate[0]);
/* 376 */       paramPKIXBuilderParameters.setTargetCertConstraints(localX509CertSelector);
/*     */       
/*     */ 
/* 379 */       ArrayList localArrayList = new ArrayList();
/*     */       
/* 381 */       localArrayList.addAll(Arrays.asList(paramArrayOfX509Certificate));
/* 382 */       if (paramCollection != null) {
/* 383 */         localArrayList.addAll(paramCollection);
/*     */       }
/* 385 */       CertStore localCertStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(localArrayList));
/*     */       
/* 387 */       paramPKIXBuilderParameters.addCertStore(localCertStore);
/*     */       
/*     */ 
/* 390 */       CertPathBuilder localCertPathBuilder = CertPathBuilder.getInstance("PKIX");
/*     */       
/* 392 */       PKIXCertPathBuilderResult localPKIXCertPathBuilderResult = (PKIXCertPathBuilderResult)localCertPathBuilder.build(paramPKIXBuilderParameters);
/*     */       
/* 394 */       return toArray(localPKIXCertPathBuilderResult.getCertPath(), localPKIXCertPathBuilderResult.getTrustAnchor());
/*     */     }
/*     */     catch (GeneralSecurityException localGeneralSecurityException) {
/* 397 */       throw new ValidatorException("PKIX path building failed: " + localGeneralSecurityException.toString(), localGeneralSecurityException);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\validator\PKIXValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
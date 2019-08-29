/*      */ package sun.security.provider.certpath;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.security.InvalidAlgorithmParameterException;
/*      */ import java.security.PublicKey;
/*      */ import java.security.Security;
/*      */ import java.security.cert.CRLException;
/*      */ import java.security.cert.CRLReason;
/*      */ import java.security.cert.CertPathValidatorException;
/*      */ import java.security.cert.CertPathValidatorException.BasicReason;
/*      */ import java.security.cert.CertStore;
/*      */ import java.security.cert.CertStoreException;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateRevokedException;
/*      */ import java.security.cert.Extension;
/*      */ import java.security.cert.PKIXRevocationChecker;
/*      */ import java.security.cert.PKIXRevocationChecker.Option;
/*      */ import java.security.cert.TrustAnchor;
/*      */ import java.security.cert.X509CRL;
/*      */ import java.security.cert.X509CRLEntry;
/*      */ import java.security.cert.X509CRLSelector;
/*      */ import java.security.cert.X509CertSelector;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.security.util.Debug;
/*      */ import sun.security.util.ObjectIdentifier;
/*      */ import sun.security.x509.CRLDistributionPointsExtension;
/*      */ import sun.security.x509.DistributionPoint;
/*      */ import sun.security.x509.GeneralNames;
/*      */ import sun.security.x509.PKIXExtensions;
/*      */ import sun.security.x509.X500Name;
/*      */ import sun.security.x509.X509CRLEntryImpl;
/*      */ import sun.security.x509.X509CertImpl;
/*      */ 
/*      */ class RevocationChecker extends PKIXRevocationChecker
/*      */ {
/*   52 */   private static final Debug debug = Debug.getInstance("certpath");
/*      */   
/*      */   private TrustAnchor anchor;
/*      */   private PKIX.ValidatorParams params;
/*      */   private boolean onlyEE;
/*      */   private boolean softFail;
/*      */   private boolean crlDP;
/*      */   private URI responderURI;
/*      */   private X509Certificate responderCert;
/*      */   private List<CertStore> certStores;
/*      */   private Map<X509Certificate, byte[]> ocspResponses;
/*      */   private List<Extension> ocspExtensions;
/*      */   private final boolean legacy;
/*   65 */   private LinkedList<CertPathValidatorException> softFailExceptions = new LinkedList();
/*      */   
/*      */   private OCSPResponse.IssuerInfo issuerInfo;
/*      */   private PublicKey prevPubKey;
/*      */   private boolean crlSignFlag;
/*      */   private int certIndex;
/*      */   
/*      */   private static enum Mode
/*      */   {
/*   74 */     PREFER_OCSP,  PREFER_CRLS,  ONLY_CRLS,  ONLY_OCSP;
/*   75 */     private Mode() {} } private Mode mode = Mode.PREFER_OCSP;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final long MAX_CLOCK_SKEW = 900000L;
/*      */   
/*      */ 
/*      */   private static final String HEX_DIGITS = "0123456789ABCDEFabcdef";
/*      */   
/*      */ 
/*      */ 
/*      */   RevocationChecker()
/*      */   {
/*   88 */     this.legacy = false;
/*      */   }
/*      */   
/*      */   RevocationChecker(TrustAnchor paramTrustAnchor, PKIX.ValidatorParams paramValidatorParams)
/*      */     throws CertPathValidatorException
/*      */   {
/*   94 */     this.legacy = true;
/*   95 */     init(paramTrustAnchor, paramValidatorParams);
/*      */   }
/*      */   
/*      */   void init(TrustAnchor paramTrustAnchor, PKIX.ValidatorParams paramValidatorParams)
/*      */     throws CertPathValidatorException
/*      */   {
/*  101 */     RevocationProperties localRevocationProperties = getRevocationProperties();
/*  102 */     URI localURI = getOcspResponder();
/*  103 */     this.responderURI = (localURI == null ? toURI(localRevocationProperties.ocspUrl) : localURI);
/*  104 */     X509Certificate localX509Certificate = getOcspResponderCert();
/*      */     
/*  106 */     this.responderCert = (localX509Certificate == null ? getResponderCert(localRevocationProperties, paramValidatorParams.trustAnchors(), paramValidatorParams
/*  107 */       .certStores()) : localX509Certificate);
/*      */     
/*  109 */     Set localSet = getOptions();
/*  110 */     for (Option localOption : localSet) {
/*  111 */       switch (localOption) {
/*      */       case ONLY_END_ENTITY: 
/*      */       case PREFER_CRLS: 
/*      */       case SOFT_FAIL: 
/*      */       case NO_FALLBACK: 
/*      */         break;
/*      */       default: 
/*  118 */         throw new CertPathValidatorException("Unrecognized revocation parameter option: " + localOption);
/*      */       }
/*      */       
/*      */     }
/*  122 */     this.softFail = localSet.contains(Option.SOFT_FAIL);
/*      */     
/*      */ 
/*  125 */     if (this.legacy) {
/*  126 */       this.mode = (localRevocationProperties.ocspEnabled ? Mode.PREFER_OCSP : Mode.ONLY_CRLS);
/*  127 */       this.onlyEE = localRevocationProperties.onlyEE;
/*      */     } else {
/*  129 */       if (localSet.contains(Option.NO_FALLBACK)) {
/*  130 */         if (localSet.contains(Option.PREFER_CRLS)) {
/*  131 */           this.mode = Mode.ONLY_CRLS;
/*      */         } else {
/*  133 */           this.mode = Mode.ONLY_OCSP;
/*      */         }
/*  135 */       } else if (localSet.contains(Option.PREFER_CRLS)) {
/*  136 */         this.mode = Mode.PREFER_CRLS;
/*      */       }
/*  138 */       this.onlyEE = localSet.contains(Option.ONLY_END_ENTITY);
/*      */     }
/*  140 */     if (this.legacy) {
/*  141 */       this.crlDP = localRevocationProperties.crlDPEnabled;
/*      */     } else {
/*  143 */       this.crlDP = true;
/*      */     }
/*  145 */     this.ocspResponses = getOcspResponses();
/*  146 */     this.ocspExtensions = getOcspExtensions();
/*      */     
/*  148 */     this.anchor = paramTrustAnchor;
/*  149 */     this.params = paramValidatorParams;
/*  150 */     this.certStores = new ArrayList(paramValidatorParams.certStores());
/*      */     try {
/*  152 */       this.certStores.add(CertStore.getInstance("Collection", new java.security.cert.CollectionCertStoreParameters(paramValidatorParams
/*  153 */         .certificates())));
/*      */ 
/*      */     }
/*      */     catch (InvalidAlgorithmParameterException|java.security.NoSuchAlgorithmException localInvalidAlgorithmParameterException)
/*      */     {
/*  158 */       if (debug != null) {
/*  159 */         debug.println("RevocationChecker: error creating Collection CertStore: " + localInvalidAlgorithmParameterException);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static URI toURI(String paramString)
/*      */     throws CertPathValidatorException
/*      */   {
/*      */     try
/*      */     {
/*  169 */       if (paramString != null) {
/*  170 */         return new URI(paramString);
/*      */       }
/*  172 */       return null;
/*      */     } catch (URISyntaxException localURISyntaxException) {
/*  174 */       throw new CertPathValidatorException("cannot parse ocsp.responderURL property", localURISyntaxException);
/*      */     }
/*      */   }
/*      */   
/*      */   private static RevocationProperties getRevocationProperties()
/*      */   {
/*  180 */     (RevocationProperties)java.security.AccessController.doPrivileged(new java.security.PrivilegedAction()
/*      */     {
/*      */       public RevocationProperties run() {
/*  183 */         RevocationProperties localRevocationProperties = new RevocationProperties(null);
/*  184 */         String str1 = Security.getProperty("com.sun.security.onlyCheckRevocationOfEECert");
/*      */         
/*      */ 
/*  187 */         localRevocationProperties.onlyEE = ((str1 != null) && (str1.equalsIgnoreCase("true")));
/*  188 */         String str2 = Security.getProperty("ocsp.enable");
/*      */         
/*  190 */         localRevocationProperties.ocspEnabled = ((str2 != null) && (str2.equalsIgnoreCase("true")));
/*  191 */         localRevocationProperties.ocspUrl = Security.getProperty("ocsp.responderURL");
/*      */         
/*  193 */         localRevocationProperties.ocspSubject = Security.getProperty("ocsp.responderCertSubjectName");
/*      */         
/*  195 */         localRevocationProperties.ocspIssuer = Security.getProperty("ocsp.responderCertIssuerName");
/*      */         
/*  197 */         localRevocationProperties.ocspSerial = Security.getProperty("ocsp.responderCertSerialNumber");
/*      */         
/*  199 */         localRevocationProperties.crlDPEnabled = Boolean.getBoolean("com.sun.security.enableCRLDP");
/*  200 */         return localRevocationProperties;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static X509Certificate getResponderCert(RevocationProperties paramRevocationProperties, Set<TrustAnchor> paramSet, List<CertStore> paramList)
/*      */     throws CertPathValidatorException
/*      */   {
/*  211 */     if (paramRevocationProperties.ocspSubject != null)
/*  212 */       return getResponderCert(paramRevocationProperties.ocspSubject, paramSet, paramList);
/*  213 */     if ((paramRevocationProperties.ocspIssuer != null) && (paramRevocationProperties.ocspSerial != null)) {
/*  214 */       return getResponderCert(paramRevocationProperties.ocspIssuer, paramRevocationProperties.ocspSerial, paramSet, paramList);
/*      */     }
/*  216 */     if ((paramRevocationProperties.ocspIssuer != null) || (paramRevocationProperties.ocspSerial != null)) {
/*  217 */       throw new CertPathValidatorException("Must specify both ocsp.responderCertIssuerName and ocsp.responderCertSerialNumber properties");
/*      */     }
/*      */     
/*      */ 
/*  221 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static X509Certificate getResponderCert(String paramString, Set<TrustAnchor> paramSet, List<CertStore> paramList)
/*      */     throws CertPathValidatorException
/*      */   {
/*  229 */     X509CertSelector localX509CertSelector = new X509CertSelector();
/*      */     try {
/*  231 */       localX509CertSelector.setSubject(new X500Principal(paramString));
/*      */     } catch (IllegalArgumentException localIllegalArgumentException) {
/*  233 */       throw new CertPathValidatorException("cannot parse ocsp.responderCertSubjectName property", localIllegalArgumentException);
/*      */     }
/*      */     
/*  236 */     return getResponderCert(localX509CertSelector, paramSet, paramList);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static X509Certificate getResponderCert(String paramString1, String paramString2, Set<TrustAnchor> paramSet, List<CertStore> paramList)
/*      */     throws CertPathValidatorException
/*      */   {
/*  245 */     X509CertSelector localX509CertSelector = new X509CertSelector();
/*      */     try {
/*  247 */       localX509CertSelector.setIssuer(new X500Principal(paramString1));
/*      */     } catch (IllegalArgumentException localIllegalArgumentException) {
/*  249 */       throw new CertPathValidatorException("cannot parse ocsp.responderCertIssuerName property", localIllegalArgumentException);
/*      */     }
/*      */     try
/*      */     {
/*  253 */       localX509CertSelector.setSerialNumber(new BigInteger(stripOutSeparators(paramString2), 16));
/*      */     } catch (NumberFormatException localNumberFormatException) {
/*  255 */       throw new CertPathValidatorException("cannot parse ocsp.responderCertSerialNumber property", localNumberFormatException);
/*      */     }
/*      */     
/*  258 */     return getResponderCert(localX509CertSelector, paramSet, paramList);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static X509Certificate getResponderCert(X509CertSelector paramX509CertSelector, Set<TrustAnchor> paramSet, List<CertStore> paramList)
/*      */     throws CertPathValidatorException
/*      */   {
/*  267 */     for (Iterator localIterator = paramSet.iterator(); localIterator.hasNext();) { localObject1 = (TrustAnchor)localIterator.next();
/*  268 */       localObject2 = ((TrustAnchor)localObject1).getTrustedCert();
/*  269 */       if (localObject2 != null)
/*      */       {
/*      */ 
/*  272 */         if (paramX509CertSelector.match((Certificate)localObject2))
/*  273 */           return (X509Certificate)localObject2; }
/*      */     }
/*      */     Object localObject1;
/*      */     Object localObject2;
/*  277 */     for (localIterator = paramList.iterator(); localIterator.hasNext();) { localObject1 = (CertStore)localIterator.next();
/*      */       try
/*      */       {
/*  280 */         localObject2 = ((CertStore)localObject1).getCertificates(paramX509CertSelector);
/*  281 */         if (!((Collection)localObject2).isEmpty()) {
/*  282 */           return (X509Certificate)((Collection)localObject2).iterator().next();
/*      */         }
/*      */       }
/*      */       catch (CertStoreException localCertStoreException) {
/*  286 */         if (debug != null) {
/*  287 */           debug.println("CertStore exception:" + localCertStoreException);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  292 */     throw new CertPathValidatorException("Cannot find the responder's certificate (set using the OCSP security properties).");
/*      */   }
/*      */   
/*      */ 
/*      */   public void init(boolean paramBoolean)
/*      */     throws CertPathValidatorException
/*      */   {
/*  299 */     if (paramBoolean) {
/*  300 */       throw new CertPathValidatorException("forward checking not supported");
/*      */     }
/*      */     
/*  303 */     if (this.anchor != null) {
/*  304 */       this.issuerInfo = new OCSPResponse.IssuerInfo(this.anchor);
/*  305 */       this.prevPubKey = this.issuerInfo.getPublicKey();
/*      */     }
/*      */     
/*  308 */     this.crlSignFlag = true;
/*  309 */     if ((this.params != null) && (this.params.certPath() != null)) {
/*  310 */       this.certIndex = (this.params.certPath().getCertificates().size() - 1);
/*      */     } else {
/*  312 */       this.certIndex = -1;
/*      */     }
/*  314 */     this.softFailExceptions.clear();
/*      */   }
/*      */   
/*      */   public boolean isForwardCheckingSupported()
/*      */   {
/*  319 */     return false;
/*      */   }
/*      */   
/*      */   public Set<String> getSupportedExtensions()
/*      */   {
/*  324 */     return null;
/*      */   }
/*      */   
/*      */   public List<CertPathValidatorException> getSoftFailExceptions()
/*      */   {
/*  329 */     return Collections.unmodifiableList(this.softFailExceptions);
/*      */   }
/*      */   
/*      */ 
/*      */   public void check(Certificate paramCertificate, Collection<String> paramCollection)
/*      */     throws CertPathValidatorException
/*      */   {
/*  336 */     check((X509Certificate)paramCertificate, paramCollection, this.prevPubKey, this.crlSignFlag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void check(X509Certificate paramX509Certificate, Collection<String> paramCollection, PublicKey paramPublicKey, boolean paramBoolean)
/*      */     throws CertPathValidatorException
/*      */   {
/*  345 */     if (debug != null) {
/*  346 */       debug.println("RevocationChecker.check: checking cert\n  SN: " + 
/*  347 */         Debug.toHexString(paramX509Certificate.getSerialNumber()) + "\n  Subject: " + paramX509Certificate
/*  348 */         .getSubjectX500Principal() + "\n  Issuer: " + paramX509Certificate
/*  349 */         .getIssuerX500Principal());
/*      */     }
/*      */     try {
/*  352 */       if ((this.onlyEE) && (paramX509Certificate.getBasicConstraints() != -1)) {
/*  353 */         if (debug != null) {
/*  354 */           debug.println("Skipping revocation check; cert is not an end entity cert");
/*      */         }
/*      */         
/*  357 */         return;
/*      */       }
/*  359 */       switch (this.mode) {
/*      */       case PREFER_OCSP: 
/*      */       case ONLY_OCSP: 
/*  362 */         checkOCSP(paramX509Certificate, paramCollection);
/*  363 */         break;
/*      */       case PREFER_CRLS: 
/*      */       case ONLY_CRLS: 
/*  366 */         checkCRLs(paramX509Certificate, paramCollection, null, paramPublicKey, paramBoolean);
/*      */       }
/*      */     }
/*      */     catch (CertPathValidatorException localCertPathValidatorException1)
/*      */     {
/*  371 */       if (localCertPathValidatorException1.getReason() == BasicReason.REVOKED) {
/*  372 */         throw localCertPathValidatorException1;
/*      */       }
/*  374 */       boolean bool = isSoftFailException(localCertPathValidatorException1);
/*  375 */       if (bool) {
/*  376 */         if ((this.mode != Mode.ONLY_OCSP) && (this.mode != Mode.ONLY_CRLS)) {}
/*      */ 
/*      */ 
/*      */       }
/*  380 */       else if ((this.mode == Mode.ONLY_OCSP) || (this.mode == Mode.ONLY_CRLS)) {
/*  381 */         throw localCertPathValidatorException1;
/*      */       }
/*      */       
/*  384 */       CertPathValidatorException localCertPathValidatorException2 = localCertPathValidatorException1;
/*      */       
/*  386 */       if (debug != null) {
/*  387 */         debug.println("RevocationChecker.check() " + localCertPathValidatorException1.getMessage());
/*  388 */         debug.println("RevocationChecker.check() preparing to failover");
/*      */       }
/*      */       try {
/*  391 */         switch (this.mode) {
/*      */         case PREFER_OCSP: 
/*  393 */           checkCRLs(paramX509Certificate, paramCollection, null, paramPublicKey, paramBoolean);
/*      */           
/*  395 */           break;
/*      */         case PREFER_CRLS: 
/*  397 */           checkOCSP(paramX509Certificate, paramCollection);
/*      */         }
/*      */       }
/*      */       catch (CertPathValidatorException localCertPathValidatorException3) {
/*  401 */         if (debug != null) {
/*  402 */           debug.println("RevocationChecker.check() failover failed");
/*  403 */           debug.println("RevocationChecker.check() " + localCertPathValidatorException3.getMessage());
/*      */         }
/*  405 */         if (localCertPathValidatorException3.getReason() == BasicReason.REVOKED) {
/*  406 */           throw localCertPathValidatorException3;
/*      */         }
/*  408 */         if (!isSoftFailException(localCertPathValidatorException3)) {
/*  409 */           localCertPathValidatorException2.addSuppressed(localCertPathValidatorException3);
/*  410 */           throw localCertPathValidatorException2;
/*      */         }
/*      */         
/*  413 */         if (!bool) {
/*  414 */           throw localCertPathValidatorException2;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  419 */       updateState(paramX509Certificate);
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean isSoftFailException(CertPathValidatorException paramCertPathValidatorException) {
/*  424 */     if ((this.softFail) && 
/*  425 */       (paramCertPathValidatorException.getReason() == BasicReason.UNDETERMINED_REVOCATION_STATUS))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  430 */       CertPathValidatorException localCertPathValidatorException = new CertPathValidatorException(paramCertPathValidatorException.getMessage(), paramCertPathValidatorException.getCause(), this.params.certPath(), this.certIndex, paramCertPathValidatorException.getReason());
/*  431 */       this.softFailExceptions.addFirst(localCertPathValidatorException);
/*  432 */       return true;
/*      */     }
/*  434 */     return false;
/*      */   }
/*      */   
/*      */   private void updateState(X509Certificate paramX509Certificate)
/*      */     throws CertPathValidatorException
/*      */   {
/*  440 */     this.issuerInfo = new OCSPResponse.IssuerInfo(this.anchor, paramX509Certificate);
/*      */     
/*      */ 
/*  443 */     PublicKey localPublicKey = paramX509Certificate.getPublicKey();
/*  444 */     if (PKIX.isDSAPublicKeyWithoutParams(localPublicKey))
/*      */     {
/*  446 */       localPublicKey = BasicChecker.makeInheritedParamsKey(localPublicKey, this.prevPubKey);
/*      */     }
/*  448 */     this.prevPubKey = localPublicKey;
/*  449 */     this.crlSignFlag = certCanSignCrl(paramX509Certificate);
/*  450 */     if (this.certIndex > 0) {
/*  451 */       this.certIndex -= 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkCRLs(X509Certificate paramX509Certificate, Collection<String> paramCollection, Set<X509Certificate> paramSet, PublicKey paramPublicKey, boolean paramBoolean)
/*      */     throws CertPathValidatorException
/*      */   {
/*  464 */     checkCRLs(paramX509Certificate, paramPublicKey, null, paramBoolean, true, paramSet, this.params
/*  465 */       .trustAnchors());
/*      */   }
/*      */   
/*      */   static boolean isCausedByNetworkIssue(String paramString, CertStoreException paramCertStoreException)
/*      */   {
/*  470 */     Throwable localThrowable = paramCertStoreException.getCause();
/*      */     boolean bool;
/*  472 */     switch (paramString) {
/*      */     case "LDAP": 
/*  474 */       if (localThrowable != null)
/*      */       {
/*  476 */         String str2 = localThrowable.getClass().getName();
/*      */         
/*  478 */         bool = (str2.equals("javax.naming.ServiceUnavailableException")) || (str2.equals("javax.naming.CommunicationException"));
/*      */       } else {
/*  480 */         bool = false;
/*      */       }
/*  482 */       break;
/*      */     case "SSLServer": 
/*  484 */       bool = (localThrowable != null) && ((localThrowable instanceof IOException));
/*  485 */       break;
/*      */     case "URI": 
/*  487 */       bool = (localThrowable != null) && ((localThrowable instanceof IOException));
/*  488 */       break;
/*      */     
/*      */     default: 
/*  491 */       return false;
/*      */     }
/*  493 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkCRLs(X509Certificate paramX509Certificate1, PublicKey paramPublicKey, X509Certificate paramX509Certificate2, boolean paramBoolean1, boolean paramBoolean2, Set<X509Certificate> paramSet, Set<TrustAnchor> paramSet1)
/*      */     throws CertPathValidatorException
/*      */   {
/*  503 */     if (debug != null) {
/*  504 */       debug.println("RevocationChecker.checkCRLs() ---checking revocation status ...");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  511 */     if ((paramSet != null) && (paramSet.contains(paramX509Certificate1))) {
/*  512 */       if (debug != null) {
/*  513 */         debug.println("RevocationChecker.checkCRLs() circular dependency");
/*      */       }
/*      */       
/*  516 */       throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  521 */     HashSet localHashSet1 = new HashSet();
/*  522 */     HashSet localHashSet2 = new HashSet();
/*  523 */     X509CRLSelector localX509CRLSelector = new X509CRLSelector();
/*  524 */     localX509CRLSelector.setCertificateChecking(paramX509Certificate1);
/*  525 */     CertPathHelper.setDateAndTime(localX509CRLSelector, this.params.date(), 900000L);
/*      */     
/*      */ 
/*  528 */     CertPathValidatorException localCertPathValidatorException1 = null;
/*  529 */     for (Object localObject = this.certStores.iterator(); ((Iterator)localObject).hasNext();) { CertStore localCertStore = (CertStore)((Iterator)localObject).next();
/*      */       try {
/*  531 */         for (java.security.cert.CRL localCRL : localCertStore.getCRLs(localX509CRLSelector)) {
/*  532 */           localHashSet1.add((X509CRL)localCRL);
/*      */         }
/*      */       } catch (CertStoreException localCertStoreException2) {
/*  535 */         if (debug != null) {
/*  536 */           debug.println("RevocationChecker.checkCRLs() CertStoreException: " + localCertStoreException2
/*  537 */             .getMessage());
/*      */         }
/*  539 */         if ((localCertPathValidatorException1 == null) && 
/*  540 */           (isCausedByNetworkIssue(localCertStore.getType(), localCertStoreException2)))
/*      */         {
/*  542 */           localCertPathValidatorException1 = new CertPathValidatorException("Unable to determine revocation status due to network error", localCertStoreException2, null, -1, BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  550 */     if (debug != null) {
/*  551 */       debug.println("RevocationChecker.checkCRLs() possible crls.size() = " + localHashSet1
/*  552 */         .size());
/*      */     }
/*  554 */     localObject = new boolean[9];
/*  555 */     if (!localHashSet1.isEmpty())
/*      */     {
/*      */ 
/*  558 */       localHashSet2.addAll(verifyPossibleCRLs(localHashSet1, paramX509Certificate1, paramPublicKey, paramBoolean1, (boolean[])localObject, paramSet1));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  563 */     if (debug != null) {
/*  564 */       debug.println("RevocationChecker.checkCRLs() approved crls.size() = " + localHashSet2
/*  565 */         .size());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  570 */     if ((!localHashSet2.isEmpty()) && 
/*  571 */       (Arrays.equals((boolean[])localObject, ALL_REASONS)))
/*      */     {
/*  573 */       checkApprovedCRLs(paramX509Certificate1, localHashSet2);
/*      */     }
/*      */     else
/*      */     {
/*      */       try {
/*  578 */         if (this.crlDP) {
/*  579 */           localHashSet2.addAll(DistributionPointFetcher.getCRLs(localX509CRLSelector, paramBoolean1, paramPublicKey, paramX509Certificate2, this.params
/*      */           
/*  581 */             .sigProvider(), this.certStores, (boolean[])localObject, paramSet1, null, this.params
/*  582 */             .variant()));
/*      */         }
/*      */       } catch (CertStoreException localCertStoreException1) {
/*  585 */         if ((localCertStoreException1 instanceof PKIX.CertStoreTypeException)) {
/*  586 */           PKIX.CertStoreTypeException localCertStoreTypeException = (PKIX.CertStoreTypeException)localCertStoreException1;
/*  587 */           if (isCausedByNetworkIssue(localCertStoreTypeException.getType(), localCertStoreException1)) {
/*  588 */             throw new CertPathValidatorException("Unable to determine revocation status due to network error", localCertStoreException1, null, -1, BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  594 */         throw new CertPathValidatorException(localCertStoreException1);
/*      */       }
/*  596 */       if ((!localHashSet2.isEmpty()) && 
/*  597 */         (Arrays.equals((boolean[])localObject, ALL_REASONS)))
/*      */       {
/*  599 */         checkApprovedCRLs(paramX509Certificate1, localHashSet2);
/*      */       } else {
/*  601 */         if (paramBoolean2) {
/*      */           try {
/*  603 */             verifyWithSeparateSigningKey(paramX509Certificate1, paramPublicKey, paramBoolean1, paramSet);
/*      */             
/*  605 */             return;
/*      */           } catch (CertPathValidatorException localCertPathValidatorException2) {
/*  607 */             if (localCertPathValidatorException1 != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  612 */               throw localCertPathValidatorException1;
/*      */             }
/*  614 */             throw localCertPathValidatorException2;
/*      */           }
/*      */         }
/*  617 */         if (localCertPathValidatorException1 != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  622 */           throw localCertPathValidatorException1;
/*      */         }
/*  624 */         throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkApprovedCRLs(X509Certificate paramX509Certificate, Set<X509CRL> paramSet)
/*      */     throws CertPathValidatorException
/*      */   {
/*  637 */     if (debug != null) {
/*  638 */       localObject = paramX509Certificate.getSerialNumber();
/*  639 */       debug.println("RevocationChecker.checkApprovedCRLs() starting the final sweep...");
/*      */       
/*  641 */       debug.println("RevocationChecker.checkApprovedCRLs() cert SN: " + ((BigInteger)localObject)
/*  642 */         .toString());
/*      */     }
/*      */     
/*  645 */     Object localObject = CRLReason.UNSPECIFIED;
/*  646 */     X509CRLEntryImpl localX509CRLEntryImpl = null;
/*  647 */     for (X509CRL localX509CRL : paramSet) {
/*  648 */       X509CRLEntry localX509CRLEntry = localX509CRL.getRevokedCertificate(paramX509Certificate);
/*  649 */       if (localX509CRLEntry != null) {
/*      */         try {
/*  651 */           localX509CRLEntryImpl = X509CRLEntryImpl.toImpl(localX509CRLEntry);
/*      */         } catch (CRLException localCRLException) {
/*  653 */           throw new CertPathValidatorException(localCRLException);
/*      */         }
/*  655 */         if (debug != null) {
/*  656 */           debug.println("RevocationChecker.checkApprovedCRLs() CRL entry: " + localX509CRLEntryImpl
/*  657 */             .toString());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  665 */         Set localSet = localX509CRLEntryImpl.getCriticalExtensionOIDs();
/*  666 */         if ((localSet != null) && (!localSet.isEmpty()))
/*      */         {
/*  668 */           localSet.remove(PKIXExtensions.ReasonCode_Id.toString());
/*  669 */           localSet.remove(PKIXExtensions.CertificateIssuer_Id.toString());
/*  670 */           if (!localSet.isEmpty()) {
/*  671 */             throw new CertPathValidatorException("Unrecognized critical extension(s) in revoked CRL entry");
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  677 */         localObject = localX509CRLEntryImpl.getRevocationReason();
/*  678 */         if (localObject == null) {
/*  679 */           localObject = CRLReason.UNSPECIFIED;
/*      */         }
/*  681 */         Date localDate = localX509CRLEntryImpl.getRevocationDate();
/*  682 */         if (localDate.before(this.params.date()))
/*      */         {
/*      */ 
/*  685 */           CertificateRevokedException localCertificateRevokedException = new CertificateRevokedException(localDate, (CRLReason)localObject, localX509CRL.getIssuerX500Principal(), localX509CRLEntryImpl.getExtensions());
/*      */           
/*  687 */           throw new CertPathValidatorException(localCertificateRevokedException.getMessage(), localCertificateRevokedException, null, -1, BasicReason.REVOKED);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkOCSP(X509Certificate paramX509Certificate, Collection<String> paramCollection)
/*      */     throws CertPathValidatorException
/*      */   {
/*  697 */     X509CertImpl localX509CertImpl = null;
/*      */     try {
/*  699 */       localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
/*      */     } catch (CertificateException localCertificateException) {
/*  701 */       throw new CertPathValidatorException(localCertificateException);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  708 */     OCSPResponse localOCSPResponse = null;
/*  709 */     CertId localCertId = null;
/*      */     Object localObject2;
/*      */     Object localObject3;
/*  712 */     try { localCertId = new CertId(this.issuerInfo.getName(), this.issuerInfo.getPublicKey(), localX509CertImpl.getSerialNumberObject());
/*      */       
/*      */ 
/*  715 */       byte[] arrayOfByte = (byte[])this.ocspResponses.get(paramX509Certificate);
/*  716 */       if (arrayOfByte != null) {
/*  717 */         if (debug != null) {
/*  718 */           debug.println("Found cached OCSP response");
/*      */         }
/*  720 */         localOCSPResponse = new OCSPResponse(arrayOfByte);
/*      */         
/*      */ 
/*  723 */         localObject1 = null;
/*  724 */         for (localObject2 = this.ocspExtensions.iterator(); ((Iterator)localObject2).hasNext();) { localObject3 = (Extension)((Iterator)localObject2).next();
/*  725 */           if (((Extension)localObject3).getId().equals("1.3.6.1.5.5.7.48.1.2")) {
/*  726 */             localObject1 = ((Extension)localObject3).getValue();
/*      */           }
/*      */         }
/*  729 */         localOCSPResponse.verify(Collections.singletonList(localCertId), this.issuerInfo, this.responderCert, this.params
/*  730 */           .date(), (byte[])localObject1, this.params.variant());
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  735 */         localObject1 = this.responderURI != null ? this.responderURI : OCSP.getResponderURI(localX509CertImpl);
/*  736 */         if (localObject1 == null) {
/*  737 */           throw new CertPathValidatorException("Certificate does not specify OCSP responder", null, null, -1);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  742 */         localOCSPResponse = OCSP.check(Collections.singletonList(localCertId), (URI)localObject1, this.issuerInfo, this.responderCert, null, this.ocspExtensions, this.params
/*      */         
/*  744 */           .variant());
/*      */       }
/*      */     } catch (IOException localIOException) {
/*  747 */       throw new CertPathValidatorException("Unable to determine revocation status due to network error", localIOException, null, -1, BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  753 */     OCSPResponse.SingleResponse localSingleResponse = localOCSPResponse.getSingleResponse(localCertId);
/*  754 */     Object localObject1 = localSingleResponse.getCertStatus();
/*  755 */     if (localObject1 == OCSP.RevocationStatus.CertStatus.REVOKED) {
/*  756 */       localObject2 = localSingleResponse.getRevocationTime();
/*  757 */       if (((Date)localObject2).before(this.params.date()))
/*      */       {
/*      */ 
/*      */ 
/*  761 */         localObject3 = new CertificateRevokedException((Date)localObject2, localSingleResponse.getRevocationReason(), localOCSPResponse.getSignerCertificate().getSubjectX500Principal(), localSingleResponse.getSingleExtensions());
/*  762 */         throw new CertPathValidatorException(((Throwable)localObject3).getMessage(), (Throwable)localObject3, null, -1, BasicReason.REVOKED);
/*      */       }
/*      */     }
/*  765 */     else if (localObject1 == OCSP.RevocationStatus.CertStatus.UNKNOWN)
/*      */     {
/*      */ 
/*  768 */       throw new CertPathValidatorException("Certificate's revocation status is unknown", null, this.params.certPath(), -1, BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String stripOutSeparators(String paramString)
/*      */   {
/*  778 */     char[] arrayOfChar = paramString.toCharArray();
/*  779 */     StringBuilder localStringBuilder = new StringBuilder();
/*  780 */     for (int i = 0; i < arrayOfChar.length; i++) {
/*  781 */       if ("0123456789ABCDEFabcdef".indexOf(arrayOfChar[i]) != -1) {
/*  782 */         localStringBuilder.append(arrayOfChar[i]);
/*      */       }
/*      */     }
/*  785 */     return localStringBuilder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static boolean certCanSignCrl(X509Certificate paramX509Certificate)
/*      */   {
/*  799 */     boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
/*  800 */     if (arrayOfBoolean != null) {
/*  801 */       return arrayOfBoolean[6];
/*      */     }
/*  803 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  818 */   private static final boolean[] ALL_REASONS = { true, true, true, true, true, true, true, true, true };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Collection<X509CRL> verifyPossibleCRLs(Set<X509CRL> paramSet, X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet1)
/*      */     throws CertPathValidatorException
/*      */   {
/*      */     try
/*      */     {
/*  829 */       X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
/*  830 */       if (debug != null) {
/*  831 */         debug.println("RevocationChecker.verifyPossibleCRLs: Checking CRLDPs for " + localX509CertImpl
/*      */         
/*  833 */           .getSubjectX500Principal());
/*      */       }
/*      */       
/*  836 */       CRLDistributionPointsExtension localCRLDistributionPointsExtension = localX509CertImpl.getCRLDistributionPointsExtension();
/*  837 */       List localList = null;
/*  838 */       if (localCRLDistributionPointsExtension == null)
/*      */       {
/*      */ 
/*      */ 
/*  842 */         localObject1 = (X500Name)localX509CertImpl.getIssuerDN();
/*      */         
/*  844 */         localObject2 = new DistributionPoint(new GeneralNames().add(new sun.security.x509.GeneralName((sun.security.x509.GeneralNameInterface)localObject1)), null, null);
/*      */         
/*  846 */         localList = Collections.singletonList(localObject2);
/*      */       } else {
/*  848 */         localList = localCRLDistributionPointsExtension.get("points");
/*      */       }
/*  850 */       Object localObject1 = new HashSet();
/*  851 */       for (Object localObject2 = localList.iterator(); ((Iterator)localObject2).hasNext();) { DistributionPoint localDistributionPoint = (DistributionPoint)((Iterator)localObject2).next();
/*  852 */         for (X509CRL localX509CRL : paramSet) {
/*  853 */           if (DistributionPointFetcher.verifyCRL(localX509CertImpl, localDistributionPoint, localX509CRL, paramArrayOfBoolean, paramBoolean, paramPublicKey, null, this.params
/*      */           
/*  855 */             .sigProvider(), paramSet1, this.certStores, this.params
/*  856 */             .date(), this.params.variant()))
/*      */           {
/*  858 */             ((Set)localObject1).add(localX509CRL);
/*      */           }
/*      */         }
/*  861 */         if (Arrays.equals(paramArrayOfBoolean, ALL_REASONS))
/*      */           break;
/*      */       }
/*  864 */       return (Collection<X509CRL>)localObject1;
/*      */     } catch (CertificateException|CRLException|IOException localCertificateException) {
/*  866 */       if (debug != null) {
/*  867 */         debug.println("Exception while verifying CRL: " + localCertificateException.getMessage());
/*  868 */         localCertificateException.printStackTrace();
/*      */       } }
/*  870 */     return Collections.emptySet();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void verifyWithSeparateSigningKey(X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean, Set<X509Certificate> paramSet)
/*      */     throws CertPathValidatorException
/*      */   {
/*  902 */     String str = "revocation status";
/*  903 */     if (debug != null) {
/*  904 */       debug.println("RevocationChecker.verifyWithSeparateSigningKey() ---checking " + str + "...");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  912 */     if ((paramSet != null) && (paramSet.contains(paramX509Certificate))) {
/*  913 */       if (debug != null) {
/*  914 */         debug.println("RevocationChecker.verifyWithSeparateSigningKey() circular dependency");
/*      */       }
/*      */       
/*      */ 
/*  918 */       throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  927 */     if (!paramBoolean) {
/*  928 */       buildToNewKey(paramX509Certificate, null, paramSet);
/*      */     } else {
/*  930 */       buildToNewKey(paramX509Certificate, paramPublicKey, paramSet);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  948 */   private static final boolean[] CRL_SIGN_USAGE = { false, false, false, false, false, false, true };
/*      */   
/*      */   /* Error */
/*      */   private void buildToNewKey(X509Certificate paramX509Certificate, PublicKey paramPublicKey, Set<X509Certificate> paramSet)
/*      */     throws CertPathValidatorException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   3: ifnull +12 -> 15
/*      */     //   6: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   9: ldc_w 403
/*      */     //   12: invokevirtual 1035	sun/security/util/Debug:println	(Ljava/lang/String;)V
/*      */     //   15: new 464	java/util/HashSet
/*      */     //   18: dup
/*      */     //   19: invokespecial 970	java/util/HashSet:<init>	()V
/*      */     //   22: astore 4
/*      */     //   24: aload_2
/*      */     //   25: ifnull +12 -> 37
/*      */     //   28: aload 4
/*      */     //   30: aload_2
/*      */     //   31: invokeinterface 1071 2 0
/*      */     //   36: pop
/*      */     //   37: new 489	sun/security/provider/certpath/RevocationChecker$RejectKeySelector
/*      */     //   40: dup
/*      */     //   41: aload 4
/*      */     //   43: invokespecial 1033	sun/security/provider/certpath/RevocationChecker$RejectKeySelector:<init>	(Ljava/util/Set;)V
/*      */     //   46: astore 5
/*      */     //   48: aload 5
/*      */     //   50: aload_1
/*      */     //   51: invokevirtual 961	java/security/cert/X509Certificate:getIssuerX500Principal	()Ljavax/security/auth/x500/X500Principal;
/*      */     //   54: invokevirtual 956	java/security/cert/X509CertSelector:setSubject	(Ljavax/security/auth/x500/X500Principal;)V
/*      */     //   57: aload 5
/*      */     //   59: getstatic 859	sun/security/provider/certpath/RevocationChecker:CRL_SIGN_USAGE	[Z
/*      */     //   62: invokevirtual 952	java/security/cert/X509CertSelector:setKeyUsage	([Z)V
/*      */     //   65: aload_0
/*      */     //   66: getfield 862	sun/security/provider/certpath/RevocationChecker:anchor	Ljava/security/cert/TrustAnchor;
/*      */     //   69: ifnonnull +13 -> 82
/*      */     //   72: aload_0
/*      */     //   73: getfield 869	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
/*      */     //   76: invokevirtual 1004	sun/security/provider/certpath/PKIX$ValidatorParams:trustAnchors	()Ljava/util/Set;
/*      */     //   79: goto +10 -> 89
/*      */     //   82: aload_0
/*      */     //   83: getfield 862	sun/security/provider/certpath/RevocationChecker:anchor	Ljava/security/cert/TrustAnchor;
/*      */     //   86: invokestatic 968	java/util/Collections:singleton	(Ljava/lang/Object;)Ljava/util/Set;
/*      */     //   89: astore 6
/*      */     //   91: new 448	java/security/cert/PKIXBuilderParameters
/*      */     //   94: dup
/*      */     //   95: aload 6
/*      */     //   97: aload 5
/*      */     //   99: invokespecial 937	java/security/cert/PKIXBuilderParameters:<init>	(Ljava/util/Set;Ljava/security/cert/CertSelector;)V
/*      */     //   102: astore 7
/*      */     //   104: goto +15 -> 119
/*      */     //   107: astore 8
/*      */     //   109: new 421	java/lang/RuntimeException
/*      */     //   112: dup
/*      */     //   113: aload 8
/*      */     //   115: invokespecial 891	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   118: athrow
/*      */     //   119: aload 7
/*      */     //   121: aload_0
/*      */     //   122: getfield 869	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
/*      */     //   125: invokevirtual 1003	sun/security/provider/certpath/PKIX$ValidatorParams:initialPolicies	()Ljava/util/Set;
/*      */     //   128: invokevirtual 936	java/security/cert/PKIXBuilderParameters:setInitialPolicies	(Ljava/util/Set;)V
/*      */     //   131: aload 7
/*      */     //   133: aload_0
/*      */     //   134: getfield 865	sun/security/provider/certpath/RevocationChecker:certStores	Ljava/util/List;
/*      */     //   137: invokevirtual 935	java/security/cert/PKIXBuilderParameters:setCertStores	(Ljava/util/List;)V
/*      */     //   140: aload 7
/*      */     //   142: aload_0
/*      */     //   143: getfield 869	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
/*      */     //   146: invokevirtual 994	sun/security/provider/certpath/PKIX$ValidatorParams:explicitPolicyRequired	()Z
/*      */     //   149: invokevirtual 928	java/security/cert/PKIXBuilderParameters:setExplicitPolicyRequired	(Z)V
/*      */     //   152: aload 7
/*      */     //   154: aload_0
/*      */     //   155: getfield 869	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
/*      */     //   158: invokevirtual 995	sun/security/provider/certpath/PKIX$ValidatorParams:policyMappingInhibited	()Z
/*      */     //   161: invokevirtual 929	java/security/cert/PKIXBuilderParameters:setPolicyMappingInhibited	(Z)V
/*      */     //   164: aload 7
/*      */     //   166: aload_0
/*      */     //   167: getfield 869	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
/*      */     //   170: invokevirtual 993	sun/security/provider/certpath/PKIX$ValidatorParams:anyPolicyInhibited	()Z
/*      */     //   173: invokevirtual 927	java/security/cert/PKIXBuilderParameters:setAnyPolicyInhibited	(Z)V
/*      */     //   176: aload 7
/*      */     //   178: aload_0
/*      */     //   179: getfield 869	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
/*      */     //   182: invokevirtual 1000	sun/security/provider/certpath/PKIX$ValidatorParams:date	()Ljava/util/Date;
/*      */     //   185: invokevirtual 933	java/security/cert/PKIXBuilderParameters:setDate	(Ljava/util/Date;)V
/*      */     //   188: aload 7
/*      */     //   190: aload_0
/*      */     //   191: getfield 869	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
/*      */     //   194: invokevirtual 999	sun/security/provider/certpath/PKIX$ValidatorParams:getPKIXParameters	()Ljava/security/cert/PKIXParameters;
/*      */     //   197: invokevirtual 941	java/security/cert/PKIXParameters:getCertPathCheckers	()Ljava/util/List;
/*      */     //   200: invokevirtual 934	java/security/cert/PKIXBuilderParameters:setCertPathCheckers	(Ljava/util/List;)V
/*      */     //   203: aload 7
/*      */     //   205: aload_0
/*      */     //   206: getfield 869	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
/*      */     //   209: invokevirtual 996	sun/security/provider/certpath/PKIX$ValidatorParams:sigProvider	()Ljava/lang/String;
/*      */     //   212: invokevirtual 931	java/security/cert/PKIXBuilderParameters:setSigProvider	(Ljava/lang/String;)V
/*      */     //   215: aload 7
/*      */     //   217: iconst_0
/*      */     //   218: invokevirtual 930	java/security/cert/PKIXBuilderParameters:setRevocationEnabled	(Z)V
/*      */     //   221: getstatic 849	sun/security/provider/certpath/Builder:USE_AIA	Z
/*      */     //   224: iconst_1
/*      */     //   225: if_icmpne +150 -> 375
/*      */     //   228: aconst_null
/*      */     //   229: astore 8
/*      */     //   231: aload_1
/*      */     //   232: invokestatic 1056	sun/security/x509/X509CertImpl:toImpl	(Ljava/security/cert/X509Certificate;)Lsun/security/x509/X509CertImpl;
/*      */     //   235: astore 8
/*      */     //   237: goto +38 -> 275
/*      */     //   240: astore 9
/*      */     //   242: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   245: ifnull +30 -> 275
/*      */     //   248: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   251: new 423	java/lang/StringBuilder
/*      */     //   254: dup
/*      */     //   255: invokespecial 896	java/lang/StringBuilder:<init>	()V
/*      */     //   258: ldc_w 404
/*      */     //   261: invokevirtual 901	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   264: aload 9
/*      */     //   266: invokevirtual 900	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   269: invokevirtual 897	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   272: invokevirtual 1035	sun/security/util/Debug:println	(Ljava/lang/String;)V
/*      */     //   275: aconst_null
/*      */     //   276: astore 9
/*      */     //   278: aload 8
/*      */     //   280: ifnull +10 -> 290
/*      */     //   283: aload 8
/*      */     //   285: invokevirtual 1053	sun/security/x509/X509CertImpl:getAuthorityInfoAccessExtension	()Lsun/security/x509/AuthorityInfoAccessExtension;
/*      */     //   288: astore 9
/*      */     //   290: aload 9
/*      */     //   292: ifnull +83 -> 375
/*      */     //   295: aload 9
/*      */     //   297: invokevirtual 1039	sun/security/x509/AuthorityInfoAccessExtension:getAccessDescriptions	()Ljava/util/List;
/*      */     //   300: astore 10
/*      */     //   302: aload 10
/*      */     //   304: ifnull +71 -> 375
/*      */     //   307: aload 10
/*      */     //   309: invokeinterface 1067 1 0
/*      */     //   314: astore 11
/*      */     //   316: aload 11
/*      */     //   318: invokeinterface 1061 1 0
/*      */     //   323: ifeq +52 -> 375
/*      */     //   326: aload 11
/*      */     //   328: invokeinterface 1062 1 0
/*      */     //   333: checkcast 494	sun/security/x509/AccessDescription
/*      */     //   336: astore 12
/*      */     //   338: aload 12
/*      */     //   340: invokestatic 1034	sun/security/provider/certpath/URICertStore:getInstance	(Lsun/security/x509/AccessDescription;)Ljava/security/cert/CertStore;
/*      */     //   343: astore 13
/*      */     //   345: aload 13
/*      */     //   347: ifnull +25 -> 372
/*      */     //   350: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   353: ifnull +12 -> 365
/*      */     //   356: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   359: ldc_w 408
/*      */     //   362: invokevirtual 1035	sun/security/util/Debug:println	(Ljava/lang/String;)V
/*      */     //   365: aload 7
/*      */     //   367: aload 13
/*      */     //   369: invokevirtual 932	java/security/cert/PKIXBuilderParameters:addCertStore	(Ljava/security/cert/CertStore;)V
/*      */     //   372: goto -56 -> 316
/*      */     //   375: aconst_null
/*      */     //   376: astore 8
/*      */     //   378: ldc_w 398
/*      */     //   381: invokestatic 908	java/security/cert/CertPathBuilder:getInstance	(Ljava/lang/String;)Ljava/security/cert/CertPathBuilder;
/*      */     //   384: astore 8
/*      */     //   386: goto +15 -> 401
/*      */     //   389: astore 9
/*      */     //   391: new 439	java/security/cert/CertPathValidatorException
/*      */     //   394: dup
/*      */     //   395: aload 9
/*      */     //   397: invokespecial 913	java/security/cert/CertPathValidatorException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   400: athrow
/*      */     //   401: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   404: ifnull +12 -> 416
/*      */     //   407: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   410: ldc_w 400
/*      */     //   413: invokevirtual 1035	sun/security/util/Debug:println	(Ljava/lang/String;)V
/*      */     //   416: aload 8
/*      */     //   418: aload 7
/*      */     //   420: invokevirtual 909	java/security/cert/CertPathBuilder:build	(Ljava/security/cert/CertPathParameters;)Ljava/security/cert/CertPathBuilderResult;
/*      */     //   423: checkcast 449	java/security/cert/PKIXCertPathBuilderResult
/*      */     //   426: astore 9
/*      */     //   428: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   431: ifnull +12 -> 443
/*      */     //   434: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   437: ldc_w 399
/*      */     //   440: invokevirtual 1035	sun/security/util/Debug:println	(Ljava/lang/String;)V
/*      */     //   443: aload_3
/*      */     //   444: ifnonnull +11 -> 455
/*      */     //   447: new 464	java/util/HashSet
/*      */     //   450: dup
/*      */     //   451: invokespecial 970	java/util/HashSet:<init>	()V
/*      */     //   454: astore_3
/*      */     //   455: aload_3
/*      */     //   456: aload_1
/*      */     //   457: invokeinterface 1071 2 0
/*      */     //   462: pop
/*      */     //   463: aload 9
/*      */     //   465: invokevirtual 940	java/security/cert/PKIXCertPathBuilderResult:getTrustAnchor	()Ljava/security/cert/TrustAnchor;
/*      */     //   468: astore 10
/*      */     //   470: aload 10
/*      */     //   472: invokevirtual 945	java/security/cert/TrustAnchor:getCAPublicKey	()Ljava/security/PublicKey;
/*      */     //   475: astore 11
/*      */     //   477: aload 11
/*      */     //   479: ifnonnull +13 -> 492
/*      */     //   482: aload 10
/*      */     //   484: invokevirtual 946	java/security/cert/TrustAnchor:getTrustedCert	()Ljava/security/cert/X509Certificate;
/*      */     //   487: invokevirtual 960	java/security/cert/X509Certificate:getPublicKey	()Ljava/security/PublicKey;
/*      */     //   490: astore 11
/*      */     //   492: iconst_1
/*      */     //   493: istore 12
/*      */     //   495: aload 9
/*      */     //   497: invokevirtual 939	java/security/cert/PKIXCertPathBuilderResult:getCertPath	()Ljava/security/cert/CertPath;
/*      */     //   500: invokevirtual 907	java/security/cert/CertPath:getCertificates	()Ljava/util/List;
/*      */     //   503: astore 13
/*      */     //   505: aload 13
/*      */     //   507: invokeinterface 1063 1 0
/*      */     //   512: iconst_1
/*      */     //   513: isub
/*      */     //   514: istore 14
/*      */     //   516: iload 14
/*      */     //   518: iflt +96 -> 614
/*      */     //   521: aload 13
/*      */     //   523: iload 14
/*      */     //   525: invokeinterface 1065 2 0
/*      */     //   530: checkcast 458	java/security/cert/X509Certificate
/*      */     //   533: astore 15
/*      */     //   535: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   538: ifnull +41 -> 579
/*      */     //   541: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   544: new 423	java/lang/StringBuilder
/*      */     //   547: dup
/*      */     //   548: invokespecial 896	java/lang/StringBuilder:<init>	()V
/*      */     //   551: ldc_w 402
/*      */     //   554: invokevirtual 901	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   557: iload 14
/*      */     //   559: invokevirtual 899	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   562: ldc_w 393
/*      */     //   565: invokevirtual 901	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   568: aload 15
/*      */     //   570: invokevirtual 900	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   573: invokevirtual 897	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   576: invokevirtual 1035	sun/security/util/Debug:println	(Ljava/lang/String;)V
/*      */     //   579: aload_0
/*      */     //   580: aload 15
/*      */     //   582: aload 11
/*      */     //   584: aconst_null
/*      */     //   585: iload 12
/*      */     //   587: iconst_1
/*      */     //   588: aload_3
/*      */     //   589: aload 6
/*      */     //   591: invokespecial 1030	sun/security/provider/certpath/RevocationChecker:checkCRLs	(Ljava/security/cert/X509Certificate;Ljava/security/PublicKey;Ljava/security/cert/X509Certificate;ZZLjava/util/Set;Ljava/util/Set;)V
/*      */     //   594: aload 15
/*      */     //   596: invokestatic 1009	sun/security/provider/certpath/RevocationChecker:certCanSignCrl	(Ljava/security/cert/X509Certificate;)Z
/*      */     //   599: istore 12
/*      */     //   601: aload 15
/*      */     //   603: invokevirtual 960	java/security/cert/X509Certificate:getPublicKey	()Ljava/security/PublicKey;
/*      */     //   606: astore 11
/*      */     //   608: iinc 14 -1
/*      */     //   611: goto -95 -> 516
/*      */     //   614: goto +21 -> 635
/*      */     //   617: astore 14
/*      */     //   619: aload 4
/*      */     //   621: aload 9
/*      */     //   623: invokevirtual 938	java/security/cert/PKIXCertPathBuilderResult:getPublicKey	()Ljava/security/PublicKey;
/*      */     //   626: invokeinterface 1071 2 0
/*      */     //   631: pop
/*      */     //   632: goto -231 -> 401
/*      */     //   635: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   638: ifnull +33 -> 671
/*      */     //   641: getstatic 871	sun/security/provider/certpath/RevocationChecker:debug	Lsun/security/util/Debug;
/*      */     //   644: new 423	java/lang/StringBuilder
/*      */     //   647: dup
/*      */     //   648: invokespecial 896	java/lang/StringBuilder:<init>	()V
/*      */     //   651: ldc_w 401
/*      */     //   654: invokevirtual 901	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   657: aload 9
/*      */     //   659: invokevirtual 938	java/security/cert/PKIXCertPathBuilderResult:getPublicKey	()Ljava/security/PublicKey;
/*      */     //   662: invokevirtual 900	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   665: invokevirtual 897	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   668: invokevirtual 1035	sun/security/util/Debug:println	(Ljava/lang/String;)V
/*      */     //   671: aload 9
/*      */     //   673: invokevirtual 938	java/security/cert/PKIXCertPathBuilderResult:getPublicKey	()Ljava/security/PublicKey;
/*      */     //   676: astore 14
/*      */     //   678: aload 13
/*      */     //   680: invokeinterface 1064 1 0
/*      */     //   685: ifeq +7 -> 692
/*      */     //   688: aconst_null
/*      */     //   689: goto +14 -> 703
/*      */     //   692: aload 13
/*      */     //   694: iconst_0
/*      */     //   695: invokeinterface 1065 2 0
/*      */     //   700: checkcast 458	java/security/cert/X509Certificate
/*      */     //   703: astore 15
/*      */     //   705: aload_0
/*      */     //   706: aload_1
/*      */     //   707: aload 14
/*      */     //   709: aload 15
/*      */     //   711: iconst_1
/*      */     //   712: iconst_0
/*      */     //   713: aconst_null
/*      */     //   714: aload_0
/*      */     //   715: getfield 869	sun/security/provider/certpath/RevocationChecker:params	Lsun/security/provider/certpath/PKIX$ValidatorParams;
/*      */     //   718: invokevirtual 1004	sun/security/provider/certpath/PKIX$ValidatorParams:trustAnchors	()Ljava/util/Set;
/*      */     //   721: invokespecial 1030	sun/security/provider/certpath/RevocationChecker:checkCRLs	(Ljava/security/cert/X509Certificate;Ljava/security/PublicKey;Ljava/security/cert/X509Certificate;ZZLjava/util/Set;Ljava/util/Set;)V
/*      */     //   724: return
/*      */     //   725: astore 16
/*      */     //   727: aload 16
/*      */     //   729: invokevirtual 915	java/security/cert/CertPathValidatorException:getReason	()Ljava/security/cert/CertPathValidatorException$Reason;
/*      */     //   732: getstatic 843	java/security/cert/CertPathValidatorException$BasicReason:REVOKED	Ljava/security/cert/CertPathValidatorException$BasicReason;
/*      */     //   735: if_acmpne +6 -> 741
/*      */     //   738: aload 16
/*      */     //   740: athrow
/*      */     //   741: aload 4
/*      */     //   743: aload 14
/*      */     //   745: invokeinterface 1071 2 0
/*      */     //   750: pop
/*      */     //   751: goto -350 -> 401
/*      */     //   754: astore 9
/*      */     //   756: new 439	java/security/cert/CertPathValidatorException
/*      */     //   759: dup
/*      */     //   760: aload 9
/*      */     //   762: invokespecial 913	java/security/cert/CertPathValidatorException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   765: athrow
/*      */     //   766: astore 9
/*      */     //   768: new 439	java/security/cert/CertPathValidatorException
/*      */     //   771: dup
/*      */     //   772: ldc 8
/*      */     //   774: aconst_null
/*      */     //   775: aconst_null
/*      */     //   776: iconst_m1
/*      */     //   777: getstatic 844	java/security/cert/CertPathValidatorException$BasicReason:UNDETERMINED_REVOCATION_STATUS	Ljava/security/cert/CertPathValidatorException$BasicReason;
/*      */     //   780: invokespecial 918	java/security/cert/CertPathValidatorException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;Ljava/security/cert/CertPath;ILjava/security/cert/CertPathValidatorException$Reason;)V
/*      */     //   783: athrow
/*      */     // Line number table:
/*      */     //   Java source line #956	-> byte code offset #0
/*      */     //   Java source line #957	-> byte code offset #6
/*      */     //   Java source line #960	-> byte code offset #15
/*      */     //   Java source line #961	-> byte code offset #24
/*      */     //   Java source line #962	-> byte code offset #28
/*      */     //   Java source line #964	-> byte code offset #37
/*      */     //   Java source line #965	-> byte code offset #48
/*      */     //   Java source line #966	-> byte code offset #57
/*      */     //   Java source line #968	-> byte code offset #65
/*      */     //   Java source line #969	-> byte code offset #76
/*      */     //   Java source line #970	-> byte code offset #86
/*      */     //   Java source line #974	-> byte code offset #91
/*      */     //   Java source line #977	-> byte code offset #104
/*      */     //   Java source line #975	-> byte code offset #107
/*      */     //   Java source line #976	-> byte code offset #109
/*      */     //   Java source line #978	-> byte code offset #119
/*      */     //   Java source line #979	-> byte code offset #131
/*      */     //   Java source line #980	-> byte code offset #140
/*      */     //   Java source line #981	-> byte code offset #146
/*      */     //   Java source line #982	-> byte code offset #152
/*      */     //   Java source line #983	-> byte code offset #158
/*      */     //   Java source line #984	-> byte code offset #164
/*      */     //   Java source line #988	-> byte code offset #176
/*      */     //   Java source line #990	-> byte code offset #188
/*      */     //   Java source line #991	-> byte code offset #194
/*      */     //   Java source line #990	-> byte code offset #200
/*      */     //   Java source line #992	-> byte code offset #203
/*      */     //   Java source line #997	-> byte code offset #215
/*      */     //   Java source line #1000	-> byte code offset #221
/*      */     //   Java source line #1001	-> byte code offset #228
/*      */     //   Java source line #1003	-> byte code offset #231
/*      */     //   Java source line #1010	-> byte code offset #237
/*      */     //   Java source line #1004	-> byte code offset #240
/*      */     //   Java source line #1006	-> byte code offset #242
/*      */     //   Java source line #1007	-> byte code offset #248
/*      */     //   Java source line #1011	-> byte code offset #275
/*      */     //   Java source line #1012	-> byte code offset #278
/*      */     //   Java source line #1013	-> byte code offset #283
/*      */     //   Java source line #1015	-> byte code offset #290
/*      */     //   Java source line #1016	-> byte code offset #295
/*      */     //   Java source line #1017	-> byte code offset #302
/*      */     //   Java source line #1018	-> byte code offset #307
/*      */     //   Java source line #1019	-> byte code offset #338
/*      */     //   Java source line #1020	-> byte code offset #345
/*      */     //   Java source line #1021	-> byte code offset #350
/*      */     //   Java source line #1022	-> byte code offset #356
/*      */     //   Java source line #1024	-> byte code offset #365
/*      */     //   Java source line #1026	-> byte code offset #372
/*      */     //   Java source line #1031	-> byte code offset #375
/*      */     //   Java source line #1033	-> byte code offset #378
/*      */     //   Java source line #1036	-> byte code offset #386
/*      */     //   Java source line #1034	-> byte code offset #389
/*      */     //   Java source line #1035	-> byte code offset #391
/*      */     //   Java source line #1039	-> byte code offset #401
/*      */     //   Java source line #1040	-> byte code offset #407
/*      */     //   Java source line #1043	-> byte code offset #416
/*      */     //   Java source line #1044	-> byte code offset #420
/*      */     //   Java source line #1046	-> byte code offset #428
/*      */     //   Java source line #1047	-> byte code offset #434
/*      */     //   Java source line #1052	-> byte code offset #443
/*      */     //   Java source line #1053	-> byte code offset #447
/*      */     //   Java source line #1055	-> byte code offset #455
/*      */     //   Java source line #1056	-> byte code offset #463
/*      */     //   Java source line #1057	-> byte code offset #470
/*      */     //   Java source line #1058	-> byte code offset #477
/*      */     //   Java source line #1059	-> byte code offset #482
/*      */     //   Java source line #1061	-> byte code offset #492
/*      */     //   Java source line #1062	-> byte code offset #495
/*      */     //   Java source line #1063	-> byte code offset #497
/*      */     //   Java source line #1065	-> byte code offset #505
/*      */     //   Java source line #1066	-> byte code offset #521
/*      */     //   Java source line #1068	-> byte code offset #535
/*      */     //   Java source line #1069	-> byte code offset #541
/*      */     //   Java source line #1073	-> byte code offset #579
/*      */     //   Java source line #1075	-> byte code offset #594
/*      */     //   Java source line #1076	-> byte code offset #601
/*      */     //   Java source line #1065	-> byte code offset #608
/*      */     //   Java source line #1082	-> byte code offset #614
/*      */     //   Java source line #1078	-> byte code offset #617
/*      */     //   Java source line #1080	-> byte code offset #619
/*      */     //   Java source line #1081	-> byte code offset #632
/*      */     //   Java source line #1084	-> byte code offset #635
/*      */     //   Java source line #1085	-> byte code offset #641
/*      */     //   Java source line #1086	-> byte code offset #659
/*      */     //   Java source line #1085	-> byte code offset #668
/*      */     //   Java source line #1092	-> byte code offset #671
/*      */     //   Java source line #1093	-> byte code offset #678
/*      */     //   Java source line #1094	-> byte code offset #695
/*      */     //   Java source line #1096	-> byte code offset #705
/*      */     //   Java source line #1097	-> byte code offset #718
/*      */     //   Java source line #1096	-> byte code offset #721
/*      */     //   Java source line #1099	-> byte code offset #724
/*      */     //   Java source line #1100	-> byte code offset #725
/*      */     //   Java source line #1102	-> byte code offset #727
/*      */     //   Java source line #1103	-> byte code offset #738
/*      */     //   Java source line #1108	-> byte code offset #741
/*      */     //   Java source line #1115	-> byte code offset #751
/*      */     //   Java source line #1109	-> byte code offset #754
/*      */     //   Java source line #1110	-> byte code offset #756
/*      */     //   Java source line #1111	-> byte code offset #766
/*      */     //   Java source line #1112	-> byte code offset #768
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	784	0	this	RevocationChecker
/*      */     //   0	784	1	paramX509Certificate	X509Certificate
/*      */     //   0	784	2	paramPublicKey	PublicKey
/*      */     //   0	784	3	paramSet	Set<X509Certificate>
/*      */     //   22	720	4	localHashSet	HashSet
/*      */     //   46	52	5	localRejectKeySelector	RejectKeySelector
/*      */     //   89	501	6	localSet	Set
/*      */     //   102	317	7	localPKIXBuilderParameters	java.security.cert.PKIXBuilderParameters
/*      */     //   107	7	8	localInvalidAlgorithmParameterException1	InvalidAlgorithmParameterException
/*      */     //   229	188	8	localObject1	Object
/*      */     //   240	25	9	localCertificateException	CertificateException
/*      */     //   276	20	9	localAuthorityInfoAccessExtension	sun.security.x509.AuthorityInfoAccessExtension
/*      */     //   389	7	9	localNoSuchAlgorithmException	java.security.NoSuchAlgorithmException
/*      */     //   426	246	9	localPKIXCertPathBuilderResult	java.security.cert.PKIXCertPathBuilderResult
/*      */     //   754	7	9	localInvalidAlgorithmParameterException2	InvalidAlgorithmParameterException
/*      */     //   766	1	9	localCertPathBuilderException	java.security.cert.CertPathBuilderException
/*      */     //   300	183	10	localObject2	Object
/*      */     //   314	293	11	localObject3	Object
/*      */     //   336	3	12	localAccessDescription	sun.security.x509.AccessDescription
/*      */     //   493	107	12	bool	boolean
/*      */     //   343	350	13	localObject4	Object
/*      */     //   514	95	14	i	int
/*      */     //   617	1	14	localCertPathValidatorException1	CertPathValidatorException
/*      */     //   676	68	14	localPublicKey	PublicKey
/*      */     //   533	177	15	localX509Certificate	X509Certificate
/*      */     //   725	14	16	localCertPathValidatorException2	CertPathValidatorException
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   91	104	107	java/security/InvalidAlgorithmParameterException
/*      */     //   231	237	240	java/security/cert/CertificateException
/*      */     //   378	386	389	java/security/NoSuchAlgorithmException
/*      */     //   505	614	617	java/security/cert/CertPathValidatorException
/*      */     //   705	724	725	java/security/cert/CertPathValidatorException
/*      */     //   401	632	754	java/security/InvalidAlgorithmParameterException
/*      */     //   635	724	754	java/security/InvalidAlgorithmParameterException
/*      */     //   725	751	754	java/security/InvalidAlgorithmParameterException
/*      */     //   401	632	766	java/security/cert/CertPathBuilderException
/*      */     //   635	724	766	java/security/cert/CertPathBuilderException
/*      */     //   725	751	766	java/security/cert/CertPathBuilderException
/*      */   }
/*      */   
/*      */   public RevocationChecker clone()
/*      */   {
/* 1121 */     RevocationChecker localRevocationChecker = (RevocationChecker)super.clone();
/*      */     
/*      */ 
/* 1124 */     localRevocationChecker.softFailExceptions = new LinkedList(this.softFailExceptions);
/* 1125 */     return localRevocationChecker;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class RejectKeySelector
/*      */     extends X509CertSelector
/*      */   {
/*      */     private final Set<PublicKey> badKeySet;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     RejectKeySelector(Set<PublicKey> paramSet)
/*      */     {
/* 1146 */       this.badKeySet = paramSet;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean match(Certificate paramCertificate)
/*      */     {
/* 1158 */       if (!super.match(paramCertificate)) {
/* 1159 */         return false;
/*      */       }
/* 1161 */       if (this.badKeySet.contains(paramCertificate.getPublicKey())) {
/* 1162 */         if (RevocationChecker.debug != null)
/* 1163 */           RevocationChecker.debug.println("RejectKeySelector.match: bad key");
/* 1164 */         return false;
/*      */       }
/*      */       
/* 1167 */       if (RevocationChecker.debug != null)
/* 1168 */         RevocationChecker.debug.println("RejectKeySelector.match: returning true");
/* 1169 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1180 */       StringBuilder localStringBuilder = new StringBuilder();
/* 1181 */       localStringBuilder.append("RejectKeySelector: [\n");
/* 1182 */       localStringBuilder.append(super.toString());
/* 1183 */       localStringBuilder.append(this.badKeySet);
/* 1184 */       localStringBuilder.append("]");
/* 1185 */       return localStringBuilder.toString();
/*      */     }
/*      */   }
/*      */   
/*      */   private static class RevocationProperties
/*      */   {
/*      */     boolean onlyEE;
/*      */     boolean ocspEnabled;
/*      */     boolean crlDPEnabled;
/*      */     String ocspUrl;
/*      */     String ocspSubject;
/*      */     String ocspIssuer;
/*      */     String ocspSerial;
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\RevocationChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
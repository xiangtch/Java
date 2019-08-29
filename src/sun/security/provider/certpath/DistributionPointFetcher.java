/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CRL;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CertPathBuilder;
/*     */ import java.security.cert.CertPathParameters;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.PKIXBuilderParameters;
/*     */ import java.security.cert.PKIXCertPathBuilderResult;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509CRLSelector;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AuthorityKeyIdentifierExtension;
/*     */ import sun.security.x509.CRLDistributionPointsExtension;
/*     */ import sun.security.x509.DistributionPoint;
/*     */ import sun.security.x509.DistributionPointName;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.GeneralNames;
/*     */ import sun.security.x509.IssuingDistributionPointExtension;
/*     */ import sun.security.x509.KeyIdentifier;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ import sun.security.x509.RDN;
/*     */ import sun.security.x509.ReasonFlags;
/*     */ import sun.security.x509.SerialNumber;
/*     */ import sun.security.x509.URIName;
/*     */ import sun.security.x509.X500Name;
/*     */ import sun.security.x509.X509CRLImpl;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ public class DistributionPointFetcher
/*     */ {
/*  55 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*  57 */   private static final boolean[] ALL_REASONS = { true, true, true, true, true, true, true, true, true };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, String paramString1, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate, String paramString2)
/*     */     throws CertStoreException
/*     */   {
/*  75 */     return getCRLs(paramX509CRLSelector, paramBoolean, paramPublicKey, null, paramString1, paramList, paramArrayOfBoolean, paramSet, paramDate, paramString2);
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
/*     */   public static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, String paramString, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate)
/*     */     throws CertStoreException
/*     */   {
/*  93 */     return getCRLs(paramX509CRLSelector, paramBoolean, paramPublicKey, null, paramString, paramList, paramArrayOfBoolean, paramSet, paramDate, "generic");
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
/*     */   public static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, X509Certificate paramX509Certificate, String paramString1, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate, String paramString2)
/*     */     throws CertStoreException
/*     */   {
/* 113 */     X509Certificate localX509Certificate = paramX509CRLSelector.getCertificateChecking();
/* 114 */     if (localX509Certificate == null) {
/* 115 */       return Collections.emptySet();
/*     */     }
/*     */     try {
/* 118 */       X509CertImpl localX509CertImpl = X509CertImpl.toImpl(localX509Certificate);
/* 119 */       if (debug != null) {
/* 120 */         debug.println("DistributionPointFetcher.getCRLs: Checking CRLDPs for " + localX509CertImpl
/* 121 */           .getSubjectX500Principal());
/*     */       }
/*     */       
/* 124 */       CRLDistributionPointsExtension localCRLDistributionPointsExtension = localX509CertImpl.getCRLDistributionPointsExtension();
/* 125 */       if (localCRLDistributionPointsExtension == null) {
/* 126 */         if (debug != null) {
/* 127 */           debug.println("No CRLDP ext");
/*     */         }
/* 129 */         return Collections.emptySet();
/*     */       }
/*     */       
/* 132 */       List localList = localCRLDistributionPointsExtension.get("points");
/* 133 */       HashSet localHashSet = new HashSet();
/* 134 */       Iterator localIterator = localList.iterator();
/* 135 */       while ((localIterator.hasNext()) && (!Arrays.equals(paramArrayOfBoolean, ALL_REASONS))) {
/* 136 */         DistributionPoint localDistributionPoint = (DistributionPoint)localIterator.next();
/* 137 */         Collection localCollection = getCRLs(paramX509CRLSelector, localX509CertImpl, localDistributionPoint, paramArrayOfBoolean, paramBoolean, paramPublicKey, paramX509Certificate, paramString1, paramList, paramSet, paramDate, paramString2);
/*     */         
/*     */ 
/* 140 */         localHashSet.addAll(localCollection);
/*     */       }
/* 142 */       if (debug != null) {
/* 143 */         debug.println("Returning " + localHashSet.size() + " CRLs");
/*     */       }
/* 145 */       return localHashSet;
/*     */     } catch (CertificateException|IOException localCertificateException) {}
/* 147 */     return Collections.emptySet();
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
/*     */   private static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, X509CertImpl paramX509CertImpl, DistributionPoint paramDistributionPoint, boolean[] paramArrayOfBoolean, boolean paramBoolean, PublicKey paramPublicKey, X509Certificate paramX509Certificate, String paramString1, List<CertStore> paramList, Set<TrustAnchor> paramSet, Date paramDate, String paramString2)
/*     */     throws CertStoreException
/*     */   {
/* 168 */     GeneralNames localGeneralNames1 = paramDistributionPoint.getFullName();
/* 169 */     if (localGeneralNames1 == null)
/*     */     {
/* 171 */       localObject1 = paramDistributionPoint.getRelativeName();
/* 172 */       if (localObject1 == null) {
/* 173 */         return Collections.emptySet();
/*     */       }
/*     */       try {
/* 176 */         GeneralNames localGeneralNames2 = paramDistributionPoint.getCRLIssuer();
/* 177 */         if (localGeneralNames2 == null)
/*     */         {
/* 179 */           localGeneralNames1 = getFullNames((X500Name)paramX509CertImpl.getIssuerDN(), (RDN)localObject1);
/*     */         }
/*     */         else {
/* 182 */           if (localGeneralNames2.size() != 1) {
/* 183 */             return Collections.emptySet();
/*     */           }
/*     */           
/* 186 */           localGeneralNames1 = getFullNames((X500Name)localGeneralNames2.get(0).getName(), (RDN)localObject1);
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException1) {
/* 190 */         return Collections.emptySet();
/*     */       }
/*     */     }
/* 193 */     Object localObject1 = new ArrayList();
/* 194 */     Object localObject2 = null;
/* 195 */     for (Object localObject3 = localGeneralNames1.iterator(); ((Iterator)localObject3).hasNext();) {
/*     */       try {
/* 197 */         GeneralName localGeneralName = (GeneralName)((Iterator)localObject3).next();
/* 198 */         if (localGeneralName.getType() == 4) {
/* 199 */           localObject4 = (X500Name)localGeneralName.getName();
/* 200 */           ((Collection)localObject1).addAll(
/* 201 */             getCRLs((X500Name)localObject4, paramX509CertImpl.getIssuerX500Principal(), paramList));
/*     */         }
/* 203 */         else if (localGeneralName.getType() == 6) {
/* 204 */           localObject4 = (URIName)localGeneralName.getName();
/* 205 */           X509CRL localX509CRL = getCRL((URIName)localObject4);
/* 206 */           if (localX509CRL != null) {
/* 207 */             ((Collection)localObject1).add(localX509CRL);
/*     */           }
/*     */         }
/*     */       } catch (CertStoreException localCertStoreException) {
/* 211 */         localObject2 = localCertStoreException;
/*     */       }
/*     */     }
/*     */     Object localObject4;
/* 215 */     if ((((Collection)localObject1).isEmpty()) && (localObject2 != null)) {
/* 216 */       throw ((Throwable)localObject2);
/*     */     }
/*     */     
/* 219 */     localObject3 = new ArrayList(2);
/* 220 */     for (Iterator localIterator = ((Collection)localObject1).iterator(); localIterator.hasNext();) { localObject4 = (X509CRL)localIterator.next();
/*     */       
/*     */       try
/*     */       {
/* 224 */         paramX509CRLSelector.setIssuerNames(null);
/* 225 */         if ((paramX509CRLSelector.match((CRL)localObject4)) && (verifyCRL(paramX509CertImpl, paramDistributionPoint, (X509CRL)localObject4, paramArrayOfBoolean, paramBoolean, paramPublicKey, paramX509Certificate, paramString1, paramSet, paramList, paramDate, paramString2)))
/*     */         {
/*     */ 
/* 228 */           ((Collection)localObject3).add(localObject4);
/*     */         }
/*     */       }
/*     */       catch (IOException|CRLException localIOException2) {
/* 232 */         if (debug != null) {
/* 233 */           debug.println("Exception verifying CRL: " + localIOException2.getMessage());
/* 234 */           localIOException2.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/* 238 */     return (Collection<X509CRL>)localObject3;
/*     */   }
/*     */   
/*     */ 
/*     */   private static X509CRL getCRL(URIName paramURIName)
/*     */     throws CertStoreException
/*     */   {
/* 245 */     URI localURI = paramURIName.getURI();
/* 246 */     if (debug != null) {
/* 247 */       debug.println("Trying to fetch CRL from DP " + localURI);
/*     */     }
/* 249 */     CertStore localCertStore = null;
/*     */     try
/*     */     {
/* 252 */       localCertStore = URICertStore.getInstance(new URICertStore.URICertStoreParameters(localURI));
/*     */     }
/*     */     catch (InvalidAlgorithmParameterException|NoSuchAlgorithmException localInvalidAlgorithmParameterException) {
/* 255 */       if (debug != null) {
/* 256 */         debug.println("Can't create URICertStore: " + localInvalidAlgorithmParameterException.getMessage());
/*     */       }
/* 258 */       return null;
/*     */     }
/*     */     
/* 261 */     Collection localCollection = localCertStore.getCRLs(null);
/* 262 */     if (localCollection.isEmpty()) {
/* 263 */       return null;
/*     */     }
/* 265 */     return (X509CRL)localCollection.iterator().next();
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
/*     */   private static Collection<X509CRL> getCRLs(X500Name paramX500Name, X500Principal paramX500Principal, List<CertStore> paramList)
/*     */     throws CertStoreException
/*     */   {
/* 282 */     if (debug != null) {
/* 283 */       debug.println("Trying to fetch CRL from DP " + paramX500Name);
/*     */     }
/* 285 */     X509CRLSelector localX509CRLSelector = new X509CRLSelector();
/* 286 */     localX509CRLSelector.addIssuer(paramX500Name.asX500Principal());
/* 287 */     localX509CRLSelector.addIssuer(paramX500Principal);
/* 288 */     ArrayList localArrayList = new ArrayList();
/* 289 */     PKIX.CertStoreTypeException localCertStoreTypeException = null;
/* 290 */     for (CertStore localCertStore : paramList) {
/*     */       try {
/* 292 */         for (CRL localCRL : localCertStore.getCRLs(localX509CRLSelector)) {
/* 293 */           localArrayList.add((X509CRL)localCRL);
/*     */         }
/*     */       } catch (CertStoreException localCertStoreException) {
/* 296 */         if (debug != null) {
/* 297 */           debug.println("Exception while retrieving CRLs: " + localCertStoreException);
/*     */           
/* 299 */           localCertStoreException.printStackTrace();
/*     */         }
/* 301 */         localCertStoreTypeException = new PKIX.CertStoreTypeException(localCertStore.getType(), localCertStoreException);
/*     */       }
/*     */     }
/*     */     
/* 305 */     if ((localArrayList.isEmpty()) && (localCertStoreTypeException != null)) {
/* 306 */       throw localCertStoreTypeException;
/*     */     }
/* 308 */     return localArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean verifyCRL(X509CertImpl paramX509CertImpl, DistributionPoint paramDistributionPoint, X509CRL paramX509CRL, boolean[] paramArrayOfBoolean, boolean paramBoolean, PublicKey paramPublicKey, X509Certificate paramX509Certificate, String paramString1, Set<TrustAnchor> paramSet, List<CertStore> paramList, Date paramDate, String paramString2)
/*     */     throws CRLException, IOException
/*     */   {
/* 338 */     if (debug != null) {
/* 339 */       debug.println("DistributionPointFetcher.verifyCRL: checking revocation status for\n  SN: " + 
/*     */       
/* 341 */         Debug.toHexString(paramX509CertImpl.getSerialNumber()) + "\n  Subject: " + paramX509CertImpl
/* 342 */         .getSubjectX500Principal() + "\n  Issuer: " + paramX509CertImpl
/* 343 */         .getIssuerX500Principal());
/*     */     }
/*     */     
/* 346 */     int i = 0;
/* 347 */     X509CRLImpl localX509CRLImpl = X509CRLImpl.toImpl(paramX509CRL);
/*     */     
/* 349 */     IssuingDistributionPointExtension localIssuingDistributionPointExtension = localX509CRLImpl.getIssuingDistributionPointExtension();
/* 350 */     X500Name localX500Name1 = (X500Name)paramX509CertImpl.getIssuerDN();
/* 351 */     X500Name localX500Name2 = (X500Name)localX509CRLImpl.getIssuerDN();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 357 */     GeneralNames localGeneralNames = paramDistributionPoint.getCRLIssuer();
/* 358 */     X500Name localX500Name3 = null;
/* 359 */     Object localObject3; if (localGeneralNames != null) {
/* 360 */       if ((localIssuingDistributionPointExtension == null) || 
/*     */       
/*     */ 
/* 363 */         (((Boolean)localIssuingDistributionPointExtension.get("indirect_crl")).equals(Boolean.FALSE))) {
/* 364 */         return false;
/*     */       }
/* 366 */       int j = 0;
/* 367 */       localObject2 = localGeneralNames.iterator();
/* 368 */       while ((j == 0) && (((Iterator)localObject2).hasNext())) {
/* 369 */         localObject3 = ((GeneralName)((Iterator)localObject2).next()).getName();
/* 370 */         if (localX500Name2.equals(localObject3) == true) {
/* 371 */           localX500Name3 = (X500Name)localObject3;
/* 372 */           j = 1;
/*     */         }
/*     */       }
/* 375 */       if (j == 0) {
/* 376 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 381 */       if (issues(paramX509CertImpl, localX509CRLImpl, paramString1))
/*     */       {
/* 383 */         paramPublicKey = paramX509CertImpl.getPublicKey();
/*     */       } else
/* 385 */         i = 1;
/*     */     } else {
/* 387 */       if (!localX500Name2.equals(localX500Name1)) {
/* 388 */         if (debug != null) {
/* 389 */           debug.println("crl issuer does not equal cert issuer.\ncrl issuer: " + localX500Name2 + "\ncert issuer: " + localX500Name1);
/*     */         }
/*     */         
/*     */ 
/* 393 */         return false;
/*     */       }
/*     */       
/* 396 */       localObject1 = paramX509CertImpl.getAuthKeyId();
/* 397 */       localObject2 = localX509CRLImpl.getAuthKeyId();
/*     */       
/* 399 */       if ((localObject1 == null) || (localObject2 == null))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 404 */         if (issues(paramX509CertImpl, localX509CRLImpl, paramString1))
/*     */         {
/* 406 */           paramPublicKey = paramX509CertImpl.getPublicKey();
/*     */         }
/* 408 */       } else if (!((KeyIdentifier)localObject1).equals(localObject2))
/*     */       {
/*     */ 
/* 411 */         if (issues(paramX509CertImpl, localX509CRLImpl, paramString1))
/*     */         {
/* 413 */           paramPublicKey = paramX509CertImpl.getPublicKey();
/*     */         } else {
/* 415 */           i = 1;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 420 */     if ((i == 0) && (!paramBoolean))
/*     */     {
/* 422 */       return false; }
/*     */     Object localObject6;
/*     */     Object localObject7;
/* 425 */     Object localObject8; Object localObject4; if (localIssuingDistributionPointExtension != null)
/*     */     {
/* 427 */       localObject1 = (DistributionPointName)localIssuingDistributionPointExtension.get("point");
/* 428 */       if (localObject1 != null) {
/* 429 */         localObject2 = ((DistributionPointName)localObject1).getFullName();
/* 430 */         if (localObject2 == null) {
/* 431 */           localObject3 = ((DistributionPointName)localObject1).getRelativeName();
/* 432 */           if (localObject3 == null) {
/* 433 */             if (debug != null) {
/* 434 */               debug.println("IDP must be relative or full DN");
/*     */             }
/* 436 */             return false;
/*     */           }
/* 438 */           if (debug != null) {
/* 439 */             debug.println("IDP relativeName:" + localObject3);
/*     */           }
/* 441 */           localObject2 = getFullNames(localX500Name2, (RDN)localObject3);
/*     */         }
/*     */         
/*     */         Object localObject5;
/*     */         
/* 446 */         if ((paramDistributionPoint.getFullName() != null) || 
/* 447 */           (paramDistributionPoint.getRelativeName() != null)) {
/* 448 */           localObject3 = paramDistributionPoint.getFullName();
/* 449 */           if (localObject3 == null) {
/* 450 */             RDN localRDN = paramDistributionPoint.getRelativeName();
/* 451 */             if (localRDN == null) {
/* 452 */               if (debug != null) {
/* 453 */                 debug.println("DP must be relative or full DN");
/*     */               }
/* 455 */               return false;
/*     */             }
/* 457 */             if (debug != null) {
/* 458 */               debug.println("DP relativeName:" + localRDN);
/*     */             }
/* 460 */             if (i != 0) {
/* 461 */               if (localGeneralNames.size() != 1)
/*     */               {
/*     */ 
/* 464 */                 if (debug != null) {
/* 465 */                   debug.println("must only be one CRL issuer when relative name present");
/*     */                 }
/*     */                 
/* 468 */                 return false;
/*     */               }
/*     */               
/* 471 */               localObject3 = getFullNames(localX500Name3, localRDN);
/*     */             } else {
/* 473 */               localObject3 = getFullNames(localX500Name1, localRDN);
/*     */             }
/*     */           }
/* 476 */           boolean bool2 = false;
/* 477 */           localObject5 = ((GeneralNames)localObject2).iterator();
/* 478 */           while ((!bool2) && (((Iterator)localObject5).hasNext())) {
/* 479 */             localObject6 = ((GeneralName)((Iterator)localObject5).next()).getName();
/* 480 */             if (debug != null) {
/* 481 */               debug.println("idpName: " + localObject6);
/*     */             }
/* 483 */             localObject7 = ((GeneralNames)localObject3).iterator();
/* 484 */             while ((!bool2) && (((Iterator)localObject7).hasNext())) {
/* 485 */               localObject8 = ((GeneralName)((Iterator)localObject7).next()).getName();
/* 486 */               if (debug != null) {
/* 487 */                 debug.println("pointName: " + localObject8);
/*     */               }
/* 489 */               bool2 = localObject6.equals(localObject8);
/*     */             }
/*     */           }
/* 492 */           if (!bool2) {
/* 493 */             if (debug != null) {
/* 494 */               debug.println("IDP name does not match DP name");
/*     */             }
/* 496 */             return false;
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 505 */           boolean bool1 = false;
/* 506 */           localObject4 = localGeneralNames.iterator();
/* 507 */           while ((!bool1) && (((Iterator)localObject4).hasNext())) {
/* 508 */             localObject5 = ((GeneralName)((Iterator)localObject4).next()).getName();
/* 509 */             localObject6 = ((GeneralNames)localObject2).iterator();
/* 510 */             while ((!bool1) && (((Iterator)localObject6).hasNext())) {
/* 511 */               localObject7 = ((GeneralName)((Iterator)localObject6).next()).getName();
/* 512 */               bool1 = localObject5.equals(localObject7);
/*     */             }
/*     */           }
/* 515 */           if (!bool1) {
/* 516 */             return false;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 524 */       localObject2 = (Boolean)localIssuingDistributionPointExtension.get("only_user_certs");
/* 525 */       if ((((Boolean)localObject2).equals(Boolean.TRUE)) && (paramX509CertImpl.getBasicConstraints() != -1)) {
/* 526 */         if (debug != null) {
/* 527 */           debug.println("cert must be a EE cert");
/*     */         }
/* 529 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 535 */       localObject2 = (Boolean)localIssuingDistributionPointExtension.get("only_ca_certs");
/* 536 */       if ((((Boolean)localObject2).equals(Boolean.TRUE)) && (paramX509CertImpl.getBasicConstraints() == -1)) {
/* 537 */         if (debug != null) {
/* 538 */           debug.println("cert must be a CA cert");
/*     */         }
/* 540 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 546 */       localObject2 = (Boolean)localIssuingDistributionPointExtension.get("only_attribute_certs");
/* 547 */       if (((Boolean)localObject2).equals(Boolean.TRUE)) {
/* 548 */         if (debug != null) {
/* 549 */           debug.println("cert must not be an AA cert");
/*     */         }
/* 551 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 556 */     Object localObject1 = new boolean[9];
/* 557 */     Object localObject2 = null;
/* 558 */     if (localIssuingDistributionPointExtension != null)
/*     */     {
/* 560 */       localObject2 = (ReasonFlags)localIssuingDistributionPointExtension.get("reasons");
/*     */     }
/*     */     
/* 563 */     boolean[] arrayOfBoolean = paramDistributionPoint.getReasonFlags();
/* 564 */     if (localObject2 != null) {
/* 565 */       if (arrayOfBoolean != null)
/*     */       {
/*     */ 
/* 568 */         localObject4 = ((ReasonFlags)localObject2).getFlags();
/* 569 */         for (m = 0; m < localObject1.length; m++) {
/* 570 */           localObject1[m] = ((m < localObject4.length) && (localObject4[m] != 0) && (m < arrayOfBoolean.length) && (arrayOfBoolean[m] != 0) ? 1 : 0);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 578 */         localObject1 = (boolean[])((ReasonFlags)localObject2).getFlags().clone();
/*     */       }
/* 580 */     } else if ((localIssuingDistributionPointExtension == null) || (localObject2 == null)) {
/* 581 */       if (arrayOfBoolean != null)
/*     */       {
/* 583 */         localObject1 = (boolean[])arrayOfBoolean.clone();
/*     */       }
/*     */       else {
/* 586 */         Arrays.fill((boolean[])localObject1, true);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 592 */     int k = 0;
/* 593 */     for (int m = 0; (m < localObject1.length) && (k == 0); m++) {
/* 594 */       if ((localObject1[m] != 0) && ((m >= paramArrayOfBoolean.length) || (paramArrayOfBoolean[m] == 0)))
/*     */       {
/*     */ 
/* 597 */         k = 1;
/*     */       }
/*     */     }
/* 600 */     if (k == 0) {
/* 601 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 607 */     if (i != 0) {
/* 608 */       X509CertSelector localX509CertSelector = new X509CertSelector();
/* 609 */       localX509CertSelector.setSubject(localX500Name2.asX500Principal());
/* 610 */       localObject6 = new boolean[] { false, false, false, false, false, false, true };
/* 611 */       localX509CertSelector.setKeyUsage((boolean[])localObject6);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 624 */       localObject7 = localX509CRLImpl.getAuthKeyIdExtension();
/* 625 */       if (localObject7 != null) {
/* 626 */         localObject8 = ((AuthorityKeyIdentifierExtension)localObject7).getEncodedKeyIdentifier();
/* 627 */         if (localObject8 != null) {
/* 628 */           localX509CertSelector.setSubjectKeyIdentifier((byte[])localObject8);
/*     */         }
/*     */         
/* 631 */         localObject9 = (SerialNumber)((AuthorityKeyIdentifierExtension)localObject7).get("serial_number");
/*     */         
/* 633 */         if (localObject9 != null) {
/* 634 */           localX509CertSelector.setSerialNumber(((SerialNumber)localObject9).getNumber());
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 644 */       localObject8 = new HashSet(paramSet);
/*     */       
/* 646 */       if (paramPublicKey != null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 653 */         if (paramX509Certificate != null) {
/* 654 */           localObject9 = new TrustAnchor(paramX509Certificate, null);
/*     */         } else {
/* 656 */           X500Principal localX500Principal = paramX509CertImpl.getIssuerX500Principal();
/* 657 */           localObject9 = new TrustAnchor(localX500Principal, paramPublicKey, null);
/*     */         }
/* 659 */         ((Set)localObject8).add(localObject9);
/*     */       }
/*     */       
/* 662 */       Object localObject9 = null;
/*     */       try {
/* 664 */         localObject9 = new PKIXBuilderParameters((Set)localObject8, localX509CertSelector);
/*     */       } catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException) {
/* 666 */         throw new CRLException(localInvalidAlgorithmParameterException);
/*     */       }
/* 668 */       ((PKIXBuilderParameters)localObject9).setCertStores(paramList);
/* 669 */       ((PKIXBuilderParameters)localObject9).setSigProvider(paramString1);
/* 670 */       ((PKIXBuilderParameters)localObject9).setDate(paramDate);
/*     */       try {
/* 672 */         CertPathBuilder localCertPathBuilder = CertPathBuilder.getInstance("PKIX");
/*     */         
/* 674 */         PKIXCertPathBuilderResult localPKIXCertPathBuilderResult = (PKIXCertPathBuilderResult)localCertPathBuilder.build((CertPathParameters)localObject9);
/* 675 */         paramPublicKey = localPKIXCertPathBuilderResult.getPublicKey();
/*     */       } catch (GeneralSecurityException localGeneralSecurityException2) {
/* 677 */         throw new CRLException(localGeneralSecurityException2);
/*     */       }
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 683 */       AlgorithmChecker.check(paramPublicKey, paramX509CRL, paramString2);
/*     */     } catch (CertPathValidatorException localCertPathValidatorException) {
/* 685 */       if (debug != null) {
/* 686 */         debug.println("CRL signature algorithm check failed: " + localCertPathValidatorException);
/*     */       }
/* 688 */       return false;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 693 */       paramX509CRL.verify(paramPublicKey, paramString1);
/*     */     } catch (GeneralSecurityException localGeneralSecurityException1) {
/* 695 */       if (debug != null) {
/* 696 */         debug.println("CRL signature failed to verify");
/*     */       }
/* 698 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 702 */     Set localSet = paramX509CRL.getCriticalExtensionOIDs();
/*     */     
/* 704 */     if (localSet != null) {
/* 705 */       localSet.remove(PKIXExtensions.IssuingDistributionPoint_Id.toString());
/* 706 */       if (!localSet.isEmpty()) {
/* 707 */         if (debug != null) {
/* 708 */           debug.println("Unrecognized critical extension(s) in CRL: " + localSet);
/*     */           
/* 710 */           for (localObject6 = localSet.iterator(); ((Iterator)localObject6).hasNext();) { localObject7 = (String)((Iterator)localObject6).next();
/* 711 */             debug.println((String)localObject7);
/*     */           }
/*     */         }
/* 714 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 719 */     for (int n = 0; n < paramArrayOfBoolean.length; n++) {
/* 720 */       paramArrayOfBoolean[n] = ((paramArrayOfBoolean[n] != 0) || ((n < localObject1.length) && (localObject1[n] != 0)) ? 1 : false);
/*     */     }
/*     */     
/*     */ 
/* 724 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static GeneralNames getFullNames(X500Name paramX500Name, RDN paramRDN)
/*     */     throws IOException
/*     */   {
/* 734 */     ArrayList localArrayList = new ArrayList(paramX500Name.rdns());
/* 735 */     localArrayList.add(paramRDN);
/* 736 */     X500Name localX500Name = new X500Name((RDN[])localArrayList.toArray(new RDN[0]));
/* 737 */     GeneralNames localGeneralNames = new GeneralNames();
/* 738 */     localGeneralNames.add(new GeneralName(localX500Name));
/* 739 */     return localGeneralNames;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean issues(X509CertImpl paramX509CertImpl, X509CRLImpl paramX509CRLImpl, String paramString)
/*     */     throws IOException
/*     */   {
/* 752 */     boolean bool = false;
/*     */     
/* 754 */     AdaptableX509CertSelector localAdaptableX509CertSelector = new AdaptableX509CertSelector();
/*     */     
/*     */ 
/*     */ 
/* 758 */     boolean[] arrayOfBoolean = paramX509CertImpl.getKeyUsage();
/* 759 */     if (arrayOfBoolean != null) {
/* 760 */       arrayOfBoolean[6] = true;
/* 761 */       localAdaptableX509CertSelector.setKeyUsage(arrayOfBoolean);
/*     */     }
/*     */     
/*     */ 
/* 765 */     X500Principal localX500Principal = paramX509CRLImpl.getIssuerX500Principal();
/* 766 */     localAdaptableX509CertSelector.setSubject(localX500Principal);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 776 */     AuthorityKeyIdentifierExtension localAuthorityKeyIdentifierExtension = paramX509CRLImpl.getAuthKeyIdExtension();
/* 777 */     localAdaptableX509CertSelector.setSkiAndSerialNumber(localAuthorityKeyIdentifierExtension);
/*     */     
/* 779 */     bool = localAdaptableX509CertSelector.match(paramX509CertImpl);
/*     */     
/*     */ 
/* 782 */     if ((bool) && ((localAuthorityKeyIdentifierExtension == null) || 
/* 783 */       (paramX509CertImpl.getAuthorityKeyIdentifierExtension() == null))) {
/*     */       try {
/* 785 */         paramX509CRLImpl.verify(paramX509CertImpl.getPublicKey(), paramString);
/* 786 */         bool = true;
/*     */       } catch (GeneralSecurityException localGeneralSecurityException) {
/* 788 */         bool = false;
/*     */       }
/*     */     }
/*     */     
/* 792 */     return bool;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\DistributionPointFetcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
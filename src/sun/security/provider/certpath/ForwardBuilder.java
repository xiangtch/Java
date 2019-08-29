/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXReason;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AccessDescription;
/*     */ import sun.security.x509.AuthorityInfoAccessExtension;
/*     */ import sun.security.x509.AuthorityKeyIdentifierExtension;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ import sun.security.x509.X500Name;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ForwardBuilder
/*     */   extends Builder
/*     */ {
/*  65 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   private final Set<X509Certificate> trustedCerts;
/*     */   private final Set<X500Principal> trustedSubjectDNs;
/*     */   private final Set<TrustAnchor> trustAnchors;
/*     */   private X509CertSelector eeSelector;
/*     */   private AdaptableX509CertSelector caSelector;
/*     */   private X509CertSelector caTargetSelector;
/*     */   TrustAnchor trustAnchor;
/*  73 */   private boolean searchAllCertStores = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   ForwardBuilder(PKIX.BuilderParams paramBuilderParams, boolean paramBoolean)
/*     */   {
/*  81 */     super(paramBuilderParams);
/*     */     
/*     */ 
/*  84 */     this.trustAnchors = paramBuilderParams.trustAnchors();
/*  85 */     this.trustedCerts = new HashSet(this.trustAnchors.size());
/*  86 */     this.trustedSubjectDNs = new HashSet(this.trustAnchors.size());
/*  87 */     for (TrustAnchor localTrustAnchor : this.trustAnchors) {
/*  88 */       X509Certificate localX509Certificate = localTrustAnchor.getTrustedCert();
/*  89 */       if (localX509Certificate != null) {
/*  90 */         this.trustedCerts.add(localX509Certificate);
/*  91 */         this.trustedSubjectDNs.add(localX509Certificate.getSubjectX500Principal());
/*     */       } else {
/*  93 */         this.trustedSubjectDNs.add(localTrustAnchor.getCA());
/*     */       }
/*     */     }
/*  96 */     this.searchAllCertStores = paramBoolean;
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
/*     */   Collection<X509Certificate> getMatchingCerts(State paramState, List<CertStore> paramList)
/*     */     throws CertStoreException, CertificateException, IOException
/*     */   {
/* 113 */     if (debug != null) {
/* 114 */       debug.println("ForwardBuilder.getMatchingCerts()...");
/*     */     }
/*     */     
/* 117 */     ForwardState localForwardState = (ForwardState)paramState;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 124 */     PKIXCertComparator localPKIXCertComparator = new PKIXCertComparator(this.trustedSubjectDNs, localForwardState.cert);
/*     */     
/* 126 */     TreeSet localTreeSet = new TreeSet(localPKIXCertComparator);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 131 */     if (localForwardState.isInitial()) {
/* 132 */       getMatchingEECerts(localForwardState, paramList, localTreeSet);
/*     */     }
/* 134 */     getMatchingCACerts(localForwardState, paramList, localTreeSet);
/*     */     
/* 136 */     return localTreeSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void getMatchingEECerts(ForwardState paramForwardState, List<CertStore> paramList, Collection<X509Certificate> paramCollection)
/*     */     throws IOException
/*     */   {
/* 148 */     if (debug != null) {
/* 149 */       debug.println("ForwardBuilder.getMatchingEECerts()...");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 159 */     if (this.eeSelector == null) {
/* 160 */       this.eeSelector = ((X509CertSelector)this.targetCertConstraints.clone());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 165 */       this.eeSelector.setCertificateValid(this.buildParams.date());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 170 */       if (this.buildParams.explicitPolicyRequired()) {
/* 171 */         this.eeSelector.setPolicy(getMatchingPolicies());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 176 */       this.eeSelector.setBasicConstraints(-2);
/*     */     }
/*     */     
/*     */ 
/* 180 */     addMatchingCerts(this.eeSelector, paramList, paramCollection, this.searchAllCertStores);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void getMatchingCACerts(ForwardState paramForwardState, List<CertStore> paramList, Collection<X509Certificate> paramCollection)
/*     */     throws IOException
/*     */   {
/* 192 */     if (debug != null) {
/* 193 */       debug.println("ForwardBuilder.getMatchingCACerts()...");
/*     */     }
/* 195 */     int i = paramCollection.size();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 201 */     Object localObject1 = null;
/*     */     
/* 203 */     if (paramForwardState.isInitial()) {
/* 204 */       if (this.targetCertConstraints.getBasicConstraints() == -2)
/*     */       {
/* 206 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 212 */       if (debug != null) {
/* 213 */         debug.println("ForwardBuilder.getMatchingCACerts(): the target is a CA");
/*     */       }
/*     */       
/*     */ 
/* 217 */       if (this.caTargetSelector == null)
/*     */       {
/* 219 */         this.caTargetSelector = ((X509CertSelector)this.targetCertConstraints.clone());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 231 */         if (this.buildParams.explicitPolicyRequired()) {
/* 232 */           this.caTargetSelector.setPolicy(getMatchingPolicies());
/*     */         }
/*     */       }
/* 235 */       localObject1 = this.caTargetSelector;
/*     */     }
/*     */     else {
/* 238 */       if (this.caSelector == null) {
/* 239 */         this.caSelector = new AdaptableX509CertSelector();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 251 */         if (this.buildParams.explicitPolicyRequired()) {
/* 252 */           this.caSelector.setPolicy(getMatchingPolicies());
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 258 */       this.caSelector.setSubject(paramForwardState.issuerDN);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 266 */       CertPathHelper.setPathToNames(this.caSelector, paramForwardState.subjectNamesTraversed);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 271 */       this.caSelector.setValidityPeriod(paramForwardState.cert.getNotBefore(), paramForwardState.cert
/* 272 */         .getNotAfter());
/*     */       
/* 274 */       localObject1 = this.caSelector;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 283 */     ((X509CertSelector)localObject1).setBasicConstraints(-1);
/*     */     
/* 285 */     for (Object localObject2 = this.trustedCerts.iterator(); ((Iterator)localObject2).hasNext();) { X509Certificate localX509Certificate = (X509Certificate)((Iterator)localObject2).next();
/* 286 */       if (((X509CertSelector)localObject1).match(localX509Certificate)) {
/* 287 */         if (debug != null) {
/* 288 */           debug.println("ForwardBuilder.getMatchingCACerts: found matching trust anchor.\n  SN: " + 
/*     */           
/*     */ 
/* 291 */             Debug.toHexString(localX509Certificate.getSerialNumber()) + "\n  Subject: " + localX509Certificate
/*     */             
/* 293 */             .getSubjectX500Principal() + "\n  Issuer: " + localX509Certificate
/*     */             
/* 295 */             .getIssuerX500Principal());
/*     */         }
/* 297 */         if ((paramCollection.add(localX509Certificate)) && (!this.searchAllCertStores)) {
/* 298 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 307 */     ((X509CertSelector)localObject1).setCertificateValid(this.buildParams.date());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 313 */     ((X509CertSelector)localObject1).setBasicConstraints(paramForwardState.traversedCACerts);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 322 */     if ((paramForwardState.isInitial()) || 
/* 323 */       (this.buildParams.maxPathLength() == -1) || 
/* 324 */       (this.buildParams.maxPathLength() > paramForwardState.traversedCACerts))
/*     */     {
/* 326 */       if ((addMatchingCerts((X509CertSelector)localObject1, paramList, paramCollection, this.searchAllCertStores)) && (!this.searchAllCertStores))
/*     */       {
/*     */ 
/* 329 */         return;
/*     */       }
/*     */     }
/*     */     
/* 333 */     if ((!paramForwardState.isInitial()) && (Builder.USE_AIA))
/*     */     {
/*     */ 
/* 336 */       localObject2 = paramForwardState.cert.getAuthorityInfoAccessExtension();
/* 337 */       if (localObject2 != null) {
/* 338 */         getCerts((AuthorityInfoAccessExtension)localObject2, paramCollection);
/*     */       }
/*     */     }
/*     */     
/* 342 */     if (debug != null) {
/* 343 */       int j = paramCollection.size() - i;
/* 344 */       debug.println("ForwardBuilder.getMatchingCACerts: found " + j + " CA certs");
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
/*     */   private boolean getCerts(AuthorityInfoAccessExtension paramAuthorityInfoAccessExtension, Collection<X509Certificate> paramCollection)
/*     */   {
/* 359 */     if (!Builder.USE_AIA) {
/* 360 */       return false;
/*     */     }
/* 362 */     List localList = paramAuthorityInfoAccessExtension.getAccessDescriptions();
/* 363 */     if ((localList == null) || (localList.isEmpty())) {
/* 364 */       return false;
/*     */     }
/*     */     
/* 367 */     boolean bool = false;
/* 368 */     for (AccessDescription localAccessDescription : localList) {
/* 369 */       CertStore localCertStore = URICertStore.getInstance(localAccessDescription);
/* 370 */       if (localCertStore != null) {
/*     */         try {
/* 372 */           if (paramCollection.addAll(localCertStore
/* 373 */             .getCertificates(this.caSelector))) {
/* 374 */             bool = true;
/* 375 */             if (!this.searchAllCertStores) {
/* 376 */               return true;
/*     */             }
/*     */           }
/*     */         } catch (CertStoreException localCertStoreException) {
/* 380 */           if (debug != null) {
/* 381 */             debug.println("exception getting certs from CertStore:");
/* 382 */             localCertStoreException.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 387 */     return bool;
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
/*     */   static class PKIXCertComparator
/*     */     implements Comparator<X509Certificate>
/*     */   {
/*     */     static final String METHOD_NME = "PKIXCertComparator.compare()";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private final Set<X500Principal> trustedSubjectDNs;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private final X509CertSelector certSkidSelector;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     PKIXCertComparator(Set<X500Principal> paramSet, X509CertImpl paramX509CertImpl)
/*     */       throws IOException
/*     */     {
/* 439 */       this.trustedSubjectDNs = paramSet;
/* 440 */       this.certSkidSelector = getSelector(paramX509CertImpl);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private X509CertSelector getSelector(X509CertImpl paramX509CertImpl)
/*     */       throws IOException
/*     */     {
/* 449 */       if (paramX509CertImpl != null)
/*     */       {
/* 451 */         AuthorityKeyIdentifierExtension localAuthorityKeyIdentifierExtension = paramX509CertImpl.getAuthorityKeyIdentifierExtension();
/* 452 */         if (localAuthorityKeyIdentifierExtension != null) {
/* 453 */           byte[] arrayOfByte = localAuthorityKeyIdentifierExtension.getEncodedKeyIdentifier();
/* 454 */           if (arrayOfByte != null) {
/* 455 */             X509CertSelector localX509CertSelector = new X509CertSelector();
/* 456 */             localX509CertSelector.setSubjectKeyIdentifier(arrayOfByte);
/* 457 */             return localX509CertSelector;
/*     */           }
/*     */         }
/*     */       }
/* 461 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public int compare(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2)
/*     */     {
/* 483 */       if (paramX509Certificate1.equals(paramX509Certificate2)) { return 0;
/*     */       }
/*     */       
/* 486 */       if (this.certSkidSelector != null) {
/* 487 */         if (this.certSkidSelector.match(paramX509Certificate1)) {
/* 488 */           return -1;
/*     */         }
/* 490 */         if (this.certSkidSelector.match(paramX509Certificate2)) {
/* 491 */           return 1;
/*     */         }
/*     */       }
/*     */       
/* 495 */       X500Principal localX500Principal1 = paramX509Certificate1.getIssuerX500Principal();
/* 496 */       X500Principal localX500Principal2 = paramX509Certificate2.getIssuerX500Principal();
/* 497 */       X500Name localX500Name1 = X500Name.asX500Name(localX500Principal1);
/* 498 */       X500Name localX500Name2 = X500Name.asX500Name(localX500Principal2);
/*     */       
/* 500 */       if (ForwardBuilder.debug != null) {
/* 501 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Issuer:  " + localX500Principal1);
/* 502 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Issuer:  " + localX500Principal2);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 508 */       if (ForwardBuilder.debug != null) {
/* 509 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() MATCH TRUSTED SUBJECT TEST...");
/*     */       }
/*     */       
/* 512 */       boolean bool1 = this.trustedSubjectDNs.contains(localX500Principal1);
/* 513 */       boolean bool2 = this.trustedSubjectDNs.contains(localX500Principal2);
/* 514 */       if (ForwardBuilder.debug != null) {
/* 515 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() m1: " + bool1);
/* 516 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() m2: " + bool2);
/*     */       }
/* 518 */       if ((bool1) && (bool2))
/* 519 */         return -1;
/* 520 */       if (bool1)
/* 521 */         return -1;
/* 522 */       if (bool2) {
/* 523 */         return 1;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 529 */       if (ForwardBuilder.debug != null) {
/* 530 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING DESCENDANT TEST...");
/*     */       }
/* 532 */       for (Object localObject = this.trustedSubjectDNs.iterator(); ((Iterator)localObject).hasNext();) { localX500Principal3 = (X500Principal)((Iterator)localObject).next();
/* 533 */         localX500Name3 = X500Name.asX500Name(localX500Principal3);
/*     */         
/* 535 */         i = Builder.distance(localX500Name3, localX500Name1, -1);
/*     */         
/* 537 */         j = Builder.distance(localX500Name3, localX500Name2, -1);
/* 538 */         if (ForwardBuilder.debug != null) {
/* 539 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + i);
/* 540 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + j);
/*     */         }
/* 542 */         if ((i > 0) || (j > 0)) {
/* 543 */           if (i == j)
/* 544 */             return -1;
/* 545 */           if ((i > 0) && (j <= 0))
/* 546 */             return -1;
/* 547 */           if ((i <= 0) && (j > 0))
/* 548 */             return 1;
/* 549 */           if (i < j) {
/* 550 */             return -1;
/*     */           }
/* 552 */           return 1;
/*     */         }
/*     */       }
/*     */       
/*     */       int i;
/*     */       
/*     */       int j;
/*     */       
/* 560 */       if (ForwardBuilder.debug != null) {
/* 561 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING ANCESTOR TEST...");
/*     */       }
/* 563 */       for (localObject = this.trustedSubjectDNs.iterator(); ((Iterator)localObject).hasNext();) { localX500Principal3 = (X500Principal)((Iterator)localObject).next();
/* 564 */         localX500Name3 = X500Name.asX500Name(localX500Principal3);
/*     */         
/*     */ 
/* 567 */         i = Builder.distance(localX500Name3, localX500Name1, Integer.MAX_VALUE);
/*     */         
/* 569 */         j = Builder.distance(localX500Name3, localX500Name2, Integer.MAX_VALUE);
/* 570 */         if (ForwardBuilder.debug != null) {
/* 571 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + i);
/* 572 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + j);
/*     */         }
/* 574 */         if ((i < 0) || (j < 0)) {
/* 575 */           if (i == j)
/* 576 */             return -1;
/* 577 */           if ((i < 0) && (j >= 0))
/* 578 */             return -1;
/* 579 */           if ((i >= 0) && (j < 0))
/* 580 */             return 1;
/* 581 */           if (i > j) {
/* 582 */             return -1;
/*     */           }
/* 584 */           return 1;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 593 */       if (ForwardBuilder.debug != null) {
/* 594 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() SAME NAMESPACE AS TRUSTED TEST...");
/*     */       }
/* 596 */       for (localObject = this.trustedSubjectDNs.iterator(); ((Iterator)localObject).hasNext();) { localX500Principal3 = (X500Principal)((Iterator)localObject).next();
/* 597 */         localX500Name3 = X500Name.asX500Name(localX500Principal3);
/* 598 */         localX500Name4 = localX500Name3.commonAncestor(localX500Name1);
/* 599 */         X500Name localX500Name5 = localX500Name3.commonAncestor(localX500Name2);
/* 600 */         if (ForwardBuilder.debug != null) {
/* 601 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo1: " + String.valueOf(localX500Name4));
/* 602 */           ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo2: " + String.valueOf(localX500Name5));
/*     */         }
/* 604 */         if ((localX500Name4 != null) || (localX500Name5 != null)) {
/* 605 */           if ((localX500Name4 != null) && (localX500Name5 != null))
/*     */           {
/* 607 */             m = Builder.hops(localX500Name3, localX500Name1, Integer.MAX_VALUE);
/*     */             
/* 609 */             int n = Builder.hops(localX500Name3, localX500Name2, Integer.MAX_VALUE);
/* 610 */             if (ForwardBuilder.debug != null) {
/* 611 */               ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto1: " + m);
/* 612 */               ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto2: " + n);
/*     */             }
/* 614 */             if (m != n) {
/* 615 */               if (m > n) {
/* 616 */                 return 1;
/*     */               }
/* 618 */               return -1;
/*     */             }
/* 620 */           } else { if (localX500Name4 == null) {
/* 621 */               return 1;
/*     */             }
/* 623 */             return -1;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 632 */       if (ForwardBuilder.debug != null) {
/* 633 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() CERT ISSUER/SUBJECT COMPARISON TEST...");
/*     */       }
/* 635 */       localObject = paramX509Certificate1.getSubjectX500Principal();
/* 636 */       X500Principal localX500Principal3 = paramX509Certificate2.getSubjectX500Principal();
/* 637 */       X500Name localX500Name3 = X500Name.asX500Name((X500Principal)localObject);
/* 638 */       X500Name localX500Name4 = X500Name.asX500Name(localX500Principal3);
/*     */       
/* 640 */       if (ForwardBuilder.debug != null) {
/* 641 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Subject: " + localObject);
/* 642 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Subject: " + localX500Principal3);
/*     */       }
/*     */       
/* 645 */       int k = Builder.distance(localX500Name3, localX500Name1, Integer.MAX_VALUE);
/*     */       
/* 647 */       int m = Builder.distance(localX500Name4, localX500Name2, Integer.MAX_VALUE);
/* 648 */       if (ForwardBuilder.debug != null) {
/* 649 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI1: " + k);
/* 650 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI2: " + m);
/*     */       }
/* 652 */       if (m > k)
/* 653 */         return -1;
/* 654 */       if (m < k) {
/* 655 */         return 1;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 660 */       if (ForwardBuilder.debug != null) {
/* 661 */         ForwardBuilder.debug.println("PKIXCertComparator.compare() no tests matched; RETURN 0");
/*     */       }
/* 663 */       return -1;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void verifyCert(X509Certificate paramX509Certificate, State paramState, List<X509Certificate> paramList)
/*     */     throws GeneralSecurityException
/*     */   {
/* 697 */     if (debug != null) {
/* 698 */       debug.println("ForwardBuilder.verifyCert(SN: " + 
/* 699 */         Debug.toHexString(paramX509Certificate.getSerialNumber()) + "\n  Issuer: " + paramX509Certificate
/* 700 */         .getIssuerX500Principal() + ")\n  Subject: " + paramX509Certificate
/* 701 */         .getSubjectX500Principal() + ")");
/*     */     }
/*     */     
/* 704 */     ForwardState localForwardState = (ForwardState)paramState;
/*     */     
/*     */ 
/* 707 */     localForwardState.untrustedChecker.check(paramX509Certificate, Collections.emptySet());
/*     */     
/*     */ 
/*     */     Iterator localIterator1;
/*     */     
/*     */ 
/* 713 */     if (paramList != null) {
/* 714 */       for (localIterator1 = paramList.iterator(); localIterator1.hasNext();) { localObject = (X509Certificate)localIterator1.next();
/* 715 */         if (paramX509Certificate.equals(localObject)) {
/* 716 */           if (debug != null) {
/* 717 */             debug.println("loop detected!!");
/*     */           }
/* 719 */           throw new CertPathValidatorException("loop detected");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     Object localObject;
/* 725 */     boolean bool = this.trustedCerts.contains(paramX509Certificate);
/*     */     
/*     */ 
/* 728 */     if (!bool)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 734 */       localObject = paramX509Certificate.getCriticalExtensionOIDs();
/* 735 */       if (localObject == null) {
/* 736 */         localObject = Collections.emptySet();
/*     */       }
/* 738 */       for (Iterator localIterator2 = localForwardState.forwardCheckers.iterator(); localIterator2.hasNext();) { localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator2.next();
/* 739 */         localPKIXCertPathChecker.check(paramX509Certificate, (Collection)localObject);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       PKIXCertPathChecker localPKIXCertPathChecker;
/*     */       
/*     */ 
/*     */ 
/* 748 */       for (localIterator2 = this.buildParams.certPathCheckers().iterator(); localIterator2.hasNext();) { localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator2.next();
/* 749 */         if (!localPKIXCertPathChecker.isForwardCheckingSupported()) {
/* 750 */           Set localSet = localPKIXCertPathChecker.getSupportedExtensions();
/* 751 */           if (localSet != null) {
/* 752 */             ((Set)localObject).removeAll(localSet);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 761 */       if (!((Set)localObject).isEmpty()) {
/* 762 */         ((Set)localObject).remove(PKIXExtensions.BasicConstraints_Id.toString());
/* 763 */         ((Set)localObject).remove(PKIXExtensions.NameConstraints_Id.toString());
/* 764 */         ((Set)localObject).remove(PKIXExtensions.CertificatePolicies_Id.toString());
/* 765 */         ((Set)localObject).remove(PKIXExtensions.PolicyMappings_Id.toString());
/* 766 */         ((Set)localObject).remove(PKIXExtensions.PolicyConstraints_Id.toString());
/* 767 */         ((Set)localObject).remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
/* 768 */         ((Set)localObject).remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
/* 769 */         ((Set)localObject).remove(PKIXExtensions.KeyUsage_Id.toString());
/* 770 */         ((Set)localObject).remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
/*     */         
/* 772 */         if (!((Set)localObject).isEmpty()) {
/* 773 */           throw new CertPathValidatorException("Unrecognized critical extension(s)", null, null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 783 */     if (localForwardState.isInitial()) {
/* 784 */       return;
/*     */     }
/*     */     
/*     */ 
/* 788 */     if (!bool)
/*     */     {
/* 790 */       if (paramX509Certificate.getBasicConstraints() == -1) {
/* 791 */         throw new CertificateException("cert is NOT a CA cert");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 797 */       KeyChecker.verifyCAKeyUsage(paramX509Certificate);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 811 */     if (!localForwardState.keyParamsNeeded()) {
/* 812 */       localForwardState.cert.verify(paramX509Certificate.getPublicKey(), this.buildParams
/* 813 */         .sigProvider());
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
/*     */ 
/*     */   boolean isPathCompleted(X509Certificate paramX509Certificate)
/*     */   {
/* 833 */     ArrayList localArrayList = new ArrayList();
/*     */     
/* 835 */     for (Iterator localIterator = this.trustAnchors.iterator(); localIterator.hasNext();) { localTrustAnchor = (TrustAnchor)localIterator.next();
/* 836 */       if (localTrustAnchor.getTrustedCert() != null) {
/* 837 */         if (paramX509Certificate.equals(localTrustAnchor.getTrustedCert())) {
/* 838 */           this.trustAnchor = localTrustAnchor;
/* 839 */           return true;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 844 */         localX500Principal = localTrustAnchor.getCA();
/* 845 */         localPublicKey = localTrustAnchor.getCAPublicKey();
/*     */         
/* 847 */         if ((localX500Principal != null) && (localPublicKey != null) && 
/* 848 */           (localX500Principal.equals(paramX509Certificate.getSubjectX500Principal())) && 
/* 849 */           (localPublicKey.equals(paramX509Certificate.getPublicKey())))
/*     */         {
/* 851 */           this.trustAnchor = localTrustAnchor;
/* 852 */           return true;
/*     */         }
/*     */         
/*     */ 
/* 856 */         localArrayList.add(localTrustAnchor); } }
/*     */     TrustAnchor localTrustAnchor;
/*     */     X500Principal localX500Principal;
/* 859 */     PublicKey localPublicKey; for (localIterator = localArrayList.iterator(); localIterator.hasNext();) { localTrustAnchor = (TrustAnchor)localIterator.next();
/* 860 */       localX500Principal = localTrustAnchor.getCA();
/* 861 */       localPublicKey = localTrustAnchor.getCAPublicKey();
/*     */       
/* 863 */       if ((localX500Principal != null) && 
/* 864 */         (localX500Principal.equals(paramX509Certificate.getIssuerX500Principal())) && 
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 869 */         (!PKIX.isDSAPublicKeyWithoutParams(localPublicKey)))
/*     */       {
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*     */ 
/* 877 */           paramX509Certificate.verify(localPublicKey, this.buildParams.sigProvider());
/*     */         } catch (InvalidKeyException localInvalidKeyException) {
/* 879 */           if (debug != null) {
/* 880 */             debug.println("ForwardBuilder.isPathCompleted() invalid DSA key found");
/*     */           }
/*     */           
/* 883 */           continue;
/*     */         } catch (GeneralSecurityException localGeneralSecurityException) {
/* 885 */           if (debug != null) {
/* 886 */             debug.println("ForwardBuilder.isPathCompleted() unexpected exception");
/*     */             
/* 888 */             localGeneralSecurityException.printStackTrace();
/*     */           } }
/* 890 */         continue;
/*     */         
/*     */ 
/* 893 */         this.trustAnchor = localTrustAnchor;
/* 894 */         return true;
/*     */       }
/*     */     }
/* 897 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void addCertToPath(X509Certificate paramX509Certificate, LinkedList<X509Certificate> paramLinkedList)
/*     */   {
/* 909 */     paramLinkedList.addFirst(paramX509Certificate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void removeFinalCertFromPath(LinkedList<X509Certificate> paramLinkedList)
/*     */   {
/* 918 */     paramLinkedList.removeFirst();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\ForwardBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
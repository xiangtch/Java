/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CertPathBuilderException;
/*     */ import java.security.cert.CertPathBuilderResult;
/*     */ import java.security.cert.CertPathBuilderSpi;
/*     */ import java.security.cert.CertPathChecker;
/*     */ import java.security.cert.CertPathParameters;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertPathValidatorException.BasicReason;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.PKIXCertPathBuilderResult;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXReason;
/*     */ import java.security.cert.PKIXRevocationChecker;
/*     */ import java.security.cert.PolicyNode;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SunCertPathBuilder
/*     */   extends CertPathBuilderSpi
/*     */ {
/*  69 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */   private PKIX.BuilderParams buildParams;
/*     */   
/*     */   private CertificateFactory cf;
/*     */   
/*  76 */   private boolean pathCompleted = false;
/*     */   
/*     */   private PolicyNode policyTreeResult;
/*     */   
/*     */   private TrustAnchor trustAnchor;
/*     */   private PublicKey finalPublicKey;
/*     */   
/*     */   public SunCertPathBuilder()
/*     */     throws CertPathBuilderException
/*     */   {
/*     */     try
/*     */     {
/*  88 */       this.cf = CertificateFactory.getInstance("X.509");
/*     */     } catch (CertificateException localCertificateException) {
/*  90 */       throw new CertPathBuilderException(localCertificateException);
/*     */     }
/*     */   }
/*     */   
/*     */   public CertPathChecker engineGetRevocationChecker()
/*     */   {
/*  96 */     return new RevocationChecker();
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
/*     */   public CertPathBuilderResult engineBuild(CertPathParameters paramCertPathParameters)
/*     */     throws CertPathBuilderException, InvalidAlgorithmParameterException
/*     */   {
/* 121 */     if (debug != null) {
/* 122 */       debug.println("SunCertPathBuilder.engineBuild(" + paramCertPathParameters + ")");
/*     */     }
/*     */     
/* 125 */     this.buildParams = PKIX.checkBuilderParams(paramCertPathParameters);
/* 126 */     return build();
/*     */   }
/*     */   
/*     */   private PKIXCertPathBuilderResult build() throws CertPathBuilderException {
/* 130 */     ArrayList localArrayList = new ArrayList();
/* 131 */     PKIXCertPathBuilderResult localPKIXCertPathBuilderResult = buildCertPath(false, localArrayList);
/* 132 */     if (localPKIXCertPathBuilderResult == null) {
/* 133 */       if (debug != null) {
/* 134 */         debug.println("SunCertPathBuilder.engineBuild: 2nd pass; try building again searching all certstores");
/*     */       }
/*     */       
/*     */ 
/* 138 */       localArrayList.clear();
/* 139 */       localPKIXCertPathBuilderResult = buildCertPath(true, localArrayList);
/* 140 */       if (localPKIXCertPathBuilderResult == null) {
/* 141 */         throw new SunCertPathBuilderException("unable to find valid certification path to requested target", new AdjacencyList(localArrayList));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 146 */     return localPKIXCertPathBuilderResult;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private PKIXCertPathBuilderResult buildCertPath(boolean paramBoolean, List<List<Vertex>> paramList)
/*     */     throws CertPathBuilderException
/*     */   {
/* 154 */     this.pathCompleted = false;
/* 155 */     this.trustAnchor = null;
/* 156 */     this.finalPublicKey = null;
/* 157 */     this.policyTreeResult = null;
/* 158 */     LinkedList localLinkedList = new LinkedList();
/*     */     try {
/* 160 */       buildForward(paramList, localLinkedList, paramBoolean);
/*     */     } catch (GeneralSecurityException|IOException localGeneralSecurityException) {
/* 162 */       if (debug != null) {
/* 163 */         debug.println("SunCertPathBuilder.engineBuild() exception in build");
/*     */         
/* 165 */         localGeneralSecurityException.printStackTrace();
/*     */       }
/* 167 */       throw new SunCertPathBuilderException("unable to find valid certification path to requested target", localGeneralSecurityException, new AdjacencyList(paramList));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 174 */       if (this.pathCompleted) {
/* 175 */         if (debug != null) {
/* 176 */           debug.println("SunCertPathBuilder.engineBuild() pathCompleted");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 182 */         Collections.reverse(localLinkedList);
/*     */         
/* 184 */         return new SunCertPathBuilderResult(this.cf
/* 185 */           .generateCertPath(localLinkedList), this.trustAnchor, this.policyTreeResult, this.finalPublicKey, new AdjacencyList(paramList));
/*     */       }
/*     */     }
/*     */     catch (CertificateException localCertificateException)
/*     */     {
/* 190 */       if (debug != null) {
/* 191 */         debug.println("SunCertPathBuilder.engineBuild() exception in wrap-up");
/*     */         
/* 193 */         localCertificateException.printStackTrace();
/*     */       }
/* 195 */       throw new SunCertPathBuilderException("unable to find valid certification path to requested target", localCertificateException, new AdjacencyList(paramList));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 200 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void buildForward(List<List<Vertex>> paramList, LinkedList<X509Certificate> paramLinkedList, boolean paramBoolean)
/*     */     throws GeneralSecurityException, IOException
/*     */   {
/* 211 */     if (debug != null) {
/* 212 */       debug.println("SunCertPathBuilder.buildForward()...");
/*     */     }
/*     */     
/*     */ 
/* 216 */     ForwardState localForwardState = new ForwardState();
/* 217 */     localForwardState.initState(this.buildParams.certPathCheckers());
/*     */     
/*     */ 
/* 220 */     paramList.clear();
/* 221 */     paramList.add(new LinkedList());
/*     */     
/* 223 */     localForwardState.untrustedChecker = new UntrustedChecker();
/*     */     
/* 225 */     depthFirstSearchForward(this.buildParams.targetSubject(), localForwardState, new ForwardBuilder(this.buildParams, paramBoolean), paramList, paramLinkedList);
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
/*     */   private void depthFirstSearchForward(X500Principal paramX500Principal, ForwardState paramForwardState, ForwardBuilder paramForwardBuilder, List<List<Vertex>> paramList, LinkedList<X509Certificate> paramLinkedList)
/*     */     throws GeneralSecurityException, IOException
/*     */   {
/* 253 */     if (debug != null) {
/* 254 */       debug.println("SunCertPathBuilder.depthFirstSearchForward(" + paramX500Principal + ", " + paramForwardState
/* 255 */         .toString() + ")");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 263 */     Collection localCollection = paramForwardBuilder.getMatchingCerts(paramForwardState, this.buildParams.certStores());
/* 264 */     List localList = addVertices(localCollection, paramList);
/* 265 */     if (debug != null) {
/* 266 */       debug.println("SunCertPathBuilder.depthFirstSearchForward(): certs.size=" + localList
/* 267 */         .size());
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
/* 278 */     for (Vertex localVertex : localList)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 286 */       ForwardState localForwardState = (ForwardState)paramForwardState.clone();
/* 287 */       X509Certificate localX509Certificate = localVertex.getCertificate();
/*     */       try
/*     */       {
/* 290 */         paramForwardBuilder.verifyCert(localX509Certificate, localForwardState, paramLinkedList);
/*     */       } catch (GeneralSecurityException localGeneralSecurityException) {
/* 292 */         if (debug != null) {
/* 293 */           debug.println("SunCertPathBuilder.depthFirstSearchForward(): validation failed: " + localGeneralSecurityException);
/*     */           
/* 295 */           localGeneralSecurityException.printStackTrace();
/*     */         }
/* 297 */         localVertex.setThrowable(localGeneralSecurityException); }
/* 298 */       continue;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 310 */       if (paramForwardBuilder.isPathCompleted(localX509Certificate))
/*     */       {
/* 312 */         if (debug != null) {
/* 313 */           debug.println("SunCertPathBuilder.depthFirstSearchForward(): commencing final verification");
/*     */         }
/*     */         
/* 316 */         ArrayList localArrayList1 = new ArrayList(paramLinkedList);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 324 */         if (paramForwardBuilder.trustAnchor.getTrustedCert() == null) {
/* 325 */           localArrayList1.add(0, localX509Certificate);
/*     */         }
/*     */         
/*     */ 
/* 329 */         Set localSet1 = Collections.singleton("2.5.29.32.0");
/*     */         
/* 331 */         PolicyNodeImpl localPolicyNodeImpl = new PolicyNodeImpl(null, "2.5.29.32.0", null, false, localSet1, false);
/*     */         
/*     */ 
/* 334 */         ArrayList localArrayList2 = new ArrayList();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 341 */         PolicyChecker localPolicyChecker = new PolicyChecker(this.buildParams.initialPolicies(), localArrayList1.size(), this.buildParams.explicitPolicyRequired(), this.buildParams.policyMappingInhibited(), this.buildParams.anyPolicyInhibited(), this.buildParams.policyQualifiersRejected(), localPolicyNodeImpl);
/*     */         
/* 343 */         localArrayList2.add(localPolicyChecker);
/*     */         
/*     */ 
/* 346 */         localArrayList2.add(new AlgorithmChecker(paramForwardBuilder.trustAnchor, this.buildParams
/* 347 */           .date(), this.buildParams.variant()));
/*     */         
/* 349 */         BasicChecker localBasicChecker = null;
/* 350 */         if (localForwardState.keyParamsNeeded()) {
/* 351 */           PublicKey localPublicKey = localX509Certificate.getPublicKey();
/* 352 */           if (paramForwardBuilder.trustAnchor.getTrustedCert() == null) {
/* 353 */             localPublicKey = paramForwardBuilder.trustAnchor.getCAPublicKey();
/* 354 */             if (debug != null) {
/* 355 */               debug.println("SunCertPathBuilder.depthFirstSearchForward using buildParams public key: " + localPublicKey
/*     */               
/*     */ 
/* 358 */                 .toString());
/*     */             }
/*     */           }
/* 361 */           localObject1 = new TrustAnchor(localX509Certificate.getSubjectX500Principal(), localPublicKey, null);
/*     */           
/*     */ 
/*     */ 
/* 365 */           localBasicChecker = new BasicChecker((TrustAnchor)localObject1, this.buildParams.date(), this.buildParams.sigProvider(), true);
/*     */           
/* 367 */           localArrayList2.add(localBasicChecker);
/*     */         }
/*     */         
/* 370 */         this.buildParams.setCertPath(this.cf.generateCertPath(localArrayList1));
/*     */         
/* 372 */         int i = 0;
/* 373 */         Object localObject1 = this.buildParams.certPathCheckers();
/* 374 */         for (Iterator localIterator2 = ((List)localObject1).iterator(); localIterator2.hasNext();) { localObject3 = (PKIXCertPathChecker)localIterator2.next();
/* 375 */           if ((localObject3 instanceof PKIXRevocationChecker)) {
/* 376 */             if (i != 0) {
/* 377 */               throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified");
/*     */             }
/*     */             
/* 380 */             i = 1;
/*     */             
/* 382 */             if ((localObject3 instanceof RevocationChecker)) {
/* 383 */               ((RevocationChecker)localObject3).init(paramForwardBuilder.trustAnchor, this.buildParams);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */         Object localObject3;
/*     */         
/* 390 */         if ((this.buildParams.revocationEnabled()) && (i == 0)) {
/* 391 */           localArrayList2.add(new RevocationChecker(paramForwardBuilder.trustAnchor, this.buildParams));
/*     */         }
/*     */         
/*     */ 
/* 395 */         localArrayList2.addAll((Collection)localObject1);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 400 */         for (int j = 0;; j++) { if (j >= localArrayList1.size()) break label1163;
/* 401 */           localObject3 = (X509Certificate)localArrayList1.get(j);
/* 402 */           if (debug != null) {
/* 403 */             debug.println("current subject = " + ((X509Certificate)localObject3)
/* 404 */               .getSubjectX500Principal());
/*     */           }
/* 406 */           Set localSet2 = ((X509Certificate)localObject3).getCriticalExtensionOIDs();
/* 407 */           if (localSet2 == null) {
/* 408 */             localSet2 = Collections.emptySet();
/*     */           }
/*     */           
/* 411 */           Iterator localIterator3 = localArrayList2.iterator(); PKIXCertPathChecker localPKIXCertPathChecker; for (;;) { if (!localIterator3.hasNext()) break label926; localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator3.next();
/* 412 */             if (!localPKIXCertPathChecker.isForwardCheckingSupported()) {
/* 413 */               if (j == 0) {
/* 414 */                 localPKIXCertPathChecker.init(false);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 419 */                 if ((localPKIXCertPathChecker instanceof AlgorithmChecker))
/*     */                 {
/* 421 */                   ((AlgorithmChecker)localPKIXCertPathChecker).trySetTrustAnchor(paramForwardBuilder.trustAnchor);
/*     */                 }
/*     */               }
/*     */               try
/*     */               {
/* 426 */                 localPKIXCertPathChecker.check((Certificate)localObject3, localSet2);
/*     */               } catch (CertPathValidatorException localCertPathValidatorException) {
/* 428 */                 if (debug != null)
/*     */                 {
/* 430 */                   debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification failed: " + localCertPathValidatorException);
/*     */                 }
/*     */                 
/*     */ 
/* 434 */                 if ((this.buildParams.targetCertConstraints().match((Certificate)localObject3)) && 
/* 435 */                   (localCertPathValidatorException.getReason() == BasicReason.REVOKED)) {
/* 436 */                   throw localCertPathValidatorException;
/*     */                 }
/* 438 */                 localVertex.setThrowable(localCertPathValidatorException); }
/* 439 */               break;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 451 */           for (localIterator3 = this.buildParams.certPathCheckers().iterator(); localIterator3.hasNext();) { localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator3.next();
/*     */             
/* 453 */             if (localPKIXCertPathChecker.isForwardCheckingSupported())
/*     */             {
/* 455 */               Set localSet3 = localPKIXCertPathChecker.getSupportedExtensions();
/* 456 */               if (localSet3 != null) {
/* 457 */                 localSet2.removeAll(localSet3);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 462 */           if (!localSet2.isEmpty()) {
/* 463 */             localSet2.remove(PKIXExtensions.BasicConstraints_Id.toString());
/* 464 */             localSet2.remove(PKIXExtensions.NameConstraints_Id.toString());
/* 465 */             localSet2.remove(PKIXExtensions.CertificatePolicies_Id.toString());
/* 466 */             localSet2.remove(PKIXExtensions.PolicyMappings_Id.toString());
/* 467 */             localSet2.remove(PKIXExtensions.PolicyConstraints_Id.toString());
/* 468 */             localSet2.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
/* 469 */             localSet2.remove(PKIXExtensions.SubjectAlternativeName_Id
/* 470 */               .toString());
/* 471 */             localSet2.remove(PKIXExtensions.KeyUsage_Id.toString());
/* 472 */             localSet2.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
/*     */             
/* 474 */             if (!localSet2.isEmpty()) {
/* 475 */               throw new CertPathValidatorException("unrecognized critical extension(s)", null, null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 481 */         if (debug != null) {
/* 482 */           debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification succeeded - path completed!");
/*     */         }
/* 484 */         this.pathCompleted = true;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 491 */         if (paramForwardBuilder.trustAnchor.getTrustedCert() == null) {
/* 492 */           paramForwardBuilder.addCertToPath(localX509Certificate, paramLinkedList);
/*     */         }
/* 494 */         this.trustAnchor = paramForwardBuilder.trustAnchor;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 499 */         if (localBasicChecker != null) {
/* 500 */           this.finalPublicKey = localBasicChecker.getPublicKey();
/*     */         } else {
/*     */           Object localObject2;
/* 503 */           if (paramLinkedList.isEmpty()) {
/* 504 */             localObject2 = paramForwardBuilder.trustAnchor.getTrustedCert();
/*     */           } else {
/* 506 */             localObject2 = (Certificate)paramLinkedList.getLast();
/*     */           }
/* 508 */           this.finalPublicKey = ((Certificate)localObject2).getPublicKey();
/*     */         }
/*     */         
/* 511 */         this.policyTreeResult = localPolicyChecker.getPolicyTree();
/* 512 */         return;
/*     */       }
/* 514 */       paramForwardBuilder.addCertToPath(localX509Certificate, paramLinkedList);
/*     */       
/*     */ 
/*     */ 
/* 518 */       localForwardState.updateState(localX509Certificate);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 524 */       paramList.add(new LinkedList());
/* 525 */       localVertex.setIndex(paramList.size() - 1);
/*     */       
/*     */ 
/* 528 */       depthFirstSearchForward(localX509Certificate.getIssuerX500Principal(), localForwardState, paramForwardBuilder, paramList, paramLinkedList);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 534 */       if (this.pathCompleted) {
/* 535 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 543 */       if (debug != null) {
/* 544 */         debug.println("SunCertPathBuilder.depthFirstSearchForward(): backtracking");
/*     */       }
/* 546 */       paramForwardBuilder.removeFinalCertFromPath(paramLinkedList); }
/*     */     label926:
/*     */     label1163:
/* 549 */     return;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static List<Vertex> addVertices(Collection<X509Certificate> paramCollection, List<List<Vertex>> paramList)
/*     */   {
/* 558 */     List localList = (List)paramList.get(paramList.size() - 1);
/*     */     
/* 560 */     for (X509Certificate localX509Certificate : paramCollection) {
/* 561 */       Vertex localVertex = new Vertex(localX509Certificate);
/* 562 */       localList.add(localVertex);
/*     */     }
/*     */     
/* 565 */     return localList;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean anchorIsTarget(TrustAnchor paramTrustAnchor, CertSelector paramCertSelector)
/*     */   {
/* 575 */     X509Certificate localX509Certificate = paramTrustAnchor.getTrustedCert();
/* 576 */     if (localX509Certificate != null) {
/* 577 */       return paramCertSelector.match(localX509Certificate);
/*     */     }
/* 579 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\SunCertPathBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
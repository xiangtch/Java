/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXReason;
/*     */ import java.security.cert.PolicyNode;
/*     */ import java.security.cert.PolicyQualifierInfo;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.CertificatePoliciesExtension;
/*     */ import sun.security.x509.CertificatePolicyId;
/*     */ import sun.security.x509.CertificatePolicyMap;
/*     */ import sun.security.x509.InhibitAnyPolicyExtension;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ import sun.security.x509.PolicyConstraintsExtension;
/*     */ import sun.security.x509.PolicyInformation;
/*     */ import sun.security.x509.PolicyMappingsExtension;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ class PolicyChecker
/*     */   extends PKIXCertPathChecker
/*     */ {
/*     */   private final Set<String> initPolicies;
/*     */   private final int certPathLen;
/*     */   private final boolean expPolicyRequired;
/*     */   private final boolean polMappingInhibited;
/*     */   private final boolean anyPolicyInhibited;
/*     */   private final boolean rejectPolicyQualifiers;
/*     */   private PolicyNodeImpl rootNode;
/*     */   private int explicitPolicy;
/*     */   private int policyMapping;
/*     */   private int inhibitAnyPolicy;
/*     */   private int certIndex;
/*     */   private Set<String> supportedExts;
/*  74 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final String ANY_POLICY = "2.5.29.32.0";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   PolicyChecker(Set<String> paramSet, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, PolicyNodeImpl paramPolicyNodeImpl)
/*     */   {
/*  93 */     if (paramSet.isEmpty())
/*     */     {
/*     */ 
/*  96 */       this.initPolicies = new HashSet(1);
/*  97 */       this.initPolicies.add("2.5.29.32.0");
/*     */     } else {
/*  99 */       this.initPolicies = new HashSet(paramSet);
/*     */     }
/* 101 */     this.certPathLen = paramInt;
/* 102 */     this.expPolicyRequired = paramBoolean1;
/* 103 */     this.polMappingInhibited = paramBoolean2;
/* 104 */     this.anyPolicyInhibited = paramBoolean3;
/* 105 */     this.rejectPolicyQualifiers = paramBoolean4;
/* 106 */     this.rootNode = paramPolicyNodeImpl;
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
/*     */   public void init(boolean paramBoolean)
/*     */     throws CertPathValidatorException
/*     */   {
/* 120 */     if (paramBoolean) {
/* 121 */       throw new CertPathValidatorException("forward checking not supported");
/*     */     }
/*     */     
/*     */ 
/* 125 */     this.certIndex = 1;
/* 126 */     this.explicitPolicy = (this.expPolicyRequired ? 0 : this.certPathLen + 1);
/* 127 */     this.policyMapping = (this.polMappingInhibited ? 0 : this.certPathLen + 1);
/* 128 */     this.inhibitAnyPolicy = (this.anyPolicyInhibited ? 0 : this.certPathLen + 1);
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
/*     */   public boolean isForwardCheckingSupported()
/*     */   {
/* 141 */     return false;
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
/*     */   public Set<String> getSupportedExtensions()
/*     */   {
/* 156 */     if (this.supportedExts == null) {
/* 157 */       this.supportedExts = new HashSet(4);
/* 158 */       this.supportedExts.add(PKIXExtensions.CertificatePolicies_Id.toString());
/* 159 */       this.supportedExts.add(PKIXExtensions.PolicyMappings_Id.toString());
/* 160 */       this.supportedExts.add(PKIXExtensions.PolicyConstraints_Id.toString());
/* 161 */       this.supportedExts.add(PKIXExtensions.InhibitAnyPolicy_Id.toString());
/* 162 */       this.supportedExts = Collections.unmodifiableSet(this.supportedExts);
/*     */     }
/* 164 */     return this.supportedExts;
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
/*     */   public void check(Certificate paramCertificate, Collection<String> paramCollection)
/*     */     throws CertPathValidatorException
/*     */   {
/* 180 */     checkPolicy((X509Certificate)paramCertificate);
/*     */     
/* 182 */     if ((paramCollection != null) && (!paramCollection.isEmpty())) {
/* 183 */       paramCollection.remove(PKIXExtensions.CertificatePolicies_Id.toString());
/* 184 */       paramCollection.remove(PKIXExtensions.PolicyMappings_Id.toString());
/* 185 */       paramCollection.remove(PKIXExtensions.PolicyConstraints_Id.toString());
/* 186 */       paramCollection.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
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
/*     */   private void checkPolicy(X509Certificate paramX509Certificate)
/*     */     throws CertPathValidatorException
/*     */   {
/* 200 */     String str = "certificate policies";
/* 201 */     if (debug != null) {
/* 202 */       debug.println("PolicyChecker.checkPolicy() ---checking " + str + "...");
/*     */       
/* 204 */       debug.println("PolicyChecker.checkPolicy() certIndex = " + this.certIndex);
/*     */       
/* 206 */       debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: explicitPolicy = " + this.explicitPolicy);
/*     */       
/* 208 */       debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyMapping = " + this.policyMapping);
/*     */       
/* 210 */       debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
/*     */       
/* 212 */       debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyTree = " + this.rootNode);
/*     */     }
/*     */     
/*     */ 
/* 216 */     X509CertImpl localX509CertImpl = null;
/*     */     try {
/* 218 */       localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
/*     */     } catch (CertificateException localCertificateException) {
/* 220 */       throw new CertPathValidatorException(localCertificateException);
/*     */     }
/*     */     
/* 223 */     boolean bool = this.certIndex == this.certPathLen;
/*     */     
/* 225 */     this.rootNode = processPolicies(this.certIndex, this.initPolicies, this.explicitPolicy, this.policyMapping, this.inhibitAnyPolicy, this.rejectPolicyQualifiers, this.rootNode, localX509CertImpl, bool);
/*     */     
/*     */ 
/*     */ 
/* 229 */     if (!bool) {
/* 230 */       this.explicitPolicy = mergeExplicitPolicy(this.explicitPolicy, localX509CertImpl, bool);
/*     */       
/* 232 */       this.policyMapping = mergePolicyMapping(this.policyMapping, localX509CertImpl);
/* 233 */       this.inhibitAnyPolicy = mergeInhibitAnyPolicy(this.inhibitAnyPolicy, localX509CertImpl);
/*     */     }
/*     */     
/*     */ 
/* 237 */     this.certIndex += 1;
/*     */     
/* 239 */     if (debug != null) {
/* 240 */       debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: explicitPolicy = " + this.explicitPolicy);
/*     */       
/* 242 */       debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyMapping = " + this.policyMapping);
/*     */       
/* 244 */       debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
/*     */       
/* 246 */       debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyTree = " + this.rootNode);
/*     */       
/* 248 */       debug.println("PolicyChecker.checkPolicy() " + str + " verified");
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
/*     */   static int mergeExplicitPolicy(int paramInt, X509CertImpl paramX509CertImpl, boolean paramBoolean)
/*     */     throws CertPathValidatorException
/*     */   {
/* 270 */     if ((paramInt > 0) && (!X509CertImpl.isSelfIssued(paramX509CertImpl))) {
/* 271 */       paramInt--;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 276 */       PolicyConstraintsExtension localPolicyConstraintsExtension = paramX509CertImpl.getPolicyConstraintsExtension();
/* 277 */       if (localPolicyConstraintsExtension == null) {
/* 278 */         return paramInt;
/*     */       }
/* 280 */       int i = localPolicyConstraintsExtension.get("require").intValue();
/* 281 */       if (debug != null) {
/* 282 */         debug.println("PolicyChecker.mergeExplicitPolicy() require Index from cert = " + i);
/*     */       }
/*     */       
/* 285 */       if (!paramBoolean) {
/* 286 */         if ((i != -1) && (
/* 287 */           (paramInt == -1) || (i < paramInt))) {
/* 288 */           paramInt = i;
/*     */         }
/*     */         
/*     */       }
/* 292 */       else if (i == 0) {
/* 293 */         paramInt = i;
/*     */       }
/*     */     } catch (IOException localIOException) {
/* 296 */       if (debug != null) {
/* 297 */         debug.println("PolicyChecker.mergeExplicitPolicy unexpected exception");
/*     */         
/* 299 */         localIOException.printStackTrace();
/*     */       }
/* 301 */       throw new CertPathValidatorException(localIOException);
/*     */     }
/*     */     
/* 304 */     return paramInt;
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
/*     */   static int mergePolicyMapping(int paramInt, X509CertImpl paramX509CertImpl)
/*     */     throws CertPathValidatorException
/*     */   {
/* 323 */     if ((paramInt > 0) && (!X509CertImpl.isSelfIssued(paramX509CertImpl))) {
/* 324 */       paramInt--;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 329 */       PolicyConstraintsExtension localPolicyConstraintsExtension = paramX509CertImpl.getPolicyConstraintsExtension();
/* 330 */       if (localPolicyConstraintsExtension == null) {
/* 331 */         return paramInt;
/*     */       }
/*     */       
/* 334 */       int i = localPolicyConstraintsExtension.get("inhibit").intValue();
/* 335 */       if (debug != null) {
/* 336 */         debug.println("PolicyChecker.mergePolicyMapping() inhibit Index from cert = " + i);
/*     */       }
/*     */       
/* 339 */       if ((i != -1) && (
/* 340 */         (paramInt == -1) || (i < paramInt))) {
/* 341 */         paramInt = i;
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException) {
/* 345 */       if (debug != null) {
/* 346 */         debug.println("PolicyChecker.mergePolicyMapping unexpected exception");
/*     */         
/* 348 */         localIOException.printStackTrace();
/*     */       }
/* 350 */       throw new CertPathValidatorException(localIOException);
/*     */     }
/*     */     
/* 353 */     return paramInt;
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
/*     */   static int mergeInhibitAnyPolicy(int paramInt, X509CertImpl paramX509CertImpl)
/*     */     throws CertPathValidatorException
/*     */   {
/* 371 */     if ((paramInt > 0) && (!X509CertImpl.isSelfIssued(paramX509CertImpl))) {
/* 372 */       paramInt--;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 377 */       InhibitAnyPolicyExtension localInhibitAnyPolicyExtension = (InhibitAnyPolicyExtension)paramX509CertImpl.getExtension(PKIXExtensions.InhibitAnyPolicy_Id);
/* 378 */       if (localInhibitAnyPolicyExtension == null) {
/* 379 */         return paramInt;
/*     */       }
/*     */       
/* 382 */       int i = localInhibitAnyPolicyExtension.get("skip_certs").intValue();
/* 383 */       if (debug != null) {
/* 384 */         debug.println("PolicyChecker.mergeInhibitAnyPolicy() skipCerts Index from cert = " + i);
/*     */       }
/*     */       
/* 387 */       if ((i != -1) && 
/* 388 */         (i < paramInt)) {
/* 389 */         paramInt = i;
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException) {
/* 393 */       if (debug != null) {
/* 394 */         debug.println("PolicyChecker.mergeInhibitAnyPolicy unexpected exception");
/*     */         
/* 396 */         localIOException.printStackTrace();
/*     */       }
/* 398 */       throw new CertPathValidatorException(localIOException);
/*     */     }
/*     */     
/* 401 */     return paramInt;
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
/*     */   static PolicyNodeImpl processPolicies(int paramInt1, Set<String> paramSet, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, PolicyNodeImpl paramPolicyNodeImpl, X509CertImpl paramX509CertImpl, boolean paramBoolean2)
/*     */     throws CertPathValidatorException
/*     */   {
/* 431 */     boolean bool1 = false;
/*     */     
/* 433 */     PolicyNodeImpl localPolicyNodeImpl = null;
/* 434 */     Object localObject = new HashSet();
/*     */     
/* 436 */     if (paramPolicyNodeImpl == null) {
/* 437 */       localPolicyNodeImpl = null;
/*     */     } else {
/* 439 */       localPolicyNodeImpl = paramPolicyNodeImpl.copyTree();
/*     */     }
/*     */     
/*     */ 
/* 443 */     CertificatePoliciesExtension localCertificatePoliciesExtension = paramX509CertImpl.getCertificatePoliciesExtension();
/*     */     
/*     */ 
/* 446 */     if ((localCertificatePoliciesExtension != null) && (localPolicyNodeImpl != null)) {
/* 447 */       bool1 = localCertificatePoliciesExtension.isCritical();
/* 448 */       if (debug != null) {
/* 449 */         debug.println("PolicyChecker.processPolicies() policiesCritical = " + bool1);
/*     */       }
/*     */       List localList;
/*     */       try {
/* 453 */         localList = localCertificatePoliciesExtension.get("policies");
/*     */       } catch (IOException localIOException) {
/* 455 */         throw new CertPathValidatorException("Exception while retrieving policyOIDs", localIOException);
/*     */       }
/*     */       
/*     */ 
/* 459 */       if (debug != null) {
/* 460 */         debug.println("PolicyChecker.processPolicies() rejectPolicyQualifiers = " + paramBoolean1);
/*     */       }
/*     */       
/* 463 */       int i = 0;
/*     */       
/*     */ 
/* 466 */       for (PolicyInformation localPolicyInformation : localList)
/*     */       {
/* 468 */         String str = localPolicyInformation.getPolicyIdentifier().getIdentifier().toString();
/*     */         
/* 470 */         if (str.equals("2.5.29.32.0")) {
/* 471 */           i = 1;
/* 472 */           localObject = localPolicyInformation.getPolicyQualifiers();
/*     */         }
/*     */         else {
/* 475 */           if (debug != null) {
/* 476 */             debug.println("PolicyChecker.processPolicies() processing policy: " + str);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 481 */           Set localSet = localPolicyInformation.getPolicyQualifiers();
/*     */           
/*     */ 
/*     */ 
/* 485 */           if ((!localSet.isEmpty()) && (paramBoolean1) && (bool1))
/*     */           {
/* 487 */             throw new CertPathValidatorException("critical policy qualifiers present in certificate", null, null, -1, PKIXReason.INVALID_POLICY);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 493 */           boolean bool2 = processParents(paramInt1, bool1, paramBoolean1, localPolicyNodeImpl, str, localSet, false);
/*     */           
/*     */ 
/*     */ 
/* 497 */           if (!bool2)
/*     */           {
/* 499 */             processParents(paramInt1, bool1, paramBoolean1, localPolicyNodeImpl, str, localSet, true);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 507 */       if ((i != 0) && (
/* 508 */         (paramInt4 > 0) || ((!paramBoolean2) && 
/* 509 */         (X509CertImpl.isSelfIssued(paramX509CertImpl))))) {
/* 510 */         if (debug != null) {
/* 511 */           debug.println("PolicyChecker.processPolicies() processing policy: 2.5.29.32.0");
/*     */         }
/*     */         
/* 514 */         processParents(paramInt1, bool1, paramBoolean1, localPolicyNodeImpl, "2.5.29.32.0", (Set)localObject, true);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 521 */       localPolicyNodeImpl.prune(paramInt1);
/* 522 */       if (!localPolicyNodeImpl.getChildren().hasNext()) {
/* 523 */         localPolicyNodeImpl = null;
/*     */       }
/* 525 */     } else if (localCertificatePoliciesExtension == null) {
/* 526 */       if (debug != null) {
/* 527 */         debug.println("PolicyChecker.processPolicies() no policies present in cert");
/*     */       }
/*     */       
/* 530 */       localPolicyNodeImpl = null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 536 */     if ((localPolicyNodeImpl != null) && 
/* 537 */       (!paramBoolean2))
/*     */     {
/* 539 */       localPolicyNodeImpl = processPolicyMappings(paramX509CertImpl, paramInt1, paramInt3, localPolicyNodeImpl, bool1, (Set)localObject);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 548 */     if ((localPolicyNodeImpl != null) && (!paramSet.contains("2.5.29.32.0")) && (localCertificatePoliciesExtension != null))
/*     */     {
/* 550 */       localPolicyNodeImpl = removeInvalidNodes(localPolicyNodeImpl, paramInt1, paramSet, localCertificatePoliciesExtension);
/*     */       
/*     */ 
/*     */ 
/* 554 */       if ((localPolicyNodeImpl != null) && (paramBoolean2))
/*     */       {
/* 556 */         localPolicyNodeImpl = rewriteLeafNodes(paramInt1, paramSet, localPolicyNodeImpl);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 561 */     if (paramBoolean2)
/*     */     {
/* 563 */       paramInt2 = mergeExplicitPolicy(paramInt2, paramX509CertImpl, paramBoolean2);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 571 */     if ((paramInt2 == 0) && (localPolicyNodeImpl == null)) {
/* 572 */       throw new CertPathValidatorException("non-null policy tree required and policy tree is null", null, null, -1, PKIXReason.INVALID_POLICY);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 577 */     return localPolicyNodeImpl;
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
/*     */   private static PolicyNodeImpl rewriteLeafNodes(int paramInt, Set<String> paramSet, PolicyNodeImpl paramPolicyNodeImpl)
/*     */   {
/* 597 */     Set localSet1 = paramPolicyNodeImpl.getPolicyNodesValid(paramInt, "2.5.29.32.0");
/* 598 */     if (localSet1.isEmpty()) {
/* 599 */       return paramPolicyNodeImpl;
/*     */     }
/* 601 */     PolicyNodeImpl localPolicyNodeImpl1 = (PolicyNodeImpl)localSet1.iterator().next();
/* 602 */     PolicyNodeImpl localPolicyNodeImpl2 = (PolicyNodeImpl)localPolicyNodeImpl1.getParent();
/* 603 */     localPolicyNodeImpl2.deleteChild(localPolicyNodeImpl1);
/*     */     
/* 605 */     HashSet localHashSet = new HashSet(paramSet);
/* 606 */     for (Iterator localIterator1 = paramPolicyNodeImpl.getPolicyNodes(paramInt).iterator(); localIterator1.hasNext();) { localObject = (PolicyNodeImpl)localIterator1.next();
/* 607 */       localHashSet.remove(((PolicyNodeImpl)localObject).getValidPolicy()); }
/*     */     Object localObject;
/* 609 */     boolean bool; if (localHashSet.isEmpty())
/*     */     {
/*     */ 
/* 612 */       paramPolicyNodeImpl.prune(paramInt);
/* 613 */       if (!paramPolicyNodeImpl.getChildren().hasNext()) {
/* 614 */         paramPolicyNodeImpl = null;
/*     */       }
/*     */     } else {
/* 617 */       bool = localPolicyNodeImpl1.isCritical();
/*     */       
/* 619 */       localObject = localPolicyNodeImpl1.getPolicyQualifiers();
/* 620 */       for (String str : localHashSet) {
/* 621 */         Set localSet2 = Collections.singleton(str);
/* 622 */         PolicyNodeImpl localPolicyNodeImpl3 = new PolicyNodeImpl(localPolicyNodeImpl2, str, (Set)localObject, bool, localSet2, false);
/*     */       }
/*     */     }
/*     */     
/* 626 */     return paramPolicyNodeImpl;
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
/*     */   private static boolean processParents(int paramInt, boolean paramBoolean1, boolean paramBoolean2, PolicyNodeImpl paramPolicyNodeImpl, String paramString, Set<PolicyQualifierInfo> paramSet, boolean paramBoolean3)
/*     */     throws CertPathValidatorException
/*     */   {
/* 659 */     boolean bool = false;
/*     */     
/* 661 */     if (debug != null) {
/* 662 */       debug.println("PolicyChecker.processParents(): matchAny = " + paramBoolean3);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 667 */     Set localSet1 = paramPolicyNodeImpl.getPolicyNodesExpected(paramInt - 1, paramString, paramBoolean3);
/*     */     
/*     */ 
/*     */ 
/* 671 */     for (PolicyNodeImpl localPolicyNodeImpl1 : localSet1) {
/* 672 */       if (debug != null) {
/* 673 */         debug.println("PolicyChecker.processParents() found parent:\n" + localPolicyNodeImpl1
/* 674 */           .asString());
/*     */       }
/* 676 */       bool = true;
/* 677 */       String str1 = localPolicyNodeImpl1.getValidPolicy();
/*     */       
/* 679 */       PolicyNodeImpl localPolicyNodeImpl2 = null;
/* 680 */       HashSet localHashSet = null;
/*     */       
/* 682 */       if (paramString.equals("2.5.29.32.0"))
/*     */       {
/* 684 */         Set localSet2 = localPolicyNodeImpl1.getExpectedPolicies();
/*     */         
/* 686 */         for (String str2 : localSet2)
/*     */         {
/*     */ 
/* 689 */           Iterator localIterator3 = localPolicyNodeImpl1.getChildren();
/* 690 */           for (;;) { if (!localIterator3.hasNext()) break label262;
/* 691 */             localObject = (PolicyNodeImpl)localIterator3.next();
/* 692 */             String str3 = ((PolicyNodeImpl)localObject).getValidPolicy();
/* 693 */             if (str2.equals(str3)) {
/* 694 */               if (debug == null) break;
/* 695 */               debug.println(str3 + " in parent's expected policy set already appears in child node"); break;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 702 */           Object localObject = new HashSet();
/* 703 */           ((Set)localObject).add(str2);
/*     */           
/* 705 */           localPolicyNodeImpl2 = new PolicyNodeImpl(localPolicyNodeImpl1, str2, paramSet, paramBoolean1, (Set)localObject, false);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 710 */         localHashSet = new HashSet();
/* 711 */         localHashSet.add(paramString);
/*     */         
/* 713 */         localPolicyNodeImpl2 = new PolicyNodeImpl(localPolicyNodeImpl1, paramString, paramSet, paramBoolean1, localHashSet, false);
/*     */       }
/*     */     }
/*     */     
/*     */     label262:
/*     */     
/* 719 */     return bool;
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
/*     */   private static PolicyNodeImpl processPolicyMappings(X509CertImpl paramX509CertImpl, int paramInt1, int paramInt2, PolicyNodeImpl paramPolicyNodeImpl, boolean paramBoolean, Set<PolicyQualifierInfo> paramSet)
/*     */     throws CertPathValidatorException
/*     */   {
/* 744 */     PolicyMappingsExtension localPolicyMappingsExtension = paramX509CertImpl.getPolicyMappingsExtension();
/*     */     
/* 746 */     if (localPolicyMappingsExtension == null) {
/* 747 */       return paramPolicyNodeImpl;
/*     */     }
/* 749 */     if (debug != null) {
/* 750 */       debug.println("PolicyChecker.processPolicyMappings() inside policyMapping check");
/*     */     }
/*     */     
/* 753 */     List localList = null;
/*     */     try {
/* 755 */       localList = localPolicyMappingsExtension.get("map");
/*     */     } catch (IOException localIOException) {
/* 757 */       if (debug != null) {
/* 758 */         debug.println("PolicyChecker.processPolicyMappings() mapping exception");
/*     */         
/* 760 */         localIOException.printStackTrace();
/*     */       }
/* 762 */       throw new CertPathValidatorException("Exception while checking mapping", localIOException);
/*     */     }
/*     */     
/*     */ 
/* 766 */     int i = 0;
/* 767 */     for (CertificatePolicyMap localCertificatePolicyMap : localList)
/*     */     {
/* 769 */       str1 = localCertificatePolicyMap.getIssuerIdentifier().getIdentifier().toString();
/*     */       
/* 771 */       str2 = localCertificatePolicyMap.getSubjectIdentifier().getIdentifier().toString();
/* 772 */       if (debug != null) {
/* 773 */         debug.println("PolicyChecker.processPolicyMappings() issuerDomain = " + str1);
/*     */         
/* 775 */         debug.println("PolicyChecker.processPolicyMappings() subjectDomain = " + str2);
/*     */       }
/*     */       
/*     */ 
/* 779 */       if (str1.equals("2.5.29.32.0")) {
/* 780 */         throw new CertPathValidatorException("encountered an issuerDomainPolicy of ANY_POLICY", null, null, -1, PKIXReason.INVALID_POLICY);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 785 */       if (str2.equals("2.5.29.32.0")) {
/* 786 */         throw new CertPathValidatorException("encountered a subjectDomainPolicy of ANY_POLICY", null, null, -1, PKIXReason.INVALID_POLICY);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 792 */       Set localSet = paramPolicyNodeImpl.getPolicyNodesValid(paramInt1, str1);
/* 793 */       Object localObject1; if (!localSet.isEmpty()) {
/* 794 */         for (localObject1 = localSet.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (PolicyNodeImpl)((Iterator)localObject1).next();
/* 795 */           if ((paramInt2 > 0) || (paramInt2 == -1)) {
/* 796 */             ((PolicyNodeImpl)localObject2).addExpectedPolicy(str2);
/* 797 */           } else if (paramInt2 == 0)
/*     */           {
/* 799 */             localPolicyNodeImpl1 = (PolicyNodeImpl)((PolicyNodeImpl)localObject2).getParent();
/* 800 */             if (debug != null) {
/* 801 */               debug.println("PolicyChecker.processPolicyMappings() before deleting: policy tree = " + paramPolicyNodeImpl);
/*     */             }
/*     */             
/* 804 */             localPolicyNodeImpl1.deleteChild((PolicyNode)localObject2);
/* 805 */             i = 1;
/* 806 */             if (debug != null) {
/* 807 */               debug.println("PolicyChecker.processPolicyMappings() after deleting: policy tree = " + paramPolicyNodeImpl);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 813 */       else if ((paramInt2 > 0) || (paramInt2 == -1))
/*     */       {
/* 815 */         localObject1 = paramPolicyNodeImpl.getPolicyNodesValid(paramInt1, "2.5.29.32.0");
/* 816 */         for (localObject2 = ((Set)localObject1).iterator(); ((Iterator)localObject2).hasNext();) { localPolicyNodeImpl1 = (PolicyNodeImpl)((Iterator)localObject2).next();
/*     */           
/* 818 */           PolicyNodeImpl localPolicyNodeImpl2 = (PolicyNodeImpl)localPolicyNodeImpl1.getParent();
/*     */           
/* 820 */           HashSet localHashSet = new HashSet();
/* 821 */           localHashSet.add(str2);
/*     */           
/* 823 */           PolicyNodeImpl localPolicyNodeImpl3 = new PolicyNodeImpl(localPolicyNodeImpl2, str1, paramSet, paramBoolean, localHashSet, true);
/*     */         }
/*     */       }
/*     */     }
/*     */     String str1;
/*     */     String str2;
/*     */     Object localObject2;
/*     */     PolicyNodeImpl localPolicyNodeImpl1;
/* 831 */     if (i != 0) {
/* 832 */       paramPolicyNodeImpl.prune(paramInt1);
/* 833 */       if (!paramPolicyNodeImpl.getChildren().hasNext()) {
/* 834 */         if (debug != null)
/* 835 */           debug.println("setting rootNode to null");
/* 836 */         paramPolicyNodeImpl = null;
/*     */       }
/*     */     }
/*     */     
/* 840 */     return paramPolicyNodeImpl;
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
/*     */   private static PolicyNodeImpl removeInvalidNodes(PolicyNodeImpl paramPolicyNodeImpl, int paramInt, Set<String> paramSet, CertificatePoliciesExtension paramCertificatePoliciesExtension)
/*     */     throws CertPathValidatorException
/*     */   {
/* 860 */     List localList = null;
/*     */     try {
/* 862 */       localList = paramCertificatePoliciesExtension.get("policies");
/*     */     } catch (IOException localIOException) {
/* 864 */       throw new CertPathValidatorException("Exception while retrieving policyOIDs", localIOException);
/*     */     }
/*     */     
/*     */ 
/* 868 */     int i = 0;
/* 869 */     for (PolicyInformation localPolicyInformation : localList)
/*     */     {
/* 871 */       str = localPolicyInformation.getPolicyIdentifier().getIdentifier().toString();
/*     */       
/* 873 */       if (debug != null) {
/* 874 */         debug.println("PolicyChecker.processPolicies() processing policy second time: " + str);
/*     */       }
/*     */       
/*     */ 
/* 878 */       Set localSet = paramPolicyNodeImpl.getPolicyNodesValid(paramInt, str);
/* 879 */       for (PolicyNodeImpl localPolicyNodeImpl1 : localSet) {
/* 880 */         PolicyNodeImpl localPolicyNodeImpl2 = (PolicyNodeImpl)localPolicyNodeImpl1.getParent();
/* 881 */         if ((localPolicyNodeImpl2.getValidPolicy().equals("2.5.29.32.0")) && 
/* 882 */           (!paramSet.contains(str)) && 
/* 883 */           (!str.equals("2.5.29.32.0"))) {
/* 884 */           if (debug != null) {
/* 885 */             debug.println("PolicyChecker.processPolicies() before deleting: policy tree = " + paramPolicyNodeImpl);
/*     */           }
/* 887 */           localPolicyNodeImpl2.deleteChild(localPolicyNodeImpl1);
/* 888 */           i = 1;
/* 889 */           if (debug != null) {
/* 890 */             debug.println("PolicyChecker.processPolicies() after deleting: policy tree = " + paramPolicyNodeImpl);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     String str;
/* 897 */     if (i != 0) {
/* 898 */       paramPolicyNodeImpl.prune(paramInt);
/* 899 */       if (!paramPolicyNodeImpl.getChildren().hasNext()) {
/* 900 */         paramPolicyNodeImpl = null;
/*     */       }
/*     */     }
/*     */     
/* 904 */     return paramPolicyNodeImpl;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   PolicyNode getPolicyTree()
/*     */   {
/* 916 */     if (this.rootNode == null) {
/* 917 */       return null;
/*     */     }
/* 919 */     PolicyNodeImpl localPolicyNodeImpl = this.rootNode.copyTree();
/* 920 */     localPolicyNodeImpl.setImmutable();
/* 921 */     return localPolicyNodeImpl;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\PolicyChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
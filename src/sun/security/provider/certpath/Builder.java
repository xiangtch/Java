/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.GeneralNameInterface;
/*     */ import sun.security.x509.GeneralNames;
/*     */ import sun.security.x509.GeneralSubtree;
/*     */ import sun.security.x509.GeneralSubtrees;
/*     */ import sun.security.x509.NameConstraintsExtension;
/*     */ import sun.security.x509.SubjectAlternativeNameExtension;
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
/*     */ public abstract class Builder
/*     */ {
/*  56 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */   private Set<String> matchingPolicies;
/*     */   
/*     */ 
/*     */   final PKIX.BuilderParams buildParams;
/*     */   
/*     */ 
/*     */   final X509CertSelector targetCertConstraints;
/*     */   
/*  67 */   static final boolean USE_AIA = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.security.enableAIAcaIssuers"))).booleanValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Builder(PKIX.BuilderParams paramBuilderParams)
/*     */   {
/*  75 */     this.buildParams = paramBuilderParams;
/*     */     
/*  77 */     this.targetCertConstraints = ((X509CertSelector)paramBuilderParams.targetCertConstraints());
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
/*     */   abstract Collection<X509Certificate> getMatchingCerts(State paramState, List<CertStore> paramList)
/*     */     throws CertStoreException, CertificateException, IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract void verifyCert(X509Certificate paramX509Certificate, State paramState, List<X509Certificate> paramList)
/*     */     throws GeneralSecurityException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract boolean isPathCompleted(X509Certificate paramX509Certificate);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract void addCertToPath(X509Certificate paramX509Certificate, LinkedList<X509Certificate> paramLinkedList);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract void removeFinalCertFromPath(LinkedList<X509Certificate> paramLinkedList);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int distance(GeneralNameInterface paramGeneralNameInterface1, GeneralNameInterface paramGeneralNameInterface2, int paramInt)
/*     */   {
/* 146 */     switch (paramGeneralNameInterface1.constrains(paramGeneralNameInterface2)) {
/*     */     case -1: 
/* 148 */       if (debug != null) {
/* 149 */         debug.println("Builder.distance(): Names are different types");
/*     */       }
/* 151 */       return paramInt;
/*     */     case 3: 
/* 153 */       if (debug != null) {
/* 154 */         debug.println("Builder.distance(): Names are same type but in different subtrees");
/*     */       }
/*     */       
/* 157 */       return paramInt;
/*     */     case 0: 
/* 159 */       return 0;
/*     */     case 2: 
/*     */       break;
/*     */     case 1: 
/*     */       break;
/*     */     default: 
/* 165 */       return paramInt;
/*     */     }
/*     */     
/*     */     
/* 169 */     return paramGeneralNameInterface2.subtreeDepth() - paramGeneralNameInterface1.subtreeDepth();
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
/*     */   static int hops(GeneralNameInterface paramGeneralNameInterface1, GeneralNameInterface paramGeneralNameInterface2, int paramInt)
/*     */   {
/* 192 */     int i = paramGeneralNameInterface1.constrains(paramGeneralNameInterface2);
/* 193 */     switch (i) {
/*     */     case -1: 
/* 195 */       if (debug != null) {
/* 196 */         debug.println("Builder.hops(): Names are different types");
/*     */       }
/* 198 */       return paramInt;
/*     */     
/*     */     case 3: 
/*     */       break;
/*     */     
/*     */     case 0: 
/* 204 */       return 0;
/*     */     
/*     */     case 2: 
/* 207 */       return paramGeneralNameInterface2.subtreeDepth() - paramGeneralNameInterface1.subtreeDepth();
/*     */     
/*     */     case 1: 
/* 210 */       return paramGeneralNameInterface2.subtreeDepth() - paramGeneralNameInterface1.subtreeDepth();
/*     */     default: 
/* 212 */       return paramInt;
/*     */     }
/*     */     
/*     */     
/* 216 */     if (paramGeneralNameInterface1.getType() != 4) {
/* 217 */       if (debug != null) {
/* 218 */         debug.println("Builder.hops(): hopDistance not implemented for this name type");
/*     */       }
/*     */       
/* 221 */       return paramInt;
/*     */     }
/* 223 */     X500Name localX500Name1 = (X500Name)paramGeneralNameInterface1;
/* 224 */     X500Name localX500Name2 = (X500Name)paramGeneralNameInterface2;
/* 225 */     X500Name localX500Name3 = localX500Name1.commonAncestor(localX500Name2);
/* 226 */     if (localX500Name3 == null) {
/* 227 */       if (debug != null) {
/* 228 */         debug.println("Builder.hops(): Names are in different namespaces");
/*     */       }
/*     */       
/* 231 */       return paramInt;
/*     */     }
/* 233 */     int j = localX500Name3.subtreeDepth();
/* 234 */     int k = localX500Name1.subtreeDepth();
/* 235 */     int m = localX500Name2.subtreeDepth();
/* 236 */     return k + m - 2 * j;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int targetDistance(NameConstraintsExtension paramNameConstraintsExtension, X509Certificate paramX509Certificate, GeneralNameInterface paramGeneralNameInterface)
/*     */     throws IOException
/*     */   {
/* 285 */     if ((paramNameConstraintsExtension != null) && (!paramNameConstraintsExtension.verify(paramX509Certificate))) {
/* 286 */       throw new IOException("certificate does not satisfy existing name constraints");
/*     */     }
/*     */     
/*     */     X509CertImpl localX509CertImpl;
/*     */     try
/*     */     {
/* 292 */       localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
/*     */     } catch (CertificateException localCertificateException) {
/* 294 */       throw new IOException("Invalid certificate", localCertificateException);
/*     */     }
/*     */     
/* 297 */     X500Name localX500Name = X500Name.asX500Name(localX509CertImpl.getSubjectX500Principal());
/* 298 */     if (localX500Name.equals(paramGeneralNameInterface))
/*     */     {
/* 300 */       return 0;
/*     */     }
/*     */     
/*     */ 
/* 304 */     SubjectAlternativeNameExtension localSubjectAlternativeNameExtension = localX509CertImpl.getSubjectAlternativeNameExtension();
/* 305 */     if (localSubjectAlternativeNameExtension != null) {
/* 306 */       localObject = localSubjectAlternativeNameExtension.get("subject_name");
/*     */       
/*     */ 
/* 309 */       if (localObject != null) {
/* 310 */         int i = 0; for (int j = ((GeneralNames)localObject).size(); i < j; i++) {
/* 311 */           GeneralNameInterface localGeneralNameInterface1 = ((GeneralNames)localObject).get(i).getName();
/* 312 */           if (localGeneralNameInterface1.equals(paramGeneralNameInterface)) {
/* 313 */             return 0;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 323 */     Object localObject = localX509CertImpl.getNameConstraintsExtension();
/* 324 */     if (localObject == null) {
/* 325 */       return -1;
/*     */     }
/*     */     
/*     */ 
/* 329 */     if (paramNameConstraintsExtension != null) {
/* 330 */       paramNameConstraintsExtension.merge((NameConstraintsExtension)localObject);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 335 */       paramNameConstraintsExtension = (NameConstraintsExtension)((NameConstraintsExtension)localObject).clone();
/*     */     }
/*     */     
/* 338 */     if (debug != null) {
/* 339 */       debug.println("Builder.targetDistance() merged constraints: " + 
/* 340 */         String.valueOf(paramNameConstraintsExtension));
/*     */     }
/*     */     
/*     */ 
/* 344 */     GeneralSubtrees localGeneralSubtrees1 = paramNameConstraintsExtension.get("permitted_subtrees");
/*     */     
/* 346 */     GeneralSubtrees localGeneralSubtrees2 = paramNameConstraintsExtension.get("excluded_subtrees");
/* 347 */     if (localGeneralSubtrees1 != null) {
/* 348 */       localGeneralSubtrees1.reduce(localGeneralSubtrees2);
/*     */     }
/* 350 */     if (debug != null) {
/* 351 */       debug.println("Builder.targetDistance() reduced constraints: " + localGeneralSubtrees1);
/*     */     }
/*     */     
/*     */ 
/* 355 */     if (!paramNameConstraintsExtension.verify(paramGeneralNameInterface)) {
/* 356 */       throw new IOException("New certificate not allowed to sign certificate for target");
/*     */     }
/*     */     
/*     */ 
/* 360 */     if (localGeneralSubtrees1 == null)
/*     */     {
/* 362 */       return -1;
/*     */     }
/* 364 */     int k = 0; for (int m = localGeneralSubtrees1.size(); k < m; k++) {
/* 365 */       GeneralNameInterface localGeneralNameInterface2 = localGeneralSubtrees1.get(k).getName().getName();
/* 366 */       int n = distance(localGeneralNameInterface2, paramGeneralNameInterface, -1);
/* 367 */       if (n >= 0) {
/* 368 */         return n + 1;
/*     */       }
/*     */     }
/*     */     
/* 372 */     return -1;
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
/*     */   Set<String> getMatchingPolicies()
/*     */   {
/* 396 */     if (this.matchingPolicies != null) {
/* 397 */       Set localSet = this.buildParams.initialPolicies();
/* 398 */       if ((!localSet.isEmpty()) && 
/* 399 */         (!localSet.contains("2.5.29.32.0")) && 
/* 400 */         (this.buildParams.policyMappingInhibited()))
/*     */       {
/* 402 */         this.matchingPolicies = new HashSet(localSet);
/* 403 */         this.matchingPolicies.add("2.5.29.32.0");
/*     */       }
/*     */       else
/*     */       {
/* 407 */         this.matchingPolicies = Collections.emptySet();
/*     */       }
/*     */     }
/* 410 */     return this.matchingPolicies;
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
/*     */   boolean addMatchingCerts(X509CertSelector paramX509CertSelector, Collection<CertStore> paramCollection, Collection<X509Certificate> paramCollection1, boolean paramBoolean)
/*     */   {
/* 432 */     X509Certificate localX509Certificate = paramX509CertSelector.getCertificate();
/* 433 */     if (localX509Certificate != null)
/*     */     {
/* 435 */       if ((paramX509CertSelector.match(localX509Certificate)) && 
/* 436 */         (!X509CertImpl.isSelfSigned(localX509Certificate, this.buildParams.sigProvider()))) {
/* 437 */         if (debug != null) {
/* 438 */           debug.println("Builder.addMatchingCerts: adding target cert\n  SN: " + 
/*     */           
/* 440 */             Debug.toHexString(localX509Certificate
/* 441 */             .getSerialNumber()) + "\n  Subject: " + localX509Certificate
/* 442 */             .getSubjectX500Principal() + "\n  Issuer: " + localX509Certificate
/* 443 */             .getIssuerX500Principal());
/*     */         }
/* 445 */         return paramCollection1.add(localX509Certificate);
/*     */       }
/* 447 */       return false;
/*     */     }
/* 449 */     boolean bool = false;
/* 450 */     for (CertStore localCertStore : paramCollection) {
/*     */       try
/*     */       {
/* 453 */         Collection localCollection = localCertStore.getCertificates(paramX509CertSelector);
/* 454 */         for (Certificate localCertificate : localCollection)
/*     */         {
/* 456 */           if ((!X509CertImpl.isSelfSigned((X509Certificate)localCertificate, this.buildParams.sigProvider())) && 
/* 457 */             (paramCollection1.add((X509Certificate)localCertificate))) {
/* 458 */             bool = true;
/*     */           }
/*     */         }
/*     */         
/* 462 */         if ((!paramBoolean) && (bool)) {
/* 463 */           return true;
/*     */         }
/*     */       }
/*     */       catch (CertStoreException localCertStoreException)
/*     */       {
/* 468 */         if (debug != null) {
/* 469 */           debug.println("Builder.addMatchingCerts, non-fatal exception retrieving certs: " + localCertStoreException);
/*     */           
/* 471 */           localCertStoreException.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/* 475 */     return bool;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\Builder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.cert.CRL;
/*     */ import java.security.cert.CRLSelector;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CertStoreParameters;
/*     */ import java.security.cert.CertStoreSpi;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CollectionCertStoreParameters;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509CRLSelector;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IndexedCollectionCertStore
/*     */   extends CertStoreSpi
/*     */ {
/*     */   private Map<X500Principal, Object> certSubjects;
/*     */   private Map<X500Principal, Object> crlIssuers;
/*     */   private Set<Certificate> otherCertificates;
/*     */   private Set<CRL> otherCRLs;
/*     */   
/*     */   public IndexedCollectionCertStore(CertStoreParameters paramCertStoreParameters)
/*     */     throws InvalidAlgorithmParameterException
/*     */   {
/* 123 */     super(paramCertStoreParameters);
/* 124 */     if (!(paramCertStoreParameters instanceof CollectionCertStoreParameters)) {
/* 125 */       throw new InvalidAlgorithmParameterException("parameters must be CollectionCertStoreParameters");
/*     */     }
/*     */     
/* 128 */     Collection localCollection = ((CollectionCertStoreParameters)paramCertStoreParameters).getCollection();
/* 129 */     if (localCollection == null) {
/* 130 */       throw new InvalidAlgorithmParameterException("Collection must not be null");
/*     */     }
/*     */     
/* 133 */     buildIndex(localCollection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void buildIndex(Collection<?> paramCollection)
/*     */   {
/* 141 */     this.certSubjects = new HashMap();
/* 142 */     this.crlIssuers = new HashMap();
/* 143 */     this.otherCertificates = null;
/* 144 */     this.otherCRLs = null;
/* 145 */     for (Object localObject : paramCollection) {
/* 146 */       if ((localObject instanceof X509Certificate)) {
/* 147 */         indexCertificate((X509Certificate)localObject);
/* 148 */       } else if ((localObject instanceof X509CRL)) {
/* 149 */         indexCRL((X509CRL)localObject);
/* 150 */       } else if ((localObject instanceof Certificate)) {
/* 151 */         if (this.otherCertificates == null) {
/* 152 */           this.otherCertificates = new HashSet();
/*     */         }
/* 154 */         this.otherCertificates.add((Certificate)localObject);
/* 155 */       } else if ((localObject instanceof CRL)) {
/* 156 */         if (this.otherCRLs == null) {
/* 157 */           this.otherCRLs = new HashSet();
/*     */         }
/* 159 */         this.otherCRLs.add((CRL)localObject);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 164 */     if (this.otherCertificates == null) {
/* 165 */       this.otherCertificates = Collections.emptySet();
/*     */     }
/* 167 */     if (this.otherCRLs == null) {
/* 168 */       this.otherCRLs = Collections.emptySet();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void indexCertificate(X509Certificate paramX509Certificate)
/*     */   {
/* 176 */     X500Principal localX500Principal = paramX509Certificate.getSubjectX500Principal();
/* 177 */     Object localObject1 = this.certSubjects.put(localX500Principal, paramX509Certificate);
/* 178 */     if (localObject1 != null) { Object localObject2;
/* 179 */       if ((localObject1 instanceof X509Certificate)) {
/* 180 */         if (paramX509Certificate.equals(localObject1)) {
/* 181 */           return;
/*     */         }
/* 183 */         localObject2 = new ArrayList(2);
/* 184 */         ((List)localObject2).add(paramX509Certificate);
/* 185 */         ((List)localObject2).add((X509Certificate)localObject1);
/* 186 */         this.certSubjects.put(localX500Principal, localObject2);
/*     */       }
/*     */       else {
/* 189 */         localObject2 = (List)localObject1;
/* 190 */         if (!((List)localObject2).contains(paramX509Certificate)) {
/* 191 */           ((List)localObject2).add(paramX509Certificate);
/*     */         }
/* 193 */         this.certSubjects.put(localX500Principal, localObject2);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void indexCRL(X509CRL paramX509CRL)
/*     */   {
/* 202 */     X500Principal localX500Principal = paramX509CRL.getIssuerX500Principal();
/* 203 */     Object localObject1 = this.crlIssuers.put(localX500Principal, paramX509CRL);
/* 204 */     if (localObject1 != null) { Object localObject2;
/* 205 */       if ((localObject1 instanceof X509CRL)) {
/* 206 */         if (paramX509CRL.equals(localObject1)) {
/* 207 */           return;
/*     */         }
/* 209 */         localObject2 = new ArrayList(2);
/* 210 */         ((List)localObject2).add(paramX509CRL);
/* 211 */         ((List)localObject2).add((X509CRL)localObject1);
/* 212 */         this.crlIssuers.put(localX500Principal, localObject2);
/*     */       }
/*     */       else
/*     */       {
/* 216 */         localObject2 = (List)localObject1;
/* 217 */         if (!((List)localObject2).contains(paramX509CRL)) {
/* 218 */           ((List)localObject2).add(paramX509CRL);
/*     */         }
/* 220 */         this.crlIssuers.put(localX500Principal, localObject2);
/*     */       }
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
/*     */   public Collection<? extends Certificate> engineGetCertificates(CertSelector paramCertSelector)
/*     */     throws CertStoreException
/*     */   {
/* 242 */     if (paramCertSelector == null) {
/* 243 */       localObject1 = new HashSet();
/* 244 */       matchX509Certs(new X509CertSelector(), (Collection)localObject1);
/* 245 */       ((Set)localObject1).addAll(this.otherCertificates);
/* 246 */       return (Collection<? extends Certificate>)localObject1;
/*     */     }
/*     */     Object localObject2;
/* 249 */     if (!(paramCertSelector instanceof X509CertSelector)) {
/* 250 */       localObject1 = new HashSet();
/* 251 */       matchX509Certs(paramCertSelector, (Collection)localObject1);
/* 252 */       for (localObject2 = this.otherCertificates.iterator(); ((Iterator)localObject2).hasNext();) { localObject3 = (Certificate)((Iterator)localObject2).next();
/* 253 */         if (paramCertSelector.match((Certificate)localObject3)) {
/* 254 */           ((Set)localObject1).add(localObject3);
/*     */         }
/*     */       }
/* 257 */       return (Collection<? extends Certificate>)localObject1;
/*     */     }
/*     */     
/* 260 */     if (this.certSubjects.isEmpty()) {
/* 261 */       return Collections.emptySet();
/*     */     }
/* 263 */     Object localObject1 = (X509CertSelector)paramCertSelector;
/*     */     
/*     */ 
/* 266 */     Object localObject3 = ((X509CertSelector)localObject1).getCertificate();
/* 267 */     if (localObject3 != null) {
/* 268 */       localObject2 = ((X509Certificate)localObject3).getSubjectX500Principal();
/*     */     } else {
/* 270 */       localObject2 = ((X509CertSelector)localObject1).getSubject();
/*     */     }
/* 272 */     if (localObject2 != null)
/*     */     {
/* 274 */       localObject4 = this.certSubjects.get(localObject2);
/* 275 */       if (localObject4 == null) {
/* 276 */         return Collections.emptySet();
/*     */       }
/* 278 */       if ((localObject4 instanceof X509Certificate)) {
/* 279 */         localObject5 = (X509Certificate)localObject4;
/* 280 */         if (((X509CertSelector)localObject1).match((Certificate)localObject5)) {
/* 281 */           return Collections.singleton(localObject5);
/*     */         }
/* 283 */         return Collections.emptySet();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 288 */       Object localObject5 = (List)localObject4;
/* 289 */       HashSet localHashSet = new HashSet(16);
/* 290 */       for (X509Certificate localX509Certificate : (List)localObject5) {
/* 291 */         if (((X509CertSelector)localObject1).match(localX509Certificate)) {
/* 292 */           localHashSet.add(localX509Certificate);
/*     */         }
/*     */       }
/* 295 */       return localHashSet;
/*     */     }
/*     */     
/*     */ 
/* 299 */     Object localObject4 = new HashSet(16);
/* 300 */     matchX509Certs((CertSelector)localObject1, (Collection)localObject4);
/* 301 */     return (Collection<? extends Certificate>)localObject4;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void matchX509Certs(CertSelector paramCertSelector, Collection<Certificate> paramCollection)
/*     */   {
/* 311 */     for (Object localObject1 : this.certSubjects.values()) { Object localObject2;
/* 312 */       if ((localObject1 instanceof X509Certificate)) {
/* 313 */         localObject2 = (X509Certificate)localObject1;
/* 314 */         if (paramCertSelector.match((Certificate)localObject2)) {
/* 315 */           paramCollection.add(localObject2);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 320 */         localObject2 = (List)localObject1;
/* 321 */         for (X509Certificate localX509Certificate : (List)localObject2) {
/* 322 */           if (paramCertSelector.match(localX509Certificate)) {
/* 323 */             paramCollection.add(localX509Certificate);
/*     */           }
/*     */         }
/*     */       }
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
/*     */   public Collection<CRL> engineGetCRLs(CRLSelector paramCRLSelector)
/*     */     throws CertStoreException
/*     */   {
/* 346 */     if (paramCRLSelector == null) {
/* 347 */       localObject1 = new HashSet();
/* 348 */       matchX509CRLs(new X509CRLSelector(), (Collection)localObject1);
/* 349 */       ((Set)localObject1).addAll(this.otherCRLs);
/* 350 */       return (Collection<CRL>)localObject1;
/*     */     }
/*     */     
/* 353 */     if (!(paramCRLSelector instanceof X509CRLSelector)) {
/* 354 */       localObject1 = new HashSet();
/* 355 */       matchX509CRLs(paramCRLSelector, (Collection)localObject1);
/* 356 */       for (localObject2 = this.otherCRLs.iterator(); ((Iterator)localObject2).hasNext();) { localObject3 = (CRL)((Iterator)localObject2).next();
/* 357 */         if (paramCRLSelector.match((CRL)localObject3)) {
/* 358 */           ((Set)localObject1).add(localObject3);
/*     */         }
/*     */       }
/* 361 */       return (Collection<CRL>)localObject1;
/*     */     }
/*     */     
/* 364 */     if (this.crlIssuers.isEmpty()) {
/* 365 */       return Collections.emptySet();
/*     */     }
/* 367 */     Object localObject1 = (X509CRLSelector)paramCRLSelector;
/*     */     
/* 369 */     Object localObject2 = ((X509CRLSelector)localObject1).getIssuers();
/* 370 */     if (localObject2 != null) {
/* 371 */       localObject3 = new HashSet(16);
/* 372 */       for (X500Principal localX500Principal : (Collection)localObject2) {
/* 373 */         Object localObject4 = this.crlIssuers.get(localX500Principal);
/* 374 */         if (localObject4 != null) {
/*     */           Object localObject5;
/* 376 */           if ((localObject4 instanceof X509CRL)) {
/* 377 */             localObject5 = (X509CRL)localObject4;
/* 378 */             if (((X509CRLSelector)localObject1).match((CRL)localObject5)) {
/* 379 */               ((HashSet)localObject3).add(localObject5);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 384 */             localObject5 = (List)localObject4;
/* 385 */             for (X509CRL localX509CRL : (List)localObject5) {
/* 386 */               if (((X509CRLSelector)localObject1).match(localX509CRL))
/* 387 */                 ((HashSet)localObject3).add(localX509CRL);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 392 */       return (Collection<CRL>)localObject3;
/*     */     }
/*     */     
/* 395 */     Object localObject3 = new HashSet(16);
/* 396 */     matchX509CRLs((CRLSelector)localObject1, (Collection)localObject3);
/* 397 */     return (Collection<CRL>)localObject3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void matchX509CRLs(CRLSelector paramCRLSelector, Collection<CRL> paramCollection)
/*     */   {
/* 405 */     for (Object localObject1 : this.crlIssuers.values()) { Object localObject2;
/* 406 */       if ((localObject1 instanceof X509CRL)) {
/* 407 */         localObject2 = (X509CRL)localObject1;
/* 408 */         if (paramCRLSelector.match((CRL)localObject2)) {
/* 409 */           paramCollection.add(localObject2);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 414 */         localObject2 = (List)localObject1;
/* 415 */         for (X509CRL localX509CRL : (List)localObject2) {
/* 416 */           if (paramCRLSelector.match(localX509CRL)) {
/* 417 */             paramCollection.add(localX509CRL);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\IndexedCollectionCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
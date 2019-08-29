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
/*     */ import java.util.Collection;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CollectionCertStore
/*     */   extends CertStoreSpi
/*     */ {
/*     */   private Collection<?> coll;
/*     */   
/*     */   public CollectionCertStore(CertStoreParameters paramCertStoreParameters)
/*     */     throws InvalidAlgorithmParameterException
/*     */   {
/*  97 */     super(paramCertStoreParameters);
/*  98 */     if (!(paramCertStoreParameters instanceof CollectionCertStoreParameters)) {
/*  99 */       throw new InvalidAlgorithmParameterException("parameters must be CollectionCertStoreParameters");
/*     */     }
/* 101 */     this.coll = ((CollectionCertStoreParameters)paramCertStoreParameters).getCollection();
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
/*     */   public Collection<Certificate> engineGetCertificates(CertSelector paramCertSelector)
/*     */     throws CertStoreException
/*     */   {
/* 119 */     if (this.coll == null) {
/* 120 */       throw new CertStoreException("Collection is null");
/*     */     }
/*     */     
/* 123 */     for (int i = 0; i < 10; i++) {
/*     */       try {
/* 125 */         HashSet localHashSet = new HashSet();
/* 126 */         Iterator localIterator; Object localObject; if (paramCertSelector != null) {
/* 127 */           for (localIterator = this.coll.iterator(); localIterator.hasNext();) { localObject = localIterator.next();
/* 128 */             if (((localObject instanceof Certificate)) && 
/* 129 */               (paramCertSelector.match((Certificate)localObject)))
/* 130 */               localHashSet.add((Certificate)localObject);
/*     */           }
/*     */         } else {
/* 133 */           for (localIterator = this.coll.iterator(); localIterator.hasNext();) { localObject = localIterator.next();
/* 134 */             if ((localObject instanceof Certificate))
/* 135 */               localHashSet.add((Certificate)localObject);
/*     */           }
/*     */         }
/* 138 */         return localHashSet;
/*     */       } catch (ConcurrentModificationException localConcurrentModificationException) {}
/*     */     }
/* 141 */     throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
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
/*     */   public Collection<CRL> engineGetCRLs(CRLSelector paramCRLSelector)
/*     */     throws CertStoreException
/*     */   {
/* 161 */     if (this.coll == null) {
/* 162 */       throw new CertStoreException("Collection is null");
/*     */     }
/*     */     
/* 165 */     for (int i = 0; i < 10; i++) {
/*     */       try {
/* 167 */         HashSet localHashSet = new HashSet();
/* 168 */         Iterator localIterator; Object localObject; if (paramCRLSelector != null) {
/* 169 */           for (localIterator = this.coll.iterator(); localIterator.hasNext();) { localObject = localIterator.next();
/* 170 */             if (((localObject instanceof CRL)) && (paramCRLSelector.match((CRL)localObject)))
/* 171 */               localHashSet.add((CRL)localObject);
/*     */           }
/*     */         } else {
/* 174 */           for (localIterator = this.coll.iterator(); localIterator.hasNext();) { localObject = localIterator.next();
/* 175 */             if ((localObject instanceof CRL))
/* 176 */               localHashSet.add((CRL)localObject);
/*     */           }
/*     */         }
/* 179 */         return localHashSet;
/*     */       } catch (ConcurrentModificationException localConcurrentModificationException) {}
/*     */     }
/* 182 */     throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\CollectionCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
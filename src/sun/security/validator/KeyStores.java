/*     */ package sun.security.validator;
/*     */ 
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KeyStores
/*     */ {
/*     */   public static Set<X509Certificate> getTrustedCerts(KeyStore paramKeyStore)
/*     */   {
/*  97 */     HashSet localHashSet = new HashSet();
/*     */     Enumeration localEnumeration;
/*  99 */     try { for (localEnumeration = paramKeyStore.aliases(); localEnumeration.hasMoreElements();) {
/* 100 */         String str = (String)localEnumeration.nextElement();
/* 101 */         Object localObject; if (paramKeyStore.isCertificateEntry(str)) {
/* 102 */           localObject = paramKeyStore.getCertificate(str);
/* 103 */           if ((localObject instanceof X509Certificate)) {
/* 104 */             localHashSet.add((X509Certificate)localObject);
/*     */           }
/* 106 */         } else if (paramKeyStore.isKeyEntry(str)) {
/* 107 */           localObject = paramKeyStore.getCertificateChain(str);
/* 108 */           if ((localObject != null) && (localObject.length > 0) && ((localObject[0] instanceof X509Certificate)))
/*     */           {
/* 110 */             localHashSet.add((X509Certificate)localObject[0]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (KeyStoreException localKeyStoreException) {}
/*     */     
/* 117 */     return localHashSet;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\validator\KeyStores.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
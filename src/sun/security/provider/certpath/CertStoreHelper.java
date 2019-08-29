/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.security.AccessController;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.X509CRLSelector;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.Cache;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class CertStoreHelper
/*     */ {
/*     */   private static final int NUM_TYPES = 2;
/*  54 */   private static final Map<String, String> classMap = new HashMap(2);
/*     */   
/*  56 */   static { classMap.put("LDAP", "sun.security.provider.certpath.ldap.LDAPCertStoreHelper");
/*     */     
/*     */ 
/*  59 */     classMap.put("SSLServer", "sun.security.provider.certpath.ssl.SSLServerCertStoreHelper");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  64 */   private static Cache<String, CertStoreHelper> cache = Cache.newSoftMemoryCache(2);
/*     */   
/*     */   public static CertStoreHelper getInstance(final String paramString)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/*  69 */     CertStoreHelper localCertStoreHelper = (CertStoreHelper)cache.get(paramString);
/*  70 */     if (localCertStoreHelper != null) {
/*  71 */       return localCertStoreHelper;
/*     */     }
/*  73 */     String str = (String)classMap.get(paramString);
/*  74 */     if (str == null) {
/*  75 */       throw new NoSuchAlgorithmException(paramString + " not available");
/*     */     }
/*     */     try {
/*  78 */       (CertStoreHelper)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public CertStoreHelper run() throws ClassNotFoundException {
/*     */           try {
/*  82 */             Class localClass = Class.forName(this.val$cl, true, null);
/*     */             
/*  84 */             CertStoreHelper localCertStoreHelper = (CertStoreHelper)localClass.newInstance();
/*  85 */             CertStoreHelper.cache.put(paramString, localCertStoreHelper);
/*  86 */             return localCertStoreHelper;
/*     */           }
/*     */           catch (InstantiationException|IllegalAccessException localInstantiationException) {
/*  89 */             throw new AssertionError(localInstantiationException);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException)
/*     */     {
/*  96 */       throw new NoSuchAlgorithmException(paramString + " not available", localPrivilegedActionException.getException());
/*     */     }
/*     */   }
/*     */   
/*     */   static boolean isCausedByNetworkIssue(String paramString, CertStoreException paramCertStoreException) {
/* 101 */     switch (paramString) {
/*     */     case "LDAP": 
/*     */     case "SSLServer": 
/*     */       try {
/* 105 */         CertStoreHelper localCertStoreHelper = getInstance(paramString);
/* 106 */         return localCertStoreHelper.isCausedByNetworkIssue(paramCertStoreException);
/*     */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 108 */         return false;
/*     */       }
/*     */     case "URI": 
/* 111 */       Throwable localThrowable = paramCertStoreException.getCause();
/* 112 */       return (localThrowable != null) && ((localThrowable instanceof IOException));
/*     */     }
/*     */     
/* 115 */     return false;
/*     */   }
/*     */   
/*     */   public abstract CertStore getCertStore(URI paramURI)
/*     */     throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
/*     */   
/*     */   public abstract X509CertSelector wrap(X509CertSelector paramX509CertSelector, X500Principal paramX500Principal, String paramString)
/*     */     throws IOException;
/*     */   
/*     */   public abstract X509CRLSelector wrap(X509CRLSelector paramX509CRLSelector, Collection<X500Principal> paramCollection, String paramString)
/*     */     throws IOException;
/*     */   
/*     */   public abstract boolean isCausedByNetworkIssue(CertStoreException paramCertStoreException);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\CertStoreHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
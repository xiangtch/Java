/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.security.AccessController;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Provider;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CRLSelector;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CertStoreParameters;
/*     */ import java.security.cert.CertStoreSpi;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509CRLSelector;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import sun.security.action.GetIntegerAction;
/*     */ import sun.security.util.Cache;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AccessDescription;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.GeneralNameInterface;
/*     */ import sun.security.x509.URIName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class URICertStore
/*     */   extends CertStoreSpi
/*     */ {
/*  91 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int CHECK_INTERVAL = 30000;
/*     */   
/*     */ 
/*     */   private static final int CACHE_SIZE = 185;
/*     */   
/*     */ 
/*     */   private final CertificateFactory factory;
/*     */   
/*     */ 
/* 104 */   private Collection<X509Certificate> certs = Collections.emptySet();
/*     */   
/*     */ 
/*     */ 
/*     */   private X509CRL crl;
/*     */   
/*     */ 
/*     */   private long lastChecked;
/*     */   
/*     */ 
/*     */   private long lastModified;
/*     */   
/*     */ 
/*     */   private URI uri;
/*     */   
/*     */ 
/* 120 */   private boolean ldap = false;
/*     */   
/*     */ 
/*     */   private CertStoreHelper ldapHelper;
/*     */   
/*     */ 
/*     */   private CertStore ldapCertStore;
/*     */   
/*     */ 
/*     */   private String ldapPath;
/*     */   
/*     */ 
/*     */   private static final int DEFAULT_CRL_CONNECT_TIMEOUT = 15000;
/*     */   
/* 134 */   private static final int CRL_CONNECT_TIMEOUT = initializeTimeout();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int initializeTimeout()
/*     */   {
/* 142 */     Integer localInteger = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.crl.timeout"));
/*     */     
/* 144 */     if ((localInteger == null) || (localInteger.intValue() < 0)) {
/* 145 */       return 15000;
/*     */     }
/*     */     
/*     */ 
/* 149 */     return localInteger.intValue() * 1000;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   URICertStore(CertStoreParameters paramCertStoreParameters)
/*     */     throws InvalidAlgorithmParameterException, NoSuchAlgorithmException
/*     */   {
/* 159 */     super(paramCertStoreParameters);
/* 160 */     if (!(paramCertStoreParameters instanceof URICertStoreParameters)) {
/* 161 */       throw new InvalidAlgorithmParameterException("params must be instanceof URICertStoreParameters");
/*     */     }
/*     */     
/* 164 */     this.uri = ((URICertStoreParameters)paramCertStoreParameters).uri;
/*     */     
/* 166 */     if (this.uri.getScheme().toLowerCase(Locale.ENGLISH).equals("ldap")) {
/* 167 */       this.ldap = true;
/* 168 */       this.ldapHelper = CertStoreHelper.getInstance("LDAP");
/* 169 */       this.ldapCertStore = this.ldapHelper.getCertStore(this.uri);
/* 170 */       this.ldapPath = this.uri.getPath();
/*     */       
/* 172 */       if (this.ldapPath.charAt(0) == '/') {
/* 173 */         this.ldapPath = this.ldapPath.substring(1);
/*     */       }
/*     */     }
/*     */     try {
/* 177 */       this.factory = CertificateFactory.getInstance("X.509");
/*     */     } catch (CertificateException localCertificateException) {
/* 179 */       throw new RuntimeException();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 188 */   private static final Cache<URICertStoreParameters, CertStore> certStoreCache = Cache.newSoftMemoryCache(185);
/*     */   
/*     */   static synchronized CertStore getInstance(URICertStoreParameters paramURICertStoreParameters) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
/* 191 */     if (debug != null) {
/* 192 */       debug.println("CertStore URI:" + paramURICertStoreParameters.uri);
/*     */     }
/* 194 */     Object localObject = (CertStore)certStoreCache.get(paramURICertStoreParameters);
/* 195 */     if (localObject == null) {
/* 196 */       localObject = new UCS(new URICertStore(paramURICertStoreParameters), null, "URI", paramURICertStoreParameters);
/* 197 */       certStoreCache.put(paramURICertStoreParameters, localObject);
/*     */     }
/* 199 */     else if (debug != null) {
/* 200 */       debug.println("URICertStore.getInstance: cache hit");
/*     */     }
/*     */     
/* 203 */     return (CertStore)localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static CertStore getInstance(AccessDescription paramAccessDescription)
/*     */   {
/* 211 */     if (!paramAccessDescription.getAccessMethod().equals(AccessDescription.Ad_CAISSUERS_Id))
/*     */     {
/* 213 */       return null;
/*     */     }
/* 215 */     GeneralNameInterface localGeneralNameInterface = paramAccessDescription.getAccessLocation().getName();
/* 216 */     if (!(localGeneralNameInterface instanceof URIName)) {
/* 217 */       return null;
/*     */     }
/* 219 */     URI localURI = ((URIName)localGeneralNameInterface).getURI();
/*     */     try {
/* 221 */       return 
/* 222 */         getInstance(new URICertStoreParameters(localURI));
/*     */     } catch (Exception localException) {
/* 224 */       if (debug != null) {
/* 225 */         debug.println("exception creating CertStore: " + localException);
/* 226 */         localException.printStackTrace();
/*     */       } }
/* 228 */     return null;
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
/*     */   public synchronized Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector)
/*     */     throws CertStoreException
/*     */   {
/* 251 */     if (this.ldap) {
/* 252 */       X509CertSelector localX509CertSelector = (X509CertSelector)paramCertSelector;
/*     */       try {
/* 254 */         localX509CertSelector = this.ldapHelper.wrap(localX509CertSelector, localX509CertSelector.getSubject(), this.ldapPath);
/*     */       } catch (IOException localIOException1) {
/* 256 */         throw new CertStoreException(localIOException1);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 261 */       return 
/* 262 */         this.ldapCertStore.getCertificates(localX509CertSelector);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 268 */     long l1 = System.currentTimeMillis();
/* 269 */     if (l1 - this.lastChecked < 30000L) {
/* 270 */       if (debug != null) {
/* 271 */         debug.println("Returning certificates from cache");
/*     */       }
/* 273 */       return getMatchingCerts(this.certs, paramCertSelector);
/*     */     }
/* 275 */     this.lastChecked = l1;
/*     */     try {
/* 277 */       URLConnection localURLConnection = this.uri.toURL().openConnection();
/* 278 */       if (this.lastModified != 0L) {
/* 279 */         localURLConnection.setIfModifiedSince(this.lastModified);
/*     */       }
/* 281 */       long l2 = this.lastModified;
/* 282 */       InputStream localInputStream = localURLConnection.getInputStream();Object localObject1 = null;
/* 283 */       try { this.lastModified = localURLConnection.getLastModified();
/* 284 */         if (l2 != 0L) { Object localObject2;
/* 285 */           if (l2 == this.lastModified) {
/* 286 */             if (debug != null) {
/* 287 */               debug.println("Not modified, using cached copy");
/*     */             }
/* 289 */             return getMatchingCerts(this.certs, paramCertSelector); }
/* 290 */           if ((localURLConnection instanceof HttpURLConnection))
/*     */           {
/* 292 */             localObject2 = (HttpURLConnection)localURLConnection;
/* 293 */             if (((HttpURLConnection)localObject2).getResponseCode() == 304)
/*     */             {
/* 295 */               if (debug != null) {
/* 296 */                 debug.println("Not modified, using cached copy");
/*     */               }
/* 298 */               return getMatchingCerts(this.certs, paramCertSelector);
/*     */             }
/*     */           }
/*     */         }
/* 302 */         if (debug != null) {
/* 303 */           debug.println("Downloading new certificates...");
/*     */         }
/*     */         
/*     */ 
/* 307 */         this.certs = this.factory.generateCertificates(localInputStream);
/*     */       }
/*     */       catch (Throwable localThrowable2)
/*     */       {
/* 282 */         localObject1 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 308 */         if (localInputStream != null) if (localObject1 != null) try { localInputStream.close(); } catch (Throwable localThrowable5) { ((Throwable)localObject1).addSuppressed(localThrowable5); } else localInputStream.close(); }
/* 309 */       return getMatchingCerts(this.certs, paramCertSelector);
/*     */     } catch (IOException|CertificateException localIOException2) {
/* 311 */       if (debug != null) {
/* 312 */         debug.println("Exception fetching certificates:");
/* 313 */         localIOException2.printStackTrace();
/*     */       }
/*     */       
/*     */ 
/* 317 */       this.lastModified = 0L;
/* 318 */       this.certs = Collections.emptySet(); }
/* 319 */     return this.certs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Collection<X509Certificate> getMatchingCerts(Collection<X509Certificate> paramCollection, CertSelector paramCertSelector)
/*     */   {
/* 330 */     if (paramCertSelector == null) {
/* 331 */       return paramCollection;
/*     */     }
/* 333 */     ArrayList localArrayList = new ArrayList(paramCollection.size());
/* 334 */     for (X509Certificate localX509Certificate : paramCollection) {
/* 335 */       if (paramCertSelector.match(localX509Certificate)) {
/* 336 */         localArrayList.add(localX509Certificate);
/*     */       }
/*     */     }
/* 339 */     return localArrayList;
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
/*     */   public synchronized Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector)
/*     */     throws CertStoreException
/*     */   {
/* 361 */     if (this.ldap) {
/* 362 */       X509CRLSelector localX509CRLSelector = (X509CRLSelector)paramCRLSelector;
/*     */       try {
/* 364 */         localX509CRLSelector = this.ldapHelper.wrap(localX509CRLSelector, null, this.ldapPath);
/*     */       } catch (IOException localIOException1) {
/* 366 */         throw new CertStoreException(localIOException1);
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 372 */         return this.ldapCertStore.getCRLs(localX509CRLSelector);
/*     */       } catch (CertStoreException localCertStoreException) {
/* 374 */         throw new PKIX.CertStoreTypeException("LDAP", localCertStoreException);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 381 */     long l1 = System.currentTimeMillis();
/* 382 */     if (l1 - this.lastChecked < 30000L) {
/* 383 */       if (debug != null) {
/* 384 */         debug.println("Returning CRL from cache");
/*     */       }
/* 386 */       return getMatchingCRLs(this.crl, paramCRLSelector);
/*     */     }
/* 388 */     this.lastChecked = l1;
/*     */     try {
/* 390 */       URLConnection localURLConnection = this.uri.toURL().openConnection();
/* 391 */       if (this.lastModified != 0L) {
/* 392 */         localURLConnection.setIfModifiedSince(this.lastModified);
/*     */       }
/* 394 */       long l2 = this.lastModified;
/* 395 */       localURLConnection.setConnectTimeout(CRL_CONNECT_TIMEOUT);
/* 396 */       InputStream localInputStream = localURLConnection.getInputStream();Object localObject1 = null;
/* 397 */       try { this.lastModified = localURLConnection.getLastModified();
/* 398 */         if (l2 != 0L) { Object localObject2;
/* 399 */           if (l2 == this.lastModified) {
/* 400 */             if (debug != null) {
/* 401 */               debug.println("Not modified, using cached copy");
/*     */             }
/* 403 */             return getMatchingCRLs(this.crl, paramCRLSelector); }
/* 404 */           if ((localURLConnection instanceof HttpURLConnection))
/*     */           {
/* 406 */             localObject2 = (HttpURLConnection)localURLConnection;
/* 407 */             if (((HttpURLConnection)localObject2).getResponseCode() == 304)
/*     */             {
/* 409 */               if (debug != null) {
/* 410 */                 debug.println("Not modified, using cached copy");
/*     */               }
/* 412 */               return getMatchingCRLs(this.crl, paramCRLSelector);
/*     */             }
/*     */           }
/*     */         }
/* 416 */         if (debug != null) {
/* 417 */           debug.println("Downloading new CRL...");
/*     */         }
/* 419 */         this.crl = ((X509CRL)this.factory.generateCRL(localInputStream));
/*     */       }
/*     */       catch (Throwable localThrowable2)
/*     */       {
/* 396 */         localObject1 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 420 */         if (localInputStream != null) if (localObject1 != null) try { localInputStream.close(); } catch (Throwable localThrowable5) { ((Throwable)localObject1).addSuppressed(localThrowable5); } else localInputStream.close(); }
/* 421 */       return getMatchingCRLs(this.crl, paramCRLSelector);
/*     */     } catch (IOException|CRLException localIOException2) {
/* 423 */       if (debug != null) {
/* 424 */         debug.println("Exception fetching CRL:");
/* 425 */         localIOException2.printStackTrace();
/*     */       }
/*     */       
/* 428 */       this.lastModified = 0L;
/* 429 */       this.crl = null;
/* 430 */       throw new PKIX.CertStoreTypeException("URI", new CertStoreException(localIOException2));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Collection<X509CRL> getMatchingCRLs(X509CRL paramX509CRL, CRLSelector paramCRLSelector)
/*     */   {
/* 441 */     if ((paramCRLSelector == null) || ((paramX509CRL != null) && (paramCRLSelector.match(paramX509CRL)))) {
/* 442 */       return Collections.singletonList(paramX509CRL);
/*     */     }
/* 444 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */   static class URICertStoreParameters
/*     */     implements CertStoreParameters
/*     */   {
/*     */     private final URI uri;
/*     */     
/* 453 */     private volatile int hashCode = 0;
/*     */     
/* 455 */     URICertStoreParameters(URI paramURI) { this.uri = paramURI; }
/*     */     
/*     */     public boolean equals(Object paramObject) {
/* 458 */       if (!(paramObject instanceof URICertStoreParameters)) {
/* 459 */         return false;
/*     */       }
/* 461 */       URICertStoreParameters localURICertStoreParameters = (URICertStoreParameters)paramObject;
/* 462 */       return this.uri.equals(localURICertStoreParameters.uri);
/*     */     }
/*     */     
/* 465 */     public int hashCode() { if (this.hashCode == 0) {
/* 466 */         int i = 17;
/* 467 */         i = 37 * i + this.uri.hashCode();
/* 468 */         this.hashCode = i;
/*     */       }
/* 470 */       return this.hashCode;
/*     */     }
/*     */     
/*     */     public Object clone() {
/* 474 */       try { return super.clone();
/*     */       }
/*     */       catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 477 */         throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class UCS
/*     */     extends CertStore
/*     */   {
/*     */     protected UCS(CertStoreSpi paramCertStoreSpi, Provider paramProvider, String paramString, CertStoreParameters paramCertStoreParameters)
/*     */     {
/* 488 */       super(paramProvider, paramString, paramCertStoreParameters);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\URICertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.security.provider.certpath.ssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.Provider;
/*     */ import java.security.cert.CRLSelector;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CertStoreParameters;
/*     */ import java.security.cert.CertStoreSpi;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509ExtendedTrustManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SSLServerCertStore
/*     */   extends CertStoreSpi
/*     */ {
/*     */   private final URI uri;
/*  69 */   private static final GetChainTrustManager trustManager = new GetChainTrustManager(null);
/*  70 */   private static final SSLSocketFactory socketFactory; private static final HostnameVerifier hostnameVerifier = new HostnameVerifier()
/*     */   {
/*  72 */     public boolean verify(String paramAnonymousString, SSLSession paramAnonymousSSLSession) { return true; }
/*     */   };
/*     */   
/*     */   static {
/*     */     SSLSocketFactory localSSLSocketFactory;
/*     */     try {
/*  78 */       SSLContext localSSLContext = SSLContext.getInstance("SSL");
/*  79 */       localSSLContext.init(null, new TrustManager[] { trustManager }, null);
/*  80 */       localSSLSocketFactory = localSSLContext.getSocketFactory();
/*     */     } catch (GeneralSecurityException localGeneralSecurityException) {
/*  82 */       localSSLSocketFactory = null;
/*     */     }
/*     */     
/*  85 */     socketFactory = localSSLSocketFactory;
/*     */   }
/*     */   
/*     */   SSLServerCertStore(URI paramURI) throws InvalidAlgorithmParameterException {
/*  89 */     super(null);
/*  90 */     this.uri = paramURI;
/*     */   }
/*     */   
/*     */   public Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector) throws CertStoreException
/*     */   {
/*     */     try
/*     */     {
/*  97 */       URLConnection localURLConnection = this.uri.toURL().openConnection();
/*  98 */       if ((localURLConnection instanceof HttpsURLConnection)) {
/*  99 */         if (socketFactory == null) {
/* 100 */           throw new CertStoreException("No initialized SSLSocketFactory");
/*     */         }
/*     */         
/*     */ 
/* 104 */         HttpsURLConnection localHttpsURLConnection = (HttpsURLConnection)localURLConnection;
/* 105 */         localHttpsURLConnection.setSSLSocketFactory(socketFactory);
/* 106 */         localHttpsURLConnection.setHostnameVerifier(hostnameVerifier);
/* 107 */         synchronized (trustManager) {
/*     */           try {
/* 109 */             localHttpsURLConnection.connect();
/* 110 */             List localList1 = getMatchingCerts(
/* 111 */               trustManager.serverChain, paramCertSelector);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */             trustManager.cleanup();return localList1;
/*     */ 
/*     */           }
/*     */           catch (IOException localIOException2)
/*     */           {
/* 115 */             if (trustManager.exchangedServerCerts) {
/* 116 */               List localList2 = getMatchingCerts(
/* 117 */                 trustManager.serverChain, paramCertSelector);
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */               trustManager.cleanup();return localList2;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 121 */             throw localIOException2;
/*     */           } finally {
/* 123 */             trustManager.cleanup();
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (IOException localIOException1) {
/* 128 */       throw new CertStoreException(localIOException1);
/*     */     }
/*     */     
/* 131 */     return Collections.emptySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static List<X509Certificate> getMatchingCerts(List<X509Certificate> paramList, CertSelector paramCertSelector)
/*     */   {
/* 138 */     if (paramCertSelector == null) {
/* 139 */       return paramList;
/*     */     }
/* 141 */     ArrayList localArrayList = new ArrayList(paramList.size());
/* 142 */     for (X509Certificate localX509Certificate : paramList) {
/* 143 */       if (paramCertSelector.match(localX509Certificate)) {
/* 144 */         localArrayList.add(localX509Certificate);
/*     */       }
/*     */     }
/* 147 */     return localArrayList;
/*     */   }
/*     */   
/*     */   public Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector)
/*     */     throws CertStoreException
/*     */   {
/* 153 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   static CertStore getInstance(URI paramURI)
/*     */     throws InvalidAlgorithmParameterException
/*     */   {
/* 159 */     return new CS(new SSLServerCertStore(paramURI), null, "SSLServer", null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class GetChainTrustManager
/*     */     extends X509ExtendedTrustManager
/*     */   {
/* 170 */     private List<X509Certificate> serverChain = Collections.emptyList();
/* 171 */     private boolean exchangedServerCerts = false;
/*     */     
/*     */     public X509Certificate[] getAcceptedIssuers()
/*     */     {
/* 175 */       return new X509Certificate[0];
/*     */     }
/*     */     
/*     */ 
/*     */     public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
/*     */       throws CertificateException
/*     */     {
/* 182 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */ 
/*     */     public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
/*     */       throws CertificateException
/*     */     {
/* 189 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */ 
/*     */     public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
/*     */       throws CertificateException
/*     */     {
/* 196 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */ 
/*     */     public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
/*     */       throws CertificateException
/*     */     {
/* 203 */       this.exchangedServerCerts = true;
/*     */       
/*     */ 
/* 206 */       this.serverChain = (paramArrayOfX509Certificate == null ? Collections.emptyList() : Arrays.asList(paramArrayOfX509Certificate));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
/*     */       throws CertificateException
/*     */     {
/* 214 */       checkServerTrusted(paramArrayOfX509Certificate, paramString);
/*     */     }
/*     */     
/*     */ 
/*     */     public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
/*     */       throws CertificateException
/*     */     {
/* 221 */       checkServerTrusted(paramArrayOfX509Certificate, paramString);
/*     */     }
/*     */     
/*     */     void cleanup() {
/* 225 */       this.exchangedServerCerts = false;
/* 226 */       this.serverChain = Collections.emptyList();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class CS
/*     */     extends CertStore
/*     */   {
/*     */     protected CS(CertStoreSpi paramCertStoreSpi, Provider paramProvider, String paramString, CertStoreParameters paramCertStoreParameters)
/*     */     {
/* 237 */       super(paramProvider, paramString, paramCertStoreParameters);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\ssl\SSLServerCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
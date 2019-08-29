/*     */ package sun.net.www.protocol.https;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.ProtocolException;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.security.Permission;
/*     */ import java.security.Principal;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.security.cert.X509Certificate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpsURLConnectionImpl
/*     */   extends HttpsURLConnection
/*     */ {
/*     */   protected DelegateHttpsURLConnection delegate;
/*     */   
/*     */   HttpsURLConnectionImpl(URL paramURL, Handler paramHandler)
/*     */     throws IOException
/*     */   {
/*  80 */     this(paramURL, null, paramHandler);
/*     */   }
/*     */   
/*     */   static URL checkURL(URL paramURL) throws IOException {
/*  84 */     if ((paramURL != null) && 
/*  85 */       (paramURL.toExternalForm().indexOf('\n') > -1)) {
/*  86 */       throw new MalformedURLException("Illegal character in URL");
/*     */     }
/*     */     
/*  89 */     return paramURL;
/*     */   }
/*     */   
/*     */   HttpsURLConnectionImpl(URL paramURL, Proxy paramProxy, Handler paramHandler) throws IOException
/*     */   {
/*  94 */     super(checkURL(paramURL));
/*  95 */     this.delegate = new DelegateHttpsURLConnection(this.url, paramProxy, paramHandler, this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected HttpsURLConnectionImpl(URL paramURL)
/*     */     throws IOException
/*     */   {
/* 102 */     super(paramURL);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setNewClient(URL paramURL)
/*     */     throws IOException
/*     */   {
/* 112 */     this.delegate.setNewClient(paramURL, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setNewClient(URL paramURL, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 124 */     this.delegate.setNewClient(paramURL, paramBoolean);
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
/*     */   protected void setProxiedClient(URL paramURL, String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 138 */     this.delegate.setProxiedClient(paramURL, paramString, paramInt);
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
/*     */   protected void setProxiedClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 154 */     this.delegate.setProxiedClient(paramURL, paramString, paramInt, paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void connect()
/*     */     throws IOException
/*     */   {
/* 162 */     this.delegate.connect();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean isConnected()
/*     */   {
/* 171 */     return this.delegate.isConnected();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setConnected(boolean paramBoolean)
/*     */   {
/* 180 */     this.delegate.setConnected(paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getCipherSuite()
/*     */   {
/* 187 */     return this.delegate.getCipherSuite();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Certificate[] getLocalCertificates()
/*     */   {
/* 196 */     return this.delegate.getLocalCertificates();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Certificate[] getServerCertificates()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/* 206 */     return this.delegate.getServerCertificates();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate[] getServerCertificateChain()
/*     */   {
/*     */     try
/*     */     {
/* 219 */       return this.delegate.getServerCertificateChain();
/*     */     }
/*     */     catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException) {}
/*     */     
/*     */ 
/* 224 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Principal getPeerPrincipal()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/* 235 */     return this.delegate.getPeerPrincipal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Principal getLocalPrincipal()
/*     */   {
/* 244 */     return this.delegate.getLocalPrincipal();
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
/*     */   public synchronized OutputStream getOutputStream()
/*     */     throws IOException
/*     */   {
/* 259 */     return this.delegate.getOutputStream();
/*     */   }
/*     */   
/*     */   public synchronized InputStream getInputStream() throws IOException {
/* 263 */     return this.delegate.getInputStream();
/*     */   }
/*     */   
/*     */   public InputStream getErrorStream() {
/* 267 */     return this.delegate.getErrorStream();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void disconnect()
/*     */   {
/* 274 */     this.delegate.disconnect();
/*     */   }
/*     */   
/*     */   public boolean usingProxy() {
/* 278 */     return this.delegate.usingProxy();
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
/*     */   public Map<String, List<String>> getHeaderFields()
/*     */   {
/* 292 */     return this.delegate.getHeaderFields();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHeaderField(String paramString)
/*     */   {
/* 300 */     return this.delegate.getHeaderField(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHeaderField(int paramInt)
/*     */   {
/* 308 */     return this.delegate.getHeaderField(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHeaderFieldKey(int paramInt)
/*     */   {
/* 316 */     return this.delegate.getHeaderFieldKey(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRequestProperty(String paramString1, String paramString2)
/*     */   {
/* 325 */     this.delegate.setRequestProperty(paramString1, paramString2);
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
/*     */   public void addRequestProperty(String paramString1, String paramString2)
/*     */   {
/* 340 */     this.delegate.addRequestProperty(paramString1, paramString2);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseCode()
/*     */     throws IOException
/*     */   {
/* 347 */     return this.delegate.getResponseCode();
/*     */   }
/*     */   
/*     */   public String getRequestProperty(String paramString) {
/* 351 */     return this.delegate.getRequestProperty(paramString);
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
/*     */   public Map<String, List<String>> getRequestProperties()
/*     */   {
/* 367 */     return this.delegate.getRequestProperties();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setInstanceFollowRedirects(boolean paramBoolean)
/*     */   {
/* 375 */     this.delegate.setInstanceFollowRedirects(paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getInstanceFollowRedirects() {
/* 379 */     return this.delegate.getInstanceFollowRedirects();
/*     */   }
/*     */   
/*     */   public void setRequestMethod(String paramString) throws ProtocolException {
/* 383 */     this.delegate.setRequestMethod(paramString);
/*     */   }
/*     */   
/*     */   public String getRequestMethod() {
/* 387 */     return this.delegate.getRequestMethod();
/*     */   }
/*     */   
/*     */   public String getResponseMessage() throws IOException {
/* 391 */     return this.delegate.getResponseMessage();
/*     */   }
/*     */   
/*     */   public long getHeaderFieldDate(String paramString, long paramLong) {
/* 395 */     return this.delegate.getHeaderFieldDate(paramString, paramLong);
/*     */   }
/*     */   
/*     */   public Permission getPermission() throws IOException {
/* 399 */     return this.delegate.getPermission();
/*     */   }
/*     */   
/*     */   public URL getURL() {
/* 403 */     return this.delegate.getURL();
/*     */   }
/*     */   
/*     */   public int getContentLength() {
/* 407 */     return this.delegate.getContentLength();
/*     */   }
/*     */   
/*     */   public long getContentLengthLong() {
/* 411 */     return this.delegate.getContentLengthLong();
/*     */   }
/*     */   
/*     */   public String getContentType() {
/* 415 */     return this.delegate.getContentType();
/*     */   }
/*     */   
/*     */   public String getContentEncoding() {
/* 419 */     return this.delegate.getContentEncoding();
/*     */   }
/*     */   
/*     */   public long getExpiration() {
/* 423 */     return this.delegate.getExpiration();
/*     */   }
/*     */   
/*     */   public long getDate() {
/* 427 */     return this.delegate.getDate();
/*     */   }
/*     */   
/*     */   public long getLastModified() {
/* 431 */     return this.delegate.getLastModified();
/*     */   }
/*     */   
/*     */   public int getHeaderFieldInt(String paramString, int paramInt) {
/* 435 */     return this.delegate.getHeaderFieldInt(paramString, paramInt);
/*     */   }
/*     */   
/*     */   public long getHeaderFieldLong(String paramString, long paramLong) {
/* 439 */     return this.delegate.getHeaderFieldLong(paramString, paramLong);
/*     */   }
/*     */   
/*     */   public Object getContent() throws IOException {
/* 443 */     return this.delegate.getContent();
/*     */   }
/*     */   
/*     */   public Object getContent(Class[] paramArrayOfClass) throws IOException
/*     */   {
/* 448 */     return this.delegate.getContent(paramArrayOfClass);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 452 */     return this.delegate.toString();
/*     */   }
/*     */   
/*     */   public void setDoInput(boolean paramBoolean) {
/* 456 */     this.delegate.setDoInput(paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getDoInput() {
/* 460 */     return this.delegate.getDoInput();
/*     */   }
/*     */   
/*     */   public void setDoOutput(boolean paramBoolean) {
/* 464 */     this.delegate.setDoOutput(paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getDoOutput() {
/* 468 */     return this.delegate.getDoOutput();
/*     */   }
/*     */   
/*     */   public void setAllowUserInteraction(boolean paramBoolean) {
/* 472 */     this.delegate.setAllowUserInteraction(paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getAllowUserInteraction() {
/* 476 */     return this.delegate.getAllowUserInteraction();
/*     */   }
/*     */   
/*     */   public void setUseCaches(boolean paramBoolean) {
/* 480 */     this.delegate.setUseCaches(paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getUseCaches() {
/* 484 */     return this.delegate.getUseCaches();
/*     */   }
/*     */   
/*     */   public void setIfModifiedSince(long paramLong) {
/* 488 */     this.delegate.setIfModifiedSince(paramLong);
/*     */   }
/*     */   
/*     */   public long getIfModifiedSince() {
/* 492 */     return this.delegate.getIfModifiedSince();
/*     */   }
/*     */   
/*     */   public boolean getDefaultUseCaches() {
/* 496 */     return this.delegate.getDefaultUseCaches();
/*     */   }
/*     */   
/*     */   public void setDefaultUseCaches(boolean paramBoolean) {
/* 500 */     this.delegate.setDefaultUseCaches(paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/* 509 */     this.delegate.dispose();
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 513 */     return this.delegate.equals(paramObject);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 517 */     return this.delegate.hashCode();
/*     */   }
/*     */   
/*     */   public void setConnectTimeout(int paramInt) {
/* 521 */     this.delegate.setConnectTimeout(paramInt);
/*     */   }
/*     */   
/*     */   public int getConnectTimeout() {
/* 525 */     return this.delegate.getConnectTimeout();
/*     */   }
/*     */   
/*     */   public void setReadTimeout(int paramInt) {
/* 529 */     this.delegate.setReadTimeout(paramInt);
/*     */   }
/*     */   
/*     */   public int getReadTimeout() {
/* 533 */     return this.delegate.getReadTimeout();
/*     */   }
/*     */   
/*     */   public void setFixedLengthStreamingMode(int paramInt) {
/* 537 */     this.delegate.setFixedLengthStreamingMode(paramInt);
/*     */   }
/*     */   
/*     */   public void setFixedLengthStreamingMode(long paramLong) {
/* 541 */     this.delegate.setFixedLengthStreamingMode(paramLong);
/*     */   }
/*     */   
/*     */   public void setChunkedStreamingMode(int paramInt) {
/* 545 */     this.delegate.setChunkedStreamingMode(paramInt);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\https\HttpsURLConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
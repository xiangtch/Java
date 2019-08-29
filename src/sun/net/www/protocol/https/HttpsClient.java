/*     */ package sun.net.www.protocol.https;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import java.security.Principal;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.net.ssl.HandshakeCompletedEvent;
/*     */ import javax.net.ssl.HandshakeCompletedListener;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.SSLParameters;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import sun.net.www.http.HttpClient;
/*     */ import sun.net.www.http.KeepAliveCache;
/*     */ import sun.net.www.protocol.http.HttpURLConnection;
/*     */ import sun.net.www.protocol.http.HttpURLConnection.TunnelState;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.ssl.SSLSocketImpl;
/*     */ import sun.security.util.HostnameChecker;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ import sun.util.logging.PlatformLogger.Level;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class HttpsClient
/*     */   extends HttpClient
/*     */   implements HandshakeCompletedListener
/*     */ {
/*     */   private static final int httpsPortNumber = 443;
/*     */   private static final String defaultHVCanonicalName = "javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier";
/*     */   private HostnameVerifier hv;
/*     */   private SSLSocketFactory sslSocketFactory;
/*     */   private SSLSession session;
/*     */   
/*     */   protected int getDefaultPort()
/*     */   {
/* 122 */     return 443;
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
/*     */   private String[] getCipherSuites()
/*     */   {
/* 142 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("https.cipherSuites"));
/*     */     
/*     */     String[] arrayOfString;
/* 145 */     if ((str == null) || ("".equals(str))) {
/* 146 */       arrayOfString = null;
/*     */     }
/*     */     else {
/* 149 */       Vector localVector = new Vector();
/*     */       
/* 151 */       StringTokenizer localStringTokenizer = new StringTokenizer(str, ",");
/* 152 */       while (localStringTokenizer.hasMoreTokens())
/* 153 */         localVector.addElement(localStringTokenizer.nextToken());
/* 154 */       arrayOfString = new String[localVector.size()];
/* 155 */       for (int i = 0; i < arrayOfString.length; i++)
/* 156 */         arrayOfString[i] = ((String)localVector.elementAt(i));
/*     */     }
/* 158 */     return arrayOfString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String[] getProtocols()
/*     */   {
/* 166 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("https.protocols"));
/*     */     
/*     */     String[] arrayOfString;
/* 169 */     if ((str == null) || ("".equals(str))) {
/* 170 */       arrayOfString = null;
/*     */     }
/*     */     else {
/* 173 */       Vector localVector = new Vector();
/*     */       
/* 175 */       StringTokenizer localStringTokenizer = new StringTokenizer(str, ",");
/* 176 */       while (localStringTokenizer.hasMoreTokens())
/* 177 */         localVector.addElement(localStringTokenizer.nextToken());
/* 178 */       arrayOfString = new String[localVector.size()];
/* 179 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 180 */         arrayOfString[i] = ((String)localVector.elementAt(i));
/*     */       }
/*     */     }
/* 183 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   private String getUserAgent() {
/* 187 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("https.agent"));
/*     */     
/* 189 */     if ((str == null) || (str.length() == 0)) {
/* 190 */       str = "JSSE";
/*     */     }
/* 192 */     return str;
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
/*     */   private HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL)
/*     */     throws IOException
/*     */   {
/* 217 */     this(paramSSLSocketFactory, paramURL, (String)null, -1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/* 226 */     this(paramSSLSocketFactory, paramURL, paramString, paramInt, -1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, String paramString, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 236 */     this(paramSSLSocketFactory, paramURL, paramString == null ? null : 
/*     */     
/* 238 */       HttpClient.newHttpProxy(paramString, paramInt1, "https"), paramInt2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, Proxy paramProxy, int paramInt)
/*     */     throws IOException
/*     */   {
/* 248 */     PlatformLogger localPlatformLogger = HttpURLConnection.getHttpLogger();
/* 249 */     if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
/* 250 */       localPlatformLogger.finest("Creating new HttpsClient with url:" + paramURL + " and proxy:" + paramProxy + " with connect timeout:" + paramInt);
/*     */     }
/*     */     
/* 253 */     this.proxy = paramProxy;
/* 254 */     setSSLSocketFactory(paramSSLSocketFactory);
/* 255 */     this.proxyDisabled = true;
/*     */     
/* 257 */     this.host = paramURL.getHost();
/* 258 */     this.url = paramURL;
/* 259 */     this.port = paramURL.getPort();
/* 260 */     if (this.port == -1) {
/* 261 */       this.port = getDefaultPort();
/*     */     }
/* 263 */     setConnectTimeout(paramInt);
/* 264 */     openServer();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, HttpURLConnection paramHttpURLConnection)
/*     */     throws IOException
/*     */   {
/* 274 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, true, paramHttpURLConnection);
/*     */   }
/*     */   
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, boolean paramBoolean, HttpURLConnection paramHttpURLConnection)
/*     */     throws IOException
/*     */   {
/* 281 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, (String)null, -1, paramBoolean, paramHttpURLConnection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt, HttpURLConnection paramHttpURLConnection)
/*     */     throws IOException
/*     */   {
/* 291 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString, paramInt, true, paramHttpURLConnection);
/*     */   }
/*     */   
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt, boolean paramBoolean, HttpURLConnection paramHttpURLConnection)
/*     */     throws IOException
/*     */   {
/* 298 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString, paramInt, paramBoolean, -1, paramHttpURLConnection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt1, boolean paramBoolean, int paramInt2, HttpURLConnection paramHttpURLConnection)
/*     */     throws IOException
/*     */   {
/* 307 */     return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString == null ? null : 
/*     */     
/* 309 */       HttpClient.newHttpProxy(paramString, paramInt1, "https"), paramBoolean, paramInt2, paramHttpURLConnection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, Proxy paramProxy, boolean paramBoolean, int paramInt, HttpURLConnection paramHttpURLConnection)
/*     */     throws IOException
/*     */   {
/* 318 */     if (paramProxy == null) {
/* 319 */       paramProxy = Proxy.NO_PROXY;
/*     */     }
/* 321 */     PlatformLogger localPlatformLogger = HttpURLConnection.getHttpLogger();
/* 322 */     if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
/* 323 */       localPlatformLogger.finest("Looking for HttpClient for URL " + paramURL + " and proxy value of " + paramProxy);
/*     */     }
/*     */     
/* 326 */     HttpsClient localHttpsClient = null;
/* 327 */     if (paramBoolean)
/*     */     {
/* 329 */       localHttpsClient = (HttpsClient)kac.get(paramURL, paramSSLSocketFactory);
/* 330 */       if ((localHttpsClient != null) && (paramHttpURLConnection != null) && 
/* 331 */         (paramHttpURLConnection.streaming()) && 
/* 332 */         (paramHttpURLConnection.getRequestMethod() == "POST") && 
/* 333 */         (!localHttpsClient.available())) {
/* 334 */         localHttpsClient = null;
/*     */       }
/*     */       
/* 337 */       if (localHttpsClient != null) {
/* 338 */         if (((localHttpsClient.proxy != null) && (localHttpsClient.proxy.equals(paramProxy))) || ((localHttpsClient.proxy == null) && (paramProxy == Proxy.NO_PROXY)))
/*     */         {
/* 340 */           synchronized (localHttpsClient) {
/* 341 */             localHttpsClient.cachedHttpClient = true;
/* 342 */             assert (localHttpsClient.inCache);
/* 343 */             localHttpsClient.inCache = false;
/* 344 */             if ((paramHttpURLConnection != null) && (localHttpsClient.needsTunneling()))
/* 345 */               paramHttpURLConnection.setTunnelState(HttpURLConnection.TunnelState.TUNNELING);
/* 346 */             if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
/* 347 */               localPlatformLogger.finest("KeepAlive stream retrieved from the cache, " + localHttpsClient);
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */         }
/*     */         else
/*     */         {
/* 355 */           synchronized (localHttpsClient) {
/* 356 */             if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
/* 357 */               localPlatformLogger.finest("Not returning this connection to cache: " + localHttpsClient);
/*     */             }
/* 359 */             localHttpsClient.inCache = false;
/* 360 */             localHttpsClient.closeServer();
/*     */           }
/* 362 */           localHttpsClient = null;
/*     */         }
/*     */       }
/*     */     }
/* 366 */     if (localHttpsClient == null) {
/* 367 */       localHttpsClient = new HttpsClient(paramSSLSocketFactory, paramURL, paramProxy, paramInt);
/*     */     } else {
/* 369 */       ??? = System.getSecurityManager();
/* 370 */       if (??? != null) {
/* 371 */         if ((localHttpsClient.proxy == Proxy.NO_PROXY) || (localHttpsClient.proxy == null)) {
/* 372 */           ((SecurityManager)???).checkConnect(InetAddress.getByName(paramURL.getHost()).getHostAddress(), paramURL.getPort());
/*     */         } else {
/* 374 */           ((SecurityManager)???).checkConnect(paramURL.getHost(), paramURL.getPort());
/*     */         }
/*     */       }
/* 377 */       localHttpsClient.url = paramURL;
/*     */     }
/* 379 */     localHttpsClient.setHostnameVerifier(paramHostnameVerifier);
/*     */     
/* 381 */     return localHttpsClient;
/*     */   }
/*     */   
/*     */   void setHostnameVerifier(HostnameVerifier paramHostnameVerifier)
/*     */   {
/* 386 */     this.hv = paramHostnameVerifier;
/*     */   }
/*     */   
/*     */   void setSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory) {
/* 390 */     this.sslSocketFactory = paramSSLSocketFactory;
/*     */   }
/*     */   
/*     */   SSLSocketFactory getSSLSocketFactory() {
/* 394 */     return this.sslSocketFactory;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Socket createSocket()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 405 */       return this.sslSocketFactory.createSocket();
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (SocketException localSocketException)
/*     */     {
/*     */ 
/*     */ 
/* 413 */       Throwable localThrowable = localSocketException.getCause();
/* 414 */       if ((localThrowable != null) && ((localThrowable instanceof UnsupportedOperationException))) {
/* 415 */         return super.createSocket();
/*     */       }
/* 417 */       throw localSocketException;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean needsTunneling()
/*     */   {
/* 425 */     return (this.proxy != null) && (this.proxy.type() != Type.DIRECT) &&
/* 426 */       (this.proxy.type() != Type.SOCKS);
/*     */   }
/*     */   
/*     */   public void afterConnect() throws IOException, UnknownHostException
/*     */   {
/* 431 */     if (!isCachedConnection()) {
/* 432 */       SSLSocket localSSLSocket = null;
/* 433 */       SSLSocketFactory localSSLSocketFactory = this.sslSocketFactory;
/*     */       try {
/* 435 */         if (!(this.serverSocket instanceof SSLSocket)) {
/* 436 */           localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(this.serverSocket, this.host, this.port, true);
/*     */         }
/*     */         else {
/* 439 */           localSSLSocket = (SSLSocket)this.serverSocket;
/* 440 */           if ((localSSLSocket instanceof SSLSocketImpl)) {
/* 441 */             ((SSLSocketImpl)localSSLSocket).setHost(this.host);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException1)
/*     */       {
/*     */         try
/*     */         {
/* 449 */           localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(this.host, this.port);
/*     */         } catch (IOException localIOException2) {
/* 451 */           throw localIOException1;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 460 */       String[] arrayOfString1 = getProtocols();
/* 461 */       String[] arrayOfString2 = getCipherSuites();
/* 462 */       if (arrayOfString1 != null) {
/* 463 */         localSSLSocket.setEnabledProtocols(arrayOfString1);
/*     */       }
/* 465 */       if (arrayOfString2 != null) {
/* 466 */         localSSLSocket.setEnabledCipherSuites(arrayOfString2);
/*     */       }
/* 468 */       localSSLSocket.addHandshakeCompletedListener(this);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 518 */       int i = 1;
/*     */       
/* 520 */       String str = localSSLSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
/* 521 */       if ((str != null) && (str.length() != 0)) {
/* 522 */         if (str.equalsIgnoreCase("HTTPS"))
/*     */         {
/*     */ 
/*     */ 
/* 526 */           i = 0;
/*     */         }
/*     */       }
/*     */       else {
/* 530 */         int j = 0;
/*     */         
/*     */ 
/*     */         Object localObject;
/*     */         
/* 535 */         if (this.hv != null) {
/* 536 */           localObject = this.hv.getClass().getCanonicalName();
/* 537 */           if ((localObject != null) && 
/* 538 */             (((String)localObject).equalsIgnoreCase("javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier"))) {
/* 539 */             j = 1;
/*     */           }
/*     */           
/*     */         }
/*     */         else
/*     */         {
/* 545 */           j = 1;
/*     */         }
/*     */         
/* 548 */         if (j != 0)
/*     */         {
/*     */ 
/* 551 */           localObject = localSSLSocket.getSSLParameters();
/* 552 */           ((SSLParameters)localObject).setEndpointIdentificationAlgorithm("HTTPS");
/* 553 */           localSSLSocket.setSSLParameters((SSLParameters)localObject);
/*     */           
/* 555 */           i = 0;
/*     */         }
/*     */       }
/*     */       
/* 559 */       localSSLSocket.startHandshake();
/* 560 */       this.session = localSSLSocket.getSession();
/*     */       
/* 562 */       this.serverSocket = localSSLSocket;
/*     */       try
/*     */       {
/* 565 */         this.serverOutput = new PrintStream(new BufferedOutputStream(this.serverSocket.getOutputStream()), false, encoding);
/*     */       }
/*     */       catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 568 */         throw new InternalError(encoding + " encoding not found");
/*     */       }
/*     */       
/*     */ 
/* 572 */       if (i != 0) {
/* 573 */         checkURLSpoofing(this.hv);
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 579 */       this.session = ((SSLSocket)this.serverSocket).getSession();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkURLSpoofing(HostnameVerifier paramHostnameVerifier)
/*     */     throws IOException
/*     */   {
/* 590 */     String str1 = this.url.getHost();
/*     */     
/*     */ 
/* 593 */     if ((str1 != null) && (str1.startsWith("[")) && (str1.endsWith("]"))) {
/* 594 */       str1 = str1.substring(1, str1.length() - 1);
/*     */     }
/*     */     
/* 597 */     Certificate[] arrayOfCertificate = null;
/* 598 */     String str2 = this.session.getCipherSuite();
/*     */     try {
/* 600 */       HostnameChecker localHostnameChecker = HostnameChecker.getInstance((byte)1);
/*     */       
/*     */ 
/*     */ 
/* 604 */       if (str2.startsWith("TLS_KRB5")) {
/* 605 */         if (!HostnameChecker.match(str1, getPeerPrincipal())) {
/* 606 */           throw new SSLPeerUnverifiedException("Hostname checker failed for Kerberos");
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 612 */         arrayOfCertificate = this.session.getPeerCertificates();
/*     */         
/*     */         java.security.cert.X509Certificate localX509Certificate;
/* 615 */         if ((arrayOfCertificate[0] instanceof java.security.cert.X509Certificate))
/*     */         {
/* 617 */           localX509Certificate = (java.security.cert.X509Certificate)arrayOfCertificate[0];
/*     */         } else {
/* 619 */           throw new SSLPeerUnverifiedException("");
/*     */         }
/* 621 */         localHostnameChecker.match(str1, localX509Certificate);
/*     */       }
/*     */       
/*     */ 
/* 625 */       return;
/*     */     }
/*     */     catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException) {}catch (CertificateException localCertificateException) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 638 */     if ((str2 != null) && (str2.indexOf("_anon_") != -1))
/* 639 */       return;
/* 640 */     if ((paramHostnameVerifier != null) && 
/* 641 */       (paramHostnameVerifier.verify(str1, this.session))) {
/* 642 */       return;
/*     */     }
/*     */     
/* 645 */     this.serverSocket.close();
/* 646 */     this.session.invalidate();
/*     */     
/*     */ 
/* 649 */     throw new IOException("HTTPS hostname wrong:  should be <" + this.url.getHost() + ">");
/*     */   }
/*     */   
/*     */   protected void putInKeepAliveCache()
/*     */   {
/* 654 */     if (this.inCache) {
/* 655 */       if (!$assertionsDisabled) throw new AssertionError("Duplicate put to keep alive cache");
/* 656 */       return;
/*     */     }
/* 658 */     this.inCache = true;
/* 659 */     kac.put(this.url, this.sslSocketFactory, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void closeIdleConnection()
/*     */   {
/* 667 */     HttpClient localHttpClient = kac.get(this.url, this.sslSocketFactory);
/* 668 */     if (localHttpClient != null) {
/* 669 */       localHttpClient.closeServer();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   String getCipherSuite()
/*     */   {
/* 677 */     return this.session.getCipherSuite();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Certificate[] getLocalCertificates()
/*     */   {
/* 685 */     return this.session.getLocalCertificates();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Certificate[] getServerCertificates()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/* 696 */     return this.session.getPeerCertificates();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   javax.security.cert.X509Certificate[] getServerCertificateChain()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/* 706 */     return this.session.getPeerCertificateChain();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   Principal getPeerPrincipal()
/*     */     throws SSLPeerUnverifiedException
/*     */   {
/*     */     Object localObject;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 719 */       localObject = this.session.getPeerPrincipal();
/*     */ 
/*     */     }
/*     */     catch (AbstractMethodError localAbstractMethodError)
/*     */     {
/* 724 */       Certificate[] arrayOfCertificate = this.session.getPeerCertificates();
/* 725 */       localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
/*     */     }
/* 727 */     return (Principal)localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   Principal getLocalPrincipal()
/*     */   {
/*     */     Object localObject;
/*     */     
/*     */     try
/*     */     {
/* 738 */       localObject = this.session.getLocalPrincipal();
/*     */     } catch (AbstractMethodError localAbstractMethodError) {
/* 740 */       localObject = null;
/*     */       
/*     */ 
/*     */ 
/* 744 */       Certificate[] arrayOfCertificate = this.session.getLocalCertificates();
/* 745 */       if (arrayOfCertificate != null) {
/* 746 */         localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
/*     */       }
/*     */     }
/* 749 */     return (Principal)localObject;
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
/*     */   public void handshakeCompleted(HandshakeCompletedEvent paramHandshakeCompletedEvent)
/*     */   {
/* 762 */     this.session = paramHandshakeCompletedEvent.getSession();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getProxyHostUsed()
/*     */   {
/* 771 */     if (!needsTunneling()) {
/* 772 */       return null;
/*     */     }
/* 774 */     return super.getProxyHostUsed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getProxyPortUsed()
/*     */   {
/* 784 */     return (this.proxy == null) || (this.proxy.type() == Type.DIRECT) ||
/* 785 */       (this.proxy.type() == Type.SOCKS) ? -1 :
/* 786 */       ((InetSocketAddress)this.proxy.address()).getPort();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\https\HttpsClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
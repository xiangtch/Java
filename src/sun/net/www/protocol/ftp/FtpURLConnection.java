/*     */ package sun.net.www.protocol.ftp;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.ProxySelector;
/*     */ import java.net.SocketPermission;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import java.security.Permission;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import sun.net.ProgressMonitor;
/*     */ import sun.net.ProgressSource;
/*     */ import sun.net.ftp.FtpClient;
/*     */ import sun.net.ftp.FtpLoginException;
/*     */ import sun.net.ftp.FtpProtocolException;
/*     */ import sun.net.www.MessageHeader;
/*     */ import sun.net.www.MeteredStream;
/*     */ import sun.net.www.ParseUtil;
/*     */ import sun.net.www.URLConnection;
/*     */ import sun.net.www.protocol.http.HttpURLConnection;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FtpURLConnection
/*     */   extends URLConnection
/*     */ {
/*  84 */   HttpURLConnection http = null;
/*     */   
/*     */   private Proxy instProxy;
/*  87 */   InputStream is = null;
/*  88 */   OutputStream os = null;
/*     */   
/*  90 */   FtpClient ftp = null;
/*     */   
/*     */   Permission permission;
/*     */   
/*     */   String password;
/*     */   String user;
/*     */   String host;
/*     */   String pathname;
/*     */   String filename;
/*     */   String fullpath;
/*     */   int port;
/*     */   static final int NONE = 0;
/*     */   static final int ASCII = 1;
/*     */   static final int BIN = 2;
/*     */   static final int DIR = 3;
/* 105 */   int type = 0;
/*     */   
/*     */ 
/*     */ 
/* 109 */   private int connectTimeout = -1;
/* 110 */   private int readTimeout = -1;
/*     */   
/*     */ 
/*     */ 
/*     */   protected class FtpInputStream
/*     */     extends FilterInputStream
/*     */   {
/*     */     FtpClient ftp;
/*     */     
/*     */ 
/*     */     FtpInputStream(FtpClient paramFtpClient, InputStream paramInputStream)
/*     */     {
/* 122 */       super();
/* 123 */       this.ftp = paramFtpClient;
/*     */     }
/*     */     
/*     */     public void close() throws IOException
/*     */     {
/* 128 */       super.close();
/* 129 */       if (this.ftp != null) {
/* 130 */         this.ftp.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected class FtpOutputStream
/*     */     extends FilterOutputStream
/*     */   {
/*     */     FtpClient ftp;
/*     */     
/*     */ 
/*     */     FtpOutputStream(FtpClient paramFtpClient, OutputStream paramOutputStream)
/*     */     {
/* 145 */       super();
/* 146 */       this.ftp = paramFtpClient;
/*     */     }
/*     */     
/*     */     public void close() throws IOException
/*     */     {
/* 151 */       super.close();
/* 152 */       if (this.ftp != null) {
/* 153 */         this.ftp.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FtpURLConnection(URL paramURL)
/*     */   {
/* 164 */     this(paramURL, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   FtpURLConnection(URL paramURL, Proxy paramProxy)
/*     */   {
/* 171 */     super(paramURL);
/* 172 */     this.instProxy = paramProxy;
/* 173 */     this.host = paramURL.getHost();
/* 174 */     this.port = paramURL.getPort();
/* 175 */     String str = paramURL.getUserInfo();
/*     */     
/* 177 */     if (str != null) {
/* 178 */       int i = str.indexOf(':');
/* 179 */       if (i == -1) {
/* 180 */         this.user = ParseUtil.decode(str);
/* 181 */         this.password = null;
/*     */       } else {
/* 183 */         this.user = ParseUtil.decode(str.substring(0, i++));
/* 184 */         this.password = ParseUtil.decode(str.substring(i));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void setTimeouts() {
/* 190 */     if (this.ftp != null) {
/* 191 */       if (this.connectTimeout >= 0) {
/* 192 */         this.ftp.setConnectTimeout(this.connectTimeout);
/*     */       }
/* 194 */       if (this.readTimeout >= 0) {
/* 195 */         this.ftp.setReadTimeout(this.readTimeout);
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
/*     */   public synchronized void connect()
/*     */     throws IOException
/*     */   {
/* 209 */     if (this.connected) {
/* 210 */       return;
/*     */     }
/*     */     
/* 213 */     Proxy localProxy = null;
/* 214 */     Object localObject; if (this.instProxy == null)
/*     */     {
/*     */ 
/*     */ 
/* 218 */       localObject = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public ProxySelector run() {
/* 221 */           return ProxySelector.getDefault();
/*     */         }
/*     */       });
/* 224 */       if (localObject != null) {
/* 225 */         URI localURI = ParseUtil.toURI(this.url);
/* 226 */         Iterator localIterator = ((ProxySelector)localObject).select(localURI).iterator();
/* 227 */         while (localIterator.hasNext()) {
/* 228 */           localProxy = (Proxy)localIterator.next();
/* 229 */           if ((localProxy == null) || (localProxy == Proxy.NO_PROXY) || 
/* 230 */             (localProxy.type() == Type.SOCKS)) {
/*     */             break;
/*     */           }
/* 233 */           if ((localProxy.type() != Type.HTTP) ||
/* 234 */             (!(localProxy.address() instanceof InetSocketAddress))) {
/* 235 */             ((ProxySelector)localObject).connectFailed(localURI, localProxy.address(), new IOException("Wrong proxy type"));
/*     */           }
/*     */           else
/*     */           {
/* 239 */             InetSocketAddress localInetSocketAddress = (InetSocketAddress)localProxy.address();
/*     */             try {
/* 241 */               this.http = new HttpURLConnection(this.url, localProxy);
/* 242 */               this.http.setDoInput(getDoInput());
/* 243 */               this.http.setDoOutput(getDoOutput());
/* 244 */               if (this.connectTimeout >= 0) {
/* 245 */                 this.http.setConnectTimeout(this.connectTimeout);
/*     */               }
/* 247 */               if (this.readTimeout >= 0) {
/* 248 */                 this.http.setReadTimeout(this.readTimeout);
/*     */               }
/* 250 */               this.http.connect();
/* 251 */               this.connected = true;
/* 252 */               return;
/*     */             } catch (IOException localIOException2) {
/* 254 */               ((ProxySelector)localObject).connectFailed(localURI, localInetSocketAddress, localIOException2);
/* 255 */               this.http = null;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 260 */     } else { localProxy = this.instProxy;
/* 261 */       if (localProxy.type() == Type.HTTP) {
/* 262 */         this.http = new HttpURLConnection(this.url, this.instProxy);
/* 263 */         this.http.setDoInput(getDoInput());
/* 264 */         this.http.setDoOutput(getDoOutput());
/* 265 */         if (this.connectTimeout >= 0) {
/* 266 */           this.http.setConnectTimeout(this.connectTimeout);
/*     */         }
/* 268 */         if (this.readTimeout >= 0) {
/* 269 */           this.http.setReadTimeout(this.readTimeout);
/*     */         }
/* 271 */         this.http.connect();
/* 272 */         this.connected = true;
/* 273 */         return;
/*     */       }
/*     */     }
/*     */     
/* 277 */     if (this.user == null) {
/* 278 */       this.user = "anonymous";
/* 279 */       localObject = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
/*     */       
/* 281 */       this.password = ((String)AccessController.doPrivileged(new GetPropertyAction("ftp.protocol.user", "Java" + (String)localObject + "@")));
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 286 */       this.ftp = FtpClient.create();
/* 287 */       if (localProxy != null) {
/* 288 */         this.ftp.setProxy(localProxy);
/*     */       }
/* 290 */       setTimeouts();
/* 291 */       if (this.port != -1) {
/* 292 */         this.ftp.connect(new InetSocketAddress(this.host, this.port));
/*     */       } else {
/* 294 */         this.ftp.connect(new InetSocketAddress(this.host, FtpClient.defaultPort()));
/*     */       }
/*     */     }
/*     */     catch (UnknownHostException localUnknownHostException)
/*     */     {
/* 299 */       throw localUnknownHostException;
/*     */     } catch (FtpProtocolException localFtpProtocolException1) {
/* 301 */       if (this.ftp != null) {
/*     */         try {
/* 303 */           this.ftp.close();
/*     */         } catch (IOException localIOException1) {
/* 305 */           localFtpProtocolException1.addSuppressed(localIOException1);
/*     */         }
/*     */       }
/* 308 */       throw new IOException(localFtpProtocolException1);
/*     */     }
/*     */     try {
/* 311 */       this.ftp.login(this.user, this.password == null ? null : this.password.toCharArray());
/*     */     } catch (FtpProtocolException localFtpProtocolException2) {
/* 313 */       this.ftp.close();
/*     */       
/* 315 */       throw new FtpLoginException("Invalid username/password");
/*     */     }
/* 317 */     this.connected = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void decodePath(String paramString)
/*     */   {
/* 325 */     int i = paramString.indexOf(";type=");
/* 326 */     if (i >= 0) {
/* 327 */       String str = paramString.substring(i + 6, paramString.length());
/* 328 */       if ("i".equalsIgnoreCase(str)) {
/* 329 */         this.type = 2;
/*     */       }
/* 331 */       if ("a".equalsIgnoreCase(str)) {
/* 332 */         this.type = 1;
/*     */       }
/* 334 */       if ("d".equalsIgnoreCase(str)) {
/* 335 */         this.type = 3;
/*     */       }
/* 337 */       paramString = paramString.substring(0, i);
/*     */     }
/* 339 */     if ((paramString != null) && (paramString.length() > 1) && 
/* 340 */       (paramString.charAt(0) == '/')) {
/* 341 */       paramString = paramString.substring(1);
/*     */     }
/* 343 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 344 */       paramString = "./";
/*     */     }
/* 346 */     if (!paramString.endsWith("/")) {
/* 347 */       i = paramString.lastIndexOf('/');
/* 348 */       if (i > 0) {
/* 349 */         this.filename = paramString.substring(i + 1, paramString.length());
/* 350 */         this.filename = ParseUtil.decode(this.filename);
/* 351 */         this.pathname = paramString.substring(0, i);
/*     */       } else {
/* 353 */         this.filename = ParseUtil.decode(paramString);
/* 354 */         this.pathname = null;
/*     */       }
/*     */     } else {
/* 357 */       this.pathname = paramString.substring(0, paramString.length() - 1);
/* 358 */       this.filename = null;
/*     */     }
/* 360 */     if (this.pathname != null) {
/* 361 */       this.fullpath = (this.pathname + "/" + (this.filename != null ? this.filename : ""));
/*     */     } else {
/* 363 */       this.fullpath = this.filename;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void cd(String paramString)
/*     */     throws FtpProtocolException, IOException
/*     */   {
/* 374 */     if ((paramString == null) || (paramString.isEmpty())) {
/* 375 */       return;
/*     */     }
/* 377 */     if (paramString.indexOf('/') == -1) {
/* 378 */       this.ftp.changeDirectory(ParseUtil.decode(paramString));
/* 379 */       return;
/*     */     }
/*     */     
/* 382 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "/");
/* 383 */     while (localStringTokenizer.hasMoreTokens()) {
/* 384 */       this.ftp.changeDirectory(ParseUtil.decode(localStringTokenizer.nextToken()));
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
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 399 */     if (!this.connected) {
/* 400 */       connect();
/*     */     }
/*     */     
/* 403 */     if (this.http != null) {
/* 404 */       return this.http.getInputStream();
/*     */     }
/*     */     
/* 407 */     if (this.os != null) {
/* 408 */       throw new IOException("Already opened for output");
/*     */     }
/*     */     
/* 411 */     if (this.is != null) {
/* 412 */       return this.is;
/*     */     }
/*     */     
/* 415 */     MessageHeader localMessageHeader = new MessageHeader();
/*     */     
/* 417 */     int i = 0;
/*     */     try {
/* 419 */       decodePath(this.url.getPath());
/* 420 */       if ((this.filename == null) || (this.type == 3)) {
/* 421 */         this.ftp.setAsciiType();
/* 422 */         cd(this.pathname);
/* 423 */         if (this.filename == null) {
/* 424 */           this.is = new FtpInputStream(this.ftp, this.ftp.list(null));
/*     */         } else {
/* 426 */           this.is = new FtpInputStream(this.ftp, this.ftp.nameList(this.filename));
/*     */         }
/*     */       } else {
/* 429 */         if (this.type == 1) {
/* 430 */           this.ftp.setAsciiType();
/*     */         } else {
/* 432 */           this.ftp.setBinaryType();
/*     */         }
/* 434 */         cd(this.pathname);
/* 435 */         this.is = new FtpInputStream(this.ftp, this.ftp.getFileStream(this.filename));
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 441 */         long l = this.ftp.getLastTransferSize();
/* 442 */         localMessageHeader.add("content-length", Long.toString(l));
/* 443 */         if (l > 0L)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 449 */           boolean bool = ProgressMonitor.getDefault().shouldMeterInput(this.url, "GET");
/* 450 */           ProgressSource localProgressSource = null;
/*     */           
/* 452 */           if (bool) {
/* 453 */             localProgressSource = new ProgressSource(this.url, "GET", l);
/* 454 */             localProgressSource.beginTracking();
/*     */           }
/*     */           
/* 457 */           this.is = new MeteredStream(this.is, localProgressSource, l);
/*     */         }
/*     */       } catch (Exception localException) {
/* 460 */         localException.printStackTrace();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 465 */       if (i != 0) {
/* 466 */         localMessageHeader.add("content-type", "text/plain");
/* 467 */         localMessageHeader.add("access-type", "directory");
/*     */       } else {
/* 469 */         localMessageHeader.add("access-type", "file");
/* 470 */         String str = guessContentTypeFromName(this.fullpath);
/* 471 */         if ((str == null) && (this.is.markSupported())) {
/* 472 */           str = guessContentTypeFromStream(this.is);
/*     */         }
/* 474 */         if (str != null) {
/* 475 */           localMessageHeader.add("content-type", str);
/*     */         }
/*     */       }
/*     */     } catch (FileNotFoundException localFileNotFoundException1) {
/*     */       try {
/* 480 */         cd(this.fullpath);
/*     */         
/*     */ 
/*     */ 
/* 484 */         this.ftp.setAsciiType();
/*     */         
/* 486 */         this.is = new FtpInputStream(this.ftp, this.ftp.list(null));
/* 487 */         localMessageHeader.add("content-type", "text/plain");
/* 488 */         localMessageHeader.add("access-type", "directory");
/*     */       } catch (IOException localIOException1) {
/* 490 */         localFileNotFoundException2 = new FileNotFoundException(this.fullpath);
/* 491 */         if (this.ftp != null) {
/*     */           try {
/* 493 */             this.ftp.close();
/*     */           } catch (IOException localIOException3) {
/* 495 */             localFileNotFoundException2.addSuppressed(localIOException3);
/*     */           }
/*     */         }
/* 498 */         throw localFileNotFoundException2;
/*     */       } catch (FtpProtocolException localFtpProtocolException2) {
/* 500 */         FileNotFoundException localFileNotFoundException2 = new FileNotFoundException(this.fullpath);
/* 501 */         if (this.ftp != null) {
/*     */           try {
/* 503 */             this.ftp.close();
/*     */           } catch (IOException localIOException4) {
/* 505 */             localFileNotFoundException2.addSuppressed(localIOException4);
/*     */           }
/*     */         }
/* 508 */         throw localFileNotFoundException2;
/*     */       }
/*     */     } catch (FtpProtocolException localFtpProtocolException1) {
/* 511 */       if (this.ftp != null) {
/*     */         try {
/* 513 */           this.ftp.close();
/*     */         } catch (IOException localIOException2) {
/* 515 */           localFtpProtocolException1.addSuppressed(localIOException2);
/*     */         }
/*     */       }
/* 518 */       throw new IOException(localFtpProtocolException1);
/*     */     }
/* 520 */     setProperties(localMessageHeader);
/* 521 */     return this.is;
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
/*     */   public OutputStream getOutputStream()
/*     */     throws IOException
/*     */   {
/* 536 */     if (!this.connected) {
/* 537 */       connect();
/*     */     }
/*     */     
/* 540 */     if (this.http != null) {
/* 541 */       OutputStream localOutputStream = this.http.getOutputStream();
/*     */       
/*     */ 
/* 544 */       this.http.getInputStream();
/* 545 */       return localOutputStream;
/*     */     }
/*     */     
/* 548 */     if (this.is != null) {
/* 549 */       throw new IOException("Already opened for input");
/*     */     }
/*     */     
/* 552 */     if (this.os != null) {
/* 553 */       return this.os;
/*     */     }
/*     */     
/* 556 */     decodePath(this.url.getPath());
/* 557 */     if ((this.filename == null) || (this.filename.length() == 0)) {
/* 558 */       throw new IOException("illegal filename for a PUT");
/*     */     }
/*     */     try {
/* 561 */       if (this.pathname != null) {
/* 562 */         cd(this.pathname);
/*     */       }
/* 564 */       if (this.type == 1) {
/* 565 */         this.ftp.setAsciiType();
/*     */       } else {
/* 567 */         this.ftp.setBinaryType();
/*     */       }
/* 569 */       this.os = new FtpOutputStream(this.ftp, this.ftp.putFileStream(this.filename, false));
/*     */     } catch (FtpProtocolException localFtpProtocolException) {
/* 571 */       throw new IOException(localFtpProtocolException);
/*     */     }
/* 573 */     return this.os;
/*     */   }
/*     */   
/*     */   String guessContentTypeFromFilename(String paramString) {
/* 577 */     return guessContentTypeFromName(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Permission getPermission()
/*     */   {
/* 587 */     if (this.permission == null) {
/* 588 */       int i = this.url.getPort();
/* 589 */       i = i < 0 ? FtpClient.defaultPort() : i;
/* 590 */       String str = this.host + ":" + i;
/* 591 */       this.permission = new SocketPermission(str, "connect");
/*     */     }
/* 593 */     return this.permission;
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
/*     */   public void setRequestProperty(String paramString1, String paramString2)
/*     */   {
/* 608 */     super.setRequestProperty(paramString1, paramString2);
/* 609 */     if ("type".equals(paramString1)) {
/* 610 */       if ("i".equalsIgnoreCase(paramString2)) {
/* 611 */         this.type = 2;
/* 612 */       } else if ("a".equalsIgnoreCase(paramString2)) {
/* 613 */         this.type = 1;
/* 614 */       } else if ("d".equalsIgnoreCase(paramString2)) {
/* 615 */         this.type = 3;
/*     */       } else {
/* 617 */         throw new IllegalArgumentException("Value of '" + paramString1 + "' request property was '" + paramString2 + "' when it must be either 'i', 'a' or 'd'");
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
/*     */   public String getRequestProperty(String paramString)
/*     */   {
/* 637 */     String str = super.getRequestProperty(paramString);
/*     */     
/* 639 */     if ((str == null) && 
/* 640 */       ("type".equals(paramString))) {
/* 641 */       str = this.type == 3 ? "d" : this.type == 1 ? "a" : "i";
/*     */     }
/*     */     
/*     */ 
/* 645 */     return str;
/*     */   }
/*     */   
/*     */   public void setConnectTimeout(int paramInt)
/*     */   {
/* 650 */     if (paramInt < 0) {
/* 651 */       throw new IllegalArgumentException("timeouts can't be negative");
/*     */     }
/* 653 */     this.connectTimeout = paramInt;
/*     */   }
/*     */   
/*     */   public int getConnectTimeout()
/*     */   {
/* 658 */     return this.connectTimeout < 0 ? 0 : this.connectTimeout;
/*     */   }
/*     */   
/*     */   public void setReadTimeout(int paramInt)
/*     */   {
/* 663 */     if (paramInt < 0) {
/* 664 */       throw new IllegalArgumentException("timeouts can't be negative");
/*     */     }
/* 666 */     this.readTimeout = paramInt;
/*     */   }
/*     */   
/*     */   public int getReadTimeout()
/*     */   {
/* 671 */     return this.readTimeout < 0 ? 0 : this.readTimeout;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\ftp\FtpURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
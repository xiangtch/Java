/*     */ package sun.awt.image;
/*     */ 
/*     */ import java.io.FilePermission;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.SocketPermission;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.security.Permission;
/*     */ import sun.net.util.URLUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URLImageSource
/*     */   extends InputStreamImageSource
/*     */ {
/*     */   URL url;
/*     */   URLConnection conn;
/*     */   String actualHost;
/*     */   int actualPort;
/*     */   
/*     */   public URLImageSource(URL paramURL)
/*     */   {
/*  43 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  44 */     if (localSecurityManager != null) {
/*     */       try
/*     */       {
/*  47 */         Permission localPermission = URLUtil.getConnectPermission(paramURL);
/*  48 */         if (localPermission != null)
/*     */           try {
/*  50 */             localSecurityManager.checkPermission(localPermission);
/*     */           }
/*     */           catch (SecurityException localSecurityException)
/*     */           {
/*  54 */             if (((localPermission instanceof FilePermission)) && 
/*  55 */               (localPermission.getActions().indexOf("read") != -1)) {
/*  56 */               localSecurityManager.checkRead(localPermission.getName());
/*  57 */             } else { if ((localPermission instanceof SocketPermission))
/*     */               {
/*  59 */                 if (localPermission.getActions().indexOf("connect") != -1) {
/*  60 */                   localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*     */                   break label100; } }
/*  62 */               throw localSecurityException;
/*     */             }
/*     */           }
/*     */       } catch (IOException localIOException) {
/*     */         label100:
/*  67 */         localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*     */       }
/*     */     }
/*  70 */     this.url = paramURL;
/*     */   }
/*     */   
/*     */   public URLImageSource(String paramString) throws MalformedURLException
/*     */   {
/*  75 */     this(new URL(null, paramString));
/*     */   }
/*     */   
/*     */   public URLImageSource(URL paramURL, URLConnection paramURLConnection) {
/*  79 */     this(paramURL);
/*  80 */     this.conn = paramURLConnection;
/*     */   }
/*     */   
/*     */   public URLImageSource(URLConnection paramURLConnection) {
/*  84 */     this(paramURLConnection.getURL(), paramURLConnection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final boolean checkSecurity(Object paramObject, boolean paramBoolean)
/*     */   {
/*  94 */     if (this.actualHost != null) {
/*     */       try {
/*  96 */         SecurityManager localSecurityManager = System.getSecurityManager();
/*  97 */         if (localSecurityManager != null) {
/*  98 */           localSecurityManager.checkConnect(this.actualHost, this.actualPort, paramObject);
/*     */         }
/*     */       } catch (SecurityException localSecurityException) {
/* 101 */         if (!paramBoolean) {
/* 102 */           throw localSecurityException;
/*     */         }
/* 104 */         return false;
/*     */       }
/*     */     }
/* 107 */     return true;
/*     */   }
/*     */   
/*     */   private synchronized URLConnection getConnection() throws IOException {
/*     */     URLConnection localURLConnection;
/* 112 */     if (this.conn != null) {
/* 113 */       localURLConnection = this.conn;
/* 114 */       this.conn = null;
/*     */     } else {
/* 116 */       localURLConnection = this.url.openConnection();
/*     */     }
/* 118 */     return localURLConnection;
/*     */   }
/*     */   
/*     */   protected ImageDecoder getDecoder() {
/* 122 */     InputStream localInputStream = null;
/* 123 */     String str = null;
/* 124 */     URLConnection localURLConnection = null;
/*     */     try {
/* 126 */       localURLConnection = getConnection();
/* 127 */       localInputStream = localURLConnection.getInputStream();
/* 128 */       str = localURLConnection.getContentType();
/* 129 */       URL localURL = localURLConnection.getURL();
/* 130 */       if ((localURL != this.url) && ((!localURL.getHost().equals(this.url.getHost())) || 
/* 131 */         (localURL.getPort() != this.url.getPort())))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 137 */         if ((this.actualHost != null) && ((!this.actualHost.equals(localURL.getHost())) || 
/* 138 */           (this.actualPort != localURL.getPort())))
/*     */         {
/* 140 */           throw new SecurityException("image moved!");
/*     */         }
/* 142 */         this.actualHost = localURL.getHost();
/* 143 */         this.actualPort = localURL.getPort();
/*     */       }
/*     */     } catch (IOException localIOException1) {
/* 146 */       if (localInputStream != null) {
/*     */         try {
/* 148 */           localInputStream.close();
/*     */         }
/*     */         catch (IOException localIOException2) {}
/* 151 */       } else if ((localURLConnection instanceof HttpURLConnection)) {
/* 152 */         ((HttpURLConnection)localURLConnection).disconnect();
/*     */       }
/* 154 */       return null;
/*     */     }
/*     */     
/* 157 */     ImageDecoder localImageDecoder = decoderForType(localInputStream, str);
/* 158 */     if (localImageDecoder == null) {
/* 159 */       localImageDecoder = getDecoder(localInputStream);
/*     */     }
/*     */     
/* 162 */     if (localImageDecoder == null)
/*     */     {
/* 164 */       if (localInputStream != null) {
/*     */         try {
/* 166 */           localInputStream.close();
/*     */         }
/*     */         catch (IOException localIOException3) {}
/* 169 */       } else if ((localURLConnection instanceof HttpURLConnection)) {
/* 170 */         ((HttpURLConnection)localURLConnection).disconnect();
/*     */       }
/*     */     }
/* 173 */     return localImageDecoder;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\image\URLImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
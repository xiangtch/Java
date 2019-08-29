/*     */ package sun.net.www.protocol.jar;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.security.Permission;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JarURLConnection
/*     */   extends java.net.JarURLConnection
/*     */ {
/*     */   private static final boolean debug = false;
/*  54 */   private static final JarFileFactory factory = ;
/*     */   
/*     */ 
/*     */   private URL jarFileURL;
/*     */   
/*     */ 
/*     */   private Permission permission;
/*     */   
/*     */ 
/*     */   private URLConnection jarFileURLConnection;
/*     */   
/*     */ 
/*     */   private String entryName;
/*     */   
/*     */ 
/*     */   private JarEntry jarEntry;
/*     */   
/*     */ 
/*     */   private JarFile jarFile;
/*     */   
/*     */ 
/*     */   private String contentType;
/*     */   
/*     */ 
/*     */   public JarURLConnection(URL paramURL, Handler paramHandler)
/*     */     throws MalformedURLException, IOException
/*     */   {
/*  81 */     super(paramURL);
/*     */     
/*  83 */     this.jarFileURL = getJarFileURL();
/*  84 */     this.jarFileURLConnection = this.jarFileURL.openConnection();
/*  85 */     this.entryName = getEntryName();
/*     */   }
/*     */   
/*     */   public JarFile getJarFile() throws IOException {
/*  89 */     connect();
/*  90 */     return this.jarFile;
/*     */   }
/*     */   
/*     */   public JarEntry getJarEntry() throws IOException {
/*  94 */     connect();
/*  95 */     return this.jarEntry;
/*     */   }
/*     */   
/*     */   public Permission getPermission() throws IOException {
/*  99 */     return this.jarFileURLConnection.getPermission();
/*     */   }
/*     */   
/*     */   class JarURLInputStream extends FilterInputStream {
/*     */     JarURLInputStream(InputStream paramInputStream) {
/* 104 */       super();
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: invokespecial 36	java/io/FilterInputStream:close	()V
/*     */       //   4: aload_0
/*     */       //   5: getfield 35	sun/net/www/protocol/jar/JarURLConnection$JarURLInputStream:this$0	Lsun/net/www/protocol/jar/JarURLConnection;
/*     */       //   8: invokevirtual 39	sun/net/www/protocol/jar/JarURLConnection:getUseCaches	()Z
/*     */       //   11: ifne +39 -> 50
/*     */       //   14: aload_0
/*     */       //   15: getfield 35	sun/net/www/protocol/jar/JarURLConnection$JarURLInputStream:this$0	Lsun/net/www/protocol/jar/JarURLConnection;
/*     */       //   18: invokestatic 40	sun/net/www/protocol/jar/JarURLConnection:access$000	(Lsun/net/www/protocol/jar/JarURLConnection;)Ljava/util/jar/JarFile;
/*     */       //   21: invokevirtual 38	java/util/jar/JarFile:close	()V
/*     */       //   24: goto +26 -> 50
/*     */       //   27: astore_1
/*     */       //   28: aload_0
/*     */       //   29: getfield 35	sun/net/www/protocol/jar/JarURLConnection$JarURLInputStream:this$0	Lsun/net/www/protocol/jar/JarURLConnection;
/*     */       //   32: invokevirtual 39	sun/net/www/protocol/jar/JarURLConnection:getUseCaches	()Z
/*     */       //   35: ifne +13 -> 48
/*     */       //   38: aload_0
/*     */       //   39: getfield 35	sun/net/www/protocol/jar/JarURLConnection$JarURLInputStream:this$0	Lsun/net/www/protocol/jar/JarURLConnection;
/*     */       //   42: invokestatic 40	sun/net/www/protocol/jar/JarURLConnection:access$000	(Lsun/net/www/protocol/jar/JarURLConnection;)Ljava/util/jar/JarFile;
/*     */       //   45: invokevirtual 38	java/util/jar/JarFile:close	()V
/*     */       //   48: aload_1
/*     */       //   49: athrow
/*     */       //   50: return
/*     */       // Line number table:
/*     */       //   Java source line #108	-> byte code offset #0
/*     */       //   Java source line #110	-> byte code offset #4
/*     */       //   Java source line #111	-> byte code offset #14
/*     */       //   Java source line #110	-> byte code offset #27
/*     */       //   Java source line #111	-> byte code offset #38
/*     */       //   Java source line #113	-> byte code offset #48
/*     */       //   Java source line #114	-> byte code offset #50
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	51	0	this	JarURLInputStream
/*     */       //   27	22	1	localObject	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   0	4	27	finally
/*     */     }
/*     */   }
/*     */   
/*     */   public void connect()
/*     */     throws IOException
/*     */   {
/* 120 */     if (!this.connected)
/*     */     {
/* 122 */       this.jarFile = factory.get(getJarFileURL(), getUseCaches());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 127 */       if (getUseCaches()) {
/* 128 */         boolean bool = this.jarFileURLConnection.getUseCaches();
/* 129 */         this.jarFileURLConnection = factory.getConnection(this.jarFile);
/* 130 */         this.jarFileURLConnection.setUseCaches(bool);
/*     */       }
/*     */       
/* 133 */       if (this.entryName != null) {
/* 134 */         this.jarEntry = ((JarEntry)this.jarFile.getEntry(this.entryName));
/* 135 */         if (this.jarEntry == null) {
/*     */           try {
/* 137 */             if (!getUseCaches()) {
/* 138 */               this.jarFile.close();
/*     */             }
/*     */           }
/*     */           catch (Exception localException) {}
/*     */           
/*     */ 
/* 144 */           throw new FileNotFoundException("JAR entry " + this.entryName + " not found in " + this.jarFile.getName());
/*     */         }
/*     */       }
/* 147 */       this.connected = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public InputStream getInputStream() throws IOException {
/* 152 */     connect();
/*     */     
/* 154 */     JarURLInputStream localJarURLInputStream = null;
/*     */     
/* 156 */     if (this.entryName == null) {
/* 157 */       throw new IOException("no entry name specified");
/*     */     }
/* 159 */     if (this.jarEntry == null)
/*     */     {
/*     */ 
/* 162 */       throw new FileNotFoundException("JAR entry " + this.entryName + " not found in " + this.jarFile.getName());
/*     */     }
/* 164 */     localJarURLInputStream = new JarURLInputStream(this.jarFile.getInputStream(this.jarEntry));
/*     */     
/* 166 */     return localJarURLInputStream;
/*     */   }
/*     */   
/*     */   public int getContentLength() {
/* 170 */     long l = getContentLengthLong();
/* 171 */     if (l > 2147483647L)
/* 172 */       return -1;
/* 173 */     return (int)l;
/*     */   }
/*     */   
/*     */   public long getContentLengthLong() {
/* 177 */     long l = -1L;
/*     */     try {
/* 179 */       connect();
/* 180 */       if (this.jarEntry == null)
/*     */       {
/* 182 */         l = this.jarFileURLConnection.getContentLengthLong();
/*     */       }
/*     */       else {
/* 185 */         l = getJarEntry().getSize();
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException) {}
/* 189 */     return l;
/*     */   }
/*     */   
/*     */   public Object getContent() throws IOException {
/* 193 */     Object localObject = null;
/*     */     
/* 195 */     connect();
/* 196 */     if (this.entryName == null) {
/* 197 */       localObject = this.jarFile;
/*     */     } else {
/* 199 */       localObject = super.getContent();
/*     */     }
/* 201 */     return localObject;
/*     */   }
/*     */   
/*     */   public String getContentType() {
/* 205 */     if (this.contentType == null) {
/* 206 */       if (this.entryName == null) {
/* 207 */         this.contentType = "x-java/jar";
/*     */       } else {
/*     */         try {
/* 210 */           connect();
/* 211 */           InputStream localInputStream = this.jarFile.getInputStream(this.jarEntry);
/* 212 */           this.contentType = guessContentTypeFromStream(new BufferedInputStream(localInputStream));
/*     */           
/* 214 */           localInputStream.close();
/*     */         }
/*     */         catch (IOException localIOException) {}
/*     */       }
/*     */       
/* 219 */       if (this.contentType == null) {
/* 220 */         this.contentType = guessContentTypeFromName(this.entryName);
/*     */       }
/* 222 */       if (this.contentType == null) {
/* 223 */         this.contentType = "content/unknown";
/*     */       }
/*     */     }
/* 226 */     return this.contentType;
/*     */   }
/*     */   
/*     */   public String getHeaderField(String paramString) {
/* 230 */     return this.jarFileURLConnection.getHeaderField(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRequestProperty(String paramString1, String paramString2)
/*     */   {
/* 241 */     this.jarFileURLConnection.setRequestProperty(paramString1, paramString2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getRequestProperty(String paramString)
/*     */   {
/* 252 */     return this.jarFileURLConnection.getRequestProperty(paramString);
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
/*     */   public void addRequestProperty(String paramString1, String paramString2)
/*     */   {
/* 265 */     this.jarFileURLConnection.addRequestProperty(paramString1, paramString2);
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
/*     */   public Map<String, List<String>> getRequestProperties()
/*     */   {
/* 279 */     return this.jarFileURLConnection.getRequestProperties();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAllowUserInteraction(boolean paramBoolean)
/*     */   {
/* 290 */     this.jarFileURLConnection.setAllowUserInteraction(paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getAllowUserInteraction()
/*     */   {
/* 302 */     return this.jarFileURLConnection.getAllowUserInteraction();
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
/*     */   public void setUseCaches(boolean paramBoolean)
/*     */   {
/* 324 */     this.jarFileURLConnection.setUseCaches(paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getUseCaches()
/*     */   {
/* 336 */     return this.jarFileURLConnection.getUseCaches();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIfModifiedSince(long paramLong)
/*     */   {
/* 347 */     this.jarFileURLConnection.setIfModifiedSince(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDefaultUseCaches(boolean paramBoolean)
/*     */   {
/* 358 */     this.jarFileURLConnection.setDefaultUseCaches(paramBoolean);
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
/*     */   public boolean getDefaultUseCaches()
/*     */   {
/* 374 */     return this.jarFileURLConnection.getDefaultUseCaches();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\jar\JarURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
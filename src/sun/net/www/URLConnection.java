/*     */ package sun.net.www;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class URLConnection
/*     */   extends java.net.URLConnection
/*     */ {
/*     */   private String contentType;
/*  42 */   private int contentLength = -1;
/*     */   
/*     */ 
/*     */ 
/*     */   protected MessageHeader properties;
/*     */   
/*     */ 
/*     */ 
/*     */   public URLConnection(URL paramURL)
/*     */   {
/*  52 */     super(paramURL);
/*  53 */     this.properties = new MessageHeader();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MessageHeader getProperties()
/*     */   {
/*  60 */     return this.properties;
/*     */   }
/*     */   
/*     */   public void setProperties(MessageHeader paramMessageHeader)
/*     */   {
/*  65 */     this.properties = paramMessageHeader;
/*     */   }
/*     */   
/*     */   public void setRequestProperty(String paramString1, String paramString2) {
/*  69 */     if (this.connected)
/*  70 */       throw new IllegalAccessError("Already connected");
/*  71 */     if (paramString1 == null)
/*  72 */       throw new NullPointerException("key cannot be null");
/*  73 */     this.properties.set(paramString1, paramString2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRequestProperty(String paramString1, String paramString2)
/*     */   {
/*  82 */     if (this.connected)
/*  83 */       throw new IllegalStateException("Already connected");
/*  84 */     if (paramString1 == null)
/*  85 */       throw new NullPointerException("key is null");
/*     */   }
/*     */   
/*     */   public String getRequestProperty(String paramString) {
/*  89 */     if (this.connected)
/*  90 */       throw new IllegalStateException("Already connected");
/*  91 */     return null;
/*     */   }
/*     */   
/*     */   public Map<String, List<String>> getRequestProperties() {
/*  95 */     if (this.connected)
/*  96 */       throw new IllegalStateException("Already connected");
/*  97 */     return Collections.emptyMap();
/*     */   }
/*     */   
/*     */   public String getHeaderField(String paramString) {
/*     */     try {
/* 102 */       getInputStream();
/*     */     } catch (Exception localException) {
/* 104 */       return null;
/*     */     }
/* 106 */     return this.properties == null ? null : this.properties.findValue(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHeaderFieldKey(int paramInt)
/*     */   {
/*     */     try
/*     */     {
/* 116 */       getInputStream();
/*     */     } catch (Exception localException) {
/* 118 */       return null;
/*     */     }
/* 120 */     MessageHeader localMessageHeader = this.properties;
/* 121 */     return localMessageHeader == null ? null : localMessageHeader.getKey(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getHeaderField(int paramInt)
/*     */   {
/*     */     try
/*     */     {
/* 131 */       getInputStream();
/*     */     } catch (Exception localException) {
/* 133 */       return null;
/*     */     }
/* 135 */     MessageHeader localMessageHeader = this.properties;
/* 136 */     return localMessageHeader == null ? null : localMessageHeader.getValue(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getContentType()
/*     */   {
/* 143 */     if (this.contentType == null)
/* 144 */       this.contentType = getHeaderField("content-type");
/* 145 */     if (this.contentType == null) {
/* 146 */       String str1 = null;
/*     */       try {
/* 148 */         str1 = guessContentTypeFromStream(getInputStream());
/*     */       }
/*     */       catch (IOException localIOException) {}
/* 151 */       String str2 = this.properties.findValue("content-encoding");
/* 152 */       if (str1 == null) {
/* 153 */         str1 = this.properties.findValue("content-type");
/*     */         
/* 155 */         if (str1 == null) {
/* 156 */           if (this.url.getFile().endsWith("/")) {
/* 157 */             str1 = "text/html";
/*     */           } else {
/* 159 */             str1 = guessContentTypeFromName(this.url.getFile());
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */       if ((str1 == null) || ((str2 != null) && 
/* 172 */         (!str2.equalsIgnoreCase("7bit")) && 
/* 173 */         (!str2.equalsIgnoreCase("8bit")) && 
/* 174 */         (!str2.equalsIgnoreCase("binary"))))
/* 175 */         str1 = "content/unknown";
/* 176 */       setContentType(str1);
/*     */     }
/* 178 */     return this.contentType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setContentType(String paramString)
/*     */   {
/* 189 */     this.contentType = paramString;
/* 190 */     this.properties.set("content-type", paramString);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getContentLength()
/*     */   {
/*     */     try
/*     */     {
/* 198 */       getInputStream();
/*     */     } catch (Exception localException1) {
/* 200 */       return -1;
/*     */     }
/* 202 */     int i = this.contentLength;
/* 203 */     if (i < 0) {
/*     */       try {
/* 205 */         i = Integer.parseInt(this.properties.findValue("content-length"));
/* 206 */         setContentLength(i);
/*     */       }
/*     */       catch (Exception localException2) {}
/*     */     }
/* 210 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setContentLength(int paramInt)
/*     */   {
/* 217 */     this.contentLength = paramInt;
/* 218 */     this.properties.set("content-length", String.valueOf(paramInt));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canCache()
/*     */   {
/* 225 */     return this.url.getFile().indexOf('?') < 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 234 */     this.url = null;
/*     */   }
/*     */   
/* 237 */   private static HashMap<String, Void> proxiedHosts = new HashMap();
/*     */   
/*     */   public static synchronized void setProxiedHost(String paramString) {
/* 240 */     proxiedHosts.put(paramString.toLowerCase(), null);
/*     */   }
/*     */   
/*     */   public static synchronized boolean isProxiedHost(String paramString) {
/* 244 */     return proxiedHosts.containsKey(paramString.toLowerCase());
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\URLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
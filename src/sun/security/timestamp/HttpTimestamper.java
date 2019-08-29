/*     */ package sun.security.timestamp;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import sun.misc.IOUtils;
/*     */ import sun.security.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpTimestamper
/*     */   implements Timestamper
/*     */ {
/*     */   private static final int CONNECT_TIMEOUT = 15000;
/*     */   private static final String TS_QUERY_MIME_TYPE = "application/timestamp-query";
/*     */   private static final String TS_REPLY_MIME_TYPE = "application/timestamp-reply";
/*  61 */   private static final Debug debug = Debug.getInstance("ts");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  66 */   private URI tsaURI = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpTimestamper(URI paramURI)
/*     */   {
/*  75 */     if ((!paramURI.getScheme().equalsIgnoreCase("http")) && 
/*  76 */       (!paramURI.getScheme().equalsIgnoreCase("https"))) {
/*  77 */       throw new IllegalArgumentException("TSA must be an HTTP or HTTPS URI");
/*     */     }
/*     */     
/*  80 */     this.tsaURI = paramURI;
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
/*     */   public TSResponse generateTimestamp(TSRequest paramTSRequest)
/*     */     throws IOException
/*     */   {
/*  94 */     HttpURLConnection localHttpURLConnection = (HttpURLConnection)this.tsaURI.toURL().openConnection();
/*  95 */     localHttpURLConnection.setDoOutput(true);
/*  96 */     localHttpURLConnection.setUseCaches(false);
/*  97 */     localHttpURLConnection.setRequestProperty("Content-Type", "application/timestamp-query");
/*  98 */     localHttpURLConnection.setRequestMethod("POST");
/*     */     
/* 100 */     localHttpURLConnection.setConnectTimeout(15000);
/*     */     
/* 102 */     if (debug != null)
/*     */     {
/* 104 */       localObject1 = localHttpURLConnection.getRequestProperties().entrySet();
/* 105 */       debug.println(localHttpURLConnection.getRequestMethod() + " " + this.tsaURI + " HTTP/1.1");
/*     */       
/* 107 */       for (localObject2 = ((Set)localObject1).iterator(); ((Iterator)localObject2).hasNext();) { localObject3 = (Entry)((Iterator)localObject2).next();
/* 108 */         debug.println("  " + localObject3);
/*     */       }
/* 110 */       debug.println();
/*     */     }
/* 112 */     localHttpURLConnection.connect();
/*     */     
/*     */ 
/* 115 */     Object localObject1 = null;
/*     */     try {
/* 117 */       localObject1 = new DataOutputStream(localHttpURLConnection.getOutputStream());
/* 118 */       localObject2 = paramTSRequest.encode();
/* 119 */       ((DataOutputStream)localObject1).write((byte[])localObject2, 0, localObject2.length);
/* 120 */       ((DataOutputStream)localObject1).flush();
/* 121 */       if (debug != null) {
/* 122 */         debug.println("sent timestamp query (length=" + localObject2.length + ")");
/*     */       }
/*     */     }
/*     */     finally {
/* 126 */       if (localObject1 != null) {
/* 127 */         ((DataOutputStream)localObject1).close();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 132 */     Object localObject2 = null;
/* 133 */     Object localObject3 = null;
/*     */     try {
/* 135 */       localObject2 = new BufferedInputStream(localHttpURLConnection.getInputStream());
/* 136 */       if (debug != null) {
/* 137 */         String str1 = localHttpURLConnection.getHeaderField(0);
/* 138 */         debug.println(str1);
/* 139 */         int j = 1;
/* 140 */         while ((str1 = localHttpURLConnection.getHeaderField(j)) != null) {
/* 141 */           String str2 = localHttpURLConnection.getHeaderFieldKey(j);
/* 142 */           debug.println("  " + (str2 == null ? "" : new StringBuilder().append(str2).append(": ").toString()) + str1);
/*     */           
/* 144 */           j++;
/*     */         }
/* 146 */         debug.println();
/*     */       }
/* 148 */       verifyMimeType(localHttpURLConnection.getContentType());
/*     */       
/* 150 */       int i = localHttpURLConnection.getContentLength();
/* 151 */       localObject3 = IOUtils.readFully((InputStream)localObject2, i, false);
/*     */       
/* 153 */       if (debug != null) {
/* 154 */         debug.println("received timestamp response (length=" + localObject3.length + ")");
/*     */       }
/*     */     }
/*     */     finally {
/* 158 */       if (localObject2 != null) {
/* 159 */         ((BufferedInputStream)localObject2).close();
/*     */       }
/*     */     }
/* 162 */     return new TSResponse((byte[])localObject3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void verifyMimeType(String paramString)
/*     */     throws IOException
/*     */   {
/* 172 */     if (!"application/timestamp-reply".equalsIgnoreCase(paramString)) {
/* 173 */       throw new IOException("MIME Content-Type is not application/timestamp-reply");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\timestamp\HttpTimestamper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
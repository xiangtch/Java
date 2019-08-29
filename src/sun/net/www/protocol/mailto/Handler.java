/*     */ package sun.net.www.protocol.mailto;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLStreamHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Handler
/*     */   extends URLStreamHandler
/*     */ {
/*     */   public synchronized URLConnection openConnection(URL paramURL)
/*     */   {
/* 103 */     return new MailToURLConnection(paramURL);
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
/*     */   public void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2)
/*     */   {
/* 118 */     String str1 = paramURL.getProtocol();
/* 119 */     String str2 = "";
/* 120 */     int i = paramURL.getPort();
/* 121 */     String str3 = "";
/*     */     
/* 123 */     if (paramInt1 < paramInt2) {
/* 124 */       str3 = paramString.substring(paramInt1, paramInt2);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 129 */     int j = 0;
/* 130 */     if ((str3 == null) || (str3.equals(""))) {
/* 131 */       j = 1;
/*     */     } else {
/* 133 */       int k = 1;
/* 134 */       for (int m = 0; m < str3.length(); m++)
/* 135 */         if (!Character.isWhitespace(str3.charAt(m)))
/* 136 */           k = 0;
/* 137 */       if (k != 0)
/* 138 */         j = 1;
/*     */     }
/* 140 */     if (j != 0)
/* 141 */       throw new RuntimeException("No email address");
/* 142 */     setURLHandler(paramURL, str1, str2, i, str3, null);
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
/*     */   private void setURLHandler(URL paramURL, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4)
/*     */   {
/* 156 */     setURL(paramURL, paramString1, paramString2, paramInt, paramString3, null);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\mailto\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
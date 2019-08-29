/*     */ package sun.net.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLPermission;
/*     */ import java.security.Permission;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URLUtil
/*     */ {
/*     */   public static String urlNoFragString(URL paramURL)
/*     */   {
/*  48 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     
/*  50 */     String str1 = paramURL.getProtocol();
/*  51 */     if (str1 != null)
/*     */     {
/*  53 */       str1 = str1.toLowerCase();
/*  54 */       localStringBuilder.append(str1);
/*  55 */       localStringBuilder.append("://");
/*     */     }
/*     */     
/*  58 */     String str2 = paramURL.getHost();
/*  59 */     if (str2 != null)
/*     */     {
/*  61 */       str2 = str2.toLowerCase();
/*  62 */       localStringBuilder.append(str2);
/*     */       
/*  64 */       int i = paramURL.getPort();
/*  65 */       if (i == -1)
/*     */       {
/*     */ 
/*  68 */         i = paramURL.getDefaultPort();
/*     */       }
/*  70 */       if (i != -1) {
/*  71 */         localStringBuilder.append(":").append(i);
/*     */       }
/*     */     }
/*     */     
/*  75 */     String str3 = paramURL.getFile();
/*  76 */     if (str3 != null) {
/*  77 */       localStringBuilder.append(str3);
/*     */     }
/*     */     
/*  80 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */   public static Permission getConnectPermission(URL paramURL) throws IOException {
/*  84 */     String str1 = paramURL.toString().toLowerCase();
/*  85 */     if ((str1.startsWith("http:")) || (str1.startsWith("https:")))
/*  86 */       return getURLConnectPermission(paramURL);
/*  87 */     if ((str1.startsWith("jar:http:")) || (str1.startsWith("jar:https:"))) {
/*  88 */       String str2 = paramURL.toString();
/*  89 */       int i = str2.indexOf("!/");
/*  90 */       str2 = str2.substring(4, i > -1 ? i : str2.length());
/*  91 */       URL localURL = new URL(str2);
/*  92 */       return getURLConnectPermission(localURL);
/*     */     }
/*     */     
/*  95 */     return paramURL.openConnection().getPermission();
/*     */   }
/*     */   
/*     */   private static Permission getURLConnectPermission(URL paramURL)
/*     */   {
/* 100 */     String str = paramURL.getProtocol() + "://" + paramURL.getAuthority() + paramURL.getPath();
/* 101 */     return new URLPermission(str);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\util\URLUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
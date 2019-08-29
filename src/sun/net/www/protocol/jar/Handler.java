/*     */ package sun.net.www.protocol.jar;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLStreamHandler;
/*     */ import sun.net.www.ParseUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */   private static final String separator = "!/";
/*     */   
/*     */   protected URLConnection openConnection(URL paramURL)
/*     */     throws IOException
/*     */   {
/*  41 */     return new JarURLConnection(paramURL, this);
/*     */   }
/*     */   
/*     */   private static int indexOfBangSlash(String paramString) {
/*  45 */     int i = paramString.length();
/*  46 */     while ((i = paramString.lastIndexOf('!', i)) != -1) {
/*  47 */       if ((i != paramString.length() - 1) && 
/*  48 */         (paramString.charAt(i + 1) == '/')) {
/*  49 */         return i + 1;
/*     */       }
/*  51 */       i--;
/*     */     }
/*     */     
/*  54 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean sameFile(URL paramURL1, URL paramURL2)
/*     */   {
/*  62 */     if ((!paramURL1.getProtocol().equals("jar")) || (!paramURL2.getProtocol().equals("jar"))) {
/*  63 */       return false;
/*     */     }
/*  65 */     String str1 = paramURL1.getFile();
/*  66 */     String str2 = paramURL2.getFile();
/*  67 */     int i = str1.indexOf("!/");
/*  68 */     int j = str2.indexOf("!/");
/*     */     
/*  70 */     if ((i == -1) || (j == -1)) {
/*  71 */       return super.sameFile(paramURL1, paramURL2);
/*     */     }
/*     */     
/*  74 */     String str3 = str1.substring(i + 2);
/*  75 */     String str4 = str2.substring(j + 2);
/*     */     
/*  77 */     if (!str3.equals(str4)) {
/*  78 */       return false;
/*     */     }
/*  80 */     URL localURL1 = null;URL localURL2 = null;
/*     */     try {
/*  82 */       localURL1 = new URL(str1.substring(0, i));
/*  83 */       localURL2 = new URL(str2.substring(0, j));
/*     */     } catch (MalformedURLException localMalformedURLException) {
/*  85 */       return super.sameFile(paramURL1, paramURL2);
/*     */     }
/*     */     
/*  88 */     if (!super.sameFile(localURL1, localURL2)) {
/*  89 */       return false;
/*     */     }
/*     */     
/*  92 */     return true;
/*     */   }
/*     */   
/*     */   protected int hashCode(URL paramURL)
/*     */   {
/*  97 */     int i = 0;
/*     */     
/*  99 */     String str1 = paramURL.getProtocol();
/* 100 */     if (str1 != null) {
/* 101 */       i += str1.hashCode();
/*     */     }
/* 103 */     String str2 = paramURL.getFile();
/* 104 */     int j = str2.indexOf("!/");
/*     */     
/* 106 */     if (j == -1) {
/* 107 */       return i + str2.hashCode();
/*     */     }
/* 109 */     URL localURL = null;
/* 110 */     String str3 = str2.substring(0, j);
/*     */     try {
/* 112 */       localURL = new URL(str3);
/* 113 */       i += localURL.hashCode();
/*     */     } catch (MalformedURLException localMalformedURLException) {
/* 115 */       i += str3.hashCode();
/*     */     }
/*     */     
/* 118 */     String str4 = str2.substring(j + 2);
/* 119 */     i += str4.hashCode();
/*     */     
/* 121 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2)
/*     */   {
/* 129 */     String str1 = null;
/* 130 */     String str2 = null;
/*     */     
/* 132 */     int i = paramString.indexOf('#', paramInt2);
/* 133 */     int j = i == paramInt1 ? 1 : 0;
/* 134 */     if (i > -1) {
/* 135 */       str2 = paramString.substring(i + 1, paramString.length());
/* 136 */       if (j != 0) {
/* 137 */         str1 = paramURL.getFile();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 144 */     boolean bool = false;
/* 145 */     if (paramString.length() >= 4) {
/* 146 */       bool = paramString.substring(0, 4).equalsIgnoreCase("jar:");
/*     */     }
/* 148 */     paramString = paramString.substring(paramInt1, paramInt2);
/*     */     
/* 150 */     if (bool) {
/* 151 */       str1 = parseAbsoluteSpec(paramString);
/* 152 */     } else if (j == 0) {
/* 153 */       str1 = parseContextSpec(paramURL, paramString);
/*     */       
/*     */ 
/* 156 */       int k = indexOfBangSlash(str1);
/* 157 */       String str3 = str1.substring(0, k);
/* 158 */       String str4 = str1.substring(k);
/* 159 */       ParseUtil localParseUtil = new ParseUtil();
/* 160 */       str4 = localParseUtil.canonizeString(str4);
/* 161 */       str1 = str3 + str4;
/*     */     }
/* 163 */     setURL(paramURL, "jar", "", -1, str1, str2);
/*     */   }
/*     */   
/*     */   private String parseAbsoluteSpec(String paramString) {
/* 167 */     URL localURL = null;
/* 168 */     int i = -1;
/*     */     
/* 170 */     if ((i = indexOfBangSlash(paramString)) == -1) {
/* 171 */       throw new NullPointerException("no !/ in spec");
/*     */     }
/*     */     try
/*     */     {
/* 175 */       String str = paramString.substring(0, i - 1);
/* 176 */       localURL = new URL(str);
/*     */     } catch (MalformedURLException localMalformedURLException) {
/* 178 */       throw new NullPointerException("invalid url: " + paramString + " (" + localMalformedURLException + ")");
/*     */     }
/*     */     
/* 181 */     return paramString;
/*     */   }
/*     */   
/*     */   private String parseContextSpec(URL paramURL, String paramString) {
/* 185 */     String str = paramURL.getFile();
/*     */     int i;
/* 187 */     if (paramString.startsWith("/")) {
/* 188 */       i = indexOfBangSlash(str);
/* 189 */       if (i == -1) {
/* 190 */         throw new NullPointerException("malformed context url:" + paramURL + ": no !/");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 195 */       str = str.substring(0, i);
/*     */     }
/* 197 */     if ((!str.endsWith("/")) && (!paramString.startsWith("/")))
/*     */     {
/* 199 */       i = str.lastIndexOf('/');
/* 200 */       if (i == -1) {
/* 201 */         throw new NullPointerException("malformed context url:" + paramURL);
/*     */       }
/*     */       
/*     */ 
/* 205 */       str = str.substring(0, i + 1);
/*     */     }
/* 207 */     return str + paramString;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\jar\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
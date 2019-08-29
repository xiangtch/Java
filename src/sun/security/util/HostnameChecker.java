/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.Principal;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.net.ssl.SNIHostName;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.net.util.IPAddressUtil;
/*     */ import sun.security.ssl.Krb5Helper;
/*     */ import sun.security.x509.X500Name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HostnameChecker
/*     */ {
/*     */   public static final byte TYPE_TLS = 1;
/*  53 */   private static final HostnameChecker INSTANCE_TLS = new HostnameChecker((byte)1);
/*     */   
/*     */ 
/*     */   public static final byte TYPE_LDAP = 2;
/*     */   
/*  58 */   private static final HostnameChecker INSTANCE_LDAP = new HostnameChecker((byte)2);
/*     */   
/*     */   private static final int ALTNAME_DNS = 2;
/*     */   
/*     */   private static final int ALTNAME_IP = 7;
/*     */   
/*     */   private final byte checkType;
/*     */   
/*     */ 
/*     */   private HostnameChecker(byte paramByte)
/*     */   {
/*  69 */     this.checkType = paramByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static HostnameChecker getInstance(byte paramByte)
/*     */   {
/*  77 */     if (paramByte == 1)
/*  78 */       return INSTANCE_TLS;
/*  79 */     if (paramByte == 2) {
/*  80 */       return INSTANCE_LDAP;
/*     */     }
/*  82 */     throw new IllegalArgumentException("Unknown check type: " + paramByte);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void match(String paramString, X509Certificate paramX509Certificate)
/*     */     throws CertificateException
/*     */   {
/*  93 */     if (isIpAddress(paramString)) {
/*  94 */       matchIP(paramString, paramX509Certificate);
/*     */     } else {
/*  96 */       matchDNS(paramString, paramX509Certificate);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean match(String paramString, Principal paramPrincipal)
/*     */   {
/* 104 */     String str = getServerName(paramPrincipal);
/* 105 */     return paramString.equalsIgnoreCase(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getServerName(Principal paramPrincipal)
/*     */   {
/* 112 */     return Krb5Helper.getPrincipalHostName(paramPrincipal);
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
/*     */   private static boolean isIpAddress(String paramString)
/*     */   {
/* 125 */     if ((IPAddressUtil.isIPv4LiteralAddress(paramString)) || 
/* 126 */       (IPAddressUtil.isIPv6LiteralAddress(paramString))) {
/* 127 */       return true;
/*     */     }
/* 129 */     return false;
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
/*     */   private static void matchIP(String paramString, X509Certificate paramX509Certificate)
/*     */     throws CertificateException
/*     */   {
/* 143 */     Collection localCollection = paramX509Certificate.getSubjectAlternativeNames();
/* 144 */     if (localCollection == null) {
/* 145 */       throw new CertificateException("No subject alternative names present");
/*     */     }
/*     */     
/* 148 */     for (List localList : localCollection)
/*     */     {
/* 150 */       if (((Integer)localList.get(0)).intValue() == 7) {
/* 151 */         String str = (String)localList.get(1);
/* 152 */         if (paramString.equalsIgnoreCase(str)) {
/* 153 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */         try
/*     */         {
/* 159 */           if (InetAddress.getByName(paramString).equals(
/* 160 */             InetAddress.getByName(str))) {
/* 161 */             return;
/*     */           }
/*     */         }
/*     */         catch (UnknownHostException localUnknownHostException) {}catch (SecurityException localSecurityException) {}
/*     */       }
/*     */     }
/*     */     
/* 168 */     throw new CertificateException("No subject alternative names matching IP address " + paramString + " found");
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
/*     */   private void matchDNS(String paramString, X509Certificate paramX509Certificate)
/*     */     throws CertificateException
/*     */   {
/*     */     try
/*     */     {
/* 193 */       SNIHostName localSNIHostName = new SNIHostName(paramString);
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 195 */       throw new CertificateException("Illegal given domain name: " + paramString, localIllegalArgumentException);
/*     */     }
/*     */     
/*     */ 
/* 199 */     Collection localCollection = paramX509Certificate.getSubjectAlternativeNames();
/* 200 */     if (localCollection != null) {
/* 201 */       int i = 0;
/* 202 */       for (localObject = localCollection.iterator(); ((Iterator)localObject).hasNext();) { List localList = (List)((Iterator)localObject).next();
/* 203 */         if (((Integer)localList.get(0)).intValue() == 2) {
/* 204 */           i = 1;
/* 205 */           String str2 = (String)localList.get(1);
/* 206 */           if (isMatched(paramString, str2)) {
/* 207 */             return;
/*     */           }
/*     */         }
/*     */       }
/* 211 */       if (i != 0)
/*     */       {
/*     */ 
/* 214 */         throw new CertificateException("No subject alternative DNS name matching " + paramString + " found.");
/*     */       }
/*     */     }
/*     */     
/* 218 */     X500Name localX500Name = getSubjectX500Name(paramX509Certificate);
/*     */     
/* 220 */     Object localObject = localX500Name.findMostSpecificAttribute(X500Name.commonName_oid);
/* 221 */     if (localObject != null) {
/*     */       try {
/* 223 */         if (isMatched(paramString, ((DerValue)localObject).getAsString())) {
/* 224 */           return;
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException) {}
/*     */     }
/*     */     
/* 230 */     String str1 = "No name matching " + paramString + " found";
/* 231 */     throw new CertificateException(str1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static X500Name getSubjectX500Name(X509Certificate paramX509Certificate)
/*     */     throws CertificateParsingException
/*     */   {
/*     */     try
/*     */     {
/* 245 */       Principal localPrincipal = paramX509Certificate.getSubjectDN();
/* 246 */       if ((localPrincipal instanceof X500Name)) {
/* 247 */         return (X500Name)localPrincipal;
/*     */       }
/* 249 */       X500Principal localX500Principal = paramX509Certificate.getSubjectX500Principal();
/* 250 */       return new X500Name(localX500Principal.getEncoded());
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 254 */       throw ((CertificateParsingException)new CertificateParsingException().initCause(localIOException));
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
/*     */ 
/*     */   private boolean isMatched(String paramString1, String paramString2)
/*     */   {
/*     */     try
/*     */     {
/* 276 */       SNIHostName localSNIHostName = new SNIHostName(paramString2.replace('*', 'x'));
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/* 279 */       return false;
/*     */     }
/*     */     
/* 282 */     if (this.checkType == 1)
/* 283 */       return matchAllWildcards(paramString1, paramString2);
/* 284 */     if (this.checkType == 2) {
/* 285 */       return matchLeftmostWildcard(paramString1, paramString2);
/*     */     }
/* 287 */     return false;
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
/*     */   private static boolean matchAllWildcards(String paramString1, String paramString2)
/*     */   {
/* 304 */     paramString1 = paramString1.toLowerCase(Locale.ENGLISH);
/* 305 */     paramString2 = paramString2.toLowerCase(Locale.ENGLISH);
/* 306 */     StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString1, ".");
/* 307 */     StringTokenizer localStringTokenizer2 = new StringTokenizer(paramString2, ".");
/*     */     
/* 309 */     if (localStringTokenizer1.countTokens() != localStringTokenizer2.countTokens()) {
/* 310 */       return false;
/*     */     }
/*     */     
/* 313 */     while (localStringTokenizer1.hasMoreTokens()) {
/* 314 */       if (!matchWildCards(localStringTokenizer1.nextToken(), localStringTokenizer2
/* 315 */         .nextToken())) {
/* 316 */         return false;
/*     */       }
/*     */     }
/* 319 */     return true;
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
/*     */   private static boolean matchLeftmostWildcard(String paramString1, String paramString2)
/*     */   {
/* 334 */     paramString1 = paramString1.toLowerCase(Locale.ENGLISH);
/* 335 */     paramString2 = paramString2.toLowerCase(Locale.ENGLISH);
/*     */     
/*     */ 
/* 338 */     int i = paramString2.indexOf(".");
/* 339 */     int j = paramString1.indexOf(".");
/*     */     
/* 341 */     if (i == -1)
/* 342 */       i = paramString2.length();
/* 343 */     if (j == -1) {
/* 344 */       j = paramString1.length();
/*     */     }
/* 346 */     if (matchWildCards(paramString1.substring(0, j), paramString2
/* 347 */       .substring(0, i)))
/*     */     {
/*     */ 
/* 350 */       return paramString2.substring(i).equals(paramString1
/* 351 */         .substring(j));
/*     */     }
/* 353 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean matchWildCards(String paramString1, String paramString2)
/*     */   {
/* 364 */     int i = paramString2.indexOf("*");
/* 365 */     if (i == -1) {
/* 366 */       return paramString1.equals(paramString2);
/*     */     }
/* 368 */     int j = 1;
/* 369 */     String str1 = "";
/* 370 */     String str2 = paramString2;
/*     */     
/* 372 */     while (i != -1)
/*     */     {
/*     */ 
/* 375 */       str1 = str2.substring(0, i);
/* 376 */       str2 = str2.substring(i + 1);
/*     */       
/* 378 */       int k = paramString1.indexOf(str1);
/* 379 */       if ((k == -1) || ((j != 0) && (k != 0)))
/*     */       {
/* 381 */         return false;
/*     */       }
/* 383 */       j = 0;
/*     */       
/*     */ 
/* 386 */       paramString1 = paramString1.substring(k + str1.length());
/* 387 */       i = str2.indexOf("*");
/*     */     }
/* 389 */     return paramString1.endsWith(str2);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\HostnameChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
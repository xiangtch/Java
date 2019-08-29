/*     */ package sun.misc;
/*     */ 
/*     */ import java.io.Console;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.net.HttpCookie;
/*     */ import java.nio.ByteOrder;
/*     */ import java.security.AccessController;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.zip.ZipFile;
/*     */ import javax.crypto.SealedObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SharedSecrets
/*     */ {
/*  47 */   private static final Unsafe unsafe = ;
/*     */   private static JavaUtilJarAccess javaUtilJarAccess;
/*     */   private static JavaLangAccess javaLangAccess;
/*     */   private static JavaLangRefAccess javaLangRefAccess;
/*     */   private static JavaIOAccess javaIOAccess;
/*     */   private static JavaNetAccess javaNetAccess;
/*     */   private static JavaNetHttpCookieAccess javaNetHttpCookieAccess;
/*     */   private static JavaNioAccess javaNioAccess;
/*     */   private static JavaIOFileDescriptorAccess javaIOFileDescriptorAccess;
/*     */   private static JavaSecurityProtectionDomainAccess javaSecurityProtectionDomainAccess;
/*     */   private static JavaSecurityAccess javaSecurityAccess;
/*     */   private static JavaUtilZipFileAccess javaUtilZipFileAccess;
/*     */   private static JavaAWTAccess javaAWTAccess;
/*     */   private static JavaOISAccess javaOISAccess;
/*     */   private static JavaxCryptoSealedObjectAccess javaxCryptoSealedObjectAccess;
/*     */   private static JavaObjectInputStreamAccess javaObjectInputStreamAccess;
/*     */   
/*     */   public static JavaUtilJarAccess javaUtilJarAccess() {
/*  65 */     if (javaUtilJarAccess == null)
/*     */     {
/*     */ 
/*  68 */       unsafe.ensureClassInitialized(JarFile.class);
/*     */     }
/*  70 */     return javaUtilJarAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaUtilJarAccess(JavaUtilJarAccess paramJavaUtilJarAccess) {
/*  74 */     javaUtilJarAccess = paramJavaUtilJarAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaLangAccess(JavaLangAccess paramJavaLangAccess) {
/*  78 */     javaLangAccess = paramJavaLangAccess;
/*     */   }
/*     */   
/*     */   public static JavaLangAccess getJavaLangAccess() {
/*  82 */     return javaLangAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaLangRefAccess(JavaLangRefAccess paramJavaLangRefAccess) {
/*  86 */     javaLangRefAccess = paramJavaLangRefAccess;
/*     */   }
/*     */   
/*     */   public static JavaLangRefAccess getJavaLangRefAccess() {
/*  90 */     return javaLangRefAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaNetAccess(JavaNetAccess paramJavaNetAccess) {
/*  94 */     javaNetAccess = paramJavaNetAccess;
/*     */   }
/*     */   
/*     */   public static JavaNetAccess getJavaNetAccess() {
/*  98 */     return javaNetAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaNetHttpCookieAccess(JavaNetHttpCookieAccess paramJavaNetHttpCookieAccess) {
/* 102 */     javaNetHttpCookieAccess = paramJavaNetHttpCookieAccess;
/*     */   }
/*     */   
/*     */   public static JavaNetHttpCookieAccess getJavaNetHttpCookieAccess() {
/* 106 */     if (javaNetHttpCookieAccess == null)
/* 107 */       unsafe.ensureClassInitialized(HttpCookie.class);
/* 108 */     return javaNetHttpCookieAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaNioAccess(JavaNioAccess paramJavaNioAccess) {
/* 112 */     javaNioAccess = paramJavaNioAccess;
/*     */   }
/*     */   
/*     */   public static JavaNioAccess getJavaNioAccess() {
/* 116 */     if (javaNioAccess == null)
/*     */     {
/*     */ 
/*     */ 
/* 120 */       unsafe.ensureClassInitialized(ByteOrder.class);
/*     */     }
/* 122 */     return javaNioAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaIOAccess(JavaIOAccess paramJavaIOAccess) {
/* 126 */     javaIOAccess = paramJavaIOAccess;
/*     */   }
/*     */   
/*     */   public static JavaIOAccess getJavaIOAccess() {
/* 130 */     if (javaIOAccess == null) {
/* 131 */       unsafe.ensureClassInitialized(Console.class);
/*     */     }
/* 133 */     return javaIOAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaIOFileDescriptorAccess(JavaIOFileDescriptorAccess paramJavaIOFileDescriptorAccess) {
/* 137 */     javaIOFileDescriptorAccess = paramJavaIOFileDescriptorAccess;
/*     */   }
/*     */   
/*     */   public static JavaIOFileDescriptorAccess getJavaIOFileDescriptorAccess() {
/* 141 */     if (javaIOFileDescriptorAccess == null) {
/* 142 */       unsafe.ensureClassInitialized(FileDescriptor.class);
/*     */     }
/* 144 */     return javaIOFileDescriptorAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaOISAccess(JavaOISAccess paramJavaOISAccess) {
/* 148 */     javaOISAccess = paramJavaOISAccess;
/*     */   }
/*     */   
/*     */   public static JavaOISAccess getJavaOISAccess() {
/* 152 */     if (javaOISAccess == null) {
/* 153 */       unsafe.ensureClassInitialized(ObjectInputStream.class);
/*     */     }
/* 155 */     return javaOISAccess;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setJavaSecurityProtectionDomainAccess(JavaSecurityProtectionDomainAccess paramJavaSecurityProtectionDomainAccess)
/*     */   {
/* 161 */     javaSecurityProtectionDomainAccess = paramJavaSecurityProtectionDomainAccess;
/*     */   }
/*     */   
/*     */   public static JavaSecurityProtectionDomainAccess getJavaSecurityProtectionDomainAccess()
/*     */   {
/* 166 */     if (javaSecurityProtectionDomainAccess == null)
/* 167 */       unsafe.ensureClassInitialized(ProtectionDomain.class);
/* 168 */     return javaSecurityProtectionDomainAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaSecurityAccess(JavaSecurityAccess paramJavaSecurityAccess) {
/* 172 */     javaSecurityAccess = paramJavaSecurityAccess;
/*     */   }
/*     */   
/*     */   public static JavaSecurityAccess getJavaSecurityAccess() {
/* 176 */     if (javaSecurityAccess == null) {
/* 177 */       unsafe.ensureClassInitialized(AccessController.class);
/*     */     }
/* 179 */     return javaSecurityAccess;
/*     */   }
/*     */   
/*     */   public static JavaUtilZipFileAccess getJavaUtilZipFileAccess() {
/* 183 */     if (javaUtilZipFileAccess == null)
/* 184 */       unsafe.ensureClassInitialized(ZipFile.class);
/* 185 */     return javaUtilZipFileAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaUtilZipFileAccess(JavaUtilZipFileAccess paramJavaUtilZipFileAccess) {
/* 189 */     javaUtilZipFileAccess = paramJavaUtilZipFileAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaAWTAccess(JavaAWTAccess paramJavaAWTAccess) {
/* 193 */     javaAWTAccess = paramJavaAWTAccess;
/*     */   }
/*     */   
/*     */ 
/*     */   public static JavaAWTAccess getJavaAWTAccess()
/*     */   {
/* 199 */     if (javaAWTAccess == null) {
/* 200 */       return null;
/*     */     }
/* 202 */     return javaAWTAccess;
/*     */   }
/*     */   
/*     */   public static JavaObjectInputStreamAccess getJavaObjectInputStreamAccess()
/*     */   {
/* 207 */     if (javaObjectInputStreamAccess == null) {
/* 208 */       unsafe.ensureClassInitialized(ObjectInputStream.class);
/*     */     }
/* 210 */     return javaObjectInputStreamAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaObjectInputStreamAccess(JavaObjectInputStreamAccess paramJavaObjectInputStreamAccess) {
/* 214 */     javaObjectInputStreamAccess = paramJavaObjectInputStreamAccess;
/*     */   }
/*     */   
/*     */   public static void setJavaxCryptoSealedObjectAccess(JavaxCryptoSealedObjectAccess paramJavaxCryptoSealedObjectAccess) {
/* 218 */     javaxCryptoSealedObjectAccess = paramJavaxCryptoSealedObjectAccess;
/*     */   }
/*     */   
/*     */   public static JavaxCryptoSealedObjectAccess getJavaxCryptoSealedObjectAccess() {
/* 222 */     if (javaxCryptoSealedObjectAccess == null) {
/* 223 */       unsafe.ensureClassInitialized(SealedObject.class);
/*     */     }
/* 225 */     return javaxCryptoSealedObjectAccess;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\SharedSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
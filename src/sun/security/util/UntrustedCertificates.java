/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class UntrustedCertificates
/*     */ {
/*  47 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */   private static final String ALGORITHM_KEY = "Algorithm";
/*  50 */   private static final Properties props = new Properties();
/*     */   
/*     */ 
/*     */ 
/*  54 */   static { AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/*  57 */         File localFile = new File(System.getProperty("java.home"), "lib/security/blacklisted.certs");
/*     */         try {
/*  59 */           FileInputStream localFileInputStream = new FileInputStream(localFile);Object localObject1 = null;
/*  60 */           try { UntrustedCertificates.props.load(localFileInputStream);
/*     */             
/*  62 */             for (Map.Entry localEntry : UntrustedCertificates.props.entrySet()) {
/*  63 */               localEntry.setValue(UntrustedCertificates.stripColons(localEntry.getValue()));
/*     */             }
/*     */           }
/*     */           catch (Throwable localThrowable2)
/*     */           {
/*  59 */             localObject1 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */           }
/*     */           finally
/*     */           {
/*     */ 
/*  65 */             if (localFileInputStream != null) if (localObject1 != null) try { localFileInputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localFileInputStream.close();
/*  66 */           } } catch (IOException localIOException) { if (UntrustedCertificates.debug != null) {
/*  67 */             UntrustedCertificates.debug.println("Error parsing blacklisted.certs");
/*     */           }
/*     */         }
/*  70 */         return null;
/*     */       } }); }
/*     */   
/*  73 */   private static final String algorithm = props.getProperty("Algorithm");
/*     */   
/*     */   private static String stripColons(Object paramObject)
/*     */   {
/*  77 */     String str = (String)paramObject;
/*  78 */     char[] arrayOfChar = str.toCharArray();
/*  79 */     int i = 0;
/*  80 */     for (int j = 0; j < arrayOfChar.length; j++) {
/*  81 */       if (arrayOfChar[j] != ':') {
/*  82 */         if (j != i) {
/*  83 */           arrayOfChar[i] = arrayOfChar[j];
/*     */         }
/*  85 */         i++;
/*     */       }
/*     */     }
/*  88 */     if (i == arrayOfChar.length) return str;
/*  89 */     return new String(arrayOfChar, 0, i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isUntrusted(X509Certificate paramX509Certificate)
/*     */   {
/*  98 */     if (algorithm == null) {
/*  99 */       return false;
/*     */     }
/*     */     String str;
/* 102 */     if ((paramX509Certificate instanceof X509CertImpl)) {
/* 103 */       str = ((X509CertImpl)paramX509Certificate).getFingerprint(algorithm);
/*     */     } else {
/*     */       try {
/* 106 */         str = new X509CertImpl(paramX509Certificate.getEncoded()).getFingerprint(algorithm);
/*     */       } catch (CertificateException localCertificateException) {
/* 108 */         return false;
/*     */       }
/*     */     }
/* 111 */     return props.containsKey(str);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\UntrustedCertificates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
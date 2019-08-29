/*    */ package sun.security.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.security.AccessController;
/*    */ import java.security.KeyStore;
/*    */ import java.security.PrivilegedAction;
/*    */ import java.security.cert.X509Certificate;
/*    */ import java.util.Collections;
/*    */ import java.util.Enumeration;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import sun.security.x509.X509CertImpl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AnchorCertificates
/*    */ {
/* 47 */   private static final Debug debug = Debug.getInstance("certpath");
/*    */   private static final String HASH = "SHA-256";
/* 49 */   private static Set<String> certs = Collections.emptySet();
/*    */   
/*    */   static {
/* 52 */     AccessController.doPrivileged(new PrivilegedAction()
/*    */     {
/*    */       public Void run() {
/* 55 */         File localFile = new File(System.getProperty("java.home"), "lib/security/cacerts");
/*    */         
/*    */         try
/*    */         {
/* 59 */           KeyStore localKeyStore = KeyStore.getInstance("JKS");
/* 60 */           FileInputStream localFileInputStream = new FileInputStream(localFile);Object localObject1 = null;
/* 61 */           try { localKeyStore.load(localFileInputStream, null);
/* 62 */             AnchorCertificates.access$002(new HashSet());
/* 63 */             Enumeration localEnumeration = localKeyStore.aliases();
/*    */             
/* 65 */             while (localEnumeration.hasMoreElements()) {
/* 66 */               String str = (String)localEnumeration.nextElement();
/*    */               
/* 68 */               if (str.contains(" [jdk"))
/*    */               {
/* 70 */                 X509Certificate localX509Certificate = (X509Certificate)localKeyStore.getCertificate(str);
/* 71 */                 AnchorCertificates.certs.add(X509CertImpl.getFingerprint("SHA-256", localX509Certificate));
/*    */               }
/*    */             }
/*    */           }
/*    */           catch (Throwable localThrowable2)
/*    */           {
/* 60 */             localObject1 = localThrowable2;throw localThrowable2;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */           }
/*    */           finally
/*    */           {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 74 */             if (localFileInputStream != null) if (localObject1 != null) try { localFileInputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localFileInputStream.close();
/*    */           }
/* 76 */         } catch (Exception localException) { if (AnchorCertificates.debug != null) {
/* 77 */             AnchorCertificates.debug.println("Error parsing cacerts");
/*    */           }
/* 79 */           localException.printStackTrace();
/*    */         }
/* 81 */         return null;
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean contains(X509Certificate paramX509Certificate)
/*    */   {
/* 93 */     String str = X509CertImpl.getFingerprint("SHA-256", paramX509Certificate);
/* 94 */     boolean bool = certs.contains(str);
/* 95 */     if ((bool) && (debug != null)) {
/* 96 */       debug.println("AnchorCertificate.contains: matched " + paramX509Certificate
/* 97 */         .getSubjectDN());
/*    */     }
/* 99 */     return bool;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\AnchorCertificates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
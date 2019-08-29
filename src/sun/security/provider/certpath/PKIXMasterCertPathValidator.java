/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXReason;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.StringJoiner;
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
/*     */ class PKIXMasterCertPathValidator
/*     */ {
/*  50 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void validate(CertPath paramCertPath, List<X509Certificate> paramList, List<PKIXCertPathChecker> paramList1)
/*     */     throws CertPathValidatorException
/*     */   {
/*  74 */     int i = paramList.size();
/*     */     
/*  76 */     if (debug != null) {
/*  77 */       debug.println("--------------------------------------------------------------");
/*     */       
/*  79 */       debug.println("Executing PKIX certification path validation algorithm.");
/*     */     }
/*     */     
/*     */ 
/*  83 */     for (int j = 0; j < i; j++)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  92 */       X509Certificate localX509Certificate = (X509Certificate)paramList.get(j);
/*     */       
/*  94 */       if (debug != null) {
/*  95 */         debug.println("Checking cert" + (j + 1) + " - Subject: " + localX509Certificate
/*  96 */           .getSubjectX500Principal());
/*     */       }
/*     */       
/*  99 */       Set localSet = localX509Certificate.getCriticalExtensionOIDs();
/* 100 */       if (localSet == null) {
/* 101 */         localSet = Collections.emptySet();
/*     */       }
/*     */       Object localObject;
/* 104 */       if ((debug != null) && (!localSet.isEmpty())) {
/* 105 */         StringJoiner localStringJoiner = new StringJoiner(", ", "{", "}");
/* 106 */         for (localObject = localSet.iterator(); ((Iterator)localObject).hasNext();) { String str = (String)((Iterator)localObject).next();
/* 107 */           localStringJoiner.add(str);
/*     */         }
/* 109 */         debug.println("Set of critical extensions: " + localStringJoiner
/* 110 */           .toString());
/*     */       }
/*     */       
/* 113 */       for (int k = 0; k < paramList1.size(); k++)
/*     */       {
/* 115 */         localObject = (PKIXCertPathChecker)paramList1.get(k);
/* 116 */         if (debug != null) {
/* 117 */           debug.println("-Using checker" + (k + 1) + " ... [" + localObject
/* 118 */             .getClass().getName() + "]");
/*     */         }
/*     */         
/* 121 */         if (j == 0) {
/* 122 */           ((PKIXCertPathChecker)localObject).init(false);
/*     */         }
/*     */         try {
/* 125 */           ((PKIXCertPathChecker)localObject).check(localX509Certificate, localSet);
/*     */           
/* 127 */           if (debug != null) {
/* 128 */             debug.println("-checker" + (k + 1) + " validation succeeded");
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (CertPathValidatorException localCertPathValidatorException)
/*     */         {
/* 135 */           throw new CertPathValidatorException(localCertPathValidatorException.getMessage(), localCertPathValidatorException.getCause() != null ? localCertPathValidatorException.getCause() : localCertPathValidatorException, paramCertPath, i - (j + 1), localCertPathValidatorException.getReason());
/*     */         }
/*     */       }
/*     */       
/* 139 */       if (!localSet.isEmpty()) {
/* 140 */         throw new CertPathValidatorException("unrecognized critical extension(s)", null, paramCertPath, i - (j + 1), PKIXReason.UNRECOGNIZED_CRIT_EXT);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 145 */       if (debug != null) {
/* 146 */         debug.println("\ncert" + (j + 1) + " validation succeeded.\n");
/*     */       }
/*     */     }
/* 149 */     if (debug != null) {
/* 150 */       debug.println("Cert path validation succeeded. (PKIX validation algorithm)");
/*     */       
/* 152 */       debug.println("--------------------------------------------------------------");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\PKIXMasterCertPathValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
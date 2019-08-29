/*     */ package sun.security.util;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.AlgorithmConstraints;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Security;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractAlgorithmConstraints
/*     */   implements AlgorithmConstraints
/*     */ {
/*     */   protected final AlgorithmDecomposer decomposer;
/*     */   
/*     */   protected AbstractAlgorithmConstraints(AlgorithmDecomposer paramAlgorithmDecomposer)
/*     */   {
/*  43 */     this.decomposer = paramAlgorithmDecomposer;
/*     */   }
/*     */   
/*     */   static String[] getAlgorithms(String paramString)
/*     */   {
/*  48 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run()
/*     */       {
/*  52 */         return Security.getProperty(this.val$propertyName);
/*     */       }
/*     */       
/*  55 */     });
/*  56 */     String[] arrayOfString = null;
/*  57 */     if ((str != null) && (!str.isEmpty()))
/*     */     {
/*  59 */       if ((str.length() >= 2) && (str.charAt(0) == '"') && 
/*  60 */         (str.charAt(str.length() - 1) == '"')) {
/*  61 */         str = str.substring(1, str.length() - 1);
/*     */       }
/*  63 */       arrayOfString = str.split(",");
/*  64 */       for (int i = 0; i < arrayOfString.length; i++) {
/*  65 */         arrayOfString[i] = arrayOfString[i].trim();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  70 */     if (arrayOfString == null) {
/*  71 */       arrayOfString = new String[0];
/*     */     }
/*  73 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   static boolean checkAlgorithm(String[] paramArrayOfString, String paramString, AlgorithmDecomposer paramAlgorithmDecomposer)
/*     */   {
/*  78 */     if ((paramString == null) || (paramString.length() == 0)) {
/*  79 */       throw new IllegalArgumentException("No algorithm name specified");
/*     */     }
/*     */     
/*  82 */     Set localSet = null;
/*  83 */     String str1; for (str1 : paramArrayOfString) {
/*  84 */       if ((str1 != null) && (!str1.isEmpty()))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  89 */         if (str1.equalsIgnoreCase(paramString)) {
/*  90 */           return false;
/*     */         }
/*     */         
/*     */ 
/*  94 */         if (localSet == null) {
/*  95 */           localSet = paramAlgorithmDecomposer.decompose(paramString);
/*     */         }
/*     */         
/*     */ 
/*  99 */         for (String str2 : localSet) {
/* 100 */           if (str1.equalsIgnoreCase(str2)) {
/* 101 */             return false;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 106 */     return true;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\AbstractAlgorithmConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
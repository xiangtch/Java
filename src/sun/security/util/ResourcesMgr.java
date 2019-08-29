/*    */ package sun.security.util;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import java.util.ResourceBundle;
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
/*    */ public class ResourcesMgr
/*    */ {
/*    */   private static ResourceBundle bundle;
/*    */   private static ResourceBundle altBundle;
/*    */   
/*    */   public static String getString(String paramString)
/*    */   {
/* 40 */     if (bundle == null)
/*    */     {
/*    */ 
/* 43 */       bundle = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
/*    */       {
/*    */         public ResourceBundle run() {
/* 46 */           return 
/* 47 */             ResourceBundle.getBundle("sun.security.util.Resources");
/*    */         }
/*    */       });
/*    */     }
/*    */     
/* 52 */     return bundle.getString(paramString);
/*    */   }
/*    */   
/*    */   public static String getString(String paramString1, String paramString2)
/*    */   {
/* 57 */     if (altBundle == null)
/*    */     {
/*    */ 
/* 60 */       altBundle = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
/*    */       {
/*    */         public ResourceBundle run() {
/* 63 */           return ResourceBundle.getBundle(this.val$altBundleName);
/*    */         }
/*    */       });
/*    */     }
/*    */     
/* 68 */     return altBundle.getString(paramString1);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\ResourcesMgr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
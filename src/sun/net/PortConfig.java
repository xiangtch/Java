/*    */ package sun.net;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
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
/*    */ public final class PortConfig
/*    */ {
/*    */   private static int defaultUpper;
/*    */   private static int defaultLower;
/*    */   private static final int upper;
/*    */   private static final int lower;
/*    */   
/*    */   static
/*    */   {
/* 42 */     AccessController.doPrivileged(new PrivilegedAction()
/*    */     {
/*    */       public Void run() {
/* 45 */         System.loadLibrary("net");
/* 46 */         return null;
/*    */       }
/*    */       
/* 49 */     });
/* 50 */     int i = getLower0();
/* 51 */     if (i == -1) {
/* 52 */       i = defaultLower;
/*    */     }
/* 54 */     lower = i;
/*    */     
/* 56 */     i = getUpper0();
/* 57 */     if (i == -1) {
/* 58 */       i = defaultUpper;
/*    */     }
/* 60 */     upper = i;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static int getLower()
/*    */   {
/* 67 */     return lower;
/*    */   }
/*    */   
/*    */   public static int getUpper() {
/* 71 */     return upper;
/*    */   }
/*    */   
/*    */   static native int getLower0();
/*    */   
/*    */   static native int getUpper0();
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\PortConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
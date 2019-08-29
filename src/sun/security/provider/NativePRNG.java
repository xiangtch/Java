/*    */ package sun.security.provider;
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
/*    */ 
/*    */ 
/*    */ public final class NativePRNG
/*    */ {
/*    */   static boolean isAvailable()
/*    */   {
/* 39 */     return false;
/*    */   }
/*    */   
/*    */   public static final class NonBlocking {
/*    */     static boolean isAvailable() {
/* 44 */       return false;
/*    */     }
/*    */   }
/*    */   
/*    */   public static final class Blocking {
/*    */     static boolean isAvailable() {
/* 50 */       return false;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\NativePRNG.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
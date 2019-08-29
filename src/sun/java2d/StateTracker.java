/*    */ package sun.java2d;
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
/*    */ 
/*    */ 
/*    */ public abstract interface StateTracker
/*    */ {
/* 72 */   public static final StateTracker ALWAYS_CURRENT = new StateTracker() {
/*    */     public boolean isCurrent() {
/* 74 */       return true;
/*    */     }
/*    */   };
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
/* 89 */   public static final StateTracker NEVER_CURRENT = new StateTracker() {
/*    */     public boolean isCurrent() {
/* 91 */       return false;
/*    */     }
/*    */   };
/*    */   
/*    */   public abstract boolean isCurrent();
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\StateTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
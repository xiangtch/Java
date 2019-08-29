/*    */ package sun.tracing;
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
/*    */ class NullProbe
/*    */   extends ProbeSkeleton
/*    */ {
/*    */   public NullProbe(Class<?>[] paramArrayOfClass)
/*    */   {
/* 74 */     super(paramArrayOfClass);
/*    */   }
/*    */   
/*    */   public boolean isEnabled() {
/* 78 */     return false;
/*    */   }
/*    */   
/*    */   public void uncheckedTrigger(Object[] paramArrayOfObject) {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\NullProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
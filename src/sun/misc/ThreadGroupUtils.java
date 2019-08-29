/*    */ package sun.misc;
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
/*    */ public final class ThreadGroupUtils
/*    */ {
/*    */   public static ThreadGroup getRootThreadGroup()
/*    */   {
/* 47 */     Object localObject = Thread.currentThread().getThreadGroup();
/* 48 */     ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent();
/* 49 */     while (localThreadGroup != null) {
/* 50 */       localObject = localThreadGroup;
/* 51 */       localThreadGroup = ((ThreadGroup)localObject).getParent();
/*    */     }
/* 53 */     return (ThreadGroup)localObject;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\ThreadGroupUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
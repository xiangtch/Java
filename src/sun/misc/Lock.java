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
/*    */ public class Lock
/*    */ {
/* 62 */   private boolean locked = false;
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
/*    */   public final synchronized void lock()
/*    */     throws InterruptedException
/*    */   {
/* 79 */     while (this.locked) {
/* 80 */       wait();
/*    */     }
/* 82 */     this.locked = true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public final synchronized void unlock()
/*    */   {
/* 90 */     this.locked = false;
/* 91 */     notifyAll();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\Lock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
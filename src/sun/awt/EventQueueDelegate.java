/*    */ package sun.awt;
/*    */ 
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.EventQueue;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EventQueueDelegate
/*    */ {
/* 33 */   private static final Object EVENT_QUEUE_DELEGATE_KEY = new StringBuilder("EventQueueDelegate.Delegate");
/*    */   
/*    */ 
/*    */ 
/* 37 */   public static void setDelegate(Delegate paramDelegate) { AppContext.getAppContext().put(EVENT_QUEUE_DELEGATE_KEY, paramDelegate); }
/*    */   
/*    */   public static Delegate getDelegate() {
/* 40 */     return 
/* 41 */       (Delegate)AppContext.getAppContext().get(EVENT_QUEUE_DELEGATE_KEY);
/*    */   }
/*    */   
/*    */   public static abstract interface Delegate
/*    */   {
/*    */     public abstract AWTEvent getNextEvent(EventQueue paramEventQueue)
/*    */       throws InterruptedException;
/*    */     
/*    */     public abstract Object beforeDispatch(AWTEvent paramAWTEvent)
/*    */       throws InterruptedException;
/*    */     
/*    */     public abstract void afterDispatch(AWTEvent paramAWTEvent, Object paramObject)
/*    */       throws InterruptedException;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\EventQueueDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
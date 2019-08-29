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
/*    */ public abstract interface SignalHandler
/*    */ {
/* 42 */   public static final SignalHandler SIG_DFL = new NativeSignalHandler(0L);
/*    */   
/*    */ 
/*    */ 
/* 46 */   public static final SignalHandler SIG_IGN = new NativeSignalHandler(1L);
/*    */   
/*    */   public abstract void handle(Signal paramSignal);
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\SignalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
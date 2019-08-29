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
/*    */ public class RequestProcessor
/*    */   implements Runnable
/*    */ {
/*    */   private static Queue<Request> requestQueue;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static Thread dispatcher;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void postRequest(Request paramRequest)
/*    */   {
/* 47 */     lazyInitialize();
/* 48 */     requestQueue.enqueue(paramRequest);
/*    */   }
/*    */   
/*    */   public void run()
/*    */   {
/*    */     
/*    */     try
/*    */     {
/*    */       for (;;)
/*    */       {
/* 58 */         Request localRequest = (Request)requestQueue.dequeue();
/*    */         try {
/* 60 */           localRequest.execute();
/*    */         }
/*    */         catch (Throwable localThrowable) {}
/*    */       }
/*    */     }
/*    */     catch (InterruptedException localInterruptedException) {}
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static synchronized void startProcessing()
/*    */   {
/* 79 */     if (dispatcher == null) {
/* 80 */       dispatcher = new Thread(new RequestProcessor(), "Request Processor");
/* 81 */       dispatcher.setPriority(7);
/* 82 */       dispatcher.start();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private static synchronized void lazyInitialize()
/*    */   {
/* 91 */     if (requestQueue == null) {
/* 92 */       requestQueue = new Queue();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\RequestProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.misc;
/*    */ 
/*    */ import sun.usagetracker.UsageTrackerClient;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PostVMInitHook
/*    */ {
/*    */   public static void run() {}
/*    */   
/*    */   private static void trackJavaUsage()
/*    */   {
/* 28 */     UsageTrackerClient localUsageTrackerClient = new UsageTrackerClient();
/* 29 */     localUsageTrackerClient.run("VM start", System.getProperty("sun.java.command"));
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\PostVMInitHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
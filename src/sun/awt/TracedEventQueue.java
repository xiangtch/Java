/*    */ package sun.awt;
/*    */ 
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.EventQueue;
/*    */ import java.awt.Toolkit;
/*    */ import java.io.PrintStream;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TracedEventQueue
/*    */   extends EventQueue
/*    */ {
/* 49 */   static boolean trace = false;
/*    */   
/*    */ 
/* 52 */   static int[] suppressedIDs = null;
/*    */   
/*    */   static {
/* 55 */     String str1 = Toolkit.getProperty("AWT.IgnoreEventIDs", "");
/* 56 */     if (str1.length() > 0) {
/* 57 */       StringTokenizer localStringTokenizer = new StringTokenizer(str1, ",");
/* 58 */       int i = localStringTokenizer.countTokens();
/* 59 */       suppressedIDs = new int[i];
/* 60 */       for (int j = 0; j < i; j++) {
/* 61 */         String str2 = localStringTokenizer.nextToken();
/*    */         try {
/* 63 */           suppressedIDs[j] = Integer.parseInt(str2);
/*    */         } catch (NumberFormatException localNumberFormatException) {
/* 65 */           System.err.println("Bad ID listed in AWT.IgnoreEventIDs in awt.properties: \"" + str2 + "\" -- skipped");
/*    */           
/*    */ 
/* 68 */           suppressedIDs[j] = 0;
/*    */         }
/*    */       }
/*    */     } else {
/* 72 */       suppressedIDs = new int[0];
/*    */     }
/*    */   }
/*    */   
/*    */   public void postEvent(AWTEvent paramAWTEvent) {
/* 77 */     int i = 1;
/* 78 */     int j = paramAWTEvent.getID();
/* 79 */     for (int k = 0; k < suppressedIDs.length; k++) {
/* 80 */       if (j == suppressedIDs[k]) {
/* 81 */         i = 0;
/* 82 */         break;
/*    */       }
/*    */     }
/* 85 */     if (i != 0) {
/* 86 */       System.out.println(Thread.currentThread().getName() + ": " + paramAWTEvent);
/*    */     }
/*    */     
/* 89 */     super.postEvent(paramAWTEvent);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\TracedEventQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
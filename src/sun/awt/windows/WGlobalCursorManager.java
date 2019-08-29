/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.Point;
/*    */ import sun.awt.GlobalCursorManager;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class WGlobalCursorManager
/*    */   extends GlobalCursorManager
/*    */ {
/*    */   private static WGlobalCursorManager manager;
/*    */   
/*    */   public static GlobalCursorManager getCursorManager()
/*    */   {
/* 35 */     if (manager == null) {
/* 36 */       manager = new WGlobalCursorManager();
/*    */     }
/* 38 */     return manager;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void nativeUpdateCursor(Component paramComponent)
/*    */   {
/* 46 */     getCursorManager().updateCursorLater(paramComponent);
/*    */   }
/*    */   
/*    */   protected native void setCursor(Component paramComponent, Cursor paramCursor, boolean paramBoolean);
/*    */   
/*    */   protected native void getCursorPos(Point paramPoint);
/*    */   
/*    */   protected native Component findHeavyweightUnderCursor(boolean paramBoolean);
/*    */   
/*    */   protected native Point getLocationOnScreen(Component paramComponent);
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WGlobalCursorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
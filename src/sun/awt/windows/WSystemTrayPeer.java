/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.SystemTray;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.peer.SystemTrayPeer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class WSystemTrayPeer
/*    */   extends WObjectPeer
/*    */   implements SystemTrayPeer
/*    */ {
/*    */   WSystemTrayPeer(SystemTray paramSystemTray)
/*    */   {
/* 35 */     this.target = paramSystemTray;
/*    */   }
/*    */   
/*    */   public Dimension getTrayIconSize()
/*    */   {
/* 40 */     return new Dimension(16, 16);
/*    */   }
/*    */   
/*    */   public boolean isSupported() {
/* 44 */     return ((WToolkit)Toolkit.getDefaultToolkit()).isTraySupported();
/*    */   }
/*    */   
/*    */   protected void disposeImpl() {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WSystemTrayPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
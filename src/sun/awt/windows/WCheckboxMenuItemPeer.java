/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.CheckboxMenuItem;
/*    */ import java.awt.event.ItemEvent;
/*    */ import java.awt.peer.CheckboxMenuItemPeer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class WCheckboxMenuItemPeer
/*    */   extends WMenuItemPeer
/*    */   implements CheckboxMenuItemPeer
/*    */ {
/*    */   public native void setState(boolean paramBoolean);
/*    */   
/*    */   WCheckboxMenuItemPeer(CheckboxMenuItem paramCheckboxMenuItem)
/*    */   {
/* 42 */     super(paramCheckboxMenuItem, true);
/* 43 */     setState(paramCheckboxMenuItem.getState());
/*    */   }
/*    */   
/*    */ 
/*    */   public void handleAction(final boolean paramBoolean)
/*    */   {
/* 49 */     final CheckboxMenuItem localCheckboxMenuItem = (CheckboxMenuItem)this.target;
/* 50 */     WToolkit.executeOnEventHandlerThread(localCheckboxMenuItem, new Runnable()
/*    */     {
/*    */       public void run() {
/* 53 */         localCheckboxMenuItem.setState(paramBoolean);
/* 54 */         WCheckboxMenuItemPeer.this.postEvent(new ItemEvent(localCheckboxMenuItem, 701, localCheckboxMenuItem
/* 55 */           .getLabel(), paramBoolean ? 1 : 2));
/*    */       }
/*    */     });
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WCheckboxMenuItemPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
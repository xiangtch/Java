/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Menu;
/*    */ import java.awt.MenuBar;
/*    */ import java.awt.MenuContainer;
/*    */ import java.awt.MenuItem;
/*    */ import java.awt.peer.MenuPeer;
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
/*    */ class WMenuPeer
/*    */   extends WMenuItemPeer
/*    */   implements MenuPeer
/*    */ {
/*    */   public native void addSeparator();
/*    */   
/*    */   public void addItem(MenuItem paramMenuItem)
/*    */   {
/* 38 */     WMenuItemPeer localWMenuItemPeer = (WMenuItemPeer)WToolkit.targetToPeer(paramMenuItem);
/*    */   }
/*    */   
/*    */ 
/*    */   public native void delItem(int paramInt);
/*    */   
/*    */   WMenuPeer() {}
/*    */   
/*    */   WMenuPeer(Menu paramMenu)
/*    */   {
/* 48 */     this.target = paramMenu;
/* 49 */     MenuContainer localMenuContainer = paramMenu.getParent();
/*    */     
/* 51 */     if ((localMenuContainer instanceof MenuBar)) {
/* 52 */       WMenuBarPeer localWMenuBarPeer = (WMenuBarPeer)WToolkit.targetToPeer(localMenuContainer);
/* 53 */       this.parent = localWMenuBarPeer;
/* 54 */       localWMenuBarPeer.addChildPeer(this);
/* 55 */       createMenu(localWMenuBarPeer);
/*    */     }
/* 57 */     else if ((localMenuContainer instanceof Menu)) {
/* 58 */       this.parent = ((WMenuPeer)WToolkit.targetToPeer(localMenuContainer));
/* 59 */       this.parent.addChildPeer(this);
/* 60 */       createSubMenu(this.parent);
/*    */     }
/*    */     else {
/* 63 */       throw new IllegalArgumentException("unknown menu container class");
/*    */     }
/*    */     
/* 66 */     checkMenuCreation();
/*    */   }
/*    */   
/*    */   native void createMenu(WMenuBarPeer paramWMenuBarPeer);
/*    */   
/*    */   native void createSubMenu(WMenuPeer paramWMenuPeer);
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WMenuPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
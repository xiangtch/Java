/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Event;
/*     */ import java.awt.Point;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.peer.PopupMenuPeer;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.MenuComponentAccessor;
/*     */ import sun.awt.AWTAccessor.PopupMenuAccessor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WPopupMenuPeer
/*     */   extends WMenuPeer
/*     */   implements PopupMenuPeer
/*     */ {
/*     */   WPopupMenuPeer(PopupMenu paramPopupMenu)
/*     */   {
/*  38 */     this.target = paramPopupMenu;
/*  39 */     Object localObject = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  44 */     boolean bool = AWTAccessor.getPopupMenuAccessor().isTrayIconPopup(paramPopupMenu);
/*  45 */     if (bool) {
/*  46 */       localObject = AWTAccessor.getMenuComponentAccessor().getParent(paramPopupMenu);
/*     */     } else {
/*  48 */       localObject = paramPopupMenu.getParent();
/*     */     }
/*     */     
/*  51 */     if ((localObject instanceof Component)) {
/*  52 */       WComponentPeer localWComponentPeer = (WComponentPeer)WToolkit.targetToPeer(localObject);
/*  53 */       if (localWComponentPeer == null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  58 */         localObject = WToolkit.getNativeContainer((Component)localObject);
/*  59 */         localWComponentPeer = (WComponentPeer)WToolkit.targetToPeer(localObject);
/*     */       }
/*  61 */       localWComponentPeer.addChildPeer(this);
/*  62 */       createMenu(localWComponentPeer);
/*     */       
/*  64 */       checkMenuCreation();
/*     */     } else {
/*  66 */       throw new IllegalArgumentException("illegal popup menu container class");
/*     */     }
/*     */   }
/*     */   
/*     */   private native void createMenu(WComponentPeer paramWComponentPeer);
/*     */   
/*     */   public void show(Event paramEvent)
/*     */   {
/*  74 */     Component localComponent = (Component)paramEvent.target;
/*  75 */     WComponentPeer localWComponentPeer = (WComponentPeer)WToolkit.targetToPeer(localComponent);
/*  76 */     if (localWComponentPeer == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  81 */       Container localContainer = WToolkit.getNativeContainer(localComponent);
/*  82 */       paramEvent.target = localContainer;
/*     */       
/*     */ 
/*  85 */       for (Object localObject = localComponent; localObject != localContainer; localObject = ((Component)localObject).getParent()) {
/*  86 */         Point localPoint = ((Component)localObject).getLocation();
/*  87 */         paramEvent.x += localPoint.x;
/*  88 */         paramEvent.y += localPoint.y;
/*     */       }
/*     */     }
/*  91 */     _show(paramEvent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void show(Component paramComponent, Point paramPoint)
/*     */   {
/*  99 */     WComponentPeer localWComponentPeer = (WComponentPeer)WToolkit.targetToPeer(paramComponent);
/* 100 */     Event localEvent = new Event(paramComponent, 0L, 501, paramPoint.x, paramPoint.y, 0, 0);
/* 101 */     if (localWComponentPeer == null) {
/* 102 */       Container localContainer = WToolkit.getNativeContainer(paramComponent);
/* 103 */       localEvent.target = localContainer;
/*     */     }
/* 105 */     localEvent.x = paramPoint.x;
/* 106 */     localEvent.y = paramPoint.y;
/* 107 */     _show(localEvent);
/*     */   }
/*     */   
/*     */   private native void _show(Event paramEvent);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WPopupMenuPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
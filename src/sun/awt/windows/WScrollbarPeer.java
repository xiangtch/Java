/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.peer.ScrollbarPeer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WScrollbarPeer
/*     */   extends WComponentPeer
/*     */   implements ScrollbarPeer
/*     */ {
/*     */   static native int getScrollbarSize(int paramInt);
/*     */   
/*     */   public Dimension getMinimumSize()
/*     */   {
/*  39 */     if (((Scrollbar)this.target).getOrientation() == 1) {
/*  40 */       return new Dimension(getScrollbarSize(1), 50);
/*     */     }
/*     */     
/*  43 */     return new Dimension(50, getScrollbarSize(0));
/*     */   }
/*     */   
/*     */ 
/*     */   public native void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */ 
/*     */   public native void setLineIncrement(int paramInt);
/*     */   
/*     */ 
/*     */   public native void setPageIncrement(int paramInt);
/*     */   
/*     */ 
/*     */   WScrollbarPeer(Scrollbar paramScrollbar)
/*     */   {
/*  58 */     super(paramScrollbar);
/*     */   }
/*     */   
/*     */   native void create(WComponentPeer paramWComponentPeer);
/*     */   
/*     */   void initialize() {
/*  64 */     Scrollbar localScrollbar = (Scrollbar)this.target;
/*  65 */     setValues(localScrollbar.getValue(), localScrollbar.getVisibleAmount(), localScrollbar
/*  66 */       .getMinimum(), localScrollbar.getMaximum());
/*  67 */     super.initialize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void postAdjustmentEvent(final int paramInt1, final int paramInt2, final boolean paramBoolean)
/*     */   {
/*  77 */     final Scrollbar localScrollbar = (Scrollbar)this.target;
/*  78 */     WToolkit.executeOnEventHandlerThread(localScrollbar, new Runnable() {
/*     */       public void run() {
/*  80 */         localScrollbar.setValueIsAdjusting(paramBoolean);
/*  81 */         localScrollbar.setValue(paramInt2);
/*  82 */         WScrollbarPeer.this.postEvent(new AdjustmentEvent(localScrollbar, 601, paramInt1, paramInt2, paramBoolean));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   void lineUp(int paramInt)
/*     */   {
/*  90 */     postAdjustmentEvent(2, paramInt, false);
/*     */   }
/*     */   
/*     */   void lineDown(int paramInt) {
/*  94 */     postAdjustmentEvent(1, paramInt, false);
/*     */   }
/*     */   
/*     */   void pageUp(int paramInt) {
/*  98 */     postAdjustmentEvent(3, paramInt, false);
/*     */   }
/*     */   
/*     */   void pageDown(int paramInt) {
/* 102 */     postAdjustmentEvent(4, paramInt, false);
/*     */   }
/*     */   
/*     */   void warp(int paramInt)
/*     */   {
/* 107 */     postAdjustmentEvent(5, paramInt, false);
/*     */   }
/*     */   
/* 110 */   private boolean dragInProgress = false;
/*     */   
/*     */   void drag(int paramInt) {
/* 113 */     if (!this.dragInProgress) {
/* 114 */       this.dragInProgress = true;
/*     */     }
/* 116 */     postAdjustmentEvent(5, paramInt, true);
/*     */   }
/*     */   
/*     */   void dragEnd(final int paramInt) {
/* 120 */     final Scrollbar localScrollbar = (Scrollbar)this.target;
/*     */     
/* 122 */     if (!this.dragInProgress) {
/* 123 */       return;
/*     */     }
/*     */     
/* 126 */     this.dragInProgress = false;
/* 127 */     WToolkit.executeOnEventHandlerThread(localScrollbar, new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 131 */         localScrollbar.setValueIsAdjusting(false);
/* 132 */         WScrollbarPeer.this.postEvent(new AdjustmentEvent(localScrollbar, 601, 5, paramInt, false));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean shouldClearRectBeforePaint()
/*     */   {
/* 140 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WScrollbarPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
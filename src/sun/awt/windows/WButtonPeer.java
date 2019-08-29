/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Button;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.peer.ButtonPeer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WButtonPeer
/*     */   extends WComponentPeer
/*     */   implements ButtonPeer
/*     */ {
/*     */   public Dimension getMinimumSize()
/*     */   {
/*  42 */     FontMetrics localFontMetrics = getFontMetrics(((Button)this.target).getFont());
/*  43 */     String str = ((Button)this.target).getLabel();
/*  44 */     if (str == null) {
/*  45 */       str = "";
/*     */     }
/*  47 */     return new Dimension(localFontMetrics.stringWidth(str) + 14, localFontMetrics
/*  48 */       .getHeight() + 8);
/*     */   }
/*     */   
/*     */   public boolean isFocusable() {
/*  52 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public native void setLabel(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */   WButtonPeer(Button paramButton)
/*     */   {
/*  63 */     super(paramButton);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   native void create(WComponentPeer paramWComponentPeer);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void handleAction(final long paramLong, int paramInt)
/*     */   {
/*  76 */     WToolkit.executeOnEventHandlerThread(this.target, new Runnable()
/*     */     {
/*     */       public void run() {
/*  79 */         WButtonPeer.this.postEvent(new ActionEvent(WButtonPeer.this.target, 1001, ((Button)WButtonPeer.this.target)
/*  80 */           .getActionCommand(), paramLong, this.val$modifiers)); } }, paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean shouldClearRectBeforePaint()
/*     */   {
/*  89 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static native void initIDs();
/*     */   
/*     */ 
/*     */   public boolean handleJavaKeyEvent(KeyEvent paramKeyEvent)
/*     */   {
/*  99 */     switch (paramKeyEvent.getID()) {
/*     */     case 402: 
/* 101 */       if (paramKeyEvent.getKeyCode() == 32) {
/* 102 */         handleAction(paramKeyEvent.getWhen(), paramKeyEvent.getModifiers());
/*     */       }
/*     */       break;
/*     */     }
/* 106 */     return false;
/*     */   }
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WButtonPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.dnd.DropTarget;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import sun.awt.LightweightFrame;
/*     */ import sun.swing.JLightweightFrame;
/*     */ import sun.swing.SwingAccessor;
/*     */ import sun.swing.SwingAccessor.JLightweightFrameAccessor;
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
/*     */ public class WLightweightFramePeer
/*     */   extends WFramePeer
/*     */ {
/*     */   public WLightweightFramePeer(LightweightFrame paramLightweightFrame)
/*     */   {
/*  41 */     super(paramLightweightFrame);
/*     */   }
/*     */   
/*     */   private LightweightFrame getLwTarget() {
/*  45 */     return (LightweightFrame)this.target;
/*     */   }
/*     */   
/*     */   public Graphics getGraphics()
/*     */   {
/*  50 */     return getLwTarget().getGraphics();
/*     */   }
/*     */   
/*     */   public void show()
/*     */   {
/*  55 */     super.show();
/*  56 */     postEvent(new ComponentEvent((Component)getTarget(), 102));
/*     */   }
/*     */   
/*     */   public void hide()
/*     */   {
/*  61 */     super.hide();
/*  62 */     postEvent(new ComponentEvent((Component)getTarget(), 103));
/*     */   }
/*     */   
/*     */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  67 */     super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
/*  68 */     postEvent(new ComponentEvent((Component)getTarget(), 100));
/*  69 */     postEvent(new ComponentEvent((Component)getTarget(), 101));
/*     */   }
/*     */   
/*     */   public void handleEvent(AWTEvent paramAWTEvent)
/*     */   {
/*  74 */     if (paramAWTEvent.getID() == 501) {
/*  75 */       emulateActivation(true);
/*     */     }
/*  77 */     super.handleEvent(paramAWTEvent);
/*     */   }
/*     */   
/*     */   public void grab()
/*     */   {
/*  82 */     getLwTarget().grabFocus();
/*     */   }
/*     */   
/*     */   public void ungrab()
/*     */   {
/*  87 */     getLwTarget().ungrabFocus();
/*     */   }
/*     */   
/*     */   public void updateCursorImmediately()
/*     */   {
/*  92 */     SwingAccessor.getJLightweightFrameAccessor().updateCursor((JLightweightFrame)getLwTarget());
/*     */   }
/*     */   
/*     */   public boolean isLightweightFramePeer() {
/*  96 */     return true;
/*     */   }
/*     */   
/*     */   public void addDropTarget(DropTarget paramDropTarget)
/*     */   {
/* 101 */     getLwTarget().addDropTarget(paramDropTarget);
/*     */   }
/*     */   
/*     */   public void removeDropTarget(DropTarget paramDropTarget)
/*     */   {
/* 106 */     getLwTarget().removeDropTarget(paramDropTarget);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WLightweightFramePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
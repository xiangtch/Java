/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Checkbox;
/*    */ import java.awt.CheckboxGroup;
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.event.ItemEvent;
/*    */ import java.awt.peer.CheckboxPeer;
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
/*    */ final class WCheckboxPeer
/*    */   extends WComponentPeer
/*    */   implements CheckboxPeer
/*    */ {
/*    */   public native void setState(boolean paramBoolean);
/*    */   
/*    */   public native void setCheckboxGroup(CheckboxGroup paramCheckboxGroup);
/*    */   
/*    */   public native void setLabel(String paramString);
/*    */   
/*    */   private static native int getCheckMarkSize();
/*    */   
/*    */   public Dimension getMinimumSize()
/*    */   {
/* 46 */     String str = ((Checkbox)this.target).getLabel();
/* 47 */     int i = getCheckMarkSize();
/* 48 */     if (str == null) {
/* 49 */       str = "";
/*    */     }
/* 51 */     FontMetrics localFontMetrics = getFontMetrics(((Checkbox)this.target).getFont());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 57 */     return new Dimension(localFontMetrics.stringWidth(str) + i / 2 + i, 
/* 58 */       Math.max(localFontMetrics.getHeight() + 8, i));
/*    */   }
/*    */   
/*    */   public boolean isFocusable()
/*    */   {
/* 63 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */   WCheckboxPeer(Checkbox paramCheckbox)
/*    */   {
/* 69 */     super(paramCheckbox);
/*    */   }
/*    */   
/*    */ 
/*    */   native void create(WComponentPeer paramWComponentPeer);
/*    */   
/*    */   void initialize()
/*    */   {
/* 77 */     Checkbox localCheckbox = (Checkbox)this.target;
/* 78 */     setState(localCheckbox.getState());
/* 79 */     setCheckboxGroup(localCheckbox.getCheckboxGroup());
/*    */     
/* 81 */     Color localColor = ((Component)this.target).getBackground();
/* 82 */     if (localColor != null) {
/* 83 */       setBackground(localColor);
/*    */     }
/*    */     
/* 86 */     super.initialize();
/*    */   }
/*    */   
/*    */   public boolean shouldClearRectBeforePaint()
/*    */   {
/* 91 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   void handleAction(final boolean paramBoolean)
/*    */   {
/* 97 */     final Checkbox localCheckbox = (Checkbox)this.target;
/* 98 */     WToolkit.executeOnEventHandlerThread(localCheckbox, new Runnable()
/*    */     {
/*    */       public void run() {
/* :1 */         CheckboxGroup localCheckboxGroup = localCheckbox.getCheckboxGroup();
/* :2 */         if ((localCheckboxGroup != null) && (localCheckbox == localCheckboxGroup.getSelectedCheckbox()) && (localCheckbox.getState())) {
/* :3 */           return;
/*    */         }
/* :5 */         localCheckbox.setState(paramBoolean);
/* :6 */         WCheckboxPeer.this.postEvent(new ItemEvent(localCheckbox, 701, localCheckbox
/* :7 */           .getLabel(), paramBoolean ? 1 : 2));
/*    */       }
/*    */     });
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WCheckboxPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
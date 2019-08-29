/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.TextField;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.KeyEvent;
/*    */ import java.awt.im.InputMethodRequests;
/*    */ import java.awt.peer.TextFieldPeer;
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
/*    */ final class WTextFieldPeer
/*    */   extends WTextComponentPeer
/*    */   implements TextFieldPeer
/*    */ {
/*    */   public Dimension getMinimumSize()
/*    */   {
/* 40 */     FontMetrics localFontMetrics = getFontMetrics(((TextField)this.target).getFont());
/* 41 */     return new Dimension(localFontMetrics.stringWidth(getText()) + 24, localFontMetrics
/* 42 */       .getHeight() + 8);
/*    */   }
/*    */   
/*    */   public boolean handleJavaKeyEvent(KeyEvent paramKeyEvent)
/*    */   {
/* 47 */     switch (paramKeyEvent.getID()) {
/*    */     case 400: 
/* 49 */       if ((paramKeyEvent.getKeyChar() == '\n') && (!paramKeyEvent.isAltDown()) && (!paramKeyEvent.isControlDown())) {
/* 50 */         postEvent(new ActionEvent(this.target, 1001, 
/* 51 */           getText(), paramKeyEvent.getWhen(), paramKeyEvent.getModifiers()));
/* 52 */         return true;
/*    */       }
/*    */       break;
/*    */     }
/* 56 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public native void setEchoChar(char paramChar);
/*    */   
/*    */ 
/*    */   public Dimension getPreferredSize(int paramInt)
/*    */   {
/* 66 */     return getMinimumSize(paramInt);
/*    */   }
/*    */   
/*    */   public Dimension getMinimumSize(int paramInt)
/*    */   {
/* 71 */     FontMetrics localFontMetrics = getFontMetrics(((TextField)this.target).getFont());
/* 72 */     return new Dimension(localFontMetrics.charWidth('0') * paramInt + 24, localFontMetrics.getHeight() + 8);
/*    */   }
/*    */   
/*    */   public InputMethodRequests getInputMethodRequests()
/*    */   {
/* 77 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   WTextFieldPeer(TextField paramTextField)
/*    */   {
/* 83 */     super(paramTextField);
/*    */   }
/*    */   
/*    */ 
/*    */   native void create(WComponentPeer paramWComponentPeer);
/*    */   
/*    */   void initialize()
/*    */   {
/* 91 */     TextField localTextField = (TextField)this.target;
/* 92 */     if (localTextField.echoCharIsSet()) {
/* 93 */       setEchoChar(localTextField.getEchoChar());
/*    */     }
/* 95 */     super.initialize();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WTextFieldPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
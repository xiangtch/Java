/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.TextArea;
/*    */ import java.awt.im.InputMethodRequests;
/*    */ import java.awt.peer.TextAreaPeer;
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
/*    */ final class WTextAreaPeer
/*    */   extends WTextComponentPeer
/*    */   implements TextAreaPeer
/*    */ {
/*    */   public Dimension getMinimumSize()
/*    */   {
/* 39 */     return getMinimumSize(10, 60);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void insert(String paramString, int paramInt)
/*    */   {
/* 46 */     replaceRange(paramString, paramInt, paramInt);
/*    */   }
/*    */   
/*    */ 
/*    */   public native void replaceRange(String paramString, int paramInt1, int paramInt2);
/*    */   
/*    */   public Dimension getPreferredSize(int paramInt1, int paramInt2)
/*    */   {
/* 54 */     return getMinimumSize(paramInt1, paramInt2);
/*    */   }
/*    */   
/*    */   public Dimension getMinimumSize(int paramInt1, int paramInt2)
/*    */   {
/* 59 */     FontMetrics localFontMetrics = getFontMetrics(((TextArea)this.target).getFont());
/* 60 */     return new Dimension(localFontMetrics.charWidth('0') * paramInt2 + 20, localFontMetrics.getHeight() * paramInt1 + 20);
/*    */   }
/*    */   
/*    */   public InputMethodRequests getInputMethodRequests()
/*    */   {
/* 65 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   WTextAreaPeer(TextArea paramTextArea)
/*    */   {
/* 71 */     super(paramTextArea);
/*    */   }
/*    */   
/*    */   native void create(WComponentPeer paramWComponentPeer);
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WTextAreaPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
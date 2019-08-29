/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.TextComponent;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.peer.TextComponentPeer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class WTextComponentPeer
/*     */   extends WComponentPeer
/*     */   implements TextComponentPeer
/*     */ {
/*     */   public void setEditable(boolean paramBoolean)
/*     */   {
/*  43 */     enableEditing(paramBoolean);
/*  44 */     setBackground(((TextComponent)this.target).getBackground());
/*     */   }
/*     */   
/*     */ 
/*     */   public native String getText();
/*     */   
/*     */   public native void setText(String paramString);
/*     */   
/*     */   public native int getSelectionStart();
/*     */   
/*     */   public native int getSelectionEnd();
/*     */   
/*     */   public native void select(int paramInt1, int paramInt2);
/*     */   
/*     */   WTextComponentPeer(TextComponent paramTextComponent)
/*     */   {
/*  60 */     super(paramTextComponent);
/*     */   }
/*     */   
/*     */   void initialize()
/*     */   {
/*  65 */     TextComponent localTextComponent = (TextComponent)this.target;
/*  66 */     String str = localTextComponent.getText();
/*     */     
/*  68 */     if (str != null) {
/*  69 */       setText(str);
/*     */     }
/*  71 */     select(localTextComponent.getSelectionStart(), localTextComponent.getSelectionEnd());
/*  72 */     setEditable(localTextComponent.isEditable());
/*     */     
/*  74 */     super.initialize();
/*     */   }
/*     */   
/*     */   native void enableEditing(boolean paramBoolean);
/*     */   
/*     */   public boolean isFocusable()
/*     */   {
/*  81 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCaretPosition(int paramInt)
/*     */   {
/*  91 */     select(paramInt, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCaretPosition()
/*     */   {
/* 100 */     return getSelectionStart();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void valueChanged()
/*     */   {
/* 107 */     postEvent(new TextEvent(this.target, 900));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static native void initIDs();
/*     */   
/*     */ 
/*     */   public boolean shouldClearRectBeforePaint()
/*     */   {
/* 117 */     return false;
/*     */   }
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WTextComponentPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dialog.ModalityType;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.Window;
/*     */ import java.awt.peer.DialogPeer;
/*     */ import java.util.List;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ComponentAccessor;
/*     */ import sun.awt.im.InputMethodManager;
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
/*     */ final class WDialogPeer
/*     */   extends WWindowPeer
/*     */   implements DialogPeer
/*     */ {
/*  38 */   static final Color defaultBackground = SystemColor.control;
/*     */   
/*     */   boolean needDefaultBackground;
/*     */   
/*     */ 
/*     */   WDialogPeer(Dialog paramDialog)
/*     */   {
/*  45 */     super(paramDialog);
/*     */     
/*  47 */     InputMethodManager localInputMethodManager = InputMethodManager.getInstance();
/*  48 */     String str = localInputMethodManager.getTriggerMenuString();
/*  49 */     if (str != null)
/*     */     {
/*  51 */       pSetIMMOption(str);
/*     */     }
/*     */   }
/*     */   
/*     */   native void createAwtDialog(WComponentPeer paramWComponentPeer);
/*     */   
/*     */   void create(WComponentPeer paramWComponentPeer) {
/*  58 */     preCreate(paramWComponentPeer);
/*  59 */     createAwtDialog(paramWComponentPeer);
/*     */   }
/*     */   
/*     */   native void showModal();
/*     */   
/*     */   native void endModal();
/*     */   
/*     */   void initialize() {
/*  67 */     Dialog localDialog = (Dialog)this.target;
/*     */     
/*     */ 
/*  70 */     if (this.needDefaultBackground) {
/*  71 */       localDialog.setBackground(defaultBackground);
/*     */     }
/*     */     
/*  74 */     super.initialize();
/*     */     
/*  76 */     if (localDialog.getTitle() != null) {
/*  77 */       setTitle(localDialog.getTitle());
/*     */     }
/*  79 */     setResizable(localDialog.isResizable());
/*     */   }
/*     */   
/*     */   protected void realShow()
/*     */   {
/*  84 */     Dialog localDialog = (Dialog)this.target;
/*  85 */     if (localDialog.getModalityType() != ModalityType.MODELESS) {
/*  86 */       showModal();
/*     */     } else {
/*  88 */       super.realShow();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   void hide()
/*     */   {
/*  95 */     Dialog localDialog = (Dialog)this.target;
/*  96 */     if (localDialog.getModalityType() != ModalityType.MODELESS) {
/*  97 */       endModal();
/*     */     } else {
/*  99 */       super.hide();
/*     */     }
/*     */   }
/*     */   
/*     */   public void blockWindows(List<Window> paramList)
/*     */   {
/* 105 */     for (Window localWindow : paramList) {
/* 106 */       WWindowPeer localWWindowPeer = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow);
/* 107 */       if (localWWindowPeer != null) {
/* 108 */         localWWindowPeer.setModalBlocked((Dialog)this.target, true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Dimension getMinimumSize()
/*     */   {
/* 115 */     if (((Dialog)this.target).isUndecorated()) {
/* 116 */       return super.getMinimumSize();
/*     */     }
/* 118 */     return new Dimension(getSysMinWidth(), getSysMinHeight());
/*     */   }
/*     */   
/*     */ 
/*     */   boolean isTargetUndecorated()
/*     */   {
/* 124 */     return ((Dialog)this.target).isUndecorated();
/*     */   }
/*     */   
/*     */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 129 */     if (((Dialog)this.target).isUndecorated()) {
/* 130 */       super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     } else {
/* 132 */       reshapeFrame(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setDefaultColor()
/*     */   {
/* 146 */     this.needDefaultBackground = true;
/*     */   }
/*     */   
/*     */   native void pSetIMMOption(String paramString);
/*     */   
/* 151 */   void notifyIMMOptionChange() { InputMethodManager.getInstance().notifyChangeRequest((Component)this.target); }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
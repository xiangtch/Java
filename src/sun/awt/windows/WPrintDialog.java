/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.Dialog;
/*    */ import java.awt.Frame;
/*    */ import java.awt.PrintJob;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.peer.ComponentPeer;
/*    */ import java.awt.print.PrinterJob;
/*    */ import sun.awt.AWTAccessor;
/*    */ import sun.awt.AWTAccessor.ComponentAccessor;
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
/*    */ class WPrintDialog
/*    */   extends Dialog
/*    */ {
/*    */   protected PrintJob job;
/*    */   protected PrinterJob pjob;
/*    */   
/*    */   WPrintDialog(Frame paramFrame, PrinterJob paramPrinterJob)
/*    */   {
/* 44 */     super(paramFrame, true);
/* 45 */     this.pjob = paramPrinterJob;
/* 46 */     setLayout(null);
/*    */   }
/*    */   
/*    */   WPrintDialog(Dialog paramDialog, PrinterJob paramPrinterJob) {
/* 50 */     super(paramDialog, "", true);
/* 51 */     this.pjob = paramPrinterJob;
/* 52 */     setLayout(null);
/*    */   }
/*    */   
/*    */   final void setPeer(ComponentPeer paramComponentPeer) {
/* 56 */     AWTAccessor.getComponentAccessor().setPeer(this, paramComponentPeer);
/*    */   }
/*    */   
/*    */ 
/*    */   public void addNotify()
/*    */   {
/* 62 */     synchronized (getTreeLock()) {
/* 63 */       Container localContainer = getParent();
/* 64 */       if ((localContainer != null) && (localContainer.getPeer() == null)) {
/* 65 */         localContainer.addNotify();
/*    */       }
/*    */       
/* 68 */       if (getPeer() == null)
/*    */       {
/* 70 */         WPrintDialogPeer localWPrintDialogPeer = ((WToolkit)Toolkit.getDefaultToolkit()).createWPrintDialog(this);
/* 71 */         setPeer(localWPrintDialogPeer);
/*    */       }
/* 73 */       super.addNotify();
/*    */     }
/*    */   }
/*    */   
/* 77 */   private boolean retval = false;
/*    */   
/*    */   final void setRetVal(boolean paramBoolean) {
/* 80 */     this.retval = paramBoolean;
/*    */   }
/*    */   
/*    */   final boolean getRetVal() {
/* 84 */     return this.retval;
/*    */   }
/*    */   
/*    */   private static native void initIDs();
/*    */   
/*    */   static {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WPrintDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
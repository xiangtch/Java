/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.Dialog;
/*    */ import java.awt.Frame;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.print.PageFormat;
/*    */ import java.awt.print.Printable;
/*    */ import java.awt.print.PrinterJob;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class WPageDialog
/*    */   extends WPrintDialog
/*    */ {
/*    */   PageFormat page;
/*    */   Printable painter;
/*    */   
/*    */   WPageDialog(Frame paramFrame, PrinterJob paramPrinterJob, PageFormat paramPageFormat, Printable paramPrintable)
/*    */   {
/* 46 */     super(paramFrame, paramPrinterJob);
/* 47 */     this.page = paramPageFormat;
/* 48 */     this.painter = paramPrintable;
/*    */   }
/*    */   
/*    */   WPageDialog(Dialog paramDialog, PrinterJob paramPrinterJob, PageFormat paramPageFormat, Printable paramPrintable)
/*    */   {
/* 53 */     super(paramDialog, paramPrinterJob);
/* 54 */     this.page = paramPageFormat;
/* 55 */     this.painter = paramPrintable;
/*    */   }
/*    */   
/*    */ 
/*    */   public void addNotify()
/*    */   {
/* 61 */     synchronized (getTreeLock()) {
/* 62 */       Container localContainer = getParent();
/* 63 */       if ((localContainer != null) && (localContainer.getPeer() == null)) {
/* 64 */         localContainer.addNotify();
/*    */       }
/*    */       
/* 67 */       if (getPeer() == null)
/*    */       {
/* 69 */         WPageDialogPeer localWPageDialogPeer = ((WToolkit)Toolkit.getDefaultToolkit()).createWPageDialog(this);
/* 70 */         setPeer(localWPageDialogPeer);
/*    */       }
/* 72 */       super.addNotify();
/*    */     }
/*    */   }
/*    */   
/*    */   private static native void initIDs();
/*    */   
/*    */   static {}
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WPageDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
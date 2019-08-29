/*    */ package sun.awt.windows;
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
/*    */ final class WPageDialogPeer
/*    */   extends WPrintDialogPeer
/*    */ {
/*    */   WPageDialogPeer(WPageDialog paramWPageDialog)
/*    */   {
/* 31 */     super(paramWPageDialog);
/*    */   }
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
/*    */   private native boolean _show();
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
/*    */   public void show()
/*    */   {
/* 56 */     new Thread(new Runnable()
/*    */     {
/*    */       public void run()
/*    */       {
/*    */         try
/*    */         {
/* 48 */           ((WPrintDialog)WPageDialogPeer.this.target).setRetVal(WPageDialogPeer.this._show());
/*    */         }
/*    */         catch (Exception localException) {}
/*    */         
/*    */ 
/*    */ 
/* 54 */         ((WPrintDialog)WPageDialogPeer.this.target).setVisible(false);
/*    */       }
/*    */     })
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
/* 56 */       .start();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WPageDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
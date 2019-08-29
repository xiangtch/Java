/*    */ package sun.awt.datatransfer;
/*    */ 
/*    */ import java.awt.datatransfer.DataFlavor;
/*    */ import java.awt.datatransfer.Transferable;
/*    */ import java.awt.datatransfer.UnsupportedFlavorException;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
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
/*    */ public class TransferableProxy
/*    */   implements Transferable
/*    */ {
/*    */   protected final Transferable transferable;
/*    */   protected final boolean isLocal;
/*    */   
/*    */   public TransferableProxy(Transferable paramTransferable, boolean paramBoolean)
/*    */   {
/* 61 */     this.transferable = paramTransferable;
/* 62 */     this.isLocal = paramBoolean;
/*    */   }
/*    */   
/* 65 */   public DataFlavor[] getTransferDataFlavors() { return this.transferable.getTransferDataFlavors(); }
/*    */   
/*    */   public boolean isDataFlavorSupported(DataFlavor paramDataFlavor) {
/* 68 */     return this.transferable.isDataFlavorSupported(paramDataFlavor);
/*    */   }
/*    */   
/*    */   public Object getTransferData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException
/*    */   {
/* 73 */     Object localObject = this.transferable.getTransferData(paramDataFlavor);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 78 */     if ((localObject != null) && (this.isLocal) && (paramDataFlavor.isFlavorSerializedObjectType())) {
/* 79 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*    */       
/* 81 */       ClassLoaderObjectOutputStream localClassLoaderObjectOutputStream = new ClassLoaderObjectOutputStream(localByteArrayOutputStream);
/*    */       
/* 83 */       localClassLoaderObjectOutputStream.writeObject(localObject);
/*    */       
/*    */ 
/* 86 */       ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
/*    */       
/*    */ 
/*    */       try
/*    */       {
/* 91 */         ClassLoaderObjectInputStream localClassLoaderObjectInputStream = new ClassLoaderObjectInputStream(localByteArrayInputStream, localClassLoaderObjectOutputStream.getClassLoaderMap());
/* 92 */         localObject = localClassLoaderObjectInputStream.readObject();
/*    */       } catch (ClassNotFoundException localClassNotFoundException) {
/* 94 */         throw ((IOException)new IOException().initCause(localClassNotFoundException));
/*    */       }
/*    */     }
/*    */     
/* 98 */     return localObject;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\datatransfer\TransferableProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
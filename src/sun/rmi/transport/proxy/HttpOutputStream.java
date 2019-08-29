/*    */ package sun.rmi.transport.proxy;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
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
/*    */ class HttpOutputStream
/*    */   extends ByteArrayOutputStream
/*    */ {
/*    */   protected OutputStream out;
/* 41 */   boolean responseSent = false;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public HttpOutputStream(OutputStream paramOutputStream)
/*    */   {
/* 49 */     this.out = paramOutputStream;
/*    */   }
/*    */   
/*    */ 
/*    */   public synchronized void close()
/*    */     throws IOException
/*    */   {
/* 56 */     if (!this.responseSent)
/*    */     {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 62 */       if (size() == 0) {
/* 63 */         write(emptyData);
/*    */       }
/* 65 */       DataOutputStream localDataOutputStream = new DataOutputStream(this.out);
/* 66 */       localDataOutputStream.writeBytes("Content-type: application/octet-stream\r\n");
/* 67 */       localDataOutputStream.writeBytes("Content-length: " + size() + "\r\n");
/* 68 */       localDataOutputStream.writeBytes("\r\n");
/* 69 */       writeTo(localDataOutputStream);
/* 70 */       localDataOutputStream.flush();
/*    */       
/*    */ 
/* 73 */       reset();
/* 74 */       this.responseSent = true;
/*    */     }
/*    */   }
/*    */   
/*    */ 
/* 79 */   private static byte[] emptyData = { 0 };
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\proxy\HttpOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.net.ftp.impl;
/*    */ 
/*    */ import sun.net.ftp.FtpClientProvider;
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
/*    */ public class DefaultFtpClientProvider
/*    */   extends FtpClientProvider
/*    */ {
/*    */   public sun.net.ftp.FtpClient createFtpClient()
/*    */   {
/* 35 */     return FtpClient.create();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\ftp\impl\DefaultFtpClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
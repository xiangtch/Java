/*    */ package sun.net.www.protocol.https;
/*    */ 
/*    */ import javax.net.ssl.HostnameVerifier;
/*    */ import javax.net.ssl.SSLSession;
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
/*    */ public final class DefaultHostnameVerifier
/*    */   implements HostnameVerifier
/*    */ {
/*    */   public boolean verify(String paramString, SSLSession paramSSLSession)
/*    */   {
/* 43 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\https\DefaultHostnameVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
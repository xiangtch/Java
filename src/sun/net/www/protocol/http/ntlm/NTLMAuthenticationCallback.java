/*    */ package sun.net.www.protocol.http.ntlm;
/*    */ 
/*    */ import java.net.URL;
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
/*    */ public abstract class NTLMAuthenticationCallback
/*    */ {
/* 36 */   private static volatile NTLMAuthenticationCallback callback = new DefaultNTLMAuthenticationCallback();
/*    */   
/*    */ 
/*    */   public static void setNTLMAuthenticationCallback(NTLMAuthenticationCallback paramNTLMAuthenticationCallback)
/*    */   {
/* 41 */     callback = paramNTLMAuthenticationCallback;
/*    */   }
/*    */   
/*    */   public static NTLMAuthenticationCallback getNTLMAuthenticationCallback() {
/* 45 */     return callback;
/*    */   }
/*    */   
/*    */ 
/*    */   public abstract boolean isTrustedSite(URL paramURL);
/*    */   
/*    */   static class DefaultNTLMAuthenticationCallback
/*    */     extends NTLMAuthenticationCallback
/*    */   {
/*    */     public boolean isTrustedSite(URL paramURL)
/*    */     {
/* 56 */       return true;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\ntlm\NTLMAuthenticationCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.net.www.protocol.http;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.net.PasswordAuthentication;
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
/*    */ public abstract class AuthCacheValue
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 735249334068211611L;
/*    */   
/*    */   public static enum Type
/*    */   {
/* 43 */     Proxy, 
/* 44 */     Server;
/*    */     
/*    */ 
/*    */     private Type() {}
/*    */   }
/*    */   
/* 50 */   protected static AuthCache cache = new AuthCacheImpl();
/*    */   
/*    */   public static void setAuthCache(AuthCache paramAuthCache) {
/* 53 */     cache = paramAuthCache;
/*    */   }
/*    */   
/*    */   abstract Type getAuthType();
/*    */   
/*    */   abstract AuthScheme getAuthScheme();
/*    */   
/*    */   abstract String getHost();
/*    */   
/*    */   abstract int getPort();
/*    */   
/*    */   abstract String getRealm();
/*    */   
/*    */   abstract String getPath();
/*    */   
/*    */   abstract String getProtocolScheme();
/*    */   
/*    */   abstract PasswordAuthentication credentials();
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\AuthCacheValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
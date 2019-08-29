/*    */ package sun.security.krb5.internal;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import sun.security.action.GetPropertyAction;
/*    */ import sun.security.krb5.internal.rcache.AuthTimeWithHash;
/*    */ import sun.security.krb5.internal.rcache.DflCache;
/*    */ import sun.security.krb5.internal.rcache.MemoryCache;
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
/*    */ public abstract class ReplayCache
/*    */ {
/*    */   public static ReplayCache getInstance(String paramString)
/*    */   {
/* 42 */     if (paramString == null)
/* 43 */       return new MemoryCache();
/* 44 */     if ((paramString.equals("dfl")) || (paramString.startsWith("dfl:")))
/* 45 */       return new DflCache(paramString);
/* 46 */     if (paramString.equals("none")) {
/* 47 */       new ReplayCache()
/*    */       {
/*    */         public void checkAndStore(KerberosTime paramAnonymousKerberosTime, AuthTimeWithHash paramAnonymousAuthTimeWithHash)
/*    */           throws KrbApErrException
/*    */         {}
/*    */       };
/*    */     }
/*    */     
/* 55 */     throw new IllegalArgumentException("Unknown type: " + paramString);
/*    */   }
/*    */   
/*    */   public static ReplayCache getInstance() {
/* 59 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.krb5.rcache"));
/*    */     
/* 61 */     return getInstance(str);
/*    */   }
/*    */   
/*    */   public abstract void checkAndStore(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash)
/*    */     throws KrbApErrException;
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\ReplayCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
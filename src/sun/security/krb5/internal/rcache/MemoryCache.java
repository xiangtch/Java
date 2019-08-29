/*    */ package sun.security.krb5.internal.rcache;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import sun.security.krb5.internal.KerberosTime;
/*    */ import sun.security.krb5.internal.Krb5;
/*    */ import sun.security.krb5.internal.KrbApErrException;
/*    */ import sun.security.krb5.internal.ReplayCache;
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
/*    */ public class MemoryCache
/*    */   extends ReplayCache
/*    */ {
/* 48 */   private static final int lifespan = ;
/* 49 */   private static final boolean DEBUG = Krb5.DEBUG;
/*    */   
/* 51 */   private final Map<String, AuthList> content = new HashMap();
/*    */   
/*    */   public synchronized void checkAndStore(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash)
/*    */     throws KrbApErrException
/*    */   {
/* 56 */     String str = paramAuthTimeWithHash.client + "|" + paramAuthTimeWithHash.server;
/* 57 */     AuthList localAuthList = (AuthList)this.content.get(str);
/* 58 */     if (DEBUG) {
/* 59 */       System.out.println("MemoryCache: add " + paramAuthTimeWithHash + " to " + str);
/*    */     }
/* 61 */     if (localAuthList == null) {
/* 62 */       localAuthList = new AuthList(lifespan);
/* 63 */       localAuthList.put(paramAuthTimeWithHash, paramKerberosTime);
/* 64 */       if (!localAuthList.isEmpty()) {
/* 65 */         this.content.put(str, localAuthList);
/*    */       }
/*    */     } else {
/* 68 */       if (DEBUG) {
/* 69 */         System.out.println("MemoryCache: Existing AuthList:\n" + localAuthList);
/*    */       }
/* 71 */       localAuthList.put(paramAuthTimeWithHash, paramKerberosTime);
/* 72 */       if (localAuthList.isEmpty()) {
/* 73 */         this.content.remove(str);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public String toString() {
/* 79 */     StringBuilder localStringBuilder = new StringBuilder();
/* 80 */     for (AuthList localAuthList : this.content.values()) {
/* 81 */       localStringBuilder.append(localAuthList.toString());
/*    */     }
/* 83 */     return localStringBuilder.toString();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\rcache\MemoryCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.security.krb5.internal.crypto;
/*    */ 
/*    */ import sun.security.krb5.Confounder;
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
/*    */ public class Nonce
/*    */ {
/*    */   public static synchronized int value()
/*    */   {
/* 37 */     return Confounder.intValue() & 0x7FFFFFFF;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\crypto\Nonce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
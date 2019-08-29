/*    */ package sun.security.smartcardio;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
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
/*    */ class PlatformPCSC
/*    */ {
/* 41 */   static final Throwable initException = ;
/*    */   static final int SCARD_PROTOCOL_T0 = 1;
/*    */   static final int SCARD_PROTOCOL_T1 = 2;
/*    */   
/*    */   private static Throwable loadLibrary() {
/* 46 */     try { AccessController.doPrivileged(new PrivilegedAction() {
/*    */         public Void run() {
/* 48 */           System.loadLibrary("j2pcsc");
/* 49 */           return null;
/*    */         }
/* 51 */       });
/* 52 */       return null;
/*    */     } catch (Throwable localThrowable) {
/* 54 */       return localThrowable;
/*    */     }
/*    */   }
/*    */   
/*    */   static final int SCARD_PROTOCOL_RAW = 65536;
/*    */   static final int SCARD_UNKNOWN = 0;
/*    */   static final int SCARD_ABSENT = 1;
/*    */   static final int SCARD_PRESENT = 2;
/*    */   static final int SCARD_SWALLOWED = 3;
/*    */   static final int SCARD_POWERED = 4;
/*    */   static final int SCARD_NEGOTIABLE = 5;
/*    */   static final int SCARD_SPECIFIC = 6;
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\smartcardio\PlatformPCSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.java2d.cmm.kcms;
/*    */ 
/*    */ import sun.java2d.cmm.CMMServiceProvider;
/*    */ import sun.java2d.cmm.PCMM;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class KcmsServiceProvider
/*    */   extends CMMServiceProvider
/*    */ {
/*    */   protected PCMM getModule()
/*    */   {
/* 15 */     return CMM.getModule();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\cmm\kcms\KcmsServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
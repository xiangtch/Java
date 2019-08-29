/*    */ package sun.io;
/*    */ 
/*    */ import sun.misc.VM;
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
/*    */ public class Win32ErrorMode
/*    */ {
/*    */   private static final long SEM_FAILCRITICALERRORS = 1L;
/*    */   private static final long SEM_NOGPFAULTERRORBOX = 2L;
/*    */   private static final long SEM_NOALIGNMENTFAULTEXCEPT = 4L;
/*    */   private static final long SEM_NOOPENFILEERRORBOX = 32768L;
/*    */   
/*    */   public static void initialize()
/*    */   {
/* 69 */     if (!VM.isBooted()) {
/* 70 */       String str = System.getProperty("sun.io.allowCriticalErrorMessageBox");
/* 71 */       if ((str == null) || (str.equals(Boolean.FALSE.toString()))) {
/* 72 */         long l = setErrorMode(0L);
/* 73 */         l |= 1L;
/* 74 */         setErrorMode(l);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   private static native long setErrorMode(long paramLong);
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\io\Win32ErrorMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
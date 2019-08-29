/*    */ package sun.management;
/*    */ 
/*    */ import com.sun.management.OperatingSystemMXBean;
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
/*    */ class OperatingSystemImpl
/*    */   extends BaseOperatingSystemImpl
/*    */   implements OperatingSystemMXBean
/*    */ {
/* 42 */   private static Object psapiLock = new Object();
/*    */   
/*    */   OperatingSystemImpl(VMManagement paramVMManagement) {
/* 45 */     super(paramVMManagement);
/*    */   }
/*    */   
/*    */   /* Error */
/*    */   public long getCommittedVirtualMemorySize()
/*    */   {
/*    */     // Byte code:
/*    */     //   0: getstatic 38	sun/management/OperatingSystemImpl:psapiLock	Ljava/lang/Object;
/*    */     //   3: dup
/*    */     //   4: astore_1
/*    */     //   5: monitorenter
/*    */     //   6: aload_0
/*    */     //   7: invokespecial 41	sun/management/OperatingSystemImpl:getCommittedVirtualMemorySize0	()J
/*    */     //   10: aload_1
/*    */     //   11: monitorexit
/*    */     //   12: lreturn
/*    */     //   13: astore_2
/*    */     //   14: aload_1
/*    */     //   15: monitorexit
/*    */     //   16: aload_2
/*    */     //   17: athrow
/*    */     // Line number table:
/*    */     //   Java source line #49	-> byte code offset #0
/*    */     //   Java source line #50	-> byte code offset #6
/*    */     //   Java source line #51	-> byte code offset #13
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	18	0	this	OperatingSystemImpl
/*    */     //   4	11	1	Ljava/lang/Object;	Object
/*    */     //   13	4	2	localObject1	Object
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   6	12	13	finally
/*    */     //   13	16	13	finally
/*    */   }
/*    */   
/*    */   private native long getCommittedVirtualMemorySize0();
/*    */   
/*    */   public native long getTotalSwapSpaceSize();
/*    */   
/*    */   public native long getFreeSwapSpaceSize();
/*    */   
/*    */   public native long getProcessCpuTime();
/*    */   
/*    */   public native long getFreePhysicalMemorySize();
/*    */   
/*    */   public native long getTotalPhysicalMemorySize();
/*    */   
/*    */   public native double getSystemCpuLoad();
/*    */   
/*    */   public native double getProcessCpuLoad();
/*    */   
/*    */   private static native void initialize();
/*    */   
/*    */   static
/*    */   {
/* 64 */     initialize();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\OperatingSystemImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
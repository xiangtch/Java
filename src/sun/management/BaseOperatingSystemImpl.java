/*    */ package sun.management;
/*    */ 
/*    */ import java.lang.management.OperatingSystemMXBean;
/*    */ import javax.management.ObjectName;
/*    */ import sun.misc.Unsafe;
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
/*    */ public class BaseOperatingSystemImpl
/*    */   implements OperatingSystemMXBean
/*    */ {
/*    */   private final VMManagement jvm;
/*    */   
/*    */   protected BaseOperatingSystemImpl(VMManagement paramVMManagement)
/*    */   {
/* 48 */     this.jvm = paramVMManagement;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 52 */     return this.jvm.getOsName();
/*    */   }
/*    */   
/*    */   public String getArch() {
/* 56 */     return this.jvm.getOsArch();
/*    */   }
/*    */   
/*    */   public String getVersion() {
/* 60 */     return this.jvm.getOsVersion();
/*    */   }
/*    */   
/*    */   public int getAvailableProcessors() {
/* 64 */     return this.jvm.getAvailableProcessors();
/*    */   }
/*    */   
/* 67 */   private static final Unsafe unsafe = ;
/* 68 */   private double[] loadavg = new double[1];
/*    */   
/* 70 */   public double getSystemLoadAverage() { if (unsafe.getLoadAverage(this.loadavg, 1) == 1) {
/* 71 */       return this.loadavg[0];
/*    */     }
/* 73 */     return -1.0D;
/*    */   }
/*    */   
/*    */   public ObjectName getObjectName() {
/* 77 */     return Util.newObjectName("java.lang:type=OperatingSystem");
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\BaseOperatingSystemImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
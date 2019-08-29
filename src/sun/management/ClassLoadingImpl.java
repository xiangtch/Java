/*    */ package sun.management;
/*    */ 
/*    */ import java.lang.management.ClassLoadingMXBean;
/*    */ import javax.management.ObjectName;
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
/*    */ class ClassLoadingImpl
/*    */   implements ClassLoadingMXBean
/*    */ {
/*    */   private final VMManagement jvm;
/*    */   
/*    */   ClassLoadingImpl(VMManagement paramVMManagement)
/*    */   {
/* 47 */     this.jvm = paramVMManagement;
/*    */   }
/*    */   
/*    */   public long getTotalLoadedClassCount() {
/* 51 */     return this.jvm.getTotalClassCount();
/*    */   }
/*    */   
/*    */   public int getLoadedClassCount() {
/* 55 */     return this.jvm.getLoadedClassCount();
/*    */   }
/*    */   
/*    */   public long getUnloadedClassCount() {
/* 59 */     return this.jvm.getUnloadedClassCount();
/*    */   }
/*    */   
/*    */   public boolean isVerbose() {
/* 63 */     return this.jvm.getVerboseClass();
/*    */   }
/*    */   
/*    */   public void setVerbose(boolean paramBoolean) {
/* 67 */     Util.checkControlAccess();
/*    */     
/* 69 */     setVerboseClass(paramBoolean);
/*    */   }
/*    */   
/*    */   static native void setVerboseClass(boolean paramBoolean);
/*    */   
/* 74 */   public ObjectName getObjectName() { return Util.newObjectName("java.lang:type=ClassLoading"); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\management\ClassLoadingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
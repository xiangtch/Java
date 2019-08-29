/*     */ package sun.management;
/*     */ 
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MemoryMXBean;
/*     */ import java.lang.management.MemoryManagerMXBean;
/*     */ import java.lang.management.MemoryNotificationInfo;
/*     */ import java.lang.management.MemoryPoolMXBean;
/*     */ import java.lang.management.MemoryUsage;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.Notification;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.openmbean.CompositeData;
/*     */ import sun.misc.VM;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class MemoryImpl
/*     */   extends NotificationEmitterSupport
/*     */   implements MemoryMXBean
/*     */ {
/*     */   private final VMManagement jvm;
/*  51 */   private static MemoryPoolMXBean[] pools = null;
/*  52 */   private static MemoryManagerMXBean[] mgrs = null;
/*     */   
/*     */   private static final String notifName = "javax.management.Notification";
/*     */   
/*     */   MemoryImpl(VMManagement paramVMManagement)
/*     */   {
/*  58 */     this.jvm = paramVMManagement;
/*     */   }
/*     */   
/*     */   public int getObjectPendingFinalizationCount() {
/*  62 */     return VM.getFinalRefCount();
/*     */   }
/*     */   
/*     */   public void gc() {
/*  66 */     Runtime.getRuntime().gc();
/*     */   }
/*     */   
/*     */   public MemoryUsage getHeapMemoryUsage()
/*     */   {
/*  71 */     return getMemoryUsage0(true);
/*     */   }
/*     */   
/*     */   public MemoryUsage getNonHeapMemoryUsage() {
/*  75 */     return getMemoryUsage0(false);
/*     */   }
/*     */   
/*     */   public boolean isVerbose() {
/*  79 */     return this.jvm.getVerboseGC();
/*     */   }
/*     */   
/*     */   public void setVerbose(boolean paramBoolean) {
/*  83 */     Util.checkControlAccess();
/*     */     
/*  85 */     setVerboseGC(paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */   static synchronized MemoryPoolMXBean[] getMemoryPools()
/*     */   {
/*  91 */     if (pools == null) {
/*  92 */       pools = getMemoryPools0();
/*     */     }
/*  94 */     return pools;
/*     */   }
/*     */   
/*  97 */   static synchronized MemoryManagerMXBean[] getMemoryManagers() { if (mgrs == null) {
/*  98 */       mgrs = getMemoryManagers0();
/*     */     }
/* 100 */     return mgrs;
/*     */   }
/*     */   
/*     */ 
/*     */   private static native MemoryPoolMXBean[] getMemoryPools0();
/*     */   
/*     */ 
/*     */   private static native MemoryManagerMXBean[] getMemoryManagers0();
/*     */   
/* 109 */   private static final String[] notifTypes = { "java.management.memory.threshold.exceeded", "java.management.memory.collection.threshold.exceeded" };
/*     */   
/*     */ 
/*     */ 
/* 113 */   private static final String[] notifMsgs = { "Memory usage exceeds usage threshold", "Memory usage exceeds collection usage threshold" };
/*     */   
/*     */   private native MemoryUsage getMemoryUsage0(boolean paramBoolean);
/*     */   
/*     */   private native void setVerboseGC(boolean paramBoolean);
/*     */   
/* 119 */   public MBeanNotificationInfo[] getNotificationInfo() { return new MBeanNotificationInfo[] { new MBeanNotificationInfo(notifTypes, "javax.management.Notification", "Memory Notification") }; }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String getNotifMsg(String paramString)
/*     */   {
/* 125 */     for (int i = 0; i < notifTypes.length; i++) {
/* 126 */       if (paramString == notifTypes[i]) {
/* 127 */         return notifMsgs[i];
/*     */       }
/*     */     }
/* 130 */     return "Unknown message";
/*     */   }
/*     */   
/* 133 */   private static long seqNumber = 0L;
/*     */   
/* 135 */   private static long getNextSeqNumber() { return ++seqNumber; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void createNotification(String paramString1, String paramString2, MemoryUsage paramMemoryUsage, long paramLong)
/*     */   {
/* 142 */     MemoryImpl localMemoryImpl = (MemoryImpl)ManagementFactory.getMemoryMXBean();
/* 143 */     if (!localMemoryImpl.hasListeners())
/*     */     {
/* 145 */       return;
/*     */     }
/* 147 */     long l = System.currentTimeMillis();
/* 148 */     String str = getNotifMsg(paramString1);
/*     */     
/*     */ 
/* 151 */     Notification localNotification = new Notification(paramString1, localMemoryImpl.getObjectName(), getNextSeqNumber(), l, str);
/*     */     
/*     */ 
/* 154 */     MemoryNotificationInfo localMemoryNotificationInfo = new MemoryNotificationInfo(paramString2, paramMemoryUsage, paramLong);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 159 */     CompositeData localCompositeData = MemoryNotifInfoCompositeData.toCompositeData(localMemoryNotificationInfo);
/* 160 */     localNotification.setUserData(localCompositeData);
/* 161 */     localMemoryImpl.sendNotification(localNotification);
/*     */   }
/*     */   
/*     */   public ObjectName getObjectName() {
/* 165 */     return Util.newObjectName("java.lang:type=Memory");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\MemoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
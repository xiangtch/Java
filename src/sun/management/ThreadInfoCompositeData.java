/*     */ package sun.management;
/*     */ 
/*     */ import java.lang.management.LockInfo;
/*     */ import java.lang.management.MonitorInfo;
/*     */ import java.lang.management.ThreadInfo;
/*     */ import java.util.Set;
/*     */ import javax.management.openmbean.CompositeData;
/*     */ import javax.management.openmbean.CompositeDataSupport;
/*     */ import javax.management.openmbean.CompositeType;
/*     */ import javax.management.openmbean.OpenDataException;
/*     */ import javax.management.openmbean.OpenType;
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
/*     */ public class ThreadInfoCompositeData
/*     */   extends LazyCompositeData
/*     */ {
/*     */   private final ThreadInfo threadInfo;
/*     */   private final CompositeData cdata;
/*     */   private final boolean currentVersion;
/*     */   private static final String THREAD_ID = "threadId";
/*     */   private static final String THREAD_NAME = "threadName";
/*     */   private static final String THREAD_STATE = "threadState";
/*     */   private static final String BLOCKED_TIME = "blockedTime";
/*     */   private static final String BLOCKED_COUNT = "blockedCount";
/*     */   private static final String WAITED_TIME = "waitedTime";
/*     */   private static final String WAITED_COUNT = "waitedCount";
/*     */   private static final String LOCK_INFO = "lockInfo";
/*     */   private static final String LOCK_NAME = "lockName";
/*     */   private static final String LOCK_OWNER_ID = "lockOwnerId";
/*     */   private static final String LOCK_OWNER_NAME = "lockOwnerName";
/*     */   private static final String STACK_TRACE = "stackTrace";
/*     */   private static final String SUSPENDED = "suspended";
/*     */   private static final String IN_NATIVE = "inNative";
/*     */   private static final String LOCKED_MONITORS = "lockedMonitors";
/*     */   private static final String LOCKED_SYNCS = "lockedSynchronizers";
/*     */   
/*     */   private ThreadInfoCompositeData(ThreadInfo paramThreadInfo)
/*     */   {
/*  48 */     this.threadInfo = paramThreadInfo;
/*  49 */     this.currentVersion = true;
/*  50 */     this.cdata = null;
/*     */   }
/*     */   
/*     */   private ThreadInfoCompositeData(CompositeData paramCompositeData) {
/*  54 */     this.threadInfo = null;
/*  55 */     this.currentVersion = isCurrentVersion(paramCompositeData);
/*  56 */     this.cdata = paramCompositeData;
/*     */   }
/*     */   
/*     */   public ThreadInfo getThreadInfo() {
/*  60 */     return this.threadInfo;
/*     */   }
/*     */   
/*     */   public boolean isCurrentVersion() {
/*  64 */     return this.currentVersion;
/*     */   }
/*     */   
/*     */   public static ThreadInfoCompositeData getInstance(CompositeData paramCompositeData) {
/*  68 */     validateCompositeData(paramCompositeData);
/*  69 */     return new ThreadInfoCompositeData(paramCompositeData);
/*     */   }
/*     */   
/*     */   public static CompositeData toCompositeData(ThreadInfo paramThreadInfo) {
/*  73 */     ThreadInfoCompositeData localThreadInfoCompositeData = new ThreadInfoCompositeData(paramThreadInfo);
/*  74 */     return localThreadInfoCompositeData.getCompositeData();
/*     */   }
/*     */   
/*     */   protected CompositeData getCompositeData()
/*     */   {
/*  79 */     StackTraceElement[] arrayOfStackTraceElement = this.threadInfo.getStackTrace();
/*  80 */     CompositeData[] arrayOfCompositeData1 = new CompositeData[arrayOfStackTraceElement.length];
/*     */     
/*  82 */     for (int i = 0; i < arrayOfStackTraceElement.length; i++) {
/*  83 */       localObject1 = arrayOfStackTraceElement[i];
/*  84 */       arrayOfCompositeData1[i] = StackTraceElementCompositeData.toCompositeData((StackTraceElement)localObject1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  89 */     CompositeData localCompositeData = LockInfoCompositeData.toCompositeData(this.threadInfo.getLockInfo());
/*     */     
/*     */ 
/*  92 */     Object localObject1 = this.threadInfo.getLockedSynchronizers();
/*  93 */     CompositeData[] arrayOfCompositeData2 = new CompositeData[localObject1.length];
/*     */     
/*  95 */     for (int j = 0; j < localObject1.length; j++) {
/*  96 */       localObject2 = localObject1[j];
/*  97 */       arrayOfCompositeData2[j] = LockInfoCompositeData.toCompositeData((LockInfo)localObject2);
/*     */     }
/*     */     
/* 100 */     MonitorInfo[] arrayOfMonitorInfo = this.threadInfo.getLockedMonitors();
/* 101 */     Object localObject2 = new CompositeData[arrayOfMonitorInfo.length];
/*     */     
/* 103 */     for (int k = 0; k < arrayOfMonitorInfo.length; k++) {
/* 104 */       MonitorInfo localMonitorInfo = arrayOfMonitorInfo[k];
/* 105 */       localObject2[k] = MonitorInfoCompositeData.toCompositeData(localMonitorInfo);
/*     */     }
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
/* 124 */     Object[] arrayOfObject = { new Long(this.threadInfo.getThreadId()), this.threadInfo.getThreadName(), this.threadInfo.getThreadState().name(), new Long(this.threadInfo.getBlockedTime()), new Long(this.threadInfo.getBlockedCount()), new Long(this.threadInfo.getWaitedTime()), new Long(this.threadInfo.getWaitedCount()), localCompositeData, this.threadInfo.getLockName(), new Long(this.threadInfo.getLockOwnerId()), this.threadInfo.getLockOwnerName(), arrayOfCompositeData1, new Boolean(this.threadInfo.isSuspended()), new Boolean(this.threadInfo.isInNative()), localObject2, arrayOfCompositeData2 };
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 130 */       return new CompositeDataSupport(threadInfoCompositeType, threadInfoItemNames, arrayOfObject);
/*     */ 
/*     */     }
/*     */     catch (OpenDataException localOpenDataException)
/*     */     {
/* 135 */       throw new AssertionError(localOpenDataException);
/*     */     }
/*     */   }
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
/* 157 */   private static final String[] threadInfoItemNames = { "threadId", "threadName", "threadState", "blockedTime", "blockedCount", "waitedTime", "waitedCount", "lockInfo", "lockName", "lockOwnerId", "lockOwnerName", "stackTrace", "suspended", "inNative", "lockedMonitors", "lockedSynchronizers" };
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
/* 177 */   private static final String[] threadInfoV6Attributes = { "lockInfo", "lockedMonitors", "lockedSynchronizers" };
/*     */   
/*     */   private static final CompositeType threadInfoCompositeType;
/*     */   
/*     */   private static final CompositeType threadInfoV5CompositeType;
/*     */   
/*     */   private static final CompositeType lockInfoCompositeType;
/*     */   
/*     */   private static final long serialVersionUID = 2464378539119753175L;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 191 */       threadInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(ThreadInfo.class);
/*     */       
/*     */ 
/* 194 */       String[] arrayOfString1 = (String[])threadInfoCompositeType.keySet().toArray(new String[0]);
/* 195 */       int i = threadInfoItemNames.length - threadInfoV6Attributes.length;
/*     */       
/* 197 */       localObject2 = new String[i];
/* 198 */       String[] arrayOfString2 = new String[i];
/* 199 */       OpenType[] arrayOfOpenType = new OpenType[i];
/* 200 */       int j = 0;
/* 201 */       for (String str : arrayOfString1) {
/* 202 */         if (isV5Attribute(str)) {
/* 203 */           localObject2[j] = str;
/* 204 */           arrayOfString2[j] = threadInfoCompositeType.getDescription(str);
/* 205 */           arrayOfOpenType[j] = threadInfoCompositeType.getType(str);
/* 206 */           j++;
/*     */         }
/*     */       }
/*     */       
/* 210 */       threadInfoV5CompositeType = new CompositeType("java.lang.management.ThreadInfo", "J2SE 5.0 java.lang.management.ThreadInfo", (String[])localObject2, arrayOfString2, arrayOfOpenType);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (OpenDataException localOpenDataException)
/*     */     {
/*     */ 
/*     */ 
/* 218 */       throw new AssertionError(localOpenDataException);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 226 */     Object localObject1 = new Object();
/*     */     
/* 228 */     LockInfo localLockInfo = new LockInfo(localObject1.getClass().getName(), System.identityHashCode(localObject1));
/* 229 */     Object localObject2 = LockInfoCompositeData.toCompositeData(localLockInfo);
/* 230 */     lockInfoCompositeType = ((CompositeData)localObject2).getCompositeType();
/*     */   }
/*     */   
/*     */   private static boolean isV5Attribute(String paramString) {
/* 234 */     for (String str : threadInfoV6Attributes) {
/* 235 */       if (paramString.equals(str)) {
/* 236 */         return false;
/*     */       }
/*     */     }
/* 239 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean isCurrentVersion(CompositeData paramCompositeData) {
/* 243 */     if (paramCompositeData == null) {
/* 244 */       throw new NullPointerException("Null CompositeData");
/*     */     }
/*     */     
/* 247 */     return isTypeMatched(threadInfoCompositeType, paramCompositeData.getCompositeType());
/*     */   }
/*     */   
/*     */   public long threadId() {
/* 251 */     return getLong(this.cdata, "threadId");
/*     */   }
/*     */   
/*     */ 
/*     */   public String threadName()
/*     */   {
/* 257 */     String str = getString(this.cdata, "threadName");
/* 258 */     if (str == null) {
/* 259 */       throw new IllegalArgumentException("Invalid composite data: Attribute threadName has null value");
/*     */     }
/*     */     
/* 262 */     return str;
/*     */   }
/*     */   
/*     */   public Thread.State threadState() {
/* 266 */     return Thread.State.valueOf(getString(this.cdata, "threadState"));
/*     */   }
/*     */   
/*     */   public long blockedTime() {
/* 270 */     return getLong(this.cdata, "blockedTime");
/*     */   }
/*     */   
/*     */   public long blockedCount() {
/* 274 */     return getLong(this.cdata, "blockedCount");
/*     */   }
/*     */   
/*     */   public long waitedTime() {
/* 278 */     return getLong(this.cdata, "waitedTime");
/*     */   }
/*     */   
/*     */   public long waitedCount() {
/* 282 */     return getLong(this.cdata, "waitedCount");
/*     */   }
/*     */   
/*     */ 
/*     */   public String lockName()
/*     */   {
/* 288 */     return getString(this.cdata, "lockName");
/*     */   }
/*     */   
/*     */   public long lockOwnerId() {
/* 292 */     return getLong(this.cdata, "lockOwnerId");
/*     */   }
/*     */   
/*     */   public String lockOwnerName() {
/* 296 */     return getString(this.cdata, "lockOwnerName");
/*     */   }
/*     */   
/*     */   public boolean suspended() {
/* 300 */     return getBoolean(this.cdata, "suspended");
/*     */   }
/*     */   
/*     */   public boolean inNative() {
/* 304 */     return getBoolean(this.cdata, "inNative");
/*     */   }
/*     */   
/*     */   public StackTraceElement[] stackTrace()
/*     */   {
/* 309 */     CompositeData[] arrayOfCompositeData = (CompositeData[])this.cdata.get("stackTrace");
/*     */     
/*     */ 
/*     */ 
/* 313 */     StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[arrayOfCompositeData.length];
/*     */     
/* 315 */     for (int i = 0; i < arrayOfCompositeData.length; i++) {
/* 316 */       CompositeData localCompositeData = arrayOfCompositeData[i];
/* 317 */       arrayOfStackTraceElement[i] = StackTraceElementCompositeData.from(localCompositeData);
/*     */     }
/* 319 */     return arrayOfStackTraceElement;
/*     */   }
/*     */   
/*     */   public LockInfo lockInfo()
/*     */   {
/* 324 */     CompositeData localCompositeData = (CompositeData)this.cdata.get("lockInfo");
/* 325 */     return LockInfo.from(localCompositeData);
/*     */   }
/*     */   
/*     */   public MonitorInfo[] lockedMonitors()
/*     */   {
/* 330 */     CompositeData[] arrayOfCompositeData = (CompositeData[])this.cdata.get("lockedMonitors");
/*     */     
/*     */ 
/*     */ 
/* 334 */     MonitorInfo[] arrayOfMonitorInfo = new MonitorInfo[arrayOfCompositeData.length];
/*     */     
/* 336 */     for (int i = 0; i < arrayOfCompositeData.length; i++) {
/* 337 */       CompositeData localCompositeData = arrayOfCompositeData[i];
/* 338 */       arrayOfMonitorInfo[i] = MonitorInfo.from(localCompositeData);
/*     */     }
/* 340 */     return arrayOfMonitorInfo;
/*     */   }
/*     */   
/*     */   public LockInfo[] lockedSynchronizers()
/*     */   {
/* 345 */     CompositeData[] arrayOfCompositeData = (CompositeData[])this.cdata.get("lockedSynchronizers");
/*     */     
/*     */ 
/*     */ 
/* 349 */     LockInfo[] arrayOfLockInfo = new LockInfo[arrayOfCompositeData.length];
/* 350 */     for (int i = 0; i < arrayOfCompositeData.length; i++) {
/* 351 */       CompositeData localCompositeData = arrayOfCompositeData[i];
/* 352 */       arrayOfLockInfo[i] = LockInfo.from(localCompositeData);
/*     */     }
/* 354 */     return arrayOfLockInfo;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void validateCompositeData(CompositeData paramCompositeData)
/*     */   {
/* 362 */     if (paramCompositeData == null) {
/* 363 */       throw new NullPointerException("Null CompositeData");
/*     */     }
/*     */     
/* 366 */     CompositeType localCompositeType = paramCompositeData.getCompositeType();
/* 367 */     int i = 1;
/* 368 */     if (!isTypeMatched(threadInfoCompositeType, localCompositeType)) {
/* 369 */       i = 0;
/*     */       
/* 371 */       if (!isTypeMatched(threadInfoV5CompositeType, localCompositeType)) {
/* 372 */         throw new IllegalArgumentException("Unexpected composite type for ThreadInfo");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 378 */     CompositeData[] arrayOfCompositeData1 = (CompositeData[])paramCompositeData.get("stackTrace");
/* 379 */     if (arrayOfCompositeData1 == null) {
/* 380 */       throw new IllegalArgumentException("StackTraceElement[] is missing");
/*     */     }
/*     */     
/* 383 */     if (arrayOfCompositeData1.length > 0) {
/* 384 */       StackTraceElementCompositeData.validateCompositeData(arrayOfCompositeData1[0]);
/*     */     }
/*     */     
/*     */ 
/* 388 */     if (i != 0) {
/* 389 */       CompositeData localCompositeData = (CompositeData)paramCompositeData.get("lockInfo");
/* 390 */       if ((localCompositeData != null) && 
/* 391 */         (!isTypeMatched(lockInfoCompositeType, localCompositeData
/* 392 */         .getCompositeType()))) {
/* 393 */         throw new IllegalArgumentException("Unexpected composite type for \"lockInfo\" attribute.");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 399 */       CompositeData[] arrayOfCompositeData2 = (CompositeData[])paramCompositeData.get("lockedMonitors");
/* 400 */       if (arrayOfCompositeData2 == null) {
/* 401 */         throw new IllegalArgumentException("MonitorInfo[] is null");
/*     */       }
/* 403 */       if (arrayOfCompositeData2.length > 0) {
/* 404 */         MonitorInfoCompositeData.validateCompositeData(arrayOfCompositeData2[0]);
/*     */       }
/*     */       
/* 407 */       CompositeData[] arrayOfCompositeData3 = (CompositeData[])paramCompositeData.get("lockedSynchronizers");
/* 408 */       if (arrayOfCompositeData3 == null) {
/* 409 */         throw new IllegalArgumentException("LockInfo[] is null");
/*     */       }
/* 411 */       if ((arrayOfCompositeData3.length > 0) && 
/* 412 */         (!isTypeMatched(lockInfoCompositeType, arrayOfCompositeData3[0]
/* 413 */         .getCompositeType()))) {
/* 414 */         throw new IllegalArgumentException("Unexpected composite type for \"lockedSynchronizers\" attribute.");
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\ThreadInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
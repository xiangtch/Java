/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpOidRecord;
/*     */ import com.sun.jmx.snmp.SnmpOidTable;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.management.ThreadInfo;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import sun.management.snmp.jvmmib.JVM_MANAGEMENT_MIBOidTable;
/*     */ import sun.management.snmp.jvmmib.JvmThreadInstanceEntryMBean;
/*     */ import sun.management.snmp.util.MibLogger;
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
/*     */ public class JvmThreadInstanceEntryImpl
/*     */   implements JvmThreadInstanceEntryMBean, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 910173589985461347L;
/*     */   private final ThreadInfo info;
/*     */   private final Byte[] index;
/*     */   
/*     */   public static final class ThreadStateMap
/*     */   {
/*     */     public static final byte mask0 = 63;
/*     */     public static final byte mask1 = -128;
/*     */     
/*     */     public static final class Byte0
/*     */     {
/*     */       public static final byte inNative = -128;
/*     */       public static final byte suspended = 64;
/*     */       public static final byte newThread = 32;
/*     */       public static final byte runnable = 16;
/*     */       public static final byte blocked = 8;
/*     */       public static final byte terminated = 4;
/*     */       public static final byte waiting = 2;
/*     */       public static final byte timedWaiting = 1;
/*     */     }
/*     */     
/*     */     private static void setBit(byte[] paramArrayOfByte, int paramInt, byte paramByte)
/*     */     {
/*  84 */       paramArrayOfByte[paramInt] = ((byte)(paramArrayOfByte[paramInt] | paramByte));
/*     */     }
/*     */     
/*  87 */     public static void setNative(byte[] paramArrayOfByte) { setBit(paramArrayOfByte, 0, (byte)Byte.MIN_VALUE); }
/*     */     
/*     */ 
/*  90 */     public static void setSuspended(byte[] paramArrayOfByte) { setBit(paramArrayOfByte, 0, (byte)64); }
/*     */     
/*     */     public static void setState(byte[] paramArrayOfByte, Thread.State paramState) {
/*  93 */       switch (JvmThreadInstanceEntryImpl.1.$SwitchMap$java$lang$Thread$State[paramState.ordinal()]) {
/*     */       case 1: 
/*  95 */         setBit(paramArrayOfByte, 0, (byte)8);
/*  96 */         return;
/*     */       case 2: 
/*  98 */         setBit(paramArrayOfByte, 0, (byte)32);
/*  99 */         return;
/*     */       case 3: 
/* 101 */         setBit(paramArrayOfByte, 0, (byte)16);
/* 102 */         return;
/*     */       case 4: 
/* 104 */         setBit(paramArrayOfByte, 0, (byte)4);
/* 105 */         return;
/*     */       case 5: 
/* 107 */         setBit(paramArrayOfByte, 0, (byte)1);
/* 108 */         return;
/*     */       case 6: 
/* 110 */         setBit(paramArrayOfByte, 0, (byte)2);
/* 111 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */     public static void checkOther(byte[] paramArrayOfByte) {
/* 116 */       if (((paramArrayOfByte[0] & 0x3F) == 0) && ((paramArrayOfByte[1] & 0xFFFFFF80) == 0))
/*     */       {
/* 118 */         setBit(paramArrayOfByte, 1, (byte)Byte.MIN_VALUE); }
/*     */     }
/*     */     
/*     */     public static Byte[] getState(ThreadInfo paramThreadInfo) {
/* 122 */       byte[] arrayOfByte = { 0, 0 };
/*     */       try {
/* 124 */         Thread.State localState = paramThreadInfo.getThreadState();
/* 125 */         boolean bool1 = paramThreadInfo.isInNative();
/* 126 */         boolean bool2 = paramThreadInfo.isSuspended();
/* 127 */         JvmThreadInstanceEntryImpl.log.debug("getJvmThreadInstState", "[State=" + localState + ",isInNative=" + bool1 + ",isSuspended=" + bool2 + "]");
/*     */         
/*     */ 
/*     */ 
/* 131 */         setState(arrayOfByte, localState);
/* 132 */         if (bool1) setNative(arrayOfByte);
/* 133 */         if (bool2) setSuspended(arrayOfByte);
/* 134 */         checkOther(arrayOfByte);
/*     */       } catch (RuntimeException localRuntimeException) {
/* 136 */         arrayOfByte[0] = 0;
/* 137 */         arrayOfByte[1] = Byte.MIN_VALUE;
/* 138 */         JvmThreadInstanceEntryImpl.log.trace("getJvmThreadInstState", "Unexpected exception: " + localRuntimeException);
/*     */         
/* 140 */         JvmThreadInstanceEntryImpl.log.debug("getJvmThreadInstState", localRuntimeException);
/*     */       }
/* 142 */       Byte[] arrayOfByte1 = { new Byte(arrayOfByte[0]), new Byte(arrayOfByte[1]) };
/* 143 */       return arrayOfByte1;
/*     */     }
/*     */     
/*     */     public static final class Byte1 { public static final byte other = -128;
/*     */       public static final byte reserved10 = 64;
/*     */       public static final byte reserved11 = 32;
/*     */       public static final byte reserved12 = 16;
/*     */       public static final byte reserved13 = 8;
/*     */       public static final byte reserved14 = 4;
/*     */       public static final byte reserved15 = 2;
/*     */       public static final byte reserved16 = 1; } }
/*     */   
/* 155 */   public JvmThreadInstanceEntryImpl(ThreadInfo paramThreadInfo, Byte[] paramArrayOfByte) { this.info = paramThreadInfo;
/* 156 */     this.index = paramArrayOfByte;
/*     */   }
/*     */   
/*     */ 
/* 160 */   private static String jvmThreadInstIndexOid = null;
/*     */   
/*     */   public static String getJvmThreadInstIndexOid() throws SnmpStatusException {
/* 163 */     if (jvmThreadInstIndexOid == null) {
/* 164 */       JVM_MANAGEMENT_MIBOidTable localJVM_MANAGEMENT_MIBOidTable = new JVM_MANAGEMENT_MIBOidTable();
/*     */       
/* 166 */       SnmpOidRecord localSnmpOidRecord = localJVM_MANAGEMENT_MIBOidTable.resolveVarName("jvmThreadInstIndex");
/* 167 */       jvmThreadInstIndexOid = localSnmpOidRecord.getOid();
/*     */     }
/* 169 */     return jvmThreadInstIndexOid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getJvmThreadInstLockOwnerPtr()
/*     */     throws SnmpStatusException
/*     */   {
/* 178 */     long l = this.info.getLockOwnerId();
/*     */     
/* 180 */     if (l == -1L) {
/* 181 */       return new String("0.0");
/*     */     }
/* 183 */     SnmpOid localSnmpOid = JvmThreadInstanceTableMetaImpl.makeOid(l);
/*     */     
/* 185 */     return getJvmThreadInstIndexOid() + "." + localSnmpOid.toString();
/*     */   }
/*     */   
/*     */   private String validDisplayStringTC(String paramString) {
/* 189 */     return JVM_MANAGEMENT_MIB_IMPL.validDisplayStringTC(paramString);
/*     */   }
/*     */   
/*     */   private String validJavaObjectNameTC(String paramString) {
/* 193 */     return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(paramString);
/*     */   }
/*     */   
/*     */   private String validPathElementTC(String paramString) {
/* 197 */     return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getJvmThreadInstLockName()
/*     */     throws SnmpStatusException
/*     */   {
/* 204 */     return validJavaObjectNameTC(this.info.getLockName());
/*     */   }
/*     */   
/*     */ 
/*     */   public String getJvmThreadInstName()
/*     */     throws SnmpStatusException
/*     */   {
/* 211 */     return validJavaObjectNameTC(this.info.getThreadName());
/*     */   }
/*     */   
/*     */ 
/*     */   public Long getJvmThreadInstCpuTimeNs()
/*     */     throws SnmpStatusException
/*     */   {
/* 218 */     long l = 0L;
/* 219 */     ThreadMXBean localThreadMXBean = JvmThreadingImpl.getThreadMXBean();
/*     */     try
/*     */     {
/* 222 */       if (localThreadMXBean.isThreadCpuTimeSupported()) {
/* 223 */         l = localThreadMXBean.getThreadCpuTime(this.info.getThreadId());
/* 224 */         log.debug("getJvmThreadInstCpuTimeNs", "Cpu time ns : " + l);
/*     */         
/*     */ 
/* 227 */         if (l == -1L) l = 0L;
/*     */       }
/*     */     }
/*     */     catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
/* 231 */       log.debug("getJvmThreadInstCpuTimeNs", "Operation not supported: " + localUnsatisfiedLinkError);
/*     */     }
/*     */     
/* 234 */     return new Long(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public Long getJvmThreadInstBlockTimeMs()
/*     */     throws SnmpStatusException
/*     */   {
/* 241 */     long l = 0L;
/*     */     
/* 243 */     ThreadMXBean localThreadMXBean = JvmThreadingImpl.getThreadMXBean();
/*     */     
/* 245 */     if (localThreadMXBean.isThreadContentionMonitoringSupported()) {
/* 246 */       l = this.info.getBlockedTime();
/*     */       
/*     */ 
/* 249 */       if (l == -1L) l = 0L;
/*     */     }
/* 251 */     return new Long(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public Long getJvmThreadInstBlockCount()
/*     */     throws SnmpStatusException
/*     */   {
/* 258 */     return new Long(this.info.getBlockedCount());
/*     */   }
/*     */   
/*     */ 
/*     */   public Long getJvmThreadInstWaitTimeMs()
/*     */     throws SnmpStatusException
/*     */   {
/* 265 */     long l = 0L;
/*     */     
/* 267 */     ThreadMXBean localThreadMXBean = JvmThreadingImpl.getThreadMXBean();
/*     */     
/* 269 */     if (localThreadMXBean.isThreadContentionMonitoringSupported()) {
/* 270 */       l = this.info.getWaitedTime();
/*     */       
/*     */ 
/* 273 */       if (l == -1L) l = 0L;
/*     */     }
/* 275 */     return new Long(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public Long getJvmThreadInstWaitCount()
/*     */     throws SnmpStatusException
/*     */   {
/* 282 */     return new Long(this.info.getWaitedCount());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Byte[] getJvmThreadInstState()
/*     */     throws SnmpStatusException
/*     */   {
/* 290 */     return ThreadStateMap.getState(this.info);
/*     */   }
/*     */   
/*     */ 
/*     */   public Long getJvmThreadInstId()
/*     */     throws SnmpStatusException
/*     */   {
/* 297 */     return new Long(this.info.getThreadId());
/*     */   }
/*     */   
/*     */ 
/*     */   public Byte[] getJvmThreadInstIndex()
/*     */     throws SnmpStatusException
/*     */   {
/* 304 */     return this.index;
/*     */   }
/*     */   
/*     */ 
/*     */   private String getJvmThreadInstStackTrace()
/*     */     throws SnmpStatusException
/*     */   {
/* 311 */     StackTraceElement[] arrayOfStackTraceElement = this.info.getStackTrace();
/*     */     
/*     */ 
/* 314 */     StringBuffer localStringBuffer = new StringBuffer();
/* 315 */     int i = arrayOfStackTraceElement.length;
/* 316 */     log.debug("getJvmThreadInstStackTrace", "Stack size : " + i);
/* 317 */     for (int j = 0; j < i; j++) {
/* 318 */       log.debug("getJvmThreadInstStackTrace", "Append " + arrayOfStackTraceElement[j]
/* 319 */         .toString());
/* 320 */       localStringBuffer.append(arrayOfStackTraceElement[j].toString());
/*     */       
/* 322 */       if (j < i) {
/* 323 */         localStringBuffer.append("\n");
/*     */       }
/*     */     }
/* 326 */     return validPathElementTC(localStringBuffer.toString()); }
/*     */   
/* 328 */   static final MibLogger log = new MibLogger(JvmThreadInstanceEntryImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmThreadInstanceEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import java.io.Serializable;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.OperatingSystemMXBean;
/*     */ import javax.management.MBeanServer;
/*     */ import sun.management.snmp.jvmmib.JvmOSMBean;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JvmOSImpl
/*     */   implements JvmOSMBean, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 1839834731763310809L;
/*     */   
/*     */   public JvmOSImpl(SnmpMib paramSnmpMib) {}
/*     */   
/*     */   public JvmOSImpl(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {}
/*     */   
/*     */   static OperatingSystemMXBean getOSMBean()
/*     */   {
/*  70 */     return ManagementFactory.getOperatingSystemMXBean();
/*     */   }
/*     */   
/*     */   private static String validDisplayStringTC(String paramString) {
/*  74 */     return JVM_MANAGEMENT_MIB_IMPL.validDisplayStringTC(paramString);
/*     */   }
/*     */   
/*     */   private static String validJavaObjectNameTC(String paramString) {
/*  78 */     return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */   public Integer getJvmOSProcessorCount()
/*     */     throws SnmpStatusException
/*     */   {
/*  85 */     return new Integer(getOSMBean().getAvailableProcessors());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getJvmOSVersion()
/*     */     throws SnmpStatusException
/*     */   {
/*  93 */     return validDisplayStringTC(getOSMBean().getVersion());
/*     */   }
/*     */   
/*     */ 
/*     */   public String getJvmOSArch()
/*     */     throws SnmpStatusException
/*     */   {
/* 100 */     return validDisplayStringTC(getOSMBean().getArch());
/*     */   }
/*     */   
/*     */ 
/*     */   public String getJvmOSName()
/*     */     throws SnmpStatusException
/*     */   {
/* 107 */     return validJavaObjectNameTC(getOSMBean().getName());
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmOSImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
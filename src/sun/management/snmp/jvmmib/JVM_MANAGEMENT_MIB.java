/*     */ package sun.management.snmp.jvmmib;
/*     */ 
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibTable;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.io.Serializable;
/*     */ import java.util.Hashtable;
/*     */ import javax.management.InstanceAlreadyExistsException;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
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
/*     */ public abstract class JVM_MANAGEMENT_MIB
/*     */   extends SnmpMib
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 6895037919735816732L;
/*     */   
/*     */   public JVM_MANAGEMENT_MIB()
/*     */   {
/*  61 */     this.mibName = "JVM_MANAGEMENT_MIB";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init()
/*     */     throws IllegalAccessException
/*     */   {
/*  70 */     if (this.isInitialized == true) {
/*  71 */       return;
/*     */     }
/*     */     try
/*     */     {
/*  75 */       populate(null, null);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/*  77 */       throw localIllegalAccessException;
/*     */     } catch (RuntimeException localRuntimeException) {
/*  79 */       throw localRuntimeException;
/*     */     } catch (Exception localException) {
/*  81 */       throw new Error(localException.getMessage());
/*     */     }
/*     */     
/*  84 */     this.isInitialized = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
/*     */     throws Exception
/*     */   {
/*  94 */     if (this.isInitialized == true) {
/*  95 */       throw new InstanceAlreadyExistsException();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 100 */     this.server = paramMBeanServer;
/*     */     
/* 102 */     populate(paramMBeanServer, paramObjectName);
/*     */     
/* 104 */     this.isInitialized = true;
/* 105 */     return paramObjectName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void populate(MBeanServer paramMBeanServer, ObjectName paramObjectName)
/*     */     throws Exception
/*     */   {
/* 115 */     if (this.isInitialized == true) {
/* 116 */       return;
/*     */     }
/*     */     
/* 119 */     if (this.objectserver == null) {
/* 120 */       this.objectserver = new SnmpStandardObjectServer();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 126 */     initJvmOS(paramMBeanServer);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 132 */     initJvmCompilation(paramMBeanServer);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 138 */     initJvmRuntime(paramMBeanServer);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */     initJvmThreading(paramMBeanServer);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 150 */     initJvmMemory(paramMBeanServer);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 156 */     initJvmClassLoading(paramMBeanServer);
/*     */     
/* 158 */     this.isInitialized = true;
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
/*     */   protected void initJvmOS(MBeanServer paramMBeanServer)
/*     */     throws Exception
/*     */   {
/* 180 */     String str = getGroupOid("JvmOS", "1.3.6.1.4.1.42.2.145.3.163.1.1.6");
/* 181 */     ObjectName localObjectName = null;
/* 182 */     if (paramMBeanServer != null) {
/* 183 */       localObjectName = getGroupObjectName("JvmOS", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmOS");
/*     */     }
/* 185 */     JvmOSMeta localJvmOSMeta = createJvmOSMetaNode("JvmOS", str, localObjectName, paramMBeanServer);
/* 186 */     if (localJvmOSMeta != null) {
/* 187 */       localJvmOSMeta.registerTableNodes(this, paramMBeanServer);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 193 */       JvmOSMBean localJvmOSMBean = (JvmOSMBean)createJvmOSMBean("JvmOS", str, localObjectName, paramMBeanServer);
/* 194 */       localJvmOSMeta.setInstance(localJvmOSMBean);
/* 195 */       registerGroupNode("JvmOS", str, localObjectName, localJvmOSMeta, localJvmOSMBean, paramMBeanServer);
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
/*     */   protected JvmOSMeta createJvmOSMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
/*     */   {
/* 217 */     return new JvmOSMeta(this, this.objectserver);
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
/*     */   protected abstract Object createJvmOSMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
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
/*     */   protected void initJvmCompilation(MBeanServer paramMBeanServer)
/*     */     throws Exception
/*     */   {
/* 261 */     String str = getGroupOid("JvmCompilation", "1.3.6.1.4.1.42.2.145.3.163.1.1.5");
/* 262 */     ObjectName localObjectName = null;
/* 263 */     if (paramMBeanServer != null) {
/* 264 */       localObjectName = getGroupObjectName("JvmCompilation", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmCompilation");
/*     */     }
/* 266 */     JvmCompilationMeta localJvmCompilationMeta = createJvmCompilationMetaNode("JvmCompilation", str, localObjectName, paramMBeanServer);
/* 267 */     if (localJvmCompilationMeta != null) {
/* 268 */       localJvmCompilationMeta.registerTableNodes(this, paramMBeanServer);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 274 */       JvmCompilationMBean localJvmCompilationMBean = (JvmCompilationMBean)createJvmCompilationMBean("JvmCompilation", str, localObjectName, paramMBeanServer);
/* 275 */       localJvmCompilationMeta.setInstance(localJvmCompilationMBean);
/* 276 */       registerGroupNode("JvmCompilation", str, localObjectName, localJvmCompilationMeta, localJvmCompilationMBean, paramMBeanServer);
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
/*     */   protected JvmCompilationMeta createJvmCompilationMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
/*     */   {
/* 298 */     return new JvmCompilationMeta(this, this.objectserver);
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
/*     */   protected abstract Object createJvmCompilationMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
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
/*     */   protected void initJvmRuntime(MBeanServer paramMBeanServer)
/*     */     throws Exception
/*     */   {
/* 342 */     String str = getGroupOid("JvmRuntime", "1.3.6.1.4.1.42.2.145.3.163.1.1.4");
/* 343 */     ObjectName localObjectName = null;
/* 344 */     if (paramMBeanServer != null) {
/* 345 */       localObjectName = getGroupObjectName("JvmRuntime", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmRuntime");
/*     */     }
/* 347 */     JvmRuntimeMeta localJvmRuntimeMeta = createJvmRuntimeMetaNode("JvmRuntime", str, localObjectName, paramMBeanServer);
/* 348 */     if (localJvmRuntimeMeta != null) {
/* 349 */       localJvmRuntimeMeta.registerTableNodes(this, paramMBeanServer);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 355 */       JvmRuntimeMBean localJvmRuntimeMBean = (JvmRuntimeMBean)createJvmRuntimeMBean("JvmRuntime", str, localObjectName, paramMBeanServer);
/* 356 */       localJvmRuntimeMeta.setInstance(localJvmRuntimeMBean);
/* 357 */       registerGroupNode("JvmRuntime", str, localObjectName, localJvmRuntimeMeta, localJvmRuntimeMBean, paramMBeanServer);
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
/*     */   protected JvmRuntimeMeta createJvmRuntimeMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
/*     */   {
/* 379 */     return new JvmRuntimeMeta(this, this.objectserver);
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
/*     */   protected abstract Object createJvmRuntimeMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
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
/*     */   protected void initJvmThreading(MBeanServer paramMBeanServer)
/*     */     throws Exception
/*     */   {
/* 423 */     String str = getGroupOid("JvmThreading", "1.3.6.1.4.1.42.2.145.3.163.1.1.3");
/* 424 */     ObjectName localObjectName = null;
/* 425 */     if (paramMBeanServer != null) {
/* 426 */       localObjectName = getGroupObjectName("JvmThreading", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmThreading");
/*     */     }
/* 428 */     JvmThreadingMeta localJvmThreadingMeta = createJvmThreadingMetaNode("JvmThreading", str, localObjectName, paramMBeanServer);
/* 429 */     if (localJvmThreadingMeta != null) {
/* 430 */       localJvmThreadingMeta.registerTableNodes(this, paramMBeanServer);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 436 */       JvmThreadingMBean localJvmThreadingMBean = (JvmThreadingMBean)createJvmThreadingMBean("JvmThreading", str, localObjectName, paramMBeanServer);
/* 437 */       localJvmThreadingMeta.setInstance(localJvmThreadingMBean);
/* 438 */       registerGroupNode("JvmThreading", str, localObjectName, localJvmThreadingMeta, localJvmThreadingMBean, paramMBeanServer);
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
/*     */   protected JvmThreadingMeta createJvmThreadingMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
/*     */   {
/* 460 */     return new JvmThreadingMeta(this, this.objectserver);
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
/*     */   protected abstract Object createJvmThreadingMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
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
/*     */   protected void initJvmMemory(MBeanServer paramMBeanServer)
/*     */     throws Exception
/*     */   {
/* 504 */     String str = getGroupOid("JvmMemory", "1.3.6.1.4.1.42.2.145.3.163.1.1.2");
/* 505 */     ObjectName localObjectName = null;
/* 506 */     if (paramMBeanServer != null) {
/* 507 */       localObjectName = getGroupObjectName("JvmMemory", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmMemory");
/*     */     }
/* 509 */     JvmMemoryMeta localJvmMemoryMeta = createJvmMemoryMetaNode("JvmMemory", str, localObjectName, paramMBeanServer);
/* 510 */     if (localJvmMemoryMeta != null) {
/* 511 */       localJvmMemoryMeta.registerTableNodes(this, paramMBeanServer);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 517 */       JvmMemoryMBean localJvmMemoryMBean = (JvmMemoryMBean)createJvmMemoryMBean("JvmMemory", str, localObjectName, paramMBeanServer);
/* 518 */       localJvmMemoryMeta.setInstance(localJvmMemoryMBean);
/* 519 */       registerGroupNode("JvmMemory", str, localObjectName, localJvmMemoryMeta, localJvmMemoryMBean, paramMBeanServer);
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
/*     */   protected JvmMemoryMeta createJvmMemoryMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
/*     */   {
/* 541 */     return new JvmMemoryMeta(this, this.objectserver);
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
/*     */   protected abstract Object createJvmMemoryMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
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
/*     */   protected void initJvmClassLoading(MBeanServer paramMBeanServer)
/*     */     throws Exception
/*     */   {
/* 585 */     String str = getGroupOid("JvmClassLoading", "1.3.6.1.4.1.42.2.145.3.163.1.1.1");
/* 586 */     ObjectName localObjectName = null;
/* 587 */     if (paramMBeanServer != null) {
/* 588 */       localObjectName = getGroupObjectName("JvmClassLoading", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmClassLoading");
/*     */     }
/* 590 */     JvmClassLoadingMeta localJvmClassLoadingMeta = createJvmClassLoadingMetaNode("JvmClassLoading", str, localObjectName, paramMBeanServer);
/* 591 */     if (localJvmClassLoadingMeta != null) {
/* 592 */       localJvmClassLoadingMeta.registerTableNodes(this, paramMBeanServer);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 598 */       JvmClassLoadingMBean localJvmClassLoadingMBean = (JvmClassLoadingMBean)createJvmClassLoadingMBean("JvmClassLoading", str, localObjectName, paramMBeanServer);
/* 599 */       localJvmClassLoadingMeta.setInstance(localJvmClassLoadingMBean);
/* 600 */       registerGroupNode("JvmClassLoading", str, localObjectName, localJvmClassLoadingMeta, localJvmClassLoadingMBean, paramMBeanServer);
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
/*     */   protected JvmClassLoadingMeta createJvmClassLoadingMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
/*     */   {
/* 622 */     return new JvmClassLoadingMeta(this, this.objectserver);
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
/*     */   protected abstract Object createJvmClassLoadingMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
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
/*     */   public void registerTableMeta(String paramString, SnmpMibTable paramSnmpMibTable)
/*     */   {
/* 656 */     if (this.metadatas == null) return;
/* 657 */     if (paramString == null) return;
/* 658 */     this.metadatas.put(paramString, paramSnmpMibTable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SnmpMibTable getRegisteredTableMeta(String paramString)
/*     */   {
/* 670 */     if (this.metadatas == null) return null;
/* 671 */     if (paramString == null) return null;
/* 672 */     return (SnmpMibTable)this.metadatas.get(paramString);
/*     */   }
/*     */   
/*     */   public SnmpStandardObjectServer getStandardObjectServer() {
/* 676 */     if (this.objectserver == null)
/* 677 */       this.objectserver = new SnmpStandardObjectServer();
/* 678 */     return this.objectserver;
/*     */   }
/*     */   
/* 681 */   private boolean isInitialized = false;
/*     */   
/*     */   protected SnmpStandardObjectServer objectserver;
/*     */   
/* 685 */   protected final Hashtable<String, SnmpMibTable> metadatas = new Hashtable();
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JVM_MANAGEMENT_MIB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.management.snmp.jvmmib;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpCounter;
/*     */ import com.sun.jmx.snmp.SnmpCounter64;
/*     */ import com.sun.jmx.snmp.SnmpGauge;
/*     */ import com.sun.jmx.snmp.SnmpInt;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.SnmpValue;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibGroup;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibTable;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.io.Serializable;
/*     */ import javax.management.MBeanServer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JvmThreadingMeta
/*     */   extends SnmpMibGroup
/*     */   implements Serializable, SnmpStandardMetaServer
/*     */ {
/*     */   static final long serialVersionUID = 5223833578005322854L;
/*     */   protected JvmThreadingMBean node;
/*     */   
/*     */   public JvmThreadingMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  78 */     this.objectserver = paramSnmpStandardObjectServer;
/*     */     try {
/*  80 */       registerObject(6L);
/*  81 */       registerObject(5L);
/*  82 */       registerObject(4L);
/*  83 */       registerObject(3L);
/*  84 */       registerObject(2L);
/*  85 */       registerObject(1L);
/*  86 */       registerObject(10L);
/*  87 */       registerObject(7L);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/*  89 */       throw new RuntimeException(localIllegalAccessException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue get(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/*  98 */     switch ((int)paramLong) {
/*     */     case 6: 
/* 100 */       return new SnmpInt(this.node.getJvmThreadCpuTimeMonitoring());
/*     */     
/*     */     case 5: 
/* 103 */       return new SnmpInt(this.node.getJvmThreadContentionMonitoring());
/*     */     
/*     */     case 4: 
/* 106 */       return new SnmpCounter64(this.node.getJvmThreadTotalStartedCount());
/*     */     
/*     */     case 3: 
/* 109 */       return new SnmpCounter(this.node.getJvmThreadPeakCount());
/*     */     
/*     */     case 2: 
/* 112 */       return new SnmpGauge(this.node.getJvmThreadDaemonCount());
/*     */     
/*     */     case 1: 
/* 115 */       return new SnmpGauge(this.node.getJvmThreadCount());
/*     */     
/*     */     case 10: 
/* 118 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 7: 
/* 122 */       return new SnmpCounter64(this.node.getJvmThreadPeakCountReset());
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 127 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 135 */     switch ((int)paramLong) {
/*     */     case 6: 
/* 137 */       if ((paramSnmpValue instanceof SnmpInt)) {
/*     */         try {
/* 139 */           this.node.setJvmThreadCpuTimeMonitoring(new EnumJvmThreadCpuTimeMonitoring(((SnmpInt)paramSnmpValue).toInteger()));
/*     */         } catch (IllegalArgumentException localIllegalArgumentException1) {
/* 141 */           throw new SnmpStatusException(10);
/*     */         }
/* 143 */         return new SnmpInt(this.node.getJvmThreadCpuTimeMonitoring());
/*     */       }
/* 145 */       throw new SnmpStatusException(7);
/*     */     
/*     */ 
/*     */     case 5: 
/* 149 */       if ((paramSnmpValue instanceof SnmpInt)) {
/*     */         try {
/* 151 */           this.node.setJvmThreadContentionMonitoring(new EnumJvmThreadContentionMonitoring(((SnmpInt)paramSnmpValue).toInteger()));
/*     */         } catch (IllegalArgumentException localIllegalArgumentException2) {
/* 153 */           throw new SnmpStatusException(10);
/*     */         }
/* 155 */         return new SnmpInt(this.node.getJvmThreadContentionMonitoring());
/*     */       }
/* 157 */       throw new SnmpStatusException(7);
/*     */     
/*     */ 
/*     */     case 4: 
/* 161 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 164 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 167 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 170 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 10: 
/* 173 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 7: 
/* 177 */       if ((paramSnmpValue instanceof SnmpCounter64)) {
/* 178 */         this.node.setJvmThreadPeakCountReset(((SnmpCounter64)paramSnmpValue).toLong());
/* 179 */         return new SnmpCounter64(this.node.getJvmThreadPeakCountReset());
/*     */       }
/* 181 */       throw new SnmpStatusException(7);
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 187 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 195 */     switch ((int)paramLong) {
/*     */     case 6: 
/* 197 */       if ((paramSnmpValue instanceof SnmpInt)) {
/*     */         try {
/* 199 */           this.node.checkJvmThreadCpuTimeMonitoring(new EnumJvmThreadCpuTimeMonitoring(((SnmpInt)paramSnmpValue).toInteger()));
/*     */         } catch (IllegalArgumentException localIllegalArgumentException1) {
/* 201 */           throw new SnmpStatusException(10);
/*     */         }
/*     */       } else {
/* 204 */         throw new SnmpStatusException(7);
/*     */       }
/*     */       
/*     */       break;
/*     */     case 5: 
/* 209 */       if ((paramSnmpValue instanceof SnmpInt)) {
/*     */         try {
/* 211 */           this.node.checkJvmThreadContentionMonitoring(new EnumJvmThreadContentionMonitoring(((SnmpInt)paramSnmpValue).toInteger()));
/*     */         } catch (IllegalArgumentException localIllegalArgumentException2) {
/* 213 */           throw new SnmpStatusException(10);
/*     */         }
/*     */       } else {
/* 216 */         throw new SnmpStatusException(7);
/*     */       }
/*     */       
/*     */       break;
/*     */     case 4: 
/* 221 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 224 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 227 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 230 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 10: 
/* 233 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 7: 
/* 237 */       if ((paramSnmpValue instanceof SnmpCounter64)) {
/* 238 */         this.node.checkJvmThreadPeakCountReset(((SnmpCounter64)paramSnmpValue).toLong());
/*     */       } else {
/* 240 */         throw new SnmpStatusException(7);
/*     */       }
/*     */       break;
/*     */     case 8: case 9: 
/*     */     default: 
/* 245 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setInstance(JvmThreadingMBean paramJvmThreadingMBean)
/*     */   {
/* 253 */     this.node = paramJvmThreadingMBean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 266 */     this.objectserver.get(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 279 */     this.objectserver.set(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 292 */     this.objectserver.check(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVariable(long paramLong)
/*     */   {
/* 300 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 6: 
/*     */     case 7: 
/* 308 */       return true;
/*     */     }
/*     */     
/*     */     
/* 312 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadable(long paramLong)
/*     */   {
/* 320 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 6: 
/*     */     case 7: 
/* 328 */       return true;
/*     */     }
/*     */     
/*     */     
/* 332 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean skipVariable(long paramLong, Object paramObject, int paramInt)
/*     */   {
/* 344 */     switch ((int)paramLong) {
/*     */     case 4: 
/*     */     case 7: 
/* 347 */       if (paramInt == 0) { return true;
/*     */       }
/*     */       break;
/*     */     }
/*     */     
/* 352 */     return super.skipVariable(paramLong, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttributeName(long paramLong)
/*     */     throws SnmpStatusException
/*     */   {
/* 360 */     switch ((int)paramLong) {
/*     */     case 6: 
/* 362 */       return "JvmThreadCpuTimeMonitoring";
/*     */     
/*     */     case 5: 
/* 365 */       return "JvmThreadContentionMonitoring";
/*     */     
/*     */     case 4: 
/* 368 */       return "JvmThreadTotalStartedCount";
/*     */     
/*     */     case 3: 
/* 371 */       return "JvmThreadPeakCount";
/*     */     
/*     */     case 2: 
/* 374 */       return "JvmThreadDaemonCount";
/*     */     
/*     */     case 1: 
/* 377 */       return "JvmThreadCount";
/*     */     
/*     */     case 10: 
/* 380 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 7: 
/* 384 */       return "JvmThreadPeakCountReset";
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 389 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTable(long paramLong)
/*     */   {
/* 397 */     switch ((int)paramLong) {
/*     */     case 10: 
/* 399 */       return true;
/*     */     }
/*     */     
/*     */     
/* 403 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SnmpMibTable getTable(long paramLong)
/*     */   {
/* 411 */     switch ((int)paramLong) {
/*     */     case 10: 
/* 413 */       return this.tableJvmThreadInstanceTable;
/*     */     }
/*     */     
/*     */     
/* 417 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void registerTableNodes(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/* 424 */     this.tableJvmThreadInstanceTable = createJvmThreadInstanceTableMetaNode("JvmThreadInstanceTable", "JvmThreading", paramSnmpMib, paramMBeanServer);
/* 425 */     if (this.tableJvmThreadInstanceTable != null) {
/* 426 */       this.tableJvmThreadInstanceTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
/* 427 */       paramSnmpMib.registerTableMeta("JvmThreadInstanceTable", this.tableJvmThreadInstanceTable);
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
/*     */   protected JvmThreadInstanceTableMeta createJvmThreadInstanceTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/* 449 */     return new JvmThreadInstanceTableMeta(paramSnmpMib, this.objectserver);
/*     */   }
/*     */   
/*     */ 
/* 453 */   protected SnmpStandardObjectServer objectserver = null;
/* 454 */   protected JvmThreadInstanceTableMeta tableJvmThreadInstanceTable = null;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmThreadingMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
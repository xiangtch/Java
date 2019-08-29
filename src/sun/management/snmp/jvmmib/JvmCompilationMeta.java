/*     */ package sun.management.snmp.jvmmib;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpCounter64;
/*     */ import com.sun.jmx.snmp.SnmpInt;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.SnmpString;
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
/*     */ 
/*     */ public class JvmCompilationMeta
/*     */   extends SnmpMibGroup
/*     */   implements Serializable, SnmpStandardMetaServer
/*     */ {
/*     */   static final long serialVersionUID = -95492874115033638L;
/*     */   protected JvmCompilationMBean node;
/*     */   
/*     */   public JvmCompilationMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  78 */     this.objectserver = paramSnmpStandardObjectServer;
/*     */     try {
/*  80 */       registerObject(3L);
/*  81 */       registerObject(2L);
/*  82 */       registerObject(1L);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/*  84 */       throw new RuntimeException(localIllegalAccessException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue get(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/*  93 */     switch ((int)paramLong) {
/*     */     case 3: 
/*  95 */       return new SnmpInt(this.node.getJvmJITCompilerTimeMonitoring());
/*     */     
/*     */     case 2: 
/*  98 */       return new SnmpCounter64(this.node.getJvmJITCompilerTimeMs());
/*     */     
/*     */     case 1: 
/* 101 */       return new SnmpString(this.node.getJvmJITCompilerName());
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 106 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 114 */     switch ((int)paramLong) {
/*     */     case 3: 
/* 116 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 119 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 122 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 127 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 135 */     switch ((int)paramLong) {
/*     */     case 3: 
/* 137 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 140 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 143 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/* 146 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setInstance(JvmCompilationMBean paramJvmCompilationMBean)
/*     */   {
/* 154 */     this.node = paramJvmCompilationMBean;
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
/* 167 */     this.objectserver.get(this, paramSnmpMibSubRequest, paramInt);
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
/* 180 */     this.objectserver.set(this, paramSnmpMibSubRequest, paramInt);
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
/* 193 */     this.objectserver.check(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVariable(long paramLong)
/*     */   {
/* 201 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/* 205 */       return true;
/*     */     }
/*     */     
/*     */     
/* 209 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadable(long paramLong)
/*     */   {
/* 217 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/* 221 */       return true;
/*     */     }
/*     */     
/*     */     
/* 225 */     return false;
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
/* 237 */     switch ((int)paramLong) {
/*     */     case 2: 
/* 239 */       if (paramInt == 0) { return true;
/*     */       }
/*     */       break;
/*     */     }
/*     */     
/* 244 */     return super.skipVariable(paramLong, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttributeName(long paramLong)
/*     */     throws SnmpStatusException
/*     */   {
/* 252 */     switch ((int)paramLong) {
/*     */     case 3: 
/* 254 */       return "JvmJITCompilerTimeMonitoring";
/*     */     
/*     */     case 2: 
/* 257 */       return "JvmJITCompilerTimeMs";
/*     */     
/*     */     case 1: 
/* 260 */       return "JvmJITCompilerName";
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 265 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTable(long paramLong)
/*     */   {
/* 273 */     switch ((int)paramLong)
/*     */     {
/*     */     }
/*     */     
/* 277 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpMibTable getTable(long paramLong)
/*     */   {
/* 284 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 294 */   protected SnmpStandardObjectServer objectserver = null;
/*     */   
/*     */   public void registerTableNodes(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmCompilationMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
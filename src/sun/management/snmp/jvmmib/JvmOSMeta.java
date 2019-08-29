/*     */ package sun.management.snmp.jvmmib;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ public class JvmOSMeta
/*     */   extends SnmpMibGroup
/*     */   implements Serializable, SnmpStandardMetaServer
/*     */ {
/*     */   static final long serialVersionUID = -2024138733580127133L;
/*     */   protected JvmOSMBean node;
/*     */   
/*     */   public JvmOSMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  79 */     this.objectserver = paramSnmpStandardObjectServer;
/*     */     try {
/*  81 */       registerObject(4L);
/*  82 */       registerObject(3L);
/*  83 */       registerObject(2L);
/*  84 */       registerObject(1L);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/*  86 */       throw new RuntimeException(localIllegalAccessException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue get(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/*  95 */     switch ((int)paramLong) {
/*     */     case 4: 
/*  97 */       return new SnmpInt(this.node.getJvmOSProcessorCount());
/*     */     
/*     */     case 3: 
/* 100 */       return new SnmpString(this.node.getJvmOSVersion());
/*     */     
/*     */     case 2: 
/* 103 */       return new SnmpString(this.node.getJvmOSArch());
/*     */     
/*     */     case 1: 
/* 106 */       return new SnmpString(this.node.getJvmOSName());
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 111 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 119 */     switch ((int)paramLong) {
/*     */     case 4: 
/* 121 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 124 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 127 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 130 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 135 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 143 */     switch ((int)paramLong) {
/*     */     case 4: 
/* 145 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 148 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 151 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 154 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/* 157 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setInstance(JvmOSMBean paramJvmOSMBean)
/*     */   {
/* 165 */     this.node = paramJvmOSMBean;
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
/* 178 */     this.objectserver.get(this, paramSnmpMibSubRequest, paramInt);
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
/* 191 */     this.objectserver.set(this, paramSnmpMibSubRequest, paramInt);
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
/* 204 */     this.objectserver.check(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVariable(long paramLong)
/*     */   {
/* 212 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/* 217 */       return true;
/*     */     }
/*     */     
/*     */     
/* 221 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadable(long paramLong)
/*     */   {
/* 229 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/* 234 */       return true;
/*     */     }
/*     */     
/*     */     
/* 238 */     return false;
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
/* 250 */     return super.skipVariable(paramLong, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttributeName(long paramLong)
/*     */     throws SnmpStatusException
/*     */   {
/* 258 */     switch ((int)paramLong) {
/*     */     case 4: 
/* 260 */       return "JvmOSProcessorCount";
/*     */     
/*     */     case 3: 
/* 263 */       return "JvmOSVersion";
/*     */     
/*     */     case 2: 
/* 266 */       return "JvmOSArch";
/*     */     
/*     */     case 1: 
/* 269 */       return "JvmOSName";
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 274 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTable(long paramLong)
/*     */   {
/* 282 */     switch ((int)paramLong)
/*     */     {
/*     */     }
/*     */     
/* 286 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpMibTable getTable(long paramLong)
/*     */   {
/* 293 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 303 */   protected SnmpStandardObjectServer objectserver = null;
/*     */   
/*     */   public void registerTableNodes(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmOSMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.management.snmp.jvmmib;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpInt;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.SnmpString;
/*     */ import com.sun.jmx.snmp.SnmpValue;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibEntry;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibNode;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JvmMemManagerEntryMeta
/*     */   extends SnmpMibEntry
/*     */   implements Serializable, SnmpStandardMetaServer
/*     */ {
/*     */   static final long serialVersionUID = 8166956416408970453L;
/*     */   protected JvmMemManagerEntryMBean node;
/*     */   
/*     */   public JvmMemManagerEntryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  79 */     this.objectserver = paramSnmpStandardObjectServer;
/*  80 */     this.varList = new int[2];
/*  81 */     this.varList[0] = 3;
/*  82 */     this.varList[1] = 2;
/*  83 */     SnmpMibNode.sort(this.varList);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue get(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/*  91 */     switch ((int)paramLong) {
/*     */     case 3: 
/*  93 */       return new SnmpInt(this.node.getJvmMemManagerState());
/*     */     
/*     */     case 2: 
/*  96 */       return new SnmpString(this.node.getJvmMemManagerName());
/*     */     
/*     */     case 1: 
/*  99 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */     
/* 103 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 111 */     switch ((int)paramLong) {
/*     */     case 3: 
/* 113 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 116 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 119 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 124 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 132 */     switch ((int)paramLong) {
/*     */     case 3: 
/* 134 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 137 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 140 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/* 143 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setInstance(JvmMemManagerEntryMBean paramJvmMemManagerEntryMBean)
/*     */   {
/* 151 */     this.node = paramJvmMemManagerEntryMBean;
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
/* 164 */     this.objectserver.get(this, paramSnmpMibSubRequest, paramInt);
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
/* 177 */     this.objectserver.set(this, paramSnmpMibSubRequest, paramInt);
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
/* 190 */     this.objectserver.check(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVariable(long paramLong)
/*     */   {
/* 198 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/* 202 */       return true;
/*     */     }
/*     */     
/*     */     
/* 206 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadable(long paramLong)
/*     */   {
/* 214 */     switch ((int)paramLong) {
/*     */     case 2: 
/*     */     case 3: 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean skipVariable(long paramLong, Object paramObject, int paramInt)
/*     */   {
/* 233 */     switch ((int)paramLong) {
/*     */     case 1: 
/* 235 */       return true;
/*     */     }
/*     */     
/*     */     
/* 239 */     return super.skipVariable(paramLong, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttributeName(long paramLong)
/*     */     throws SnmpStatusException
/*     */   {
/* 247 */     switch ((int)paramLong) {
/*     */     case 3: 
/* 249 */       return "JvmMemManagerState";
/*     */     
/*     */     case 2: 
/* 252 */       return "JvmMemManagerName";
/*     */     
/*     */     case 1: 
/* 255 */       return "JvmMemManagerIndex";
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 260 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/* 264 */   protected SnmpStandardObjectServer objectserver = null;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmMemManagerEntryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.management.snmp.jvmmib;
/*     */ 
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
/*     */ 
/*     */ public class JvmMemMgrPoolRelEntryMeta
/*     */   extends SnmpMibEntry
/*     */   implements Serializable, SnmpStandardMetaServer
/*     */ {
/*     */   static final long serialVersionUID = 7414270971113459798L;
/*     */   protected JvmMemMgrPoolRelEntryMBean node;
/*     */   
/*     */   public JvmMemMgrPoolRelEntryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
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
/*  93 */       return new SnmpString(this.node.getJvmMemMgrRelPoolName());
/*     */     
/*     */     case 2: 
/*  96 */       return new SnmpString(this.node.getJvmMemMgrRelManagerName());
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 101 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 109 */     switch ((int)paramLong) {
/*     */     case 3: 
/* 111 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 114 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 119 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 127 */     switch ((int)paramLong) {
/*     */     case 3: 
/* 129 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 132 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/* 135 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setInstance(JvmMemMgrPoolRelEntryMBean paramJvmMemMgrPoolRelEntryMBean)
/*     */   {
/* 143 */     this.node = paramJvmMemMgrPoolRelEntryMBean;
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
/* 156 */     this.objectserver.get(this, paramSnmpMibSubRequest, paramInt);
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
/* 169 */     this.objectserver.set(this, paramSnmpMibSubRequest, paramInt);
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
/* 182 */     this.objectserver.check(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVariable(long paramLong)
/*     */   {
/* 190 */     switch ((int)paramLong) {
/*     */     case 2: 
/*     */     case 3: 
/* 193 */       return true;
/*     */     }
/*     */     
/*     */     
/* 197 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadable(long paramLong)
/*     */   {
/* 205 */     switch ((int)paramLong) {
/*     */     case 2: 
/*     */     case 3: 
/* 208 */       return true;
/*     */     }
/*     */     
/*     */     
/* 212 */     return false;
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
/* 224 */     return super.skipVariable(paramLong, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttributeName(long paramLong)
/*     */     throws SnmpStatusException
/*     */   {
/* 232 */     switch ((int)paramLong) {
/*     */     case 3: 
/* 234 */       return "JvmMemMgrRelPoolName";
/*     */     
/*     */     case 2: 
/* 237 */       return "JvmMemMgrRelManagerName";
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 242 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/* 246 */   protected SnmpStandardObjectServer objectserver = null;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmMemMgrPoolRelEntryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
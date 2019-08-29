/*     */ package sun.management.snmp.jvmmib;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
/*     */ import com.sun.jmx.snmp.agent.SnmpMibTable;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import com.sun.jmx.snmp.agent.SnmpTableEntryFactory;
/*     */ import java.io.Serializable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JvmMemPoolTableMeta
/*     */   extends SnmpMibTable
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -2799470815264898659L;
/*     */   private JvmMemPoolEntryMeta node;
/*     */   protected SnmpStandardObjectServer objectserver;
/*     */   
/*     */   public JvmMemPoolTableMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  78 */     super(paramSnmpMib);
/*  79 */     this.objectserver = paramSnmpStandardObjectServer;
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
/*     */   protected JvmMemPoolEntryMeta createJvmMemPoolEntryMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/*  99 */     return new JvmMemPoolEntryMeta(paramSnmpMib, this.objectserver);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void createNewEntry(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 112 */     if (this.factory != null) {
/* 113 */       this.factory.createNewEntry(paramSnmpMibSubRequest, paramSnmpOid, paramInt, this);
/*     */     } else {
/* 115 */       throw new SnmpStatusException(6);
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
/*     */   public boolean isRegistrationRequired()
/*     */   {
/* 129 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void registerEntryNode(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/* 135 */     this.node = createJvmMemPoolEntryMetaNode("JvmMemPoolEntry", "JvmMemPoolTable", paramSnmpMib, paramMBeanServer);
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
/*     */   public synchronized void addEntry(SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 149 */     if (!(paramObject instanceof JvmMemPoolEntryMBean)) {
/* 150 */       throw new ClassCastException("Entries for Table \"JvmMemPoolTable\" must implement the \"JvmMemPoolEntryMBean\" interface.");
/*     */     }
/*     */     
/* 153 */     super.addEntry(paramSnmpOid, paramObjectName, paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void get(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 166 */     JvmMemPoolEntryMBean localJvmMemPoolEntryMBean = (JvmMemPoolEntryMBean)getEntry(paramSnmpOid);
/* 167 */     synchronized (this) {
/* 168 */       this.node.setInstance(localJvmMemPoolEntryMBean);
/* 169 */       this.node.get(paramSnmpMibSubRequest, paramInt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 182 */     if (paramSnmpMibSubRequest.getSize() == 0) { return;
/*     */     }
/* 184 */     JvmMemPoolEntryMBean localJvmMemPoolEntryMBean = (JvmMemPoolEntryMBean)getEntry(paramSnmpOid);
/* 185 */     synchronized (this) {
/* 186 */       this.node.setInstance(localJvmMemPoolEntryMBean);
/* 187 */       this.node.set(paramSnmpMibSubRequest, paramInt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void check(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
/*     */     throws SnmpStatusException
/*     */   {
/* 200 */     if (paramSnmpMibSubRequest.getSize() == 0) { return;
/*     */     }
/* 202 */     JvmMemPoolEntryMBean localJvmMemPoolEntryMBean = (JvmMemPoolEntryMBean)getEntry(paramSnmpOid);
/* 203 */     synchronized (this) {
/* 204 */       this.node.setInstance(localJvmMemPoolEntryMBean);
/* 205 */       this.node.check(paramSnmpMibSubRequest, paramInt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void validateVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 214 */     this.node.validateVarId(paramLong, paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isReadableEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 222 */     return this.node.isReadable(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getNextVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 230 */     long l = this.node.getNextVarId(paramLong, paramObject);
/* 231 */     while (!isReadableEntryId(paramSnmpOid, l, paramObject))
/* 232 */       l = this.node.getNextVarId(l, paramObject);
/* 233 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean skipEntryVariable(SnmpOid paramSnmpOid, long paramLong, Object paramObject, int paramInt)
/*     */   {
/*     */     try
/*     */     {
/* 245 */       JvmMemPoolEntryMBean localJvmMemPoolEntryMBean = (JvmMemPoolEntryMBean)getEntry(paramSnmpOid);
/* 246 */       synchronized (this) {
/* 247 */         this.node.setInstance(localJvmMemPoolEntryMBean);
/* 248 */         return this.node.skipVariable(paramLong, paramObject, paramInt);
/*     */       }
/*     */       
/* 251 */       return false;
/*     */     }
/*     */     catch (SnmpStatusException localSnmpStatusException) {}
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmMemPoolTableMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
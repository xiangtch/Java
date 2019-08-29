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
/*     */ public class JvmRTInputArgsTableMeta
/*     */   extends SnmpMibTable
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 5395531763015738645L;
/*     */   private JvmRTInputArgsEntryMeta node;
/*     */   protected SnmpStandardObjectServer objectserver;
/*     */   
/*     */   public JvmRTInputArgsTableMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  77 */     super(paramSnmpMib);
/*  78 */     this.objectserver = paramSnmpStandardObjectServer;
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
/*     */   protected JvmRTInputArgsEntryMeta createJvmRTInputArgsEntryMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/*  98 */     return new JvmRTInputArgsEntryMeta(paramSnmpMib, this.objectserver);
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
/* 111 */     if (this.factory != null) {
/* 112 */       this.factory.createNewEntry(paramSnmpMibSubRequest, paramSnmpOid, paramInt, this);
/*     */     } else {
/* 114 */       throw new SnmpStatusException(6);
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
/* 128 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void registerEntryNode(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/* 134 */     this.node = createJvmRTInputArgsEntryMetaNode("JvmRTInputArgsEntry", "JvmRTInputArgsTable", paramSnmpMib, paramMBeanServer);
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
/* 148 */     if (!(paramObject instanceof JvmRTInputArgsEntryMBean)) {
/* 149 */       throw new ClassCastException("Entries for Table \"JvmRTInputArgsTable\" must implement the \"JvmRTInputArgsEntryMBean\" interface.");
/*     */     }
/*     */     
/* 152 */     super.addEntry(paramSnmpOid, paramObjectName, paramObject);
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
/* 165 */     JvmRTInputArgsEntryMBean localJvmRTInputArgsEntryMBean = (JvmRTInputArgsEntryMBean)getEntry(paramSnmpOid);
/* 166 */     synchronized (this) {
/* 167 */       this.node.setInstance(localJvmRTInputArgsEntryMBean);
/* 168 */       this.node.get(paramSnmpMibSubRequest, paramInt);
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
/* 181 */     if (paramSnmpMibSubRequest.getSize() == 0) { return;
/*     */     }
/* 183 */     JvmRTInputArgsEntryMBean localJvmRTInputArgsEntryMBean = (JvmRTInputArgsEntryMBean)getEntry(paramSnmpOid);
/* 184 */     synchronized (this) {
/* 185 */       this.node.setInstance(localJvmRTInputArgsEntryMBean);
/* 186 */       this.node.set(paramSnmpMibSubRequest, paramInt);
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
/* 199 */     if (paramSnmpMibSubRequest.getSize() == 0) { return;
/*     */     }
/* 201 */     JvmRTInputArgsEntryMBean localJvmRTInputArgsEntryMBean = (JvmRTInputArgsEntryMBean)getEntry(paramSnmpOid);
/* 202 */     synchronized (this) {
/* 203 */       this.node.setInstance(localJvmRTInputArgsEntryMBean);
/* 204 */       this.node.check(paramSnmpMibSubRequest, paramInt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void validateVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 213 */     this.node.validateVarId(paramLong, paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isReadableEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 221 */     return this.node.isReadable(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getNextVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 229 */     long l = this.node.getNextVarId(paramLong, paramObject);
/* 230 */     while (!isReadableEntryId(paramSnmpOid, l, paramObject))
/* 231 */       l = this.node.getNextVarId(l, paramObject);
/* 232 */     return l;
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
/* 244 */       JvmRTInputArgsEntryMBean localJvmRTInputArgsEntryMBean = (JvmRTInputArgsEntryMBean)getEntry(paramSnmpOid);
/* 245 */       synchronized (this) {
/* 246 */         this.node.setInstance(localJvmRTInputArgsEntryMBean);
/* 247 */         return this.node.skipVariable(paramLong, paramObject, paramInt);
/*     */       }
/*     */       
/* 250 */       return false;
/*     */     }
/*     */     catch (SnmpStatusException localSnmpStatusException) {}
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmRTInputArgsTableMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.management.snmp.jvmmib;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpCounter64;
/*     */ import com.sun.jmx.snmp.SnmpOid;
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
/*     */ public class JvmThreadInstanceEntryMeta
/*     */   extends SnmpMibEntry
/*     */   implements Serializable, SnmpStandardMetaServer
/*     */ {
/*     */   static final long serialVersionUID = -2015330111801477399L;
/*     */   protected JvmThreadInstanceEntryMBean node;
/*     */   
/*     */   public JvmThreadInstanceEntryMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  79 */     this.objectserver = paramSnmpStandardObjectServer;
/*  80 */     this.varList = new int[10];
/*  81 */     this.varList[0] = 9;
/*  82 */     this.varList[1] = 8;
/*  83 */     this.varList[2] = 7;
/*  84 */     this.varList[3] = 6;
/*  85 */     this.varList[4] = 5;
/*  86 */     this.varList[5] = 4;
/*  87 */     this.varList[6] = 3;
/*  88 */     this.varList[7] = 11;
/*  89 */     this.varList[8] = 2;
/*  90 */     this.varList[9] = 10;
/*  91 */     SnmpMibNode.sort(this.varList);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue get(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/*  99 */     switch ((int)paramLong) {
/*     */     case 9: 
/* 101 */       return new SnmpString(this.node.getJvmThreadInstName());
/*     */     
/*     */     case 8: 
/* 104 */       return new SnmpCounter64(this.node.getJvmThreadInstCpuTimeNs());
/*     */     
/*     */     case 7: 
/* 107 */       return new SnmpCounter64(this.node.getJvmThreadInstWaitTimeMs());
/*     */     
/*     */     case 6: 
/* 110 */       return new SnmpCounter64(this.node.getJvmThreadInstWaitCount());
/*     */     
/*     */     case 5: 
/* 113 */       return new SnmpCounter64(this.node.getJvmThreadInstBlockTimeMs());
/*     */     
/*     */     case 4: 
/* 116 */       return new SnmpCounter64(this.node.getJvmThreadInstBlockCount());
/*     */     
/*     */     case 3: 
/* 119 */       return new SnmpString(this.node.getJvmThreadInstState());
/*     */     
/*     */     case 11: 
/* 122 */       return new SnmpOid(this.node.getJvmThreadInstLockOwnerPtr());
/*     */     
/*     */     case 2: 
/* 125 */       return new SnmpCounter64(this.node.getJvmThreadInstId());
/*     */     
/*     */     case 10: 
/* 128 */       return new SnmpString(this.node.getJvmThreadInstLockName());
/*     */     
/*     */     case 1: 
/* 131 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */     
/* 135 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 143 */     switch ((int)paramLong) {
/*     */     case 9: 
/* 145 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 8: 
/* 148 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 7: 
/* 151 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 6: 
/* 154 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 5: 
/* 157 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 4: 
/* 160 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 163 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 11: 
/* 166 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 169 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 10: 
/* 172 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 175 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 180 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 188 */     switch ((int)paramLong) {
/*     */     case 9: 
/* 190 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 8: 
/* 193 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 7: 
/* 196 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 6: 
/* 199 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 5: 
/* 202 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 4: 
/* 205 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 208 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 11: 
/* 211 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 214 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 10: 
/* 217 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 220 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/* 223 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setInstance(JvmThreadInstanceEntryMBean paramJvmThreadInstanceEntryMBean)
/*     */   {
/* 231 */     this.node = paramJvmThreadInstanceEntryMBean;
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
/* 244 */     this.objectserver.get(this, paramSnmpMibSubRequest, paramInt);
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
/* 257 */     this.objectserver.set(this, paramSnmpMibSubRequest, paramInt);
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
/* 270 */     this.objectserver.check(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVariable(long paramLong)
/*     */   {
/* 278 */     switch ((int)paramLong) {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 6: 
/*     */     case 7: 
/*     */     case 8: 
/*     */     case 9: 
/*     */     case 10: 
/*     */     case 11: 
/* 290 */       return true;
/*     */     }
/*     */     
/*     */     
/* 294 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadable(long paramLong)
/*     */   {
/* 302 */     switch ((int)paramLong) {
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 6: 
/*     */     case 7: 
/*     */     case 8: 
/*     */     case 9: 
/*     */     case 10: 
/*     */     case 11: 
/* 313 */       return true;
/*     */     }
/*     */     
/*     */     
/* 317 */     return false;
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
/* 329 */     switch ((int)paramLong) {
/*     */     case 2: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 6: 
/*     */     case 7: 
/*     */     case 8: 
/* 336 */       if (paramInt == 0) return true;
/*     */       break;
/*     */     case 1: 
/* 339 */       return true;
/*     */     }
/*     */     
/*     */     
/* 343 */     return super.skipVariable(paramLong, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttributeName(long paramLong)
/*     */     throws SnmpStatusException
/*     */   {
/* 351 */     switch ((int)paramLong) {
/*     */     case 9: 
/* 353 */       return "JvmThreadInstName";
/*     */     
/*     */     case 8: 
/* 356 */       return "JvmThreadInstCpuTimeNs";
/*     */     
/*     */     case 7: 
/* 359 */       return "JvmThreadInstWaitTimeMs";
/*     */     
/*     */     case 6: 
/* 362 */       return "JvmThreadInstWaitCount";
/*     */     
/*     */     case 5: 
/* 365 */       return "JvmThreadInstBlockTimeMs";
/*     */     
/*     */     case 4: 
/* 368 */       return "JvmThreadInstBlockCount";
/*     */     
/*     */     case 3: 
/* 371 */       return "JvmThreadInstState";
/*     */     
/*     */     case 11: 
/* 374 */       return "JvmThreadInstLockOwnerPtr";
/*     */     
/*     */     case 2: 
/* 377 */       return "JvmThreadInstId";
/*     */     
/*     */     case 10: 
/* 380 */       return "JvmThreadInstLockName";
/*     */     
/*     */     case 1: 
/* 383 */       return "JvmThreadInstIndex";
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 388 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/* 392 */   protected SnmpStandardObjectServer objectserver = null;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmThreadInstanceEntryMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
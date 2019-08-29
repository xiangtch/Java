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
/*     */ public class JvmRuntimeMeta
/*     */   extends SnmpMibGroup
/*     */   implements Serializable, SnmpStandardMetaServer
/*     */ {
/*     */   static final long serialVersionUID = 1994595220765880109L;
/*     */   protected JvmRuntimeMBean node;
/*     */   
/*     */   public JvmRuntimeMeta(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/*  78 */     this.objectserver = paramSnmpStandardObjectServer;
/*     */     try {
/*  80 */       registerObject(23L);
/*  81 */       registerObject(22L);
/*  82 */       registerObject(21L);
/*  83 */       registerObject(9L);
/*  84 */       registerObject(20L);
/*  85 */       registerObject(8L);
/*  86 */       registerObject(7L);
/*  87 */       registerObject(6L);
/*  88 */       registerObject(5L);
/*  89 */       registerObject(4L);
/*  90 */       registerObject(3L);
/*  91 */       registerObject(12L);
/*  92 */       registerObject(11L);
/*  93 */       registerObject(2L);
/*  94 */       registerObject(1L);
/*  95 */       registerObject(10L);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/*  97 */       throw new RuntimeException(localIllegalAccessException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue get(long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 106 */     switch ((int)paramLong) {
/*     */     case 23: 
/* 108 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 22: 
/* 112 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 21: 
/* 116 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 9: 
/* 120 */       return new SnmpInt(this.node.getJvmRTBootClassPathSupport());
/*     */     
/*     */     case 20: 
/* 123 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 8: 
/* 127 */       return new SnmpString(this.node.getJvmRTManagementSpecVersion());
/*     */     
/*     */     case 7: 
/* 130 */       return new SnmpString(this.node.getJvmRTSpecVersion());
/*     */     
/*     */     case 6: 
/* 133 */       return new SnmpString(this.node.getJvmRTSpecVendor());
/*     */     
/*     */     case 5: 
/* 136 */       return new SnmpString(this.node.getJvmRTSpecName());
/*     */     
/*     */     case 4: 
/* 139 */       return new SnmpString(this.node.getJvmRTVMVersion());
/*     */     
/*     */     case 3: 
/* 142 */       return new SnmpString(this.node.getJvmRTVMVendor());
/*     */     
/*     */     case 12: 
/* 145 */       return new SnmpCounter64(this.node.getJvmRTStartTimeMs());
/*     */     
/*     */     case 11: 
/* 148 */       return new SnmpCounter64(this.node.getJvmRTUptimeMs());
/*     */     
/*     */     case 2: 
/* 151 */       return new SnmpString(this.node.getJvmRTVMName());
/*     */     
/*     */     case 1: 
/* 154 */       return new SnmpString(this.node.getJvmRTName());
/*     */     
/*     */     case 10: 
/* 157 */       return new SnmpInt(this.node.getJvmRTInputArgsCount());
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 162 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 170 */     switch ((int)paramLong) {
/*     */     case 23: 
/* 172 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 22: 
/* 176 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 21: 
/* 180 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 9: 
/* 184 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 20: 
/* 187 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 8: 
/* 191 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 7: 
/* 194 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 6: 
/* 197 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 5: 
/* 200 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 4: 
/* 203 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 206 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 12: 
/* 209 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 11: 
/* 212 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 215 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 218 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 10: 
/* 221 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 226 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 234 */     switch ((int)paramLong) {
/*     */     case 23: 
/* 236 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 22: 
/* 240 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 21: 
/* 244 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 9: 
/* 248 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 20: 
/* 251 */       throw new SnmpStatusException(17);
/*     */     
/*     */ 
/*     */     case 8: 
/* 255 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 7: 
/* 258 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 6: 
/* 261 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 5: 
/* 264 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 4: 
/* 267 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 3: 
/* 270 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 12: 
/* 273 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 11: 
/* 276 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 2: 
/* 279 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 1: 
/* 282 */       throw new SnmpStatusException(17);
/*     */     
/*     */     case 10: 
/* 285 */       throw new SnmpStatusException(17);
/*     */     }
/*     */     
/* 288 */     throw new SnmpStatusException(17);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setInstance(JvmRuntimeMBean paramJvmRuntimeMBean)
/*     */   {
/* 296 */     this.node = paramJvmRuntimeMBean;
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
/* 309 */     this.objectserver.get(this, paramSnmpMibSubRequest, paramInt);
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
/* 322 */     this.objectserver.set(this, paramSnmpMibSubRequest, paramInt);
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
/* 335 */     this.objectserver.check(this, paramSnmpMibSubRequest, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVariable(long paramLong)
/*     */   {
/* 343 */     switch ((int)paramLong) {
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
/*     */     case 12: 
/* 356 */       return true;
/*     */     }
/*     */     
/*     */     
/* 360 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadable(long paramLong)
/*     */   {
/* 368 */     switch ((int)paramLong) {
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
/*     */     case 12: 
/* 381 */       return true;
/*     */     }
/*     */     
/*     */     
/* 385 */     return false;
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
/* 397 */     switch ((int)paramLong) {
/*     */     case 11: 
/*     */     case 12: 
/* 400 */       if (paramInt == 0) { return true;
/*     */       }
/*     */       break;
/*     */     }
/*     */     
/* 405 */     return super.skipVariable(paramLong, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttributeName(long paramLong)
/*     */     throws SnmpStatusException
/*     */   {
/* 413 */     switch ((int)paramLong) {
/*     */     case 23: 
/* 415 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 22: 
/* 419 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 21: 
/* 423 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 9: 
/* 427 */       return "JvmRTBootClassPathSupport";
/*     */     
/*     */     case 20: 
/* 430 */       throw new SnmpStatusException(224);
/*     */     
/*     */ 
/*     */     case 8: 
/* 434 */       return "JvmRTManagementSpecVersion";
/*     */     
/*     */     case 7: 
/* 437 */       return "JvmRTSpecVersion";
/*     */     
/*     */     case 6: 
/* 440 */       return "JvmRTSpecVendor";
/*     */     
/*     */     case 5: 
/* 443 */       return "JvmRTSpecName";
/*     */     
/*     */     case 4: 
/* 446 */       return "JvmRTVMVersion";
/*     */     
/*     */     case 3: 
/* 449 */       return "JvmRTVMVendor";
/*     */     
/*     */     case 12: 
/* 452 */       return "JvmRTStartTimeMs";
/*     */     
/*     */     case 11: 
/* 455 */       return "JvmRTUptimeMs";
/*     */     
/*     */     case 2: 
/* 458 */       return "JvmRTVMName";
/*     */     
/*     */     case 1: 
/* 461 */       return "JvmRTName";
/*     */     
/*     */     case 10: 
/* 464 */       return "JvmRTInputArgsCount";
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 469 */     throw new SnmpStatusException(225);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTable(long paramLong)
/*     */   {
/* 477 */     switch ((int)paramLong) {
/*     */     case 23: 
/* 479 */       return true;
/*     */     case 22: 
/* 481 */       return true;
/*     */     case 21: 
/* 483 */       return true;
/*     */     case 20: 
/* 485 */       return true;
/*     */     }
/*     */     
/*     */     
/* 489 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SnmpMibTable getTable(long paramLong)
/*     */   {
/* 497 */     switch ((int)paramLong) {
/*     */     case 23: 
/* 499 */       return this.tableJvmRTLibraryPathTable;
/*     */     case 22: 
/* 501 */       return this.tableJvmRTClassPathTable;
/*     */     case 21: 
/* 503 */       return this.tableJvmRTBootClassPathTable;
/*     */     case 20: 
/* 505 */       return this.tableJvmRTInputArgsTable;
/*     */     }
/*     */     
/*     */     
/* 509 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void registerTableNodes(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/* 516 */     this.tableJvmRTLibraryPathTable = createJvmRTLibraryPathTableMetaNode("JvmRTLibraryPathTable", "JvmRuntime", paramSnmpMib, paramMBeanServer);
/* 517 */     if (this.tableJvmRTLibraryPathTable != null) {
/* 518 */       this.tableJvmRTLibraryPathTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
/* 519 */       paramSnmpMib.registerTableMeta("JvmRTLibraryPathTable", this.tableJvmRTLibraryPathTable);
/*     */     }
/*     */     
/* 522 */     this.tableJvmRTClassPathTable = createJvmRTClassPathTableMetaNode("JvmRTClassPathTable", "JvmRuntime", paramSnmpMib, paramMBeanServer);
/* 523 */     if (this.tableJvmRTClassPathTable != null) {
/* 524 */       this.tableJvmRTClassPathTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
/* 525 */       paramSnmpMib.registerTableMeta("JvmRTClassPathTable", this.tableJvmRTClassPathTable);
/*     */     }
/*     */     
/* 528 */     this.tableJvmRTBootClassPathTable = createJvmRTBootClassPathTableMetaNode("JvmRTBootClassPathTable", "JvmRuntime", paramSnmpMib, paramMBeanServer);
/* 529 */     if (this.tableJvmRTBootClassPathTable != null) {
/* 530 */       this.tableJvmRTBootClassPathTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
/* 531 */       paramSnmpMib.registerTableMeta("JvmRTBootClassPathTable", this.tableJvmRTBootClassPathTable);
/*     */     }
/*     */     
/* 534 */     this.tableJvmRTInputArgsTable = createJvmRTInputArgsTableMetaNode("JvmRTInputArgsTable", "JvmRuntime", paramSnmpMib, paramMBeanServer);
/* 535 */     if (this.tableJvmRTInputArgsTable != null) {
/* 536 */       this.tableJvmRTInputArgsTable.registerEntryNode(paramSnmpMib, paramMBeanServer);
/* 537 */       paramSnmpMib.registerTableMeta("JvmRTInputArgsTable", this.tableJvmRTInputArgsTable);
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
/*     */   protected JvmRTLibraryPathTableMeta createJvmRTLibraryPathTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/* 559 */     return new JvmRTLibraryPathTableMeta(paramSnmpMib, this.objectserver);
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
/*     */   protected JvmRTClassPathTableMeta createJvmRTClassPathTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/* 579 */     return new JvmRTClassPathTableMeta(paramSnmpMib, this.objectserver);
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
/*     */   protected JvmRTBootClassPathTableMeta createJvmRTBootClassPathTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/* 599 */     return new JvmRTBootClassPathTableMeta(paramSnmpMib, this.objectserver);
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
/*     */   protected JvmRTInputArgsTableMeta createJvmRTInputArgsTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer)
/*     */   {
/* 619 */     return new JvmRTInputArgsTableMeta(paramSnmpMib, this.objectserver);
/*     */   }
/*     */   
/*     */ 
/* 623 */   protected SnmpStandardObjectServer objectserver = null;
/* 624 */   protected JvmRTLibraryPathTableMeta tableJvmRTLibraryPathTable = null;
/* 625 */   protected JvmRTClassPathTableMeta tableJvmRTClassPathTable = null;
/* 626 */   protected JvmRTBootClassPathTableMeta tableJvmRTBootClassPathTable = null;
/* 627 */   protected JvmRTInputArgsTableMeta tableJvmRTInputArgsTable = null;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmRuntimeMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
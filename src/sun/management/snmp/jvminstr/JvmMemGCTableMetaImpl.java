/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.lang.management.GarbageCollectorMXBean;
/*     */ import java.lang.management.MemoryManagerMXBean;
/*     */ import java.util.Map;
/*     */ import sun.management.snmp.jvmmib.JvmMemGCTableMeta;
/*     */ import sun.management.snmp.util.JvmContextFactory;
/*     */ import sun.management.snmp.util.MibLogger;
/*     */ import sun.management.snmp.util.SnmpCachedData;
/*     */ import sun.management.snmp.util.SnmpTableHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JvmMemGCTableMetaImpl
/*     */   extends JvmMemGCTableMeta
/*     */ {
/*     */   static final long serialVersionUID = 8250461197108867607L;
/*     */   
/*     */   protected static class GCTableFilter
/*     */   {
/*     */     public SnmpOid getNext(SnmpCachedData paramSnmpCachedData, SnmpOid paramSnmpOid)
/*     */     {
/*  88 */       boolean bool = JvmMemGCTableMetaImpl.log.isDebugOn();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  96 */       int i = paramSnmpOid == null ? -1 : paramSnmpCachedData.find(paramSnmpOid);
/*  97 */       if (bool) { JvmMemGCTableMetaImpl.log.debug("GCTableFilter", "oid=" + paramSnmpOid + " at insertion=" + i);
/*     */       }
/*     */       
/*     */ 
/* 101 */       if (i > -1) j = i + 1;
/* 102 */       for (int j = -i - 1; 
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 110 */           j < paramSnmpCachedData.indexes.length; j++) {
/* 111 */         if (bool) JvmMemGCTableMetaImpl.log.debug("GCTableFilter", "next=" + j);
/* 112 */         Object localObject = paramSnmpCachedData.datas[j];
/* 113 */         if (bool) JvmMemGCTableMetaImpl.log.debug("GCTableFilter", "value[" + j + "]=" + ((MemoryManagerMXBean)localObject)
/* 114 */             .getName());
/* 115 */         if ((localObject instanceof GarbageCollectorMXBean))
/*     */         {
/* 117 */           if (bool) { JvmMemGCTableMetaImpl.log.debug("GCTableFilter", ((MemoryManagerMXBean)localObject)
/* 118 */               .getName() + " is a  GarbageCollectorMXBean.");
/*     */           }
/* 120 */           return paramSnmpCachedData.indexes[j];
/*     */         }
/* 122 */         if (bool) { JvmMemGCTableMetaImpl.log.debug("GCTableFilter", ((MemoryManagerMXBean)localObject)
/* 123 */             .getName() + " is not a  GarbageCollectorMXBean: " + localObject
/*     */             
/* 125 */             .getClass().getName());
/*     */         }
/*     */       }
/* 128 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public SnmpOid getNext(SnmpTableHandler paramSnmpTableHandler, SnmpOid paramSnmpOid)
/*     */     {
/* 143 */       if ((paramSnmpTableHandler instanceof SnmpCachedData)) {
/* 144 */         return getNext((SnmpCachedData)paramSnmpTableHandler, paramSnmpOid);
/*     */       }
/*     */       
/* 147 */       SnmpOid localSnmpOid = paramSnmpOid;
/*     */       do {
/* 149 */         localSnmpOid = paramSnmpTableHandler.getNext(localSnmpOid);
/* 150 */         Object localObject = paramSnmpTableHandler.getData(localSnmpOid);
/* 151 */         if ((localObject instanceof GarbageCollectorMXBean))
/*     */         {
/* 153 */           return localSnmpOid;
/*     */         }
/* 155 */       } while (localSnmpOid != null);
/* 156 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Object getData(SnmpTableHandler paramSnmpTableHandler, SnmpOid paramSnmpOid)
/*     */     {
/* 166 */       Object localObject = paramSnmpTableHandler.getData(paramSnmpOid);
/* 167 */       if ((localObject instanceof GarbageCollectorMXBean)) { return localObject;
/*     */       }
/*     */       
/* 170 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean contains(SnmpTableHandler paramSnmpTableHandler, SnmpOid paramSnmpOid)
/*     */     {
/* 177 */       if ((paramSnmpTableHandler.getData(paramSnmpOid) instanceof GarbageCollectorMXBean)) {
/* 178 */         return true;
/*     */       }
/*     */       
/* 181 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 186 */   private transient JvmMemManagerTableMetaImpl managers = null;
/* 187 */   private static GCTableFilter filter = new GCTableFilter();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JvmMemGCTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/* 195 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private final JvmMemManagerTableMetaImpl getManagers(SnmpMib paramSnmpMib)
/*     */   {
/* 202 */     if (this.managers == null)
/*     */     {
/* 204 */       this.managers = ((JvmMemManagerTableMetaImpl)paramSnmpMib.getRegisteredTableMeta("JvmMemManagerTable"));
/*     */     }
/* 206 */     return this.managers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected SnmpTableHandler getHandler(Object paramObject)
/*     */   {
/* 213 */     JvmMemManagerTableMetaImpl localJvmMemManagerTableMetaImpl = getManagers(this.theMib);
/* 214 */     return localJvmMemManagerTableMetaImpl.getHandler(paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */   protected SnmpOid getNextOid(Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 221 */     return getNextOid(null, paramObject);
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 227 */     boolean bool = log.isDebugOn();
/*     */     try {
/* 229 */       if (bool) { log.debug("getNextOid", "previous=" + paramSnmpOid);
/*     */       }
/*     */       
/*     */ 
/* 233 */       SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/* 234 */       if (localSnmpTableHandler == null)
/*     */       {
/*     */ 
/*     */ 
/* 238 */         if (bool) log.debug("getNextOid", "handler is null!");
/* 239 */         throw new SnmpStatusException(224);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 246 */       SnmpOid localSnmpOid = filter.getNext(localSnmpTableHandler, paramSnmpOid);
/* 247 */       if (bool) { log.debug("getNextOid", "next=" + localSnmpOid);
/*     */       }
/*     */       
/*     */ 
/* 251 */       if (localSnmpOid == null) {
/* 252 */         throw new SnmpStatusException(224);
/*     */       }
/*     */       
/* 255 */       return localSnmpOid;
/*     */     }
/*     */     catch (RuntimeException localRuntimeException)
/*     */     {
/* 259 */       if (bool) log.debug("getNextOid", localRuntimeException);
/* 260 */       throw localRuntimeException;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
/*     */   {
/* 269 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/*     */     
/*     */ 
/*     */ 
/* 273 */     if (localSnmpTableHandler == null)
/* 274 */       return false;
/* 275 */     return filter.contains(localSnmpTableHandler, paramSnmpOid);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getEntry(SnmpOid paramSnmpOid)
/*     */     throws SnmpStatusException
/*     */   {
/* 282 */     if (paramSnmpOid == null) {
/* 283 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 287 */     Map localMap = JvmContextFactory.getUserData();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 296 */     long l = paramSnmpOid.getOidArc(0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 304 */     String str = "JvmMemGCTable.entry." + l;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 309 */     if (localMap != null) {
/* 310 */       localObject1 = localMap.get(str);
/* 311 */       if (localObject1 != null) { return localObject1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 318 */     Object localObject1 = getHandler(localMap);
/*     */     
/*     */ 
/*     */ 
/* 322 */     if (localObject1 == null) {
/* 323 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 327 */     Object localObject2 = filter.getData((SnmpTableHandler)localObject1, paramSnmpOid);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 333 */     if (localObject2 == null) {
/* 334 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 339 */     JvmMemGCEntryImpl localJvmMemGCEntryImpl = new JvmMemGCEntryImpl((GarbageCollectorMXBean)localObject2, (int)l);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 354 */     if ((localMap != null) && (localJvmMemGCEntryImpl != null)) {
/* 355 */       localMap.put(str, localJvmMemGCEntryImpl);
/*     */     }
/*     */     
/* 358 */     return localJvmMemGCEntryImpl;
/*     */   }
/*     */   
/* 361 */   static final MibLogger log = new MibLogger(JvmMemGCTableMetaImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmMemGCTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.io.Serializable;
/*     */ import java.lang.management.MemoryManagerMXBean;
/*     */ import java.lang.management.MemoryPoolMXBean;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import sun.management.snmp.jvmmib.JvmMemMgrPoolRelTableMeta;
/*     */ import sun.management.snmp.util.JvmContextFactory;
/*     */ import sun.management.snmp.util.MibLogger;
/*     */ import sun.management.snmp.util.SnmpCachedData;
/*     */ import sun.management.snmp.util.SnmpTableCache;
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
/*     */ public class JvmMemMgrPoolRelTableMetaImpl
/*     */   extends JvmMemMgrPoolRelTableMeta
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 1896509775012355443L;
/*     */   protected SnmpTableCache cache;
/*     */   
/*     */   private static class JvmMemMgrPoolRelTableCache
/*     */     extends SnmpTableCache
/*     */   {
/*     */     static final long serialVersionUID = 6059937161990659184L;
/*     */     private final JvmMemMgrPoolRelTableMetaImpl meta;
/*     */     
/*     */     JvmMemMgrPoolRelTableCache(JvmMemMgrPoolRelTableMetaImpl paramJvmMemMgrPoolRelTableMetaImpl, long paramLong)
/*     */     {
/*  86 */       this.validity = paramLong;
/*  87 */       this.meta = paramJvmMemMgrPoolRelTableMetaImpl;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public SnmpTableHandler getTableHandler()
/*     */     {
/*  94 */       Map localMap = JvmContextFactory.getUserData();
/*  95 */       return getTableDatas(localMap);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static Map<String, SnmpOid> buildPoolIndexMap(SnmpTableHandler paramSnmpTableHandler)
/*     */     {
/* 104 */       if ((paramSnmpTableHandler instanceof SnmpCachedData)) {
/* 105 */         return buildPoolIndexMap((SnmpCachedData)paramSnmpTableHandler);
/*     */       }
/*     */       
/* 108 */       HashMap localHashMap = new HashMap();
/* 109 */       SnmpOid localSnmpOid = null;
/* 110 */       while ((localSnmpOid = paramSnmpTableHandler.getNext(localSnmpOid)) != null)
/*     */       {
/* 112 */         MemoryPoolMXBean localMemoryPoolMXBean = (MemoryPoolMXBean)paramSnmpTableHandler.getData(localSnmpOid);
/* 113 */         if (localMemoryPoolMXBean != null) {
/* 114 */           String str = localMemoryPoolMXBean.getName();
/* 115 */           if (str != null)
/* 116 */             localHashMap.put(str, localSnmpOid);
/*     */         } }
/* 118 */       return localHashMap;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static Map<String, SnmpOid> buildPoolIndexMap(SnmpCachedData paramSnmpCachedData)
/*     */     {
/* 127 */       if (paramSnmpCachedData == null) return Collections.emptyMap();
/* 128 */       SnmpOid[] arrayOfSnmpOid = paramSnmpCachedData.indexes;
/* 129 */       Object[] arrayOfObject = paramSnmpCachedData.datas;
/* 130 */       int i = arrayOfSnmpOid.length;
/* 131 */       HashMap localHashMap = new HashMap(i);
/* 132 */       for (int j = 0; j < i; j++) {
/* 133 */         SnmpOid localSnmpOid = arrayOfSnmpOid[j];
/* 134 */         if (localSnmpOid != null) {
/* 135 */           MemoryPoolMXBean localMemoryPoolMXBean = (MemoryPoolMXBean)arrayOfObject[j];
/*     */           
/* 137 */           if (localMemoryPoolMXBean != null) {
/* 138 */             String str = localMemoryPoolMXBean.getName();
/* 139 */             if (str != null)
/* 140 */               localHashMap.put(str, localSnmpOid);
/*     */           } } }
/* 142 */       return localHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected SnmpCachedData updateCachedDatas(Object paramObject)
/*     */     {
/* 162 */       SnmpTableHandler localSnmpTableHandler1 = this.meta.getManagerHandler(paramObject);
/*     */       
/*     */ 
/*     */ 
/* 166 */       SnmpTableHandler localSnmpTableHandler2 = this.meta.getPoolHandler(paramObject);
/*     */       
/*     */ 
/* 169 */       long l = System.currentTimeMillis();
/*     */       
/*     */ 
/* 172 */       Map localMap = buildPoolIndexMap(localSnmpTableHandler2);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 177 */       TreeMap localTreeMap = new TreeMap(SnmpCachedData.oidComparator);
/*     */       
/* 179 */       updateTreeMap(localTreeMap, paramObject, localSnmpTableHandler1, localSnmpTableHandler2, localMap);
/*     */       
/* 181 */       return new SnmpCachedData(l, localTreeMap);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected String[] getMemoryPools(Object paramObject, MemoryManagerMXBean paramMemoryManagerMXBean, long paramLong)
/*     */     {
/* 191 */       String str = "JvmMemManager." + paramLong + ".getMemoryPools";
/*     */       
/*     */ 
/* 194 */       String[] arrayOfString = null;
/* 195 */       if ((paramObject instanceof Map)) {
/* 196 */         arrayOfString = (String[])((Map)paramObject).get(str);
/* 197 */         if (arrayOfString != null) { return arrayOfString;
/*     */         }
/*     */       }
/* 200 */       if (paramMemoryManagerMXBean != null) {
/* 201 */         arrayOfString = paramMemoryManagerMXBean.getMemoryPoolNames();
/*     */       }
/* 203 */       if ((arrayOfString != null) && ((paramObject instanceof Map))) {
/* 204 */         Map localMap = (Map)Util.cast(paramObject);
/* 205 */         localMap.put(str, arrayOfString);
/*     */       }
/*     */       
/* 208 */       return arrayOfString;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void updateTreeMap(TreeMap<SnmpOid, Object> paramTreeMap, Object paramObject, MemoryManagerMXBean paramMemoryManagerMXBean, SnmpOid paramSnmpOid, Map<String, SnmpOid> paramMap)
/*     */     {
/*     */       long l1;
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 220 */         l1 = paramSnmpOid.getOidArc(0);
/*     */       } catch (SnmpStatusException localSnmpStatusException1) {
/* 222 */         JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", "Bad MemoryManager OID index: " + paramSnmpOid);
/*     */         
/* 224 */         JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", localSnmpStatusException1);
/* 225 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 230 */       String[] arrayOfString = getMemoryPools(paramObject, paramMemoryManagerMXBean, l1);
/* 231 */       if ((arrayOfString == null) || (arrayOfString.length < 1)) { return;
/*     */       }
/* 233 */       String str1 = paramMemoryManagerMXBean.getName();
/* 234 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 235 */         String str2 = arrayOfString[i];
/* 236 */         if (str2 != null) {
/* 237 */           SnmpOid localSnmpOid1 = (SnmpOid)paramMap.get(str2);
/* 238 */           if (localSnmpOid1 != null)
/*     */           {
/*     */             long l2;
/*     */             
/*     */             try
/*     */             {
/* 244 */               l2 = localSnmpOid1.getOidArc(0);
/*     */             } catch (SnmpStatusException localSnmpStatusException2) {
/* 246 */               JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", "Bad MemoryPool OID index: " + localSnmpOid1);
/*     */               
/* 248 */               JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", localSnmpStatusException2);
/* 249 */               continue;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 254 */             long[] arrayOfLong = { l1, l2 };
/*     */             
/* 256 */             SnmpOid localSnmpOid2 = new SnmpOid(arrayOfLong);
/*     */             
/* 258 */             paramTreeMap.put(localSnmpOid2, new JvmMemMgrPoolRelEntryImpl(str1, str2, (int)l1, (int)l2));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void updateTreeMap(TreeMap<SnmpOid, Object> paramTreeMap, Object paramObject, SnmpTableHandler paramSnmpTableHandler1, SnmpTableHandler paramSnmpTableHandler2, Map<String, SnmpOid> paramMap)
/*     */     {
/* 269 */       if ((paramSnmpTableHandler1 instanceof SnmpCachedData)) {
/* 270 */         updateTreeMap(paramTreeMap, paramObject, (SnmpCachedData)paramSnmpTableHandler1, paramSnmpTableHandler2, paramMap);
/*     */         
/* 272 */         return;
/*     */       }
/*     */       
/* 275 */       SnmpOid localSnmpOid = null;
/* 276 */       while ((localSnmpOid = paramSnmpTableHandler1.getNext(localSnmpOid)) != null)
/*     */       {
/* 278 */         MemoryManagerMXBean localMemoryManagerMXBean = (MemoryManagerMXBean)paramSnmpTableHandler1.getData(localSnmpOid);
/* 279 */         if (localMemoryManagerMXBean != null) {
/* 280 */           updateTreeMap(paramTreeMap, paramObject, localMemoryManagerMXBean, localSnmpOid, paramMap);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void updateTreeMap(TreeMap<SnmpOid, Object> paramTreeMap, Object paramObject, SnmpCachedData paramSnmpCachedData, SnmpTableHandler paramSnmpTableHandler, Map<String, SnmpOid> paramMap)
/*     */     {
/* 289 */       SnmpOid[] arrayOfSnmpOid = paramSnmpCachedData.indexes;
/* 290 */       Object[] arrayOfObject = paramSnmpCachedData.datas;
/* 291 */       int i = arrayOfSnmpOid.length;
/* 292 */       for (int j = i - 1; j > -1; j--) {
/* 293 */         MemoryManagerMXBean localMemoryManagerMXBean = (MemoryManagerMXBean)arrayOfObject[j];
/*     */         
/* 295 */         if (localMemoryManagerMXBean != null) {
/* 296 */           updateTreeMap(paramTreeMap, paramObject, localMemoryManagerMXBean, arrayOfSnmpOid[j], paramMap);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 304 */   private transient JvmMemManagerTableMetaImpl managers = null;
/* 305 */   private transient JvmMemPoolTableMetaImpl pools = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JvmMemMgrPoolRelTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/* 316 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/*     */     
/*     */ 
/* 319 */     this.cache = new JvmMemMgrPoolRelTableCache(this, ((JVM_MANAGEMENT_MIB_IMPL)paramSnmpMib).validity());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private final JvmMemManagerTableMetaImpl getManagers(SnmpMib paramSnmpMib)
/*     */   {
/* 326 */     if (this.managers == null)
/*     */     {
/* 328 */       this.managers = ((JvmMemManagerTableMetaImpl)paramSnmpMib.getRegisteredTableMeta("JvmMemManagerTable"));
/*     */     }
/* 330 */     return this.managers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private final JvmMemPoolTableMetaImpl getPools(SnmpMib paramSnmpMib)
/*     */   {
/* 337 */     if (this.pools == null)
/*     */     {
/* 339 */       this.pools = ((JvmMemPoolTableMetaImpl)paramSnmpMib.getRegisteredTableMeta("JvmMemPoolTable"));
/*     */     }
/* 341 */     return this.pools;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected SnmpTableHandler getManagerHandler(Object paramObject)
/*     */   {
/* 348 */     JvmMemManagerTableMetaImpl localJvmMemManagerTableMetaImpl = getManagers(this.theMib);
/* 349 */     return localJvmMemManagerTableMetaImpl.getHandler(paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected SnmpTableHandler getPoolHandler(Object paramObject)
/*     */   {
/* 356 */     JvmMemPoolTableMetaImpl localJvmMemPoolTableMetaImpl = getPools(this.theMib);
/* 357 */     return localJvmMemPoolTableMetaImpl.getHandler(paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */   protected SnmpOid getNextOid(Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 364 */     return getNextOid(null, paramObject);
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 370 */     boolean bool = log.isDebugOn();
/* 371 */     if (bool) { log.debug("getNextOid", "previous=" + paramSnmpOid);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 376 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/* 377 */     if (localSnmpTableHandler == null)
/*     */     {
/*     */ 
/*     */ 
/* 381 */       if (bool) log.debug("getNextOid", "handler is null!");
/* 382 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 387 */     SnmpOid localSnmpOid = localSnmpTableHandler.getNext(paramSnmpOid);
/* 388 */     if (bool) { log.debug("getNextOid", "next=" + localSnmpOid);
/*     */     }
/*     */     
/*     */ 
/* 392 */     if (localSnmpOid == null) {
/* 393 */       throw new SnmpStatusException(224);
/*     */     }
/* 395 */     return localSnmpOid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
/*     */   {
/* 404 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/*     */     
/*     */ 
/*     */ 
/* 408 */     if (localSnmpTableHandler == null) {
/* 409 */       return false;
/*     */     }
/* 411 */     return localSnmpTableHandler.contains(paramSnmpOid);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getEntry(SnmpOid paramSnmpOid)
/*     */     throws SnmpStatusException
/*     */   {
/* 418 */     if ((paramSnmpOid == null) || (paramSnmpOid.getLength() < 2)) {
/* 419 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 423 */     Map localMap = JvmContextFactory.getUserData();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 430 */     long l1 = paramSnmpOid.getOidArc(0);
/* 431 */     long l2 = paramSnmpOid.getOidArc(1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 439 */     String str = "JvmMemMgrPoolRelTable.entry." + l1 + "." + l2;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 445 */     if (localMap != null) {
/* 446 */       localObject1 = localMap.get(str);
/* 447 */       if (localObject1 != null) { return localObject1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 454 */     Object localObject1 = getHandler(localMap);
/*     */     
/*     */ 
/*     */ 
/* 458 */     if (localObject1 == null) {
/* 459 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 463 */     Object localObject2 = ((SnmpTableHandler)localObject1).getData(paramSnmpOid);
/*     */     
/*     */ 
/*     */ 
/* 467 */     if (!(localObject2 instanceof JvmMemMgrPoolRelEntryImpl)) {
/* 468 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 473 */     JvmMemMgrPoolRelEntryImpl localJvmMemMgrPoolRelEntryImpl = (JvmMemMgrPoolRelEntryImpl)localObject2;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 481 */     if ((localMap != null) && (localJvmMemMgrPoolRelEntryImpl != null)) {
/* 482 */       localMap.put(str, localJvmMemMgrPoolRelEntryImpl);
/*     */     }
/*     */     
/* 485 */     return localJvmMemMgrPoolRelEntryImpl;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SnmpTableHandler getHandler(Object paramObject)
/*     */   {
/*     */     Map localMap;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 502 */     if ((paramObject instanceof Map)) localMap = (Map)Util.cast(paramObject); else {
/* 503 */       localMap = null;
/*     */     }
/*     */     
/* 506 */     if (localMap != null)
/*     */     {
/* 508 */       localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmMemMgrPoolRelTable.handler");
/* 509 */       if (localSnmpTableHandler != null) { return localSnmpTableHandler;
/*     */       }
/*     */     }
/*     */     
/* 513 */     SnmpTableHandler localSnmpTableHandler = this.cache.getTableHandler();
/*     */     
/* 515 */     if ((localMap != null) && (localSnmpTableHandler != null)) {
/* 516 */       localMap.put("JvmMemMgrPoolRelTable.handler", localSnmpTableHandler);
/*     */     }
/* 518 */     return localSnmpTableHandler;
/*     */   }
/*     */   
/* 521 */   static final MibLogger log = new MibLogger(JvmMemMgrPoolRelTableMetaImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmMemMgrPoolRelTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
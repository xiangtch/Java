/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.lang.management.ThreadInfo;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import sun.management.snmp.jvmmib.JvmThreadInstanceTableMeta;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JvmThreadInstanceTableMetaImpl
/*     */   extends JvmThreadInstanceTableMeta
/*     */ {
/*     */   static final long serialVersionUID = -8432271929226397492L;
/*     */   public static final int MAX_STACK_TRACE_DEPTH = 0;
/*     */   protected SnmpTableCache cache;
/*     */   
/*     */   static SnmpOid makeOid(long paramLong)
/*     */   {
/* 100 */     long[] arrayOfLong = new long[8];
/* 101 */     arrayOfLong[0] = (paramLong >> 56 & 0xFF);
/* 102 */     arrayOfLong[1] = (paramLong >> 48 & 0xFF);
/* 103 */     arrayOfLong[2] = (paramLong >> 40 & 0xFF);
/* 104 */     arrayOfLong[3] = (paramLong >> 32 & 0xFF);
/* 105 */     arrayOfLong[4] = (paramLong >> 24 & 0xFF);
/* 106 */     arrayOfLong[5] = (paramLong >> 16 & 0xFF);
/* 107 */     arrayOfLong[6] = (paramLong >> 8 & 0xFF);
/* 108 */     arrayOfLong[7] = (paramLong & 0xFF);
/* 109 */     return new SnmpOid(arrayOfLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static long makeId(SnmpOid paramSnmpOid)
/*     */   {
/* 118 */     long l = 0L;
/* 119 */     long[] arrayOfLong = paramSnmpOid.longValue(false);
/*     */     
/* 121 */     l |= arrayOfLong[0] << 56;
/* 122 */     l |= arrayOfLong[1] << 48;
/* 123 */     l |= arrayOfLong[2] << 40;
/* 124 */     l |= arrayOfLong[3] << 32;
/* 125 */     l |= arrayOfLong[4] << 24;
/* 126 */     l |= arrayOfLong[5] << 16;
/* 127 */     l |= arrayOfLong[6] << 8;
/* 128 */     l |= arrayOfLong[7];
/*     */     
/* 130 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class JvmThreadInstanceTableCache
/*     */     extends SnmpTableCache
/*     */   {
/*     */     static final long serialVersionUID = 4947330124563406878L;
/*     */     
/*     */ 
/*     */ 
/*     */     private final JvmThreadInstanceTableMetaImpl meta;
/*     */     
/*     */ 
/*     */ 
/*     */     JvmThreadInstanceTableCache(JvmThreadInstanceTableMetaImpl paramJvmThreadInstanceTableMetaImpl, long paramLong)
/*     */     {
/* 149 */       this.validity = paramLong;
/* 150 */       this.meta = paramJvmThreadInstanceTableMetaImpl;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public SnmpTableHandler getTableHandler()
/*     */     {
/* 157 */       Map localMap = JvmContextFactory.getUserData();
/* 158 */       return getTableDatas(localMap);
/*     */     }
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
/* 170 */       long[] arrayOfLong = JvmThreadingImpl.getThreadMXBean().getAllThreadIds();
/*     */       
/*     */ 
/*     */ 
/* 174 */       long l = System.currentTimeMillis();
/*     */       
/* 176 */       SnmpOid[] arrayOfSnmpOid = new SnmpOid[arrayOfLong.length];
/* 177 */       TreeMap localTreeMap = new TreeMap(SnmpCachedData.oidComparator);
/*     */       
/* 179 */       for (int i = 0; i < arrayOfLong.length; i++) {
/* 180 */         JvmThreadInstanceTableMetaImpl.log.debug("", "Making index for thread id [" + arrayOfLong[i] + "]");
/*     */         
/* 182 */         SnmpOid localSnmpOid = JvmThreadInstanceTableMetaImpl.makeOid(arrayOfLong[i]);
/* 183 */         localTreeMap.put(localSnmpOid, localSnmpOid);
/*     */       }
/*     */       
/* 186 */       return new SnmpCachedData(l, localTreeMap);
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
/*     */   public JvmThreadInstanceTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/* 203 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/*     */     
/* 205 */     this.cache = new JvmThreadInstanceTableCache(this, ((JVM_MANAGEMENT_MIB_IMPL)paramSnmpMib).validity());
/* 206 */     log.debug("JvmThreadInstanceTableMetaImpl", "Create Thread meta");
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 212 */     log.debug("JvmThreadInstanceTableMetaImpl", "getNextOid");
/*     */     
/* 214 */     return getNextOid(null, paramObject);
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 220 */     log.debug("getNextOid", "previous=" + paramSnmpOid);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 225 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/* 226 */     if (localSnmpTableHandler == null)
/*     */     {
/*     */ 
/*     */ 
/* 230 */       log.debug("getNextOid", "handler is null!");
/* 231 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 236 */     SnmpOid localSnmpOid = paramSnmpOid;
/*     */     for (;;) {
/* 238 */       localSnmpOid = localSnmpTableHandler.getNext(localSnmpOid);
/* 239 */       if (localSnmpOid != null)
/* 240 */         if (getJvmThreadInstance(paramObject, localSnmpOid) != null)
/*     */           break;
/*     */     }
/* 243 */     log.debug("*** **** **** **** getNextOid", "next=" + localSnmpOid);
/*     */     
/*     */ 
/*     */ 
/* 247 */     if (localSnmpOid == null) {
/* 248 */       throw new SnmpStatusException(224);
/*     */     }
/* 250 */     return localSnmpOid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
/*     */   {
/* 258 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/*     */     
/*     */ 
/*     */ 
/* 262 */     if (localSnmpTableHandler == null)
/* 263 */       return false;
/* 264 */     if (!localSnmpTableHandler.contains(paramSnmpOid)) {
/* 265 */       return false;
/*     */     }
/* 267 */     JvmThreadInstanceEntryImpl localJvmThreadInstanceEntryImpl = getJvmThreadInstance(paramObject, paramSnmpOid);
/* 268 */     return localJvmThreadInstanceEntryImpl != null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getEntry(SnmpOid paramSnmpOid)
/*     */     throws SnmpStatusException
/*     */   {
/* 275 */     log.debug("*** **** **** **** getEntry", "oid [" + paramSnmpOid + "]");
/* 276 */     if ((paramSnmpOid == null) || (paramSnmpOid.getLength() != 8)) {
/* 277 */       log.debug("getEntry", "Invalid oid [" + paramSnmpOid + "]");
/* 278 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 283 */     Map localMap = JvmContextFactory.getUserData();
/*     */     
/*     */ 
/*     */ 
/* 287 */     SnmpTableHandler localSnmpTableHandler = getHandler(localMap);
/*     */     
/*     */ 
/*     */ 
/* 291 */     if ((localSnmpTableHandler == null) || (!localSnmpTableHandler.contains(paramSnmpOid))) {
/* 292 */       throw new SnmpStatusException(224);
/*     */     }
/* 294 */     JvmThreadInstanceEntryImpl localJvmThreadInstanceEntryImpl = getJvmThreadInstance(localMap, paramSnmpOid);
/*     */     
/* 296 */     if (localJvmThreadInstanceEntryImpl == null) {
/* 297 */       throw new SnmpStatusException(224);
/*     */     }
/* 299 */     return localJvmThreadInstanceEntryImpl;
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
/* 316 */     if ((paramObject instanceof Map)) localMap = (Map)Util.cast(paramObject); else {
/* 317 */       localMap = null;
/*     */     }
/*     */     
/* 320 */     if (localMap != null)
/*     */     {
/* 322 */       localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmThreadInstanceTable.handler");
/* 323 */       if (localSnmpTableHandler != null) { return localSnmpTableHandler;
/*     */       }
/*     */     }
/*     */     
/* 327 */     SnmpTableHandler localSnmpTableHandler = this.cache.getTableHandler();
/*     */     
/* 329 */     if ((localMap != null) && (localSnmpTableHandler != null)) {
/* 330 */       localMap.put("JvmThreadInstanceTable.handler", localSnmpTableHandler);
/*     */     }
/* 332 */     return localSnmpTableHandler;
/*     */   }
/*     */   
/*     */   private ThreadInfo getThreadInfo(long paramLong) {
/* 336 */     return 
/* 337 */       JvmThreadingImpl.getThreadMXBean().getThreadInfo(paramLong, 0);
/*     */   }
/*     */   
/*     */   private ThreadInfo getThreadInfo(SnmpOid paramSnmpOid) {
/* 341 */     return getThreadInfo(makeId(paramSnmpOid));
/*     */   }
/*     */   
/*     */   private JvmThreadInstanceEntryImpl getJvmThreadInstance(Object paramObject, SnmpOid paramSnmpOid)
/*     */   {
/* 346 */     JvmThreadInstanceEntryImpl localJvmThreadInstanceEntryImpl = null;
/* 347 */     String str = null;
/* 348 */     Map localMap = null;
/* 349 */     boolean bool = log.isDebugOn();
/*     */     
/* 351 */     if ((paramObject instanceof Map)) {
/* 352 */       localMap = (Map)Util.cast(paramObject);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 360 */       str = "JvmThreadInstanceTable.entry." + paramSnmpOid.toString();
/*     */       
/* 362 */       localJvmThreadInstanceEntryImpl = (JvmThreadInstanceEntryImpl)localMap.get(str);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 367 */     if (localJvmThreadInstanceEntryImpl != null) {
/* 368 */       if (bool) { log.debug("*** getJvmThreadInstance", "Entry found in cache: " + str);
/*     */       }
/* 370 */       return localJvmThreadInstanceEntryImpl;
/*     */     }
/*     */     
/* 373 */     if (bool) { log.debug("*** getJvmThreadInstance", "Entry [" + paramSnmpOid + "] is not in cache");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 378 */     ThreadInfo localThreadInfo = null;
/*     */     try {
/* 380 */       localThreadInfo = getThreadInfo(paramSnmpOid);
/*     */     } catch (RuntimeException localRuntimeException) {
/* 382 */       log.trace("*** getJvmThreadInstance", "Failed to get thread info for rowOid: " + paramSnmpOid);
/*     */       
/* 384 */       log.debug("*** getJvmThreadInstance", localRuntimeException);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 389 */     if (localThreadInfo == null) {
/* 390 */       if (bool) { log.debug("*** getJvmThreadInstance", "No entry by that oid [" + paramSnmpOid + "]");
/*     */       }
/* 392 */       return null;
/*     */     }
/*     */     
/* 395 */     localJvmThreadInstanceEntryImpl = new JvmThreadInstanceEntryImpl(localThreadInfo, paramSnmpOid.toByte());
/* 396 */     if (localMap != null) localMap.put(str, localJvmThreadInstanceEntryImpl);
/* 397 */     if (bool) { log.debug("*** getJvmThreadInstance", "Entry created for Thread OID [" + paramSnmpOid + "]");
/*     */     }
/* 399 */     return localJvmThreadInstanceEntryImpl;
/*     */   }
/*     */   
/* 402 */   static final MibLogger log = new MibLogger(JvmThreadInstanceTableMetaImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmThreadInstanceTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
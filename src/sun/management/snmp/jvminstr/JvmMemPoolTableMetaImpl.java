/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MemoryPoolMXBean;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import sun.management.snmp.jvmmib.JvmMemPoolTableMeta;
/*     */ import sun.management.snmp.util.JvmContextFactory;
/*     */ import sun.management.snmp.util.MibLogger;
/*     */ import sun.management.snmp.util.SnmpNamedListTableCache;
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
/*     */ public class JvmMemPoolTableMetaImpl
/*     */   extends JvmMemPoolTableMeta
/*     */ {
/*     */   static final long serialVersionUID = -2525820976094284957L;
/*     */   protected SnmpTableCache cache;
/*     */   
/*     */   private static class JvmMemPoolTableCache
/*     */     extends SnmpNamedListTableCache
/*     */   {
/*     */     static final long serialVersionUID = -1755520683086760574L;
/*     */     
/*     */     JvmMemPoolTableCache(long paramLong)
/*     */     {
/*  79 */       this.validity = paramLong;
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
/*     */     protected String getKey(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2)
/*     */     {
/*  96 */       if (paramObject2 == null) return null;
/*  97 */       String str = ((MemoryPoolMXBean)paramObject2).getName();
/*  98 */       JvmMemPoolTableMetaImpl.log.debug("getKey", "key=" + str);
/*  99 */       return str;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public SnmpTableHandler getTableHandler()
/*     */     {
/* 106 */       Map localMap = JvmContextFactory.getUserData();
/* 107 */       return getTableDatas(localMap);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected String getRawDatasKey()
/*     */     {
/* 114 */       return "JvmMemManagerTable.getMemoryPools";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected List<MemoryPoolMXBean> loadRawDatas(Map<Object, Object> paramMap)
/*     */     {
/* 122 */       return ManagementFactory.getMemoryPoolMXBeans();
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
/*     */   public JvmMemPoolTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/* 135 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/*     */     
/*     */ 
/* 138 */     this.cache = new JvmMemPoolTableCache(((JVM_MANAGEMENT_MIB_IMPL)paramSnmpMib).validity() * 30L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected SnmpOid getNextOid(Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 146 */     return getNextOid(null, paramObject);
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 152 */     boolean bool = log.isDebugOn();
/*     */     try {
/* 154 */       if (bool) { log.debug("getNextOid", "previous=" + paramSnmpOid);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 159 */       SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/* 160 */       if (localSnmpTableHandler == null)
/*     */       {
/*     */ 
/*     */ 
/* 164 */         if (bool) log.debug("getNextOid", "handler is null!");
/* 165 */         throw new SnmpStatusException(224);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 171 */       SnmpOid localSnmpOid = localSnmpTableHandler.getNext(paramSnmpOid);
/* 172 */       if (bool) { log.debug("getNextOid", "next=" + localSnmpOid);
/*     */       }
/*     */       
/*     */ 
/* 176 */       if (localSnmpOid == null) {
/* 177 */         throw new SnmpStatusException(224);
/*     */       }
/*     */       
/* 180 */       return localSnmpOid;
/*     */     } catch (SnmpStatusException localSnmpStatusException) {
/* 182 */       if (bool) log.debug("getNextOid", "End of MIB View: " + localSnmpStatusException);
/* 183 */       throw localSnmpStatusException;
/*     */     } catch (RuntimeException localRuntimeException) {
/* 185 */       if (bool) log.debug("getNextOid", "Unexpected exception: " + localRuntimeException);
/* 186 */       if (bool) log.debug("getNextOid", localRuntimeException);
/* 187 */       throw localRuntimeException;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
/*     */   {
/* 197 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/*     */     
/*     */ 
/*     */ 
/* 201 */     if (localSnmpTableHandler == null) {
/* 202 */       return false;
/*     */     }
/* 204 */     return localSnmpTableHandler.contains(paramSnmpOid);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getEntry(SnmpOid paramSnmpOid)
/*     */     throws SnmpStatusException
/*     */   {
/* 211 */     if (paramSnmpOid == null) {
/* 212 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 216 */     Map localMap = (Map)Util.cast(JvmContextFactory.getUserData());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 221 */     long l = paramSnmpOid.getOidArc(0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 229 */     String str = "JvmMemPoolTable.entry." + l;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 234 */     if (localMap != null) {
/* 235 */       localObject1 = localMap.get(str);
/* 236 */       if (localObject1 != null) { return localObject1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 243 */     Object localObject1 = getHandler(localMap);
/*     */     
/*     */ 
/*     */ 
/* 247 */     if (localObject1 == null) {
/* 248 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 252 */     Object localObject2 = ((SnmpTableHandler)localObject1).getData(paramSnmpOid);
/*     */     
/*     */ 
/*     */ 
/* 256 */     if (localObject2 == null) {
/* 257 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 262 */     if (log.isDebugOn())
/* 263 */       log.debug("getEntry", "data is a: " + localObject2.getClass().getName());
/* 264 */     JvmMemPoolEntryImpl localJvmMemPoolEntryImpl = new JvmMemPoolEntryImpl((MemoryPoolMXBean)localObject2, (int)l);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 270 */     if ((localMap != null) && (localJvmMemPoolEntryImpl != null)) {
/* 271 */       localMap.put(str, localJvmMemPoolEntryImpl);
/*     */     }
/*     */     
/* 274 */     return localJvmMemPoolEntryImpl;
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
/* 291 */     if ((paramObject instanceof Map)) localMap = (Map)Util.cast(paramObject); else {
/* 292 */       localMap = null;
/*     */     }
/*     */     
/* 295 */     if (localMap != null)
/*     */     {
/* 297 */       localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmMemPoolTable.handler");
/* 298 */       if (localSnmpTableHandler != null) { return localSnmpTableHandler;
/*     */       }
/*     */     }
/*     */     
/* 302 */     SnmpTableHandler localSnmpTableHandler = this.cache.getTableHandler();
/*     */     
/* 304 */     if ((localMap != null) && (localSnmpTableHandler != null)) {
/* 305 */       localMap.put("JvmMemPoolTable.handler", localSnmpTableHandler);
/*     */     }
/* 307 */     return localSnmpTableHandler;
/*     */   }
/*     */   
/* 310 */   static final MibLogger log = new MibLogger(JvmMemPoolTableMetaImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmMemPoolTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
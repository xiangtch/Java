/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MemoryManagerMXBean;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import sun.management.snmp.jvmmib.JvmMemManagerTableMeta;
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
/*     */ 
/*     */ public class JvmMemManagerTableMetaImpl
/*     */   extends JvmMemManagerTableMeta
/*     */ {
/*     */   static final long serialVersionUID = 36176771566817592L;
/*     */   protected SnmpTableCache cache;
/*     */   
/*     */   private static class JvmMemManagerTableCache
/*     */     extends SnmpNamedListTableCache
/*     */   {
/*     */     static final long serialVersionUID = 6564294074653009240L;
/*     */     
/*     */     JvmMemManagerTableCache(long paramLong)
/*     */     {
/*  80 */       this.validity = paramLong;
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
/*  97 */       if (paramObject2 == null) return null;
/*  98 */       String str = ((MemoryManagerMXBean)paramObject2).getName();
/*  99 */       JvmMemManagerTableMetaImpl.log.debug("getKey", "key=" + str);
/* 100 */       return str;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public SnmpTableHandler getTableHandler()
/*     */     {
/* 107 */       Map localMap = JvmContextFactory.getUserData();
/* 108 */       return getTableDatas(localMap);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected String getRawDatasKey()
/*     */     {
/* 115 */       return "JvmMemManagerTable.getMemoryManagers";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected List<MemoryManagerMXBean> loadRawDatas(Map<Object, Object> paramMap)
/*     */     {
/* 123 */       return ManagementFactory.getMemoryManagerMXBeans();
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
/*     */   public JvmMemManagerTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/* 140 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/*     */     
/*     */ 
/* 143 */     this.cache = new JvmMemManagerTableCache(((JVM_MANAGEMENT_MIB_IMPL)paramSnmpMib).validity());
/*     */   }
/*     */   
/*     */ 
/*     */   protected SnmpOid getNextOid(Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 150 */     return getNextOid(null, paramObject);
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 156 */     boolean bool = log.isDebugOn();
/* 157 */     if (bool) { log.debug("getNextOid", "previous=" + paramSnmpOid);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 162 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/* 163 */     if (localSnmpTableHandler == null)
/*     */     {
/*     */ 
/*     */ 
/* 167 */       if (bool) log.debug("getNextOid", "handler is null!");
/* 168 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 173 */     SnmpOid localSnmpOid = localSnmpTableHandler.getNext(paramSnmpOid);
/* 174 */     if (bool) { log.debug("getNextOid", "next=" + localSnmpOid);
/*     */     }
/*     */     
/*     */ 
/* 178 */     if (localSnmpOid == null) {
/* 179 */       throw new SnmpStatusException(224);
/*     */     }
/* 181 */     return localSnmpOid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
/*     */   {
/* 190 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/*     */     
/*     */ 
/*     */ 
/* 194 */     if (localSnmpTableHandler == null) {
/* 195 */       return false;
/*     */     }
/* 197 */     return localSnmpTableHandler.contains(paramSnmpOid);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getEntry(SnmpOid paramSnmpOid)
/*     */     throws SnmpStatusException
/*     */   {
/* 204 */     if (paramSnmpOid == null) {
/* 205 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 209 */     Map localMap = JvmContextFactory.getUserData();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 214 */     long l = paramSnmpOid.getOidArc(0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 222 */     String str = "JvmMemManagerTable.entry." + l;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 227 */     if (localMap != null) {
/* 228 */       localObject1 = localMap.get(str);
/* 229 */       if (localObject1 != null) { return localObject1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 236 */     Object localObject1 = getHandler(localMap);
/*     */     
/*     */ 
/*     */ 
/* 240 */     if (localObject1 == null) {
/* 241 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 245 */     Object localObject2 = ((SnmpTableHandler)localObject1).getData(paramSnmpOid);
/*     */     
/*     */ 
/*     */ 
/* 249 */     if (localObject2 == null) {
/* 250 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 255 */     JvmMemManagerEntryImpl localJvmMemManagerEntryImpl = new JvmMemManagerEntryImpl((MemoryManagerMXBean)localObject2, (int)l);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 261 */     if ((localMap != null) && (localJvmMemManagerEntryImpl != null)) {
/* 262 */       localMap.put(str, localJvmMemManagerEntryImpl);
/*     */     }
/*     */     
/* 265 */     return localJvmMemManagerEntryImpl;
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
/* 282 */     if ((paramObject instanceof Map)) localMap = (Map)Util.cast(paramObject); else {
/* 283 */       localMap = null;
/*     */     }
/*     */     
/* 286 */     if (localMap != null)
/*     */     {
/* 288 */       localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmMemManagerTable.handler");
/* 289 */       if (localSnmpTableHandler != null) { return localSnmpTableHandler;
/*     */       }
/*     */     }
/*     */     
/* 293 */     SnmpTableHandler localSnmpTableHandler = this.cache.getTableHandler();
/*     */     
/* 295 */     if ((localMap != null) && (localSnmpTableHandler != null)) {
/* 296 */       localMap.put("JvmMemManagerTable.handler", localSnmpTableHandler);
/*     */     }
/* 298 */     return localSnmpTableHandler;
/*     */   }
/*     */   
/* 301 */   static final MibLogger log = new MibLogger(JvmMemManagerTableMetaImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmMemManagerTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
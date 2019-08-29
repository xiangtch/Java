/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.util.Map;
/*     */ import sun.management.snmp.jvmmib.JvmRTBootClassPathTableMeta;
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
/*     */ public class JvmRTBootClassPathTableMetaImpl
/*     */   extends JvmRTBootClassPathTableMeta
/*     */ {
/*     */   static final long serialVersionUID = -8659886610487538299L;
/*     */   private SnmpTableCache cache;
/*     */   
/*     */   private static class JvmRTBootClassPathTableCache
/*     */     extends SnmpTableCache
/*     */   {
/*     */     static final long serialVersionUID = -2637458695413646098L;
/*     */     private JvmRTBootClassPathTableMetaImpl meta;
/*     */     
/*     */     JvmRTBootClassPathTableCache(JvmRTBootClassPathTableMetaImpl paramJvmRTBootClassPathTableMetaImpl, long paramLong)
/*     */     {
/*  88 */       this.meta = paramJvmRTBootClassPathTableMetaImpl;
/*  89 */       this.validity = paramLong;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public SnmpTableHandler getTableHandler()
/*     */     {
/*  96 */       Map localMap = JvmContextFactory.getUserData();
/*  97 */       return getTableDatas(localMap);
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
/*     */     protected SnmpCachedData updateCachedDatas(Object paramObject)
/*     */     {
/* 110 */       String[] arrayOfString = JvmRuntimeImpl.getBootClassPath(paramObject);
/*     */       
/*     */ 
/* 113 */       long l = System.currentTimeMillis();
/* 114 */       int i = arrayOfString.length;
/*     */       
/* 116 */       SnmpOid[] arrayOfSnmpOid = new SnmpOid[i];
/*     */       
/* 118 */       for (int j = 0; j < i; j++) {
/* 119 */         arrayOfSnmpOid[j] = new SnmpOid(j + 1);
/*     */       }
/*     */       
/* 122 */       return new SnmpCachedData(l, arrayOfSnmpOid, arrayOfString);
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
/*     */   public JvmRTBootClassPathTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/* 135 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/* 136 */     this.cache = new JvmRTBootClassPathTableCache(this, -1L);
/*     */   }
/*     */   
/*     */ 
/*     */   protected SnmpOid getNextOid(Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 143 */     return getNextOid(null, paramObject);
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 149 */     boolean bool = log.isDebugOn();
/* 150 */     if (bool) { log.debug("getNextOid", "previous=" + paramSnmpOid);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 155 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/* 156 */     if (localSnmpTableHandler == null)
/*     */     {
/*     */ 
/*     */ 
/* 160 */       if (bool) log.debug("getNextOid", "handler is null!");
/* 161 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 166 */     SnmpOid localSnmpOid = localSnmpTableHandler.getNext(paramSnmpOid);
/* 167 */     if (bool) { log.debug("*** **** **** **** getNextOid", "next=" + localSnmpOid);
/*     */     }
/*     */     
/*     */ 
/* 171 */     if (localSnmpOid == null) {
/* 172 */       throw new SnmpStatusException(224);
/*     */     }
/* 174 */     return localSnmpOid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
/*     */   {
/* 183 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/*     */     
/*     */ 
/*     */ 
/* 187 */     if (localSnmpTableHandler == null) {
/* 188 */       return false;
/*     */     }
/* 190 */     return localSnmpTableHandler.contains(paramSnmpOid);
/*     */   }
/*     */   
/*     */   public Object getEntry(SnmpOid paramSnmpOid)
/*     */     throws SnmpStatusException
/*     */   {
/* 196 */     boolean bool = log.isDebugOn();
/* 197 */     if (bool) log.debug("getEntry", "oid [" + paramSnmpOid + "]");
/* 198 */     if ((paramSnmpOid == null) || (paramSnmpOid.getLength() != 1)) {
/* 199 */       if (bool) log.debug("getEntry", "Invalid oid [" + paramSnmpOid + "]");
/* 200 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 205 */     Map localMap = JvmContextFactory.getUserData();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 215 */     String str = "JvmRTBootClassPathTable.entry." + paramSnmpOid.toString();
/*     */     
/*     */ 
/*     */ 
/* 219 */     if (localMap != null) {
/* 220 */       localObject1 = localMap.get(str);
/* 221 */       if (localObject1 != null) {
/* 222 */         if (bool)
/* 223 */           log.debug("getEntry", "Entry is already in the cache");
/* 224 */         return localObject1;
/*     */       }
/* 226 */       if (bool) { log.debug("getEntry", "Entry is not in the cache");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 233 */     Object localObject1 = getHandler(localMap);
/*     */     
/*     */ 
/*     */ 
/* 237 */     if (localObject1 == null) {
/* 238 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 242 */     Object localObject2 = ((SnmpTableHandler)localObject1).getData(paramSnmpOid);
/*     */     
/*     */ 
/*     */ 
/* 246 */     if (localObject2 == null) {
/* 247 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 252 */     if (bool) {
/* 253 */       log.debug("getEntry", "data is a: " + localObject2.getClass().getName());
/*     */     }
/*     */     
/* 256 */     JvmRTBootClassPathEntryImpl localJvmRTBootClassPathEntryImpl = new JvmRTBootClassPathEntryImpl((String)localObject2, (int)paramSnmpOid.getOidArc(0));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 261 */     if ((localMap != null) && (localJvmRTBootClassPathEntryImpl != null)) {
/* 262 */       localMap.put(str, localJvmRTBootClassPathEntryImpl);
/*     */     }
/*     */     
/* 265 */     return localJvmRTBootClassPathEntryImpl;
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
/* 288 */       localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmRTBootClassPathTable.handler");
/* 289 */       if (localSnmpTableHandler != null) { return localSnmpTableHandler;
/*     */       }
/*     */     }
/*     */     
/* 293 */     SnmpTableHandler localSnmpTableHandler = this.cache.getTableHandler();
/*     */     
/* 295 */     if ((localMap != null) && (localSnmpTableHandler != null)) {
/* 296 */       localMap.put("JvmRTBootClassPathTable.handler", localSnmpTableHandler);
/*     */     }
/* 298 */     return localSnmpTableHandler;
/*     */   }
/*     */   
/* 301 */   static final MibLogger log = new MibLogger(JvmRTBootClassPathTableMetaImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmRTBootClassPathTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.util.Map;
/*     */ import sun.management.snmp.jvmmib.JvmRTLibraryPathTableMeta;
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
/*     */ public class JvmRTLibraryPathTableMetaImpl
/*     */   extends JvmRTLibraryPathTableMeta
/*     */ {
/*     */   static final long serialVersionUID = 6713252710712502068L;
/*     */   private SnmpTableCache cache;
/*     */   
/*     */   private static class JvmRTLibraryPathTableCache
/*     */     extends SnmpTableCache
/*     */   {
/*     */     static final long serialVersionUID = 2035304445719393195L;
/*     */     private JvmRTLibraryPathTableMetaImpl meta;
/*     */     
/*     */     JvmRTLibraryPathTableCache(JvmRTLibraryPathTableMetaImpl paramJvmRTLibraryPathTableMetaImpl, long paramLong)
/*     */     {
/*  86 */       this.meta = paramJvmRTLibraryPathTableMetaImpl;
/*  87 */       this.validity = paramLong;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected SnmpCachedData updateCachedDatas(Object paramObject)
/*     */     {
/* 108 */       String[] arrayOfString = JvmRuntimeImpl.getLibraryPath(paramObject);
/*     */       
/*     */ 
/* 111 */       long l = System.currentTimeMillis();
/* 112 */       int i = arrayOfString.length;
/*     */       
/* 114 */       SnmpOid[] arrayOfSnmpOid = new SnmpOid[i];
/*     */       
/* 116 */       for (int j = 0; j < i; j++) {
/* 117 */         arrayOfSnmpOid[j] = new SnmpOid(j + 1);
/*     */       }
/*     */       
/* 120 */       return new SnmpCachedData(l, arrayOfSnmpOid, arrayOfString);
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
/*     */   public JvmRTLibraryPathTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/* 133 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/* 134 */     this.cache = new JvmRTLibraryPathTableCache(this, -1L);
/*     */   }
/*     */   
/*     */ 
/*     */   protected SnmpOid getNextOid(Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 141 */     return getNextOid(null, paramObject);
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 147 */     boolean bool = log.isDebugOn();
/* 148 */     if (bool) { log.debug("getNextOid", "previous=" + paramSnmpOid);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 153 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/* 154 */     if (localSnmpTableHandler == null)
/*     */     {
/*     */ 
/*     */ 
/* 158 */       if (bool) log.debug("getNextOid", "handler is null!");
/* 159 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 164 */     SnmpOid localSnmpOid = localSnmpTableHandler.getNext(paramSnmpOid);
/* 165 */     if (bool) { log.debug("*** **** **** **** getNextOid", "next=" + localSnmpOid);
/*     */     }
/*     */     
/*     */ 
/* 169 */     if (localSnmpOid == null) {
/* 170 */       throw new SnmpStatusException(224);
/*     */     }
/* 172 */     return localSnmpOid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
/*     */   {
/* 181 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/*     */     
/*     */ 
/*     */ 
/* 185 */     if (localSnmpTableHandler == null) {
/* 186 */       return false;
/*     */     }
/* 188 */     return localSnmpTableHandler.contains(paramSnmpOid);
/*     */   }
/*     */   
/*     */   public Object getEntry(SnmpOid paramSnmpOid)
/*     */     throws SnmpStatusException
/*     */   {
/* 194 */     boolean bool = log.isDebugOn();
/* 195 */     if (bool) log.debug("getEntry", "oid [" + paramSnmpOid + "]");
/* 196 */     if ((paramSnmpOid == null) || (paramSnmpOid.getLength() != 1)) {
/* 197 */       if (bool) log.debug("getEntry", "Invalid oid [" + paramSnmpOid + "]");
/* 198 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 203 */     Map localMap = JvmContextFactory.getUserData();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */     String str = "JvmRTLibraryPathTable.entry." + paramSnmpOid.toString();
/*     */     
/*     */ 
/*     */ 
/* 217 */     if (localMap != null) {
/* 218 */       localObject1 = localMap.get(str);
/* 219 */       if (localObject1 != null) {
/* 220 */         if (bool)
/* 221 */           log.debug("getEntry", "Entry is already in the cache");
/* 222 */         return localObject1; }
/* 223 */       if (bool) { log.debug("getEntry", "Entry is not in the cache");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 231 */     Object localObject1 = getHandler(localMap);
/*     */     
/*     */ 
/*     */ 
/* 235 */     if (localObject1 == null) {
/* 236 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 240 */     Object localObject2 = ((SnmpTableHandler)localObject1).getData(paramSnmpOid);
/*     */     
/*     */ 
/*     */ 
/* 244 */     if (localObject2 == null) {
/* 245 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 250 */     if (bool) { log.debug("getEntry", "data is a: " + localObject2
/* 251 */         .getClass().getName());
/*     */     }
/* 253 */     JvmRTLibraryPathEntryImpl localJvmRTLibraryPathEntryImpl = new JvmRTLibraryPathEntryImpl((String)localObject2, (int)paramSnmpOid.getOidArc(0));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 258 */     if ((localMap != null) && (localJvmRTLibraryPathEntryImpl != null)) {
/* 259 */       localMap.put(str, localJvmRTLibraryPathEntryImpl);
/*     */     }
/*     */     
/* 262 */     return localJvmRTLibraryPathEntryImpl;
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
/* 279 */     if ((paramObject instanceof Map)) localMap = (Map)Util.cast(paramObject); else {
/* 280 */       localMap = null;
/*     */     }
/*     */     
/* 283 */     if (localMap != null)
/*     */     {
/* 285 */       localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmRTLibraryPathTable.handler");
/* 286 */       if (localSnmpTableHandler != null) { return localSnmpTableHandler;
/*     */       }
/*     */     }
/*     */     
/* 290 */     SnmpTableHandler localSnmpTableHandler = this.cache.getTableHandler();
/*     */     
/* 292 */     if ((localMap != null) && (localSnmpTableHandler != null)) {
/* 293 */       localMap.put("JvmRTLibraryPathTable.handler", localSnmpTableHandler);
/*     */     }
/* 295 */     return localSnmpTableHandler;
/*     */   }
/*     */   
/* 298 */   static final MibLogger log = new MibLogger(JvmRTLibraryPathTableMetaImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmRTLibraryPathTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
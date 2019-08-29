/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.util.Map;
/*     */ import sun.management.snmp.jvmmib.JvmRTClassPathTableMeta;
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
/*     */ public class JvmRTClassPathTableMetaImpl
/*     */   extends JvmRTClassPathTableMeta
/*     */ {
/*     */   static final long serialVersionUID = -6914494148818455166L;
/*     */   private SnmpTableCache cache;
/*     */   
/*     */   private static class JvmRTClassPathTableCache
/*     */     extends SnmpTableCache
/*     */   {
/*     */     static final long serialVersionUID = 3805032372592117315L;
/*     */     private JvmRTClassPathTableMetaImpl meta;
/*     */     
/*     */     JvmRTClassPathTableCache(JvmRTClassPathTableMetaImpl paramJvmRTClassPathTableMetaImpl, long paramLong)
/*     */     {
/*  87 */       this.meta = paramJvmRTClassPathTableMetaImpl;
/*  88 */       this.validity = paramLong;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public SnmpTableHandler getTableHandler()
/*     */     {
/*  95 */       Map localMap = JvmContextFactory.getUserData();
/*  96 */       return getTableDatas(localMap);
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
/* 109 */       String[] arrayOfString = JvmRuntimeImpl.getClassPath(paramObject);
/*     */       
/*     */ 
/* 112 */       long l = System.currentTimeMillis();
/* 113 */       int i = arrayOfString.length;
/*     */       
/* 115 */       SnmpOid[] arrayOfSnmpOid = new SnmpOid[i];
/*     */       
/* 117 */       for (int j = 0; j < i; j++) {
/* 118 */         arrayOfSnmpOid[j] = new SnmpOid(j + 1);
/*     */       }
/*     */       
/* 121 */       return new SnmpCachedData(l, arrayOfSnmpOid, arrayOfString);
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
/*     */   public JvmRTClassPathTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/* 134 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/* 135 */     this.cache = new JvmRTClassPathTableCache(this, -1L);
/*     */   }
/*     */   
/*     */ 
/*     */   protected SnmpOid getNextOid(Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 142 */     return getNextOid(null, paramObject);
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 148 */     boolean bool = log.isDebugOn();
/* 149 */     if (bool) { log.debug("getNextOid", "previous=" + paramSnmpOid);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 154 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/* 155 */     if (localSnmpTableHandler == null)
/*     */     {
/*     */ 
/*     */ 
/* 159 */       if (bool) log.debug("getNextOid", "handler is null!");
/* 160 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 165 */     SnmpOid localSnmpOid = localSnmpTableHandler.getNext(paramSnmpOid);
/* 166 */     if (bool) { log.debug("*** **** **** **** getNextOid", "next=" + localSnmpOid);
/*     */     }
/*     */     
/*     */ 
/* 170 */     if (localSnmpOid == null) {
/* 171 */       throw new SnmpStatusException(224);
/*     */     }
/* 173 */     return localSnmpOid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
/*     */   {
/* 182 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/*     */     
/*     */ 
/*     */ 
/* 186 */     if (localSnmpTableHandler == null) {
/* 187 */       return false;
/*     */     }
/* 189 */     return localSnmpTableHandler.contains(paramSnmpOid);
/*     */   }
/*     */   
/*     */   public Object getEntry(SnmpOid paramSnmpOid)
/*     */     throws SnmpStatusException
/*     */   {
/* 195 */     boolean bool = log.isDebugOn();
/* 196 */     if (bool) log.debug("getEntry", "oid [" + paramSnmpOid + "]");
/* 197 */     if ((paramSnmpOid == null) || (paramSnmpOid.getLength() != 1)) {
/* 198 */       if (bool) log.debug("getEntry", "Invalid oid [" + paramSnmpOid + "]");
/* 199 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 204 */     Map localMap = JvmContextFactory.getUserData();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */     String str = "JvmRTClassPathTable.entry." + paramSnmpOid.toString();
/*     */     
/*     */ 
/*     */ 
/* 218 */     if (localMap != null) {
/* 219 */       localObject1 = localMap.get(str);
/* 220 */       if (localObject1 != null) {
/* 221 */         if (bool)
/* 222 */           log.debug("getEntry", "Entry is already in the cache");
/* 223 */         return localObject1;
/*     */       }
/* 225 */       if (bool) { log.debug("getEntry", "Entry is not in the cache");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 232 */     Object localObject1 = getHandler(localMap);
/*     */     
/*     */ 
/*     */ 
/* 236 */     if (localObject1 == null) {
/* 237 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 241 */     Object localObject2 = ((SnmpTableHandler)localObject1).getData(paramSnmpOid);
/*     */     
/*     */ 
/*     */ 
/* 245 */     if (localObject2 == null) {
/* 246 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 251 */     if (bool) {
/* 252 */       log.debug("getEntry", "data is a: " + localObject2.getClass().getName());
/*     */     }
/* 254 */     JvmRTClassPathEntryImpl localJvmRTClassPathEntryImpl = new JvmRTClassPathEntryImpl((String)localObject2, (int)paramSnmpOid.getOidArc(0));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 259 */     if ((localMap != null) && (localJvmRTClassPathEntryImpl != null)) {
/* 260 */       localMap.put(str, localJvmRTClassPathEntryImpl);
/*     */     }
/*     */     
/* 263 */     return localJvmRTClassPathEntryImpl;
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
/* 280 */     if ((paramObject instanceof Map)) localMap = (Map)Util.cast(paramObject); else {
/* 281 */       localMap = null;
/*     */     }
/*     */     
/* 284 */     if (localMap != null)
/*     */     {
/* 286 */       localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmRTClassPathTable.handler");
/* 287 */       if (localSnmpTableHandler != null) { return localSnmpTableHandler;
/*     */       }
/*     */     }
/*     */     
/* 291 */     SnmpTableHandler localSnmpTableHandler = this.cache.getTableHandler();
/*     */     
/* 293 */     if ((localMap != null) && (localSnmpTableHandler != null)) {
/* 294 */       localMap.put("JvmRTClassPathTable.handler", localSnmpTableHandler);
/*     */     }
/* 296 */     return localSnmpTableHandler;
/*     */   }
/*     */   
/* 299 */   static final MibLogger log = new MibLogger(JvmRTClassPathTableMetaImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmRTClassPathTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
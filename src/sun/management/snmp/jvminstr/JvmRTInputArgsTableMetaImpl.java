/*     */ package sun.management.snmp.jvminstr;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import com.sun.jmx.snmp.SnmpOid;
/*     */ import com.sun.jmx.snmp.SnmpStatusException;
/*     */ import com.sun.jmx.snmp.agent.SnmpMib;
/*     */ import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
/*     */ import java.util.Map;
/*     */ import sun.management.snmp.jvmmib.JvmRTInputArgsTableMeta;
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
/*     */ public class JvmRTInputArgsTableMetaImpl
/*     */   extends JvmRTInputArgsTableMeta
/*     */ {
/*     */   static final long serialVersionUID = -2083438094888099238L;
/*     */   private SnmpTableCache cache;
/*     */   
/*     */   private static class JvmRTInputArgsTableCache
/*     */     extends SnmpTableCache
/*     */   {
/*     */     static final long serialVersionUID = 1693751105464785192L;
/*     */     private JvmRTInputArgsTableMetaImpl meta;
/*     */     
/*     */     JvmRTInputArgsTableCache(JvmRTInputArgsTableMetaImpl paramJvmRTInputArgsTableMetaImpl, long paramLong)
/*     */     {
/*  87 */       this.meta = paramJvmRTInputArgsTableMetaImpl;
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
/*     */     protected SnmpCachedData updateCachedDatas(Object paramObject)
/*     */     {
/* 108 */       String[] arrayOfString = JvmRuntimeImpl.getInputArguments(paramObject);
/*     */       
/*     */ 
/* 111 */       long l = System.currentTimeMillis();
/* 112 */       SnmpOid[] arrayOfSnmpOid = new SnmpOid[arrayOfString.length];
/*     */       
/* 114 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 115 */         arrayOfSnmpOid[i] = new SnmpOid(i + 1);
/*     */       }
/*     */       
/* 118 */       return new SnmpCachedData(l, arrayOfSnmpOid, arrayOfString);
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
/*     */   public JvmRTInputArgsTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
/*     */   {
/* 131 */     super(paramSnmpMib, paramSnmpStandardObjectServer);
/* 132 */     this.cache = new JvmRTInputArgsTableCache(this, -1L);
/*     */   }
/*     */   
/*     */ 
/*     */   protected SnmpOid getNextOid(Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 139 */     return getNextOid(null, paramObject);
/*     */   }
/*     */   
/*     */   protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
/*     */     throws SnmpStatusException
/*     */   {
/* 145 */     boolean bool = log.isDebugOn();
/* 146 */     if (bool) { log.debug("getNextOid", "previous=" + paramSnmpOid);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 151 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/* 152 */     if (localSnmpTableHandler == null)
/*     */     {
/*     */ 
/*     */ 
/* 156 */       if (bool) log.debug("getNextOid", "handler is null!");
/* 157 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 162 */     SnmpOid localSnmpOid = localSnmpTableHandler.getNext(paramSnmpOid);
/* 163 */     if (bool) { log.debug("*** **** **** **** getNextOid", "next=" + localSnmpOid);
/*     */     }
/*     */     
/*     */ 
/* 167 */     if (localSnmpOid == null) {
/* 168 */       throw new SnmpStatusException(224);
/*     */     }
/* 170 */     return localSnmpOid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
/*     */   {
/* 179 */     SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
/*     */     
/*     */ 
/*     */ 
/* 183 */     if (localSnmpTableHandler == null) {
/* 184 */       return false;
/*     */     }
/* 186 */     return localSnmpTableHandler.contains(paramSnmpOid);
/*     */   }
/*     */   
/*     */   public Object getEntry(SnmpOid paramSnmpOid)
/*     */     throws SnmpStatusException
/*     */   {
/* 192 */     boolean bool = log.isDebugOn();
/* 193 */     if (bool) log.debug("getEntry", "oid [" + paramSnmpOid + "]");
/* 194 */     if ((paramSnmpOid == null) || (paramSnmpOid.getLength() != 1)) {
/* 195 */       if (bool) log.debug("getEntry", "Invalid oid [" + paramSnmpOid + "]");
/* 196 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 201 */     Map localMap = JvmContextFactory.getUserData();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 211 */     String str = "JvmRTInputArgsTable.entry." + paramSnmpOid.toString();
/*     */     
/*     */ 
/*     */ 
/* 215 */     if (localMap != null) {
/* 216 */       localObject1 = localMap.get(str);
/* 217 */       if (localObject1 != null) {
/* 218 */         if (bool)
/* 219 */           log.debug("getEntry", "Entry is already in the cache");
/* 220 */         return localObject1; }
/* 221 */       if (bool) { log.debug("getEntry", "Entry is not in the cache");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 228 */     Object localObject1 = getHandler(localMap);
/*     */     
/*     */ 
/*     */ 
/* 232 */     if (localObject1 == null) {
/* 233 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/* 237 */     Object localObject2 = ((SnmpTableHandler)localObject1).getData(paramSnmpOid);
/*     */     
/*     */ 
/*     */ 
/* 241 */     if (localObject2 == null) {
/* 242 */       throw new SnmpStatusException(224);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 247 */     if (bool) { log.debug("getEntry", "data is a: " + localObject2
/* 248 */         .getClass().getName());
/*     */     }
/* 250 */     JvmRTInputArgsEntryImpl localJvmRTInputArgsEntryImpl = new JvmRTInputArgsEntryImpl((String)localObject2, (int)paramSnmpOid.getOidArc(0));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 255 */     if ((localMap != null) && (localJvmRTInputArgsEntryImpl != null)) {
/* 256 */       localMap.put(str, localJvmRTInputArgsEntryImpl);
/*     */     }
/*     */     
/* 259 */     return localJvmRTInputArgsEntryImpl;
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
/* 276 */     if ((paramObject instanceof Map)) localMap = (Map)Util.cast(paramObject); else {
/* 277 */       localMap = null;
/*     */     }
/*     */     
/* 280 */     if (localMap != null)
/*     */     {
/* 282 */       localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmRTInputArgsTable.handler");
/* 283 */       if (localSnmpTableHandler != null) { return localSnmpTableHandler;
/*     */       }
/*     */     }
/*     */     
/* 287 */     SnmpTableHandler localSnmpTableHandler = this.cache.getTableHandler();
/*     */     
/* 289 */     if ((localMap != null) && (localSnmpTableHandler != null)) {
/* 290 */       localMap.put("JvmRTInputArgsTable.handler", localSnmpTableHandler);
/*     */     }
/* 292 */     return localSnmpTableHandler;
/*     */   }
/*     */   
/* 295 */   static final MibLogger log = new MibLogger(JvmRTInputArgsTableMetaImpl.class);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\JvmRTInputArgsTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
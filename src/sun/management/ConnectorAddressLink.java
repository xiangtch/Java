/*     */ package sun.management;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import sun.management.counter.Counter;
/*     */ import sun.management.counter.Units;
/*     */ import sun.management.counter.perf.PerfInstrumentation;
/*     */ import sun.misc.Perf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConnectorAddressLink
/*     */ {
/*     */   private static final String CONNECTOR_ADDRESS_COUNTER = "sun.management.JMXConnectorServer.address";
/*     */   private static final String REMOTE_CONNECTOR_COUNTER_PREFIX = "sun.management.JMXConnectorServer.";
/*  81 */   private static AtomicInteger counter = new AtomicInteger();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void export(String paramString)
/*     */   {
/*  91 */     if ((paramString == null) || (paramString.length() == 0)) {
/*  92 */       throw new IllegalArgumentException("address not specified");
/*     */     }
/*  94 */     Perf localPerf = Perf.getPerf();
/*  95 */     localPerf.createString("sun.management.JMXConnectorServer.address", 1, Units.STRING
/*  96 */       .intValue(), paramString);
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
/*     */   public static String importFrom(int paramInt)
/*     */     throws IOException
/*     */   {
/* 113 */     Perf localPerf = Perf.getPerf();
/*     */     ByteBuffer localByteBuffer;
/*     */     try {
/* 116 */       localByteBuffer = localPerf.attach(paramInt, "r");
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 118 */       throw new IOException(localIllegalArgumentException.getMessage());
/*     */     }
/*     */     
/* 121 */     List localList = new PerfInstrumentation(localByteBuffer).findByPattern("sun.management.JMXConnectorServer.address");
/* 122 */     Iterator localIterator = localList.iterator();
/* 123 */     if (localIterator.hasNext()) {
/* 124 */       Counter localCounter = (Counter)localIterator.next();
/* 125 */       return (String)localCounter.getValue();
/*     */     }
/* 127 */     return null;
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
/*     */   public static void exportRemote(Map<String, String> paramMap)
/*     */   {
/* 140 */     int i = counter.getAndIncrement();
/* 141 */     Perf localPerf = Perf.getPerf();
/* 142 */     for (Entry localEntry : paramMap.entrySet()) {
/* 143 */       localPerf.createString("sun.management.JMXConnectorServer." + i + "." + 
/* 144 */         (String)localEntry.getKey(), 1, Units.STRING.intValue(), (String)localEntry.getValue());
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
/*     */   public static Map<String, String> importRemoteFrom(int paramInt)
/*     */     throws IOException
/*     */   {
/* 163 */     Perf localPerf = Perf.getPerf();
/*     */     ByteBuffer localByteBuffer;
/*     */     try {
/* 166 */       localByteBuffer = localPerf.attach(paramInt, "r");
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 168 */       throw new IOException(localIllegalArgumentException.getMessage());
/*     */     }
/* 170 */     List localList = new PerfInstrumentation(localByteBuffer).getAllCounters();
/* 171 */     HashMap localHashMap = new HashMap();
/* 172 */     for (Counter localCounter : localList) {
/* 173 */       String str = localCounter.getName();
/* 174 */       if ((str.startsWith("sun.management.JMXConnectorServer.")) && 
/* 175 */         (!str.equals("sun.management.JMXConnectorServer.address"))) {
/* 176 */         localHashMap.put(str, localCounter.getValue().toString());
/*     */       }
/*     */     }
/* 179 */     return localHashMap;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\ConnectorAddressLink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
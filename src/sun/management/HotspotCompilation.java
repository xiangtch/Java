/*     */ package sun.management;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import sun.management.counter.Counter;
/*     */ import sun.management.counter.LongCounter;
/*     */ import sun.management.counter.StringCounter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class HotspotCompilation
/*     */   implements HotspotCompilationMBean
/*     */ {
/*     */   private VMManagement jvm;
/*     */   private static final String JAVA_CI = "java.ci.";
/*     */   private static final String COM_SUN_CI = "com.sun.ci.";
/*     */   private static final String SUN_CI = "sun.ci.";
/*     */   private static final String CI_COUNTER_NAME_PATTERN = "java.ci.|com.sun.ci.|sun.ci.";
/*     */   private LongCounter compilerThreads;
/*     */   private LongCounter totalCompiles;
/*     */   private LongCounter totalBailouts;
/*     */   private LongCounter totalInvalidates;
/*     */   private LongCounter nmethodCodeSize;
/*     */   private LongCounter nmethodSize;
/*     */   private StringCounter lastMethod;
/*     */   private LongCounter lastSize;
/*     */   private LongCounter lastType;
/*     */   private StringCounter lastFailedMethod;
/*     */   private LongCounter lastFailedType;
/*     */   private StringCounter lastInvalidatedMethod;
/*     */   private LongCounter lastInvalidatedType;
/*     */   private CompilerThreadInfo[] threads;
/*     */   private int numActiveThreads;
/*     */   private Map<String, Counter> counters;
/*     */   
/*     */   HotspotCompilation(VMManagement paramVMManagement)
/*     */   {
/*  53 */     this.jvm = paramVMManagement;
/*  54 */     initCompilerCounters();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private class CompilerThreadInfo
/*     */   {
/*     */     int index;
/*     */     
/*     */ 
/*     */ 
/*     */     String name;
/*     */     
/*     */ 
/*     */ 
/*     */     StringCounter method;
/*     */     
/*     */ 
/*     */ 
/*     */     LongCounter type;
/*     */     
/*     */ 
/*     */     LongCounter compiles;
/*     */     
/*     */ 
/*     */     LongCounter time;
/*     */     
/*     */ 
/*     */ 
/*     */     CompilerThreadInfo(String paramString, int paramInt)
/*     */     {
/*  86 */       String str = paramString + "." + paramInt + ".";
/*  87 */       this.name = (paramString + "-" + paramInt);
/*  88 */       this.method = ((StringCounter)HotspotCompilation.this.lookup(str + "method"));
/*  89 */       this.type = ((LongCounter)HotspotCompilation.this.lookup(str + "type"));
/*  90 */       this.compiles = ((LongCounter)HotspotCompilation.this.lookup(str + "compiles"));
/*  91 */       this.time = ((LongCounter)HotspotCompilation.this.lookup(str + "time"));
/*     */     }
/*     */     
/*  94 */     CompilerThreadInfo(String paramString) { String str = paramString + ".";
/*  95 */       this.name = paramString;
/*  96 */       this.method = ((StringCounter)HotspotCompilation.this.lookup(str + "method"));
/*  97 */       this.type = ((LongCounter)HotspotCompilation.this.lookup(str + "type"));
/*  98 */       this.compiles = ((LongCounter)HotspotCompilation.this.lookup(str + "compiles"));
/*  99 */       this.time = ((LongCounter)HotspotCompilation.this.lookup(str + "time"));
/*     */     }
/*     */     
/*     */     CompilerThreadStat getCompilerThreadStat()
/*     */     {
/* 104 */       MethodInfo localMethodInfo = new MethodInfo(this.method.stringValue(), (int)this.type.longValue(), -1);
/*     */       
/* 106 */       return new CompilerThreadStat(this.name, this.compiles
/* 107 */         .longValue(), this.time
/* 108 */         .longValue(), localMethodInfo);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Counter lookup(String paramString)
/*     */   {
/* 117 */     Counter localCounter = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */     if ((localCounter = (Counter)this.counters.get("sun.ci." + paramString)) != null) {
/* 124 */       return localCounter;
/*     */     }
/* 126 */     if ((localCounter = (Counter)this.counters.get("com.sun.ci." + paramString)) != null) {
/* 127 */       return localCounter;
/*     */     }
/* 129 */     if ((localCounter = (Counter)this.counters.get("java.ci." + paramString)) != null) {
/* 130 */       return localCounter;
/*     */     }
/*     */     
/*     */ 
/* 134 */     throw new AssertionError("Counter " + paramString + " does not exist");
/*     */   }
/*     */   
/*     */   private void initCompilerCounters()
/*     */   {
/* 139 */     this.counters = new TreeMap();
/* 140 */     for (Counter localCounter : getInternalCompilerCounters()) {
/* 141 */       this.counters.put(localCounter.getName(), localCounter);
/*     */     }
/*     */     
/* 144 */     this.compilerThreads = ((LongCounter)lookup("threads"));
/* 145 */     this.totalCompiles = ((LongCounter)lookup("totalCompiles"));
/* 146 */     this.totalBailouts = ((LongCounter)lookup("totalBailouts"));
/* 147 */     this.totalInvalidates = ((LongCounter)lookup("totalInvalidates"));
/* 148 */     this.nmethodCodeSize = ((LongCounter)lookup("nmethodCodeSize"));
/* 149 */     this.nmethodSize = ((LongCounter)lookup("nmethodSize"));
/* 150 */     this.lastMethod = ((StringCounter)lookup("lastMethod"));
/* 151 */     this.lastSize = ((LongCounter)lookup("lastSize"));
/* 152 */     this.lastType = ((LongCounter)lookup("lastType"));
/* 153 */     this.lastFailedMethod = ((StringCounter)lookup("lastFailedMethod"));
/* 154 */     this.lastFailedType = ((LongCounter)lookup("lastFailedType"));
/* 155 */     this.lastInvalidatedMethod = ((StringCounter)lookup("lastInvalidatedMethod"));
/* 156 */     this.lastInvalidatedType = ((LongCounter)lookup("lastInvalidatedType"));
/*     */     
/* 158 */     this.numActiveThreads = ((int)this.compilerThreads.longValue());
/*     */     
/*     */ 
/* 161 */     this.threads = new CompilerThreadInfo[this.numActiveThreads + 1];
/*     */     
/*     */ 
/* 164 */     if (this.counters.containsKey("sun.ci.adapterThread.compiles")) {
/* 165 */       this.threads[0] = new CompilerThreadInfo("adapterThread", 0);
/* 166 */       this.numActiveThreads += 1;
/*     */     } else {
/* 168 */       this.threads[0] = null;
/*     */     }
/*     */     
/* 171 */     for (int i = 1; i < this.threads.length; i++) {
/* 172 */       this.threads[i] = new CompilerThreadInfo("compilerThread", i - 1);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getCompilerThreadCount() {
/* 177 */     return this.numActiveThreads;
/*     */   }
/*     */   
/*     */   public long getTotalCompileCount() {
/* 181 */     return this.totalCompiles.longValue();
/*     */   }
/*     */   
/*     */   public long getBailoutCompileCount() {
/* 185 */     return this.totalBailouts.longValue();
/*     */   }
/*     */   
/*     */   public long getInvalidatedCompileCount() {
/* 189 */     return this.totalInvalidates.longValue();
/*     */   }
/*     */   
/*     */   public long getCompiledMethodCodeSize() {
/* 193 */     return this.nmethodCodeSize.longValue();
/*     */   }
/*     */   
/*     */   public long getCompiledMethodSize() {
/* 197 */     return this.nmethodSize.longValue();
/*     */   }
/*     */   
/*     */   public List<CompilerThreadStat> getCompilerThreadStats() {
/* 201 */     ArrayList localArrayList = new ArrayList(this.threads.length);
/* 202 */     int i = 0;
/* 203 */     if (this.threads[0] == null) {}
/*     */     
/* 205 */     for (i = 1; 
/*     */         
/* 207 */         i < this.threads.length; i++) {
/* 208 */       localArrayList.add(this.threads[i].getCompilerThreadStat());
/*     */     }
/* 210 */     return localArrayList;
/*     */   }
/*     */   
/*     */   public MethodInfo getLastCompile() {
/* 214 */     return new MethodInfo(this.lastMethod.stringValue(), 
/* 215 */       (int)this.lastType.longValue(), 
/* 216 */       (int)this.lastSize.longValue());
/*     */   }
/*     */   
/*     */   public MethodInfo getFailedCompile() {
/* 220 */     return new MethodInfo(this.lastFailedMethod.stringValue(), 
/* 221 */       (int)this.lastFailedType.longValue(), -1);
/*     */   }
/*     */   
/*     */   public MethodInfo getInvalidatedCompile()
/*     */   {
/* 226 */     return new MethodInfo(this.lastInvalidatedMethod.stringValue(), 
/* 227 */       (int)this.lastInvalidatedType.longValue(), -1);
/*     */   }
/*     */   
/*     */   public List<Counter> getInternalCompilerCounters()
/*     */   {
/* 232 */     return this.jvm.getInternalCounters("java.ci.|com.sun.ci.|sun.ci.");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\HotspotCompilation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
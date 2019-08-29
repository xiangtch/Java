/*     */ package sun.management;
/*     */ 
/*     */ import com.sun.management.GcInfo;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.lang.management.MemoryUsage;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Map;
/*     */ import javax.management.openmbean.CompositeData;
/*     */ import javax.management.openmbean.CompositeDataSupport;
/*     */ import javax.management.openmbean.CompositeType;
/*     */ import javax.management.openmbean.OpenDataException;
/*     */ import javax.management.openmbean.OpenType;
/*     */ import javax.management.openmbean.SimpleType;
/*     */ import javax.management.openmbean.TabularData;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GcInfoCompositeData
/*     */   extends LazyCompositeData
/*     */ {
/*     */   private final GcInfo info;
/*     */   private final GcInfoBuilder builder;
/*     */   private final Object[] gcExtItemValues;
/*     */   private static final String ID = "id";
/*     */   private static final String START_TIME = "startTime";
/*     */   private static final String END_TIME = "endTime";
/*     */   private static final String DURATION = "duration";
/*     */   private static final String MEMORY_USAGE_BEFORE_GC = "memoryUsageBeforeGc";
/*     */   private static final String MEMORY_USAGE_AFTER_GC = "memoryUsageAfterGc";
/*     */   
/*     */   public GcInfoCompositeData(GcInfo paramGcInfo, GcInfoBuilder paramGcInfoBuilder, Object[] paramArrayOfObject)
/*     */   {
/*  62 */     this.info = paramGcInfo;
/*  63 */     this.builder = paramGcInfoBuilder;
/*  64 */     this.gcExtItemValues = paramArrayOfObject;
/*     */   }
/*     */   
/*     */   public GcInfo getGcInfo() {
/*  68 */     return this.info;
/*     */   }
/*     */   
/*     */   public static CompositeData toCompositeData(GcInfo paramGcInfo) {
/*  72 */     GcInfoBuilder localGcInfoBuilder = (GcInfoBuilder)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public GcInfoBuilder run() {
/*     */         try {
/*  75 */           Class localClass = Class.forName("com.sun.management.GcInfo");
/*  76 */           Field localField = localClass.getDeclaredField("builder");
/*  77 */           localField.setAccessible(true);
/*  78 */           return (GcInfoBuilder)localField.get(this.val$info);
/*     */         } catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException localClassNotFoundException) {}
/*  80 */         return null;
/*     */       }
/*     */       
/*  83 */     });
/*  84 */     Object[] arrayOfObject = (Object[])AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object[] run() {
/*     */         try {
/*  87 */           Class localClass = Class.forName("com.sun.management.GcInfo");
/*  88 */           Field localField = localClass.getDeclaredField("extAttributes");
/*  89 */           localField.setAccessible(true);
/*  90 */           return (Object[])localField.get(this.val$info);
/*     */         } catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException localClassNotFoundException) {}
/*  92 */         return null;
/*     */       }
/*     */       
/*  95 */     });
/*  96 */     GcInfoCompositeData localGcInfoCompositeData = new GcInfoCompositeData(paramGcInfo, localGcInfoBuilder, arrayOfObject);
/*     */     
/*  98 */     return localGcInfoCompositeData.getCompositeData();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CompositeData getCompositeData()
/*     */   {
/*     */     Object[] arrayOfObject1;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 113 */       arrayOfObject1 = new Object[] { new Long(this.info.getId()), new Long(this.info.getStartTime()), new Long(this.info.getEndTime()), new Long(this.info.getDuration()), memoryUsageMapType.toOpenTypeData(this.info.getMemoryUsageBeforeGc()), memoryUsageMapType.toOpenTypeData(this.info.getMemoryUsageAfterGc()) };
/*     */     }
/*     */     catch (OpenDataException localOpenDataException1)
/*     */     {
/* 117 */       throw new AssertionError(localOpenDataException1);
/*     */     }
/*     */     
/*     */ 
/* 121 */     int i = this.builder.getGcExtItemCount();
/* 122 */     if ((i == 0) && (this.gcExtItemValues != null) && (this.gcExtItemValues.length != 0))
/*     */     {
/* 124 */       throw new AssertionError("Unexpected Gc Extension Item Values");
/*     */     }
/*     */     
/* 127 */     if ((i > 0) && ((this.gcExtItemValues == null) || (i != this.gcExtItemValues.length)))
/*     */     {
/* 129 */       throw new AssertionError("Unmatched Gc Extension Item Values");
/*     */     }
/*     */     
/* 132 */     Object[] arrayOfObject2 = new Object[arrayOfObject1.length + i];
/*     */     
/* 134 */     System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
/*     */     
/*     */ 
/* 137 */     if (i > 0) {
/* 138 */       System.arraycopy(this.gcExtItemValues, 0, arrayOfObject2, arrayOfObject1.length, i);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 143 */       return new CompositeDataSupport(this.builder.getGcInfoCompositeType(), this.builder
/* 144 */         .getItemNames(), arrayOfObject2);
/*     */     }
/*     */     catch (OpenDataException localOpenDataException2)
/*     */     {
/* 148 */       throw new AssertionError(localOpenDataException2);
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
/* 159 */   private static final String[] baseGcInfoItemNames = { "id", "startTime", "endTime", "duration", "memoryUsageBeforeGc", "memoryUsageAfterGc" };
/*     */   
/*     */ 
/*     */ 
/*     */   private static MappedMXBeanType memoryUsageMapType;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 172 */       Method localMethod = GcInfo.class.getMethod("getMemoryUsageBeforeGc", new Class[0]);
/*     */       
/* 174 */       memoryUsageMapType = MappedMXBeanType.getMappedType(localMethod.getGenericReturnType());
/*     */     }
/*     */     catch (NoSuchMethodException|OpenDataException localNoSuchMethodException) {
/* 177 */       throw new AssertionError(localNoSuchMethodException);
/*     */     }
/*     */   }
/*     */   
/*     */   static String[] getBaseGcInfoItemNames() {
/* 182 */     return baseGcInfoItemNames;
/*     */   }
/*     */   
/* 185 */   private static OpenType[] baseGcInfoItemTypes = null;
/*     */   
/* 187 */   static synchronized OpenType[] getBaseGcInfoItemTypes() { if (baseGcInfoItemTypes == null) {
/* 188 */       OpenType localOpenType = memoryUsageMapType.getOpenType();
/* 189 */       baseGcInfoItemTypes = new OpenType[] { SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, localOpenType, localOpenType };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 199 */     return baseGcInfoItemTypes;
/*     */   }
/*     */   
/*     */   public static long getId(CompositeData paramCompositeData) {
/* 203 */     return getLong(paramCompositeData, "id");
/*     */   }
/*     */   
/* 206 */   public static long getStartTime(CompositeData paramCompositeData) { return getLong(paramCompositeData, "startTime"); }
/*     */   
/*     */   public static long getEndTime(CompositeData paramCompositeData) {
/* 209 */     return getLong(paramCompositeData, "endTime");
/*     */   }
/*     */   
/*     */   public static Map<String, MemoryUsage> getMemoryUsageBeforeGc(CompositeData paramCompositeData)
/*     */   {
/*     */     try {
/* 215 */       TabularData localTabularData = (TabularData)paramCompositeData.get("memoryUsageBeforeGc");
/* 216 */       return cast(memoryUsageMapType.toJavaTypeData(localTabularData));
/*     */     }
/*     */     catch (InvalidObjectException|OpenDataException localInvalidObjectException) {
/* 219 */       throw new AssertionError(localInvalidObjectException);
/*     */     }
/*     */   }
/*     */   
/*     */   public static Map<String, MemoryUsage> cast(Object paramObject)
/*     */   {
/* 225 */     return (Map)paramObject;
/*     */   }
/*     */   
/*     */   public static Map<String, MemoryUsage> getMemoryUsageAfterGc(CompositeData paramCompositeData) {
/*     */     try {
/* 230 */       TabularData localTabularData = (TabularData)paramCompositeData.get("memoryUsageAfterGc");
/*     */       
/* 232 */       return cast(memoryUsageMapType.toJavaTypeData(localTabularData));
/*     */     }
/*     */     catch (InvalidObjectException|OpenDataException localInvalidObjectException) {
/* 235 */       throw new AssertionError(localInvalidObjectException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void validateCompositeData(CompositeData paramCompositeData)
/*     */   {
/* 245 */     if (paramCompositeData == null) {
/* 246 */       throw new NullPointerException("Null CompositeData");
/*     */     }
/*     */     
/* 249 */     if (!isTypeMatched(getBaseGcInfoCompositeType(), paramCompositeData
/* 250 */       .getCompositeType())) {
/* 251 */       throw new IllegalArgumentException("Unexpected composite type for GcInfo");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 257 */   private static CompositeType baseGcInfoCompositeType = null;
/*     */   
/* 259 */   static synchronized CompositeType getBaseGcInfoCompositeType() { if (baseGcInfoCompositeType == null)
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/*     */ 
/* 266 */         baseGcInfoCompositeType = new CompositeType("sun.management.BaseGcInfoCompositeType", "CompositeType for Base GcInfo", getBaseGcInfoItemNames(), getBaseGcInfoItemNames(), getBaseGcInfoItemTypes());
/*     */       }
/*     */       catch (OpenDataException localOpenDataException) {
/* 269 */         throw Util.newException(localOpenDataException);
/*     */       }
/*     */     }
/* 272 */     return baseGcInfoCompositeType;
/*     */   }
/*     */   
/*     */   private static final long serialVersionUID = -5716428894085882742L;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\GcInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
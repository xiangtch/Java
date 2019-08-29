/*     */ package sun.management;
/*     */ 
/*     */ import com.sun.management.GarbageCollectionNotificationInfo;
/*     */ import com.sun.management.GcInfo;
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import javax.management.openmbean.CompositeData;
/*     */ import javax.management.openmbean.CompositeDataSupport;
/*     */ import javax.management.openmbean.CompositeType;
/*     */ import javax.management.openmbean.OpenDataException;
/*     */ import javax.management.openmbean.OpenType;
/*     */ import javax.management.openmbean.SimpleType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GarbageCollectionNotifInfoCompositeData
/*     */   extends LazyCompositeData
/*     */ {
/*     */   private final GarbageCollectionNotificationInfo gcNotifInfo;
/*     */   private static final String GC_NAME = "gcName";
/*     */   private static final String GC_ACTION = "gcAction";
/*     */   private static final String GC_CAUSE = "gcCause";
/*     */   private static final String GC_INFO = "gcInfo";
/*     */   
/*     */   public GarbageCollectionNotifInfoCompositeData(GarbageCollectionNotificationInfo paramGarbageCollectionNotificationInfo)
/*     */   {
/*  51 */     this.gcNotifInfo = paramGarbageCollectionNotificationInfo;
/*     */   }
/*     */   
/*     */   public GarbageCollectionNotificationInfo getGarbageCollectionNotifInfo() {
/*  55 */     return this.gcNotifInfo;
/*     */   }
/*     */   
/*     */   public static CompositeData toCompositeData(GarbageCollectionNotificationInfo paramGarbageCollectionNotificationInfo) {
/*  59 */     GarbageCollectionNotifInfoCompositeData localGarbageCollectionNotifInfoCompositeData = new GarbageCollectionNotifInfoCompositeData(paramGarbageCollectionNotificationInfo);
/*     */     
/*  61 */     return localGarbageCollectionNotifInfoCompositeData.getCompositeData();
/*     */   }
/*     */   
/*     */   private CompositeType getCompositeTypeByBuilder() {
/*  65 */     GcInfoBuilder localGcInfoBuilder = (GcInfoBuilder)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public GcInfoBuilder run() {
/*     */         try {
/*  68 */           Class localClass = Class.forName("com.sun.management.GcInfo");
/*  69 */           Field localField = localClass.getDeclaredField("builder");
/*  70 */           localField.setAccessible(true);
/*  71 */           return (GcInfoBuilder)localField.get(GarbageCollectionNotifInfoCompositeData.this.gcNotifInfo.getGcInfo());
/*     */         } catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException localClassNotFoundException) {}
/*  73 */         return null;
/*     */       }
/*     */       
/*  76 */     });
/*  77 */     CompositeType localCompositeType = null;
/*  78 */     synchronized (compositeTypeByBuilder) {
/*  79 */       localCompositeType = (CompositeType)compositeTypeByBuilder.get(localGcInfoBuilder);
/*  80 */       if (localCompositeType == null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  85 */         OpenType[] arrayOfOpenType = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, localGcInfoBuilder.getGcInfoCompositeType() };
/*     */         
/*     */ 
/*     */         try
/*     */         {
/*  90 */           localCompositeType = new CompositeType("sun.management.GarbageCollectionNotifInfoCompositeType", "CompositeType for GC notification info", gcNotifInfoItemNames, gcNotifInfoItemNames, arrayOfOpenType);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*  95 */           compositeTypeByBuilder.put(localGcInfoBuilder, localCompositeType);
/*     */         }
/*     */         catch (OpenDataException localOpenDataException) {
/*  98 */           throw Util.newException(localOpenDataException);
/*     */         }
/*     */       }
/*     */     }
/* 102 */     return localCompositeType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CompositeData getCompositeData()
/*     */   {
/* 113 */     Object[] arrayOfObject = { this.gcNotifInfo.getGcName(), this.gcNotifInfo.getGcAction(), this.gcNotifInfo.getGcCause(), GcInfoCompositeData.toCompositeData(this.gcNotifInfo.getGcInfo()) };
/*     */     
/*     */ 
/* 116 */     CompositeType localCompositeType = getCompositeTypeByBuilder();
/*     */     try
/*     */     {
/* 119 */       return new CompositeDataSupport(localCompositeType, gcNotifInfoItemNames, arrayOfObject);
/*     */ 
/*     */     }
/*     */     catch (OpenDataException localOpenDataException)
/*     */     {
/* 124 */       throw new AssertionError(localOpenDataException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 133 */   private static final String[] gcNotifInfoItemNames = { "gcName", "gcAction", "gcCause", "gcInfo" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 139 */   private static HashMap<GcInfoBuilder, CompositeType> compositeTypeByBuilder = new HashMap();
/*     */   
/*     */   public static String getGcName(CompositeData paramCompositeData)
/*     */   {
/* 143 */     String str = getString(paramCompositeData, "gcName");
/* 144 */     if (str == null) {
/* 145 */       throw new IllegalArgumentException("Invalid composite data: Attribute gcName has null value");
/*     */     }
/*     */     
/* 148 */     return str;
/*     */   }
/*     */   
/*     */   public static String getGcAction(CompositeData paramCompositeData) {
/* 152 */     String str = getString(paramCompositeData, "gcAction");
/* 153 */     if (str == null) {
/* 154 */       throw new IllegalArgumentException("Invalid composite data: Attribute gcAction has null value");
/*     */     }
/*     */     
/* 157 */     return str;
/*     */   }
/*     */   
/*     */   public static String getGcCause(CompositeData paramCompositeData) {
/* 161 */     String str = getString(paramCompositeData, "gcCause");
/* 162 */     if (str == null) {
/* 163 */       throw new IllegalArgumentException("Invalid composite data: Attribute gcCause has null value");
/*     */     }
/*     */     
/* 166 */     return str;
/*     */   }
/*     */   
/*     */   public static GcInfo getGcInfo(CompositeData paramCompositeData) {
/* 170 */     CompositeData localCompositeData = (CompositeData)paramCompositeData.get("gcInfo");
/* 171 */     return GcInfo.from(localCompositeData);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void validateCompositeData(CompositeData paramCompositeData)
/*     */   {
/* 179 */     if (paramCompositeData == null) {
/* 180 */       throw new NullPointerException("Null CompositeData");
/*     */     }
/*     */     
/* 183 */     if (!isTypeMatched(getBaseGcNotifInfoCompositeType(), paramCompositeData.getCompositeType())) {
/* 184 */       throw new IllegalArgumentException("Unexpected composite type for GarbageCollectionNotificationInfo");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 190 */   private static CompositeType baseGcNotifInfoCompositeType = null;
/*     */   
/* 192 */   private static synchronized CompositeType getBaseGcNotifInfoCompositeType() { if (baseGcNotifInfoCompositeType == null)
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 198 */         OpenType[] arrayOfOpenType = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, GcInfoCompositeData.getBaseGcInfoCompositeType() };
/*     */         
/* 200 */         baseGcNotifInfoCompositeType = new CompositeType("sun.management.BaseGarbageCollectionNotifInfoCompositeType", "CompositeType for Base GarbageCollectionNotificationInfo", gcNotifInfoItemNames, gcNotifInfoItemNames, arrayOfOpenType);
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (OpenDataException localOpenDataException)
/*     */       {
/*     */ 
/*     */ 
/* 208 */         throw Util.newException(localOpenDataException);
/*     */       }
/*     */     }
/* 211 */     return baseGcNotifInfoCompositeType;
/*     */   }
/*     */   
/*     */   private static final long serialVersionUID = -1805123446483771292L;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\GarbageCollectionNotifInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
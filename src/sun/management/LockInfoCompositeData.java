/*     */ package sun.management;
/*     */ 
/*     */ import java.lang.management.LockInfo;
/*     */ import javax.management.openmbean.CompositeData;
/*     */ import javax.management.openmbean.CompositeDataSupport;
/*     */ import javax.management.openmbean.CompositeType;
/*     */ import javax.management.openmbean.OpenDataException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LockInfoCompositeData
/*     */   extends LazyCompositeData
/*     */ {
/*     */   private final LockInfo lock;
/*     */   private static final CompositeType lockInfoCompositeType;
/*     */   private static final String CLASS_NAME = "className";
/*     */   private static final String IDENTITY_HASH_CODE = "identityHashCode";
/*     */   
/*     */   private LockInfoCompositeData(LockInfo paramLockInfo)
/*     */   {
/*  43 */     this.lock = paramLockInfo;
/*     */   }
/*     */   
/*     */   public LockInfo getLockInfo() {
/*  47 */     return this.lock;
/*     */   }
/*     */   
/*     */   public static CompositeData toCompositeData(LockInfo paramLockInfo) {
/*  51 */     if (paramLockInfo == null) {
/*  52 */       return null;
/*     */     }
/*     */     
/*  55 */     LockInfoCompositeData localLockInfoCompositeData = new LockInfoCompositeData(paramLockInfo);
/*  56 */     return localLockInfoCompositeData.getCompositeData();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CompositeData getCompositeData()
/*     */   {
/*  64 */     Object[] arrayOfObject = { new String(this.lock.getClassName()), new Integer(this.lock.getIdentityHashCode()) };
/*     */     
/*     */     try
/*     */     {
/*  68 */       return new CompositeDataSupport(lockInfoCompositeType, lockInfoItemNames, arrayOfObject);
/*     */ 
/*     */     }
/*     */     catch (OpenDataException localOpenDataException)
/*     */     {
/*  73 */       throw Util.newException(localOpenDataException);
/*     */     }
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  81 */       lockInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(LockInfo.class);
/*     */     }
/*     */     catch (OpenDataException localOpenDataException) {
/*  84 */       throw Util.newException(localOpenDataException);
/*     */     }
/*     */   }
/*     */   
/*     */   static CompositeType getLockInfoCompositeType() {
/*  89 */     return lockInfoCompositeType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  94 */   private static final String[] lockInfoItemNames = { "className", "identityHashCode" };
/*     */   
/*     */ 
/*     */   private static final long serialVersionUID = -6374759159749014052L;
/*     */   
/*     */ 
/*     */ 
/*     */   public static LockInfo toLockInfo(CompositeData paramCompositeData)
/*     */   {
/* 103 */     if (paramCompositeData == null) {
/* 104 */       throw new NullPointerException("Null CompositeData");
/*     */     }
/*     */     
/* 107 */     if (!isTypeMatched(lockInfoCompositeType, paramCompositeData.getCompositeType())) {
/* 108 */       throw new IllegalArgumentException("Unexpected composite type for LockInfo");
/*     */     }
/*     */     
/*     */ 
/* 112 */     String str = getString(paramCompositeData, "className");
/* 113 */     int i = getInt(paramCompositeData, "identityHashCode");
/* 114 */     return new LockInfo(str, i);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\LockInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
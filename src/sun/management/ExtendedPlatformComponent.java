/*     */ package sun.management;
/*     */ 
/*     */ import com.sun.management.VMOption;
/*     */ import java.lang.management.PlatformManagedObject;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import jdk.internal.cmm.SystemResourcePressureImpl;
/*     */ import jdk.management.cmm.SystemResourcePressureMXBean;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ExtendedPlatformComponent
/*     */ {
/*  21 */   private static SystemResourcePressureMXBean cmmBeanImpl = null;
/*     */   
/*     */   private static synchronized SystemResourcePressureMXBean getCMMBean()
/*     */   {
/*  25 */     if (cmmBeanImpl == null) {
/*  26 */       cmmBeanImpl = new SystemResourcePressureImpl();
/*     */     }
/*  28 */     return cmmBeanImpl;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static List<? extends PlatformManagedObject> getMXBeans()
/*     */   {
/*  36 */     if (shouldRegisterCMMBean()) {
/*  37 */       return Collections.singletonList(getCMMBean());
/*     */     }
/*  39 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T extends PlatformManagedObject> T getMXBean(Class<T> paramClass)
/*     */   {
/*  51 */     if ((paramClass != null) && 
/*  52 */       ("jdk.management.cmm.SystemResourcePressureMXBean".equals(paramClass
/*  53 */       .getName())))
/*     */     {
/*  55 */       if (isUnlockCommercialFeaturesEnabled()) {
/*  56 */         return (PlatformManagedObject)paramClass.cast(getCMMBean());
/*     */       }
/*  58 */       throw new IllegalArgumentException("Cooperative Memory Management is a commercial feature which must be unlocked before being used.  To learn more about commercial features and how to unlock them visit http://www.oracle.com/technetwork/java/javaseproducts/");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  66 */     return null;
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
/*     */   private static boolean shouldRegisterCMMBean()
/*     */   {
/*  82 */     if (!isUnlockCommercialFeaturesEnabled()) {
/*  83 */       return false;
/*     */     }
/*     */     
/*  86 */     boolean bool = false;
/*  87 */     Class localClass = null;
/*     */     try
/*     */     {
/*  90 */       ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
/*  91 */       if (localClassLoader == null) return false;
/*  92 */       localClassLoader = localClassLoader.getParent();
/*  93 */       localClass = Class.forName("com.oracle.exalogic.ExaManager", false, localClassLoader);
/*     */       
/*  95 */       Object localObject1 = localClass.getMethod("instance", new Class[0]).invoke(null, new Object[0]);
/*  96 */       Object localObject2; if (localObject1 != null)
/*     */       {
/*  98 */         localObject2 = localClass.getMethod("isExalogicSystem", new Class[0]).invoke(localObject1, new Object[0]); }
/*  99 */       return ((Boolean)localObject2).booleanValue();
/*     */     }
/*     */     catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException|IllegalArgumentException|InvocationTargetException localClassNotFoundException) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 106 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isUnlockCommercialFeaturesEnabled()
/*     */   {
/* 114 */     Flag localFlag = Flag.getFlag("UnlockCommercialFeatures");
/* 115 */     if ((localFlag != null) && ("true".equals(localFlag.getVMOption().getValue()))) {
/* 116 */       return true;
/*     */     }
/* 118 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\ExtendedPlatformComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
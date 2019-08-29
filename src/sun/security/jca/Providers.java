/*     */ package sun.security.jca;
/*     */ 
/*     */ import java.security.Provider;
/*     */ import sun.security.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Providers
/*     */ {
/*  39 */   private static final ThreadLocal<ProviderList> threadLists = new InheritableThreadLocal();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static volatile int threadListsUsed;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  54 */   private static volatile ProviderList providerList = ProviderList.fromSecurityProperties();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String BACKUP_PROVIDER_CLASSNAME = "sun.security.provider.VerificationProvider";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */   private static final String[] jarVerificationProviders = { "sun.security.provider.Sun", "sun.security.rsa.SunRsaSign", "sun.security.ec.SunEC", "sun.security.provider.VerificationProvider" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Provider getSunProvider()
/*     */   {
/*     */     try
/*     */     {
/*  97 */       Class localClass1 = Class.forName(jarVerificationProviders[0]);
/*  98 */       return (Provider)localClass1.newInstance();
/*     */     } catch (Exception localException1) {
/*     */       try {
/* 101 */         Class localClass2 = Class.forName("sun.security.provider.VerificationProvider");
/* 102 */         return (Provider)localClass2.newInstance();
/*     */       } catch (Exception localException2) {
/* 104 */         throw new RuntimeException("Sun provider not found", localException1);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object startJarVerification()
/*     */   {
/* 116 */     ProviderList localProviderList1 = getProviderList();
/* 117 */     ProviderList localProviderList2 = localProviderList1.getJarList(jarVerificationProviders);
/*     */     
/* 119 */     return beginThreadProviderList(localProviderList2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void stopJarVerification(Object paramObject)
/*     */   {
/* 127 */     endThreadProviderList((ProviderList)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ProviderList getProviderList()
/*     */   {
/* 135 */     ProviderList localProviderList = getThreadProviderList();
/* 136 */     if (localProviderList == null) {
/* 137 */       localProviderList = getSystemProviderList();
/*     */     }
/* 139 */     return localProviderList;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setProviderList(ProviderList paramProviderList)
/*     */   {
/* 147 */     if (getThreadProviderList() == null) {
/* 148 */       setSystemProviderList(paramProviderList);
/*     */     } else {
/* 150 */       changeThreadProviderList(paramProviderList);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ProviderList getFullProviderList()
/*     */   {
/* 161 */     synchronized (Providers.class) {
/* 162 */       localObject1 = getThreadProviderList();
/* 163 */       if (localObject1 != null) {
/* 164 */         ProviderList localProviderList = ((ProviderList)localObject1).removeInvalid();
/* 165 */         if (localProviderList != localObject1) {
/* 166 */           changeThreadProviderList(localProviderList);
/* 167 */           localObject1 = localProviderList;
/*     */         }
/* 169 */         return (ProviderList)localObject1;
/*     */       }
/*     */     }
/* 172 */     Object localObject1 = getSystemProviderList();
/* 173 */     ??? = ((ProviderList)localObject1).removeInvalid();
/* 174 */     if (??? != localObject1) {
/* 175 */       setSystemProviderList((ProviderList)???);
/* 176 */       localObject1 = ???;
/*     */     }
/* 178 */     return (ProviderList)localObject1;
/*     */   }
/*     */   
/*     */   private static ProviderList getSystemProviderList() {
/* 182 */     return providerList;
/*     */   }
/*     */   
/*     */   private static void setSystemProviderList(ProviderList paramProviderList) {
/* 186 */     providerList = paramProviderList;
/*     */   }
/*     */   
/*     */ 
/*     */   public static ProviderList getThreadProviderList()
/*     */   {
/* 192 */     if (threadListsUsed == 0) {
/* 193 */       return null;
/*     */     }
/* 195 */     return (ProviderList)threadLists.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void changeThreadProviderList(ProviderList paramProviderList)
/*     */   {
/* 202 */     threadLists.set(paramProviderList);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized ProviderList beginThreadProviderList(ProviderList paramProviderList)
/*     */   {
/* 222 */     if (ProviderList.debug != null) {
/* 223 */       ProviderList.debug.println("ThreadLocal providers: " + paramProviderList);
/*     */     }
/* 225 */     ProviderList localProviderList = (ProviderList)threadLists.get();
/* 226 */     threadListsUsed += 1;
/* 227 */     threadLists.set(paramProviderList);
/* 228 */     return localProviderList;
/*     */   }
/*     */   
/*     */   public static synchronized void endThreadProviderList(ProviderList paramProviderList) {
/* 232 */     if (paramProviderList == null) {
/* 233 */       if (ProviderList.debug != null) {
/* 234 */         ProviderList.debug.println("Disabling ThreadLocal providers");
/*     */       }
/* 236 */       threadLists.remove();
/*     */     } else {
/* 238 */       if (ProviderList.debug != null)
/*     */       {
/* 240 */         ProviderList.debug.println("Restoring previous ThreadLocal providers: " + paramProviderList);
/*     */       }
/* 242 */       threadLists.set(paramProviderList);
/*     */     }
/* 244 */     threadListsUsed -= 1;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jca\Providers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
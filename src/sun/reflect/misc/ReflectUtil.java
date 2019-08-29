/*     */ package sun.reflect.misc;
/*     */ 
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.Proxy;
/*     */ import sun.reflect.Reflection;
/*     */ import sun.security.util.SecurityConstants;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ReflectUtil
/*     */ {
/*     */   public static final String PROXY_PACKAGE = "com.sun.proxy";
/*     */   
/*     */   public static Class<?> forName(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/*  44 */     checkPackageAccess(paramString);
/*  45 */     return Class.forName(paramString);
/*     */   }
/*     */   
/*     */   public static Object newInstance(Class<?> paramClass) throws InstantiationException, IllegalAccessException
/*     */   {
/*  50 */     checkPackageAccess(paramClass);
/*  51 */     return paramClass.newInstance();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void ensureMemberAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt)
/*     */     throws IllegalAccessException
/*     */   {
/*  64 */     if ((paramObject == null) && (Modifier.isProtected(paramInt))) {
/*  65 */       int i = paramInt;
/*  66 */       i &= 0xFFFFFFFB;
/*  67 */       i |= 0x1;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  72 */       Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, i);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/*  81 */         i &= 0xFFFFFFFE;
/*  82 */         Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, i);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */         return;
/*     */ 
/*     */       }
/*     */       catch (IllegalAccessException localIllegalAccessException)
/*     */       {
/*     */ 
/*  96 */         if (isSubclassOf(paramClass1, paramClass2)) {
/*  97 */           return;
/*     */         }
/*  99 */         throw localIllegalAccessException;
/*     */       }
/*     */     }
/*     */     
/* 103 */     Reflection.ensureMemberAccess(paramClass1, paramClass2, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isSubclassOf(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 113 */     while (paramClass1 != null) {
/* 114 */       if (paramClass1 == paramClass2) {
/* 115 */         return true;
/*     */       }
/* 117 */       paramClass1 = paramClass1.getSuperclass();
/*     */     }
/* 119 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void conservativeCheckMemberAccess(Member paramMember)
/*     */     throws SecurityException
/*     */   {
/* 132 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 133 */     if (localSecurityManager == null) {
/* 134 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */     Class localClass = paramMember.getDeclaringClass();
/*     */     
/* 146 */     checkPackageAccess(localClass);
/*     */     
/* 148 */     if ((Modifier.isPublic(paramMember.getModifiers())) && 
/* 149 */       (Modifier.isPublic(localClass.getModifiers()))) {
/* 150 */       return;
/*     */     }
/*     */     
/* 153 */     localSecurityManager.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void checkPackageAccess(Class<?> paramClass)
/*     */   {
/* 164 */     checkPackageAccess(paramClass.getName());
/* 165 */     if (isNonPublicProxyClass(paramClass)) {
/* 166 */       checkProxyPackageAccess(paramClass);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void checkPackageAccess(String paramString)
/*     */   {
/* 177 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 178 */     if (localSecurityManager != null) {
/* 179 */       String str = paramString.replace('/', '.');
/* 180 */       if (str.startsWith("[")) {
/* 181 */         i = str.lastIndexOf('[') + 2;
/* 182 */         if ((i > 1) && (i < str.length())) {
/* 183 */           str = str.substring(i);
/*     */         }
/*     */       }
/* 186 */       int i = str.lastIndexOf('.');
/* 187 */       if (i != -1) {
/* 188 */         localSecurityManager.checkPackageAccess(str.substring(0, i));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isPackageAccessible(Class<?> paramClass) {
/*     */     try {
/* 195 */       checkPackageAccess(paramClass);
/*     */     } catch (SecurityException localSecurityException) {
/* 197 */       return false;
/*     */     }
/* 199 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   private static boolean isAncestor(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*     */   {
/* 205 */     ClassLoader localClassLoader = paramClassLoader2;
/*     */     do {
/* 207 */       localClassLoader = localClassLoader.getParent();
/* 208 */       if (paramClassLoader1 == localClassLoader) {
/* 209 */         return true;
/*     */       }
/* 211 */     } while (localClassLoader != null);
/* 212 */     return false;
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
/*     */   public static boolean needsPackageAccessCheck(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*     */   {
/* 227 */     if ((paramClassLoader1 == null) || (paramClassLoader1 == paramClassLoader2)) {
/* 228 */       return false;
/*     */     }
/* 230 */     if (paramClassLoader2 == null) {
/* 231 */       return true;
/*     */     }
/* 233 */     return !isAncestor(paramClassLoader1, paramClassLoader2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void checkProxyPackageAccess(Class<?> paramClass)
/*     */   {
/* 243 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 244 */     if (localSecurityManager != null)
/*     */     {
/* 246 */       if (Proxy.isProxyClass(paramClass)) {
/* 247 */         for (Class localClass : paramClass.getInterfaces()) {
/* 248 */           checkPackageAccess(localClass);
/*     */         }
/*     */       }
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
/*     */   public static void checkProxyPackageAccess(ClassLoader paramClassLoader, Class<?>... paramVarArgs)
/*     */   {
/* 265 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 266 */     if (localSecurityManager != null) {
/* 267 */       for (Class<?> localClass : paramVarArgs) {
/* 268 */         ClassLoader localClassLoader = localClass.getClassLoader();
/* 269 */         if (needsPackageAccessCheck(paramClassLoader, localClassLoader)) {
/* 270 */           checkPackageAccess(localClass);
/*     */         }
/*     */       }
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
/*     */   public static boolean isNonPublicProxyClass(Class<?> paramClass)
/*     */   {
/* 286 */     String str1 = paramClass.getName();
/* 287 */     int i = str1.lastIndexOf('.');
/* 288 */     String str2 = i != -1 ? str1.substring(0, i) : "";
/* 289 */     return (Proxy.isProxyClass(paramClass)) && (!str2.equals("com.sun.proxy"));
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
/*     */   public static void checkProxyMethod(Object paramObject, Method paramMethod)
/*     */   {
/* 303 */     if ((paramObject == null) || (!Proxy.isProxyClass(paramObject.getClass()))) {
/* 304 */       throw new IllegalArgumentException("Not a Proxy instance");
/*     */     }
/* 306 */     if (Modifier.isStatic(paramMethod.getModifiers())) {
/* 307 */       throw new IllegalArgumentException("Can't handle static method");
/*     */     }
/*     */     
/* 310 */     Class localClass = paramMethod.getDeclaringClass();
/* 311 */     if (localClass == Object.class) {
/* 312 */       String str = paramMethod.getName();
/* 313 */       if ((str.equals("hashCode")) || (str.equals("equals")) || (str.equals("toString"))) {
/* 314 */         return;
/*     */       }
/*     */     }
/*     */     
/* 318 */     if (isSuperInterface(paramObject.getClass(), localClass)) {
/* 319 */       return;
/*     */     }
/*     */     
/*     */ 
/* 323 */     throw new IllegalArgumentException("Can't handle: " + paramMethod);
/*     */   }
/*     */   
/*     */   private static boolean isSuperInterface(Class<?> paramClass1, Class<?> paramClass2) {
/* 327 */     for (Class localClass : paramClass1.getInterfaces()) {
/* 328 */       if (localClass == paramClass2) {
/* 329 */         return true;
/*     */       }
/* 331 */       if (isSuperInterface(localClass, paramClass2)) {
/* 332 */         return true;
/*     */       }
/*     */     }
/* 335 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isVMAnonymousClass(Class<?> paramClass)
/*     */   {
/* 344 */     return paramClass.getName().indexOf("/") > -1;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\misc\ReflectUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
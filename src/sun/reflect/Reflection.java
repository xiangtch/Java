/*     */ package sun.reflect;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import sun.misc.VM;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Reflection
/*     */ {
/*     */   private static volatile Map<Class<?>, String[]> fieldFilterMap;
/*     */   
/*     */   static
/*     */   {
/*  45 */     HashMap localHashMap = new HashMap();
/*  46 */     localHashMap.put(Reflection.class, new String[] { "fieldFilterMap", "methodFilterMap" });
/*     */     
/*  48 */     localHashMap.put(System.class, new String[] { "security" });
/*  49 */     localHashMap.put(Class.class, new String[] { "classLoader" });
/*  50 */     fieldFilterMap = localHashMap; }
/*     */   
/*  52 */   private static volatile Map<Class<?>, String[]> methodFilterMap = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean quickCheckMemberAccess(Class<?> paramClass, int paramInt)
/*     */   {
/*  84 */     return Modifier.isPublic(getClassAccessFlags(paramClass) & paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void ensureMemberAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt)
/*     */     throws IllegalAccessException
/*     */   {
/*  93 */     if ((paramClass1 == null) || (paramClass2 == null)) {
/*  94 */       throw new InternalError();
/*     */     }
/*     */     
/*  97 */     if (!verifyMemberAccess(paramClass1, paramClass2, paramObject, paramInt))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 102 */       throw new IllegalAccessException("Class " + paramClass1.getName() + " can not access a member of class " + paramClass2.getName() + " with modifiers \"" + Modifier.toString(paramInt) + "\"");
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
/*     */   public static boolean verifyMemberAccess(Class<?> paramClass1, Class<?> paramClass2, Object paramObject, int paramInt)
/*     */   {
/* 119 */     int i = 0;
/* 120 */     boolean bool = false;
/*     */     
/* 122 */     if (paramClass1 == paramClass2)
/*     */     {
/* 124 */       return true;
/*     */     }
/*     */     
/* 127 */     if (!Modifier.isPublic(getClassAccessFlags(paramClass2))) {
/* 128 */       bool = isSameClassPackage(paramClass1, paramClass2);
/* 129 */       i = 1;
/* 130 */       if (!bool) {
/* 131 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 137 */     if (Modifier.isPublic(paramInt)) {
/* 138 */       return true;
/*     */     }
/*     */     
/* 141 */     int j = 0;
/*     */     
/* 143 */     if (Modifier.isProtected(paramInt))
/*     */     {
/* 145 */       if (isSubclassOf(paramClass1, paramClass2)) {
/* 146 */         j = 1;
/*     */       }
/*     */     }
/*     */     
/* 150 */     if ((j == 0) && (!Modifier.isPrivate(paramInt))) {
/* 151 */       if (i == 0) {
/* 152 */         bool = isSameClassPackage(paramClass1, paramClass2);
/*     */         
/* 154 */         i = 1;
/*     */       }
/*     */       
/* 157 */       if (bool) {
/* 158 */         j = 1;
/*     */       }
/*     */     }
/*     */     
/* 162 */     if (j == 0) {
/* 163 */       return false;
/*     */     }
/*     */     
/* 166 */     if (Modifier.isProtected(paramInt))
/*     */     {
/* 168 */       Class localClass = paramObject == null ? paramClass2 : paramObject.getClass();
/* 169 */       if (localClass != paramClass1) {
/* 170 */         if (i == 0) {
/* 171 */           bool = isSameClassPackage(paramClass1, paramClass2);
/* 172 */           i = 1;
/*     */         }
/* 174 */         if ((!bool) && 
/* 175 */           (!isSubclassOf(localClass, paramClass1))) {
/* 176 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 182 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean isSameClassPackage(Class<?> paramClass1, Class<?> paramClass2) {
/* 186 */     return isSameClassPackage(paramClass1.getClassLoader(), paramClass1.getName(), paramClass2
/* 187 */       .getClassLoader(), paramClass2.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isSameClassPackage(ClassLoader paramClassLoader1, String paramString1, ClassLoader paramClassLoader2, String paramString2)
/*     */   {
/* 195 */     if (paramClassLoader1 != paramClassLoader2) {
/* 196 */       return false;
/*     */     }
/* 198 */     int i = paramString1.lastIndexOf('.');
/* 199 */     int j = paramString2.lastIndexOf('.');
/* 200 */     if ((i == -1) || (j == -1))
/*     */     {
/*     */ 
/* 203 */       return i == j;
/*     */     }
/* 205 */     int k = 0;
/* 206 */     int m = 0;
/*     */     
/*     */ 
/* 209 */     if (paramString1.charAt(k) == '[') {
/*     */       do {
/* 211 */         k++;
/* 212 */       } while (paramString1.charAt(k) == '[');
/* 213 */       if (paramString1.charAt(k) != 'L')
/*     */       {
/* 215 */         throw new InternalError("Illegal class name " + paramString1);
/*     */       }
/*     */     }
/* 218 */     if (paramString2.charAt(m) == '[') {
/*     */       do {
/* 220 */         m++;
/* 221 */       } while (paramString2.charAt(m) == '[');
/* 222 */       if (paramString2.charAt(m) != 'L')
/*     */       {
/* 224 */         throw new InternalError("Illegal class name " + paramString2);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 229 */     int n = i - k;
/* 230 */     int i1 = j - m;
/*     */     
/* 232 */     if (n != i1) {
/* 233 */       return false;
/*     */     }
/* 235 */     return paramString1.regionMatches(false, k, paramString2, m, n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean isSubclassOf(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 243 */     while (paramClass1 != null) {
/* 244 */       if (paramClass1 == paramClass2) {
/* 245 */         return true;
/*     */       }
/* 247 */       paramClass1 = paramClass1.getSuperclass();
/*     */     }
/* 249 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static synchronized void registerFieldsToFilter(Class<?> paramClass, String... paramVarArgs)
/*     */   {
/* 256 */     fieldFilterMap = registerFilter(fieldFilterMap, paramClass, paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static synchronized void registerMethodsToFilter(Class<?> paramClass, String... paramVarArgs)
/*     */   {
/* 263 */     methodFilterMap = registerFilter(methodFilterMap, paramClass, paramVarArgs);
/*     */   }
/*     */   
/*     */   private static Map<Class<?>, String[]> registerFilter(Map<Class<?>, String[]> paramMap, Class<?> paramClass, String... paramVarArgs)
/*     */   {
/* 268 */     if (paramMap.get(paramClass) != null) {
/* 269 */       throw new IllegalArgumentException("Filter already registered: " + paramClass);
/*     */     }
/*     */     
/* 272 */     paramMap = new HashMap(paramMap);
/* 273 */     paramMap.put(paramClass, paramVarArgs);
/* 274 */     return paramMap;
/*     */   }
/*     */   
/*     */   public static Field[] filterFields(Class<?> paramClass, Field[] paramArrayOfField)
/*     */   {
/* 279 */     if (fieldFilterMap == null)
/*     */     {
/* 281 */       return paramArrayOfField;
/*     */     }
/* 283 */     return (Field[])filter(paramArrayOfField, (String[])fieldFilterMap.get(paramClass));
/*     */   }
/*     */   
/*     */   public static Method[] filterMethods(Class<?> paramClass, Method[] paramArrayOfMethod) {
/* 287 */     if (methodFilterMap == null)
/*     */     {
/* 289 */       return paramArrayOfMethod;
/*     */     }
/* 291 */     return (Method[])filter(paramArrayOfMethod, (String[])methodFilterMap.get(paramClass));
/*     */   }
/*     */   
/*     */   private static Member[] filter(Member[] paramArrayOfMember, String[] paramArrayOfString) {
/* 295 */     if ((paramArrayOfString == null) || (paramArrayOfMember.length == 0)) {
/* 296 */       return paramArrayOfMember;
/*     */     }
/* 298 */     int i = 0;
/* 299 */     int n; for (Member localMember : paramArrayOfMember) {
/* 300 */       n = 0;
/* 301 */       for (Object localObject2 : paramArrayOfString) {
/* 302 */         if (localMember.getName() == localObject2) {
/* 303 */           n = 1;
/* 304 */           break;
/*     */         }
/*     */       }
/* 307 */       if (n == 0) {
/* 308 */         i++;
/*     */       }
/*     */     }
/*     */     
/* 312 */     ??? = (Member[])Array.newInstance(paramArrayOfMember[0].getClass(), i);
/* 313 */     ??? = 0;
/* 314 */     for (??? : paramArrayOfMember) {
/* 315 */       ??? = 0;
/* 316 */       for (String str : paramArrayOfString) {
/* 317 */         if (((Member)???).getName() == str) {
/* 318 */           ??? = 1;
/* 319 */           break;
/*     */         }
/*     */       }
/* 322 */       if (??? == 0) {
/* 323 */         ???[(???++)] = ???;
/*     */       }
/*     */     }
/* 326 */     return (Member[])???;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isCallerSensitive(Method paramMethod)
/*     */   {
/* 334 */     ClassLoader localClassLoader = paramMethod.getDeclaringClass().getClassLoader();
/* 335 */     if ((VM.isSystemDomainLoader(localClassLoader)) || (isExtClassLoader(localClassLoader))) {
/* 336 */       return paramMethod.isAnnotationPresent(CallerSensitive.class);
/*     */     }
/* 338 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean isExtClassLoader(ClassLoader paramClassLoader) {
/* 342 */     ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
/* 343 */     while (localClassLoader != null) {
/* 344 */       if ((localClassLoader.getParent() == null) && (localClassLoader == paramClassLoader)) {
/* 345 */         return true;
/*     */       }
/* 347 */       localClassLoader = localClassLoader.getParent();
/*     */     }
/* 349 */     return false;
/*     */   }
/*     */   
/*     */   @CallerSensitive
/*     */   public static native Class<?> getCallerClass();
/*     */   
/*     */   @Deprecated
/*     */   public static native Class<?> getCallerClass(int paramInt);
/*     */   
/*     */   public static native int getClassAccessFlags(Class<?> paramClass);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\Reflection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
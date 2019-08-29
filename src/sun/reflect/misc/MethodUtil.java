/*     */ package sun.reflect.misc;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.security.AccessController;
/*     */ import java.security.AllPermission;
/*     */ import java.security.CodeSource;
/*     */ import java.security.PermissionCollection;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.SecureClassLoader;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import sun.misc.IOUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MethodUtil
/*     */   extends SecureClassLoader
/*     */ {
/*     */   private static final String MISC_PKG = "sun.reflect.misc.";
/*     */   private static final String TRAMPOLINE = "sun.reflect.misc.Trampoline";
/*  81 */   private static final Method bounce = ;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Method getMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
/*     */     throws NoSuchMethodException
/*     */   {
/*  89 */     ReflectUtil.checkPackageAccess(paramClass);
/*  90 */     return paramClass.getMethod(paramString, paramArrayOfClass);
/*     */   }
/*     */   
/*     */   public static Method[] getMethods(Class<?> paramClass) {
/*  94 */     ReflectUtil.checkPackageAccess(paramClass);
/*  95 */     return paramClass.getMethods();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Method[] getPublicMethods(Class<?> paramClass)
/*     */   {
/* 106 */     if (System.getSecurityManager() == null) {
/* 107 */       return paramClass.getMethods();
/*     */     }
/* 109 */     HashMap localHashMap = new HashMap();
/* 110 */     while (paramClass != null) {
/* 111 */       boolean bool = getInternalPublicMethods(paramClass, localHashMap);
/* 112 */       if (bool) {
/*     */         break;
/*     */       }
/* 115 */       getInterfaceMethods(paramClass, localHashMap);
/* 116 */       paramClass = paramClass.getSuperclass();
/*     */     }
/* 118 */     return (Method[])localHashMap.values().toArray(new Method[localHashMap.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void getInterfaceMethods(Class<?> paramClass, Map<Signature, Method> paramMap)
/*     */   {
/* 126 */     Class[] arrayOfClass = paramClass.getInterfaces();
/* 127 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 128 */       Class localClass = arrayOfClass[i];
/* 129 */       boolean bool = getInternalPublicMethods(localClass, paramMap);
/* 130 */       if (!bool) {
/* 131 */         getInterfaceMethods(localClass, paramMap);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean getInternalPublicMethods(Class<?> paramClass, Map<Signature, Method> paramMap)
/*     */   {
/* 142 */     Method[] arrayOfMethod = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 149 */       if (!Modifier.isPublic(paramClass.getModifiers())) {
/* 150 */         return false;
/*     */       }
/* 152 */       if (!ReflectUtil.isPackageAccessible(paramClass)) {
/* 153 */         return false;
/*     */       }
/*     */       
/* 156 */       arrayOfMethod = paramClass.getMethods();
/*     */     } catch (SecurityException localSecurityException) {
/* 158 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 167 */     boolean bool = true;
/* 168 */     Class localClass; for (int i = 0; i < arrayOfMethod.length; i++) {
/* 169 */       localClass = arrayOfMethod[i].getDeclaringClass();
/* 170 */       if (!Modifier.isPublic(localClass.getModifiers())) {
/* 171 */         bool = false;
/* 172 */         break;
/*     */       }
/*     */     }
/*     */     
/* 176 */     if (bool)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 181 */       for (i = 0; i < arrayOfMethod.length; i++) {
/* 182 */         addMethod(paramMap, arrayOfMethod[i]);
/*     */ 
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 189 */       for (i = 0; i < arrayOfMethod.length; i++) {
/* 190 */         localClass = arrayOfMethod[i].getDeclaringClass();
/* 191 */         if (paramClass.equals(localClass)) {
/* 192 */           addMethod(paramMap, arrayOfMethod[i]);
/*     */         }
/*     */       }
/*     */     }
/* 196 */     return bool;
/*     */   }
/*     */   
/*     */   private static void addMethod(Map<Signature, Method> paramMap, Method paramMethod) {
/* 200 */     Signature localSignature = new Signature(paramMethod);
/* 201 */     if (!paramMap.containsKey(localSignature)) {
/* 202 */       paramMap.put(localSignature, paramMethod);
/* 203 */     } else if (!paramMethod.getDeclaringClass().isInterface())
/*     */     {
/*     */ 
/*     */ 
/* 207 */       Method localMethod = (Method)paramMap.get(localSignature);
/* 208 */       if (localMethod.getDeclaringClass().isInterface()) {
/* 209 */         paramMap.put(localSignature, paramMethod);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class Signature
/*     */   {
/*     */     private String methodName;
/*     */     
/*     */     private Class<?>[] argClasses;
/*     */     
/* 222 */     private volatile int hashCode = 0;
/*     */     
/*     */     Signature(Method paramMethod) {
/* 225 */       this.methodName = paramMethod.getName();
/* 226 */       this.argClasses = paramMethod.getParameterTypes();
/*     */     }
/*     */     
/*     */     public boolean equals(Object paramObject) {
/* 230 */       if (this == paramObject) {
/* 231 */         return true;
/*     */       }
/* 233 */       Signature localSignature = (Signature)paramObject;
/* 234 */       if (!this.methodName.equals(localSignature.methodName)) {
/* 235 */         return false;
/*     */       }
/* 237 */       if (this.argClasses.length != localSignature.argClasses.length) {
/* 238 */         return false;
/*     */       }
/* 240 */       for (int i = 0; i < this.argClasses.length; i++) {
/* 241 */         if (this.argClasses[i] != localSignature.argClasses[i]) {
/* 242 */           return false;
/*     */         }
/*     */       }
/* 245 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 253 */       if (this.hashCode == 0) {
/* 254 */         int i = 17;
/* 255 */         i = 37 * i + this.methodName.hashCode();
/* 256 */         if (this.argClasses != null) {
/* 257 */           for (int j = 0; j < this.argClasses.length; j++)
/*     */           {
/* 259 */             i = 37 * i + (this.argClasses[j] == null ? 0 : this.argClasses[j].hashCode());
/*     */           }
/*     */         }
/* 262 */         this.hashCode = i;
/*     */       }
/* 264 */       return this.hashCode;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/*     */     try
/*     */     {
/* 275 */       return bounce.invoke(null, new Object[] { paramMethod, paramObject, paramArrayOfObject });
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 277 */       Throwable localThrowable = localInvocationTargetException.getCause();
/*     */       
/* 279 */       if ((localThrowable instanceof InvocationTargetException))
/* 280 */         throw ((InvocationTargetException)localThrowable);
/* 281 */       if ((localThrowable instanceof IllegalAccessException))
/* 282 */         throw ((IllegalAccessException)localThrowable);
/* 283 */       if ((localThrowable instanceof RuntimeException))
/* 284 */         throw ((RuntimeException)localThrowable);
/* 285 */       if ((localThrowable instanceof Error)) {
/* 286 */         throw ((Error)localThrowable);
/*     */       }
/* 288 */       throw new Error("Unexpected invocation error", localThrowable);
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException)
/*     */     {
/* 292 */       throw new Error("Unexpected invocation error", localIllegalAccessException);
/*     */     }
/*     */   }
/*     */   
/*     */   private static Method getTrampoline() {
/*     */     try {
/* 298 */       (Method)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Method run() throws Exception {
/* 301 */           Class localClass = MethodUtil.access$000();
/* 302 */           Class[] arrayOfClass = { Method.class, Object.class, Object[].class };
/*     */           
/*     */ 
/* 305 */           Method localMethod = localClass.getDeclaredMethod("invoke", arrayOfClass);
/* 306 */           localMethod.setAccessible(true);
/* 307 */           return localMethod;
/*     */         }
/*     */       });
/*     */     } catch (Exception localException) {
/* 311 */       throw new InternalError("bouncer cannot be found", localException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected synchronized Class<?> loadClass(String paramString, boolean paramBoolean)
/*     */     throws ClassNotFoundException
/*     */   {
/* 320 */     ReflectUtil.checkPackageAccess(paramString);
/* 321 */     Class localClass = findLoadedClass(paramString);
/* 322 */     if (localClass == null) {
/*     */       try {
/* 324 */         localClass = findClass(paramString);
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException) {}
/*     */       
/* 328 */       if (localClass == null) {
/* 329 */         localClass = getParent().loadClass(paramString);
/*     */       }
/*     */     }
/* 332 */     if (paramBoolean) {
/* 333 */       resolveClass(localClass);
/*     */     }
/* 335 */     return localClass;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Class<?> findClass(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/* 342 */     if (!paramString.startsWith("sun.reflect.misc.")) {
/* 343 */       throw new ClassNotFoundException(paramString);
/*     */     }
/* 345 */     String str = paramString.replace('.', '/').concat(".class");
/* 346 */     URL localURL = getResource(str);
/* 347 */     if (localURL != null) {
/*     */       try {
/* 349 */         return defineClass(paramString, localURL);
/*     */       } catch (IOException localIOException) {
/* 351 */         throw new ClassNotFoundException(paramString, localIOException);
/*     */       }
/*     */     }
/* 354 */     throw new ClassNotFoundException(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Class<?> defineClass(String paramString, URL paramURL)
/*     */     throws IOException
/*     */   {
/* 363 */     byte[] arrayOfByte = getBytes(paramURL);
/* 364 */     CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
/* 365 */     if (!paramString.equals("sun.reflect.misc.Trampoline")) {
/* 366 */       throw new IOException("MethodUtil: bad name " + paramString);
/*     */     }
/* 368 */     return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length, localCodeSource);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static byte[] getBytes(URL paramURL)
/*     */     throws IOException
/*     */   {
/* 376 */     URLConnection localURLConnection = paramURL.openConnection();
/* 377 */     if ((localURLConnection instanceof HttpURLConnection)) {
/* 378 */       HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURLConnection;
/* 379 */       int j = localHttpURLConnection.getResponseCode();
/* 380 */       if (j >= 400) {
/* 381 */         throw new IOException("open HTTP connection failed.");
/*     */       }
/*     */     }
/* 384 */     int i = localURLConnection.getContentLength();
/* 385 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream(localURLConnection.getInputStream());
/*     */     byte[] arrayOfByte;
/*     */     try
/*     */     {
/* 389 */       arrayOfByte = IOUtils.readFully(localBufferedInputStream, i, true);
/*     */     } finally {
/* 391 */       localBufferedInputStream.close();
/*     */     }
/* 393 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */   protected PermissionCollection getPermissions(CodeSource paramCodeSource)
/*     */   {
/* 399 */     PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
/* 400 */     localPermissionCollection.add(new AllPermission());
/* 401 */     return localPermissionCollection;
/*     */   }
/*     */   
/*     */   private static Class<?> getTrampolineClass() {
/*     */     try {
/* 406 */       return Class.forName("sun.reflect.misc.Trampoline", true, new MethodUtil());
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {}
/* 409 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\misc\MethodUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.rmi.server;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.StubNotFoundException;
/*     */ import java.rmi.server.LogStream;
/*     */ import java.rmi.server.RemoteObjectInvocationHandler;
/*     */ import java.rmi.server.RemoteRef;
/*     */ import java.rmi.server.RemoteStub;
/*     */ import java.rmi.server.Skeleton;
/*     */ import java.rmi.server.SkeletonNotFoundException;
/*     */ import java.security.AccessController;
/*     */ import java.security.DigestOutputStream;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Util
/*     */ {
/*  71 */   static final int logLevel = LogStream.parseLevel(
/*  72 */     (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.server.logLevel")));
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  77 */   public static final Log serverRefLog = Log.getLog("sun.rmi.server.ref", "transport", logLevel);
/*     */   
/*     */ 
/*     */ 
/*  81 */   private static final boolean ignoreStubClasses = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.rmi.server.ignoreStubClasses")))
/*     */   
/*  83 */     .booleanValue();
/*     */   
/*     */ 
/*     */ 
/*  87 */   private static final Map<Class<?>, Void> withoutStubs = Collections.synchronizedMap(new WeakHashMap(11));
/*     */   
/*     */ 
/*  90 */   private static final Class<?>[] stubConsParamTypes = { RemoteRef.class };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Remote createProxy(Class<?> paramClass, RemoteRef paramRemoteRef, boolean paramBoolean)
/*     */     throws StubNotFoundException
/*     */   {
/*     */     Class localClass;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 132 */       localClass = getRemoteClass(paramClass);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException)
/*     */     {
/* 136 */       throw new StubNotFoundException("object does not implement a remote interface: " + paramClass.getName());
/*     */     }
/*     */     
/* 139 */     if ((paramBoolean) || ((!ignoreStubClasses) && 
/* 140 */       (stubClassExists(localClass))))
/*     */     {
/* 142 */       return createStub(localClass, paramRemoteRef);
/*     */     }
/*     */     
/* 145 */     ClassLoader localClassLoader = paramClass.getClassLoader();
/* 146 */     final Class[] arrayOfClass = getRemoteInterfaces(paramClass);
/* 147 */     final RemoteObjectInvocationHandler localRemoteObjectInvocationHandler = new RemoteObjectInvocationHandler(paramRemoteRef);
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 153 */       (Remote)AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public Remote run() {
/* 155 */           return (Remote)Proxy.newProxyInstance(this.val$loader, arrayOfClass, localRemoteObjectInvocationHandler);
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/* 160 */       throw new StubNotFoundException("unable to create proxy", localIllegalArgumentException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean stubClassExists(Class<?> paramClass)
/*     */   {
/* 171 */     if (!withoutStubs.containsKey(paramClass)) {
/*     */       try {
/* 173 */         Class.forName(paramClass.getName() + "_Stub", false, paramClass
/*     */         
/* 175 */           .getClassLoader());
/* 176 */         return true;
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException) {
/* 179 */         withoutStubs.put(paramClass, null);
/*     */       }
/*     */     }
/* 182 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Class<?> getRemoteClass(Class<?> paramClass)
/*     */     throws ClassNotFoundException
/*     */   {
/* 193 */     while (paramClass != null) {
/* 194 */       Class[] arrayOfClass = paramClass.getInterfaces();
/* 195 */       for (int i = arrayOfClass.length - 1; i >= 0; i--) {
/* 196 */         if (Remote.class.isAssignableFrom(arrayOfClass[i]))
/* 197 */           return paramClass;
/*     */       }
/* 199 */       paramClass = paramClass.getSuperclass();
/*     */     }
/* 201 */     throw new ClassNotFoundException("class does not implement java.rmi.Remote");
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
/*     */   private static Class<?>[] getRemoteInterfaces(Class<?> paramClass)
/*     */   {
/* 215 */     ArrayList localArrayList = new ArrayList();
/* 216 */     getRemoteInterfaces(localArrayList, paramClass);
/* 217 */     return (Class[])localArrayList.toArray(new Class[localArrayList.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void getRemoteInterfaces(ArrayList<Class<?>> paramArrayList, Class<?> paramClass)
/*     */   {
/* 229 */     Class localClass1 = paramClass.getSuperclass();
/* 230 */     if (localClass1 != null) {
/* 231 */       getRemoteInterfaces(paramArrayList, localClass1);
/*     */     }
/*     */     
/* 234 */     Class[] arrayOfClass = paramClass.getInterfaces();
/* 235 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 236 */       Class localClass2 = arrayOfClass[i];
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 242 */       if ((Remote.class.isAssignableFrom(localClass2)) && 
/* 243 */         (!paramArrayList.contains(localClass2))) {
/* 244 */         Method[] arrayOfMethod = localClass2.getMethods();
/* 245 */         for (int j = 0; j < arrayOfMethod.length; j++) {
/* 246 */           checkMethod(arrayOfMethod[j]);
/*     */         }
/* 248 */         paramArrayList.add(localClass2);
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
/*     */   private static void checkMethod(Method paramMethod)
/*     */   {
/* 262 */     Class[] arrayOfClass = paramMethod.getExceptionTypes();
/* 263 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 264 */       if (arrayOfClass[i].isAssignableFrom(RemoteException.class))
/* 265 */         return;
/*     */     }
/* 267 */     throw new IllegalArgumentException("illegal remote method encountered: " + paramMethod);
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
/*     */   private static RemoteStub createStub(Class<?> paramClass, RemoteRef paramRemoteRef)
/*     */     throws StubNotFoundException
/*     */   {
/* 283 */     String str = paramClass.getName() + "_Stub";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 292 */       Class localClass = Class.forName(str, false, paramClass.getClassLoader());
/* 293 */       Constructor localConstructor = localClass.getConstructor(stubConsParamTypes);
/* 294 */       return (RemoteStub)localConstructor.newInstance(new Object[] { paramRemoteRef });
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/* 297 */       throw new StubNotFoundException("Stub class not found: " + str, localClassNotFoundException);
/*     */     }
/*     */     catch (NoSuchMethodException localNoSuchMethodException) {
/* 300 */       throw new StubNotFoundException("Stub class missing constructor: " + str, localNoSuchMethodException);
/*     */     }
/*     */     catch (InstantiationException localInstantiationException) {
/* 303 */       throw new StubNotFoundException("Can't create instance of stub class: " + str, localInstantiationException);
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 306 */       throw new StubNotFoundException("Stub class constructor not public: " + str, localIllegalAccessException);
/*     */     }
/*     */     catch (InvocationTargetException localInvocationTargetException) {
/* 309 */       throw new StubNotFoundException("Exception creating instance of stub class: " + str, localInvocationTargetException);
/*     */     }
/*     */     catch (ClassCastException localClassCastException) {
/* 312 */       throw new StubNotFoundException("Stub class not instance of RemoteStub: " + str, localClassCastException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static Skeleton createSkeleton(Remote paramRemote)
/*     */     throws SkeletonNotFoundException
/*     */   {
/*     */     Class localClass1;
/*     */     
/*     */     try
/*     */     {
/* 325 */       localClass1 = getRemoteClass(paramRemote.getClass());
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException1)
/*     */     {
/* 329 */       throw new SkeletonNotFoundException("object does not implement a remote interface: " + paramRemote.getClass().getName());
/*     */     }
/*     */     
/*     */ 
/* 333 */     String str = localClass1.getName() + "_Skel";
/*     */     try {
/* 335 */       Class localClass2 = Class.forName(str, false, localClass1.getClassLoader());
/*     */       
/* 337 */       return (Skeleton)localClass2.newInstance();
/*     */     } catch (ClassNotFoundException localClassNotFoundException2) {
/* 339 */       throw new SkeletonNotFoundException("Skeleton class not found: " + str, localClassNotFoundException2);
/*     */     }
/*     */     catch (InstantiationException localInstantiationException) {
/* 342 */       throw new SkeletonNotFoundException("Can't create skeleton: " + str, localInstantiationException);
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 345 */       throw new SkeletonNotFoundException("No public constructor: " + str, localIllegalAccessException);
/*     */     }
/*     */     catch (ClassCastException localClassCastException) {
/* 348 */       throw new SkeletonNotFoundException("Skeleton not of correct class: " + str, localClassCastException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long computeMethodHash(Method paramMethod)
/*     */   {
/* 359 */     long l = 0L;
/* 360 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(127);
/*     */     try {
/* 362 */       MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
/* 363 */       DataOutputStream localDataOutputStream = new DataOutputStream(new DigestOutputStream(localByteArrayOutputStream, localMessageDigest));
/*     */       
/*     */ 
/* 366 */       String str = getMethodNameAndDescriptor(paramMethod);
/* 367 */       if (serverRefLog.isLoggable(Log.VERBOSE)) {
/* 368 */         serverRefLog.log(Log.VERBOSE, "string used for method hash: \"" + str + "\"");
/*     */       }
/*     */       
/* 371 */       localDataOutputStream.writeUTF(str);
/*     */       
/*     */ 
/* 374 */       localDataOutputStream.flush();
/* 375 */       byte[] arrayOfByte = localMessageDigest.digest();
/* 376 */       for (int i = 0; i < Math.min(8, arrayOfByte.length); i++) {
/* 377 */         l += ((arrayOfByte[i] & 0xFF) << i * 8);
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException) {
/* 381 */       l = -1L;
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 383 */       throw new SecurityException(localNoSuchAlgorithmException.getMessage());
/*     */     }
/* 385 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getMethodNameAndDescriptor(Method paramMethod)
/*     */   {
/* 397 */     StringBuffer localStringBuffer = new StringBuffer(paramMethod.getName());
/* 398 */     localStringBuffer.append('(');
/* 399 */     Class[] arrayOfClass = paramMethod.getParameterTypes();
/* 400 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 401 */       localStringBuffer.append(getTypeDescriptor(arrayOfClass[i]));
/*     */     }
/* 403 */     localStringBuffer.append(')');
/* 404 */     Class localClass = paramMethod.getReturnType();
/* 405 */     if (localClass == Void.TYPE) {
/* 406 */       localStringBuffer.append('V');
/*     */     } else {
/* 408 */       localStringBuffer.append(getTypeDescriptor(localClass));
/*     */     }
/* 410 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getTypeDescriptor(Class<?> paramClass)
/*     */   {
/* 418 */     if (paramClass.isPrimitive()) {
/* 419 */       if (paramClass == Integer.TYPE)
/* 420 */         return "I";
/* 421 */       if (paramClass == Boolean.TYPE)
/* 422 */         return "Z";
/* 423 */       if (paramClass == Byte.TYPE)
/* 424 */         return "B";
/* 425 */       if (paramClass == Character.TYPE)
/* 426 */         return "C";
/* 427 */       if (paramClass == Short.TYPE)
/* 428 */         return "S";
/* 429 */       if (paramClass == Long.TYPE)
/* 430 */         return "J";
/* 431 */       if (paramClass == Float.TYPE)
/* 432 */         return "F";
/* 433 */       if (paramClass == Double.TYPE)
/* 434 */         return "D";
/* 435 */       if (paramClass == Void.TYPE) {
/* 436 */         return "V";
/*     */       }
/* 438 */       throw new Error("unrecognized primitive type: " + paramClass);
/*     */     }
/* 440 */     if (paramClass.isArray())
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 448 */       return paramClass.getName().replace('.', '/');
/*     */     }
/* 450 */     return "L" + paramClass.getName().replace('.', '/') + ";";
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
/*     */   public static String getUnqualifiedName(Class<?> paramClass)
/*     */   {
/* 463 */     String str = paramClass.getName();
/* 464 */     return str.substring(str.lastIndexOf('.') + 1);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
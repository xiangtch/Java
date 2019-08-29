/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectInputStream.GetField;
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.AnnotationFormatError;
/*     */ import java.lang.annotation.IncompleteAnnotationException;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class AnnotationInvocationHandler
/*     */   implements InvocationHandler, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 6182022883658399397L;
/*     */   private final Class<? extends Annotation> type;
/*     */   private final Map<String, Object> memberValues;
/*     */   
/*     */   AnnotationInvocationHandler(Class<? extends Annotation> paramClass, Map<String, Object> paramMap)
/*     */   {
/*  48 */     Class[] arrayOfClass = paramClass.getInterfaces();
/*  49 */     if ((!paramClass.isAnnotation()) || (arrayOfClass.length != 1) || (arrayOfClass[0] != Annotation.class))
/*     */     {
/*     */ 
/*  52 */       throw new AnnotationFormatError("Attempt to create proxy for a non-annotation type."); }
/*  53 */     this.type = paramClass;
/*  54 */     this.memberValues = paramMap;
/*     */   }
/*     */   
/*     */   public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) {
/*  58 */     String str = paramMethod.getName();
/*  59 */     Class[] arrayOfClass = paramMethod.getParameterTypes();
/*     */     
/*     */ 
/*  62 */     if ((str.equals("equals")) && (arrayOfClass.length == 1) && (arrayOfClass[0] == Object.class))
/*     */     {
/*  64 */       return equalsImpl(paramArrayOfObject[0]); }
/*  65 */     if (arrayOfClass.length != 0) {
/*  66 */       throw new AssertionError("Too many parameters for an annotation method");
/*     */     }
/*  68 */     Object localObject = str;int i = -1; switch (((String)localObject).hashCode()) {case -1776922004:  if (((String)localObject).equals("toString")) i = 0; break; case 147696667:  if (((String)localObject).equals("hashCode")) i = 1; break; case 1444986633:  if (((String)localObject).equals("annotationType")) i = 2; break; } switch (i) {
/*     */     case 0: 
/*  70 */       return toStringImpl();
/*     */     case 1: 
/*  72 */       return Integer.valueOf(hashCodeImpl());
/*     */     case 2: 
/*  74 */       return this.type;
/*     */     }
/*     */     
/*     */     
/*  78 */     localObject = this.memberValues.get(str);
/*     */     
/*  80 */     if (localObject == null) {
/*  81 */       throw new IncompleteAnnotationException(this.type, str);
/*     */     }
/*  83 */     if ((localObject instanceof ExceptionProxy)) {
/*  84 */       throw ((ExceptionProxy)localObject).generateException();
/*     */     }
/*  86 */     if ((localObject.getClass().isArray()) && (Array.getLength(localObject) != 0)) {
/*  87 */       localObject = cloneArray(localObject);
/*     */     }
/*  89 */     return localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object cloneArray(Object paramObject)
/*     */   {
/*  97 */     Class localClass = paramObject.getClass();
/*     */     
/*  99 */     if (localClass == byte[].class) {
/* 100 */       localObject = (byte[])paramObject;
/* 101 */       return ((byte[])localObject).clone();
/*     */     }
/* 103 */     if (localClass == char[].class) {
/* 104 */       localObject = (char[])paramObject;
/* 105 */       return ((char[])localObject).clone();
/*     */     }
/* 107 */     if (localClass == double[].class) {
/* 108 */       localObject = (double[])paramObject;
/* 109 */       return ((double[])localObject).clone();
/*     */     }
/* 111 */     if (localClass == float[].class) {
/* 112 */       localObject = (float[])paramObject;
/* 113 */       return ((float[])localObject).clone();
/*     */     }
/* 115 */     if (localClass == int[].class) {
/* 116 */       localObject = (int[])paramObject;
/* 117 */       return ((int[])localObject).clone();
/*     */     }
/* 119 */     if (localClass == long[].class) {
/* 120 */       localObject = (long[])paramObject;
/* 121 */       return ((long[])localObject).clone();
/*     */     }
/* 123 */     if (localClass == short[].class) {
/* 124 */       localObject = (short[])paramObject;
/* 125 */       return ((short[])localObject).clone();
/*     */     }
/* 127 */     if (localClass == boolean[].class) {
/* 128 */       localObject = (boolean[])paramObject;
/* 129 */       return ((boolean[])localObject).clone();
/*     */     }
/*     */     
/* 132 */     Object localObject = (Object[])paramObject;
/* 133 */     return ((Object[])localObject).clone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String toStringImpl()
/*     */   {
/* 141 */     StringBuilder localStringBuilder = new StringBuilder(128);
/* 142 */     localStringBuilder.append('@');
/* 143 */     localStringBuilder.append(this.type.getName());
/* 144 */     localStringBuilder.append('(');
/* 145 */     int i = 1;
/* 146 */     for (Entry localEntry : this.memberValues.entrySet()) {
/* 147 */       if (i != 0) {
/* 148 */         i = 0;
/*     */       } else {
/* 150 */         localStringBuilder.append(", ");
/*     */       }
/* 152 */       localStringBuilder.append((String)localEntry.getKey());
/* 153 */       localStringBuilder.append('=');
/* 154 */       localStringBuilder.append(memberValueToString(localEntry.getValue()));
/*     */     }
/* 156 */     localStringBuilder.append(')');
/* 157 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String memberValueToString(Object paramObject)
/*     */   {
/* 164 */     Class localClass = paramObject.getClass();
/* 165 */     if (!localClass.isArray())
/*     */     {
/* 167 */       return paramObject.toString();
/*     */     }
/* 169 */     if (localClass == byte[].class)
/* 170 */       return Arrays.toString((byte[])paramObject);
/* 171 */     if (localClass == char[].class)
/* 172 */       return Arrays.toString((char[])paramObject);
/* 173 */     if (localClass == double[].class)
/* 174 */       return Arrays.toString((double[])paramObject);
/* 175 */     if (localClass == float[].class)
/* 176 */       return Arrays.toString((float[])paramObject);
/* 177 */     if (localClass == int[].class)
/* 178 */       return Arrays.toString((int[])paramObject);
/* 179 */     if (localClass == long[].class)
/* 180 */       return Arrays.toString((long[])paramObject);
/* 181 */     if (localClass == short[].class)
/* 182 */       return Arrays.toString((short[])paramObject);
/* 183 */     if (localClass == boolean[].class)
/* 184 */       return Arrays.toString((boolean[])paramObject);
/* 185 */     return Arrays.toString((Object[])paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Boolean equalsImpl(Object paramObject)
/*     */   {
/* 192 */     if (paramObject == this) {
/* 193 */       return Boolean.valueOf(true);
/*     */     }
/* 195 */     if (!this.type.isInstance(paramObject))
/* 196 */       return Boolean.valueOf(false);
/* 197 */     for (Method localMethod : getMemberMethods()) {
/* 198 */       String str = localMethod.getName();
/* 199 */       Object localObject1 = this.memberValues.get(str);
/* 200 */       Object localObject2 = null;
/* 201 */       AnnotationInvocationHandler localAnnotationInvocationHandler = asOneOfUs(paramObject);
/* 202 */       if (localAnnotationInvocationHandler != null) {
/* 203 */         localObject2 = localAnnotationInvocationHandler.memberValues.get(str);
/*     */       } else {
/*     */         try {
/* 206 */           localObject2 = localMethod.invoke(paramObject, new Object[0]);
/*     */         } catch (InvocationTargetException localInvocationTargetException) {
/* 208 */           return Boolean.valueOf(false);
/*     */         } catch (IllegalAccessException localIllegalAccessException) {
/* 210 */           throw new AssertionError(localIllegalAccessException);
/*     */         }
/*     */       }
/* 213 */       if (!memberValueEquals(localObject1, localObject2))
/* 214 */         return Boolean.valueOf(false);
/*     */     }
/* 216 */     return Boolean.valueOf(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private AnnotationInvocationHandler asOneOfUs(Object paramObject)
/*     */   {
/* 225 */     if (Proxy.isProxyClass(paramObject.getClass())) {
/* 226 */       InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(paramObject);
/* 227 */       if ((localInvocationHandler instanceof AnnotationInvocationHandler))
/* 228 */         return (AnnotationInvocationHandler)localInvocationHandler;
/*     */     }
/* 230 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean memberValueEquals(Object paramObject1, Object paramObject2)
/*     */   {
/* 242 */     Class localClass = paramObject1.getClass();
/*     */     
/*     */ 
/*     */ 
/* 246 */     if (!localClass.isArray()) {
/* 247 */       return paramObject1.equals(paramObject2);
/*     */     }
/*     */     
/*     */ 
/* 251 */     if (((paramObject1 instanceof Object[])) && ((paramObject2 instanceof Object[]))) {
/* 252 */       return Arrays.equals((Object[])paramObject1, (Object[])paramObject2);
/*     */     }
/*     */     
/* 255 */     if (paramObject2.getClass() != localClass) {
/* 256 */       return false;
/*     */     }
/*     */     
/* 259 */     if (localClass == byte[].class)
/* 260 */       return Arrays.equals((byte[])paramObject1, (byte[])paramObject2);
/* 261 */     if (localClass == char[].class)
/* 262 */       return Arrays.equals((char[])paramObject1, (char[])paramObject2);
/* 263 */     if (localClass == double[].class)
/* 264 */       return Arrays.equals((double[])paramObject1, (double[])paramObject2);
/* 265 */     if (localClass == float[].class)
/* 266 */       return Arrays.equals((float[])paramObject1, (float[])paramObject2);
/* 267 */     if (localClass == int[].class)
/* 268 */       return Arrays.equals((int[])paramObject1, (int[])paramObject2);
/* 269 */     if (localClass == long[].class)
/* 270 */       return Arrays.equals((long[])paramObject1, (long[])paramObject2);
/* 271 */     if (localClass == short[].class)
/* 272 */       return Arrays.equals((short[])paramObject1, (short[])paramObject2);
/* 273 */     assert (localClass == boolean[].class);
/* 274 */     return Arrays.equals((boolean[])paramObject1, (boolean[])paramObject2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Method[] getMemberMethods()
/*     */   {
/* 284 */     if (this.memberMethods == null) {
/* 285 */       this.memberMethods = ((Method[])AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Method[] run() {
/* 288 */           Method[] arrayOfMethod = AnnotationInvocationHandler.this.type.getDeclaredMethods();
/* 289 */           AnnotationInvocationHandler.this.validateAnnotationMethods(arrayOfMethod);
/* 290 */           AccessibleObject.setAccessible(arrayOfMethod, true);
/* 291 */           return arrayOfMethod;
/*     */         }
/*     */       }));
/*     */     }
/* 295 */     return this.memberMethods; }
/*     */   
/* 297 */   private volatile transient Method[] memberMethods = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void validateAnnotationMethods(Method[] paramArrayOfMethod)
/*     */   {
/* 311 */     int i = 1;
/* 312 */     for (Method localMethod : paramArrayOfMethod)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 323 */       if ((localMethod.getModifiers() != 1025) || 
/* 324 */         (localMethod.isDefault()) || 
/* 325 */         (localMethod.getParameterCount() != 0) || 
/* 326 */         (localMethod.getExceptionTypes().length != 0)) {
/* 327 */         i = 0;
/* 328 */         break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 340 */       Class localClass = localMethod.getReturnType();
/* 341 */       if (localClass.isArray()) {
/* 342 */         localClass = localClass.getComponentType();
/* 343 */         if (localClass.isArray()) {
/* 344 */           i = 0;
/* 345 */           break;
/*     */         }
/*     */       }
/*     */       
/* 349 */       if (((!localClass.isPrimitive()) || (localClass == Void.TYPE)) && (localClass != String.class) && (localClass != Class.class))
/*     */       {
/*     */ 
/* 352 */         if ((!localClass.isEnum()) && 
/* 353 */           (!localClass.isAnnotation())) {
/* 354 */           i = 0;
/* 355 */           break;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 373 */       String str = localMethod.getName();
/* 374 */       if (((str.equals("toString")) && (localClass == String.class)) || 
/* 375 */         ((str.equals("hashCode")) && (localClass == Integer.TYPE)) || (
/* 376 */         (str.equals("annotationType")) && (localClass == Class.class))) {
/* 377 */         i = 0;
/* 378 */         break;
/*     */       }
/*     */     }
/* 381 */     if (i != 0) {
/* 382 */       return;
/*     */     }
/* 384 */     throw new AnnotationFormatError("Malformed method on an annotation type");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int hashCodeImpl()
/*     */   {
/* 391 */     int i = 0;
/* 392 */     for (Entry localEntry : this.memberValues.entrySet())
/*     */     {
/* 394 */       i = i + (127 * ((String)localEntry.getKey()).hashCode() ^ memberValueHashCode(localEntry.getValue()));
/*     */     }
/* 396 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static int memberValueHashCode(Object paramObject)
/*     */   {
/* 403 */     Class localClass = paramObject.getClass();
/* 404 */     if (!localClass.isArray())
/*     */     {
/* 406 */       return paramObject.hashCode();
/*     */     }
/* 408 */     if (localClass == byte[].class)
/* 409 */       return Arrays.hashCode((byte[])paramObject);
/* 410 */     if (localClass == char[].class)
/* 411 */       return Arrays.hashCode((char[])paramObject);
/* 412 */     if (localClass == double[].class)
/* 413 */       return Arrays.hashCode((double[])paramObject);
/* 414 */     if (localClass == float[].class)
/* 415 */       return Arrays.hashCode((float[])paramObject);
/* 416 */     if (localClass == int[].class)
/* 417 */       return Arrays.hashCode((int[])paramObject);
/* 418 */     if (localClass == long[].class)
/* 419 */       return Arrays.hashCode((long[])paramObject);
/* 420 */     if (localClass == short[].class)
/* 421 */       return Arrays.hashCode((short[])paramObject);
/* 422 */     if (localClass == boolean[].class)
/* 423 */       return Arrays.hashCode((boolean[])paramObject);
/* 424 */     return Arrays.hashCode((Object[])paramObject);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException
/*     */   {
/* 429 */     GetField localGetField = paramObjectInputStream.readFields();
/*     */     
/*     */ 
/* 432 */     Class localClass1 = (Class)localGetField.get("type", null);
/*     */     
/* 434 */     Map localMap1 = (Map)localGetField.get("memberValues", null);
/*     */     
/*     */ 
/*     */ 
/* 438 */     AnnotationType localAnnotationType = null;
/*     */     try {
/* 440 */       localAnnotationType = AnnotationType.getInstance(localClass1);
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/* 443 */       throw new InvalidObjectException("Non-annotation type in annotation serial stream");
/*     */     }
/*     */     
/* 446 */     Map localMap2 = localAnnotationType.memberTypes();
/*     */     
/* 448 */     LinkedHashMap localLinkedHashMap = new LinkedHashMap();
/*     */     
/*     */ 
/*     */ 
/* 452 */     for (Entry localEntry : localMap1.entrySet()) {
/* 453 */       String str = (String)localEntry.getKey();
/* 454 */       Object localObject = null;
/* 455 */       Class localClass2 = (Class)localMap2.get(str);
/* 456 */       if (localClass2 != null) {
/* 457 */         localObject = localEntry.getValue();
/* 458 */         if ((!localClass2.isInstance(localObject)) && (!(localObject instanceof ExceptionProxy)))
/*     */         {
/*     */ 
/* 461 */           localObject = new AnnotationTypeMismatchExceptionProxy(localObject.getClass() + "[" + localObject + "]").setMember(
/* 462 */             (Method)localAnnotationType.members().get(str));
/*     */         }
/*     */       }
/* 465 */       localLinkedHashMap.put(str, localObject);
/*     */     }
/*     */     
/* 468 */     UnsafeAccessor.setType(this, localClass1);
/* 469 */     UnsafeAccessor.setMemberValues(this, localLinkedHashMap);
/*     */   }
/*     */   
/*     */   private static class UnsafeAccessor {
/*     */     private static final Unsafe unsafe;
/*     */     private static final long typeOffset;
/*     */     private static final long memberValuesOffset;
/*     */     
/*     */     static {
/* 478 */       try { unsafe = Unsafe.getUnsafe();
/*     */         
/* 480 */         typeOffset = unsafe.objectFieldOffset(AnnotationInvocationHandler.class.getDeclaredField("type"));
/*     */         
/* 482 */         memberValuesOffset = unsafe.objectFieldOffset(AnnotationInvocationHandler.class.getDeclaredField("memberValues"));
/*     */       } catch (Exception localException) {
/* 484 */         throw new ExceptionInInitializerError(localException);
/*     */       }
/*     */     }
/*     */     
/*     */     static void setType(AnnotationInvocationHandler paramAnnotationInvocationHandler, Class<? extends Annotation> paramClass) {
/* 489 */       unsafe.putObject(paramAnnotationInvocationHandler, typeOffset, paramClass);
/*     */     }
/*     */     
/*     */     static void setMemberValues(AnnotationInvocationHandler paramAnnotationInvocationHandler, Map<String, Object> paramMap)
/*     */     {
/* 494 */       unsafe.putObject(paramAnnotationInvocationHandler, memberValuesOffset, paramMap);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\annotation\AnnotationInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
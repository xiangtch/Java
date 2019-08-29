/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Reflect
/*     */ {
/*     */   private static class ReflectionError
/*     */     extends Error
/*     */   {
/*     */     private static final long serialVersionUID = -8659519328078164097L;
/*     */     
/*     */     ReflectionError(Throwable paramThrowable)
/*     */     {
/*  41 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   private static void setAccessible(AccessibleObject paramAccessibleObject) {
/*  46 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/*  48 */         this.val$ao.setAccessible(true);
/*  49 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   static Constructor<?> lookupConstructor(String paramString, Class<?>[] paramArrayOfClass)
/*     */   {
/*     */     try {
/*  57 */       Class localClass = Class.forName(paramString);
/*  58 */       Constructor localConstructor = localClass.getDeclaredConstructor(paramArrayOfClass);
/*  59 */       setAccessible(localConstructor);
/*  60 */       return localConstructor;
/*     */     } catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException) {
/*  62 */       throw new ReflectionError(localClassNotFoundException);
/*     */     }
/*     */   }
/*     */   
/*     */   static Object invoke(Constructor<?> paramConstructor, Object[] paramArrayOfObject) {
/*     */     try {
/*  68 */       return paramConstructor.newInstance(paramArrayOfObject);
/*     */     }
/*     */     catch (InstantiationException|IllegalAccessException|InvocationTargetException localInstantiationException)
/*     */     {
/*  72 */       throw new ReflectionError(localInstantiationException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static Method lookupMethod(String paramString1, String paramString2, Class... paramVarArgs)
/*     */   {
/*     */     try
/*     */     {
/*  81 */       Class localClass = Class.forName(paramString1);
/*  82 */       Method localMethod = localClass.getDeclaredMethod(paramString2, paramVarArgs);
/*  83 */       setAccessible(localMethod);
/*  84 */       return localMethod;
/*     */     } catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException) {
/*  86 */       throw new ReflectionError(localClassNotFoundException);
/*     */     }
/*     */   }
/*     */   
/*     */   static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject) {
/*     */     try {
/*  92 */       return paramMethod.invoke(paramObject, paramArrayOfObject);
/*     */     } catch (IllegalAccessException|InvocationTargetException localIllegalAccessException) {
/*  94 */       throw new ReflectionError(localIllegalAccessException);
/*     */     }
/*     */   }
/*     */   
/*     */   static Object invokeIO(Method paramMethod, Object paramObject, Object[] paramArrayOfObject) throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 102 */       return paramMethod.invoke(paramObject, paramArrayOfObject);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 104 */       throw new ReflectionError(localIllegalAccessException);
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 106 */       if (IOException.class.isInstance(localInvocationTargetException.getCause()))
/* 107 */         throw ((IOException)localInvocationTargetException.getCause());
/* 108 */       throw new ReflectionError(localInvocationTargetException);
/*     */     }
/*     */   }
/*     */   
/*     */   static Field lookupField(String paramString1, String paramString2) {
/*     */     try {
/* 114 */       Class localClass = Class.forName(paramString1);
/* 115 */       Field localField = localClass.getDeclaredField(paramString2);
/* 116 */       setAccessible(localField);
/* 117 */       return localField;
/*     */     } catch (ClassNotFoundException|NoSuchFieldException localClassNotFoundException) {
/* 119 */       throw new ReflectionError(localClassNotFoundException);
/*     */     }
/*     */   }
/*     */   
/*     */   static Object get(Object paramObject, Field paramField) {
/*     */     try {
/* 125 */       return paramField.get(paramObject);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 127 */       throw new ReflectionError(localIllegalAccessException);
/*     */     }
/*     */   }
/*     */   
/*     */   static Object get(Field paramField) {
/* 132 */     return get(null, paramField);
/*     */   }
/*     */   
/*     */   static void set(Object paramObject1, Field paramField, Object paramObject2) {
/*     */     try {
/* 137 */       paramField.set(paramObject1, paramObject2);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 139 */       throw new ReflectionError(localIllegalAccessException);
/*     */     }
/*     */   }
/*     */   
/*     */   static void setInt(Object paramObject, Field paramField, int paramInt) {
/*     */     try {
/* 145 */       paramField.setInt(paramObject, paramInt);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 147 */       throw new ReflectionError(localIllegalAccessException);
/*     */     }
/*     */   }
/*     */   
/*     */   static void setBoolean(Object paramObject, Field paramField, boolean paramBoolean) {
/*     */     try {
/* 153 */       paramField.setBoolean(paramObject, paramBoolean);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 155 */       throw new ReflectionError(localIllegalAccessException);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\Reflect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
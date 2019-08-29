/*    */ package sun.reflect.misc;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.security.AccessController;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class Trampoline
/*    */ {
/*    */   static
/*    */   {
/* 50 */     if (Trampoline.class.getClassLoader() == null) {
/* 51 */       throw new Error("Trampoline must not be defined by the bootstrap classloader");
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   private static void ensureInvocableMethod(Method paramMethod)
/*    */     throws InvocationTargetException
/*    */   {
/* 59 */     Class localClass = paramMethod.getDeclaringClass();
/* 60 */     if ((localClass.equals(AccessController.class)) || 
/* 61 */       (localClass.equals(Method.class)) || 
/* 62 */       (localClass.getName().startsWith("java.lang.invoke."))) {
/* 63 */       throw new InvocationTargetException(new UnsupportedOperationException("invocation not supported"));
/*    */     }
/*    */   }
/*    */   
/*    */   private static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
/*    */     throws InvocationTargetException, IllegalAccessException
/*    */   {
/* 70 */     ensureInvocableMethod(paramMethod);
/* 71 */     return paramMethod.invoke(paramObject, paramArrayOfObject);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\misc\Trampoline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
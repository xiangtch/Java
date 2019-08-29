/*    */ package sun.reflect;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import sun.misc.Unsafe;
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
/*    */ class BootstrapConstructorAccessorImpl
/*    */   extends ConstructorAccessorImpl
/*    */ {
/*    */   private final Constructor<?> constructor;
/*    */   
/*    */   BootstrapConstructorAccessorImpl(Constructor<?> paramConstructor)
/*    */   {
/* 38 */     this.constructor = paramConstructor;
/*    */   }
/*    */   
/*    */   public Object newInstance(Object[] paramArrayOfObject) throws IllegalArgumentException, InvocationTargetException
/*    */   {
/*    */     try
/*    */     {
/* 45 */       return 
/* 46 */         UnsafeFieldAccessorImpl.unsafe.allocateInstance(this.constructor.getDeclaringClass());
/*    */     } catch (InstantiationException localInstantiationException) {
/* 48 */       throw new InvocationTargetException(localInstantiationException);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\BootstrapConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
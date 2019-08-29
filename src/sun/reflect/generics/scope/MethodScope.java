/*    */ package sun.reflect.generics.scope;
/*    */ 
/*    */ import java.lang.reflect.Method;
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
/*    */ public class MethodScope
/*    */   extends AbstractScope<Method>
/*    */ {
/*    */   private MethodScope(Method paramMethod)
/*    */   {
/* 39 */     super(paramMethod);
/*    */   }
/*    */   
/*    */ 
/*    */   private Class<?> getEnclosingClass()
/*    */   {
/* 45 */     return ((Method)getRecvr()).getDeclaringClass();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Scope computeEnclosingScope()
/*    */   {
/* 55 */     return ClassScope.make(getEnclosingClass());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static MethodScope make(Method paramMethod)
/*    */   {
/* 65 */     return new MethodScope(paramMethod);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\scope\MethodScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
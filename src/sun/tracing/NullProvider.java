/*    */ package sun.tracing;
/*    */ 
/*    */ import com.sun.tracing.Provider;
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
/*    */ class NullProvider
/*    */   extends ProviderSkeleton
/*    */ {
/*    */   NullProvider(Class<? extends Provider> paramClass)
/*    */   {
/* 63 */     super(paramClass);
/*    */   }
/*    */   
/*    */   protected ProbeSkeleton createProbe(Method paramMethod) {
/* 67 */     return new NullProbe(paramMethod.getParameterTypes());
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\NullProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
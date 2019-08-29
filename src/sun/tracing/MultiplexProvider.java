/*    */ package sun.tracing;
/*    */ 
/*    */ import com.sun.tracing.Provider;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Set;
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
/*    */ class MultiplexProvider
/*    */   extends ProviderSkeleton
/*    */ {
/*    */   private Set<Provider> providers;
/*    */   
/*    */   protected ProbeSkeleton createProbe(Method paramMethod)
/*    */   {
/* 77 */     return new MultiplexProbe(paramMethod, this.providers);
/*    */   }
/*    */   
/*    */   MultiplexProvider(Class<? extends Provider> paramClass, Set<Provider> paramSet) {
/* 81 */     super(paramClass);
/* 82 */     this.providers = paramSet;
/*    */   }
/*    */   
/*    */   public void dispose() {
/* 86 */     for (Provider localProvider : this.providers) {
/* 87 */       localProvider.dispose();
/*    */     }
/* 89 */     super.dispose();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\MultiplexProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.tracing;
/*    */ 
/*    */ import com.sun.tracing.Provider;
/*    */ import com.sun.tracing.ProviderFactory;
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
/*    */ public class NullProviderFactory
/*    */   extends ProviderFactory
/*    */ {
/*    */   public <T extends Provider> T createProvider(Class<T> paramClass)
/*    */   {
/* 54 */     NullProvider localNullProvider = new NullProvider(paramClass);
/* 55 */     localNullProvider.init();
/* 56 */     return localNullProvider.newProxyInstance();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\NullProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
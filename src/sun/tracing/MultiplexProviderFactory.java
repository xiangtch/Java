/*    */ package sun.tracing;
/*    */ 
/*    */ import com.sun.tracing.Provider;
/*    */ import com.sun.tracing.ProviderFactory;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
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
/*    */ public class MultiplexProviderFactory
/*    */   extends ProviderFactory
/*    */ {
/*    */   private Set<ProviderFactory> factories;
/*    */   
/*    */   public MultiplexProviderFactory(Set<ProviderFactory> paramSet)
/*    */   {
/* 58 */     this.factories = paramSet;
/*    */   }
/*    */   
/*    */   public <T extends Provider> T createProvider(Class<T> paramClass) {
/* 62 */     HashSet localHashSet = new HashSet();
/* 63 */     for (Object localObject = this.factories.iterator(); ((Iterator)localObject).hasNext();) { ProviderFactory localProviderFactory = (ProviderFactory)((Iterator)localObject).next();
/* 64 */       localHashSet.add(localProviderFactory.createProvider(paramClass));
/*    */     }
/* 66 */     localObject = new MultiplexProvider(paramClass, localHashSet);
/* 67 */     ((MultiplexProvider)localObject).init();
/* 68 */     return ((MultiplexProvider)localObject).newProxyInstance();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\MultiplexProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
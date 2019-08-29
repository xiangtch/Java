/*    */ package sun.reflect.generics.reflectiveObjects;
/*    */ 
/*    */ import sun.reflect.generics.factory.GenericsFactory;
/*    */ import sun.reflect.generics.visitor.Reifier;
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
/*    */ public abstract class LazyReflectiveObjectGenerator
/*    */ {
/*    */   private final GenericsFactory factory;
/*    */   
/*    */   protected LazyReflectiveObjectGenerator(GenericsFactory paramGenericsFactory)
/*    */   {
/* 46 */     this.factory = paramGenericsFactory;
/*    */   }
/*    */   
/*    */   private GenericsFactory getFactory()
/*    */   {
/* 51 */     return this.factory;
/*    */   }
/*    */   
/*    */   protected Reifier getReifier() {
/* 55 */     return Reifier.make(getFactory());
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\reflectiveObjects\LazyReflectiveObjectGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
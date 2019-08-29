/*    */ package sun.reflect.generics.tree;
/*    */ 
/*    */ import sun.reflect.generics.visitor.TypeTreeVisitor;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FloatSignature
/*    */   implements BaseType
/*    */ {
/* 32 */   private static final FloatSignature singleton = new FloatSignature();
/*    */   
/*    */ 
/*    */ 
/* 36 */   public static FloatSignature make() { return singleton; }
/*    */   
/* 38 */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitFloatSignature(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\FloatSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
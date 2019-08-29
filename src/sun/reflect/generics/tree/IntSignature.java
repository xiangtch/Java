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
/*    */ public class IntSignature
/*    */   implements BaseType
/*    */ {
/* 32 */   private static final IntSignature singleton = new IntSignature();
/*    */   
/*    */ 
/*    */ 
/* 36 */   public static IntSignature make() { return singleton; }
/*    */   
/* 38 */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitIntSignature(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\IntSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
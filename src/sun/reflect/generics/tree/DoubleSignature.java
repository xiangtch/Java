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
/*    */ public class DoubleSignature
/*    */   implements BaseType
/*    */ {
/* 32 */   private static final DoubleSignature singleton = new DoubleSignature();
/*    */   
/*    */ 
/*    */ 
/* 36 */   public static DoubleSignature make() { return singleton; }
/*    */   
/* 38 */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitDoubleSignature(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\DoubleSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
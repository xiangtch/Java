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
/*    */ public class BottomSignature
/*    */   implements FieldTypeSignature
/*    */ {
/* 31 */   private static final BottomSignature singleton = new BottomSignature();
/*    */   
/*    */ 
/*    */ 
/* 35 */   public static BottomSignature make() { return singleton; }
/*    */   
/* 37 */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitBottomSignature(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\BottomSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
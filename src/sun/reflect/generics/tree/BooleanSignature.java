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
/*    */ public class BooleanSignature
/*    */   implements BaseType
/*    */ {
/* 32 */   private static final BooleanSignature singleton = new BooleanSignature();
/*    */   
/*    */   public static BooleanSignature make()
/*    */   {
/* 36 */     return singleton;
/*    */   }
/*    */   
/* 39 */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitBooleanSignature(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\BooleanSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
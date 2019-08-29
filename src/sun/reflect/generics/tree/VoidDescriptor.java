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
/*    */ 
/*    */ public class VoidDescriptor
/*    */   implements ReturnType
/*    */ {
/* 33 */   private static final VoidDescriptor singleton = new VoidDescriptor();
/*    */   
/*    */   public static VoidDescriptor make()
/*    */   {
/* 37 */     return singleton;
/*    */   }
/*    */   
/*    */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
/* 41 */     paramTypeTreeVisitor.visitVoidDescriptor(this);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\VoidDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
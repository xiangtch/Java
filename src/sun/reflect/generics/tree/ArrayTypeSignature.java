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
/*    */ public class ArrayTypeSignature
/*    */   implements FieldTypeSignature
/*    */ {
/*    */   private final TypeSignature componentType;
/*    */   
/*    */   private ArrayTypeSignature(TypeSignature paramTypeSignature)
/*    */   {
/* 33 */     this.componentType = paramTypeSignature;
/*    */   }
/*    */   
/* 36 */   public static ArrayTypeSignature make(TypeSignature paramTypeSignature) { return new ArrayTypeSignature(paramTypeSignature); }
/*    */   
/*    */   public TypeSignature getComponentType() {
/* 39 */     return this.componentType;
/*    */   }
/*    */   
/* 42 */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitArrayTypeSignature(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\ArrayTypeSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
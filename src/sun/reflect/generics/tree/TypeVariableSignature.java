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
/*    */ public class TypeVariableSignature
/*    */   implements FieldTypeSignature
/*    */ {
/*    */   private final String identifier;
/*    */   
/*    */   private TypeVariableSignature(String paramString)
/*    */   {
/* 33 */     this.identifier = paramString;
/*    */   }
/*    */   
/*    */   public static TypeVariableSignature make(String paramString) {
/* 37 */     return new TypeVariableSignature(paramString);
/*    */   }
/*    */   
/* 40 */   public String getIdentifier() { return this.identifier; }
/*    */   
/*    */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
/* 43 */     paramTypeTreeVisitor.visitTypeVariableSignature(this);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\TypeVariableSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
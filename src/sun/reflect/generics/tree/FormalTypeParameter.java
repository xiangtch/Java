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
/*    */ public class FormalTypeParameter
/*    */   implements TypeTree
/*    */ {
/*    */   private final String name;
/*    */   private final FieldTypeSignature[] bounds;
/*    */   
/*    */   private FormalTypeParameter(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature)
/*    */   {
/* 36 */     this.name = paramString;
/* 37 */     this.bounds = paramArrayOfFieldTypeSignature;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static FormalTypeParameter make(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature)
/*    */   {
/* 48 */     return new FormalTypeParameter(paramString, paramArrayOfFieldTypeSignature);
/*    */   }
/*    */   
/* 51 */   public FieldTypeSignature[] getBounds() { return this.bounds; }
/* 52 */   public String getName() { return this.name; }
/*    */   
/* 54 */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitFormalTypeParameter(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\FormalTypeParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
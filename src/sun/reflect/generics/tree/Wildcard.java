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
/*    */ public class Wildcard
/*    */   implements TypeArgument
/*    */ {
/*    */   private FieldTypeSignature[] upperBounds;
/*    */   private FieldTypeSignature[] lowerBounds;
/*    */   
/*    */   private Wildcard(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2)
/*    */   {
/* 35 */     this.upperBounds = paramArrayOfFieldTypeSignature1;
/* 36 */     this.lowerBounds = paramArrayOfFieldTypeSignature2;
/*    */   }
/*    */   
/* 39 */   private static final FieldTypeSignature[] emptyBounds = new FieldTypeSignature[0];
/*    */   
/*    */   public static Wildcard make(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2)
/*    */   {
/* 43 */     return new Wildcard(paramArrayOfFieldTypeSignature1, paramArrayOfFieldTypeSignature2);
/*    */   }
/*    */   
/*    */   public FieldTypeSignature[] getUpperBounds() {
/* 47 */     return this.upperBounds;
/*    */   }
/*    */   
/*    */   public FieldTypeSignature[] getLowerBounds() {
/* 51 */     if ((this.lowerBounds.length == 1) && 
/* 52 */       (this.lowerBounds[0] == BottomSignature.make())) {
/* 53 */       return emptyBounds;
/*    */     }
/* 55 */     return this.lowerBounds;
/*    */   }
/*    */   
/* 58 */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitWildcard(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\Wildcard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
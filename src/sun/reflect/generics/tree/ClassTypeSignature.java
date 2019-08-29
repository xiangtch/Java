/*    */ package sun.reflect.generics.tree;
/*    */ 
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ public class ClassTypeSignature
/*    */   implements FieldTypeSignature
/*    */ {
/*    */   private final List<SimpleClassTypeSignature> path;
/*    */   
/*    */   private ClassTypeSignature(List<SimpleClassTypeSignature> paramList)
/*    */   {
/* 40 */     this.path = paramList;
/*    */   }
/*    */   
/*    */   public static ClassTypeSignature make(List<SimpleClassTypeSignature> paramList) {
/* 44 */     return new ClassTypeSignature(paramList);
/*    */   }
/*    */   
/* 47 */   public List<SimpleClassTypeSignature> getPath() { return this.path; }
/*    */   
/* 49 */   public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitClassTypeSignature(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\ClassTypeSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
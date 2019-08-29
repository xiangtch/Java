/*    */ package sun.reflect.generics.tree;
/*    */ 
/*    */ import sun.reflect.generics.visitor.Visitor;
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
/*    */ public class ClassSignature
/*    */   implements Signature
/*    */ {
/*    */   private final FormalTypeParameter[] formalTypeParams;
/*    */   private final ClassTypeSignature superclass;
/*    */   private final ClassTypeSignature[] superInterfaces;
/*    */   
/*    */   private ClassSignature(FormalTypeParameter[] paramArrayOfFormalTypeParameter, ClassTypeSignature paramClassTypeSignature, ClassTypeSignature[] paramArrayOfClassTypeSignature)
/*    */   {
/* 38 */     this.formalTypeParams = paramArrayOfFormalTypeParameter;
/* 39 */     this.superclass = paramClassTypeSignature;
/* 40 */     this.superInterfaces = paramArrayOfClassTypeSignature;
/*    */   }
/*    */   
/*    */ 
/*    */   public static ClassSignature make(FormalTypeParameter[] paramArrayOfFormalTypeParameter, ClassTypeSignature paramClassTypeSignature, ClassTypeSignature[] paramArrayOfClassTypeSignature)
/*    */   {
/* 46 */     return new ClassSignature(paramArrayOfFormalTypeParameter, paramClassTypeSignature, paramArrayOfClassTypeSignature);
/*    */   }
/*    */   
/*    */ 
/* 50 */   public FormalTypeParameter[] getFormalTypeParameters() { return this.formalTypeParams; }
/*    */   
/* 52 */   public ClassTypeSignature getSuperclass() { return this.superclass; }
/* 53 */   public ClassTypeSignature[] getSuperInterfaces() { return this.superInterfaces; }
/*    */   
/* 55 */   public void accept(Visitor<?> paramVisitor) { paramVisitor.visitClassSignature(this); }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\ClassSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.reflect.generics.repository;
/*    */ 
/*    */ import java.lang.reflect.TypeVariable;
/*    */ import sun.reflect.generics.factory.GenericsFactory;
/*    */ import sun.reflect.generics.tree.FormalTypeParameter;
/*    */ import sun.reflect.generics.tree.Signature;
/*    */ import sun.reflect.generics.visitor.Reifier;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class GenericDeclRepository<S extends Signature>
/*    */   extends AbstractRepository<S>
/*    */ {
/*    */   private volatile TypeVariable<?>[] typeParams;
/*    */   
/*    */   protected GenericDeclRepository(String paramString, GenericsFactory paramGenericsFactory)
/*    */   {
/* 49 */     super(paramString, paramGenericsFactory);
/*    */   }
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
/*    */   public TypeVariable<?>[] getTypeParameters()
/*    */   {
/* 68 */     TypeVariable[] arrayOfTypeVariable = this.typeParams;
/* 69 */     if (arrayOfTypeVariable == null)
/*    */     {
/* 71 */       FormalTypeParameter[] arrayOfFormalTypeParameter = ((Signature)getTree()).getFormalTypeParameters();
/*    */       
/* 73 */       arrayOfTypeVariable = new TypeVariable[arrayOfFormalTypeParameter.length];
/*    */       
/* 75 */       for (int i = 0; i < arrayOfFormalTypeParameter.length; i++) {
/* 76 */         Reifier localReifier = getReifier();
/* 77 */         arrayOfFormalTypeParameter[i].accept(localReifier);
/*    */         
/* 79 */         arrayOfTypeVariable[i] = ((TypeVariable)localReifier.getResult());
/*    */       }
/* 81 */       this.typeParams = arrayOfTypeVariable;
/*    */     }
/* 83 */     return (TypeVariable[])arrayOfTypeVariable.clone();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\repository\GenericDeclRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
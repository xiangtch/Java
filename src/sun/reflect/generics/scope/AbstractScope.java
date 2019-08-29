/*    */ package sun.reflect.generics.scope;
/*    */ 
/*    */ import java.lang.reflect.GenericDeclaration;
/*    */ import java.lang.reflect.TypeVariable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractScope<D extends GenericDeclaration>
/*    */   implements Scope
/*    */ {
/*    */   private final D recvr;
/*    */   private volatile Scope enclosingScope;
/*    */   
/*    */   protected AbstractScope(D paramD)
/*    */   {
/* 55 */     this.recvr = paramD;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected D getRecvr()
/*    */   {
/* 62 */     return this.recvr;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected abstract Scope computeEnclosingScope();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Scope getEnclosingScope()
/*    */   {
/* 76 */     Scope localScope = this.enclosingScope;
/* 77 */     if (localScope == null) {
/* 78 */       localScope = computeEnclosingScope();
/* 79 */       this.enclosingScope = localScope;
/*    */     }
/* 81 */     return localScope;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public TypeVariable<?> lookup(String paramString)
/*    */   {
/* 92 */     TypeVariable[] arrayOfTypeVariable1 = getRecvr().getTypeParameters();
/* 93 */     for (TypeVariable localTypeVariable : arrayOfTypeVariable1) {
/* 94 */       if (localTypeVariable.getName().equals(paramString)) return localTypeVariable;
/*    */     }
/* 96 */     return getEnclosingScope().lookup(paramString);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\scope\AbstractScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
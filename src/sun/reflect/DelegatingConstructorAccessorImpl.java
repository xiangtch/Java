/*    */ package sun.reflect;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
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
/*    */ class DelegatingConstructorAccessorImpl
/*    */   extends ConstructorAccessorImpl
/*    */ {
/*    */   private ConstructorAccessorImpl delegate;
/*    */   
/*    */   DelegatingConstructorAccessorImpl(ConstructorAccessorImpl paramConstructorAccessorImpl)
/*    */   {
/* 37 */     setDelegate(paramConstructorAccessorImpl);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Object newInstance(Object[] paramArrayOfObject)
/*    */     throws InstantiationException, IllegalArgumentException, InvocationTargetException
/*    */   {
/* 45 */     return this.delegate.newInstance(paramArrayOfObject);
/*    */   }
/*    */   
/*    */   void setDelegate(ConstructorAccessorImpl paramConstructorAccessorImpl) {
/* 49 */     this.delegate = paramConstructorAccessorImpl;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\DelegatingConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
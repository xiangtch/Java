/*    */ package sun.reflect;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ import sun.misc.Unsafe;
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
/*    */ abstract class UnsafeStaticFieldAccessorImpl
/*    */   extends UnsafeFieldAccessorImpl
/*    */ {
/*    */   protected final Object base;
/*    */   
/*    */   static
/*    */   {
/* 42 */     Reflection.registerFieldsToFilter(UnsafeStaticFieldAccessorImpl.class, new String[] { "base" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   UnsafeStaticFieldAccessorImpl(Field paramField)
/*    */   {
/* 49 */     super(paramField);
/* 50 */     this.base = unsafe.staticFieldBase(paramField);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\UnsafeStaticFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
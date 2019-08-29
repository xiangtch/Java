/*    */ package sun.reflect;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
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
/*    */ class ClassDefiner
/*    */ {
/* 37 */   static final Unsafe unsafe = ;
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
/*    */   static Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ClassLoader paramClassLoader)
/*    */   {
/* 57 */     ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*    */     {
/*    */       public ClassLoader run() {
/* 60 */         return new DelegatingClassLoader(this.val$parentClassLoader);
/*    */       }
/* 62 */     });
/* 63 */     return unsafe.defineClass(paramString, paramArrayOfByte, paramInt1, paramInt2, localClassLoader, null);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\ClassDefiner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
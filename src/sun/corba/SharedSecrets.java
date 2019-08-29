/*    */ package sun.corba;
/*    */ 
/*    */ import com.sun.corba.se.impl.io.ValueUtility;
/*    */ import java.lang.reflect.Method;
/*    */ import sun.misc.JavaOISAccess;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SharedSecrets
/*    */ {
/* 45 */   private static final Unsafe unsafe = ;
/*    */   private static JavaCorbaAccess javaCorbaAccess;
/*    */   private static final Method getJavaOISAccessMethod;
/*    */   private static JavaOISAccess javaOISAccess;
/*    */   
/*    */   static
/*    */   {
/*    */     try {
/* 53 */       Class localClass = Class.forName("sun.misc.SharedSecrets");
/*    */       
/* 55 */       getJavaOISAccessMethod = localClass.getMethod("getJavaOISAccess", new Class[0]);
/*    */     } catch (Exception localException) {
/* 57 */       throw new ExceptionInInitializerError(localException);
/*    */     }
/*    */   }
/*    */   
/*    */   public static JavaOISAccess getJavaOISAccess() {
/* 62 */     if (javaOISAccess == null) {
/*    */       try
/*    */       {
/* 65 */         javaOISAccess = (JavaOISAccess)getJavaOISAccessMethod.invoke(null, new Object[0]);
/*    */       } catch (Exception localException) {
/* 67 */         throw new ExceptionInInitializerError(localException);
/*    */       }
/*    */     }
/* 70 */     return javaOISAccess;
/*    */   }
/*    */   
/*    */   public static JavaCorbaAccess getJavaCorbaAccess() {
/* 74 */     if (javaCorbaAccess == null)
/*    */     {
/*    */ 
/* 77 */       unsafe.ensureClassInitialized(ValueUtility.class);
/*    */     }
/* 79 */     return javaCorbaAccess;
/*    */   }
/*    */   
/*    */   public static void setJavaCorbaAccess(JavaCorbaAccess paramJavaCorbaAccess) {
/* 83 */     javaCorbaAccess = paramJavaCorbaAccess;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\corba\SharedSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
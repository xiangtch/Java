/*    */ package sun.tracing;
/*    */ 
/*    */ import com.sun.tracing.Probe;
/*    */ import java.lang.reflect.Field;
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
/*    */ public abstract class ProbeSkeleton
/*    */   implements Probe
/*    */ {
/*    */   protected Class<?>[] parameters;
/*    */   
/*    */   protected ProbeSkeleton(Class<?>[] paramArrayOfClass)
/*    */   {
/* 42 */     this.parameters = paramArrayOfClass;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean isEnabled();
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract void uncheckedTrigger(Object[] paramArrayOfObject);
/*    */   
/*    */ 
/*    */ 
/*    */   private static boolean isAssignable(Object paramObject, Class<?> paramClass)
/*    */   {
/* 57 */     if ((paramObject != null) && 
/* 58 */       (!paramClass.isInstance(paramObject))) {
/* 59 */       if (paramClass.isPrimitive()) {
/*    */         try
/*    */         {
/* 62 */           Field localField = paramObject.getClass().getField("TYPE");
/* 63 */           return paramClass.isAssignableFrom((Class)localField.get(null));
/*    */         }
/*    */         catch (Exception localException) {}
/*    */       }
/*    */       
/* 68 */       return false;
/*    */     }
/*    */     
/* 71 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void trigger(Object... paramVarArgs)
/*    */   {
/* 78 */     if (paramVarArgs.length != this.parameters.length) {
/* 79 */       throw new IllegalArgumentException("Wrong number of arguments");
/*    */     }
/* 81 */     for (int i = 0; i < this.parameters.length; i++) {
/* 82 */       if (!isAssignable(paramVarArgs[i], this.parameters[i])) {
/* 83 */         throw new IllegalArgumentException("Wrong type of argument at position " + i);
/*    */       }
/*    */     }
/*    */     
/* 87 */     uncheckedTrigger(paramVarArgs);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\ProbeSkeleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
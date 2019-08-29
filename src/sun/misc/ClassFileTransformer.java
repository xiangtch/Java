/*    */ package sun.misc;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ @Deprecated
/*    */ public abstract class ClassFileTransformer
/*    */ {
/* 42 */   private static final List<ClassFileTransformer> transformers = new ArrayList();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void add(ClassFileTransformer paramClassFileTransformer)
/*    */   {
/* 51 */     synchronized (transformers) {
/* 52 */       transformers.add(paramClassFileTransformer);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static ClassFileTransformer[] getTransformers()
/*    */   {
/* 62 */     synchronized (transformers) {
/* 63 */       ClassFileTransformer[] arrayOfClassFileTransformer = new ClassFileTransformer[transformers.size()];
/* 64 */       return (ClassFileTransformer[])transformers.toArray(arrayOfClassFileTransformer);
/*    */     }
/*    */   }
/*    */   
/*    */   public abstract byte[] transform(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*    */     throws ClassFormatError;
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\ClassFileTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
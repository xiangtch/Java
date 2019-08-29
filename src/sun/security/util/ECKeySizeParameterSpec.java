/*    */ package sun.security.util;
/*    */ 
/*    */ import java.security.spec.AlgorithmParameterSpec;
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
/*    */ 
/*    */ 
/*    */ public class ECKeySizeParameterSpec
/*    */   implements AlgorithmParameterSpec
/*    */ {
/*    */   private int keySize;
/*    */   
/*    */   public ECKeySizeParameterSpec(int paramInt)
/*    */   {
/* 55 */     this.keySize = paramInt;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getKeySize()
/*    */   {
/* 64 */     return this.keySize;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\ECKeySizeParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.text;
/*    */ 
/*    */ import sun.text.normalizer.NormalizerImpl;
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
/*    */ public final class ComposedCharIter
/*    */ {
/*    */   public static final int DONE = -1;
/*    */   private static int[] chars;
/*    */   private static String[] decomps;
/*    */   
/*    */   static
/*    */   {
/* 46 */     int i = 2000;
/* 47 */     chars = new int[i];
/* 48 */     decomps = new String[i]; }
/* 49 */   private static int decompNum = NormalizerImpl.getDecompose(chars, decomps);
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
/*    */   public int next()
/*    */   {
/* 67 */     if (this.curChar == decompNum - 1) {
/* 68 */       return -1;
/*    */     }
/* 70 */     return chars[(++this.curChar)];
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 80 */   public String decomposition() { return decomps[this.curChar]; }
/*    */   
/* 82 */   private int curChar = -1;
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\text\ComposedCharIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
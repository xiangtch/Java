/*    */ package sun.text;
/*    */ 
/*    */ import sun.text.normalizer.NormalizerBase;
/*    */ import sun.text.normalizer.NormalizerBase.Mode;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CollatorUtilities
/*    */ {
/*    */   public static int toLegacyMode(Mode paramMode)
/*    */   {
/* 35 */     int i = legacyModeMap.length;
/* 36 */     while (i > 0) {
/* 37 */       i--;
/* 38 */       if (legacyModeMap[i] == paramMode) {
/*    */         break;
/*    */       }
/*    */     }
/* 42 */     return i;
/*    */   }
/*    */   
/*    */   public static Mode toNormalizerMode(int paramInt)
/*    */   {
/*    */     Mode localMode;
/*    */     try {
/* 49 */       localMode = legacyModeMap[paramInt];
/*    */     }
/*    */     catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 52 */       localMode = NormalizerBase.NONE;
/*    */     }
/* 54 */     return localMode;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/* 59 */   static Mode[] legacyModeMap = { NormalizerBase.NONE, NormalizerBase.NFD, NormalizerBase.NFKD };
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\text\CollatorUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
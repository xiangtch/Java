/*    */ package sun.nio.ch;
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
/*    */ class OptionKey
/*    */ {
/*    */   private int level;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private int name;
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
/*    */   OptionKey(int paramInt1, int paramInt2)
/*    */   {
/* 37 */     this.level = paramInt1;
/* 38 */     this.name = paramInt2;
/*    */   }
/*    */   
/*    */   int level() {
/* 42 */     return this.level;
/*    */   }
/*    */   
/*    */   int name() {
/* 46 */     return this.name;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\OptionKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
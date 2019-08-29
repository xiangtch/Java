/*    */ package sun.awt;
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
/*    */ public class CharsetString
/*    */ {
/*    */   public char[] charsetChars;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int offset;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int length;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public FontDescriptor fontDescriptor;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CharsetString(char[] paramArrayOfChar, int paramInt1, int paramInt2, FontDescriptor paramFontDescriptor)
/*    */   {
/* 54 */     this.charsetChars = paramArrayOfChar;
/* 55 */     this.offset = paramInt1;
/* 56 */     this.length = paramInt2;
/* 57 */     this.fontDescriptor = paramFontDescriptor;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\CharsetString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.text;
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
/*    */ public final class SupplementaryCharacterData
/*    */   implements Cloneable
/*    */ {
/*    */   private static final byte IGNORE = -1;
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
/*    */   private int[] dataTable;
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
/*    */   public SupplementaryCharacterData(int[] paramArrayOfInt)
/*    */   {
/* 59 */     this.dataTable = paramArrayOfInt;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getValue(int paramInt)
/*    */   {
/* 67 */     assert ((paramInt >= 65536) && (paramInt <= 1114111)) : 
/*    */     
/* 69 */       ("Invalid code point:" + Integer.toHexString(paramInt));
/*    */     
/* 71 */     int i = 0;
/* 72 */     int j = this.dataTable.length - 1;
/*    */     
/*    */     for (;;)
/*    */     {
/* 76 */       int k = (i + j) / 2;
/*    */       
/* 78 */       int m = this.dataTable[k] >> 8;
/* 79 */       int n = this.dataTable[(k + 1)] >> 8;
/*    */       
/* 81 */       if (paramInt < m) {
/* 82 */         j = k;
/* 83 */       } else if (paramInt > n - 1) {
/* 84 */         i = k;
/*    */       } else {
/* 86 */         int i1 = this.dataTable[k] & 0xFF;
/* 87 */         return i1 == 255 ? -1 : i1;
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public int[] getArray()
/*    */   {
/* 96 */     return this.dataTable;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\text\SupplementaryCharacterData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
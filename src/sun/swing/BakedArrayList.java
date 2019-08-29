/*    */ package sun.swing;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BakedArrayList
/*    */   extends ArrayList
/*    */ {
/*    */   private int _hashCode;
/*    */   
/*    */   public BakedArrayList(int paramInt)
/*    */   {
/* 53 */     super(paramInt);
/*    */   }
/*    */   
/*    */   public BakedArrayList(List paramList) {
/* 57 */     this(paramList.size());
/* 58 */     int i = 0; for (int j = paramList.size(); i < j; i++) {
/* 59 */       add(paramList.get(i));
/*    */     }
/* 61 */     cacheHashCode();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void cacheHashCode()
/*    */   {
/* 69 */     this._hashCode = 1;
/* 70 */     for (int i = size() - 1; i >= 0; i--) {
/* 71 */       this._hashCode = (31 * this._hashCode + get(i).hashCode());
/*    */     }
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 76 */     return this._hashCode;
/*    */   }
/*    */   
/*    */   public boolean equals(Object paramObject) {
/* 80 */     BakedArrayList localBakedArrayList = (BakedArrayList)paramObject;
/* 81 */     int i = size();
/*    */     
/* 83 */     if (localBakedArrayList.size() != i) {
/* 84 */       return false;
/*    */     }
/* 86 */     while (i-- > 0) {
/* 87 */       if (!get(i).equals(localBakedArrayList.get(i))) {
/* 88 */         return false;
/*    */       }
/*    */     }
/* 91 */     return true;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\swing\BakedArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
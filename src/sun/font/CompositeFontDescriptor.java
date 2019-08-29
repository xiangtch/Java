/*    */ package sun.font;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CompositeFontDescriptor
/*    */ {
/*    */   private String faceName;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private int coreComponentCount;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private String[] componentFaceNames;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private String[] componentFileNames;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private int[] exclusionRanges;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private int[] exclusionRangeLimits;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CompositeFontDescriptor(String paramString, int paramInt, String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
/*    */   {
/* 61 */     this.faceName = paramString;
/* 62 */     this.coreComponentCount = paramInt;
/* 63 */     this.componentFaceNames = paramArrayOfString1;
/* 64 */     this.componentFileNames = paramArrayOfString2;
/* 65 */     this.exclusionRanges = paramArrayOfInt1;
/* 66 */     this.exclusionRangeLimits = paramArrayOfInt2;
/*    */   }
/*    */   
/*    */   public String getFaceName() {
/* 70 */     return this.faceName;
/*    */   }
/*    */   
/*    */   public int getCoreComponentCount() {
/* 74 */     return this.coreComponentCount;
/*    */   }
/*    */   
/*    */   public String[] getComponentFaceNames() {
/* 78 */     return this.componentFaceNames;
/*    */   }
/*    */   
/*    */   public String[] getComponentFileNames() {
/* 82 */     return this.componentFileNames;
/*    */   }
/*    */   
/*    */   public int[] getExclusionRanges() {
/* 86 */     return this.exclusionRanges;
/*    */   }
/*    */   
/*    */   public int[] getExclusionRangeLimits() {
/* 90 */     return this.exclusionRangeLimits;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\font\CompositeFontDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
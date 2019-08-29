/*    */ package sun.dc.pr;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PRException
/*    */   extends Exception
/*    */ {
/*    */   public static final String BAD_COORD_setOutputArea = "setOutputArea: alpha coordinate out of bounds";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final String ALPHA_ARRAY_SHORT = "writeAlpha: alpha destination array too short";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final String DUMMY = "";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PRException() {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PRException(String paramString)
/*    */   {
/* 34 */     super(paramString);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\dc\pr\PRException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
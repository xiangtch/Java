/*    */ package sun.security.action;
/*    */ 
/*    */ import java.security.PrivilegedAction;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GetBooleanAction
/*    */   implements PrivilegedAction<Boolean>
/*    */ {
/*    */   private String theProp;
/*    */   
/*    */   public GetBooleanAction(String paramString)
/*    */   {
/* 60 */     this.theProp = paramString;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Boolean run()
/*    */   {
/* 70 */     return Boolean.valueOf(Boolean.getBoolean(this.theProp));
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\action\GetBooleanAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
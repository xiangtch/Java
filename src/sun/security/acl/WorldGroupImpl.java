/*    */ package sun.security.acl;
/*    */ 
/*    */ import java.security.Principal;
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
/*    */ public class WorldGroupImpl
/*    */   extends GroupImpl
/*    */ {
/*    */   public WorldGroupImpl(String paramString)
/*    */   {
/* 37 */     super(paramString);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean isMember(Principal paramPrincipal)
/*    */   {
/* 46 */     return true;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\acl\WorldGroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
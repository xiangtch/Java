/*    */ package sun.security.acl;
/*    */ 
/*    */ import java.security.acl.Permission;
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
/*    */ public class AllPermissionsImpl
/*    */   extends PermissionImpl
/*    */ {
/*    */   public AllPermissionsImpl(String paramString)
/*    */   {
/* 38 */     super(paramString);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean equals(Permission paramPermission)
/*    */   {
/* 48 */     return true;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\acl\AllPermissionsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PermissionImpl
/*    */   implements Permission
/*    */ {
/*    */   private String permission;
/*    */   
/*    */   public PermissionImpl(String paramString)
/*    */   {
/* 45 */     this.permission = paramString;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean equals(Object paramObject)
/*    */   {
/* 55 */     if ((paramObject instanceof Permission)) {
/* 56 */       Permission localPermission = (Permission)paramObject;
/* 57 */       return this.permission.equals(localPermission.toString());
/*    */     }
/* 59 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String toString()
/*    */   {
/* 68 */     return this.permission;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 77 */     return toString().hashCode();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\acl\PermissionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
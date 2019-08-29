/*     */ package sun.security.acl;
/*     */ 
/*     */ import java.security.Principal;
/*     */ import java.security.acl.AclEntry;
/*     */ import java.security.acl.Group;
/*     */ import java.security.acl.Permission;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AclEntryImpl
/*     */   implements AclEntry
/*     */ {
/*  38 */   private Principal user = null;
/*  39 */   private Vector<Permission> permissionSet = new Vector(10, 10);
/*  40 */   private boolean negative = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AclEntryImpl(Principal paramPrincipal)
/*     */   {
/*  48 */     this.user = paramPrincipal;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AclEntryImpl() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean setPrincipal(Principal paramPrincipal)
/*     */   {
/*  66 */     if (this.user != null)
/*  67 */       return false;
/*  68 */     this.user = paramPrincipal;
/*  69 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNegativePermissions()
/*     */   {
/*  78 */     this.negative = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isNegative()
/*     */   {
/*  85 */     return this.negative;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean addPermission(Permission paramPermission)
/*     */   {
/*  98 */     if (this.permissionSet.contains(paramPermission)) {
/*  99 */       return false;
/*     */     }
/* 101 */     this.permissionSet.addElement(paramPermission);
/*     */     
/* 103 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean removePermission(Permission paramPermission)
/*     */   {
/* 115 */     return this.permissionSet.removeElement(paramPermission);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean checkPermission(Permission paramPermission)
/*     */   {
/* 127 */     return this.permissionSet.contains(paramPermission);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Enumeration<Permission> permissions()
/*     */   {
/* 134 */     return this.permissionSet.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 141 */     StringBuffer localStringBuffer = new StringBuffer();
/* 142 */     if (this.negative) {
/* 143 */       localStringBuffer.append("-");
/*     */     } else
/* 145 */       localStringBuffer.append("+");
/* 146 */     if ((this.user instanceof Group)) {
/* 147 */       localStringBuffer.append("Group.");
/*     */     } else
/* 149 */       localStringBuffer.append("User.");
/* 150 */     localStringBuffer.append(this.user + "=");
/* 151 */     Enumeration localEnumeration = permissions();
/* 152 */     while (localEnumeration.hasMoreElements()) {
/* 153 */       Permission localPermission = (Permission)localEnumeration.nextElement();
/* 154 */       localStringBuffer.append(localPermission);
/* 155 */       if (localEnumeration.hasMoreElements())
/* 156 */         localStringBuffer.append(",");
/*     */     }
/* 158 */     return new String(localStringBuffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Object clone()
/*     */   {
/* 167 */     AclEntryImpl localAclEntryImpl = new AclEntryImpl(this.user);
/* 168 */     localAclEntryImpl.permissionSet = ((Vector)this.permissionSet.clone());
/* 169 */     localAclEntryImpl.negative = this.negative;
/* 170 */     return localAclEntryImpl;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Principal getPrincipal()
/*     */   {
/* 179 */     return this.user;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\acl\AclEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
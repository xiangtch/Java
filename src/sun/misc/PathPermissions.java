/*     */ package sun.misc;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FilePermission;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.Permission;
/*     */ import java.security.PermissionCollection;
/*     */ import java.security.Permissions;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.PropertyPermission;
/*     */ import sun.security.util.SecurityConstants;
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
/*     */ class PathPermissions
/*     */   extends PermissionCollection
/*     */ {
/*     */   private static final long serialVersionUID = 8133287259134945693L;
/*     */   private File[] path;
/*     */   private Permissions perms;
/*     */   URL codeBase;
/*     */   
/*     */   PathPermissions(File[] paramArrayOfFile)
/*     */   {
/* 539 */     this.path = paramArrayOfFile;
/* 540 */     this.perms = null;
/* 541 */     this.codeBase = null;
/*     */   }
/*     */   
/*     */   URL getCodeBase()
/*     */   {
/* 546 */     return this.codeBase;
/*     */   }
/*     */   
/*     */   public void add(Permission paramPermission) {
/* 550 */     throw new SecurityException("attempt to add a permission");
/*     */   }
/*     */   
/*     */   private synchronized void init()
/*     */   {
/* 555 */     if (this.perms != null) {
/* 556 */       return;
/*     */     }
/* 558 */     this.perms = new Permissions();
/*     */     
/*     */ 
/* 561 */     this.perms.add(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
/*     */     
/*     */ 
/* 564 */     this.perms.add(new PropertyPermission("java.*", "read"));
/*     */     
/*     */ 
/* 567 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/* 569 */         for (int i = 0; i < PathPermissions.this.path.length; i++) {
/* 570 */           File localFile = PathPermissions.this.path[i];
/*     */           String str;
/*     */           try {
/* 573 */             str = localFile.getCanonicalPath();
/*     */           } catch (IOException localIOException) {
/* 575 */             str = localFile.getAbsolutePath();
/*     */           }
/* 577 */           if (i == 0) {
/* 578 */             PathPermissions.this.codeBase = Launcher.getFileURL(new File(str));
/*     */           }
/* 580 */           if (localFile.isDirectory()) {
/* 581 */             if (str.endsWith(File.separator)) {
/* 582 */               PathPermissions.this.perms.add(new FilePermission(str + "-", "read"));
/*     */             }
/*     */             else {
/* 585 */               PathPermissions.this.perms.add(new FilePermission(str + File.separator + "-", "read"));
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 590 */             int j = str.lastIndexOf(File.separatorChar);
/* 591 */             if (j != -1) {
/* 592 */               str = str.substring(0, j + 1) + "-";
/* 593 */               PathPermissions.this.perms.add(new FilePermission(str, "read"));
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 600 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean implies(Permission paramPermission) {
/* 606 */     if (this.perms == null)
/* 607 */       init();
/* 608 */     return this.perms.implies(paramPermission);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public java.util.Enumeration<Permission> elements()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 87	sun/misc/PathPermissions:perms	Ljava/security/Permissions;
/*     */     //   4: ifnonnull +7 -> 11
/*     */     //   7: aload_0
/*     */     //   8: invokespecial 98	sun/misc/PathPermissions:init	()V
/*     */     //   11: aload_0
/*     */     //   12: getfield 87	sun/misc/PathPermissions:perms	Ljava/security/Permissions;
/*     */     //   15: dup
/*     */     //   16: astore_1
/*     */     //   17: monitorenter
/*     */     //   18: aload_0
/*     */     //   19: getfield 87	sun/misc/PathPermissions:perms	Ljava/security/Permissions;
/*     */     //   22: invokevirtual 96	java/security/Permissions:elements	()Ljava/util/Enumeration;
/*     */     //   25: aload_1
/*     */     //   26: monitorexit
/*     */     //   27: areturn
/*     */     //   28: astore_2
/*     */     //   29: aload_1
/*     */     //   30: monitorexit
/*     */     //   31: aload_2
/*     */     //   32: athrow
/*     */     // Line number table:
/*     */     //   Java source line #612	-> byte code offset #0
/*     */     //   Java source line #613	-> byte code offset #7
/*     */     //   Java source line #614	-> byte code offset #11
/*     */     //   Java source line #615	-> byte code offset #18
/*     */     //   Java source line #616	-> byte code offset #28
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	33	0	this	PathPermissions
/*     */     //   16	14	1	Ljava/lang/Object;	Object
/*     */     //   28	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   18	27	28	finally
/*     */     //   28	31	28	finally
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 620 */     if (this.perms == null)
/* 621 */       init();
/* 622 */     return this.perms.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\PathPermissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
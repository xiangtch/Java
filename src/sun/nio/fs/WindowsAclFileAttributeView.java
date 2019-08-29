/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.ProviderMismatchException;
/*     */ import java.nio.file.attribute.AclEntry;
/*     */ import java.nio.file.attribute.UserPrincipal;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WindowsAclFileAttributeView
/*     */   extends AbstractAclFileAttributeView
/*     */ {
/*     */   private static final short SIZEOF_SECURITY_DESCRIPTOR = 20;
/*     */   private final WindowsPath file;
/*     */   private final boolean followLinks;
/*     */   
/*     */   WindowsAclFileAttributeView(WindowsPath paramWindowsPath, boolean paramBoolean)
/*     */   {
/*  60 */     this.file = paramWindowsPath;
/*  61 */     this.followLinks = paramBoolean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkAccess(WindowsPath paramWindowsPath, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/*  69 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  70 */     if (localSecurityManager != null) {
/*  71 */       if (paramBoolean1)
/*  72 */         localSecurityManager.checkRead(paramWindowsPath.getPathForPermissionCheck());
/*  73 */       if (paramBoolean2)
/*  74 */         localSecurityManager.checkWrite(paramWindowsPath.getPathForPermissionCheck());
/*  75 */       localSecurityManager.checkPermission(new RuntimePermission("accessUserInformation"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static NativeBuffer getFileSecurity(String paramString, int paramInt)
/*     */     throws IOException
/*     */   {
/*  84 */     int i = 0;
/*     */     try {
/*  86 */       i = WindowsNativeDispatcher.GetFileSecurity(paramString, paramInt, 0L, 0);
/*     */     } catch (WindowsException localWindowsException1) {
/*  88 */       localWindowsException1.rethrowAsIOException(paramString);
/*     */     }
/*  90 */     assert (i > 0);
/*     */     
/*     */ 
/*  93 */     NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(i);
/*     */     try {
/*     */       for (;;) {
/*  96 */         int j = WindowsNativeDispatcher.GetFileSecurity(paramString, paramInt, localNativeBuffer.address(), i);
/*  97 */         if (j <= i) {
/*  98 */           return localNativeBuffer;
/*     */         }
/*     */         
/* 101 */         localNativeBuffer.release();
/* 102 */         localNativeBuffer = NativeBuffers.getNativeBuffer(j);
/* 103 */         i = j;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 108 */       return null;
/*     */     }
/*     */     catch (WindowsException localWindowsException2)
/*     */     {
/* 106 */       localNativeBuffer.release();
/* 107 */       localWindowsException2.rethrowAsIOException(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UserPrincipal getOwner()
/*     */     throws IOException
/*     */   {
/* 116 */     checkAccess(this.file, true, false);
/*     */     
/*     */ 
/*     */ 
/* 120 */     String str = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
/* 121 */     NativeBuffer localNativeBuffer = getFileSecurity(str, 1);
/*     */     try
/*     */     {
/* 124 */       long l = WindowsNativeDispatcher.GetSecurityDescriptorOwner(localNativeBuffer.address());
/* 125 */       if (l == 0L)
/* 126 */         throw new IOException("no owner");
/* 127 */       return WindowsUserPrincipals.fromSid(l);
/*     */     } catch (WindowsException localWindowsException) {
/* 129 */       localWindowsException.rethrowAsIOException(this.file);
/* 130 */       return null;
/*     */     } finally {
/* 132 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public List<AclEntry> getAcl()
/*     */     throws IOException
/*     */   {
/* 140 */     checkAccess(this.file, true, false);
/*     */     
/*     */ 
/*     */ 
/* 144 */     String str = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
/*     */     
/*     */ 
/*     */ 
/* 148 */     NativeBuffer localNativeBuffer = getFileSecurity(str, 4);
/*     */     try {
/* 150 */       return WindowsSecurityDescriptor.getAcl(localNativeBuffer.address());
/*     */     } finally {
/* 152 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setOwner(UserPrincipal paramUserPrincipal)
/*     */     throws IOException
/*     */   {
/* 160 */     if (paramUserPrincipal == null)
/* 161 */       throw new NullPointerException("'owner' is null");
/* 162 */     if (!(paramUserPrincipal instanceof WindowsUserPrincipals.User))
/* 163 */       throw new ProviderMismatchException();
/* 164 */     WindowsUserPrincipals.User localUser = (WindowsUserPrincipals.User)paramUserPrincipal;
/*     */     
/*     */ 
/* 167 */     checkAccess(this.file, false, true);
/*     */     
/*     */ 
/*     */ 
/* 171 */     String str = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
/*     */     
/*     */ 
/*     */ 
/* 175 */     long l = 0L;
/*     */     try {
/* 177 */       l = WindowsNativeDispatcher.ConvertStringSidToSid(localUser.sidString());
/*     */     }
/*     */     catch (WindowsException localWindowsException1) {
/* 180 */       throw new IOException("Failed to get SID for " + localUser.getName() + ": " + localWindowsException1.errorString());
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 186 */       NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(20);
/*     */       try {
/* 188 */         WindowsNativeDispatcher.InitializeSecurityDescriptor(localNativeBuffer.address());
/* 189 */         WindowsNativeDispatcher.SetSecurityDescriptorOwner(localNativeBuffer.address(), l);
/*     */         
/*     */ 
/* 192 */         WindowsSecurity.Privilege localPrivilege = WindowsSecurity.enablePrivilege("SeRestorePrivilege");
/*     */         try {
/* 194 */           WindowsNativeDispatcher.SetFileSecurity(str, 1, localNativeBuffer
/*     */           
/* 196 */             .address());
/*     */         } finally {
/* 198 */           localPrivilege.drop();
/*     */         }
/*     */       } catch (WindowsException localWindowsException2) {
/* 201 */         localWindowsException2.rethrowAsIOException(this.file);
/*     */       } finally {
/* 203 */         localNativeBuffer.release();
/*     */       }
/*     */     } finally {
/* 206 */       WindowsNativeDispatcher.LocalFree(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAcl(List<AclEntry> paramList) throws IOException
/*     */   {
/* 212 */     checkAccess(this.file, false, true);
/*     */     
/*     */ 
/*     */ 
/* 216 */     String str = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
/* 217 */     WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.create(paramList);
/*     */     try {
/* 219 */       WindowsNativeDispatcher.SetFileSecurity(str, 4, localWindowsSecurityDescriptor.address());
/*     */     } catch (WindowsException localWindowsException) {
/* 221 */       localWindowsException.rethrowAsIOException(this.file);
/*     */     } finally {
/* 223 */       localWindowsSecurityDescriptor.release();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\WindowsAclFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
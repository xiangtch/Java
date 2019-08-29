/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.attribute.GroupPrincipal;
/*     */ import java.nio.file.attribute.UserPrincipal;
/*     */ import java.nio.file.attribute.UserPrincipalNotFoundException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WindowsUserPrincipals
/*     */ {
/*     */   static class User
/*     */     implements UserPrincipal
/*     */   {
/*     */     private final String sidString;
/*     */     private final int sidType;
/*     */     private final String accountName;
/*     */     
/*     */     User(String paramString1, int paramInt, String paramString2)
/*     */     {
/*  47 */       this.sidString = paramString1;
/*  48 */       this.sidType = paramInt;
/*  49 */       this.accountName = paramString2;
/*     */     }
/*     */     
/*     */     String sidString()
/*     */     {
/*  54 */       return this.sidString;
/*     */     }
/*     */     
/*     */     public String getName()
/*     */     {
/*  59 */       return this.accountName;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/*     */       String str;
/*  65 */       switch (this.sidType) {
/*  66 */       case 1:  str = "User"; break;
/*  67 */       case 2:  str = "Group"; break;
/*  68 */       case 3:  str = "Domain"; break;
/*  69 */       case 4:  str = "Alias"; break;
/*  70 */       case 5:  str = "Well-known group"; break;
/*  71 */       case 6:  str = "Deleted"; break;
/*  72 */       case 7:  str = "Invalid"; break;
/*  73 */       case 9:  str = "Computer"; break;
/*  74 */       case 8: default:  str = "Unknown";
/*     */       }
/*  76 */       return this.accountName + " (" + str + ")";
/*     */     }
/*     */     
/*     */     public boolean equals(Object paramObject)
/*     */     {
/*  81 */       if (paramObject == this)
/*  82 */         return true;
/*  83 */       if (!(paramObject instanceof User))
/*  84 */         return false;
/*  85 */       User localUser = (User)paramObject;
/*  86 */       return this.sidString.equals(localUser.sidString);
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/*  91 */       return this.sidString.hashCode();
/*     */     }
/*     */   }
/*     */   
/*     */   static class Group extends User implements GroupPrincipal {
/*     */     Group(String paramString1, int paramInt, String paramString2) {
/*  97 */       super(paramInt, paramString2);
/*     */     }
/*     */   }
/*     */   
/*     */   static UserPrincipal fromSid(long paramLong) throws IOException {
/*     */     String str1;
/*     */     try {
/* 104 */       str1 = WindowsNativeDispatcher.ConvertSidToStringSid(paramLong);
/* 105 */       if (str1 == null)
/*     */       {
/* 107 */         throw new AssertionError();
/*     */       }
/*     */     }
/*     */     catch (WindowsException localWindowsException1) {
/* 111 */       throw new IOException("Unable to convert SID to String: " + localWindowsException1.errorString());
/*     */     }
/*     */     
/*     */ 
/* 115 */     WindowsNativeDispatcher.Account localAccount = null;
/*     */     String str2;
/*     */     try {
/* 118 */       localAccount = WindowsNativeDispatcher.LookupAccountSid(paramLong);
/* 119 */       str2 = localAccount.domain() + "\\" + localAccount.name();
/*     */     } catch (WindowsException localWindowsException2) {
/* 121 */       str2 = str1;
/*     */     }
/*     */     
/* 124 */     int i = localAccount == null ? 8 : localAccount.use();
/* 125 */     if ((i == 2) || (i == 5) || (i == 4))
/*     */     {
/*     */ 
/*     */ 
/* 129 */       return new Group(str1, i, str2);
/*     */     }
/* 131 */     return new User(str1, i, str2);
/*     */   }
/*     */   
/*     */   static UserPrincipal lookup(String paramString) throws IOException
/*     */   {
/* 136 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 137 */     if (localSecurityManager != null) {
/* 138 */       localSecurityManager.checkPermission(new RuntimePermission("lookupUserInformation"));
/*     */     }
/*     */     
/*     */ 
/* 142 */     int i = 0;
/*     */     try {
/* 144 */       i = WindowsNativeDispatcher.LookupAccountName(paramString, 0L, 0);
/*     */     } catch (WindowsException localWindowsException1) {
/* 146 */       if (localWindowsException1.lastError() == 1332)
/* 147 */         throw new UserPrincipalNotFoundException(paramString);
/* 148 */       throw new IOException(paramString + ": " + localWindowsException1.errorString());
/*     */     }
/* 150 */     assert (i > 0);
/*     */     
/*     */ 
/* 153 */     NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(i);
/*     */     try {
/* 155 */       int j = WindowsNativeDispatcher.LookupAccountName(paramString, localNativeBuffer.address(), i);
/* 156 */       if (j != i)
/*     */       {
/* 158 */         throw new AssertionError("SID change during lookup");
/*     */       }
/*     */       
/*     */ 
/* 162 */       return fromSid(localNativeBuffer.address());
/*     */     } catch (WindowsException localWindowsException2) {
/* 164 */       throw new IOException(paramString + ": " + localWindowsException2.errorString());
/*     */     } finally {
/* 166 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\WindowsUserPrincipals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
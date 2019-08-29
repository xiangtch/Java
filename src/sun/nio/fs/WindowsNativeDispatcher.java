/*      */ package sun.nio.fs;
/*      */ 
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import sun.misc.Unsafe;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class WindowsNativeDispatcher
/*      */ {
/*      */   static native long CreateEvent(boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws WindowsException;
/*      */   
/*      */   static long CreateFile(String paramString, int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4)
/*      */     throws WindowsException
/*      */   {
/*   69 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*   71 */       return CreateFile0(localNativeBuffer.address(), paramInt1, paramInt2, paramLong, paramInt3, paramInt4);
/*      */ 
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*   78 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static long CreateFile(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws WindowsException
/*      */   {
/*   88 */     return CreateFile(paramString, paramInt1, paramInt2, 0L, paramInt3, paramInt4);
/*      */   }
/*      */   
/*      */   private static native long CreateFile0(long paramLong1, int paramInt1, int paramInt2, long paramLong2, int paramInt3, int paramInt4)
/*      */     throws WindowsException;
/*      */   
/*      */   static native void CloseHandle(long paramLong);
/*      */   
/*      */   /* Error */
/*      */   static void DeleteFile(String paramString)
/*      */     throws WindowsException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokestatic 324	sun/nio/fs/WindowsNativeDispatcher:asNativeBuffer	(Ljava/lang/String;)Lsun/nio/fs/NativeBuffer;
/*      */     //   4: astore_1
/*      */     //   5: aload_1
/*      */     //   6: invokevirtual 288	sun/nio/fs/NativeBuffer:address	()J
/*      */     //   9: invokestatic 299	sun/nio/fs/WindowsNativeDispatcher:DeleteFile0	(J)V
/*      */     //   12: aload_1
/*      */     //   13: invokevirtual 289	sun/nio/fs/NativeBuffer:release	()V
/*      */     //   16: goto +10 -> 26
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: invokevirtual 289	sun/nio/fs/NativeBuffer:release	()V
/*      */     //   24: aload_2
/*      */     //   25: athrow
/*      */     //   26: return
/*      */     // Line number table:
/*      */     //   Java source line #112	-> byte code offset #0
/*      */     //   Java source line #114	-> byte code offset #5
/*      */     //   Java source line #116	-> byte code offset #12
/*      */     //   Java source line #117	-> byte code offset #16
/*      */     //   Java source line #116	-> byte code offset #19
/*      */     //   Java source line #117	-> byte code offset #24
/*      */     //   Java source line #118	-> byte code offset #26
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	27	0	paramString	String
/*      */     //   4	17	1	localNativeBuffer	NativeBuffer
/*      */     //   19	6	2	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   5	12	19	finally
/*      */   }
/*      */   
/*      */   private static native void DeleteFile0(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */   static void CreateDirectory(String paramString, long paramLong)
/*      */     throws WindowsException
/*      */   {
/*  129 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  131 */       CreateDirectory0(localNativeBuffer.address(), paramLong);
/*      */     } finally {
/*  133 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */   private static native void CreateDirectory0(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */   /* Error */
/*      */   static void RemoveDirectory(String paramString)
/*      */     throws WindowsException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokestatic 324	sun/nio/fs/WindowsNativeDispatcher:asNativeBuffer	(Ljava/lang/String;)Lsun/nio/fs/NativeBuffer;
/*      */     //   4: astore_1
/*      */     //   5: aload_1
/*      */     //   6: invokevirtual 288	sun/nio/fs/NativeBuffer:address	()J
/*      */     //   9: invokestatic 300	sun/nio/fs/WindowsNativeDispatcher:RemoveDirectory0	(J)V
/*      */     //   12: aload_1
/*      */     //   13: invokevirtual 289	sun/nio/fs/NativeBuffer:release	()V
/*      */     //   16: goto +10 -> 26
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: invokevirtual 289	sun/nio/fs/NativeBuffer:release	()V
/*      */     //   24: aload_2
/*      */     //   25: athrow
/*      */     //   26: return
/*      */     // Line number table:
/*      */     //   Java source line #145	-> byte code offset #0
/*      */     //   Java source line #147	-> byte code offset #5
/*      */     //   Java source line #149	-> byte code offset #12
/*      */     //   Java source line #150	-> byte code offset #16
/*      */     //   Java source line #149	-> byte code offset #19
/*      */     //   Java source line #150	-> byte code offset #24
/*      */     //   Java source line #151	-> byte code offset #26
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	27	0	paramString	String
/*      */     //   4	17	1	localNativeBuffer	NativeBuffer
/*      */     //   19	6	2	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   5	12	19	finally
/*      */   }
/*      */   
/*      */   private static native void RemoveDirectory0(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */   static native void DeviceIoControlSetSparse(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */   static native void DeviceIoControlGetReparsePoint(long paramLong1, long paramLong2, int paramInt)
/*      */     throws WindowsException;
/*      */   
/*      */   static FirstFile FindFirstFile(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  182 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  184 */       FirstFile localFirstFile1 = new FirstFile(null);
/*  185 */       FindFirstFile0(localNativeBuffer.address(), localFirstFile1);
/*  186 */       return localFirstFile1;
/*      */     } finally {
/*  188 */       localNativeBuffer.release();
/*      */     } }
/*      */   
/*      */   private static native void FindFirstFile0(long paramLong, FirstFile paramFirstFile) throws WindowsException;
/*      */   
/*      */   static class FirstFile { private long handle;
/*      */     private String name;
/*      */     private int attributes;
/*      */     
/*  197 */     public long handle() { return this.handle; }
/*  198 */     public String name() { return this.name; }
/*  199 */     public int attributes() { return this.attributes; }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static long FindFirstFile(String paramString, long paramLong)
/*      */     throws WindowsException
/*      */   {
/*  211 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  213 */       return FindFirstFile1(localNativeBuffer.address(), paramLong);
/*      */     } finally {
/*  215 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native long FindFirstFile1(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native String FindNextFile(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static FirstStream FindFirstStream(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  241 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  243 */       FirstStream localFirstStream1 = new FirstStream(null);
/*  244 */       FindFirstStream0(localNativeBuffer.address(), localFirstStream1);
/*  245 */       FirstStream localFirstStream2; if (localFirstStream1.handle() == -1L)
/*  246 */         return null;
/*  247 */       return localFirstStream1;
/*      */     } finally {
/*  249 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */   private static native void FindFirstStream0(long paramLong, FirstStream paramFirstStream) throws WindowsException;
/*      */   
/*      */   static native String FindNextStream(long paramLong) throws WindowsException;
/*      */   
/*  257 */   static class FirstStream { public long handle() { return this.handle; }
/*  258 */     public String name() { return this.name; }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private long handle;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private String name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void FindClose(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void GetFileInformationByHandle(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static void CopyFileEx(String paramString1, String paramString2, int paramInt, long paramLong)
/*      */     throws WindowsException
/*      */   {
/*  301 */     NativeBuffer localNativeBuffer1 = asNativeBuffer(paramString1);
/*  302 */     NativeBuffer localNativeBuffer2 = asNativeBuffer(paramString2);
/*      */     try {
/*  304 */       CopyFileEx0(localNativeBuffer1.address(), localNativeBuffer2.address(), paramInt, paramLong);
/*      */     }
/*      */     finally {
/*  307 */       localNativeBuffer2.release();
/*  308 */       localNativeBuffer1.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native void CopyFileEx0(long paramLong1, long paramLong2, int paramInt, long paramLong3)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static void MoveFileEx(String paramString1, String paramString2, int paramInt)
/*      */     throws WindowsException
/*      */   {
/*  324 */     NativeBuffer localNativeBuffer1 = asNativeBuffer(paramString1);
/*  325 */     NativeBuffer localNativeBuffer2 = asNativeBuffer(paramString2);
/*      */     try {
/*  327 */       MoveFileEx0(localNativeBuffer1.address(), localNativeBuffer2.address(), paramInt);
/*      */     } finally {
/*  329 */       localNativeBuffer2.release();
/*  330 */       localNativeBuffer1.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static native void MoveFileEx0(long paramLong1, long paramLong2, int paramInt)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */   static int GetFileAttributes(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  342 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  344 */       return GetFileAttributes0(localNativeBuffer.address());
/*      */     } finally {
/*  346 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */   private static native int GetFileAttributes0(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */   /* Error */
/*      */   static void SetFileAttributes(String paramString, int paramInt)
/*      */     throws WindowsException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokestatic 324	sun/nio/fs/WindowsNativeDispatcher:asNativeBuffer	(Ljava/lang/String;)Lsun/nio/fs/NativeBuffer;
/*      */     //   4: astore_2
/*      */     //   5: aload_2
/*      */     //   6: invokevirtual 288	sun/nio/fs/NativeBuffer:address	()J
/*      */     //   9: iload_1
/*      */     //   10: invokestatic 301	sun/nio/fs/WindowsNativeDispatcher:SetFileAttributes0	(JI)V
/*      */     //   13: aload_2
/*      */     //   14: invokevirtual 289	sun/nio/fs/NativeBuffer:release	()V
/*      */     //   17: goto +10 -> 27
/*      */     //   20: astore_3
/*      */     //   21: aload_2
/*      */     //   22: invokevirtual 289	sun/nio/fs/NativeBuffer:release	()V
/*      */     //   25: aload_3
/*      */     //   26: athrow
/*      */     //   27: return
/*      */     // Line number table:
/*      */     //   Java source line #360	-> byte code offset #0
/*      */     //   Java source line #362	-> byte code offset #5
/*      */     //   Java source line #364	-> byte code offset #13
/*      */     //   Java source line #365	-> byte code offset #17
/*      */     //   Java source line #364	-> byte code offset #20
/*      */     //   Java source line #365	-> byte code offset #25
/*      */     //   Java source line #366	-> byte code offset #27
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	28	0	paramString	String
/*      */     //   0	28	1	paramInt	int
/*      */     //   4	18	2	localNativeBuffer	NativeBuffer
/*      */     //   20	6	3	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   5	13	20	finally
/*      */   }
/*      */   
/*      */   private static native void SetFileAttributes0(long paramLong, int paramInt)
/*      */     throws WindowsException;
/*      */   
/*      */   static void GetFileAttributesEx(String paramString, long paramLong)
/*      */     throws WindowsException
/*      */   {
/*  378 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  380 */       GetFileAttributesEx0(localNativeBuffer.address(), paramLong);
/*      */     } finally {
/*  382 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native void GetFileAttributesEx0(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void SetFileTime(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void SetEndOfFile(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native int GetLogicalDrives()
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static VolumeInformation GetVolumeInformation(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  428 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  430 */       VolumeInformation localVolumeInformation1 = new VolumeInformation(null);
/*  431 */       GetVolumeInformation0(localNativeBuffer.address(), localVolumeInformation1);
/*  432 */       return localVolumeInformation1;
/*      */     } finally {
/*  434 */       localNativeBuffer.release();
/*      */     } }
/*      */   
/*      */   private static native void GetVolumeInformation0(long paramLong, VolumeInformation paramVolumeInformation) throws WindowsException;
/*      */   
/*      */   static class VolumeInformation { private String fileSystemName;
/*      */     private String volumeName;
/*      */     private int volumeSerialNumber;
/*      */     private int flags;
/*      */     
/*  444 */     public String fileSystemName() { return this.fileSystemName; }
/*  445 */     public String volumeName() { return this.volumeName; }
/*  446 */     public int volumeSerialNumber() { return this.volumeSerialNumber; }
/*  447 */     public int flags() { return this.flags; }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static int GetDriveType(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  459 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  461 */       return GetDriveType0(localNativeBuffer.address());
/*      */     } finally {
/*  463 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native int GetDriveType0(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static DiskFreeSpace GetDiskFreeSpaceEx(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  479 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  481 */       DiskFreeSpace localDiskFreeSpace1 = new DiskFreeSpace(null);
/*  482 */       GetDiskFreeSpaceEx0(localNativeBuffer.address(), localDiskFreeSpace1);
/*  483 */       return localDiskFreeSpace1;
/*      */     } finally {
/*  485 */       localNativeBuffer.release();
/*      */     } }
/*      */   
/*      */   private static native void GetDiskFreeSpaceEx0(long paramLong, DiskFreeSpace paramDiskFreeSpace) throws WindowsException;
/*      */   
/*      */   static class DiskFreeSpace { private long freeBytesAvailable;
/*      */     private long totalNumberOfBytes;
/*      */     private long totalNumberOfFreeBytes;
/*      */     
/*  494 */     public long freeBytesAvailable() { return this.freeBytesAvailable; }
/*  495 */     public long totalNumberOfBytes() { return this.totalNumberOfBytes; }
/*  496 */     public long totalNumberOfFreeBytes() { return this.totalNumberOfFreeBytes; }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static String GetVolumePathName(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  513 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  515 */       return GetVolumePathName0(localNativeBuffer.address());
/*      */     } finally {
/*  517 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native String GetVolumePathName0(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void InitializeSecurityDescriptor(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void InitializeAcl(long paramLong, int paramInt)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static int GetFileSecurity(String paramString, int paramInt1, long paramLong, int paramInt2)
/*      */     throws WindowsException
/*      */   {
/*  557 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  559 */       return GetFileSecurity0(localNativeBuffer.address(), paramInt1, paramLong, paramInt2);
/*      */     }
/*      */     finally {
/*  562 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native int GetFileSecurity0(long paramLong1, int paramInt1, long paramLong2, int paramInt2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static void SetFileSecurity(String paramString, int paramInt, long paramLong)
/*      */     throws WindowsException
/*      */   {
/*  582 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  584 */       SetFileSecurity0(localNativeBuffer.address(), paramInt, paramLong);
/*      */     }
/*      */     finally {
/*  587 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void SetFileSecurity0(long paramLong1, int paramInt, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native long GetSecurityDescriptorOwner(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void SetSecurityDescriptorOwner(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native long GetSecurityDescriptorDacl(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void SetSecurityDescriptorDacl(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static AclInformation GetAclInformation(long paramLong)
/*      */   {
/*  647 */     AclInformation localAclInformation = new AclInformation(null);
/*  648 */     GetAclInformation0(paramLong, localAclInformation);
/*  649 */     return localAclInformation; }
/*      */   
/*      */   private static native void GetAclInformation0(long paramLong, AclInformation paramAclInformation);
/*      */   
/*      */   static native long GetAce(long paramLong, int paramInt);
/*      */   
/*  655 */   static class AclInformation { public int aceCount() { return this.aceCount; }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int aceCount;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void AddAccessAllowedAceEx(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void AddAccessDeniedAceEx(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static Account LookupAccountSid(long paramLong)
/*      */     throws WindowsException
/*      */   {
/*  705 */     Account localAccount = new Account(null);
/*  706 */     LookupAccountSid0(paramLong, localAccount);
/*  707 */     return localAccount; }
/*      */   
/*      */   private static native void LookupAccountSid0(long paramLong, Account paramAccount) throws WindowsException;
/*      */   
/*      */   static class Account { private String domain;
/*      */     private String name;
/*      */     private int use;
/*      */     
/*  715 */     public String domain() { return this.domain; }
/*  716 */     public String name() { return this.name; }
/*  717 */     public int use() { return this.use; }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static int LookupAccountName(String paramString, long paramLong, int paramInt)
/*      */     throws WindowsException
/*      */   {
/*  739 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  741 */       return LookupAccountName0(localNativeBuffer.address(), paramLong, paramInt);
/*      */     } finally {
/*  743 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native int LookupAccountName0(long paramLong1, long paramLong2, int paramInt)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native int GetLengthSid(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native String ConvertSidToStringSid(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static long ConvertStringSidToSid(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  778 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  780 */       return ConvertStringSidToSid0(localNativeBuffer.address());
/*      */     } finally {
/*  782 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native long ConvertStringSidToSid0(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native long GetCurrentProcess();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native long GetCurrentThread();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native long OpenProcessToken(long paramLong, int paramInt)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native long OpenThreadToken(long paramLong, int paramInt, boolean paramBoolean)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native long DuplicateTokenEx(long paramLong, int paramInt)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void SetThreadToken(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native int GetTokenInformation(long paramLong1, int paramInt1, long paramLong2, int paramInt2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void AdjustTokenPrivileges(long paramLong1, long paramLong2, int paramInt)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native boolean AccessCheck(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static long LookupPrivilegeValue(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  878 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  880 */       return LookupPrivilegeValue0(localNativeBuffer.address());
/*      */     } finally {
/*  882 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native long LookupPrivilegeValue0(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static void CreateSymbolicLink(String paramString1, String paramString2, int paramInt)
/*      */     throws WindowsException
/*      */   {
/*  898 */     NativeBuffer localNativeBuffer1 = asNativeBuffer(paramString1);
/*  899 */     NativeBuffer localNativeBuffer2 = asNativeBuffer(paramString2);
/*      */     try {
/*  901 */       CreateSymbolicLink0(localNativeBuffer1.address(), localNativeBuffer2.address(), paramInt);
/*      */     }
/*      */     finally {
/*  904 */       localNativeBuffer2.release();
/*  905 */       localNativeBuffer1.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native void CreateSymbolicLink0(long paramLong1, long paramLong2, int paramInt)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static void CreateHardLink(String paramString1, String paramString2)
/*      */     throws WindowsException
/*      */   {
/*  921 */     NativeBuffer localNativeBuffer1 = asNativeBuffer(paramString1);
/*  922 */     NativeBuffer localNativeBuffer2 = asNativeBuffer(paramString2);
/*      */     try {
/*  924 */       CreateHardLink0(localNativeBuffer1.address(), localNativeBuffer2.address());
/*      */     } finally {
/*  926 */       localNativeBuffer2.release();
/*  927 */       localNativeBuffer1.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native void CreateHardLink0(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */   static String GetFullPathName(String paramString)
/*      */     throws WindowsException
/*      */   {
/*  942 */     NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
/*      */     try {
/*  944 */       return GetFullPathName0(localNativeBuffer.address());
/*      */     } finally {
/*  946 */       localNativeBuffer.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static native String GetFullPathName0(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native String GetFinalPathNameByHandle(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native String FormatMessage(int paramInt);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void LocalFree(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native long CreateIoCompletionPort(long paramLong1, long paramLong2, long paramLong3)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static CompletionStatus GetQueuedCompletionStatus(long paramLong)
/*      */     throws WindowsException
/*      */   {
/* 1006 */     CompletionStatus localCompletionStatus = new CompletionStatus(null);
/* 1007 */     GetQueuedCompletionStatus0(paramLong, localCompletionStatus);
/* 1008 */     return localCompletionStatus;
/*      */   }
/*      */   
/*      */   private static native void GetQueuedCompletionStatus0(long paramLong, CompletionStatus paramCompletionStatus) throws WindowsException;
/*      */   
/*      */   static native void PostQueuedCompletionStatus(long paramLong1, long paramLong2) throws WindowsException;
/*      */   
/*      */   static class CompletionStatus {
/* 1016 */     int error() { return this.error; }
/* 1017 */     int bytesTransferred() { return this.bytesTransferred; }
/* 1018 */     long completionKey() { return this.completionKey; }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int error;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int bytesTransferred;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private long completionKey;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void ReadDirectoryChangesW(long paramLong1, long paramLong2, int paramInt1, boolean paramBoolean, int paramInt2, long paramLong3, long paramLong4)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native void CancelIo(long paramLong)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static native int GetOverlappedResult(long paramLong1, long paramLong2)
/*      */     throws WindowsException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static BackupResult BackupRead(long paramLong1, long paramLong2, int paramInt, boolean paramBoolean, long paramLong3)
/*      */     throws WindowsException
/*      */   {
/* 1092 */     BackupResult localBackupResult = new BackupResult(null);
/* 1093 */     BackupRead0(paramLong1, paramLong2, paramInt, paramBoolean, paramLong3, localBackupResult);
/* 1094 */     return localBackupResult;
/*      */   }
/*      */   
/*      */   private static native void BackupRead0(long paramLong1, long paramLong2, int paramInt, boolean paramBoolean, long paramLong3, BackupResult paramBackupResult) throws WindowsException;
/*      */   
/*      */   static native void BackupSeek(long paramLong1, long paramLong2, long paramLong3) throws WindowsException;
/*      */   
/* 1101 */   static class BackupResult { int bytesTransferred() { return this.bytesTransferred; }
/* 1102 */     long context() { return this.context; }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int bytesTransferred;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private long context;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1124 */   private static final Unsafe unsafe = ;
/*      */   
/*      */   static NativeBuffer asNativeBuffer(String paramString) {
/* 1127 */     int i = paramString.length() << 1;
/* 1128 */     int j = i + 2;
/*      */     
/*      */ 
/* 1131 */     NativeBuffer localNativeBuffer = NativeBuffers.getNativeBufferFromCache(j);
/* 1132 */     if (localNativeBuffer == null) {
/* 1133 */       localNativeBuffer = NativeBuffers.allocNativeBuffer(j);
/*      */ 
/*      */     }
/* 1136 */     else if (localNativeBuffer.owner() == paramString) {
/* 1137 */       return localNativeBuffer;
/*      */     }
/*      */     
/*      */ 
/* 1141 */     char[] arrayOfChar = paramString.toCharArray();
/* 1142 */     unsafe.copyMemory(arrayOfChar, Unsafe.ARRAY_CHAR_BASE_OFFSET, null, localNativeBuffer
/* 1143 */       .address(), i);
/* 1144 */     unsafe.putChar(localNativeBuffer.address() + i, '\000');
/* 1145 */     localNativeBuffer.setOwner(paramString);
/* 1146 */     return localNativeBuffer;
/*      */   }
/*      */   
/*      */ 
/*      */   private static native void initIDs();
/*      */   
/*      */   static
/*      */   {
/* 1154 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Void run() {
/* 1157 */         System.loadLibrary("net");
/* 1158 */         System.loadLibrary("nio");
/* 1159 */         return null;
/* 1160 */       } });
/* 1161 */     initIDs();
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\WindowsNativeDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
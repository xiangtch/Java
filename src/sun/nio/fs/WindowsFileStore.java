/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.FileStore;
/*     */ import java.nio.file.FileSystemException;
/*     */ import java.nio.file.attribute.AclFileAttributeView;
/*     */ import java.nio.file.attribute.BasicFileAttributeView;
/*     */ import java.nio.file.attribute.DosFileAttributeView;
/*     */ import java.nio.file.attribute.FileAttributeView;
/*     */ import java.nio.file.attribute.FileOwnerAttributeView;
/*     */ import java.nio.file.attribute.FileStoreAttributeView;
/*     */ import java.nio.file.attribute.UserDefinedFileAttributeView;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WindowsFileStore
/*     */   extends FileStore
/*     */ {
/*     */   private final String root;
/*     */   private final WindowsNativeDispatcher.VolumeInformation volInfo;
/*     */   private final int volType;
/*     */   private final String displayName;
/*     */   
/*     */   private WindowsFileStore(String paramString)
/*     */     throws WindowsException
/*     */   {
/*  48 */     assert (paramString.charAt(paramString.length() - 1) == '\\');
/*  49 */     this.root = paramString;
/*  50 */     this.volInfo = WindowsNativeDispatcher.GetVolumeInformation(paramString);
/*  51 */     this.volType = WindowsNativeDispatcher.GetDriveType(paramString);
/*     */     
/*     */ 
/*  54 */     String str = this.volInfo.volumeName();
/*  55 */     if (str.length() > 0) {
/*  56 */       this.displayName = str;
/*     */     }
/*     */     else {
/*  59 */       this.displayName = (this.volType == 2 ? "Removable Disk" : "");
/*     */     }
/*     */   }
/*     */   
/*     */   static WindowsFileStore create(String paramString, boolean paramBoolean) throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  67 */       return new WindowsFileStore(paramString);
/*     */     } catch (WindowsException localWindowsException) {
/*  69 */       if ((paramBoolean) && (localWindowsException.lastError() == 21))
/*  70 */         return null;
/*  71 */       localWindowsException.rethrowAsIOException(paramString); }
/*  72 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   static WindowsFileStore create(WindowsPath paramWindowsPath)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*     */       String str;
/*  82 */       if (paramWindowsPath.getFileSystem().supportsLinks()) {
/*  83 */         str = WindowsLinkSupport.getFinalPath(paramWindowsPath, true);
/*     */       }
/*     */       else {
/*  86 */         WindowsFileAttributes.get(paramWindowsPath, true);
/*  87 */         str = paramWindowsPath.getPathForWin32Calls();
/*     */       }
/*     */       try {
/*  90 */         return createFromPath(str);
/*     */       } catch (WindowsException localWindowsException2) {
/*  92 */         if (localWindowsException2.lastError() != 144)
/*  93 */           throw localWindowsException2;
/*  94 */         str = WindowsLinkSupport.getFinalPath(paramWindowsPath);
/*  95 */         if (str == null) {
/*  96 */           throw new FileSystemException(paramWindowsPath.getPathForExceptionMessage(), null, "Couldn't resolve path");
/*     */         }
/*  98 */         return createFromPath(str);
/*     */       }
/*     */       
/*     */ 
/* 102 */       return null;
/*     */     }
/*     */     catch (WindowsException localWindowsException1)
/*     */     {
/* 101 */       localWindowsException1.rethrowAsIOException(paramWindowsPath);
/*     */     }
/*     */   }
/*     */   
/*     */   private static WindowsFileStore createFromPath(String paramString) throws WindowsException
/*     */   {
/* 107 */     String str = WindowsNativeDispatcher.GetVolumePathName(paramString);
/* 108 */     return new WindowsFileStore(str);
/*     */   }
/*     */   
/*     */   WindowsNativeDispatcher.VolumeInformation volumeInformation() {
/* 112 */     return this.volInfo;
/*     */   }
/*     */   
/*     */   int volumeType() {
/* 116 */     return this.volType;
/*     */   }
/*     */   
/*     */   public String name()
/*     */   {
/* 121 */     return this.volInfo.volumeName();
/*     */   }
/*     */   
/*     */   public String type()
/*     */   {
/* 126 */     return this.volInfo.fileSystemName();
/*     */   }
/*     */   
/*     */   public boolean isReadOnly()
/*     */   {
/* 131 */     return (this.volInfo.flags() & 0x80000) != 0;
/*     */   }
/*     */   
/*     */   private WindowsNativeDispatcher.DiskFreeSpace readDiskFreeSpace() throws IOException
/*     */   {
/*     */     try {
/* 137 */       return WindowsNativeDispatcher.GetDiskFreeSpaceEx(this.root);
/*     */     } catch (WindowsException localWindowsException) {
/* 139 */       localWindowsException.rethrowAsIOException(this.root); }
/* 140 */     return null;
/*     */   }
/*     */   
/*     */   public long getTotalSpace()
/*     */     throws IOException
/*     */   {
/* 146 */     return readDiskFreeSpace().totalNumberOfBytes();
/*     */   }
/*     */   
/*     */   public long getUsableSpace() throws IOException
/*     */   {
/* 151 */     return readDiskFreeSpace().freeBytesAvailable();
/*     */   }
/*     */   
/*     */   public long getUnallocatedSpace() throws IOException
/*     */   {
/* 156 */     return readDiskFreeSpace().freeBytesAvailable();
/*     */   }
/*     */   
/*     */   public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> paramClass)
/*     */   {
/* 161 */     if (paramClass == null)
/* 162 */       throw new NullPointerException();
/* 163 */     return (FileStoreAttributeView)null;
/*     */   }
/*     */   
/*     */   public Object getAttribute(String paramString)
/*     */     throws IOException
/*     */   {
/* 169 */     if (paramString.equals("totalSpace"))
/* 170 */       return Long.valueOf(getTotalSpace());
/* 171 */     if (paramString.equals("usableSpace"))
/* 172 */       return Long.valueOf(getUsableSpace());
/* 173 */     if (paramString.equals("unallocatedSpace")) {
/* 174 */       return Long.valueOf(getUnallocatedSpace());
/*     */     }
/* 176 */     if (paramString.equals("volume:vsn"))
/* 177 */       return Integer.valueOf(this.volInfo.volumeSerialNumber());
/* 178 */     if (paramString.equals("volume:isRemovable"))
/* 179 */       return Boolean.valueOf(this.volType == 2);
/* 180 */     if (paramString.equals("volume:isCdrom"))
/* 181 */       return Boolean.valueOf(this.volType == 5);
/* 182 */     throw new UnsupportedOperationException("'" + paramString + "' not recognized");
/*     */   }
/*     */   
/*     */   public boolean supportsFileAttributeView(Class<? extends FileAttributeView> paramClass)
/*     */   {
/* 187 */     if (paramClass == null)
/* 188 */       throw new NullPointerException();
/* 189 */     if ((paramClass == BasicFileAttributeView.class) || (paramClass == DosFileAttributeView.class))
/* 190 */       return true;
/* 191 */     if ((paramClass == AclFileAttributeView.class) || (paramClass == FileOwnerAttributeView.class))
/* 192 */       return (this.volInfo.flags() & 0x8) != 0;
/* 193 */     if (paramClass == UserDefinedFileAttributeView.class)
/* 194 */       return (this.volInfo.flags() & 0x40000) != 0;
/* 195 */     return false;
/*     */   }
/*     */   
/*     */   public boolean supportsFileAttributeView(String paramString)
/*     */   {
/* 200 */     if ((paramString.equals("basic")) || (paramString.equals("dos")))
/* 201 */       return true;
/* 202 */     if (paramString.equals("acl"))
/* 203 */       return supportsFileAttributeView(AclFileAttributeView.class);
/* 204 */     if (paramString.equals("owner"))
/* 205 */       return supportsFileAttributeView(FileOwnerAttributeView.class);
/* 206 */     if (paramString.equals("user"))
/* 207 */       return supportsFileAttributeView(UserDefinedFileAttributeView.class);
/* 208 */     return false;
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 213 */     if (paramObject == this)
/* 214 */       return true;
/* 215 */     if (!(paramObject instanceof WindowsFileStore))
/* 216 */       return false;
/* 217 */     WindowsFileStore localWindowsFileStore = (WindowsFileStore)paramObject;
/* 218 */     return this.root.equals(localWindowsFileStore.root);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 223 */     return this.root.hashCode();
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 228 */     StringBuilder localStringBuilder = new StringBuilder(this.displayName);
/* 229 */     if (localStringBuilder.length() > 0)
/* 230 */       localStringBuilder.append(" ");
/* 231 */     localStringBuilder.append("(");
/*     */     
/* 233 */     localStringBuilder.append(this.root.subSequence(0, this.root.length() - 1));
/* 234 */     localStringBuilder.append(")");
/* 235 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\WindowsFileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
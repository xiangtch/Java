/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.nio.file.attribute.DosFileAttributes;
/*     */ import java.nio.file.attribute.FileTime;
/*     */ import java.security.AccessController;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WindowsFileAttributes
/*     */   implements DosFileAttributes
/*     */ {
/*  44 */   private static final Unsafe unsafe = ;
/*     */   
/*     */   private static final short SIZEOF_FILE_INFORMATION = 52;
/*     */   
/*     */   private static final short OFFSETOF_FILE_INFORMATION_ATTRIBUTES = 0;
/*     */   
/*     */   private static final short OFFSETOF_FILE_INFORMATION_CREATETIME = 4;
/*     */   
/*     */   private static final short OFFSETOF_FILE_INFORMATION_LASTACCESSTIME = 12;
/*     */   
/*     */   private static final short OFFSETOF_FILE_INFORMATION_LASTWRITETIME = 20;
/*     */   
/*     */   private static final short OFFSETOF_FILE_INFORMATION_VOLSERIALNUM = 28;
/*     */   
/*     */   private static final short OFFSETOF_FILE_INFORMATION_SIZEHIGH = 32;
/*     */   
/*     */   private static final short OFFSETOF_FILE_INFORMATION_SIZELOW = 36;
/*     */   
/*     */   private static final short OFFSETOF_FILE_INFORMATION_INDEXHIGH = 44;
/*     */   
/*     */   private static final short OFFSETOF_FILE_INFORMATION_INDEXLOW = 48;
/*     */   
/*     */   private static final short SIZEOF_FILE_ATTRIBUTE_DATA = 36;
/*     */   
/*     */   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_ATTRIBUTES = 0;
/*     */   
/*     */   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_CREATETIME = 4;
/*     */   
/*     */   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTACCESSTIME = 12;
/*     */   
/*     */   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTWRITETIME = 20;
/*     */   
/*     */   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZEHIGH = 28;
/*     */   
/*     */   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZELOW = 32;
/*     */   
/*     */   private static final short SIZEOF_FIND_DATA = 592;
/*     */   
/*     */   private static final short OFFSETOF_FIND_DATA_ATTRIBUTES = 0;
/*     */   
/*     */   private static final short OFFSETOF_FIND_DATA_CREATETIME = 4;
/*     */   
/*     */   private static final short OFFSETOF_FIND_DATA_LASTACCESSTIME = 12;
/*     */   
/*     */   private static final short OFFSETOF_FIND_DATA_LASTWRITETIME = 20;
/*     */   
/*     */   private static final short OFFSETOF_FIND_DATA_SIZEHIGH = 28;
/*     */   
/*     */   private static final short OFFSETOF_FIND_DATA_SIZELOW = 32;
/*     */   
/*     */   private static final short OFFSETOF_FIND_DATA_RESERVED0 = 36;
/*     */   
/*     */   private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
/*     */   
/*     */   private static final boolean ensureAccurateMetadata;
/*     */   
/*     */   private final int fileAttrs;
/*     */   
/*     */   private final long creationTime;
/*     */   
/*     */   private final long lastAccessTime;
/*     */   
/*     */   private final long lastWriteTime;
/*     */   
/*     */   private final long size;
/*     */   
/*     */   private final int reparseTag;
/*     */   
/*     */   private final int volSerialNumber;
/*     */   private final int fileIndexHigh;
/*     */   private final int fileIndexLow;
/*     */   
/*     */   static
/*     */   {
/* 118 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.fs.ensureAccurateMetadata", "false"));
/*     */     
/*     */ 
/* 121 */     ensureAccurateMetadata = str.length() == 0 ? true : Boolean.valueOf(str).booleanValue();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static FileTime toFileTime(long paramLong)
/*     */   {
/* 143 */     paramLong /= 10L;
/*     */     
/* 145 */     paramLong += -11644473600000000L;
/* 146 */     return FileTime.from(paramLong, TimeUnit.MICROSECONDS);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static long toWindowsTime(FileTime paramFileTime)
/*     */   {
/* 154 */     long l = paramFileTime.to(TimeUnit.MICROSECONDS);
/*     */     
/* 156 */     l -= -11644473600000000L;
/*     */     
/* 158 */     l *= 10L;
/* 159 */     return l;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   private WindowsFileAttributes(int paramInt1, long paramLong1, long paramLong2, long paramLong3, long paramLong4, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 175 */     this.fileAttrs = paramInt1;
/* 176 */     this.creationTime = paramLong1;
/* 177 */     this.lastAccessTime = paramLong2;
/* 178 */     this.lastWriteTime = paramLong3;
/* 179 */     this.size = paramLong4;
/* 180 */     this.reparseTag = paramInt2;
/* 181 */     this.volSerialNumber = paramInt3;
/* 182 */     this.fileIndexHigh = paramInt4;
/* 183 */     this.fileIndexLow = paramInt5;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static WindowsFileAttributes fromFileInformation(long paramLong, int paramInt)
/*     */   {
/* 190 */     int i = unsafe.getInt(paramLong + 0L);
/* 191 */     long l1 = unsafe.getLong(paramLong + 4L);
/* 192 */     long l2 = unsafe.getLong(paramLong + 12L);
/* 193 */     long l3 = unsafe.getLong(paramLong + 20L);
/*     */     
/* 195 */     long l4 = (unsafe.getInt(paramLong + 32L) << 32) + (unsafe.getInt(paramLong + 36L) & 0xFFFFFFFF);
/* 196 */     int j = unsafe.getInt(paramLong + 28L);
/* 197 */     int k = unsafe.getInt(paramLong + 44L);
/* 198 */     int m = unsafe.getInt(paramLong + 48L);
/* 199 */     return new WindowsFileAttributes(i, l1, l2, l3, l4, paramInt, j, k, m);
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
/*     */ 
/*     */ 
/*     */   private static WindowsFileAttributes fromFileAttributeData(long paramLong, int paramInt)
/*     */   {
/* 214 */     int i = unsafe.getInt(paramLong + 0L);
/* 215 */     long l1 = unsafe.getLong(paramLong + 4L);
/* 216 */     long l2 = unsafe.getLong(paramLong + 12L);
/* 217 */     long l3 = unsafe.getLong(paramLong + 20L);
/*     */     
/* 219 */     long l4 = (unsafe.getInt(paramLong + 28L) << 32) + (unsafe.getInt(paramLong + 32L) & 0xFFFFFFFF);
/* 220 */     return new WindowsFileAttributes(i, l1, l2, l3, l4, paramInt, 0, 0, 0);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   static NativeBuffer getBufferForFindData()
/*     */   {
/* 236 */     return NativeBuffers.getNativeBuffer(592);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static WindowsFileAttributes fromFindData(long paramLong)
/*     */   {
/* 243 */     int i = unsafe.getInt(paramLong + 0L);
/* 244 */     long l1 = unsafe.getLong(paramLong + 4L);
/* 245 */     long l2 = unsafe.getLong(paramLong + 12L);
/* 246 */     long l3 = unsafe.getLong(paramLong + 20L);
/*     */     
/* 248 */     long l4 = (unsafe.getInt(paramLong + 28L) << 32) + (unsafe.getInt(paramLong + 32L) & 0xFFFFFFFF);
/*     */     
/* 250 */     int j = isReparsePoint(i) ? unsafe.getInt(paramLong + 36L) : 0;
/* 251 */     return new WindowsFileAttributes(i, l1, l2, l3, l4, j, 0, 0, 0);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static WindowsFileAttributes readAttributes(long paramLong)
/*     */     throws WindowsException
/*     */   {
/* 269 */     NativeBuffer localNativeBuffer1 = NativeBuffers.getNativeBuffer(52);
/*     */     try {
/* 271 */       long l = localNativeBuffer1.address();
/* 272 */       WindowsNativeDispatcher.GetFileInformationByHandle(paramLong, l);
/*     */       
/*     */ 
/* 275 */       int i = 0;
/*     */       
/* 277 */       int j = unsafe.getInt(l + 0L);
/* 278 */       if (isReparsePoint(j)) {
/* 279 */         int k = 16384;
/* 280 */         NativeBuffer localNativeBuffer2 = NativeBuffers.getNativeBuffer(k);
/*     */         try {
/* 282 */           WindowsNativeDispatcher.DeviceIoControlGetReparsePoint(paramLong, localNativeBuffer2.address(), k);
/* 283 */           i = (int)unsafe.getLong(localNativeBuffer2.address());
/*     */         }
/*     */         finally {}
/*     */       }
/*     */       
/*     */ 
/* 289 */       return fromFileInformation(l, i);
/*     */     } finally {
/* 291 */       localNativeBuffer1.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static WindowsFileAttributes get(WindowsPath paramWindowsPath, boolean paramBoolean)
/*     */     throws WindowsException
/*     */   {
/*     */     Object localObject2;
/*     */     
/* 301 */     if (!ensureAccurateMetadata) {
/* 302 */       Object localObject1 = null;
/*     */       
/*     */ 
/*     */ 
/* 306 */       NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(36);
/*     */       try {
/* 308 */         long l2 = localNativeBuffer.address();
/* 309 */         WindowsNativeDispatcher.GetFileAttributesEx(paramWindowsPath.getPathForWin32Calls(), l2);
/*     */         
/*     */ 
/*     */ 
/* 313 */         int j = unsafe.getInt(l2 + 0L);
/* 314 */         if (!isReparsePoint(j))
/* 315 */           return fromFileAttributeData(l2, 0);
/*     */       } catch (WindowsException localWindowsException1) {
/* 317 */         if (localWindowsException1.lastError() != 32)
/* 318 */           throw localWindowsException1;
/* 319 */         localObject1 = localWindowsException1;
/*     */       } finally {
/* 321 */         localNativeBuffer.release();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 326 */       if (localObject1 != null) {
/* 327 */         localObject2 = paramWindowsPath.getPathForWin32Calls();
/* 328 */         int i = ((String)localObject2).charAt(((String)localObject2).length() - 1);
/* 329 */         if ((i == 58) || (i == 92))
/* 330 */           throw ((Throwable)localObject1);
/* 331 */         localNativeBuffer = getBufferForFindData();
/*     */         try {
/* 333 */           long l3 = WindowsNativeDispatcher.FindFirstFile((String)localObject2, localNativeBuffer.address());
/* 334 */           WindowsNativeDispatcher.FindClose(l3);
/* 335 */           WindowsFileAttributes localWindowsFileAttributes2 = fromFindData(localNativeBuffer.address());
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 340 */           if (localWindowsFileAttributes2.isReparsePoint())
/* 341 */             throw ((Throwable)localObject1);
/* 342 */           return localWindowsFileAttributes2;
/*     */         } catch (WindowsException localWindowsException2) {
/* 344 */           throw ((Throwable)localObject1);
/*     */         } finally {
/* 346 */           localNativeBuffer.release();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 352 */     long l1 = paramWindowsPath.openForReadAttributeAccess(paramBoolean);
/*     */     try {
/* 354 */       return readAttributes(l1);
/*     */     } finally {
/* 356 */       WindowsNativeDispatcher.CloseHandle(l1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean isSameFile(WindowsFileAttributes paramWindowsFileAttributes1, WindowsFileAttributes paramWindowsFileAttributes2)
/*     */   {
/* 368 */     return (paramWindowsFileAttributes1.volSerialNumber == paramWindowsFileAttributes2.volSerialNumber) && (paramWindowsFileAttributes1.fileIndexHigh == paramWindowsFileAttributes2.fileIndexHigh) && (paramWindowsFileAttributes1.fileIndexLow == paramWindowsFileAttributes2.fileIndexLow);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean isReparsePoint(int paramInt)
/*     */   {
/* 377 */     return (paramInt & 0x400) != 0;
/*     */   }
/*     */   
/*     */   int attributes()
/*     */   {
/* 382 */     return this.fileAttrs;
/*     */   }
/*     */   
/*     */   int volSerialNumber() {
/* 386 */     return this.volSerialNumber;
/*     */   }
/*     */   
/*     */   int fileIndexHigh() {
/* 390 */     return this.fileIndexHigh;
/*     */   }
/*     */   
/*     */   int fileIndexLow() {
/* 394 */     return this.fileIndexLow;
/*     */   }
/*     */   
/*     */   public long size()
/*     */   {
/* 399 */     return this.size;
/*     */   }
/*     */   
/*     */   public FileTime lastModifiedTime()
/*     */   {
/* 404 */     return toFileTime(this.lastWriteTime);
/*     */   }
/*     */   
/*     */   public FileTime lastAccessTime()
/*     */   {
/* 409 */     return toFileTime(this.lastAccessTime);
/*     */   }
/*     */   
/*     */   public FileTime creationTime()
/*     */   {
/* 414 */     return toFileTime(this.creationTime);
/*     */   }
/*     */   
/*     */   public Object fileKey()
/*     */   {
/* 419 */     return null;
/*     */   }
/*     */   
/*     */   boolean isReparsePoint()
/*     */   {
/* 424 */     return isReparsePoint(this.fileAttrs);
/*     */   }
/*     */   
/*     */   boolean isDirectoryLink() {
/* 428 */     return (isSymbolicLink()) && ((this.fileAttrs & 0x10) != 0);
/*     */   }
/*     */   
/*     */   public boolean isSymbolicLink()
/*     */   {
/* 433 */     return this.reparseTag == -1610612724;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDirectory()
/*     */   {
/* 439 */     if (isSymbolicLink())
/* 440 */       return false;
/* 441 */     return (this.fileAttrs & 0x10) != 0;
/*     */   }
/*     */   
/*     */   public boolean isOther()
/*     */   {
/* 446 */     if (isSymbolicLink()) {
/* 447 */       return false;
/*     */     }
/* 449 */     return (this.fileAttrs & 0x440) != 0;
/*     */   }
/*     */   
/*     */   public boolean isRegularFile()
/*     */   {
/* 454 */     return (!isSymbolicLink()) && (!isDirectory()) && (!isOther());
/*     */   }
/*     */   
/*     */   public boolean isReadOnly()
/*     */   {
/* 459 */     return (this.fileAttrs & 0x1) != 0;
/*     */   }
/*     */   
/*     */   public boolean isHidden()
/*     */   {
/* 464 */     return (this.fileAttrs & 0x2) != 0;
/*     */   }
/*     */   
/*     */   public boolean isArchive()
/*     */   {
/* 469 */     return (this.fileAttrs & 0x20) != 0;
/*     */   }
/*     */   
/*     */   public boolean isSystem()
/*     */   {
/* 474 */     return (this.fileAttrs & 0x4) != 0;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\WindowsFileAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
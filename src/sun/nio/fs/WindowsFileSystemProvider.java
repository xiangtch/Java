/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.FilePermission;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.nio.channels.AsynchronousFileChannel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.AccessDeniedException;
/*     */ import java.nio.file.AccessMode;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.DirectoryNotEmptyException;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.DirectoryStream.Filter;
/*     */ import java.nio.file.FileAlreadyExistsException;
/*     */ import java.nio.file.FileStore;
/*     */ import java.nio.file.FileSystem;
/*     */ import java.nio.file.FileSystemAlreadyExistsException;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.LinkPermission;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.ProviderMismatchException;
/*     */ import java.nio.file.attribute.AclFileAttributeView;
/*     */ import java.nio.file.attribute.BasicFileAttributeView;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.DosFileAttributeView;
/*     */ import java.nio.file.attribute.DosFileAttributes;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.nio.file.attribute.FileAttributeView;
/*     */ import java.nio.file.attribute.FileOwnerAttributeView;
/*     */ import java.nio.file.attribute.UserDefinedFileAttributeView;
/*     */ import java.security.Permission;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.nio.ch.ThreadPool;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WindowsFileSystemProvider
/*     */   extends AbstractFileSystemProvider
/*     */ {
/*  47 */   private static final Unsafe unsafe = ;
/*     */   private static final String USER_DIR = "user.dir";
/*     */   private final WindowsFileSystem theFileSystem;
/*     */   
/*     */   public WindowsFileSystemProvider()
/*     */   {
/*  53 */     this.theFileSystem = new WindowsFileSystem(this, System.getProperty("user.dir"));
/*     */   }
/*     */   
/*     */   public String getScheme()
/*     */   {
/*  58 */     return "file";
/*     */   }
/*     */   
/*     */   private void checkUri(URI paramURI) {
/*  62 */     if (!paramURI.getScheme().equalsIgnoreCase(getScheme()))
/*  63 */       throw new IllegalArgumentException("URI does not match this provider");
/*  64 */     if (paramURI.getAuthority() != null)
/*  65 */       throw new IllegalArgumentException("Authority component present");
/*  66 */     if (paramURI.getPath() == null)
/*  67 */       throw new IllegalArgumentException("Path component is undefined");
/*  68 */     if (!paramURI.getPath().equals("/"))
/*  69 */       throw new IllegalArgumentException("Path component should be '/'");
/*  70 */     if (paramURI.getQuery() != null)
/*  71 */       throw new IllegalArgumentException("Query component present");
/*  72 */     if (paramURI.getFragment() != null) {
/*  73 */       throw new IllegalArgumentException("Fragment component present");
/*     */     }
/*     */   }
/*     */   
/*     */   public FileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap)
/*     */     throws IOException
/*     */   {
/*  80 */     checkUri(paramURI);
/*  81 */     throw new FileSystemAlreadyExistsException();
/*     */   }
/*     */   
/*     */   public final FileSystem getFileSystem(URI paramURI)
/*     */   {
/*  86 */     checkUri(paramURI);
/*  87 */     return this.theFileSystem;
/*     */   }
/*     */   
/*     */   public Path getPath(URI paramURI)
/*     */   {
/*  92 */     return WindowsUriSupport.fromUri(this.theFileSystem, paramURI);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public FileChannel newFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs)
/*     */     throws IOException
/*     */   {
/* 101 */     if (paramPath == null)
/* 102 */       throw new NullPointerException();
/* 103 */     if (!(paramPath instanceof WindowsPath))
/* 104 */       throw new ProviderMismatchException();
/* 105 */     WindowsPath localWindowsPath = (WindowsPath)paramPath;
/*     */     
/* 107 */     WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
/*     */     try {
/* 109 */       return 
/* 110 */         WindowsChannelFactory.newFileChannel(localWindowsPath.getPathForWin32Calls(), localWindowsPath
/* 111 */         .getPathForPermissionCheck(), paramSet, localWindowsSecurityDescriptor
/*     */         
/* 113 */         .address());
/*     */     } catch (WindowsException localWindowsException) {
/* 115 */       localWindowsException.rethrowAsIOException(localWindowsPath);
/* 116 */       return null;
/*     */     } finally {
/* 118 */       if (localWindowsSecurityDescriptor != null) {
/* 119 */         localWindowsSecurityDescriptor.release();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AsynchronousFileChannel newAsynchronousFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, ExecutorService paramExecutorService, FileAttribute<?>... paramVarArgs)
/*     */     throws IOException
/*     */   {
/* 130 */     if (paramPath == null)
/* 131 */       throw new NullPointerException();
/* 132 */     if (!(paramPath instanceof WindowsPath))
/* 133 */       throw new ProviderMismatchException();
/* 134 */     WindowsPath localWindowsPath = (WindowsPath)paramPath;
/* 135 */     ThreadPool localThreadPool = paramExecutorService == null ? null : ThreadPool.wrap(paramExecutorService, 0);
/*     */     
/* 137 */     WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
/*     */     try {
/* 139 */       return 
/* 140 */         WindowsChannelFactory.newAsynchronousFileChannel(localWindowsPath.getPathForWin32Calls(), localWindowsPath
/* 141 */         .getPathForPermissionCheck(), paramSet, localWindowsSecurityDescriptor
/*     */         
/* 143 */         .address(), localThreadPool);
/*     */     }
/*     */     catch (WindowsException localWindowsException) {
/* 146 */       localWindowsException.rethrowAsIOException(localWindowsPath);
/* 147 */       return null;
/*     */     } finally {
/* 149 */       if (localWindowsSecurityDescriptor != null) {
/* 150 */         localWindowsSecurityDescriptor.release();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public <V extends FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption... paramVarArgs)
/*     */   {
/* 159 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/* 160 */     if (paramClass == null)
/* 161 */       throw new NullPointerException();
/* 162 */     boolean bool = Util.followLinks(paramVarArgs);
/* 163 */     if (paramClass == BasicFileAttributeView.class)
/* 164 */       return WindowsFileAttributeViews.createBasicView(localWindowsPath, bool);
/* 165 */     if (paramClass == DosFileAttributeView.class)
/* 166 */       return WindowsFileAttributeViews.createDosView(localWindowsPath, bool);
/* 167 */     if (paramClass == AclFileAttributeView.class)
/* 168 */       return new WindowsAclFileAttributeView(localWindowsPath, bool);
/* 169 */     if (paramClass == FileOwnerAttributeView.class) {
/* 170 */       return new FileOwnerAttributeViewImpl(new WindowsAclFileAttributeView(localWindowsPath, bool));
/*     */     }
/* 172 */     if (paramClass == UserDefinedFileAttributeView.class)
/* 173 */       return new WindowsUserDefinedFileAttributeView(localWindowsPath, bool);
/* 174 */     return (FileAttributeView)null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public <A extends BasicFileAttributes> A readAttributes(Path paramPath, Class<A> paramClass, LinkOption... paramVarArgs)
/*     */     throws IOException
/*     */   {
/*     */     Class localClass;
/*     */     
/*     */ 
/* 185 */     if (paramClass == BasicFileAttributes.class) {
/* 186 */       localClass = BasicFileAttributeView.class;
/* 187 */     } else if (paramClass == DosFileAttributes.class) {
/* 188 */       localClass = DosFileAttributeView.class;
/* 189 */     } else { if (paramClass == null) {
/* 190 */         throw new NullPointerException();
/*     */       }
/* 192 */       throw new UnsupportedOperationException(); }
/* 193 */     return ((BasicFileAttributeView)getFileAttributeView(paramPath, localClass, paramVarArgs)).readAttributes();
/*     */   }
/*     */   
/*     */   public DynamicFileAttributeView getFileAttributeView(Path paramPath, String paramString, LinkOption... paramVarArgs)
/*     */   {
/* 198 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/* 199 */     boolean bool = Util.followLinks(paramVarArgs);
/* 200 */     if (paramString.equals("basic"))
/* 201 */       return WindowsFileAttributeViews.createBasicView(localWindowsPath, bool);
/* 202 */     if (paramString.equals("dos"))
/* 203 */       return WindowsFileAttributeViews.createDosView(localWindowsPath, bool);
/* 204 */     if (paramString.equals("acl"))
/* 205 */       return new WindowsAclFileAttributeView(localWindowsPath, bool);
/* 206 */     if (paramString.equals("owner")) {
/* 207 */       return new FileOwnerAttributeViewImpl(new WindowsAclFileAttributeView(localWindowsPath, bool));
/*     */     }
/* 209 */     if (paramString.equals("user"))
/* 210 */       return new WindowsUserDefinedFileAttributeView(localWindowsPath, bool);
/* 211 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SeekableByteChannel newByteChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs)
/*     */     throws IOException
/*     */   {
/* 220 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/*     */     
/* 222 */     WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
/*     */     try {
/* 224 */       return 
/* 225 */         WindowsChannelFactory.newFileChannel(localWindowsPath.getPathForWin32Calls(), localWindowsPath
/* 226 */         .getPathForPermissionCheck(), paramSet, localWindowsSecurityDescriptor
/*     */         
/* 228 */         .address());
/*     */     } catch (WindowsException localWindowsException) {
/* 230 */       localWindowsException.rethrowAsIOException(localWindowsPath);
/* 231 */       return null;
/*     */     } finally {
/* 233 */       localWindowsSecurityDescriptor.release();
/*     */     }
/*     */   }
/*     */   
/*     */   boolean implDelete(Path paramPath, boolean paramBoolean) throws IOException
/*     */   {
/* 239 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/* 240 */     localWindowsPath.checkDelete();
/*     */     
/* 242 */     WindowsFileAttributes localWindowsFileAttributes = null;
/*     */     try
/*     */     {
/* 245 */       localWindowsFileAttributes = WindowsFileAttributes.get(localWindowsPath, false);
/* 246 */       if ((localWindowsFileAttributes.isDirectory()) || (localWindowsFileAttributes.isDirectoryLink())) {
/* 247 */         WindowsNativeDispatcher.RemoveDirectory(localWindowsPath.getPathForWin32Calls());
/*     */       } else {
/* 249 */         WindowsNativeDispatcher.DeleteFile(localWindowsPath.getPathForWin32Calls());
/*     */       }
/* 251 */       return true;
/*     */     }
/*     */     catch (WindowsException localWindowsException)
/*     */     {
/* 255 */       if ((!paramBoolean) && (
/* 256 */         (localWindowsException.lastError() == 2) || 
/* 257 */         (localWindowsException.lastError() == 3))) { return false;
/*     */       }
/* 259 */       if ((localWindowsFileAttributes != null) && (localWindowsFileAttributes.isDirectory()))
/*     */       {
/*     */ 
/* 262 */         if ((localWindowsException.lastError() == 145) || 
/* 263 */           (localWindowsException.lastError() == 183))
/*     */         {
/*     */ 
/* 266 */           throw new DirectoryNotEmptyException(localWindowsPath.getPathForExceptionMessage());
/*     */         }
/*     */       }
/* 269 */       localWindowsException.rethrowAsIOException(localWindowsPath); }
/* 270 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void copy(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs)
/*     */     throws IOException
/*     */   {
/* 278 */     WindowsFileCopy.copy(WindowsPath.toWindowsPath(paramPath1), 
/* 279 */       WindowsPath.toWindowsPath(paramPath2), paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void move(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs)
/*     */     throws IOException
/*     */   {
/* 287 */     WindowsFileCopy.move(WindowsPath.toWindowsPath(paramPath1), 
/* 288 */       WindowsPath.toWindowsPath(paramPath2), paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean hasDesiredAccess(WindowsPath paramWindowsPath, int paramInt)
/*     */     throws IOException
/*     */   {
/* 297 */     boolean bool = false;
/* 298 */     String str = WindowsLinkSupport.getFinalPath(paramWindowsPath, true);
/*     */     
/* 300 */     NativeBuffer localNativeBuffer = WindowsAclFileAttributeView.getFileSecurity(str, 7);
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 305 */       bool = WindowsSecurity.checkAccessMask(localNativeBuffer.address(), paramInt, 1179785, 1179926, 1179808, 2032127);
/*     */ 
/*     */     }
/*     */     catch (WindowsException localWindowsException)
/*     */     {
/*     */ 
/* 311 */       localWindowsException.rethrowAsIOException(paramWindowsPath);
/*     */     } finally {
/* 313 */       localNativeBuffer.release();
/*     */     }
/* 315 */     return bool;
/*     */   }
/*     */   
/*     */   private void checkReadAccess(WindowsPath paramWindowsPath)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 323 */       Set localSet = Collections.emptySet();
/*     */       
/* 325 */       FileChannel localFileChannel = WindowsChannelFactory.newFileChannel(paramWindowsPath.getPathForWin32Calls(), paramWindowsPath
/* 326 */         .getPathForPermissionCheck(), localSet, 0L);
/*     */       
/*     */ 
/* 329 */       localFileChannel.close();
/*     */     }
/*     */     catch (WindowsException localWindowsException)
/*     */     {
/*     */       try
/*     */       {
/* 335 */         new WindowsDirectoryStream(paramWindowsPath, null).close();
/*     */       }
/*     */       catch (IOException localIOException) {
/* 338 */         localWindowsException.rethrowAsIOException(paramWindowsPath);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void checkAccess(Path paramPath, AccessMode... paramVarArgs) throws IOException
/*     */   {
/* 345 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/*     */     
/* 347 */     int i = 0;
/* 348 */     int j = 0;
/* 349 */     int k = 0;
/* 350 */     for (AccessMode localAccessMode : paramVarArgs) {
/* 351 */       switch (localAccessMode) {
/* 352 */       case READ:  i = 1; break;
/* 353 */       case WRITE:  j = 1; break;
/* 354 */       case EXECUTE:  k = 1; break;
/* 355 */       default:  throw new AssertionError("Should not get here");
/*     */       }
/*     */       
/*     */     }
/*     */     
/*     */ 
/* 361 */     if ((j == 0) && (k == 0)) {
/* 362 */       checkReadAccess(localWindowsPath);
/* 363 */       return;
/*     */     }
/*     */     
/* 366 */     int m = 0;
/* 367 */     if (i != 0) {
/* 368 */       localWindowsPath.checkRead();
/* 369 */       m |= 0x1;
/*     */     }
/* 371 */     if (j != 0) {
/* 372 */       localWindowsPath.checkWrite();
/* 373 */       m |= 0x2; }
/*     */     Object localObject;
/* 375 */     if (k != 0) {
/* 376 */       localObject = System.getSecurityManager();
/* 377 */       if (localObject != null)
/* 378 */         ((SecurityManager)localObject).checkExec(localWindowsPath.getPathForPermissionCheck());
/* 379 */       m |= 0x20;
/*     */     }
/*     */     
/* 382 */     if (!hasDesiredAccess(localWindowsPath, m))
/*     */     {
/* 384 */       throw new AccessDeniedException(localWindowsPath.getPathForExceptionMessage(), null, "Permissions does not allow requested access");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 389 */     if (j != 0) {
/*     */       try {
/* 391 */         localObject = WindowsFileAttributes.get(localWindowsPath, true);
/* 392 */         if ((!((WindowsFileAttributes)localObject).isDirectory()) && (((WindowsFileAttributes)localObject).isReadOnly()))
/*     */         {
/* 394 */           throw new AccessDeniedException(localWindowsPath.getPathForExceptionMessage(), null, "DOS readonly attribute is set");
/*     */         }
/*     */       } catch (WindowsException localWindowsException) {
/* 397 */         localWindowsException.rethrowAsIOException(localWindowsPath);
/*     */       }
/*     */       
/* 400 */       if (WindowsFileStore.create(localWindowsPath).isReadOnly())
/*     */       {
/* 402 */         throw new AccessDeniedException(localWindowsPath.getPathForExceptionMessage(), null, "Read-only file system");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean isSameFile(Path paramPath1, Path paramPath2)
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: invokestatic 541	sun/nio/fs/WindowsPath:toWindowsPath	(Ljava/nio/file/Path;)Lsun/nio/fs/WindowsPath;
/*     */     //   4: astore_3
/*     */     //   5: aload_3
/*     */     //   6: aload_2
/*     */     //   7: invokevirtual 531	sun/nio/fs/WindowsPath:equals	(Ljava/lang/Object;)Z
/*     */     //   10: ifeq +5 -> 15
/*     */     //   13: iconst_1
/*     */     //   14: ireturn
/*     */     //   15: aload_2
/*     */     //   16: ifnonnull +11 -> 27
/*     */     //   19: new 247	java/lang/NullPointerException
/*     */     //   22: dup
/*     */     //   23: invokespecial 459	java/lang/NullPointerException:<init>	()V
/*     */     //   26: athrow
/*     */     //   27: aload_2
/*     */     //   28: instanceof 292
/*     */     //   31: ifne +5 -> 36
/*     */     //   34: iconst_0
/*     */     //   35: ireturn
/*     */     //   36: aload_2
/*     */     //   37: checkcast 292	sun/nio/fs/WindowsPath
/*     */     //   40: astore 4
/*     */     //   42: aload_3
/*     */     //   43: invokevirtual 528	sun/nio/fs/WindowsPath:checkRead	()V
/*     */     //   46: aload 4
/*     */     //   48: invokevirtual 528	sun/nio/fs/WindowsPath:checkRead	()V
/*     */     //   51: lconst_0
/*     */     //   52: lstore 5
/*     */     //   54: aload_3
/*     */     //   55: iconst_1
/*     */     //   56: invokevirtual 530	sun/nio/fs/WindowsPath:openForReadAttributeAccess	(Z)J
/*     */     //   59: lstore 5
/*     */     //   61: goto +11 -> 72
/*     */     //   64: astore 7
/*     */     //   66: aload 7
/*     */     //   68: aload_3
/*     */     //   69: invokevirtual 497	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
/*     */     //   72: aconst_null
/*     */     //   73: astore 7
/*     */     //   75: lload 5
/*     */     //   77: invokestatic 505	sun/nio/fs/WindowsFileAttributes:readAttributes	(J)Lsun/nio/fs/WindowsFileAttributes;
/*     */     //   80: astore 7
/*     */     //   82: goto +11 -> 93
/*     */     //   85: astore 8
/*     */     //   87: aload 8
/*     */     //   89: aload_3
/*     */     //   90: invokevirtual 497	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
/*     */     //   93: lconst_0
/*     */     //   94: lstore 8
/*     */     //   96: aload 4
/*     */     //   98: iconst_1
/*     */     //   99: invokevirtual 530	sun/nio/fs/WindowsPath:openForReadAttributeAccess	(Z)J
/*     */     //   102: lstore 8
/*     */     //   104: goto +12 -> 116
/*     */     //   107: astore 10
/*     */     //   109: aload 10
/*     */     //   111: aload 4
/*     */     //   113: invokevirtual 497	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
/*     */     //   116: aconst_null
/*     */     //   117: astore 10
/*     */     //   119: lload 8
/*     */     //   121: invokestatic 505	sun/nio/fs/WindowsFileAttributes:readAttributes	(J)Lsun/nio/fs/WindowsFileAttributes;
/*     */     //   124: astore 10
/*     */     //   126: goto +12 -> 138
/*     */     //   129: astore 11
/*     */     //   131: aload 11
/*     */     //   133: aload 4
/*     */     //   135: invokevirtual 497	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
/*     */     //   138: aload 7
/*     */     //   140: aload 10
/*     */     //   142: invokestatic 506	sun/nio/fs/WindowsFileAttributes:isSameFile	(Lsun/nio/fs/WindowsFileAttributes;Lsun/nio/fs/WindowsFileAttributes;)Z
/*     */     //   145: istore 11
/*     */     //   147: lload 8
/*     */     //   149: invokestatic 521	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
/*     */     //   152: lload 5
/*     */     //   154: invokestatic 521	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
/*     */     //   157: iload 11
/*     */     //   159: ireturn
/*     */     //   160: astore 12
/*     */     //   162: lload 8
/*     */     //   164: invokestatic 521	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
/*     */     //   167: aload 12
/*     */     //   169: athrow
/*     */     //   170: astore 13
/*     */     //   172: lload 5
/*     */     //   174: invokestatic 521	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
/*     */     //   177: aload 13
/*     */     //   179: athrow
/*     */     // Line number table:
/*     */     //   Java source line #409	-> byte code offset #0
/*     */     //   Java source line #410	-> byte code offset #5
/*     */     //   Java source line #411	-> byte code offset #13
/*     */     //   Java source line #412	-> byte code offset #15
/*     */     //   Java source line #413	-> byte code offset #19
/*     */     //   Java source line #414	-> byte code offset #27
/*     */     //   Java source line #415	-> byte code offset #34
/*     */     //   Java source line #416	-> byte code offset #36
/*     */     //   Java source line #419	-> byte code offset #42
/*     */     //   Java source line #420	-> byte code offset #46
/*     */     //   Java source line #423	-> byte code offset #51
/*     */     //   Java source line #425	-> byte code offset #54
/*     */     //   Java source line #428	-> byte code offset #61
/*     */     //   Java source line #426	-> byte code offset #64
/*     */     //   Java source line #427	-> byte code offset #66
/*     */     //   Java source line #430	-> byte code offset #72
/*     */     //   Java source line #432	-> byte code offset #75
/*     */     //   Java source line #435	-> byte code offset #82
/*     */     //   Java source line #433	-> byte code offset #85
/*     */     //   Java source line #434	-> byte code offset #87
/*     */     //   Java source line #436	-> byte code offset #93
/*     */     //   Java source line #438	-> byte code offset #96
/*     */     //   Java source line #441	-> byte code offset #104
/*     */     //   Java source line #439	-> byte code offset #107
/*     */     //   Java source line #440	-> byte code offset #109
/*     */     //   Java source line #443	-> byte code offset #116
/*     */     //   Java source line #445	-> byte code offset #119
/*     */     //   Java source line #448	-> byte code offset #126
/*     */     //   Java source line #446	-> byte code offset #129
/*     */     //   Java source line #447	-> byte code offset #131
/*     */     //   Java source line #449	-> byte code offset #138
/*     */     //   Java source line #451	-> byte code offset #147
/*     */     //   Java source line #454	-> byte code offset #152
/*     */     //   Java source line #449	-> byte code offset #157
/*     */     //   Java source line #451	-> byte code offset #160
/*     */     //   Java source line #452	-> byte code offset #167
/*     */     //   Java source line #454	-> byte code offset #170
/*     */     //   Java source line #455	-> byte code offset #177
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	180	0	this	WindowsFileSystemProvider
/*     */     //   0	180	1	paramPath1	Path
/*     */     //   0	180	2	paramPath2	Path
/*     */     //   4	86	3	localWindowsPath1	WindowsPath
/*     */     //   40	94	4	localWindowsPath2	WindowsPath
/*     */     //   52	121	5	l1	long
/*     */     //   64	3	7	localWindowsException1	WindowsException
/*     */     //   73	66	7	localWindowsFileAttributes1	WindowsFileAttributes
/*     */     //   85	3	8	localWindowsException2	WindowsException
/*     */     //   94	69	8	l2	long
/*     */     //   107	3	10	localWindowsException3	WindowsException
/*     */     //   117	24	10	localWindowsFileAttributes2	WindowsFileAttributes
/*     */     //   129	3	11	localWindowsException4	WindowsException
/*     */     //   145	13	11	bool	boolean
/*     */     //   160	8	12	localObject1	Object
/*     */     //   170	8	13	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   54	61	64	sun/nio/fs/WindowsException
/*     */     //   75	82	85	sun/nio/fs/WindowsException
/*     */     //   96	104	107	sun/nio/fs/WindowsException
/*     */     //   119	126	129	sun/nio/fs/WindowsException
/*     */     //   116	147	160	finally
/*     */     //   160	162	160	finally
/*     */     //   72	152	170	finally
/*     */     //   160	172	170	finally
/*     */   }
/*     */   
/*     */   public boolean isHidden(Path paramPath)
/*     */     throws IOException
/*     */   {
/* 460 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/* 461 */     localWindowsPath.checkRead();
/* 462 */     WindowsFileAttributes localWindowsFileAttributes = null;
/*     */     try {
/* 464 */       localWindowsFileAttributes = WindowsFileAttributes.get(localWindowsPath, true);
/*     */     } catch (WindowsException localWindowsException) {
/* 466 */       localWindowsException.rethrowAsIOException(localWindowsPath);
/*     */     }
/*     */     
/* 469 */     if (localWindowsFileAttributes.isDirectory())
/* 470 */       return false;
/* 471 */     return localWindowsFileAttributes.isHidden();
/*     */   }
/*     */   
/*     */   public FileStore getFileStore(Path paramPath) throws IOException
/*     */   {
/* 476 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/* 477 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 478 */     if (localSecurityManager != null) {
/* 479 */       localSecurityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
/* 480 */       localWindowsPath.checkRead();
/*     */     }
/* 482 */     return WindowsFileStore.create(localWindowsPath);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void createDirectory(Path paramPath, FileAttribute<?>... paramVarArgs)
/*     */     throws IOException
/*     */   {
/* 490 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/* 491 */     localWindowsPath.checkWrite();
/* 492 */     WindowsSecurityDescriptor localWindowsSecurityDescriptor = WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
/*     */     try {
/* 494 */       WindowsNativeDispatcher.CreateDirectory(localWindowsPath.getPathForWin32Calls(), localWindowsSecurityDescriptor.address());
/*     */     }
/*     */     catch (WindowsException localWindowsException1)
/*     */     {
/* 498 */       if (localWindowsException1.lastError() == 5) {
/*     */         try {
/* 500 */           if (WindowsFileAttributes.get(localWindowsPath, false).isDirectory())
/* 501 */             throw new FileAlreadyExistsException(localWindowsPath.toString());
/*     */         } catch (WindowsException localWindowsException2) {}
/*     */       }
/* 504 */       localWindowsException1.rethrowAsIOException(localWindowsPath);
/*     */     } finally {
/* 506 */       localWindowsSecurityDescriptor.release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectoryStream<Path> newDirectoryStream(Path paramPath, Filter<? super Path> paramFilter)
/*     */     throws IOException
/*     */   {
/* 514 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/* 515 */     localWindowsPath.checkRead();
/* 516 */     if (paramFilter == null)
/* 517 */       throw new NullPointerException();
/* 518 */     return new WindowsDirectoryStream(localWindowsPath, paramFilter);
/*     */   }
/*     */   
/*     */ 
/*     */   public void createSymbolicLink(Path paramPath1, Path paramPath2, FileAttribute<?>... paramVarArgs)
/*     */     throws IOException
/*     */   {
/* 525 */     WindowsPath localWindowsPath1 = WindowsPath.toWindowsPath(paramPath1);
/* 526 */     WindowsPath localWindowsPath2 = WindowsPath.toWindowsPath(paramPath2);
/*     */     
/* 528 */     if (!localWindowsPath1.getFileSystem().supportsLinks()) {
/* 529 */       throw new UnsupportedOperationException("Symbolic links not supported on this operating system");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 534 */     if (paramVarArgs.length > 0) {
/* 535 */       WindowsSecurityDescriptor.fromAttribute(paramVarArgs);
/* 536 */       throw new UnsupportedOperationException("Initial file attributesnot supported when creating symbolic link");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 541 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 542 */     if (localSecurityManager != null) {
/* 543 */       localSecurityManager.checkPermission(new LinkPermission("symbolic"));
/* 544 */       localWindowsPath1.checkWrite();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 551 */     if (localWindowsPath2.type() == WindowsPathType.DRIVE_RELATIVE) {
/* 552 */       throw new IOException("Cannot create symbolic link to working directory relative target");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     WindowsPath localWindowsPath3;
/*     */     
/*     */ 
/*     */ 
/* 561 */     if (localWindowsPath2.type() == WindowsPathType.RELATIVE) {
/* 562 */       WindowsPath localWindowsPath4 = localWindowsPath1.getParent();
/* 563 */       localWindowsPath3 = localWindowsPath4 == null ? localWindowsPath2 : localWindowsPath4.resolve(localWindowsPath2);
/*     */     } else {
/* 565 */       localWindowsPath3 = localWindowsPath1.resolve(localWindowsPath2);
/*     */     }
/* 567 */     int i = 0;
/*     */     try {
/* 569 */       WindowsFileAttributes localWindowsFileAttributes = WindowsFileAttributes.get(localWindowsPath3, false);
/* 570 */       if ((localWindowsFileAttributes.isDirectory()) || (localWindowsFileAttributes.isDirectoryLink())) {
/* 571 */         i |= 0x1;
/*     */       }
/*     */     }
/*     */     catch (WindowsException localWindowsException1) {}
/*     */     
/*     */     try
/*     */     {
/* 578 */       WindowsNativeDispatcher.CreateSymbolicLink(localWindowsPath1.getPathForWin32Calls(), 
/* 579 */         WindowsPath.addPrefixIfNeeded(localWindowsPath2.toString()), i);
/*     */     }
/*     */     catch (WindowsException localWindowsException2) {
/* 582 */       if (localWindowsException2.lastError() == 4392) {
/* 583 */         localWindowsException2.rethrowAsIOException(localWindowsPath1, localWindowsPath2);
/*     */       } else {
/* 585 */         localWindowsException2.rethrowAsIOException(localWindowsPath1);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void createLink(Path paramPath1, Path paramPath2) throws IOException
/*     */   {
/* 592 */     WindowsPath localWindowsPath1 = WindowsPath.toWindowsPath(paramPath1);
/* 593 */     WindowsPath localWindowsPath2 = WindowsPath.toWindowsPath(paramPath2);
/*     */     
/*     */ 
/* 596 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 597 */     if (localSecurityManager != null) {
/* 598 */       localSecurityManager.checkPermission(new LinkPermission("hard"));
/* 599 */       localWindowsPath1.checkWrite();
/* 600 */       localWindowsPath2.checkWrite();
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 605 */       WindowsNativeDispatcher.CreateHardLink(localWindowsPath1.getPathForWin32Calls(), localWindowsPath2
/* 606 */         .getPathForWin32Calls());
/*     */     } catch (WindowsException localWindowsException) {
/* 608 */       localWindowsException.rethrowAsIOException(localWindowsPath1, localWindowsPath2);
/*     */     }
/*     */   }
/*     */   
/*     */   public Path readSymbolicLink(Path paramPath) throws IOException
/*     */   {
/* 614 */     WindowsPath localWindowsPath = WindowsPath.toWindowsPath(paramPath);
/* 615 */     WindowsFileSystem localWindowsFileSystem = localWindowsPath.getFileSystem();
/* 616 */     if (!localWindowsFileSystem.supportsLinks()) {
/* 617 */       throw new UnsupportedOperationException("symbolic links not supported");
/*     */     }
/*     */     
/*     */ 
/* 621 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 622 */     if (localSecurityManager != null) {
/* 623 */       localObject = new FilePermission(localWindowsPath.getPathForPermissionCheck(), "readlink");
/*     */       
/* 625 */       localSecurityManager.checkPermission((Permission)localObject);
/*     */     }
/*     */     
/* 628 */     Object localObject = WindowsLinkSupport.readLink(localWindowsPath);
/* 629 */     return WindowsPath.createFromNormalizedPath(localWindowsFileSystem, (String)localObject);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\WindowsFileSystemProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
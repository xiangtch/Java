/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WindowsUserDefinedFileAttributeView
/*     */   extends AbstractUserDefinedFileAttributeView
/*     */ {
/*  46 */   private static final Unsafe unsafe = ;
/*     */   private final WindowsPath file;
/*     */   private final boolean followLinks;
/*     */   
/*  50 */   private String join(String paramString1, String paramString2) { if (paramString2 == null)
/*  51 */       throw new NullPointerException("'name' is null");
/*  52 */     return paramString1 + ":" + paramString2;
/*     */   }
/*     */   
/*  55 */   private String join(WindowsPath paramWindowsPath, String paramString) throws WindowsException { return join(paramWindowsPath.getPathForWin32Calls(), paramString); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   WindowsUserDefinedFileAttributeView(WindowsPath paramWindowsPath, boolean paramBoolean)
/*     */   {
/*  62 */     this.file = paramWindowsPath;
/*  63 */     this.followLinks = paramBoolean;
/*     */   }
/*     */   
/*     */   private List<String> listUsingStreamEnumeration() throws IOException
/*     */   {
/*  68 */     ArrayList localArrayList = new ArrayList();
/*     */     try {
/*  70 */       WindowsNativeDispatcher.FirstStream localFirstStream = WindowsNativeDispatcher.FindFirstStream(this.file.getPathForWin32Calls());
/*  71 */       if (localFirstStream != null) {
/*  72 */         long l = localFirstStream.handle();
/*     */         try
/*     */         {
/*  75 */           String str = localFirstStream.name();
/*  76 */           String[] arrayOfString; if (!str.equals("::$DATA")) {
/*  77 */             arrayOfString = str.split(":");
/*  78 */             localArrayList.add(arrayOfString[1]);
/*     */           }
/*  80 */           while ((str = WindowsNativeDispatcher.FindNextStream(l)) != null) {
/*  81 */             arrayOfString = str.split(":");
/*  82 */             localArrayList.add(arrayOfString[1]);
/*     */           }
/*     */         } finally {
/*  85 */           WindowsNativeDispatcher.FindClose(l);
/*     */         }
/*     */       }
/*     */     } catch (WindowsException localWindowsException) {
/*  89 */       localWindowsException.rethrowAsIOException(this.file);
/*     */     }
/*  91 */     return Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */   
/*     */   private List<String> listUsingBackupRead()
/*     */     throws IOException
/*     */   {
/*  97 */     long l1 = -1L;
/*     */     try {
/*  99 */       int i = 33554432;
/* 100 */       if ((!this.followLinks) && (this.file.getFileSystem().supportsLinks())) {
/* 101 */         i |= 0x200000;
/*     */       }
/* 103 */       l1 = WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), Integer.MIN_VALUE, 1, 3, i);
/*     */ 
/*     */     }
/*     */     catch (WindowsException localWindowsException1)
/*     */     {
/*     */ 
/* 109 */       localWindowsException1.rethrowAsIOException(this.file);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 114 */     NativeBuffer localNativeBuffer = null;
/*     */     
/*     */ 
/* 117 */     ArrayList localArrayList = new ArrayList();
/*     */     try
/*     */     {
/* 120 */       localNativeBuffer = NativeBuffers.getNativeBuffer(4096);
/* 121 */       long l2 = localNativeBuffer.address();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 137 */       long l3 = 0L;
/*     */       try
/*     */       {
/*     */         for (;;) {
/* 141 */           WindowsNativeDispatcher.BackupResult localBackupResult = WindowsNativeDispatcher.BackupRead(l1, l2, 20, false, l3);
/*     */           
/* 143 */           l3 = localBackupResult.context();
/* 144 */           if (localBackupResult.bytesTransferred() == 0) {
/*     */             break;
/*     */           }
/* 147 */           int j = unsafe.getInt(l2 + 0L);
/* 148 */           long l4 = unsafe.getLong(l2 + 8L);
/* 149 */           int k = unsafe.getInt(l2 + 16L);
/*     */           
/*     */ 
/* 152 */           if (k > 0) {
/* 153 */             localBackupResult = WindowsNativeDispatcher.BackupRead(l1, l2, k, false, l3);
/* 154 */             if (localBackupResult.bytesTransferred() != k) {
/*     */               break;
/*     */             }
/*     */           }
/*     */           
/* 159 */           if (j == 4) {
/* 160 */             char[] arrayOfChar = new char[k / 2];
/* 161 */             unsafe.copyMemory(null, l2, arrayOfChar, Unsafe.ARRAY_CHAR_BASE_OFFSET, k);
/*     */             
/*     */ 
/* 164 */             String[] arrayOfString = new String(arrayOfChar).split(":");
/* 165 */             if (arrayOfString.length == 3) {
/* 166 */               localArrayList.add(arrayOfString[1]);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 171 */           if (j == 9) {
/* 172 */             throw new IOException("Spare blocks not handled");
/*     */           }
/*     */           
/*     */ 
/* 176 */           if (l4 > 0L) {
/* 177 */             WindowsNativeDispatcher.BackupSeek(l1, l4, l3);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 185 */         if (l3 != 0L) {
/*     */           try {
/* 187 */             WindowsNativeDispatcher.BackupRead(l1, 0L, 0, true, l3);
/*     */           }
/*     */           catch (WindowsException localWindowsException2) {}
/*     */         }
/*     */         
/* 192 */         if (localNativeBuffer == null) {
/*     */           break label371;
/*     */         }
/*     */       }
/*     */       catch (WindowsException localWindowsException3)
/*     */       {
/* 182 */         throw new IOException(localWindowsException3.errorString());
/*     */       }
/*     */       finally {
/* 185 */         if (l3 != 0L) {
/*     */           try {
/* 187 */             WindowsNativeDispatcher.BackupRead(l1, 0L, 0, true, l3);
/*     */           }
/*     */           catch (WindowsException localWindowsException4) {}
/*     */         }
/*     */       }
/*     */       
/* 193 */       localNativeBuffer.release();
/* 194 */       label371: WindowsNativeDispatcher.CloseHandle(l1);
/*     */     }
/*     */     finally
/*     */     {
/* 192 */       if (localNativeBuffer != null)
/* 193 */         localNativeBuffer.release();
/* 194 */       WindowsNativeDispatcher.CloseHandle(l1);
/*     */     }
/* 196 */     return Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */   
/*     */   public List<String> list() throws IOException
/*     */   {
/* 201 */     if (System.getSecurityManager() != null) {
/* 202 */       checkAccess(this.file.getPathForPermissionCheck(), true, false);
/*     */     }
/* 204 */     if (this.file.getFileSystem().supportsStreamEnumeration()) {
/* 205 */       return listUsingStreamEnumeration();
/*     */     }
/* 207 */     return listUsingBackupRead();
/*     */   }
/*     */   
/*     */   public int size(String paramString)
/*     */     throws IOException
/*     */   {
/* 213 */     if (System.getSecurityManager() != null) {
/* 214 */       checkAccess(this.file.getPathForPermissionCheck(), true, false);
/*     */     }
/*     */     
/* 217 */     FileChannel localFileChannel = null;
/*     */     try {
/* 219 */       HashSet localHashSet = new HashSet();
/* 220 */       localHashSet.add(StandardOpenOption.READ);
/* 221 */       if (!this.followLinks) {
/* 222 */         localHashSet.add(WindowsChannelFactory.OPEN_REPARSE_POINT);
/*     */       }
/* 224 */       localFileChannel = WindowsChannelFactory.newFileChannel(join(this.file, paramString), null, localHashSet, 0L);
/*     */     } catch (WindowsException localWindowsException) {
/* 226 */       localWindowsException.rethrowAsIOException(join(this.file.getPathForPermissionCheck(), paramString));
/*     */     }
/*     */     try {
/* 229 */       long l = localFileChannel.size();
/* 230 */       if (l > 2147483647L)
/* 231 */         throw new ArithmeticException("Stream too large");
/* 232 */       return (int)l;
/*     */     } finally {
/* 234 */       localFileChannel.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public int read(String paramString, ByteBuffer paramByteBuffer) throws IOException
/*     */   {
/* 240 */     if (System.getSecurityManager() != null) {
/* 241 */       checkAccess(this.file.getPathForPermissionCheck(), true, false);
/*     */     }
/*     */     
/* 244 */     FileChannel localFileChannel = null;
/*     */     try {
/* 246 */       HashSet localHashSet = new HashSet();
/* 247 */       localHashSet.add(StandardOpenOption.READ);
/* 248 */       if (!this.followLinks) {
/* 249 */         localHashSet.add(WindowsChannelFactory.OPEN_REPARSE_POINT);
/*     */       }
/* 251 */       localFileChannel = WindowsChannelFactory.newFileChannel(join(this.file, paramString), null, localHashSet, 0L);
/*     */     } catch (WindowsException localWindowsException) {
/* 253 */       localWindowsException.rethrowAsIOException(join(this.file.getPathForPermissionCheck(), paramString));
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 258 */       if (localFileChannel.size() > paramByteBuffer.remaining())
/* 259 */         throw new IOException("Stream too large");
/* 260 */       int i = 0;
/* 261 */       int j; while (paramByteBuffer.hasRemaining()) {
/* 262 */         j = localFileChannel.read(paramByteBuffer);
/* 263 */         if (j < 0)
/*     */           break;
/* 265 */         i += j;
/*     */       }
/* 267 */       return i;
/*     */     } finally {
/* 269 */       localFileChannel.close();
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int write(String paramString, ByteBuffer paramByteBuffer)
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: invokestatic 262	java/lang/System:getSecurityManager	()Ljava/lang/SecurityManager;
/*     */     //   3: ifnull +16 -> 19
/*     */     //   6: aload_0
/*     */     //   7: aload_0
/*     */     //   8: getfield 252	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
/*     */     //   11: invokevirtual 299	sun/nio/fs/WindowsPath:getPathForPermissionCheck	()Ljava/lang/String;
/*     */     //   14: iconst_0
/*     */     //   15: iconst_1
/*     */     //   16: invokevirtual 302	sun/nio/fs/WindowsUserDefinedFileAttributeView:checkAccess	(Ljava/lang/String;ZZ)V
/*     */     //   19: ldc2_w 122
/*     */     //   22: lstore_3
/*     */     //   23: ldc 3
/*     */     //   25: istore 5
/*     */     //   27: aload_0
/*     */     //   28: getfield 250	sun/nio/fs/WindowsUserDefinedFileAttributeView:followLinks	Z
/*     */     //   31: ifne +10 -> 41
/*     */     //   34: iload 5
/*     */     //   36: ldc 2
/*     */     //   38: ior
/*     */     //   39: istore 5
/*     */     //   41: aload_0
/*     */     //   42: getfield 252	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
/*     */     //   45: invokevirtual 300	sun/nio/fs/WindowsPath:getPathForWin32Calls	()Ljava/lang/String;
/*     */     //   48: ldc 1
/*     */     //   50: bipush 7
/*     */     //   52: iconst_3
/*     */     //   53: iload 5
/*     */     //   55: invokestatic 292	sun/nio/fs/WindowsNativeDispatcher:CreateFile	(Ljava/lang/String;IIII)J
/*     */     //   58: lstore_3
/*     */     //   59: goto +14 -> 73
/*     */     //   62: astore 5
/*     */     //   64: aload 5
/*     */     //   66: aload_0
/*     */     //   67: getfield 252	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
/*     */     //   70: invokevirtual 283	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
/*     */     //   73: new 142	java/util/HashSet
/*     */     //   76: dup
/*     */     //   77: invokespecial 271	java/util/HashSet:<init>	()V
/*     */     //   80: astore 5
/*     */     //   82: aload_0
/*     */     //   83: getfield 250	sun/nio/fs/WindowsUserDefinedFileAttributeView:followLinks	Z
/*     */     //   86: ifne +14 -> 100
/*     */     //   89: aload 5
/*     */     //   91: getstatic 249	sun/nio/fs/WindowsChannelFactory:OPEN_REPARSE_POINT	Ljava/nio/file/OpenOption;
/*     */     //   94: invokeinterface 308 2 0
/*     */     //   99: pop
/*     */     //   100: aload 5
/*     */     //   102: getstatic 244	java/nio/file/StandardOpenOption:CREATE	Ljava/nio/file/StandardOpenOption;
/*     */     //   105: invokeinterface 308 2 0
/*     */     //   110: pop
/*     */     //   111: aload 5
/*     */     //   113: getstatic 247	java/nio/file/StandardOpenOption:WRITE	Ljava/nio/file/StandardOpenOption;
/*     */     //   116: invokeinterface 308 2 0
/*     */     //   121: pop
/*     */     //   122: aload 5
/*     */     //   124: getstatic 246	java/nio/file/StandardOpenOption:TRUNCATE_EXISTING	Ljava/nio/file/StandardOpenOption;
/*     */     //   127: invokeinterface 308 2 0
/*     */     //   132: pop
/*     */     //   133: aconst_null
/*     */     //   134: astore 6
/*     */     //   136: aload_0
/*     */     //   137: aload_0
/*     */     //   138: getfield 252	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
/*     */     //   141: aload_1
/*     */     //   142: invokespecial 306	sun/nio/fs/WindowsUserDefinedFileAttributeView:join	(Lsun/nio/fs/WindowsPath;Ljava/lang/String;)Ljava/lang/String;
/*     */     //   145: aconst_null
/*     */     //   146: aload 5
/*     */     //   148: lconst_0
/*     */     //   149: invokestatic 280	sun/nio/fs/WindowsChannelFactory:newFileChannel	(Ljava/lang/String;Ljava/lang/String;Ljava/util/Set;J)Ljava/nio/channels/FileChannel;
/*     */     //   152: astore 6
/*     */     //   154: goto +22 -> 176
/*     */     //   157: astore 7
/*     */     //   159: aload 7
/*     */     //   161: aload_0
/*     */     //   162: aload_0
/*     */     //   163: getfield 252	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
/*     */     //   166: invokevirtual 299	sun/nio/fs/WindowsPath:getPathForPermissionCheck	()Ljava/lang/String;
/*     */     //   169: aload_1
/*     */     //   170: invokespecial 305	sun/nio/fs/WindowsUserDefinedFileAttributeView:join	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*     */     //   173: invokevirtual 282	sun/nio/fs/WindowsException:rethrowAsIOException	(Ljava/lang/String;)V
/*     */     //   176: aload_2
/*     */     //   177: invokevirtual 263	java/nio/ByteBuffer:remaining	()I
/*     */     //   180: istore 7
/*     */     //   182: aload_2
/*     */     //   183: invokevirtual 264	java/nio/ByteBuffer:hasRemaining	()Z
/*     */     //   186: ifeq +13 -> 199
/*     */     //   189: aload 6
/*     */     //   191: aload_2
/*     */     //   192: invokevirtual 268	java/nio/channels/FileChannel:write	(Ljava/nio/ByteBuffer;)I
/*     */     //   195: pop
/*     */     //   196: goto -14 -> 182
/*     */     //   199: iload 7
/*     */     //   201: istore 8
/*     */     //   203: aload 6
/*     */     //   205: invokevirtual 266	java/nio/channels/FileChannel:close	()V
/*     */     //   208: lload_3
/*     */     //   209: invokestatic 287	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
/*     */     //   212: iload 8
/*     */     //   214: ireturn
/*     */     //   215: astore 9
/*     */     //   217: aload 6
/*     */     //   219: invokevirtual 266	java/nio/channels/FileChannel:close	()V
/*     */     //   222: aload 9
/*     */     //   224: athrow
/*     */     //   225: astore 10
/*     */     //   227: lload_3
/*     */     //   228: invokestatic 287	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
/*     */     //   231: aload 10
/*     */     //   233: athrow
/*     */     // Line number table:
/*     */     //   Java source line #275	-> byte code offset #0
/*     */     //   Java source line #276	-> byte code offset #6
/*     */     //   Java source line #286	-> byte code offset #19
/*     */     //   Java source line #288	-> byte code offset #23
/*     */     //   Java source line #289	-> byte code offset #27
/*     */     //   Java source line #290	-> byte code offset #34
/*     */     //   Java source line #292	-> byte code offset #41
/*     */     //   Java source line #299	-> byte code offset #59
/*     */     //   Java source line #297	-> byte code offset #62
/*     */     //   Java source line #298	-> byte code offset #64
/*     */     //   Java source line #301	-> byte code offset #73
/*     */     //   Java source line #302	-> byte code offset #82
/*     */     //   Java source line #303	-> byte code offset #89
/*     */     //   Java source line #304	-> byte code offset #100
/*     */     //   Java source line #305	-> byte code offset #111
/*     */     //   Java source line #306	-> byte code offset #122
/*     */     //   Java source line #307	-> byte code offset #133
/*     */     //   Java source line #309	-> byte code offset #136
/*     */     //   Java source line #310	-> byte code offset #142
/*     */     //   Java source line #313	-> byte code offset #154
/*     */     //   Java source line #311	-> byte code offset #157
/*     */     //   Java source line #312	-> byte code offset #159
/*     */     //   Java source line #316	-> byte code offset #176
/*     */     //   Java source line #317	-> byte code offset #182
/*     */     //   Java source line #318	-> byte code offset #189
/*     */     //   Java source line #320	-> byte code offset #199
/*     */     //   Java source line #322	-> byte code offset #203
/*     */     //   Java source line #325	-> byte code offset #208
/*     */     //   Java source line #320	-> byte code offset #212
/*     */     //   Java source line #322	-> byte code offset #215
/*     */     //   Java source line #323	-> byte code offset #222
/*     */     //   Java source line #325	-> byte code offset #225
/*     */     //   Java source line #326	-> byte code offset #231
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	234	0	this	WindowsUserDefinedFileAttributeView
/*     */     //   0	234	1	paramString	String
/*     */     //   0	234	2	paramByteBuffer	ByteBuffer
/*     */     //   22	206	3	l	long
/*     */     //   25	29	5	i	int
/*     */     //   62	3	5	localWindowsException1	WindowsException
/*     */     //   80	67	5	localHashSet	HashSet
/*     */     //   134	84	6	localFileChannel	FileChannel
/*     */     //   157	3	7	localWindowsException2	WindowsException
/*     */     //   180	20	7	j	int
/*     */     //   201	12	8	k	int
/*     */     //   215	8	9	localObject1	Object
/*     */     //   225	7	10	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   23	59	62	sun/nio/fs/WindowsException
/*     */     //   136	154	157	sun/nio/fs/WindowsException
/*     */     //   176	203	215	finally
/*     */     //   215	217	215	finally
/*     */     //   73	208	225	finally
/*     */     //   215	227	225	finally
/*     */   }
/*     */   
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 331 */     if (System.getSecurityManager() != null) {
/* 332 */       checkAccess(this.file.getPathForPermissionCheck(), false, true);
/*     */     }
/* 334 */     String str1 = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
/* 335 */     String str2 = join(str1, paramString);
/*     */     try {
/* 337 */       WindowsNativeDispatcher.DeleteFile(str2);
/*     */     } catch (WindowsException localWindowsException) {
/* 339 */       localWindowsException.rethrowAsIOException(str2);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\WindowsUserDefinedFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
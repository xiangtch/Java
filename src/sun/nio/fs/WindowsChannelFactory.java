/*     */ package sun.nio.fs;
/*     */ 
/*     */ import com.sun.nio.file.ExtendedOpenOption;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.AsynchronousFileChannel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.Set;
/*     */ import sun.misc.JavaIOFileDescriptorAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.nio.ch.FileChannelImpl;
/*     */ import sun.nio.ch.ThreadPool;
/*     */ import sun.nio.ch.WindowsAsynchronousFileChannelImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WindowsChannelFactory
/*     */ {
/*  54 */   private static final JavaIOFileDescriptorAccess fdAccess = ;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  62 */   static final OpenOption OPEN_REPARSE_POINT = new OpenOption() {};
/*     */   
/*     */ 
/*     */   private static class Flags
/*     */   {
/*     */     boolean read;
/*     */     
/*     */     boolean write;
/*     */     
/*     */     boolean append;
/*     */     
/*     */     boolean truncateExisting;
/*     */     boolean create;
/*     */     boolean createNew;
/*     */     boolean deleteOnClose;
/*     */     boolean sparse;
/*     */     boolean overlapped;
/*     */     boolean sync;
/*     */     boolean dsync;
/*  81 */     boolean shareRead = true;
/*  82 */     boolean shareWrite = true;
/*  83 */     boolean shareDelete = true;
/*     */     boolean noFollowLinks;
/*     */     boolean openReparsePoint;
/*     */     
/*     */     static Flags toFlags(Set<? extends OpenOption> paramSet) {
/*  88 */       Flags localFlags = new Flags();
/*  89 */       for (OpenOption localOpenOption : paramSet)
/*  90 */         if ((localOpenOption instanceof StandardOpenOption)) {
/*  91 */           switch (WindowsChannelFactory.2.$SwitchMap$java$nio$file$StandardOpenOption[((StandardOpenOption)localOpenOption).ordinal()]) {
/*  92 */           case 1:  localFlags.read = true; break;
/*  93 */           case 2:  localFlags.write = true; break;
/*  94 */           case 3:  localFlags.append = true; break;
/*  95 */           case 4:  localFlags.truncateExisting = true; break;
/*  96 */           case 5:  localFlags.create = true; break;
/*  97 */           case 6:  localFlags.createNew = true; break;
/*  98 */           case 7:  localFlags.deleteOnClose = true; break;
/*  99 */           case 8:  localFlags.sparse = true; break;
/* 100 */           case 9:  localFlags.sync = true; break;
/* 101 */           case 10:  localFlags.dsync = true; break;
/* 102 */           default:  throw new UnsupportedOperationException();
/*     */           }
/*     */           
/*     */         }
/* 106 */         else if ((localOpenOption instanceof ExtendedOpenOption)) {
/* 107 */           switch (WindowsChannelFactory.2.$SwitchMap$com$sun$nio$file$ExtendedOpenOption[((ExtendedOpenOption)localOpenOption).ordinal()]) {
/* 108 */           case 1:  localFlags.shareRead = false; break;
/* 109 */           case 2:  localFlags.shareWrite = false; break;
/* 110 */           case 3:  localFlags.shareDelete = false; break;
/* 111 */           default:  throw new UnsupportedOperationException();
/*     */           }
/*     */           
/*     */         }
/* 115 */         else if (localOpenOption == LinkOption.NOFOLLOW_LINKS) {
/* 116 */           localFlags.noFollowLinks = true;
/*     */ 
/*     */         }
/* 119 */         else if (localOpenOption == WindowsChannelFactory.OPEN_REPARSE_POINT) {
/* 120 */           localFlags.openReparsePoint = true;
/*     */         }
/*     */         else {
/* 123 */           if (localOpenOption == null)
/* 124 */             throw new NullPointerException();
/* 125 */           throw new UnsupportedOperationException();
/*     */         }
/* 127 */       return localFlags;
/*     */     }
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
/*     */   static FileChannel newFileChannel(String paramString1, String paramString2, Set<? extends OpenOption> paramSet, long paramLong)
/*     */     throws WindowsException
/*     */   {
/* 145 */     Flags localFlags = Flags.toFlags(paramSet);
/*     */     
/*     */ 
/* 148 */     if ((!localFlags.read) && (!localFlags.write)) {
/* 149 */       if (localFlags.append) {
/* 150 */         localFlags.write = true;
/*     */       } else {
/* 152 */         localFlags.read = true;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 157 */     if ((localFlags.read) && (localFlags.append))
/* 158 */       throw new IllegalArgumentException("READ + APPEND not allowed");
/* 159 */     if ((localFlags.append) && (localFlags.truncateExisting)) {
/* 160 */       throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
/*     */     }
/* 162 */     FileDescriptor localFileDescriptor = open(paramString1, paramString2, localFlags, paramLong);
/* 163 */     return FileChannelImpl.open(localFileDescriptor, paramString1, localFlags.read, localFlags.write, localFlags.append, null);
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
/*     */   static AsynchronousFileChannel newAsynchronousFileChannel(String paramString1, String paramString2, Set<? extends OpenOption> paramSet, long paramLong, ThreadPool paramThreadPool)
/*     */     throws IOException
/*     */   {
/* 183 */     Flags localFlags = Flags.toFlags(paramSet);
/*     */     
/*     */ 
/* 186 */     localFlags.overlapped = true;
/*     */     
/*     */ 
/* 189 */     if ((!localFlags.read) && (!localFlags.write)) {
/* 190 */       localFlags.read = true;
/*     */     }
/*     */     
/*     */ 
/* 194 */     if (localFlags.append) {
/* 195 */       throw new UnsupportedOperationException("APPEND not allowed");
/*     */     }
/*     */     FileDescriptor localFileDescriptor;
/*     */     try
/*     */     {
/* 200 */       localFileDescriptor = open(paramString1, paramString2, localFlags, paramLong);
/*     */     } catch (WindowsException localWindowsException) {
/* 202 */       localWindowsException.rethrowAsIOException(paramString1);
/* 203 */       return null;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 208 */       return WindowsAsynchronousFileChannelImpl.open(localFileDescriptor, localFlags.read, localFlags.write, paramThreadPool);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 212 */       long l = fdAccess.getHandle(localFileDescriptor);
/* 213 */       WindowsNativeDispatcher.CloseHandle(l);
/* 214 */       throw localIOException;
/*     */     }
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
/*     */   private static FileDescriptor open(String paramString1, String paramString2, Flags paramFlags, long paramLong)
/*     */     throws WindowsException
/*     */   {
/* 229 */     int i = 0;
/*     */     
/*     */ 
/* 232 */     int j = 0;
/* 233 */     if (paramFlags.read)
/* 234 */       j |= 0x80000000;
/* 235 */     if (paramFlags.write) {
/* 236 */       j |= 0x40000000;
/*     */     }
/* 238 */     int k = 0;
/* 239 */     if (paramFlags.shareRead)
/* 240 */       k |= 0x1;
/* 241 */     if (paramFlags.shareWrite)
/* 242 */       k |= 0x2;
/* 243 */     if (paramFlags.shareDelete) {
/* 244 */       k |= 0x4;
/*     */     }
/* 246 */     int m = 128;
/* 247 */     int n = 3;
/* 248 */     if (paramFlags.write) {
/* 249 */       if (paramFlags.createNew) {
/* 250 */         n = 1;
/*     */         
/* 252 */         m |= 0x200000;
/*     */       } else {
/* 254 */         if (paramFlags.create)
/* 255 */           n = 4;
/* 256 */         if (paramFlags.truncateExisting)
/*     */         {
/*     */ 
/*     */ 
/* 260 */           if (n == 4) {
/* 261 */             i = 1;
/*     */           } else {
/* 263 */             n = 5;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 269 */     if ((paramFlags.dsync) || (paramFlags.sync))
/* 270 */       m |= 0x80000000;
/* 271 */     if (paramFlags.overlapped)
/* 272 */       m |= 0x40000000;
/* 273 */     if (paramFlags.deleteOnClose) {
/* 274 */       m |= 0x4000000;
/*     */     }
/*     */     
/* 277 */     int i1 = 1;
/* 278 */     if ((n != 1) && ((paramFlags.noFollowLinks) || (paramFlags.openReparsePoint) || (paramFlags.deleteOnClose)))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 283 */       if ((paramFlags.noFollowLinks) || (paramFlags.deleteOnClose))
/* 284 */         i1 = 0;
/* 285 */       m |= 0x200000;
/*     */     }
/*     */     
/*     */ 
/* 289 */     if (paramString2 != null) {
/* 290 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 291 */       if (localSecurityManager != null) {
/* 292 */         if (paramFlags.read)
/* 293 */           localSecurityManager.checkRead(paramString2);
/* 294 */         if (paramFlags.write)
/* 295 */           localSecurityManager.checkWrite(paramString2);
/* 296 */         if (paramFlags.deleteOnClose) {
/* 297 */           localSecurityManager.checkDelete(paramString2);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 302 */     long l = WindowsNativeDispatcher.CreateFile(paramString1, j, k, paramLong, n, m);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 310 */     if (i1 == 0) {
/*     */       try {
/* 312 */         if (WindowsFileAttributes.readAttributes(l).isSymbolicLink())
/* 313 */           throw new WindowsException("File is symbolic link");
/*     */       } catch (WindowsException localWindowsException1) {
/* 315 */         WindowsNativeDispatcher.CloseHandle(l);
/* 316 */         throw localWindowsException1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 321 */     if (i != 0) {
/*     */       try {
/* 323 */         WindowsNativeDispatcher.SetEndOfFile(l);
/*     */       } catch (WindowsException localWindowsException2) {
/* 325 */         WindowsNativeDispatcher.CloseHandle(l);
/* 326 */         throw localWindowsException2;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 331 */     if ((n == 1) && (paramFlags.sparse)) {
/*     */       try {
/* 333 */         WindowsNativeDispatcher.DeviceIoControlSetSparse(l);
/*     */       }
/*     */       catch (WindowsException localWindowsException3) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 340 */     FileDescriptor localFileDescriptor = new FileDescriptor();
/* 341 */     fdAccess.setHandle(localFileDescriptor, l);
/* 342 */     return localFileDescriptor;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\WindowsChannelFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
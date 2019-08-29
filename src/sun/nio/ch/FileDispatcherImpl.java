/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.misc.JavaIOFileDescriptorAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class FileDispatcherImpl
/*     */   extends FileDispatcher
/*     */ {
/*     */   FileDispatcherImpl(boolean paramBoolean)
/*     */   {
/*  46 */     this.append = paramBoolean;
/*     */   }
/*     */   
/*     */   FileDispatcherImpl() {
/*  50 */     this(false);
/*     */   }
/*     */   
/*     */   boolean needsPositionLock()
/*     */   {
/*  55 */     return true;
/*     */   }
/*     */   
/*     */   int read(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*     */     throws IOException
/*     */   {
/*  61 */     return read0(paramFileDescriptor, paramLong, paramInt);
/*     */   }
/*     */   
/*     */   int pread(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2)
/*     */     throws IOException
/*     */   {
/*  67 */     return pread0(paramFileDescriptor, paramLong1, paramInt, paramLong2);
/*     */   }
/*     */   
/*     */   long readv(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
/*  71 */     return readv0(paramFileDescriptor, paramLong, paramInt);
/*     */   }
/*     */   
/*     */   int write(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
/*  75 */     return write0(paramFileDescriptor, paramLong, paramInt, this.append);
/*     */   }
/*     */   
/*     */   int pwrite(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2)
/*     */     throws IOException
/*     */   {
/*  81 */     return pwrite0(paramFileDescriptor, paramLong1, paramInt, paramLong2);
/*     */   }
/*     */   
/*     */   long writev(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
/*  85 */     return writev0(paramFileDescriptor, paramLong, paramInt, this.append);
/*     */   }
/*     */   
/*     */   int force(FileDescriptor paramFileDescriptor, boolean paramBoolean) throws IOException {
/*  89 */     return force0(paramFileDescriptor, paramBoolean);
/*     */   }
/*     */   
/*     */   int truncate(FileDescriptor paramFileDescriptor, long paramLong) throws IOException {
/*  93 */     return truncate0(paramFileDescriptor, paramLong);
/*     */   }
/*     */   
/*     */   long size(FileDescriptor paramFileDescriptor) throws IOException {
/*  97 */     return size0(paramFileDescriptor);
/*     */   }
/*     */   
/*     */   int lock(FileDescriptor paramFileDescriptor, boolean paramBoolean1, long paramLong1, long paramLong2, boolean paramBoolean2)
/*     */     throws IOException
/*     */   {
/* 103 */     return lock0(paramFileDescriptor, paramBoolean1, paramLong1, paramLong2, paramBoolean2);
/*     */   }
/*     */   
/*     */   void release(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2) throws IOException {
/* 107 */     release0(paramFileDescriptor, paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   void close(FileDescriptor paramFileDescriptor) throws IOException {
/* 111 */     close0(paramFileDescriptor);
/*     */   }
/*     */   
/*     */   FileDescriptor duplicateForMapping(FileDescriptor paramFileDescriptor)
/*     */     throws IOException
/*     */   {
/* 117 */     JavaIOFileDescriptorAccess localJavaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
/* 118 */     FileDescriptor localFileDescriptor = new FileDescriptor();
/* 119 */     long l = duplicateHandle(localJavaIOFileDescriptorAccess.getHandle(paramFileDescriptor));
/* 120 */     localJavaIOFileDescriptorAccess.setHandle(localFileDescriptor, l);
/* 121 */     return localFileDescriptor;
/*     */   }
/*     */   
/*     */   boolean canTransferToDirectly(SelectableChannel paramSelectableChannel) {
/* 125 */     return (fastFileTransfer) && (paramSelectableChannel.isBlocking());
/*     */   }
/*     */   
/*     */   boolean transferToDirectlyNeedsPositionLock() {
/* 129 */     return true;
/*     */   }
/*     */   
/*     */   static boolean isFastFileTransferRequested() {
/* 133 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run()
/*     */       {
/* 137 */         return System.getProperty("jdk.nio.enableFastFileTransfer");
/*     */       }
/*     */     });
/*     */     boolean bool;
/* 141 */     if ("".equals(str)) {
/* 142 */       bool = true;
/*     */     } else {
/* 144 */       bool = Boolean.parseBoolean(str);
/*     */     }
/* 146 */     return bool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 151 */   private static final boolean fastFileTransfer = isFastFileTransferRequested();
/*     */   private final boolean append;
/*     */   
/*     */   static native int read0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */   static native int pread0(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2)
/*     */     throws IOException;
/*     */   
/*     */   static native long readv0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */   static native int write0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt, boolean paramBoolean)
/*     */     throws IOException;
/*     */   
/*     */   static native int pwrite0(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2)
/*     */     throws IOException;
/*     */   
/*     */   static native long writev0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt, boolean paramBoolean)
/*     */     throws IOException;
/*     */   
/*     */   static native int force0(FileDescriptor paramFileDescriptor, boolean paramBoolean)
/*     */     throws IOException;
/*     */   
/*     */   static native int truncate0(FileDescriptor paramFileDescriptor, long paramLong)
/*     */     throws IOException;
/*     */   
/*     */   static native long size0(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */   
/*     */   static native int lock0(FileDescriptor paramFileDescriptor, boolean paramBoolean1, long paramLong1, long paramLong2, boolean paramBoolean2)
/*     */     throws IOException;
/*     */   
/*     */   static native void release0(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2)
/*     */     throws IOException;
/*     */   
/*     */   static native void close0(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */   
/*     */   static native void closeByHandle(long paramLong)
/*     */     throws IOException;
/*     */   
/*     */   static native long duplicateHandle(long paramLong)
/*     */     throws IOException;
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\FileDispatcherImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
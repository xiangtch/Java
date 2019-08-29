/*      */ package sun.nio.ch;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.MappedByteBuffer;
/*      */ import java.nio.channels.ClosedByInterruptException;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.FileChannel.MapMode;
/*      */ import java.nio.channels.FileLock;
/*      */ import java.nio.channels.FileLockInterruptionException;
/*      */ import java.nio.channels.NonReadableChannelException;
/*      */ import java.nio.channels.NonWritableChannelException;
/*      */ import java.nio.channels.OverlappingFileLockException;
/*      */ import java.nio.channels.ReadableByteChannel;
/*      */ import java.nio.channels.SelectableChannel;
/*      */ import java.nio.channels.WritableByteChannel;
/*      */ import java.security.AccessController;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import sun.misc.Cleaner;
/*      */ import sun.misc.JavaNioAccess.BufferPool;
/*      */ import sun.security.action.GetPropertyAction;
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
/*      */ public class FileChannelImpl
/*      */   extends FileChannel
/*      */ {
/*   75 */   private final NativeThreadSet threads = new NativeThreadSet(2);
/*      */   
/*      */ 
/*   78 */   private final Object positionLock = new Object();
/*      */   
/*      */ 
/*      */   private FileChannelImpl(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject)
/*      */   {
/*   83 */     this.fd = paramFileDescriptor;
/*   84 */     this.readable = paramBoolean1;
/*   85 */     this.writable = paramBoolean2;
/*   86 */     this.append = paramBoolean3;
/*   87 */     this.parent = paramObject;
/*   88 */     this.path = paramString;
/*   89 */     this.nd = new FileDispatcherImpl(paramBoolean3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, Object paramObject)
/*      */   {
/*   97 */     return new FileChannelImpl(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, false, paramObject);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject)
/*      */   {
/*  105 */     return new FileChannelImpl(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, paramBoolean3, paramObject);
/*      */   }
/*      */   
/*      */   private void ensureOpen() throws IOException {
/*  109 */     if (!isOpen()) {
/*  110 */       throw new ClosedChannelException();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void implCloseChannel()
/*      */     throws IOException
/*      */   {
/*  118 */     if (this.fileLockTable != null) {
/*  119 */       for (FileLock localFileLock : this.fileLockTable.removeAll()) {
/*  120 */         synchronized (localFileLock) {
/*  121 */           if (localFileLock.isValid()) {
/*  122 */             this.nd.release(this.fd, localFileLock.position(), localFileLock.size());
/*  123 */             ((FileLockImpl)localFileLock).invalidate();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  130 */     this.threads.signalAndWait();
/*      */     
/*  132 */     if (this.parent != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  139 */       ((Closeable)this.parent).close();
/*      */     } else {
/*  141 */       this.nd.close(this.fd);
/*      */     }
/*      */   }
/*      */   
/*      */   public int read(ByteBuffer paramByteBuffer) throws IOException
/*      */   {
/*  147 */     ensureOpen();
/*  148 */     if (!this.readable)
/*  149 */       throw new NonReadableChannelException();
/*  150 */     synchronized (this.positionLock) {
/*  151 */       int i = 0;
/*  152 */       int j = -1;
/*      */       try {
/*  154 */         begin();
/*  155 */         j = this.threads.add();
/*  156 */         if (!isOpen()) {
/*  157 */           k = 0;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  163 */           this.threads.remove(j);
/*  164 */           end(i > 0);
/*  165 */           assert (IOStatus.check(i));return k;
/*      */         }
/*  159 */         do { i = IOUtil.read(this.fd, paramByteBuffer, -1L, this.nd);
/*  160 */         } while ((i == -3) && (isOpen()));
/*  161 */         int k = IOStatus.normalize(i);
/*      */         
/*  163 */         this.threads.remove(j);
/*  164 */         end(i > 0);
/*  165 */         assert (IOStatus.check(i));return k;
/*      */       } finally {
/*  163 */         this.threads.remove(j);
/*  164 */         end(i > 0);
/*  165 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  173 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  174 */       throw new IndexOutOfBoundsException();
/*  175 */     ensureOpen();
/*  176 */     if (!this.readable)
/*  177 */       throw new NonReadableChannelException();
/*  178 */     synchronized (this.positionLock) {
/*  179 */       long l1 = 0L;
/*  180 */       int i = -1;
/*      */       try {
/*  182 */         begin();
/*  183 */         i = this.threads.add();
/*  184 */         if (!isOpen()) {
/*  185 */           l2 = 0L;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  191 */           this.threads.remove(i);
/*  192 */           end(l1 > 0L);
/*  193 */           assert (IOStatus.check(l1));return l2;
/*      */         }
/*  187 */         do { l1 = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, this.nd);
/*  188 */         } while ((l1 == -3L) && (isOpen()));
/*  189 */         long l2 = IOStatus.normalize(l1);
/*      */         
/*  191 */         this.threads.remove(i);
/*  192 */         end(l1 > 0L);
/*  193 */         assert (IOStatus.check(l1));return l2;
/*      */       } finally {
/*  191 */         this.threads.remove(i);
/*  192 */         end(l1 > 0L);
/*  193 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/*  199 */     ensureOpen();
/*  200 */     if (!this.writable)
/*  201 */       throw new NonWritableChannelException();
/*  202 */     synchronized (this.positionLock) {
/*  203 */       int i = 0;
/*  204 */       int j = -1;
/*      */       try {
/*  206 */         begin();
/*  207 */         j = this.threads.add();
/*  208 */         if (!isOpen()) {
/*  209 */           k = 0;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  215 */           this.threads.remove(j);
/*  216 */           end(i > 0);
/*  217 */           assert (IOStatus.check(i));return k;
/*      */         }
/*  211 */         do { i = IOUtil.write(this.fd, paramByteBuffer, -1L, this.nd);
/*  212 */         } while ((i == -3) && (isOpen()));
/*  213 */         int k = IOStatus.normalize(i);
/*      */         
/*  215 */         this.threads.remove(j);
/*  216 */         end(i > 0);
/*  217 */         assert (IOStatus.check(i));return k;
/*      */       } finally {
/*  215 */         this.threads.remove(j);
/*  216 */         end(i > 0);
/*  217 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  225 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  226 */       throw new IndexOutOfBoundsException();
/*  227 */     ensureOpen();
/*  228 */     if (!this.writable)
/*  229 */       throw new NonWritableChannelException();
/*  230 */     synchronized (this.positionLock) {
/*  231 */       long l1 = 0L;
/*  232 */       int i = -1;
/*      */       try {
/*  234 */         begin();
/*  235 */         i = this.threads.add();
/*  236 */         if (!isOpen()) {
/*  237 */           l2 = 0L;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  243 */           this.threads.remove(i);
/*  244 */           end(l1 > 0L);
/*  245 */           assert (IOStatus.check(l1));return l2;
/*      */         }
/*  239 */         do { l1 = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, this.nd);
/*  240 */         } while ((l1 == -3L) && (isOpen()));
/*  241 */         long l2 = IOStatus.normalize(l1);
/*      */         
/*  243 */         this.threads.remove(i);
/*  244 */         end(l1 > 0L);
/*  245 */         assert (IOStatus.check(l1));return l2;
/*      */       } finally {
/*  243 */         this.threads.remove(i);
/*  244 */         end(l1 > 0L);
/*  245 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public long position()
/*      */     throws IOException
/*      */   {
/*  253 */     ensureOpen();
/*  254 */     synchronized (this.positionLock) {
/*  255 */       long l1 = -1L;
/*  256 */       int i = -1;
/*      */       try {
/*  258 */         begin();
/*  259 */         i = this.threads.add();
/*  260 */         if (!isOpen()) {
/*  261 */           l2 = 0L;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  268 */           this.threads.remove(i);
/*  269 */           end(l1 > -1L);
/*  270 */           assert (IOStatus.check(l1));return l2;
/*      */         }
/*      */         do {
/*  264 */           l1 = this.append ? this.nd.size(this.fd) : position0(this.fd, -1L);
/*  265 */         } while ((l1 == -3L) && (isOpen()));
/*  266 */         long l2 = IOStatus.normalize(l1);
/*      */         
/*  268 */         this.threads.remove(i);
/*  269 */         end(l1 > -1L);
/*  270 */         assert (IOStatus.check(l1));return l2;
/*      */       } finally {
/*  268 */         this.threads.remove(i);
/*  269 */         end(l1 > -1L);
/*  270 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public FileChannel position(long paramLong) throws IOException {
/*  276 */     ensureOpen();
/*  277 */     if (paramLong < 0L)
/*  278 */       throw new IllegalArgumentException();
/*  279 */     synchronized (this.positionLock) {
/*  280 */       long l = -1L;
/*  281 */       int i = -1;
/*      */       try {
/*  283 */         begin();
/*  284 */         i = this.threads.add();
/*  285 */         if (!isOpen()) {
/*  286 */           localObject1 = null;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  292 */           this.threads.remove(i);
/*  293 */           end(l > -1L);
/*  294 */           assert (IOStatus.check(l));return (FileChannel)localObject1;
/*      */         }
/*  288 */         do { l = position0(this.fd, paramLong);
/*  289 */         } while ((l == -3L) && (isOpen()));
/*  290 */         Object localObject1 = this;
/*      */         
/*  292 */         this.threads.remove(i);
/*  293 */         end(l > -1L);
/*  294 */         assert (IOStatus.check(l));return (FileChannel)localObject1;
/*      */       } finally {
/*  292 */         this.threads.remove(i);
/*  293 */         end(l > -1L);
/*  294 */         if ((!$assertionsDisabled) && (!IOStatus.check(l))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public long size() throws IOException {
/*  300 */     ensureOpen();
/*  301 */     synchronized (this.positionLock) {
/*  302 */       long l1 = -1L;
/*  303 */       int i = -1;
/*      */       try {
/*  305 */         begin();
/*  306 */         i = this.threads.add();
/*  307 */         if (!isOpen()) {
/*  308 */           l2 = -1L;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  314 */           this.threads.remove(i);
/*  315 */           end(l1 > -1L);
/*  316 */           assert (IOStatus.check(l1));return l2;
/*      */         }
/*  310 */         do { l1 = this.nd.size(this.fd);
/*  311 */         } while ((l1 == -3L) && (isOpen()));
/*  312 */         long l2 = IOStatus.normalize(l1);
/*      */         
/*  314 */         this.threads.remove(i);
/*  315 */         end(l1 > -1L);
/*  316 */         assert (IOStatus.check(l1));return l2;
/*      */       } finally {
/*  314 */         this.threads.remove(i);
/*  315 */         end(l1 > -1L);
/*  316 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public FileChannel truncate(long paramLong) throws IOException {
/*  322 */     ensureOpen();
/*  323 */     if (paramLong < 0L)
/*  324 */       throw new IllegalArgumentException("Negative size");
/*  325 */     if (!this.writable)
/*  326 */       throw new NonWritableChannelException();
/*  327 */     synchronized (this.positionLock) {
/*  328 */       int i = -1;
/*  329 */       long l1 = -1L;
/*  330 */       int j = -1;
/*  331 */       long l2 = -1L;
/*      */       try {
/*  333 */         begin();
/*  334 */         j = this.threads.add();
/*  335 */         if (!isOpen()) {
/*  336 */           FileChannel localFileChannel = null;
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
/*  371 */           this.threads.remove(j);
/*  372 */           end(i > -1);
/*  373 */           assert (IOStatus.check(i));return localFileChannel;
/*      */         }
/*      */         long l3;
/*      */         do
/*      */         {
/*  341 */           l3 = this.nd.size(this.fd);
/*  342 */         } while ((l3 == -3L) && (isOpen()));
/*  343 */         if (!isOpen()) {
/*  344 */           localObject1 = null;
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
/*  371 */           this.threads.remove(j);
/*  372 */           end(i > -1);
/*  373 */           assert (IOStatus.check(i));return (FileChannel)localObject1;
/*      */         }
/*      */         do
/*      */         {
/*  348 */           l1 = position0(this.fd, -1L);
/*  349 */         } while ((l1 == -3L) && (isOpen()));
/*  350 */         if (!isOpen()) {
/*  351 */           localObject1 = null;
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
/*  371 */           this.threads.remove(j);
/*  372 */           end(i > -1);
/*  373 */           assert (IOStatus.check(i));return (FileChannel)localObject1; }
/*  352 */         assert (l1 >= 0L);
/*      */         
/*      */ 
/*  355 */         if (paramLong < l3) {
/*      */           do {
/*  357 */             i = this.nd.truncate(this.fd, paramLong);
/*  358 */           } while ((i == -3) && (isOpen()));
/*  359 */           if (!isOpen()) {
/*  360 */             localObject1 = null;
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
/*  371 */             this.threads.remove(j);
/*  372 */             end(i > -1);
/*  373 */             assert (IOStatus.check(i));return (FileChannel)localObject1;
/*      */           }
/*      */         }
/*      */         
/*  364 */         if (l1 > paramLong)
/*  365 */           l1 = paramLong;
/*      */         do {
/*  367 */           l2 = position0(this.fd, l1);
/*  368 */         } while ((l2 == -3L) && (isOpen()));
/*  369 */         Object localObject1 = this;
/*      */         
/*  371 */         this.threads.remove(j);
/*  372 */         end(i > -1);
/*  373 */         assert (IOStatus.check(i));return (FileChannel)localObject1;
/*      */       } finally {
/*  371 */         this.threads.remove(j);
/*  372 */         end(i > -1);
/*  373 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void force(boolean paramBoolean) throws IOException {
/*  379 */     ensureOpen();
/*  380 */     int i = -1;
/*  381 */     int j = -1;
/*      */     try {
/*  383 */       begin();
/*  384 */       j = this.threads.add();
/*  385 */       if (!isOpen())
/*      */         return;
/*      */       do {
/*  388 */         i = this.nd.force(this.fd, paramBoolean);
/*  389 */         if (i != -3) break; } while (isOpen());
/*      */     } finally {
/*  391 */       this.threads.remove(j);
/*  392 */       end(i > -1);
/*  393 */       if ((!$assertionsDisabled) && (!IOStatus.check(i))) { throw new AssertionError();
/*      */       }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long transferToDirectlyInternal(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel, FileDescriptor paramFileDescriptor)
/*      */     throws IOException
/*      */   {
/*  417 */     assert ((!this.nd.transferToDirectlyNeedsPositionLock()) || 
/*  418 */       (Thread.holdsLock(this.positionLock)));
/*      */     
/*  420 */     long l1 = -1L;
/*  421 */     int i = -1;
/*      */     try {
/*  423 */       begin();
/*  424 */       i = this.threads.add();
/*  425 */       long l2; if (!isOpen())
/*  426 */         return -1L;
/*      */       do {
/*  428 */         l1 = transferTo0(this.fd, paramLong, paramInt, paramFileDescriptor);
/*  429 */       } while ((l1 == -3L) && (isOpen()));
/*  430 */       if (l1 == -6L) {
/*  431 */         if ((paramWritableByteChannel instanceof SinkChannelImpl))
/*  432 */           pipeSupported = false;
/*  433 */         if ((paramWritableByteChannel instanceof FileChannelImpl))
/*  434 */           fileSupported = false;
/*  435 */         return -6L;
/*      */       }
/*  437 */       if (l1 == -4L)
/*      */       {
/*  439 */         transferSupported = false;
/*  440 */         return -4L;
/*      */       }
/*  442 */       return IOStatus.normalize(l1);
/*      */     } finally {
/*  444 */       this.threads.remove(i);
/*  445 */       end(l1 > -1L);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private long transferToDirectly(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel)
/*      */     throws IOException
/*      */   {
/*  453 */     if (!transferSupported) {
/*  454 */       return -4L;
/*      */     }
/*  456 */     FileDescriptor localFileDescriptor = null;
/*  457 */     if ((paramWritableByteChannel instanceof FileChannelImpl)) {
/*  458 */       if (!fileSupported)
/*  459 */         return -6L;
/*  460 */       localFileDescriptor = ((FileChannelImpl)paramWritableByteChannel).fd;
/*  461 */     } else if ((paramWritableByteChannel instanceof SelChImpl))
/*      */     {
/*  463 */       if (((paramWritableByteChannel instanceof SinkChannelImpl)) && (!pipeSupported)) {
/*  464 */         return -6L;
/*      */       }
/*      */       
/*      */ 
/*  468 */       SelectableChannel localSelectableChannel = (SelectableChannel)paramWritableByteChannel;
/*  469 */       if (!this.nd.canTransferToDirectly(localSelectableChannel)) {
/*  470 */         return -6L;
/*      */       }
/*  472 */       localFileDescriptor = ((SelChImpl)paramWritableByteChannel).getFD();
/*      */     }
/*      */     
/*  475 */     if (localFileDescriptor == null)
/*  476 */       return -4L;
/*  477 */     int i = IOUtil.fdVal(this.fd);
/*  478 */     int j = IOUtil.fdVal(localFileDescriptor);
/*  479 */     if (i == j) {
/*  480 */       return -4L;
/*      */     }
/*  482 */     if (this.nd.transferToDirectlyNeedsPositionLock()) {
/*  483 */       synchronized (this.positionLock) {
/*  484 */         long l1 = position();
/*      */         try {
/*  486 */           long l2 = transferToDirectlyInternal(paramLong, paramInt, paramWritableByteChannel, localFileDescriptor);
/*      */           
/*      */ 
/*  489 */           position(l1);return l2;
/*      */         }
/*      */         finally {
/*  489 */           position(l1);
/*      */         }
/*      */       }
/*      */     }
/*  493 */     return transferToDirectlyInternal(paramLong, paramInt, paramWritableByteChannel, localFileDescriptor);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long transferToTrustedChannel(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
/*      */     throws IOException
/*      */   {
/*  504 */     boolean bool = paramWritableByteChannel instanceof SelChImpl;
/*  505 */     if ((!(paramWritableByteChannel instanceof FileChannelImpl)) && (!bool)) {
/*  506 */       return -4L;
/*      */     }
/*      */     
/*  509 */     long l1 = paramLong2;
/*  510 */     while (l1 > 0L) {
/*  511 */       long l2 = Math.min(l1, 8388608L);
/*      */       try {
/*  513 */         MappedByteBuffer localMappedByteBuffer = map(MapMode.READ_ONLY, paramLong1, l2);
/*      */         try
/*      */         {
/*  516 */           int i = paramWritableByteChannel.write(localMappedByteBuffer);
/*  517 */           assert (i >= 0);
/*  518 */           l1 -= i;
/*  519 */           if (bool)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  526 */             unmap(localMappedByteBuffer); break;
/*      */           }
/*  523 */           assert (i > 0);
/*  524 */           paramLong1 += i;
/*      */         } finally {
/*  526 */           unmap(localMappedByteBuffer);
/*      */         }
/*      */       }
/*      */       catch (ClosedByInterruptException localClosedByInterruptException)
/*      */       {
/*  531 */         assert (!paramWritableByteChannel.isOpen());
/*      */         try {
/*  533 */           close();
/*      */         } catch (Throwable localThrowable) {
/*  535 */           localClosedByInterruptException.addSuppressed(localThrowable);
/*      */         }
/*  537 */         throw localClosedByInterruptException;
/*      */       }
/*      */       catch (IOException localIOException) {
/*  540 */         if (l1 == paramLong2)
/*  541 */           throw localIOException;
/*  542 */         break;
/*      */       }
/*      */     }
/*  545 */     return paramLong2 - l1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private long transferToArbitraryChannel(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel)
/*      */     throws IOException
/*      */   {
/*  553 */     int i = Math.min(paramInt, 8192);
/*  554 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(i);
/*  555 */     long l1 = 0L;
/*  556 */     long l2 = paramLong;
/*      */     try {
/*  558 */       Util.erase(localByteBuffer);
/*  559 */       while (l1 < paramInt) {
/*  560 */         localByteBuffer.limit(Math.min((int)(paramInt - l1), 8192));
/*  561 */         int j = read(localByteBuffer, l2);
/*  562 */         if (j <= 0)
/*      */           break;
/*  564 */         localByteBuffer.flip();
/*      */         
/*      */ 
/*  567 */         int k = paramWritableByteChannel.write(localByteBuffer);
/*  568 */         l1 += k;
/*  569 */         if (k != j)
/*      */           break;
/*  571 */         l2 += k;
/*  572 */         localByteBuffer.clear();
/*      */       }
/*  574 */       return l1;
/*      */     } catch (IOException localIOException) {
/*  576 */       if (l1 > 0L)
/*  577 */         return l1;
/*  578 */       throw localIOException;
/*      */     } finally {
/*  580 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
/*      */     throws IOException
/*      */   {
/*  588 */     ensureOpen();
/*  589 */     if (!paramWritableByteChannel.isOpen())
/*  590 */       throw new ClosedChannelException();
/*  591 */     if (!this.readable)
/*  592 */       throw new NonReadableChannelException();
/*  593 */     if (((paramWritableByteChannel instanceof FileChannelImpl)) && (!((FileChannelImpl)paramWritableByteChannel).writable))
/*      */     {
/*  595 */       throw new NonWritableChannelException(); }
/*  596 */     if ((paramLong1 < 0L) || (paramLong2 < 0L))
/*  597 */       throw new IllegalArgumentException();
/*  598 */     long l1 = size();
/*  599 */     if (paramLong1 > l1)
/*  600 */       return 0L;
/*  601 */     int i = (int)Math.min(paramLong2, 2147483647L);
/*  602 */     if (l1 - paramLong1 < i) {
/*  603 */       i = (int)(l1 - paramLong1);
/*      */     }
/*      */     
/*      */     long l2;
/*      */     
/*  608 */     if ((l2 = transferToDirectly(paramLong1, i, paramWritableByteChannel)) >= 0L) {
/*  609 */       return l2;
/*      */     }
/*      */     
/*  612 */     if ((l2 = transferToTrustedChannel(paramLong1, i, paramWritableByteChannel)) >= 0L) {
/*  613 */       return l2;
/*      */     }
/*      */     
/*  616 */     return transferToArbitraryChannel(paramLong1, i, paramWritableByteChannel);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private long transferFromFileChannel(FileChannelImpl paramFileChannelImpl, long paramLong1, long paramLong2)
/*      */     throws IOException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: getfield 456	sun/nio/ch/FileChannelImpl:readable	Z
/*      */     //   4: ifne +11 -> 15
/*      */     //   7: new 252	java/nio/channels/NonReadableChannelException
/*      */     //   10: dup
/*      */     //   11: invokespecial 495	java/nio/channels/NonReadableChannelException:<init>	()V
/*      */     //   14: athrow
/*      */     //   15: aload_1
/*      */     //   16: getfield 461	sun/nio/ch/FileChannelImpl:positionLock	Ljava/lang/Object;
/*      */     //   19: dup
/*      */     //   20: astore 6
/*      */     //   22: monitorenter
/*      */     //   23: aload_1
/*      */     //   24: invokevirtual 500	sun/nio/ch/FileChannelImpl:position	()J
/*      */     //   27: lstore 7
/*      */     //   29: lload 4
/*      */     //   31: aload_1
/*      */     //   32: invokevirtual 501	sun/nio/ch/FileChannelImpl:size	()J
/*      */     //   35: lload 7
/*      */     //   37: lsub
/*      */     //   38: invokestatic 475	java/lang/Math:min	(JJ)J
/*      */     //   41: lstore 9
/*      */     //   43: lload 9
/*      */     //   45: lstore 11
/*      */     //   47: lload 7
/*      */     //   49: lstore 13
/*      */     //   51: lload 11
/*      */     //   53: lconst_0
/*      */     //   54: lcmp
/*      */     //   55: ifle +118 -> 173
/*      */     //   58: lload 11
/*      */     //   60: ldc2_w 223
/*      */     //   63: invokestatic 475	java/lang/Math:min	(JJ)J
/*      */     //   66: lstore 15
/*      */     //   68: aload_1
/*      */     //   69: getstatic 447	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
/*      */     //   72: lload 13
/*      */     //   74: lload 15
/*      */     //   76: invokevirtual 526	sun/nio/ch/FileChannelImpl:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
/*      */     //   79: astore 17
/*      */     //   81: aload_0
/*      */     //   82: aload 17
/*      */     //   84: lload_2
/*      */     //   85: invokevirtual 513	sun/nio/ch/FileChannelImpl:write	(Ljava/nio/ByteBuffer;J)I
/*      */     //   88: i2l
/*      */     //   89: lstore 18
/*      */     //   91: getstatic 450	sun/nio/ch/FileChannelImpl:$assertionsDisabled	Z
/*      */     //   94: ifne +18 -> 112
/*      */     //   97: lload 18
/*      */     //   99: lconst_0
/*      */     //   100: lcmp
/*      */     //   101: ifgt +11 -> 112
/*      */     //   104: new 231	java/lang/AssertionError
/*      */     //   107: dup
/*      */     //   108: invokespecial 469	java/lang/AssertionError:<init>	()V
/*      */     //   111: athrow
/*      */     //   112: lload 13
/*      */     //   114: lload 18
/*      */     //   116: ladd
/*      */     //   117: lstore 13
/*      */     //   119: lload_2
/*      */     //   120: lload 18
/*      */     //   122: ladd
/*      */     //   123: lstore_2
/*      */     //   124: lload 11
/*      */     //   126: lload 18
/*      */     //   128: lsub
/*      */     //   129: lstore 11
/*      */     //   131: aload 17
/*      */     //   133: invokestatic 515	sun/nio/ch/FileChannelImpl:unmap	(Ljava/nio/MappedByteBuffer;)V
/*      */     //   136: goto +34 -> 170
/*      */     //   139: astore 18
/*      */     //   141: lload 11
/*      */     //   143: lload 9
/*      */     //   145: lcmp
/*      */     //   146: ifne +6 -> 152
/*      */     //   149: aload 18
/*      */     //   151: athrow
/*      */     //   152: aload 17
/*      */     //   154: invokestatic 515	sun/nio/ch/FileChannelImpl:unmap	(Ljava/nio/MappedByteBuffer;)V
/*      */     //   157: goto +16 -> 173
/*      */     //   160: astore 20
/*      */     //   162: aload 17
/*      */     //   164: invokestatic 515	sun/nio/ch/FileChannelImpl:unmap	(Ljava/nio/MappedByteBuffer;)V
/*      */     //   167: aload 20
/*      */     //   169: athrow
/*      */     //   170: goto -119 -> 51
/*      */     //   173: lload 9
/*      */     //   175: lload 11
/*      */     //   177: lsub
/*      */     //   178: lstore 15
/*      */     //   180: aload_1
/*      */     //   181: lload 7
/*      */     //   183: lload 15
/*      */     //   185: ladd
/*      */     //   186: invokevirtual 516	sun/nio/ch/FileChannelImpl:position	(J)Ljava/nio/channels/FileChannel;
/*      */     //   189: pop
/*      */     //   190: lload 15
/*      */     //   192: aload 6
/*      */     //   194: monitorexit
/*      */     //   195: lreturn
/*      */     //   196: astore 21
/*      */     //   198: aload 6
/*      */     //   200: monitorexit
/*      */     //   201: aload 21
/*      */     //   203: athrow
/*      */     // Line number table:
/*      */     //   Java source line #623	-> byte code offset #0
/*      */     //   Java source line #624	-> byte code offset #7
/*      */     //   Java source line #625	-> byte code offset #15
/*      */     //   Java source line #626	-> byte code offset #23
/*      */     //   Java source line #627	-> byte code offset #29
/*      */     //   Java source line #629	-> byte code offset #43
/*      */     //   Java source line #630	-> byte code offset #47
/*      */     //   Java source line #631	-> byte code offset #51
/*      */     //   Java source line #632	-> byte code offset #58
/*      */     //   Java source line #634	-> byte code offset #68
/*      */     //   Java source line #636	-> byte code offset #81
/*      */     //   Java source line #637	-> byte code offset #91
/*      */     //   Java source line #638	-> byte code offset #112
/*      */     //   Java source line #639	-> byte code offset #119
/*      */     //   Java source line #640	-> byte code offset #124
/*      */     //   Java source line #647	-> byte code offset #131
/*      */     //   Java source line #648	-> byte code offset #136
/*      */     //   Java source line #641	-> byte code offset #139
/*      */     //   Java source line #643	-> byte code offset #141
/*      */     //   Java source line #644	-> byte code offset #149
/*      */     //   Java source line #647	-> byte code offset #152
/*      */     //   Java source line #648	-> byte code offset #167
/*      */     //   Java source line #649	-> byte code offset #170
/*      */     //   Java source line #650	-> byte code offset #173
/*      */     //   Java source line #651	-> byte code offset #180
/*      */     //   Java source line #652	-> byte code offset #190
/*      */     //   Java source line #653	-> byte code offset #196
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	204	0	this	FileChannelImpl
/*      */     //   0	204	1	paramFileChannelImpl	FileChannelImpl
/*      */     //   0	204	2	paramLong1	long
/*      */     //   0	204	4	paramLong2	long
/*      */     //   20	179	6	Ljava/lang/Object;	Object
/*      */     //   27	155	7	l1	long
/*      */     //   41	133	9	l2	long
/*      */     //   45	131	11	l3	long
/*      */     //   49	69	13	l4	long
/*      */     //   66	125	15	l5	long
/*      */     //   79	84	17	localMappedByteBuffer	MappedByteBuffer
/*      */     //   89	38	18	l6	long
/*      */     //   139	11	18	localIOException	IOException
/*      */     //   160	8	20	localObject1	Object
/*      */     //   196	6	21	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   81	131	139	java/io/IOException
/*      */     //   81	131	160	finally
/*      */     //   139	152	160	finally
/*      */     //   160	162	160	finally
/*      */     //   23	195	196	finally
/*      */     //   196	201	196	finally
/*      */   }
/*      */   
/*      */   private long transferFromArbitraryChannel(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2)
/*      */     throws IOException
/*      */   {
/*  663 */     int i = (int)Math.min(paramLong2, 8192L);
/*  664 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(i);
/*  665 */     long l1 = 0L;
/*  666 */     long l2 = paramLong1;
/*      */     try {
/*  668 */       Util.erase(localByteBuffer);
/*  669 */       while (l1 < paramLong2) {
/*  670 */         localByteBuffer.limit((int)Math.min(paramLong2 - l1, 8192L));
/*      */         
/*      */ 
/*  673 */         int j = paramReadableByteChannel.read(localByteBuffer);
/*  674 */         if (j <= 0)
/*      */           break;
/*  676 */         localByteBuffer.flip();
/*  677 */         int k = write(localByteBuffer, l2);
/*  678 */         l1 += k;
/*  679 */         if (k != j)
/*      */           break;
/*  681 */         l2 += k;
/*  682 */         localByteBuffer.clear();
/*      */       }
/*  684 */       return l1;
/*      */     } catch (IOException localIOException) {
/*  686 */       if (l1 > 0L)
/*  687 */         return l1;
/*  688 */       throw localIOException;
/*      */     } finally {
/*  690 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long transferFrom(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2)
/*      */     throws IOException
/*      */   {
/*  698 */     ensureOpen();
/*  699 */     if (!paramReadableByteChannel.isOpen())
/*  700 */       throw new ClosedChannelException();
/*  701 */     if (!this.writable)
/*  702 */       throw new NonWritableChannelException();
/*  703 */     if ((paramLong1 < 0L) || (paramLong2 < 0L))
/*  704 */       throw new IllegalArgumentException();
/*  705 */     if (paramLong1 > size())
/*  706 */       return 0L;
/*  707 */     if ((paramReadableByteChannel instanceof FileChannelImpl)) {
/*  708 */       return transferFromFileChannel((FileChannelImpl)paramReadableByteChannel, paramLong1, paramLong2);
/*      */     }
/*      */     
/*  711 */     return transferFromArbitraryChannel(paramReadableByteChannel, paramLong1, paramLong2);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int read(ByteBuffer paramByteBuffer, long paramLong)
/*      */     throws IOException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: ifnonnull +11 -> 12
/*      */     //   4: new 237	java/lang/NullPointerException
/*      */     //   7: dup
/*      */     //   8: invokespecial 476	java/lang/NullPointerException:<init>	()V
/*      */     //   11: athrow
/*      */     //   12: lload_2
/*      */     //   13: lconst_0
/*      */     //   14: lcmp
/*      */     //   15: ifge +13 -> 28
/*      */     //   18: new 233	java/lang/IllegalArgumentException
/*      */     //   21: dup
/*      */     //   22: ldc 4
/*      */     //   24: invokespecial 472	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
/*      */     //   27: athrow
/*      */     //   28: aload_0
/*      */     //   29: getfield 456	sun/nio/ch/FileChannelImpl:readable	Z
/*      */     //   32: ifne +11 -> 43
/*      */     //   35: new 252	java/nio/channels/NonReadableChannelException
/*      */     //   38: dup
/*      */     //   39: invokespecial 495	java/nio/channels/NonReadableChannelException:<init>	()V
/*      */     //   42: athrow
/*      */     //   43: aload_0
/*      */     //   44: invokespecial 504	sun/nio/ch/FileChannelImpl:ensureOpen	()V
/*      */     //   47: aload_0
/*      */     //   48: getfield 463	sun/nio/ch/FileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
/*      */     //   51: invokevirtual 531	sun/nio/ch/FileDispatcher:needsPositionLock	()Z
/*      */     //   54: ifeq +29 -> 83
/*      */     //   57: aload_0
/*      */     //   58: getfield 461	sun/nio/ch/FileChannelImpl:positionLock	Ljava/lang/Object;
/*      */     //   61: dup
/*      */     //   62: astore 4
/*      */     //   64: monitorenter
/*      */     //   65: aload_0
/*      */     //   66: aload_1
/*      */     //   67: lload_2
/*      */     //   68: invokespecial 512	sun/nio/ch/FileChannelImpl:readInternal	(Ljava/nio/ByteBuffer;J)I
/*      */     //   71: aload 4
/*      */     //   73: monitorexit
/*      */     //   74: ireturn
/*      */     //   75: astore 5
/*      */     //   77: aload 4
/*      */     //   79: monitorexit
/*      */     //   80: aload 5
/*      */     //   82: athrow
/*      */     //   83: aload_0
/*      */     //   84: aload_1
/*      */     //   85: lload_2
/*      */     //   86: invokespecial 512	sun/nio/ch/FileChannelImpl:readInternal	(Ljava/nio/ByteBuffer;J)I
/*      */     //   89: ireturn
/*      */     // Line number table:
/*      */     //   Java source line #715	-> byte code offset #0
/*      */     //   Java source line #716	-> byte code offset #4
/*      */     //   Java source line #717	-> byte code offset #12
/*      */     //   Java source line #718	-> byte code offset #18
/*      */     //   Java source line #719	-> byte code offset #28
/*      */     //   Java source line #720	-> byte code offset #35
/*      */     //   Java source line #721	-> byte code offset #43
/*      */     //   Java source line #722	-> byte code offset #47
/*      */     //   Java source line #723	-> byte code offset #57
/*      */     //   Java source line #724	-> byte code offset #65
/*      */     //   Java source line #725	-> byte code offset #75
/*      */     //   Java source line #727	-> byte code offset #83
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	90	0	this	FileChannelImpl
/*      */     //   0	90	1	paramByteBuffer	ByteBuffer
/*      */     //   0	90	2	paramLong	long
/*      */     //   62	16	4	Ljava/lang/Object;	Object
/*      */     //   75	6	5	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   65	74	75	finally
/*      */     //   75	80	75	finally
/*      */   }
/*      */   
/*      */   private int readInternal(ByteBuffer paramByteBuffer, long paramLong)
/*      */     throws IOException
/*      */   {
/*  732 */     assert ((!this.nd.needsPositionLock()) || (Thread.holdsLock(this.positionLock)));
/*  733 */     int i = 0;
/*  734 */     int j = -1;
/*      */     try {
/*  736 */       begin();
/*  737 */       j = this.threads.add();
/*  738 */       int k; if (!isOpen())
/*  739 */         return -1;
/*      */       do {
/*  741 */         i = IOUtil.read(this.fd, paramByteBuffer, paramLong, this.nd);
/*  742 */       } while ((i == -3) && (isOpen()));
/*  743 */       return IOStatus.normalize(i);
/*      */     } finally {
/*  745 */       this.threads.remove(j);
/*  746 */       end(i > 0);
/*  747 */       if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int write(ByteBuffer paramByteBuffer, long paramLong)
/*      */     throws IOException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: ifnonnull +11 -> 12
/*      */     //   4: new 237	java/lang/NullPointerException
/*      */     //   7: dup
/*      */     //   8: invokespecial 476	java/lang/NullPointerException:<init>	()V
/*      */     //   11: athrow
/*      */     //   12: lload_2
/*      */     //   13: lconst_0
/*      */     //   14: lcmp
/*      */     //   15: ifge +13 -> 28
/*      */     //   18: new 233	java/lang/IllegalArgumentException
/*      */     //   21: dup
/*      */     //   22: ldc 4
/*      */     //   24: invokespecial 472	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
/*      */     //   27: athrow
/*      */     //   28: aload_0
/*      */     //   29: getfield 458	sun/nio/ch/FileChannelImpl:writable	Z
/*      */     //   32: ifne +11 -> 43
/*      */     //   35: new 253	java/nio/channels/NonWritableChannelException
/*      */     //   38: dup
/*      */     //   39: invokespecial 496	java/nio/channels/NonWritableChannelException:<init>	()V
/*      */     //   42: athrow
/*      */     //   43: aload_0
/*      */     //   44: invokespecial 504	sun/nio/ch/FileChannelImpl:ensureOpen	()V
/*      */     //   47: aload_0
/*      */     //   48: getfield 463	sun/nio/ch/FileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
/*      */     //   51: invokevirtual 531	sun/nio/ch/FileDispatcher:needsPositionLock	()Z
/*      */     //   54: ifeq +29 -> 83
/*      */     //   57: aload_0
/*      */     //   58: getfield 461	sun/nio/ch/FileChannelImpl:positionLock	Ljava/lang/Object;
/*      */     //   61: dup
/*      */     //   62: astore 4
/*      */     //   64: monitorenter
/*      */     //   65: aload_0
/*      */     //   66: aload_1
/*      */     //   67: lload_2
/*      */     //   68: invokespecial 514	sun/nio/ch/FileChannelImpl:writeInternal	(Ljava/nio/ByteBuffer;J)I
/*      */     //   71: aload 4
/*      */     //   73: monitorexit
/*      */     //   74: ireturn
/*      */     //   75: astore 5
/*      */     //   77: aload 4
/*      */     //   79: monitorexit
/*      */     //   80: aload 5
/*      */     //   82: athrow
/*      */     //   83: aload_0
/*      */     //   84: aload_1
/*      */     //   85: lload_2
/*      */     //   86: invokespecial 514	sun/nio/ch/FileChannelImpl:writeInternal	(Ljava/nio/ByteBuffer;J)I
/*      */     //   89: ireturn
/*      */     // Line number table:
/*      */     //   Java source line #752	-> byte code offset #0
/*      */     //   Java source line #753	-> byte code offset #4
/*      */     //   Java source line #754	-> byte code offset #12
/*      */     //   Java source line #755	-> byte code offset #18
/*      */     //   Java source line #756	-> byte code offset #28
/*      */     //   Java source line #757	-> byte code offset #35
/*      */     //   Java source line #758	-> byte code offset #43
/*      */     //   Java source line #759	-> byte code offset #47
/*      */     //   Java source line #760	-> byte code offset #57
/*      */     //   Java source line #761	-> byte code offset #65
/*      */     //   Java source line #762	-> byte code offset #75
/*      */     //   Java source line #764	-> byte code offset #83
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	90	0	this	FileChannelImpl
/*      */     //   0	90	1	paramByteBuffer	ByteBuffer
/*      */     //   0	90	2	paramLong	long
/*      */     //   62	16	4	Ljava/lang/Object;	Object
/*      */     //   75	6	5	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   65	74	75	finally
/*      */     //   75	80	75	finally
/*      */   }
/*      */   
/*      */   private int writeInternal(ByteBuffer paramByteBuffer, long paramLong)
/*      */     throws IOException
/*      */   {
/*  769 */     assert ((!this.nd.needsPositionLock()) || (Thread.holdsLock(this.positionLock)));
/*  770 */     int i = 0;
/*  771 */     int j = -1;
/*      */     try {
/*  773 */       begin();
/*  774 */       j = this.threads.add();
/*  775 */       int k; if (!isOpen())
/*  776 */         return -1;
/*      */       do {
/*  778 */         i = IOUtil.write(this.fd, paramByteBuffer, paramLong, this.nd);
/*  779 */       } while ((i == -3) && (isOpen()));
/*  780 */       return IOStatus.normalize(i);
/*      */     } finally {
/*  782 */       this.threads.remove(j);
/*  783 */       end(i > 0);
/*  784 */       if ((!$assertionsDisabled) && (!IOStatus.check(i))) { throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class Unmapper
/*      */     implements Runnable
/*      */   {
/*  795 */     private static final NativeDispatcher nd = new FileDispatcherImpl();
/*      */     
/*      */     static volatile int count;
/*      */     
/*      */     static volatile long totalSize;
/*      */     
/*      */     static volatile long totalCapacity;
/*      */     
/*      */     private volatile long address;
/*      */     private final long size;
/*      */     private final int cap;
/*      */     private final FileDescriptor fd;
/*      */     
/*      */     private Unmapper(long paramLong1, long paramLong2, int paramInt, FileDescriptor paramFileDescriptor)
/*      */     {
/*  810 */       assert (paramLong1 != 0L);
/*  811 */       this.address = paramLong1;
/*  812 */       this.size = paramLong2;
/*  813 */       this.cap = paramInt;
/*  814 */       this.fd = paramFileDescriptor;
/*      */       
/*  816 */       synchronized (Unmapper.class) {
/*  817 */         count += 1;
/*  818 */         totalSize += paramLong2;
/*  819 */         totalCapacity += paramInt;
/*      */       }
/*      */     }
/*      */     
/*      */     public void run() {
/*  824 */       if (this.address == 0L)
/*  825 */         return;
/*  826 */       FileChannelImpl.unmap0(this.address, this.size);
/*  827 */       this.address = 0L;
/*      */       
/*      */ 
/*  830 */       if (this.fd.valid()) {
/*      */         try {
/*  832 */           nd.close(this.fd);
/*      */         }
/*      */         catch (IOException localIOException) {}
/*      */       }
/*      */       
/*      */ 
/*  838 */       synchronized (Unmapper.class) {
/*  839 */         count -= 1;
/*  840 */         totalSize -= this.size;
/*  841 */         totalCapacity -= this.cap;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static void unmap(MappedByteBuffer paramMappedByteBuffer) {
/*  847 */     Cleaner localCleaner = ((DirectBuffer)paramMappedByteBuffer).cleaner();
/*  848 */     if (localCleaner != null) {
/*  849 */       localCleaner.clean();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public MappedByteBuffer map(MapMode paramMapMode, long paramLong1, long paramLong2)
/*      */     throws IOException
/*      */   {
/*  859 */     ensureOpen();
/*  860 */     if (paramMapMode == null)
/*  861 */       throw new NullPointerException("Mode is null");
/*  862 */     if (paramLong1 < 0L)
/*  863 */       throw new IllegalArgumentException("Negative position");
/*  864 */     if (paramLong2 < 0L)
/*  865 */       throw new IllegalArgumentException("Negative size");
/*  866 */     if (paramLong1 + paramLong2 < 0L)
/*  867 */       throw new IllegalArgumentException("Position + size overflow");
/*  868 */     if (paramLong2 > 2147483647L) {
/*  869 */       throw new IllegalArgumentException("Size exceeds Integer.MAX_VALUE");
/*      */     }
/*  871 */     int i = -1;
/*  872 */     if (paramMapMode == MapMode.READ_ONLY) {
/*  873 */       i = 0;
/*  874 */     } else if (paramMapMode == MapMode.READ_WRITE) {
/*  875 */       i = 1;
/*  876 */     } else if (paramMapMode == MapMode.PRIVATE)
/*  877 */       i = 2;
/*  878 */     assert (i >= 0);
/*  879 */     if ((paramMapMode != MapMode.READ_ONLY) && (!this.writable))
/*  880 */       throw new NonWritableChannelException();
/*  881 */     if (!this.readable) {
/*  882 */       throw new NonReadableChannelException();
/*      */     }
/*  884 */     long l1 = -1L;
/*  885 */     int j = -1;
/*      */     try {
/*  887 */       begin();
/*  888 */       j = this.threads.add();
/*  889 */       if (!isOpen()) {
/*  890 */         return null;
/*      */       }
/*      */       long l2;
/*      */       do {
/*  894 */         l2 = this.nd.size(this.fd);
/*  895 */       } while ((l2 == -3L) && (isOpen()));
/*  896 */       if (!isOpen())
/*  897 */         return null;
/*      */       MappedByteBuffer localMappedByteBuffer3;
/*  899 */       if (l2 < paramLong1 + paramLong2) {
/*  900 */         if (!this.writable) {
/*  901 */           throw new IOException("Channel not open for writing - cannot extend file to required size");
/*      */         }
/*      */         int k;
/*      */         do
/*      */         {
/*  906 */           k = this.nd.truncate(this.fd, paramLong1 + paramLong2);
/*  907 */         } while ((k == -3) && (isOpen()));
/*  908 */         if (!isOpen())
/*  909 */           return null;
/*      */       }
/*  911 */       if (paramLong2 == 0L) {
/*  912 */         l1 = 0L;
/*      */         
/*  914 */         FileDescriptor localFileDescriptor1 = new FileDescriptor();
/*  915 */         if ((!this.writable) || (i == 0)) {
/*  916 */           return Util.newMappedByteBufferR(0, 0L, localFileDescriptor1, null);
/*      */         }
/*  918 */         return Util.newMappedByteBuffer(0, 0L, localFileDescriptor1, null);
/*      */       }
/*      */       
/*  921 */       int m = (int)(paramLong1 % allocationGranularity);
/*  922 */       long l3 = paramLong1 - m;
/*  923 */       long l4 = paramLong2 + m;
/*      */       try
/*      */       {
/*  926 */         l1 = map0(i, l3, l4);
/*      */       }
/*      */       catch (OutOfMemoryError localOutOfMemoryError1)
/*      */       {
/*  930 */         System.gc();
/*      */         try {
/*  932 */           Thread.sleep(100L);
/*      */         } catch (InterruptedException localInterruptedException) {
/*  934 */           Thread.currentThread().interrupt();
/*      */         }
/*      */         try {
/*  937 */           l1 = map0(i, l3, l4);
/*      */         }
/*      */         catch (OutOfMemoryError localOutOfMemoryError2) {
/*  940 */           throw new IOException("Map failed", localOutOfMemoryError2);
/*      */         }
/*      */       }
/*      */       
/*      */       FileDescriptor localFileDescriptor2;
/*      */       
/*      */       try
/*      */       {
/*  948 */         localFileDescriptor2 = this.nd.duplicateForMapping(this.fd);
/*      */       } catch (IOException localIOException) {
/*  950 */         unmap0(l1, l4);
/*  951 */         throw localIOException;
/*      */       }
/*      */       
/*  954 */       assert (IOStatus.checkAll(l1));
/*  955 */       assert (l1 % allocationGranularity == 0L);
/*  956 */       int n = (int)paramLong2;
/*  957 */       Unmapper localUnmapper = new Unmapper(l1, l4, n, localFileDescriptor2, null);
/*  958 */       MappedByteBuffer localMappedByteBuffer4; if ((!this.writable) || (i == 0)) {
/*  959 */         return Util.newMappedByteBufferR(n, l1 + m, localFileDescriptor2, localUnmapper);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  964 */       return Util.newMappedByteBuffer(n, l1 + m, localFileDescriptor2, localUnmapper);
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*  970 */       this.threads.remove(j);
/*  971 */       end(IOStatus.checkAll(l1));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static JavaNioAccess.BufferPool getMappedBufferPool()
/*      */   {
/*  980 */     new JavaNioAccess.BufferPool()
/*      */     {
/*      */       public String getName() {
/*  983 */         return "mapped";
/*      */       }
/*      */       
/*      */       public long getCount() {
/*  987 */         return Unmapper.count;
/*      */       }
/*      */       
/*      */       public long getTotalCapacity() {
/*  991 */         return Unmapper.totalCapacity;
/*      */       }
/*      */       
/*      */       public long getMemoryUsed() {
/*  995 */         return Unmapper.totalSize;
/*      */       }
/*      */     };
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
/*      */   private static boolean isSharedFileLockTable()
/*      */   {
/* 1018 */     if (!propertyChecked) {
/* 1019 */       synchronized (FileChannelImpl.class) {
/* 1020 */         if (!propertyChecked) {
/* 1021 */           String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.ch.disableSystemWideOverlappingFileLockCheck"));
/*      */           
/*      */ 
/* 1024 */           isSharedFileLockTable = (str == null) || (str.equals("false"));
/* 1025 */           propertyChecked = true;
/*      */         }
/*      */       }
/*      */     }
/* 1029 */     return isSharedFileLockTable;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private FileLockTable fileLockTable()
/*      */     throws IOException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 464	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
/*      */     //   4: ifnonnull +92 -> 96
/*      */     //   7: aload_0
/*      */     //   8: dup
/*      */     //   9: astore_1
/*      */     //   10: monitorenter
/*      */     //   11: aload_0
/*      */     //   12: getfield 464	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
/*      */     //   15: ifnonnull +69 -> 84
/*      */     //   18: invokestatic 506	sun/nio/ch/FileChannelImpl:isSharedFileLockTable	()Z
/*      */     //   21: ifeq +52 -> 73
/*      */     //   24: aload_0
/*      */     //   25: getfield 465	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
/*      */     //   28: invokevirtual 562	sun/nio/ch/NativeThreadSet:add	()I
/*      */     //   31: istore_2
/*      */     //   32: aload_0
/*      */     //   33: invokespecial 504	sun/nio/ch/FileChannelImpl:ensureOpen	()V
/*      */     //   36: aload_0
/*      */     //   37: aload_0
/*      */     //   38: aload_0
/*      */     //   39: getfield 459	sun/nio/ch/FileChannelImpl:fd	Ljava/io/FileDescriptor;
/*      */     //   42: invokestatic 550	sun/nio/ch/FileLockTable:newSharedFileLockTable	(Ljava/nio/channels/Channel;Ljava/io/FileDescriptor;)Lsun/nio/ch/FileLockTable;
/*      */     //   45: putfield 464	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
/*      */     //   48: aload_0
/*      */     //   49: getfield 465	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
/*      */     //   52: iload_2
/*      */     //   53: invokevirtual 565	sun/nio/ch/NativeThreadSet:remove	(I)V
/*      */     //   56: goto +14 -> 70
/*      */     //   59: astore_3
/*      */     //   60: aload_0
/*      */     //   61: getfield 465	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
/*      */     //   64: iload_2
/*      */     //   65: invokevirtual 565	sun/nio/ch/NativeThreadSet:remove	(I)V
/*      */     //   68: aload_3
/*      */     //   69: athrow
/*      */     //   70: goto +14 -> 84
/*      */     //   73: aload_0
/*      */     //   74: new 265	sun/nio/ch/FileChannelImpl$SimpleFileLockTable
/*      */     //   77: dup
/*      */     //   78: invokespecial 529	sun/nio/ch/FileChannelImpl$SimpleFileLockTable:<init>	()V
/*      */     //   81: putfield 464	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
/*      */     //   84: aload_1
/*      */     //   85: monitorexit
/*      */     //   86: goto +10 -> 96
/*      */     //   89: astore 4
/*      */     //   91: aload_1
/*      */     //   92: monitorexit
/*      */     //   93: aload 4
/*      */     //   95: athrow
/*      */     //   96: aload_0
/*      */     //   97: getfield 464	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
/*      */     //   100: areturn
/*      */     // Line number table:
/*      */     //   Java source line #1033	-> byte code offset #0
/*      */     //   Java source line #1034	-> byte code offset #7
/*      */     //   Java source line #1035	-> byte code offset #11
/*      */     //   Java source line #1036	-> byte code offset #18
/*      */     //   Java source line #1037	-> byte code offset #24
/*      */     //   Java source line #1039	-> byte code offset #32
/*      */     //   Java source line #1040	-> byte code offset #36
/*      */     //   Java source line #1042	-> byte code offset #48
/*      */     //   Java source line #1043	-> byte code offset #56
/*      */     //   Java source line #1042	-> byte code offset #59
/*      */     //   Java source line #1043	-> byte code offset #68
/*      */     //   Java source line #1044	-> byte code offset #70
/*      */     //   Java source line #1045	-> byte code offset #73
/*      */     //   Java source line #1048	-> byte code offset #84
/*      */     //   Java source line #1050	-> byte code offset #96
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	101	0	this	FileChannelImpl
/*      */     //   9	83	1	Ljava/lang/Object;	Object
/*      */     //   31	34	2	i	int
/*      */     //   59	10	3	localObject1	Object
/*      */     //   89	5	4	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   32	48	59	finally
/*      */     //   11	86	89	finally
/*      */     //   89	93	89	finally
/*      */   }
/*      */   
/*      */   public FileLock lock(long paramLong1, long paramLong2, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1056 */     ensureOpen();
/* 1057 */     if ((paramBoolean) && (!this.readable))
/* 1058 */       throw new NonReadableChannelException();
/* 1059 */     if ((!paramBoolean) && (!this.writable))
/* 1060 */       throw new NonWritableChannelException();
/* 1061 */     localObject1 = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
/* 1062 */     FileLockTable localFileLockTable = fileLockTable();
/* 1063 */     localFileLockTable.add((FileLock)localObject1);
/* 1064 */     boolean bool = false;
/* 1065 */     int i = -1;
/*      */     try {
/* 1067 */       begin();
/* 1068 */       i = this.threads.add();
/* 1069 */       if (!isOpen())
/* 1070 */         return null;
/*      */       int j;
/*      */       do {
/* 1073 */         j = this.nd.lock(this.fd, true, paramLong1, paramLong2, paramBoolean);
/* 1074 */       } while ((j == 2) && (isOpen()));
/* 1075 */       if (isOpen()) {
/* 1076 */         if (j == 1) {
/* 1077 */           assert (paramBoolean);
/* 1078 */           FileLockImpl localFileLockImpl = new FileLockImpl(this, paramLong1, paramLong2, false);
/*      */           
/* 1080 */           localFileLockTable.replace((FileLock)localObject1, localFileLockImpl);
/* 1081 */           localObject1 = localFileLockImpl;
/*      */         }
/* 1083 */         bool = true;
/*      */       }
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
/* 1095 */       return (FileLock)localObject1;
/*      */     }
/*      */     finally
/*      */     {
/* 1086 */       if (!bool)
/* 1087 */         localFileLockTable.remove((FileLock)localObject1);
/* 1088 */       this.threads.remove(i);
/*      */       try {
/* 1090 */         end(bool);
/*      */       } catch (ClosedByInterruptException localClosedByInterruptException3) {
/* 1092 */         throw new FileLockInterruptionException();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1101 */     ensureOpen();
/* 1102 */     if ((paramBoolean) && (!this.readable))
/* 1103 */       throw new NonReadableChannelException();
/* 1104 */     if ((!paramBoolean) && (!this.writable))
/* 1105 */       throw new NonWritableChannelException();
/* 1106 */     FileLockImpl localFileLockImpl = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
/* 1107 */     FileLockTable localFileLockTable = fileLockTable();
/* 1108 */     localFileLockTable.add(localFileLockImpl);
/*      */     
/*      */ 
/* 1111 */     int j = this.threads.add();
/*      */     try {
/*      */       int i;
/* 1114 */       try { ensureOpen();
/* 1115 */         i = this.nd.lock(this.fd, false, paramLong1, paramLong2, paramBoolean);
/*      */       } catch (IOException localIOException) {
/* 1117 */         localFileLockTable.remove(localFileLockImpl);
/* 1118 */         throw localIOException; }
/*      */       Object localObject1;
/* 1120 */       if (i == -1) {
/* 1121 */         localFileLockTable.remove(localFileLockImpl);
/* 1122 */         return null;
/*      */       }
/* 1124 */       if (i == 1) {
/* 1125 */         assert (paramBoolean);
/* 1126 */         localObject1 = new FileLockImpl(this, paramLong1, paramLong2, false);
/*      */         
/* 1128 */         localFileLockTable.replace(localFileLockImpl, (FileLock)localObject1);
/* 1129 */         return (FileLock)localObject1;
/*      */       }
/* 1131 */       return localFileLockImpl;
/*      */     } finally {
/* 1133 */       this.threads.remove(j);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   void release(FileLockImpl paramFileLockImpl)
/*      */     throws IOException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 465	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
/*      */     //   4: invokevirtual 562	sun/nio/ch/NativeThreadSet:add	()I
/*      */     //   7: istore_2
/*      */     //   8: aload_0
/*      */     //   9: invokespecial 504	sun/nio/ch/FileChannelImpl:ensureOpen	()V
/*      */     //   12: aload_0
/*      */     //   13: getfield 463	sun/nio/ch/FileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
/*      */     //   16: aload_0
/*      */     //   17: getfield 459	sun/nio/ch/FileChannelImpl:fd	Ljava/io/FileDescriptor;
/*      */     //   20: aload_1
/*      */     //   21: invokevirtual 542	sun/nio/ch/FileLockImpl:position	()J
/*      */     //   24: aload_1
/*      */     //   25: invokevirtual 543	sun/nio/ch/FileLockImpl:size	()J
/*      */     //   28: invokevirtual 536	sun/nio/ch/FileDispatcher:release	(Ljava/io/FileDescriptor;JJ)V
/*      */     //   31: aload_0
/*      */     //   32: getfield 465	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
/*      */     //   35: iload_2
/*      */     //   36: invokevirtual 565	sun/nio/ch/NativeThreadSet:remove	(I)V
/*      */     //   39: goto +14 -> 53
/*      */     //   42: astore_3
/*      */     //   43: aload_0
/*      */     //   44: getfield 465	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
/*      */     //   47: iload_2
/*      */     //   48: invokevirtual 565	sun/nio/ch/NativeThreadSet:remove	(I)V
/*      */     //   51: aload_3
/*      */     //   52: athrow
/*      */     //   53: getstatic 450	sun/nio/ch/FileChannelImpl:$assertionsDisabled	Z
/*      */     //   56: ifne +18 -> 74
/*      */     //   59: aload_0
/*      */     //   60: getfield 464	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
/*      */     //   63: ifnonnull +11 -> 74
/*      */     //   66: new 231	java/lang/AssertionError
/*      */     //   69: dup
/*      */     //   70: invokespecial 469	java/lang/AssertionError:<init>	()V
/*      */     //   73: athrow
/*      */     //   74: aload_0
/*      */     //   75: getfield 464	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
/*      */     //   78: aload_1
/*      */     //   79: invokevirtual 547	sun/nio/ch/FileLockTable:remove	(Ljava/nio/channels/FileLock;)V
/*      */     //   82: return
/*      */     // Line number table:
/*      */     //   Java source line #1138	-> byte code offset #0
/*      */     //   Java source line #1140	-> byte code offset #8
/*      */     //   Java source line #1141	-> byte code offset #12
/*      */     //   Java source line #1143	-> byte code offset #31
/*      */     //   Java source line #1144	-> byte code offset #39
/*      */     //   Java source line #1143	-> byte code offset #42
/*      */     //   Java source line #1144	-> byte code offset #51
/*      */     //   Java source line #1145	-> byte code offset #53
/*      */     //   Java source line #1146	-> byte code offset #74
/*      */     //   Java source line #1147	-> byte code offset #82
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	83	0	this	FileChannelImpl
/*      */     //   0	83	1	paramFileLockImpl	FileLockImpl
/*      */     //   7	41	2	i	int
/*      */     //   42	10	3	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   8	31	42	finally
/*      */   }
/*      */   
/*      */   private native long map0(int paramInt, long paramLong1, long paramLong2)
/*      */     throws IOException;
/*      */   
/*      */   private static native int unmap0(long paramLong1, long paramLong2);
/*      */   
/*      */   private native long transferTo0(FileDescriptor paramFileDescriptor1, long paramLong1, long paramLong2, FileDescriptor paramFileDescriptor2);
/*      */   
/*      */   private native long position0(FileDescriptor paramFileDescriptor, long paramLong);
/*      */   
/*      */   private static native long initIDs();
/*      */   
/*      */   private static class SimpleFileLockTable
/*      */     extends FileLockTable
/*      */   {
/* 1157 */     private final List<FileLock> lockList = new ArrayList(2);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void checkList(long paramLong1, long paramLong2)
/*      */       throws OverlappingFileLockException
/*      */     {
/* 1165 */       assert (Thread.holdsLock(this.lockList));
/* 1166 */       for (FileLock localFileLock : this.lockList) {
/* 1167 */         if (localFileLock.overlaps(paramLong1, paramLong2)) {
/* 1168 */           throw new OverlappingFileLockException();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public void add(FileLock paramFileLock) throws OverlappingFileLockException {
/* 1174 */       synchronized (this.lockList) {
/* 1175 */         checkList(paramFileLock.position(), paramFileLock.size());
/* 1176 */         this.lockList.add(paramFileLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public void remove(FileLock paramFileLock) {
/* 1181 */       synchronized (this.lockList) {
/* 1182 */         this.lockList.remove(paramFileLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public List<FileLock> removeAll() {
/* 1187 */       synchronized (this.lockList) {
/* 1188 */         ArrayList localArrayList = new ArrayList(this.lockList);
/* 1189 */         this.lockList.clear();
/* 1190 */         return localArrayList;
/*      */       }
/*      */     }
/*      */     
/*      */     public void replace(FileLock paramFileLock1, FileLock paramFileLock2) {
/* 1195 */       synchronized (this.lockList) {
/* 1196 */         this.lockList.remove(paramFileLock1);
/* 1197 */         this.lockList.add(paramFileLock2);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  400 */     transferSupported = true;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  405 */     pipeSupported = true;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  410 */     fileSupported = true;
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
/* 1224 */     IOUtil.load(); }
/* 1225 */   private static final long allocationGranularity = initIDs();
/*      */   private final FileDispatcher nd;
/*      */   private final FileDescriptor fd;
/*      */   private final boolean writable;
/*      */   private final boolean readable;
/*      */   private final boolean append;
/*      */   private final Object parent;
/*      */   private final String path;
/*      */   private static volatile boolean transferSupported;
/*      */   private static volatile boolean pipeSupported;
/*      */   private static volatile boolean fileSupported;
/*      */   private static final long MAPPED_TRANSFER_SIZE = 8388608L;
/*      */   private static final int TRANSFER_SIZE = 8192;
/*      */   private static final int MAP_RO = 0;
/*      */   private static final int MAP_RW = 1;
/*      */   private static final int MAP_PV = 2;
/*      */   private volatile FileLockTable fileLockTable;
/*      */   private static boolean isSharedFileLockTable;
/*      */   private static volatile boolean propertyChecked;
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\FileChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
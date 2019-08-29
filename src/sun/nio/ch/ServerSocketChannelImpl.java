/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardProtocolFamily;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.channels.AlreadyBoundException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.NotYetBoundException;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import sun.net.NetHooks;
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
/*     */ class ServerSocketChannelImpl
/*     */   extends ServerSocketChannel
/*     */   implements SelChImpl
/*     */ {
/*  57 */   private volatile long thread = 0L;
/*     */   
/*     */ 
/*  60 */   private final Object lock = new Object();
/*     */   
/*     */ 
/*     */ 
/*  64 */   private final Object stateLock = new Object();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  72 */   private int state = -1;
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
/*     */   ServerSocketChannelImpl(SelectorProvider paramSelectorProvider)
/*     */     throws IOException
/*     */   {
/*  87 */     super(paramSelectorProvider);
/*  88 */     this.fd = Net.serverSocket(true);
/*  89 */     this.fdVal = IOUtil.fdVal(this.fd);
/*  90 */     this.state = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   ServerSocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  98 */     super(paramSelectorProvider);
/*  99 */     this.fd = paramFileDescriptor;
/* 100 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/* 101 */     this.state = 0;
/* 102 */     if (paramBoolean)
/* 103 */       this.localAddress = Net.localAddress(paramFileDescriptor);
/*     */   }
/*     */   
/*     */   public ServerSocket socket() {
/* 107 */     synchronized (this.stateLock) {
/* 108 */       if (this.socket == null)
/* 109 */         this.socket = ServerSocketAdaptor.create(this);
/* 110 */       return this.socket;
/*     */     }
/*     */   }
/*     */   
/*     */   public SocketAddress getLocalAddress() throws IOException
/*     */   {
/* 116 */     synchronized (this.stateLock) {
/* 117 */       if (!isOpen())
/* 118 */         throw new ClosedChannelException();
/* 119 */       return this.localAddress == null ? this.localAddress : 
/* 120 */         Net.getRevealedLocalAddress(
/* 121 */         Net.asInetSocketAddress(this.localAddress));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> ServerSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
/*     */     throws IOException
/*     */   {
/* 129 */     if (paramSocketOption == null)
/* 130 */       throw new NullPointerException();
/* 131 */     if (!supportedOptions().contains(paramSocketOption))
/* 132 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/* 133 */     synchronized (this.stateLock) {
/* 134 */       if (!isOpen()) {
/* 135 */         throw new ClosedChannelException();
/*     */       }
/* 137 */       if (paramSocketOption == StandardSocketOptions.IP_TOS) {
/* 138 */         StandardProtocolFamily localStandardProtocolFamily = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
/*     */         
/* 140 */         Net.setSocketOption(this.fd, localStandardProtocolFamily, paramSocketOption, paramT);
/* 141 */         return this;
/*     */       }
/*     */       
/* 144 */       if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && 
/* 145 */         (Net.useExclusiveBind()))
/*     */       {
/*     */ 
/* 148 */         this.isReuseAddress = ((Boolean)paramT).booleanValue();
/*     */       }
/*     */       else {
/* 151 */         Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
/*     */       }
/* 153 */       return this;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public <T> T getOption(SocketOption<T> paramSocketOption)
/*     */     throws IOException
/*     */   {
/* 162 */     if (paramSocketOption == null)
/* 163 */       throw new NullPointerException();
/* 164 */     if (!supportedOptions().contains(paramSocketOption)) {
/* 165 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*     */     }
/* 167 */     synchronized (this.stateLock) {
/* 168 */       if (!isOpen())
/* 169 */         throw new ClosedChannelException();
/* 170 */       if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && 
/* 171 */         (Net.useExclusiveBind()))
/*     */       {
/*     */ 
/* 174 */         return Boolean.valueOf(this.isReuseAddress);
/*     */       }
/*     */       
/* 177 */       return (T)Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class DefaultOptionsHolder {
/* 182 */     static final Set<SocketOption<?>> defaultOptions = ;
/*     */     
/*     */     private static Set<SocketOption<?>> defaultOptions() {
/* 185 */       HashSet localHashSet = new HashSet(2);
/* 186 */       localHashSet.add(StandardSocketOptions.SO_RCVBUF);
/* 187 */       localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
/* 188 */       localHashSet.add(StandardSocketOptions.IP_TOS);
/* 189 */       return Collections.unmodifiableSet(localHashSet);
/*     */     }
/*     */   }
/*     */   
/*     */   public final Set<SocketOption<?>> supportedOptions()
/*     */   {
/* 195 */     return DefaultOptionsHolder.defaultOptions;
/*     */   }
/*     */   
/*     */   public boolean isBound() {
/* 199 */     synchronized (this.stateLock) {
/* 200 */       return this.localAddress != null;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public InetSocketAddress localAddress()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 374	sun/nio/ch/ServerSocketChannelImpl:stateLock	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 375	sun/nio/ch/ServerSocketChannelImpl:localAddress	Ljava/net/InetSocketAddress;
/*     */     //   11: aload_1
/*     */     //   12: monitorexit
/*     */     //   13: areturn
/*     */     //   14: astore_2
/*     */     //   15: aload_1
/*     */     //   16: monitorexit
/*     */     //   17: aload_2
/*     */     //   18: athrow
/*     */     // Line number table:
/*     */     //   Java source line #205	-> byte code offset #0
/*     */     //   Java source line #206	-> byte code offset #7
/*     */     //   Java source line #207	-> byte code offset #14
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	19	0	this	ServerSocketChannelImpl
/*     */     //   5	11	1	Ljava/lang/Object;	Object
/*     */     //   14	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	13	14	finally
/*     */     //   14	17	14	finally
/*     */   }
/*     */   
/*     */   public ServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 212 */     synchronized (this.lock) {
/* 213 */       if (!isOpen())
/* 214 */         throw new ClosedChannelException();
/* 215 */       if (isBound()) {
/* 216 */         throw new AlreadyBoundException();
/*     */       }
/* 218 */       InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/* 219 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 220 */       if (localSecurityManager != null)
/* 221 */         localSecurityManager.checkListen(localInetSocketAddress.getPort());
/* 222 */       NetHooks.beforeTcpBind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 223 */       Net.bind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/* 224 */       Net.listen(this.fd, paramInt < 1 ? 50 : paramInt);
/* 225 */       synchronized (this.stateLock) {
/* 226 */         this.localAddress = Net.localAddress(this.fd);
/*     */       }
/*     */     }
/* 229 */     return this;
/*     */   }
/*     */   
/*     */   public SocketChannel accept() throws IOException {
/* 233 */     synchronized (this.lock) {
/* 234 */       if (!isOpen())
/* 235 */         throw new ClosedChannelException();
/* 236 */       if (!isBound())
/* 237 */         throw new NotYetBoundException();
/* 238 */       SocketChannelImpl localSocketChannelImpl = null;
/*     */       
/* 240 */       int i = 0;
/* 241 */       FileDescriptor localFileDescriptor = new FileDescriptor();
/* 242 */       InetSocketAddress[] arrayOfInetSocketAddress = new InetSocketAddress[1];
/*     */       try
/*     */       {
/* 245 */         begin();
/* 246 */         if (!isOpen()) {
/* 247 */           localObject1 = null;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 256 */           this.thread = 0L;
/* 257 */           end(i > 0);
/* 258 */           assert (IOStatus.check(i));return (SocketChannel)localObject1; }
/* 248 */         this.thread = NativeThread.current();
/*     */         for (;;) {
/* 250 */           i = accept(this.fd, localFileDescriptor, arrayOfInetSocketAddress);
/* 251 */           if ((i != -3) || (!isOpen())) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       } finally {
/* 256 */         this.thread = 0L;
/* 257 */         end(i > 0);
/* 258 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) { throw new AssertionError();
/*     */         }
/*     */       }
/* 261 */       if (i < 1) {
/* 262 */         return null;
/*     */       }
/* 264 */       IOUtil.configureBlocking(localFileDescriptor, true);
/* 265 */       Object localObject1 = arrayOfInetSocketAddress[0];
/* 266 */       localSocketChannelImpl = new SocketChannelImpl(provider(), localFileDescriptor, (InetSocketAddress)localObject1);
/* 267 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 268 */       if (localSecurityManager != null) {
/*     */         try {
/* 270 */           localSecurityManager.checkAccept(((InetSocketAddress)localObject1).getAddress().getHostAddress(), ((InetSocketAddress)localObject1)
/* 271 */             .getPort());
/*     */         } catch (SecurityException localSecurityException) {
/* 273 */           localSocketChannelImpl.close();
/* 274 */           throw localSecurityException;
/*     */         }
/*     */       }
/* 277 */       return localSocketChannelImpl;
/*     */     }
/*     */   }
/*     */   
/*     */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException
/*     */   {
/* 283 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*     */   }
/*     */   
/*     */   protected void implCloseSelectableChannel() throws IOException {
/* 287 */     synchronized (this.stateLock) {
/* 288 */       if (this.state != 1)
/* 289 */         nd.preClose(this.fd);
/* 290 */       long l = this.thread;
/* 291 */       if (l != 0L)
/* 292 */         NativeThread.signal(l);
/* 293 */       if (!isRegistered())
/* 294 */         kill();
/*     */     }
/*     */   }
/*     */   
/*     */   public void kill() throws IOException {
/* 299 */     synchronized (this.stateLock) {
/* 300 */       if (this.state == 1)
/* 301 */         return;
/* 302 */       if (this.state == -1) {
/* 303 */         this.state = 1;
/* 304 */         return;
/*     */       }
/* 306 */       assert ((!isOpen()) && (!isRegistered()));
/* 307 */       nd.close(this.fd);
/* 308 */       this.state = 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 317 */     int i = paramSelectionKeyImpl.nioInterestOps();
/* 318 */     int j = paramSelectionKeyImpl.nioReadyOps();
/* 319 */     int k = paramInt2;
/*     */     
/* 321 */     if ((paramInt1 & Net.POLLNVAL) != 0)
/*     */     {
/*     */ 
/*     */ 
/* 325 */       return false;
/*     */     }
/*     */     
/* 328 */     if ((paramInt1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
/* 329 */       k = i;
/* 330 */       paramSelectionKeyImpl.nioReadyOps(k);
/* 331 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */     }
/*     */     
/* 334 */     if (((paramInt1 & Net.POLLIN) != 0) && ((i & 0x10) != 0))
/*     */     {
/* 336 */       k |= 0x10;
/*     */     }
/* 338 */     paramSelectionKeyImpl.nioReadyOps(k);
/* 339 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*     */   }
/*     */   
/*     */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 343 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*     */   }
/*     */   
/*     */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 347 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*     */   }
/*     */   
/*     */   int poll(int paramInt, long paramLong) throws IOException
/*     */   {
/* 352 */     assert ((Thread.holdsLock(blockingLock())) && (!isBlocking()));
/*     */     
/* 354 */     synchronized (this.lock) {
/* 355 */       int i = 0;
/*     */       try {
/* 357 */         begin();
/* 358 */         synchronized (this.stateLock) {
/* 359 */           if (!isOpen()) {
/* 360 */             int j = 0;
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 365 */             this.thread = 0L;
/* 366 */             end(i > 0);return j; }
/* 361 */           this.thread = NativeThread.current();
/*     */         }
/* 363 */         i = Net.poll(this.fd, paramInt, paramLong);
/*     */       } finally {
/* 365 */         this.thread = 0L;
/* 366 */         end(i > 0);
/*     */       }
/* 368 */       return i;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 376 */     int i = 0;
/*     */     
/*     */ 
/* 379 */     if ((paramInt & 0x10) != 0) {
/* 380 */       i |= Net.POLLIN;
/*     */     }
/* 382 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
/*     */   }
/*     */   
/*     */   public FileDescriptor getFD() {
/* 386 */     return this.fd;
/*     */   }
/*     */   
/*     */   public int getFDVal() {
/* 390 */     return this.fdVal;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 394 */     StringBuffer localStringBuffer = new StringBuffer();
/* 395 */     localStringBuffer.append(getClass().getName());
/* 396 */     localStringBuffer.append('[');
/* 397 */     if (!isOpen()) {
/* 398 */       localStringBuffer.append("closed");
/*     */     } else {
/* 400 */       synchronized (this.stateLock) {
/* 401 */         InetSocketAddress localInetSocketAddress = localAddress();
/* 402 */         if (localInetSocketAddress == null) {
/* 403 */           localStringBuffer.append("unbound");
/*     */         } else {
/* 405 */           localStringBuffer.append(Net.getRevealedLocalAddressAsString(localInetSocketAddress));
/*     */         }
/*     */       }
/*     */     }
/* 409 */     localStringBuffer.append(']');
/* 410 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int accept(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress)
/*     */     throws IOException
/*     */   {
/* 422 */     return accept0(paramFileDescriptor1, paramFileDescriptor2, paramArrayOfInetSocketAddress);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private native int accept0(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   private static native void initIDs();
/*     */   
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/* 439 */     IOUtil.load();
/* 440 */     initIDs(); }
/* 441 */   private static NativeDispatcher nd = new SocketDispatcher();
/*     */   private final FileDescriptor fd;
/*     */   private int fdVal;
/*     */   private static final int ST_UNINITIALIZED = -1;
/*     */   private static final int ST_INUSE = 0;
/*     */   private static final int ST_KILLED = 1;
/*     */   private InetSocketAddress localAddress;
/*     */   private boolean isReuseAddress;
/*     */   ServerSocket socket;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\ServerSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
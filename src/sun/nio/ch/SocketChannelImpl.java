/*      */ package sun.nio.ch;
/*      */ 
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketAddress;
/*      */ import java.net.SocketOption;
/*      */ import java.net.StandardProtocolFamily;
/*      */ import java.net.StandardSocketOptions;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.AlreadyBoundException;
/*      */ import java.nio.channels.AlreadyConnectedException;
/*      */ import java.nio.channels.AsynchronousCloseException;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.ConnectionPendingException;
/*      */ import java.nio.channels.NoConnectionPendingException;
/*      */ import java.nio.channels.NotYetConnectedException;
/*      */ import java.nio.channels.SocketChannel;
/*      */ import java.nio.channels.spi.SelectorProvider;
/*      */ import java.util.Collections;
/*      */ import java.util.HashSet;
/*      */ import java.util.Set;
/*      */ import jdk.net.ExtendedSocketOptions;
/*      */ import sun.net.ExtendedOptionsImpl;
/*      */ import sun.net.NetHooks;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class SocketChannelImpl
/*      */   extends SocketChannel
/*      */   implements SelChImpl
/*      */ {
/*   59 */   private volatile long readerThread = 0L;
/*   60 */   private volatile long writerThread = 0L;
/*      */   
/*      */ 
/*   63 */   private final Object readLock = new Object();
/*      */   
/*      */ 
/*   66 */   private final Object writeLock = new Object();
/*      */   
/*      */ 
/*      */ 
/*   70 */   private final Object stateLock = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   84 */   private int state = -1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   91 */   private boolean isInputOpen = true;
/*   92 */   private boolean isOutputOpen = true;
/*   93 */   private boolean readyToConnect = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   SocketChannelImpl(SelectorProvider paramSelectorProvider)
/*      */     throws IOException
/*      */   {
/*  104 */     super(paramSelectorProvider);
/*  105 */     this.fd = Net.socket(true);
/*  106 */     this.fdVal = IOUtil.fdVal(this.fd);
/*  107 */     this.state = 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  115 */     super(paramSelectorProvider);
/*  116 */     this.fd = paramFileDescriptor;
/*  117 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/*  118 */     this.state = 0;
/*  119 */     if (paramBoolean) {
/*  120 */       this.localAddress = Net.localAddress(paramFileDescriptor);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress)
/*      */     throws IOException
/*      */   {
/*  129 */     super(paramSelectorProvider);
/*  130 */     this.fd = paramFileDescriptor;
/*  131 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/*  132 */     this.state = 2;
/*  133 */     this.localAddress = Net.localAddress(paramFileDescriptor);
/*  134 */     this.remoteAddress = paramInetSocketAddress;
/*      */   }
/*      */   
/*      */   public Socket socket() {
/*  138 */     synchronized (this.stateLock) {
/*  139 */       if (this.socket == null)
/*  140 */         this.socket = SocketAdaptor.create(this);
/*  141 */       return this.socket;
/*      */     }
/*      */   }
/*      */   
/*      */   public SocketAddress getLocalAddress() throws IOException
/*      */   {
/*  147 */     synchronized (this.stateLock) {
/*  148 */       if (!isOpen())
/*  149 */         throw new ClosedChannelException();
/*  150 */       return Net.getRevealedLocalAddress(this.localAddress);
/*      */     }
/*      */   }
/*      */   
/*      */   public SocketAddress getRemoteAddress() throws IOException
/*      */   {
/*  156 */     synchronized (this.stateLock) {
/*  157 */       if (!isOpen())
/*  158 */         throw new ClosedChannelException();
/*  159 */       return this.remoteAddress;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> SocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
/*      */     throws IOException
/*      */   {
/*  167 */     if (paramSocketOption == null)
/*  168 */       throw new NullPointerException();
/*  169 */     if (!supportedOptions().contains(paramSocketOption)) {
/*  170 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*      */     }
/*  172 */     synchronized (this.stateLock) {
/*  173 */       if (!isOpen()) {
/*  174 */         throw new ClosedChannelException();
/*      */       }
/*  176 */       if (paramSocketOption == StandardSocketOptions.IP_TOS) {
/*  177 */         StandardProtocolFamily localStandardProtocolFamily = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
/*      */         
/*  179 */         Net.setSocketOption(this.fd, localStandardProtocolFamily, paramSocketOption, paramT);
/*  180 */         return this;
/*      */       }
/*      */       
/*  183 */       if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind()))
/*      */       {
/*  185 */         this.isReuseAddress = ((Boolean)paramT).booleanValue();
/*  186 */         return this;
/*      */       }
/*      */       
/*      */ 
/*  190 */       Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
/*  191 */       return this;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T getOption(SocketOption<T> paramSocketOption)
/*      */     throws IOException
/*      */   {
/*  200 */     if (paramSocketOption == null)
/*  201 */       throw new NullPointerException();
/*  202 */     if (!supportedOptions().contains(paramSocketOption)) {
/*  203 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*      */     }
/*  205 */     synchronized (this.stateLock) {
/*  206 */       if (!isOpen()) {
/*  207 */         throw new ClosedChannelException();
/*      */       }
/*  209 */       if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && 
/*  210 */         (Net.useExclusiveBind()))
/*      */       {
/*      */ 
/*  213 */         return Boolean.valueOf(this.isReuseAddress);
/*      */       }
/*      */       
/*      */ 
/*  217 */       if (paramSocketOption == StandardSocketOptions.IP_TOS) {
/*  218 */         StandardProtocolFamily localStandardProtocolFamily = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
/*      */         
/*  220 */         return (T)Net.getSocketOption(this.fd, localStandardProtocolFamily, paramSocketOption);
/*      */       }
/*      */       
/*      */ 
/*  224 */       return (T)Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class DefaultOptionsHolder {
/*  229 */     static final Set<SocketOption<?>> defaultOptions = ;
/*      */     
/*      */     private static Set<SocketOption<?>> defaultOptions() {
/*  232 */       HashSet localHashSet = new HashSet(8);
/*  233 */       localHashSet.add(StandardSocketOptions.SO_SNDBUF);
/*  234 */       localHashSet.add(StandardSocketOptions.SO_RCVBUF);
/*  235 */       localHashSet.add(StandardSocketOptions.SO_KEEPALIVE);
/*  236 */       localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
/*  237 */       localHashSet.add(StandardSocketOptions.SO_LINGER);
/*  238 */       localHashSet.add(StandardSocketOptions.TCP_NODELAY);
/*      */       
/*  240 */       localHashSet.add(StandardSocketOptions.IP_TOS);
/*  241 */       localHashSet.add(ExtendedSocketOption.SO_OOBINLINE);
/*  242 */       if (ExtendedOptionsImpl.flowSupported()) {
/*  243 */         localHashSet.add(ExtendedSocketOptions.SO_FLOW_SLA);
/*      */       }
/*  245 */       return Collections.unmodifiableSet(localHashSet);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Set<SocketOption<?>> supportedOptions()
/*      */   {
/*  251 */     return DefaultOptionsHolder.defaultOptions;
/*      */   }
/*      */   
/*      */   private boolean ensureReadOpen() throws ClosedChannelException {
/*  255 */     synchronized (this.stateLock) {
/*  256 */       if (!isOpen())
/*  257 */         throw new ClosedChannelException();
/*  258 */       if (!isConnected())
/*  259 */         throw new NotYetConnectedException();
/*  260 */       if (!this.isInputOpen) {
/*  261 */         return false;
/*      */       }
/*  263 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */   private void ensureWriteOpen() throws ClosedChannelException {
/*  268 */     synchronized (this.stateLock) {
/*  269 */       if (!isOpen())
/*  270 */         throw new ClosedChannelException();
/*  271 */       if (!this.isOutputOpen)
/*  272 */         throw new ClosedChannelException();
/*  273 */       if (!isConnected())
/*  274 */         throw new NotYetConnectedException();
/*      */     }
/*      */   }
/*      */   
/*      */   private void readerCleanup() throws IOException {
/*  279 */     synchronized (this.stateLock) {
/*  280 */       this.readerThread = 0L;
/*  281 */       if (this.state == 3)
/*  282 */         kill();
/*      */     }
/*      */   }
/*      */   
/*      */   private void writerCleanup() throws IOException {
/*  287 */     synchronized (this.stateLock) {
/*  288 */       this.writerThread = 0L;
/*  289 */       if (this.state == 3) {
/*  290 */         kill();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public int read(ByteBuffer paramByteBuffer) throws IOException {
/*  296 */     if (paramByteBuffer == null) {
/*  297 */       throw new NullPointerException();
/*      */     }
/*  299 */     synchronized (this.readLock) {
/*  300 */       if (!ensureReadOpen())
/*  301 */         return -1;
/*  302 */       int i = 0;
/*      */       
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  308 */         begin();
/*      */         
/*  310 */         synchronized (this.stateLock) {
/*  311 */           if (!isOpen())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  319 */             int k = 0;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  390 */             readerCleanup();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  407 */             end((i > 0) || (i == -2));
/*      */             
/*      */ 
/*      */ 
/*  411 */             synchronized (this.stateLock) {
/*  412 */               if ((i <= 0) && (!this.isInputOpen)) {
/*  413 */                 return -1;
/*      */               }
/*      */             }
/*  416 */             assert (IOStatus.check(i));return k;
/*      */           }
/*  326 */           this.readerThread = NativeThread.current();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         do
/*      */         {
/*  380 */           i = IOUtil.read(this.fd, paramByteBuffer, -1L, nd);
/*  381 */         } while ((i == -3) && (isOpen()));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  386 */         int j = IOStatus.normalize(i);
/*      */         
/*      */ 
/*      */ 
/*  390 */         readerCleanup();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  407 */         end((i > 0) || (i == -2));
/*      */         
/*      */ 
/*      */ 
/*  411 */         synchronized (this.stateLock) {
/*  412 */           if ((i <= 0) && (!this.isInputOpen)) {
/*  413 */             return -1;
/*      */           }
/*      */         }
/*  416 */         assert (IOStatus.check(i));return j;
/*      */       }
/*      */       finally
/*      */       {
/*  390 */         readerCleanup();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  407 */         end((i > 0) || (i == -2));
/*      */         
/*      */ 
/*      */ 
/*  411 */         synchronized (this.stateLock) {
/*  412 */           if ((i <= 0) && (!this.isInputOpen)) {
/*  413 */             return -1;
/*      */           }
/*      */         }
/*  416 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) { throw new AssertionError();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  425 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  426 */       throw new IndexOutOfBoundsException();
/*  427 */     synchronized (this.readLock) {
/*  428 */       if (!ensureReadOpen())
/*  429 */         return -1L;
/*  430 */       long l1 = 0L;
/*      */       try {
/*  432 */         begin();
/*  433 */         synchronized (this.stateLock) {
/*  434 */           if (!isOpen()) {
/*  435 */             long l3 = 0L;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  446 */             readerCleanup();
/*  447 */             end((l1 > 0L) || (l1 == -2L));
/*  448 */             synchronized (this.stateLock) {
/*  449 */               if ((l1 <= 0L) && (!this.isInputOpen))
/*  450 */                 return -1L;
/*      */             }
/*  452 */             assert (IOStatus.check(l1));return l3;
/*      */           }
/*  436 */           this.readerThread = NativeThread.current();
/*      */         }
/*      */         do
/*      */         {
/*  440 */           l1 = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
/*  441 */         } while ((l1 == -3L) && (isOpen()));
/*      */         
/*  443 */         long l2 = IOStatus.normalize(l1);
/*      */         
/*      */ 
/*  446 */         readerCleanup();
/*  447 */         end((l1 > 0L) || (l1 == -2L));
/*  448 */         synchronized (this.stateLock) {
/*  449 */           if ((l1 <= 0L) && (!this.isInputOpen))
/*  450 */             return -1L;
/*      */         }
/*  452 */         assert (IOStatus.check(l1));return l2;
/*      */       }
/*      */       finally
/*      */       {
/*  446 */         readerCleanup();
/*  447 */         end((l1 > 0L) || (l1 == -2L));
/*  448 */         synchronized (this.stateLock) {
/*  449 */           if ((l1 <= 0L) && (!this.isInputOpen))
/*  450 */             return -1L;
/*      */         }
/*  452 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/*  458 */     if (paramByteBuffer == null)
/*  459 */       throw new NullPointerException();
/*  460 */     synchronized (this.writeLock) {
/*  461 */       ensureWriteOpen();
/*  462 */       int i = 0;
/*      */       try {
/*  464 */         begin();
/*  465 */         synchronized (this.stateLock) {
/*  466 */           if (!isOpen()) {
/*  467 */             int k = 0;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  477 */             writerCleanup();
/*  478 */             end((i > 0) || (i == -2));
/*  479 */             synchronized (this.stateLock) {
/*  480 */               if ((i <= 0) && (!this.isOutputOpen)) {
/*  481 */                 throw new AsynchronousCloseException();
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  483 */             assert (IOStatus.check(i));return k;
/*      */           }
/*  468 */           this.writerThread = NativeThread.current();
/*      */         }
/*      */         do {
/*  471 */           i = IOUtil.write(this.fd, paramByteBuffer, -1L, nd);
/*  472 */         } while ((i == -3) && (isOpen()));
/*      */         
/*  474 */         int j = IOStatus.normalize(i);
/*      */         
/*      */ 
/*  477 */         writerCleanup();
/*  478 */         end((i > 0) || (i == -2));
/*  479 */         synchronized (this.stateLock) {
/*  480 */           if ((i <= 0) && (!this.isOutputOpen)) {
/*  481 */             throw new AsynchronousCloseException();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  483 */         assert (IOStatus.check(i));return j;
/*      */       }
/*      */       finally
/*      */       {
/*  477 */         writerCleanup();
/*  478 */         end((i > 0) || (i == -2));
/*  479 */         synchronized (this.stateLock) {
/*  480 */           if ((i <= 0) && (!this.isOutputOpen))
/*  481 */             throw new AsynchronousCloseException();
/*      */         }
/*  483 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  491 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  492 */       throw new IndexOutOfBoundsException();
/*  493 */     synchronized (this.writeLock) {
/*  494 */       ensureWriteOpen();
/*  495 */       long l1 = 0L;
/*      */       try {
/*  497 */         begin();
/*  498 */         synchronized (this.stateLock) {
/*  499 */           if (!isOpen()) {
/*  500 */             long l3 = 0L;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  510 */             writerCleanup();
/*  511 */             end((l1 > 0L) || (l1 == -2L));
/*  512 */             synchronized (this.stateLock) {
/*  513 */               if ((l1 <= 0L) && (!this.isOutputOpen)) {
/*  514 */                 throw new AsynchronousCloseException();
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  516 */             assert (IOStatus.check(l1));return l3;
/*      */           }
/*  501 */           this.writerThread = NativeThread.current();
/*      */         }
/*      */         do {
/*  504 */           l1 = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
/*  505 */         } while ((l1 == -3L) && (isOpen()));
/*      */         
/*  507 */         long l2 = IOStatus.normalize(l1);
/*      */         
/*      */ 
/*  510 */         writerCleanup();
/*  511 */         end((l1 > 0L) || (l1 == -2L));
/*  512 */         synchronized (this.stateLock) {
/*  513 */           if ((l1 <= 0L) && (!this.isOutputOpen)) {
/*  514 */             throw new AsynchronousCloseException();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  516 */         assert (IOStatus.check(l1));return l2;
/*      */       }
/*      */       finally
/*      */       {
/*  510 */         writerCleanup();
/*  511 */         end((l1 > 0L) || (l1 == -2L));
/*  512 */         synchronized (this.stateLock) {
/*  513 */           if ((l1 <= 0L) && (!this.isOutputOpen))
/*  514 */             throw new AsynchronousCloseException();
/*      */         }
/*  516 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   int sendOutOfBandData(byte paramByte) throws IOException
/*      */   {
/*  523 */     synchronized (this.writeLock) {
/*  524 */       ensureWriteOpen();
/*  525 */       int i = 0;
/*      */       try {
/*  527 */         begin();
/*  528 */         synchronized (this.stateLock) {
/*  529 */           if (!isOpen()) {
/*  530 */             int k = 0;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  540 */             writerCleanup();
/*  541 */             end((i > 0) || (i == -2));
/*  542 */             synchronized (this.stateLock) {
/*  543 */               if ((i <= 0) && (!this.isOutputOpen)) {
/*  544 */                 throw new AsynchronousCloseException();
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  546 */             assert (IOStatus.check(i));return k;
/*      */           }
/*  531 */           this.writerThread = NativeThread.current();
/*      */         }
/*      */         do {
/*  534 */           i = sendOutOfBandData(this.fd, paramByte);
/*  535 */         } while ((i == -3) && (isOpen()));
/*      */         
/*  537 */         int j = IOStatus.normalize(i);
/*      */         
/*      */ 
/*  540 */         writerCleanup();
/*  541 */         end((i > 0) || (i == -2));
/*  542 */         synchronized (this.stateLock) {
/*  543 */           if ((i <= 0) && (!this.isOutputOpen)) {
/*  544 */             throw new AsynchronousCloseException();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  546 */         assert (IOStatus.check(i));return j;
/*      */       }
/*      */       finally
/*      */       {
/*  540 */         writerCleanup();
/*  541 */         end((i > 0) || (i == -2));
/*  542 */         synchronized (this.stateLock) {
/*  543 */           if ((i <= 0) && (!this.isOutputOpen))
/*  544 */             throw new AsynchronousCloseException();
/*      */         }
/*  546 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException {
/*  552 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public InetSocketAddress localAddress()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 465	sun/nio/ch/SocketChannelImpl:stateLock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 467	sun/nio/ch/SocketChannelImpl:localAddress	Ljava/net/InetSocketAddress;
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: areturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #556	-> byte code offset #0
/*      */     //   Java source line #557	-> byte code offset #7
/*      */     //   Java source line #558	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	SocketChannelImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public SocketAddress remoteAddress()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 465	sun/nio/ch/SocketChannelImpl:stateLock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 468	sun/nio/ch/SocketChannelImpl:remoteAddress	Ljava/net/InetSocketAddress;
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: areturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #562	-> byte code offset #0
/*      */     //   Java source line #563	-> byte code offset #7
/*      */     //   Java source line #564	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	SocketChannelImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   public SocketChannel bind(SocketAddress paramSocketAddress)
/*      */     throws IOException
/*      */   {
/*  569 */     synchronized (this.readLock) {
/*  570 */       synchronized (this.writeLock) {
/*  571 */         synchronized (this.stateLock) {
/*  572 */           if (!isOpen())
/*  573 */             throw new ClosedChannelException();
/*  574 */           if (this.state == 1)
/*  575 */             throw new ConnectionPendingException();
/*  576 */           if (this.localAddress != null) {
/*  577 */             throw new AlreadyBoundException();
/*      */           }
/*  579 */           InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
/*  580 */           SecurityManager localSecurityManager = System.getSecurityManager();
/*  581 */           if (localSecurityManager != null) {
/*  582 */             localSecurityManager.checkListen(localInetSocketAddress.getPort());
/*      */           }
/*  584 */           NetHooks.beforeTcpBind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/*  585 */           Net.bind(this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/*  586 */           this.localAddress = Net.localAddress(this.fd);
/*      */         }
/*      */       }
/*      */     }
/*  590 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isConnected() {
/*  594 */     synchronized (this.stateLock) {
/*  595 */       return this.state == 2;
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isConnectionPending() {
/*  600 */     synchronized (this.stateLock) {
/*  601 */       return this.state == 1;
/*      */     }
/*      */   }
/*      */   
/*      */   void ensureOpenAndUnconnected() throws IOException {
/*  606 */     synchronized (this.stateLock) {
/*  607 */       if (!isOpen())
/*  608 */         throw new ClosedChannelException();
/*  609 */       if (this.state == 2)
/*  610 */         throw new AlreadyConnectedException();
/*  611 */       if (this.state == 1)
/*  612 */         throw new ConnectionPendingException();
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean connect(SocketAddress paramSocketAddress) throws IOException {
/*  617 */     int i = 0;
/*      */     
/*  619 */     synchronized (this.readLock) {
/*  620 */       synchronized (this.writeLock) {
/*  621 */         ensureOpenAndUnconnected();
/*  622 */         InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/*  623 */         SecurityManager localSecurityManager = System.getSecurityManager();
/*  624 */         if (localSecurityManager != null)
/*  625 */           localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress
/*  626 */             .getPort());
/*  627 */         synchronized (blockingLock()) {
/*  628 */           int j = 0;
/*      */           try {
/*      */             try {
/*  631 */               begin();
/*  632 */               synchronized (this.stateLock) {
/*  633 */                 if (!isOpen()) {
/*  634 */                   boolean bool = false;
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  658 */                   readerCleanup();
/*  659 */                   end((j > 0) || (j == -2));
/*  660 */                   assert (IOStatus.check(j));return bool;
/*      */                 }
/*      */                 
/*  637 */                 if (this.localAddress == null) {
/*  638 */                   NetHooks.beforeTcpConnect(this.fd, localInetSocketAddress
/*  639 */                     .getAddress(), localInetSocketAddress
/*  640 */                     .getPort());
/*      */                 }
/*  642 */                 this.readerThread = NativeThread.current();
/*      */               }
/*      */               for (;;) {
/*  645 */                 ??? = localInetSocketAddress.getAddress();
/*  646 */                 if (((InetAddress)???).isAnyLocalAddress())
/*  647 */                   ??? = InetAddress.getLocalHost();
/*  648 */                 j = Net.connect(this.fd, (InetAddress)???, localInetSocketAddress
/*      */                 
/*  650 */                   .getPort());
/*  651 */                 if ((j != -3) || 
/*  652 */                   (!isOpen())) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/*  658 */               readerCleanup();
/*  659 */               end((j > 0) || (j == -2));
/*  660 */               if ((!$assertionsDisabled) && (!IOStatus.check(j))) { throw new AssertionError();
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (IOException ???)
/*      */           {
/*  666 */             close();
/*  667 */             throw ((Throwable)???);
/*      */           }
/*  669 */           synchronized (this.stateLock) {
/*  670 */             this.remoteAddress = localInetSocketAddress;
/*  671 */             if (j > 0)
/*      */             {
/*      */ 
/*      */ 
/*  675 */               this.state = 2;
/*  676 */               if (isOpen())
/*  677 */                 this.localAddress = Net.localAddress(this.fd);
/*  678 */               return true;
/*      */             }
/*      */             
/*      */ 
/*  682 */             if (!isBlocking()) {
/*  683 */               this.state = 1;
/*      */             }
/*  685 */             else if (!$assertionsDisabled) throw new AssertionError();
/*      */           }
/*      */         }
/*  688 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean finishConnect() throws IOException {
/*  694 */     synchronized (this.readLock) {
/*  695 */       synchronized (this.writeLock) {
/*  696 */         synchronized (this.stateLock) {
/*  697 */           if (!isOpen())
/*  698 */             throw new ClosedChannelException();
/*  699 */           if (this.state == 2)
/*  700 */             return true;
/*  701 */           if (this.state != 1)
/*  702 */             throw new NoConnectionPendingException();
/*      */         }
/*  704 */         int i = 0;
/*      */         try {
/*      */           try {
/*  707 */             begin();
/*  708 */             synchronized (blockingLock()) {
/*  709 */               synchronized (this.stateLock) {
/*  710 */                 if (!isOpen()) {
/*  711 */                   boolean bool = false;
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  741 */                   synchronized (this.stateLock) {
/*  742 */                     this.readerThread = 0L;
/*  743 */                     if (this.state == 3) {
/*  744 */                       kill();
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  750 */                       i = 0;
/*      */                     }
/*      */                   }
/*  753 */                   end((i > 0) || (i == -2));
/*  754 */                   assert (IOStatus.check(i));return bool;
/*      */                 }
/*  713 */                 this.readerThread = NativeThread.current();
/*      */               }
/*  715 */               if (!isBlocking()) {
/*      */                 for (;;) {
/*  717 */                   i = checkConnect(this.fd, false, this.readyToConnect);
/*      */                   
/*  719 */                   if ((i != -3) || 
/*  720 */                     (!isOpen())) {
/*      */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               for (;;) {
/*  726 */                 i = checkConnect(this.fd, true, this.readyToConnect);
/*      */                 
/*  728 */                 if (i != 0)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  733 */                   if ((i != -3) || 
/*  734 */                     (!isOpen())) {
/*      */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           } finally {
/*  741 */             synchronized (this.stateLock) {
/*  742 */               this.readerThread = 0L;
/*  743 */               if (this.state == 3) {
/*  744 */                 kill();
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  750 */                 i = 0;
/*      */               }
/*      */             }
/*  753 */             end((i > 0) || (i == -2));
/*  754 */             if ((!$assertionsDisabled) && (!IOStatus.check(i))) { throw new AssertionError();
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (IOException localObject1)
/*      */         {
/*  760 */           close();
/*  761 */           throw ((Throwable)???);
/*      */         }
/*  763 */         if (i > 0) {
/*  764 */           synchronized (this.stateLock) {
/*  765 */             this.state = 2;
/*  766 */             if (isOpen())
/*  767 */               this.localAddress = Net.localAddress(this.fd);
/*      */           }
/*  769 */           return true;
/*      */         }
/*  771 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public SocketChannel shutdownInput() throws IOException
/*      */   {
/*  778 */     synchronized (this.stateLock) {
/*  779 */       if (!isOpen())
/*  780 */         throw new ClosedChannelException();
/*  781 */       if (!isConnected())
/*  782 */         throw new NotYetConnectedException();
/*  783 */       if (this.isInputOpen) {
/*  784 */         Net.shutdown(this.fd, 0);
/*  785 */         if (this.readerThread != 0L)
/*  786 */           NativeThread.signal(this.readerThread);
/*  787 */         this.isInputOpen = false;
/*      */       }
/*  789 */       return this;
/*      */     }
/*      */   }
/*      */   
/*      */   public SocketChannel shutdownOutput() throws IOException
/*      */   {
/*  795 */     synchronized (this.stateLock) {
/*  796 */       if (!isOpen())
/*  797 */         throw new ClosedChannelException();
/*  798 */       if (!isConnected())
/*  799 */         throw new NotYetConnectedException();
/*  800 */       if (this.isOutputOpen) {
/*  801 */         Net.shutdown(this.fd, 1);
/*  802 */         if (this.writerThread != 0L)
/*  803 */           NativeThread.signal(this.writerThread);
/*  804 */         this.isOutputOpen = false;
/*      */       }
/*  806 */       return this;
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isInputOpen()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 465	sun/nio/ch/SocketChannelImpl:stateLock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 459	sun/nio/ch/SocketChannelImpl:isInputOpen	Z
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: ireturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #811	-> byte code offset #0
/*      */     //   Java source line #812	-> byte code offset #7
/*      */     //   Java source line #813	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	SocketChannelImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isOutputOpen()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 465	sun/nio/ch/SocketChannelImpl:stateLock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 460	sun/nio/ch/SocketChannelImpl:isOutputOpen	Z
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: ireturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #817	-> byte code offset #0
/*      */     //   Java source line #818	-> byte code offset #7
/*      */     //   Java source line #819	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	SocketChannelImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   protected void implCloseSelectableChannel()
/*      */     throws IOException
/*      */   {
/*  828 */     synchronized (this.stateLock) {
/*  829 */       this.isInputOpen = false;
/*  830 */       this.isOutputOpen = false;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  837 */       if (this.state != 4) {
/*  838 */         nd.preClose(this.fd);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  844 */       if (this.readerThread != 0L) {
/*  845 */         NativeThread.signal(this.readerThread);
/*      */       }
/*  847 */       if (this.writerThread != 0L) {
/*  848 */         NativeThread.signal(this.writerThread);
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
/*  860 */       if (!isRegistered())
/*  861 */         kill();
/*      */     }
/*      */   }
/*      */   
/*      */   public void kill() throws IOException {
/*  866 */     synchronized (this.stateLock) {
/*  867 */       if (this.state == 4)
/*  868 */         return;
/*  869 */       if (this.state == -1) {
/*  870 */         this.state = 4;
/*  871 */         return;
/*      */       }
/*  873 */       assert ((!isOpen()) && (!isRegistered()));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  878 */       if ((this.readerThread == 0L) && (this.writerThread == 0L)) {
/*  879 */         nd.close(this.fd);
/*  880 */         this.state = 4;
/*      */       } else {
/*  882 */         this.state = 3;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/*  892 */     int i = paramSelectionKeyImpl.nioInterestOps();
/*  893 */     int j = paramSelectionKeyImpl.nioReadyOps();
/*  894 */     int k = paramInt2;
/*      */     
/*  896 */     if ((paramInt1 & Net.POLLNVAL) != 0)
/*      */     {
/*      */ 
/*      */ 
/*  900 */       return false;
/*      */     }
/*      */     
/*  903 */     if ((paramInt1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
/*  904 */       k = i;
/*  905 */       paramSelectionKeyImpl.nioReadyOps(k);
/*      */       
/*      */ 
/*  908 */       this.readyToConnect = true;
/*  909 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*      */     }
/*      */     
/*  912 */     if (((paramInt1 & Net.POLLIN) != 0) && ((i & 0x1) != 0) && (this.state == 2))
/*      */     {
/*      */ 
/*  915 */       k |= 0x1;
/*      */     }
/*  917 */     if (((paramInt1 & Net.POLLCONN) != 0) && ((i & 0x8) != 0) && ((this.state == 0) || (this.state == 1)))
/*      */     {
/*      */ 
/*  920 */       k |= 0x8;
/*  921 */       this.readyToConnect = true;
/*      */     }
/*      */     
/*  924 */     if (((paramInt1 & Net.POLLOUT) != 0) && ((i & 0x4) != 0) && (this.state == 2))
/*      */     {
/*      */ 
/*  927 */       k |= 0x4;
/*      */     }
/*  929 */     paramSelectionKeyImpl.nioReadyOps(k);
/*  930 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*      */   }
/*      */   
/*      */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/*  934 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*      */   }
/*      */   
/*      */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/*  938 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*      */   }
/*      */   
/*      */   int poll(int paramInt, long paramLong) throws IOException
/*      */   {
/*  943 */     assert ((Thread.holdsLock(blockingLock())) && (!isBlocking()));
/*      */     
/*  945 */     synchronized (this.readLock) {
/*  946 */       int i = 0;
/*      */       try {
/*  948 */         begin();
/*  949 */         synchronized (this.stateLock) {
/*  950 */           if (!isOpen()) {
/*  951 */             int j = 0;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  956 */             readerCleanup();
/*  957 */             end(i > 0);return j; }
/*  952 */           this.readerThread = NativeThread.current();
/*      */         }
/*  954 */         i = Net.poll(this.fd, paramInt, paramLong);
/*      */       } finally {
/*  956 */         readerCleanup();
/*  957 */         end(i > 0);
/*      */       }
/*  959 */       return i;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/*  967 */     int i = 0;
/*  968 */     if ((paramInt & 0x1) != 0)
/*  969 */       i |= Net.POLLIN;
/*  970 */     if ((paramInt & 0x4) != 0)
/*  971 */       i |= Net.POLLOUT;
/*  972 */     if ((paramInt & 0x8) != 0)
/*  973 */       i |= Net.POLLCONN;
/*  974 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
/*      */   }
/*      */   
/*      */   public FileDescriptor getFD() {
/*  978 */     return this.fd;
/*      */   }
/*      */   
/*      */   public int getFDVal() {
/*  982 */     return this.fdVal;
/*      */   }
/*      */   
/*      */   public String toString()
/*      */   {
/*  987 */     StringBuffer localStringBuffer = new StringBuffer();
/*  988 */     localStringBuffer.append(getClass().getSuperclass().getName());
/*  989 */     localStringBuffer.append('[');
/*  990 */     if (!isOpen()) {
/*  991 */       localStringBuffer.append("closed");
/*      */     } else {
/*  993 */       synchronized (this.stateLock) {
/*  994 */         switch (this.state) {
/*      */         case 0: 
/*  996 */           localStringBuffer.append("unconnected");
/*  997 */           break;
/*      */         case 1: 
/*  999 */           localStringBuffer.append("connection-pending");
/* 1000 */           break;
/*      */         case 2: 
/* 1002 */           localStringBuffer.append("connected");
/* 1003 */           if (!this.isInputOpen)
/* 1004 */             localStringBuffer.append(" ishut");
/* 1005 */           if (!this.isOutputOpen)
/* 1006 */             localStringBuffer.append(" oshut");
/*      */           break;
/*      */         }
/* 1009 */         InetSocketAddress localInetSocketAddress = localAddress();
/* 1010 */         if (localInetSocketAddress != null) {
/* 1011 */           localStringBuffer.append(" local=");
/* 1012 */           localStringBuffer.append(Net.getRevealedLocalAddressAsString(localInetSocketAddress));
/*      */         }
/* 1014 */         if (remoteAddress() != null) {
/* 1015 */           localStringBuffer.append(" remote=");
/* 1016 */           localStringBuffer.append(remoteAddress().toString());
/*      */         }
/*      */       }
/*      */     }
/* 1020 */     localStringBuffer.append(']');
/* 1021 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static native int checkConnect(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws IOException;
/*      */   
/*      */ 
/*      */ 
/*      */   private static native int sendOutOfBandData(FileDescriptor paramFileDescriptor, byte paramByte)
/*      */     throws IOException;
/*      */   
/*      */ 
/* 1035 */   static { IOUtil.load(); }
/* 1036 */   private static NativeDispatcher nd = new SocketDispatcher();
/*      */   private final FileDescriptor fd;
/*      */   private final int fdVal;
/*      */   private boolean isReuseAddress;
/*      */   private static final int ST_UNINITIALIZED = -1;
/*      */   private static final int ST_UNCONNECTED = 0;
/*      */   private static final int ST_PENDING = 1;
/*      */   private static final int ST_CONNECTED = 2;
/*      */   private static final int ST_KILLPENDING = 3;
/*      */   private static final int ST_KILLED = 4;
/*      */   private InetSocketAddress localAddress;
/*      */   private InetSocketAddress remoteAddress;
/*      */   private Socket socket;
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\SocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
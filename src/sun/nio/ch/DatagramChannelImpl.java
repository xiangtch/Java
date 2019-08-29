/*      */ package sun.nio.ch;
/*      */ 
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.IOException;
/*      */ import java.net.DatagramSocket;
/*      */ import java.net.Inet4Address;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NetworkInterface;
/*      */ import java.net.PortUnreachableException;
/*      */ import java.net.ProtocolFamily;
/*      */ import java.net.SocketAddress;
/*      */ import java.net.SocketOption;
/*      */ import java.net.StandardProtocolFamily;
/*      */ import java.net.StandardSocketOptions;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.AlreadyBoundException;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.DatagramChannel;
/*      */ import java.nio.channels.MembershipKey;
/*      */ import java.nio.channels.NotYetConnectedException;
/*      */ import java.nio.channels.UnsupportedAddressTypeException;
/*      */ import java.nio.channels.spi.SelectorProvider;
/*      */ import java.util.Collections;
/*      */ import java.util.HashSet;
/*      */ import java.util.Set;
/*      */ import jdk.net.ExtendedSocketOptions;
/*      */ import sun.net.ExtendedOptionsImpl;
/*      */ import sun.net.ResourceManager;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class DatagramChannelImpl
/*      */   extends DatagramChannel
/*      */   implements SelChImpl
/*      */ {
/*      */   private static NativeDispatcher nd;
/*      */   private final FileDescriptor fd;
/*      */   private final int fdVal;
/*      */   private final ProtocolFamily family;
/*   61 */   private volatile long readerThread = 0L;
/*   62 */   private volatile long writerThread = 0L;
/*      */   
/*      */ 
/*      */   private InetAddress cachedSenderInetAddress;
/*      */   
/*      */ 
/*      */   private int cachedSenderPort;
/*      */   
/*   70 */   private final Object readLock = new Object();
/*      */   
/*      */ 
/*   73 */   private final Object writeLock = new Object();
/*      */   
/*      */ 
/*      */ 
/*   77 */   private final Object stateLock = new Object();
/*      */   
/*      */   private static final int ST_UNINITIALIZED = -1;
/*      */   
/*      */   private static final int ST_UNCONNECTED = 0;
/*      */   
/*      */   private static final int ST_CONNECTED = 1;
/*      */   
/*      */   private static final int ST_KILLED = 2;
/*   86 */   private int state = -1;
/*      */   
/*      */ 
/*      */   private InetSocketAddress localAddress;
/*      */   
/*      */ 
/*      */   private InetSocketAddress remoteAddress;
/*      */   
/*      */ 
/*      */   private DatagramSocket socket;
/*      */   
/*      */ 
/*      */   private MembershipRegistry registry;
/*      */   
/*      */   private boolean reuseAddressEmulated;
/*      */   
/*      */   private boolean isReuseAddress;
/*      */   
/*      */   private SocketAddress sender;
/*      */   
/*      */ 
/*      */   public DatagramChannelImpl(SelectorProvider paramSelectorProvider)
/*      */     throws IOException
/*      */   {
/*  110 */     super(paramSelectorProvider);
/*  111 */     ResourceManager.beforeUdpCreate();
/*      */     try {
/*  113 */       this.family = (Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET);
/*      */       
/*  115 */       this.fd = Net.socket(this.family, false);
/*  116 */       this.fdVal = IOUtil.fdVal(this.fd);
/*  117 */       this.state = 0;
/*      */     } catch (IOException localIOException) {
/*  119 */       ResourceManager.afterUdpClose();
/*  120 */       throw localIOException;
/*      */     }
/*      */   }
/*      */   
/*      */   public DatagramChannelImpl(SelectorProvider paramSelectorProvider, ProtocolFamily paramProtocolFamily)
/*      */     throws IOException
/*      */   {
/*  127 */     super(paramSelectorProvider);
/*  128 */     if ((paramProtocolFamily != StandardProtocolFamily.INET) && (paramProtocolFamily != StandardProtocolFamily.INET6))
/*      */     {
/*      */ 
/*  131 */       if (paramProtocolFamily == null) {
/*  132 */         throw new NullPointerException("'family' is null");
/*      */       }
/*  134 */       throw new UnsupportedOperationException("Protocol family not supported");
/*      */     }
/*  136 */     if ((paramProtocolFamily == StandardProtocolFamily.INET6) && 
/*  137 */       (!Net.isIPv6Available())) {
/*  138 */       throw new UnsupportedOperationException("IPv6 not available");
/*      */     }
/*      */     
/*  141 */     this.family = paramProtocolFamily;
/*  142 */     this.fd = Net.socket(paramProtocolFamily, false);
/*  143 */     this.fdVal = IOUtil.fdVal(this.fd);
/*  144 */     this.state = 0;
/*      */   }
/*      */   
/*      */   public DatagramChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor)
/*      */     throws IOException
/*      */   {
/*  150 */     super(paramSelectorProvider);
/*  151 */     this.family = (Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET);
/*      */     
/*  153 */     this.fd = paramFileDescriptor;
/*  154 */     this.fdVal = IOUtil.fdVal(paramFileDescriptor);
/*  155 */     this.state = 0;
/*  156 */     this.localAddress = Net.localAddress(paramFileDescriptor);
/*      */   }
/*      */   
/*      */   public DatagramSocket socket() {
/*  160 */     synchronized (this.stateLock) {
/*  161 */       if (this.socket == null)
/*  162 */         this.socket = DatagramSocketAdaptor.create(this);
/*  163 */       return this.socket;
/*      */     }
/*      */   }
/*      */   
/*      */   public SocketAddress getLocalAddress() throws IOException
/*      */   {
/*  169 */     synchronized (this.stateLock) {
/*  170 */       if (!isOpen()) {
/*  171 */         throw new ClosedChannelException();
/*      */       }
/*  173 */       return Net.getRevealedLocalAddress(this.localAddress);
/*      */     }
/*      */   }
/*      */   
/*      */   public SocketAddress getRemoteAddress() throws IOException
/*      */   {
/*  179 */     synchronized (this.stateLock) {
/*  180 */       if (!isOpen())
/*  181 */         throw new ClosedChannelException();
/*  182 */       return this.remoteAddress;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> DatagramChannel setOption(SocketOption<T> paramSocketOption, T paramT)
/*      */     throws IOException
/*      */   {
/*  190 */     if (paramSocketOption == null)
/*  191 */       throw new NullPointerException();
/*  192 */     if (!supportedOptions().contains(paramSocketOption)) {
/*  193 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*      */     }
/*  195 */     synchronized (this.stateLock) {
/*  196 */       ensureOpen();
/*      */       
/*  198 */       if ((paramSocketOption == StandardSocketOptions.IP_TOS) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  203 */         Net.setSocketOption(this.fd, this.family, paramSocketOption, paramT);
/*  204 */         return this;
/*      */       }
/*      */       
/*  207 */       if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF) {
/*  208 */         if (paramT == null)
/*  209 */           throw new IllegalArgumentException("Cannot set IP_MULTICAST_IF to 'null'");
/*  210 */         NetworkInterface localNetworkInterface = (NetworkInterface)paramT;
/*  211 */         if (this.family == StandardProtocolFamily.INET6) {
/*  212 */           int i = localNetworkInterface.getIndex();
/*  213 */           if (i == -1)
/*  214 */             throw new IOException("Network interface cannot be identified");
/*  215 */           Net.setInterface6(this.fd, i);
/*      */         }
/*      */         else {
/*  218 */           Inet4Address localInet4Address = Net.anyInet4Address(localNetworkInterface);
/*  219 */           if (localInet4Address == null)
/*  220 */             throw new IOException("Network interface not configured for IPv4");
/*  221 */           int j = Net.inet4AsInt(localInet4Address);
/*  222 */           Net.setInterface4(this.fd, j);
/*      */         }
/*  224 */         return this;
/*      */       }
/*  226 */       if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && 
/*  227 */         (Net.useExclusiveBind()) && (this.localAddress != null))
/*      */       {
/*  229 */         this.reuseAddressEmulated = true;
/*  230 */         this.isReuseAddress = ((Boolean)paramT).booleanValue();
/*      */       }
/*      */       
/*      */ 
/*  234 */       Net.setSocketOption(this.fd, Net.UNSPEC, paramSocketOption, paramT);
/*  235 */       return this;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T getOption(SocketOption<T> paramSocketOption)
/*      */     throws IOException
/*      */   {
/*  244 */     if (paramSocketOption == null)
/*  245 */       throw new NullPointerException();
/*  246 */     if (!supportedOptions().contains(paramSocketOption)) {
/*  247 */       throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
/*      */     }
/*  249 */     synchronized (this.stateLock) {
/*  250 */       ensureOpen();
/*      */       
/*  252 */       if ((paramSocketOption == StandardSocketOptions.IP_TOS) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL) || (paramSocketOption == StandardSocketOptions.IP_MULTICAST_LOOP))
/*      */       {
/*      */ 
/*      */ 
/*  256 */         return (T)Net.getSocketOption(this.fd, this.family, paramSocketOption);
/*      */       }
/*      */       
/*  259 */       if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_IF) {
/*  260 */         if (this.family == StandardProtocolFamily.INET) {
/*  261 */           i = Net.getInterface4(this.fd);
/*  262 */           if (i == 0) {
/*  263 */             return null;
/*      */           }
/*  265 */           localObject1 = Net.inet4FromInt(i);
/*  266 */           NetworkInterface localNetworkInterface = NetworkInterface.getByInetAddress((InetAddress)localObject1);
/*  267 */           if (localNetworkInterface == null)
/*  268 */             throw new IOException("Unable to map address to interface");
/*  269 */           return localNetworkInterface;
/*      */         }
/*  271 */         int i = Net.getInterface6(this.fd);
/*  272 */         if (i == 0) {
/*  273 */           return null;
/*      */         }
/*  275 */         Object localObject1 = NetworkInterface.getByIndex(i);
/*  276 */         if (localObject1 == null)
/*  277 */           throw new IOException("Unable to map index to interface");
/*  278 */         return (T)localObject1;
/*      */       }
/*      */       
/*      */ 
/*  282 */       if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (this.reuseAddressEmulated))
/*      */       {
/*      */ 
/*  285 */         return Boolean.valueOf(this.isReuseAddress);
/*      */       }
/*      */       
/*      */ 
/*  289 */       return (T)Net.getSocketOption(this.fd, Net.UNSPEC, paramSocketOption);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class DefaultOptionsHolder {
/*  294 */     static final Set<SocketOption<?>> defaultOptions = ;
/*      */     
/*      */     private static Set<SocketOption<?>> defaultOptions() {
/*  297 */       HashSet localHashSet = new HashSet(8);
/*  298 */       localHashSet.add(StandardSocketOptions.SO_SNDBUF);
/*  299 */       localHashSet.add(StandardSocketOptions.SO_RCVBUF);
/*  300 */       localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
/*  301 */       localHashSet.add(StandardSocketOptions.SO_BROADCAST);
/*  302 */       localHashSet.add(StandardSocketOptions.IP_TOS);
/*  303 */       localHashSet.add(StandardSocketOptions.IP_MULTICAST_IF);
/*  304 */       localHashSet.add(StandardSocketOptions.IP_MULTICAST_TTL);
/*  305 */       localHashSet.add(StandardSocketOptions.IP_MULTICAST_LOOP);
/*  306 */       if (ExtendedOptionsImpl.flowSupported()) {
/*  307 */         localHashSet.add(ExtendedSocketOptions.SO_FLOW_SLA);
/*      */       }
/*  309 */       return Collections.unmodifiableSet(localHashSet);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Set<SocketOption<?>> supportedOptions()
/*      */   {
/*  315 */     return DefaultOptionsHolder.defaultOptions;
/*      */   }
/*      */   
/*      */   private void ensureOpen() throws ClosedChannelException {
/*  319 */     if (!isOpen()) {
/*  320 */       throw new ClosedChannelException();
/*      */     }
/*      */   }
/*      */   
/*      */   public SocketAddress receive(ByteBuffer paramByteBuffer) throws IOException
/*      */   {
/*  326 */     if (paramByteBuffer.isReadOnly())
/*  327 */       throw new IllegalArgumentException("Read-only buffer");
/*  328 */     if (paramByteBuffer == null)
/*  329 */       throw new NullPointerException();
/*  330 */     synchronized (this.readLock) {
/*  331 */       ensureOpen();
/*      */       
/*  333 */       if (localAddress() == null)
/*  334 */         bind(null);
/*  335 */       int i = 0;
/*  336 */       ByteBuffer localByteBuffer = null;
/*      */       try {
/*  338 */         begin();
/*  339 */         if (!isOpen()) {
/*  340 */           localObject1 = null;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  375 */           if (localByteBuffer != null)
/*  376 */             Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  377 */           this.readerThread = 0L;
/*  378 */           end((i > 0) || (i == -2));
/*  379 */           assert (IOStatus.check(i));return (SocketAddress)localObject1;
/*      */         }
/*  341 */         Object localObject1 = System.getSecurityManager();
/*  342 */         this.readerThread = NativeThread.current();
/*  343 */         if ((isConnected()) || (localObject1 == null)) {
/*      */           do {
/*  345 */             i = receive(this.fd, paramByteBuffer);
/*  346 */           } while ((i == -3) && (isOpen()));
/*  347 */           if (i == -2) {
/*  348 */             localObject2 = null;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  375 */             if (localByteBuffer != null)
/*  376 */               Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  377 */             this.readerThread = 0L;
/*  378 */             end((i > 0) || (i == -2));
/*  379 */             assert (IOStatus.check(i));return (SocketAddress)localObject2;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  350 */           localByteBuffer = Util.getTemporaryDirectBuffer(paramByteBuffer.remaining());
/*      */           for (;;)
/*      */           {
/*  353 */             i = receive(this.fd, localByteBuffer);
/*  354 */             if ((i != -3) || (!isOpen())) {
/*  355 */               if (i == -2) {
/*  356 */                 localObject2 = null;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  375 */                 if (localByteBuffer != null)
/*  376 */                   Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  377 */                 this.readerThread = 0L;
/*  378 */                 end((i > 0) || (i == -2));
/*  379 */                 assert (IOStatus.check(i));return (SocketAddress)localObject2;
/*      */               }
/*  357 */               localObject2 = (InetSocketAddress)this.sender;
/*      */               try {
/*  359 */                 ((SecurityManager)localObject1).checkAccept(((InetSocketAddress)localObject2)
/*  360 */                   .getAddress().getHostAddress(), ((InetSocketAddress)localObject2)
/*  361 */                   .getPort());
/*      */               }
/*      */               catch (SecurityException localSecurityException) {
/*  364 */                 localByteBuffer.clear();
/*  365 */                 i = 0;
/*      */               }
/*      */             } }
/*  368 */           localByteBuffer.flip();
/*  369 */           paramByteBuffer.put(localByteBuffer);
/*      */         }
/*      */         
/*      */ 
/*  373 */         Object localObject2 = this.sender;
/*      */         
/*  375 */         if (localByteBuffer != null)
/*  376 */           Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  377 */         this.readerThread = 0L;
/*  378 */         end((i > 0) || (i == -2));
/*  379 */         assert (IOStatus.check(i));return (SocketAddress)localObject2;
/*      */       }
/*      */       finally
/*      */       {
/*  375 */         if (localByteBuffer != null)
/*  376 */           Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*  377 */         this.readerThread = 0L;
/*  378 */         end((i > 0) || (i == -2));
/*  379 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private int receive(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer)
/*      */     throws IOException
/*      */   {
/*  387 */     int i = paramByteBuffer.position();
/*  388 */     int j = paramByteBuffer.limit();
/*  389 */     assert (i <= j);
/*  390 */     int k = i <= j ? j - i : 0;
/*  391 */     if (((paramByteBuffer instanceof DirectBuffer)) && (k > 0)) {
/*  392 */       return receiveIntoNativeBuffer(paramFileDescriptor, paramByteBuffer, k, i);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  397 */     int m = Math.max(k, 1);
/*  398 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(m);
/*      */     try {
/*  400 */       int n = receiveIntoNativeBuffer(paramFileDescriptor, localByteBuffer, m, 0);
/*  401 */       localByteBuffer.flip();
/*  402 */       if ((n > 0) && (k > 0))
/*  403 */         paramByteBuffer.put(localByteBuffer);
/*  404 */       return n;
/*      */     } finally {
/*  406 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private int receiveIntoNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  414 */     int i = receive0(paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + paramInt2, paramInt1, 
/*  415 */       isConnected());
/*  416 */     if (i > 0)
/*  417 */       paramByteBuffer.position(paramInt2 + i);
/*  418 */     return i;
/*      */   }
/*      */   
/*      */   public int send(ByteBuffer paramByteBuffer, SocketAddress paramSocketAddress)
/*      */     throws IOException
/*      */   {
/*  424 */     if (paramByteBuffer == null) {
/*  425 */       throw new NullPointerException();
/*      */     }
/*  427 */     synchronized (this.writeLock) {
/*  428 */       ensureOpen();
/*  429 */       InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/*  430 */       InetAddress localInetAddress = localInetSocketAddress.getAddress();
/*  431 */       if (localInetAddress == null)
/*  432 */         throw new IOException("Target address not resolved");
/*  433 */       synchronized (this.stateLock) {
/*  434 */         if (!isConnected()) {
/*  435 */           if (paramSocketAddress == null)
/*  436 */             throw new NullPointerException();
/*  437 */           SecurityManager localSecurityManager = System.getSecurityManager();
/*  438 */           if (localSecurityManager != null) {
/*  439 */             if (localInetAddress.isMulticastAddress()) {
/*  440 */               localSecurityManager.checkMulticast(localInetAddress);
/*      */             } else {
/*  442 */               localSecurityManager.checkConnect(localInetAddress.getHostAddress(), localInetSocketAddress
/*  443 */                 .getPort());
/*      */             }
/*      */           }
/*      */         } else {
/*  447 */           if (!paramSocketAddress.equals(this.remoteAddress)) {
/*  448 */             throw new IllegalArgumentException("Connected address not equal to target address");
/*      */           }
/*      */           
/*  451 */           return write(paramByteBuffer);
/*      */         }
/*      */       }
/*      */       
/*  455 */       int i = 0;
/*      */       try {
/*  457 */         begin();
/*  458 */         if (!isOpen()) {
/*  459 */           int j = 0;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  472 */           this.writerThread = 0L;
/*  473 */           end((i > 0) || (i == -2));
/*  474 */           assert (IOStatus.check(i));return j; }
/*  460 */         this.writerThread = NativeThread.current();
/*      */         do {
/*  462 */           i = send(this.fd, paramByteBuffer, localInetSocketAddress);
/*  463 */         } while ((i == -3) && (isOpen()));
/*      */         
/*  465 */         synchronized (this.stateLock) {
/*  466 */           if ((isOpen()) && (this.localAddress == null)) {
/*  467 */             this.localAddress = Net.localAddress(this.fd);
/*      */           }
/*      */         }
/*  470 */         int k = IOStatus.normalize(i);
/*      */         
/*  472 */         this.writerThread = 0L;
/*  473 */         end((i > 0) || (i == -2));
/*  474 */         assert (IOStatus.check(i));return k;
/*      */       } finally {
/*  472 */         this.writerThread = 0L;
/*  473 */         end((i > 0) || (i == -2));
/*  474 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private int send(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, InetSocketAddress paramInetSocketAddress)
/*      */     throws IOException
/*      */   {
/*  482 */     if ((paramByteBuffer instanceof DirectBuffer)) {
/*  483 */       return sendFromNativeBuffer(paramFileDescriptor, paramByteBuffer, paramInetSocketAddress);
/*      */     }
/*      */     
/*  486 */     int i = paramByteBuffer.position();
/*  487 */     int j = paramByteBuffer.limit();
/*  488 */     assert (i <= j);
/*  489 */     int k = i <= j ? j - i : 0;
/*      */     
/*  491 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(k);
/*      */     try {
/*  493 */       localByteBuffer.put(paramByteBuffer);
/*  494 */       localByteBuffer.flip();
/*      */       
/*  496 */       paramByteBuffer.position(i);
/*      */       
/*  498 */       int m = sendFromNativeBuffer(paramFileDescriptor, localByteBuffer, paramInetSocketAddress);
/*  499 */       if (m > 0)
/*      */       {
/*  501 */         paramByteBuffer.position(i + m);
/*      */       }
/*  503 */       return m;
/*      */     } finally {
/*  505 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private int sendFromNativeBuffer(FileDescriptor paramFileDescriptor, ByteBuffer paramByteBuffer, InetSocketAddress paramInetSocketAddress)
/*      */     throws IOException
/*      */   {
/*  513 */     int i = paramByteBuffer.position();
/*  514 */     int j = paramByteBuffer.limit();
/*  515 */     assert (i <= j);
/*  516 */     int k = i <= j ? j - i : 0;
/*      */     
/*  518 */     boolean bool = this.family != StandardProtocolFamily.INET;
/*      */     int m;
/*      */     try {
/*  521 */       m = send0(bool, paramFileDescriptor, ((DirectBuffer)paramByteBuffer).address() + i, k, paramInetSocketAddress
/*  522 */         .getAddress(), paramInetSocketAddress.getPort());
/*      */     } catch (PortUnreachableException localPortUnreachableException) {
/*  524 */       if (isConnected())
/*  525 */         throw localPortUnreachableException;
/*  526 */       m = k;
/*      */     }
/*  528 */     if (m > 0)
/*  529 */       paramByteBuffer.position(i + m);
/*  530 */     return m;
/*      */   }
/*      */   
/*      */   public int read(ByteBuffer paramByteBuffer) throws IOException {
/*  534 */     if (paramByteBuffer == null)
/*  535 */       throw new NullPointerException();
/*  536 */     synchronized (this.readLock) {
/*  537 */       synchronized (this.stateLock) {
/*  538 */         ensureOpen();
/*  539 */         if (!isConnected())
/*  540 */           throw new NotYetConnectedException();
/*      */       }
/*  542 */       int i = 0;
/*      */       try {
/*  544 */         begin();
/*  545 */         if (!isOpen()) {
/*  546 */           j = 0;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  553 */           this.readerThread = 0L;
/*  554 */           end((i > 0) || (i == -2));
/*  555 */           assert (IOStatus.check(i));return j; }
/*  547 */         this.readerThread = NativeThread.current();
/*      */         do {
/*  549 */           i = IOUtil.read(this.fd, paramByteBuffer, -1L, nd);
/*  550 */         } while ((i == -3) && (isOpen()));
/*  551 */         int j = IOStatus.normalize(i);
/*      */         
/*  553 */         this.readerThread = 0L;
/*  554 */         end((i > 0) || (i == -2));
/*  555 */         assert (IOStatus.check(i));return j;
/*      */       } finally {
/*  553 */         this.readerThread = 0L;
/*  554 */         end((i > 0) || (i == -2));
/*  555 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  563 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  564 */       throw new IndexOutOfBoundsException();
/*  565 */     synchronized (this.readLock) {
/*  566 */       synchronized (this.stateLock) {
/*  567 */         ensureOpen();
/*  568 */         if (!isConnected())
/*  569 */           throw new NotYetConnectedException();
/*      */       }
/*  571 */       long l1 = 0L;
/*      */       try {
/*  573 */         begin();
/*  574 */         if (!isOpen()) {
/*  575 */           l2 = 0L;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  582 */           this.readerThread = 0L;
/*  583 */           end((l1 > 0L) || (l1 == -2L));
/*  584 */           assert (IOStatus.check(l1));return l2; }
/*  576 */         this.readerThread = NativeThread.current();
/*      */         do {
/*  578 */           l1 = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
/*  579 */         } while ((l1 == -3L) && (isOpen()));
/*  580 */         long l2 = IOStatus.normalize(l1);
/*      */         
/*  582 */         this.readerThread = 0L;
/*  583 */         end((l1 > 0L) || (l1 == -2L));
/*  584 */         assert (IOStatus.check(l1));return l2;
/*      */       } finally {
/*  582 */         this.readerThread = 0L;
/*  583 */         end((l1 > 0L) || (l1 == -2L));
/*  584 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/*  590 */     if (paramByteBuffer == null)
/*  591 */       throw new NullPointerException();
/*  592 */     synchronized (this.writeLock) {
/*  593 */       synchronized (this.stateLock) {
/*  594 */         ensureOpen();
/*  595 */         if (!isConnected())
/*  596 */           throw new NotYetConnectedException();
/*      */       }
/*  598 */       int i = 0;
/*      */       try {
/*  600 */         begin();
/*  601 */         if (!isOpen()) {
/*  602 */           j = 0;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  609 */           this.writerThread = 0L;
/*  610 */           end((i > 0) || (i == -2));
/*  611 */           assert (IOStatus.check(i));return j; }
/*  603 */         this.writerThread = NativeThread.current();
/*      */         do {
/*  605 */           i = IOUtil.write(this.fd, paramByteBuffer, -1L, nd);
/*  606 */         } while ((i == -3) && (isOpen()));
/*  607 */         int j = IOStatus.normalize(i);
/*      */         
/*  609 */         this.writerThread = 0L;
/*  610 */         end((i > 0) || (i == -2));
/*  611 */         assert (IOStatus.check(i));return j;
/*      */       } finally {
/*  609 */         this.writerThread = 0L;
/*  610 */         end((i > 0) || (i == -2));
/*  611 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  619 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  620 */       throw new IndexOutOfBoundsException();
/*  621 */     synchronized (this.writeLock) {
/*  622 */       synchronized (this.stateLock) {
/*  623 */         ensureOpen();
/*  624 */         if (!isConnected())
/*  625 */           throw new NotYetConnectedException();
/*      */       }
/*  627 */       long l1 = 0L;
/*      */       try {
/*  629 */         begin();
/*  630 */         if (!isOpen()) {
/*  631 */           l2 = 0L;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  638 */           this.writerThread = 0L;
/*  639 */           end((l1 > 0L) || (l1 == -2L));
/*  640 */           assert (IOStatus.check(l1));return l2; }
/*  632 */         this.writerThread = NativeThread.current();
/*      */         do {
/*  634 */           l1 = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
/*  635 */         } while ((l1 == -3L) && (isOpen()));
/*  636 */         long l2 = IOStatus.normalize(l1);
/*      */         
/*  638 */         this.writerThread = 0L;
/*  639 */         end((l1 > 0L) || (l1 == -2L));
/*  640 */         assert (IOStatus.check(l1));return l2;
/*      */       } finally {
/*  638 */         this.writerThread = 0L;
/*  639 */         end((l1 > 0L) || (l1 == -2L));
/*  640 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException {
/*  646 */     IOUtil.configureBlocking(this.fd, paramBoolean);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public SocketAddress localAddress()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 658	sun/nio/ch/DatagramChannelImpl:stateLock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 662	sun/nio/ch/DatagramChannelImpl:localAddress	Ljava/net/InetSocketAddress;
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: areturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #650	-> byte code offset #0
/*      */     //   Java source line #651	-> byte code offset #7
/*      */     //   Java source line #652	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	DatagramChannelImpl
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
/*      */     //   1: getfield 658	sun/nio/ch/DatagramChannelImpl:stateLock	Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 663	sun/nio/ch/DatagramChannelImpl:remoteAddress	Ljava/net/InetSocketAddress;
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: areturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #656	-> byte code offset #0
/*      */     //   Java source line #657	-> byte code offset #7
/*      */     //   Java source line #658	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	DatagramChannelImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   public DatagramChannel bind(SocketAddress paramSocketAddress)
/*      */     throws IOException
/*      */   {
/*  663 */     synchronized (this.readLock) {
/*  664 */       synchronized (this.writeLock) {
/*  665 */         synchronized (this.stateLock) {
/*  666 */           ensureOpen();
/*  667 */           if (this.localAddress != null)
/*  668 */             throw new AlreadyBoundException();
/*      */           InetSocketAddress localInetSocketAddress;
/*  670 */           if (paramSocketAddress == null)
/*      */           {
/*  672 */             if (this.family == StandardProtocolFamily.INET) {
/*  673 */               localInetSocketAddress = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0);
/*      */             } else {
/*  675 */               localInetSocketAddress = new InetSocketAddress(0);
/*      */             }
/*      */           } else {
/*  678 */             localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/*      */             
/*      */ 
/*  681 */             if (this.family == StandardProtocolFamily.INET) {
/*  682 */               localObject1 = localInetSocketAddress.getAddress();
/*  683 */               if (!(localObject1 instanceof Inet4Address))
/*  684 */                 throw new UnsupportedAddressTypeException();
/*      */             }
/*      */           }
/*  687 */           Object localObject1 = System.getSecurityManager();
/*  688 */           if (localObject1 != null) {
/*  689 */             ((SecurityManager)localObject1).checkListen(localInetSocketAddress.getPort());
/*      */           }
/*  691 */           Net.bind(this.family, this.fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
/*  692 */           this.localAddress = Net.localAddress(this.fd);
/*      */         }
/*      */       }
/*      */     }
/*  696 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isConnected() {
/*  700 */     synchronized (this.stateLock) {
/*  701 */       return this.state == 1;
/*      */     }
/*      */   }
/*      */   
/*      */   void ensureOpenAndUnconnected() throws IOException {
/*  706 */     synchronized (this.stateLock) {
/*  707 */       if (!isOpen())
/*  708 */         throw new ClosedChannelException();
/*  709 */       if (this.state != 0) {
/*  710 */         throw new IllegalStateException("Connect already invoked");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public DatagramChannel connect(SocketAddress paramSocketAddress) throws IOException {
/*  716 */     int i = 0;
/*      */     
/*  718 */     synchronized (this.readLock) {
/*  719 */       synchronized (this.writeLock) {
/*  720 */         synchronized (this.stateLock) {
/*  721 */           ensureOpenAndUnconnected();
/*  722 */           InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
/*  723 */           SecurityManager localSecurityManager = System.getSecurityManager();
/*  724 */           if (localSecurityManager != null)
/*  725 */             localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress
/*  726 */               .getPort());
/*  727 */           int j = Net.connect(this.family, this.fd, localInetSocketAddress
/*      */           
/*  729 */             .getAddress(), localInetSocketAddress
/*  730 */             .getPort());
/*  731 */           if (j <= 0) {
/*  732 */             throw new Error();
/*      */           }
/*      */           
/*  735 */           this.state = 1;
/*  736 */           this.remoteAddress = localInetSocketAddress;
/*  737 */           this.sender = localInetSocketAddress;
/*  738 */           this.cachedSenderInetAddress = localInetSocketAddress.getAddress();
/*  739 */           this.cachedSenderPort = localInetSocketAddress.getPort();
/*      */           
/*      */ 
/*  742 */           this.localAddress = Net.localAddress(this.fd);
/*      */           
/*      */ 
/*  745 */           boolean bool = false;
/*  746 */           synchronized (blockingLock()) {
/*      */             try {
/*  748 */               bool = isBlocking();
/*      */               
/*  750 */               ByteBuffer localByteBuffer = ByteBuffer.allocate(1);
/*  751 */               if (bool) {
/*  752 */                 configureBlocking(false);
/*      */               }
/*      */               do {
/*  755 */                 localByteBuffer.clear();
/*  756 */               } while (receive(localByteBuffer) != null);
/*      */             } finally {
/*  758 */               if (bool) {
/*  759 */                 configureBlocking(true);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  766 */     return this;
/*      */   }
/*      */   
/*      */   public DatagramChannel disconnect() throws IOException {
/*  770 */     synchronized (this.readLock) {
/*  771 */       synchronized (this.writeLock) {
/*  772 */         synchronized (this.stateLock) {
/*  773 */           if ((!isConnected()) || (!isOpen()))
/*  774 */             return this;
/*  775 */           InetSocketAddress localInetSocketAddress = this.remoteAddress;
/*  776 */           SecurityManager localSecurityManager = System.getSecurityManager();
/*  777 */           if (localSecurityManager != null)
/*  778 */             localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress
/*  779 */               .getPort());
/*  780 */           boolean bool = this.family == StandardProtocolFamily.INET6;
/*  781 */           disconnect0(this.fd, bool);
/*  782 */           this.remoteAddress = null;
/*  783 */           this.state = 0;
/*      */           
/*      */ 
/*  786 */           this.localAddress = Net.localAddress(this.fd);
/*      */         }
/*      */       }
/*      */     }
/*  790 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private MembershipKey innerJoin(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2)
/*      */     throws IOException
/*      */   {
/*  802 */     if (!paramInetAddress1.isMulticastAddress()) {
/*  803 */       throw new IllegalArgumentException("Group not a multicast address");
/*      */     }
/*      */     
/*  806 */     if ((paramInetAddress1 instanceof Inet4Address)) {
/*  807 */       if ((this.family == StandardProtocolFamily.INET6) && (!Net.canIPv6SocketJoinIPv4Group()))
/*  808 */         throw new IllegalArgumentException("IPv6 socket cannot join IPv4 multicast group");
/*  809 */     } else if ((paramInetAddress1 instanceof Inet6Address)) {
/*  810 */       if (this.family != StandardProtocolFamily.INET6)
/*  811 */         throw new IllegalArgumentException("Only IPv6 sockets can join IPv6 multicast group");
/*      */     } else {
/*  813 */       throw new IllegalArgumentException("Address type not supported");
/*      */     }
/*      */     
/*      */ 
/*  817 */     if (paramInetAddress2 != null) {
/*  818 */       if (paramInetAddress2.isAnyLocalAddress())
/*  819 */         throw new IllegalArgumentException("Source address is a wildcard address");
/*  820 */       if (paramInetAddress2.isMulticastAddress())
/*  821 */         throw new IllegalArgumentException("Source address is multicast address");
/*  822 */       if (paramInetAddress2.getClass() != paramInetAddress1.getClass()) {
/*  823 */         throw new IllegalArgumentException("Source address is different type to group");
/*      */       }
/*      */     }
/*  826 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  827 */     if (localSecurityManager != null) {
/*  828 */       localSecurityManager.checkMulticast(paramInetAddress1);
/*      */     }
/*  830 */     synchronized (this.stateLock) {
/*  831 */       if (!isOpen()) {
/*  832 */         throw new ClosedChannelException();
/*      */       }
/*      */       Object localObject1;
/*  835 */       if (this.registry == null) {
/*  836 */         this.registry = new MembershipRegistry();
/*      */       }
/*      */       else {
/*  839 */         localObject1 = this.registry.checkMembership(paramInetAddress1, paramNetworkInterface, paramInetAddress2);
/*  840 */         if (localObject1 != null) {
/*  841 */           return (MembershipKey)localObject1;
/*      */         }
/*      */       }
/*      */       int m;
/*  845 */       if ((this.family == StandardProtocolFamily.INET6) && (((paramInetAddress1 instanceof Inet6Address)) || 
/*  846 */         (Net.canJoin6WithIPv4Group())))
/*      */       {
/*  848 */         int i = paramNetworkInterface.getIndex();
/*  849 */         if (i == -1) {
/*  850 */           throw new IOException("Network interface cannot be identified");
/*      */         }
/*      */         
/*  853 */         byte[] arrayOfByte1 = Net.inet6AsByteArray(paramInetAddress1);
/*      */         
/*  855 */         byte[] arrayOfByte2 = paramInetAddress2 == null ? null : Net.inet6AsByteArray(paramInetAddress2);
/*      */         
/*      */ 
/*  858 */         m = Net.join6(this.fd, arrayOfByte1, i, arrayOfByte2);
/*  859 */         if (m == -2) {
/*  860 */           throw new UnsupportedOperationException();
/*      */         }
/*  862 */         localObject1 = new MembershipKeyImpl.Type6(this, paramInetAddress1, paramNetworkInterface, paramInetAddress2, arrayOfByte1, i, arrayOfByte2);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  867 */         Inet4Address localInet4Address = Net.anyInet4Address(paramNetworkInterface);
/*  868 */         if (localInet4Address == null) {
/*  869 */           throw new IOException("Network interface not configured for IPv4");
/*      */         }
/*  871 */         int j = Net.inet4AsInt(paramInetAddress1);
/*  872 */         int k = Net.inet4AsInt(localInet4Address);
/*  873 */         m = paramInetAddress2 == null ? 0 : Net.inet4AsInt(paramInetAddress2);
/*      */         
/*      */ 
/*  876 */         int n = Net.join4(this.fd, j, k, m);
/*  877 */         if (n == -2) {
/*  878 */           throw new UnsupportedOperationException();
/*      */         }
/*  880 */         localObject1 = new MembershipKeyImpl.Type4(this, paramInetAddress1, paramNetworkInterface, paramInetAddress2, j, k, m);
/*      */       }
/*      */       
/*      */ 
/*  884 */       this.registry.add((MembershipKeyImpl)localObject1);
/*  885 */       return (MembershipKey)localObject1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public MembershipKey join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
/*      */     throws IOException
/*      */   {
/*  894 */     return innerJoin(paramInetAddress, paramNetworkInterface, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public MembershipKey join(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2)
/*      */     throws IOException
/*      */   {
/*  903 */     if (paramInetAddress2 == null)
/*  904 */       throw new NullPointerException("source address is null");
/*  905 */     return innerJoin(paramInetAddress1, paramNetworkInterface, paramInetAddress2);
/*      */   }
/*      */   
/*      */   void drop(MembershipKeyImpl paramMembershipKeyImpl)
/*      */   {
/*  910 */     assert (paramMembershipKeyImpl.channel() == this);
/*      */     
/*  912 */     synchronized (this.stateLock) {
/*  913 */       if (!paramMembershipKeyImpl.isValid())
/*  914 */         return;
/*      */       try {
/*      */         Object localObject1;
/*  917 */         if ((paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6)) {
/*  918 */           localObject1 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
/*      */           
/*  920 */           Net.drop6(this.fd, ((MembershipKeyImpl.Type6)localObject1).groupAddress(), ((MembershipKeyImpl.Type6)localObject1).index(), ((MembershipKeyImpl.Type6)localObject1).source());
/*      */         } else {
/*  922 */           localObject1 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
/*  923 */           Net.drop4(this.fd, ((MembershipKeyImpl.Type4)localObject1).groupAddress(), ((MembershipKeyImpl.Type4)localObject1).interfaceAddress(), ((MembershipKeyImpl.Type4)localObject1)
/*  924 */             .source());
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException) {
/*  928 */         throw new AssertionError(localIOException);
/*      */       }
/*      */       
/*  931 */       paramMembershipKeyImpl.invalidate();
/*  932 */       this.registry.remove(paramMembershipKeyImpl);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void block(MembershipKeyImpl paramMembershipKeyImpl, InetAddress paramInetAddress)
/*      */     throws IOException
/*      */   {
/*  943 */     assert (paramMembershipKeyImpl.channel() == this);
/*  944 */     assert (paramMembershipKeyImpl.sourceAddress() == null);
/*      */     
/*  946 */     synchronized (this.stateLock) {
/*  947 */       if (!paramMembershipKeyImpl.isValid())
/*  948 */         throw new IllegalStateException("key is no longer valid");
/*  949 */       if (paramInetAddress.isAnyLocalAddress())
/*  950 */         throw new IllegalArgumentException("Source address is a wildcard address");
/*  951 */       if (paramInetAddress.isMulticastAddress())
/*  952 */         throw new IllegalArgumentException("Source address is multicast address");
/*  953 */       if (paramInetAddress.getClass() != paramMembershipKeyImpl.group().getClass())
/*  954 */         throw new IllegalArgumentException("Source address is different type to group");
/*      */       Object localObject1;
/*      */       int i;
/*  957 */       if ((paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6)) {
/*  958 */         localObject1 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
/*      */         
/*  960 */         i = Net.block6(this.fd, ((MembershipKeyImpl.Type6)localObject1).groupAddress(), ((MembershipKeyImpl.Type6)localObject1).index(), 
/*  961 */           Net.inet6AsByteArray(paramInetAddress));
/*      */       } else {
/*  963 */         localObject1 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
/*      */         
/*  965 */         i = Net.block4(this.fd, ((MembershipKeyImpl.Type4)localObject1).groupAddress(), ((MembershipKeyImpl.Type4)localObject1).interfaceAddress(), 
/*  966 */           Net.inet4AsInt(paramInetAddress));
/*      */       }
/*  968 */       if (i == -2)
/*      */       {
/*  970 */         throw new UnsupportedOperationException();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void unblock(MembershipKeyImpl paramMembershipKeyImpl, InetAddress paramInetAddress)
/*      */   {
/*  979 */     assert (paramMembershipKeyImpl.channel() == this);
/*  980 */     assert (paramMembershipKeyImpl.sourceAddress() == null);
/*      */     
/*  982 */     synchronized (this.stateLock) {
/*  983 */       if (!paramMembershipKeyImpl.isValid())
/*  984 */         throw new IllegalStateException("key is no longer valid");
/*      */       try {
/*      */         Object localObject1;
/*  987 */         if ((paramMembershipKeyImpl instanceof MembershipKeyImpl.Type6)) {
/*  988 */           localObject1 = (MembershipKeyImpl.Type6)paramMembershipKeyImpl;
/*      */           
/*  990 */           Net.unblock6(this.fd, ((MembershipKeyImpl.Type6)localObject1).groupAddress(), ((MembershipKeyImpl.Type6)localObject1).index(), 
/*  991 */             Net.inet6AsByteArray(paramInetAddress));
/*      */         } else {
/*  993 */           localObject1 = (MembershipKeyImpl.Type4)paramMembershipKeyImpl;
/*      */           
/*  995 */           Net.unblock4(this.fd, ((MembershipKeyImpl.Type4)localObject1).groupAddress(), ((MembershipKeyImpl.Type4)localObject1).interfaceAddress(), 
/*  996 */             Net.inet4AsInt(paramInetAddress));
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException) {
/* 1000 */         throw new AssertionError(localIOException);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void implCloseSelectableChannel() throws IOException {
/* 1006 */     synchronized (this.stateLock) {
/* 1007 */       if (this.state != 2)
/* 1008 */         nd.preClose(this.fd);
/* 1009 */       ResourceManager.afterUdpClose();
/*      */       
/*      */ 
/* 1012 */       if (this.registry != null) {
/* 1013 */         this.registry.invalidateAll();
/*      */       }
/*      */       long l;
/* 1016 */       if ((l = this.readerThread) != 0L)
/* 1017 */         NativeThread.signal(l);
/* 1018 */       if ((l = this.writerThread) != 0L)
/* 1019 */         NativeThread.signal(l);
/* 1020 */       if (!isRegistered())
/* 1021 */         kill();
/*      */     }
/*      */   }
/*      */   
/*      */   public void kill() throws IOException {
/* 1026 */     synchronized (this.stateLock) {
/* 1027 */       if (this.state == 2)
/* 1028 */         return;
/* 1029 */       if (this.state == -1) {
/* 1030 */         this.state = 2;
/* 1031 */         return;
/*      */       }
/* 1033 */       assert ((!isOpen()) && (!isRegistered()));
/* 1034 */       nd.close(this.fd);
/* 1035 */       this.state = 2;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void finalize() throws IOException
/*      */   {
/* 1041 */     if (this.fd != null) {
/* 1042 */       close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/* 1050 */     int i = paramSelectionKeyImpl.nioInterestOps();
/* 1051 */     int j = paramSelectionKeyImpl.nioReadyOps();
/* 1052 */     int k = paramInt2;
/*      */     
/* 1054 */     if ((paramInt1 & Net.POLLNVAL) != 0)
/*      */     {
/*      */ 
/*      */ 
/* 1058 */       return false;
/*      */     }
/*      */     
/* 1061 */     if ((paramInt1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
/* 1062 */       k = i;
/* 1063 */       paramSelectionKeyImpl.nioReadyOps(k);
/* 1064 */       return (k & (j ^ 0xFFFFFFFF)) != 0;
/*      */     }
/*      */     
/* 1067 */     if (((paramInt1 & Net.POLLIN) != 0) && ((i & 0x1) != 0))
/*      */     {
/* 1069 */       k |= 0x1;
/*      */     }
/* 1071 */     if (((paramInt1 & Net.POLLOUT) != 0) && ((i & 0x4) != 0))
/*      */     {
/* 1073 */       k |= 0x4;
/*      */     }
/* 1075 */     paramSelectionKeyImpl.nioReadyOps(k);
/* 1076 */     return (k & (j ^ 0xFFFFFFFF)) != 0;
/*      */   }
/*      */   
/*      */   public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 1080 */     return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
/*      */   }
/*      */   
/*      */   public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
/* 1084 */     return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
/*      */   }
/*      */   
/*      */   int poll(int paramInt, long paramLong) throws IOException
/*      */   {
/* 1089 */     assert ((Thread.holdsLock(blockingLock())) && (!isBlocking()));
/*      */     
/* 1091 */     synchronized (this.readLock) {
/* 1092 */       int i = 0;
/*      */       try {
/* 1094 */         begin();
/* 1095 */         synchronized (this.stateLock) {
/* 1096 */           if (!isOpen()) {
/* 1097 */             int j = 0;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1102 */             this.readerThread = 0L;
/* 1103 */             end(i > 0);return j; }
/* 1098 */           this.readerThread = NativeThread.current();
/*      */         }
/* 1100 */         i = Net.poll(this.fd, paramInt, paramLong);
/*      */       } finally {
/* 1102 */         this.readerThread = 0L;
/* 1103 */         end(i > 0);
/*      */       }
/* 1105 */       return i;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*      */   {
/* 1113 */     int i = 0;
/*      */     
/* 1115 */     if ((paramInt & 0x1) != 0)
/* 1116 */       i |= Net.POLLIN;
/* 1117 */     if ((paramInt & 0x4) != 0)
/* 1118 */       i |= Net.POLLOUT;
/* 1119 */     if ((paramInt & 0x8) != 0)
/* 1120 */       i |= Net.POLLIN;
/* 1121 */     paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, i);
/*      */   }
/*      */   
/*      */   public FileDescriptor getFD() {
/* 1125 */     return this.fd;
/*      */   }
/*      */   
/*      */   public int getFDVal() {
/* 1129 */     return this.fdVal;
/*      */   }
/*      */   
/*      */   private static native void initIDs();
/*      */   
/*      */   private static native void disconnect0(FileDescriptor paramFileDescriptor, boolean paramBoolean)
/*      */     throws IOException;
/*      */   
/*      */   private native int receive0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt, boolean paramBoolean)
/*      */     throws IOException;
/*      */   
/*      */   private native int send0(boolean paramBoolean, FileDescriptor paramFileDescriptor, long paramLong, int paramInt1, InetAddress paramInetAddress, int paramInt2)
/*      */     throws IOException;
/*      */   
/*      */   static
/*      */   {
/*   48 */     nd = new DatagramDispatcher();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1149 */     IOUtil.load();
/* 1150 */     initIDs();
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\DatagramChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
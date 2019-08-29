/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.ProtocolFamily;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardProtocolFamily;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.channels.AlreadyBoundException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.NotYetBoundException;
/*     */ import java.nio.channels.NotYetConnectedException;
/*     */ import java.nio.channels.UnresolvedAddressException;
/*     */ import java.nio.channels.UnsupportedAddressTypeException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import jdk.net.NetworkPermission;
/*     */ import jdk.net.SocketFlow;
/*     */ import sun.net.ExtendedOptionsImpl;
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
/*     */ public class Net
/*     */ {
/*  44 */   static final ProtocolFamily UNSPEC = new ProtocolFamily() {
/*     */     public String name() {
/*  46 */       return "UNSPEC";
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  58 */   private static volatile boolean checkedIPv6 = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean isIPv6Available()
/*     */   {
/*  65 */     if (!checkedIPv6) {
/*  66 */       isIPv6Available = isIPv6Available0();
/*  67 */       checkedIPv6 = true;
/*     */     }
/*  69 */     return isIPv6Available;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static boolean useExclusiveBind()
/*     */   {
/*  76 */     return exclusiveBind;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static boolean canIPv6SocketJoinIPv4Group()
/*     */   {
/*  83 */     return canIPv6SocketJoinIPv4Group0();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean canJoin6WithIPv4Group()
/*     */   {
/*  91 */     return canJoin6WithIPv4Group0();
/*     */   }
/*     */   
/*     */   public static InetSocketAddress checkAddress(SocketAddress paramSocketAddress) {
/*  95 */     if (paramSocketAddress == null)
/*  96 */       throw new NullPointerException();
/*  97 */     if (!(paramSocketAddress instanceof InetSocketAddress))
/*  98 */       throw new UnsupportedAddressTypeException();
/*  99 */     InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
/* 100 */     if (localInetSocketAddress.isUnresolved())
/* 101 */       throw new UnresolvedAddressException();
/* 102 */     InetAddress localInetAddress = localInetSocketAddress.getAddress();
/* 103 */     if ((!(localInetAddress instanceof Inet4Address)) && (!(localInetAddress instanceof Inet6Address)))
/* 104 */       throw new IllegalArgumentException("Invalid address type");
/* 105 */     return localInetSocketAddress;
/*     */   }
/*     */   
/*     */   static InetSocketAddress asInetSocketAddress(SocketAddress paramSocketAddress) {
/* 109 */     if (!(paramSocketAddress instanceof InetSocketAddress))
/* 110 */       throw new UnsupportedAddressTypeException();
/* 111 */     return (InetSocketAddress)paramSocketAddress;
/*     */   }
/*     */   
/*     */   static void translateToSocketException(Exception paramException)
/*     */     throws SocketException
/*     */   {
/* 117 */     if ((paramException instanceof SocketException))
/* 118 */       throw ((SocketException)paramException);
/* 119 */     Object localObject = paramException;
/* 120 */     if ((paramException instanceof ClosedChannelException)) {
/* 121 */       localObject = new SocketException("Socket is closed");
/* 122 */     } else if ((paramException instanceof NotYetConnectedException)) {
/* 123 */       localObject = new SocketException("Socket is not connected");
/* 124 */     } else if ((paramException instanceof AlreadyBoundException)) {
/* 125 */       localObject = new SocketException("Already bound");
/* 126 */     } else if ((paramException instanceof NotYetBoundException)) {
/* 127 */       localObject = new SocketException("Socket is not bound yet");
/* 128 */     } else if ((paramException instanceof UnsupportedAddressTypeException)) {
/* 129 */       localObject = new SocketException("Unsupported address type");
/* 130 */     } else if ((paramException instanceof UnresolvedAddressException)) {
/* 131 */       localObject = new SocketException("Unresolved address");
/*     */     }
/* 133 */     if (localObject != paramException) {
/* 134 */       ((Exception)localObject).initCause(paramException);
/*     */     }
/* 136 */     if ((localObject instanceof SocketException))
/* 137 */       throw ((SocketException)localObject);
/* 138 */     if ((localObject instanceof RuntimeException)) {
/* 139 */       throw ((RuntimeException)localObject);
/*     */     }
/* 141 */     throw new Error("Untranslated exception", (Throwable)localObject);
/*     */   }
/*     */   
/*     */ 
/*     */   static void translateException(Exception paramException, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 148 */     if ((paramException instanceof IOException)) {
/* 149 */       throw ((IOException)paramException);
/*     */     }
/*     */     
/* 152 */     if ((paramBoolean) && ((paramException instanceof UnresolvedAddressException)))
/*     */     {
/*     */ 
/* 155 */       throw new UnknownHostException();
/*     */     }
/* 157 */     translateToSocketException(paramException);
/*     */   }
/*     */   
/*     */   static void translateException(Exception paramException)
/*     */     throws IOException
/*     */   {
/* 163 */     translateException(paramException, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static InetSocketAddress getRevealedLocalAddress(InetSocketAddress paramInetSocketAddress)
/*     */   {
/* 170 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 171 */     if ((paramInetSocketAddress == null) || (localSecurityManager == null)) {
/* 172 */       return paramInetSocketAddress;
/*     */     }
/*     */     try {
/* 175 */       localSecurityManager.checkConnect(paramInetSocketAddress.getAddress().getHostAddress(), -1);
/*     */     }
/*     */     catch (SecurityException localSecurityException)
/*     */     {
/* 179 */       paramInetSocketAddress = getLoopbackAddress(paramInetSocketAddress.getPort());
/*     */     }
/* 181 */     return paramInetSocketAddress;
/*     */   }
/*     */   
/*     */   static String getRevealedLocalAddressAsString(InetSocketAddress paramInetSocketAddress) {
/* 185 */     return System.getSecurityManager() == null ? paramInetSocketAddress.toString() : 
/* 186 */       getLoopbackAddress(paramInetSocketAddress.getPort()).toString();
/*     */   }
/*     */   
/*     */   private static InetSocketAddress getLoopbackAddress(int paramInt) {
/* 190 */     return new InetSocketAddress(InetAddress.getLoopbackAddress(), paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Inet4Address anyInet4Address(NetworkInterface paramNetworkInterface)
/*     */   {
/* 199 */     (Inet4Address)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Inet4Address run() {
/* 201 */         Enumeration localEnumeration = this.val$interf.getInetAddresses();
/* 202 */         while (localEnumeration.hasMoreElements()) {
/* 203 */           InetAddress localInetAddress = (InetAddress)localEnumeration.nextElement();
/* 204 */           if ((localInetAddress instanceof Inet4Address)) {
/* 205 */             return (Inet4Address)localInetAddress;
/*     */           }
/*     */         }
/* 208 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static int inet4AsInt(InetAddress paramInetAddress)
/*     */   {
/* 217 */     if ((paramInetAddress instanceof Inet4Address)) {
/* 218 */       byte[] arrayOfByte = paramInetAddress.getAddress();
/* 219 */       int i = arrayOfByte[3] & 0xFF;
/* 220 */       i |= arrayOfByte[2] << 8 & 0xFF00;
/* 221 */       i |= arrayOfByte[1] << 16 & 0xFF0000;
/* 222 */       i |= arrayOfByte[0] << 24 & 0xFF000000;
/* 223 */       return i;
/*     */     }
/* 225 */     throw new AssertionError("Should not reach here");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static InetAddress inet4FromInt(int paramInt)
/*     */   {
/* 233 */     byte[] arrayOfByte = new byte[4];
/* 234 */     arrayOfByte[0] = ((byte)(paramInt >>> 24 & 0xFF));
/* 235 */     arrayOfByte[1] = ((byte)(paramInt >>> 16 & 0xFF));
/* 236 */     arrayOfByte[2] = ((byte)(paramInt >>> 8 & 0xFF));
/* 237 */     arrayOfByte[3] = ((byte)(paramInt & 0xFF));
/*     */     try {
/* 239 */       return InetAddress.getByAddress(arrayOfByte);
/*     */     } catch (UnknownHostException localUnknownHostException) {
/* 241 */       throw new AssertionError("Should not reach here");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static byte[] inet6AsByteArray(InetAddress paramInetAddress)
/*     */   {
/* 249 */     if ((paramInetAddress instanceof Inet6Address)) {
/* 250 */       return paramInetAddress.getAddress();
/*     */     }
/*     */     
/*     */ 
/* 254 */     if ((paramInetAddress instanceof Inet4Address)) {
/* 255 */       byte[] arrayOfByte1 = paramInetAddress.getAddress();
/* 256 */       byte[] arrayOfByte2 = new byte[16];
/* 257 */       arrayOfByte2[10] = -1;
/* 258 */       arrayOfByte2[11] = -1;
/* 259 */       arrayOfByte2[12] = arrayOfByte1[0];
/* 260 */       arrayOfByte2[13] = arrayOfByte1[1];
/* 261 */       arrayOfByte2[14] = arrayOfByte1[2];
/* 262 */       arrayOfByte2[15] = arrayOfByte1[3];
/* 263 */       return arrayOfByte2;
/*     */     }
/*     */     
/* 266 */     throw new AssertionError("Should not reach here");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void setSocketOption(FileDescriptor paramFileDescriptor, ProtocolFamily paramProtocolFamily, SocketOption<?> paramSocketOption, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 275 */     if (paramObject == null) {
/* 276 */       throw new IllegalArgumentException("Invalid option value");
/*     */     }
/*     */     
/* 279 */     Class localClass = paramSocketOption.type();
/*     */     
/* 281 */     if (localClass == SocketFlow.class) {
/* 282 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 283 */       if (localSecurityManager != null) {
/* 284 */         localSecurityManager.checkPermission(new NetworkPermission("setOption.SO_FLOW_SLA"));
/*     */       }
/* 286 */       ExtendedOptionsImpl.setFlowOption(paramFileDescriptor, (SocketFlow)paramObject);
/* 287 */       return;
/*     */     }
/*     */     
/* 290 */     if ((localClass != Integer.class) && (localClass != Boolean.class)) {
/* 291 */       throw new AssertionError("Should not reach here");
/*     */     }
/*     */     int i;
/* 294 */     if ((paramSocketOption == StandardSocketOptions.SO_RCVBUF) || (paramSocketOption == StandardSocketOptions.SO_SNDBUF))
/*     */     {
/*     */ 
/* 297 */       i = ((Integer)paramObject).intValue();
/* 298 */       if (i < 0)
/* 299 */         throw new IllegalArgumentException("Invalid send/receive buffer size");
/*     */     }
/* 301 */     if (paramSocketOption == StandardSocketOptions.SO_LINGER) {
/* 302 */       i = ((Integer)paramObject).intValue();
/* 303 */       if (i < 0)
/* 304 */         paramObject = Integer.valueOf(-1);
/* 305 */       if (i > 65535)
/* 306 */         paramObject = Integer.valueOf(65535);
/*     */     }
/* 308 */     if (paramSocketOption == StandardSocketOptions.IP_TOS) {
/* 309 */       i = ((Integer)paramObject).intValue();
/* 310 */       if ((i < 0) || (i > 255))
/* 311 */         throw new IllegalArgumentException("Invalid IP_TOS value");
/*     */     }
/* 313 */     if (paramSocketOption == StandardSocketOptions.IP_MULTICAST_TTL) {
/* 314 */       i = ((Integer)paramObject).intValue();
/* 315 */       if ((i < 0) || (i > 255)) {
/* 316 */         throw new IllegalArgumentException("Invalid TTL/hop value");
/*     */       }
/*     */     }
/*     */     
/* 320 */     OptionKey localOptionKey = SocketOptionRegistry.findOption(paramSocketOption, paramProtocolFamily);
/* 321 */     if (localOptionKey == null) {
/* 322 */       throw new AssertionError("Option not found");
/*     */     }
/*     */     int j;
/* 325 */     if (localClass == Integer.class) {
/* 326 */       j = ((Integer)paramObject).intValue();
/*     */     } else {
/* 328 */       bool1 = ((Boolean)paramObject).booleanValue();
/* 329 */       j = bool1 ? 1 : 0;
/*     */     }
/*     */     
/* 332 */     boolean bool1 = paramProtocolFamily == UNSPEC;
/* 333 */     boolean bool2 = paramProtocolFamily == StandardProtocolFamily.INET6;
/* 334 */     setIntOption0(paramFileDescriptor, bool1, localOptionKey.level(), localOptionKey.name(), j, bool2);
/*     */   }
/*     */   
/*     */ 
/*     */   static Object getSocketOption(FileDescriptor paramFileDescriptor, ProtocolFamily paramProtocolFamily, SocketOption<?> paramSocketOption)
/*     */     throws IOException
/*     */   {
/* 341 */     Class localClass = paramSocketOption.type();
/*     */     
/* 343 */     if (localClass == SocketFlow.class) {
/* 344 */       localObject = System.getSecurityManager();
/* 345 */       if (localObject != null) {
/* 346 */         ((SecurityManager)localObject).checkPermission(new NetworkPermission("getOption.SO_FLOW_SLA"));
/*     */       }
/* 348 */       SocketFlow localSocketFlow = SocketFlow.create();
/* 349 */       ExtendedOptionsImpl.getFlowOption(paramFileDescriptor, localSocketFlow);
/* 350 */       return localSocketFlow;
/*     */     }
/*     */     
/*     */ 
/* 354 */     if ((localClass != Integer.class) && (localClass != Boolean.class)) {
/* 355 */       throw new AssertionError("Should not reach here");
/*     */     }
/*     */     
/* 358 */     Object localObject = SocketOptionRegistry.findOption(paramSocketOption, paramProtocolFamily);
/* 359 */     if (localObject == null) {
/* 360 */       throw new AssertionError("Option not found");
/*     */     }
/* 362 */     boolean bool = paramProtocolFamily == UNSPEC;
/* 363 */     int i = getIntOption0(paramFileDescriptor, bool, ((OptionKey)localObject).level(), ((OptionKey)localObject).name());
/*     */     
/* 365 */     if (localClass == Integer.class) {
/* 366 */       return Integer.valueOf(i);
/*     */     }
/* 368 */     return i == 0 ? Boolean.FALSE : Boolean.TRUE;
/*     */   }
/*     */   
/*     */   public static boolean isFastTcpLoopbackRequested()
/*     */   {
/* 373 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run()
/*     */       {
/* 377 */         return System.getProperty("jdk.net.useFastTcpLoopback");
/*     */       }
/*     */     });
/*     */     boolean bool;
/* 381 */     if ("".equals(str)) {
/* 382 */       bool = true;
/*     */     } else {
/* 384 */       bool = Boolean.parseBoolean(str);
/*     */     }
/* 386 */     return bool;
/*     */   }
/*     */   
/*     */ 
/*     */   private static native boolean isIPv6Available0();
/*     */   
/*     */ 
/*     */   private static native int isExclusiveBindAvailable();
/*     */   
/*     */ 
/*     */   private static native boolean canIPv6SocketJoinIPv4Group0();
/*     */   
/*     */ 
/*     */   private static native boolean canJoin6WithIPv4Group0();
/*     */   
/*     */   static FileDescriptor socket(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 404 */     return socket(UNSPEC, paramBoolean);
/*     */   }
/*     */   
/*     */   static FileDescriptor socket(ProtocolFamily paramProtocolFamily, boolean paramBoolean) throws IOException
/*     */   {
/* 409 */     boolean bool = (isIPv6Available()) && (paramProtocolFamily != StandardProtocolFamily.INET);
/*     */     
/* 411 */     return IOUtil.newFD(socket0(bool, paramBoolean, false, fastLoopback));
/*     */   }
/*     */   
/*     */   static FileDescriptor serverSocket(boolean paramBoolean) {
/* 415 */     return IOUtil.newFD(socket0(isIPv6Available(), paramBoolean, true, fastLoopback));
/*     */   }
/*     */   
/*     */ 
/*     */   private static native int socket0(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4);
/*     */   
/*     */ 
/*     */   public static void bind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 425 */     bind(UNSPEC, paramFileDescriptor, paramInetAddress, paramInt);
/*     */   }
/*     */   
/*     */   static void bind(ProtocolFamily paramProtocolFamily, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 431 */     boolean bool = (isIPv6Available()) && (paramProtocolFamily != StandardProtocolFamily.INET);
/*     */     
/* 433 */     bind0(paramFileDescriptor, bool, exclusiveBind, paramInetAddress, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */   private static native void bind0(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */   static native void listen(FileDescriptor paramFileDescriptor, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */   static int connect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 446 */     return connect(UNSPEC, paramFileDescriptor, paramInetAddress, paramInt);
/*     */   }
/*     */   
/*     */   static int connect(ProtocolFamily paramProtocolFamily, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 452 */     boolean bool = (isIPv6Available()) && (paramProtocolFamily != StandardProtocolFamily.INET);
/*     */     
/* 454 */     return connect0(bool, paramFileDescriptor, paramInetAddress, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static native int connect0(boolean paramBoolean, FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   static native void shutdown(FileDescriptor paramFileDescriptor, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */   private static native int localPort(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */   private static native InetAddress localInetAddress(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */   public static InetSocketAddress localAddress(FileDescriptor paramFileDescriptor)
/*     */     throws IOException
/*     */   {
/* 479 */     return new InetSocketAddress(localInetAddress(paramFileDescriptor), localPort(paramFileDescriptor));
/*     */   }
/*     */   
/*     */   private static native int remotePort(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */   
/*     */   private static native InetAddress remoteInetAddress(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */   
/*     */   static InetSocketAddress remoteAddress(FileDescriptor paramFileDescriptor)
/*     */     throws IOException
/*     */   {
/* 491 */     return new InetSocketAddress(remoteInetAddress(paramFileDescriptor), remotePort(paramFileDescriptor));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static native int getIntOption0(FileDescriptor paramFileDescriptor, boolean paramBoolean, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   private static native void setIntOption0(FileDescriptor paramFileDescriptor, boolean paramBoolean1, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean2)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   static native int poll(FileDescriptor paramFileDescriptor, int paramInt, long paramLong)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   static int join4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 514 */     return joinOrDrop4(true, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void drop4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 523 */     joinOrDrop4(false, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static native int joinOrDrop4(boolean paramBoolean, FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */   static int block4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 535 */     return blockOrUnblock4(true, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void unblock4(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException
/*     */   {
/* 544 */     blockOrUnblock4(false, paramFileDescriptor, paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static native int blockOrUnblock4(boolean paramBoolean, FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   static int join6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException
/*     */   {
/* 557 */     return joinOrDrop6(true, paramFileDescriptor, paramArrayOfByte1, paramInt, paramArrayOfByte2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void drop6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException
/*     */   {
/* 566 */     joinOrDrop6(false, paramFileDescriptor, paramArrayOfByte1, paramInt, paramArrayOfByte2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static native int joinOrDrop6(boolean paramBoolean, FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */   static int block6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException
/*     */   {
/* 578 */     return blockOrUnblock6(true, paramFileDescriptor, paramArrayOfByte1, paramInt, paramArrayOfByte2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void unblock6(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException
/*     */   {
/* 587 */     blockOrUnblock6(false, paramFileDescriptor, paramArrayOfByte1, paramInt, paramArrayOfByte2);
/*     */   }
/*     */   
/*     */ 
/*     */   static native int blockOrUnblock6(boolean paramBoolean, FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
/*     */     throws IOException;
/*     */   
/*     */   static native void setInterface4(FileDescriptor paramFileDescriptor, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */   static native int getInterface4(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */   
/*     */   static native void setInterface6(FileDescriptor paramFileDescriptor, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */   static native int getInterface6(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */   
/*     */   private static native void initIDs();
/*     */   
/*     */   static native short pollinValue();
/*     */   
/*     */   static native short polloutValue();
/*     */   
/*     */   static native short pollerrValue();
/*     */   
/*     */   static native short pollhupValue();
/*     */   
/*     */   static native short pollnvalValue();
/*     */   
/*     */   static native short pollconnValue();
/*     */   
/*     */   static
/*     */   {
/* 622 */     IOUtil.load();
/* 623 */     initIDs();
/*     */     
/* 625 */     POLLIN = pollinValue();
/* 626 */     POLLOUT = polloutValue();
/* 627 */     POLLERR = pollerrValue();
/* 628 */     POLLHUP = pollhupValue();
/* 629 */     POLLNVAL = pollnvalValue();
/* 630 */     POLLCONN = pollconnValue();
/*     */     
/*     */ 
/*     */ 
/* 634 */     int i = isExclusiveBindAvailable();
/* 635 */     if (i >= 0)
/*     */     {
/* 637 */       String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public String run()
/*     */         {
/* 641 */           return System.getProperty("sun.net.useExclusiveBind");
/*     */         }
/*     */       });
/*     */       
/* 645 */       if (str != null)
/*     */       {
/* 647 */         exclusiveBind = str.length() == 0 ? true : Boolean.parseBoolean(str);
/* 648 */       } else if (i == 1) {
/* 649 */         exclusiveBind = true;
/*     */       } else {
/* 651 */         exclusiveBind = false;
/*     */       }
/*     */     } else {
/* 654 */       exclusiveBind = false;
/*     */     } }
/*     */   
/* 657 */   private static final boolean fastLoopback = isFastTcpLoopbackRequested();
/*     */   private static final boolean exclusiveBind;
/*     */   private static volatile boolean isIPv6Available;
/*     */   public static final int SHUT_RD = 0;
/*     */   public static final int SHUT_WR = 1;
/*     */   public static final int SHUT_RDWR = 2;
/*     */   public static final short POLLIN;
/*     */   public static final short POLLOUT;
/*     */   public static final short POLLERR;
/*     */   public static final short POLLHUP;
/*     */   public static final short POLLNVAL;
/*     */   public static final short POLLCONN;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\Net.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
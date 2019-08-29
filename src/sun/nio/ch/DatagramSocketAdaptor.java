/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.DatagramSocketImpl;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketOption;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.DatagramChannel;
/*     */ import java.nio.channels.IllegalBlockingModeException;
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
/*     */ public class DatagramSocketAdaptor
/*     */   extends DatagramSocket
/*     */ {
/*     */   private final DatagramChannelImpl dc;
/*  49 */   private volatile int timeout = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private DatagramSocketAdaptor(DatagramChannelImpl paramDatagramChannelImpl)
/*     */     throws IOException
/*     */   {
/*  57 */     super(dummyDatagramSocket);
/*  58 */     this.dc = paramDatagramChannelImpl;
/*     */   }
/*     */   
/*     */   public static DatagramSocket create(DatagramChannelImpl paramDatagramChannelImpl) {
/*     */     try {
/*  63 */       return new DatagramSocketAdaptor(paramDatagramChannelImpl);
/*     */     } catch (IOException localIOException) {
/*  65 */       throw new Error(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   private void connectInternal(SocketAddress paramSocketAddress)
/*     */     throws SocketException
/*     */   {
/*  72 */     InetSocketAddress localInetSocketAddress = Net.asInetSocketAddress(paramSocketAddress);
/*  73 */     int i = localInetSocketAddress.getPort();
/*  74 */     if ((i < 0) || (i > 65535))
/*  75 */       throw new IllegalArgumentException("connect: " + i);
/*  76 */     if (paramSocketAddress == null)
/*  77 */       throw new IllegalArgumentException("connect: null address");
/*  78 */     if (isClosed())
/*  79 */       return;
/*     */     try {
/*  81 */       this.dc.connect(paramSocketAddress);
/*     */     } catch (Exception localException) {
/*  83 */       Net.translateToSocketException(localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public void bind(SocketAddress paramSocketAddress) throws SocketException {
/*     */     try {
/*  89 */       if (paramSocketAddress == null)
/*  90 */         paramSocketAddress = new InetSocketAddress(0);
/*  91 */       this.dc.bind(paramSocketAddress);
/*     */     } catch (Exception localException) {
/*  93 */       Net.translateToSocketException(localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public void connect(InetAddress paramInetAddress, int paramInt) {
/*     */     try {
/*  99 */       connectInternal(new InetSocketAddress(paramInetAddress, paramInt));
/*     */     }
/*     */     catch (SocketException localSocketException) {}
/*     */   }
/*     */   
/*     */   public void connect(SocketAddress paramSocketAddress) throws SocketException
/*     */   {
/* 106 */     if (paramSocketAddress == null)
/* 107 */       throw new IllegalArgumentException("Address can't be null");
/* 108 */     connectInternal(paramSocketAddress);
/*     */   }
/*     */   
/*     */   public void disconnect() {
/*     */     try {
/* 113 */       this.dc.disconnect();
/*     */     } catch (IOException localIOException) {
/* 115 */       throw new Error(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isBound() {
/* 120 */     return this.dc.localAddress() != null;
/*     */   }
/*     */   
/*     */   public boolean isConnected() {
/* 124 */     return this.dc.remoteAddress() != null;
/*     */   }
/*     */   
/*     */   public InetAddress getInetAddress() {
/* 128 */     return isConnected() ? 
/* 129 */       Net.asInetSocketAddress(this.dc.remoteAddress()).getAddress() : null;
/*     */   }
/*     */   
/*     */   public int getPort()
/*     */   {
/* 134 */     return isConnected() ? 
/* 135 */       Net.asInetSocketAddress(this.dc.remoteAddress()).getPort() : -1;
/*     */   }
/*     */   
/*     */   public void send(DatagramPacket paramDatagramPacket) throws IOException
/*     */   {
/* 140 */     synchronized (this.dc.blockingLock()) {
/* 141 */       if (!this.dc.isBlocking())
/* 142 */         throw new IllegalBlockingModeException();
/*     */       try {
/* 144 */         synchronized (paramDatagramPacket) {
/* 145 */           ByteBuffer localByteBuffer = ByteBuffer.wrap(paramDatagramPacket.getData(), paramDatagramPacket
/* 146 */             .getOffset(), paramDatagramPacket
/* 147 */             .getLength());
/* 148 */           if (this.dc.isConnected()) {
/* 149 */             if (paramDatagramPacket.getAddress() == null)
/*     */             {
/*     */ 
/*     */ 
/* 153 */               InetSocketAddress localInetSocketAddress = (InetSocketAddress)this.dc.remoteAddress();
/* 154 */               paramDatagramPacket.setPort(localInetSocketAddress.getPort());
/* 155 */               paramDatagramPacket.setAddress(localInetSocketAddress.getAddress());
/* 156 */               this.dc.write(localByteBuffer);
/*     */             }
/*     */             else {
/* 159 */               this.dc.send(localByteBuffer, paramDatagramPacket.getSocketAddress());
/*     */             }
/*     */           }
/*     */           else {
/* 163 */             this.dc.send(localByteBuffer, paramDatagramPacket.getSocketAddress());
/*     */           }
/*     */         }
/*     */       } catch (IOException localIOException) {
/* 167 */         Net.translateException(localIOException);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private SocketAddress receive(ByteBuffer paramByteBuffer)
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 271	sun/nio/ch/DatagramSocketAdaptor:timeout	I
/*     */     //   4: ifne +12 -> 16
/*     */     //   7: aload_0
/*     */     //   8: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   11: aload_1
/*     */     //   12: invokevirtual 321	sun/nio/ch/DatagramChannelImpl:receive	(Ljava/nio/ByteBuffer;)Ljava/net/SocketAddress;
/*     */     //   15: areturn
/*     */     //   16: aload_0
/*     */     //   17: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   20: iconst_0
/*     */     //   21: invokevirtual 319	sun/nio/ch/DatagramChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
/*     */     //   24: pop
/*     */     //   25: aload_0
/*     */     //   26: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   29: aload_1
/*     */     //   30: invokevirtual 321	sun/nio/ch/DatagramChannelImpl:receive	(Ljava/nio/ByteBuffer;)Ljava/net/SocketAddress;
/*     */     //   33: dup
/*     */     //   34: astore_3
/*     */     //   35: ifnull +28 -> 63
/*     */     //   38: aload_3
/*     */     //   39: astore 4
/*     */     //   41: aload_0
/*     */     //   42: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   45: invokevirtual 311	sun/nio/ch/DatagramChannelImpl:isOpen	()Z
/*     */     //   48: ifeq +12 -> 60
/*     */     //   51: aload_0
/*     */     //   52: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   55: iconst_1
/*     */     //   56: invokevirtual 319	sun/nio/ch/DatagramChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
/*     */     //   59: pop
/*     */     //   60: aload 4
/*     */     //   62: areturn
/*     */     //   63: aload_0
/*     */     //   64: getfield 271	sun/nio/ch/DatagramSocketAdaptor:timeout	I
/*     */     //   67: i2l
/*     */     //   68: lstore 4
/*     */     //   70: aload_0
/*     */     //   71: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   74: invokevirtual 311	sun/nio/ch/DatagramChannelImpl:isOpen	()Z
/*     */     //   77: ifne +11 -> 88
/*     */     //   80: new 157	java/nio/channels/ClosedChannelException
/*     */     //   83: dup
/*     */     //   84: invokespecial 306	java/nio/channels/ClosedChannelException:<init>	()V
/*     */     //   87: athrow
/*     */     //   88: invokestatic 286	java/lang/System:currentTimeMillis	()J
/*     */     //   91: lstore 6
/*     */     //   93: aload_0
/*     */     //   94: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   97: getstatic 274	sun/nio/ch/Net:POLLIN	S
/*     */     //   100: lload 4
/*     */     //   102: invokevirtual 312	sun/nio/ch/DatagramChannelImpl:poll	(IJ)I
/*     */     //   105: istore 8
/*     */     //   107: iload 8
/*     */     //   109: ifle +50 -> 159
/*     */     //   112: iload 8
/*     */     //   114: getstatic 274	sun/nio/ch/Net:POLLIN	S
/*     */     //   117: iand
/*     */     //   118: ifeq +41 -> 159
/*     */     //   121: aload_0
/*     */     //   122: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   125: aload_1
/*     */     //   126: invokevirtual 321	sun/nio/ch/DatagramChannelImpl:receive	(Ljava/nio/ByteBuffer;)Ljava/net/SocketAddress;
/*     */     //   129: dup
/*     */     //   130: astore_3
/*     */     //   131: ifnull +28 -> 159
/*     */     //   134: aload_3
/*     */     //   135: astore 9
/*     */     //   137: aload_0
/*     */     //   138: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   141: invokevirtual 311	sun/nio/ch/DatagramChannelImpl:isOpen	()Z
/*     */     //   144: ifeq +12 -> 156
/*     */     //   147: aload_0
/*     */     //   148: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   151: iconst_1
/*     */     //   152: invokevirtual 319	sun/nio/ch/DatagramChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
/*     */     //   155: pop
/*     */     //   156: aload 9
/*     */     //   158: areturn
/*     */     //   159: lload 4
/*     */     //   161: invokestatic 286	java/lang/System:currentTimeMillis	()J
/*     */     //   164: lload 6
/*     */     //   166: lsub
/*     */     //   167: lsub
/*     */     //   168: lstore 4
/*     */     //   170: lload 4
/*     */     //   172: lconst_0
/*     */     //   173: lcmp
/*     */     //   174: ifgt +11 -> 185
/*     */     //   177: new 154	java/net/SocketTimeoutException
/*     */     //   180: dup
/*     */     //   181: invokespecial 303	java/net/SocketTimeoutException:<init>	()V
/*     */     //   184: athrow
/*     */     //   185: goto -115 -> 70
/*     */     //   188: astore 10
/*     */     //   190: aload_0
/*     */     //   191: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   194: invokevirtual 311	sun/nio/ch/DatagramChannelImpl:isOpen	()Z
/*     */     //   197: ifeq +12 -> 209
/*     */     //   200: aload_0
/*     */     //   201: getfield 273	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
/*     */     //   204: iconst_1
/*     */     //   205: invokevirtual 319	sun/nio/ch/DatagramChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
/*     */     //   208: pop
/*     */     //   209: aload 10
/*     */     //   211: athrow
/*     */     // Line number table:
/*     */     //   Java source line #175	-> byte code offset #0
/*     */     //   Java source line #176	-> byte code offset #7
/*     */     //   Java source line #179	-> byte code offset #16
/*     */     //   Java source line #183	-> byte code offset #25
/*     */     //   Java source line #184	-> byte code offset #38
/*     */     //   Java source line #202	-> byte code offset #41
/*     */     //   Java source line #203	-> byte code offset #51
/*     */     //   Java source line #184	-> byte code offset #60
/*     */     //   Java source line #185	-> byte code offset #63
/*     */     //   Java source line #187	-> byte code offset #70
/*     */     //   Java source line #188	-> byte code offset #80
/*     */     //   Java source line #189	-> byte code offset #88
/*     */     //   Java source line #190	-> byte code offset #93
/*     */     //   Java source line #191	-> byte code offset #107
/*     */     //   Java source line #193	-> byte code offset #121
/*     */     //   Java source line #194	-> byte code offset #134
/*     */     //   Java source line #202	-> byte code offset #137
/*     */     //   Java source line #203	-> byte code offset #147
/*     */     //   Java source line #194	-> byte code offset #156
/*     */     //   Java source line #196	-> byte code offset #159
/*     */     //   Java source line #197	-> byte code offset #170
/*     */     //   Java source line #198	-> byte code offset #177
/*     */     //   Java source line #200	-> byte code offset #185
/*     */     //   Java source line #202	-> byte code offset #188
/*     */     //   Java source line #203	-> byte code offset #200
/*     */     //   Java source line #204	-> byte code offset #209
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	212	0	this	DatagramSocketAdaptor
/*     */     //   0	212	1	paramByteBuffer	ByteBuffer
/*     */     //   34	101	3	localSocketAddress1	SocketAddress
/*     */     //   39	22	4	localSocketAddress2	SocketAddress
/*     */     //   68	103	4	l1	long
/*     */     //   91	74	6	l2	long
/*     */     //   105	13	8	i	int
/*     */     //   135	22	9	localSocketAddress3	SocketAddress
/*     */     //   188	22	10	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   25	41	188	finally
/*     */     //   63	137	188	finally
/*     */     //   159	190	188	finally
/*     */   }
/*     */   
/*     */   public void receive(DatagramPacket paramDatagramPacket)
/*     */     throws IOException
/*     */   {
/* 208 */     synchronized (this.dc.blockingLock()) {
/* 209 */       if (!this.dc.isBlocking())
/* 210 */         throw new IllegalBlockingModeException();
/*     */       try {
/* 212 */         synchronized (paramDatagramPacket) {
/* 213 */           ByteBuffer localByteBuffer = ByteBuffer.wrap(paramDatagramPacket.getData(), paramDatagramPacket
/* 214 */             .getOffset(), paramDatagramPacket
/* 215 */             .getLength());
/* 216 */           SocketAddress localSocketAddress = receive(localByteBuffer);
/* 217 */           paramDatagramPacket.setSocketAddress(localSocketAddress);
/* 218 */           paramDatagramPacket.setLength(localByteBuffer.position() - paramDatagramPacket.getOffset());
/*     */         }
/*     */       } catch (IOException localIOException) {
/* 221 */         Net.translateException(localIOException);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public InetAddress getLocalAddress() {
/* 227 */     if (isClosed())
/* 228 */       return null;
/* 229 */     Object localObject = this.dc.localAddress();
/* 230 */     if (localObject == null)
/* 231 */       localObject = new InetSocketAddress(0);
/* 232 */     InetAddress localInetAddress = ((InetSocketAddress)localObject).getAddress();
/* 233 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 234 */     if (localSecurityManager != null) {
/*     */       try {
/* 236 */         localSecurityManager.checkConnect(localInetAddress.getHostAddress(), -1);
/*     */       } catch (SecurityException localSecurityException) {
/* 238 */         return new InetSocketAddress(0).getAddress();
/*     */       }
/*     */     }
/* 241 */     return localInetAddress;
/*     */   }
/*     */   
/*     */   public int getLocalPort() {
/* 245 */     if (isClosed())
/* 246 */       return -1;
/*     */     try {
/* 248 */       SocketAddress localSocketAddress = this.dc.getLocalAddress();
/* 249 */       if (localSocketAddress != null) {
/* 250 */         return ((InetSocketAddress)localSocketAddress).getPort();
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {}
/* 254 */     return 0;
/*     */   }
/*     */   
/*     */   public void setSoTimeout(int paramInt) throws SocketException {
/* 258 */     this.timeout = paramInt;
/*     */   }
/*     */   
/*     */   public int getSoTimeout() throws SocketException {
/* 262 */     return this.timeout;
/*     */   }
/*     */   
/*     */   private void setBooleanOption(SocketOption<Boolean> paramSocketOption, boolean paramBoolean) throws SocketException
/*     */   {
/*     */     try
/*     */     {
/* 269 */       this.dc.setOption(paramSocketOption, Boolean.valueOf(paramBoolean));
/*     */     } catch (IOException localIOException) {
/* 271 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setIntOption(SocketOption<Integer> paramSocketOption, int paramInt) throws SocketException
/*     */   {
/*     */     try
/*     */     {
/* 279 */       this.dc.setOption(paramSocketOption, Integer.valueOf(paramInt));
/*     */     } catch (IOException localIOException) {
/* 281 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean getBooleanOption(SocketOption<Boolean> paramSocketOption) throws SocketException {
/*     */     try {
/* 287 */       return ((Boolean)this.dc.getOption(paramSocketOption)).booleanValue();
/*     */     } catch (IOException localIOException) {
/* 289 */       Net.translateToSocketException(localIOException); }
/* 290 */     return false;
/*     */   }
/*     */   
/*     */   private int getIntOption(SocketOption<Integer> paramSocketOption) throws SocketException
/*     */   {
/*     */     try {
/* 296 */       return ((Integer)this.dc.getOption(paramSocketOption)).intValue();
/*     */     } catch (IOException localIOException) {
/* 298 */       Net.translateToSocketException(localIOException); }
/* 299 */     return -1;
/*     */   }
/*     */   
/*     */   public void setSendBufferSize(int paramInt) throws SocketException
/*     */   {
/* 304 */     if (paramInt <= 0)
/* 305 */       throw new IllegalArgumentException("Invalid send size");
/* 306 */     setIntOption(StandardSocketOptions.SO_SNDBUF, paramInt);
/*     */   }
/*     */   
/*     */   public int getSendBufferSize() throws SocketException {
/* 310 */     return getIntOption(StandardSocketOptions.SO_SNDBUF);
/*     */   }
/*     */   
/*     */   public void setReceiveBufferSize(int paramInt) throws SocketException {
/* 314 */     if (paramInt <= 0)
/* 315 */       throw new IllegalArgumentException("Invalid receive size");
/* 316 */     setIntOption(StandardSocketOptions.SO_RCVBUF, paramInt);
/*     */   }
/*     */   
/*     */   public int getReceiveBufferSize() throws SocketException {
/* 320 */     return getIntOption(StandardSocketOptions.SO_RCVBUF);
/*     */   }
/*     */   
/*     */   public void setReuseAddress(boolean paramBoolean) throws SocketException {
/* 324 */     setBooleanOption(StandardSocketOptions.SO_REUSEADDR, paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getReuseAddress() throws SocketException {
/* 328 */     return getBooleanOption(StandardSocketOptions.SO_REUSEADDR);
/*     */   }
/*     */   
/*     */   public void setBroadcast(boolean paramBoolean) throws SocketException
/*     */   {
/* 333 */     setBooleanOption(StandardSocketOptions.SO_BROADCAST, paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getBroadcast() throws SocketException {
/* 337 */     return getBooleanOption(StandardSocketOptions.SO_BROADCAST);
/*     */   }
/*     */   
/*     */   public void setTrafficClass(int paramInt) throws SocketException {
/* 341 */     setIntOption(StandardSocketOptions.IP_TOS, paramInt);
/*     */   }
/*     */   
/*     */   public int getTrafficClass() throws SocketException {
/* 345 */     return getIntOption(StandardSocketOptions.IP_TOS);
/*     */   }
/*     */   
/*     */   public void close() {
/*     */     try {
/* 350 */       this.dc.close();
/*     */     } catch (IOException localIOException) {
/* 352 */       throw new Error(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/* 357 */     return !this.dc.isOpen();
/*     */   }
/*     */   
/*     */   public DatagramChannel getChannel() {
/* 361 */     return this.dc;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 369 */   private static final DatagramSocketImpl dummyDatagramSocket = new DatagramSocketImpl() { protected void create() throws SocketException
/*     */     {}
/*     */     
/*     */     protected void bind(int paramAnonymousInt, InetAddress paramAnonymousInetAddress) throws SocketException
/*     */     {}
/*     */     
/*     */     protected void send(DatagramPacket paramAnonymousDatagramPacket) throws IOException
/*     */     {}
/*     */     
/* 378 */     protected int peek(InetAddress paramAnonymousInetAddress) throws IOException { return 0; }
/*     */     
/* 380 */     protected int peekData(DatagramPacket paramAnonymousDatagramPacket) throws IOException { return 0; }
/*     */     
/*     */     protected void receive(DatagramPacket paramAnonymousDatagramPacket) throws IOException
/*     */     {}
/*     */     @Deprecated
/*     */     protected void setTTL(byte paramAnonymousByte) throws IOException
/*     */     {}
/*     */     @Deprecated
/* 388 */     protected byte getTTL() throws IOException { return 0; }
/*     */     
/*     */     protected void setTimeToLive(int paramAnonymousInt) throws IOException
/*     */     {}
/* 392 */     protected int getTimeToLive() throws IOException { return 0; }
/*     */     
/*     */     protected void join(InetAddress paramAnonymousInetAddress) throws IOException
/*     */     {}
/*     */     
/*     */     protected void leave(InetAddress paramAnonymousInetAddress) throws IOException
/*     */     {}
/*     */     
/*     */     protected void joinGroup(SocketAddress paramAnonymousSocketAddress, NetworkInterface paramAnonymousNetworkInterface) throws IOException
/*     */     {}
/*     */     
/*     */     protected void leaveGroup(SocketAddress paramAnonymousSocketAddress, NetworkInterface paramAnonymousNetworkInterface) throws IOException
/*     */     {}
/*     */     protected void close() {}
/* 406 */     public Object getOption(int paramAnonymousInt) throws SocketException { return null; }
/*     */     
/*     */     public void setOption(int paramAnonymousInt, Object paramAnonymousObject)
/*     */       throws SocketException
/*     */     {}
/*     */   };
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\DatagramSocketAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
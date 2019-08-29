/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.channels.ServerSocketChannel;
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
/*     */ public class ServerSocketAdaptor
/*     */   extends ServerSocket
/*     */ {
/*     */   private final ServerSocketChannelImpl ssc;
/*  48 */   private volatile int timeout = 0;
/*     */   
/*     */   public static ServerSocket create(ServerSocketChannelImpl paramServerSocketChannelImpl) {
/*     */     try {
/*  52 */       return new ServerSocketAdaptor(paramServerSocketChannelImpl);
/*     */     } catch (IOException localIOException) {
/*  54 */       throw new Error(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private ServerSocketAdaptor(ServerSocketChannelImpl paramServerSocketChannelImpl)
/*     */     throws IOException
/*     */   {
/*  62 */     this.ssc = paramServerSocketChannelImpl;
/*     */   }
/*     */   
/*     */   public void bind(SocketAddress paramSocketAddress) throws IOException
/*     */   {
/*  67 */     bind(paramSocketAddress, 50);
/*     */   }
/*     */   
/*     */   public void bind(SocketAddress paramSocketAddress, int paramInt) throws IOException {
/*  71 */     if (paramSocketAddress == null)
/*  72 */       paramSocketAddress = new InetSocketAddress(0);
/*     */     try {
/*  74 */       this.ssc.bind(paramSocketAddress, paramInt);
/*     */     } catch (Exception localException) {
/*  76 */       Net.translateException(localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public InetAddress getInetAddress() {
/*  81 */     if (!this.ssc.isBound())
/*  82 */       return null;
/*  83 */     return Net.getRevealedLocalAddress(this.ssc.localAddress()).getAddress();
/*     */   }
/*     */   
/*     */   public int getLocalPort()
/*     */   {
/*  88 */     if (!this.ssc.isBound())
/*  89 */       return -1;
/*  90 */     return Net.asInetSocketAddress(this.ssc.localAddress()).getPort();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public java.net.Socket accept()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   4: invokevirtual 231	sun/nio/ch/ServerSocketChannelImpl:blockingLock	()Ljava/lang/Object;
/*     */     //   7: dup
/*     */     //   8: astore_1
/*     */     //   9: monitorenter
/*     */     //   10: aload_0
/*     */     //   11: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   14: invokevirtual 228	sun/nio/ch/ServerSocketChannelImpl:isBound	()Z
/*     */     //   17: ifne +11 -> 28
/*     */     //   20: new 119	java/nio/channels/IllegalBlockingModeException
/*     */     //   23: dup
/*     */     //   24: invokespecial 215	java/nio/channels/IllegalBlockingModeException:<init>	()V
/*     */     //   27: athrow
/*     */     //   28: aload_0
/*     */     //   29: getfield 192	sun/nio/ch/ServerSocketAdaptor:timeout	I
/*     */     //   32: ifne +40 -> 72
/*     */     //   35: aload_0
/*     */     //   36: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   39: invokevirtual 234	sun/nio/ch/ServerSocketChannelImpl:accept	()Ljava/nio/channels/SocketChannel;
/*     */     //   42: astore_2
/*     */     //   43: aload_2
/*     */     //   44: ifnonnull +21 -> 65
/*     */     //   47: aload_0
/*     */     //   48: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   51: invokevirtual 227	sun/nio/ch/ServerSocketChannelImpl:isBlocking	()Z
/*     */     //   54: ifne +11 -> 65
/*     */     //   57: new 119	java/nio/channels/IllegalBlockingModeException
/*     */     //   60: dup
/*     */     //   61: invokespecial 215	java/nio/channels/IllegalBlockingModeException:<init>	()V
/*     */     //   64: athrow
/*     */     //   65: aload_2
/*     */     //   66: invokevirtual 216	java/nio/channels/SocketChannel:socket	()Ljava/net/Socket;
/*     */     //   69: aload_1
/*     */     //   70: monitorexit
/*     */     //   71: areturn
/*     */     //   72: aload_0
/*     */     //   73: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   76: iconst_0
/*     */     //   77: invokevirtual 233	sun/nio/ch/ServerSocketChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
/*     */     //   80: pop
/*     */     //   81: aload_0
/*     */     //   82: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   85: invokevirtual 234	sun/nio/ch/ServerSocketChannelImpl:accept	()Ljava/nio/channels/SocketChannel;
/*     */     //   88: dup
/*     */     //   89: astore_2
/*     */     //   90: ifnull +31 -> 121
/*     */     //   93: aload_2
/*     */     //   94: invokevirtual 216	java/nio/channels/SocketChannel:socket	()Ljava/net/Socket;
/*     */     //   97: astore_3
/*     */     //   98: aload_0
/*     */     //   99: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   102: invokevirtual 229	sun/nio/ch/ServerSocketChannelImpl:isOpen	()Z
/*     */     //   105: ifeq +12 -> 117
/*     */     //   108: aload_0
/*     */     //   109: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   112: iconst_1
/*     */     //   113: invokevirtual 233	sun/nio/ch/ServerSocketChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
/*     */     //   116: pop
/*     */     //   117: aload_1
/*     */     //   118: monitorexit
/*     */     //   119: aload_3
/*     */     //   120: areturn
/*     */     //   121: aload_0
/*     */     //   122: getfield 192	sun/nio/ch/ServerSocketAdaptor:timeout	I
/*     */     //   125: i2l
/*     */     //   126: lstore_3
/*     */     //   127: aload_0
/*     */     //   128: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   131: invokevirtual 229	sun/nio/ch/ServerSocketChannelImpl:isOpen	()Z
/*     */     //   134: ifne +11 -> 145
/*     */     //   137: new 118	java/nio/channels/ClosedChannelException
/*     */     //   140: dup
/*     */     //   141: invokespecial 214	java/nio/channels/ClosedChannelException:<init>	()V
/*     */     //   144: athrow
/*     */     //   145: invokestatic 208	java/lang/System:currentTimeMillis	()J
/*     */     //   148: lstore 5
/*     */     //   150: aload_0
/*     */     //   151: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   154: getstatic 191	sun/nio/ch/Net:POLLIN	S
/*     */     //   157: lload_3
/*     */     //   158: invokevirtual 230	sun/nio/ch/ServerSocketChannelImpl:poll	(IJ)I
/*     */     //   161: istore 7
/*     */     //   163: iload 7
/*     */     //   165: ifle +45 -> 210
/*     */     //   168: aload_0
/*     */     //   169: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   172: invokevirtual 234	sun/nio/ch/ServerSocketChannelImpl:accept	()Ljava/nio/channels/SocketChannel;
/*     */     //   175: dup
/*     */     //   176: astore_2
/*     */     //   177: ifnull +33 -> 210
/*     */     //   180: aload_2
/*     */     //   181: invokevirtual 216	java/nio/channels/SocketChannel:socket	()Ljava/net/Socket;
/*     */     //   184: astore 8
/*     */     //   186: aload_0
/*     */     //   187: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   190: invokevirtual 229	sun/nio/ch/ServerSocketChannelImpl:isOpen	()Z
/*     */     //   193: ifeq +12 -> 205
/*     */     //   196: aload_0
/*     */     //   197: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   200: iconst_1
/*     */     //   201: invokevirtual 233	sun/nio/ch/ServerSocketChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
/*     */     //   204: pop
/*     */     //   205: aload_1
/*     */     //   206: monitorexit
/*     */     //   207: aload 8
/*     */     //   209: areturn
/*     */     //   210: lload_3
/*     */     //   211: invokestatic 208	java/lang/System:currentTimeMillis	()J
/*     */     //   214: lload 5
/*     */     //   216: lsub
/*     */     //   217: lsub
/*     */     //   218: lstore_3
/*     */     //   219: lload_3
/*     */     //   220: lconst_0
/*     */     //   221: lcmp
/*     */     //   222: ifgt +11 -> 233
/*     */     //   225: new 116	java/net/SocketTimeoutException
/*     */     //   228: dup
/*     */     //   229: invokespecial 213	java/net/SocketTimeoutException:<init>	()V
/*     */     //   232: athrow
/*     */     //   233: goto -106 -> 127
/*     */     //   236: astore 9
/*     */     //   238: aload_0
/*     */     //   239: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   242: invokevirtual 229	sun/nio/ch/ServerSocketChannelImpl:isOpen	()Z
/*     */     //   245: ifeq +12 -> 257
/*     */     //   248: aload_0
/*     */     //   249: getfield 194	sun/nio/ch/ServerSocketAdaptor:ssc	Lsun/nio/ch/ServerSocketChannelImpl;
/*     */     //   252: iconst_1
/*     */     //   253: invokevirtual 233	sun/nio/ch/ServerSocketChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
/*     */     //   256: pop
/*     */     //   257: aload 9
/*     */     //   259: athrow
/*     */     //   260: astore_2
/*     */     //   261: aload_2
/*     */     //   262: invokestatic 217	sun/nio/ch/Net:translateException	(Ljava/lang/Exception;)V
/*     */     //   265: getstatic 193	sun/nio/ch/ServerSocketAdaptor:$assertionsDisabled	Z
/*     */     //   268: ifne +11 -> 279
/*     */     //   271: new 101	java/lang/AssertionError
/*     */     //   274: dup
/*     */     //   275: invokespecial 195	java/lang/AssertionError:<init>	()V
/*     */     //   278: athrow
/*     */     //   279: aconst_null
/*     */     //   280: aload_1
/*     */     //   281: monitorexit
/*     */     //   282: areturn
/*     */     //   283: astore 10
/*     */     //   285: aload_1
/*     */     //   286: monitorexit
/*     */     //   287: aload 10
/*     */     //   289: athrow
/*     */     // Line number table:
/*     */     //   Java source line #95	-> byte code offset #0
/*     */     //   Java source line #96	-> byte code offset #10
/*     */     //   Java source line #97	-> byte code offset #20
/*     */     //   Java source line #99	-> byte code offset #28
/*     */     //   Java source line #100	-> byte code offset #35
/*     */     //   Java source line #101	-> byte code offset #43
/*     */     //   Java source line #102	-> byte code offset #57
/*     */     //   Java source line #103	-> byte code offset #65
/*     */     //   Java source line #106	-> byte code offset #72
/*     */     //   Java source line #109	-> byte code offset #81
/*     */     //   Java source line #110	-> byte code offset #93
/*     */     //   Java source line #124	-> byte code offset #98
/*     */     //   Java source line #125	-> byte code offset #108
/*     */     //   Java source line #110	-> byte code offset #119
/*     */     //   Java source line #111	-> byte code offset #121
/*     */     //   Java source line #113	-> byte code offset #127
/*     */     //   Java source line #114	-> byte code offset #137
/*     */     //   Java source line #115	-> byte code offset #145
/*     */     //   Java source line #116	-> byte code offset #150
/*     */     //   Java source line #117	-> byte code offset #163
/*     */     //   Java source line #118	-> byte code offset #180
/*     */     //   Java source line #124	-> byte code offset #186
/*     */     //   Java source line #125	-> byte code offset #196
/*     */     //   Java source line #118	-> byte code offset #207
/*     */     //   Java source line #119	-> byte code offset #210
/*     */     //   Java source line #120	-> byte code offset #219
/*     */     //   Java source line #121	-> byte code offset #225
/*     */     //   Java source line #122	-> byte code offset #233
/*     */     //   Java source line #124	-> byte code offset #236
/*     */     //   Java source line #125	-> byte code offset #248
/*     */     //   Java source line #126	-> byte code offset #257
/*     */     //   Java source line #128	-> byte code offset #260
/*     */     //   Java source line #129	-> byte code offset #261
/*     */     //   Java source line #130	-> byte code offset #265
/*     */     //   Java source line #131	-> byte code offset #279
/*     */     //   Java source line #133	-> byte code offset #283
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	290	0	this	ServerSocketAdaptor
/*     */     //   8	278	1	Ljava/lang/Object;	Object
/*     */     //   42	139	2	localSocketChannel	java.nio.channels.SocketChannel
/*     */     //   260	2	2	localException	Exception
/*     */     //   97	23	3	localSocket1	java.net.Socket
/*     */     //   126	94	3	l1	long
/*     */     //   148	67	5	l2	long
/*     */     //   161	3	7	i	int
/*     */     //   184	24	8	localSocket2	java.net.Socket
/*     */     //   236	22	9	localObject1	Object
/*     */     //   283	5	10	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   81	98	236	finally
/*     */     //   121	186	236	finally
/*     */     //   210	238	236	finally
/*     */     //   28	69	260	java/lang/Exception
/*     */     //   72	117	260	java/lang/Exception
/*     */     //   121	205	260	java/lang/Exception
/*     */     //   210	260	260	java/lang/Exception
/*     */     //   10	71	283	finally
/*     */     //   72	119	283	finally
/*     */     //   121	207	283	finally
/*     */     //   210	282	283	finally
/*     */     //   283	287	283	finally
/*     */   }
/*     */   
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 137 */     this.ssc.close();
/*     */   }
/*     */   
/*     */   public ServerSocketChannel getChannel() {
/* 141 */     return this.ssc;
/*     */   }
/*     */   
/*     */   public boolean isBound() {
/* 145 */     return this.ssc.isBound();
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/* 149 */     return !this.ssc.isOpen();
/*     */   }
/*     */   
/*     */   public void setSoTimeout(int paramInt) throws SocketException {
/* 153 */     this.timeout = paramInt;
/*     */   }
/*     */   
/*     */   public int getSoTimeout() throws SocketException {
/* 157 */     return this.timeout;
/*     */   }
/*     */   
/*     */   public void setReuseAddress(boolean paramBoolean) throws SocketException {
/*     */     try {
/* 162 */       this.ssc.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.valueOf(paramBoolean));
/*     */     } catch (IOException localIOException) {
/* 164 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean getReuseAddress() throws SocketException {
/*     */     try {
/* 170 */       return ((Boolean)this.ssc.getOption(StandardSocketOptions.SO_REUSEADDR)).booleanValue();
/*     */     } catch (IOException localIOException) {
/* 172 */       Net.translateToSocketException(localIOException); }
/* 173 */     return false;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 178 */     if (!isBound())
/* 179 */       return "ServerSocket[unbound]";
/* 180 */     return 
/*     */     
/* 182 */       "ServerSocket[addr=" + getInetAddress() + ",localport=" + getLocalPort() + "]";
/*     */   }
/*     */   
/*     */   public void setReceiveBufferSize(int paramInt) throws SocketException
/*     */   {
/* 187 */     if (paramInt <= 0)
/* 188 */       throw new IllegalArgumentException("size cannot be 0 or negative");
/*     */     try {
/* 190 */       this.ssc.setOption(StandardSocketOptions.SO_RCVBUF, Integer.valueOf(paramInt));
/*     */     } catch (IOException localIOException) {
/* 192 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getReceiveBufferSize() throws SocketException {
/*     */     try {
/* 198 */       return ((Integer)this.ssc.getOption(StandardSocketOptions.SO_RCVBUF)).intValue();
/*     */     } catch (IOException localIOException) {
/* 200 */       Net.translateToSocketException(localIOException); }
/* 201 */     return -1;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\ServerSocketAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketImpl;
/*     */ import java.net.SocketOption;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.IllegalBlockingModeException;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
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
/*     */ public class SocketAdaptor
/*     */   extends Socket
/*     */ {
/*     */   private final SocketChannelImpl sc;
/*  58 */   private volatile int timeout = 0;
/*     */   
/*     */   private SocketAdaptor(SocketChannelImpl paramSocketChannelImpl) throws SocketException {
/*  61 */     super((SocketImpl)null);
/*  62 */     this.sc = paramSocketChannelImpl;
/*     */   }
/*     */   
/*     */   public static Socket create(SocketChannelImpl paramSocketChannelImpl) {
/*     */     try {
/*  67 */       return new SocketAdaptor(paramSocketChannelImpl);
/*     */     } catch (SocketException localSocketException) {
/*  69 */       throw new InternalError("Should not reach here");
/*     */     }
/*     */   }
/*     */   
/*     */   public SocketChannel getChannel() {
/*  74 */     return this.sc;
/*     */   }
/*     */   
/*     */   public void connect(SocketAddress paramSocketAddress)
/*     */     throws IOException
/*     */   {
/*  80 */     connect(paramSocketAddress, 0);
/*     */   }
/*     */   
/*     */   public void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException {
/*  84 */     if (paramSocketAddress == null)
/*  85 */       throw new IllegalArgumentException("connect: The address can't be null");
/*  86 */     if (paramInt < 0) {
/*  87 */       throw new IllegalArgumentException("connect: timeout can't be negative");
/*     */     }
/*  89 */     synchronized (this.sc.blockingLock()) {
/*  90 */       if (!this.sc.isBlocking()) {
/*  91 */         throw new IllegalBlockingModeException();
/*     */       }
/*     */       try
/*     */       {
/*  95 */         if (paramInt == 0) {
/*  96 */           this.sc.connect(paramSocketAddress);
/*  97 */           return;
/*     */         }
/*     */         
/* 100 */         this.sc.configureBlocking(false);
/*     */         try {
/* 102 */           if (this.sc.connect(paramSocketAddress))
/*     */           {
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
/* 122 */             if (this.sc.isOpen()) {
/* 123 */               this.sc.configureBlocking(true);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 104 */             long l1 = paramInt;
/*     */             for (;;) {
/* 106 */               if (!this.sc.isOpen())
/* 107 */                 throw new ClosedChannelException();
/* 108 */               long l2 = System.currentTimeMillis();
/*     */               
/* 110 */               int i = this.sc.poll(Net.POLLCONN, l1);
/* 111 */               if ((i > 0) && (this.sc.finishConnect()))
/*     */                 break;
/* 113 */               l1 -= System.currentTimeMillis() - l2;
/* 114 */               if (l1 <= 0L) {
/*     */                 try {
/* 116 */                   this.sc.close();
/*     */                 } catch (IOException localIOException) {}
/* 118 */                 throw new SocketTimeoutException();
/*     */               }
/*     */             }
/*     */           }
/* 122 */         } finally { if (this.sc.isOpen()) {
/* 123 */             this.sc.configureBlocking(true);
/*     */           }
/*     */         }
/*     */       } catch (Exception localException) {
/* 127 */         Net.translateException(localException, true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void bind(SocketAddress paramSocketAddress) throws IOException
/*     */   {
/*     */     try {
/* 135 */       this.sc.bind(paramSocketAddress);
/*     */     } catch (Exception localException) {
/* 137 */       Net.translateException(localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public InetAddress getInetAddress() {
/* 142 */     SocketAddress localSocketAddress = this.sc.remoteAddress();
/* 143 */     if (localSocketAddress == null) {
/* 144 */       return null;
/*     */     }
/* 146 */     return ((InetSocketAddress)localSocketAddress).getAddress();
/*     */   }
/*     */   
/*     */   public InetAddress getLocalAddress()
/*     */   {
/* 151 */     if (this.sc.isOpen()) {
/* 152 */       InetSocketAddress localInetSocketAddress = this.sc.localAddress();
/* 153 */       if (localInetSocketAddress != null) {
/* 154 */         return Net.getRevealedLocalAddress(localInetSocketAddress).getAddress();
/*     */       }
/*     */     }
/* 157 */     return new InetSocketAddress(0).getAddress();
/*     */   }
/*     */   
/*     */   public int getPort() {
/* 161 */     SocketAddress localSocketAddress = this.sc.remoteAddress();
/* 162 */     if (localSocketAddress == null) {
/* 163 */       return 0;
/*     */     }
/* 165 */     return ((InetSocketAddress)localSocketAddress).getPort();
/*     */   }
/*     */   
/*     */   public int getLocalPort()
/*     */   {
/* 170 */     InetSocketAddress localInetSocketAddress = this.sc.localAddress();
/* 171 */     if (localInetSocketAddress == null) {
/* 172 */       return -1;
/*     */     }
/* 174 */     return ((InetSocketAddress)localInetSocketAddress).getPort();
/*     */   }
/*     */   
/*     */   private class SocketInputStream
/*     */     extends ChannelInputStream
/*     */   {
/*     */     private SocketInputStream()
/*     */     {
/* 182 */       super();
/*     */     }
/*     */     
/*     */     protected int read(ByteBuffer paramByteBuffer)
/*     */       throws IOException
/*     */     {
/* 188 */       synchronized (SocketAdaptor.this.sc.blockingLock()) {
/* 189 */         if (!SocketAdaptor.this.sc.isBlocking())
/* 190 */           throw new IllegalBlockingModeException();
/* 191 */         if (SocketAdaptor.this.timeout == 0)
/* 192 */           return SocketAdaptor.this.sc.read(paramByteBuffer);
/* 193 */         SocketAdaptor.this.sc.configureBlocking(false);
/*     */         try
/*     */         {
/*     */           int i;
/* 197 */           if ((i = SocketAdaptor.this.sc.read(paramByteBuffer)) != 0) {
/* 198 */             int j = i;
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
/* 214 */             if (SocketAdaptor.this.sc.isOpen()) {
/* 215 */               SocketAdaptor.this.sc.configureBlocking(true);
/*     */             }
/* 198 */             return j; }
/* 199 */           long l1 = SocketAdaptor.this.timeout;
/*     */           for (;;) {
/* 201 */             if (!SocketAdaptor.this.sc.isOpen())
/* 202 */               throw new ClosedChannelException();
/* 203 */             long l2 = System.currentTimeMillis();
/* 204 */             int k = SocketAdaptor.this.sc.poll(Net.POLLIN, l1);
/* 205 */             if ((k > 0) && 
/* 206 */               ((i = SocketAdaptor.this.sc.read(paramByteBuffer)) != 0)) {
/* 207 */               int m = i;
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */               if (SocketAdaptor.this.sc.isOpen()) {
/* 215 */                 SocketAdaptor.this.sc.configureBlocking(true);
/*     */               }
/* 207 */               return m;
/*     */             }
/* 209 */             l1 -= System.currentTimeMillis() - l2;
/* 210 */             if (l1 <= 0L) {
/* 211 */               throw new SocketTimeoutException();
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 218 */           localObject2 = finally;
/*     */         }
/*     */         finally
/*     */         {
/* 214 */           if (SocketAdaptor.this.sc.isOpen()) {
/* 215 */             SocketAdaptor.this.sc.configureBlocking(true);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 222 */   private InputStream socketInputStream = null;
/*     */   
/*     */   public InputStream getInputStream() throws IOException {
/* 225 */     if (!this.sc.isOpen())
/* 226 */       throw new SocketException("Socket is closed");
/* 227 */     if (!this.sc.isConnected())
/* 228 */       throw new SocketException("Socket is not connected");
/* 229 */     if (!this.sc.isInputOpen())
/* 230 */       throw new SocketException("Socket input is shutdown");
/* 231 */     if (this.socketInputStream == null) {
/*     */       try {
/* 233 */         this.socketInputStream = ((InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */         {
/*     */           public InputStream run() throws IOException {
/* 236 */             return new SocketInputStream(SocketAdaptor.this, null);
/*     */           }
/*     */         }));
/*     */       } catch (PrivilegedActionException localPrivilegedActionException) {
/* 240 */         throw ((IOException)localPrivilegedActionException.getException());
/*     */       }
/*     */     }
/* 243 */     return this.socketInputStream;
/*     */   }
/*     */   
/*     */   public OutputStream getOutputStream() throws IOException {
/* 247 */     if (!this.sc.isOpen())
/* 248 */       throw new SocketException("Socket is closed");
/* 249 */     if (!this.sc.isConnected())
/* 250 */       throw new SocketException("Socket is not connected");
/* 251 */     if (!this.sc.isOutputOpen())
/* 252 */       throw new SocketException("Socket output is shutdown");
/* 253 */     OutputStream localOutputStream = null;
/*     */     try {
/* 255 */       localOutputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public OutputStream run() throws IOException {
/* 258 */           return Channels.newOutputStream(SocketAdaptor.this.sc);
/*     */         }
/*     */       });
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 262 */       throw ((IOException)localPrivilegedActionException.getException());
/*     */     }
/* 264 */     return localOutputStream;
/*     */   }
/*     */   
/*     */   private void setBooleanOption(SocketOption<Boolean> paramSocketOption, boolean paramBoolean) throws SocketException
/*     */   {
/*     */     try
/*     */     {
/* 271 */       this.sc.setOption(paramSocketOption, Boolean.valueOf(paramBoolean));
/*     */     } catch (IOException localIOException) {
/* 273 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setIntOption(SocketOption<Integer> paramSocketOption, int paramInt) throws SocketException
/*     */   {
/*     */     try
/*     */     {
/* 281 */       this.sc.setOption(paramSocketOption, Integer.valueOf(paramInt));
/*     */     } catch (IOException localIOException) {
/* 283 */       Net.translateToSocketException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean getBooleanOption(SocketOption<Boolean> paramSocketOption) throws SocketException {
/*     */     try {
/* 289 */       return ((Boolean)this.sc.getOption(paramSocketOption)).booleanValue();
/*     */     } catch (IOException localIOException) {
/* 291 */       Net.translateToSocketException(localIOException); }
/* 292 */     return false;
/*     */   }
/*     */   
/*     */   private int getIntOption(SocketOption<Integer> paramSocketOption) throws SocketException
/*     */   {
/*     */     try {
/* 298 */       return ((Integer)this.sc.getOption(paramSocketOption)).intValue();
/*     */     } catch (IOException localIOException) {
/* 300 */       Net.translateToSocketException(localIOException); }
/* 301 */     return -1;
/*     */   }
/*     */   
/*     */   public void setTcpNoDelay(boolean paramBoolean) throws SocketException
/*     */   {
/* 306 */     setBooleanOption(StandardSocketOptions.TCP_NODELAY, paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getTcpNoDelay() throws SocketException {
/* 310 */     return getBooleanOption(StandardSocketOptions.TCP_NODELAY);
/*     */   }
/*     */   
/*     */   public void setSoLinger(boolean paramBoolean, int paramInt) throws SocketException {
/* 314 */     if (!paramBoolean)
/* 315 */       paramInt = -1;
/* 316 */     setIntOption(StandardSocketOptions.SO_LINGER, paramInt);
/*     */   }
/*     */   
/*     */   public int getSoLinger() throws SocketException {
/* 320 */     return getIntOption(StandardSocketOptions.SO_LINGER);
/*     */   }
/*     */   
/*     */   public void sendUrgentData(int paramInt) throws IOException {
/* 324 */     int i = this.sc.sendOutOfBandData((byte)paramInt);
/* 325 */     if (i == 0)
/* 326 */       throw new IOException("Socket buffer full");
/*     */   }
/*     */   
/*     */   public void setOOBInline(boolean paramBoolean) throws SocketException {
/* 330 */     setBooleanOption(ExtendedSocketOption.SO_OOBINLINE, paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getOOBInline() throws SocketException {
/* 334 */     return getBooleanOption(ExtendedSocketOption.SO_OOBINLINE);
/*     */   }
/*     */   
/*     */   public void setSoTimeout(int paramInt) throws SocketException {
/* 338 */     if (paramInt < 0)
/* 339 */       throw new IllegalArgumentException("timeout can't be negative");
/* 340 */     this.timeout = paramInt;
/*     */   }
/*     */   
/*     */   public int getSoTimeout() throws SocketException {
/* 344 */     return this.timeout;
/*     */   }
/*     */   
/*     */   public void setSendBufferSize(int paramInt) throws SocketException
/*     */   {
/* 349 */     if (paramInt <= 0)
/* 350 */       throw new IllegalArgumentException("Invalid send size");
/* 351 */     setIntOption(StandardSocketOptions.SO_SNDBUF, paramInt);
/*     */   }
/*     */   
/*     */   public int getSendBufferSize() throws SocketException {
/* 355 */     return getIntOption(StandardSocketOptions.SO_SNDBUF);
/*     */   }
/*     */   
/*     */   public void setReceiveBufferSize(int paramInt) throws SocketException
/*     */   {
/* 360 */     if (paramInt <= 0)
/* 361 */       throw new IllegalArgumentException("Invalid receive size");
/* 362 */     setIntOption(StandardSocketOptions.SO_RCVBUF, paramInt);
/*     */   }
/*     */   
/*     */   public int getReceiveBufferSize() throws SocketException {
/* 366 */     return getIntOption(StandardSocketOptions.SO_RCVBUF);
/*     */   }
/*     */   
/*     */   public void setKeepAlive(boolean paramBoolean) throws SocketException {
/* 370 */     setBooleanOption(StandardSocketOptions.SO_KEEPALIVE, paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getKeepAlive() throws SocketException {
/* 374 */     return getBooleanOption(StandardSocketOptions.SO_KEEPALIVE);
/*     */   }
/*     */   
/*     */   public void setTrafficClass(int paramInt) throws SocketException {
/* 378 */     setIntOption(StandardSocketOptions.IP_TOS, paramInt);
/*     */   }
/*     */   
/*     */   public int getTrafficClass() throws SocketException {
/* 382 */     return getIntOption(StandardSocketOptions.IP_TOS);
/*     */   }
/*     */   
/*     */   public void setReuseAddress(boolean paramBoolean) throws SocketException {
/* 386 */     setBooleanOption(StandardSocketOptions.SO_REUSEADDR, paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean getReuseAddress() throws SocketException {
/* 390 */     return getBooleanOption(StandardSocketOptions.SO_REUSEADDR);
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 394 */     this.sc.close();
/*     */   }
/*     */   
/*     */   public void shutdownInput() throws IOException {
/*     */     try {
/* 399 */       this.sc.shutdownInput();
/*     */     } catch (Exception localException) {
/* 401 */       Net.translateException(localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public void shutdownOutput() throws IOException {
/*     */     try {
/* 407 */       this.sc.shutdownOutput();
/*     */     } catch (Exception localException) {
/* 409 */       Net.translateException(localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 414 */     if (this.sc.isConnected())
/* 415 */       return 
/*     */       
/* 417 */         "Socket[addr=" + getInetAddress() + ",port=" + getPort() + ",localport=" + getLocalPort() + "]";
/* 418 */     return "Socket[unconnected]";
/*     */   }
/*     */   
/*     */   public boolean isConnected() {
/* 422 */     return this.sc.isConnected();
/*     */   }
/*     */   
/*     */   public boolean isBound() {
/* 426 */     return this.sc.localAddress() != null;
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/* 430 */     return !this.sc.isOpen();
/*     */   }
/*     */   
/*     */   public boolean isInputShutdown() {
/* 434 */     return !this.sc.isInputOpen();
/*     */   }
/*     */   
/*     */   public boolean isOutputShutdown() {
/* 438 */     return !this.sc.isOutputOpen();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\SocketAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
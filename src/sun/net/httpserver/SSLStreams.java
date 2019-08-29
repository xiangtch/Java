/*     */ package sun.net.httpserver;
/*     */ 
/*     */ import com.sun.net.httpserver.HttpsConfigurator;
/*     */ import com.sun.net.httpserver.HttpsParameters;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLEngineResult;
/*     */ import javax.net.ssl.SSLEngineResult.HandshakeStatus;
/*     */ import javax.net.ssl.SSLEngineResult.Status;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLParameters;
/*     */ import javax.net.ssl.SSLSession;
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
/*     */ class SSLStreams
/*     */ {
/*     */   SSLContext sslctx;
/*     */   SocketChannel chan;
/*     */   TimeSource time;
/*     */   ServerImpl server;
/*     */   SSLEngine engine;
/*     */   EngineWrapper wrapper;
/*     */   OutputStream os;
/*     */   InputStream is;
/*  55 */   Lock handshaking = new ReentrantLock();
/*     */   int app_buf_size;
/*     */   
/*  58 */   SSLStreams(ServerImpl paramServerImpl, SSLContext paramSSLContext, SocketChannel paramSocketChannel) throws IOException { this.server = paramServerImpl;
/*  59 */     this.time = paramServerImpl;
/*  60 */     this.sslctx = paramSSLContext;
/*  61 */     this.chan = paramSocketChannel;
/*     */     
/*  63 */     InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketChannel.socket().getRemoteSocketAddress();
/*  64 */     this.engine = paramSSLContext.createSSLEngine(localInetSocketAddress.getHostName(), localInetSocketAddress.getPort());
/*  65 */     this.engine.setUseClientMode(false);
/*  66 */     HttpsConfigurator localHttpsConfigurator = paramServerImpl.getHttpsConfigurator();
/*  67 */     configureEngine(localHttpsConfigurator, localInetSocketAddress);
/*  68 */     this.wrapper = new EngineWrapper(paramSocketChannel, this.engine);
/*     */   }
/*     */   
/*     */   private void configureEngine(HttpsConfigurator paramHttpsConfigurator, InetSocketAddress paramInetSocketAddress) {
/*  72 */     if (paramHttpsConfigurator != null) {
/*  73 */       Parameters localParameters = new Parameters(paramHttpsConfigurator, paramInetSocketAddress);
/*     */       
/*  75 */       paramHttpsConfigurator.configure(localParameters);
/*  76 */       SSLParameters localSSLParameters = localParameters.getSSLParameters();
/*  77 */       if (localSSLParameters != null) {
/*  78 */         this.engine.setSSLParameters(localSSLParameters);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*  83 */         if (localParameters.getCipherSuites() != null) {
/*     */           try {
/*  85 */             this.engine.setEnabledCipherSuites(localParameters
/*  86 */               .getCipherSuites());
/*     */           }
/*     */           catch (IllegalArgumentException localIllegalArgumentException1) {}
/*     */         }
/*  90 */         this.engine.setNeedClientAuth(localParameters.getNeedClientAuth());
/*  91 */         this.engine.setWantClientAuth(localParameters.getWantClientAuth());
/*  92 */         if (localParameters.getProtocols() != null) {
/*     */           try {
/*  94 */             this.engine.setEnabledProtocols(localParameters
/*  95 */               .getProtocols());
/*     */           } catch (IllegalArgumentException localIllegalArgumentException2) {}
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   class Parameters extends HttpsParameters {
/*     */     InetSocketAddress addr;
/*     */     HttpsConfigurator cfg;
/*     */     SSLParameters params;
/*     */     
/*     */     Parameters(HttpsConfigurator paramHttpsConfigurator, InetSocketAddress paramInetSocketAddress) {
/* 108 */       this.addr = paramInetSocketAddress;
/* 109 */       this.cfg = paramHttpsConfigurator;
/*     */     }
/*     */     
/* 112 */     public InetSocketAddress getClientAddress() { return this.addr; }
/*     */     
/*     */     public HttpsConfigurator getHttpsConfigurator() {
/* 115 */       return this.cfg;
/*     */     }
/*     */     
/*     */     public void setSSLParameters(SSLParameters paramSSLParameters)
/*     */     {
/* 120 */       this.params = paramSSLParameters;
/*     */     }
/*     */     
/* 123 */     SSLParameters getSSLParameters() { return this.params; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void close()
/*     */     throws IOException
/*     */   {
/* 132 */     this.wrapper.close();
/*     */   }
/*     */   
/*     */ 
/*     */   InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 139 */     if (this.is == null) {
/* 140 */       this.is = new InputStream();
/*     */     }
/* 142 */     return this.is;
/*     */   }
/*     */   
/*     */ 
/*     */   OutputStream getOutputStream()
/*     */     throws IOException
/*     */   {
/* 149 */     if (this.os == null) {
/* 150 */       this.os = new OutputStream();
/*     */     }
/* 152 */     return this.os;
/*     */   }
/*     */   
/*     */   SSLEngine getSSLEngine() {
/* 156 */     return this.engine;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void beginHandshake()
/*     */     throws SSLException
/*     */   {
/* 165 */     this.engine.beginHandshake();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int packet_buf_size;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static enum BufType
/*     */   {
/* 181 */     PACKET,  APPLICATION;
/*     */     
/*     */     private BufType() {} }
/*     */   
/* 185 */   private ByteBuffer allocate(BufType paramBufType) { return allocate(paramBufType, -1); }
/*     */   
/*     */   private ByteBuffer allocate(BufType paramBufType, int paramInt)
/*     */   {
/* 189 */     assert (this.engine != null);
/* 190 */     synchronized (this) { SSLSession localSSLSession;
/*     */       int i;
/* 192 */       if (paramBufType == BufType.PACKET) {
/* 193 */         if (this.packet_buf_size == 0) {
/* 194 */           localSSLSession = this.engine.getSession();
/* 195 */           this.packet_buf_size = localSSLSession.getPacketBufferSize();
/*     */         }
/* 197 */         if (paramInt > this.packet_buf_size) {
/* 198 */           this.packet_buf_size = paramInt;
/*     */         }
/* 200 */         i = this.packet_buf_size;
/*     */       } else {
/* 202 */         if (this.app_buf_size == 0) {
/* 203 */           localSSLSession = this.engine.getSession();
/* 204 */           this.app_buf_size = localSSLSession.getApplicationBufferSize();
/*     */         }
/* 206 */         if (paramInt > this.app_buf_size) {
/* 207 */           this.app_buf_size = paramInt;
/*     */         }
/* 209 */         i = this.app_buf_size;
/*     */       }
/* 211 */       return ByteBuffer.allocate(i);
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
/*     */   private ByteBuffer realloc(ByteBuffer paramByteBuffer, boolean paramBoolean, BufType paramBufType)
/*     */   {
/* 224 */     synchronized (this) {
/* 225 */       int i = 2 * paramByteBuffer.capacity();
/* 226 */       ByteBuffer localByteBuffer = allocate(paramBufType, i);
/* 227 */       if (paramBoolean) {
/* 228 */         paramByteBuffer.flip();
/*     */       }
/* 230 */       localByteBuffer.put(paramByteBuffer);
/* 231 */       paramByteBuffer = localByteBuffer;
/*     */     }
/* 233 */     return paramByteBuffer;
/*     */   }
/*     */   
/*     */ 
/*     */   class EngineWrapper
/*     */   {
/*     */     SocketChannel chan;
/*     */     
/*     */     SSLEngine engine;
/*     */     
/*     */     Object wrapLock;
/*     */     
/*     */     Object unwrapLock;
/*     */     
/*     */     ByteBuffer unwrap_src;
/*     */     
/*     */     ByteBuffer wrap_dst;
/* 250 */     boolean closed = false;
/*     */     int u_remaining;
/*     */     
/*     */     EngineWrapper(SocketChannel paramSocketChannel, SSLEngine paramSSLEngine) throws IOException {
/* 254 */       this.chan = paramSocketChannel;
/* 255 */       this.engine = paramSSLEngine;
/* 256 */       this.wrapLock = new Object();
/* 257 */       this.unwrapLock = new Object();
/* 258 */       this.unwrap_src = SSLStreams.this.allocate(BufType.PACKET);
/* 259 */       this.wrap_dst = SSLStreams.this.allocate(BufType.PACKET);
/*     */     }
/*     */     
/*     */ 
/*     */     void close()
/*     */       throws IOException
/*     */     {}
/*     */     
/*     */ 
/*     */     WrapperResult wrapAndSend(ByteBuffer paramByteBuffer)
/*     */       throws IOException
/*     */     {
/* 271 */       return wrapAndSendX(paramByteBuffer, false);
/*     */     }
/*     */     
/*     */     WrapperResult wrapAndSendX(ByteBuffer paramByteBuffer, boolean paramBoolean) throws IOException {
/* 275 */       if ((this.closed) && (!paramBoolean)) {
/* 276 */         throw new IOException("Engine is closed");
/*     */       }
/*     */       
/* 279 */       WrapperResult localWrapperResult = new WrapperResult(SSLStreams.this);
/* 280 */       synchronized (this.wrapLock) {
/* 281 */         this.wrap_dst.clear();
/*     */         SSLEngineResult.Status localStatus;
/* 283 */         do { localWrapperResult.result = this.engine.wrap(paramByteBuffer, this.wrap_dst);
/* 284 */           localStatus = localWrapperResult.result.getStatus();
/* 285 */           if (localStatus == SSLEngineResult.Status.BUFFER_OVERFLOW) {
/* 286 */             this.wrap_dst = SSLStreams.this.realloc(this.wrap_dst, true, BufType.PACKET);
/*     */           }
/* 288 */         } while (localStatus == SSLEngineResult.Status.BUFFER_OVERFLOW);
/* 289 */         if ((localStatus == SSLEngineResult.Status.CLOSED) && (!paramBoolean)) {
/* 290 */           this.closed = true;
/* 291 */           return localWrapperResult;
/*     */         }
/* 293 */         if (localWrapperResult.result.bytesProduced() > 0) {
/* 294 */           this.wrap_dst.flip();
/* 295 */           int i = this.wrap_dst.remaining();
/* 296 */           assert (i == localWrapperResult.result.bytesProduced());
/* 297 */           while (i > 0) {
/* 298 */             i -= this.chan.write(this.wrap_dst);
/*     */           }
/*     */         }
/*     */       }
/* 302 */       return localWrapperResult;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     WrapperResult recvAndUnwrap(ByteBuffer paramByteBuffer)
/*     */       throws IOException
/*     */     {
/* 311 */       SSLEngineResult.Status localStatus = SSLEngineResult.Status.OK;
/* 312 */       WrapperResult localWrapperResult = new WrapperResult(SSLStreams.this);
/* 313 */       localWrapperResult.buf = paramByteBuffer;
/* 314 */       if (this.closed) {
/* 315 */         throw new IOException("Engine is closed");
/*     */       }
/*     */       int i;
/* 318 */       if (this.u_remaining > 0) {
/* 319 */         this.unwrap_src.compact();
/* 320 */         this.unwrap_src.flip();
/* 321 */         i = 0;
/*     */       } else {
/* 323 */         this.unwrap_src.clear();
/* 324 */         i = 1;
/*     */       }
/* 326 */       synchronized (this.unwrapLock)
/*     */       {
/*     */         do {
/* 329 */           if (i != 0) {
/*     */             int j;
/* 331 */             do { j = this.chan.read(this.unwrap_src);
/* 332 */             } while (j == 0);
/* 333 */             if (j == -1) {
/* 334 */               throw new IOException("connection closed for reading");
/*     */             }
/* 336 */             this.unwrap_src.flip();
/*     */           }
/* 338 */           localWrapperResult.result = this.engine.unwrap(this.unwrap_src, localWrapperResult.buf);
/* 339 */           localStatus = localWrapperResult.result.getStatus();
/* 340 */           if (localStatus == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
/* 341 */             if (this.unwrap_src.limit() == this.unwrap_src.capacity())
/*     */             {
/* 343 */               this.unwrap_src = SSLStreams.this.realloc(this.unwrap_src, false, BufType.PACKET);
/*     */ 
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/*     */ 
/* 351 */               this.unwrap_src.position(this.unwrap_src.limit());
/* 352 */               this.unwrap_src.limit(this.unwrap_src.capacity());
/*     */             }
/* 354 */             i = 1;
/* 355 */           } else if (localStatus == SSLEngineResult.Status.BUFFER_OVERFLOW) {
/* 356 */             localWrapperResult.buf = SSLStreams.this.realloc(localWrapperResult.buf, true, BufType.APPLICATION);
/* 357 */             i = 0;
/* 358 */           } else if (localStatus == SSLEngineResult.Status.CLOSED) {
/* 359 */             this.closed = true;
/* 360 */             localWrapperResult.buf.flip();
/* 361 */             return localWrapperResult;
/*     */           }
/* 363 */         } while (localStatus != SSLEngineResult.Status.OK);
/*     */       }
/* 365 */       this.u_remaining = this.unwrap_src.remaining();
/* 366 */       return localWrapperResult;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public WrapperResult sendData(ByteBuffer paramByteBuffer)
/*     */     throws IOException
/*     */   {
/* 377 */     WrapperResult localWrapperResult = null;
/* 378 */     while (paramByteBuffer.remaining() > 0) {
/* 379 */       localWrapperResult = this.wrapper.wrapAndSend(paramByteBuffer);
/* 380 */       SSLEngineResult.Status localStatus = localWrapperResult.result.getStatus();
/* 381 */       if (localStatus == SSLEngineResult.Status.CLOSED) {
/* 382 */         doClosure();
/* 383 */         return localWrapperResult;
/*     */       }
/* 385 */       SSLEngineResult.HandshakeStatus localHandshakeStatus = localWrapperResult.result.getHandshakeStatus();
/* 386 */       if ((localHandshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED) && (localHandshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING))
/*     */       {
/*     */ 
/* 389 */         doHandshake(localHandshakeStatus);
/*     */       }
/*     */     }
/* 392 */     return localWrapperResult;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public WrapperResult recvData(ByteBuffer paramByteBuffer)
/*     */     throws IOException
/*     */   {
/* 403 */     WrapperResult localWrapperResult = null;
/* 404 */     assert (paramByteBuffer.position() == 0);
/* 405 */     while (paramByteBuffer.position() == 0) {
/* 406 */       localWrapperResult = this.wrapper.recvAndUnwrap(paramByteBuffer);
/* 407 */       paramByteBuffer = localWrapperResult.buf != paramByteBuffer ? localWrapperResult.buf : paramByteBuffer;
/* 408 */       SSLEngineResult.Status localStatus = localWrapperResult.result.getStatus();
/* 409 */       if (localStatus == SSLEngineResult.Status.CLOSED) {
/* 410 */         doClosure();
/* 411 */         return localWrapperResult;
/*     */       }
/*     */       
/* 414 */       SSLEngineResult.HandshakeStatus localHandshakeStatus = localWrapperResult.result.getHandshakeStatus();
/* 415 */       if ((localHandshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED) && (localHandshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING))
/*     */       {
/*     */ 
/* 418 */         doHandshake(localHandshakeStatus);
/*     */       }
/*     */     }
/* 421 */     paramByteBuffer.flip();
/* 422 */     return localWrapperResult;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   void doClosure()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 276	sun/net/httpserver/SSLStreams:handshaking	Ljava/util/concurrent/locks/Lock;
/*     */     //   4: invokeinterface 339 1 0
/*     */     //   9: aload_0
/*     */     //   10: getstatic 285	sun/net/httpserver/SSLStreams$BufType:APPLICATION	Lsun/net/httpserver/SSLStreams$BufType;
/*     */     //   13: invokespecial 321	sun/net/httpserver/SSLStreams:allocate	(Lsun/net/httpserver/SSLStreams$BufType;)Ljava/nio/ByteBuffer;
/*     */     //   16: astore_1
/*     */     //   17: aload_1
/*     */     //   18: invokevirtual 299	java/nio/ByteBuffer:clear	()Ljava/nio/Buffer;
/*     */     //   21: pop
/*     */     //   22: aload_1
/*     */     //   23: invokevirtual 300	java/nio/ByteBuffer:flip	()Ljava/nio/Buffer;
/*     */     //   26: pop
/*     */     //   27: aload_0
/*     */     //   28: getfield 279	sun/net/httpserver/SSLStreams:wrapper	Lsun/net/httpserver/SSLStreams$EngineWrapper;
/*     */     //   31: aload_1
/*     */     //   32: iconst_1
/*     */     //   33: invokevirtual 327	sun/net/httpserver/SSLStreams$EngineWrapper:wrapAndSendX	(Ljava/nio/ByteBuffer;Z)Lsun/net/httpserver/SSLStreams$WrapperResult;
/*     */     //   36: astore_2
/*     */     //   37: aload_2
/*     */     //   38: getfield 288	sun/net/httpserver/SSLStreams$WrapperResult:result	Ljavax/net/ssl/SSLEngineResult;
/*     */     //   41: invokevirtual 316	javax/net/ssl/SSLEngineResult:getStatus	()Ljavax/net/ssl/SSLEngineResult$Status;
/*     */     //   44: getstatic 271	javax/net/ssl/SSLEngineResult$Status:CLOSED	Ljavax/net/ssl/SSLEngineResult$Status;
/*     */     //   47: if_acmpne -30 -> 17
/*     */     //   50: aload_0
/*     */     //   51: getfield 276	sun/net/httpserver/SSLStreams:handshaking	Ljava/util/concurrent/locks/Lock;
/*     */     //   54: invokeinterface 340 1 0
/*     */     //   59: goto +15 -> 74
/*     */     //   62: astore_3
/*     */     //   63: aload_0
/*     */     //   64: getfield 276	sun/net/httpserver/SSLStreams:handshaking	Ljava/util/concurrent/locks/Lock;
/*     */     //   67: invokeinterface 340 1 0
/*     */     //   72: aload_3
/*     */     //   73: athrow
/*     */     //   74: return
/*     */     // Line number table:
/*     */     //   Java source line #430	-> byte code offset #0
/*     */     //   Java source line #431	-> byte code offset #9
/*     */     //   Java source line #434	-> byte code offset #17
/*     */     //   Java source line #435	-> byte code offset #22
/*     */     //   Java source line #436	-> byte code offset #27
/*     */     //   Java source line #437	-> byte code offset #37
/*     */     //   Java source line #439	-> byte code offset #50
/*     */     //   Java source line #440	-> byte code offset #59
/*     */     //   Java source line #439	-> byte code offset #62
/*     */     //   Java source line #440	-> byte code offset #72
/*     */     //   Java source line #441	-> byte code offset #74
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	75	0	this	SSLStreams
/*     */     //   16	16	1	localByteBuffer	ByteBuffer
/*     */     //   36	2	2	localWrapperResult	WrapperResult
/*     */     //   62	11	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	50	62	finally
/*     */   }
/*     */   
/*     */   void doHandshake(SSLEngineResult.HandshakeStatus paramHandshakeStatus)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 451 */       this.handshaking.lock();
/* 452 */       ByteBuffer localByteBuffer = allocate(BufType.APPLICATION);
/* 453 */       while ((paramHandshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED) && (paramHandshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING))
/*     */       {
/*     */ 
/* 456 */         WrapperResult localWrapperResult = null;
/* 457 */         switch (paramHandshakeStatus) {
/*     */         case NEED_TASK: 
/*     */           Runnable localRunnable;
/* 460 */           while ((localRunnable = this.engine.getDelegatedTask()) != null)
/*     */           {
/*     */ 
/*     */ 
/* 464 */             localRunnable.run();
/*     */           }
/*     */         
/*     */         case NEED_WRAP: 
/* 468 */           localByteBuffer.clear();
/* 469 */           localByteBuffer.flip();
/* 470 */           localWrapperResult = this.wrapper.wrapAndSend(localByteBuffer);
/* 471 */           break;
/*     */         
/*     */         case NEED_UNWRAP: 
/* 474 */           localByteBuffer.clear();
/* 475 */           localWrapperResult = this.wrapper.recvAndUnwrap(localByteBuffer);
/* 476 */           if (localWrapperResult.buf != localByteBuffer) {
/* 477 */             localByteBuffer = localWrapperResult.buf;
/*     */           }
/* 479 */           assert (localByteBuffer.position() == 0);
/*     */         }
/*     */         
/* 482 */         paramHandshakeStatus = localWrapperResult.result.getHandshakeStatus();
/*     */       }
/*     */     } finally {
/* 485 */       this.handshaking.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   class InputStream
/*     */     extends InputStream
/*     */   {
/*     */     ByteBuffer bbuf;
/*     */     
/*     */ 
/* 497 */     boolean closed = false;
/*     */     
/*     */ 
/* 500 */     boolean eof = false;
/*     */     
/* 502 */     boolean needData = true;
/*     */     
/*     */     InputStream() {
/* 505 */       this.bbuf = SSLStreams.this.allocate(BufType.APPLICATION);
/*     */     }
/*     */     
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 509 */       if (this.closed) {
/* 510 */         throw new IOException("SSL stream is closed");
/*     */       }
/* 512 */       if (this.eof) {
/* 513 */         return 0;
/*     */       }
/* 515 */       int i = 0;
/* 516 */       if (!this.needData) {
/* 517 */         i = this.bbuf.remaining();
/* 518 */         this.needData = (i == 0);
/*     */       }
/* 520 */       if (this.needData) {
/* 521 */         this.bbuf.clear();
/* 522 */         WrapperResult localWrapperResult = SSLStreams.this.recvData(this.bbuf);
/* 523 */         this.bbuf = (localWrapperResult.buf == this.bbuf ? this.bbuf : localWrapperResult.buf);
/* 524 */         if ((i = this.bbuf.remaining()) == 0) {
/* 525 */           this.eof = true;
/* 526 */           return 0;
/*     */         }
/* 528 */         this.needData = false;
/*     */       }
/*     */       
/*     */ 
/* 532 */       if (paramInt2 > i) {
/* 533 */         paramInt2 = i;
/*     */       }
/* 535 */       this.bbuf.get(paramArrayOfByte, paramInt1, paramInt2);
/* 536 */       return paramInt2;
/*     */     }
/*     */     
/*     */     public int available() throws IOException {
/* 540 */       return this.bbuf.remaining();
/*     */     }
/*     */     
/*     */     public boolean markSupported() {
/* 544 */       return false;
/*     */     }
/*     */     
/*     */     public void reset() throws IOException {
/* 548 */       throw new IOException("mark/reset not supported");
/*     */     }
/*     */     
/*     */     public long skip(long paramLong) throws IOException {
/* 552 */       int i = (int)paramLong;
/* 553 */       if (this.closed) {
/* 554 */         throw new IOException("SSL stream is closed");
/*     */       }
/* 556 */       if (this.eof) {
/* 557 */         return 0L;
/*     */       }
/* 559 */       int j = i;
/* 560 */       while (i > 0) {
/* 561 */         if (this.bbuf.remaining() >= i) {
/* 562 */           this.bbuf.position(this.bbuf.position() + i);
/* 563 */           return j;
/*     */         }
/* 565 */         i -= this.bbuf.remaining();
/* 566 */         this.bbuf.clear();
/* 567 */         WrapperResult localWrapperResult = SSLStreams.this.recvData(this.bbuf);
/* 568 */         this.bbuf = (localWrapperResult.buf == this.bbuf ? this.bbuf : localWrapperResult.buf);
/*     */       }
/*     */       
/* 571 */       return j;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 580 */       this.eof = true;
/* 581 */       SSLStreams.this.engine.closeInbound();
/*     */     }
/*     */     
/*     */     public int read(byte[] paramArrayOfByte) throws IOException {
/* 585 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */     
/* 588 */     byte[] single = new byte[1];
/*     */     
/*     */     public int read() throws IOException {
/* 591 */       int i = read(this.single, 0, 1);
/* 592 */       if (i == 0) {
/* 593 */         return -1;
/*     */       }
/* 595 */       return this.single[0] & 0xFF;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   class OutputStream
/*     */     extends OutputStream
/*     */   {
/*     */     ByteBuffer buf;
/*     */     
/*     */ 
/* 607 */     boolean closed = false;
/* 608 */     byte[] single = new byte[1];
/*     */     
/*     */     OutputStream() {
/* 611 */       this.buf = SSLStreams.this.allocate(BufType.APPLICATION);
/*     */     }
/*     */     
/*     */     public void write(int paramInt) throws IOException {
/* 615 */       this.single[0] = ((byte)paramInt);
/* 616 */       write(this.single, 0, 1);
/*     */     }
/*     */     
/*     */ 
/* 620 */     public void write(byte[] paramArrayOfByte) throws IOException { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
/*     */     
/*     */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 623 */       if (this.closed) {
/* 624 */         throw new IOException("output stream is closed");
/*     */       }
/* 626 */       while (paramInt2 > 0) {
/* 627 */         int i = paramInt2 > this.buf.capacity() ? this.buf.capacity() : paramInt2;
/* 628 */         this.buf.clear();
/* 629 */         this.buf.put(paramArrayOfByte, paramInt1, i);
/* 630 */         paramInt2 -= i;
/* 631 */         paramInt1 += i;
/* 632 */         this.buf.flip();
/* 633 */         WrapperResult localWrapperResult = SSLStreams.this.sendData(this.buf);
/* 634 */         if (localWrapperResult.result.getStatus() == SSLEngineResult.Status.CLOSED) {
/* 635 */           this.closed = true;
/* 636 */           if (paramInt2 > 0) {
/* 637 */             throw new IOException("output stream is closed");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public void flush() throws IOException
/*     */     {}
/*     */     
/*     */     public void close() throws IOException
/*     */     {
/* 648 */       WrapperResult localWrapperResult = null;
/* 649 */       SSLStreams.this.engine.closeOutbound();
/* 650 */       this.closed = true;
/* 651 */       SSLEngineResult.HandshakeStatus localHandshakeStatus = SSLEngineResult.HandshakeStatus.NEED_WRAP;
/* 652 */       this.buf.clear();
/* 653 */       while (localHandshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
/* 654 */         localWrapperResult = SSLStreams.this.wrapper.wrapAndSend(this.buf);
/* 655 */         localHandshakeStatus = localWrapperResult.result.getHandshakeStatus();
/*     */       }
/* 657 */       assert (localWrapperResult.result.getStatus() == SSLEngineResult.Status.CLOSED);
/*     */     }
/*     */   }
/*     */   
/*     */   class WrapperResult
/*     */   {
/*     */     SSLEngineResult result;
/*     */     ByteBuffer buf;
/*     */     
/*     */     WrapperResult() {}
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\httpserver\SSLStreams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
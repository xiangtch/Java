/*     */ package sun.net.httpserver;
/*     */ 
/*     */ import com.sun.net.httpserver.Headers;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.BufferOverflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.SocketChannel;
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
/*     */ class Request
/*     */ {
/*     */   static final int BUF_LEN = 2048;
/*     */   static final byte CR = 13;
/*     */   static final byte LF = 10;
/*     */   private String startLine;
/*     */   private SocketChannel chan;
/*     */   private InputStream is;
/*     */   private OutputStream os;
/*     */   
/*     */   Request(InputStream paramInputStream, OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/*  47 */     this.is = paramInputStream;
/*  48 */     this.os = paramOutputStream;
/*     */     do {
/*  50 */       this.startLine = readLine();
/*  51 */       if (this.startLine == null) {
/*  52 */         return;
/*     */       }
/*     */       
/*  55 */     } while ((this.startLine != null) && (this.startLine.equals("")));
/*     */   }
/*     */   
/*     */ 
/*  59 */   char[] buf = new char['ࠀ'];
/*     */   int pos;
/*     */   StringBuffer lineBuf;
/*     */   
/*     */   public InputStream inputStream() {
/*  64 */     return this.is;
/*     */   }
/*     */   
/*     */   public OutputStream outputStream() {
/*  68 */     return this.os;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String readLine()
/*     */     throws IOException
/*     */   {
/*  77 */     int i = 0;int j = 0;
/*  78 */     this.pos = 0;this.lineBuf = new StringBuffer();
/*  79 */     while (j == 0) {
/*  80 */       int k = this.is.read();
/*  81 */       if (k == -1) {
/*  82 */         return null;
/*     */       }
/*  84 */       if (i != 0) {
/*  85 */         if (k == 10) {
/*  86 */           j = 1;
/*     */         } else {
/*  88 */           i = 0;
/*  89 */           consume(13);
/*  90 */           consume(k);
/*     */         }
/*     */       }
/*  93 */       else if (k == 13) {
/*  94 */         i = 1;
/*     */       } else {
/*  96 */         consume(k);
/*     */       }
/*     */     }
/*     */     
/* 100 */     this.lineBuf.append(this.buf, 0, this.pos);
/* 101 */     return new String(this.lineBuf);
/*     */   }
/*     */   
/*     */   private void consume(int paramInt) {
/* 105 */     if (this.pos == 2048) {
/* 106 */       this.lineBuf.append(this.buf);
/* 107 */       this.pos = 0;
/*     */     }
/* 109 */     this.buf[(this.pos++)] = ((char)paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String requestLine()
/*     */   {
/* 116 */     return this.startLine;
/*     */   }
/*     */   
/* 119 */   Headers hdrs = null;
/*     */   
/*     */   Headers headers() throws IOException {
/* 122 */     if (this.hdrs != null) {
/* 123 */       return this.hdrs;
/*     */     }
/* 125 */     this.hdrs = new Headers();
/*     */     
/* 127 */     Object localObject1 = new char[10];
/* 128 */     int i = 0;
/*     */     
/* 130 */     int j = this.is.read();
/*     */     
/*     */     int k;
/* 133 */     if ((j == 13) || (j == 10)) {
/* 134 */       k = this.is.read();
/* 135 */       if ((k == 13) || (k == 10)) {
/* 136 */         return this.hdrs;
/*     */       }
/* 138 */       localObject1[0] = ((char)j);
/* 139 */       i = 1;
/* 140 */       j = k;
/*     */     }
/*     */     
/* 143 */     while ((j != 10) && (j != 13) && (j >= 0)) {
/* 144 */       k = -1;
/*     */       
/* 146 */       int n = j > 32 ? 1 : 0;
/* 147 */       localObject1[(i++)] = ((char)j);
/*     */       int m;
/* 149 */       Object localObject2; while ((m = this.is.read()) >= 0) {
/* 150 */         switch (m)
/*     */         {
/*     */         case 58: 
/* 153 */           if ((n != 0) && (i > 0))
/* 154 */             k = i;
/* 155 */           n = 0;
/* 156 */           break;
/*     */         case 9: 
/* 158 */           m = 32;
/*     */         case 32: 
/* 160 */           n = 0;
/* 161 */           break;
/*     */         case 10: 
/*     */         case 13: 
/* 164 */           j = this.is.read();
/* 165 */           if ((m == 13) && (j == 10)) {
/* 166 */             j = this.is.read();
/* 167 */             if (j == 13)
/* 168 */               j = this.is.read();
/*     */           }
/* 170 */           if ((j == 10) || (j == 13) || (j > 32)) {
/*     */             break label328;
/*     */           }
/* 173 */           m = 32;
/*     */         }
/*     */         
/* 176 */         if (i >= localObject1.length) {
/* 177 */           localObject2 = new char[localObject1.length * 2];
/* 178 */           System.arraycopy(localObject1, 0, localObject2, 0, i);
/* 179 */           localObject1 = localObject2;
/*     */         }
/* 181 */         localObject1[(i++)] = ((char)m);
/*     */       }
/* 183 */       j = -1;
/*     */       label328:
/* 185 */       while ((i > 0) && (localObject1[(i - 1)] <= ' ')) {
/* 186 */         i--;
/*     */       }
/* 188 */       if (k <= 0) {
/* 189 */         localObject2 = null;
/* 190 */         k = 0;
/*     */       } else {
/* 192 */         localObject2 = String.copyValueOf((char[])localObject1, 0, k);
/* 193 */         if ((k < i) && (localObject1[k] == ':'))
/* 194 */           k++;
/* 195 */         while ((k < i) && (localObject1[k] <= ' '))
/* 196 */           k++;
/*     */       }
/*     */       String str;
/* 199 */       if (k >= i) {
/* 200 */         str = new String();
/*     */       } else {
/* 202 */         str = String.copyValueOf((char[])localObject1, k, i - k);
/*     */       }
/* 204 */       if (this.hdrs.size() >= ServerConfig.getMaxReqHeaders())
/*     */       {
/*     */ 
/* 207 */         throw new IOException("Maximum number of request headers (sun.net.httpserver.maxReqHeaders) exceeded, " + ServerConfig.getMaxReqHeaders() + ".");
/*     */       }
/*     */       
/* 210 */       this.hdrs.add((String)localObject2, str);
/* 211 */       i = 0;
/*     */     }
/* 213 */     return this.hdrs;
/*     */   }
/*     */   
/*     */   static class ReadStream extends InputStream {
/*     */     static final int BUFSIZE = 8192;
/*     */     ServerImpl server;
/*     */     static long readTimeout;
/*     */     int readlimit;
/*     */     boolean reset;
/*     */     boolean marked;
/*     */     ByteBuffer markBuf;
/* 224 */     private boolean eof = false; private boolean closed = false;
/*     */     
/*     */     byte[] one;
/*     */     
/*     */     ByteBuffer chanbuf;
/*     */     SocketChannel channel;
/*     */     
/*     */     public ReadStream(ServerImpl paramServerImpl, SocketChannel paramSocketChannel)
/*     */       throws IOException
/*     */     {
/* 234 */       this.channel = paramSocketChannel;
/* 235 */       this.server = paramServerImpl;
/* 236 */       this.chanbuf = ByteBuffer.allocate(8192);
/* 237 */       this.chanbuf.clear();
/* 238 */       this.one = new byte[1];
/* 239 */       this.closed = (this.marked = this.reset = 0);
/*     */     }
/*     */     
/*     */     public synchronized int read(byte[] paramArrayOfByte) throws IOException {
/* 243 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */     
/*     */     public synchronized int read() throws IOException {
/* 247 */       int i = read(this.one, 0, 1);
/* 248 */       if (i == 1) {
/* 249 */         return this.one[0] & 0xFF;
/*     */       }
/* 251 */       return -1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */       throws IOException
/*     */     {
/* 259 */       if (this.closed) {
/* 260 */         throw new IOException("Stream closed");
/*     */       }
/* 262 */       if (this.eof) {
/* 263 */         return -1;
/*     */       }
/*     */       
/* 266 */       assert (this.channel.isBlocking());
/*     */       
/* 268 */       if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
/* 269 */         throw new IndexOutOfBoundsException();
/*     */       }
/*     */       int j;
/* 272 */       if (this.reset) {
/* 273 */         int i = this.markBuf.remaining();
/* 274 */         j = i > paramInt2 ? paramInt2 : i;
/* 275 */         this.markBuf.get(paramArrayOfByte, paramInt1, j);
/* 276 */         if (i == j) {
/* 277 */           this.reset = false;
/*     */         }
/*     */       } else {
/* 280 */         this.chanbuf.clear();
/* 281 */         if (paramInt2 < 8192) {
/* 282 */           this.chanbuf.limit(paramInt2);
/*     */         }
/*     */         do {
/* 285 */           j = this.channel.read(this.chanbuf);
/* 286 */         } while (j == 0);
/* 287 */         if (j == -1) {
/* 288 */           this.eof = true;
/* 289 */           return -1;
/*     */         }
/* 291 */         this.chanbuf.flip();
/* 292 */         this.chanbuf.get(paramArrayOfByte, paramInt1, j);
/*     */         
/* 294 */         if (this.marked) {
/*     */           try {
/* 296 */             this.markBuf.put(paramArrayOfByte, paramInt1, j);
/*     */           } catch (BufferOverflowException localBufferOverflowException) {
/* 298 */             this.marked = false;
/*     */           }
/*     */         }
/*     */       }
/* 302 */       return j;
/*     */     }
/*     */     
/*     */     public boolean markSupported() {
/* 306 */       return true;
/*     */     }
/*     */     
/*     */     public synchronized int available() throws IOException
/*     */     {
/* 311 */       if (this.closed) {
/* 312 */         throw new IOException("Stream is closed");
/*     */       }
/* 314 */       if (this.eof) {
/* 315 */         return -1;
/*     */       }
/* 317 */       if (this.reset) {
/* 318 */         return this.markBuf.remaining();
/*     */       }
/* 320 */       return this.chanbuf.remaining();
/*     */     }
/*     */     
/*     */     public void close() throws IOException {
/* 324 */       if (this.closed) {
/* 325 */         return;
/*     */       }
/* 327 */       this.channel.close();
/* 328 */       this.closed = true;
/*     */     }
/*     */     
/*     */     public synchronized void mark(int paramInt) {
/* 332 */       if (this.closed)
/* 333 */         return;
/* 334 */       this.readlimit = paramInt;
/* 335 */       this.markBuf = ByteBuffer.allocate(paramInt);
/* 336 */       this.marked = true;
/* 337 */       this.reset = false;
/*     */     }
/*     */     
/*     */     public synchronized void reset() throws IOException {
/* 341 */       if (this.closed)
/* 342 */         return;
/* 343 */       if (!this.marked)
/* 344 */         throw new IOException("Stream not marked");
/* 345 */       this.marked = false;
/* 346 */       this.reset = true;
/* 347 */       this.markBuf.flip();
/*     */     }
/*     */   }
/*     */   
/*     */   static class WriteStream extends OutputStream {
/*     */     SocketChannel channel;
/*     */     ByteBuffer buf;
/*     */     SelectionKey key;
/*     */     boolean closed;
/*     */     byte[] one;
/*     */     ServerImpl server;
/*     */     
/*     */     public WriteStream(ServerImpl paramServerImpl, SocketChannel paramSocketChannel) throws IOException {
/* 360 */       this.channel = paramSocketChannel;
/* 361 */       this.server = paramServerImpl;
/* 362 */       assert (paramSocketChannel.isBlocking());
/* 363 */       this.closed = false;
/* 364 */       this.one = new byte[1];
/* 365 */       this.buf = ByteBuffer.allocate(4096);
/*     */     }
/*     */     
/*     */     public synchronized void write(int paramInt) throws IOException {
/* 369 */       this.one[0] = ((byte)paramInt);
/* 370 */       write(this.one, 0, 1);
/*     */     }
/*     */     
/*     */     public synchronized void write(byte[] paramArrayOfByte) throws IOException {
/* 374 */       write(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */     
/*     */     public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 378 */       int i = paramInt2;
/* 379 */       if (this.closed) {
/* 380 */         throw new IOException("stream is closed");
/*     */       }
/* 382 */       int j = this.buf.capacity();
/* 383 */       int k; if (j < paramInt2) {
/* 384 */         k = paramInt2 - j;
/* 385 */         this.buf = ByteBuffer.allocate(2 * (j + k));
/*     */       }
/* 387 */       this.buf.clear();
/* 388 */       this.buf.put(paramArrayOfByte, paramInt1, paramInt2);
/* 389 */       this.buf.flip();
/*     */       
/* 391 */       while ((k = this.channel.write(this.buf)) < i) {
/* 392 */         i -= k;
/* 393 */         if (i == 0) {}
/*     */       }
/*     */     }
/*     */     
/*     */     public void close() throws IOException
/*     */     {
/* 399 */       if (this.closed) {
/* 400 */         return;
/*     */       }
/* 402 */       this.channel.close();
/* 403 */       this.closed = true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\httpserver\Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
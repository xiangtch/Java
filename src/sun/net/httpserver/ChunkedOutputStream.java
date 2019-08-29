/*     */ package sun.net.httpserver;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */ class ChunkedOutputStream
/*     */   extends FilterOutputStream
/*     */ {
/*  52 */   private boolean closed = false;
/*     */   
/*     */   static final int CHUNK_SIZE = 4096;
/*     */   
/*     */   static final int OFFSET = 6;
/*  57 */   private int pos = 6;
/*  58 */   private int count = 0;
/*  59 */   private byte[] buf = new byte['ဈ'];
/*     */   ExchangeImpl t;
/*     */   
/*     */   ChunkedOutputStream(ExchangeImpl paramExchangeImpl, OutputStream paramOutputStream) {
/*  63 */     super(paramOutputStream);
/*  64 */     this.t = paramExchangeImpl;
/*     */   }
/*     */   
/*     */   public void write(int paramInt) throws IOException {
/*  68 */     if (this.closed) {
/*  69 */       throw new StreamClosedException();
/*     */     }
/*  71 */     this.buf[(this.pos++)] = ((byte)paramInt);
/*  72 */     this.count += 1;
/*  73 */     if (this.count == 4096) {
/*  74 */       writeChunk();
/*     */     }
/*  76 */     assert (this.count < 4096);
/*     */   }
/*     */   
/*     */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/*  80 */     if (this.closed) {
/*  81 */       throw new StreamClosedException();
/*     */     }
/*  83 */     int i = 4096 - this.count;
/*  84 */     if (paramInt2 > i) {
/*  85 */       System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.pos, i);
/*  86 */       this.count = 4096;
/*  87 */       writeChunk();
/*  88 */       paramInt2 -= i;
/*  89 */       paramInt1 += i;
/*  90 */       while (paramInt2 >= 4096) {
/*  91 */         System.arraycopy(paramArrayOfByte, paramInt1, this.buf, 6, 4096);
/*  92 */         paramInt2 -= 4096;
/*  93 */         paramInt1 += 4096;
/*  94 */         this.count = 4096;
/*  95 */         writeChunk();
/*     */       }
/*     */     }
/*  98 */     if (paramInt2 > 0) {
/*  99 */       System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.pos, paramInt2);
/* 100 */       this.count += paramInt2;
/* 101 */       this.pos += paramInt2;
/*     */     }
/* 103 */     if (this.count == 4096) {
/* 104 */       writeChunk();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void writeChunk()
/*     */     throws IOException
/*     */   {
/* 114 */     char[] arrayOfChar = Integer.toHexString(this.count).toCharArray();
/* 115 */     int i = arrayOfChar.length;
/* 116 */     int j = 4 - i;
/*     */     
/* 118 */     for (int k = 0; k < i; k++) {
/* 119 */       this.buf[(j + k)] = ((byte)arrayOfChar[k]);
/*     */     }
/* 121 */     this.buf[(j + k++)] = 13;
/* 122 */     this.buf[(j + k++)] = 10;
/* 123 */     this.buf[(j + k++ + this.count)] = 13;
/* 124 */     this.buf[(j + k++ + this.count)] = 10;
/* 125 */     this.out.write(this.buf, j, k + this.count);
/* 126 */     this.count = 0;
/* 127 */     this.pos = 6;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 112	sun/net/httpserver/ChunkedOutputStream:closed	Z
/*     */     //   4: ifeq +4 -> 8
/*     */     //   7: return
/*     */     //   8: aload_0
/*     */     //   9: invokevirtual 124	sun/net/httpserver/ChunkedOutputStream:flush	()V
/*     */     //   12: aload_0
/*     */     //   13: invokespecial 125	sun/net/httpserver/ChunkedOutputStream:writeChunk	()V
/*     */     //   16: aload_0
/*     */     //   17: getfield 114	sun/net/httpserver/ChunkedOutputStream:out	Ljava/io/OutputStream;
/*     */     //   20: invokevirtual 117	java/io/OutputStream:flush	()V
/*     */     //   23: aload_0
/*     */     //   24: getfield 115	sun/net/httpserver/ChunkedOutputStream:t	Lsun/net/httpserver/ExchangeImpl;
/*     */     //   27: invokevirtual 127	sun/net/httpserver/ExchangeImpl:getOriginalInputStream	()Lsun/net/httpserver/LeftOverInputStream;
/*     */     //   30: astore_1
/*     */     //   31: aload_1
/*     */     //   32: invokevirtual 130	sun/net/httpserver/LeftOverInputStream:isClosed	()Z
/*     */     //   35: ifne +7 -> 42
/*     */     //   38: aload_1
/*     */     //   39: invokevirtual 129	sun/net/httpserver/LeftOverInputStream:close	()V
/*     */     //   42: aload_0
/*     */     //   43: iconst_1
/*     */     //   44: putfield 112	sun/net/httpserver/ChunkedOutputStream:closed	Z
/*     */     //   47: goto +20 -> 67
/*     */     //   50: astore_1
/*     */     //   51: aload_0
/*     */     //   52: iconst_1
/*     */     //   53: putfield 112	sun/net/httpserver/ChunkedOutputStream:closed	Z
/*     */     //   56: goto +11 -> 67
/*     */     //   59: astore_2
/*     */     //   60: aload_0
/*     */     //   61: iconst_1
/*     */     //   62: putfield 112	sun/net/httpserver/ChunkedOutputStream:closed	Z
/*     */     //   65: aload_2
/*     */     //   66: athrow
/*     */     //   67: new 75	sun/net/httpserver/WriteFinishedEvent
/*     */     //   70: dup
/*     */     //   71: aload_0
/*     */     //   72: getfield 115	sun/net/httpserver/ChunkedOutputStream:t	Lsun/net/httpserver/ExchangeImpl;
/*     */     //   75: invokespecial 133	sun/net/httpserver/WriteFinishedEvent:<init>	(Lsun/net/httpserver/ExchangeImpl;)V
/*     */     //   78: astore_1
/*     */     //   79: aload_0
/*     */     //   80: getfield 115	sun/net/httpserver/ChunkedOutputStream:t	Lsun/net/httpserver/ExchangeImpl;
/*     */     //   83: invokevirtual 126	sun/net/httpserver/ExchangeImpl:getHttpContext	()Lsun/net/httpserver/HttpContextImpl;
/*     */     //   86: invokevirtual 128	sun/net/httpserver/HttpContextImpl:getServerImpl	()Lsun/net/httpserver/ServerImpl;
/*     */     //   89: aload_1
/*     */     //   90: invokevirtual 131	sun/net/httpserver/ServerImpl:addEvent	(Lsun/net/httpserver/Event;)V
/*     */     //   93: return
/*     */     // Line number table:
/*     */     //   Java source line #131	-> byte code offset #0
/*     */     //   Java source line #132	-> byte code offset #7
/*     */     //   Java source line #134	-> byte code offset #8
/*     */     //   Java source line #137	-> byte code offset #12
/*     */     //   Java source line #138	-> byte code offset #16
/*     */     //   Java source line #139	-> byte code offset #23
/*     */     //   Java source line #140	-> byte code offset #31
/*     */     //   Java source line #141	-> byte code offset #38
/*     */     //   Java source line #147	-> byte code offset #42
/*     */     //   Java source line #148	-> byte code offset #47
/*     */     //   Java source line #144	-> byte code offset #50
/*     */     //   Java source line #147	-> byte code offset #51
/*     */     //   Java source line #148	-> byte code offset #56
/*     */     //   Java source line #147	-> byte code offset #59
/*     */     //   Java source line #148	-> byte code offset #65
/*     */     //   Java source line #150	-> byte code offset #67
/*     */     //   Java source line #151	-> byte code offset #79
/*     */     //   Java source line #152	-> byte code offset #93
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	94	0	this	ChunkedOutputStream
/*     */     //   30	9	1	localLeftOverInputStream	LeftOverInputStream
/*     */     //   50	1	1	localIOException	IOException
/*     */     //   78	12	1	localWriteFinishedEvent	WriteFinishedEvent
/*     */     //   59	7	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   12	42	50	java/io/IOException
/*     */     //   12	42	59	finally
/*     */   }
/*     */   
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/* 155 */     if (this.closed) {
/* 156 */       throw new StreamClosedException();
/*     */     }
/* 158 */     if (this.count > 0) {
/* 159 */       writeChunk();
/*     */     }
/* 161 */     this.out.flush();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\httpserver\ChunkedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.net.www;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import sun.net.ProgressSource;
/*     */ import sun.net.www.http.ChunkedInputStream;
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
/*     */ public class MeteredStream
/*     */   extends FilterInputStream
/*     */ {
/*  41 */   protected boolean closed = false;
/*     */   protected long expected;
/*  43 */   protected long count = 0L;
/*  44 */   protected long markedCount = 0L;
/*  45 */   protected int markLimit = -1;
/*     */   protected ProgressSource pi;
/*     */   
/*     */   public MeteredStream(InputStream paramInputStream, ProgressSource paramProgressSource, long paramLong)
/*     */   {
/*  50 */     super(paramInputStream);
/*     */     
/*  52 */     this.pi = paramProgressSource;
/*  53 */     this.expected = paramLong;
/*     */     
/*  55 */     if (paramProgressSource != null) {
/*  56 */       paramProgressSource.updateProgress(0L, paramLong);
/*     */     }
/*     */   }
/*     */   
/*     */   private final void justRead(long paramLong) throws IOException {
/*  61 */     if (paramLong == -1L)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  67 */       if (!isMarked()) {
/*  68 */         close();
/*     */       }
/*  70 */       return;
/*     */     }
/*     */     
/*  73 */     this.count += paramLong;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  78 */     if (this.count - this.markedCount > this.markLimit) {
/*  79 */       this.markLimit = -1;
/*     */     }
/*     */     
/*  82 */     if (this.pi != null) {
/*  83 */       this.pi.updateProgress(this.count, this.expected);
/*     */     }
/*  85 */     if (isMarked()) {
/*  86 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  91 */     if ((this.expected > 0L) && 
/*  92 */       (this.count >= this.expected)) {
/*  93 */       close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean isMarked()
/*     */   {
/* 103 */     if (this.markLimit < 0) {
/* 104 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 108 */     if (this.count - this.markedCount > this.markLimit) {
/* 109 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 113 */     return true;
/*     */   }
/*     */   
/*     */   public synchronized int read() throws IOException {
/* 117 */     if (this.closed) {
/* 118 */       return -1;
/*     */     }
/* 120 */     int i = this.in.read();
/* 121 */     if (i != -1) {
/* 122 */       justRead(1L);
/*     */     } else {
/* 124 */       justRead(i);
/*     */     }
/* 126 */     return i;
/*     */   }
/*     */   
/*     */   public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */   {
/* 131 */     if (this.closed) {
/* 132 */       return -1;
/*     */     }
/* 134 */     int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
/* 135 */     justRead(i);
/* 136 */     return i;
/*     */   }
/*     */   
/*     */   public synchronized long skip(long paramLong)
/*     */     throws IOException
/*     */   {
/* 142 */     if (this.closed) {
/* 143 */       return 0L;
/*     */     }
/*     */     
/* 146 */     if ((this.in instanceof ChunkedInputStream)) {
/* 147 */       paramLong = this.in.skip(paramLong);
/*     */     }
/*     */     else
/*     */     {
/* 151 */       long l = paramLong > this.expected - this.count ? this.expected - this.count : paramLong;
/* 152 */       paramLong = this.in.skip(l);
/*     */     }
/* 154 */     justRead(paramLong);
/* 155 */     return paramLong;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 159 */     if (this.closed) {
/* 160 */       return;
/*     */     }
/* 162 */     if (this.pi != null) {
/* 163 */       this.pi.finishTracking();
/*     */     }
/* 165 */     this.closed = true;
/* 166 */     this.in.close();
/*     */   }
/*     */   
/*     */   public synchronized int available() throws IOException {
/* 170 */     return this.closed ? 0 : this.in.available();
/*     */   }
/*     */   
/*     */   public synchronized void mark(int paramInt) {
/* 174 */     if (this.closed) {
/* 175 */       return;
/*     */     }
/* 177 */     super.mark(paramInt);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 182 */     this.markedCount = this.count;
/* 183 */     this.markLimit = paramInt;
/*     */   }
/*     */   
/*     */   public synchronized void reset() throws IOException {
/* 187 */     if (this.closed) {
/* 188 */       return;
/*     */     }
/*     */     
/* 191 */     if (!isMarked()) {
/* 192 */       throw new IOException("Resetting to an invalid mark");
/*     */     }
/*     */     
/* 195 */     this.count = this.markedCount;
/* 196 */     super.reset();
/*     */   }
/*     */   
/*     */   public boolean markSupported() {
/* 200 */     if (this.closed) {
/* 201 */       return false;
/*     */     }
/* 203 */     return super.markSupported();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 105	sun/net/www/MeteredStream:close	()V
/*     */     //   4: aload_0
/*     */     //   5: getfield 90	sun/net/www/MeteredStream:pi	Lsun/net/ProgressSource;
/*     */     //   8: ifnull +10 -> 18
/*     */     //   11: aload_0
/*     */     //   12: getfield 90	sun/net/www/MeteredStream:pi	Lsun/net/ProgressSource;
/*     */     //   15: invokevirtual 102	sun/net/ProgressSource:close	()V
/*     */     //   18: aload_0
/*     */     //   19: invokespecial 101	java/lang/Object:finalize	()V
/*     */     //   22: goto +10 -> 32
/*     */     //   25: astore_1
/*     */     //   26: aload_0
/*     */     //   27: invokespecial 101	java/lang/Object:finalize	()V
/*     */     //   30: aload_1
/*     */     //   31: athrow
/*     */     //   32: return
/*     */     // Line number table:
/*     */     //   Java source line #208	-> byte code offset #0
/*     */     //   Java source line #209	-> byte code offset #4
/*     */     //   Java source line #210	-> byte code offset #11
/*     */     //   Java source line #214	-> byte code offset #18
/*     */     //   Java source line #215	-> byte code offset #22
/*     */     //   Java source line #214	-> byte code offset #25
/*     */     //   Java source line #215	-> byte code offset #30
/*     */     //   Java source line #216	-> byte code offset #32
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	33	0	this	MeteredStream
/*     */     //   25	6	1	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	18	25	finally
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\MeteredStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.net.www.http;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.net.ProgressSource;
/*     */ import sun.net.www.MeteredStream;
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
/*     */ public class KeepAliveStream
/*     */   extends MeteredStream
/*     */   implements Hurryable
/*     */ {
/*     */   HttpClient hc;
/*     */   boolean hurried;
/*  48 */   protected boolean queuedForCleanup = false;
/*     */   
/*  50 */   private static final KeepAliveStreamCleaner queue = new KeepAliveStreamCleaner();
/*     */   
/*     */   private static Thread cleanerThread;
/*     */   
/*     */ 
/*     */   public KeepAliveStream(InputStream paramInputStream, ProgressSource paramProgressSource, long paramLong, HttpClient paramHttpClient)
/*     */   {
/*  57 */     super(paramInputStream, paramProgressSource, paramLong);
/*  58 */     this.hc = paramHttpClient;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 143	sun/net/www/http/KeepAliveStream:closed	Z
/*     */     //   4: ifeq +4 -> 8
/*     */     //   7: return
/*     */     //   8: aload_0
/*     */     //   9: getfield 145	sun/net/www/http/KeepAliveStream:queuedForCleanup	Z
/*     */     //   12: ifeq +4 -> 16
/*     */     //   15: return
/*     */     //   16: aload_0
/*     */     //   17: getfield 142	sun/net/www/http/KeepAliveStream:expected	J
/*     */     //   20: aload_0
/*     */     //   21: getfield 141	sun/net/www/http/KeepAliveStream:count	J
/*     */     //   24: lcmp
/*     */     //   25: ifle +104 -> 129
/*     */     //   28: aload_0
/*     */     //   29: getfield 142	sun/net/www/http/KeepAliveStream:expected	J
/*     */     //   32: aload_0
/*     */     //   33: getfield 141	sun/net/www/http/KeepAliveStream:count	J
/*     */     //   36: lsub
/*     */     //   37: lstore_1
/*     */     //   38: lload_1
/*     */     //   39: aload_0
/*     */     //   40: invokevirtual 169	sun/net/www/http/KeepAliveStream:available	()I
/*     */     //   43: i2l
/*     */     //   44: lcmp
/*     */     //   45: ifgt +40 -> 85
/*     */     //   48: aload_0
/*     */     //   49: getfield 142	sun/net/www/http/KeepAliveStream:expected	J
/*     */     //   52: aload_0
/*     */     //   53: getfield 141	sun/net/www/http/KeepAliveStream:count	J
/*     */     //   56: lsub
/*     */     //   57: dup2
/*     */     //   58: lstore_1
/*     */     //   59: lconst_0
/*     */     //   60: lcmp
/*     */     //   61: ifle +68 -> 129
/*     */     //   64: aload_0
/*     */     //   65: lload_1
/*     */     //   66: aload_0
/*     */     //   67: invokevirtual 169	sun/net/www/http/KeepAliveStream:available	()I
/*     */     //   70: i2l
/*     */     //   71: invokestatic 157	java/lang/Math:min	(JJ)J
/*     */     //   74: invokevirtual 170	sun/net/www/http/KeepAliveStream:skip	(J)J
/*     */     //   77: lconst_0
/*     */     //   78: lcmp
/*     */     //   79: ifgt -31 -> 48
/*     */     //   82: goto +47 -> 129
/*     */     //   85: aload_0
/*     */     //   86: getfield 142	sun/net/www/http/KeepAliveStream:expected	J
/*     */     //   89: getstatic 151	sun/net/www/http/KeepAliveStreamCleaner:MAX_DATA_REMAINING	I
/*     */     //   92: i2l
/*     */     //   93: lcmp
/*     */     //   94: ifgt +28 -> 122
/*     */     //   97: aload_0
/*     */     //   98: getfield 144	sun/net/www/http/KeepAliveStream:hurried	Z
/*     */     //   101: ifne +21 -> 122
/*     */     //   104: new 89	sun/net/www/http/KeepAliveCleanerEntry
/*     */     //   107: dup
/*     */     //   108: aload_0
/*     */     //   109: aload_0
/*     */     //   110: getfield 149	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
/*     */     //   113: invokespecial 168	sun/net/www/http/KeepAliveCleanerEntry:<init>	(Lsun/net/www/http/KeepAliveStream;Lsun/net/www/http/HttpClient;)V
/*     */     //   116: invokestatic 171	sun/net/www/http/KeepAliveStream:queueForCleanup	(Lsun/net/www/http/KeepAliveCleanerEntry;)V
/*     */     //   119: goto +10 -> 129
/*     */     //   122: aload_0
/*     */     //   123: getfield 149	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
/*     */     //   126: invokevirtual 163	sun/net/www/http/HttpClient:closeServer	()V
/*     */     //   129: aload_0
/*     */     //   130: getfield 143	sun/net/www/http/KeepAliveStream:closed	Z
/*     */     //   133: ifne +24 -> 157
/*     */     //   136: aload_0
/*     */     //   137: getfield 144	sun/net/www/http/KeepAliveStream:hurried	Z
/*     */     //   140: ifne +17 -> 157
/*     */     //   143: aload_0
/*     */     //   144: getfield 145	sun/net/www/http/KeepAliveStream:queuedForCleanup	Z
/*     */     //   147: ifne +10 -> 157
/*     */     //   150: aload_0
/*     */     //   151: getfield 149	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
/*     */     //   154: invokevirtual 164	sun/net/www/http/HttpClient:finished	()V
/*     */     //   157: aload_0
/*     */     //   158: getfield 148	sun/net/www/http/KeepAliveStream:pi	Lsun/net/ProgressSource;
/*     */     //   161: ifnull +10 -> 171
/*     */     //   164: aload_0
/*     */     //   165: getfield 148	sun/net/www/http/KeepAliveStream:pi	Lsun/net/ProgressSource;
/*     */     //   168: invokevirtual 161	sun/net/ProgressSource:finishTracking	()V
/*     */     //   171: aload_0
/*     */     //   172: getfield 145	sun/net/www/http/KeepAliveStream:queuedForCleanup	Z
/*     */     //   175: ifne +60 -> 235
/*     */     //   178: aload_0
/*     */     //   179: aconst_null
/*     */     //   180: putfield 146	sun/net/www/http/KeepAliveStream:in	Ljava/io/InputStream;
/*     */     //   183: aload_0
/*     */     //   184: aconst_null
/*     */     //   185: putfield 149	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
/*     */     //   188: aload_0
/*     */     //   189: iconst_1
/*     */     //   190: putfield 143	sun/net/www/http/KeepAliveStream:closed	Z
/*     */     //   193: goto +42 -> 235
/*     */     //   196: astore_3
/*     */     //   197: aload_0
/*     */     //   198: getfield 148	sun/net/www/http/KeepAliveStream:pi	Lsun/net/ProgressSource;
/*     */     //   201: ifnull +10 -> 211
/*     */     //   204: aload_0
/*     */     //   205: getfield 148	sun/net/www/http/KeepAliveStream:pi	Lsun/net/ProgressSource;
/*     */     //   208: invokevirtual 161	sun/net/ProgressSource:finishTracking	()V
/*     */     //   211: aload_0
/*     */     //   212: getfield 145	sun/net/www/http/KeepAliveStream:queuedForCleanup	Z
/*     */     //   215: ifne +18 -> 233
/*     */     //   218: aload_0
/*     */     //   219: aconst_null
/*     */     //   220: putfield 146	sun/net/www/http/KeepAliveStream:in	Ljava/io/InputStream;
/*     */     //   223: aload_0
/*     */     //   224: aconst_null
/*     */     //   225: putfield 149	sun/net/www/http/KeepAliveStream:hc	Lsun/net/www/http/HttpClient;
/*     */     //   228: aload_0
/*     */     //   229: iconst_1
/*     */     //   230: putfield 143	sun/net/www/http/KeepAliveStream:closed	Z
/*     */     //   233: aload_3
/*     */     //   234: athrow
/*     */     //   235: return
/*     */     // Line number table:
/*     */     //   Java source line #66	-> byte code offset #0
/*     */     //   Java source line #67	-> byte code offset #7
/*     */     //   Java source line #71	-> byte code offset #8
/*     */     //   Java source line #72	-> byte code offset #15
/*     */     //   Java source line #83	-> byte code offset #16
/*     */     //   Java source line #84	-> byte code offset #28
/*     */     //   Java source line #85	-> byte code offset #38
/*     */     //   Java source line #86	-> byte code offset #48
/*     */     //   Java source line #87	-> byte code offset #67
/*     */     //   Java source line #88	-> byte code offset #85
/*     */     //   Java source line #91	-> byte code offset #104
/*     */     //   Java source line #93	-> byte code offset #122
/*     */     //   Java source line #96	-> byte code offset #129
/*     */     //   Java source line #97	-> byte code offset #150
/*     */     //   Java source line #100	-> byte code offset #157
/*     */     //   Java source line #101	-> byte code offset #164
/*     */     //   Java source line #103	-> byte code offset #171
/*     */     //   Java source line #106	-> byte code offset #178
/*     */     //   Java source line #107	-> byte code offset #183
/*     */     //   Java source line #108	-> byte code offset #188
/*     */     //   Java source line #100	-> byte code offset #196
/*     */     //   Java source line #101	-> byte code offset #204
/*     */     //   Java source line #103	-> byte code offset #211
/*     */     //   Java source line #106	-> byte code offset #218
/*     */     //   Java source line #107	-> byte code offset #223
/*     */     //   Java source line #108	-> byte code offset #228
/*     */     //   Java source line #110	-> byte code offset #233
/*     */     //   Java source line #111	-> byte code offset #235
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	236	0	this	KeepAliveStream
/*     */     //   37	29	1	l	long
/*     */     //   196	38	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   16	157	196	finally
/*     */   }
/*     */   
/*     */   public boolean markSupported()
/*     */   {
/* 116 */     return false;
/*     */   }
/*     */   
/*     */   public void mark(int paramInt) {}
/*     */   
/*     */   public void reset() throws IOException {
/* 122 */     throw new IOException("mark/reset not supported");
/*     */   }
/*     */   
/*     */   public synchronized boolean hurry()
/*     */   {
/*     */     try {
/* 128 */       if ((this.closed) || (this.count >= this.expected))
/* 129 */         return false;
/* 130 */       if (this.in.available() < this.expected - this.count)
/*     */       {
/* 132 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 137 */       int i = (int)(this.expected - this.count);
/* 138 */       byte[] arrayOfByte = new byte[i];
/* 139 */       DataInputStream localDataInputStream = new DataInputStream(this.in);
/* 140 */       localDataInputStream.readFully(arrayOfByte);
/* 141 */       this.in = new ByteArrayInputStream(arrayOfByte);
/* 142 */       this.hurried = true;
/* 143 */       return true;
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */     
/* 147 */     return false;
/*     */   }
/*     */   
/*     */   private static void queueForCleanup(KeepAliveCleanerEntry paramKeepAliveCleanerEntry)
/*     */   {
/* 152 */     synchronized (queue) {
/* 153 */       if (!paramKeepAliveCleanerEntry.getQueuedForCleanup()) {
/* 154 */         if (!queue.offer(paramKeepAliveCleanerEntry)) {
/* 155 */           paramKeepAliveCleanerEntry.getHttpClient().closeServer();
/* 156 */           return;
/*     */         }
/*     */         
/* 159 */         paramKeepAliveCleanerEntry.setQueuedForCleanup();
/* 160 */         queue.notifyAll();
/*     */       }
/*     */       
/* 163 */       int i = cleanerThread == null ? 1 : 0;
/* 164 */       if ((i == 0) && 
/* 165 */         (!cleanerThread.isAlive())) {
/* 166 */         i = 1;
/*     */       }
/*     */       
/*     */ 
/* 170 */       if (i != 0) {
/* 171 */         AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */ 
/*     */           public Void run()
/*     */           {
/* 176 */             Object localObject = Thread.currentThread().getThreadGroup();
/* 177 */             ThreadGroup localThreadGroup = null;
/* 178 */             while ((localThreadGroup = ((ThreadGroup)localObject).getParent()) != null) {
/* 179 */               localObject = localThreadGroup;
/*     */             }
/*     */             
/* 182 */             KeepAliveStream.access$002(new Thread((ThreadGroup)localObject, KeepAliveStream.queue, "Keep-Alive-SocketCleaner"));
/* 183 */             KeepAliveStream.cleanerThread.setDaemon(true);
/* 184 */             KeepAliveStream.cleanerThread.setPriority(8);
/*     */             
/*     */ 
/* 187 */             KeepAliveStream.cleanerThread.setContextClassLoader(null);
/* 188 */             KeepAliveStream.cleanerThread.start();
/* 189 */             return null;
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected long remainingToRead() {
/* 197 */     return this.expected - this.count;
/*     */   }
/*     */   
/*     */   protected void setClosed() {
/* 201 */     this.in = null;
/* 202 */     this.hc = null;
/* 203 */     this.closed = true;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\http\KeepAliveStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
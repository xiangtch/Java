/*     */ package sun.rmi.runtime;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.rmi.server.LogStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.logging.Handler;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogRecord;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.logging.SimpleFormatter;
/*     */ import java.util.logging.StreamHandler;
/*     */ import sun.security.action.GetPropertyAction;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Log
/*     */ {
/*  68 */   public static final Level BRIEF = Level.FINE;
/*  69 */   public static final Level VERBOSE = Level.FINER;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  77 */     boolean bool = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.log.useOld"))).booleanValue();
/*     */   }
/*     */   
/*  80 */   private static final LogFactory logFactory = bool ? new LogStreamLogFactory() : new LoggerLogFactory();
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
/*     */   public static Log getLog(String paramString1, String paramString2, int paramInt)
/*     */   {
/*     */     Level localLevel;
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
/* 125 */     if (paramInt < 0) {
/* 126 */       localLevel = null;
/* 127 */     } else if (paramInt == 0) {
/* 128 */       localLevel = Level.OFF;
/* 129 */     } else if ((paramInt > 0) && (paramInt <= 10))
/*     */     {
/* 131 */       localLevel = BRIEF;
/* 132 */     } else if ((paramInt > 10) && (paramInt <= 20))
/*     */     {
/*     */ 
/* 135 */       localLevel = VERBOSE;
/*     */     } else {
/* 137 */       localLevel = Level.FINEST;
/*     */     }
/* 139 */     return logFactory.createLog(paramString1, paramString2, localLevel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Log getLog(String paramString1, String paramString2, boolean paramBoolean)
/*     */   {
/* 152 */     Level localLevel = paramBoolean ? VERBOSE : null;
/* 153 */     return logFactory.createLog(paramString1, paramString2, localLevel);
/*     */   }
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
/*     */   private static class LoggerLogFactory
/*     */     implements LogFactory
/*     */   {
/*     */     public Log createLog(String paramString1, String paramString2, Level paramLevel)
/*     */     {
/* 172 */       Logger localLogger = Logger.getLogger(paramString1);
/* 173 */       return new LoggerLog(localLogger, paramLevel, null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class LoggerLog
/*     */     extends Log
/*     */   {
/* 184 */     private static final Handler alternateConsole = (Handler)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Handler run()
/*     */       {
/* 187 */         InternalStreamHandler localInternalStreamHandler = new InternalStreamHandler(System.err);
/*     */         
/* 189 */         localInternalStreamHandler.setLevel(Level.ALL);
/* 190 */         return localInternalStreamHandler;
/*     */       }
/* 184 */     });
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
/* 195 */     private InternalStreamHandler copyHandler = null;
/*     */     
/*     */ 
/*     */     private final Logger logger;
/*     */     
/*     */     private LoggerPrintStream loggerSandwich;
/*     */     
/*     */ 
/*     */     private LoggerLog(final Logger paramLogger, final Level paramLevel)
/*     */     {
/* 205 */       this.logger = paramLogger;
/*     */       
/* 207 */       if (paramLevel != null) {
/* 208 */         AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Void run() {
/* 211 */             if (!paramLogger.isLoggable(paramLevel)) {
/* 212 */               paramLogger.setLevel(paramLevel);
/*     */             }
/* 214 */             paramLogger.addHandler(LoggerLog.alternateConsole);
/* 215 */             return null;
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean isLoggable(Level paramLevel)
/*     */     {
/* 223 */       return this.logger.isLoggable(paramLevel);
/*     */     }
/*     */     
/*     */     public void log(Level paramLevel, String paramString) {
/* 227 */       if (isLoggable(paramLevel)) {
/* 228 */         String[] arrayOfString = Log.access$200();
/* 229 */         this.logger.logp(paramLevel, arrayOfString[0], arrayOfString[1], 
/* 230 */           Thread.currentThread().getName() + ": " + paramString);
/*     */       }
/*     */     }
/*     */     
/*     */     public void log(Level paramLevel, String paramString, Throwable paramThrowable) {
/* 235 */       if (isLoggable(paramLevel)) {
/* 236 */         String[] arrayOfString = Log.access$200();
/* 237 */         this.logger.logp(paramLevel, arrayOfString[0], arrayOfString[1], 
/* 238 */           Thread.currentThread().getName() + ": " + paramString, paramThrowable);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public synchronized void setOutputStream(OutputStream paramOutputStream)
/*     */     {
/* 250 */       if (paramOutputStream != null) {
/* 251 */         if (!this.logger.isLoggable(VERBOSE)) {
/* 252 */           this.logger.setLevel(VERBOSE);
/*     */         }
/* 254 */         this.copyHandler = new InternalStreamHandler(paramOutputStream);
/* 255 */         this.copyHandler.setLevel(Log.VERBOSE);
/* 256 */         this.logger.addHandler(this.copyHandler);
/*     */       }
/*     */       else {
/* 259 */         if (this.copyHandler != null) {
/* 260 */           this.logger.removeHandler(this.copyHandler);
/*     */         }
/* 262 */         this.copyHandler = null;
/*     */       }
/*     */     }
/*     */     
/*     */     public synchronized PrintStream getPrintStream() {
/* 267 */       if (this.loggerSandwich == null) {
/* 268 */         this.loggerSandwich = new LoggerPrintStream(this.logger, null);
/*     */       }
/* 270 */       return this.loggerSandwich;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class InternalStreamHandler
/*     */     extends StreamHandler
/*     */   {
/*     */     InternalStreamHandler(OutputStream paramOutputStream)
/*     */     {
/* 280 */       super(new SimpleFormatter());
/*     */     }
/*     */     
/*     */     public void publish(LogRecord paramLogRecord) {
/* 284 */       super.publish(paramLogRecord);
/* 285 */       flush();
/*     */     }
/*     */     
/*     */     public void close() {
/* 289 */       flush();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class LoggerPrintStream
/*     */     extends PrintStream
/*     */   {
/*     */     private final Logger logger;
/*     */     
/*     */ 
/*     */ 
/* 304 */     private int last = -1;
/*     */     
/*     */     private final ByteArrayOutputStream bufOut;
/*     */     
/*     */ 
/*     */     private LoggerPrintStream(Logger paramLogger)
/*     */     {
/* 311 */       super();
/* 312 */       this.bufOut = ((ByteArrayOutputStream)this.out);
/* 313 */       this.logger = paramLogger;
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void write(int paramInt)
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 82	sun/rmi/runtime/Log$LoggerPrintStream:last	I
/*     */       //   4: bipush 13
/*     */       //   6: if_icmpne +15 -> 21
/*     */       //   9: iload_1
/*     */       //   10: bipush 10
/*     */       //   12: if_icmpne +9 -> 21
/*     */       //   15: aload_0
/*     */       //   16: iconst_m1
/*     */       //   17: putfield 82	sun/rmi/runtime/Log$LoggerPrintStream:last	I
/*     */       //   20: return
/*     */       //   21: iload_1
/*     */       //   22: bipush 10
/*     */       //   24: if_icmpeq +9 -> 33
/*     */       //   27: iload_1
/*     */       //   28: bipush 13
/*     */       //   30: if_icmpne +76 -> 106
/*     */       //   33: new 46	java/lang/StringBuilder
/*     */       //   36: dup
/*     */       //   37: invokespecial 91	java/lang/StringBuilder:<init>	()V
/*     */       //   40: invokestatic 95	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */       //   43: invokevirtual 94	java/lang/Thread:getName	()Ljava/lang/String;
/*     */       //   46: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   49: ldc 1
/*     */       //   51: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   54: aload_0
/*     */       //   55: getfield 83	sun/rmi/runtime/Log$LoggerPrintStream:bufOut	Ljava/io/ByteArrayOutputStream;
/*     */       //   58: invokevirtual 87	java/io/ByteArrayOutputStream:toString	()Ljava/lang/String;
/*     */       //   61: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   64: invokevirtual 92	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */       //   67: astore_2
/*     */       //   68: aload_0
/*     */       //   69: getfield 84	sun/rmi/runtime/Log$LoggerPrintStream:logger	Ljava/util/logging/Logger;
/*     */       //   72: getstatic 81	java/util/logging/Level:INFO	Ljava/util/logging/Level;
/*     */       //   75: ldc 2
/*     */       //   77: ldc 4
/*     */       //   79: aload_2
/*     */       //   80: invokevirtual 96	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
/*     */       //   83: aload_0
/*     */       //   84: getfield 83	sun/rmi/runtime/Log$LoggerPrintStream:bufOut	Ljava/io/ByteArrayOutputStream;
/*     */       //   87: invokevirtual 86	java/io/ByteArrayOutputStream:reset	()V
/*     */       //   90: goto +13 -> 103
/*     */       //   93: astore_3
/*     */       //   94: aload_0
/*     */       //   95: getfield 83	sun/rmi/runtime/Log$LoggerPrintStream:bufOut	Ljava/io/ByteArrayOutputStream;
/*     */       //   98: invokevirtual 86	java/io/ByteArrayOutputStream:reset	()V
/*     */       //   101: aload_3
/*     */       //   102: athrow
/*     */       //   103: goto +8 -> 111
/*     */       //   106: aload_0
/*     */       //   107: iload_1
/*     */       //   108: invokespecial 88	java/io/PrintStream:write	(I)V
/*     */       //   111: aload_0
/*     */       //   112: iload_1
/*     */       //   113: putfield 82	sun/rmi/runtime/Log$LoggerPrintStream:last	I
/*     */       //   116: return
/*     */       // Line number table:
/*     */       //   Java source line #317	-> byte code offset #0
/*     */       //   Java source line #318	-> byte code offset #15
/*     */       //   Java source line #319	-> byte code offset #20
/*     */       //   Java source line #320	-> byte code offset #21
/*     */       //   Java source line #323	-> byte code offset #33
/*     */       //   Java source line #324	-> byte code offset #40
/*     */       //   Java source line #325	-> byte code offset #58
/*     */       //   Java source line #326	-> byte code offset #68
/*     */       //   Java source line #328	-> byte code offset #83
/*     */       //   Java source line #329	-> byte code offset #90
/*     */       //   Java source line #328	-> byte code offset #93
/*     */       //   Java source line #329	-> byte code offset #101
/*     */       //   Java source line #331	-> byte code offset #106
/*     */       //   Java source line #333	-> byte code offset #111
/*     */       //   Java source line #334	-> byte code offset #116
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	117	0	this	LoggerPrintStream
/*     */       //   0	117	1	paramInt	int
/*     */       //   67	13	2	str	String
/*     */       //   93	9	3	localObject	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   33	83	93	finally
/*     */     }
/*     */     
/*     */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     {
/* 337 */       if (paramInt2 < 0) {
/* 338 */         throw new ArrayIndexOutOfBoundsException(paramInt2);
/*     */       }
/* 340 */       for (int i = 0; i < paramInt2; i++) {
/* 341 */         write(paramArrayOfByte[(paramInt1 + i)]);
/*     */       }
/*     */     }
/*     */     
/*     */     public String toString() {
/* 346 */       return "RMI";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class LogStreamLogFactory
/*     */     implements LogFactory
/*     */   {
/*     */     public Log createLog(String paramString1, String paramString2, Level paramLevel)
/*     */     {
/* 361 */       LogStream localLogStream = null;
/* 362 */       if (paramString2 != null) {
/* 363 */         localLogStream = LogStream.log(paramString2);
/*     */       }
/* 365 */       return new LogStreamLog(localLogStream, paramLevel, null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class LogStreamLog
/*     */     extends Log
/*     */   {
/*     */     private final LogStream stream;
/*     */     
/*     */ 
/* 378 */     private int levelValue = Level.OFF.intValue();
/*     */     
/*     */     private LogStreamLog(LogStream paramLogStream, Level paramLevel) {
/* 381 */       if ((paramLogStream != null) && (paramLevel != null))
/*     */       {
/*     */ 
/*     */ 
/* 385 */         this.levelValue = paramLevel.intValue();
/*     */       }
/* 387 */       this.stream = paramLogStream;
/*     */     }
/*     */     
/*     */     public synchronized boolean isLoggable(Level paramLevel) {
/* 391 */       return paramLevel.intValue() >= this.levelValue;
/*     */     }
/*     */     
/*     */     public void log(Level paramLevel, String paramString) {
/* 395 */       if (isLoggable(paramLevel)) {
/* 396 */         String[] arrayOfString = Log.access$200();
/* 397 */         this.stream.println(unqualifiedName(arrayOfString[0]) + "." + arrayOfString[1] + ": " + paramString);
/*     */       }
/*     */     }
/*     */     
/*     */     public void log(Level paramLevel, String paramString, Throwable paramThrowable)
/*     */     {
/* 403 */       if (isLoggable(paramLevel))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 408 */         synchronized (this.stream) {
/* 409 */           String[] arrayOfString = Log.access$200();
/* 410 */           this.stream.println(unqualifiedName(arrayOfString[0]) + "." + arrayOfString[1] + ": " + paramString);
/*     */           
/* 412 */           paramThrowable.printStackTrace(this.stream);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public PrintStream getPrintStream() {
/* 418 */       return this.stream;
/*     */     }
/*     */     
/*     */     public synchronized void setOutputStream(OutputStream paramOutputStream) {
/* 422 */       if (paramOutputStream != null) {
/* 423 */         if (VERBOSE.intValue() < this.levelValue) {
/* 424 */           this.levelValue = VERBOSE.intValue();
/*     */         }
/* 426 */         this.stream.setOutputStream(paramOutputStream);
/*     */       }
/*     */       else {
/* 429 */         this.levelValue = Level.OFF.intValue();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static String unqualifiedName(String paramString)
/*     */     {
/* 437 */       int i = paramString.lastIndexOf(".");
/* 438 */       if (i >= 0) {
/* 439 */         paramString = paramString.substring(i + 1);
/*     */       }
/* 441 */       paramString = paramString.replace('$', '.');
/* 442 */       return paramString;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String[] getSource()
/*     */   {
/* 450 */     StackTraceElement[] arrayOfStackTraceElement = new Exception().getStackTrace();
/* 451 */     return new String[] {arrayOfStackTraceElement[3]
/* 452 */       .getClassName(), arrayOfStackTraceElement[3]
/* 453 */       .getMethodName() };
/*     */   }
/*     */   
/*     */   public abstract boolean isLoggable(Level paramLevel);
/*     */   
/*     */   public abstract void log(Level paramLevel, String paramString);
/*     */   
/*     */   public abstract void log(Level paramLevel, String paramString, Throwable paramThrowable);
/*     */   
/*     */   public abstract void setOutputStream(OutputStream paramOutputStream);
/*     */   
/*     */   public abstract PrintStream getPrintStream();
/*     */   
/*     */   private static abstract interface LogFactory
/*     */   {
/*     */     public abstract Log createLog(String paramString1, String paramString2, Level paramLevel);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\runtime\Log.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
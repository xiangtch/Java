/*     */ package sun.util.logging;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import sun.misc.JavaLangAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlatformLogger
/*     */ {
/*     */   private static final int OFF = Integer.MAX_VALUE;
/*     */   private static final int SEVERE = 1000;
/*     */   private static final int WARNING = 900;
/*     */   private static final int INFO = 800;
/*     */   private static final int CONFIG = 700;
/*     */   private static final int FINE = 500;
/*     */   private static final int FINER = 400;
/*     */   private static final int FINEST = 300;
/*     */   private static final int ALL = Integer.MIN_VALUE;
/*     */   
/*     */   public static enum Level
/*     */   {
/* 107 */     ALL, 
/* 108 */     FINEST, 
/* 109 */     FINER, 
/* 110 */     FINE, 
/* 111 */     CONFIG, 
/* 112 */     INFO, 
/* 113 */     WARNING, 
/* 114 */     SEVERE, 
/* 115 */     OFF;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     Object javaLevel;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 126 */     private static final int[] LEVEL_VALUES = { Integer.MIN_VALUE, 300, 400, 500, 700, 800, 900, 1000, Integer.MAX_VALUE };
/*     */     
/*     */ 
/*     */     private Level() {}
/*     */     
/*     */     public int intValue()
/*     */     {
/* 133 */       return LEVEL_VALUES[ordinal()];
/*     */     }
/*     */     
/*     */     static Level valueOf(int paramInt) {
/* 137 */       switch (paramInt)
/*     */       {
/*     */       case 300: 
/* 140 */         return FINEST;
/* 141 */       case 500:  return FINE;
/* 142 */       case 400:  return FINER;
/* 143 */       case 800:  return INFO;
/* 144 */       case 900:  return WARNING;
/* 145 */       case 700:  return CONFIG;
/* 146 */       case 1000:  return SEVERE;
/* 147 */       case 2147483647:  return OFF;
/* 148 */       case -2147483648:  return ALL;
/*     */       }
/*     */       
/*     */       
/* 152 */       int i = Arrays.binarySearch(LEVEL_VALUES, 0, LEVEL_VALUES.length - 2, paramInt);
/* 153 */       return values()[(-i - 1)];
/*     */     }
/*     */   }
/*     */   
/* 157 */   private static final Level DEFAULT_LEVEL = Level.INFO;
/*     */   
/*     */ 
/* 160 */   private static boolean loggingEnabled = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Boolean run() {
/* 163 */       String str1 = System.getProperty("java.util.logging.config.class");
/* 164 */       String str2 = System.getProperty("java.util.logging.config.file");
/* 165 */       return Boolean.valueOf((str1 != null) || (str2 != null));
/*     */     }
/* 160 */   })).booleanValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 173 */       Class.forName("sun.util.logging.PlatformLogger$DefaultLoggerProxy", false, PlatformLogger.class
/*     */       
/* 175 */         .getClassLoader());
/* 176 */       Class.forName("sun.util.logging.PlatformLogger$JavaLoggerProxy", false, PlatformLogger.class
/*     */       
/* 178 */         .getClassLoader());
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 180 */       throw new InternalError(localClassNotFoundException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 185 */   private static Map<String, WeakReference<PlatformLogger>> loggers = new HashMap();
/*     */   
/*     */   private volatile LoggerProxy loggerProxy;
/*     */   private volatile JavaLoggerProxy javaLoggerProxy;
/*     */   
/*     */   public static synchronized PlatformLogger getLogger(String paramString)
/*     */   {
/* 192 */     PlatformLogger localPlatformLogger = null;
/* 193 */     WeakReference localWeakReference = (WeakReference)loggers.get(paramString);
/* 194 */     if (localWeakReference != null) {
/* 195 */       localPlatformLogger = (PlatformLogger)localWeakReference.get();
/*     */     }
/* 197 */     if (localPlatformLogger == null) {
/* 198 */       localPlatformLogger = new PlatformLogger(paramString);
/* 199 */       loggers.put(paramString, new WeakReference(localPlatformLogger));
/*     */     }
/* 201 */     return localPlatformLogger;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void redirectPlatformLoggers()
/*     */   {
/* 209 */     if ((loggingEnabled) || (!LoggingSupport.isAvailable())) { return;
/*     */     }
/* 211 */     loggingEnabled = true;
/* 212 */     for (Entry localEntry : loggers.entrySet()) {
/* 213 */       WeakReference localWeakReference = (WeakReference)localEntry.getValue();
/* 214 */       PlatformLogger localPlatformLogger = (PlatformLogger)localWeakReference.get();
/* 215 */       if (localPlatformLogger != null) {
/* 216 */         localPlatformLogger.redirectToJavaLoggerProxy();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void redirectToJavaLoggerProxy()
/*     */   {
/* 225 */     DefaultLoggerProxy localDefaultLoggerProxy = (DefaultLoggerProxy)DefaultLoggerProxy.class.cast(this.loggerProxy);
/* 226 */     JavaLoggerProxy localJavaLoggerProxy = new JavaLoggerProxy(localDefaultLoggerProxy.name, localDefaultLoggerProxy.level);
/*     */     
/* 228 */     this.javaLoggerProxy = localJavaLoggerProxy;
/* 229 */     this.loggerProxy = localJavaLoggerProxy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private PlatformLogger(String paramString)
/*     */   {
/* 238 */     if (loggingEnabled) {
/* 239 */       this.loggerProxy = (this.javaLoggerProxy = new JavaLoggerProxy(paramString));
/*     */     } else {
/* 241 */       this.loggerProxy = new DefaultLoggerProxy(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 250 */     return this.loggerProxy.isEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 257 */     return this.loggerProxy.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLoggable(Level paramLevel)
/*     */   {
/* 265 */     if (paramLevel == null) {
/* 266 */       throw new NullPointerException();
/*     */     }
/*     */     
/* 269 */     JavaLoggerProxy localJavaLoggerProxy = this.javaLoggerProxy;
/* 270 */     return localJavaLoggerProxy != null ? localJavaLoggerProxy.isLoggable(paramLevel) : this.loggerProxy.isLoggable(paramLevel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Level level()
/*     */   {
/* 281 */     return this.loggerProxy.getLevel();
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
/*     */   public void setLevel(Level paramLevel)
/*     */   {
/* 297 */     this.loggerProxy.setLevel(paramLevel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void severe(String paramString)
/*     */   {
/* 304 */     this.loggerProxy.doLog(Level.SEVERE, paramString);
/*     */   }
/*     */   
/*     */   public void severe(String paramString, Throwable paramThrowable) {
/* 308 */     this.loggerProxy.doLog(Level.SEVERE, paramString, paramThrowable);
/*     */   }
/*     */   
/*     */   public void severe(String paramString, Object... paramVarArgs) {
/* 312 */     this.loggerProxy.doLog(Level.SEVERE, paramString, paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void warning(String paramString)
/*     */   {
/* 319 */     this.loggerProxy.doLog(Level.WARNING, paramString);
/*     */   }
/*     */   
/*     */   public void warning(String paramString, Throwable paramThrowable) {
/* 323 */     this.loggerProxy.doLog(Level.WARNING, paramString, paramThrowable);
/*     */   }
/*     */   
/*     */   public void warning(String paramString, Object... paramVarArgs) {
/* 327 */     this.loggerProxy.doLog(Level.WARNING, paramString, paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void info(String paramString)
/*     */   {
/* 334 */     this.loggerProxy.doLog(Level.INFO, paramString);
/*     */   }
/*     */   
/*     */   public void info(String paramString, Throwable paramThrowable) {
/* 338 */     this.loggerProxy.doLog(Level.INFO, paramString, paramThrowable);
/*     */   }
/*     */   
/*     */   public void info(String paramString, Object... paramVarArgs) {
/* 342 */     this.loggerProxy.doLog(Level.INFO, paramString, paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void config(String paramString)
/*     */   {
/* 349 */     this.loggerProxy.doLog(Level.CONFIG, paramString);
/*     */   }
/*     */   
/*     */   public void config(String paramString, Throwable paramThrowable) {
/* 353 */     this.loggerProxy.doLog(Level.CONFIG, paramString, paramThrowable);
/*     */   }
/*     */   
/*     */   public void config(String paramString, Object... paramVarArgs) {
/* 357 */     this.loggerProxy.doLog(Level.CONFIG, paramString, paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fine(String paramString)
/*     */   {
/* 364 */     this.loggerProxy.doLog(Level.FINE, paramString);
/*     */   }
/*     */   
/*     */   public void fine(String paramString, Throwable paramThrowable) {
/* 368 */     this.loggerProxy.doLog(Level.FINE, paramString, paramThrowable);
/*     */   }
/*     */   
/*     */   public void fine(String paramString, Object... paramVarArgs) {
/* 372 */     this.loggerProxy.doLog(Level.FINE, paramString, paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void finer(String paramString)
/*     */   {
/* 379 */     this.loggerProxy.doLog(Level.FINER, paramString);
/*     */   }
/*     */   
/*     */   public void finer(String paramString, Throwable paramThrowable) {
/* 383 */     this.loggerProxy.doLog(Level.FINER, paramString, paramThrowable);
/*     */   }
/*     */   
/*     */   public void finer(String paramString, Object... paramVarArgs) {
/* 387 */     this.loggerProxy.doLog(Level.FINER, paramString, paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void finest(String paramString)
/*     */   {
/* 394 */     this.loggerProxy.doLog(Level.FINEST, paramString);
/*     */   }
/*     */   
/*     */   public void finest(String paramString, Throwable paramThrowable) {
/* 398 */     this.loggerProxy.doLog(Level.FINEST, paramString, paramThrowable);
/*     */   }
/*     */   
/*     */   public void finest(String paramString, Object... paramVarArgs) {
/* 402 */     this.loggerProxy.doLog(Level.FINEST, paramString, paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */   private static abstract class LoggerProxy
/*     */   {
/*     */     final String name;
/*     */     
/*     */     protected LoggerProxy(String paramString)
/*     */     {
/* 412 */       this.name = paramString;
/*     */     }
/*     */     
/*     */     abstract boolean isEnabled();
/*     */     
/*     */     abstract Level getLevel();
/*     */     
/*     */     abstract void setLevel(Level paramLevel);
/*     */     
/*     */     abstract void doLog(Level paramLevel, String paramString);
/*     */     
/*     */     abstract void doLog(Level paramLevel, String paramString, Throwable paramThrowable);
/*     */     
/*     */     abstract void doLog(Level paramLevel, String paramString, Object... paramVarArgs);
/*     */     
/*     */     abstract boolean isLoggable(Level paramLevel);
/*     */   }
/*     */   
/*     */   private static final class DefaultLoggerProxy extends LoggerProxy {
/*     */     volatile Level effectiveLevel;
/*     */     volatile Level level;
/*     */     
/* 434 */     private static PrintStream outputStream() { return System.err; }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     DefaultLoggerProxy(String paramString)
/*     */     {
/* 441 */       super();
/* 442 */       this.effectiveLevel = deriveEffectiveLevel(null);
/* 443 */       this.level = null;
/*     */     }
/*     */     
/*     */     boolean isEnabled() {
/* 447 */       return this.effectiveLevel != Level.OFF;
/*     */     }
/*     */     
/*     */     Level getLevel() {
/* 451 */       return this.level;
/*     */     }
/*     */     
/*     */     void setLevel(Level paramLevel) {
/* 455 */       Level localLevel = this.level;
/* 456 */       if (localLevel != paramLevel) {
/* 457 */         this.level = paramLevel;
/* 458 */         this.effectiveLevel = deriveEffectiveLevel(paramLevel);
/*     */       }
/*     */     }
/*     */     
/*     */     void doLog(Level paramLevel, String paramString) {
/* 463 */       if (isLoggable(paramLevel)) {
/* 464 */         outputStream().print(format(paramLevel, paramString, null));
/*     */       }
/*     */     }
/*     */     
/*     */     void doLog(Level paramLevel, String paramString, Throwable paramThrowable) {
/* 469 */       if (isLoggable(paramLevel)) {
/* 470 */         outputStream().print(format(paramLevel, paramString, paramThrowable));
/*     */       }
/*     */     }
/*     */     
/*     */     void doLog(Level paramLevel, String paramString, Object... paramVarArgs) {
/* 475 */       if (isLoggable(paramLevel)) {
/* 476 */         String str = formatMessage(paramString, paramVarArgs);
/* 477 */         outputStream().print(format(paramLevel, str, null));
/*     */       }
/*     */     }
/*     */     
/*     */     boolean isLoggable(Level paramLevel) {
/* 482 */       Level localLevel = this.effectiveLevel;
/* 483 */       return (paramLevel.intValue() >= localLevel.intValue()) && (localLevel != Level.OFF);
/*     */     }
/*     */     
/*     */     private Level deriveEffectiveLevel(Level paramLevel)
/*     */     {
/* 488 */       return paramLevel == null ? PlatformLogger.DEFAULT_LEVEL : paramLevel;
/*     */     }
/*     */     
/*     */     private String formatMessage(String paramString, Object... paramVarArgs)
/*     */     {
/*     */       try
/*     */       {
/* 495 */         if ((paramVarArgs == null) || (paramVarArgs.length == 0))
/*     */         {
/* 497 */           return paramString;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 504 */         if ((paramString.indexOf("{0") >= 0) || (paramString.indexOf("{1") >= 0) || 
/* 505 */           (paramString.indexOf("{2") >= 0) || (paramString.indexOf("{3") >= 0)) {
/* 506 */           return MessageFormat.format(paramString, paramVarArgs);
/*     */         }
/* 508 */         return paramString;
/*     */       }
/*     */       catch (Exception localException) {}
/* 511 */       return paramString;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 516 */     private static final String formatString = LoggingSupport.getSimpleFormat(false);
/*     */     
/*     */ 
/* 519 */     private Date date = new Date();
/*     */     
/* 521 */     private synchronized String format(Level paramLevel, String paramString, Throwable paramThrowable) { this.date.setTime(System.currentTimeMillis());
/* 522 */       String str = "";
/* 523 */       if (paramThrowable != null) {
/* 524 */         StringWriter localStringWriter = new StringWriter();
/* 525 */         PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
/* 526 */         localPrintWriter.println();
/* 527 */         paramThrowable.printStackTrace(localPrintWriter);
/* 528 */         localPrintWriter.close();
/* 529 */         str = localStringWriter.toString();
/*     */       }
/*     */       
/* 532 */       return String.format(formatString, new Object[] { this.date, 
/*     */       
/* 534 */         getCallerInfo(), this.name, paramLevel
/*     */         
/* 536 */         .name(), paramString, str });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private String getCallerInfo()
/*     */     {
/* 544 */       Object localObject = null;
/* 545 */       String str1 = null;
/*     */       
/* 547 */       JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
/* 548 */       Throwable localThrowable = new Throwable();
/* 549 */       int i = localJavaLangAccess.getStackTraceDepth(localThrowable);
/*     */       
/* 551 */       String str2 = "sun.util.logging.PlatformLogger";
/* 552 */       int j = 1;
/* 553 */       for (int k = 0; k < i; k++)
/*     */       {
/*     */ 
/*     */ 
/* 557 */         StackTraceElement localStackTraceElement = localJavaLangAccess.getStackTraceElement(localThrowable, k);
/* 558 */         String str3 = localStackTraceElement.getClassName();
/* 559 */         if (j != 0)
/*     */         {
/* 561 */           if (str3.equals(str2)) {
/* 562 */             j = 0;
/*     */           }
/*     */         }
/* 565 */         else if (!str3.equals(str2))
/*     */         {
/* 567 */           localObject = str3;
/* 568 */           str1 = localStackTraceElement.getMethodName();
/* 569 */           break;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 574 */       if (localObject != null) {
/* 575 */         return (String)localObject + " " + str1;
/*     */       }
/* 577 */       return this.name;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class JavaLoggerProxy
/*     */     extends LoggerProxy
/*     */   {
/*     */     private final Object javaLogger;
/*     */     
/*     */     static
/*     */     {
/* 589 */       for (Level localLevel : ) {
/* 590 */         localLevel.javaLevel = LoggingSupport.parseLevel(localLevel.name());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     JavaLoggerProxy(String paramString)
/*     */     {
/* 597 */       this(paramString, null);
/*     */     }
/*     */     
/*     */     JavaLoggerProxy(String paramString, Level paramLevel) {
/* 601 */       super();
/* 602 */       this.javaLogger = LoggingSupport.getLogger(paramString);
/* 603 */       if (paramLevel != null)
/*     */       {
/* 605 */         LoggingSupport.setLevel(this.javaLogger, paramLevel.javaLevel);
/*     */       }
/*     */     }
/*     */     
/*     */     void doLog(Level paramLevel, String paramString) {
/* 610 */       LoggingSupport.log(this.javaLogger, paramLevel.javaLevel, paramString);
/*     */     }
/*     */     
/*     */     void doLog(Level paramLevel, String paramString, Throwable paramThrowable) {
/* 614 */       LoggingSupport.log(this.javaLogger, paramLevel.javaLevel, paramString, paramThrowable);
/*     */     }
/*     */     
/*     */     void doLog(Level paramLevel, String paramString, Object... paramVarArgs) {
/* 618 */       if (!isLoggable(paramLevel)) {
/* 619 */         return;
/*     */       }
/*     */       
/*     */ 
/* 623 */       int i = paramVarArgs != null ? paramVarArgs.length : 0;
/* 624 */       String[] arrayOfString = new String[i];
/* 625 */       for (int j = 0; j < i; j++) {
/* 626 */         arrayOfString[j] = String.valueOf(paramVarArgs[j]);
/*     */       }
/* 628 */       LoggingSupport.log(this.javaLogger, paramLevel.javaLevel, paramString, arrayOfString);
/*     */     }
/*     */     
/*     */     boolean isEnabled() {
/* 632 */       return LoggingSupport.isLoggable(this.javaLogger, Level.OFF.javaLevel);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     Level getLevel()
/*     */     {
/* 641 */       Object localObject = LoggingSupport.getLevel(this.javaLogger);
/* 642 */       if (localObject == null) return null;
/*     */       try
/*     */       {
/* 645 */         return Level.valueOf(LoggingSupport.getLevelName(localObject));
/*     */       } catch (IllegalArgumentException localIllegalArgumentException) {}
/* 647 */       return Level.valueOf(LoggingSupport.getLevelValue(localObject));
/*     */     }
/*     */     
/*     */     void setLevel(Level paramLevel)
/*     */     {
/* 652 */       LoggingSupport.setLevel(this.javaLogger, paramLevel == null ? null : paramLevel.javaLevel);
/*     */     }
/*     */     
/*     */     boolean isLoggable(Level paramLevel) {
/* 656 */       return LoggingSupport.isLoggable(this.javaLogger, paramLevel.javaLevel);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\logging\PlatformLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
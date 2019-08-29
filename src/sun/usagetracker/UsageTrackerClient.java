/*     */ package sun.usagetracker;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import jdk.internal.util.EnvUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class UsageTrackerClient
/*     */ {
/*  98 */   private static final Object LOCK = new Object();
/*     */   
/*     */   private static final String ORCL_UT_CONFIG_FILE_NAME = "usagetracker.properties";
/*     */   
/*     */   private static final String ORCL_UT_USAGE_DIR = ".oracle_jre_usage";
/*     */   
/*     */   private static final String ORCL_UT_PROPERTY_NAME = "com.oracle.usagetracker.";
/*     */   
/*     */   private static final String ORCL_UT_PROPERTY_RUN_SYNCHRONOUSLY = "com.oracle.usagetracker.run.synchronous";
/*     */   
/*     */   private static final String ORCL_UT_PROPERTY_CONFIG_FILE_NAME = "com.oracle.usagetracker.config.file";
/*     */   
/*     */   private static final String ORCL_UT_LOGTOFILE = "com.oracle.usagetracker.logToFile";
/*     */   
/*     */   private static final String ORCL_UT_LOGFILEMAXSIZE = "com.oracle.usagetracker.logFileMaxSize";
/*     */   
/*     */   private static final String ORCL_UT_LOGTOUDP = "com.oracle.usagetracker.logToUDP";
/*     */   
/*     */   private static final String ORCL_UT_RECORD_MAXSIZE = "com.oracle.usagetracker.maxSize";
/*     */   
/*     */   private static final String ORCL_UT_RECORD_MAXFIELDSIZE = "com.oracle.usagetracker.maxFieldSize";
/*     */   private static final String ORCL_UT_SEND_TRUNCATED = "com.oracle.usagetracker.sendTruncatedRecords";
/*     */   private static final String ORCL_UT_TRACK_LAST_USAGE = "com.oracle.usagetracker.track.last.usage";
/*     */   private static final String ORCL_UT_VERBOSE = "com.oracle.usagetracker.verbose";
/*     */   private static final String ORCL_UT_DEBUG = "com.oracle.usagetracker.debug";
/*     */   private static final String ORCL_UT_ADDITIONALPROPERTIES = "com.oracle.usagetracker.additionalProperties";
/*     */   private static final String ORCL_UT_SEPARATOR = "com.oracle.usagetracker.separator";
/*     */   private static final String ORCL_UT_QUOTE = "com.oracle.usagetracker.quote";
/*     */   private static final String ORCL_UT_QUOTE_INNER = "com.oracle.usagetracker.innerQuote";
/*     */   private static final String DISABLE_LAST_USAGE_PROP_NAME = "jdk.disableLastUsageTracking";
/*     */   private static final String DEFAULT_SEP = ",";
/*     */   private static final String DEFAULT_QUOTE = "\"";
/*     */   private static final String DEFAULT_QUOTE_INNER = "'";
/* 131 */   private static final AtomicBoolean isFirstRun = new AtomicBoolean(true);
/*     */   
/* 133 */   private static final String javaHome = getPropertyPrivileged("java.home");
/*     */   
/*     */   private static final String userHomeKeyword = "${user.home}";
/*     */   
/*     */   private static String separator;
/*     */   private static String quote;
/*     */   private static String innerQuote;
/*     */   private static boolean enabled;
/*     */   private static boolean verbose;
/*     */   private static boolean debug;
/* 143 */   private static boolean trackTime = initTrackTime();
/*     */   
/*     */   private static String[] additionalProperties;
/*     */   
/*     */   private static String fullLogFilename;
/*     */   
/*     */   private static long logFileMaxSize;
/*     */   private static int maxSize;
/*     */   private static int maxFieldSize;
/*     */   private static boolean sendTruncated;
/*     */   private static String datagramHost;
/*     */   private static int datagramPort;
/*     */   private static String staticMessage;
/*     */   private static boolean staticMessageIsTruncated;
/*     */   
/*     */   private static String getPropertyPrivileged(String paramString)
/*     */   {
/* 160 */     return getPropertyPrivileged(paramString, null);
/*     */   }
/*     */   
/*     */   private static String getPropertyPrivileged(String paramString1, final String paramString2) {
/* 164 */     (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public String run() {
/* 166 */         return System.getProperty(this.val$property, paramString2);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static String getEnvPrivileged(String paramString) {
/* 172 */     (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public String run() {
/* 174 */         return EnvUtils.getEnvVar(this.val$envName);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static boolean initTrackTime() {
/* 180 */     String str = getPropertyPrivileged("jdk.disableLastUsageTracking");
/* 181 */     if (str == null) {
/* 182 */       return true;
/*     */     }
/* 184 */     return (!str.isEmpty()) && (!str.equalsIgnoreCase("true"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static File getConfigFilePrivileged()
/*     */   {
/* 196 */     File localFile = null;
/* 197 */     String[] arrayOfString1 = new String[3];
/* 198 */     arrayOfString1[0] = getPropertyPrivileged("com.oracle.usagetracker.config.file");
/* 199 */     arrayOfString1[1] = getOSSpecificConfigFilePath();
/* 200 */     arrayOfString1[2] = (javaHome + File.separator + "lib" + File.separator + "management" + File.separator + "usagetracker.properties");
/*     */     
/*     */ 
/* 203 */     for (String str : arrayOfString1) {
/* 204 */       if (str != null) {
/* 205 */         localFile = (File)AccessController.doPrivileged(new PrivilegedAction() {
/*     */           public File run() {
/* 207 */             File localFile = new File(this.val$path);
/* 208 */             return localFile.exists() ? localFile : null;
/*     */           }
/*     */         });
/* 211 */         if (localFile != null) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 217 */     return localFile;
/*     */   }
/*     */   
/*     */   private static String getOSSpecificConfigFilePath() {
/* 221 */     String str1 = getPropertyPrivileged("os.name");
/* 222 */     if (str1 != null) {
/* 223 */       if (str1.toLowerCase().startsWith("sunos"))
/* 224 */         return "/etc/oracle/java/usagetracker.properties";
/* 225 */       if (str1.toLowerCase().startsWith("mac"))
/* 226 */         return "/Library/Application Support/Oracle/Java/usagetracker.properties";
/* 227 */       if (str1.toLowerCase().startsWith("win")) {
/* 228 */         String str2 = getEnvPrivileged("ProgramData");
/* 229 */         return str2 + "\\Oracle\\Java\\" + "usagetracker.properties";
/*     */       }
/* 231 */       if (str1.toLowerCase().startsWith("linux")) {
/* 232 */         return "/etc/oracle/java/usagetracker.properties";
/*     */       }
/*     */     }
/* 235 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private String getFullLogFilename(Properties paramProperties)
/*     */   {
/* 241 */     String str = paramProperties.getProperty("com.oracle.usagetracker.logToFile", "");
/* 242 */     if (str.isEmpty()) {
/* 243 */       return null;
/*     */     }
/* 245 */     if (str.startsWith("${user.home}")) {
/* 246 */       if (str.length() > "${user.home}".length()) {
/* 247 */         str = getPropertyPrivileged("user.home") + str.substring("${user.home}".length());
/*     */       } else {
/* 249 */         printVerbose("UsageTracker: blank filename after user.home.");
/* 250 */         return null;
/*     */       }
/*     */     }
/* 253 */     else if (!new File(str).isAbsolute()) {
/* 254 */       printVerbose("UsageTracker: relative path disallowed.");
/* 255 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 259 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long getPropertyValueLong(Properties paramProperties, String paramString)
/*     */   {
/* 267 */     String str = paramProperties.getProperty(paramString, "");
/* 268 */     if (!str.isEmpty()) {
/*     */       try {
/* 270 */         return Long.parseLong(str);
/*     */       } catch (NumberFormatException localNumberFormatException) {
/* 272 */         printVerbose("UsageTracker: bad value: " + paramString);
/*     */       }
/*     */     }
/* 275 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean getPropertyValueBoolean(Properties paramProperties, String paramString, boolean paramBoolean)
/*     */   {
/* 283 */     String str = paramProperties.getProperty(paramString, "");
/* 284 */     if (!str.isEmpty()) {
/* 285 */       return Boolean.parseBoolean(str);
/*     */     }
/* 287 */     return paramBoolean;
/*     */   }
/*     */   
/*     */ 
/*     */   private String[] getAdditionalProperties(Properties paramProperties)
/*     */   {
/* 293 */     String str = paramProperties.getProperty("com.oracle.usagetracker.additionalProperties", "");
/* 294 */     return str.isEmpty() ? new String[0] : str.split(",");
/*     */   }
/*     */   
/*     */   private String parseDatagramHost(String paramString) {
/* 298 */     if (paramString != null) {
/* 299 */       int i = paramString.indexOf(':');
/* 300 */       if ((i > 0) && (i < paramString.length() - 1)) {
/* 301 */         return paramString.substring(0, i);
/*     */       }
/* 303 */       printVerbose("UsageTracker: bad UDP details.");
/*     */     }
/*     */     
/* 306 */     return null;
/*     */   }
/*     */   
/*     */   private int parseDatagramPort(String paramString) {
/* 310 */     if (paramString != null) {
/* 311 */       int i = paramString.indexOf(':');
/*     */       try {
/* 313 */         return Integer.parseInt(paramString.substring(i + 1));
/*     */       } catch (Exception localException) {
/* 315 */         printVerbose("UsageTracker: bad UDP port.");
/*     */       }
/*     */     }
/* 318 */     return 0;
/*     */   }
/*     */   
/*     */   private void printVerbose(String paramString) {
/* 322 */     if (verbose) {
/* 323 */       System.err.println(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */   private void printDebug(String paramString) {
/* 328 */     if (debug) {
/* 329 */       System.err.println(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */   private void printDebugStackTrace(Throwable paramThrowable) {
/* 334 */     if (debug) {
/* 335 */       paramThrowable.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setupAndTimestamp(long paramLong)
/*     */   {
/* 346 */     if (isFirstRun.compareAndSet(true, false)) {
/* 347 */       File localFile = getConfigFilePrivileged();
/* 348 */       if (localFile != null) {
/* 349 */         setup(localFile);
/*     */       }
/* 351 */       if (trackTime) {
/* 352 */         registerUsage(paramLong);
/*     */       }
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
/*     */ 
/*     */   public void run(final String paramString1, final String paramString2)
/*     */   {
/* 367 */     printDebug("UsageTracker.run: " + paramString1 + ", javaCommand: " + paramString2);
/*     */     try {
/* 369 */       AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public Void run() {
/* 371 */           long l = System.currentTimeMillis();
/* 372 */           boolean bool = Boolean.parseBoolean(System.getProperty("com.oracle.usagetracker.run.synchronous", "true"));
/* 373 */           if (bool) {
/* 374 */             UsageTrackerClient.this.setupAndTimestamp(l);
/* 375 */             UsageTrackerClient.this.printVerbose("UsageTracker: running synchronous.");
/*     */           }
/* 377 */           if ((UsageTrackerClient.enabled) || (!bool)) {
/* 378 */             UsageTrackerRunnable localUsageTrackerRunnable = new UsageTrackerRunnable(UsageTrackerClient.this, paramString1, paramString2, l, !bool);
/*     */             
/*     */ 
/* 381 */             ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
/* 382 */             while (localThreadGroup.getParent() != null) {
/* 383 */               localThreadGroup = localThreadGroup.getParent();
/*     */             }
/* 385 */             Thread localThread = new Thread(localThreadGroup, localUsageTrackerRunnable, "UsageTracker");
/* 386 */             localThread.setDaemon(true);
/* 387 */             localThread.start();
/*     */           }
/* 389 */           return null;
/*     */         }
/*     */       });
/*     */     } catch (Throwable localThrowable) {
/* 393 */       printVerbose("UsageTracker: error in starting thread.");
/* 394 */       printDebugStackTrace(localThrowable);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setup(File paramFile) {
/* 399 */     Properties localProperties = new Properties();
/* 400 */     if (paramFile != null) {
/* 401 */       try { FileInputStream localFileInputStream = new FileInputStream(paramFile);Object localObject1 = null;
/* 402 */         try { BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);Object localObject2 = null;
/* 403 */           try { localProperties.load(localBufferedInputStream);
/*     */           }
/*     */           catch (Throwable localThrowable4)
/*     */           {
/* 401 */             localObject2 = localThrowable4;throw localThrowable4; } finally {} } catch (Throwable localThrowable2) { localObject1 = localThrowable2;throw localThrowable2;
/*     */         }
/*     */         finally {
/* 404 */           if (localFileInputStream != null) if (localObject1 != null) try { localFileInputStream.close(); } catch (Throwable localThrowable6) { ((Throwable)localObject1).addSuppressed(localThrowable6); } else localFileInputStream.close();
/*     */         }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 409 */         localProperties.clear();
/*     */       }
/*     */     }
/* 412 */     verbose = getPropertyValueBoolean(localProperties, "com.oracle.usagetracker.verbose", false);
/* 413 */     debug = getPropertyValueBoolean(localProperties, "com.oracle.usagetracker.debug", false);
/* 414 */     separator = localProperties.getProperty("com.oracle.usagetracker.separator", ",");
/* 415 */     quote = localProperties.getProperty("com.oracle.usagetracker.quote", "\"");
/* 416 */     innerQuote = localProperties.getProperty("com.oracle.usagetracker.innerQuote", "'");
/*     */     
/* 418 */     fullLogFilename = getFullLogFilename(localProperties);
/* 419 */     logFileMaxSize = getPropertyValueLong(localProperties, "com.oracle.usagetracker.logFileMaxSize");
/* 420 */     maxSize = (int)getPropertyValueLong(localProperties, "com.oracle.usagetracker.maxSize");
/* 421 */     maxFieldSize = (int)getPropertyValueLong(localProperties, "com.oracle.usagetracker.maxFieldSize");
/* 422 */     sendTruncated = getPropertyValueBoolean(localProperties, "com.oracle.usagetracker.sendTruncatedRecords", true);
/* 423 */     additionalProperties = getAdditionalProperties(localProperties);
/*     */     
/* 425 */     String str = localProperties.getProperty("com.oracle.usagetracker.logToUDP");
/* 426 */     datagramHost = parseDatagramHost(str);
/* 427 */     datagramPort = parseDatagramPort(str);
/*     */     
/* 429 */     enabled = ((fullLogFilename != null) || ((datagramHost != null) && (datagramPort > 0)) ? 1 : 0) == 1;
/*     */     
/*     */ 
/* 432 */     if (trackTime) {
/* 433 */       trackTime = getPropertyValueBoolean(localProperties, "com.oracle.usagetracker.track.last.usage", true);
/*     */     }
/*     */   }
/*     */   
/*     */   private void registerUsage(long paramLong)
/*     */   {
/*     */     try {
/* 440 */       String str1 = new File(System.getProperty("java.home")).getCanonicalPath();
/* 441 */       String str2 = getPropertyPrivileged("os.name");
/* 442 */       File localFile = null;
/*     */       Object localObject1;
/*     */       Object localObject2;
/*     */       Object localObject3;
/* 446 */       if (str2.toLowerCase().startsWith("win")) {
/* 447 */         localObject1 = getEnvPrivileged("ProgramData");
/*     */         
/* 449 */         if (localObject1 != null)
/*     */         {
/* 451 */           localFile = new File((String)localObject1 + File.separator + "Oracle" + File.separator + "Java" + File.separator + ".oracle_jre_usage", getPathHash(str1) + ".timestamp");
/*     */           
/*     */ 
/*     */ 
/* 455 */           if (!localFile.exists()) {
/* 456 */             if (!localFile.getParentFile().exists()) {
/* 457 */               if (localFile.getParentFile().mkdirs())
/*     */               {
/* 459 */                 localObject2 = getEnvPrivileged("SYSTEMROOT");
/* 460 */                 localObject3 = new File((String)localObject2 + File.separator + "system32" + File.separator + "icacls.exe");
/* 461 */                 if (((File)localObject3).exists()) {
/* 462 */                   Runtime.getRuntime().exec(localObject3 + " " + localFile.getParentFile() + " /grant \"everyone\":(OI)(CI)M");
/*     */                 }
/*     */               }
/*     */               else {
/* 466 */                 localFile = null;
/*     */               }
/*     */             }
/* 469 */             if (localFile != null) {
/* 470 */               localFile.createNewFile();
/*     */             }
/*     */           }
/*     */         }
/*     */       } else {
/* 475 */         localObject1 = System.getProperty("user.home");
/*     */         
/* 477 */         if (localObject1 != null)
/*     */         {
/* 479 */           localFile = new File((String)localObject1 + File.separator + ".oracle_jre_usage", getPathHash(str1) + ".timestamp");
/*     */           
/*     */ 
/* 482 */           if (!localFile.exists()) {
/* 483 */             if (!localFile.getParentFile().exists()) {
/* 484 */               localFile.getParentFile().mkdirs();
/*     */             }
/* 486 */             localFile.createNewFile();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 491 */       if (localFile != null) {
/* 492 */         try { localObject1 = new FileOutputStream(localFile);localObject2 = null;
/*     */           
/*     */           try
/*     */           {
/* 496 */             localObject3 = str1 + System.lineSeparator() + paramLong + System.lineSeparator();
/* 497 */             ((FileOutputStream)localObject1).write(((String)localObject3).getBytes("UTF-8"));
/*     */           }
/*     */           catch (Throwable localThrowable2)
/*     */           {
/* 492 */             localObject2 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */           }
/*     */           finally
/*     */           {
/*     */ 
/* 498 */             if (localObject1 != null) if (localObject2 != null) try { ((FileOutputStream)localObject1).close(); } catch (Throwable localThrowable3) { ((Throwable)localObject2).addSuppressed(localThrowable3); } else ((FileOutputStream)localObject1).close();
/* 499 */           } } catch (IOException localIOException2) { printDebugStackTrace(localIOException2);
/*     */         }
/*     */       }
/*     */     } catch (IOException localIOException1) {
/* 503 */       printDebugStackTrace(localIOException1);
/*     */     }
/*     */   }
/*     */   
/*     */   private String getPathHash(String paramString) {
/* 508 */     long l = 0L;
/* 509 */     for (int i = 0; i < paramString.length(); i++) {
/* 510 */       l = 31L * l + paramString.charAt(i);
/*     */     }
/* 512 */     return Long.toHexString(l);
/*     */   }
/*     */   
/*     */   class UsageTrackerRunnable
/*     */     implements Runnable
/*     */   {
/*     */     private String callerName;
/*     */     private String javaCommand;
/*     */     private long timestamp;
/*     */     private boolean runAsync;
/*     */     private boolean truncated;
/*     */     
/*     */     UsageTrackerRunnable(String paramString1, String paramString2, long paramLong, boolean paramBoolean)
/*     */     {
/* 526 */       this.callerName = paramString1;
/* 527 */       this.javaCommand = (paramString2 != null ? paramString2 : "");
/* 528 */       this.timestamp = paramLong;
/* 529 */       this.runAsync = paramBoolean;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private String limitString(String paramString, int paramInt)
/*     */     {
/* 537 */       if ((paramInt > 0) && (paramString.length() >= paramInt)) {
/* 538 */         UsageTrackerClient.this.printDebug("UsgeTracker: limitString truncating: max=" + paramInt + " length=" + paramString.length() + " String: " + paramString);
/* 539 */         this.truncated = true;
/* 540 */         paramString = paramString.substring(0, paramInt);
/*     */       }
/* 542 */       return paramString;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private String buildMessage(String paramString1, String paramString2, long paramLong)
/*     */     {
/* 551 */       paramString2 = limitString(paramString2, UsageTrackerClient.maxFieldSize);
/* 552 */       if ((this.truncated) && (!UsageTrackerClient.sendTruncated)) {
/* 553 */         return null;
/*     */       }
/* 555 */       StringBuilder localStringBuilder = new StringBuilder();
/* 556 */       appendWithQuotes(localStringBuilder, paramString1);
/* 557 */       localStringBuilder.append(UsageTrackerClient.separator);
/* 558 */       Date localDate = new Date(paramLong);
/* 559 */       appendWithQuotes(localStringBuilder, localDate.toString());
/* 560 */       localStringBuilder.append(UsageTrackerClient.separator);
/*     */       
/* 562 */       String str1 = "0";
/*     */       try {
/* 564 */         InetAddress localInetAddress = InetAddress.getLocalHost();
/* 565 */         str1 = localInetAddress.toString();
/*     */       }
/*     */       catch (Throwable localThrowable) {}
/*     */       
/* 569 */       appendWithQuotes(localStringBuilder, str1);
/* 570 */       localStringBuilder.append(UsageTrackerClient.separator);
/*     */       
/* 572 */       appendWithQuotes(localStringBuilder, paramString2);
/* 573 */       localStringBuilder.append(UsageTrackerClient.separator);
/*     */       
/* 575 */       localStringBuilder.append(getRuntimeDetails());
/* 576 */       localStringBuilder.append("\n");
/* 577 */       String str2 = limitString(localStringBuilder.toString(), UsageTrackerClient.maxSize);
/* 578 */       if ((this.truncated) && (!UsageTrackerClient.sendTruncated)) {
/* 579 */         UsageTrackerClient.this.printVerbose("UsageTracker: length limit exceeded.");
/* 580 */         return null;
/*     */       }
/* 582 */       return str2;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private String getRuntimeDetails()
/*     */     {
/* 591 */       synchronized (UsageTrackerClient.LOCK) {
/* 592 */         if (UsageTrackerClient.staticMessage == null) {
/* 593 */           StringBuilder localStringBuilder1 = new StringBuilder();
/*     */           
/*     */ 
/*     */ 
/* 597 */           boolean bool = this.truncated;
/* 598 */           this.truncated = false;
/* 599 */           appendWithQuotes(localStringBuilder1, UsageTrackerClient.javaHome);
/* 600 */           localStringBuilder1.append(UsageTrackerClient.separator);
/* 601 */           appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.version"));
/* 602 */           localStringBuilder1.append(UsageTrackerClient.separator);
/* 603 */           appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.vm.version"));
/* 604 */           localStringBuilder1.append(UsageTrackerClient.separator);
/* 605 */           appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.vendor"));
/* 606 */           localStringBuilder1.append(UsageTrackerClient.separator);
/* 607 */           appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.vm.vendor"));
/* 608 */           localStringBuilder1.append(UsageTrackerClient.separator);
/* 609 */           appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("os.name"));
/* 610 */           localStringBuilder1.append(UsageTrackerClient.separator);
/* 611 */           appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("os.arch"));
/* 612 */           localStringBuilder1.append(UsageTrackerClient.separator);
/* 613 */           appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("os.version"));
/* 614 */           localStringBuilder1.append(UsageTrackerClient.separator);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 620 */           List localList = getInputArguments();
/* 621 */           StringBuilder localStringBuilder2 = new StringBuilder();
/* 622 */           for (Object localObject1 = localList.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (String)((Iterator)localObject1).next();
/* 623 */             localStringBuilder2.append(addQuotesFor((String)localObject2, " ", UsageTrackerClient.innerQuote));
/* 624 */             localStringBuilder2.append(' '); }
/*     */           Object localObject2;
/* 626 */           appendWithQuotes(localStringBuilder1, localStringBuilder2.toString());
/* 627 */           localStringBuilder1.append(UsageTrackerClient.separator);
/* 628 */           appendWithQuotes(localStringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.class.path"));
/* 629 */           localStringBuilder1.append(UsageTrackerClient.separator);
/*     */           
/*     */ 
/*     */ 
/* 633 */           localObject1 = new StringBuilder();
/* 634 */           for (Object localObject3 : UsageTrackerClient.additionalProperties) {
/* 635 */             ((StringBuilder)localObject1).append(((String)localObject3).trim());
/* 636 */             ((StringBuilder)localObject1).append("=");
/* 637 */             ((StringBuilder)localObject1).append(addQuotesFor(UsageTrackerClient.getPropertyPrivileged(((String)localObject3).trim()), " ", UsageTrackerClient.innerQuote));
/* 638 */             ((StringBuilder)localObject1).append(" ");
/*     */           }
/* 640 */           appendWithQuotes(localStringBuilder1, ((StringBuilder)localObject1).toString());
/*     */           
/*     */ 
/* 643 */           UsageTrackerClient.access$902(localStringBuilder1.toString());
/* 644 */           UsageTrackerClient.access$1402(this.truncated);
/* 645 */           this.truncated = (bool | UsageTrackerClient.staticMessageIsTruncated);
/*     */         }
/*     */         else
/*     */         {
/* 649 */           this.truncated |= UsageTrackerClient.staticMessageIsTruncated;
/*     */         }
/* 651 */         return UsageTrackerClient.staticMessage;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void appendWithQuotes(StringBuilder paramStringBuilder, String paramString)
/*     */     {
/* 662 */       paramStringBuilder.append(UsageTrackerClient.quote);
/* 663 */       paramString = limitString(paramString, UsageTrackerClient.maxFieldSize);
/* 664 */       paramString = paramString.replace(UsageTrackerClient.quote, UsageTrackerClient.quote + UsageTrackerClient.quote);
/* 665 */       if (!paramString.isEmpty()) {
/* 666 */         paramStringBuilder.append(paramString);
/*     */       } else {
/* 668 */         paramStringBuilder.append(" ");
/*     */       }
/* 670 */       paramStringBuilder.append(UsageTrackerClient.quote);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private String addQuotesFor(String paramString1, String paramString2, String paramString3)
/*     */     {
/* 682 */       if (paramString1 == null) {
/* 683 */         return paramString1;
/*     */       }
/* 685 */       paramString1 = paramString1.replace(paramString3, paramString3 + paramString3);
/* 686 */       if (paramString1.indexOf(paramString2) >= 0) {
/* 687 */         paramString1 = paramString3 + paramString1 + paramString3;
/*     */       }
/* 689 */       return paramString1;
/*     */     }
/*     */     
/*     */     private List<String> getInputArguments() {
/* 693 */       (List)AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public List<String> run() {
/*     */           try {
/* 696 */             Class localClass = Class.forName("java.lang.management.ManagementFactory", true, null);
/* 697 */             Method localMethod = localClass.getMethod("getRuntimeMXBean", (Class[])null);
/* 698 */             Object localObject = localMethod.invoke(null, (Object[])null);
/* 699 */             localClass = Class.forName("java.lang.management.RuntimeMXBean", true, null);
/* 700 */             localMethod = localClass.getMethod("getInputArguments", (Class[])null);
/*     */             
/* 702 */             return (List)localMethod.invoke(localObject, (Object[])null);
/*     */           }
/*     */           catch (ClassNotFoundException localClassNotFoundException) {
/* 705 */             return Collections.singletonList("n/a");
/*     */           } catch (NoSuchMethodException localNoSuchMethodException) {
/* 707 */             throw new AssertionError(localNoSuchMethodException);
/*     */           } catch (IllegalAccessException localIllegalAccessException) {
/* 709 */             throw new AssertionError(localIllegalAccessException);
/*     */           } catch (InvocationTargetException localInvocationTargetException) {
/* 711 */             throw new AssertionError(localInvocationTargetException.getCause());
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void sendDatagram(String paramString)
/*     */     {
/* 726 */       UsageTrackerClient.this.printDebug("UsageTracker: sendDatagram");
/* 727 */       try { DatagramSocket localDatagramSocket = new DatagramSocket();Object localObject1 = null;
/* 728 */         try { byte[] arrayOfByte = paramString.getBytes("UTF-8");
/* 729 */           if (arrayOfByte.length > localDatagramSocket.getSendBufferSize()) {
/* 730 */             UsageTrackerClient.this.printVerbose("UsageTracker: message truncated for Datagram.");
/*     */           }
/* 732 */           UsageTrackerClient.this.printDebug("UsageTracker: host=" + UsageTrackerClient.datagramHost + ", port=" + UsageTrackerClient.datagramPort);
/* 733 */           UsageTrackerClient.this.printDebug("UsageTracker: SendBufferSize = " + localDatagramSocket.getSendBufferSize());
/* 734 */           UsageTrackerClient.this.printDebug("UsageTracker: packet length  = " + arrayOfByte.length);
/* 735 */           InetAddress localInetAddress = InetAddress.getByName(UsageTrackerClient.datagramHost);
/*     */           
/*     */ 
/* 738 */           DatagramPacket localDatagramPacket = new DatagramPacket(arrayOfByte, arrayOfByte.length > localDatagramSocket.getSendBufferSize() ? localDatagramSocket.getSendBufferSize() : arrayOfByte.length, localInetAddress, UsageTrackerClient.datagramPort);
/* 739 */           localDatagramSocket.send(localDatagramPacket);
/* 740 */           UsageTrackerClient.this.printVerbose("UsageTracker: done sending to UDP.");
/* 741 */           UsageTrackerClient.this.printDebug("UsageTracker: sent size = " + localDatagramPacket.getLength());
/*     */         }
/*     */         catch (Throwable localThrowable3)
/*     */         {
/* 727 */           localObject1 = localThrowable3;throw localThrowable3;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 742 */           if (localDatagramSocket != null) if (localObject1 != null) try { localDatagramSocket.close(); } catch (Throwable localThrowable4) { ((Throwable)localObject1).addSuppressed(localThrowable4); } else localDatagramSocket.close();
/* 743 */         } } catch (Throwable localThrowable1) { UsageTrackerClient.this.printVerbose("UsageTracker: error in sendDatagram: " + localThrowable1);
/* 744 */         UsageTrackerClient.this.printDebugStackTrace(localThrowable1);
/*     */       }
/*     */     }
/*     */     
/*     */     private void sendToFile(String paramString) {
/* 749 */       UsageTrackerClient.this.printDebug("UsageTracker: sendToFile");
/* 750 */       File localFile = new File(UsageTrackerClient.fullLogFilename);
/* 751 */       if ((UsageTrackerClient.logFileMaxSize >= 0L) && 
/* 752 */         (localFile.length() >= UsageTrackerClient.logFileMaxSize)) {
/* 753 */         UsageTrackerClient.this.printVerbose("UsageTracker: log file size exceeds maximum.");
/* 754 */         return;
/*     */       }
/*     */       
/* 757 */       synchronized (UsageTrackerClient.LOCK) {
/* 758 */         try { FileOutputStream localFileOutputStream = new FileOutputStream(localFile, true);Object localObject1 = null;
/* 759 */           try { OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localFileOutputStream, "UTF-8");Object localObject2 = null;
/* 760 */             try { localOutputStreamWriter.write(paramString, 0, paramString.length());
/* 761 */               UsageTrackerClient.this.printVerbose("UsageTracker: done sending to file.");
/* 762 */               UsageTrackerClient.this.printDebug("UsageTracker: " + UsageTrackerClient.fullLogFilename);
/*     */             }
/*     */             catch (Throwable localThrowable5)
/*     */             {
/* 758 */               localObject2 = localThrowable5;throw localThrowable5; } finally {} } catch (Throwable localThrowable3) { localObject1 = localThrowable3;throw localThrowable3;
/*     */ 
/*     */           }
/*     */           finally
/*     */           {
/* 763 */             if (localFileOutputStream != null) if (localObject1 != null) try { localFileOutputStream.close(); } catch (Throwable localThrowable7) { ((Throwable)localObject1).addSuppressed(localThrowable7); } else localFileOutputStream.close();
/* 764 */           } } catch (Throwable localThrowable1) { UsageTrackerClient.this.printVerbose("UsageTracker: error in sending to file.");
/* 765 */           UsageTrackerClient.this.printDebugStackTrace(localThrowable1);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public void run() {
/* 771 */       if (this.runAsync) {
/* 772 */         UsageTrackerClient.this.setupAndTimestamp(this.timestamp);
/* 773 */         UsageTrackerClient.this.printVerbose("UsageTracker: running asynchronous.");
/*     */       }
/* 775 */       if (UsageTrackerClient.enabled) {
/* 776 */         UsageTrackerClient.this.printDebug("UsageTrackerRunnable.run: " + this.callerName + ", javaCommand: " + this.javaCommand);
/* 777 */         String str = buildMessage(this.callerName, this.javaCommand, this.timestamp);
/*     */         
/* 779 */         if (str != null) {
/* 780 */           if ((UsageTrackerClient.datagramHost != null) && (UsageTrackerClient.datagramPort > 0)) {
/* 781 */             sendDatagram(str);
/*     */           }
/* 783 */           if (UsageTrackerClient.fullLogFilename != null) {
/* 784 */             sendToFile(str);
/*     */           }
/*     */         } else {
/* 787 */           UsageTrackerClient.this.printVerbose("UsageTracker: length limit exceeded.");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\usagetracker\UsageTrackerClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.launcher;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.text.MessageFormat;
/*     */ import java.text.Normalizer;
/*     */ import java.text.Normalizer.Form;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Locale.Category;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Attributes.Name;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.Manifest;
/*     */ import sun.misc.VM;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum LauncherHelper
/*     */ {
/*  71 */   INSTANCE;
/*     */   
/*     */   private static final String MAIN_CLASS = "Main-Class";
/*  74 */   private static StringBuilder outBuf = new StringBuilder();
/*     */   
/*     */   private static final String INDENT = "    ";
/*     */   
/*     */   private static final String VM_SETTINGS = "VM settings:";
/*     */   
/*     */   private static final String PROP_SETTINGS = "Property settings:";
/*     */   private static final String LOCALE_SETTINGS = "Locale settings:";
/*     */   private static final String diagprop = "sun.java.launcher.diag";
/*  83 */   static final boolean trace = VM.getSavedProperty("sun.java.launcher.diag") != null;
/*     */   private static final String defaultBundleName = "sun.launcher.resources.launcher";
/*     */   private static PrintStream ostream;
/*     */   private LauncherHelper() {}
/*     */   
/*     */   private static class ResourceBundleHolder {
/*  89 */     private static final ResourceBundle RB = ResourceBundle.getBundle("sun.launcher.resources.launcher");
/*     */   }
/*     */   
/*  92 */   private static final ClassLoader scloader = ClassLoader.getSystemClassLoader();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Class<?> appClass;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int LM_UNKNOWN = 0;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int LM_CLASS = 1;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int LM_JAR = 2;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final String encprop = "sun.jnu.encoding";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void showSettings(boolean paramBoolean1, String paramString, long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean2)
/*     */   {
/* 121 */     initOutput(paramBoolean1);
/* 122 */     String[] arrayOfString = paramString.split(":");
/*     */     
/* 124 */     String str1 = (arrayOfString.length > 1) && (arrayOfString[1] != null) ? arrayOfString[1].trim() : "all";
/*     */     
/* 126 */     switch (str1) {
/*     */     case "vm": 
/* 128 */       printVmSettings(paramLong1, paramLong2, paramLong3, paramBoolean2);
/*     */       
/* 130 */       break;
/*     */     case "properties": 
/* 132 */       printProperties();
/* 133 */       break;
/*     */     case "locale": 
/* 135 */       printLocale();
/* 136 */       break;
/*     */     default: 
/* 138 */       printVmSettings(paramLong1, paramLong2, paramLong3, paramBoolean2);
/*     */       
/* 140 */       printProperties();
/* 141 */       printLocale();
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void printVmSettings(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean)
/*     */   {
/* 153 */     ostream.println("VM settings:");
/* 154 */     if (paramLong3 != 0L) {
/* 155 */       ostream.println("    Stack Size: " + 
/* 156 */         SizePrefix.scaleValue(paramLong3));
/*     */     }
/* 158 */     if (paramLong1 != 0L) {
/* 159 */       ostream.println("    Min. Heap Size: " + 
/* 160 */         SizePrefix.scaleValue(paramLong1));
/*     */     }
/* 162 */     if (paramLong2 != 0L) {
/* 163 */       ostream.println("    Max. Heap Size: " + 
/* 164 */         SizePrefix.scaleValue(paramLong2));
/*     */     } else {
/* 166 */       ostream.println("    Max. Heap Size (Estimated): " + 
/* 167 */         SizePrefix.scaleValue(Runtime.getRuntime().maxMemory()));
/*     */     }
/* 169 */     ostream.println("    Ergonomics Machine Class: " + (paramBoolean ? "server" : "client"));
/*     */     
/* 171 */     ostream.println("    Using VM: " + 
/* 172 */       System.getProperty("java.vm.name"));
/* 173 */     ostream.println();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void printProperties()
/*     */   {
/* 180 */     Properties localProperties = System.getProperties();
/* 181 */     ostream.println("Property settings:");
/* 182 */     ArrayList localArrayList = new ArrayList();
/* 183 */     localArrayList.addAll(localProperties.stringPropertyNames());
/* 184 */     Collections.sort(localArrayList);
/* 185 */     for (String str : localArrayList) {
/* 186 */       printPropertyValue(str, localProperties.getProperty(str));
/*     */     }
/* 188 */     ostream.println();
/*     */   }
/*     */   
/*     */   private static boolean isPath(String paramString) {
/* 192 */     return (paramString.endsWith(".dirs")) || (paramString.endsWith(".path"));
/*     */   }
/*     */   
/*     */   private static void printPropertyValue(String paramString1, String paramString2) {
/* 196 */     ostream.print("    " + paramString1 + " = ");
/* 197 */     int k; if (paramString1.equals("line.separator")) {
/* 198 */       for (k : paramString2.getBytes()) {
/* 199 */         switch (k) {
/*     */         case 13: 
/* 201 */           ostream.print("\\r ");
/* 202 */           break;
/*     */         case 10: 
/* 204 */           ostream.print("\\n ");
/* 205 */           break;
/*     */         
/*     */ 
/*     */         default: 
/* 209 */           ostream.printf("0x%02X", new Object[] { Integer.valueOf(k & 0xFF) });
/*     */         }
/*     */         
/*     */       }
/* 213 */       ostream.println();
/* 214 */       return;
/*     */     }
/* 216 */     if (!isPath(paramString1)) {
/* 217 */       ostream.println(paramString2);
/* 218 */       return;
/*     */     }
/* 220 */     ??? = paramString2.split(System.getProperty("path.separator"));
/* 221 */     ??? = 1;
/* 222 */     for (String str : ???) {
/* 223 */       if (??? != 0) {
/* 224 */         ostream.println(str);
/* 225 */         ??? = 0;
/*     */       } else {
/* 227 */         ostream.println("        " + str);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void printLocale()
/*     */   {
/* 236 */     Locale localLocale = Locale.getDefault();
/* 237 */     ostream.println("Locale settings:");
/* 238 */     ostream.println("    default locale = " + localLocale
/* 239 */       .getDisplayLanguage());
/* 240 */     ostream.println("    default display locale = " + 
/* 241 */       Locale.getDefault(Category.DISPLAY).getDisplayName());
/* 242 */     ostream.println("    default format locale = " + 
/* 243 */       Locale.getDefault(Category.FORMAT).getDisplayName());
/* 244 */     printLocales();
/* 245 */     ostream.println();
/*     */   }
/*     */   
/*     */   private static void printLocales() {
/* 249 */     Locale[] arrayOfLocale = Locale.getAvailableLocales();
/* 250 */     int i = arrayOfLocale == null ? 0 : arrayOfLocale.length;
/* 251 */     if (i < 1) {
/* 252 */       return;
/*     */     }
/*     */     
/*     */ 
/* 256 */     TreeSet localTreeSet = new TreeSet();
/* 257 */     String str; for (str : arrayOfLocale) {
/* 258 */       localTreeSet.add(str.toString());
/*     */     }
/*     */     
/* 261 */     ostream.print("    available locales = ");
/* 262 */     ??? = localTreeSet.iterator();
/* 263 */     ??? = i - 1;
/* 264 */     for (??? = 0; ((Iterator)???).hasNext(); ???++) {
/* 265 */       str = (String)((Iterator)???).next();
/* 266 */       ostream.print(str);
/* 267 */       if (??? != ???) {
/* 268 */         ostream.print(", ");
/*     */       }
/*     */       
/* 271 */       if ((??? + 1) % 8 == 0) {
/* 272 */         ostream.println();
/* 273 */         ostream.print("        ");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static enum SizePrefix
/*     */   {
/* 280 */     KILO(1024L, "K"), 
/* 281 */     MEGA(1048576L, "M"), 
/* 282 */     GIGA(1073741824L, "G"), 
/* 283 */     TERA(1099511627776L, "T");
/*     */     
/*     */     long size;
/*     */     String abbrev;
/*     */     
/* 288 */     private SizePrefix(long paramLong, String paramString) { this.size = paramLong;
/* 289 */       this.abbrev = paramString;
/*     */     }
/*     */     
/*     */     private static String scale(long paramLong, SizePrefix paramSizePrefix) {
/* 293 */       return 
/* 294 */         BigDecimal.valueOf(paramLong).divide(BigDecimal.valueOf(paramSizePrefix.size), 2, RoundingMode.HALF_EVEN).toPlainString() + paramSizePrefix.abbrev;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     static String scaleValue(long paramLong)
/*     */     {
/* 302 */       if (paramLong < MEGA.size)
/* 303 */         return scale(paramLong, KILO);
/* 304 */       if (paramLong < GIGA.size)
/* 305 */         return scale(paramLong, MEGA);
/* 306 */       if (paramLong < TERA.size) {
/* 307 */         return scale(paramLong, GIGA);
/*     */       }
/* 309 */       return scale(paramLong, TERA);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getLocalizedMessage(String paramString, Object... paramVarArgs)
/*     */   {
/* 319 */     String str = ResourceBundleHolder.RB.getString(paramString);
/* 320 */     return paramVarArgs != null ? MessageFormat.format(str, paramVarArgs) : str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void initHelpMessage(String paramString)
/*     */   {
/* 331 */     outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.header", new Object[] { paramString == null ? "java" : paramString }));
/*     */     
/* 333 */     outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", new Object[] {
/* 334 */       Integer.valueOf(32) }));
/* 335 */     outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", new Object[] {
/* 336 */       Integer.valueOf(64) }));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void appendVmSelectMessage(String paramString1, String paramString2)
/*     */   {
/* 344 */     outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.vmselect", new Object[] { paramString1, paramString2 }));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void appendVmSynonymMessage(String paramString1, String paramString2)
/*     */   {
/* 353 */     outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.hotspot", new Object[] { paramString1, paramString2 }));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void appendVmErgoMessage(boolean paramBoolean, String paramString)
/*     */   {
/* 362 */     outBuf = outBuf.append(getLocalizedMessage("java.launcher.ergo.message1", new Object[] { paramString }));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 367 */     outBuf = paramBoolean ? outBuf.append(",\n" + getLocalizedMessage("java.launcher.ergo.message2", new Object[0]) + "\n\n") : outBuf.append(".\n\n");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void printHelpMessage(boolean paramBoolean)
/*     */   {
/* 376 */     initOutput(paramBoolean);
/* 377 */     outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.footer", new Object[] { File.pathSeparator }));
/*     */     
/* 379 */     ostream.println(outBuf.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static void printXUsageMessage(boolean paramBoolean)
/*     */   {
/* 386 */     initOutput(paramBoolean);
/* 387 */     ostream.println(getLocalizedMessage("java.launcher.X.usage", new Object[] { File.pathSeparator }));
/*     */     
/* 389 */     if (System.getProperty("os.name").contains("OS X")) {
/* 390 */       ostream.println(getLocalizedMessage("java.launcher.X.macosx.usage", new Object[] { File.pathSeparator }));
/*     */     }
/*     */   }
/*     */   
/*     */   static void initOutput(boolean paramBoolean)
/*     */   {
/* 396 */     ostream = paramBoolean ? System.err : System.out;
/*     */   }
/*     */   
/*     */   static String getMainClassFromJar(String paramString) {
/* 400 */     String str1 = null;
/* 401 */     try { JarFile localJarFile = new JarFile(paramString);Object localObject1 = null;
/* 402 */       try { Manifest localManifest = localJarFile.getManifest();
/* 403 */         if (localManifest == null) {
/* 404 */           abort(null, "java.launcher.jar.error2", new Object[] { paramString });
/*     */         }
/* 406 */         Attributes localAttributes = localManifest.getMainAttributes();
/* 407 */         if (localAttributes == null) {
/* 408 */           abort(null, "java.launcher.jar.error3", new Object[] { paramString });
/*     */         }
/* 410 */         str1 = localAttributes.getValue("Main-Class");
/* 411 */         if (str1 == null) {
/* 412 */           abort(null, "java.launcher.jar.error3", new Object[] { paramString });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         String str2;
/*     */         
/*     */ 
/* 420 */         if (localAttributes.containsKey(new Name("JavaFX-Application-Class")))
/*     */         {
/* 422 */           return FXHelper.class.getName();
/*     */         }
/*     */         
/* 425 */         return str1.trim();
/*     */       }
/*     */       catch (Throwable localThrowable1)
/*     */       {
/* 401 */         localObject1 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 426 */         if (localJarFile != null) if (localObject1 != null) try { localJarFile.close(); } catch (Throwable localThrowable4) { ((Throwable)localObject1).addSuppressed(localThrowable4); } else { localJarFile.close();
/*     */           }
/*     */       }
/* 429 */       return null;
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 427 */       abort(localIOException, "java.launcher.jar.error1", new Object[] { paramString });
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
/*     */   static void abort(Throwable paramThrowable, String paramString, Object... paramVarArgs)
/*     */   {
/* 440 */     if (paramString != null) {
/* 441 */       ostream.println(getLocalizedMessage(paramString, paramVarArgs));
/*     */     }
/* 443 */     if (trace) {
/* 444 */       if (paramThrowable != null) {
/* 445 */         paramThrowable.printStackTrace();
/*     */       } else {
/* 447 */         Thread.dumpStack();
/*     */       }
/*     */     }
/* 450 */     System.exit(1);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Class<?> checkAndLoadMain(boolean paramBoolean, int paramInt, String paramString)
/*     */   {
/* 478 */     initOutput(paramBoolean);
/*     */     
/* 480 */     String str = null;
/* 481 */     switch (paramInt) {
/*     */     case 1: 
/* 483 */       str = paramString;
/* 484 */       break;
/*     */     case 2: 
/* 486 */       str = getMainClassFromJar(paramString);
/* 487 */       break;
/*     */     
/*     */     default: 
/* 490 */       throw new InternalError("" + paramInt + ": Unknown launch mode");
/*     */     }
/* 492 */     str = str.replace('/', '.');
/* 493 */     Class localClass = null;
/*     */     try {
/* 495 */       localClass = scloader.loadClass(str);
/*     */     } catch (NoClassDefFoundError|ClassNotFoundException localNoClassDefFoundError1) {
/* 497 */       if ((System.getProperty("os.name", "").contains("OS X")) && 
/* 498 */         (Normalizer.isNormalized(str, Form.NFD)))
/*     */       {
/*     */         try
/*     */         {
/*     */ 
/* 503 */           localClass = scloader.loadClass(Normalizer.normalize(str, Form.NFC));
/*     */         } catch (NoClassDefFoundError|ClassNotFoundException localNoClassDefFoundError2) {
/* 505 */           abort(localNoClassDefFoundError1, "java.launcher.cls.error1", new Object[] { str });
/*     */         }
/*     */       } else {
/* 508 */         abort(localNoClassDefFoundError1, "java.launcher.cls.error1", new Object[] { str });
/*     */       }
/*     */     }
/*     */     
/* 512 */     appClass = localClass;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 519 */     if ((localClass.equals(FXHelper.class)) || 
/* 520 */       (FXHelper.doesExtendFXApplication(localClass)))
/*     */     {
/* 522 */       FXHelper.setFXLaunchParameters(paramString, paramInt);
/* 523 */       return FXHelper.class;
/*     */     }
/*     */     
/* 526 */     validateMainClass(localClass);
/* 527 */     return localClass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Class<?> getApplicationClass()
/*     */   {
/* 537 */     return appClass;
/*     */   }
/*     */   
/*     */   static void validateMainClass(Class<?> paramClass)
/*     */   {
/*     */     Method localMethod;
/*     */     try {
/* 544 */       localMethod = paramClass.getMethod("main", new Class[] { String[].class });
/*     */     }
/*     */     catch (NoSuchMethodException localNoSuchMethodException) {
/* 547 */       abort(null, "java.launcher.cls.error4", new Object[] { paramClass.getName(), "javafx.application.Application" });
/*     */       
/* 549 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 557 */     int i = localMethod.getModifiers();
/* 558 */     if (!Modifier.isStatic(i)) {
/* 559 */       abort(null, "java.launcher.cls.error2", new Object[] { "static", localMethod
/* 560 */         .getDeclaringClass().getName() });
/*     */     }
/* 562 */     if (localMethod.getReturnType() != Void.TYPE) {
/* 563 */       abort(null, "java.launcher.cls.error3", new Object[] {localMethod
/* 564 */         .getDeclaringClass().getName() });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 569 */   private static String encoding = null;
/* 570 */   private static boolean isCharsetSupported = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static String makePlatformString(boolean paramBoolean, byte[] paramArrayOfByte)
/*     */   {
/* 577 */     initOutput(paramBoolean);
/* 578 */     if (encoding == null) {
/* 579 */       encoding = System.getProperty("sun.jnu.encoding");
/* 580 */       isCharsetSupported = Charset.isSupported(encoding);
/*     */     }
/*     */     try {
/* 583 */       return isCharsetSupported ? new String(paramArrayOfByte, encoding) : new String(paramArrayOfByte);
/*     */ 
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */     {
/* 588 */       abort(localUnsupportedEncodingException, null, new Object[0]);
/*     */     }
/* 590 */     return null;
/*     */   }
/*     */   
/*     */   static String[] expandArgs(String[] paramArrayOfString) {
/* 594 */     ArrayList localArrayList = new ArrayList();
/* 595 */     for (String str : paramArrayOfString) {
/* 596 */       localArrayList.add(new StdArg(str));
/*     */     }
/* 598 */     return expandArgs(localArrayList);
/*     */   }
/*     */   
/*     */   static String[] expandArgs(List<StdArg> paramList) {
/* 602 */     ArrayList localArrayList = new ArrayList();
/* 603 */     if (trace) {
/* 604 */       System.err.println("Incoming arguments:");
/*     */     }
/* 606 */     for (Object localObject1 = paramList.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (StdArg)((Iterator)localObject1).next();
/* 607 */       if (trace) {
/* 608 */         System.err.println(localObject2);
/*     */       }
/* 610 */       if (((StdArg)localObject2).needsExpansion) {
/* 611 */         File localFile1 = new File(((StdArg)localObject2).arg);
/* 612 */         File localFile2 = localFile1.getParentFile();
/* 613 */         str = localFile1.getName();
/* 614 */         if (localFile2 == null) {
/* 615 */           localFile2 = new File(".");
/*     */         }
/*     */         try {
/* 618 */           DirectoryStream localDirectoryStream = Files.newDirectoryStream(localFile2.toPath(), str);Object localObject3 = null;
/* 619 */           try { int k = 0;
/* 620 */             for (Path localPath : localDirectoryStream) {
/* 621 */               localArrayList.add(localPath.normalize().toString());
/* 622 */               k++;
/*     */             }
/* 624 */             if (k == 0) {
/* 625 */               localArrayList.add(((StdArg)localObject2).arg);
/*     */             }
/*     */           }
/*     */           catch (Throwable localThrowable2)
/*     */           {
/* 617 */             localObject3 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */ 
/*     */ 
/*     */           }
/*     */           finally
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 627 */             if (localDirectoryStream != null) if (localObject3 != null) try { localDirectoryStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject3).addSuppressed(localThrowable3); } else localDirectoryStream.close();
/* 628 */           } } catch (Exception localException) { localArrayList.add(((StdArg)localObject2).arg);
/* 629 */           if (trace) {
/* 630 */             System.err.println("Warning: passing argument as-is " + localObject2);
/* 631 */             System.err.print(localException);
/*     */           }
/*     */         }
/*     */       } else {
/* 635 */         localArrayList.add(((StdArg)localObject2).arg); } }
/*     */     Object localObject2;
/*     */     String str;
/* 638 */     localObject1 = new String[localArrayList.size()];
/* 639 */     localArrayList.toArray((Object[])localObject1);
/*     */     
/* 641 */     if (trace) {
/* 642 */       System.err.println("Expanded arguments:");
/* 643 */       for (str : localObject1) {
/* 644 */         System.err.println(str);
/*     */       }
/*     */     }
/* 647 */     return (String[])localObject1;
/*     */   }
/*     */   
/*     */   private static class StdArg {
/*     */     final String arg;
/*     */     final boolean needsExpansion;
/*     */     
/*     */     StdArg(String paramString, boolean paramBoolean) {
/* 655 */       this.arg = paramString;
/* 656 */       this.needsExpansion = paramBoolean;
/*     */     }
/*     */     
/*     */ 
/*     */     StdArg(String paramString)
/*     */     {
/* 662 */       this.arg = paramString.substring(1);
/* 663 */       this.needsExpansion = (paramString.charAt(0) == 'T');
/*     */     }
/*     */     
/* 666 */     public String toString() { return "StdArg{arg=" + this.arg + ", needsExpansion=" + this.needsExpansion + '}'; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final class FXHelper
/*     */   {
/*     */     private static final String JAVAFX_APPLICATION_MARKER = "JavaFX-Application-Class";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private static final String JAVAFX_APPLICATION_CLASS_NAME = "javafx.application.Application";
/*     */     
/*     */ 
/*     */ 
/*     */     private static final String JAVAFX_LAUNCHER_CLASS_NAME = "com.sun.javafx.application.LauncherImpl";
/*     */     
/*     */ 
/*     */ 
/*     */     private static final String JAVAFX_LAUNCH_MODE_CLASS = "LM_CLASS";
/*     */     
/*     */ 
/*     */ 
/*     */     private static final String JAVAFX_LAUNCH_MODE_JAR = "LM_JAR";
/*     */     
/*     */ 
/*     */ 
/* 696 */     private static String fxLaunchName = null;
/* 697 */     private static String fxLaunchMode = null;
/*     */     
/* 699 */     private static Class<?> fxLauncherClass = null;
/* 700 */     private static Method fxLauncherMethod = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static void setFXLaunchParameters(String paramString, int paramInt)
/*     */     {
/*     */       try
/*     */       {
/* 710 */         fxLauncherClass = LauncherHelper.scloader.loadClass("com.sun.javafx.application.LauncherImpl");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 716 */         fxLauncherMethod = fxLauncherClass.getMethod("launchApplication", new Class[] { String.class, String.class, String[].class });
/*     */         
/*     */ 
/*     */ 
/* 720 */         int i = fxLauncherMethod.getModifiers();
/* 721 */         if (!Modifier.isStatic(i)) {
/* 722 */           LauncherHelper.abort(null, "java.launcher.javafx.error1", new Object[0]);
/*     */         }
/* 724 */         if (fxLauncherMethod.getReturnType() != Void.TYPE) {
/* 725 */           LauncherHelper.abort(null, "java.launcher.javafx.error1", new Object[0]);
/*     */         }
/*     */       } catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException) {
/* 728 */         LauncherHelper.abort(localClassNotFoundException, "java.launcher.cls.error5", new Object[] { localClassNotFoundException });
/*     */       }
/*     */       
/* 731 */       fxLaunchName = paramString;
/* 732 */       switch (paramInt) {
/*     */       case 1: 
/* 734 */         fxLaunchMode = "LM_CLASS";
/* 735 */         break;
/*     */       case 2: 
/* 737 */         fxLaunchMode = "LM_JAR";
/* 738 */         break;
/*     */       
/*     */       default: 
/* 741 */         throw new InternalError(paramInt + ": Unknown launch mode");
/*     */       }
/*     */       
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private static boolean doesExtendFXApplication(Class<?> paramClass)
/*     */     {
/* 751 */       for (Class localClass = paramClass.getSuperclass(); localClass != null; 
/* 752 */           localClass = localClass.getSuperclass()) {
/* 753 */         if (localClass.getName().equals("javafx.application.Application")) {
/* 754 */           return true;
/*     */         }
/*     */       }
/* 757 */       return false;
/*     */     }
/*     */     
/*     */     public static void main(String... paramVarArgs) throws Exception {
/* 761 */       if ((fxLauncherMethod == null) || (fxLaunchMode == null) || (fxLaunchName == null))
/*     */       {
/*     */ 
/* 764 */         throw new RuntimeException("Invalid JavaFX launch parameters");
/*     */       }
/*     */       
/* 767 */       fxLauncherMethod.invoke(null, new Object[] { fxLaunchName, fxLaunchMode, paramVarArgs });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\launcher\LauncherHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
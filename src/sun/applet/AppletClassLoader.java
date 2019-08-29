/*     */ package sun.applet;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FilePermission;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.net.URLConnection;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.CodeSource;
/*     */ import java.security.Permission;
/*     */ import java.security.PermissionCollection;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.NoSuchElementException;
/*     */ import sun.awt.AppContext;
/*     */ import sun.misc.IOUtils;
/*     */ import sun.net.www.ParseUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AppletClassLoader
/*     */   extends URLClassLoader
/*     */ {
/*     */   private URL base;
/*     */   private CodeSource codesource;
/*     */   private AccessControlContext acc;
/*  67 */   private boolean exceptionStatus = false;
/*     */   
/*  69 */   private final Object threadGroupSynchronizer = new Object();
/*  70 */   private final Object grabReleaseSynchronizer = new Object();
/*     */   
/*  72 */   private boolean codebaseLookup = true;
/*  73 */   private volatile boolean allowRecursiveDirectoryRead = true;
/*     */   
/*     */ 
/*     */ 
/*     */   protected AppletClassLoader(URL paramURL)
/*     */   {
/*  79 */     super(new URL[0]);
/*  80 */     this.base = paramURL;
/*  81 */     this.codesource = new CodeSource(paramURL, (Certificate[])null);
/*     */     
/*  83 */     this.acc = AccessController.getContext();
/*     */   }
/*     */   
/*     */   public void disableRecursiveDirectoryRead() {
/*  87 */     this.allowRecursiveDirectoryRead = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void setCodebaseLookup(boolean paramBoolean)
/*     */   {
/*  95 */     this.codebaseLookup = paramBoolean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   URL getBaseURL()
/*     */   {
/* 102 */     return this.base;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public URL[] getURLs()
/*     */   {
/* 109 */     URL[] arrayOfURL1 = super.getURLs();
/* 110 */     URL[] arrayOfURL2 = new URL[arrayOfURL1.length + 1];
/* 111 */     System.arraycopy(arrayOfURL1, 0, arrayOfURL2, 0, arrayOfURL1.length);
/* 112 */     arrayOfURL2[(arrayOfURL2.length - 1)] = this.base;
/* 113 */     return arrayOfURL2;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void addJar(String paramString)
/*     */     throws IOException
/*     */   {
/*     */     URL localURL;
/*     */     
/*     */     try
/*     */     {
/* 124 */       localURL = new URL(this.base, paramString);
/*     */     } catch (MalformedURLException localMalformedURLException) {
/* 126 */       throw new IllegalArgumentException("name");
/*     */     }
/* 128 */     addURL(localURL);
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
/*     */   public synchronized Class loadClass(String paramString, boolean paramBoolean)
/*     */     throws ClassNotFoundException
/*     */   {
/* 145 */     int i = paramString.lastIndexOf('.');
/* 146 */     if (i != -1) {
/* 147 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 148 */       if (localSecurityManager != null)
/* 149 */         localSecurityManager.checkPackageAccess(paramString.substring(0, i));
/*     */     }
/*     */     try {
/* 152 */       return super.loadClass(paramString, paramBoolean);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/* 155 */       throw localClassNotFoundException;
/*     */     }
/*     */     catch (RuntimeException localRuntimeException) {
/* 158 */       throw localRuntimeException;
/*     */     }
/*     */     catch (Error localError) {
/* 161 */       throw localError;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Class findClass(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/* 171 */     int i = paramString.indexOf(";");
/* 172 */     String str1 = "";
/* 173 */     if (i != -1) {
/* 174 */       str1 = paramString.substring(i, paramString.length());
/* 175 */       paramString = paramString.substring(0, i);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 180 */       return super.findClass(paramString);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException)
/*     */     {
/*     */ 
/*     */ 
/* 188 */       if (!this.codebaseLookup) {
/* 189 */         throw new ClassNotFoundException(paramString);
/*     */       }
/*     */       
/* 192 */       String str2 = ParseUtil.encodePath(paramString.replace('.', '/'), false);
/* 193 */       final String str3 = str2 + ".class" + str1;
/*     */       try {
/* 195 */         byte[] arrayOfByte = (byte[])AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */         {
/*     */           public Object run() throws IOException {
/*     */             try {
/* 199 */               URL localURL = new URL(AppletClassLoader.this.base, str3);
/*     */               
/*     */ 
/* 202 */               if ((AppletClassLoader.this.base.getProtocol().equals(localURL.getProtocol())) && 
/* 203 */                 (AppletClassLoader.this.base.getHost().equals(localURL.getHost())) && 
/* 204 */                 (AppletClassLoader.this.base.getPort() == localURL.getPort())) {
/* 205 */                 return AppletClassLoader.getBytes(localURL);
/*     */               }
/*     */               
/* 208 */               return null;
/*     */             }
/*     */             catch (Exception localException) {}
/* 211 */             return null; } }, this.acc);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 216 */         if (arrayOfByte != null) {
/* 217 */           return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length, this.codesource);
/*     */         }
/* 219 */         throw new ClassNotFoundException(paramString);
/*     */       }
/*     */       catch (PrivilegedActionException localPrivilegedActionException) {
/* 222 */         throw new ClassNotFoundException(paramString, localPrivilegedActionException.getException());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PermissionCollection getPermissions(CodeSource paramCodeSource)
/*     */   {
/* 247 */     PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
/*     */     
/* 249 */     URL localURL = paramCodeSource.getLocation();
/*     */     
/* 251 */     String str1 = null;
/*     */     Permission localPermission1;
/*     */     try
/*     */     {
/* 255 */       localPermission1 = localURL.openConnection().getPermission();
/*     */     } catch (IOException localIOException1) {
/* 257 */       localPermission1 = null;
/*     */     }
/*     */     
/* 260 */     if ((localPermission1 instanceof FilePermission)) {
/* 261 */       str1 = localPermission1.getName();
/* 262 */     } else if ((localPermission1 == null) && (localURL.getProtocol().equals("file"))) {
/* 263 */       str1 = localURL.getFile().replace('/', File.separatorChar);
/* 264 */       str1 = ParseUtil.decode(str1);
/*     */     }
/*     */     
/* 267 */     if (str1 != null) {
/* 268 */       String str2 = str1;
/* 269 */       if (!str1.endsWith(File.separator)) {
/* 270 */         int i = str1.lastIndexOf(File.separatorChar);
/* 271 */         if (i != -1) {
/* 272 */           str1 = str1.substring(0, i + 1) + "-";
/* 273 */           localPermissionCollection.add(new FilePermission(str1, "read"));
/*     */         }
/*     */       }
/*     */       
/* 277 */       File localFile = new File(str2);
/* 278 */       boolean bool = localFile.isDirectory();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 283 */       if ((this.allowRecursiveDirectoryRead) && ((bool) || 
/* 284 */         (str2.toLowerCase().endsWith(".jar")) || 
/* 285 */         (str2.toLowerCase().endsWith(".zip"))))
/*     */       {
/*     */         Permission localPermission2;
/*     */         try {
/* 289 */           localPermission2 = this.base.openConnection().getPermission();
/*     */         } catch (IOException localIOException2) {
/* 291 */           localPermission2 = null; }
/*     */         String str3;
/* 293 */         if ((localPermission2 instanceof FilePermission)) {
/* 294 */           str3 = localPermission2.getName();
/* 295 */           if (str3.endsWith(File.separator)) {
/* 296 */             str3 = str3 + "-";
/*     */           }
/* 298 */           localPermissionCollection.add(new FilePermission(str3, "read"));
/*     */         }
/* 300 */         else if ((localPermission2 == null) && (this.base.getProtocol().equals("file"))) {
/* 301 */           str3 = this.base.getFile().replace('/', File.separatorChar);
/* 302 */           str3 = ParseUtil.decode(str3);
/* 303 */           if (str3.endsWith(File.separator)) {
/* 304 */             str3 = str3 + "-";
/*     */           }
/* 306 */           localPermissionCollection.add(new FilePermission(str3, "read"));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 311 */     return localPermissionCollection;
/*     */   }
/*     */   
/*     */ 
/*     */   private static byte[] getBytes(URL paramURL)
/*     */     throws IOException
/*     */   {
/* 318 */     URLConnection localURLConnection = paramURL.openConnection();
/* 319 */     if ((localURLConnection instanceof HttpURLConnection)) {
/* 320 */       HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURLConnection;
/* 321 */       int j = localHttpURLConnection.getResponseCode();
/* 322 */       if (j >= 400) {
/* 323 */         throw new IOException("open HTTP connection failed.");
/*     */       }
/*     */     }
/* 326 */     int i = localURLConnection.getContentLength();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 332 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream(localURLConnection.getInputStream());
/*     */     byte[] arrayOfByte;
/*     */     try
/*     */     {
/* 336 */       arrayOfByte = IOUtils.readFully(localBufferedInputStream, i, true);
/*     */     } finally {
/* 338 */       localBufferedInputStream.close();
/*     */     }
/* 340 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/* 344 */   private Object syncResourceAsStream = new Object();
/* 345 */   private Object syncResourceAsStreamFromJar = new Object();
/*     */   
/*     */ 
/* 348 */   private boolean resourceAsStreamInCall = false;
/* 349 */   private boolean resourceAsStreamFromJarInCall = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private AppletThreadGroup threadGroup;
/*     */   
/*     */ 
/*     */ 
/*     */   private AppContext appContext;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputStream getResourceAsStream(String paramString)
/*     */   {
/* 365 */     if (paramString == null) {
/* 366 */       throw new NullPointerException("name");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 371 */       InputStream localInputStream = null;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 381 */       synchronized (this.syncResourceAsStream)
/*     */       {
/* 383 */         this.resourceAsStreamInCall = true;
/*     */         
/*     */ 
/* 386 */         localInputStream = super.getResourceAsStream(paramString);
/*     */         
/* 388 */         this.resourceAsStreamInCall = false;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 393 */       if ((this.codebaseLookup == true) && (localInputStream == null))
/*     */       {
/*     */ 
/*     */ 
/* 397 */         ??? = new URL(this.base, ParseUtil.encodePath(paramString, false)); }
/* 398 */       return ((URL)???).openStream();
/*     */     }
/*     */     catch (Exception localException) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 405 */     return null;
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
/*     */   public InputStream getResourceAsStreamFromJar(String paramString)
/*     */   {
/* 424 */     if (paramString == null) {
/* 425 */       throw new NullPointerException("name");
/*     */     }
/*     */     try
/*     */     {
/* 429 */       InputStream localInputStream = null;
/* 430 */       synchronized (this.syncResourceAsStreamFromJar) {
/* 431 */         this.resourceAsStreamFromJarInCall = true;
/*     */         
/* 433 */         localInputStream = super.getResourceAsStream(paramString);
/* 434 */         this.resourceAsStreamFromJarInCall = false;
/*     */       }
/*     */       
/* 437 */       return localInputStream;
/*     */     } catch (Exception localException) {}
/* 439 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public URL findResource(String paramString)
/*     */   {
/* 450 */     URL localURL = super.findResource(paramString);
/*     */     
/*     */ 
/*     */ 
/* 454 */     if (paramString.startsWith("META-INF/")) {
/* 455 */       return localURL;
/*     */     }
/*     */     
/*     */ 
/* 459 */     if (!this.codebaseLookup) {
/* 460 */       return localURL;
/*     */     }
/* 462 */     if (localURL == null)
/*     */     {
/*     */ 
/*     */ 
/* 466 */       boolean bool1 = false;
/* 467 */       synchronized (this.syncResourceAsStreamFromJar) {
/* 468 */         bool1 = this.resourceAsStreamFromJarInCall;
/*     */       }
/*     */       
/* 471 */       if (bool1) {
/* 472 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 480 */       boolean bool2 = false;
/*     */       
/* 482 */       synchronized (this.syncResourceAsStream)
/*     */       {
/* 484 */         bool2 = this.resourceAsStreamInCall;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 491 */       if (!bool2)
/*     */       {
/*     */         try
/*     */         {
/* 495 */           localURL = new URL(this.base, ParseUtil.encodePath(paramString, false));
/*     */           
/* 497 */           if (!resourceExists(localURL)) {
/* 498 */             localURL = null;
/*     */           }
/*     */         } catch (Exception localException) {
/* 501 */           localURL = null;
/*     */         }
/*     */       }
/*     */     }
/* 505 */     return localURL;
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
/*     */   private boolean resourceExists(URL paramURL)
/*     */   {
/* 519 */     boolean bool = true;
/*     */     try {
/* 521 */       URLConnection localURLConnection = paramURL.openConnection();
/* 522 */       Object localObject; if ((localURLConnection instanceof HttpURLConnection)) {
/* 523 */         localObject = (HttpURLConnection)localURLConnection;
/*     */         
/*     */ 
/*     */ 
/* 527 */         ((HttpURLConnection)localObject).setRequestMethod("HEAD");
/*     */         
/* 529 */         int i = ((HttpURLConnection)localObject).getResponseCode();
/* 530 */         if (i == 200) {
/* 531 */           return true;
/*     */         }
/* 533 */         if (i >= 400) {
/* 534 */           return false;
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/* 545 */         localObject = localURLConnection.getInputStream();
/* 546 */         ((InputStream)localObject).close();
/*     */       }
/*     */     } catch (Exception localException) {
/* 549 */       bool = false;
/*     */     }
/* 551 */     return bool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration findResources(String paramString)
/*     */     throws IOException
/*     */   {
/* 561 */     final Enumeration localEnumeration = super.findResources(paramString);
/*     */     
/*     */ 
/*     */ 
/* 565 */     if (paramString.startsWith("META-INF/")) {
/* 566 */       return localEnumeration;
/*     */     }
/*     */     
/*     */ 
/* 570 */     if (!this.codebaseLookup) {
/* 571 */       return localEnumeration;
/*     */     }
/* 573 */     URL localURL1 = new URL(this.base, ParseUtil.encodePath(paramString, false));
/* 574 */     if (!resourceExists(localURL1)) {
/* 575 */       localURL1 = null;
/*     */     }
/*     */     
/* 578 */     final URL localURL2 = localURL1;
/* 579 */     new Enumeration() {
/*     */       private boolean done;
/*     */       
/* 582 */       public Object nextElement() { if (!this.done) {
/* 583 */           if (localEnumeration.hasMoreElements()) {
/* 584 */             return localEnumeration.nextElement();
/*     */           }
/* 586 */           this.done = true;
/* 587 */           if (localURL2 != null) {
/* 588 */             return localURL2;
/*     */           }
/*     */         }
/* 591 */         throw new NoSuchElementException();
/*     */       }
/*     */       
/* 594 */       public boolean hasMoreElements() { return (!this.done) && ((localEnumeration.hasMoreElements()) || (localURL2 != null)); }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Class loadCode(String paramString)
/*     */     throws ClassNotFoundException
/*     */   {
/* 606 */     paramString = paramString.replace('/', '.');
/* 607 */     paramString = paramString.replace(File.separatorChar, '.');
/*     */     
/*     */ 
/* 610 */     String str1 = null;
/* 611 */     int i = paramString.indexOf(";");
/* 612 */     if (i != -1) {
/* 613 */       str1 = paramString.substring(i, paramString.length());
/* 614 */       paramString = paramString.substring(0, i);
/*     */     }
/*     */     
/*     */ 
/* 618 */     String str2 = paramString;
/*     */     
/* 620 */     if ((paramString.endsWith(".class")) || (paramString.endsWith(".java"))) {
/* 621 */       paramString = paramString.substring(0, paramString.lastIndexOf('.'));
/*     */     }
/*     */     try {
/* 624 */       if (str1 != null)
/* 625 */         paramString = paramString + str1;
/* 626 */       return loadClass(paramString);
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException)
/*     */     {
/* 631 */       if (str1 != null)
/* 632 */         str2 = str2 + str1;
/*     */     }
/* 634 */     return loadClass(str2);
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
/*     */   public ThreadGroup getThreadGroup()
/*     */   {
/* 647 */     synchronized (this.threadGroupSynchronizer) {
/* 648 */       if ((this.threadGroup == null) || (this.threadGroup.isDestroyed())) {
/* 649 */         AccessController.doPrivileged(new PrivilegedAction() {
/*     */           public Object run() {
/* 651 */             AppletClassLoader.this.threadGroup = new AppletThreadGroup(AppletClassLoader.this.base + "-threadGroup");
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 658 */             AppContextCreator localAppContextCreator = new AppContextCreator(AppletClassLoader.this.threadGroup);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 665 */             localAppContextCreator.setContextClassLoader(AppletClassLoader.this);
/*     */             
/* 667 */             localAppContextCreator.start();
/*     */             try {
/* 669 */               synchronized (localAppContextCreator.syncObject) {
/* 670 */                 while (!localAppContextCreator.created) {
/* 671 */                   localAppContextCreator.syncObject.wait();
/*     */                 }
/*     */               }
/*     */             } catch (InterruptedException localInterruptedException) {}
/* 675 */             AppletClassLoader.this.appContext = localAppContextCreator.appContext;
/* 676 */             return null;
/*     */           }
/*     */         });
/*     */       }
/* 680 */       return this.threadGroup;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AppContext getAppContext()
/*     */   {
/* 688 */     return this.appContext;
/*     */   }
/*     */   
/* 691 */   int usageCount = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void grab()
/*     */   {
/* 698 */     synchronized (this.grabReleaseSynchronizer) {
/* 699 */       this.usageCount += 1;
/*     */     }
/* 701 */     getThreadGroup();
/*     */   }
/*     */   
/*     */   protected void setExceptionStatus()
/*     */   {
/* 706 */     this.exceptionStatus = true;
/*     */   }
/*     */   
/*     */   public boolean getExceptionStatus()
/*     */   {
/* 711 */     return this.exceptionStatus;
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
/*     */   protected void release()
/*     */   {
/* 728 */     AppContext localAppContext = null;
/*     */     
/* 730 */     synchronized (this.grabReleaseSynchronizer) {
/* 731 */       if (this.usageCount > 1) {
/* 732 */         this.usageCount -= 1;
/*     */       } else {
/* 734 */         synchronized (this.threadGroupSynchronizer) {
/* 735 */           localAppContext = resetAppContext();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 742 */     if (localAppContext != null) {
/*     */       try {
/* 744 */         localAppContext.dispose();
/*     */       }
/*     */       catch (IllegalThreadStateException localIllegalThreadStateException) {}
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
/*     */   protected AppContext resetAppContext()
/*     */   {
/* 759 */     AppContext localAppContext = null;
/*     */     
/* 761 */     synchronized (this.threadGroupSynchronizer)
/*     */     {
/* 763 */       localAppContext = this.appContext;
/* 764 */       this.usageCount = 0;
/* 765 */       this.appContext = null;
/* 766 */       this.threadGroup = null;
/*     */     }
/* 768 */     return localAppContext;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 773 */   private HashMap jdk11AppletInfo = new HashMap();
/* 774 */   private HashMap jdk12AppletInfo = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void setJDK11Target(Class paramClass, boolean paramBoolean)
/*     */   {
/* 785 */     this.jdk11AppletInfo.put(paramClass.toString(), Boolean.valueOf(paramBoolean));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void setJDK12Target(Class paramClass, boolean paramBoolean)
/*     */   {
/* 797 */     this.jdk12AppletInfo.put(paramClass.toString(), Boolean.valueOf(paramBoolean));
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
/*     */   Boolean isJDK11Target(Class paramClass)
/*     */   {
/* 810 */     return (Boolean)this.jdk11AppletInfo.get(paramClass.toString());
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
/*     */   Boolean isJDK12Target(Class paramClass)
/*     */   {
/* 823 */     return (Boolean)this.jdk12AppletInfo.get(paramClass.toString());
/*     */   }
/*     */   
/* 826 */   private static AppletMessageHandler mh = new AppletMessageHandler("appletclassloader");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void printError(String paramString, Throwable paramThrowable)
/*     */   {
/* 833 */     String str = null;
/* 834 */     if (paramThrowable == null) {
/* 835 */       str = mh.getMessage("filenotfound", paramString);
/* 836 */     } else if ((paramThrowable instanceof IOException)) {
/* 837 */       str = mh.getMessage("fileioexception", paramString);
/* 838 */     } else if ((paramThrowable instanceof ClassFormatError)) {
/* 839 */       str = mh.getMessage("fileformat", paramString);
/* 840 */     } else if ((paramThrowable instanceof ThreadDeath)) {
/* 841 */       str = mh.getMessage("filedeath", paramString);
/* 842 */     } else if ((paramThrowable instanceof Error)) {
/* 843 */       str = mh.getMessage("fileerror", paramThrowable.toString(), paramString);
/*     */     }
/* 845 */     if (str != null) {
/* 846 */       System.err.println(str);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\applet\AppletClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
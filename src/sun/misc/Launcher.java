/*     */ package sun.misc;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.net.URLStreamHandler;
/*     */ import java.net.URLStreamHandlerFactory;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.CodeSource;
/*     */ import java.security.PermissionCollection;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
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
/*     */ public class Launcher
/*     */ {
/*  53 */   private static URLStreamHandlerFactory factory = new Factory(null);
/*  54 */   private static Launcher launcher = new Launcher();
/*     */   
/*  56 */   private static String bootClassPath = System.getProperty("sun.boot.class.path");
/*     */   private ClassLoader loader;
/*     */   
/*  59 */   public static Launcher getLauncher() { return launcher; }
/*     */   
/*     */ 
/*     */   public Launcher()
/*     */   {
/*     */     ExtClassLoader localExtClassLoader;
/*     */     
/*     */     try
/*     */     {
/*  68 */       localExtClassLoader = ExtClassLoader.getExtClassLoader();
/*     */     } catch (IOException localIOException1) {
/*  70 */       throw new InternalError("Could not create extension class loader", localIOException1);
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*  76 */       this.loader = AppClassLoader.getAppClassLoader(localExtClassLoader);
/*     */     } catch (IOException localIOException2) {
/*  78 */       throw new InternalError("Could not create application class loader", localIOException2);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  83 */     Thread.currentThread().setContextClassLoader(this.loader);
/*     */     
/*     */ 
/*  86 */     String str = System.getProperty("java.security.manager");
/*  87 */     if (str != null) {
/*  88 */       SecurityManager localSecurityManager = null;
/*  89 */       if (("".equals(str)) || ("default".equals(str))) {
/*  90 */         localSecurityManager = new SecurityManager();
/*     */       } else {
/*     */         try {
/*  93 */           localSecurityManager = (SecurityManager)this.loader.loadClass(str).newInstance();
/*     */         }
/*     */         catch (IllegalAccessException localIllegalAccessException) {}catch (InstantiationException localInstantiationException) {}catch (ClassNotFoundException localClassNotFoundException) {}catch (ClassCastException localClassCastException) {}
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 100 */       if (localSecurityManager != null) {
/* 101 */         System.setSecurityManager(localSecurityManager);
/*     */       } else {
/* 103 */         throw new InternalError("Could not create SecurityManager: " + str);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static URLStreamHandler fileHandler;
/*     */   
/*     */   public ClassLoader getClassLoader()
/*     */   {
/* 113 */     return this.loader;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class ExtClassLoader
/*     */     extends URLClassLoader
/*     */   {
/* 122 */     static { ClassLoader.registerAsParallelCapable(); }
/*     */     
/* 124 */     private static volatile ExtClassLoader instance = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public static ExtClassLoader getExtClassLoader()
/*     */       throws IOException
/*     */     {
/* 132 */       if (instance == null) {
/* 133 */         synchronized (ExtClassLoader.class) {
/* 134 */           if (instance == null) {
/* 135 */             instance = createExtClassLoader();
/*     */           }
/*     */         }
/*     */       }
/* 139 */       return instance;
/*     */     }
/*     */     
/*     */ 
/*     */     private static ExtClassLoader createExtClassLoader()
/*     */       throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 148 */         (ExtClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */         {
/*     */           public ExtClassLoader run() throws IOException {
/* 151 */             File[] arrayOfFile = ExtClassLoader.access$100();
/* 152 */             int i = arrayOfFile.length;
/* 153 */             for (int j = 0; j < i; j++) {
/* 154 */               MetaIndex.registerDirectory(arrayOfFile[j]);
/*     */             }
/* 156 */             return new ExtClassLoader(arrayOfFile);
/*     */           }
/*     */         });
/*     */       } catch (PrivilegedActionException localPrivilegedActionException) {
/* 160 */         throw ((IOException)localPrivilegedActionException.getException());
/*     */       }
/*     */     }
/*     */     
/*     */     void addExtURL(URL paramURL) {
/* 165 */       super.addURL(paramURL);
/*     */     }
/*     */     
/*     */ 
/*     */     public ExtClassLoader(File[] paramArrayOfFile)
/*     */       throws IOException
/*     */     {
/* 172 */       super(null, Launcher.factory);
/* 173 */       SharedSecrets.getJavaNetAccess()
/* 174 */         .getURLClassPath(this).initLookupCache(this);
/*     */     }
/*     */     
/*     */     private static File[] getExtDirs() {
/* 178 */       String str = System.getProperty("java.ext.dirs");
/*     */       File[] arrayOfFile;
/* 180 */       if (str != null) {
/* 181 */         StringTokenizer localStringTokenizer = new StringTokenizer(str, File.pathSeparator);
/*     */         
/* 183 */         int i = localStringTokenizer.countTokens();
/* 184 */         arrayOfFile = new File[i];
/* 185 */         for (int j = 0; j < i; j++) {
/* 186 */           arrayOfFile[j] = new File(localStringTokenizer.nextToken());
/*     */         }
/*     */       } else {
/* 189 */         arrayOfFile = new File[0];
/*     */       }
/* 191 */       return arrayOfFile;
/*     */     }
/*     */     
/*     */     private static URL[] getExtURLs(File[] paramArrayOfFile) throws IOException {
/* 195 */       Vector localVector = new Vector();
/* 196 */       for (int i = 0; i < paramArrayOfFile.length; i++) {
/* 197 */         String[] arrayOfString = paramArrayOfFile[i].list();
/* 198 */         if (arrayOfString != null) {
/* 199 */           for (int j = 0; j < arrayOfString.length; j++) {
/* 200 */             if (!arrayOfString[j].equals("meta-index")) {
/* 201 */               File localFile = new File(paramArrayOfFile[i], arrayOfString[j]);
/* 202 */               localVector.add(Launcher.getFileURL(localFile));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 207 */       URL[] arrayOfURL = new URL[localVector.size()];
/* 208 */       localVector.copyInto(arrayOfURL);
/* 209 */       return arrayOfURL;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String findLibrary(String paramString)
/*     */     {
/* 220 */       paramString = System.mapLibraryName(paramString);
/* 221 */       URL[] arrayOfURL = super.getURLs();
/* 222 */       Object localObject = null;
/* 223 */       for (int i = 0; i < arrayOfURL.length; i++)
/*     */       {
/*     */         URI localURI;
/*     */         try
/*     */         {
/* 228 */           localURI = arrayOfURL[i].toURI();
/*     */         }
/*     */         catch (URISyntaxException localURISyntaxException)
/*     */         {
/*     */           continue;
/*     */         }
/*     */         
/* 235 */         File localFile1 = Paths.get(localURI).toFile().getParentFile();
/* 236 */         if ((localFile1 != null) && (!localFile1.equals(localObject)))
/*     */         {
/*     */ 
/* 239 */           String str = VM.getSavedProperty("os.arch");
/* 240 */           if (str != null) {
/* 241 */             localFile2 = new File(new File(localFile1, str), paramString);
/* 242 */             if (localFile2.exists()) {
/* 243 */               return localFile2.getAbsolutePath();
/*     */             }
/*     */           }
/*     */           
/* 247 */           File localFile2 = new File(localFile1, paramString);
/* 248 */           if (localFile2.exists()) {
/* 249 */             return localFile2.getAbsolutePath();
/*     */           }
/*     */         }
/* 252 */         localObject = localFile1;
/*     */       }
/* 254 */       return null;
/*     */     }
/*     */     
/*     */     private static AccessControlContext getContext(File[] paramArrayOfFile)
/*     */       throws IOException
/*     */     {
/* 260 */       PathPermissions localPathPermissions = new PathPermissions(paramArrayOfFile);
/*     */       
/*     */ 
/*     */ 
/* 264 */       ProtectionDomain localProtectionDomain = new ProtectionDomain(new CodeSource(localPathPermissions.getCodeBase(), (Certificate[])null), localPathPermissions);
/*     */       
/*     */ 
/*     */ 
/* 268 */       AccessControlContext localAccessControlContext = new AccessControlContext(new ProtectionDomain[] { localProtectionDomain });
/*     */       
/*     */ 
/* 271 */       return localAccessControlContext;
/*     */     }
/*     */   }
/*     */   
/*     */   static class AppClassLoader
/*     */     extends URLClassLoader
/*     */   {
/*     */     final URLClassPath ucp;
/*     */     
/*     */     static
/*     */     {
/* 282 */       ClassLoader.registerAsParallelCapable();
/*     */     }
/*     */     
/*     */     public static ClassLoader getAppClassLoader(final ClassLoader paramClassLoader)
/*     */       throws IOException
/*     */     {
/* 288 */       String str = System.getProperty("java.class.path");
/* 289 */       final File[] arrayOfFile = str == null ? new File[0] : Launcher.getClassPath(str);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 298 */       (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public AppClassLoader run()
/*     */         {
/* 302 */           URL[] arrayOfURL = this.val$s == null ? new URL[0] : Launcher.pathToURLs(arrayOfFile);
/* 303 */           return new AppClassLoader(arrayOfURL, paramClassLoader);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     AppClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
/*     */     {
/* 314 */       super(paramClassLoader, Launcher.factory);
/* 315 */       this.ucp = SharedSecrets.getJavaNetAccess().getURLClassPath(this);
/* 316 */       this.ucp.initLookupCache(this);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Class<?> loadClass(String paramString, boolean paramBoolean)
/*     */       throws ClassNotFoundException
/*     */     {
/* 325 */       int i = paramString.lastIndexOf('.');
/* 326 */       Object localObject; if (i != -1) {
/* 327 */         localObject = System.getSecurityManager();
/* 328 */         if (localObject != null) {
/* 329 */           ((SecurityManager)localObject).checkPackageAccess(paramString.substring(0, i));
/*     */         }
/*     */       }
/*     */       
/* 333 */       if (this.ucp.knownToNotExist(paramString))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 339 */         localObject = findLoadedClass(paramString);
/* 340 */         if (localObject != null) {
/* 341 */           if (paramBoolean) {
/* 342 */             resolveClass((Class)localObject);
/*     */           }
/* 344 */           return (Class<?>)localObject;
/*     */         }
/* 346 */         throw new ClassNotFoundException(paramString);
/*     */       }
/*     */       
/* 349 */       return super.loadClass(paramString, paramBoolean);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected PermissionCollection getPermissions(CodeSource paramCodeSource)
/*     */     {
/* 357 */       PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
/* 358 */       localPermissionCollection.add(new RuntimePermission("exitVM"));
/* 359 */       return localPermissionCollection;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void appendToClassPathForInstrumentation(String paramString)
/*     */     {
/* 369 */       assert (Thread.holdsLock(this));
/*     */       
/*     */ 
/* 372 */       super.addURL(Launcher.getFileURL(new File(paramString)));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static AccessControlContext getContext(File[] paramArrayOfFile)
/*     */       throws MalformedURLException
/*     */     {
/* 385 */       PathPermissions localPathPermissions = new PathPermissions(paramArrayOfFile);
/*     */       
/*     */ 
/*     */ 
/* 389 */       ProtectionDomain localProtectionDomain = new ProtectionDomain(new CodeSource(localPathPermissions.getCodeBase(), (Certificate[])null), localPathPermissions);
/*     */       
/*     */ 
/*     */ 
/* 393 */       AccessControlContext localAccessControlContext = new AccessControlContext(new ProtectionDomain[] { localProtectionDomain });
/*     */       
/*     */ 
/* 396 */       return localAccessControlContext;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class BootClassPathHolder {
/*     */     static final URLClassPath bcp;
/*     */     
/*     */     static { URL[] arrayOfURL;
/* 404 */       if (Launcher.bootClassPath != null) {
/* 405 */         arrayOfURL = (URL[])AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public URL[] run() {
/* 408 */             File[] arrayOfFile = Launcher.getClassPath(Launcher.bootClassPath);
/* 409 */             int i = arrayOfFile.length;
/* 410 */             HashSet localHashSet = new HashSet();
/* 411 */             for (int j = 0; j < i; j++) {
/* 412 */               File localFile = arrayOfFile[j];
/*     */               
/*     */ 
/* 415 */               if (!localFile.isDirectory()) {
/* 416 */                 localFile = localFile.getParentFile();
/*     */               }
/* 418 */               if ((localFile != null) && (localHashSet.add(localFile))) {
/* 419 */                 MetaIndex.registerDirectory(localFile);
/*     */               }
/*     */             }
/* 422 */             return Launcher.pathToURLs(arrayOfFile);
/*     */           }
/*     */           
/*     */         });
/*     */       } else {
/* 427 */         arrayOfURL = new URL[0];
/*     */       }
/* 429 */       bcp = new URLClassPath(arrayOfURL, Launcher.factory, null);
/* 430 */       bcp.initLookupCache(null);
/*     */     }
/*     */   }
/*     */   
/*     */   public static URLClassPath getBootstrapClassPath() {
/* 435 */     return BootClassPathHolder.bcp;
/*     */   }
/*     */   
/*     */   private static URL[] pathToURLs(File[] paramArrayOfFile) {
/* 439 */     URL[] arrayOfURL = new URL[paramArrayOfFile.length];
/* 440 */     for (int i = 0; i < paramArrayOfFile.length; i++) {
/* 441 */       arrayOfURL[i] = getFileURL(paramArrayOfFile[i]);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 447 */     return arrayOfURL;
/*     */   }
/*     */   
/*     */   private static File[] getClassPath(String paramString) {
/*     */     Object localObject;
/* 452 */     if (paramString != null) {
/* 453 */       int i = 0;int j = 1;
/* 454 */       int k = 0;int m = 0;
/*     */       
/* 456 */       while ((k = paramString.indexOf(File.pathSeparator, m)) != -1) {
/* 457 */         j++;
/* 458 */         m = k + 1;
/*     */       }
/* 460 */       localObject = new File[j];
/* 461 */       m = k = 0;
/*     */       
/* 463 */       while ((k = paramString.indexOf(File.pathSeparator, m)) != -1) {
/* 464 */         if (k - m > 0) {
/* 465 */           localObject[(i++)] = new File(paramString.substring(m, k));
/*     */         }
/*     */         else {
/* 468 */           localObject[(i++)] = new File(".");
/*     */         }
/* 470 */         m = k + 1;
/*     */       }
/*     */       
/* 473 */       if (m < paramString.length()) {
/* 474 */         localObject[(i++)] = new File(paramString.substring(m));
/*     */       } else {
/* 476 */         localObject[(i++)] = new File(".");
/*     */       }
/*     */       
/* 479 */       if (i != j) {
/* 480 */         File[] arrayOfFile = new File[i];
/* 481 */         System.arraycopy(localObject, 0, arrayOfFile, 0, i);
/* 482 */         localObject = arrayOfFile;
/*     */       }
/*     */     } else {
/* 485 */       localObject = new File[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 491 */     return (File[])localObject;
/*     */   }
/*     */   
/*     */   static URL getFileURL(File paramFile)
/*     */   {
/*     */     try
/*     */     {
/* 498 */       paramFile = paramFile.getCanonicalFile();
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */     try {
/* 502 */       return ParseUtil.fileToEncodedURL(paramFile);
/*     */     }
/*     */     catch (MalformedURLException localMalformedURLException) {
/* 505 */       throw new InternalError(localMalformedURLException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class Factory
/*     */     implements URLStreamHandlerFactory
/*     */   {
/* 513 */     private static String PREFIX = "sun.net.www.protocol";
/*     */     
/*     */     public URLStreamHandler createURLStreamHandler(String paramString) {
/* 516 */       String str = PREFIX + "." + paramString + ".Handler";
/*     */       try {
/* 518 */         Class localClass = Class.forName(str);
/* 519 */         return (URLStreamHandler)localClass.newInstance();
/*     */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 521 */         throw new InternalError("could not load " + paramString + "system protocol handler", localReflectiveOperationException);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\Launcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
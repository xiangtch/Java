/*      */ package sun.misc;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.JarURLConnection;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.SocketPermission;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.net.URLStreamHandler;
/*      */ import java.net.URLStreamHandlerFactory;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessControlException;
/*      */ import java.security.AccessController;
/*      */ import java.security.CodeSigner;
/*      */ import java.security.Permission;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.cert.Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.jar.Attributes;
/*      */ import java.util.jar.Attributes.Name;
/*      */ import java.util.jar.JarEntry;
/*      */ import java.util.jar.JarFile;
/*      */ import java.util.jar.Manifest;
/*      */ import java.util.zip.ZipEntry;
/*      */ import sun.net.util.URLUtil;
/*      */ import sun.net.www.ParseUtil;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class URLClassPath
/*      */ {
/*      */   static final String USER_AGENT_JAVA_VERSION = "UA-Java-Version";
/*   74 */   static final String JAVA_VERSION = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
/*      */   
/*   76 */   private static final boolean DEBUG = AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.debug")) != null;
/*      */   
/*   78 */   private static final boolean DEBUG_LOOKUP_CACHE = AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.debugLookupCache")) != null;
/*      */   
/*   80 */   static { String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.misc.URLClassPath.disableJarChecking"));
/*      */     
/*   82 */     DISABLE_JAR_CHECKING = (str.equals("true")) || (str.equals(""));
/*      */     
/*   84 */     str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.net.URLClassPath.disableRestrictedPermissions"));
/*      */     
/*   86 */     DISABLE_ACC_CHECKING = (str.equals("true")) || (str.equals("")); }
/*      */   
/*      */   private static final boolean DISABLE_JAR_CHECKING;
/*      */   private static final boolean DISABLE_ACC_CHECKING;
/*   90 */   private ArrayList<URL> path = new ArrayList();
/*      */   
/*      */ 
/*   93 */   Stack<URL> urls = new Stack();
/*      */   
/*      */ 
/*   96 */   ArrayList<Loader> loaders = new ArrayList();
/*      */   
/*      */ 
/*   99 */   HashMap<String, Loader> lmap = new HashMap();
/*      */   
/*      */ 
/*      */   private URLStreamHandler jarHandler;
/*      */   
/*      */ 
/*  105 */   private boolean closed = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final AccessControlContext acc;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public URLClassPath(URL[] paramArrayOfURL, URLStreamHandlerFactory paramURLStreamHandlerFactory, AccessControlContext paramAccessControlContext)
/*      */   {
/*  127 */     for (int i = 0; i < paramArrayOfURL.length; i++) {
/*  128 */       this.path.add(paramArrayOfURL[i]);
/*      */     }
/*  130 */     push(paramArrayOfURL);
/*  131 */     if (paramURLStreamHandlerFactory != null) {
/*  132 */       this.jarHandler = paramURLStreamHandlerFactory.createURLStreamHandler("jar");
/*      */     }
/*  134 */     if (DISABLE_ACC_CHECKING) {
/*  135 */       this.acc = null;
/*      */     } else {
/*  137 */       this.acc = paramAccessControlContext;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public URLClassPath(URL[] paramArrayOfURL)
/*      */   {
/*  145 */     this(paramArrayOfURL, null, null);
/*      */   }
/*      */   
/*      */   public URLClassPath(URL[] paramArrayOfURL, AccessControlContext paramAccessControlContext) {
/*  149 */     this(paramArrayOfURL, null, paramAccessControlContext);
/*      */   }
/*      */   
/*      */   public synchronized List<IOException> closeLoaders() {
/*  153 */     if (this.closed) {
/*  154 */       return Collections.emptyList();
/*      */     }
/*  156 */     LinkedList localLinkedList = new LinkedList();
/*  157 */     for (Loader localLoader : this.loaders) {
/*      */       try {
/*  159 */         localLoader.close();
/*      */       } catch (IOException localIOException) {
/*  161 */         localLinkedList.add(localIOException);
/*      */       }
/*      */     }
/*  164 */     this.closed = true;
/*  165 */     return localLinkedList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void addURL(URL paramURL)
/*      */   {
/*  176 */     if (this.closed)
/*  177 */       return;
/*  178 */     synchronized (this.urls) {
/*  179 */       if ((paramURL == null) || (this.path.contains(paramURL))) {
/*  180 */         return;
/*      */       }
/*  182 */       this.urls.add(0, paramURL);
/*  183 */       this.path.add(paramURL);
/*      */       
/*  185 */       if (this.lookupCacheURLs != null)
/*      */       {
/*      */ 
/*  188 */         disableAllLookupCaches();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public URL findResource(String paramString, boolean paramBoolean)
/*      */   {
/*  213 */     int[] arrayOfInt = getLookupCache(paramString);
/*  214 */     Loader localLoader; for (int i = 0; (localLoader = getNextLoader(arrayOfInt, i)) != null; i++) {
/*  215 */       URL localURL = localLoader.findResource(paramString, paramBoolean);
/*  216 */       if (localURL != null) {
/*  217 */         return localURL;
/*      */       }
/*      */     }
/*  220 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Resource getResource(String paramString, boolean paramBoolean)
/*      */   {
/*  232 */     if (DEBUG) {
/*  233 */       System.err.println("URLClassPath.getResource(\"" + paramString + "\")");
/*      */     }
/*      */     
/*      */ 
/*  237 */     int[] arrayOfInt = getLookupCache(paramString);
/*  238 */     Loader localLoader; for (int i = 0; (localLoader = getNextLoader(arrayOfInt, i)) != null; i++) {
/*  239 */       Resource localResource = localLoader.getResource(paramString, paramBoolean);
/*  240 */       if (localResource != null) {
/*  241 */         return localResource;
/*      */       }
/*      */     }
/*  244 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Enumeration<URL> findResources(final String paramString, final boolean paramBoolean)
/*      */   {
/*  256 */     new Enumeration() {
/*  257 */       private int index = 0;
/*  258 */       private int[] cache = URLClassPath.this.getLookupCache(paramString);
/*  259 */       private URL url = null;
/*      */       
/*      */       private boolean next() {
/*  262 */         if (this.url != null) {
/*  263 */           return true;
/*      */         }
/*      */         Loader localLoader;
/*  266 */         while ((localLoader = URLClassPath.this.getNextLoader(this.cache, this.index++)) != null) {
/*  267 */           this.url = localLoader.findResource(paramString, paramBoolean);
/*  268 */           if (this.url != null) {
/*  269 */             return true;
/*      */           }
/*      */         }
/*  272 */         return false;
/*      */       }
/*      */       
/*      */       public boolean hasMoreElements()
/*      */       {
/*  277 */         return next();
/*      */       }
/*      */       
/*      */       public URL nextElement() {
/*  281 */         if (!next()) {
/*  282 */           throw new NoSuchElementException();
/*      */         }
/*  284 */         URL localURL = this.url;
/*  285 */         this.url = null;
/*  286 */         return localURL;
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */   public Resource getResource(String paramString) {
/*  292 */     return getResource(paramString, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Enumeration<Resource> getResources(final String paramString, final boolean paramBoolean)
/*      */   {
/*  304 */     new Enumeration() {
/*  305 */       private int index = 0;
/*  306 */       private int[] cache = URLClassPath.this.getLookupCache(paramString);
/*  307 */       private Resource res = null;
/*      */       
/*      */       private boolean next() {
/*  310 */         if (this.res != null) {
/*  311 */           return true;
/*      */         }
/*      */         Loader localLoader;
/*  314 */         while ((localLoader = URLClassPath.this.getNextLoader(this.cache, this.index++)) != null) {
/*  315 */           this.res = localLoader.getResource(paramString, paramBoolean);
/*  316 */           if (this.res != null) {
/*  317 */             return true;
/*      */           }
/*      */         }
/*  320 */         return false;
/*      */       }
/*      */       
/*      */       public boolean hasMoreElements()
/*      */       {
/*  325 */         return next();
/*      */       }
/*      */       
/*      */       public Resource nextElement() {
/*  329 */         if (!next()) {
/*  330 */           throw new NoSuchElementException();
/*      */         }
/*  332 */         Resource localResource = this.res;
/*  333 */         this.res = null;
/*  334 */         return localResource;
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */   public Enumeration<Resource> getResources(String paramString) {
/*  340 */     return getResources(paramString, true);
/*      */   }
/*      */   
/*  343 */   private static volatile boolean lookupCacheEnabled = "true"
/*  344 */     .equals(VM.getSavedProperty("sun.cds.enableSharedLookupCache"));
/*      */   private URL[] lookupCacheURLs;
/*      */   private ClassLoader lookupCacheLoader;
/*      */   
/*      */   synchronized void initLookupCache(ClassLoader paramClassLoader) {
/*  349 */     if ((this.lookupCacheURLs = getLookupCacheURLs(paramClassLoader)) != null) {
/*  350 */       this.lookupCacheLoader = paramClassLoader;
/*      */     }
/*      */     else {
/*  353 */       disableAllLookupCaches();
/*      */     }
/*      */   }
/*      */   
/*      */   static void disableAllLookupCaches() {
/*  358 */     lookupCacheEnabled = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   synchronized boolean knownToNotExist(String paramString)
/*      */   {
/*  368 */     if ((this.lookupCacheURLs != null) && (lookupCacheEnabled)) {
/*  369 */       return knownToNotExist0(this.lookupCacheLoader, paramString);
/*      */     }
/*      */     
/*      */ 
/*  373 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private synchronized int[] getLookupCache(String paramString)
/*      */   {
/*  396 */     if ((this.lookupCacheURLs == null) || (!lookupCacheEnabled)) {
/*  397 */       return null;
/*      */     }
/*      */     
/*  400 */     int[] arrayOfInt = getLookupCacheForClassLoader(this.lookupCacheLoader, paramString);
/*  401 */     if ((arrayOfInt != null) && (arrayOfInt.length > 0)) {
/*  402 */       int i = arrayOfInt[(arrayOfInt.length - 1)];
/*  403 */       if (!ensureLoaderOpened(i)) {
/*  404 */         if (DEBUG_LOOKUP_CACHE) {
/*  405 */           System.out.println("Expanded loaders FAILED " + this.loaders
/*  406 */             .size() + " for maxindex=" + i);
/*      */         }
/*  408 */         return null;
/*      */       }
/*      */     }
/*      */     
/*  412 */     return arrayOfInt;
/*      */   }
/*      */   
/*      */   private boolean ensureLoaderOpened(int paramInt) {
/*  416 */     if (this.loaders.size() <= paramInt)
/*      */     {
/*  418 */       if (getLoader(paramInt) == null) {
/*  419 */         return false;
/*      */       }
/*  421 */       if (!lookupCacheEnabled)
/*      */       {
/*  423 */         return false;
/*      */       }
/*  425 */       if (DEBUG_LOOKUP_CACHE) {
/*  426 */         System.out.println("Expanded loaders " + this.loaders.size() + " to index=" + paramInt);
/*      */       }
/*      */     }
/*      */     
/*  430 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private synchronized void validateLookupCache(int paramInt, String paramString)
/*      */   {
/*  442 */     if ((this.lookupCacheURLs != null) && (lookupCacheEnabled)) {
/*  443 */       if ((paramInt < this.lookupCacheURLs.length) && 
/*  444 */         (paramString.equals(
/*  445 */         URLUtil.urlNoFragString(this.lookupCacheURLs[paramInt])))) {
/*  446 */         return;
/*      */       }
/*  448 */       if ((DEBUG) || (DEBUG_LOOKUP_CACHE)) {
/*  449 */         System.out.println("WARNING: resource lookup cache invalidated for lookupCacheLoader at " + paramInt);
/*      */       }
/*      */       
/*  452 */       disableAllLookupCaches();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private synchronized Loader getNextLoader(int[] paramArrayOfInt, int paramInt)
/*      */   {
/*  469 */     if (this.closed) {
/*  470 */       return null;
/*      */     }
/*  472 */     if (paramArrayOfInt != null) {
/*  473 */       if (paramInt < paramArrayOfInt.length) {
/*  474 */         Loader localLoader = (Loader)this.loaders.get(paramArrayOfInt[paramInt]);
/*  475 */         if (DEBUG_LOOKUP_CACHE) {
/*  476 */           System.out.println("HASCACHE: Loading from : " + paramArrayOfInt[paramInt] + " = " + localLoader
/*  477 */             .getBaseURL());
/*      */         }
/*  479 */         return localLoader;
/*      */       }
/*  481 */       return null;
/*      */     }
/*      */     
/*  484 */     return getLoader(paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private synchronized Loader getLoader(int paramInt)
/*      */   {
/*  494 */     if (this.closed) {
/*  495 */       return null;
/*      */     }
/*      */     
/*      */ 
/*  499 */     while (this.loaders.size() < paramInt + 1)
/*      */     {
/*      */       URL localURL;
/*  502 */       synchronized (this.urls) {
/*  503 */         if (this.urls.empty()) {
/*  504 */           return null;
/*      */         }
/*  506 */         localURL = (URL)this.urls.pop();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  512 */       ??? = URLUtil.urlNoFragString(localURL);
/*  513 */       if (!this.lmap.containsKey(???))
/*      */       {
/*      */         Loader localLoader;
/*      */         
/*      */         try
/*      */         {
/*  519 */           localLoader = getLoader(localURL);
/*      */           
/*      */ 
/*  522 */           URL[] arrayOfURL = localLoader.getClassPath();
/*  523 */           if (arrayOfURL != null) {
/*  524 */             push(arrayOfURL);
/*      */           }
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*      */           continue;
/*      */         }
/*      */         catch (SecurityException localSecurityException)
/*      */         {
/*  533 */           if (DEBUG)
/*  534 */             System.err.println("Failed to access " + localURL + ", " + localSecurityException);
/*      */         }
/*  536 */         continue;
/*      */         
/*      */ 
/*  539 */         validateLookupCache(this.loaders.size(), (String)???);
/*  540 */         this.loaders.add(localLoader);
/*  541 */         this.lmap.put(???, localLoader);
/*      */       } }
/*  543 */     if (DEBUG_LOOKUP_CACHE) {
/*  544 */       System.out.println("NOCACHE: Loading from : " + paramInt);
/*      */     }
/*  546 */     return (Loader)this.loaders.get(paramInt);
/*      */   }
/*      */   
/*      */   private Loader getLoader(final URL paramURL)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  554 */       (Loader)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Loader run() throws IOException {
/*  557 */           String str = paramURL.getFile();
/*  558 */           if ((str != null) && (str.endsWith("/"))) {
/*  559 */             if ("file".equals(paramURL.getProtocol())) {
/*  560 */               return new FileLoader(paramURL);
/*      */             }
/*  562 */             return new Loader(paramURL);
/*      */           }
/*      */           
/*  565 */           return new JarLoader(paramURL, URLClassPath.this.jarHandler, URLClassPath.this.lmap, URLClassPath.this.acc); } }, this.acc);
/*      */ 
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  570 */       throw ((IOException)localPrivilegedActionException.getException());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void push(URL[] paramArrayOfURL)
/*      */   {
/*  578 */     synchronized (this.urls) {
/*  579 */       for (int i = paramArrayOfURL.length - 1; i >= 0; i--) {
/*  580 */         this.urls.push(paramArrayOfURL[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static URL[] pathToURLs(String paramString)
/*      */   {
/*  592 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
/*  593 */     Object localObject1 = new URL[localStringTokenizer.countTokens()];
/*  594 */     int i = 0;
/*  595 */     Object localObject2; while (localStringTokenizer.hasMoreTokens()) {
/*  596 */       localObject2 = new File(localStringTokenizer.nextToken());
/*      */       try {
/*  598 */         localObject2 = new File(((File)localObject2).getCanonicalPath());
/*      */       }
/*      */       catch (IOException localIOException1) {}
/*      */       try
/*      */       {
/*  603 */         localObject1[(i++)] = ParseUtil.fileToEncodedURL((File)localObject2);
/*      */       }
/*      */       catch (IOException localIOException2) {}
/*      */     }
/*  607 */     if (localObject1.length != i) {
/*  608 */       localObject2 = new URL[i];
/*  609 */       System.arraycopy(localObject1, 0, localObject2, 0, i);
/*  610 */       localObject1 = localObject2;
/*      */     }
/*  612 */     return (URL[])localObject1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public URL checkURL(URL paramURL)
/*      */   {
/*      */     try
/*      */     {
/*  622 */       check(paramURL);
/*      */     } catch (Exception localException) {
/*  624 */       return null;
/*      */     }
/*      */     
/*  627 */     return paramURL;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static void check(URL paramURL)
/*      */     throws IOException
/*      */   {
/*  636 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  637 */     if (localSecurityManager != null) {
/*  638 */       URLConnection localURLConnection = paramURL.openConnection();
/*  639 */       Permission localPermission = localURLConnection.getPermission();
/*  640 */       if (localPermission != null) {
/*      */         try {
/*  642 */           localSecurityManager.checkPermission(localPermission);
/*      */         }
/*      */         catch (SecurityException localSecurityException)
/*      */         {
/*  646 */           if (((localPermission instanceof FilePermission)) && 
/*  647 */             (localPermission.getActions().indexOf("read") != -1)) {
/*  648 */             localSecurityManager.checkRead(localPermission.getName());
/*  649 */           } else { if ((localPermission instanceof SocketPermission))
/*      */             {
/*  651 */               if (localPermission.getActions().indexOf("connect") != -1) {
/*  652 */                 URL localURL = paramURL;
/*  653 */                 if ((localURLConnection instanceof JarURLConnection)) {
/*  654 */                   localURL = ((JarURLConnection)localURLConnection).getJarFileURL();
/*      */                 }
/*  656 */                 localSecurityManager.checkConnect(localURL.getHost(), localURL
/*  657 */                   .getPort());
/*  658 */                 return; } }
/*  659 */             throw localSecurityException;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public URL[] getURLs()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 416	sun/misc/URLClassPath:urls	Ljava/util/Stack;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 414	sun/misc/URLClassPath:path	Ljava/util/ArrayList;
/*      */     //   11: aload_0
/*      */     //   12: getfield 414	sun/misc/URLClassPath:path	Ljava/util/ArrayList;
/*      */     //   15: invokevirtual 443	java/util/ArrayList:size	()I
/*      */     //   18: anewarray 223	java/net/URL
/*      */     //   21: invokevirtual 449	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
/*      */     //   24: checkcast 208	[Ljava/net/URL;
/*      */     //   27: aload_1
/*      */     //   28: monitorexit
/*      */     //   29: areturn
/*      */     //   30: astore_2
/*      */     //   31: aload_1
/*      */     //   32: monitorexit
/*      */     //   33: aload_2
/*      */     //   34: athrow
/*      */     // Line number table:
/*      */     //   Java source line #197	-> byte code offset #0
/*      */     //   Java source line #198	-> byte code offset #7
/*      */     //   Java source line #199	-> byte code offset #30
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	35	0	this	URLClassPath
/*      */     //   5	27	1	Ljava/lang/Object;	Object
/*      */     //   30	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	29	30	finally
/*      */     //   30	33	30	finally
/*      */   }
/*      */   
/*      */   private static native URL[] getLookupCacheURLs(ClassLoader paramClassLoader);
/*      */   
/*      */   private static native int[] getLookupCacheForClassLoader(ClassLoader paramClassLoader, String paramString);
/*      */   
/*      */   private static native boolean knownToNotExist0(ClassLoader paramClassLoader, String paramString);
/*      */   
/*      */   private static class Loader
/*      */     implements Closeable
/*      */   {
/*      */     private final URL base;
/*      */     private JarFile jarfile;
/*      */     
/*      */     Loader(URL paramURL)
/*      */     {
/*  678 */       this.base = paramURL;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     URL getBaseURL()
/*      */     {
/*  685 */       return this.base;
/*      */     }
/*      */     
/*      */     URL findResource(String paramString, boolean paramBoolean) {
/*      */       URL localURL;
/*      */       try {
/*  691 */         localURL = new URL(this.base, ParseUtil.encodePath(paramString, false));
/*      */       } catch (MalformedURLException localMalformedURLException) {
/*  693 */         throw new IllegalArgumentException("name");
/*      */       }
/*      */       try
/*      */       {
/*  697 */         if (paramBoolean) {
/*  698 */           URLClassPath.check(localURL);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  705 */         URLConnection localURLConnection = localURL.openConnection();
/*  706 */         Object localObject; if ((localURLConnection instanceof HttpURLConnection)) {
/*  707 */           localObject = (HttpURLConnection)localURLConnection;
/*  708 */           ((HttpURLConnection)localObject).setRequestMethod("HEAD");
/*  709 */           if (((HttpURLConnection)localObject).getResponseCode() >= 400) {
/*  710 */             return null;
/*      */           }
/*      */         }
/*      */         else {
/*  714 */           localURLConnection.setUseCaches(false);
/*  715 */           localObject = localURLConnection.getInputStream();
/*  716 */           ((InputStream)localObject).close();
/*      */         }
/*  718 */         return localURL;
/*      */       } catch (Exception localException) {}
/*  720 */       return null;
/*      */     }
/*      */     
/*      */     Resource getResource(final String paramString, boolean paramBoolean)
/*      */     {
/*      */       final URL localURL;
/*      */       try {
/*  727 */         localURL = new URL(this.base, ParseUtil.encodePath(paramString, false));
/*      */       } catch (MalformedURLException localMalformedURLException) {
/*  729 */         throw new IllegalArgumentException("name");
/*      */       }
/*      */       final URLConnection localURLConnection;
/*      */       try {
/*  733 */         if (paramBoolean) {
/*  734 */           URLClassPath.check(localURL);
/*      */         }
/*  736 */         localURLConnection = localURL.openConnection();
/*  737 */         InputStream localInputStream = localURLConnection.getInputStream();
/*  738 */         if ((localURLConnection instanceof JarURLConnection))
/*      */         {
/*      */ 
/*      */ 
/*  742 */           JarURLConnection localJarURLConnection = (JarURLConnection)localURLConnection;
/*  743 */           this.jarfile = JarLoader.checkJar(localJarURLConnection.getJarFile());
/*      */         }
/*      */       } catch (Exception localException) {
/*  746 */         return null;
/*      */       }
/*  748 */       new Resource() {
/*  749 */         public String getName() { return paramString; }
/*  750 */         public URL getURL() { return localURL; }
/*  751 */         public URL getCodeSourceURL() { return Loader.this.base; }
/*      */         
/*  753 */         public InputStream getInputStream() throws IOException { return localURLConnection.getInputStream(); }
/*      */         
/*      */         public int getContentLength() throws IOException {
/*  756 */           return localURLConnection.getContentLength();
/*      */         }
/*      */       };
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Resource getResource(String paramString)
/*      */     {
/*  767 */       return getResource(paramString, true);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void close()
/*      */       throws IOException
/*      */     {
/*  775 */       if (this.jarfile != null) {
/*  776 */         this.jarfile.close();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     URL[] getClassPath()
/*      */       throws IOException
/*      */     {
/*  784 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class JarLoader
/*      */     extends Loader
/*      */   {
/*      */     private JarFile jar;
/*      */     private final URL csu;
/*      */     private JarIndex index;
/*      */     private MetaIndex metaIndex;
/*      */     private URLStreamHandler handler;
/*      */     private final HashMap<String, Loader> lmap;
/*      */     private final AccessControlContext acc;
/*  799 */     private boolean closed = false;
/*      */     
/*  801 */     private static final JavaUtilZipFileAccess zipAccess = ;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     JarLoader(URL paramURL, URLStreamHandler paramURLStreamHandler, HashMap<String, Loader> paramHashMap, AccessControlContext paramAccessControlContext)
/*      */       throws IOException
/*      */     {
/*  812 */       super();
/*  813 */       this.csu = paramURL;
/*  814 */       this.handler = paramURLStreamHandler;
/*  815 */       this.lmap = paramHashMap;
/*  816 */       this.acc = paramAccessControlContext;
/*      */       
/*  818 */       if (!isOptimizable(paramURL)) {
/*  819 */         ensureOpen();
/*      */       } else {
/*  821 */         String str = paramURL.getFile();
/*  822 */         if (str != null) {
/*  823 */           str = ParseUtil.decode(str);
/*  824 */           File localFile = new File(str);
/*  825 */           this.metaIndex = MetaIndex.forJar(localFile);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  832 */           if ((this.metaIndex != null) && (!localFile.exists())) {
/*  833 */             this.metaIndex = null;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  840 */         if (this.metaIndex == null) {
/*  841 */           ensureOpen();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public void close()
/*      */       throws IOException
/*      */     {
/*  849 */       if (!this.closed) {
/*  850 */         this.closed = true;
/*      */         
/*  852 */         ensureOpen();
/*  853 */         this.jar.close();
/*      */       }
/*      */     }
/*      */     
/*      */     JarFile getJarFile() {
/*  858 */       return this.jar;
/*      */     }
/*      */     
/*      */     private boolean isOptimizable(URL paramURL) {
/*  862 */       return "file".equals(paramURL.getProtocol());
/*      */     }
/*      */     
/*      */     private void ensureOpen() throws IOException {
/*  866 */       if (this.jar == null) {
/*      */         try {
/*  868 */           AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */           {
/*      */             public Void run() throws IOException {
/*  871 */               if (URLClassPath.DEBUG) {
/*  872 */                 System.err.println("Opening " + JarLoader.this.csu);
/*  873 */                 Thread.dumpStack();
/*      */               }
/*      */               
/*  876 */               JarLoader.this.jar = JarLoader.this.getJarFile(JarLoader.this.csu);
/*  877 */               JarLoader.this.index = JarIndex.getJarIndex(JarLoader.this.jar, JarLoader.this.metaIndex);
/*  878 */               if (JarLoader.this.index != null) {
/*  879 */                 String[] arrayOfString = JarLoader.this.index.getJarFiles();
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  885 */                 for (int i = 0; i < arrayOfString.length; i++) {
/*      */                   try {
/*  887 */                     URL localURL = new URL(JarLoader.this.csu, arrayOfString[i]);
/*      */                     
/*  889 */                     String str = URLUtil.urlNoFragString(localURL);
/*  890 */                     if (!JarLoader.this.lmap.containsKey(str)) {
/*  891 */                       JarLoader.this.lmap.put(str, null);
/*      */                     }
/*      */                   }
/*      */                   catch (MalformedURLException localMalformedURLException) {}
/*      */                 }
/*      */               }
/*      */               
/*  898 */               return null; } }, this.acc);
/*      */         }
/*      */         catch (PrivilegedActionException localPrivilegedActionException)
/*      */         {
/*  902 */           throw ((IOException)localPrivilegedActionException.getException());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     static JarFile checkJar(JarFile paramJarFile) throws IOException
/*      */     {
/*  909 */       if ((System.getSecurityManager() != null) && (!URLClassPath.DISABLE_JAR_CHECKING) && 
/*  910 */         (!zipAccess.startsWithLocHeader(paramJarFile))) {
/*  911 */         IOException localIOException1 = new IOException("Invalid Jar file");
/*      */         try {
/*  913 */           paramJarFile.close();
/*      */         } catch (IOException localIOException2) {
/*  915 */           localIOException1.addSuppressed(localIOException2);
/*      */         }
/*  917 */         throw localIOException1;
/*      */       }
/*      */       
/*  920 */       return paramJarFile;
/*      */     }
/*      */     
/*      */     private JarFile getJarFile(URL paramURL) throws IOException
/*      */     {
/*  925 */       if (isOptimizable(paramURL)) {
/*  926 */         localObject = new FileURLMapper(paramURL);
/*  927 */         if (!((FileURLMapper)localObject).exists()) {
/*  928 */           throw new FileNotFoundException(((FileURLMapper)localObject).getPath());
/*      */         }
/*  930 */         return checkJar(new JarFile(((FileURLMapper)localObject).getPath()));
/*      */       }
/*  932 */       Object localObject = getBaseURL().openConnection();
/*  933 */       ((URLConnection)localObject).setRequestProperty("UA-Java-Version", URLClassPath.JAVA_VERSION);
/*  934 */       JarFile localJarFile = ((JarURLConnection)localObject).getJarFile();
/*  935 */       return checkJar(localJarFile);
/*      */     }
/*      */     
/*      */ 
/*      */     JarIndex getIndex()
/*      */     {
/*      */       try
/*      */       {
/*  943 */         ensureOpen();
/*      */       } catch (IOException localIOException) {
/*  945 */         throw new InternalError(localIOException);
/*      */       }
/*  947 */       return this.index;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     Resource checkResource(final String paramString, boolean paramBoolean, final JarEntry paramJarEntry)
/*      */     {
/*      */       final URL localURL;
/*      */       
/*      */ 
/*      */       try
/*      */       {
/*  959 */         localURL = new URL(getBaseURL(), ParseUtil.encodePath(paramString, false));
/*  960 */         if (paramBoolean) {
/*  961 */           URLClassPath.check(localURL);
/*      */         }
/*      */       } catch (MalformedURLException localMalformedURLException) {
/*  964 */         return null;
/*      */       }
/*      */       catch (IOException localIOException) {
/*  967 */         return null;
/*      */       } catch (AccessControlException localAccessControlException) {
/*  969 */         return null;
/*      */       }
/*      */       
/*  972 */       new Resource() {
/*  973 */         public String getName() { return paramString; }
/*  974 */         public URL getURL() { return localURL; }
/*  975 */         public URL getCodeSourceURL() { return JarLoader.this.csu; }
/*      */         
/*  977 */         public InputStream getInputStream() throws IOException { return JarLoader.this.jar.getInputStream(paramJarEntry); }
/*      */         
/*  979 */         public int getContentLength() { return (int)paramJarEntry.getSize(); }
/*      */         
/*  981 */         public Manifest getManifest() throws IOException { return JarLoader.this.jar.getManifest(); }
/*      */         
/*  983 */         public Certificate[] getCertificates() { return paramJarEntry.getCertificates(); }
/*      */         
/*  985 */         public CodeSigner[] getCodeSigners() { return paramJarEntry.getCodeSigners(); }
/*      */       };
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean validIndex(String paramString)
/*      */     {
/*  995 */       String str1 = paramString;
/*      */       int i;
/*  997 */       if ((i = paramString.lastIndexOf("/")) != -1) {
/*  998 */         str1 = paramString.substring(0, i);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1003 */       Enumeration localEnumeration = this.jar.entries();
/* 1004 */       while (localEnumeration.hasMoreElements()) {
/* 1005 */         ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
/* 1006 */         String str2 = localZipEntry.getName();
/* 1007 */         if ((i = str2.lastIndexOf("/")) != -1)
/* 1008 */           str2 = str2.substring(0, i);
/* 1009 */         if (str2.equals(str1)) {
/* 1010 */           return true;
/*      */         }
/*      */       }
/* 1013 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     URL findResource(String paramString, boolean paramBoolean)
/*      */     {
/* 1020 */       Resource localResource = getResource(paramString, paramBoolean);
/* 1021 */       if (localResource != null) {
/* 1022 */         return localResource.getURL();
/*      */       }
/* 1024 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     Resource getResource(String paramString, boolean paramBoolean)
/*      */     {
/* 1031 */       if ((this.metaIndex != null) && 
/* 1032 */         (!this.metaIndex.mayContain(paramString))) {
/* 1033 */         return null;
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 1038 */         ensureOpen();
/*      */       } catch (IOException localIOException) {
/* 1040 */         throw new InternalError(localIOException);
/*      */       }
/* 1042 */       JarEntry localJarEntry = this.jar.getJarEntry(paramString);
/* 1043 */       if (localJarEntry != null) {
/* 1044 */         return checkResource(paramString, paramBoolean, localJarEntry);
/*      */       }
/* 1046 */       if (this.index == null) {
/* 1047 */         return null;
/*      */       }
/* 1049 */       HashSet localHashSet = new HashSet();
/* 1050 */       return getResource(paramString, paramBoolean, localHashSet);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Resource getResource(String paramString, boolean paramBoolean, Set<String> paramSet)
/*      */     {
/* 1065 */       int i = 0;
/* 1066 */       LinkedList localLinkedList = null;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1071 */       if ((localLinkedList = this.index.get(paramString)) == null) {
/* 1072 */         return null;
/*      */       }
/*      */       do {
/* 1075 */         int j = localLinkedList.size();
/* 1076 */         String[] arrayOfString = (String[])localLinkedList.toArray(new String[j]);
/*      */         
/* 1078 */         while (i < j) {
/* 1079 */           String str1 = arrayOfString[(i++)];
/*      */           final URL localURL;
/*      */           JarLoader localJarLoader;
/*      */           try
/*      */           {
/* 1084 */             localURL = new URL(this.csu, str1);
/* 1085 */             String str2 = URLUtil.urlNoFragString(localURL);
/* 1086 */             if ((localJarLoader = (JarLoader)this.lmap.get(str2)) == null)
/*      */             {
/*      */ 
/*      */ 
/* 1090 */               localJarLoader = (JarLoader)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */               {
/*      */                 public JarLoader run() throws IOException {
/* 1093 */                   return new JarLoader(localURL, JarLoader.this.handler,
/* 1094 */                     JarLoader.this.lmap, JarLoader.this.acc); } }, this.acc);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1102 */               JarIndex localJarIndex = localJarLoader.getIndex();
/* 1103 */               if (localJarIndex != null) {
/* 1104 */                 int m = str1.lastIndexOf("/");
/* 1105 */                 localJarIndex.merge(this.index, m == -1 ? null : str1
/* 1106 */                   .substring(0, m + 1));
/*      */               }
/*      */               
/*      */ 
/* 1110 */               this.lmap.put(str2, localJarLoader);
/*      */             }
/*      */           } catch (PrivilegedActionException localPrivilegedActionException) {
/*      */             continue;
/*      */           } catch (MalformedURLException localMalformedURLException) {}
/* 1115 */           continue;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1122 */           int k = !paramSet.add(URLUtil.urlNoFragString(localURL)) ? 1 : 0;
/* 1123 */           if (k == 0) {
/*      */             try {
/* 1125 */               localJarLoader.ensureOpen();
/*      */             } catch (IOException localIOException) {
/* 1127 */               throw new InternalError(localIOException);
/*      */             }
/* 1129 */             JarEntry localJarEntry = localJarLoader.jar.getJarEntry(paramString);
/* 1130 */             if (localJarEntry != null) {
/* 1131 */               return localJarLoader.checkResource(paramString, paramBoolean, localJarEntry);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1138 */             if (!localJarLoader.validIndex(paramString))
/*      */             {
/* 1140 */               throw new InvalidJarIndexException("Invalid index");
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1149 */           if ((k == 0) && (localJarLoader != this) && 
/* 1150 */             (localJarLoader.getIndex() != null))
/*      */           {
/*      */             Resource localResource;
/*      */             
/*      */ 
/*      */ 
/* 1156 */             if ((localResource = localJarLoader.getResource(paramString, paramBoolean, paramSet)) != null)
/*      */             {
/* 1158 */               return localResource;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1163 */         localLinkedList = this.index.get(paramString);
/*      */ 
/*      */       }
/* 1166 */       while (i < localLinkedList.size());
/* 1167 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     URL[] getClassPath()
/*      */       throws IOException
/*      */     {
/* 1175 */       if (this.index != null) {
/* 1176 */         return null;
/*      */       }
/*      */       
/* 1179 */       if (this.metaIndex != null) {
/* 1180 */         return null;
/*      */       }
/*      */       
/* 1183 */       ensureOpen();
/* 1184 */       parseExtensionsDependencies();
/*      */       
/* 1186 */       if (SharedSecrets.javaUtilJarAccess().jarFileHasClassPathAttribute(this.jar)) {
/* 1187 */         Manifest localManifest = this.jar.getManifest();
/* 1188 */         if (localManifest != null) {
/* 1189 */           Attributes localAttributes = localManifest.getMainAttributes();
/* 1190 */           if (localAttributes != null) {
/* 1191 */             String str = localAttributes.getValue(Name.CLASS_PATH);
/* 1192 */             if (str != null) {
/* 1193 */               return parseClassPath(this.csu, str);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1198 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     private void parseExtensionsDependencies()
/*      */       throws IOException
/*      */     {
/* 1205 */       ExtensionDependency.checkExtensionsDependencies(this.jar);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private URL[] parseClassPath(URL paramURL, String paramString)
/*      */       throws MalformedURLException
/*      */     {
/* 1215 */       StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/* 1216 */       URL[] arrayOfURL = new URL[localStringTokenizer.countTokens()];
/* 1217 */       int i = 0;
/* 1218 */       while (localStringTokenizer.hasMoreTokens()) {
/* 1219 */         String str = localStringTokenizer.nextToken();
/* 1220 */         arrayOfURL[i] = new URL(paramURL, str);
/* 1221 */         i++;
/*      */       }
/* 1223 */       return arrayOfURL;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class FileLoader
/*      */     extends Loader
/*      */   {
/*      */     private File dir;
/*      */     
/*      */     FileLoader(URL paramURL)
/*      */       throws IOException
/*      */     {
/* 1236 */       super();
/* 1237 */       if (!"file".equals(paramURL.getProtocol())) {
/* 1238 */         throw new IllegalArgumentException("url");
/*      */       }
/* 1240 */       String str = paramURL.getFile().replace('/', File.separatorChar);
/* 1241 */       str = ParseUtil.decode(str);
/* 1242 */       this.dir = new File(str).getCanonicalFile();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     URL findResource(String paramString, boolean paramBoolean)
/*      */     {
/* 1249 */       Resource localResource = getResource(paramString, paramBoolean);
/* 1250 */       if (localResource != null) {
/* 1251 */         return localResource.getURL();
/*      */       }
/* 1253 */       return null;
/*      */     }
/*      */     
/*      */     Resource getResource(final String paramString, boolean paramBoolean)
/*      */     {
/*      */       try {
/* 1259 */         URL localURL2 = new URL(getBaseURL(), ".");
/* 1260 */         final URL localURL1 = new URL(getBaseURL(), ParseUtil.encodePath(paramString, false));
/*      */         
/* 1262 */         if (!localURL1.getFile().startsWith(localURL2.getFile()))
/*      */         {
/* 1264 */           return null;
/*      */         }
/*      */         
/* 1267 */         if (paramBoolean) {
/* 1268 */           URLClassPath.check(localURL1);
/*      */         }
/*      */         final File localFile;
/* 1271 */         if (paramString.indexOf("..") != -1)
/*      */         {
/* 1273 */           localFile = new File(this.dir, paramString.replace('/', File.separatorChar)).getCanonicalFile();
/* 1274 */           if (!localFile.getPath().startsWith(this.dir.getPath()))
/*      */           {
/* 1276 */             return null;
/*      */           }
/*      */         } else {
/* 1279 */           localFile = new File(this.dir, paramString.replace('/', File.separatorChar));
/*      */         }
/*      */         
/* 1282 */         if (localFile.exists()) {
/* 1283 */           new Resource() {
/* 1284 */             public String getName() { return paramString; }
/* 1285 */             public URL getURL() { return localURL1; }
/* 1286 */             public URL getCodeSourceURL() { return FileLoader.this.getBaseURL(); }
/*      */             
/* 1288 */             public InputStream getInputStream() throws IOException { return new FileInputStream(localFile); }
/*      */             
/* 1290 */             public int getContentLength() throws IOException { return (int)localFile.length(); }
/*      */           };
/*      */         }
/*      */       } catch (Exception localException) {
/* 1294 */         return null;
/*      */       }
/* 1296 */       return null;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\URLClassPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
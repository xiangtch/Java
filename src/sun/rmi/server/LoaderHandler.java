/*      */ package sun.rmi.server;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.net.JarURLConnection;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.SocketPermission;
/*      */ import java.net.URL;
/*      */ import java.net.URLClassLoader;
/*      */ import java.net.URLConnection;
/*      */ import java.rmi.server.LogStream;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.CodeSource;
/*      */ import java.security.Permission;
/*      */ import java.security.PermissionCollection;
/*      */ import java.security.Permissions;
/*      */ import java.security.Policy;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.security.cert.Certificate;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Map;
/*      */ import java.util.PropertyPermission;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.WeakHashMap;
/*      */ import sun.reflect.misc.ReflectUtil;
/*      */ import sun.rmi.runtime.Log;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class LoaderHandler
/*      */ {
/*   74 */   static final int logLevel = LogStream.parseLevel(
/*   75 */     (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.loader.logLevel")));
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   80 */   static final Log loaderLog = Log.getLog("sun.rmi.loader", "loader", logLevel);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   86 */   private static String codebaseProperty = null;
/*      */   
/*   88 */   static { Object localObject = (String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.codebase"));
/*      */     
/*   90 */     if ((localObject != null) && (((String)localObject).trim().length() > 0)) {
/*   91 */       codebaseProperty = (String)localObject;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*   96 */     codebaseURLs = null;
/*      */     
/*      */ 
/*      */ 
/*  100 */     codebaseLoaders = Collections.synchronizedMap(new IdentityHashMap(5));
/*      */     
/*  102 */     for (localObject = ClassLoader.getSystemClassLoader(); 
/*  103 */         localObject != null; 
/*  104 */         localObject = ((ClassLoader)localObject).getParent())
/*      */     {
/*  106 */       codebaseLoaders.put(localObject, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static URL[] codebaseURLs;
/*      */   
/*      */   private static final Map<ClassLoader, Void> codebaseLoaders;
/*      */   
/*  116 */   private static final HashMap<LoaderKey, LoaderEntry> loaderTable = new HashMap(5);
/*      */   
/*      */ 
/*      */ 
/*  120 */   private static final ReferenceQueue<Loader> refQueue = new ReferenceQueue();
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
/*      */   private static synchronized URL[] getDefaultCodebaseURLs()
/*      */     throws MalformedURLException
/*      */   {
/*  139 */     if (codebaseURLs == null) {
/*  140 */       if (codebaseProperty != null) {
/*  141 */         codebaseURLs = pathToURLs(codebaseProperty);
/*      */       } else {
/*  143 */         codebaseURLs = new URL[0];
/*      */       }
/*      */     }
/*  146 */     return codebaseURLs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Class<?> loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader)
/*      */     throws MalformedURLException, ClassNotFoundException
/*      */   {
/*  158 */     if (loaderLog.isLoggable(Log.BRIEF)) {
/*  159 */       loaderLog.log(Log.BRIEF, "name = \"" + paramString2 + "\", codebase = \"" + (paramString1 != null ? paramString1 : "") + "\"" + (paramClassLoader != null ? ", defaultLoader = " + paramClassLoader : ""));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     URL[] arrayOfURL;
/*      */     
/*      */ 
/*  167 */     if (paramString1 != null) {
/*  168 */       arrayOfURL = pathToURLs(paramString1);
/*      */     } else {
/*  170 */       arrayOfURL = getDefaultCodebaseURLs();
/*      */     }
/*      */     
/*  173 */     if (paramClassLoader != null) {
/*      */       try {
/*  175 */         Class localClass = loadClassForName(paramString2, false, paramClassLoader);
/*  176 */         if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  177 */           loaderLog.log(Log.VERBOSE, "class \"" + paramString2 + "\" found via defaultLoader, defined by " + localClass
/*      */           
/*  179 */             .getClassLoader());
/*      */         }
/*  181 */         return localClass;
/*      */       }
/*      */       catch (ClassNotFoundException localClassNotFoundException) {}
/*      */     }
/*      */     
/*  186 */     return loadClass(arrayOfURL, paramString2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getClassAnnotation(Class<?> paramClass)
/*      */   {
/*  195 */     String str1 = paramClass.getName();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  204 */     int i = str1.length();
/*  205 */     if ((i > 0) && (str1.charAt(0) == '['))
/*      */     {
/*  207 */       int j = 1;
/*  208 */       while ((i > j) && (str1.charAt(j) == '[')) {
/*  209 */         j++;
/*      */       }
/*  211 */       if ((i > j) && (str1.charAt(j) != 'L')) {
/*  212 */         return null;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  222 */     ClassLoader localClassLoader = paramClass.getClassLoader();
/*  223 */     if ((localClassLoader == null) || (codebaseLoaders.containsKey(localClassLoader))) {
/*  224 */       return codebaseProperty;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  231 */     String str2 = null;
/*  232 */     if ((localClassLoader instanceof Loader))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  238 */       str2 = ((Loader)localClassLoader).getClassAnnotation();
/*      */     }
/*  240 */     else if ((localClassLoader instanceof URLClassLoader)) {
/*      */       try {
/*  242 */         URL[] arrayOfURL = ((URLClassLoader)localClassLoader).getURLs();
/*  243 */         if (arrayOfURL != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  249 */           SecurityManager localSecurityManager = System.getSecurityManager();
/*  250 */           if (localSecurityManager != null) {
/*  251 */             Permissions localPermissions = new Permissions();
/*  252 */             for (int k = 0; k < arrayOfURL.length; k++)
/*      */             {
/*  254 */               Permission localPermission = arrayOfURL[k].openConnection().getPermission();
/*  255 */               if ((localPermission != null) && 
/*  256 */                 (!localPermissions.implies(localPermission))) {
/*  257 */                 localSecurityManager.checkPermission(localPermission);
/*  258 */                 localPermissions.add(localPermission);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  264 */           str2 = urlsToPath(arrayOfURL);
/*      */         }
/*      */       }
/*      */       catch (SecurityException|IOException localSecurityException) {}
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
/*  279 */     if (str2 != null) {
/*  280 */       return str2;
/*      */     }
/*  282 */     return codebaseProperty;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ClassLoader getClassLoader(String paramString)
/*      */     throws MalformedURLException
/*      */   {
/*  294 */     ClassLoader localClassLoader = getRMIContextClassLoader();
/*      */     
/*      */     URL[] arrayOfURL;
/*  297 */     if (paramString != null) {
/*  298 */       arrayOfURL = pathToURLs(paramString);
/*      */     } else {
/*  300 */       arrayOfURL = getDefaultCodebaseURLs();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  307 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  308 */     if (localSecurityManager != null) {
/*  309 */       localSecurityManager.checkPermission(new RuntimePermission("getClassLoader"));
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*  315 */       return localClassLoader;
/*      */     }
/*      */     
/*  318 */     Loader localLoader = lookupLoader(arrayOfURL, localClassLoader);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  323 */     if (localLoader != null) {
/*  324 */       localLoader.checkPermissions();
/*      */     }
/*      */     
/*  327 */     return localLoader;
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
/*      */   public static Object getSecurityContext(ClassLoader paramClassLoader)
/*      */   {
/*  340 */     if ((paramClassLoader instanceof Loader)) {
/*  341 */       URL[] arrayOfURL = ((Loader)paramClassLoader).getURLs();
/*  342 */       if (arrayOfURL.length > 0) {
/*  343 */         return arrayOfURL[0];
/*      */       }
/*      */     }
/*  346 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void registerCodebaseLoader(ClassLoader paramClassLoader)
/*      */   {
/*  354 */     codebaseLoaders.put(paramClassLoader, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Class<?> loadClass(URL[] paramArrayOfURL, String paramString)
/*      */     throws ClassNotFoundException
/*      */   {
/*  364 */     ClassLoader localClassLoader = getRMIContextClassLoader();
/*  365 */     if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  366 */       loaderLog.log(Log.VERBOSE, "(thread context class loader: " + localClassLoader + ")");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  375 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  376 */     if (localSecurityManager == null) {
/*      */       try {
/*  378 */         Class localClass1 = Class.forName(paramString, false, localClassLoader);
/*  379 */         if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  380 */           loaderLog.log(Log.VERBOSE, "class \"" + paramString + "\" found via thread context class loader (no security manager: codebase disabled), defined by " + localClass1
/*      */           
/*      */ 
/*      */ 
/*  384 */             .getClassLoader());
/*      */         }
/*  386 */         return localClass1;
/*      */       } catch (ClassNotFoundException localClassNotFoundException1) {
/*  388 */         if (loaderLog.isLoggable(Log.BRIEF)) {
/*  389 */           loaderLog.log(Log.BRIEF, "class \"" + paramString + "\" not found via thread context class loader (no security manager: codebase disabled)", localClassNotFoundException1);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  396 */         throw new ClassNotFoundException(localClassNotFoundException1.getMessage() + " (no security manager: RMI class loader disabled)", localClassNotFoundException1.getException());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  404 */     Loader localLoader = lookupLoader(paramArrayOfURL, localClassLoader);
/*      */     try
/*      */     {
/*  407 */       if (localLoader != null)
/*      */       {
/*      */ 
/*      */ 
/*  411 */         localLoader.checkPermissions();
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (SecurityException localSecurityException)
/*      */     {
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/*      */ 
/*  427 */         Class localClass3 = loadClassForName(paramString, false, localClassLoader);
/*  428 */         if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  429 */           loaderLog.log(Log.VERBOSE, "class \"" + paramString + "\" found via thread context class loader (access to codebase denied), defined by " + localClass3
/*      */           
/*      */ 
/*      */ 
/*  433 */             .getClassLoader());
/*      */         }
/*  435 */         return localClass3;
/*      */ 
/*      */       }
/*      */       catch (ClassNotFoundException localClassNotFoundException3)
/*      */       {
/*      */ 
/*  441 */         if (loaderLog.isLoggable(Log.BRIEF)) {
/*  442 */           loaderLog.log(Log.BRIEF, "class \"" + paramString + "\" not found via thread context class loader (access to codebase denied)", localSecurityException);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  447 */         throw new ClassNotFoundException("access to class loader denied", localSecurityException);
/*      */       }
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  453 */       Class localClass2 = loadClassForName(paramString, false, localLoader);
/*  454 */       if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  455 */         loaderLog.log(Log.VERBOSE, "class \"" + paramString + "\" found via codebase, defined by " + localClass2
/*      */         
/*  457 */           .getClassLoader());
/*      */       }
/*  459 */       return localClass2;
/*      */     } catch (ClassNotFoundException localClassNotFoundException2) {
/*  461 */       if (loaderLog.isLoggable(Log.BRIEF)) {
/*  462 */         loaderLog.log(Log.BRIEF, "class \"" + paramString + "\" not found via codebase", localClassNotFoundException2);
/*      */       }
/*      */       
/*  465 */       throw localClassNotFoundException2;
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
/*      */   public static Class<?> loadProxyClass(String paramString, String[] paramArrayOfString, ClassLoader paramClassLoader)
/*      */     throws MalformedURLException, ClassNotFoundException
/*      */   {
/*  479 */     if (loaderLog.isLoggable(Log.BRIEF)) {
/*  480 */       loaderLog.log(Log.BRIEF, "interfaces = " + 
/*  481 */         Arrays.asList(paramArrayOfString) + ", codebase = \"" + (paramString != null ? paramString : "") + "\"" + (paramClassLoader != null ? ", defaultLoader = " + paramClassLoader : ""));
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
/*      */ 
/*      */ 
/*  520 */     ClassLoader localClassLoader = getRMIContextClassLoader();
/*  521 */     if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  522 */       loaderLog.log(Log.VERBOSE, "(thread context class loader: " + localClassLoader + ")");
/*      */     }
/*      */     
/*      */     URL[] arrayOfURL;
/*      */     
/*  527 */     if (paramString != null) {
/*  528 */       arrayOfURL = pathToURLs(paramString);
/*      */     } else {
/*  530 */       arrayOfURL = getDefaultCodebaseURLs();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  537 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  538 */     if (localSecurityManager == null) {
/*      */       try {
/*  540 */         Class localClass1 = loadProxyClass(paramArrayOfString, paramClassLoader, localClassLoader, false);
/*      */         
/*  542 */         if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  543 */           loaderLog.log(Log.VERBOSE, "(no security manager: codebase disabled) proxy class defined by " + localClass1
/*      */           
/*  545 */             .getClassLoader());
/*      */         }
/*  547 */         return localClass1;
/*      */       } catch (ClassNotFoundException localClassNotFoundException1) {
/*  549 */         if (loaderLog.isLoggable(Log.BRIEF)) {
/*  550 */           loaderLog.log(Log.BRIEF, "(no security manager: codebase disabled) proxy class resolution failed", localClassNotFoundException1);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  556 */         throw new ClassNotFoundException(localClassNotFoundException1.getMessage() + " (no security manager: RMI class loader disabled)", localClassNotFoundException1.getException());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  564 */     Loader localLoader = lookupLoader(arrayOfURL, localClassLoader);
/*      */     try
/*      */     {
/*  567 */       if (localLoader != null)
/*      */       {
/*      */ 
/*      */ 
/*  571 */         localLoader.checkPermissions();
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (SecurityException localSecurityException)
/*      */     {
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/*      */ 
/*  587 */         Class localClass3 = loadProxyClass(paramArrayOfString, paramClassLoader, localClassLoader, false);
/*      */         
/*  589 */         if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  590 */           loaderLog.log(Log.VERBOSE, "(access to codebase denied) proxy class defined by " + localClass3
/*      */           
/*  592 */             .getClassLoader());
/*      */         }
/*  594 */         return localClass3;
/*      */ 
/*      */       }
/*      */       catch (ClassNotFoundException localClassNotFoundException3)
/*      */       {
/*      */ 
/*  600 */         if (loaderLog.isLoggable(Log.BRIEF)) {
/*  601 */           loaderLog.log(Log.BRIEF, "(access to codebase denied) proxy class resolution failed", localSecurityException);
/*      */         }
/*      */         
/*      */ 
/*  605 */         throw new ClassNotFoundException("access to class loader denied", localSecurityException);
/*      */       }
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  611 */       Class localClass2 = loadProxyClass(paramArrayOfString, paramClassLoader, localLoader, true);
/*  612 */       if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  613 */         loaderLog.log(Log.VERBOSE, "proxy class defined by " + localClass2
/*  614 */           .getClassLoader());
/*      */       }
/*  616 */       return localClass2;
/*      */     } catch (ClassNotFoundException localClassNotFoundException2) {
/*  618 */       if (loaderLog.isLoggable(Log.BRIEF)) {
/*  619 */         loaderLog.log(Log.BRIEF, "proxy class resolution failed", localClassNotFoundException2);
/*      */       }
/*      */       
/*  622 */       throw localClassNotFoundException2;
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
/*      */   private static Class<?> loadProxyClass(String[] paramArrayOfString, ClassLoader paramClassLoader1, ClassLoader paramClassLoader2, boolean paramBoolean)
/*      */     throws ClassNotFoundException
/*      */   {
/*  638 */     ClassLoader localClassLoader = null;
/*  639 */     Class[] arrayOfClass = new Class[paramArrayOfString.length];
/*  640 */     boolean[] arrayOfBoolean = { false };
/*      */     
/*      */     int i;
/*  643 */     if (paramClassLoader1 != null)
/*      */     {
/*      */       try {
/*  646 */         localClassLoader = loadProxyInterfaces(paramArrayOfString, paramClassLoader1, arrayOfClass, arrayOfBoolean);
/*      */         
/*  648 */         if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  649 */           ClassLoader[] arrayOfClassLoader1 = new ClassLoader[arrayOfClass.length];
/*      */           
/*  651 */           for (i = 0; i < arrayOfClassLoader1.length; i++) {
/*  652 */             arrayOfClassLoader1[i] = arrayOfClass[i].getClassLoader();
/*      */           }
/*  654 */           loaderLog.log(Log.VERBOSE, "proxy interfaces found via defaultLoader, defined by " + 
/*      */           
/*  656 */             Arrays.asList(arrayOfClassLoader1));
/*      */         }
/*      */       } catch (ClassNotFoundException localClassNotFoundException) {
/*      */         break label155;
/*      */       }
/*  661 */       if (arrayOfBoolean[0] == 0) {
/*  662 */         if (paramBoolean) {
/*      */           try {
/*  664 */             return Proxy.getProxyClass(paramClassLoader2, arrayOfClass);
/*      */           }
/*      */           catch (IllegalArgumentException localIllegalArgumentException) {}
/*      */         }
/*  668 */         localClassLoader = paramClassLoader1;
/*      */       }
/*  670 */       return loadProxyClass(localClassLoader, arrayOfClass);
/*      */     }
/*      */     label155:
/*  673 */     arrayOfBoolean[0] = false;
/*  674 */     localClassLoader = loadProxyInterfaces(paramArrayOfString, paramClassLoader2, arrayOfClass, arrayOfBoolean);
/*      */     
/*  676 */     if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  677 */       ClassLoader[] arrayOfClassLoader2 = new ClassLoader[arrayOfClass.length];
/*  678 */       for (i = 0; i < arrayOfClassLoader2.length; i++) {
/*  679 */         arrayOfClassLoader2[i] = arrayOfClass[i].getClassLoader();
/*      */       }
/*  681 */       loaderLog.log(Log.VERBOSE, "proxy interfaces found via codebase, defined by " + 
/*      */       
/*  683 */         Arrays.asList(arrayOfClassLoader2));
/*      */     }
/*  685 */     if (arrayOfBoolean[0] == 0) {
/*  686 */       localClassLoader = paramClassLoader2;
/*      */     }
/*  688 */     return loadProxyClass(localClassLoader, arrayOfClass);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Class<?> loadProxyClass(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass)
/*      */     throws ClassNotFoundException
/*      */   {
/*      */     try
/*      */     {
/*  699 */       return Proxy.getProxyClass(paramClassLoader, paramArrayOfClass);
/*      */     } catch (IllegalArgumentException localIllegalArgumentException) {
/*  701 */       throw new ClassNotFoundException("error creating dynamic proxy class", localIllegalArgumentException);
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
/*      */ 
/*      */   private static ClassLoader loadProxyInterfaces(String[] paramArrayOfString, ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass, boolean[] paramArrayOfBoolean)
/*      */     throws ClassNotFoundException
/*      */   {
/*  727 */     Object localObject = null;
/*      */     
/*  729 */     for (int i = 0; i < paramArrayOfString.length; i++)
/*      */     {
/*  731 */       Class localClass = paramArrayOfClass[i] = loadClassForName(paramArrayOfString[i], false, paramClassLoader);
/*      */       
/*  733 */       if (!Modifier.isPublic(localClass.getModifiers())) {
/*  734 */         ClassLoader localClassLoader = localClass.getClassLoader();
/*  735 */         if (loaderLog.isLoggable(Log.VERBOSE)) {
/*  736 */           loaderLog.log(Log.VERBOSE, "non-public interface \"" + paramArrayOfString[i] + "\" defined by " + localClassLoader);
/*      */         }
/*      */         
/*      */ 
/*  740 */         if (paramArrayOfBoolean[0] == 0) {
/*  741 */           localObject = localClassLoader;
/*  742 */           paramArrayOfBoolean[0] = true;
/*  743 */         } else if (localClassLoader != localObject) {
/*  744 */           throw new IllegalAccessError("non-public interfaces defined in different class loaders");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  750 */     return (ClassLoader)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static URL[] pathToURLs(String paramString)
/*      */     throws MalformedURLException
/*      */   {
/*  761 */     synchronized (pathToURLsCache) {
/*  762 */       localObject1 = (Object[])pathToURLsCache.get(paramString);
/*  763 */       if (localObject1 != null) {
/*  764 */         return (URL[])localObject1[0];
/*      */       }
/*      */     }
/*  767 */     ??? = new StringTokenizer(paramString);
/*  768 */     Object localObject1 = new URL[((StringTokenizer)???).countTokens()];
/*  769 */     for (int i = 0; ((StringTokenizer)???).hasMoreTokens(); i++) {
/*  770 */       localObject1[i] = new URL(((StringTokenizer)???).nextToken());
/*      */     }
/*  772 */     synchronized (pathToURLsCache) {
/*  773 */       pathToURLsCache.put(paramString, new Object[] { localObject1, new SoftReference(paramString) });
/*      */     }
/*      */     
/*  776 */     return (URL[])localObject1;
/*      */   }
/*      */   
/*      */ 
/*  780 */   private static final Map<String, Object[]> pathToURLsCache = new WeakHashMap(5);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String urlsToPath(URL[] paramArrayOfURL)
/*      */   {
/*  791 */     if (paramArrayOfURL.length == 0)
/*  792 */       return null;
/*  793 */     if (paramArrayOfURL.length == 1) {
/*  794 */       return paramArrayOfURL[0].toExternalForm();
/*      */     }
/*  796 */     StringBuffer localStringBuffer = new StringBuffer(paramArrayOfURL[0].toExternalForm());
/*  797 */     for (int i = 1; i < paramArrayOfURL.length; i++) {
/*  798 */       localStringBuffer.append(' ');
/*  799 */       localStringBuffer.append(paramArrayOfURL[i].toExternalForm());
/*      */     }
/*  801 */     return localStringBuffer.toString();
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
/*      */   private static ClassLoader getRMIContextClassLoader()
/*      */   {
/*  814 */     return Thread.currentThread().getContextClassLoader();
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
/*      */   private static Loader lookupLoader(URL[] paramArrayOfURL, final ClassLoader paramClassLoader)
/*      */   {
/*      */     Loader localLoader;
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
/*  843 */     synchronized (LoaderHandler.class)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  848 */       while ((localLoaderEntry = (LoaderEntry)refQueue.poll()) != null) {
/*  849 */         if (!localLoaderEntry.removed) {
/*  850 */           loaderTable.remove(localLoaderEntry.key);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  858 */       LoaderKey localLoaderKey = new LoaderKey(paramArrayOfURL, paramClassLoader);
/*  859 */       LoaderEntry localLoaderEntry = (LoaderEntry)loaderTable.get(localLoaderKey);
/*      */       
/*  861 */       if ((localLoaderEntry == null) || ((localLoader = (Loader)localLoaderEntry.get()) == null))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  869 */         if (localLoaderEntry != null) {
/*  870 */           loaderTable.remove(localLoaderKey);
/*  871 */           localLoaderEntry.removed = true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  881 */         AccessControlContext localAccessControlContext = getLoaderAccessControlContext(paramArrayOfURL);
/*  882 */         localLoader = (Loader)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */ 
/*  885 */           public Loader run() { return new Loader(this.val$urls, paramClassLoader, null); } }, localAccessControlContext);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  893 */         localLoaderEntry = new LoaderEntry(localLoaderKey, localLoader);
/*  894 */         loaderTable.put(localLoaderKey, localLoaderEntry);
/*      */       }
/*      */     }
/*      */     
/*  898 */     return localLoader;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class LoaderKey
/*      */   {
/*      */     private URL[] urls;
/*      */     
/*      */     private ClassLoader parent;
/*      */     
/*      */     private int hashValue;
/*      */     
/*      */ 
/*      */     public LoaderKey(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
/*      */     {
/*  914 */       this.urls = paramArrayOfURL;
/*  915 */       this.parent = paramClassLoader;
/*      */       
/*  917 */       if (paramClassLoader != null) {
/*  918 */         this.hashValue = paramClassLoader.hashCode();
/*      */       }
/*  920 */       for (int i = 0; i < paramArrayOfURL.length; i++) {
/*  921 */         this.hashValue ^= paramArrayOfURL[i].hashCode();
/*      */       }
/*      */     }
/*      */     
/*      */     public int hashCode() {
/*  926 */       return this.hashValue;
/*      */     }
/*      */     
/*      */     public boolean equals(Object paramObject) {
/*  930 */       if ((paramObject instanceof LoaderKey)) {
/*  931 */         LoaderKey localLoaderKey = (LoaderKey)paramObject;
/*  932 */         if (this.parent != localLoaderKey.parent) {
/*  933 */           return false;
/*      */         }
/*  935 */         if (this.urls == localLoaderKey.urls) {
/*  936 */           return true;
/*      */         }
/*  938 */         if (this.urls.length != localLoaderKey.urls.length) {
/*  939 */           return false;
/*      */         }
/*  941 */         for (int i = 0; i < this.urls.length; i++) {
/*  942 */           if (!this.urls[i].equals(localLoaderKey.urls[i])) {
/*  943 */             return false;
/*      */           }
/*      */         }
/*  946 */         return true;
/*      */       }
/*  948 */       return false;
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
/*      */   private static class LoaderEntry
/*      */     extends WeakReference<Loader>
/*      */   {
/*      */     public LoaderKey key;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  969 */     public boolean removed = false;
/*      */     
/*      */     public LoaderEntry(LoaderKey paramLoaderKey, Loader paramLoader) {
/*  972 */       super(LoaderHandler.refQueue);
/*  973 */       this.key = paramLoaderKey;
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
/*      */   private static AccessControlContext getLoaderAccessControlContext(URL[] paramArrayOfURL)
/*      */   {
/*  990 */     PermissionCollection localPermissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public PermissionCollection run() {
/*  993 */         CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
/*      */         
/*  995 */         Policy localPolicy = Policy.getPolicy();
/*  996 */         if (localPolicy != null) {
/*  997 */           return localPolicy.getPermissions(localCodeSource);
/*      */         }
/*  999 */         return new Permissions();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/* 1004 */     });
/* 1005 */     localPermissionCollection.add(new RuntimePermission("createClassLoader"));
/*      */     
/*      */ 
/* 1008 */     localPermissionCollection.add(new PropertyPermission("java.*", "read"));
/*      */     
/*      */ 
/* 1011 */     addPermissionsForURLs(paramArrayOfURL, localPermissionCollection, true);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1017 */     ProtectionDomain localProtectionDomain = new ProtectionDomain(new CodeSource(paramArrayOfURL.length > 0 ? paramArrayOfURL[0] : null, (Certificate[])null), localPermissionCollection);
/*      */     
/*      */ 
/*      */ 
/* 1021 */     return new AccessControlContext(new ProtectionDomain[] { localProtectionDomain });
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
/*      */   private static void addPermissionsForURLs(URL[] paramArrayOfURL, PermissionCollection paramPermissionCollection, boolean paramBoolean)
/*      */   {
/* 1038 */     for (int i = 0; i < paramArrayOfURL.length; i++) {
/* 1039 */       URL localURL = paramArrayOfURL[i];
/*      */       try {
/* 1041 */         URLConnection localURLConnection = localURL.openConnection();
/* 1042 */         Permission localPermission = localURLConnection.getPermission();
/* 1043 */         if (localPermission != null) { Object localObject1;
/* 1044 */           Object localObject3; if ((localPermission instanceof FilePermission))
/*      */           {
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
/* 1056 */             localObject1 = localPermission.getName();
/* 1057 */             int j = ((String)localObject1).lastIndexOf(File.separatorChar);
/* 1058 */             if (j != -1) {
/* 1059 */               localObject1 = ((String)localObject1).substring(0, j + 1);
/* 1060 */               if (((String)localObject1).endsWith(File.separator)) {
/* 1061 */                 localObject1 = (String)localObject1 + "-";
/*      */               }
/* 1063 */               localObject3 = new FilePermission((String)localObject1, "read");
/* 1064 */               if (!paramPermissionCollection.implies((Permission)localObject3)) {
/* 1065 */                 paramPermissionCollection.add((Permission)localObject3);
/*      */               }
/* 1067 */               paramPermissionCollection.add(new FilePermission((String)localObject1, "read"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             }
/* 1073 */             else if (!paramPermissionCollection.implies(localPermission)) {
/* 1074 */               paramPermissionCollection.add(localPermission);
/*      */             }
/*      */           }
/*      */           else {
/* 1078 */             if (!paramPermissionCollection.implies(localPermission)) {
/* 1079 */               paramPermissionCollection.add(localPermission);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1090 */             if (paramBoolean)
/*      */             {
/* 1092 */               localObject1 = localURL;
/* 1093 */               Object localObject2 = localURLConnection;
/* 1094 */               while ((localObject2 instanceof JarURLConnection))
/*      */               {
/*      */ 
/* 1097 */                 localObject1 = ((JarURLConnection)localObject2).getJarFileURL();
/* 1098 */                 localObject2 = ((URL)localObject1).openConnection();
/*      */               }
/* 1100 */               localObject2 = ((URL)localObject1).getHost();
/* 1101 */               if ((localObject2 != null) && 
/* 1102 */                 (localPermission.implies(new SocketPermission((String)localObject2, "resolve"))))
/*      */               {
/*      */ 
/* 1105 */                 localObject3 = new SocketPermission((String)localObject2, "connect,accept");
/*      */                 
/*      */ 
/* 1108 */                 if (!paramPermissionCollection.implies((Permission)localObject3)) {
/* 1109 */                   paramPermissionCollection.add((Permission)localObject3);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class Loader
/*      */     extends URLClassLoader
/*      */   {
/*      */     private ClassLoader parent;
/*      */     
/*      */ 
/*      */ 
/*      */     private String annotation;
/*      */     
/*      */ 
/*      */ 
/*      */     private Permissions permissions;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private Loader(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
/*      */     {
/* 1142 */       super(paramClassLoader);
/* 1143 */       this.parent = paramClassLoader;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1148 */       this.permissions = new Permissions();
/* 1149 */       LoaderHandler.addPermissionsForURLs(paramArrayOfURL, this.permissions, false);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1156 */       this.annotation = LoaderHandler.urlsToPath(paramArrayOfURL);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getClassAnnotation()
/*      */     {
/* 1164 */       return this.annotation;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void checkPermissions()
/*      */     {
/* 1172 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 1173 */       if (localSecurityManager != null) {
/* 1174 */         Enumeration localEnumeration = this.permissions.elements();
/* 1175 */         while (localEnumeration.hasMoreElements()) {
/* 1176 */           localSecurityManager.checkPermission((Permission)localEnumeration.nextElement());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected PermissionCollection getPermissions(CodeSource paramCodeSource)
/*      */     {
/* 1186 */       PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
/*      */       
/*      */ 
/*      */ 
/* 1190 */       return localPermissionCollection;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1198 */       return super.toString() + "[\"" + this.annotation + "\"]";
/*      */     }
/*      */     
/*      */     protected Class<?> loadClass(String paramString, boolean paramBoolean)
/*      */       throws ClassNotFoundException
/*      */     {
/* 1204 */       if (this.parent == null) {
/* 1205 */         ReflectUtil.checkPackageAccess(paramString);
/*      */       }
/* 1207 */       return super.loadClass(paramString, paramBoolean);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Class<?> loadClassForName(String paramString, boolean paramBoolean, ClassLoader paramClassLoader)
/*      */     throws ClassNotFoundException
/*      */   {
/* 1218 */     if (paramClassLoader == null) {
/* 1219 */       ReflectUtil.checkPackageAccess(paramString);
/*      */     }
/* 1221 */     return Class.forName(paramString, paramBoolean, paramClassLoader);
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\LoaderHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
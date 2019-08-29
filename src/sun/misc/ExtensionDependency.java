/*     */ package sun.misc;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Attributes.Name;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.Manifest;
/*     */ import sun.net.www.ParseUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExtensionDependency
/*     */ {
/*     */   private static Vector<ExtensionInstallationProvider> providers;
/*     */   static final boolean DEBUG = false;
/*     */   
/*     */   public static synchronized void addExtensionInstallationProvider(ExtensionInstallationProvider paramExtensionInstallationProvider)
/*     */   {
/*  85 */     if (providers == null) {
/*  86 */       providers = new Vector();
/*     */     }
/*  88 */     providers.add(paramExtensionInstallationProvider);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void removeExtensionInstallationProvider(ExtensionInstallationProvider paramExtensionInstallationProvider)
/*     */   {
/*  99 */     providers.remove(paramExtensionInstallationProvider);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean checkExtensionsDependencies(JarFile paramJarFile)
/*     */   {
/* 110 */     if (providers == null)
/*     */     {
/*     */ 
/* 113 */       return true;
/*     */     }
/*     */     try
/*     */     {
/* 117 */       ExtensionDependency localExtensionDependency = new ExtensionDependency();
/* 118 */       return localExtensionDependency.checkExtensions(paramJarFile);
/*     */     } catch (ExtensionInstallationException localExtensionInstallationException) {
/* 120 */       debug(localExtensionInstallationException.getMessage());
/*     */     }
/* 122 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean checkExtensions(JarFile paramJarFile)
/*     */     throws ExtensionInstallationException
/*     */   {
/*     */     Manifest localManifest;
/*     */     
/*     */     try
/*     */     {
/* 134 */       localManifest = paramJarFile.getManifest();
/*     */     } catch (IOException localIOException) {
/* 136 */       return false;
/*     */     }
/*     */     
/* 139 */     if (localManifest == null)
/*     */     {
/*     */ 
/* 142 */       return true;
/*     */     }
/*     */     
/* 145 */     boolean bool = true;
/* 146 */     Attributes localAttributes = localManifest.getMainAttributes();
/* 147 */     if (localAttributes != null)
/*     */     {
/* 149 */       String str1 = localAttributes.getValue(Name.EXTENSION_LIST);
/* 150 */       if (str1 != null) {
/* 151 */         StringTokenizer localStringTokenizer = new StringTokenizer(str1);
/*     */         
/* 153 */         while (localStringTokenizer.hasMoreTokens()) {
/* 154 */           String str2 = localStringTokenizer.nextToken();
/* 155 */           debug("The file " + paramJarFile.getName() + " appears to depend on " + str2);
/*     */           
/*     */ 
/*     */ 
/* 159 */           String str3 = str2 + "-" + Name.EXTENSION_NAME.toString();
/* 160 */           if (localAttributes.getValue(str3) == null) {
/* 161 */             debug("The jar file " + paramJarFile.getName() + " appers to depend on " + str2 + " but does not define the " + str3 + " attribute in its manifest ");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           }
/* 167 */           else if (!checkExtension(str2, localAttributes)) {
/* 168 */             debug("Failed installing " + str2);
/* 169 */             bool = false;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 174 */         debug("No dependencies for " + paramJarFile.getName());
/*     */       }
/*     */     }
/* 177 */     return bool;
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
/*     */   protected synchronized boolean checkExtension(String paramString, Attributes paramAttributes)
/*     */     throws ExtensionInstallationException
/*     */   {
/* 194 */     debug("Checking extension " + paramString);
/* 195 */     if (checkExtensionAgainstInstalled(paramString, paramAttributes)) {
/* 196 */       return true;
/*     */     }
/* 198 */     debug("Extension not currently installed ");
/* 199 */     ExtensionInfo localExtensionInfo = new ExtensionInfo(paramString, paramAttributes);
/* 200 */     return installExtension(localExtensionInfo, null);
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
/*     */   boolean checkExtensionAgainstInstalled(String paramString, Attributes paramAttributes)
/*     */     throws ExtensionInstallationException
/*     */   {
/* 217 */     File localFile = checkExtensionExists(paramString);
/*     */     
/* 219 */     if (localFile != null)
/*     */     {
/*     */       try {
/* 222 */         if (checkExtensionAgainst(paramString, paramAttributes, localFile))
/* 223 */           return true;
/*     */       } catch (FileNotFoundException localFileNotFoundException1) {
/* 225 */         debugException(localFileNotFoundException1);
/*     */       } catch (IOException localIOException1) {
/* 227 */         debugException(localIOException1);
/*     */       }
/* 229 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     File[] arrayOfFile;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 240 */       arrayOfFile = getInstalledExtensions();
/*     */     } catch (IOException localIOException2) {
/* 242 */       debugException(localIOException2);
/* 243 */       return false;
/*     */     }
/*     */     
/* 246 */     for (int i = 0; i < arrayOfFile.length; i++) {
/*     */       try {
/* 248 */         if (checkExtensionAgainst(paramString, paramAttributes, arrayOfFile[i]))
/* 249 */           return true;
/*     */       } catch (FileNotFoundException localFileNotFoundException2) {
/* 251 */         debugException(localFileNotFoundException2);
/*     */       } catch (IOException localIOException3) {
/* 253 */         debugException(localIOException3);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 258 */     return false;
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
/*     */   protected boolean checkExtensionAgainst(String paramString, Attributes paramAttributes, final File paramFile)
/*     */     throws IOException, FileNotFoundException, ExtensionInstallationException
/*     */   {
/* 281 */     debug(
/* 282 */       "Checking extension " + paramString + " against " + paramFile.getName());
/*     */     
/*     */     Manifest localManifest;
/*     */     try
/*     */     {
/* 287 */       localManifest = (Manifest)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Manifest run() throws IOException, FileNotFoundException
/*     */         {
/* 291 */           if (!paramFile.exists())
/* 292 */             throw new FileNotFoundException(paramFile.getName());
/* 293 */           JarFile localJarFile = new JarFile(paramFile);
/* 294 */           return localJarFile.getManifest();
/*     */         }
/*     */       });
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 298 */       if ((localPrivilegedActionException.getException() instanceof FileNotFoundException))
/* 299 */         throw ((FileNotFoundException)localPrivilegedActionException.getException());
/* 300 */       throw ((IOException)localPrivilegedActionException.getException());
/*     */     }
/*     */     
/*     */ 
/* 304 */     ExtensionInfo localExtensionInfo1 = new ExtensionInfo(paramString, paramAttributes);
/* 305 */     debug("Requested Extension : " + localExtensionInfo1);
/*     */     
/* 307 */     int i = 4;
/* 308 */     ExtensionInfo localExtensionInfo2 = null;
/*     */     
/* 310 */     if (localManifest != null) {
/* 311 */       Attributes localAttributes = localManifest.getMainAttributes();
/* 312 */       if (localAttributes != null) {
/* 313 */         localExtensionInfo2 = new ExtensionInfo(null, localAttributes);
/* 314 */         debug("Extension Installed " + localExtensionInfo2);
/* 315 */         i = localExtensionInfo2.isCompatibleWith(localExtensionInfo1);
/* 316 */         switch (i) {
/*     */         case 0: 
/* 318 */           debug("Extensions are compatible");
/* 319 */           return true;
/*     */         
/*     */         case 4: 
/* 322 */           debug("Extensions are incompatible");
/* 323 */           return false;
/*     */         }
/*     */         
/*     */         
/* 327 */         debug("Extensions require an upgrade or vendor switch");
/* 328 */         return installExtension(localExtensionInfo1, localExtensionInfo2);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 333 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean installExtension(ExtensionInfo paramExtensionInfo1, ExtensionInfo paramExtensionInfo2)
/*     */     throws ExtensionInstallationException
/*     */   {
/*     */     Object localObject2;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     Object localObject1;
/*     */     
/*     */ 
/*     */ 
/* 352 */     synchronized (providers)
/*     */     {
/*     */ 
/* 355 */       localObject2 = (Vector)providers.clone();
/* 356 */       localObject1 = localObject2;
/*     */     }
/* 358 */     ??? = ((Vector)localObject1).elements();
/* 359 */     while (((Enumeration)???).hasMoreElements()) {
/* 360 */       localObject2 = (ExtensionInstallationProvider)((Enumeration)???).nextElement();
/*     */       
/* 362 */       if (localObject2 != null)
/*     */       {
/* 364 */         if (((ExtensionInstallationProvider)localObject2).installExtension(paramExtensionInfo1, paramExtensionInfo2)) {
/* 365 */           debug(paramExtensionInfo1.name + " installation successful");
/*     */           
/* 367 */           Launcher.ExtClassLoader localExtClassLoader = (Launcher.ExtClassLoader)Launcher.getLauncher().getClassLoader().getParent();
/* 368 */           addNewExtensionsToClassLoader(localExtClassLoader);
/* 369 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 375 */     debug(paramExtensionInfo1.name + " installation failed");
/* 376 */     return false;
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
/*     */   private File checkExtensionExists(String paramString)
/*     */   {
/* 392 */     final String str = paramString;
/* 393 */     final String[] arrayOfString = { ".jar", ".zip" };
/*     */     
/* 395 */     (File)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public File run()
/*     */       {
/*     */         try {
/* 400 */           File[] arrayOfFile = ExtensionDependency.access$000();
/*     */           
/*     */ 
/*     */ 
/* 404 */           for (int i = 0; i < arrayOfFile.length; i++) {
/* 405 */             for (int j = 0; j < arrayOfString.length; j++) { File localFile;
/* 406 */               if (str.toLowerCase().endsWith(arrayOfString[j])) {
/* 407 */                 localFile = new File(arrayOfFile[i], str);
/*     */               } else {
/* 409 */                 localFile = new File(arrayOfFile[i], str + arrayOfString[j]);
/*     */               }
/* 411 */               ExtensionDependency.debug("checkExtensionExists:fileName " + localFile.getName());
/* 412 */               if (localFile.exists()) {
/* 413 */                 return localFile;
/*     */               }
/*     */             }
/*     */           }
/* 417 */           return null;
/*     */         }
/*     */         catch (Exception localException) {
/* 420 */           ExtensionDependency.this.debugException(localException); }
/* 421 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static File[] getExtDirs()
/*     */   {
/* 433 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.ext.dirs"));
/*     */     
/*     */     File[] arrayOfFile;
/*     */     
/* 437 */     if (str != null) {
/* 438 */       StringTokenizer localStringTokenizer = new StringTokenizer(str, File.pathSeparator);
/*     */       
/* 440 */       int i = localStringTokenizer.countTokens();
/* 441 */       debug("getExtDirs count " + i);
/* 442 */       arrayOfFile = new File[i];
/* 443 */       for (int j = 0; j < i; j++) {
/* 444 */         arrayOfFile[j] = new File(localStringTokenizer.nextToken());
/* 445 */         debug("getExtDirs dirs[" + j + "] " + arrayOfFile[j]);
/*     */       }
/*     */     } else {
/* 448 */       arrayOfFile = new File[0];
/* 449 */       debug("getExtDirs dirs " + arrayOfFile);
/*     */     }
/* 451 */     debug("getExtDirs dirs.length " + arrayOfFile.length);
/* 452 */     return arrayOfFile;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static File[] getExtFiles(File[] paramArrayOfFile)
/*     */     throws IOException
/*     */   {
/* 464 */     Vector localVector = new Vector();
/* 465 */     for (int i = 0; i < paramArrayOfFile.length; i++) {
/* 466 */       String[] arrayOfString = paramArrayOfFile[i].list(new JarFilter());
/* 467 */       if (arrayOfString != null) {
/* 468 */         debug("getExtFiles files.length " + arrayOfString.length);
/* 469 */         for (int j = 0; j < arrayOfString.length; j++) {
/* 470 */           File localFile = new File(paramArrayOfFile[i], arrayOfString[j]);
/* 471 */           localVector.add(localFile);
/* 472 */           debug("getExtFiles f[" + j + "] " + localFile);
/*     */         }
/*     */       }
/*     */     }
/* 476 */     File[] arrayOfFile = new File[localVector.size()];
/* 477 */     localVector.copyInto(arrayOfFile);
/* 478 */     debug("getExtFiles ua.length " + arrayOfFile.length);
/* 479 */     return arrayOfFile;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private File[] getInstalledExtensions()
/*     */     throws IOException
/*     */   {
/* 488 */     (File[])AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public File[] run() {
/*     */         try {
/* 492 */           return ExtensionDependency.getExtFiles(ExtensionDependency.access$000());
/*     */         } catch (IOException localIOException) {
/* 494 */           ExtensionDependency.debug("Cannot get list of installed extensions");
/* 495 */           ExtensionDependency.this.debugException(localIOException); }
/* 496 */         return new File[0];
/*     */       }
/*     */     });
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
/*     */   private Boolean addNewExtensionsToClassLoader(Launcher.ExtClassLoader paramExtClassLoader)
/*     */   {
/*     */     try
/*     */     {
/* 513 */       File[] arrayOfFile = getInstalledExtensions();
/* 514 */       for (int i = 0; i < arrayOfFile.length; i++) {
/* 515 */         final File localFile = arrayOfFile[i];
/* 516 */         URL localURL = (URL)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public URL run() {
/*     */             try {
/* 520 */               return ParseUtil.fileToEncodedURL(localFile);
/*     */             } catch (MalformedURLException localMalformedURLException) {
/* 522 */               ExtensionDependency.this.debugException(localMalformedURLException); }
/* 523 */             return null;
/*     */           }
/*     */         });
/*     */         
/* 527 */         if (localURL != null) {
/* 528 */           URL[] arrayOfURL = paramExtClassLoader.getURLs();
/* 529 */           int j = 0;
/* 530 */           for (int k = 0; k < arrayOfURL.length; k++) {
/* 531 */             debug("URL[" + k + "] is " + arrayOfURL[k] + " looking for " + localURL);
/*     */             
/* 533 */             if (arrayOfURL[k].toString().compareToIgnoreCase(localURL
/* 534 */               .toString()) == 0) {
/* 535 */               j = 1;
/* 536 */               debug("Found !");
/*     */             }
/*     */           }
/* 539 */           if (j == 0) {
/* 540 */             debug("Not Found ! adding to the classloader " + localURL);
/*     */             
/* 542 */             paramExtClassLoader.addExtURL(localURL);
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (MalformedURLException localMalformedURLException) {
/* 547 */       localMalformedURLException.printStackTrace();
/*     */     } catch (IOException localIOException) {
/* 549 */       localIOException.printStackTrace();
/*     */     }
/*     */     
/* 552 */     return Boolean.TRUE;
/*     */   }
/*     */   
/*     */   private static void debug(String paramString) {}
/*     */   
/*     */   private void debugException(Throwable paramThrowable) {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\ExtensionDependency.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.StreamTokenizer;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.Security;
/*     */ import java.security.URIParameter;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.AuthPermission;
/*     */ import javax.security.auth.login.AppConfigurationEntry;
/*     */ import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
/*     */ import javax.security.auth.login.Configuration;
/*     */ import javax.security.auth.login.Configuration.Parameters;
/*     */ import javax.security.auth.login.ConfigurationSpi;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.PropertyExpander;
/*     */ import sun.security.util.PropertyExpander.ExpandException;
/*     */ import sun.security.util.ResourcesMgr;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ConfigFile
/*     */   extends Configuration
/*     */ {
/*     */   private final Spi spi;
/*     */   
/*     */   public ConfigFile()
/*     */   {
/* 102 */     this.spi = new Spi();
/*     */   }
/*     */   
/*     */   public AppConfigurationEntry[] getAppConfigurationEntry(String paramString)
/*     */   {
/* 107 */     return this.spi.engineGetAppConfigurationEntry(paramString);
/*     */   }
/*     */   
/*     */   public synchronized void refresh()
/*     */   {
/* 112 */     this.spi.engineRefresh();
/*     */   }
/*     */   
/*     */   public static final class Spi extends ConfigurationSpi
/*     */   {
/*     */     private URL url;
/* 118 */     private boolean expandProp = true;
/*     */     
/*     */     private Map<String, List<AppConfigurationEntry>> configuration;
/*     */     private int linenum;
/*     */     private StreamTokenizer st;
/*     */     private int lookahead;
/* 124 */     private static Debug debugConfig = Debug.getInstance("configfile");
/* 125 */     private static Debug debugParser = Debug.getInstance("configparser");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Spi()
/*     */     {
/*     */       try
/*     */       {
/* 135 */         init();
/*     */       } catch (IOException localIOException) {
/* 137 */         throw new SecurityException(localIOException);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Spi(URI paramURI)
/*     */     {
/*     */       try
/*     */       {
/* 153 */         this.url = paramURI.toURL();
/* 154 */         init();
/*     */       } catch (IOException localIOException) {
/* 156 */         throw new SecurityException(localIOException);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Spi(final Parameters paramParameters)
/*     */       throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 169 */         AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */           public Void run() throws IOException {
/* 171 */             if (paramParameters == null) {
/* 172 */               Spi.this.init();
/*     */             } else {
/* 174 */               if (!(paramParameters instanceof URIParameter)) {
/* 175 */                 throw new IllegalArgumentException("Unrecognized parameter: " + paramParameters);
/*     */               }
/*     */               
/* 178 */               URIParameter localURIParameter = (URIParameter)paramParameters;
/* 179 */               Spi.this.url = localURIParameter.getURI().toURL();
/* 180 */               Spi.this.init();
/*     */             }
/* 182 */             return null;
/*     */           }
/*     */         });
/*     */       } catch (PrivilegedActionException localPrivilegedActionException) {
/* 186 */         throw ((IOException)localPrivilegedActionException.getException());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void init()
/*     */       throws IOException
/*     */     {
/* 203 */       int i = 0;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 208 */       String str1 = Security.getProperty("policy.expandProperties");
/* 209 */       if (str1 == null) {
/* 210 */         str1 = System.getProperty("policy.expandProperties");
/*     */       }
/* 212 */       if ("false".equals(str1)) {
/* 213 */         this.expandProp = false;
/*     */       }
/*     */       
/*     */ 
/* 217 */       HashMap localHashMap = new HashMap();
/*     */       
/* 219 */       if (this.url != null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 224 */         if (debugConfig != null) {
/* 225 */           debugConfig.println("reading " + this.url);
/*     */         }
/* 227 */         init(this.url, localHashMap);
/* 228 */         this.configuration = localHashMap;
/* 229 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */       String str2 = Security.getProperty("policy.allowSystemProperty");
/*     */       
/* 238 */       if ("true".equalsIgnoreCase(str2))
/*     */       {
/* 240 */         String str3 = System.getProperty("java.security.auth.login.config");
/* 241 */         if (str3 != null) {
/* 242 */           int k = 0;
/* 243 */           if (str3.startsWith("=")) {
/* 244 */             k = 1;
/* 245 */             str3 = str3.substring(1);
/*     */           }
/*     */           try {
/* 248 */             str3 = PropertyExpander.expand(str3);
/*     */           } catch (ExpandException localExpandException1) {
/* 250 */             throw ioException("Unable.to.properly.expand.config", new Object[] { str3 });
/*     */           }
/*     */           
/*     */ 
/* 254 */           URL localURL = null;
/*     */           try {
/* 256 */             localURL = new URL(str3);
/*     */           } catch (MalformedURLException localMalformedURLException) {
/* 258 */             File localFile = new File(str3);
/* 259 */             if (localFile.exists()) {
/* 260 */               localURL = localFile.toURI().toURL();
/*     */             } else {
/* 262 */               throw ioException("extra.config.No.such.file.or.directory.", new Object[] { str3 });
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 268 */           if (debugConfig != null) {
/* 269 */             debugConfig.println("reading " + localURL);
/*     */           }
/* 271 */           init(localURL, localHashMap);
/* 272 */           i = 1;
/* 273 */           if (k != 0) {
/* 274 */             if (debugConfig != null) {
/* 275 */               debugConfig.println("overriding other policies!");
/*     */             }
/* 277 */             this.configuration = localHashMap;
/* 278 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 283 */       int j = 1;
/*     */       
/*     */       String str4;
/* 286 */       while ((str4 = Security.getProperty("login.config.url." + j)) != null)
/*     */       {
/*     */         try {
/* 289 */           str4 = PropertyExpander.expand(str4).replace(File.separatorChar, '/');
/* 290 */           if (debugConfig != null) {
/* 291 */             debugConfig.println("\tReading config: " + str4);
/*     */           }
/* 293 */           init(new URL(str4), localHashMap);
/* 294 */           i = 1;
/*     */         } catch (ExpandException localExpandException2) {
/* 296 */           throw ioException("Unable.to.properly.expand.config", new Object[] { str4 });
/*     */         }
/*     */         
/* 299 */         j++;
/*     */       }
/*     */       
/* 302 */       if ((i == 0) && (j == 1) && (str4 == null))
/*     */       {
/*     */ 
/* 305 */         if (debugConfig != null) {
/* 306 */           debugConfig.println("\tReading Policy from ~/.java.login.config");
/*     */         }
/*     */         
/* 309 */         str4 = System.getProperty("user.home");
/* 310 */         String str5 = str4 + File.separatorChar + ".java.login.config";
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 315 */         if (new File(str5).exists()) {
/* 316 */           init(new File(str5).toURI().toURL(), localHashMap);
/*     */         }
/*     */       }
/*     */       
/* 320 */       this.configuration = localHashMap;
/*     */     }
/*     */     
/*     */     private void init(URL paramURL, Map<String, List<AppConfigurationEntry>> paramMap)
/*     */       throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 328 */         InputStreamReader localInputStreamReader = new InputStreamReader(getInputStream(paramURL), "UTF-8");Object localObject1 = null;
/* 329 */         try { readConfig(localInputStreamReader, paramMap);
/*     */         }
/*     */         catch (Throwable localThrowable2)
/*     */         {
/* 327 */           localObject1 = localThrowable2;throw localThrowable2;
/*     */         }
/*     */         finally {
/* 330 */           if (localInputStreamReader != null) if (localObject1 != null) try { localInputStreamReader.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localInputStreamReader.close();
/* 331 */         } } catch (FileNotFoundException localFileNotFoundException) { if (debugConfig != null) {
/* 332 */           debugConfig.println(localFileNotFoundException.toString());
/*     */         }
/*     */         
/* 335 */         throw new IOException(ResourcesMgr.getString("Configuration.Error.No.such.file.or.directory", "sun.security.util.AuthResources"));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public AppConfigurationEntry[] engineGetAppConfigurationEntry(String paramString)
/*     */     {
/* 354 */       List localList = null;
/* 355 */       synchronized (this.configuration) {
/* 356 */         localList = (List)this.configuration.get(paramString);
/*     */       }
/*     */       
/* 359 */       if ((localList == null) || (localList.size() == 0)) {
/* 360 */         return null;
/*     */       }
/*     */       
/*     */ 
/* 364 */       ??? = new AppConfigurationEntry[localList.size()];
/* 365 */       Iterator localIterator = localList.iterator();
/* 366 */       for (int i = 0; localIterator.hasNext(); i++) {
/* 367 */         AppConfigurationEntry localAppConfigurationEntry = (AppConfigurationEntry)localIterator.next();
/* 368 */         ???[i] = new AppConfigurationEntry(localAppConfigurationEntry.getLoginModuleName(), localAppConfigurationEntry
/* 369 */           .getControlFlag(), localAppConfigurationEntry
/* 370 */           .getOptions());
/*     */       }
/* 372 */       return (AppConfigurationEntry[])???;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public synchronized void engineRefresh()
/*     */     {
/* 385 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 386 */       if (localSecurityManager != null) {
/* 387 */         localSecurityManager.checkPermission(new AuthPermission("refreshLoginConfiguration"));
/*     */       }
/*     */       
/*     */ 
/* 391 */       AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public Void run() {
/*     */           try {
/* 394 */             Spi.this.init();
/*     */           } catch (IOException localIOException) {
/* 396 */             throw new SecurityException(localIOException.getLocalizedMessage(), localIOException);
/*     */           }
/*     */           
/* 399 */           return null;
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */     private void readConfig(Reader paramReader, Map<String, List<AppConfigurationEntry>> paramMap)
/*     */       throws IOException
/*     */     {
/* 408 */       this.linenum = 1;
/*     */       
/* 410 */       if (!(paramReader instanceof BufferedReader)) {
/* 411 */         paramReader = new BufferedReader(paramReader);
/*     */       }
/*     */       
/* 414 */       this.st = new StreamTokenizer(paramReader);
/* 415 */       this.st.quoteChar(34);
/* 416 */       this.st.wordChars(36, 36);
/* 417 */       this.st.wordChars(95, 95);
/* 418 */       this.st.wordChars(45, 45);
/* 419 */       this.st.wordChars(42, 42);
/* 420 */       this.st.lowerCaseMode(false);
/* 421 */       this.st.slashSlashComments(true);
/* 422 */       this.st.slashStarComments(true);
/* 423 */       this.st.eolIsSignificant(true);
/*     */       
/* 425 */       this.lookahead = nextToken();
/* 426 */       while (this.lookahead != -1) {
/* 427 */         parseLoginEntry(paramMap);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     private void parseLoginEntry(Map<String, List<AppConfigurationEntry>> paramMap)
/*     */       throws IOException
/*     */     {
/* 435 */       LinkedList localLinkedList = new LinkedList();
/*     */       
/*     */ 
/* 438 */       String str1 = this.st.sval;
/* 439 */       this.lookahead = nextToken();
/*     */       
/* 441 */       if (debugParser != null) {
/* 442 */         debugParser.println("\tReading next config entry: " + str1);
/*     */       }
/*     */       
/* 445 */       match("{");
/*     */       
/*     */ 
/* 448 */       while (!peek("}"))
/*     */       {
/* 450 */         String str2 = match("module class name");
/*     */         
/*     */ 
/*     */ 
/* 454 */         String str3 = match("controlFlag").toUpperCase(Locale.ENGLISH);
/* 455 */         Object localObject1 = str3;int i = -1; switch (((String)localObject1).hashCode()) {case 389487519:  if (((String)localObject1).equals("REQUIRED")) i = 0; break; case -810754599:  if (((String)localObject1).equals("REQUISITE")) i = 1; break; case -848090850:  if (((String)localObject1).equals("SUFFICIENT")) i = 2; break; case 703609696:  if (((String)localObject1).equals("OPTIONAL")) i = 3; break; } LoginModuleControlFlag localLoginModuleControlFlag; switch (i) {
/*     */         case 0: 
/* 457 */           localLoginModuleControlFlag = LoginModuleControlFlag.REQUIRED;
/* 458 */           break;
/*     */         case 1: 
/* 460 */           localLoginModuleControlFlag = LoginModuleControlFlag.REQUISITE;
/* 461 */           break;
/*     */         case 2: 
/* 463 */           localLoginModuleControlFlag = LoginModuleControlFlag.SUFFICIENT;
/* 464 */           break;
/*     */         case 3: 
/* 466 */           localLoginModuleControlFlag = LoginModuleControlFlag.OPTIONAL;
/* 467 */           break;
/*     */         default: 
/* 469 */           throw ioException("Configuration.Error.Invalid.control.flag.flag", new Object[] { str3 });
/*     */         }
/*     */         
/*     */         
/*     */ 
/*     */ 
/* 475 */         localObject1 = new HashMap();
/* 476 */         Object localObject2; while (!peek(";")) {
/* 477 */           localObject2 = match("option key");
/* 478 */           match("=");
/*     */           try {
/* 480 */             ((Map)localObject1).put(localObject2, expand(match("option value")));
/*     */           } catch (ExpandException localExpandException) {
/* 482 */             throw new IOException(localExpandException.getLocalizedMessage());
/*     */           }
/*     */         }
/*     */         
/* 486 */         this.lookahead = nextToken();
/*     */         
/*     */ 
/* 489 */         if (debugParser != null) {
/* 490 */           debugParser.println("\t\t" + str2 + ", " + str3);
/* 491 */           for (localObject2 = ((Map)localObject1).keySet().iterator(); ((Iterator)localObject2).hasNext();) { String str4 = (String)((Iterator)localObject2).next();
/* 492 */             debugParser.println("\t\t\t" + str4 + "=" + 
/* 493 */               (String)((Map)localObject1).get(str4));
/*     */           }
/*     */         }
/* 496 */         localLinkedList.add(new AppConfigurationEntry(str2, localLoginModuleControlFlag, (Map)localObject1));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 501 */       match("}");
/* 502 */       match(";");
/*     */       
/*     */ 
/* 505 */       if (paramMap.containsKey(str1)) {
/* 506 */         throw ioException("Configuration.Error.Can.not.specify.multiple.entries.for.appName", new Object[] { str1 });
/*     */       }
/*     */       
/*     */ 
/* 510 */       paramMap.put(str1, localLinkedList);
/*     */     }
/*     */     
/*     */     private String match(String paramString) throws IOException
/*     */     {
/* 515 */       String str = null;
/*     */       
/* 517 */       switch (this.lookahead) {
/*     */       case -1: 
/* 519 */         throw ioException("Configuration.Error.expected.expect.read.end.of.file.", new Object[] { paramString });
/*     */       
/*     */ 
/*     */ 
/*     */       case -3: 
/*     */       case 34: 
/* 525 */         if ((paramString.equalsIgnoreCase("module class name")) || 
/* 526 */           (paramString.equalsIgnoreCase("controlFlag")) || 
/* 527 */           (paramString.equalsIgnoreCase("option key")) || 
/* 528 */           (paramString.equalsIgnoreCase("option value"))) {
/* 529 */           str = this.st.sval;
/* 530 */           this.lookahead = nextToken();
/*     */         } else {
/* 532 */           throw ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Object[] { new Integer(this.linenum), paramString, this.st.sval });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       case 123: 
/* 539 */         if (paramString.equalsIgnoreCase("{")) {
/* 540 */           this.lookahead = nextToken();
/*     */         } else {
/* 542 */           throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(this.linenum), paramString, this.st.sval });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       case 59: 
/* 549 */         if (paramString.equalsIgnoreCase(";")) {
/* 550 */           this.lookahead = nextToken();
/*     */         } else {
/* 552 */           throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(this.linenum), paramString, this.st.sval });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       case 125: 
/* 559 */         if (paramString.equalsIgnoreCase("}")) {
/* 560 */           this.lookahead = nextToken();
/*     */         } else {
/* 562 */           throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(this.linenum), paramString, this.st.sval });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       case 61: 
/* 569 */         if (paramString.equalsIgnoreCase("=")) {
/* 570 */           this.lookahead = nextToken();
/*     */         } else {
/* 572 */           throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(this.linenum), paramString, this.st.sval });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       default: 
/* 579 */         throw ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Object[] { new Integer(this.linenum), paramString, this.st.sval });
/*     */       }
/*     */       
/*     */       
/* 583 */       return str;
/*     */     }
/*     */     
/*     */     private boolean peek(String paramString) {
/* 587 */       switch (this.lookahead) {
/*     */       case 44: 
/* 589 */         return paramString.equalsIgnoreCase(",");
/*     */       case 59: 
/* 591 */         return paramString.equalsIgnoreCase(";");
/*     */       case 123: 
/* 593 */         return paramString.equalsIgnoreCase("{");
/*     */       case 125: 
/* 595 */         return paramString.equalsIgnoreCase("}");
/*     */       }
/* 597 */       return false;
/*     */     }
/*     */     
/*     */     private int nextToken() throws IOException
/*     */     {
/*     */       int i;
/* 603 */       while ((i = this.st.nextToken()) == 10) {
/* 604 */         this.linenum += 1;
/*     */       }
/* 606 */       return i;
/*     */     }
/*     */     
/*     */     private InputStream getInputStream(URL paramURL) throws IOException {
/* 610 */       if ("file".equalsIgnoreCase(paramURL.getProtocol()))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 627 */           return paramURL.openStream();
/*     */         } catch (Exception localException) {
/* 629 */           String str = paramURL.getPath();
/* 630 */           if (paramURL.getHost().length() > 0) {
/* 631 */             str = "//" + paramURL.getHost() + str;
/*     */           }
/* 633 */           if (debugConfig != null) {
/* 634 */             debugConfig.println("cannot read " + paramURL + ", try " + str);
/*     */           }
/*     */           
/* 637 */           return new FileInputStream(str);
/*     */         }
/*     */       }
/* 640 */       return paramURL.openStream();
/*     */     }
/*     */     
/*     */ 
/*     */     private String expand(String paramString)
/*     */       throws ExpandException, IOException
/*     */     {
/* 647 */       if (paramString.isEmpty()) {
/* 648 */         return paramString;
/*     */       }
/*     */       
/* 651 */       if (!this.expandProp) {
/* 652 */         return paramString;
/*     */       }
/* 654 */       String str = PropertyExpander.expand(paramString);
/* 655 */       if ((str == null) || (str.length() == 0)) {
/* 656 */         throw ioException("Configuration.Error.Line.line.system.property.value.expanded.to.empty.value", new Object[] { new Integer(this.linenum), paramString });
/*     */       }
/*     */       
/*     */ 
/* 660 */       return str;
/*     */     }
/*     */     
/*     */     private IOException ioException(String paramString, Object... paramVarArgs)
/*     */     {
/* 665 */       MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString(paramString, "sun.security.util.AuthResources"));
/* 666 */       return new IOException(localMessageFormat.format(paramVarArgs));
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\ConfigFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
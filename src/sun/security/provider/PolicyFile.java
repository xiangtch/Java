/*      */ package sun.security.provider;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.NetPermission;
/*      */ import java.net.SocketPermission;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.AllPermission;
/*      */ import java.security.CodeSource;
/*      */ import java.security.KeyStore;
/*      */ import java.security.KeyStoreException;
/*      */ import java.security.Permission;
/*      */ import java.security.PermissionCollection;
/*      */ import java.security.Permissions;
/*      */ import java.security.Policy;
/*      */ import java.security.Principal;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.security.Security;
/*      */ import java.security.UnresolvedPermission;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.PropertyPermission;
/*      */ import java.util.Random;
/*      */ import java.util.StringTokenizer;
/*      */ import javax.security.auth.Subject;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.misc.JavaSecurityProtectionDomainAccess;
/*      */ import sun.misc.JavaSecurityProtectionDomainAccess.ProtectionDomainCache;
/*      */ import sun.misc.SharedSecrets;
/*      */ import sun.net.www.ParseUtil;
/*      */ import sun.security.util.Debug;
/*      */ import sun.security.util.PolicyUtil;
/*      */ import sun.security.util.PropertyExpander;
/*      */ import sun.security.util.ResourcesMgr;
/*      */ import sun.security.util.SecurityConstants;
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
/*      */ public class PolicyFile
/*      */   extends Policy
/*      */ {
/*  259 */   private static final Debug debug = Debug.getInstance("policy");
/*      */   
/*      */   private static final String NONE = "NONE";
/*      */   
/*      */   private static final String P11KEYSTORE = "PKCS11";
/*      */   
/*      */   private static final String SELF = "${{self}}";
/*      */   
/*      */   private static final String X500PRINCIPAL = "javax.security.auth.x500.X500Principal";
/*      */   
/*      */   private static final String POLICY = "java.security.policy";
/*      */   
/*      */   private static final String SECURITY_MANAGER = "java.security.manager";
/*      */   
/*      */   private static final String POLICY_URL = "policy.url.";
/*      */   private static final String AUTH_POLICY = "java.security.auth.policy";
/*      */   private static final String AUTH_POLICY_URL = "auth.policy.url.";
/*      */   private static final int DEFAULT_CACHE_SIZE = 1;
/*      */   private volatile PolicyInfo policyInfo;
/*  278 */   private boolean constructed = false;
/*      */   
/*  280 */   private boolean expandProperties = true;
/*  281 */   private boolean ignoreIdentityScope = true;
/*  282 */   private boolean allowSystemProperties = true;
/*  283 */   private boolean notUtf8 = false;
/*      */   
/*      */ 
/*      */   private URL url;
/*      */   
/*  288 */   private static final Class[] PARAMS0 = new Class[0];
/*  289 */   private static final Class[] PARAMS1 = { String.class };
/*  290 */   private static final Class[] PARAMS2 = { String.class, String.class };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PolicyFile()
/*      */   {
/*  297 */     init((URL)null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PolicyFile(URL paramURL)
/*      */   {
/*  305 */     this.url = paramURL;
/*  306 */     init(paramURL);
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
/*      */   private void init(URL paramURL)
/*      */   {
/*  414 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public String run() {
/*  416 */         PolicyFile.this.expandProperties = "true"
/*  417 */           .equalsIgnoreCase(Security.getProperty("policy.expandProperties"));
/*  418 */         PolicyFile.this.ignoreIdentityScope = "true"
/*  419 */           .equalsIgnoreCase(Security.getProperty("policy.ignoreIdentityScope"));
/*  420 */         PolicyFile.this.allowSystemProperties = "true"
/*  421 */           .equalsIgnoreCase(Security.getProperty("policy.allowSystemProperty"));
/*  422 */         PolicyFile.this.notUtf8 = "false"
/*  423 */           .equalsIgnoreCase(System.getProperty("sun.security.policy.utf8"));
/*  424 */         return System.getProperty("sun.security.policy.numcaches");
/*      */       }
/*      */     });
/*      */     int i;
/*  428 */     if (str != null) {
/*      */       try {
/*  430 */         i = Integer.parseInt(str);
/*      */       } catch (NumberFormatException localNumberFormatException) {
/*  432 */         i = 1;
/*      */       }
/*      */     } else {
/*  435 */       i = 1;
/*      */     }
/*      */     
/*  438 */     PolicyInfo localPolicyInfo = new PolicyInfo(i);
/*  439 */     initPolicyFile(localPolicyInfo, paramURL);
/*  440 */     this.policyInfo = localPolicyInfo;
/*      */   }
/*      */   
/*      */   private void initPolicyFile(final PolicyInfo paramPolicyInfo, final URL paramURL)
/*      */   {
/*  445 */     if (paramURL != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  452 */       if (debug != null) {
/*  453 */         debug.println("reading " + paramURL);
/*      */       }
/*  455 */       AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Void run() {
/*  457 */           if (!PolicyFile.this.init(paramURL, paramPolicyInfo))
/*      */           {
/*  459 */             PolicyFile.this.initStaticPolicy(paramPolicyInfo);
/*      */           }
/*  461 */           return null;
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */       });
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  480 */       boolean bool = initPolicyFile("java.security.policy", "policy.url.", paramPolicyInfo);
/*      */       
/*      */ 
/*  483 */       if (!bool)
/*      */       {
/*  485 */         initStaticPolicy(paramPolicyInfo);
/*      */       }
/*      */       
/*  488 */       initPolicyFile("java.security.auth.policy", "auth.policy.url.", paramPolicyInfo);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean initPolicyFile(final String paramString1, final String paramString2, final PolicyInfo paramPolicyInfo)
/*      */   {
/*  495 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Boolean run() {
/*  497 */         boolean bool = false;
/*      */         Object localObject;
/*  499 */         if (PolicyFile.this.allowSystemProperties) {
/*  500 */           String str1 = System.getProperty(paramString1);
/*  501 */           if (str1 != null) {
/*  502 */             int j = 0;
/*  503 */             if (str1.startsWith("=")) {
/*  504 */               j = 1;
/*  505 */               str1 = str1.substring(1);
/*      */             }
/*      */             try
/*      */             {
/*  509 */               str1 = PropertyExpander.expand(str1);
/*      */               
/*      */ 
/*  512 */               localObject = new File(str1);
/*  513 */               URL localURL1; if (((File)localObject).exists())
/*      */               {
/*  515 */                 localURL1 = ParseUtil.fileToEncodedURL(new File(((File)localObject).getCanonicalPath()));
/*      */               } else {
/*  517 */                 localURL1 = new URL(str1);
/*      */               }
/*  519 */               if (PolicyFile.debug != null)
/*  520 */                 PolicyFile.debug.println("reading " + localURL1);
/*  521 */               if (PolicyFile.this.init(localURL1, paramPolicyInfo)) {
/*  522 */                 bool = true;
/*      */               }
/*      */             } catch (Exception localException1) {
/*  525 */               if (PolicyFile.debug != null) {
/*  526 */                 PolicyFile.debug.println("caught exception: " + localException1);
/*      */               }
/*      */             }
/*  529 */             if (j != 0) {
/*  530 */               if (PolicyFile.debug != null) {
/*  531 */                 PolicyFile.debug.println("overriding other policies!");
/*      */               }
/*  533 */               return Boolean.valueOf(bool);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  538 */         int i = 1;
/*      */         
/*      */         String str2;
/*  541 */         while ((str2 = Security.getProperty(paramString2 + i)) != null) {
/*      */           try {
/*  543 */             URL localURL2 = null;
/*      */             
/*  545 */             localObject = PropertyExpander.expand(str2).replace(File.separatorChar, '/');
/*      */             
/*  547 */             if ((str2.startsWith("file:${java.home}/")) || 
/*  548 */               (str2.startsWith("file:${user.home}/")))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  555 */               localURL2 = new File(((String)localObject).substring(5)).toURI().toURL();
/*      */             } else {
/*  557 */               localURL2 = new URI((String)localObject).toURL();
/*      */             }
/*      */             
/*  560 */             if (PolicyFile.debug != null)
/*  561 */               PolicyFile.debug.println("reading " + localURL2);
/*  562 */             if (PolicyFile.this.init(localURL2, paramPolicyInfo))
/*  563 */               bool = true;
/*      */           } catch (Exception localException2) {
/*  565 */             if (PolicyFile.debug != null) {
/*  566 */               PolicyFile.debug.println("error reading policy " + localException2);
/*  567 */               localException2.printStackTrace();
/*      */             }
/*      */           }
/*      */           
/*  571 */           i++;
/*      */         }
/*  573 */         return Boolean.valueOf(bool);
/*      */       }
/*      */       
/*  576 */     });
/*  577 */     return localBoolean.booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean init(URL paramURL, PolicyInfo paramPolicyInfo)
/*      */   {
/*  587 */     boolean bool = false;
/*  588 */     PolicyParser localPolicyParser = new PolicyParser(this.expandProperties);
/*  589 */     InputStreamReader localInputStreamReader = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  597 */       if (this.notUtf8)
/*      */       {
/*  599 */         localInputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURL));
/*      */       }
/*      */       else {
/*  602 */         localInputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURL), "UTF-8");
/*      */       }
/*      */       
/*  605 */       localPolicyParser.read(localInputStreamReader);
/*      */       
/*  607 */       KeyStore localKeyStore = null;
/*      */       try
/*      */       {
/*  610 */         localKeyStore = PolicyUtil.getKeyStore(paramURL, localPolicyParser
/*  611 */           .getKeyStoreUrl(), localPolicyParser
/*  612 */           .getKeyStoreType(), localPolicyParser
/*  613 */           .getKeyStoreProvider(), localPolicyParser
/*  614 */           .getStorePassURL(), debug);
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/*  618 */         if (debug != null) {
/*  619 */           localException2.printStackTrace();
/*      */         }
/*      */       }
/*      */       
/*  623 */       localObject1 = localPolicyParser.grantElements();
/*  624 */       while (((Enumeration)localObject1).hasMoreElements()) {
/*  625 */         localObject2 = (PolicyParser.GrantEntry)((Enumeration)localObject1).nextElement();
/*  626 */         addGrantEntry((PolicyParser.GrantEntry)localObject2, localKeyStore, paramPolicyInfo);
/*      */       }
/*      */     }
/*      */     catch (PolicyParser.ParsingException localParsingException) {
/*  630 */       Object localObject1 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.parsing.policy.message"));
/*  631 */       Object localObject2 = { paramURL, localParsingException.getLocalizedMessage() };
/*  632 */       System.err.println(((MessageFormat)localObject1).format(localObject2));
/*  633 */       if (debug != null) {
/*  634 */         localParsingException.printStackTrace();
/*      */       }
/*      */     } catch (Exception localException1) {
/*  637 */       if (debug != null) {
/*  638 */         debug.println("error parsing " + paramURL);
/*  639 */         debug.println(localException1.toString());
/*  640 */         localException1.printStackTrace();
/*      */       }
/*      */     } finally {
/*  643 */       if (localInputStreamReader != null) {
/*      */         try {
/*  645 */           localInputStreamReader.close();
/*  646 */           bool = true;
/*      */ 
/*      */         }
/*      */         catch (IOException localIOException4) {}
/*      */       } else {
/*  651 */         bool = true;
/*      */       }
/*      */     }
/*      */     
/*  655 */     return bool;
/*      */   }
/*      */   
/*      */   private void initStaticPolicy(final PolicyInfo paramPolicyInfo) {
/*  659 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/*  661 */         PolicyEntry localPolicyEntry = new PolicyEntry(new CodeSource(null, (Certificate[])null));
/*      */         
/*  663 */         localPolicyEntry.add(SecurityConstants.LOCAL_LISTEN_PERMISSION);
/*  664 */         localPolicyEntry.add(new PropertyPermission("java.version", "read"));
/*      */         
/*  666 */         localPolicyEntry.add(new PropertyPermission("java.vendor", "read"));
/*      */         
/*  668 */         localPolicyEntry.add(new PropertyPermission("java.vendor.url", "read"));
/*      */         
/*  670 */         localPolicyEntry.add(new PropertyPermission("java.class.version", "read"));
/*      */         
/*  672 */         localPolicyEntry.add(new PropertyPermission("os.name", "read"));
/*      */         
/*  674 */         localPolicyEntry.add(new PropertyPermission("os.version", "read"));
/*      */         
/*  676 */         localPolicyEntry.add(new PropertyPermission("os.arch", "read"));
/*      */         
/*  678 */         localPolicyEntry.add(new PropertyPermission("file.separator", "read"));
/*      */         
/*  680 */         localPolicyEntry.add(new PropertyPermission("path.separator", "read"));
/*      */         
/*  682 */         localPolicyEntry.add(new PropertyPermission("line.separator", "read"));
/*      */         
/*  684 */         localPolicyEntry.add(new PropertyPermission("java.specification.version", "read"));
/*      */         
/*      */ 
/*  687 */         localPolicyEntry.add(new PropertyPermission("java.specification.vendor", "read"));
/*      */         
/*      */ 
/*  690 */         localPolicyEntry.add(new PropertyPermission("java.specification.name", "read"));
/*      */         
/*      */ 
/*  693 */         localPolicyEntry.add(new PropertyPermission("java.vm.specification.version", "read"));
/*      */         
/*      */ 
/*  696 */         localPolicyEntry.add(new PropertyPermission("java.vm.specification.vendor", "read"));
/*      */         
/*      */ 
/*  699 */         localPolicyEntry.add(new PropertyPermission("java.vm.specification.name", "read"));
/*      */         
/*      */ 
/*  702 */         localPolicyEntry.add(new PropertyPermission("java.vm.version", "read"));
/*      */         
/*  704 */         localPolicyEntry.add(new PropertyPermission("java.vm.vendor", "read"));
/*      */         
/*  706 */         localPolicyEntry.add(new PropertyPermission("java.vm.name", "read"));
/*      */         
/*      */ 
/*      */ 
/*  710 */         paramPolicyInfo.policyEntries.add(localPolicyEntry);
/*      */         
/*      */ 
/*  713 */         String[] arrayOfString = PolicyParser.parseExtDirs("${{java.ext.dirs}}", 0);
/*      */         
/*  715 */         if ((arrayOfString != null) && (arrayOfString.length > 0)) {
/*  716 */           for (int i = 0; i < arrayOfString.length; i++) {
/*      */             try {
/*  718 */               localPolicyEntry = new PolicyEntry(PolicyFile.this.canonicalizeCodebase(new CodeSource(new URL(arrayOfString[i]), (Certificate[])null), false));
/*      */               
/*      */ 
/*  721 */               localPolicyEntry.add(SecurityConstants.ALL_PERMISSION);
/*      */               
/*      */ 
/*      */ 
/*  725 */               paramPolicyInfo.policyEntries.add(localPolicyEntry);
/*      */             }
/*      */             catch (Exception localException) {}
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  732 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private CodeSource getCodeSource(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore, PolicyInfo paramPolicyInfo)
/*      */     throws MalformedURLException
/*      */   {
/*  745 */     Certificate[] arrayOfCertificate = null;
/*  746 */     if (paramGrantEntry.signedBy != null) {
/*  747 */       arrayOfCertificate = getCertificates(paramKeyStore, paramGrantEntry.signedBy, paramPolicyInfo);
/*  748 */       if (arrayOfCertificate == null)
/*      */       {
/*      */ 
/*  751 */         if (debug != null) {
/*  752 */           debug.println("  -- No certs for alias '" + paramGrantEntry.signedBy + "' - ignoring entry");
/*      */         }
/*      */         
/*  755 */         return null;
/*      */       }
/*      */     }
/*      */     
/*      */     URL localURL;
/*      */     
/*  761 */     if (paramGrantEntry.codeBase != null) {
/*  762 */       localURL = new URL(paramGrantEntry.codeBase);
/*      */     } else {
/*  764 */       localURL = null;
/*      */     }
/*  766 */     return canonicalizeCodebase(new CodeSource(localURL, arrayOfCertificate), false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void addGrantEntry(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore, PolicyInfo paramPolicyInfo)
/*      */   {
/*      */     Object localObject1;
/*      */     
/*  775 */     if (debug != null) {
/*  776 */       debug.println("Adding policy entry: ");
/*  777 */       debug.println("  signedBy " + paramGrantEntry.signedBy);
/*  778 */       debug.println("  codeBase " + paramGrantEntry.codeBase);
/*  779 */       if (paramGrantEntry.principals != null) {
/*  780 */         for (localObject1 = paramGrantEntry.principals.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (PolicyParser.PrincipalEntry)((Iterator)localObject1).next();
/*  781 */           debug.println("  " + ((PolicyParser.PrincipalEntry)localObject2).toString());
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  787 */       localObject1 = getCodeSource(paramGrantEntry, paramKeyStore, paramPolicyInfo);
/*      */       
/*  789 */       if (localObject1 == null) { return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  795 */       if (!replacePrincipals(paramGrantEntry.principals, paramKeyStore))
/*  796 */         return;
/*  797 */       localObject2 = new PolicyEntry((CodeSource)localObject1, paramGrantEntry.principals);
/*      */       
/*  799 */       localObject3 = paramGrantEntry.permissionElements();
/*  800 */       while (((Enumeration)localObject3).hasMoreElements()) {
/*  801 */         PolicyParser.PermissionEntry localPermissionEntry = (PolicyParser.PermissionEntry)((Enumeration)localObject3).nextElement();
/*      */         
/*      */         try
/*      */         {
/*  805 */           expandPermissionName(localPermissionEntry, paramKeyStore);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  810 */           if ((localPermissionEntry.permission.equals("javax.security.auth.PrivateCredentialPermission")) && 
/*  811 */             (localPermissionEntry.name.endsWith(" self"))) {
/*  812 */             localPermissionEntry.name = (localPermissionEntry.name.substring(0, localPermissionEntry.name.indexOf("self")) + "${{self}}");
/*      */           }
/*      */           
/*      */           Object localObject4;
/*  816 */           if ((localPermissionEntry.name != null) && (localPermissionEntry.name.indexOf("${{self}}") != -1))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  822 */             if (localPermissionEntry.signedBy != null) {
/*  823 */               localObject5 = getCertificates(paramKeyStore, localPermissionEntry.signedBy, paramPolicyInfo);
/*      */             }
/*      */             else
/*      */             {
/*  827 */               localObject5 = null;
/*      */             }
/*  829 */             localObject4 = new SelfPermission(localPermissionEntry.permission, localPermissionEntry.name, localPermissionEntry.action, (Certificate[])localObject5);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  834 */             localObject4 = getInstance(localPermissionEntry.permission, localPermissionEntry.name, localPermissionEntry.action);
/*      */           }
/*      */           
/*      */ 
/*  838 */           ((PolicyEntry)localObject2).add((Permission)localObject4);
/*  839 */           if (debug != null) {
/*  840 */             debug.println("  " + localObject4);
/*      */           }
/*      */         }
/*      */         catch (ClassNotFoundException localClassNotFoundException) {
/*  844 */           if (localPermissionEntry.signedBy != null) {
/*  845 */             localObject5 = getCertificates(paramKeyStore, localPermissionEntry.signedBy, paramPolicyInfo);
/*      */           }
/*      */           else
/*      */           {
/*  849 */             localObject5 = null;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  854 */           if ((localObject5 != null) || (localPermissionEntry.signedBy == null)) {
/*  855 */             localObject6 = new UnresolvedPermission(localPermissionEntry.permission, localPermissionEntry.name, localPermissionEntry.action, (Certificate[])localObject5);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  860 */             ((PolicyEntry)localObject2).add((Permission)localObject6);
/*  861 */             if (debug != null) {
/*  862 */               debug.println("  " + localObject6);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (InvocationTargetException localInvocationTargetException)
/*      */         {
/*  868 */           localObject5 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message"));
/*      */           
/*      */ 
/*  871 */           localObject6 = new Object[] { localPermissionEntry.permission, localInvocationTargetException.getTargetException().toString() };
/*  872 */           System.err.println(((MessageFormat)localObject5).format(localObject6));
/*      */         }
/*      */         catch (Exception localException2)
/*      */         {
/*  876 */           Object localObject5 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message"));
/*      */           
/*      */ 
/*  879 */           Object localObject6 = { localPermissionEntry.permission, localException2.toString() };
/*  880 */           System.err.println(((MessageFormat)localObject5).format(localObject6));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  885 */       paramPolicyInfo.policyEntries.add(localObject2);
/*      */     }
/*      */     catch (Exception localException1) {
/*  888 */       Object localObject2 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Entry.message"));
/*      */       
/*  890 */       Object localObject3 = { localException1.toString() };
/*  891 */       System.err.println(((MessageFormat)localObject2).format(localObject3));
/*      */     }
/*  893 */     if (debug != null) {
/*  894 */       debug.println();
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
/*      */   private static final Permission getInstance(String paramString1, String paramString2, String paramString3)
/*      */     throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
/*      */   {
/*  937 */     Class localClass = Class.forName(paramString1, false, null);
/*  938 */     Permission localPermission = getKnownInstance(localClass, paramString2, paramString3);
/*  939 */     if (localPermission != null) {
/*  940 */       return localPermission;
/*      */     }
/*  942 */     if (!Permission.class.isAssignableFrom(localClass))
/*      */     {
/*  944 */       throw new ClassCastException(paramString1 + " is not a Permission");
/*      */     }
/*      */     
/*  947 */     if ((paramString2 == null) && (paramString3 == null)) {
/*      */       try {
/*  949 */         Constructor localConstructor1 = localClass.getConstructor(PARAMS0);
/*  950 */         return (Permission)localConstructor1.newInstance(new Object[0]);
/*      */       } catch (NoSuchMethodException localNoSuchMethodException1) {
/*      */         try {
/*  953 */           Constructor localConstructor4 = localClass.getConstructor(PARAMS1);
/*  954 */           return (Permission)localConstructor4.newInstance(new Object[] { paramString2 });
/*      */         }
/*      */         catch (NoSuchMethodException localNoSuchMethodException3) {
/*  957 */           Constructor localConstructor6 = localClass.getConstructor(PARAMS2);
/*  958 */           return (Permission)localConstructor6.newInstance(new Object[] { paramString2, paramString3 });
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  963 */     if ((paramString2 != null) && (paramString3 == null)) {
/*      */       try {
/*  965 */         Constructor localConstructor2 = localClass.getConstructor(PARAMS1);
/*  966 */         return (Permission)localConstructor2.newInstance(new Object[] { paramString2 });
/*      */       } catch (NoSuchMethodException localNoSuchMethodException2) {
/*  968 */         Constructor localConstructor5 = localClass.getConstructor(PARAMS2);
/*  969 */         return (Permission)localConstructor5.newInstance(new Object[] { paramString2, paramString3 });
/*      */       }
/*      */     }
/*      */     
/*  973 */     Constructor localConstructor3 = localClass.getConstructor(PARAMS2);
/*  974 */     return (Permission)localConstructor3.newInstance(new Object[] { paramString2, paramString3 });
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
/*      */   private static final Permission getKnownInstance(Class<?> paramClass, String paramString1, String paramString2)
/*      */   {
/*  987 */     if (paramClass.equals(FilePermission.class))
/*  988 */       return new FilePermission(paramString1, paramString2);
/*  989 */     if (paramClass.equals(SocketPermission.class))
/*  990 */       return new SocketPermission(paramString1, paramString2);
/*  991 */     if (paramClass.equals(RuntimePermission.class))
/*  992 */       return new RuntimePermission(paramString1, paramString2);
/*  993 */     if (paramClass.equals(PropertyPermission.class))
/*  994 */       return new PropertyPermission(paramString1, paramString2);
/*  995 */     if (paramClass.equals(NetPermission.class))
/*  996 */       return new NetPermission(paramString1, paramString2);
/*  997 */     if (paramClass.equals(AllPermission.class)) {
/*  998 */       return SecurityConstants.ALL_PERMISSION;
/*      */     }
/* 1000 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Certificate[] getCertificates(KeyStore paramKeyStore, String paramString, PolicyInfo paramPolicyInfo)
/*      */   {
/* 1010 */     ArrayList localArrayList = null;
/*      */     
/* 1012 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
/* 1013 */     int i = 0;
/*      */     Object localObject1;
/* 1015 */     while (localStringTokenizer.hasMoreTokens()) {
/* 1016 */       localObject1 = localStringTokenizer.nextToken().trim();
/* 1017 */       i++;
/* 1018 */       Certificate localCertificate = null;
/*      */       
/* 1020 */       synchronized (paramPolicyInfo.aliasMapping) {
/* 1021 */         localCertificate = (Certificate)paramPolicyInfo.aliasMapping.get(localObject1);
/*      */         
/* 1023 */         if ((localCertificate == null) && (paramKeyStore != null))
/*      */         {
/*      */           try {
/* 1026 */             localCertificate = paramKeyStore.getCertificate((String)localObject1);
/*      */           }
/*      */           catch (KeyStoreException localKeyStoreException) {}
/*      */           
/*      */ 
/* 1031 */           if (localCertificate != null) {
/* 1032 */             paramPolicyInfo.aliasMapping.put(localObject1, localCertificate);
/* 1033 */             paramPolicyInfo.aliasMapping.put(localCertificate, localObject1);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1038 */       if (localCertificate != null) {
/* 1039 */         if (localArrayList == null)
/* 1040 */           localArrayList = new ArrayList();
/* 1041 */         localArrayList.add(localCertificate);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1046 */     if ((localArrayList != null) && (i == localArrayList.size())) {
/* 1047 */       localObject1 = new Certificate[localArrayList.size()];
/* 1048 */       localArrayList.toArray((Object[])localObject1);
/* 1049 */       return (Certificate[])localObject1;
/*      */     }
/* 1051 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void refresh()
/*      */   {
/* 1059 */     init(this.url);
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
/*      */   public boolean implies(ProtectionDomain paramProtectionDomain, Permission paramPermission)
/*      */   {
/* 1077 */     ProtectionDomainCache localProtectionDomainCache = this.policyInfo.getPdMapping();
/* 1078 */     PermissionCollection localPermissionCollection = localProtectionDomainCache.get(paramProtectionDomain);
/*      */     
/* 1080 */     if (localPermissionCollection != null) {
/* 1081 */       return localPermissionCollection.implies(paramPermission);
/*      */     }
/*      */     
/* 1084 */     localPermissionCollection = getPermissions(paramProtectionDomain);
/* 1085 */     if (localPermissionCollection == null) {
/* 1086 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1090 */     localProtectionDomainCache.put(paramProtectionDomain, localPermissionCollection);
/* 1091 */     return localPermissionCollection.implies(paramPermission);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PermissionCollection getPermissions(ProtectionDomain paramProtectionDomain)
/*      */   {
/* 1124 */     Permissions localPermissions = new Permissions();
/*      */     
/* 1126 */     if (paramProtectionDomain == null) {
/* 1127 */       return localPermissions;
/*      */     }
/*      */     
/* 1130 */     getPermissions(localPermissions, paramProtectionDomain);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1135 */     PermissionCollection localPermissionCollection = paramProtectionDomain.getPermissions();
/* 1136 */     if (localPermissionCollection != null) {
/* 1137 */       synchronized (localPermissionCollection) {
/* 1138 */         Enumeration localEnumeration = localPermissionCollection.elements();
/* 1139 */         while (localEnumeration.hasMoreElements()) {
/* 1140 */           localPermissions.add((Permission)localEnumeration.nextElement());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1145 */     return localPermissions;
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
/*      */   public PermissionCollection getPermissions(CodeSource paramCodeSource)
/*      */   {
/* 1160 */     return getPermissions(new Permissions(), paramCodeSource);
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
/*      */   private PermissionCollection getPermissions(Permissions paramPermissions, ProtectionDomain paramProtectionDomain)
/*      */   {
/* 1175 */     if (debug != null) {
/* 1176 */       debug.println("getPermissions:\n\t" + printPD(paramProtectionDomain));
/*      */     }
/*      */     
/* 1179 */     final CodeSource localCodeSource1 = paramProtectionDomain.getCodeSource();
/* 1180 */     if (localCodeSource1 == null) {
/* 1181 */       return paramPermissions;
/*      */     }
/* 1183 */     CodeSource localCodeSource2 = (CodeSource)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public CodeSource run() {
/* 1186 */         return PolicyFile.this.canonicalizeCodebase(localCodeSource1, true);
/*      */       }
/* 1188 */     });
/* 1189 */     return getPermissions(paramPermissions, localCodeSource2, paramProtectionDomain.getPrincipals());
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
/*      */   private PermissionCollection getPermissions(Permissions paramPermissions, final CodeSource paramCodeSource)
/*      */   {
/* 1207 */     if (paramCodeSource == null) {
/* 1208 */       return paramPermissions;
/*      */     }
/* 1210 */     CodeSource localCodeSource = (CodeSource)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public CodeSource run() {
/* 1213 */         return PolicyFile.this.canonicalizeCodebase(paramCodeSource, true);
/*      */       }
/*      */       
/* 1216 */     });
/* 1217 */     return getPermissions(paramPermissions, localCodeSource, null);
/*      */   }
/*      */   
/*      */ 
/*      */   private Permissions getPermissions(Permissions paramPermissions, CodeSource paramCodeSource, Principal[] paramArrayOfPrincipal)
/*      */   {
/* 1223 */     PolicyInfo localPolicyInfo = this.policyInfo;
/*      */     
/* 1225 */     for (Iterator localIterator = localPolicyInfo.policyEntries.iterator(); localIterator.hasNext();) { localObject1 = (PolicyEntry)localIterator.next();
/* 1226 */       addPermissions(paramPermissions, paramCodeSource, paramArrayOfPrincipal, (PolicyEntry)localObject1);
/*      */     }
/*      */     
/*      */     Object localObject1;
/*      */     Object localObject2;
/* 1231 */     synchronized (localPolicyInfo.identityPolicyEntries) {
/* 1232 */       for (localObject1 = localPolicyInfo.identityPolicyEntries.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (PolicyEntry)((Iterator)localObject1).next();
/* 1233 */         addPermissions(paramPermissions, paramCodeSource, paramArrayOfPrincipal, (PolicyEntry)localObject2);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1238 */     if (!this.ignoreIdentityScope) {
/* 1239 */       ??? = paramCodeSource.getCertificates();
/* 1240 */       if (??? != null) {
/* 1241 */         for (int i = 0; i < ???.length; i++) {
/* 1242 */           localObject2 = localPolicyInfo.aliasMapping.get(???[i]);
/* 1243 */           if ((localObject2 == null) && 
/* 1244 */             (checkForTrustedIdentity(???[i], localPolicyInfo)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1249 */             paramPermissions.add(SecurityConstants.ALL_PERMISSION);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1254 */     return paramPermissions;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addPermissions(Permissions paramPermissions, final CodeSource paramCodeSource, Principal[] paramArrayOfPrincipal, final PolicyEntry paramPolicyEntry)
/*      */   {
/* 1262 */     if (debug != null) {
/* 1263 */       debug.println("evaluate codesources:\n\tPolicy CodeSource: " + paramPolicyEntry
/* 1264 */         .getCodeSource() + "\n\tActive CodeSource: " + paramCodeSource);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1270 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Boolean run() {
/* 1272 */         return new Boolean(paramPolicyEntry.getCodeSource().implies(paramCodeSource));
/*      */       }
/*      */     });
/* 1275 */     if (!localBoolean.booleanValue()) {
/* 1276 */       if (debug != null) {
/* 1277 */         debug.println("evaluation (codesource) failed");
/*      */       }
/*      */       
/*      */ 
/* 1281 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1286 */     List localList = paramPolicyEntry.getPrincipals();
/* 1287 */     if (debug != null) {
/* 1288 */       localObject = new ArrayList();
/* 1289 */       if (paramArrayOfPrincipal != null) {
/* 1290 */         for (int i = 0; i < paramArrayOfPrincipal.length; i++) {
/* 1291 */           ((List)localObject).add(new PolicyParser.PrincipalEntry(paramArrayOfPrincipal[i]
/* 1292 */             .getClass().getName(), paramArrayOfPrincipal[i]
/* 1293 */             .getName()));
/*      */         }
/*      */       }
/* 1296 */       debug.println("evaluate principals:\n\tPolicy Principals: " + localList + "\n\tActive Principals: " + localObject);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1301 */     if ((localList == null) || (localList.isEmpty()))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1306 */       addPerms(paramPermissions, paramArrayOfPrincipal, paramPolicyEntry);
/* 1307 */       if (debug != null) {
/* 1308 */         debug.println("evaluation (codesource/principals) passed");
/*      */       }
/* 1310 */       return;
/*      */     }
/* 1312 */     if ((paramArrayOfPrincipal == null) || (paramArrayOfPrincipal.length == 0))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1317 */       if (debug != null) {
/* 1318 */         debug.println("evaluation (principals) failed");
/*      */       }
/* 1320 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1327 */     for (Object localObject = localList.iterator(); ((Iterator)localObject).hasNext();) { PolicyParser.PrincipalEntry localPrincipalEntry = (PolicyParser.PrincipalEntry)((Iterator)localObject).next();
/*      */       
/*      */ 
/* 1330 */       if (!localPrincipalEntry.isWildcardClass())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1335 */         if (localPrincipalEntry.isWildcardName())
/*      */         {
/* 1337 */           if (!wildcardPrincipalNameImplies(localPrincipalEntry.principalClass, paramArrayOfPrincipal))
/*      */           {
/*      */ 
/*      */ 
/* 1341 */             if (debug != null) {
/* 1342 */               debug.println("evaluation (principal name wildcard) failed");
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1349 */           HashSet localHashSet = new HashSet(Arrays.asList(paramArrayOfPrincipal));
/* 1350 */           Subject localSubject = new Subject(true, localHashSet, Collections.EMPTY_SET, Collections.EMPTY_SET);
/*      */           
/*      */           try
/*      */           {
/* 1354 */             ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/* 1355 */             Class localClass = Class.forName(localPrincipalEntry.principalClass, false, localClassLoader);
/* 1356 */             if (!Principal.class.isAssignableFrom(localClass))
/*      */             {
/* 1358 */               throw new ClassCastException(localPrincipalEntry.principalClass + " is not a Principal");
/*      */             }
/*      */             
/*      */ 
/* 1362 */             Constructor localConstructor = localClass.getConstructor(PARAMS1);
/* 1363 */             Principal localPrincipal = (Principal)localConstructor.newInstance(new Object[] { localPrincipalEntry.principalName });
/*      */             
/*      */ 
/* 1366 */             if (debug != null) {
/* 1367 */               debug.println("found Principal " + localPrincipal.getClass().getName());
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1372 */             if (!localPrincipal.implies(localSubject)) {
/* 1373 */               if (debug != null) {
/* 1374 */                 debug.println("evaluation (principal implies) failed");
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 1379 */               return;
/*      */             }
/*      */             
/*      */           }
/*      */           catch (Exception localException)
/*      */           {
/* 1385 */             if (debug != null) {
/* 1386 */               localException.printStackTrace();
/*      */             }
/*      */             
/* 1389 */             if (!localPrincipalEntry.implies(localSubject)) {
/* 1390 */               if (debug != null) {
/* 1391 */                 debug.println("evaluation (default principal implies) failed");
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 1396 */               return;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1408 */     if (debug != null) {
/* 1409 */       debug.println("evaluation (codesource/principals) passed");
/*      */     }
/* 1411 */     addPerms(paramPermissions, paramArrayOfPrincipal, paramPolicyEntry);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean wildcardPrincipalNameImplies(String paramString, Principal[] paramArrayOfPrincipal)
/*      */   {
/* 1421 */     for (Principal localPrincipal : paramArrayOfPrincipal) {
/* 1422 */       if (paramString.equals(localPrincipal.getClass().getName())) {
/* 1423 */         return true;
/*      */       }
/*      */     }
/* 1426 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   private void addPerms(Permissions paramPermissions, Principal[] paramArrayOfPrincipal, PolicyEntry paramPolicyEntry)
/*      */   {
/* 1432 */     for (int i = 0; i < paramPolicyEntry.permissions.size(); i++) {
/* 1433 */       Permission localPermission = (Permission)paramPolicyEntry.permissions.get(i);
/* 1434 */       if (debug != null) {
/* 1435 */         debug.println("  granting " + localPermission);
/*      */       }
/*      */       
/* 1438 */       if ((localPermission instanceof SelfPermission))
/*      */       {
/* 1440 */         expandSelf((SelfPermission)localPermission, paramPolicyEntry
/* 1441 */           .getPrincipals(), paramArrayOfPrincipal, paramPermissions);
/*      */       }
/*      */       else
/*      */       {
/* 1445 */         paramPermissions.add(localPermission);
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
/*      */   private void expandSelf(SelfPermission paramSelfPermission, List<PolicyParser.PrincipalEntry> paramList, Principal[] paramArrayOfPrincipal, Permissions paramPermissions)
/*      */   {
/* 1468 */     if ((paramList == null) || (paramList.isEmpty()))
/*      */     {
/* 1470 */       if (debug != null) {
/* 1471 */         debug.println("Ignoring permission " + paramSelfPermission
/* 1472 */           .getSelfType() + " with target name (" + paramSelfPermission
/*      */           
/* 1474 */           .getSelfName() + ").  No Principal(s) specified in the grant clause.  SELF-based target names are only valid in the context of a Principal-based grant entry.");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1482 */       return;
/*      */     }
/* 1484 */     int i = 0;
/*      */     
/* 1486 */     StringBuilder localStringBuilder = new StringBuilder();
/* 1487 */     int j; Object localObject1; while ((j = paramSelfPermission.getSelfName().indexOf("${{self}}", i)) != -1)
/*      */     {
/*      */ 
/* 1490 */       localStringBuilder.append(paramSelfPermission.getSelfName().substring(i, j));
/*      */       
/*      */ 
/* 1493 */       Iterator localIterator = paramList.iterator();
/* 1494 */       while (localIterator.hasNext()) {
/* 1495 */         localObject1 = (PolicyParser.PrincipalEntry)localIterator.next();
/* 1496 */         String[][] arrayOfString = getPrincipalInfo((PolicyParser.PrincipalEntry)localObject1, paramArrayOfPrincipal);
/* 1497 */         for (int k = 0; k < arrayOfString.length; k++) {
/* 1498 */           if (k != 0) {
/* 1499 */             localStringBuilder.append(", ");
/*      */           }
/* 1501 */           localStringBuilder.append(arrayOfString[k][0] + " \"" + arrayOfString[k][1] + "\"");
/*      */         }
/*      */         
/* 1504 */         if (localIterator.hasNext()) {
/* 1505 */           localStringBuilder.append(", ");
/*      */         }
/*      */       }
/* 1508 */       i = j + "${{self}}".length();
/*      */     }
/*      */     
/* 1511 */     localStringBuilder.append(paramSelfPermission.getSelfName().substring(i));
/*      */     
/* 1513 */     if (debug != null) {
/* 1514 */       debug.println("  expanded:\n\t" + paramSelfPermission.getSelfName() + "\n  into:\n\t" + localStringBuilder
/* 1515 */         .toString());
/*      */     }
/*      */     try
/*      */     {
/* 1519 */       paramPermissions.add(getInstance(paramSelfPermission.getSelfType(), localStringBuilder
/* 1520 */         .toString(), paramSelfPermission
/* 1521 */         .getSelfActions()));
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException)
/*      */     {
/*      */ 
/* 1528 */       localObject1 = null;
/* 1529 */       synchronized (paramPermissions) {
/* 1530 */         Enumeration localEnumeration = paramPermissions.elements();
/* 1531 */         while (localEnumeration.hasMoreElements()) {
/* 1532 */           Permission localPermission = (Permission)localEnumeration.nextElement();
/* 1533 */           if (localPermission.getClass().getName().equals(paramSelfPermission.getSelfType())) {
/* 1534 */             localObject1 = localPermission.getClass();
/* 1535 */             break;
/*      */           }
/*      */         }
/*      */       }
/* 1539 */       if (localObject1 == null)
/*      */       {
/* 1541 */         paramPermissions.add(new UnresolvedPermission(paramSelfPermission.getSelfType(), localStringBuilder
/* 1542 */           .toString(), paramSelfPermission
/* 1543 */           .getSelfActions(), paramSelfPermission
/* 1544 */           .getCerts()));
/*      */       }
/*      */       else
/*      */       {
/*      */         try
/*      */         {
/*      */ 
/* 1551 */           if (paramSelfPermission.getSelfActions() == null) {
/*      */             try {
/* 1553 */               ??? = ((Class)localObject1).getConstructor(PARAMS1);
/* 1554 */               paramPermissions.add(
/* 1555 */                 (Permission)((Constructor)???).newInstance(new Object[] {localStringBuilder.toString() }));
/*      */             } catch (NoSuchMethodException localNoSuchMethodException) {
/* 1557 */               ??? = ((Class)localObject1).getConstructor(PARAMS2);
/* 1558 */               paramPermissions.add(
/* 1559 */                 (Permission)((Constructor)???).newInstance(new Object[] {localStringBuilder.toString(), paramSelfPermission
/* 1560 */                 .getSelfActions() }));
/*      */             }
/*      */           } else {
/* 1563 */             ??? = ((Class)localObject1).getConstructor(PARAMS2);
/* 1564 */             paramPermissions.add(
/* 1565 */               (Permission)((Constructor)???).newInstance(new Object[] {localStringBuilder.toString(), paramSelfPermission
/* 1566 */               .getSelfActions() }));
/*      */           }
/*      */         } catch (Exception localException2) {
/* 1569 */           if (debug != null) {
/* 1570 */             debug.println("self entry expansion  instantiation failed: " + localException2
/*      */             
/* 1572 */               .toString());
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Exception localException1) {
/* 1577 */       if (debug != null) {
/* 1578 */         debug.println(localException1.toString());
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
/*      */   private String[][] getPrincipalInfo(PolicyParser.PrincipalEntry paramPrincipalEntry, Principal[] paramArrayOfPrincipal)
/*      */   {
/* 1597 */     if ((!paramPrincipalEntry.isWildcardClass()) && (!paramPrincipalEntry.isWildcardName()))
/*      */     {
/*      */ 
/*      */ 
/* 1601 */       localObject = new String[1][2];
/* 1602 */       localObject[0][0] = paramPrincipalEntry.principalClass;
/* 1603 */       localObject[0][1] = paramPrincipalEntry.principalName;
/* 1604 */       return (String[][])localObject;
/*      */     }
/* 1606 */     if ((!paramPrincipalEntry.isWildcardClass()) && (paramPrincipalEntry.isWildcardName()))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1611 */       localObject = new ArrayList();
/* 1612 */       for (int i = 0; i < paramArrayOfPrincipal.length; i++) {
/* 1613 */         if (paramPrincipalEntry.principalClass.equals(paramArrayOfPrincipal[i].getClass().getName()))
/* 1614 */           ((List)localObject).add(paramArrayOfPrincipal[i]);
/*      */       }
/* 1616 */       String[][] arrayOfString = new String[((List)localObject).size()][2];
/* 1617 */       int k = 0;
/* 1618 */       for (Principal localPrincipal : (List)localObject) {
/* 1619 */         arrayOfString[k][0] = localPrincipal.getClass().getName();
/* 1620 */         arrayOfString[k][1] = localPrincipal.getName();
/* 1621 */         k++;
/*      */       }
/* 1623 */       return arrayOfString;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1630 */     Object localObject = new String[paramArrayOfPrincipal.length][2];
/*      */     
/* 1632 */     for (int j = 0; j < paramArrayOfPrincipal.length; j++) {
/* 1633 */       localObject[j][0] = paramArrayOfPrincipal[j].getClass().getName();
/* 1634 */       localObject[j][1] = paramArrayOfPrincipal[j].getName();
/*      */     }
/* 1636 */     return (String[][])localObject;
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
/*      */   protected Certificate[] getSignerCertificates(CodeSource paramCodeSource)
/*      */   {
/* 1655 */     Certificate[] arrayOfCertificate1 = null;
/* 1656 */     if ((arrayOfCertificate1 = paramCodeSource.getCertificates()) == null)
/* 1657 */       return null;
/* 1658 */     for (int i = 0; i < arrayOfCertificate1.length; i++) {
/* 1659 */       if (!(arrayOfCertificate1[i] instanceof X509Certificate)) {
/* 1660 */         return paramCodeSource.getCertificates();
/*      */       }
/*      */     }
/*      */     
/* 1664 */     i = 0;
/* 1665 */     int j = 0;
/* 1666 */     while (i < arrayOfCertificate1.length) {
/* 1667 */       j++;
/* 1668 */       while ((i + 1 < arrayOfCertificate1.length) && 
/* 1669 */         (((X509Certificate)arrayOfCertificate1[i]).getIssuerDN().equals(((X509Certificate)arrayOfCertificate1[(i + 1)])
/* 1670 */         .getSubjectDN()))) {
/* 1671 */         i++;
/*      */       }
/* 1673 */       i++;
/*      */     }
/* 1675 */     if (j == arrayOfCertificate1.length)
/*      */     {
/* 1677 */       return arrayOfCertificate1;
/*      */     }
/* 1679 */     ArrayList localArrayList = new ArrayList();
/* 1680 */     i = 0;
/* 1681 */     while (i < arrayOfCertificate1.length) {
/* 1682 */       localArrayList.add(arrayOfCertificate1[i]);
/* 1683 */       while ((i + 1 < arrayOfCertificate1.length) && 
/* 1684 */         (((X509Certificate)arrayOfCertificate1[i]).getIssuerDN().equals(((X509Certificate)arrayOfCertificate1[(i + 1)])
/* 1685 */         .getSubjectDN()))) {
/* 1686 */         i++;
/*      */       }
/* 1688 */       i++;
/*      */     }
/* 1690 */     Certificate[] arrayOfCertificate2 = new Certificate[localArrayList.size()];
/* 1691 */     localArrayList.toArray(arrayOfCertificate2);
/* 1692 */     return arrayOfCertificate2;
/*      */   }
/*      */   
/*      */ 
/*      */   private CodeSource canonicalizeCodebase(CodeSource paramCodeSource, boolean paramBoolean)
/*      */   {
/* 1698 */     String str1 = null;
/*      */     
/* 1700 */     CodeSource localCodeSource = paramCodeSource;
/* 1701 */     URL localURL1 = paramCodeSource.getLocation();
/* 1702 */     if (localURL1 != null) {
/* 1703 */       if (localURL1.getProtocol().equals("jar"))
/*      */       {
/* 1705 */         String str2 = localURL1.getFile();
/* 1706 */         int j = str2.indexOf("!/");
/* 1707 */         if (j != -1) {
/*      */           try {
/* 1709 */             localURL1 = new URL(str2.substring(0, j));
/*      */           }
/*      */           catch (MalformedURLException localMalformedURLException) {}
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1716 */       if (localURL1.getProtocol().equals("file")) {
/* 1717 */         int i = 0;
/* 1718 */         String str3 = localURL1.getHost();
/*      */         
/* 1720 */         i = (str3 == null) || (str3.equals("")) || (str3.equals("~")) || (str3.equalsIgnoreCase("localhost")) ? 1 : 0;
/*      */         
/* 1722 */         if (i != 0) {
/* 1723 */           str1 = localURL1.getFile().replace('/', File.separatorChar);
/* 1724 */           str1 = ParseUtil.decode(str1);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1729 */     if (str1 != null) {
/*      */       try {
/* 1731 */         URL localURL2 = null;
/* 1732 */         str1 = canonPath(str1);
/* 1733 */         localURL2 = ParseUtil.fileToEncodedURL(new File(str1));
/*      */         
/* 1735 */         if (paramBoolean)
/*      */         {
/* 1737 */           localCodeSource = new CodeSource(localURL2, getSignerCertificates(paramCodeSource));
/*      */         }
/*      */         else {
/* 1740 */           localCodeSource = new CodeSource(localURL2, paramCodeSource.getCertificates());
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1745 */         if (paramBoolean)
/*      */         {
/* 1747 */           localCodeSource = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
/*      */         }
/*      */         
/*      */       }
/* 1751 */     } else if (paramBoolean)
/*      */     {
/* 1753 */       localCodeSource = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
/*      */     }
/*      */     
/* 1756 */     return localCodeSource;
/*      */   }
/*      */   
/*      */   private static String canonPath(String paramString)
/*      */     throws IOException
/*      */   {
/* 1762 */     if (paramString.endsWith("*")) {
/* 1763 */       paramString = paramString.substring(0, paramString.length() - 1) + "-";
/* 1764 */       paramString = new File(paramString).getCanonicalPath();
/* 1765 */       return paramString.substring(0, paramString.length() - 1) + "*";
/*      */     }
/* 1767 */     return new File(paramString).getCanonicalPath();
/*      */   }
/*      */   
/*      */   private String printPD(ProtectionDomain paramProtectionDomain)
/*      */   {
/* 1772 */     Principal[] arrayOfPrincipal = paramProtectionDomain.getPrincipals();
/* 1773 */     String str = "<no principals>";
/* 1774 */     if ((arrayOfPrincipal != null) && (arrayOfPrincipal.length > 0)) {
/* 1775 */       StringBuilder localStringBuilder = new StringBuilder("(principals ");
/* 1776 */       for (int i = 0; i < arrayOfPrincipal.length; i++) {
/* 1777 */         localStringBuilder.append(arrayOfPrincipal[i].getClass().getName() + " \"" + arrayOfPrincipal[i]
/* 1778 */           .getName() + "\"");
/*      */         
/* 1780 */         if (i < arrayOfPrincipal.length - 1) {
/* 1781 */           localStringBuilder.append(", ");
/*      */         } else
/* 1783 */           localStringBuilder.append(")");
/*      */       }
/* 1785 */       str = localStringBuilder.toString();
/*      */     }
/* 1787 */     return 
/*      */     
/*      */ 
/* 1790 */       "PD CodeSource: " + paramProtectionDomain.getCodeSource() + "\n\tPD ClassLoader: " + paramProtectionDomain.getClassLoader() + "\n\tPD Principals: " + str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean replacePrincipals(List<PolicyParser.PrincipalEntry> paramList, KeyStore paramKeyStore)
/*      */   {
/* 1802 */     if ((paramList == null) || (paramList.isEmpty()) || (paramKeyStore == null)) {
/* 1803 */       return true;
/*      */     }
/* 1805 */     for (PolicyParser.PrincipalEntry localPrincipalEntry : paramList) {
/* 1806 */       if (localPrincipalEntry.isReplaceName())
/*      */       {
/*      */         String str;
/*      */         
/*      */ 
/* 1811 */         if ((str = getDN(localPrincipalEntry.principalName, paramKeyStore)) == null) {
/* 1812 */           return false;
/*      */         }
/*      */         
/* 1815 */         if (debug != null) {
/* 1816 */           debug.println("  Replacing \"" + localPrincipalEntry.principalName + "\" with " + "javax.security.auth.x500.X500Principal" + "/\"" + str + "\"");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1824 */         localPrincipalEntry.principalClass = "javax.security.auth.x500.X500Principal";
/* 1825 */         localPrincipalEntry.principalName = str;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1830 */     return true;
/*      */   }
/*      */   
/*      */   private void expandPermissionName(PolicyParser.PermissionEntry paramPermissionEntry, KeyStore paramKeyStore)
/*      */     throws Exception
/*      */   {
/* 1836 */     if ((paramPermissionEntry.name == null) || (paramPermissionEntry.name.indexOf("${{", 0) == -1)) {
/* 1837 */       return;
/*      */     }
/*      */     
/* 1840 */     int i = 0;
/*      */     
/* 1842 */     StringBuilder localStringBuilder = new StringBuilder();
/* 1843 */     int j; while ((j = paramPermissionEntry.name.indexOf("${{", i)) != -1) {
/* 1844 */       int k = paramPermissionEntry.name.indexOf("}}", j);
/* 1845 */       if (k < 1) {
/*      */         break;
/*      */       }
/* 1848 */       localStringBuilder.append(paramPermissionEntry.name.substring(i, j));
/*      */       
/*      */ 
/* 1851 */       String str1 = paramPermissionEntry.name.substring(j + 3, k);
/*      */       
/*      */ 
/*      */ 
/* 1855 */       String str2 = str1;
/*      */       int m;
/* 1857 */       if ((m = str1.indexOf(":")) != -1) {
/* 1858 */         str2 = str1.substring(0, m);
/*      */       }
/*      */       
/*      */ 
/* 1862 */       if (str2.equalsIgnoreCase("self"))
/*      */       {
/* 1864 */         localStringBuilder.append(paramPermissionEntry.name.substring(j, k + 2));
/* 1865 */         i = k + 2; } else { MessageFormat localMessageFormat;
/*      */         Object[] arrayOfObject;
/* 1867 */         if (str2.equalsIgnoreCase("alias"))
/*      */         {
/* 1869 */           if (m == -1)
/*      */           {
/*      */ 
/* 1872 */             localMessageFormat = new MessageFormat(ResourcesMgr.getString("alias.name.not.provided.pe.name."));
/* 1873 */             arrayOfObject = new Object[] { paramPermissionEntry.name };
/* 1874 */             throw new Exception(localMessageFormat.format(arrayOfObject));
/*      */           }
/* 1876 */           String str3 = str1.substring(m + 1);
/* 1877 */           if ((str3 = getDN(str3, paramKeyStore)) == null)
/*      */           {
/*      */ 
/* 1880 */             localMessageFormat = new MessageFormat(ResourcesMgr.getString("unable.to.perform.substitution.on.alias.suffix"));
/* 1881 */             arrayOfObject = new Object[] { str1.substring(m + 1) };
/* 1882 */             throw new Exception(localMessageFormat.format(arrayOfObject));
/*      */           }
/*      */           
/* 1885 */           localStringBuilder.append("javax.security.auth.x500.X500Principal \"" + str3 + "\"");
/* 1886 */           i = k + 2;
/*      */         }
/*      */         else
/*      */         {
/* 1890 */           localMessageFormat = new MessageFormat(ResourcesMgr.getString("substitution.value.prefix.unsupported"));
/* 1891 */           arrayOfObject = new Object[] { str2 };
/* 1892 */           throw new Exception(localMessageFormat.format(arrayOfObject));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1897 */     localStringBuilder.append(paramPermissionEntry.name.substring(i));
/*      */     
/*      */ 
/* 1900 */     if (debug != null) {
/* 1901 */       debug.println("  Permission name expanded from:\n\t" + paramPermissionEntry.name + "\nto\n\t" + localStringBuilder
/* 1902 */         .toString());
/*      */     }
/* 1904 */     paramPermissionEntry.name = localStringBuilder.toString();
/*      */   }
/*      */   
/*      */   private String getDN(String paramString, KeyStore paramKeyStore) {
/* 1908 */     Certificate localCertificate = null;
/*      */     try {
/* 1910 */       localCertificate = paramKeyStore.getCertificate(paramString);
/*      */     } catch (Exception localException) {
/* 1912 */       if (debug != null) {
/* 1913 */         debug.println("  Error retrieving certificate for '" + paramString + "': " + localException
/*      */         
/*      */ 
/* 1916 */           .toString());
/*      */       }
/* 1918 */       return null;
/*      */     }
/*      */     
/* 1921 */     if ((localCertificate == null) || (!(localCertificate instanceof X509Certificate))) {
/* 1922 */       if (debug != null) {
/* 1923 */         debug.println("  -- No certificate for '" + paramString + "' - ignoring entry");
/*      */       }
/*      */       
/*      */ 
/* 1927 */       return null;
/*      */     }
/* 1929 */     X509Certificate localX509Certificate = (X509Certificate)localCertificate;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1936 */     X500Principal localX500Principal = new X500Principal(localX509Certificate.getSubjectX500Principal().toString());
/* 1937 */     return localX500Principal.getName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean checkForTrustedIdentity(Certificate paramCertificate, PolicyInfo paramPolicyInfo)
/*      */   {
/* 1949 */     return false;
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
/*      */   private static class PolicyEntry
/*      */   {
/*      */     private final CodeSource codesource;
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
/*      */     final List<Permission> permissions;
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
/*      */     private final List<PolicyParser.PrincipalEntry> principals;
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
/*      */     PolicyEntry(CodeSource paramCodeSource, List<PolicyParser.PrincipalEntry> paramList)
/*      */     {
/* 2017 */       this.codesource = paramCodeSource;
/* 2018 */       this.permissions = new ArrayList();
/* 2019 */       this.principals = paramList;
/*      */     }
/*      */     
/*      */     PolicyEntry(CodeSource paramCodeSource)
/*      */     {
/* 2024 */       this(paramCodeSource, null);
/*      */     }
/*      */     
/*      */     List<PolicyParser.PrincipalEntry> getPrincipals() {
/* 2028 */       return this.principals;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     void add(Permission paramPermission)
/*      */     {
/* 2037 */       this.permissions.add(paramPermission);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     CodeSource getCodeSource()
/*      */     {
/* 2044 */       return this.codesource;
/*      */     }
/*      */     
/*      */     public String toString() {
/* 2048 */       StringBuilder localStringBuilder = new StringBuilder();
/* 2049 */       localStringBuilder.append(ResourcesMgr.getString("LPARAM"));
/* 2050 */       localStringBuilder.append(getCodeSource());
/* 2051 */       localStringBuilder.append("\n");
/* 2052 */       for (int i = 0; i < this.permissions.size(); i++) {
/* 2053 */         Permission localPermission = (Permission)this.permissions.get(i);
/* 2054 */         localStringBuilder.append(ResourcesMgr.getString("SPACE"));
/* 2055 */         localStringBuilder.append(ResourcesMgr.getString("SPACE"));
/* 2056 */         localStringBuilder.append(localPermission);
/* 2057 */         localStringBuilder.append(ResourcesMgr.getString("NEWLINE"));
/*      */       }
/* 2059 */       localStringBuilder.append(ResourcesMgr.getString("RPARAM"));
/* 2060 */       localStringBuilder.append(ResourcesMgr.getString("NEWLINE"));
/* 2061 */       return localStringBuilder.toString();
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
/*      */   private static class SelfPermission
/*      */     extends Permission
/*      */   {
/*      */     private static final long serialVersionUID = -8315562579967246806L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private String type;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private String name;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private String actions;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private Certificate[] certs;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public SelfPermission(String paramString1, String paramString2, String paramString3, Certificate[] paramArrayOfCertificate)
/*      */     {
/* 2114 */       super();
/* 2115 */       if (paramString1 == null)
/*      */       {
/* 2117 */         throw new NullPointerException(ResourcesMgr.getString("type.can.t.be.null"));
/*      */       }
/* 2119 */       this.type = paramString1;
/* 2120 */       this.name = paramString2;
/* 2121 */       this.actions = paramString3;
/* 2122 */       if (paramArrayOfCertificate != null)
/*      */       {
/* 2124 */         for (int i = 0; i < paramArrayOfCertificate.length; i++) {
/* 2125 */           if (!(paramArrayOfCertificate[i] instanceof X509Certificate))
/*      */           {
/*      */ 
/* 2128 */             this.certs = ((Certificate[])paramArrayOfCertificate.clone());
/* 2129 */             break;
/*      */           }
/*      */         }
/*      */         
/* 2133 */         if (this.certs == null)
/*      */         {
/*      */ 
/* 2136 */           i = 0;
/* 2137 */           int j = 0;
/* 2138 */           while (i < paramArrayOfCertificate.length) {
/* 2139 */             j++;
/* 2140 */             while ((i + 1 < paramArrayOfCertificate.length) && 
/* 2141 */               (((X509Certificate)paramArrayOfCertificate[i]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[(i + 1)])
/* 2142 */               .getSubjectDN()))) {
/* 2143 */               i++;
/*      */             }
/* 2145 */             i++;
/*      */           }
/* 2147 */           if (j == paramArrayOfCertificate.length)
/*      */           {
/*      */ 
/* 2150 */             this.certs = ((Certificate[])paramArrayOfCertificate.clone());
/*      */           }
/*      */           
/* 2153 */           if (this.certs == null)
/*      */           {
/* 2155 */             ArrayList localArrayList = new ArrayList();
/* 2156 */             i = 0;
/* 2157 */             while (i < paramArrayOfCertificate.length) {
/* 2158 */               localArrayList.add(paramArrayOfCertificate[i]);
/* 2159 */               while ((i + 1 < paramArrayOfCertificate.length) && 
/* 2160 */                 (((X509Certificate)paramArrayOfCertificate[i]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[(i + 1)])
/* 2161 */                 .getSubjectDN()))) {
/* 2162 */                 i++;
/*      */               }
/* 2164 */               i++;
/*      */             }
/* 2166 */             this.certs = new Certificate[localArrayList.size()];
/* 2167 */             localArrayList.toArray(this.certs);
/*      */           }
/*      */         }
/*      */       }
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
/*      */     public boolean implies(Permission paramPermission)
/*      */     {
/* 2183 */       return false;
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
/*      */     public boolean equals(Object paramObject)
/*      */     {
/* 2200 */       if (paramObject == this) {
/* 2201 */         return true;
/*      */       }
/* 2203 */       if (!(paramObject instanceof SelfPermission))
/* 2204 */         return false;
/* 2205 */       SelfPermission localSelfPermission = (SelfPermission)paramObject;
/*      */       
/* 2207 */       if ((!this.type.equals(localSelfPermission.type)) || 
/* 2208 */         (!this.name.equals(localSelfPermission.name)) || 
/* 2209 */         (!this.actions.equals(localSelfPermission.actions))) {
/* 2210 */         return false;
/*      */       }
/* 2212 */       if (this.certs.length != localSelfPermission.certs.length) {
/* 2213 */         return false;
/*      */       }
/*      */       
/*      */       int k;
/*      */       int j;
/* 2218 */       for (int i = 0; i < this.certs.length; i++) {
/* 2219 */         k = 0;
/* 2220 */         for (j = 0; j < localSelfPermission.certs.length; j++) {
/* 2221 */           if (this.certs[i].equals(localSelfPermission.certs[j])) {
/* 2222 */             k = 1;
/* 2223 */             break;
/*      */           }
/*      */         }
/* 2226 */         if (k == 0) { return false;
/*      */         }
/*      */       }
/* 2229 */       for (i = 0; i < localSelfPermission.certs.length; i++) {
/* 2230 */         k = 0;
/* 2231 */         for (j = 0; j < this.certs.length; j++) {
/* 2232 */           if (localSelfPermission.certs[i].equals(this.certs[j])) {
/* 2233 */             k = 1;
/* 2234 */             break;
/*      */           }
/*      */         }
/* 2237 */         if (k == 0) return false;
/*      */       }
/* 2239 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 2248 */       int i = this.type.hashCode();
/* 2249 */       if (this.name != null)
/* 2250 */         i ^= this.name.hashCode();
/* 2251 */       if (this.actions != null)
/* 2252 */         i ^= this.actions.hashCode();
/* 2253 */       return i;
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
/*      */     public String getActions()
/*      */     {
/* 2267 */       return "";
/*      */     }
/*      */     
/*      */     public String getSelfType() {
/* 2271 */       return this.type;
/*      */     }
/*      */     
/*      */     public String getSelfName() {
/* 2275 */       return this.name;
/*      */     }
/*      */     
/*      */     public String getSelfActions() {
/* 2279 */       return this.actions;
/*      */     }
/*      */     
/*      */     public Certificate[] getCerts() {
/* 2283 */       return this.certs;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String toString()
/*      */     {
/* 2294 */       return "(SelfPermission " + this.type + " " + this.name + " " + this.actions + ")";
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class PolicyInfo
/*      */   {
/*      */     private static final boolean verbose = false;
/*      */     
/*      */ 
/*      */     final List<PolicyEntry> policyEntries;
/*      */     
/*      */ 
/*      */     final List<PolicyEntry> identityPolicyEntries;
/*      */     
/*      */     final Map<Object, Object> aliasMapping;
/*      */     
/*      */     private final ProtectionDomainCache[] pdMapping;
/*      */     
/*      */     private Random random;
/*      */     
/*      */ 
/*      */     PolicyInfo(int paramInt)
/*      */     {
/* 2319 */       this.policyEntries = new ArrayList();
/*      */       
/* 2321 */       this.identityPolicyEntries = Collections.synchronizedList(new ArrayList(2));
/* 2322 */       this.aliasMapping = Collections.synchronizedMap(new HashMap(11));
/*      */       
/* 2324 */       this.pdMapping = new ProtectionDomainCache[paramInt];
/*      */       
/* 2326 */       JavaSecurityProtectionDomainAccess localJavaSecurityProtectionDomainAccess = SharedSecrets.getJavaSecurityProtectionDomainAccess();
/* 2327 */       for (int i = 0; i < paramInt; i++) {
/* 2328 */         this.pdMapping[i] = localJavaSecurityProtectionDomainAccess.getProtectionDomainCache();
/*      */       }
/* 2330 */       if (paramInt > 1)
/* 2331 */         this.random = new Random();
/*      */     }
/*      */     
/*      */     ProtectionDomainCache getPdMapping() {
/* 2335 */       if (this.pdMapping.length == 1) {
/* 2336 */         return this.pdMapping[0];
/*      */       }
/* 2338 */       int i = Math.abs(this.random.nextInt() % this.pdMapping.length);
/* 2339 */       return this.pdMapping[i];
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\PolicyFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
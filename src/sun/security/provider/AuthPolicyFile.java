/*      */ package sun.security.provider;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.AllPermission;
/*      */ import java.security.CodeSource;
/*      */ import java.security.KeyStore;
/*      */ import java.security.KeyStoreException;
/*      */ import java.security.Permission;
/*      */ import java.security.PermissionCollection;
/*      */ import java.security.Permissions;
/*      */ import java.security.Principal;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.Security;
/*      */ import java.security.UnresolvedPermission;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import javax.security.auth.AuthPermission;
/*      */ import javax.security.auth.Policy;
/*      */ import javax.security.auth.PrivateCredentialPermission;
/*      */ import javax.security.auth.Subject;
/*      */ import sun.security.util.Debug;
/*      */ import sun.security.util.PolicyUtil;
/*      */ import sun.security.util.PropertyExpander;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @Deprecated
/*      */ public class AuthPolicyFile
/*      */   extends Policy
/*      */ {
/*   71 */   static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
/*      */   {
/*      */     public ResourceBundle run() {
/*   73 */       return 
/*   74 */         ResourceBundle.getBundle("sun.security.util.AuthResources");
/*      */     }
/*   71 */   });
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   78 */   private static final Debug debug = Debug.getInstance("policy", "\t[Auth Policy]");
/*      */   
/*      */   private static final String AUTH_POLICY = "java.security.auth.policy";
/*      */   
/*      */   private static final String SECURITY_MANAGER = "java.security.manager";
/*      */   
/*      */   private static final String AUTH_POLICY_URL = "auth.policy.url.";
/*      */   
/*      */   private Vector<PolicyEntry> policyEntries;
/*      */   private Hashtable<Object, Object> aliasMapping;
/*   88 */   private boolean initialized = false;
/*      */   
/*   90 */   private boolean expandProperties = true;
/*   91 */   private boolean ignoreIdentityScope = true;
/*      */   
/*      */ 
/*   94 */   private static final Class<?>[] PARAMS = { String.class, String.class };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public AuthPolicyFile()
/*      */   {
/*  103 */     String str = System.getProperty("java.security.auth.policy");
/*      */     
/*  105 */     if (str == null) {
/*  106 */       str = System.getProperty("java.security.manager");
/*      */     }
/*  108 */     if (str != null) {
/*  109 */       init();
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized void init() {
/*  114 */     if (this.initialized) {
/*  115 */       return;
/*      */     }
/*      */     
/*  118 */     this.policyEntries = new Vector();
/*  119 */     this.aliasMapping = new Hashtable(11);
/*      */     
/*  121 */     initPolicyFile();
/*  122 */     this.initialized = true;
/*      */   }
/*      */   
/*      */ 
/*      */   public synchronized void refresh()
/*      */   {
/*  128 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  129 */     if (localSecurityManager != null) {
/*  130 */       localSecurityManager.checkPermission(new AuthPermission("refreshPolicy"));
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
/*  145 */     this.initialized = false;
/*  146 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/*  148 */         AuthPolicyFile.this.init();
/*  149 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private KeyStore initKeyStore(URL paramURL, String paramString1, String paramString2)
/*      */   {
/*  156 */     if (paramString1 != null)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/*  162 */         URL localURL = null;
/*      */         try {
/*  164 */           localURL = new URL(paramString1);
/*      */         }
/*      */         catch (MalformedURLException localMalformedURLException)
/*      */         {
/*  168 */           localURL = new URL(paramURL, paramString1);
/*      */         }
/*      */         
/*  171 */         if (debug != null) {
/*  172 */           debug.println("reading keystore" + localURL);
/*      */         }
/*      */         
/*      */ 
/*  176 */         BufferedInputStream localBufferedInputStream = new BufferedInputStream(PolicyUtil.getInputStream(localURL));
/*      */         
/*      */         KeyStore localKeyStore;
/*  179 */         if (paramString2 != null) {
/*  180 */           localKeyStore = KeyStore.getInstance(paramString2);
/*      */         } else
/*  182 */           localKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
/*  183 */         localKeyStore.load(localBufferedInputStream, null);
/*  184 */         localBufferedInputStream.close();
/*  185 */         return localKeyStore;
/*      */       }
/*      */       catch (Exception localException) {
/*  188 */         if (debug != null) {
/*  189 */           localException.printStackTrace();
/*      */         }
/*  191 */         return null;
/*      */       }
/*      */     }
/*  194 */     return null;
/*      */   }
/*      */   
/*      */   private void initPolicyFile()
/*      */   {
/*  199 */     String str1 = Security.getProperty("policy.expandProperties");
/*  200 */     if (str1 != null) {
/*  201 */       this.expandProperties = str1.equalsIgnoreCase("true");
/*      */     }
/*      */     
/*  204 */     String str2 = Security.getProperty("policy.ignoreIdentityScope");
/*  205 */     if (str2 != null) {
/*  206 */       this.ignoreIdentityScope = str2.equalsIgnoreCase("true");
/*      */     }
/*      */     
/*  209 */     String str3 = Security.getProperty("policy.allowSystemProperty");
/*  210 */     if ((str3 != null) && (str3.equalsIgnoreCase("true"))) {
/*  211 */       String str4 = System.getProperty("java.security.auth.policy");
/*  212 */       if (str4 != null) {
/*  213 */         j = 0;
/*  214 */         if (str4.startsWith("=")) {
/*  215 */           j = 1;
/*  216 */           str4 = str4.substring(1);
/*      */         }
/*      */         try {
/*  219 */           str4 = PropertyExpander.expand(str4);
/*      */           
/*  221 */           File localFile = new File(str4);
/*  222 */           URL localURL; if (localFile.exists())
/*      */           {
/*  224 */             localURL = new URL("file:" + localFile.getCanonicalPath());
/*      */           } else {
/*  226 */             localURL = new URL(str4);
/*      */           }
/*  228 */           if (debug != null) {
/*  229 */             debug.println("reading " + localURL);
/*      */           }
/*  231 */           init(localURL);
/*      */         }
/*      */         catch (Exception localException1) {
/*  234 */           if (debug != null) {
/*  235 */             debug.println("caught exception: " + localException1);
/*      */           }
/*      */         }
/*      */         
/*  239 */         if (j != 0) {
/*  240 */           if (debug != null) {
/*  241 */             debug.println("overriding other policies!");
/*      */           }
/*  243 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  248 */     int i = 1;
/*  249 */     int j = 0;
/*      */     
/*      */     String str5;
/*  252 */     while ((str5 = Security.getProperty("auth.policy.url." + i)) != null)
/*      */     {
/*      */       try {
/*  255 */         str5 = PropertyExpander.expand(str5).replace(File.separatorChar, '/');
/*  256 */         if (debug != null) {
/*  257 */           debug.println("reading " + str5);
/*      */         }
/*  259 */         init(new URL(str5));
/*  260 */         j = 1;
/*      */       } catch (Exception localException2) {
/*  262 */         if (debug != null) {
/*  263 */           debug.println("error reading policy " + localException2);
/*  264 */           localException2.printStackTrace();
/*      */         }
/*      */       }
/*      */       
/*  268 */       i++;
/*      */     }
/*      */     
/*  271 */     if (j == 0) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean checkForTrustedIdentity(Certificate paramCertificate)
/*      */   {
/*  282 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void init(URL paramURL)
/*      */   {
/*  292 */     PolicyParser localPolicyParser = new PolicyParser(this.expandProperties);
/*      */     try {
/*  294 */       InputStreamReader localInputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURL));Object localObject1 = null;
/*  295 */       try { localPolicyParser.read(localInputStreamReader);
/*  296 */         KeyStore localKeyStore = initKeyStore(paramURL, localPolicyParser.getKeyStoreUrl(), localPolicyParser
/*  297 */           .getKeyStoreType());
/*  298 */         Enumeration localEnumeration = localPolicyParser.grantElements();
/*  299 */         while (localEnumeration.hasMoreElements()) {
/*  300 */           PolicyParser.GrantEntry localGrantEntry = (PolicyParser.GrantEntry)localEnumeration.nextElement();
/*  301 */           addGrantEntry(localGrantEntry, localKeyStore);
/*      */         }
/*      */       }
/*      */       catch (Throwable localThrowable2)
/*      */       {
/*  293 */         localObject1 = localThrowable2;throw localThrowable2;
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  303 */         if (localInputStreamReader != null) if (localObject1 != null) try { localInputStreamReader.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localInputStreamReader.close();
/*  304 */       } } catch (PolicyParser.ParsingException localParsingException) { System.err.println("java.security.auth.policy" + rb
/*  305 */         .getString(".error.parsing.") + paramURL);
/*  306 */       System.err.println("java.security.auth.policy" + rb.getString("COLON") + localParsingException
/*  307 */         .getMessage());
/*  308 */       if (debug != null) {
/*  309 */         localParsingException.printStackTrace();
/*      */       }
/*      */     } catch (Exception localException) {
/*  312 */       if (debug != null) {
/*  313 */         debug.println("error parsing " + paramURL);
/*  314 */         debug.println(localException.toString());
/*  315 */         localException.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   CodeSource getCodeSource(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore)
/*      */     throws MalformedURLException
/*      */   {
/*  328 */     Certificate[] arrayOfCertificate = null;
/*  329 */     if (paramGrantEntry.signedBy != null) {
/*  330 */       arrayOfCertificate = getCertificates(paramKeyStore, paramGrantEntry.signedBy);
/*  331 */       if (arrayOfCertificate == null)
/*      */       {
/*      */ 
/*  334 */         if (debug != null) {
/*  335 */           debug.println(" no certs for alias " + paramGrantEntry.signedBy + ", ignoring.");
/*      */         }
/*      */         
/*  338 */         return null;
/*      */       }
/*      */     }
/*      */     
/*      */     URL localURL;
/*  343 */     if (paramGrantEntry.codeBase != null) {
/*  344 */       localURL = new URL(paramGrantEntry.codeBase);
/*      */     } else {
/*  346 */       localURL = null;
/*      */     }
/*      */     
/*  349 */     if ((paramGrantEntry.principals == null) || (paramGrantEntry.principals.size() == 0)) {
/*  350 */       return 
/*  351 */         canonicalizeCodebase(new CodeSource(localURL, arrayOfCertificate), false);
/*      */     }
/*      */     
/*  354 */     return 
/*  355 */       canonicalizeCodebase(new SubjectCodeSource(null, paramGrantEntry.principals, localURL, arrayOfCertificate), false);
/*      */   }
/*      */   
/*      */ 
/*      */   private void addGrantEntry(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore)
/*      */   {
/*      */     Object localObject1;
/*      */     
/*      */     Object localObject2;
/*      */     
/*  365 */     if (debug != null) {
/*  366 */       debug.println("Adding policy entry: ");
/*  367 */       debug.println("  signedBy " + paramGrantEntry.signedBy);
/*  368 */       debug.println("  codeBase " + paramGrantEntry.codeBase);
/*  369 */       if (paramGrantEntry.principals != null) {
/*  370 */         for (localObject1 = paramGrantEntry.principals.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (PolicyParser.PrincipalEntry)((Iterator)localObject1).next();
/*  371 */           debug.println("  " + ((PolicyParser.PrincipalEntry)localObject2).getPrincipalClass() + " " + ((PolicyParser.PrincipalEntry)localObject2)
/*  372 */             .getPrincipalName());
/*      */         }
/*      */       }
/*  375 */       debug.println();
/*      */     }
/*      */     try
/*      */     {
/*  379 */       localObject1 = getCodeSource(paramGrantEntry, paramKeyStore);
/*      */       
/*  381 */       if (localObject1 == null) { return;
/*      */       }
/*  383 */       localObject2 = new PolicyEntry((CodeSource)localObject1);
/*  384 */       Enumeration localEnumeration = paramGrantEntry.permissionElements();
/*  385 */       while (localEnumeration.hasMoreElements()) {
/*  386 */         PolicyParser.PermissionEntry localPermissionEntry = (PolicyParser.PermissionEntry)localEnumeration.nextElement();
/*      */         
/*      */         try
/*      */         {
/*      */           Permission localPermission;
/*  391 */           if ((localPermissionEntry.permission.equals("javax.security.auth.PrivateCredentialPermission")) && 
/*  392 */             (localPermissionEntry.name.endsWith(" self"))) {
/*  393 */             localPermission = getInstance(localPermissionEntry.permission, localPermissionEntry.name + " \"self\"", localPermissionEntry.action);
/*      */           }
/*      */           else
/*      */           {
/*  397 */             localPermission = getInstance(localPermissionEntry.permission, localPermissionEntry.name, localPermissionEntry.action);
/*      */           }
/*      */           
/*      */ 
/*  401 */           ((PolicyEntry)localObject2).add(localPermission);
/*  402 */           if (debug != null) {
/*  403 */             debug.println("  " + localPermission);
/*      */           }
/*      */         } catch (ClassNotFoundException localClassNotFoundException) {
/*      */           Certificate[] arrayOfCertificate;
/*  407 */           if (localPermissionEntry.signedBy != null) {
/*  408 */             arrayOfCertificate = getCertificates(paramKeyStore, localPermissionEntry.signedBy);
/*      */           } else {
/*  410 */             arrayOfCertificate = null;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  415 */           if ((arrayOfCertificate != null) || (localPermissionEntry.signedBy == null)) {
/*  416 */             UnresolvedPermission localUnresolvedPermission = new UnresolvedPermission(localPermissionEntry.permission, localPermissionEntry.name, localPermissionEntry.action, arrayOfCertificate);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  421 */             ((PolicyEntry)localObject2).add(localUnresolvedPermission);
/*  422 */             if (debug != null) {
/*  423 */               debug.println("  " + localUnresolvedPermission);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (InvocationTargetException localInvocationTargetException) {
/*  428 */           System.err.println("java.security.auth.policy" + rb
/*  429 */             .getString(".error.adding.Permission.") + localPermissionEntry.permission + rb
/*      */             
/*  431 */             .getString("SPACE") + localInvocationTargetException
/*  432 */             .getTargetException());
/*      */         }
/*      */         catch (Exception localException2) {
/*  435 */           System.err.println("java.security.auth.policy" + rb
/*  436 */             .getString(".error.adding.Permission.") + localPermissionEntry.permission + rb
/*      */             
/*  438 */             .getString("SPACE") + localException2);
/*      */         }
/*      */       }
/*      */       
/*  442 */       this.policyEntries.addElement(localObject2);
/*      */     }
/*      */     catch (Exception localException1) {
/*  445 */       System.err.println("java.security.auth.policy" + rb
/*  446 */         .getString(".error.adding.Entry.") + paramGrantEntry + rb
/*      */         
/*  448 */         .getString("SPACE") + localException1);
/*      */     }
/*      */     
/*      */ 
/*  452 */     if (debug != null) {
/*  453 */       debug.println();
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
/*  496 */     Class localClass = Class.forName(paramString1);
/*  497 */     Constructor localConstructor = localClass.getConstructor(PARAMS);
/*  498 */     return (Permission)localConstructor.newInstance(new Object[] { paramString2, paramString3 });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Certificate[] getCertificates(KeyStore paramKeyStore, String paramString)
/*      */   {
/*  506 */     Vector localVector = null;
/*      */     
/*  508 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
/*  509 */     int i = 0;
/*      */     Object localObject;
/*  511 */     while (localStringTokenizer.hasMoreTokens()) {
/*  512 */       localObject = localStringTokenizer.nextToken().trim();
/*  513 */       i++;
/*  514 */       Certificate localCertificate = null;
/*      */       
/*  516 */       localCertificate = (Certificate)this.aliasMapping.get(localObject);
/*  517 */       if ((localCertificate == null) && (paramKeyStore != null))
/*      */       {
/*      */         try {
/*  520 */           localCertificate = paramKeyStore.getCertificate((String)localObject);
/*      */         }
/*      */         catch (KeyStoreException localKeyStoreException) {}
/*      */         
/*      */ 
/*  525 */         if (localCertificate != null) {
/*  526 */           this.aliasMapping.put(localObject, localCertificate);
/*  527 */           this.aliasMapping.put(localCertificate, localObject);
/*      */         }
/*      */       }
/*      */       
/*  531 */       if (localCertificate != null) {
/*  532 */         if (localVector == null) {
/*  533 */           localVector = new Vector();
/*      */         }
/*  535 */         localVector.addElement(localCertificate);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  540 */     if ((localVector != null) && (i == localVector.size())) {
/*  541 */       localObject = new Certificate[localVector.size()];
/*  542 */       localVector.copyInto((Object[])localObject);
/*  543 */       return (Certificate[])localObject;
/*      */     }
/*  545 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final synchronized Enumeration<PolicyEntry> elements()
/*      */   {
/*  556 */     return this.policyEntries.elements();
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
/*      */   public PermissionCollection getPermissions(final Subject paramSubject, final CodeSource paramCodeSource)
/*      */   {
/*  571 */     
/*  572 */       (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */ 
/*      */         public PermissionCollection run()
/*      */         {
/*  577 */           SubjectCodeSource localSubjectCodeSource = new SubjectCodeSource(paramSubject, null, paramCodeSource == null ? null : paramCodeSource.getLocation(), paramCodeSource == null ? null : paramCodeSource.getCertificates());
/*  578 */           if (AuthPolicyFile.this.initialized) {
/*  579 */             return AuthPolicyFile.this.getPermissions(new Permissions(), localSubjectCodeSource);
/*      */           }
/*  581 */           return new PolicyPermissions(AuthPolicyFile.this, localSubjectCodeSource);
/*      */         }
/*      */       });
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
/*      */   PermissionCollection getPermissions(CodeSource paramCodeSource)
/*      */   {
/*  600 */     if (this.initialized) {
/*  601 */       return getPermissions(new Permissions(), paramCodeSource);
/*      */     }
/*  603 */     return new PolicyPermissions(this, paramCodeSource);
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
/*      */   Permissions getPermissions(Permissions paramPermissions, CodeSource paramCodeSource)
/*      */   {
/*  622 */     if (!this.initialized) {
/*  623 */       init();
/*      */     }
/*      */     
/*  626 */     CodeSource[] arrayOfCodeSource = { null };
/*      */     
/*  628 */     arrayOfCodeSource[0] = canonicalizeCodebase(paramCodeSource, true);
/*      */     
/*  630 */     if (debug != null) {
/*  631 */       debug.println("evaluate(" + arrayOfCodeSource[0] + ")\n");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  638 */     for (int i = 0; i < this.policyEntries.size(); i++)
/*      */     {
/*  640 */       PolicyEntry localPolicyEntry = (PolicyEntry)this.policyEntries.elementAt(i);
/*      */       
/*  642 */       if (debug != null) {
/*  643 */         debug.println("PolicyFile CodeSource implies: " + localPolicyEntry.codesource
/*  644 */           .toString() + "\n\n\t" + arrayOfCodeSource[0]
/*  645 */           .toString() + "\n\n");
/*      */       }
/*      */       
/*  648 */       if (localPolicyEntry.codesource.implies(arrayOfCodeSource[0])) {
/*  649 */         for (int k = 0; k < localPolicyEntry.permissions.size(); k++) {
/*  650 */           Permission localPermission = (Permission)localPolicyEntry.permissions.elementAt(k);
/*  651 */           if (debug != null) {
/*  652 */             debug.println("  granting " + localPermission);
/*      */           }
/*  654 */           if (!addSelfPermissions(localPermission, localPolicyEntry.codesource, arrayOfCodeSource[0], paramPermissions))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  660 */             paramPermissions.add(localPermission);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  668 */     if (!this.ignoreIdentityScope) {
/*  669 */       Certificate[] arrayOfCertificate = arrayOfCodeSource[0].getCertificates();
/*  670 */       if (arrayOfCertificate != null) {
/*  671 */         for (int j = 0; j < arrayOfCertificate.length; j++) {
/*  672 */           if ((this.aliasMapping.get(arrayOfCertificate[j]) == null) && 
/*  673 */             (checkForTrustedIdentity(arrayOfCertificate[j])))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  678 */             paramPermissions.add(new AllPermission());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  683 */     return paramPermissions;
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
/*      */   private boolean addSelfPermissions(Permission paramPermission, CodeSource paramCodeSource1, CodeSource paramCodeSource2, Permissions paramPermissions)
/*      */   {
/*  707 */     if (!(paramPermission instanceof PrivateCredentialPermission)) {
/*  708 */       return false;
/*      */     }
/*      */     
/*  711 */     if (!(paramCodeSource1 instanceof SubjectCodeSource)) {
/*  712 */       return false;
/*      */     }
/*      */     
/*  715 */     PrivateCredentialPermission localPrivateCredentialPermission1 = (PrivateCredentialPermission)paramPermission;
/*  716 */     SubjectCodeSource localSubjectCodeSource = (SubjectCodeSource)paramCodeSource1;
/*      */     
/*      */ 
/*  719 */     String[][] arrayOfString1 = localPrivateCredentialPermission1.getPrincipals();
/*  720 */     if ((arrayOfString1.length <= 0) || 
/*  721 */       (!arrayOfString1[0][0].equalsIgnoreCase("self")) || 
/*  722 */       (!arrayOfString1[0][1].equalsIgnoreCase("self")))
/*      */     {
/*      */ 
/*  725 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  732 */     if (localSubjectCodeSource.getPrincipals() == null)
/*      */     {
/*  734 */       return true;
/*      */     }
/*      */     
/*  737 */     for (PolicyParser.PrincipalEntry localPrincipalEntry : localSubjectCodeSource.getPrincipals())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  749 */       String[][] arrayOfString2 = getPrincipalInfo(localPrincipalEntry, paramCodeSource2);
/*      */       
/*  751 */       for (int i = 0; i < arrayOfString2.length; i++)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  757 */         PrivateCredentialPermission localPrivateCredentialPermission2 = new PrivateCredentialPermission(localPrivateCredentialPermission1.getCredentialClass() + " " + arrayOfString2[i][0] + " \"" + arrayOfString2[i][1] + "\"", "read");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  764 */         if (debug != null) {
/*  765 */           debug.println("adding SELF permission: " + localPrivateCredentialPermission2
/*  766 */             .toString());
/*      */         }
/*      */         
/*  769 */         paramPermissions.add(localPrivateCredentialPermission2);
/*      */       }
/*      */     }
/*      */     
/*  773 */     return true;
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
/*      */   private String[][] getPrincipalInfo(PolicyParser.PrincipalEntry paramPrincipalEntry, CodeSource paramCodeSource)
/*      */   {
/*  791 */     if (!paramPrincipalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS"))
/*      */     {
/*  793 */       if (!paramPrincipalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME"))
/*      */       {
/*      */ 
/*      */ 
/*  797 */         localObject = new String[1][2];
/*  798 */         localObject[0][0] = paramPrincipalEntry.getPrincipalClass();
/*  799 */         localObject[0][1] = paramPrincipalEntry.getPrincipalName();
/*  800 */         return (String[][])localObject;
/*      */       } }
/*      */     Principal localPrincipal;
/*  803 */     if (!paramPrincipalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS"))
/*      */     {
/*  805 */       if (paramPrincipalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME"))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  812 */         localObject = (SubjectCodeSource)paramCodeSource;
/*      */         
/*  814 */         localSet = null;
/*      */         
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  820 */           Class localClass = Class.forName(paramPrincipalEntry.getPrincipalClass(), false, 
/*  821 */             ClassLoader.getSystemClassLoader());
/*  822 */           localSet = ((SubjectCodeSource)localObject).getSubject().getPrincipals(localClass);
/*      */         } catch (Exception localException) {
/*  824 */           if (debug != null) {
/*  825 */             debug.println("problem finding Principal Class when expanding SELF permission: " + localException
/*      */             
/*  827 */               .toString());
/*      */           }
/*      */         }
/*      */         
/*  831 */         if (localSet == null)
/*      */         {
/*  833 */           return new String[0][0];
/*      */         }
/*      */         
/*  836 */         arrayOfString = new String[localSet.size()][2];
/*      */         
/*  838 */         i = 0;
/*  839 */         for (localIterator = localSet.iterator(); localIterator.hasNext();) { localPrincipal = (Principal)localIterator.next();
/*  840 */           arrayOfString[i][0] = localPrincipal.getClass().getName();
/*  841 */           arrayOfString[i][1] = localPrincipal.getName();
/*  842 */           i++;
/*      */         }
/*  844 */         return arrayOfString;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  853 */     Object localObject = (SubjectCodeSource)paramCodeSource;
/*  854 */     Set localSet = ((SubjectCodeSource)localObject).getSubject().getPrincipals();
/*      */     
/*  856 */     String[][] arrayOfString = new String[localSet.size()][2];
/*      */     
/*  858 */     int i = 0;
/*  859 */     for (Iterator localIterator = localSet.iterator(); localIterator.hasNext();) { localPrincipal = (Principal)localIterator.next();
/*  860 */       arrayOfString[i][0] = localPrincipal.getClass().getName();
/*  861 */       arrayOfString[i][1] = localPrincipal.getName();
/*  862 */       i++;
/*      */     }
/*  864 */     return arrayOfString;
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
/*      */   Certificate[] getSignerCertificates(CodeSource paramCodeSource)
/*      */   {
/*  882 */     Certificate[] arrayOfCertificate1 = null;
/*  883 */     if ((arrayOfCertificate1 = paramCodeSource.getCertificates()) == null) {
/*  884 */       return null;
/*      */     }
/*  886 */     for (int i = 0; i < arrayOfCertificate1.length; i++) {
/*  887 */       if (!(arrayOfCertificate1[i] instanceof X509Certificate)) {
/*  888 */         return paramCodeSource.getCertificates();
/*      */       }
/*      */     }
/*      */     
/*  892 */     i = 0;
/*  893 */     int j = 0;
/*  894 */     while (i < arrayOfCertificate1.length) {
/*  895 */       j++;
/*  896 */       while ((i + 1 < arrayOfCertificate1.length) && 
/*  897 */         (((X509Certificate)arrayOfCertificate1[i]).getIssuerDN().equals(((X509Certificate)arrayOfCertificate1[(i + 1)])
/*  898 */         .getSubjectDN()))) {
/*  899 */         i++;
/*      */       }
/*  901 */       i++;
/*      */     }
/*  903 */     if (j == arrayOfCertificate1.length)
/*      */     {
/*  905 */       return arrayOfCertificate1;
/*      */     }
/*      */     
/*  908 */     ArrayList localArrayList = new ArrayList();
/*  909 */     i = 0;
/*  910 */     while (i < arrayOfCertificate1.length) {
/*  911 */       localArrayList.add(arrayOfCertificate1[i]);
/*  912 */       while ((i + 1 < arrayOfCertificate1.length) && 
/*  913 */         (((X509Certificate)arrayOfCertificate1[i]).getIssuerDN().equals(((X509Certificate)arrayOfCertificate1[(i + 1)])
/*  914 */         .getSubjectDN()))) {
/*  915 */         i++;
/*      */       }
/*  917 */       i++;
/*      */     }
/*  919 */     Certificate[] arrayOfCertificate2 = new Certificate[localArrayList.size()];
/*  920 */     localArrayList.toArray(arrayOfCertificate2);
/*  921 */     return arrayOfCertificate2;
/*      */   }
/*      */   
/*      */   private CodeSource canonicalizeCodebase(CodeSource paramCodeSource, boolean paramBoolean)
/*      */   {
/*  926 */     Object localObject1 = paramCodeSource;
/*  927 */     if ((paramCodeSource.getLocation() != null) && 
/*  928 */       (paramCodeSource.getLocation().getProtocol().equalsIgnoreCase("file"))) {
/*      */       try
/*      */       {
/*  931 */         String str = paramCodeSource.getLocation().getFile().replace('/', File.separatorChar);
/*      */         
/*  933 */         localObject2 = null;
/*  934 */         if (str.endsWith("*"))
/*      */         {
/*      */ 
/*  937 */           str = str.substring(0, str.length() - 1);
/*  938 */           int i = 0;
/*  939 */           if (str.endsWith(File.separator)) {
/*  940 */             i = 1;
/*      */           }
/*  942 */           if (str.equals("")) {
/*  943 */             str = System.getProperty("user.dir");
/*      */           }
/*  945 */           File localFile = new File(str);
/*  946 */           str = localFile.getCanonicalPath();
/*  947 */           StringBuffer localStringBuffer = new StringBuffer(str);
/*      */           
/*      */ 
/*      */ 
/*  951 */           if ((!str.endsWith(File.separator)) && ((i != 0) || 
/*  952 */             (localFile.isDirectory()))) {
/*  953 */             localStringBuffer.append(File.separatorChar);
/*      */           }
/*  955 */           localStringBuffer.append('*');
/*  956 */           str = localStringBuffer.toString();
/*      */         } else {
/*  958 */           str = new File(str).getCanonicalPath();
/*      */         }
/*  960 */         localObject2 = new File(str).toURL();
/*      */         
/*  962 */         if ((paramCodeSource instanceof SubjectCodeSource)) {
/*  963 */           SubjectCodeSource localSubjectCodeSource2 = (SubjectCodeSource)paramCodeSource;
/*  964 */           if (paramBoolean)
/*      */           {
/*      */ 
/*      */ 
/*  968 */             localObject1 = new SubjectCodeSource(localSubjectCodeSource2.getSubject(), localSubjectCodeSource2.getPrincipals(), (URL)localObject2, getSignerCertificates(localSubjectCodeSource2));
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  973 */             localObject1 = new SubjectCodeSource(localSubjectCodeSource2.getSubject(), localSubjectCodeSource2.getPrincipals(), (URL)localObject2, localSubjectCodeSource2.getCertificates());
/*      */           }
/*      */         }
/*  976 */         else if (paramBoolean)
/*      */         {
/*  978 */           localObject1 = new CodeSource((URL)localObject2, getSignerCertificates(paramCodeSource));
/*      */         }
/*      */         else {
/*  981 */           localObject1 = new CodeSource((URL)localObject2, paramCodeSource.getCertificates());
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*      */         Object localObject2;
/*  987 */         if (paramBoolean) {
/*  988 */           if (!(paramCodeSource instanceof SubjectCodeSource))
/*      */           {
/*  990 */             localObject1 = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
/*      */           } else {
/*  992 */             localObject2 = (SubjectCodeSource)paramCodeSource;
/*      */             
/*      */ 
/*      */ 
/*  996 */             localObject1 = new SubjectCodeSource(((SubjectCodeSource)localObject2).getSubject(), ((SubjectCodeSource)localObject2).getPrincipals(), ((SubjectCodeSource)localObject2).getLocation(), getSignerCertificates((CodeSource)localObject2));
/*      */           }
/*      */           
/*      */         }
/*      */       }
/* 1001 */     } else if (paramBoolean) {
/* 1002 */       if (!(paramCodeSource instanceof SubjectCodeSource))
/*      */       {
/* 1004 */         localObject1 = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
/*      */       } else {
/* 1006 */         SubjectCodeSource localSubjectCodeSource1 = (SubjectCodeSource)paramCodeSource;
/*      */         
/*      */ 
/*      */ 
/* 1010 */         localObject1 = new SubjectCodeSource(localSubjectCodeSource1.getSubject(), localSubjectCodeSource1.getPrincipals(), localSubjectCodeSource1.getLocation(), getSignerCertificates(localSubjectCodeSource1));
/*      */       }
/*      */     }
/*      */     
/* 1014 */     return (CodeSource)localObject1;
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
/*      */   private static class PolicyEntry
/*      */   {
/*      */     CodeSource codesource;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Vector<Permission> permissions;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     PolicyEntry(CodeSource paramCodeSource)
/*      */     {
/* 1078 */       this.codesource = paramCodeSource;
/* 1079 */       this.permissions = new Vector();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     void add(Permission paramPermission)
/*      */     {
/* 1086 */       this.permissions.addElement(paramPermission);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     CodeSource getCodeSource()
/*      */     {
/* 1093 */       return this.codesource;
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/* 1098 */       StringBuffer localStringBuffer = new StringBuffer();
/* 1099 */       localStringBuffer.append(AuthPolicyFile.rb.getString("LPARAM"));
/* 1100 */       localStringBuffer.append(getCodeSource());
/* 1101 */       localStringBuffer.append("\n");
/* 1102 */       for (int i = 0; i < this.permissions.size(); i++) {
/* 1103 */         Permission localPermission = (Permission)this.permissions.elementAt(i);
/* 1104 */         localStringBuffer.append(AuthPolicyFile.rb.getString("SPACE"));
/* 1105 */         localStringBuffer.append(AuthPolicyFile.rb.getString("SPACE"));
/* 1106 */         localStringBuffer.append(localPermission);
/* 1107 */         localStringBuffer.append(AuthPolicyFile.rb.getString("NEWLINE"));
/*      */       }
/* 1109 */       localStringBuffer.append(AuthPolicyFile.rb.getString("RPARAM"));
/* 1110 */       localStringBuffer.append(AuthPolicyFile.rb.getString("NEWLINE"));
/* 1111 */       return localStringBuffer.toString();
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\AuthPolicyFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
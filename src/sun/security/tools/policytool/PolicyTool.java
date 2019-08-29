/*     */ package sun.security.tools.policytool;
/*     */ 
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.Permission;
/*     */ import java.security.PublicKey;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.text.Collator;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedList;
/*     */ import java.util.ListIterator;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Vector;
/*     */ import javax.security.auth.login.LoginException;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.UIManager;
/*     */ import sun.security.provider.PolicyParser;
/*     */ import sun.security.provider.PolicyParser.GrantEntry;
/*     */ import sun.security.provider.PolicyParser.ParsingException;
/*     */ import sun.security.provider.PolicyParser.PermissionEntry;
/*     */ import sun.security.provider.PolicyParser.PrincipalEntry;
/*     */ import sun.security.util.PolicyUtil;
/*     */ import sun.security.util.PropertyExpander;
/*     */ import sun.security.util.PropertyExpander.ExpandException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PolicyTool
/*     */ {
/*  74 */   static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.tools.policytool.Resources");
/*     */   
/*  76 */   static final Collator collator = Collator.getInstance();
/*     */   Vector<String> warnings;
/*     */   
/*  79 */   static { collator.setStrength(0);
/*     */     
/*     */ 
/*  82 */     if (System.getProperty("apple.laf.useScreenMenuBar") == null) {
/*  83 */       System.setProperty("apple.laf.useScreenMenuBar", "true");
/*     */     }
/*  85 */     System.setProperty("apple.awt.application.name", getMessage("Policy.Tool"));
/*     */     
/*     */ 
/*  88 */     if (System.getProperty("swing.defaultlaf") == null) {
/*     */       try {
/*  90 */         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/*     */       }
/*     */       catch (Exception localException) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  99 */   boolean newWarning = false;
/*     */   
/*     */ 
/*     */ 
/* 103 */   boolean modified = false;
/*     */   
/*     */   private static final boolean testing = false;
/* 106 */   private static final Class<?>[] TWOPARAMS = { String.class, String.class };
/* 107 */   private static final Class<?>[] ONEPARAMS = { String.class };
/* 108 */   private static final Class<?>[] NOPARAMS = new Class[0];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 116 */   private static String policyFileName = null;
/* 117 */   private Vector<PolicyEntry> policyEntries = null;
/* 118 */   private PolicyParser parser = null;
/*     */   
/*     */ 
/* 121 */   private KeyStore keyStore = null;
/* 122 */   private String keyStoreName = " ";
/* 123 */   private String keyStoreType = " ";
/* 124 */   private String keyStoreProvider = " ";
/* 125 */   private String keyStorePwdURL = " ";
/*     */   
/*     */ 
/*     */   private static final String P11KEYSTORE = "PKCS11";
/*     */   
/*     */ 
/*     */   private static final String NONE = "NONE";
/*     */   
/*     */ 
/*     */ 
/*     */   private PolicyTool()
/*     */   {
/* 137 */     this.policyEntries = new Vector();
/* 138 */     this.parser = new PolicyParser();
/* 139 */     this.warnings = new Vector();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   String getPolicyFileName()
/*     */   {
/* 146 */     return policyFileName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void setPolicyFileName(String paramString)
/*     */   {
/* 153 */     policyFileName = paramString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void clearKeyStoreInfo()
/*     */   {
/* 160 */     this.keyStoreName = null;
/* 161 */     this.keyStoreType = null;
/* 162 */     this.keyStoreProvider = null;
/* 163 */     this.keyStorePwdURL = null;
/*     */     
/* 165 */     this.keyStore = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   String getKeyStoreName()
/*     */   {
/* 172 */     return this.keyStoreName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   String getKeyStoreType()
/*     */   {
/* 179 */     return this.keyStoreType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   String getKeyStoreProvider()
/*     */   {
/* 186 */     return this.keyStoreProvider;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   String getKeyStorePwdURL()
/*     */   {
/* 193 */     return this.keyStorePwdURL;
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
/*     */   void openPolicy(String paramString)
/*     */     throws FileNotFoundException, PolicyParser.ParsingException, KeyStoreException, CertificateException, InstantiationException, MalformedURLException, IOException, NoSuchAlgorithmException, IllegalAccessException, NoSuchMethodException, UnrecoverableKeyException, NoSuchProviderException, ClassNotFoundException, ExpandException, InvocationTargetException
/*     */   {
/* 215 */     this.newWarning = false;
/*     */     
/*     */ 
/* 218 */     this.policyEntries = new Vector();
/* 219 */     this.parser = new PolicyParser();
/* 220 */     this.warnings = new Vector();
/* 221 */     setPolicyFileName(null);
/* 222 */     clearKeyStoreInfo();
/*     */     
/*     */ 
/* 225 */     if (paramString == null) {
/* 226 */       this.modified = false;
/* 227 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 235 */     setPolicyFileName(paramString);
/* 236 */     this.parser.read(new FileReader(paramString));
/*     */     
/*     */ 
/* 239 */     openKeyStore(this.parser.getKeyStoreUrl(), this.parser.getKeyStoreType(), this.parser
/* 240 */       .getKeyStoreProvider(), this.parser.getStorePassURL());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 245 */     Enumeration localEnumeration = this.parser.grantElements();
/* 246 */     while (localEnumeration.hasMoreElements()) {
/* 247 */       PolicyParser.GrantEntry localGrantEntry = (PolicyParser.GrantEntry)localEnumeration.nextElement();
/*     */       MessageFormat localMessageFormat1;
/*     */       Object localObject4;
/* 250 */       if (localGrantEntry.signedBy != null)
/*     */       {
/* 252 */         localObject1 = parseSigners(localGrantEntry.signedBy);
/* 253 */         for (int i = 0; i < localObject1.length; i++) {
/* 254 */           PublicKey localPublicKey = getPublicKeyAlias(localObject1[i]);
/* 255 */           if (localPublicKey == null) {
/* 256 */             this.newWarning = true;
/*     */             
/* 258 */             localMessageFormat1 = new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
/* 259 */             localObject4 = new Object[] { localObject1[i] };
/* 260 */             this.warnings.addElement(localMessageFormat1.format(localObject4));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 267 */       Object localObject1 = localGrantEntry.principals.listIterator(0);
/* 268 */       while (((ListIterator)localObject1).hasNext()) {
/* 269 */         localObject2 = (PolicyParser.PrincipalEntry)((ListIterator)localObject1).next();
/*     */         try {
/* 271 */           verifyPrincipal(((PolicyParser.PrincipalEntry)localObject2).getPrincipalClass(), ((PolicyParser.PrincipalEntry)localObject2)
/* 272 */             .getPrincipalName());
/*     */         } catch (ClassNotFoundException localClassNotFoundException1) {
/* 274 */           this.newWarning = true;
/*     */           
/* 276 */           localMessageFormat1 = new MessageFormat(getMessage("Warning.Class.not.found.class"));
/* 277 */           localObject4 = new Object[] { ((PolicyParser.PrincipalEntry)localObject2).getPrincipalClass() };
/* 278 */           this.warnings.addElement(localMessageFormat1.format(localObject4));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 284 */       Object localObject2 = localGrantEntry.permissionElements();
/* 285 */       while (((Enumeration)localObject2).hasMoreElements()) {
/* 286 */         localObject3 = (PolicyParser.PermissionEntry)((Enumeration)localObject2).nextElement();
/*     */         Object localObject5;
/* 288 */         try { verifyPermission(((PolicyParser.PermissionEntry)localObject3).permission, ((PolicyParser.PermissionEntry)localObject3).name, ((PolicyParser.PermissionEntry)localObject3).action);
/*     */         } catch (ClassNotFoundException localClassNotFoundException2) {
/* 290 */           this.newWarning = true;
/*     */           
/* 292 */           localObject4 = new MessageFormat(getMessage("Warning.Class.not.found.class"));
/* 293 */           localObject5 = new Object[] { ((PolicyParser.PermissionEntry)localObject3).permission };
/* 294 */           this.warnings.addElement(((MessageFormat)localObject4).format(localObject5));
/*     */         } catch (InvocationTargetException localInvocationTargetException) {
/* 296 */           this.newWarning = true;
/*     */           
/* 298 */           localObject4 = new MessageFormat(getMessage("Warning.Invalid.argument.s.for.constructor.arg"));
/* 299 */           localObject5 = new Object[] { ((PolicyParser.PermissionEntry)localObject3).permission };
/* 300 */           this.warnings.addElement(((MessageFormat)localObject4).format(localObject5));
/*     */         }
/*     */         
/*     */ 
/* 304 */         if (((PolicyParser.PermissionEntry)localObject3).signedBy != null)
/*     */         {
/* 306 */           String[] arrayOfString = parseSigners(((PolicyParser.PermissionEntry)localObject3).signedBy);
/*     */           
/* 308 */           for (int j = 0; j < arrayOfString.length; j++) {
/* 309 */             localObject5 = getPublicKeyAlias(arrayOfString[j]);
/* 310 */             if (localObject5 == null) {
/* 311 */               this.newWarning = true;
/*     */               
/* 313 */               MessageFormat localMessageFormat2 = new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
/* 314 */               Object[] arrayOfObject = { arrayOfString[j] };
/* 315 */               this.warnings.addElement(localMessageFormat2.format(arrayOfObject));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 320 */       Object localObject3 = new PolicyEntry(this, localGrantEntry);
/* 321 */       this.policyEntries.addElement(localObject3);
/*     */     }
/*     */     
/*     */ 
/* 325 */     this.modified = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void savePolicy(String paramString)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/* 335 */     this.parser.setKeyStoreUrl(this.keyStoreName);
/* 336 */     this.parser.setKeyStoreType(this.keyStoreType);
/* 337 */     this.parser.setKeyStoreProvider(this.keyStoreProvider);
/* 338 */     this.parser.setStorePassURL(this.keyStorePwdURL);
/* 339 */     this.parser.write(new FileWriter(paramString));
/* 340 */     this.modified = false;
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
/*     */   void openKeyStore(String paramString1, String paramString2, String paramString3, String paramString4)
/*     */     throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, NoSuchProviderException, ExpandException
/*     */   {
/* 357 */     if ((paramString1 == null) && (paramString2 == null) && (paramString3 == null) && (paramString4 == null))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 363 */       this.keyStoreName = null;
/* 364 */       this.keyStoreType = null;
/* 365 */       this.keyStoreProvider = null;
/* 366 */       this.keyStorePwdURL = null;
/*     */       
/*     */ 
/*     */ 
/* 370 */       return;
/*     */     }
/*     */     
/* 373 */     URL localURL = null;
/* 374 */     if (policyFileName != null) {
/* 375 */       File localFile = new File(policyFileName);
/* 376 */       localURL = new URL("file:" + localFile.getCanonicalPath());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 384 */     if ((paramString1 != null) && (paramString1.length() > 0))
/*     */     {
/* 386 */       paramString1 = PropertyExpander.expand(paramString1).replace(File.separatorChar, '/');
/*     */     }
/* 388 */     if ((paramString2 == null) || (paramString2.length() == 0)) {
/* 389 */       paramString2 = KeyStore.getDefaultType();
/*     */     }
/* 391 */     if ((paramString4 != null) && (paramString4.length() > 0))
/*     */     {
/* 393 */       paramString4 = PropertyExpander.expand(paramString4).replace(File.separatorChar, '/');
/*     */     }
/*     */     try
/*     */     {
/* 397 */       this.keyStore = PolicyUtil.getKeyStore(localURL, paramString1, paramString2, paramString3, paramString4, null);
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */ 
/*     */ 
/* 406 */       String str = "no password provided, and no callback handler available for retrieving password";
/*     */       
/*     */ 
/* 409 */       Throwable localThrowable = localIOException.getCause();
/* 410 */       if ((localThrowable != null) && ((localThrowable instanceof LoginException)))
/*     */       {
/* 412 */         if (str.equals(localThrowable.getMessage()))
/*     */         {
/*     */ 
/* 415 */           throw new IOException(str); }
/*     */       }
/* 417 */       throw localIOException;
/*     */     }
/*     */     
/*     */ 
/* 421 */     this.keyStoreName = paramString1;
/* 422 */     this.keyStoreType = paramString2;
/* 423 */     this.keyStoreProvider = paramString3;
/* 424 */     this.keyStorePwdURL = paramString4;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean addEntry(PolicyEntry paramPolicyEntry, int paramInt)
/*     */   {
/* 435 */     if (paramInt < 0)
/*     */     {
/* 437 */       this.policyEntries.addElement(paramPolicyEntry);
/* 438 */       this.parser.add(paramPolicyEntry.getGrantEntry());
/*     */     }
/*     */     else {
/* 441 */       PolicyEntry localPolicyEntry = (PolicyEntry)this.policyEntries.elementAt(paramInt);
/* 442 */       this.parser.replace(localPolicyEntry.getGrantEntry(), paramPolicyEntry.getGrantEntry());
/* 443 */       this.policyEntries.setElementAt(paramPolicyEntry, paramInt);
/*     */     }
/* 445 */     return true;
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
/*     */   boolean addPrinEntry(PolicyEntry paramPolicyEntry, PolicyParser.PrincipalEntry paramPrincipalEntry, int paramInt)
/*     */   {
/* 459 */     PolicyParser.GrantEntry localGrantEntry = paramPolicyEntry.getGrantEntry();
/* 460 */     if (localGrantEntry.contains(paramPrincipalEntry) == true) {
/* 461 */       return false;
/*     */     }
/* 463 */     LinkedList localLinkedList = localGrantEntry.principals;
/*     */     
/* 465 */     if (paramInt != -1) {
/* 466 */       localLinkedList.set(paramInt, paramPrincipalEntry);
/*     */     } else {
/* 468 */       localLinkedList.add(paramPrincipalEntry);
/*     */     }
/* 470 */     this.modified = true;
/* 471 */     return true;
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
/*     */   boolean addPermEntry(PolicyEntry paramPolicyEntry, PolicyParser.PermissionEntry paramPermissionEntry, int paramInt)
/*     */   {
/* 485 */     PolicyParser.GrantEntry localGrantEntry = paramPolicyEntry.getGrantEntry();
/* 486 */     if (localGrantEntry.contains(paramPermissionEntry) == true) {
/* 487 */       return false;
/*     */     }
/* 489 */     Vector localVector = localGrantEntry.permissionEntries;
/*     */     
/* 491 */     if (paramInt != -1) {
/* 492 */       localVector.setElementAt(paramPermissionEntry, paramInt);
/*     */     } else {
/* 494 */       localVector.addElement(paramPermissionEntry);
/*     */     }
/* 496 */     this.modified = true;
/* 497 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean removePermEntry(PolicyEntry paramPolicyEntry, PolicyParser.PermissionEntry paramPermissionEntry)
/*     */   {
/* 507 */     PolicyParser.GrantEntry localGrantEntry = paramPolicyEntry.getGrantEntry();
/* 508 */     this.modified = localGrantEntry.remove(paramPermissionEntry);
/* 509 */     return this.modified;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean removeEntry(PolicyEntry paramPolicyEntry)
/*     */   {
/* 517 */     this.parser.remove(paramPolicyEntry.getGrantEntry());
/* 518 */     this.modified = true;
/* 519 */     return this.policyEntries.removeElement(paramPolicyEntry);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   PolicyEntry[] getEntry()
/*     */   {
/* 527 */     if (this.policyEntries.size() > 0) {
/* 528 */       PolicyEntry[] arrayOfPolicyEntry = new PolicyEntry[this.policyEntries.size()];
/* 529 */       for (int i = 0; i < this.policyEntries.size(); i++)
/* 530 */         arrayOfPolicyEntry[i] = ((PolicyEntry)this.policyEntries.elementAt(i));
/* 531 */       return arrayOfPolicyEntry;
/*     */     }
/* 533 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   PublicKey getPublicKeyAlias(String paramString)
/*     */     throws KeyStoreException
/*     */   {
/* 541 */     if (this.keyStore == null) {
/* 542 */       return null;
/*     */     }
/*     */     
/* 545 */     Certificate localCertificate = this.keyStore.getCertificate(paramString);
/* 546 */     if (localCertificate == null) {
/* 547 */       return null;
/*     */     }
/* 549 */     PublicKey localPublicKey = localCertificate.getPublicKey();
/* 550 */     return localPublicKey;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   String[] getPublicKeyAlias()
/*     */     throws KeyStoreException
/*     */   {
/* 558 */     int i = 0;
/* 559 */     String[] arrayOfString = null;
/*     */     
/* 561 */     if (this.keyStore == null) {
/* 562 */       return null;
/*     */     }
/* 564 */     Enumeration localEnumeration = this.keyStore.aliases();
/*     */     
/*     */ 
/* 567 */     while (localEnumeration.hasMoreElements()) {
/* 568 */       localEnumeration.nextElement();
/* 569 */       i++;
/*     */     }
/*     */     
/* 572 */     if (i > 0)
/*     */     {
/* 574 */       arrayOfString = new String[i];
/* 575 */       i = 0;
/* 576 */       localEnumeration = this.keyStore.aliases();
/* 577 */       while (localEnumeration.hasMoreElements()) {
/* 578 */         arrayOfString[i] = new String((String)localEnumeration.nextElement());
/* 579 */         i++;
/*     */       }
/*     */     }
/* 582 */     return arrayOfString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   String[] parseSigners(String paramString)
/*     */   {
/* 591 */     String[] arrayOfString = null;
/* 592 */     int i = 1;
/* 593 */     int j = 0;
/* 594 */     int k = 0;
/* 595 */     int m = 0;
/*     */     
/*     */ 
/* 598 */     while (k >= 0) {
/* 599 */       k = paramString.indexOf(',', j);
/* 600 */       if (k >= 0) {
/* 601 */         i++;
/* 602 */         j = k + 1;
/*     */       }
/*     */     }
/* 605 */     arrayOfString = new String[i];
/*     */     
/*     */ 
/* 608 */     k = 0;
/* 609 */     j = 0;
/* 610 */     while (k >= 0) {
/* 611 */       if ((k = paramString.indexOf(',', j)) >= 0)
/*     */       {
/*     */ 
/* 614 */         arrayOfString[m] = paramString.substring(j, k).trim();
/* 615 */         m++;
/* 616 */         j = k + 1;
/*     */       }
/*     */       else {
/* 619 */         arrayOfString[m] = paramString.substring(j).trim();
/*     */       }
/*     */     }
/* 622 */     return arrayOfString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void verifyPrincipal(String paramString1, String paramString2)
/*     */     throws ClassNotFoundException, InstantiationException
/*     */   {
/* 632 */     if ((paramString1.equals("WILDCARD_PRINCIPAL_CLASS")) || 
/* 633 */       (paramString1.equals("PolicyParser.REPLACE_NAME"))) {
/* 634 */       return;
/*     */     }
/* 636 */     Class localClass1 = Class.forName("java.security.Principal");
/* 637 */     Class localClass2 = Class.forName(paramString1, true, 
/* 638 */       Thread.currentThread().getContextClassLoader());
/* 639 */     Object localObject; if (!localClass1.isAssignableFrom(localClass2))
/*     */     {
/* 641 */       localObject = new MessageFormat(getMessage("Illegal.Principal.Type.type"));
/* 642 */       Object[] arrayOfObject = { paramString1 };
/* 643 */       throw new InstantiationException(((MessageFormat)localObject).format(arrayOfObject));
/*     */     }
/*     */     
/* 646 */     if ("javax.security.auth.x500.X500Principal".equals(localClass2.getName()))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 652 */       localObject = new X500Principal(paramString2);
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
/*     */   void verifyPermission(String paramString1, String paramString2, String paramString3)
/*     */     throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
/*     */   {
/* 671 */     Class localClass = Class.forName(paramString1, true, 
/* 672 */       Thread.currentThread().getContextClassLoader());
/* 673 */     Constructor localConstructor = null;
/* 674 */     Vector localVector = new Vector(2);
/* 675 */     if (paramString2 != null) localVector.add(paramString2);
/* 676 */     if (paramString3 != null) localVector.add(paramString3);
/* 677 */     switch (localVector.size()) {
/*     */     case 0: 
/*     */       try {
/* 680 */         localConstructor = localClass.getConstructor(NOPARAMS);
/*     */       }
/*     */       catch (NoSuchMethodException localNoSuchMethodException1)
/*     */       {
/* 684 */         localVector.add(null);
/*     */       }
/*     */     case 1: 
/*     */       try
/*     */       {
/* 689 */         localConstructor = localClass.getConstructor(ONEPARAMS);
/*     */       }
/*     */       catch (NoSuchMethodException localNoSuchMethodException2)
/*     */       {
/* 693 */         localVector.add(null);
/*     */       }
/*     */     
/*     */     case 2: 
/* 697 */       localConstructor = localClass.getConstructor(TWOPARAMS);
/*     */     }
/*     */     
/* 700 */     Object[] arrayOfObject = localVector.toArray();
/* 701 */     Permission localPermission = (Permission)localConstructor.newInstance(arrayOfObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void parseArgs(String[] paramArrayOfString)
/*     */   {
/* 709 */     int i = 0;
/*     */     
/* 711 */     for (i = 0; (i < paramArrayOfString.length) && (paramArrayOfString[i].startsWith("-")); i++)
/*     */     {
/* 713 */       String str = paramArrayOfString[i];
/*     */       
/* 715 */       if (collator.compare(str, "-file") == 0) {
/* 716 */         i++; if (i == paramArrayOfString.length) usage();
/* 717 */         policyFileName = paramArrayOfString[i];
/*     */       }
/*     */       else {
/* 720 */         MessageFormat localMessageFormat = new MessageFormat(getMessage("Illegal.option.option"));
/* 721 */         Object[] arrayOfObject = { str };
/* 722 */         System.err.println(localMessageFormat.format(arrayOfObject));
/* 723 */         usage();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static void usage() {
/* 729 */     System.out.println(getMessage("Usage.policytool.options."));
/* 730 */     System.out.println();
/* 731 */     System.out.println(
/* 732 */       getMessage(".file.file.policy.file.location"));
/* 733 */     System.out.println();
/*     */     
/* 735 */     System.exit(1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] paramArrayOfString)
/*     */   {
/* 742 */     parseArgs(paramArrayOfString);
/* 743 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 745 */         ToolWindow localToolWindow = new ToolWindow(new PolicyTool(null));
/* 746 */         localToolWindow.displayToolWindow(this.val$args);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static String splitToWords(String paramString)
/*     */   {
/* 755 */     return paramString.replaceAll("([A-Z])", " $1");
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
/*     */   static String getMessage(String paramString)
/*     */   {
/* 768 */     return removeMnemonicAmpersand(rb.getString(paramString));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int getMnemonicInt(String paramString)
/*     */   {
/* 780 */     String str = rb.getString(paramString);
/* 781 */     return findMnemonicInt(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int getDisplayedMnemonicIndex(String paramString)
/*     */   {
/* 792 */     String str = rb.getString(paramString);
/* 793 */     return findMnemonicIndex(str);
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
/*     */   private static int findMnemonicInt(String paramString)
/*     */   {
/* 806 */     for (int i = 0; i < paramString.length() - 1; i++) {
/* 807 */       if (paramString.charAt(i) == '&') {
/* 808 */         if (paramString.charAt(i + 1) != '&') {
/* 809 */           return KeyEvent.getExtendedKeyCodeForChar(paramString.charAt(i + 1));
/*     */         }
/* 811 */         i++;
/*     */       }
/*     */     }
/*     */     
/* 815 */     return 0;
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
/*     */   private static int findMnemonicIndex(String paramString)
/*     */   {
/* 828 */     for (int i = 0; i < paramString.length() - 1; i++) {
/* 829 */       if (paramString.charAt(i) == '&') {
/* 830 */         if (paramString.charAt(i + 1) != '&')
/*     */         {
/* 832 */           return i;
/*     */         }
/* 834 */         i++;
/*     */       }
/*     */     }
/*     */     
/* 838 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String removeMnemonicAmpersand(String paramString)
/*     */   {
/* 850 */     StringBuilder localStringBuilder = new StringBuilder();
/* 851 */     for (int i = 0; i < paramString.length(); i++) {
/* 852 */       char c = paramString.charAt(i);
/* 853 */       if ((c != '&') || (i == paramString.length() - 1) || 
/* 854 */         (paramString.charAt(i + 1) == '&')) {
/* 855 */         localStringBuilder.append(c);
/*     */       }
/*     */     }
/* 858 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\tools\policytool\PolicyTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*      */ package sun.security.provider;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.File;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.Reader;
/*      */ import java.io.StreamTokenizer;
/*      */ import java.io.Writer;
/*      */ import java.security.GeneralSecurityException;
/*      */ import java.security.Principal;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TreeMap;
/*      */ import java.util.Vector;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.net.www.ParseUtil;
/*      */ import sun.security.util.Debug;
/*      */ import sun.security.util.PropertyExpander;
/*      */ import sun.security.util.PropertyExpander.ExpandException;
/*      */ import sun.security.util.ResourcesMgr;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PolicyParser
/*      */ {
/*      */   private static final String EXTDIRS_PROPERTY = "java.ext.dirs";
/*      */   private static final String OLD_EXTDIRS_EXPANSION = "${java.ext.dirs}";
/*      */   static final String EXTDIRS_EXPANSION = "${{java.ext.dirs}}";
/*      */   private Vector<GrantEntry> grantEntries;
/*      */   private Map<String, DomainEntry> domainEntries;
/*   98 */   private static final Debug debug = Debug.getInstance("parser", "\t[Policy Parser]");
/*      */   
/*      */   private StreamTokenizer st;
/*      */   private int lookahead;
/*  102 */   private boolean expandProp = false;
/*  103 */   private String keyStoreUrlString = null;
/*  104 */   private String keyStoreType = null;
/*  105 */   private String keyStoreProvider = null;
/*  106 */   private String storePassURL = null;
/*      */   
/*      */   private String expand(String paramString)
/*      */     throws ExpandException
/*      */   {
/*  111 */     return expand(paramString, false);
/*      */   }
/*      */   
/*      */   private String expand(String paramString, boolean paramBoolean)
/*      */     throws ExpandException
/*      */   {
/*  117 */     if (!this.expandProp) {
/*  118 */       return paramString;
/*      */     }
/*  120 */     return PropertyExpander.expand(paramString, paramBoolean);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PolicyParser()
/*      */   {
/*  129 */     this.grantEntries = new Vector();
/*      */   }
/*      */   
/*      */   public PolicyParser(boolean paramBoolean)
/*      */   {
/*  134 */     this();
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
/*      */   public void read(Reader paramReader)
/*      */     throws ParsingException, IOException
/*      */   {
/*  154 */     if (!(paramReader instanceof BufferedReader)) {
/*  155 */       paramReader = new BufferedReader(paramReader);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  165 */     this.st = new StreamTokenizer(paramReader);
/*      */     
/*  167 */     this.st.resetSyntax();
/*  168 */     this.st.wordChars(97, 122);
/*  169 */     this.st.wordChars(65, 90);
/*  170 */     this.st.wordChars(46, 46);
/*  171 */     this.st.wordChars(48, 57);
/*  172 */     this.st.wordChars(95, 95);
/*  173 */     this.st.wordChars(36, 36);
/*  174 */     this.st.wordChars(160, 255);
/*  175 */     this.st.whitespaceChars(0, 32);
/*  176 */     this.st.commentChar(47);
/*  177 */     this.st.quoteChar(39);
/*  178 */     this.st.quoteChar(34);
/*  179 */     this.st.lowerCaseMode(false);
/*  180 */     this.st.ordinaryChar(47);
/*  181 */     this.st.slashSlashComments(true);
/*  182 */     this.st.slashStarComments(true);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  193 */     this.lookahead = this.st.nextToken();
/*  194 */     GrantEntry localGrantEntry = null;
/*  195 */     while (this.lookahead != -1) {
/*  196 */       if (peek("grant")) {
/*  197 */         localGrantEntry = parseGrantEntry();
/*      */         
/*  199 */         if (localGrantEntry != null)
/*  200 */           add(localGrantEntry);
/*  201 */       } else if ((peek("keystore")) && (this.keyStoreUrlString == null))
/*      */       {
/*      */ 
/*  204 */         parseKeyStoreEntry();
/*  205 */       } else if ((peek("keystorePasswordURL")) && (this.storePassURL == null))
/*      */       {
/*      */ 
/*  208 */         parseStorePassURL();
/*  209 */       } else if ((localGrantEntry == null) && (this.keyStoreUrlString == null) && (this.storePassURL == null) && 
/*  210 */         (peek("domain"))) {
/*  211 */         if (this.domainEntries == null) {
/*  212 */           this.domainEntries = new TreeMap();
/*      */         }
/*  214 */         DomainEntry localDomainEntry = parseDomainEntry();
/*  215 */         if (localDomainEntry != null) {
/*  216 */           String str = localDomainEntry.getName();
/*  217 */           if (!this.domainEntries.containsKey(str)) {
/*  218 */             this.domainEntries.put(str, localDomainEntry);
/*      */           }
/*      */           else {
/*  221 */             MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("duplicate.keystore.domain.name"));
/*      */             
/*  223 */             Object[] arrayOfObject = { str };
/*  224 */             throw new ParsingException(localMessageFormat.format(arrayOfObject));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  230 */       match(";");
/*      */     }
/*      */     
/*  233 */     if ((this.keyStoreUrlString == null) && (this.storePassURL != null))
/*      */     {
/*  235 */       throw new ParsingException(ResourcesMgr.getString("keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore"));
/*      */     }
/*      */   }
/*      */   
/*      */   public void add(GrantEntry paramGrantEntry)
/*      */   {
/*  241 */     this.grantEntries.addElement(paramGrantEntry);
/*      */   }
/*      */   
/*      */   public void replace(GrantEntry paramGrantEntry1, GrantEntry paramGrantEntry2)
/*      */   {
/*  246 */     this.grantEntries.setElementAt(paramGrantEntry2, this.grantEntries.indexOf(paramGrantEntry1));
/*      */   }
/*      */   
/*      */   public boolean remove(GrantEntry paramGrantEntry)
/*      */   {
/*  251 */     return this.grantEntries.removeElement(paramGrantEntry);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getKeyStoreUrl()
/*      */   {
/*      */     try
/*      */     {
/*  260 */       if ((this.keyStoreUrlString != null) && (this.keyStoreUrlString.length() != 0)) {
/*  261 */         return 
/*  262 */           expand(this.keyStoreUrlString, true).replace(File.separatorChar, '/');
/*      */       }
/*      */     } catch (ExpandException localExpandException) {
/*  265 */       if (debug != null) {
/*  266 */         debug.println(localExpandException.toString());
/*      */       }
/*  268 */       return null;
/*      */     }
/*  270 */     return null;
/*      */   }
/*      */   
/*      */   public void setKeyStoreUrl(String paramString) {
/*  274 */     this.keyStoreUrlString = paramString;
/*      */   }
/*      */   
/*      */   public String getKeyStoreType() {
/*  278 */     return this.keyStoreType;
/*      */   }
/*      */   
/*      */   public void setKeyStoreType(String paramString) {
/*  282 */     this.keyStoreType = paramString;
/*      */   }
/*      */   
/*      */   public String getKeyStoreProvider() {
/*  286 */     return this.keyStoreProvider;
/*      */   }
/*      */   
/*      */   public void setKeyStoreProvider(String paramString) {
/*  290 */     this.keyStoreProvider = paramString;
/*      */   }
/*      */   
/*      */   public String getStorePassURL() {
/*      */     try {
/*  295 */       if ((this.storePassURL != null) && (this.storePassURL.length() != 0)) {
/*  296 */         return 
/*  297 */           expand(this.storePassURL, true).replace(File.separatorChar, '/');
/*      */       }
/*      */     } catch (ExpandException localExpandException) {
/*  300 */       if (debug != null) {
/*  301 */         debug.println(localExpandException.toString());
/*      */       }
/*  303 */       return null;
/*      */     }
/*  305 */     return null;
/*      */   }
/*      */   
/*      */   public void setStorePassURL(String paramString) {
/*  309 */     this.storePassURL = paramString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Enumeration<GrantEntry> grantElements()
/*      */   {
/*  319 */     return this.grantEntries.elements();
/*      */   }
/*      */   
/*      */   public Collection<DomainEntry> getDomainEntries() {
/*  323 */     return this.domainEntries.values();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void write(Writer paramWriter)
/*      */   {
/*  332 */     PrintWriter localPrintWriter = new PrintWriter(new BufferedWriter(paramWriter));
/*      */     
/*  334 */     Enumeration localEnumeration = grantElements();
/*      */     
/*  336 */     localPrintWriter.println("/* AUTOMATICALLY GENERATED ON " + new Date() + "*/");
/*      */     
/*  338 */     localPrintWriter.println("/* DO NOT EDIT */");
/*  339 */     localPrintWriter.println();
/*      */     
/*      */ 
/*      */ 
/*  343 */     if (this.keyStoreUrlString != null) {
/*  344 */       writeKeyStoreEntry(localPrintWriter);
/*      */     }
/*  346 */     if (this.storePassURL != null) {
/*  347 */       writeStorePassURL(localPrintWriter);
/*      */     }
/*      */     
/*      */ 
/*  351 */     while (localEnumeration.hasMoreElements()) {
/*  352 */       GrantEntry localGrantEntry = (GrantEntry)localEnumeration.nextElement();
/*  353 */       localGrantEntry.write(localPrintWriter);
/*  354 */       localPrintWriter.println();
/*      */     }
/*  356 */     localPrintWriter.flush();
/*      */   }
/*      */   
/*      */ 
/*      */   private void parseKeyStoreEntry()
/*      */     throws ParsingException, IOException
/*      */   {
/*  363 */     match("keystore");
/*  364 */     this.keyStoreUrlString = match("quoted string");
/*      */     
/*      */ 
/*  367 */     if (!peek(",")) {
/*  368 */       return;
/*      */     }
/*  370 */     match(",");
/*      */     
/*  372 */     if (peek("\"")) {
/*  373 */       this.keyStoreType = match("quoted string");
/*      */     }
/*      */     else {
/*  376 */       throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.keystore.type"));
/*      */     }
/*      */     
/*      */ 
/*  380 */     if (!peek(",")) {
/*  381 */       return;
/*      */     }
/*  383 */     match(",");
/*      */     
/*  385 */     if (peek("\"")) {
/*  386 */       this.keyStoreProvider = match("quoted string");
/*      */     }
/*      */     else {
/*  389 */       throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.keystore.provider"));
/*      */     }
/*      */   }
/*      */   
/*      */   private void parseStorePassURL() throws ParsingException, IOException {
/*  394 */     match("keyStorePasswordURL");
/*  395 */     this.storePassURL = match("quoted string");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void writeKeyStoreEntry(PrintWriter paramPrintWriter)
/*      */   {
/*  402 */     paramPrintWriter.print("keystore \"");
/*  403 */     paramPrintWriter.print(this.keyStoreUrlString);
/*  404 */     paramPrintWriter.print('"');
/*  405 */     if ((this.keyStoreType != null) && (this.keyStoreType.length() > 0))
/*  406 */       paramPrintWriter.print(", \"" + this.keyStoreType + "\"");
/*  407 */     if ((this.keyStoreProvider != null) && (this.keyStoreProvider.length() > 0))
/*  408 */       paramPrintWriter.print(", \"" + this.keyStoreProvider + "\"");
/*  409 */     paramPrintWriter.println(";");
/*  410 */     paramPrintWriter.println();
/*      */   }
/*      */   
/*      */   private void writeStorePassURL(PrintWriter paramPrintWriter) {
/*  414 */     paramPrintWriter.print("keystorePasswordURL \"");
/*  415 */     paramPrintWriter.print(this.storePassURL);
/*  416 */     paramPrintWriter.print('"');
/*  417 */     paramPrintWriter.println(";");
/*  418 */     paramPrintWriter.println();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private GrantEntry parseGrantEntry()
/*      */     throws ParsingException, IOException
/*      */   {
/*  427 */     GrantEntry localGrantEntry = new GrantEntry();
/*  428 */     LinkedList localLinkedList = null;
/*  429 */     int i = 0;
/*      */     
/*  431 */     match("grant");
/*      */     Object localObject1;
/*  433 */     Object localObject3; Object localObject2; while (!peek("{"))
/*      */     {
/*  435 */       if (peekAndMatch("Codebase")) {
/*  436 */         if (localGrantEntry.codeBase != null)
/*      */         {
/*      */ 
/*      */ 
/*  440 */           throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("multiple.Codebase.expressions")); }
/*  441 */         localGrantEntry.codeBase = match("quoted string");
/*  442 */         peekAndMatch(",");
/*  443 */       } else if (peekAndMatch("SignedBy")) {
/*  444 */         if (localGrantEntry.signedBy != null)
/*      */         {
/*      */ 
/*  447 */           throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("multiple.SignedBy.expressions"));
/*      */         }
/*  449 */         localGrantEntry.signedBy = match("quoted string");
/*      */         
/*      */ 
/*  452 */         localObject1 = new StringTokenizer(localGrantEntry.signedBy, ",", true);
/*      */         
/*  454 */         int k = 0;
/*  455 */         int m = 0;
/*  456 */         while (((StringTokenizer)localObject1).hasMoreTokens()) {
/*  457 */           localObject3 = ((StringTokenizer)localObject1).nextToken().trim();
/*  458 */           if (((String)localObject3).equals(",")) {
/*  459 */             m++;
/*  460 */           } else if (((String)localObject3).length() > 0)
/*  461 */             k++;
/*      */         }
/*  463 */         if (k <= m)
/*      */         {
/*      */ 
/*  466 */           throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("SignedBy.has.empty.alias"));
/*      */         }
/*      */         
/*  469 */         peekAndMatch(",");
/*  470 */       } else if (peekAndMatch("Principal")) {
/*  471 */         if (localLinkedList == null) {
/*  472 */           localLinkedList = new LinkedList();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  478 */         if (peek("\""))
/*      */         {
/*      */ 
/*  481 */           localObject1 = "PolicyParser.REPLACE_NAME";
/*  482 */           localObject2 = match("principal type");
/*      */         }
/*      */         else {
/*  485 */           if (peek("*")) {
/*  486 */             match("*");
/*  487 */             localObject1 = "WILDCARD_PRINCIPAL_CLASS";
/*      */           } else {
/*  489 */             localObject1 = match("principal type");
/*      */           }
/*      */           
/*      */ 
/*  493 */           if (peek("*")) {
/*  494 */             match("*");
/*  495 */             localObject2 = "WILDCARD_PRINCIPAL_NAME";
/*      */           } else {
/*  497 */             localObject2 = match("quoted string");
/*      */           }
/*      */           
/*      */ 
/*  501 */           if ((((String)localObject1).equals("WILDCARD_PRINCIPAL_CLASS")) && 
/*  502 */             (!((String)localObject2).equals("WILDCARD_PRINCIPAL_NAME"))) {
/*  503 */             if (debug != null) {
/*  504 */               debug.println("disallowing principal that has WILDCARD class but no WILDCARD name");
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  510 */             throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name"));
/*      */           }
/*      */         }
/*      */         try
/*      */         {
/*  515 */           localObject2 = expand((String)localObject2);
/*      */           
/*      */ 
/*  518 */           if ((((String)localObject1).equals("javax.security.auth.x500.X500Principal")) && 
/*  519 */             (!((String)localObject2).equals("WILDCARD_PRINCIPAL_NAME")))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  526 */             X500Principal localX500Principal = new X500Principal(new X500Principal((String)localObject2).toString());
/*  527 */             localObject2 = localX500Principal.getName();
/*      */           }
/*      */           
/*      */ 
/*  531 */           localLinkedList.add(new PrincipalEntry((String)localObject1, (String)localObject2));
/*      */ 
/*      */         }
/*      */         catch (ExpandException localExpandException3)
/*      */         {
/*  536 */           if (debug != null) {
/*  537 */             debug.println("principal name expansion failed: " + (String)localObject2);
/*      */           }
/*      */           
/*  540 */           i = 1;
/*      */         }
/*  542 */         peekAndMatch(",");
/*      */       }
/*      */       else
/*      */       {
/*  546 */         throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.codeBase.or.SignedBy.or.Principal"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  551 */     if (localLinkedList != null) localGrantEntry.principals = localLinkedList;
/*  552 */     match("{");
/*      */     
/*  554 */     while (!peek("}")) {
/*  555 */       if (peek("Permission")) {
/*      */         try {
/*  557 */           localObject1 = parsePermissionEntry();
/*  558 */           localGrantEntry.add((PermissionEntry)localObject1);
/*      */         }
/*      */         catch (ExpandException localExpandException1) {
/*  561 */           if (debug != null) {
/*  562 */             debug.println(localExpandException1.toString());
/*      */           }
/*  564 */           skipEntry();
/*      */         }
/*  566 */         match(";");
/*      */       }
/*      */       else
/*      */       {
/*  570 */         throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.permission.entry"));
/*      */       }
/*      */     }
/*      */     
/*  574 */     match("}");
/*      */     try
/*      */     {
/*  577 */       if (localGrantEntry.signedBy != null) localGrantEntry.signedBy = expand(localGrantEntry.signedBy);
/*  578 */       if (localGrantEntry.codeBase != null)
/*      */       {
/*      */ 
/*  581 */         if (localGrantEntry.codeBase.equals("${java.ext.dirs}")) {
/*  582 */           localGrantEntry.codeBase = "${{java.ext.dirs}}";
/*      */         }
/*      */         int j;
/*  585 */         if ((j = localGrantEntry.codeBase.indexOf("${{java.ext.dirs}}")) < 0)
/*      */         {
/*  587 */           localGrantEntry.codeBase = expand(localGrantEntry.codeBase, true).replace(File.separatorChar, '/');
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  592 */           localObject2 = parseExtDirs(localGrantEntry.codeBase, j);
/*  593 */           if ((localObject2 != null) && (localObject2.length > 0)) {
/*  594 */             for (int n = 0; n < localObject2.length; n++) {
/*  595 */               localObject3 = (GrantEntry)localGrantEntry.clone();
/*  596 */               ((GrantEntry)localObject3).codeBase = localObject2[n];
/*  597 */               add((GrantEntry)localObject3);
/*      */               
/*  599 */               if (debug != null) {
/*  600 */                 debug.println("creating policy entry for expanded java.ext.dirs path:\n\t\t" + localObject2[n]);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  606 */           i = 1;
/*      */         }
/*      */       }
/*      */     } catch (ExpandException localExpandException2) {
/*  610 */       if (debug != null) {
/*  611 */         debug.println(localExpandException2.toString());
/*      */       }
/*  613 */       return null;
/*      */     }
/*      */     
/*  616 */     return i == 1 ? null : localGrantEntry;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private PermissionEntry parsePermissionEntry()
/*      */     throws ParsingException, IOException, ExpandException
/*      */   {
/*  625 */     PermissionEntry localPermissionEntry = new PermissionEntry();
/*      */     
/*      */ 
/*  628 */     match("Permission");
/*  629 */     localPermissionEntry.permission = match("permission type");
/*      */     
/*  631 */     if (peek("\""))
/*      */     {
/*  633 */       localPermissionEntry.name = expand(match("quoted string"));
/*      */     }
/*      */     
/*  636 */     if (!peek(",")) {
/*  637 */       return localPermissionEntry;
/*      */     }
/*  639 */     match(",");
/*      */     
/*  641 */     if (peek("\"")) {
/*  642 */       localPermissionEntry.action = expand(match("quoted string"));
/*  643 */       if (!peek(",")) {
/*  644 */         return localPermissionEntry;
/*      */       }
/*  646 */       match(",");
/*      */     }
/*      */     
/*  649 */     if (peekAndMatch("SignedBy")) {
/*  650 */       localPermissionEntry.signedBy = expand(match("quoted string"));
/*      */     }
/*  652 */     return localPermissionEntry;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private DomainEntry parseDomainEntry()
/*      */     throws ParsingException, IOException
/*      */   {
/*  661 */     int i = 0;
/*      */     
/*  663 */     String str = null;
/*  664 */     Object localObject = new HashMap();
/*      */     
/*  666 */     match("domain");
/*  667 */     str = match("domain name");
/*      */     
/*  669 */     while (!peek("{"))
/*      */     {
/*  671 */       localObject = parseProperties("{");
/*      */     }
/*  673 */     match("{");
/*  674 */     DomainEntry localDomainEntry = new DomainEntry(str, (Map)localObject);
/*      */     
/*  676 */     while (!peek("}"))
/*      */     {
/*  678 */       match("keystore");
/*  679 */       str = match("keystore name");
/*      */       
/*  681 */       if (!peek("}")) {
/*  682 */         localObject = parseProperties(";");
/*      */       }
/*  684 */       match(";");
/*  685 */       localDomainEntry.add(new KeyStoreEntry(str, (Map)localObject));
/*      */     }
/*  687 */     match("}");
/*      */     
/*  689 */     return i == 1 ? null : localDomainEntry;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map<String, String> parseProperties(String paramString)
/*      */     throws ParsingException, IOException
/*      */   {
/*  698 */     HashMap localHashMap = new HashMap();
/*      */     
/*      */ 
/*  701 */     while (!peek(paramString)) {
/*  702 */       String str1 = match("property name");
/*  703 */       match("=");
/*      */       String str2;
/*      */       try {
/*  706 */         str2 = expand(match("quoted string"));
/*      */       } catch (ExpandException localExpandException) {
/*  708 */         throw new IOException(localExpandException.getLocalizedMessage());
/*      */       }
/*  710 */       localHashMap.put(str1.toLowerCase(Locale.ENGLISH), str2);
/*      */     }
/*      */     
/*  713 */     return localHashMap;
/*      */   }
/*      */   
/*      */ 
/*      */   static String[] parseExtDirs(String paramString, int paramInt)
/*      */   {
/*  719 */     String str1 = System.getProperty("java.ext.dirs");
/*  720 */     String str2 = paramInt > 0 ? paramString.substring(0, paramInt) : "file:";
/*  721 */     int i = paramInt + "${{java.ext.dirs}}".length();
/*  722 */     String str3 = i < paramString.length() ? paramString.substring(i) : (String)null;
/*      */     
/*      */ 
/*  725 */     String[] arrayOfString = null;
/*      */     
/*  727 */     if (str1 != null) {
/*  728 */       StringTokenizer localStringTokenizer = new StringTokenizer(str1, File.pathSeparator);
/*      */       
/*  730 */       int j = localStringTokenizer.countTokens();
/*  731 */       arrayOfString = new String[j];
/*  732 */       for (int k = 0; k < j; k++) {
/*  733 */         File localFile = new File(localStringTokenizer.nextToken());
/*  734 */         arrayOfString[k] = 
/*  735 */           ParseUtil.encodePath(localFile.getAbsolutePath());
/*      */         
/*  737 */         if (!arrayOfString[k].startsWith("/")) {
/*  738 */           arrayOfString[k] = ("/" + arrayOfString[k]);
/*      */         }
/*      */         
/*      */ 
/*  742 */         String str4 = str3 == null ? "/*" : arrayOfString[k].endsWith("/") ? "*" : str3;
/*      */         
/*      */ 
/*  745 */         arrayOfString[k] = (str2 + arrayOfString[k] + str4);
/*      */       }
/*      */     }
/*  748 */     return arrayOfString;
/*      */   }
/*      */   
/*      */   private boolean peekAndMatch(String paramString)
/*      */     throws ParsingException, IOException
/*      */   {
/*  754 */     if (peek(paramString)) {
/*  755 */       match(paramString);
/*  756 */       return true;
/*      */     }
/*  758 */     return false;
/*      */   }
/*      */   
/*      */   private boolean peek(String paramString)
/*      */   {
/*  763 */     boolean bool = false;
/*      */     
/*  765 */     switch (this.lookahead)
/*      */     {
/*      */     case -3: 
/*  768 */       if (paramString.equalsIgnoreCase(this.st.sval))
/*  769 */         bool = true;
/*      */       break;
/*      */     case 44: 
/*  772 */       if (paramString.equalsIgnoreCase(","))
/*  773 */         bool = true;
/*      */       break;
/*      */     case 123: 
/*  776 */       if (paramString.equalsIgnoreCase("{"))
/*  777 */         bool = true;
/*      */       break;
/*      */     case 125: 
/*  780 */       if (paramString.equalsIgnoreCase("}"))
/*  781 */         bool = true;
/*      */       break;
/*      */     case 34: 
/*  784 */       if (paramString.equalsIgnoreCase("\""))
/*  785 */         bool = true;
/*      */       break;
/*      */     case 42: 
/*  788 */       if (paramString.equalsIgnoreCase("*"))
/*  789 */         bool = true;
/*      */       break;
/*      */     case 59: 
/*  792 */       if (paramString.equalsIgnoreCase(";")) {
/*  793 */         bool = true;
/*      */       }
/*      */       break;
/*      */     }
/*      */     
/*  798 */     return bool;
/*      */   }
/*      */   
/*      */   private String match(String paramString)
/*      */     throws ParsingException, IOException
/*      */   {
/*  804 */     String str = null;
/*      */     
/*  806 */     switch (this.lookahead)
/*      */     {
/*      */ 
/*      */     case -2: 
/*  810 */       throw new ParsingException(this.st.lineno(), paramString, ResourcesMgr.getString("number.") + String.valueOf(this.st.nval));
/*      */     
/*      */ 
/*      */     case -1: 
/*  814 */       MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("expected.expect.read.end.of.file."));
/*  815 */       Object[] arrayOfObject = { paramString };
/*  816 */       throw new ParsingException(localMessageFormat.format(arrayOfObject));
/*      */     case -3: 
/*  818 */       if (paramString.equalsIgnoreCase(this.st.sval)) {
/*  819 */         this.lookahead = this.st.nextToken();
/*  820 */       } else if (paramString.equalsIgnoreCase("permission type")) {
/*  821 */         str = this.st.sval;
/*  822 */         this.lookahead = this.st.nextToken();
/*  823 */       } else if (paramString.equalsIgnoreCase("principal type")) {
/*  824 */         str = this.st.sval;
/*  825 */         this.lookahead = this.st.nextToken();
/*  826 */       } else if ((paramString.equalsIgnoreCase("domain name")) || 
/*  827 */         (paramString.equalsIgnoreCase("keystore name")) || 
/*  828 */         (paramString.equalsIgnoreCase("property name"))) {
/*  829 */         str = this.st.sval;
/*  830 */         this.lookahead = this.st.nextToken();
/*      */       } else {
/*  832 */         throw new ParsingException(this.st.lineno(), paramString, this.st.sval);
/*      */       }
/*      */       
/*      */       break;
/*      */     case 34: 
/*  837 */       if (paramString.equalsIgnoreCase("quoted string")) {
/*  838 */         str = this.st.sval;
/*  839 */         this.lookahead = this.st.nextToken();
/*  840 */       } else if (paramString.equalsIgnoreCase("permission type")) {
/*  841 */         str = this.st.sval;
/*  842 */         this.lookahead = this.st.nextToken();
/*  843 */       } else if (paramString.equalsIgnoreCase("principal type")) {
/*  844 */         str = this.st.sval;
/*  845 */         this.lookahead = this.st.nextToken();
/*      */       } else {
/*  847 */         throw new ParsingException(this.st.lineno(), paramString, this.st.sval);
/*      */       }
/*      */       break;
/*      */     case 44: 
/*  851 */       if (paramString.equalsIgnoreCase(",")) {
/*  852 */         this.lookahead = this.st.nextToken();
/*      */       } else
/*  854 */         throw new ParsingException(this.st.lineno(), paramString, ",");
/*      */       break;
/*      */     case 123: 
/*  857 */       if (paramString.equalsIgnoreCase("{")) {
/*  858 */         this.lookahead = this.st.nextToken();
/*      */       } else
/*  860 */         throw new ParsingException(this.st.lineno(), paramString, "{");
/*      */       break;
/*      */     case 125: 
/*  863 */       if (paramString.equalsIgnoreCase("}")) {
/*  864 */         this.lookahead = this.st.nextToken();
/*      */       } else
/*  866 */         throw new ParsingException(this.st.lineno(), paramString, "}");
/*      */       break;
/*      */     case 59: 
/*  869 */       if (paramString.equalsIgnoreCase(";")) {
/*  870 */         this.lookahead = this.st.nextToken();
/*      */       } else
/*  872 */         throw new ParsingException(this.st.lineno(), paramString, ";");
/*      */       break;
/*      */     case 42: 
/*  875 */       if (paramString.equalsIgnoreCase("*")) {
/*  876 */         this.lookahead = this.st.nextToken();
/*      */       } else
/*  878 */         throw new ParsingException(this.st.lineno(), paramString, "*");
/*      */       break;
/*      */     case 61: 
/*  881 */       if (paramString.equalsIgnoreCase("=")) {
/*  882 */         this.lookahead = this.st.nextToken();
/*      */       } else
/*  884 */         throw new ParsingException(this.st.lineno(), paramString, "=");
/*      */       break;
/*      */     default: 
/*  887 */       throw new ParsingException(this.st.lineno(), paramString, new String(new char[] { (char)this.lookahead }));
/*      */     }
/*      */     
/*  890 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void skipEntry()
/*      */     throws ParsingException, IOException
/*      */   {
/*  898 */     while (this.lookahead != 59) {
/*  899 */       switch (this.lookahead)
/*      */       {
/*      */ 
/*      */       case -2: 
/*  903 */         throw new ParsingException(this.st.lineno(), ";", ResourcesMgr.getString("number.") + String.valueOf(this.st.nval));
/*      */       
/*      */       case -1: 
/*  906 */         throw new ParsingException(ResourcesMgr.getString("expected.read.end.of.file.")); }
/*      */       
/*  908 */       this.lookahead = this.st.nextToken();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class GrantEntry
/*      */   {
/*      */     public String signedBy;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String codeBase;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LinkedList<PrincipalEntry> principals;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Vector<PermissionEntry> permissionEntries;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public GrantEntry()
/*      */     {
/*  951 */       this.principals = new LinkedList();
/*  952 */       this.permissionEntries = new Vector();
/*      */     }
/*      */     
/*      */     public GrantEntry(String paramString1, String paramString2) {
/*  956 */       this.codeBase = paramString2;
/*  957 */       this.signedBy = paramString1;
/*  958 */       this.principals = new LinkedList();
/*  959 */       this.permissionEntries = new Vector();
/*      */     }
/*      */     
/*      */     public void add(PermissionEntry paramPermissionEntry)
/*      */     {
/*  964 */       this.permissionEntries.addElement(paramPermissionEntry);
/*      */     }
/*      */     
/*      */     public boolean remove(PrincipalEntry paramPrincipalEntry)
/*      */     {
/*  969 */       return this.principals.remove(paramPrincipalEntry);
/*      */     }
/*      */     
/*      */     public boolean remove(PermissionEntry paramPermissionEntry)
/*      */     {
/*  974 */       return this.permissionEntries.removeElement(paramPermissionEntry);
/*      */     }
/*      */     
/*      */     public boolean contains(PrincipalEntry paramPrincipalEntry)
/*      */     {
/*  979 */       return this.principals.contains(paramPrincipalEntry);
/*      */     }
/*      */     
/*      */     public boolean contains(PermissionEntry paramPermissionEntry)
/*      */     {
/*  984 */       return this.permissionEntries.contains(paramPermissionEntry);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Enumeration<PermissionEntry> permissionElements()
/*      */     {
/*  991 */       return this.permissionEntries.elements();
/*      */     }
/*      */     
/*      */     public void write(PrintWriter paramPrintWriter)
/*      */     {
/*  996 */       paramPrintWriter.print("grant");
/*  997 */       if (this.signedBy != null) {
/*  998 */         paramPrintWriter.print(" signedBy \"");
/*  999 */         paramPrintWriter.print(this.signedBy);
/* 1000 */         paramPrintWriter.print('"');
/* 1001 */         if (this.codeBase != null)
/* 1002 */           paramPrintWriter.print(", ");
/*      */       }
/* 1004 */       if (this.codeBase != null) {
/* 1005 */         paramPrintWriter.print(" codeBase \"");
/* 1006 */         paramPrintWriter.print(this.codeBase);
/* 1007 */         paramPrintWriter.print('"');
/* 1008 */         if ((this.principals != null) && (this.principals.size() > 0))
/* 1009 */           paramPrintWriter.print(",\n"); }
/*      */       Object localObject2;
/* 1011 */       if ((this.principals != null) && (this.principals.size() > 0)) {
/* 1012 */         localObject1 = this.principals.iterator();
/* 1013 */         while (((Iterator)localObject1).hasNext()) {
/* 1014 */           paramPrintWriter.print("      ");
/* 1015 */           localObject2 = (PrincipalEntry)((Iterator)localObject1).next();
/* 1016 */           ((PrincipalEntry)localObject2).write(paramPrintWriter);
/* 1017 */           if (((Iterator)localObject1).hasNext())
/* 1018 */             paramPrintWriter.print(",\n");
/*      */         }
/*      */       }
/* 1021 */       paramPrintWriter.println(" {");
/* 1022 */       Object localObject1 = this.permissionEntries.elements();
/* 1023 */       while (((Enumeration)localObject1).hasMoreElements()) {
/* 1024 */         localObject2 = (PermissionEntry)((Enumeration)localObject1).nextElement();
/* 1025 */         paramPrintWriter.write("  ");
/* 1026 */         ((PermissionEntry)localObject2).write(paramPrintWriter);
/*      */       }
/* 1028 */       paramPrintWriter.println("};");
/*      */     }
/*      */     
/*      */     public Object clone() {
/* 1032 */       GrantEntry localGrantEntry = new GrantEntry();
/* 1033 */       localGrantEntry.codeBase = this.codeBase;
/* 1034 */       localGrantEntry.signedBy = this.signedBy;
/* 1035 */       localGrantEntry.principals = new LinkedList(this.principals);
/* 1036 */       localGrantEntry.permissionEntries = new Vector(this.permissionEntries);
/*      */       
/* 1038 */       return localGrantEntry;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static class PrincipalEntry
/*      */     implements Principal
/*      */   {
/*      */     public static final String WILDCARD_CLASS = "WILDCARD_PRINCIPAL_CLASS";
/*      */     
/*      */ 
/*      */     public static final String WILDCARD_NAME = "WILDCARD_PRINCIPAL_NAME";
/*      */     
/*      */ 
/*      */     public static final String REPLACE_NAME = "PolicyParser.REPLACE_NAME";
/*      */     
/*      */ 
/*      */     String principalClass;
/*      */     
/*      */     String principalName;
/*      */     
/*      */ 
/*      */     public PrincipalEntry(String paramString1, String paramString2)
/*      */     {
/* 1063 */       if ((paramString1 == null) || (paramString2 == null)) {
/* 1064 */         throw new NullPointerException(ResourcesMgr.getString("null.principalClass.or.principalName"));
/*      */       }
/* 1066 */       this.principalClass = paramString1;
/* 1067 */       this.principalName = paramString2;
/*      */     }
/*      */     
/*      */     boolean isWildcardName() {
/* 1071 */       return this.principalName.equals("WILDCARD_PRINCIPAL_NAME");
/*      */     }
/*      */     
/*      */     boolean isWildcardClass() {
/* 1075 */       return this.principalClass.equals("WILDCARD_PRINCIPAL_CLASS");
/*      */     }
/*      */     
/*      */     boolean isReplaceName() {
/* 1079 */       return this.principalClass.equals("PolicyParser.REPLACE_NAME");
/*      */     }
/*      */     
/*      */     public String getPrincipalClass() {
/* 1083 */       return this.principalClass;
/*      */     }
/*      */     
/*      */     public String getPrincipalName() {
/* 1087 */       return this.principalName;
/*      */     }
/*      */     
/*      */     public String getDisplayClass() {
/* 1091 */       if (isWildcardClass())
/* 1092 */         return "*";
/* 1093 */       if (isReplaceName()) {
/* 1094 */         return "";
/*      */       }
/* 1096 */       return this.principalClass;
/*      */     }
/*      */     
/*      */     public String getDisplayName() {
/* 1100 */       return getDisplayName(false);
/*      */     }
/*      */     
/*      */     public String getDisplayName(boolean paramBoolean) {
/* 1104 */       if (isWildcardName()) {
/* 1105 */         return "*";
/*      */       }
/*      */       
/* 1108 */       if (paramBoolean) return "\"" + this.principalName + "\"";
/* 1109 */       return this.principalName;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/* 1115 */       return this.principalName;
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/* 1120 */       if (!isReplaceName()) {
/* 1121 */         return getDisplayClass() + "/" + getDisplayName();
/*      */       }
/* 1123 */       return getDisplayName();
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
/*      */     public boolean equals(Object paramObject)
/*      */     {
/* 1137 */       if (this == paramObject) {
/* 1138 */         return true;
/*      */       }
/* 1140 */       if (!(paramObject instanceof PrincipalEntry)) {
/* 1141 */         return false;
/*      */       }
/* 1143 */       PrincipalEntry localPrincipalEntry = (PrincipalEntry)paramObject;
/* 1144 */       return (this.principalClass.equals(localPrincipalEntry.principalClass)) && 
/* 1145 */         (this.principalName.equals(localPrincipalEntry.principalName));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1155 */       return this.principalClass.hashCode();
/*      */     }
/*      */     
/*      */     public void write(PrintWriter paramPrintWriter) {
/* 1159 */       paramPrintWriter.print("principal " + getDisplayClass() + " " + 
/* 1160 */         getDisplayName(true));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class PermissionEntry
/*      */   {
/*      */     public String permission;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String name;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String action;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String signedBy;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public PermissionEntry() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public PermissionEntry(String paramString1, String paramString2, String paramString3)
/*      */     {
/* 1199 */       this.permission = paramString1;
/* 1200 */       this.name = paramString2;
/* 1201 */       this.action = paramString3;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1210 */       int i = this.permission.hashCode();
/* 1211 */       if (this.name != null) i ^= this.name.hashCode();
/* 1212 */       if (this.action != null) i ^= this.action.hashCode();
/* 1213 */       return i;
/*      */     }
/*      */     
/*      */     public boolean equals(Object paramObject)
/*      */     {
/* 1218 */       if (paramObject == this) {
/* 1219 */         return true;
/*      */       }
/* 1221 */       if (!(paramObject instanceof PermissionEntry)) {
/* 1222 */         return false;
/*      */       }
/* 1224 */       PermissionEntry localPermissionEntry = (PermissionEntry)paramObject;
/*      */       
/* 1226 */       if (this.permission == null) {
/* 1227 */         if (localPermissionEntry.permission != null) return false;
/*      */       }
/* 1229 */       else if (!this.permission.equals(localPermissionEntry.permission)) { return false;
/*      */       }
/*      */       
/* 1232 */       if (this.name == null) {
/* 1233 */         if (localPermissionEntry.name != null) return false;
/*      */       }
/* 1235 */       else if (!this.name.equals(localPermissionEntry.name)) { return false;
/*      */       }
/*      */       
/* 1238 */       if (this.action == null) {
/* 1239 */         if (localPermissionEntry.action != null) return false;
/*      */       }
/* 1241 */       else if (!this.action.equals(localPermissionEntry.action)) { return false;
/*      */       }
/*      */       
/* 1244 */       if (this.signedBy == null) {
/* 1245 */         if (localPermissionEntry.signedBy != null) return false;
/*      */       }
/* 1247 */       else if (!this.signedBy.equals(localPermissionEntry.signedBy)) { return false;
/*      */       }
/*      */       
/*      */ 
/* 1251 */       return true;
/*      */     }
/*      */     
/*      */     public void write(PrintWriter paramPrintWriter) {
/* 1255 */       paramPrintWriter.print("permission ");
/* 1256 */       paramPrintWriter.print(this.permission);
/* 1257 */       if (this.name != null) {
/* 1258 */         paramPrintWriter.print(" \"");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1266 */         paramPrintWriter.print(this.name.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\\\""));
/* 1267 */         paramPrintWriter.print('"');
/*      */       }
/* 1269 */       if (this.action != null) {
/* 1270 */         paramPrintWriter.print(", \"");
/* 1271 */         paramPrintWriter.print(this.action);
/* 1272 */         paramPrintWriter.print('"');
/*      */       }
/* 1274 */       if (this.signedBy != null) {
/* 1275 */         paramPrintWriter.print(", signedBy \"");
/* 1276 */         paramPrintWriter.print(this.signedBy);
/* 1277 */         paramPrintWriter.print('"');
/*      */       }
/* 1279 */       paramPrintWriter.println(";");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class DomainEntry
/*      */   {
/*      */     private final String name;
/*      */     
/*      */     private final Map<String, String> properties;
/*      */     private final Map<String, KeyStoreEntry> entries;
/*      */     
/*      */     DomainEntry(String paramString, Map<String, String> paramMap)
/*      */     {
/* 1293 */       this.name = paramString;
/* 1294 */       this.properties = paramMap;
/* 1295 */       this.entries = new HashMap();
/*      */     }
/*      */     
/*      */     String getName() {
/* 1299 */       return this.name;
/*      */     }
/*      */     
/*      */     Map<String, String> getProperties() {
/* 1303 */       return this.properties;
/*      */     }
/*      */     
/*      */     Collection<KeyStoreEntry> getEntries() {
/* 1307 */       return this.entries.values();
/*      */     }
/*      */     
/*      */     void add(KeyStoreEntry paramKeyStoreEntry) throws ParsingException {
/* 1311 */       String str = paramKeyStoreEntry.getName();
/* 1312 */       if (!this.entries.containsKey(str)) {
/* 1313 */         this.entries.put(str, paramKeyStoreEntry);
/*      */       } else {
/* 1315 */         MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("duplicate.keystore.name"));
/*      */         
/* 1317 */         Object[] arrayOfObject = { str };
/* 1318 */         throw new ParsingException(localMessageFormat.format(arrayOfObject));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1325 */       StringBuilder localStringBuilder = new StringBuilder("\ndomain ").append(this.name);
/*      */       Iterator localIterator;
/* 1327 */       if (this.properties != null)
/*      */       {
/* 1329 */         for (localIterator = this.properties.entrySet().iterator(); localIterator.hasNext();) { localObject = (Entry)localIterator.next();
/* 1330 */           localStringBuilder.append("\n        ").append((String)((Entry)localObject).getKey()).append('=')
/* 1331 */             .append((String)((Entry)localObject).getValue());
/*      */         } }
/*      */       Object localObject;
/* 1334 */       localStringBuilder.append(" {\n");
/*      */       
/* 1336 */       if (this.entries != null) {
/* 1337 */         for (localIterator = this.entries.values().iterator(); localIterator.hasNext();) { localObject = (KeyStoreEntry)localIterator.next();
/* 1338 */           localStringBuilder.append(localObject).append("\n");
/*      */         }
/*      */       }
/* 1341 */       localStringBuilder.append("}");
/*      */       
/* 1343 */       return localStringBuilder.toString();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class KeyStoreEntry
/*      */   {
/*      */     private final String name;
/*      */     
/*      */     private final Map<String, String> properties;
/*      */     
/*      */ 
/*      */     KeyStoreEntry(String paramString, Map<String, String> paramMap)
/*      */     {
/* 1357 */       this.name = paramString;
/* 1358 */       this.properties = paramMap;
/*      */     }
/*      */     
/*      */     String getName() {
/* 1362 */       return this.name;
/*      */     }
/*      */     
/*      */     Map<String, String> getProperties() {
/* 1366 */       return this.properties;
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/* 1371 */       StringBuilder localStringBuilder = new StringBuilder("\n    keystore ").append(this.name);
/* 1372 */       if (this.properties != null)
/*      */       {
/* 1374 */         for (Entry localEntry : this.properties.entrySet())
/*      */         {
/* 1376 */           localStringBuilder.append("\n        ").append((String)localEntry.getKey()).append('=').append((String)localEntry.getValue());
/*      */         }
/*      */       }
/* 1379 */       localStringBuilder.append(";");
/*      */       
/* 1381 */       return localStringBuilder.toString();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class ParsingException
/*      */     extends GeneralSecurityException
/*      */   {
/*      */     private static final long serialVersionUID = -4330692689482574072L;
/*      */     
/*      */ 
/*      */     private String i18nMessage;
/*      */     
/*      */ 
/*      */ 
/*      */     public ParsingException(String paramString)
/*      */     {
/* 1400 */       super();
/* 1401 */       this.i18nMessage = paramString;
/*      */     }
/*      */     
/*      */     public ParsingException(int paramInt, String paramString) {
/* 1405 */       super();
/*      */       
/* 1407 */       MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("line.number.msg"));
/* 1408 */       Object[] arrayOfObject = { new Integer(paramInt), paramString };
/* 1409 */       this.i18nMessage = localMessageFormat.format(arrayOfObject);
/*      */     }
/*      */     
/*      */     public ParsingException(int paramInt, String paramString1, String paramString2) {
/* 1413 */       super();
/*      */       
/*      */ 
/* 1416 */       MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("line.number.expected.expect.found.actual."));
/* 1417 */       Object[] arrayOfObject = { new Integer(paramInt), paramString1, paramString2 };
/* 1418 */       this.i18nMessage = localMessageFormat.format(arrayOfObject);
/*      */     }
/*      */     
/*      */     public String getLocalizedMessage()
/*      */     {
/* 1423 */       return this.i18nMessage;
/*      */     }
/*      */   }
/*      */   
/*      */   public static void main(String[] paramArrayOfString) throws Exception {
/* 1428 */     FileReader localFileReader = new FileReader(paramArrayOfString[0]);Object localObject1 = null;
/* 1429 */     try { FileWriter localFileWriter = new FileWriter(paramArrayOfString[1]);Object localObject2 = null;
/* 1430 */       try { PolicyParser localPolicyParser = new PolicyParser(true);
/* 1431 */         localPolicyParser.read(localFileReader);
/* 1432 */         localPolicyParser.write(localFileWriter);
/*      */       }
/*      */       catch (Throwable localThrowable4)
/*      */       {
/* 1428 */         localObject2 = localThrowable4;throw localThrowable4; } finally {} } catch (Throwable localThrowable2) { localObject1 = localThrowable2;throw localThrowable2;
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 1433 */       if (localFileReader != null) if (localObject1 != null) try { localFileReader.close(); } catch (Throwable localThrowable6) { ((Throwable)localObject1).addSuppressed(localThrowable6); } else localFileReader.close();
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\PolicyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
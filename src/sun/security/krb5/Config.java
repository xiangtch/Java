/*      */ package sun.security.krb5;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.net.InetAddress;
/*      */ import java.net.UnknownHostException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import sun.net.dns.ResolverConfiguration;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.security.krb5.internal.Krb5;
/*      */ import sun.security.krb5.internal.crypto.EType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Config
/*      */ {
/*   63 */   private static Config singleton = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   68 */   private Hashtable<String, Object> stanzaTable = new Hashtable();
/*      */   
/*   70 */   private static boolean DEBUG = Krb5.DEBUG;
/*      */   
/*      */ 
/*      */   private static final int BASE16_0 = 1;
/*      */   
/*      */ 
/*      */   private static final int BASE16_1 = 16;
/*      */   
/*      */ 
/*      */   private static final int BASE16_2 = 256;
/*      */   
/*      */ 
/*      */   private static final int BASE16_3 = 4096;
/*      */   
/*      */ 
/*      */   private final String defaultRealm;
/*      */   
/*      */ 
/*      */   private final String defaultKDC;
/*      */   
/*      */ 
/*      */   private static native String getWindowsDirectory(boolean paramBoolean);
/*      */   
/*      */ 
/*      */   public static synchronized Config getInstance()
/*      */     throws KrbException
/*      */   {
/*   97 */     if (singleton == null) {
/*   98 */       singleton = new Config();
/*      */     }
/*  100 */     return singleton;
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
/*      */   public static synchronized void refresh()
/*      */     throws KrbException
/*      */   {
/*  116 */     singleton = new Config();
/*  117 */     KdcComm.initStatic();
/*  118 */     EType.initStatic();
/*  119 */     Checksum.initStatic();
/*      */   }
/*      */   
/*      */ 
/*      */   private static boolean isMacosLionOrBetter()
/*      */   {
/*  125 */     String str1 = getProperty("os.name");
/*  126 */     if (!str1.contains("OS X")) {
/*  127 */       return false;
/*      */     }
/*      */     
/*  130 */     String str2 = getProperty("os.version");
/*  131 */     String[] arrayOfString = str2.split("\\.");
/*      */     
/*      */ 
/*  134 */     if (!arrayOfString[0].equals("10")) return false;
/*  135 */     if (arrayOfString.length < 2) { return false;
/*      */     }
/*      */     try
/*      */     {
/*  139 */       int i = Integer.parseInt(arrayOfString[1]);
/*  140 */       if (i >= 7) { return true;
/*      */       }
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException) {}
/*      */     
/*  145 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Config()
/*      */     throws KrbException
/*      */   {
/*  155 */     String str1 = getProperty("java.security.krb5.kdc");
/*  156 */     if (str1 != null)
/*      */     {
/*  158 */       this.defaultKDC = str1.replace(':', ' ');
/*      */     } else {
/*  160 */       this.defaultKDC = null;
/*      */     }
/*  162 */     this.defaultRealm = getProperty("java.security.krb5.realm");
/*  163 */     if (((this.defaultKDC == null) && (this.defaultRealm != null)) || ((this.defaultRealm == null) && (this.defaultKDC != null)))
/*      */     {
/*  165 */       throw new KrbException("System property java.security.krb5.kdc and java.security.krb5.realm both must be set or neither must be set.");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  174 */       String str2 = getJavaFileName();
/*  175 */       List localList; if (str2 != null) {
/*  176 */         localList = loadConfigFile(str2);
/*  177 */         this.stanzaTable = parseStanzaTable(localList);
/*  178 */         if (DEBUG) {
/*  179 */           System.out.println("Loaded from Java config");
/*      */         }
/*      */       } else {
/*  182 */         int i = 0;
/*  183 */         if (isMacosLionOrBetter()) {
/*      */           try {
/*  185 */             this.stanzaTable = SCDynamicStoreConfig.getConfig();
/*  186 */             if (DEBUG) {
/*  187 */               System.out.println("Loaded from SCDynamicStoreConfig");
/*      */             }
/*  189 */             i = 1;
/*      */           }
/*      */           catch (IOException localIOException2) {}
/*      */         }
/*      */         
/*  194 */         if (i == 0) {
/*  195 */           str2 = getNativeFileName();
/*  196 */           localList = loadConfigFile(str2);
/*  197 */           this.stanzaTable = parseStanzaTable(localList);
/*  198 */           if (DEBUG) {
/*  199 */             System.out.println("Loaded from native config");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException1) {}
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
/*      */   public String get(String... paramVarArgs)
/*      */   {
/*  229 */     Vector localVector = getString0(paramVarArgs);
/*  230 */     if (localVector == null) return null;
/*  231 */     return (String)localVector.lastElement();
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
/*      */   private Boolean getBooleanObject(String... paramVarArgs)
/*      */   {
/*  245 */     String str1 = get(paramVarArgs);
/*  246 */     if (str1 == null) {
/*  247 */       return null;
/*      */     }
/*  249 */     switch (str1.toLowerCase(Locale.US)) {
/*      */     case "true": case "yes": 
/*  251 */       return Boolean.TRUE;
/*      */     case "false": case "no": 
/*  253 */       return Boolean.FALSE;
/*      */     }
/*  255 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getAll(String... paramVarArgs)
/*      */   {
/*  265 */     Vector localVector = getString0(paramVarArgs);
/*  266 */     if (localVector == null) return null;
/*  267 */     StringBuilder localStringBuilder = new StringBuilder();
/*  268 */     int i = 1;
/*  269 */     for (String str : localVector) {
/*  270 */       if (i != 0) {
/*  271 */         localStringBuilder.append(str);
/*  272 */         i = 0;
/*      */       } else {
/*  274 */         localStringBuilder.append(' ').append(str);
/*      */       }
/*      */     }
/*  277 */     return localStringBuilder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean exists(String... paramVarArgs)
/*      */   {
/*  286 */     return get0(paramVarArgs) != null;
/*      */   }
/*      */   
/*      */   private Vector<String> getString0(String... paramVarArgs)
/*      */   {
/*      */     try
/*      */     {
/*  293 */       return (Vector)get0(paramVarArgs);
/*      */     } catch (ClassCastException localClassCastException) {
/*  295 */       throw new IllegalArgumentException(localClassCastException);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object get0(String... paramVarArgs)
/*      */   {
/*  304 */     Object localObject = this.stanzaTable;
/*      */     try {
/*  306 */       for (String str : paramVarArgs) {
/*  307 */         localObject = ((Hashtable)localObject).get(str);
/*  308 */         if (localObject == null) return null;
/*      */       }
/*  310 */       return localObject;
/*      */     } catch (ClassCastException localClassCastException) {
/*  312 */       throw new IllegalArgumentException(localClassCastException);
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
/*      */   public int getIntValue(String... paramVarArgs)
/*      */   {
/*  325 */     String str = get(paramVarArgs);
/*  326 */     int i = Integer.MIN_VALUE;
/*  327 */     if (str != null) {
/*      */       try {
/*  329 */         i = parseIntValue(str);
/*      */       } catch (NumberFormatException localNumberFormatException) {
/*  331 */         if (DEBUG) {
/*  332 */           System.out.println("Exception in getting value of " + 
/*  333 */             Arrays.toString(paramVarArgs) + " " + localNumberFormatException
/*  334 */             .getMessage());
/*  335 */           System.out.println("Setting " + Arrays.toString(paramVarArgs) + " to minimum value");
/*      */         }
/*      */         
/*  338 */         i = Integer.MIN_VALUE;
/*      */       }
/*      */     }
/*  341 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getBooleanValue(String... paramVarArgs)
/*      */   {
/*  353 */     String str = get(paramVarArgs);
/*  354 */     if ((str != null) && (str.equalsIgnoreCase("true"))) {
/*  355 */       return true;
/*      */     }
/*  357 */     return false;
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
/*      */   private int parseIntValue(String paramString)
/*      */     throws NumberFormatException
/*      */   {
/*  373 */     int i = 0;
/*  374 */     String str; if (paramString.startsWith("+")) {
/*  375 */       str = paramString.substring(1);
/*  376 */       return Integer.parseInt(str); }
/*  377 */     if (paramString.startsWith("0x")) {
/*  378 */       str = paramString.substring(2);
/*  379 */       char[] arrayOfChar = str.toCharArray();
/*  380 */       if (arrayOfChar.length > 8) {
/*  381 */         throw new NumberFormatException();
/*      */       }
/*  383 */       for (int j = 0; j < arrayOfChar.length; j++) {
/*  384 */         int k = arrayOfChar.length - j - 1;
/*  385 */         switch (arrayOfChar[j]) {
/*      */         case '0': 
/*  387 */           i += 0;
/*  388 */           break;
/*      */         case '1': 
/*  390 */           i += 1 * getBase(k);
/*  391 */           break;
/*      */         case '2': 
/*  393 */           i += 2 * getBase(k);
/*  394 */           break;
/*      */         case '3': 
/*  396 */           i += 3 * getBase(k);
/*  397 */           break;
/*      */         case '4': 
/*  399 */           i += 4 * getBase(k);
/*  400 */           break;
/*      */         case '5': 
/*  402 */           i += 5 * getBase(k);
/*  403 */           break;
/*      */         case '6': 
/*  405 */           i += 6 * getBase(k);
/*  406 */           break;
/*      */         case '7': 
/*  408 */           i += 7 * getBase(k);
/*  409 */           break;
/*      */         case '8': 
/*  411 */           i += 8 * getBase(k);
/*  412 */           break;
/*      */         case '9': 
/*  414 */           i += 9 * getBase(k);
/*  415 */           break;
/*      */         case 'A': 
/*      */         case 'a': 
/*  418 */           i += 10 * getBase(k);
/*  419 */           break;
/*      */         case 'B': 
/*      */         case 'b': 
/*  422 */           i += 11 * getBase(k);
/*  423 */           break;
/*      */         case 'C': 
/*      */         case 'c': 
/*  426 */           i += 12 * getBase(k);
/*  427 */           break;
/*      */         case 'D': 
/*      */         case 'd': 
/*  430 */           i += 13 * getBase(k);
/*  431 */           break;
/*      */         case 'E': 
/*      */         case 'e': 
/*  434 */           i += 14 * getBase(k);
/*  435 */           break;
/*      */         case 'F': 
/*      */         case 'f': 
/*  438 */           i += 15 * getBase(k);
/*  439 */           break;
/*      */         case ':': case ';': case '<': case '=': case '>': case '?': case '@': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z': case '[': case '\\': case ']': case '^': case '_': case '`': default: 
/*  441 */           throw new NumberFormatException("Invalid numerical format");
/*      */         }
/*      */         
/*      */       }
/*  445 */       if (i < 0) {
/*  446 */         throw new NumberFormatException("Data overflow.");
/*      */       }
/*      */     } else {
/*  449 */       i = Integer.parseInt(paramString);
/*      */     }
/*  451 */     return i;
/*      */   }
/*      */   
/*      */   private int getBase(int paramInt) {
/*  455 */     int i = 16;
/*  456 */     switch (paramInt) {
/*      */     case 0: 
/*  458 */       i = 1;
/*  459 */       break;
/*      */     case 1: 
/*  461 */       i = 16;
/*  462 */       break;
/*      */     case 2: 
/*  464 */       i = 256;
/*  465 */       break;
/*      */     case 3: 
/*  467 */       i = 4096;
/*  468 */       break;
/*      */     default: 
/*  470 */       for (int j = 1; j < paramInt; j++) {
/*  471 */         i *= 16;
/*      */       }
/*      */     }
/*  474 */     return i;
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
/*      */   private List<String> loadConfigFile(final String paramString)
/*      */     throws IOException, KrbException
/*      */   {
/*      */     try
/*      */     {
/*  516 */       ArrayList localArrayList = new ArrayList();
/*      */       
/*  518 */       BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader((InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public FileInputStream run() throws IOException
/*      */         {
/*  521 */           return new FileInputStream(paramString);
/*      */         }
/*  516 */       }
/*  517 */         )));Object 
/*      */       
/*      */ 
/*      */ 
/*  521 */         localObject1 = null;
/*      */       
/*      */       try
/*      */       {
/*  525 */         Object localObject2 = null;
/*  526 */         String str1; while ((str1 = localBufferedReader.readLine()) != null) {
/*  527 */           str1 = str1.trim();
/*  528 */           if ((!str1.isEmpty()) && (!str1.startsWith("#")) && (!str1.startsWith(";")))
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  549 */             if (str1.startsWith("[")) {
/*  550 */               if (!str1.endsWith("]")) {
/*  551 */                 throw new KrbException("Illegal config content:" + str1);
/*      */               }
/*      */               
/*  554 */               if (localObject2 != null) {
/*  555 */                 localArrayList.add(localObject2);
/*  556 */                 localArrayList.add("}");
/*      */               }
/*      */               
/*  559 */               String str2 = str1.substring(1, str1.length() - 1).trim();
/*  560 */               if (str2.isEmpty()) {
/*  561 */                 throw new KrbException("Illegal config content:" + str1);
/*      */               }
/*      */               
/*  564 */               localObject2 = str2 + " = {";
/*  565 */             } else if (str1.startsWith("{")) {
/*  566 */               if (localObject2 == null) {
/*  567 */                 throw new KrbException("Config file should not start with \"{\"");
/*      */               }
/*      */               
/*  570 */               localObject2 = (String)localObject2 + " {";
/*  571 */               if (str1.length() > 1)
/*      */               {
/*  573 */                 localArrayList.add(localObject2);
/*  574 */                 localObject2 = str1.substring(1).trim();
/*      */               }
/*      */               
/*      */             }
/*  578 */             else if (localObject2 != null) {
/*  579 */               localArrayList.add(localObject2);
/*  580 */               localObject2 = str1;
/*      */             }
/*      */           }
/*      */         }
/*  584 */         if (localObject2 != null) {
/*  585 */           localArrayList.add(localObject2);
/*  586 */           localArrayList.add("}");
/*      */         }
/*      */       }
/*      */       catch (Throwable localThrowable2)
/*      */       {
/*  517 */         localObject1 = localThrowable2;throw localThrowable2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  588 */         if (localBufferedReader != null) if (localObject1 != null) try { localBufferedReader.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localBufferedReader.close(); }
/*  589 */       return localArrayList;
/*      */     } catch (PrivilegedActionException localPrivilegedActionException) {
/*  591 */       throw ((IOException)localPrivilegedActionException.getException());
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
/*      */   private Hashtable<String, Object> parseStanzaTable(List<String> paramList)
/*      */     throws KrbException
/*      */   {
/*  619 */     Object localObject1 = this.stanzaTable;
/*  620 */     for (String str1 : paramList)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  625 */       if (str1.equals("}"))
/*      */       {
/*  627 */         localObject1 = (Hashtable)((Hashtable)localObject1).remove(" PARENT ");
/*  628 */         if (localObject1 == null) {
/*  629 */           throw new KrbException("Unmatched close brace");
/*      */         }
/*      */       } else {
/*  632 */         int i = str1.indexOf('=');
/*  633 */         if (i < 0) {
/*  634 */           throw new KrbException("Illegal config content:" + str1);
/*      */         }
/*  636 */         String str2 = str1.substring(0, i).trim();
/*  637 */         String str3 = trimmed(str1.substring(i + 1));
/*  638 */         Object localObject2; if (str3.equals("{"))
/*      */         {
/*  640 */           if (localObject1 == this.stanzaTable) {
/*  641 */             str2 = str2.toLowerCase(Locale.US);
/*      */           }
/*  643 */           localObject2 = new Hashtable();
/*  644 */           ((Hashtable)localObject1).put(str2, localObject2);
/*      */           
/*      */ 
/*  647 */           ((Hashtable)localObject2).put(" PARENT ", localObject1);
/*  648 */           localObject1 = localObject2;
/*      */         }
/*      */         else {
/*  651 */           if (((Hashtable)localObject1).containsKey(str2)) {
/*  652 */             Object localObject3 = ((Hashtable)localObject1).get(str2);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  657 */             if (!(localObject3 instanceof Vector)) {
/*  658 */               throw new KrbException("Key " + str2 + "used for both value and section");
/*      */             }
/*      */             
/*  661 */             localObject2 = (Vector)((Hashtable)localObject1).get(str2);
/*      */           } else {
/*  663 */             localObject2 = new Vector();
/*  664 */             ((Hashtable)localObject1).put(str2, localObject2);
/*      */           }
/*  666 */           ((Vector)localObject2).add(str3);
/*      */         }
/*      */       }
/*      */     }
/*  670 */     if (localObject1 != this.stanzaTable) {
/*  671 */       throw new KrbException("Not closed");
/*      */     }
/*  673 */     return (Hashtable<String, Object>)localObject1;
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
/*      */   private String getJavaFileName()
/*      */   {
/*  687 */     String str = getProperty("java.security.krb5.conf");
/*  688 */     if (str == null) {
/*  689 */       str = getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "krb5.conf";
/*      */       
/*      */ 
/*  692 */       if (!fileExists(str)) {
/*  693 */         str = null;
/*      */       }
/*      */     }
/*  696 */     if (DEBUG) {
/*  697 */       System.out.println("Java config name: " + str);
/*      */     }
/*  699 */     return str;
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
/*      */   private String getNativeFileName()
/*      */   {
/*  720 */     Object localObject = null;
/*  721 */     String str1 = getProperty("os.name");
/*  722 */     if (str1.startsWith("Windows")) {
/*      */       try {
/*  724 */         Credentials.ensureLoaded();
/*      */       }
/*      */       catch (Exception localException) {}
/*      */       
/*  728 */       if (Credentials.alreadyLoaded) {
/*  729 */         String str2 = getWindowsDirectory(false);
/*  730 */         if (str2 != null) {
/*  731 */           if (str2.endsWith("\\")) {
/*  732 */             str2 = str2 + "krb5.ini";
/*      */           } else {
/*  734 */             str2 = str2 + "\\krb5.ini";
/*      */           }
/*  736 */           if (fileExists(str2)) {
/*  737 */             localObject = str2;
/*      */           }
/*      */         }
/*  740 */         if (localObject == null) {
/*  741 */           str2 = getWindowsDirectory(true);
/*  742 */           if (str2 != null) {
/*  743 */             if (str2.endsWith("\\")) {
/*  744 */               str2 = str2 + "krb5.ini";
/*      */             } else {
/*  746 */               str2 = str2 + "\\krb5.ini";
/*      */             }
/*  748 */             localObject = str2;
/*      */           }
/*      */         }
/*      */       }
/*  752 */       if (localObject == null) {
/*  753 */         localObject = "c:\\winnt\\krb5.ini";
/*      */       }
/*  755 */     } else if (str1.startsWith("SunOS")) {
/*  756 */       localObject = "/etc/krb5/krb5.conf";
/*  757 */     } else if (str1.contains("OS X")) {
/*  758 */       localObject = findMacosConfigFile();
/*      */     } else {
/*  760 */       localObject = "/etc/krb5.conf";
/*      */     }
/*  762 */     if (DEBUG) {
/*  763 */       System.out.println("Native config name: " + (String)localObject);
/*      */     }
/*  765 */     return (String)localObject;
/*      */   }
/*      */   
/*      */   private static String getProperty(String paramString) {
/*  769 */     return (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
/*      */   }
/*      */   
/*      */   private String findMacosConfigFile()
/*      */   {
/*  774 */     String str1 = getProperty("user.home");
/*      */     
/*  776 */     String str2 = str1 + "/Library/Preferences/edu.mit.Kerberos";
/*      */     
/*  778 */     if (fileExists(str2)) {
/*  779 */       return str2;
/*      */     }
/*      */     
/*  782 */     if (fileExists("/Library/Preferences/edu.mit.Kerberos")) {
/*  783 */       return "/Library/Preferences/edu.mit.Kerberos";
/*      */     }
/*      */     
/*  786 */     return "/etc/krb5.conf";
/*      */   }
/*      */   
/*      */   private static String trimmed(String paramString) {
/*  790 */     paramString = paramString.trim();
/*  791 */     if ((paramString.length() >= 2) && (
/*  792 */       ((paramString.charAt(0) == '"') && (paramString.charAt(paramString.length() - 1) == '"')) || (
/*  793 */       (paramString.charAt(0) == '\'') && (paramString.charAt(paramString.length() - 1) == '\'')))) {
/*  794 */       paramString = paramString.substring(1, paramString.length() - 1).trim();
/*      */     }
/*  796 */     return paramString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void listTable()
/*      */   {
/*  804 */     System.out.println(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] defaultEtype(String paramString)
/*      */     throws KrbException
/*      */   {
/*  814 */     String str1 = get(new String[] { "libdefaults", paramString });
/*      */     int[] arrayOfInt;
/*  816 */     if (str1 == null) {
/*  817 */       if (DEBUG) {
/*  818 */         System.out.println("Using builtin default etypes for " + paramString);
/*      */       }
/*      */       
/*  821 */       arrayOfInt = EType.getBuiltInDefaults();
/*      */     } else {
/*  823 */       String str2 = " ";
/*      */       
/*  825 */       for (int j = 0; j < str1.length(); j++) {
/*  826 */         if (str1.substring(j, j + 1).equals(","))
/*      */         {
/*      */ 
/*  829 */           str2 = ",";
/*  830 */           break;
/*      */         }
/*      */       }
/*  833 */       StringTokenizer localStringTokenizer = new StringTokenizer(str1, str2);
/*  834 */       j = localStringTokenizer.countTokens();
/*  835 */       ArrayList localArrayList = new ArrayList(j);
/*      */       
/*  837 */       for (int m = 0; m < j; m++) {
/*  838 */         int k = getType(localStringTokenizer.nextToken());
/*  839 */         if ((k != -1) && (EType.isSupported(k))) {
/*  840 */           localArrayList.add(Integer.valueOf(k));
/*      */         }
/*      */       }
/*  843 */       if (localArrayList.isEmpty()) {
/*  844 */         throw new KrbException("no supported default etypes for " + paramString);
/*      */       }
/*      */       
/*  847 */       arrayOfInt = new int[localArrayList.size()];
/*  848 */       for (m = 0; m < arrayOfInt.length; m++) {
/*  849 */         arrayOfInt[m] = ((Integer)localArrayList.get(m)).intValue();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  854 */     if (DEBUG) {
/*  855 */       System.out.print("default etypes for " + paramString + ":");
/*  856 */       for (int i = 0; i < arrayOfInt.length; i++) {
/*  857 */         System.out.print(" " + arrayOfInt[i]);
/*      */       }
/*  859 */       System.out.println(".");
/*      */     }
/*  861 */     return arrayOfInt;
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
/*      */   public static int getType(String paramString)
/*      */   {
/*  876 */     int i = -1;
/*  877 */     if (paramString == null) {
/*  878 */       return i;
/*      */     }
/*  880 */     if ((paramString.startsWith("d")) || (paramString.startsWith("D"))) {
/*  881 */       if (paramString.equalsIgnoreCase("des-cbc-crc")) {
/*  882 */         i = 1;
/*  883 */       } else if (paramString.equalsIgnoreCase("des-cbc-md5")) {
/*  884 */         i = 3;
/*  885 */       } else if (paramString.equalsIgnoreCase("des-mac")) {
/*  886 */         i = 4;
/*  887 */       } else if (paramString.equalsIgnoreCase("des-mac-k")) {
/*  888 */         i = 5;
/*  889 */       } else if (paramString.equalsIgnoreCase("des-cbc-md4")) {
/*  890 */         i = 2;
/*  891 */       } else if ((paramString.equalsIgnoreCase("des3-cbc-sha1")) || 
/*  892 */         (paramString.equalsIgnoreCase("des3-hmac-sha1")) || 
/*  893 */         (paramString.equalsIgnoreCase("des3-cbc-sha1-kd")) || 
/*  894 */         (paramString.equalsIgnoreCase("des3-cbc-hmac-sha1-kd"))) {
/*  895 */         i = 16;
/*      */       }
/*  897 */     } else if ((paramString.startsWith("a")) || (paramString.startsWith("A")))
/*      */     {
/*  899 */       if ((paramString.equalsIgnoreCase("aes128-cts")) || 
/*  900 */         (paramString.equalsIgnoreCase("aes128-cts-hmac-sha1-96"))) {
/*  901 */         i = 17;
/*  902 */       } else if ((paramString.equalsIgnoreCase("aes256-cts")) || 
/*  903 */         (paramString.equalsIgnoreCase("aes256-cts-hmac-sha1-96"))) {
/*  904 */         i = 18;
/*      */       }
/*  906 */       else if ((paramString.equalsIgnoreCase("arcfour-hmac")) || 
/*  907 */         (paramString.equalsIgnoreCase("arcfour-hmac-md5"))) {
/*  908 */         i = 23;
/*      */       }
/*      */     }
/*  911 */     else if (paramString.equalsIgnoreCase("rc4-hmac")) {
/*  912 */       i = 23;
/*  913 */     } else if (paramString.equalsIgnoreCase("CRC32")) {
/*  914 */       i = 1;
/*  915 */     } else if ((paramString.startsWith("r")) || (paramString.startsWith("R"))) {
/*  916 */       if (paramString.equalsIgnoreCase("rsa-md5")) {
/*  917 */         i = 7;
/*  918 */       } else if (paramString.equalsIgnoreCase("rsa-md5-des")) {
/*  919 */         i = 8;
/*      */       }
/*  921 */     } else if (paramString.equalsIgnoreCase("hmac-sha1-des3-kd")) {
/*  922 */       i = 12;
/*  923 */     } else if (paramString.equalsIgnoreCase("hmac-sha1-96-aes128")) {
/*  924 */       i = 15;
/*  925 */     } else if (paramString.equalsIgnoreCase("hmac-sha1-96-aes256")) {
/*  926 */       i = 16;
/*  927 */     } else if ((paramString.equalsIgnoreCase("hmac-md5-rc4")) || 
/*  928 */       (paramString.equalsIgnoreCase("hmac-md5-arcfour")) || 
/*  929 */       (paramString.equalsIgnoreCase("hmac-md5-enc"))) {
/*  930 */       i = 65398;
/*  931 */     } else if (paramString.equalsIgnoreCase("NULL")) {
/*  932 */       i = 0;
/*      */     }
/*      */     
/*  935 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void resetDefaultRealm(String paramString)
/*      */   {
/*  945 */     if (DEBUG) {
/*  946 */       System.out.println(">>> Config try resetting default kdc " + paramString);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean useAddresses()
/*      */   {
/*  955 */     boolean bool = false;
/*      */     
/*  957 */     String str = get(new String[] { "libdefaults", "no_addresses" });
/*  958 */     bool = (str != null) && (str.equalsIgnoreCase("false"));
/*  959 */     if (!bool)
/*      */     {
/*  961 */       str = get(new String[] { "libdefaults", "noaddresses" });
/*  962 */       bool = (str != null) && (str.equalsIgnoreCase("false"));
/*      */     }
/*  964 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean useDNS(String paramString, boolean paramBoolean)
/*      */   {
/*  971 */     Boolean localBoolean = getBooleanObject(new String[] { "libdefaults", paramString });
/*  972 */     if (localBoolean != null) {
/*  973 */       return localBoolean.booleanValue();
/*      */     }
/*  975 */     localBoolean = getBooleanObject(new String[] { "libdefaults", "dns_fallback" });
/*  976 */     if (localBoolean != null) {
/*  977 */       return localBoolean.booleanValue();
/*      */     }
/*  979 */     return paramBoolean;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean useDNS_KDC()
/*      */   {
/*  986 */     return useDNS("dns_lookup_kdc", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean useDNS_Realm()
/*      */   {
/*  993 */     return useDNS("dns_lookup_realm", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDefaultRealm()
/*      */     throws KrbException
/*      */   {
/* 1002 */     if (this.defaultRealm != null) {
/* 1003 */       return this.defaultRealm;
/*      */     }
/* 1005 */     Object localObject = null;
/* 1006 */     String str = get(new String[] { "libdefaults", "default_realm" });
/* 1007 */     if ((str == null) && (useDNS_Realm())) {
/*      */       try
/*      */       {
/* 1010 */         str = getRealmFromDNS();
/*      */       } catch (KrbException localKrbException1) {
/* 1012 */         localObject = localKrbException1;
/*      */       }
/*      */     }
/* 1015 */     if (str == null) {
/* 1016 */       str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public String run()
/*      */         {
/* 1020 */           String str = System.getProperty("os.name");
/* 1021 */           if (str.startsWith("Windows")) {
/* 1022 */             return System.getenv("USERDNSDOMAIN");
/*      */           }
/* 1024 */           return null;
/*      */         }
/*      */       });
/*      */     }
/* 1028 */     if (str == null) {
/* 1029 */       KrbException localKrbException2 = new KrbException("Cannot locate default realm");
/* 1030 */       if (localObject != null) {
/* 1031 */         localKrbException2.initCause((Throwable)localObject);
/*      */       }
/* 1033 */       throw localKrbException2;
/*      */     }
/* 1035 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getKDCList(String paramString)
/*      */     throws KrbException
/*      */   {
/* 1046 */     if (paramString == null) {
/* 1047 */       paramString = getDefaultRealm();
/*      */     }
/* 1049 */     if (paramString.equalsIgnoreCase(this.defaultRealm)) {
/* 1050 */       return this.defaultKDC;
/*      */     }
/* 1052 */     Object localObject = null;
/* 1053 */     String str = getAll(new String[] { "realms", paramString, "kdc" });
/* 1054 */     if ((str == null) && (useDNS_KDC())) {
/*      */       try
/*      */       {
/* 1057 */         str = getKDCFromDNS(paramString);
/*      */       } catch (KrbException localKrbException1) {
/* 1059 */         localObject = localKrbException1;
/*      */       }
/*      */     }
/* 1062 */     if (str == null) {
/* 1063 */       str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public String run()
/*      */         {
/* 1067 */           String str1 = System.getProperty("os.name");
/* 1068 */           if (str1.startsWith("Windows")) {
/* 1069 */             String str2 = System.getenv("LOGONSERVER");
/* 1070 */             if ((str2 != null) && 
/* 1071 */               (str2.startsWith("\\\\"))) {
/* 1072 */               str2 = str2.substring(2);
/*      */             }
/* 1074 */             return str2;
/*      */           }
/* 1076 */           return null;
/*      */         }
/*      */       });
/*      */     }
/* 1080 */     if (str == null) {
/* 1081 */       if (this.defaultKDC != null) {
/* 1082 */         return this.defaultKDC;
/*      */       }
/* 1084 */       KrbException localKrbException2 = new KrbException("Cannot locate KDC");
/* 1085 */       if (localObject != null) {
/* 1086 */         localKrbException2.initCause((Throwable)localObject);
/*      */       }
/* 1088 */       throw localKrbException2;
/*      */     }
/* 1090 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getRealmFromDNS()
/*      */     throws KrbException
/*      */   {
/* 1100 */     String str1 = null;
/* 1101 */     String str2 = null;
/*      */     Object localObject;
/* 1103 */     try { str2 = InetAddress.getLocalHost().getCanonicalHostName();
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException) {
/* 1106 */       localObject = new KrbException(60, "Unable to locate Kerberos realm: " + localUnknownHostException.getMessage());
/* 1107 */       ((KrbException)localObject).initCause(localUnknownHostException);
/* 1108 */       throw ((Throwable)localObject);
/*      */     }
/*      */     
/* 1111 */     String str3 = PrincipalName.mapHostToRealm(str2);
/* 1112 */     if (str3 == null)
/*      */     {
/* 1114 */       localObject = ResolverConfiguration.open().searchlist();
/* 1115 */       for (String str4 : (List)localObject) {
/* 1116 */         str1 = checkRealm(str4);
/* 1117 */         if (str1 != null) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     } else {
/* 1122 */       str1 = checkRealm(str3);
/*      */     }
/* 1124 */     if (str1 == null) {
/* 1125 */       throw new KrbException(60, "Unable to locate Kerberos realm");
/*      */     }
/*      */     
/* 1128 */     return str1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String checkRealm(String paramString)
/*      */   {
/* 1136 */     if (DEBUG) {
/* 1137 */       System.out.println("getRealmFromDNS: trying " + paramString);
/*      */     }
/* 1139 */     String[] arrayOfString = null;
/* 1140 */     String str = paramString;
/* 1141 */     while ((arrayOfString == null) && (str != null))
/*      */     {
/* 1143 */       arrayOfString = KrbServiceLocator.getKerberosService(str);
/* 1144 */       str = Realm.parseRealmComponent(str);
/*      */     }
/*      */     
/* 1147 */     if (arrayOfString != null) {
/* 1148 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 1149 */         if (arrayOfString[i].equalsIgnoreCase(paramString)) {
/* 1150 */           return arrayOfString[i];
/*      */         }
/*      */       }
/*      */     }
/* 1154 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getKDCFromDNS(String paramString)
/*      */     throws KrbException
/*      */   {
/* 1165 */     String str = "";
/* 1166 */     String[] arrayOfString = null;
/*      */     
/* 1168 */     if (DEBUG) {
/* 1169 */       System.out.println("getKDCFromDNS using UDP");
/*      */     }
/* 1171 */     arrayOfString = KrbServiceLocator.getKerberosService(paramString, "_udp");
/* 1172 */     if (arrayOfString == null)
/*      */     {
/* 1174 */       if (DEBUG) {
/* 1175 */         System.out.println("getKDCFromDNS using TCP");
/*      */       }
/* 1177 */       arrayOfString = KrbServiceLocator.getKerberosService(paramString, "_tcp");
/*      */     }
/* 1179 */     if (arrayOfString == null)
/*      */     {
/* 1181 */       throw new KrbException(60, "Unable to locate KDC for realm " + paramString);
/*      */     }
/*      */     
/* 1184 */     if (arrayOfString.length == 0) {
/* 1185 */       return null;
/*      */     }
/* 1187 */     for (int i = 0; i < arrayOfString.length; i++) {
/* 1188 */       str = str + arrayOfString[i].trim() + " ";
/*      */     }
/* 1190 */     str = str.trim();
/* 1191 */     if (str.equals("")) {
/* 1192 */       return null;
/*      */     }
/* 1194 */     return str;
/*      */   }
/*      */   
/*      */   private boolean fileExists(String paramString) {
/* 1198 */     return ((Boolean)AccessController.doPrivileged(new FileExistsAction(paramString))).booleanValue();
/*      */   }
/*      */   
/*      */   static class FileExistsAction
/*      */     implements PrivilegedAction<Boolean>
/*      */   {
/*      */     private String fileName;
/*      */     
/*      */     public FileExistsAction(String paramString)
/*      */     {
/* 1208 */       this.fileName = paramString;
/*      */     }
/*      */     
/*      */     public Boolean run() {
/* 1212 */       return Boolean.valueOf(new File(this.fileName).exists());
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
/*      */   public String toString()
/*      */   {
/* 1231 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1232 */     toStringInternal("", this.stanzaTable, localStringBuffer);
/* 1233 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */   private static void toStringInternal(String paramString, Object paramObject, StringBuffer paramStringBuffer) {
/* 1237 */     if ((paramObject instanceof String))
/*      */     {
/* 1239 */       paramStringBuffer.append(paramObject).append('\n'); } else { Object localObject1;
/* 1240 */       Object localObject2; if ((paramObject instanceof Hashtable))
/*      */       {
/* 1242 */         localObject1 = (Hashtable)paramObject;
/* 1243 */         paramStringBuffer.append("{\n");
/* 1244 */         for (Iterator localIterator = ((Hashtable)localObject1).keySet().iterator(); localIterator.hasNext();) { localObject2 = localIterator.next();
/*      */           
/* 1246 */           paramStringBuffer.append(paramString).append("    ").append(localObject2).append(" = ");
/*      */           
/* 1248 */           toStringInternal(paramString + "    ", ((Hashtable)localObject1).get(localObject2), paramStringBuffer);
/*      */         }
/* 1250 */         paramStringBuffer.append(paramString).append("}\n");
/* 1251 */       } else if ((paramObject instanceof Vector))
/*      */       {
/* 1253 */         localObject1 = (Vector)paramObject;
/* 1254 */         paramStringBuffer.append("[");
/* 1255 */         int i = 1;
/* 1256 */         for (Object localObject3 : ((Vector)localObject1).toArray()) {
/* 1257 */           if (i == 0) paramStringBuffer.append(",");
/* 1258 */           paramStringBuffer.append(localObject3);
/* 1259 */           i = 0;
/*      */         }
/* 1261 */         paramStringBuffer.append("]\n");
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\Config.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
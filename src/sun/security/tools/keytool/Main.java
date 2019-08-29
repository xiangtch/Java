/*      */ package sun.security.tools.keytool;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URI;
/*      */ import java.security.CodeSigner;
/*      */ import java.security.Key;
/*      */ import java.security.KeyStore;
/*      */ import java.security.KeyStore.Entry;
/*      */ import java.security.KeyStore.PasswordProtection;
/*      */ import java.security.KeyStore.PrivateKeyEntry;
/*      */ import java.security.KeyStore.ProtectionParameter;
/*      */ import java.security.KeyStore.SecretKeyEntry;
/*      */ import java.security.KeyStore.TrustedCertificateEntry;
/*      */ import java.security.KeyStoreException;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.Principal;
/*      */ import java.security.PrivateKey;
/*      */ import java.security.Provider;
/*      */ import java.security.PublicKey;
/*      */ import java.security.Signature;
/*      */ import java.security.Timestamp;
/*      */ import java.security.UnrecoverableEntryException;
/*      */ import java.security.UnrecoverableKeyException;
/*      */ import java.security.cert.CRL;
/*      */ import java.security.cert.CertPath;
/*      */ import java.security.cert.CertStore;
/*      */ import java.security.cert.CertStoreException;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.cert.X509CRL;
/*      */ import java.security.cert.X509CRLEntry;
/*      */ import java.security.cert.X509CRLSelector;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.text.Collator;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Base64;
/*      */ import java.util.Base64.Encoder;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Random;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import java.util.jar.JarEntry;
/*      */ import java.util.jar.JarFile;
/*      */ import javax.crypto.KeyGenerator;
/*      */ import javax.crypto.SecretKey;
/*      */ import javax.crypto.SecretKeyFactory;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.misc.HexDumpEncoder;
/*      */ import sun.security.pkcs.PKCS9Attribute;
/*      */ import sun.security.pkcs10.PKCS10;
/*      */ import sun.security.pkcs10.PKCS10Attribute;
/*      */ import sun.security.pkcs10.PKCS10Attributes;
/*      */ import sun.security.provider.certpath.CertStoreHelper;
/*      */ import sun.security.tools.KeyStoreUtil;
/*      */ import sun.security.tools.PathList;
/*      */ import sun.security.util.DerValue;
/*      */ import sun.security.util.DisabledAlgorithmConstraints;
/*      */ import sun.security.util.KeyUtil;
/*      */ import sun.security.util.ObjectIdentifier;
/*      */ import sun.security.util.Password;
/*      */ import sun.security.util.Pem;
/*      */ import sun.security.util.SecurityProviderConstants;
/*      */ import sun.security.x509.AlgorithmId;
/*      */ import sun.security.x509.CRLDistributionPointsExtension;
/*      */ import sun.security.x509.CRLExtensions;
/*      */ import sun.security.x509.CertificateExtensions;
/*      */ import sun.security.x509.CertificateValidity;
/*      */ import sun.security.x509.CertificateVersion;
/*      */ import sun.security.x509.DistributionPoint;
/*      */ import sun.security.x509.Extension;
/*      */ import sun.security.x509.GeneralName;
/*      */ import sun.security.x509.GeneralNames;
/*      */ import sun.security.x509.KeyIdentifier;
/*      */ import sun.security.x509.KeyUsageExtension;
/*      */ import sun.security.x509.PKIXExtensions;
/*      */ import sun.security.x509.URIName;
/*      */ import sun.security.x509.X500Name;
/*      */ import sun.security.x509.X509CRLEntryImpl;
/*      */ import sun.security.x509.X509CRLImpl;
/*      */ import sun.security.x509.X509CertImpl;
/*      */ import sun.security.x509.X509CertInfo;
/*      */ 
/*      */ public final class Main
/*      */ {
/*  110 */   private static final byte[] CRLF = { 13, 10 };
/*      */   
/*  112 */   private boolean debug = false;
/*  113 */   private Command command = null;
/*  114 */   private String sigAlgName = null;
/*  115 */   private String keyAlgName = null;
/*  116 */   private boolean verbose = false;
/*  117 */   private int keysize = -1;
/*  118 */   private boolean rfc = false;
/*  119 */   private long validity = 90L;
/*  120 */   private String alias = null;
/*  121 */   private String dname = null;
/*  122 */   private String dest = null;
/*  123 */   private String filename = null;
/*  124 */   private String infilename = null;
/*  125 */   private String outfilename = null;
/*  126 */   private String srcksfname = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  134 */   private Set<Pair<String, String>> providers = null;
/*  135 */   private String storetype = null;
/*  136 */   private String srcProviderName = null;
/*  137 */   private String providerName = null;
/*  138 */   private String pathlist = null;
/*  139 */   private char[] storePass = null;
/*  140 */   private char[] storePassNew = null;
/*  141 */   private char[] keyPass = null;
/*  142 */   private char[] keyPassNew = null;
/*  143 */   private char[] newPass = null;
/*  144 */   private char[] destKeyPass = null;
/*  145 */   private char[] srckeyPass = null;
/*  146 */   private String ksfname = null;
/*  147 */   private File ksfile = null;
/*  148 */   private InputStream ksStream = null;
/*  149 */   private String sslserver = null;
/*  150 */   private String jarfile = null;
/*  151 */   private KeyStore keyStore = null;
/*  152 */   private boolean token = false;
/*  153 */   private boolean nullStream = false;
/*  154 */   private boolean kssave = false;
/*  155 */   private boolean noprompt = false;
/*  156 */   private boolean trustcacerts = false;
/*  157 */   private boolean nowarn = false;
/*  158 */   private boolean protectedPath = false;
/*  159 */   private boolean srcprotectedPath = false;
/*  160 */   private CertificateFactory cf = null;
/*  161 */   private KeyStore caks = null;
/*  162 */   private char[] srcstorePass = null;
/*  163 */   private String srcstoretype = null;
/*  164 */   private Set<char[]> passwords = new HashSet();
/*  165 */   private String startDate = null;
/*      */   
/*  167 */   private List<String> ids = new ArrayList();
/*  168 */   private List<String> v3ext = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*  172 */   private boolean inplaceImport = false;
/*  173 */   private String inplaceBackupName = null;
/*      */   
/*      */ 
/*  176 */   private List<String> weakWarnings = new ArrayList();
/*      */   
/*  178 */   private static final DisabledAlgorithmConstraints DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  183 */   private static final Set<java.security.CryptoPrimitive> SIG_PRIMITIVE_SET = Collections.unmodifiableSet(java.util.EnumSet.of(java.security.CryptoPrimitive.SIGNATURE));
/*      */   
/*      */   static enum Command {
/*  186 */     CERTREQ("Generates.a.certificate.request", new Option[] { Option.ALIAS, Option.SIGALG, Option.FILEOUT, Option.KEYPASS, Option.KEYSTORE, Option.DNAME, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*  190 */     CHANGEALIAS("Changes.an.entry.s.alias", new Option[] { Option.ALIAS, Option.DESTALIAS, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*  194 */     DELETE("Deletes.an.entry", new Option[] { Option.ALIAS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*  198 */     EXPORTCERT("Exports.certificate", new Option[] { Option.RFC, Option.ALIAS, Option.FILEOUT, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*  202 */     GENKEYPAIR("Generates.a.key.pair", new Option[] { Option.ALIAS, Option.KEYALG, Option.KEYSIZE, Option.SIGALG, Option.DESTALIAS, Option.DNAME, Option.STARTDATE, Option.EXT, Option.VALIDITY, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  207 */     GENSECKEY("Generates.a.secret.key", new Option[] { Option.ALIAS, Option.KEYPASS, Option.KEYALG, Option.KEYSIZE, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*  211 */     GENCERT("Generates.certificate.from.a.certificate.request", new Option[] { Option.RFC, Option.INFILE, Option.OUTFILE, Option.ALIAS, Option.SIGALG, Option.DNAME, Option.STARTDATE, Option.EXT, Option.VALIDITY, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  216 */     IMPORTCERT("Imports.a.certificate.or.a.certificate.chain", new Option[] { Option.NOPROMPT, Option.TRUSTCACERTS, Option.PROTECTED, Option.ALIAS, Option.FILEIN, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }),
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  221 */     IMPORTPASS("Imports.a.password", new Option[] { Option.ALIAS, Option.KEYPASS, Option.KEYALG, Option.KEYSIZE, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*  225 */     IMPORTKEYSTORE("Imports.one.or.all.entries.from.another.keystore", new Option[] { Option.SRCKEYSTORE, Option.DESTKEYSTORE, Option.SRCSTORETYPE, Option.DESTSTORETYPE, Option.SRCSTOREPASS, Option.DESTSTOREPASS, Option.SRCPROTECTED, Option.SRCPROVIDERNAME, Option.DESTPROVIDERNAME, Option.SRCALIAS, Option.DESTALIAS, Option.SRCKEYPASS, Option.DESTKEYPASS, Option.NOPROMPT, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }),
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  232 */     KEYPASSWD("Changes.the.key.password.of.an.entry", new Option[] { Option.ALIAS, Option.KEYPASS, Option.NEW, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }),
/*      */     
/*      */ 
/*      */ 
/*  236 */     LIST("Lists.entries.in.a.keystore", new Option[] { Option.RFC, Option.ALIAS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*  240 */     PRINTCERT("Prints.the.content.of.a.certificate", new Option[] { Option.RFC, Option.FILEIN, Option.SSLSERVER, Option.JARFILE, Option.V }),
/*      */     
/*  242 */     PRINTCERTREQ("Prints.the.content.of.a.certificate.request", new Option[] { Option.FILEIN, Option.V }),
/*      */     
/*  244 */     PRINTCRL("Prints.the.content.of.a.CRL.file", new Option[] { Option.FILEIN, Option.V }),
/*      */     
/*  246 */     STOREPASSWD("Changes.the.store.password.of.a.keystore", new Option[] { Option.NEW, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }),
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  252 */     KEYCLONE("Clones.a.key.entry", new Option[] { Option.ALIAS, Option.DESTALIAS, Option.KEYPASS, Option.NEW, Option.STORETYPE, Option.KEYSTORE, Option.STOREPASS, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }),
/*      */     
/*      */ 
/*      */ 
/*  256 */     SELFCERT("Generates.a.self.signed.certificate", new Option[] { Option.ALIAS, Option.SIGALG, Option.DNAME, Option.STARTDATE, Option.VALIDITY, Option.KEYPASS, Option.STORETYPE, Option.KEYSTORE, Option.STOREPASS, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V }),
/*      */     
/*      */ 
/*      */ 
/*  260 */     GENCRL("Generates.CRL", new Option[] { Option.RFC, Option.FILEOUT, Option.ID, Option.ALIAS, Option.SIGALG, Option.EXT, Option.KEYPASS, Option.KEYSTORE, Option.STOREPASS, Option.STORETYPE, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V, Option.PROTECTED }),
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  265 */     IDENTITYDB("Imports.entries.from.a.JDK.1.1.x.style.identity.database", new Option[] { Option.FILEIN, Option.STORETYPE, Option.KEYSTORE, Option.STOREPASS, Option.PROVIDERNAME, Option.PROVIDERCLASS, Option.PROVIDERARG, Option.PROVIDERPATH, Option.V });
/*      */     
/*      */     final String description;
/*      */     final Option[] options;
/*      */     
/*      */     private Command(String paramString, Option... paramVarArgs)
/*      */     {
/*  272 */       this.description = paramString;
/*  273 */       this.options = paramVarArgs;
/*      */     }
/*      */     
/*      */     public String toString() {
/*  277 */       return "-" + name().toLowerCase(Locale.ENGLISH);
/*      */     }
/*      */   }
/*      */   
/*      */   static enum Option {
/*  282 */     ALIAS("alias", "<alias>", "alias.name.of.the.entry.to.process"), 
/*  283 */     DESTALIAS("destalias", "<destalias>", "destination.alias"), 
/*  284 */     DESTKEYPASS("destkeypass", "<arg>", "destination.key.password"), 
/*  285 */     DESTKEYSTORE("destkeystore", "<destkeystore>", "destination.keystore.name"), 
/*  286 */     DESTPROTECTED("destprotected", null, "destination.keystore.password.protected"), 
/*  287 */     DESTPROVIDERNAME("destprovidername", "<destprovidername>", "destination.keystore.provider.name"), 
/*  288 */     DESTSTOREPASS("deststorepass", "<arg>", "destination.keystore.password"), 
/*  289 */     DESTSTORETYPE("deststoretype", "<deststoretype>", "destination.keystore.type"), 
/*  290 */     DNAME("dname", "<dname>", "distinguished.name"), 
/*  291 */     EXT("ext", "<value>", "X.509.extension"), 
/*  292 */     FILEOUT("file", "<filename>", "output.file.name"), 
/*  293 */     FILEIN("file", "<filename>", "input.file.name"), 
/*  294 */     ID("id", "<id:reason>", "Serial.ID.of.cert.to.revoke"), 
/*  295 */     INFILE("infile", "<filename>", "input.file.name"), 
/*  296 */     KEYALG("keyalg", "<keyalg>", "key.algorithm.name"), 
/*  297 */     KEYPASS("keypass", "<arg>", "key.password"), 
/*  298 */     KEYSIZE("keysize", "<keysize>", "key.bit.size"), 
/*  299 */     KEYSTORE("keystore", "<keystore>", "keystore.name"), 
/*  300 */     NEW("new", "<arg>", "new.password"), 
/*  301 */     NOPROMPT("noprompt", null, "do.not.prompt"), 
/*  302 */     OUTFILE("outfile", "<filename>", "output.file.name"), 
/*  303 */     PROTECTED("protected", null, "password.through.protected.mechanism"), 
/*  304 */     PROVIDERARG("providerarg", "<arg>", "provider.argument"), 
/*  305 */     PROVIDERCLASS("providerclass", "<providerclass>", "provider.class.name"), 
/*  306 */     PROVIDERNAME("providername", "<providername>", "provider.name"), 
/*  307 */     PROVIDERPATH("providerpath", "<pathlist>", "provider.classpath"), 
/*  308 */     RFC("rfc", null, "output.in.RFC.style"), 
/*  309 */     SIGALG("sigalg", "<sigalg>", "signature.algorithm.name"), 
/*  310 */     SRCALIAS("srcalias", "<srcalias>", "source.alias"), 
/*  311 */     SRCKEYPASS("srckeypass", "<arg>", "source.key.password"), 
/*  312 */     SRCKEYSTORE("srckeystore", "<srckeystore>", "source.keystore.name"), 
/*  313 */     SRCPROTECTED("srcprotected", null, "source.keystore.password.protected"), 
/*  314 */     SRCPROVIDERNAME("srcprovidername", "<srcprovidername>", "source.keystore.provider.name"), 
/*  315 */     SRCSTOREPASS("srcstorepass", "<arg>", "source.keystore.password"), 
/*  316 */     SRCSTORETYPE("srcstoretype", "<srcstoretype>", "source.keystore.type"), 
/*  317 */     SSLSERVER("sslserver", "<server[:port]>", "SSL.server.host.and.port"), 
/*  318 */     JARFILE("jarfile", "<filename>", "signed.jar.file"), 
/*  319 */     STARTDATE("startdate", "<startdate>", "certificate.validity.start.date.time"), 
/*  320 */     STOREPASS("storepass", "<arg>", "keystore.password"), 
/*  321 */     STORETYPE("storetype", "<storetype>", "keystore.type"), 
/*  322 */     TRUSTCACERTS("trustcacerts", null, "trust.certificates.from.cacerts"), 
/*  323 */     V("v", null, "verbose.output"), 
/*  324 */     VALIDITY("validity", "<valDays>", "validity.number.of.days");
/*      */     
/*      */     final String name;
/*      */     
/*  328 */     private Option(String paramString1, String paramString2, String paramString3) { this.name = paramString1;
/*  329 */       this.arg = paramString2;
/*  330 */       this.description = paramString3; }
/*      */     
/*      */     final String arg;
/*      */     final String description;
/*  334 */     public String toString() { return "-" + this.name; }
/*      */   }
/*      */   
/*      */ 
/*  338 */   private static final Class<?>[] PARAM_STRING = { String.class };
/*      */   
/*      */   private static final String NONE = "NONE";
/*      */   
/*      */   private static final String P11KEYSTORE = "PKCS11";
/*      */   
/*      */   private static final String P12KEYSTORE = "PKCS12";
/*      */   
/*      */   private static final String keyAlias = "mykey";
/*  347 */   private static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.tools.keytool.Resources");
/*      */   
/*  349 */   private static final Collator collator = Collator.getInstance();
/*      */   
/*      */   static {
/*  352 */     collator.setStrength(0);
/*      */   }
/*      */   
/*      */   public static void main(String[] paramArrayOfString)
/*      */     throws Exception
/*      */   {
/*  358 */     Main localMain = new Main();
/*  359 */     localMain.run(paramArrayOfString, System.out);
/*      */   }
/*      */   
/*      */   private void run(String[] paramArrayOfString, PrintStream paramPrintStream) throws Exception {
/*      */     try {
/*  364 */       parseArgs(paramArrayOfString);
/*  365 */       if (this.command != null)
/*  366 */         doCommands(paramPrintStream);
/*      */     } catch (Exception localException) {
/*      */       Iterator localIterator1;
/*  369 */       System.out.println(rb.getString("keytool.error.") + localException);
/*  370 */       if (this.verbose) {
/*  371 */         localException.printStackTrace(System.out);
/*      */       }
/*  373 */       if (!this.debug) {
/*  374 */         System.exit(1);
/*      */       } else
/*  376 */         throw localException;
/*      */     } finally { char[] arrayOfChar1;
/*      */       Iterator localIterator2;
/*  379 */       printWeakWarnings(false);
/*  380 */       for (char[] arrayOfChar2 : this.passwords) {
/*  381 */         if (arrayOfChar2 != null) {
/*  382 */           Arrays.fill(arrayOfChar2, ' ');
/*  383 */           arrayOfChar2 = null;
/*      */         }
/*      */       }
/*      */       
/*  387 */       if (this.ksStream != null) {
/*  388 */         this.ksStream.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void parseArgs(String[] paramArrayOfString)
/*      */   {
/*  398 */     int i = 0;
/*  399 */     int j = paramArrayOfString.length == 0 ? 1 : 0;
/*      */     
/*  401 */     for (i = 0; (i < paramArrayOfString.length) && (paramArrayOfString[i].startsWith("-")); i++)
/*      */     {
/*  403 */       String str1 = paramArrayOfString[i];
/*      */       
/*      */       Object localObject2;
/*  406 */       if (i == paramArrayOfString.length - 1) {
/*  407 */         for (localObject2 : Option.values())
/*      */         {
/*  409 */           if (collator.compare(str1, ((Option)localObject2).toString()) == 0) {
/*  410 */             if (((Option)localObject2).arg == null) break; errorNeedArgument(str1); break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  419 */       ??? = null;
/*  420 */       ??? = str1.indexOf(':');
/*  421 */       if (??? > 0) {
/*  422 */         ??? = str1.substring(??? + 1);
/*  423 */         str1 = str1.substring(0, ???);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  428 */       ??? = 0;
/*  429 */       for (Command localCommand : Command.values()) {
/*  430 */         if (collator.compare(str1, localCommand.toString()) == 0) {
/*  431 */           this.command = localCommand;
/*  432 */           ??? = 1;
/*  433 */           break;
/*      */         }
/*      */       }
/*      */       
/*  437 */       if (??? == 0)
/*      */       {
/*  439 */         if (collator.compare(str1, "-export") == 0) {
/*  440 */           this.command = Command.EXPORTCERT;
/*  441 */         } else if (collator.compare(str1, "-genkey") == 0) {
/*  442 */           this.command = Command.GENKEYPAIR;
/*  443 */         } else if (collator.compare(str1, "-import") == 0) {
/*  444 */           this.command = Command.IMPORTCERT;
/*  445 */         } else if (collator.compare(str1, "-importpassword") == 0) {
/*  446 */           this.command = Command.IMPORTPASS;
/*  447 */         } else if (collator.compare(str1, "-help") == 0) {
/*  448 */           j = 1;
/*  449 */         } else if (collator.compare(str1, "-nowarn") == 0) {
/*  450 */           this.nowarn = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*  456 */         else if ((collator.compare(str1, "-keystore") == 0) || 
/*  457 */           (collator.compare(str1, "-destkeystore") == 0)) {
/*  458 */           this.ksfname = paramArrayOfString[(++i)];
/*  459 */         } else if ((collator.compare(str1, "-storepass") == 0) || 
/*  460 */           (collator.compare(str1, "-deststorepass") == 0)) {
/*  461 */           this.storePass = getPass((String)???, paramArrayOfString[(++i)]);
/*  462 */           this.passwords.add(this.storePass);
/*  463 */         } else if ((collator.compare(str1, "-storetype") == 0) || 
/*  464 */           (collator.compare(str1, "-deststoretype") == 0)) {
/*  465 */           this.storetype = paramArrayOfString[(++i)];
/*  466 */         } else if (collator.compare(str1, "-srcstorepass") == 0) {
/*  467 */           this.srcstorePass = getPass((String)???, paramArrayOfString[(++i)]);
/*  468 */           this.passwords.add(this.srcstorePass);
/*  469 */         } else if (collator.compare(str1, "-srcstoretype") == 0) {
/*  470 */           this.srcstoretype = paramArrayOfString[(++i)];
/*  471 */         } else if (collator.compare(str1, "-srckeypass") == 0) {
/*  472 */           this.srckeyPass = getPass((String)???, paramArrayOfString[(++i)]);
/*  473 */           this.passwords.add(this.srckeyPass);
/*  474 */         } else if (collator.compare(str1, "-srcprovidername") == 0) {
/*  475 */           this.srcProviderName = paramArrayOfString[(++i)];
/*  476 */         } else if ((collator.compare(str1, "-providername") == 0) || 
/*  477 */           (collator.compare(str1, "-destprovidername") == 0)) {
/*  478 */           this.providerName = paramArrayOfString[(++i)];
/*  479 */         } else if (collator.compare(str1, "-providerpath") == 0) {
/*  480 */           this.pathlist = paramArrayOfString[(++i)];
/*  481 */         } else if (collator.compare(str1, "-keypass") == 0) {
/*  482 */           this.keyPass = getPass((String)???, paramArrayOfString[(++i)]);
/*  483 */           this.passwords.add(this.keyPass);
/*  484 */         } else if (collator.compare(str1, "-new") == 0) {
/*  485 */           this.newPass = getPass((String)???, paramArrayOfString[(++i)]);
/*  486 */           this.passwords.add(this.newPass);
/*  487 */         } else if (collator.compare(str1, "-destkeypass") == 0) {
/*  488 */           this.destKeyPass = getPass((String)???, paramArrayOfString[(++i)]);
/*  489 */           this.passwords.add(this.destKeyPass);
/*  490 */         } else if ((collator.compare(str1, "-alias") == 0) || 
/*  491 */           (collator.compare(str1, "-srcalias") == 0)) {
/*  492 */           this.alias = paramArrayOfString[(++i)];
/*  493 */         } else if ((collator.compare(str1, "-dest") == 0) || 
/*  494 */           (collator.compare(str1, "-destalias") == 0)) {
/*  495 */           this.dest = paramArrayOfString[(++i)];
/*  496 */         } else if (collator.compare(str1, "-dname") == 0) {
/*  497 */           this.dname = paramArrayOfString[(++i)];
/*  498 */         } else if (collator.compare(str1, "-keysize") == 0) {
/*  499 */           this.keysize = Integer.parseInt(paramArrayOfString[(++i)]);
/*  500 */         } else if (collator.compare(str1, "-keyalg") == 0) {
/*  501 */           this.keyAlgName = paramArrayOfString[(++i)];
/*  502 */         } else if (collator.compare(str1, "-sigalg") == 0) {
/*  503 */           this.sigAlgName = paramArrayOfString[(++i)];
/*  504 */         } else if (collator.compare(str1, "-startdate") == 0) {
/*  505 */           this.startDate = paramArrayOfString[(++i)];
/*  506 */         } else if (collator.compare(str1, "-validity") == 0) {
/*  507 */           this.validity = Long.parseLong(paramArrayOfString[(++i)]);
/*  508 */         } else if (collator.compare(str1, "-ext") == 0) {
/*  509 */           this.v3ext.add(paramArrayOfString[(++i)]);
/*  510 */         } else if (collator.compare(str1, "-id") == 0) {
/*  511 */           this.ids.add(paramArrayOfString[(++i)]);
/*  512 */         } else if (collator.compare(str1, "-file") == 0) {
/*  513 */           this.filename = paramArrayOfString[(++i)];
/*  514 */         } else if (collator.compare(str1, "-infile") == 0) {
/*  515 */           this.infilename = paramArrayOfString[(++i)];
/*  516 */         } else if (collator.compare(str1, "-outfile") == 0) {
/*  517 */           this.outfilename = paramArrayOfString[(++i)];
/*  518 */         } else if (collator.compare(str1, "-sslserver") == 0) {
/*  519 */           this.sslserver = paramArrayOfString[(++i)];
/*  520 */         } else if (collator.compare(str1, "-jarfile") == 0) {
/*  521 */           this.jarfile = paramArrayOfString[(++i)];
/*  522 */         } else if (collator.compare(str1, "-srckeystore") == 0) {
/*  523 */           this.srcksfname = paramArrayOfString[(++i)];
/*  524 */         } else if ((collator.compare(str1, "-provider") == 0) || 
/*  525 */           (collator.compare(str1, "-providerclass") == 0)) {
/*  526 */           if (this.providers == null) {
/*  527 */             this.providers = new HashSet(3);
/*      */           }
/*  529 */           localObject2 = paramArrayOfString[(++i)];
/*  530 */           String str2 = null;
/*      */           
/*  532 */           if (paramArrayOfString.length > i + 1) {
/*  533 */             str1 = paramArrayOfString[(i + 1)];
/*  534 */             if (collator.compare(str1, "-providerarg") == 0) {
/*  535 */               if (paramArrayOfString.length == i + 2) errorNeedArgument(str1);
/*  536 */               str2 = paramArrayOfString[(i + 2)];
/*  537 */               i += 2;
/*      */             }
/*      */           }
/*  540 */           this.providers.add(
/*  541 */             Pair.of(localObject2, str2));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*  547 */         else if (collator.compare(str1, "-v") == 0) {
/*  548 */           this.verbose = true;
/*  549 */         } else if (collator.compare(str1, "-debug") == 0) {
/*  550 */           this.debug = true;
/*  551 */         } else if (collator.compare(str1, "-rfc") == 0) {
/*  552 */           this.rfc = true;
/*  553 */         } else if (collator.compare(str1, "-noprompt") == 0) {
/*  554 */           this.noprompt = true;
/*  555 */         } else if (collator.compare(str1, "-trustcacerts") == 0) {
/*  556 */           this.trustcacerts = true;
/*  557 */         } else if ((collator.compare(str1, "-protected") == 0) || 
/*  558 */           (collator.compare(str1, "-destprotected") == 0)) {
/*  559 */           this.protectedPath = true;
/*  560 */         } else if (collator.compare(str1, "-srcprotected") == 0) {
/*  561 */           this.srcprotectedPath = true;
/*      */         } else {
/*  563 */           System.err.println(rb.getString("Illegal.option.") + str1);
/*  564 */           tinyHelp();
/*      */         }
/*      */       }
/*      */     }
/*  568 */     if (i < paramArrayOfString.length) {
/*  569 */       System.err.println(rb.getString("Illegal.option.") + paramArrayOfString[i]);
/*  570 */       tinyHelp();
/*      */     }
/*      */     
/*  573 */     if (this.command == null) {
/*  574 */       if (j != 0) {
/*  575 */         usage();
/*      */       } else {
/*  577 */         System.err.println(rb.getString("Usage.error.no.command.provided"));
/*  578 */         tinyHelp();
/*      */       }
/*  580 */     } else if (j != 0) {
/*  581 */       usage();
/*  582 */       this.command = null;
/*      */     }
/*      */   }
/*      */   
/*      */   boolean isKeyStoreRelated(Command paramCommand) {
/*  587 */     return (paramCommand != Command.PRINTCERT) && (paramCommand != Command.PRINTCERTREQ);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void doCommands(PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/*  595 */     if (this.storetype == null) {
/*  596 */       this.storetype = KeyStore.getDefaultType();
/*      */     }
/*  598 */     this.storetype = KeyStoreUtil.niceStoreTypeName(this.storetype);
/*      */     
/*  600 */     if (this.srcstoretype == null) {
/*  601 */       this.srcstoretype = KeyStore.getDefaultType();
/*      */     }
/*  603 */     this.srcstoretype = KeyStoreUtil.niceStoreTypeName(this.srcstoretype);
/*      */     
/*  605 */     if (("PKCS11".equalsIgnoreCase(this.storetype)) || 
/*  606 */       (KeyStoreUtil.isWindowsKeyStore(this.storetype))) {
/*  607 */       this.token = true;
/*  608 */       if (this.ksfname == null) {
/*  609 */         this.ksfname = "NONE";
/*      */       }
/*      */     }
/*  612 */     if ("NONE".equals(this.ksfname)) {
/*  613 */       this.nullStream = true;
/*      */     }
/*      */     
/*  616 */     if ((this.token) && (!this.nullStream)) {
/*  617 */       System.err.println(MessageFormat.format(rb
/*  618 */         .getString(".keystore.must.be.NONE.if.storetype.is.{0}"), new Object[] { this.storetype }));
/*  619 */       System.err.println();
/*  620 */       tinyHelp();
/*      */     }
/*      */     
/*  623 */     if ((this.token) && ((this.command == Command.KEYPASSWD) || (this.command == Command.STOREPASSWD)))
/*      */     {
/*  625 */       throw new UnsupportedOperationException(MessageFormat.format(rb
/*  626 */         .getString(".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}"), new Object[] { this.storetype }));
/*      */     }
/*      */     
/*  629 */     if (("PKCS12".equalsIgnoreCase(this.storetype)) && (this.command == Command.KEYPASSWD))
/*      */     {
/*  631 */       throw new UnsupportedOperationException(rb.getString(".keypasswd.commands.not.supported.if.storetype.is.PKCS12"));
/*      */     }
/*      */     
/*  634 */     if ((this.token) && ((this.keyPass != null) || (this.newPass != null) || (this.destKeyPass != null))) {
/*  635 */       throw new IllegalArgumentException(MessageFormat.format(rb
/*  636 */         .getString(".keypass.and.new.can.not.be.specified.if.storetype.is.{0}"), new Object[] { this.storetype }));
/*      */     }
/*      */     
/*  639 */     if ((this.protectedPath) && (
/*  640 */       (this.storePass != null) || (this.keyPass != null) || (this.newPass != null) || (this.destKeyPass != null)))
/*      */     {
/*      */ 
/*  643 */       throw new IllegalArgumentException(rb.getString("if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified"));
/*      */     }
/*      */     
/*      */ 
/*  647 */     if ((this.srcprotectedPath) && (
/*  648 */       (this.srcstorePass != null) || (this.srckeyPass != null)))
/*      */     {
/*  650 */       throw new IllegalArgumentException(rb.getString("if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified"));
/*      */     }
/*      */     
/*      */ 
/*  654 */     if ((KeyStoreUtil.isWindowsKeyStore(this.storetype)) && (
/*  655 */       (this.storePass != null) || (this.keyPass != null) || (this.newPass != null) || (this.destKeyPass != null)))
/*      */     {
/*      */ 
/*  658 */       throw new IllegalArgumentException(rb.getString("if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified"));
/*      */     }
/*      */     
/*      */ 
/*  662 */     if ((KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) && (
/*  663 */       (this.srcstorePass != null) || (this.srckeyPass != null)))
/*      */     {
/*  665 */       throw new IllegalArgumentException(rb.getString("if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified"));
/*      */     }
/*      */     
/*      */ 
/*  669 */     if (this.validity <= 0L)
/*      */     {
/*  671 */       throw new Exception(rb.getString("Validity.must.be.greater.than.zero"));
/*      */     }
/*      */     Object localObject2;
/*      */     Object localObject4;
/*  675 */     if (this.providers != null) {
/*  676 */       localObject1 = null;
/*  677 */       if (this.pathlist != null) {
/*  678 */         localObject2 = null;
/*  679 */         localObject2 = PathList.appendPath((String)localObject2, 
/*  680 */           System.getProperty("java.class.path"));
/*  681 */         localObject2 = PathList.appendPath((String)localObject2, 
/*  682 */           System.getProperty("env.class.path"));
/*  683 */         localObject2 = PathList.appendPath((String)localObject2, this.pathlist);
/*      */         
/*  685 */         localObject4 = PathList.pathToURLs((String)localObject2);
/*  686 */         localObject1 = new java.net.URLClassLoader((java.net.URL[])localObject4);
/*      */       } else {
/*  688 */         localObject1 = ClassLoader.getSystemClassLoader();
/*      */       }
/*      */       
/*  691 */       for (localObject2 = this.providers.iterator(); ((Iterator)localObject2).hasNext();) { localObject4 = (Pair)((Iterator)localObject2).next();
/*  692 */         String str1 = (String)((Pair)localObject4).fst;
/*      */         
/*  694 */         if (localObject1 != null) {
/*  695 */           localObject5 = ((ClassLoader)localObject1).loadClass(str1);
/*      */         } else {
/*  697 */           localObject5 = Class.forName(str1);
/*      */         }
/*      */         
/*  700 */         String str3 = (String)((Pair)localObject4).snd;
/*      */         Object localObject6;
/*  702 */         Object localObject7; if (str3 == null) {
/*  703 */           localObject6 = ((Class)localObject5).newInstance();
/*      */         } else {
/*  705 */           localObject7 = ((Class)localObject5).getConstructor(PARAM_STRING);
/*  706 */           localObject6 = ((java.lang.reflect.Constructor)localObject7).newInstance(new Object[] { str3 });
/*      */         }
/*  708 */         if (!(localObject6 instanceof Provider))
/*      */         {
/*  710 */           localObject7 = new MessageFormat(rb.getString("provName.not.a.provider"));
/*  711 */           Object[] arrayOfObject = { str1 };
/*  712 */           throw new Exception(((MessageFormat)localObject7).format(arrayOfObject));
/*      */         }
/*  714 */         java.security.Security.addProvider((Provider)localObject6);
/*      */       }
/*      */     }
/*      */     Object localObject5;
/*  718 */     if ((this.command == Command.LIST) && (this.verbose) && (this.rfc)) {
/*  719 */       System.err.println(rb
/*  720 */         .getString("Must.not.specify.both.v.and.rfc.with.list.command"));
/*  721 */       tinyHelp();
/*      */     }
/*      */     
/*      */ 
/*  725 */     if ((this.command == Command.GENKEYPAIR) && (this.keyPass != null) && (this.keyPass.length < 6))
/*      */     {
/*  727 */       throw new Exception(rb.getString("Key.password.must.be.at.least.6.characters"));
/*      */     }
/*  729 */     if ((this.newPass != null) && (this.newPass.length < 6))
/*      */     {
/*  731 */       throw new Exception(rb.getString("New.password.must.be.at.least.6.characters"));
/*      */     }
/*  733 */     if ((this.destKeyPass != null) && (this.destKeyPass.length < 6))
/*      */     {
/*  735 */       throw new Exception(rb.getString("New.password.must.be.at.least.6.characters"));
/*      */     }
/*      */     
/*      */ 
/*  739 */     if (this.ksfname == null) {
/*  740 */       this.ksfname = (System.getProperty("user.home") + File.separator + ".keystore");
/*      */     }
/*      */     
/*      */ 
/*  744 */     Object localObject1 = null;
/*  745 */     if (this.command == Command.IMPORTKEYSTORE) {
/*  746 */       this.inplaceImport = inplaceImportCheck();
/*  747 */       if (this.inplaceImport)
/*      */       {
/*      */ 
/*  750 */         localObject1 = loadSourceKeyStore();
/*  751 */         if (this.storePass == null) {
/*  752 */           this.storePass = this.srcstorePass;
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
/*  765 */     if ((isKeyStoreRelated(this.command)) && (!this.nullStream) && (!this.inplaceImport)) {
/*      */       try {
/*  767 */         this.ksfile = new File(this.ksfname);
/*      */         
/*  769 */         if ((this.ksfile.exists()) && (this.ksfile.length() == 0L))
/*      */         {
/*  771 */           throw new Exception(rb.getString("Keystore.file.exists.but.is.empty.") + this.ksfname);
/*      */         }
/*  773 */         this.ksStream = new FileInputStream(this.ksfile);
/*      */       } catch (FileNotFoundException localFileNotFoundException) {
/*  775 */         if ((this.command != Command.GENKEYPAIR) && (this.command != Command.GENSECKEY) && (this.command != Command.IDENTITYDB) && (this.command != Command.IMPORTCERT) && (this.command != Command.IMPORTPASS) && (this.command != Command.IMPORTKEYSTORE) && (this.command != Command.PRINTCRL))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  783 */           throw new Exception(rb.getString("Keystore.file.does.not.exist.") + this.ksfname);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  788 */     if (((this.command == Command.KEYCLONE) || (this.command == Command.CHANGEALIAS)) && (this.dest == null))
/*      */     {
/*  790 */       this.dest = getAlias("destination");
/*  791 */       if ("".equals(this.dest))
/*      */       {
/*  793 */         throw new Exception(rb.getString("Must.specify.destination.alias"));
/*      */       }
/*      */     }
/*      */     
/*  797 */     if ((this.command == Command.DELETE) && (this.alias == null)) {
/*  798 */       this.alias = getAlias(null);
/*  799 */       if ("".equals(this.alias)) {
/*  800 */         throw new Exception(rb.getString("Must.specify.alias"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  805 */     if (this.providerName == null) {
/*  806 */       this.keyStore = KeyStore.getInstance(this.storetype);
/*      */     } else {
/*  808 */       this.keyStore = KeyStore.getInstance(this.storetype, this.providerName);
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
/*  831 */     if (!this.nullStream) {
/*  832 */       if (this.inplaceImport) {
/*  833 */         this.keyStore.load(null, this.storePass);
/*      */       } else {
/*  835 */         this.keyStore.load(this.ksStream, this.storePass);
/*      */       }
/*  837 */       if (this.ksStream != null) {
/*  838 */         this.ksStream.close();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  845 */     if ((this.nullStream) && (this.storePass != null)) {
/*  846 */       this.keyStore.load(null, this.storePass);
/*  847 */     } else if ((!this.nullStream) && (this.storePass != null))
/*      */     {
/*      */ 
/*  850 */       if ((this.ksStream == null) && (this.storePass.length < 6))
/*      */       {
/*  852 */         throw new Exception(rb.getString("Keystore.password.must.be.at.least.6.characters"));
/*      */       }
/*  854 */     } else if (this.storePass == null)
/*      */     {
/*      */ 
/*      */ 
/*  858 */       if ((!this.protectedPath) && (!KeyStoreUtil.isWindowsKeyStore(this.storetype)) && ((this.command == Command.CERTREQ) || (this.command == Command.DELETE) || (this.command == Command.GENKEYPAIR) || (this.command == Command.GENSECKEY) || (this.command == Command.IMPORTCERT) || (this.command == Command.IMPORTPASS) || (this.command == Command.IMPORTKEYSTORE) || (this.command == Command.KEYCLONE) || (this.command == Command.CHANGEALIAS) || (this.command == Command.SELFCERT) || (this.command == Command.STOREPASSWD) || (this.command == Command.KEYPASSWD) || (this.command == Command.IDENTITYDB)))
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
/*  872 */         int i = 0;
/*      */         do {
/*  874 */           if (this.command == Command.IMPORTKEYSTORE)
/*      */           {
/*  876 */             System.err.print(rb.getString("Enter.destination.keystore.password."));
/*      */           }
/*      */           else {
/*  879 */             System.err.print(rb.getString("Enter.keystore.password."));
/*      */           }
/*  881 */           System.err.flush();
/*  882 */           this.storePass = Password.readPassword(System.in);
/*  883 */           this.passwords.add(this.storePass);
/*      */           
/*      */ 
/*      */ 
/*  887 */           if ((!this.nullStream) && ((this.storePass == null) || (this.storePass.length < 6))) {
/*  888 */             System.err.println(rb
/*  889 */               .getString("Keystore.password.is.too.short.must.be.at.least.6.characters"));
/*  890 */             this.storePass = null;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  895 */           if ((this.storePass != null) && (!this.nullStream) && (this.ksStream == null)) {
/*  896 */             System.err.print(rb.getString("Re.enter.new.password."));
/*  897 */             localObject4 = Password.readPassword(System.in);
/*  898 */             this.passwords.add(localObject4);
/*  899 */             if (!Arrays.equals(this.storePass, (char[])localObject4))
/*      */             {
/*  901 */               System.err.println(rb.getString("They.don.t.match.Try.again"));
/*  902 */               this.storePass = null;
/*      */             }
/*      */           }
/*      */           
/*  906 */           i++;
/*  907 */         } while ((this.storePass == null) && (i < 3));
/*      */         
/*      */ 
/*  910 */         if (this.storePass == null)
/*      */         {
/*  912 */           System.err.println(rb.getString("Too.many.failures.try.later"));
/*  913 */           return;
/*      */         }
/*  915 */       } else if ((!this.protectedPath) && 
/*  916 */         (!KeyStoreUtil.isWindowsKeyStore(this.storetype)) && 
/*  917 */         (isKeyStoreRelated(this.command)))
/*      */       {
/*  919 */         if (this.command != Command.PRINTCRL) {
/*  920 */           System.err.print(rb.getString("Enter.keystore.password."));
/*  921 */           System.err.flush();
/*  922 */           this.storePass = Password.readPassword(System.in);
/*  923 */           this.passwords.add(this.storePass);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  929 */       if (this.nullStream) {
/*  930 */         this.keyStore.load(null, this.storePass);
/*  931 */       } else if (this.ksStream != null) {
/*  932 */         this.ksStream = new FileInputStream(this.ksfile);
/*  933 */         this.keyStore.load(this.ksStream, this.storePass);
/*  934 */         this.ksStream.close();
/*      */       }
/*      */     }
/*      */     Object localObject3;
/*  938 */     if ((this.storePass != null) && ("PKCS12".equalsIgnoreCase(this.storetype))) {
/*  939 */       localObject3 = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
/*      */       
/*  941 */       if ((this.keyPass != null) && (!Arrays.equals(this.storePass, this.keyPass))) {
/*  942 */         localObject4 = new Object[] { "-keypass" };
/*  943 */         System.err.println(((MessageFormat)localObject3).format(localObject4));
/*  944 */         this.keyPass = this.storePass;
/*      */       }
/*  946 */       if ((this.newPass != null) && (!Arrays.equals(this.storePass, this.newPass))) {
/*  947 */         localObject4 = new Object[] { "-new" };
/*  948 */         System.err.println(((MessageFormat)localObject3).format(localObject4));
/*  949 */         this.newPass = this.storePass;
/*      */       }
/*  951 */       if ((this.destKeyPass != null) && (!Arrays.equals(this.storePass, this.destKeyPass))) {
/*  952 */         localObject4 = new Object[] { "-destkeypass" };
/*  953 */         System.err.println(((MessageFormat)localObject3).format(localObject4));
/*  954 */         this.destKeyPass = this.storePass;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  959 */     if ((this.command == Command.PRINTCERT) || (this.command == Command.IMPORTCERT) || (this.command == Command.IDENTITYDB) || (this.command == Command.PRINTCRL))
/*      */     {
/*  961 */       this.cf = CertificateFactory.getInstance("X509");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  967 */     if (this.command != Command.IMPORTCERT) {
/*  968 */       this.trustcacerts = false;
/*      */     }
/*      */     
/*  971 */     if (this.trustcacerts) {
/*  972 */       this.caks = KeyStoreUtil.getCacertsKeyStore();
/*      */     }
/*      */     
/*      */ 
/*  976 */     if (this.command == Command.CERTREQ) {
/*  977 */       if (this.filename != null) {
/*  978 */         localObject3 = new PrintStream(new FileOutputStream(this.filename));localObject4 = null;
/*      */         try {
/*  980 */           doCertReq(this.alias, this.sigAlgName, (PrintStream)localObject3);
/*      */         }
/*      */         catch (Throwable localThrowable2)
/*      */         {
/*  978 */           localObject4 = localThrowable2;throw localThrowable2;
/*      */         }
/*      */         finally {
/*  981 */           if (localObject3 != null) if (localObject4 != null) try { ((PrintStream)localObject3).close(); } catch (Throwable localThrowable13) { ((Throwable)localObject4).addSuppressed(localThrowable13); } else ((PrintStream)localObject3).close();
/*      */         }
/*  983 */       } else { doCertReq(this.alias, this.sigAlgName, paramPrintStream);
/*      */       }
/*  985 */       if ((this.verbose) && (this.filename != null))
/*      */       {
/*  987 */         localObject3 = new MessageFormat(rb.getString("Certification.request.stored.in.file.filename."));
/*  988 */         localObject4 = new Object[] { this.filename };
/*  989 */         System.err.println(((MessageFormat)localObject3).format(localObject4));
/*  990 */         System.err.println(rb.getString("Submit.this.to.your.CA"));
/*      */       }
/*  992 */     } else if (this.command == Command.DELETE) {
/*  993 */       doDeleteEntry(this.alias);
/*  994 */       this.kssave = true;
/*  995 */     } else if (this.command == Command.EXPORTCERT) {
/*  996 */       if (this.filename != null) {
/*  997 */         localObject3 = new PrintStream(new FileOutputStream(this.filename));localObject4 = null;
/*      */         try {
/*  999 */           doExportCert(this.alias, (PrintStream)localObject3);
/*      */         }
/*      */         catch (Throwable localThrowable4)
/*      */         {
/*  997 */           localObject4 = localThrowable4;throw localThrowable4;
/*      */         }
/*      */         finally {
/* 1000 */           if (localObject3 != null) if (localObject4 != null) try { ((PrintStream)localObject3).close(); } catch (Throwable localThrowable14) { ((Throwable)localObject4).addSuppressed(localThrowable14); } else ((PrintStream)localObject3).close();
/*      */         }
/* 1002 */       } else { doExportCert(this.alias, paramPrintStream);
/*      */       }
/* 1004 */       if (this.filename != null)
/*      */       {
/* 1006 */         localObject3 = new MessageFormat(rb.getString("Certificate.stored.in.file.filename."));
/* 1007 */         localObject4 = new Object[] { this.filename };
/* 1008 */         System.err.println(((MessageFormat)localObject3).format(localObject4));
/*      */       }
/* 1010 */     } else if (this.command == Command.GENKEYPAIR) {
/* 1011 */       if (this.keyAlgName == null) {
/* 1012 */         this.keyAlgName = "DSA";
/*      */       }
/* 1014 */       doGenKeyPair(this.alias, this.dname, this.keyAlgName, this.keysize, this.sigAlgName);
/* 1015 */       this.kssave = true;
/* 1016 */     } else if (this.command == Command.GENSECKEY) {
/* 1017 */       if (this.keyAlgName == null) {
/* 1018 */         this.keyAlgName = "DES";
/*      */       }
/* 1020 */       doGenSecretKey(this.alias, this.keyAlgName, this.keysize);
/* 1021 */       this.kssave = true;
/* 1022 */     } else if (this.command == Command.IMPORTPASS) {
/* 1023 */       if (this.keyAlgName == null) {
/* 1024 */         this.keyAlgName = "PBE";
/*      */       }
/*      */       
/* 1027 */       doGenSecretKey(this.alias, this.keyAlgName, this.keysize);
/* 1028 */       this.kssave = true;
/* 1029 */     } else if (this.command == Command.IDENTITYDB) {
/* 1030 */       if (this.filename != null) {
/* 1031 */         localObject3 = new FileInputStream(this.filename);localObject4 = null;
/* 1032 */         try { doImportIdentityDatabase((InputStream)localObject3);
/*      */         }
/*      */         catch (Throwable localThrowable6)
/*      */         {
/* 1031 */           localObject4 = localThrowable6;throw localThrowable6;
/*      */         } finally {
/* 1033 */           if (localObject3 != null) if (localObject4 != null) try { ((InputStream)localObject3).close(); } catch (Throwable localThrowable15) { ((Throwable)localObject4).addSuppressed(localThrowable15); } else ((InputStream)localObject3).close();
/*      */         }
/* 1035 */       } else { doImportIdentityDatabase(System.in);
/*      */       }
/* 1037 */     } else if (this.command == Command.IMPORTCERT) {
/* 1038 */       localObject3 = System.in;
/* 1039 */       if (this.filename != null) {
/* 1040 */         localObject3 = new FileInputStream(this.filename);
/*      */       }
/* 1042 */       localObject4 = this.alias != null ? this.alias : "mykey";
/*      */       try {
/* 1044 */         if (this.keyStore.entryInstanceOf((String)localObject4, PrivateKeyEntry.class))
/*      */         {
/* 1046 */           this.kssave = installReply((String)localObject4, (InputStream)localObject3);
/* 1047 */           if (this.kssave) {
/* 1048 */             System.err.println(rb
/* 1049 */               .getString("Certificate.reply.was.installed.in.keystore"));
/*      */           } else {
/* 1051 */             System.err.println(rb
/* 1052 */               .getString("Certificate.reply.was.not.installed.in.keystore"));
/*      */           }
/* 1054 */         } else if ((!this.keyStore.containsAlias((String)localObject4)) || 
/* 1055 */           (this.keyStore.entryInstanceOf((String)localObject4, TrustedCertificateEntry.class)))
/*      */         {
/* 1057 */           this.kssave = addTrustedCert((String)localObject4, (InputStream)localObject3);
/* 1058 */           if (this.kssave) {
/* 1059 */             System.err.println(rb
/* 1060 */               .getString("Certificate.was.added.to.keystore"));
/*      */           } else {
/* 1062 */             System.err.println(rb
/* 1063 */               .getString("Certificate.was.not.added.to.keystore"));
/*      */           }
/*      */         }
/*      */       } finally {
/* 1067 */         if (localObject3 != System.in) {
/* 1068 */           ((InputStream)localObject3).close();
/*      */         }
/*      */       }
/* 1071 */     } else if (this.command == Command.IMPORTKEYSTORE)
/*      */     {
/* 1073 */       if (localObject1 == null) {
/* 1074 */         localObject1 = loadSourceKeyStore();
/*      */       }
/* 1076 */       doImportKeyStore((KeyStore)localObject1);
/* 1077 */       this.kssave = true;
/* 1078 */     } else if (this.command == Command.KEYCLONE) {
/* 1079 */       this.keyPassNew = this.newPass;
/*      */       
/*      */ 
/* 1082 */       if (this.alias == null) {
/* 1083 */         this.alias = "mykey";
/*      */       }
/* 1085 */       if (!this.keyStore.containsAlias(this.alias))
/*      */       {
/* 1087 */         localObject3 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/* 1088 */         localObject4 = new Object[] { this.alias };
/* 1089 */         throw new Exception(((MessageFormat)localObject3).format(localObject4));
/*      */       }
/* 1091 */       if (!this.keyStore.entryInstanceOf(this.alias, PrivateKeyEntry.class)) {
/* 1092 */         localObject3 = new MessageFormat(rb.getString("Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key"));
/*      */         
/* 1094 */         localObject4 = new Object[] { this.alias };
/* 1095 */         throw new Exception(((MessageFormat)localObject3).format(localObject4));
/*      */       }
/*      */       
/* 1098 */       doCloneEntry(this.alias, this.dest, true);
/* 1099 */       this.kssave = true;
/* 1100 */     } else if (this.command == Command.CHANGEALIAS) {
/* 1101 */       if (this.alias == null) {
/* 1102 */         this.alias = "mykey";
/*      */       }
/* 1104 */       doCloneEntry(this.alias, this.dest, false);
/*      */       
/* 1106 */       if (this.keyStore.containsAlias(this.alias)) {
/* 1107 */         doDeleteEntry(this.alias);
/*      */       }
/* 1109 */       this.kssave = true;
/* 1110 */     } else if (this.command == Command.KEYPASSWD) {
/* 1111 */       this.keyPassNew = this.newPass;
/* 1112 */       doChangeKeyPasswd(this.alias);
/* 1113 */       this.kssave = true;
/* 1114 */     } else if (this.command == Command.LIST) {
/* 1115 */       if ((this.storePass == null) && 
/* 1116 */         (!KeyStoreUtil.isWindowsKeyStore(this.storetype))) {
/* 1117 */         printNoIntegrityWarning();
/*      */       }
/*      */       
/* 1120 */       if (this.alias != null) {
/* 1121 */         doPrintEntry(rb.getString("the.certificate"), this.alias, paramPrintStream);
/*      */       } else {
/* 1123 */         doPrintEntries(paramPrintStream);
/*      */       }
/* 1125 */     } else if (this.command == Command.PRINTCERT) {
/* 1126 */       doPrintCert(paramPrintStream);
/* 1127 */     } else if (this.command == Command.SELFCERT) {
/* 1128 */       doSelfCert(this.alias, this.dname, this.sigAlgName);
/* 1129 */       this.kssave = true;
/* 1130 */     } else if (this.command == Command.STOREPASSWD) {
/* 1131 */       this.storePassNew = this.newPass;
/* 1132 */       if (this.storePassNew == null) {
/* 1133 */         this.storePassNew = getNewPasswd("keystore password", this.storePass);
/*      */       }
/* 1135 */       this.kssave = true;
/* 1136 */     } else if (this.command == Command.GENCERT) {
/* 1137 */       if (this.alias == null) {
/* 1138 */         this.alias = "mykey";
/*      */       }
/* 1140 */       localObject3 = System.in;
/* 1141 */       if (this.infilename != null) {
/* 1142 */         localObject3 = new FileInputStream(this.infilename);
/*      */       }
/* 1144 */       localObject4 = null;
/* 1145 */       if (this.outfilename != null) {
/* 1146 */         localObject4 = new PrintStream(new FileOutputStream(this.outfilename));
/* 1147 */         paramPrintStream = (PrintStream)localObject4;
/*      */       }
/*      */       try {
/* 1150 */         doGenCert(this.alias, this.sigAlgName, (InputStream)localObject3, paramPrintStream);
/*      */       } finally {
/* 1152 */         if (localObject3 != System.in) {
/* 1153 */           ((InputStream)localObject3).close();
/*      */         }
/* 1155 */         if (localObject4 != null) {
/* 1156 */           ((PrintStream)localObject4).close();
/*      */         }
/*      */       }
/* 1159 */     } else if (this.command == Command.GENCRL) {
/* 1160 */       if (this.alias == null) {
/* 1161 */         this.alias = "mykey";
/*      */       }
/* 1163 */       if (this.filename != null) {
/* 1164 */         localObject3 = new PrintStream(new FileOutputStream(this.filename));localObject4 = null;
/*      */         try {
/* 1166 */           doGenCRL((PrintStream)localObject3);
/*      */         }
/*      */         catch (Throwable localThrowable8)
/*      */         {
/* 1164 */           localObject4 = localThrowable8;throw localThrowable8;
/*      */         }
/*      */         finally {
/* 1167 */           if (localObject3 != null) if (localObject4 != null) try { ((PrintStream)localObject3).close(); } catch (Throwable localThrowable16) { ((Throwable)localObject4).addSuppressed(localThrowable16); } else ((PrintStream)localObject3).close();
/*      */         }
/* 1169 */       } else { doGenCRL(paramPrintStream);
/*      */       }
/* 1171 */     } else if (this.command == Command.PRINTCERTREQ) {
/* 1172 */       if (this.filename != null) {
/* 1173 */         localObject3 = new FileInputStream(this.filename);localObject4 = null;
/* 1174 */         try { doPrintCertReq((InputStream)localObject3, paramPrintStream);
/*      */         }
/*      */         catch (Throwable localThrowable10)
/*      */         {
/* 1173 */           localObject4 = localThrowable10;throw localThrowable10;
/*      */         } finally {
/* 1175 */           if (localObject3 != null) if (localObject4 != null) try { ((InputStream)localObject3).close(); } catch (Throwable localThrowable17) { ((Throwable)localObject4).addSuppressed(localThrowable17); } else ((InputStream)localObject3).close();
/*      */         }
/* 1177 */       } else { doPrintCertReq(System.in, paramPrintStream);
/*      */       }
/* 1179 */     } else if (this.command == Command.PRINTCRL) {
/* 1180 */       doPrintCRL(this.filename, paramPrintStream);
/*      */     }
/*      */     
/*      */ 
/* 1184 */     if (this.kssave) {
/* 1185 */       if (this.verbose)
/*      */       {
/* 1187 */         localObject3 = new MessageFormat(rb.getString(".Storing.ksfname."));
/* 1188 */         localObject4 = new Object[] { this.nullStream ? "keystore" : this.ksfname };
/* 1189 */         System.err.println(((MessageFormat)localObject3).format(localObject4));
/*      */       }
/*      */       
/* 1192 */       if (this.token) {
/* 1193 */         this.keyStore.store(null, null);
/*      */       } else {
/* 1195 */         localObject3 = this.storePassNew != null ? this.storePassNew : this.storePass;
/* 1196 */         if (this.nullStream) {
/* 1197 */           this.keyStore.store(null, (char[])localObject3);
/*      */         } else {
/* 1199 */           localObject4 = new ByteArrayOutputStream();
/* 1200 */           this.keyStore.store((java.io.OutputStream)localObject4, (char[])localObject3);
/* 1201 */           FileOutputStream localFileOutputStream = new FileOutputStream(this.ksfname);localObject5 = null;
/* 1202 */           try { localFileOutputStream.write(((ByteArrayOutputStream)localObject4).toByteArray());
/*      */           }
/*      */           catch (Throwable localThrowable12)
/*      */           {
/* 1201 */             localObject5 = localThrowable12;throw localThrowable12;
/*      */           } finally {
/* 1203 */             if (localFileOutputStream != null) if (localObject5 != null) try { localFileOutputStream.close(); } catch (Throwable localThrowable18) { ((Throwable)localObject5).addSuppressed(localThrowable18); } else localFileOutputStream.close();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1208 */     if ((isKeyStoreRelated(this.command)) && (!this.token) && (!this.nullStream) && (this.ksfname != null))
/*      */     {
/*      */ 
/*      */ 
/* 1212 */       localObject3 = new File(this.ksfname);
/* 1213 */       if (((File)localObject3).exists())
/*      */       {
/*      */ 
/* 1216 */         localObject4 = keyStoreType((File)localObject3);
/* 1217 */         if ((((String)localObject4).equalsIgnoreCase("JKS")) || 
/* 1218 */           (((String)localObject4).equalsIgnoreCase("JCEKS"))) {
/* 1219 */           int j = 1;
/* 1220 */           for (localObject5 = Collections.list(this.keyStore.aliases()).iterator(); ((Iterator)localObject5).hasNext();) { String str4 = (String)((Iterator)localObject5).next();
/* 1221 */             if (!this.keyStore.entryInstanceOf(str4, TrustedCertificateEntry.class))
/*      */             {
/* 1223 */               j = 0;
/* 1224 */               break;
/*      */             }
/*      */           }
/*      */           
/* 1228 */           if (j == 0) {
/* 1229 */             this.weakWarnings.add(String.format(rb
/* 1230 */               .getString("jks.storetype.warning"), new Object[] { localObject4, this.ksfname }));
/*      */           }
/*      */         }
/*      */         
/* 1234 */         if (this.inplaceImport)
/*      */         {
/* 1236 */           String str2 = keyStoreType(new File(this.inplaceBackupName));
/*      */           
/*      */ 
/*      */ 
/* 1240 */           localObject5 = ((String)localObject4).equalsIgnoreCase(str2) ? rb.getString("backup.keystore.warning") : rb.getString("migrate.keystore.warning");
/* 1241 */           this.weakWarnings.add(
/* 1242 */             String.format((String)localObject5, new Object[] { this.srcksfname, str2, this.inplaceBackupName, localObject4 }));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String keyStoreType(File paramFile)
/*      */     throws IOException
/*      */   {
/* 1253 */     int i = -17957139;
/* 1254 */     int j = -825307442;
/* 1255 */     DataInputStream localDataInputStream = new DataInputStream(new FileInputStream(paramFile));Object localObject1 = null;
/*      */     try {
/* 1257 */       int k = localDataInputStream.readInt();
/* 1258 */       String str; if (k == i)
/* 1259 */         return "JKS";
/* 1260 */       if (k == j) {
/* 1261 */         return "JCEKS";
/*      */       }
/* 1263 */       return "Non JKS/JCEKS";
/*      */     }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/* 1255 */       localObject1 = localThrowable1;throw localThrowable1;
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1265 */       if (localDataInputStream != null) { if (localObject1 != null) try { localDataInputStream.close(); } catch (Throwable localThrowable5) { ((Throwable)localObject1).addSuppressed(localThrowable5); } else { localDataInputStream.close();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doGenCert(String paramString1, String paramString2, InputStream paramInputStream, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 1277 */     if (!this.keyStore.containsAlias(paramString1))
/*      */     {
/* 1279 */       localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/* 1280 */       localObject2 = new Object[] { paramString1 };
/* 1281 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/* 1283 */     Object localObject1 = this.keyStore.getCertificate(paramString1);
/* 1284 */     Object localObject2 = ((Certificate)localObject1).getEncoded();
/* 1285 */     X509CertImpl localX509CertImpl1 = new X509CertImpl((byte[])localObject2);
/* 1286 */     X509CertInfo localX509CertInfo1 = (X509CertInfo)localX509CertImpl1.get("x509.info");
/*      */     
/* 1288 */     X500Name localX500Name = (X500Name)localX509CertInfo1.get("subject.dname");
/*      */     
/*      */ 
/* 1291 */     Date localDate1 = getStartDate(this.startDate);
/* 1292 */     Date localDate2 = new Date();
/* 1293 */     localDate2.setTime(localDate1.getTime() + this.validity * 1000L * 24L * 60L * 60L);
/* 1294 */     CertificateValidity localCertificateValidity = new CertificateValidity(localDate1, localDate2);
/*      */     
/*      */ 
/*      */ 
/* 1298 */     PrivateKey localPrivateKey = (PrivateKey)recoverKey(paramString1, this.storePass, this.keyPass).fst;
/* 1299 */     if (paramString2 == null) {
/* 1300 */       paramString2 = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
/*      */     }
/* 1302 */     Signature localSignature = Signature.getInstance(paramString2);
/* 1303 */     localSignature.initSign(localPrivateKey);
/*      */     
/* 1305 */     X509CertInfo localX509CertInfo2 = new X509CertInfo();
/* 1306 */     localX509CertInfo2.set("validity", localCertificateValidity);
/* 1307 */     localX509CertInfo2.set("serialNumber", new sun.security.x509.CertificateSerialNumber(new Random()
/* 1308 */       .nextInt() & 0x7FFFFFFF));
/* 1309 */     localX509CertInfo2.set("version", new CertificateVersion(2));
/*      */     
/* 1311 */     localX509CertInfo2.set("algorithmID", new sun.security.x509.CertificateAlgorithmId(
/*      */     
/* 1313 */       AlgorithmId.get(paramString2)));
/* 1314 */     localX509CertInfo2.set("issuer", localX500Name);
/*      */     
/* 1316 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
/* 1317 */     int i = 0;
/* 1318 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */     for (;;) {
/* 1320 */       localObject3 = localBufferedReader.readLine();
/* 1321 */       if (localObject3 == null) {
/*      */         break;
/*      */       }
/* 1324 */       if ((((String)localObject3).startsWith("-----BEGIN")) && (((String)localObject3).indexOf("REQUEST") >= 0)) {
/* 1325 */         i = 1;
/*      */       } else {
/* 1327 */         if ((((String)localObject3).startsWith("-----END")) && (((String)localObject3).indexOf("REQUEST") >= 0))
/*      */           break;
/* 1329 */         if (i != 0)
/* 1330 */           localStringBuffer.append((String)localObject3);
/*      */       }
/*      */     }
/* 1333 */     Object localObject3 = Pem.decode(new String(localStringBuffer));
/* 1334 */     PKCS10 localPKCS10 = new PKCS10((byte[])localObject3);
/*      */     
/* 1336 */     checkWeak(rb.getString("the.certificate.request"), localPKCS10);
/*      */     
/* 1338 */     localX509CertInfo2.set("key", new sun.security.x509.CertificateX509Key(localPKCS10.getSubjectPublicKeyInfo()));
/* 1339 */     localX509CertInfo2.set("subject", this.dname == null ? localPKCS10
/* 1340 */       .getSubjectName() : new X500Name(this.dname));
/* 1341 */     CertificateExtensions localCertificateExtensions = null;
/* 1342 */     Iterator localIterator = localPKCS10.getAttributes().getAttributes().iterator();
/* 1343 */     while (localIterator.hasNext()) {
/* 1344 */       localObject4 = (PKCS10Attribute)localIterator.next();
/* 1345 */       if (((PKCS10Attribute)localObject4).getAttributeId().equals(PKCS9Attribute.EXTENSION_REQUEST_OID)) {
/* 1346 */         localCertificateExtensions = (CertificateExtensions)((PKCS10Attribute)localObject4).getAttributeValue();
/*      */       }
/*      */     }
/* 1349 */     Object localObject4 = createV3Extensions(localCertificateExtensions, null, this.v3ext, localPKCS10
/*      */     
/*      */ 
/*      */ 
/* 1353 */       .getSubjectPublicKeyInfo(), ((Certificate)localObject1)
/* 1354 */       .getPublicKey());
/* 1355 */     localX509CertInfo2.set("extensions", localObject4);
/* 1356 */     X509CertImpl localX509CertImpl2 = new X509CertImpl(localX509CertInfo2);
/* 1357 */     localX509CertImpl2.sign(localPrivateKey, paramString2);
/* 1358 */     dumpCert(localX509CertImpl2, paramPrintStream);
/* 1359 */     for (Certificate localCertificate : this.keyStore.getCertificateChain(paramString1)) {
/* 1360 */       if ((localCertificate instanceof X509Certificate)) {
/* 1361 */         X509Certificate localX509Certificate = (X509Certificate)localCertificate;
/* 1362 */         if (!isSelfSigned(localX509Certificate)) {
/* 1363 */           dumpCert(localX509Certificate, paramPrintStream);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1368 */     checkWeak(rb.getString("the.issuer"), this.keyStore.getCertificateChain(paramString1));
/* 1369 */     checkWeak(rb.getString("the.generated.certificate"), localX509CertImpl2);
/*      */   }
/*      */   
/*      */   private void doGenCRL(PrintStream paramPrintStream) throws Exception
/*      */   {
/* 1374 */     if (this.ids == null) {
/* 1375 */       throw new Exception("Must provide -id when -gencrl");
/*      */     }
/* 1377 */     Certificate localCertificate = this.keyStore.getCertificate(this.alias);
/* 1378 */     byte[] arrayOfByte = localCertificate.getEncoded();
/* 1379 */     X509CertImpl localX509CertImpl = new X509CertImpl(arrayOfByte);
/* 1380 */     X509CertInfo localX509CertInfo = (X509CertInfo)localX509CertImpl.get("x509.info");
/*      */     
/* 1382 */     X500Name localX500Name = (X500Name)localX509CertInfo.get("subject.dname");
/*      */     
/*      */ 
/* 1385 */     Date localDate1 = getStartDate(this.startDate);
/* 1386 */     Date localDate2 = (Date)localDate1.clone();
/* 1387 */     localDate2.setTime(localDate2.getTime() + this.validity * 1000L * 24L * 60L * 60L);
/* 1388 */     CertificateValidity localCertificateValidity = new CertificateValidity(localDate1, localDate2);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1393 */     PrivateKey localPrivateKey = (PrivateKey)recoverKey(this.alias, this.storePass, this.keyPass).fst;
/* 1394 */     if (this.sigAlgName == null) {
/* 1395 */       this.sigAlgName = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
/*      */     }
/*      */     
/* 1398 */     X509CRLEntry[] arrayOfX509CRLEntry = new X509CRLEntry[this.ids.size()];
/* 1399 */     for (int i = 0; i < this.ids.size(); i++) {
/* 1400 */       String str = (String)this.ids.get(i);
/* 1401 */       int j = str.indexOf(':');
/* 1402 */       if (j >= 0) {
/* 1403 */         CRLExtensions localCRLExtensions = new CRLExtensions();
/* 1404 */         localCRLExtensions.set("Reason", new sun.security.x509.CRLReasonCodeExtension(Integer.parseInt(str.substring(j + 1))));
/* 1405 */         arrayOfX509CRLEntry[i] = new X509CRLEntryImpl(new BigInteger(str.substring(0, j)), localDate1, localCRLExtensions);
/*      */       }
/*      */       else {
/* 1408 */         arrayOfX509CRLEntry[i] = new X509CRLEntryImpl(new BigInteger((String)this.ids.get(i)), localDate1);
/*      */       }
/*      */     }
/* 1411 */     X509CRLImpl localX509CRLImpl = new X509CRLImpl(localX500Name, localDate1, localDate2, arrayOfX509CRLEntry);
/* 1412 */     localX509CRLImpl.sign(localPrivateKey, this.sigAlgName);
/* 1413 */     if (this.rfc) {
/* 1414 */       paramPrintStream.println("-----BEGIN X509 CRL-----");
/* 1415 */       paramPrintStream.println(Base64.getMimeEncoder(64, CRLF).encodeToString(localX509CRLImpl.getEncodedInternal()));
/* 1416 */       paramPrintStream.println("-----END X509 CRL-----");
/*      */     } else {
/* 1418 */       paramPrintStream.write(localX509CRLImpl.getEncodedInternal());
/*      */     }
/* 1420 */     checkWeak(rb.getString("the.generated.crl"), localX509CRLImpl, localPrivateKey);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doCertReq(String paramString1, String paramString2, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 1430 */     if (paramString1 == null) {
/* 1431 */       paramString1 = "mykey";
/*      */     }
/*      */     
/* 1434 */     Pair localPair = recoverKey(paramString1, this.storePass, this.keyPass);
/* 1435 */     PrivateKey localPrivateKey = (PrivateKey)localPair.fst;
/* 1436 */     if (this.keyPass == null) {
/* 1437 */       this.keyPass = ((char[])localPair.snd);
/*      */     }
/*      */     
/* 1440 */     Certificate localCertificate = this.keyStore.getCertificate(paramString1);
/* 1441 */     if (localCertificate == null)
/*      */     {
/* 1443 */       localObject1 = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
/* 1444 */       localObject2 = new Object[] { paramString1 };
/* 1445 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/* 1447 */     Object localObject1 = new PKCS10(localCertificate.getPublicKey());
/* 1448 */     Object localObject2 = createV3Extensions(null, null, this.v3ext, localCertificate.getPublicKey(), null);
/*      */     
/* 1450 */     ((PKCS10)localObject1).getAttributes().setAttribute("extensions", new PKCS10Attribute(PKCS9Attribute.EXTENSION_REQUEST_OID, localObject2));
/*      */     
/*      */ 
/*      */ 
/* 1454 */     if (paramString2 == null) {
/* 1455 */       paramString2 = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
/*      */     }
/*      */     
/* 1458 */     Signature localSignature = Signature.getInstance(paramString2);
/* 1459 */     localSignature.initSign(localPrivateKey);
/*      */     
/* 1461 */     X500Name localX500Name = this.dname == null ? new X500Name(((X509Certificate)localCertificate).getSubjectDN().toString()) : new X500Name(this.dname);
/*      */     
/*      */ 
/*      */ 
/* 1465 */     ((PKCS10)localObject1).encodeAndSign(localX500Name, localSignature);
/* 1466 */     ((PKCS10)localObject1).print(paramPrintStream);
/*      */     
/* 1468 */     checkWeak(rb.getString("the.generated.certificate.request"), (PKCS10)localObject1);
/*      */   }
/*      */   
/*      */ 
/*      */   private void doDeleteEntry(String paramString)
/*      */     throws Exception
/*      */   {
/* 1475 */     if (!this.keyStore.containsAlias(paramString))
/*      */     {
/* 1477 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/* 1478 */       Object[] arrayOfObject = { paramString };
/* 1479 */       throw new Exception(localMessageFormat.format(arrayOfObject));
/*      */     }
/* 1481 */     this.keyStore.deleteEntry(paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doExportCert(String paramString, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 1490 */     if ((this.storePass == null) && 
/* 1491 */       (!KeyStoreUtil.isWindowsKeyStore(this.storetype))) {
/* 1492 */       printNoIntegrityWarning();
/*      */     }
/* 1494 */     if (paramString == null)
/* 1495 */       paramString = "mykey";
/*      */     Object localObject2;
/* 1497 */     if (!this.keyStore.containsAlias(paramString))
/*      */     {
/* 1499 */       localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/* 1500 */       localObject2 = new Object[] { paramString };
/* 1501 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */     
/* 1504 */     Object localObject1 = (X509Certificate)this.keyStore.getCertificate(paramString);
/* 1505 */     if (localObject1 == null)
/*      */     {
/* 1507 */       localObject2 = new MessageFormat(rb.getString("Alias.alias.has.no.certificate"));
/* 1508 */       Object[] arrayOfObject = { paramString };
/* 1509 */       throw new Exception(((MessageFormat)localObject2).format(arrayOfObject));
/*      */     }
/* 1511 */     dumpCert((Certificate)localObject1, paramPrintStream);
/* 1512 */     checkWeak(rb.getString("the.certificate"), (Certificate)localObject1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private char[] promptForKeyPass(String paramString1, String paramString2, char[] paramArrayOfChar)
/*      */     throws Exception
/*      */   {
/* 1522 */     if ("PKCS12".equalsIgnoreCase(this.storetype))
/* 1523 */       return paramArrayOfChar;
/* 1524 */     if ((!this.token) && (!this.protectedPath))
/*      */     {
/*      */ 
/* 1527 */       for (int i = 0; i < 3; i++)
/*      */       {
/* 1529 */         MessageFormat localMessageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
/* 1530 */         Object[] arrayOfObject = { paramString1 };
/* 1531 */         System.err.println(localMessageFormat.format(arrayOfObject));
/* 1532 */         if (paramString2 == null) {
/* 1533 */           System.err.print(rb
/* 1534 */             .getString(".RETURN.if.same.as.keystore.password."));
/*      */         }
/*      */         else {
/* 1537 */           localMessageFormat = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
/* 1538 */           localObject = new Object[] { paramString2 };
/* 1539 */           System.err.print(localMessageFormat.format(localObject));
/*      */         }
/* 1541 */         System.err.flush();
/* 1542 */         Object localObject = Password.readPassword(System.in);
/* 1543 */         this.passwords.add(localObject);
/* 1544 */         if (localObject == null)
/* 1545 */           return paramArrayOfChar;
/* 1546 */         if (localObject.length >= 6) {
/* 1547 */           System.err.print(rb.getString("Re.enter.new.password."));
/* 1548 */           char[] arrayOfChar = Password.readPassword(System.in);
/* 1549 */           this.passwords.add(arrayOfChar);
/* 1550 */           if (!Arrays.equals((char[])localObject, arrayOfChar))
/*      */           {
/* 1552 */             System.err.println(rb.getString("They.don.t.match.Try.again"));
/*      */           }
/*      */           else
/* 1555 */             return (char[])localObject;
/*      */         } else {
/* 1557 */           System.err.println(rb
/* 1558 */             .getString("Key.password.is.too.short.must.be.at.least.6.characters"));
/*      */         }
/*      */       }
/* 1561 */       if (i == 3) {
/* 1562 */         if (this.command == Command.KEYCLONE)
/*      */         {
/* 1564 */           throw new Exception(rb.getString("Too.many.failures.Key.entry.not.cloned"));
/*      */         }
/*      */         
/* 1567 */         throw new Exception(rb.getString("Too.many.failures.key.not.added.to.keystore"));
/*      */       }
/*      */     }
/*      */     
/* 1571 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private char[] promptForCredential()
/*      */     throws Exception
/*      */   {
/* 1579 */     if (System.console() == null) {
/* 1580 */       char[] arrayOfChar1 = Password.readPassword(System.in);
/* 1581 */       this.passwords.add(arrayOfChar1);
/* 1582 */       return arrayOfChar1;
/*      */     }
/*      */     
/*      */ 
/* 1586 */     for (int i = 0; i < 3; i++) {
/* 1587 */       System.err.print(rb
/* 1588 */         .getString("Enter.the.password.to.be.stored."));
/* 1589 */       System.err.flush();
/* 1590 */       char[] arrayOfChar2 = Password.readPassword(System.in);
/* 1591 */       this.passwords.add(arrayOfChar2);
/* 1592 */       System.err.print(rb.getString("Re.enter.password."));
/* 1593 */       char[] arrayOfChar3 = Password.readPassword(System.in);
/* 1594 */       this.passwords.add(arrayOfChar3);
/* 1595 */       if (!Arrays.equals(arrayOfChar2, arrayOfChar3)) {
/* 1596 */         System.err.println(rb.getString("They.don.t.match.Try.again"));
/*      */       }
/*      */       else {
/* 1599 */         return arrayOfChar2;
/*      */       }
/*      */     }
/* 1602 */     if (i == 3)
/*      */     {
/* 1604 */       throw new Exception(rb.getString("Too.many.failures.key.not.added.to.keystore"));
/*      */     }
/*      */     
/* 1607 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doGenSecretKey(String paramString1, String paramString2, int paramInt)
/*      */     throws Exception
/*      */   {
/* 1617 */     if (paramString1 == null) {
/* 1618 */       paramString1 = "mykey";
/*      */     }
/* 1620 */     if (this.keyStore.containsAlias(paramString1))
/*      */     {
/* 1622 */       MessageFormat localMessageFormat1 = new MessageFormat(rb.getString("Secret.key.not.generated.alias.alias.already.exists"));
/* 1623 */       localObject1 = new Object[] { paramString1 };
/* 1624 */       throw new Exception(localMessageFormat1.format(localObject1));
/*      */     }
/*      */     
/*      */ 
/* 1628 */     int i = 1;
/* 1629 */     Object localObject1 = null;
/*      */     Object localObject2;
/* 1631 */     MessageFormat localMessageFormat2; Object[] arrayOfObject; if (paramString2.toUpperCase(Locale.ENGLISH).startsWith("PBE")) {
/* 1632 */       localObject2 = SecretKeyFactory.getInstance("PBE");
/*      */       
/*      */ 
/*      */ 
/* 1636 */       localObject1 = ((SecretKeyFactory)localObject2).generateSecret(new javax.crypto.spec.PBEKeySpec(promptForCredential()));
/*      */       
/*      */ 
/* 1639 */       if (!"PBE".equalsIgnoreCase(paramString2)) {
/* 1640 */         i = 0;
/*      */       }
/*      */       
/* 1643 */       if (this.verbose) {
/* 1644 */         localMessageFormat2 = new MessageFormat(rb.getString("Generated.keyAlgName.secret.key"));
/*      */         
/*      */ 
/* 1647 */         arrayOfObject = new Object[] { i != 0 ? "PBE" : ((SecretKey)localObject1).getAlgorithm() };
/* 1648 */         System.err.println(localMessageFormat2.format(arrayOfObject));
/*      */       }
/*      */     } else {
/* 1651 */       localObject2 = KeyGenerator.getInstance(paramString2);
/* 1652 */       if (paramInt == -1) {
/* 1653 */         if ("DES".equalsIgnoreCase(paramString2)) {
/* 1654 */           paramInt = 56;
/* 1655 */         } else if ("DESede".equalsIgnoreCase(paramString2)) {
/* 1656 */           paramInt = 168;
/*      */         }
/*      */         else {
/* 1659 */           throw new Exception(rb.getString("Please.provide.keysize.for.secret.key.generation"));
/*      */         }
/*      */       }
/* 1662 */       ((KeyGenerator)localObject2).init(paramInt);
/* 1663 */       localObject1 = ((KeyGenerator)localObject2).generateKey();
/*      */       
/* 1665 */       if (this.verbose)
/*      */       {
/* 1667 */         localMessageFormat2 = new MessageFormat(rb.getString("Generated.keysize.bit.keyAlgName.secret.key"));
/*      */         
/* 1669 */         arrayOfObject = new Object[] { new Integer(paramInt), ((SecretKey)localObject1).getAlgorithm() };
/* 1670 */         System.err.println(localMessageFormat2.format(arrayOfObject));
/*      */       }
/*      */     }
/*      */     
/* 1674 */     if (this.keyPass == null) {
/* 1675 */       this.keyPass = promptForKeyPass(paramString1, null, this.storePass);
/*      */     }
/*      */     
/* 1678 */     if (i != 0) {
/* 1679 */       this.keyStore.setKeyEntry(paramString1, (Key)localObject1, this.keyPass, null);
/*      */     } else {
/* 1681 */       this.keyStore.setEntry(paramString1, new SecretKeyEntry((SecretKey)localObject1), new PasswordProtection(this.keyPass, paramString2, null));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getCompatibleSigAlgName(String paramString)
/*      */     throws Exception
/*      */   {
/* 1692 */     if ("DSA".equalsIgnoreCase(paramString))
/* 1693 */       return "SHA256WithDSA";
/* 1694 */     if ("RSA".equalsIgnoreCase(paramString))
/* 1695 */       return "SHA256WithRSA";
/* 1696 */     if ("EC".equalsIgnoreCase(paramString)) {
/* 1697 */       return "SHA256withECDSA";
/*      */     }
/*      */     
/* 1700 */     throw new Exception(rb.getString("Cannot.derive.signature.algorithm"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doGenKeyPair(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4)
/*      */     throws Exception
/*      */   {
/* 1710 */     if (paramInt == -1) {
/* 1711 */       if ("EC".equalsIgnoreCase(paramString3)) {
/* 1712 */         paramInt = SecurityProviderConstants.DEF_EC_KEY_SIZE;
/* 1713 */       } else if ("RSA".equalsIgnoreCase(paramString3)) {
/* 1714 */         paramInt = SecurityProviderConstants.DEF_RSA_KEY_SIZE;
/* 1715 */       } else if ("DSA".equalsIgnoreCase(paramString3)) {
/* 1716 */         paramInt = SecurityProviderConstants.DEF_DSA_KEY_SIZE;
/*      */       }
/*      */     }
/*      */     
/* 1720 */     if (paramString1 == null) {
/* 1721 */       paramString1 = "mykey";
/*      */     }
/*      */     Object localObject2;
/* 1724 */     if (this.keyStore.containsAlias(paramString1))
/*      */     {
/* 1726 */       localObject1 = new MessageFormat(rb.getString("Key.pair.not.generated.alias.alias.already.exists"));
/* 1727 */       localObject2 = new Object[] { paramString1 };
/* 1728 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */     
/* 1731 */     if (paramString4 == null) {
/* 1732 */       paramString4 = getCompatibleSigAlgName(paramString3);
/*      */     }
/* 1734 */     Object localObject1 = new CertAndKeyGen(paramString3, paramString4, this.providerName);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1740 */     if (paramString2 == null) {
/* 1741 */       localObject2 = getX500Name();
/*      */     } else {
/* 1743 */       localObject2 = new X500Name(paramString2);
/*      */     }
/*      */     
/* 1746 */     ((CertAndKeyGen)localObject1).generate(paramInt);
/* 1747 */     PrivateKey localPrivateKey = ((CertAndKeyGen)localObject1).getPrivateKey();
/*      */     
/* 1749 */     CertificateExtensions localCertificateExtensions = createV3Extensions(null, null, this.v3ext, ((CertAndKeyGen)localObject1)
/*      */     
/*      */ 
/*      */ 
/* 1753 */       .getPublicKeyAnyway(), null);
/*      */     
/*      */ 
/* 1756 */     X509Certificate[] arrayOfX509Certificate = new X509Certificate[1];
/* 1757 */     arrayOfX509Certificate[0] = ((CertAndKeyGen)localObject1).getSelfCertificate((X500Name)localObject2, 
/* 1758 */       getStartDate(this.startDate), this.validity * 24L * 60L * 60L, localCertificateExtensions);
/*      */     
/* 1760 */     if (this.verbose)
/*      */     {
/* 1762 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for"));
/*      */       
/*      */ 
/* 1765 */       Object[] arrayOfObject = { new Integer(paramInt), localPrivateKey.getAlgorithm(), arrayOfX509Certificate[0].getSigAlgName(), new Long(this.validity), localObject2 };
/*      */       
/*      */ 
/* 1768 */       System.err.println(localMessageFormat.format(arrayOfObject));
/*      */     }
/*      */     
/* 1771 */     if (this.keyPass == null) {
/* 1772 */       this.keyPass = promptForKeyPass(paramString1, null, this.storePass);
/*      */     }
/* 1774 */     checkWeak(rb.getString("the.generated.certificate"), arrayOfX509Certificate[0]);
/* 1775 */     this.keyStore.setKeyEntry(paramString1, localPrivateKey, this.keyPass, arrayOfX509Certificate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doCloneEntry(String paramString1, String paramString2, boolean paramBoolean)
/*      */     throws Exception
/*      */   {
/* 1787 */     if (paramString1 == null) {
/* 1788 */       paramString1 = "mykey";
/*      */     }
/*      */     
/* 1791 */     if (this.keyStore.containsAlias(paramString2))
/*      */     {
/* 1793 */       localObject1 = new MessageFormat(rb.getString("Destination.alias.dest.already.exists"));
/* 1794 */       localObject2 = new Object[] { paramString2 };
/* 1795 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */     
/* 1798 */     Object localObject1 = recoverEntry(this.keyStore, paramString1, this.storePass, this.keyPass);
/* 1799 */     Object localObject2 = (Entry)((Pair)localObject1).fst;
/* 1800 */     this.keyPass = ((char[])((Pair)localObject1).snd);
/*      */     
/* 1802 */     PasswordProtection localPasswordProtection = null;
/*      */     
/* 1804 */     if (this.keyPass != null) {
/* 1805 */       if ((!paramBoolean) || ("PKCS12".equalsIgnoreCase(this.storetype))) {
/* 1806 */         this.keyPassNew = this.keyPass;
/*      */       }
/* 1808 */       else if (this.keyPassNew == null) {
/* 1809 */         this.keyPassNew = promptForKeyPass(paramString2, paramString1, this.keyPass);
/*      */       }
/*      */       
/* 1812 */       localPasswordProtection = new PasswordProtection(this.keyPassNew);
/*      */     }
/* 1814 */     this.keyStore.setEntry(paramString2, (Entry)localObject2, localPasswordProtection);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doChangeKeyPasswd(String paramString)
/*      */     throws Exception
/*      */   {
/* 1823 */     if (paramString == null) {
/* 1824 */       paramString = "mykey";
/*      */     }
/* 1826 */     Pair localPair = recoverKey(paramString, this.storePass, this.keyPass);
/* 1827 */     Key localKey = (Key)localPair.fst;
/* 1828 */     if (this.keyPass == null) {
/* 1829 */       this.keyPass = ((char[])localPair.snd);
/*      */     }
/*      */     
/* 1832 */     if (this.keyPassNew == null)
/*      */     {
/* 1834 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("key.password.for.alias."));
/* 1835 */       Object[] arrayOfObject = { paramString };
/* 1836 */       this.keyPassNew = getNewPasswd(localMessageFormat.format(arrayOfObject), this.keyPass);
/*      */     }
/* 1838 */     this.keyStore.setKeyEntry(paramString, localKey, this.keyPassNew, this.keyStore
/* 1839 */       .getCertificateChain(paramString));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doImportIdentityDatabase(InputStream paramInputStream)
/*      */     throws Exception
/*      */   {
/* 1850 */     System.err.println(rb
/* 1851 */       .getString("No.entries.from.identity.database.added"));
/*      */   }
/*      */   
/*      */ 
/*      */   private void doPrintEntry(String paramString1, String paramString2, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/*      */     Object localObject1;
/*      */     Object[] arrayOfObject1;
/* 1860 */     if (!this.keyStore.containsAlias(paramString2))
/*      */     {
/* 1862 */       localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/* 1863 */       arrayOfObject1 = new Object[] { paramString2 };
/* 1864 */       throw new Exception(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */     }
/*      */     Object localObject2;
/* 1867 */     if ((this.verbose) || (this.rfc) || (this.debug))
/*      */     {
/* 1869 */       localObject1 = new MessageFormat(rb.getString("Alias.name.alias"));
/* 1870 */       arrayOfObject1 = new Object[] { paramString2 };
/* 1871 */       paramPrintStream.println(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */       
/* 1873 */       if (!this.token)
/*      */       {
/* 1875 */         localObject1 = new MessageFormat(rb.getString("Creation.date.keyStore.getCreationDate.alias."));
/* 1876 */         localObject2 = new Object[] { this.keyStore.getCreationDate(paramString2) };
/* 1877 */         paramPrintStream.println(((MessageFormat)localObject1).format(localObject2));
/*      */       }
/*      */     }
/* 1880 */     else if (!this.token)
/*      */     {
/* 1882 */       localObject1 = new MessageFormat(rb.getString("alias.keyStore.getCreationDate.alias."));
/* 1883 */       arrayOfObject1 = new Object[] { paramString2, this.keyStore.getCreationDate(paramString2) };
/* 1884 */       paramPrintStream.print(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */     }
/*      */     else {
/* 1887 */       localObject1 = new MessageFormat(rb.getString("alias."));
/* 1888 */       arrayOfObject1 = new Object[] { paramString2 };
/* 1889 */       paramPrintStream.print(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */     }
/*      */     
/*      */ 
/* 1893 */     if (this.keyStore.entryInstanceOf(paramString2, SecretKeyEntry.class)) {
/* 1894 */       if ((this.verbose) || (this.rfc) || (this.debug)) {
/* 1895 */         localObject1 = new Object[] { "SecretKeyEntry" };
/* 1896 */         paramPrintStream.println(new MessageFormat(rb
/* 1897 */           .getString("Entry.type.type.")).format(localObject1));
/*      */       } else {
/* 1899 */         paramPrintStream.println("SecretKeyEntry, ");
/*      */       }
/* 1901 */     } else if (this.keyStore.entryInstanceOf(paramString2, PrivateKeyEntry.class)) {
/* 1902 */       if ((this.verbose) || (this.rfc) || (this.debug)) {
/* 1903 */         localObject1 = new Object[] { "PrivateKeyEntry" };
/* 1904 */         paramPrintStream.println(new MessageFormat(rb
/* 1905 */           .getString("Entry.type.type.")).format(localObject1));
/*      */       } else {
/* 1907 */         paramPrintStream.println("PrivateKeyEntry, ");
/*      */       }
/*      */       
/*      */ 
/* 1911 */       localObject1 = this.keyStore.getCertificateChain(paramString2);
/* 1912 */       if (localObject1 != null) {
/* 1913 */         if ((this.verbose) || (this.rfc) || (this.debug)) {
/* 1914 */           paramPrintStream.println(rb
/* 1915 */             .getString("Certificate.chain.length.") + localObject1.length);
/* 1916 */           for (int i = 0; i < localObject1.length; i++)
/*      */           {
/* 1918 */             localObject2 = new MessageFormat(rb.getString("Certificate.i.1."));
/* 1919 */             Object[] arrayOfObject3 = { new Integer(i + 1) };
/* 1920 */             paramPrintStream.println(((MessageFormat)localObject2).format(arrayOfObject3));
/* 1921 */             if ((this.verbose) && ((localObject1[i] instanceof X509Certificate))) {
/* 1922 */               printX509Cert((X509Certificate)localObject1[i], paramPrintStream);
/* 1923 */             } else if (this.debug) {
/* 1924 */               paramPrintStream.println(localObject1[i].toString());
/*      */             } else {
/* 1926 */               dumpCert(localObject1[i], paramPrintStream);
/*      */             }
/* 1928 */             checkWeak(paramString1, localObject1[i]);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1933 */           paramPrintStream.println(rb.getString("Certificate.fingerprint.SHA1.") + 
/* 1934 */             getCertFingerPrint("SHA1", localObject1[0]));
/* 1935 */           checkWeak(paramString1, localObject1[0]);
/*      */         }
/*      */       }
/* 1938 */     } else if (this.keyStore.entryInstanceOf(paramString2, TrustedCertificateEntry.class))
/*      */     {
/*      */ 
/* 1941 */       localObject1 = this.keyStore.getCertificate(paramString2);
/* 1942 */       Object[] arrayOfObject2 = { "trustedCertEntry" };
/*      */       
/* 1944 */       localObject2 = new MessageFormat(rb.getString("Entry.type.type.")).format(arrayOfObject2) + "\n";
/* 1945 */       if ((this.verbose) && ((localObject1 instanceof X509Certificate))) {
/* 1946 */         paramPrintStream.println((String)localObject2);
/* 1947 */         printX509Cert((X509Certificate)localObject1, paramPrintStream);
/* 1948 */       } else if (this.rfc) {
/* 1949 */         paramPrintStream.println((String)localObject2);
/* 1950 */         dumpCert((Certificate)localObject1, paramPrintStream);
/* 1951 */       } else if (this.debug) {
/* 1952 */         paramPrintStream.println(((Certificate)localObject1).toString());
/*      */       } else {
/* 1954 */         paramPrintStream.println("trustedCertEntry, ");
/* 1955 */         paramPrintStream.println(rb.getString("Certificate.fingerprint.SHA1.") + 
/* 1956 */           getCertFingerPrint("SHA1", (Certificate)localObject1));
/*      */       }
/* 1958 */       checkWeak(paramString1, (Certificate)localObject1);
/*      */     } else {
/* 1960 */       paramPrintStream.println(rb.getString("Unknown.Entry.Type"));
/*      */     }
/*      */   }
/*      */   
/*      */   boolean inplaceImportCheck() throws Exception {
/* 1965 */     if (("PKCS11".equalsIgnoreCase(this.srcstoretype)) || 
/* 1966 */       (KeyStoreUtil.isWindowsKeyStore(this.srcstoretype))) {
/* 1967 */       return false;
/*      */     }
/*      */     
/* 1970 */     if (this.srcksfname != null) {
/* 1971 */       File localFile = new File(this.srcksfname);
/* 1972 */       if ((localFile.exists()) && (localFile.length() == 0L))
/*      */       {
/* 1974 */         throw new Exception(rb.getString("Source.keystore.file.exists.but.is.empty.") + this.srcksfname);
/*      */       }
/*      */       
/*      */ 
/* 1978 */       if (localFile.getCanonicalFile().equals(new File(this.ksfname).getCanonicalFile())) {
/* 1979 */         return true;
/*      */       }
/*      */       
/*      */ 
/* 1983 */       System.err.println(String.format(rb.getString("importing.keystore.status"), new Object[] { this.srcksfname, this.ksfname }));
/*      */       
/* 1985 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1989 */     throw new Exception(rb.getString("Please.specify.srckeystore"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   KeyStore loadSourceKeyStore()
/*      */     throws Exception
/*      */   {
/* 1999 */     FileInputStream localFileInputStream = null;
/* 2000 */     File localFile = null;
/*      */     
/* 2002 */     if (("PKCS11".equalsIgnoreCase(this.srcstoretype)) || 
/* 2003 */       (KeyStoreUtil.isWindowsKeyStore(this.srcstoretype))) {
/* 2004 */       if (!"NONE".equals(this.srcksfname)) {
/* 2005 */         System.err.println(MessageFormat.format(rb
/* 2006 */           .getString(".keystore.must.be.NONE.if.storetype.is.{0}"), new Object[] { this.srcstoretype }));
/* 2007 */         System.err.println();
/* 2008 */         tinyHelp();
/*      */       }
/*      */     } else {
/* 2011 */       localFile = new File(this.srcksfname);
/* 2012 */       localFileInputStream = new FileInputStream(localFile);
/*      */     }
/*      */     KeyStore localKeyStore;
/*      */     try
/*      */     {
/* 2017 */       if (this.srcProviderName == null) {
/* 2018 */         localKeyStore = KeyStore.getInstance(this.srcstoretype);
/*      */       } else {
/* 2020 */         localKeyStore = KeyStore.getInstance(this.srcstoretype, this.srcProviderName);
/*      */       }
/*      */       
/* 2023 */       if ((this.srcstorePass == null) && (!this.srcprotectedPath))
/*      */       {
/* 2025 */         if (!KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)) {
/* 2026 */           System.err.print(rb.getString("Enter.source.keystore.password."));
/* 2027 */           System.err.flush();
/* 2028 */           this.srcstorePass = Password.readPassword(System.in);
/* 2029 */           this.passwords.add(this.srcstorePass);
/*      */         }
/*      */       }
/*      */       
/* 2033 */       if (("PKCS12".equalsIgnoreCase(this.srcstoretype)) && 
/* 2034 */         (this.srckeyPass != null) && (this.srcstorePass != null) && 
/* 2035 */         (!Arrays.equals(this.srcstorePass, this.srckeyPass))) {
/* 2036 */         MessageFormat localMessageFormat = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
/*      */         
/* 2038 */         Object[] arrayOfObject = { "-srckeypass" };
/* 2039 */         System.err.println(localMessageFormat.format(arrayOfObject));
/* 2040 */         this.srckeyPass = this.srcstorePass;
/*      */       }
/*      */       
/*      */ 
/* 2044 */       localKeyStore.load(localFileInputStream, this.srcstorePass);
/*      */     } finally {
/* 2046 */       if (localFileInputStream != null) {
/* 2047 */         localFileInputStream.close();
/*      */       }
/*      */     }
/*      */     
/* 2051 */     if ((this.srcstorePass == null) && 
/* 2052 */       (!KeyStoreUtil.isWindowsKeyStore(this.srcstoretype)))
/*      */     {
/*      */ 
/* 2055 */       System.err.println();
/* 2056 */       System.err.println(rb
/* 2057 */         .getString(".WARNING.WARNING.WARNING."));
/* 2058 */       System.err.println(rb
/* 2059 */         .getString(".The.integrity.of.the.information.stored.in.the.srckeystore."));
/* 2060 */       System.err.println(rb
/* 2061 */         .getString(".WARNING.WARNING.WARNING."));
/* 2062 */       System.err.println();
/*      */     }
/*      */     
/* 2065 */     return localKeyStore;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doImportKeyStore(KeyStore paramKeyStore)
/*      */     throws Exception
/*      */   {
/* 2075 */     if (this.alias != null) {
/* 2076 */       doImportKeyStoreSingle(paramKeyStore, this.alias);
/*      */     } else {
/* 2078 */       if ((this.dest != null) || (this.srckeyPass != null)) {
/* 2079 */         throw new Exception(rb.getString("if.alias.not.specified.destalias.and.srckeypass.must.not.be.specified"));
/*      */       }
/*      */       
/* 2082 */       doImportKeyStoreAll(paramKeyStore);
/*      */     }
/*      */     
/* 2085 */     if (this.inplaceImport)
/*      */     {
/*      */ 
/* 2088 */       for (int i = 1;; i++) {
/* 2089 */         this.inplaceBackupName = (this.srcksfname + ".old" + (i == 1 ? "" : Integer.valueOf(i)));
/* 2090 */         File localFile = new File(this.inplaceBackupName);
/* 2091 */         if (!localFile.exists()) {
/* 2092 */           java.nio.file.Files.copy(java.nio.file.Paths.get(this.srcksfname, new String[0]), localFile.toPath(), new java.nio.file.CopyOption[0]);
/* 2093 */           break;
/*      */         }
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
/*      */   private int doImportKeyStoreSingle(KeyStore paramKeyStore, String paramString)
/*      */     throws Exception
/*      */   {
/* 2117 */     String str = this.dest == null ? paramString : this.dest;
/*      */     
/* 2119 */     if (this.keyStore.containsAlias(str)) {
/* 2120 */       localObject1 = new Object[] { paramString };
/* 2121 */       if (this.noprompt) {
/* 2122 */         System.err.println(new MessageFormat(rb.getString("Warning.Overwriting.existing.alias.alias.in.destination.keystore"))
/* 2123 */           .format(localObject1));
/*      */       } else {
/* 2125 */         localObject2 = getYesNoReply(new MessageFormat(rb.getString("Existing.entry.alias.alias.exists.overwrite.no."))
/* 2126 */           .format(localObject1));
/* 2127 */         if ("NO".equals(localObject2)) {
/* 2128 */           str = inputStringFromStdin(rb
/* 2129 */             .getString("Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry."));
/* 2130 */           if ("".equals(str)) {
/* 2131 */             System.err.println(new MessageFormat(rb.getString("Entry.for.alias.alias.not.imported."))
/* 2132 */               .format(localObject1));
/*      */             
/* 2134 */             return 0;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2140 */     Object localObject1 = recoverEntry(paramKeyStore, paramString, this.srcstorePass, this.srckeyPass);
/* 2141 */     Object localObject2 = (Entry)((Pair)localObject1).fst;
/*      */     
/* 2143 */     PasswordProtection localPasswordProtection = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2149 */     char[] arrayOfChar = null;
/* 2150 */     if (this.destKeyPass != null) {
/* 2151 */       arrayOfChar = this.destKeyPass;
/* 2152 */       localPasswordProtection = new PasswordProtection(this.destKeyPass);
/* 2153 */     } else if (((Pair)localObject1).snd != null) {
/* 2154 */       arrayOfChar = (char[])((Pair)localObject1).snd;
/* 2155 */       localPasswordProtection = new PasswordProtection((char[])((Pair)localObject1).snd);
/*      */     }
/*      */     try
/*      */     {
/* 2159 */       Certificate localCertificate = paramKeyStore.getCertificate(paramString);
/* 2160 */       if (localCertificate != null) {
/* 2161 */         checkWeak("<" + str + ">", localCertificate);
/*      */       }
/* 2163 */       this.keyStore.setEntry(str, (Entry)localObject2, localPasswordProtection);
/*      */       
/*      */ 
/* 2166 */       if (("PKCS12".equalsIgnoreCase(this.storetype)) && 
/* 2167 */         (arrayOfChar != null) && (!Arrays.equals(arrayOfChar, this.storePass))) {
/* 2168 */         throw new Exception(rb.getString("The.destination.pkcs12.keystore.has.different.storepass.and.keypass.Please.retry.with.destkeypass.specified."));
/*      */       }
/*      */       
/*      */ 
/* 2172 */       return 1;
/*      */     } catch (KeyStoreException localKeyStoreException) {
/* 2174 */       Object[] arrayOfObject = { paramString, localKeyStoreException.toString() };
/* 2175 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported."));
/*      */       
/* 2177 */       System.err.println(localMessageFormat.format(arrayOfObject)); }
/* 2178 */     return 2;
/*      */   }
/*      */   
/*      */   private void doImportKeyStoreAll(KeyStore paramKeyStore)
/*      */     throws Exception
/*      */   {
/* 2184 */     int i = 0;
/* 2185 */     int j = paramKeyStore.size();
/* 2186 */     Object localObject1 = paramKeyStore.aliases();
/* 2187 */     while (((Enumeration)localObject1).hasMoreElements()) {
/* 2188 */       localObject2 = (String)((Enumeration)localObject1).nextElement();
/* 2189 */       int k = doImportKeyStoreSingle(paramKeyStore, (String)localObject2);
/* 2190 */       Object localObject3; if (k == 1) {
/* 2191 */         i++;
/* 2192 */         localObject3 = new Object[] { localObject2 };
/* 2193 */         MessageFormat localMessageFormat = new MessageFormat(rb.getString("Entry.for.alias.alias.successfully.imported."));
/* 2194 */         System.err.println(localMessageFormat.format(localObject3));
/* 2195 */       } else if ((k == 2) && 
/* 2196 */         (!this.noprompt)) {
/* 2197 */         localObject3 = getYesNoReply("Do you want to quit the import process? [no]:  ");
/* 2198 */         if ("YES".equals(localObject3)) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2204 */     localObject1 = new Object[] { Integer.valueOf(i), Integer.valueOf(j - i) };
/* 2205 */     Object localObject2 = new MessageFormat(rb.getString("Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled"));
/*      */     
/* 2207 */     System.err.println(((MessageFormat)localObject2).format(localObject1));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doPrintEntries(PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 2216 */     paramPrintStream.println(rb.getString("Keystore.type.") + this.keyStore.getType());
/* 2217 */     paramPrintStream.println(rb.getString("Keystore.provider.") + this.keyStore
/* 2218 */       .getProvider().getName());
/* 2219 */     paramPrintStream.println();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2226 */     MessageFormat localMessageFormat = this.keyStore.size() == 1 ? new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entry")) : new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entries"));
/* 2227 */     Object[] arrayOfObject = { new Integer(this.keyStore.size()) };
/* 2228 */     paramPrintStream.println(localMessageFormat.format(arrayOfObject));
/* 2229 */     paramPrintStream.println();
/*      */     
/* 2231 */     Enumeration localEnumeration = this.keyStore.aliases();
/* 2232 */     while (localEnumeration.hasMoreElements()) {
/* 2233 */       String str = (String)localEnumeration.nextElement();
/* 2234 */       doPrintEntry("<" + str + ">", str, paramPrintStream);
/* 2235 */       if ((this.verbose) || (this.rfc)) {
/* 2236 */         paramPrintStream.println(rb.getString("NEWLINE"));
/* 2237 */         paramPrintStream.println(rb
/* 2238 */           .getString("STAR"));
/* 2239 */         paramPrintStream.println(rb
/* 2240 */           .getString("STARNN"));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static <T> Iterable<T> e2i(Enumeration<T> paramEnumeration) {
/* 2246 */     new Iterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/* 2249 */         new Iterator()
/*      */         {
/*      */           public boolean hasNext() {
/* 2252 */             return Main.1.this.val$e.hasMoreElements();
/*      */           }
/*      */           
/*      */           public T next() {
/* 2256 */             return (T)Main.1.this.val$e.nextElement();
/*      */           }
/*      */           
/* 2259 */           public void remove() { throw new UnsupportedOperationException("Not supported yet."); }
/*      */         };
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Collection<? extends CRL> loadCRLs(String paramString)
/*      */     throws Exception
/*      */   {
/* 2272 */     Object localObject1 = null;
/* 2273 */     URI localURI = null;
/* 2274 */     if (paramString == null) {
/* 2275 */       localObject1 = System.in;
/*      */     } else {
/*      */       try {
/* 2278 */         localURI = new URI(paramString);
/* 2279 */         if (!localURI.getScheme().equals("ldap"))
/*      */         {
/*      */ 
/* 2282 */           localObject1 = localURI.toURL().openStream();
/*      */         }
/*      */       } catch (Exception localException1) {
/*      */         try {
/* 2286 */           localObject1 = new FileInputStream(paramString);
/*      */         } catch (Exception localException2) {
/* 2288 */           if ((localURI == null) || (localURI.getScheme() == null)) {
/* 2289 */             throw localException2;
/*      */           }
/* 2291 */           throw localException1;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2296 */     if (localObject1 != null)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/* 2302 */         localObject2 = new ByteArrayOutputStream();
/* 2303 */         localObject3 = new byte['က'];
/*      */         for (;;) {
/* 2305 */           int i = ((InputStream)localObject1).read((byte[])localObject3);
/* 2306 */           if (i < 0) break;
/* 2307 */           ((ByteArrayOutputStream)localObject2).write((byte[])localObject3, 0, i);
/*      */         }
/* 2309 */         return CertificateFactory.getInstance("X509").generateCRLs(new java.io.ByteArrayInputStream(((ByteArrayOutputStream)localObject2)
/* 2310 */           .toByteArray()));
/*      */       } finally {
/* 2312 */         if (localObject1 != System.in) {
/* 2313 */           ((InputStream)localObject1).close();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2318 */     Object localObject2 = CertStoreHelper.getInstance("LDAP");
/* 2319 */     Object localObject3 = localURI.getPath();
/* 2320 */     if (((String)localObject3).charAt(0) == '/') localObject3 = ((String)localObject3).substring(1);
/* 2321 */     Object localObject4 = ((CertStoreHelper)localObject2).getCertStore(localURI);
/*      */     
/* 2323 */     X509CRLSelector localX509CRLSelector = ((CertStoreHelper)localObject2).wrap(new X509CRLSelector(), null, (String)localObject3);
/* 2324 */     return ((CertStore)localObject4).getCRLs(localX509CRLSelector);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List<CRL> readCRLsFromCert(X509Certificate paramX509Certificate)
/*      */     throws Exception
/*      */   {
/* 2334 */     ArrayList localArrayList = new ArrayList();
/*      */     
/* 2336 */     CRLDistributionPointsExtension localCRLDistributionPointsExtension = X509CertImpl.toImpl(paramX509Certificate).getCRLDistributionPointsExtension();
/* 2337 */     if (localCRLDistributionPointsExtension == null) { return localArrayList;
/*      */     }
/* 2339 */     List localList = localCRLDistributionPointsExtension.get("points");
/* 2340 */     for (DistributionPoint localDistributionPoint : localList) {
/* 2341 */       GeneralNames localGeneralNames = localDistributionPoint.getFullName();
/* 2342 */       if (localGeneralNames != null) {
/* 2343 */         for (GeneralName localGeneralName : localGeneralNames.names()) {
/* 2344 */           if (localGeneralName.getType() == 6) {
/* 2345 */             URIName localURIName = (URIName)localGeneralName.getName();
/* 2346 */             for (CRL localCRL : loadCRLs(localURIName.getName())) {
/* 2347 */               if ((localCRL instanceof X509CRL)) {
/* 2348 */                 localArrayList.add((X509CRL)localCRL);
/*      */               }
/*      */             }
/* 2351 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2356 */     return localArrayList;
/*      */   }
/*      */   
/*      */   private static String verifyCRL(KeyStore paramKeyStore, CRL paramCRL) throws Exception
/*      */   {
/* 2361 */     X509CRLImpl localX509CRLImpl = (X509CRLImpl)paramCRL;
/* 2362 */     X500Principal localX500Principal = localX509CRLImpl.getIssuerX500Principal();
/* 2363 */     for (String str : e2i(paramKeyStore.aliases())) {
/* 2364 */       Certificate localCertificate = paramKeyStore.getCertificate(str);
/* 2365 */       if ((localCertificate instanceof X509Certificate)) {
/* 2366 */         X509Certificate localX509Certificate = (X509Certificate)localCertificate;
/* 2367 */         if (localX509Certificate.getSubjectX500Principal().equals(localX500Principal)) {
/*      */           try {
/* 2369 */             ((X509CRLImpl)paramCRL).verify(localCertificate.getPublicKey());
/* 2370 */             return str;
/*      */           }
/*      */           catch (Exception localException) {}
/*      */         }
/*      */       }
/*      */     }
/* 2376 */     return null;
/*      */   }
/*      */   
/*      */   private void doPrintCRL(String paramString, PrintStream paramPrintStream) throws Exception
/*      */   {
/* 2381 */     for (CRL localCRL : loadCRLs(paramString)) {
/* 2382 */       printCRL(localCRL, paramPrintStream);
/* 2383 */       String str = null;
/* 2384 */       Certificate localCertificate = null;
/* 2385 */       if (this.caks != null) {
/* 2386 */         str = verifyCRL(this.caks, localCRL);
/* 2387 */         if (str != null) {
/* 2388 */           localCertificate = this.caks.getCertificate(str);
/* 2389 */           paramPrintStream.printf(rb.getString("verified.by.s.in.s.weak"), new Object[] { str, "cacerts", 
/*      */           
/*      */ 
/*      */ 
/* 2393 */             withWeak(localCertificate.getPublicKey()) });
/* 2394 */           paramPrintStream.println();
/*      */         }
/*      */       }
/* 2397 */       if ((str == null) && (this.keyStore != null)) {
/* 2398 */         str = verifyCRL(this.keyStore, localCRL);
/* 2399 */         if (str != null) {
/* 2400 */           localCertificate = this.keyStore.getCertificate(str);
/* 2401 */           paramPrintStream.printf(rb.getString("verified.by.s.in.s.weak"), new Object[] { str, "keystore", 
/*      */           
/*      */ 
/*      */ 
/* 2405 */             withWeak(localCertificate.getPublicKey()) });
/* 2406 */           paramPrintStream.println();
/*      */         }
/*      */       }
/* 2409 */       if (str == null) {
/* 2410 */         paramPrintStream.println(rb
/* 2411 */           .getString("STAR"));
/* 2412 */         paramPrintStream.println(rb
/* 2413 */           .getString("warning.not.verified.make.sure.keystore.is.correct"));
/* 2414 */         paramPrintStream.println(rb
/* 2415 */           .getString("STARNN"));
/*      */       }
/* 2417 */       checkWeak(rb.getString("the.crl"), localCRL, localCertificate == null ? null : localCertificate.getPublicKey());
/*      */     }
/*      */   }
/*      */   
/*      */   private void printCRL(CRL paramCRL, PrintStream paramPrintStream) throws Exception
/*      */   {
/* 2423 */     X509CRL localX509CRL = (X509CRL)paramCRL;
/* 2424 */     if (this.rfc) {
/* 2425 */       paramPrintStream.println("-----BEGIN X509 CRL-----");
/* 2426 */       paramPrintStream.println(Base64.getMimeEncoder(64, CRLF).encodeToString(localX509CRL.getEncoded()));
/* 2427 */       paramPrintStream.println("-----END X509 CRL-----");
/*      */     } else {
/*      */       String str;
/* 2430 */       if ((paramCRL instanceof X509CRLImpl)) {
/* 2431 */         X509CRLImpl localX509CRLImpl = (X509CRLImpl)paramCRL;
/* 2432 */         str = localX509CRLImpl.toStringWithAlgName(withWeak("" + localX509CRLImpl.getSigAlgId()));
/*      */       } else {
/* 2434 */         str = paramCRL.toString();
/*      */       }
/* 2436 */       paramPrintStream.println(str);
/*      */     }
/*      */   }
/*      */   
/*      */   private void doPrintCertReq(InputStream paramInputStream, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 2443 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
/* 2444 */     StringBuffer localStringBuffer = new StringBuffer();
/* 2445 */     int i = 0;
/*      */     for (;;) {
/* 2447 */       localObject1 = localBufferedReader.readLine();
/* 2448 */       if (localObject1 == null) break;
/* 2449 */       if (i == 0) {
/* 2450 */         if (((String)localObject1).startsWith("-----")) {
/* 2451 */           i = 1;
/*      */         }
/*      */       } else {
/* 2454 */         if (((String)localObject1).startsWith("-----")) {
/*      */           break;
/*      */         }
/* 2457 */         localStringBuffer.append((String)localObject1);
/*      */       }
/*      */     }
/* 2460 */     Object localObject1 = new PKCS10(Pem.decode(new String(localStringBuffer)));
/*      */     
/* 2462 */     PublicKey localPublicKey = ((PKCS10)localObject1).getSubjectPublicKeyInfo();
/* 2463 */     paramPrintStream.printf(rb.getString("PKCS.10.with.weak"), new Object[] {((PKCS10)localObject1)
/* 2464 */       .getSubjectName(), localPublicKey
/* 2465 */       .getFormat(), 
/* 2466 */       withWeak(localPublicKey), 
/* 2467 */       withWeak(((PKCS10)localObject1).getSigAlg()) });
/* 2468 */     for (PKCS10Attribute localPKCS10Attribute : ((PKCS10)localObject1).getAttributes().getAttributes()) {
/* 2469 */       ObjectIdentifier localObjectIdentifier = localPKCS10Attribute.getAttributeId();
/* 2470 */       Object localObject2; if (localObjectIdentifier.equals(PKCS9Attribute.EXTENSION_REQUEST_OID)) {
/* 2471 */         localObject2 = (CertificateExtensions)localPKCS10Attribute.getAttributeValue();
/* 2472 */         if (localObject2 != null) {
/* 2473 */           printExtensions(rb.getString("Extension.Request."), (CertificateExtensions)localObject2, paramPrintStream);
/*      */         }
/*      */       } else {
/* 2476 */         paramPrintStream.println("Attribute: " + localPKCS10Attribute.getAttributeId());
/*      */         
/*      */ 
/* 2479 */         localObject2 = new PKCS9Attribute(localPKCS10Attribute.getAttributeId(), localPKCS10Attribute.getAttributeValue());
/* 2480 */         paramPrintStream.print(((PKCS9Attribute)localObject2).getName() + ": ");
/* 2481 */         Object localObject3 = localPKCS10Attribute.getAttributeValue();
/* 2482 */         paramPrintStream.println((localObject3 instanceof String[]) ? 
/* 2483 */           Arrays.toString((String[])localObject3) : localObject3);
/*      */       }
/*      */     }
/*      */     
/* 2487 */     if (this.debug) {
/* 2488 */       paramPrintStream.println(localObject1);
/*      */     }
/* 2490 */     checkWeak(rb.getString("the.certificate.request"), (PKCS10)localObject1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void printCertFromStream(InputStream paramInputStream, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 2500 */     Collection localCollection = null;
/*      */     try {
/* 2502 */       localCollection = this.cf.generateCertificates(paramInputStream);
/*      */     } catch (CertificateException localCertificateException) {
/* 2504 */       throw new Exception(rb.getString("Failed.to.parse.input"), localCertificateException);
/*      */     }
/* 2506 */     if (localCollection.isEmpty()) {
/* 2507 */       throw new Exception(rb.getString("Empty.input"));
/*      */     }
/* 2509 */     Certificate[] arrayOfCertificate = (Certificate[])localCollection.toArray(new Certificate[localCollection.size()]);
/* 2510 */     for (int i = 0; i < arrayOfCertificate.length; i++) {
/* 2511 */       X509Certificate localX509Certificate = null;
/*      */       try {
/* 2513 */         localX509Certificate = (X509Certificate)arrayOfCertificate[i];
/*      */       } catch (ClassCastException localClassCastException) {
/* 2515 */         throw new Exception(rb.getString("Not.X.509.certificate"));
/*      */       }
/* 2517 */       if (arrayOfCertificate.length > 1)
/*      */       {
/* 2519 */         MessageFormat localMessageFormat = new MessageFormat(rb.getString("Certificate.i.1."));
/* 2520 */         Object[] arrayOfObject = { new Integer(i + 1) };
/* 2521 */         paramPrintStream.println(localMessageFormat.format(arrayOfObject));
/*      */       }
/* 2523 */       if (this.rfc) {
/* 2524 */         dumpCert(localX509Certificate, paramPrintStream);
/*      */       } else
/* 2526 */         printX509Cert(localX509Certificate, paramPrintStream);
/* 2527 */       if (i < arrayOfCertificate.length - 1) {
/* 2528 */         paramPrintStream.println();
/*      */       }
/* 2530 */       checkWeak(oneInMany(rb.getString("the.certificate"), i, arrayOfCertificate.length), localX509Certificate);
/*      */     }
/*      */   }
/*      */   
/*      */   private static String oneInMany(String paramString, int paramInt1, int paramInt2) {
/* 2535 */     if (paramInt2 == 1) {
/* 2536 */       return paramString;
/*      */     }
/* 2538 */     return String.format(rb.getString("one.in.many"), new Object[] { paramString, Integer.valueOf(paramInt1 + 1), Integer.valueOf(paramInt2) }); }
/*      */   
/*      */   private void doPrintCert(PrintStream paramPrintStream) throws Exception { Object localObject1;
/*      */     Object localObject2;
/*      */     Object localObject3;
/* 2543 */     Object localObject4; if (this.jarfile != null) {
/* 2544 */       localObject1 = new JarFile(this.jarfile, true);
/* 2545 */       localObject2 = ((JarFile)localObject1).entries();
/* 2546 */       localObject3 = new HashSet();
/* 2547 */       byte[] arrayOfByte = new byte[' '];
/* 2548 */       int j = 0;
/* 2549 */       while (((Enumeration)localObject2).hasMoreElements()) {
/* 2550 */         localObject4 = (JarEntry)((Enumeration)localObject2).nextElement();
/* 2551 */         Object localObject5 = ((JarFile)localObject1).getInputStream((java.util.zip.ZipEntry)localObject4);Object localObject6 = null;
/* 2552 */         try { while (((InputStream)localObject5).read(arrayOfByte) != -1) {}
/*      */         }
/*      */         catch (Throwable localThrowable4)
/*      */         {
/* 2551 */           localObject6 = localThrowable4;throw localThrowable4;
/*      */ 
/*      */         }
/*      */         finally
/*      */         {
/*      */ 
/* 2557 */           if (localObject5 != null) if (localObject6 != null) try { ((InputStream)localObject5).close(); } catch (Throwable localThrowable5) { ((Throwable)localObject6).addSuppressed(localThrowable5); } else ((InputStream)localObject5).close(); }
/* 2558 */         localObject5 = ((JarEntry)localObject4).getCodeSigners();
/* 2559 */         if (localObject5 != null) { List localList;
/* 2560 */           int n; Object localObject10; Object localObject11; for (Object localObject8 : localObject5) {
/* 2561 */             if (!((Set)localObject3).contains(localObject8)) {
/* 2562 */               ((Set)localObject3).add(localObject8);
/* 2563 */               paramPrintStream.printf(rb.getString("Signer.d."), new Object[] { Integer.valueOf(++j) });
/* 2564 */               paramPrintStream.println();
/* 2565 */               paramPrintStream.println();
/* 2566 */               paramPrintStream.println(rb.getString("Signature."));
/* 2567 */               paramPrintStream.println();
/*      */               
/*      */ 
/* 2570 */               localList = ((CodeSigner)localObject8).getSignerCertPath().getCertificates();
/* 2571 */               n = 0;
/* 2572 */               for (Object localObject9 = localList.iterator(); ((Iterator)localObject9).hasNext();) { localObject10 = (Certificate)((Iterator)localObject9).next();
/* 2573 */                 localObject11 = (X509Certificate)localObject10;
/* 2574 */                 if (this.rfc) {
/* 2575 */                   paramPrintStream.println(rb.getString("Certificate.owner.") + ((X509Certificate)localObject11).getSubjectDN() + "\n");
/* 2576 */                   dumpCert((Certificate)localObject11, paramPrintStream);
/*      */                 } else {
/* 2578 */                   printX509Cert((X509Certificate)localObject11, paramPrintStream);
/*      */                 }
/* 2580 */                 paramPrintStream.println();
/* 2581 */                 checkWeak(oneInMany(rb.getString("the.certificate"), n++, localList.size()), (Certificate)localObject11);
/*      */               }
/* 2583 */               localObject9 = ((CodeSigner)localObject8).getTimestamp();
/* 2584 */               if (localObject9 != null) {
/* 2585 */                 paramPrintStream.println(rb.getString("Timestamp."));
/* 2586 */                 paramPrintStream.println();
/* 2587 */                 localList = ((Timestamp)localObject9).getSignerCertPath().getCertificates();
/* 2588 */                 n = 0;
/* 2589 */                 for (localObject10 = localList.iterator(); ((Iterator)localObject10).hasNext();) { localObject11 = (Certificate)((Iterator)localObject10).next();
/* 2590 */                   X509Certificate localX509Certificate = (X509Certificate)localObject11;
/* 2591 */                   if (this.rfc) {
/* 2592 */                     paramPrintStream.println(rb.getString("Certificate.owner.") + localX509Certificate.getSubjectDN() + "\n");
/* 2593 */                     dumpCert(localX509Certificate, paramPrintStream);
/*      */                   } else {
/* 2595 */                     printX509Cert(localX509Certificate, paramPrintStream);
/*      */                   }
/* 2597 */                   paramPrintStream.println();
/* 2598 */                   checkWeak(oneInMany(rb.getString("the.tsa.certificate"), n++, localList.size()), localX509Certificate);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2605 */       ((JarFile)localObject1).close();
/* 2606 */       if (((Set)localObject3).isEmpty())
/* 2607 */         paramPrintStream.println(rb.getString("Not.a.signed.jar.file")); } else { int i;
/*      */       Iterator localIterator;
/* 2609 */       if (this.sslserver != null)
/*      */       {
/* 2611 */         localObject1 = CertStoreHelper.getInstance("SSLServer");
/* 2612 */         localObject2 = ((CertStoreHelper)localObject1).getCertStore(new URI("https://" + this.sslserver));
/*      */         try
/*      */         {
/* 2615 */           localObject3 = ((CertStore)localObject2).getCertificates(null);
/* 2616 */           if (((Collection)localObject3).isEmpty())
/*      */           {
/*      */ 
/* 2619 */             throw new Exception(rb.getString("No.certificate.from.the.SSL.server"));
/*      */           }
/*      */         }
/*      */         catch (CertStoreException localCertStoreException) {
/* 2623 */           if ((localCertStoreException.getCause() instanceof IOException))
/*      */           {
/*      */ 
/* 2626 */             throw new Exception(rb.getString("No.certificate.from.the.SSL.server"), localCertStoreException.getCause());
/*      */           }
/* 2628 */           throw localCertStoreException;
/*      */         }
/*      */         
/*      */ 
/* 2632 */         i = 0;
/* 2633 */         for (localIterator = ((Collection)localObject3).iterator(); localIterator.hasNext();) { localObject4 = (Certificate)localIterator.next();
/*      */           try {
/* 2635 */             if (this.rfc) {
/* 2636 */               dumpCert((Certificate)localObject4, paramPrintStream);
/*      */             } else {
/* 2638 */               paramPrintStream.println("Certificate #" + i++);
/* 2639 */               paramPrintStream.println("====================================");
/* 2640 */               printX509Cert((X509Certificate)localObject4, paramPrintStream);
/* 2641 */               paramPrintStream.println();
/*      */             }
/* 2643 */             checkWeak(oneInMany(rb.getString("the.certificate"), i, ((Collection)localObject3).size()), (Certificate)localObject4);
/*      */           } catch (Exception localException) {
/* 2645 */             if (this.debug) {
/* 2646 */               localException.printStackTrace();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2651 */       else if (this.filename != null) {
/* 2652 */         localObject1 = new FileInputStream(this.filename);localObject2 = null;
/* 2653 */         try { printCertFromStream((InputStream)localObject1, paramPrintStream);
/*      */         }
/*      */         catch (Throwable localThrowable2)
/*      */         {
/* 2652 */           localObject2 = localThrowable2;throw localThrowable2;
/*      */         } finally {
/* 2654 */           if (localObject1 != null) if (localObject2 != null) try { ((FileInputStream)localObject1).close(); } catch (Throwable localThrowable6) { ((Throwable)localObject2).addSuppressed(localThrowable6); } else ((FileInputStream)localObject1).close();
/*      */         }
/* 2656 */       } else { printCertFromStream(System.in, paramPrintStream);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doSelfCert(String paramString1, String paramString2, String paramString3)
/*      */     throws Exception
/*      */   {
/* 2667 */     if (paramString1 == null) {
/* 2668 */       paramString1 = "mykey";
/*      */     }
/*      */     
/* 2671 */     Pair localPair = recoverKey(paramString1, this.storePass, this.keyPass);
/* 2672 */     PrivateKey localPrivateKey = (PrivateKey)localPair.fst;
/* 2673 */     if (this.keyPass == null) {
/* 2674 */       this.keyPass = ((char[])localPair.snd);
/*      */     }
/*      */     
/* 2677 */     if (paramString3 == null) {
/* 2678 */       paramString3 = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
/*      */     }
/*      */     
/*      */ 
/* 2682 */     Certificate localCertificate = this.keyStore.getCertificate(paramString1);
/* 2683 */     if (localCertificate == null)
/*      */     {
/* 2685 */       localObject1 = new MessageFormat(rb.getString("alias.has.no.public.key"));
/* 2686 */       localObject2 = new Object[] { paramString1 };
/* 2687 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/* 2689 */     if (!(localCertificate instanceof X509Certificate))
/*      */     {
/* 2691 */       localObject1 = new MessageFormat(rb.getString("alias.has.no.X.509.certificate"));
/* 2692 */       localObject2 = new Object[] { paramString1 };
/* 2693 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2698 */     Object localObject1 = localCertificate.getEncoded();
/* 2699 */     Object localObject2 = new X509CertImpl((byte[])localObject1);
/* 2700 */     X509CertInfo localX509CertInfo = (X509CertInfo)((X509CertImpl)localObject2).get("x509.info");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2705 */     Date localDate1 = getStartDate(this.startDate);
/* 2706 */     Date localDate2 = new Date();
/* 2707 */     localDate2.setTime(localDate1.getTime() + this.validity * 1000L * 24L * 60L * 60L);
/* 2708 */     CertificateValidity localCertificateValidity = new CertificateValidity(localDate1, localDate2);
/*      */     
/* 2710 */     localX509CertInfo.set("validity", localCertificateValidity);
/*      */     
/*      */ 
/* 2713 */     localX509CertInfo.set("serialNumber", new sun.security.x509.CertificateSerialNumber(new Random()
/* 2714 */       .nextInt() & 0x7FFFFFFF));
/*      */     
/*      */     X500Name localX500Name;
/*      */     
/* 2718 */     if (paramString2 == null)
/*      */     {
/* 2720 */       localX500Name = (X500Name)localX509CertInfo.get("subject.dname");
/*      */     }
/*      */     else
/*      */     {
/* 2724 */       localX500Name = new X500Name(paramString2);
/* 2725 */       localX509CertInfo.set("subject.dname", localX500Name);
/*      */     }
/*      */     
/*      */ 
/* 2729 */     localX509CertInfo.set("issuer.dname", localX500Name);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2736 */     X509CertImpl localX509CertImpl = new X509CertImpl(localX509CertInfo);
/* 2737 */     localX509CertImpl.sign(localPrivateKey, paramString3);
/* 2738 */     AlgorithmId localAlgorithmId = (AlgorithmId)localX509CertImpl.get("x509.algorithm");
/* 2739 */     localX509CertInfo.set("algorithmID.algorithm", localAlgorithmId);
/*      */     
/*      */ 
/* 2742 */     localX509CertInfo.set("version", new CertificateVersion(2));
/*      */     
/*      */ 
/* 2745 */     CertificateExtensions localCertificateExtensions = createV3Extensions(null, 
/*      */     
/* 2747 */       (CertificateExtensions)localX509CertInfo.get("extensions"), this.v3ext, localCertificate
/*      */       
/* 2749 */       .getPublicKey(), null);
/*      */     
/* 2751 */     localX509CertInfo.set("extensions", localCertificateExtensions);
/*      */     
/* 2753 */     localX509CertImpl = new X509CertImpl(localX509CertInfo);
/* 2754 */     localX509CertImpl.sign(localPrivateKey, paramString3);
/*      */     
/*      */ 
/* 2757 */     this.keyStore.setKeyEntry(paramString1, localPrivateKey, this.keyPass != null ? this.keyPass : this.storePass, new Certificate[] { localX509CertImpl });
/*      */     
/*      */ 
/*      */ 
/* 2761 */     if (this.verbose) {
/* 2762 */       System.err.println(rb.getString("New.certificate.self.signed."));
/* 2763 */       System.err.print(localX509CertImpl.toString());
/* 2764 */       System.err.println();
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
/*      */   private boolean installReply(String paramString, InputStream paramInputStream)
/*      */     throws Exception
/*      */   {
/* 2785 */     if (paramString == null) {
/* 2786 */       paramString = "mykey";
/*      */     }
/*      */     
/* 2789 */     Pair localPair = recoverKey(paramString, this.storePass, this.keyPass);
/* 2790 */     PrivateKey localPrivateKey = (PrivateKey)localPair.fst;
/* 2791 */     if (this.keyPass == null) {
/* 2792 */       this.keyPass = ((char[])localPair.snd);
/*      */     }
/*      */     
/* 2795 */     Certificate localCertificate = this.keyStore.getCertificate(paramString);
/* 2796 */     if (localCertificate == null)
/*      */     {
/* 2798 */       localObject1 = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
/* 2799 */       localObject2 = new Object[] { paramString };
/* 2800 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */     
/*      */ 
/* 2804 */     Object localObject1 = this.cf.generateCertificates(paramInputStream);
/* 2805 */     if (((Collection)localObject1).isEmpty()) {
/* 2806 */       throw new Exception(rb.getString("Reply.has.no.certificates"));
/*      */     }
/* 2808 */     Object localObject2 = (Certificate[])((Collection)localObject1).toArray(new Certificate[((Collection)localObject1).size()]);
/*      */     Certificate[] arrayOfCertificate;
/* 2810 */     if (localObject2.length == 1)
/*      */     {
/* 2812 */       arrayOfCertificate = establishCertChain(localCertificate, localObject2[0]);
/*      */     }
/*      */     else {
/* 2815 */       arrayOfCertificate = validateReply(paramString, localCertificate, (Certificate[])localObject2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2820 */     if (arrayOfCertificate != null) {
/* 2821 */       this.keyStore.setKeyEntry(paramString, localPrivateKey, this.keyPass != null ? this.keyPass : this.storePass, arrayOfCertificate);
/*      */       
/*      */ 
/* 2824 */       return true;
/*      */     }
/* 2826 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean addTrustedCert(String paramString, InputStream paramInputStream)
/*      */     throws Exception
/*      */   {
/* 2838 */     if (paramString == null) {
/* 2839 */       throw new Exception(rb.getString("Must.specify.alias"));
/*      */     }
/* 2841 */     if (this.keyStore.containsAlias(paramString))
/*      */     {
/* 2843 */       localObject1 = new MessageFormat(rb.getString("Certificate.not.imported.alias.alias.already.exists"));
/* 2844 */       Object[] arrayOfObject1 = { paramString };
/* 2845 */       throw new Exception(((MessageFormat)localObject1).format(arrayOfObject1));
/*      */     }
/*      */     
/*      */ 
/* 2849 */     Object localObject1 = null;
/*      */     try {
/* 2851 */       localObject1 = (X509Certificate)this.cf.generateCertificate(paramInputStream);
/*      */     } catch (ClassCastException|CertificateException localClassCastException) {
/* 2853 */       throw new Exception(rb.getString("Input.not.an.X.509.certificate"));
/*      */     }
/*      */     
/* 2856 */     if (this.noprompt) {
/* 2857 */       checkWeak(rb.getString("the.input"), (Certificate)localObject1);
/* 2858 */       this.keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
/* 2859 */       return true;
/*      */     }
/*      */     
/*      */ 
/* 2863 */     int i = 0;
/* 2864 */     if (isSelfSigned((X509Certificate)localObject1)) {
/* 2865 */       ((X509Certificate)localObject1).verify(((X509Certificate)localObject1).getPublicKey());
/* 2866 */       i = 1;
/*      */     }
/*      */     
/*      */ 
/* 2870 */     String str1 = null;
/* 2871 */     String str2 = this.keyStore.getCertificateAlias((Certificate)localObject1);
/* 2872 */     Object localObject2; Object[] arrayOfObject2; if (str2 != null)
/*      */     {
/* 2874 */       localObject2 = new MessageFormat(rb.getString("Certificate.already.exists.in.keystore.under.alias.trustalias."));
/* 2875 */       arrayOfObject2 = new Object[] { str2 };
/* 2876 */       System.err.println(((MessageFormat)localObject2).format(arrayOfObject2));
/* 2877 */       checkWeak(rb.getString("the.input"), (Certificate)localObject1);
/* 2878 */       printWeakWarnings(true);
/*      */       
/* 2880 */       str1 = getYesNoReply(rb.getString("Do.you.still.want.to.add.it.no."));
/* 2881 */     } else if (i != 0) {
/* 2882 */       if ((this.trustcacerts) && (this.caks != null) && 
/* 2883 */         ((str2 = this.caks.getCertificateAlias((Certificate)localObject1)) != null))
/*      */       {
/* 2885 */         localObject2 = new MessageFormat(rb.getString("Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias."));
/* 2886 */         arrayOfObject2 = new Object[] { str2 };
/* 2887 */         System.err.println(((MessageFormat)localObject2).format(arrayOfObject2));
/* 2888 */         checkWeak(rb.getString("the.input"), (Certificate)localObject1);
/* 2889 */         printWeakWarnings(true);
/*      */         
/* 2891 */         str1 = getYesNoReply(rb.getString("Do.you.still.want.to.add.it.to.your.own.keystore.no."));
/*      */       }
/* 2893 */       if (str2 == null)
/*      */       {
/*      */ 
/* 2896 */         printX509Cert((X509Certificate)localObject1, System.out);
/* 2897 */         checkWeak(rb.getString("the.input"), (Certificate)localObject1);
/* 2898 */         printWeakWarnings(true);
/*      */         
/* 2900 */         str1 = getYesNoReply(rb.getString("Trust.this.certificate.no."));
/*      */       }
/*      */     }
/* 2903 */     if (str1 != null) {
/* 2904 */       if ("YES".equals(str1)) {
/* 2905 */         this.keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
/* 2906 */         return true;
/*      */       }
/* 2908 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 2915 */       localObject2 = establishCertChain(null, (Certificate)localObject1);
/* 2916 */       if (localObject2 != null) {
/* 2917 */         this.keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
/* 2918 */         return true;
/*      */       }
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 2923 */       printX509Cert((X509Certificate)localObject1, System.out);
/* 2924 */       checkWeak(rb.getString("the.input"), (Certificate)localObject1);
/* 2925 */       printWeakWarnings(true);
/*      */       
/* 2927 */       str1 = getYesNoReply(rb.getString("Trust.this.certificate.no."));
/* 2928 */       if ("YES".equals(str1)) {
/* 2929 */         this.keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
/* 2930 */         return true;
/*      */       }
/* 2932 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 2936 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private char[] getNewPasswd(String paramString, char[] paramArrayOfChar)
/*      */     throws Exception
/*      */   {
/* 2949 */     char[] arrayOfChar1 = null;
/* 2950 */     char[] arrayOfChar2 = null;
/*      */     
/* 2952 */     for (int i = 0; i < 3; i++)
/*      */     {
/* 2954 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("New.prompt."));
/* 2955 */       Object[] arrayOfObject1 = { paramString };
/* 2956 */       System.err.print(localMessageFormat.format(arrayOfObject1));
/* 2957 */       arrayOfChar1 = Password.readPassword(System.in);
/* 2958 */       this.passwords.add(arrayOfChar1);
/* 2959 */       if ((arrayOfChar1 == null) || (arrayOfChar1.length < 6)) {
/* 2960 */         System.err.println(rb
/* 2961 */           .getString("Password.is.too.short.must.be.at.least.6.characters"));
/* 2962 */       } else if (Arrays.equals(arrayOfChar1, paramArrayOfChar)) {
/* 2963 */         System.err.println(rb.getString("Passwords.must.differ"));
/*      */       }
/*      */       else {
/* 2966 */         localMessageFormat = new MessageFormat(rb.getString("Re.enter.new.prompt."));
/* 2967 */         Object[] arrayOfObject2 = { paramString };
/* 2968 */         System.err.print(localMessageFormat.format(arrayOfObject2));
/* 2969 */         arrayOfChar2 = Password.readPassword(System.in);
/* 2970 */         this.passwords.add(arrayOfChar2);
/* 2971 */         if (!Arrays.equals(arrayOfChar1, arrayOfChar2))
/*      */         {
/* 2973 */           System.err.println(rb.getString("They.don.t.match.Try.again"));
/*      */         } else {
/* 2975 */           Arrays.fill(arrayOfChar2, ' ');
/* 2976 */           return arrayOfChar1;
/*      */         }
/*      */       }
/* 2979 */       if (arrayOfChar1 != null) {
/* 2980 */         Arrays.fill(arrayOfChar1, ' ');
/* 2981 */         arrayOfChar1 = null;
/*      */       }
/* 2983 */       if (arrayOfChar2 != null) {
/* 2984 */         Arrays.fill(arrayOfChar2, ' ');
/* 2985 */         arrayOfChar2 = null;
/*      */       }
/*      */     }
/* 2988 */     throw new Exception(rb.getString("Too.many.failures.try.later"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getAlias(String paramString)
/*      */     throws Exception
/*      */   {
/* 2997 */     if (paramString != null)
/*      */     {
/* 2999 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Enter.prompt.alias.name."));
/* 3000 */       Object[] arrayOfObject = { paramString };
/* 3001 */       System.err.print(localMessageFormat.format(arrayOfObject));
/*      */     } else {
/* 3003 */       System.err.print(rb.getString("Enter.alias.name."));
/*      */     }
/* 3005 */     return 
/* 3006 */       new BufferedReader(new InputStreamReader(System.in)).readLine();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String inputStringFromStdin(String paramString)
/*      */     throws Exception
/*      */   {
/* 3015 */     System.err.print(paramString);
/* 3016 */     return new BufferedReader(new InputStreamReader(System.in))
/* 3017 */       .readLine();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private char[] getKeyPasswd(String paramString1, String paramString2, char[] paramArrayOfChar)
/*      */     throws Exception
/*      */   {
/* 3028 */     int i = 0;
/* 3029 */     char[] arrayOfChar = null;
/*      */     do { MessageFormat localMessageFormat;
/*      */       Object[] arrayOfObject1;
/* 3032 */       if (paramArrayOfChar != null)
/*      */       {
/* 3034 */         localMessageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
/* 3035 */         arrayOfObject1 = new Object[] { paramString1 };
/* 3036 */         System.err.println(localMessageFormat.format(arrayOfObject1));
/*      */         
/*      */ 
/* 3039 */         localMessageFormat = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
/* 3040 */         Object[] arrayOfObject2 = { paramString2 };
/* 3041 */         System.err.print(localMessageFormat.format(arrayOfObject2));
/*      */       }
/*      */       else {
/* 3044 */         localMessageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
/* 3045 */         arrayOfObject1 = new Object[] { paramString1 };
/* 3046 */         System.err.print(localMessageFormat.format(arrayOfObject1));
/*      */       }
/* 3048 */       System.err.flush();
/* 3049 */       arrayOfChar = Password.readPassword(System.in);
/* 3050 */       this.passwords.add(arrayOfChar);
/* 3051 */       if (arrayOfChar == null) {
/* 3052 */         arrayOfChar = paramArrayOfChar;
/*      */       }
/* 3054 */       i++;
/* 3055 */     } while ((arrayOfChar == null) && (i < 3));
/*      */     
/* 3057 */     if (arrayOfChar == null) {
/* 3058 */       throw new Exception(rb.getString("Too.many.failures.try.later"));
/*      */     }
/*      */     
/* 3061 */     return arrayOfChar;
/*      */   }
/*      */   
/*      */   private String withWeak(String paramString) {
/* 3065 */     if (DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramString, null)) {
/* 3066 */       return paramString;
/*      */     }
/* 3068 */     return String.format(rb.getString("with.weak"), new Object[] { paramString });
/*      */   }
/*      */   
/*      */   private String withWeak(PublicKey paramPublicKey)
/*      */   {
/* 3073 */     if (DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramPublicKey)) {
/* 3074 */       return String.format(rb.getString("key.bit"), new Object[] {
/* 3075 */         Integer.valueOf(KeyUtil.getKeySize(paramPublicKey)), paramPublicKey.getAlgorithm() });
/*      */     }
/* 3077 */     return String.format(rb.getString("key.bit.weak"), new Object[] {
/* 3078 */       Integer.valueOf(KeyUtil.getKeySize(paramPublicKey)), paramPublicKey.getAlgorithm() });
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
/*      */   private void printX509Cert(X509Certificate paramX509Certificate, PrintStream paramPrintStream)
/*      */     throws Exception
/*      */   {
/* 3107 */     MessageFormat localMessageFormat = new MessageFormat(rb.getString(".PATTERN.printX509Cert.with.weak"));
/* 3108 */     PublicKey localPublicKey = paramX509Certificate.getPublicKey();
/* 3109 */     String str = paramX509Certificate.getSigAlgName();
/*      */     
/* 3111 */     if (!isTrustedCert(paramX509Certificate)) {
/* 3112 */       str = withWeak(str);
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
/* 3124 */     Object[] arrayOfObject = { paramX509Certificate.getSubjectDN().toString(), paramX509Certificate.getIssuerDN().toString(), paramX509Certificate.getSerialNumber().toString(16), paramX509Certificate.getNotBefore().toString(), paramX509Certificate.getNotAfter().toString(), getCertFingerPrint("MD5", paramX509Certificate), getCertFingerPrint("SHA1", paramX509Certificate), getCertFingerPrint("SHA-256", paramX509Certificate), str, withWeak(localPublicKey), Integer.valueOf(paramX509Certificate.getVersion()) };
/*      */     
/* 3126 */     paramPrintStream.println(localMessageFormat.format(arrayOfObject));
/*      */     
/* 3128 */     if ((paramX509Certificate instanceof X509CertImpl)) {
/* 3129 */       X509CertImpl localX509CertImpl = (X509CertImpl)paramX509Certificate;
/* 3130 */       X509CertInfo localX509CertInfo = (X509CertInfo)localX509CertImpl.get("x509.info");
/*      */       
/*      */ 
/*      */ 
/* 3134 */       CertificateExtensions localCertificateExtensions = (CertificateExtensions)localX509CertInfo.get("extensions");
/* 3135 */       if (localCertificateExtensions != null) {
/* 3136 */         printExtensions(rb.getString("Extensions."), localCertificateExtensions, paramPrintStream);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static void printExtensions(String paramString, CertificateExtensions paramCertificateExtensions, PrintStream paramPrintStream) throws Exception
/*      */   {
/* 3143 */     int i = 0;
/* 3144 */     Iterator localIterator1 = paramCertificateExtensions.getAllExtensions().iterator();
/* 3145 */     Iterator localIterator2 = paramCertificateExtensions.getUnparseableExtensions().values().iterator();
/* 3146 */     while ((localIterator1.hasNext()) || (localIterator2.hasNext())) {
/* 3147 */       Extension localExtension = localIterator1.hasNext() ? (Extension)localIterator1.next() : (Extension)localIterator2.next();
/* 3148 */       if (i == 0) {
/* 3149 */         paramPrintStream.println();
/* 3150 */         paramPrintStream.println(paramString);
/* 3151 */         paramPrintStream.println();
/*      */       }
/* 3153 */       paramPrintStream.print("#" + ++i + ": " + localExtension);
/* 3154 */       if (localExtension.getClass() == Extension.class) {
/* 3155 */         byte[] arrayOfByte = localExtension.getExtensionValue();
/* 3156 */         if (arrayOfByte.length == 0) {
/* 3157 */           paramPrintStream.println(rb.getString(".Empty.value."));
/*      */         } else {
/* 3159 */           new HexDumpEncoder().encodeBuffer(localExtension.getExtensionValue(), paramPrintStream);
/* 3160 */           paramPrintStream.println();
/*      */         }
/*      */       }
/* 3163 */       paramPrintStream.println();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean isSelfSigned(X509Certificate paramX509Certificate)
/*      */   {
/* 3171 */     return signedBy(paramX509Certificate, paramX509Certificate);
/*      */   }
/*      */   
/*      */   private boolean signedBy(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2) {
/* 3175 */     if (!paramX509Certificate2.getSubjectDN().equals(paramX509Certificate1.getIssuerDN())) {
/* 3176 */       return false;
/*      */     }
/*      */     try {
/* 3179 */       paramX509Certificate1.verify(paramX509Certificate2.getPublicKey());
/* 3180 */       return true;
/*      */     } catch (Exception localException) {}
/* 3182 */     return false;
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
/*      */   private static Pair<String, Certificate> getSigner(Certificate paramCertificate, KeyStore paramKeyStore)
/*      */     throws Exception
/*      */   {
/* 3197 */     if (paramKeyStore.getCertificateAlias(paramCertificate) != null) {
/* 3198 */       return new Pair("", paramCertificate);
/*      */     }
/* 3200 */     Enumeration localEnumeration = paramKeyStore.aliases();
/* 3201 */     while (localEnumeration.hasMoreElements()) {
/* 3202 */       String str = (String)localEnumeration.nextElement();
/* 3203 */       Certificate localCertificate = paramKeyStore.getCertificate(str);
/* 3204 */       if (localCertificate != null) {
/*      */         try {
/* 3206 */           paramCertificate.verify(localCertificate.getPublicKey());
/* 3207 */           return new Pair(str, localCertificate);
/*      */         }
/*      */         catch (Exception localException) {}
/*      */       }
/*      */     }
/*      */     
/* 3213 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private X500Name getX500Name()
/*      */     throws IOException
/*      */   {
/* 3221 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
/* 3222 */     String str1 = "Unknown";
/* 3223 */     String str2 = "Unknown";
/* 3224 */     String str3 = "Unknown";
/* 3225 */     String str4 = "Unknown";
/* 3226 */     String str5 = "Unknown";
/* 3227 */     String str6 = "Unknown";
/*      */     
/* 3229 */     String str7 = null;
/*      */     
/* 3231 */     int i = 20;
/*      */     X500Name localX500Name;
/* 3233 */     do { if (i-- < 0) {
/* 3234 */         throw new RuntimeException(rb.getString("Too.many.retries.program.terminated"));
/*      */       }
/*      */       
/* 3237 */       str1 = inputString(localBufferedReader, rb
/* 3238 */         .getString("What.is.your.first.and.last.name."), str1);
/*      */       
/* 3240 */       str2 = inputString(localBufferedReader, rb
/*      */       
/* 3242 */         .getString("What.is.the.name.of.your.organizational.unit."), str2);
/*      */       
/* 3244 */       str3 = inputString(localBufferedReader, rb
/* 3245 */         .getString("What.is.the.name.of.your.organization."), str3);
/*      */       
/* 3247 */       str4 = inputString(localBufferedReader, rb
/* 3248 */         .getString("What.is.the.name.of.your.City.or.Locality."), str4);
/*      */       
/* 3250 */       str5 = inputString(localBufferedReader, rb
/* 3251 */         .getString("What.is.the.name.of.your.State.or.Province."), str5);
/*      */       
/* 3253 */       str6 = inputString(localBufferedReader, rb
/*      */       
/* 3255 */         .getString("What.is.the.two.letter.country.code.for.this.unit."), str6);
/*      */       
/* 3257 */       localX500Name = new X500Name(str1, str2, str3, str4, str5, str6);
/*      */       
/*      */ 
/* 3260 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Is.name.correct."));
/* 3261 */       Object[] arrayOfObject = { localX500Name };
/*      */       
/* 3263 */       str7 = inputString(localBufferedReader, localMessageFormat.format(arrayOfObject), rb.getString("no"));
/* 3264 */     } while ((collator.compare(str7, rb.getString("yes")) != 0) && 
/* 3265 */       (collator.compare(str7, rb.getString("y")) != 0));
/*      */     
/* 3267 */     System.err.println();
/* 3268 */     return localX500Name;
/*      */   }
/*      */   
/*      */ 
/*      */   private String inputString(BufferedReader paramBufferedReader, String paramString1, String paramString2)
/*      */     throws IOException
/*      */   {
/* 3275 */     System.err.println(paramString1);
/*      */     
/* 3277 */     MessageFormat localMessageFormat = new MessageFormat(rb.getString(".defaultValue."));
/* 3278 */     Object[] arrayOfObject = { paramString2 };
/* 3279 */     System.err.print(localMessageFormat.format(arrayOfObject));
/* 3280 */     System.err.flush();
/*      */     
/* 3282 */     String str = paramBufferedReader.readLine();
/* 3283 */     if ((str == null) || (collator.compare(str, "") == 0)) {
/* 3284 */       str = paramString2;
/*      */     }
/* 3286 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void dumpCert(Certificate paramCertificate, PrintStream paramPrintStream)
/*      */     throws IOException, CertificateException
/*      */   {
/* 3296 */     if (this.rfc) {
/* 3297 */       paramPrintStream.println("-----BEGIN CERTIFICATE-----");
/* 3298 */       paramPrintStream.println(Base64.getMimeEncoder(64, CRLF).encodeToString(paramCertificate.getEncoded()));
/* 3299 */       paramPrintStream.println("-----END CERTIFICATE-----");
/*      */     } else {
/* 3301 */       paramPrintStream.write(paramCertificate.getEncoded());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void byte2hex(byte paramByte, StringBuffer paramStringBuffer)
/*      */   {
/* 3309 */     char[] arrayOfChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*      */     
/* 3311 */     int i = (paramByte & 0xF0) >> 4;
/* 3312 */     int j = paramByte & 0xF;
/* 3313 */     paramStringBuffer.append(arrayOfChar[i]);
/* 3314 */     paramStringBuffer.append(arrayOfChar[j]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String toHexString(byte[] paramArrayOfByte)
/*      */   {
/* 3321 */     StringBuffer localStringBuffer = new StringBuffer();
/* 3322 */     int i = paramArrayOfByte.length;
/* 3323 */     for (int j = 0; j < i; j++) {
/* 3324 */       byte2hex(paramArrayOfByte[j], localStringBuffer);
/* 3325 */       if (j < i - 1) {
/* 3326 */         localStringBuffer.append(":");
/*      */       }
/*      */     }
/* 3329 */     return localStringBuffer.toString();
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
/*      */   private Pair<Key, char[]> recoverKey(String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2)
/*      */     throws Exception
/*      */   {
/* 3343 */     Key localKey = null;
/*      */     MessageFormat localMessageFormat;
/* 3345 */     Object[] arrayOfObject; if (!this.keyStore.containsAlias(paramString))
/*      */     {
/* 3347 */       localMessageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/* 3348 */       arrayOfObject = new Object[] { paramString };
/* 3349 */       throw new Exception(localMessageFormat.format(arrayOfObject));
/*      */     }
/* 3351 */     if ((!this.keyStore.entryInstanceOf(paramString, PrivateKeyEntry.class)) &&
/* 3352 */       (!this.keyStore.entryInstanceOf(paramString, SecretKeyEntry.class)))
/*      */     {
/* 3354 */       localMessageFormat = new MessageFormat(rb.getString("Alias.alias.has.no.key"));
/* 3355 */       arrayOfObject = new Object[] { paramString };
/* 3356 */       throw new Exception(localMessageFormat.format(arrayOfObject));
/*      */     }
/*      */     
/* 3359 */     if (paramArrayOfChar2 == null) {
/*      */       try
/*      */       {
/* 3362 */         localKey = this.keyStore.getKey(paramString, paramArrayOfChar1);
/*      */         
/* 3364 */         paramArrayOfChar2 = paramArrayOfChar1;
/* 3365 */         this.passwords.add(paramArrayOfChar2);
/*      */       }
/*      */       catch (UnrecoverableKeyException localUnrecoverableKeyException) {
/* 3368 */         if (!this.token) {
/* 3369 */           paramArrayOfChar2 = getKeyPasswd(paramString, null, null);
/* 3370 */           localKey = this.keyStore.getKey(paramString, paramArrayOfChar2);
/*      */         } else {
/* 3372 */           throw localUnrecoverableKeyException;
/*      */         }
/*      */       }
/*      */     } else {
/* 3376 */       localKey = this.keyStore.getKey(paramString, paramArrayOfChar2);
/*      */     }
/*      */     
/* 3379 */     return Pair.of(localKey, paramArrayOfChar2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Pair<Entry, char[]> recoverEntry(KeyStore paramKeyStore, String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2)
/*      */     throws Exception
/*      */   {
/*      */     Object localObject2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3394 */     if (!paramKeyStore.containsAlias(paramString))
/*      */     {
/* 3396 */       localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
/* 3397 */       localObject2 = new Object[] { paramString };
/* 3398 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */     
/* 3401 */     Object localObject1 = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 3408 */       localObject2 = paramKeyStore.getEntry(paramString, (ProtectionParameter)localObject1);
/* 3409 */       paramArrayOfChar2 = null;
/*      */     }
/*      */     catch (UnrecoverableEntryException localUnrecoverableEntryException1) {
/* 3412 */       if (("PKCS11".equalsIgnoreCase(paramKeyStore.getType())) || 
/* 3413 */         (KeyStoreUtil.isWindowsKeyStore(paramKeyStore.getType())))
/*      */       {
/* 3415 */         throw localUnrecoverableEntryException1;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3420 */       if (paramArrayOfChar2 != null)
/*      */       {
/*      */ 
/*      */ 
/* 3424 */         localObject1 = new PasswordProtection(paramArrayOfChar2);
/* 3425 */         localObject2 = paramKeyStore.getEntry(paramString, (ProtectionParameter)localObject1);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */         try
/*      */         {
/* 3432 */           localObject1 = new PasswordProtection(paramArrayOfChar1);
/* 3433 */           localObject2 = paramKeyStore.getEntry(paramString, (ProtectionParameter)localObject1);
/* 3434 */           paramArrayOfChar2 = paramArrayOfChar1;
/*      */         } catch (UnrecoverableEntryException localUnrecoverableEntryException2) {
/* 3436 */           if ("PKCS12".equalsIgnoreCase(paramKeyStore.getType()))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 3441 */             throw localUnrecoverableEntryException2;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 3446 */           paramArrayOfChar2 = getKeyPasswd(paramString, null, null);
/* 3447 */           localObject1 = new PasswordProtection(paramArrayOfChar2);
/* 3448 */           localObject2 = paramKeyStore.getEntry(paramString, (ProtectionParameter)localObject1);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3454 */     return Pair.of(localObject2, paramArrayOfChar2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getCertFingerPrint(String paramString, Certificate paramCertificate)
/*      */     throws Exception
/*      */   {
/* 3462 */     byte[] arrayOfByte1 = paramCertificate.getEncoded();
/* 3463 */     MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
/* 3464 */     byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
/* 3465 */     return toHexString(arrayOfByte2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void printNoIntegrityWarning()
/*      */   {
/* 3472 */     System.err.println();
/* 3473 */     System.err.println(rb
/* 3474 */       .getString(".WARNING.WARNING.WARNING."));
/* 3475 */     System.err.println(rb
/* 3476 */       .getString(".The.integrity.of.the.information.stored.in.your.keystore."));
/* 3477 */     System.err.println(rb
/* 3478 */       .getString(".WARNING.WARNING.WARNING."));
/* 3479 */     System.err.println();
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
/*      */   private Certificate[] validateReply(String paramString, Certificate paramCertificate, Certificate[] paramArrayOfCertificate)
/*      */     throws Exception
/*      */   {
/* 3497 */     checkWeak(rb.getString("reply"), paramArrayOfCertificate);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3503 */     PublicKey localPublicKey = paramCertificate.getPublicKey();
/* 3504 */     for (int i = 0; i < paramArrayOfCertificate.length; i++) {
/* 3505 */       if (localPublicKey.equals(paramArrayOfCertificate[i].getPublicKey())) {
/*      */         break;
/*      */       }
/*      */     }
/* 3509 */     if (i == paramArrayOfCertificate.length)
/*      */     {
/* 3511 */       localObject1 = new MessageFormat(rb.getString("Certificate.reply.does.not.contain.public.key.for.alias."));
/* 3512 */       localObject2 = new Object[] { paramString };
/* 3513 */       throw new Exception(((MessageFormat)localObject1).format(localObject2));
/*      */     }
/*      */     
/* 3516 */     Object localObject1 = paramArrayOfCertificate[0];
/* 3517 */     paramArrayOfCertificate[0] = paramArrayOfCertificate[i];
/* 3518 */     paramArrayOfCertificate[i] = localObject1;
/*      */     
/* 3520 */     Object localObject2 = (X509Certificate)paramArrayOfCertificate[0];
/*      */     
/* 3522 */     for (i = 1; i < paramArrayOfCertificate.length - 1; i++)
/*      */     {
/*      */ 
/* 3525 */       for (int j = i; j < paramArrayOfCertificate.length; j++) {
/* 3526 */         if (signedBy((X509Certificate)localObject2, (X509Certificate)paramArrayOfCertificate[j])) {
/* 3527 */           localObject1 = paramArrayOfCertificate[i];
/* 3528 */           paramArrayOfCertificate[i] = paramArrayOfCertificate[j];
/* 3529 */           paramArrayOfCertificate[j] = localObject1;
/* 3530 */           localObject2 = (X509Certificate)paramArrayOfCertificate[i];
/* 3531 */           break;
/*      */         }
/*      */       }
/* 3534 */       if (j == paramArrayOfCertificate.length)
/*      */       {
/* 3536 */         throw new Exception(rb.getString("Incomplete.certificate.chain.in.reply"));
/*      */       }
/*      */     }
/*      */     
/* 3540 */     if (this.noprompt) {
/* 3541 */       return paramArrayOfCertificate;
/*      */     }
/*      */     
/*      */ 
/* 3545 */     Certificate localCertificate = paramArrayOfCertificate[(paramArrayOfCertificate.length - 1)];
/* 3546 */     int k = 1;
/* 3547 */     Pair localPair = getSigner(localCertificate, this.keyStore);
/* 3548 */     if ((localPair == null) && (this.trustcacerts) && (this.caks != null)) {
/* 3549 */       localPair = getSigner(localCertificate, this.caks);
/* 3550 */       k = 0; }
/*      */     Object localObject3;
/* 3552 */     if (localPair == null) {
/* 3553 */       System.err.println();
/* 3554 */       System.err
/* 3555 */         .println(rb.getString("Top.level.certificate.in.reply."));
/* 3556 */       printX509Cert((X509Certificate)localCertificate, System.out);
/* 3557 */       System.err.println();
/* 3558 */       System.err.print(rb.getString(".is.not.trusted."));
/* 3559 */       printWeakWarnings(true);
/*      */       
/* 3561 */       localObject3 = getYesNoReply(rb.getString("Install.reply.anyway.no."));
/* 3562 */       if ("NO".equals(localObject3)) {
/* 3563 */         return null;
/*      */       }
/*      */     }
/* 3566 */     else if (localPair.snd != localCertificate)
/*      */     {
/* 3568 */       localObject3 = new Certificate[paramArrayOfCertificate.length + 1];
/*      */       
/* 3570 */       System.arraycopy(paramArrayOfCertificate, 0, localObject3, 0, paramArrayOfCertificate.length);
/*      */       
/* 3572 */       localObject3[(localObject3.length - 1)] = ((Certificate)localPair.snd);
/* 3573 */       paramArrayOfCertificate = (Certificate[])localObject3;
/* 3574 */       checkWeak(String.format(rb.getString(k != 0 ? "alias.in.keystore" : "alias.in.cacerts"), new Object[] { localPair.fst }), (Certificate)localPair.snd);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3581 */     return paramArrayOfCertificate;
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
/*      */   private Certificate[] establishCertChain(Certificate paramCertificate1, Certificate paramCertificate2)
/*      */     throws Exception
/*      */   {
/* 3600 */     if (paramCertificate1 != null)
/*      */     {
/*      */ 
/* 3603 */       localObject1 = paramCertificate1.getPublicKey();
/* 3604 */       localObject2 = paramCertificate2.getPublicKey();
/* 3605 */       if (!localObject1.equals(localObject2))
/*      */       {
/* 3607 */         throw new Exception(rb.getString("Public.keys.in.reply.and.keystore.don.t.match"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3612 */       if (paramCertificate2.equals(paramCertificate1))
/*      */       {
/* 3614 */         throw new Exception(rb.getString("Certificate.reply.and.certificate.in.keystore.are.identical"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3622 */     Object localObject1 = null;
/* 3623 */     if (this.keyStore.size() > 0) {
/* 3624 */       localObject1 = new Hashtable(11);
/* 3625 */       keystorecerts2Hashtable(this.keyStore, (Hashtable)localObject1);
/*      */     }
/* 3627 */     if ((this.trustcacerts) && 
/* 3628 */       (this.caks != null) && (this.caks.size() > 0)) {
/* 3629 */       if (localObject1 == null) {
/* 3630 */         localObject1 = new Hashtable(11);
/*      */       }
/* 3632 */       keystorecerts2Hashtable(this.caks, (Hashtable)localObject1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3637 */     Object localObject2 = new Vector(2);
/* 3638 */     if (buildChain(new Pair(rb
/* 3639 */       .getString("the.input"), (X509Certificate)paramCertificate2), (Vector)localObject2, (Hashtable)localObject1))
/*      */     {
/*      */ 
/* 3642 */       for (Object localObject3 = ((Vector)localObject2).iterator(); ((Iterator)localObject3).hasNext();) { Pair localPair = (Pair)((Iterator)localObject3).next();
/* 3643 */         checkWeak((String)localPair.fst, (Certificate)localPair.snd);
/*      */       }
/*      */       
/* 3646 */       localObject3 = new Certificate[((Vector)localObject2).size()];
/*      */       
/*      */ 
/*      */ 
/* 3650 */       int i = 0;
/* 3651 */       for (int j = ((Vector)localObject2).size() - 1; j >= 0; j--) {
/* 3652 */         localObject3[i] = ((Certificate)((Pair)((Vector)localObject2).elementAt(j)).snd);
/* 3653 */         i++;
/*      */       }
/* 3655 */       return (Certificate[])localObject3;
/*      */     }
/*      */     
/* 3658 */     throw new Exception(rb.getString("Failed.to.establish.chain.from.reply"));
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
/*      */   private boolean buildChain(Pair<String, X509Certificate> paramPair, Vector<Pair<String, X509Certificate>> paramVector, Hashtable<Principal, Vector<Pair<String, X509Certificate>>> paramHashtable)
/*      */   {
/* 3684 */     if (isSelfSigned((X509Certificate)paramPair.snd))
/*      */     {
/*      */ 
/* 3687 */       paramVector.addElement(paramPair);
/* 3688 */       return true;
/*      */     }
/*      */     
/* 3691 */     Principal localPrincipal = ((X509Certificate)paramPair.snd).getIssuerDN();
/*      */     
/*      */ 
/* 3694 */     Vector localVector = (Vector)paramHashtable.get(localPrincipal);
/* 3695 */     if (localVector == null) {
/* 3696 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3702 */     Enumeration localEnumeration = localVector.elements();
/* 3703 */     while (localEnumeration.hasMoreElements()) {
/* 3704 */       Pair localPair = (Pair)localEnumeration.nextElement();
/* 3705 */       PublicKey localPublicKey = ((X509Certificate)localPair.snd).getPublicKey();
/*      */       try {
/* 3707 */         ((X509Certificate)paramPair.snd).verify(localPublicKey);
/*      */       } catch (Exception localException) {}
/* 3709 */       continue;
/*      */       
/* 3711 */       if (buildChain(localPair, paramVector, paramHashtable)) {
/* 3712 */         paramVector.addElement(paramPair);
/* 3713 */         return true;
/*      */       }
/*      */     }
/* 3716 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getYesNoReply(String paramString)
/*      */     throws IOException
/*      */   {
/* 3727 */     String str = null;
/* 3728 */     int i = 20;
/*      */     do {
/* 3730 */       if (i-- < 0) {
/* 3731 */         throw new RuntimeException(rb.getString("Too.many.retries.program.terminated"));
/*      */       }
/*      */       
/* 3734 */       System.err.print(paramString);
/* 3735 */       System.err.flush();
/*      */       
/* 3737 */       str = new BufferedReader(new InputStreamReader(System.in)).readLine();
/* 3738 */       if ((collator.compare(str, "") == 0) || 
/* 3739 */         (collator.compare(str, rb.getString("n")) == 0) || 
/* 3740 */         (collator.compare(str, rb.getString("no")) == 0)) {
/* 3741 */         str = "NO";
/* 3742 */       } else if ((collator.compare(str, rb.getString("y")) == 0) || 
/* 3743 */         (collator.compare(str, rb.getString("yes")) == 0)) {
/* 3744 */         str = "YES";
/*      */       } else {
/* 3746 */         System.err.println(rb.getString("Wrong.answer.try.again"));
/* 3747 */         str = null;
/*      */       }
/* 3749 */     } while (str == null);
/* 3750 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void keystorecerts2Hashtable(KeyStore paramKeyStore, Hashtable<Principal, Vector<Pair<String, X509Certificate>>> paramHashtable)
/*      */     throws Exception
/*      */   {
/* 3763 */     Enumeration localEnumeration = paramKeyStore.aliases();
/* 3764 */     while (localEnumeration.hasMoreElements()) {
/* 3765 */       String str = (String)localEnumeration.nextElement();
/* 3766 */       Certificate localCertificate = paramKeyStore.getCertificate(str);
/* 3767 */       if (localCertificate != null) {
/* 3768 */         Principal localPrincipal = ((X509Certificate)localCertificate).getSubjectDN();
/*      */         
/* 3770 */         Pair localPair = new Pair(String.format(rb
/* 3771 */           .getString(paramKeyStore == this.caks ? "alias.in.cacerts" : "alias.in.keystore"), new Object[] { str }), (X509Certificate)localCertificate);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 3776 */         Vector localVector = (Vector)paramHashtable.get(localPrincipal);
/* 3777 */         if (localVector == null) {
/* 3778 */           localVector = new Vector();
/* 3779 */           localVector.addElement(localPair);
/*      */         }
/* 3781 */         else if (!localVector.contains(localPair)) {
/* 3782 */           localVector.addElement(localPair);
/*      */         }
/*      */         
/* 3785 */         paramHashtable.put(localPrincipal, localVector);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static Date getStartDate(String paramString)
/*      */     throws IOException
/*      */   {
/* 3795 */     GregorianCalendar localGregorianCalendar = new GregorianCalendar();
/* 3796 */     if (paramString != null)
/*      */     {
/* 3798 */       IOException localIOException = new IOException(rb.getString("Illegal.startdate.value"));
/* 3799 */       int i = paramString.length();
/* 3800 */       if (i == 0) {
/* 3801 */         throw localIOException;
/*      */       }
/* 3803 */       if ((paramString.charAt(0) == '-') || (paramString.charAt(0) == '+'))
/*      */       {
/* 3805 */         int j = 0;
/* 3806 */         while (j < i) {
/* 3807 */           int k = 0;
/* 3808 */           switch (paramString.charAt(j)) {
/* 3809 */           case '+':  k = 1; break;
/* 3810 */           case '-':  k = -1; break;
/* 3811 */           default:  throw localIOException;
/*      */           }
/* 3813 */           for (int m = j + 1; 
/* 3814 */               m < i; m++) {
/* 3815 */             n = paramString.charAt(m);
/* 3816 */             if ((n < 48) || (n > 57)) break;
/*      */           }
/* 3818 */           if (m == j + 1) throw localIOException;
/* 3819 */           int n = Integer.parseInt(paramString.substring(j + 1, m));
/* 3820 */           if (m >= i) throw localIOException;
/* 3821 */           int i1 = 0;
/* 3822 */           switch (paramString.charAt(m)) {
/* 3823 */           case 'y':  i1 = 1; break;
/* 3824 */           case 'm':  i1 = 2; break;
/* 3825 */           case 'd':  i1 = 5; break;
/* 3826 */           case 'H':  i1 = 10; break;
/* 3827 */           case 'M':  i1 = 12; break;
/* 3828 */           case 'S':  i1 = 13; break;
/* 3829 */           default:  throw localIOException;
/*      */           }
/* 3831 */           localGregorianCalendar.add(i1, k * n);
/* 3832 */           j = m + 1;
/*      */         }
/*      */       }
/*      */       else {
/* 3836 */         String str1 = null;String str2 = null;
/* 3837 */         if (i == 19) {
/* 3838 */           str1 = paramString.substring(0, 10);
/* 3839 */           str2 = paramString.substring(11);
/* 3840 */           if (paramString.charAt(10) != ' ')
/* 3841 */             throw localIOException;
/* 3842 */         } else if (i == 10) {
/* 3843 */           str1 = paramString;
/* 3844 */         } else if (i == 8) {
/* 3845 */           str2 = paramString;
/*      */         } else {
/* 3847 */           throw localIOException;
/*      */         }
/* 3849 */         if (str1 != null) {
/* 3850 */           if (str1.matches("\\d\\d\\d\\d\\/\\d\\d\\/\\d\\d")) {
/* 3851 */             localGregorianCalendar.set(Integer.valueOf(str1.substring(0, 4)).intValue(), 
/* 3852 */               Integer.valueOf(str1.substring(5, 7)).intValue() - 1, 
/* 3853 */               Integer.valueOf(str1.substring(8, 10)).intValue());
/*      */           } else {
/* 3855 */             throw localIOException;
/*      */           }
/*      */         }
/* 3858 */         if (str2 != null) {
/* 3859 */           if (str2.matches("\\d\\d:\\d\\d:\\d\\d")) {
/* 3860 */             localGregorianCalendar.set(11, Integer.valueOf(str2.substring(0, 2)).intValue());
/* 3861 */             localGregorianCalendar.set(12, Integer.valueOf(str2.substring(0, 2)).intValue());
/* 3862 */             localGregorianCalendar.set(13, Integer.valueOf(str2.substring(0, 2)).intValue());
/* 3863 */             localGregorianCalendar.set(14, 0);
/*      */           } else {
/* 3865 */             throw localIOException;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 3870 */     return localGregorianCalendar.getTime();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int oneOf(String paramString, String... paramVarArgs)
/*      */     throws Exception
/*      */   {
/* 3883 */     int[] arrayOfInt = new int[paramVarArgs.length];
/* 3884 */     int i = 0;
/* 3885 */     int j = Integer.MAX_VALUE;
/* 3886 */     for (int k = 0; k < paramVarArgs.length; k++) {
/* 3887 */       localObject1 = paramVarArgs[k];
/* 3888 */       if (localObject1 == null) {
/* 3889 */         j = k;
/*      */ 
/*      */ 
/*      */       }
/* 3893 */       else if (((String)localObject1).toLowerCase(Locale.ENGLISH).startsWith(paramString.toLowerCase(Locale.ENGLISH))) {
/* 3894 */         arrayOfInt[(i++)] = k;
/*      */       } else {
/* 3896 */         localObject2 = new StringBuffer();
/* 3897 */         m = 1;
/* 3898 */         for (char c : ((String)localObject1).toCharArray()) {
/* 3899 */           if (m != 0) {
/* 3900 */             ((StringBuffer)localObject2).append(c);
/* 3901 */             m = 0;
/*      */           }
/* 3903 */           else if (!Character.isLowerCase(c)) {
/* 3904 */             ((StringBuffer)localObject2).append(c);
/*      */           }
/*      */         }
/*      */         
/* 3908 */         if (((StringBuffer)localObject2).toString().equalsIgnoreCase(paramString)) {
/* 3909 */           arrayOfInt[(i++)] = k;
/*      */         }
/*      */       }
/*      */     }
/* 3913 */     if (i == 0)
/* 3914 */       return -1;
/* 3915 */     if (i == 1) {
/* 3916 */       return arrayOfInt[0];
/*      */     }
/*      */     
/* 3919 */     if (arrayOfInt[1] > j) {
/* 3920 */       return arrayOfInt[0];
/*      */     }
/* 3922 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */     
/* 3924 */     Object localObject1 = new MessageFormat(rb.getString("command.{0}.is.ambiguous."));
/* 3925 */     Object localObject2 = { paramString };
/* 3926 */     localStringBuffer.append(((MessageFormat)localObject1).format(localObject2));
/* 3927 */     localStringBuffer.append("\n    ");
/* 3928 */     for (int m = 0; (m < i) && (arrayOfInt[m] < j); m++) {
/* 3929 */       localStringBuffer.append(' ');
/* 3930 */       localStringBuffer.append(paramVarArgs[arrayOfInt[m]]);
/*      */     }
/* 3932 */     throw new Exception(localStringBuffer.toString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private GeneralName createGeneralName(String paramString1, String paramString2)
/*      */     throws Exception
/*      */   {
/* 3945 */     int i = oneOf(paramString1, new String[] { "EMAIL", "URI", "DNS", "IP", "OID" });
/* 3946 */     if (i < 0) {
/* 3947 */       throw new Exception(rb.getString("Unrecognized.GeneralName.type.") + paramString1);
/*      */     }
/*      */     Object localObject;
/* 3950 */     switch (i) {
/* 3951 */     case 0:  localObject = new sun.security.x509.RFC822Name(paramString2); break;
/* 3952 */     case 1:  localObject = new URIName(paramString2); break;
/* 3953 */     case 2:  localObject = new sun.security.x509.DNSName(paramString2); break;
/* 3954 */     case 3:  localObject = new sun.security.x509.IPAddressName(paramString2); break;
/* 3955 */     default:  localObject = new sun.security.x509.OIDName(paramString2);
/*      */     }
/* 3957 */     return new GeneralName((sun.security.x509.GeneralNameInterface)localObject);
/*      */   }
/*      */   
/* 3960 */   private static final String[] extSupported = { "BasicConstraints", "KeyUsage", "ExtendedKeyUsage", "SubjectAlternativeName", "IssuerAlternativeName", "SubjectInfoAccess", "AuthorityInfoAccess", null, "CRLDistributionPoints" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private ObjectIdentifier findOidForExtName(String paramString)
/*      */     throws Exception
/*      */   {
/* 3974 */     switch (oneOf(paramString, extSupported)) {
/* 3975 */     case 0:  return PKIXExtensions.BasicConstraints_Id;
/* 3976 */     case 1:  return PKIXExtensions.KeyUsage_Id;
/* 3977 */     case 2:  return PKIXExtensions.ExtendedKeyUsage_Id;
/* 3978 */     case 3:  return PKIXExtensions.SubjectAlternativeName_Id;
/* 3979 */     case 4:  return PKIXExtensions.IssuerAlternativeName_Id;
/* 3980 */     case 5:  return PKIXExtensions.SubjectInfoAccess_Id;
/* 3981 */     case 6:  return PKIXExtensions.AuthInfoAccess_Id;
/* 3982 */     case 8:  return PKIXExtensions.CRLDistributionPoints_Id; }
/* 3983 */     return new ObjectIdentifier(paramString);
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
/*      */   private CertificateExtensions createV3Extensions(CertificateExtensions paramCertificateExtensions1, CertificateExtensions paramCertificateExtensions2, List<String> paramList, PublicKey paramPublicKey1, PublicKey paramPublicKey2)
/*      */     throws Exception
/*      */   {
/* 4006 */     if ((paramCertificateExtensions2 != null) && (paramCertificateExtensions1 != null))
/*      */     {
/* 4008 */       throw new Exception("One of request and original should be null.");
/*      */     }
/* 4010 */     if (paramCertificateExtensions2 == null) { paramCertificateExtensions2 = new CertificateExtensions();
/*      */     }
/*      */     try
/*      */     {
/* 4014 */       if (paramCertificateExtensions1 != null)
/* 4015 */         for (localIterator = paramList.iterator(); localIterator.hasNext();) { str1 = (String)localIterator.next();
/* 4016 */           if (str1.toLowerCase(Locale.ENGLISH).startsWith("honored=")) {
/* 4017 */             localObject1 = Arrays.asList(str1
/* 4018 */               .toLowerCase(Locale.ENGLISH).substring(8).split(","));
/*      */             
/* 4020 */             if (((List)localObject1).contains("all")) {
/* 4021 */               paramCertificateExtensions2 = paramCertificateExtensions1;
/*      */             }
/*      */             
/* 4024 */             for (localObject2 = ((List)localObject1).iterator(); ((Iterator)localObject2).hasNext();) { String str2 = (String)((Iterator)localObject2).next();
/* 4025 */               if (!str2.equals("all"))
/*      */               {
/*      */ 
/* 4028 */                 i = 1;
/*      */                 
/* 4030 */                 j = -1;
/* 4031 */                 String str3 = null;
/* 4032 */                 if (str2.startsWith("-")) {
/* 4033 */                   i = 0;
/* 4034 */                   str3 = str2.substring(1);
/*      */                 } else {
/* 4036 */                   int m = str2.indexOf(':');
/* 4037 */                   if (m >= 0) {
/* 4038 */                     str3 = str2.substring(0, m);
/* 4039 */                     j = oneOf(str2.substring(m + 1), new String[] { "critical", "non-critical" });
/*      */                     
/* 4041 */                     if (j == -1)
/*      */                     {
/* 4043 */                       throw new Exception(rb.getString("Illegal.value.") + str2);
/*      */                     }
/*      */                   }
/*      */                 }
/* 4047 */                 String str4 = paramCertificateExtensions1.getNameByOid(findOidForExtName(str3));
/* 4048 */                 if (i != 0) {
/* 4049 */                   Extension localExtension = paramCertificateExtensions1.get(str4);
/* 4050 */                   if (((!localExtension.isCritical()) && (j == 0)) || (
/* 4051 */                     (localExtension.isCritical()) && (j == 1))) {
/* 4052 */                     localExtension = Extension.newExtension(localExtension
/* 4053 */                       .getExtensionId(), 
/* 4054 */                       !localExtension.isCritical(), localExtension
/* 4055 */                       .getExtensionValue());
/* 4056 */                     paramCertificateExtensions2.set(str4, localExtension);
/*      */                   }
/*      */                 } else {
/* 4059 */                   paramCertificateExtensions2.delete(str4);
/*      */                 }
/*      */               } }
/* 4062 */             break; } }
/*      */       String str1;
/*      */       Object localObject1;
/*      */       Object localObject2;
/* 4066 */       int i; int j; for (Iterator localIterator = paramList.iterator(); localIterator.hasNext();) { str1 = (String)localIterator.next();
/*      */         
/* 4068 */         boolean bool1 = false;
/*      */         
/* 4070 */         i = str1.indexOf('=');
/* 4071 */         if (i >= 0) {
/* 4072 */           localObject1 = str1.substring(0, i);
/* 4073 */           localObject2 = str1.substring(i + 1);
/*      */         } else {
/* 4075 */           localObject1 = str1;
/* 4076 */           localObject2 = null;
/*      */         }
/*      */         
/* 4079 */         j = ((String)localObject1).indexOf(':');
/* 4080 */         if (j >= 0) {
/* 4081 */           if (oneOf(((String)localObject1).substring(j + 1), new String[] { "critical" }) == 0) {
/* 4082 */             bool1 = true;
/*      */           }
/* 4084 */           localObject1 = ((String)localObject1).substring(0, j);
/*      */         }
/*      */         
/* 4087 */         if (!((String)localObject1).equalsIgnoreCase("honored"))
/*      */         {
/*      */ 
/* 4090 */           int k = oneOf((String)localObject1, extSupported);
/* 4091 */           Object localObject4; int i1; int i3; String str5; Object localObject3; int i5; String str6; String str9; switch (k) {
/*      */           case 0: 
/* 4093 */             int n = -1;
/* 4094 */             boolean bool2 = false;
/* 4095 */             if (localObject2 == null) {
/* 4096 */               bool2 = true;
/*      */             } else {
/*      */               try {
/* 4099 */                 n = Integer.parseInt((String)localObject2);
/* 4100 */                 bool2 = true;
/*      */               }
/*      */               catch (NumberFormatException localNumberFormatException) {
/* 4103 */                 localObject4 = ((String)localObject2).split(",");i1 = localObject4.length;i3 = 0; } for (; i3 < i1; i3++) { str5 = localObject4[i3];
/* 4104 */                 String[] arrayOfString = str5.split(":");
/* 4105 */                 if (arrayOfString.length != 2)
/*      */                 {
/* 4107 */                   throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */                 }
/* 4109 */                 if (arrayOfString[0].equalsIgnoreCase("ca")) {
/* 4110 */                   bool2 = Boolean.parseBoolean(arrayOfString[1]);
/* 4111 */                 } else if (arrayOfString[0].equalsIgnoreCase("pathlen")) {
/* 4112 */                   n = Integer.parseInt(arrayOfString[1]);
/*      */                 }
/*      */                 else {
/* 4115 */                   throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/* 4121 */             paramCertificateExtensions2.set("BasicConstraints", new sun.security.x509.BasicConstraintsExtension(
/* 4122 */               Boolean.valueOf(bool1), bool2, n));
/*      */             
/* 4124 */             break;
/*      */           case 1: 
/* 4126 */             if (localObject2 != null) {
/* 4127 */               localObject3 = new boolean[9];
/* 4128 */               for (str5 : ((String)localObject2).split(",")) {
/* 4129 */                 i5 = oneOf(str5, new String[] { "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment", "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly", "contentCommitment" });
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4141 */                 if (i5 < 0) {
/* 4142 */                   throw new Exception(rb.getString("Unknown.keyUsage.type.") + str5);
/*      */                 }
/* 4144 */                 if (i5 == 9) i5 = 1;
/* 4145 */                 localObject3[i5] = 1;
/*      */               }
/* 4147 */               localObject4 = new KeyUsageExtension((boolean[])localObject3);
/*      */               
/*      */ 
/* 4150 */               paramCertificateExtensions2.set("KeyUsage", Extension.newExtension(((KeyUsageExtension)localObject4)
/* 4151 */                 .getExtensionId(), bool1, ((KeyUsageExtension)localObject4)
/*      */                 
/* 4153 */                 .getExtensionValue()));
/*      */             }
/*      */             else {
/* 4156 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */             break;
/*      */           case 2: 
/* 4160 */             if (localObject2 != null) {
/* 4161 */               localObject3 = new Vector();
/* 4162 */               for (str5 : ((String)localObject2).split(",")) {
/* 4163 */                 i5 = oneOf(str5, new String[] { "anyExtendedKeyUsage", "serverAuth", "clientAuth", "codeSigning", "emailProtection", "", "", "", "timeStamping", "OCSPSigning" });
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4175 */                 if (i5 < 0) {
/*      */                   try {
/* 4177 */                     ((Vector)localObject3).add(new ObjectIdentifier(str5));
/*      */                   } catch (Exception localException1) {
/* 4179 */                     throw new Exception(rb.getString("Unknown.extendedkeyUsage.type.") + str5);
/*      */                   }
/*      */                   
/* 4182 */                 } else if (i5 == 0) {
/* 4183 */                   ((Vector)localObject3).add(new ObjectIdentifier("2.5.29.37.0"));
/*      */                 } else {
/* 4185 */                   ((Vector)localObject3).add(new ObjectIdentifier("1.3.6.1.5.5.7.3." + i5));
/*      */                 }
/*      */               }
/* 4188 */               paramCertificateExtensions2.set("ExtendedKeyUsage", new sun.security.x509.ExtendedKeyUsageExtension(
/* 4189 */                 Boolean.valueOf(bool1), (Vector)localObject3));
/*      */             }
/*      */             else {
/* 4192 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */             break;
/*      */           case 3: 
/*      */           case 4: 
/* 4197 */             if (localObject2 != null) {
/* 4198 */               localObject3 = ((String)localObject2).split(",");
/* 4199 */               localObject4 = new GeneralNames();
/* 4200 */               for (str6 : localObject3) {
/* 4201 */                 j = str6.indexOf(':');
/* 4202 */                 if (j < 0) {
/* 4203 */                   throw new Exception("Illegal item " + str6 + " in " + str1);
/*      */                 }
/* 4205 */                 String str7 = str6.substring(0, j);
/* 4206 */                 str9 = str6.substring(j + 1);
/* 4207 */                 ((GeneralNames)localObject4).add(createGeneralName(str7, str9));
/*      */               }
/* 4209 */               if (k == 3) {
/* 4210 */                 paramCertificateExtensions2.set("SubjectAlternativeName", new sun.security.x509.SubjectAlternativeNameExtension(
/*      */                 
/* 4212 */                   Boolean.valueOf(bool1), (GeneralNames)localObject4));
/*      */               } else {
/* 4214 */                 paramCertificateExtensions2.set("IssuerAlternativeName", new sun.security.x509.IssuerAlternativeNameExtension(
/*      */                 
/* 4216 */                   Boolean.valueOf(bool1), (GeneralNames)localObject4));
/*      */               }
/*      */             }
/*      */             else {
/* 4220 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */             break;
/*      */           case 5: 
/*      */           case 6: 
/* 4225 */             if (bool1) {
/* 4226 */               throw new Exception(rb.getString("This.extension.cannot.be.marked.as.critical.") + str1);
/*      */             }
/*      */             
/* 4229 */             if (localObject2 != null) {
/* 4230 */               localObject3 = new ArrayList();
/*      */               
/* 4232 */               localObject4 = ((String)localObject2).split(",");
/* 4233 */               for (str6 : localObject4) {
/* 4234 */                 j = str6.indexOf(':');
/* 4235 */                 int i7 = str6.indexOf(':', j + 1);
/* 4236 */                 if ((j < 0) || (i7 < 0))
/*      */                 {
/* 4238 */                   throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */                 }
/* 4240 */                 str9 = str6.substring(0, j);
/* 4241 */                 String str10 = str6.substring(j + 1, i7);
/* 4242 */                 String str11 = str6.substring(i7 + 1);
/* 4243 */                 int i10 = oneOf(str9, new String[] { "", "ocsp", "caIssuers", "timeStamping", "", "caRepository" });
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 ObjectIdentifier localObjectIdentifier;
/*      */                 
/*      */ 
/*      */ 
/* 4252 */                 if (i10 < 0) {
/*      */                   try {
/* 4254 */                     localObjectIdentifier = new ObjectIdentifier(str9);
/*      */                   } catch (Exception localException2) {
/* 4256 */                     throw new Exception(rb.getString("Unknown.AccessDescription.type.") + str9);
/*      */                   }
/*      */                   
/*      */                 } else {
/* 4260 */                   localObjectIdentifier = new ObjectIdentifier("1.3.6.1.5.5.7.48." + i10);
/*      */                 }
/* 4262 */                 ((List)localObject3).add(new sun.security.x509.AccessDescription(localObjectIdentifier, 
/* 4263 */                   createGeneralName(str10, str11)));
/*      */               }
/* 4265 */               if (k == 5) {
/* 4266 */                 paramCertificateExtensions2.set("SubjectInfoAccess", new sun.security.x509.SubjectInfoAccessExtension((List)localObject3));
/*      */               }
/*      */               else {
/* 4269 */                 paramCertificateExtensions2.set("AuthorityInfoAccess", new sun.security.x509.AuthorityInfoAccessExtension((List)localObject3));
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/* 4274 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */             break;
/*      */           case 8: 
/* 4278 */             if (localObject2 != null) {
/* 4279 */               localObject3 = ((String)localObject2).split(",");
/* 4280 */               localObject4 = new GeneralNames();
/* 4281 */               for (str6 : localObject3) {
/* 4282 */                 j = str6.indexOf(':');
/* 4283 */                 if (j < 0) {
/* 4284 */                   throw new Exception("Illegal item " + str6 + " in " + str1);
/*      */                 }
/* 4286 */                 String str8 = str6.substring(0, j);
/* 4287 */                 str9 = str6.substring(j + 1);
/* 4288 */                 ((GeneralNames)localObject4).add(createGeneralName(str8, str9));
/*      */               }
/* 4290 */               paramCertificateExtensions2.set("CRLDistributionPoints", new CRLDistributionPointsExtension(bool1, 
/*      */               
/* 4292 */                 Collections.singletonList(new DistributionPoint((GeneralNames)localObject4, null, null))));
/*      */             }
/*      */             else
/*      */             {
/* 4296 */               throw new Exception(rb.getString("Illegal.value.") + str1);
/*      */             }
/*      */             break;
/*      */           case -1: 
/* 4300 */             localObject3 = new ObjectIdentifier((String)localObject1);
/* 4301 */             localObject4 = null;
/* 4302 */             if (localObject2 != null) {
/* 4303 */               localObject4 = new byte[((String)localObject2).length() / 2 + 1];
/* 4304 */               int i2 = 0;
/* 4305 */               for (int i8 : ((String)localObject2).toCharArray()) {
/*      */                 int i9;
/* 4307 */                 if ((i8 >= 48) && (i8 <= 57)) {
/* 4308 */                   i9 = i8 - 48;
/* 4309 */                 } else if ((i8 >= 65) && (i8 <= 70)) {
/* 4310 */                   i9 = i8 - 65 + 10;
/* 4311 */                 } else { if ((i8 < 97) || (i8 > 102)) continue;
/* 4312 */                   i9 = i8 - 97 + 10;
/*      */                 }
/*      */                 
/*      */ 
/* 4316 */                 if (i2 % 2 == 0) {
/* 4317 */                   localObject4[(i2 / 2)] = ((byte)(i9 << 4));
/*      */                 } else {
/* 4319 */                   int tmp2446_2445 = (i2 / 2); Object tmp2446_2440 = localObject4;tmp2446_2440[tmp2446_2445] = ((byte)(tmp2446_2440[tmp2446_2445] + i9));
/*      */                 }
/* 4321 */                 i2++;
/*      */               }
/* 4323 */               if (i2 % 2 != 0) {
/* 4324 */                 throw new Exception(rb.getString("Odd.number.of.hex.digits.found.") + str1);
/*      */               }
/*      */               
/* 4327 */               localObject4 = Arrays.copyOf((byte[])localObject4, i2 / 2);
/*      */             } else {
/* 4329 */               localObject4 = new byte[0];
/*      */             }
/* 4331 */             paramCertificateExtensions2.set(((ObjectIdentifier)localObject3).toString(), new Extension((ObjectIdentifier)localObject3, bool1, new DerValue((byte)4, (byte[])localObject4)
/*      */             
/* 4333 */               .toByteArray()));
/* 4334 */             break;
/*      */           case 7: default: 
/* 4336 */             throw new Exception(rb.getString("Unknown.extension.type.") + str1);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/* 4341 */       paramCertificateExtensions2.set("SubjectKeyIdentifier", new sun.security.x509.SubjectKeyIdentifierExtension(new KeyIdentifier(paramPublicKey1)
/*      */       
/* 4343 */         .getIdentifier()));
/* 4344 */       if ((paramPublicKey2 != null) && (!paramPublicKey1.equals(paramPublicKey2))) {
/* 4345 */         paramCertificateExtensions2.set("AuthorityKeyIdentifier", new sun.security.x509.AuthorityKeyIdentifierExtension(new KeyIdentifier(paramPublicKey2), null, null));
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 4350 */       throw new RuntimeException(localIOException);
/*      */     }
/* 4352 */     return paramCertificateExtensions2;
/*      */   }
/*      */   
/*      */   private boolean isTrustedCert(Certificate paramCertificate) throws KeyStoreException {
/* 4356 */     if ((this.caks != null) && (this.caks.getCertificateAlias(paramCertificate) != null)) {
/* 4357 */       return true;
/*      */     }
/* 4359 */     String str = this.keyStore.getCertificateAlias(paramCertificate);
/* 4360 */     return (str != null) && (this.keyStore.isCertificateEntry(str));
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkWeak(String paramString1, String paramString2, Key paramKey)
/*      */   {
/* 4366 */     if ((paramString2 != null) && (!DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramString2, null)))
/*      */     {
/* 4368 */       this.weakWarnings.add(String.format(rb
/* 4369 */         .getString("whose.sigalg.risk"), new Object[] { paramString1, paramString2 }));
/*      */     }
/* 4371 */     if ((paramKey != null) && (!DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramKey))) {
/* 4372 */       this.weakWarnings.add(String.format(rb
/* 4373 */         .getString("whose.key.risk"), new Object[] { paramString1, 
/*      */         
/* 4375 */         String.format(rb.getString("key.bit"), new Object[] {
/* 4376 */         Integer.valueOf(KeyUtil.getKeySize(paramKey)), paramKey.getAlgorithm() }) }));
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkWeak(String paramString, Certificate[] paramArrayOfCertificate) throws KeyStoreException
/*      */   {
/* 4382 */     for (int i = 0; i < paramArrayOfCertificate.length; i++) {
/* 4383 */       Certificate localCertificate = paramArrayOfCertificate[i];
/* 4384 */       if ((localCertificate instanceof X509Certificate)) {
/* 4385 */         X509Certificate localX509Certificate = (X509Certificate)localCertificate;
/* 4386 */         String str = paramString;
/* 4387 */         if (paramArrayOfCertificate.length > 1) {
/* 4388 */           str = oneInMany(paramString, i, paramArrayOfCertificate.length);
/*      */         }
/* 4390 */         checkWeak(str, localX509Certificate);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkWeak(String paramString, Certificate paramCertificate) throws KeyStoreException
/*      */   {
/* 4397 */     if ((paramCertificate instanceof X509Certificate)) {
/* 4398 */       X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
/*      */       
/* 4400 */       String str = isTrustedCert(paramCertificate) ? null : localX509Certificate.getSigAlgName();
/* 4401 */       checkWeak(paramString, str, localX509Certificate.getPublicKey());
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkWeak(String paramString, PKCS10 paramPKCS10) {
/* 4406 */     checkWeak(paramString, paramPKCS10.getSigAlg(), paramPKCS10.getSubjectPublicKeyInfo());
/*      */   }
/*      */   
/*      */   private void checkWeak(String paramString, CRL paramCRL, Key paramKey) {
/* 4410 */     if ((paramCRL instanceof X509CRLImpl)) {
/* 4411 */       X509CRLImpl localX509CRLImpl = (X509CRLImpl)paramCRL;
/* 4412 */       checkWeak(paramString, localX509CRLImpl.getSigAlgName(), paramKey);
/*      */     }
/*      */   }
/*      */   
/*      */   private void printWeakWarnings(boolean paramBoolean) {
/* 4417 */     if ((!this.weakWarnings.isEmpty()) && (!this.nowarn)) {
/* 4418 */       System.err.println("\nWarning:");
/* 4419 */       for (String str : this.weakWarnings) {
/* 4420 */         System.err.println(str);
/*      */       }
/* 4422 */       if (paramBoolean)
/*      */       {
/* 4424 */         System.err.println();
/*      */       }
/*      */     }
/* 4427 */     this.weakWarnings.clear();
/*      */   }
/*      */   
/*      */   private void usage()
/*      */   {
/*      */     Object localObject1;
/*      */     int j;
/* 4434 */     if (this.command != null) {
/* 4435 */       System.err.println("keytool " + this.command + rb
/* 4436 */         .getString(".OPTION."));
/* 4437 */       System.err.println();
/* 4438 */       System.err.println(rb.getString(this.command.description));
/* 4439 */       System.err.println();
/* 4440 */       System.err.println(rb.getString("Options."));
/* 4441 */       System.err.println();
/*      */       
/*      */ 
/* 4444 */       localObject1 = new String[this.command.options.length];
/* 4445 */       String[] arrayOfString = new String[this.command.options.length];
/*      */       
/*      */ 
/* 4448 */       j = 0;
/*      */       
/*      */ 
/* 4451 */       int k = 0;
/* 4452 */       for (int m = 0; m < localObject1.length; m++) {
/* 4453 */         Option localOption = this.command.options[m];
/* 4454 */         localObject1[m] = localOption.toString();
/* 4455 */         if (localOption.arg != null) { int tmp178_176 = m; Object tmp178_175 = localObject1;tmp178_175[tmp178_176] = (tmp178_175[tmp178_176] + " " + localOption.arg); }
/* 4456 */         if (localObject1[m].length() > k) {
/* 4457 */           k = localObject1[m].length();
/*      */         }
/* 4459 */         arrayOfString[m] = rb.getString(localOption.description);
/*      */       }
/* 4461 */       for (m = 0; m < localObject1.length; m++) {
/* 4462 */         System.err.printf(" %-" + k + "s  %s\n", new Object[] { localObject1[m], arrayOfString[m] });
/*      */       }
/*      */       
/* 4465 */       System.err.println();
/* 4466 */       System.err.println(rb.getString("Use.keytool.help.for.all.available.commands"));
/*      */     }
/*      */     else {
/* 4469 */       System.err.println(rb.getString("Key.and.Certificate.Management.Tool"));
/*      */       
/* 4471 */       System.err.println();
/* 4472 */       System.err.println(rb.getString("Commands."));
/* 4473 */       System.err.println();
/* 4474 */       for (Object localObject2 : Command.values()) {
/* 4475 */         if (localObject2 == Command.KEYCLONE) break;
/* 4476 */         System.err.printf(" %-20s%s\n", new Object[] { localObject2, rb.getString(((Command)localObject2).description) });
/*      */       }
/* 4478 */       System.err.println();
/* 4479 */       System.err.println(rb.getString("Use.keytool.command.name.help.for.usage.of.command.name"));
/*      */     }
/*      */   }
/*      */   
/*      */   private void tinyHelp()
/*      */   {
/* 4485 */     usage();
/* 4486 */     if (this.debug) {
/* 4487 */       throw new RuntimeException("NO BIG ERROR, SORRY");
/*      */     }
/* 4489 */     System.exit(1);
/*      */   }
/*      */   
/*      */   private void errorNeedArgument(String paramString)
/*      */   {
/* 4494 */     Object[] arrayOfObject = { paramString };
/* 4495 */     System.err.println(new MessageFormat(rb
/* 4496 */       .getString("Command.option.flag.needs.an.argument.")).format(arrayOfObject));
/* 4497 */     tinyHelp();
/*      */   }
/*      */   
/*      */   private char[] getPass(String paramString1, String paramString2) {
/* 4501 */     char[] arrayOfChar = KeyStoreUtil.getPassWithModifier(paramString1, paramString2, rb);
/* 4502 */     if (arrayOfChar != null) return arrayOfChar;
/* 4503 */     tinyHelp();
/* 4504 */     return null;
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\tools\keytool\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
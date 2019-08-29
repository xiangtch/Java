/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.AccessController;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class Debug
/*     */ {
/*     */   private String prefix;
/*  46 */   private static String args = (String)AccessController.doPrivileged(new GetPropertyAction("java.security.debug"));
/*     */   
/*     */   static
/*     */   {
/*  50 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.security.auth.debug"));
/*     */     
/*     */ 
/*  53 */     if (args == null) {
/*  54 */       args = str;
/*     */     }
/*  56 */     else if (str != null) {
/*  57 */       args = args + "," + str;
/*     */     }
/*     */     
/*  60 */     if (args != null) {
/*  61 */       args = marshal(args);
/*  62 */       if (args.equals("help")) {
/*  63 */         Help();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void Help()
/*     */   {
/*  70 */     System.err.println();
/*  71 */     System.err.println("all           turn on all debugging");
/*  72 */     System.err.println("access        print all checkPermission results");
/*  73 */     System.err.println("certpath      PKIX CertPathBuilder and");
/*  74 */     System.err.println("              CertPathValidator debugging");
/*  75 */     System.err.println("combiner      SubjectDomainCombiner debugging");
/*  76 */     System.err.println("gssloginconfig");
/*  77 */     System.err.println("              GSS LoginConfigImpl debugging");
/*  78 */     System.err.println("configfile    JAAS ConfigFile loading");
/*  79 */     System.err.println("configparser  JAAS ConfigFile parsing");
/*  80 */     System.err.println("jar           jar verification");
/*  81 */     System.err.println("logincontext  login context results");
/*  82 */     System.err.println("jca           JCA engine class debugging");
/*  83 */     System.err.println("policy        loading and granting");
/*  84 */     System.err.println("provider      security provider debugging");
/*  85 */     System.err.println("pkcs11        PKCS11 session manager debugging");
/*  86 */     System.err.println("pkcs11keystore");
/*  87 */     System.err.println("              PKCS11 KeyStore debugging");
/*  88 */     System.err.println("sunpkcs11     SunPKCS11 provider debugging");
/*  89 */     System.err.println("scl           permissions SecureClassLoader assigns");
/*  90 */     System.err.println("ts            timestamping");
/*  91 */     System.err.println();
/*  92 */     System.err.println("The following can be used with access:");
/*  93 */     System.err.println();
/*  94 */     System.err.println("stack         include stack trace");
/*  95 */     System.err.println("domain        dump all domains in context");
/*  96 */     System.err.println("failure       before throwing exception, dump stack");
/*  97 */     System.err.println("              and domain that didn't have permission");
/*  98 */     System.err.println();
/*  99 */     System.err.println("The following can be used with stack and domain:");
/* 100 */     System.err.println();
/* 101 */     System.err.println("permission=<classname>");
/* 102 */     System.err.println("              only dump output if specified permission");
/* 103 */     System.err.println("              is being checked");
/* 104 */     System.err.println("codebase=<URL>");
/* 105 */     System.err.println("              only dump output if specified codebase");
/* 106 */     System.err.println("              is being checked");
/* 107 */     System.err.println();
/* 108 */     System.err.println("The following can be used with provider:");
/* 109 */     System.err.println();
/* 110 */     System.err.println("engine=<engines>");
/* 111 */     System.err.println("              only dump output for the specified list");
/* 112 */     System.err.println("              of JCA engines. Supported values:");
/* 113 */     System.err.println("              Cipher, KeyAgreement, KeyGenerator,");
/* 114 */     System.err.println("              KeyPairGenerator, KeyStore, Mac,");
/* 115 */     System.err.println("              MessageDigest, SecureRandom, Signature.");
/* 116 */     System.err.println();
/* 117 */     System.err.println("Note: Separate multiple options with a comma");
/* 118 */     System.exit(0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Debug getInstance(String paramString)
/*     */   {
/* 129 */     return getInstance(paramString, paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Debug getInstance(String paramString1, String paramString2)
/*     */   {
/* 138 */     if (isOn(paramString1)) {
/* 139 */       Debug localDebug = new Debug();
/* 140 */       localDebug.prefix = paramString2;
/* 141 */       return localDebug;
/*     */     }
/* 143 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isOn(String paramString)
/*     */   {
/* 153 */     if (args == null) {
/* 154 */       return false;
/*     */     }
/* 156 */     if (args.indexOf("all") != -1) {
/* 157 */       return true;
/*     */     }
/* 159 */     return args.indexOf(paramString) != -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void println(String paramString)
/*     */   {
/* 170 */     System.err.println(this.prefix + ": " + paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void println()
/*     */   {
/* 179 */     System.err.println(this.prefix + ":");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void println(String paramString1, String paramString2)
/*     */   {
/* 188 */     System.err.println(paramString1 + ": " + paramString2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toHexString(BigInteger paramBigInteger)
/*     */   {
/* 198 */     String str = paramBigInteger.toString(16);
/* 199 */     StringBuffer localStringBuffer = new StringBuffer(str.length() * 2);
/*     */     
/* 201 */     if (str.startsWith("-")) {
/* 202 */       localStringBuffer.append("   -");
/* 203 */       str = str.substring(1);
/*     */     } else {
/* 205 */       localStringBuffer.append("    ");
/*     */     }
/* 207 */     if (str.length() % 2 != 0)
/*     */     {
/* 209 */       str = "0" + str;
/*     */     }
/* 211 */     int i = 0;
/* 212 */     while (i < str.length())
/*     */     {
/* 214 */       localStringBuffer.append(str.substring(i, i + 2));
/* 215 */       i += 2;
/* 216 */       if (i != str.length()) {
/* 217 */         if (i % 64 == 0) {
/* 218 */           localStringBuffer.append("\n    ");
/* 219 */         } else if (i % 8 == 0) {
/* 220 */           localStringBuffer.append(" ");
/*     */         }
/*     */       }
/*     */     }
/* 224 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String marshal(String paramString)
/*     */   {
/* 231 */     if (paramString != null) {
/* 232 */       StringBuffer localStringBuffer1 = new StringBuffer();
/* 233 */       Object localObject = new StringBuffer(paramString);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 239 */       String str1 = "[Pp][Ee][Rr][Mm][Ii][Ss][Ss][Ii][Oo][Nn]=";
/* 240 */       String str2 = "permission=";
/* 241 */       String str3 = str1 + "[a-zA-Z_$][a-zA-Z0-9_$]*([.][a-zA-Z_$][a-zA-Z0-9_$]*)*";
/*     */       
/* 243 */       Pattern localPattern = Pattern.compile(str3);
/* 244 */       Matcher localMatcher = localPattern.matcher((CharSequence)localObject);
/* 245 */       StringBuffer localStringBuffer2 = new StringBuffer();
/* 246 */       String str4; while (localMatcher.find()) {
/* 247 */         str4 = localMatcher.group();
/* 248 */         localStringBuffer1.append(str4.replaceFirst(str1, str2));
/* 249 */         localStringBuffer1.append("  ");
/*     */         
/*     */ 
/* 252 */         localMatcher.appendReplacement(localStringBuffer2, "");
/*     */       }
/* 254 */       localMatcher.appendTail(localStringBuffer2);
/* 255 */       localObject = localStringBuffer2;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 264 */       str1 = "[Cc][Oo][Dd][Ee][Bb][Aa][Ss][Ee]=";
/* 265 */       str2 = "codebase=";
/* 266 */       str3 = str1 + "[^, ;]*";
/* 267 */       localPattern = Pattern.compile(str3);
/* 268 */       localMatcher = localPattern.matcher((CharSequence)localObject);
/* 269 */       localStringBuffer2 = new StringBuffer();
/* 270 */       while (localMatcher.find()) {
/* 271 */         str4 = localMatcher.group();
/* 272 */         localStringBuffer1.append(str4.replaceFirst(str1, str2));
/* 273 */         localStringBuffer1.append("  ");
/*     */         
/*     */ 
/* 276 */         localMatcher.appendReplacement(localStringBuffer2, "");
/*     */       }
/* 278 */       localMatcher.appendTail(localStringBuffer2);
/* 279 */       localObject = localStringBuffer2;
/*     */       
/*     */ 
/* 282 */       localStringBuffer1.append(((StringBuffer)localObject).toString().toLowerCase(Locale.ENGLISH));
/*     */       
/* 284 */       return localStringBuffer1.toString();
/*     */     }
/*     */     
/* 287 */     return null;
/*     */   }
/*     */   
/* 290 */   private static final char[] hexDigits = "0123456789abcdef".toCharArray();
/*     */   
/*     */   public static String toString(byte[] paramArrayOfByte) {
/* 293 */     if (paramArrayOfByte == null) {
/* 294 */       return "(null)";
/*     */     }
/* 296 */     StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length * 3);
/* 297 */     for (int i = 0; i < paramArrayOfByte.length; i++) {
/* 298 */       int j = paramArrayOfByte[i] & 0xFF;
/* 299 */       if (i != 0) {
/* 300 */         localStringBuilder.append(':');
/*     */       }
/* 302 */       localStringBuilder.append(hexDigits[(j >>> 4)]);
/* 303 */       localStringBuilder.append(hexDigits[(j & 0xF)]);
/*     */     }
/* 305 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\Debug.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
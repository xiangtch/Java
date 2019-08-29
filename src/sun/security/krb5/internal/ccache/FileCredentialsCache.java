/*     */ package sun.security.krb5.internal.ccache;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.Realm;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.LoginOptions;
/*     */ import sun.security.krb5.internal.TicketFlags;
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
/*     */ public class FileCredentialsCache
/*     */   extends CredentialsCache
/*     */   implements FileCCacheConstants
/*     */ {
/*     */   public int version;
/*     */   public Tag tag;
/*     */   public PrincipalName primaryPrincipal;
/*     */   private Vector<Credentials> credentialsList;
/*     */   private static String dir;
/*  64 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */   public static synchronized FileCredentialsCache acquireInstance(PrincipalName paramPrincipalName, String paramString)
/*     */   {
/*     */     try {
/*  69 */       FileCredentialsCache localFileCredentialsCache = new FileCredentialsCache();
/*  70 */       if (paramString == null) {
/*  71 */         cacheName = getDefaultCacheName();
/*     */       } else {
/*  73 */         cacheName = checkValidation(paramString);
/*     */       }
/*  75 */       if ((cacheName == null) || (!new File(cacheName).exists()))
/*     */       {
/*  77 */         return null;
/*     */       }
/*  79 */       if (paramPrincipalName != null) {
/*  80 */         localFileCredentialsCache.primaryPrincipal = paramPrincipalName;
/*     */       }
/*  82 */       localFileCredentialsCache.load(cacheName);
/*  83 */       return localFileCredentialsCache;
/*     */     }
/*     */     catch (IOException localIOException) {
/*  86 */       if (DEBUG) {
/*  87 */         localIOException.printStackTrace();
/*     */       }
/*     */     }
/*     */     catch (KrbException localKrbException) {
/*  91 */       if (DEBUG) {
/*  92 */         localKrbException.printStackTrace();
/*     */       }
/*     */     }
/*  95 */     return null;
/*     */   }
/*     */   
/*     */   public static FileCredentialsCache acquireInstance() {
/*  99 */     return acquireInstance(null, null);
/*     */   }
/*     */   
/*     */   static synchronized FileCredentialsCache New(PrincipalName paramPrincipalName, String paramString)
/*     */   {
/*     */     try {
/* 105 */       FileCredentialsCache localFileCredentialsCache = new FileCredentialsCache();
/* 106 */       cacheName = checkValidation(paramString);
/* 107 */       if (cacheName == null)
/*     */       {
/* 109 */         return null;
/*     */       }
/* 111 */       localFileCredentialsCache.init(paramPrincipalName, cacheName);
/* 112 */       return localFileCredentialsCache;
/*     */     }
/*     */     catch (IOException localIOException) {}catch (KrbException localKrbException) {}
/*     */     
/*     */ 
/*     */ 
/* 118 */     return null;
/*     */   }
/*     */   
/*     */   static synchronized FileCredentialsCache New(PrincipalName paramPrincipalName) {
/*     */     try {
/* 123 */       FileCredentialsCache localFileCredentialsCache = new FileCredentialsCache();
/* 124 */       cacheName = getDefaultCacheName();
/* 125 */       localFileCredentialsCache.init(paramPrincipalName, cacheName);
/* 126 */       return localFileCredentialsCache;
/*     */     }
/*     */     catch (IOException localIOException) {
/* 129 */       if (DEBUG) {
/* 130 */         localIOException.printStackTrace();
/*     */       }
/*     */     } catch (KrbException localKrbException) {
/* 133 */       if (DEBUG) {
/* 134 */         localKrbException.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 138 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   boolean exists(String paramString)
/*     */   {
/* 145 */     File localFile = new File(paramString);
/* 146 */     if (localFile.exists())
/* 147 */       return true;
/* 148 */     return false;
/*     */   }
/*     */   
/*     */   synchronized void init(PrincipalName paramPrincipalName, String paramString) throws IOException, KrbException
/*     */   {
/* 153 */     this.primaryPrincipal = paramPrincipalName;
/* 154 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramString);Object localObject1 = null;
/* 155 */     try { CCacheOutputStream localCCacheOutputStream = new CCacheOutputStream(localFileOutputStream);Object localObject2 = null;
/* 156 */       try { this.version = 1283;
/* 157 */         localCCacheOutputStream.writeHeader(this.primaryPrincipal, this.version);
/*     */       }
/*     */       catch (Throwable localThrowable4)
/*     */       {
/* 154 */         localObject2 = localThrowable4;throw localThrowable4; } finally {} } catch (Throwable localThrowable2) { localObject1 = localThrowable2;throw localThrowable2;
/*     */     }
/*     */     finally
/*     */     {
/* 158 */       if (localFileOutputStream != null) if (localObject1 != null) try { localFileOutputStream.close(); } catch (Throwable localThrowable6) { ((Throwable)localObject1).addSuppressed(localThrowable6); } else localFileOutputStream.close(); }
/* 159 */     load(paramString);
/*     */   }
/*     */   
/*     */   synchronized void load(String paramString) throws IOException, KrbException
/*     */   {
/* 164 */     FileInputStream localFileInputStream = new FileInputStream(paramString);Object localObject1 = null;
/* 165 */     try { CCacheInputStream localCCacheInputStream = new CCacheInputStream(localFileInputStream);Object localObject2 = null;
/* 166 */       try { this.version = localCCacheInputStream.readVersion();
/* 167 */         if (this.version == 1284) {
/* 168 */           this.tag = localCCacheInputStream.readTag();
/*     */         } else {
/* 170 */           this.tag = null;
/* 171 */           if ((this.version == 1281) || (this.version == 1282)) {
/* 172 */             localCCacheInputStream.setNativeByteOrder();
/*     */           }
/*     */         }
/* 175 */         PrincipalName localPrincipalName = localCCacheInputStream.readPrincipal(this.version);
/*     */         
/* 177 */         if (this.primaryPrincipal != null) {
/* 178 */           if (!this.primaryPrincipal.match(localPrincipalName)) {
/* 179 */             throw new IOException("Primary principals don't match.");
/*     */           }
/*     */         } else
/* 182 */           this.primaryPrincipal = localPrincipalName;
/* 183 */         this.credentialsList = new Vector();
/* 184 */         while (localCCacheInputStream.available() > 0) {
/* 185 */           Credentials localCredentials = localCCacheInputStream.readCred(this.version);
/* 186 */           if (localCredentials != null) {
/* 187 */             this.credentialsList.addElement(localCredentials);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable4)
/*     */       {
/* 164 */         localObject2 = localThrowable4;throw localThrowable4; } finally {} } catch (Throwable localThrowable2) { localObject1 = localThrowable2;throw localThrowable2;
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
/*     */     }
/*     */     finally
/*     */     {
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
/* 190 */       if (localFileInputStream != null) { if (localObject1 != null) try { localFileInputStream.close(); } catch (Throwable localThrowable6) { ((Throwable)localObject1).addSuppressed(localThrowable6); } else { localFileInputStream.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void update(Credentials paramCredentials)
/*     */   {
/* 202 */     if (this.credentialsList != null) {
/* 203 */       if (this.credentialsList.isEmpty()) {
/* 204 */         this.credentialsList.addElement(paramCredentials);
/*     */       } else {
/* 206 */         Credentials localCredentials = null;
/* 207 */         int i = 0;
/*     */         
/* 209 */         for (int j = 0; j < this.credentialsList.size(); j++) {
/* 210 */           localCredentials = (Credentials)this.credentialsList.elementAt(j);
/* 211 */           if (match(paramCredentials.sname.getNameStrings(), localCredentials.sname
/* 212 */             .getNameStrings()))
/* 213 */             if (paramCredentials.sname.getRealmString().equalsIgnoreCase(localCredentials.sname
/* 214 */               .getRealmString())) {
/* 215 */               i = 1;
/* 216 */               if (paramCredentials.endtime.getTime() >= localCredentials.endtime.getTime()) {
/* 217 */                 if (DEBUG) {
/* 218 */                   System.out.println(" >>> FileCredentialsCache Ticket matched, overwrite the old one.");
/*     */                 }
/*     */                 
/*     */ 
/* 222 */                 this.credentialsList.removeElementAt(j);
/* 223 */                 this.credentialsList.addElement(paramCredentials);
/*     */               }
/*     */             }
/*     */         }
/* 227 */         if (i == 0) {
/* 228 */           if (DEBUG) {
/* 229 */             System.out.println(" >>> FileCredentialsCache Ticket not exactly matched, add new one into cache.");
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 234 */           this.credentialsList.addElement(paramCredentials);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized PrincipalName getPrimaryPrincipal() {
/* 241 */     return this.primaryPrincipal;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void save()
/*     */     throws IOException, Asn1Exception
/*     */   {
/* 249 */     FileOutputStream localFileOutputStream = new FileOutputStream(cacheName);Object localObject1 = null;
/* 250 */     try { CCacheOutputStream localCCacheOutputStream = new CCacheOutputStream(localFileOutputStream);Object localObject2 = null;
/* 251 */       try { localCCacheOutputStream.writeHeader(this.primaryPrincipal, this.version);
/* 252 */         Credentials[] arrayOfCredentials = null;
/* 253 */         if ((arrayOfCredentials = getCredsList()) != null) {
/* 254 */           for (int i = 0; i < arrayOfCredentials.length; i++) {
/* 255 */             localCCacheOutputStream.addCreds(arrayOfCredentials[i]);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable4)
/*     */       {
/* 249 */         localObject2 = localThrowable4;throw localThrowable4; } finally {} } catch (Throwable localThrowable2) { localObject1 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/* 258 */       if (localFileOutputStream != null) if (localObject1 != null) try { localFileOutputStream.close(); } catch (Throwable localThrowable6) { ((Throwable)localObject1).addSuppressed(localThrowable6); } else localFileOutputStream.close();
/*     */     }
/*     */   }
/*     */   
/* 262 */   boolean match(String[] paramArrayOfString1, String[] paramArrayOfString2) { if (paramArrayOfString1.length != paramArrayOfString2.length) {
/* 263 */       return false;
/*     */     }
/* 265 */     for (int i = 0; i < paramArrayOfString1.length; i++) {
/* 266 */       if (!paramArrayOfString1[i].equalsIgnoreCase(paramArrayOfString2[i])) {
/* 267 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 271 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized Credentials[] getCredsList()
/*     */   {
/* 278 */     if ((this.credentialsList == null) || (this.credentialsList.isEmpty())) {
/* 279 */       return null;
/*     */     }
/* 281 */     Credentials[] arrayOfCredentials = new Credentials[this.credentialsList.size()];
/* 282 */     for (int i = 0; i < this.credentialsList.size(); i++) {
/* 283 */       arrayOfCredentials[i] = ((Credentials)this.credentialsList.elementAt(i));
/*     */     }
/* 285 */     return arrayOfCredentials;
/*     */   }
/*     */   
/*     */ 
/*     */   public Credentials getCreds(LoginOptions paramLoginOptions, PrincipalName paramPrincipalName)
/*     */   {
/* 291 */     if (paramLoginOptions == null) {
/* 292 */       return getCreds(paramPrincipalName);
/*     */     }
/* 294 */     Credentials[] arrayOfCredentials = getCredsList();
/* 295 */     if (arrayOfCredentials == null) {
/* 296 */       return null;
/*     */     }
/* 298 */     for (int i = 0; i < arrayOfCredentials.length; i++) {
/* 299 */       if ((paramPrincipalName.match(arrayOfCredentials[i].sname)) && 
/* 300 */         (arrayOfCredentials[i].flags.match(paramLoginOptions))) {
/* 301 */         return arrayOfCredentials[i];
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 306 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Credentials getCreds(PrincipalName paramPrincipalName)
/*     */   {
/* 316 */     Credentials[] arrayOfCredentials = getCredsList();
/* 317 */     if (arrayOfCredentials == null) {
/* 318 */       return null;
/*     */     }
/* 320 */     for (int i = 0; i < arrayOfCredentials.length; i++) {
/* 321 */       if (paramPrincipalName.match(arrayOfCredentials[i].sname)) {
/* 322 */         return arrayOfCredentials[i];
/*     */       }
/*     */     }
/*     */     
/* 326 */     return null;
/*     */   }
/*     */   
/*     */   public Credentials getDefaultCreds() {
/* 330 */     Credentials[] arrayOfCredentials = getCredsList();
/* 331 */     if (arrayOfCredentials == null) {
/* 332 */       return null;
/*     */     }
/* 334 */     for (int i = arrayOfCredentials.length - 1; i >= 0; i--) {
/* 335 */       if (arrayOfCredentials[i].sname.toString().startsWith("krbtgt")) {
/* 336 */         String[] arrayOfString = arrayOfCredentials[i].sname.getNameStrings();
/*     */         
/* 338 */         if (arrayOfString[1].equals(arrayOfCredentials[i].sname.getRealm().toString())) {
/* 339 */           return arrayOfCredentials[i];
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 344 */     return null;
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
/*     */   public static String getDefaultCacheName()
/*     */   {
/* 359 */     String str1 = "krb5cc";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 364 */     String str2 = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run()
/*     */       {
/* 368 */         String str = System.getenv("KRB5CCNAME");
/* 369 */         if ((str != null) && 
/* 370 */           (str.length() >= 5) && 
/* 371 */           (str.regionMatches(true, 0, "FILE:", 0, 5))) {
/* 372 */           str = str.substring(5);
/*     */         }
/* 374 */         return str;
/*     */       }
/*     */     });
/* 377 */     if (str2 != null) {
/* 378 */       if (DEBUG) {
/* 379 */         System.out.println(">>>KinitOptions cache name is " + str2);
/*     */       }
/* 381 */       return str2;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 386 */     String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
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
/* 400 */     if (str3 != null) {
/* 401 */       str4 = null;
/* 402 */       str5 = null;
/* 403 */       long l = 0L;
/*     */       
/* 405 */       if (!str3.startsWith("Windows")) {
/*     */         try
/*     */         {
/* 408 */           Class localClass = Class.forName("com.sun.security.auth.module.UnixSystem");
/* 409 */           Constructor localConstructor = localClass.getConstructor(new Class[0]);
/* 410 */           Object localObject = localConstructor.newInstance(new Object[0]);
/* 411 */           Method localMethod = localClass.getMethod("getUid", new Class[0]);
/* 412 */           l = ((Long)localMethod.invoke(localObject, new Object[0])).longValue();
/* 413 */           str2 = File.separator + "tmp" + File.separator + str1 + "_" + l;
/*     */           
/* 415 */           if (DEBUG) {
/* 416 */             System.out.println(">>>KinitOptions cache name is " + str2);
/*     */           }
/*     */           
/* 419 */           return str2;
/*     */         } catch (Exception localException) {
/* 421 */           if (DEBUG) {
/* 422 */             System.out.println("Exception in obtaining uid for Unix platforms Using user's home directory");
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 427 */             localException.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 437 */     String str4 = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
/*     */     
/*     */ 
/*     */ 
/* 441 */     String str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
/*     */     
/*     */ 
/* 444 */     if (str5 == null)
/*     */     {
/* 446 */       str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.dir"));
/*     */     }
/*     */     
/*     */ 
/* 450 */     if (str4 != null) {
/* 451 */       str2 = str5 + File.separator + str1 + "_" + str4;
/*     */     }
/*     */     else {
/* 454 */       str2 = str5 + File.separator + str1;
/*     */     }
/*     */     
/* 457 */     if (DEBUG) {
/* 458 */       System.out.println(">>>KinitOptions cache name is " + str2);
/*     */     }
/*     */     
/* 461 */     return str2;
/*     */   }
/*     */   
/*     */   public static String checkValidation(String paramString) {
/* 465 */     String str = null;
/* 466 */     if (paramString == null) {
/* 467 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 471 */       str = new File(paramString).getCanonicalPath();
/* 472 */       File localFile1 = new File(str);
/* 473 */       if (!localFile1.exists())
/*     */       {
/* 475 */         File localFile2 = new File(localFile1.getParent());
/*     */         
/* 477 */         if (!localFile2.isDirectory())
/* 478 */           str = null;
/* 479 */         localFile2 = null;
/*     */       }
/* 481 */       localFile1 = null;
/*     */     }
/*     */     catch (IOException localIOException) {
/* 484 */       str = null;
/*     */     }
/* 486 */     return str;
/*     */   }
/*     */   
/*     */   private static String exec(String paramString)
/*     */   {
/* 491 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/* 492 */     Vector localVector = new Vector();
/* 493 */     while (localStringTokenizer.hasMoreTokens()) {
/* 494 */       localVector.addElement(localStringTokenizer.nextToken());
/*     */     }
/* 496 */     String[] arrayOfString = new String[localVector.size()];
/* 497 */     localVector.copyInto(arrayOfString);
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 502 */       Process localProcess = (Process)AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public Process run() {
/*     */           try {
/* 505 */             return Runtime.getRuntime().exec(this.val$command);
/*     */           } catch (IOException localIOException) {
/* 507 */             if (FileCredentialsCache.DEBUG)
/* 508 */               localIOException.printStackTrace();
/*     */           }
/* 510 */           return null;
/*     */         }
/*     */       });
/*     */       
/* 514 */       if (localProcess == null)
/*     */       {
/* 516 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 521 */       BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream(), "8859_1"));
/* 522 */       String str = null;
/* 523 */       if ((arrayOfString.length == 1) && 
/* 524 */         (arrayOfString[0].equals("/usr/bin/env"))) {}
/* 525 */       while ((str = localBufferedReader.readLine()) != null)
/* 526 */         if ((str.length() >= 11) && 
/*     */         
/* 528 */           (str.substring(0, 11).equalsIgnoreCase("KRB5CCNAME="))) {
/* 529 */           str = str.substring(11);
/* 530 */           break;
/*     */           
/*     */ 
/*     */ 
/* 534 */           str = localBufferedReader.readLine(); }
/* 535 */       localBufferedReader.close();
/* 536 */       return str;
/*     */     } catch (Exception localException) {
/* 538 */       if (DEBUG) {
/* 539 */         localException.printStackTrace();
/*     */       }
/*     */     }
/* 542 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\ccache\FileCredentialsCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
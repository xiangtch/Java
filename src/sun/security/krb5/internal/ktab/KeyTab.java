/*     */ package sun.security.krb5.internal.ktab;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.security.krb5.Config;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.RealmException;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.crypto.EType;
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
/*     */ public class KeyTab
/*     */   implements KeyTabConstants
/*     */ {
/*  66 */   private static final boolean DEBUG = Krb5.DEBUG;
/*  67 */   private static String defaultTabName = null;
/*     */   
/*     */ 
/*     */ 
/*  71 */   private static Map<String, KeyTab> map = new HashMap();
/*     */   
/*     */ 
/*  74 */   private boolean isMissing = false;
/*     */   
/*     */ 
/*  77 */   private boolean isValid = true;
/*     */   
/*     */   private final String tabName;
/*     */   private long lastModified;
/*  81 */   private int kt_vno = 1282;
/*     */   
/*  83 */   private Vector<KeyTabEntry> entries = new Vector();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private KeyTab(String paramString)
/*     */   {
/*  93 */     this.tabName = paramString;
/*     */     try {
/*  95 */       this.lastModified = new File(this.tabName).lastModified();
/*  96 */       KeyTabInputStream localKeyTabInputStream = new KeyTabInputStream(new FileInputStream(paramString));Object localObject1 = null;
/*     */       try {
/*  98 */         load(localKeyTabInputStream);
/*     */       }
/*     */       catch (Throwable localThrowable2)
/*     */       {
/*  96 */         localObject1 = localThrowable2;throw localThrowable2;
/*     */       }
/*     */       finally {
/*  99 */         if (localKeyTabInputStream != null) if (localObject1 != null) try { localKeyTabInputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localKeyTabInputStream.close();
/*     */       }
/* 101 */     } catch (FileNotFoundException localFileNotFoundException) { this.entries.clear();
/* 102 */       this.isMissing = true;
/*     */     } catch (Exception localException) {
/* 104 */       this.entries.clear();
/* 105 */       this.isValid = false;
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
/*     */   private static synchronized KeyTab getInstance0(String paramString)
/*     */   {
/* 120 */     long l = new File(paramString).lastModified();
/* 121 */     KeyTab localKeyTab1 = (KeyTab)map.get(paramString);
/* 122 */     if ((localKeyTab1 != null) && (localKeyTab1.isValid()) && (localKeyTab1.lastModified == l)) {
/* 123 */       return localKeyTab1;
/*     */     }
/* 125 */     KeyTab localKeyTab2 = new KeyTab(paramString);
/* 126 */     if (localKeyTab2.isValid()) {
/* 127 */       map.put(paramString, localKeyTab2);
/* 128 */       return localKeyTab2; }
/* 129 */     if (localKeyTab1 != null) {
/* 130 */       return localKeyTab1;
/*     */     }
/* 132 */     return localKeyTab2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static KeyTab getInstance(String paramString)
/*     */   {
/* 142 */     if (paramString == null) {
/* 143 */       return getInstance();
/*     */     }
/* 145 */     return getInstance0(normalize(paramString));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static KeyTab getInstance(File paramFile)
/*     */   {
/* 155 */     if (paramFile == null) {
/* 156 */       return getInstance();
/*     */     }
/* 158 */     return getInstance0(paramFile.getPath());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static KeyTab getInstance()
/*     */   {
/* 167 */     return getInstance(getDefaultTabName());
/*     */   }
/*     */   
/*     */   public boolean isMissing() {
/* 171 */     return this.isMissing;
/*     */   }
/*     */   
/*     */   public boolean isValid() {
/* 175 */     return this.isValid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getDefaultTabName()
/*     */   {
/* 185 */     if (defaultTabName != null) {
/* 186 */       return defaultTabName;
/*     */     }
/* 188 */     String str1 = null;
/*     */     try
/*     */     {
/* 191 */       String str2 = Config.getInstance().get(new String[] { "libdefaults", "default_keytab_name" });
/* 192 */       if (str2 != null) {
/* 193 */         StringTokenizer localStringTokenizer = new StringTokenizer(str2, " ");
/* 194 */         while (localStringTokenizer.hasMoreTokens()) {
/* 195 */           str1 = normalize(localStringTokenizer.nextToken());
/* 196 */           if (new File(str1).exists()) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (KrbException localKrbException) {
/* 202 */       str1 = null;
/*     */     }
/*     */     
/* 205 */     if (str1 == null)
/*     */     {
/* 207 */       String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
/*     */       
/*     */ 
/* 210 */       if (str3 == null)
/*     */       {
/* 212 */         str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.dir"));
/*     */       }
/*     */       
/*     */ 
/* 216 */       str1 = str3 + File.separator + "krb5.keytab";
/*     */     }
/* 218 */     defaultTabName = str1;
/* 219 */     return str1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String normalize(String paramString)
/*     */   {
/*     */     String str;
/*     */     
/*     */ 
/*     */ 
/* 232 */     if ((paramString.length() >= 5) && 
/* 233 */       (paramString.substring(0, 5).equalsIgnoreCase("FILE:"))) {
/* 234 */       str = paramString.substring(5);
/* 235 */     } else if ((paramString.length() >= 9) && 
/* 236 */       (paramString.substring(0, 9).equalsIgnoreCase("ANY:FILE:")))
/*     */     {
/* 238 */       str = paramString.substring(9);
/* 239 */     } else if ((paramString.length() >= 7) && 
/* 240 */       (paramString.substring(0, 7).equalsIgnoreCase("SRVTAB:")))
/*     */     {
/* 242 */       str = paramString.substring(7);
/*     */     } else
/* 244 */       str = paramString;
/* 245 */     return str;
/*     */   }
/*     */   
/*     */   private void load(KeyTabInputStream paramKeyTabInputStream)
/*     */     throws IOException, RealmException
/*     */   {
/* 251 */     this.entries.clear();
/* 252 */     this.kt_vno = paramKeyTabInputStream.readVersion();
/* 253 */     if (this.kt_vno == 1281) {
/* 254 */       paramKeyTabInputStream.setNativeByteOrder();
/*     */     }
/* 256 */     int i = 0;
/*     */     
/* 258 */     while (paramKeyTabInputStream.available() > 0) {
/* 259 */       i = paramKeyTabInputStream.readEntryLength();
/* 260 */       KeyTabEntry localKeyTabEntry = paramKeyTabInputStream.readEntry(i, this.kt_vno);
/* 261 */       if (DEBUG) {
/* 262 */         System.out.println(">>> KeyTab: load() entry length: " + i + "; type: " + (localKeyTabEntry != null ? localKeyTabEntry.keyType : 0));
/*     */       }
/*     */       
/*     */ 
/* 266 */       if (localKeyTabEntry != null) {
/* 267 */         this.entries.addElement(localKeyTabEntry);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PrincipalName getOneName()
/*     */   {
/* 276 */     int i = this.entries.size();
/* 277 */     return i > 0 ? ((KeyTabEntry)this.entries.elementAt(i - 1)).service : null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncryptionKey[] readServiceKeys(PrincipalName paramPrincipalName)
/*     */   {
/* 289 */     int i = this.entries.size();
/* 290 */     ArrayList localArrayList = new ArrayList(i);
/* 291 */     if (DEBUG) {
/* 292 */       System.out.println("Looking for keys for: " + paramPrincipalName);
/*     */     }
/* 294 */     for (int j = i - 1; j >= 0; j--) {
/* 295 */       KeyTabEntry localKeyTabEntry = (KeyTabEntry)this.entries.elementAt(j);
/* 296 */       if (localKeyTabEntry.service.match(paramPrincipalName)) {
/* 297 */         if (EType.isSupported(localKeyTabEntry.keyType)) {
/* 298 */           EncryptionKey localEncryptionKey = new EncryptionKey(localKeyTabEntry.keyblock, localKeyTabEntry.keyType, new Integer(localKeyTabEntry.keyVersion));
/*     */           
/*     */ 
/* 301 */           localArrayList.add(localEncryptionKey);
/* 302 */           if (DEBUG) {
/* 303 */             System.out.println("Added key: " + localKeyTabEntry.keyType + "version: " + localKeyTabEntry.keyVersion);
/*     */           }
/*     */         }
/* 306 */         else if (DEBUG) {
/* 307 */           System.out.println("Found unsupported keytype (" + localKeyTabEntry.keyType + ") for " + paramPrincipalName);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 312 */     i = localArrayList.size();
/* 313 */     EncryptionKey[] arrayOfEncryptionKey = (EncryptionKey[])localArrayList.toArray(new EncryptionKey[i]);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 318 */     Arrays.sort(arrayOfEncryptionKey, new Comparator()
/*     */     {
/*     */       public int compare(EncryptionKey paramAnonymousEncryptionKey1, EncryptionKey paramAnonymousEncryptionKey2) {
/* 321 */         return 
/* 322 */           paramAnonymousEncryptionKey2.getKeyVersionNumber().intValue() - paramAnonymousEncryptionKey1.getKeyVersionNumber().intValue();
/*     */       }
/*     */       
/* 325 */     });
/* 326 */     return arrayOfEncryptionKey;
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
/*     */   public boolean findServiceEntry(PrincipalName paramPrincipalName)
/*     */   {
/* 340 */     for (int i = 0; i < this.entries.size(); i++) {
/* 341 */       KeyTabEntry localKeyTabEntry = (KeyTabEntry)this.entries.elementAt(i);
/* 342 */       if (localKeyTabEntry.service.match(paramPrincipalName)) {
/* 343 */         if (EType.isSupported(localKeyTabEntry.keyType))
/* 344 */           return true;
/* 345 */         if (DEBUG) {
/* 346 */           System.out.println("Found unsupported keytype (" + localKeyTabEntry.keyType + ") for " + paramPrincipalName);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 351 */     return false;
/*     */   }
/*     */   
/*     */   public String tabName() {
/* 355 */     return this.tabName;
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
/*     */   public void addEntry(PrincipalName paramPrincipalName, char[] paramArrayOfChar, int paramInt, boolean paramBoolean)
/*     */     throws KrbException
/*     */   {
/* 371 */     addEntry(paramPrincipalName, paramPrincipalName.getSalt(), paramArrayOfChar, paramInt, paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */   public void addEntry(PrincipalName paramPrincipalName, String paramString, char[] paramArrayOfChar, int paramInt, boolean paramBoolean)
/*     */     throws KrbException
/*     */   {
/* 378 */     EncryptionKey[] arrayOfEncryptionKey = EncryptionKey.acquireSecretKeys(paramArrayOfChar, paramString);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 384 */     int i = 0;
/* 385 */     for (int j = this.entries.size() - 1; j >= 0; j--) {
/* 386 */       KeyTabEntry localKeyTabEntry1 = (KeyTabEntry)this.entries.get(j);
/* 387 */       if (localKeyTabEntry1.service.match(paramPrincipalName)) {
/* 388 */         if (localKeyTabEntry1.keyVersion > i) {
/* 389 */           i = localKeyTabEntry1.keyVersion;
/*     */         }
/* 391 */         if ((!paramBoolean) || (localKeyTabEntry1.keyVersion == paramInt)) {
/* 392 */           this.entries.removeElementAt(j);
/*     */         }
/*     */       }
/*     */     }
/* 396 */     if (paramInt == -1) {
/* 397 */       paramInt = i + 1;
/*     */     }
/*     */     
/* 400 */     for (j = 0; (arrayOfEncryptionKey != null) && (j < arrayOfEncryptionKey.length); j++) {
/* 401 */       int k = arrayOfEncryptionKey[j].getEType();
/* 402 */       byte[] arrayOfByte = arrayOfEncryptionKey[j].getBytes();
/*     */       
/*     */ 
/*     */ 
/* 406 */       KeyTabEntry localKeyTabEntry2 = new KeyTabEntry(paramPrincipalName, paramPrincipalName.getRealm(), new KerberosTime(System.currentTimeMillis()), paramInt, k, arrayOfByte);
/*     */       
/* 408 */       this.entries.addElement(localKeyTabEntry2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public KeyTabEntry[] getEntries()
/*     */   {
/* 417 */     KeyTabEntry[] arrayOfKeyTabEntry = new KeyTabEntry[this.entries.size()];
/* 418 */     for (int i = 0; i < arrayOfKeyTabEntry.length; i++) {
/* 419 */       arrayOfKeyTabEntry[i] = ((KeyTabEntry)this.entries.elementAt(i));
/*     */     }
/* 421 */     return arrayOfKeyTabEntry;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static synchronized KeyTab create()
/*     */     throws IOException, RealmException
/*     */   {
/* 429 */     String str = getDefaultTabName();
/* 430 */     return create(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized KeyTab create(String paramString)
/*     */     throws IOException, RealmException
/*     */   {
/* 439 */     KeyTabOutputStream localKeyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(paramString));Object localObject1 = null;
/*     */     try {
/* 441 */       localKeyTabOutputStream.writeVersion(1282);
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/* 439 */       localObject1 = localThrowable2;throw localThrowable2;
/*     */     }
/*     */     finally {
/* 442 */       if (localKeyTabOutputStream != null) if (localObject1 != null) try { localKeyTabOutputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localKeyTabOutputStream.close(); }
/* 443 */     return new KeyTab(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void save()
/*     */     throws IOException
/*     */   {
/* 450 */     KeyTabOutputStream localKeyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(this.tabName));Object localObject1 = null;
/*     */     try {
/* 452 */       localKeyTabOutputStream.writeVersion(this.kt_vno);
/* 453 */       for (int i = 0; i < this.entries.size(); i++) {
/* 454 */         localKeyTabOutputStream.writeEntry((KeyTabEntry)this.entries.elementAt(i));
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/* 450 */       localObject1 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 456 */       if (localKeyTabOutputStream != null) { if (localObject1 != null) try { localKeyTabOutputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else { localKeyTabOutputStream.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int deleteEntries(PrincipalName paramPrincipalName, int paramInt1, int paramInt2)
/*     */   {
/* 467 */     int i = 0;
/*     */     
/*     */ 
/* 470 */     HashMap localHashMap = new HashMap();
/*     */     KeyTabEntry localKeyTabEntry;
/* 472 */     int k; for (int j = this.entries.size() - 1; j >= 0; j--) {
/* 473 */       localKeyTabEntry = (KeyTabEntry)this.entries.get(j);
/* 474 */       if ((paramPrincipalName.match(localKeyTabEntry.getService())) && (
/* 475 */         (paramInt1 == -1) || (localKeyTabEntry.keyType == paramInt1))) {
/* 476 */         if (paramInt2 == -2)
/*     */         {
/*     */ 
/* 479 */           if (localHashMap.containsKey(Integer.valueOf(localKeyTabEntry.keyType))) {
/* 480 */             k = ((Integer)localHashMap.get(Integer.valueOf(localKeyTabEntry.keyType))).intValue();
/* 481 */             if (localKeyTabEntry.keyVersion > k) {
/* 482 */               localHashMap.put(Integer.valueOf(localKeyTabEntry.keyType), Integer.valueOf(localKeyTabEntry.keyVersion));
/*     */             }
/*     */           } else {
/* 485 */             localHashMap.put(Integer.valueOf(localKeyTabEntry.keyType), Integer.valueOf(localKeyTabEntry.keyVersion));
/*     */           }
/* 487 */         } else if ((paramInt2 == -1) || (localKeyTabEntry.keyVersion == paramInt2)) {
/* 488 */           this.entries.removeElementAt(j);
/* 489 */           i++;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 496 */     if (paramInt2 == -2) {
/* 497 */       for (j = this.entries.size() - 1; j >= 0; j--) {
/* 498 */         localKeyTabEntry = (KeyTabEntry)this.entries.get(j);
/* 499 */         if ((paramPrincipalName.match(localKeyTabEntry.getService())) && (
/* 500 */           (paramInt1 == -1) || (localKeyTabEntry.keyType == paramInt1))) {
/* 501 */           k = ((Integer)localHashMap.get(Integer.valueOf(localKeyTabEntry.keyType))).intValue();
/* 502 */           if (localKeyTabEntry.keyVersion != k) {
/* 503 */             this.entries.removeElementAt(j);
/* 504 */             i++;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 510 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void createVersion(File paramFile)
/*     */     throws IOException
/*     */   {
/* 519 */     KeyTabOutputStream localKeyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(paramFile));Object localObject1 = null;
/*     */     try {
/* 521 */       localKeyTabOutputStream.write16(1282);
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/* 519 */       localObject1 = localThrowable2;throw localThrowable2;
/*     */     }
/*     */     finally {
/* 522 */       if (localKeyTabOutputStream != null) if (localObject1 != null) try { localKeyTabOutputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localKeyTabOutputStream.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\ktab\KeyTab.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
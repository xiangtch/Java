/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.security.DomainLoadStoreParameter;
/*     */ import java.security.Key;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStore.Builder;
/*     */ import java.security.KeyStore.LoadStoreParameter;
/*     */ import java.security.KeyStore.PasswordProtection;
/*     */ import java.security.KeyStore.ProtectionParameter;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.KeyStoreSpi;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Provider;
/*     */ import java.security.Security;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.AbstractMap.SimpleEntry;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import sun.security.util.PolicyUtil;
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
/*     */ abstract class DomainKeyStore
/*     */   extends KeyStoreSpi
/*     */ {
/*     */   private static final String ENTRY_NAME_SEPARATOR = "entrynameseparator";
/*     */   private static final String KEYSTORE_PROVIDER_NAME = "keystoreprovidername";
/*     */   private static final String KEYSTORE_TYPE = "keystoretype";
/*     */   private static final String KEYSTORE_URI = "keystoreuri";
/*     */   private static final String KEYSTORE_PASSWORD_ENV = "keystorepasswordenv";
/*     */   private static final String REGEX_META = ".$|()[{^?*+\\";
/*     */   private static final String DEFAULT_STREAM_PREFIX = "iostream";
/*     */   abstract String convertAlias(String paramString);
/*     */   
/*     */   public static final class DKS
/*     */     extends DomainKeyStore
/*     */   {
/*     */     String convertAlias(String paramString)
/*     */     {
/*  70 */       return paramString.toLowerCase(Locale.ENGLISH);
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
/*  86 */   private int streamCounter = 1;
/*  87 */   private String entryNameSeparator = " ";
/*  88 */   private String entryNameSeparatorRegEx = " ";
/*     */   
/*     */ 
/*     */ 
/*  92 */   private static final String DEFAULT_KEYSTORE_TYPE = ;
/*     */   
/*     */ 
/*  95 */   private final Map<String, KeyStore> keystores = new HashMap();
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
/*     */   public Key engineGetKey(String paramString, char[] paramArrayOfChar)
/*     */     throws NoSuchAlgorithmException, UnrecoverableKeyException
/*     */   {
/* 123 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
/* 124 */     Key localKey = null;
/*     */     try
/*     */     {
/* 127 */       str = (String)localSimpleEntry.getKey();
/* 128 */       for (KeyStore localKeyStore : (Collection)localSimpleEntry.getValue()) {
/* 129 */         localKey = localKeyStore.getKey(str, paramArrayOfChar);
/* 130 */         if (localKey != null)
/*     */           break;
/*     */       }
/*     */     } catch (KeyStoreException localKeyStoreException) {
/*     */       String str;
/* 135 */       throw new IllegalStateException(localKeyStoreException);
/*     */     }
/*     */     
/* 138 */     return localKey;
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
/*     */   public Certificate[] engineGetCertificateChain(String paramString)
/*     */   {
/* 155 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
/* 156 */     Certificate[] arrayOfCertificate = null;
/*     */     try
/*     */     {
/* 159 */       str = (String)localSimpleEntry.getKey();
/* 160 */       for (KeyStore localKeyStore : (Collection)localSimpleEntry.getValue()) {
/* 161 */         arrayOfCertificate = localKeyStore.getCertificateChain(str);
/* 162 */         if (arrayOfCertificate != null)
/*     */           break;
/*     */       }
/*     */     } catch (KeyStoreException localKeyStoreException) {
/*     */       String str;
/* 167 */       throw new IllegalStateException(localKeyStoreException);
/*     */     }
/*     */     
/* 170 */     return arrayOfCertificate;
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
/*     */   public Certificate engineGetCertificate(String paramString)
/*     */   {
/* 191 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
/* 192 */     Certificate localCertificate = null;
/*     */     try
/*     */     {
/* 195 */       str = (String)localSimpleEntry.getKey();
/* 196 */       for (KeyStore localKeyStore : (Collection)localSimpleEntry.getValue()) {
/* 197 */         localCertificate = localKeyStore.getCertificate(str);
/* 198 */         if (localCertificate != null)
/*     */           break;
/*     */       }
/*     */     } catch (KeyStoreException localKeyStoreException) {
/*     */       String str;
/* 203 */       throw new IllegalStateException(localKeyStoreException);
/*     */     }
/*     */     
/* 206 */     return localCertificate;
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
/*     */   public Date engineGetCreationDate(String paramString)
/*     */   {
/* 220 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
/* 221 */     Date localDate = null;
/*     */     try
/*     */     {
/* 224 */       str = (String)localSimpleEntry.getKey();
/* 225 */       for (KeyStore localKeyStore : (Collection)localSimpleEntry.getValue()) {
/* 226 */         localDate = localKeyStore.getCreationDate(str);
/* 227 */         if (localDate != null)
/*     */           break;
/*     */       }
/*     */     } catch (KeyStoreException localKeyStoreException) {
/*     */       String str;
/* 232 */       throw new IllegalStateException(localKeyStoreException);
/*     */     }
/*     */     
/* 235 */     return localDate;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
/*     */     throws KeyStoreException
/*     */   {
/* 266 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoreForWriting(paramString);
/*     */     
/* 268 */     if (localSimpleEntry == null) {
/* 269 */       throw new KeyStoreException("Error setting key entry for '" + paramString + "'");
/*     */     }
/*     */     
/* 272 */     String str = (String)localSimpleEntry.getKey();
/* 273 */     Entry localEntry = (Entry)localSimpleEntry.getValue();
/* 274 */     ((KeyStore)localEntry.getValue()).setKeyEntry(str, paramKey, paramArrayOfChar, paramArrayOfCertificate);
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
/*     */   public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
/*     */     throws KeyStoreException
/*     */   {
/* 306 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoreForWriting(paramString);
/*     */     
/* 308 */     if (localSimpleEntry == null) {
/* 309 */       throw new KeyStoreException("Error setting protected key entry for '" + paramString + "'");
/*     */     }
/*     */     
/* 312 */     String str = (String)localSimpleEntry.getKey();
/* 313 */     Entry localEntry = (Entry)localSimpleEntry.getValue();
/* 314 */     ((KeyStore)localEntry.getValue()).setKeyEntry(str, paramArrayOfByte, paramArrayOfCertificate);
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
/*     */   public void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
/*     */     throws KeyStoreException
/*     */   {
/* 336 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoreForWriting(paramString);
/*     */     
/* 338 */     if (localSimpleEntry == null) {
/* 339 */       throw new KeyStoreException("Error setting certificate entry for '" + paramString + "'");
/*     */     }
/*     */     
/* 342 */     String str = (String)localSimpleEntry.getKey();
/* 343 */     Entry localEntry = (Entry)localSimpleEntry.getValue();
/* 344 */     ((KeyStore)localEntry.getValue()).setCertificateEntry(str, paramCertificate);
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
/*     */   public void engineDeleteEntry(String paramString)
/*     */     throws KeyStoreException
/*     */   {
/* 358 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoreForWriting(paramString);
/*     */     
/* 360 */     if (localSimpleEntry == null) {
/* 361 */       throw new KeyStoreException("Error deleting entry for '" + paramString + "'");
/*     */     }
/*     */     
/* 364 */     String str = (String)localSimpleEntry.getKey();
/* 365 */     Entry localEntry = (Entry)localSimpleEntry.getValue();
/* 366 */     ((KeyStore)localEntry.getValue()).deleteEntry(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> engineAliases()
/*     */   {
/* 376 */     final Iterator localIterator = this.keystores.entrySet().iterator();
/*     */     
/* 378 */     new Enumeration() {
/* 379 */       private int index = 0;
/* 380 */       private Entry<String, KeyStore> keystoresEntry = null;
/* 381 */       private String prefix = null;
/* 382 */       private Enumeration<String> aliases = null;
/*     */       
/*     */       public boolean hasMoreElements() {
/*     */         try {
/* 386 */           if (this.aliases == null) {
/* 387 */             if (localIterator.hasNext()) {
/* 388 */               this.keystoresEntry = ((Entry)localIterator.next());
/*     */               
/* 390 */               this.prefix = ((String)this.keystoresEntry.getKey() + DomainKeyStore.this.entryNameSeparator);
/* 391 */               this.aliases = ((KeyStore)this.keystoresEntry.getValue()).aliases();
/*     */             } else {
/* 393 */               return false;
/*     */             }
/*     */           }
/* 396 */           if (this.aliases.hasMoreElements()) {
/* 397 */             return true;
/*     */           }
/* 399 */           if (localIterator.hasNext()) {
/* 400 */             this.keystoresEntry = ((Entry)localIterator.next());
/*     */             
/* 402 */             this.prefix = ((String)this.keystoresEntry.getKey() + DomainKeyStore.this.entryNameSeparator);
/* 403 */             this.aliases = ((KeyStore)this.keystoresEntry.getValue()).aliases();
/*     */           } else {
/* 405 */             return false;
/*     */           }
/*     */         }
/*     */         catch (KeyStoreException localKeyStoreException) {
/* 409 */           return false;
/*     */         }
/*     */         
/* 412 */         return this.aliases.hasMoreElements();
/*     */       }
/*     */       
/*     */       public String nextElement() {
/* 416 */         if (hasMoreElements()) {
/* 417 */           return this.prefix + (String)this.aliases.nextElement();
/*     */         }
/* 419 */         throw new NoSuchElementException();
/*     */       }
/*     */     };
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
/*     */   public boolean engineContainsAlias(String paramString)
/*     */   {
/* 434 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
/*     */     try
/*     */     {
/* 437 */       str = (String)localSimpleEntry.getKey();
/* 438 */       for (KeyStore localKeyStore : (Collection)localSimpleEntry.getValue()) {
/* 439 */         if (localKeyStore.containsAlias(str))
/* 440 */           return true;
/*     */       }
/*     */     } catch (KeyStoreException localKeyStoreException) {
/*     */       String str;
/* 444 */       throw new IllegalStateException(localKeyStoreException);
/*     */     }
/*     */     
/* 447 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int engineSize()
/*     */   {
/* 457 */     int i = 0;
/*     */     try {
/* 459 */       for (KeyStore localKeyStore : this.keystores.values()) {
/* 460 */         i += localKeyStore.size();
/*     */       }
/*     */     } catch (KeyStoreException localKeyStoreException) {
/* 463 */       throw new IllegalStateException(localKeyStoreException);
/*     */     }
/*     */     
/* 466 */     return i;
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
/*     */   public boolean engineIsKeyEntry(String paramString)
/*     */   {
/* 479 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
/*     */     try
/*     */     {
/* 482 */       str = (String)localSimpleEntry.getKey();
/* 483 */       for (KeyStore localKeyStore : (Collection)localSimpleEntry.getValue()) {
/* 484 */         if (localKeyStore.isKeyEntry(str))
/* 485 */           return true;
/*     */       }
/*     */     } catch (KeyStoreException localKeyStoreException) {
/*     */       String str;
/* 489 */       throw new IllegalStateException(localKeyStoreException);
/*     */     }
/*     */     
/* 492 */     return false;
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
/*     */   public boolean engineIsCertificateEntry(String paramString)
/*     */   {
/* 505 */     AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
/*     */     try
/*     */     {
/* 508 */       str = (String)localSimpleEntry.getKey();
/* 509 */       for (KeyStore localKeyStore : (Collection)localSimpleEntry.getValue()) {
/* 510 */         if (localKeyStore.isCertificateEntry(str))
/* 511 */           return true;
/*     */       }
/*     */     } catch (KeyStoreException localKeyStoreException) {
/*     */       String str;
/* 515 */       throw new IllegalStateException(localKeyStoreException);
/*     */     }
/*     */     
/* 518 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private AbstractMap.SimpleEntry<String, Collection<KeyStore>> getKeystoresForReading(String paramString)
/*     */   {
/* 530 */     String[] arrayOfString = paramString.split(this.entryNameSeparatorRegEx, 2);
/* 531 */     if (arrayOfString.length == 2) {
/* 532 */       KeyStore localKeyStore = (KeyStore)this.keystores.get(arrayOfString[0]);
/* 533 */       if (localKeyStore != null) {
/* 534 */         return new AbstractMap.SimpleEntry(arrayOfString[1], 
/* 535 */           Collections.singleton(localKeyStore));
/*     */       }
/* 537 */     } else if (arrayOfString.length == 1)
/*     */     {
/* 539 */       return new AbstractMap.SimpleEntry(paramString, this.keystores.values());
/*     */     }
/* 541 */     return new AbstractMap.SimpleEntry("", 
/* 542 */       Collections.emptyList());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>> getKeystoreForWriting(String paramString)
/*     */   {
/* 553 */     String[] arrayOfString = paramString.split(this.entryNameSeparator, 2);
/* 554 */     if (arrayOfString.length == 2) {
/* 555 */       KeyStore localKeyStore = (KeyStore)this.keystores.get(arrayOfString[0]);
/* 556 */       if (localKeyStore != null) {
/* 557 */         return new AbstractMap.SimpleEntry(arrayOfString[1], new AbstractMap.SimpleEntry(arrayOfString[0], localKeyStore));
/*     */       }
/*     */     }
/*     */     
/* 561 */     return null;
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
/*     */   public String engineGetCertificateAlias(Certificate paramCertificate)
/*     */   {
/*     */     try
/*     */     {
/* 584 */       String str = null;
/* 585 */       for (KeyStore localKeyStore : this.keystores.values()) {
/* 586 */         if ((str = localKeyStore.getCertificateAlias(paramCertificate)) != null) {
/*     */           break;
/*     */         }
/*     */       }
/* 590 */       return str;
/*     */     }
/*     */     catch (KeyStoreException localKeyStoreException) {
/* 593 */       throw new IllegalStateException(localKeyStoreException);
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
/*     */ 
/*     */ 
/*     */   public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
/*     */     throws IOException, NoSuchAlgorithmException, CertificateException
/*     */   {
/*     */     try
/*     */     {
/* 616 */       if (this.keystores.size() == 1) {
/* 617 */         ((KeyStore)this.keystores.values().iterator().next()).store(paramOutputStream, paramArrayOfChar);
/* 618 */         return;
/*     */       }
/*     */     } catch (KeyStoreException localKeyStoreException) {
/* 621 */       throw new IllegalStateException(localKeyStoreException);
/*     */     }
/*     */     
/* 624 */     throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void engineStore(LoadStoreParameter paramLoadStoreParameter)
/*     */     throws IOException, NoSuchAlgorithmException, CertificateException
/*     */   {
/* 632 */     if ((paramLoadStoreParameter instanceof DomainLoadStoreParameter)) {
/* 633 */       DomainLoadStoreParameter localDomainLoadStoreParameter = (DomainLoadStoreParameter)paramLoadStoreParameter;
/*     */       
/* 635 */       List localList = getBuilders(localDomainLoadStoreParameter
/* 636 */         .getConfiguration(), localDomainLoadStoreParameter
/* 637 */         .getProtectionParams());
/*     */       
/* 639 */       for (KeyStoreBuilderComponents localKeyStoreBuilderComponents : localList)
/*     */       {
/*     */         try
/*     */         {
/* 643 */           ProtectionParameter localProtectionParameter = localKeyStoreBuilderComponents.protection;
/* 644 */           if (!(localProtectionParameter instanceof PasswordProtection)) {
/* 645 */             throw new KeyStoreException(new IllegalArgumentException("ProtectionParameter must be a KeyStore.PasswordProtection"));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 651 */           char[] arrayOfChar = ((PasswordProtection)localKeyStoreBuilderComponents.protection).getPassword();
/*     */           
/*     */ 
/* 654 */           KeyStore localKeyStore = (KeyStore)this.keystores.get(localKeyStoreBuilderComponents.name);
/*     */           
/* 656 */           FileOutputStream localFileOutputStream = new FileOutputStream(localKeyStoreBuilderComponents.file);Object localObject1 = null;
/*     */           try
/*     */           {
/* 659 */             localKeyStore.store(localFileOutputStream, arrayOfChar);
/*     */           }
/*     */           catch (Throwable localThrowable2)
/*     */           {
/* 656 */             localObject1 = localThrowable2;throw localThrowable2;
/*     */           }
/*     */           finally
/*     */           {
/* 660 */             if (localFileOutputStream != null) if (localObject1 != null) try { localFileOutputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localFileOutputStream.close();
/*     */           }
/* 662 */         } catch (KeyStoreException localKeyStoreException) { throw new IOException(localKeyStoreException);
/*     */         }
/*     */       }
/*     */     } else {
/* 666 */       throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
/*     */     throws IOException, NoSuchAlgorithmException, CertificateException
/*     */   {
/*     */     try
/*     */     {
/* 694 */       KeyStore localKeyStore = null;
/*     */       try
/*     */       {
/* 697 */         localKeyStore = KeyStore.getInstance("JKS");
/* 698 */         localKeyStore.load(paramInputStream, paramArrayOfChar);
/*     */       }
/*     */       catch (Exception localException2)
/*     */       {
/* 702 */         if (!"JKS".equalsIgnoreCase(DEFAULT_KEYSTORE_TYPE)) {
/* 703 */           localKeyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
/* 704 */           localKeyStore.load(paramInputStream, paramArrayOfChar);
/*     */         } else {
/* 706 */           throw localException2;
/*     */         }
/*     */       }
/* 709 */       String str = "iostream" + this.streamCounter++;
/* 710 */       this.keystores.put(str, localKeyStore);
/*     */     }
/*     */     catch (Exception localException1) {
/* 713 */       throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void engineLoad(LoadStoreParameter paramLoadStoreParameter)
/*     */     throws IOException, NoSuchAlgorithmException, CertificateException
/*     */   {
/* 723 */     if ((paramLoadStoreParameter instanceof DomainLoadStoreParameter)) {
/* 724 */       DomainLoadStoreParameter localDomainLoadStoreParameter = (DomainLoadStoreParameter)paramLoadStoreParameter;
/*     */       
/* 726 */       List localList = getBuilders(localDomainLoadStoreParameter
/* 727 */         .getConfiguration(), localDomainLoadStoreParameter
/* 728 */         .getProtectionParams());
/*     */       
/* 730 */       for (KeyStoreBuilderComponents localKeyStoreBuilderComponents : localList)
/*     */       {
/*     */         try
/*     */         {
/* 734 */           if (localKeyStoreBuilderComponents.file != null) {
/* 735 */             this.keystores.put(localKeyStoreBuilderComponents.name, 
/* 736 */               Builder.newInstance(localKeyStoreBuilderComponents.type, localKeyStoreBuilderComponents.provider, localKeyStoreBuilderComponents.file, localKeyStoreBuilderComponents.protection)
/*     */               
/*     */ 
/* 739 */               .getKeyStore());
/*     */           } else {
/* 741 */             this.keystores.put(localKeyStoreBuilderComponents.name, 
/* 742 */               Builder.newInstance(localKeyStoreBuilderComponents.type, localKeyStoreBuilderComponents.provider, localKeyStoreBuilderComponents.protection)
/*     */               
/* 744 */               .getKeyStore());
/*     */           }
/*     */         } catch (KeyStoreException localKeyStoreException) {
/* 747 */           throw new IOException(localKeyStoreException);
/*     */         }
/*     */       }
/*     */     } else {
/* 751 */       throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
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
/*     */   private List<KeyStoreBuilderComponents> getBuilders(URI paramURI, Map<String, ProtectionParameter> paramMap)
/*     */     throws IOException
/*     */   {
/* 765 */     PolicyParser localPolicyParser = new PolicyParser(true);
/* 766 */     Collection localCollection1 = null;
/* 767 */     ArrayList localArrayList = new ArrayList();
/* 768 */     String str1 = paramURI.getFragment();
/*     */     Object localObject1;
/*     */     try
/*     */     {
/* 772 */       InputStreamReader localInputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURI.toURL()), "UTF-8");localObject1 = null;
/* 773 */       try { localPolicyParser.read(localInputStreamReader);
/* 774 */         localCollection1 = localPolicyParser.getDomainEntries();
/*     */       }
/*     */       catch (Throwable localThrowable2)
/*     */       {
/* 770 */         localObject1 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/* 776 */         if (localInputStreamReader != null) if (localObject1 != null) try { localInputStreamReader.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localInputStreamReader.close();
/* 777 */       } } catch (MalformedURLException localMalformedURLException) { throw new IOException(localMalformedURLException);
/*     */     }
/*     */     catch (PolicyParser.ParsingException localParsingException) {
/* 780 */       throw new IOException(localParsingException);
/*     */     }
/*     */     
/* 783 */     for (Iterator localIterator = localCollection1.iterator(); localIterator.hasNext();) { localObject1 = (PolicyParser.DomainEntry)localIterator.next();
/* 784 */       Map localMap = ((PolicyParser.DomainEntry)localObject1).getProperties();
/*     */       
/* 786 */       if ((str1 == null) || 
/* 787 */         (str1.equalsIgnoreCase(((PolicyParser.DomainEntry)localObject1).getName())))
/*     */       {
/*     */ 
/*     */ 
/* 791 */         if (localMap.containsKey("entrynameseparator"))
/*     */         {
/* 793 */           this.entryNameSeparator = ((String)localMap.get("entrynameseparator"));
/*     */           
/* 795 */           char c = '\000';
/* 796 */           localObject3 = new StringBuilder();
/* 797 */           for (int i = 0; i < this.entryNameSeparator.length(); i++) {
/* 798 */             c = this.entryNameSeparator.charAt(i);
/* 799 */             if (".$|()[{^?*+\\".indexOf(c) != -1) {
/* 800 */               ((StringBuilder)localObject3).append('\\');
/*     */             }
/* 802 */             ((StringBuilder)localObject3).append(c);
/*     */           }
/* 804 */           this.entryNameSeparatorRegEx = ((StringBuilder)localObject3).toString();
/*     */         }
/*     */         
/*     */ 
/* 808 */         Collection localCollection2 = ((PolicyParser.DomainEntry)localObject1).getEntries();
/* 809 */         for (Object localObject3 = localCollection2.iterator(); ((Iterator)localObject3).hasNext();) { PolicyParser.KeyStoreEntry localKeyStoreEntry = (PolicyParser.KeyStoreEntry)((Iterator)localObject3).next();
/* 810 */           String str2 = localKeyStoreEntry.getName();
/* 811 */           HashMap localHashMap = new HashMap(localMap);
/*     */           
/* 813 */           localHashMap.putAll(localKeyStoreEntry.getProperties());
/*     */           
/* 815 */           String str3 = DEFAULT_KEYSTORE_TYPE;
/* 816 */           if (localHashMap.containsKey("keystoretype")) {
/* 817 */             str3 = (String)localHashMap.get("keystoretype");
/*     */           }
/*     */           
/* 820 */           Provider localProvider = null;
/* 821 */           if (localHashMap.containsKey("keystoreprovidername"))
/*     */           {
/* 823 */             localObject4 = (String)localHashMap.get("keystoreprovidername");
/*     */             
/* 825 */             localProvider = Security.getProvider((String)localObject4);
/* 826 */             if (localProvider == null) {
/* 827 */               throw new IOException("Error locating JCE provider: " + (String)localObject4);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 832 */           Object localObject4 = null;
/* 833 */           if (localHashMap.containsKey("keystoreuri")) {
/* 834 */             localObject5 = (String)localHashMap.get("keystoreuri");
/*     */             try
/*     */             {
/* 837 */               if (((String)localObject5).startsWith("file://")) {
/* 838 */                 localObject4 = new File(new URI((String)localObject5));
/*     */               } else {
/* 840 */                 localObject4 = new File((String)localObject5);
/*     */               }
/*     */             }
/*     */             catch (URISyntaxException|IllegalArgumentException localURISyntaxException) {
/* 844 */               throw new IOException("Error processing keystore property: keystoreURI=\"" + (String)localObject5 + "\"", localURISyntaxException);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 850 */           Object localObject5 = null;
/* 851 */           if (paramMap.containsKey(str2)) {
/* 852 */             localObject5 = (ProtectionParameter)paramMap.get(str2);
/*     */           }
/* 854 */           else if (localHashMap.containsKey("keystorepasswordenv")) {
/* 855 */             String str4 = (String)localHashMap.get("keystorepasswordenv");
/* 856 */             String str5 = System.getenv(str4);
/* 857 */             if (str5 != null)
/*     */             {
/* 859 */               localObject5 = new PasswordProtection(str5.toCharArray());
/*     */             } else {
/* 861 */               throw new IOException("Error processing keystore property: keystorePasswordEnv=\"" + str4 + "\"");
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 866 */             localObject5 = new PasswordProtection(null);
/*     */           }
/*     */           
/* 869 */           localArrayList.add(new KeyStoreBuilderComponents(str2, str3, localProvider, (File)localObject4, (ProtectionParameter)localObject5));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 875 */     if (localArrayList.isEmpty()) {
/* 876 */       throw new IOException("Error locating domain configuration data for: " + paramURI);
/*     */     }
/*     */     
/*     */ 
/* 880 */     return localArrayList;
/*     */   }
/*     */   
/*     */ 
/*     */   class KeyStoreBuilderComponents
/*     */   {
/*     */     String name;
/*     */     
/*     */     String type;
/*     */     Provider provider;
/*     */     File file;
/*     */     ProtectionParameter protection;
/*     */     
/*     */     KeyStoreBuilderComponents(String paramString1, String paramString2, Provider paramProvider, File paramFile, ProtectionParameter paramProtectionParameter)
/*     */     {
/* 895 */       this.name = paramString1;
/* 896 */       this.type = paramString2;
/* 897 */       this.provider = paramProvider;
/* 898 */       this.file = paramFile;
/* 899 */       this.protection = paramProtectionParameter;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\DomainKeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
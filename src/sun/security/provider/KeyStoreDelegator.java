/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.Key;
/*     */ import java.security.KeyStore.Entry;
/*     */ import java.security.KeyStore.ProtectionParameter;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.KeyStoreSpi;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Security;
/*     */ import java.security.UnrecoverableEntryException;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import sun.security.util.Debug;
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
/*     */ class KeyStoreDelegator
/*     */   extends KeyStoreSpi
/*     */ {
/*     */   private static final String KEYSTORE_TYPE_COMPAT = "keystore.type.compat";
/*  46 */   private static final Debug debug = Debug.getInstance("keystore");
/*     */   
/*     */   private final String primaryType;
/*     */   
/*     */   private final String secondaryType;
/*     */   
/*     */   private final Class<? extends KeyStoreSpi> primaryKeyStore;
/*     */   private final Class<? extends KeyStoreSpi> secondaryKeyStore;
/*     */   private String type;
/*     */   private KeyStoreSpi keystore;
/*  56 */   private boolean compatModeEnabled = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KeyStoreDelegator(String paramString1, Class<? extends KeyStoreSpi> paramClass1, String paramString2, Class<? extends KeyStoreSpi> paramClass2)
/*     */   {
/*  66 */     this.compatModeEnabled = "true".equalsIgnoreCase(
/*  67 */       (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public String run()
/*     */         {
/*  70 */           return Security.getProperty("keystore.type.compat");
/*     */         }
/*     */       }));
/*     */     
/*     */ 
/*  75 */     if (this.compatModeEnabled) {
/*  76 */       this.primaryType = paramString1;
/*  77 */       this.secondaryType = paramString2;
/*  78 */       this.primaryKeyStore = paramClass1;
/*  79 */       this.secondaryKeyStore = paramClass2;
/*     */     } else {
/*  81 */       this.primaryType = paramString1;
/*  82 */       this.secondaryType = null;
/*  83 */       this.primaryKeyStore = paramClass1;
/*  84 */       this.secondaryKeyStore = null;
/*     */       
/*  86 */       if (debug != null) {
/*  87 */         debug.println("WARNING: compatibility mode disabled for " + paramString1 + " and " + paramString2 + " keystore types");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Key engineGetKey(String paramString, char[] paramArrayOfChar)
/*     */     throws NoSuchAlgorithmException, UnrecoverableKeyException
/*     */   {
/*  96 */     return this.keystore.engineGetKey(paramString, paramArrayOfChar);
/*     */   }
/*     */   
/*     */   public Certificate[] engineGetCertificateChain(String paramString)
/*     */   {
/* 101 */     return this.keystore.engineGetCertificateChain(paramString);
/*     */   }
/*     */   
/*     */   public Certificate engineGetCertificate(String paramString)
/*     */   {
/* 106 */     return this.keystore.engineGetCertificate(paramString);
/*     */   }
/*     */   
/*     */   public Date engineGetCreationDate(String paramString)
/*     */   {
/* 111 */     return this.keystore.engineGetCreationDate(paramString);
/*     */   }
/*     */   
/*     */   public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
/*     */     throws KeyStoreException
/*     */   {
/* 117 */     this.keystore.engineSetKeyEntry(paramString, paramKey, paramArrayOfChar, paramArrayOfCertificate);
/*     */   }
/*     */   
/*     */   public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
/*     */     throws KeyStoreException
/*     */   {
/* 123 */     this.keystore.engineSetKeyEntry(paramString, paramArrayOfByte, paramArrayOfCertificate);
/*     */   }
/*     */   
/*     */   public void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
/*     */     throws KeyStoreException
/*     */   {
/* 129 */     this.keystore.engineSetCertificateEntry(paramString, paramCertificate);
/*     */   }
/*     */   
/*     */   public void engineDeleteEntry(String paramString) throws KeyStoreException
/*     */   {
/* 134 */     this.keystore.engineDeleteEntry(paramString);
/*     */   }
/*     */   
/*     */   public Enumeration<String> engineAliases()
/*     */   {
/* 139 */     return this.keystore.engineAliases();
/*     */   }
/*     */   
/*     */   public boolean engineContainsAlias(String paramString)
/*     */   {
/* 144 */     return this.keystore.engineContainsAlias(paramString);
/*     */   }
/*     */   
/*     */   public int engineSize()
/*     */   {
/* 149 */     return this.keystore.engineSize();
/*     */   }
/*     */   
/*     */   public boolean engineIsKeyEntry(String paramString)
/*     */   {
/* 154 */     return this.keystore.engineIsKeyEntry(paramString);
/*     */   }
/*     */   
/*     */   public boolean engineIsCertificateEntry(String paramString)
/*     */   {
/* 159 */     return this.keystore.engineIsCertificateEntry(paramString);
/*     */   }
/*     */   
/*     */   public String engineGetCertificateAlias(Certificate paramCertificate)
/*     */   {
/* 164 */     return this.keystore.engineGetCertificateAlias(paramCertificate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public KeyStore.Entry engineGetEntry(String paramString, KeyStore.ProtectionParameter paramProtectionParameter)
/*     */     throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException
/*     */   {
/* 172 */     return this.keystore.engineGetEntry(paramString, paramProtectionParameter);
/*     */   }
/*     */   
/*     */ 
/*     */   public void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter)
/*     */     throws KeyStoreException
/*     */   {
/* 179 */     this.keystore.engineSetEntry(paramString, paramEntry, paramProtectionParameter);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean engineEntryInstanceOf(String paramString, Class<? extends KeyStore.Entry> paramClass)
/*     */   {
/* 185 */     return this.keystore.engineEntryInstanceOf(paramString, paramClass);
/*     */   }
/*     */   
/*     */ 
/*     */   public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
/*     */     throws IOException, NoSuchAlgorithmException, CertificateException
/*     */   {
/* 192 */     if (debug != null) {
/* 193 */       debug.println("Storing keystore in " + this.type + " format");
/*     */     }
/* 195 */     this.keystore.engineStore(paramOutputStream, paramArrayOfChar);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
/*     */     throws IOException, NoSuchAlgorithmException, CertificateException
/*     */   {
/* 203 */     if ((paramInputStream == null) || (!this.compatModeEnabled)) {
/*     */       try {
/* 205 */         this.keystore = ((KeyStoreSpi)this.primaryKeyStore.newInstance());
/*     */       }
/*     */       catch (InstantiationException|IllegalAccessException localInstantiationException1) {}
/*     */       
/*     */ 
/* 210 */       this.type = this.primaryType;
/*     */       
/* 212 */       if ((debug != null) && (paramInputStream == null)) {
/* 213 */         debug.println("Creating a new keystore in " + this.type + " format");
/*     */       }
/* 215 */       this.keystore.engineLoad(paramInputStream, paramArrayOfChar);
/*     */     }
/*     */     else
/*     */     {
/* 219 */       BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream);
/* 220 */       localBufferedInputStream.mark(Integer.MAX_VALUE);
/*     */       try {
/* 222 */         this.keystore = ((KeyStoreSpi)this.primaryKeyStore.newInstance());
/* 223 */         this.type = this.primaryType;
/* 224 */         this.keystore.engineLoad(localBufferedInputStream, paramArrayOfChar);
/*     */ 
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 229 */         if (((localException instanceof IOException)) && 
/* 230 */           ((localException.getCause() instanceof UnrecoverableKeyException))) {
/* 231 */           throw ((IOException)localException);
/*     */         }
/*     */         try
/*     */         {
/* 235 */           this.keystore = ((KeyStoreSpi)this.secondaryKeyStore.newInstance());
/* 236 */           this.type = this.secondaryType;
/* 237 */           localBufferedInputStream.reset();
/* 238 */           this.keystore.engineLoad(localBufferedInputStream, paramArrayOfChar);
/*     */           
/* 240 */           if (debug != null) {
/* 241 */             debug.println("WARNING: switching from " + this.primaryType + " to " + this.secondaryType + " keystore file format has altered the keystore security level");
/*     */ 
/*     */ 
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         catch (InstantiationException|IllegalAccessException localInstantiationException2) {}catch (IOException|NoSuchAlgorithmException|CertificateException localIOException)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 256 */           if (((localIOException instanceof IOException)) && 
/* 257 */             ((localIOException.getCause() instanceof UnrecoverableKeyException)))
/*     */           {
/* 259 */             throw ((IOException)localIOException);
/*     */           }
/*     */           
/* 262 */           if ((localException instanceof IOException))
/* 263 */             throw ((IOException)localException);
/* 264 */           if ((localException instanceof CertificateException))
/* 265 */             throw ((CertificateException)localException);
/* 266 */           if ((localException instanceof NoSuchAlgorithmException)) {
/* 267 */             throw ((NoSuchAlgorithmException)localException);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 272 */       if (debug != null) {
/* 273 */         debug.println("Loaded a keystore in " + this.type + " format");
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\KeyStoreDelegator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
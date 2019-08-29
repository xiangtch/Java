/*      */ package sun.security.pkcs12;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.security.AccessController;
/*      */ import java.security.AlgorithmParameters;
/*      */ import java.security.InvalidAlgorithmParameterException;
/*      */ import java.security.Key;
/*      */ import java.security.KeyFactory;
/*      */ import java.security.KeyStore.Entry;
/*      */ import java.security.KeyStore.Entry.Attribute;
/*      */ import java.security.KeyStore.PasswordProtection;
/*      */ import java.security.KeyStore.PrivateKeyEntry;
/*      */ import java.security.KeyStore.ProtectionParameter;
/*      */ import java.security.KeyStore.SecretKeyEntry;
/*      */ import java.security.KeyStore.TrustedCertificateEntry;
/*      */ import java.security.KeyStoreException;
/*      */ import java.security.KeyStoreSpi;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PKCS12Attribute;
/*      */ import java.security.PrivateKey;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.SecureRandom;
/*      */ import java.security.Security;
/*      */ import java.security.UnrecoverableEntryException;
/*      */ import java.security.UnrecoverableKeyException;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.security.spec.AlgorithmParameterSpec;
/*      */ import java.security.spec.InvalidParameterSpecException;
/*      */ import java.security.spec.KeySpec;
/*      */ import java.security.spec.PKCS8EncodedKeySpec;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.Mac;
/*      */ import javax.crypto.SecretKey;
/*      */ import javax.crypto.SecretKeyFactory;
/*      */ import javax.crypto.spec.PBEKeySpec;
/*      */ import javax.crypto.spec.PBEParameterSpec;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import javax.security.auth.DestroyFailedException;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.security.pkcs.ContentInfo;
/*      */ import sun.security.pkcs.EncryptedPrivateKeyInfo;
/*      */ import sun.security.util.Debug;
/*      */ import sun.security.util.DerInputStream;
/*      */ import sun.security.util.DerOutputStream;
/*      */ import sun.security.util.DerValue;
/*      */ import sun.security.util.ObjectIdentifier;
/*      */ import sun.security.x509.AlgorithmId;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class PKCS12KeyStore
/*      */   extends KeyStoreSpi
/*      */ {
/*      */   public static final int VERSION_3 = 3;
/*  136 */   private static final String[] KEY_PROTECTION_ALGORITHM = { "keystore.pkcs12.keyProtectionAlgorithm", "keystore.PKCS12.keyProtectionAlgorithm" };
/*      */   
/*      */ 
/*      */   private static final int MAX_ITERATION_COUNT = 5000000;
/*      */   
/*      */   private static final int PBE_ITERATION_COUNT = 50000;
/*      */   
/*      */   private static final int MAC_ITERATION_COUNT = 100000;
/*      */   
/*      */   private static final int SALT_LEN = 20;
/*      */   
/*  147 */   private static final String[] CORE_ATTRIBUTES = { "1.2.840.113549.1.9.20", "1.2.840.113549.1.9.21", "2.16.840.1.113894.746875.1.1" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  153 */   private static final Debug debug = Debug.getInstance("pkcs12");
/*      */   
/*  155 */   private static final int[] keyBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 2 };
/*  156 */   private static final int[] certBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 3 };
/*  157 */   private static final int[] secretBag = { 1, 2, 840, 113549, 1, 12, 10, 1, 5 };
/*      */   
/*  159 */   private static final int[] pkcs9Name = { 1, 2, 840, 113549, 1, 9, 20 };
/*  160 */   private static final int[] pkcs9KeyId = { 1, 2, 840, 113549, 1, 9, 21 };
/*      */   
/*  162 */   private static final int[] pkcs9certType = { 1, 2, 840, 113549, 1, 9, 22, 1 };
/*      */   
/*  164 */   private static final int[] pbeWithSHAAnd40BitRC2CBC = { 1, 2, 840, 113549, 1, 12, 1, 6 };
/*      */   
/*  166 */   private static final int[] pbeWithSHAAnd3KeyTripleDESCBC = { 1, 2, 840, 113549, 1, 12, 1, 3 };
/*      */   
/*  168 */   private static final int[] pbes2 = { 1, 2, 840, 113549, 1, 5, 13 };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  174 */   private static final int[] TrustedKeyUsage = { 2, 16, 840, 1, 113894, 746875, 1, 1 };
/*      */   
/*  176 */   private static final int[] AnyExtendedKeyUsage = { 2, 5, 29, 37, 0 };
/*      */   
/*      */   private static ObjectIdentifier PKCS8ShroudedKeyBag_OID;
/*      */   
/*      */   private static ObjectIdentifier CertBag_OID;
/*      */   private static ObjectIdentifier SecretBag_OID;
/*      */   private static ObjectIdentifier PKCS9FriendlyName_OID;
/*      */   private static ObjectIdentifier PKCS9LocalKeyId_OID;
/*      */   private static ObjectIdentifier PKCS9CertType_OID;
/*      */   private static ObjectIdentifier pbeWithSHAAnd40BitRC2CBC_OID;
/*      */   private static ObjectIdentifier pbeWithSHAAnd3KeyTripleDESCBC_OID;
/*      */   private static ObjectIdentifier pbes2_OID;
/*      */   private static ObjectIdentifier TrustedKeyUsage_OID;
/*      */   private static ObjectIdentifier[] AnyUsage;
/*  190 */   private int counter = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  195 */   private int privateKeyCount = 0;
/*      */   
/*      */ 
/*  198 */   private int secretKeyCount = 0;
/*      */   
/*      */ 
/*  201 */   private int certificateCount = 0;
/*      */   private SecureRandom random;
/*      */   
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  208 */       PKCS8ShroudedKeyBag_OID = new ObjectIdentifier(keyBag);
/*  209 */       CertBag_OID = new ObjectIdentifier(certBag);
/*  210 */       SecretBag_OID = new ObjectIdentifier(secretBag);
/*  211 */       PKCS9FriendlyName_OID = new ObjectIdentifier(pkcs9Name);
/*  212 */       PKCS9LocalKeyId_OID = new ObjectIdentifier(pkcs9KeyId);
/*  213 */       PKCS9CertType_OID = new ObjectIdentifier(pkcs9certType);
/*  214 */       pbeWithSHAAnd40BitRC2CBC_OID = new ObjectIdentifier(pbeWithSHAAnd40BitRC2CBC);
/*      */       
/*  216 */       pbeWithSHAAnd3KeyTripleDESCBC_OID = new ObjectIdentifier(pbeWithSHAAnd3KeyTripleDESCBC);
/*      */       
/*  218 */       pbes2_OID = new ObjectIdentifier(pbes2);
/*  219 */       TrustedKeyUsage_OID = new ObjectIdentifier(TrustedKeyUsage);
/*  220 */       AnyUsage = new ObjectIdentifier[] { new ObjectIdentifier(AnyExtendedKeyUsage) };
/*      */     }
/*      */     catch (IOException localIOException) {}
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
/*      */   private static class KeyEntry
/*      */     extends PKCS12KeyStore.Entry
/*      */   {
/*  236 */     private KeyEntry() { super(); } }
/*      */   
/*      */   private static class PrivateKeyEntry extends KeyEntry { byte[] protectedPrivKey;
/*      */     
/*  240 */     private PrivateKeyEntry() { super(); }
/*      */     
/*      */     Certificate[] chain; }
/*      */   
/*      */   private static class SecretKeyEntry extends KeyEntry { byte[] protectedSecretKey;
/*      */     
/*  246 */     private SecretKeyEntry() { super(); }
/*      */   }
/*      */   
/*      */   private static class CertEntry extends PKCS12KeyStore.Entry
/*      */   {
/*      */     final X509Certificate cert;
/*      */     ObjectIdentifier[] trustedKeyUsage;
/*      */     
/*      */     CertEntry(X509Certificate paramX509Certificate, byte[] paramArrayOfByte, String paramString)
/*      */     {
/*  256 */       this(paramX509Certificate, paramArrayOfByte, paramString, null, null);
/*      */     }
/*      */     
/*      */     CertEntry(X509Certificate paramX509Certificate, byte[] paramArrayOfByte, String paramString, ObjectIdentifier[] paramArrayOfObjectIdentifier, Set<? extends KeyStore.Entry.Attribute> paramSet)
/*      */     {
/*  261 */       super();
/*  262 */       this.date = new Date();
/*  263 */       this.cert = paramX509Certificate;
/*  264 */       this.keyId = paramArrayOfByte;
/*  265 */       this.alias = paramString;
/*  266 */       this.trustedKeyUsage = paramArrayOfObjectIdentifier;
/*  267 */       this.attributes = new HashSet();
/*  268 */       if (paramSet != null) {
/*  269 */         this.attributes.addAll(paramSet);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  279 */   private Map<String, Entry> entries = Collections.synchronizedMap(new LinkedHashMap());
/*      */   
/*  281 */   private ArrayList<KeyEntry> keyList = new ArrayList();
/*  282 */   private LinkedHashMap<X500Principal, X509Certificate> certsMap = new LinkedHashMap();
/*      */   
/*  284 */   private ArrayList<CertEntry> certEntries = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Key engineGetKey(String paramString, char[] paramArrayOfChar)
/*      */     throws NoSuchAlgorithmException, UnrecoverableKeyException
/*      */   {
/*  304 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/*  305 */     Object localObject1 = null;
/*      */     
/*  307 */     if ((localEntry == null) || (!(localEntry instanceof KeyEntry))) {
/*  308 */       return null;
/*      */     }
/*      */     
/*      */ 
/*  312 */     byte[] arrayOfByte1 = null;
/*  313 */     if ((localEntry instanceof PrivateKeyEntry)) {
/*  314 */       arrayOfByte1 = ((PrivateKeyEntry)localEntry).protectedPrivKey;
/*  315 */     } else if ((localEntry instanceof SecretKeyEntry)) {
/*  316 */       arrayOfByte1 = ((SecretKeyEntry)localEntry).protectedSecretKey;
/*      */     } else {
/*  318 */       throw new UnrecoverableKeyException("Error locating key");
/*      */     }
/*      */     
/*      */     byte[] arrayOfByte2;
/*      */     
/*      */     ObjectIdentifier localObjectIdentifier;
/*      */     AlgorithmParameters localAlgorithmParameters;
/*      */     try
/*      */     {
/*  327 */       EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte1);
/*      */       
/*  329 */       arrayOfByte2 = localEncryptedPrivateKeyInfo.getEncryptedData();
/*      */       
/*      */ 
/*  332 */       localObject2 = new DerValue(localEncryptedPrivateKeyInfo.getAlgorithm().encode());
/*  333 */       DerInputStream localDerInputStream = ((DerValue)localObject2).toDerInputStream();
/*  334 */       localObjectIdentifier = localDerInputStream.getOID();
/*  335 */       localAlgorithmParameters = parseAlgParameters(localObjectIdentifier, localDerInputStream);
/*      */     }
/*      */     catch (IOException localIOException) {
/*  338 */       Object localObject2 = new UnrecoverableKeyException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo: " + localIOException);
/*      */       
/*      */ 
/*  341 */       ((UnrecoverableKeyException)localObject2).initCause(localIOException);
/*  342 */       throw ((Throwable)localObject2);
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  347 */       int i = 0;
/*      */       
/*  349 */       if (localAlgorithmParameters != null) {
/*      */         PBEParameterSpec localPBEParameterSpec;
/*      */         try {
/*  352 */           localPBEParameterSpec = (PBEParameterSpec)localAlgorithmParameters.getParameterSpec(PBEParameterSpec.class);
/*      */         } catch (InvalidParameterSpecException localInvalidParameterSpecException) {
/*  354 */           throw new IOException("Invalid PBE algorithm parameters");
/*      */         }
/*  356 */         i = localPBEParameterSpec.getIterationCount();
/*      */         
/*  358 */         if (i > 5000000) {
/*  359 */           throw new IOException("PBE iteration count too large");
/*      */         }
/*      */       }
/*      */       
/*      */       byte[] arrayOfByte3;
/*      */       
/*      */       try
/*      */       {
/*  367 */         SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
/*  368 */         localObject3 = Cipher.getInstance(
/*  369 */           mapPBEParamsToAlgorithm(localObjectIdentifier, localAlgorithmParameters));
/*  370 */         ((Cipher)localObject3).init(2, localSecretKey, localAlgorithmParameters);
/*  371 */         arrayOfByte3 = ((Cipher)localObject3).doFinal(arrayOfByte2);
/*      */       }
/*      */       catch (Exception localException2) {
/*  374 */         while (paramArrayOfChar.length == 0)
/*      */         {
/*      */ 
/*  377 */           paramArrayOfChar = new char[1];
/*      */         }
/*      */         
/*  380 */         throw localException2;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  388 */       DerValue localDerValue = new DerValue(arrayOfByte3);
/*  389 */       Object localObject3 = localDerValue.toDerInputStream();
/*  390 */       int j = ((DerInputStream)localObject3).getInteger();
/*  391 */       DerValue[] arrayOfDerValue = ((DerInputStream)localObject3).getSequence(2);
/*  392 */       AlgorithmId localAlgorithmId = new AlgorithmId(arrayOfDerValue[0].getOID());
/*  393 */       String str = localAlgorithmId.getName();
/*      */       Object localObject4;
/*      */       Object localObject5;
/*  396 */       if ((localEntry instanceof PrivateKeyEntry)) {
/*  397 */         localObject4 = KeyFactory.getInstance(str);
/*  398 */         localObject5 = new PKCS8EncodedKeySpec(arrayOfByte3);
/*  399 */         localObject1 = ((KeyFactory)localObject4).generatePrivate((KeySpec)localObject5);
/*      */         
/*  401 */         if (debug != null) {
/*  402 */           debug.println("Retrieved a protected private key at alias '" + paramString + "' (" + new AlgorithmId(localObjectIdentifier)
/*      */           
/*  404 */             .getName() + " iterations: " + i + ")");
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/*  410 */         localObject4 = ((DerInputStream)localObject3).getOctetString();
/*  411 */         localObject5 = new SecretKeySpec((byte[])localObject4, str);
/*      */         
/*      */ 
/*      */ 
/*  415 */         if (str.startsWith("PBE"))
/*      */         {
/*  417 */           SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance(str);
/*      */           
/*  419 */           KeySpec localKeySpec = localSecretKeyFactory.getKeySpec((SecretKey)localObject5, PBEKeySpec.class);
/*  420 */           localObject1 = localSecretKeyFactory.generateSecret(localKeySpec);
/*      */         } else {
/*  422 */           localObject1 = localObject5;
/*      */         }
/*      */         
/*  425 */         if (debug != null) {
/*  426 */           debug.println("Retrieved a protected secret key at alias '" + paramString + "' (" + new AlgorithmId(localObjectIdentifier)
/*      */           
/*  428 */             .getName() + " iterations: " + i + ")");
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     catch (Exception localException1)
/*      */     {
/*  435 */       UnrecoverableKeyException localUnrecoverableKeyException = new UnrecoverableKeyException("Get Key failed: " + localException1.getMessage());
/*  436 */       localUnrecoverableKeyException.initCause(localException1);
/*  437 */       throw localUnrecoverableKeyException;
/*      */     }
/*  439 */     return (Key)localObject1;
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
/*      */   public Certificate[] engineGetCertificateChain(String paramString)
/*      */   {
/*  454 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/*  455 */     if ((localEntry != null) && ((localEntry instanceof PrivateKeyEntry))) {
/*  456 */       if (((PrivateKeyEntry)localEntry).chain == null) {
/*  457 */         return null;
/*      */       }
/*      */       
/*  460 */       if (debug != null) {
/*  461 */         debug.println("Retrieved a " + ((PrivateKeyEntry)localEntry).chain.length + "-certificate chain at alias '" + paramString + "'");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  466 */       return (Certificate[])((PrivateKeyEntry)localEntry).chain.clone();
/*      */     }
/*      */     
/*  469 */     return null;
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
/*      */   public Certificate engineGetCertificate(String paramString)
/*      */   {
/*  489 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/*  490 */     if (localEntry == null) {
/*  491 */       return null;
/*      */     }
/*  493 */     if (((localEntry instanceof CertEntry)) && (((CertEntry)localEntry).trustedKeyUsage != null))
/*      */     {
/*      */ 
/*  496 */       if (debug != null) {
/*  497 */         if (Arrays.equals(AnyUsage, ((CertEntry)localEntry).trustedKeyUsage))
/*      */         {
/*  499 */           debug.println("Retrieved a certificate at alias '" + paramString + "' (trusted for any purpose)");
/*      */         }
/*      */         else {
/*  502 */           debug.println("Retrieved a certificate at alias '" + paramString + "' (trusted for limited purposes)");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  507 */       return ((CertEntry)localEntry).cert;
/*      */     }
/*  509 */     if ((localEntry instanceof PrivateKeyEntry)) {
/*  510 */       if (((PrivateKeyEntry)localEntry).chain == null) {
/*  511 */         return null;
/*      */       }
/*      */       
/*  514 */       if (debug != null) {
/*  515 */         debug.println("Retrieved a certificate at alias '" + paramString + "'");
/*      */       }
/*      */       
/*      */ 
/*  519 */       return ((PrivateKeyEntry)localEntry).chain[0];
/*      */     }
/*      */     
/*      */ 
/*  523 */     return null;
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
/*      */   public Date engineGetCreationDate(String paramString)
/*      */   {
/*  536 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/*  537 */     if (localEntry != null) {
/*  538 */       return new Date(localEntry.date.getTime());
/*      */     }
/*  540 */     return null;
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
/*      */   public synchronized void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
/*      */     throws KeyStoreException
/*      */   {
/*  570 */     KeyStore.PasswordProtection localPasswordProtection = new KeyStore.PasswordProtection(paramArrayOfChar);
/*      */     
/*      */     try
/*      */     {
/*  574 */       setKeyEntry(paramString, paramKey, localPasswordProtection, paramArrayOfCertificate, null); return;
/*      */     }
/*      */     finally {
/*      */       try {
/*  578 */         localPasswordProtection.destroy();
/*      */       }
/*      */       catch (DestroyFailedException localDestroyFailedException2) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setKeyEntry(String paramString, Key paramKey, KeyStore.PasswordProtection paramPasswordProtection, Certificate[] paramArrayOfCertificate, Set<KeyStore.Entry.Attribute> paramSet)
/*      */     throws KeyStoreException
/*      */   {
/*      */     try
/*      */     {
/*      */       Object localObject2;
/*      */       
/*      */ 
/*      */       Object localObject1;
/*      */       
/*  596 */       if ((paramKey instanceof PrivateKey)) {
/*  597 */         localObject2 = new PrivateKeyEntry(null);
/*  598 */         ((PrivateKeyEntry)localObject2).date = new Date();
/*      */         
/*  600 */         if ((paramKey.getFormat().equals("PKCS#8")) || 
/*  601 */           (paramKey.getFormat().equals("PKCS8")))
/*      */         {
/*  603 */           if (debug != null) {
/*  604 */             debug.println("Setting a protected private key at alias '" + paramString + "'");
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  611 */           ((PrivateKeyEntry)localObject2).protectedPrivKey = encryptPrivateKey(paramKey.getEncoded(), paramPasswordProtection);
/*      */         } else {
/*  613 */           throw new KeyStoreException("Private key is not encodedas PKCS#8");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  618 */         if (paramArrayOfCertificate != null)
/*      */         {
/*  620 */           if ((paramArrayOfCertificate.length > 1) && (!validateChain(paramArrayOfCertificate))) {
/*  621 */             throw new KeyStoreException("Certificate chain is not valid");
/*      */           }
/*  623 */           ((PrivateKeyEntry)localObject2).chain = ((Certificate[])paramArrayOfCertificate.clone());
/*  624 */           this.certificateCount += paramArrayOfCertificate.length;
/*      */           
/*  626 */           if (debug != null) {
/*  627 */             debug.println("Setting a " + paramArrayOfCertificate.length + "-certificate chain at alias '" + paramString + "'");
/*      */           }
/*      */         }
/*      */         
/*  631 */         this.privateKeyCount += 1;
/*  632 */         localObject1 = localObject2;
/*      */       }
/*  634 */       else if ((paramKey instanceof SecretKey)) {
/*  635 */         localObject2 = new SecretKeyEntry(null);
/*  636 */         ((SecretKeyEntry)localObject2).date = new Date();
/*      */         
/*      */ 
/*  639 */         DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  640 */         DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*  641 */         localDerOutputStream2.putInteger(0);
/*  642 */         AlgorithmId localAlgorithmId = AlgorithmId.get(paramKey.getAlgorithm());
/*  643 */         localAlgorithmId.encode(localDerOutputStream2);
/*  644 */         localDerOutputStream2.putOctetString(paramKey.getEncoded());
/*  645 */         localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*      */         
/*      */ 
/*      */ 
/*  649 */         ((SecretKeyEntry)localObject2).protectedSecretKey = encryptPrivateKey(localDerOutputStream1.toByteArray(), paramPasswordProtection);
/*      */         
/*  651 */         if (debug != null) {
/*  652 */           debug.println("Setting a protected secret key at alias '" + paramString + "'");
/*      */         }
/*      */         
/*  655 */         this.secretKeyCount += 1;
/*  656 */         localObject1 = localObject2;
/*      */       }
/*      */       else {
/*  659 */         throw new KeyStoreException("Unsupported Key type");
/*      */       }
/*      */       
/*  662 */       ((Entry)localObject1).attributes = new HashSet();
/*  663 */       if (paramSet != null) {
/*  664 */         ((Entry)localObject1).attributes.addAll(paramSet);
/*      */       }
/*      */       
/*  667 */       ((Entry)localObject1).keyId = ("Time " + ((Entry)localObject1).date.getTime()).getBytes("UTF8");
/*      */       
/*  669 */       ((Entry)localObject1).alias = paramString.toLowerCase(Locale.ENGLISH);
/*      */       
/*  671 */       this.entries.put(paramString.toLowerCase(Locale.ENGLISH), localObject1);
/*      */     }
/*      */     catch (Exception localException) {
/*  674 */       throw new KeyStoreException("Key protection  algorithm not found: " + localException, localException);
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
/*      */   public synchronized void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
/*      */     throws KeyStoreException
/*      */   {
/*      */     try
/*      */     {
/*  709 */       new EncryptedPrivateKeyInfo(paramArrayOfByte);
/*      */     } catch (IOException localIOException) {
/*  711 */       throw new KeyStoreException("Private key is not stored as PKCS#8 EncryptedPrivateKeyInfo: " + localIOException, localIOException);
/*      */     }
/*      */     
/*      */ 
/*  715 */     PrivateKeyEntry localPrivateKeyEntry = new PrivateKeyEntry(null);
/*  716 */     localPrivateKeyEntry.date = new Date();
/*      */     
/*  718 */     if (debug != null) {
/*  719 */       debug.println("Setting a protected private key at alias '" + paramString + "'");
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  725 */       localPrivateKeyEntry.keyId = ("Time " + localPrivateKeyEntry.date.getTime()).getBytes("UTF8");
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
/*      */     
/*      */ 
/*  730 */     localPrivateKeyEntry.alias = paramString.toLowerCase(Locale.ENGLISH);
/*      */     
/*  732 */     localPrivateKeyEntry.protectedPrivKey = ((byte[])paramArrayOfByte.clone());
/*  733 */     if (paramArrayOfCertificate != null)
/*      */     {
/*  735 */       if ((paramArrayOfCertificate.length > 1) && (!validateChain(paramArrayOfCertificate))) {
/*  736 */         throw new KeyStoreException("Certificate chain is not valid");
/*      */       }
/*      */       
/*  739 */       localPrivateKeyEntry.chain = ((Certificate[])paramArrayOfCertificate.clone());
/*  740 */       this.certificateCount += paramArrayOfCertificate.length;
/*      */       
/*  742 */       if (debug != null) {
/*  743 */         debug.println("Setting a " + localPrivateKeyEntry.chain.length + "-certificate chain at alias '" + paramString + "'");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  749 */     this.privateKeyCount += 1;
/*  750 */     this.entries.put(paramString.toLowerCase(Locale.ENGLISH), localPrivateKeyEntry);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] getSalt()
/*      */   {
/*  760 */     byte[] arrayOfByte = new byte[20];
/*  761 */     if (this.random == null) {
/*  762 */       this.random = new SecureRandom();
/*      */     }
/*  764 */     this.random.nextBytes(arrayOfByte);
/*  765 */     return arrayOfByte;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private AlgorithmParameters getPBEAlgorithmParameters(String paramString)
/*      */     throws IOException
/*      */   {
/*  774 */     AlgorithmParameters localAlgorithmParameters = null;
/*      */     
/*      */ 
/*      */ 
/*  778 */     PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(getSalt(), 50000);
/*      */     try {
/*  780 */       localAlgorithmParameters = AlgorithmParameters.getInstance(paramString);
/*  781 */       localAlgorithmParameters.init(localPBEParameterSpec);
/*      */     }
/*      */     catch (Exception localException) {
/*  784 */       throw new IOException("getPBEAlgorithmParameters failed: " + localException.getMessage(), localException);
/*      */     }
/*  786 */     return localAlgorithmParameters;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private AlgorithmParameters parseAlgParameters(ObjectIdentifier paramObjectIdentifier, DerInputStream paramDerInputStream)
/*      */     throws IOException
/*      */   {
/*  795 */     AlgorithmParameters localAlgorithmParameters = null;
/*      */     try {
/*      */       DerValue localDerValue;
/*  798 */       if (paramDerInputStream.available() == 0) {
/*  799 */         localDerValue = null;
/*      */       } else {
/*  801 */         localDerValue = paramDerInputStream.getDerValue();
/*  802 */         if (localDerValue.tag == 5) {
/*  803 */           localDerValue = null;
/*      */         }
/*      */       }
/*  806 */       if (localDerValue != null) {
/*  807 */         if (paramObjectIdentifier.equals(pbes2_OID)) {
/*  808 */           localAlgorithmParameters = AlgorithmParameters.getInstance("PBES2");
/*      */         } else {
/*  810 */           localAlgorithmParameters = AlgorithmParameters.getInstance("PBE");
/*      */         }
/*  812 */         localAlgorithmParameters.init(localDerValue.toByteArray());
/*      */       }
/*      */     }
/*      */     catch (Exception localException) {
/*  816 */       throw new IOException("parseAlgParameters failed: " + localException.getMessage(), localException);
/*      */     }
/*  818 */     return localAlgorithmParameters;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private SecretKey getPBEKey(char[] paramArrayOfChar)
/*      */     throws IOException
/*      */   {
/*  826 */     SecretKey localSecretKey = null;
/*      */     try
/*      */     {
/*  829 */       PBEKeySpec localPBEKeySpec = new PBEKeySpec(paramArrayOfChar);
/*  830 */       SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance("PBE");
/*  831 */       localSecretKey = localSecretKeyFactory.generateSecret(localPBEKeySpec);
/*  832 */       localPBEKeySpec.clearPassword();
/*      */     }
/*      */     catch (Exception localException) {
/*  835 */       throw new IOException("getSecretKey failed: " + localException.getMessage(), localException);
/*      */     }
/*  837 */     return localSecretKey;
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
/*      */   private byte[] encryptPrivateKey(byte[] paramArrayOfByte, KeyStore.PasswordProtection paramPasswordProtection)
/*      */     throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException
/*      */   {
/*  853 */     byte[] arrayOfByte1 = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  861 */       String str = paramPasswordProtection.getProtectionAlgorithm();
/*  862 */       if (str != null)
/*      */       {
/*  864 */         localObject2 = paramPasswordProtection.getProtectionParameters();
/*  865 */         if (localObject2 != null) {
/*  866 */           localObject1 = AlgorithmParameters.getInstance(str);
/*  867 */           ((AlgorithmParameters)localObject1).init((AlgorithmParameterSpec)localObject2);
/*      */         } else {
/*  869 */           localObject1 = getPBEAlgorithmParameters(str);
/*      */         }
/*      */       }
/*      */       else {
/*  873 */         str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public String run()
/*      */           {
/*  877 */             String str = Security.getProperty(
/*  878 */               PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[0]);
/*  879 */             if (str == null) {
/*  880 */               str = Security.getProperty(
/*  881 */                 PKCS12KeyStore.KEY_PROTECTION_ALGORITHM[1]);
/*      */             }
/*  883 */             return str;
/*      */           }
/*      */         });
/*  886 */         if ((str == null) || (str.isEmpty())) {
/*  887 */           str = "PBEWithSHA1AndDESede";
/*      */         }
/*  889 */         localObject1 = getPBEAlgorithmParameters(str);
/*      */       }
/*      */       
/*  892 */       Object localObject2 = mapPBEAlgorithmToOID(str);
/*  893 */       if (localObject2 == null) {
/*  894 */         throw new IOException("PBE algorithm '" + str + " 'is not supported for key entry protection");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  899 */       SecretKey localSecretKey = getPBEKey(paramPasswordProtection.getPassword());
/*  900 */       Cipher localCipher = Cipher.getInstance(str);
/*  901 */       localCipher.init(1, localSecretKey, (AlgorithmParameters)localObject1);
/*  902 */       byte[] arrayOfByte2 = localCipher.doFinal(paramArrayOfByte);
/*  903 */       AlgorithmId localAlgorithmId = new AlgorithmId((ObjectIdentifier)localObject2, localCipher.getParameters());
/*      */       
/*  905 */       if (debug != null) {
/*  906 */         debug.println("  (Cipher algorithm: " + localCipher.getAlgorithm() + ")");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  912 */       EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(localAlgorithmId, arrayOfByte2);
/*      */       
/*  914 */       arrayOfByte1 = localEncryptedPrivateKeyInfo.getEncoded();
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*  918 */       Object localObject1 = new UnrecoverableKeyException("Encrypt Private Key failed: " + localException.getMessage());
/*  919 */       ((UnrecoverableKeyException)localObject1).initCause(localException);
/*  920 */       throw ((Throwable)localObject1);
/*      */     }
/*      */     
/*  923 */     return arrayOfByte1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static ObjectIdentifier mapPBEAlgorithmToOID(String paramString)
/*      */     throws NoSuchAlgorithmException
/*      */   {
/*  932 */     if (paramString.toLowerCase(Locale.ENGLISH).startsWith("pbewithhmacsha")) {
/*  933 */       return pbes2_OID;
/*      */     }
/*  935 */     return AlgorithmId.get(paramString).getOID();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String mapPBEParamsToAlgorithm(ObjectIdentifier paramObjectIdentifier, AlgorithmParameters paramAlgorithmParameters)
/*      */     throws NoSuchAlgorithmException
/*      */   {
/*  944 */     if ((paramObjectIdentifier.equals(pbes2_OID)) && (paramAlgorithmParameters != null)) {
/*  945 */       return paramAlgorithmParameters.toString();
/*      */     }
/*  947 */     return paramObjectIdentifier.toString();
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
/*      */   public synchronized void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
/*      */     throws KeyStoreException
/*      */   {
/*  967 */     setCertEntry(paramString, paramCertificate, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setCertEntry(String paramString, Certificate paramCertificate, Set<KeyStore.Entry.Attribute> paramSet)
/*      */     throws KeyStoreException
/*      */   {
/*  976 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/*  977 */     if ((localEntry != null) && ((localEntry instanceof KeyEntry))) {
/*  978 */       throw new KeyStoreException("Cannot overwrite own certificate");
/*      */     }
/*      */     
/*  981 */     CertEntry localCertEntry = new CertEntry((X509Certificate)paramCertificate, null, paramString, AnyUsage, paramSet);
/*      */     
/*      */ 
/*  984 */     this.certificateCount += 1;
/*  985 */     this.entries.put(paramString, localCertEntry);
/*      */     
/*  987 */     if (debug != null) {
/*  988 */       debug.println("Setting a trusted certificate at alias '" + paramString + "'");
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
/*      */   public synchronized void engineDeleteEntry(String paramString)
/*      */     throws KeyStoreException
/*      */   {
/* 1003 */     if (debug != null) {
/* 1004 */       debug.println("Removing entry at alias '" + paramString + "'");
/*      */     }
/*      */     
/* 1007 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/* 1008 */     if ((localEntry instanceof PrivateKeyEntry)) {
/* 1009 */       PrivateKeyEntry localPrivateKeyEntry = (PrivateKeyEntry)localEntry;
/* 1010 */       if (localPrivateKeyEntry.chain != null) {
/* 1011 */         this.certificateCount -= localPrivateKeyEntry.chain.length;
/*      */       }
/* 1013 */       this.privateKeyCount -= 1;
/* 1014 */     } else if ((localEntry instanceof CertEntry)) {
/* 1015 */       this.certificateCount -= 1;
/* 1016 */     } else if ((localEntry instanceof SecretKeyEntry)) {
/* 1017 */       this.secretKeyCount -= 1;
/*      */     }
/* 1019 */     this.entries.remove(paramString.toLowerCase(Locale.ENGLISH));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Enumeration<String> engineAliases()
/*      */   {
/* 1028 */     return Collections.enumeration(this.entries.keySet());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean engineContainsAlias(String paramString)
/*      */   {
/* 1039 */     return this.entries.containsKey(paramString.toLowerCase(Locale.ENGLISH));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int engineSize()
/*      */   {
/* 1048 */     return this.entries.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean engineIsKeyEntry(String paramString)
/*      */   {
/* 1059 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/* 1060 */     if ((localEntry != null) && ((localEntry instanceof KeyEntry))) {
/* 1061 */       return true;
/*      */     }
/* 1063 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean engineIsCertificateEntry(String paramString)
/*      */   {
/* 1075 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/* 1076 */     if ((localEntry != null) && ((localEntry instanceof CertEntry)) && (((CertEntry)localEntry).trustedKeyUsage != null))
/*      */     {
/* 1078 */       return true;
/*      */     }
/* 1080 */     return false;
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
/*      */   public boolean engineEntryInstanceOf(String paramString, Class<? extends KeyStore.Entry> paramClass)
/*      */   {
/* 1103 */     if (paramClass == KeyStore.TrustedCertificateEntry.class) {
/* 1104 */       return engineIsCertificateEntry(paramString);
/*      */     }
/*      */     
/* 1107 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/* 1108 */     if (paramClass == KeyStore.PrivateKeyEntry.class) {
/* 1109 */       return (localEntry != null) && ((localEntry instanceof PrivateKeyEntry));
/*      */     }
/* 1111 */     if (paramClass == KeyStore.SecretKeyEntry.class) {
/* 1112 */       return (localEntry != null) && ((localEntry instanceof SecretKeyEntry));
/*      */     }
/* 1114 */     return false;
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
/*      */   public String engineGetCertificateAlias(Certificate paramCertificate)
/*      */   {
/* 1134 */     Object localObject = null;
/*      */     
/* 1136 */     for (Enumeration localEnumeration = engineAliases(); localEnumeration.hasMoreElements();) {
/* 1137 */       String str = (String)localEnumeration.nextElement();
/* 1138 */       Entry localEntry = (Entry)this.entries.get(str);
/* 1139 */       if ((localEntry instanceof PrivateKeyEntry)) {
/* 1140 */         if (((PrivateKeyEntry)localEntry).chain != null)
/* 1141 */           localObject = ((PrivateKeyEntry)localEntry).chain[0];
/*      */       } else {
/* 1143 */         if ((!(localEntry instanceof CertEntry)) || (((CertEntry)localEntry).trustedKeyUsage == null))
/*      */           continue;
/* 1145 */         localObject = ((CertEntry)localEntry).cert;
/*      */       }
/*      */       
/*      */ 
/* 1149 */       if ((localObject != null) && (((Certificate)localObject).equals(paramCertificate))) {
/* 1150 */         return str;
/*      */       }
/*      */     }
/* 1153 */     return null;
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
/*      */   public synchronized void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
/*      */     throws IOException, NoSuchAlgorithmException, CertificateException
/*      */   {
/* 1173 */     if (paramArrayOfChar == null) {
/* 1174 */       throw new IllegalArgumentException("password can't be null");
/*      */     }
/*      */     
/*      */ 
/* 1178 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*      */     
/*      */ 
/* 1181 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 1182 */     localDerOutputStream2.putInteger(3);
/* 1183 */     byte[] arrayOfByte1 = localDerOutputStream2.toByteArray();
/* 1184 */     localDerOutputStream1.write(arrayOfByte1);
/*      */     
/*      */ 
/* 1187 */     DerOutputStream localDerOutputStream3 = new DerOutputStream();
/*      */     
/*      */ 
/* 1190 */     DerOutputStream localDerOutputStream4 = new DerOutputStream();
/*      */     
/*      */ 
/* 1193 */     if ((this.privateKeyCount > 0) || (this.secretKeyCount > 0))
/*      */     {
/* 1195 */       if (debug != null) {
/* 1196 */         debug.println("Storing " + (this.privateKeyCount + this.secretKeyCount) + " protected key(s) in a PKCS#7 data");
/*      */       }
/*      */       
/*      */ 
/* 1200 */       localObject1 = createSafeContent();
/* 1201 */       localObject2 = new ContentInfo((byte[])localObject1);
/* 1202 */       ((ContentInfo)localObject2).encode(localDerOutputStream4);
/*      */     }
/*      */     
/*      */ 
/* 1206 */     if (this.certificateCount > 0)
/*      */     {
/* 1208 */       if (debug != null) {
/* 1209 */         debug.println("Storing " + this.certificateCount + " certificate(s) in a PKCS#7 encryptedData");
/*      */       }
/*      */       
/*      */ 
/* 1213 */       localObject1 = createEncryptedData(paramArrayOfChar);
/* 1214 */       localObject2 = new ContentInfo(ContentInfo.ENCRYPTED_DATA_OID, new DerValue((byte[])localObject1));
/*      */       
/*      */ 
/* 1217 */       ((ContentInfo)localObject2).encode(localDerOutputStream4);
/*      */     }
/*      */     
/*      */ 
/* 1221 */     Object localObject1 = new DerOutputStream();
/* 1222 */     ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream4);
/* 1223 */     Object localObject2 = ((DerOutputStream)localObject1).toByteArray();
/*      */     
/*      */ 
/* 1226 */     ContentInfo localContentInfo = new ContentInfo((byte[])localObject2);
/* 1227 */     localContentInfo.encode(localDerOutputStream3);
/* 1228 */     byte[] arrayOfByte2 = localDerOutputStream3.toByteArray();
/* 1229 */     localDerOutputStream1.write(arrayOfByte2);
/*      */     
/*      */ 
/* 1232 */     byte[] arrayOfByte3 = calculateMac(paramArrayOfChar, (byte[])localObject2);
/* 1233 */     localDerOutputStream1.write(arrayOfByte3);
/*      */     
/*      */ 
/* 1236 */     DerOutputStream localDerOutputStream5 = new DerOutputStream();
/* 1237 */     localDerOutputStream5.write((byte)48, localDerOutputStream1);
/* 1238 */     byte[] arrayOfByte4 = localDerOutputStream5.toByteArray();
/* 1239 */     paramOutputStream.write(arrayOfByte4);
/* 1240 */     paramOutputStream.flush();
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
/*      */   public KeyStore.Entry engineGetEntry(String paramString, KeyStore.ProtectionParameter paramProtectionParameter)
/*      */     throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException
/*      */   {
/* 1273 */     if (!engineContainsAlias(paramString)) {
/* 1274 */       return null;
/*      */     }
/*      */     
/* 1277 */     Entry localEntry = (Entry)this.entries.get(paramString.toLowerCase(Locale.ENGLISH));
/* 1278 */     if (paramProtectionParameter == null) {
/* 1279 */       if (engineIsCertificateEntry(paramString)) {
/* 1280 */         if (((localEntry instanceof CertEntry)) && (((CertEntry)localEntry).trustedKeyUsage != null))
/*      */         {
/*      */ 
/* 1283 */           if (debug != null) {
/* 1284 */             debug.println("Retrieved a trusted certificate at alias '" + paramString + "'");
/*      */           }
/*      */           
/*      */ 
/* 1288 */           return new KeyStore.TrustedCertificateEntry(((CertEntry)localEntry).cert, 
/* 1289 */             getAttributes(localEntry));
/*      */         }
/*      */       } else {
/* 1292 */         throw new UnrecoverableKeyException("requested entry requires a password");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1297 */     if ((paramProtectionParameter instanceof KeyStore.PasswordProtection)) {
/* 1298 */       if (engineIsCertificateEntry(paramString)) {
/* 1299 */         throw new UnsupportedOperationException("trusted certificate entries are not password-protected");
/*      */       }
/* 1301 */       if (engineIsKeyEntry(paramString)) {
/* 1302 */         KeyStore.PasswordProtection localPasswordProtection = (KeyStore.PasswordProtection)paramProtectionParameter;
/*      */         
/* 1304 */         char[] arrayOfChar = localPasswordProtection.getPassword();
/*      */         
/* 1306 */         Key localKey = engineGetKey(paramString, arrayOfChar);
/* 1307 */         if ((localKey instanceof PrivateKey)) {
/* 1308 */           Certificate[] arrayOfCertificate = engineGetCertificateChain(paramString);
/*      */           
/* 1310 */           return new KeyStore.PrivateKeyEntry((PrivateKey)localKey, arrayOfCertificate, 
/* 1311 */             getAttributes(localEntry));
/*      */         }
/* 1313 */         if ((localKey instanceof SecretKey))
/*      */         {
/* 1315 */           return new KeyStore.SecretKeyEntry((SecretKey)localKey, 
/* 1316 */             getAttributes(localEntry));
/*      */         }
/* 1318 */       } else if (!engineIsKeyEntry(paramString)) {
/* 1319 */         throw new UnsupportedOperationException("untrusted certificate entries are not password-protected");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1325 */     throw new UnsupportedOperationException();
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
/*      */   public synchronized void engineSetEntry(String paramString, KeyStore.Entry paramEntry, KeyStore.ProtectionParameter paramProtectionParameter)
/*      */     throws KeyStoreException
/*      */   {
/* 1351 */     if ((paramProtectionParameter != null) && (!(paramProtectionParameter instanceof KeyStore.PasswordProtection)))
/*      */     {
/* 1353 */       throw new KeyStoreException("unsupported protection parameter");
/*      */     }
/* 1355 */     KeyStore.PasswordProtection localPasswordProtection = null;
/* 1356 */     if (paramProtectionParameter != null) {
/* 1357 */       localPasswordProtection = (KeyStore.PasswordProtection)paramProtectionParameter;
/*      */     }
/*      */     
/*      */     Object localObject;
/* 1361 */     if ((paramEntry instanceof KeyStore.TrustedCertificateEntry)) {
/* 1362 */       if ((paramProtectionParameter != null) && (localPasswordProtection.getPassword() != null))
/*      */       {
/* 1364 */         throw new KeyStoreException("trusted certificate entries are not password-protected");
/*      */       }
/*      */       
/* 1367 */       localObject = (KeyStore.TrustedCertificateEntry)paramEntry;
/*      */       
/* 1369 */       setCertEntry(paramString, ((KeyStore.TrustedCertificateEntry)localObject).getTrustedCertificate(), ((KeyStore.TrustedCertificateEntry)localObject)
/* 1370 */         .getAttributes());
/*      */       
/* 1372 */       return;
/*      */     }
/* 1374 */     if ((paramEntry instanceof KeyStore.PrivateKeyEntry)) {
/* 1375 */       if ((localPasswordProtection == null) || (localPasswordProtection.getPassword() == null))
/*      */       {
/* 1377 */         throw new KeyStoreException("non-null password required to create PrivateKeyEntry");
/*      */       }
/*      */       
/* 1380 */       localObject = (KeyStore.PrivateKeyEntry)paramEntry;
/* 1381 */       setKeyEntry(paramString, ((KeyStore.PrivateKeyEntry)localObject).getPrivateKey(), localPasswordProtection, ((KeyStore.PrivateKeyEntry)localObject)
/* 1382 */         .getCertificateChain(), ((KeyStore.PrivateKeyEntry)localObject).getAttributes());
/*      */       
/* 1384 */       return;
/*      */     }
/* 1386 */     if ((paramEntry instanceof KeyStore.SecretKeyEntry)) {
/* 1387 */       if ((localPasswordProtection == null) || (localPasswordProtection.getPassword() == null))
/*      */       {
/* 1389 */         throw new KeyStoreException("non-null password required to create SecretKeyEntry");
/*      */       }
/*      */       
/* 1392 */       localObject = (KeyStore.SecretKeyEntry)paramEntry;
/* 1393 */       setKeyEntry(paramString, ((KeyStore.SecretKeyEntry)localObject).getSecretKey(), localPasswordProtection, (Certificate[])null, ((KeyStore.SecretKeyEntry)localObject)
/* 1394 */         .getAttributes());
/*      */       
/* 1396 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1401 */     throw new KeyStoreException("unsupported entry type: " + paramEntry.getClass().getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Set<KeyStore.Entry.Attribute> getAttributes(Entry paramEntry)
/*      */   {
/* 1409 */     if (paramEntry.attributes == null) {
/* 1410 */       paramEntry.attributes = new HashSet();
/*      */     }
/*      */     
/*      */ 
/* 1414 */     paramEntry.attributes.add(new PKCS12Attribute(PKCS9FriendlyName_OID
/* 1415 */       .toString(), paramEntry.alias));
/*      */     
/*      */ 
/* 1418 */     byte[] arrayOfByte = paramEntry.keyId;
/* 1419 */     if (arrayOfByte != null) {
/* 1420 */       paramEntry.attributes.add(new PKCS12Attribute(PKCS9LocalKeyId_OID
/* 1421 */         .toString(), Debug.toString(arrayOfByte)));
/*      */     }
/*      */     
/*      */ 
/* 1425 */     if ((paramEntry instanceof CertEntry)) {
/* 1426 */       ObjectIdentifier[] arrayOfObjectIdentifier = ((CertEntry)paramEntry).trustedKeyUsage;
/*      */       
/* 1428 */       if (arrayOfObjectIdentifier != null) {
/* 1429 */         if (arrayOfObjectIdentifier.length == 1) {
/* 1430 */           paramEntry.attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID
/* 1431 */             .toString(), arrayOfObjectIdentifier[0]
/* 1432 */             .toString()));
/*      */         } else {
/* 1434 */           paramEntry.attributes.add(new PKCS12Attribute(TrustedKeyUsage_OID
/* 1435 */             .toString(), 
/* 1436 */             Arrays.toString(arrayOfObjectIdentifier)));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1441 */     return paramEntry.attributes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private byte[] generateHash(byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/* 1449 */     byte[] arrayOfByte = null;
/*      */     try
/*      */     {
/* 1452 */       MessageDigest localMessageDigest = MessageDigest.getInstance("SHA1");
/* 1453 */       localMessageDigest.update(paramArrayOfByte);
/* 1454 */       arrayOfByte = localMessageDigest.digest();
/*      */     } catch (Exception localException) {
/* 1456 */       throw new IOException("generateHash failed: " + localException, localException);
/*      */     }
/* 1458 */     return arrayOfByte;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] calculateMac(char[] paramArrayOfChar, byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/* 1471 */     byte[] arrayOfByte1 = null;
/* 1472 */     String str = "SHA1";
/*      */     
/*      */     try
/*      */     {
/* 1476 */       byte[] arrayOfByte2 = getSalt();
/*      */       
/*      */ 
/* 1479 */       Mac localMac = Mac.getInstance("HmacPBESHA1");
/* 1480 */       PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(arrayOfByte2, 100000);
/*      */       
/* 1482 */       SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
/* 1483 */       localMac.init(localSecretKey, localPBEParameterSpec);
/* 1484 */       localMac.update(paramArrayOfByte);
/* 1485 */       byte[] arrayOfByte3 = localMac.doFinal();
/*      */       
/*      */ 
/* 1488 */       MacData localMacData = new MacData(str, arrayOfByte3, arrayOfByte2, 100000);
/*      */       
/* 1490 */       DerOutputStream localDerOutputStream = new DerOutputStream();
/* 1491 */       localDerOutputStream.write(localMacData.getEncoded());
/* 1492 */       arrayOfByte1 = localDerOutputStream.toByteArray();
/*      */     } catch (Exception localException) {
/* 1494 */       throw new IOException("calculateMac failed: " + localException, localException);
/*      */     }
/* 1496 */     return arrayOfByte1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean validateChain(Certificate[] paramArrayOfCertificate)
/*      */   {
/* 1505 */     for (int i = 0; i < paramArrayOfCertificate.length - 1; i++)
/*      */     {
/* 1507 */       X500Principal localX500Principal1 = ((X509Certificate)paramArrayOfCertificate[i]).getIssuerX500Principal();
/*      */       
/* 1509 */       X500Principal localX500Principal2 = ((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectX500Principal();
/* 1510 */       if (!localX500Principal1.equals(localX500Principal2)) {
/* 1511 */         return false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1517 */     HashSet localHashSet = new HashSet(Arrays.asList(paramArrayOfCertificate));
/* 1518 */     return localHashSet.size() == paramArrayOfCertificate.length;
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
/*      */   private byte[] getBagAttributes(String paramString, byte[] paramArrayOfByte, Set<KeyStore.Entry.Attribute> paramSet)
/*      */     throws IOException
/*      */   {
/* 1559 */     return getBagAttributes(paramString, paramArrayOfByte, null, paramSet);
/*      */   }
/*      */   
/*      */ 
/*      */   private byte[] getBagAttributes(String paramString, byte[] paramArrayOfByte, ObjectIdentifier[] paramArrayOfObjectIdentifier, Set<KeyStore.Entry.Attribute> paramSet)
/*      */     throws IOException
/*      */   {
/* 1566 */     byte[] arrayOfByte1 = null;
/* 1567 */     byte[] arrayOfByte2 = null;
/* 1568 */     byte[] arrayOfByte3 = null;
/*      */     
/*      */ 
/* 1571 */     if ((paramString == null) && (paramArrayOfByte == null) && (arrayOfByte3 == null)) {
/* 1572 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 1576 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*      */     Object localObject1;
/*      */     Object localObject2;
/* 1579 */     if (paramString != null) {
/* 1580 */       localDerOutputStream2 = new DerOutputStream();
/* 1581 */       localDerOutputStream2.putOID(PKCS9FriendlyName_OID);
/* 1582 */       localObject1 = new DerOutputStream();
/* 1583 */       localObject2 = new DerOutputStream();
/* 1584 */       ((DerOutputStream)localObject1).putBMPString(paramString);
/* 1585 */       localDerOutputStream2.write((byte)49, (DerOutputStream)localObject1);
/* 1586 */       ((DerOutputStream)localObject2).write((byte)48, localDerOutputStream2);
/* 1587 */       arrayOfByte2 = ((DerOutputStream)localObject2).toByteArray();
/*      */     }
/*      */     
/*      */ 
/* 1591 */     if (paramArrayOfByte != null) {
/* 1592 */       localDerOutputStream2 = new DerOutputStream();
/* 1593 */       localDerOutputStream2.putOID(PKCS9LocalKeyId_OID);
/* 1594 */       localObject1 = new DerOutputStream();
/* 1595 */       localObject2 = new DerOutputStream();
/* 1596 */       ((DerOutputStream)localObject1).putOctetString(paramArrayOfByte);
/* 1597 */       localDerOutputStream2.write((byte)49, (DerOutputStream)localObject1);
/* 1598 */       ((DerOutputStream)localObject2).write((byte)48, localDerOutputStream2);
/* 1599 */       arrayOfByte1 = ((DerOutputStream)localObject2).toByteArray();
/*      */     }
/*      */     
/*      */ 
/* 1603 */     if (paramArrayOfObjectIdentifier != null) {
/* 1604 */       localDerOutputStream2 = new DerOutputStream();
/* 1605 */       localDerOutputStream2.putOID(TrustedKeyUsage_OID);
/* 1606 */       localObject1 = new DerOutputStream();
/* 1607 */       localObject2 = new DerOutputStream();
/* 1608 */       for (ObjectIdentifier localObjectIdentifier : paramArrayOfObjectIdentifier) {
/* 1609 */         ((DerOutputStream)localObject1).putOID(localObjectIdentifier);
/*      */       }
/* 1611 */       localDerOutputStream2.write((byte)49, (DerOutputStream)localObject1);
/* 1612 */       ((DerOutputStream)localObject2).write((byte)48, localDerOutputStream2);
/* 1613 */       arrayOfByte3 = ((DerOutputStream)localObject2).toByteArray();
/*      */     }
/*      */     
/* 1616 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 1617 */     if (arrayOfByte2 != null) {
/* 1618 */       localDerOutputStream2.write(arrayOfByte2);
/*      */     }
/* 1620 */     if (arrayOfByte1 != null) {
/* 1621 */       localDerOutputStream2.write(arrayOfByte1);
/*      */     }
/* 1623 */     if (arrayOfByte3 != null) {
/* 1624 */       localDerOutputStream2.write(arrayOfByte3);
/*      */     }
/*      */     
/* 1627 */     if (paramSet != null) {
/* 1628 */       for (localObject1 = paramSet.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (KeyStore.Entry.Attribute)((Iterator)localObject1).next();
/* 1629 */         ??? = ((KeyStore.Entry.Attribute)localObject2).getName();
/*      */         
/* 1631 */         if ((!CORE_ATTRIBUTES[0].equals(???)) && 
/* 1632 */           (!CORE_ATTRIBUTES[1].equals(???)) && 
/* 1633 */           (!CORE_ATTRIBUTES[2].equals(???)))
/*      */         {
/*      */ 
/* 1636 */           localDerOutputStream2.write(((PKCS12Attribute)localObject2).getEncoded());
/*      */         }
/*      */       }
/*      */     }
/* 1640 */     localDerOutputStream1.write((byte)49, localDerOutputStream2);
/* 1641 */     return localDerOutputStream1.toByteArray();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] createEncryptedData(char[] paramArrayOfChar)
/*      */     throws CertificateException, IOException
/*      */   {
/* 1653 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 1654 */     for (Object localObject1 = engineAliases(); ((Enumeration)localObject1).hasMoreElements();)
/*      */     {
/* 1656 */       localObject2 = (String)((Enumeration)localObject1).nextElement();
/* 1657 */       localObject3 = (Entry)this.entries.get(localObject2);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1662 */       if ((localObject3 instanceof PrivateKeyEntry)) {
/* 1663 */         PrivateKeyEntry localPrivateKeyEntry = (PrivateKeyEntry)localObject3;
/* 1664 */         if (localPrivateKeyEntry.chain != null) {
/* 1665 */           localObject4 = localPrivateKeyEntry.chain;
/*      */         } else {
/* 1667 */           localObject4 = new Certificate[0];
/*      */         }
/* 1669 */       } else if ((localObject3 instanceof CertEntry)) {
/* 1670 */         localObject4 = new Certificate[] { ((CertEntry)localObject3).cert };
/*      */       } else {
/* 1672 */         localObject4 = new Certificate[0];
/*      */       }
/*      */       
/* 1675 */       for (int i = 0; i < localObject4.length; i++)
/*      */       {
/* 1677 */         DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 1678 */         localDerOutputStream3.putOID(CertBag_OID);
/*      */         
/*      */ 
/* 1681 */         DerOutputStream localDerOutputStream4 = new DerOutputStream();
/* 1682 */         localDerOutputStream4.putOID(PKCS9CertType_OID);
/*      */         
/*      */ 
/* 1685 */         DerOutputStream localDerOutputStream5 = new DerOutputStream();
/* 1686 */         X509Certificate localX509Certificate = (X509Certificate)localObject4[i];
/* 1687 */         localDerOutputStream5.putOctetString(localX509Certificate.getEncoded());
/* 1688 */         localDerOutputStream4.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream5);
/*      */         
/*      */ 
/*      */ 
/* 1692 */         DerOutputStream localDerOutputStream6 = new DerOutputStream();
/* 1693 */         localDerOutputStream6.write((byte)48, localDerOutputStream4);
/* 1694 */         byte[] arrayOfByte1 = localDerOutputStream6.toByteArray();
/*      */         
/*      */ 
/* 1697 */         DerOutputStream localDerOutputStream7 = new DerOutputStream();
/* 1698 */         localDerOutputStream7.write(arrayOfByte1);
/*      */         
/* 1700 */         localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream7);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1706 */         byte[] arrayOfByte2 = null;
/* 1707 */         if (i == 0) {
/*      */           Object localObject5;
/* 1709 */           if ((localObject3 instanceof KeyEntry)) {
/* 1710 */             localObject5 = (KeyEntry)localObject3;
/*      */             
/* 1712 */             arrayOfByte2 = getBagAttributes(((KeyEntry)localObject5).alias, ((KeyEntry)localObject5).keyId, ((KeyEntry)localObject5).attributes);
/*      */           }
/*      */           else {
/* 1715 */             localObject5 = (CertEntry)localObject3;
/*      */             
/* 1717 */             arrayOfByte2 = getBagAttributes(((CertEntry)localObject5).alias, ((CertEntry)localObject5).keyId, ((CertEntry)localObject5).trustedKeyUsage, ((CertEntry)localObject5).attributes);
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/* 1728 */           arrayOfByte2 = getBagAttributes(localX509Certificate
/* 1729 */             .getSubjectX500Principal().getName(), null, ((Entry)localObject3).attributes);
/*      */         }
/*      */         
/* 1732 */         if (arrayOfByte2 != null) {
/* 1733 */           localDerOutputStream3.write(arrayOfByte2);
/*      */         }
/*      */         
/*      */ 
/* 1737 */         localDerOutputStream1.write((byte)48, localDerOutputStream3);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1742 */     localObject1 = new DerOutputStream();
/* 1743 */     ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream1);
/* 1744 */     Object localObject2 = ((DerOutputStream)localObject1).toByteArray();
/*      */     
/*      */ 
/* 1747 */     Object localObject3 = encryptContent((byte[])localObject2, paramArrayOfChar);
/*      */     
/*      */ 
/* 1750 */     Object localObject4 = new DerOutputStream();
/* 1751 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 1752 */     ((DerOutputStream)localObject4).putInteger(0);
/* 1753 */     ((DerOutputStream)localObject4).write((byte[])localObject3);
/* 1754 */     localDerOutputStream2.write((byte)48, (DerOutputStream)localObject4);
/* 1755 */     return localDerOutputStream2.toByteArray();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] createSafeContent()
/*      */     throws CertificateException, IOException
/*      */   {
/* 1768 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 1769 */     for (Object localObject1 = engineAliases(); ((Enumeration)localObject1).hasMoreElements();)
/*      */     {
/* 1771 */       String str = (String)((Enumeration)localObject1).nextElement();
/* 1772 */       Entry localEntry = (Entry)this.entries.get(str);
/* 1773 */       if ((localEntry != null) && ((localEntry instanceof KeyEntry)))
/*      */       {
/*      */ 
/* 1776 */         DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 1777 */         KeyEntry localKeyEntry = (KeyEntry)localEntry;
/*      */         Object localObject3;
/*      */         DerOutputStream localDerOutputStream3;
/* 1780 */         if ((localKeyEntry instanceof PrivateKeyEntry))
/*      */         {
/* 1782 */           localDerOutputStream2.putOID(PKCS8ShroudedKeyBag_OID);
/*      */           
/*      */ 
/* 1785 */           localObject2 = ((PrivateKeyEntry)localKeyEntry).protectedPrivKey;
/* 1786 */           localObject3 = null;
/*      */           try {
/* 1788 */             localObject3 = new EncryptedPrivateKeyInfo((byte[])localObject2);
/*      */ 
/*      */           }
/*      */           catch (IOException localIOException)
/*      */           {
/* 1793 */             throw new IOException("Private key not stored as PKCS#8 EncryptedPrivateKeyInfo" + localIOException.getMessage());
/*      */           }
/*      */           
/*      */ 
/* 1797 */           localDerOutputStream3 = new DerOutputStream();
/* 1798 */           localDerOutputStream3.write(((EncryptedPrivateKeyInfo)localObject3).getEncoded());
/* 1799 */           localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream3);
/*      */         }
/*      */         else
/*      */         {
/* 1803 */           if (!(localKeyEntry instanceof SecretKeyEntry))
/*      */             continue;
/* 1805 */           localDerOutputStream2.putOID(SecretBag_OID);
/*      */           
/*      */ 
/* 1808 */           localObject2 = new DerOutputStream();
/* 1809 */           ((DerOutputStream)localObject2).putOID(PKCS8ShroudedKeyBag_OID);
/*      */           
/*      */ 
/* 1812 */           localObject3 = new DerOutputStream();
/* 1813 */           ((DerOutputStream)localObject3).putOctetString(((SecretKeyEntry)localKeyEntry).protectedSecretKey);
/*      */           
/* 1815 */           ((DerOutputStream)localObject2).write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), (DerOutputStream)localObject3);
/*      */           
/*      */ 
/*      */ 
/* 1819 */           localDerOutputStream3 = new DerOutputStream();
/* 1820 */           localDerOutputStream3.write((byte)48, (DerOutputStream)localObject2);
/* 1821 */           byte[] arrayOfByte = localDerOutputStream3.toByteArray();
/*      */           
/*      */ 
/* 1824 */           DerOutputStream localDerOutputStream4 = new DerOutputStream();
/* 1825 */           localDerOutputStream4.write(arrayOfByte);
/*      */           
/*      */ 
/* 1828 */           localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream4);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1836 */         Object localObject2 = getBagAttributes(str, localEntry.keyId, localEntry.attributes);
/* 1837 */         localDerOutputStream2.write((byte[])localObject2);
/*      */         
/*      */ 
/* 1840 */         localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*      */       }
/*      */     }
/*      */     
/* 1844 */     localObject1 = new DerOutputStream();
/* 1845 */     ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream1);
/* 1846 */     return ((DerOutputStream)localObject1).toByteArray();
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
/*      */   private byte[] encryptContent(byte[] paramArrayOfByte, char[] paramArrayOfChar)
/*      */     throws IOException
/*      */   {
/* 1862 */     byte[] arrayOfByte1 = null;
/*      */     
/*      */ 
/*      */ 
/* 1866 */     AlgorithmParameters localAlgorithmParameters = getPBEAlgorithmParameters("PBEWithSHA1AndRC2_40");
/* 1867 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 1868 */     AlgorithmId localAlgorithmId = new AlgorithmId(pbeWithSHAAnd40BitRC2CBC_OID, localAlgorithmParameters);
/*      */     
/* 1870 */     localAlgorithmId.encode(localDerOutputStream1);
/* 1871 */     byte[] arrayOfByte2 = localDerOutputStream1.toByteArray();
/*      */     
/*      */     try
/*      */     {
/* 1875 */       SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
/* 1876 */       localObject = Cipher.getInstance("PBEWithSHA1AndRC2_40");
/* 1877 */       ((Cipher)localObject).init(1, localSecretKey, localAlgorithmParameters);
/* 1878 */       arrayOfByte1 = ((Cipher)localObject).doFinal(paramArrayOfByte);
/*      */       
/* 1880 */       if (debug != null) {
/* 1881 */         debug.println("  (Cipher algorithm: " + ((Cipher)localObject).getAlgorithm() + ")");
/*      */       }
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 1886 */       throw new IOException("Failed to encrypt safe contents entry: " + localException, localException);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1891 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 1892 */     localDerOutputStream2.putOID(ContentInfo.DATA_OID);
/* 1893 */     localDerOutputStream2.write(arrayOfByte2);
/*      */     
/*      */ 
/* 1896 */     Object localObject = new DerOutputStream();
/* 1897 */     ((DerOutputStream)localObject).putOctetString(arrayOfByte1);
/* 1898 */     localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)0), (DerOutputStream)localObject);
/*      */     
/*      */ 
/*      */ 
/* 1902 */     DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 1903 */     localDerOutputStream3.write((byte)48, localDerOutputStream2);
/* 1904 */     return localDerOutputStream3.toByteArray();
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
/*      */   public synchronized void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
/*      */     throws IOException, NoSuchAlgorithmException, CertificateException
/*      */   {
/* 1928 */     Object localObject1 = null;
/* 1929 */     Object localObject2 = null;
/* 1930 */     Object localObject3 = null;
/*      */     
/* 1932 */     if (paramInputStream == null) {
/* 1933 */       return;
/*      */     }
/*      */     
/* 1936 */     this.counter = 0;
/*      */     
/* 1938 */     DerValue localDerValue = new DerValue(paramInputStream);
/* 1939 */     DerInputStream localDerInputStream1 = localDerValue.toDerInputStream();
/* 1940 */     int i = localDerInputStream1.getInteger();
/*      */     
/* 1942 */     if (i != 3) {
/* 1943 */       throw new IOException("PKCS12 keystore not in version 3 format");
/*      */     }
/*      */     
/* 1946 */     this.entries.clear();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1952 */     ContentInfo localContentInfo = new ContentInfo(localDerInputStream1);
/* 1953 */     ObjectIdentifier localObjectIdentifier1 = localContentInfo.getContentType();
/*      */     byte[] arrayOfByte1;
/* 1955 */     if (localObjectIdentifier1.equals(ContentInfo.DATA_OID)) {
/* 1956 */       arrayOfByte1 = localContentInfo.getData();
/*      */     } else {
/* 1958 */       throw new IOException("public key protected PKCS12 not supported");
/*      */     }
/*      */     
/* 1961 */     DerInputStream localDerInputStream2 = new DerInputStream(arrayOfByte1);
/* 1962 */     DerValue[] arrayOfDerValue1 = localDerInputStream2.getSequence(2);
/* 1963 */     int j = arrayOfDerValue1.length;
/*      */     
/*      */ 
/* 1966 */     this.privateKeyCount = 0;
/* 1967 */     this.secretKeyCount = 0;
/* 1968 */     this.certificateCount = 0;
/*      */     Object localObject7;
/*      */     Object localObject6;
/*      */     Object localObject5;
/*      */     Object localObject8;
/* 1973 */     for (int k = 0; k < j; k++)
/*      */     {
/*      */ 
/*      */ 
/* 1977 */       localObject7 = null;
/*      */       
/* 1979 */       localObject6 = new DerInputStream(arrayOfDerValue1[k].toByteArray());
/* 1980 */       localObject5 = new ContentInfo((DerInputStream)localObject6);
/* 1981 */       localObjectIdentifier1 = ((ContentInfo)localObject5).getContentType();
/* 1982 */       byte[] arrayOfByte2 = null;
/* 1983 */       if (localObjectIdentifier1.equals(ContentInfo.DATA_OID))
/*      */       {
/* 1985 */         if (debug != null) {
/* 1986 */           debug.println("Loading PKCS#7 data");
/*      */         }
/*      */         
/* 1989 */         arrayOfByte2 = ((ContentInfo)localObject5).getData();
/* 1990 */       } else if (localObjectIdentifier1.equals(ContentInfo.ENCRYPTED_DATA_OID)) {
/* 1991 */         if (paramArrayOfChar == null)
/*      */         {
/* 1993 */           if (debug == null) continue;
/* 1994 */           debug.println("Warning: skipping PKCS#7 encryptedData - no password was supplied"); continue;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2001 */         localObject8 = ((ContentInfo)localObject5).getContent().toDerInputStream();
/* 2002 */         int n = ((DerInputStream)localObject8).getInteger();
/* 2003 */         DerValue[] arrayOfDerValue2 = ((DerInputStream)localObject8).getSequence(2);
/* 2004 */         ObjectIdentifier localObjectIdentifier2 = arrayOfDerValue2[0].getOID();
/* 2005 */         localObject7 = arrayOfDerValue2[1].toByteArray();
/* 2006 */         if (!arrayOfDerValue2[2].isContextSpecific((byte)0)) {
/* 2007 */           throw new IOException("encrypted content not present!");
/*      */         }
/* 2009 */         byte b = 4;
/* 2010 */         if (arrayOfDerValue2[2].isConstructed())
/* 2011 */           b = (byte)(b | 0x20);
/* 2012 */         arrayOfDerValue2[2].resetTag(b);
/* 2013 */         arrayOfByte2 = arrayOfDerValue2[2].getOctetString();
/*      */         
/*      */ 
/* 2016 */         DerInputStream localDerInputStream3 = arrayOfDerValue2[1].toDerInputStream();
/* 2017 */         ObjectIdentifier localObjectIdentifier3 = localDerInputStream3.getOID();
/* 2018 */         AlgorithmParameters localAlgorithmParameters = parseAlgParameters(localObjectIdentifier3, localDerInputStream3);
/*      */         
/*      */ 
/* 2021 */         int i1 = 0;
/*      */         
/* 2023 */         if (localAlgorithmParameters != null) {
/*      */           PBEParameterSpec localPBEParameterSpec;
/*      */           try {
/* 2026 */             localPBEParameterSpec = (PBEParameterSpec)localAlgorithmParameters.getParameterSpec(PBEParameterSpec.class);
/*      */           } catch (InvalidParameterSpecException localInvalidParameterSpecException) {
/* 2028 */             throw new IOException("Invalid PBE algorithm parameters");
/*      */           }
/*      */           
/* 2031 */           i1 = localPBEParameterSpec.getIterationCount();
/*      */           
/* 2033 */           if (i1 > 5000000) {
/* 2034 */             throw new IOException("PBE iteration count too large");
/*      */           }
/*      */         }
/*      */         
/* 2038 */         if (debug != null) {
/* 2039 */           debug.println("Loading PKCS#7 encryptedData (" + new AlgorithmId(localObjectIdentifier3)
/* 2040 */             .getName() + " iterations: " + i1 + ")");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/* 2047 */           SecretKey localSecretKey = getPBEKey(paramArrayOfChar);
/* 2048 */           Cipher localCipher = Cipher.getInstance(localObjectIdentifier3.toString());
/* 2049 */           localCipher.init(2, localSecretKey, localAlgorithmParameters);
/* 2050 */           arrayOfByte2 = localCipher.doFinal(arrayOfByte2);
/*      */         }
/*      */         catch (Exception localException2) {
/* 2053 */           while (paramArrayOfChar.length == 0)
/*      */           {
/*      */ 
/* 2056 */             paramArrayOfChar = new char[1];
/*      */           }
/*      */           
/* 2059 */           throw new IOException("keystore password was incorrect", new UnrecoverableKeyException("failed to decrypt safe contents entry: " + localException2));
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/* 2065 */         throw new IOException("public key protected PKCS12 not supported");
/*      */       }
/*      */       
/* 2068 */       localObject8 = new DerInputStream(arrayOfByte2);
/* 2069 */       loadSafeContents((DerInputStream)localObject8, paramArrayOfChar);
/*      */     }
/*      */     
/*      */     Object localObject9;
/* 2073 */     if ((paramArrayOfChar != null) && (localDerInputStream1.available() > 0)) {
/* 2074 */       localObject4 = new MacData(localDerInputStream1);
/* 2075 */       m = ((MacData)localObject4).getIterations();
/*      */       try
/*      */       {
/* 2078 */         if (m > 5000000) {
/* 2079 */           throw new InvalidAlgorithmParameterException("MAC iteration count too large: " + m);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2084 */         localObject5 = ((MacData)localObject4).getDigestAlgName().toUpperCase(Locale.ENGLISH);
/*      */         
/*      */ 
/* 2087 */         localObject5 = ((String)localObject5).replace("-", "");
/*      */         
/*      */ 
/* 2090 */         localObject6 = Mac.getInstance("HmacPBE" + (String)localObject5);
/*      */         
/* 2092 */         localObject7 = new PBEParameterSpec(((MacData)localObject4).getSalt(), m);
/* 2093 */         localObject8 = getPBEKey(paramArrayOfChar);
/* 2094 */         ((Mac)localObject6).init((Key)localObject8, (AlgorithmParameterSpec)localObject7);
/* 2095 */         ((Mac)localObject6).update(arrayOfByte1);
/* 2096 */         localObject9 = ((Mac)localObject6).doFinal();
/*      */         
/* 2098 */         if (debug != null) {
/* 2099 */           debug.println("Checking keystore integrity (" + ((Mac)localObject6)
/* 2100 */             .getAlgorithm() + " iterations: " + m + ")");
/*      */         }
/*      */         
/* 2103 */         if (!MessageDigest.isEqual(((MacData)localObject4).getDigest(), (byte[])localObject9)) {
/* 2104 */           throw new UnrecoverableKeyException("Failed PKCS12 integrity checking");
/*      */         }
/*      */       }
/*      */       catch (Exception localException1) {
/* 2108 */         throw new IOException("Integrity check failed: " + localException1, localException1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2116 */     Object localObject4 = (PrivateKeyEntry[])this.keyList.toArray(new PrivateKeyEntry[this.keyList.size()]);
/* 2117 */     for (int m = 0; m < localObject4.length; m++) {
/* 2118 */       PrivateKeyEntry localPrivateKeyEntry = localObject4[m];
/* 2119 */       if (localPrivateKeyEntry.keyId != null) {
/* 2120 */         localObject6 = new ArrayList();
/*      */         
/* 2122 */         localObject7 = findMatchedCertificate(localPrivateKeyEntry);
/*      */         
/*      */ 
/* 2125 */         while (localObject7 != null)
/*      */         {
/* 2127 */           if (!((ArrayList)localObject6).isEmpty()) {
/* 2128 */             for (localObject8 = ((ArrayList)localObject6).iterator(); ((Iterator)localObject8).hasNext();) { localObject9 = (X509Certificate)((Iterator)localObject8).next();
/* 2129 */               if (((X509Certificate)localObject7).equals(localObject9)) {
/* 2130 */                 if (debug == null) break label1140;
/* 2131 */                 debug.println("Loop detected in certificate chain. Skip adding repeated cert to chain. Subject: " + ((X509Certificate)localObject7)
/*      */                 
/*      */ 
/* 2134 */                   .getSubjectX500Principal()
/* 2135 */                   .toString());
/*      */                 
/*      */                 break label1140;
/*      */               }
/*      */             }
/*      */           }
/* 2141 */           ((ArrayList)localObject6).add(localObject7);
/* 2142 */           localObject8 = ((X509Certificate)localObject7).getIssuerX500Principal();
/* 2143 */           if (((X500Principal)localObject8).equals(((X509Certificate)localObject7).getSubjectX500Principal())) {
/*      */             break;
/*      */           }
/* 2146 */           localObject7 = (X509Certificate)this.certsMap.get(localObject8);
/*      */         }
/*      */         label1140:
/* 2149 */         if (((ArrayList)localObject6).size() > 0) {
/* 2150 */           localPrivateKeyEntry.chain = ((Certificate[])((ArrayList)localObject6).toArray(new Certificate[((ArrayList)localObject6).size()]));
/*      */         }
/*      */       }
/*      */     }
/* 2154 */     if (debug != null) {
/* 2155 */       if (this.privateKeyCount > 0) {
/* 2156 */         debug.println("Loaded " + this.privateKeyCount + " protected private key(s)");
/*      */       }
/*      */       
/* 2159 */       if (this.secretKeyCount > 0) {
/* 2160 */         debug.println("Loaded " + this.secretKeyCount + " protected secret key(s)");
/*      */       }
/*      */       
/* 2163 */       if (this.certificateCount > 0) {
/* 2164 */         debug.println("Loaded " + this.certificateCount + " certificate(s)");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2169 */     this.certEntries.clear();
/* 2170 */     this.certsMap.clear();
/* 2171 */     this.keyList.clear();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private X509Certificate findMatchedCertificate(PrivateKeyEntry paramPrivateKeyEntry)
/*      */   {
/* 2180 */     Object localObject1 = null;
/* 2181 */     Object localObject2 = null;
/* 2182 */     for (CertEntry localCertEntry : this.certEntries) {
/* 2183 */       if (Arrays.equals(paramPrivateKeyEntry.keyId, localCertEntry.keyId)) {
/* 2184 */         localObject1 = localCertEntry;
/* 2185 */         if (paramPrivateKeyEntry.alias.equalsIgnoreCase(localCertEntry.alias))
/*      */         {
/* 2187 */           return localCertEntry.cert;
/*      */         }
/* 2189 */       } else if (paramPrivateKeyEntry.alias.equalsIgnoreCase(localCertEntry.alias)) {
/* 2190 */         localObject2 = localCertEntry;
/*      */       }
/*      */     }
/*      */     
/* 2194 */     if (localObject1 != null) return ((CertEntry)localObject1).cert;
/* 2195 */     if (localObject2 != null) return ((CertEntry)localObject2).cert;
/* 2196 */     return null;
/*      */   }
/*      */   
/*      */   private void loadSafeContents(DerInputStream paramDerInputStream, char[] paramArrayOfChar)
/*      */     throws IOException, NoSuchAlgorithmException, CertificateException
/*      */   {
/* 2202 */     DerValue[] arrayOfDerValue1 = paramDerInputStream.getSequence(2);
/* 2203 */     int i = arrayOfDerValue1.length;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2208 */     for (int j = 0; j < i; j++)
/*      */     {
/*      */ 
/*      */ 
/* 2212 */       Object localObject1 = null;
/*      */       
/* 2214 */       DerInputStream localDerInputStream1 = arrayOfDerValue1[j].toDerInputStream();
/* 2215 */       ObjectIdentifier localObjectIdentifier1 = localDerInputStream1.getOID();
/* 2216 */       DerValue localDerValue = localDerInputStream1.getDerValue();
/* 2217 */       if (!localDerValue.isContextSpecific((byte)0)) {
/* 2218 */         throw new IOException("unsupported PKCS12 bag value type " + localDerValue.tag);
/*      */       }
/*      */       
/* 2221 */       localDerValue = localDerValue.data.getDerValue();
/* 2222 */       Object localObject2; if (localObjectIdentifier1.equals(PKCS8ShroudedKeyBag_OID)) {
/* 2223 */         localObject2 = new PrivateKeyEntry(null);
/* 2224 */         ((PrivateKeyEntry)localObject2).protectedPrivKey = localDerValue.toByteArray();
/* 2225 */         localObject1 = localObject2;
/* 2226 */         this.privateKeyCount += 1; } else { DerValue[] arrayOfDerValue2;
/* 2227 */         if (localObjectIdentifier1.equals(CertBag_OID)) {
/* 2228 */           localObject2 = new DerInputStream(localDerValue.toByteArray());
/* 2229 */           arrayOfDerValue2 = ((DerInputStream)localObject2).getSequence(2);
/* 2230 */           localObject3 = arrayOfDerValue2[0].getOID();
/* 2231 */           if (!arrayOfDerValue2[1].isContextSpecific((byte)0)) {
/* 2232 */             throw new IOException("unsupported PKCS12 cert value type " + arrayOfDerValue2[1].tag);
/*      */           }
/*      */           
/* 2235 */           localObject4 = arrayOfDerValue2[1].data.getDerValue();
/* 2236 */           localObject5 = CertificateFactory.getInstance("X509");
/*      */           
/*      */ 
/* 2239 */           X509Certificate localX509Certificate = (X509Certificate)((CertificateFactory)localObject5).generateCertificate(new ByteArrayInputStream(((DerValue)localObject4).getOctetString()));
/* 2240 */           localObject1 = localX509Certificate;
/* 2241 */           this.certificateCount += 1;
/* 2242 */         } else if (localObjectIdentifier1.equals(SecretBag_OID)) {
/* 2243 */           localObject2 = new DerInputStream(localDerValue.toByteArray());
/* 2244 */           arrayOfDerValue2 = ((DerInputStream)localObject2).getSequence(2);
/* 2245 */           localObject3 = arrayOfDerValue2[0].getOID();
/* 2246 */           if (!arrayOfDerValue2[1].isContextSpecific((byte)0)) {
/* 2247 */             throw new IOException("unsupported PKCS12 secret value type " + arrayOfDerValue2[1].tag);
/*      */           }
/*      */           
/*      */ 
/* 2251 */           localObject4 = arrayOfDerValue2[1].data.getDerValue();
/* 2252 */           localObject5 = new SecretKeyEntry(null);
/* 2253 */           ((SecretKeyEntry)localObject5).protectedSecretKey = ((DerValue)localObject4).getOctetString();
/* 2254 */           localObject1 = localObject5;
/* 2255 */           this.secretKeyCount += 1;
/*      */ 
/*      */         }
/* 2258 */         else if (debug != null) {
/* 2259 */           debug.println("Unsupported PKCS12 bag type: " + localObjectIdentifier1);
/*      */         }
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 2265 */         localObject2 = localDerInputStream1.getSet(3);
/*      */ 
/*      */       }
/*      */       catch (IOException localIOException1)
/*      */       {
/* 2270 */         localObject2 = null;
/*      */       }
/*      */       
/* 2273 */       String str = null;
/* 2274 */       Object localObject3 = null;
/* 2275 */       Object localObject4 = null;
/* 2276 */       Object localObject5 = new HashSet();
/*      */       Object localObject7;
/* 2278 */       Object localObject8; if (localObject2 != null) {
/* 2279 */         for (int k = 0; k < localObject2.length; k++) {
/* 2280 */           localObject7 = localObject2[k].toByteArray();
/* 2281 */           localObject8 = new DerInputStream((byte[])localObject7);
/* 2282 */           DerValue[] arrayOfDerValue3 = ((DerInputStream)localObject8).getSequence(2);
/* 2283 */           ObjectIdentifier localObjectIdentifier2 = arrayOfDerValue3[0].getOID();
/*      */           
/* 2285 */           DerInputStream localDerInputStream2 = new DerInputStream(arrayOfDerValue3[1].toByteArray());
/*      */           DerValue[] arrayOfDerValue4;
/*      */           try {
/* 2288 */             arrayOfDerValue4 = localDerInputStream2.getSet(1);
/*      */           }
/*      */           catch (IOException localIOException2) {
/* 2291 */             throw new IOException("Attribute " + localObjectIdentifier2 + " should have a value " + localIOException2.getMessage());
/*      */           }
/* 2293 */           if (localObjectIdentifier2.equals(PKCS9FriendlyName_OID)) {
/* 2294 */             str = arrayOfDerValue4[0].getBMPString();
/* 2295 */           } else if (localObjectIdentifier2.equals(PKCS9LocalKeyId_OID)) {
/* 2296 */             localObject3 = arrayOfDerValue4[0].getOctetString();
/*      */           }
/* 2298 */           else if (localObjectIdentifier2.equals(TrustedKeyUsage_OID)) {
/* 2299 */             localObject4 = new ObjectIdentifier[arrayOfDerValue4.length];
/* 2300 */             for (int m = 0; m < arrayOfDerValue4.length; m++) {
/* 2301 */               localObject4[m] = arrayOfDerValue4[m].getOID();
/*      */             }
/*      */           } else {
/* 2304 */             ((Set)localObject5).add(new PKCS12Attribute((byte[])localObject7));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       Object localObject6;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2318 */       if ((localObject1 instanceof KeyEntry)) {
/* 2319 */         localObject6 = (KeyEntry)localObject1;
/*      */         
/* 2321 */         if (((localObject1 instanceof PrivateKeyEntry)) && 
/* 2322 */           (localObject3 == null))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2327 */           if (this.privateKeyCount == 1) {
/* 2328 */             localObject3 = "01".getBytes("UTF8");
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/* 2334 */           ((KeyEntry)localObject6).keyId = ((byte[])localObject3);
/*      */           
/* 2336 */           localObject7 = new String((byte[])localObject3, "UTF8");
/* 2337 */           localObject8 = null;
/* 2338 */           if (((String)localObject7).startsWith("Time ")) {
/*      */             try
/*      */             {
/* 2341 */               localObject8 = new Date(Long.parseLong(((String)localObject7).substring(5)));
/*      */             } catch (Exception localException) {
/* 2343 */               localObject8 = null;
/*      */             }
/*      */           }
/* 2346 */           if (localObject8 == null) {
/* 2347 */             localObject8 = new Date();
/*      */           }
/* 2349 */           ((KeyEntry)localObject6).date = ((Date)localObject8);
/*      */           
/* 2351 */           if ((localObject1 instanceof PrivateKeyEntry)) {
/* 2352 */             this.keyList.add((PrivateKeyEntry)localObject6);
/*      */           }
/* 2354 */           if (((KeyEntry)localObject6).attributes == null) {
/* 2355 */             ((KeyEntry)localObject6).attributes = new HashSet();
/*      */           }
/* 2357 */           ((KeyEntry)localObject6).attributes.addAll((Collection)localObject5);
/* 2358 */           if (str == null) {
/* 2359 */             str = getUnfriendlyName();
/*      */           }
/* 2361 */           ((KeyEntry)localObject6).alias = str;
/* 2362 */           this.entries.put(str.toLowerCase(Locale.ENGLISH), localObject6);
/*      */         }
/* 2364 */       } else if ((localObject1 instanceof X509Certificate)) {
/* 2365 */         localObject6 = (X509Certificate)localObject1;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2370 */         if ((localObject3 == null) && (this.privateKeyCount == 1))
/*      */         {
/* 2372 */           if (j == 0) {
/* 2373 */             localObject3 = "01".getBytes("UTF8");
/*      */           }
/*      */         }
/*      */         
/* 2377 */         if (localObject4 != null) {
/* 2378 */           if (str == null) {
/* 2379 */             str = getUnfriendlyName();
/*      */           }
/* 2381 */           localObject7 = new CertEntry((X509Certificate)localObject6, (byte[])localObject3, str, (ObjectIdentifier[])localObject4, (Set)localObject5);
/*      */           
/*      */ 
/* 2384 */           this.entries.put(str.toLowerCase(Locale.ENGLISH), localObject7);
/*      */         } else {
/* 2386 */           this.certEntries.add(new CertEntry((X509Certificate)localObject6, (byte[])localObject3, str));
/*      */         }
/* 2388 */         localObject7 = ((X509Certificate)localObject6).getSubjectX500Principal();
/* 2389 */         if ((localObject7 != null) && 
/* 2390 */           (!this.certsMap.containsKey(localObject7))) {
/* 2391 */           this.certsMap.put(localObject7, localObject6);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private String getUnfriendlyName()
/*      */   {
/* 2399 */     this.counter += 1;
/* 2400 */     return String.valueOf(this.counter);
/*      */   }
/*      */   
/*      */   private static class Entry
/*      */   {
/*      */     Date date;
/*      */     String alias;
/*      */     byte[] keyId;
/*      */     Set<KeyStore.Entry.Attribute> attributes;
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\pkcs12\PKCS12KeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
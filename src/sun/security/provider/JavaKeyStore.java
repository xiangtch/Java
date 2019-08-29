/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.DigestInputStream;
/*     */ import java.security.DigestOutputStream;
/*     */ import java.security.Key;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.KeyStoreSpi;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import sun.misc.IOUtils;
/*     */ import sun.security.pkcs.EncryptedPrivateKeyInfo;
/*     */ import sun.security.pkcs12.PKCS12KeyStore;
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
/*     */ abstract class JavaKeyStore
/*     */   extends KeyStoreSpi
/*     */ {
/*     */   private static final int MAGIC = -17957139;
/*     */   private static final int VERSION_1 = 1;
/*     */   private static final int VERSION_2 = 2;
/*     */   private final Hashtable<String, Object> entries;
/*     */   
/*     */   public static final class JKS
/*     */     extends JavaKeyStore
/*     */   {
/*     */     String convertAlias(String paramString)
/*     */     {
/*  58 */       return paramString.toLowerCase(Locale.ENGLISH);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class CaseExactJKS extends JavaKeyStore
/*     */   {
/*     */     String convertAlias(String paramString) {
/*  65 */       return paramString;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class DualFormatJKS extends KeyStoreDelegator
/*     */   {
/*     */     public DualFormatJKS() {
/*  72 */       super(JKS.class, "PKCS12", PKCS12KeyStore.class);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   JavaKeyStore()
/*     */   {
/* 100 */     this.entries = new Hashtable();
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
/*     */   abstract String convertAlias(String paramString);
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
/* 126 */     Object localObject = this.entries.get(convertAlias(paramString));
/*     */     
/* 128 */     if ((localObject == null) || (!(localObject instanceof KeyEntry))) {
/* 129 */       return null;
/*     */     }
/* 131 */     if (paramArrayOfChar == null) {
/* 132 */       throw new UnrecoverableKeyException("Password must not be null");
/*     */     }
/*     */     
/* 135 */     KeyProtector localKeyProtector = new KeyProtector(paramArrayOfChar);
/* 136 */     byte[] arrayOfByte = ((KeyEntry)localObject).protectedPrivKey;
/*     */     EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo;
/*     */     try
/*     */     {
/* 140 */       localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte);
/*     */     } catch (IOException localIOException) {
/* 142 */       throw new UnrecoverableKeyException("Private key not stored as PKCS #8 EncryptedPrivateKeyInfo");
/*     */     }
/*     */     
/*     */ 
/* 146 */     return localKeyProtector.recover(localEncryptedPrivateKeyInfo);
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
/*     */   public Certificate[] engineGetCertificateChain(String paramString)
/*     */   {
/* 161 */     Object localObject = this.entries.get(convertAlias(paramString));
/*     */     
/* 163 */     if ((localObject != null) && ((localObject instanceof KeyEntry))) {
/* 164 */       if (((KeyEntry)localObject).chain == null) {
/* 165 */         return null;
/*     */       }
/* 167 */       return (Certificate[])((KeyEntry)localObject).chain.clone();
/*     */     }
/*     */     
/* 170 */     return null;
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
/*     */   public Certificate engineGetCertificate(String paramString)
/*     */   {
/* 190 */     Object localObject = this.entries.get(convertAlias(paramString));
/*     */     
/* 192 */     if (localObject != null) {
/* 193 */       if ((localObject instanceof TrustedCertEntry)) {
/* 194 */         return ((TrustedCertEntry)localObject).cert;
/*     */       }
/* 196 */       if (((KeyEntry)localObject).chain == null) {
/* 197 */         return null;
/*     */       }
/* 199 */       return ((KeyEntry)localObject).chain[0];
/*     */     }
/*     */     
/*     */ 
/* 203 */     return null;
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
/*     */   public Date engineGetCreationDate(String paramString)
/*     */   {
/* 216 */     Object localObject = this.entries.get(convertAlias(paramString));
/*     */     
/* 218 */     if (localObject != null) {
/* 219 */       if ((localObject instanceof TrustedCertEntry)) {
/* 220 */         return new Date(((TrustedCertEntry)localObject).date.getTime());
/*     */       }
/* 222 */       return new Date(((KeyEntry)localObject).date.getTime());
/*     */     }
/*     */     
/* 225 */     return null;
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
/*     */   public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
/*     */     throws KeyStoreException
/*     */   {
/* 255 */     KeyProtector localKeyProtector = null;
/*     */     
/* 257 */     if (!(paramKey instanceof PrivateKey)) {
/* 258 */       throw new KeyStoreException("Cannot store non-PrivateKeys");
/*     */     }
/*     */     try {
/* 261 */       synchronized (this.entries) {
/* 262 */         KeyEntry localKeyEntry = new KeyEntry(null);
/* 263 */         localKeyEntry.date = new Date();
/*     */         
/*     */ 
/* 266 */         localKeyProtector = new KeyProtector(paramArrayOfChar);
/* 267 */         localKeyEntry.protectedPrivKey = localKeyProtector.protect(paramKey);
/*     */         
/*     */ 
/* 270 */         if ((paramArrayOfCertificate != null) && (paramArrayOfCertificate.length != 0))
/*     */         {
/* 272 */           localKeyEntry.chain = ((Certificate[])paramArrayOfCertificate.clone());
/*     */         } else {
/* 274 */           localKeyEntry.chain = null;
/*     */         }
/*     */         
/* 277 */         this.entries.put(convertAlias(paramString), localKeyEntry);
/*     */       }
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 280 */       throw new KeyStoreException("Key protection algorithm not found");
/*     */     } finally {
/* 282 */       localKeyProtector = null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
/*     */     throws KeyStoreException
/*     */   {
/* 313 */     synchronized (this.entries)
/*     */     {
/*     */       try
/*     */       {
/* 317 */         new EncryptedPrivateKeyInfo(paramArrayOfByte);
/*     */       } catch (IOException localIOException) {
/* 319 */         throw new KeyStoreException("key is not encoded as EncryptedPrivateKeyInfo");
/*     */       }
/*     */       
/*     */ 
/* 323 */       KeyEntry localKeyEntry = new KeyEntry(null);
/* 324 */       localKeyEntry.date = new Date();
/*     */       
/* 326 */       localKeyEntry.protectedPrivKey = ((byte[])paramArrayOfByte.clone());
/* 327 */       if ((paramArrayOfCertificate != null) && (paramArrayOfCertificate.length != 0))
/*     */       {
/* 329 */         localKeyEntry.chain = ((Certificate[])paramArrayOfCertificate.clone());
/*     */       } else {
/* 331 */         localKeyEntry.chain = null;
/*     */       }
/*     */       
/* 334 */       this.entries.put(convertAlias(paramString), localKeyEntry);
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
/*     */   public void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
/*     */     throws KeyStoreException
/*     */   {
/* 355 */     synchronized (this.entries)
/*     */     {
/* 357 */       Object localObject1 = this.entries.get(convertAlias(paramString));
/* 358 */       if ((localObject1 != null) && ((localObject1 instanceof KeyEntry))) {
/* 359 */         throw new KeyStoreException("Cannot overwrite own certificate");
/*     */       }
/*     */       
/*     */ 
/* 363 */       TrustedCertEntry localTrustedCertEntry = new TrustedCertEntry(null);
/* 364 */       localTrustedCertEntry.cert = paramCertificate;
/* 365 */       localTrustedCertEntry.date = new Date();
/* 366 */       this.entries.put(convertAlias(paramString), localTrustedCertEntry);
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
/*     */   public void engineDeleteEntry(String paramString)
/*     */     throws KeyStoreException
/*     */   {
/* 380 */     synchronized (this.entries) {
/* 381 */       this.entries.remove(convertAlias(paramString));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> engineAliases()
/*     */   {
/* 391 */     return this.entries.keys();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean engineContainsAlias(String paramString)
/*     */   {
/* 402 */     return this.entries.containsKey(convertAlias(paramString));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int engineSize()
/*     */   {
/* 411 */     return this.entries.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean engineIsKeyEntry(String paramString)
/*     */   {
/* 422 */     Object localObject = this.entries.get(convertAlias(paramString));
/* 423 */     if ((localObject != null) && ((localObject instanceof KeyEntry))) {
/* 424 */       return true;
/*     */     }
/* 426 */     return false;
/*     */   }
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
/* 438 */     Object localObject = this.entries.get(convertAlias(paramString));
/* 439 */     if ((localObject != null) && ((localObject instanceof TrustedCertEntry))) {
/* 440 */       return true;
/*     */     }
/* 442 */     return false;
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
/*     */   public String engineGetCertificateAlias(Certificate paramCertificate)
/*     */   {
/* 465 */     for (Enumeration localEnumeration = this.entries.keys(); localEnumeration.hasMoreElements();) {
/* 466 */       String str = (String)localEnumeration.nextElement();
/* 467 */       Object localObject = this.entries.get(str);
/* 468 */       Certificate localCertificate; if ((localObject instanceof TrustedCertEntry)) {
/* 469 */         localCertificate = ((TrustedCertEntry)localObject).cert;
/* 470 */       } else { if (((KeyEntry)localObject).chain == null) continue;
/* 471 */         localCertificate = ((KeyEntry)localObject).chain[0];
/*     */       }
/*     */       
/*     */ 
/* 475 */       if (localCertificate.equals(paramCertificate)) {
/* 476 */         return str;
/*     */       }
/*     */     }
/* 479 */     return null;
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
/*     */   public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
/*     */     throws IOException, NoSuchAlgorithmException, CertificateException
/*     */   {
/* 498 */     synchronized (this.entries)
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
/* 532 */       if (paramArrayOfChar == null) {
/* 533 */         throw new IllegalArgumentException("password can't be null");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 538 */       MessageDigest localMessageDigest = getPreKeyedHash(paramArrayOfChar);
/* 539 */       DataOutputStream localDataOutputStream = new DataOutputStream(new DigestOutputStream(paramOutputStream, localMessageDigest));
/*     */       
/*     */ 
/* 542 */       localDataOutputStream.writeInt(-17957139);
/*     */       
/* 544 */       localDataOutputStream.writeInt(2);
/*     */       
/* 546 */       localDataOutputStream.writeInt(this.entries.size());
/*     */       
/* 548 */       for (Object localObject1 = this.entries.keys(); ((Enumeration)localObject1).hasMoreElements();)
/*     */       {
/* 550 */         String str = (String)((Enumeration)localObject1).nextElement();
/* 551 */         Object localObject2 = this.entries.get(str);
/*     */         byte[] arrayOfByte;
/* 553 */         if ((localObject2 instanceof KeyEntry))
/*     */         {
/*     */ 
/* 556 */           localDataOutputStream.writeInt(1);
/*     */           
/*     */ 
/* 559 */           localDataOutputStream.writeUTF(str);
/*     */           
/*     */ 
/* 562 */           localDataOutputStream.writeLong(((KeyEntry)localObject2).date.getTime());
/*     */           
/*     */ 
/* 565 */           localDataOutputStream.writeInt(((KeyEntry)localObject2).protectedPrivKey.length);
/* 566 */           localDataOutputStream.write(((KeyEntry)localObject2).protectedPrivKey);
/*     */           
/*     */           int i;
/*     */           
/* 570 */           if (((KeyEntry)localObject2).chain == null) {
/* 571 */             i = 0;
/*     */           } else {
/* 573 */             i = ((KeyEntry)localObject2).chain.length;
/*     */           }
/* 575 */           localDataOutputStream.writeInt(i);
/* 576 */           for (int j = 0; j < i; j++) {
/* 577 */             arrayOfByte = ((KeyEntry)localObject2).chain[j].getEncoded();
/* 578 */             localDataOutputStream.writeUTF(((KeyEntry)localObject2).chain[j].getType());
/* 579 */             localDataOutputStream.writeInt(arrayOfByte.length);
/* 580 */             localDataOutputStream.write(arrayOfByte);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 585 */           localDataOutputStream.writeInt(2);
/*     */           
/*     */ 
/* 588 */           localDataOutputStream.writeUTF(str);
/*     */           
/*     */ 
/* 591 */           localDataOutputStream.writeLong(((TrustedCertEntry)localObject2).date.getTime());
/*     */           
/*     */ 
/* 594 */           arrayOfByte = ((TrustedCertEntry)localObject2).cert.getEncoded();
/* 595 */           localDataOutputStream.writeUTF(((TrustedCertEntry)localObject2).cert.getType());
/* 596 */           localDataOutputStream.writeInt(arrayOfByte.length);
/* 597 */           localDataOutputStream.write(arrayOfByte);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 606 */       localObject1 = localMessageDigest.digest();
/*     */       
/* 608 */       localDataOutputStream.write((byte[])localObject1);
/* 609 */       localDataOutputStream.flush();
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
/*     */   public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
/*     */     throws IOException, NoSuchAlgorithmException, CertificateException
/*     */   {
/* 633 */     synchronized (this.entries)
/*     */     {
/* 635 */       MessageDigest localMessageDigest = null;
/* 636 */       CertificateFactory localCertificateFactory = null;
/* 637 */       Hashtable localHashtable = null;
/* 638 */       ByteArrayInputStream localByteArrayInputStream = null;
/* 639 */       byte[] arrayOfByte1 = null;
/*     */       
/* 641 */       if (paramInputStream == null)
/*     */         return;
/*     */       DataInputStream localDataInputStream;
/* 644 */       if (paramArrayOfChar != null) {
/* 645 */         localMessageDigest = getPreKeyedHash(paramArrayOfChar);
/* 646 */         localDataInputStream = new DataInputStream(new DigestInputStream(paramInputStream, localMessageDigest));
/*     */       } else {
/* 648 */         localDataInputStream = new DataInputStream(paramInputStream);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 653 */       int i = localDataInputStream.readInt();
/* 654 */       int j = localDataInputStream.readInt();
/*     */       
/* 656 */       if ((i != -17957139) || ((j != 1) && (j != 2)))
/*     */       {
/* 658 */         throw new IOException("Invalid keystore format");
/*     */       }
/*     */       
/* 661 */       if (j == 1) {
/* 662 */         localCertificateFactory = CertificateFactory.getInstance("X509");
/*     */       }
/*     */       else {
/* 665 */         localHashtable = new Hashtable(3);
/*     */       }
/*     */       
/* 668 */       this.entries.clear();
/* 669 */       int k = localDataInputStream.readInt();
/*     */       Object localObject1;
/* 671 */       for (int m = 0; m < k; m++)
/*     */       {
/*     */ 
/*     */ 
/* 675 */         int n = localDataInputStream.readInt();
/*     */         String str1;
/* 677 */         if (n == 1)
/*     */         {
/* 679 */           localObject1 = new KeyEntry(null);
/*     */           
/*     */ 
/* 682 */           str1 = localDataInputStream.readUTF();
/*     */           
/*     */ 
/* 685 */           ((KeyEntry)localObject1).date = new Date(localDataInputStream.readLong());
/*     */           
/*     */ 
/*     */ 
/* 689 */           ((KeyEntry)localObject1).protectedPrivKey = IOUtils.readFully(localDataInputStream, localDataInputStream.readInt(), true);
/*     */           
/*     */ 
/* 692 */           int i2 = localDataInputStream.readInt();
/* 693 */           if (i2 > 0) {
/* 694 */             ArrayList localArrayList = new ArrayList(i2 > 10 ? 10 : i2);
/*     */             
/* 696 */             for (int i3 = 0; i3 < i2; i3++) {
/* 697 */               if (j == 2)
/*     */               {
/*     */ 
/*     */ 
/* 701 */                 String str3 = localDataInputStream.readUTF();
/* 702 */                 if (localHashtable.containsKey(str3))
/*     */                 {
/* 704 */                   localCertificateFactory = (CertificateFactory)localHashtable.get(str3);
/*     */                 }
/*     */                 else {
/* 707 */                   localCertificateFactory = CertificateFactory.getInstance(str3);
/*     */                   
/*     */ 
/* 710 */                   localHashtable.put(str3, localCertificateFactory);
/*     */                 }
/*     */               }
/*     */               
/* 714 */               arrayOfByte1 = IOUtils.readFully(localDataInputStream, localDataInputStream.readInt(), true);
/* 715 */               localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
/* 716 */               localArrayList.add(localCertificateFactory.generateCertificate(localByteArrayInputStream));
/* 717 */               localByteArrayInputStream.close();
/*     */             }
/*     */             
/* 720 */             ((KeyEntry)localObject1).chain = ((Certificate[])localArrayList.toArray(new Certificate[i2]));
/*     */           }
/*     */           
/*     */ 
/* 724 */           this.entries.put(str1, localObject1);
/*     */         }
/* 726 */         else if (n == 2)
/*     */         {
/* 728 */           localObject1 = new TrustedCertEntry(null);
/*     */           
/*     */ 
/* 731 */           str1 = localDataInputStream.readUTF();
/*     */           
/*     */ 
/* 734 */           ((TrustedCertEntry)localObject1).date = new Date(localDataInputStream.readLong());
/*     */           
/*     */ 
/* 737 */           if (j == 2)
/*     */           {
/*     */ 
/*     */ 
/* 741 */             String str2 = localDataInputStream.readUTF();
/* 742 */             if (localHashtable.containsKey(str2))
/*     */             {
/* 744 */               localCertificateFactory = (CertificateFactory)localHashtable.get(str2);
/*     */             }
/*     */             else {
/* 747 */               localCertificateFactory = CertificateFactory.getInstance(str2);
/*     */               
/*     */ 
/* 750 */               localHashtable.put(str2, localCertificateFactory);
/*     */             }
/*     */           }
/* 753 */           arrayOfByte1 = IOUtils.readFully(localDataInputStream, localDataInputStream.readInt(), true);
/* 754 */           localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
/* 755 */           ((TrustedCertEntry)localObject1).cert = localCertificateFactory.generateCertificate(localByteArrayInputStream);
/* 756 */           localByteArrayInputStream.close();
/*     */           
/*     */ 
/* 759 */           this.entries.put(str1, localObject1);
/*     */         }
/*     */         else {
/* 762 */           throw new IOException("Unrecognized keystore entry");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 771 */       if (paramArrayOfChar != null)
/*     */       {
/* 773 */         byte[] arrayOfByte2 = localMessageDigest.digest();
/* 774 */         byte[] arrayOfByte3 = new byte[arrayOfByte2.length];
/* 775 */         localDataInputStream.readFully(arrayOfByte3);
/* 776 */         for (int i1 = 0; i1 < arrayOfByte2.length; i1++) {
/* 777 */           if (arrayOfByte2[i1] != arrayOfByte3[i1]) {
/* 778 */             localObject1 = new UnrecoverableKeyException("Password verification failed");
/*     */             
/*     */ 
/*     */ 
/* 782 */             throw ((IOException)new IOException("Keystore was tampered with, or password was incorrect").initCause((Throwable)localObject1));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private MessageDigest getPreKeyedHash(char[] paramArrayOfChar)
/*     */     throws NoSuchAlgorithmException, UnsupportedEncodingException
/*     */   {
/* 798 */     MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
/* 799 */     byte[] arrayOfByte = new byte[paramArrayOfChar.length * 2];
/* 800 */     int i = 0; for (int j = 0; i < paramArrayOfChar.length; i++) {
/* 801 */       arrayOfByte[(j++)] = ((byte)(paramArrayOfChar[i] >> '\b'));
/* 802 */       arrayOfByte[(j++)] = ((byte)paramArrayOfChar[i]);
/*     */     }
/* 804 */     localMessageDigest.update(arrayOfByte);
/* 805 */     for (i = 0; i < arrayOfByte.length; i++)
/* 806 */       arrayOfByte[i] = 0;
/* 807 */     localMessageDigest.update("Mighty Aphrodite".getBytes("UTF8"));
/* 808 */     return localMessageDigest;
/*     */   }
/*     */   
/*     */   private static class KeyEntry
/*     */   {
/*     */     Date date;
/*     */     byte[] protectedPrivKey;
/*     */     Certificate[] chain;
/*     */   }
/*     */   
/*     */   private static class TrustedCertEntry
/*     */   {
/*     */     Date date;
/*     */     Certificate cert;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\JavaKeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
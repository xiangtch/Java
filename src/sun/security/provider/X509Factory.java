/*     */ package sun.security.provider;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PushbackInputStream;
/*     */ import java.security.cert.CRL;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactorySpi;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import sun.security.pkcs.PKCS7;
/*     */ import sun.security.pkcs.ParsingException;
/*     */ import sun.security.provider.certpath.X509CertPath;
/*     */ import sun.security.provider.certpath.X509CertificatePair;
/*     */ import sun.security.util.Cache;
/*     */ import sun.security.util.Cache.EqualByteArray;
/*     */ import sun.security.util.Pem;
/*     */ import sun.security.x509.X509CRLImpl;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509Factory
/*     */   extends CertificateFactorySpi
/*     */ {
/*     */   public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
/*     */   public static final String END_CERT = "-----END CERTIFICATE-----";
/*     */   private static final int ENC_MAX_LENGTH = 4194304;
/*  70 */   private static final Cache<Object, X509CertImpl> certCache = Cache.newSoftMemoryCache(750);
/*     */   
/*  72 */   private static final Cache<Object, X509CRLImpl> crlCache = Cache.newSoftMemoryCache(750);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Certificate engineGenerateCertificate(InputStream paramInputStream)
/*     */     throws CertificateException
/*     */   {
/*  89 */     if (paramInputStream == null)
/*     */     {
/*  91 */       certCache.clear();
/*  92 */       X509CertificatePair.clearCache();
/*  93 */       throw new CertificateException("Missing input stream");
/*     */     }
/*     */     try {
/*  96 */       byte[] arrayOfByte = readOneBlock(paramInputStream);
/*  97 */       if (arrayOfByte != null) {
/*  98 */         X509CertImpl localX509CertImpl = (X509CertImpl)getFromCache(certCache, arrayOfByte);
/*  99 */         if (localX509CertImpl != null) {
/* 100 */           return localX509CertImpl;
/*     */         }
/* 102 */         localX509CertImpl = new X509CertImpl(arrayOfByte);
/* 103 */         addToCache(certCache, localX509CertImpl.getEncodedInternal(), localX509CertImpl);
/* 104 */         return localX509CertImpl;
/*     */       }
/* 106 */       throw new IOException("Empty input");
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 110 */       throw new CertificateException("Could not parse certificate: " + localIOException.toString(), localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int readFully(InputStream paramInputStream, ByteArrayOutputStream paramByteArrayOutputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/* 120 */     int i = 0;
/* 121 */     byte[] arrayOfByte = new byte['ࠀ'];
/* 122 */     while (paramInt > 0) {
/* 123 */       int j = paramInputStream.read(arrayOfByte, 0, paramInt < 2048 ? paramInt : 2048);
/* 124 */       if (j <= 0) {
/*     */         break;
/*     */       }
/* 127 */       paramByteArrayOutputStream.write(arrayOfByte, 0, j);
/* 128 */       i += j;
/* 129 */       paramInt -= j;
/*     */     }
/* 131 */     return i;
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
/*     */   public static synchronized X509CertImpl intern(X509Certificate paramX509Certificate)
/*     */     throws CertificateException
/*     */   {
/* 155 */     if (paramX509Certificate == null) {
/* 156 */       return null;
/*     */     }
/* 158 */     boolean bool = paramX509Certificate instanceof X509CertImpl;
/*     */     byte[] arrayOfByte;
/* 160 */     if (bool) {
/* 161 */       arrayOfByte = ((X509CertImpl)paramX509Certificate).getEncodedInternal();
/*     */     } else {
/* 163 */       arrayOfByte = paramX509Certificate.getEncoded();
/*     */     }
/* 165 */     X509CertImpl localX509CertImpl = (X509CertImpl)getFromCache(certCache, arrayOfByte);
/* 166 */     if (localX509CertImpl != null) {
/* 167 */       return localX509CertImpl;
/*     */     }
/* 169 */     if (bool) {
/* 170 */       localX509CertImpl = (X509CertImpl)paramX509Certificate;
/*     */     } else {
/* 172 */       localX509CertImpl = new X509CertImpl(arrayOfByte);
/* 173 */       arrayOfByte = localX509CertImpl.getEncodedInternal();
/*     */     }
/* 175 */     addToCache(certCache, arrayOfByte, localX509CertImpl);
/* 176 */     return localX509CertImpl;
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
/*     */   public static synchronized X509CRLImpl intern(X509CRL paramX509CRL)
/*     */     throws CRLException
/*     */   {
/* 191 */     if (paramX509CRL == null) {
/* 192 */       return null;
/*     */     }
/* 194 */     boolean bool = paramX509CRL instanceof X509CRLImpl;
/*     */     byte[] arrayOfByte;
/* 196 */     if (bool) {
/* 197 */       arrayOfByte = ((X509CRLImpl)paramX509CRL).getEncodedInternal();
/*     */     } else {
/* 199 */       arrayOfByte = paramX509CRL.getEncoded();
/*     */     }
/* 201 */     X509CRLImpl localX509CRLImpl = (X509CRLImpl)getFromCache(crlCache, arrayOfByte);
/* 202 */     if (localX509CRLImpl != null) {
/* 203 */       return localX509CRLImpl;
/*     */     }
/* 205 */     if (bool) {
/* 206 */       localX509CRLImpl = (X509CRLImpl)paramX509CRL;
/*     */     } else {
/* 208 */       localX509CRLImpl = new X509CRLImpl(arrayOfByte);
/* 209 */       arrayOfByte = localX509CRLImpl.getEncodedInternal();
/*     */     }
/* 211 */     addToCache(crlCache, arrayOfByte, localX509CRLImpl);
/* 212 */     return localX509CRLImpl;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static synchronized <K, V> V getFromCache(Cache<K, V> paramCache, byte[] paramArrayOfByte)
/*     */   {
/* 220 */     EqualByteArray localEqualByteArray = new EqualByteArray(paramArrayOfByte);
/* 221 */     return (V)paramCache.get(localEqualByteArray);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static synchronized <V> void addToCache(Cache<Object, V> paramCache, byte[] paramArrayOfByte, V paramV)
/*     */   {
/* 229 */     if (paramArrayOfByte.length > 4194304) {
/* 230 */       return;
/*     */     }
/* 232 */     EqualByteArray localEqualByteArray = new EqualByteArray(paramArrayOfByte);
/* 233 */     paramCache.put(localEqualByteArray, paramV);
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
/*     */   public CertPath engineGenerateCertPath(InputStream paramInputStream)
/*     */     throws CertificateException
/*     */   {
/* 251 */     if (paramInputStream == null) {
/* 252 */       throw new CertificateException("Missing input stream");
/*     */     }
/*     */     try {
/* 255 */       byte[] arrayOfByte = readOneBlock(paramInputStream);
/* 256 */       if (arrayOfByte != null) {
/* 257 */         return new X509CertPath(new ByteArrayInputStream(arrayOfByte));
/*     */       }
/* 259 */       throw new IOException("Empty input");
/*     */     }
/*     */     catch (IOException localIOException) {
/* 262 */       throw new CertificateException(localIOException.getMessage());
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
/*     */   public CertPath engineGenerateCertPath(InputStream paramInputStream, String paramString)
/*     */     throws CertificateException
/*     */   {
/* 283 */     if (paramInputStream == null) {
/* 284 */       throw new CertificateException("Missing input stream");
/*     */     }
/*     */     try {
/* 287 */       byte[] arrayOfByte = readOneBlock(paramInputStream);
/* 288 */       if (arrayOfByte != null) {
/* 289 */         return new X509CertPath(new ByteArrayInputStream(arrayOfByte), paramString);
/*     */       }
/* 291 */       throw new IOException("Empty input");
/*     */     }
/*     */     catch (IOException localIOException) {
/* 294 */       throw new CertificateException(localIOException.getMessage());
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
/*     */   public CertPath engineGenerateCertPath(List<? extends Certificate> paramList)
/*     */     throws CertificateException
/*     */   {
/* 317 */     return new X509CertPath(paramList);
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
/*     */   public Iterator<String> engineGetCertPathEncodings()
/*     */   {
/* 334 */     return X509CertPath.getEncodingsStatic();
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
/*     */   public Collection<? extends Certificate> engineGenerateCertificates(InputStream paramInputStream)
/*     */     throws CertificateException
/*     */   {
/* 352 */     if (paramInputStream == null) {
/* 353 */       throw new CertificateException("Missing input stream");
/*     */     }
/*     */     try {
/* 356 */       return parseX509orPKCS7Cert(paramInputStream);
/*     */     } catch (IOException localIOException) {
/* 358 */       throw new CertificateException(localIOException);
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
/*     */   public CRL engineGenerateCRL(InputStream paramInputStream)
/*     */     throws CRLException
/*     */   {
/* 378 */     if (paramInputStream == null)
/*     */     {
/* 380 */       crlCache.clear();
/* 381 */       throw new CRLException("Missing input stream");
/*     */     }
/*     */     try {
/* 384 */       byte[] arrayOfByte = readOneBlock(paramInputStream);
/* 385 */       if (arrayOfByte != null) {
/* 386 */         X509CRLImpl localX509CRLImpl = (X509CRLImpl)getFromCache(crlCache, arrayOfByte);
/* 387 */         if (localX509CRLImpl != null) {
/* 388 */           return localX509CRLImpl;
/*     */         }
/* 390 */         localX509CRLImpl = new X509CRLImpl(arrayOfByte);
/* 391 */         addToCache(crlCache, localX509CRLImpl.getEncodedInternal(), localX509CRLImpl);
/* 392 */         return localX509CRLImpl;
/*     */       }
/* 394 */       throw new IOException("Empty input");
/*     */     }
/*     */     catch (IOException localIOException) {
/* 397 */       throw new CRLException(localIOException.getMessage());
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
/*     */   public Collection<? extends CRL> engineGenerateCRLs(InputStream paramInputStream)
/*     */     throws CRLException
/*     */   {
/* 416 */     if (paramInputStream == null) {
/* 417 */       throw new CRLException("Missing input stream");
/*     */     }
/*     */     try {
/* 420 */       return parseX509orPKCS7CRL(paramInputStream);
/*     */     } catch (IOException localIOException) {
/* 422 */       throw new CRLException(localIOException.getMessage());
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
/*     */   private Collection<? extends Certificate> parseX509orPKCS7Cert(InputStream paramInputStream)
/*     */     throws CertificateException, IOException
/*     */   {
/* 437 */     PushbackInputStream localPushbackInputStream = new PushbackInputStream(paramInputStream);
/* 438 */     ArrayList localArrayList = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 444 */     int i = localPushbackInputStream.read();
/* 445 */     if (i == -1) {
/* 446 */       return new ArrayList(0);
/*     */     }
/* 448 */     localPushbackInputStream.unread(i);
/* 449 */     byte[] arrayOfByte = readOneBlock(localPushbackInputStream);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 455 */     if (arrayOfByte == null) {
/* 456 */       throw new CertificateException("No certificate data found");
/*     */     }
/*     */     try
/*     */     {
/* 460 */       PKCS7 localPKCS7 = new PKCS7(arrayOfByte);
/* 461 */       X509Certificate[] arrayOfX509Certificate = localPKCS7.getCertificates();
/*     */       
/* 463 */       if (arrayOfX509Certificate != null) {
/* 464 */         return Arrays.asList(arrayOfX509Certificate);
/*     */       }
/*     */       
/* 467 */       return new ArrayList(0);
/*     */     }
/*     */     catch (ParsingException localParsingException) {
/* 470 */       while (arrayOfByte != null) {
/* 471 */         localArrayList.add(new X509CertImpl(arrayOfByte));
/* 472 */         arrayOfByte = readOneBlock(localPushbackInputStream);
/*     */       }
/*     */     }
/* 475 */     return localArrayList;
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
/*     */   private Collection<? extends CRL> parseX509orPKCS7CRL(InputStream paramInputStream)
/*     */     throws CRLException, IOException
/*     */   {
/* 489 */     PushbackInputStream localPushbackInputStream = new PushbackInputStream(paramInputStream);
/* 490 */     ArrayList localArrayList = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 496 */     int i = localPushbackInputStream.read();
/* 497 */     if (i == -1) {
/* 498 */       return new ArrayList(0);
/*     */     }
/* 500 */     localPushbackInputStream.unread(i);
/* 501 */     byte[] arrayOfByte = readOneBlock(localPushbackInputStream);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 507 */     if (arrayOfByte == null) {
/* 508 */       throw new CRLException("No CRL data found");
/*     */     }
/*     */     try
/*     */     {
/* 512 */       PKCS7 localPKCS7 = new PKCS7(arrayOfByte);
/* 513 */       X509CRL[] arrayOfX509CRL = localPKCS7.getCRLs();
/*     */       
/* 515 */       if (arrayOfX509CRL != null) {
/* 516 */         return Arrays.asList(arrayOfX509CRL);
/*     */       }
/*     */       
/* 519 */       return new ArrayList(0);
/*     */     }
/*     */     catch (ParsingException localParsingException) {
/* 522 */       while (arrayOfByte != null) {
/* 523 */         localArrayList.add(new X509CRLImpl(arrayOfByte));
/* 524 */         arrayOfByte = readOneBlock(localPushbackInputStream);
/*     */       }
/*     */     }
/* 527 */     return localArrayList;
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
/*     */   private static byte[] readOneBlock(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 545 */     int i = paramInputStream.read();
/* 546 */     if (i == -1) {
/* 547 */       return null;
/*     */     }
/* 549 */     if (i == 48) {
/* 550 */       localObject = new ByteArrayOutputStream(2048);
/* 551 */       ((ByteArrayOutputStream)localObject).write(i);
/* 552 */       readBERInternal(paramInputStream, (ByteArrayOutputStream)localObject, i);
/* 553 */       return ((ByteArrayOutputStream)localObject).toByteArray();
/*     */     }
/*     */     
/* 556 */     Object localObject = new char['ࠀ'];
/* 557 */     int j = 0;
/*     */     
/*     */ 
/* 560 */     int k = i == 45 ? 1 : 0;
/* 561 */     int m = i == 45 ? -1 : i;
/*     */     int n;
/* 563 */     for (;;) { n = paramInputStream.read();
/* 564 */       if (n == -1)
/*     */       {
/*     */ 
/* 567 */         return null;
/*     */       }
/* 569 */       if (n == 45) {
/* 570 */         k++;
/*     */       } else {
/* 572 */         k = 0;
/* 573 */         m = n;
/*     */       }
/* 575 */       if ((k == 5) && ((m == -1) || (m == 13) || (m == 10))) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 582 */     StringBuilder localStringBuilder1 = new StringBuilder("-----");
/*     */     int i1;
/* 584 */     for (;;) { i1 = paramInputStream.read();
/* 585 */       if (i1 == -1) {
/* 586 */         throw new IOException("Incomplete data");
/*     */       }
/* 588 */       if (i1 == 10) {
/* 589 */         n = 10;
/* 590 */         break;
/*     */       }
/* 592 */       if (i1 == 13) {
/* 593 */         i1 = paramInputStream.read();
/* 594 */         if (i1 == -1) {
/* 595 */           throw new IOException("Incomplete data");
/*     */         }
/* 597 */         if (i1 == 10) {
/* 598 */           n = 10; break;
/*     */         }
/* 600 */         n = 13;
/* 601 */         localObject[(j++)] = ((char)i1);
/*     */         
/* 603 */         break;
/*     */       }
/* 605 */       localStringBuilder1.append((char)i1);
/*     */     }
/*     */     
/*     */     for (;;)
/*     */     {
/* 610 */       i1 = paramInputStream.read();
/* 611 */       if (i1 == -1) {
/* 612 */         throw new IOException("Incomplete data");
/*     */       }
/* 614 */       if (i1 == 45) break;
/* 615 */       localObject[(j++)] = ((char)i1);
/* 616 */       if (j >= localObject.length) {
/* 617 */         localObject = Arrays.copyOf((char[])localObject, localObject.length + 1024);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 625 */     StringBuilder localStringBuilder2 = new StringBuilder("-");
/*     */     for (;;) {
/* 627 */       int i2 = paramInputStream.read();
/*     */       
/*     */ 
/* 630 */       if ((i2 == -1) || (i2 == n) || (i2 == 10)) {
/*     */         break;
/*     */       }
/* 633 */       if (i2 != 13) { localStringBuilder2.append((char)i2);
/*     */       }
/*     */     }
/* 636 */     checkHeaderFooter(localStringBuilder1.toString(), localStringBuilder2.toString());
/*     */     
/* 638 */     return Pem.decode(new String((char[])localObject, 0, j));
/*     */   }
/*     */   
/*     */   private static void checkHeaderFooter(String paramString1, String paramString2)
/*     */     throws IOException
/*     */   {
/* 644 */     if ((paramString1.length() < 16) || (!paramString1.startsWith("-----BEGIN ")) || 
/* 645 */       (!paramString1.endsWith("-----"))) {
/* 646 */       throw new IOException("Illegal header: " + paramString1);
/*     */     }
/* 648 */     if ((paramString2.length() < 14) || (!paramString2.startsWith("-----END ")) || 
/* 649 */       (!paramString2.endsWith("-----"))) {
/* 650 */       throw new IOException("Illegal footer: " + paramString2);
/*     */     }
/* 652 */     String str1 = paramString1.substring(11, paramString1.length() - 5);
/* 653 */     String str2 = paramString2.substring(9, paramString2.length() - 5);
/* 654 */     if (!str1.equals(str2)) {
/* 655 */       throw new IOException("Header and footer do not match: " + paramString1 + " " + paramString2);
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
/*     */   private static int readBERInternal(InputStream paramInputStream, ByteArrayOutputStream paramByteArrayOutputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/* 673 */     if (paramInt == -1) {
/* 674 */       paramInt = paramInputStream.read();
/* 675 */       if (paramInt == -1) {
/* 676 */         throw new IOException("BER/DER tag info absent");
/*     */       }
/* 678 */       if ((paramInt & 0x1F) == 31) {
/* 679 */         throw new IOException("Multi octets tag not supported");
/*     */       }
/* 681 */       paramByteArrayOutputStream.write(paramInt);
/*     */     }
/*     */     
/* 684 */     int i = paramInputStream.read();
/* 685 */     if (i == -1) {
/* 686 */       throw new IOException("BER/DER length info absent");
/*     */     }
/* 688 */     paramByteArrayOutputStream.write(i);
/*     */     
/*     */     int k;
/*     */     
/* 692 */     if (i == 128) {
/* 693 */       if ((paramInt & 0x20) != 32) {
/* 694 */         throw new IOException("Non constructed encoding must have definite length");
/*     */       }
/*     */       for (;;)
/*     */       {
/* 698 */         k = readBERInternal(paramInputStream, paramByteArrayOutputStream, -1);
/* 699 */         if (k == 0)
/*     */           break;
/*     */       }
/*     */     }
/*     */     int j;
/* 704 */     if (i < 128) {
/* 705 */       j = i;
/* 706 */     } else if (i == 129) {
/* 707 */       j = paramInputStream.read();
/* 708 */       if (j == -1) {
/* 709 */         throw new IOException("Incomplete BER/DER length info");
/*     */       }
/* 711 */       paramByteArrayOutputStream.write(j); } else { int m;
/* 712 */       if (i == 130) {
/* 713 */         k = paramInputStream.read();
/* 714 */         m = paramInputStream.read();
/* 715 */         if (m == -1) {
/* 716 */           throw new IOException("Incomplete BER/DER length info");
/*     */         }
/* 718 */         paramByteArrayOutputStream.write(k);
/* 719 */         paramByteArrayOutputStream.write(m);
/* 720 */         j = k << 8 | m; } else { int n;
/* 721 */         if (i == 131) {
/* 722 */           k = paramInputStream.read();
/* 723 */           m = paramInputStream.read();
/* 724 */           n = paramInputStream.read();
/* 725 */           if (n == -1) {
/* 726 */             throw new IOException("Incomplete BER/DER length info");
/*     */           }
/* 728 */           paramByteArrayOutputStream.write(k);
/* 729 */           paramByteArrayOutputStream.write(m);
/* 730 */           paramByteArrayOutputStream.write(n);
/* 731 */           j = k << 16 | m << 8 | n;
/* 732 */         } else if (i == 132) {
/* 733 */           k = paramInputStream.read();
/* 734 */           m = paramInputStream.read();
/* 735 */           n = paramInputStream.read();
/* 736 */           int i1 = paramInputStream.read();
/* 737 */           if (i1 == -1) {
/* 738 */             throw new IOException("Incomplete BER/DER length info");
/*     */           }
/* 740 */           if (k > 127) {
/* 741 */             throw new IOException("Invalid BER/DER data (a little huge?)");
/*     */           }
/* 743 */           paramByteArrayOutputStream.write(k);
/* 744 */           paramByteArrayOutputStream.write(m);
/* 745 */           paramByteArrayOutputStream.write(n);
/* 746 */           paramByteArrayOutputStream.write(i1);
/* 747 */           j = k << 24 | m << 16 | n << 8 | i1;
/*     */         }
/*     */         else {
/* 750 */           throw new IOException("Invalid BER/DER data (too huge?)");
/*     */         } } }
/* 752 */     if (readFully(paramInputStream, paramByteArrayOutputStream, j) != j) {
/* 753 */       throw new IOException("Incomplete BER/DER data");
/*     */     }
/*     */     
/* 756 */     return paramInt;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\X509Factory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
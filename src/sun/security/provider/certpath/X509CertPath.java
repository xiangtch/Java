/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import sun.security.pkcs.ContentInfo;
/*     */ import sun.security.pkcs.PKCS7;
/*     */ import sun.security.pkcs.SignerInfo;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509CertPath
/*     */   extends CertPath
/*     */ {
/*     */   private static final long serialVersionUID = 4989800333263052980L;
/*     */   private List<X509Certificate> certs;
/*     */   private static final String COUNT_ENCODING = "count";
/*     */   private static final String PKCS7_ENCODING = "PKCS7";
/*     */   private static final String PKIPATH_ENCODING = "PkiPath";
/*     */   private static final Collection<String> encodingList;
/*     */   
/*     */   static
/*     */   {
/*  86 */     ArrayList localArrayList = new ArrayList(2);
/*  87 */     localArrayList.add("PkiPath");
/*  88 */     localArrayList.add("PKCS7");
/*  89 */     encodingList = Collections.unmodifiableCollection(localArrayList);
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
/*     */   public X509CertPath(List<? extends Certificate> paramList)
/*     */     throws CertificateException
/*     */   {
/* 105 */     super("X.509");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 114 */     for (Object localObject : paramList) {
/* 115 */       if (!(localObject instanceof X509Certificate))
/*     */       {
/*     */ 
/* 118 */         throw new CertificateException("List is not all X509Certificates: " + localObject.getClass().getName());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 126 */     this.certs = Collections.unmodifiableList(new ArrayList(paramList));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CertPath(InputStream paramInputStream)
/*     */     throws CertificateException
/*     */   {
/* 139 */     this(paramInputStream, "PkiPath");
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
/*     */   public X509CertPath(InputStream paramInputStream, String paramString)
/*     */     throws CertificateException
/*     */   {
/* 154 */     super("X.509");
/*     */     
/* 156 */     switch (paramString) {
/*     */     case "PkiPath": 
/* 158 */       this.certs = parsePKIPATH(paramInputStream);
/* 159 */       break;
/*     */     case "PKCS7": 
/* 161 */       this.certs = parsePKCS7(paramInputStream);
/* 162 */       break;
/*     */     default: 
/* 164 */       throw new CertificateException("unsupported encoding");
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static List<X509Certificate> parsePKIPATH(InputStream paramInputStream)
/*     */     throws CertificateException
/*     */   {
/* 178 */     ArrayList localArrayList = null;
/* 179 */     CertificateFactory localCertificateFactory = null;
/*     */     
/* 181 */     if (paramInputStream == null) {
/* 182 */       throw new CertificateException("input stream is null");
/*     */     }
/*     */     try
/*     */     {
/* 186 */       DerInputStream localDerInputStream = new DerInputStream(readAllBytes(paramInputStream));
/* 187 */       DerValue[] arrayOfDerValue = localDerInputStream.getSequence(3);
/* 188 */       if (arrayOfDerValue.length == 0) {
/* 189 */         return Collections.emptyList();
/*     */       }
/*     */       
/* 192 */       localCertificateFactory = CertificateFactory.getInstance("X.509");
/* 193 */       localArrayList = new ArrayList(arrayOfDerValue.length);
/*     */       
/*     */ 
/* 196 */       for (int i = arrayOfDerValue.length - 1; i >= 0; i--) {
/* 197 */         localArrayList.add(
/* 198 */           (X509Certificate)localCertificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfDerValue[i].toByteArray())));
/*     */       }
/*     */       
/* 201 */       return Collections.unmodifiableList(localArrayList);
/*     */     }
/*     */     catch (IOException localIOException) {
/* 204 */       throw new CertificateException("IOException parsing PkiPath data: " + localIOException, localIOException);
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
/*     */   private static List<X509Certificate> parsePKCS7(InputStream paramInputStream)
/*     */     throws CertificateException
/*     */   {
/* 221 */     if (paramInputStream == null) {
/* 222 */       throw new CertificateException("input stream is null");
/*     */     }
/*     */     Object localObject;
/*     */     try {
/* 226 */       if (!paramInputStream.markSupported())
/*     */       {
/*     */ 
/* 229 */         paramInputStream = new ByteArrayInputStream(readAllBytes(paramInputStream));
/*     */       }
/* 231 */       PKCS7 localPKCS7 = new PKCS7(paramInputStream);
/*     */       
/* 233 */       X509Certificate[] arrayOfX509Certificate = localPKCS7.getCertificates();
/*     */       
/* 235 */       if (arrayOfX509Certificate != null) {
/* 236 */         localObject = Arrays.asList(arrayOfX509Certificate);
/*     */       }
/*     */       else {
/* 239 */         localObject = new ArrayList(0);
/*     */       }
/*     */     } catch (IOException localIOException) {
/* 242 */       throw new CertificateException("IOException parsing PKCS7 data: " + localIOException);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 249 */     return Collections.unmodifiableList((List)localObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static byte[] readAllBytes(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 259 */     byte[] arrayOfByte = new byte[' '];
/* 260 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
/*     */     int i;
/* 262 */     while ((i = paramInputStream.read(arrayOfByte)) != -1) {
/* 263 */       localByteArrayOutputStream.write(arrayOfByte, 0, i);
/*     */     }
/* 265 */     return localByteArrayOutputStream.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */     throws CertificateEncodingException
/*     */   {
/* 278 */     return encodePKIPATH();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] encodePKIPATH()
/*     */     throws CertificateEncodingException
/*     */   {
/* 289 */     ListIterator localListIterator = this.certs.listIterator(this.certs.size());
/*     */     try {
/* 291 */       DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */       
/*     */ 
/* 294 */       while (localListIterator.hasPrevious()) {
/* 295 */         localObject = (X509Certificate)localListIterator.previous();
/*     */         
/* 297 */         if (this.certs.lastIndexOf(localObject) != this.certs.indexOf(localObject)) {
/* 298 */           throw new CertificateEncodingException("Duplicate Certificate");
/*     */         }
/*     */         
/*     */ 
/* 302 */         byte[] arrayOfByte = ((X509Certificate)localObject).getEncoded();
/* 303 */         localDerOutputStream.write(arrayOfByte);
/*     */       }
/*     */       
/*     */ 
/* 307 */       Object localObject = new DerOutputStream();
/* 308 */       ((DerOutputStream)localObject).write((byte)48, localDerOutputStream);
/* 309 */       return ((DerOutputStream)localObject).toByteArray();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 312 */       throw new CertificateEncodingException("IOException encoding PkiPath data: " + localIOException, localIOException);
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
/*     */   private byte[] encodePKCS7()
/*     */     throws CertificateEncodingException
/*     */   {
/* 326 */     PKCS7 localPKCS7 = new PKCS7(new AlgorithmId[0], new ContentInfo(ContentInfo.DATA_OID, null), (X509Certificate[])this.certs.toArray(new X509Certificate[this.certs.size()]), new SignerInfo[0]);
/*     */     
/* 328 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */     try {
/* 330 */       localPKCS7.encodeSignedData(localDerOutputStream);
/*     */     } catch (IOException localIOException) {
/* 332 */       throw new CertificateEncodingException(localIOException.getMessage());
/*     */     }
/* 334 */     return localDerOutputStream.toByteArray();
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
/*     */   public byte[] getEncoded(String paramString)
/*     */     throws CertificateEncodingException
/*     */   {
/* 349 */     switch (paramString) {
/*     */     case "PkiPath": 
/* 351 */       return encodePKIPATH();
/*     */     case "PKCS7": 
/* 353 */       return encodePKCS7();
/*     */     }
/* 355 */     throw new CertificateEncodingException("unsupported encoding");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Iterator<String> getEncodingsStatic()
/*     */   {
/* 367 */     return encodingList.iterator();
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
/*     */   public Iterator<String> getEncodings()
/*     */   {
/* 383 */     return getEncodingsStatic();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<X509Certificate> getCertificates()
/*     */   {
/* 395 */     return this.certs;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\X509CertPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
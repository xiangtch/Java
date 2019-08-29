/*     */ package sun.security.pkcs;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.net.URI;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Principal;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.SignatureException;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509CRL;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import sun.security.timestamp.HttpTimestamper;
/*     */ import sun.security.timestamp.TSRequest;
/*     */ import sun.security.timestamp.TSResponse;
/*     */ import sun.security.timestamp.TimestampToken;
/*     */ import sun.security.timestamp.Timestamper;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerEncoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ import sun.security.x509.X500Name;
/*     */ import sun.security.x509.X509CRLImpl;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ import sun.security.x509.X509CertInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PKCS7
/*     */ {
/*     */   private ObjectIdentifier contentType;
/*  61 */   private BigInteger version = null;
/*  62 */   private AlgorithmId[] digestAlgorithmIds = null;
/*  63 */   private ContentInfo contentInfo = null;
/*  64 */   private X509Certificate[] certificates = null;
/*  65 */   private X509CRL[] crls = null;
/*  66 */   private SignerInfo[] signerInfos = null;
/*     */   
/*  68 */   private boolean oldStyle = false;
/*     */   private Principal[] certIssuerNames;
/*     */   private static final String KP_TIMESTAMPING_OID = "1.3.6.1.5.5.7.3.8";
/*     */   private static final String EXTENDED_KEY_USAGE_OID = "2.5.29.37";
/*     */   
/*     */   private static class SecureRandomHolder
/*     */   {
/*     */     static final SecureRandom RANDOM;
/*     */     
/*     */     static
/*     */     {
/*  79 */       SecureRandom localSecureRandom = null;
/*     */       try {
/*  81 */         localSecureRandom = SecureRandom.getInstance("SHA1PRNG");
/*     */       }
/*     */       catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
/*     */       
/*  85 */       RANDOM = localSecureRandom;
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
/*     */   public PKCS7(InputStream paramInputStream)
/*     */     throws ParsingException, IOException
/*     */   {
/* 108 */     DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
/* 109 */     byte[] arrayOfByte = new byte[localDataInputStream.available()];
/* 110 */     localDataInputStream.readFully(arrayOfByte);
/*     */     
/* 112 */     parse(new DerInputStream(arrayOfByte));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS7(DerInputStream paramDerInputStream)
/*     */     throws ParsingException
/*     */   {
/* 123 */     parse(paramDerInputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS7(byte[] paramArrayOfByte)
/*     */     throws ParsingException
/*     */   {
/*     */     try
/*     */     {
/* 135 */       DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte);
/* 136 */       parse(localDerInputStream);
/*     */     } catch (IOException localIOException) {
/* 138 */       ParsingException localParsingException = new ParsingException("Unable to parse the encoded bytes");
/*     */       
/* 140 */       localParsingException.initCause(localIOException);
/* 141 */       throw localParsingException;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void parse(DerInputStream paramDerInputStream)
/*     */     throws ParsingException
/*     */   {
/*     */     try
/*     */     {
/* 152 */       paramDerInputStream.mark(paramDerInputStream.available());
/*     */       
/* 154 */       parse(paramDerInputStream, false);
/*     */     } catch (IOException localIOException1) {
/*     */       try {
/* 157 */         paramDerInputStream.reset();
/*     */         
/* 159 */         parse(paramDerInputStream, true);
/* 160 */         this.oldStyle = true;
/*     */       }
/*     */       catch (IOException localIOException2) {
/* 163 */         ParsingException localParsingException = new ParsingException(localIOException2.getMessage());
/* 164 */         localParsingException.initCause(localIOException1);
/* 165 */         localParsingException.addSuppressed(localIOException2);
/* 166 */         throw localParsingException;
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
/*     */ 
/*     */   private void parse(DerInputStream paramDerInputStream, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 181 */     this.contentInfo = new ContentInfo(paramDerInputStream, paramBoolean);
/* 182 */     this.contentType = this.contentInfo.contentType;
/* 183 */     DerValue localDerValue = this.contentInfo.getContent();
/*     */     
/* 185 */     if (this.contentType.equals(ContentInfo.SIGNED_DATA_OID)) {
/* 186 */       parseSignedData(localDerValue);
/* 187 */     } else if (this.contentType.equals(ContentInfo.OLD_SIGNED_DATA_OID))
/*     */     {
/* 189 */       parseOldSignedData(localDerValue);
/* 190 */     } else if (this.contentType.equals(ContentInfo.NETSCAPE_CERT_SEQUENCE_OID))
/*     */     {
/* 192 */       parseNetscapeCertChain(localDerValue);
/*     */     } else {
/* 194 */       throw new ParsingException("content type " + this.contentType + " not supported.");
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
/*     */   public PKCS7(AlgorithmId[] paramArrayOfAlgorithmId, ContentInfo paramContentInfo, X509Certificate[] paramArrayOfX509Certificate, X509CRL[] paramArrayOfX509CRL, SignerInfo[] paramArrayOfSignerInfo)
/*     */   {
/* 214 */     this.version = BigInteger.ONE;
/* 215 */     this.digestAlgorithmIds = paramArrayOfAlgorithmId;
/* 216 */     this.contentInfo = paramContentInfo;
/* 217 */     this.certificates = paramArrayOfX509Certificate;
/* 218 */     this.crls = paramArrayOfX509CRL;
/* 219 */     this.signerInfos = paramArrayOfSignerInfo;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PKCS7(AlgorithmId[] paramArrayOfAlgorithmId, ContentInfo paramContentInfo, X509Certificate[] paramArrayOfX509Certificate, SignerInfo[] paramArrayOfSignerInfo)
/*     */   {
/* 226 */     this(paramArrayOfAlgorithmId, paramContentInfo, paramArrayOfX509Certificate, null, paramArrayOfSignerInfo);
/*     */   }
/*     */   
/*     */   private void parseNetscapeCertChain(DerValue paramDerValue) throws ParsingException, IOException
/*     */   {
/* 231 */     DerInputStream localDerInputStream = new DerInputStream(paramDerValue.toByteArray());
/* 232 */     DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
/* 233 */     this.certificates = new X509Certificate[arrayOfDerValue.length];
/*     */     
/* 235 */     CertificateFactory localCertificateFactory = null;
/*     */     try {
/* 237 */       localCertificateFactory = CertificateFactory.getInstance("X.509");
/*     */     }
/*     */     catch (CertificateException localCertificateException1) {}
/*     */     
/*     */ 
/* 242 */     for (int i = 0; i < arrayOfDerValue.length; i++) {
/* 243 */       ByteArrayInputStream localByteArrayInputStream = null;
/*     */       try {
/* 245 */         if (localCertificateFactory == null) {
/* 246 */           this.certificates[i] = new X509CertImpl(arrayOfDerValue[i]);
/*     */         } else {
/* 248 */           byte[] arrayOfByte = arrayOfDerValue[i].toByteArray();
/* 249 */           localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
/* 250 */           this.certificates[i] = 
/* 251 */             ((X509Certificate)localCertificateFactory.generateCertificate(localByteArrayInputStream));
/* 252 */           localByteArrayInputStream.close();
/* 253 */           localByteArrayInputStream = null;
/*     */         }
/*     */       } catch (CertificateException localCertificateException2) {
/* 256 */         localParsingException = new ParsingException(localCertificateException2.getMessage());
/* 257 */         localParsingException.initCause(localCertificateException2);
/* 258 */         throw localParsingException;
/*     */       } catch (IOException localIOException) {
/* 260 */         ParsingException localParsingException = new ParsingException(localIOException.getMessage());
/* 261 */         localParsingException.initCause(localIOException);
/* 262 */         throw localParsingException;
/*     */       } finally {
/* 264 */         if (localByteArrayInputStream != null) {
/* 265 */           localByteArrayInputStream.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void parseSignedData(DerValue paramDerValue) throws ParsingException, IOException
/*     */   {
/* 273 */     DerInputStream localDerInputStream = paramDerValue.toDerInputStream();
/*     */     
/*     */ 
/* 276 */     this.version = localDerInputStream.getBigInteger();
/*     */     
/*     */ 
/* 279 */     DerValue[] arrayOfDerValue1 = localDerInputStream.getSet(1);
/* 280 */     int i = arrayOfDerValue1.length;
/* 281 */     this.digestAlgorithmIds = new AlgorithmId[i];
/*     */     try {
/* 283 */       for (int j = 0; j < i; j++) {
/* 284 */         localObject1 = arrayOfDerValue1[j];
/* 285 */         this.digestAlgorithmIds[j] = AlgorithmId.parse((DerValue)localObject1);
/*     */       }
/*     */       
/*     */     }
/*     */     catch (IOException localIOException1)
/*     */     {
/* 291 */       Object localObject1 = new ParsingException("Error parsing digest AlgorithmId IDs: " + localIOException1.getMessage());
/* 292 */       ((ParsingException)localObject1).initCause(localIOException1);
/* 293 */       throw ((Throwable)localObject1);
/*     */     }
/*     */     
/* 296 */     this.contentInfo = new ContentInfo(localDerInputStream);
/*     */     
/* 298 */     CertificateFactory localCertificateFactory = null;
/*     */     try {
/* 300 */       localCertificateFactory = CertificateFactory.getInstance("X.509");
/*     */     }
/*     */     catch (CertificateException localCertificateException1) {}
/*     */     
/*     */ 
/*     */ 
/*     */     Object localObject3;
/*     */     
/*     */ 
/* 309 */     if ((byte)localDerInputStream.peekByte() == -96) {
/* 310 */       arrayOfDerValue2 = localDerInputStream.getSet(2, true);
/*     */       
/* 312 */       i = arrayOfDerValue2.length;
/* 313 */       this.certificates = new X509Certificate[i];
/* 314 */       k = 0;
/*     */       
/* 316 */       for (int m = 0; m < i; m++) {
/* 317 */         localObject3 = null;
/*     */         try {
/* 319 */           int n = arrayOfDerValue2[m].getTag();
/*     */           
/*     */ 
/* 322 */           if (n == 48) {
/* 323 */             if (localCertificateFactory == null) {
/* 324 */               this.certificates[k] = new X509CertImpl(arrayOfDerValue2[m]);
/*     */             } else {
/* 326 */               localObject4 = arrayOfDerValue2[m].toByteArray();
/* 327 */               localObject3 = new ByteArrayInputStream((byte[])localObject4);
/* 328 */               this.certificates[k] = 
/* 329 */                 ((X509Certificate)localCertificateFactory.generateCertificate((InputStream)localObject3));
/* 330 */               ((ByteArrayInputStream)localObject3).close();
/* 331 */               localObject3 = null;
/*     */             }
/* 333 */             k++;
/*     */           }
/*     */         } catch (CertificateException localCertificateException2) {
/* 336 */           localObject4 = new ParsingException(localCertificateException2.getMessage());
/* 337 */           ((ParsingException)localObject4).initCause(localCertificateException2);
/* 338 */           throw ((Throwable)localObject4);
/*     */         } catch (IOException localIOException2) {
/* 340 */           Object localObject4 = new ParsingException(localIOException2.getMessage());
/* 341 */           ((ParsingException)localObject4).initCause(localIOException2);
/* 342 */           throw ((Throwable)localObject4);
/*     */         } finally {
/* 344 */           if (localObject3 != null)
/* 345 */             ((ByteArrayInputStream)localObject3).close();
/*     */         }
/*     */       }
/* 348 */       if (k != i) {
/* 349 */         this.certificates = ((X509Certificate[])Arrays.copyOf(this.certificates, k));
/*     */       }
/*     */     }
/*     */     
/*     */     Object localObject2;
/* 354 */     if ((byte)localDerInputStream.peekByte() == -95) {
/* 355 */       arrayOfDerValue2 = localDerInputStream.getSet(1, true);
/*     */       
/* 357 */       i = arrayOfDerValue2.length;
/* 358 */       this.crls = new X509CRL[i];
/*     */       
/* 360 */       for (k = 0; k < i; k++) {
/* 361 */         localObject2 = null;
/*     */         try {
/* 363 */           if (localCertificateFactory == null) {
/* 364 */             this.crls[k] = new X509CRLImpl(arrayOfDerValue2[k]);
/*     */           } else {
/* 366 */             localObject3 = arrayOfDerValue2[k].toByteArray();
/* 367 */             localObject2 = new ByteArrayInputStream((byte[])localObject3);
/* 368 */             this.crls[k] = ((X509CRL)localCertificateFactory.generateCRL((InputStream)localObject2));
/* 369 */             ((ByteArrayInputStream)localObject2).close();
/* 370 */             localObject2 = null;
/*     */           }
/*     */         }
/*     */         catch (CRLException localCRLException) {
/* 374 */           ParsingException localParsingException = new ParsingException(localCRLException.getMessage());
/* 375 */           localParsingException.initCause(localCRLException);
/* 376 */           throw localParsingException;
/*     */         } finally {
/* 378 */           if (localObject2 != null) {
/* 379 */             ((ByteArrayInputStream)localObject2).close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 385 */     DerValue[] arrayOfDerValue2 = localDerInputStream.getSet(1);
/*     */     
/* 387 */     i = arrayOfDerValue2.length;
/* 388 */     this.signerInfos = new SignerInfo[i];
/*     */     
/* 390 */     for (int k = 0; k < i; k++) {
/* 391 */       localObject2 = arrayOfDerValue2[k].toDerInputStream();
/* 392 */       this.signerInfos[k] = new SignerInfo((DerInputStream)localObject2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void parseOldSignedData(DerValue paramDerValue)
/*     */     throws ParsingException, IOException
/*     */   {
/* 403 */     DerInputStream localDerInputStream1 = paramDerValue.toDerInputStream();
/*     */     
/*     */ 
/* 406 */     this.version = localDerInputStream1.getBigInteger();
/*     */     
/*     */ 
/* 409 */     DerValue[] arrayOfDerValue1 = localDerInputStream1.getSet(1);
/* 410 */     int i = arrayOfDerValue1.length;
/*     */     
/* 412 */     this.digestAlgorithmIds = new AlgorithmId[i];
/*     */     try {
/* 414 */       for (int j = 0; j < i; j++) {
/* 415 */         DerValue localDerValue = arrayOfDerValue1[j];
/* 416 */         this.digestAlgorithmIds[j] = AlgorithmId.parse(localDerValue);
/*     */       }
/*     */     } catch (IOException localIOException1) {
/* 419 */       throw new ParsingException("Error parsing digest AlgorithmId IDs");
/*     */     }
/*     */     
/*     */ 
/* 423 */     this.contentInfo = new ContentInfo(localDerInputStream1, true);
/*     */     
/*     */ 
/* 426 */     CertificateFactory localCertificateFactory = null;
/*     */     try {
/* 428 */       localCertificateFactory = CertificateFactory.getInstance("X.509");
/*     */     }
/*     */     catch (CertificateException localCertificateException1) {}
/*     */     
/* 432 */     DerValue[] arrayOfDerValue2 = localDerInputStream1.getSet(2);
/* 433 */     i = arrayOfDerValue2.length;
/* 434 */     this.certificates = new X509Certificate[i];
/*     */     
/* 436 */     for (int k = 0; k < i; k++) {
/* 437 */       ByteArrayInputStream localByteArrayInputStream = null;
/*     */       try {
/* 439 */         if (localCertificateFactory == null) {
/* 440 */           this.certificates[k] = new X509CertImpl(arrayOfDerValue2[k]);
/*     */         } else {
/* 442 */           byte[] arrayOfByte = arrayOfDerValue2[k].toByteArray();
/* 443 */           localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
/* 444 */           this.certificates[k] = 
/* 445 */             ((X509Certificate)localCertificateFactory.generateCertificate(localByteArrayInputStream));
/* 446 */           localByteArrayInputStream.close();
/* 447 */           localByteArrayInputStream = null;
/*     */         }
/*     */       } catch (CertificateException localCertificateException2) {
/* 450 */         localParsingException = new ParsingException(localCertificateException2.getMessage());
/* 451 */         localParsingException.initCause(localCertificateException2);
/* 452 */         throw localParsingException;
/*     */       } catch (IOException localIOException2) {
/* 454 */         ParsingException localParsingException = new ParsingException(localIOException2.getMessage());
/* 455 */         localParsingException.initCause(localIOException2);
/* 456 */         throw localParsingException;
/*     */       } finally {
/* 458 */         if (localByteArrayInputStream != null) {
/* 459 */           localByteArrayInputStream.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 464 */     localDerInputStream1.getSet(0);
/*     */     
/*     */ 
/* 467 */     DerValue[] arrayOfDerValue3 = localDerInputStream1.getSet(1);
/* 468 */     i = arrayOfDerValue3.length;
/* 469 */     this.signerInfos = new SignerInfo[i];
/* 470 */     for (int m = 0; m < i; m++) {
/* 471 */       DerInputStream localDerInputStream2 = arrayOfDerValue3[m].toDerInputStream();
/* 472 */       this.signerInfos[m] = new SignerInfo(localDerInputStream2, true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encodeSignedData(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 483 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 484 */     encodeSignedData(localDerOutputStream);
/* 485 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encodeSignedData(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 497 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */     
/*     */ 
/* 500 */     localDerOutputStream.putInteger(this.version);
/*     */     
/*     */ 
/* 503 */     localDerOutputStream.putOrderedSetOf((byte)49, this.digestAlgorithmIds);
/*     */     
/*     */ 
/* 506 */     this.contentInfo.encode(localDerOutputStream);
/*     */     
/*     */ 
/* 509 */     if ((this.certificates != null) && (this.certificates.length != 0))
/*     */     {
/* 511 */       localObject1 = new X509CertImpl[this.certificates.length];
/* 512 */       for (int i = 0; i < this.certificates.length; i++) {
/* 513 */         if ((this.certificates[i] instanceof X509CertImpl)) {
/* 514 */           localObject1[i] = ((X509CertImpl)this.certificates[i]);
/*     */         } else {
/*     */           try {
/* 517 */             byte[] arrayOfByte1 = this.certificates[i].getEncoded();
/* 518 */             localObject1[i] = new X509CertImpl(arrayOfByte1);
/*     */           } catch (CertificateException localCertificateException) {
/* 520 */             throw new IOException(localCertificateException);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 527 */       localDerOutputStream.putOrderedSetOf((byte)-96, (DerEncoder[])localObject1);
/*     */     }
/*     */     
/*     */ 
/* 531 */     if ((this.crls != null) && (this.crls.length != 0))
/*     */     {
/* 533 */       localObject1 = new HashSet(this.crls.length);
/* 534 */       for (Object localObject3 : this.crls) {
/* 535 */         if ((localObject3 instanceof X509CRLImpl)) {
/* 536 */           ((Set)localObject1).add((X509CRLImpl)localObject3);
/*     */         } else {
/*     */           try {
/* 539 */             byte[] arrayOfByte2 = ((X509CRL)localObject3).getEncoded();
/* 540 */             ((Set)localObject1).add(new X509CRLImpl(arrayOfByte2));
/*     */           } catch (CRLException localCRLException) {
/* 542 */             throw new IOException(localCRLException);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 549 */       localDerOutputStream.putOrderedSetOf((byte)-95, 
/* 550 */         (DerEncoder[])((Set)localObject1).toArray(new X509CRLImpl[((Set)localObject1).size()]));
/*     */     }
/*     */     
/*     */ 
/* 554 */     localDerOutputStream.putOrderedSetOf((byte)49, this.signerInfos);
/*     */     
/*     */ 
/*     */ 
/* 558 */     Object localObject1 = new DerValue((byte)48, localDerOutputStream.toByteArray());
/*     */     
/*     */ 
/* 561 */     ??? = new ContentInfo(ContentInfo.SIGNED_DATA_OID, (DerValue)localObject1);
/*     */     
/*     */ 
/*     */ 
/* 565 */     ((ContentInfo)???).encode(paramDerOutputStream);
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
/*     */   public SignerInfo verify(SignerInfo paramSignerInfo, byte[] paramArrayOfByte)
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/* 579 */     return paramSignerInfo.verify(this, paramArrayOfByte);
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
/*     */   public SignerInfo[] verify(byte[] paramArrayOfByte)
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/* 593 */     Vector localVector = new Vector();
/* 594 */     for (int i = 0; i < this.signerInfos.length; i++)
/*     */     {
/* 596 */       SignerInfo localSignerInfo = verify(this.signerInfos[i], paramArrayOfByte);
/* 597 */       if (localSignerInfo != null) {
/* 598 */         localVector.addElement(localSignerInfo);
/*     */       }
/*     */     }
/* 601 */     if (!localVector.isEmpty())
/*     */     {
/* 603 */       SignerInfo[] arrayOfSignerInfo = new SignerInfo[localVector.size()];
/* 604 */       localVector.copyInto(arrayOfSignerInfo);
/* 605 */       return arrayOfSignerInfo;
/*     */     }
/* 607 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignerInfo[] verify()
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/* 618 */     return verify(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getVersion()
/*     */   {
/* 627 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AlgorithmId[] getDigestAlgorithmIds()
/*     */   {
/* 636 */     return this.digestAlgorithmIds;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ContentInfo getContentInfo()
/*     */   {
/* 643 */     return this.contentInfo;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate[] getCertificates()
/*     */   {
/* 652 */     if (this.certificates != null) {
/* 653 */       return (X509Certificate[])this.certificates.clone();
/*     */     }
/* 655 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CRL[] getCRLs()
/*     */   {
/* 664 */     if (this.crls != null) {
/* 665 */       return (X509CRL[])this.crls.clone();
/*     */     }
/* 667 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignerInfo[] getSignerInfos()
/*     */   {
/* 676 */     return this.signerInfos;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate getCertificate(BigInteger paramBigInteger, X500Name paramX500Name)
/*     */   {
/* 688 */     if (this.certificates != null) {
/* 689 */       if (this.certIssuerNames == null)
/* 690 */         populateCertIssuerNames();
/* 691 */       for (int i = 0; i < this.certificates.length; i++) {
/* 692 */         X509Certificate localX509Certificate = this.certificates[i];
/* 693 */         BigInteger localBigInteger = localX509Certificate.getSerialNumber();
/* 694 */         if ((paramBigInteger.equals(localBigInteger)) && 
/* 695 */           (paramX500Name.equals(this.certIssuerNames[i])))
/*     */         {
/* 697 */           return localX509Certificate;
/*     */         }
/*     */       }
/*     */     }
/* 701 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void populateCertIssuerNames()
/*     */   {
/* 709 */     if (this.certificates == null) {
/* 710 */       return;
/*     */     }
/* 712 */     this.certIssuerNames = new Principal[this.certificates.length];
/* 713 */     for (int i = 0; i < this.certificates.length; i++) {
/* 714 */       X509Certificate localX509Certificate = this.certificates[i];
/* 715 */       Principal localPrincipal = localX509Certificate.getIssuerDN();
/* 716 */       if (!(localPrincipal instanceof X500Name))
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*     */ 
/* 723 */           X509CertInfo localX509CertInfo = new X509CertInfo(localX509Certificate.getTBSCertificate());
/*     */           
/* 725 */           localPrincipal = (Principal)localX509CertInfo.get("issuer.dname");
/*     */         }
/*     */         catch (Exception localException) {}
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 732 */       this.certIssuerNames[i] = localPrincipal;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 740 */     String str = "";
/*     */     
/* 742 */     str = str + this.contentInfo + "\n";
/* 743 */     if (this.version != null)
/* 744 */       str = str + "PKCS7 :: version: " + Debug.toHexString(this.version) + "\n";
/* 745 */     int i; if (this.digestAlgorithmIds != null) {
/* 746 */       str = str + "PKCS7 :: digest AlgorithmIds: \n";
/* 747 */       for (i = 0; i < this.digestAlgorithmIds.length; i++)
/* 748 */         str = str + "\t" + this.digestAlgorithmIds[i] + "\n";
/*     */     }
/* 750 */     if (this.certificates != null) {
/* 751 */       str = str + "PKCS7 :: certificates: \n";
/* 752 */       for (i = 0; i < this.certificates.length; i++)
/* 753 */         str = str + "\t" + i + ".   " + this.certificates[i] + "\n";
/*     */     }
/* 755 */     if (this.crls != null) {
/* 756 */       str = str + "PKCS7 :: crls: \n";
/* 757 */       for (i = 0; i < this.crls.length; i++)
/* 758 */         str = str + "\t" + i + ".   " + this.crls[i] + "\n";
/*     */     }
/* 760 */     if (this.signerInfos != null) {
/* 761 */       str = str + "PKCS7 :: signer infos: \n";
/* 762 */       for (i = 0; i < this.signerInfos.length; i++)
/* 763 */         str = str + "\t" + i + ".  " + this.signerInfos[i] + "\n";
/*     */     }
/* 765 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isOldStyle()
/*     */   {
/* 773 */     return this.oldStyle;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] generateSignedData(byte[] paramArrayOfByte1, X509Certificate[] paramArrayOfX509Certificate, byte[] paramArrayOfByte2, String paramString1, URI paramURI, String paramString2, String paramString3)
/*     */     throws CertificateException, IOException, NoSuchAlgorithmException
/*     */   {
/* 811 */     PKCS9Attributes localPKCS9Attributes = null;
/* 812 */     if (paramURI != null)
/*     */     {
/* 814 */       localObject1 = new HttpTimestamper(paramURI);
/* 815 */       localObject2 = generateTimestampToken((Timestamper)localObject1, paramString2, paramString3, paramArrayOfByte1);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 820 */       localPKCS9Attributes = new PKCS9Attributes(new PKCS9Attribute[] { new PKCS9Attribute("SignatureTimestampToken", localObject2) });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 829 */     Object localObject1 = X500Name.asX500Name(paramArrayOfX509Certificate[0].getIssuerX500Principal());
/* 830 */     Object localObject2 = paramArrayOfX509Certificate[0].getSerialNumber();
/* 831 */     String str1 = AlgorithmId.getEncAlgFromSigAlg(paramString1);
/* 832 */     String str2 = AlgorithmId.getDigAlgFromSigAlg(paramString1);
/*     */     
/*     */ 
/* 835 */     SignerInfo localSignerInfo = new SignerInfo((X500Name)localObject1, (BigInteger)localObject2, AlgorithmId.get(str2), null, AlgorithmId.get(str1), paramArrayOfByte1, localPKCS9Attributes);
/*     */     
/*     */ 
/*     */ 
/* 839 */     SignerInfo[] arrayOfSignerInfo = { localSignerInfo };
/* 840 */     AlgorithmId[] arrayOfAlgorithmId = { localSignerInfo.getDigestAlgorithmId() };
/*     */     
/* 842 */     ContentInfo localContentInfo = paramArrayOfByte2 == null ? new ContentInfo(ContentInfo.DATA_OID, null) : new ContentInfo(paramArrayOfByte2);
/*     */     
/*     */ 
/* 845 */     PKCS7 localPKCS7 = new PKCS7(arrayOfAlgorithmId, localContentInfo, paramArrayOfX509Certificate, arrayOfSignerInfo);
/*     */     
/* 847 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 848 */     localPKCS7.encodeSignedData(localByteArrayOutputStream);
/*     */     
/* 850 */     return localByteArrayOutputStream.toByteArray();
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
/*     */   private static byte[] generateTimestampToken(Timestamper paramTimestamper, String paramString1, String paramString2, byte[] paramArrayOfByte)
/*     */     throws IOException, CertificateException
/*     */   {
/* 879 */     MessageDigest localMessageDigest = null;
/* 880 */     TSRequest localTSRequest = null;
/*     */     try {
/* 882 */       localMessageDigest = MessageDigest.getInstance(paramString2);
/* 883 */       localTSRequest = new TSRequest(paramString1, paramArrayOfByte, localMessageDigest);
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException1) {
/* 885 */       throw new IllegalArgumentException(localNoSuchAlgorithmException1);
/*     */     }
/*     */     
/*     */ 
/* 889 */     BigInteger localBigInteger1 = null;
/* 890 */     if (SecureRandomHolder.RANDOM != null) {
/* 891 */       localBigInteger1 = new BigInteger(64, SecureRandomHolder.RANDOM);
/* 892 */       localTSRequest.setNonce(localBigInteger1);
/*     */     }
/* 894 */     localTSRequest.requestCertificate(true);
/*     */     
/* 896 */     TSResponse localTSResponse = paramTimestamper.generateTimestamp(localTSRequest);
/* 897 */     int i = localTSResponse.getStatusCode();
/*     */     
/* 899 */     if ((i != 0) && (i != 1))
/*     */     {
/*     */ 
/* 902 */       throw new IOException("Error generating timestamp: " + localTSResponse.getStatusCodeAsText() + " " + localTSResponse.getFailureCodeAsText());
/*     */     }
/*     */     
/* 905 */     if ((paramString1 != null) && 
/* 906 */       (!paramString1.equals(localTSResponse.getTimestampToken().getPolicyID()))) {
/* 907 */       throw new IOException("TSAPolicyID changed in timestamp token");
/*     */     }
/*     */     
/* 910 */     PKCS7 localPKCS7 = localTSResponse.getToken();
/*     */     
/* 912 */     TimestampToken localTimestampToken = localTSResponse.getTimestampToken();
/*     */     try {
/* 914 */       if (!localTimestampToken.getHashAlgorithm().equals(AlgorithmId.get(paramString2))) {
/* 915 */         throw new IOException("Digest algorithm not " + paramString2 + " in timestamp token");
/*     */       }
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException2) {
/* 919 */       throw new IllegalArgumentException();
/*     */     }
/* 921 */     if (!MessageDigest.isEqual(localTimestampToken.getHashedMessage(), localTSRequest
/* 922 */       .getHashedMessage())) {
/* 923 */       throw new IOException("Digest octets changed in timestamp token");
/*     */     }
/*     */     
/* 926 */     BigInteger localBigInteger2 = localTimestampToken.getNonce();
/* 927 */     if ((localBigInteger2 == null) && (localBigInteger1 != null)) {
/* 928 */       throw new IOException("Nonce missing in timestamp token");
/*     */     }
/* 930 */     if ((localBigInteger2 != null) && (!localBigInteger2.equals(localBigInteger1))) {
/* 931 */       throw new IOException("Nonce changed in timestamp token");
/*     */     }
/*     */     
/*     */ 
/* 935 */     for (SignerInfo localSignerInfo : localPKCS7.getSignerInfos()) {
/* 936 */       X509Certificate localX509Certificate = localSignerInfo.getCertificate(localPKCS7);
/* 937 */       if (localX509Certificate == null)
/*     */       {
/* 939 */         throw new CertificateException("Certificate not included in timestamp token");
/*     */       }
/*     */       
/* 942 */       if (!localX509Certificate.getCriticalExtensionOIDs().contains("2.5.29.37"))
/*     */       {
/* 944 */         throw new CertificateException("Certificate is not valid for timestamping");
/*     */       }
/*     */       
/* 947 */       List localList = localX509Certificate.getExtendedKeyUsage();
/* 948 */       if ((localList == null) || 
/* 949 */         (!localList.contains("1.3.6.1.5.5.7.3.8"))) {
/* 950 */         throw new CertificateException("Certificate is not valid for timestamping");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 955 */     return localTSResponse.getEncodedToken();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\pkcs\PKCS7.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
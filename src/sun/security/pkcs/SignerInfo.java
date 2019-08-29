/*     */ package sun.security.pkcs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.CryptoPrimitive;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Principal;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.security.Timestamp;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Set;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.timestamp.TimestampToken;
/*     */ import sun.security.util.ConstraintsParameters;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerEncoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.DisabledAlgorithmConstraints;
/*     */ import sun.security.util.KeyUtil;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ import sun.security.x509.KeyUsageExtension;
/*     */ import sun.security.x509.X500Name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SignerInfo
/*     */   implements DerEncoder
/*     */ {
/*  75 */   private static final Set<CryptoPrimitive> DIGEST_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.MESSAGE_DIGEST));
/*     */   
/*     */ 
/*  78 */   private static final Set<CryptoPrimitive> SIG_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
/*     */   
/*  80 */   private static final DisabledAlgorithmConstraints JAR_DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
/*     */   
/*     */   BigInteger version;
/*     */   
/*     */   X500Name issuerName;
/*     */   
/*     */   BigInteger certificateSerialNumber;
/*     */   AlgorithmId digestAlgorithmId;
/*     */   AlgorithmId digestEncryptionAlgorithmId;
/*     */   byte[] encryptedDigest;
/*     */   Timestamp timestamp;
/*  91 */   private boolean hasTimestamp = true;
/*  92 */   private static final Debug debug = Debug.getInstance("jar");
/*     */   
/*     */ 
/*     */   PKCS9Attributes authenticatedAttributes;
/*     */   
/*     */   PKCS9Attributes unauthenticatedAttributes;
/*     */   
/*     */ 
/*     */   public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte)
/*     */   {
/* 102 */     this.version = BigInteger.ONE;
/* 103 */     this.issuerName = paramX500Name;
/* 104 */     this.certificateSerialNumber = paramBigInteger;
/* 105 */     this.digestAlgorithmId = paramAlgorithmId1;
/* 106 */     this.digestEncryptionAlgorithmId = paramAlgorithmId2;
/* 107 */     this.encryptedDigest = paramArrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, PKCS9Attributes paramPKCS9Attributes1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte, PKCS9Attributes paramPKCS9Attributes2)
/*     */   {
/* 117 */     this.version = BigInteger.ONE;
/* 118 */     this.issuerName = paramX500Name;
/* 119 */     this.certificateSerialNumber = paramBigInteger;
/* 120 */     this.digestAlgorithmId = paramAlgorithmId1;
/* 121 */     this.authenticatedAttributes = paramPKCS9Attributes1;
/* 122 */     this.digestEncryptionAlgorithmId = paramAlgorithmId2;
/* 123 */     this.encryptedDigest = paramArrayOfByte;
/* 124 */     this.unauthenticatedAttributes = paramPKCS9Attributes2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignerInfo(DerInputStream paramDerInputStream)
/*     */     throws IOException, ParsingException
/*     */   {
/* 133 */     this(paramDerInputStream, false);
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
/*     */   public SignerInfo(DerInputStream paramDerInputStream, boolean paramBoolean)
/*     */     throws IOException, ParsingException
/*     */   {
/* 150 */     this.version = paramDerInputStream.getBigInteger();
/*     */     
/*     */ 
/* 153 */     DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(2);
/* 154 */     byte[] arrayOfByte = arrayOfDerValue[0].toByteArray();
/* 155 */     this.issuerName = new X500Name(new DerValue((byte)48, arrayOfByte));
/*     */     
/* 157 */     this.certificateSerialNumber = arrayOfDerValue[1].getBigInteger();
/*     */     
/*     */ 
/* 160 */     DerValue localDerValue = paramDerInputStream.getDerValue();
/*     */     
/* 162 */     this.digestAlgorithmId = AlgorithmId.parse(localDerValue);
/*     */     
/*     */ 
/* 165 */     if (paramBoolean)
/*     */     {
/*     */ 
/* 168 */       paramDerInputStream.getSet(0);
/*     */ 
/*     */ 
/*     */     }
/* 172 */     else if ((byte)paramDerInputStream.peekByte() == -96) {
/* 173 */       this.authenticatedAttributes = new PKCS9Attributes(paramDerInputStream);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 179 */     localDerValue = paramDerInputStream.getDerValue();
/*     */     
/* 181 */     this.digestEncryptionAlgorithmId = AlgorithmId.parse(localDerValue);
/*     */     
/*     */ 
/* 184 */     this.encryptedDigest = paramDerInputStream.getOctetString();
/*     */     
/*     */ 
/* 187 */     if (paramBoolean)
/*     */     {
/*     */ 
/* 190 */       paramDerInputStream.getSet(0);
/*     */ 
/*     */ 
/*     */     }
/* 194 */     else if ((paramDerInputStream.available() != 0) && 
/* 195 */       ((byte)paramDerInputStream.peekByte() == -95)) {
/* 196 */       this.unauthenticatedAttributes = new PKCS9Attributes(paramDerInputStream, true);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 202 */     if (paramDerInputStream.available() != 0) {
/* 203 */       throw new ParsingException("extra data at the end");
/*     */     }
/*     */   }
/*     */   
/*     */   public void encode(DerOutputStream paramDerOutputStream) throws IOException
/*     */   {
/* 209 */     derEncode(paramDerOutputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void derEncode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 222 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 223 */     localDerOutputStream1.putInteger(this.version);
/* 224 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 225 */     this.issuerName.encode(localDerOutputStream2);
/* 226 */     localDerOutputStream2.putInteger(this.certificateSerialNumber);
/* 227 */     localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*     */     
/* 229 */     this.digestAlgorithmId.encode(localDerOutputStream1);
/*     */     
/*     */ 
/* 232 */     if (this.authenticatedAttributes != null) {
/* 233 */       this.authenticatedAttributes.encode((byte)-96, localDerOutputStream1);
/*     */     }
/* 235 */     this.digestEncryptionAlgorithmId.encode(localDerOutputStream1);
/*     */     
/* 237 */     localDerOutputStream1.putOctetString(this.encryptedDigest);
/*     */     
/*     */ 
/* 240 */     if (this.unauthenticatedAttributes != null) {
/* 241 */       this.unauthenticatedAttributes.encode((byte)-95, localDerOutputStream1);
/*     */     }
/* 243 */     DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 244 */     localDerOutputStream3.write((byte)48, localDerOutputStream1);
/*     */     
/* 246 */     paramOutputStream.write(localDerOutputStream3.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate getCertificate(PKCS7 paramPKCS7)
/*     */     throws IOException
/*     */   {
/* 257 */     return paramPKCS7.getCertificate(this.certificateSerialNumber, this.issuerName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ArrayList<X509Certificate> getCertificateChain(PKCS7 paramPKCS7)
/*     */     throws IOException
/*     */   {
/* 267 */     X509Certificate localX509Certificate1 = paramPKCS7.getCertificate(this.certificateSerialNumber, this.issuerName);
/* 268 */     if (localX509Certificate1 == null) {
/* 269 */       return null;
/*     */     }
/* 271 */     ArrayList localArrayList = new ArrayList();
/* 272 */     localArrayList.add(localX509Certificate1);
/*     */     
/* 274 */     X509Certificate[] arrayOfX509Certificate = paramPKCS7.getCertificates();
/* 275 */     if ((arrayOfX509Certificate == null) || 
/* 276 */       (localX509Certificate1.getSubjectDN().equals(localX509Certificate1.getIssuerDN()))) {
/* 277 */       return localArrayList;
/*     */     }
/*     */     
/* 280 */     Principal localPrincipal = localX509Certificate1.getIssuerDN();
/* 281 */     int i = 0;
/*     */     for (;;) {
/* 283 */       int j = 0;
/* 284 */       int k = i;
/* 285 */       while (k < arrayOfX509Certificate.length) {
/* 286 */         if (localPrincipal.equals(arrayOfX509Certificate[k].getSubjectDN()))
/*     */         {
/* 288 */           localArrayList.add(arrayOfX509Certificate[k]);
/*     */           
/*     */ 
/* 291 */           if (arrayOfX509Certificate[k].getSubjectDN().equals(arrayOfX509Certificate[k]
/* 292 */             .getIssuerDN())) {
/* 293 */             i = arrayOfX509Certificate.length;
/*     */           } else {
/* 295 */             localPrincipal = arrayOfX509Certificate[k].getIssuerDN();
/* 296 */             X509Certificate localX509Certificate2 = arrayOfX509Certificate[i];
/* 297 */             arrayOfX509Certificate[i] = arrayOfX509Certificate[k];
/* 298 */             arrayOfX509Certificate[k] = localX509Certificate2;
/* 299 */             i++;
/*     */           }
/* 301 */           j = 1;
/* 302 */           break;
/*     */         }
/* 304 */         k++;
/*     */       }
/*     */       
/* 307 */       if (j == 0) {
/*     */         break;
/*     */       }
/*     */     }
/* 311 */     return localArrayList;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   SignerInfo verify(PKCS7 paramPKCS7, byte[] paramArrayOfByte)
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/*     */     try
/*     */     {
/* 321 */       ContentInfo localContentInfo = paramPKCS7.getContentInfo();
/* 322 */       if (paramArrayOfByte == null) {
/* 323 */         paramArrayOfByte = localContentInfo.getContentBytes();
/*     */       }
/*     */       
/* 326 */       Timestamp localTimestamp = null;
/*     */       try {
/* 328 */         localTimestamp = getTimestamp();
/*     */       }
/*     */       catch (Exception localException) {}
/*     */       
/* 332 */       ConstraintsParameters localConstraintsParameters = new ConstraintsParameters(localTimestamp);
/*     */       
/* 334 */       String str = getDigestAlgorithmId().getName();
/*     */       
/*     */ 
/*     */       byte[] arrayOfByte1;
/*     */       
/*     */ 
/* 340 */       if (this.authenticatedAttributes == null) {
/* 341 */         arrayOfByte1 = paramArrayOfByte;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 346 */         localObject1 = (ObjectIdentifier)this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.CONTENT_TYPE_OID);
/*     */         
/* 348 */         if ((localObject1 == null) || 
/* 349 */           (!((ObjectIdentifier)localObject1).equals(localContentInfo.contentType))) {
/* 350 */           return null;
/*     */         }
/*     */         
/*     */ 
/* 354 */         localObject2 = (byte[])this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.MESSAGE_DIGEST_OID);
/*     */         
/*     */ 
/* 357 */         if (localObject2 == null) {
/* 358 */           return null;
/*     */         }
/*     */         try
/*     */         {
/* 362 */           JAR_DISABLED_CHECK.permits(str, localConstraintsParameters);
/*     */         } catch (CertPathValidatorException localCertPathValidatorException1) {
/* 364 */           throw new SignatureException(localCertPathValidatorException1.getMessage(), localCertPathValidatorException1);
/*     */         }
/*     */         
/* 367 */         localObject3 = MessageDigest.getInstance(str);
/* 368 */         byte[] arrayOfByte2 = ((MessageDigest)localObject3).digest(paramArrayOfByte);
/*     */         
/* 370 */         if (localObject2.length != arrayOfByte2.length)
/* 371 */           return null;
/* 372 */         for (int i = 0; i < localObject2.length; i++) {
/* 373 */           if (localObject2[i] != arrayOfByte2[i]) {
/* 374 */             return null;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 383 */         arrayOfByte1 = this.authenticatedAttributes.getDerEncoding();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 389 */       Object localObject1 = getDigestEncryptionAlgorithmId().getName();
/*     */       
/*     */ 
/*     */ 
/* 393 */       Object localObject2 = AlgorithmId.getEncAlgFromSigAlg((String)localObject1);
/* 394 */       if (localObject2 != null) localObject1 = localObject2;
/* 395 */       Object localObject3 = AlgorithmId.makeSigAlg(str, (String)localObject1);
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 400 */         JAR_DISABLED_CHECK.permits((String)localObject3, localConstraintsParameters);
/*     */       } catch (CertPathValidatorException localCertPathValidatorException2) {
/* 402 */         throw new SignatureException(localCertPathValidatorException2.getMessage(), localCertPathValidatorException2);
/*     */       }
/*     */       
/* 405 */       X509Certificate localX509Certificate = getCertificate(paramPKCS7);
/* 406 */       if (localX509Certificate == null) {
/* 407 */         return null;
/*     */       }
/* 409 */       PublicKey localPublicKey = localX509Certificate.getPublicKey();
/*     */       
/*     */ 
/* 412 */       if (!JAR_DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, localPublicKey))
/*     */       {
/*     */ 
/*     */ 
/* 416 */         throw new SignatureException("Public key check failed. Disabled key used: " + KeyUtil.getKeySize(localPublicKey) + " bit " + localPublicKey.getAlgorithm());
/*     */       }
/*     */       
/* 419 */       if (localX509Certificate.hasUnsupportedCriticalExtension()) {
/* 420 */         throw new SignatureException("Certificate has unsupported critical extension(s)");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 428 */       boolean[] arrayOfBoolean = localX509Certificate.getKeyUsage();
/* 429 */       if (arrayOfBoolean != null)
/*     */       {
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 436 */           localObject4 = new KeyUsageExtension(arrayOfBoolean);
/*     */         } catch (IOException localIOException2) {
/* 438 */           throw new SignatureException("Failed to parse keyUsage extension");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 443 */         boolean bool1 = ((KeyUsageExtension)localObject4).get("digital_signature").booleanValue();
/*     */         
/*     */ 
/* 446 */         boolean bool2 = ((KeyUsageExtension)localObject4).get("non_repudiation").booleanValue();
/*     */         
/* 448 */         if ((!bool1) && (!bool2)) {
/* 449 */           throw new SignatureException("Key usage restricted: cannot be used for digital signatures");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 455 */       Object localObject4 = Signature.getInstance((String)localObject3);
/* 456 */       ((Signature)localObject4).initVerify(localPublicKey);
/* 457 */       ((Signature)localObject4).update(arrayOfByte1);
/* 458 */       if (((Signature)localObject4).verify(this.encryptedDigest)) {
/* 459 */         return this;
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException1)
/*     */     {
/* 464 */       throw new SignatureException("IO error verifying signature:\n" + localIOException1.getMessage());
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/* 467 */       throw new SignatureException("InvalidKey: " + localInvalidKeyException.getMessage());
/*     */     }
/*     */     
/* 470 */     return null;
/*     */   }
/*     */   
/*     */   SignerInfo verify(PKCS7 paramPKCS7)
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/* 476 */     return verify(paramPKCS7, null);
/*     */   }
/*     */   
/*     */   public BigInteger getVersion()
/*     */   {
/* 481 */     return this.version;
/*     */   }
/*     */   
/*     */   public X500Name getIssuerName() {
/* 485 */     return this.issuerName;
/*     */   }
/*     */   
/*     */   public BigInteger getCertificateSerialNumber() {
/* 489 */     return this.certificateSerialNumber;
/*     */   }
/*     */   
/*     */   public AlgorithmId getDigestAlgorithmId() {
/* 493 */     return this.digestAlgorithmId;
/*     */   }
/*     */   
/*     */   public PKCS9Attributes getAuthenticatedAttributes() {
/* 497 */     return this.authenticatedAttributes;
/*     */   }
/*     */   
/*     */   public AlgorithmId getDigestEncryptionAlgorithmId() {
/* 501 */     return this.digestEncryptionAlgorithmId;
/*     */   }
/*     */   
/*     */   public byte[] getEncryptedDigest() {
/* 505 */     return this.encryptedDigest;
/*     */   }
/*     */   
/*     */   public PKCS9Attributes getUnauthenticatedAttributes() {
/* 509 */     return this.unauthenticatedAttributes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PKCS7 getTsToken()
/*     */     throws IOException
/*     */   {
/* 517 */     if (this.unauthenticatedAttributes == null) {
/* 518 */       return null;
/*     */     }
/*     */     
/* 521 */     PKCS9Attribute localPKCS9Attribute = this.unauthenticatedAttributes.getAttribute(PKCS9Attribute.SIGNATURE_TIMESTAMP_TOKEN_OID);
/*     */     
/* 523 */     if (localPKCS9Attribute == null) {
/* 524 */       return null;
/*     */     }
/* 526 */     return new PKCS7((byte[])localPKCS9Attribute.getValue());
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
/*     */   public Timestamp getTimestamp()
/*     */     throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException
/*     */   {
/* 553 */     if ((this.timestamp != null) || (!this.hasTimestamp)) {
/* 554 */       return this.timestamp;
/*     */     }
/* 556 */     PKCS7 localPKCS7 = getTsToken();
/* 557 */     if (localPKCS7 == null) {
/* 558 */       this.hasTimestamp = false;
/* 559 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 563 */     byte[] arrayOfByte = localPKCS7.getContentInfo().getData();
/*     */     
/*     */ 
/* 566 */     SignerInfo[] arrayOfSignerInfo = localPKCS7.verify(arrayOfByte);
/*     */     
/* 568 */     ArrayList localArrayList = arrayOfSignerInfo[0].getCertificateChain(localPKCS7);
/* 569 */     CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
/* 570 */     CertPath localCertPath = localCertificateFactory.generateCertPath(localArrayList);
/*     */     
/* 572 */     TimestampToken localTimestampToken = new TimestampToken(arrayOfByte);
/*     */     
/* 574 */     verifyTimestamp(localTimestampToken);
/*     */     
/* 576 */     this.timestamp = new Timestamp(localTimestampToken.getDate(), localCertPath);
/* 577 */     return this.timestamp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void verifyTimestamp(TimestampToken paramTimestampToken)
/*     */     throws NoSuchAlgorithmException, SignatureException
/*     */   {
/* 587 */     String str = paramTimestampToken.getHashAlgorithm().getName();
/*     */     
/* 589 */     if (!JAR_DISABLED_CHECK.permits(DIGEST_PRIMITIVE_SET, str, null))
/*     */     {
/* 591 */       throw new SignatureException("Timestamp token digest check failed. Disabled algorithm used: " + str);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 596 */     MessageDigest localMessageDigest = MessageDigest.getInstance(str);
/*     */     
/* 598 */     if (!Arrays.equals(paramTimestampToken.getHashedMessage(), localMessageDigest
/* 599 */       .digest(this.encryptedDigest)))
/*     */     {
/*     */ 
/* 602 */       throw new SignatureException("Signature timestamp (#" + paramTimestampToken.getSerialNumber() + ") generated on " + paramTimestampToken.getDate() + " is inapplicable");
/*     */     }
/*     */     
/*     */ 
/* 606 */     if (debug != null) {
/* 607 */       debug.println();
/* 608 */       debug.println("Detected signature timestamp (#" + paramTimestampToken
/* 609 */         .getSerialNumber() + ") generated on " + paramTimestampToken.getDate());
/* 610 */       debug.println();
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 615 */     HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*     */     
/* 617 */     String str = "";
/*     */     
/* 619 */     str = str + "Signer Info for (issuer): " + this.issuerName + "\n";
/* 620 */     str = str + "\tversion: " + Debug.toHexString(this.version) + "\n";
/*     */     
/* 622 */     str = str + "\tcertificateSerialNumber: " + Debug.toHexString(this.certificateSerialNumber) + "\n";
/* 623 */     str = str + "\tdigestAlgorithmId: " + this.digestAlgorithmId + "\n";
/* 624 */     if (this.authenticatedAttributes != null) {
/* 625 */       str = str + "\tauthenticatedAttributes: " + this.authenticatedAttributes + "\n";
/*     */     }
/*     */     
/* 628 */     str = str + "\tdigestEncryptionAlgorithmId: " + this.digestEncryptionAlgorithmId + "\n";
/*     */     
/*     */ 
/*     */ 
/* 632 */     str = str + "\tencryptedDigest: \n" + localHexDumpEncoder.encodeBuffer(this.encryptedDigest) + "\n";
/* 633 */     if (this.unauthenticatedAttributes != null) {
/* 634 */       str = str + "\tunauthenticatedAttributes: " + this.unauthenticatedAttributes + "\n";
/*     */     }
/*     */     
/* 637 */     return str;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\pkcs\SignerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
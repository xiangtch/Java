/*     */ package sun.security.validator;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.AlgorithmConstraints;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.provider.certpath.AlgorithmChecker;
/*     */ import sun.security.provider.certpath.UntrustedChecker;
/*     */ import sun.security.util.BitArray;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.NetscapeCertTypeExtension;
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
/*     */ 
/*     */ 
/*     */ public final class SimpleValidator
/*     */   extends Validator
/*     */ {
/*     */   static final String OID_BASIC_CONSTRAINTS = "2.5.29.19";
/*     */   static final String OID_NETSCAPE_CERT_TYPE = "2.16.840.1.113730.1.1";
/*     */   static final String OID_KEY_USAGE = "2.5.29.15";
/*     */   static final String OID_EXTENDED_KEY_USAGE = "2.5.29.37";
/*     */   static final String OID_EKU_ANY_USAGE = "2.5.29.37.0";
/*  73 */   static final ObjectIdentifier OBJID_NETSCAPE_CERT_TYPE = NetscapeCertTypeExtension.NetscapeCertType_Id;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String NSCT_SSL_CA = "ssl_ca";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final String NSCT_CODE_SIGNING_CA = "object_signing_ca";
/*     */   
/*     */ 
/*     */ 
/*     */   private final Map<X500Principal, List<X509Certificate>> trustedX500Principals;
/*     */   
/*     */ 
/*     */ 
/*     */   private final Collection<X509Certificate> trustedCerts;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   SimpleValidator(String paramString, Collection<X509Certificate> paramCollection)
/*     */   {
/*  98 */     super("Simple", paramString);
/*  99 */     this.trustedCerts = paramCollection;
/* 100 */     this.trustedX500Principals = new HashMap();
/*     */     
/* 102 */     for (X509Certificate localX509Certificate : paramCollection) {
/* 103 */       X500Principal localX500Principal = localX509Certificate.getSubjectX500Principal();
/* 104 */       Object localObject = (List)this.trustedX500Principals.get(localX500Principal);
/* 105 */       if (localObject == null)
/*     */       {
/*     */ 
/* 108 */         localObject = new ArrayList(2);
/* 109 */         this.trustedX500Principals.put(localX500Principal, localObject);
/*     */       }
/* 111 */       ((List)localObject).add(localX509Certificate);
/*     */     }
/*     */   }
/*     */   
/*     */   public Collection<X509Certificate> getTrustedCertificates() {
/* 116 */     return this.trustedCerts;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   X509Certificate[] engineValidate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, AlgorithmConstraints paramAlgorithmConstraints, Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 128 */     if ((paramArrayOfX509Certificate == null) || (paramArrayOfX509Certificate.length == 0)) {
/* 129 */       throw new CertificateException("null or zero-length certificate chain");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 134 */     paramArrayOfX509Certificate = buildTrustedChain(paramArrayOfX509Certificate);
/*     */     
/*     */ 
/* 137 */     Date localDate = this.validationDate;
/* 138 */     if (localDate == null) {
/* 139 */       localDate = new Date();
/*     */     }
/*     */     
/*     */ 
/* 143 */     UntrustedChecker localUntrustedChecker = new UntrustedChecker();
/*     */     
/*     */ 
/* 146 */     X509Certificate localX509Certificate1 = paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)];
/*     */     try {
/* 148 */       localUntrustedChecker.check(localX509Certificate1);
/*     */     }
/*     */     catch (CertPathValidatorException localCertPathValidatorException1) {
/* 151 */       throw new ValidatorException("Untrusted certificate: " + localX509Certificate1.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, localX509Certificate1, localCertPathValidatorException1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 156 */     TrustAnchor localTrustAnchor = new TrustAnchor(localX509Certificate1, null);
/* 157 */     AlgorithmChecker localAlgorithmChecker1 = new AlgorithmChecker(localTrustAnchor, this.variant);
/*     */     
/*     */ 
/*     */ 
/* 161 */     AlgorithmChecker localAlgorithmChecker2 = null;
/* 162 */     if (paramAlgorithmConstraints != null) {
/* 163 */       localAlgorithmChecker2 = new AlgorithmChecker(localTrustAnchor, paramAlgorithmConstraints, null, null, this.variant);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 169 */     int i = paramArrayOfX509Certificate.length - 1;
/* 170 */     for (int j = paramArrayOfX509Certificate.length - 2; j >= 0; j--) {
/* 171 */       X509Certificate localX509Certificate2 = paramArrayOfX509Certificate[(j + 1)];
/* 172 */       X509Certificate localX509Certificate3 = paramArrayOfX509Certificate[j];
/*     */       
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 178 */         localUntrustedChecker.check(localX509Certificate3, Collections.emptySet());
/*     */       }
/*     */       catch (CertPathValidatorException localCertPathValidatorException2) {
/* 181 */         throw new ValidatorException("Untrusted certificate: " + localX509Certificate3.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, localX509Certificate3, localCertPathValidatorException2);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 189 */         localAlgorithmChecker1.check(localX509Certificate3, Collections.emptySet());
/* 190 */         if (localAlgorithmChecker2 != null) {
/* 191 */           localAlgorithmChecker2.check(localX509Certificate3, Collections.emptySet());
/*     */         }
/*     */       } catch (CertPathValidatorException localCertPathValidatorException3) {
/* 194 */         throw new ValidatorException(ValidatorException.T_ALGORITHM_DISABLED, localX509Certificate3, localCertPathValidatorException3);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 199 */       if ((!this.variant.equals("code signing")) && 
/* 200 */         (!this.variant.equals("jce signing"))) {
/* 201 */         localX509Certificate3.checkValidity(localDate);
/*     */       }
/*     */       
/*     */ 
/* 205 */       if (!localX509Certificate3.getIssuerX500Principal().equals(localX509Certificate2
/* 206 */         .getSubjectX500Principal())) {
/* 207 */         throw new ValidatorException(ValidatorException.T_NAME_CHAINING, localX509Certificate3);
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 213 */         localX509Certificate3.verify(localX509Certificate2.getPublicKey());
/*     */       } catch (GeneralSecurityException localGeneralSecurityException) {
/* 215 */         throw new ValidatorException(ValidatorException.T_SIGNATURE_ERROR, localX509Certificate3, localGeneralSecurityException);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 220 */       if (j != 0) {
/* 221 */         i = checkExtensions(localX509Certificate3, i);
/*     */       }
/*     */     }
/*     */     
/* 225 */     return paramArrayOfX509Certificate;
/*     */   }
/*     */   
/*     */   private int checkExtensions(X509Certificate paramX509Certificate, int paramInt) throws CertificateException
/*     */   {
/* 230 */     Set localSet = paramX509Certificate.getCriticalExtensionOIDs();
/* 231 */     if (localSet == null) {
/* 232 */       localSet = Collections.emptySet();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 237 */     int i = checkBasicConstraints(paramX509Certificate, localSet, paramInt);
/*     */     
/*     */ 
/* 240 */     checkKeyUsage(paramX509Certificate, localSet);
/*     */     
/*     */ 
/* 243 */     checkNetscapeCertType(paramX509Certificate, localSet);
/*     */     
/* 245 */     if (!localSet.isEmpty()) {
/* 246 */       throw new ValidatorException("Certificate contains unknown critical extensions: " + localSet, ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 251 */     return i;
/*     */   }
/*     */   
/*     */   private void checkNetscapeCertType(X509Certificate paramX509Certificate, Set<String> paramSet) throws CertificateException
/*     */   {
/* 256 */     if (!this.variant.equals("generic"))
/*     */     {
/* 258 */       if ((this.variant.equals("tls client")) || 
/* 259 */         (this.variant.equals("tls server"))) {
/* 260 */         if (!getNetscapeCertTypeBit(paramX509Certificate, "ssl_ca")) {
/* 261 */           throw new ValidatorException("Invalid Netscape CertType extension for SSL CA certificate", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 266 */         paramSet.remove("2.16.840.1.113730.1.1");
/* 267 */       } else if ((this.variant.equals("code signing")) || 
/* 268 */         (this.variant.equals("jce signing"))) {
/* 269 */         if (!getNetscapeCertTypeBit(paramX509Certificate, "object_signing_ca")) {
/* 270 */           throw new ValidatorException("Invalid Netscape CertType extension for code signing CA certificate", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 275 */         paramSet.remove("2.16.840.1.113730.1.1");
/*     */       } else {
/* 277 */         throw new CertificateException("Unknown variant " + this.variant);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static boolean getNetscapeCertTypeBit(X509Certificate paramX509Certificate, String paramString)
/*     */   {
/*     */     try
/*     */     {
/*     */       Object localObject2;
/*     */       NetscapeCertTypeExtension localNetscapeCertTypeExtension;
/* 288 */       if ((paramX509Certificate instanceof X509CertImpl)) {
/* 289 */         localObject1 = (X509CertImpl)paramX509Certificate;
/* 290 */         localObject2 = OBJID_NETSCAPE_CERT_TYPE;
/* 291 */         localNetscapeCertTypeExtension = (NetscapeCertTypeExtension)((X509CertImpl)localObject1).getExtension((ObjectIdentifier)localObject2);
/* 292 */         if (localNetscapeCertTypeExtension == null) {
/* 293 */           return true;
/*     */         }
/*     */       } else {
/* 296 */         localObject1 = paramX509Certificate.getExtensionValue("2.16.840.1.113730.1.1");
/* 297 */         if (localObject1 == null) {
/* 298 */           return true;
/*     */         }
/* 300 */         localObject2 = new DerInputStream((byte[])localObject1);
/* 301 */         byte[] arrayOfByte = ((DerInputStream)localObject2).getOctetString();
/*     */         
/* 303 */         arrayOfByte = new DerValue(arrayOfByte).getUnalignedBitString().toByteArray();
/* 304 */         localNetscapeCertTypeExtension = new NetscapeCertTypeExtension(arrayOfByte);
/*     */       }
/* 306 */       Object localObject1 = localNetscapeCertTypeExtension.get(paramString);
/* 307 */       return ((Boolean)localObject1).booleanValue();
/*     */     } catch (IOException localIOException) {}
/* 309 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private int checkBasicConstraints(X509Certificate paramX509Certificate, Set<String> paramSet, int paramInt)
/*     */     throws CertificateException
/*     */   {
/* 316 */     paramSet.remove("2.5.29.19");
/* 317 */     int i = paramX509Certificate.getBasicConstraints();
/*     */     
/* 319 */     if (i < 0) {
/* 320 */       throw new ValidatorException("End user tried to act as a CA", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 326 */     if (!X509CertImpl.isSelfIssued(paramX509Certificate)) {
/* 327 */       if (paramInt <= 0) {
/* 328 */         throw new ValidatorException("Violated path length constraints", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
/*     */       }
/*     */       
/*     */ 
/* 332 */       paramInt--;
/*     */     }
/*     */     
/* 335 */     if (paramInt > i) {
/* 336 */       paramInt = i;
/*     */     }
/*     */     
/* 339 */     return paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkKeyUsage(X509Certificate paramX509Certificate, Set<String> paramSet)
/*     */     throws CertificateException
/*     */   {
/* 349 */     paramSet.remove("2.5.29.15");
/*     */     
/* 351 */     paramSet.remove("2.5.29.37");
/*     */     
/*     */ 
/* 354 */     boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
/* 355 */     if (arrayOfBoolean != null)
/*     */     {
/* 357 */       if ((arrayOfBoolean.length < 6) || (arrayOfBoolean[5] == 0)) {
/* 358 */         throw new ValidatorException("Wrong key usage: expected keyCertSign", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate);
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
/*     */   private X509Certificate[] buildTrustedChain(X509Certificate[] paramArrayOfX509Certificate)
/*     */     throws CertificateException
/*     */   {
/* 372 */     ArrayList localArrayList = new ArrayList(paramArrayOfX509Certificate.length);
/*     */     
/*     */ 
/* 375 */     for (int i = 0; i < paramArrayOfX509Certificate.length; i++) {
/* 376 */       localObject1 = paramArrayOfX509Certificate[i];
/* 377 */       localObject2 = getTrustedCertificate((X509Certificate)localObject1);
/* 378 */       if (localObject2 != null) {
/* 379 */         localArrayList.add(localObject2);
/* 380 */         return (X509Certificate[])localArrayList.toArray(CHAIN0);
/*     */       }
/* 382 */       localArrayList.add(localObject1);
/*     */     }
/*     */     
/*     */ 
/* 386 */     X509Certificate localX509Certificate1 = paramArrayOfX509Certificate[(paramArrayOfX509Certificate.length - 1)];
/* 387 */     Object localObject1 = localX509Certificate1.getSubjectX500Principal();
/* 388 */     Object localObject2 = localX509Certificate1.getIssuerX500Principal();
/* 389 */     List localList = (List)this.trustedX500Principals.get(localObject2);
/* 390 */     if (localList != null) {
/* 391 */       X509Certificate localX509Certificate2 = (X509Certificate)localList.iterator().next();
/* 392 */       localArrayList.add(localX509Certificate2);
/* 393 */       return (X509Certificate[])localArrayList.toArray(CHAIN0);
/*     */     }
/*     */     
/*     */ 
/* 397 */     throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private X509Certificate getTrustedCertificate(X509Certificate paramX509Certificate)
/*     */   {
/* 407 */     X500Principal localX500Principal1 = paramX509Certificate.getSubjectX500Principal();
/* 408 */     List localList = (List)this.trustedX500Principals.get(localX500Principal1);
/* 409 */     if (localList == null) {
/* 410 */       return null;
/*     */     }
/*     */     
/* 413 */     X500Principal localX500Principal2 = paramX509Certificate.getIssuerX500Principal();
/* 414 */     PublicKey localPublicKey = paramX509Certificate.getPublicKey();
/*     */     
/* 416 */     for (X509Certificate localX509Certificate : localList) {
/* 417 */       if (localX509Certificate.equals(paramX509Certificate)) {
/* 418 */         return paramX509Certificate;
/*     */       }
/* 420 */       if ((localX509Certificate.getIssuerX500Principal().equals(localX500Principal2)) && 
/*     */       
/*     */ 
/* 423 */         (localX509Certificate.getPublicKey().equals(localPublicKey)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 428 */         return localX509Certificate; }
/*     */     }
/* 430 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\validator\SimpleValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
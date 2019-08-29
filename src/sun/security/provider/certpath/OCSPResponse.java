/*      */ package sun.security.provider.certpath;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.security.AccessController;
/*      */ import java.security.GeneralSecurityException;
/*      */ import java.security.InvalidKeyException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.PublicKey;
/*      */ import java.security.Signature;
/*      */ import java.security.SignatureException;
/*      */ import java.security.cert.CRLReason;
/*      */ import java.security.cert.CertPathValidatorException;
/*      */ import java.security.cert.CertPathValidatorException.BasicReason;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.CertificateParsingException;
/*      */ import java.security.cert.TrustAnchor;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.misc.HexDumpEncoder;
/*      */ import sun.security.action.GetIntegerAction;
/*      */ import sun.security.util.Debug;
/*      */ import sun.security.util.DerInputStream;
/*      */ import sun.security.util.DerValue;
/*      */ import sun.security.util.ObjectIdentifier;
/*      */ import sun.security.x509.AlgorithmId;
/*      */ import sun.security.x509.KeyIdentifier;
/*      */ import sun.security.x509.PKIXExtensions;
/*      */ import sun.security.x509.X509CertImpl;
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
/*      */ public final class OCSPResponse
/*      */ {
/*      */   public static enum ResponseStatus
/*      */   {
/*  125 */     SUCCESSFUL, 
/*  126 */     MALFORMED_REQUEST, 
/*  127 */     INTERNAL_ERROR, 
/*  128 */     TRY_LATER, 
/*  129 */     UNUSED, 
/*  130 */     SIG_REQUIRED, 
/*  131 */     UNAUTHORIZED;
/*      */     private ResponseStatus() {} }
/*  133 */   private static final ResponseStatus[] rsvalues = ;
/*      */   
/*  135 */   private static final Debug debug = Debug.getInstance("certpath");
/*  136 */   private static final boolean dump = (debug != null) && (Debug.isOn("ocsp"));
/*      */   
/*  138 */   private static final ObjectIdentifier OCSP_BASIC_RESPONSE_OID = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1, 1 });
/*      */   
/*      */ 
/*      */   private static final int CERT_STATUS_GOOD = 0;
/*      */   
/*      */ 
/*      */   private static final int CERT_STATUS_REVOKED = 1;
/*      */   
/*      */ 
/*      */   private static final int CERT_STATUS_UNKNOWN = 2;
/*      */   
/*      */ 
/*      */   private static final int NAME_TAG = 1;
/*      */   
/*      */   private static final int KEY_TAG = 2;
/*      */   
/*      */   private static final String KP_OCSP_SIGNING_OID = "1.3.6.1.5.5.7.3.9";
/*      */   
/*      */   private static final int DEFAULT_MAX_CLOCK_SKEW = 900000;
/*      */   
/*  158 */   private static final int MAX_CLOCK_SKEW = initializeClockSkew();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int initializeClockSkew()
/*      */   {
/*  166 */     Integer localInteger = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.ocsp.clockSkew"));
/*      */     
/*  168 */     if ((localInteger == null) || (localInteger.intValue() < 0)) {
/*  169 */       return 900000;
/*      */     }
/*      */     
/*      */ 
/*  173 */     return localInteger.intValue() * 1000;
/*      */   }
/*      */   
/*      */ 
/*  177 */   private static final CRLReason[] values = CRLReason.values();
/*      */   
/*      */   private final ResponseStatus responseStatus;
/*      */   private final Map<CertId, SingleResponse> singleResponseMap;
/*      */   private final AlgorithmId sigAlgId;
/*      */   private final byte[] signature;
/*      */   private final byte[] tbsResponseData;
/*      */   private final byte[] responseNonce;
/*      */   private List<X509CertImpl> certs;
/*  186 */   private X509CertImpl signerCert = null;
/*      */   private final ResponderId respId;
/*  188 */   private Date producedAtDate = null;
/*      */   
/*      */ 
/*      */   private final Map<String, java.security.cert.Extension> responseExtensions;
/*      */   
/*      */ 
/*      */   public OCSPResponse(byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/*  197 */     if (dump) {
/*  198 */       localObject1 = new HexDumpEncoder();
/*  199 */       debug.println("OCSPResponse bytes...\n\n" + ((HexDumpEncoder)localObject1)
/*  200 */         .encode(paramArrayOfByte) + "\n");
/*      */     }
/*  202 */     Object localObject1 = new DerValue(paramArrayOfByte);
/*  203 */     if (((DerValue)localObject1).tag != 48) {
/*  204 */       throw new IOException("Bad encoding in OCSP response: expected ASN.1 SEQUENCE tag.");
/*      */     }
/*      */     
/*  207 */     DerInputStream localDerInputStream1 = ((DerValue)localObject1).getData();
/*      */     
/*      */ 
/*  210 */     int i = localDerInputStream1.getEnumerated();
/*  211 */     if ((i >= 0) && (i < rsvalues.length)) {
/*  212 */       this.responseStatus = rsvalues[i];
/*      */     }
/*      */     else {
/*  215 */       throw new IOException("Unknown OCSPResponse status: " + i);
/*      */     }
/*  217 */     if (debug != null) {
/*  218 */       debug.println("OCSP response status: " + this.responseStatus);
/*      */     }
/*  220 */     if (this.responseStatus != ResponseStatus.SUCCESSFUL)
/*      */     {
/*  222 */       this.singleResponseMap = Collections.emptyMap();
/*  223 */       this.certs = new ArrayList();
/*  224 */       this.sigAlgId = null;
/*  225 */       this.signature = null;
/*  226 */       this.tbsResponseData = null;
/*  227 */       this.responseNonce = null;
/*  228 */       this.responseExtensions = Collections.emptyMap();
/*  229 */       this.respId = null;
/*  230 */       return;
/*      */     }
/*      */     
/*      */ 
/*  234 */     localObject1 = localDerInputStream1.getDerValue();
/*  235 */     if (!((DerValue)localObject1).isContextSpecific((byte)0)) {
/*  236 */       throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 context specific tag 0.");
/*      */     }
/*      */     
/*  239 */     DerValue localDerValue1 = ((DerValue)localObject1).data.getDerValue();
/*  240 */     if (localDerValue1.tag != 48) {
/*  241 */       throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 SEQUENCE tag.");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  246 */     localDerInputStream1 = localDerValue1.data;
/*  247 */     ObjectIdentifier localObjectIdentifier = localDerInputStream1.getOID();
/*  248 */     if (localObjectIdentifier.equals(OCSP_BASIC_RESPONSE_OID)) {
/*  249 */       if (debug != null) {
/*  250 */         debug.println("OCSP response type: basic");
/*      */       }
/*      */     } else {
/*  253 */       if (debug != null) {
/*  254 */         debug.println("OCSP response type: " + localObjectIdentifier);
/*      */       }
/*  256 */       throw new IOException("Unsupported OCSP response type: " + localObjectIdentifier);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  262 */     DerInputStream localDerInputStream2 = new DerInputStream(localDerInputStream1.getOctetString());
/*      */     
/*  264 */     DerValue[] arrayOfDerValue1 = localDerInputStream2.getSequence(2);
/*  265 */     if (arrayOfDerValue1.length < 3) {
/*  266 */       throw new IOException("Unexpected BasicOCSPResponse value");
/*      */     }
/*      */     
/*  269 */     DerValue localDerValue2 = arrayOfDerValue1[0];
/*      */     
/*      */ 
/*  272 */     this.tbsResponseData = arrayOfDerValue1[0].toByteArray();
/*      */     
/*      */ 
/*  275 */     if (localDerValue2.tag != 48) {
/*  276 */       throw new IOException("Bad encoding in tbsResponseData element of OCSP response: expected ASN.1 SEQUENCE tag.");
/*      */     }
/*      */     
/*  279 */     DerInputStream localDerInputStream3 = localDerValue2.data;
/*  280 */     DerValue localDerValue3 = localDerInputStream3.getDerValue();
/*      */     
/*      */ 
/*  283 */     if (localDerValue3.isContextSpecific((byte)0))
/*      */     {
/*  285 */       if ((localDerValue3.isConstructed()) && (localDerValue3.isContextSpecific()))
/*      */       {
/*  287 */         localDerValue3 = localDerValue3.data.getDerValue();
/*  288 */         int j = localDerValue3.getInteger();
/*  289 */         if (localDerValue3.data.available() != 0) {
/*  290 */           throw new IOException("Bad encoding in version  element of OCSP response: bad format");
/*      */         }
/*      */         
/*  293 */         localDerValue3 = localDerInputStream3.getDerValue();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  298 */     this.respId = new ResponderId(localDerValue3.toByteArray());
/*  299 */     if (debug != null) {
/*  300 */       debug.println("Responder ID: " + this.respId);
/*      */     }
/*      */     
/*      */ 
/*  304 */     localDerValue3 = localDerInputStream3.getDerValue();
/*  305 */     this.producedAtDate = localDerValue3.getGeneralizedTime();
/*  306 */     if (debug != null) {
/*  307 */       debug.println("OCSP response produced at: " + this.producedAtDate);
/*      */     }
/*      */     
/*      */ 
/*  311 */     DerValue[] arrayOfDerValue2 = localDerInputStream3.getSequence(1);
/*  312 */     this.singleResponseMap = new HashMap(arrayOfDerValue2.length);
/*  313 */     if (debug != null) {
/*  314 */       debug.println("OCSP number of SingleResponses: " + arrayOfDerValue2.length);
/*      */     }
/*      */     Object localObject3;
/*  317 */     for (localObject3 : arrayOfDerValue2) {
/*  318 */       SingleResponse localSingleResponse = new SingleResponse((DerValue)localObject3, null);
/*  319 */       this.singleResponseMap.put(localSingleResponse.getCertId(), localSingleResponse);
/*      */     }
/*      */     
/*      */ 
/*  323 */     ??? = new HashMap();
/*  324 */     if (localDerInputStream3.available() > 0) {
/*  325 */       localDerValue3 = localDerInputStream3.getDerValue();
/*  326 */       if (localDerValue3.isContextSpecific((byte)1)) {
/*  327 */         ??? = parseExtensions(localDerValue3);
/*      */       }
/*      */     }
/*  330 */     this.responseExtensions = ((Map)???);
/*      */     
/*      */ 
/*  333 */     sun.security.x509.Extension localExtension = (sun.security.x509.Extension)((Map)???).get(PKIXExtensions.OCSPNonce_Id
/*  334 */       .toString());
/*      */     
/*  336 */     this.responseNonce = (localExtension != null ? localExtension.getExtensionValue() : null);
/*  337 */     if ((debug != null) && (this.responseNonce != null)) {
/*  338 */       debug.println("Response nonce: " + Arrays.toString(this.responseNonce));
/*      */     }
/*      */     
/*      */ 
/*  342 */     this.sigAlgId = AlgorithmId.parse(arrayOfDerValue1[1]);
/*      */     
/*      */ 
/*  345 */     this.signature = arrayOfDerValue1[2].getBitString();
/*      */     
/*      */ 
/*  348 */     if (arrayOfDerValue1.length > 3)
/*      */     {
/*  350 */       DerValue localDerValue4 = arrayOfDerValue1[3];
/*  351 */       if (!localDerValue4.isContextSpecific((byte)0)) {
/*  352 */         throw new IOException("Bad encoding in certs element of OCSP response: expected ASN.1 context specific tag 0.");
/*      */       }
/*      */       
/*  355 */       localObject3 = localDerValue4.getData().getSequence(3);
/*  356 */       this.certs = new ArrayList(localObject3.length);
/*      */       try {
/*  358 */         for (int n = 0; n < localObject3.length; n++)
/*      */         {
/*  360 */           X509CertImpl localX509CertImpl = new X509CertImpl(localObject3[n].toByteArray());
/*  361 */           this.certs.add(localX509CertImpl);
/*      */           
/*  363 */           if (debug != null) {
/*  364 */             debug.println("OCSP response cert #" + (n + 1) + ": " + localX509CertImpl
/*  365 */               .getSubjectX500Principal());
/*      */           }
/*      */         }
/*      */       } catch (CertificateException localCertificateException) {
/*  369 */         throw new IOException("Bad encoding in X509 Certificate", localCertificateException);
/*      */       }
/*      */     } else {
/*  372 */       this.certs = new ArrayList();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void verify(List<CertId> paramList, IssuerInfo paramIssuerInfo, X509Certificate paramX509Certificate, Date paramDate, byte[] paramArrayOfByte, String paramString)
/*      */     throws CertPathValidatorException
/*      */   {
/*  381 */     switch (this.responseStatus) {
/*      */     case SUCCESSFUL: 
/*      */       break;
/*      */     case TRY_LATER: 
/*      */     case INTERNAL_ERROR: 
/*  386 */       throw new CertPathValidatorException("OCSP response error: " + this.responseStatus, null, null, -1, BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*      */     
/*      */ 
/*      */     case UNAUTHORIZED: 
/*      */     default: 
/*  391 */       throw new CertPathValidatorException("OCSP response error: " + this.responseStatus);
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*  397 */     for (Iterator localIterator1 = paramList.iterator(); localIterator1.hasNext();) { localObject2 = (CertId)localIterator1.next();
/*  398 */       localObject3 = getSingleResponse((CertId)localObject2);
/*  399 */       if (localObject3 == null) {
/*  400 */         if (debug != null) {
/*  401 */           debug.println("No response found for CertId: " + localObject2);
/*      */         }
/*  403 */         throw new CertPathValidatorException("OCSP response does not include a response for a certificate supplied in the OCSP request");
/*      */       }
/*      */       
/*      */ 
/*  407 */       if (debug != null)
/*  408 */         debug.println("Status of certificate (with serial number " + ((CertId)localObject2)
/*  409 */           .getSerialNumber() + ") is: " + ((SingleResponse)localObject3).getCertStatus());
/*      */     }
/*      */     Object localObject2;
/*      */     Object localObject3;
/*      */     Object localObject1;
/*  414 */     if (this.signerCert == null)
/*      */     {
/*      */       try
/*      */       {
/*  418 */         if (paramIssuerInfo.getCertificate() != null) {
/*  419 */           this.certs.add(X509CertImpl.toImpl(paramIssuerInfo.getCertificate()));
/*      */         }
/*  421 */         if (paramX509Certificate != null) {
/*  422 */           this.certs.add(X509CertImpl.toImpl(paramX509Certificate));
/*      */         }
/*      */       } catch (CertificateException localCertificateException1) {
/*  425 */         throw new CertPathValidatorException("Invalid issuer or trusted responder certificate", localCertificateException1);
/*      */       }
/*      */       
/*      */ 
/*  429 */       if (this.respId.getType() == ResponderId.Type.BY_NAME) {
/*  430 */         localObject1 = this.respId.getResponderName();
/*  431 */         for (localObject2 = this.certs.iterator(); ((Iterator)localObject2).hasNext();) { localObject3 = (X509CertImpl)((Iterator)localObject2).next();
/*  432 */           if (((X509CertImpl)localObject3).getSubjectX500Principal().equals(localObject1)) {
/*  433 */             this.signerCert = ((X509CertImpl)localObject3);
/*  434 */             break;
/*      */           }
/*      */         }
/*  437 */       } else if (this.respId.getType() == ResponderId.Type.BY_KEY) {
/*  438 */         localObject1 = this.respId.getKeyIdentifier();
/*  439 */         for (localObject2 = this.certs.iterator(); ((Iterator)localObject2).hasNext();) { localObject3 = (X509CertImpl)((Iterator)localObject2).next();
/*      */           
/*      */ 
/*      */ 
/*  443 */           localObject4 = ((X509CertImpl)localObject3).getSubjectKeyId();
/*  444 */           if ((localObject4 != null) && (((KeyIdentifier)localObject1).equals(localObject4))) {
/*  445 */             this.signerCert = ((X509CertImpl)localObject3);
/*  446 */             break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*  454 */             localObject4 = new KeyIdentifier(((X509CertImpl)localObject3).getPublicKey());
/*      */           }
/*      */           catch (IOException localIOException) {}
/*      */           
/*  458 */           if (((KeyIdentifier)localObject1).equals(localObject4)) {
/*  459 */             this.signerCert = ((X509CertImpl)localObject3);
/*  460 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  468 */     if (this.signerCert != null)
/*      */     {
/*  470 */       if (this.signerCert.getSubjectX500Principal().equals(paramIssuerInfo
/*  471 */         .getName())) {
/*  472 */         if (this.signerCert.getPublicKey().equals(paramIssuerInfo
/*  473 */           .getPublicKey())) {
/*  474 */           if (debug == null) break label832;
/*  475 */           debug.println("OCSP response is signed by the target's Issuing CA");
/*      */           
/*      */           break label832;
/*      */         }
/*      */       }
/*      */       
/*  481 */       if (this.signerCert.equals(paramX509Certificate)) {
/*  482 */         if (debug != null) {
/*  483 */           debug.println("OCSP response is signed by a Trusted Responder");
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*  489 */       else if (this.signerCert.getIssuerX500Principal().equals(paramIssuerInfo
/*  490 */         .getName()))
/*      */       {
/*      */         try
/*      */         {
/*  494 */           localObject1 = this.signerCert.getExtendedKeyUsage();
/*  495 */           if ((localObject1 == null) || 
/*  496 */             (!((List)localObject1).contains("1.3.6.1.5.5.7.3.9"))) {
/*  497 */             throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses");
/*      */           }
/*      */           
/*      */         }
/*      */         catch (CertificateParsingException localCertificateParsingException)
/*      */         {
/*  503 */           throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses", localCertificateParsingException);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  511 */         AlgorithmChecker localAlgorithmChecker = new AlgorithmChecker(paramIssuerInfo.getAnchor(), paramDate, paramString);
/*      */         
/*  513 */         localAlgorithmChecker.init(false);
/*  514 */         localAlgorithmChecker.check(this.signerCert, Collections.emptySet());
/*      */         
/*      */         try
/*      */         {
/*  518 */           if (paramDate == null) {
/*  519 */             this.signerCert.checkValidity();
/*      */           } else {
/*  521 */             this.signerCert.checkValidity(paramDate);
/*      */           }
/*      */         } catch (CertificateException localCertificateException2) {
/*  524 */           throw new CertPathValidatorException("Responder's certificate not within the validity period", localCertificateException2);
/*      */         }
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
/*  537 */         sun.security.x509.Extension localExtension = this.signerCert.getExtension(PKIXExtensions.OCSPNoCheck_Id);
/*  538 */         if ((localExtension != null) && 
/*  539 */           (debug != null)) {
/*  540 */           debug.println("Responder's certificate includes the extension id-pkix-ocsp-nocheck.");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  550 */           this.signerCert.verify(paramIssuerInfo.getPublicKey());
/*  551 */           if (debug != null) {
/*  552 */             debug.println("OCSP response is signed by an Authorized Responder");
/*      */           }
/*      */           
/*      */         }
/*      */         catch (GeneralSecurityException localGeneralSecurityException)
/*      */         {
/*  558 */           this.signerCert = null;
/*      */         }
/*      */       } else {
/*  561 */         throw new CertPathValidatorException("Responder's certificate is not authorized to sign OCSP responses");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     label832:
/*      */     
/*      */ 
/*  569 */     if (this.signerCert != null)
/*      */     {
/*      */ 
/*  572 */       AlgorithmChecker.check(this.signerCert.getPublicKey(), this.sigAlgId, paramString);
/*      */       
/*  574 */       if (!verifySignature(this.signerCert)) {
/*  575 */         throw new CertPathValidatorException("Error verifying OCSP Response's signature");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  580 */       throw new CertPathValidatorException("Unable to verify OCSP Response's signature");
/*      */     }
/*      */     
/*      */ 
/*  584 */     if ((paramArrayOfByte != null) && 
/*  585 */       (this.responseNonce != null) && (!Arrays.equals(paramArrayOfByte, this.responseNonce))) {
/*  586 */       throw new CertPathValidatorException("Nonces don't match");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  591 */     long l = paramDate == null ? System.currentTimeMillis() : paramDate.getTime();
/*  592 */     Date localDate = new Date(l + MAX_CLOCK_SKEW);
/*  593 */     Object localObject4 = new Date(l - MAX_CLOCK_SKEW);
/*  594 */     for (SingleResponse localSingleResponse : this.singleResponseMap.values()) {
/*  595 */       if (debug != null) {
/*  596 */         String str = "";
/*  597 */         if (localSingleResponse.nextUpdate != null) {
/*  598 */           str = " until " + localSingleResponse.nextUpdate;
/*      */         }
/*  600 */         debug.println("OCSP response validity interval is from " + 
/*  601 */           localSingleResponse.thisUpdate + str);
/*  602 */         debug.println("Checking validity of OCSP response on: " + new Date(l));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  609 */       if (!localDate.before(localSingleResponse.thisUpdate)) {
/*  610 */         if (!((Date)localObject4).after(
/*  611 */           localSingleResponse.nextUpdate != null ? localSingleResponse.nextUpdate : localSingleResponse.thisUpdate)) {}
/*      */       } else {
/*  613 */         throw new CertPathValidatorException("Response is unreliable: its validity interval is out-of-date");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResponseStatus getResponseStatus()
/*      */   {
/*  626 */     return this.responseStatus;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean verifySignature(X509Certificate paramX509Certificate)
/*      */     throws CertPathValidatorException
/*      */   {
/*      */     try
/*      */     {
/*  636 */       Signature localSignature = Signature.getInstance(this.sigAlgId.getName());
/*  637 */       localSignature.initVerify(paramX509Certificate.getPublicKey());
/*  638 */       localSignature.update(this.tbsResponseData);
/*      */       
/*  640 */       if (localSignature.verify(this.signature)) {
/*  641 */         if (debug != null) {
/*  642 */           debug.println("Verified signature of OCSP Response");
/*      */         }
/*  644 */         return true;
/*      */       }
/*      */       
/*  647 */       if (debug != null) {
/*  648 */         debug.println("Error verifying signature of OCSP Response");
/*      */       }
/*      */       
/*  651 */       return false;
/*      */ 
/*      */     }
/*      */     catch (InvalidKeyException|NoSuchAlgorithmException|SignatureException localInvalidKeyException)
/*      */     {
/*  656 */       throw new CertPathValidatorException(localInvalidKeyException);
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
/*      */   public SingleResponse getSingleResponse(CertId paramCertId)
/*      */   {
/*  671 */     return (SingleResponse)this.singleResponseMap.get(paramCertId);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<CertId> getCertIds()
/*      */   {
/*  681 */     return Collections.unmodifiableSet(this.singleResponseMap.keySet());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   X509Certificate getSignerCertificate()
/*      */   {
/*  688 */     return this.signerCert;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResponderId getResponderId()
/*      */   {
/*  699 */     return this.respId;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  709 */     StringBuilder localStringBuilder = new StringBuilder();
/*  710 */     localStringBuilder.append("OCSP Response:\n");
/*  711 */     localStringBuilder.append("Response Status: ").append(this.responseStatus).append("\n");
/*  712 */     localStringBuilder.append("Responder ID: ").append(this.respId).append("\n");
/*  713 */     localStringBuilder.append("Produced at: ").append(this.producedAtDate).append("\n");
/*  714 */     int i = this.singleResponseMap.size();
/*  715 */     localStringBuilder.append(i).append(i == 1 ? " response:\n" : " responses:\n");
/*      */     
/*  717 */     for (Iterator localIterator = this.singleResponseMap.values().iterator(); localIterator.hasNext();) { localObject = (SingleResponse)localIterator.next();
/*  718 */       localStringBuilder.append(localObject).append("\n"); }
/*      */     Object localObject;
/*  720 */     if ((this.responseExtensions != null) && (this.responseExtensions.size() > 0)) {
/*  721 */       i = this.responseExtensions.size();
/*  722 */       localStringBuilder.append(i).append(i == 1 ? " extension:\n" : " extensions:\n");
/*      */       
/*  724 */       for (localIterator = this.responseExtensions.keySet().iterator(); localIterator.hasNext();) { localObject = (String)localIterator.next();
/*  725 */         localStringBuilder.append(this.responseExtensions.get(localObject)).append("\n");
/*      */       }
/*      */     }
/*      */     
/*  729 */     return localStringBuilder.toString();
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
/*      */   private static Map<String, java.security.cert.Extension> parseExtensions(DerValue paramDerValue)
/*      */     throws IOException
/*      */   {
/*  745 */     DerValue[] arrayOfDerValue1 = paramDerValue.data.getSequence(3);
/*  746 */     HashMap localHashMap = new HashMap(arrayOfDerValue1.length);
/*      */     
/*      */ 
/*  749 */     for (DerValue localDerValue : arrayOfDerValue1) {
/*  750 */       sun.security.x509.Extension localExtension = new sun.security.x509.Extension(localDerValue);
/*  751 */       if (debug != null) {
/*  752 */         debug.println("Extension: " + localExtension);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  757 */       if (localExtension.isCritical())
/*      */       {
/*  759 */         throw new IOException("Unsupported OCSP critical extension: " + localExtension.getExtensionId());
/*      */       }
/*  761 */       localHashMap.put(localExtension.getId(), localExtension);
/*      */     }
/*      */     
/*  764 */     return localHashMap;
/*      */   }
/*      */   
/*      */   public static final class SingleResponse
/*      */     implements OCSP.RevocationStatus
/*      */   {
/*      */     private final CertId certId;
/*      */     private final CertStatus certStatus;
/*      */     private final Date thisUpdate;
/*      */     private final Date nextUpdate;
/*      */     private final Date revocationTime;
/*      */     private final CRLReason revocationReason;
/*      */     private final Map<String, java.security.cert.Extension> singleExtensions;
/*      */     
/*      */     private SingleResponse(DerValue paramDerValue) throws IOException
/*      */     {
/*  780 */       if (paramDerValue.tag != 48) {
/*  781 */         throw new IOException("Bad ASN.1 encoding in SingleResponse");
/*      */       }
/*  783 */       DerInputStream localDerInputStream = paramDerValue.data;
/*      */       
/*  785 */       this.certId = new CertId(localDerInputStream.getDerValue().data);
/*  786 */       DerValue localDerValue = localDerInputStream.getDerValue();
/*  787 */       int i = (short)(byte)(localDerValue.tag & 0x1F);
/*  788 */       if (i == 1) {
/*  789 */         this.certStatus = CertStatus.REVOKED;
/*  790 */         this.revocationTime = localDerValue.data.getGeneralizedTime();
/*  791 */         if (localDerValue.data.available() != 0) {
/*  792 */           localObject = localDerValue.data.getDerValue();
/*  793 */           i = (short)(byte)(((DerValue)localObject).tag & 0x1F);
/*  794 */           if (i == 0) {
/*  795 */             int j = ((DerValue)localObject).data.getEnumerated();
/*      */             
/*  797 */             if ((j >= 0) && (j < OCSPResponse.values.length)) {
/*  798 */               this.revocationReason = OCSPResponse.values[j];
/*      */             } else {
/*  800 */               this.revocationReason = CRLReason.UNSPECIFIED;
/*      */             }
/*      */           } else {
/*  803 */             this.revocationReason = CRLReason.UNSPECIFIED;
/*      */           }
/*      */         } else {
/*  806 */           this.revocationReason = CRLReason.UNSPECIFIED;
/*      */         }
/*      */         
/*  809 */         if (OCSPResponse.debug != null) {
/*  810 */           OCSPResponse.debug.println("Revocation time: " + this.revocationTime);
/*  811 */           OCSPResponse.debug.println("Revocation reason: " + this.revocationReason);
/*      */         }
/*      */       } else {
/*  814 */         this.revocationTime = null;
/*  815 */         this.revocationReason = null;
/*  816 */         if (i == 0) {
/*  817 */           this.certStatus = CertStatus.GOOD;
/*  818 */         } else if (i == 2) {
/*  819 */           this.certStatus = CertStatus.UNKNOWN;
/*      */         } else {
/*  821 */           throw new IOException("Invalid certificate status");
/*      */         }
/*      */       }
/*      */       
/*  825 */       this.thisUpdate = localDerInputStream.getGeneralizedTime();
/*  826 */       if (OCSPResponse.debug != null) {
/*  827 */         OCSPResponse.debug.println("thisUpdate: " + this.thisUpdate);
/*      */       }
/*      */       
/*      */ 
/*  831 */       Object localObject = null;
/*  832 */       Map localMap = null;
/*      */       
/*      */ 
/*      */ 
/*  836 */       if (localDerInputStream.available() > 0) {
/*  837 */         localDerValue = localDerInputStream.getDerValue();
/*      */         
/*      */ 
/*  840 */         if (localDerValue.isContextSpecific((byte)0)) {
/*  841 */           localObject = localDerValue.data.getGeneralizedTime();
/*  842 */           if (OCSPResponse.debug != null) {
/*  843 */             OCSPResponse.debug.println("nextUpdate: " + localObject);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  849 */           localDerValue = localDerInputStream.available() > 0 ? localDerInputStream.getDerValue() : null;
/*      */         }
/*      */         
/*      */ 
/*  853 */         if (localDerValue != null) {
/*  854 */           if (localDerValue.isContextSpecific((byte)1)) {
/*  855 */             localMap = OCSPResponse.parseExtensions(localDerValue);
/*      */             
/*      */ 
/*      */ 
/*  859 */             if (localDerInputStream.available() > 0) {
/*  860 */               throw new IOException(localDerInputStream.available() + " bytes of additional data in singleResponse");
/*      */             }
/*      */             
/*      */           }
/*      */           else
/*      */           {
/*  866 */             throw new IOException("Unsupported singleResponse item, tag = " + String.format("%02X", new Object[] {Byte.valueOf(localDerValue.tag) }));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  871 */       this.nextUpdate = ((Date)localObject);
/*      */       
/*  873 */       this.singleExtensions = (localMap != null ? localMap : Collections.emptyMap());
/*  874 */       if (OCSPResponse.debug != null)
/*      */       {
/*  876 */         for (java.security.cert.Extension localExtension : this.singleExtensions.values()) {
/*  877 */           OCSPResponse.debug.println("singleExtension: " + localExtension);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public CertStatus getCertStatus()
/*      */     {
/*  887 */       return this.certStatus;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public CertId getCertId()
/*      */     {
/*  896 */       return this.certId;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Date getThisUpdate()
/*      */     {
/*  905 */       return this.thisUpdate != null ? (Date)this.thisUpdate.clone() : null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Date getNextUpdate()
/*      */     {
/*  915 */       return this.nextUpdate != null ? (Date)this.nextUpdate.clone() : null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Date getRevocationTime()
/*      */     {
/*  928 */       return this.revocationTime != null ? (Date)this.revocationTime.clone() : null;
/*      */     }
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
/*      */     public CRLReason getRevocationReason()
/*      */     {
/*  942 */       return this.revocationReason;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Map<String, java.security.cert.Extension> getSingleExtensions()
/*      */     {
/*  953 */       return Collections.unmodifiableMap(this.singleExtensions);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String toString()
/*      */     {
/*  960 */       StringBuilder localStringBuilder = new StringBuilder();
/*  961 */       localStringBuilder.append("SingleResponse:\n");
/*  962 */       localStringBuilder.append(this.certId);
/*  963 */       localStringBuilder.append("\nCertStatus: ").append(this.certStatus).append("\n");
/*  964 */       if (this.certStatus == CertStatus.REVOKED) {
/*  965 */         localStringBuilder.append("revocationTime is ");
/*  966 */         localStringBuilder.append(this.revocationTime).append("\n");
/*  967 */         localStringBuilder.append("revocationReason is ");
/*  968 */         localStringBuilder.append(this.revocationReason).append("\n");
/*      */       }
/*  970 */       localStringBuilder.append("thisUpdate is ").append(this.thisUpdate).append("\n");
/*  971 */       if (this.nextUpdate != null) {
/*  972 */         localStringBuilder.append("nextUpdate is ").append(this.nextUpdate).append("\n");
/*      */       }
/*  974 */       for (java.security.cert.Extension localExtension : this.singleExtensions.values()) {
/*  975 */         localStringBuilder.append("singleExtension: ");
/*  976 */         localStringBuilder.append(localExtension.toString()).append("\n");
/*      */       }
/*  978 */       return localStringBuilder.toString();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static final class IssuerInfo
/*      */   {
/*      */     private final TrustAnchor anchor;
/*      */     
/*      */     private final X509Certificate certificate;
/*      */     
/*      */     private final X500Principal name;
/*      */     
/*      */     private final PublicKey pubKey;
/*      */     
/*      */ 
/*      */     IssuerInfo(TrustAnchor paramTrustAnchor)
/*      */     {
/*  996 */       this(paramTrustAnchor, paramTrustAnchor != null ? paramTrustAnchor.getTrustedCert() : null);
/*      */     }
/*      */     
/*      */     IssuerInfo(X509Certificate paramX509Certificate) {
/* 1000 */       this(null, paramX509Certificate);
/*      */     }
/*      */     
/*      */     IssuerInfo(TrustAnchor paramTrustAnchor, X509Certificate paramX509Certificate) {
/* 1004 */       if ((paramTrustAnchor == null) && (paramX509Certificate == null)) {
/* 1005 */         throw new NullPointerException("TrustAnchor and issuerCert cannot be null");
/*      */       }
/*      */       
/* 1008 */       this.anchor = paramTrustAnchor;
/* 1009 */       if (paramX509Certificate != null) {
/* 1010 */         this.name = paramX509Certificate.getSubjectX500Principal();
/* 1011 */         this.pubKey = paramX509Certificate.getPublicKey();
/* 1012 */         this.certificate = paramX509Certificate;
/*      */       } else {
/* 1014 */         this.name = paramTrustAnchor.getCA();
/* 1015 */         this.pubKey = paramTrustAnchor.getCAPublicKey();
/* 1016 */         this.certificate = paramTrustAnchor.getTrustedCert();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     X509Certificate getCertificate()
/*      */     {
/* 1028 */       return this.certificate;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     X500Principal getName()
/*      */     {
/* 1039 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     PublicKey getPublicKey()
/*      */     {
/* 1048 */       return this.pubKey;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     TrustAnchor getAnchor()
/*      */     {
/* 1057 */       return this.anchor;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1067 */       StringBuilder localStringBuilder = new StringBuilder();
/* 1068 */       localStringBuilder.append("Issuer Info:\n");
/* 1069 */       localStringBuilder.append("Name: ").append(this.name.toString()).append("\n");
/* 1070 */       localStringBuilder.append("Public Key:\n").append(this.pubKey.toString()).append("\n");
/* 1071 */       return localStringBuilder.toString();
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\OCSPResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
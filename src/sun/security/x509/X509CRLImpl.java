/*      */ package sun.security.x509;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.math.BigInteger;
/*      */ import java.security.InvalidKeyException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.NoSuchProviderException;
/*      */ import java.security.Principal;
/*      */ import java.security.PrivateKey;
/*      */ import java.security.Provider;
/*      */ import java.security.PublicKey;
/*      */ import java.security.Signature;
/*      */ import java.security.SignatureException;
/*      */ import java.security.cert.CRLException;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.X509CRL;
/*      */ import java.security.cert.X509CRLEntry;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import java.util.TreeSet;
/*      */ import javax.security.auth.x500.X500Principal;
/*      */ import sun.misc.HexDumpEncoder;
/*      */ import sun.security.provider.X509Factory;
/*      */ import sun.security.util.DerEncoder;
/*      */ import sun.security.util.DerInputStream;
/*      */ import sun.security.util.DerOutputStream;
/*      */ import sun.security.util.DerValue;
/*      */ import sun.security.util.ObjectIdentifier;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class X509CRLImpl
/*      */   extends X509CRL
/*      */   implements DerEncoder
/*      */ {
/*   95 */   private byte[] signedCRL = null;
/*   96 */   private byte[] signature = null;
/*   97 */   private byte[] tbsCertList = null;
/*   98 */   private AlgorithmId sigAlgId = null;
/*      */   
/*      */   private int version;
/*      */   
/*      */   private AlgorithmId infoSigAlgId;
/*  103 */   private X500Name issuer = null;
/*  104 */   private X500Principal issuerPrincipal = null;
/*  105 */   private Date thisUpdate = null;
/*  106 */   private Date nextUpdate = null;
/*  107 */   private Map<X509IssuerSerial, X509CRLEntry> revokedMap = new TreeMap();
/*  108 */   private List<X509CRLEntry> revokedList = new LinkedList();
/*  109 */   private CRLExtensions extensions = null;
/*      */   
/*      */   private static final boolean isExplicit = true;
/*      */   private static final long YR_2050 = 2524636800000L;
/*  113 */   private boolean readOnly = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private PublicKey verifiedPublicKey;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String verifiedProvider;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private X509CRLImpl() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509CRLImpl(byte[] paramArrayOfByte)
/*      */     throws CRLException
/*      */   {
/*      */     try
/*      */     {
/*  146 */       parse(new DerValue(paramArrayOfByte));
/*      */     } catch (IOException localIOException) {
/*  148 */       this.signedCRL = null;
/*  149 */       throw new CRLException("Parsing error: " + localIOException.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509CRLImpl(DerValue paramDerValue)
/*      */     throws CRLException
/*      */   {
/*      */     try
/*      */     {
/*  161 */       parse(paramDerValue);
/*      */     } catch (IOException localIOException) {
/*  163 */       this.signedCRL = null;
/*  164 */       throw new CRLException("Parsing error: " + localIOException.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509CRLImpl(InputStream paramInputStream)
/*      */     throws CRLException
/*      */   {
/*      */     try
/*      */     {
/*  177 */       parse(new DerValue(paramInputStream));
/*      */     } catch (IOException localIOException) {
/*  179 */       this.signedCRL = null;
/*  180 */       throw new CRLException("Parsing error: " + localIOException.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509CRLImpl(X500Name paramX500Name, Date paramDate1, Date paramDate2)
/*      */   {
/*  192 */     this.issuer = paramX500Name;
/*  193 */     this.thisUpdate = paramDate1;
/*  194 */     this.nextUpdate = paramDate2;
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
/*      */   public X509CRLImpl(X500Name paramX500Name, Date paramDate1, Date paramDate2, X509CRLEntry[] paramArrayOfX509CRLEntry)
/*      */     throws CRLException
/*      */   {
/*  211 */     this.issuer = paramX500Name;
/*  212 */     this.thisUpdate = paramDate1;
/*  213 */     this.nextUpdate = paramDate2;
/*  214 */     if (paramArrayOfX509CRLEntry != null) {
/*  215 */       X500Principal localX500Principal1 = getIssuerX500Principal();
/*  216 */       X500Principal localX500Principal2 = localX500Principal1;
/*  217 */       for (int i = 0; i < paramArrayOfX509CRLEntry.length; i++) {
/*  218 */         X509CRLEntryImpl localX509CRLEntryImpl = (X509CRLEntryImpl)paramArrayOfX509CRLEntry[i];
/*      */         try {
/*  220 */           localX500Principal2 = getCertIssuer(localX509CRLEntryImpl, localX500Principal2);
/*      */         } catch (IOException localIOException) {
/*  222 */           throw new CRLException(localIOException);
/*      */         }
/*  224 */         localX509CRLEntryImpl.setCertificateIssuer(localX500Principal1, localX500Principal2);
/*      */         
/*  226 */         X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(localX500Principal2, localX509CRLEntryImpl.getSerialNumber());
/*  227 */         this.revokedMap.put(localX509IssuerSerial, localX509CRLEntryImpl);
/*  228 */         this.revokedList.add(localX509CRLEntryImpl);
/*  229 */         if (localX509CRLEntryImpl.hasExtensions()) {
/*  230 */           this.version = 1;
/*      */         }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509CRLImpl(X500Name paramX500Name, Date paramDate1, Date paramDate2, X509CRLEntry[] paramArrayOfX509CRLEntry, CRLExtensions paramCRLExtensions)
/*      */     throws CRLException
/*      */   {
/*  251 */     this(paramX500Name, paramDate1, paramDate2, paramArrayOfX509CRLEntry);
/*  252 */     if (paramCRLExtensions != null) {
/*  253 */       this.extensions = paramCRLExtensions;
/*  254 */       this.version = 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getEncodedInternal()
/*      */     throws CRLException
/*      */   {
/*  264 */     if (this.signedCRL == null) {
/*  265 */       throw new CRLException("Null CRL to encode");
/*      */     }
/*  267 */     return this.signedCRL;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getEncoded()
/*      */     throws CRLException
/*      */   {
/*  276 */     return (byte[])getEncodedInternal().clone();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void encodeInfo(OutputStream paramOutputStream)
/*      */     throws CRLException
/*      */   {
/*      */     try
/*      */     {
/*  287 */       DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  288 */       DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*  289 */       DerOutputStream localDerOutputStream3 = new DerOutputStream();
/*      */       
/*  291 */       if (this.version != 0)
/*  292 */         localDerOutputStream1.putInteger(this.version);
/*  293 */       this.infoSigAlgId.encode(localDerOutputStream1);
/*  294 */       if ((this.version == 0) && (this.issuer.toString() == null))
/*  295 */         throw new CRLException("Null Issuer DN not allowed in v1 CRL");
/*  296 */       this.issuer.encode(localDerOutputStream1);
/*      */       
/*  298 */       if (this.thisUpdate.getTime() < 2524636800000L) {
/*  299 */         localDerOutputStream1.putUTCTime(this.thisUpdate);
/*      */       } else {
/*  301 */         localDerOutputStream1.putGeneralizedTime(this.thisUpdate);
/*      */       }
/*  303 */       if (this.nextUpdate != null) {
/*  304 */         if (this.nextUpdate.getTime() < 2524636800000L) {
/*  305 */           localDerOutputStream1.putUTCTime(this.nextUpdate);
/*      */         } else {
/*  307 */           localDerOutputStream1.putGeneralizedTime(this.nextUpdate);
/*      */         }
/*      */       }
/*  310 */       if (!this.revokedList.isEmpty()) {
/*  311 */         for (X509CRLEntry localX509CRLEntry : this.revokedList) {
/*  312 */           ((X509CRLEntryImpl)localX509CRLEntry).encode(localDerOutputStream2);
/*      */         }
/*  314 */         localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*      */       }
/*      */       
/*  317 */       if (this.extensions != null) {
/*  318 */         this.extensions.encode(localDerOutputStream1, true);
/*      */       }
/*  320 */       localDerOutputStream3.write((byte)48, localDerOutputStream1);
/*      */       
/*  322 */       this.tbsCertList = localDerOutputStream3.toByteArray();
/*  323 */       paramOutputStream.write(this.tbsCertList);
/*      */     } catch (IOException localIOException) {
/*  325 */       throw new CRLException("Encoding error: " + localIOException.getMessage());
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
/*      */   public void verify(PublicKey paramPublicKey)
/*      */     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*      */   {
/*  345 */     verify(paramPublicKey, "");
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
/*      */   public synchronized void verify(PublicKey paramPublicKey, String paramString)
/*      */     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*      */   {
/*  368 */     if (paramString == null) {
/*  369 */       paramString = "";
/*      */     }
/*  371 */     if ((this.verifiedPublicKey != null) && (this.verifiedPublicKey.equals(paramPublicKey)))
/*      */     {
/*      */ 
/*  374 */       if (paramString.equals(this.verifiedProvider)) {
/*  375 */         return;
/*      */       }
/*      */     }
/*  378 */     if (this.signedCRL == null) {
/*  379 */       throw new CRLException("Uninitialized CRL");
/*      */     }
/*  381 */     Signature localSignature = null;
/*  382 */     if (paramString.length() == 0) {
/*  383 */       localSignature = Signature.getInstance(this.sigAlgId.getName());
/*      */     } else {
/*  385 */       localSignature = Signature.getInstance(this.sigAlgId.getName(), paramString);
/*      */     }
/*  387 */     localSignature.initVerify(paramPublicKey);
/*      */     
/*  389 */     if (this.tbsCertList == null) {
/*  390 */       throw new CRLException("Uninitialized CRL");
/*      */     }
/*      */     
/*  393 */     localSignature.update(this.tbsCertList, 0, this.tbsCertList.length);
/*      */     
/*  395 */     if (!localSignature.verify(this.signature)) {
/*  396 */       throw new SignatureException("Signature does not match.");
/*      */     }
/*  398 */     this.verifiedPublicKey = paramPublicKey;
/*  399 */     this.verifiedProvider = paramString;
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
/*      */   public synchronized void verify(PublicKey paramPublicKey, Provider paramProvider)
/*      */     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
/*      */   {
/*  422 */     if (this.signedCRL == null) {
/*  423 */       throw new CRLException("Uninitialized CRL");
/*      */     }
/*  425 */     Signature localSignature = null;
/*  426 */     if (paramProvider == null) {
/*  427 */       localSignature = Signature.getInstance(this.sigAlgId.getName());
/*      */     } else {
/*  429 */       localSignature = Signature.getInstance(this.sigAlgId.getName(), paramProvider);
/*      */     }
/*  431 */     localSignature.initVerify(paramPublicKey);
/*      */     
/*  433 */     if (this.tbsCertList == null) {
/*  434 */       throw new CRLException("Uninitialized CRL");
/*      */     }
/*      */     
/*  437 */     localSignature.update(this.tbsCertList, 0, this.tbsCertList.length);
/*      */     
/*  439 */     if (!localSignature.verify(this.signature)) {
/*  440 */       throw new SignatureException("Signature does not match.");
/*      */     }
/*  442 */     this.verifiedPublicKey = paramPublicKey;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void verify(X509CRL paramX509CRL, PublicKey paramPublicKey, Provider paramProvider)
/*      */     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
/*      */   {
/*  454 */     paramX509CRL.verify(paramPublicKey, paramProvider);
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
/*      */   public void sign(PrivateKey paramPrivateKey, String paramString)
/*      */     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*      */   {
/*  473 */     sign(paramPrivateKey, paramString, null);
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
/*      */   public void sign(PrivateKey paramPrivateKey, String paramString1, String paramString2)
/*      */     throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
/*      */   {
/*      */     try
/*      */     {
/*  494 */       if (this.readOnly)
/*  495 */         throw new CRLException("cannot over-write existing CRL");
/*  496 */       Signature localSignature = null;
/*  497 */       if ((paramString2 == null) || (paramString2.length() == 0)) {
/*  498 */         localSignature = Signature.getInstance(paramString1);
/*      */       } else {
/*  500 */         localSignature = Signature.getInstance(paramString1, paramString2);
/*      */       }
/*  502 */       localSignature.initSign(paramPrivateKey);
/*      */       
/*      */ 
/*  505 */       this.sigAlgId = AlgorithmId.get(localSignature.getAlgorithm());
/*  506 */       this.infoSigAlgId = this.sigAlgId;
/*      */       
/*  508 */       DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  509 */       DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*      */       
/*      */ 
/*  512 */       encodeInfo(localDerOutputStream2);
/*      */       
/*      */ 
/*  515 */       this.sigAlgId.encode(localDerOutputStream2);
/*      */       
/*      */ 
/*  518 */       localSignature.update(this.tbsCertList, 0, this.tbsCertList.length);
/*  519 */       this.signature = localSignature.sign();
/*  520 */       localDerOutputStream2.putBitString(this.signature);
/*      */       
/*      */ 
/*  523 */       localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*  524 */       this.signedCRL = localDerOutputStream1.toByteArray();
/*  525 */       this.readOnly = true;
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  529 */       throw new CRLException("Error while encoding data: " + localIOException.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  539 */     return toStringWithAlgName("" + this.sigAlgId);
/*      */   }
/*      */   
/*      */   public String toStringWithAlgName(String paramString)
/*      */   {
/*  544 */     StringBuffer localStringBuffer = new StringBuffer();
/*  545 */     localStringBuffer.append("X.509 CRL v" + (this.version + 1) + "\n");
/*  546 */     if (this.sigAlgId != null)
/*  547 */       localStringBuffer.append("Signature Algorithm: " + paramString.toString() + ", OID=" + this.sigAlgId
/*  548 */         .getOID().toString() + "\n");
/*  549 */     if (this.issuer != null)
/*  550 */       localStringBuffer.append("Issuer: " + this.issuer.toString() + "\n");
/*  551 */     if (this.thisUpdate != null)
/*  552 */       localStringBuffer.append("\nThis Update: " + this.thisUpdate.toString() + "\n");
/*  553 */     if (this.nextUpdate != null)
/*  554 */       localStringBuffer.append("Next Update: " + this.nextUpdate.toString() + "\n");
/*  555 */     int i; Object localObject2; if (this.revokedList.isEmpty()) {
/*  556 */       localStringBuffer.append("\nNO certificates have been revoked\n");
/*      */     } else {
/*  558 */       localStringBuffer.append("\nRevoked Certificates: " + this.revokedList.size());
/*  559 */       i = 1;
/*  560 */       for (localObject2 = this.revokedList.iterator(); ((Iterator)localObject2).hasNext();) { X509CRLEntry localX509CRLEntry = (X509CRLEntry)((Iterator)localObject2).next();
/*  561 */         localStringBuffer.append("\n[" + i++ + "] " + localX509CRLEntry.toString());
/*      */       } }
/*      */     Object localObject1;
/*  564 */     if (this.extensions != null) {
/*  565 */       localObject1 = this.extensions.getAllExtensions();
/*  566 */       localObject2 = ((Collection)localObject1).toArray();
/*  567 */       localStringBuffer.append("\nCRL Extensions: " + localObject2.length);
/*  568 */       for (int j = 0; j < localObject2.length; j++) {
/*  569 */         localStringBuffer.append("\n[" + (j + 1) + "]: ");
/*  570 */         Extension localExtension = (Extension)localObject2[j];
/*      */         try {
/*  572 */           if (OIDMap.getClass(localExtension.getExtensionId()) == null) {
/*  573 */             localStringBuffer.append(localExtension.toString());
/*  574 */             byte[] arrayOfByte = localExtension.getExtensionValue();
/*  575 */             if (arrayOfByte != null) {
/*  576 */               DerOutputStream localDerOutputStream = new DerOutputStream();
/*  577 */               localDerOutputStream.putOctetString(arrayOfByte);
/*  578 */               arrayOfByte = localDerOutputStream.toByteArray();
/*  579 */               HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/*  580 */               localStringBuffer.append("Extension unknown: DER encoded OCTET string =\n" + localHexDumpEncoder
/*      */               
/*  582 */                 .encodeBuffer(arrayOfByte) + "\n");
/*      */             }
/*      */           } else {
/*  585 */             localStringBuffer.append(localExtension.toString());
/*      */           }
/*  587 */         } catch (Exception localException) { localStringBuffer.append(", Error parsing this extension");
/*      */         }
/*      */       }
/*      */     }
/*  591 */     if (this.signature != null) {
/*  592 */       localObject1 = new HexDumpEncoder();
/*  593 */       localStringBuffer.append("\nSignature:\n" + ((HexDumpEncoder)localObject1).encodeBuffer(this.signature) + "\n");
/*      */     }
/*      */     else {
/*  596 */       localStringBuffer.append("NOT signed yet\n"); }
/*  597 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isRevoked(Certificate paramCertificate)
/*      */   {
/*  608 */     if ((this.revokedMap.isEmpty()) || (!(paramCertificate instanceof X509Certificate))) {
/*  609 */       return false;
/*      */     }
/*  611 */     X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
/*  612 */     X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(localX509Certificate);
/*  613 */     return this.revokedMap.containsKey(localX509IssuerSerial);
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
/*      */   public int getVersion()
/*      */   {
/*  627 */     return this.version + 1;
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
/*      */   public Principal getIssuerDN()
/*      */   {
/*  659 */     return this.issuer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public X500Principal getIssuerX500Principal()
/*      */   {
/*  667 */     if (this.issuerPrincipal == null) {
/*  668 */       this.issuerPrincipal = this.issuer.asX500Principal();
/*      */     }
/*  670 */     return this.issuerPrincipal;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Date getThisUpdate()
/*      */   {
/*  680 */     return new Date(this.thisUpdate.getTime());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Date getNextUpdate()
/*      */   {
/*  690 */     if (this.nextUpdate == null)
/*  691 */       return null;
/*  692 */     return new Date(this.nextUpdate.getTime());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X509CRLEntry getRevokedCertificate(BigInteger paramBigInteger)
/*      */   {
/*  703 */     if (this.revokedMap.isEmpty()) {
/*  704 */       return null;
/*      */     }
/*      */     
/*      */ 
/*  708 */     X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(getIssuerX500Principal(), paramBigInteger);
/*  709 */     return (X509CRLEntry)this.revokedMap.get(localX509IssuerSerial);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public X509CRLEntry getRevokedCertificate(X509Certificate paramX509Certificate)
/*      */   {
/*  716 */     if (this.revokedMap.isEmpty()) {
/*  717 */       return null;
/*      */     }
/*  719 */     X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(paramX509Certificate);
/*  720 */     return (X509CRLEntry)this.revokedMap.get(localX509IssuerSerial);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<X509CRLEntry> getRevokedCertificates()
/*      */   {
/*  732 */     if (this.revokedList.isEmpty()) {
/*  733 */       return null;
/*      */     }
/*  735 */     return new TreeSet(this.revokedList);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getTBSCertList()
/*      */     throws CRLException
/*      */   {
/*  748 */     if (this.tbsCertList == null)
/*  749 */       throw new CRLException("Uninitialized CRL");
/*  750 */     return (byte[])this.tbsCertList.clone();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getSignature()
/*      */   {
/*  759 */     if (this.signature == null)
/*  760 */       return null;
/*  761 */     return (byte[])this.signature.clone();
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
/*      */   public String getSigAlgName()
/*      */   {
/*  780 */     if (this.sigAlgId == null)
/*  781 */       return null;
/*  782 */     return this.sigAlgId.getName();
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
/*      */   public String getSigAlgOID()
/*      */   {
/*  799 */     if (this.sigAlgId == null)
/*  800 */       return null;
/*  801 */     ObjectIdentifier localObjectIdentifier = this.sigAlgId.getOID();
/*  802 */     return localObjectIdentifier.toString();
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
/*      */   public byte[] getSigAlgParams()
/*      */   {
/*  815 */     if (this.sigAlgId == null)
/*  816 */       return null;
/*      */     try {
/*  818 */       return this.sigAlgId.getEncodedParams();
/*      */     } catch (IOException localIOException) {}
/*  820 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public AlgorithmId getSigAlgId()
/*      */   {
/*  830 */     return this.sigAlgId;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public KeyIdentifier getAuthKeyId()
/*      */     throws IOException
/*      */   {
/*  841 */     AuthorityKeyIdentifierExtension localAuthorityKeyIdentifierExtension = getAuthKeyIdExtension();
/*  842 */     if (localAuthorityKeyIdentifierExtension != null) {
/*  843 */       KeyIdentifier localKeyIdentifier = (KeyIdentifier)localAuthorityKeyIdentifierExtension.get("key_id");
/*      */       
/*  845 */       return localKeyIdentifier;
/*      */     }
/*  847 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public AuthorityKeyIdentifierExtension getAuthKeyIdExtension()
/*      */     throws IOException
/*      */   {
/*  859 */     Object localObject = getExtension(PKIXExtensions.AuthorityKey_Id);
/*  860 */     return (AuthorityKeyIdentifierExtension)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public CRLNumberExtension getCRLNumberExtension()
/*      */     throws IOException
/*      */   {
/*  870 */     Object localObject = getExtension(PKIXExtensions.CRLNumber_Id);
/*  871 */     return (CRLNumberExtension)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BigInteger getCRLNumber()
/*      */     throws IOException
/*      */   {
/*  881 */     CRLNumberExtension localCRLNumberExtension = getCRLNumberExtension();
/*  882 */     if (localCRLNumberExtension != null) {
/*  883 */       BigInteger localBigInteger = localCRLNumberExtension.get("value");
/*  884 */       return localBigInteger;
/*      */     }
/*  886 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DeltaCRLIndicatorExtension getDeltaCRLIndicatorExtension()
/*      */     throws IOException
/*      */   {
/*  899 */     Object localObject = getExtension(PKIXExtensions.DeltaCRLIndicator_Id);
/*  900 */     return (DeltaCRLIndicatorExtension)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BigInteger getBaseCRLNumber()
/*      */     throws IOException
/*      */   {
/*  910 */     DeltaCRLIndicatorExtension localDeltaCRLIndicatorExtension = getDeltaCRLIndicatorExtension();
/*  911 */     if (localDeltaCRLIndicatorExtension != null) {
/*  912 */       BigInteger localBigInteger = localDeltaCRLIndicatorExtension.get("value");
/*  913 */       return localBigInteger;
/*      */     }
/*  915 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public IssuerAlternativeNameExtension getIssuerAltNameExtension()
/*      */     throws IOException
/*      */   {
/*  927 */     Object localObject = getExtension(PKIXExtensions.IssuerAlternativeName_Id);
/*  928 */     return (IssuerAlternativeNameExtension)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public IssuingDistributionPointExtension getIssuingDistributionPointExtension()
/*      */     throws IOException
/*      */   {
/*  941 */     Object localObject = getExtension(PKIXExtensions.IssuingDistributionPoint_Id);
/*  942 */     return (IssuingDistributionPointExtension)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasUnsupportedCriticalExtension()
/*      */   {
/*  950 */     if (this.extensions == null)
/*  951 */       return false;
/*  952 */     return this.extensions.hasUnsupportedCriticalExtension();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<String> getCriticalExtensionOIDs()
/*      */   {
/*  964 */     if (this.extensions == null) {
/*  965 */       return null;
/*      */     }
/*  967 */     TreeSet localTreeSet = new TreeSet();
/*  968 */     for (Extension localExtension : this.extensions.getAllExtensions()) {
/*  969 */       if (localExtension.isCritical()) {
/*  970 */         localTreeSet.add(localExtension.getExtensionId().toString());
/*      */       }
/*      */     }
/*  973 */     return localTreeSet;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<String> getNonCriticalExtensionOIDs()
/*      */   {
/*  985 */     if (this.extensions == null) {
/*  986 */       return null;
/*      */     }
/*  988 */     TreeSet localTreeSet = new TreeSet();
/*  989 */     for (Extension localExtension : this.extensions.getAllExtensions()) {
/*  990 */       if (!localExtension.isCritical()) {
/*  991 */         localTreeSet.add(localExtension.getExtensionId().toString());
/*      */       }
/*      */     }
/*  994 */     return localTreeSet;
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
/*      */   public byte[] getExtensionValue(String paramString)
/*      */   {
/* 1009 */     if (this.extensions == null)
/* 1010 */       return null;
/*      */     try {
/* 1012 */       String str = OIDMap.getName(new ObjectIdentifier(paramString));
/* 1013 */       Object localObject1 = null;
/*      */       
/* 1015 */       if (str == null) {
/* 1016 */         localObject2 = new ObjectIdentifier(paramString);
/* 1017 */         localObject3 = null;
/*      */         
/* 1019 */         Enumeration localEnumeration = this.extensions.getElements();
/* 1020 */         while (localEnumeration.hasMoreElements()) {
/* 1021 */           localObject3 = (Extension)localEnumeration.nextElement();
/* 1022 */           ObjectIdentifier localObjectIdentifier = ((Extension)localObject3).getExtensionId();
/* 1023 */           if (localObjectIdentifier.equals(localObject2)) {
/* 1024 */             localObject1 = localObject3;
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 1029 */         localObject1 = this.extensions.get(str); }
/* 1030 */       if (localObject1 == null)
/* 1031 */         return null;
/* 1032 */       Object localObject2 = ((Extension)localObject1).getExtensionValue();
/* 1033 */       if (localObject2 == null)
/* 1034 */         return null;
/* 1035 */       Object localObject3 = new DerOutputStream();
/* 1036 */       ((DerOutputStream)localObject3).putOctetString((byte[])localObject2);
/* 1037 */       return ((DerOutputStream)localObject3).toByteArray();
/*      */     } catch (Exception localException) {}
/* 1039 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getExtension(ObjectIdentifier paramObjectIdentifier)
/*      */   {
/* 1051 */     if (this.extensions == null) {
/* 1052 */       return null;
/*      */     }
/*      */     
/* 1055 */     return this.extensions.get(OIDMap.getName(paramObjectIdentifier));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void parse(DerValue paramDerValue)
/*      */     throws CRLException, IOException
/*      */   {
/* 1063 */     if (this.readOnly) {
/* 1064 */       throw new CRLException("cannot over-write existing CRL");
/*      */     }
/* 1066 */     if ((paramDerValue.getData() == null) || (paramDerValue.tag != 48)) {
/* 1067 */       throw new CRLException("Invalid DER-encoded CRL data");
/*      */     }
/* 1069 */     this.signedCRL = paramDerValue.toByteArray();
/* 1070 */     DerValue[] arrayOfDerValue1 = new DerValue[3];
/*      */     
/* 1072 */     arrayOfDerValue1[0] = paramDerValue.data.getDerValue();
/* 1073 */     arrayOfDerValue1[1] = paramDerValue.data.getDerValue();
/* 1074 */     arrayOfDerValue1[2] = paramDerValue.data.getDerValue();
/*      */     
/* 1076 */     if (paramDerValue.data.available() != 0)
/*      */     {
/* 1078 */       throw new CRLException("signed overrun, bytes = " + paramDerValue.data.available());
/*      */     }
/* 1080 */     if (arrayOfDerValue1[0].tag != 48) {
/* 1081 */       throw new CRLException("signed CRL fields invalid");
/*      */     }
/* 1083 */     this.sigAlgId = AlgorithmId.parse(arrayOfDerValue1[1]);
/* 1084 */     this.signature = arrayOfDerValue1[2].getBitString();
/*      */     
/* 1086 */     if (arrayOfDerValue1[1].data.available() != 0) {
/* 1087 */       throw new CRLException("AlgorithmId field overrun");
/*      */     }
/* 1089 */     if (arrayOfDerValue1[2].data.available() != 0) {
/* 1090 */       throw new CRLException("Signature field overrun");
/*      */     }
/*      */     
/* 1093 */     this.tbsCertList = arrayOfDerValue1[0].toByteArray();
/*      */     
/*      */ 
/* 1096 */     DerInputStream localDerInputStream = arrayOfDerValue1[0].data;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1101 */     this.version = 0;
/* 1102 */     int i = (byte)localDerInputStream.peekByte();
/* 1103 */     if (i == 2) {
/* 1104 */       this.version = localDerInputStream.getInteger();
/* 1105 */       if (this.version != 1)
/* 1106 */         throw new CRLException("Invalid version");
/*      */     }
/* 1108 */     DerValue localDerValue = localDerInputStream.getDerValue();
/*      */     
/*      */ 
/* 1111 */     AlgorithmId localAlgorithmId = AlgorithmId.parse(localDerValue);
/*      */     
/*      */ 
/* 1114 */     if (!localAlgorithmId.equals(this.sigAlgId))
/* 1115 */       throw new CRLException("Signature algorithm mismatch");
/* 1116 */     this.infoSigAlgId = localAlgorithmId;
/*      */     
/*      */ 
/* 1119 */     this.issuer = new X500Name(localDerInputStream);
/* 1120 */     if (this.issuer.isEmpty()) {
/* 1121 */       throw new CRLException("Empty issuer DN not allowed in X509CRLs");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1127 */     i = (byte)localDerInputStream.peekByte();
/* 1128 */     if (i == 23) {
/* 1129 */       this.thisUpdate = localDerInputStream.getUTCTime();
/* 1130 */     } else if (i == 24) {
/* 1131 */       this.thisUpdate = localDerInputStream.getGeneralizedTime();
/*      */     } else {
/* 1133 */       throw new CRLException("Invalid encoding for thisUpdate (tag=" + i + ")");
/*      */     }
/*      */     
/*      */ 
/* 1137 */     if (localDerInputStream.available() == 0) {
/* 1138 */       return;
/*      */     }
/*      */     
/* 1141 */     i = (byte)localDerInputStream.peekByte();
/* 1142 */     if (i == 23) {
/* 1143 */       this.nextUpdate = localDerInputStream.getUTCTime();
/* 1144 */     } else if (i == 24) {
/* 1145 */       this.nextUpdate = localDerInputStream.getGeneralizedTime();
/*      */     }
/*      */     
/* 1148 */     if (localDerInputStream.available() == 0) {
/* 1149 */       return;
/*      */     }
/*      */     
/* 1152 */     i = (byte)localDerInputStream.peekByte();
/* 1153 */     if ((i == 48) && ((i & 0xC0) != 128))
/*      */     {
/* 1155 */       DerValue[] arrayOfDerValue2 = localDerInputStream.getSequence(4);
/*      */       
/* 1157 */       X500Principal localX500Principal1 = getIssuerX500Principal();
/* 1158 */       X500Principal localX500Principal2 = localX500Principal1;
/* 1159 */       for (int j = 0; j < arrayOfDerValue2.length; j++) {
/* 1160 */         X509CRLEntryImpl localX509CRLEntryImpl = new X509CRLEntryImpl(arrayOfDerValue2[j]);
/* 1161 */         localX500Principal2 = getCertIssuer(localX509CRLEntryImpl, localX500Principal2);
/* 1162 */         localX509CRLEntryImpl.setCertificateIssuer(localX500Principal1, localX500Principal2);
/*      */         
/* 1164 */         X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(localX500Principal2, localX509CRLEntryImpl.getSerialNumber());
/* 1165 */         this.revokedMap.put(localX509IssuerSerial, localX509CRLEntryImpl);
/* 1166 */         this.revokedList.add(localX509CRLEntryImpl);
/*      */       }
/*      */     }
/*      */     
/* 1170 */     if (localDerInputStream.available() == 0) {
/* 1171 */       return;
/*      */     }
/*      */     
/* 1174 */     localDerValue = localDerInputStream.getDerValue();
/* 1175 */     if ((localDerValue.isConstructed()) && (localDerValue.isContextSpecific((byte)0))) {
/* 1176 */       this.extensions = new CRLExtensions(localDerValue.data);
/*      */     }
/* 1178 */     this.readOnly = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static X500Principal getIssuerX500Principal(X509CRL paramX509CRL)
/*      */   {
/*      */     try
/*      */     {
/* 1189 */       byte[] arrayOfByte1 = paramX509CRL.getEncoded();
/* 1190 */       DerInputStream localDerInputStream1 = new DerInputStream(arrayOfByte1);
/* 1191 */       DerValue localDerValue1 = localDerInputStream1.getSequence(3)[0];
/* 1192 */       DerInputStream localDerInputStream2 = localDerValue1.data;
/*      */       
/*      */ 
/*      */ 
/* 1196 */       int i = (byte)localDerInputStream2.peekByte();
/* 1197 */       if (i == 2) {
/* 1198 */         localDerValue2 = localDerInputStream2.getDerValue();
/*      */       }
/*      */       
/* 1201 */       DerValue localDerValue2 = localDerInputStream2.getDerValue();
/* 1202 */       localDerValue2 = localDerInputStream2.getDerValue();
/* 1203 */       byte[] arrayOfByte2 = localDerValue2.toByteArray();
/* 1204 */       return new X500Principal(arrayOfByte2);
/*      */     } catch (Exception localException) {
/* 1206 */       throw new RuntimeException("Could not parse issuer", localException);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] getEncodedInternal(X509CRL paramX509CRL)
/*      */     throws CRLException
/*      */   {
/* 1217 */     if ((paramX509CRL instanceof X509CRLImpl)) {
/* 1218 */       return ((X509CRLImpl)paramX509CRL).getEncodedInternal();
/*      */     }
/* 1220 */     return paramX509CRL.getEncoded();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static X509CRLImpl toImpl(X509CRL paramX509CRL)
/*      */     throws CRLException
/*      */   {
/* 1231 */     if ((paramX509CRL instanceof X509CRLImpl)) {
/* 1232 */       return (X509CRLImpl)paramX509CRL;
/*      */     }
/* 1234 */     return X509Factory.intern(paramX509CRL);
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
/*      */   private X500Principal getCertIssuer(X509CRLEntryImpl paramX509CRLEntryImpl, X500Principal paramX500Principal)
/*      */     throws IOException
/*      */   {
/* 1250 */     CertificateIssuerExtension localCertificateIssuerExtension = paramX509CRLEntryImpl.getCertificateIssuerExtension();
/* 1251 */     if (localCertificateIssuerExtension != null) {
/* 1252 */       GeneralNames localGeneralNames = localCertificateIssuerExtension.get("issuer");
/* 1253 */       X500Name localX500Name = (X500Name)localGeneralNames.get(0).getName();
/* 1254 */       return localX500Name.asX500Principal();
/*      */     }
/* 1256 */     return paramX500Principal;
/*      */   }
/*      */   
/*      */   public void derEncode(OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/* 1262 */     if (this.signedCRL == null)
/* 1263 */       throw new IOException("Null CRL to encode");
/* 1264 */     paramOutputStream.write((byte[])this.signedCRL.clone());
/*      */   }
/*      */   
/*      */ 
/*      */   private static final class X509IssuerSerial
/*      */     implements Comparable<X509IssuerSerial>
/*      */   {
/*      */     final X500Principal issuer;
/*      */     
/*      */     final BigInteger serial;
/* 1274 */     volatile int hashcode = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     X509IssuerSerial(X500Principal paramX500Principal, BigInteger paramBigInteger)
/*      */     {
/* 1283 */       this.issuer = paramX500Principal;
/* 1284 */       this.serial = paramBigInteger;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     X509IssuerSerial(X509Certificate paramX509Certificate)
/*      */     {
/* 1291 */       this(paramX509Certificate.getIssuerX500Principal(), paramX509Certificate.getSerialNumber());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     X500Principal getIssuer()
/*      */     {
/* 1300 */       return this.issuer;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     BigInteger getSerial()
/*      */     {
/* 1309 */       return this.serial;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean equals(Object paramObject)
/*      */     {
/* 1320 */       if (paramObject == this) {
/* 1321 */         return true;
/*      */       }
/*      */       
/* 1324 */       if (!(paramObject instanceof X509IssuerSerial)) {
/* 1325 */         return false;
/*      */       }
/*      */       
/* 1328 */       X509IssuerSerial localX509IssuerSerial = (X509IssuerSerial)paramObject;
/* 1329 */       if ((this.serial.equals(localX509IssuerSerial.getSerial())) && 
/* 1330 */         (this.issuer.equals(localX509IssuerSerial.getIssuer()))) {
/* 1331 */         return true;
/*      */       }
/* 1333 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1342 */       if (this.hashcode == 0) {
/* 1343 */         int i = 17;
/* 1344 */         i = 37 * i + this.issuer.hashCode();
/* 1345 */         i = 37 * i + this.serial.hashCode();
/* 1346 */         this.hashcode = i;
/*      */       }
/* 1348 */       return this.hashcode;
/*      */     }
/*      */     
/*      */ 
/*      */     public int compareTo(X509IssuerSerial paramX509IssuerSerial)
/*      */     {
/* 1354 */       int i = this.issuer.toString().compareTo(paramX509IssuerSerial.issuer.toString());
/* 1355 */       if (i != 0) return i;
/* 1356 */       return this.serial.compareTo(paramX509IssuerSerial.serial);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\X509CRLImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
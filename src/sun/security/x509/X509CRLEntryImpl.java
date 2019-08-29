/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CRLReason;
/*     */ import java.security.cert.X509CRLEntry;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509CRLEntryImpl
/*     */   extends X509CRLEntry
/*     */   implements Comparable<X509CRLEntryImpl>
/*     */ {
/*  73 */   private SerialNumber serialNumber = null;
/*  74 */   private Date revocationDate = null;
/*  75 */   private CRLExtensions extensions = null;
/*  76 */   private byte[] revokedCert = null;
/*     */   
/*     */ 
/*     */   private X500Principal certIssuer;
/*     */   
/*     */ 
/*     */   private static final boolean isExplicit = false;
/*     */   
/*     */ 
/*     */   private static final long YR_2050 = 2524636800000L;
/*     */   
/*     */ 
/*     */   public X509CRLEntryImpl(BigInteger paramBigInteger, Date paramDate)
/*     */   {
/*  90 */     this.serialNumber = new SerialNumber(paramBigInteger);
/*  91 */     this.revocationDate = paramDate;
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
/*     */   public X509CRLEntryImpl(BigInteger paramBigInteger, Date paramDate, CRLExtensions paramCRLExtensions)
/*     */   {
/* 105 */     this.serialNumber = new SerialNumber(paramBigInteger);
/* 106 */     this.revocationDate = paramDate;
/* 107 */     this.extensions = paramCRLExtensions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CRLEntryImpl(byte[] paramArrayOfByte)
/*     */     throws CRLException
/*     */   {
/*     */     try
/*     */     {
/* 118 */       parse(new DerValue(paramArrayOfByte));
/*     */     } catch (IOException localIOException) {
/* 120 */       this.revokedCert = null;
/* 121 */       throw new CRLException("Parsing error: " + localIOException.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CRLEntryImpl(DerValue paramDerValue)
/*     */     throws CRLException
/*     */   {
/*     */     try
/*     */     {
/* 133 */       parse(paramDerValue);
/*     */     } catch (IOException localIOException) {
/* 135 */       this.revokedCert = null;
/* 136 */       throw new CRLException("Parsing error: " + localIOException.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasExtensions()
/*     */   {
/* 148 */     return this.extensions != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws CRLException
/*     */   {
/*     */     try
/*     */     {
/* 160 */       if (this.revokedCert == null) {
/* 161 */         DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*     */         
/* 163 */         this.serialNumber.encode(localDerOutputStream1);
/*     */         
/* 165 */         if (this.revocationDate.getTime() < 2524636800000L) {
/* 166 */           localDerOutputStream1.putUTCTime(this.revocationDate);
/*     */         } else {
/* 168 */           localDerOutputStream1.putGeneralizedTime(this.revocationDate);
/*     */         }
/*     */         
/* 171 */         if (this.extensions != null) {
/* 172 */           this.extensions.encode(localDerOutputStream1, false);
/*     */         }
/* 174 */         DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 175 */         localDerOutputStream2.write((byte)48, localDerOutputStream1);
/*     */         
/* 177 */         this.revokedCert = localDerOutputStream2.toByteArray();
/*     */       }
/* 179 */       paramDerOutputStream.write(this.revokedCert);
/*     */     } catch (IOException localIOException) {
/* 181 */       throw new CRLException("Encoding error: " + localIOException.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */     throws CRLException
/*     */   {
/* 192 */     return (byte[])getEncoded0().clone();
/*     */   }
/*     */   
/*     */   private byte[] getEncoded0() throws CRLException
/*     */   {
/* 197 */     if (this.revokedCert == null)
/* 198 */       encode(new DerOutputStream());
/* 199 */     return this.revokedCert;
/*     */   }
/*     */   
/*     */   public X500Principal getCertificateIssuer()
/*     */   {
/* 204 */     return this.certIssuer;
/*     */   }
/*     */   
/*     */   void setCertificateIssuer(X500Principal paramX500Principal1, X500Principal paramX500Principal2) {
/* 208 */     if (paramX500Principal1.equals(paramX500Principal2)) {
/* 209 */       this.certIssuer = null;
/*     */     } else {
/* 211 */       this.certIssuer = paramX500Principal2;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getSerialNumber()
/*     */   {
/* 222 */     return this.serialNumber.getNumber();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date getRevocationDate()
/*     */   {
/* 232 */     return new Date(this.revocationDate.getTime());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CRLReason getRevocationReason()
/*     */   {
/* 242 */     Extension localExtension = getExtension(PKIXExtensions.ReasonCode_Id);
/* 243 */     if (localExtension == null) {
/* 244 */       return null;
/*     */     }
/* 246 */     CRLReasonCodeExtension localCRLReasonCodeExtension = (CRLReasonCodeExtension)localExtension;
/* 247 */     return localCRLReasonCodeExtension.getReasonCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static CRLReason getRevocationReason(X509CRLEntry paramX509CRLEntry)
/*     */   {
/*     */     try
/*     */     {
/* 256 */       byte[] arrayOfByte1 = paramX509CRLEntry.getExtensionValue("2.5.29.21");
/* 257 */       if (arrayOfByte1 == null) {
/* 258 */         return null;
/*     */       }
/* 260 */       DerValue localDerValue = new DerValue(arrayOfByte1);
/* 261 */       byte[] arrayOfByte2 = localDerValue.getOctetString();
/*     */       
/* 263 */       CRLReasonCodeExtension localCRLReasonCodeExtension = new CRLReasonCodeExtension(Boolean.FALSE, arrayOfByte2);
/*     */       
/* 265 */       return localCRLReasonCodeExtension.getReasonCode();
/*     */     } catch (IOException localIOException) {}
/* 267 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Integer getReasonCode()
/*     */     throws IOException
/*     */   {
/* 278 */     Extension localExtension = getExtension(PKIXExtensions.ReasonCode_Id);
/* 279 */     if (localExtension == null)
/* 280 */       return null;
/* 281 */     CRLReasonCodeExtension localCRLReasonCodeExtension = (CRLReasonCodeExtension)localExtension;
/* 282 */     return localCRLReasonCodeExtension.get("reason");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 292 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     
/* 294 */     localStringBuilder.append(this.serialNumber.toString());
/* 295 */     localStringBuilder.append("  On: " + this.revocationDate.toString());
/* 296 */     if (this.certIssuer != null) {
/* 297 */       localStringBuilder.append("\n    Certificate issuer: " + this.certIssuer);
/*     */     }
/* 299 */     if (this.extensions != null) {
/* 300 */       Collection localCollection = this.extensions.getAllExtensions();
/* 301 */       Extension[] arrayOfExtension = (Extension[])localCollection.toArray(new Extension[0]);
/*     */       
/* 303 */       localStringBuilder.append("\n    CRL Entry Extensions: " + arrayOfExtension.length);
/* 304 */       for (int i = 0; i < arrayOfExtension.length; i++) {
/* 305 */         localStringBuilder.append("\n    [" + (i + 1) + "]: ");
/* 306 */         Extension localExtension = arrayOfExtension[i];
/*     */         try {
/* 308 */           if (OIDMap.getClass(localExtension.getExtensionId()) == null) {
/* 309 */             localStringBuilder.append(localExtension.toString());
/* 310 */             byte[] arrayOfByte = localExtension.getExtensionValue();
/* 311 */             if (arrayOfByte != null) {
/* 312 */               DerOutputStream localDerOutputStream = new DerOutputStream();
/* 313 */               localDerOutputStream.putOctetString(arrayOfByte);
/* 314 */               arrayOfByte = localDerOutputStream.toByteArray();
/* 315 */               HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/* 316 */               localStringBuilder.append("Extension unknown: DER encoded OCTET string =\n" + localHexDumpEncoder
/*     */               
/* 318 */                 .encodeBuffer(arrayOfByte) + "\n");
/*     */             }
/*     */           } else {
/* 321 */             localStringBuilder.append(localExtension.toString());
/*     */           }
/* 323 */         } catch (Exception localException) { localStringBuilder.append(", Error parsing this extension");
/*     */         }
/*     */       }
/*     */     }
/* 327 */     localStringBuilder.append("\n");
/* 328 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasUnsupportedCriticalExtension()
/*     */   {
/* 336 */     if (this.extensions == null)
/* 337 */       return false;
/* 338 */     return this.extensions.hasUnsupportedCriticalExtension();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<String> getCriticalExtensionOIDs()
/*     */   {
/* 350 */     if (this.extensions == null) {
/* 351 */       return null;
/*     */     }
/* 353 */     TreeSet localTreeSet = new TreeSet();
/* 354 */     for (Extension localExtension : this.extensions.getAllExtensions()) {
/* 355 */       if (localExtension.isCritical()) {
/* 356 */         localTreeSet.add(localExtension.getExtensionId().toString());
/*     */       }
/*     */     }
/* 359 */     return localTreeSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<String> getNonCriticalExtensionOIDs()
/*     */   {
/* 371 */     if (this.extensions == null) {
/* 372 */       return null;
/*     */     }
/* 374 */     TreeSet localTreeSet = new TreeSet();
/* 375 */     for (Extension localExtension : this.extensions.getAllExtensions()) {
/* 376 */       if (!localExtension.isCritical()) {
/* 377 */         localTreeSet.add(localExtension.getExtensionId().toString());
/*     */       }
/*     */     }
/* 380 */     return localTreeSet;
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
/*     */   public byte[] getExtensionValue(String paramString)
/*     */   {
/* 396 */     if (this.extensions == null)
/* 397 */       return null;
/*     */     try {
/* 399 */       String str = OIDMap.getName(new ObjectIdentifier(paramString));
/* 400 */       Object localObject1 = null;
/*     */       
/* 402 */       if (str == null) {
/* 403 */         localObject2 = new ObjectIdentifier(paramString);
/* 404 */         localObject3 = null;
/*     */         
/* 406 */         Enumeration localEnumeration = this.extensions.getElements();
/* 407 */         while (localEnumeration.hasMoreElements()) {
/* 408 */           localObject3 = (Extension)localEnumeration.nextElement();
/* 409 */           ObjectIdentifier localObjectIdentifier = ((Extension)localObject3).getExtensionId();
/* 410 */           if (localObjectIdentifier.equals(localObject2)) {
/* 411 */             localObject1 = localObject3;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 416 */         localObject1 = this.extensions.get(str); }
/* 417 */       if (localObject1 == null)
/* 418 */         return null;
/* 419 */       Object localObject2 = ((Extension)localObject1).getExtensionValue();
/* 420 */       if (localObject2 == null) {
/* 421 */         return null;
/*     */       }
/* 423 */       Object localObject3 = new DerOutputStream();
/* 424 */       ((DerOutputStream)localObject3).putOctetString((byte[])localObject2);
/* 425 */       return ((DerOutputStream)localObject3).toByteArray();
/*     */     } catch (Exception localException) {}
/* 427 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Extension getExtension(ObjectIdentifier paramObjectIdentifier)
/*     */   {
/* 438 */     if (this.extensions == null) {
/* 439 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 443 */     return this.extensions.get(OIDMap.getName(paramObjectIdentifier));
/*     */   }
/*     */   
/*     */   private void parse(DerValue paramDerValue)
/*     */     throws CRLException, IOException
/*     */   {
/* 449 */     if (paramDerValue.tag != 48) {
/* 450 */       throw new CRLException("Invalid encoded RevokedCertificate, starting sequence tag missing.");
/*     */     }
/*     */     
/* 453 */     if (paramDerValue.data.available() == 0) {
/* 454 */       throw new CRLException("No data encoded for RevokedCertificates");
/*     */     }
/* 456 */     this.revokedCert = paramDerValue.toByteArray();
/*     */     
/* 458 */     DerInputStream localDerInputStream = paramDerValue.toDerInputStream();
/* 459 */     DerValue localDerValue = localDerInputStream.getDerValue();
/* 460 */     this.serialNumber = new SerialNumber(localDerValue);
/*     */     
/*     */ 
/* 463 */     int i = paramDerValue.data.peekByte();
/* 464 */     if ((byte)i == 23) {
/* 465 */       this.revocationDate = paramDerValue.data.getUTCTime();
/* 466 */     } else if ((byte)i == 24) {
/* 467 */       this.revocationDate = paramDerValue.data.getGeneralizedTime();
/*     */     } else {
/* 469 */       throw new CRLException("Invalid encoding for revocation date");
/*     */     }
/* 471 */     if (paramDerValue.data.available() == 0) {
/* 472 */       return;
/*     */     }
/*     */     
/* 475 */     this.extensions = new CRLExtensions(paramDerValue.toDerInputStream());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static X509CRLEntryImpl toImpl(X509CRLEntry paramX509CRLEntry)
/*     */     throws CRLException
/*     */   {
/* 485 */     if ((paramX509CRLEntry instanceof X509CRLEntryImpl)) {
/* 486 */       return (X509CRLEntryImpl)paramX509CRLEntry;
/*     */     }
/* 488 */     return new X509CRLEntryImpl(paramX509CRLEntry.getEncoded());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   CertificateIssuerExtension getCertificateIssuerExtension()
/*     */   {
/* 498 */     return 
/* 499 */       (CertificateIssuerExtension)getExtension(PKIXExtensions.CertificateIssuer_Id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, java.security.cert.Extension> getExtensions()
/*     */   {
/* 507 */     if (this.extensions == null) {
/* 508 */       return Collections.emptyMap();
/*     */     }
/* 510 */     Collection localCollection = this.extensions.getAllExtensions();
/* 511 */     TreeMap localTreeMap = new TreeMap();
/* 512 */     for (Extension localExtension : localCollection) {
/* 513 */       localTreeMap.put(localExtension.getId(), localExtension);
/*     */     }
/* 515 */     return localTreeMap;
/*     */   }
/*     */   
/*     */   public int compareTo(X509CRLEntryImpl paramX509CRLEntryImpl)
/*     */   {
/* 520 */     int i = getSerialNumber().compareTo(paramX509CRLEntryImpl.getSerialNumber());
/* 521 */     if (i != 0) {
/* 522 */       return i;
/*     */     }
/*     */     try {
/* 525 */       byte[] arrayOfByte1 = getEncoded0();
/* 526 */       byte[] arrayOfByte2 = paramX509CRLEntryImpl.getEncoded0();
/* 527 */       for (int j = 0; (j < arrayOfByte1.length) && (j < arrayOfByte2.length); j++) {
/* 528 */         int k = arrayOfByte1[j] & 0xFF;
/* 529 */         int m = arrayOfByte2[j] & 0xFF;
/* 530 */         if (k != m) return k - m;
/*     */       }
/* 532 */       return arrayOfByte1.length - arrayOfByte2.length;
/*     */     } catch (CRLException localCRLException) {}
/* 534 */     return -1;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\X509CRLEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
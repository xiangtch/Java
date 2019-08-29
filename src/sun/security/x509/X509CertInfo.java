/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509CertInfo
/*     */   implements CertAttrSet<String>
/*     */ {
/*     */   public static final String IDENT = "x509.info";
/*     */   public static final String NAME = "info";
/*     */   public static final String DN_NAME = "dname";
/*     */   public static final String VERSION = "version";
/*     */   public static final String SERIAL_NUMBER = "serialNumber";
/*     */   public static final String ALGORITHM_ID = "algorithmID";
/*     */   public static final String ISSUER = "issuer";
/*     */   public static final String SUBJECT = "subject";
/*     */   public static final String VALIDITY = "validity";
/*     */   public static final String KEY = "key";
/*     */   public static final String ISSUER_ID = "issuerID";
/*     */   public static final String SUBJECT_ID = "subjectID";
/*     */   public static final String EXTENSIONS = "extensions";
/*  84 */   protected CertificateVersion version = new CertificateVersion();
/*  85 */   protected CertificateSerialNumber serialNum = null;
/*  86 */   protected CertificateAlgorithmId algId = null;
/*  87 */   protected X500Name issuer = null;
/*  88 */   protected X500Name subject = null;
/*  89 */   protected CertificateValidity interval = null;
/*  90 */   protected CertificateX509Key pubKey = null;
/*     */   
/*     */ 
/*  93 */   protected UniqueIdentity issuerUniqueId = null;
/*  94 */   protected UniqueIdentity subjectUniqueId = null;
/*     */   
/*     */ 
/*  97 */   protected CertificateExtensions extensions = null;
/*     */   
/*     */   private static final int ATTR_VERSION = 1;
/*     */   
/*     */   private static final int ATTR_SERIAL = 2;
/*     */   
/*     */   private static final int ATTR_ALGORITHM = 3;
/*     */   
/*     */   private static final int ATTR_ISSUER = 4;
/*     */   private static final int ATTR_VALIDITY = 5;
/*     */   private static final int ATTR_SUBJECT = 6;
/*     */   private static final int ATTR_KEY = 7;
/*     */   private static final int ATTR_ISSUER_ID = 8;
/*     */   private static final int ATTR_SUBJECT_ID = 9;
/*     */   private static final int ATTR_EXTENSIONS = 10;
/* 112 */   private byte[] rawCertInfo = null;
/*     */   
/*     */ 
/* 115 */   private static final Map<String, Integer> map = new HashMap();
/*     */   
/* 117 */   static { map.put("version", Integer.valueOf(1));
/* 118 */     map.put("serialNumber", Integer.valueOf(2));
/* 119 */     map.put("algorithmID", Integer.valueOf(3));
/* 120 */     map.put("issuer", Integer.valueOf(4));
/* 121 */     map.put("validity", Integer.valueOf(5));
/* 122 */     map.put("subject", Integer.valueOf(6));
/* 123 */     map.put("key", Integer.valueOf(7));
/* 124 */     map.put("issuerID", Integer.valueOf(8));
/* 125 */     map.put("subjectID", Integer.valueOf(9));
/* 126 */     map.put("extensions", Integer.valueOf(10));
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
/*     */   public X509CertInfo(byte[] paramArrayOfByte)
/*     */     throws CertificateParsingException
/*     */   {
/*     */     try
/*     */     {
/* 149 */       DerValue localDerValue = new DerValue(paramArrayOfByte);
/*     */       
/* 151 */       parse(localDerValue);
/*     */     } catch (IOException localIOException) {
/* 153 */       throw new CertificateParsingException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CertInfo(DerValue paramDerValue)
/*     */     throws CertificateParsingException
/*     */   {
/*     */     try
/*     */     {
/* 167 */       parse(paramDerValue);
/*     */     } catch (IOException localIOException) {
/* 169 */       throw new CertificateParsingException(localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws CertificateException, IOException
/*     */   {
/* 182 */     if (this.rawCertInfo == null) {
/* 183 */       DerOutputStream localDerOutputStream = new DerOutputStream();
/* 184 */       emit(localDerOutputStream);
/* 185 */       this.rawCertInfo = localDerOutputStream.toByteArray();
/*     */     }
/* 187 */     paramOutputStream.write((byte[])this.rawCertInfo.clone());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 195 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 196 */     localAttributeNameEnumeration.addElement("version");
/* 197 */     localAttributeNameEnumeration.addElement("serialNumber");
/* 198 */     localAttributeNameEnumeration.addElement("algorithmID");
/* 199 */     localAttributeNameEnumeration.addElement("issuer");
/* 200 */     localAttributeNameEnumeration.addElement("validity");
/* 201 */     localAttributeNameEnumeration.addElement("subject");
/* 202 */     localAttributeNameEnumeration.addElement("key");
/* 203 */     localAttributeNameEnumeration.addElement("issuerID");
/* 204 */     localAttributeNameEnumeration.addElement("subjectID");
/* 205 */     localAttributeNameEnumeration.addElement("extensions");
/*     */     
/* 207 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 214 */     return "info";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getEncodedInfo()
/*     */     throws CertificateEncodingException
/*     */   {
/*     */     try
/*     */     {
/* 224 */       if (this.rawCertInfo == null) {
/* 225 */         DerOutputStream localDerOutputStream = new DerOutputStream();
/* 226 */         emit(localDerOutputStream);
/* 227 */         this.rawCertInfo = localDerOutputStream.toByteArray();
/*     */       }
/* 229 */       return (byte[])this.rawCertInfo.clone();
/*     */     } catch (IOException localIOException) {
/* 231 */       throw new CertificateEncodingException(localIOException.toString());
/*     */     } catch (CertificateException localCertificateException) {
/* 233 */       throw new CertificateEncodingException(localCertificateException.toString());
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 246 */     if ((paramObject instanceof X509CertInfo)) {
/* 247 */       return equals((X509CertInfo)paramObject);
/*     */     }
/* 249 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(X509CertInfo paramX509CertInfo)
/*     */   {
/* 261 */     if (this == paramX509CertInfo)
/* 262 */       return true;
/* 263 */     if ((this.rawCertInfo == null) || (paramX509CertInfo.rawCertInfo == null))
/* 264 */       return false;
/* 265 */     if (this.rawCertInfo.length != paramX509CertInfo.rawCertInfo.length) {
/* 266 */       return false;
/*     */     }
/* 268 */     for (int i = 0; i < this.rawCertInfo.length; i++) {
/* 269 */       if (this.rawCertInfo[i] != paramX509CertInfo.rawCertInfo[i]) {
/* 270 */         return false;
/*     */       }
/*     */     }
/* 273 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 281 */     int i = 0;
/*     */     
/* 283 */     for (int j = 1; j < this.rawCertInfo.length; j++) {
/* 284 */       i += this.rawCertInfo[j] * j;
/*     */     }
/* 286 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 294 */     if ((this.subject == null) || (this.pubKey == null) || (this.interval == null) || (this.issuer == null) || (this.algId == null) || (this.serialNum == null))
/*     */     {
/* 296 */       throw new NullPointerException("X.509 cert is incomplete");
/*     */     }
/* 298 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     
/* 300 */     localStringBuilder.append("[\n");
/* 301 */     localStringBuilder.append("  " + this.version.toString() + "\n");
/* 302 */     localStringBuilder.append("  Subject: " + this.subject.toString() + "\n");
/* 303 */     localStringBuilder.append("  Signature Algorithm: " + this.algId.toString() + "\n");
/* 304 */     localStringBuilder.append("  Key:  " + this.pubKey.toString() + "\n");
/* 305 */     localStringBuilder.append("  " + this.interval.toString() + "\n");
/* 306 */     localStringBuilder.append("  Issuer: " + this.issuer.toString() + "\n");
/* 307 */     localStringBuilder.append("  " + this.serialNum.toString() + "\n");
/*     */     
/*     */ 
/* 310 */     if (this.issuerUniqueId != null) {
/* 311 */       localStringBuilder.append("  Issuer Id:\n" + this.issuerUniqueId.toString() + "\n");
/*     */     }
/* 313 */     if (this.subjectUniqueId != null)
/* 314 */       localStringBuilder.append("  Subject Id:\n" + this.subjectUniqueId.toString() + "\n");
/*     */     Object localObject;
/* 316 */     int j; Iterator localIterator; if (this.extensions != null) {
/* 317 */       Collection localCollection = this.extensions.getAllExtensions();
/* 318 */       Extension[] arrayOfExtension = (Extension[])localCollection.toArray(new Extension[0]);
/* 319 */       localStringBuilder.append("\nCertificate Extensions: " + arrayOfExtension.length);
/* 320 */       for (int i = 0; i < arrayOfExtension.length; i++) {
/* 321 */         localStringBuilder.append("\n[" + (i + 1) + "]: ");
/* 322 */         Extension localExtension = arrayOfExtension[i];
/*     */         try {
/* 324 */           if (OIDMap.getClass(localExtension.getExtensionId()) == null) {
/* 325 */             localStringBuilder.append(localExtension.toString());
/* 326 */             byte[] arrayOfByte = localExtension.getExtensionValue();
/* 327 */             if (arrayOfByte != null) {
/* 328 */               localObject = new DerOutputStream();
/* 329 */               ((DerOutputStream)localObject).putOctetString(arrayOfByte);
/* 330 */               arrayOfByte = ((DerOutputStream)localObject).toByteArray();
/* 331 */               HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/* 332 */               localStringBuilder.append("Extension unknown: DER encoded OCTET string =\n" + localHexDumpEncoder
/*     */               
/* 334 */                 .encodeBuffer(arrayOfByte) + "\n");
/*     */             }
/*     */           } else {
/* 337 */             localStringBuilder.append(localExtension.toString());
/*     */           }
/* 339 */         } catch (Exception localException) { localStringBuilder.append(", Error parsing this extension");
/*     */         }
/*     */       }
/* 342 */       Map localMap = this.extensions.getUnparseableExtensions();
/* 343 */       if (!localMap.isEmpty()) {
/* 344 */         localStringBuilder.append("\nUnparseable certificate extensions: " + localMap.size());
/* 345 */         j = 1;
/* 346 */         for (localIterator = localMap.values().iterator(); localIterator.hasNext();) { localObject = (Extension)localIterator.next();
/* 347 */           localStringBuilder.append("\n[" + j++ + "]: ");
/* 348 */           localStringBuilder.append(localObject);
/*     */         }
/*     */       }
/*     */     }
/* 352 */     localStringBuilder.append("\n]");
/* 353 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws CertificateException, IOException
/*     */   {
/* 366 */     X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
/*     */     
/* 368 */     int i = attributeMap(localX509AttributeName.getPrefix());
/* 369 */     if (i == 0) {
/* 370 */       throw new CertificateException("Attribute name not recognized: " + paramString);
/*     */     }
/*     */     
/*     */ 
/* 374 */     this.rawCertInfo = null;
/* 375 */     String str = localX509AttributeName.getSuffix();
/*     */     
/* 377 */     switch (i) {
/*     */     case 1: 
/* 379 */       if (str == null) {
/* 380 */         setVersion(paramObject);
/*     */       } else {
/* 382 */         this.version.set(str, paramObject);
/*     */       }
/* 384 */       break;
/*     */     
/*     */     case 2: 
/* 387 */       if (str == null) {
/* 388 */         setSerialNumber(paramObject);
/*     */       } else {
/* 390 */         this.serialNum.set(str, paramObject);
/*     */       }
/* 392 */       break;
/*     */     
/*     */     case 3: 
/* 395 */       if (str == null) {
/* 396 */         setAlgorithmId(paramObject);
/*     */       } else {
/* 398 */         this.algId.set(str, paramObject);
/*     */       }
/* 400 */       break;
/*     */     
/*     */     case 4: 
/* 403 */       setIssuer(paramObject);
/* 404 */       break;
/*     */     
/*     */     case 5: 
/* 407 */       if (str == null) {
/* 408 */         setValidity(paramObject);
/*     */       } else {
/* 410 */         this.interval.set(str, paramObject);
/*     */       }
/* 412 */       break;
/*     */     
/*     */     case 6: 
/* 415 */       setSubject(paramObject);
/* 416 */       break;
/*     */     
/*     */     case 7: 
/* 419 */       if (str == null) {
/* 420 */         setKey(paramObject);
/*     */       } else {
/* 422 */         this.pubKey.set(str, paramObject);
/*     */       }
/* 424 */       break;
/*     */     
/*     */     case 8: 
/* 427 */       setIssuerUniqueId(paramObject);
/* 428 */       break;
/*     */     
/*     */     case 9: 
/* 431 */       setSubjectUniqueId(paramObject);
/* 432 */       break;
/*     */     
/*     */     case 10: 
/* 435 */       if (str == null) {
/* 436 */         setExtensions(paramObject);
/*     */       } else {
/* 438 */         if (this.extensions == null)
/* 439 */           this.extensions = new CertificateExtensions();
/* 440 */         this.extensions.set(str, paramObject);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       break;
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws CertificateException, IOException
/*     */   {
/* 455 */     X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
/*     */     
/* 457 */     int i = attributeMap(localX509AttributeName.getPrefix());
/* 458 */     if (i == 0) {
/* 459 */       throw new CertificateException("Attribute name not recognized: " + paramString);
/*     */     }
/*     */     
/*     */ 
/* 463 */     this.rawCertInfo = null;
/* 464 */     String str = localX509AttributeName.getSuffix();
/*     */     
/* 466 */     switch (i) {
/*     */     case 1: 
/* 468 */       if (str == null) {
/* 469 */         this.version = null;
/*     */       } else {
/* 471 */         this.version.delete(str);
/*     */       }
/* 473 */       break;
/*     */     case 2: 
/* 475 */       if (str == null) {
/* 476 */         this.serialNum = null;
/*     */       } else {
/* 478 */         this.serialNum.delete(str);
/*     */       }
/* 480 */       break;
/*     */     case 3: 
/* 482 */       if (str == null) {
/* 483 */         this.algId = null;
/*     */       } else {
/* 485 */         this.algId.delete(str);
/*     */       }
/* 487 */       break;
/*     */     case 4: 
/* 489 */       this.issuer = null;
/* 490 */       break;
/*     */     case 5: 
/* 492 */       if (str == null) {
/* 493 */         this.interval = null;
/*     */       } else {
/* 495 */         this.interval.delete(str);
/*     */       }
/* 497 */       break;
/*     */     case 6: 
/* 499 */       this.subject = null;
/* 500 */       break;
/*     */     case 7: 
/* 502 */       if (str == null) {
/* 503 */         this.pubKey = null;
/*     */       } else {
/* 505 */         this.pubKey.delete(str);
/*     */       }
/* 507 */       break;
/*     */     case 8: 
/* 509 */       this.issuerUniqueId = null;
/* 510 */       break;
/*     */     case 9: 
/* 512 */       this.subjectUniqueId = null;
/* 513 */       break;
/*     */     case 10: 
/* 515 */       if (str == null) {
/* 516 */         this.extensions = null;
/*     */       }
/* 518 */       else if (this.extensions != null) {
/* 519 */         this.extensions.delete(str);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       break;
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object get(String paramString)
/*     */     throws CertificateException, IOException
/*     */   {
/* 535 */     X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
/*     */     
/* 537 */     int i = attributeMap(localX509AttributeName.getPrefix());
/* 538 */     if (i == 0) {
/* 539 */       throw new CertificateParsingException("Attribute name not recognized: " + paramString);
/*     */     }
/*     */     
/* 542 */     String str = localX509AttributeName.getSuffix();
/*     */     
/* 544 */     switch (i) {
/*     */     case 10: 
/* 546 */       if (str == null) {
/* 547 */         return this.extensions;
/*     */       }
/* 549 */       if (this.extensions == null) {
/* 550 */         return null;
/*     */       }
/* 552 */       return this.extensions.get(str);
/*     */     
/*     */ 
/*     */     case 6: 
/* 556 */       if (str == null) {
/* 557 */         return this.subject;
/*     */       }
/* 559 */       return getX500Name(str, false);
/*     */     
/*     */     case 4: 
/* 562 */       if (str == null) {
/* 563 */         return this.issuer;
/*     */       }
/* 565 */       return getX500Name(str, true);
/*     */     
/*     */     case 7: 
/* 568 */       if (str == null) {
/* 569 */         return this.pubKey;
/*     */       }
/* 571 */       return this.pubKey.get(str);
/*     */     
/*     */     case 3: 
/* 574 */       if (str == null) {
/* 575 */         return this.algId;
/*     */       }
/* 577 */       return this.algId.get(str);
/*     */     
/*     */     case 5: 
/* 580 */       if (str == null) {
/* 581 */         return this.interval;
/*     */       }
/* 583 */       return this.interval.get(str);
/*     */     
/*     */     case 1: 
/* 586 */       if (str == null) {
/* 587 */         return this.version;
/*     */       }
/* 589 */       return this.version.get(str);
/*     */     
/*     */     case 2: 
/* 592 */       if (str == null) {
/* 593 */         return this.serialNum;
/*     */       }
/* 595 */       return this.serialNum.get(str);
/*     */     
/*     */     case 8: 
/* 598 */       return this.issuerUniqueId;
/*     */     case 9: 
/* 600 */       return this.subjectUniqueId;
/*     */     }
/* 602 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object getX500Name(String paramString, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 610 */     if (paramString.equalsIgnoreCase("dname"))
/* 611 */       return paramBoolean ? this.issuer : this.subject;
/* 612 */     if (paramString.equalsIgnoreCase("x500principal")) {
/* 613 */       return paramBoolean ? this.issuer.asX500Principal() : this.subject
/* 614 */         .asX500Principal();
/*     */     }
/* 616 */     throw new IOException("Attribute name not recognized.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void parse(DerValue paramDerValue)
/*     */     throws CertificateParsingException, IOException
/*     */   {
/* 628 */     if (paramDerValue.tag != 48) {
/* 629 */       throw new CertificateParsingException("signed fields invalid");
/*     */     }
/* 631 */     this.rawCertInfo = paramDerValue.toByteArray();
/*     */     
/* 633 */     DerInputStream localDerInputStream = paramDerValue.data;
/*     */     
/*     */ 
/* 636 */     DerValue localDerValue = localDerInputStream.getDerValue();
/* 637 */     if (localDerValue.isContextSpecific((byte)0)) {
/* 638 */       this.version = new CertificateVersion(localDerValue);
/* 639 */       localDerValue = localDerInputStream.getDerValue();
/*     */     }
/*     */     
/*     */ 
/* 643 */     this.serialNum = new CertificateSerialNumber(localDerValue);
/*     */     
/*     */ 
/* 646 */     this.algId = new CertificateAlgorithmId(localDerInputStream);
/*     */     
/*     */ 
/* 649 */     this.issuer = new X500Name(localDerInputStream);
/* 650 */     if (this.issuer.isEmpty()) {
/* 651 */       throw new CertificateParsingException("Empty issuer DN not allowed in X509Certificates");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 656 */     this.interval = new CertificateValidity(localDerInputStream);
/*     */     
/*     */ 
/* 659 */     this.subject = new X500Name(localDerInputStream);
/* 660 */     if ((this.version.compare(0) == 0) && 
/* 661 */       (this.subject.isEmpty())) {
/* 662 */       throw new CertificateParsingException("Empty subject DN not allowed in v1 certificate");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 667 */     this.pubKey = new CertificateX509Key(localDerInputStream);
/*     */     
/*     */ 
/* 670 */     if (localDerInputStream.available() != 0) {
/* 671 */       if (this.version.compare(0) == 0) {
/* 672 */         throw new CertificateParsingException("no more data allowed for version 1 certificate");
/*     */       }
/*     */     }
/*     */     else {
/* 676 */       return;
/*     */     }
/*     */     
/*     */ 
/* 680 */     localDerValue = localDerInputStream.getDerValue();
/* 681 */     if (localDerValue.isContextSpecific((byte)1)) {
/* 682 */       this.issuerUniqueId = new UniqueIdentity(localDerValue);
/* 683 */       if (localDerInputStream.available() == 0)
/* 684 */         return;
/* 685 */       localDerValue = localDerInputStream.getDerValue();
/*     */     }
/*     */     
/*     */ 
/* 689 */     if (localDerValue.isContextSpecific((byte)2)) {
/* 690 */       this.subjectUniqueId = new UniqueIdentity(localDerValue);
/* 691 */       if (localDerInputStream.available() == 0)
/* 692 */         return;
/* 693 */       localDerValue = localDerInputStream.getDerValue();
/*     */     }
/*     */     
/*     */ 
/* 697 */     if (this.version.compare(2) != 0) {
/* 698 */       throw new CertificateParsingException("Extensions not allowed in v2 certificate");
/*     */     }
/*     */     
/* 701 */     if ((localDerValue.isConstructed()) && (localDerValue.isContextSpecific((byte)3))) {
/* 702 */       this.extensions = new CertificateExtensions(localDerValue.data);
/*     */     }
/*     */     
/*     */ 
/* 706 */     verifyCert(this.subject, this.extensions);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void verifyCert(X500Name paramX500Name, CertificateExtensions paramCertificateExtensions)
/*     */     throws CertificateParsingException, IOException
/*     */   {
/* 718 */     if (paramX500Name.isEmpty()) {
/* 719 */       if (paramCertificateExtensions == null) {
/* 720 */         throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and certificate has no extensions");
/*     */       }
/*     */       
/*     */ 
/* 724 */       SubjectAlternativeNameExtension localSubjectAlternativeNameExtension = null;
/* 725 */       Object localObject = null;
/* 726 */       GeneralNames localGeneralNames = null;
/*     */       try
/*     */       {
/* 729 */         localSubjectAlternativeNameExtension = (SubjectAlternativeNameExtension)paramCertificateExtensions.get("SubjectAlternativeName");
/* 730 */         localGeneralNames = localSubjectAlternativeNameExtension.get("subject_name");
/*     */       }
/*     */       catch (IOException localIOException) {
/* 733 */         throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and SubjectAlternativeName extension is absent");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 739 */       if ((localGeneralNames == null) || (localGeneralNames.isEmpty())) {
/* 740 */         throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and SubjectAlternativeName extension is empty");
/*     */       }
/*     */       
/* 743 */       if (!localSubjectAlternativeNameExtension.isCritical()) {
/* 744 */         throw new CertificateParsingException("X.509 Certificate is incomplete: SubjectAlternativeName extension MUST be marked critical when subject field is empty");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void emit(DerOutputStream paramDerOutputStream)
/*     */     throws CertificateException, IOException
/*     */   {
/* 756 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */     
/*     */ 
/* 759 */     this.version.encode(localDerOutputStream);
/*     */     
/*     */ 
/*     */ 
/* 763 */     this.serialNum.encode(localDerOutputStream);
/* 764 */     this.algId.encode(localDerOutputStream);
/*     */     
/* 766 */     if ((this.version.compare(0) == 0) && 
/* 767 */       (this.issuer.toString() == null)) {
/* 768 */       throw new CertificateParsingException("Null issuer DN not allowed in v1 certificate");
/*     */     }
/*     */     
/* 771 */     this.issuer.encode(localDerOutputStream);
/* 772 */     this.interval.encode(localDerOutputStream);
/*     */     
/*     */ 
/* 775 */     if ((this.version.compare(0) == 0) && 
/* 776 */       (this.subject.toString() == null)) {
/* 777 */       throw new CertificateParsingException("Null subject DN not allowed in v1 certificate");
/*     */     }
/* 779 */     this.subject.encode(localDerOutputStream);
/* 780 */     this.pubKey.encode(localDerOutputStream);
/*     */     
/*     */ 
/* 783 */     if (this.issuerUniqueId != null) {
/* 784 */       this.issuerUniqueId.encode(localDerOutputStream, DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1));
/*     */     }
/*     */     
/* 787 */     if (this.subjectUniqueId != null) {
/* 788 */       this.subjectUniqueId.encode(localDerOutputStream, DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)2));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 793 */     if (this.extensions != null) {
/* 794 */       this.extensions.encode(localDerOutputStream);
/*     */     }
/*     */     
/*     */ 
/* 798 */     paramDerOutputStream.write((byte)48, localDerOutputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int attributeMap(String paramString)
/*     */   {
/* 805 */     Integer localInteger = (Integer)map.get(paramString);
/* 806 */     if (localInteger == null) {
/* 807 */       return 0;
/*     */     }
/* 809 */     return localInteger.intValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setVersion(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 819 */     if (!(paramObject instanceof CertificateVersion)) {
/* 820 */       throw new CertificateException("Version class type invalid.");
/*     */     }
/* 822 */     this.version = ((CertificateVersion)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setSerialNumber(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 832 */     if (!(paramObject instanceof CertificateSerialNumber)) {
/* 833 */       throw new CertificateException("SerialNumber class type invalid.");
/*     */     }
/* 835 */     this.serialNum = ((CertificateSerialNumber)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setAlgorithmId(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 845 */     if (!(paramObject instanceof CertificateAlgorithmId)) {
/* 846 */       throw new CertificateException("AlgorithmId class type invalid.");
/*     */     }
/*     */     
/* 849 */     this.algId = ((CertificateAlgorithmId)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setIssuer(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 859 */     if (!(paramObject instanceof X500Name)) {
/* 860 */       throw new CertificateException("Issuer class type invalid.");
/*     */     }
/*     */     
/* 863 */     this.issuer = ((X500Name)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setValidity(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 873 */     if (!(paramObject instanceof CertificateValidity)) {
/* 874 */       throw new CertificateException("CertificateValidity class type invalid.");
/*     */     }
/*     */     
/* 877 */     this.interval = ((CertificateValidity)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setSubject(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 887 */     if (!(paramObject instanceof X500Name)) {
/* 888 */       throw new CertificateException("Subject class type invalid.");
/*     */     }
/*     */     
/* 891 */     this.subject = ((X500Name)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setKey(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 901 */     if (!(paramObject instanceof CertificateX509Key)) {
/* 902 */       throw new CertificateException("Key class type invalid.");
/*     */     }
/*     */     
/* 905 */     this.pubKey = ((CertificateX509Key)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setIssuerUniqueId(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 915 */     if (this.version.compare(1) < 0) {
/* 916 */       throw new CertificateException("Invalid version");
/*     */     }
/* 918 */     if (!(paramObject instanceof UniqueIdentity)) {
/* 919 */       throw new CertificateException("IssuerUniqueId class type invalid.");
/*     */     }
/*     */     
/* 922 */     this.issuerUniqueId = ((UniqueIdentity)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setSubjectUniqueId(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 932 */     if (this.version.compare(1) < 0) {
/* 933 */       throw new CertificateException("Invalid version");
/*     */     }
/* 935 */     if (!(paramObject instanceof UniqueIdentity)) {
/* 936 */       throw new CertificateException("SubjectUniqueId class type invalid.");
/*     */     }
/*     */     
/* 939 */     this.subjectUniqueId = ((UniqueIdentity)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setExtensions(Object paramObject)
/*     */     throws CertificateException
/*     */   {
/* 949 */     if (this.version.compare(2) < 0) {
/* 950 */       throw new CertificateException("Invalid version");
/*     */     }
/* 952 */     if (!(paramObject instanceof CertificateExtensions)) {
/* 953 */       throw new CertificateException("Extensions class type invalid.");
/*     */     }
/*     */     
/* 956 */     this.extensions = ((CertificateExtensions)paramObject);
/*     */   }
/*     */   
/*     */   public X509CertInfo() {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\X509CertInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.net.util.IPAddressUtil;
/*     */ import sun.security.pkcs.PKCS9Attribute;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NameConstraintsExtension
/*     */   extends Extension
/*     */   implements CertAttrSet<String>, Cloneable
/*     */ {
/*     */   public static final String IDENT = "x509.info.extensions.NameConstraints";
/*     */   public static final String NAME = "NameConstraints";
/*     */   public static final String PERMITTED_SUBTREES = "permitted_subtrees";
/*     */   public static final String EXCLUDED_SUBTREES = "excluded_subtrees";
/*     */   private static final byte TAG_PERMITTED = 0;
/*     */   private static final byte TAG_EXCLUDED = 1;
/*  82 */   private GeneralSubtrees permitted = null;
/*  83 */   private GeneralSubtrees excluded = null;
/*     */   
/*     */   private boolean hasMin;
/*     */   private boolean hasMax;
/*  87 */   private boolean minMaxValid = false;
/*     */   
/*     */   private void calcMinMax() throws IOException
/*     */   {
/*  91 */     this.hasMin = false;
/*  92 */     this.hasMax = false;
/*  93 */     int i; GeneralSubtree localGeneralSubtree; if (this.excluded != null) {
/*  94 */       for (i = 0; i < this.excluded.size(); i++) {
/*  95 */         localGeneralSubtree = this.excluded.get(i);
/*  96 */         if (localGeneralSubtree.getMinimum() != 0)
/*  97 */           this.hasMin = true;
/*  98 */         if (localGeneralSubtree.getMaximum() != -1) {
/*  99 */           this.hasMax = true;
/*     */         }
/*     */       }
/*     */     }
/* 103 */     if (this.permitted != null) {
/* 104 */       for (i = 0; i < this.permitted.size(); i++) {
/* 105 */         localGeneralSubtree = this.permitted.get(i);
/* 106 */         if (localGeneralSubtree.getMinimum() != 0)
/* 107 */           this.hasMin = true;
/* 108 */         if (localGeneralSubtree.getMaximum() != -1)
/* 109 */           this.hasMax = true;
/*     */       }
/*     */     }
/* 112 */     this.minMaxValid = true;
/*     */   }
/*     */   
/*     */   private void encodeThis() throws IOException
/*     */   {
/* 117 */     this.minMaxValid = false;
/* 118 */     if ((this.permitted == null) && (this.excluded == null)) {
/* 119 */       this.extensionValue = null;
/* 120 */       return;
/*     */     }
/* 122 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*     */     
/* 124 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 125 */     DerOutputStream localDerOutputStream3; if (this.permitted != null) {
/* 126 */       localDerOutputStream3 = new DerOutputStream();
/* 127 */       this.permitted.encode(localDerOutputStream3);
/* 128 */       localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream3);
/*     */     }
/*     */     
/* 131 */     if (this.excluded != null) {
/* 132 */       localDerOutputStream3 = new DerOutputStream();
/* 133 */       this.excluded.encode(localDerOutputStream3);
/* 134 */       localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream3);
/*     */     }
/*     */     
/* 137 */     localDerOutputStream1.write((byte)48, localDerOutputStream2);
/* 138 */     this.extensionValue = localDerOutputStream1.toByteArray();
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
/*     */   public NameConstraintsExtension(GeneralSubtrees paramGeneralSubtrees1, GeneralSubtrees paramGeneralSubtrees2)
/*     */     throws IOException
/*     */   {
/* 152 */     this.permitted = paramGeneralSubtrees1;
/* 153 */     this.excluded = paramGeneralSubtrees2;
/*     */     
/* 155 */     this.extensionId = PKIXExtensions.NameConstraints_Id;
/* 156 */     this.critical = true;
/* 157 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NameConstraintsExtension(Boolean paramBoolean, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 170 */     this.extensionId = PKIXExtensions.NameConstraints_Id;
/* 171 */     this.critical = paramBoolean.booleanValue();
/*     */     
/* 173 */     this.extensionValue = ((byte[])paramObject);
/* 174 */     DerValue localDerValue1 = new DerValue(this.extensionValue);
/* 175 */     if (localDerValue1.tag != 48) {
/* 176 */       throw new IOException("Invalid encoding for NameConstraintsExtension.");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 186 */     if (localDerValue1.data == null)
/* 187 */       return;
/* 188 */     while (localDerValue1.data.available() != 0) {
/* 189 */       DerValue localDerValue2 = localDerValue1.data.getDerValue();
/*     */       
/* 191 */       if ((localDerValue2.isContextSpecific((byte)0)) && (localDerValue2.isConstructed())) {
/* 192 */         if (this.permitted != null) {
/* 193 */           throw new IOException("Duplicate permitted GeneralSubtrees in NameConstraintsExtension.");
/*     */         }
/*     */         
/* 196 */         localDerValue2.resetTag((byte)48);
/* 197 */         this.permitted = new GeneralSubtrees(localDerValue2);
/*     */       }
/* 199 */       else if ((localDerValue2.isContextSpecific((byte)1)) && 
/* 200 */         (localDerValue2.isConstructed())) {
/* 201 */         if (this.excluded != null) {
/* 202 */           throw new IOException("Duplicate excluded GeneralSubtrees in NameConstraintsExtension.");
/*     */         }
/*     */         
/* 205 */         localDerValue2.resetTag((byte)48);
/* 206 */         this.excluded = new GeneralSubtrees(localDerValue2);
/*     */       } else {
/* 208 */         throw new IOException("Invalid encoding of NameConstraintsExtension.");
/*     */       }
/*     */     }
/* 211 */     this.minMaxValid = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 218 */     return 
/*     */     
/*     */ 
/*     */ 
/* 222 */       super.toString() + "NameConstraints: [" + (this.permitted == null ? "" : new StringBuilder().append("\n    Permitted:").append(this.permitted.toString()).toString()) + (this.excluded == null ? "" : new StringBuilder().append("\n    Excluded:").append(this.excluded.toString()).toString()) + "   ]\n";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 233 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 234 */     if (this.extensionValue == null) {
/* 235 */       this.extensionId = PKIXExtensions.NameConstraints_Id;
/* 236 */       this.critical = true;
/* 237 */       encodeThis();
/*     */     }
/* 239 */     super.encode(localDerOutputStream);
/* 240 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 247 */     if (paramString.equalsIgnoreCase("permitted_subtrees")) {
/* 248 */       if (!(paramObject instanceof GeneralSubtrees)) {
/* 249 */         throw new IOException("Attribute value should be of type GeneralSubtrees.");
/*     */       }
/*     */       
/* 252 */       this.permitted = ((GeneralSubtrees)paramObject);
/* 253 */     } else if (paramString.equalsIgnoreCase("excluded_subtrees")) {
/* 254 */       if (!(paramObject instanceof GeneralSubtrees)) {
/* 255 */         throw new IOException("Attribute value should be of type GeneralSubtrees.");
/*     */       }
/*     */       
/* 258 */       this.excluded = ((GeneralSubtrees)paramObject);
/*     */     } else {
/* 260 */       throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
/*     */     }
/*     */     
/* 263 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */   public GeneralSubtrees get(String paramString)
/*     */     throws IOException
/*     */   {
/* 270 */     if (paramString.equalsIgnoreCase("permitted_subtrees"))
/* 271 */       return this.permitted;
/* 272 */     if (paramString.equalsIgnoreCase("excluded_subtrees")) {
/* 273 */       return this.excluded;
/*     */     }
/* 275 */     throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 284 */     if (paramString.equalsIgnoreCase("permitted_subtrees")) {
/* 285 */       this.permitted = null;
/* 286 */     } else if (paramString.equalsIgnoreCase("excluded_subtrees")) {
/* 287 */       this.excluded = null;
/*     */     } else {
/* 289 */       throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
/*     */     }
/*     */     
/* 292 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 300 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 301 */     localAttributeNameEnumeration.addElement("permitted_subtrees");
/* 302 */     localAttributeNameEnumeration.addElement("excluded_subtrees");
/*     */     
/* 304 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 311 */     return "NameConstraints";
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
/*     */   public void merge(NameConstraintsExtension paramNameConstraintsExtension)
/*     */     throws IOException
/*     */   {
/* 339 */     if (paramNameConstraintsExtension == null)
/*     */     {
/* 341 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 350 */     GeneralSubtrees localGeneralSubtrees1 = paramNameConstraintsExtension.get("excluded_subtrees");
/* 351 */     if (this.excluded == null)
/*     */     {
/* 353 */       this.excluded = (localGeneralSubtrees1 != null ? (GeneralSubtrees)localGeneralSubtrees1.clone() : null);
/*     */     }
/* 355 */     else if (localGeneralSubtrees1 != null)
/*     */     {
/* 357 */       this.excluded.union(localGeneralSubtrees1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 367 */     GeneralSubtrees localGeneralSubtrees2 = paramNameConstraintsExtension.get("permitted_subtrees");
/* 368 */     if (this.permitted == null)
/*     */     {
/* 370 */       this.permitted = (localGeneralSubtrees2 != null ? (GeneralSubtrees)localGeneralSubtrees2.clone() : null);
/*     */     }
/* 372 */     else if (localGeneralSubtrees2 != null)
/*     */     {
/* 374 */       localGeneralSubtrees1 = this.permitted.intersect(localGeneralSubtrees2);
/*     */       
/*     */ 
/* 377 */       if (localGeneralSubtrees1 != null) {
/* 378 */         if (this.excluded != null) {
/* 379 */           this.excluded.union(localGeneralSubtrees1);
/*     */         } else {
/* 381 */           this.excluded = ((GeneralSubtrees)localGeneralSubtrees1.clone());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 391 */     if (this.permitted != null) {
/* 392 */       this.permitted.reduce(this.excluded);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 397 */     encodeThis();
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
/*     */   public boolean verify(X509Certificate paramX509Certificate)
/*     */     throws IOException
/*     */   {
/* 415 */     if (paramX509Certificate == null) {
/* 416 */       throw new IOException("Certificate is null");
/*     */     }
/*     */     
/*     */ 
/* 420 */     if (!this.minMaxValid) {
/* 421 */       calcMinMax();
/*     */     }
/*     */     
/* 424 */     if (this.hasMin) {
/* 425 */       throw new IOException("Non-zero minimum BaseDistance in name constraints not supported");
/*     */     }
/*     */     
/*     */ 
/* 429 */     if (this.hasMax) {
/* 430 */       throw new IOException("Maximum BaseDistance in name constraints not supported");
/*     */     }
/*     */     
/*     */ 
/* 434 */     X500Principal localX500Principal = paramX509Certificate.getSubjectX500Principal();
/* 435 */     X500Name localX500Name = X500Name.asX500Name(localX500Principal);
/*     */     
/*     */ 
/* 438 */     if ((!localX500Name.isEmpty()) && 
/* 439 */       (!verify(localX500Name))) {
/* 440 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 444 */     GeneralNames localGeneralNames = null;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 449 */       X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
/*     */       
/* 451 */       localObject2 = localX509CertImpl.getSubjectAlternativeNameExtension();
/* 452 */       if (localObject2 != null)
/*     */       {
/*     */ 
/* 455 */         localGeneralNames = ((SubjectAlternativeNameExtension)localObject2).get("subject_name");
/*     */       }
/*     */     }
/*     */     catch (CertificateException localCertificateException)
/*     */     {
/* 460 */       throw new IOException("Unable to extract extensions from certificate: " + localCertificateException.getMessage());
/*     */     }
/*     */     
/* 463 */     if (localGeneralNames == null) {
/* 464 */       localGeneralNames = new GeneralNames();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 471 */       for (localObject1 = localX500Name.allAvas().iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (AVA)((Iterator)localObject1).next();
/* 472 */         ObjectIdentifier localObjectIdentifier = ((AVA)localObject2).getObjectIdentifier();
/* 473 */         if (localObjectIdentifier.equals(PKCS9Attribute.EMAIL_ADDRESS_OID)) {
/* 474 */           localObject3 = ((AVA)localObject2).getValueString();
/* 475 */           if (localObject3 != null) {
/*     */             try {
/* 477 */               localGeneralNames.add(new GeneralName(new RFC822Name((String)localObject3)));
/*     */             }
/*     */             catch (IOException localIOException2) {}
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     Object localObject3;
/*     */     
/*     */ 
/* 490 */     Object localObject1 = localX500Name.findMostSpecificAttribute(X500Name.commonName_oid);
/* 491 */     Object localObject2 = localObject1 == null ? null : ((DerValue)localObject1).getAsString();
/*     */     
/* 493 */     if (localObject2 != null) {
/*     */       try {
/* 495 */         if ((IPAddressUtil.isIPv4LiteralAddress((String)localObject2)) || 
/* 496 */           (IPAddressUtil.isIPv6LiteralAddress((String)localObject2))) {
/* 497 */           if (!hasNameType(localGeneralNames, 7)) {
/* 498 */             localGeneralNames.add(new GeneralName(new IPAddressName((String)localObject2)));
/*     */           }
/*     */         }
/* 501 */         else if (!hasNameType(localGeneralNames, 2)) {
/* 502 */           localGeneralNames.add(new GeneralName(new DNSName((String)localObject2)));
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException1) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 511 */     for (int i = 0; i < localGeneralNames.size(); i++) {
/* 512 */       localObject3 = localGeneralNames.get(i).getName();
/* 513 */       if (!verify((GeneralNameInterface)localObject3)) {
/* 514 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 519 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean hasNameType(GeneralNames paramGeneralNames, int paramInt) {
/* 523 */     for (GeneralName localGeneralName : paramGeneralNames.names()) {
/* 524 */       if (localGeneralName.getType() == paramInt) {
/* 525 */         return true;
/*     */       }
/*     */     }
/* 528 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean verify(GeneralNameInterface paramGeneralNameInterface)
/*     */     throws IOException
/*     */   {
/* 541 */     if (paramGeneralNameInterface == null)
/* 542 */       throw new IOException("name is null");
/*     */     int i;
/*     */     Object localObject1;
/*     */     Object localObject2;
/* 546 */     if ((this.excluded != null) && (this.excluded.size() > 0))
/*     */     {
/* 548 */       for (i = 0; i < this.excluded.size(); i++) {
/* 549 */         GeneralSubtree localGeneralSubtree = this.excluded.get(i);
/* 550 */         if (localGeneralSubtree != null)
/*     */         {
/* 552 */           localObject1 = localGeneralSubtree.getName();
/* 553 */           if (localObject1 != null)
/*     */           {
/* 555 */             localObject2 = ((GeneralName)localObject1).getName();
/* 556 */             if (localObject2 != null)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 561 */               switch (((GeneralNameInterface)localObject2).constrains(paramGeneralNameInterface)) {
/*     */               case -1: 
/*     */               case 2: 
/*     */               case 3: 
/*     */                 break;
/*     */               case 0: 
/*     */               case 1: 
/* 568 */                 return false;
/*     */               } }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 574 */     if ((this.permitted != null) && (this.permitted.size() > 0))
/*     */     {
/* 576 */       i = 0;
/*     */       
/* 578 */       for (int j = 0; j < this.permitted.size(); j++) {
/* 579 */         localObject1 = this.permitted.get(j);
/* 580 */         if (localObject1 != null)
/*     */         {
/* 582 */           localObject2 = ((GeneralSubtree)localObject1).getName();
/* 583 */           if (localObject2 != null)
/*     */           {
/* 585 */             GeneralNameInterface localGeneralNameInterface = ((GeneralName)localObject2).getName();
/* 586 */             if (localGeneralNameInterface != null)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 592 */               switch (localGeneralNameInterface.constrains(paramGeneralNameInterface)) {
/*     */               case -1: 
/*     */                 break;
/*     */               case 2: 
/*     */               case 3: 
/* 597 */                 i = 1;
/* 598 */                 break;
/*     */               
/*     */               case 0: 
/*     */               case 1: 
/* 602 */                 return true; } }
/*     */           }
/*     */         } }
/* 605 */       if (i != 0) {
/* 606 */         return false;
/*     */       }
/*     */     }
/* 609 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 618 */       NameConstraintsExtension localNameConstraintsExtension = (NameConstraintsExtension)super.clone();
/*     */       
/* 620 */       if (this.permitted != null) {
/* 621 */         localNameConstraintsExtension.permitted = ((GeneralSubtrees)this.permitted.clone());
/*     */       }
/* 623 */       if (this.excluded != null) {
/* 624 */         localNameConstraintsExtension.excluded = ((GeneralSubtrees)this.excluded.clone());
/*     */       }
/* 626 */       return localNameConstraintsExtension;
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 628 */       throw new RuntimeException("CloneNotSupportedException while cloning NameConstraintsException. This should never happen.");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\NameConstraintsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
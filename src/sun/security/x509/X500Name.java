/*      */ package sun.security.x509;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.security.AccessController;
/*      */ import java.security.Principal;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import javax.security.auth.x500.X500Principal;
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
/*      */ public class X500Name
/*      */   implements GeneralNameInterface, Principal
/*      */ {
/*      */   private String dn;
/*      */   private String rfc1779Dn;
/*      */   private String rfc2253Dn;
/*      */   private String canonicalDn;
/*      */   private RDN[] names;
/*      */   private X500Principal x500Principal;
/*      */   private byte[] encoded;
/*      */   private volatile List<RDN> rdnList;
/*      */   private volatile List<AVA> allAvaList;
/*      */   
/*      */   public X500Name(String paramString)
/*      */     throws IOException
/*      */   {
/*  150 */     this(paramString, Collections.emptyMap());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X500Name(String paramString, Map<String, String> paramMap)
/*      */     throws IOException
/*      */   {
/*  163 */     parseDN(paramString, paramMap);
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
/*      */   public X500Name(String paramString1, String paramString2)
/*      */     throws IOException
/*      */   {
/*  177 */     if (paramString1 == null) {
/*  178 */       throw new NullPointerException("Name must not be null");
/*      */     }
/*  180 */     if (paramString2.equalsIgnoreCase("RFC2253")) {
/*  181 */       parseRFC2253DN(paramString1);
/*  182 */     } else if (paramString2.equalsIgnoreCase("DEFAULT")) {
/*  183 */       parseDN(paramString1, Collections.emptyMap());
/*      */     } else {
/*  185 */       throw new IOException("Unsupported format " + paramString2);
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
/*      */   public X500Name(String paramString1, String paramString2, String paramString3, String paramString4)
/*      */     throws IOException
/*      */   {
/*  205 */     this.names = new RDN[4];
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  210 */     this.names[3] = new RDN(1);
/*  211 */     this.names[3].assertion[0] = new AVA(commonName_oid, new DerValue(paramString1));
/*      */     
/*  213 */     this.names[2] = new RDN(1);
/*  214 */     this.names[2].assertion[0] = new AVA(orgUnitName_oid, new DerValue(paramString2));
/*      */     
/*  216 */     this.names[1] = new RDN(1);
/*  217 */     this.names[1].assertion[0] = new AVA(orgName_oid, new DerValue(paramString3));
/*      */     
/*  219 */     this.names[0] = new RDN(1);
/*  220 */     this.names[0].assertion[0] = new AVA(countryName_oid, new DerValue(paramString4));
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
/*      */   public X500Name(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
/*      */     throws IOException
/*      */   {
/*  243 */     this.names = new RDN[6];
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  248 */     this.names[5] = new RDN(1);
/*  249 */     this.names[5].assertion[0] = new AVA(commonName_oid, new DerValue(paramString1));
/*      */     
/*  251 */     this.names[4] = new RDN(1);
/*  252 */     this.names[4].assertion[0] = new AVA(orgUnitName_oid, new DerValue(paramString2));
/*      */     
/*  254 */     this.names[3] = new RDN(1);
/*  255 */     this.names[3].assertion[0] = new AVA(orgName_oid, new DerValue(paramString3));
/*      */     
/*  257 */     this.names[2] = new RDN(1);
/*  258 */     this.names[2].assertion[0] = new AVA(localityName_oid, new DerValue(paramString4));
/*      */     
/*  260 */     this.names[1] = new RDN(1);
/*  261 */     this.names[1].assertion[0] = new AVA(stateName_oid, new DerValue(paramString5));
/*      */     
/*  263 */     this.names[0] = new RDN(1);
/*  264 */     this.names[0].assertion[0] = new AVA(countryName_oid, new DerValue(paramString6));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X500Name(RDN[] paramArrayOfRDN)
/*      */     throws IOException
/*      */   {
/*  275 */     if (paramArrayOfRDN == null) {
/*  276 */       this.names = new RDN[0];
/*      */     } else {
/*  278 */       this.names = ((RDN[])paramArrayOfRDN.clone());
/*  279 */       for (int i = 0; i < this.names.length; i++) {
/*  280 */         if (this.names[i] == null) {
/*  281 */           throw new IOException("Cannot create an X500Name");
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
/*      */   public X500Name(DerValue paramDerValue)
/*      */     throws IOException
/*      */   {
/*  296 */     this(paramDerValue.toDerInputStream());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X500Name(DerInputStream paramDerInputStream)
/*      */     throws IOException
/*      */   {
/*  306 */     parseDER(paramDerInputStream);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public X500Name(byte[] paramArrayOfByte)
/*      */     throws IOException
/*      */   {
/*  315 */     DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte);
/*  316 */     parseDER(localDerInputStream);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<RDN> rdns()
/*      */   {
/*  323 */     List localList = this.rdnList;
/*  324 */     if (localList == null) {
/*  325 */       localList = Collections.unmodifiableList(Arrays.asList(this.names));
/*  326 */       this.rdnList = localList;
/*      */     }
/*  328 */     return localList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int size()
/*      */   {
/*  335 */     return this.names.length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<AVA> allAvas()
/*      */   {
/*  343 */     Object localObject = this.allAvaList;
/*  344 */     if (localObject == null) {
/*  345 */       localObject = new ArrayList();
/*  346 */       for (int i = 0; i < this.names.length; i++) {
/*  347 */         ((List)localObject).addAll(this.names[i].avas());
/*      */       }
/*  349 */       localObject = Collections.unmodifiableList((List)localObject);
/*  350 */       this.allAvaList = ((List)localObject);
/*      */     }
/*  352 */     return (List<AVA>)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int avaSize()
/*      */   {
/*  360 */     return allAvas().size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/*  368 */     int i = this.names.length;
/*  369 */     for (int j = 0; j < i; j++) {
/*  370 */       if (this.names[j].assertion.length != 0) {
/*  371 */         return false;
/*      */       }
/*      */     }
/*  374 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  382 */     return getRFC2253CanonicalName().hashCode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  391 */     if (this == paramObject) {
/*  392 */       return true;
/*      */     }
/*  394 */     if (!(paramObject instanceof X500Name)) {
/*  395 */       return false;
/*      */     }
/*  397 */     X500Name localX500Name = (X500Name)paramObject;
/*      */     
/*  399 */     if ((this.canonicalDn != null) && (localX500Name.canonicalDn != null)) {
/*  400 */       return this.canonicalDn.equals(localX500Name.canonicalDn);
/*      */     }
/*      */     
/*  403 */     int i = this.names.length;
/*  404 */     if (i != localX500Name.names.length) {
/*  405 */       return false;
/*      */     }
/*  407 */     for (int j = 0; j < i; j++) {
/*  408 */       localObject = this.names[j];
/*  409 */       RDN localRDN = localX500Name.names[j];
/*  410 */       if (((RDN)localObject).assertion.length != localRDN.assertion.length) {
/*  411 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  415 */     String str = getRFC2253CanonicalName();
/*  416 */     Object localObject = localX500Name.getRFC2253CanonicalName();
/*  417 */     return str.equals(localObject);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getString(DerValue paramDerValue)
/*      */     throws IOException
/*      */   {
/*  425 */     if (paramDerValue == null)
/*  426 */       return null;
/*  427 */     String str = paramDerValue.getAsString();
/*      */     
/*  429 */     if (str == null) {
/*  430 */       throw new IOException("not a DER string encoding, " + paramDerValue.tag);
/*      */     }
/*      */     
/*  433 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getType()
/*      */   {
/*  440 */     return 4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getCountry()
/*      */     throws IOException
/*      */   {
/*  450 */     DerValue localDerValue = findAttribute(countryName_oid);
/*      */     
/*  452 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getOrganization()
/*      */     throws IOException
/*      */   {
/*  463 */     DerValue localDerValue = findAttribute(orgName_oid);
/*      */     
/*  465 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getOrganizationalUnit()
/*      */     throws IOException
/*      */   {
/*  476 */     DerValue localDerValue = findAttribute(orgUnitName_oid);
/*      */     
/*  478 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getCommonName()
/*      */     throws IOException
/*      */   {
/*  489 */     DerValue localDerValue = findAttribute(commonName_oid);
/*      */     
/*  491 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getLocality()
/*      */     throws IOException
/*      */   {
/*  502 */     DerValue localDerValue = findAttribute(localityName_oid);
/*      */     
/*  504 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getState()
/*      */     throws IOException
/*      */   {
/*  514 */     DerValue localDerValue = findAttribute(stateName_oid);
/*      */     
/*  516 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDomain()
/*      */     throws IOException
/*      */   {
/*  526 */     DerValue localDerValue = findAttribute(DOMAIN_COMPONENT_OID);
/*      */     
/*  528 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDNQualifier()
/*      */     throws IOException
/*      */   {
/*  538 */     DerValue localDerValue = findAttribute(DNQUALIFIER_OID);
/*      */     
/*  540 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getSurname()
/*      */     throws IOException
/*      */   {
/*  550 */     DerValue localDerValue = findAttribute(SURNAME_OID);
/*      */     
/*  552 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getGivenName()
/*      */     throws IOException
/*      */   {
/*  562 */     DerValue localDerValue = findAttribute(GIVENNAME_OID);
/*      */     
/*  564 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getInitials()
/*      */     throws IOException
/*      */   {
/*  574 */     DerValue localDerValue = findAttribute(INITIALS_OID);
/*      */     
/*  576 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getGeneration()
/*      */     throws IOException
/*      */   {
/*  586 */     DerValue localDerValue = findAttribute(GENERATIONQUALIFIER_OID);
/*      */     
/*  588 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getIP()
/*      */     throws IOException
/*      */   {
/*  598 */     DerValue localDerValue = findAttribute(ipAddress_oid);
/*      */     
/*  600 */     return getString(localDerValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  610 */     if (this.dn == null) {
/*  611 */       generateDN();
/*      */     }
/*  613 */     return this.dn;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getRFC1779Name()
/*      */   {
/*  622 */     return getRFC1779Name(Collections.emptyMap());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getRFC1779Name(Map<String, String> paramMap)
/*      */     throws IllegalArgumentException
/*      */   {
/*  633 */     if (paramMap.isEmpty())
/*      */     {
/*  635 */       if (this.rfc1779Dn != null) {
/*  636 */         return this.rfc1779Dn;
/*      */       }
/*  638 */       this.rfc1779Dn = generateRFC1779DN(paramMap);
/*  639 */       return this.rfc1779Dn;
/*      */     }
/*      */     
/*  642 */     return generateRFC1779DN(paramMap);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getRFC2253Name()
/*      */   {
/*  651 */     return getRFC2253Name(Collections.emptyMap());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getRFC2253Name(Map<String, String> paramMap)
/*      */   {
/*  662 */     if (paramMap.isEmpty()) {
/*  663 */       if (this.rfc2253Dn != null) {
/*  664 */         return this.rfc2253Dn;
/*      */       }
/*  666 */       this.rfc2253Dn = generateRFC2253DN(paramMap);
/*  667 */       return this.rfc2253Dn;
/*      */     }
/*      */     
/*  670 */     return generateRFC2253DN(paramMap);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String generateRFC2253DN(Map<String, String> paramMap)
/*      */   {
/*  678 */     if (this.names.length == 0) {
/*  679 */       return "";
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
/*  691 */     StringBuilder localStringBuilder = new StringBuilder(48);
/*  692 */     for (int i = this.names.length - 1; i >= 0; i--) {
/*  693 */       if (i < this.names.length - 1) {
/*  694 */         localStringBuilder.append(',');
/*      */       }
/*  696 */       localStringBuilder.append(this.names[i].toRFC2253String(paramMap));
/*      */     }
/*  698 */     return localStringBuilder.toString();
/*      */   }
/*      */   
/*      */   public String getRFC2253CanonicalName()
/*      */   {
/*  703 */     if (this.canonicalDn != null) {
/*  704 */       return this.canonicalDn;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  710 */     if (this.names.length == 0) {
/*  711 */       this.canonicalDn = "";
/*  712 */       return this.canonicalDn;
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
/*  724 */     StringBuilder localStringBuilder = new StringBuilder(48);
/*  725 */     for (int i = this.names.length - 1; i >= 0; i--) {
/*  726 */       if (i < this.names.length - 1) {
/*  727 */         localStringBuilder.append(',');
/*      */       }
/*  729 */       localStringBuilder.append(this.names[i].toRFC2253String(true));
/*      */     }
/*  731 */     this.canonicalDn = localStringBuilder.toString();
/*  732 */     return this.canonicalDn;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getName()
/*      */   {
/*  739 */     return toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private DerValue findAttribute(ObjectIdentifier paramObjectIdentifier)
/*      */   {
/*  746 */     if (this.names != null) {
/*  747 */       for (int i = 0; i < this.names.length; i++) {
/*  748 */         DerValue localDerValue = this.names[i].findAttribute(paramObjectIdentifier);
/*  749 */         if (localDerValue != null) {
/*  750 */           return localDerValue;
/*      */         }
/*      */       }
/*      */     }
/*  754 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DerValue findMostSpecificAttribute(ObjectIdentifier paramObjectIdentifier)
/*      */   {
/*  762 */     if (this.names != null) {
/*  763 */       for (int i = this.names.length - 1; i >= 0; i--) {
/*  764 */         DerValue localDerValue = this.names[i].findAttribute(paramObjectIdentifier);
/*  765 */         if (localDerValue != null) {
/*  766 */           return localDerValue;
/*      */         }
/*      */       }
/*      */     }
/*  770 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void parseDER(DerInputStream paramDerInputStream)
/*      */     throws IOException
/*      */   {
/*  781 */     DerValue[] arrayOfDerValue = null;
/*  782 */     byte[] arrayOfByte = paramDerInputStream.toByteArray();
/*      */     try
/*      */     {
/*  785 */       arrayOfDerValue = paramDerInputStream.getSequence(5);
/*      */     } catch (IOException localIOException) {
/*  787 */       if (arrayOfByte == null) {
/*  788 */         arrayOfDerValue = null;
/*      */       } else {
/*  790 */         DerValue localDerValue = new DerValue((byte)48, arrayOfByte);
/*      */         
/*  792 */         arrayOfByte = localDerValue.toByteArray();
/*  793 */         arrayOfDerValue = new DerInputStream(arrayOfByte).getSequence(5);
/*      */       }
/*      */     }
/*      */     
/*  797 */     if (arrayOfDerValue == null) {
/*  798 */       this.names = new RDN[0];
/*      */     } else {
/*  800 */       this.names = new RDN[arrayOfDerValue.length];
/*  801 */       for (int i = 0; i < arrayOfDerValue.length; i++) {
/*  802 */         this.names[i] = new RDN(arrayOfDerValue[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public void emit(DerOutputStream paramDerOutputStream)
/*      */     throws IOException
/*      */   {
/*  815 */     encode(paramDerOutputStream);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void encode(DerOutputStream paramDerOutputStream)
/*      */     throws IOException
/*      */   {
/*  824 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*  825 */     for (int i = 0; i < this.names.length; i++) {
/*  826 */       this.names[i].encode(localDerOutputStream);
/*      */     }
/*  828 */     paramDerOutputStream.write((byte)48, localDerOutputStream);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getEncodedInternal()
/*      */     throws IOException
/*      */   {
/*  837 */     if (this.encoded == null) {
/*  838 */       DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  839 */       DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*  840 */       for (int i = 0; i < this.names.length; i++) {
/*  841 */         this.names[i].encode(localDerOutputStream2);
/*      */       }
/*  843 */       localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*  844 */       this.encoded = localDerOutputStream1.toByteArray();
/*      */     }
/*  846 */     return this.encoded;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getEncoded()
/*      */     throws IOException
/*      */   {
/*  855 */     return (byte[])getEncodedInternal().clone();
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
/*      */   private void parseDN(String paramString, Map<String, String> paramMap)
/*      */     throws IOException
/*      */   {
/*  873 */     if ((paramString == null) || (paramString.length() == 0)) {
/*  874 */       this.names = new RDN[0];
/*  875 */       return;
/*      */     }
/*      */     
/*  878 */     ArrayList localArrayList = new ArrayList();
/*  879 */     int i = 0;
/*      */     
/*      */ 
/*  882 */     int k = 0;
/*      */     
/*  884 */     String str2 = paramString;
/*      */     
/*  886 */     int m = 0;
/*  887 */     int n = str2.indexOf(',');
/*  888 */     int i1 = str2.indexOf(';');
/*  889 */     while ((n >= 0) || (i1 >= 0)) {
/*      */       int j;
/*  891 */       if (i1 < 0) {
/*  892 */         j = n;
/*  893 */       } else if (n < 0) {
/*  894 */         j = i1;
/*      */       } else {
/*  896 */         j = Math.min(n, i1);
/*      */       }
/*  898 */       k += countQuotes(str2, m, j);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  907 */       if ((j >= 0) && (k != 1) && 
/*  908 */         (!escaped(j, m, str2)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  913 */         str1 = str2.substring(i, j);
/*      */         
/*      */ 
/*  916 */         localRDN = new RDN(str1, paramMap);
/*  917 */         localArrayList.add(localRDN);
/*      */         
/*      */ 
/*  920 */         i = j + 1;
/*      */         
/*      */ 
/*  923 */         k = 0;
/*      */       }
/*      */       
/*  926 */       m = j + 1;
/*  927 */       n = str2.indexOf(',', m);
/*  928 */       i1 = str2.indexOf(';', m);
/*      */     }
/*      */     
/*      */ 
/*  932 */     String str1 = str2.substring(i);
/*  933 */     RDN localRDN = new RDN(str1, paramMap);
/*  934 */     localArrayList.add(localRDN);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  940 */     Collections.reverse(localArrayList);
/*  941 */     this.names = ((RDN[])localArrayList.toArray(new RDN[localArrayList.size()]));
/*      */   }
/*      */   
/*      */   private void parseRFC2253DN(String paramString) throws IOException {
/*  945 */     if (paramString.length() == 0) {
/*  946 */       this.names = new RDN[0];
/*  947 */       return;
/*      */     }
/*      */     
/*  950 */     ArrayList localArrayList = new ArrayList();
/*  951 */     int i = 0;
/*      */     
/*  953 */     int j = 0;
/*  954 */     int k = paramString.indexOf(',');
/*  955 */     while (k >= 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  963 */       if ((k > 0) && (!escaped(k, j, paramString)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  968 */         str = paramString.substring(i, k);
/*      */         
/*      */ 
/*  971 */         localRDN = new RDN(str, "RFC2253");
/*  972 */         localArrayList.add(localRDN);
/*      */         
/*      */ 
/*  975 */         i = k + 1;
/*      */       }
/*      */       
/*  978 */       j = k + 1;
/*  979 */       k = paramString.indexOf(',', j);
/*      */     }
/*      */     
/*      */ 
/*  983 */     String str = paramString.substring(i);
/*  984 */     RDN localRDN = new RDN(str, "RFC2253");
/*  985 */     localArrayList.add(localRDN);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  991 */     Collections.reverse(localArrayList);
/*  992 */     this.names = ((RDN[])localArrayList.toArray(new RDN[localArrayList.size()]));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static int countQuotes(String paramString, int paramInt1, int paramInt2)
/*      */   {
/* 1000 */     int i = 0;
/*      */     
/* 1002 */     for (int j = paramInt1; j < paramInt2; j++) {
/* 1003 */       if (((paramString.charAt(j) == '"') && (j == paramInt1)) || (
/* 1004 */         (paramString.charAt(j) == '"') && (paramString.charAt(j - 1) != '\\'))) {
/* 1005 */         i++;
/*      */       }
/*      */     }
/*      */     
/* 1009 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */   private static boolean escaped(int paramInt1, int paramInt2, String paramString)
/*      */   {
/* 1015 */     if ((paramInt1 == 1) && (paramString.charAt(paramInt1 - 1) == '\\'))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1020 */       return true;
/*      */     }
/* 1022 */     if ((paramInt1 > 1) && (paramString.charAt(paramInt1 - 1) == '\\') && 
/* 1023 */       (paramString.charAt(paramInt1 - 2) != '\\'))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1028 */       return true;
/*      */     }
/* 1030 */     if ((paramInt1 > 1) && (paramString.charAt(paramInt1 - 1) == '\\') && 
/* 1031 */       (paramString.charAt(paramInt1 - 2) == '\\'))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1036 */       int i = 0;
/* 1037 */       paramInt1--;
/* 1038 */       while (paramInt1 >= paramInt2) {
/* 1039 */         if (paramString.charAt(paramInt1) == '\\') {
/* 1040 */           i++;
/*      */         }
/* 1042 */         paramInt1--;
/*      */       }
/*      */       
/*      */ 
/* 1046 */       return i % 2 != 0;
/*      */     }
/*      */     
/* 1049 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void generateDN()
/*      */   {
/* 1061 */     if (this.names.length == 1) {
/* 1062 */       this.dn = this.names[0].toString();
/* 1063 */       return;
/*      */     }
/*      */     
/* 1066 */     StringBuilder localStringBuilder = new StringBuilder(48);
/* 1067 */     if (this.names != null) {
/* 1068 */       for (int i = this.names.length - 1; i >= 0; i--) {
/* 1069 */         if (i != this.names.length - 1) {
/* 1070 */           localStringBuilder.append(", ");
/*      */         }
/* 1072 */         localStringBuilder.append(this.names[i].toString());
/*      */       }
/*      */     }
/* 1075 */     this.dn = localStringBuilder.toString();
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
/*      */   private String generateRFC1779DN(Map<String, String> paramMap)
/*      */   {
/* 1088 */     if (this.names.length == 1) {
/* 1089 */       return this.names[0].toRFC1779String(paramMap);
/*      */     }
/*      */     
/* 1092 */     StringBuilder localStringBuilder = new StringBuilder(48);
/* 1093 */     if (this.names != null) {
/* 1094 */       for (int i = this.names.length - 1; i >= 0; i--) {
/* 1095 */         if (i != this.names.length - 1) {
/* 1096 */           localStringBuilder.append(", ");
/*      */         }
/* 1098 */         localStringBuilder.append(this.names[i].toRFC1779String(paramMap));
/*      */       }
/*      */     }
/* 1101 */     return localStringBuilder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static ObjectIdentifier intern(ObjectIdentifier paramObjectIdentifier)
/*      */   {
/* 1111 */     ObjectIdentifier localObjectIdentifier = (ObjectIdentifier)internedOIDs.putIfAbsent(paramObjectIdentifier, paramObjectIdentifier);
/* 1112 */     return localObjectIdentifier == null ? paramObjectIdentifier : localObjectIdentifier;
/*      */   }
/*      */   
/* 1115 */   private static final Map<ObjectIdentifier, ObjectIdentifier> internedOIDs = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1123 */   private static final int[] commonName_data = { 2, 5, 4, 3 };
/* 1124 */   private static final int[] SURNAME_DATA = { 2, 5, 4, 4 };
/* 1125 */   private static final int[] SERIALNUMBER_DATA = { 2, 5, 4, 5 };
/* 1126 */   private static final int[] countryName_data = { 2, 5, 4, 6 };
/* 1127 */   private static final int[] localityName_data = { 2, 5, 4, 7 };
/* 1128 */   private static final int[] stateName_data = { 2, 5, 4, 8 };
/* 1129 */   private static final int[] streetAddress_data = { 2, 5, 4, 9 };
/* 1130 */   private static final int[] orgName_data = { 2, 5, 4, 10 };
/* 1131 */   private static final int[] orgUnitName_data = { 2, 5, 4, 11 };
/* 1132 */   private static final int[] title_data = { 2, 5, 4, 12 };
/* 1133 */   private static final int[] GIVENNAME_DATA = { 2, 5, 4, 42 };
/* 1134 */   private static final int[] INITIALS_DATA = { 2, 5, 4, 43 };
/* 1135 */   private static final int[] GENERATIONQUALIFIER_DATA = { 2, 5, 4, 44 };
/* 1136 */   private static final int[] DNQUALIFIER_DATA = { 2, 5, 4, 46 };
/*      */   
/* 1138 */   private static final int[] ipAddress_data = { 1, 3, 6, 1, 4, 1, 42, 2, 11, 2, 1 };
/* 1139 */   private static final int[] DOMAIN_COMPONENT_DATA = { 0, 9, 2342, 19200300, 100, 1, 25 };
/*      */   
/* 1141 */   private static final int[] userid_data = { 0, 9, 2342, 19200300, 100, 1, 1 };
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
/* 1165 */   public static final ObjectIdentifier commonName_oid = intern(ObjectIdentifier.newInternal(commonName_data));
/*      */   public static final ObjectIdentifier countryName_oid;
/*      */   public static final ObjectIdentifier localityName_oid;
/*      */   public static final ObjectIdentifier orgName_oid;
/*      */   public static final ObjectIdentifier orgUnitName_oid;
/* 1170 */   public static final ObjectIdentifier stateName_oid; public static final ObjectIdentifier streetAddress_oid; public static final ObjectIdentifier title_oid; public static final ObjectIdentifier DNQUALIFIER_OID; public static final ObjectIdentifier SURNAME_OID; public static final ObjectIdentifier GIVENNAME_OID; public static final ObjectIdentifier INITIALS_OID; public static final ObjectIdentifier GENERATIONQUALIFIER_OID; public static final ObjectIdentifier ipAddress_oid; public static final ObjectIdentifier DOMAIN_COMPONENT_OID; public static final ObjectIdentifier userid_oid; public static final ObjectIdentifier SERIALNUMBER_OID = intern(ObjectIdentifier.newInternal(SERIALNUMBER_DATA));
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
/*      */   private static final Constructor<X500Principal> principalConstructor;
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
/*      */   private static final Field principalField;
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
/*      */   public int constrains(GeneralNameInterface paramGeneralNameInterface)
/*      */     throws UnsupportedOperationException
/*      */   {
/*      */     int i;
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
/* 1252 */     if (paramGeneralNameInterface == null) {
/* 1253 */       i = -1;
/* 1254 */     } else if (paramGeneralNameInterface.getType() != 4) {
/* 1255 */       i = -1;
/*      */     } else {
/* 1257 */       X500Name localX500Name = (X500Name)paramGeneralNameInterface;
/* 1258 */       if (localX500Name.equals(this)) {
/* 1259 */         i = 0;
/* 1260 */       } else if (localX500Name.names.length == 0) {
/* 1261 */         i = 2;
/* 1262 */       } else if (this.names.length == 0) {
/* 1263 */         i = 1;
/* 1264 */       } else if (localX500Name.isWithinSubtree(this)) {
/* 1265 */         i = 1;
/* 1266 */       } else if (isWithinSubtree(localX500Name)) {
/* 1267 */         i = 2;
/*      */       } else {
/* 1269 */         i = 3;
/*      */       }
/*      */     }
/* 1272 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean isWithinSubtree(X500Name paramX500Name)
/*      */   {
/* 1283 */     if (this == paramX500Name) {
/* 1284 */       return true;
/*      */     }
/* 1286 */     if (paramX500Name == null) {
/* 1287 */       return false;
/*      */     }
/* 1289 */     if (paramX500Name.names.length == 0) {
/* 1290 */       return true;
/*      */     }
/* 1292 */     if (this.names.length == 0) {
/* 1293 */       return false;
/*      */     }
/* 1295 */     if (this.names.length < paramX500Name.names.length) {
/* 1296 */       return false;
/*      */     }
/* 1298 */     for (int i = 0; i < paramX500Name.names.length; i++) {
/* 1299 */       if (!this.names[i].equals(paramX500Name.names[i])) {
/* 1300 */         return false;
/*      */       }
/*      */     }
/* 1303 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int subtreeDepth()
/*      */     throws UnsupportedOperationException
/*      */   {
/* 1315 */     return this.names.length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X500Name commonAncestor(X500Name paramX500Name)
/*      */   {
/* 1326 */     if (paramX500Name == null) {
/* 1327 */       return null;
/*      */     }
/* 1329 */     int i = paramX500Name.names.length;
/* 1330 */     int j = this.names.length;
/* 1331 */     if ((j == 0) || (i == 0)) {
/* 1332 */       return null;
/*      */     }
/* 1334 */     int k = j < i ? j : i;
/*      */     
/*      */ 
/*      */ 
/* 1338 */     for (int m = 0; 
/* 1339 */         m < k; m++) {
/* 1340 */       if (!this.names[m].equals(paramX500Name.names[m])) {
/* 1341 */         if (m != 0) break;
/* 1342 */         return null;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1350 */     RDN[] arrayOfRDN = new RDN[m];
/* 1351 */     for (int n = 0; n < m; n++) {
/* 1352 */       arrayOfRDN[n] = this.names[n];
/*      */     }
/*      */     
/* 1355 */     X500Name localX500Name = null;
/*      */     try {
/* 1357 */       localX500Name = new X500Name(arrayOfRDN);
/*      */     } catch (IOException localIOException) {
/* 1359 */       return null;
/*      */     }
/* 1361 */     return localX500Name;
/*      */   }
/*      */   
/*      */   static
/*      */   {
/* 1173 */     countryName_oid = intern(ObjectIdentifier.newInternal(countryName_data));
/*      */     
/*      */ 
/* 1176 */     localityName_oid = intern(ObjectIdentifier.newInternal(localityName_data));
/*      */     
/*      */ 
/* 1179 */     orgName_oid = intern(ObjectIdentifier.newInternal(orgName_data));
/*      */     
/*      */ 
/* 1182 */     orgUnitName_oid = intern(ObjectIdentifier.newInternal(orgUnitName_data));
/*      */     
/*      */ 
/* 1185 */     stateName_oid = intern(ObjectIdentifier.newInternal(stateName_data));
/*      */     
/*      */ 
/* 1188 */     streetAddress_oid = intern(ObjectIdentifier.newInternal(streetAddress_data));
/*      */     
/*      */ 
/* 1191 */     title_oid = intern(ObjectIdentifier.newInternal(title_data));
/*      */     
/*      */ 
/*      */ 
/* 1195 */     DNQUALIFIER_OID = intern(ObjectIdentifier.newInternal(DNQUALIFIER_DATA));
/*      */     
/*      */ 
/* 1198 */     SURNAME_OID = intern(ObjectIdentifier.newInternal(SURNAME_DATA));
/*      */     
/*      */ 
/* 1201 */     GIVENNAME_OID = intern(ObjectIdentifier.newInternal(GIVENNAME_DATA));
/*      */     
/*      */ 
/* 1204 */     INITIALS_OID = intern(ObjectIdentifier.newInternal(INITIALS_DATA));
/*      */     
/*      */ 
/*      */ 
/* 1208 */     GENERATIONQUALIFIER_OID = intern(ObjectIdentifier.newInternal(GENERATIONQUALIFIER_DATA));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1215 */     ipAddress_oid = intern(ObjectIdentifier.newInternal(ipAddress_data));
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
/* 1226 */     DOMAIN_COMPONENT_OID = intern(ObjectIdentifier.newInternal(DOMAIN_COMPONENT_DATA));
/*      */     
/*      */ 
/* 1229 */     userid_oid = intern(ObjectIdentifier.newInternal(userid_data));
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
/* 1379 */     PrivilegedExceptionAction local1 = new PrivilegedExceptionAction()
/*      */     {
/*      */       public Object[] run() throws Exception {
/* 1382 */         Class localClass = X500Principal.class;
/* 1383 */         Class[] arrayOfClass = { X500Name.class };
/* 1384 */         Constructor localConstructor = localClass.getDeclaredConstructor(arrayOfClass);
/* 1385 */         localConstructor.setAccessible(true);
/* 1386 */         Field localField = localClass.getDeclaredField("thisX500Name");
/* 1387 */         localField.setAccessible(true);
/* 1388 */         return new Object[] { localConstructor, localField };
/*      */       }
/*      */     };
/*      */     try {
/* 1392 */       Object[] arrayOfObject = (Object[])AccessController.doPrivileged(local1);
/*      */       
/* 1394 */       Constructor localConstructor = (Constructor)arrayOfObject[0];
/*      */       
/* 1396 */       principalConstructor = localConstructor;
/* 1397 */       principalField = (Field)arrayOfObject[1];
/*      */     } catch (Exception localException) {
/* 1399 */       throw new InternalError("Could not obtain X500Principal access", localException);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public X500Principal asX500Principal()
/*      */   {
/* 1410 */     if (this.x500Principal == null) {
/*      */       try {
/* 1412 */         Object[] arrayOfObject = { this };
/* 1413 */         this.x500Principal = ((X500Principal)principalConstructor.newInstance(arrayOfObject));
/*      */       } catch (Exception localException) {
/* 1415 */         throw new RuntimeException("Unexpected exception", localException);
/*      */       }
/*      */     }
/* 1418 */     return this.x500Principal;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static X500Name asX500Name(X500Principal paramX500Principal)
/*      */   {
/*      */     try
/*      */     {
/* 1428 */       X500Name localX500Name = (X500Name)principalField.get(paramX500Principal);
/* 1429 */       localX500Name.x500Principal = paramX500Principal;
/* 1430 */       return localX500Name;
/*      */     } catch (Exception localException) {
/* 1432 */       throw new RuntimeException("Unexpected exception", localException);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\X500Name.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateExpiredException;
/*     */ import java.security.cert.CertificateNotYetValidException;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrivateKeyUsageExtension
/*     */   extends Extension
/*     */   implements CertAttrSet<String>
/*     */ {
/*     */   public static final String IDENT = "x509.info.extensions.PrivateKeyUsage";
/*     */   public static final String NAME = "PrivateKeyUsage";
/*     */   public static final String NOT_BEFORE = "not_before";
/*     */   public static final String NOT_AFTER = "not_after";
/*     */   private static final byte TAG_BEFORE = 0;
/*     */   private static final byte TAG_AFTER = 1;
/*  80 */   private Date notBefore = null;
/*  81 */   private Date notAfter = null;
/*     */   
/*     */   private void encodeThis() throws IOException
/*     */   {
/*  85 */     if ((this.notBefore == null) && (this.notAfter == null)) {
/*  86 */       this.extensionValue = null;
/*  87 */       return;
/*     */     }
/*  89 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*     */     
/*  91 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*  92 */     DerOutputStream localDerOutputStream3; if (this.notBefore != null) {
/*  93 */       localDerOutputStream3 = new DerOutputStream();
/*  94 */       localDerOutputStream3.putGeneralizedTime(this.notBefore);
/*  95 */       localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)0), localDerOutputStream3);
/*     */     }
/*     */     
/*  98 */     if (this.notAfter != null) {
/*  99 */       localDerOutputStream3 = new DerOutputStream();
/* 100 */       localDerOutputStream3.putGeneralizedTime(this.notAfter);
/* 101 */       localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1), localDerOutputStream3);
/*     */     }
/*     */     
/* 104 */     localDerOutputStream1.write((byte)48, localDerOutputStream2);
/* 105 */     this.extensionValue = localDerOutputStream1.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PrivateKeyUsageExtension(Date paramDate1, Date paramDate2)
/*     */     throws IOException
/*     */   {
/* 118 */     this.notBefore = paramDate1;
/* 119 */     this.notAfter = paramDate2;
/*     */     
/* 121 */     this.extensionId = PKIXExtensions.PrivateKeyUsage_Id;
/* 122 */     this.critical = false;
/* 123 */     encodeThis();
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
/*     */   public PrivateKeyUsageExtension(Boolean paramBoolean, Object paramObject)
/*     */     throws CertificateException, IOException
/*     */   {
/* 137 */     this.extensionId = PKIXExtensions.PrivateKeyUsage_Id;
/* 138 */     this.critical = paramBoolean.booleanValue();
/*     */     
/* 140 */     this.extensionValue = ((byte[])paramObject);
/* 141 */     DerInputStream localDerInputStream = new DerInputStream(this.extensionValue);
/* 142 */     DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 147 */     for (int i = 0; i < arrayOfDerValue.length; i++) {
/* 148 */       DerValue localDerValue = arrayOfDerValue[i];
/*     */       
/* 150 */       if ((localDerValue.isContextSpecific((byte)0)) && 
/* 151 */         (!localDerValue.isConstructed())) {
/* 152 */         if (this.notBefore != null) {
/* 153 */           throw new CertificateParsingException("Duplicate notBefore in PrivateKeyUsage.");
/*     */         }
/*     */         
/* 156 */         localDerValue.resetTag((byte)24);
/* 157 */         localDerInputStream = new DerInputStream(localDerValue.toByteArray());
/* 158 */         this.notBefore = localDerInputStream.getGeneralizedTime();
/*     */       }
/* 160 */       else if ((localDerValue.isContextSpecific((byte)1)) && 
/* 161 */         (!localDerValue.isConstructed())) {
/* 162 */         if (this.notAfter != null) {
/* 163 */           throw new CertificateParsingException("Duplicate notAfter in PrivateKeyUsage.");
/*     */         }
/*     */         
/* 166 */         localDerValue.resetTag((byte)24);
/* 167 */         localDerInputStream = new DerInputStream(localDerValue.toByteArray());
/* 168 */         this.notAfter = localDerInputStream.getGeneralizedTime();
/*     */       } else {
/* 170 */         throw new IOException("Invalid encoding of PrivateKeyUsageExtension");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 179 */     return 
/*     */     
/*     */ 
/* 182 */       super.toString() + "PrivateKeyUsage: [\n" + (this.notBefore == null ? "" : new StringBuilder().append("From: ").append(this.notBefore.toString()).append(", ").toString()) + (this.notAfter == null ? "" : new StringBuilder().append("To: ").append(this.notAfter.toString()).toString()) + "]\n";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void valid()
/*     */     throws CertificateNotYetValidException, CertificateExpiredException
/*     */   {
/* 195 */     Date localDate = new Date();
/* 196 */     valid(localDate);
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
/*     */   public void valid(Date paramDate)
/*     */     throws CertificateNotYetValidException, CertificateExpiredException
/*     */   {
/* 210 */     Objects.requireNonNull(paramDate);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 216 */     if ((this.notBefore != null) && (this.notBefore.after(paramDate)))
/*     */     {
/* 218 */       throw new CertificateNotYetValidException("NotBefore: " + this.notBefore.toString());
/*     */     }
/* 220 */     if ((this.notAfter != null) && (this.notAfter.before(paramDate)))
/*     */     {
/* 222 */       throw new CertificateExpiredException("NotAfter: " + this.notAfter.toString());
/*     */     }
/*     */   }
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
/* 235 */       this.extensionId = PKIXExtensions.PrivateKeyUsage_Id;
/* 236 */       this.critical = false;
/* 237 */       encodeThis();
/*     */     }
/* 239 */     super.encode(localDerOutputStream);
/* 240 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws CertificateException, IOException
/*     */   {
/* 249 */     if (!(paramObject instanceof Date)) {
/* 250 */       throw new CertificateException("Attribute must be of type Date.");
/*     */     }
/* 252 */     if (paramString.equalsIgnoreCase("not_before")) {
/* 253 */       this.notBefore = ((Date)paramObject);
/* 254 */     } else if (paramString.equalsIgnoreCase("not_after")) {
/* 255 */       this.notAfter = ((Date)paramObject);
/*     */     } else {
/* 257 */       throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
/*     */     }
/*     */     
/* 260 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Date get(String paramString)
/*     */     throws CertificateException
/*     */   {
/* 268 */     if (paramString.equalsIgnoreCase("not_before"))
/* 269 */       return new Date(this.notBefore.getTime());
/* 270 */     if (paramString.equalsIgnoreCase("not_after")) {
/* 271 */       return new Date(this.notAfter.getTime());
/*     */     }
/* 273 */     throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws CertificateException, IOException
/*     */   {
/* 283 */     if (paramString.equalsIgnoreCase("not_before")) {
/* 284 */       this.notBefore = null;
/* 285 */     } else if (paramString.equalsIgnoreCase("not_after")) {
/* 286 */       this.notAfter = null;
/*     */     } else {
/* 288 */       throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
/*     */     }
/*     */     
/* 291 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 299 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 300 */     localAttributeNameEnumeration.addElement("not_before");
/* 301 */     localAttributeNameEnumeration.addElement("not_after");
/*     */     
/* 303 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 310 */     return "PrivateKeyUsage";
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\PrivateKeyUsageExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
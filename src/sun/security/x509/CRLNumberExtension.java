/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Enumeration;
/*     */ import sun.security.util.Debug;
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
/*     */ public class CRLNumberExtension
/*     */   extends Extension
/*     */   implements CertAttrSet<String>
/*     */ {
/*     */   public static final String NAME = "CRLNumber";
/*     */   public static final String NUMBER = "value";
/*     */   private static final String LABEL = "CRL Number";
/*  59 */   private BigInteger crlNumber = null;
/*     */   private String extensionName;
/*     */   private String extensionLabel;
/*     */   
/*     */   private void encodeThis() throws IOException
/*     */   {
/*  65 */     if (this.crlNumber == null) {
/*  66 */       this.extensionValue = null;
/*  67 */       return;
/*     */     }
/*  69 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*  70 */     localDerOutputStream.putInteger(this.crlNumber);
/*  71 */     this.extensionValue = localDerOutputStream.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CRLNumberExtension(int paramInt)
/*     */     throws IOException
/*     */   {
/*  81 */     this(PKIXExtensions.CRLNumber_Id, false, BigInteger.valueOf(paramInt), "CRLNumber", "CRL Number");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CRLNumberExtension(BigInteger paramBigInteger)
/*     */     throws IOException
/*     */   {
/*  92 */     this(PKIXExtensions.CRLNumber_Id, false, paramBigInteger, "CRLNumber", "CRL Number");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CRLNumberExtension(ObjectIdentifier paramObjectIdentifier, boolean paramBoolean, BigInteger paramBigInteger, String paramString1, String paramString2)
/*     */     throws IOException
/*     */   {
/* 102 */     this.extensionId = paramObjectIdentifier;
/* 103 */     this.critical = paramBoolean;
/* 104 */     this.crlNumber = paramBigInteger;
/* 105 */     this.extensionName = paramString1;
/* 106 */     this.extensionLabel = paramString2;
/* 107 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CRLNumberExtension(Boolean paramBoolean, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 120 */     this(PKIXExtensions.CRLNumber_Id, paramBoolean, paramObject, "CRLNumber", "CRL Number");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CRLNumberExtension(ObjectIdentifier paramObjectIdentifier, Boolean paramBoolean, Object paramObject, String paramString1, String paramString2)
/*     */     throws IOException
/*     */   {
/* 130 */     this.extensionId = paramObjectIdentifier;
/* 131 */     this.critical = paramBoolean.booleanValue();
/* 132 */     this.extensionValue = ((byte[])paramObject);
/* 133 */     DerValue localDerValue = new DerValue(this.extensionValue);
/* 134 */     this.crlNumber = localDerValue.getBigInteger();
/* 135 */     this.extensionName = paramString1;
/* 136 */     this.extensionLabel = paramString2;
/*     */   }
/*     */   
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 143 */     if (paramString.equalsIgnoreCase("value")) {
/* 144 */       if (!(paramObject instanceof BigInteger)) {
/* 145 */         throw new IOException("Attribute must be of type BigInteger.");
/*     */       }
/* 147 */       this.crlNumber = ((BigInteger)paramObject);
/*     */     } else {
/* 149 */       throw new IOException("Attribute name not recognized by CertAttrSet:" + this.extensionName + ".");
/*     */     }
/*     */     
/* 152 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */   public BigInteger get(String paramString)
/*     */     throws IOException
/*     */   {
/* 159 */     if (paramString.equalsIgnoreCase("value")) {
/* 160 */       return this.crlNumber;
/*     */     }
/* 162 */     throw new IOException("Attribute name not recognized by CertAttrSet:" + this.extensionName + '.');
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 171 */     if (paramString.equalsIgnoreCase("value")) {
/* 172 */       this.crlNumber = null;
/*     */     } else {
/* 174 */       throw new IOException("Attribute name not recognized by CertAttrSet:" + this.extensionName + ".");
/*     */     }
/*     */     
/* 177 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 185 */     String str = super.toString() + this.extensionLabel + ": " + (this.crlNumber == null ? "" : Debug.toHexString(this.crlNumber)) + "\n";
/*     */     
/* 187 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 197 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 198 */     encode(paramOutputStream, PKIXExtensions.CRLNumber_Id, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void encode(OutputStream paramOutputStream, ObjectIdentifier paramObjectIdentifier, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 208 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */     
/* 210 */     if (this.extensionValue == null) {
/* 211 */       this.extensionId = paramObjectIdentifier;
/* 212 */       this.critical = paramBoolean;
/* 213 */       encodeThis();
/*     */     }
/* 215 */     super.encode(localDerOutputStream);
/* 216 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 224 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 225 */     localAttributeNameEnumeration.addElement("value");
/* 226 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 233 */     return this.extensionName;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\CRLNumberExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
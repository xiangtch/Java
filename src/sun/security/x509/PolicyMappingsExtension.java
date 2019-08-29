/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
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
/*     */ public class PolicyMappingsExtension
/*     */   extends Extension
/*     */   implements CertAttrSet<String>
/*     */ {
/*     */   public static final String IDENT = "x509.info.extensions.PolicyMappings";
/*     */   public static final String NAME = "PolicyMappings";
/*     */   public static final String MAP = "map";
/*     */   private List<CertificatePolicyMap> maps;
/*     */   
/*     */   private void encodeThis()
/*     */     throws IOException
/*     */   {
/*  71 */     if ((this.maps == null) || (this.maps.isEmpty())) {
/*  72 */       this.extensionValue = null;
/*  73 */       return;
/*     */     }
/*  75 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  76 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*     */     
/*  78 */     for (CertificatePolicyMap localCertificatePolicyMap : this.maps) {
/*  79 */       localCertificatePolicyMap.encode(localDerOutputStream2);
/*     */     }
/*     */     
/*  82 */     localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*  83 */     this.extensionValue = localDerOutputStream1.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PolicyMappingsExtension(List<CertificatePolicyMap> paramList)
/*     */     throws IOException
/*     */   {
/*  93 */     this.maps = paramList;
/*  94 */     this.extensionId = PKIXExtensions.PolicyMappings_Id;
/*  95 */     this.critical = false;
/*  96 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PolicyMappingsExtension()
/*     */   {
/* 103 */     this.extensionId = PKIXExtensions.KeyUsage_Id;
/* 104 */     this.critical = false;
/* 105 */     this.maps = Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PolicyMappingsExtension(Boolean paramBoolean, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 118 */     this.extensionId = PKIXExtensions.PolicyMappings_Id;
/* 119 */     this.critical = paramBoolean.booleanValue();
/*     */     
/* 121 */     this.extensionValue = ((byte[])paramObject);
/* 122 */     DerValue localDerValue1 = new DerValue(this.extensionValue);
/* 123 */     if (localDerValue1.tag != 48) {
/* 124 */       throw new IOException("Invalid encoding for PolicyMappingsExtension.");
/*     */     }
/*     */     
/* 127 */     this.maps = new ArrayList();
/* 128 */     while (localDerValue1.data.available() != 0) {
/* 129 */       DerValue localDerValue2 = localDerValue1.data.getDerValue();
/* 130 */       CertificatePolicyMap localCertificatePolicyMap = new CertificatePolicyMap(localDerValue2);
/* 131 */       this.maps.add(localCertificatePolicyMap);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 139 */     if (this.maps == null) { return "";
/*     */     }
/* 141 */     String str = super.toString() + "PolicyMappings [\n" + this.maps.toString() + "]\n";
/*     */     
/* 143 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 153 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 154 */     if (this.extensionValue == null) {
/* 155 */       this.extensionId = PKIXExtensions.PolicyMappings_Id;
/* 156 */       this.critical = false;
/* 157 */       encodeThis();
/*     */     }
/* 159 */     super.encode(localDerOutputStream);
/* 160 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 168 */     if (paramString.equalsIgnoreCase("map")) {
/* 169 */       if (!(paramObject instanceof List)) {
/* 170 */         throw new IOException("Attribute value should be of type List.");
/*     */       }
/*     */       
/* 173 */       this.maps = ((List)paramObject);
/*     */     } else {
/* 175 */       throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
/*     */     }
/*     */     
/* 178 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */   public List<CertificatePolicyMap> get(String paramString)
/*     */     throws IOException
/*     */   {
/* 185 */     if (paramString.equalsIgnoreCase("map")) {
/* 186 */       return this.maps;
/*     */     }
/* 188 */     throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 197 */     if (paramString.equalsIgnoreCase("map")) {
/* 198 */       this.maps = null;
/*     */     } else {
/* 200 */       throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
/*     */     }
/*     */     
/* 203 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 211 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 212 */     localAttributeNameEnumeration.addElement("map");
/*     */     
/* 214 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 221 */     return "PolicyMappings";
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\PolicyMappingsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
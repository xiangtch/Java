/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CertificatePoliciesExtension
/*     */   extends Extension
/*     */   implements CertAttrSet<String>
/*     */ {
/*     */   public static final String IDENT = "x509.info.extensions.CertificatePolicies";
/*     */   public static final String NAME = "CertificatePolicies";
/*     */   public static final String POLICIES = "policies";
/*     */   private List<PolicyInformation> certPolicies;
/*     */   
/*     */   private void encodeThis()
/*     */     throws IOException
/*     */   {
/*  89 */     if ((this.certPolicies == null) || (this.certPolicies.isEmpty())) {
/*  90 */       this.extensionValue = null;
/*     */     } else {
/*  92 */       DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  93 */       DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*     */       
/*  95 */       for (PolicyInformation localPolicyInformation : this.certPolicies) {
/*  96 */         localPolicyInformation.encode(localDerOutputStream2);
/*     */       }
/*     */       
/*  99 */       localDerOutputStream1.write((byte)48, localDerOutputStream2);
/* 100 */       this.extensionValue = localDerOutputStream1.toByteArray();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertificatePoliciesExtension(List<PolicyInformation> paramList)
/*     */     throws IOException
/*     */   {
/* 112 */     this(Boolean.FALSE, paramList);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertificatePoliciesExtension(Boolean paramBoolean, List<PolicyInformation> paramList)
/*     */     throws IOException
/*     */   {
/* 124 */     this.certPolicies = paramList;
/* 125 */     this.extensionId = PKIXExtensions.CertificatePolicies_Id;
/* 126 */     this.critical = paramBoolean.booleanValue();
/* 127 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertificatePoliciesExtension(Boolean paramBoolean, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 140 */     this.extensionId = PKIXExtensions.CertificatePolicies_Id;
/* 141 */     this.critical = paramBoolean.booleanValue();
/* 142 */     this.extensionValue = ((byte[])paramObject);
/* 143 */     DerValue localDerValue1 = new DerValue(this.extensionValue);
/* 144 */     if (localDerValue1.tag != 48) {
/* 145 */       throw new IOException("Invalid encoding for CertificatePoliciesExtension.");
/*     */     }
/*     */     
/* 148 */     this.certPolicies = new ArrayList();
/* 149 */     while (localDerValue1.data.available() != 0) {
/* 150 */       DerValue localDerValue2 = localDerValue1.data.getDerValue();
/* 151 */       PolicyInformation localPolicyInformation = new PolicyInformation(localDerValue2);
/* 152 */       this.certPolicies.add(localPolicyInformation);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 160 */     if (this.certPolicies == null) {
/* 161 */       return "";
/*     */     }
/* 163 */     StringBuilder localStringBuilder = new StringBuilder(super.toString());
/* 164 */     localStringBuilder.append("CertificatePolicies [\n");
/* 165 */     for (PolicyInformation localPolicyInformation : this.certPolicies) {
/* 166 */       localStringBuilder.append(localPolicyInformation.toString());
/*     */     }
/* 168 */     localStringBuilder.append("]\n");
/* 169 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 179 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 180 */     if (this.extensionValue == null) {
/* 181 */       this.extensionId = PKIXExtensions.CertificatePolicies_Id;
/* 182 */       this.critical = false;
/* 183 */       encodeThis();
/*     */     }
/* 185 */     super.encode(localDerOutputStream);
/* 186 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 194 */     if (paramString.equalsIgnoreCase("policies")) {
/* 195 */       if (!(paramObject instanceof List)) {
/* 196 */         throw new IOException("Attribute value should be of type List.");
/*     */       }
/* 198 */       this.certPolicies = ((List)paramObject);
/*     */     } else {
/* 200 */       throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
/*     */     }
/*     */     
/*     */ 
/* 204 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */   public List<PolicyInformation> get(String paramString)
/*     */     throws IOException
/*     */   {
/* 211 */     if (paramString.equalsIgnoreCase("policies"))
/*     */     {
/* 213 */       return this.certPolicies;
/*     */     }
/* 215 */     throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 225 */     if (paramString.equalsIgnoreCase("policies")) {
/* 226 */       this.certPolicies = null;
/*     */     } else {
/* 228 */       throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
/*     */     }
/*     */     
/*     */ 
/* 232 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 240 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 241 */     localAttributeNameEnumeration.addElement("policies");
/*     */     
/* 243 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 250 */     return "CertificatePolicies";
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\CertificatePoliciesExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
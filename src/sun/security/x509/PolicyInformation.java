/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.cert.PolicyQualifierInfo;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
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
/*     */ public class PolicyInformation
/*     */ {
/*     */   public static final String NAME = "PolicyInformation";
/*     */   public static final String ID = "id";
/*     */   public static final String QUALIFIERS = "qualifiers";
/*     */   private CertificatePolicyId policyIdentifier;
/*     */   private Set<PolicyQualifierInfo> policyQualifiers;
/*     */   
/*     */   public PolicyInformation(CertificatePolicyId paramCertificatePolicyId, Set<PolicyQualifierInfo> paramSet)
/*     */     throws IOException
/*     */   {
/*  87 */     if (paramSet == null) {
/*  88 */       throw new NullPointerException("policyQualifiers is null");
/*     */     }
/*  90 */     this.policyQualifiers = new LinkedHashSet(paramSet);
/*     */     
/*  92 */     this.policyIdentifier = paramCertificatePolicyId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PolicyInformation(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/* 103 */     if (paramDerValue.tag != 48) {
/* 104 */       throw new IOException("Invalid encoding of PolicyInformation");
/*     */     }
/* 106 */     this.policyIdentifier = new CertificatePolicyId(paramDerValue.data.getDerValue());
/* 107 */     if (paramDerValue.data.available() != 0) {
/* 108 */       this.policyQualifiers = new LinkedHashSet();
/* 109 */       DerValue localDerValue = paramDerValue.data.getDerValue();
/* 110 */       if (localDerValue.tag != 48)
/* 111 */         throw new IOException("Invalid encoding of PolicyInformation");
/* 112 */       if (localDerValue.data.available() == 0)
/* 113 */         throw new IOException("No data available in policyQualifiers");
/* 114 */       while (localDerValue.data.available() != 0)
/* 115 */         this.policyQualifiers.add(new PolicyQualifierInfo(localDerValue.data
/* 116 */           .getDerValue().toByteArray()));
/*     */     } else {
/* 118 */       this.policyQualifiers = Collections.emptySet();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 129 */     if (!(paramObject instanceof PolicyInformation))
/* 130 */       return false;
/* 131 */     PolicyInformation localPolicyInformation = (PolicyInformation)paramObject;
/*     */     
/* 133 */     if (!this.policyIdentifier.equals(localPolicyInformation.getPolicyIdentifier())) {
/* 134 */       return false;
/*     */     }
/* 136 */     return this.policyQualifiers.equals(localPolicyInformation.getPolicyQualifiers());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 145 */     int i = 37 + this.policyIdentifier.hashCode();
/* 146 */     i = 37 * i + this.policyQualifiers.hashCode();
/* 147 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertificatePolicyId getPolicyIdentifier()
/*     */   {
/* 157 */     return this.policyIdentifier;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<PolicyQualifierInfo> getPolicyQualifiers()
/*     */   {
/* 169 */     return this.policyQualifiers;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object get(String paramString)
/*     */     throws IOException
/*     */   {
/* 176 */     if (paramString.equalsIgnoreCase("id"))
/* 177 */       return this.policyIdentifier;
/* 178 */     if (paramString.equalsIgnoreCase("qualifiers")) {
/* 179 */       return this.policyQualifiers;
/*     */     }
/* 181 */     throw new IOException("Attribute name [" + paramString + "] not recognized by PolicyInformation.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 191 */     if (paramString.equalsIgnoreCase("id")) {
/* 192 */       if ((paramObject instanceof CertificatePolicyId)) {
/* 193 */         this.policyIdentifier = ((CertificatePolicyId)paramObject);
/*     */       } else {
/* 195 */         throw new IOException("Attribute value must be instance of CertificatePolicyId.");
/*     */       }
/* 197 */     } else if (paramString.equalsIgnoreCase("qualifiers")) {
/* 198 */       if (this.policyIdentifier == null) {
/* 199 */         throw new IOException("Attribute must have a CertificatePolicyIdentifier value before PolicyQualifierInfo can be set.");
/*     */       }
/*     */       
/*     */ 
/* 203 */       if ((paramObject instanceof Set)) {
/* 204 */         Iterator localIterator = ((Set)paramObject).iterator();
/* 205 */         while (localIterator.hasNext()) {
/* 206 */           Object localObject = localIterator.next();
/* 207 */           if (!(localObject instanceof PolicyQualifierInfo)) {
/* 208 */             throw new IOException("Attribute value must be aSet of PolicyQualifierInfo objects.");
/*     */           }
/*     */         }
/*     */         
/* 212 */         this.policyQualifiers = ((Set)paramObject);
/*     */       } else {
/* 214 */         throw new IOException("Attribute value must be of type Set.");
/*     */       }
/*     */     } else {
/* 217 */       throw new IOException("Attribute name [" + paramString + "] not recognized by PolicyInformation");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 226 */     if (paramString.equalsIgnoreCase("qualifiers")) {
/* 227 */       this.policyQualifiers = Collections.emptySet();
/* 228 */     } else { if (paramString.equalsIgnoreCase("id")) {
/* 229 */         throw new IOException("Attribute ID may not be deleted from PolicyInformation.");
/*     */       }
/*     */       
/*     */ 
/* 233 */       throw new IOException("Attribute name [" + paramString + "] not recognized by PolicyInformation.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 243 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 244 */     localAttributeNameEnumeration.addElement("id");
/* 245 */     localAttributeNameEnumeration.addElement("qualifiers");
/*     */     
/* 247 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 254 */     return "PolicyInformation";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 261 */     StringBuilder localStringBuilder = new StringBuilder("  [" + this.policyIdentifier.toString());
/* 262 */     localStringBuilder.append(this.policyQualifiers + "  ]\n");
/* 263 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 273 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 274 */     this.policyIdentifier.encode(localDerOutputStream1);
/* 275 */     if (!this.policyQualifiers.isEmpty()) {
/* 276 */       DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 277 */       for (PolicyQualifierInfo localPolicyQualifierInfo : this.policyQualifiers) {
/* 278 */         localDerOutputStream2.write(localPolicyQualifierInfo.getEncoded());
/*     */       }
/* 280 */       localDerOutputStream1.write((byte)48, localDerOutputStream2);
/*     */     }
/* 282 */     paramDerOutputStream.write((byte)48, localDerOutputStream1);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\PolicyInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
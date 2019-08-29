/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SubjectInfoAccessExtension
/*     */   extends Extension
/*     */   implements CertAttrSet<String>
/*     */ {
/*     */   public static final String IDENT = "x509.info.extensions.SubjectInfoAccess";
/*     */   public static final String NAME = "SubjectInfoAccess";
/*     */   public static final String DESCRIPTIONS = "descriptions";
/*     */   private List<AccessDescription> accessDescriptions;
/*     */   
/*     */   public SubjectInfoAccessExtension(List<AccessDescription> paramList)
/*     */     throws IOException
/*     */   {
/* 101 */     this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
/* 102 */     this.critical = false;
/* 103 */     this.accessDescriptions = paramList;
/* 104 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SubjectInfoAccessExtension(Boolean paramBoolean, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 116 */     this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
/* 117 */     this.critical = paramBoolean.booleanValue();
/*     */     
/* 119 */     if (!(paramObject instanceof byte[])) {
/* 120 */       throw new IOException("Illegal argument type");
/*     */     }
/*     */     
/* 123 */     this.extensionValue = ((byte[])paramObject);
/* 124 */     DerValue localDerValue1 = new DerValue(this.extensionValue);
/* 125 */     if (localDerValue1.tag != 48) {
/* 126 */       throw new IOException("Invalid encoding for SubjectInfoAccessExtension.");
/*     */     }
/*     */     
/* 129 */     this.accessDescriptions = new ArrayList();
/* 130 */     while (localDerValue1.data.available() != 0) {
/* 131 */       DerValue localDerValue2 = localDerValue1.data.getDerValue();
/* 132 */       AccessDescription localAccessDescription = new AccessDescription(localDerValue2);
/* 133 */       this.accessDescriptions.add(localAccessDescription);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List<AccessDescription> getAccessDescriptions()
/*     */   {
/* 141 */     return this.accessDescriptions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 148 */     return "SubjectInfoAccess";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 158 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 159 */     if (this.extensionValue == null) {
/* 160 */       this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
/* 161 */       this.critical = false;
/* 162 */       encodeThis();
/*     */     }
/* 164 */     super.encode(localDerOutputStream);
/* 165 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 173 */     if (paramString.equalsIgnoreCase("descriptions")) {
/* 174 */       if (!(paramObject instanceof List)) {
/* 175 */         throw new IOException("Attribute value should be of type List.");
/*     */       }
/* 177 */       this.accessDescriptions = ((List)paramObject);
/*     */     } else {
/* 179 */       throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
/*     */     }
/*     */     
/*     */ 
/* 183 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */   public List<AccessDescription> get(String paramString)
/*     */     throws IOException
/*     */   {
/* 190 */     if (paramString.equalsIgnoreCase("descriptions")) {
/* 191 */       return this.accessDescriptions;
/*     */     }
/* 193 */     throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 203 */     if (paramString.equalsIgnoreCase("descriptions"))
/*     */     {
/* 205 */       this.accessDescriptions = Collections.emptyList();
/*     */     } else {
/* 207 */       throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
/*     */     }
/*     */     
/*     */ 
/* 211 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 219 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 220 */     localAttributeNameEnumeration.addElement("descriptions");
/* 221 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */   
/*     */   private void encodeThis() throws IOException
/*     */   {
/* 226 */     if (this.accessDescriptions.isEmpty()) {
/* 227 */       this.extensionValue = null;
/*     */     } else {
/* 229 */       DerOutputStream localDerOutputStream = new DerOutputStream();
/* 230 */       for (Object localObject = this.accessDescriptions.iterator(); ((Iterator)localObject).hasNext();) { AccessDescription localAccessDescription = (AccessDescription)((Iterator)localObject).next();
/* 231 */         localAccessDescription.encode(localDerOutputStream);
/*     */       }
/* 233 */       localObject = new DerOutputStream();
/* 234 */       ((DerOutputStream)localObject).write((byte)48, localDerOutputStream);
/* 235 */       this.extensionValue = ((DerOutputStream)localObject).toByteArray();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 243 */     return super.toString() + "SubjectInfoAccess [\n  " + this.accessDescriptions + "\n]\n";
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\SubjectInfoAccessExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
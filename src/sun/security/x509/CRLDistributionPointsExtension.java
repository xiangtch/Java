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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CRLDistributionPointsExtension
/*     */   extends Extension
/*     */   implements CertAttrSet<String>
/*     */ {
/*     */   public static final String IDENT = "x509.info.extensions.CRLDistributionPoints";
/*     */   public static final String NAME = "CRLDistributionPoints";
/*     */   public static final String POINTS = "points";
/*     */   private List<DistributionPoint> distributionPoints;
/*     */   private String extensionName;
/*     */   
/*     */   public CRLDistributionPointsExtension(List<DistributionPoint> paramList)
/*     */     throws IOException
/*     */   {
/* 116 */     this(false, paramList);
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
/*     */   public CRLDistributionPointsExtension(boolean paramBoolean, List<DistributionPoint> paramList)
/*     */     throws IOException
/*     */   {
/* 130 */     this(PKIXExtensions.CRLDistributionPoints_Id, paramBoolean, paramList, "CRLDistributionPoints");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CRLDistributionPointsExtension(ObjectIdentifier paramObjectIdentifier, boolean paramBoolean, List<DistributionPoint> paramList, String paramString)
/*     */     throws IOException
/*     */   {
/* 141 */     this.extensionId = paramObjectIdentifier;
/* 142 */     this.critical = paramBoolean;
/* 143 */     this.distributionPoints = paramList;
/* 144 */     encodeThis();
/* 145 */     this.extensionName = paramString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CRLDistributionPointsExtension(Boolean paramBoolean, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 157 */     this(PKIXExtensions.CRLDistributionPoints_Id, paramBoolean, paramObject, "CRLDistributionPoints");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CRLDistributionPointsExtension(ObjectIdentifier paramObjectIdentifier, Boolean paramBoolean, Object paramObject, String paramString)
/*     */     throws IOException
/*     */   {
/* 167 */     this.extensionId = paramObjectIdentifier;
/* 168 */     this.critical = paramBoolean.booleanValue();
/*     */     
/* 170 */     if (!(paramObject instanceof byte[])) {
/* 171 */       throw new IOException("Illegal argument type");
/*     */     }
/*     */     
/* 174 */     this.extensionValue = ((byte[])paramObject);
/* 175 */     DerValue localDerValue1 = new DerValue(this.extensionValue);
/* 176 */     if (localDerValue1.tag != 48) {
/* 177 */       throw new IOException("Invalid encoding for " + paramString + " extension.");
/*     */     }
/*     */     
/* 180 */     this.distributionPoints = new ArrayList();
/* 181 */     while (localDerValue1.data.available() != 0) {
/* 182 */       DerValue localDerValue2 = localDerValue1.data.getDerValue();
/* 183 */       DistributionPoint localDistributionPoint = new DistributionPoint(localDerValue2);
/* 184 */       this.distributionPoints.add(localDistributionPoint);
/*     */     }
/* 186 */     this.extensionName = paramString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 193 */     return this.extensionName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 203 */     encode(paramOutputStream, PKIXExtensions.CRLDistributionPoints_Id, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void encode(OutputStream paramOutputStream, ObjectIdentifier paramObjectIdentifier, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 213 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 214 */     if (this.extensionValue == null) {
/* 215 */       this.extensionId = paramObjectIdentifier;
/* 216 */       this.critical = paramBoolean;
/* 217 */       encodeThis();
/*     */     }
/* 219 */     super.encode(localDerOutputStream);
/* 220 */     paramOutputStream.write(localDerOutputStream.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 228 */     if (paramString.equalsIgnoreCase("points")) {
/* 229 */       if (!(paramObject instanceof List)) {
/* 230 */         throw new IOException("Attribute value should be of type List.");
/*     */       }
/* 232 */       this.distributionPoints = ((List)paramObject);
/*     */     } else {
/* 234 */       throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:" + this.extensionName + ".");
/*     */     }
/*     */     
/*     */ 
/* 238 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */   public List<DistributionPoint> get(String paramString)
/*     */     throws IOException
/*     */   {
/* 245 */     if (paramString.equalsIgnoreCase("points")) {
/* 246 */       return this.distributionPoints;
/*     */     }
/* 248 */     throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:" + this.extensionName + ".");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 258 */     if (paramString.equalsIgnoreCase("points"))
/*     */     {
/* 260 */       this.distributionPoints = Collections.emptyList();
/*     */     } else {
/* 262 */       throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:" + this.extensionName + '.');
/*     */     }
/*     */     
/*     */ 
/* 266 */     encodeThis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getElements()
/*     */   {
/* 274 */     AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
/* 275 */     localAttributeNameEnumeration.addElement("points");
/* 276 */     return localAttributeNameEnumeration.elements();
/*     */   }
/*     */   
/*     */   private void encodeThis() throws IOException
/*     */   {
/* 281 */     if (this.distributionPoints.isEmpty()) {
/* 282 */       this.extensionValue = null;
/*     */     } else {
/* 284 */       DerOutputStream localDerOutputStream = new DerOutputStream();
/* 285 */       for (Object localObject = this.distributionPoints.iterator(); ((Iterator)localObject).hasNext();) { DistributionPoint localDistributionPoint = (DistributionPoint)((Iterator)localObject).next();
/* 286 */         localDistributionPoint.encode(localDerOutputStream);
/*     */       }
/* 288 */       localObject = new DerOutputStream();
/* 289 */       ((DerOutputStream)localObject).write((byte)48, localDerOutputStream);
/* 290 */       this.extensionValue = ((DerOutputStream)localObject).toByteArray();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 298 */     return super.toString() + this.extensionName + " [\n  " + this.distributionPoints + "]\n";
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\CRLDistributionPointsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
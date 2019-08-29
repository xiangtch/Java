/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ public class DistributionPointName
/*     */ {
/*     */   private static final byte TAG_FULL_NAME = 0;
/*     */   private static final byte TAG_RELATIVE_NAME = 1;
/*  89 */   private GeneralNames fullName = null;
/*  90 */   private RDN relativeName = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private volatile int hashCode;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributionPointName(GeneralNames paramGeneralNames)
/*     */   {
/* 103 */     if (paramGeneralNames == null) {
/* 104 */       throw new IllegalArgumentException("fullName must not be null");
/*     */     }
/* 106 */     this.fullName = paramGeneralNames;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributionPointName(RDN paramRDN)
/*     */   {
/* 118 */     if (paramRDN == null) {
/* 119 */       throw new IllegalArgumentException("relativeName must not be null");
/*     */     }
/* 121 */     this.relativeName = paramRDN;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributionPointName(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/* 132 */     if ((paramDerValue.isContextSpecific((byte)0)) && 
/* 133 */       (paramDerValue.isConstructed()))
/*     */     {
/* 135 */       paramDerValue.resetTag((byte)48);
/* 136 */       this.fullName = new GeneralNames(paramDerValue);
/*     */     }
/* 138 */     else if ((paramDerValue.isContextSpecific((byte)1)) && 
/* 139 */       (paramDerValue.isConstructed()))
/*     */     {
/* 141 */       paramDerValue.resetTag((byte)49);
/* 142 */       this.relativeName = new RDN(paramDerValue);
/*     */     }
/*     */     else {
/* 145 */       throw new IOException("Invalid encoding for DistributionPointName");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public GeneralNames getFullName()
/*     */   {
/* 154 */     return this.fullName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RDN getRelativeName()
/*     */   {
/* 161 */     return this.relativeName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 172 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */     
/* 174 */     if (this.fullName != null) {
/* 175 */       this.fullName.encode(localDerOutputStream);
/* 176 */       paramDerOutputStream.writeImplicit(
/* 177 */         DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream);
/*     */     }
/*     */     else
/*     */     {
/* 181 */       this.relativeName.encode(localDerOutputStream);
/* 182 */       paramDerOutputStream.writeImplicit(
/* 183 */         DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream);
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
/* 196 */     if (this == paramObject) {
/* 197 */       return true;
/*     */     }
/* 199 */     if (!(paramObject instanceof DistributionPointName)) {
/* 200 */       return false;
/*     */     }
/* 202 */     DistributionPointName localDistributionPointName = (DistributionPointName)paramObject;
/*     */     
/* 204 */     return (Objects.equals(this.fullName, localDistributionPointName.fullName)) && 
/* 205 */       (Objects.equals(this.relativeName, localDistributionPointName.relativeName));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 214 */     int i = this.hashCode;
/* 215 */     if (i == 0) {
/* 216 */       i = 1;
/* 217 */       if (this.fullName != null) {
/* 218 */         i += this.fullName.hashCode();
/*     */       }
/*     */       else {
/* 221 */         i += this.relativeName.hashCode();
/*     */       }
/* 223 */       this.hashCode = i;
/*     */     }
/* 225 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 232 */     StringBuilder localStringBuilder = new StringBuilder();
/* 233 */     if (this.fullName != null) {
/* 234 */       localStringBuilder.append("DistributionPointName:\n     " + this.fullName + "\n");
/*     */     }
/*     */     else {
/* 237 */       localStringBuilder.append("DistributionPointName:\n     " + this.relativeName + "\n");
/*     */     }
/*     */     
/* 240 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\DistributionPointName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
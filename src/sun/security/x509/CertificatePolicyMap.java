/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ public class CertificatePolicyMap
/*     */ {
/*     */   private CertificatePolicyId issuerDomain;
/*     */   private CertificatePolicyId subjectDomain;
/*     */   
/*     */   public CertificatePolicyMap(CertificatePolicyId paramCertificatePolicyId1, CertificatePolicyId paramCertificatePolicyId2)
/*     */   {
/*  50 */     this.issuerDomain = paramCertificatePolicyId1;
/*  51 */     this.subjectDomain = paramCertificatePolicyId2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertificatePolicyMap(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/*  60 */     if (paramDerValue.tag != 48) {
/*  61 */       throw new IOException("Invalid encoding for CertificatePolicyMap");
/*     */     }
/*  63 */     this.issuerDomain = new CertificatePolicyId(paramDerValue.data.getDerValue());
/*  64 */     this.subjectDomain = new CertificatePolicyId(paramDerValue.data.getDerValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public CertificatePolicyId getIssuerIdentifier()
/*     */   {
/*  71 */     return this.issuerDomain;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public CertificatePolicyId getSubjectIdentifier()
/*     */   {
/*  78 */     return this.subjectDomain;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/*  87 */     String str = "CertificatePolicyMap: [\nIssuerDomain:" + this.issuerDomain.toString() + "SubjectDomain:" + this.subjectDomain.toString() + "]\n";
/*     */     
/*     */ 
/*  90 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 100 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */     
/* 102 */     this.issuerDomain.encode(localDerOutputStream);
/* 103 */     this.subjectDomain.encode(localDerOutputStream);
/* 104 */     paramDerOutputStream.write((byte)48, localDerOutputStream);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\CertificatePolicyMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
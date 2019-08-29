/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Date;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.x509.AuthorityKeyIdentifierExtension;
/*     */ import sun.security.x509.KeyIdentifier;
/*     */ import sun.security.x509.SubjectKeyIdentifierExtension;
/*     */ import sun.security.x509.X509CertImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Vertex
/*     */ {
/*  51 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */   private X509Certificate cert;
/*     */   
/*     */   private int index;
/*     */   
/*     */   private Throwable throwable;
/*     */   
/*     */ 
/*     */   Vertex(X509Certificate paramX509Certificate)
/*     */   {
/*  63 */     this.cert = paramX509Certificate;
/*  64 */     this.index = -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate getCertificate()
/*     */   {
/*  73 */     return this.cert;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getIndex()
/*     */   {
/*  84 */     return this.index;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void setIndex(int paramInt)
/*     */   {
/*  95 */     this.index = paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Throwable getThrowable()
/*     */   {
/* 105 */     return this.throwable;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void setThrowable(Throwable paramThrowable)
/*     */   {
/* 115 */     this.throwable = paramThrowable;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 125 */     return certToString() + throwableToString() + indexToString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String certToString()
/*     */   {
/* 135 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     
/* 137 */     X509CertImpl localX509CertImpl = null;
/*     */     try {
/* 139 */       localX509CertImpl = X509CertImpl.toImpl(this.cert);
/*     */     } catch (CertificateException localCertificateException) {
/* 141 */       if (debug != null) {
/* 142 */         debug.println("Vertex.certToString() unexpected exception");
/* 143 */         localCertificateException.printStackTrace();
/*     */       }
/* 145 */       return localStringBuilder.toString();
/*     */     }
/*     */     
/*     */ 
/* 149 */     localStringBuilder.append("Issuer:     ").append(localX509CertImpl.getIssuerX500Principal()).append("\n");
/* 150 */     localStringBuilder.append("Subject:    ")
/* 151 */       .append(localX509CertImpl.getSubjectX500Principal()).append("\n");
/* 152 */     localStringBuilder.append("SerialNum:  ")
/* 153 */       .append(localX509CertImpl.getSerialNumber().toString(16)).append("\n");
/* 154 */     localStringBuilder.append("Expires:    ")
/* 155 */       .append(localX509CertImpl.getNotAfter().toString()).append("\n");
/* 156 */     boolean[] arrayOfBoolean1 = localX509CertImpl.getIssuerUniqueID();
/* 157 */     int k; if (arrayOfBoolean1 != null) {
/* 158 */       localStringBuilder.append("IssuerUID:  ");
/* 159 */       for (k : arrayOfBoolean1) {
/* 160 */         localStringBuilder.append(k != 0 ? 1 : 0);
/*     */       }
/* 162 */       localStringBuilder.append("\n");
/*     */     }
/* 164 */     ??? = localX509CertImpl.getSubjectUniqueID();
/* 165 */     if (??? != null) {
/* 166 */       localStringBuilder.append("SubjectUID: ");
/* 167 */       for (int m : ???) {
/* 168 */         localStringBuilder.append(m != 0 ? 1 : 0);
/*     */       }
/* 170 */       localStringBuilder.append("\n");
/*     */     }
/*     */     try
/*     */     {
/* 174 */       ??? = localX509CertImpl.getSubjectKeyIdentifierExtension();
/* 175 */       if (??? != null) {
/* 176 */         localObject2 = ((SubjectKeyIdentifierExtension)???).get("key_id");
/*     */         
/* 178 */         localStringBuilder.append("SubjKeyID:  ").append(((KeyIdentifier)localObject2).toString());
/*     */       }
/*     */       
/* 181 */       Object localObject2 = localX509CertImpl.getAuthorityKeyIdentifierExtension();
/* 182 */       if (localObject2 != null) {
/* 183 */         KeyIdentifier localKeyIdentifier = (KeyIdentifier)((AuthorityKeyIdentifierExtension)localObject2).get("key_id");
/*     */         
/* 185 */         localStringBuilder.append("AuthKeyID:  ").append(localKeyIdentifier.toString());
/*     */       }
/*     */     } catch (IOException localIOException) {
/* 188 */       if (debug != null) {
/* 189 */         debug.println("Vertex.certToString() unexpected exception");
/* 190 */         localIOException.printStackTrace();
/*     */       }
/*     */     }
/* 193 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String throwableToString()
/*     */   {
/* 203 */     StringBuilder localStringBuilder = new StringBuilder("Exception:  ");
/* 204 */     if (this.throwable != null) {
/* 205 */       localStringBuilder.append(this.throwable.toString());
/*     */     } else
/* 207 */       localStringBuilder.append("null");
/* 208 */     localStringBuilder.append("\n");
/* 209 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String moreToString()
/*     */   {
/* 220 */     StringBuilder localStringBuilder = new StringBuilder("Last cert?  ");
/* 221 */     localStringBuilder.append(this.index == -1 ? "Yes" : "No");
/* 222 */     localStringBuilder.append("\n");
/* 223 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String indexToString()
/*     */   {
/* 233 */     return "Index:      " + this.index + "\n";
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\Vertex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.GeneralNameInterface;
/*     */ import sun.security.x509.GeneralNames;
/*     */ import sun.security.x509.SubjectAlternativeNameExtension;
/*     */ import sun.security.x509.X500Name;
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
/*     */ class ForwardState
/*     */   implements State
/*     */ {
/*  56 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */ 
/*     */   X500Principal issuerDN;
/*     */   
/*     */ 
/*     */ 
/*     */   X509CertImpl cert;
/*     */   
/*     */ 
/*     */ 
/*     */   HashSet<GeneralNameInterface> subjectNamesTraversed;
/*     */   
/*     */ 
/*     */   int traversedCACerts;
/*     */   
/*     */ 
/*  74 */   private boolean init = true;
/*     */   
/*     */ 
/*     */ 
/*     */   UntrustedChecker untrustedChecker;
/*     */   
/*     */ 
/*     */ 
/*     */   ArrayList<PKIXCertPathChecker> forwardCheckers;
/*     */   
/*     */ 
/*     */ 
/*  86 */   boolean keyParamsNeededFlag = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isInitial()
/*     */   {
/*  96 */     return this.init;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean keyParamsNeeded()
/*     */   {
/* 108 */     return this.keyParamsNeededFlag;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 116 */     StringBuilder localStringBuilder = new StringBuilder();
/* 117 */     localStringBuilder.append("State [");
/* 118 */     localStringBuilder.append("\n  issuerDN of last cert: ").append(this.issuerDN);
/* 119 */     localStringBuilder.append("\n  traversedCACerts: ").append(this.traversedCACerts);
/* 120 */     localStringBuilder.append("\n  init: ").append(String.valueOf(this.init));
/* 121 */     localStringBuilder.append("\n  keyParamsNeeded: ")
/* 122 */       .append(String.valueOf(this.keyParamsNeededFlag));
/* 123 */     localStringBuilder.append("\n  subjectNamesTraversed: \n")
/* 124 */       .append(this.subjectNamesTraversed);
/* 125 */     localStringBuilder.append("]\n");
/* 126 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initState(List<PKIXCertPathChecker> paramList)
/*     */     throws CertPathValidatorException
/*     */   {
/* 137 */     this.subjectNamesTraversed = new HashSet();
/* 138 */     this.traversedCACerts = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */     this.forwardCheckers = new ArrayList();
/* 145 */     for (PKIXCertPathChecker localPKIXCertPathChecker : paramList) {
/* 146 */       if (localPKIXCertPathChecker.isForwardCheckingSupported()) {
/* 147 */         localPKIXCertPathChecker.init(true);
/* 148 */         this.forwardCheckers.add(localPKIXCertPathChecker);
/*     */       }
/*     */     }
/*     */     
/* 152 */     this.init = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateState(X509Certificate paramX509Certificate)
/*     */     throws CertificateException, IOException, CertPathValidatorException
/*     */   {
/* 164 */     if (paramX509Certificate == null) {
/* 165 */       return;
/*     */     }
/* 167 */     X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
/*     */     
/*     */ 
/* 170 */     if (PKIX.isDSAPublicKeyWithoutParams(localX509CertImpl.getPublicKey())) {
/* 171 */       this.keyParamsNeededFlag = true;
/*     */     }
/*     */     
/*     */ 
/* 175 */     this.cert = localX509CertImpl;
/*     */     
/*     */ 
/* 178 */     this.issuerDN = paramX509Certificate.getIssuerX500Principal();
/*     */     
/* 180 */     if (!X509CertImpl.isSelfIssued(paramX509Certificate))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 186 */       if ((!this.init) && (paramX509Certificate.getBasicConstraints() != -1)) {
/* 187 */         this.traversedCACerts += 1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 193 */     if ((this.init) || (!X509CertImpl.isSelfIssued(paramX509Certificate))) {
/* 194 */       X500Principal localX500Principal = paramX509Certificate.getSubjectX500Principal();
/* 195 */       this.subjectNamesTraversed.add(X500Name.asX500Name(localX500Principal));
/*     */       
/*     */       try
/*     */       {
/* 199 */         SubjectAlternativeNameExtension localSubjectAlternativeNameExtension = localX509CertImpl.getSubjectAlternativeNameExtension();
/* 200 */         if (localSubjectAlternativeNameExtension != null) {
/* 201 */           GeneralNames localGeneralNames = localSubjectAlternativeNameExtension.get("subject_name");
/*     */           
/* 203 */           for (GeneralName localGeneralName : localGeneralNames.names()) {
/* 204 */             this.subjectNamesTraversed.add(localGeneralName.getName());
/*     */           }
/*     */         }
/*     */       } catch (IOException localIOException) {
/* 208 */         if (debug != null) {
/* 209 */           debug.println("ForwardState.updateState() unexpected exception");
/*     */           
/* 211 */           localIOException.printStackTrace();
/*     */         }
/* 213 */         throw new CertPathValidatorException(localIOException);
/*     */       }
/*     */     }
/*     */     
/* 217 */     this.init = false;
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
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 233 */       ForwardState localForwardState = (ForwardState)super.clone();
/*     */       
/*     */ 
/*     */ 
/* 237 */       localForwardState.forwardCheckers = ((ArrayList)this.forwardCheckers.clone());
/*     */       
/* 239 */       ListIterator localListIterator = localForwardState.forwardCheckers.listIterator();
/* 240 */       while (localListIterator.hasNext()) {
/* 241 */         PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localListIterator.next();
/* 242 */         if ((localPKIXCertPathChecker instanceof Cloneable)) {
/* 243 */           localListIterator.set((PKIXCertPathChecker)localPKIXCertPathChecker.clone());
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 253 */       localForwardState.subjectNamesTraversed = ((HashSet)this.subjectNamesTraversed.clone());
/* 254 */       return localForwardState;
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 256 */       throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\ForwardState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
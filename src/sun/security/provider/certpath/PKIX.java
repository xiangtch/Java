/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Timestamp;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertPathParameters;
/*     */ import java.security.cert.CertSelector;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.CertStoreException;
/*     */ import java.security.cert.CollectionCertStoreParameters;
/*     */ import java.security.cert.PKIXBuilderParameters;
/*     */ import java.security.cert.PKIXCertPathChecker;
/*     */ import java.security.cert.PKIXParameters;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.security.interfaces.DSAPublicKey;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class PKIX
/*     */ {
/*  43 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */   static boolean isDSAPublicKeyWithoutParams(PublicKey paramPublicKey)
/*     */   {
/*  48 */     return ((paramPublicKey instanceof DSAPublicKey)) && 
/*  49 */       (((DSAPublicKey)paramPublicKey).getParams() == null);
/*     */   }
/*     */   
/*     */   static ValidatorParams checkParams(CertPath paramCertPath, CertPathParameters paramCertPathParameters)
/*     */     throws InvalidAlgorithmParameterException
/*     */   {
/*  55 */     if (!(paramCertPathParameters instanceof PKIXParameters)) {
/*  56 */       throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXParameters");
/*     */     }
/*     */     
/*  59 */     return new ValidatorParams(paramCertPath, (PKIXParameters)paramCertPathParameters);
/*     */   }
/*     */   
/*     */   static BuilderParams checkBuilderParams(CertPathParameters paramCertPathParameters)
/*     */     throws InvalidAlgorithmParameterException
/*     */   {
/*  65 */     if (!(paramCertPathParameters instanceof PKIXBuilderParameters)) {
/*  66 */       throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXBuilderParameters");
/*     */     }
/*     */     
/*  69 */     return new BuilderParams((PKIXBuilderParameters)paramCertPathParameters);
/*     */   }
/*     */   
/*     */ 
/*     */   static class ValidatorParams
/*     */   {
/*     */     private final PKIXParameters params;
/*     */     
/*     */     private CertPath certPath;
/*     */     
/*     */     private List<PKIXCertPathChecker> checkers;
/*     */     
/*     */     private List<CertStore> stores;
/*     */     private boolean gotDate;
/*     */     private Date date;
/*     */     private Set<String> policies;
/*     */     private boolean gotConstraints;
/*     */     private CertSelector constraints;
/*     */     private Set<TrustAnchor> anchors;
/*     */     private List<X509Certificate> certs;
/*     */     private Timestamp timestamp;
/*     */     private String variant;
/*     */     
/*     */     ValidatorParams(CertPath paramCertPath, PKIXParameters paramPKIXParameters)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/*  95 */       this(paramPKIXParameters);
/*  96 */       if ((!paramCertPath.getType().equals("X.509")) && (!paramCertPath.getType().equals("X509"))) {
/*  97 */         throw new InvalidAlgorithmParameterException("inappropriate CertPath type specified, must be X.509 or X509");
/*     */       }
/*     */       
/* 100 */       this.certPath = paramCertPath;
/*     */     }
/*     */     
/*     */     ValidatorParams(PKIXParameters paramPKIXParameters)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/* 106 */       if ((paramPKIXParameters instanceof PKIXExtendedParameters)) {
/* 107 */         this.timestamp = ((PKIXExtendedParameters)paramPKIXParameters).getTimestamp();
/* 108 */         this.variant = ((PKIXExtendedParameters)paramPKIXParameters).getVariant();
/*     */       }
/*     */       
/* 111 */       this.anchors = paramPKIXParameters.getTrustAnchors();
/*     */       
/*     */ 
/* 114 */       for (TrustAnchor localTrustAnchor : this.anchors) {
/* 115 */         if (localTrustAnchor.getNameConstraints() != null) {
/* 116 */           throw new InvalidAlgorithmParameterException("name constraints in trust anchor not supported");
/*     */         }
/*     */       }
/*     */       
/* 120 */       this.params = paramPKIXParameters;
/*     */     }
/*     */     
/*     */     CertPath certPath() {
/* 124 */       return this.certPath;
/*     */     }
/*     */     
/*     */ 
/* 128 */     void setCertPath(CertPath paramCertPath) { this.certPath = paramCertPath; }
/*     */     
/*     */     List<X509Certificate> certificates() {
/* 131 */       if (this.certs == null) {
/* 132 */         if (this.certPath == null) {
/* 133 */           this.certs = Collections.emptyList();
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 139 */           ArrayList localArrayList = new ArrayList(this.certPath.getCertificates());
/* 140 */           Collections.reverse(localArrayList);
/* 141 */           this.certs = localArrayList;
/*     */         }
/*     */       }
/* 144 */       return this.certs;
/*     */     }
/*     */     
/* 147 */     List<PKIXCertPathChecker> certPathCheckers() { if (this.checkers == null)
/* 148 */         this.checkers = this.params.getCertPathCheckers();
/* 149 */       return this.checkers;
/*     */     }
/*     */     
/* 152 */     List<CertStore> certStores() { if (this.stores == null)
/* 153 */         this.stores = this.params.getCertStores();
/* 154 */       return this.stores;
/*     */     }
/*     */     
/* 157 */     Date date() { if (!this.gotDate) {
/* 158 */         this.date = this.params.getDate();
/* 159 */         if (this.date == null)
/* 160 */           this.date = new Date();
/* 161 */         this.gotDate = true;
/*     */       }
/* 163 */       return this.date;
/*     */     }
/*     */     
/* 166 */     Set<String> initialPolicies() { if (this.policies == null)
/* 167 */         this.policies = this.params.getInitialPolicies();
/* 168 */       return this.policies;
/*     */     }
/*     */     
/* 171 */     CertSelector targetCertConstraints() { if (!this.gotConstraints) {
/* 172 */         this.constraints = this.params.getTargetCertConstraints();
/* 173 */         this.gotConstraints = true;
/*     */       }
/* 175 */       return this.constraints;
/*     */     }
/*     */     
/* 178 */     Set<TrustAnchor> trustAnchors() { return this.anchors; }
/*     */     
/*     */     boolean revocationEnabled() {
/* 181 */       return this.params.isRevocationEnabled();
/*     */     }
/*     */     
/* 184 */     boolean policyMappingInhibited() { return this.params.isPolicyMappingInhibited(); }
/*     */     
/*     */     boolean explicitPolicyRequired() {
/* 187 */       return this.params.isExplicitPolicyRequired();
/*     */     }
/*     */     
/* 190 */     boolean policyQualifiersRejected() { return this.params.getPolicyQualifiersRejected(); }
/*     */     
/* 192 */     String sigProvider() { return this.params.getSigProvider(); }
/* 193 */     boolean anyPolicyInhibited() { return this.params.isAnyPolicyInhibited(); }
/*     */     
/*     */ 
/*     */     PKIXParameters getPKIXParameters()
/*     */     {
/* 198 */       return this.params;
/*     */     }
/*     */     
/*     */     Timestamp timestamp() {
/* 202 */       return this.timestamp;
/*     */     }
/*     */     
/*     */     String variant() {
/* 206 */       return this.variant;
/*     */     }
/*     */   }
/*     */   
/*     */   static class BuilderParams extends ValidatorParams
/*     */   {
/*     */     private PKIXBuilderParameters params;
/*     */     private List<CertStore> stores;
/*     */     private X500Principal targetSubject;
/*     */     
/*     */     BuilderParams(PKIXBuilderParameters paramPKIXBuilderParameters) throws InvalidAlgorithmParameterException
/*     */     {
/* 218 */       super();
/* 219 */       checkParams(paramPKIXBuilderParameters);
/*     */     }
/*     */     
/*     */     private void checkParams(PKIXBuilderParameters paramPKIXBuilderParameters) throws InvalidAlgorithmParameterException
/*     */     {
/* 224 */       CertSelector localCertSelector = targetCertConstraints();
/* 225 */       if (!(localCertSelector instanceof X509CertSelector)) {
/* 226 */         throw new InvalidAlgorithmParameterException("the targetCertConstraints parameter must be an X509CertSelector");
/*     */       }
/*     */       
/*     */ 
/* 230 */       this.params = paramPKIXBuilderParameters;
/* 231 */       this.targetSubject = getTargetSubject(
/* 232 */         certStores(), (X509CertSelector)targetCertConstraints());
/*     */     }
/*     */     
/* 235 */     List<CertStore> certStores() { if (this.stores == null)
/*     */       {
/* 237 */         this.stores = new ArrayList(this.params.getCertStores());
/* 238 */         Collections.sort(this.stores, new CertStoreComparator(null));
/*     */       }
/* 240 */       return this.stores; }
/*     */     
/* 242 */     int maxPathLength() { return this.params.getMaxPathLength(); }
/* 243 */     PKIXBuilderParameters params() { return this.params; }
/* 244 */     X500Principal targetSubject() { return this.targetSubject; }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static X500Principal getTargetSubject(List<CertStore> paramList, X509CertSelector paramX509CertSelector)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/* 254 */       X500Principal localX500Principal = paramX509CertSelector.getSubject();
/* 255 */       if (localX500Principal != null) {
/* 256 */         return localX500Principal;
/*     */       }
/* 258 */       X509Certificate localX509Certificate1 = paramX509CertSelector.getCertificate();
/* 259 */       if (localX509Certificate1 != null) {
/* 260 */         localX500Principal = localX509Certificate1.getSubjectX500Principal();
/*     */       }
/* 262 */       if (localX500Principal != null) {
/* 263 */         return localX500Principal;
/*     */       }
/* 265 */       for (CertStore localCertStore : paramList)
/*     */       {
/*     */         try
/*     */         {
/* 269 */           Collection localCollection = localCertStore.getCertificates(paramX509CertSelector);
/* 270 */           if (!localCollection.isEmpty())
/*     */           {
/* 272 */             X509Certificate localX509Certificate2 = (X509Certificate)localCollection.iterator().next();
/* 273 */             return localX509Certificate2.getSubjectX500Principal();
/*     */           }
/*     */         }
/*     */         catch (CertStoreException localCertStoreException) {
/* 277 */           if (PKIX.debug != null) {
/* 278 */             PKIX.debug.println("BuilderParams.getTargetSubjectDN: non-fatal exception retrieving certs: " + localCertStoreException);
/*     */             
/* 280 */             localCertStoreException.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/* 284 */       throw new InvalidAlgorithmParameterException("Could not determine unique target subject");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static class CertStoreTypeException
/*     */     extends CertStoreException
/*     */   {
/*     */     private static final long serialVersionUID = 7463352639238322556L;
/*     */     
/*     */     private final String type;
/*     */     
/*     */ 
/*     */     CertStoreTypeException(String paramString, CertStoreException paramCertStoreException)
/*     */     {
/* 299 */       super(paramCertStoreException.getCause());
/* 300 */       this.type = paramString;
/*     */     }
/*     */     
/* 303 */     String getType() { return this.type; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class CertStoreComparator
/*     */     implements Comparator<CertStore>
/*     */   {
/*     */     public int compare(CertStore paramCertStore1, CertStore paramCertStore2)
/*     */     {
/* 314 */       if ((paramCertStore1.getType().equals("Collection")) || 
/* 315 */         ((paramCertStore1.getCertStoreParameters() instanceof CollectionCertStoreParameters)))
/*     */       {
/* 317 */         return -1;
/*     */       }
/* 319 */       return 1;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\PKIX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
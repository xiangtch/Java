/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.cert.CRLReason;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertPathValidatorException.BasicReason;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.Extension;
/*     */ import java.security.cert.TrustAnchor;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import sun.security.action.GetIntegerAction;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.AccessDescription;
/*     */ import sun.security.x509.AuthorityInfoAccessExtension;
/*     */ import sun.security.x509.GeneralName;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ import sun.security.x509.URIName;
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
/*     */ public final class OCSP
/*     */ {
/*  69 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
/*     */   
/*     */ 
/*     */ 
/*  78 */   private static final int CONNECT_TIMEOUT = initializeTimeout();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int initializeTimeout()
/*     */   {
/*  86 */     Integer localInteger = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.ocsp.timeout"));
/*     */     
/*  88 */     if ((localInteger == null) || (localInteger.intValue() < 0)) {
/*  89 */       return 15000;
/*     */     }
/*     */     
/*     */ 
/*  93 */     return localInteger.intValue() * 1000;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static RevocationStatus check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, URI paramURI, X509Certificate paramX509Certificate3, Date paramDate)
/*     */     throws IOException, CertPathValidatorException
/*     */   {
/* 123 */     return check(paramX509Certificate1, paramX509Certificate2, paramURI, paramX509Certificate3, paramDate, 
/* 124 */       Collections.emptyList(), "generic");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static RevocationStatus check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, URI paramURI, X509Certificate paramX509Certificate3, Date paramDate, List<Extension> paramList, String paramString)
/*     */     throws IOException, CertPathValidatorException
/*     */   {
/* 134 */     return check(paramX509Certificate1, paramURI, null, paramX509Certificate2, paramX509Certificate3, paramDate, paramList, paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static RevocationStatus check(X509Certificate paramX509Certificate1, URI paramURI, TrustAnchor paramTrustAnchor, X509Certificate paramX509Certificate2, X509Certificate paramX509Certificate3, Date paramDate, List<Extension> paramList, String paramString)
/*     */     throws IOException, CertPathValidatorException
/*     */   {
/*     */     CertId localCertId;
/*     */     
/*     */     try
/*     */     {
/* 146 */       X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate1);
/* 147 */       localCertId = new CertId(paramX509Certificate2, localX509CertImpl.getSerialNumberObject());
/*     */     } catch (CertificateException|IOException localCertificateException) {
/* 149 */       throw new CertPathValidatorException("Exception while encoding OCSPRequest", localCertificateException);
/*     */     }
/*     */     
/* 152 */     OCSPResponse localOCSPResponse = check(Collections.singletonList(localCertId), paramURI, new OCSPResponse.IssuerInfo(paramTrustAnchor, paramX509Certificate2), paramX509Certificate3, paramDate, paramList, paramString);
/*     */     
/*     */ 
/* 155 */     return localOCSPResponse.getSingleResponse(localCertId);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static OCSPResponse check(List<CertId> paramList, URI paramURI, OCSPResponse.IssuerInfo paramIssuerInfo, X509Certificate paramX509Certificate, Date paramDate, List<Extension> paramList1, String paramString)
/*     */     throws IOException, CertPathValidatorException
/*     */   {
/* 182 */     byte[] arrayOfByte = null;
/* 183 */     for (Object localObject1 = paramList1.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (Extension)((Iterator)localObject1).next();
/* 184 */       if (((Extension)localObject2).getId().equals(PKIXExtensions.OCSPNonce_Id.toString())) {
/* 185 */         arrayOfByte = ((Extension)localObject2).getValue();
/*     */       }
/*     */     }
/*     */     Object localObject2;
/* 189 */     localObject1 = null;
/*     */     try {
/* 191 */       localObject2 = getOCSPBytes(paramList, paramURI, paramList1);
/* 192 */       localObject1 = new OCSPResponse((byte[])localObject2);
/*     */       
/*     */ 
/* 195 */       ((OCSPResponse)localObject1).verify(paramList, paramIssuerInfo, paramX509Certificate, paramDate, arrayOfByte, paramString);
/*     */     }
/*     */     catch (IOException localIOException) {
/* 198 */       throw new CertPathValidatorException("Unable to determine revocation status due to network error", localIOException, null, -1, BasicReason.UNDETERMINED_REVOCATION_STATUS);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 203 */     return (OCSPResponse)localObject1;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] getOCSPBytes(List<CertId> paramList, URI paramURI, List<Extension> paramList1)
/*     */     throws IOException
/*     */   {
/* 223 */     OCSPRequest localOCSPRequest = new OCSPRequest(paramList, paramList1);
/* 224 */     byte[] arrayOfByte1 = localOCSPRequest.encodeBytes();
/*     */     
/* 226 */     InputStream localInputStream = null;
/* 227 */     OutputStream localOutputStream = null;
/* 228 */     arrayOfByte2 = null;
/*     */     try
/*     */     {
/* 231 */       URL localURL = paramURI.toURL();
/* 232 */       if (debug != null) {
/* 233 */         debug.println("connecting to OCSP service at: " + localURL);
/*     */       }
/* 235 */       HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURL.openConnection();
/* 236 */       localHttpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
/* 237 */       localHttpURLConnection.setReadTimeout(CONNECT_TIMEOUT);
/* 238 */       localHttpURLConnection.setDoOutput(true);
/* 239 */       localHttpURLConnection.setDoInput(true);
/* 240 */       localHttpURLConnection.setRequestMethod("POST");
/* 241 */       localHttpURLConnection
/* 242 */         .setRequestProperty("Content-type", "application/ocsp-request");
/* 243 */       localHttpURLConnection
/* 244 */         .setRequestProperty("Content-length", String.valueOf(arrayOfByte1.length));
/* 245 */       localOutputStream = localHttpURLConnection.getOutputStream();
/* 246 */       localOutputStream.write(arrayOfByte1);
/* 247 */       localOutputStream.flush();
/*     */       
/* 249 */       if ((debug != null) && 
/* 250 */         (localHttpURLConnection.getResponseCode() != 200)) {
/* 251 */         debug.println("Received HTTP error: " + localHttpURLConnection.getResponseCode() + " - " + localHttpURLConnection
/* 252 */           .getResponseMessage());
/*     */       }
/* 254 */       localInputStream = localHttpURLConnection.getInputStream();
/* 255 */       int i = localHttpURLConnection.getContentLength();
/* 256 */       if (i == -1) {
/* 257 */         i = Integer.MAX_VALUE;
/*     */       }
/* 259 */       arrayOfByte2 = new byte[i > 2048 ? 2048 : i];
/* 260 */       int j = 0;
/* 261 */       while (j < i) {
/* 262 */         int k = localInputStream.read(arrayOfByte2, j, arrayOfByte2.length - j);
/* 263 */         if (k < 0) {
/*     */           break;
/*     */         }
/* 266 */         j += k;
/* 267 */         if ((j >= arrayOfByte2.length) && (j < i)) {
/* 268 */           arrayOfByte2 = Arrays.copyOf(arrayOfByte2, j * 2);
/*     */         }
/*     */       }
/* 271 */       return Arrays.copyOf(arrayOfByte2, j);
/*     */     } finally {
/* 273 */       if (localInputStream != null) {
/*     */         try {
/* 275 */           localInputStream.close();
/*     */         } catch (IOException localIOException3) {
/* 277 */           throw localIOException3;
/*     */         }
/*     */       }
/* 280 */       if (localOutputStream != null) {
/*     */         try {
/* 282 */           localOutputStream.close();
/*     */         } catch (IOException localIOException4) {
/* 284 */           throw localIOException4;
/*     */         }
/*     */       }
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
/*     */ 
/*     */   public static URI getResponderURI(X509Certificate paramX509Certificate)
/*     */   {
/*     */     try
/*     */     {
/* 302 */       return getResponderURI(X509CertImpl.toImpl(paramX509Certificate));
/*     */     }
/*     */     catch (CertificateException localCertificateException) {}
/* 305 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static URI getResponderURI(X509CertImpl paramX509CertImpl)
/*     */   {
/* 313 */     AuthorityInfoAccessExtension localAuthorityInfoAccessExtension = paramX509CertImpl.getAuthorityInfoAccessExtension();
/* 314 */     if (localAuthorityInfoAccessExtension == null) {
/* 315 */       return null;
/*     */     }
/*     */     
/* 318 */     List localList = localAuthorityInfoAccessExtension.getAccessDescriptions();
/* 319 */     for (AccessDescription localAccessDescription : localList) {
/* 320 */       if (localAccessDescription.getAccessMethod().equals(AccessDescription.Ad_OCSP_Id))
/*     */       {
/*     */ 
/* 323 */         GeneralName localGeneralName = localAccessDescription.getAccessLocation();
/* 324 */         if (localGeneralName.getType() == 6) {
/* 325 */           URIName localURIName = (URIName)localGeneralName.getName();
/* 326 */           return localURIName.getURI();
/*     */         }
/*     */       }
/*     */     }
/* 330 */     return null;
/*     */   }
/*     */   
/*     */   public static abstract interface RevocationStatus { public abstract CertStatus getCertStatus();
/*     */     
/*     */     public abstract Date getRevocationTime();
/*     */     
/* 337 */     public static enum CertStatus { GOOD,  REVOKED,  UNKNOWN;
/*     */       
/*     */       private CertStatus() {}
/*     */     }
/*     */     
/*     */     public abstract CRLReason getRevocationReason();
/*     */     
/*     */     public abstract Map<String, Extension> getSingleExtensions();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\OCSP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
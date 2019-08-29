/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.PublicKey;
/*     */ import java.security.cert.CertificateEncodingException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.security.interfaces.DSAPublicKey;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import sun.security.provider.X509Factory;
/*     */ import sun.security.util.Cache;
/*     */ import sun.security.util.Cache.EqualByteArray;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class X509CertificatePair
/*     */ {
/*     */   private static final byte TAG_FORWARD = 0;
/*     */   private static final byte TAG_REVERSE = 1;
/*     */   private X509Certificate forward;
/*     */   private X509Certificate reverse;
/*     */   private byte[] encoded;
/*  83 */   private static final Cache<Object, X509CertificatePair> cache = Cache.newSoftMemoryCache(750);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CertificatePair() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CertificatePair(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2)
/*     */     throws CertificateException
/*     */   {
/* 102 */     if ((paramX509Certificate1 == null) && (paramX509Certificate2 == null)) {
/* 103 */       throw new CertificateException("at least one of certificate pair must be non-null");
/*     */     }
/*     */     
/*     */ 
/* 107 */     this.forward = paramX509Certificate1;
/* 108 */     this.reverse = paramX509Certificate2;
/*     */     
/* 110 */     checkPair();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private X509CertificatePair(byte[] paramArrayOfByte)
/*     */     throws CertificateException
/*     */   {
/*     */     try
/*     */     {
/* 120 */       parse(new DerValue(paramArrayOfByte));
/* 121 */       this.encoded = paramArrayOfByte;
/*     */     } catch (IOException localIOException) {
/* 123 */       throw new CertificateException(localIOException.toString());
/*     */     }
/* 125 */     checkPair();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static synchronized void clearCache()
/*     */   {
/* 132 */     cache.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized X509CertificatePair generateCertificatePair(byte[] paramArrayOfByte)
/*     */     throws CertificateException
/*     */   {
/* 141 */     EqualByteArray localEqualByteArray = new EqualByteArray(paramArrayOfByte);
/* 142 */     X509CertificatePair localX509CertificatePair = (X509CertificatePair)cache.get(localEqualByteArray);
/* 143 */     if (localX509CertificatePair != null) {
/* 144 */       return localX509CertificatePair;
/*     */     }
/* 146 */     localX509CertificatePair = new X509CertificatePair(paramArrayOfByte);
/* 147 */     localEqualByteArray = new EqualByteArray(localX509CertificatePair.encoded);
/* 148 */     cache.put(localEqualByteArray, localX509CertificatePair);
/* 149 */     return localX509CertificatePair;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setForward(X509Certificate paramX509Certificate)
/*     */     throws CertificateException
/*     */   {
/* 156 */     checkPair();
/* 157 */     this.forward = paramX509Certificate;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setReverse(X509Certificate paramX509Certificate)
/*     */     throws CertificateException
/*     */   {
/* 164 */     checkPair();
/* 165 */     this.reverse = paramX509Certificate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate getForward()
/*     */   {
/* 174 */     return this.forward;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509Certificate getReverse()
/*     */   {
/* 183 */     return this.reverse;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getEncoded()
/*     */     throws CertificateEncodingException
/*     */   {
/*     */     try
/*     */     {
/* 194 */       if (this.encoded == null) {
/* 195 */         DerOutputStream localDerOutputStream = new DerOutputStream();
/* 196 */         emit(localDerOutputStream);
/* 197 */         this.encoded = localDerOutputStream.toByteArray();
/*     */       }
/*     */     } catch (IOException localIOException) {
/* 200 */       throw new CertificateEncodingException(localIOException.toString());
/*     */     }
/* 202 */     return this.encoded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 212 */     StringBuilder localStringBuilder = new StringBuilder();
/* 213 */     localStringBuilder.append("X.509 Certificate Pair: [\n");
/* 214 */     if (this.forward != null)
/* 215 */       localStringBuilder.append("  Forward: ").append(this.forward).append("\n");
/* 216 */     if (this.reverse != null)
/* 217 */       localStringBuilder.append("  Reverse: ").append(this.reverse).append("\n");
/* 218 */     localStringBuilder.append("]");
/* 219 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   private void parse(DerValue paramDerValue)
/*     */     throws IOException, CertificateException
/*     */   {
/* 226 */     if (paramDerValue.tag != 48) {
/* 227 */       throw new IOException("Sequence tag missing for X509CertificatePair");
/*     */     }
/*     */     
/*     */ 
/* 231 */     while ((paramDerValue.data != null) && (paramDerValue.data.available() != 0)) {
/* 232 */       DerValue localDerValue = paramDerValue.data.getDerValue();
/* 233 */       int i = (short)(byte)(localDerValue.tag & 0x1F);
/* 234 */       switch (i) {
/*     */       case 0: 
/* 236 */         if ((localDerValue.isContextSpecific()) && (localDerValue.isConstructed())) {
/* 237 */           if (this.forward != null) {
/* 238 */             throw new IOException("Duplicate forward certificate in X509CertificatePair");
/*     */           }
/*     */           
/* 241 */           localDerValue = localDerValue.data.getDerValue();
/*     */           
/* 243 */           this.forward = X509Factory.intern(new X509CertImpl(localDerValue.toByteArray()));
/*     */         }
/*     */         break;
/*     */       case 1: 
/* 247 */         if ((localDerValue.isContextSpecific()) && (localDerValue.isConstructed())) {
/* 248 */           if (this.reverse != null) {
/* 249 */             throw new IOException("Duplicate reverse certificate in X509CertificatePair");
/*     */           }
/*     */           
/* 252 */           localDerValue = localDerValue.data.getDerValue();
/*     */           
/* 254 */           this.reverse = X509Factory.intern(new X509CertImpl(localDerValue.toByteArray()));
/*     */         }
/*     */         break;
/*     */       default: 
/* 258 */         throw new IOException("Invalid encoding of X509CertificatePair");
/*     */       }
/*     */       
/*     */     }
/* 262 */     if ((this.forward == null) && (this.reverse == null)) {
/* 263 */       throw new CertificateException("at least one of certificate pair must be non-null");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void emit(DerOutputStream paramDerOutputStream)
/*     */     throws IOException, CertificateEncodingException
/*     */   {
/* 272 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*     */     DerOutputStream localDerOutputStream2;
/* 274 */     if (this.forward != null) {
/* 275 */       localDerOutputStream2 = new DerOutputStream();
/* 276 */       localDerOutputStream2.putDerValue(new DerValue(this.forward.getEncoded()));
/* 277 */       localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
/*     */     }
/*     */     
/*     */ 
/* 281 */     if (this.reverse != null) {
/* 282 */       localDerOutputStream2 = new DerOutputStream();
/* 283 */       localDerOutputStream2.putDerValue(new DerValue(this.reverse.getEncoded()));
/* 284 */       localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
/*     */     }
/*     */     
/*     */ 
/* 288 */     paramDerOutputStream.write((byte)48, localDerOutputStream1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkPair()
/*     */     throws CertificateException
/*     */   {
/* 297 */     if ((this.forward == null) || (this.reverse == null)) {
/* 298 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 304 */     X500Principal localX500Principal1 = this.forward.getSubjectX500Principal();
/* 305 */     X500Principal localX500Principal2 = this.forward.getIssuerX500Principal();
/* 306 */     X500Principal localX500Principal3 = this.reverse.getSubjectX500Principal();
/* 307 */     X500Principal localX500Principal4 = this.reverse.getIssuerX500Principal();
/* 308 */     if ((!localX500Principal2.equals(localX500Principal3)) || (!localX500Principal4.equals(localX500Principal1))) {
/* 309 */       throw new CertificateException("subject and issuer names in forward and reverse certificates do not match");
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 315 */       PublicKey localPublicKey = this.reverse.getPublicKey();
/* 316 */       if ((!(localPublicKey instanceof DSAPublicKey)) || 
/* 317 */         (((DSAPublicKey)localPublicKey).getParams() != null)) {
/* 318 */         this.forward.verify(localPublicKey);
/*     */       }
/* 320 */       localPublicKey = this.forward.getPublicKey();
/* 321 */       if ((!(localPublicKey instanceof DSAPublicKey)) || 
/* 322 */         (((DSAPublicKey)localPublicKey).getParams() != null)) {
/* 323 */         this.reverse.verify(localPublicKey);
/*     */       }
/*     */     }
/*     */     catch (GeneralSecurityException localGeneralSecurityException) {
/* 327 */       throw new CertificateException("invalid signature: " + localGeneralSecurityException.getMessage());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\X509CertificatePair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.cert.Extension;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.PKIXExtensions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class OCSPRequest
/*     */ {
/*  80 */   private static final Debug debug = Debug.getInstance("certpath");
/*  81 */   private static final boolean dump = (debug != null) && (Debug.isOn("ocsp"));
/*     */   
/*     */ 
/*     */   private final List<CertId> certIds;
/*     */   
/*     */   private final List<Extension> extensions;
/*     */   
/*     */   private byte[] nonce;
/*     */   
/*     */ 
/*     */   OCSPRequest(CertId paramCertId)
/*     */   {
/*  93 */     this(Collections.singletonList(paramCertId));
/*     */   }
/*     */   
/*     */   OCSPRequest(List<CertId> paramList) {
/*  97 */     this.certIds = paramList;
/*  98 */     this.extensions = Collections.emptyList();
/*     */   }
/*     */   
/*     */   OCSPRequest(List<CertId> paramList, List<Extension> paramList1) {
/* 102 */     this.certIds = paramList;
/* 103 */     this.extensions = paramList1;
/*     */   }
/*     */   
/*     */   byte[] encodeBytes()
/*     */     throws IOException
/*     */   {
/* 109 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 110 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 111 */     for (Object localObject1 = this.certIds.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (CertId)((Iterator)localObject1).next();
/* 112 */       localObject3 = new DerOutputStream();
/* 113 */       ((CertId)localObject2).encode((DerOutputStream)localObject3);
/* 114 */       localDerOutputStream2.write((byte)48, (DerOutputStream)localObject3);
/*     */     }
/*     */     
/* 117 */     localDerOutputStream1.write((byte)48, localDerOutputStream2);
/* 118 */     if (!this.extensions.isEmpty()) {
/* 119 */       localObject1 = new DerOutputStream();
/* 120 */       for (localObject2 = this.extensions.iterator(); ((Iterator)localObject2).hasNext();) { localObject3 = (Extension)((Iterator)localObject2).next();
/* 121 */         ((Extension)localObject3).encode((OutputStream)localObject1);
/* 122 */         if (((Extension)localObject3).getId().equals(PKIXExtensions.OCSPNonce_Id
/* 123 */           .toString())) {
/* 124 */           this.nonce = ((Extension)localObject3).getValue();
/*     */         }
/*     */       }
/* 127 */       localObject2 = new DerOutputStream();
/* 128 */       ((DerOutputStream)localObject2).write((byte)48, (DerOutputStream)localObject1);
/* 129 */       localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), (DerOutputStream)localObject2);
/*     */     }
/*     */     
/*     */ 
/* 133 */     localObject1 = new DerOutputStream();
/* 134 */     ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream1);
/*     */     
/*     */ 
/* 137 */     Object localObject2 = new DerOutputStream();
/* 138 */     ((DerOutputStream)localObject2).write((byte)48, (DerOutputStream)localObject1);
/*     */     
/* 140 */     Object localObject3 = ((DerOutputStream)localObject2).toByteArray();
/*     */     
/* 142 */     if (dump) {
/* 143 */       HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/* 144 */       debug.println("OCSPRequest bytes...\n\n" + localHexDumpEncoder
/* 145 */         .encode((byte[])localObject3) + "\n");
/*     */     }
/*     */     
/* 148 */     return (byte[])localObject3;
/*     */   }
/*     */   
/*     */   List<CertId> getCertIds() {
/* 152 */     return this.certIds;
/*     */   }
/*     */   
/*     */   byte[] getNonce() {
/* 156 */     return this.nonce;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\OCSPRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
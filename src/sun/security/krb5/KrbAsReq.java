/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import sun.security.krb5.internal.ASReq;
/*     */ import sun.security.krb5.internal.HostAddresses;
/*     */ import sun.security.krb5.internal.KDCOptions;
/*     */ import sun.security.krb5.internal.KDCReqBody;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.PAData;
/*     */ import sun.security.krb5.internal.PAEncTSEnc;
/*     */ import sun.security.krb5.internal.crypto.Nonce;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KrbAsReq
/*     */ {
/*     */   private ASReq asReqMessg;
/*  46 */   private boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KrbAsReq(EncryptionKey paramEncryptionKey, KDCOptions paramKDCOptions, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, int[] paramArrayOfInt, HostAddresses paramHostAddresses)
/*     */     throws KrbException, IOException
/*     */   {
/*  64 */     if (paramKDCOptions == null) {
/*  65 */       paramKDCOptions = new KDCOptions();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  70 */     if ((paramKDCOptions.get(2)) || 
/*  71 */       (paramKDCOptions.get(4)) || 
/*  72 */       (paramKDCOptions.get(28)) || 
/*  73 */       (paramKDCOptions.get(30)) || 
/*  74 */       (paramKDCOptions.get(31)))
/*     */     {
/*     */ 
/*  77 */       throw new KrbException(101);
/*     */     }
/*  79 */     if (!paramKDCOptions.get(6))
/*     */     {
/*     */ 
/*     */ 
/*  83 */       if (paramKerberosTime1 != null) paramKerberosTime1 = null;
/*     */     }
/*  85 */     if (!paramKDCOptions.get(8))
/*     */     {
/*     */ 
/*     */ 
/*  89 */       if (paramKerberosTime3 != null) { paramKerberosTime3 = null;
/*     */       }
/*     */     }
/*  92 */     PAData[] arrayOfPAData = null;
/*  93 */     if (paramEncryptionKey != null) {
/*  94 */       localObject = new PAEncTSEnc();
/*  95 */       byte[] arrayOfByte = ((PAEncTSEnc)localObject).asn1Encode();
/*  96 */       EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey, arrayOfByte, 1);
/*     */       
/*  98 */       arrayOfPAData = new PAData[1];
/*  99 */       arrayOfPAData[0] = new PAData(2, localEncryptedData
/* 100 */         .asn1Encode());
/*     */     }
/*     */     
/* 103 */     if (paramPrincipalName1.getRealm() == null) {
/* 104 */       throw new RealmException(601, "default realm not specified ");
/*     */     }
/*     */     
/*     */ 
/* 108 */     if (this.DEBUG) {
/* 109 */       System.out.println(">>> KrbAsReq creating message");
/*     */     }
/*     */     
/*     */ 
/* 113 */     if ((paramHostAddresses == null) && (Config.getInstance().useAddresses())) {
/* 114 */       paramHostAddresses = HostAddresses.getLocalAddresses();
/*     */     }
/*     */     
/* 117 */     if (paramPrincipalName2 == null) {
/* 118 */       localObject = paramPrincipalName1.getRealmAsString();
/* 119 */       paramPrincipalName2 = PrincipalName.tgsService((String)localObject, (String)localObject);
/*     */     }
/*     */     
/* 122 */     if (paramKerberosTime2 == null) {
/* 123 */       paramKerberosTime2 = new KerberosTime(0L);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 133 */     Object localObject = new KDCReqBody(paramKDCOptions, paramPrincipalName1, paramPrincipalName2, paramKerberosTime1, paramKerberosTime2, paramKerberosTime3, Nonce.value(), paramArrayOfInt, paramHostAddresses, null, null);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 139 */     this.asReqMessg = new ASReq(arrayOfPAData, (KDCReqBody)localObject);
/*     */   }
/*     */   
/*     */   byte[] encoding()
/*     */     throws IOException, Asn1Exception
/*     */   {
/* 145 */     return this.asReqMessg.asn1Encode();
/*     */   }
/*     */   
/*     */   ASReq getMessage()
/*     */   {
/* 150 */     return this.asReqMessg;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KrbAsReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
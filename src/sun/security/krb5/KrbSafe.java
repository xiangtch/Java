/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import sun.security.krb5.internal.HostAddress;
/*     */ import sun.security.krb5.internal.KRBSafe;
/*     */ import sun.security.krb5.internal.KRBSafeBody;
/*     */ import sun.security.krb5.internal.KdcErrException;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.KrbApErrException;
/*     */ import sun.security.krb5.internal.SeqNumber;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class KrbSafe
/*     */   extends KrbAppMessage
/*     */ {
/*     */   private byte[] obuf;
/*     */   private byte[] userData;
/*     */   
/*     */   public KrbSafe(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2)
/*     */     throws KrbException, IOException
/*     */   {
/*  52 */     EncryptionKey localEncryptionKey = null;
/*  53 */     if (paramEncryptionKey != null) {
/*  54 */       localEncryptionKey = paramEncryptionKey;
/*     */     } else {
/*  56 */       localEncryptionKey = paramCredentials.key;
/*     */     }
/*  58 */     this.obuf = mk_safe(paramArrayOfByte, localEncryptionKey, paramKerberosTime, paramSeqNumber, paramHostAddress1, paramHostAddress2);
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
/*     */   public KrbSafe(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2)
/*     */     throws KrbException, IOException
/*     */   {
/*  77 */     KRBSafe localKRBSafe = new KRBSafe(paramArrayOfByte);
/*     */     
/*  79 */     EncryptionKey localEncryptionKey = null;
/*  80 */     if (paramEncryptionKey != null) {
/*  81 */       localEncryptionKey = paramEncryptionKey;
/*     */     } else {
/*  83 */       localEncryptionKey = paramCredentials.key;
/*     */     }
/*  85 */     this.userData = rd_safe(localKRBSafe, localEncryptionKey, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, paramCredentials.client);
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
/*     */   public byte[] getMessage()
/*     */   {
/*  98 */     return this.obuf;
/*     */   }
/*     */   
/*     */   public byte[] getData() {
/* 102 */     return this.userData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] mk_safe(byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2)
/*     */     throws Asn1Exception, IOException, KdcErrException, KrbApErrException, KrbCryptoException
/*     */   {
/* 114 */     Integer localInteger1 = null;
/* 115 */     Integer localInteger2 = null;
/*     */     
/* 117 */     if (paramKerberosTime != null) {
/* 118 */       localInteger1 = new Integer(paramKerberosTime.getMicroSeconds());
/*     */     }
/* 120 */     if (paramSeqNumber != null) {
/* 121 */       localInteger2 = new Integer(paramSeqNumber.current());
/* 122 */       paramSeqNumber.step();
/*     */     }
/*     */     
/* 125 */     KRBSafeBody localKRBSafeBody = new KRBSafeBody(paramArrayOfByte, paramKerberosTime, localInteger1, localInteger2, paramHostAddress1, paramHostAddress2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 134 */     byte[] arrayOfByte = localKRBSafeBody.asn1Encode();
/* 135 */     Checksum localChecksum = new Checksum(Checksum.SAFECKSUMTYPE_DEFAULT, arrayOfByte, paramEncryptionKey, 15);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 142 */     KRBSafe localKRBSafe = new KRBSafe(localKRBSafeBody, localChecksum);
/*     */     
/* 144 */     arrayOfByte = localKRBSafe.asn1Encode();
/*     */     
/* 146 */     return localKRBSafe.asn1Encode();
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
/*     */   private byte[] rd_safe(KRBSafe paramKRBSafe, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2, PrincipalName paramPrincipalName)
/*     */     throws Asn1Exception, KdcErrException, KrbApErrException, IOException, KrbCryptoException
/*     */   {
/* 160 */     byte[] arrayOfByte = paramKRBSafe.safeBody.asn1Encode();
/*     */     
/* 162 */     if (!paramKRBSafe.cksum.verifyKeyedChecksum(arrayOfByte, paramEncryptionKey, 15))
/*     */     {
/* 164 */       throw new KrbApErrException(41);
/*     */     }
/*     */     
/*     */ 
/* 168 */     check(paramKRBSafe.safeBody.timestamp, paramKRBSafe.safeBody.usec, paramKRBSafe.safeBody.seqNumber, paramKRBSafe.safeBody.sAddress, paramKRBSafe.safeBody.rAddress, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, paramPrincipalName);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 181 */     return paramKRBSafe.safeBody.userData;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KrbSafe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
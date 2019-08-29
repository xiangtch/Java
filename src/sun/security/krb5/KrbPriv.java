/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import sun.security.krb5.internal.EncKrbPrivPart;
/*     */ import sun.security.krb5.internal.HostAddress;
/*     */ import sun.security.krb5.internal.KRBPriv;
/*     */ import sun.security.krb5.internal.KdcErrException;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.KrbApErrException;
/*     */ import sun.security.krb5.internal.SeqNumber;
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
/*     */ class KrbPriv
/*     */   extends KrbAppMessage
/*     */ {
/*     */   private byte[] obuf;
/*     */   private byte[] userData;
/*     */   
/*     */   private KrbPriv(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2)
/*     */     throws KrbException, IOException
/*     */   {
/*  53 */     EncryptionKey localEncryptionKey = null;
/*  54 */     if (paramEncryptionKey != null) {
/*  55 */       localEncryptionKey = paramEncryptionKey;
/*     */     } else {
/*  57 */       localEncryptionKey = paramCredentials.key;
/*     */     }
/*  59 */     this.obuf = mk_priv(paramArrayOfByte, localEncryptionKey, paramKerberosTime, paramSeqNumber, paramHostAddress1, paramHostAddress2);
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
/*     */   private KrbPriv(byte[] paramArrayOfByte, Credentials paramCredentials, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2)
/*     */     throws KrbException, IOException
/*     */   {
/*  79 */     KRBPriv localKRBPriv = new KRBPriv(paramArrayOfByte);
/*  80 */     EncryptionKey localEncryptionKey = null;
/*  81 */     if (paramEncryptionKey != null) {
/*  82 */       localEncryptionKey = paramEncryptionKey;
/*     */     } else
/*  84 */       localEncryptionKey = paramCredentials.key;
/*  85 */     this.userData = rd_priv(localKRBPriv, localEncryptionKey, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, paramCredentials.client);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getMessage()
/*     */     throws KrbException
/*     */   {
/*  97 */     return this.obuf;
/*     */   }
/*     */   
/*     */   public byte[] getData() {
/* 101 */     return this.userData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] mk_priv(byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2)
/*     */     throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
/*     */   {
/* 113 */     Integer localInteger1 = null;
/* 114 */     Integer localInteger2 = null;
/*     */     
/* 116 */     if (paramKerberosTime != null) {
/* 117 */       localInteger1 = new Integer(paramKerberosTime.getMicroSeconds());
/*     */     }
/* 119 */     if (paramSeqNumber != null) {
/* 120 */       localInteger2 = new Integer(paramSeqNumber.current());
/* 121 */       paramSeqNumber.step();
/*     */     }
/*     */     
/* 124 */     EncKrbPrivPart localEncKrbPrivPart = new EncKrbPrivPart(paramArrayOfByte, paramKerberosTime, localInteger1, localInteger2, paramHostAddress1, paramHostAddress2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 133 */     byte[] arrayOfByte = localEncKrbPrivPart.asn1Encode();
/*     */     
/* 135 */     EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey, arrayOfByte, 13);
/*     */     
/*     */ 
/*     */ 
/* 139 */     KRBPriv localKRBPriv = new KRBPriv(localEncryptedData);
/*     */     
/* 141 */     arrayOfByte = localKRBPriv.asn1Encode();
/*     */     
/* 143 */     return localKRBPriv.asn1Encode();
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
/*     */   private byte[] rd_priv(KRBPriv paramKRBPriv, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, HostAddress paramHostAddress1, HostAddress paramHostAddress2, boolean paramBoolean1, boolean paramBoolean2, PrincipalName paramPrincipalName)
/*     */     throws Asn1Exception, KdcErrException, KrbApErrException, IOException, KrbCryptoException
/*     */   {
/* 157 */     byte[] arrayOfByte1 = paramKRBPriv.encPart.decrypt(paramEncryptionKey, 13);
/*     */     
/* 159 */     byte[] arrayOfByte2 = paramKRBPriv.encPart.reset(arrayOfByte1);
/* 160 */     DerValue localDerValue = new DerValue(arrayOfByte2);
/* 161 */     EncKrbPrivPart localEncKrbPrivPart = new EncKrbPrivPart(localDerValue);
/*     */     
/* 163 */     check(localEncKrbPrivPart.timestamp, localEncKrbPrivPart.usec, localEncKrbPrivPart.seqNumber, localEncKrbPrivPart.sAddress, localEncKrbPrivPart.rAddress, paramSeqNumber, paramHostAddress1, paramHostAddress2, paramBoolean1, paramBoolean2, paramPrincipalName);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 176 */     return localEncKrbPrivPart.userData;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KrbPriv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
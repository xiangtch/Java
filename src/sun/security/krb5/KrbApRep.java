/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import sun.security.krb5.internal.APRep;
/*     */ import sun.security.krb5.internal.EncAPRepPart;
/*     */ import sun.security.krb5.internal.KRBError;
/*     */ import sun.security.krb5.internal.KdcErrException;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.KrbApErrException;
/*     */ import sun.security.krb5.internal.LocalSeqNumber;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KrbApRep
/*     */ {
/*     */   private byte[] obuf;
/*     */   private byte[] ibuf;
/*     */   private EncAPRepPart encPart;
/*     */   private APRep apRepMessg;
/*     */   
/*     */   public KrbApRep(KrbApReq paramKrbApReq, boolean paramBoolean, EncryptionKey paramEncryptionKey)
/*     */     throws KrbException, IOException
/*     */   {
/*  60 */     LocalSeqNumber localLocalSeqNumber = new LocalSeqNumber();
/*     */     
/*  62 */     init(paramKrbApReq, paramEncryptionKey, localLocalSeqNumber);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KrbApRep(byte[] paramArrayOfByte, Credentials paramCredentials, KrbApReq paramKrbApReq)
/*     */     throws KrbException, IOException
/*     */   {
/*  73 */     this(paramArrayOfByte, paramCredentials);
/*  74 */     authenticate(paramKrbApReq);
/*     */   }
/*     */   
/*     */ 
/*     */   private void init(KrbApReq paramKrbApReq, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber)
/*     */     throws KrbException, IOException
/*     */   {
/*  81 */     createMessage(
/*  82 */       paramKrbApReq.getCreds().key, paramKrbApReq
/*  83 */       .getCtime(), paramKrbApReq
/*  84 */       .cusec(), paramEncryptionKey, paramSeqNumber);
/*     */     
/*     */ 
/*  87 */     this.obuf = this.apRepMessg.asn1Encode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private KrbApRep(byte[] paramArrayOfByte, Credentials paramCredentials)
/*     */     throws KrbException, IOException
/*     */   {
/* 100 */     this(new DerValue(paramArrayOfByte), paramCredentials);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private KrbApRep(DerValue paramDerValue, Credentials paramCredentials)
/*     */     throws KrbException, IOException
/*     */   {
/* 112 */     APRep localAPRep = null;
/*     */     try {
/* 114 */       localAPRep = new APRep(paramDerValue);
/*     */     } catch (Asn1Exception localAsn1Exception) {
/* 116 */       localAPRep = null;
/* 117 */       localObject = new KRBError(paramDerValue);
/* 118 */       String str1 = ((KRBError)localObject).getErrorString();
/*     */       String str2;
/* 120 */       if (str1.charAt(str1.length() - 1) == 0) {
/* 121 */         str2 = str1.substring(0, str1.length() - 1);
/*     */       } else
/* 123 */         str2 = str1;
/* 124 */       KrbException localKrbException = new KrbException(((KRBError)localObject).getErrorCode(), str2);
/* 125 */       localKrbException.initCause(localAsn1Exception);
/* 126 */       throw localKrbException;
/*     */     }
/*     */     
/* 129 */     byte[] arrayOfByte = localAPRep.encPart.decrypt(paramCredentials.key, 12);
/*     */     
/* 131 */     Object localObject = localAPRep.encPart.reset(arrayOfByte);
/*     */     
/* 133 */     paramDerValue = new DerValue((byte[])localObject);
/* 134 */     this.encPart = new EncAPRepPart(paramDerValue);
/*     */   }
/*     */   
/*     */   private void authenticate(KrbApReq paramKrbApReq) throws KrbException, IOException
/*     */   {
/* 139 */     if ((this.encPart.ctime.getSeconds() != paramKrbApReq.getCtime().getSeconds()) || 
/* 140 */       (this.encPart.cusec != paramKrbApReq.getCtime().getMicroSeconds())) {
/* 141 */       throw new KrbApErrException(46);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncryptionKey getSubKey()
/*     */   {
/* 151 */     return this.encPart.getSubKey();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Integer getSeqNumber()
/*     */   {
/* 161 */     return this.encPart.getSeqNumber();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getMessage()
/*     */   {
/* 168 */     return this.obuf;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createMessage(EncryptionKey paramEncryptionKey1, KerberosTime paramKerberosTime, int paramInt, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber)
/*     */     throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
/*     */   {
/* 180 */     Integer localInteger = null;
/*     */     
/* 182 */     if (paramSeqNumber != null) {
/* 183 */       localInteger = new Integer(paramSeqNumber.current());
/*     */     }
/* 185 */     this.encPart = new EncAPRepPart(paramKerberosTime, paramInt, paramEncryptionKey2, localInteger);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 190 */     byte[] arrayOfByte = this.encPart.asn1Encode();
/*     */     
/* 192 */     EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey1, arrayOfByte, 12);
/*     */     
/*     */ 
/* 195 */     this.apRepMessg = new APRep(localEncryptedData);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KrbApRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
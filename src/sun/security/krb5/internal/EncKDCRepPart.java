/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.Realm;
/*     */ import sun.security.krb5.RealmException;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EncKDCRepPart
/*     */ {
/*     */   public EncryptionKey key;
/*     */   public LastReq lastReq;
/*     */   public int nonce;
/*     */   public KerberosTime keyExpiration;
/*     */   public TicketFlags flags;
/*     */   public KerberosTime authtime;
/*     */   public KerberosTime starttime;
/*     */   public KerberosTime endtime;
/*     */   public KerberosTime renewTill;
/*     */   public PrincipalName sname;
/*     */   public HostAddresses caddr;
/*     */   public int msgType;
/*     */   
/*     */   public EncKDCRepPart(EncryptionKey paramEncryptionKey, LastReq paramLastReq, int paramInt1, KerberosTime paramKerberosTime1, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, KerberosTime paramKerberosTime5, PrincipalName paramPrincipalName, HostAddresses paramHostAddresses, int paramInt2)
/*     */   {
/*  94 */     this.key = paramEncryptionKey;
/*  95 */     this.lastReq = paramLastReq;
/*  96 */     this.nonce = paramInt1;
/*  97 */     this.keyExpiration = paramKerberosTime1;
/*  98 */     this.flags = paramTicketFlags;
/*  99 */     this.authtime = paramKerberosTime2;
/* 100 */     this.starttime = paramKerberosTime3;
/* 101 */     this.endtime = paramKerberosTime4;
/* 102 */     this.renewTill = paramKerberosTime5;
/* 103 */     this.sname = paramPrincipalName;
/* 104 */     this.caddr = paramHostAddresses;
/* 105 */     this.msgType = paramInt2;
/*     */   }
/*     */   
/*     */   public EncKDCRepPart() {}
/*     */   
/*     */   public EncKDCRepPart(byte[] paramArrayOfByte, int paramInt)
/*     */     throws Asn1Exception, IOException, RealmException
/*     */   {
/* 113 */     init(new DerValue(paramArrayOfByte), paramInt);
/*     */   }
/*     */   
/*     */   public EncKDCRepPart(DerValue paramDerValue, int paramInt) throws Asn1Exception, IOException, RealmException
/*     */   {
/* 118 */     init(paramDerValue, paramInt);
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
/*     */   protected void init(DerValue paramDerValue, int paramInt)
/*     */     throws Asn1Exception, IOException, RealmException
/*     */   {
/* 135 */     this.msgType = (paramDerValue.getTag() & 0x1F);
/* 136 */     if ((this.msgType != 25) && (this.msgType != 26))
/*     */     {
/* 138 */       throw new Asn1Exception(906);
/*     */     }
/* 140 */     DerValue localDerValue1 = paramDerValue.getData().getDerValue();
/* 141 */     if (localDerValue1.getTag() != 48) {
/* 142 */       throw new Asn1Exception(906);
/*     */     }
/* 144 */     this.key = EncryptionKey.parse(localDerValue1.getData(), (byte)0, false);
/* 145 */     this.lastReq = LastReq.parse(localDerValue1.getData(), (byte)1, false);
/* 146 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 147 */     if ((localDerValue2.getTag() & 0x1F) == 2) {
/* 148 */       this.nonce = localDerValue2.getData().getBigInteger().intValue();
/*     */     } else {
/* 150 */       throw new Asn1Exception(906);
/*     */     }
/* 152 */     this.keyExpiration = KerberosTime.parse(localDerValue1.getData(), (byte)3, true);
/* 153 */     this.flags = TicketFlags.parse(localDerValue1.getData(), (byte)4, false);
/* 154 */     this.authtime = KerberosTime.parse(localDerValue1.getData(), (byte)5, false);
/* 155 */     this.starttime = KerberosTime.parse(localDerValue1.getData(), (byte)6, true);
/* 156 */     this.endtime = KerberosTime.parse(localDerValue1.getData(), (byte)7, false);
/* 157 */     this.renewTill = KerberosTime.parse(localDerValue1.getData(), (byte)8, true);
/* 158 */     Realm localRealm = Realm.parse(localDerValue1.getData(), (byte)9, false);
/* 159 */     this.sname = PrincipalName.parse(localDerValue1.getData(), (byte)10, false, localRealm);
/* 160 */     if (localDerValue1.getData().available() > 0) {
/* 161 */       this.caddr = HostAddresses.parse(localDerValue1.getData(), (byte)11, true);
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
/*     */ 
/*     */ 
/*     */   public byte[] asn1Encode(int paramInt)
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 178 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 179 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 180 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), this.key
/* 181 */       .asn1Encode());
/* 182 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), this.lastReq
/* 183 */       .asn1Encode());
/* 184 */     localDerOutputStream1.putInteger(BigInteger.valueOf(this.nonce));
/* 185 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream1);
/*     */     
/*     */ 
/* 188 */     if (this.keyExpiration != null) {
/* 189 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), this.keyExpiration
/* 190 */         .asn1Encode());
/*     */     }
/* 192 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), this.flags
/* 193 */       .asn1Encode());
/* 194 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), this.authtime
/* 195 */       .asn1Encode());
/* 196 */     if (this.starttime != null) {
/* 197 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), this.starttime
/* 198 */         .asn1Encode());
/*     */     }
/* 200 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)7), this.endtime
/* 201 */       .asn1Encode());
/* 202 */     if (this.renewTill != null) {
/* 203 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)8), this.renewTill
/* 204 */         .asn1Encode());
/*     */     }
/* 206 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)9), this.sname
/* 207 */       .getRealm().asn1Encode());
/* 208 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)10), this.sname
/* 209 */       .asn1Encode());
/* 210 */     if (this.caddr != null) {
/* 211 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)11), this.caddr
/* 212 */         .asn1Encode());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 217 */     localDerOutputStream1 = new DerOutputStream();
/* 218 */     localDerOutputStream1.write((byte)48, localDerOutputStream2);
/* 219 */     localDerOutputStream2 = new DerOutputStream();
/* 220 */     localDerOutputStream2.write(DerValue.createTag((byte)64, true, (byte)this.msgType), localDerOutputStream1);
/*     */     
/* 222 */     return localDerOutputStream2.toByteArray();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\EncKDCRepPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
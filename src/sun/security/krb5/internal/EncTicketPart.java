/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.KrbException;
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
/*     */ public class EncTicketPart
/*     */ {
/*     */   public TicketFlags flags;
/*     */   public EncryptionKey key;
/*     */   public PrincipalName cname;
/*     */   public TransitedEncoding transited;
/*     */   public KerberosTime authtime;
/*     */   public KerberosTime starttime;
/*     */   public KerberosTime endtime;
/*     */   public KerberosTime renewTill;
/*     */   public HostAddresses caddr;
/*     */   public AuthorizationData authorizationData;
/*     */   
/*     */   public EncTicketPart(TicketFlags paramTicketFlags, EncryptionKey paramEncryptionKey, PrincipalName paramPrincipalName, TransitedEncoding paramTransitedEncoding, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData)
/*     */   {
/*  88 */     this.flags = paramTicketFlags;
/*  89 */     this.key = paramEncryptionKey;
/*  90 */     this.cname = paramPrincipalName;
/*  91 */     this.transited = paramTransitedEncoding;
/*  92 */     this.authtime = paramKerberosTime1;
/*  93 */     this.starttime = paramKerberosTime2;
/*  94 */     this.endtime = paramKerberosTime3;
/*  95 */     this.renewTill = paramKerberosTime4;
/*  96 */     this.caddr = paramHostAddresses;
/*  97 */     this.authorizationData = paramAuthorizationData;
/*     */   }
/*     */   
/*     */   public EncTicketPart(byte[] paramArrayOfByte) throws Asn1Exception, KrbException, IOException
/*     */   {
/* 102 */     init(new DerValue(paramArrayOfByte));
/*     */   }
/*     */   
/*     */   public EncTicketPart(DerValue paramDerValue) throws Asn1Exception, KrbException, IOException
/*     */   {
/* 107 */     init(paramDerValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getHexBytes(byte[] paramArrayOfByte, int paramInt)
/*     */     throws IOException
/*     */   {
/* 120 */     StringBuffer localStringBuffer = new StringBuffer();
/* 121 */     for (int i = 0; i < paramInt; i++)
/*     */     {
/* 123 */       int j = paramArrayOfByte[i] >> 4 & 0xF;
/* 124 */       int k = paramArrayOfByte[i] & 0xF;
/*     */       
/* 126 */       localStringBuffer.append(Integer.toHexString(j));
/* 127 */       localStringBuffer.append(Integer.toHexString(k));
/* 128 */       localStringBuffer.append(' ');
/*     */     }
/* 130 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   private void init(DerValue paramDerValue)
/*     */     throws Asn1Exception, IOException, RealmException
/*     */   {
/* 137 */     this.renewTill = null;
/* 138 */     this.caddr = null;
/* 139 */     this.authorizationData = null;
/* 140 */     if (((paramDerValue.getTag() & 0x1F) != 3) || 
/* 141 */       (paramDerValue.isApplication() != true) || 
/* 142 */       (paramDerValue.isConstructed() != true)) {
/* 143 */       throw new Asn1Exception(906);
/*     */     }
/* 145 */     DerValue localDerValue = paramDerValue.getData().getDerValue();
/* 146 */     if (localDerValue.getTag() != 48) {
/* 147 */       throw new Asn1Exception(906);
/*     */     }
/* 149 */     this.flags = TicketFlags.parse(localDerValue.getData(), (byte)0, false);
/* 150 */     this.key = EncryptionKey.parse(localDerValue.getData(), (byte)1, false);
/* 151 */     Realm localRealm = Realm.parse(localDerValue.getData(), (byte)2, false);
/* 152 */     this.cname = PrincipalName.parse(localDerValue.getData(), (byte)3, false, localRealm);
/* 153 */     this.transited = TransitedEncoding.parse(localDerValue.getData(), (byte)4, false);
/* 154 */     this.authtime = KerberosTime.parse(localDerValue.getData(), (byte)5, false);
/* 155 */     this.starttime = KerberosTime.parse(localDerValue.getData(), (byte)6, true);
/* 156 */     this.endtime = KerberosTime.parse(localDerValue.getData(), (byte)7, false);
/* 157 */     if (localDerValue.getData().available() > 0) {
/* 158 */       this.renewTill = KerberosTime.parse(localDerValue.getData(), (byte)8, true);
/*     */     }
/* 160 */     if (localDerValue.getData().available() > 0) {
/* 161 */       this.caddr = HostAddresses.parse(localDerValue.getData(), (byte)9, true);
/*     */     }
/* 163 */     if (localDerValue.getData().available() > 0) {
/* 164 */       this.authorizationData = AuthorizationData.parse(localDerValue.getData(), (byte)10, true);
/*     */     }
/* 166 */     if (localDerValue.getData().available() > 0) {
/* 167 */       throw new Asn1Exception(906);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] asn1Encode()
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 179 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 180 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 181 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), this.flags
/* 182 */       .asn1Encode());
/* 183 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), this.key
/* 184 */       .asn1Encode());
/* 185 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), this.cname
/* 186 */       .getRealm().asn1Encode());
/* 187 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), this.cname
/* 188 */       .asn1Encode());
/* 189 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), this.transited
/* 190 */       .asn1Encode());
/* 191 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), this.authtime
/* 192 */       .asn1Encode());
/* 193 */     if (this.starttime != null) {
/* 194 */       localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), this.starttime
/* 195 */         .asn1Encode());
/*     */     }
/* 197 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)7), this.endtime
/* 198 */       .asn1Encode());
/*     */     
/* 200 */     if (this.renewTill != null) {
/* 201 */       localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)8), this.renewTill
/* 202 */         .asn1Encode());
/*     */     }
/*     */     
/* 205 */     if (this.caddr != null) {
/* 206 */       localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)9), this.caddr
/* 207 */         .asn1Encode());
/*     */     }
/*     */     
/* 210 */     if (this.authorizationData != null) {
/* 211 */       localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)10), this.authorizationData
/* 212 */         .asn1Encode());
/*     */     }
/* 214 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/* 215 */     localDerOutputStream1 = new DerOutputStream();
/* 216 */     localDerOutputStream1.write(DerValue.createTag((byte)64, true, (byte)3), localDerOutputStream2);
/*     */     
/* 218 */     return localDerOutputStream1.toByteArray();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\EncTicketPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
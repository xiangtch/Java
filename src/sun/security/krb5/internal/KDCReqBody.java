/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Vector;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.EncryptedData;
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
/*     */ public class KDCReqBody
/*     */ {
/*     */   public KDCOptions kdcOptions;
/*     */   public PrincipalName cname;
/*     */   public PrincipalName sname;
/*     */   public KerberosTime from;
/*     */   public KerberosTime till;
/*     */   public KerberosTime rtime;
/*     */   public HostAddresses addresses;
/*     */   private int nonce;
/*  82 */   private int[] eType = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private EncryptedData encAuthorizationData;
/*     */   
/*     */ 
/*     */ 
/*     */   private Ticket[] additionalTickets;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public KDCReqBody(KDCOptions paramKDCOptions, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, int paramInt, int[] paramArrayOfInt, HostAddresses paramHostAddresses, EncryptedData paramEncryptedData, Ticket[] paramArrayOfTicket)
/*     */     throws IOException
/*     */   {
/*  99 */     this.kdcOptions = paramKDCOptions;
/* 100 */     this.cname = paramPrincipalName1;
/* 101 */     this.sname = paramPrincipalName2;
/* 102 */     this.from = paramKerberosTime1;
/* 103 */     this.till = paramKerberosTime2;
/* 104 */     this.rtime = paramKerberosTime3;
/* 105 */     this.nonce = paramInt;
/* 106 */     if (paramArrayOfInt != null) {
/* 107 */       this.eType = ((int[])paramArrayOfInt.clone());
/*     */     }
/* 109 */     this.addresses = paramHostAddresses;
/* 110 */     this.encAuthorizationData = paramEncryptedData;
/* 111 */     if (paramArrayOfTicket != null) {
/* 112 */       this.additionalTickets = new Ticket[paramArrayOfTicket.length];
/* 113 */       for (int i = 0; i < paramArrayOfTicket.length; i++) {
/* 114 */         if (paramArrayOfTicket[i] == null) {
/* 115 */           throw new IOException("Cannot create a KDCReqBody");
/*     */         }
/* 117 */         this.additionalTickets[i] = ((Ticket)paramArrayOfTicket[i].clone());
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
/*     */ 
/*     */ 
/*     */   public KDCReqBody(DerValue paramDerValue, int paramInt)
/*     */     throws Asn1Exception, RealmException, KrbException, IOException
/*     */   {
/* 135 */     this.addresses = null;
/* 136 */     this.encAuthorizationData = null;
/* 137 */     this.additionalTickets = null;
/* 138 */     if (paramDerValue.getTag() != 48) {
/* 139 */       throw new Asn1Exception(906);
/*     */     }
/* 141 */     this.kdcOptions = KDCOptions.parse(paramDerValue.getData(), (byte)0, false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 147 */     this.cname = PrincipalName.parse(paramDerValue.getData(), (byte)1, true, new Realm("PLACEHOLDER"));
/*     */     
/* 149 */     if ((paramInt != 10) && (this.cname != null)) {
/* 150 */       throw new Asn1Exception(906);
/*     */     }
/* 152 */     Realm localRealm = Realm.parse(paramDerValue.getData(), (byte)2, false);
/* 153 */     if (this.cname != null)
/*     */     {
/* 155 */       this.cname = new PrincipalName(this.cname.getNameType(), this.cname.getNameStrings(), localRealm);
/*     */     }
/* 157 */     this.sname = PrincipalName.parse(paramDerValue.getData(), (byte)3, true, localRealm);
/* 158 */     this.from = KerberosTime.parse(paramDerValue.getData(), (byte)4, true);
/* 159 */     this.till = KerberosTime.parse(paramDerValue.getData(), (byte)5, false);
/* 160 */     this.rtime = KerberosTime.parse(paramDerValue.getData(), (byte)6, true);
/* 161 */     DerValue localDerValue1 = paramDerValue.getData().getDerValue();
/* 162 */     if ((localDerValue1.getTag() & 0x1F) == 7) {
/* 163 */       this.nonce = localDerValue1.getData().getBigInteger().intValue();
/*     */     } else {
/* 165 */       throw new Asn1Exception(906);
/*     */     }
/* 167 */     localDerValue1 = paramDerValue.getData().getDerValue();
/* 168 */     Vector localVector1 = new Vector();
/* 169 */     DerValue localDerValue2; if ((localDerValue1.getTag() & 0x1F) == 8) {
/* 170 */       localDerValue2 = localDerValue1.getData().getDerValue();
/*     */       
/* 172 */       if (localDerValue2.getTag() == 48) {
/* 173 */         while (localDerValue2.getData().available() > 0) {
/* 174 */           localVector1.addElement(Integer.valueOf(localDerValue2.getData().getBigInteger().intValue()));
/*     */         }
/* 176 */         this.eType = new int[localVector1.size()];
/* 177 */         for (int i = 0; i < localVector1.size(); i++) {
/* 178 */           this.eType[i] = ((Integer)localVector1.elementAt(i)).intValue();
/*     */         }
/*     */       } else {
/* 181 */         throw new Asn1Exception(906);
/*     */       }
/*     */     } else {
/* 184 */       throw new Asn1Exception(906);
/*     */     }
/* 186 */     if (paramDerValue.getData().available() > 0) {
/* 187 */       this.addresses = HostAddresses.parse(paramDerValue.getData(), (byte)9, true);
/*     */     }
/* 189 */     if (paramDerValue.getData().available() > 0) {
/* 190 */       this.encAuthorizationData = EncryptedData.parse(paramDerValue.getData(), (byte)10, true);
/*     */     }
/* 192 */     if (paramDerValue.getData().available() > 0) {
/* 193 */       Vector localVector2 = new Vector();
/* 194 */       localDerValue1 = paramDerValue.getData().getDerValue();
/* 195 */       if ((localDerValue1.getTag() & 0x1F) == 11) {
/* 196 */         localDerValue2 = localDerValue1.getData().getDerValue();
/* 197 */         if (localDerValue2.getTag() == 48) {
/* 198 */           while (localDerValue2.getData().available() > 0) {
/* 199 */             localVector2.addElement(new Ticket(localDerValue2.getData().getDerValue()));
/*     */           }
/*     */         }
/* 202 */         throw new Asn1Exception(906);
/*     */         
/* 204 */         if (localVector2.size() > 0) {
/* 205 */           this.additionalTickets = new Ticket[localVector2.size()];
/* 206 */           localVector2.copyInto(this.additionalTickets);
/*     */         }
/*     */       } else {
/* 209 */         throw new Asn1Exception(906);
/*     */       }
/*     */     }
/* 212 */     if (paramDerValue.getData().available() > 0) {
/* 213 */       throw new Asn1Exception(906);
/*     */     }
/*     */   }
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
/* 226 */     Vector localVector = new Vector();
/* 227 */     localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), this.kdcOptions.asn1Encode()));
/* 228 */     if ((paramInt == 10) && 
/* 229 */       (this.cname != null)) {
/* 230 */       localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), this.cname.asn1Encode()));
/*     */     }
/*     */     
/* 233 */     if (this.sname != null) {
/* 234 */       localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), this.sname.getRealm().asn1Encode()));
/* 235 */       localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), this.sname.asn1Encode()));
/* 236 */     } else if (this.cname != null) {
/* 237 */       localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), this.cname.getRealm().asn1Encode()));
/*     */     }
/* 239 */     if (this.from != null) {
/* 240 */       localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), this.from.asn1Encode()));
/*     */     }
/* 242 */     localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), this.till.asn1Encode()));
/* 243 */     if (this.rtime != null) {
/* 244 */       localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), this.rtime.asn1Encode()));
/*     */     }
/* 246 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 247 */     localDerOutputStream1.putInteger(BigInteger.valueOf(this.nonce));
/* 248 */     localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)7), localDerOutputStream1.toByteArray()));
/*     */     
/* 250 */     localDerOutputStream1 = new DerOutputStream();
/* 251 */     for (int i = 0; i < this.eType.length; i++) {
/* 252 */       localDerOutputStream1.putInteger(BigInteger.valueOf(this.eType[i]));
/*     */     }
/* 254 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 255 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/* 256 */     localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)8), localDerOutputStream2.toByteArray()));
/* 257 */     if (this.addresses != null) {
/* 258 */       localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)9), this.addresses.asn1Encode()));
/*     */     }
/* 260 */     if (this.encAuthorizationData != null) {
/* 261 */       localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)10), this.encAuthorizationData.asn1Encode()));
/*     */     }
/* 263 */     if ((this.additionalTickets != null) && (this.additionalTickets.length > 0)) {
/* 264 */       localDerOutputStream1 = new DerOutputStream();
/* 265 */       for (int j = 0; j < this.additionalTickets.length; j++) {
/* 266 */         localDerOutputStream1.write(this.additionalTickets[j].asn1Encode());
/*     */       }
/* 268 */       localObject = new DerOutputStream();
/* 269 */       ((DerOutputStream)localObject).write((byte)48, localDerOutputStream1);
/* 270 */       localVector.addElement(new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)11), ((DerOutputStream)localObject).toByteArray()));
/*     */     }
/* 272 */     Object localObject = new DerValue[localVector.size()];
/* 273 */     localVector.copyInto((Object[])localObject);
/* 274 */     localDerOutputStream1 = new DerOutputStream();
/* 275 */     localDerOutputStream1.putSequence((DerValue[])localObject);
/* 276 */     return localDerOutputStream1.toByteArray();
/*     */   }
/*     */   
/*     */   public int getNonce() {
/* 280 */     return this.nonce;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\KDCReqBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
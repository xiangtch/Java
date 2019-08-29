/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.EncryptedData;
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
/*     */ public class Ticket
/*     */   implements Cloneable
/*     */ {
/*     */   public int tkt_vno;
/*     */   public PrincipalName sname;
/*     */   public EncryptedData encPart;
/*     */   
/*     */   private Ticket() {}
/*     */   
/*     */   public Object clone()
/*     */   {
/*  70 */     Ticket localTicket = new Ticket();
/*  71 */     localTicket.sname = ((PrincipalName)this.sname.clone());
/*  72 */     localTicket.encPart = ((EncryptedData)this.encPart.clone());
/*  73 */     localTicket.tkt_vno = this.tkt_vno;
/*  74 */     return localTicket;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Ticket(PrincipalName paramPrincipalName, EncryptedData paramEncryptedData)
/*     */   {
/*  81 */     this.tkt_vno = 5;
/*  82 */     this.sname = paramPrincipalName;
/*  83 */     this.encPart = paramEncryptedData;
/*     */   }
/*     */   
/*     */   public Ticket(byte[] paramArrayOfByte) throws Asn1Exception, RealmException, KrbApErrException, IOException
/*     */   {
/*  88 */     init(new DerValue(paramArrayOfByte));
/*     */   }
/*     */   
/*     */   public Ticket(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException
/*     */   {
/*  93 */     init(paramDerValue);
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
/*     */   private void init(DerValue paramDerValue)
/*     */     throws Asn1Exception, RealmException, KrbApErrException, IOException
/*     */   {
/* 109 */     if (((paramDerValue.getTag() & 0x1F) != 1) || 
/* 110 */       (paramDerValue.isApplication() != true) || 
/* 111 */       (paramDerValue.isConstructed() != true))
/* 112 */       throw new Asn1Exception(906);
/* 113 */     DerValue localDerValue1 = paramDerValue.getData().getDerValue();
/* 114 */     if (localDerValue1.getTag() != 48)
/* 115 */       throw new Asn1Exception(906);
/* 116 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 117 */     if ((localDerValue2.getTag() & 0x1F) != 0)
/* 118 */       throw new Asn1Exception(906);
/* 119 */     this.tkt_vno = localDerValue2.getData().getBigInteger().intValue();
/* 120 */     if (this.tkt_vno != 5)
/* 121 */       throw new KrbApErrException(39);
/* 122 */     Realm localRealm = Realm.parse(localDerValue1.getData(), (byte)1, false);
/* 123 */     this.sname = PrincipalName.parse(localDerValue1.getData(), (byte)2, false, localRealm);
/* 124 */     this.encPart = EncryptedData.parse(localDerValue1.getData(), (byte)3, false);
/* 125 */     if (localDerValue1.getData().available() > 0) {
/* 126 */       throw new Asn1Exception(906);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] asn1Encode()
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 136 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 137 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 138 */     DerValue[] arrayOfDerValue = new DerValue[4];
/* 139 */     localDerOutputStream2.putInteger(BigInteger.valueOf(this.tkt_vno));
/* 140 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
/* 141 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), this.sname.getRealm().asn1Encode());
/* 142 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), this.sname.asn1Encode());
/* 143 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), this.encPart.asn1Encode());
/* 144 */     localDerOutputStream2 = new DerOutputStream();
/* 145 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/* 146 */     DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 147 */     localDerOutputStream3.write(DerValue.createTag((byte)64, true, (byte)1), localDerOutputStream2);
/* 148 */     return localDerOutputStream3.toByteArray();
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
/*     */   public static Ticket parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
/*     */     throws Asn1Exception, IOException, RealmException, KrbApErrException
/*     */   {
/* 163 */     if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte))
/* 164 */       return null;
/* 165 */     DerValue localDerValue1 = paramDerInputStream.getDerValue();
/* 166 */     if (paramByte != (localDerValue1.getTag() & 0x1F)) {
/* 167 */       throw new Asn1Exception(906);
/*     */     }
/*     */     
/* 170 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 171 */     return new Ticket(localDerValue2);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\Ticket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
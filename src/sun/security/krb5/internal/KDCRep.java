/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
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
/*     */ 
/*     */ 
/*     */ public class KDCRep
/*     */ {
/*     */   public PrincipalName cname;
/*     */   public Ticket ticket;
/*     */   public EncryptedData encPart;
/*     */   public EncKDCRepPart encKDCRepPart;
/*     */   private int pvno;
/*     */   private int msgType;
/*  70 */   public PAData[] pAData = null;
/*  71 */   private boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public KDCRep(PAData[] paramArrayOfPAData, PrincipalName paramPrincipalName, Ticket paramTicket, EncryptedData paramEncryptedData, int paramInt)
/*     */     throws IOException
/*     */   {
/*  79 */     this.pvno = 5;
/*  80 */     this.msgType = paramInt;
/*  81 */     if (paramArrayOfPAData != null) {
/*  82 */       this.pAData = new PAData[paramArrayOfPAData.length];
/*  83 */       for (int i = 0; i < paramArrayOfPAData.length; i++) {
/*  84 */         if (paramArrayOfPAData[i] == null) {
/*  85 */           throw new IOException("Cannot create a KDCRep");
/*     */         }
/*  87 */         this.pAData[i] = ((PAData)paramArrayOfPAData[i].clone());
/*     */       }
/*     */     }
/*     */     
/*  91 */     this.cname = paramPrincipalName;
/*  92 */     this.ticket = paramTicket;
/*  93 */     this.encPart = paramEncryptedData;
/*     */   }
/*     */   
/*     */   public KDCRep() {}
/*     */   
/*     */   public KDCRep(byte[] paramArrayOfByte, int paramInt)
/*     */     throws Asn1Exception, KrbApErrException, RealmException, IOException
/*     */   {
/* 101 */     init(new DerValue(paramArrayOfByte), paramInt);
/*     */   }
/*     */   
/*     */   public KDCRep(DerValue paramDerValue, int paramInt) throws Asn1Exception, RealmException, KrbApErrException, IOException
/*     */   {
/* 106 */     init(paramDerValue, paramInt);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void init(DerValue paramDerValue, int paramInt)
/*     */     throws Asn1Exception, RealmException, IOException, KrbApErrException
/*     */   {
/* 133 */     if ((paramDerValue.getTag() & 0x1F) != paramInt) {
/* 134 */       if (this.DEBUG) {
/* 135 */         System.out.println(">>> KDCRep: init() encoding tag is " + paramDerValue
/*     */         
/* 137 */           .getTag() + " req type is " + paramInt);
/*     */       }
/*     */       
/* 140 */       throw new Asn1Exception(906);
/*     */     }
/* 142 */     DerValue localDerValue1 = paramDerValue.getData().getDerValue();
/* 143 */     if (localDerValue1.getTag() != 48) {
/* 144 */       throw new Asn1Exception(906);
/*     */     }
/* 146 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 147 */     if ((localDerValue2.getTag() & 0x1F) == 0) {
/* 148 */       this.pvno = localDerValue2.getData().getBigInteger().intValue();
/* 149 */       if (this.pvno != 5) {
/* 150 */         throw new KrbApErrException(39);
/*     */       }
/*     */     } else {
/* 153 */       throw new Asn1Exception(906);
/*     */     }
/* 155 */     localDerValue2 = localDerValue1.getData().getDerValue();
/* 156 */     if ((localDerValue2.getTag() & 0x1F) == 1) {
/* 157 */       this.msgType = localDerValue2.getData().getBigInteger().intValue();
/* 158 */       if (this.msgType != paramInt) {
/* 159 */         throw new KrbApErrException(40);
/*     */       }
/*     */     } else {
/* 162 */       throw new Asn1Exception(906);
/*     */     }
/* 164 */     if ((localDerValue1.getData().peekByte() & 0x1F) == 2) {
/* 165 */       localDerValue2 = localDerValue1.getData().getDerValue();
/* 166 */       localObject = localDerValue2.getData().getSequence(1);
/* 167 */       this.pAData = new PAData[localObject.length];
/* 168 */       for (int i = 0; i < localObject.length; i++) {
/* 169 */         this.pAData[i] = new PAData(localObject[i]);
/*     */       }
/*     */     } else {
/* 172 */       this.pAData = null;
/*     */     }
/* 174 */     Object localObject = Realm.parse(localDerValue1.getData(), (byte)3, false);
/* 175 */     this.cname = PrincipalName.parse(localDerValue1.getData(), (byte)4, false, (Realm)localObject);
/* 176 */     this.ticket = Ticket.parse(localDerValue1.getData(), (byte)5, false);
/* 177 */     this.encPart = EncryptedData.parse(localDerValue1.getData(), (byte)6, false);
/* 178 */     if (localDerValue1.getData().available() > 0) {
/* 179 */       throw new Asn1Exception(906);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] asn1Encode()
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 192 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 193 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 194 */     localDerOutputStream2.putInteger(BigInteger.valueOf(this.pvno));
/* 195 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
/*     */     
/* 197 */     localDerOutputStream2 = new DerOutputStream();
/* 198 */     localDerOutputStream2.putInteger(BigInteger.valueOf(this.msgType));
/* 199 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
/*     */     
/* 201 */     if ((this.pAData != null) && (this.pAData.length > 0)) {
/* 202 */       DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 203 */       for (int i = 0; i < this.pAData.length; i++) {
/* 204 */         localDerOutputStream3.write(this.pAData[i].asn1Encode());
/*     */       }
/* 206 */       localDerOutputStream2 = new DerOutputStream();
/* 207 */       localDerOutputStream2.write((byte)48, localDerOutputStream3);
/* 208 */       localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
/*     */     }
/*     */     
/* 211 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), this.cname
/* 212 */       .getRealm().asn1Encode());
/* 213 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), this.cname
/* 214 */       .asn1Encode());
/* 215 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), this.ticket
/* 216 */       .asn1Encode());
/* 217 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), this.encPart
/* 218 */       .asn1Encode());
/* 219 */     localDerOutputStream2 = new DerOutputStream();
/* 220 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/* 221 */     return localDerOutputStream2.toByteArray();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\KDCRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
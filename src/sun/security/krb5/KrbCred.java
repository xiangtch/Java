/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import sun.security.krb5.internal.EncKrbCredPart;
/*     */ import sun.security.krb5.internal.HostAddresses;
/*     */ import sun.security.krb5.internal.KDCOptions;
/*     */ import sun.security.krb5.internal.KRBCred;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.KrbCredInfo;
/*     */ import sun.security.krb5.internal.Ticket;
/*     */ import sun.security.krb5.internal.TicketFlags;
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
/*     */ public class KrbCred
/*     */ {
/*  48 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */   
/*  50 */   private byte[] obuf = null;
/*  51 */   private KRBCred credMessg = null;
/*  52 */   private Ticket ticket = null;
/*  53 */   private EncKrbCredPart encPart = null;
/*  54 */   private Credentials creds = null;
/*  55 */   private KerberosTime timeStamp = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public KrbCred(Credentials paramCredentials1, Credentials paramCredentials2, EncryptionKey paramEncryptionKey)
/*     */     throws KrbException, IOException
/*     */   {
/*  63 */     PrincipalName localPrincipalName1 = paramCredentials1.getClient();
/*  64 */     PrincipalName localPrincipalName2 = paramCredentials1.getServer();
/*  65 */     PrincipalName localPrincipalName3 = paramCredentials2.getServer();
/*  66 */     if (!paramCredentials2.getClient().equals(localPrincipalName1)) {
/*  67 */       throw new KrbException(60, "Client principal does not match");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */     KDCOptions localKDCOptions = new KDCOptions();
/*  75 */     localKDCOptions.set(2, true);
/*  76 */     localKDCOptions.set(1, true);
/*     */     
/*  78 */     HostAddresses localHostAddresses = null;
/*     */     
/*     */ 
/*  81 */     if (localPrincipalName3.getNameType() == 3) {
/*  82 */       localHostAddresses = new HostAddresses(localPrincipalName3);
/*     */     }
/*  84 */     KrbTgsReq localKrbTgsReq = new KrbTgsReq(localKDCOptions, paramCredentials1, localPrincipalName2, null, null, null, null, localHostAddresses, null, null, null);
/*     */     
/*  86 */     this.credMessg = createMessage(localKrbTgsReq.sendAndGetCreds(), paramEncryptionKey);
/*     */     
/*  88 */     this.obuf = this.credMessg.asn1Encode();
/*     */   }
/*     */   
/*     */ 
/*     */   KRBCred createMessage(Credentials paramCredentials, EncryptionKey paramEncryptionKey)
/*     */     throws KrbException, IOException
/*     */   {
/*  95 */     EncryptionKey localEncryptionKey = paramCredentials.getSessionKey();
/*  96 */     PrincipalName localPrincipalName1 = paramCredentials.getClient();
/*  97 */     Realm localRealm = localPrincipalName1.getRealm();
/*  98 */     PrincipalName localPrincipalName2 = paramCredentials.getServer();
/*     */     
/* 100 */     KrbCredInfo localKrbCredInfo = new KrbCredInfo(localEncryptionKey, localPrincipalName1, paramCredentials.flags, paramCredentials.authTime, paramCredentials.startTime, paramCredentials.endTime, paramCredentials.renewTill, localPrincipalName2, paramCredentials.cAddr);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */     this.timeStamp = KerberosTime.now();
/* 107 */     KrbCredInfo[] arrayOfKrbCredInfo = { localKrbCredInfo };
/* 108 */     EncKrbCredPart localEncKrbCredPart = new EncKrbCredPart(arrayOfKrbCredInfo, this.timeStamp, null, null, null, null);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 113 */     EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey, localEncKrbCredPart.asn1Encode(), 14);
/*     */     
/* 115 */     Ticket[] arrayOfTicket = { paramCredentials.ticket };
/*     */     
/* 117 */     this.credMessg = new KRBCred(arrayOfTicket, localEncryptedData);
/*     */     
/* 119 */     return this.credMessg;
/*     */   }
/*     */   
/*     */ 
/*     */   public KrbCred(byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey)
/*     */     throws KrbException, IOException
/*     */   {
/* 126 */     this.credMessg = new KRBCred(paramArrayOfByte);
/*     */     
/* 128 */     this.ticket = this.credMessg.tickets[0];
/*     */     
/* 130 */     if (this.credMessg.encPart.getEType() == 0) {
/* 131 */       paramEncryptionKey = EncryptionKey.NULL_KEY;
/*     */     }
/* 133 */     byte[] arrayOfByte1 = this.credMessg.encPart.decrypt(paramEncryptionKey, 14);
/*     */     
/* 135 */     byte[] arrayOfByte2 = this.credMessg.encPart.reset(arrayOfByte1);
/* 136 */     DerValue localDerValue = new DerValue(arrayOfByte2);
/* 137 */     EncKrbCredPart localEncKrbCredPart = new EncKrbCredPart(localDerValue);
/*     */     
/* 139 */     this.timeStamp = localEncKrbCredPart.timeStamp;
/*     */     
/* 141 */     KrbCredInfo localKrbCredInfo = localEncKrbCredPart.ticketInfo[0];
/* 142 */     EncryptionKey localEncryptionKey = localKrbCredInfo.key;
/* 143 */     PrincipalName localPrincipalName1 = localKrbCredInfo.pname;
/* 144 */     TicketFlags localTicketFlags = localKrbCredInfo.flags;
/* 145 */     KerberosTime localKerberosTime1 = localKrbCredInfo.authtime;
/* 146 */     KerberosTime localKerberosTime2 = localKrbCredInfo.starttime;
/* 147 */     KerberosTime localKerberosTime3 = localKrbCredInfo.endtime;
/* 148 */     KerberosTime localKerberosTime4 = localKrbCredInfo.renewTill;
/* 149 */     PrincipalName localPrincipalName2 = localKrbCredInfo.sname;
/* 150 */     HostAddresses localHostAddresses = localKrbCredInfo.caddr;
/*     */     
/* 152 */     if (DEBUG) {
/* 153 */       System.out.println(">>>Delegated Creds have pname=" + localPrincipalName1 + " sname=" + localPrincipalName2 + " authtime=" + localKerberosTime1 + " starttime=" + localKerberosTime2 + " endtime=" + localKerberosTime3 + "renewTill=" + localKerberosTime4);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 160 */     this.creds = new Credentials(this.ticket, localPrincipalName1, localPrincipalName2, localEncryptionKey, localTicketFlags, localKerberosTime1, localKerberosTime2, localKerberosTime3, localKerberosTime4, localHostAddresses);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Credentials[] getDelegatedCreds()
/*     */   {
/* 169 */     Credentials[] arrayOfCredentials = { this.creds };
/* 170 */     return arrayOfCredentials;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getMessage()
/*     */   {
/* 177 */     return this.obuf;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KrbCred.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
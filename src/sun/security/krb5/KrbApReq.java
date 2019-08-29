/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Arrays;
/*     */ import sun.security.jgss.krb5.Krb5AcceptCredential;
/*     */ import sun.security.krb5.internal.APOptions;
/*     */ import sun.security.krb5.internal.APReq;
/*     */ import sun.security.krb5.internal.Authenticator;
/*     */ import sun.security.krb5.internal.AuthorizationData;
/*     */ import sun.security.krb5.internal.EncTicketPart;
/*     */ import sun.security.krb5.internal.HostAddress;
/*     */ import sun.security.krb5.internal.HostAddresses;
/*     */ import sun.security.krb5.internal.KRBError;
/*     */ import sun.security.krb5.internal.KdcErrException;
/*     */ import sun.security.krb5.internal.KerberosTime;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.KrbApErrException;
/*     */ import sun.security.krb5.internal.LocalSeqNumber;
/*     */ import sun.security.krb5.internal.ReplayCache;
/*     */ import sun.security.krb5.internal.SeqNumber;
/*     */ import sun.security.krb5.internal.Ticket;
/*     */ import sun.security.krb5.internal.TicketFlags;
/*     */ import sun.security.krb5.internal.crypto.EType;
/*     */ import sun.security.krb5.internal.rcache.AuthTimeWithHash;
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
/*     */ public class KrbApReq
/*     */ {
/*     */   private byte[] obuf;
/*     */   private KerberosTime ctime;
/*     */   private int cusec;
/*     */   private Authenticator authenticator;
/*     */   private Credentials creds;
/*     */   private APReq apReqMessg;
/*  59 */   private static ReplayCache rcache = ;
/*  60 */   private static boolean DEBUG = Krb5.DEBUG;
/*  61 */   private static final char[] hexConst = "0123456789ABCDEF".toCharArray();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public KrbApReq(Credentials paramCredentials, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Checksum paramChecksum)
/*     */     throws Asn1Exception, KrbCryptoException, KrbException, IOException
/*     */   {
/* 109 */     APOptions localAPOptions = paramBoolean1 ? new APOptions(2) : new APOptions();
/*     */     
/*     */ 
/* 112 */     if (DEBUG) {
/* 113 */       System.out.println(">>> KrbApReq: APOptions are " + localAPOptions);
/*     */     }
/*     */     
/* 116 */     EncryptionKey localEncryptionKey = paramBoolean2 ? new EncryptionKey(paramCredentials.getSessionKey()) : null;
/*     */     
/*     */ 
/* 119 */     LocalSeqNumber localLocalSeqNumber = new LocalSeqNumber();
/*     */     
/* 121 */     init(localAPOptions, paramCredentials, paramChecksum, localEncryptionKey, localLocalSeqNumber, null, 11);
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
/*     */   public KrbApReq(byte[] paramArrayOfByte, Krb5AcceptCredential paramKrb5AcceptCredential, InetAddress paramInetAddress)
/*     */     throws KrbException, IOException
/*     */   {
/* 146 */     this.obuf = paramArrayOfByte;
/* 147 */     if (this.apReqMessg == null)
/* 148 */       decode();
/* 149 */     authenticate(paramKrb5AcceptCredential, paramInetAddress);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   KrbApReq(APOptions paramAPOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, PrincipalName paramPrincipalName, Checksum paramChecksum, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData)
/*     */     throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
/*     */   {
/* 196 */     init(paramAPOptions, paramTicket, paramEncryptionKey1, paramPrincipalName, paramChecksum, paramKerberosTime, paramEncryptionKey2, paramSeqNumber, paramAuthorizationData, 7);
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
/*     */   private void init(APOptions paramAPOptions, Credentials paramCredentials, Checksum paramChecksum, EncryptionKey paramEncryptionKey, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData, int paramInt)
/*     */     throws KrbException, IOException
/*     */   {
/* 211 */     this.ctime = KerberosTime.now();
/* 212 */     init(paramAPOptions, paramCredentials.ticket, paramCredentials.key, paramCredentials.client, paramChecksum, this.ctime, paramEncryptionKey, paramSeqNumber, paramAuthorizationData, paramInt);
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
/*     */   private void init(APOptions paramAPOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, PrincipalName paramPrincipalName, Checksum paramChecksum, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData, int paramInt)
/*     */     throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
/*     */   {
/* 237 */     createMessage(paramAPOptions, paramTicket, paramEncryptionKey1, paramPrincipalName, paramChecksum, paramKerberosTime, paramEncryptionKey2, paramSeqNumber, paramAuthorizationData, paramInt);
/*     */     
/*     */ 
/* 240 */     this.obuf = this.apReqMessg.asn1Encode();
/*     */   }
/*     */   
/*     */   void decode() throws KrbException, IOException
/*     */   {
/* 245 */     DerValue localDerValue = new DerValue(this.obuf);
/* 246 */     decode(localDerValue);
/*     */   }
/*     */   
/*     */   void decode(DerValue paramDerValue) throws KrbException, IOException {
/* 250 */     this.apReqMessg = null;
/*     */     try {
/* 252 */       this.apReqMessg = new APReq(paramDerValue);
/*     */     } catch (Asn1Exception localAsn1Exception) {
/* 254 */       this.apReqMessg = null;
/* 255 */       KRBError localKRBError = new KRBError(paramDerValue);
/* 256 */       String str1 = localKRBError.getErrorString();
/*     */       String str2;
/* 258 */       if (str1.charAt(str1.length() - 1) == 0) {
/* 259 */         str2 = str1.substring(0, str1.length() - 1);
/*     */       } else
/* 261 */         str2 = str1;
/* 262 */       KrbException localKrbException = new KrbException(localKRBError.getErrorCode(), str2);
/* 263 */       localKrbException.initCause(localAsn1Exception);
/* 264 */       throw localKrbException;
/*     */     }
/*     */   }
/*     */   
/*     */   private void authenticate(Krb5AcceptCredential paramKrb5AcceptCredential, InetAddress paramInetAddress) throws KrbException, IOException
/*     */   {
/* 270 */     int i = this.apReqMessg.ticket.encPart.getEType();
/* 271 */     Integer localInteger = this.apReqMessg.ticket.encPart.getKeyVersionNumber();
/* 272 */     EncryptionKey[] arrayOfEncryptionKey = paramKrb5AcceptCredential.getKrb5EncryptionKeys(this.apReqMessg.ticket.sname);
/* 273 */     EncryptionKey localEncryptionKey = EncryptionKey.findKey(i, localInteger, arrayOfEncryptionKey);
/*     */     
/* 275 */     if (localEncryptionKey == null)
/*     */     {
/*     */ 
/* 278 */       throw new KrbException(400, "Cannot find key of appropriate type to decrypt AP REP - " + EType.toString(i));
/*     */     }
/*     */     
/* 281 */     byte[] arrayOfByte1 = this.apReqMessg.ticket.encPart.decrypt(localEncryptionKey, 2);
/*     */     
/* 283 */     byte[] arrayOfByte2 = this.apReqMessg.ticket.encPart.reset(arrayOfByte1);
/* 284 */     EncTicketPart localEncTicketPart = new EncTicketPart(arrayOfByte2);
/*     */     
/* 286 */     checkPermittedEType(localEncTicketPart.key.getEType());
/*     */     
/* 288 */     byte[] arrayOfByte3 = this.apReqMessg.authenticator.decrypt(localEncTicketPart.key, 11);
/*     */     
/* 290 */     byte[] arrayOfByte4 = this.apReqMessg.authenticator.reset(arrayOfByte3);
/* 291 */     this.authenticator = new Authenticator(arrayOfByte4);
/* 292 */     this.ctime = this.authenticator.ctime;
/* 293 */     this.cusec = this.authenticator.cusec;
/*     */     
/* 295 */     this.authenticator.ctime = this.authenticator.ctime.withMicroSeconds(this.authenticator.cusec);
/*     */     
/* 297 */     if (!this.authenticator.cname.equals(localEncTicketPart.cname)) {
/* 298 */       throw new KrbApErrException(36);
/*     */     }
/*     */     
/* 301 */     if (!this.authenticator.ctime.inClockSkew()) {
/* 302 */       throw new KrbApErrException(37);
/*     */     }
/*     */     byte[] arrayOfByte5;
/*     */     try
/*     */     {
/* 307 */       arrayOfByte5 = MessageDigest.getInstance("MD5").digest(this.apReqMessg.authenticator.cipher);
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 309 */       throw new AssertionError("Impossible");
/*     */     }
/*     */     
/* 312 */     char[] arrayOfChar = new char[arrayOfByte5.length * 2];
/* 313 */     for (int j = 0; j < arrayOfByte5.length; j++) {
/* 314 */       arrayOfChar[(2 * j)] = hexConst[((arrayOfByte5[j] & 0xFF) >> 4)];
/* 315 */       arrayOfChar[(2 * j + 1)] = hexConst[(arrayOfByte5[j] & 0xF)];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 320 */     AuthTimeWithHash localAuthTimeWithHash = new AuthTimeWithHash(this.authenticator.cname.toString(), this.apReqMessg.ticket.sname.toString(), this.authenticator.ctime.getSeconds(), this.authenticator.cusec, new String(arrayOfChar));
/*     */     
/*     */ 
/* 323 */     rcache.checkAndStore(KerberosTime.now(), localAuthTimeWithHash);
/*     */     
/* 325 */     if (paramInetAddress != null)
/*     */     {
/* 327 */       localObject = new HostAddress(paramInetAddress);
/* 328 */       if ((localEncTicketPart.caddr != null) && 
/* 329 */         (!localEncTicketPart.caddr.inList((HostAddress)localObject))) {
/* 330 */         if (DEBUG) {
/* 331 */           System.out.println(">>> KrbApReq: initiator is " + ((HostAddress)localObject)
/* 332 */             .getInetAddress() + ", but caddr is " + 
/*     */             
/* 334 */             Arrays.toString(localEncTicketPart.caddr
/* 335 */             .getInetAddresses()));
/*     */         }
/* 337 */         throw new KrbApErrException(38);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 347 */     Object localObject = KerberosTime.now();
/*     */     
/* 349 */     if (((localEncTicketPart.starttime != null) && 
/* 350 */       (localEncTicketPart.starttime.greaterThanWRTClockSkew((KerberosTime)localObject))) || 
/* 351 */       (localEncTicketPart.flags.get(7))) {
/* 352 */       throw new KrbApErrException(33);
/*     */     }
/*     */     
/*     */ 
/* 356 */     if ((localEncTicketPart.endtime != null) && 
/* 357 */       (((KerberosTime)localObject).greaterThanWRTClockSkew(localEncTicketPart.endtime))) {
/* 358 */       throw new KrbApErrException(32);
/*     */     }
/*     */     
/* 361 */     this.creds = new Credentials(this.apReqMessg.ticket, this.authenticator.cname, this.apReqMessg.ticket.sname, localEncTicketPart.key, localEncTicketPart.flags, localEncTicketPart.authtime, localEncTicketPart.starttime, localEncTicketPart.endtime, localEncTicketPart.renewTill, localEncTicketPart.caddr, localEncTicketPart.authorizationData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 373 */     if (DEBUG) {
/* 374 */       System.out.println(">>> KrbApReq: authenticate succeed.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Credentials getCreds()
/*     */   {
/* 383 */     return this.creds;
/*     */   }
/*     */   
/*     */   KerberosTime getCtime() {
/* 387 */     if (this.ctime != null)
/* 388 */       return this.ctime;
/* 389 */     return this.authenticator.ctime;
/*     */   }
/*     */   
/*     */   int cusec() {
/* 393 */     return this.cusec;
/*     */   }
/*     */   
/*     */   APOptions getAPOptions() throws KrbException, IOException {
/* 397 */     if (this.apReqMessg == null)
/* 398 */       decode();
/* 399 */     if (this.apReqMessg != null)
/* 400 */       return this.apReqMessg.apOptions;
/* 401 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getMutualAuthRequired()
/*     */     throws KrbException, IOException
/*     */   {
/* 411 */     if (this.apReqMessg == null)
/* 412 */       decode();
/* 413 */     if (this.apReqMessg != null)
/* 414 */       return this.apReqMessg.apOptions.get(2);
/* 415 */     return false;
/*     */   }
/*     */   
/*     */   boolean useSessionKey() throws KrbException, IOException {
/* 419 */     if (this.apReqMessg == null)
/* 420 */       decode();
/* 421 */     if (this.apReqMessg != null)
/* 422 */       return this.apReqMessg.apOptions.get(1);
/* 423 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncryptionKey getSubKey()
/*     */   {
/* 432 */     return this.authenticator.getSubKey();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Integer getSeqNumber()
/*     */   {
/* 442 */     return this.authenticator.getSeqNumber();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Checksum getChecksum()
/*     */   {
/* 451 */     return this.authenticator.getChecksum();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getMessage()
/*     */   {
/* 458 */     return this.obuf;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PrincipalName getClient()
/*     */   {
/* 466 */     return this.creds.getClient();
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
/*     */   private void createMessage(APOptions paramAPOptions, Ticket paramTicket, EncryptionKey paramEncryptionKey1, PrincipalName paramPrincipalName, Checksum paramChecksum, KerberosTime paramKerberosTime, EncryptionKey paramEncryptionKey2, SeqNumber paramSeqNumber, AuthorizationData paramAuthorizationData, int paramInt)
/*     */     throws Asn1Exception, IOException, KdcErrException, KrbCryptoException
/*     */   {
/* 482 */     Integer localInteger = null;
/*     */     
/* 484 */     if (paramSeqNumber != null) {
/* 485 */       localInteger = new Integer(paramSeqNumber.current());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 490 */     this.authenticator = new Authenticator(paramPrincipalName, paramChecksum, paramKerberosTime.getMicroSeconds(), paramKerberosTime, paramEncryptionKey2, localInteger, paramAuthorizationData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 496 */     byte[] arrayOfByte = this.authenticator.asn1Encode();
/*     */     
/* 498 */     EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey1, arrayOfByte, paramInt);
/*     */     
/*     */ 
/* 501 */     this.apReqMessg = new APReq(paramAPOptions, paramTicket, localEncryptedData);
/*     */   }
/*     */   
/*     */   private static void checkPermittedEType(int paramInt)
/*     */     throws KrbException
/*     */   {
/* 507 */     int[] arrayOfInt = EType.getDefaults("permitted_enctypes");
/* 508 */     if (!EType.isSupported(paramInt, arrayOfInt)) {
/* 509 */       throw new KrbException(EType.toString(paramInt) + " encryption type not in permitted_enctypes list");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\KrbApReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
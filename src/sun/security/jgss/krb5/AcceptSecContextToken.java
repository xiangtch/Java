/*     */ package sun.security.jgss.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.AccessController;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.krb5.Credentials;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.KrbApRep;
/*     */ import sun.security.krb5.KrbApReq;
/*     */ import sun.security.krb5.KrbException;
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
/*     */ class AcceptSecContextToken
/*     */   extends InitialToken
/*     */ {
/*  38 */   private KrbApRep apRep = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AcceptSecContextToken(Krb5Context paramKrb5Context, KrbApReq paramKrbApReq)
/*     */     throws KrbException, IOException, GSSException
/*     */   {
/*  48 */     boolean bool1 = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.acceptor.subkey"))).booleanValue();
/*     */     
/*     */ 
/*  51 */     boolean bool2 = true;
/*     */     
/*  53 */     EncryptionKey localEncryptionKey = null;
/*  54 */     if (bool1) {
/*  55 */       localEncryptionKey = new EncryptionKey(paramKrbApReq.getCreds().getSessionKey());
/*  56 */       paramKrb5Context.setKey(2, localEncryptionKey);
/*     */     }
/*  58 */     this.apRep = new KrbApRep(paramKrbApReq, bool2, localEncryptionKey);
/*     */     
/*  60 */     paramKrb5Context.resetMySequenceNumber(this.apRep.getSeqNumber().intValue());
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
/*     */   public AcceptSecContextToken(Krb5Context paramKrb5Context, Credentials paramCredentials, KrbApReq paramKrbApReq, InputStream paramInputStream)
/*     */     throws IOException, GSSException, KrbException
/*     */   {
/*  77 */     int i = paramInputStream.read() << 8 | paramInputStream.read();
/*     */     
/*  79 */     if (i != 512) {
/*  80 */       throw new GSSException(10, -1, "AP_REP token id does not match!");
/*     */     }
/*     */     
/*     */ 
/*  84 */     byte[] arrayOfByte = new DerValue(paramInputStream).toByteArray();
/*     */     
/*  86 */     KrbApRep localKrbApRep = new KrbApRep(arrayOfByte, paramCredentials, paramKrbApReq);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  92 */     EncryptionKey localEncryptionKey = localKrbApRep.getSubKey();
/*  93 */     if (localEncryptionKey != null) {
/*  94 */       paramKrb5Context.setKey(2, localEncryptionKey);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */     Integer localInteger = localKrbApRep.getSeqNumber();
/*     */     
/* 103 */     int j = localInteger != null ? localInteger.intValue() : 0;
/*     */     
/* 105 */     paramKrb5Context.resetPeerSequenceNumber(j);
/*     */   }
/*     */   
/*     */   public final byte[] encode() throws IOException {
/* 109 */     byte[] arrayOfByte1 = this.apRep.getMessage();
/* 110 */     byte[] arrayOfByte2 = new byte[2 + arrayOfByte1.length];
/* 111 */     writeInt(512, arrayOfByte2, 0);
/* 112 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 2, arrayOfByte1.length);
/* 113 */     return arrayOfByte2;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\AcceptSecContextToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
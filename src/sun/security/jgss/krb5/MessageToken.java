/*     */ package sun.security.jgss.krb5;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.security.MessageDigest;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.MessageProp;
/*     */ import sun.security.jgss.GSSHeader;
/*     */ import sun.security.jgss.GSSToken;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class MessageToken
/*     */   extends Krb5Token
/*     */ {
/*     */   private static final int TOKEN_NO_CKSUM_SIZE = 16;
/*     */   private static final int FILLER = 65535;
/*     */   static final int SGN_ALG_DES_MAC_MD5 = 0;
/*     */   static final int SGN_ALG_DES_MAC = 512;
/*     */   static final int SGN_ALG_HMAC_SHA1_DES3_KD = 1024;
/*     */   static final int SEAL_ALG_NONE = 65535;
/*     */   static final int SEAL_ALG_DES = 0;
/*     */   static final int SEAL_ALG_DES3_KD = 512;
/*     */   static final int SEAL_ALG_ARCFOUR_HMAC = 4096;
/*     */   static final int SGN_ALG_HMAC_MD5_ARCFOUR = 4352;
/*     */   private static final int TOKEN_ID_POS = 0;
/*     */   private static final int SIGN_ALG_POS = 2;
/*     */   private static final int SEAL_ALG_POS = 4;
/*     */   private int seqNumber;
/* 128 */   private boolean confState = true;
/* 129 */   private boolean initiator = true;
/*     */   
/* 131 */   private int tokenId = 0;
/* 132 */   private GSSHeader gssHeader = null;
/* 133 */   private MessageTokenHeader tokenHeader = null;
/* 134 */   private byte[] checksum = null;
/* 135 */   private byte[] encSeqNumber = null;
/* 136 */   private byte[] seqNumberData = null;
/*     */   
/*     */ 
/* 139 */   CipherHelper cipherHelper = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MessageToken(int paramInt1, Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt2, int paramInt3, MessageProp paramMessageProp)
/*     */     throws GSSException
/*     */   {
/* 159 */     this(paramInt1, paramKrb5Context, new ByteArrayInputStream(paramArrayOfByte, paramInt2, paramInt3), paramMessageProp);
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
/*     */   MessageToken(int paramInt, Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp)
/*     */     throws GSSException
/*     */   {
/* 180 */     init(paramInt, paramKrb5Context);
/*     */     try
/*     */     {
/* 183 */       this.gssHeader = new GSSHeader(paramInputStream);
/*     */       
/* 185 */       if (!this.gssHeader.getOid().equals(OID))
/*     */       {
/* 187 */         throw new GSSException(10, -1, getTokenName(paramInt));
/*     */       }
/* 189 */       if (!this.confState) {
/* 190 */         paramMessageProp.setPrivacy(false);
/*     */       }
/*     */       
/* 193 */       this.tokenHeader = new MessageTokenHeader(paramInputStream, paramMessageProp);
/*     */       
/* 195 */       this.encSeqNumber = new byte[8];
/* 196 */       readFully(paramInputStream, this.encSeqNumber);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 201 */       this.checksum = new byte[this.cipherHelper.getChecksumLength()];
/* 202 */       readFully(paramInputStream, this.checksum);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */ 
/*     */ 
/* 210 */       throw new GSSException(10, -1, getTokenName(paramInt) + ":" + localIOException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final GSSHeader getGSSHeader()
/*     */   {
/* 219 */     return this.gssHeader;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final int getTokenId()
/*     */   {
/* 227 */     return this.tokenId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final byte[] getEncSeqNumber()
/*     */   {
/* 235 */     return this.encSeqNumber;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final byte[] getChecksum()
/*     */   {
/* 243 */     return this.checksum;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean getConfState()
/*     */   {
/* 252 */     return this.confState;
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
/*     */   public void genSignAndSeqNumber(MessageProp paramMessageProp, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3)
/*     */     throws GSSException
/*     */   {
/* 297 */     int i = paramMessageProp.getQOP();
/* 298 */     if (i != 0) {
/* 299 */       i = 0;
/* 300 */       paramMessageProp.setQOP(i);
/*     */     }
/*     */     
/* 303 */     if (!this.confState) {
/* 304 */       paramMessageProp.setPrivacy(false);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 310 */     this.tokenHeader = new MessageTokenHeader(this.tokenId, paramMessageProp.getPrivacy(), i);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 315 */     this.checksum = getChecksum(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 322 */     this.seqNumberData = new byte[8];
/*     */     
/*     */ 
/*     */ 
/* 326 */     if (this.cipherHelper.isArcFour()) {
/* 327 */       writeBigEndian(this.seqNumber, this.seqNumberData);
/*     */     }
/*     */     else {
/* 330 */       writeLittleEndian(this.seqNumber, this.seqNumberData);
/*     */     }
/* 332 */     if (!this.initiator) {
/* 333 */       this.seqNumberData[4] = -1;
/* 334 */       this.seqNumberData[5] = -1;
/* 335 */       this.seqNumberData[6] = -1;
/* 336 */       this.seqNumberData[7] = -1;
/*     */     }
/*     */     
/* 339 */     this.encSeqNumber = this.cipherHelper.encryptSeq(this.checksum, this.seqNumberData, 0, 8);
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
/*     */   public final boolean verifySignAndSeqNumber(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3)
/*     */     throws GSSException
/*     */   {
/* 374 */     byte[] arrayOfByte = getChecksum(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 379 */     if (MessageDigest.isEqual(this.checksum, arrayOfByte))
/*     */     {
/* 381 */       this.seqNumberData = this.cipherHelper.decryptSeq(this.checksum, this.encSeqNumber, 0, 8);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 393 */       int i = 0;
/* 394 */       if (this.initiator) {
/* 395 */         i = -1;
/*     */       }
/* 397 */       if ((this.seqNumberData[4] == i) && (this.seqNumberData[5] == i) && (this.seqNumberData[6] == i) && (this.seqNumberData[7] == i))
/*     */       {
/*     */ 
/*     */ 
/* 401 */         return true;
/*     */       }
/*     */     }
/* 404 */     return false;
/*     */   }
/*     */   
/*     */   public final int getSequenceNumber()
/*     */   {
/* 409 */     int i = 0;
/* 410 */     if (this.cipherHelper.isArcFour()) {
/* 411 */       i = readBigEndian(this.seqNumberData, 0, 4);
/*     */     } else {
/* 413 */       i = readLittleEndian(this.seqNumberData, 0, 4);
/*     */     }
/* 415 */     return i;
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
/*     */   private byte[] getChecksum(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3)
/*     */     throws GSSException
/*     */   {
/* 452 */     byte[] arrayOfByte1 = this.tokenHeader.getBytes();
/* 453 */     byte[] arrayOfByte2 = paramArrayOfByte1;
/* 454 */     byte[] arrayOfByte3 = arrayOfByte1;
/*     */     
/* 456 */     if (arrayOfByte2 != null) {
/* 457 */       arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
/*     */       
/* 459 */       System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
/*     */       
/* 461 */       System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 466 */     return this.cipherHelper.calculateChecksum(this.tokenHeader.getSignAlg(), arrayOfByte3, paramArrayOfByte3, paramArrayOfByte2, paramInt1, paramInt2, this.tokenId);
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
/*     */   MessageToken(int paramInt, Krb5Context paramKrb5Context)
/*     */     throws GSSException
/*     */   {
/* 489 */     init(paramInt, paramKrb5Context);
/* 490 */     this.seqNumber = paramKrb5Context.incrementMySequenceNumber();
/*     */   }
/*     */   
/*     */   private void init(int paramInt, Krb5Context paramKrb5Context) throws GSSException {
/* 494 */     this.tokenId = paramInt;
/*     */     
/* 496 */     this.confState = paramKrb5Context.getConfState();
/*     */     
/* 498 */     this.initiator = paramKrb5Context.isInitiator();
/*     */     
/* 500 */     this.cipherHelper = paramKrb5Context.getCipherHelper(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws IOException, GSSException
/*     */   {
/* 511 */     this.gssHeader = new GSSHeader(OID, getKrb5TokenSize());
/* 512 */     this.gssHeader.encode(paramOutputStream);
/* 513 */     this.tokenHeader.encode(paramOutputStream);
/*     */     
/* 515 */     paramOutputStream.write(this.encSeqNumber);
/*     */     
/* 517 */     paramOutputStream.write(this.checksum);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getKrb5TokenSize()
/*     */     throws GSSException
/*     */   {
/* 526 */     return getTokenSize();
/*     */   }
/*     */   
/*     */   protected final int getTokenSize() throws GSSException {
/* 530 */     return 16 + this.cipherHelper.getChecksumLength();
/*     */   }
/*     */   
/*     */   protected static final int getTokenSize(CipherHelper paramCipherHelper) throws GSSException
/*     */   {
/* 535 */     return 16 + paramCipherHelper.getChecksumLength();
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
/*     */   protected abstract int getSealAlg(boolean paramBoolean, int paramInt)
/*     */     throws GSSException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   class MessageTokenHeader
/*     */   {
/*     */     private int tokenId;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int signAlg;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int sealAlg;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 593 */     private byte[] bytes = new byte[8];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public MessageTokenHeader(int paramInt1, boolean paramBoolean, int paramInt2)
/*     */       throws GSSException
/*     */     {
/* 608 */       this.tokenId = paramInt1;
/*     */       
/* 610 */       this.signAlg = MessageToken.this.getSgnAlg(paramInt2);
/*     */       
/* 612 */       this.sealAlg = MessageToken.this.getSealAlg(paramBoolean, paramInt2);
/*     */       
/* 614 */       this.bytes[0] = ((byte)(paramInt1 >>> 8));
/* 615 */       this.bytes[1] = ((byte)paramInt1);
/*     */       
/* 617 */       this.bytes[2] = ((byte)(this.signAlg >>> 8));
/* 618 */       this.bytes[3] = ((byte)this.signAlg);
/*     */       
/* 620 */       this.bytes[4] = ((byte)(this.sealAlg >>> 8));
/* 621 */       this.bytes[5] = ((byte)this.sealAlg);
/*     */       
/* 623 */       this.bytes[6] = -1;
/* 624 */       this.bytes[7] = -1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public MessageTokenHeader(InputStream paramInputStream, MessageProp paramMessageProp)
/*     */       throws IOException
/*     */     {
/* 639 */       GSSToken.readFully(paramInputStream, this.bytes);
/* 640 */       this.tokenId = GSSToken.readInt(this.bytes, 0);
/* 641 */       this.signAlg = GSSToken.readInt(this.bytes, 2);
/* 642 */       this.sealAlg = GSSToken.readInt(this.bytes, 4);
/*     */       
/*     */ 
/*     */ 
/* 646 */       int i = GSSToken.readInt(this.bytes, 6);
/*     */       
/*     */ 
/*     */ 
/* 650 */       switch (this.sealAlg) {
/*     */       case 0: 
/*     */       case 512: 
/*     */       case 4096: 
/* 654 */         paramMessageProp.setPrivacy(true);
/* 655 */         break;
/*     */       
/*     */       default: 
/* 658 */         paramMessageProp.setPrivacy(false);
/*     */       }
/*     */       
/* 661 */       paramMessageProp.setQOP(0);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public final void encode(OutputStream paramOutputStream)
/*     */       throws IOException
/*     */     {
/* 670 */       paramOutputStream.write(this.bytes);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public final int getTokenId()
/*     */     {
/* 681 */       return this.tokenId;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public final int getSignAlg()
/*     */     {
/* 691 */       return this.signAlg;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public final int getSealAlg()
/*     */     {
/* 701 */       return this.sealAlg;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public final byte[] getBytes()
/*     */     {
/* 709 */       return this.bytes;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getSgnAlg(int paramInt)
/*     */     throws GSSException
/*     */   {
/* 719 */     return this.cipherHelper.getSgnAlg();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\MessageToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
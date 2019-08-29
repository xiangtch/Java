/*     */ package sun.security.krb5.internal.crypto;
/*     */ 
/*     */ import java.security.GeneralSecurityException;
/*     */ import sun.security.krb5.KrbCryptoException;
/*     */ import sun.security.krb5.internal.KrbApErrException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ArcFourHmacEType
/*     */   extends EType
/*     */ {
/*     */   public int eType()
/*     */   {
/*  43 */     return 23;
/*     */   }
/*     */   
/*     */   public int minimumPadSize() {
/*  47 */     return 1;
/*     */   }
/*     */   
/*     */   public int confounderSize() {
/*  51 */     return 8;
/*     */   }
/*     */   
/*     */   public int checksumType() {
/*  55 */     return 65398;
/*     */   }
/*     */   
/*     */   public int checksumSize() {
/*  59 */     return ArcFourHmac.getChecksumLength();
/*     */   }
/*     */   
/*     */   public int blockSize() {
/*  63 */     return 1;
/*     */   }
/*     */   
/*     */   public int keyType() {
/*  67 */     return 4;
/*     */   }
/*     */   
/*     */   public int keySize() {
/*  71 */     return 16;
/*     */   }
/*     */   
/*     */   public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbCryptoException
/*     */   {
/*  76 */     byte[] arrayOfByte = new byte[blockSize()];
/*  77 */     return encrypt(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt);
/*     */   }
/*     */   
/*     */   public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) throws KrbCryptoException
/*     */   {
/*     */     try {
/*  83 */       return ArcFourHmac.encrypt(paramArrayOfByte2, paramInt, paramArrayOfByte3, paramArrayOfByte1, 0, paramArrayOfByte1.length);
/*     */     } catch (GeneralSecurityException localGeneralSecurityException) {
/*  85 */       KrbCryptoException localKrbCryptoException = new KrbCryptoException(localGeneralSecurityException.getMessage());
/*  86 */       localKrbCryptoException.initCause(localGeneralSecurityException);
/*  87 */       throw localKrbCryptoException;
/*     */     }
/*     */   }
/*     */   
/*     */   public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbApErrException, KrbCryptoException
/*     */   {
/*  93 */     byte[] arrayOfByte = new byte[blockSize()];
/*  94 */     return decrypt(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt);
/*     */   }
/*     */   
/*     */   public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) throws KrbApErrException, KrbCryptoException
/*     */   {
/*     */     try {
/* 100 */       return ArcFourHmac.decrypt(paramArrayOfByte2, paramInt, paramArrayOfByte3, paramArrayOfByte1, 0, paramArrayOfByte1.length);
/*     */     } catch (GeneralSecurityException localGeneralSecurityException) {
/* 102 */       KrbCryptoException localKrbCryptoException = new KrbCryptoException(localGeneralSecurityException.getMessage());
/* 103 */       localKrbCryptoException.initCause(localGeneralSecurityException);
/* 104 */       throw localKrbCryptoException;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] decryptedData(byte[] paramArrayOfByte)
/*     */   {
/* 112 */     return paramArrayOfByte;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\crypto\ArcFourHmacEType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
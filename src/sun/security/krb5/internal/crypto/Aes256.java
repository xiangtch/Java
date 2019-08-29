/*    */ package sun.security.krb5.internal.crypto;
/*    */ 
/*    */ import java.security.GeneralSecurityException;
/*    */ import sun.security.krb5.KrbCryptoException;
/*    */ import sun.security.krb5.internal.crypto.dk.AesDkCrypto;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Aes256
/*    */ {
/* 39 */   private static final AesDkCrypto CRYPTO = new AesDkCrypto(256);
/*    */   
/*    */ 
/*    */ 
/*    */   public static byte[] stringToKey(char[] paramArrayOfChar, String paramString, byte[] paramArrayOfByte)
/*    */     throws GeneralSecurityException
/*    */   {
/* 46 */     return CRYPTO.stringToKey(paramArrayOfChar, paramString, paramArrayOfByte);
/*    */   }
/*    */   
/*    */   public static int getChecksumLength()
/*    */   {
/* 51 */     return CRYPTO.getChecksumLength();
/*    */   }
/*    */   
/*    */   public static byte[] calculateChecksum(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3) throws GeneralSecurityException
/*    */   {
/* 56 */     return CRYPTO.calculateChecksum(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramInt2, paramInt3);
/*    */   }
/*    */   
/*    */   public static byte[] encrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3)
/*    */     throws GeneralSecurityException, KrbCryptoException
/*    */   {
/* 62 */     return CRYPTO.encrypt(paramArrayOfByte1, paramInt1, paramArrayOfByte2, null, paramArrayOfByte3, paramInt2, paramInt3);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static byte[] encryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3)
/*    */     throws GeneralSecurityException, KrbCryptoException
/*    */   {
/* 70 */     return CRYPTO.encryptRaw(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3);
/*    */   }
/*    */   
/*    */   public static byte[] decrypt(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3)
/*    */     throws GeneralSecurityException
/*    */   {
/* 76 */     return CRYPTO.decrypt(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static byte[] decryptRaw(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3)
/*    */     throws GeneralSecurityException
/*    */   {
/* 86 */     return CRYPTO.decryptRaw(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramArrayOfByte3, paramInt2, paramInt3);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\crypto\Aes256.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
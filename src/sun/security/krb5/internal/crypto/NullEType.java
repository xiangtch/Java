/*    */ package sun.security.krb5.internal.crypto;
/*    */ 
/*    */ import sun.security.krb5.internal.KrbApErrException;
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
/*    */ 
/*    */ 
/*    */ public class NullEType
/*    */   extends EType
/*    */ {
/*    */   public int eType()
/*    */   {
/* 42 */     return 0;
/*    */   }
/*    */   
/*    */   public int minimumPadSize() {
/* 46 */     return 0;
/*    */   }
/*    */   
/*    */   public int confounderSize() {
/* 50 */     return 0;
/*    */   }
/*    */   
/*    */   public int checksumType() {
/* 54 */     return 0;
/*    */   }
/*    */   
/*    */   public int checksumSize() {
/* 58 */     return 0;
/*    */   }
/*    */   
/*    */   public int blockSize() {
/* 62 */     return 1;
/*    */   }
/*    */   
/*    */   public int keyType() {
/* 66 */     return 0;
/*    */   }
/*    */   
/*    */   public int keySize() {
/* 70 */     return 0;
/*    */   }
/*    */   
/*    */   public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) {
/* 74 */     byte[] arrayOfByte = new byte[paramArrayOfByte1.length];
/* 75 */     System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
/* 76 */     return arrayOfByte;
/*    */   }
/*    */   
/*    */   public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) {
/* 80 */     byte[] arrayOfByte = new byte[paramArrayOfByte1.length];
/* 81 */     System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
/* 82 */     return arrayOfByte;
/*    */   }
/*    */   
/*    */   public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws KrbApErrException
/*    */   {
/* 87 */     return (byte[])paramArrayOfByte1.clone();
/*    */   }
/*    */   
/*    */   public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) throws KrbApErrException
/*    */   {
/* 92 */     return (byte[])paramArrayOfByte1.clone();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\crypto\NullEType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
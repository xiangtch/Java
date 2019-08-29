/*      */ package sun.security.jgss.krb5;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.security.GeneralSecurityException;
/*      */ import java.security.Key;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.CipherInputStream;
/*      */ import javax.crypto.CipherOutputStream;
/*      */ import javax.crypto.spec.IvParameterSpec;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import org.ietf.jgss.GSSException;
/*      */ import sun.security.krb5.EncryptionKey;
/*      */ import sun.security.krb5.internal.crypto.Aes128;
/*      */ import sun.security.krb5.internal.crypto.Aes256;
/*      */ import sun.security.krb5.internal.crypto.ArcFourHmac;
/*      */ import sun.security.krb5.internal.crypto.Des3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class CipherHelper
/*      */ {
/*      */   private static final int KG_USAGE_SEAL = 22;
/*      */   private static final int KG_USAGE_SIGN = 23;
/*      */   private static final int KG_USAGE_SEQ = 24;
/*      */   private static final int DES_CHECKSUM_SIZE = 8;
/*      */   private static final int DES_IV_SIZE = 8;
/*      */   private static final int AES_IV_SIZE = 16;
/*      */   private static final int HMAC_CHECKSUM_SIZE = 8;
/*      */   private static final int KG_USAGE_SIGN_MS = 15;
/*   67 */   private static final boolean DEBUG = Krb5Util.DEBUG;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   73 */   private static final byte[] ZERO_IV = new byte[8];
/*   74 */   private static final byte[] ZERO_IV_AES = new byte[16];
/*      */   
/*      */   private int etype;
/*      */   
/*      */   private int sgnAlg;
/*      */   
/*      */   private int sealAlg;
/*      */   private byte[] keybytes;
/*   82 */   private int proto = 0;
/*      */   
/*      */   CipherHelper(EncryptionKey paramEncryptionKey) throws GSSException {
/*   85 */     this.etype = paramEncryptionKey.getEType();
/*   86 */     this.keybytes = paramEncryptionKey.getBytes();
/*      */     
/*   88 */     switch (this.etype) {
/*      */     case 1: 
/*      */     case 3: 
/*   91 */       this.sgnAlg = 0;
/*   92 */       this.sealAlg = 0;
/*   93 */       break;
/*      */     
/*      */     case 16: 
/*   96 */       this.sgnAlg = 1024;
/*   97 */       this.sealAlg = 512;
/*   98 */       break;
/*      */     
/*      */     case 23: 
/*  101 */       this.sgnAlg = 4352;
/*  102 */       this.sealAlg = 4096;
/*  103 */       break;
/*      */     
/*      */     case 17: 
/*      */     case 18: 
/*  107 */       this.sgnAlg = -1;
/*  108 */       this.sealAlg = -1;
/*  109 */       this.proto = 1;
/*  110 */       break;
/*      */     
/*      */     default: 
/*  113 */       throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
/*      */     }
/*      */   }
/*      */   
/*      */   int getSgnAlg()
/*      */   {
/*  119 */     return this.sgnAlg;
/*      */   }
/*      */   
/*      */   int getSealAlg() {
/*  123 */     return this.sealAlg;
/*      */   }
/*      */   
/*      */   int getProto() {
/*  127 */     return this.proto;
/*      */   }
/*      */   
/*      */   int getEType() {
/*  131 */     return this.etype;
/*      */   }
/*      */   
/*      */   boolean isArcFour() {
/*  135 */     boolean bool = false;
/*  136 */     if (this.etype == 23) {
/*  137 */       bool = true;
/*      */     }
/*  139 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */   byte[] calculateChecksum(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws GSSException
/*      */   {
/*  146 */     switch (paramInt1)
/*      */     {
/*      */ 
/*      */     case 0: 
/*      */       try
/*      */       {
/*      */ 
/*  153 */         MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  159 */         localMessageDigest.update(paramArrayOfByte1);
/*      */         
/*      */ 
/*  162 */         localMessageDigest.update(paramArrayOfByte3, paramInt2, paramInt3);
/*      */         
/*  164 */         if (paramArrayOfByte2 != null)
/*      */         {
/*      */ 
/*      */ 
/*  168 */           localMessageDigest.update(paramArrayOfByte2);
/*      */         }
/*      */         
/*      */ 
/*  172 */         paramArrayOfByte3 = localMessageDigest.digest();
/*  173 */         paramInt2 = 0;
/*  174 */         paramInt3 = paramArrayOfByte3.length;
/*      */         
/*      */ 
/*  177 */         paramArrayOfByte1 = null;
/*  178 */         paramArrayOfByte2 = null;
/*      */       }
/*      */       catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/*  181 */         GSSException localGSSException1 = new GSSException(11, -1, "Could not get MD5 Message Digest - " + localNoSuchAlgorithmException.getMessage());
/*  182 */         localGSSException1.initCause(localNoSuchAlgorithmException);
/*  183 */         throw localGSSException1;
/*      */       }
/*      */     
/*      */ 
/*      */     case 512: 
/*  188 */       return getDesCbcChecksum(this.keybytes, paramArrayOfByte1, paramArrayOfByte3, paramInt2, paramInt3);
/*      */     case 1024: 
/*      */       byte[] arrayOfByte1;
/*      */       int j;
/*      */       int i;
/*  193 */       if ((paramArrayOfByte1 == null) && (paramArrayOfByte2 == null)) {
/*  194 */         arrayOfByte1 = paramArrayOfByte3;
/*  195 */         j = paramInt3;
/*  196 */         i = paramInt2;
/*      */       } else {
/*  198 */         j = (paramArrayOfByte1 != null ? paramArrayOfByte1.length : 0) + paramInt3 + (paramArrayOfByte2 != null ? paramArrayOfByte2.length : 0);
/*      */         
/*      */ 
/*  201 */         arrayOfByte1 = new byte[j];
/*  202 */         int k = 0;
/*  203 */         if (paramArrayOfByte1 != null) {
/*  204 */           System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, paramArrayOfByte1.length);
/*  205 */           k = paramArrayOfByte1.length;
/*      */         }
/*  207 */         System.arraycopy(paramArrayOfByte3, paramInt2, arrayOfByte1, k, paramInt3);
/*  208 */         k += paramInt3;
/*  209 */         if (paramArrayOfByte2 != null) {
/*  210 */           System.arraycopy(paramArrayOfByte2, 0, arrayOfByte1, k, paramArrayOfByte2.length);
/*      */         }
/*      */         
/*  213 */         i = 0;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  231 */         return Des3.calculateChecksum(this.keybytes, 23, arrayOfByte1, i, j);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (GeneralSecurityException localGeneralSecurityException1)
/*      */       {
/*      */ 
/*      */ 
/*  239 */         GSSException localGSSException2 = new GSSException(11, -1, "Could not use HMAC-SHA1-DES3-KD signing algorithm - " + localGeneralSecurityException1.getMessage());
/*  240 */         localGSSException2.initCause(localGeneralSecurityException1);
/*  241 */         throw localGSSException2;
/*      */       }
/*      */     case 4352:  byte[] arrayOfByte3;
/*      */       int n;
/*      */       int m;
/*      */       int i1;
/*  247 */       if ((paramArrayOfByte1 == null) && (paramArrayOfByte2 == null)) {
/*  248 */         arrayOfByte3 = paramArrayOfByte3;
/*  249 */         n = paramInt3;
/*  250 */         m = paramInt2;
/*      */       } else {
/*  252 */         n = (paramArrayOfByte1 != null ? paramArrayOfByte1.length : 0) + paramInt3 + (paramArrayOfByte2 != null ? paramArrayOfByte2.length : 0);
/*      */         
/*      */ 
/*  255 */         arrayOfByte3 = new byte[n];
/*  256 */         i1 = 0;
/*      */         
/*  258 */         if (paramArrayOfByte1 != null) {
/*  259 */           System.arraycopy(paramArrayOfByte1, 0, arrayOfByte3, 0, paramArrayOfByte1.length);
/*  260 */           i1 = paramArrayOfByte1.length;
/*      */         }
/*  262 */         System.arraycopy(paramArrayOfByte3, paramInt2, arrayOfByte3, i1, paramInt3);
/*  263 */         i1 += paramInt3;
/*  264 */         if (paramArrayOfByte2 != null) {
/*  265 */           System.arraycopy(paramArrayOfByte2, 0, arrayOfByte3, i1, paramArrayOfByte2.length);
/*      */         }
/*      */         
/*  268 */         m = 0;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  290 */         i1 = 23;
/*  291 */         if (paramInt4 == 257) {
/*  292 */           i1 = 15;
/*      */         }
/*  294 */         localObject = ArcFourHmac.calculateChecksum(this.keybytes, i1, arrayOfByte3, m, n);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  300 */         byte[] arrayOfByte4 = new byte[getChecksumLength()];
/*  301 */         System.arraycopy(localObject, 0, arrayOfByte4, 0, arrayOfByte4.length);
/*      */         
/*      */ 
/*  304 */         return arrayOfByte4;
/*      */       }
/*      */       catch (GeneralSecurityException localGeneralSecurityException2)
/*      */       {
/*  308 */         Object localObject = new GSSException(11, -1, "Could not use HMAC_MD5_ARCFOUR signing algorithm - " + localGeneralSecurityException2.getMessage());
/*  309 */         ((GSSException)localObject).initCause(localGeneralSecurityException2);
/*  310 */         throw ((Throwable)localObject);
/*      */       }
/*      */     }
/*      */     
/*  314 */     throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   byte[] calculateChecksum(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws GSSException
/*      */   {
/*  324 */     int i = (paramArrayOfByte1 != null ? paramArrayOfByte1.length : 0) + paramInt2;
/*      */     
/*      */ 
/*  327 */     byte[] arrayOfByte1 = new byte[i];
/*      */     
/*      */ 
/*  330 */     System.arraycopy(paramArrayOfByte2, paramInt1, arrayOfByte1, 0, paramInt2);
/*      */     
/*      */ 
/*  333 */     if (paramArrayOfByte1 != null) {
/*  334 */       System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, paramInt2, paramArrayOfByte1.length);
/*      */     }
/*      */     
/*      */     GSSException localGSSException;
/*      */     
/*  339 */     switch (this.etype) {
/*      */     case 17: 
/*      */       try {
/*  342 */         return Aes128.calculateChecksum(this.keybytes, paramInt3, arrayOfByte1, 0, i);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (GeneralSecurityException localGeneralSecurityException1)
/*      */       {
/*      */ 
/*      */ 
/*  350 */         localGSSException = new GSSException(11, -1, "Could not use AES128 signing algorithm - " + localGeneralSecurityException1.getMessage());
/*  351 */         localGSSException.initCause(localGeneralSecurityException1);
/*  352 */         throw localGSSException;
/*      */       }
/*      */     case 18: 
/*      */       try
/*      */       {
/*  357 */         return Aes256.calculateChecksum(this.keybytes, paramInt3, arrayOfByte1, 0, i);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (GeneralSecurityException localGeneralSecurityException2)
/*      */       {
/*      */ 
/*      */ 
/*  365 */         localGSSException = new GSSException(11, -1, "Could not use AES256 signing algorithm - " + localGeneralSecurityException2.getMessage());
/*  366 */         localGSSException.initCause(localGeneralSecurityException2);
/*  367 */         throw localGSSException;
/*      */       }
/*      */     }
/*      */     
/*  371 */     throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   byte[] encryptSeq(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
/*      */     throws GSSException
/*      */   {
/*  379 */     switch (this.sgnAlg) {
/*      */     case 0: 
/*      */     case 512: 
/*      */       try {
/*  383 */         Cipher localCipher = getInitializedDes(true, this.keybytes, paramArrayOfByte1);
/*  384 */         return localCipher.doFinal(paramArrayOfByte2, paramInt1, paramInt2);
/*      */ 
/*      */       }
/*      */       catch (GeneralSecurityException localGeneralSecurityException)
/*      */       {
/*  389 */         GSSException localGSSException1 = new GSSException(11, -1, "Could not encrypt sequence number using DES - " + localGeneralSecurityException.getMessage());
/*  390 */         localGSSException1.initCause(localGeneralSecurityException);
/*  391 */         throw localGSSException1;
/*      */       }
/*      */     case 1024: 
/*      */       byte[] arrayOfByte1;
/*      */       
/*  396 */       if (paramArrayOfByte1.length == 8) {
/*  397 */         arrayOfByte1 = paramArrayOfByte1;
/*      */       } else {
/*  399 */         arrayOfByte1 = new byte[8];
/*  400 */         System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, 8);
/*      */       }
/*      */       try {
/*  403 */         return Des3.encryptRaw(this.keybytes, 24, arrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2);
/*      */ 
/*      */       }
/*      */       catch (Exception localException1)
/*      */       {
/*      */ 
/*  409 */         GSSException localGSSException2 = new GSSException(11, -1, "Could not encrypt sequence number using DES3-KD - " + localException1.getMessage());
/*  410 */         localGSSException2.initCause(localException1);
/*  411 */         throw localGSSException2;
/*      */       }
/*      */     case 4352: 
/*      */       byte[] arrayOfByte2;
/*      */       
/*      */ 
/*  417 */       if (paramArrayOfByte1.length == 8) {
/*  418 */         arrayOfByte2 = paramArrayOfByte1;
/*      */       } else {
/*  420 */         arrayOfByte2 = new byte[8];
/*  421 */         System.arraycopy(paramArrayOfByte1, 0, arrayOfByte2, 0, 8);
/*      */       }
/*      */       try
/*      */       {
/*  425 */         return ArcFourHmac.encryptSeq(this.keybytes, 24, arrayOfByte2, paramArrayOfByte2, paramInt1, paramInt2);
/*      */ 
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/*      */ 
/*  431 */         GSSException localGSSException3 = new GSSException(11, -1, "Could not encrypt sequence number using RC4-HMAC - " + localException2.getMessage());
/*  432 */         localGSSException3.initCause(localException2);
/*  433 */         throw localGSSException3;
/*      */       }
/*      */     }
/*      */     
/*  437 */     throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   byte[] decryptSeq(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
/*      */     throws GSSException
/*      */   {
/*  445 */     switch (this.sgnAlg) {
/*      */     case 0: 
/*      */     case 512: 
/*      */       try {
/*  449 */         Cipher localCipher = getInitializedDes(false, this.keybytes, paramArrayOfByte1);
/*  450 */         return localCipher.doFinal(paramArrayOfByte2, paramInt1, paramInt2);
/*      */       }
/*      */       catch (GeneralSecurityException localGeneralSecurityException)
/*      */       {
/*  454 */         GSSException localGSSException1 = new GSSException(11, -1, "Could not decrypt sequence number using DES - " + localGeneralSecurityException.getMessage());
/*  455 */         localGSSException1.initCause(localGeneralSecurityException);
/*  456 */         throw localGSSException1;
/*      */       }
/*      */     case 1024: 
/*      */       byte[] arrayOfByte1;
/*      */       
/*  461 */       if (paramArrayOfByte1.length == 8) {
/*  462 */         arrayOfByte1 = paramArrayOfByte1;
/*      */       } else {
/*  464 */         arrayOfByte1 = new byte[8];
/*  465 */         System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, 8);
/*      */       }
/*      */       try
/*      */       {
/*  469 */         return Des3.decryptRaw(this.keybytes, 24, arrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2);
/*      */ 
/*      */       }
/*      */       catch (Exception localException1)
/*      */       {
/*      */ 
/*  475 */         GSSException localGSSException2 = new GSSException(11, -1, "Could not decrypt sequence number using DES3-KD - " + localException1.getMessage());
/*  476 */         localGSSException2.initCause(localException1);
/*  477 */         throw localGSSException2;
/*      */       }
/*      */     case 4352: 
/*      */       byte[] arrayOfByte2;
/*      */       
/*      */ 
/*  483 */       if (paramArrayOfByte1.length == 8) {
/*  484 */         arrayOfByte2 = paramArrayOfByte1;
/*      */       } else {
/*  486 */         arrayOfByte2 = new byte[8];
/*  487 */         System.arraycopy(paramArrayOfByte1, 0, arrayOfByte2, 0, 8);
/*      */       }
/*      */       try
/*      */       {
/*  491 */         return ArcFourHmac.decryptSeq(this.keybytes, 24, arrayOfByte2, paramArrayOfByte2, paramInt1, paramInt2);
/*      */ 
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/*      */ 
/*  497 */         GSSException localGSSException3 = new GSSException(11, -1, "Could not decrypt sequence number using RC4-HMAC - " + localException2.getMessage());
/*  498 */         localGSSException3.initCause(localException2);
/*  499 */         throw localGSSException3;
/*      */       }
/*      */     }
/*      */     
/*  503 */     throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
/*      */   }
/*      */   
/*      */   int getChecksumLength()
/*      */     throws GSSException
/*      */   {
/*  509 */     switch (this.etype) {
/*      */     case 1: 
/*      */     case 3: 
/*  512 */       return 8;
/*      */     
/*      */     case 16: 
/*  515 */       return Des3.getChecksumLength();
/*      */     
/*      */     case 17: 
/*  518 */       return Aes128.getChecksumLength();
/*      */     case 18: 
/*  520 */       return Aes256.getChecksumLength();
/*      */     
/*      */ 
/*      */     case 23: 
/*  524 */       return 8;
/*      */     }
/*      */     
/*  527 */     throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void decryptData(WrapToken paramWrapToken, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
/*      */     throws GSSException
/*      */   {
/*  540 */     switch (this.sealAlg) {
/*      */     case 0: 
/*  542 */       desCbcDecrypt(paramWrapToken, getDesEncryptionKey(this.keybytes), paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
/*      */       
/*  544 */       break;
/*      */     
/*      */     case 512: 
/*  547 */       des3KdDecrypt(paramWrapToken, paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
/*  548 */       break;
/*      */     
/*      */     case 4096: 
/*  551 */       arcFourDecrypt(paramWrapToken, paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3);
/*  552 */       break;
/*      */     
/*      */     default: 
/*  555 */       throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void decryptData(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
/*      */     throws GSSException
/*      */   {
/*  570 */     switch (this.etype) {
/*      */     case 17: 
/*  572 */       aes128Decrypt(paramWrapToken_v2, paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
/*      */       
/*  574 */       break;
/*      */     case 18: 
/*  576 */       aes256Decrypt(paramWrapToken_v2, paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
/*      */       
/*  578 */       break;
/*      */     default: 
/*  580 */       throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */   void decryptData(WrapToken paramWrapToken, InputStream paramInputStream, int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*      */     throws GSSException, IOException
/*      */   {
/*  589 */     switch (this.sealAlg) {
/*      */     case 0: 
/*  591 */       desCbcDecrypt(paramWrapToken, getDesEncryptionKey(this.keybytes), paramInputStream, paramInt1, paramArrayOfByte, paramInt2);
/*      */       
/*  593 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */     case 512: 
/*  598 */       byte[] arrayOfByte1 = new byte[paramInt1];
/*      */       try {
/*  600 */         Krb5Token.readFully(paramInputStream, arrayOfByte1, 0, paramInt1);
/*      */       } catch (IOException localIOException1) {
/*  602 */         GSSException localGSSException1 = new GSSException(10, -1, "Cannot read complete token");
/*      */         
/*      */ 
/*  605 */         localGSSException1.initCause(localIOException1);
/*  606 */         throw localGSSException1;
/*      */       }
/*      */       
/*  609 */       des3KdDecrypt(paramWrapToken, arrayOfByte1, 0, paramInt1, paramArrayOfByte, paramInt2);
/*  610 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */     case 4096: 
/*  615 */       byte[] arrayOfByte2 = new byte[paramInt1];
/*      */       try {
/*  617 */         Krb5Token.readFully(paramInputStream, arrayOfByte2, 0, paramInt1);
/*      */       } catch (IOException localIOException2) {
/*  619 */         GSSException localGSSException2 = new GSSException(10, -1, "Cannot read complete token");
/*      */         
/*      */ 
/*  622 */         localGSSException2.initCause(localIOException2);
/*  623 */         throw localGSSException2;
/*      */       }
/*      */       
/*  626 */       arcFourDecrypt(paramWrapToken, arrayOfByte2, 0, paramInt1, paramArrayOfByte, paramInt2);
/*  627 */       break;
/*      */     
/*      */     default: 
/*  630 */       throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void decryptData(WrapToken_v2 paramWrapToken_v2, InputStream paramInputStream, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
/*      */     throws GSSException, IOException
/*      */   {
/*  640 */     byte[] arrayOfByte = new byte[paramInt1];
/*      */     try {
/*  642 */       Krb5Token.readFully(paramInputStream, arrayOfByte, 0, paramInt1);
/*      */     } catch (IOException localIOException) {
/*  644 */       GSSException localGSSException = new GSSException(10, -1, "Cannot read complete token");
/*      */       
/*      */ 
/*  647 */       localGSSException.initCause(localIOException);
/*  648 */       throw localGSSException;
/*      */     }
/*  650 */     switch (this.etype) {
/*      */     case 17: 
/*  652 */       aes128Decrypt(paramWrapToken_v2, arrayOfByte, 0, paramInt1, paramArrayOfByte, paramInt2, paramInt3);
/*      */       
/*  654 */       break;
/*      */     case 18: 
/*  656 */       aes256Decrypt(paramWrapToken_v2, arrayOfByte, 0, paramInt1, paramArrayOfByte, paramInt2, paramInt3);
/*      */       
/*  658 */       break;
/*      */     default: 
/*  660 */       throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */   void encryptData(WrapToken paramWrapToken, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, OutputStream paramOutputStream)
/*      */     throws GSSException, IOException
/*      */   {
/*  669 */     switch (this.sealAlg)
/*      */     {
/*      */     case 0: 
/*  672 */       Cipher localCipher = getInitializedDes(true, getDesEncryptionKey(this.keybytes), ZERO_IV);
/*      */       
/*  674 */       CipherOutputStream localCipherOutputStream = new CipherOutputStream(paramOutputStream, localCipher);
/*      */       
/*  676 */       localCipherOutputStream.write(paramArrayOfByte1);
/*      */       
/*  678 */       localCipherOutputStream.write(paramArrayOfByte2, paramInt1, paramInt2);
/*      */       
/*  680 */       localCipherOutputStream.write(paramArrayOfByte3);
/*  681 */       break;
/*      */     
/*      */     case 512: 
/*  684 */       byte[] arrayOfByte1 = des3KdEncrypt(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
/*      */       
/*      */ 
/*      */ 
/*  688 */       paramOutputStream.write(arrayOfByte1);
/*  689 */       break;
/*      */     
/*      */     case 4096: 
/*  692 */       byte[] arrayOfByte2 = arcFourEncrypt(paramWrapToken, paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
/*      */       
/*      */ 
/*      */ 
/*  696 */       paramOutputStream.write(arrayOfByte2);
/*  697 */       break;
/*      */     
/*      */     default: 
/*  700 */       throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   byte[] encryptData(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws GSSException
/*      */   {
/*  718 */     switch (this.etype) {
/*      */     case 17: 
/*  720 */       return aes128Encrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramInt1, paramInt2, paramInt3);
/*      */     
/*      */     case 18: 
/*  723 */       return aes256Encrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramInt1, paramInt2, paramInt3);
/*      */     }
/*      */     
/*  726 */     throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
/*      */   }
/*      */   
/*      */ 
/*      */   void encryptData(WrapToken paramWrapToken, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt3)
/*      */     throws GSSException
/*      */   {
/*      */     Object localObject;
/*      */     
/*  735 */     switch (this.sealAlg) {
/*      */     case 0: 
/*  737 */       int i = paramInt3;
/*      */       
/*  739 */       Cipher localCipher = getInitializedDes(true, getDesEncryptionKey(this.keybytes), ZERO_IV);
/*      */       
/*      */       try
/*      */       {
/*  743 */         i += localCipher.update(paramArrayOfByte1, 0, paramArrayOfByte1.length, paramArrayOfByte4, i);
/*      */         
/*      */ 
/*  746 */         i += localCipher.update(paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte4, i);
/*      */         
/*      */ 
/*  749 */         localCipher.update(paramArrayOfByte3, 0, paramArrayOfByte3.length, paramArrayOfByte4, i);
/*      */         
/*  751 */         localCipher.doFinal();
/*      */       }
/*      */       catch (GeneralSecurityException localGeneralSecurityException) {
/*  754 */         localObject = new GSSException(11, -1, "Could not use DES Cipher - " + localGeneralSecurityException.getMessage());
/*  755 */         ((GSSException)localObject).initCause(localGeneralSecurityException);
/*  756 */         throw ((Throwable)localObject);
/*      */       }
/*      */     
/*      */ 
/*      */     case 512: 
/*  761 */       byte[] arrayOfByte = des3KdEncrypt(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
/*      */       
/*  763 */       System.arraycopy(arrayOfByte, 0, paramArrayOfByte4, paramInt3, arrayOfByte.length);
/*  764 */       break;
/*      */     
/*      */     case 4096: 
/*  767 */       localObject = arcFourEncrypt(paramWrapToken, paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
/*      */       
/*  769 */       System.arraycopy(localObject, 0, paramArrayOfByte4, paramInt3, localObject.length);
/*  770 */       break;
/*      */     
/*      */     default: 
/*  773 */       throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   int encryptData(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, byte[] paramArrayOfByte4, int paramInt3, int paramInt4)
/*      */     throws GSSException
/*      */   {
/*  791 */     byte[] arrayOfByte = null;
/*  792 */     switch (this.etype) {
/*      */     case 17: 
/*  794 */       arrayOfByte = aes128Encrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramInt1, paramInt2, paramInt4);
/*      */       
/*  796 */       break;
/*      */     case 18: 
/*  798 */       arrayOfByte = aes256Encrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramInt1, paramInt2, paramInt4);
/*      */       
/*  800 */       break;
/*      */     default: 
/*  802 */       throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
/*      */     }
/*      */     
/*  805 */     System.arraycopy(arrayOfByte, 0, paramArrayOfByte4, paramInt3, arrayOfByte.length);
/*  806 */     return arrayOfByte.length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] getDesCbcChecksum(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2)
/*      */     throws GSSException
/*      */   {
/*  830 */     Cipher localCipher = getInitializedDes(true, paramArrayOfByte1, ZERO_IV);
/*      */     
/*  832 */     int i = localCipher.getBlockSize();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  840 */     byte[] arrayOfByte1 = new byte[i];
/*      */     
/*  842 */     int j = paramInt2 / i;
/*  843 */     int k = paramInt2 % i;
/*  844 */     if (k == 0)
/*      */     {
/*  846 */       j--;
/*  847 */       System.arraycopy(paramArrayOfByte3, paramInt1 + j * i, arrayOfByte1, 0, i);
/*      */     }
/*      */     else {
/*  850 */       System.arraycopy(paramArrayOfByte3, paramInt1 + j * i, arrayOfByte1, 0, k);
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  856 */       byte[] arrayOfByte2 = new byte[Math.max(i, paramArrayOfByte2 == null ? i : paramArrayOfByte2.length)];
/*      */       
/*      */ 
/*  859 */       if (paramArrayOfByte2 != null)
/*      */       {
/*  861 */         localCipher.update(paramArrayOfByte2, 0, paramArrayOfByte2.length, arrayOfByte2, 0);
/*      */       }
/*      */       
/*      */ 
/*  865 */       for (int m = 0; m < j; m++) {
/*  866 */         localCipher.update(paramArrayOfByte3, paramInt1, i, arrayOfByte2, 0);
/*      */         
/*  868 */         paramInt1 += i;
/*      */       }
/*      */       
/*      */ 
/*  872 */       localObject = new byte[i];
/*  873 */       localCipher.update(arrayOfByte1, 0, i, (byte[])localObject, 0);
/*  874 */       localCipher.doFinal();
/*      */       
/*  876 */       return (byte[])localObject;
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException) {
/*  879 */       Object localObject = new GSSException(11, -1, "Could not use DES Cipher - " + localGeneralSecurityException.getMessage());
/*  880 */       ((GSSException)localObject).initCause(localGeneralSecurityException);
/*  881 */       throw ((Throwable)localObject);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final Cipher getInitializedDes(boolean paramBoolean, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*      */     throws GSSException
/*      */   {
/*      */     try
/*      */     {
/*  899 */       IvParameterSpec localIvParameterSpec = new IvParameterSpec(paramArrayOfByte2);
/*  900 */       localObject = new SecretKeySpec(paramArrayOfByte1, "DES");
/*      */       
/*  902 */       Cipher localCipher = Cipher.getInstance("DES/CBC/NoPadding");
/*  903 */       localCipher.init(paramBoolean ? 1 : 2, (Key)localObject, localIvParameterSpec);
/*      */       
/*      */ 
/*  906 */       return localCipher;
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException) {
/*  909 */       Object localObject = new GSSException(11, -1, localGeneralSecurityException.getMessage());
/*  910 */       ((GSSException)localObject).initCause(localGeneralSecurityException);
/*  911 */       throw ((Throwable)localObject);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void desCbcDecrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, int paramInt3)
/*      */     throws GSSException
/*      */   {
/*      */     try
/*      */     {
/*  937 */       int i = 0;
/*      */       
/*  939 */       localObject = getInitializedDes(false, paramArrayOfByte1, ZERO_IV);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  945 */       i = ((Cipher)localObject).update(paramArrayOfByte2, paramInt1, 8, paramWrapToken.confounder);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  951 */       paramInt1 += 8;
/*  952 */       paramInt2 -= 8;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  961 */       int j = ((Cipher)localObject).getBlockSize();
/*  962 */       int k = paramInt2 / j - 1;
/*      */       
/*      */ 
/*  965 */       for (int m = 0; m < k; m++) {
/*  966 */         i = ((Cipher)localObject).update(paramArrayOfByte2, paramInt1, j, paramArrayOfByte3, paramInt3);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  972 */         paramInt1 += j;
/*  973 */         paramInt3 += j;
/*      */       }
/*      */       
/*      */ 
/*  977 */       byte[] arrayOfByte = new byte[j];
/*  978 */       ((Cipher)localObject).update(paramArrayOfByte2, paramInt1, j, arrayOfByte);
/*      */       
/*  980 */       ((Cipher)localObject).doFinal();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  987 */       int n = arrayOfByte[(j - 1)];
/*  988 */       if ((n < 1) || (n > 8)) {
/*  989 */         throw new GSSException(10, -1, "Invalid padding on Wrap Token");
/*      */       }
/*  991 */       paramWrapToken.padding = WrapToken.pads[n];
/*  992 */       j -= n;
/*      */       
/*      */ 
/*  995 */       System.arraycopy(arrayOfByte, 0, paramArrayOfByte3, paramInt3, j);
/*      */ 
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException)
/*      */     {
/* 1000 */       Object localObject = new GSSException(11, -1, "Could not use DES cipher - " + localGeneralSecurityException.getMessage());
/* 1001 */       ((GSSException)localObject).initCause(localGeneralSecurityException);
/* 1002 */       throw ((Throwable)localObject);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void desCbcDecrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, InputStream paramInputStream, int paramInt1, byte[] paramArrayOfByte2, int paramInt2)
/*      */     throws GSSException, IOException
/*      */   {
/* 1026 */     int i = 0;
/*      */     
/* 1028 */     Cipher localCipher = getInitializedDes(false, paramArrayOfByte1, ZERO_IV);
/*      */     
/* 1030 */     WrapTokenInputStream localWrapTokenInputStream = new WrapTokenInputStream(paramInputStream, paramInt1);
/*      */     
/* 1032 */     CipherInputStream localCipherInputStream = new CipherInputStream(localWrapTokenInputStream, localCipher);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1038 */     i = localCipherInputStream.read(paramWrapToken.confounder);
/*      */     
/* 1040 */     paramInt1 -= i;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1054 */     int j = localCipher.getBlockSize();
/* 1055 */     int k = paramInt1 / j - 1;
/*      */     
/*      */ 
/* 1058 */     for (int m = 0; m < k; m++)
/*      */     {
/* 1060 */       i = localCipherInputStream.read(paramArrayOfByte2, paramInt2, j);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1067 */       paramInt2 += j;
/*      */     }
/*      */     
/*      */ 
/* 1071 */     byte[] arrayOfByte = new byte[j];
/*      */     
/* 1073 */     i = localCipherInputStream.read(arrayOfByte);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1083 */       localCipher.doFinal();
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException) {
/* 1086 */       GSSException localGSSException = new GSSException(11, -1, "Could not use DES cipher - " + localGeneralSecurityException.getMessage());
/* 1087 */       localGSSException.initCause(localGeneralSecurityException);
/* 1088 */       throw localGSSException;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1096 */     int n = arrayOfByte[(j - 1)];
/* 1097 */     if ((n < 1) || (n > 8)) {
/* 1098 */       throw new GSSException(10, -1, "Invalid padding on Wrap Token");
/*      */     }
/* 1100 */     paramWrapToken.padding = WrapToken.pads[n];
/* 1101 */     j -= n;
/*      */     
/*      */ 
/* 1104 */     System.arraycopy(arrayOfByte, 0, paramArrayOfByte2, paramInt2, j);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static byte[] getDesEncryptionKey(byte[] paramArrayOfByte)
/*      */     throws GSSException
/*      */   {
/* 1120 */     if (paramArrayOfByte.length > 8) {
/* 1121 */       throw new GSSException(11, -100, "Invalid DES Key!");
/*      */     }
/*      */     
/* 1124 */     byte[] arrayOfByte = new byte[paramArrayOfByte.length];
/* 1125 */     for (int i = 0; i < paramArrayOfByte.length; i++)
/* 1126 */       arrayOfByte[i] = ((byte)(paramArrayOfByte[i] ^ 0xF0));
/* 1127 */     return arrayOfByte;
/*      */   }
/*      */   
/*      */   private void des3KdDecrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
/*      */     throws GSSException
/*      */   {
/*      */     byte[] arrayOfByte;
/*      */     try
/*      */     {
/* 1136 */       arrayOfByte = Des3.decryptRaw(this.keybytes, 22, ZERO_IV, paramArrayOfByte1, paramInt1, paramInt2);
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException)
/*      */     {
/* 1140 */       GSSException localGSSException = new GSSException(11, -1, "Could not use DES3-KD Cipher - " + localGeneralSecurityException.getMessage());
/* 1141 */       localGSSException.initCause(localGeneralSecurityException);
/* 1142 */       throw localGSSException;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1157 */     int i = arrayOfByte[(arrayOfByte.length - 1)];
/* 1158 */     if ((i < 1) || (i > 8)) {
/* 1159 */       throw new GSSException(10, -1, "Invalid padding on Wrap Token");
/*      */     }
/*      */     
/* 1162 */     paramWrapToken.padding = WrapToken.pads[i];
/* 1163 */     int j = arrayOfByte.length - 8 - i;
/*      */     
/* 1165 */     System.arraycopy(arrayOfByte, 8, paramArrayOfByte2, paramInt3, j);
/*      */     
/*      */ 
/*      */ 
/* 1169 */     System.arraycopy(arrayOfByte, 0, paramWrapToken.confounder, 0, 8);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] des3KdEncrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3)
/*      */     throws GSSException
/*      */   {
/* 1178 */     byte[] arrayOfByte1 = new byte[paramArrayOfByte1.length + paramInt2 + paramArrayOfByte3.length];
/* 1179 */     System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, paramArrayOfByte1.length);
/* 1180 */     System.arraycopy(paramArrayOfByte2, paramInt1, arrayOfByte1, paramArrayOfByte1.length, paramInt2);
/* 1181 */     System.arraycopy(paramArrayOfByte3, 0, arrayOfByte1, paramArrayOfByte1.length + paramInt2, paramArrayOfByte3.length);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1188 */       return Des3.encryptRaw(this.keybytes, 22, ZERO_IV, arrayOfByte1, 0, arrayOfByte1.length);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */ 
/*      */ 
/* 1196 */       GSSException localGSSException = new GSSException(11, -1, "Could not use DES3-KD Cipher - " + localException.getMessage());
/* 1197 */       localGSSException.initCause(localException);
/* 1198 */       throw localGSSException;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void arcFourDecrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
/*      */     throws GSSException
/*      */   {
/* 1209 */     byte[] arrayOfByte1 = decryptSeq(paramWrapToken.getChecksum(), paramWrapToken
/* 1210 */       .getEncSeqNumber(), 0, 8);
/*      */     byte[] arrayOfByte2;
/*      */     try
/*      */     {
/* 1214 */       arrayOfByte2 = ArcFourHmac.decryptRaw(this.keybytes, 22, ZERO_IV, paramArrayOfByte1, paramInt1, paramInt2, arrayOfByte1);
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException)
/*      */     {
/* 1218 */       GSSException localGSSException = new GSSException(11, -1, "Could not use ArcFour Cipher - " + localGeneralSecurityException.getMessage());
/* 1219 */       localGSSException.initCause(localGeneralSecurityException);
/* 1220 */       throw localGSSException;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1235 */     int i = arrayOfByte2[(arrayOfByte2.length - 1)];
/* 1236 */     if (i < 1) {
/* 1237 */       throw new GSSException(10, -1, "Invalid padding on Wrap Token");
/*      */     }
/*      */     
/* 1240 */     paramWrapToken.padding = WrapToken.pads[i];
/* 1241 */     int j = arrayOfByte2.length - 8 - i;
/*      */     
/* 1243 */     System.arraycopy(arrayOfByte2, 8, paramArrayOfByte2, paramInt3, j);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1250 */     System.arraycopy(arrayOfByte2, 0, paramWrapToken.confounder, 0, 8);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] arcFourEncrypt(WrapToken paramWrapToken, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3)
/*      */     throws GSSException
/*      */   {
/* 1259 */     byte[] arrayOfByte1 = new byte[paramArrayOfByte1.length + paramInt2 + paramArrayOfByte3.length];
/* 1260 */     System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, paramArrayOfByte1.length);
/* 1261 */     System.arraycopy(paramArrayOfByte2, paramInt1, arrayOfByte1, paramArrayOfByte1.length, paramInt2);
/* 1262 */     System.arraycopy(paramArrayOfByte3, 0, arrayOfByte1, paramArrayOfByte1.length + paramInt2, paramArrayOfByte3.length);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1268 */     byte[] arrayOfByte2 = new byte[4];
/* 1269 */     WrapToken.writeBigEndian(paramWrapToken.getSequenceNumber(), arrayOfByte2);
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1275 */       return ArcFourHmac.encryptRaw(this.keybytes, 22, arrayOfByte2, arrayOfByte1, 0, arrayOfByte1.length);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */ 
/*      */ 
/* 1283 */       GSSException localGSSException = new GSSException(11, -1, "Could not use ArcFour Cipher - " + localException.getMessage());
/* 1284 */       localGSSException.initCause(localException);
/* 1285 */       throw localGSSException;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] aes128Encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws GSSException
/*      */   {
/* 1299 */     byte[] arrayOfByte1 = new byte[paramArrayOfByte1.length + paramInt2 + paramArrayOfByte2.length];
/* 1300 */     System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, paramArrayOfByte1.length);
/* 1301 */     System.arraycopy(paramArrayOfByte3, paramInt1, arrayOfByte1, paramArrayOfByte1.length, paramInt2);
/* 1302 */     System.arraycopy(paramArrayOfByte2, 0, arrayOfByte1, paramArrayOfByte1.length + paramInt2, paramArrayOfByte2.length);
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1307 */       return Aes128.encryptRaw(this.keybytes, paramInt3, ZERO_IV_AES, arrayOfByte1, 0, arrayOfByte1.length);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */ 
/*      */ 
/* 1316 */       GSSException localGSSException = new GSSException(11, -1, "Could not use AES128 Cipher - " + localException.getMessage());
/* 1317 */       localGSSException.initCause(localException);
/* 1318 */       throw localGSSException;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void aes128Decrypt(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
/*      */     throws GSSException
/*      */   {
/* 1326 */     byte[] arrayOfByte = null;
/*      */     try
/*      */     {
/* 1329 */       arrayOfByte = Aes128.decryptRaw(this.keybytes, paramInt4, ZERO_IV_AES, paramArrayOfByte1, paramInt1, paramInt2);
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException)
/*      */     {
/* 1333 */       GSSException localGSSException = new GSSException(11, -1, "Could not use AES128 Cipher - " + localGeneralSecurityException.getMessage());
/* 1334 */       localGSSException.initCause(localGeneralSecurityException);
/* 1335 */       throw localGSSException;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1348 */     int i = arrayOfByte.length - 16 - 16;
/*      */     
/* 1350 */     System.arraycopy(arrayOfByte, 16, paramArrayOfByte2, paramInt3, i);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] aes256Encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, int paramInt3)
/*      */     throws GSSException
/*      */   {
/* 1368 */     byte[] arrayOfByte1 = new byte[paramArrayOfByte1.length + paramInt2 + paramArrayOfByte2.length];
/* 1369 */     System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, paramArrayOfByte1.length);
/* 1370 */     System.arraycopy(paramArrayOfByte3, paramInt1, arrayOfByte1, paramArrayOfByte1.length, paramInt2);
/* 1371 */     System.arraycopy(paramArrayOfByte2, 0, arrayOfByte1, paramArrayOfByte1.length + paramInt2, paramArrayOfByte2.length);
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1377 */       return Aes256.encryptRaw(this.keybytes, paramInt3, ZERO_IV_AES, arrayOfByte1, 0, arrayOfByte1.length);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */ 
/*      */ 
/* 1385 */       GSSException localGSSException = new GSSException(11, -1, "Could not use AES256 Cipher - " + localException.getMessage());
/* 1386 */       localGSSException.initCause(localException);
/* 1387 */       throw localGSSException;
/*      */     }
/*      */   }
/*      */   
/*      */   private void aes256Decrypt(WrapToken_v2 paramWrapToken_v2, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
/*      */     throws GSSException
/*      */   {
/*      */     byte[] arrayOfByte;
/*      */     try
/*      */     {
/* 1397 */       arrayOfByte = Aes256.decryptRaw(this.keybytes, paramInt4, ZERO_IV_AES, paramArrayOfByte1, paramInt1, paramInt2);
/*      */     }
/*      */     catch (GeneralSecurityException localGeneralSecurityException)
/*      */     {
/* 1401 */       GSSException localGSSException = new GSSException(11, -1, "Could not use AES128 Cipher - " + localGeneralSecurityException.getMessage());
/* 1402 */       localGSSException.initCause(localGeneralSecurityException);
/* 1403 */       throw localGSSException;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1416 */     int i = arrayOfByte.length - 16 - 16;
/*      */     
/* 1418 */     System.arraycopy(arrayOfByte, 16, paramArrayOfByte2, paramInt3, i);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   class WrapTokenInputStream
/*      */     extends InputStream
/*      */   {
/*      */     private InputStream is;
/*      */     
/*      */ 
/*      */ 
/*      */     private int length;
/*      */     
/*      */ 
/*      */     private int remaining;
/*      */     
/*      */ 
/*      */     private int temp;
/*      */     
/*      */ 
/*      */ 
/*      */     public WrapTokenInputStream(InputStream paramInputStream, int paramInt)
/*      */     {
/* 1443 */       this.is = paramInputStream;
/* 1444 */       this.length = paramInt;
/* 1445 */       this.remaining = paramInt;
/*      */     }
/*      */     
/*      */     public final int read() throws IOException {
/* 1449 */       if (this.remaining == 0) {
/* 1450 */         return -1;
/*      */       }
/* 1452 */       this.temp = this.is.read();
/* 1453 */       if (this.temp != -1)
/* 1454 */         this.remaining -= this.temp;
/* 1455 */       return this.temp;
/*      */     }
/*      */     
/*      */     public final int read(byte[] paramArrayOfByte) throws IOException
/*      */     {
/* 1460 */       if (this.remaining == 0) {
/* 1461 */         return -1;
/*      */       }
/* 1463 */       this.temp = Math.min(this.remaining, paramArrayOfByte.length);
/* 1464 */       this.temp = this.is.read(paramArrayOfByte, 0, this.temp);
/* 1465 */       if (this.temp != -1)
/* 1466 */         this.remaining -= this.temp;
/* 1467 */       return this.temp;
/*      */     }
/*      */     
/*      */ 
/*      */     public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */       throws IOException
/*      */     {
/* 1474 */       if (this.remaining == 0) {
/* 1475 */         return -1;
/*      */       }
/* 1477 */       this.temp = Math.min(this.remaining, paramInt2);
/* 1478 */       this.temp = this.is.read(paramArrayOfByte, paramInt1, this.temp);
/* 1479 */       if (this.temp != -1)
/* 1480 */         this.remaining -= this.temp;
/* 1481 */       return this.temp;
/*      */     }
/*      */     
/*      */     public final long skip(long paramLong) throws IOException
/*      */     {
/* 1486 */       if (this.remaining == 0) {
/* 1487 */         return 0L;
/*      */       }
/* 1489 */       this.temp = ((int)Math.min(this.remaining, paramLong));
/* 1490 */       this.temp = ((int)this.is.skip(this.temp));
/* 1491 */       this.remaining -= this.temp;
/* 1492 */       return this.temp;
/*      */     }
/*      */     
/*      */     public final int available() throws IOException
/*      */     {
/* 1497 */       return Math.min(this.remaining, this.is.available());
/*      */     }
/*      */     
/*      */     public final void close() throws IOException {
/* 1501 */       this.remaining = 0;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\krb5\CipherHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
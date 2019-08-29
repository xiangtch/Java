/*     */ package sun.security.krb5.internal.crypto;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import javax.crypto.Cipher;
/*     */ import sun.security.krb5.Config;
/*     */ import sun.security.krb5.EncryptionKey;
/*     */ import sun.security.krb5.KrbCryptoException;
/*     */ import sun.security.krb5.KrbException;
/*     */ import sun.security.krb5.internal.KdcErrException;
/*     */ import sun.security.krb5.internal.Krb5;
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
/*     */ 
/*     */ public abstract class EType
/*     */ {
/*  50 */   private static final boolean DEBUG = Krb5.DEBUG;
/*     */   private static boolean allowWeakCrypto;
/*     */   
/*     */   static {
/*  54 */     initStatic();
/*     */   }
/*     */   
/*     */   public static void initStatic() {
/*  58 */     boolean bool = false;
/*     */     try {
/*  60 */       Config localConfig = Config.getInstance();
/*  61 */       String str = localConfig.get(new String[] { "libdefaults", "allow_weak_crypto" });
/*  62 */       if ((str != null) && (str.equals("true"))) bool = true;
/*     */     } catch (Exception localException) {
/*  64 */       if (DEBUG) {
/*  65 */         System.out.println("Exception in getting allow_weak_crypto, using default value " + localException
/*     */         
/*  67 */           .getMessage());
/*     */       }
/*     */     }
/*  70 */     allowWeakCrypto = bool;
/*     */   }
/*     */   
/*     */   public static EType getInstance(int paramInt) throws KdcErrException
/*     */   {
/*  75 */     Object localObject = null;
/*  76 */     String str1 = null;
/*  77 */     switch (paramInt) {
/*     */     case 0: 
/*  79 */       localObject = new NullEType();
/*  80 */       str1 = "sun.security.krb5.internal.crypto.NullEType";
/*  81 */       break;
/*     */     case 1: 
/*  83 */       localObject = new DesCbcCrcEType();
/*  84 */       str1 = "sun.security.krb5.internal.crypto.DesCbcCrcEType";
/*  85 */       break;
/*     */     case 3: 
/*  87 */       localObject = new DesCbcMd5EType();
/*  88 */       str1 = "sun.security.krb5.internal.crypto.DesCbcMd5EType";
/*  89 */       break;
/*     */     
/*     */     case 16: 
/*  92 */       localObject = new Des3CbcHmacSha1KdEType();
/*  93 */       str1 = "sun.security.krb5.internal.crypto.Des3CbcHmacSha1KdEType";
/*     */       
/*  95 */       break;
/*     */     
/*     */     case 17: 
/*  98 */       localObject = new Aes128CtsHmacSha1EType();
/*  99 */       str1 = "sun.security.krb5.internal.crypto.Aes128CtsHmacSha1EType";
/*     */       
/* 101 */       break;
/*     */     
/*     */     case 18: 
/* 104 */       localObject = new Aes256CtsHmacSha1EType();
/* 105 */       str1 = "sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType";
/*     */       
/* 107 */       break;
/*     */     
/*     */     case 23: 
/* 110 */       localObject = new ArcFourHmacEType();
/* 111 */       str1 = "sun.security.krb5.internal.crypto.ArcFourHmacEType";
/* 112 */       break;
/*     */     case 2: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
/*     */     case 12: case 13: case 14: case 15: case 19: case 20: case 21: case 22: default: 
/* 115 */       String str2 = "encryption type = " + toString(paramInt) + " (" + paramInt + ")";
/*     */       
/* 117 */       throw new KdcErrException(14, str2);
/*     */     }
/* 119 */     if (DEBUG) {
/* 120 */       System.out.println(">>> EType: " + str1);
/*     */     }
/* 122 */     return (EType)localObject;
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
/*     */   public int dataSize(byte[] paramArrayOfByte)
/*     */   {
/* 160 */     return paramArrayOfByte.length - startOfData();
/*     */   }
/*     */   
/*     */   public int padSize(byte[] paramArrayOfByte) {
/* 164 */     return 
/* 165 */       paramArrayOfByte.length - confounderSize() - checksumSize() - dataSize(paramArrayOfByte);
/*     */   }
/*     */   
/*     */   public int startOfChecksum() {
/* 169 */     return confounderSize();
/*     */   }
/*     */   
/*     */   public int startOfData() {
/* 173 */     return confounderSize() + checksumSize();
/*     */   }
/*     */   
/*     */   public int startOfPad(byte[] paramArrayOfByte) {
/* 177 */     return confounderSize() + checksumSize() + dataSize(paramArrayOfByte);
/*     */   }
/*     */   
/*     */   public byte[] decryptedData(byte[] paramArrayOfByte) {
/* 181 */     int i = dataSize(paramArrayOfByte);
/* 182 */     byte[] arrayOfByte = new byte[i];
/* 183 */     System.arraycopy(paramArrayOfByte, startOfData(), arrayOfByte, 0, i);
/* 184 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 191 */   private static final int[] BUILTIN_ETYPES = { 18, 17, 16, 23, 1, 3 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 200 */   private static final int[] BUILTIN_ETYPES_NOAES256 = { 17, 16, 23, 1, 3 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int[] getBuiltInDefaults()
/*     */   {
/* 211 */     int i = 0;
/*     */     try {
/* 213 */       i = Cipher.getMaxAllowedKeyLength("AES");
/*     */     }
/*     */     catch (Exception localException) {}
/*     */     
/*     */     int[] arrayOfInt;
/* 218 */     if (i < 256) {
/* 219 */       arrayOfInt = BUILTIN_ETYPES_NOAES256;
/*     */     } else {
/* 221 */       arrayOfInt = BUILTIN_ETYPES;
/*     */     }
/* 223 */     if (!allowWeakCrypto)
/*     */     {
/* 225 */       return Arrays.copyOfRange(arrayOfInt, 0, arrayOfInt.length - 2);
/*     */     }
/* 227 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int[] getDefaults(String paramString)
/*     */     throws KrbException
/*     */   {
/* 238 */     Config localConfig = null;
/*     */     try {
/* 240 */       localConfig = Config.getInstance();
/*     */     } catch (KrbException localKrbException) {
/* 242 */       if (DEBUG) {
/* 243 */         System.out.println("Exception while getting " + paramString + localKrbException
/* 244 */           .getMessage());
/* 245 */         System.out.println("Using default builtin etypes");
/*     */       }
/* 247 */       return getBuiltInDefaults();
/*     */     }
/* 249 */     return localConfig.defaultEtype(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int[] getDefaults(String paramString, EncryptionKey[] paramArrayOfEncryptionKey)
/*     */     throws KrbException
/*     */   {
/* 262 */     int[] arrayOfInt = getDefaults(paramString);
/*     */     
/* 264 */     ArrayList localArrayList = new ArrayList(arrayOfInt.length);
/* 265 */     for (int i = 0; i < arrayOfInt.length; i++) {
/* 266 */       if (EncryptionKey.findKey(arrayOfInt[i], paramArrayOfEncryptionKey) != null) {
/* 267 */         localArrayList.add(Integer.valueOf(arrayOfInt[i]));
/*     */       }
/*     */     }
/* 270 */     i = localArrayList.size();
/* 271 */     if (i <= 0) {
/* 272 */       StringBuffer localStringBuffer = new StringBuffer();
/* 273 */       for (int k = 0; k < paramArrayOfEncryptionKey.length; k++) {
/* 274 */         localStringBuffer.append(toString(paramArrayOfEncryptionKey[k].getEType()));
/* 275 */         localStringBuffer.append(" ");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 280 */       throw new KrbException("Do not have keys of types listed in " + paramString + " available; only have keys of following type: " + localStringBuffer.toString());
/*     */     }
/* 282 */     arrayOfInt = new int[i];
/* 283 */     for (int j = 0; j < i; j++) {
/* 284 */       arrayOfInt[j] = ((Integer)localArrayList.get(j)).intValue();
/*     */     }
/* 286 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */   public static boolean isSupported(int paramInt, int[] paramArrayOfInt)
/*     */   {
/* 291 */     for (int i = 0; i < paramArrayOfInt.length; i++) {
/* 292 */       if (paramInt == paramArrayOfInt[i]) {
/* 293 */         return true;
/*     */       }
/*     */     }
/* 296 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isSupported(int paramInt) {
/* 300 */     int[] arrayOfInt = getBuiltInDefaults();
/* 301 */     return isSupported(paramInt, arrayOfInt);
/*     */   }
/*     */   
/*     */   public static String toString(int paramInt) {
/* 305 */     switch (paramInt) {
/*     */     case 0: 
/* 307 */       return "NULL";
/*     */     case 1: 
/* 309 */       return "DES CBC mode with CRC-32";
/*     */     case 2: 
/* 311 */       return "DES CBC mode with MD4";
/*     */     case 3: 
/* 313 */       return "DES CBC mode with MD5";
/*     */     case 4: 
/* 315 */       return "reserved";
/*     */     case 5: 
/* 317 */       return "DES3 CBC mode with MD5";
/*     */     case 6: 
/* 319 */       return "reserved";
/*     */     case 7: 
/* 321 */       return "DES3 CBC mode with SHA1";
/*     */     case 9: 
/* 323 */       return "DSA with SHA1- Cms0ID";
/*     */     case 10: 
/* 325 */       return "MD5 with RSA encryption - Cms0ID";
/*     */     case 11: 
/* 327 */       return "SHA1 with RSA encryption - Cms0ID";
/*     */     case 12: 
/* 329 */       return "RC2 CBC mode with Env0ID";
/*     */     case 13: 
/* 331 */       return "RSA encryption with Env0ID";
/*     */     case 14: 
/* 333 */       return "RSAES-0AEP-ENV-0ID";
/*     */     case 15: 
/* 335 */       return "DES-EDE3-CBC-ENV-0ID";
/*     */     case 16: 
/* 337 */       return "DES3 CBC mode with SHA1-KD";
/*     */     case 17: 
/* 339 */       return "AES128 CTS mode with HMAC SHA1-96";
/*     */     case 18: 
/* 341 */       return "AES256 CTS mode with HMAC SHA1-96";
/*     */     case 23: 
/* 343 */       return "RC4 with HMAC";
/*     */     case 24: 
/* 345 */       return "RC4 with HMAC EXP";
/*     */     }
/*     */     
/* 348 */     return "Unknown (" + paramInt + ")";
/*     */   }
/*     */   
/*     */   public abstract int eType();
/*     */   
/*     */   public abstract int minimumPadSize();
/*     */   
/*     */   public abstract int confounderSize();
/*     */   
/*     */   public abstract int checksumType();
/*     */   
/*     */   public abstract int checksumSize();
/*     */   
/*     */   public abstract int blockSize();
/*     */   
/*     */   public abstract int keyType();
/*     */   
/*     */   public abstract int keySize();
/*     */   
/*     */   public abstract byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
/*     */     throws KrbCryptoException;
/*     */   
/*     */   public abstract byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
/*     */     throws KrbCryptoException;
/*     */   
/*     */   public abstract byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
/*     */     throws KrbApErrException, KrbCryptoException;
/*     */   
/*     */   public abstract byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
/*     */     throws KrbApErrException, KrbCryptoException;
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\crypto\EType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
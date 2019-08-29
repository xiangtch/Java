/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.util.Arrays;
/*     */ import javax.crypto.spec.DESKeySpec;
/*     */ import javax.crypto.spec.DESedeKeySpec;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.krb5.internal.Krb5;
/*     */ import sun.security.krb5.internal.PAData.SaltAndParams;
/*     */ import sun.security.krb5.internal.ccache.CCacheOutputStream;
/*     */ import sun.security.krb5.internal.crypto.Aes128;
/*     */ import sun.security.krb5.internal.crypto.Aes256;
/*     */ import sun.security.krb5.internal.crypto.ArcFourHmac;
/*     */ import sun.security.krb5.internal.crypto.Des;
/*     */ import sun.security.krb5.internal.crypto.Des3;
/*     */ import sun.security.krb5.internal.crypto.EType;
/*     */ import sun.security.krb5.internal.ktab.KeyTab;
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
/*     */ public class EncryptionKey
/*     */   implements Cloneable
/*     */ {
/*  70 */   public static final EncryptionKey NULL_KEY = new EncryptionKey(new byte[0], 0, null);
/*     */   
/*     */   private int keyType;
/*     */   
/*     */   private byte[] keyValue;
/*     */   
/*     */   private Integer kvno;
/*  77 */   private static final boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */   public synchronized int getEType() {
/*  80 */     return this.keyType;
/*     */   }
/*     */   
/*     */   public final Integer getKeyVersionNumber() {
/*  84 */     return this.kvno;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final byte[] getBytes()
/*     */   {
/*  93 */     return this.keyValue;
/*     */   }
/*     */   
/*     */   public synchronized Object clone() {
/*  97 */     return new EncryptionKey(this.keyValue, this.keyType, this.kvno);
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
/*     */   public static EncryptionKey[] acquireSecretKeys(PrincipalName paramPrincipalName, String paramString)
/*     */   {
/* 113 */     if (paramPrincipalName == null) {
/* 114 */       throw new IllegalArgumentException("Cannot have null pricipal name to look in keytab.");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 119 */     KeyTab localKeyTab = KeyTab.getInstance(paramString);
/* 120 */     return localKeyTab.readServiceKeys(paramPrincipalName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EncryptionKey acquireSecretKey(PrincipalName paramPrincipalName, char[] paramArrayOfChar, int paramInt, PAData.SaltAndParams paramSaltAndParams)
/*     */     throws KrbException
/*     */   {
/*     */     String str;
/*     */     
/*     */ 
/*     */ 
/*     */     byte[] arrayOfByte;
/*     */     
/*     */ 
/*     */ 
/* 137 */     if (paramSaltAndParams != null) {
/* 138 */       str = paramSaltAndParams.salt != null ? paramSaltAndParams.salt : paramPrincipalName.getSalt();
/* 139 */       arrayOfByte = paramSaltAndParams.params;
/*     */     } else {
/* 141 */       str = paramPrincipalName.getSalt();
/* 142 */       arrayOfByte = null;
/*     */     }
/* 144 */     return acquireSecretKey(paramArrayOfChar, str, paramInt, arrayOfByte);
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
/*     */   public static EncryptionKey acquireSecretKey(char[] paramArrayOfChar, String paramString, int paramInt, byte[] paramArrayOfByte)
/*     */     throws KrbException
/*     */   {
/* 159 */     return new EncryptionKey(
/* 160 */       stringToKey(paramArrayOfChar, paramString, paramArrayOfByte, paramInt), paramInt, null);
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
/*     */   public static EncryptionKey[] acquireSecretKeys(char[] paramArrayOfChar, String paramString)
/*     */     throws KrbException
/*     */   {
/* 180 */     int[] arrayOfInt = EType.getDefaults("default_tkt_enctypes");
/*     */     
/* 182 */     EncryptionKey[] arrayOfEncryptionKey = new EncryptionKey[arrayOfInt.length];
/* 183 */     for (int i = 0; i < arrayOfInt.length; i++) {
/* 184 */       if (EType.isSupported(arrayOfInt[i]))
/*     */       {
/* 186 */         arrayOfEncryptionKey[i] = new EncryptionKey(stringToKey(paramArrayOfChar, paramString, null, arrayOfInt[i]), arrayOfInt[i], null);
/*     */ 
/*     */       }
/* 189 */       else if (DEBUG) {
/* 190 */         System.out.println("Encryption Type " + 
/* 191 */           EType.toString(arrayOfInt[i]) + " is not supported/enabled");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 196 */     return arrayOfEncryptionKey;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncryptionKey(byte[] paramArrayOfByte, int paramInt, Integer paramInteger)
/*     */   {
/* 204 */     if (paramArrayOfByte != null) {
/* 205 */       this.keyValue = new byte[paramArrayOfByte.length];
/* 206 */       System.arraycopy(paramArrayOfByte, 0, this.keyValue, 0, paramArrayOfByte.length);
/*     */     } else {
/* 208 */       throw new IllegalArgumentException("EncryptionKey: Key bytes cannot be null!");
/*     */     }
/*     */     
/* 211 */     this.keyType = paramInt;
/* 212 */     this.kvno = paramInteger;
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
/*     */   public EncryptionKey(int paramInt, byte[] paramArrayOfByte)
/*     */   {
/* 225 */     this(paramArrayOfByte, paramInt, null);
/*     */   }
/*     */   
/*     */   private static byte[] stringToKey(char[] paramArrayOfChar, String paramString, byte[] paramArrayOfByte, int paramInt)
/*     */     throws KrbCryptoException
/*     */   {
/* 231 */     char[] arrayOfChar1 = paramString.toCharArray();
/* 232 */     char[] arrayOfChar2 = new char[paramArrayOfChar.length + arrayOfChar1.length];
/* 233 */     System.arraycopy(paramArrayOfChar, 0, arrayOfChar2, 0, paramArrayOfChar.length);
/* 234 */     System.arraycopy(arrayOfChar1, 0, arrayOfChar2, paramArrayOfChar.length, arrayOfChar1.length);
/* 235 */     Arrays.fill(arrayOfChar1, '0');
/*     */     try {
/*     */       byte[] arrayOfByte;
/* 238 */       switch (paramInt) {
/*     */       case 1: 
/*     */       case 3: 
/* 241 */         return Des.string_to_key_bytes(arrayOfChar2);
/*     */       
/*     */       case 16: 
/* 244 */         return Des3.stringToKey(arrayOfChar2);
/*     */       
/*     */       case 23: 
/* 247 */         return ArcFourHmac.stringToKey(paramArrayOfChar);
/*     */       
/*     */       case 17: 
/* 250 */         return Aes128.stringToKey(paramArrayOfChar, paramString, paramArrayOfByte);
/*     */       
/*     */       case 18: 
/* 253 */         return Aes256.stringToKey(paramArrayOfChar, paramString, paramArrayOfByte);
/*     */       }
/*     */       
/*     */       
/* 257 */       throw new IllegalArgumentException("encryption type " + EType.toString(paramInt) + " not supported");
/*     */     }
/*     */     catch (GeneralSecurityException localGeneralSecurityException)
/*     */     {
/* 261 */       KrbCryptoException localKrbCryptoException = new KrbCryptoException(localGeneralSecurityException.getMessage());
/* 262 */       localKrbCryptoException.initCause(localGeneralSecurityException);
/* 263 */       throw localKrbCryptoException;
/*     */     } finally {
/* 265 */       Arrays.fill(arrayOfChar2, '0');
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public EncryptionKey(char[] paramArrayOfChar, String paramString1, String paramString2)
/*     */     throws KrbCryptoException
/*     */   {
/* 274 */     if ((paramString2 == null) || (paramString2.equalsIgnoreCase("DES"))) {
/* 275 */       this.keyType = 3;
/* 276 */     } else if (paramString2.equalsIgnoreCase("DESede")) {
/* 277 */       this.keyType = 16;
/* 278 */     } else if (paramString2.equalsIgnoreCase("AES128")) {
/* 279 */       this.keyType = 17;
/* 280 */     } else if (paramString2.equalsIgnoreCase("ArcFourHmac")) {
/* 281 */       this.keyType = 23;
/* 282 */     } else if (paramString2.equalsIgnoreCase("AES256")) {
/* 283 */       this.keyType = 18;
/*     */       
/* 285 */       if (!EType.isSupported(this.keyType)) {
/* 286 */         throw new IllegalArgumentException("Algorithm " + paramString2 + " not enabled");
/*     */       }
/*     */     }
/*     */     else {
/* 290 */       throw new IllegalArgumentException("Algorithm " + paramString2 + " not supported");
/*     */     }
/*     */     
/*     */ 
/* 294 */     this.keyValue = stringToKey(paramArrayOfChar, paramString1, null, this.keyType);
/* 295 */     this.kvno = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncryptionKey(EncryptionKey paramEncryptionKey)
/*     */     throws KrbCryptoException
/*     */   {
/* 306 */     this.keyValue = Confounder.bytes(paramEncryptionKey.keyValue.length);
/* 307 */     for (int i = 0; i < this.keyValue.length; i++) {
/* 308 */       int tmp32_31 = i; byte[] tmp32_28 = this.keyValue;tmp32_28[tmp32_31] = ((byte)(tmp32_28[tmp32_31] ^ paramEncryptionKey.keyValue[i]));
/*     */     }
/* 310 */     this.keyType = paramEncryptionKey.keyType;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 315 */       if ((this.keyType == 3) || (this.keyType == 1))
/*     */       {
/*     */ 
/* 318 */         if (!DESKeySpec.isParityAdjusted(this.keyValue, 0)) {
/* 319 */           this.keyValue = Des.set_parity(this.keyValue);
/*     */         }
/*     */         
/* 322 */         if (DESKeySpec.isWeak(this.keyValue, 0)) {
/* 323 */           this.keyValue[7] = ((byte)(this.keyValue[7] ^ 0xF0));
/*     */         }
/*     */       }
/*     */       
/* 327 */       if (this.keyType == 16)
/*     */       {
/* 329 */         if (!DESedeKeySpec.isParityAdjusted(this.keyValue, 0)) {
/* 330 */           this.keyValue = Des3.parityFix(this.keyValue);
/*     */         }
/*     */         
/* 333 */         byte[] arrayOfByte = new byte[8];
/* 334 */         for (int j = 0; j < this.keyValue.length; j += 8) {
/* 335 */           System.arraycopy(this.keyValue, j, arrayOfByte, 0, 8);
/* 336 */           if (DESKeySpec.isWeak(arrayOfByte, 0)) {
/* 337 */             this.keyValue[(j + 7)] = ((byte)(this.keyValue[(j + 7)] ^ 0xF0));
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (GeneralSecurityException localGeneralSecurityException) {
/* 342 */       KrbCryptoException localKrbCryptoException = new KrbCryptoException(localGeneralSecurityException.getMessage());
/* 343 */       localKrbCryptoException.initCause(localGeneralSecurityException);
/* 344 */       throw localKrbCryptoException;
/*     */     }
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
/*     */   public EncryptionKey(DerValue paramDerValue)
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 361 */     if (paramDerValue.getTag() != 48) {
/* 362 */       throw new Asn1Exception(906);
/*     */     }
/* 364 */     DerValue localDerValue = paramDerValue.getData().getDerValue();
/* 365 */     if ((localDerValue.getTag() & 0x1F) == 0) {
/* 366 */       this.keyType = localDerValue.getData().getBigInteger().intValue();
/*     */     }
/*     */     else
/* 369 */       throw new Asn1Exception(906);
/* 370 */     localDerValue = paramDerValue.getData().getDerValue();
/* 371 */     if ((localDerValue.getTag() & 0x1F) == 1) {
/* 372 */       this.keyValue = localDerValue.getData().getOctetString();
/*     */     }
/*     */     else
/* 375 */       throw new Asn1Exception(906);
/* 376 */     if (localDerValue.getData().available() > 0) {
/* 377 */       throw new Asn1Exception(906);
/*     */     }
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
/*     */   public synchronized byte[] asn1Encode()
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 404 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 405 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 406 */     localDerOutputStream2.putInteger(this.keyType);
/* 407 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
/*     */     
/* 409 */     localDerOutputStream2 = new DerOutputStream();
/* 410 */     localDerOutputStream2.putOctetString(this.keyValue);
/* 411 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
/*     */     
/* 413 */     localDerOutputStream2 = new DerOutputStream();
/* 414 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/* 415 */     return localDerOutputStream2.toByteArray();
/*     */   }
/*     */   
/*     */   public synchronized void destroy() {
/* 419 */     if (this.keyValue != null) {
/* 420 */       for (int i = 0; i < this.keyValue.length; i++) {
/* 421 */         this.keyValue[i] = 0;
/*     */       }
/*     */     }
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
/*     */   public static EncryptionKey parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 444 */     if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte))
/*     */     {
/* 446 */       return null;
/*     */     }
/* 448 */     DerValue localDerValue1 = paramDerInputStream.getDerValue();
/* 449 */     if (paramByte != (localDerValue1.getTag() & 0x1F)) {
/* 450 */       throw new Asn1Exception(906);
/*     */     }
/* 452 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 453 */     return new EncryptionKey(localDerValue2);
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
/*     */   public synchronized void writeKey(CCacheOutputStream paramCCacheOutputStream)
/*     */     throws IOException
/*     */   {
/* 468 */     paramCCacheOutputStream.write16(this.keyType);
/*     */     
/* 470 */     paramCCacheOutputStream.write16(this.keyType);
/* 471 */     paramCCacheOutputStream.write32(this.keyValue.length);
/* 472 */     for (int i = 0; i < this.keyValue.length; i++) {
/* 473 */       paramCCacheOutputStream.write8(this.keyValue[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 478 */     return new String("EncryptionKey: keyType=" + this.keyType + " kvno=" + this.kvno + " keyValue (hex dump)=" + ((this.keyValue == null) || (this.keyValue.length == 0) ? " Empty Key" : new StringBuilder().append('\n')
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 483 */       .append(Krb5.hexDumper.encodeBuffer(this.keyValue)).append('\n').toString()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EncryptionKey findKey(int paramInt, EncryptionKey[] paramArrayOfEncryptionKey)
/*     */     throws KrbException
/*     */   {
/* 492 */     return findKey(paramInt, null, paramArrayOfEncryptionKey);
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
/*     */   private static boolean versionMatches(Integer paramInteger1, Integer paramInteger2)
/*     */   {
/* 506 */     if ((paramInteger1 == null) || (paramInteger1.intValue() == 0) || (paramInteger2 == null) || (paramInteger2.intValue() == 0)) {
/* 507 */       return true;
/*     */     }
/* 509 */     return paramInteger1.equals(paramInteger2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EncryptionKey findKey(int paramInt, Integer paramInteger, EncryptionKey[] paramArrayOfEncryptionKey)
/*     */     throws KrbException
/*     */   {
/* 520 */     if (!EType.isSupported(paramInt))
/*     */     {
/* 522 */       throw new KrbException("Encryption type " + EType.toString(paramInt) + " is not supported/enabled");
/*     */     }
/*     */     
/*     */ 
/* 526 */     int j = 0;
/*     */     
/*     */ 
/*     */ 
/* 530 */     int k = 0;
/* 531 */     EncryptionKey localEncryptionKey = null;
/*     */     int i;
/* 533 */     Integer localInteger; for (int m = 0; m < paramArrayOfEncryptionKey.length; m++) {
/* 534 */       i = paramArrayOfEncryptionKey[m].getEType();
/* 535 */       if (EType.isSupported(i)) {
/* 536 */         localInteger = paramArrayOfEncryptionKey[m].getKeyVersionNumber();
/* 537 */         if (paramInt == i) {
/* 538 */           j = 1;
/* 539 */           if (versionMatches(paramInteger, localInteger))
/* 540 */             return paramArrayOfEncryptionKey[m];
/* 541 */           if (localInteger.intValue() > k)
/*     */           {
/* 543 */             localEncryptionKey = paramArrayOfEncryptionKey[m];
/* 544 */             k = localInteger.intValue();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 552 */     if ((paramInt == 1) || (paramInt == 3))
/*     */     {
/* 554 */       for (m = 0; m < paramArrayOfEncryptionKey.length; m++) {
/* 555 */         i = paramArrayOfEncryptionKey[m].getEType();
/* 556 */         if ((i == 1) || (i == 3))
/*     */         {
/* 558 */           localInteger = paramArrayOfEncryptionKey[m].getKeyVersionNumber();
/* 559 */           j = 1;
/* 560 */           if (versionMatches(paramInteger, localInteger))
/* 561 */             return new EncryptionKey(paramInt, paramArrayOfEncryptionKey[m].getBytes());
/* 562 */           if (localInteger.intValue() > k) {
/* 563 */             localEncryptionKey = new EncryptionKey(paramInt, paramArrayOfEncryptionKey[m].getBytes());
/* 564 */             k = localInteger.intValue();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 569 */     if (j != 0) {
/* 570 */       return localEncryptionKey;
/*     */     }
/*     */     
/*     */ 
/* 574 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\EncryptionKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
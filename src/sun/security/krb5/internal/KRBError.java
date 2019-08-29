/*     */ package sun.security.krb5.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.krb5.Asn1Exception;
/*     */ import sun.security.krb5.Checksum;
/*     */ import sun.security.krb5.PrincipalName;
/*     */ import sun.security.krb5.Realm;
/*     */ import sun.security.krb5.RealmException;
/*     */ import sun.security.krb5.internal.util.KerberosString;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KRBError
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 3643809337475284503L;
/*     */   private int pvno;
/*     */   private int msgType;
/*     */   private KerberosTime cTime;
/*     */   private Integer cuSec;
/*     */   private KerberosTime sTime;
/*     */   private Integer suSec;
/*     */   private int errorCode;
/*     */   private PrincipalName cname;
/*     */   private PrincipalName sname;
/*     */   private String eText;
/*     */   private byte[] eData;
/*     */   private Checksum eCksum;
/*     */   private PAData[] pa;
/* 101 */   private static boolean DEBUG = Krb5.DEBUG;
/*     */   
/*     */   private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException
/*     */   {
/*     */     try {
/* 106 */       init(new DerValue((byte[])paramObjectInputStream.readObject()));
/* 107 */       parseEData(this.eData);
/*     */     } catch (Exception localException) {
/* 109 */       throw new IOException(localException);
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException
/*     */   {
/*     */     try {
/* 116 */       paramObjectOutputStream.writeObject(asn1Encode());
/*     */     } catch (Exception localException) {
/* 118 */       throw new IOException(localException);
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
/*     */   public KRBError(APOptions paramAPOptions, KerberosTime paramKerberosTime1, Integer paramInteger1, KerberosTime paramKerberosTime2, Integer paramInteger2, int paramInt, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, String paramString, byte[] paramArrayOfByte)
/*     */     throws IOException, Asn1Exception
/*     */   {
/* 134 */     this.pvno = 5;
/* 135 */     this.msgType = 30;
/* 136 */     this.cTime = paramKerberosTime1;
/* 137 */     this.cuSec = paramInteger1;
/* 138 */     this.sTime = paramKerberosTime2;
/* 139 */     this.suSec = paramInteger2;
/* 140 */     this.errorCode = paramInt;
/* 141 */     this.cname = paramPrincipalName1;
/* 142 */     this.sname = paramPrincipalName2;
/* 143 */     this.eText = paramString;
/* 144 */     this.eData = paramArrayOfByte;
/*     */     
/* 146 */     parseEData(this.eData);
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
/*     */   public KRBError(APOptions paramAPOptions, KerberosTime paramKerberosTime1, Integer paramInteger1, KerberosTime paramKerberosTime2, Integer paramInteger2, int paramInt, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, String paramString, byte[] paramArrayOfByte, Checksum paramChecksum)
/*     */     throws IOException, Asn1Exception
/*     */   {
/* 162 */     this.pvno = 5;
/* 163 */     this.msgType = 30;
/* 164 */     this.cTime = paramKerberosTime1;
/* 165 */     this.cuSec = paramInteger1;
/* 166 */     this.sTime = paramKerberosTime2;
/* 167 */     this.suSec = paramInteger2;
/* 168 */     this.errorCode = paramInt;
/* 169 */     this.cname = paramPrincipalName1;
/* 170 */     this.sname = paramPrincipalName2;
/* 171 */     this.eText = paramString;
/* 172 */     this.eData = paramArrayOfByte;
/* 173 */     this.eCksum = paramChecksum;
/*     */     
/* 175 */     parseEData(this.eData);
/*     */   }
/*     */   
/*     */   public KRBError(byte[] paramArrayOfByte) throws Asn1Exception, RealmException, KrbApErrException, IOException
/*     */   {
/* 180 */     init(new DerValue(paramArrayOfByte));
/* 181 */     parseEData(this.eData);
/*     */   }
/*     */   
/*     */   public KRBError(DerValue paramDerValue) throws Asn1Exception, RealmException, KrbApErrException, IOException
/*     */   {
/* 186 */     init(paramDerValue);
/* 187 */     showDebug();
/* 188 */     parseEData(this.eData);
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
/*     */   private void parseEData(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 215 */     if (paramArrayOfByte == null) {
/* 216 */       return;
/*     */     }
/*     */     
/*     */ 
/* 220 */     if ((this.errorCode == 25) || (this.errorCode == 24))
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 226 */         parsePAData(paramArrayOfByte);
/*     */       } catch (Exception localException) {
/* 228 */         if (DEBUG) {
/* 229 */           System.out.println("Unable to parse eData field of KRB-ERROR:\n" + new HexDumpEncoder()
/* 230 */             .encodeBuffer(paramArrayOfByte));
/*     */         }
/* 232 */         IOException localIOException = new IOException("Unable to parse eData field of KRB-ERROR");
/*     */         
/* 234 */         localIOException.initCause(localException);
/* 235 */         throw localIOException;
/*     */       }
/*     */       
/* 238 */     } else if (DEBUG) {
/* 239 */       System.out.println("Unknown eData field of KRB-ERROR:\n" + new HexDumpEncoder()
/* 240 */         .encodeBuffer(paramArrayOfByte));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void parsePAData(byte[] paramArrayOfByte)
/*     */     throws IOException, Asn1Exception
/*     */   {
/* 251 */     DerValue localDerValue1 = new DerValue(paramArrayOfByte);
/* 252 */     ArrayList localArrayList = new ArrayList();
/* 253 */     while (localDerValue1.data.available() > 0)
/*     */     {
/* 255 */       DerValue localDerValue2 = localDerValue1.data.getDerValue();
/* 256 */       PAData localPAData = new PAData(localDerValue2);
/* 257 */       localArrayList.add(localPAData);
/* 258 */       if (DEBUG) {
/* 259 */         System.out.println(localPAData);
/*     */       }
/*     */     }
/* 262 */     this.pa = ((PAData[])localArrayList.toArray(new PAData[localArrayList.size()]));
/*     */   }
/*     */   
/*     */   public final KerberosTime getServerTime() {
/* 266 */     return this.sTime;
/*     */   }
/*     */   
/*     */   public final KerberosTime getClientTime() {
/* 270 */     return this.cTime;
/*     */   }
/*     */   
/*     */   public final Integer getServerMicroSeconds() {
/* 274 */     return this.suSec;
/*     */   }
/*     */   
/*     */   public final Integer getClientMicroSeconds() {
/* 278 */     return this.cuSec;
/*     */   }
/*     */   
/*     */   public final int getErrorCode() {
/* 282 */     return this.errorCode;
/*     */   }
/*     */   
/*     */   public final PAData[] getPA()
/*     */   {
/* 287 */     return this.pa;
/*     */   }
/*     */   
/*     */   public final String getErrorString() {
/* 291 */     return this.eText;
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
/*     */   private void init(DerValue paramDerValue)
/*     */     throws Asn1Exception, RealmException, KrbApErrException, IOException
/*     */   {
/* 306 */     if (((paramDerValue.getTag() & 0x1F) != 30) || 
/* 307 */       (paramDerValue.isApplication() != true) || 
/* 308 */       (paramDerValue.isConstructed() != true)) {
/* 309 */       throw new Asn1Exception(906);
/*     */     }
/* 311 */     DerValue localDerValue1 = paramDerValue.getData().getDerValue();
/* 312 */     if (localDerValue1.getTag() != 48) {
/* 313 */       throw new Asn1Exception(906);
/*     */     }
/* 315 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 316 */     if ((localDerValue2.getTag() & 0x1F) == 0)
/*     */     {
/* 318 */       this.pvno = localDerValue2.getData().getBigInteger().intValue();
/* 319 */       if (this.pvno != 5)
/* 320 */         throw new KrbApErrException(39);
/*     */     } else {
/* 322 */       throw new Asn1Exception(906);
/*     */     }
/*     */     
/* 325 */     localDerValue2 = localDerValue1.getData().getDerValue();
/* 326 */     if ((localDerValue2.getTag() & 0x1F) == 1) {
/* 327 */       this.msgType = localDerValue2.getData().getBigInteger().intValue();
/* 328 */       if (this.msgType != 30) {
/* 329 */         throw new KrbApErrException(40);
/*     */       }
/*     */     } else {
/* 332 */       throw new Asn1Exception(906);
/*     */     }
/*     */     
/* 335 */     this.cTime = KerberosTime.parse(localDerValue1.getData(), (byte)2, true);
/* 336 */     if ((localDerValue1.getData().peekByte() & 0x1F) == 3) {
/* 337 */       localDerValue2 = localDerValue1.getData().getDerValue();
/* 338 */       this.cuSec = new Integer(localDerValue2.getData().getBigInteger().intValue());
/*     */     } else {
/* 340 */       this.cuSec = null; }
/* 341 */     this.sTime = KerberosTime.parse(localDerValue1.getData(), (byte)4, false);
/* 342 */     localDerValue2 = localDerValue1.getData().getDerValue();
/* 343 */     if ((localDerValue2.getTag() & 0x1F) == 5) {
/* 344 */       this.suSec = new Integer(localDerValue2.getData().getBigInteger().intValue());
/*     */     } else
/* 346 */       throw new Asn1Exception(906);
/* 347 */     localDerValue2 = localDerValue1.getData().getDerValue();
/* 348 */     if ((localDerValue2.getTag() & 0x1F) == 6) {
/* 349 */       this.errorCode = localDerValue2.getData().getBigInteger().intValue();
/*     */     } else
/* 351 */       throw new Asn1Exception(906);
/* 352 */     Realm localRealm1 = Realm.parse(localDerValue1.getData(), (byte)7, true);
/* 353 */     this.cname = PrincipalName.parse(localDerValue1.getData(), (byte)8, true, localRealm1);
/* 354 */     Realm localRealm2 = Realm.parse(localDerValue1.getData(), (byte)9, false);
/* 355 */     this.sname = PrincipalName.parse(localDerValue1.getData(), (byte)10, false, localRealm2);
/* 356 */     this.eText = null;
/* 357 */     this.eData = null;
/* 358 */     this.eCksum = null;
/* 359 */     if ((localDerValue1.getData().available() > 0) && 
/* 360 */       ((localDerValue1.getData().peekByte() & 0x1F) == 11)) {
/* 361 */       localDerValue2 = localDerValue1.getData().getDerValue();
/*     */       
/* 363 */       this.eText = new KerberosString(localDerValue2.getData().getDerValue()).toString();
/*     */     }
/*     */     
/* 366 */     if ((localDerValue1.getData().available() > 0) && 
/* 367 */       ((localDerValue1.getData().peekByte() & 0x1F) == 12)) {
/* 368 */       localDerValue2 = localDerValue1.getData().getDerValue();
/* 369 */       this.eData = localDerValue2.getData().getOctetString();
/*     */     }
/*     */     
/* 372 */     if (localDerValue1.getData().available() > 0) {
/* 373 */       this.eCksum = Checksum.parse(localDerValue1.getData(), (byte)13, true);
/*     */     }
/* 375 */     if (localDerValue1.getData().available() > 0) {
/* 376 */       throw new Asn1Exception(906);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void showDebug()
/*     */   {
/* 383 */     if (DEBUG) {
/* 384 */       System.out.println(">>>KRBError:");
/* 385 */       if (this.cTime != null)
/* 386 */         System.out.println("\t cTime is " + this.cTime.toDate().toString() + " " + this.cTime.toDate().getTime());
/* 387 */       if (this.cuSec != null) {
/* 388 */         System.out.println("\t cuSec is " + this.cuSec.intValue());
/*     */       }
/*     */       
/* 391 */       System.out.println("\t sTime is " + this.sTime.toDate()
/* 392 */         .toString() + " " + this.sTime.toDate().getTime());
/* 393 */       System.out.println("\t suSec is " + this.suSec);
/* 394 */       System.out.println("\t error code is " + this.errorCode);
/* 395 */       System.out.println("\t error Message is " + Krb5.getErrorMessage(this.errorCode));
/* 396 */       if (this.cname != null) {
/* 397 */         System.out.println("\t cname is " + this.cname.toString());
/*     */       }
/* 399 */       if (this.sname != null) {
/* 400 */         System.out.println("\t sname is " + this.sname.toString());
/*     */       }
/* 402 */       if (this.eData != null) {
/* 403 */         System.out.println("\t eData provided.");
/*     */       }
/* 405 */       if (this.eCksum != null) {
/* 406 */         System.out.println("\t checksum provided.");
/*     */       }
/* 408 */       System.out.println("\t msgType is " + this.msgType);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] asn1Encode()
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 419 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 420 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*     */     
/* 422 */     localDerOutputStream1.putInteger(BigInteger.valueOf(this.pvno));
/* 423 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream1);
/* 424 */     localDerOutputStream1 = new DerOutputStream();
/* 425 */     localDerOutputStream1.putInteger(BigInteger.valueOf(this.msgType));
/* 426 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream1);
/*     */     
/* 428 */     if (this.cTime != null) {
/* 429 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), this.cTime.asn1Encode());
/*     */     }
/* 431 */     if (this.cuSec != null) {
/* 432 */       localDerOutputStream1 = new DerOutputStream();
/* 433 */       localDerOutputStream1.putInteger(BigInteger.valueOf(this.cuSec.intValue()));
/* 434 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream1);
/*     */     }
/*     */     
/* 437 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), this.sTime.asn1Encode());
/* 438 */     localDerOutputStream1 = new DerOutputStream();
/* 439 */     localDerOutputStream1.putInteger(BigInteger.valueOf(this.suSec.intValue()));
/* 440 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), localDerOutputStream1);
/* 441 */     localDerOutputStream1 = new DerOutputStream();
/* 442 */     localDerOutputStream1.putInteger(BigInteger.valueOf(this.errorCode));
/* 443 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), localDerOutputStream1);
/*     */     
/* 445 */     if (this.cname != null) {
/* 446 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)7), this.cname.getRealm().asn1Encode());
/* 447 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)8), this.cname.asn1Encode());
/*     */     }
/*     */     
/* 450 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)9), this.sname.getRealm().asn1Encode());
/* 451 */     localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)10), this.sname.asn1Encode());
/*     */     
/* 453 */     if (this.eText != null) {
/* 454 */       localDerOutputStream1 = new DerOutputStream();
/* 455 */       localDerOutputStream1.putDerValue(new KerberosString(this.eText).toDerValue());
/* 456 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)11), localDerOutputStream1);
/*     */     }
/* 458 */     if (this.eData != null) {
/* 459 */       localDerOutputStream1 = new DerOutputStream();
/* 460 */       localDerOutputStream1.putOctetString(this.eData);
/* 461 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)12), localDerOutputStream1);
/*     */     }
/* 463 */     if (this.eCksum != null) {
/* 464 */       localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)13), this.eCksum.asn1Encode());
/*     */     }
/*     */     
/* 467 */     localDerOutputStream1 = new DerOutputStream();
/* 468 */     localDerOutputStream1.write((byte)48, localDerOutputStream2);
/* 469 */     localDerOutputStream2 = new DerOutputStream();
/* 470 */     localDerOutputStream2.write(DerValue.createTag((byte)64, true, (byte)30), localDerOutputStream1);
/* 471 */     return localDerOutputStream2.toByteArray();
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 475 */     if (this == paramObject) {
/* 476 */       return true;
/*     */     }
/*     */     
/* 479 */     if (!(paramObject instanceof KRBError)) {
/* 480 */       return false;
/*     */     }
/*     */     
/* 483 */     KRBError localKRBError = (KRBError)paramObject;
/* 484 */     if ((this.pvno == localKRBError.pvno) && (this.msgType == localKRBError.msgType))
/*     */     {
/* 486 */       if ((!isEqual(this.cTime, localKRBError.cTime)) || 
/* 487 */         (!isEqual(this.cuSec, localKRBError.cuSec)) || 
/* 488 */         (!isEqual(this.sTime, localKRBError.sTime)) || 
/* 489 */         (!isEqual(this.suSec, localKRBError.suSec)) || (this.errorCode != localKRBError.errorCode)) {}
/*     */     }
/* 484 */     return 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 491 */       (isEqual(this.cname, localKRBError.cname)) && 
/* 492 */       (isEqual(this.sname, localKRBError.sname)) && 
/* 493 */       (isEqual(this.eText, localKRBError.eText)) && 
/* 494 */       (Arrays.equals(this.eData, localKRBError.eData)) && 
/* 495 */       (isEqual(this.eCksum, localKRBError.eCksum));
/*     */   }
/*     */   
/*     */   private static boolean isEqual(Object paramObject1, Object paramObject2) {
/* 499 */     return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 503 */     int i = 17;
/* 504 */     i = 37 * i + this.pvno;
/* 505 */     i = 37 * i + this.msgType;
/* 506 */     if (this.cTime != null) i = 37 * i + this.cTime.hashCode();
/* 507 */     if (this.cuSec != null) i = 37 * i + this.cuSec.hashCode();
/* 508 */     if (this.sTime != null) i = 37 * i + this.sTime.hashCode();
/* 509 */     if (this.suSec != null) i = 37 * i + this.suSec.hashCode();
/* 510 */     i = 37 * i + this.errorCode;
/* 511 */     if (this.cname != null) i = 37 * i + this.cname.hashCode();
/* 512 */     if (this.sname != null) i = 37 * i + this.sname.hashCode();
/* 513 */     if (this.eText != null) i = 37 * i + this.eText.hashCode();
/* 514 */     i = 37 * i + Arrays.hashCode(this.eData);
/* 515 */     if (this.eCksum != null) i = 37 * i + this.eCksum.hashCode();
/* 516 */     return i;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\internal\KRBError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
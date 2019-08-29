/*     */ package sun.security.krb5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import java.util.Vector;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.security.krb5.internal.ccache.CCacheOutputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrincipalName
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int KRB_NT_UNKNOWN = 0;
/*     */   public static final int KRB_NT_PRINCIPAL = 1;
/*     */   public static final int KRB_NT_SRV_INST = 2;
/*     */   public static final int KRB_NT_SRV_HST = 3;
/*     */   public static final int KRB_NT_SRV_XHST = 4;
/*     */   public static final int KRB_NT_UID = 5;
/*     */   public static final String TGS_DEFAULT_SRV_NAME = "krbtgt";
/*     */   public static final int TGS_DEFAULT_NT = 2;
/*     */   public static final char NAME_COMPONENT_SEPARATOR = '/';
/*     */   public static final char NAME_REALM_SEPARATOR = '@';
/*     */   public static final char REALM_COMPONENT_SEPARATOR = '.';
/*     */   public static final String NAME_COMPONENT_SEPARATOR_STR = "/";
/*     */   public static final String NAME_REALM_SEPARATOR_STR = "@";
/*     */   public static final String REALM_COMPONENT_SEPARATOR_STR = ".";
/*     */   private final int nameType;
/*     */   private final String[] nameStrings;
/*     */   private final Realm nameRealm;
/*     */   private final boolean realmDeduced;
/* 134 */   private transient String salt = null;
/*     */   
/*     */ 
/*     */   private static final long NAME_STRINGS_OFFSET;
/*     */   
/*     */ 
/*     */   private static final Unsafe UNSAFE;
/*     */   
/*     */ 
/*     */ 
/*     */   public PrincipalName(int paramInt, String[] paramArrayOfString, Realm paramRealm)
/*     */   {
/* 146 */     if (paramRealm == null) {
/* 147 */       throw new IllegalArgumentException("Null realm not allowed");
/*     */     }
/* 149 */     validateNameStrings(paramArrayOfString);
/* 150 */     this.nameType = paramInt;
/* 151 */     this.nameStrings = ((String[])paramArrayOfString.clone());
/* 152 */     this.nameRealm = paramRealm;
/* 153 */     this.realmDeduced = false;
/*     */   }
/*     */   
/*     */   public PrincipalName(String[] paramArrayOfString, String paramString) throws RealmException
/*     */   {
/* 158 */     this(0, paramArrayOfString, new Realm(paramString));
/*     */   }
/*     */   
/*     */   private static void validateNameStrings(String[] paramArrayOfString)
/*     */   {
/* 163 */     if (paramArrayOfString == null) {
/* 164 */       throw new IllegalArgumentException("Null nameStrings not allowed");
/*     */     }
/* 166 */     if (paramArrayOfString.length == 0) {
/* 167 */       throw new IllegalArgumentException("Empty nameStrings not allowed");
/*     */     }
/* 169 */     for (String str : paramArrayOfString) {
/* 170 */       if (str == null) {
/* 171 */         throw new IllegalArgumentException("Null nameString not allowed");
/*     */       }
/* 173 */       if (str.isEmpty()) {
/* 174 */         throw new IllegalArgumentException("Empty nameString not allowed");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 181 */       PrincipalName localPrincipalName = (PrincipalName)super.clone();
/* 182 */       UNSAFE.putObject(this, NAME_STRINGS_OFFSET, this.nameStrings.clone());
/* 183 */       return localPrincipalName;
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 185 */       throw new AssertionError("Should never happen");
/*     */     }
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 193 */       Unsafe localUnsafe = Unsafe.getUnsafe();
/* 194 */       NAME_STRINGS_OFFSET = localUnsafe.objectFieldOffset(PrincipalName.class
/* 195 */         .getDeclaredField("nameStrings"));
/* 196 */       UNSAFE = localUnsafe;
/*     */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 198 */       throw new Error(localReflectiveOperationException);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 204 */     if (this == paramObject) {
/* 205 */       return true;
/*     */     }
/* 207 */     if ((paramObject instanceof PrincipalName)) {
/* 208 */       PrincipalName localPrincipalName = (PrincipalName)paramObject;
/* 209 */       return (this.nameRealm.equals(localPrincipalName.nameRealm)) && 
/* 210 */         (Arrays.equals(this.nameStrings, localPrincipalName.nameStrings));
/*     */     }
/* 212 */     return false;
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
/*     */   public PrincipalName(DerValue paramDerValue, Realm paramRealm)
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 243 */     if (paramRealm == null) {
/* 244 */       throw new IllegalArgumentException("Null realm not allowed");
/*     */     }
/* 246 */     this.realmDeduced = false;
/* 247 */     this.nameRealm = paramRealm;
/*     */     
/* 249 */     if (paramDerValue == null) {
/* 250 */       throw new IllegalArgumentException("Null encoding not allowed");
/*     */     }
/* 252 */     if (paramDerValue.getTag() != 48) {
/* 253 */       throw new Asn1Exception(906);
/*     */     }
/* 255 */     DerValue localDerValue1 = paramDerValue.getData().getDerValue();
/* 256 */     Object localObject; if ((localDerValue1.getTag() & 0x1F) == 0) {
/* 257 */       localObject = localDerValue1.getData().getBigInteger();
/* 258 */       this.nameType = ((BigInteger)localObject).intValue();
/*     */     } else {
/* 260 */       throw new Asn1Exception(906);
/*     */     }
/* 262 */     localDerValue1 = paramDerValue.getData().getDerValue();
/* 263 */     if ((localDerValue1.getTag() & 0x1F) == 1) {
/* 264 */       localObject = localDerValue1.getData().getDerValue();
/* 265 */       if (((DerValue)localObject).getTag() != 48) {
/* 266 */         throw new Asn1Exception(906);
/*     */       }
/* 268 */       Vector localVector = new Vector();
/*     */       
/* 270 */       while (((DerValue)localObject).getData().available() > 0) {
/* 271 */         DerValue localDerValue2 = ((DerValue)localObject).getData().getDerValue();
/* 272 */         String str = new KerberosString(localDerValue2).toString();
/* 273 */         localVector.addElement(str);
/*     */       }
/* 275 */       this.nameStrings = new String[localVector.size()];
/* 276 */       localVector.copyInto(this.nameStrings);
/* 277 */       validateNameStrings(this.nameStrings);
/*     */     } else {
/* 279 */       throw new Asn1Exception(906);
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
/*     */   public static PrincipalName parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean, Realm paramRealm)
/*     */     throws Asn1Exception, IOException, RealmException
/*     */   {
/* 304 */     if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte))
/*     */     {
/* 306 */       return null; }
/* 307 */     DerValue localDerValue1 = paramDerInputStream.getDerValue();
/* 308 */     if (paramByte != (localDerValue1.getTag() & 0x1F)) {
/* 309 */       throw new Asn1Exception(906);
/*     */     }
/* 311 */     DerValue localDerValue2 = localDerValue1.getData().getDerValue();
/* 312 */     if (paramRealm == null) {
/* 313 */       paramRealm = Realm.getDefault();
/*     */     }
/* 315 */     return new PrincipalName(localDerValue2, paramRealm);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String[] parseName(String paramString)
/*     */   {
/* 324 */     Vector localVector = new Vector();
/* 325 */     String str1 = paramString;
/* 326 */     int i = 0;
/* 327 */     int j = 0;
/*     */     
/*     */     String str2;
/* 330 */     while (i < str1.length()) {
/* 331 */       if (str1.charAt(i) == '/')
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 336 */         if ((i > 0) && (str1.charAt(i - 1) == '\\'))
/*     */         {
/* 338 */           str1 = str1.substring(0, i - 1) + str1.substring(i, str1.length());
/* 339 */           continue;
/*     */         }
/*     */         
/* 342 */         if (j <= i) {
/* 343 */           str2 = str1.substring(j, i);
/* 344 */           localVector.addElement(str2);
/*     */         }
/* 346 */         j = i + 1;
/*     */ 
/*     */       }
/* 349 */       else if (str1.charAt(i) == '@')
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 354 */         if ((i > 0) && (str1.charAt(i - 1) == '\\'))
/*     */         {
/* 356 */           str1 = str1.substring(0, i - 1) + str1.substring(i, str1.length());
/* 357 */           continue;
/*     */         }
/* 359 */         if (j < i) {
/* 360 */           str2 = str1.substring(j, i);
/* 361 */           localVector.addElement(str2);
/*     */         }
/* 363 */         j = i + 1;
/* 364 */         break;
/*     */       }
/*     */       
/*     */ 
/* 368 */       i++;
/*     */     }
/*     */     
/* 371 */     if (i == str1.length()) {
/* 372 */       str2 = str1.substring(j, i);
/* 373 */       localVector.addElement(str2);
/*     */     }
/*     */     
/* 376 */     String[] arrayOfString = new String[localVector.size()];
/* 377 */     localVector.copyInto(arrayOfString);
/* 378 */     return arrayOfString;
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
/*     */   public PrincipalName(String paramString1, int paramInt, String paramString2)
/*     */     throws RealmException
/*     */   {
/* 393 */     if (paramString1 == null) {
/* 394 */       throw new IllegalArgumentException("Null name not allowed");
/*     */     }
/* 396 */     String[] arrayOfString = parseName(paramString1);
/* 397 */     validateNameStrings(arrayOfString);
/* 398 */     if (paramString2 == null) {
/* 399 */       paramString2 = Realm.parseRealmAtSeparator(paramString1);
/*     */     }
/*     */     
/*     */ 
/* 403 */     this.realmDeduced = (paramString2 == null);
/*     */     
/* 405 */     switch (paramInt) {
/*     */     case 3:  Object localObject;
/* 407 */       if (arrayOfString.length >= 2) {
/* 408 */         localObject = arrayOfString[1];
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/* 415 */           String str = InetAddress.getByName((String)localObject).getCanonicalHostName();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 420 */           if (str.toLowerCase(Locale.ENGLISH).startsWith(((String)localObject)
/* 421 */             .toLowerCase(Locale.ENGLISH) + ".")) {
/* 422 */             localObject = str;
/*     */           }
/*     */         }
/*     */         catch (UnknownHostException|SecurityException localUnknownHostException) {}
/*     */         
/* 427 */         if (((String)localObject).endsWith(".")) {
/* 428 */           localObject = ((String)localObject).substring(0, ((String)localObject).length() - 1);
/*     */         }
/* 430 */         arrayOfString[1] = ((String)localObject).toLowerCase(Locale.ENGLISH);
/*     */       }
/* 432 */       this.nameStrings = arrayOfString;
/* 433 */       this.nameType = paramInt;
/*     */       
/* 435 */       if (paramString2 != null) {
/* 436 */         this.nameRealm = new Realm(paramString2);
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/* 444 */         localObject = mapHostToRealm(arrayOfString[1]);
/* 445 */         if (localObject != null) {
/* 446 */           this.nameRealm = new Realm((String)localObject);
/*     */         } else {
/* 448 */           this.nameRealm = Realm.getDefault();
/*     */         }
/*     */       }
/* 451 */       break;
/*     */     case 0: 
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 4: 
/*     */     case 5: 
/* 457 */       this.nameStrings = arrayOfString;
/* 458 */       this.nameType = paramInt;
/* 459 */       if (paramString2 != null) {
/* 460 */         this.nameRealm = new Realm(paramString2);
/*     */       } else {
/* 462 */         this.nameRealm = Realm.getDefault();
/*     */       }
/* 464 */       break;
/*     */     default: 
/* 466 */       throw new IllegalArgumentException("Illegal name type");
/*     */     }
/*     */   }
/*     */   
/*     */   public PrincipalName(String paramString, int paramInt) throws RealmException {
/* 471 */     this(paramString, paramInt, (String)null);
/*     */   }
/*     */   
/*     */   public PrincipalName(String paramString) throws RealmException {
/* 475 */     this(paramString, 0);
/*     */   }
/*     */   
/*     */   public PrincipalName(String paramString1, String paramString2) throws RealmException {
/* 479 */     this(paramString1, 0, paramString2);
/*     */   }
/*     */   
/*     */   public static PrincipalName tgsService(String paramString1, String paramString2) throws KrbException
/*     */   {
/* 484 */     return new PrincipalName(2, new String[] { "krbtgt", paramString1 }, new Realm(paramString2));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRealmAsString()
/*     */   {
/* 490 */     return getRealmString();
/*     */   }
/*     */   
/*     */   public String getPrincipalNameAsString() {
/* 494 */     StringBuffer localStringBuffer = new StringBuffer(this.nameStrings[0]);
/* 495 */     for (int i = 1; i < this.nameStrings.length; i++)
/* 496 */       localStringBuffer.append(this.nameStrings[i]);
/* 497 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 501 */     return toString().hashCode();
/*     */   }
/*     */   
/*     */   public String getName() {
/* 505 */     return toString();
/*     */   }
/*     */   
/*     */   public int getNameType() {
/* 509 */     return this.nameType;
/*     */   }
/*     */   
/*     */   public String[] getNameStrings() {
/* 513 */     return (String[])this.nameStrings.clone();
/*     */   }
/*     */   
/*     */   public byte[][] toByteArray() {
/* 517 */     byte[][] arrayOfByte = new byte[this.nameStrings.length][];
/* 518 */     for (int i = 0; i < this.nameStrings.length; i++) {
/* 519 */       arrayOfByte[i] = new byte[this.nameStrings[i].length()];
/* 520 */       arrayOfByte[i] = this.nameStrings[i].getBytes();
/*     */     }
/* 522 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */   public String getRealmString() {
/* 526 */     return this.nameRealm.toString();
/*     */   }
/*     */   
/*     */   public Realm getRealm() {
/* 530 */     return this.nameRealm;
/*     */   }
/*     */   
/*     */   public String getSalt() {
/* 534 */     if (this.salt == null) {
/* 535 */       StringBuffer localStringBuffer = new StringBuffer();
/* 536 */       localStringBuffer.append(this.nameRealm.toString());
/* 537 */       for (int i = 0; i < this.nameStrings.length; i++) {
/* 538 */         localStringBuffer.append(this.nameStrings[i]);
/*     */       }
/* 540 */       return localStringBuffer.toString();
/*     */     }
/* 542 */     return this.salt;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 546 */     StringBuffer localStringBuffer = new StringBuffer();
/* 547 */     for (int i = 0; i < this.nameStrings.length; i++) {
/* 548 */       if (i > 0)
/* 549 */         localStringBuffer.append("/");
/* 550 */       localStringBuffer.append(this.nameStrings[i]);
/*     */     }
/* 552 */     localStringBuffer.append("@");
/* 553 */     localStringBuffer.append(this.nameRealm.toString());
/* 554 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */   public String getNameString() {
/* 558 */     StringBuffer localStringBuffer = new StringBuffer();
/* 559 */     for (int i = 0; i < this.nameStrings.length; i++) {
/* 560 */       if (i > 0)
/* 561 */         localStringBuffer.append("/");
/* 562 */       localStringBuffer.append(this.nameStrings[i]);
/*     */     }
/* 564 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] asn1Encode()
/*     */     throws Asn1Exception, IOException
/*     */   {
/* 576 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 577 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 578 */     BigInteger localBigInteger = BigInteger.valueOf(this.nameType);
/* 579 */     localDerOutputStream2.putInteger(localBigInteger);
/* 580 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
/* 581 */     localDerOutputStream2 = new DerOutputStream();
/* 582 */     DerValue[] arrayOfDerValue = new DerValue[this.nameStrings.length];
/* 583 */     for (int i = 0; i < this.nameStrings.length; i++) {
/* 584 */       arrayOfDerValue[i] = new KerberosString(this.nameStrings[i]).toDerValue();
/*     */     }
/* 586 */     localDerOutputStream2.putSequence(arrayOfDerValue);
/* 587 */     localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
/* 588 */     localDerOutputStream2 = new DerOutputStream();
/* 589 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/* 590 */     return localDerOutputStream2.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean match(PrincipalName paramPrincipalName)
/*     */   {
/* 602 */     boolean bool = true;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 607 */     if ((this.nameRealm != null) && (paramPrincipalName.nameRealm != null) && 
/* 608 */       (!this.nameRealm.toString().equalsIgnoreCase(paramPrincipalName.nameRealm.toString()))) {
/* 609 */       bool = false;
/*     */     }
/*     */     
/* 612 */     if (this.nameStrings.length != paramPrincipalName.nameStrings.length) {
/* 613 */       bool = false;
/*     */     } else {
/* 615 */       for (int i = 0; i < this.nameStrings.length; i++) {
/* 616 */         if (!this.nameStrings[i].equalsIgnoreCase(paramPrincipalName.nameStrings[i])) {
/* 617 */           bool = false;
/*     */         }
/*     */       }
/*     */     }
/* 621 */     return bool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writePrincipal(CCacheOutputStream paramCCacheOutputStream)
/*     */     throws IOException
/*     */   {
/* 632 */     paramCCacheOutputStream.write32(this.nameType);
/* 633 */     paramCCacheOutputStream.write32(this.nameStrings.length);
/* 634 */     byte[] arrayOfByte1 = null;
/* 635 */     arrayOfByte1 = this.nameRealm.toString().getBytes();
/* 636 */     paramCCacheOutputStream.write32(arrayOfByte1.length);
/* 637 */     paramCCacheOutputStream.write(arrayOfByte1, 0, arrayOfByte1.length);
/* 638 */     byte[] arrayOfByte2 = null;
/* 639 */     for (int i = 0; i < this.nameStrings.length; i++) {
/* 640 */       arrayOfByte2 = this.nameStrings[i].getBytes();
/* 641 */       paramCCacheOutputStream.write32(arrayOfByte2.length);
/* 642 */       paramCCacheOutputStream.write(arrayOfByte2, 0, arrayOfByte2.length);
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
/*     */   public String getInstanceComponent()
/*     */   {
/* 656 */     if ((this.nameStrings != null) && (this.nameStrings.length >= 2))
/*     */     {
/* 658 */       return new String(this.nameStrings[1]);
/*     */     }
/*     */     
/* 661 */     return null;
/*     */   }
/*     */   
/*     */   static String mapHostToRealm(String paramString) {
/* 665 */     String str1 = null;
/*     */     try {
/* 667 */       String str2 = null;
/* 668 */       Config localConfig = Config.getInstance();
/* 669 */       if ((str1 = localConfig.get(new String[] { "domain_realm", paramString })) != null) {
/* 670 */         return str1;
/*     */       }
/* 672 */       for (int i = 1; i < paramString.length(); i++) {
/* 673 */         if ((paramString.charAt(i) == '.') && (i != paramString.length() - 1)) {
/* 674 */           str2 = paramString.substring(i);
/* 675 */           str1 = localConfig.get(new String[] { "domain_realm", str2 });
/* 676 */           if (str1 != null) {
/*     */             break;
/*     */           }
/*     */           
/* 680 */           str2 = paramString.substring(i + 1);
/* 681 */           str1 = localConfig.get(new String[] { "domain_realm", str2 });
/* 682 */           if (str1 != null) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (KrbException localKrbException) {}
/*     */     
/*     */ 
/* 691 */     return str1;
/*     */   }
/*     */   
/*     */   public boolean isRealmDeduced() {
/* 695 */     return this.realmDeduced;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\krb5\PrincipalName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
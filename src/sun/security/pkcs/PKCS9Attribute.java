/*     */ package sun.security.pkcs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.Date;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.DerEncoder;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
/*     */ import sun.security.x509.CertificateExtensions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PKCS9Attribute
/*     */   implements DerEncoder
/*     */ {
/* 183 */   private static final Debug debug = Debug.getInstance("jar");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 188 */   static final ObjectIdentifier[] PKCS9_OIDS = new ObjectIdentifier[18];
/*     */   private static final Class<?> BYTE_ARRAY_CLASS;
/*     */   public static final ObjectIdentifier EMAIL_ADDRESS_OID;
/*     */   public static final ObjectIdentifier UNSTRUCTURED_NAME_OID;
/*     */   public static final ObjectIdentifier CONTENT_TYPE_OID; public static final ObjectIdentifier MESSAGE_DIGEST_OID; public static final ObjectIdentifier SIGNING_TIME_OID; public static final ObjectIdentifier COUNTERSIGNATURE_OID; public static final ObjectIdentifier CHALLENGE_PASSWORD_OID; public static final ObjectIdentifier UNSTRUCTURED_ADDRESS_OID; public static final ObjectIdentifier EXTENDED_CERTIFICATE_ATTRIBUTES_OID; public static final ObjectIdentifier ISSUER_SERIALNUMBER_OID; public static final ObjectIdentifier EXTENSION_REQUEST_OID; public static final ObjectIdentifier SMIME_CAPABILITY_OID; public static final ObjectIdentifier SIGNING_CERTIFICATE_OID; public static final ObjectIdentifier SIGNATURE_TIMESTAMP_TOKEN_OID; public static final String EMAIL_ADDRESS_STR = "EmailAddress"; public static final String UNSTRUCTURED_NAME_STR = "UnstructuredName"; public static final String CONTENT_TYPE_STR = "ContentType"; public static final String MESSAGE_DIGEST_STR = "MessageDigest"; public static final String SIGNING_TIME_STR = "SigningTime"; public static final String COUNTERSIGNATURE_STR = "Countersignature"; public static final String CHALLENGE_PASSWORD_STR = "ChallengePassword";
/* 193 */   static { for (int i = 1; i < PKCS9_OIDS.length - 2; i++)
/*     */     {
/* 195 */       PKCS9_OIDS[i] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, i });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 200 */     PKCS9_OIDS[(PKCS9_OIDS.length - 2)] = ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 12 });
/* 201 */     PKCS9_OIDS[(PKCS9_OIDS.length - 1)] = 
/* 202 */       ObjectIdentifier.newInternal(new int[] { 1, 2, 840, 113549, 1, 9, 16, 2, 14 });
/*     */     try
/*     */     {
/* 205 */       BYTE_ARRAY_CLASS = Class.forName("[B");
/*     */     } catch (ClassNotFoundException localClassNotFoundException1) {
/* 207 */       throw new ExceptionInInitializerError(localClassNotFoundException1.toString());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 212 */     EMAIL_ADDRESS_OID = PKCS9_OIDS[1];
/* 213 */     UNSTRUCTURED_NAME_OID = PKCS9_OIDS[2];
/* 214 */     CONTENT_TYPE_OID = PKCS9_OIDS[3];
/* 215 */     MESSAGE_DIGEST_OID = PKCS9_OIDS[4];
/* 216 */     SIGNING_TIME_OID = PKCS9_OIDS[5];
/* 217 */     COUNTERSIGNATURE_OID = PKCS9_OIDS[6];
/* 218 */     CHALLENGE_PASSWORD_OID = PKCS9_OIDS[7];
/* 219 */     UNSTRUCTURED_ADDRESS_OID = PKCS9_OIDS[8];
/* 220 */     EXTENDED_CERTIFICATE_ATTRIBUTES_OID = PKCS9_OIDS[9];
/*     */     
/* 222 */     ISSUER_SERIALNUMBER_OID = PKCS9_OIDS[10];
/*     */     
/*     */ 
/* 225 */     EXTENSION_REQUEST_OID = PKCS9_OIDS[14];
/* 226 */     SMIME_CAPABILITY_OID = PKCS9_OIDS[15];
/* 227 */     SIGNING_CERTIFICATE_OID = PKCS9_OIDS[16];
/* 228 */     SIGNATURE_TIMESTAMP_TOKEN_OID = PKCS9_OIDS[17];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 256 */     NAME_OID_TABLE = new Hashtable(18);
/*     */     
/*     */ 
/*     */ 
/* 260 */     NAME_OID_TABLE.put("emailaddress", PKCS9_OIDS[1]);
/* 261 */     NAME_OID_TABLE.put("unstructuredname", PKCS9_OIDS[2]);
/* 262 */     NAME_OID_TABLE.put("contenttype", PKCS9_OIDS[3]);
/* 263 */     NAME_OID_TABLE.put("messagedigest", PKCS9_OIDS[4]);
/* 264 */     NAME_OID_TABLE.put("signingtime", PKCS9_OIDS[5]);
/* 265 */     NAME_OID_TABLE.put("countersignature", PKCS9_OIDS[6]);
/* 266 */     NAME_OID_TABLE.put("challengepassword", PKCS9_OIDS[7]);
/* 267 */     NAME_OID_TABLE.put("unstructuredaddress", PKCS9_OIDS[8]);
/* 268 */     NAME_OID_TABLE.put("extendedcertificateattributes", PKCS9_OIDS[9]);
/* 269 */     NAME_OID_TABLE.put("issuerandserialnumber", PKCS9_OIDS[10]);
/* 270 */     NAME_OID_TABLE.put("rsaproprietary", PKCS9_OIDS[11]);
/* 271 */     NAME_OID_TABLE.put("rsaproprietary", PKCS9_OIDS[12]);
/* 272 */     NAME_OID_TABLE.put("signingdescription", PKCS9_OIDS[13]);
/* 273 */     NAME_OID_TABLE.put("extensionrequest", PKCS9_OIDS[14]);
/* 274 */     NAME_OID_TABLE.put("smimecapability", PKCS9_OIDS[15]);
/* 275 */     NAME_OID_TABLE.put("signingcertificate", PKCS9_OIDS[16]);
/* 276 */     NAME_OID_TABLE.put("signaturetimestamptoken", PKCS9_OIDS[17]);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 283 */     OID_NAME_TABLE = new Hashtable(16);
/*     */     
/*     */ 
/* 286 */     OID_NAME_TABLE.put(PKCS9_OIDS[1], "EmailAddress");
/* 287 */     OID_NAME_TABLE.put(PKCS9_OIDS[2], "UnstructuredName");
/* 288 */     OID_NAME_TABLE.put(PKCS9_OIDS[3], "ContentType");
/* 289 */     OID_NAME_TABLE.put(PKCS9_OIDS[4], "MessageDigest");
/* 290 */     OID_NAME_TABLE.put(PKCS9_OIDS[5], "SigningTime");
/* 291 */     OID_NAME_TABLE.put(PKCS9_OIDS[6], "Countersignature");
/* 292 */     OID_NAME_TABLE.put(PKCS9_OIDS[7], "ChallengePassword");
/* 293 */     OID_NAME_TABLE.put(PKCS9_OIDS[8], "UnstructuredAddress");
/* 294 */     OID_NAME_TABLE.put(PKCS9_OIDS[9], "ExtendedCertificateAttributes");
/* 295 */     OID_NAME_TABLE.put(PKCS9_OIDS[10], "IssuerAndSerialNumber");
/* 296 */     OID_NAME_TABLE.put(PKCS9_OIDS[11], "RSAProprietary");
/* 297 */     OID_NAME_TABLE.put(PKCS9_OIDS[12], "RSAProprietary");
/* 298 */     OID_NAME_TABLE.put(PKCS9_OIDS[13], "SMIMESigningDesc");
/* 299 */     OID_NAME_TABLE.put(PKCS9_OIDS[14], "ExtensionRequest");
/* 300 */     OID_NAME_TABLE.put(PKCS9_OIDS[15], "SMIMECapability");
/* 301 */     OID_NAME_TABLE.put(PKCS9_OIDS[16], "SigningCertificate");
/* 302 */     OID_NAME_TABLE.put(PKCS9_OIDS[17], "SignatureTimestampToken");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 310 */     PKCS9_VALUE_TAGS = new Byte[][] { null, { new Byte(22) }, { new Byte(22), new Byte(19) }, { new Byte(6) }, { new Byte(4) }, { new Byte(23) }, { new Byte(48) }, { new Byte(19), new Byte(20) }, { new Byte(19), new Byte(20) }, { new Byte(49) }, { new Byte(48) }, null, null, null, { new Byte(48) }, { new Byte(48) }, { new Byte(48) }, { new Byte(48) } };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 334 */     VALUE_CLASSES = new Class[18];
/*     */     
/*     */     try
/*     */     {
/* 338 */       Class localClass = Class.forName("[Ljava.lang.String;");
/*     */       
/* 340 */       VALUE_CLASSES[0] = null;
/* 341 */       VALUE_CLASSES[1] = localClass;
/* 342 */       VALUE_CLASSES[2] = localClass;
/* 343 */       VALUE_CLASSES[3] = 
/* 344 */         Class.forName("sun.security.util.ObjectIdentifier");
/* 345 */       VALUE_CLASSES[4] = BYTE_ARRAY_CLASS;
/* 346 */       VALUE_CLASSES[5] = Class.forName("java.util.Date");
/* 347 */       VALUE_CLASSES[6] = 
/* 348 */         Class.forName("[Lsun.security.pkcs.SignerInfo;");
/* 349 */       VALUE_CLASSES[7] = 
/* 350 */         Class.forName("java.lang.String");
/* 351 */       VALUE_CLASSES[8] = localClass;
/* 352 */       VALUE_CLASSES[9] = null;
/* 353 */       VALUE_CLASSES[10] = null;
/* 354 */       VALUE_CLASSES[11] = null;
/* 355 */       VALUE_CLASSES[12] = null;
/* 356 */       VALUE_CLASSES[13] = null;
/* 357 */       VALUE_CLASSES[14] = 
/* 358 */         Class.forName("sun.security.x509.CertificateExtensions");
/* 359 */       VALUE_CLASSES[15] = null;
/* 360 */       VALUE_CLASSES[16] = null;
/* 361 */       VALUE_CLASSES[17] = BYTE_ARRAY_CLASS;
/*     */     } catch (ClassNotFoundException localClassNotFoundException2) {
/* 363 */       throw new ExceptionInInitializerError(localClassNotFoundException2.toString()); } }
/*     */   
/*     */   public static final String UNSTRUCTURED_ADDRESS_STR = "UnstructuredAddress";
/*     */   public static final String EXTENDED_CERTIFICATE_ATTRIBUTES_STR = "ExtendedCertificateAttributes";
/*     */   public static final String ISSUER_SERIALNUMBER_STR = "IssuerAndSerialNumber";
/*     */   private static final String RSA_PROPRIETARY_STR = "RSAProprietary";
/*     */   private static final String SMIME_SIGNING_DESC_STR = "SMIMESigningDesc";
/*     */   public static final String EXTENSION_REQUEST_STR = "ExtensionRequest";
/* 371 */   public static final String SMIME_CAPABILITY_STR = "SMIMECapability"; public static final String SIGNING_CERTIFICATE_STR = "SigningCertificate"; public static final String SIGNATURE_TIMESTAMP_TOKEN_STR = "SignatureTimestampToken"; private static final Hashtable<String, ObjectIdentifier> NAME_OID_TABLE; private static final Hashtable<ObjectIdentifier, String> OID_NAME_TABLE; private static final Byte[][] PKCS9_VALUE_TAGS; private static final Class<?>[] VALUE_CLASSES; private static final boolean[] SINGLE_VALUED = { false, false, false, true, true, true, false, true, false, false, true, false, false, false, true, true, true, true };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ObjectIdentifier oid;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int index;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object value;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS9Attribute(ObjectIdentifier paramObjectIdentifier, Object paramObject)
/*     */     throws IllegalArgumentException
/*     */   {
/* 426 */     init(paramObjectIdentifier, paramObject);
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
/*     */   public PKCS9Attribute(String paramString, Object paramObject)
/*     */     throws IllegalArgumentException
/*     */   {
/* 447 */     ObjectIdentifier localObjectIdentifier = getOID(paramString);
/*     */     
/* 449 */     if (localObjectIdentifier == null) {
/* 450 */       throw new IllegalArgumentException("Unrecognized attribute name " + paramString + " constructing PKCS9Attribute.");
/*     */     }
/*     */     
/*     */ 
/* 454 */     init(localObjectIdentifier, paramObject);
/*     */   }
/*     */   
/*     */   private void init(ObjectIdentifier paramObjectIdentifier, Object paramObject)
/*     */     throws IllegalArgumentException
/*     */   {
/* 460 */     this.oid = paramObjectIdentifier;
/* 461 */     this.index = indexOf(paramObjectIdentifier, PKCS9_OIDS, 1);
/* 462 */     Class localClass = this.index == -1 ? BYTE_ARRAY_CLASS : VALUE_CLASSES[this.index];
/* 463 */     if (!localClass.isInstance(paramObject))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 469 */       throw new IllegalArgumentException("Wrong value class  for attribute " + paramObjectIdentifier + " constructing PKCS9Attribute; was " + paramObject.getClass().toString() + ", should be " + localClass.toString());
/*     */     }
/* 471 */     this.value = paramObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PKCS9Attribute(DerValue paramDerValue)
/*     */     throws IOException
/*     */   {
/* 484 */     DerInputStream localDerInputStream = new DerInputStream(paramDerValue.toByteArray());
/* 485 */     DerValue[] arrayOfDerValue1 = localDerInputStream.getSequence(2);
/*     */     
/* 487 */     if (localDerInputStream.available() != 0) {
/* 488 */       throw new IOException("Excess data parsing PKCS9Attribute");
/*     */     }
/* 490 */     if (arrayOfDerValue1.length != 2) {
/* 491 */       throw new IOException("PKCS9Attribute doesn't have two components");
/*     */     }
/*     */     
/* 494 */     this.oid = arrayOfDerValue1[0].getOID();
/* 495 */     byte[] arrayOfByte = arrayOfDerValue1[1].toByteArray();
/* 496 */     DerValue[] arrayOfDerValue2 = new DerInputStream(arrayOfByte).getSet(1);
/*     */     
/* 498 */     this.index = indexOf(this.oid, PKCS9_OIDS, 1);
/* 499 */     if (this.index == -1) {
/* 500 */       if (debug != null) {
/* 501 */         debug.println("Unsupported signer attribute: " + this.oid);
/*     */       }
/* 503 */       this.value = arrayOfByte;
/* 504 */       return;
/*     */     }
/*     */     
/*     */ 
/* 508 */     if ((SINGLE_VALUED[this.index] != 0) && (arrayOfDerValue2.length > 1)) {
/* 509 */       throwSingleValuedException();
/*     */     }
/*     */     
/*     */ 
/* 513 */     for (int i = 0; i < arrayOfDerValue2.length; i++) {
/* 514 */       Byte localByte = new Byte(arrayOfDerValue2[i].tag);
/*     */       
/* 516 */       if (indexOf(localByte, PKCS9_VALUE_TAGS[this.index], 0) == -1)
/* 517 */         throwTagException(localByte); }
/*     */     Object localObject;
/*     */     int j;
/* 520 */     switch (this.index)
/*     */     {
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 8: 
/* 525 */       localObject = new String[arrayOfDerValue2.length];
/*     */       
/* 527 */       for (j = 0; j < arrayOfDerValue2.length; j++)
/* 528 */         localObject[j] = arrayOfDerValue2[j].getAsString();
/* 529 */       this.value = localObject;
/*     */       
/* 531 */       break;
/*     */     
/*     */     case 3: 
/* 534 */       this.value = arrayOfDerValue2[0].getOID();
/* 535 */       break;
/*     */     
/*     */     case 4: 
/* 538 */       this.value = arrayOfDerValue2[0].getOctetString();
/* 539 */       break;
/*     */     
/*     */     case 5: 
/* 542 */       this.value = new DerInputStream(arrayOfDerValue2[0].toByteArray()).getUTCTime();
/* 543 */       break;
/*     */     
/*     */ 
/*     */     case 6: 
/* 547 */       localObject = new SignerInfo[arrayOfDerValue2.length];
/* 548 */       for (j = 0; j < arrayOfDerValue2.length; j++)
/*     */       {
/* 550 */         localObject[j] = new SignerInfo(arrayOfDerValue2[j].toDerInputStream()); }
/* 551 */       this.value = localObject;
/*     */       
/* 553 */       break;
/*     */     
/*     */     case 7: 
/* 556 */       this.value = arrayOfDerValue2[0].getAsString();
/* 557 */       break;
/*     */     
/*     */     case 9: 
/* 560 */       throw new IOException("PKCS9 extended-certificate attribute not supported.");
/*     */     
/*     */ 
/*     */     case 10: 
/* 564 */       throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
/*     */     
/*     */ 
/*     */     case 11: 
/*     */     case 12: 
/* 569 */       throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
/*     */     
/*     */ 
/*     */     case 13: 
/* 573 */       throw new IOException("PKCS9 attribute #13 not supported.");
/*     */     
/*     */ 
/*     */ 
/*     */     case 14: 
/* 578 */       this.value = new CertificateExtensions(new DerInputStream(arrayOfDerValue2[0].toByteArray()));
/* 579 */       break;
/*     */     
/*     */     case 15: 
/* 582 */       throw new IOException("PKCS9 SMIMECapability attribute not supported.");
/*     */     
/*     */ 
/*     */     case 16: 
/* 586 */       this.value = new SigningCertificateInfo(arrayOfDerValue2[0].toByteArray());
/* 587 */       break;
/*     */     
/*     */     case 17: 
/* 590 */       this.value = arrayOfDerValue2[0].toByteArray();
/* 591 */       break;
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void derEncode(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 605 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/* 606 */     localDerOutputStream.putOID(this.oid);
/* 607 */     Object localObject2; int i; switch (this.index) {
/*     */     case -1: 
/* 609 */       localDerOutputStream.write((byte[])this.value);
/* 610 */       break;
/*     */     
/*     */     case 1: 
/*     */     case 2: 
/* 614 */       localObject1 = (String[])this.value;
/* 615 */       localObject2 = new DerOutputStream[localObject1.length];
/*     */       
/*     */ 
/* 618 */       for (i = 0; i < localObject1.length; i++) {
/* 619 */         localObject2[i] = new DerOutputStream();
/* 620 */         localObject2[i].putIA5String(localObject1[i]);
/*     */       }
/* 622 */       localDerOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])localObject2);
/*     */       
/* 624 */       break;
/*     */     
/*     */ 
/*     */     case 3: 
/* 628 */       localObject1 = new DerOutputStream();
/* 629 */       ((DerOutputStream)localObject1).putOID((ObjectIdentifier)this.value);
/* 630 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */       
/* 632 */       break;
/*     */     
/*     */ 
/*     */     case 4: 
/* 636 */       localObject1 = new DerOutputStream();
/* 637 */       ((DerOutputStream)localObject1).putOctetString((byte[])this.value);
/* 638 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */       
/* 640 */       break;
/*     */     
/*     */ 
/*     */     case 5: 
/* 644 */       localObject1 = new DerOutputStream();
/* 645 */       ((DerOutputStream)localObject1).putUTCTime((Date)this.value);
/* 646 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */       
/* 648 */       break;
/*     */     
/*     */     case 6: 
/* 651 */       localDerOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])this.value);
/* 652 */       break;
/*     */     
/*     */ 
/*     */     case 7: 
/* 656 */       localObject1 = new DerOutputStream();
/* 657 */       ((DerOutputStream)localObject1).putPrintableString((String)this.value);
/* 658 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */       
/* 660 */       break;
/*     */     
/*     */ 
/*     */     case 8: 
/* 664 */       localObject1 = (String[])this.value;
/* 665 */       localObject2 = new DerOutputStream[localObject1.length];
/*     */       
/*     */ 
/* 668 */       for (i = 0; i < localObject1.length; i++) {
/* 669 */         localObject2[i] = new DerOutputStream();
/* 670 */         localObject2[i].putPrintableString(localObject1[i]);
/*     */       }
/* 672 */       localDerOutputStream.putOrderedSetOf((byte)49, (DerEncoder[])localObject2);
/*     */       
/* 674 */       break;
/*     */     
/*     */     case 9: 
/* 677 */       throw new IOException("PKCS9 extended-certificate attribute not supported.");
/*     */     
/*     */ 
/*     */     case 10: 
/* 681 */       throw new IOException("PKCS9 IssuerAndSerialNumberattribute not supported.");
/*     */     
/*     */ 
/*     */     case 11: 
/*     */     case 12: 
/* 686 */       throw new IOException("PKCS9 RSA DSI attributes11 and 12, not supported.");
/*     */     
/*     */ 
/*     */     case 13: 
/* 690 */       throw new IOException("PKCS9 attribute #13 not supported.");
/*     */     
/*     */ 
/*     */ 
/*     */     case 14: 
/* 695 */       localObject1 = new DerOutputStream();
/* 696 */       localObject2 = (CertificateExtensions)this.value;
/*     */       try {
/* 698 */         ((CertificateExtensions)localObject2).encode((OutputStream)localObject1, true);
/*     */       } catch (CertificateException localCertificateException) {
/* 700 */         throw new IOException(localCertificateException.toString());
/*     */       }
/* 702 */       localDerOutputStream.write((byte)49, ((DerOutputStream)localObject1).toByteArray());
/*     */       
/* 704 */       break;
/*     */     case 15: 
/* 706 */       throw new IOException("PKCS9 attribute #15 not supported.");
/*     */     
/*     */ 
/*     */     case 16: 
/* 710 */       throw new IOException("PKCS9 SigningCertificate attribute not supported.");
/*     */     
/*     */ 
/*     */ 
/*     */     case 17: 
/* 715 */       localDerOutputStream.write((byte)49, (byte[])this.value);
/* 716 */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 721 */     Object localObject1 = new DerOutputStream();
/* 722 */     ((DerOutputStream)localObject1).write((byte)48, localDerOutputStream.toByteArray());
/*     */     
/* 724 */     paramOutputStream.write(((DerOutputStream)localObject1).toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isKnown()
/*     */   {
/* 733 */     return this.index != -1;
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
/*     */   public Object getValue()
/*     */   {
/* 747 */     return this.value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isSingleValued()
/*     */   {
/* 754 */     return (this.index == -1) || (SINGLE_VALUED[this.index] != 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ObjectIdentifier getOID()
/*     */   {
/* 761 */     return this.oid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 768 */     return this.index == -1 ? this.oid
/* 769 */       .toString() : 
/* 770 */       (String)OID_NAME_TABLE.get(PKCS9_OIDS[this.index]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ObjectIdentifier getOID(String paramString)
/*     */   {
/* 778 */     return (ObjectIdentifier)NAME_OID_TABLE.get(paramString.toLowerCase(Locale.ENGLISH));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getName(ObjectIdentifier paramObjectIdentifier)
/*     */   {
/* 786 */     return (String)OID_NAME_TABLE.get(paramObjectIdentifier);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 793 */     StringBuffer localStringBuffer = new StringBuffer(100);
/*     */     
/* 795 */     localStringBuffer.append("[");
/*     */     
/* 797 */     if (this.index == -1) {
/* 798 */       localStringBuffer.append(this.oid.toString());
/*     */     } else {
/* 800 */       localStringBuffer.append((String)OID_NAME_TABLE.get(PKCS9_OIDS[this.index]));
/*     */     }
/* 802 */     localStringBuffer.append(": ");
/*     */     
/* 804 */     if ((this.index == -1) || (SINGLE_VALUED[this.index] != 0)) {
/* 805 */       if ((this.value instanceof byte[])) {
/* 806 */         HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
/* 807 */         localStringBuffer.append(localHexDumpEncoder.encodeBuffer((byte[])this.value));
/*     */       } else {
/* 809 */         localStringBuffer.append(this.value.toString());
/*     */       }
/* 811 */       localStringBuffer.append("]");
/* 812 */       return localStringBuffer.toString();
/*     */     }
/* 814 */     int i = 1;
/* 815 */     Object[] arrayOfObject = (Object[])this.value;
/*     */     
/* 817 */     for (int j = 0; j < arrayOfObject.length; j++) {
/* 818 */       if (i != 0) {
/* 819 */         i = 0;
/*     */       } else {
/* 821 */         localStringBuffer.append(", ");
/*     */       }
/* 823 */       localStringBuffer.append(arrayOfObject[j].toString());
/*     */     }
/* 825 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int indexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt)
/*     */   {
/* 836 */     for (int i = paramInt; i < paramArrayOfObject.length; i++) {
/* 837 */       if (paramObject.equals(paramArrayOfObject[i])) return i;
/*     */     }
/* 839 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void throwSingleValuedException()
/*     */     throws IOException
/*     */   {
/* 848 */     throw new IOException("Single-value attribute " + this.oid + " (" + getName() + ") has multiple values.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void throwTagException(Byte paramByte)
/*     */     throws IOException
/*     */   {
/* 859 */     Byte[] arrayOfByte = PKCS9_VALUE_TAGS[this.index];
/* 860 */     StringBuffer localStringBuffer = new StringBuffer(100);
/* 861 */     localStringBuffer.append("Value of attribute ");
/* 862 */     localStringBuffer.append(this.oid.toString());
/* 863 */     localStringBuffer.append(" (");
/* 864 */     localStringBuffer.append(getName());
/* 865 */     localStringBuffer.append(") has wrong tag: ");
/* 866 */     localStringBuffer.append(paramByte.toString());
/* 867 */     localStringBuffer.append(".  Expected tags: ");
/*     */     
/* 869 */     localStringBuffer.append(arrayOfByte[0].toString());
/*     */     
/* 871 */     for (int i = 1; i < arrayOfByte.length; i++) {
/* 872 */       localStringBuffer.append(", ");
/* 873 */       localStringBuffer.append(arrayOfByte[i].toString());
/*     */     }
/* 875 */     localStringBuffer.append(".");
/* 876 */     throw new IOException(localStringBuffer.toString());
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\pkcs\PKCS9Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
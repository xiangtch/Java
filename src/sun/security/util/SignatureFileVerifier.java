/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.CodeSigner;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SignatureException;
/*     */ import java.security.Timestamp;
/*     */ import java.security.cert.CertPath;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Base64;
/*     */ import java.util.Base64.Decoder;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Attributes.Name;
/*     */ import java.util.jar.JarException;
/*     */ import java.util.jar.Manifest;
/*     */ import sun.security.jca.Providers;
/*     */ import sun.security.pkcs.ContentInfo;
/*     */ import sun.security.pkcs.PKCS7;
/*     */ import sun.security.pkcs.SignerInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SignatureFileVerifier
/*     */ {
/*  60 */   private static final Debug debug = Debug.getInstance("jar");
/*     */   
/*  62 */   private static final DisabledAlgorithmConstraints JAR_DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
/*     */   
/*     */ 
/*     */   private ArrayList<CodeSigner[]> signerCache;
/*     */   
/*     */ 
/*  68 */   private static final String ATTR_DIGEST = "-DIGEST-Manifest-Main-Attributes"
/*     */   
/*  70 */     .toUpperCase(Locale.ENGLISH);
/*     */   
/*     */ 
/*     */ 
/*     */   private PKCS7 block;
/*     */   
/*     */ 
/*     */ 
/*     */   private byte[] sfBytes;
/*     */   
/*     */ 
/*     */   private String name;
/*     */   
/*     */ 
/*     */   private ManifestDigester md;
/*     */   
/*     */ 
/*     */   private HashMap<String, MessageDigest> createdDigests;
/*     */   
/*     */ 
/*  90 */   private boolean workaround = false;
/*     */   
/*     */ 
/*  93 */   private CertificateFactory certificateFactory = null;
/*     */   
/*     */ 
/*  96 */   private Map<String, Boolean> permittedAlgs = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 101 */   private Timestamp timestamp = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignatureFileVerifier(ArrayList<CodeSigner[]> paramArrayList, ManifestDigester paramManifestDigester, String paramString, byte[] paramArrayOfByte)
/*     */     throws IOException, CertificateException
/*     */   {
/* 118 */     Object localObject1 = null;
/*     */     try {
/* 120 */       localObject1 = Providers.startJarVerification();
/* 121 */       this.block = new PKCS7(paramArrayOfByte);
/* 122 */       this.sfBytes = this.block.getContentInfo().getData();
/* 123 */       this.certificateFactory = CertificateFactory.getInstance("X509");
/*     */     } finally {
/* 125 */       Providers.stopJarVerification(localObject1);
/*     */     }
/*     */     
/* 128 */     this.name = paramString.substring(0, paramString.lastIndexOf('.')).toUpperCase(Locale.ENGLISH);
/* 129 */     this.md = paramManifestDigester;
/* 130 */     this.signerCache = paramArrayList;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean needSignatureFileBytes()
/*     */   {
/* 139 */     return this.sfBytes == null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean needSignatureFile(String paramString)
/*     */   {
/* 151 */     return this.name.equalsIgnoreCase(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSignatureFile(byte[] paramArrayOfByte)
/*     */   {
/* 160 */     this.sfBytes = paramArrayOfByte;
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
/*     */   public static boolean isBlockOrSF(String paramString)
/*     */   {
/* 174 */     return (paramString.endsWith(".SF")) || 
/* 175 */       (paramString.endsWith(".DSA")) || 
/* 176 */       (paramString.endsWith(".RSA")) || 
/* 177 */       (paramString.endsWith(".EC"));
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
/*     */   public static boolean isSigningRelated(String paramString)
/*     */   {
/* 191 */     paramString = paramString.toUpperCase(Locale.ENGLISH);
/* 192 */     if (!paramString.startsWith("META-INF/")) {
/* 193 */       return false;
/*     */     }
/* 195 */     paramString = paramString.substring(9);
/* 196 */     if (paramString.indexOf('/') != -1) {
/* 197 */       return false;
/*     */     }
/* 199 */     if ((isBlockOrSF(paramString)) || (paramString.equals("MANIFEST.MF")))
/* 200 */       return true;
/* 201 */     if (paramString.startsWith("SIG-"))
/*     */     {
/*     */ 
/*     */ 
/* 205 */       int i = paramString.lastIndexOf('.');
/* 206 */       if (i != -1) {
/* 207 */         String str = paramString.substring(i + 1);
/*     */         
/* 209 */         if ((str.length() > 3) || (str.length() < 1)) {
/* 210 */           return false;
/*     */         }
/*     */         
/* 213 */         for (int j = 0; j < str.length(); j++) {
/* 214 */           int k = str.charAt(j);
/*     */           
/* 216 */           if (((k < 65) || (k > 90)) && ((k < 48) || (k > 57))) {
/* 217 */             return false;
/*     */           }
/*     */         }
/*     */       }
/* 221 */       return true;
/*     */     }
/* 223 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private MessageDigest getDigest(String paramString)
/*     */     throws SignatureException
/*     */   {
/* 230 */     if (this.createdDigests == null) {
/* 231 */       this.createdDigests = new HashMap();
/*     */     }
/* 233 */     MessageDigest localMessageDigest = (MessageDigest)this.createdDigests.get(paramString);
/*     */     
/* 235 */     if (localMessageDigest == null) {
/*     */       try {
/* 237 */         localMessageDigest = MessageDigest.getInstance(paramString);
/* 238 */         this.createdDigests.put(paramString, localMessageDigest);
/*     */       }
/*     */       catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
/*     */     }
/*     */     
/* 243 */     return localMessageDigest;
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
/*     */   public void process(Hashtable<String, CodeSigner[]> paramHashtable, List<Object> paramList)
/*     */     throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException
/*     */   {
/* 260 */     Object localObject1 = null;
/*     */     try {
/* 262 */       localObject1 = Providers.startJarVerification();
/* 263 */       processImpl(paramHashtable, paramList);
/*     */     } finally {
/* 265 */       Providers.stopJarVerification(localObject1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void processImpl(Hashtable<String, CodeSigner[]> paramHashtable, List<Object> paramList)
/*     */     throws IOException, SignatureException, NoSuchAlgorithmException, JarException, CertificateException
/*     */   {
/* 275 */     Manifest localManifest = new Manifest();
/* 276 */     localManifest.read(new ByteArrayInputStream(this.sfBytes));
/*     */     
/*     */ 
/* 279 */     String str1 = localManifest.getMainAttributes().getValue(Name.SIGNATURE_VERSION);
/*     */     
/* 281 */     if ((str1 == null) || (!str1.equalsIgnoreCase("1.0")))
/*     */     {
/*     */ 
/* 284 */       return;
/*     */     }
/*     */     
/* 287 */     SignerInfo[] arrayOfSignerInfo = this.block.verify(this.sfBytes);
/*     */     
/* 289 */     if (arrayOfSignerInfo == null) {
/* 290 */       throw new SecurityException("cannot verify signature block file " + this.name);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 295 */     CodeSigner[] arrayOfCodeSigner = getSigners(arrayOfSignerInfo, this.block);
/*     */     
/*     */ 
/* 298 */     if (arrayOfCodeSigner == null) {
/*     */       return;
/*     */     }
/*     */     
/*     */ 
/*     */     String str2;
/*     */     
/* 305 */     for (str2 : arrayOfCodeSigner) {
/* 306 */       if (debug != null) {
/* 307 */         debug.println("Gathering timestamp for:  " + str2.toString());
/*     */       }
/* 309 */       if (str2.getTimestamp() == null) {
/* 310 */         this.timestamp = null;
/* 311 */         break; }
/* 312 */       if (this.timestamp == null) {
/* 313 */         this.timestamp = str2.getTimestamp();
/*     */       }
/* 315 */       else if (this.timestamp.getTimestamp().before(str2
/* 316 */         .getTimestamp().getTimestamp())) {
/* 317 */         this.timestamp = str2.getTimestamp();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 323 */     ??? = localManifest.getEntries().entrySet().iterator();
/*     */     
/*     */ 
/* 326 */     boolean bool = verifyManifestHash(localManifest, this.md, paramList);
/*     */     
/*     */ 
/* 329 */     if ((!bool) && (!verifyManifestMainAttrs(localManifest, this.md))) {
/* 330 */       throw new SecurityException("Invalid signature file digest for Manifest main attributes");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 335 */     while (((Iterator)???).hasNext())
/*     */     {
/* 337 */       Entry localEntry = (Entry)((Iterator)???).next();
/* 338 */       str2 = (String)localEntry.getKey();
/*     */       
/* 340 */       if ((bool) || 
/* 341 */         (verifySection((Attributes)localEntry.getValue(), str2, this.md)))
/*     */       {
/* 343 */         if (str2.startsWith("./")) {
/* 344 */           str2 = str2.substring(2);
/*     */         }
/* 346 */         if (str2.startsWith("/")) {
/* 347 */           str2 = str2.substring(1);
/*     */         }
/* 349 */         updateSigners(arrayOfCodeSigner, paramHashtable, str2);
/*     */         
/* 351 */         if (debug != null) {
/* 352 */           debug.println("processSignature signed name = " + str2);
/*     */         }
/*     */       }
/* 355 */       else if (debug != null) {
/* 356 */         debug.println("processSignature unsigned name = " + str2);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 361 */     updateSigners(arrayOfCodeSigner, paramHashtable, "META-INF/MANIFEST.MF");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean permittedCheck(String paramString1, String paramString2)
/*     */   {
/* 371 */     Boolean localBoolean = (Boolean)this.permittedAlgs.get(paramString2);
/* 372 */     if (localBoolean == null) {
/*     */       try {
/* 374 */         JAR_DISABLED_CHECK.permits(paramString2, new ConstraintsParameters(this.timestamp));
/*     */       }
/*     */       catch (GeneralSecurityException localGeneralSecurityException) {
/* 377 */         this.permittedAlgs.put(paramString2, Boolean.FALSE);
/* 378 */         this.permittedAlgs.put(paramString1.toUpperCase(), Boolean.FALSE);
/* 379 */         if (debug != null) {
/* 380 */           if (localGeneralSecurityException.getMessage() != null) {
/* 381 */             debug.println(paramString1 + ":  " + localGeneralSecurityException.getMessage());
/*     */           } else {
/* 383 */             debug.println(paramString1 + ":  " + paramString2 + " was disabled, no exception msg given.");
/*     */             
/* 385 */             localGeneralSecurityException.printStackTrace();
/*     */           }
/*     */         }
/* 388 */         return false;
/*     */       }
/*     */       
/* 391 */       this.permittedAlgs.put(paramString2, Boolean.TRUE);
/* 392 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 396 */     return localBoolean.booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   String getWeakAlgorithms(String paramString)
/*     */   {
/* 405 */     String str1 = "";
/*     */     try {
/* 407 */       for (String str2 : this.permittedAlgs.keySet()) {
/* 408 */         if (str2.endsWith(paramString)) {
/* 409 */           str1 = str1 + str2.substring(0, str2.length() - paramString.length()) + " ";
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (RuntimeException localRuntimeException) {
/* 414 */       str1 = "Unknown Algorithm(s).  Error processing " + paramString + ".  " + localRuntimeException.getMessage();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 419 */     if (str1.length() == 0) {
/* 420 */       return "Unknown Algorithm(s)";
/*     */     }
/*     */     
/* 423 */     return str1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean verifyManifestHash(Manifest paramManifest, ManifestDigester paramManifestDigester, List<Object> paramList)
/*     */     throws IOException, SignatureException
/*     */   {
/* 434 */     Attributes localAttributes = paramManifest.getMainAttributes();
/* 435 */     boolean bool = false;
/*     */     
/* 437 */     int i = 1;
/*     */     
/* 439 */     int j = 0;
/*     */     
/*     */ 
/* 442 */     for (Iterator localIterator = localAttributes.entrySet().iterator(); localIterator.hasNext();) { localObject = (Entry)localIterator.next();
/*     */       
/* 444 */       String str1 = ((Entry)localObject).getKey().toString();
/*     */       
/* 446 */       if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST-MANIFEST"))
/*     */       {
/* 448 */         String str2 = str1.substring(0, str1.length() - 16);
/* 449 */         j = 1;
/*     */         
/*     */ 
/* 452 */         if (permittedCheck(str1, str2))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 458 */           i = 0;
/*     */           
/* 460 */           paramList.add(str1);
/* 461 */           paramList.add(((Entry)localObject).getValue());
/* 462 */           MessageDigest localMessageDigest = getDigest(str2);
/* 463 */           if (localMessageDigest != null) {
/* 464 */             byte[] arrayOfByte1 = paramManifestDigester.manifestDigest(localMessageDigest);
/*     */             
/* 466 */             byte[] arrayOfByte2 = Base64.getMimeDecoder().decode((String)((Entry)localObject).getValue());
/*     */             
/* 468 */             if (debug != null) {
/* 469 */               debug.println("Signature File: Manifest digest " + str2);
/*     */               
/* 471 */               debug.println("  sigfile  " + toHex(arrayOfByte2));
/* 472 */               debug.println("  computed " + toHex(arrayOfByte1));
/* 473 */               debug.println();
/*     */             }
/*     */             
/* 476 */             if (MessageDigest.isEqual(arrayOfByte1, arrayOfByte2)) {
/* 477 */               bool = true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     Object localObject;
/* 485 */     if (debug != null) {
/* 486 */       debug.println("PermittedAlgs mapping: ");
/* 487 */       for (localIterator = this.permittedAlgs.keySet().iterator(); localIterator.hasNext();) { localObject = (String)localIterator.next();
/* 488 */         debug.println((String)localObject + " : " + 
/* 489 */           ((Boolean)this.permittedAlgs.get(localObject)).toString());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 494 */     if ((j != 0) && (i != 0))
/*     */     {
/*     */ 
/* 497 */       throw new SignatureException("Manifest hash check failed (DIGEST-MANIFEST). Disabled algorithm(s) used: " + getWeakAlgorithms("-DIGEST-MANIFEST"));
/*     */     }
/* 499 */     return bool;
/*     */   }
/*     */   
/*     */   private boolean verifyManifestMainAttrs(Manifest paramManifest, ManifestDigester paramManifestDigester)
/*     */     throws IOException, SignatureException
/*     */   {
/* 505 */     Attributes localAttributes = paramManifest.getMainAttributes();
/* 506 */     boolean bool = true;
/*     */     
/* 508 */     int i = 1;
/*     */     
/* 510 */     int j = 0;
/*     */     
/*     */ 
/*     */ 
/* 514 */     for (Iterator localIterator = localAttributes.entrySet().iterator(); localIterator.hasNext();) { localObject = (Entry)localIterator.next();
/* 515 */       String str1 = ((Entry)localObject).getKey().toString();
/*     */       
/* 517 */       if (str1.toUpperCase(Locale.ENGLISH).endsWith(ATTR_DIGEST))
/*     */       {
/* 519 */         String str2 = str1.substring(0, str1.length() - ATTR_DIGEST.length());
/* 520 */         j = 1;
/*     */         
/*     */ 
/* 523 */         if (permittedCheck(str1, str2))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 529 */           i = 0;
/*     */           
/* 531 */           MessageDigest localMessageDigest = getDigest(str2);
/* 532 */           if (localMessageDigest != null)
/*     */           {
/* 534 */             ManifestDigester.Entry localEntry = paramManifestDigester.get("Manifest-Main-Attributes", false);
/* 535 */             byte[] arrayOfByte1 = localEntry.digest(localMessageDigest);
/*     */             
/* 537 */             byte[] arrayOfByte2 = Base64.getMimeDecoder().decode((String)((Entry)localObject).getValue());
/*     */             
/* 539 */             if (debug != null) {
/* 540 */               debug.println("Signature File: Manifest Main Attributes digest " + localMessageDigest
/*     */               
/* 542 */                 .getAlgorithm());
/* 543 */               debug.println("  sigfile  " + toHex(arrayOfByte2));
/* 544 */               debug.println("  computed " + toHex(arrayOfByte1));
/* 545 */               debug.println();
/*     */             }
/*     */             
/* 548 */             if (!MessageDigest.isEqual(arrayOfByte1, arrayOfByte2))
/*     */             {
/*     */ 
/*     */ 
/* 552 */               bool = false;
/* 553 */               if (debug == null) break;
/* 554 */               debug.println("Verification of Manifest main attributes failed");
/*     */               
/* 556 */               debug.println(); break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     Object localObject;
/* 564 */     if (debug != null) {
/* 565 */       debug.println("PermittedAlgs mapping: ");
/* 566 */       for (localIterator = this.permittedAlgs.keySet().iterator(); localIterator.hasNext();) { localObject = (String)localIterator.next();
/* 567 */         debug.println((String)localObject + " : " + 
/* 568 */           ((Boolean)this.permittedAlgs.get(localObject)).toString());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 573 */     if ((j != 0) && (i != 0))
/*     */     {
/*     */ 
/*     */ 
/* 577 */       throw new SignatureException("Manifest Main Attribute check failed (" + ATTR_DIGEST + ").  Disabled algorithm(s) used: " + getWeakAlgorithms(ATTR_DIGEST));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 583 */     return bool;
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
/*     */   private boolean verifySection(Attributes paramAttributes, String paramString, ManifestDigester paramManifestDigester)
/*     */     throws IOException, SignatureException
/*     */   {
/* 600 */     boolean bool = false;
/* 601 */     ManifestDigester.Entry localEntry = paramManifestDigester.get(paramString, this.block.isOldStyle());
/*     */     
/* 603 */     int i = 1;
/*     */     
/* 605 */     int j = 0;
/*     */     
/* 607 */     if (localEntry == null) {
/* 608 */       throw new SecurityException("no manifest section for signature file entry " + paramString);
/*     */     }
/*     */     
/*     */     Iterator localIterator;
/* 612 */     if (paramAttributes != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 617 */       for (localIterator = paramAttributes.entrySet().iterator(); localIterator.hasNext();) { localObject = (Entry)localIterator.next();
/* 618 */         String str1 = ((Entry)localObject).getKey().toString();
/*     */         
/* 620 */         if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST"))
/*     */         {
/* 622 */           String str2 = str1.substring(0, str1.length() - 7);
/* 623 */           j = 1;
/*     */           
/*     */ 
/* 626 */           if (permittedCheck(str1, str2))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 632 */             i = 0;
/*     */             
/* 634 */             MessageDigest localMessageDigest = getDigest(str2);
/*     */             
/* 636 */             if (localMessageDigest != null) {
/* 637 */               int k = 0;
/*     */               
/*     */ 
/* 640 */               byte[] arrayOfByte1 = Base64.getMimeDecoder().decode((String)((Entry)localObject).getValue());
/*     */               byte[] arrayOfByte2;
/* 642 */               if (this.workaround) {
/* 643 */                 arrayOfByte2 = localEntry.digestWorkaround(localMessageDigest);
/*     */               } else {
/* 645 */                 arrayOfByte2 = localEntry.digest(localMessageDigest);
/*     */               }
/*     */               
/* 648 */               if (debug != null) {
/* 649 */                 debug.println("Signature Block File: " + paramString + " digest=" + localMessageDigest
/* 650 */                   .getAlgorithm());
/* 651 */                 debug.println("  expected " + toHex(arrayOfByte1));
/* 652 */                 debug.println("  computed " + toHex(arrayOfByte2));
/* 653 */                 debug.println();
/*     */               }
/*     */               
/* 656 */               if (MessageDigest.isEqual(arrayOfByte2, arrayOfByte1)) {
/* 657 */                 bool = true;
/* 658 */                 k = 1;
/*     */ 
/*     */               }
/* 661 */               else if (!this.workaround) {
/* 662 */                 arrayOfByte2 = localEntry.digestWorkaround(localMessageDigest);
/* 663 */                 if (MessageDigest.isEqual(arrayOfByte2, arrayOfByte1)) {
/* 664 */                   if (debug != null) {
/* 665 */                     debug.println("  re-computed " + toHex(arrayOfByte2));
/* 666 */                     debug.println();
/*     */                   }
/* 668 */                   this.workaround = true;
/* 669 */                   bool = true;
/* 670 */                   k = 1;
/*     */                 }
/*     */               }
/*     */               
/* 674 */               if (k == 0)
/*     */               {
/* 676 */                 throw new SecurityException("invalid " + localMessageDigest.getAlgorithm() + " signature file digest for " + paramString);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     Object localObject;
/* 684 */     if (debug != null) {
/* 685 */       debug.println("PermittedAlgs mapping: ");
/* 686 */       for (localIterator = this.permittedAlgs.keySet().iterator(); localIterator.hasNext();) { localObject = (String)localIterator.next();
/* 687 */         debug.println((String)localObject + " : " + 
/* 688 */           ((Boolean)this.permittedAlgs.get(localObject)).toString());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 693 */     if ((j != 0) && (i != 0))
/*     */     {
/*     */ 
/* 696 */       throw new SignatureException("Manifest Main Attribute check failed (DIGEST).  Disabled algorithm(s) used: " + getWeakAlgorithms("DIGEST"));
/*     */     }
/*     */     
/* 699 */     return bool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private CodeSigner[] getSigners(SignerInfo[] paramArrayOfSignerInfo, PKCS7 paramPKCS7)
/*     */     throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException
/*     */   {
/* 711 */     ArrayList localArrayList1 = null;
/*     */     
/* 713 */     for (int i = 0; i < paramArrayOfSignerInfo.length; i++)
/*     */     {
/* 715 */       SignerInfo localSignerInfo = paramArrayOfSignerInfo[i];
/* 716 */       ArrayList localArrayList2 = localSignerInfo.getCertificateChain(paramPKCS7);
/* 717 */       CertPath localCertPath = this.certificateFactory.generateCertPath(localArrayList2);
/* 718 */       if (localArrayList1 == null) {
/* 719 */         localArrayList1 = new ArrayList();
/*     */       }
/*     */       
/* 722 */       localArrayList1.add(new CodeSigner(localCertPath, localSignerInfo.getTimestamp()));
/*     */       
/* 724 */       if (debug != null) {
/* 725 */         debug.println("Signature Block Certificate: " + localArrayList2
/* 726 */           .get(0));
/*     */       }
/*     */     }
/*     */     
/* 730 */     if (localArrayList1 != null) {
/* 731 */       return (CodeSigner[])localArrayList1.toArray(new CodeSigner[localArrayList1.size()]);
/*     */     }
/* 733 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 738 */   private static final char[] hexc = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static String toHex(byte[] paramArrayOfByte)
/*     */   {
/* 748 */     StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length * 2);
/*     */     
/* 750 */     for (int i = 0; i < paramArrayOfByte.length; i++) {
/* 751 */       localStringBuilder.append(hexc[(paramArrayOfByte[i] >> 4 & 0xF)]);
/* 752 */       localStringBuilder.append(hexc[(paramArrayOfByte[i] & 0xF)]);
/*     */     }
/* 754 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   static boolean contains(CodeSigner[] paramArrayOfCodeSigner, CodeSigner paramCodeSigner)
/*     */   {
/* 760 */     for (int i = 0; i < paramArrayOfCodeSigner.length; i++) {
/* 761 */       if (paramArrayOfCodeSigner[i].equals(paramCodeSigner))
/* 762 */         return true;
/*     */     }
/* 764 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static boolean isSubSet(CodeSigner[] paramArrayOfCodeSigner1, CodeSigner[] paramArrayOfCodeSigner2)
/*     */   {
/* 771 */     if (paramArrayOfCodeSigner2 == paramArrayOfCodeSigner1) {
/* 772 */       return true;
/*     */     }
/*     */     
/* 775 */     for (int i = 0; i < paramArrayOfCodeSigner1.length; i++) {
/* 776 */       if (!contains(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1[i]))
/* 777 */         return false;
/*     */     }
/* 779 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean matches(CodeSigner[] paramArrayOfCodeSigner1, CodeSigner[] paramArrayOfCodeSigner2, CodeSigner[] paramArrayOfCodeSigner3)
/*     */   {
/* 791 */     if ((paramArrayOfCodeSigner2 == null) && (paramArrayOfCodeSigner1 == paramArrayOfCodeSigner3)) {
/* 792 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 797 */     if ((paramArrayOfCodeSigner2 != null) && (!isSubSet(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1))) {
/* 798 */       return false;
/*     */     }
/*     */     
/* 801 */     if (!isSubSet(paramArrayOfCodeSigner3, paramArrayOfCodeSigner1)) {
/* 802 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 808 */     for (int i = 0; i < paramArrayOfCodeSigner1.length; i++)
/*     */     {
/*     */ 
/* 811 */       int j = ((paramArrayOfCodeSigner2 != null) && (contains(paramArrayOfCodeSigner2, paramArrayOfCodeSigner1[i]))) || (contains(paramArrayOfCodeSigner3, paramArrayOfCodeSigner1[i])) ? 1 : 0;
/* 812 */       if (j == 0)
/* 813 */         return false;
/*     */     }
/* 815 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   void updateSigners(CodeSigner[] paramArrayOfCodeSigner, Hashtable<String, CodeSigner[]> paramHashtable, String paramString)
/*     */   {
/* 821 */     CodeSigner[] arrayOfCodeSigner1 = (CodeSigner[])paramHashtable.get(paramString);
/*     */     
/*     */ 
/*     */ 
/*     */     CodeSigner[] arrayOfCodeSigner2;
/*     */     
/*     */ 
/* 828 */     for (int i = this.signerCache.size() - 1; i != -1; i--) {
/* 829 */       arrayOfCodeSigner2 = (CodeSigner[])this.signerCache.get(i);
/* 830 */       if (matches(arrayOfCodeSigner2, arrayOfCodeSigner1, paramArrayOfCodeSigner)) {
/* 831 */         paramHashtable.put(paramString, arrayOfCodeSigner2);
/* 832 */         return;
/*     */       }
/*     */     }
/*     */     
/* 836 */     if (arrayOfCodeSigner1 == null) {
/* 837 */       arrayOfCodeSigner2 = paramArrayOfCodeSigner;
/*     */     } else {
/* 839 */       arrayOfCodeSigner2 = new CodeSigner[arrayOfCodeSigner1.length + paramArrayOfCodeSigner.length];
/*     */       
/* 841 */       System.arraycopy(arrayOfCodeSigner1, 0, arrayOfCodeSigner2, 0, arrayOfCodeSigner1.length);
/*     */       
/* 843 */       System.arraycopy(paramArrayOfCodeSigner, 0, arrayOfCodeSigner2, arrayOfCodeSigner1.length, paramArrayOfCodeSigner.length);
/*     */     }
/*     */     
/* 846 */     this.signerCache.add(arrayOfCodeSigner2);
/* 847 */     paramHashtable.put(paramString, arrayOfCodeSigner2);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\SignatureFileVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
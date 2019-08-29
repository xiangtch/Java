/*      */ package sun.security.x509;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.security.AccessController;
/*      */ import java.text.Normalizer;
/*      */ import java.text.Normalizer.Form;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.pkcs.PKCS9Attribute;
/*      */ import sun.security.util.Debug;
/*      */ import sun.security.util.DerEncoder;
/*      */ import sun.security.util.DerInputStream;
/*      */ import sun.security.util.DerOutputStream;
/*      */ import sun.security.util.DerValue;
/*      */ import sun.security.util.ObjectIdentifier;
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
/*      */ 
/*      */ public class AVA
/*      */   implements DerEncoder
/*      */ {
/*   63 */   private static final Debug debug = Debug.getInstance("x509", "\t[AVA]");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   68 */   private static final boolean PRESERVE_OLD_DC_ENCODING = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.security.preserveOldDCEncoding"))).booleanValue();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static final int DEFAULT = 1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static final int RFC1779 = 2;
/*      */   
/*      */ 
/*      */ 
/*      */   static final int RFC2253 = 3;
/*      */   
/*      */ 
/*      */ 
/*      */   final ObjectIdentifier oid;
/*      */   
/*      */ 
/*      */ 
/*      */   final DerValue value;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String specialChars1779 = ",=\n+<>#;\\\"";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String specialChars2253 = ",=+<>#;\\\"";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String specialCharsDefault = ",=\n+<>#;\\\" ";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String escapedDefault = ",+<>;\"";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String hexDigits = "0123456789ABCDEF";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public AVA(ObjectIdentifier paramObjectIdentifier, DerValue paramDerValue)
/*      */   {
/*  117 */     if ((paramObjectIdentifier == null) || (paramDerValue == null)) {
/*  118 */       throw new NullPointerException();
/*      */     }
/*  120 */     this.oid = paramObjectIdentifier;
/*  121 */     this.value = paramDerValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   AVA(Reader paramReader)
/*      */     throws IOException
/*      */   {
/*  134 */     this(paramReader, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   AVA(Reader paramReader, Map<String, String> paramMap)
/*      */     throws IOException
/*      */   {
/*  147 */     this(paramReader, 1, paramMap);
/*      */   }
/*      */   
/*      */ 
/*      */   AVA(Reader paramReader, int paramInt)
/*      */     throws IOException
/*      */   {
/*  154 */     this(paramReader, paramInt, Collections.emptyMap());
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
/*      */   AVA(Reader paramReader, int paramInt, Map<String, String> paramMap)
/*      */     throws IOException
/*      */   {
/*  173 */     StringBuilder localStringBuilder = new StringBuilder();
/*      */     
/*      */ 
/*      */     int i;
/*      */     
/*      */ 
/*      */     for (;;)
/*      */     {
/*  181 */       i = readChar(paramReader, "Incorrect AVA format");
/*  182 */       if (i == 61) {
/*      */         break;
/*      */       }
/*  185 */       localStringBuilder.append((char)i);
/*      */     }
/*      */     
/*  188 */     this.oid = AVAKeyword.getOID(localStringBuilder.toString(), paramInt, paramMap);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  195 */     localStringBuilder.setLength(0);
/*  196 */     if (paramInt == 3)
/*      */     {
/*  198 */       i = paramReader.read();
/*  199 */       if (i == 32) {
/*  200 */         throw new IOException("Incorrect AVA RFC2253 format - leading space must be escaped");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       do {
/*  206 */         i = paramReader.read();
/*  207 */       } while ((i == 32) || (i == 10));
/*      */     }
/*  209 */     if (i == -1)
/*      */     {
/*  211 */       this.value = new DerValue("");
/*  212 */       return;
/*      */     }
/*      */     
/*  215 */     if (i == 35) {
/*  216 */       this.value = parseHexString(paramReader, paramInt);
/*  217 */     } else if ((i == 34) && (paramInt != 3)) {
/*  218 */       this.value = parseQuotedString(paramReader, localStringBuilder);
/*      */     } else {
/*  220 */       this.value = parseString(paramReader, i, paramInt, localStringBuilder);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ObjectIdentifier getObjectIdentifier()
/*      */   {
/*  228 */     return this.oid;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DerValue getDerValue()
/*      */   {
/*  235 */     return this.value;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getValueString()
/*      */   {
/*      */     try
/*      */     {
/*  246 */       String str = this.value.getAsString();
/*  247 */       if (str == null) {
/*  248 */         throw new RuntimeException("AVA string is null");
/*      */       }
/*  250 */       return str;
/*      */     }
/*      */     catch (IOException localIOException) {
/*  253 */       throw new RuntimeException("AVA error: " + localIOException, localIOException);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static DerValue parseHexString(Reader paramReader, int paramInt)
/*      */     throws IOException
/*      */   {
/*  261 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*  262 */     int j = 0;
/*  263 */     int k = 0;
/*      */     for (;;) {
/*  265 */       int i = paramReader.read();
/*      */       
/*  267 */       if (isTerminator(i, paramInt)) {
/*      */         break;
/*      */       }
/*      */       
/*  271 */       int m = "0123456789ABCDEF".indexOf(Character.toUpperCase((char)i));
/*      */       
/*  273 */       if (m == -1) {
/*  274 */         throw new IOException("AVA parse, invalid hex digit: " + (char)i);
/*      */       }
/*      */       
/*      */ 
/*  278 */       if (k % 2 == 1) {
/*  279 */         j = (byte)(j * 16 + (byte)m);
/*  280 */         localByteArrayOutputStream.write(j);
/*      */       } else {
/*  282 */         j = (byte)m;
/*      */       }
/*  284 */       k++;
/*      */     }
/*      */     
/*      */ 
/*  288 */     if (k == 0) {
/*  289 */       throw new IOException("AVA parse, zero hex digits");
/*      */     }
/*      */     
/*      */ 
/*  293 */     if (k % 2 == 1) {
/*  294 */       throw new IOException("AVA parse, odd number of hex digits");
/*      */     }
/*      */     
/*  297 */     return new DerValue(localByteArrayOutputStream.toByteArray());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DerValue parseQuotedString(Reader paramReader, StringBuilder paramStringBuilder)
/*      */     throws IOException
/*      */   {
/*  308 */     int i = readChar(paramReader, "Quoted string did not end in quote");
/*      */     
/*  310 */     ArrayList localArrayList = new ArrayList();
/*  311 */     boolean bool = true;
/*  312 */     Object localObject; while (i != 34) {
/*  313 */       if (i == 92) {
/*  314 */         i = readChar(paramReader, "Quoted string did not end in quote");
/*      */         
/*      */ 
/*  317 */         localObject = null;
/*  318 */         if ((localObject = getEmbeddedHexPair(i, paramReader)) != null)
/*      */         {
/*      */ 
/*  321 */           bool = false;
/*      */           
/*      */ 
/*      */ 
/*  325 */           localArrayList.add(localObject);
/*  326 */           i = paramReader.read();
/*  327 */           continue;
/*      */         }
/*      */         
/*  330 */         if (",=\n+<>#;\\\"".indexOf((char)i) < 0) {
/*  331 */           throw new IOException("Invalid escaped character in AVA: " + (char)i);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  338 */       if (localArrayList.size() > 0) {
/*  339 */         localObject = getEmbeddedHexString(localArrayList);
/*  340 */         paramStringBuilder.append((String)localObject);
/*  341 */         localArrayList.clear();
/*      */       }
/*      */       
/*      */ 
/*  345 */       bool &= DerValue.isPrintableStringChar((char)i);
/*  346 */       paramStringBuilder.append((char)i);
/*  347 */       i = readChar(paramReader, "Quoted string did not end in quote");
/*      */     }
/*      */     
/*      */ 
/*  351 */     if (localArrayList.size() > 0) {
/*  352 */       localObject = getEmbeddedHexString(localArrayList);
/*  353 */       paramStringBuilder.append((String)localObject);
/*  354 */       localArrayList.clear();
/*      */     }
/*      */     do
/*      */     {
/*  358 */       i = paramReader.read();
/*  359 */     } while ((i == 10) || (i == 32));
/*  360 */     if (i != -1) {
/*  361 */       throw new IOException("AVA had characters other than whitespace after terminating quote");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  367 */     if ((this.oid.equals(PKCS9Attribute.EMAIL_ADDRESS_OID)) || (
/*  368 */       (this.oid.equals(X500Name.DOMAIN_COMPONENT_OID)) && (!PRESERVE_OLD_DC_ENCODING)))
/*      */     {
/*      */ 
/*  371 */       return new DerValue((byte)22, paramStringBuilder
/*  372 */         .toString().trim()); }
/*  373 */     if (bool) {
/*  374 */       return new DerValue(paramStringBuilder.toString().trim());
/*      */     }
/*  376 */     return new DerValue((byte)12, paramStringBuilder
/*  377 */       .toString().trim());
/*      */   }
/*      */   
/*      */ 
/*      */   private DerValue parseString(Reader paramReader, int paramInt1, int paramInt2, StringBuilder paramStringBuilder)
/*      */     throws IOException
/*      */   {
/*  384 */     ArrayList localArrayList = new ArrayList();
/*  385 */     boolean bool = true;
/*  386 */     int i = 0;
/*  387 */     int j = 1;
/*  388 */     int k = 0;
/*      */     do {
/*  390 */       i = 0;
/*  391 */       if (paramInt1 == 92) {
/*  392 */         i = 1;
/*  393 */         paramInt1 = readChar(paramReader, "Invalid trailing backslash");
/*      */         
/*      */ 
/*  396 */         Byte localByte = null;
/*  397 */         if ((localByte = getEmbeddedHexPair(paramInt1, paramReader)) != null)
/*      */         {
/*      */ 
/*  400 */           bool = false;
/*      */           
/*      */ 
/*      */ 
/*  404 */           localArrayList.add(localByte);
/*  405 */           paramInt1 = paramReader.read();
/*  406 */           j = 0;
/*  407 */           continue;
/*      */         }
/*      */         
/*      */ 
/*  411 */         if ((paramInt2 == 1) && 
/*  412 */           (",=\n+<>#;\\\" ".indexOf((char)paramInt1) == -1)) {
/*  413 */           throw new IOException("Invalid escaped character in AVA: '" + (char)paramInt1 + "'");
/*      */         }
/*      */         
/*  416 */         if (paramInt2 == 3) {
/*  417 */           if (paramInt1 == 32)
/*      */           {
/*  419 */             if ((j == 0) && (!trailingSpace(paramReader))) {
/*  420 */               throw new IOException("Invalid escaped space character in AVA.  Only a leading or trailing space character can be escaped.");
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*  425 */           else if (paramInt1 == 35)
/*      */           {
/*  427 */             if (j == 0) {
/*  428 */               throw new IOException("Invalid escaped '#' character in AVA.  Only a leading '#' can be escaped.");
/*      */             }
/*      */             
/*      */           }
/*  432 */           else if (",=+<>#;\\\"".indexOf((char)paramInt1) == -1) {
/*  433 */             throw new IOException("Invalid escaped character in AVA: '" + (char)paramInt1 + "'");
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*  440 */       else if (paramInt2 == 3) {
/*  441 */         if (",=+<>#;\\\"".indexOf((char)paramInt1) != -1) {
/*  442 */           throw new IOException("Character '" + (char)paramInt1 + "' in AVA appears without escape");
/*      */         }
/*      */         
/*      */       }
/*  446 */       else if (",+<>;\"".indexOf((char)paramInt1) != -1) {
/*  447 */         throw new IOException("Character '" + (char)paramInt1 + "' in AVA appears without escape");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  454 */       if (localArrayList.size() > 0)
/*      */       {
/*  456 */         for (int m = 0; m < k; m++) {
/*  457 */           paramStringBuilder.append(" ");
/*      */         }
/*  459 */         k = 0;
/*      */         
/*  461 */         String str1 = getEmbeddedHexString(localArrayList);
/*  462 */         paramStringBuilder.append(str1);
/*  463 */         localArrayList.clear();
/*      */       }
/*      */       
/*      */ 
/*  467 */       bool &= DerValue.isPrintableStringChar((char)paramInt1);
/*  468 */       if ((paramInt1 == 32) && (i == 0))
/*      */       {
/*      */ 
/*  471 */         k++;
/*      */       }
/*      */       else {
/*  474 */         for (int n = 0; n < k; n++) {
/*  475 */           paramStringBuilder.append(" ");
/*      */         }
/*  477 */         k = 0;
/*  478 */         paramStringBuilder.append((char)paramInt1);
/*      */       }
/*  480 */       paramInt1 = paramReader.read();
/*  481 */       j = 0;
/*  482 */     } while (!isTerminator(paramInt1, paramInt2));
/*      */     
/*  484 */     if ((paramInt2 == 3) && (k > 0)) {
/*  485 */       throw new IOException("Incorrect AVA RFC2253 format - trailing space must be escaped");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  490 */     if (localArrayList.size() > 0) {
/*  491 */       String str2 = getEmbeddedHexString(localArrayList);
/*  492 */       paramStringBuilder.append(str2);
/*  493 */       localArrayList.clear();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  498 */     if ((this.oid.equals(PKCS9Attribute.EMAIL_ADDRESS_OID)) || (
/*  499 */       (this.oid.equals(X500Name.DOMAIN_COMPONENT_OID)) && (!PRESERVE_OLD_DC_ENCODING)))
/*      */     {
/*      */ 
/*  502 */       return new DerValue((byte)22, paramStringBuilder.toString()); }
/*  503 */     if (bool) {
/*  504 */       return new DerValue(paramStringBuilder.toString());
/*      */     }
/*  506 */     return new DerValue((byte)12, paramStringBuilder.toString());
/*      */   }
/*      */   
/*      */ 
/*      */   private static Byte getEmbeddedHexPair(int paramInt, Reader paramReader)
/*      */     throws IOException
/*      */   {
/*  513 */     if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)paramInt)) >= 0) {
/*  514 */       int i = readChar(paramReader, "unexpected EOF - escaped hex value must include two valid digits");
/*      */       
/*      */ 
/*  517 */       if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)i)) >= 0) {
/*  518 */         int j = Character.digit((char)paramInt, 16);
/*  519 */         int k = Character.digit((char)i, 16);
/*  520 */         return new Byte((byte)((j << 4) + k));
/*      */       }
/*  522 */       throw new IOException("escaped hex value must include two valid digits");
/*      */     }
/*      */     
/*      */ 
/*  526 */     return null;
/*      */   }
/*      */   
/*      */   private static String getEmbeddedHexString(List<Byte> paramList) throws IOException
/*      */   {
/*  531 */     int i = paramList.size();
/*  532 */     byte[] arrayOfByte = new byte[i];
/*  533 */     for (int j = 0; j < i; j++) {
/*  534 */       arrayOfByte[j] = ((Byte)paramList.get(j)).byteValue();
/*      */     }
/*  536 */     return new String(arrayOfByte, "UTF8");
/*      */   }
/*      */   
/*      */   private static boolean isTerminator(int paramInt1, int paramInt2) {
/*  540 */     switch (paramInt1) {
/*      */     case -1: 
/*      */     case 43: 
/*      */     case 44: 
/*  544 */       return true;
/*      */     case 59: 
/*  546 */       return paramInt2 != 3;
/*      */     }
/*  548 */     return false;
/*      */   }
/*      */   
/*      */   private static int readChar(Reader paramReader, String paramString) throws IOException
/*      */   {
/*  553 */     int i = paramReader.read();
/*  554 */     if (i == -1) {
/*  555 */       throw new IOException(paramString);
/*      */     }
/*  557 */     return i;
/*      */   }
/*      */   
/*      */   private static boolean trailingSpace(Reader paramReader) throws IOException
/*      */   {
/*  562 */     boolean bool = false;
/*      */     
/*  564 */     if (!paramReader.markSupported())
/*      */     {
/*  566 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  571 */     paramReader.mark(9999);
/*      */     for (;;) {
/*  573 */       int i = paramReader.read();
/*  574 */       if (i == -1) {
/*  575 */         bool = true;
/*  576 */         break; }
/*  577 */       if (i != 32)
/*      */       {
/*  579 */         if (i == 92) {
/*  580 */           int j = paramReader.read();
/*  581 */           if (j != 32) {
/*  582 */             bool = false;
/*  583 */             break;
/*      */           }
/*      */         } else {
/*  586 */           bool = false;
/*  587 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  591 */     paramReader.reset();
/*  592 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */   AVA(DerValue paramDerValue)
/*      */     throws IOException
/*      */   {
/*  599 */     if (paramDerValue.tag != 48) {
/*  600 */       throw new IOException("AVA not a sequence");
/*      */     }
/*  602 */     this.oid = X500Name.intern(paramDerValue.data.getOID());
/*  603 */     this.value = paramDerValue.data.getDerValue();
/*      */     
/*  605 */     if (paramDerValue.data.available() != 0)
/*      */     {
/*  607 */       throw new IOException("AVA, extra bytes = " + paramDerValue.data.available());
/*      */     }
/*      */   }
/*      */   
/*      */   AVA(DerInputStream paramDerInputStream) throws IOException {
/*  612 */     this(paramDerInputStream.getDerValue());
/*      */   }
/*      */   
/*      */   public boolean equals(Object paramObject) {
/*  616 */     if (this == paramObject) {
/*  617 */       return true;
/*      */     }
/*  619 */     if (!(paramObject instanceof AVA)) {
/*  620 */       return false;
/*      */     }
/*  622 */     AVA localAVA = (AVA)paramObject;
/*  623 */     return toRFC2253CanonicalString()
/*  624 */       .equals(localAVA.toRFC2253CanonicalString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  633 */     return toRFC2253CanonicalString().hashCode();
/*      */   }
/*      */   
/*      */ 
/*      */   public void encode(DerOutputStream paramDerOutputStream)
/*      */     throws IOException
/*      */   {
/*  640 */     derEncode(paramDerOutputStream);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void derEncode(OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/*  653 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  654 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*      */     
/*  656 */     localDerOutputStream1.putOID(this.oid);
/*  657 */     this.value.encode(localDerOutputStream1);
/*  658 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/*  659 */     paramOutputStream.write(localDerOutputStream2.toByteArray());
/*      */   }
/*      */   
/*      */   private String toKeyword(int paramInt, Map<String, String> paramMap) {
/*  663 */     return AVAKeyword.getKeyword(this.oid, paramInt, paramMap);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  671 */     return 
/*  672 */       toKeywordValueString(toKeyword(1, Collections.emptyMap()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toRFC1779String()
/*      */   {
/*  681 */     return toRFC1779String(Collections.emptyMap());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toRFC1779String(Map<String, String> paramMap)
/*      */   {
/*  691 */     return toKeywordValueString(toKeyword(2, paramMap));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toRFC2253String()
/*      */   {
/*  700 */     return toRFC2253String(Collections.emptyMap());
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
/*      */   public String toRFC2253String(Map<String, String> paramMap)
/*      */   {
/*  717 */     StringBuilder localStringBuilder1 = new StringBuilder(100);
/*  718 */     localStringBuilder1.append(toKeyword(3, paramMap));
/*  719 */     localStringBuilder1.append('=');
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Object localObject;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  730 */     if (((localStringBuilder1.charAt(0) >= '0') && (localStringBuilder1.charAt(0) <= '9')) || 
/*  731 */       (!isDerString(this.value, false)))
/*      */     {
/*  733 */       localObject = null;
/*      */       try {
/*  735 */         localObject = this.value.toByteArray();
/*      */       } catch (IOException localIOException1) {
/*  737 */         throw new IllegalArgumentException("DER Value conversion");
/*      */       }
/*  739 */       localStringBuilder1.append('#');
/*  740 */       for (int i = 0; i < localObject.length; i++) {
/*  741 */         int j = localObject[i];
/*  742 */         localStringBuilder1.append(Character.forDigit(0xF & j >>> 4, 16));
/*  743 */         localStringBuilder1.append(Character.forDigit(0xF & j, 16));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*  754 */       localObject = null;
/*      */       try {
/*  756 */         localObject = new String(this.value.getDataBytes(), "UTF8");
/*      */       } catch (IOException localIOException2) {
/*  758 */         throw new IllegalArgumentException("DER Value conversion");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  785 */       StringBuilder localStringBuilder2 = new StringBuilder();
/*      */       char c;
/*  787 */       for (int k = 0; k < ((String)localObject).length(); k++) {
/*  788 */         m = ((String)localObject).charAt(k);
/*  789 */         if ((DerValue.isPrintableStringChar(m)) || 
/*  790 */           (",=+<>#;\"\\".indexOf(m) >= 0))
/*      */         {
/*      */ 
/*  793 */           if (",=+<>#;\"\\".indexOf(m) >= 0) {
/*  794 */             localStringBuilder2.append('\\');
/*      */           }
/*      */           
/*      */ 
/*  798 */           localStringBuilder2.append(m);
/*      */         }
/*  800 */         else if (m == 0)
/*      */         {
/*  802 */           localStringBuilder2.append("\\00");
/*      */         }
/*  804 */         else if ((debug != null) && (Debug.isOn("ava")))
/*      */         {
/*      */ 
/*      */ 
/*  808 */           byte[] arrayOfByte = null;
/*      */           try {
/*  810 */             arrayOfByte = Character.toString(m).getBytes("UTF8");
/*      */           } catch (IOException localIOException3) {
/*  812 */             throw new IllegalArgumentException("DER Value conversion");
/*      */           }
/*      */           
/*  815 */           for (i1 = 0; i1 < arrayOfByte.length; i1++) {
/*  816 */             localStringBuilder2.append('\\');
/*      */             
/*  818 */             c = Character.forDigit(0xF & arrayOfByte[i1] >>> 4, 16);
/*  819 */             localStringBuilder2.append(Character.toUpperCase(c));
/*      */             
/*  821 */             c = Character.forDigit(0xF & arrayOfByte[i1], 16);
/*  822 */             localStringBuilder2.append(Character.toUpperCase(c));
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  827 */           localStringBuilder2.append(m);
/*      */         }
/*      */       }
/*      */       
/*  831 */       char[] arrayOfChar = localStringBuilder2.toString().toCharArray();
/*  832 */       localStringBuilder2 = new StringBuilder();
/*      */       
/*      */ 
/*      */ 
/*  836 */       for (int m = 0; m < arrayOfChar.length; m++) {
/*  837 */         if ((arrayOfChar[m] != ' ') && (arrayOfChar[m] != '\r')) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*  842 */       for (int n = arrayOfChar.length - 1; n >= 0; n--) {
/*  843 */         if ((arrayOfChar[n] != ' ') && (arrayOfChar[n] != '\r')) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  849 */       for (int i1 = 0; i1 < arrayOfChar.length; i1++) {
/*  850 */         c = arrayOfChar[i1];
/*  851 */         if ((i1 < m) || (i1 > n)) {
/*  852 */           localStringBuilder2.append('\\');
/*      */         }
/*  854 */         localStringBuilder2.append(c);
/*      */       }
/*  856 */       localStringBuilder1.append(localStringBuilder2.toString());
/*      */     }
/*  858 */     return localStringBuilder1.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toRFC2253CanonicalString()
/*      */   {
/*  869 */     StringBuilder localStringBuilder1 = new StringBuilder(40);
/*  870 */     localStringBuilder1
/*  871 */       .append(toKeyword(3, Collections.emptyMap()));
/*  872 */     localStringBuilder1.append('=');
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
/*  883 */     if (((localStringBuilder1.charAt(0) >= '0') && (localStringBuilder1.charAt(0) <= '9')) || 
/*  884 */       (!isDerString(this.value, true)))
/*      */     {
/*  886 */       localObject = null;
/*      */       try {
/*  888 */         localObject = this.value.toByteArray();
/*      */       } catch (IOException localIOException1) {
/*  890 */         throw new IllegalArgumentException("DER Value conversion");
/*      */       }
/*  892 */       localStringBuilder1.append('#');
/*  893 */       for (int i = 0; i < localObject.length; i++) {
/*  894 */         int j = localObject[i];
/*  895 */         localStringBuilder1.append(Character.forDigit(0xF & j >>> 4, 16));
/*  896 */         localStringBuilder1.append(Character.forDigit(0xF & j, 16));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*  907 */       localObject = null;
/*      */       try {
/*  909 */         localObject = new String(this.value.getDataBytes(), "UTF8");
/*      */       } catch (IOException localIOException2) {
/*  911 */         throw new IllegalArgumentException("DER Value conversion");
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
/*      */ 
/*      */ 
/*  933 */       StringBuilder localStringBuilder2 = new StringBuilder();
/*  934 */       int k = 0;
/*      */       
/*  936 */       for (int m = 0; m < ((String)localObject).length(); m++) {
/*  937 */         char c = ((String)localObject).charAt(m);
/*      */         
/*  939 */         if ((DerValue.isPrintableStringChar(c)) || 
/*  940 */           (",+<>;\"\\".indexOf(c) >= 0) || ((m == 0) && (c == '#')))
/*      */         {
/*      */ 
/*      */ 
/*  944 */           if (((m == 0) && (c == '#')) || (",+<>;\"\\".indexOf(c) >= 0)) {
/*  945 */             localStringBuilder2.append('\\');
/*      */           }
/*      */           
/*      */ 
/*  949 */           if (!Character.isWhitespace(c)) {
/*  950 */             k = 0;
/*  951 */             localStringBuilder2.append(c);
/*      */           }
/*  953 */           else if (k == 0)
/*      */           {
/*  955 */             k = 1;
/*  956 */             localStringBuilder2.append(c);
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */         }
/*  963 */         else if ((debug != null) && (Debug.isOn("ava")))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  968 */           k = 0;
/*      */           
/*  970 */           byte[] arrayOfByte = null;
/*      */           try {
/*  972 */             arrayOfByte = Character.toString(c).getBytes("UTF8");
/*      */           } catch (IOException localIOException3) {
/*  974 */             throw new IllegalArgumentException("DER Value conversion");
/*      */           }
/*      */           
/*  977 */           for (int n = 0; n < arrayOfByte.length; n++) {
/*  978 */             localStringBuilder2.append('\\');
/*  979 */             localStringBuilder2.append(
/*  980 */               Character.forDigit(0xF & arrayOfByte[n] >>> 4, 16));
/*  981 */             localStringBuilder2.append(
/*  982 */               Character.forDigit(0xF & arrayOfByte[n], 16));
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/*  988 */           k = 0;
/*  989 */           localStringBuilder2.append(c);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  994 */       localStringBuilder1.append(localStringBuilder2.toString().trim());
/*      */     }
/*      */     
/*  997 */     Object localObject = localStringBuilder1.toString();
/*  998 */     localObject = ((String)localObject).toUpperCase(Locale.US).toLowerCase(Locale.US);
/*  999 */     return Normalizer.normalize((CharSequence)localObject, Form.NFKD);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static boolean isDerString(DerValue paramDerValue, boolean paramBoolean)
/*      */   {
/* 1006 */     if (paramBoolean) {
/* 1007 */       switch (paramDerValue.tag) {
/*      */       case 12: 
/*      */       case 19: 
/* 1010 */         return true;
/*      */       }
/* 1012 */       return false;
/*      */     }
/*      */     
/* 1015 */     switch (paramDerValue.tag) {
/*      */     case 12: 
/*      */     case 19: 
/*      */     case 20: 
/*      */     case 22: 
/*      */     case 27: 
/*      */     case 30: 
/* 1022 */       return true;
/*      */     }
/* 1024 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   boolean hasRFC2253Keyword()
/*      */   {
/* 1030 */     return AVAKeyword.hasKeyword(this.oid, 3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String toKeywordValueString(String paramString)
/*      */   {
/* 1039 */     StringBuilder localStringBuilder1 = new StringBuilder(40);
/*      */     
/* 1041 */     localStringBuilder1.append(paramString);
/* 1042 */     localStringBuilder1.append("=");
/*      */     try
/*      */     {
/* 1045 */       String str = this.value.getAsString();
/*      */       
/* 1047 */       if (str == null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1054 */         byte[] arrayOfByte1 = this.value.toByteArray();
/*      */         
/* 1056 */         localStringBuilder1.append('#');
/* 1057 */         for (int j = 0; j < arrayOfByte1.length; j++) {
/* 1058 */           localStringBuilder1.append("0123456789ABCDEF".charAt(arrayOfByte1[j] >> 4 & 0xF));
/* 1059 */           localStringBuilder1.append("0123456789ABCDEF".charAt(arrayOfByte1[j] & 0xF));
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1064 */         int i = 0;
/* 1065 */         StringBuilder localStringBuilder2 = new StringBuilder();
/* 1066 */         int k = 0;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1074 */         int m = str.length();
/*      */         
/*      */ 
/* 1077 */         int n = (m > 1) && (str.charAt(0) == '"') && (str.charAt(m - 1) == '"') ? 1 : 0;
/*      */         
/* 1079 */         for (int i1 = 0; i1 < m; i1++) {
/* 1080 */           char c1 = str.charAt(i1);
/* 1081 */           if ((n != 0) && ((i1 == 0) || (i1 == m - 1))) {
/* 1082 */             localStringBuilder2.append(c1);
/*      */ 
/*      */           }
/* 1085 */           else if ((DerValue.isPrintableStringChar(c1)) || 
/* 1086 */             (",+=\n<>#;\\\"".indexOf(c1) >= 0))
/*      */           {
/*      */ 
/* 1089 */             if ((i == 0) && (((i1 == 0) && ((c1 == ' ') || (c1 == '\n'))) || 
/*      */             
/* 1091 */               (",+=\n<>#;\\\"".indexOf(c1) >= 0))) {
/* 1092 */               i = 1;
/*      */             }
/*      */             
/*      */ 
/* 1096 */             if ((c1 != ' ') && (c1 != '\n'))
/*      */             {
/* 1098 */               if ((c1 == '"') || (c1 == '\\')) {
/* 1099 */                 localStringBuilder2.append('\\');
/*      */               }
/* 1101 */               k = 0;
/*      */             } else {
/* 1103 */               if ((i == 0) && (k != 0)) {
/* 1104 */                 i = 1;
/*      */               }
/* 1106 */               k = 1;
/*      */             }
/*      */             
/* 1109 */             localStringBuilder2.append(c1);
/*      */           }
/* 1111 */           else if ((debug != null) && (Debug.isOn("ava")))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1116 */             k = 0;
/*      */             
/*      */ 
/*      */ 
/* 1120 */             byte[] arrayOfByte2 = Character.toString(c1).getBytes("UTF8");
/* 1121 */             for (int i2 = 0; i2 < arrayOfByte2.length; i2++) {
/* 1122 */               localStringBuilder2.append('\\');
/*      */               
/* 1124 */               char c2 = Character.forDigit(0xF & arrayOfByte2[i2] >>> 4, 16);
/* 1125 */               localStringBuilder2.append(Character.toUpperCase(c2));
/*      */               
/* 1127 */               c2 = Character.forDigit(0xF & arrayOfByte2[i2], 16);
/* 1128 */               localStringBuilder2.append(Character.toUpperCase(c2));
/*      */             }
/*      */             
/*      */           }
/*      */           else
/*      */           {
/* 1134 */             k = 0;
/* 1135 */             localStringBuilder2.append(c1);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1140 */         if (localStringBuilder2.length() > 0) {
/* 1141 */           i1 = localStringBuilder2.charAt(localStringBuilder2.length() - 1);
/* 1142 */           if ((i1 == 32) || (i1 == 10)) {
/* 1143 */             i = 1;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1149 */         if ((n == 0) && (i != 0)) {
/* 1150 */           localStringBuilder1.append("\"" + localStringBuilder2.toString() + "\"");
/*      */         } else {
/* 1152 */           localStringBuilder1.append(localStringBuilder2.toString());
/*      */         }
/*      */       }
/*      */     } catch (IOException localIOException) {
/* 1156 */       throw new IllegalArgumentException("DER Value conversion");
/*      */     }
/*      */     
/* 1159 */     return localStringBuilder1.toString();
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\AVA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
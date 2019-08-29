/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
/*     */ import sun.misc.IOUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DerValue
/*     */ {
/*     */   public static final byte TAG_UNIVERSAL = 0;
/*     */   public static final byte TAG_APPLICATION = 64;
/*     */   public static final byte TAG_CONTEXT = -128;
/*     */   public static final byte TAG_PRIVATE = -64;
/*     */   public byte tag;
/*     */   protected DerInputBuffer buffer;
/*     */   public final DerInputStream data;
/*     */   private int length;
/*     */   public static final byte tag_Boolean = 1;
/*     */   public static final byte tag_Integer = 2;
/*     */   public static final byte tag_BitString = 3;
/*     */   public static final byte tag_OctetString = 4;
/*     */   public static final byte tag_Null = 5;
/*     */   public static final byte tag_ObjectId = 6;
/*     */   public static final byte tag_Enumerated = 10;
/*     */   public static final byte tag_UTF8String = 12;
/*     */   public static final byte tag_PrintableString = 19;
/*     */   public static final byte tag_T61String = 20;
/*     */   public static final byte tag_IA5String = 22;
/*     */   public static final byte tag_UtcTime = 23;
/*     */   public static final byte tag_GeneralizedTime = 24;
/*     */   public static final byte tag_GeneralString = 27;
/*     */   public static final byte tag_UniversalString = 28;
/*     */   public static final byte tag_BMPString = 30;
/*     */   public static final byte tag_Sequence = 48;
/*     */   public static final byte tag_SequenceOf = 48;
/*     */   public static final byte tag_Set = 49;
/*     */   public static final byte tag_SetOf = 49;
/*     */   
/*     */   public boolean isUniversal()
/*     */   {
/* 167 */     return (this.tag & 0xC0) == 0;
/*     */   }
/*     */   
/*     */   public boolean isApplication()
/*     */   {
/* 172 */     return (this.tag & 0xC0) == 64;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isContextSpecific()
/*     */   {
/* 178 */     return (this.tag & 0xC0) == 128;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isContextSpecific(byte paramByte)
/*     */   {
/* 184 */     if (!isContextSpecific()) {
/* 185 */       return false;
/*     */     }
/* 187 */     return (this.tag & 0x1F) == paramByte;
/*     */   }
/*     */   
/* 190 */   boolean isPrivate() { return (this.tag & 0xC0) == 192; }
/*     */   
/*     */   public boolean isConstructed() {
/* 193 */     return (this.tag & 0x20) == 32;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isConstructed(byte paramByte)
/*     */   {
/* 199 */     if (!isConstructed()) {
/* 200 */       return false;
/*     */     }
/* 202 */     return (this.tag & 0x1F) == paramByte;
/*     */   }
/*     */   
/*     */ 
/*     */   public DerValue(String paramString)
/*     */     throws IOException
/*     */   {
/* 209 */     int i = 1;
/* 210 */     for (int j = 0; j < paramString.length(); j++) {
/* 211 */       if (!isPrintableStringChar(paramString.charAt(j))) {
/* 212 */         i = 0;
/* 213 */         break;
/*     */       }
/*     */     }
/*     */     
/* 217 */     this.data = init((byte)(i != 0 ? 19 : 12), paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DerValue(byte paramByte, String paramString)
/*     */     throws IOException
/*     */   {
/* 226 */     this.data = init(paramByte, paramString);
/*     */   }
/*     */   
/*     */ 
/*     */   DerValue(byte paramByte, byte[] paramArrayOfByte, boolean paramBoolean)
/*     */   {
/* 232 */     this.tag = paramByte;
/* 233 */     this.buffer = new DerInputBuffer((byte[])paramArrayOfByte.clone(), paramBoolean);
/* 234 */     this.length = paramArrayOfByte.length;
/* 235 */     this.data = new DerInputStream(this.buffer);
/* 236 */     this.data.mark(Integer.MAX_VALUE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DerValue(byte paramByte, byte[] paramArrayOfByte)
/*     */   {
/* 246 */     this(paramByte, paramArrayOfByte, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   DerValue(DerInputBuffer paramDerInputBuffer)
/*     */     throws IOException
/*     */   {
/* 256 */     this.tag = ((byte)paramDerInputBuffer.read());
/* 257 */     int i = (byte)paramDerInputBuffer.read();
/* 258 */     this.length = DerInputStream.getLength(i, paramDerInputBuffer);
/* 259 */     if (this.length == -1) {
/* 260 */       DerInputBuffer localDerInputBuffer = paramDerInputBuffer.dup();
/* 261 */       int j = localDerInputBuffer.available();
/* 262 */       int k = 2;
/* 263 */       byte[] arrayOfByte = new byte[j + k];
/* 264 */       arrayOfByte[0] = this.tag;
/* 265 */       arrayOfByte[1] = i;
/* 266 */       DataInputStream localDataInputStream = new DataInputStream(localDerInputBuffer);
/* 267 */       localDataInputStream.readFully(arrayOfByte, k, j);
/* 268 */       localDataInputStream.close();
/* 269 */       DerIndefLenConverter localDerIndefLenConverter = new DerIndefLenConverter();
/* 270 */       localDerInputBuffer = new DerInputBuffer(localDerIndefLenConverter.convert(arrayOfByte), paramDerInputBuffer.allowBER);
/* 271 */       if (this.tag != localDerInputBuffer.read()) {
/* 272 */         throw new IOException("Indefinite length encoding not supported");
/*     */       }
/* 274 */       this.length = DerInputStream.getLength(localDerInputBuffer);
/* 275 */       this.buffer = localDerInputBuffer.dup();
/* 276 */       this.buffer.truncate(this.length);
/* 277 */       this.data = new DerInputStream(this.buffer);
/*     */       
/*     */ 
/*     */ 
/* 281 */       paramDerInputBuffer.skip(this.length + k);
/*     */     }
/*     */     else {
/* 284 */       this.buffer = paramDerInputBuffer.dup();
/* 285 */       this.buffer.truncate(this.length);
/* 286 */       this.data = new DerInputStream(this.buffer);
/*     */       
/* 288 */       paramDerInputBuffer.skip(this.length);
/*     */     }
/*     */   }
/*     */   
/*     */   DerValue(byte[] paramArrayOfByte, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 295 */     this.data = init(true, new ByteArrayInputStream(paramArrayOfByte), paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DerValue(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 306 */     this(paramArrayOfByte, true);
/*     */   }
/*     */   
/*     */ 
/*     */   DerValue(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 313 */     this.data = init(true, new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2), paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DerValue(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 326 */     this(paramArrayOfByte, paramInt1, paramInt2, true);
/*     */   }
/*     */   
/*     */   DerValue(InputStream paramInputStream, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 332 */     this.data = init(false, paramInputStream, paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DerValue(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 345 */     this(paramInputStream, true);
/*     */   }
/*     */   
/*     */   private DerInputStream init(byte paramByte, String paramString) throws IOException
/*     */   {
/* 350 */     String str = null;
/*     */     
/* 352 */     this.tag = paramByte;
/*     */     
/* 354 */     switch (paramByte) {
/*     */     case 19: 
/*     */     case 22: 
/*     */     case 27: 
/* 358 */       str = "ASCII";
/* 359 */       break;
/*     */     case 20: 
/* 361 */       str = "ISO-8859-1";
/* 362 */       break;
/*     */     case 30: 
/* 364 */       str = "UnicodeBigUnmarked";
/* 365 */       break;
/*     */     case 12: 
/* 367 */       str = "UTF8";
/* 368 */       break;
/*     */     case 13: case 14: case 15: case 16: case 17: 
/*     */     case 18: case 21: case 23: case 24: case 25: 
/*     */     case 26: case 28: case 29: default: 
/* 372 */       throw new IllegalArgumentException("Unsupported DER string type");
/*     */     }
/*     */     
/* 375 */     byte[] arrayOfByte = paramString.getBytes(str);
/* 376 */     this.length = arrayOfByte.length;
/* 377 */     this.buffer = new DerInputBuffer(arrayOfByte, true);
/* 378 */     DerInputStream localDerInputStream = new DerInputStream(this.buffer);
/* 379 */     localDerInputStream.mark(Integer.MAX_VALUE);
/* 380 */     return localDerInputStream;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private DerInputStream init(boolean paramBoolean1, InputStream paramInputStream, boolean paramBoolean2)
/*     */     throws IOException
/*     */   {
/* 389 */     this.tag = ((byte)paramInputStream.read());
/* 390 */     int i = (byte)paramInputStream.read();
/* 391 */     this.length = DerInputStream.getLength(i, paramInputStream);
/* 392 */     if (this.length == -1) {
/* 393 */       int j = paramInputStream.available();
/* 394 */       int k = 2;
/* 395 */       byte[] arrayOfByte2 = new byte[j + k];
/* 396 */       arrayOfByte2[0] = this.tag;
/* 397 */       arrayOfByte2[1] = i;
/* 398 */       DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
/* 399 */       localDataInputStream.readFully(arrayOfByte2, k, j);
/* 400 */       localDataInputStream.close();
/* 401 */       DerIndefLenConverter localDerIndefLenConverter = new DerIndefLenConverter();
/* 402 */       paramInputStream = new ByteArrayInputStream(localDerIndefLenConverter.convert(arrayOfByte2));
/* 403 */       if (this.tag != paramInputStream.read()) {
/* 404 */         throw new IOException("Indefinite length encoding not supported");
/*     */       }
/* 406 */       this.length = DerInputStream.getLength(paramInputStream);
/*     */     }
/*     */     
/* 409 */     if ((paramBoolean1) && (paramInputStream.available() != this.length)) {
/* 410 */       throw new IOException("extra data given to DerValue constructor");
/*     */     }
/* 412 */     byte[] arrayOfByte1 = IOUtils.readFully(paramInputStream, this.length, true);
/*     */     
/* 414 */     this.buffer = new DerInputBuffer(arrayOfByte1, paramBoolean2);
/* 415 */     return new DerInputStream(this.buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void encode(DerOutputStream paramDerOutputStream)
/*     */     throws IOException
/*     */   {
/* 423 */     paramDerOutputStream.write(this.tag);
/* 424 */     paramDerOutputStream.putLength(this.length);
/*     */     
/* 426 */     if (this.length > 0) {
/* 427 */       byte[] arrayOfByte = new byte[this.length];
/*     */       
/* 429 */       synchronized (this.data) {
/* 430 */         this.buffer.reset();
/* 431 */         if (this.buffer.read(arrayOfByte) != this.length) {
/* 432 */           throw new IOException("short DER value read (encode)");
/*     */         }
/* 434 */         paramDerOutputStream.write(arrayOfByte);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public final DerInputStream getData() {
/* 440 */     return this.data;
/*     */   }
/*     */   
/*     */   public final byte getTag() {
/* 444 */     return this.tag;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getBoolean()
/*     */     throws IOException
/*     */   {
/* 453 */     if (this.tag != 1) {
/* 454 */       throw new IOException("DerValue.getBoolean, not a BOOLEAN " + this.tag);
/*     */     }
/* 456 */     if (this.length != 1) {
/* 457 */       throw new IOException("DerValue.getBoolean, invalid length " + this.length);
/*     */     }
/*     */     
/* 460 */     if (this.buffer.read() != 0) {
/* 461 */       return true;
/*     */     }
/* 463 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ObjectIdentifier getOID()
/*     */     throws IOException
/*     */   {
/* 472 */     if (this.tag != 6)
/* 473 */       throw new IOException("DerValue.getOID, not an OID " + this.tag);
/* 474 */     return new ObjectIdentifier(this.buffer);
/*     */   }
/*     */   
/*     */   private byte[] append(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
/* 478 */     if (paramArrayOfByte1 == null) {
/* 479 */       return paramArrayOfByte2;
/*     */     }
/* 481 */     byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramArrayOfByte2.length];
/* 482 */     System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
/* 483 */     System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, paramArrayOfByte1.length, paramArrayOfByte2.length);
/*     */     
/* 485 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getOctetString()
/*     */     throws IOException
/*     */   {
/* 495 */     if ((this.tag != 4) && (!isConstructed((byte)4))) {
/* 496 */       throw new IOException("DerValue.getOctetString, not an Octet String: " + this.tag);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 501 */     if (this.length == 0) {
/* 502 */       return new byte[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 508 */     DerInputBuffer localDerInputBuffer = this.buffer;
/* 509 */     if (localDerInputBuffer.available() < this.length) {
/* 510 */       throw new IOException("short read on DerValue buffer");
/*     */     }
/* 512 */     byte[] arrayOfByte = new byte[this.length];
/* 513 */     localDerInputBuffer.read(arrayOfByte);
/*     */     
/* 515 */     if (isConstructed()) {
/* 516 */       DerInputStream localDerInputStream = new DerInputStream(arrayOfByte, 0, arrayOfByte.length, this.buffer.allowBER);
/*     */       
/* 518 */       arrayOfByte = null;
/* 519 */       while (localDerInputStream.available() != 0) {
/* 520 */         arrayOfByte = append(arrayOfByte, localDerInputStream.getOctetString());
/*     */       }
/*     */     }
/* 523 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInteger()
/*     */     throws IOException
/*     */   {
/* 532 */     if (this.tag != 2) {
/* 533 */       throw new IOException("DerValue.getInteger, not an int " + this.tag);
/*     */     }
/* 535 */     return this.buffer.getInteger(this.data.available());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getBigInteger()
/*     */     throws IOException
/*     */   {
/* 544 */     if (this.tag != 2)
/* 545 */       throw new IOException("DerValue.getBigInteger, not an int " + this.tag);
/* 546 */     return this.buffer.getBigInteger(this.data.available(), false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigInteger getPositiveBigInteger()
/*     */     throws IOException
/*     */   {
/* 557 */     if (this.tag != 2)
/* 558 */       throw new IOException("DerValue.getBigInteger, not an int " + this.tag);
/* 559 */     return this.buffer.getBigInteger(this.data.available(), true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getEnumerated()
/*     */     throws IOException
/*     */   {
/* 568 */     if (this.tag != 10) {
/* 569 */       throw new IOException("DerValue.getEnumerated, incorrect tag: " + this.tag);
/*     */     }
/*     */     
/* 572 */     return this.buffer.getInteger(this.data.available());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getBitString()
/*     */     throws IOException
/*     */   {
/* 581 */     if (this.tag != 3) {
/* 582 */       throw new IOException("DerValue.getBitString, not a bit string " + this.tag);
/*     */     }
/*     */     
/* 585 */     return this.buffer.getBitString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BitArray getUnalignedBitString()
/*     */     throws IOException
/*     */   {
/* 594 */     if (this.tag != 3) {
/* 595 */       throw new IOException("DerValue.getBitString, not a bit string " + this.tag);
/*     */     }
/*     */     
/* 598 */     return this.buffer.getUnalignedBitString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsString()
/*     */     throws IOException
/*     */   {
/* 607 */     if (this.tag == 12)
/* 608 */       return getUTF8String();
/* 609 */     if (this.tag == 19)
/* 610 */       return getPrintableString();
/* 611 */     if (this.tag == 20)
/* 612 */       return getT61String();
/* 613 */     if (this.tag == 22) {
/* 614 */       return getIA5String();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 619 */     if (this.tag == 30)
/* 620 */       return getBMPString();
/* 621 */     if (this.tag == 27) {
/* 622 */       return getGeneralString();
/*     */     }
/* 624 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getBitString(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 635 */     if ((!paramBoolean) && 
/* 636 */       (this.tag != 3)) {
/* 637 */       throw new IOException("DerValue.getBitString, not a bit string " + this.tag);
/*     */     }
/*     */     
/* 640 */     return this.buffer.getBitString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BitArray getUnalignedBitString(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 652 */     if ((!paramBoolean) && 
/* 653 */       (this.tag != 3)) {
/* 654 */       throw new IOException("DerValue.getBitString, not a bit string " + this.tag);
/*     */     }
/*     */     
/* 657 */     return this.buffer.getUnalignedBitString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getDataBytes()
/*     */     throws IOException
/*     */   {
/* 665 */     byte[] arrayOfByte = new byte[this.length];
/* 666 */     synchronized (this.data) {
/* 667 */       this.data.reset();
/* 668 */       this.data.getBytes(arrayOfByte);
/*     */     }
/* 670 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPrintableString()
/*     */     throws IOException
/*     */   {
/* 680 */     if (this.tag != 19) {
/* 681 */       throw new IOException("DerValue.getPrintableString, not a string " + this.tag);
/*     */     }
/*     */     
/* 684 */     return new String(getDataBytes(), "ASCII");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getT61String()
/*     */     throws IOException
/*     */   {
/* 693 */     if (this.tag != 20) {
/* 694 */       throw new IOException("DerValue.getT61String, not T61 " + this.tag);
/*     */     }
/*     */     
/* 697 */     return new String(getDataBytes(), "ISO-8859-1");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getIA5String()
/*     */     throws IOException
/*     */   {
/* 706 */     if (this.tag != 22) {
/* 707 */       throw new IOException("DerValue.getIA5String, not IA5 " + this.tag);
/*     */     }
/*     */     
/* 710 */     return new String(getDataBytes(), "ASCII");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getBMPString()
/*     */     throws IOException
/*     */   {
/* 720 */     if (this.tag != 30) {
/* 721 */       throw new IOException("DerValue.getBMPString, not BMP " + this.tag);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 726 */     return new String(getDataBytes(), "UnicodeBigUnmarked");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUTF8String()
/*     */     throws IOException
/*     */   {
/* 736 */     if (this.tag != 12) {
/* 737 */       throw new IOException("DerValue.getUTF8String, not UTF-8 " + this.tag);
/*     */     }
/*     */     
/* 740 */     return new String(getDataBytes(), "UTF8");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getGeneralString()
/*     */     throws IOException
/*     */   {
/* 750 */     if (this.tag != 27) {
/* 751 */       throw new IOException("DerValue.getGeneralString, not GeneralString " + this.tag);
/*     */     }
/*     */     
/* 754 */     return new String(getDataBytes(), "ASCII");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date getUTCTime()
/*     */     throws IOException
/*     */   {
/* 763 */     if (this.tag != 23) {
/* 764 */       throw new IOException("DerValue.getUTCTime, not a UtcTime: " + this.tag);
/*     */     }
/* 766 */     return this.buffer.getUTCTime(this.data.available());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date getGeneralizedTime()
/*     */     throws IOException
/*     */   {
/* 775 */     if (this.tag != 24) {
/* 776 */       throw new IOException("DerValue.getGeneralizedTime, not a GeneralizedTime: " + this.tag);
/*     */     }
/*     */     
/* 779 */     return this.buffer.getGeneralizedTime(this.data.available());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 789 */     if ((paramObject instanceof DerValue)) {
/* 790 */       return equals((DerValue)paramObject);
/*     */     }
/* 792 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(DerValue paramDerValue)
/*     */   {
/* 803 */     if (this == paramDerValue) {
/* 804 */       return true;
/*     */     }
/* 806 */     if (this.tag != paramDerValue.tag) {
/* 807 */       return false;
/*     */     }
/* 809 */     if (this.data == paramDerValue.data) {
/* 810 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 814 */     return 
/* 815 */       System.identityHashCode(this.data) > System.identityHashCode(paramDerValue.data) ? 
/* 816 */       doEquals(this, paramDerValue) : 
/* 817 */       doEquals(paramDerValue, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean doEquals(DerValue paramDerValue1, DerValue paramDerValue2)
/*     */   {
/* 824 */     synchronized (paramDerValue1.data) {
/* 825 */       synchronized (paramDerValue2.data) {
/* 826 */         paramDerValue1.data.reset();
/* 827 */         paramDerValue2.data.reset();
/* 828 */         return paramDerValue1.buffer.equals(paramDerValue2.buffer);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 841 */       String str = getAsString();
/* 842 */       if (str != null)
/* 843 */         return "\"" + str + "\"";
/* 844 */       if (this.tag == 5)
/* 845 */         return "[DerValue, null]";
/* 846 */       if (this.tag == 6) {
/* 847 */         return "OID." + getOID();
/*     */       }
/*     */       
/*     */ 
/* 851 */       return "[DerValue, tag = " + this.tag + ", length = " + this.length + "]";
/*     */     }
/*     */     catch (IOException localIOException) {
/* 854 */       throw new IllegalArgumentException("misformatted DER value");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] toByteArray()
/*     */     throws IOException
/*     */   {
/* 865 */     DerOutputStream localDerOutputStream = new DerOutputStream();
/*     */     
/* 867 */     encode(localDerOutputStream);
/* 868 */     this.data.reset();
/* 869 */     return localDerOutputStream.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DerInputStream toDerInputStream()
/*     */     throws IOException
/*     */   {
/* 879 */     if ((this.tag == 48) || (this.tag == 49))
/* 880 */       return new DerInputStream(this.buffer);
/* 881 */     throw new IOException("toDerInputStream rejects tag type " + this.tag);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int length()
/*     */   {
/* 888 */     return this.length;
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
/*     */   public static boolean isPrintableStringChar(char paramChar)
/*     */   {
/* 909 */     if (((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z')) || ((paramChar >= '0') && (paramChar <= '9')))
/*     */     {
/* 911 */       return true;
/*     */     }
/* 913 */     switch (paramChar) {
/*     */     case ' ': 
/*     */     case '\'': 
/*     */     case '(': 
/*     */     case ')': 
/*     */     case '+': 
/*     */     case ',': 
/*     */     case '-': 
/*     */     case '.': 
/*     */     case '/': 
/*     */     case ':': 
/*     */     case '=': 
/*     */     case '?': 
/* 926 */       return true;
/*     */     }
/* 928 */     return false;
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
/*     */   public static byte createTag(byte paramByte1, boolean paramBoolean, byte paramByte2)
/*     */   {
/* 943 */     byte b = (byte)(paramByte1 | paramByte2);
/* 944 */     if (paramBoolean) {
/* 945 */       b = (byte)(b | 0x20);
/*     */     }
/* 947 */     return b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resetTag(byte paramByte)
/*     */   {
/* 957 */     this.tag = paramByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 966 */     return toString().hashCode();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\DerValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
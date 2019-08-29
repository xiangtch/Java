/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
/*     */ import sun.util.calendar.CalendarDate;
/*     */ import sun.util.calendar.CalendarSystem;
/*     */ import sun.util.calendar.Gregorian;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class DerInputBuffer
/*     */   extends ByteArrayInputStream
/*     */   implements Cloneable
/*     */ {
/*  47 */   boolean allowBER = true;
/*     */   
/*     */   DerInputBuffer(byte[] paramArrayOfByte)
/*     */   {
/*  51 */     this(paramArrayOfByte, true);
/*     */   }
/*     */   
/*     */   DerInputBuffer(byte[] paramArrayOfByte, boolean paramBoolean) {
/*  55 */     super(paramArrayOfByte);
/*  56 */     this.allowBER = paramBoolean;
/*     */   }
/*     */   
/*     */   DerInputBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean) {
/*  60 */     super(paramArrayOfByte, paramInt1, paramInt2);
/*  61 */     this.allowBER = paramBoolean;
/*     */   }
/*     */   
/*     */   DerInputBuffer dup() {
/*     */     try {
/*  66 */       DerInputBuffer localDerInputBuffer = (DerInputBuffer)clone();
/*  67 */       localDerInputBuffer.mark(Integer.MAX_VALUE);
/*  68 */       return localDerInputBuffer;
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*  70 */       throw new IllegalArgumentException(localCloneNotSupportedException.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   byte[] toByteArray() {
/*  75 */     int i = available();
/*  76 */     if (i <= 0)
/*  77 */       return null;
/*  78 */     byte[] arrayOfByte = new byte[i];
/*     */     
/*  80 */     System.arraycopy(this.buf, this.pos, arrayOfByte, 0, i);
/*  81 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */   int peek() throws IOException {
/*  85 */     if (this.pos >= this.count) {
/*  86 */       throw new IOException("out of data");
/*     */     }
/*  88 */     return this.buf[this.pos];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/*  96 */     if ((paramObject instanceof DerInputBuffer)) {
/*  97 */       return equals((DerInputBuffer)paramObject);
/*     */     }
/*  99 */     return false;
/*     */   }
/*     */   
/*     */   boolean equals(DerInputBuffer paramDerInputBuffer) {
/* 103 */     if (this == paramDerInputBuffer) {
/* 104 */       return true;
/*     */     }
/* 106 */     int i = available();
/* 107 */     if (paramDerInputBuffer.available() != i)
/* 108 */       return false;
/* 109 */     for (int j = 0; j < i; j++) {
/* 110 */       if (this.buf[(this.pos + j)] != paramDerInputBuffer.buf[(paramDerInputBuffer.pos + j)]) {
/* 111 */         return false;
/*     */       }
/*     */     }
/* 114 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 123 */     int i = 0;
/*     */     
/* 125 */     int j = available();
/* 126 */     int k = this.pos;
/*     */     
/* 128 */     for (int m = 0; m < j; m++)
/* 129 */       i += this.buf[(k + m)] * m;
/* 130 */     return i;
/*     */   }
/*     */   
/*     */   void truncate(int paramInt) throws IOException {
/* 134 */     if (paramInt > available())
/* 135 */       throw new IOException("insufficient data");
/* 136 */     this.count = (this.pos + paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   BigInteger getBigInteger(int paramInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 148 */     if (paramInt > available()) {
/* 149 */       throw new IOException("short read of integer");
/*     */     }
/* 151 */     if (paramInt == 0) {
/* 152 */       throw new IOException("Invalid encoding: zero length Int value");
/*     */     }
/*     */     
/* 155 */     byte[] arrayOfByte = new byte[paramInt];
/*     */     
/* 157 */     System.arraycopy(this.buf, this.pos, arrayOfByte, 0, paramInt);
/* 158 */     skip(paramInt);
/*     */     
/*     */ 
/* 161 */     if ((!this.allowBER) && (paramInt >= 2) && (arrayOfByte[0] == 0) && (arrayOfByte[1] >= 0)) {
/* 162 */       throw new IOException("Invalid encoding: redundant leading 0s");
/*     */     }
/*     */     
/* 165 */     if (paramBoolean) {
/* 166 */       return new BigInteger(1, arrayOfByte);
/*     */     }
/* 168 */     return new BigInteger(arrayOfByte);
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
/*     */   public int getInteger(int paramInt)
/*     */     throws IOException
/*     */   {
/* 183 */     BigInteger localBigInteger = getBigInteger(paramInt, false);
/* 184 */     if (localBigInteger.compareTo(BigInteger.valueOf(-2147483648L)) < 0) {
/* 185 */       throw new IOException("Integer below minimum valid value");
/*     */     }
/* 187 */     if (localBigInteger.compareTo(BigInteger.valueOf(2147483647L)) > 0) {
/* 188 */       throw new IOException("Integer exceeds maximum valid value");
/*     */     }
/* 190 */     return localBigInteger.intValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getBitString(int paramInt)
/*     */     throws IOException
/*     */   {
/* 198 */     if (paramInt > available()) {
/* 199 */       throw new IOException("short read of bit string");
/*     */     }
/* 201 */     if (paramInt == 0) {
/* 202 */       throw new IOException("Invalid encoding: zero length bit string");
/*     */     }
/*     */     
/* 205 */     int i = this.buf[this.pos];
/* 206 */     if ((i < 0) || (i > 7)) {
/* 207 */       throw new IOException("Invalid number of padding bits");
/*     */     }
/*     */     
/* 210 */     byte[] arrayOfByte = new byte[paramInt - 1];
/* 211 */     System.arraycopy(this.buf, this.pos + 1, arrayOfByte, 0, paramInt - 1);
/* 212 */     if (i != 0)
/*     */     {
/* 214 */       int tmp94_93 = (paramInt - 2); byte[] tmp94_90 = arrayOfByte;tmp94_90[tmp94_93] = ((byte)(tmp94_90[tmp94_93] & 255 << i));
/*     */     }
/* 216 */     skip(paramInt);
/* 217 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */ 
/*     */   byte[] getBitString()
/*     */     throws IOException
/*     */   {
/* 224 */     return getBitString(available());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   BitArray getUnalignedBitString()
/*     */     throws IOException
/*     */   {
/* 232 */     if (this.pos >= this.count) {
/* 233 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 238 */     int i = available();
/* 239 */     int j = this.buf[this.pos] & 0xFF;
/* 240 */     if (j > 7) {
/* 241 */       throw new IOException("Invalid value for unused bits: " + j);
/*     */     }
/* 243 */     byte[] arrayOfByte = new byte[i - 1];
/*     */     
/* 245 */     int k = arrayOfByte.length == 0 ? 0 : arrayOfByte.length * 8 - j;
/*     */     
/* 247 */     System.arraycopy(this.buf, this.pos + 1, arrayOfByte, 0, i - 1);
/*     */     
/* 249 */     BitArray localBitArray = new BitArray(k, arrayOfByte);
/* 250 */     this.pos = this.count;
/* 251 */     return localBitArray;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date getUTCTime(int paramInt)
/*     */     throws IOException
/*     */   {
/* 260 */     if (paramInt > available()) {
/* 261 */       throw new IOException("short read of DER UTC Time");
/*     */     }
/* 263 */     if ((paramInt < 11) || (paramInt > 17)) {
/* 264 */       throw new IOException("DER UTC Time length error");
/*     */     }
/* 266 */     return getTime(paramInt, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date getGeneralizedTime(int paramInt)
/*     */     throws IOException
/*     */   {
/* 275 */     if (paramInt > available()) {
/* 276 */       throw new IOException("short read of DER Generalized Time");
/*     */     }
/* 278 */     if ((paramInt < 13) || (paramInt > 23)) {
/* 279 */       throw new IOException("DER Generalized Time length error");
/*     */     }
/* 281 */     return getTime(paramInt, true);
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
/*     */   private Date getTime(int paramInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 312 */     String str = null;
/*     */     int i;
/* 314 */     if (paramBoolean) {
/* 315 */       str = "Generalized";
/* 316 */       i = 1000 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 317 */       i += 100 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 318 */       i += 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 319 */       i += Character.digit((char)this.buf[(this.pos++)], 10);
/* 320 */       paramInt -= 2;
/*     */     } else {
/* 322 */       str = "UTC";
/* 323 */       i = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 324 */       i += Character.digit((char)this.buf[(this.pos++)], 10);
/*     */       
/* 326 */       if (i < 50) {
/* 327 */         i += 2000;
/*     */       } else {
/* 329 */         i += 1900;
/*     */       }
/*     */     }
/* 332 */     int j = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 333 */     j += Character.digit((char)this.buf[(this.pos++)], 10);
/*     */     
/* 335 */     int k = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 336 */     k += Character.digit((char)this.buf[(this.pos++)], 10);
/*     */     
/* 338 */     int m = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 339 */     m += Character.digit((char)this.buf[(this.pos++)], 10);
/*     */     
/* 341 */     int n = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 342 */     n += Character.digit((char)this.buf[(this.pos++)], 10);
/*     */     
/* 344 */     paramInt -= 10;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 352 */     int i2 = 0;
/* 353 */     int i1; if ((paramInt > 2) && (paramInt < 12)) {
/* 354 */       i1 = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 355 */       i1 += Character.digit((char)this.buf[(this.pos++)], 10);
/* 356 */       paramInt -= 2;
/*     */       
/* 358 */       if ((this.buf[this.pos] == 46) || (this.buf[this.pos] == 44)) {
/* 359 */         paramInt--;
/* 360 */         this.pos += 1;
/*     */         
/* 362 */         int i3 = 0;
/* 363 */         int i4 = this.pos;
/* 364 */         while ((this.buf[i4] != 90) && (this.buf[i4] != 43) && (this.buf[i4] != 45))
/*     */         {
/*     */ 
/* 367 */           i4++;
/* 368 */           i3++;
/*     */         }
/* 370 */         switch (i3) {
/*     */         case 3: 
/* 372 */           i2 += 100 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 373 */           i2 += 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 374 */           i2 += Character.digit((char)this.buf[(this.pos++)], 10);
/* 375 */           break;
/*     */         case 2: 
/* 377 */           i2 += 100 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 378 */           i2 += 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 379 */           break;
/*     */         case 1: 
/* 381 */           i2 += 100 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 382 */           break;
/*     */         default: 
/* 384 */           throw new IOException("Parse " + str + " time, unsupported precision for seconds value");
/*     */         }
/*     */         
/* 387 */         paramInt -= i3;
/*     */       }
/*     */     } else {
/* 390 */       i1 = 0;
/*     */     }
/* 392 */     if ((j == 0) || (k == 0) || (j > 12) || (k > 31) || (m >= 24) || (n >= 60) || (i1 >= 60))
/*     */     {
/*     */ 
/* 395 */       throw new IOException("Parse " + str + " time, invalid format");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 401 */     Gregorian localGregorian = CalendarSystem.getGregorianCalendar();
/* 402 */     CalendarDate localCalendarDate = localGregorian.newCalendarDate(null);
/* 403 */     localCalendarDate.setDate(i, j, k);
/* 404 */     localCalendarDate.setTimeOfDay(m, n, i1, i2);
/* 405 */     long l = localGregorian.getTime(localCalendarDate);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 410 */     if ((paramInt != 1) && (paramInt != 5)) {
/* 411 */       throw new IOException("Parse " + str + " time, invalid offset");
/*     */     }
/*     */     int i5;
/*     */     int i6;
/* 415 */     switch (this.buf[(this.pos++)]) {
/*     */     case 43: 
/* 417 */       i5 = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 418 */       i5 += Character.digit((char)this.buf[(this.pos++)], 10);
/* 419 */       i6 = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 420 */       i6 += Character.digit((char)this.buf[(this.pos++)], 10);
/*     */       
/* 422 */       if ((i5 >= 24) || (i6 >= 60)) {
/* 423 */         throw new IOException("Parse " + str + " time, +hhmm");
/*     */       }
/* 425 */       l -= (i5 * 60 + i6) * 60 * 1000;
/* 426 */       break;
/*     */     
/*     */     case 45: 
/* 429 */       i5 = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 430 */       i5 += Character.digit((char)this.buf[(this.pos++)], 10);
/* 431 */       i6 = 10 * Character.digit((char)this.buf[(this.pos++)], 10);
/* 432 */       i6 += Character.digit((char)this.buf[(this.pos++)], 10);
/*     */       
/* 434 */       if ((i5 >= 24) || (i6 >= 60)) {
/* 435 */         throw new IOException("Parse " + str + " time, -hhmm");
/*     */       }
/* 437 */       l += (i5 * 60 + i6) * 60 * 1000;
/* 438 */       break;
/*     */     
/*     */     case 90: 
/*     */       break;
/*     */     
/*     */     default: 
/* 444 */       throw new IOException("Parse " + str + " time, garbage offset");
/*     */     }
/* 446 */     return new Date(l);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\DerInputBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
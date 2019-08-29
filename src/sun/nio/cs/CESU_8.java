/*     */ package sun.nio.cs;
/*     */ 
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class CESU_8
/*     */   extends Unicode
/*     */ {
/*     */   public CESU_8()
/*     */   {
/*  55 */     super("CESU-8", StandardCharsets.aliases_CESU_8);
/*     */   }
/*     */   
/*     */   public String historicalName() {
/*  59 */     return "CESU8";
/*     */   }
/*     */   
/*     */   public CharsetDecoder newDecoder() {
/*  63 */     return new Decoder(this, null);
/*     */   }
/*     */   
/*     */   public CharsetEncoder newEncoder() {
/*  67 */     return new Encoder(this, null);
/*     */   }
/*     */   
/*     */   private static final void updatePositions(Buffer paramBuffer1, int paramInt1, Buffer paramBuffer2, int paramInt2)
/*     */   {
/*  72 */     paramBuffer1.position(paramInt1 - paramBuffer1.arrayOffset());
/*  73 */     paramBuffer2.position(paramInt2 - paramBuffer2.arrayOffset());
/*     */   }
/*     */   
/*     */   private static class Decoder extends CharsetDecoder implements ArrayDecoder
/*     */   {
/*     */     private Decoder(Charset paramCharset) {
/*  79 */       super(1.0F, 1.0F);
/*     */     }
/*     */     
/*     */     private static boolean isNotContinuation(int paramInt) {
/*  83 */       return (paramInt & 0xC0) != 128;
/*     */     }
/*     */     
/*     */ 
/*     */     private static boolean isMalformed3(int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/*  89 */       return ((paramInt1 == -32) && ((paramInt2 & 0xE0) == 128)) || ((paramInt2 & 0xC0) != 128) || ((paramInt3 & 0xC0) != 128);
/*     */     }
/*     */     
/*     */ 
/*     */     private static boolean isMalformed3_2(int paramInt1, int paramInt2)
/*     */     {
/*  95 */       return ((paramInt1 == -32) && ((paramInt2 & 0xE0) == 128)) || ((paramInt2 & 0xC0) != 128);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static boolean isMalformed4(int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 106 */       return ((paramInt1 & 0xC0) != 128) || ((paramInt2 & 0xC0) != 128) || ((paramInt3 & 0xC0) != 128);
/*     */     }
/*     */     
/*     */ 
/*     */     private static boolean isMalformed4_2(int paramInt1, int paramInt2)
/*     */     {
/* 112 */       return ((paramInt1 == 240) && (paramInt2 == 144)) || ((paramInt2 & 0xC0) != 128);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 117 */     private static boolean isMalformed4_3(int paramInt) { return (paramInt & 0xC0) != 128; }
/*     */     
/*     */     private static CoderResult malformedN(ByteBuffer paramByteBuffer, int paramInt) { int i;
/*     */       int j;
/* 121 */       switch (paramInt) {
/*     */       case 1: 
/*     */       case 2: 
/* 124 */         return CoderResult.malformedForLength(1);
/*     */       case 3: 
/* 126 */         i = paramByteBuffer.get();
/* 127 */         j = paramByteBuffer.get();
/* 128 */         return CoderResult.malformedForLength(((i == -32) && ((j & 0xE0) == 128)) || 
/*     */         
/* 130 */           (isNotContinuation(j)) ? 1 : 2);
/*     */       case 4: 
/* 132 */         i = paramByteBuffer.get() & 0xFF;
/* 133 */         j = paramByteBuffer.get() & 0xFF;
/* 134 */         if ((i > 244) || ((i == 240) && ((j < 144) || (j > 191))) || ((i == 244) && ((j & 0xF0) != 128)) || 
/*     */         
/*     */ 
/* 137 */           (isNotContinuation(j)))
/* 138 */           return CoderResult.malformedForLength(1);
/* 139 */         if (isNotContinuation(paramByteBuffer.get()))
/* 140 */           return CoderResult.malformedForLength(2);
/* 141 */         return CoderResult.malformedForLength(3);
/*     */       }
/* 143 */       if (!$assertionsDisabled) throw new AssertionError();
/* 144 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private static CoderResult malformed(ByteBuffer paramByteBuffer, int paramInt1, CharBuffer paramCharBuffer, int paramInt2, int paramInt3)
/*     */     {
/* 152 */       paramByteBuffer.position(paramInt1 - paramByteBuffer.arrayOffset());
/* 153 */       CoderResult localCoderResult = malformedN(paramByteBuffer, paramInt3);
/* 154 */       CESU_8.updatePositions(paramByteBuffer, paramInt1, paramCharBuffer, paramInt2);
/* 155 */       return localCoderResult;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static CoderResult malformed(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
/*     */     {
/* 162 */       paramByteBuffer.position(paramInt1);
/* 163 */       CoderResult localCoderResult = malformedN(paramByteBuffer, paramInt2);
/* 164 */       paramByteBuffer.position(paramInt1);
/* 165 */       return localCoderResult;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static CoderResult malformedForLength(ByteBuffer paramByteBuffer, int paramInt1, CharBuffer paramCharBuffer, int paramInt2, int paramInt3)
/*     */     {
/* 174 */       CESU_8.updatePositions(paramByteBuffer, paramInt1, paramCharBuffer, paramInt2);
/* 175 */       return CoderResult.malformedForLength(paramInt3);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static CoderResult malformedForLength(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
/*     */     {
/* 182 */       paramByteBuffer.position(paramInt1);
/* 183 */       return CoderResult.malformedForLength(paramInt2);
/*     */     }
/*     */     
/*     */ 
/*     */     private static CoderResult xflow(Buffer paramBuffer1, int paramInt1, int paramInt2, Buffer paramBuffer2, int paramInt3, int paramInt4)
/*     */     {
/* 189 */       CESU_8.updatePositions(paramBuffer1, paramInt1, paramBuffer2, paramInt3);
/* 190 */       return (paramInt4 == 0) || (paramInt2 - paramInt1 < paramInt4) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
/*     */     }
/*     */     
/*     */     private static CoderResult xflow(Buffer paramBuffer, int paramInt1, int paramInt2)
/*     */     {
/* 195 */       paramBuffer.position(paramInt1);
/* 196 */       return (paramInt2 == 0) || (paramBuffer.remaining() < paramInt2) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private CoderResult decodeArrayLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/* 204 */       byte[] arrayOfByte = paramByteBuffer.array();
/* 205 */       int i = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/* 206 */       int j = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/*     */       
/* 208 */       char[] arrayOfChar = paramCharBuffer.array();
/* 209 */       int k = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/* 210 */       int m = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/* 211 */       int n = k + Math.min(j - i, m - k);
/*     */       
/*     */ 
/* 214 */       while ((k < n) && (arrayOfByte[i] >= 0))
/* 215 */         arrayOfChar[(k++)] = ((char)arrayOfByte[(i++)]);
/* 216 */       while (i < j) {
/* 217 */         int i1 = arrayOfByte[i];
/* 218 */         if (i1 >= 0)
/*     */         {
/* 220 */           if (k >= m)
/* 221 */             return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 1);
/* 222 */           arrayOfChar[(k++)] = ((char)i1);
/* 223 */           i++; } else { int i2;
/* 224 */           if ((i1 >> 5 == -2) && ((i1 & 0x1E) != 0))
/*     */           {
/* 226 */             if ((j - i < 2) || (k >= m))
/* 227 */               return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 2);
/* 228 */             i2 = arrayOfByte[(i + 1)];
/* 229 */             if (isNotContinuation(i2))
/* 230 */               return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 1);
/* 231 */             arrayOfChar[(k++)] = ((char)(i1 << 6 ^ i2 ^ 0xF80));
/*     */             
/*     */ 
/*     */ 
/* 235 */             i += 2;
/* 236 */           } else if (i1 >> 4 == -2)
/*     */           {
/* 238 */             i2 = j - i;
/* 239 */             if ((i2 < 3) || (k >= m)) {
/* 240 */               if ((i2 > 1) && (isMalformed3_2(i1, arrayOfByte[(i + 1)])))
/* 241 */                 return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 1);
/* 242 */               return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 3);
/*     */             }
/* 244 */             int i3 = arrayOfByte[(i + 1)];
/* 245 */             int i4 = arrayOfByte[(i + 2)];
/* 246 */             if (isMalformed3(i1, i3, i4))
/* 247 */               return malformed(paramByteBuffer, i, paramCharBuffer, k, 3);
/* 248 */             arrayOfChar[(k++)] = ((char)(i1 << 12 ^ i3 << 6 ^ i4 ^ 0xFFFE1F80));
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 255 */             i += 3;
/*     */           } else {
/* 257 */             return malformed(paramByteBuffer, i, paramCharBuffer, k, 1);
/*     */           }
/*     */         } }
/* 260 */       return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 0);
/*     */     }
/*     */     
/*     */ 
/*     */     private CoderResult decodeBufferLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/* 266 */       int i = paramByteBuffer.position();
/* 267 */       int j = paramByteBuffer.limit();
/* 268 */       while (i < j) {
/* 269 */         int k = paramByteBuffer.get();
/* 270 */         if (k >= 0)
/*     */         {
/* 272 */           if (paramCharBuffer.remaining() < 1)
/* 273 */             return xflow(paramByteBuffer, i, 1);
/* 274 */           paramCharBuffer.put((char)k);
/* 275 */           i++; } else { int m;
/* 276 */           if ((k >> 5 == -2) && ((k & 0x1E) != 0))
/*     */           {
/* 278 */             if ((j - i < 2) || (paramCharBuffer.remaining() < 1))
/* 279 */               return xflow(paramByteBuffer, i, 2);
/* 280 */             m = paramByteBuffer.get();
/* 281 */             if (isNotContinuation(m))
/* 282 */               return malformedForLength(paramByteBuffer, i, 1);
/* 283 */             paramCharBuffer.put((char)(k << 6 ^ m ^ 0xF80));
/*     */             
/*     */ 
/*     */ 
/* 287 */             i += 2;
/* 288 */           } else if (k >> 4 == -2)
/*     */           {
/* 290 */             m = j - i;
/* 291 */             if ((m < 3) || (paramCharBuffer.remaining() < 1)) {
/* 292 */               if ((m > 1) && (isMalformed3_2(k, paramByteBuffer.get())))
/* 293 */                 return malformedForLength(paramByteBuffer, i, 1);
/* 294 */               return xflow(paramByteBuffer, i, 3);
/*     */             }
/* 296 */             int n = paramByteBuffer.get();
/* 297 */             int i1 = paramByteBuffer.get();
/* 298 */             if (isMalformed3(k, n, i1))
/* 299 */               return malformed(paramByteBuffer, i, 3);
/* 300 */             paramCharBuffer.put((char)(k << 12 ^ n << 6 ^ i1 ^ 0xFFFE1F80));
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 307 */             i += 3;
/*     */           } else {
/* 309 */             return malformed(paramByteBuffer, i, 1);
/*     */           }
/*     */         } }
/* 312 */       return xflow(paramByteBuffer, i, 0);
/*     */     }
/*     */     
/*     */ 
/*     */     protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/* 318 */       if ((paramByteBuffer.hasArray()) && (paramCharBuffer.hasArray())) {
/* 319 */         return decodeArrayLoop(paramByteBuffer, paramCharBuffer);
/*     */       }
/* 321 */       return decodeBufferLoop(paramByteBuffer, paramCharBuffer);
/*     */     }
/*     */     
/*     */     private static ByteBuffer getByteBuffer(ByteBuffer paramByteBuffer, byte[] paramArrayOfByte, int paramInt)
/*     */     {
/* 326 */       if (paramByteBuffer == null)
/* 327 */         paramByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
/* 328 */       paramByteBuffer.position(paramInt);
/* 329 */       return paramByteBuffer;
/*     */     }
/*     */     
/*     */ 
/*     */     public int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar)
/*     */     {
/* 335 */       int i = paramInt1 + paramInt2;
/* 336 */       int j = 0;
/* 337 */       int k = Math.min(paramInt2, paramArrayOfChar.length);
/* 338 */       ByteBuffer localByteBuffer = null;
/*     */       
/*     */ 
/* 341 */       while ((j < k) && (paramArrayOfByte[paramInt1] >= 0)) {
/* 342 */         paramArrayOfChar[(j++)] = ((char)paramArrayOfByte[(paramInt1++)]);
/*     */       }
/* 344 */       while (paramInt1 < i) {
/* 345 */         int m = paramArrayOfByte[(paramInt1++)];
/* 346 */         if (m >= 0)
/*     */         {
/* 348 */           paramArrayOfChar[(j++)] = ((char)m); } else { int n;
/* 349 */           if ((m >> 5 == -2) && ((m & 0x1E) != 0))
/*     */           {
/* 351 */             if (paramInt1 < i) {
/* 352 */               n = paramArrayOfByte[(paramInt1++)];
/* 353 */               if (isNotContinuation(n)) {
/* 354 */                 if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 355 */                   return -1;
/* 356 */                 paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 357 */                 paramInt1--;
/*     */               } else {
/* 359 */                 paramArrayOfChar[(j++)] = ((char)(m << 6 ^ n ^ 0xF80));
/*     */               }
/*     */               
/*     */             }
/*     */             else
/*     */             {
/* 365 */               if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 366 */                 return -1;
/* 367 */               paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 368 */               return j;
/* 369 */             } } else if (m >> 4 == -2)
/*     */           {
/* 371 */             if (paramInt1 + 1 < i) {
/* 372 */               n = paramArrayOfByte[(paramInt1++)];
/* 373 */               int i1 = paramArrayOfByte[(paramInt1++)];
/* 374 */               if (isMalformed3(m, n, i1)) {
/* 375 */                 if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 376 */                   return -1;
/* 377 */                 paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 378 */                 paramInt1 -= 3;
/* 379 */                 localByteBuffer = getByteBuffer(localByteBuffer, paramArrayOfByte, paramInt1);
/* 380 */                 paramInt1 += malformedN(localByteBuffer, 3).length();
/*     */               } else {
/* 382 */                 paramArrayOfChar[(j++)] = ((char)(m << 12 ^ n << 6 ^ i1 ^ 0xFFFE1F80));
/*     */ 
/*     */               }
/*     */               
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 391 */               if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 392 */                 return -1;
/* 393 */               if ((paramInt1 < i) && (isMalformed3_2(m, paramArrayOfByte[paramInt1]))) {
/* 394 */                 paramArrayOfChar[(j++)] = replacement().charAt(0);
/*     */               }
/*     */               else
/*     */               {
/* 398 */                 paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 399 */                 return j;
/*     */               }
/* 401 */             } } else { if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 402 */               return -1;
/* 403 */             paramArrayOfChar[(j++)] = replacement().charAt(0);
/*     */           }
/*     */         } }
/* 406 */       return j;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Encoder extends CharsetEncoder implements ArrayEncoder {
/*     */     private Surrogate.Parser sgp;
/*     */     private char[] c2;
/*     */     
/* 414 */     private Encoder(Charset paramCharset) { super(1.1F, 3.0F); }
/*     */     
/*     */     public boolean canEncode(char paramChar)
/*     */     {
/* 418 */       return !Character.isSurrogate(paramChar);
/*     */     }
/*     */     
/*     */     public boolean isLegalReplacement(byte[] paramArrayOfByte) {
/* 422 */       return ((paramArrayOfByte.length == 1) && (paramArrayOfByte[0] >= 0)) || 
/* 423 */         (super.isLegalReplacement(paramArrayOfByte));
/*     */     }
/*     */     
/*     */     private static CoderResult overflow(CharBuffer paramCharBuffer, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2)
/*     */     {
/* 428 */       CESU_8.updatePositions(paramCharBuffer, paramInt1, paramByteBuffer, paramInt2);
/* 429 */       return CoderResult.OVERFLOW;
/*     */     }
/*     */     
/*     */     private static CoderResult overflow(CharBuffer paramCharBuffer, int paramInt) {
/* 433 */       paramCharBuffer.position(paramInt);
/* 434 */       return CoderResult.OVERFLOW;
/*     */     }
/*     */     
/*     */     private static void to3Bytes(byte[] paramArrayOfByte, int paramInt, char paramChar) {
/* 438 */       paramArrayOfByte[paramInt] = ((byte)(0xE0 | paramChar >> '\f'));
/* 439 */       paramArrayOfByte[(paramInt + 1)] = ((byte)(0x80 | paramChar >> '\006' & 0x3F));
/* 440 */       paramArrayOfByte[(paramInt + 2)] = ((byte)(0x80 | paramChar & 0x3F));
/*     */     }
/*     */     
/*     */     private static void to3Bytes(ByteBuffer paramByteBuffer, char paramChar) {
/* 444 */       paramByteBuffer.put((byte)(0xE0 | paramChar >> '\f'));
/* 445 */       paramByteBuffer.put((byte)(0x80 | paramChar >> '\006' & 0x3F));
/* 446 */       paramByteBuffer.put((byte)(0x80 | paramChar & 0x3F));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private CoderResult encodeArrayLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/* 454 */       char[] arrayOfChar = paramCharBuffer.array();
/* 455 */       int i = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/* 456 */       int j = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/*     */       
/* 458 */       byte[] arrayOfByte = paramByteBuffer.array();
/* 459 */       int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/* 460 */       int m = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/* 461 */       int n = k + Math.min(j - i, m - k);
/*     */       
/*     */ 
/* 464 */       while ((k < n) && (arrayOfChar[i] < ''))
/* 465 */         arrayOfByte[(k++)] = ((byte)arrayOfChar[(i++)]);
/* 466 */       while (i < j) {
/* 467 */         char c = arrayOfChar[i];
/* 468 */         if (c < '')
/*     */         {
/* 470 */           if (k >= m)
/* 471 */             return overflow(paramCharBuffer, i, paramByteBuffer, k);
/* 472 */           arrayOfByte[(k++)] = ((byte)c);
/* 473 */         } else if (c < 'ࠀ')
/*     */         {
/* 475 */           if (m - k < 2)
/* 476 */             return overflow(paramCharBuffer, i, paramByteBuffer, k);
/* 477 */           arrayOfByte[(k++)] = ((byte)(0xC0 | c >> '\006'));
/* 478 */           arrayOfByte[(k++)] = ((byte)(0x80 | c & 0x3F));
/* 479 */         } else if (Character.isSurrogate(c))
/*     */         {
/* 481 */           if (this.sgp == null)
/* 482 */             this.sgp = new Surrogate.Parser();
/* 483 */           int i1 = this.sgp.parse(c, arrayOfChar, i, j);
/* 484 */           if (i1 < 0) {
/* 485 */             CESU_8.updatePositions(paramCharBuffer, i, paramByteBuffer, k);
/* 486 */             return this.sgp.error();
/*     */           }
/* 488 */           if (m - k < 6)
/* 489 */             return overflow(paramCharBuffer, i, paramByteBuffer, k);
/* 490 */           to3Bytes(arrayOfByte, k, Character.highSurrogate(i1));
/* 491 */           k += 3;
/* 492 */           to3Bytes(arrayOfByte, k, Character.lowSurrogate(i1));
/* 493 */           k += 3;
/* 494 */           i++;
/*     */         }
/*     */         else {
/* 497 */           if (m - k < 3)
/* 498 */             return overflow(paramCharBuffer, i, paramByteBuffer, k);
/* 499 */           to3Bytes(arrayOfByte, k, c);
/* 500 */           k += 3;
/*     */         }
/* 502 */         i++;
/*     */       }
/* 504 */       CESU_8.updatePositions(paramCharBuffer, i, paramByteBuffer, k);
/* 505 */       return CoderResult.UNDERFLOW;
/*     */     }
/*     */     
/*     */ 
/*     */     private CoderResult encodeBufferLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/* 511 */       int i = paramCharBuffer.position();
/* 512 */       while (paramCharBuffer.hasRemaining()) {
/* 513 */         char c = paramCharBuffer.get();
/* 514 */         if (c < '')
/*     */         {
/* 516 */           if (!paramByteBuffer.hasRemaining())
/* 517 */             return overflow(paramCharBuffer, i);
/* 518 */           paramByteBuffer.put((byte)c);
/* 519 */         } else if (c < 'ࠀ')
/*     */         {
/* 521 */           if (paramByteBuffer.remaining() < 2)
/* 522 */             return overflow(paramCharBuffer, i);
/* 523 */           paramByteBuffer.put((byte)(0xC0 | c >> '\006'));
/* 524 */           paramByteBuffer.put((byte)(0x80 | c & 0x3F));
/* 525 */         } else if (Character.isSurrogate(c))
/*     */         {
/* 527 */           if (this.sgp == null)
/* 528 */             this.sgp = new Surrogate.Parser();
/* 529 */           int j = this.sgp.parse(c, paramCharBuffer);
/* 530 */           if (j < 0) {
/* 531 */             paramCharBuffer.position(i);
/* 532 */             return this.sgp.error();
/*     */           }
/* 534 */           if (paramByteBuffer.remaining() < 6)
/* 535 */             return overflow(paramCharBuffer, i);
/* 536 */           to3Bytes(paramByteBuffer, Character.highSurrogate(j));
/* 537 */           to3Bytes(paramByteBuffer, Character.lowSurrogate(j));
/* 538 */           i++;
/*     */         }
/*     */         else {
/* 541 */           if (paramByteBuffer.remaining() < 3)
/* 542 */             return overflow(paramCharBuffer, i);
/* 543 */           to3Bytes(paramByteBuffer, c);
/*     */         }
/* 545 */         i++;
/*     */       }
/* 547 */       paramCharBuffer.position(i);
/* 548 */       return CoderResult.UNDERFLOW;
/*     */     }
/*     */     
/*     */ 
/*     */     protected final CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/* 554 */       if ((paramCharBuffer.hasArray()) && (paramByteBuffer.hasArray())) {
/* 555 */         return encodeArrayLoop(paramCharBuffer, paramByteBuffer);
/*     */       }
/* 557 */       return encodeBufferLoop(paramCharBuffer, paramByteBuffer);
/*     */     }
/*     */     
/*     */ 
/*     */     public int encode(char[] paramArrayOfChar, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
/*     */     {
/* 563 */       int i = paramInt1 + paramInt2;
/* 564 */       int j = 0;
/* 565 */       int k = j + Math.min(paramInt2, paramArrayOfByte.length);
/*     */       
/*     */ 
/* 568 */       while ((j < k) && (paramArrayOfChar[paramInt1] < '')) {
/* 569 */         paramArrayOfByte[(j++)] = ((byte)paramArrayOfChar[(paramInt1++)]);
/*     */       }
/* 571 */       while (paramInt1 < i) {
/* 572 */         char c = paramArrayOfChar[(paramInt1++)];
/* 573 */         if (c < '')
/*     */         {
/* 575 */           paramArrayOfByte[(j++)] = ((byte)c);
/* 576 */         } else if (c < 'ࠀ')
/*     */         {
/* 578 */           paramArrayOfByte[(j++)] = ((byte)(0xC0 | c >> '\006'));
/* 579 */           paramArrayOfByte[(j++)] = ((byte)(0x80 | c & 0x3F));
/* 580 */         } else if (Character.isSurrogate(c)) {
/* 581 */           if (this.sgp == null)
/* 582 */             this.sgp = new Surrogate.Parser();
/* 583 */           int m = this.sgp.parse(c, paramArrayOfChar, paramInt1 - 1, i);
/* 584 */           if (m < 0) {
/* 585 */             if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 586 */               return -1;
/* 587 */             paramArrayOfByte[(j++)] = replacement()[0];
/*     */           } else {
/* 589 */             to3Bytes(paramArrayOfByte, j, Character.highSurrogate(m));
/* 590 */             j += 3;
/* 591 */             to3Bytes(paramArrayOfByte, j, Character.lowSurrogate(m));
/* 592 */             j += 3;
/* 593 */             paramInt1++;
/*     */           }
/*     */         }
/*     */         else {
/* 597 */           to3Bytes(paramArrayOfByte, j, c);
/* 598 */           j += 3;
/*     */         }
/*     */       }
/* 601 */       return j;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\CESU_8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
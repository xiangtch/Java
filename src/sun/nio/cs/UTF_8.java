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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class UTF_8
/*     */   extends Unicode
/*     */ {
/*     */   public UTF_8()
/*     */   {
/*  60 */     super("UTF-8", StandardCharsets.aliases_UTF_8);
/*     */   }
/*     */   
/*     */   public String historicalName() {
/*  64 */     return "UTF8";
/*     */   }
/*     */   
/*     */   public CharsetDecoder newDecoder() {
/*  68 */     return new Decoder(this, null);
/*     */   }
/*     */   
/*     */   public CharsetEncoder newEncoder() {
/*  72 */     return new Encoder(this, null);
/*     */   }
/*     */   
/*     */   private static final void updatePositions(Buffer paramBuffer1, int paramInt1, Buffer paramBuffer2, int paramInt2)
/*     */   {
/*  77 */     paramBuffer1.position(paramInt1 - paramBuffer1.arrayOffset());
/*  78 */     paramBuffer2.position(paramInt2 - paramBuffer2.arrayOffset());
/*     */   }
/*     */   
/*     */   private static class Decoder extends CharsetDecoder implements ArrayDecoder
/*     */   {
/*     */     private Decoder(Charset paramCharset) {
/*  84 */       super(1.0F, 1.0F);
/*     */     }
/*     */     
/*     */     private static boolean isNotContinuation(int paramInt) {
/*  88 */       return (paramInt & 0xC0) != 128;
/*     */     }
/*     */     
/*     */ 
/*     */     private static boolean isMalformed3(int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/*  94 */       return ((paramInt1 == -32) && ((paramInt2 & 0xE0) == 128)) || ((paramInt2 & 0xC0) != 128) || ((paramInt3 & 0xC0) != 128);
/*     */     }
/*     */     
/*     */ 
/*     */     private static boolean isMalformed3_2(int paramInt1, int paramInt2)
/*     */     {
/* 100 */       return ((paramInt1 == -32) && ((paramInt2 & 0xE0) == 128)) || ((paramInt2 & 0xC0) != 128);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static boolean isMalformed4(int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 110 */       return ((paramInt1 & 0xC0) != 128) || ((paramInt2 & 0xC0) != 128) || ((paramInt3 & 0xC0) != 128);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static boolean isMalformed4_2(int paramInt1, int paramInt2)
/*     */     {
/* 117 */       return ((paramInt1 == 240) && ((paramInt2 < 144) || (paramInt2 > 191))) || ((paramInt1 == 244) && ((paramInt2 & 0xF0) != 128)) || ((paramInt2 & 0xC0) != 128);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static boolean isMalformed4_3(int paramInt)
/*     */     {
/* 127 */       return (paramInt & 0xC0) != 128;
/*     */     }
/*     */     
/*     */     private static CoderResult lookupN(ByteBuffer paramByteBuffer, int paramInt)
/*     */     {
/* 132 */       for (int i = 1; i < paramInt; i++) {
/* 133 */         if (isNotContinuation(paramByteBuffer.get()))
/* 134 */           return CoderResult.malformedForLength(i);
/*     */       }
/* 136 */       return CoderResult.malformedForLength(paramInt); }
/*     */     
/*     */     private static CoderResult malformedN(ByteBuffer paramByteBuffer, int paramInt) { int i;
/*     */       int j;
/* 140 */       switch (paramInt) {
/*     */       case 1: 
/*     */       case 2: 
/* 143 */         return CoderResult.malformedForLength(1);
/*     */       case 3: 
/* 145 */         i = paramByteBuffer.get();
/* 146 */         j = paramByteBuffer.get();
/* 147 */         return CoderResult.malformedForLength(((i == -32) && ((j & 0xE0) == 128)) || 
/*     */         
/* 149 */           (isNotContinuation(j)) ? 1 : 2);
/*     */       case 4: 
/* 151 */         i = paramByteBuffer.get() & 0xFF;
/* 152 */         j = paramByteBuffer.get() & 0xFF;
/* 153 */         if ((i > 244) || ((i == 240) && ((j < 144) || (j > 191))) || ((i == 244) && ((j & 0xF0) != 128)) || 
/*     */         
/*     */ 
/* 156 */           (isNotContinuation(j)))
/* 157 */           return CoderResult.malformedForLength(1);
/* 158 */         if (isNotContinuation(paramByteBuffer.get()))
/* 159 */           return CoderResult.malformedForLength(2);
/* 160 */         return CoderResult.malformedForLength(3);
/*     */       }
/* 162 */       if (!$assertionsDisabled) throw new AssertionError();
/* 163 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private static CoderResult malformed(ByteBuffer paramByteBuffer, int paramInt1, CharBuffer paramCharBuffer, int paramInt2, int paramInt3)
/*     */     {
/* 171 */       paramByteBuffer.position(paramInt1 - paramByteBuffer.arrayOffset());
/* 172 */       CoderResult localCoderResult = malformedN(paramByteBuffer, paramInt3);
/* 173 */       UTF_8.updatePositions(paramByteBuffer, paramInt1, paramCharBuffer, paramInt2);
/* 174 */       return localCoderResult;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static CoderResult malformed(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
/*     */     {
/* 181 */       paramByteBuffer.position(paramInt1);
/* 182 */       CoderResult localCoderResult = malformedN(paramByteBuffer, paramInt2);
/* 183 */       paramByteBuffer.position(paramInt1);
/* 184 */       return localCoderResult;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static CoderResult malformedForLength(ByteBuffer paramByteBuffer, int paramInt1, CharBuffer paramCharBuffer, int paramInt2, int paramInt3)
/*     */     {
/* 193 */       UTF_8.updatePositions(paramByteBuffer, paramInt1, paramCharBuffer, paramInt2);
/* 194 */       return CoderResult.malformedForLength(paramInt3);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static CoderResult malformedForLength(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
/*     */     {
/* 201 */       paramByteBuffer.position(paramInt1);
/* 202 */       return CoderResult.malformedForLength(paramInt2);
/*     */     }
/*     */     
/*     */ 
/*     */     private static CoderResult xflow(Buffer paramBuffer1, int paramInt1, int paramInt2, Buffer paramBuffer2, int paramInt3, int paramInt4)
/*     */     {
/* 208 */       UTF_8.updatePositions(paramBuffer1, paramInt1, paramBuffer2, paramInt3);
/* 209 */       return (paramInt4 == 0) || (paramInt2 - paramInt1 < paramInt4) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
/*     */     }
/*     */     
/*     */     private static CoderResult xflow(Buffer paramBuffer, int paramInt1, int paramInt2)
/*     */     {
/* 214 */       paramBuffer.position(paramInt1);
/* 215 */       return (paramInt2 == 0) || (paramBuffer.remaining() < paramInt2) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private CoderResult decodeArrayLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/* 223 */       byte[] arrayOfByte = paramByteBuffer.array();
/* 224 */       int i = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/* 225 */       int j = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/*     */       
/* 227 */       char[] arrayOfChar = paramCharBuffer.array();
/* 228 */       int k = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/* 229 */       int m = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/* 230 */       int n = k + Math.min(j - i, m - k);
/*     */       
/*     */ 
/* 233 */       while ((k < n) && (arrayOfByte[i] >= 0))
/* 234 */         arrayOfChar[(k++)] = ((char)arrayOfByte[(i++)]);
/* 235 */       while (i < j) {
/* 236 */         int i1 = arrayOfByte[i];
/* 237 */         if (i1 >= 0)
/*     */         {
/* 239 */           if (k >= m)
/* 240 */             return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 1);
/* 241 */           arrayOfChar[(k++)] = ((char)i1);
/* 242 */           i++; } else { int i2;
/* 243 */           if ((i1 >> 5 == -2) && ((i1 & 0x1E) != 0))
/*     */           {
/*     */ 
/* 246 */             if ((j - i < 2) || (k >= m))
/* 247 */               return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 2);
/* 248 */             i2 = arrayOfByte[(i + 1)];
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 255 */             if (isNotContinuation(i2))
/* 256 */               return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 1);
/* 257 */             arrayOfChar[(k++)] = ((char)(i1 << 6 ^ i2 ^ 0xF80));
/*     */             
/*     */ 
/*     */ 
/* 261 */             i += 2; } else { int i3;
/* 262 */             int i4; if (i1 >> 4 == -2)
/*     */             {
/* 264 */               i2 = j - i;
/* 265 */               if ((i2 < 3) || (k >= m)) {
/* 266 */                 if ((i2 > 1) && (isMalformed3_2(i1, arrayOfByte[(i + 1)])))
/* 267 */                   return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 1);
/* 268 */                 return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 3);
/*     */               }
/* 270 */               i3 = arrayOfByte[(i + 1)];
/* 271 */               i4 = arrayOfByte[(i + 2)];
/* 272 */               if (isMalformed3(i1, i3, i4))
/* 273 */                 return malformed(paramByteBuffer, i, paramCharBuffer, k, 3);
/* 274 */               char c = (char)(i1 << 12 ^ i3 << 6 ^ i4 ^ 0xFFFE1F80);
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 281 */               if (Character.isSurrogate(c))
/* 282 */                 return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 3);
/* 283 */               arrayOfChar[(k++)] = c;
/* 284 */               i += 3;
/* 285 */             } else if (i1 >> 3 == -2)
/*     */             {
/* 287 */               i2 = j - i;
/* 288 */               if ((i2 < 4) || (m - k < 2)) {
/* 289 */                 i1 &= 0xFF;
/* 290 */                 if ((i1 > 244) || ((i2 > 1) && 
/* 291 */                   (isMalformed4_2(i1, arrayOfByte[(i + 1)] & 0xFF))))
/* 292 */                   return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 1);
/* 293 */                 if ((i2 > 2) && (isMalformed4_3(arrayOfByte[(i + 2)])))
/* 294 */                   return malformedForLength(paramByteBuffer, i, paramCharBuffer, k, 2);
/* 295 */                 return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 4);
/*     */               }
/* 297 */               i3 = arrayOfByte[(i + 1)];
/* 298 */               i4 = arrayOfByte[(i + 2)];
/* 299 */               int i5 = arrayOfByte[(i + 3)];
/* 300 */               int i6 = i1 << 18 ^ i3 << 12 ^ i4 << 6 ^ i5 ^ 0x381F80;
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 308 */               if ((isMalformed4(i3, i4, i5)) || 
/*     */               
/* 310 */                 (!Character.isSupplementaryCodePoint(i6))) {
/* 311 */                 return malformed(paramByteBuffer, i, paramCharBuffer, k, 4);
/*     */               }
/* 313 */               arrayOfChar[(k++)] = Character.highSurrogate(i6);
/* 314 */               arrayOfChar[(k++)] = Character.lowSurrogate(i6);
/* 315 */               i += 4;
/*     */             } else {
/* 317 */               return malformed(paramByteBuffer, i, paramCharBuffer, k, 1);
/*     */             } } } }
/* 319 */       return xflow(paramByteBuffer, i, j, paramCharBuffer, k, 0);
/*     */     }
/*     */     
/*     */ 
/*     */     private CoderResult decodeBufferLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/* 325 */       int i = paramByteBuffer.position();
/* 326 */       int j = paramByteBuffer.limit();
/* 327 */       while (i < j) {
/* 328 */         int k = paramByteBuffer.get();
/* 329 */         if (k >= 0)
/*     */         {
/* 331 */           if (paramCharBuffer.remaining() < 1)
/* 332 */             return xflow(paramByteBuffer, i, 1);
/* 333 */           paramCharBuffer.put((char)k);
/* 334 */           i++; } else { int m;
/* 335 */           if ((k >> 5 == -2) && ((k & 0x1E) != 0))
/*     */           {
/* 337 */             if ((j - i < 2) || (paramCharBuffer.remaining() < 1))
/* 338 */               return xflow(paramByteBuffer, i, 2);
/* 339 */             m = paramByteBuffer.get();
/* 340 */             if (isNotContinuation(m))
/* 341 */               return malformedForLength(paramByteBuffer, i, 1);
/* 342 */             paramCharBuffer.put((char)(k << 6 ^ m ^ 0xF80));
/*     */             
/*     */ 
/*     */ 
/* 346 */             i += 2; } else { int n;
/* 347 */             int i1; if (k >> 4 == -2)
/*     */             {
/* 349 */               m = j - i;
/* 350 */               if ((m < 3) || (paramCharBuffer.remaining() < 1)) {
/* 351 */                 if ((m > 1) && (isMalformed3_2(k, paramByteBuffer.get())))
/* 352 */                   return malformedForLength(paramByteBuffer, i, 1);
/* 353 */                 return xflow(paramByteBuffer, i, 3);
/*     */               }
/* 355 */               n = paramByteBuffer.get();
/* 356 */               i1 = paramByteBuffer.get();
/* 357 */               if (isMalformed3(k, n, i1))
/* 358 */                 return malformed(paramByteBuffer, i, 3);
/* 359 */               char c = (char)(k << 12 ^ n << 6 ^ i1 ^ 0xFFFE1F80);
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 366 */               if (Character.isSurrogate(c))
/* 367 */                 return malformedForLength(paramByteBuffer, i, 3);
/* 368 */               paramCharBuffer.put(c);
/* 369 */               i += 3;
/* 370 */             } else if (k >> 3 == -2)
/*     */             {
/* 372 */               m = j - i;
/* 373 */               if ((m < 4) || (paramCharBuffer.remaining() < 2)) {
/* 374 */                 k &= 0xFF;
/* 375 */                 if ((k > 244) || ((m > 1) && 
/* 376 */                   (isMalformed4_2(k, paramByteBuffer.get() & 0xFF))))
/* 377 */                   return malformedForLength(paramByteBuffer, i, 1);
/* 378 */                 if ((m > 2) && (isMalformed4_3(paramByteBuffer.get())))
/* 379 */                   return malformedForLength(paramByteBuffer, i, 2);
/* 380 */                 return xflow(paramByteBuffer, i, 4);
/*     */               }
/* 382 */               n = paramByteBuffer.get();
/* 383 */               i1 = paramByteBuffer.get();
/* 384 */               int i2 = paramByteBuffer.get();
/* 385 */               int i3 = k << 18 ^ n << 12 ^ i1 << 6 ^ i2 ^ 0x381F80;
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 393 */               if ((isMalformed4(n, i1, i2)) || 
/*     */               
/* 395 */                 (!Character.isSupplementaryCodePoint(i3))) {
/* 396 */                 return malformed(paramByteBuffer, i, 4);
/*     */               }
/* 398 */               paramCharBuffer.put(Character.highSurrogate(i3));
/* 399 */               paramCharBuffer.put(Character.lowSurrogate(i3));
/* 400 */               i += 4;
/*     */             } else {
/* 402 */               return malformed(paramByteBuffer, i, 1);
/*     */             }
/*     */           } } }
/* 405 */       return xflow(paramByteBuffer, i, 0);
/*     */     }
/*     */     
/*     */ 
/*     */     protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/* 411 */       if ((paramByteBuffer.hasArray()) && (paramCharBuffer.hasArray())) {
/* 412 */         return decodeArrayLoop(paramByteBuffer, paramCharBuffer);
/*     */       }
/* 414 */       return decodeBufferLoop(paramByteBuffer, paramCharBuffer);
/*     */     }
/*     */     
/*     */     private static ByteBuffer getByteBuffer(ByteBuffer paramByteBuffer, byte[] paramArrayOfByte, int paramInt)
/*     */     {
/* 419 */       if (paramByteBuffer == null)
/* 420 */         paramByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
/* 421 */       paramByteBuffer.position(paramInt);
/* 422 */       return paramByteBuffer;
/*     */     }
/*     */     
/*     */ 
/*     */     public int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar)
/*     */     {
/* 428 */       int i = paramInt1 + paramInt2;
/* 429 */       int j = 0;
/* 430 */       int k = Math.min(paramInt2, paramArrayOfChar.length);
/* 431 */       ByteBuffer localByteBuffer = null;
/*     */       
/*     */ 
/* 434 */       while ((j < k) && (paramArrayOfByte[paramInt1] >= 0)) {
/* 435 */         paramArrayOfChar[(j++)] = ((char)paramArrayOfByte[(paramInt1++)]);
/*     */       }
/* 437 */       while (paramInt1 < i) {
/* 438 */         int m = paramArrayOfByte[(paramInt1++)];
/* 439 */         if (m >= 0)
/*     */         {
/* 441 */           paramArrayOfChar[(j++)] = ((char)m); } else { int n;
/* 442 */           if ((m >> 5 == -2) && ((m & 0x1E) != 0))
/*     */           {
/* 444 */             if (paramInt1 < i) {
/* 445 */               n = paramArrayOfByte[(paramInt1++)];
/* 446 */               if (isNotContinuation(n)) {
/* 447 */                 if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 448 */                   return -1;
/* 449 */                 paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 450 */                 paramInt1--;
/*     */               } else {
/* 452 */                 paramArrayOfChar[(j++)] = ((char)(m << 6 ^ n ^ 0xF80));
/*     */               }
/*     */               
/*     */             }
/*     */             else
/*     */             {
/* 458 */               if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 459 */                 return -1;
/* 460 */               paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 461 */               return j; } } else { int i1;
/* 462 */             if (m >> 4 == -2)
/*     */             {
/* 464 */               if (paramInt1 + 1 < i) {
/* 465 */                 n = paramArrayOfByte[(paramInt1++)];
/* 466 */                 i1 = paramArrayOfByte[(paramInt1++)];
/* 467 */                 if (isMalformed3(m, n, i1)) {
/* 468 */                   if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 469 */                     return -1;
/* 470 */                   paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 471 */                   paramInt1 -= 3;
/* 472 */                   localByteBuffer = getByteBuffer(localByteBuffer, paramArrayOfByte, paramInt1);
/* 473 */                   paramInt1 += malformedN(localByteBuffer, 3).length();
/*     */                 } else {
/* 475 */                   char c = (char)(m << 12 ^ n << 6 ^ i1 ^ 0xFFFE1F80);
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 481 */                   if (Character.isSurrogate(c)) {
/* 482 */                     if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 483 */                       return -1;
/* 484 */                     paramArrayOfChar[(j++)] = replacement().charAt(0);
/*     */                   } else {
/* 486 */                     paramArrayOfChar[(j++)] = c;
/*     */                   }
/*     */                 }
/*     */               }
/*     */               else {
/* 491 */                 if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 492 */                   return -1;
/* 493 */                 if ((paramInt1 < i) && (isMalformed3_2(m, paramArrayOfByte[paramInt1]))) {
/* 494 */                   paramArrayOfChar[(j++)] = replacement().charAt(0);
/*     */                 }
/*     */                 else
/*     */                 {
/* 498 */                   paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 499 */                   return j;
/* 500 */                 } } } else if (m >> 3 == -2)
/*     */             {
/* 502 */               if (paramInt1 + 2 < i) {
/* 503 */                 n = paramArrayOfByte[(paramInt1++)];
/* 504 */                 i1 = paramArrayOfByte[(paramInt1++)];
/* 505 */                 int i2 = paramArrayOfByte[(paramInt1++)];
/* 506 */                 int i3 = m << 18 ^ n << 12 ^ i1 << 6 ^ i2 ^ 0x381F80;
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 514 */                 if ((isMalformed4(n, i1, i2)) || 
/*     */                 
/* 516 */                   (!Character.isSupplementaryCodePoint(i3))) {
/* 517 */                   if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 518 */                     return -1;
/* 519 */                   paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 520 */                   paramInt1 -= 4;
/* 521 */                   localByteBuffer = getByteBuffer(localByteBuffer, paramArrayOfByte, paramInt1);
/* 522 */                   paramInt1 += malformedN(localByteBuffer, 4).length();
/*     */                 } else {
/* 524 */                   paramArrayOfChar[(j++)] = Character.highSurrogate(i3);
/* 525 */                   paramArrayOfChar[(j++)] = Character.lowSurrogate(i3);
/*     */                 }
/*     */               }
/*     */               else {
/* 529 */                 if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 530 */                   return -1;
/* 531 */                 m &= 0xFF;
/* 532 */                 if ((m > 244) || ((paramInt1 < i) && 
/* 533 */                   (isMalformed4_2(m, paramArrayOfByte[paramInt1] & 0xFF)))) {
/* 534 */                   paramArrayOfChar[(j++)] = replacement().charAt(0);
/*     */                 }
/*     */                 else {
/* 537 */                   paramInt1++;
/* 538 */                   if ((paramInt1 < i) && (isMalformed4_3(paramArrayOfByte[paramInt1]))) {
/* 539 */                     paramArrayOfChar[(j++)] = replacement().charAt(0);
/*     */                   }
/*     */                   else {
/* 542 */                     paramArrayOfChar[(j++)] = replacement().charAt(0);
/* 543 */                     return j;
/*     */                   }
/* 545 */                 } } } else { if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 546 */                 return -1;
/* 547 */               paramArrayOfChar[(j++)] = replacement().charAt(0);
/*     */             }
/*     */           } } }
/* 550 */       return j;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class Encoder extends CharsetEncoder implements ArrayEncoder {
/*     */     private Surrogate.Parser sgp;
/*     */     
/*     */     private Encoder(Charset paramCharset) {
/* 558 */       super(1.1F, 3.0F);
/*     */     }
/*     */     
/*     */     public boolean canEncode(char paramChar) {
/* 562 */       return !Character.isSurrogate(paramChar);
/*     */     }
/*     */     
/*     */     public boolean isLegalReplacement(byte[] paramArrayOfByte) {
/* 566 */       return ((paramArrayOfByte.length == 1) && (paramArrayOfByte[0] >= 0)) || 
/* 567 */         (super.isLegalReplacement(paramArrayOfByte));
/*     */     }
/*     */     
/*     */     private static CoderResult overflow(CharBuffer paramCharBuffer, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2)
/*     */     {
/* 572 */       UTF_8.updatePositions(paramCharBuffer, paramInt1, paramByteBuffer, paramInt2);
/* 573 */       return CoderResult.OVERFLOW;
/*     */     }
/*     */     
/*     */     private static CoderResult overflow(CharBuffer paramCharBuffer, int paramInt) {
/* 577 */       paramCharBuffer.position(paramInt);
/* 578 */       return CoderResult.OVERFLOW;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private CoderResult encodeArrayLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/* 585 */       char[] arrayOfChar = paramCharBuffer.array();
/* 586 */       int i = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/* 587 */       int j = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/*     */       
/* 589 */       byte[] arrayOfByte = paramByteBuffer.array();
/* 590 */       int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/* 591 */       int m = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/* 592 */       int n = k + Math.min(j - i, m - k);
/*     */       
/*     */ 
/* 595 */       while ((k < n) && (arrayOfChar[i] < ''))
/* 596 */         arrayOfByte[(k++)] = ((byte)arrayOfChar[(i++)]);
/* 597 */       while (i < j) {
/* 598 */         char c = arrayOfChar[i];
/* 599 */         if (c < '')
/*     */         {
/* 601 */           if (k >= m)
/* 602 */             return overflow(paramCharBuffer, i, paramByteBuffer, k);
/* 603 */           arrayOfByte[(k++)] = ((byte)c);
/* 604 */         } else if (c < 'ࠀ')
/*     */         {
/* 606 */           if (m - k < 2)
/* 607 */             return overflow(paramCharBuffer, i, paramByteBuffer, k);
/* 608 */           arrayOfByte[(k++)] = ((byte)(0xC0 | c >> '\006'));
/* 609 */           arrayOfByte[(k++)] = ((byte)(0x80 | c & 0x3F));
/* 610 */         } else if (Character.isSurrogate(c))
/*     */         {
/* 612 */           if (this.sgp == null)
/* 613 */             this.sgp = new Surrogate.Parser();
/* 614 */           int i1 = this.sgp.parse(c, arrayOfChar, i, j);
/* 615 */           if (i1 < 0) {
/* 616 */             UTF_8.updatePositions(paramCharBuffer, i, paramByteBuffer, k);
/* 617 */             return this.sgp.error();
/*     */           }
/* 619 */           if (m - k < 4)
/* 620 */             return overflow(paramCharBuffer, i, paramByteBuffer, k);
/* 621 */           arrayOfByte[(k++)] = ((byte)(0xF0 | i1 >> 18));
/* 622 */           arrayOfByte[(k++)] = ((byte)(0x80 | i1 >> 12 & 0x3F));
/* 623 */           arrayOfByte[(k++)] = ((byte)(0x80 | i1 >> 6 & 0x3F));
/* 624 */           arrayOfByte[(k++)] = ((byte)(0x80 | i1 & 0x3F));
/* 625 */           i++;
/*     */         }
/*     */         else {
/* 628 */           if (m - k < 3)
/* 629 */             return overflow(paramCharBuffer, i, paramByteBuffer, k);
/* 630 */           arrayOfByte[(k++)] = ((byte)(0xE0 | c >> '\f'));
/* 631 */           arrayOfByte[(k++)] = ((byte)(0x80 | c >> '\006' & 0x3F));
/* 632 */           arrayOfByte[(k++)] = ((byte)(0x80 | c & 0x3F));
/*     */         }
/* 634 */         i++;
/*     */       }
/* 636 */       UTF_8.updatePositions(paramCharBuffer, i, paramByteBuffer, k);
/* 637 */       return CoderResult.UNDERFLOW;
/*     */     }
/*     */     
/*     */ 
/*     */     private CoderResult encodeBufferLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/* 643 */       int i = paramCharBuffer.position();
/* 644 */       while (paramCharBuffer.hasRemaining()) {
/* 645 */         char c = paramCharBuffer.get();
/* 646 */         if (c < '')
/*     */         {
/* 648 */           if (!paramByteBuffer.hasRemaining())
/* 649 */             return overflow(paramCharBuffer, i);
/* 650 */           paramByteBuffer.put((byte)c);
/* 651 */         } else if (c < 'ࠀ')
/*     */         {
/* 653 */           if (paramByteBuffer.remaining() < 2)
/* 654 */             return overflow(paramCharBuffer, i);
/* 655 */           paramByteBuffer.put((byte)(0xC0 | c >> '\006'));
/* 656 */           paramByteBuffer.put((byte)(0x80 | c & 0x3F));
/* 657 */         } else if (Character.isSurrogate(c))
/*     */         {
/* 659 */           if (this.sgp == null)
/* 660 */             this.sgp = new Surrogate.Parser();
/* 661 */           int j = this.sgp.parse(c, paramCharBuffer);
/* 662 */           if (j < 0) {
/* 663 */             paramCharBuffer.position(i);
/* 664 */             return this.sgp.error();
/*     */           }
/* 666 */           if (paramByteBuffer.remaining() < 4)
/* 667 */             return overflow(paramCharBuffer, i);
/* 668 */           paramByteBuffer.put((byte)(0xF0 | j >> 18));
/* 669 */           paramByteBuffer.put((byte)(0x80 | j >> 12 & 0x3F));
/* 670 */           paramByteBuffer.put((byte)(0x80 | j >> 6 & 0x3F));
/* 671 */           paramByteBuffer.put((byte)(0x80 | j & 0x3F));
/* 672 */           i++;
/*     */         }
/*     */         else {
/* 675 */           if (paramByteBuffer.remaining() < 3)
/* 676 */             return overflow(paramCharBuffer, i);
/* 677 */           paramByteBuffer.put((byte)(0xE0 | c >> '\f'));
/* 678 */           paramByteBuffer.put((byte)(0x80 | c >> '\006' & 0x3F));
/* 679 */           paramByteBuffer.put((byte)(0x80 | c & 0x3F));
/*     */         }
/* 681 */         i++;
/*     */       }
/* 683 */       paramCharBuffer.position(i);
/* 684 */       return CoderResult.UNDERFLOW;
/*     */     }
/*     */     
/*     */ 
/*     */     protected final CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer)
/*     */     {
/* 690 */       if ((paramCharBuffer.hasArray()) && (paramByteBuffer.hasArray())) {
/* 691 */         return encodeArrayLoop(paramCharBuffer, paramByteBuffer);
/*     */       }
/* 693 */       return encodeBufferLoop(paramCharBuffer, paramByteBuffer);
/*     */     }
/*     */     
/* 696 */     private byte repl = 63;
/*     */     
/* 698 */     protected void implReplaceWith(byte[] paramArrayOfByte) { this.repl = paramArrayOfByte[0]; }
/*     */     
/*     */ 
/*     */ 
/*     */     public int encode(char[] paramArrayOfChar, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
/*     */     {
/* 704 */       int i = paramInt1 + paramInt2;
/* 705 */       int j = 0;
/* 706 */       int k = j + Math.min(paramInt2, paramArrayOfByte.length);
/*     */       
/*     */ 
/* 709 */       while ((j < k) && (paramArrayOfChar[paramInt1] < '')) {
/* 710 */         paramArrayOfByte[(j++)] = ((byte)paramArrayOfChar[(paramInt1++)]);
/*     */       }
/* 712 */       while (paramInt1 < i) {
/* 713 */         char c = paramArrayOfChar[(paramInt1++)];
/* 714 */         if (c < '')
/*     */         {
/* 716 */           paramArrayOfByte[(j++)] = ((byte)c);
/* 717 */         } else if (c < 'ࠀ')
/*     */         {
/* 719 */           paramArrayOfByte[(j++)] = ((byte)(0xC0 | c >> '\006'));
/* 720 */           paramArrayOfByte[(j++)] = ((byte)(0x80 | c & 0x3F));
/* 721 */         } else if (Character.isSurrogate(c)) {
/* 722 */           if (this.sgp == null)
/* 723 */             this.sgp = new Surrogate.Parser();
/* 724 */           int m = this.sgp.parse(c, paramArrayOfChar, paramInt1 - 1, i);
/* 725 */           if (m < 0) {
/* 726 */             if (malformedInputAction() != CodingErrorAction.REPLACE)
/* 727 */               return -1;
/* 728 */             paramArrayOfByte[(j++)] = this.repl;
/*     */           } else {
/* 730 */             paramArrayOfByte[(j++)] = ((byte)(0xF0 | m >> 18));
/* 731 */             paramArrayOfByte[(j++)] = ((byte)(0x80 | m >> 12 & 0x3F));
/* 732 */             paramArrayOfByte[(j++)] = ((byte)(0x80 | m >> 6 & 0x3F));
/* 733 */             paramArrayOfByte[(j++)] = ((byte)(0x80 | m & 0x3F));
/* 734 */             paramInt1++;
/*     */           }
/*     */         }
/*     */         else {
/* 738 */           paramArrayOfByte[(j++)] = ((byte)(0xE0 | c >> '\f'));
/* 739 */           paramArrayOfByte[(j++)] = ((byte)(0x80 | c >> '\006' & 0x3F));
/* 740 */           paramArrayOfByte[(j++)] = ((byte)(0x80 | c & 0x3F));
/*     */         }
/*     */       }
/* 743 */       return j;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\UTF_8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
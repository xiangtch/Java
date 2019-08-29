/*      */ package sun.misc;
/*      */ 
/*      */ import java.math.BigInteger;
/*      */ import java.util.Arrays;
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
/*      */ public class FDBigInteger
/*      */ {
/*      */   static final int[] SMALL_5_POW;
/*      */   static final long[] LONG_5_POW;
/*      */   private static final int MAX_FIVE_POW = 340;
/*      */   private static final FDBigInteger[] POW_5_CACHE;
/*      */   public static final FDBigInteger ZERO;
/*      */   private static final long LONG_MASK = 4294967295L;
/*      */   private int[] data;
/*      */   private int offset;
/*      */   private int nWords;
/*      */   
/*      */   static
/*      */   {
/*   67 */     SMALL_5_POW = new int[] { 1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625, 1220703125 };
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
/*   84 */     LONG_5_POW = new long[] { 1L, 5L, 25L, 125L, 625L, 3125L, 15625L, 78125L, 390625L, 1953125L, 9765625L, 48828125L, 244140625L, 1220703125L, 6103515625L, 30517578125L, 152587890625L, 762939453125L, 3814697265625L, 19073486328125L, 95367431640625L, 476837158203125L, 2384185791015625L, 11920928955078125L, 59604644775390625L, 298023223876953125L, 1490116119384765625L };
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
/*  122 */     POW_5_CACHE = new FDBigInteger['Ŕ'];
/*  123 */     int i = 0;
/*  124 */     while (i < SMALL_5_POW.length) {
/*  125 */       localFDBigInteger = new FDBigInteger(new int[] { SMALL_5_POW[i] }, 0);
/*  126 */       localFDBigInteger.makeImmutable();
/*  127 */       POW_5_CACHE[i] = localFDBigInteger;
/*  128 */       i++;
/*      */     }
/*  130 */     FDBigInteger localFDBigInteger = POW_5_CACHE[(i - 1)];
/*  131 */     while (i < 340) {
/*  132 */       POW_5_CACHE[i] = (localFDBigInteger = localFDBigInteger.mult(5));
/*  133 */       localFDBigInteger.makeImmutable();
/*  134 */       i++;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  139 */     ZERO = new FDBigInteger(new int[0], 0);
/*      */     
/*      */ 
/*      */ 
/*  143 */     ZERO.makeImmutable();
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
/*  157 */   private boolean isImmutable = false;
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
/*      */   private FDBigInteger(int[] paramArrayOfInt, int paramInt)
/*      */   {
/*  186 */     this.data = paramArrayOfInt;
/*  187 */     this.offset = paramInt;
/*  188 */     this.nWords = paramArrayOfInt.length;
/*  189 */     trimLeadingZeros();
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
/*      */ 
/*      */   public FDBigInteger(long paramLong, char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*      */   {
/*  208 */     int i = Math.max((paramInt2 + 8) / 9, 2);
/*  209 */     this.data = new int[i];
/*  210 */     this.data[0] = ((int)paramLong);
/*  211 */     this.data[1] = ((int)(paramLong >>> 32));
/*  212 */     this.offset = 0;
/*  213 */     this.nWords = 2;
/*  214 */     int j = paramInt1;
/*  215 */     int k = paramInt2 - 5;
/*      */     
/*  217 */     while (j < k) {
/*  218 */       n = j + 5;
/*  219 */       m = paramArrayOfChar[(j++)] - '0';
/*  220 */       while (j < n) {
/*  221 */         m = 10 * m + paramArrayOfChar[(j++)] - 48;
/*      */       }
/*  223 */       multAddMe(100000, m);
/*      */     }
/*  225 */     int n = 1;
/*  226 */     int m = 0;
/*  227 */     while (j < paramInt2) {
/*  228 */       m = 10 * m + paramArrayOfChar[(j++)] - 48;
/*  229 */       n *= 10;
/*      */     }
/*  231 */     if (n != 1) {
/*  232 */       multAddMe(n, m);
/*      */     }
/*  234 */     trimLeadingZeros();
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
/*      */   public static FDBigInteger valueOfPow52(int paramInt1, int paramInt2)
/*      */   {
/*  251 */     if (paramInt1 != 0) {
/*  252 */       if (paramInt2 == 0)
/*  253 */         return big5pow(paramInt1);
/*  254 */       if (paramInt1 < SMALL_5_POW.length) {
/*  255 */         int i = SMALL_5_POW[paramInt1];
/*  256 */         int j = paramInt2 >> 5;
/*  257 */         int k = paramInt2 & 0x1F;
/*  258 */         if (k == 0) {
/*  259 */           return new FDBigInteger(new int[] { i }, j);
/*      */         }
/*  261 */         return new FDBigInteger(new int[] { i << k, i >>> 32 - k }, j);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  267 */       return big5pow(paramInt1).leftShift(paramInt2);
/*      */     }
/*      */     
/*  270 */     return valueOfPow2(paramInt2);
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
/*      */ 
/*      */   public static FDBigInteger valueOfMulPow52(long paramLong, int paramInt1, int paramInt2)
/*      */   {
/*  289 */     assert (paramInt1 >= 0) : paramInt1;
/*  290 */     assert (paramInt2 >= 0) : paramInt2;
/*  291 */     int i = (int)paramLong;
/*  292 */     int j = (int)(paramLong >>> 32);
/*  293 */     int k = paramInt2 >> 5;
/*  294 */     int m = paramInt2 & 0x1F;
/*  295 */     if (paramInt1 != 0) {
/*  296 */       if (paramInt1 < SMALL_5_POW.length) {
/*  297 */         long l1 = SMALL_5_POW[paramInt1] & 0xFFFFFFFF;
/*  298 */         long l2 = (i & 0xFFFFFFFF) * l1;
/*  299 */         i = (int)l2;
/*  300 */         l2 >>>= 32;
/*  301 */         l2 = (j & 0xFFFFFFFF) * l1 + l2;
/*  302 */         j = (int)l2;
/*  303 */         int n = (int)(l2 >>> 32);
/*  304 */         if (m == 0) {
/*  305 */           return new FDBigInteger(new int[] { i, j, n }, k);
/*      */         }
/*  307 */         return new FDBigInteger(new int[] { i << m, j << m | i >>> 32 - m, n << m | j >>> 32 - m, n >>> 32 - m }, k);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  315 */       FDBigInteger localFDBigInteger = big5pow(paramInt1);
/*      */       int[] arrayOfInt;
/*  317 */       if (j == 0) {
/*  318 */         arrayOfInt = new int[localFDBigInteger.nWords + 1 + (paramInt2 != 0 ? 1 : 0)];
/*  319 */         mult(localFDBigInteger.data, localFDBigInteger.nWords, i, arrayOfInt);
/*      */       } else {
/*  321 */         arrayOfInt = new int[localFDBigInteger.nWords + 2 + (paramInt2 != 0 ? 1 : 0)];
/*  322 */         mult(localFDBigInteger.data, localFDBigInteger.nWords, i, j, arrayOfInt);
/*      */       }
/*  324 */       return new FDBigInteger(arrayOfInt, localFDBigInteger.offset).leftShift(paramInt2);
/*      */     }
/*  326 */     if (paramInt2 != 0) {
/*  327 */       if (m == 0) {
/*  328 */         return new FDBigInteger(new int[] { i, j }, k);
/*      */       }
/*  330 */       return new FDBigInteger(new int[] { i << m, j << m | i >>> 32 - m, j >>> 32 - m }, k);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  337 */     return new FDBigInteger(new int[] { i, j }, 0);
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
/*      */   private static FDBigInteger valueOfPow2(int paramInt)
/*      */   {
/*  353 */     int i = paramInt >> 5;
/*  354 */     int j = paramInt & 0x1F;
/*  355 */     return new FDBigInteger(new int[] { 1 << j }, i);
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
/*      */   private void trimLeadingZeros()
/*      */   {
/*  370 */     int i = this.nWords;
/*  371 */     if ((i > 0) && (this.data[(--i)] == 0))
/*      */     {
/*  373 */       while ((i > 0) && (this.data[(i - 1)] == 0)) {
/*  374 */         i--;
/*      */       }
/*  376 */       this.nWords = i;
/*  377 */       if (i == 0) {
/*  378 */         this.offset = 0;
/*      */       }
/*      */     }
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
/*      */   public int getNormalizationBias()
/*      */   {
/*  396 */     if (this.nWords == 0) {
/*  397 */       throw new IllegalArgumentException("Zero value cannot be normalized");
/*      */     }
/*  399 */     int i = Integer.numberOfLeadingZeros(this.data[(this.nWords - 1)]);
/*  400 */     return i < 4 ? 28 + i : i - 4;
/*      */   }
/*      */   
/*      */   private static void leftShift(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  421 */     for (; 
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
/*  421 */         paramInt1 > 0; paramInt1--) {
/*  422 */       i = paramInt4 << paramInt2;
/*  423 */       paramInt4 = paramArrayOfInt1[(paramInt1 - 1)];
/*  424 */       i |= paramInt4 >>> paramInt3;
/*  425 */       paramArrayOfInt2[paramInt1] = i;
/*      */     }
/*  427 */     int i = paramInt4 << paramInt2;
/*  428 */     paramArrayOfInt2[0] = i;
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
/*      */   public FDBigInteger leftShift(int paramInt)
/*      */   {
/*  458 */     if ((paramInt == 0) || (this.nWords == 0)) {
/*  459 */       return this;
/*      */     }
/*  461 */     int i = paramInt >> 5;
/*  462 */     int j = paramInt & 0x1F;
/*  463 */     int k; int m; int n; int i1; int[] arrayOfInt1; if (this.isImmutable) {
/*  464 */       if (j == 0) {
/*  465 */         return new FDBigInteger(Arrays.copyOf(this.data, this.nWords), this.offset + i);
/*      */       }
/*  467 */       k = 32 - j;
/*  468 */       m = this.nWords - 1;
/*  469 */       n = this.data[m];
/*  470 */       i1 = n >>> k;
/*      */       
/*  472 */       if (i1 != 0) {
/*  473 */         arrayOfInt1 = new int[this.nWords + 1];
/*  474 */         arrayOfInt1[this.nWords] = i1;
/*      */       } else {
/*  476 */         arrayOfInt1 = new int[this.nWords];
/*      */       }
/*  478 */       leftShift(this.data, m, arrayOfInt1, j, k, n);
/*  479 */       return new FDBigInteger(arrayOfInt1, this.offset + i);
/*      */     }
/*      */     
/*  482 */     if (j != 0) {
/*  483 */       k = 32 - j;
/*  484 */       if (this.data[0] << j == 0) {
/*  485 */         m = 0;
/*  486 */         n = this.data[m];
/*  487 */         for (; m < this.nWords - 1; m++) {
/*  488 */           i1 = n >>> k;
/*  489 */           n = this.data[(m + 1)];
/*  490 */           i1 |= n << j;
/*  491 */           this.data[m] = i1;
/*      */         }
/*  493 */         i1 = n >>> k;
/*  494 */         this.data[m] = i1;
/*  495 */         if (i1 == 0) {
/*  496 */           this.nWords -= 1;
/*      */         }
/*  498 */         this.offset += 1;
/*      */       } else {
/*  500 */         m = this.nWords - 1;
/*  501 */         n = this.data[m];
/*  502 */         i1 = n >>> k;
/*  503 */         arrayOfInt1 = this.data;
/*  504 */         int[] arrayOfInt2 = this.data;
/*  505 */         if (i1 != 0) {
/*  506 */           if (this.nWords == this.data.length) {
/*  507 */             this.data = (arrayOfInt1 = new int[this.nWords + 1]);
/*      */           }
/*  509 */           arrayOfInt1[(this.nWords++)] = i1;
/*      */         }
/*  511 */         leftShift(arrayOfInt2, m, arrayOfInt1, j, k, n);
/*      */       }
/*      */     }
/*  514 */     this.offset += i;
/*  515 */     return this;
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
/*      */ 
/*      */   private int size()
/*      */   {
/*  534 */     return this.nWords + this.offset;
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
/*      */   public int quoRemIteration(FDBigInteger paramFDBigInteger)
/*      */     throws IllegalArgumentException
/*      */   {
/*  564 */     assert (!this.isImmutable) : "cannot modify immutable value";
/*      */     
/*      */ 
/*      */ 
/*  568 */     int i = size();
/*  569 */     int j = paramFDBigInteger.size();
/*  570 */     if (i < j)
/*      */     {
/*      */ 
/*  573 */       int k = multAndCarryBy10(this.data, this.nWords, this.data);
/*  574 */       if (k != 0) {
/*  575 */         this.data[(this.nWords++)] = k;
/*      */       } else {
/*  577 */         trimLeadingZeros();
/*      */       }
/*  579 */       return 0; }
/*  580 */     if (i > j) {
/*  581 */       throw new IllegalArgumentException("disparate values");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  586 */     long l1 = (this.data[(this.nWords - 1)] & 0xFFFFFFFF) / (paramFDBigInteger.data[(paramFDBigInteger.nWords - 1)] & 0xFFFFFFFF);
/*  587 */     long l2 = multDiffMe(l1, paramFDBigInteger);
/*  588 */     if (l2 != 0L)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  596 */       long l3 = 0L;
/*  597 */       int n = paramFDBigInteger.offset - this.offset;
/*      */       
/*  599 */       int[] arrayOfInt1 = paramFDBigInteger.data;
/*  600 */       int[] arrayOfInt2 = this.data;
/*  601 */       while (l3 == 0L) {
/*  602 */         int i1 = 0; for (int i2 = n; i2 < this.nWords; i2++) {
/*  603 */           l3 += (arrayOfInt2[i2] & 0xFFFFFFFF) + (arrayOfInt1[i1] & 0xFFFFFFFF);
/*  604 */           arrayOfInt2[i2] = ((int)l3);
/*  605 */           l3 >>>= 32;i1++;
/*      */         }
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
/*  615 */         assert ((l3 == 0L) || (l3 == 1L)) : l3;
/*  616 */         l1 -= 1L;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  622 */     int m = multAndCarryBy10(this.data, this.nWords, this.data);
/*  623 */     assert (m == 0) : m;
/*  624 */     trimLeadingZeros();
/*  625 */     return (int)l1;
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
/*      */   public FDBigInteger multBy10()
/*      */   {
/*  654 */     if (this.nWords == 0) {
/*  655 */       return this;
/*      */     }
/*  657 */     if (this.isImmutable) {
/*  658 */       int[] arrayOfInt = new int[this.nWords + 1];
/*  659 */       arrayOfInt[this.nWords] = multAndCarryBy10(this.data, this.nWords, arrayOfInt);
/*  660 */       return new FDBigInteger(arrayOfInt, this.offset);
/*      */     }
/*  662 */     int i = multAndCarryBy10(this.data, this.nWords, this.data);
/*  663 */     if (i != 0) {
/*  664 */       if (this.nWords == this.data.length) {
/*  665 */         if (this.data[0] == 0) {
/*  666 */           System.arraycopy(this.data, 1, this.data, 0, --this.nWords);
/*  667 */           this.offset += 1;
/*      */         } else {
/*  669 */           this.data = Arrays.copyOf(this.data, this.data.length + 1);
/*      */         }
/*      */       }
/*  672 */       this.data[(this.nWords++)] = i;
/*      */     } else {
/*  674 */       trimLeadingZeros();
/*      */     }
/*  676 */     return this;
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
/*      */   public FDBigInteger multByPow52(int paramInt1, int paramInt2)
/*      */   {
/*  709 */     if (this.nWords == 0) {
/*  710 */       return this;
/*      */     }
/*  712 */     FDBigInteger localFDBigInteger1 = this;
/*  713 */     if (paramInt1 != 0)
/*      */     {
/*  715 */       int i = paramInt2 != 0 ? 1 : 0;
/*  716 */       int[] arrayOfInt; if (paramInt1 < SMALL_5_POW.length) {
/*  717 */         arrayOfInt = new int[this.nWords + 1 + i];
/*  718 */         mult(this.data, this.nWords, SMALL_5_POW[paramInt1], arrayOfInt);
/*  719 */         localFDBigInteger1 = new FDBigInteger(arrayOfInt, this.offset);
/*      */       } else {
/*  721 */         FDBigInteger localFDBigInteger2 = big5pow(paramInt1);
/*  722 */         arrayOfInt = new int[this.nWords + localFDBigInteger2.size() + i];
/*  723 */         mult(this.data, this.nWords, localFDBigInteger2.data, localFDBigInteger2.nWords, arrayOfInt);
/*  724 */         localFDBigInteger1 = new FDBigInteger(arrayOfInt, this.offset + localFDBigInteger2.offset);
/*      */       }
/*      */     }
/*  727 */     return localFDBigInteger1.leftShift(paramInt2);
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
/*      */ 
/*      */   private static void mult(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int[] paramArrayOfInt3)
/*      */   {
/*  746 */     for (int i = 0; i < paramInt1; i++) {
/*  747 */       long l1 = paramArrayOfInt1[i] & 0xFFFFFFFF;
/*  748 */       long l2 = 0L;
/*  749 */       for (int j = 0; j < paramInt2; j++) {
/*  750 */         l2 += (paramArrayOfInt3[(i + j)] & 0xFFFFFFFF) + l1 * (paramArrayOfInt2[j] & 0xFFFFFFFF);
/*  751 */         paramArrayOfInt3[(i + j)] = ((int)l2);
/*  752 */         l2 >>>= 32;
/*      */       }
/*  754 */       paramArrayOfInt3[(i + paramInt2)] = ((int)l2);
/*      */     }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public FDBigInteger leftInplaceSub(FDBigInteger paramFDBigInteger)
/*      */   {
/*  782 */     assert (size() >= paramFDBigInteger.size()) : "result should be positive";
/*      */     FDBigInteger localFDBigInteger;
/*  784 */     if (this.isImmutable) {
/*  785 */       localFDBigInteger = new FDBigInteger((int[])this.data.clone(), this.offset);
/*      */     } else {
/*  787 */       localFDBigInteger = this;
/*      */     }
/*  789 */     int i = paramFDBigInteger.offset - localFDBigInteger.offset;
/*  790 */     int[] arrayOfInt1 = paramFDBigInteger.data;
/*  791 */     Object localObject = localFDBigInteger.data;
/*  792 */     int j = paramFDBigInteger.nWords;
/*  793 */     int k = localFDBigInteger.nWords;
/*  794 */     if (i < 0)
/*      */     {
/*  796 */       int m = k - i;
/*  797 */       if (m < localObject.length) {
/*  798 */         System.arraycopy(localObject, 0, localObject, -i, k);
/*  799 */         Arrays.fill((int[])localObject, 0, -i, 0);
/*      */       } else {
/*  801 */         int[] arrayOfInt2 = new int[m];
/*  802 */         System.arraycopy(localObject, 0, arrayOfInt2, -i, k);
/*  803 */         localFDBigInteger.data = (localObject = arrayOfInt2);
/*      */       }
/*  805 */       localFDBigInteger.offset = paramFDBigInteger.offset;
/*  806 */       localFDBigInteger.nWords = (k = m);
/*  807 */       i = 0;
/*      */     }
/*  809 */     long l1 = 0L;
/*  810 */     int n = i;
/*  811 */     for (int i1 = 0; (i1 < j) && (n < k); n++) {
/*  812 */       long l3 = (localObject[n] & 0xFFFFFFFF) - (arrayOfInt1[i1] & 0xFFFFFFFF) + l1;
/*  813 */       localObject[n] = ((int)l3);
/*  814 */       l1 = l3 >> 32;i1++;
/*      */     }
/*  816 */     for (; 
/*      */         
/*  816 */         (l1 != 0L) && (n < k); n++) {
/*  817 */       long l2 = (localObject[n] & 0xFFFFFFFF) + l1;
/*  818 */       localObject[n] = ((int)l2);
/*  819 */       l1 = l2 >> 32;
/*      */     }
/*  821 */     assert (l1 == 0L) : l1;
/*      */     
/*  823 */     localFDBigInteger.trimLeadingZeros();
/*  824 */     return localFDBigInteger;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public FDBigInteger rightInplaceSub(FDBigInteger paramFDBigInteger)
/*      */   {
/*  851 */     assert (size() >= paramFDBigInteger.size()) : "result should be positive";
/*  852 */     FDBigInteger localFDBigInteger = this;
/*  853 */     if (paramFDBigInteger.isImmutable) {
/*  854 */       paramFDBigInteger = new FDBigInteger((int[])paramFDBigInteger.data.clone(), paramFDBigInteger.offset);
/*      */     }
/*  856 */     int i = localFDBigInteger.offset - paramFDBigInteger.offset;
/*  857 */     Object localObject = paramFDBigInteger.data;
/*  858 */     int[] arrayOfInt1 = localFDBigInteger.data;
/*  859 */     int j = paramFDBigInteger.nWords;
/*  860 */     int k = localFDBigInteger.nWords;
/*  861 */     if (i < 0) {
/*  862 */       m = k;
/*  863 */       if (m < localObject.length) {
/*  864 */         System.arraycopy(localObject, 0, localObject, -i, j);
/*  865 */         Arrays.fill((int[])localObject, 0, -i, 0);
/*      */       } else {
/*  867 */         int[] arrayOfInt2 = new int[m];
/*  868 */         System.arraycopy(localObject, 0, arrayOfInt2, -i, j);
/*  869 */         paramFDBigInteger.data = (localObject = arrayOfInt2);
/*      */       }
/*  871 */       paramFDBigInteger.offset = localFDBigInteger.offset;
/*  872 */       j -= i;
/*  873 */       i = 0;
/*      */     } else {
/*  875 */       m = k + i;
/*  876 */       if (m >= localObject.length) {
/*  877 */         paramFDBigInteger.data = (localObject = Arrays.copyOf((int[])localObject, m));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  888 */     int m = 0;
/*  889 */     long l1 = 0L;
/*  890 */     for (; m < i; m++) {
/*  891 */       long l2 = 0L - (localObject[m] & 0xFFFFFFFF) + l1;
/*  892 */       localObject[m] = ((int)l2);
/*  893 */       l1 = l2 >> 32;
/*      */     }
/*      */     
/*  896 */     for (int n = 0; n < k; n++)
/*      */     {
/*  898 */       long l3 = (arrayOfInt1[n] & 0xFFFFFFFF) - (localObject[m] & 0xFFFFFFFF) + l1;
/*  899 */       localObject[m] = ((int)l3);
/*  900 */       l1 = l3 >> 32;m++;
/*      */     }
/*      */     
/*      */ 
/*  902 */     assert (l1 == 0L) : l1;
/*      */     
/*  904 */     paramFDBigInteger.nWords = m;
/*  905 */     paramFDBigInteger.trimLeadingZeros();
/*  906 */     return paramFDBigInteger;
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
/*      */   private static int checkZeroTail(int[] paramArrayOfInt, int paramInt)
/*      */   {
/*  923 */     while (paramInt > 0) {
/*  924 */       if (paramArrayOfInt[(--paramInt)] != 0) {
/*  925 */         return 1;
/*      */       }
/*      */     }
/*  928 */     return 0;
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
/*      */ 
/*      */ 
/*      */   public int cmp(FDBigInteger paramFDBigInteger)
/*      */   {
/*  948 */     int i = this.nWords + this.offset;
/*  949 */     int j = paramFDBigInteger.nWords + paramFDBigInteger.offset;
/*  950 */     if (i > j)
/*  951 */       return 1;
/*  952 */     if (i < j) {
/*  953 */       return -1;
/*      */     }
/*  955 */     int k = this.nWords;
/*  956 */     int m = paramFDBigInteger.nWords;
/*  957 */     while ((k > 0) && (m > 0)) {
/*  958 */       int n = this.data[(--k)];
/*  959 */       int i1 = paramFDBigInteger.data[(--m)];
/*  960 */       if (n != i1) {
/*  961 */         return (n & 0xFFFFFFFF) < (i1 & 0xFFFFFFFF) ? -1 : 1;
/*      */       }
/*      */     }
/*  964 */     if (k > 0) {
/*  965 */       return checkZeroTail(this.data, k);
/*      */     }
/*  967 */     if (m > 0) {
/*  968 */       return -checkZeroTail(paramFDBigInteger.data, m);
/*      */     }
/*  970 */     return 0;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int cmpPow52(int paramInt1, int paramInt2)
/*      */   {
/*  992 */     if (paramInt1 == 0) {
/*  993 */       int i = paramInt2 >> 5;
/*  994 */       int j = paramInt2 & 0x1F;
/*  995 */       int k = this.nWords + this.offset;
/*  996 */       if (k > i + 1)
/*  997 */         return 1;
/*  998 */       if (k < i + 1) {
/*  999 */         return -1;
/*      */       }
/* 1001 */       int m = this.data[(this.nWords - 1)];
/* 1002 */       int n = 1 << j;
/* 1003 */       if (m != n) {
/* 1004 */         return (m & 0xFFFFFFFF) < (n & 0xFFFFFFFF) ? -1 : 1;
/*      */       }
/* 1006 */       return checkZeroTail(this.data, this.nWords - 1);
/*      */     }
/* 1008 */     return cmp(big5pow(paramInt1).leftShift(paramInt2));
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public int addAndCmp(FDBigInteger paramFDBigInteger1, FDBigInteger paramFDBigInteger2)
/*      */   {
/* 1029 */     int i = paramFDBigInteger1.size();
/* 1030 */     int j = paramFDBigInteger2.size();
/*      */     FDBigInteger localFDBigInteger1;
/*      */     FDBigInteger localFDBigInteger2;
/* 1033 */     int k; int m; if (i >= j) {
/* 1034 */       localFDBigInteger1 = paramFDBigInteger1;
/* 1035 */       localFDBigInteger2 = paramFDBigInteger2;
/* 1036 */       k = i;
/* 1037 */       m = j;
/*      */     } else {
/* 1039 */       localFDBigInteger1 = paramFDBigInteger2;
/* 1040 */       localFDBigInteger2 = paramFDBigInteger1;
/* 1041 */       k = j;
/* 1042 */       m = i;
/*      */     }
/* 1044 */     int n = size();
/* 1045 */     if (k == 0) {
/* 1046 */       return n == 0 ? 0 : 1;
/*      */     }
/* 1048 */     if (m == 0) {
/* 1049 */       return cmp(localFDBigInteger1);
/*      */     }
/* 1051 */     if (k > n) {
/* 1052 */       return -1;
/*      */     }
/* 1054 */     if (k + 1 < n) {
/* 1055 */       return 1;
/*      */     }
/* 1057 */     long l1 = localFDBigInteger1.data[(localFDBigInteger1.nWords - 1)] & 0xFFFFFFFF;
/* 1058 */     if (m == k)
/* 1059 */       l1 += (localFDBigInteger2.data[(localFDBigInteger2.nWords - 1)] & 0xFFFFFFFF);
/*      */     long l2;
/* 1061 */     if (l1 >>> 32 == 0L) {
/* 1062 */       if (l1 + 1L >>> 32 == 0L)
/*      */       {
/* 1064 */         if (k < n) {
/* 1065 */           return 1;
/*      */         }
/*      */         
/* 1068 */         l2 = this.data[(this.nWords - 1)] & 0xFFFFFFFF;
/* 1069 */         if (l2 < l1) {
/* 1070 */           return -1;
/*      */         }
/* 1072 */         if (l2 > l1 + 1L) {
/* 1073 */           return 1;
/*      */         }
/*      */       }
/*      */     } else {
/* 1077 */       if (k + 1 > n) {
/* 1078 */         return -1;
/*      */       }
/*      */       
/* 1081 */       l1 >>>= 32;
/* 1082 */       l2 = this.data[(this.nWords - 1)] & 0xFFFFFFFF;
/* 1083 */       if (l2 < l1) {
/* 1084 */         return -1;
/*      */       }
/* 1086 */       if (l2 > l1 + 1L) {
/* 1087 */         return 1;
/*      */       }
/*      */     }
/* 1090 */     return cmp(localFDBigInteger1.add(localFDBigInteger2));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void makeImmutable()
/*      */   {
/* 1101 */     this.isImmutable = true;
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
/*      */ 
/*      */ 
/*      */ 
/*      */   private FDBigInteger mult(int paramInt)
/*      */   {
/* 1122 */     if (this.nWords == 0) {
/* 1123 */       return this;
/*      */     }
/* 1125 */     int[] arrayOfInt = new int[this.nWords + 1];
/* 1126 */     mult(this.data, this.nWords, paramInt, arrayOfInt);
/* 1127 */     return new FDBigInteger(arrayOfInt, this.offset);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private FDBigInteger mult(FDBigInteger paramFDBigInteger)
/*      */   {
/* 1154 */     if (this.nWords == 0) {
/* 1155 */       return this;
/*      */     }
/* 1157 */     if (size() == 1) {
/* 1158 */       return paramFDBigInteger.mult(this.data[0]);
/*      */     }
/* 1160 */     if (paramFDBigInteger.nWords == 0) {
/* 1161 */       return paramFDBigInteger;
/*      */     }
/* 1163 */     if (paramFDBigInteger.size() == 1) {
/* 1164 */       return mult(paramFDBigInteger.data[0]);
/*      */     }
/* 1166 */     int[] arrayOfInt = new int[this.nWords + paramFDBigInteger.nWords];
/* 1167 */     mult(this.data, this.nWords, paramFDBigInteger.data, paramFDBigInteger.nWords, arrayOfInt);
/* 1168 */     return new FDBigInteger(arrayOfInt, this.offset + paramFDBigInteger.offset);
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
/*      */   private FDBigInteger add(FDBigInteger paramFDBigInteger)
/*      */   {
/* 1184 */     int k = size();
/* 1185 */     int m = paramFDBigInteger.size();
/* 1186 */     FDBigInteger localFDBigInteger1; int i; FDBigInteger localFDBigInteger2; int j; if (k >= m) {
/* 1187 */       localFDBigInteger1 = this;
/* 1188 */       i = k;
/* 1189 */       localFDBigInteger2 = paramFDBigInteger;
/* 1190 */       j = m;
/*      */     } else {
/* 1192 */       localFDBigInteger1 = paramFDBigInteger;
/* 1193 */       i = m;
/* 1194 */       localFDBigInteger2 = this;
/* 1195 */       j = k;
/*      */     }
/* 1197 */     int[] arrayOfInt = new int[i + 1];
/* 1198 */     int n = 0;
/* 1199 */     long l = 0L;
/* 1200 */     for (; n < j; n++) {
/* 1201 */       l += (n < localFDBigInteger1.offset ? 0L : localFDBigInteger1.data[(n - localFDBigInteger1.offset)] & 0xFFFFFFFF) + (n < localFDBigInteger2.offset ? 0L : localFDBigInteger2.data[(n - localFDBigInteger2.offset)] & 0xFFFFFFFF);
/*      */       
/* 1203 */       arrayOfInt[n] = ((int)l);
/* 1204 */       l >>= 32;
/*      */     }
/* 1206 */     for (; n < i; n++) {
/* 1207 */       l += (n < localFDBigInteger1.offset ? 0L : localFDBigInteger1.data[(n - localFDBigInteger1.offset)] & 0xFFFFFFFF);
/* 1208 */       arrayOfInt[n] = ((int)l);
/* 1209 */       l >>= 32;
/*      */     }
/* 1211 */     arrayOfInt[i] = ((int)l);
/* 1212 */     return new FDBigInteger(arrayOfInt, 0);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void multAddMe(int paramInt1, int paramInt2)
/*      */   {
/* 1234 */     long l1 = paramInt1 & 0xFFFFFFFF;
/*      */     
/* 1236 */     long l2 = l1 * (this.data[0] & 0xFFFFFFFF) + (paramInt2 & 0xFFFFFFFF);
/* 1237 */     this.data[0] = ((int)l2);
/* 1238 */     l2 >>>= 32;
/* 1239 */     for (int i = 1; i < this.nWords; i++) {
/* 1240 */       l2 += l1 * (this.data[i] & 0xFFFFFFFF);
/* 1241 */       this.data[i] = ((int)l2);
/* 1242 */       l2 >>>= 32;
/*      */     }
/* 1244 */     if (l2 != 0L) {
/* 1245 */       this.data[(this.nWords++)] = ((int)l2);
/*      */     }
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
/*      */   private long multDiffMe(long paramLong, FDBigInteger paramFDBigInteger)
/*      */   {
/* 1290 */     long l = 0L;
/* 1291 */     if (paramLong != 0L) {
/* 1292 */       int i = paramFDBigInteger.offset - this.offset;
/* 1293 */       int[] arrayOfInt1; int k; if (i >= 0) {
/* 1294 */         arrayOfInt1 = paramFDBigInteger.data;
/* 1295 */         int[] arrayOfInt2 = this.data;
/* 1296 */         k = 0; for (int m = i; k < paramFDBigInteger.nWords; m++) {
/* 1297 */           l += (arrayOfInt2[m] & 0xFFFFFFFF) - paramLong * (arrayOfInt1[k] & 0xFFFFFFFF);
/* 1298 */           arrayOfInt2[m] = ((int)l);
/* 1299 */           l >>= 32;k++;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1302 */         i = -i;
/* 1303 */         arrayOfInt1 = new int[this.nWords + i];
/* 1304 */         int j = 0;
/* 1305 */         k = 0;
/* 1306 */         int[] arrayOfInt3 = paramFDBigInteger.data;
/* 1307 */         for (; (k < i) && (j < paramFDBigInteger.nWords); k++) {
/* 1308 */           l -= paramLong * (arrayOfInt3[j] & 0xFFFFFFFF);
/* 1309 */           arrayOfInt1[k] = ((int)l);
/* 1310 */           l >>= 32;j++;
/*      */         }
/*      */         
/*      */ 
/* 1312 */         int n = 0;
/* 1313 */         int[] arrayOfInt4 = this.data;
/* 1314 */         for (; j < paramFDBigInteger.nWords; k++) {
/* 1315 */           l += (arrayOfInt4[n] & 0xFFFFFFFF) - paramLong * (arrayOfInt3[j] & 0xFFFFFFFF);
/* 1316 */           arrayOfInt1[k] = ((int)l);
/* 1317 */           l >>= 32;j++;n++;
/*      */         }
/*      */         
/*      */ 
/* 1319 */         this.nWords += i;
/* 1320 */         this.offset -= i;
/* 1321 */         this.data = arrayOfInt1;
/*      */       }
/*      */     }
/* 1324 */     return l;
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
/*      */ 
/*      */ 
/*      */   private static int multAndCarryBy10(int[] paramArrayOfInt1, int paramInt, int[] paramArrayOfInt2)
/*      */   {
/* 1344 */     long l1 = 0L;
/* 1345 */     for (int i = 0; i < paramInt; i++) {
/* 1346 */       long l2 = (paramArrayOfInt1[i] & 0xFFFFFFFF) * 10L + l1;
/* 1347 */       paramArrayOfInt2[i] = ((int)l2);
/* 1348 */       l1 = l2 >>> 32;
/*      */     }
/* 1350 */     return (int)l1;
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
/*      */   private static void mult(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2)
/*      */   {
/* 1368 */     long l1 = paramInt2 & 0xFFFFFFFF;
/* 1369 */     long l2 = 0L;
/* 1370 */     for (int i = 0; i < paramInt1; i++) {
/* 1371 */       long l3 = (paramArrayOfInt1[i] & 0xFFFFFFFF) * l1 + l2;
/* 1372 */       paramArrayOfInt2[i] = ((int)l3);
/* 1373 */       l2 = l3 >>> 32;
/*      */     }
/* 1375 */     paramArrayOfInt2[paramInt1] = ((int)l2);
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
/*      */ 
/*      */ 
/*      */   private static void mult(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt2)
/*      */   {
/* 1395 */     long l1 = paramInt2 & 0xFFFFFFFF;
/* 1396 */     long l2 = 0L;
/* 1397 */     long l3; for (int i = 0; i < paramInt1; i++) {
/* 1398 */       l3 = l1 * (paramArrayOfInt1[i] & 0xFFFFFFFF) + l2;
/* 1399 */       paramArrayOfInt2[i] = ((int)l3);
/* 1400 */       l2 = l3 >>> 32;
/*      */     }
/* 1402 */     paramArrayOfInt2[paramInt1] = ((int)l2);
/* 1403 */     l1 = paramInt3 & 0xFFFFFFFF;
/* 1404 */     l2 = 0L;
/* 1405 */     for (i = 0; i < paramInt1; i++) {
/* 1406 */       l3 = (paramArrayOfInt2[(i + 1)] & 0xFFFFFFFF) + l1 * (paramArrayOfInt1[i] & 0xFFFFFFFF) + l2;
/* 1407 */       paramArrayOfInt2[(i + 1)] = ((int)l3);
/* 1408 */       l2 = l3 >>> 32;
/*      */     }
/* 1410 */     paramArrayOfInt2[(paramInt1 + 1)] = ((int)l2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static FDBigInteger big5pow(int paramInt)
/*      */   {
/* 1421 */     assert (paramInt >= 0) : paramInt;
/* 1422 */     if (paramInt < 340) {
/* 1423 */       return POW_5_CACHE[paramInt];
/*      */     }
/* 1425 */     return big5powRec(paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static FDBigInteger big5powRec(int paramInt)
/*      */   {
/* 1436 */     if (paramInt < 340) {
/* 1437 */       return POW_5_CACHE[paramInt];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1446 */     int i = paramInt >> 1;
/* 1447 */     int j = paramInt - i;
/* 1448 */     FDBigInteger localFDBigInteger = big5powRec(i);
/* 1449 */     if (j < SMALL_5_POW.length) {
/* 1450 */       return localFDBigInteger.mult(SMALL_5_POW[j]);
/*      */     }
/* 1452 */     return localFDBigInteger.mult(big5powRec(j));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toHexString()
/*      */   {
/* 1463 */     if (this.nWords == 0) {
/* 1464 */       return "0";
/*      */     }
/* 1466 */     StringBuilder localStringBuilder = new StringBuilder((this.nWords + this.offset) * 8);
/* 1467 */     for (int i = this.nWords - 1; i >= 0; i--) {
/* 1468 */       String str = Integer.toHexString(this.data[i]);
/* 1469 */       for (int j = str.length(); j < 8; j++) {
/* 1470 */         localStringBuilder.append('0');
/*      */       }
/* 1472 */       localStringBuilder.append(str);
/*      */     }
/* 1474 */     for (i = this.offset; i > 0; i--) {
/* 1475 */       localStringBuilder.append("00000000");
/*      */     }
/* 1477 */     return localStringBuilder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BigInteger toBigInteger()
/*      */   {
/* 1487 */     byte[] arrayOfByte = new byte[this.nWords * 4 + 1];
/* 1488 */     for (int i = 0; i < this.nWords; i++) {
/* 1489 */       int j = this.data[i];
/* 1490 */       arrayOfByte[(arrayOfByte.length - 4 * i - 1)] = ((byte)j);
/* 1491 */       arrayOfByte[(arrayOfByte.length - 4 * i - 2)] = ((byte)(j >> 8));
/* 1492 */       arrayOfByte[(arrayOfByte.length - 4 * i - 3)] = ((byte)(j >> 16));
/* 1493 */       arrayOfByte[(arrayOfByte.length - 4 * i - 4)] = ((byte)(j >> 24));
/*      */     }
/* 1495 */     return new BigInteger(arrayOfByte).shiftLeft(this.offset * 32);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1506 */     return toBigInteger().toString();
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\FDBigInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*      */ package sun.misc;
/*      */ 
/*      */ import java.util.Arrays;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FloatingDecimal
/*      */ {
/*      */   static final int EXP_SHIFT = 52;
/*      */   static final long FRACT_HOB = 4503599627370496L;
/*      */   static final long EXP_ONE = 4607182418800017408L;
/*      */   static final int MAX_SMALL_BIN_EXP = 62;
/*      */   static final int MIN_SMALL_BIN_EXP = -21;
/*      */   static final int MAX_DECIMAL_DIGITS = 15;
/*      */   static final int MAX_DECIMAL_EXPONENT = 308;
/*      */   static final int MIN_DECIMAL_EXPONENT = -324;
/*      */   static final int BIG_DECIMAL_EXPONENT = 324;
/*      */   static final int MAX_NDIGITS = 1100;
/*      */   static final int SINGLE_EXP_SHIFT = 23;
/*      */   static final int SINGLE_FRACT_HOB = 8388608;
/*      */   static final int SINGLE_MAX_DECIMAL_DIGITS = 7;
/*      */   static final int SINGLE_MAX_DECIMAL_EXPONENT = 38;
/*      */   static final int SINGLE_MIN_DECIMAL_EXPONENT = -45;
/*      */   static final int SINGLE_MAX_NDIGITS = 200;
/*      */   static final int INT_DECIMAL_DIGITS = 9;
/*      */   private static final String INFINITY_REP = "Infinity";
/*      */   
/*      */   public static String toJavaFormatString(double paramDouble)
/*      */   {
/*   70 */     return getBinaryToASCIIConverter(paramDouble).toJavaFormatString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toJavaFormatString(float paramFloat)
/*      */   {
/*   80 */     return getBinaryToASCIIConverter(paramFloat).toJavaFormatString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void appendTo(double paramDouble, Appendable paramAppendable)
/*      */   {
/*   89 */     getBinaryToASCIIConverter(paramDouble).appendTo(paramAppendable);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void appendTo(float paramFloat, Appendable paramAppendable)
/*      */   {
/*   98 */     getBinaryToASCIIConverter(paramFloat).appendTo(paramAppendable);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static double parseDouble(String paramString) // 将 String 类型转成 double 类型
/*      */     throws NumberFormatException
/*      */   {
/*  110 */     return readJavaFormatString(paramString).doubleValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static float parseFloat(String paramString)
/*      */     throws NumberFormatException
/*      */   {
/*  122 */     return readJavaFormatString(paramString).floatValue();
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
/*      */   private static class ExceptionalBinaryToASCIIBuffer
/*      */     implements BinaryToASCIIConverter
/*      */   {
/*      */     private final String image;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean isNegative;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public ExceptionalBinaryToASCIIBuffer(String paramString, boolean paramBoolean)
/*      */     {
/*  194 */       this.image = paramString;
/*  195 */       this.isNegative = paramBoolean;
/*      */     }
/*      */     
/*      */     public String toJavaFormatString()
/*      */     {
/*  200 */       return this.image;
/*      */     }
/*      */     
/*      */     public void appendTo(Appendable paramAppendable)
/*      */     {
/*  205 */       if ((paramAppendable instanceof StringBuilder)) {
/*  206 */         ((StringBuilder)paramAppendable).append(this.image);
/*  207 */       } else if ((paramAppendable instanceof StringBuffer)) {
/*  208 */         ((StringBuffer)paramAppendable).append(this.image);
/*      */       }
/*  210 */       else if (!$assertionsDisabled) { throw new AssertionError();
/*      */       }
/*      */     }
/*      */     
/*      */     public int getDecimalExponent()
/*      */     {
/*  216 */       throw new IllegalArgumentException("Exceptional value does not have an exponent");
/*      */     }
/*      */     
/*      */     public int getDigits(char[] paramArrayOfChar)
/*      */     {
/*  221 */       throw new IllegalArgumentException("Exceptional value does not have digits");
/*      */     }
/*      */     
/*      */     public boolean isNegative()
/*      */     {
/*  226 */       return this.isNegative;
/*      */     }
/*      */     
/*      */     public boolean isExceptional()
/*      */     {
/*  231 */       return true;
/*      */     }
/*      */     
/*      */     public boolean digitsRoundedUp()
/*      */     {
/*  236 */       throw new IllegalArgumentException("Exceptional value is not rounded");
/*      */     }
/*      */     
/*      */     public boolean decimalDigitsExact()
/*      */     {
/*  241 */       throw new IllegalArgumentException("Exceptional value is not exact");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*  246 */   private static final int INFINITY_LENGTH = "Infinity".length();
/*      */   private static final String NAN_REP = "NaN";
/*  248 */   private static final int NAN_LENGTH = "NaN".length();
/*      */   
/*  250 */   private static final BinaryToASCIIConverter B2AC_POSITIVE_INFINITY = new ExceptionalBinaryToASCIIBuffer("Infinity", false);
/*  251 */   private static final BinaryToASCIIConverter B2AC_NEGATIVE_INFINITY = new ExceptionalBinaryToASCIIBuffer("-Infinity", true);
/*  252 */   private static final BinaryToASCIIConverter B2AC_NOT_A_NUMBER = new ExceptionalBinaryToASCIIBuffer("NaN", false);
/*  253 */   private static final BinaryToASCIIConverter B2AC_POSITIVE_ZERO = new BinaryToASCIIBuffer(false, new char[] { '0' });
/*  254 */   private static final BinaryToASCIIConverter B2AC_NEGATIVE_ZERO = new BinaryToASCIIBuffer(true, new char[] { '0' });
/*      */   
/*      */ 
/*      */   static class BinaryToASCIIBuffer
/*      */     implements BinaryToASCIIConverter
/*      */   {
/*      */     private boolean isNegative;
/*      */     private int decExponent;
/*      */     private int firstDigitIndex;
/*      */     private int nDigits;
/*      */     private final char[] digits;
/*  265 */     private final char[] buffer = new char[26];
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  274 */     private boolean exactDecimalConversion = false;
/*      */     
/*      */ 
/*      */ 
/*  278 */     private boolean decimalDigitsRoundedUp = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     BinaryToASCIIBuffer()
/*      */     {
/*  285 */       this.digits = new char[20];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     BinaryToASCIIBuffer(boolean paramBoolean, char[] paramArrayOfChar)
/*      */     {
/*  292 */       this.isNegative = paramBoolean;
/*  293 */       this.decExponent = 0;
/*  294 */       this.digits = paramArrayOfChar;
/*  295 */       this.firstDigitIndex = 0;
/*  296 */       this.nDigits = paramArrayOfChar.length;
/*      */     }
/*      */     
/*      */     public String toJavaFormatString()
/*      */     {
/*  301 */       int i = getChars(this.buffer);
/*  302 */       return new String(this.buffer, 0, i);
/*      */     }
/*      */     
/*      */     public void appendTo(Appendable paramAppendable)
/*      */     {
/*  307 */       int i = getChars(this.buffer);
/*  308 */       if ((paramAppendable instanceof StringBuilder)) {
/*  309 */         ((StringBuilder)paramAppendable).append(this.buffer, 0, i);
/*  310 */       } else if ((paramAppendable instanceof StringBuffer)) {
/*  311 */         ((StringBuffer)paramAppendable).append(this.buffer, 0, i);
/*      */       }
/*  313 */       else if (!$assertionsDisabled) { throw new AssertionError();
/*      */       }
/*      */     }
/*      */     
/*      */     public int getDecimalExponent()
/*      */     {
/*  319 */       return this.decExponent;
/*      */     }
/*      */     
/*      */     public int getDigits(char[] paramArrayOfChar)
/*      */     {
/*  324 */       System.arraycopy(this.digits, this.firstDigitIndex, paramArrayOfChar, 0, this.nDigits);
/*  325 */       return this.nDigits;
/*      */     }
/*      */     
/*      */     public boolean isNegative()
/*      */     {
/*  330 */       return this.isNegative;
/*      */     }
/*      */     
/*      */     public boolean isExceptional()
/*      */     {
/*  335 */       return false;
/*      */     }
/*      */     
/*      */     public boolean digitsRoundedUp()
/*      */     {
/*  340 */       return this.decimalDigitsRoundedUp;
/*      */     }
/*      */     
/*      */     public boolean decimalDigitsExact()
/*      */     {
/*  345 */       return this.exactDecimalConversion;
/*      */     }
/*      */     
/*      */     private void setSign(boolean paramBoolean) {
/*  349 */       this.isNegative = paramBoolean;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void developLongDigits(int paramInt1, long paramLong, int paramInt2)
/*      */     {
/*  368 */       if (paramInt2 != 0)
/*      */       {
/*      */ 
/*  371 */         long l1 = FDBigInteger.LONG_5_POW[paramInt2] << paramInt2;
/*  372 */         long l2 = paramLong % l1;
/*  373 */         paramLong /= l1;
/*  374 */         paramInt1 += paramInt2;
/*  375 */         if (l2 >= l1 >> 1)
/*      */         {
/*  377 */           paramLong += 1L;
/*      */         }
/*      */       }
/*  380 */       int i = this.digits.length - 1;
/*      */       int j;
/*  382 */       if (paramLong <= 2147483647L) {
/*  383 */         assert (paramLong > 0L) : paramLong;
/*      */         
/*      */ 
/*  386 */         int k = (int)paramLong;
/*  387 */         j = k % 10;
/*  388 */         k /= 10;
/*  389 */         while (j == 0) {
/*  390 */           paramInt1++;
/*  391 */           j = k % 10;
/*  392 */           k /= 10;
/*      */         }
/*  394 */         while (k != 0) {
/*  395 */           this.digits[(i--)] = ((char)(j + 48));
/*  396 */           paramInt1++;
/*  397 */           j = k % 10;
/*  398 */           k /= 10;
/*      */         }
/*  400 */         this.digits[i] = ((char)(j + 48));
/*      */       }
/*      */       else
/*      */       {
/*  404 */         j = (int)(paramLong % 10L);
/*  405 */         paramLong /= 10L;
/*  406 */         while (j == 0) {
/*  407 */           paramInt1++;
/*  408 */           j = (int)(paramLong % 10L);
/*  409 */           paramLong /= 10L;
/*      */         }
/*  411 */         while (paramLong != 0L) {
/*  412 */           this.digits[(i--)] = ((char)(j + 48));
/*  413 */           paramInt1++;
/*  414 */           j = (int)(paramLong % 10L);
/*  415 */           paramLong /= 10L;
/*      */         }
/*  417 */         this.digits[i] = ((char)(j + 48));
/*      */       }
/*  419 */       this.decExponent = (paramInt1 + 1);
/*  420 */       this.firstDigitIndex = i;
/*  421 */       this.nDigits = (this.digits.length - i);
/*      */     }
/*      */
               // paramInt1 - E 的值
/*      */     private void dtoa(int paramInt1, long paramLong, int paramInt2, boolean paramBoolean)
/*      */     {
/*  426 */       assert (paramLong > 0L); // 断言 paramLong > 0
/*  427 */       assert ((paramLong & 0x10000000000000) != 0L); // 断言 paramLong 的第 53 位(从右到左) 为 1
/*      */       
/*      */ 
/*      */
                 // 获取 paramLong 的末尾 0 的个数
/*  431 */       int i = Long.numberOfTrailingZeros(paramLong);
/*      */       
/*      */ 
/*  434 */       int j = 53 - i; // 获取有效数值 j
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  439 */       this.decimalDigitsRoundedUp = false;
/*  440 */       this.exactDecimalConversion = false;
/*      */       
/*      */ 
/*  443 */       int k = Math.max(0, j - paramInt1 - 1);
/*  444 */       if ((paramInt1 <= 62) && (paramInt1 >= -21))
/*      */       {
/*      */ 
/*      */ 
/*  448 */         if ((k < FDBigInteger.LONG_5_POW.length) && (j + N_5_BITS[k] < 64))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  464 */           if (k == 0)
/*      */           {
/*  466 */             if (paramInt1 > paramInt2) {
/*  467 */               m = insignificantDigitsForPow2(paramInt1 - paramInt2 - 1);
/*      */             } else {
/*  469 */               m = 0;
/*      */             }
/*  471 */             if (paramInt1 >= 52) {
/*  472 */               paramLong <<= paramInt1 - 52;
/*      */             } else {
/*  474 */               paramLong >>>= 52 - paramInt1;
/*      */             }
/*  476 */             developLongDigits(0, paramLong, m);
/*  477 */             return;
/*      */           }
/*      */         }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  515 */       int m = estimateDecExp(paramLong, paramInt1);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  520 */       int i1 = Math.max(0, -m);
/*  521 */       int n = i1 + k + paramInt1;
/*      */       
/*  523 */       int i3 = Math.max(0, m);
/*  524 */       int i2 = i3 + k;
/*      */       
/*  526 */       int i5 = i1;
/*  527 */       int i4 = n - paramInt2;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  537 */       paramLong >>>= i;
/*  538 */       n -= j - 1;
/*  539 */       int i6 = Math.min(n, i2);
/*  540 */       n -= i6;
/*  541 */       i2 -= i6;
/*  542 */       i4 -= i6;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  550 */       if (j == 1) {
/*  551 */         i4--;
/*      */       }
/*      */       
/*  554 */       if (i4 < 0)
/*      */       {
/*      */ 
/*      */ 
/*  558 */         n -= i4;
/*  559 */         i2 -= i4;
/*  560 */         i4 = 0;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  570 */       int i7 = 0;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  591 */       int i11 = j + n + (i1 < N_5_BITS.length ? N_5_BITS[i1] : i1 * 3);
/*      */       
/*      */ 
/*  594 */       int i12 = i2 + 1 + (i3 + 1 < N_5_BITS.length ? N_5_BITS[(i3 + 1)] : (i3 + 1) * 3);
/*  595 */       int i14; int i10; int i8; int i9; long l1; if ((i11 < 64) && (i12 < 64)) {
/*  596 */         if ((i11 < 32) && (i12 < 32))
/*      */         {
/*  598 */           int i13 = (int)paramLong * FDBigInteger.SMALL_5_POW[i1] << n;
/*  599 */           i14 = FDBigInteger.SMALL_5_POW[i3] << i2;
/*  600 */           int i15 = FDBigInteger.SMALL_5_POW[i5] << i4;
/*  601 */           int i16 = i14 * 10;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  607 */           i7 = 0;
/*  608 */           i10 = i13 / i14;
/*  609 */           i13 = 10 * (i13 % i14);
/*  610 */           i15 *= 10;
/*  611 */           i8 = i13 < i15 ? 1 : 0;
/*  612 */           i9 = i13 + i15 > i16 ? 1 : 0;
/*  613 */           assert (i10 < 10) : i10;
/*  614 */           if ((i10 == 0) && (i9 == 0))
/*      */           {
/*  616 */             m--;
/*      */           } else {
/*  618 */             this.digits[(i7++)] = ((char)(48 + i10));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  626 */           if ((!paramBoolean) || (m < -3) || (m >= 8)) {
/*  627 */             i9 = i8 = 0;
/*      */           }
/*  629 */           while ((i8 == 0) && (i9 == 0)) {
/*  630 */             i10 = i13 / i14;
/*  631 */             i13 = 10 * (i13 % i14);
/*  632 */             i15 *= 10;
/*  633 */             assert (i10 < 10) : i10;
/*  634 */             if (i15 > 0L) {
/*  635 */               i8 = i13 < i15 ? 1 : 0;
/*  636 */               i9 = i13 + i15 > i16 ? 1 : 0;
/*      */ 
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  643 */               i8 = 1;
/*  644 */               i9 = 1;
/*      */             }
/*  646 */             this.digits[(i7++)] = ((char)(48 + i10));
/*      */           }
/*  648 */           l1 = (i13 << 1) - i16;
/*  649 */           this.exactDecimalConversion = (i13 == 0);
/*      */         }
/*      */         else {
/*  652 */           long l2 = paramLong * FDBigInteger.LONG_5_POW[i1] << n;
/*  653 */           long l3 = FDBigInteger.LONG_5_POW[i3] << i2;
/*  654 */           long l4 = FDBigInteger.LONG_5_POW[i5] << i4;
/*  655 */           long l5 = l3 * 10L;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  661 */           i7 = 0;
/*  662 */           i10 = (int)(l2 / l3);
/*  663 */           l2 = 10L * (l2 % l3);
/*  664 */           l4 *= 10L;
/*  665 */           i8 = l2 < l4 ? 1 : 0;
/*  666 */           i9 = l2 + l4 > l5 ? 1 : 0;
/*  667 */           assert (i10 < 10) : i10;
/*  668 */           if ((i10 == 0) && (i9 == 0))
/*      */           {
/*  670 */             m--;
/*      */           } else {
/*  672 */             this.digits[(i7++)] = ((char)(48 + i10));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  680 */           if ((!paramBoolean) || (m < -3) || (m >= 8)) {
/*  681 */             i9 = i8 = 0;
/*      */           }
/*  683 */           while ((i8 == 0) && (i9 == 0)) {
/*  684 */             i10 = (int)(l2 / l3);
/*  685 */             l2 = 10L * (l2 % l3);
/*  686 */             l4 *= 10L;
/*  687 */             assert (i10 < 10) : i10;
/*  688 */             if (l4 > 0L) {
/*  689 */               i8 = l2 < l4 ? 1 : 0;
/*  690 */               i9 = l2 + l4 > l5 ? 1 : 0;
/*      */ 
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  697 */               i8 = 1;
/*  698 */               i9 = 1;
/*      */             }
/*  700 */             this.digits[(i7++)] = ((char)(48 + i10));
/*      */           }
/*  702 */           l1 = (l2 << 1) - l5;
/*  703 */           this.exactDecimalConversion = (l2 == 0L);
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  710 */         FDBigInteger localFDBigInteger1 = FDBigInteger.valueOfPow52(i3, i2);
/*  711 */         i14 = localFDBigInteger1.getNormalizationBias();
/*  712 */         localFDBigInteger1 = localFDBigInteger1.leftShift(i14);
/*      */         
/*  714 */         FDBigInteger localFDBigInteger2 = FDBigInteger.valueOfMulPow52(paramLong, i1, n + i14);
/*  715 */         FDBigInteger localFDBigInteger3 = FDBigInteger.valueOfPow52(i5 + 1, i4 + i14 + 1);
/*      */         
/*  717 */         FDBigInteger localFDBigInteger4 = FDBigInteger.valueOfPow52(i3 + 1, i2 + i14 + 1);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  723 */         i7 = 0;
/*  724 */         i10 = localFDBigInteger2.quoRemIteration(localFDBigInteger1);
/*  725 */         i8 = localFDBigInteger2.cmp(localFDBigInteger3) < 0 ? 1 : 0;
/*  726 */         i9 = localFDBigInteger4.addAndCmp(localFDBigInteger2, localFDBigInteger3) <= 0 ? 1 : 0;
/*      */         
/*  728 */         assert (i10 < 10) : i10;
/*  729 */         if ((i10 == 0) && (i9 == 0))
/*      */         {
/*  731 */           m--;
/*      */         } else {
/*  733 */           this.digits[(i7++)] = ((char)(48 + i10));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  741 */         if ((!paramBoolean) || (m < -3) || (m >= 8)) {
/*  742 */           i9 = i8 = 0;
/*      */         }
/*  744 */         while ((i8 == 0) && (i9 == 0)) {
/*  745 */           i10 = localFDBigInteger2.quoRemIteration(localFDBigInteger1);
/*  746 */           assert (i10 < 10) : i10;
/*  747 */           localFDBigInteger3 = localFDBigInteger3.multBy10();
/*  748 */           i8 = localFDBigInteger2.cmp(localFDBigInteger3) < 0 ? 1 : 0;
/*  749 */           i9 = localFDBigInteger4.addAndCmp(localFDBigInteger2, localFDBigInteger3) <= 0 ? 1 : 0;
/*  750 */           this.digits[(i7++)] = ((char)(48 + i10));
/*      */         }
/*  752 */         if ((i9 != 0) && (i8 != 0)) {
/*  753 */           localFDBigInteger2 = localFDBigInteger2.leftShift(1);
/*  754 */           l1 = localFDBigInteger2.cmp(localFDBigInteger4);
/*      */         } else {
/*  756 */           l1 = 0L;
/*      */         }
/*  758 */         this.exactDecimalConversion = (localFDBigInteger2.cmp(FDBigInteger.ZERO) == 0);
/*      */       }
/*  760 */       this.decExponent = (m + 1);
/*  761 */       this.firstDigitIndex = 0;
/*  762 */       this.nDigits = i7;
/*      */       
/*      */ 
/*      */ 
/*  766 */       if (i9 != 0) {
/*  767 */         if (i8 != 0) {
/*  768 */           if (l1 == 0L)
/*      */           {
/*      */ 
/*  771 */             if ((this.digits[(this.firstDigitIndex + this.nDigits - 1)] & 0x1) != 0) {
/*  772 */               roundup();
/*      */             }
/*  774 */           } else if (l1 > 0L) {
/*  775 */             roundup();
/*      */           }
/*      */         } else {
/*  778 */           roundup();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void roundup()
/*      */     {
/*  789 */       int i = this.firstDigitIndex + this.nDigits - 1;
/*  790 */       int j = this.digits[i];
/*  791 */       if (j == 57) {
/*  792 */         while ((j == 57) && (i > this.firstDigitIndex)) {
/*  793 */           this.digits[i] = '0';
/*  794 */           j = this.digits[(--i)];
/*      */         }
/*  796 */         if (j == 57)
/*      */         {
/*  798 */           this.decExponent += 1;
/*  799 */           this.digits[this.firstDigitIndex] = '1';
/*  800 */           return;
/*      */         }
/*      */       }
/*      */       
/*  804 */       this.digits[i] = ((char)(j + 1));
/*  805 */       this.decimalDigitsRoundedUp = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     static int estimateDecExp(long paramLong, int paramInt)
/*      */     {
/*  820 */       double d1 = Double.longBitsToDouble(0x3FF0000000000000 | paramLong & 0xFFFFFFFFFFFFF);
/*  821 */       double d2 = (d1 - 1.5D) * 0.289529654D + 0.176091259D + paramInt * 0.301029995663981D;
/*  822 */       long l1 = Double.doubleToRawLongBits(d2);
/*  823 */       int i = (int)((l1 & 0x7FF0000000000000) >> 52) - 1023;
/*  824 */       int j = (l1 & 0x8000000000000000) != 0L ? 1 : 0;
/*  825 */       if ((i >= 0) && (i < 52)) {
/*  826 */         long l2 = 4503599627370495L >> i;
/*  827 */         int k = (int)((l1 & 0xFFFFFFFFFFFFF | 0x10000000000000) >> 52 - i);
/*  828 */         return j != 0 ? -k - 1 : (l2 & l1) == 0L ? -k : k; }
/*  829 */       if (i < 0) {
/*  830 */         return j != 0 ? -1 : (l1 & 0x7FFFFFFFFFFFFFFF) == 0L ? 0 : 0;
/*      */       }
/*      */       
/*  833 */       return (int)d2;
/*      */     }
/*      */     
/*      */ 
/*      */     private static int insignificantDigits(int paramInt)
/*      */     {
/*  839 */       for (int i = 0; paramInt >= 10L; i++) {
/*  840 */         paramInt = (int)(paramInt / 10L);
/*      */       }
/*  842 */       return i;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private static int insignificantDigitsForPow2(int paramInt)
/*      */     {
/*  852 */       if ((paramInt > 1) && (paramInt < insignificantDigitsNumber.length)) {
/*  853 */         return insignificantDigitsNumber[paramInt];
/*      */       }
/*  855 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  865 */     private static int[] insignificantDigitsNumber = { 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 15, 15, 16, 16, 16, 17, 17, 17, 18, 18, 18, 19 };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  875 */     private static final int[] N_5_BITS = { 0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 24, 26, 28, 31, 33, 35, 38, 40, 42, 45, 47, 49, 52, 54, 56, 59, 61 };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int getChars(char[] paramArrayOfChar) // paramArrayOfChar 为 26 位是由于 24 位有效数字还有 . 和 -
/*      */     {
/*  906 */       assert (this.nDigits <= 19) : this.nDigits;
/*  907 */       int i = 0;
/*  908 */       if (this.isNegative) {
/*  909 */         paramArrayOfChar[0] = '-';
/*  910 */         i = 1; }
/*      */       int j;
/*  912 */       if ((this.decExponent > 0) && (this.decExponent < 8))
/*      */       {
/*  914 */         j = Math.min(this.nDigits, this.decExponent);
/*  915 */         System.arraycopy(this.digits, this.firstDigitIndex, paramArrayOfChar, i, j);
/*  916 */         i += j;
/*  917 */         if (j < this.decExponent) {
/*  918 */           j = this.decExponent - j;
/*  919 */           Arrays.fill(paramArrayOfChar, i, i + j, '0');
/*  920 */           i += j;
/*  921 */           paramArrayOfChar[(i++)] = '.';
/*  922 */           paramArrayOfChar[(i++)] = '0';
/*      */         } else {
/*  924 */           paramArrayOfChar[(i++)] = '.';
/*  925 */           if (j < this.nDigits) {
/*  926 */             int k = this.nDigits - j;
/*  927 */             System.arraycopy(this.digits, this.firstDigitIndex + j, paramArrayOfChar, i, k);
/*  928 */             i += k;
/*      */           } else {
/*  930 */             paramArrayOfChar[(i++)] = '0';
/*      */           }
/*      */         }
/*  933 */       } else if ((this.decExponent <= 0) && (this.decExponent > -3)) {
/*  934 */         paramArrayOfChar[(i++)] = '0';
/*  935 */         paramArrayOfChar[(i++)] = '.';
/*  936 */         if (this.decExponent != 0) {
/*  937 */           Arrays.fill(paramArrayOfChar, i, i - this.decExponent, '0');
/*  938 */           i -= this.decExponent;
/*      */         }
/*  940 */         System.arraycopy(this.digits, this.firstDigitIndex, paramArrayOfChar, i, this.nDigits);
/*  941 */         i += this.nDigits;
/*      */       } else {
/*  943 */         paramArrayOfChar[(i++)] = this.digits[this.firstDigitIndex];
/*  944 */         paramArrayOfChar[(i++)] = '.';
/*  945 */         if (this.nDigits > 1) {
/*  946 */           System.arraycopy(this.digits, this.firstDigitIndex + 1, paramArrayOfChar, i, this.nDigits - 1);
/*  947 */           i += this.nDigits - 1;
/*      */         } else {
/*  949 */           paramArrayOfChar[(i++)] = '0';
/*      */         }
/*  951 */         paramArrayOfChar[(i++)] = 'E';
/*      */         
/*  953 */         if (this.decExponent <= 0) {
/*  954 */           paramArrayOfChar[(i++)] = '-';
/*  955 */           j = -this.decExponent + 1;
/*      */         } else {
/*  957 */           j = this.decExponent - 1;
/*      */         }
/*      */         
/*  960 */         if (j <= 9) {
/*  961 */           paramArrayOfChar[(i++)] = ((char)(j + 48));
/*  962 */         } else if (j <= 99) {
/*  963 */           paramArrayOfChar[(i++)] = ((char)(j / 10 + 48));
/*  964 */           paramArrayOfChar[(i++)] = ((char)(j % 10 + 48));
/*      */         } else {
/*  966 */           paramArrayOfChar[(i++)] = ((char)(j / 100 + 48));
/*  967 */           j %= 100;
/*  968 */           paramArrayOfChar[(i++)] = ((char)(j / 10 + 48));
/*  969 */           paramArrayOfChar[(i++)] = ((char)(j % 10 + 48));
/*      */         }
/*      */       }
/*  972 */       return i;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*  977 */   private static final ThreadLocal<BinaryToASCIIBuffer> threadLocalBinaryToASCIIBuffer = new ThreadLocal()
/*      */   {
/*      */     protected BinaryToASCIIBuffer initialValue()
/*      */     {
/*  981 */       return new BinaryToASCIIBuffer();
/*      */     }
/*      */   };
/*      */   
/*      */   private static BinaryToASCIIBuffer getBinaryToASCIIBuffer() {
/*  986 */     return (BinaryToASCIIBuffer)threadLocalBinaryToASCIIBuffer.get();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static class PreparedASCIIToBinaryBuffer
/*      */     implements ASCIIToBinaryConverter
/*      */   {
/*      */     private final double doubleVal;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private final float floatVal;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public PreparedASCIIToBinaryBuffer(double paramDouble, float paramFloat)
/*      */     {
/* 1010 */       this.doubleVal = paramDouble;
/* 1011 */       this.floatVal = paramFloat;
/*      */     }
/*      */     
/*      */     public double doubleValue()
/*      */     {
/* 1016 */       return this.doubleVal;
/*      */     }
/*      */     
/*      */     public float floatValue()
/*      */     {
/* 1021 */       return this.floatVal;
/*      */     }
/*      */   }
/*      */   
/* 1025 */   static final ASCIIToBinaryConverter A2BC_POSITIVE_INFINITY = new PreparedASCIIToBinaryBuffer(Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
/* 1026 */   static final ASCIIToBinaryConverter A2BC_NEGATIVE_INFINITY = new PreparedASCIIToBinaryBuffer(Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
/* 1027 */   static final ASCIIToBinaryConverter A2BC_NOT_A_NUMBER = new PreparedASCIIToBinaryBuffer(NaN.0D, NaN.0F);
/* 1028 */   static final ASCIIToBinaryConverter A2BC_POSITIVE_ZERO = new PreparedASCIIToBinaryBuffer(0.0D, 0.0F);
/* 1029 */   static final ASCIIToBinaryConverter A2BC_NEGATIVE_ZERO = new PreparedASCIIToBinaryBuffer(-0.0D, -0.0F);
/*      */   
/*      */ 
/*      */   static class ASCIIToBinaryBuffer
/*      */     implements ASCIIToBinaryConverter
/*      */   {
/*      */     boolean isNegative;
/*      */     int decExponent;
/*      */     char[] digits;
/*      */     int nDigits;
/*      */     
/*      */     ASCIIToBinaryBuffer(boolean paramBoolean, int paramInt1, char[] paramArrayOfChar, int paramInt2)
/*      */     {
/* 1042 */       this.isNegative = paramBoolean;
/* 1043 */       this.decExponent = paramInt1;
/* 1044 */       this.digits = paramArrayOfChar;
/* 1045 */       this.nDigits = paramInt2;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public double doubleValue()
/*      */     {
/* 1058 */       int i = Math.min(this.nDigits, 16);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1063 */       int j = this.digits[0] - '0';
/* 1064 */       int k = Math.min(i, 9);
/* 1065 */       for (int m = 1; m < k; m++) {
/* 1066 */         j = j * 10 + this.digits[m] - 48;
/*      */       }
/* 1068 */       long l1 = j;
/* 1069 */       for (int n = k; n < i; n++) {
/* 1070 */         l1 = l1 * 10L + (this.digits[n] - '0');
/*      */       }
/* 1072 */       double d1 = l1;
/* 1073 */       int i1 = this.decExponent - i;
/*      */       
/*      */ 
/*      */ 
/*      */       double d4;
/*      */       
/*      */ 
/* 1080 */       if (this.nDigits <= 15)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1091 */         if ((i1 == 0) || (d1 == 0.0D)) {
/* 1092 */           return this.isNegative ? -d1 : d1;
/*      */         }
/* 1094 */         if (i1 >= 0) {
/* 1095 */           if (i1 <= MAX_SMALL_TEN)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1100 */             double d2 = d1 * SMALL_10_POW[i1];
/* 1101 */             return this.isNegative ? -d2 : d2;
/*      */           }
/* 1103 */           int i2 = 15 - i;
/* 1104 */           if (i1 <= MAX_SMALL_TEN + i2)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1111 */             d1 *= SMALL_10_POW[i2];
/* 1112 */             d4 = d1 * SMALL_10_POW[(i1 - i2)];
/* 1113 */             return this.isNegative ? -d4 : d4;
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 1119 */         else if (i1 >= -MAX_SMALL_TEN)
/*      */         {
/*      */ 
/*      */ 
/* 1123 */           double d3 = d1 / SMALL_10_POW[(-i1)];
/* 1124 */           return this.isNegative ? -d3 : d3;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       int i3;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1140 */       if (i1 > 0) {
/* 1141 */         if (this.decExponent > 309)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1146 */           return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
/*      */         }
/* 1148 */         if ((i1 & 0xF) != 0) {
/* 1149 */           d1 *= SMALL_10_POW[(i1 & 0xF)];
/*      */         }
/* 1151 */         if (i1 >>= 4 != 0)
/*      */         {
/* 1153 */           for (i3 = 0; i1 > 1; i1 >>= 1) {
/* 1154 */             if ((i1 & 0x1) != 0) {
/* 1155 */               d1 *= BIG_10_POW[i3];
/*      */             }
/* 1153 */             i3++;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1164 */           d4 = d1 * BIG_10_POW[i3];
/* 1165 */           if (Double.isInfinite(d4))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1178 */             d4 = d1 / 2.0D;
/* 1179 */             d4 *= BIG_10_POW[i3];
/* 1180 */             if (Double.isInfinite(d4)) {
/* 1181 */               return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
/*      */             }
/* 1183 */             d4 = Double.MAX_VALUE;
/*      */           }
/* 1185 */           d1 = d4;
/*      */         }
/* 1187 */       } else if (i1 < 0) {
/* 1188 */         i1 = -i1;
/* 1189 */         if (this.decExponent < 65211)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1194 */           return this.isNegative ? -0.0D : 0.0D;
/*      */         }
/* 1196 */         if ((i1 & 0xF) != 0) {
/* 1197 */           d1 /= SMALL_10_POW[(i1 & 0xF)];
/*      */         }
/* 1199 */         if (i1 >>= 4 != 0)
/*      */         {
/* 1201 */           for (i3 = 0; i1 > 1; i1 >>= 1) {
/* 1202 */             if ((i1 & 0x1) != 0) {
/* 1203 */               d1 *= TINY_10_POW[i3];
/*      */             }
/* 1201 */             i3++;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1212 */           d4 = d1 * TINY_10_POW[i3];
/* 1213 */           if (d4 == 0.0D)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1226 */             d4 = d1 * 2.0D;
/* 1227 */             d4 *= TINY_10_POW[i3];
/* 1228 */             if (d4 == 0.0D) {
/* 1229 */               return this.isNegative ? -0.0D : 0.0D;
/*      */             }
/* 1231 */             d4 = Double.MIN_VALUE;
/*      */           }
/* 1233 */           d1 = d4;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1244 */       if (this.nDigits > 1100) {
/* 1245 */         this.nDigits = 1101;
/* 1246 */         this.digits['ь'] = '1';
/*      */       }
/* 1248 */       FDBigInteger localFDBigInteger1 = new FDBigInteger(l1, this.digits, i, this.nDigits);
/* 1249 */       i1 = this.decExponent - this.nDigits;
/*      */       
/* 1251 */       long l2 = Double.doubleToRawLongBits(d1);
/* 1252 */       int i4 = Math.max(0, -i1);
/* 1253 */       int i5 = Math.max(0, i1);
/* 1254 */       localFDBigInteger1 = localFDBigInteger1.multByPow52(i5, 0);
/* 1255 */       localFDBigInteger1.makeImmutable();
/* 1256 */       FDBigInteger localFDBigInteger2 = null;
/* 1257 */       int i6 = 0;
/*      */       
/*      */ 
/*      */       for (;;)
/*      */       {
/* 1262 */         int i7 = (int)(l2 >>> 52);
/* 1263 */         long l3 = l2 & 0xFFFFFFFFFFFFF;
/* 1264 */         if (i7 > 0) {
/* 1265 */           l3 |= 0x10000000000000;
/*      */         } else {
/* 1267 */           assert (l3 != 0L) : l3;
/* 1268 */           i8 = Long.numberOfLeadingZeros(l3);
/* 1269 */           i9 = i8 - 11;
/* 1270 */           l3 <<= i9;
/* 1271 */           i7 = 1 - i9;
/*      */         }
/* 1273 */         i7 -= 1023;
/* 1274 */         int i8 = Long.numberOfTrailingZeros(l3);
/* 1275 */         l3 >>>= i8;
/* 1276 */         int i9 = i7 - 52 + i8;
/* 1277 */         int i10 = 53 - i8;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1288 */         int i11 = i4;
/* 1289 */         int i12 = i5;
/*      */         
/* 1291 */         if (i9 >= 0) {
/* 1292 */           i11 += i9;
/*      */         } else {
/* 1294 */           i12 -= i9;
/*      */         }
/* 1296 */         int i13 = i11;
/*      */         
/*      */         int i14;
/*      */         
/* 1300 */         if (i7 <= 64513)
/*      */         {
/*      */ 
/*      */ 
/* 1304 */           i14 = i7 + i8 + 1023;
/*      */         } else {
/* 1306 */           i14 = 1 + i8;
/*      */         }
/* 1308 */         i11 += i14;
/* 1309 */         i12 += i14;
/*      */         
/*      */ 
/* 1312 */         int i15 = Math.min(i11, Math.min(i12, i13));
/* 1313 */         i11 -= i15;
/* 1314 */         i12 -= i15;
/* 1315 */         i13 -= i15;
/*      */         
/* 1317 */         FDBigInteger localFDBigInteger3 = FDBigInteger.valueOfMulPow52(l3, i4, i11);
/* 1318 */         if ((localFDBigInteger2 == null) || (i6 != i12)) {
/* 1319 */           localFDBigInteger2 = localFDBigInteger1.leftShift(i12);
/* 1320 */           i6 = i12;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         int i17;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         FDBigInteger localFDBigInteger4;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1339 */         if ((i16 = localFDBigInteger3.cmp(localFDBigInteger2)) > 0) {
/* 1340 */           i17 = 1;
/* 1341 */           localFDBigInteger4 = localFDBigInteger3.leftInplaceSub(localFDBigInteger2);
/* 1342 */           if ((i10 == 1) && (i9 > 64514))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1347 */             i13--;
/* 1348 */             if (i13 < 0)
/*      */             {
/*      */ 
/* 1351 */               i13 = 0;
/* 1352 */               localFDBigInteger4 = localFDBigInteger4.leftShift(1);
/*      */             }
/*      */           }
/* 1355 */         } else { if (i16 >= 0) break;
/* 1356 */           i17 = 0;
/* 1357 */           localFDBigInteger4 = localFDBigInteger2.rightInplaceSub(localFDBigInteger3);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1363 */         int i16 = localFDBigInteger4.cmpPow52(i4, i13);
/* 1364 */         if (i16 >= 0)
/*      */         {
/*      */ 
/*      */ 
/* 1368 */           if (i16 == 0)
/*      */           {
/*      */ 
/* 1371 */             if ((l2 & 1L) != 0L) {
/* 1372 */               l2 += (i17 != 0 ? -1L : 1L);
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 1380 */             l2 += (i17 != 0 ? -1L : 1L);
/* 1381 */             if (l2 != 0L) { if (l2 == 9218868437227405312L) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1388 */       if (this.isNegative) {
/* 1389 */         l2 |= 0x8000000000000000;
/*      */       }
/* 1391 */       return Double.longBitsToDouble(l2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public float floatValue()
/*      */     {
/* 1405 */       int i = Math.min(this.nDigits, 8);
/*      */       
/*      */ 
/*      */ 
/* 1409 */       int j = this.digits[0] - '0';
/* 1410 */       for (int k = 1; k < i; k++) {
/* 1411 */         j = j * 10 + this.digits[k] - 48;
/*      */       }
/* 1413 */       float f = j;
/* 1414 */       int m = this.decExponent - i;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1421 */       if (this.nDigits <= 7)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1432 */         if ((m == 0) || (f == 0.0F))
/* 1433 */           return this.isNegative ? -f : f;
/* 1434 */         if (m >= 0) {
/* 1435 */           if (m <= SINGLE_MAX_SMALL_TEN)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1440 */             f *= SINGLE_SMALL_10_POW[m];
/* 1441 */             return this.isNegative ? -f : f;
/*      */           }
/* 1443 */           int n = 7 - i;
/* 1444 */           if (m <= SINGLE_MAX_SMALL_TEN + n)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1451 */             f *= SINGLE_SMALL_10_POW[n];
/* 1452 */             f *= SINGLE_SMALL_10_POW[(m - n)];
/* 1453 */             return this.isNegative ? -f : f;
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 1459 */         else if (m >= -SINGLE_MAX_SMALL_TEN)
/*      */         {
/*      */ 
/*      */ 
/* 1463 */           f /= SINGLE_SMALL_10_POW[(-m)];
/* 1464 */           return this.isNegative ? -f : f;
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/* 1470 */       else if ((this.decExponent >= this.nDigits) && (this.nDigits + this.decExponent <= 15))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1480 */         long l = j;
/* 1481 */         for (int i1 = i; i1 < this.nDigits; i1++) {
/* 1482 */           l = l * 10L + (this.digits[i1] - '0');
/*      */         }
/* 1484 */         double d2 = l;
/* 1485 */         m = this.decExponent - this.nDigits;
/* 1486 */         d2 *= SMALL_10_POW[m];
/* 1487 */         f = (float)d2;
/* 1488 */         return this.isNegative ? -f : f;
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
/* 1500 */       double d1 = f;
/* 1501 */       int i2; if (m > 0) {
/* 1502 */         if (this.decExponent > 39)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1507 */           return this.isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
/*      */         }
/* 1509 */         if ((m & 0xF) != 0) {
/* 1510 */           d1 *= SMALL_10_POW[(m & 0xF)];
/*      */         }
/* 1512 */         if (m >>= 4 != 0)
/*      */         {
/* 1514 */           for (i2 = 0; m > 0; m >>= 1) {
/* 1515 */             if ((m & 0x1) != 0) {
/* 1516 */               d1 *= BIG_10_POW[i2];
/*      */             }
/* 1514 */             i2++;
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/* 1520 */       else if (m < 0) {
/* 1521 */         m = -m;
/* 1522 */         if (this.decExponent < -46)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1527 */           return this.isNegative ? -0.0F : 0.0F;
/*      */         }
/* 1529 */         if ((m & 0xF) != 0) {
/* 1530 */           d1 /= SMALL_10_POW[(m & 0xF)];
/*      */         }
/* 1532 */         if (m >>= 4 != 0)
/*      */         {
/* 1534 */           for (i2 = 0; m > 0; m >>= 1) {
/* 1535 */             if ((m & 0x1) != 0) {
/* 1536 */               d1 *= TINY_10_POW[i2];
/*      */             }
/* 1534 */             i2++;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1541 */       f = Math.max(Float.MIN_VALUE, Math.min(Float.MAX_VALUE, (float)d1));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1550 */       if (this.nDigits > 200) {
/* 1551 */         this.nDigits = 201;
/* 1552 */         this.digits['È'] = '1';
/*      */       }
/* 1554 */       FDBigInteger localFDBigInteger1 = new FDBigInteger(j, this.digits, i, this.nDigits);
/* 1555 */       m = this.decExponent - this.nDigits;
/*      */       
/* 1557 */       int i3 = Float.floatToRawIntBits(f);
/* 1558 */       int i4 = Math.max(0, -m);
/* 1559 */       int i5 = Math.max(0, m);
/* 1560 */       localFDBigInteger1 = localFDBigInteger1.multByPow52(i5, 0);
/* 1561 */       localFDBigInteger1.makeImmutable();
/* 1562 */       FDBigInteger localFDBigInteger2 = null;
/* 1563 */       int i6 = 0;
/*      */       
/*      */ 
/*      */       for (;;)
/*      */       {
/* 1568 */         int i7 = i3 >>> 23;
/* 1569 */         int i8 = i3 & 0x7FFFFF;
/* 1570 */         if (i7 > 0) {
/* 1571 */           i8 |= 0x800000;
/*      */         } else {
/* 1573 */           assert (i8 != 0) : i8;
/* 1574 */           i9 = Integer.numberOfLeadingZeros(i8);
/* 1575 */           i10 = i9 - 8;
/* 1576 */           i8 <<= i10;
/* 1577 */           i7 = 1 - i10;
/*      */         }
/* 1579 */         i7 -= 127;
/* 1580 */         int i9 = Integer.numberOfTrailingZeros(i8);
/* 1581 */         i8 >>>= i9;
/* 1582 */         int i10 = i7 - 23 + i9;
/* 1583 */         int i11 = 24 - i9;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1594 */         int i12 = i4;
/* 1595 */         int i13 = i5;
/*      */         
/* 1597 */         if (i10 >= 0) {
/* 1598 */           i12 += i10;
/*      */         } else {
/* 1600 */           i13 -= i10;
/*      */         }
/* 1602 */         int i14 = i12;
/*      */         
/*      */         int i15;
/*      */         
/* 1606 */         if (i7 <= -127)
/*      */         {
/*      */ 
/*      */ 
/* 1610 */           i15 = i7 + i9 + 127;
/*      */         } else {
/* 1612 */           i15 = 1 + i9;
/*      */         }
/* 1614 */         i12 += i15;
/* 1615 */         i13 += i15;
/*      */         
/*      */ 
/* 1618 */         int i16 = Math.min(i12, Math.min(i13, i14));
/* 1619 */         i12 -= i16;
/* 1620 */         i13 -= i16;
/* 1621 */         i14 -= i16;
/*      */         
/* 1623 */         FDBigInteger localFDBigInteger3 = FDBigInteger.valueOfMulPow52(i8, i4, i12);
/* 1624 */         if ((localFDBigInteger2 == null) || (i6 != i13)) {
/* 1625 */           localFDBigInteger2 = localFDBigInteger1.leftShift(i13);
/* 1626 */           i6 = i13;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         int i18;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         FDBigInteger localFDBigInteger4;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1645 */         if ((i17 = localFDBigInteger3.cmp(localFDBigInteger2)) > 0) {
/* 1646 */           i18 = 1;
/* 1647 */           localFDBigInteger4 = localFDBigInteger3.leftInplaceSub(localFDBigInteger2);
/* 1648 */           if ((i11 == 1) && (i10 > -126))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1653 */             i14--;
/* 1654 */             if (i14 < 0)
/*      */             {
/*      */ 
/* 1657 */               i14 = 0;
/* 1658 */               localFDBigInteger4 = localFDBigInteger4.leftShift(1);
/*      */             }
/*      */           }
/* 1661 */         } else { if (i17 >= 0) break;
/* 1662 */           i18 = 0;
/* 1663 */           localFDBigInteger4 = localFDBigInteger2.rightInplaceSub(localFDBigInteger3);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1669 */         int i17 = localFDBigInteger4.cmpPow52(i4, i14);
/* 1670 */         if (i17 >= 0)
/*      */         {
/*      */ 
/*      */ 
/* 1674 */           if (i17 == 0)
/*      */           {
/*      */ 
/* 1677 */             if ((i3 & 0x1) != 0) {
/* 1678 */               i3 += (i18 != 0 ? -1 : 1);
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 1686 */             i3 += (i18 != 0 ? -1 : 1);
/* 1687 */             if (i3 != 0) { if (i3 == 2139095040) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1694 */       if (this.isNegative) {
/* 1695 */         i3 |= 0x80000000;
/*      */       }
/* 1697 */       return Float.intBitsToFloat(i3);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1705 */     private static final double[] SMALL_10_POW = { 1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1714 */     private static final float[] SINGLE_SMALL_10_POW = { 1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 1.0E10F };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1720 */     private static final double[] BIG_10_POW = { 1.0E16D, 1.0E32D, 1.0E64D, 1.0E128D, 1.0E256D };
/*      */     
/* 1722 */     private static final double[] TINY_10_POW = { 1.0E-16D, 1.0E-32D, 1.0E-64D, 1.0E-128D, 1.0E-256D };
/*      */     
/*      */ 
/* 1725 */     private static final int MAX_SMALL_TEN = SMALL_10_POW.length - 1;
/* 1726 */     private static final int SINGLE_MAX_SMALL_TEN = SINGLE_SMALL_10_POW.length - 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static BinaryToASCIIConverter getBinaryToASCIIConverter(double paramDouble)
/*      */   {
/* 1738 */     return getBinaryToASCIIConverter(paramDouble, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static BinaryToASCIIConverter getBinaryToASCIIConverter(double paramDouble, boolean paramBoolean)
/*      */   {
/* 1750 */     long l1 = Double.doubleToRawLongBits(paramDouble);
/* 1751 */     boolean bool = (l1 & 0x8000000000000000) != 0L;
/* 1752 */     long l2 = l1 & 0xFFFFFFFFFFFFF;
/* 1753 */     int i = (int)((l1 & 0x7FF0000000000000) >> 52);
/*      */     
/* 1755 */     if (i == 2047) {
/* 1756 */       if (l2 == 0L) {
/* 1757 */         return bool ? B2AC_NEGATIVE_INFINITY : B2AC_POSITIVE_INFINITY;
/*      */       }
/* 1759 */       return B2AC_NOT_A_NUMBER;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     int j;
/*      */     
/*      */ 
/* 1767 */     if (i == 0) {
/* 1768 */       if (l2 == 0L)
/*      */       {
/* 1770 */         return bool ? B2AC_NEGATIVE_ZERO : B2AC_POSITIVE_ZERO;
/*      */       }
/* 1772 */       int k = Long.numberOfLeadingZeros(l2);
/* 1773 */       int m = k - 11;
/* 1774 */       l2 <<= m;
/* 1775 */       i = 1 - m;
/* 1776 */       j = 64 - k;
/*      */     } else {
/* 1778 */       l2 |= 0x10000000000000;
/* 1779 */       j = 53;
/*      */     }
/* 1781 */     i -= 1023;
/* 1782 */     BinaryToASCIIBuffer localBinaryToASCIIBuffer = getBinaryToASCIIBuffer();
/* 1783 */     localBinaryToASCIIBuffer.setSign(bool);
/*      */     
/* 1785 */     localBinaryToASCIIBuffer.dtoa(i, l2, j, paramBoolean);
/* 1786 */     return localBinaryToASCIIBuffer;
/*      */   }
/*      */   
/*      */   private static BinaryToASCIIConverter getBinaryToASCIIConverter(float paramFloat) {
               // 获取 float 的计算机二进制编码的 int 值
/* 1790 */     int i = Float.floatToRawIntBits(paramFloat);
               // 判断最高位是否为 0, S
/* 1791 */     boolean bool = (i & 0x80000000) != 0;
               // 获取低 23 位的有效数字, M
/* 1792 */     int j = i & 0x7FFFFF;
               // 获取 8 位指数位，E
/* 1793 */     int k = (i & 0x7F800000) >> 23;
/*      */
               // 如果 E 为全 1
/* 1795 */     if (k == 255) {
                  // 判断 M 是否为全 0，如果 M 为全 0, 则表示正负无穷大，否则该 paramFloat 不是一个数
/* 1796 */       if (j == 0L) {
                   // 根据最高位的实数符号来判断是正无穷大还是负无穷大
/* 1797 */         return bool ? B2AC_NEGATIVE_INFINITY : B2AC_POSITIVE_INFINITY;
/*      */       }
/* 1799 */       return B2AC_NOT_A_NUMBER; // 否则返回 NaN
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     int m;
/*      */     
/*      */ 
/* 1807 */     if (k == 0) { // 如果 E 为全 0，M 在计算的时候不在加 1，按非规约数处理
/* 1808 */       if (j == 0) // 判断 M 是否为 0
/*      */       {
/* 1810 */         return bool ? B2AC_NEGATIVE_ZERO : B2AC_POSITIVE_ZERO; // 根据符号返回正负 0，对于 paramFloat 而言都是 0
/*      */       }
/* 1812 */       int n = Integer.numberOfLeadingZeros(j); // 获取 int 类型的有效数字 M 的小数部分高位 0 的个数，这里 j 为 32 位
/* 1813 */       int i1 = n - 8; // 获取有效数字 M 的小数部分高位 0 的个数 + 1
/* 1814 */       j <<= i1; // 将 j 左移，非 0 数字置顶(24 位)
                 // 对于非规约数，IEEE 754 中规定：非规约数的浮点数的指数偏移值比规约形式的浮点数的指数偏移值小 1，所以这里相对规约数要多加 1
/* 1815 */       k = 1 - i1; // 对于非规约数，计算 E 的值(后面要减去 127)
/* 1816 */       m = 32 - n; // 有效数字的非 0 位个数
/*      */     } else {
/* 1818 */       j |= 0x800000; // M 的第 24 位 + 1，得到原始的 M 值的二进制
/* 1819 */       m = 24; // j 的有效非 0 数字位个数
/*      */     }
/* 1821 */     k -= 127; // E - 127 得到原始的 E 值，因为 E 在存入内存的时候会加上一个中间值，32 位的中间值为 127
/* 1822 */     BinaryToASCIIBuffer localBinaryToASCIIBuffer = getBinaryToASCIIBuffer();
/* 1823 */     localBinaryToASCIIBuffer.setSign(bool);
/*      */     // 这里 k - E, j << 29 是因为这个方法里有个断言 j 的第 53 位为 1，m - j 的有效数字的长度
/* 1825 */     localBinaryToASCIIBuffer.dtoa(k, j << 29, m, true);
/* 1826 */     return localBinaryToASCIIBuffer;
/*      */   }
/*      */   
/*      */   static ASCIIToBinaryConverter readJavaFormatString(String paramString) throws NumberFormatException
/*      */   {
/* 1831 */     boolean bool = false;
/* 1832 */     int i = 0;
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1838 */       paramString = paramString.trim();
/*      */       
/* 1840 */       int m = paramString.length();
/* 1841 */       if (m == 0) {
/* 1842 */         throw new NumberFormatException("empty String");
/*      */       }
/* 1844 */       int n = 0;
/* 1845 */       switch (paramString.charAt(n)) {
/*      */       case '-': 
/* 1847 */         bool = true;
/*      */       
/*      */       case '+': 
/* 1850 */         n++;
/* 1851 */         i = 1;
/*      */       }
/* 1853 */       int k = paramString.charAt(n);
/* 1854 */       if (k == 78) {
/* 1855 */         if ((m - n == NAN_LENGTH) && (paramString.indexOf("NaN", n) == n)) {
/* 1856 */           return A2BC_NOT_A_NUMBER;
/*      */         }
/*      */         
/*      */       }
/* 1860 */       else if (k == 73) {
/* 1861 */         if ((m - n == INFINITY_LENGTH) && (paramString.indexOf("Infinity", n) == n)) {
/* 1862 */           return bool ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_INFINITY;
/*      */         }
/*      */       }
/*      */       else {
/* 1866 */         if ((k == 48) && 
/* 1867 */           (m > n + 1)) {
/* 1868 */           int i1 = paramString.charAt(n + 1);
/* 1869 */           if ((i1 == 120) || (i1 == 88)) {
/* 1870 */             return parseHexString(paramString);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1875 */         char[] arrayOfChar = new char[m];
/* 1876 */         int i2 = 0;
/* 1877 */         int i3 = 0;
/* 1878 */         int i4 = 0;
/* 1879 */         int i5 = 0;
/* 1880 */         int i6 = 0;
/*      */         
/*      */ 
/* 1883 */         while (n < m) {
/* 1884 */           k = paramString.charAt(n);
/* 1885 */           if (k == 48) {
/* 1886 */             i5++;
/* 1887 */           } else { if (k != 46) break;
/* 1888 */             if (i3 != 0)
/*      */             {
/* 1890 */               throw new NumberFormatException("multiple points");
/*      */             }
/* 1892 */             i4 = n;
/* 1893 */             if (i != 0) {
/* 1894 */               i4--;
/*      */             }
/* 1896 */             i3 = 1;
/*      */           }
/*      */           
/*      */ 
/* 1900 */           n++;
/*      */         }
/*      */         
/* 1903 */         while (n < m) {
/* 1904 */           k = paramString.charAt(n);
/* 1905 */           if ((k >= 49) && (k <= 57)) {
/* 1906 */             arrayOfChar[(i2++)] = k;
/* 1907 */             i6 = 0;
/* 1908 */           } else if (k == 48) {
/* 1909 */             arrayOfChar[(i2++)] = k;
/* 1910 */             i6++;
/* 1911 */           } else { if (k != 46) break;
/* 1912 */             if (i3 != 0)
/*      */             {
/* 1914 */               throw new NumberFormatException("multiple points");
/*      */             }
/* 1916 */             i4 = n;
/* 1917 */             if (i != 0) {
/* 1918 */               i4--;
/*      */             }
/* 1920 */             i3 = 1;
/*      */           }
/*      */           
/*      */ 
/* 1924 */           n++;
/*      */         }
/* 1926 */         i2 -= i6;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1946 */         int i7 = i2 == 0 ? 1 : 0;
/* 1947 */         if ((i7 == 0) || (i5 != 0))
/*      */         {
/*      */           int j;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1958 */           if (i3 != 0) {
/* 1959 */             j = i4 - i5;
/*      */           } else {
/* 1961 */             j = i2 + i6;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1967 */           if ((n < m) && (((k = paramString.charAt(n)) == 'e') || (k == 69))) {
/* 1968 */             int i8 = 1;
/* 1969 */             int i9 = 0;
/* 1970 */             int i10 = 214748364;
/* 1971 */             int i11 = 0;
/* 1972 */             switch (paramString.charAt(++n)) {
/*      */             case '-': 
/* 1974 */               i8 = -1;
/*      */             
/*      */             case '+': 
/* 1977 */               n++;
/*      */             }
/* 1979 */             int i12 = n;
/*      */             
/* 1981 */             while (n < m) {
/* 1982 */               if (i9 >= i10)
/*      */               {
/*      */ 
/* 1985 */                 i11 = 1;
/*      */               }
/* 1987 */               k = paramString.charAt(n++);
/* 1988 */               if ((k >= 48) && (k <= 57)) {
/* 1989 */                 i9 = i9 * 10 + (k - 48);
/*      */               } else {
/* 1991 */                 n--;
/*      */               }
/*      */             }
/*      */             
/* 1995 */             int i13 = 324 + i2 + i6;
/* 1996 */             if ((i11 != 0) || (i9 > i13))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2009 */               j = i8 * i13;
/*      */             }
/*      */             else
/*      */             {
/* 2013 */               j += i8 * i9;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2022 */             if (n == i12) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/* 2030 */           else if ((n >= m) || ((n != m - 1) || (
/*      */           
/* 2032 */             (paramString.charAt(n) == 'f') || 
/* 2033 */             (paramString.charAt(n) == 'F') || 
/* 2034 */             (paramString.charAt(n) == 'd') || 
/* 2035 */             (paramString.charAt(n) == 'D'))))
/*      */           {
/*      */ 
/* 2038 */             if (i7 != 0) {
/* 2039 */               return bool ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO;
/*      */             }
/* 2041 */             return new ASCIIToBinaryBuffer(bool, j, arrayOfChar, i2);
/*      */           }
/* 2043 */         } } } catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) { throw new NumberFormatException("For input string: \"" + paramString + "\"");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class HexFloatPattern
/*      */   {
/* 2051 */     private static final Pattern VALUE = Pattern.compile("([-+])?0[xX](((\\p{XDigit}+)\\.?)|((\\p{XDigit}*)\\.(\\p{XDigit}+)))[pP]([-+])?(\\p{Digit}+)[fFdD]?");
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
/*      */   static ASCIIToBinaryConverter parseHexString(String paramString)
/*      */   {
/* 2067 */     Matcher localMatcher = HexFloatPattern.VALUE.matcher(paramString);
/* 2068 */     boolean bool = localMatcher.matches();
/* 2069 */     if (!bool)
/*      */     {
/* 2071 */       throw new NumberFormatException("For input string: \"" + paramString + "\"");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2097 */     String str1 = localMatcher.group(1);
/* 2098 */     int i = (str1 != null) && (str1.equals("-")) ? 1 : 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2129 */     String str2 = null;
/* 2130 */     int j = 0;
/* 2131 */     int k = 0;
/*      */     
/* 2133 */     int m = 0;
/*      */     
/*      */ 
/* 2136 */     int n = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     String str4;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2150 */     if ((str4 = localMatcher.group(4)) != null)
/*      */     {
/* 2152 */       str2 = stripLeadingZeros(str4);
/* 2153 */       m = str2.length();
/*      */     }
/*      */     else
/*      */     {
/* 2157 */       String str5 = stripLeadingZeros(localMatcher.group(6));
/* 2158 */       m = str5.length();
/*      */       
/*      */ 
/* 2161 */       String str6 = localMatcher.group(7);
/* 2162 */       n = str6.length();
/*      */       
/*      */ 
/* 2165 */       str2 = (str5 == null ? "" : str5) + str6;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2171 */     str2 = stripLeadingZeros(str2);
/* 2172 */     j = str2.length();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2177 */     if (m >= 1) {
/* 2178 */       k = 4 * (m - 1);
/*      */     } else {
/* 2180 */       k = -4 * (n - j + 1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2186 */     if (j == 0) {
/* 2187 */       return i != 0 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2199 */     String str3 = localMatcher.group(8);
/* 2200 */     n = (str3 == null) || (str3.equals("+")) ? 1 : 0;
/*      */     long l1;
/*      */     try {
/* 2203 */       l1 = Integer.parseInt(localMatcher.group(9));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2219 */       return n != 0 ? A2BC_POSITIVE_INFINITY : i != 0 ? A2BC_NEGATIVE_ZERO : n != 0 ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_ZERO;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2225 */     long l2 = (n != 0 ? 1L : -1L) * l1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2230 */     long l3 = l2 + k;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2236 */     int i1 = 0;
/* 2237 */     int i2 = 0;
/* 2238 */     int i3 = 0;
/* 2239 */     long l4 = 0L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2246 */     long l5 = getHexDigit(str2, 0);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2257 */     if (l5 == 1L) {
/* 2258 */       l4 |= l5 << 52;
/* 2259 */       i3 = 48;
/*      */     }
/* 2261 */     else if (l5 <= 3L) {
/* 2262 */       l4 |= l5 << 51;
/* 2263 */       i3 = 47;
/* 2264 */       l3 += 1L;
/* 2265 */     } else if (l5 <= 7L) {
/* 2266 */       l4 |= l5 << 50;
/* 2267 */       i3 = 46;
/* 2268 */       l3 += 2L;
/* 2269 */     } else if (l5 <= 15L) {
/* 2270 */       l4 |= l5 << 49;
/* 2271 */       i3 = 45;
/* 2272 */       l3 += 3L;
/*      */     } else {
/* 2274 */       throw new AssertionError("Result from digit conversion too large!");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2295 */     int i4 = 0;
/* 2296 */     long l6; for (i4 = 1; 
/* 2297 */         (i4 < j) && (i3 >= 0); 
/* 2298 */         i4++) {
/* 2299 */       l6 = getHexDigit(str2, i4);
/* 2300 */       l4 |= l6 << i3;
/* 2301 */       i3 -= 4;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2309 */     if (i4 < j) {
/* 2310 */       l6 = getHexDigit(str2, i4);
/*      */       
/*      */ 
/*      */ 
/* 2314 */       switch (i3)
/*      */       {
/*      */ 
/*      */       case -1: 
/* 2318 */         l4 |= (l6 & 0xE) >> 1;
/* 2319 */         i1 = (l6 & 1L) != 0L ? 1 : 0;
/* 2320 */         break;
/*      */       
/*      */ 
/*      */ 
/*      */       case -2: 
/* 2325 */         l4 |= (l6 & 0xC) >> 2;
/* 2326 */         i1 = (l6 & 0x2) != 0L ? 1 : 0;
/* 2327 */         i2 = (l6 & 1L) != 0L ? 1 : 0;
/* 2328 */         break;
/*      */       
/*      */ 
/*      */       case -3: 
/* 2332 */         l4 |= (l6 & 0x8) >> 3;
/*      */         
/* 2334 */         i1 = (l6 & 0x4) != 0L ? 1 : 0;
/* 2335 */         i2 = (l6 & 0x3) != 0L ? 1 : 0;
/* 2336 */         break;
/*      */       
/*      */ 
/*      */ 
/*      */       case -4: 
/* 2341 */         i1 = (l6 & 0x8) != 0L ? 1 : 0;
/*      */         
/* 2343 */         i2 = (l6 & 0x7) != 0L ? 1 : 0;
/* 2344 */         break;
/*      */       
/*      */       default: 
/* 2347 */         throw new AssertionError("Unexpected shift distance remainder.");
/*      */       }
/*      */       
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2356 */       i4++;
/* 2357 */       while ((i4 < j) && (i2 == 0)) {
/* 2358 */         l6 = getHexDigit(str2, i4);
/* 2359 */         i2 = (i2 != 0) || (l6 != 0L) ? 1 : 0;
/* 2360 */         i4++;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2368 */     int i5 = i != 0 ? Integer.MIN_VALUE : 0;
/* 2369 */     int i6; int i8; if (l3 >= -126L) {
/* 2370 */       if (l3 > 127L)
/*      */       {
/* 2372 */         i5 |= 0x7F800000;
/*      */       } else {
/* 2374 */         i6 = 28;
/* 2375 */         i7 = ((l4 & (1L << i6) - 1L) != 0L) || (i1 != 0) || (i2 != 0) ? 1 : 0;
/* 2376 */         i8 = (int)(l4 >>> i6);
/* 2377 */         if (((i8 & 0x3) != 1) || (i7 != 0)) {
/* 2378 */           i8++;
/*      */         }
/* 2380 */         i5 |= ((int)l3 + 126 << 23) + (i8 >> 1);
/*      */       }
/*      */     }
/* 2383 */     else if (l3 >= -150L)
/*      */     {
/*      */ 
/*      */ 
/* 2387 */       i6 = (int)(-98L - l3);
/* 2388 */       assert (i6 >= 29);
/* 2389 */       assert (i6 < 53);
/* 2390 */       i7 = ((l4 & (1L << i6) - 1L) != 0L) || (i1 != 0) || (i2 != 0) ? 1 : 0;
/* 2391 */       i8 = (int)(l4 >>> i6);
/* 2392 */       if (((i8 & 0x3) != 1) || (i7 != 0)) {
/* 2393 */         i8++;
/*      */       }
/* 2395 */       i5 |= i8 >> 1;
/*      */     }
/*      */     
/* 2398 */     float f = Float.intBitsToFloat(i5);
/*      */     
/*      */ 
/* 2401 */     if (l3 > 1023L)
/*      */     {
/* 2403 */       return i != 0 ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_INFINITY;
/*      */     }
/* 2405 */     if ((l3 <= 1023L) && (l3 >= -1022L))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2419 */       l4 = l3 + 1023L << 52 & 0x7FF0000000000000 | 0xFFFFFFFFFFFFF & l4;
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/* 2428 */       if (l3 < -1075L)
/*      */       {
/*      */ 
/*      */ 
/* 2432 */         return i != 0 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2440 */       i2 = (i2 != 0) || (i1 != 0) ? 1 : 0;
/* 2441 */       i1 = 0;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2449 */       i7 = 53 - ((int)l3 - 64462 + 1);
/*      */       
/* 2451 */       assert ((i7 >= 1) && (i7 <= 53));
/*      */       
/*      */ 
/*      */ 
/* 2455 */       i1 = (l4 & 1L << i7 - 1) != 0L ? 1 : 0;
/* 2456 */       if (i7 > 1)
/*      */       {
/*      */ 
/* 2459 */         long l7 = -1L << i7 - 1 ^ 0xFFFFFFFFFFFFFFFF;
/* 2460 */         i2 = (i2 != 0) || ((l4 & l7) != 0L) ? 1 : 0;
/*      */       }
/*      */       
/*      */ 
/* 2464 */       l4 >>= i7;
/*      */       
/* 2466 */       l4 = 0L | 0xFFFFFFFFFFFFF & l4;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2498 */     int i7 = (l4 & 1L) == 0L ? 1 : 0;
/* 2499 */     if (((i7 != 0) && (i1 != 0) && (i2 != 0)) || ((i7 == 0) && (i1 != 0)))
/*      */     {
/* 2501 */       l4 += 1L;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2506 */     double d = i != 0 ? Double.longBitsToDouble(l4 | 0x8000000000000000) : Double.longBitsToDouble(l4);
/*      */     
/* 2508 */     return new PreparedASCIIToBinaryBuffer(d, f);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static String stripLeadingZeros(String paramString)
/*      */   {
/* 2518 */     if ((!paramString.isEmpty()) && (paramString.charAt(0) == '0')) {
/* 2519 */       for (int i = 1; i < paramString.length(); i++) {
/* 2520 */         if (paramString.charAt(i) != '0') {
/* 2521 */           return paramString.substring(i);
/*      */         }
/*      */       }
/* 2524 */       return "";
/*      */     }
/* 2526 */     return paramString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static int getHexDigit(String paramString, int paramInt)
/*      */   {
/* 2534 */     int i = Character.digit(paramString.charAt(paramInt), 16);
/* 2535 */     if ((i <= -1) || (i >= 16))
/*      */     {
/* 2537 */       throw new AssertionError("Unexpected failure of digit conversion of " + paramString.charAt(paramInt));
/*      */     }
/* 2539 */     return i;
/*      */   }
/*      */   
/*      */   static abstract interface ASCIIToBinaryConverter
/*      */   {
/*      */     public abstract double doubleValue();
/*      */     
/*      */     public abstract float floatValue();
/*      */   }
/*      */   
/*      */   public static abstract interface BinaryToASCIIConverter
/*      */   {
/*      */     public abstract String toJavaFormatString();
/*      */     
/*      */     public abstract void appendTo(Appendable paramAppendable);
/*      */     
/*      */     public abstract int getDecimalExponent();
/*      */     
/*      */     public abstract int getDigits(char[] paramArrayOfChar);
/*      */     
/*      */     public abstract boolean isNegative();
/*      */     
/*      */     public abstract boolean isExceptional();
/*      */     
/*      */     public abstract boolean digitsRoundedUp();
/*      */     
/*      */     public abstract boolean decimalDigitsExact();
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\FloatingDecimal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
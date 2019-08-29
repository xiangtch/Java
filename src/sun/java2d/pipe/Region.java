/*      */ package sun.java2d.pipe;
/*      */ 
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.RectangularShape;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Region
/*      */ {
/*      */   static final int INIT_SIZE = 50;
/*      */   static final int GROW_SIZE = 50;
/*      */   
/*      */   private static final class ImmutableRegion
/*      */     extends Region
/*      */   {
/*   74 */     protected ImmutableRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { super(paramInt2, paramInt3, paramInt4); }
/*      */     
/*      */     public void appendSpans(SpanIterator paramSpanIterator) {}
/*      */     
/*      */     public void setOutputArea(Rectangle paramRectangle) {}
/*      */     
/*      */     public void setOutputAreaXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
/*      */     
/*      */     public void setOutputArea(int[] paramArrayOfInt) {}
/*      */     
/*      */     public void setOutputAreaXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {} }
/*   85 */   public static final Region EMPTY_REGION = new ImmutableRegion(0, 0, 0, 0);
/*   86 */   public static final Region WHOLE_REGION = new ImmutableRegion(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
/*      */   
/*      */   int lox;
/*      */   
/*      */   int loy;
/*      */   
/*      */   int hix;
/*      */   
/*      */   int hiy;
/*      */   int endIndex;
/*      */   int[] bands;
/*      */   static final int INCLUDE_A = 1;
/*      */   static final int INCLUDE_B = 2;
/*      */   static final int INCLUDE_COMMON = 4;
/*      */   
/*      */   static
/*      */   {
/*  103 */     initIDs();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int dimAdd(int paramInt1, int paramInt2)
/*      */   {
/*  114 */     if (paramInt2 <= 0) return paramInt1;
/*  115 */     if (paramInt2 += paramInt1 < paramInt1) return Integer.MAX_VALUE;
/*  116 */     return paramInt2;
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
/*      */   public static int clipAdd(int paramInt1, int paramInt2)
/*      */   {
/*  129 */     int i = paramInt1 + paramInt2;
/*  130 */     if ((i > paramInt1 ? 1 : 0) != (paramInt2 > 0 ? 1 : 0)) {
/*  131 */       i = paramInt2 < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
/*      */     }
/*  133 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int clipScale(int paramInt, double paramDouble)
/*      */   {
/*  145 */     if (paramDouble == 1.0D) {
/*  146 */       return paramInt;
/*      */     }
/*  148 */     double d = paramInt * paramDouble;
/*  149 */     if (d < -2.147483648E9D) {
/*  150 */       return Integer.MIN_VALUE;
/*      */     }
/*  152 */     if (d > 2.147483647E9D) {
/*  153 */       return Integer.MAX_VALUE;
/*      */     }
/*  155 */     return (int)Math.round(d);
/*      */   }
/*      */   
/*      */   protected Region(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  159 */     this.lox = paramInt1;
/*  160 */     this.loy = paramInt2;
/*  161 */     this.hix = paramInt3;
/*  162 */     this.hiy = paramInt4;
/*      */   }
/*      */   
/*      */   private Region(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5) {
/*  166 */     this.lox = paramInt1;
/*  167 */     this.loy = paramInt2;
/*  168 */     this.hix = paramInt3;
/*  169 */     this.hiy = paramInt4;
/*  170 */     this.bands = paramArrayOfInt;
/*  171 */     this.endIndex = paramInt5;
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
/*      */   public static Region getInstance(Shape paramShape, AffineTransform paramAffineTransform)
/*      */   {
/*  187 */     return getInstance(WHOLE_REGION, false, paramShape, paramAffineTransform);
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
/*      */   public static Region getInstance(Region paramRegion, Shape paramShape, AffineTransform paramAffineTransform)
/*      */   {
/*  215 */     return getInstance(paramRegion, false, paramShape, paramAffineTransform);
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
/*      */   public static Region getInstance(Region paramRegion, boolean paramBoolean, Shape paramShape, AffineTransform paramAffineTransform)
/*      */   {
/*  249 */     if (((paramShape instanceof RectangularShape)) && 
/*  250 */       (((RectangularShape)paramShape).isEmpty()))
/*      */     {
/*  252 */       return EMPTY_REGION;
/*      */     }
/*      */     
/*  255 */     int[] arrayOfInt = new int[4];
/*  256 */     ShapeSpanIterator localShapeSpanIterator = new ShapeSpanIterator(paramBoolean);
/*      */     try {
/*  258 */       localShapeSpanIterator.setOutputArea(paramRegion);
/*  259 */       localShapeSpanIterator.appendPath(paramShape.getPathIterator(paramAffineTransform));
/*  260 */       localShapeSpanIterator.getPathBox(arrayOfInt);
/*  261 */       Region localRegion1 = getInstance(arrayOfInt);
/*  262 */       localRegion1.appendSpans(localShapeSpanIterator);
/*  263 */       return localRegion1;
/*      */     } finally {
/*  265 */       localShapeSpanIterator.dispose();
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
/*      */   static Region getInstance(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
/*      */   {
/*  279 */     int i = paramArrayOfInt[0];
/*  280 */     int j = paramArrayOfInt[1];
/*  281 */     if ((paramInt4 <= paramInt2) || (paramInt3 <= paramInt1) || (j <= i)) {
/*  282 */       return EMPTY_REGION;
/*      */     }
/*      */     
/*  285 */     int[] arrayOfInt = new int[(j - i) * 5];
/*  286 */     int k = 0;
/*  287 */     int m = 2;
/*  288 */     for (int n = i; n < j; n++) {
/*  289 */       int i1 = Math.max(clipAdd(paramInt1, paramArrayOfInt[(m++)]), paramInt1);
/*  290 */       int i2 = Math.min(clipAdd(paramInt1, paramArrayOfInt[(m++)]), paramInt3);
/*  291 */       if (i1 < i2) {
/*  292 */         int i3 = Math.max(clipAdd(paramInt2, n), paramInt2);
/*  293 */         int i4 = Math.min(clipAdd(i3, 1), paramInt4);
/*  294 */         if (i3 < i4) {
/*  295 */           arrayOfInt[(k++)] = i3;
/*  296 */           arrayOfInt[(k++)] = i4;
/*  297 */           arrayOfInt[(k++)] = 1;
/*  298 */           arrayOfInt[(k++)] = i1;
/*  299 */           arrayOfInt[(k++)] = i2;
/*      */         }
/*      */       }
/*      */     }
/*  303 */     return k != 0 ? new Region(paramInt1, paramInt2, paramInt3, paramInt4, arrayOfInt, k) : EMPTY_REGION;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Region getInstance(Rectangle paramRectangle)
/*      */   {
/*  315 */     return getInstanceXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Region getInstanceXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  326 */     return getInstanceXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Region getInstance(int[] paramArrayOfInt)
/*      */   {
/*  337 */     return new Region(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2], paramArrayOfInt[3]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Region getInstanceXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  348 */     return new Region(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setOutputArea(Rectangle paramRectangle)
/*      */   {
/*  359 */     setOutputAreaXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
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
/*      */   public void setOutputAreaXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  372 */     setOutputAreaXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setOutputArea(int[] paramArrayOfInt)
/*      */   {
/*  383 */     this.lox = paramArrayOfInt[0];
/*  384 */     this.loy = paramArrayOfInt[1];
/*  385 */     this.hix = paramArrayOfInt[2];
/*  386 */     this.hiy = paramArrayOfInt[3];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setOutputAreaXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  398 */     this.lox = paramInt1;
/*  399 */     this.loy = paramInt2;
/*  400 */     this.hix = paramInt3;
/*  401 */     this.hiy = paramInt4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void appendSpans(SpanIterator paramSpanIterator)
/*      */   {
/*  412 */     int[] arrayOfInt = new int[6];
/*      */     
/*  414 */     while (paramSpanIterator.nextSpan(arrayOfInt)) {
/*  415 */       appendSpan(arrayOfInt);
/*      */     }
/*      */     
/*  418 */     endRow(arrayOfInt);
/*  419 */     calcBBox();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Region getScaledRegion(double paramDouble1, double paramDouble2)
/*      */   {
/*  427 */     if ((paramDouble1 == 0.0D) || (paramDouble2 == 0.0D) || (this == EMPTY_REGION)) {
/*  428 */       return EMPTY_REGION;
/*      */     }
/*  430 */     if (((paramDouble1 == 1.0D) && (paramDouble2 == 1.0D)) || (this == WHOLE_REGION)) {
/*  431 */       return this;
/*      */     }
/*      */     
/*  434 */     int i = clipScale(this.lox, paramDouble1);
/*  435 */     int j = clipScale(this.loy, paramDouble2);
/*  436 */     int k = clipScale(this.hix, paramDouble1);
/*  437 */     int m = clipScale(this.hiy, paramDouble2);
/*  438 */     Region localRegion = new Region(i, j, k, m);
/*  439 */     int[] arrayOfInt1 = this.bands;
/*  440 */     if (arrayOfInt1 != null) {
/*  441 */       int n = this.endIndex;
/*  442 */       int[] arrayOfInt2 = new int[n];
/*  443 */       int i1 = 0;
/*  444 */       int i2 = 0;
/*      */       
/*  446 */       while (i1 < n) {
/*      */         int i4;
/*  448 */         arrayOfInt2[(i2++)] = (i4 = clipScale(arrayOfInt1[(i1++)], paramDouble2));
/*  449 */         int i5; arrayOfInt2[(i2++)] = (i5 = clipScale(arrayOfInt1[(i1++)], paramDouble2));
/*  450 */         int i3; arrayOfInt2[(i2++)] = (i3 = arrayOfInt1[(i1++)]);
/*  451 */         int i6 = i2;
/*  452 */         if (i4 < i5) {
/*  453 */           for (;;) { i3--; if (i3 < 0) break;
/*  454 */             int i7 = clipScale(arrayOfInt1[(i1++)], paramDouble1);
/*  455 */             int i8 = clipScale(arrayOfInt1[(i1++)], paramDouble1);
/*  456 */             if (i7 < i8) {
/*  457 */               arrayOfInt2[(i2++)] = i7;
/*  458 */               arrayOfInt2[(i2++)] = i8;
/*      */             }
/*      */           }
/*      */         }
/*  462 */         i1 += i3 * 2;
/*      */         
/*      */ 
/*  465 */         if (i2 > i6) {
/*  466 */           arrayOfInt2[(i6 - 1)] = ((i2 - i6) / 2);
/*      */         } else {
/*  468 */           i2 = i6 - 3;
/*      */         }
/*      */       }
/*  471 */       if (i2 <= 5) {
/*  472 */         if (i2 < 5)
/*      */         {
/*  474 */           localRegion.lox = (localRegion.loy = localRegion.hix = localRegion.hiy = 0);
/*      */         }
/*      */         else {
/*  477 */           localRegion.loy = arrayOfInt2[0];
/*  478 */           localRegion.hiy = arrayOfInt2[1];
/*  479 */           localRegion.lox = arrayOfInt2[3];
/*  480 */           localRegion.hix = arrayOfInt2[4];
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  487 */         localRegion.endIndex = i2;
/*  488 */         localRegion.bands = arrayOfInt2;
/*      */       }
/*      */     }
/*  491 */     return localRegion;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Region getTranslatedRegion(int paramInt1, int paramInt2)
/*      */   {
/*  501 */     if ((paramInt1 | paramInt2) == 0) {
/*  502 */       return this;
/*      */     }
/*  504 */     int i = this.lox + paramInt1;
/*  505 */     int j = this.loy + paramInt2;
/*  506 */     int k = this.hix + paramInt1;
/*  507 */     int m = this.hiy + paramInt2;
/*  508 */     if ((i > this.lox ? 1 : 0) == (paramInt1 > 0 ? 1 : 0)) if ((j > this.loy ? 1 : 0) == (paramInt2 > 0 ? 1 : 0)) { if ((k > this.hix ? 1 : 0) == (paramInt1 > 0 ? 1 : 0)) { if ((m > this.hiy ? 1 : 0) == (paramInt2 > 0 ? 1 : 0)) {
/*      */             break label149;
/*      */           }
/*      */         }
/*      */       }
/*  513 */     return getSafeTranslatedRegion(paramInt1, paramInt2);
/*      */     label149:
/*  515 */     Region localRegion = new Region(i, j, k, m);
/*  516 */     int[] arrayOfInt1 = this.bands;
/*  517 */     if (arrayOfInt1 != null) {
/*  518 */       int n = this.endIndex;
/*  519 */       localRegion.endIndex = n;
/*  520 */       int[] arrayOfInt2 = new int[n];
/*  521 */       localRegion.bands = arrayOfInt2;
/*  522 */       int i1 = 0;
/*      */       
/*  524 */       while (i1 < n) {
/*  525 */         arrayOfInt1[i1] += paramInt2;i1++;
/*  526 */         arrayOfInt1[i1] += paramInt2;i1++;
/*  527 */         int i2; arrayOfInt2[i1] = (i2 = arrayOfInt1[i1]);i1++;
/*  528 */         for (;;) { i2--; if (i2 < 0) break;
/*  529 */           arrayOfInt1[i1] += paramInt1;i1++;
/*  530 */           arrayOfInt1[i1] += paramInt1;i1++;
/*      */         }
/*      */       }
/*      */     }
/*  534 */     return localRegion;
/*      */   }
/*      */   
/*      */   private Region getSafeTranslatedRegion(int paramInt1, int paramInt2) {
/*  538 */     int i = clipAdd(this.lox, paramInt1);
/*  539 */     int j = clipAdd(this.loy, paramInt2);
/*  540 */     int k = clipAdd(this.hix, paramInt1);
/*  541 */     int m = clipAdd(this.hiy, paramInt2);
/*  542 */     Region localRegion = new Region(i, j, k, m);
/*  543 */     int[] arrayOfInt1 = this.bands;
/*  544 */     if (arrayOfInt1 != null) {
/*  545 */       int n = this.endIndex;
/*  546 */       int[] arrayOfInt2 = new int[n];
/*  547 */       int i1 = 0;
/*  548 */       int i2 = 0;
/*      */       
/*  550 */       while (i1 < n) {
/*      */         int i4;
/*  552 */         arrayOfInt2[(i2++)] = (i4 = clipAdd(arrayOfInt1[(i1++)], paramInt2));
/*  553 */         int i5; arrayOfInt2[(i2++)] = (i5 = clipAdd(arrayOfInt1[(i1++)], paramInt2));
/*  554 */         int i3; arrayOfInt2[(i2++)] = (i3 = arrayOfInt1[(i1++)]);
/*  555 */         int i6 = i2;
/*  556 */         if (i4 < i5) {
/*  557 */           for (;;) { i3--; if (i3 < 0) break;
/*  558 */             int i7 = clipAdd(arrayOfInt1[(i1++)], paramInt1);
/*  559 */             int i8 = clipAdd(arrayOfInt1[(i1++)], paramInt1);
/*  560 */             if (i7 < i8) {
/*  561 */               arrayOfInt2[(i2++)] = i7;
/*  562 */               arrayOfInt2[(i2++)] = i8;
/*      */             }
/*      */           }
/*      */         }
/*  566 */         i1 += i3 * 2;
/*      */         
/*      */ 
/*  569 */         if (i2 > i6) {
/*  570 */           arrayOfInt2[(i6 - 1)] = ((i2 - i6) / 2);
/*      */         } else {
/*  572 */           i2 = i6 - 3;
/*      */         }
/*      */       }
/*  575 */       if (i2 <= 5) {
/*  576 */         if (i2 < 5)
/*      */         {
/*  578 */           localRegion.lox = (localRegion.loy = localRegion.hix = localRegion.hiy = 0);
/*      */         }
/*      */         else {
/*  581 */           localRegion.loy = arrayOfInt2[0];
/*  582 */           localRegion.hiy = arrayOfInt2[1];
/*  583 */           localRegion.lox = arrayOfInt2[3];
/*  584 */           localRegion.hix = arrayOfInt2[4];
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  591 */         localRegion.endIndex = i2;
/*  592 */         localRegion.bands = arrayOfInt2;
/*      */       }
/*      */     }
/*  595 */     return localRegion;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Region getIntersection(Rectangle paramRectangle)
/*      */   {
/*  604 */     return getIntersectionXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Region getIntersectionXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  613 */     return getIntersectionXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Region getIntersectionXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  622 */     if (isInsideXYXY(paramInt1, paramInt2, paramInt3, paramInt4)) {
/*  623 */       return this;
/*      */     }
/*  625 */     Region localRegion = new Region(paramInt1 < this.lox ? this.lox : paramInt1, paramInt2 < this.loy ? this.loy : paramInt2, paramInt3 > this.hix ? this.hix : paramInt3, paramInt4 > this.hiy ? this.hiy : paramInt4);
/*      */     
/*      */ 
/*      */ 
/*  629 */     if (this.bands != null) {
/*  630 */       localRegion.appendSpans(getSpanIterator());
/*      */     }
/*  632 */     return localRegion;
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
/*      */   public Region getIntersection(Region paramRegion)
/*      */   {
/*  648 */     if (isInsideQuickCheck(paramRegion)) {
/*  649 */       return this;
/*      */     }
/*  651 */     if (paramRegion.isInsideQuickCheck(this)) {
/*  652 */       return paramRegion;
/*      */     }
/*  654 */     Region localRegion = new Region(paramRegion.lox < this.lox ? this.lox : paramRegion.lox, paramRegion.loy < this.loy ? this.loy : paramRegion.loy, paramRegion.hix > this.hix ? this.hix : paramRegion.hix, paramRegion.hiy > this.hiy ? this.hiy : paramRegion.hiy);
/*      */     
/*      */ 
/*      */ 
/*  658 */     if (!localRegion.isEmpty()) {
/*  659 */       localRegion.filterSpans(this, paramRegion, 4);
/*      */     }
/*  661 */     return localRegion;
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
/*      */   public Region getUnion(Region paramRegion)
/*      */   {
/*  677 */     if ((paramRegion.isEmpty()) || (paramRegion.isInsideQuickCheck(this))) {
/*  678 */       return this;
/*      */     }
/*  680 */     if ((isEmpty()) || (isInsideQuickCheck(paramRegion))) {
/*  681 */       return paramRegion;
/*      */     }
/*  683 */     Region localRegion = new Region(paramRegion.lox > this.lox ? this.lox : paramRegion.lox, paramRegion.loy > this.loy ? this.loy : paramRegion.loy, paramRegion.hix < this.hix ? this.hix : paramRegion.hix, paramRegion.hiy < this.hiy ? this.hiy : paramRegion.hiy);
/*      */     
/*      */ 
/*      */ 
/*  687 */     localRegion.filterSpans(this, paramRegion, 7);
/*  688 */     return localRegion;
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
/*      */   public Region getDifference(Region paramRegion)
/*      */   {
/*  704 */     if (!paramRegion.intersectsQuickCheck(this)) {
/*  705 */       return this;
/*      */     }
/*  707 */     if (isInsideQuickCheck(paramRegion)) {
/*  708 */       return EMPTY_REGION;
/*      */     }
/*  710 */     Region localRegion = new Region(this.lox, this.loy, this.hix, this.hiy);
/*  711 */     localRegion.filterSpans(this, paramRegion, 1);
/*  712 */     return localRegion;
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
/*      */   public Region getExclusiveOr(Region paramRegion)
/*      */   {
/*  728 */     if (paramRegion.isEmpty()) {
/*  729 */       return this;
/*      */     }
/*  731 */     if (isEmpty()) {
/*  732 */       return paramRegion;
/*      */     }
/*  734 */     Region localRegion = new Region(paramRegion.lox > this.lox ? this.lox : paramRegion.lox, paramRegion.loy > this.loy ? this.loy : paramRegion.loy, paramRegion.hix < this.hix ? this.hix : paramRegion.hix, paramRegion.hiy < this.hiy ? this.hiy : paramRegion.hiy);
/*      */     
/*      */ 
/*      */ 
/*  738 */     localRegion.filterSpans(this, paramRegion, 3);
/*  739 */     return localRegion;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void filterSpans(Region paramRegion1, Region paramRegion2, int paramInt)
/*      */   {
/*  747 */     int[] arrayOfInt1 = paramRegion1.bands;
/*  748 */     int[] arrayOfInt2 = paramRegion2.bands;
/*  749 */     if (arrayOfInt1 == null) {
/*  750 */       arrayOfInt1 = new int[] { paramRegion1.loy, paramRegion1.hiy, 1, paramRegion1.lox, paramRegion1.hix };
/*      */     }
/*  752 */     if (arrayOfInt2 == null) {
/*  753 */       arrayOfInt2 = new int[] { paramRegion2.loy, paramRegion2.hiy, 1, paramRegion2.lox, paramRegion2.hix };
/*      */     }
/*  755 */     int[] arrayOfInt3 = new int[6];
/*  756 */     int i = 0;
/*  757 */     int j = arrayOfInt1[(i++)];
/*  758 */     int k = arrayOfInt1[(i++)];
/*  759 */     int m = arrayOfInt1[(i++)];
/*  760 */     m = i + 2 * m;
/*  761 */     int n = 0;
/*  762 */     int i1 = arrayOfInt2[(n++)];
/*  763 */     int i2 = arrayOfInt2[(n++)];
/*  764 */     int i3 = arrayOfInt2[(n++)];
/*  765 */     i3 = n + 2 * i3;
/*  766 */     int i4 = this.loy;
/*  767 */     while (i4 < this.hiy)
/*  768 */       if (i4 >= k) {
/*  769 */         if (m < paramRegion1.endIndex) {
/*  770 */           i = m;
/*  771 */           j = arrayOfInt1[(i++)];
/*  772 */           k = arrayOfInt1[(i++)];
/*  773 */           m = arrayOfInt1[(i++)];
/*  774 */           m = i + 2 * m;
/*      */         } else {
/*  776 */           if ((paramInt & 0x2) == 0) break;
/*  777 */           j = k = this.hiy;
/*      */         }
/*      */         
/*      */       }
/*  781 */       else if (i4 >= i2) {
/*  782 */         if (i3 < paramRegion2.endIndex) {
/*  783 */           n = i3;
/*  784 */           i1 = arrayOfInt2[(n++)];
/*  785 */           i2 = arrayOfInt2[(n++)];
/*  786 */           i3 = arrayOfInt2[(n++)];
/*  787 */           i3 = n + 2 * i3;
/*      */         } else {
/*  789 */           if ((paramInt & 0x1) == 0) break;
/*  790 */           i1 = i2 = this.hiy;
/*      */         }
/*      */       } else {
/*      */         int i5;
/*      */         int i6;
/*  795 */         if (i4 < i1) {
/*  796 */           if (i4 < j) {
/*  797 */             i4 = Math.min(j, i1);
/*  798 */             continue;
/*      */           }
/*      */           
/*  801 */           i5 = Math.min(k, i1);
/*  802 */           if ((paramInt & 0x1) != 0) {
/*  803 */             arrayOfInt3[1] = i4;
/*  804 */             arrayOfInt3[3] = i5;
/*  805 */             i6 = i;
/*  806 */             while (i6 < m) {
/*  807 */               arrayOfInt3[0] = arrayOfInt1[(i6++)];
/*  808 */               arrayOfInt3[2] = arrayOfInt1[(i6++)];
/*  809 */               appendSpan(arrayOfInt3);
/*      */             }
/*      */           }
/*  812 */         } else if (i4 < j)
/*      */         {
/*  814 */           i5 = Math.min(i2, j);
/*  815 */           if ((paramInt & 0x2) != 0) {
/*  816 */             arrayOfInt3[1] = i4;
/*  817 */             arrayOfInt3[3] = i5;
/*  818 */             i6 = n;
/*  819 */             while (i6 < i3) {
/*  820 */               arrayOfInt3[0] = arrayOfInt2[(i6++)];
/*  821 */               arrayOfInt3[2] = arrayOfInt2[(i6++)];
/*  822 */               appendSpan(arrayOfInt3);
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  827 */           i5 = Math.min(k, i2);
/*  828 */           arrayOfInt3[1] = i4;
/*  829 */           arrayOfInt3[3] = i5;
/*  830 */           i6 = i;
/*  831 */           int i7 = n;
/*  832 */           int i8 = arrayOfInt1[(i6++)];
/*  833 */           int i9 = arrayOfInt1[(i6++)];
/*  834 */           int i10 = arrayOfInt2[(i7++)];
/*  835 */           int i11 = arrayOfInt2[(i7++)];
/*  836 */           int i12 = Math.min(i8, i10);
/*  837 */           if (i12 < this.lox) i12 = this.lox;
/*  838 */           while (i12 < this.hix)
/*  839 */             if (i12 >= i9) {
/*  840 */               if (i6 < m) {
/*  841 */                 i8 = arrayOfInt1[(i6++)];
/*  842 */                 i9 = arrayOfInt1[(i6++)];
/*      */               } else {
/*  844 */                 if ((paramInt & 0x2) == 0) break;
/*  845 */                 i8 = i9 = this.hix;
/*      */               }
/*      */               
/*      */             }
/*  849 */             else if (i12 >= i11) {
/*  850 */               if (i7 < i3) {
/*  851 */                 i10 = arrayOfInt2[(i7++)];
/*  852 */                 i11 = arrayOfInt2[(i7++)];
/*      */               } else {
/*  854 */                 if ((paramInt & 0x1) == 0) break;
/*  855 */                 i10 = i11 = this.hix;
/*      */               }
/*      */             }
/*      */             else {
/*      */               int i13;
/*      */               int i14;
/*  861 */               if (i12 < i10) {
/*  862 */                 if (i12 < i8) {
/*  863 */                   i13 = Math.min(i8, i10);
/*  864 */                   i14 = 0;
/*      */                 } else {
/*  866 */                   i13 = Math.min(i9, i10);
/*  867 */                   i14 = (paramInt & 0x1) != 0 ? 1 : 0;
/*      */                 }
/*  869 */               } else if (i12 < i8) {
/*  870 */                 i13 = Math.min(i8, i11);
/*  871 */                 i14 = (paramInt & 0x2) != 0 ? 1 : 0;
/*      */               } else {
/*  873 */                 i13 = Math.min(i9, i11);
/*  874 */                 i14 = (paramInt & 0x4) != 0 ? 1 : 0;
/*      */               }
/*  876 */               if (i14 != 0) {
/*  877 */                 arrayOfInt3[0] = i12;
/*  878 */                 arrayOfInt3[2] = i13;
/*  879 */                 appendSpan(arrayOfInt3);
/*      */               }
/*  881 */               i12 = i13;
/*      */             }
/*      */         }
/*  884 */         i4 = i5;
/*      */       }
/*  886 */     endRow(arrayOfInt3);
/*  887 */     calcBBox();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Region getBoundsIntersection(Rectangle paramRectangle)
/*      */   {
/*  899 */     return getBoundsIntersectionXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Region getBoundsIntersectionXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  911 */     return getBoundsIntersectionXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
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
/*      */   public Region getBoundsIntersectionXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  925 */     if ((this.bands == null) && (this.lox >= paramInt1) && (this.loy >= paramInt2) && (this.hix <= paramInt3) && (this.hiy <= paramInt4))
/*      */     {
/*      */ 
/*      */ 
/*  929 */       return this;
/*      */     }
/*  931 */     return new Region(paramInt1 < this.lox ? this.lox : paramInt1, paramInt2 < this.loy ? this.loy : paramInt2, paramInt3 > this.hix ? this.hix : paramInt3, paramInt4 > this.hiy ? this.hiy : paramInt4);
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
/*      */   public Region getBoundsIntersection(Region paramRegion)
/*      */   {
/*  946 */     if (encompasses(paramRegion)) {
/*  947 */       return paramRegion;
/*      */     }
/*  949 */     if (paramRegion.encompasses(this)) {
/*  950 */       return this;
/*      */     }
/*  952 */     return new Region(paramRegion.lox < this.lox ? this.lox : paramRegion.lox, paramRegion.loy < this.loy ? this.loy : paramRegion.loy, paramRegion.hix > this.hix ? this.hix : paramRegion.hix, paramRegion.hiy > this.hiy ? this.hiy : paramRegion.hiy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void appendSpan(int[] paramArrayOfInt)
/*      */   {
/*      */     int i;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  968 */     if ((i = paramArrayOfInt[0]) < this.lox) i = this.lox;
/*  969 */     int j; if ((j = paramArrayOfInt[1]) < this.loy) j = this.loy;
/*  970 */     int k; if ((k = paramArrayOfInt[2]) > this.hix) k = this.hix;
/*  971 */     int m; if ((m = paramArrayOfInt[3]) > this.hiy) m = this.hiy;
/*  972 */     if ((k <= i) || (m <= j)) {
/*  973 */       return;
/*      */     }
/*      */     
/*  976 */     int n = paramArrayOfInt[4];
/*  977 */     if ((this.endIndex == 0) || (j >= this.bands[(n + 1)])) {
/*  978 */       if (this.bands == null) {
/*  979 */         this.bands = new int[50];
/*      */       } else {
/*  981 */         needSpace(5);
/*  982 */         endRow(paramArrayOfInt);
/*  983 */         n = paramArrayOfInt[4];
/*      */       }
/*  985 */       this.bands[(this.endIndex++)] = j;
/*  986 */       this.bands[(this.endIndex++)] = m;
/*  987 */       this.bands[(this.endIndex++)] = 0;
/*  988 */     } else if ((j == this.bands[n]) && (m == this.bands[(n + 1)]) && (i >= this.bands[(this.endIndex - 1)]))
/*      */     {
/*      */ 
/*  991 */       if (i == this.bands[(this.endIndex - 1)]) {
/*  992 */         this.bands[(this.endIndex - 1)] = k;
/*  993 */         return;
/*      */       }
/*  995 */       needSpace(2);
/*      */     } else {
/*  997 */       throw new InternalError("bad span");
/*      */     }
/*  999 */     this.bands[(this.endIndex++)] = i;
/* 1000 */     this.bands[(this.endIndex++)] = k;
/* 1001 */     this.bands[(n + 2)] += 1;
/*      */   }
/*      */   
/*      */   private void needSpace(int paramInt) {
/* 1005 */     if (this.endIndex + paramInt >= this.bands.length) {
/* 1006 */       int[] arrayOfInt = new int[this.bands.length + 50];
/* 1007 */       System.arraycopy(this.bands, 0, arrayOfInt, 0, this.endIndex);
/* 1008 */       this.bands = arrayOfInt;
/*      */     }
/*      */   }
/*      */   
/*      */   private void endRow(int[] paramArrayOfInt) {
/* 1013 */     int i = paramArrayOfInt[4];
/* 1014 */     int j = paramArrayOfInt[5];
/* 1015 */     if (i > j) {
/* 1016 */       int[] arrayOfInt = this.bands;
/* 1017 */       if ((arrayOfInt[(j + 1)] == arrayOfInt[i]) && (arrayOfInt[(j + 2)] == arrayOfInt[(i + 2)]))
/*      */       {
/*      */ 
/* 1020 */         int k = arrayOfInt[(i + 2)] * 2;
/* 1021 */         i += 3;
/* 1022 */         j += 3;
/* 1023 */         while ((k > 0) && 
/* 1024 */           (arrayOfInt[(i++)] == arrayOfInt[(j++)]))
/*      */         {
/*      */ 
/* 1027 */           k--;
/*      */         }
/* 1029 */         if (k == 0)
/*      */         {
/* 1031 */           arrayOfInt[(paramArrayOfInt[5] + 1)] = arrayOfInt[(j + 1)];
/* 1032 */           this.endIndex = j;
/* 1033 */           return;
/*      */         }
/*      */       }
/*      */     }
/* 1037 */     paramArrayOfInt[5] = paramArrayOfInt[4];
/* 1038 */     paramArrayOfInt[4] = this.endIndex;
/*      */   }
/*      */   
/*      */   private void calcBBox() {
/* 1042 */     int[] arrayOfInt = this.bands;
/* 1043 */     if (this.endIndex <= 5) {
/* 1044 */       if (this.endIndex == 0) {
/* 1045 */         this.lox = (this.loy = this.hix = this.hiy = 0);
/*      */       } else {
/* 1047 */         this.loy = arrayOfInt[0];
/* 1048 */         this.hiy = arrayOfInt[1];
/* 1049 */         this.lox = arrayOfInt[3];
/* 1050 */         this.hix = arrayOfInt[4];
/* 1051 */         this.endIndex = 0;
/*      */       }
/* 1053 */       this.bands = null;
/* 1054 */       return;
/*      */     }
/* 1056 */     int i = this.hix;
/* 1057 */     int j = this.lox;
/* 1058 */     int k = 0;
/*      */     
/* 1060 */     int m = 0;
/* 1061 */     while (m < this.endIndex) {
/* 1062 */       k = m;
/* 1063 */       int n = arrayOfInt[(m + 2)];
/* 1064 */       m += 3;
/* 1065 */       if (i > arrayOfInt[m]) {
/* 1066 */         i = arrayOfInt[m];
/*      */       }
/* 1068 */       m += n * 2;
/* 1069 */       if (j < arrayOfInt[(m - 1)]) {
/* 1070 */         j = arrayOfInt[(m - 1)];
/*      */       }
/*      */     }
/*      */     
/* 1074 */     this.lox = i;
/* 1075 */     this.loy = arrayOfInt[0];
/* 1076 */     this.hix = j;
/* 1077 */     this.hiy = arrayOfInt[(k + 1)];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final int getLoX()
/*      */   {
/* 1084 */     return this.lox;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final int getLoY()
/*      */   {
/* 1091 */     return this.loy;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final int getHiX()
/*      */   {
/* 1098 */     return this.hix;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final int getHiY()
/*      */   {
/* 1105 */     return this.hiy;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final int getWidth()
/*      */   {
/* 1112 */     if (this.hix < this.lox) return 0;
/*      */     int i;
/* 1114 */     if ((i = this.hix - this.lox) < 0) {
/* 1115 */       i = Integer.MAX_VALUE;
/*      */     }
/* 1117 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final int getHeight()
/*      */   {
/* 1124 */     if (this.hiy < this.loy) return 0;
/*      */     int i;
/* 1126 */     if ((i = this.hiy - this.loy) < 0) {
/* 1127 */       i = Integer.MAX_VALUE;
/*      */     }
/* 1129 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/* 1136 */     return (this.hix <= this.lox) || (this.hiy <= this.loy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isRectangular()
/*      */   {
/* 1144 */     return this.bands == null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean contains(int paramInt1, int paramInt2)
/*      */   {
/* 1151 */     if ((paramInt1 < this.lox) || (paramInt1 >= this.hix) || (paramInt2 < this.loy) || (paramInt2 >= this.hiy)) return false;
/* 1152 */     if (this.bands == null) return true;
/* 1153 */     int i = 0;
/* 1154 */     while (i < this.endIndex) {
/* 1155 */       if (paramInt2 < this.bands[(i++)])
/* 1156 */         return false;
/*      */       int j;
/* 1158 */       if (paramInt2 >= this.bands[(i++)]) {
/* 1159 */         j = this.bands[(i++)];
/* 1160 */         i += j * 2;
/*      */       } else {
/* 1162 */         j = this.bands[(i++)];
/* 1163 */         j = i + j * 2;
/* 1164 */         while (i < j) {
/* 1165 */           if (paramInt1 < this.bands[(i++)]) return false;
/* 1166 */           if (paramInt1 < this.bands[(i++)]) return true;
/*      */         }
/* 1168 */         return false;
/*      */       }
/*      */     }
/* 1171 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isInsideXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1180 */     return isInsideXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isInsideXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1188 */     return (this.lox >= paramInt1) && (this.loy >= paramInt2) && (this.hix <= paramInt3) && (this.hiy <= paramInt4);
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
/*      */   public boolean isInsideQuickCheck(Region paramRegion)
/*      */   {
/* 1201 */     return (paramRegion.bands == null) && (paramRegion.lox <= this.lox) && (paramRegion.loy <= this.loy) && (paramRegion.hix >= this.hix) && (paramRegion.hiy >= this.hiy);
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
/*      */   public boolean intersectsQuickCheckXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1217 */     return (paramInt3 > this.lox) && (paramInt1 < this.hix) && (paramInt4 > this.loy) && (paramInt2 < this.hiy);
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
/*      */   public boolean intersectsQuickCheck(Region paramRegion)
/*      */   {
/* 1230 */     return (paramRegion.hix > this.lox) && (paramRegion.lox < this.hix) && (paramRegion.hiy > this.loy) && (paramRegion.loy < this.hiy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean encompasses(Region paramRegion)
/*      */   {
/* 1242 */     return (this.bands == null) && (this.lox <= paramRegion.lox) && (this.loy <= paramRegion.loy) && (this.hix >= paramRegion.hix) && (this.hiy >= paramRegion.hiy);
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
/*      */   public boolean encompassesXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1255 */     return encompassesXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean encompassesXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1266 */     return (this.bands == null) && (this.lox <= paramInt1) && (this.loy <= paramInt2) && (this.hix >= paramInt3) && (this.hiy >= paramInt4);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void getBounds(int[] paramArrayOfInt)
/*      */   {
/* 1275 */     paramArrayOfInt[0] = this.lox;
/* 1276 */     paramArrayOfInt[1] = this.loy;
/* 1277 */     paramArrayOfInt[2] = this.hix;
/* 1278 */     paramArrayOfInt[3] = this.hiy;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void clipBoxToBounds(int[] paramArrayOfInt)
/*      */   {
/* 1285 */     if (paramArrayOfInt[0] < this.lox) paramArrayOfInt[0] = this.lox;
/* 1286 */     if (paramArrayOfInt[1] < this.loy) paramArrayOfInt[1] = this.loy;
/* 1287 */     if (paramArrayOfInt[2] > this.hix) paramArrayOfInt[2] = this.hix;
/* 1288 */     if (paramArrayOfInt[3] > this.hiy) { paramArrayOfInt[3] = this.hiy;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public RegionIterator getIterator()
/*      */   {
/* 1295 */     return new RegionIterator(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SpanIterator getSpanIterator()
/*      */   {
/* 1302 */     return new RegionSpanIterator(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SpanIterator getSpanIterator(int[] paramArrayOfInt)
/*      */   {
/* 1310 */     SpanIterator localSpanIterator = getSpanIterator();
/* 1311 */     localSpanIterator.intersectClipBox(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2], paramArrayOfInt[3]);
/* 1312 */     return localSpanIterator;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SpanIterator filter(SpanIterator paramSpanIterator)
/*      */   {
/* 1320 */     if (this.bands == null) {
/* 1321 */       paramSpanIterator.intersectClipBox(this.lox, this.loy, this.hix, this.hiy);
/*      */     } else {
/* 1323 */       paramSpanIterator = new RegionClipSpanIterator(this, paramSpanIterator);
/*      */     }
/* 1325 */     return paramSpanIterator;
/*      */   }
/*      */   
/*      */   public String toString() {
/* 1329 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1330 */     localStringBuffer.append("Region[[");
/* 1331 */     localStringBuffer.append(this.lox);
/* 1332 */     localStringBuffer.append(", ");
/* 1333 */     localStringBuffer.append(this.loy);
/* 1334 */     localStringBuffer.append(" => ");
/* 1335 */     localStringBuffer.append(this.hix);
/* 1336 */     localStringBuffer.append(", ");
/* 1337 */     localStringBuffer.append(this.hiy);
/* 1338 */     localStringBuffer.append("]");
/* 1339 */     if (this.bands != null) {
/* 1340 */       int i = 0;
/* 1341 */       while (i < this.endIndex) {
/* 1342 */         localStringBuffer.append("y{");
/* 1343 */         localStringBuffer.append(this.bands[(i++)]);
/* 1344 */         localStringBuffer.append(",");
/* 1345 */         localStringBuffer.append(this.bands[(i++)]);
/* 1346 */         localStringBuffer.append("}[");
/* 1347 */         int j = this.bands[(i++)];
/* 1348 */         j = i + j * 2;
/* 1349 */         while (i < j) {
/* 1350 */           localStringBuffer.append("x(");
/* 1351 */           localStringBuffer.append(this.bands[(i++)]);
/* 1352 */           localStringBuffer.append(", ");
/* 1353 */           localStringBuffer.append(this.bands[(i++)]);
/* 1354 */           localStringBuffer.append(")");
/*      */         }
/* 1356 */         localStringBuffer.append("]");
/*      */       }
/*      */     }
/* 1359 */     localStringBuffer.append("]");
/* 1360 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */   public int hashCode() {
/* 1364 */     return isEmpty() ? 0 : this.lox * 3 + this.loy * 5 + this.hix * 7 + this.hiy * 9;
/*      */   }
/*      */   
/*      */   public boolean equals(Object paramObject) {
/* 1368 */     if (!(paramObject instanceof Region)) {
/* 1369 */       return false;
/*      */     }
/* 1371 */     Region localRegion = (Region)paramObject;
/* 1372 */     if (isEmpty())
/* 1373 */       return localRegion.isEmpty();
/* 1374 */     if (localRegion.isEmpty()) {
/* 1375 */       return false;
/*      */     }
/* 1377 */     if ((localRegion.lox != this.lox) || (localRegion.loy != this.loy) || (localRegion.hix != this.hix) || (localRegion.hiy != this.hiy))
/*      */     {
/*      */ 
/* 1380 */       return false;
/*      */     }
/* 1382 */     if (this.bands == null)
/* 1383 */       return localRegion.bands == null;
/* 1384 */     if (localRegion.bands == null) {
/* 1385 */       return false;
/*      */     }
/* 1387 */     if (this.endIndex != localRegion.endIndex) {
/* 1388 */       return false;
/*      */     }
/* 1390 */     int[] arrayOfInt1 = this.bands;
/* 1391 */     int[] arrayOfInt2 = localRegion.bands;
/* 1392 */     for (int i = 0; i < this.endIndex; i++) {
/* 1393 */       if (arrayOfInt1[i] != arrayOfInt2[i]) {
/* 1394 */         return false;
/*      */       }
/*      */     }
/* 1397 */     return true;
/*      */   }
/*      */   
/*      */   private static native void initIDs();
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\Region.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
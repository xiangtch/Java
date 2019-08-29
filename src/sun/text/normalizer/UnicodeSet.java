/*      */ package sun.text.normalizer;
/*      */ 
/*      */ import java.text.ParsePosition;
/*      */ import java.util.Iterator;
/*      */ import java.util.TreeSet;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UnicodeSet
/*      */   implements UnicodeMatcher
/*      */ {
/*      */   private static final int LOW = 0;
/*      */   private static final int HIGH = 1114112;
/*      */   public static final int MIN_VALUE = 0;
/*      */   public static final int MAX_VALUE = 1114111;
/*      */   private int len;
/*      */   private int[] list;
/*      */   private int[] rangeList;
/*      */   private int[] buffer;
/*  301 */   TreeSet<String> strings = new TreeSet();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  312 */   private String pat = null;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int START_EXTRA = 16;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int GROW_EXTRA = 16;
/*      */   
/*      */ 
/*  323 */   private static UnicodeSet[] INCLUSIONS = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UnicodeSet()
/*      */   {
/*  334 */     this.list = new int[17];
/*  335 */     this.list[(this.len++)] = 1114112;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UnicodeSet(int paramInt1, int paramInt2)
/*      */   {
/*  347 */     this();
/*  348 */     complement(paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UnicodeSet(String paramString)
/*      */   {
/*  360 */     this();
/*  361 */     applyPattern(paramString, null, null, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UnicodeSet set(UnicodeSet paramUnicodeSet)
/*      */   {
/*  371 */     this.list = ((int[])paramUnicodeSet.list.clone());
/*  372 */     this.len = paramUnicodeSet.len;
/*  373 */     this.pat = paramUnicodeSet.pat;
/*  374 */     this.strings = ((TreeSet)paramUnicodeSet.strings.clone());
/*  375 */     return this;
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
/*      */   public final UnicodeSet applyPattern(String paramString)
/*      */   {
/*  388 */     return applyPattern(paramString, null, null, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void _appendToPat(StringBuffer paramStringBuffer, String paramString, boolean paramBoolean)
/*      */   {
/*  396 */     for (int i = 0; i < paramString.length(); i += UTF16.getCharCount(i)) {
/*  397 */       _appendToPat(paramStringBuffer, UTF16.charAt(paramString, i), paramBoolean);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void _appendToPat(StringBuffer paramStringBuffer, int paramInt, boolean paramBoolean)
/*      */   {
/*  406 */     if ((paramBoolean) && (Utility.isUnprintable(paramInt)))
/*      */     {
/*      */ 
/*  409 */       if (Utility.escapeUnprintable(paramStringBuffer, paramInt)) {
/*  410 */         return;
/*      */       }
/*      */     }
/*      */     
/*  414 */     switch (paramInt) {
/*      */     case 36: 
/*      */     case 38: 
/*      */     case 45: 
/*      */     case 58: 
/*      */     case 91: 
/*      */     case 92: 
/*      */     case 93: 
/*      */     case 94: 
/*      */     case 123: 
/*      */     case 125: 
/*  425 */       paramStringBuffer.append('\\');
/*  426 */       break;
/*      */     
/*      */     default: 
/*  429 */       if (UCharacterProperty.isRuleWhiteSpace(paramInt)) {
/*  430 */         paramStringBuffer.append('\\');
/*      */       }
/*      */       break;
/*      */     }
/*  434 */     UTF16.append(paramStringBuffer, paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private StringBuffer _toPattern(StringBuffer paramStringBuffer, boolean paramBoolean)
/*      */   {
/*  444 */     if (this.pat != null)
/*      */     {
/*  446 */       int j = 0;
/*  447 */       for (int i = 0; i < this.pat.length();) {
/*  448 */         int k = UTF16.charAt(this.pat, i);
/*  449 */         i += UTF16.getCharCount(k);
/*  450 */         if ((paramBoolean) && (Utility.isUnprintable(k)))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  455 */           if (j % 2 == 1) {
/*  456 */             paramStringBuffer.setLength(paramStringBuffer.length() - 1);
/*      */           }
/*  458 */           Utility.escapeUnprintable(paramStringBuffer, k);
/*  459 */           j = 0;
/*      */         } else {
/*  461 */           UTF16.append(paramStringBuffer, k);
/*  462 */           if (k == 92) {
/*  463 */             j++;
/*      */           } else {
/*  465 */             j = 0;
/*      */           }
/*      */         }
/*      */       }
/*  469 */       return paramStringBuffer;
/*      */     }
/*      */     
/*  472 */     return _generatePattern(paramStringBuffer, paramBoolean, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public StringBuffer _generatePattern(StringBuffer paramStringBuffer, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  484 */     paramStringBuffer.append('[');
/*      */     
/*  486 */     int i = getRangeCount();
/*      */     
/*      */     int j;
/*      */     int k;
/*      */     int m;
/*  491 */     if ((i > 1) && 
/*  492 */       (getRangeStart(0) == 0) && 
/*  493 */       (getRangeEnd(i - 1) == 1114111))
/*      */     {
/*      */ 
/*  496 */       paramStringBuffer.append('^');
/*      */       
/*  498 */       for (j = 1; j < i; j++) {
/*  499 */         k = getRangeEnd(j - 1) + 1;
/*  500 */         m = getRangeStart(j) - 1;
/*  501 */         _appendToPat(paramStringBuffer, k, paramBoolean1);
/*  502 */         if (k != m) {
/*  503 */           if (k + 1 != m) {
/*  504 */             paramStringBuffer.append('-');
/*      */           }
/*  506 */           _appendToPat(paramStringBuffer, m, paramBoolean1);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  513 */       for (j = 0; j < i; j++) {
/*  514 */         k = getRangeStart(j);
/*  515 */         m = getRangeEnd(j);
/*  516 */         _appendToPat(paramStringBuffer, k, paramBoolean1);
/*  517 */         if (k != m) {
/*  518 */           if (k + 1 != m) {
/*  519 */             paramStringBuffer.append('-');
/*      */           }
/*  521 */           _appendToPat(paramStringBuffer, m, paramBoolean1);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  526 */     if ((paramBoolean2) && (this.strings.size() > 0)) {
/*  527 */       Iterator localIterator = this.strings.iterator();
/*  528 */       while (localIterator.hasNext()) {
/*  529 */         paramStringBuffer.append('{');
/*  530 */         _appendToPat(paramStringBuffer, (String)localIterator.next(), paramBoolean1);
/*  531 */         paramStringBuffer.append('}');
/*      */       }
/*      */     }
/*  534 */     return paramStringBuffer.append(']');
/*      */   }
/*      */   
/*      */   private UnicodeSet add_unchecked(int paramInt1, int paramInt2)
/*      */   {
/*  539 */     if ((paramInt1 < 0) || (paramInt1 > 1114111)) {
/*  540 */       throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt1, 6));
/*      */     }
/*  542 */     if ((paramInt2 < 0) || (paramInt2 > 1114111)) {
/*  543 */       throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt2, 6));
/*      */     }
/*  545 */     if (paramInt1 < paramInt2) {
/*  546 */       add(range(paramInt1, paramInt2), 2, 0);
/*  547 */     } else if (paramInt1 == paramInt2) {
/*  548 */       add(paramInt1);
/*      */     }
/*  550 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final UnicodeSet add(int paramInt)
/*      */   {
/*  560 */     return add_unchecked(paramInt);
/*      */   }
/*      */   
/*      */   private final UnicodeSet add_unchecked(int paramInt)
/*      */   {
/*  565 */     if ((paramInt < 0) || (paramInt > 1114111)) {
/*  566 */       throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt, 6));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  572 */     int i = findCodePoint(paramInt);
/*      */     
/*      */ 
/*  575 */     if ((i & 0x1) != 0) { return this;
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
/*  589 */     if (paramInt == this.list[i] - 1)
/*      */     {
/*  591 */       this.list[i] = paramInt;
/*      */       
/*  593 */       if (paramInt == 1114111) {
/*  594 */         ensureCapacity(this.len + 1);
/*  595 */         this.list[(this.len++)] = 1114112;
/*      */       }
/*  597 */       if ((i > 0) && (paramInt == this.list[(i - 1)]))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  603 */         System.arraycopy(this.list, i + 1, this.list, i - 1, this.len - i - 1);
/*  604 */         this.len -= 2;
/*      */       }
/*      */       
/*      */     }
/*  608 */     else if ((i > 0) && (paramInt == this.list[(i - 1)]))
/*      */     {
/*  610 */       this.list[(i - 1)] += 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  630 */       if (this.len + 2 > this.list.length) {
/*  631 */         int[] arrayOfInt = new int[this.len + 2 + 16];
/*  632 */         if (i != 0) System.arraycopy(this.list, 0, arrayOfInt, 0, i);
/*  633 */         System.arraycopy(this.list, i, arrayOfInt, i + 2, this.len - i);
/*  634 */         this.list = arrayOfInt;
/*      */       } else {
/*  636 */         System.arraycopy(this.list, i, this.list, i + 2, this.len - i);
/*      */       }
/*      */       
/*  639 */       this.list[i] = paramInt;
/*  640 */       this.list[(i + 1)] = (paramInt + 1);
/*  641 */       this.len += 2;
/*      */     }
/*      */     
/*  644 */     this.pat = null;
/*  645 */     return this;
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
/*      */   public final UnicodeSet add(String paramString)
/*      */   {
/*  659 */     int i = getSingleCP(paramString);
/*  660 */     if (i < 0) {
/*  661 */       this.strings.add(paramString);
/*  662 */       this.pat = null;
/*      */     } else {
/*  664 */       add_unchecked(i, i);
/*      */     }
/*  666 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int getSingleCP(String paramString)
/*      */   {
/*  675 */     if (paramString.length() < 1) {
/*  676 */       throw new IllegalArgumentException("Can't use zero-length strings in UnicodeSet");
/*      */     }
/*  678 */     if (paramString.length() > 2) return -1;
/*  679 */     if (paramString.length() == 1) { return paramString.charAt(0);
/*      */     }
/*      */     
/*  682 */     int i = UTF16.charAt(paramString, 0);
/*  683 */     if (i > 65535) {
/*  684 */       return i;
/*      */     }
/*  686 */     return -1;
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
/*      */   public UnicodeSet complement(int paramInt1, int paramInt2)
/*      */   {
/*  702 */     if ((paramInt1 < 0) || (paramInt1 > 1114111)) {
/*  703 */       throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt1, 6));
/*      */     }
/*  705 */     if ((paramInt2 < 0) || (paramInt2 > 1114111)) {
/*  706 */       throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt2, 6));
/*      */     }
/*  708 */     if (paramInt1 <= paramInt2) {
/*  709 */       xor(range(paramInt1, paramInt2), 2, 0);
/*      */     }
/*  711 */     this.pat = null;
/*  712 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UnicodeSet complement()
/*      */   {
/*  721 */     if (this.list[0] == 0) {
/*  722 */       System.arraycopy(this.list, 1, this.list, 0, this.len - 1);
/*  723 */       this.len -= 1;
/*      */     } else {
/*  725 */       ensureCapacity(this.len + 1);
/*  726 */       System.arraycopy(this.list, 0, this.list, 1, this.len);
/*  727 */       this.list[0] = 0;
/*  728 */       this.len += 1;
/*      */     }
/*  730 */     this.pat = null;
/*  731 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean contains(int paramInt)
/*      */   {
/*  741 */     if ((paramInt < 0) || (paramInt > 1114111)) {
/*  742 */       throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt, 6));
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
/*  754 */     int i = findCodePoint(paramInt);
/*      */     
/*  756 */     return (i & 0x1) != 0;
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
/*      */   private final int findCodePoint(int paramInt)
/*      */   {
/*  781 */     if (paramInt < this.list[0]) { return 0;
/*      */     }
/*      */     
/*  784 */     if ((this.len >= 2) && (paramInt >= this.list[(this.len - 2)])) return this.len - 1;
/*  785 */     int i = 0;
/*  786 */     int j = this.len - 1;
/*      */     
/*      */     for (;;)
/*      */     {
/*  790 */       int k = i + j >>> 1;
/*  791 */       if (k == i) return j;
/*  792 */       if (paramInt < this.list[k]) {
/*  793 */         j = k;
/*      */       } else {
/*  795 */         i = k;
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
/*      */   public UnicodeSet addAll(UnicodeSet paramUnicodeSet)
/*      */   {
/*  811 */     add(paramUnicodeSet.list, paramUnicodeSet.len, 0);
/*  812 */     this.strings.addAll(paramUnicodeSet.strings);
/*  813 */     return this;
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
/*      */   public UnicodeSet retainAll(UnicodeSet paramUnicodeSet)
/*      */   {
/*  827 */     retain(paramUnicodeSet.list, paramUnicodeSet.len, 0);
/*  828 */     this.strings.retainAll(paramUnicodeSet.strings);
/*  829 */     return this;
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
/*      */   public UnicodeSet removeAll(UnicodeSet paramUnicodeSet)
/*      */   {
/*  843 */     retain(paramUnicodeSet.list, paramUnicodeSet.len, 2);
/*  844 */     this.strings.removeAll(paramUnicodeSet.strings);
/*  845 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UnicodeSet clear()
/*      */   {
/*  854 */     this.list[0] = 1114112;
/*  855 */     this.len = 1;
/*  856 */     this.pat = null;
/*  857 */     this.strings.clear();
/*  858 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getRangeCount()
/*      */   {
/*  869 */     return this.len / 2;
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
/*      */   public int getRangeStart(int paramInt)
/*      */   {
/*  882 */     return this.list[(paramInt * 2)];
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
/*      */   public int getRangeEnd(int paramInt)
/*      */   {
/*  895 */     return this.list[(paramInt * 2 + 1)] - 1;
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
/*      */   UnicodeSet applyPattern(String paramString, ParsePosition paramParsePosition, SymbolTable paramSymbolTable, int paramInt)
/*      */   {
/*  931 */     int i = paramParsePosition == null ? 1 : 0;
/*  932 */     if (i != 0) {
/*  933 */       paramParsePosition = new ParsePosition(0);
/*      */     }
/*      */     
/*  936 */     StringBuffer localStringBuffer = new StringBuffer();
/*  937 */     RuleCharacterIterator localRuleCharacterIterator = new RuleCharacterIterator(paramString, paramSymbolTable, paramParsePosition);
/*      */     
/*  939 */     applyPattern(localRuleCharacterIterator, paramSymbolTable, localStringBuffer, paramInt);
/*  940 */     if (localRuleCharacterIterator.inVariable()) {
/*  941 */       syntaxError(localRuleCharacterIterator, "Extra chars in variable value");
/*      */     }
/*  943 */     this.pat = localStringBuffer.toString();
/*  944 */     if (i != 0) {
/*  945 */       int j = paramParsePosition.getIndex();
/*      */       
/*      */ 
/*  948 */       if ((paramInt & 0x1) != 0) {
/*  949 */         j = Utility.skipWhitespace(paramString, j);
/*      */       }
/*      */       
/*  952 */       if (j != paramString.length()) {
/*  953 */         throw new IllegalArgumentException("Parse of \"" + paramString + "\" failed at " + j);
/*      */       }
/*      */     }
/*      */     
/*  957 */     return this;
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
/*      */   void applyPattern(RuleCharacterIterator paramRuleCharacterIterator, SymbolTable paramSymbolTable, StringBuffer paramStringBuffer, int paramInt)
/*      */   {
/*  980 */     int i = 3;
/*      */     
/*  982 */     if ((paramInt & 0x1) != 0) {
/*  983 */       i |= 0x4;
/*      */     }
/*      */     
/*  986 */     StringBuffer localStringBuffer1 = new StringBuffer();StringBuffer localStringBuffer2 = null;
/*  987 */     int j = 0;
/*  988 */     UnicodeSet localUnicodeSet1 = null;
/*  989 */     Object localObject = null;
/*      */     
/*      */ 
/*      */ 
/*  993 */     int k = 0;int m = 0;int n = 0;
/*  994 */     char c = '\000';
/*      */     
/*  996 */     int i1 = 0;
/*      */     
/*  998 */     clear();
/*      */     
/* 1000 */     while ((n != 2) && (!paramRuleCharacterIterator.atEnd()))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1010 */       int i2 = 0;
/* 1011 */       boolean bool = false;
/* 1012 */       UnicodeSet localUnicodeSet2 = null;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1017 */       int i3 = 0;
/* 1018 */       if (resemblesPropertyPattern(paramRuleCharacterIterator, i)) {
/* 1019 */         i3 = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1032 */         localObject = paramRuleCharacterIterator.getPos(localObject);
/* 1033 */         i2 = paramRuleCharacterIterator.next(i);
/* 1034 */         bool = paramRuleCharacterIterator.isEscaped();
/*      */         
/* 1036 */         if ((i2 == 91) && (!bool)) {
/* 1037 */           if (n == 1) {
/* 1038 */             paramRuleCharacterIterator.setPos(localObject);
/* 1039 */             i3 = 1;
/*      */           }
/*      */           else {
/* 1042 */             n = 1;
/* 1043 */             localStringBuffer1.append('[');
/* 1044 */             localObject = paramRuleCharacterIterator.getPos(localObject);
/* 1045 */             i2 = paramRuleCharacterIterator.next(i);
/* 1046 */             bool = paramRuleCharacterIterator.isEscaped();
/* 1047 */             if ((i2 == 94) && (!bool)) {
/* 1048 */               i1 = 1;
/* 1049 */               localStringBuffer1.append('^');
/* 1050 */               localObject = paramRuleCharacterIterator.getPos(localObject);
/* 1051 */               i2 = paramRuleCharacterIterator.next(i);
/* 1052 */               bool = paramRuleCharacterIterator.isEscaped();
/*      */             }
/*      */             
/*      */ 
/* 1056 */             if (i2 == 45) {
/* 1057 */               bool = true;
/*      */             }
/*      */             else {
/* 1060 */               paramRuleCharacterIterator.setPos(localObject);
/*      */             }
/*      */           }
/*      */         }
/* 1064 */         else if (paramSymbolTable != null) {
/* 1065 */           UnicodeMatcher localUnicodeMatcher = paramSymbolTable.lookupMatcher(i2);
/* 1066 */           if (localUnicodeMatcher != null) {
/*      */             try {
/* 1068 */               localUnicodeSet2 = (UnicodeSet)localUnicodeMatcher;
/* 1069 */               i3 = 3;
/*      */             } catch (ClassCastException localClassCastException) {
/* 1071 */               syntaxError(paramRuleCharacterIterator, "Syntax error");
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1082 */       if (i3 != 0) {
/* 1083 */         if (k == 1) {
/* 1084 */           if (c != 0) {
/* 1085 */             syntaxError(paramRuleCharacterIterator, "Char expected after operator");
/*      */           }
/* 1087 */           add_unchecked(m, m);
/* 1088 */           _appendToPat(localStringBuffer1, m, false);
/* 1089 */           k = c = 0;
/*      */         }
/*      */         
/* 1092 */         if ((c == '-') || (c == '&')) {
/* 1093 */           localStringBuffer1.append(c);
/*      */         }
/*      */         
/* 1096 */         if (localUnicodeSet2 == null) {
/* 1097 */           if (localUnicodeSet1 == null) localUnicodeSet1 = new UnicodeSet();
/* 1098 */           localUnicodeSet2 = localUnicodeSet1;
/*      */         }
/* 1100 */         switch (i3) {
/*      */         case 1: 
/* 1102 */           localUnicodeSet2.applyPattern(paramRuleCharacterIterator, paramSymbolTable, localStringBuffer1, paramInt);
/* 1103 */           break;
/*      */         case 2: 
/* 1105 */           paramRuleCharacterIterator.skipIgnored(i);
/* 1106 */           localUnicodeSet2.applyPropertyPattern(paramRuleCharacterIterator, localStringBuffer1, paramSymbolTable);
/* 1107 */           break;
/*      */         case 3: 
/* 1109 */           localUnicodeSet2._toPattern(localStringBuffer1, false);
/*      */         }
/*      */         
/*      */         
/* 1113 */         j = 1;
/*      */         
/* 1115 */         if (n == 0)
/*      */         {
/* 1117 */           set(localUnicodeSet2);
/* 1118 */           n = 2;
/* 1119 */           break;
/*      */         }
/*      */         
/* 1122 */         switch (c) {
/*      */         case '-': 
/* 1124 */           removeAll(localUnicodeSet2);
/* 1125 */           break;
/*      */         case '&': 
/* 1127 */           retainAll(localUnicodeSet2);
/* 1128 */           break;
/*      */         case '\000': 
/* 1130 */           addAll(localUnicodeSet2);
/*      */         }
/*      */         
/*      */         
/* 1134 */         c = '\000';
/* 1135 */         k = 2;
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1140 */         if (n == 0) {
/* 1141 */           syntaxError(paramRuleCharacterIterator, "Missing '['");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1148 */         if (!bool) {
/* 1149 */           switch (i2) {
/*      */           case 93: 
/* 1151 */             if (k == 1) {
/* 1152 */               add_unchecked(m, m);
/* 1153 */               _appendToPat(localStringBuffer1, m, false);
/*      */             }
/*      */             
/* 1156 */             if (c == '-') {
/* 1157 */               add_unchecked(c, c);
/* 1158 */               localStringBuffer1.append(c);
/* 1159 */             } else if (c == '&') {
/* 1160 */               syntaxError(paramRuleCharacterIterator, "Trailing '&'");
/*      */             }
/* 1162 */             localStringBuffer1.append(']');
/* 1163 */             n = 2;
/* 1164 */             break;
/*      */           case 45: 
/* 1166 */             if (c == 0) {
/* 1167 */               if (k != 0) {
/* 1168 */                 c = (char)i2;
/* 1169 */                 continue;
/*      */               }
/*      */               
/* 1172 */               add_unchecked(i2, i2);
/* 1173 */               i2 = paramRuleCharacterIterator.next(i);
/* 1174 */               bool = paramRuleCharacterIterator.isEscaped();
/* 1175 */               if ((i2 == 93) && (!bool)) {
/* 1176 */                 localStringBuffer1.append("-]");
/* 1177 */                 n = 2;
/* 1178 */                 continue;
/*      */               }
/*      */             }
/*      */             
/* 1182 */             syntaxError(paramRuleCharacterIterator, "'-' not after char or set");
/* 1183 */             break;
/*      */           case 38: 
/* 1185 */             if ((k == 2) && (c == 0)) {
/* 1186 */               c = (char)i2;
/* 1187 */               continue;
/*      */             }
/* 1189 */             syntaxError(paramRuleCharacterIterator, "'&' not after set");
/* 1190 */             break;
/*      */           case 94: 
/* 1192 */             syntaxError(paramRuleCharacterIterator, "'^' not after '['");
/* 1193 */             break;
/*      */           case 123: 
/* 1195 */             if (c != 0) {
/* 1196 */               syntaxError(paramRuleCharacterIterator, "Missing operand after operator");
/*      */             }
/* 1198 */             if (k == 1) {
/* 1199 */               add_unchecked(m, m);
/* 1200 */               _appendToPat(localStringBuffer1, m, false);
/*      */             }
/* 1202 */             k = 0;
/* 1203 */             if (localStringBuffer2 == null) {
/* 1204 */               localStringBuffer2 = new StringBuffer();
/*      */             } else {
/* 1206 */               localStringBuffer2.setLength(0);
/*      */             }
/* 1208 */             int i4 = 0;
/* 1209 */             while (!paramRuleCharacterIterator.atEnd()) {
/* 1210 */               i2 = paramRuleCharacterIterator.next(i);
/* 1211 */               bool = paramRuleCharacterIterator.isEscaped();
/* 1212 */               if ((i2 == 125) && (!bool)) {
/* 1213 */                 i4 = 1;
/* 1214 */                 break;
/*      */               }
/* 1216 */               UTF16.append(localStringBuffer2, i2);
/*      */             }
/* 1218 */             if ((localStringBuffer2.length() < 1) || (i4 == 0)) {
/* 1219 */               syntaxError(paramRuleCharacterIterator, "Invalid multicharacter string");
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1224 */             add(localStringBuffer2.toString());
/* 1225 */             localStringBuffer1.append('{');
/* 1226 */             _appendToPat(localStringBuffer1, localStringBuffer2.toString(), false);
/* 1227 */             localStringBuffer1.append('}');
/* 1228 */             break;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           case 36: 
/* 1236 */             localObject = paramRuleCharacterIterator.getPos(localObject);
/* 1237 */             i2 = paramRuleCharacterIterator.next(i);
/* 1238 */             bool = paramRuleCharacterIterator.isEscaped();
/* 1239 */             int i5 = (i2 == 93) && (!bool) ? 1 : 0;
/* 1240 */             if ((paramSymbolTable == null) && (i5 == 0)) {
/* 1241 */               i2 = 36;
/* 1242 */               paramRuleCharacterIterator.setPos(localObject);
/*      */             }
/*      */             else {
/* 1245 */               if ((i5 != 0) && (c == 0)) {
/* 1246 */                 if (k == 1) {
/* 1247 */                   add_unchecked(m, m);
/* 1248 */                   _appendToPat(localStringBuffer1, m, false);
/*      */                 }
/* 1250 */                 add_unchecked(65535);
/* 1251 */                 j = 1;
/* 1252 */                 localStringBuffer1.append('$').append(']');
/* 1253 */                 n = 2;
/* 1254 */                 continue;
/*      */               }
/* 1256 */               syntaxError(paramRuleCharacterIterator, "Unquoted '$'"); }
/* 1257 */             break;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           
/*      */         } else {
/* 1267 */           switch (k) {
/*      */           case 0: 
/* 1269 */             k = 1;
/* 1270 */             m = i2;
/* 1271 */             break;
/*      */           case 1: 
/* 1273 */             if (c == '-') {
/* 1274 */               if (m >= i2)
/*      */               {
/*      */ 
/* 1277 */                 syntaxError(paramRuleCharacterIterator, "Invalid range");
/*      */               }
/* 1279 */               add_unchecked(m, i2);
/* 1280 */               _appendToPat(localStringBuffer1, m, false);
/* 1281 */               localStringBuffer1.append(c);
/* 1282 */               _appendToPat(localStringBuffer1, i2, false);
/* 1283 */               k = c = 0;
/*      */             } else {
/* 1285 */               add_unchecked(m, m);
/* 1286 */               _appendToPat(localStringBuffer1, m, false);
/* 1287 */               m = i2;
/*      */             }
/* 1289 */             break;
/*      */           case 2: 
/* 1291 */             if (c != 0) {
/* 1292 */               syntaxError(paramRuleCharacterIterator, "Set expected after operator");
/*      */             }
/* 1294 */             m = i2;
/* 1295 */             k = 1;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1300 */     if (n != 2) {
/* 1301 */       syntaxError(paramRuleCharacterIterator, "Missing ']'");
/*      */     }
/*      */     
/* 1304 */     paramRuleCharacterIterator.skipIgnored(i);
/*      */     
/* 1306 */     if (i1 != 0) {
/* 1307 */       complement();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1312 */     if (j != 0) {
/* 1313 */       paramStringBuffer.append(localStringBuffer1.toString());
/*      */     } else {
/* 1315 */       _generatePattern(paramStringBuffer, false, true);
/*      */     }
/*      */   }
/*      */   
/*      */   private static void syntaxError(RuleCharacterIterator paramRuleCharacterIterator, String paramString)
/*      */   {
/* 1321 */     throw new IllegalArgumentException("Error: " + paramString + " at \"" + Utility.escape(paramRuleCharacterIterator.toString()) + '"');
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void ensureCapacity(int paramInt)
/*      */   {
/* 1330 */     if (paramInt <= this.list.length) return;
/* 1331 */     int[] arrayOfInt = new int[paramInt + 16];
/* 1332 */     System.arraycopy(this.list, 0, arrayOfInt, 0, this.len);
/* 1333 */     this.list = arrayOfInt;
/*      */   }
/*      */   
/*      */   private void ensureBufferCapacity(int paramInt) {
/* 1337 */     if ((this.buffer != null) && (paramInt <= this.buffer.length)) return;
/* 1338 */     this.buffer = new int[paramInt + 16];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int[] range(int paramInt1, int paramInt2)
/*      */   {
/* 1345 */     if (this.rangeList == null) {
/* 1346 */       this.rangeList = new int[] { paramInt1, paramInt2 + 1, 1114112 };
/*      */     } else {
/* 1348 */       this.rangeList[0] = paramInt1;
/* 1349 */       this.rangeList[1] = (paramInt2 + 1);
/*      */     }
/* 1351 */     return this.rangeList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private UnicodeSet xor(int[] paramArrayOfInt, int paramInt1, int paramInt2)
/*      */   {
/* 1362 */     ensureBufferCapacity(this.len + paramInt1);
/* 1363 */     int i = 0;int j = 0;int k = 0;
/* 1364 */     int m = this.list[(i++)];
/*      */     int n;
/* 1366 */     if ((paramInt2 == 1) || (paramInt2 == 2)) {
/* 1367 */       n = 0;
/* 1368 */       if (paramArrayOfInt[j] == 0) {
/* 1369 */         j++;
/* 1370 */         n = paramArrayOfInt[j];
/*      */       }
/*      */     } else {
/* 1373 */       n = paramArrayOfInt[(j++)];
/*      */     }
/*      */     
/*      */     for (;;)
/*      */     {
/* 1378 */       if (m < n) {
/* 1379 */         this.buffer[(k++)] = m;
/* 1380 */         m = this.list[(i++)];
/* 1381 */       } else if (n < m) {
/* 1382 */         this.buffer[(k++)] = n;
/* 1383 */         n = paramArrayOfInt[(j++)];
/* 1384 */       } else { if (m == 1114112)
/*      */           break;
/* 1386 */         m = this.list[(i++)];
/* 1387 */         n = paramArrayOfInt[(j++)];
/*      */       } }
/* 1389 */     this.buffer[(k++)] = 1114112;
/* 1390 */     this.len = k;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1395 */     int[] arrayOfInt = this.list;
/* 1396 */     this.list = this.buffer;
/* 1397 */     this.buffer = arrayOfInt;
/* 1398 */     this.pat = null;
/* 1399 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private UnicodeSet add(int[] paramArrayOfInt, int paramInt1, int paramInt2)
/*      */   {
/* 1408 */     ensureBufferCapacity(this.len + paramInt1);
/* 1409 */     int i = 0;int j = 0;int k = 0;
/* 1410 */     int m = this.list[(i++)];
/* 1411 */     int n = paramArrayOfInt[(j++)];
/*      */     
/*      */ 
/*      */     for (;;)
/*      */     {
/* 1416 */       switch (paramInt2) {
/*      */       case 0: 
/* 1418 */         if (m < n)
/*      */         {
/* 1420 */           if ((k > 0) && (m <= this.buffer[(k - 1)]))
/*      */           {
/* 1422 */             m = max(this.list[i], this.buffer[(--k)]);
/*      */           }
/*      */           else {
/* 1425 */             this.buffer[(k++)] = m;
/* 1426 */             m = this.list[i];
/*      */           }
/* 1428 */           i++;
/* 1429 */           paramInt2 ^= 0x1;
/* 1430 */         } else if (n < m) {
/* 1431 */           if ((k > 0) && (n <= this.buffer[(k - 1)])) {
/* 1432 */             n = max(paramArrayOfInt[j], this.buffer[(--k)]);
/*      */           } else {
/* 1434 */             this.buffer[(k++)] = n;
/* 1435 */             n = paramArrayOfInt[j];
/*      */           }
/* 1437 */           j++;
/* 1438 */           paramInt2 ^= 0x2;
/*      */         } else {
/* 1440 */           if (m == 1114112) {
/*      */             break label620;
/*      */           }
/* 1443 */           if ((k > 0) && (m <= this.buffer[(k - 1)])) {
/* 1444 */             m = max(this.list[i], this.buffer[(--k)]);
/*      */           }
/*      */           else {
/* 1447 */             this.buffer[(k++)] = m;
/* 1448 */             m = this.list[i];
/*      */           }
/* 1450 */           i++;
/* 1451 */           paramInt2 ^= 0x1;
/* 1452 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         }
/* 1454 */         break;
/*      */       case 3: 
/* 1456 */         if (n <= m) {
/* 1457 */           if (m == 1114112) break label620;
/* 1458 */           this.buffer[(k++)] = m;
/*      */         } else {
/* 1460 */           if (n == 1114112) break label620;
/* 1461 */           this.buffer[(k++)] = n;
/*      */         }
/* 1463 */         m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1464 */         n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/* 1465 */         break;
/*      */       case 1: 
/* 1467 */         if (m < n) {
/* 1468 */           this.buffer[(k++)] = m;m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1469 */         } else if (n < m) {
/* 1470 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         } else {
/* 1472 */           if (m == 1114112) break label620;
/* 1473 */           m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1474 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         }
/* 1476 */         break;
/*      */       case 2: 
/* 1478 */         if (n < m) {
/* 1479 */           this.buffer[(k++)] = n;n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/* 1480 */         } else if (m < n) {
/* 1481 */           m = this.list[(i++)];paramInt2 ^= 0x1;
/*      */         } else {
/* 1483 */           if (m == 1114112) break label620;
/* 1484 */           m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1485 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         }
/*      */         break; }
/*      */     }
/*      */     label620:
/* 1490 */     this.buffer[(k++)] = 1114112;
/* 1491 */     this.len = k;
/*      */     
/* 1493 */     int[] arrayOfInt = this.list;
/* 1494 */     this.list = this.buffer;
/* 1495 */     this.buffer = arrayOfInt;
/* 1496 */     this.pat = null;
/* 1497 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private UnicodeSet retain(int[] paramArrayOfInt, int paramInt1, int paramInt2)
/*      */   {
/* 1506 */     ensureBufferCapacity(this.len + paramInt1);
/* 1507 */     int i = 0;int j = 0;int k = 0;
/* 1508 */     int m = this.list[(i++)];
/* 1509 */     int n = paramArrayOfInt[(j++)];
/*      */     
/*      */ 
/*      */     for (;;)
/*      */     {
/* 1514 */       switch (paramInt2) {
/*      */       case 0: 
/* 1516 */         if (m < n) {
/* 1517 */           m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1518 */         } else if (n < m) {
/* 1519 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         } else {
/* 1521 */           if (m == 1114112) break label508;
/* 1522 */           this.buffer[(k++)] = m;m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1523 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         }
/* 1525 */         break;
/*      */       case 3: 
/* 1527 */         if (m < n) {
/* 1528 */           this.buffer[(k++)] = m;m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1529 */         } else if (n < m) {
/* 1530 */           this.buffer[(k++)] = n;n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         } else {
/* 1532 */           if (m == 1114112) break label508;
/* 1533 */           this.buffer[(k++)] = m;m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1534 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         }
/* 1536 */         break;
/*      */       case 1: 
/* 1538 */         if (m < n) {
/* 1539 */           m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1540 */         } else if (n < m) {
/* 1541 */           this.buffer[(k++)] = n;n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         } else {
/* 1543 */           if (m == 1114112) break label508;
/* 1544 */           m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1545 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         }
/* 1547 */         break;
/*      */       case 2: 
/* 1549 */         if (n < m) {
/* 1550 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/* 1551 */         } else if (m < n) {
/* 1552 */           this.buffer[(k++)] = m;m = this.list[(i++)];paramInt2 ^= 0x1;
/*      */         } else {
/* 1554 */           if (m == 1114112) break label508;
/* 1555 */           m = this.list[(i++)];paramInt2 ^= 0x1;
/* 1556 */           n = paramArrayOfInt[(j++)];paramInt2 ^= 0x2;
/*      */         }
/*      */         break; }
/*      */     }
/*      */     label508:
/* 1561 */     this.buffer[(k++)] = 1114112;
/* 1562 */     this.len = k;
/*      */     
/* 1564 */     int[] arrayOfInt = this.list;
/* 1565 */     this.list = this.buffer;
/* 1566 */     this.buffer = arrayOfInt;
/* 1567 */     this.pat = null;
/* 1568 */     return this;
/*      */   }
/*      */   
/*      */   private static final int max(int paramInt1, int paramInt2) {
/* 1572 */     return paramInt1 > paramInt2 ? paramInt1 : paramInt2;
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
/* 1584 */   static final VersionInfo NO_VERSION = VersionInfo.getInstance(0, 0, 0, 0);
/*      */   public static final int IGNORE_SPACE = 1;
/*      */   
/*      */   private static abstract interface Filter { public abstract boolean contains(int paramInt); }
/*      */   
/* 1589 */   private static class VersionFilter implements Filter { VersionFilter(VersionInfo paramVersionInfo) { this.version = paramVersionInfo; }
/*      */     
/*      */     VersionInfo version;
/* 1592 */     public boolean contains(int paramInt) { VersionInfo localVersionInfo = UCharacter.getAge(paramInt);
/*      */       
/*      */ 
/* 1595 */       return (localVersionInfo != UnicodeSet.NO_VERSION) && 
/* 1596 */         (localVersionInfo.compareTo(this.version) <= 0);
/*      */     }
/*      */   }
/*      */   
/*      */   private static synchronized UnicodeSet getInclusions(int paramInt) {
/* 1601 */     if (INCLUSIONS == null) {
/* 1602 */       INCLUSIONS = new UnicodeSet[9];
/*      */     }
/* 1604 */     if (INCLUSIONS[paramInt] == null) {
/* 1605 */       UnicodeSet localUnicodeSet = new UnicodeSet();
/* 1606 */       switch (paramInt) {
/*      */       case 2: 
/* 1608 */         UCharacterProperty.getInstance().upropsvec_addPropertyStarts(localUnicodeSet);
/* 1609 */         break;
/*      */       default: 
/* 1611 */         throw new IllegalStateException("UnicodeSet.getInclusions(unknown src " + paramInt + ")");
/*      */       }
/* 1613 */       INCLUSIONS[paramInt] = localUnicodeSet;
/*      */     }
/* 1615 */     return INCLUSIONS[paramInt];
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
/*      */   private UnicodeSet applyFilter(Filter paramFilter, int paramInt)
/*      */   {
/* 1636 */     clear();
/*      */     
/* 1638 */     int i = -1;
/* 1639 */     UnicodeSet localUnicodeSet = getInclusions(paramInt);
/* 1640 */     int j = localUnicodeSet.getRangeCount();
/*      */     
/* 1642 */     for (int k = 0; k < j; k++)
/*      */     {
/* 1644 */       int m = localUnicodeSet.getRangeStart(k);
/* 1645 */       int n = localUnicodeSet.getRangeEnd(k);
/*      */       
/*      */ 
/* 1648 */       for (int i1 = m; i1 <= n; i1++)
/*      */       {
/*      */ 
/* 1651 */         if (paramFilter.contains(i1)) {
/* 1652 */           if (i < 0) {
/* 1653 */             i = i1;
/*      */           }
/* 1655 */         } else if (i >= 0) {
/* 1656 */           add_unchecked(i, i1 - 1);
/* 1657 */           i = -1;
/*      */         }
/*      */       }
/*      */     }
/* 1661 */     if (i >= 0) {
/* 1662 */       add_unchecked(i, 1114111);
/*      */     }
/*      */     
/* 1665 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String mungeCharName(String paramString)
/*      */   {
/* 1675 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1676 */     for (int i = 0; i < paramString.length();) {
/* 1677 */       int j = UTF16.charAt(paramString, i);
/* 1678 */       i += UTF16.getCharCount(j);
/* 1679 */       if (UCharacterProperty.isRuleWhiteSpace(j)) {
/* 1680 */         if ((localStringBuffer.length() != 0) && 
/* 1681 */           (localStringBuffer.charAt(localStringBuffer.length() - 1) != ' '))
/*      */         {
/*      */ 
/* 1684 */           j = 32; }
/*      */       } else
/* 1686 */         UTF16.append(localStringBuffer, j);
/*      */     }
/* 1688 */     if ((localStringBuffer.length() != 0) && 
/* 1689 */       (localStringBuffer.charAt(localStringBuffer.length() - 1) == ' ')) {
/* 1690 */       localStringBuffer.setLength(localStringBuffer.length() - 1);
/*      */     }
/* 1692 */     return localStringBuffer.toString();
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
/*      */   public UnicodeSet applyPropertyAlias(String paramString1, String paramString2, SymbolTable paramSymbolTable)
/*      */   {
/* 1708 */     if ((paramString2.length() > 0) && 
/* 1709 */       (paramString1.equals("Age")))
/*      */     {
/*      */ 
/*      */ 
/* 1713 */       VersionInfo localVersionInfo = VersionInfo.getInstance(mungeCharName(paramString2));
/* 1714 */       applyFilter(new VersionFilter(localVersionInfo), 2);
/* 1715 */       return this;
/*      */     }
/*      */     
/* 1718 */     throw new IllegalArgumentException("Unsupported property: " + paramString1);
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
/*      */   private static boolean resemblesPropertyPattern(RuleCharacterIterator paramRuleCharacterIterator, int paramInt)
/*      */   {
/* 1731 */     boolean bool = false;
/* 1732 */     paramInt &= 0xFFFFFFFD;
/* 1733 */     Object localObject = paramRuleCharacterIterator.getPos(null);
/* 1734 */     int i = paramRuleCharacterIterator.next(paramInt);
/* 1735 */     if ((i == 91) || (i == 92)) {
/* 1736 */       int j = paramRuleCharacterIterator.next(paramInt & 0xFFFFFFFB);
/* 1737 */       bool = j == 58;
/*      */     }
/*      */     
/* 1740 */     paramRuleCharacterIterator.setPos(localObject);
/* 1741 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private UnicodeSet applyPropertyPattern(String paramString, ParsePosition paramParsePosition, SymbolTable paramSymbolTable)
/*      */   {
/* 1749 */     int i = paramParsePosition.getIndex();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1754 */     if (i + 5 > paramString.length()) {
/* 1755 */       return null;
/*      */     }
/*      */     
/* 1758 */     int j = 0;
/* 1759 */     int k = 0;
/* 1760 */     int m = 0;
/*      */     
/*      */ 
/* 1763 */     if (paramString.regionMatches(i, "[:", 0, 2)) {
/* 1764 */       j = 1;
/* 1765 */       i = Utility.skipWhitespace(paramString, i + 2);
/* 1766 */       if ((i < paramString.length()) && (paramString.charAt(i) == '^')) {
/* 1767 */         i++;
/* 1768 */         m = 1;
/*      */       }
/* 1770 */     } else if ((paramString.regionMatches(true, i, "\\p", 0, 2)) || 
/* 1771 */       (paramString.regionMatches(i, "\\N", 0, 2))) {
/* 1772 */       n = paramString.charAt(i + 1);
/* 1773 */       m = n == 80 ? 1 : 0;
/* 1774 */       k = n == 78 ? 1 : 0;
/* 1775 */       i = Utility.skipWhitespace(paramString, i + 2);
/* 1776 */       if ((i == paramString.length()) || (paramString.charAt(i++) != '{'))
/*      */       {
/* 1778 */         return null;
/*      */       }
/*      */     }
/*      */     else {
/* 1782 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 1786 */     int n = paramString.indexOf(j != 0 ? ":]" : "}", i);
/* 1787 */     if (n < 0)
/*      */     {
/* 1789 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1795 */     int i1 = paramString.indexOf('=', i);
/*      */     String str1;
/* 1797 */     String str2; if ((i1 >= 0) && (i1 < n) && (k == 0))
/*      */     {
/* 1799 */       str1 = paramString.substring(i, i1);
/* 1800 */       str2 = paramString.substring(i1 + 1, n);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1805 */       str1 = paramString.substring(i, n);
/* 1806 */       str2 = "";
/*      */       
/*      */ 
/* 1809 */       if (k != 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1815 */         str2 = str1;
/* 1816 */         str1 = "na";
/*      */       }
/*      */     }
/*      */     
/* 1820 */     applyPropertyAlias(str1, str2, paramSymbolTable);
/*      */     
/* 1822 */     if (m != 0) {
/* 1823 */       complement();
/*      */     }
/*      */     
/*      */ 
/* 1827 */     paramParsePosition.setIndex(n + (j != 0 ? 2 : 1));
/*      */     
/* 1829 */     return this;
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
/*      */   private void applyPropertyPattern(RuleCharacterIterator paramRuleCharacterIterator, StringBuffer paramStringBuffer, SymbolTable paramSymbolTable)
/*      */   {
/* 1844 */     String str = paramRuleCharacterIterator.lookahead();
/* 1845 */     ParsePosition localParsePosition = new ParsePosition(0);
/* 1846 */     applyPropertyPattern(str, localParsePosition, paramSymbolTable);
/* 1847 */     if (localParsePosition.getIndex() == 0) {
/* 1848 */       syntaxError(paramRuleCharacterIterator, "Invalid property pattern");
/*      */     }
/* 1850 */     paramRuleCharacterIterator.jumpahead(localParsePosition.getIndex());
/* 1851 */     paramStringBuffer.append(str.substring(0, localParsePosition.getIndex()));
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\text\normalizer\UnicodeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*      */ package sun.util.locale.provider;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.IOException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.text.BreakIterator;
/*      */ import java.text.CharacterIterator;
/*      */ import java.text.StringCharacterIterator;
/*      */ import java.util.MissingResourceException;
/*      */ import sun.text.CompactByteArray;
/*      */ import sun.text.SupplementaryCharacterData;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class RuleBasedBreakIterator
/*      */   extends BreakIterator
/*      */ {
/*      */   protected static final byte IGNORE = -1;
/*      */   private static final short START_STATE = 1;
/*      */   private static final short STOP_STATE = 0;
/*  239 */   static final byte[] LABEL = { 66, 73, 100, 97, 116, 97, 0 };
/*      */   
/*      */ 
/*      */ 
/*  243 */   static final int LABEL_LENGTH = LABEL.length;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static final byte supportedVersion = 1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int HEADER_LENGTH = 36;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int BMP_INDICES_LENGTH = 512;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  263 */   private CompactByteArray charCategoryTable = null;
/*  264 */   private SupplementaryCharacterData supplementaryCharCategoryTable = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  269 */   private short[] stateTable = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  275 */   private short[] backwardsStateTable = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  281 */   private boolean[] endStates = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  287 */   private boolean[] lookaheadStates = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  293 */   private byte[] additionalData = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int numCategories;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  304 */   private CharacterIterator text = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long checksum;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   RuleBasedBreakIterator(String paramString)
/*      */     throws IOException, MissingResourceException
/*      */   {
/*  321 */     readTables(paramString);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void readTables(String paramString)
/*      */     throws IOException, MissingResourceException
/*      */   {
/*  375 */     byte[] arrayOfByte1 = readFile(paramString);
/*      */     
/*      */ 
/*  378 */     int i = getInt(arrayOfByte1, 0);
/*  379 */     int j = getInt(arrayOfByte1, 4);
/*  380 */     int k = getInt(arrayOfByte1, 8);
/*  381 */     int m = getInt(arrayOfByte1, 12);
/*  382 */     int n = getInt(arrayOfByte1, 16);
/*  383 */     int i1 = getInt(arrayOfByte1, 20);
/*  384 */     int i2 = getInt(arrayOfByte1, 24);
/*  385 */     this.checksum = getLong(arrayOfByte1, 28);
/*      */     
/*      */ 
/*  388 */     this.stateTable = new short[i];
/*  389 */     int i3 = 36;
/*  390 */     for (int i4 = 0; i4 < i; i3 += 2) {
/*  391 */       this.stateTable[i4] = getShort(arrayOfByte1, i3);i4++;
/*      */     }
/*      */     
/*      */ 
/*  395 */     this.backwardsStateTable = new short[j];
/*  396 */     for (i4 = 0; i4 < j; i3 += 2) {
/*  397 */       this.backwardsStateTable[i4] = getShort(arrayOfByte1, i3);i4++;
/*      */     }
/*      */     
/*      */ 
/*  401 */     this.endStates = new boolean[k];
/*  402 */     for (i4 = 0; i4 < k; i3++) {
/*  403 */       this.endStates[i4] = (arrayOfByte1[i3] == 1 ? 1 : false);i4++;
/*      */     }
/*      */     
/*      */ 
/*  407 */     this.lookaheadStates = new boolean[m];
/*  408 */     for (i4 = 0; i4 < m; i3++) {
/*  409 */       this.lookaheadStates[i4] = (arrayOfByte1[i3] == 1 ? 1 : false);i4++;
/*      */     }
/*      */     
/*      */ 
/*  413 */     short[] arrayOfShort = new short['Ȁ'];
/*  414 */     for (int i5 = 0; i5 < 512; i3 += 2) {
/*  415 */       arrayOfShort[i5] = getShort(arrayOfByte1, i3);i5++;
/*      */     }
/*  417 */     byte[] arrayOfByte2 = new byte[n];
/*  418 */     System.arraycopy(arrayOfByte1, i3, arrayOfByte2, 0, n);
/*  419 */     i3 += n;
/*  420 */     this.charCategoryTable = new CompactByteArray(arrayOfShort, arrayOfByte2);
/*      */     
/*      */ 
/*  423 */     int[] arrayOfInt = new int[i1];
/*  424 */     for (int i6 = 0; i6 < i1; i3 += 4) {
/*  425 */       arrayOfInt[i6] = getInt(arrayOfByte1, i3);i6++;
/*      */     }
/*  427 */     this.supplementaryCharCategoryTable = new SupplementaryCharacterData(arrayOfInt);
/*      */     
/*      */ 
/*  430 */     if (i2 > 0) {
/*  431 */       this.additionalData = new byte[i2];
/*  432 */       System.arraycopy(arrayOfByte1, i3, this.additionalData, 0, i2);
/*      */     }
/*      */     
/*      */ 
/*  436 */     this.numCategories = (this.stateTable.length / this.endStates.length);
/*      */   }
/*      */   
/*      */   protected byte[] readFile(final String paramString) throws IOException, MissingResourceException
/*      */   {
/*      */     BufferedInputStream localBufferedInputStream;
/*      */     try
/*      */     {
/*  444 */       localBufferedInputStream = (BufferedInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public BufferedInputStream run() throws Exception
/*      */         {
/*  448 */           return new BufferedInputStream(getClass().getResourceAsStream("/sun/text/resources/" + paramString));
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  454 */       throw new InternalError(localPrivilegedActionException.toString(), localPrivilegedActionException);
/*      */     }
/*      */     
/*  457 */     int i = 0;
/*      */     
/*      */ 
/*  460 */     int j = LABEL_LENGTH + 5;
/*  461 */     byte[] arrayOfByte = new byte[j];
/*  462 */     if (localBufferedInputStream.read(arrayOfByte) != j) {
/*  463 */       throw new MissingResourceException("Wrong header length", paramString, "");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  468 */     for (int k = 0; k < LABEL_LENGTH; i++) {
/*  469 */       if (arrayOfByte[i] != LABEL[i]) {
/*  470 */         throw new MissingResourceException("Wrong magic number", paramString, "");
/*      */       }
/*  468 */       k++;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  476 */     if (arrayOfByte[i] != 1) {
/*  477 */       throw new MissingResourceException("Unsupported version(" + arrayOfByte[i] + ")", paramString, "");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  482 */     j = getInt(arrayOfByte, ++i);
/*  483 */     arrayOfByte = new byte[j];
/*  484 */     if (localBufferedInputStream.read(arrayOfByte) != j) {
/*  485 */       throw new MissingResourceException("Wrong data length", paramString, "");
/*      */     }
/*      */     
/*      */ 
/*  489 */     localBufferedInputStream.close();
/*      */     
/*  491 */     return arrayOfByte;
/*      */   }
/*      */   
/*      */   byte[] getAdditionalData() {
/*  495 */     return this.additionalData;
/*      */   }
/*      */   
/*      */   void setAdditionalData(byte[] paramArrayOfByte) {
/*  499 */     this.additionalData = paramArrayOfByte;
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
/*      */   public Object clone()
/*      */   {
/*  512 */     RuleBasedBreakIterator localRuleBasedBreakIterator = (RuleBasedBreakIterator)super.clone();
/*  513 */     if (this.text != null) {
/*  514 */       localRuleBasedBreakIterator.text = ((CharacterIterator)this.text.clone());
/*      */     }
/*  516 */     return localRuleBasedBreakIterator;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*      */     try
/*      */     {
/*  526 */       if (paramObject == null) {
/*  527 */         return false;
/*      */       }
/*      */       
/*  530 */       RuleBasedBreakIterator localRuleBasedBreakIterator = (RuleBasedBreakIterator)paramObject;
/*  531 */       if (this.checksum != localRuleBasedBreakIterator.checksum) {
/*  532 */         return false;
/*      */       }
/*  534 */       if (this.text == null) {
/*  535 */         return localRuleBasedBreakIterator.text == null;
/*      */       }
/*  537 */       return this.text.equals(localRuleBasedBreakIterator.text);
/*      */     }
/*      */     catch (ClassCastException localClassCastException) {}
/*      */     
/*  541 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  550 */     StringBuilder localStringBuilder = new StringBuilder();
/*  551 */     localStringBuilder.append('[');
/*  552 */     localStringBuilder.append("checksum=0x");
/*  553 */     localStringBuilder.append(Long.toHexString(this.checksum));
/*  554 */     localStringBuilder.append(']');
/*  555 */     return localStringBuilder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  564 */     return (int)this.checksum;
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
/*      */   public int first()
/*      */   {
/*  578 */     CharacterIterator localCharacterIterator = getText();
/*      */     
/*  580 */     localCharacterIterator.first();
/*  581 */     return localCharacterIterator.getIndex();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int last()
/*      */   {
/*  591 */     CharacterIterator localCharacterIterator = getText();
/*      */     
/*      */ 
/*      */ 
/*  595 */     localCharacterIterator.setIndex(localCharacterIterator.getEndIndex());
/*  596 */     return localCharacterIterator.getIndex();
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
/*      */   public int next(int paramInt)
/*      */   {
/*  610 */     int i = current();
/*  611 */     while (paramInt > 0) {
/*  612 */       i = handleNext();
/*  613 */       paramInt--;
/*      */     }
/*  615 */     while (paramInt < 0) {
/*  616 */       i = previous();
/*  617 */       paramInt++;
/*      */     }
/*  619 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int next()
/*      */   {
/*  628 */     return handleNext();
/*      */   }
/*      */   
/*  631 */   private int cachedLastKnownBreak = -1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int previous()
/*      */   {
/*  640 */     CharacterIterator localCharacterIterator = getText();
/*  641 */     if (current() == localCharacterIterator.getBeginIndex()) {
/*  642 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  650 */     int i = current();
/*  651 */     int j = this.cachedLastKnownBreak;
/*  652 */     if ((j >= i) || (j <= -1)) {
/*  653 */       getPrevious();
/*  654 */       j = handlePrevious();
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  659 */       localCharacterIterator.setIndex(j);
/*      */     }
/*  661 */     int k = j;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  666 */     while ((k != -1) && (k < i)) {
/*  667 */       j = k;
/*  668 */       k = handleNext();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  673 */     localCharacterIterator.setIndex(j);
/*  674 */     this.cachedLastKnownBreak = j;
/*  675 */     return j;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int getPrevious()
/*      */   {
/*  682 */     char c1 = this.text.previous();
/*  683 */     if ((Character.isLowSurrogate(c1)) && 
/*  684 */       (this.text.getIndex() > this.text.getBeginIndex())) {
/*  685 */       char c2 = this.text.previous();
/*  686 */       if (Character.isHighSurrogate(c2)) {
/*  687 */         return Character.toCodePoint(c2, c1);
/*      */       }
/*  689 */       this.text.next();
/*      */     }
/*      */     
/*  692 */     return c1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   int getCurrent()
/*      */   {
/*  699 */     char c1 = this.text.current();
/*  700 */     if ((Character.isHighSurrogate(c1)) && 
/*  701 */       (this.text.getIndex() < this.text.getEndIndex())) {
/*  702 */       char c2 = this.text.next();
/*  703 */       this.text.previous();
/*  704 */       if (Character.isLowSurrogate(c2)) {
/*  705 */         return Character.toCodePoint(c1, c2);
/*      */       }
/*      */     }
/*  708 */     return c1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int getCurrentCodePointCount()
/*      */   {
/*  715 */     char c1 = this.text.current();
/*  716 */     if ((Character.isHighSurrogate(c1)) && 
/*  717 */       (this.text.getIndex() < this.text.getEndIndex())) {
/*  718 */       char c2 = this.text.next();
/*  719 */       this.text.previous();
/*  720 */       if (Character.isLowSurrogate(c2)) {
/*  721 */         return 2;
/*      */       }
/*      */     }
/*  724 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   int getNext()
/*      */   {
/*  731 */     int i = this.text.getIndex();
/*  732 */     int j = this.text.getEndIndex();
/*  733 */     if ((i == j) || 
/*  734 */       ((i = i + getCurrentCodePointCount()) >= j)) {
/*  735 */       return 65535;
/*      */     }
/*  737 */     this.text.setIndex(i);
/*  738 */     return getCurrent();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int getNextIndex()
/*      */   {
/*  745 */     int i = this.text.getIndex() + getCurrentCodePointCount();
/*  746 */     int j = this.text.getEndIndex();
/*  747 */     if (i > j) {
/*  748 */       return j;
/*      */     }
/*  750 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static final void checkOffset(int paramInt, CharacterIterator paramCharacterIterator)
/*      */   {
/*  758 */     if ((paramInt < paramCharacterIterator.getBeginIndex()) || (paramInt > paramCharacterIterator.getEndIndex())) {
/*  759 */       throw new IllegalArgumentException("offset out of bounds");
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
/*      */   public int following(int paramInt)
/*      */   {
/*  772 */     CharacterIterator localCharacterIterator = getText();
/*  773 */     checkOffset(paramInt, localCharacterIterator);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  778 */     localCharacterIterator.setIndex(paramInt);
/*  779 */     if (paramInt == localCharacterIterator.getBeginIndex()) {
/*  780 */       this.cachedLastKnownBreak = handleNext();
/*  781 */       return this.cachedLastKnownBreak;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  791 */     int i = this.cachedLastKnownBreak;
/*  792 */     if ((i >= paramInt) || (i <= -1)) {
/*  793 */       i = handlePrevious();
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  798 */       localCharacterIterator.setIndex(i);
/*      */     }
/*  800 */     while ((i != -1) && (i <= paramInt)) {
/*  801 */       i = handleNext();
/*      */     }
/*  803 */     this.cachedLastKnownBreak = i;
/*  804 */     return i;
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
/*      */   public int preceding(int paramInt)
/*      */   {
/*  818 */     CharacterIterator localCharacterIterator = getText();
/*  819 */     checkOffset(paramInt, localCharacterIterator);
/*  820 */     localCharacterIterator.setIndex(paramInt);
/*  821 */     return previous();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isBoundary(int paramInt)
/*      */   {
/*  833 */     CharacterIterator localCharacterIterator = getText();
/*  834 */     checkOffset(paramInt, localCharacterIterator);
/*  835 */     if (paramInt == localCharacterIterator.getBeginIndex()) {
/*  836 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  843 */     return following(paramInt - 1) == paramInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int current()
/*      */   {
/*  853 */     return getText().getIndex();
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
/*      */   public CharacterIterator getText()
/*      */   {
/*  868 */     if (this.text == null) {
/*  869 */       this.text = new StringCharacterIterator("");
/*      */     }
/*  871 */     return this.text;
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
/*      */   public void setText(CharacterIterator paramCharacterIterator)
/*      */   {
/*  886 */     int i = paramCharacterIterator.getEndIndex();
/*      */     int j;
/*      */     try {
/*  889 */       paramCharacterIterator.setIndex(i);
/*  890 */       j = paramCharacterIterator.getIndex() == i ? 1 : 0;
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException) {
/*  893 */       j = 0;
/*      */     }
/*      */     
/*  896 */     if (j != 0) {
/*  897 */       this.text = paramCharacterIterator;
/*      */     }
/*      */     else {
/*  900 */       this.text = new SafeCharIterator(paramCharacterIterator);
/*      */     }
/*  902 */     this.text.first();
/*      */     
/*  904 */     this.cachedLastKnownBreak = -1;
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
/*      */   protected int handleNext()
/*      */   {
/*  921 */     CharacterIterator localCharacterIterator = getText();
/*  922 */     if (localCharacterIterator.getIndex() == localCharacterIterator.getEndIndex()) {
/*  923 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*  927 */     int i = getNextIndex();
/*  928 */     int j = 0;
/*      */     
/*      */ 
/*  931 */     int k = 1;
/*      */     
/*  933 */     int n = getCurrent();
/*      */     
/*      */ 
/*  936 */     while ((n != 65535) && (k != 0))
/*      */     {
/*      */ 
/*      */ 
/*  940 */       int m = lookupCategory(n);
/*      */       
/*      */ 
/*      */ 
/*  944 */       if (m != -1) {
/*  945 */         k = lookupState(k, m);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  952 */       if (this.lookaheadStates[k] != 0) {
/*  953 */         if (this.endStates[k] != 0) {
/*  954 */           i = j;
/*      */         }
/*      */         else {
/*  957 */           j = getNextIndex();
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */       }
/*  964 */       else if (this.endStates[k] != 0) {
/*  965 */         i = getNextIndex();
/*      */       }
/*      */       
/*      */ 
/*  969 */       n = getNext();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  976 */     if ((n == 65535) && (j == localCharacterIterator.getEndIndex())) {
/*  977 */       i = j;
/*      */     }
/*      */     
/*  980 */     localCharacterIterator.setIndex(i);
/*  981 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int handlePrevious()
/*      */   {
/*  992 */     CharacterIterator localCharacterIterator = getText();
/*  993 */     int i = 1;
/*  994 */     int j = 0;
/*  995 */     int k = 0;
/*  996 */     int m = getCurrent();
/*      */     
/*      */ 
/*  999 */     while ((m != 65535) && (i != 0))
/*      */     {
/*      */ 
/*      */ 
/* 1003 */       k = j;
/* 1004 */       j = lookupCategory(m);
/*      */       
/*      */ 
/*      */ 
/* 1008 */       if (j != -1) {
/* 1009 */         i = lookupBackwardState(i, j);
/*      */       }
/*      */       
/*      */ 
/* 1013 */       m = getPrevious();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1021 */     if (m != 65535) {
/* 1022 */       if (k != -1) {
/* 1023 */         getNext();
/* 1024 */         getNext();
/*      */       }
/*      */       else {
/* 1027 */         getNext();
/*      */       }
/*      */     }
/* 1030 */     return localCharacterIterator.getIndex();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int lookupCategory(int paramInt)
/*      */   {
/* 1038 */     if (paramInt < 65536) {
/* 1039 */       return this.charCategoryTable.elementAt((char)paramInt);
/*      */     }
/* 1041 */     return this.supplementaryCharCategoryTable.getValue(paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int lookupState(int paramInt1, int paramInt2)
/*      */   {
/* 1050 */     return this.stateTable[(paramInt1 * this.numCategories + paramInt2)];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int lookupBackwardState(int paramInt1, int paramInt2)
/*      */   {
/* 1058 */     return this.backwardsStateTable[(paramInt1 * this.numCategories + paramInt2)];
/*      */   }
/*      */   
/*      */   static long getLong(byte[] paramArrayOfByte, int paramInt) {
/* 1062 */     long l = paramArrayOfByte[paramInt] & 0xFF;
/* 1063 */     for (int i = 1; i < 8; i++) {
/* 1064 */       l = l << 8 | paramArrayOfByte[(paramInt + i)] & 0xFF;
/*      */     }
/* 1066 */     return l;
/*      */   }
/*      */   
/*      */   static int getInt(byte[] paramArrayOfByte, int paramInt) {
/* 1070 */     int i = paramArrayOfByte[paramInt] & 0xFF;
/* 1071 */     for (int j = 1; j < 4; j++) {
/* 1072 */       i = i << 8 | paramArrayOfByte[(paramInt + j)] & 0xFF;
/*      */     }
/* 1074 */     return i;
/*      */   }
/*      */   
/*      */   static short getShort(byte[] paramArrayOfByte, int paramInt) {
/* 1078 */     short s = (short)(paramArrayOfByte[paramInt] & 0xFF);
/* 1079 */     s = (short)(s << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF);
/* 1080 */     return s;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static final class SafeCharIterator
/*      */     implements CharacterIterator, Cloneable
/*      */   {
/*      */     private CharacterIterator base;
/*      */     
/*      */ 
/*      */     private int rangeStart;
/*      */     
/*      */ 
/*      */     private int rangeLimit;
/*      */     
/*      */ 
/*      */     private int currentIndex;
/*      */     
/*      */ 
/*      */     SafeCharIterator(CharacterIterator paramCharacterIterator)
/*      */     {
/* 1102 */       this.base = paramCharacterIterator;
/* 1103 */       this.rangeStart = paramCharacterIterator.getBeginIndex();
/* 1104 */       this.rangeLimit = paramCharacterIterator.getEndIndex();
/* 1105 */       this.currentIndex = paramCharacterIterator.getIndex();
/*      */     }
/*      */     
/*      */     public char first()
/*      */     {
/* 1110 */       return setIndex(this.rangeStart);
/*      */     }
/*      */     
/*      */     public char last()
/*      */     {
/* 1115 */       return setIndex(this.rangeLimit - 1);
/*      */     }
/*      */     
/*      */     public char current()
/*      */     {
/* 1120 */       if ((this.currentIndex < this.rangeStart) || (this.currentIndex >= this.rangeLimit)) {
/* 1121 */         return 65535;
/*      */       }
/*      */       
/* 1124 */       return this.base.setIndex(this.currentIndex);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public char next()
/*      */     {
/* 1131 */       this.currentIndex += 1;
/* 1132 */       if (this.currentIndex >= this.rangeLimit) {
/* 1133 */         this.currentIndex = this.rangeLimit;
/* 1134 */         return 65535;
/*      */       }
/*      */       
/* 1137 */       return this.base.setIndex(this.currentIndex);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public char previous()
/*      */     {
/* 1144 */       this.currentIndex -= 1;
/* 1145 */       if (this.currentIndex < this.rangeStart) {
/* 1146 */         this.currentIndex = this.rangeStart;
/* 1147 */         return 65535;
/*      */       }
/*      */       
/* 1150 */       return this.base.setIndex(this.currentIndex);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public char setIndex(int paramInt)
/*      */     {
/* 1157 */       if ((paramInt < this.rangeStart) || (paramInt > this.rangeLimit)) {
/* 1158 */         throw new IllegalArgumentException("Invalid position");
/*      */       }
/* 1160 */       this.currentIndex = paramInt;
/* 1161 */       return current();
/*      */     }
/*      */     
/*      */     public int getBeginIndex()
/*      */     {
/* 1166 */       return this.rangeStart;
/*      */     }
/*      */     
/*      */     public int getEndIndex()
/*      */     {
/* 1171 */       return this.rangeLimit;
/*      */     }
/*      */     
/*      */     public int getIndex()
/*      */     {
/* 1176 */       return this.currentIndex;
/*      */     }
/*      */     
/*      */ 
/*      */     public Object clone()
/*      */     {
/* 1182 */       SafeCharIterator localSafeCharIterator = null;
/*      */       try {
/* 1184 */         localSafeCharIterator = (SafeCharIterator)super.clone();
/*      */       }
/*      */       catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 1187 */         throw new Error("Clone not supported: " + localCloneNotSupportedException);
/*      */       }
/*      */       
/* 1190 */       CharacterIterator localCharacterIterator = (CharacterIterator)this.base.clone();
/* 1191 */       localSafeCharIterator.base = localCharacterIterator;
/* 1192 */       return localSafeCharIterator;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\RuleBasedBreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
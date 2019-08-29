/*      */ package sun.text.normalizer;
/*      */ 
/*      */ import java.text.CharacterIterator;
/*      */ import java.text.Normalizer.Form;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class NormalizerBase
/*      */   implements Cloneable
/*      */ {
/*  149 */   private char[] buffer = new char[100];
/*  150 */   private int bufferStart = 0;
/*  151 */   private int bufferPos = 0;
/*  152 */   private int bufferLimit = 0;
/*      */   
/*      */   private UCharacterIterator text;
/*      */   
/*  156 */   private Mode mode = NFC;
/*  157 */   private int options = 0;
/*      */   
/*      */ 
/*      */ 
/*      */   private int currentIndex;
/*      */   
/*      */ 
/*      */ 
/*      */   private int nextIndex;
/*      */   
/*      */ 
/*      */   public static final int UNICODE_3_2 = 32;
/*      */   
/*      */ 
/*      */   public static final int DONE = -1;
/*      */   
/*      */ 
/*      */ 
/*      */   public static class Mode
/*      */   {
/*      */     private int modeValue;
/*      */     
/*      */ 
/*      */ 
/*      */     private Mode(int paramInt)
/*      */     {
/*  183 */       this.modeValue = paramInt;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
/*      */     {
/*  193 */       int i = paramInt2 - paramInt1;
/*  194 */       int j = paramInt4 - paramInt3;
/*  195 */       if (i > j) {
/*  196 */         return i;
/*      */       }
/*  198 */       System.arraycopy(paramArrayOfChar1, paramInt1, paramArrayOfChar2, paramInt3, i);
/*  199 */       return i;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, int paramInt5)
/*      */     {
/*  209 */       return normalize(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, 
/*      */       
/*  211 */         NormalizerImpl.getNX(paramInt5));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected String normalize(String paramString, int paramInt)
/*      */     {
/*  220 */       return paramString;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected int getMinC()
/*      */     {
/*  228 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected int getMask()
/*      */     {
/*  236 */       return -1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected IsPrevBoundary getPrevBoundary()
/*      */     {
/*  244 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected IsNextBoundary getNextBoundary()
/*      */     {
/*  252 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
/*      */     {
/*  261 */       if (paramBoolean) {
/*  262 */         return NormalizerBase.MAYBE;
/*      */       }
/*  264 */       return NormalizerBase.NO;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean isNFSkippable(int paramInt)
/*      */     {
/*  272 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  280 */   public static final Mode NONE = new Mode(1, null);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  286 */   public static final Mode NFD = new NFDMode(2, null);
/*      */   
/*      */   private static final class NFDMode extends Mode {
/*      */     private NFDMode(int paramInt) {
/*  290 */       super(null);
/*      */     }
/*      */     
/*      */ 
/*      */     protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
/*      */     {
/*  296 */       int[] arrayOfInt = new int[1];
/*  297 */       return NormalizerImpl.decompose(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, false, arrayOfInt, paramUnicodeSet);
/*      */     }
/*      */     
/*      */ 
/*      */     protected String normalize(String paramString, int paramInt)
/*      */     {
/*  303 */       return NormalizerBase.decompose(paramString, false, paramInt);
/*      */     }
/*      */     
/*      */     protected int getMinC() {
/*  307 */       return 768;
/*      */     }
/*      */     
/*      */     protected IsPrevBoundary getPrevBoundary() {
/*  311 */       return new IsPrevNFDSafe(null);
/*      */     }
/*      */     
/*      */     protected IsNextBoundary getNextBoundary() {
/*  315 */       return new IsNextNFDSafe(null);
/*      */     }
/*      */     
/*      */     protected int getMask() {
/*  319 */       return 65284;
/*      */     }
/*      */     
/*      */ 
/*      */     protected QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
/*      */     {
/*  325 */       return NormalizerImpl.quickCheck(paramArrayOfChar, paramInt1, paramInt2, 
/*      */       
/*  327 */         NormalizerImpl.getFromIndexesArr(8), 4, 0, paramBoolean, paramUnicodeSet);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean isNFSkippable(int paramInt)
/*      */     {
/*  338 */       return NormalizerImpl.isNFSkippable(paramInt, this, 65284L);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  348 */   public static final Mode NFKD = new NFKDMode(3, null);
/*      */   
/*      */   private static final class NFKDMode extends Mode {
/*      */     private NFKDMode(int paramInt) {
/*  352 */       super(null);
/*      */     }
/*      */     
/*      */ 
/*      */     protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
/*      */     {
/*  358 */       int[] arrayOfInt = new int[1];
/*  359 */       return NormalizerImpl.decompose(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, true, arrayOfInt, paramUnicodeSet);
/*      */     }
/*      */     
/*      */ 
/*      */     protected String normalize(String paramString, int paramInt)
/*      */     {
/*  365 */       return NormalizerBase.decompose(paramString, true, paramInt);
/*      */     }
/*      */     
/*      */     protected int getMinC() {
/*  369 */       return 768;
/*      */     }
/*      */     
/*      */     protected IsPrevBoundary getPrevBoundary() {
/*  373 */       return new IsPrevNFDSafe(null);
/*      */     }
/*      */     
/*      */     protected IsNextBoundary getNextBoundary() {
/*  377 */       return new IsNextNFDSafe(null);
/*      */     }
/*      */     
/*      */     protected int getMask() {
/*  381 */       return 65288;
/*      */     }
/*      */     
/*      */ 
/*      */     protected QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
/*      */     {
/*  387 */       return NormalizerImpl.quickCheck(paramArrayOfChar, paramInt1, paramInt2, 
/*      */       
/*  389 */         NormalizerImpl.getFromIndexesArr(9), 8, 4096, paramBoolean, paramUnicodeSet);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean isNFSkippable(int paramInt)
/*      */     {
/*  400 */       return NormalizerImpl.isNFSkippable(paramInt, this, 65288L);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  410 */   public static final Mode NFC = new NFCMode(4, null);
/*      */   
/*      */   private static final class NFCMode extends Mode {
/*      */     private NFCMode(int paramInt) {
/*  414 */       super(null);
/*      */     }
/*      */     
/*      */     protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
/*      */     {
/*  419 */       return NormalizerImpl.compose(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, 0, paramUnicodeSet);
/*      */     }
/*      */     
/*      */ 
/*      */     protected String normalize(String paramString, int paramInt)
/*      */     {
/*  425 */       return NormalizerBase.compose(paramString, false, paramInt);
/*      */     }
/*      */     
/*      */     protected int getMinC() {
/*  429 */       return NormalizerImpl.getFromIndexesArr(6);
/*      */     }
/*      */     
/*      */     protected IsPrevBoundary getPrevBoundary()
/*      */     {
/*  434 */       return new IsPrevTrueStarter(null);
/*      */     }
/*      */     
/*  437 */     protected IsNextBoundary getNextBoundary() { return new IsNextTrueStarter(null); }
/*      */     
/*      */     protected int getMask() {
/*  440 */       return 65297;
/*      */     }
/*      */     
/*      */     protected QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
/*      */     {
/*  445 */       return NormalizerImpl.quickCheck(paramArrayOfChar, paramInt1, paramInt2, 
/*      */       
/*  447 */         NormalizerImpl.getFromIndexesArr(6), 17, 0, paramBoolean, paramUnicodeSet);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean isNFSkippable(int paramInt)
/*      */     {
/*  457 */       return NormalizerImpl.isNFSkippable(paramInt, this, 65473L);
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
/*  469 */   public static final Mode NFKC = new NFKCMode(5, null);
/*      */   
/*      */   private static final class NFKCMode extends Mode {
/*      */     private NFKCMode(int paramInt) {
/*  473 */       super(null);
/*      */     }
/*      */     
/*      */     protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
/*      */     {
/*  478 */       return NormalizerImpl.compose(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, 4096, paramUnicodeSet);
/*      */     }
/*      */     
/*      */ 
/*      */     protected String normalize(String paramString, int paramInt)
/*      */     {
/*  484 */       return NormalizerBase.compose(paramString, true, paramInt);
/*      */     }
/*      */     
/*  487 */     protected int getMinC() { return NormalizerImpl.getFromIndexesArr(7); }
/*      */     
/*      */ 
/*      */     protected IsPrevBoundary getPrevBoundary()
/*      */     {
/*  492 */       return new IsPrevTrueStarter(null);
/*      */     }
/*      */     
/*  495 */     protected IsNextBoundary getNextBoundary() { return new IsNextTrueStarter(null); }
/*      */     
/*      */     protected int getMask() {
/*  498 */       return 65314;
/*      */     }
/*      */     
/*      */     protected QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
/*      */     {
/*  503 */       return NormalizerImpl.quickCheck(paramArrayOfChar, paramInt1, paramInt2, 
/*      */       
/*  505 */         NormalizerImpl.getFromIndexesArr(7), 34, 4096, paramBoolean, paramUnicodeSet);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean isNFSkippable(int paramInt)
/*      */     {
/*  515 */       return NormalizerImpl.isNFSkippable(paramInt, this, 65474L);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final class QuickCheckResult
/*      */   {
/*      */     private int resultValue;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private QuickCheckResult(int paramInt)
/*      */     {
/*  531 */       this.resultValue = paramInt;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  538 */   public static final QuickCheckResult NO = new QuickCheckResult(0, null);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  544 */   public static final QuickCheckResult YES = new QuickCheckResult(1, null);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  551 */   public static final QuickCheckResult MAYBE = new QuickCheckResult(2, null);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int MAX_BUF_SIZE_COMPOSE = 2;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int MAX_BUF_SIZE_DECOMPOSE = 3;
/*      */   
/*      */ 
/*      */ 
/*      */   public static final int UNICODE_3_2_0_ORIGINAL = 262432;
/*      */   
/*      */ 
/*      */ 
/*      */   public static final int UNICODE_LATEST = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public NormalizerBase(String paramString, Mode paramMode, int paramInt)
/*      */   {
/*  576 */     this.text = UCharacterIterator.getInstance(paramString);
/*  577 */     this.mode = paramMode;
/*  578 */     this.options = paramInt;
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
/*      */   public NormalizerBase(CharacterIterator paramCharacterIterator, Mode paramMode)
/*      */   {
/*  591 */     this(paramCharacterIterator, paramMode, 0);
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
/*      */   public NormalizerBase(CharacterIterator paramCharacterIterator, Mode paramMode, int paramInt)
/*      */   {
/*  610 */     this.text = UCharacterIterator.getInstance(
/*  611 */       (CharacterIterator)paramCharacterIterator.clone());
/*      */     
/*  613 */     this.mode = paramMode;
/*  614 */     this.options = paramInt;
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
/*      */     try
/*      */     {
/*  629 */       NormalizerBase localNormalizerBase = (NormalizerBase)super.clone();
/*  630 */       localNormalizerBase.text = ((UCharacterIterator)this.text.clone());
/*      */       
/*  632 */       if (this.buffer != null) {
/*  633 */         localNormalizerBase.buffer = new char[this.buffer.length];
/*  634 */         System.arraycopy(this.buffer, 0, localNormalizerBase.buffer, 0, this.buffer.length);
/*      */       }
/*  636 */       return localNormalizerBase;
/*      */     }
/*      */     catch (CloneNotSupportedException localCloneNotSupportedException) {
/*  639 */       throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String compose(String paramString, boolean paramBoolean, int paramInt)
/*      */   {
/*      */     char[] arrayOfChar1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     char[] arrayOfChar2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  661 */     if (paramInt == 262432) {
/*  662 */       String str = NormalizerImpl.convert(paramString);
/*  663 */       arrayOfChar1 = new char[str.length() * 2];
/*  664 */       arrayOfChar2 = str.toCharArray();
/*      */     } else {
/*  666 */       arrayOfChar1 = new char[paramString.length() * 2];
/*  667 */       arrayOfChar2 = paramString.toCharArray();
/*      */     }
/*  669 */     int i = 0;
/*      */     
/*  671 */     UnicodeSet localUnicodeSet = NormalizerImpl.getNX(paramInt);
/*      */     
/*      */ 
/*  674 */     paramInt &= 0xCF00;
/*      */     
/*  676 */     if (paramBoolean) {
/*  677 */       paramInt |= 0x1000;
/*      */     }
/*      */     for (;;)
/*      */     {
/*  681 */       i = NormalizerImpl.compose(arrayOfChar2, 0, arrayOfChar2.length, arrayOfChar1, 0, arrayOfChar1.length, paramInt, localUnicodeSet);
/*      */       
/*      */ 
/*  684 */       if (i <= arrayOfChar1.length) {
/*  685 */         return new String(arrayOfChar1, 0, i);
/*      */       }
/*  687 */       arrayOfChar1 = new char[i];
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
/*      */   public static String decompose(String paramString, boolean paramBoolean)
/*      */   {
/*  706 */     return decompose(paramString, paramBoolean, 0);
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
/*      */   public static String decompose(String paramString, boolean paramBoolean, int paramInt)
/*      */   {
/*  722 */     int[] arrayOfInt = new int[1];
/*  723 */     int i = 0;
/*  724 */     UnicodeSet localUnicodeSet = NormalizerImpl.getNX(paramInt);
/*      */     
/*      */ 
/*  727 */     if (paramInt == 262432) {
/*  728 */       String str = NormalizerImpl.convert(paramString);
/*  729 */       arrayOfChar = new char[str.length() * 3];
/*      */       for (;;)
/*      */       {
/*  732 */         i = NormalizerImpl.decompose(str.toCharArray(), 0, str.length(), arrayOfChar, 0, arrayOfChar.length, paramBoolean, arrayOfInt, localUnicodeSet);
/*      */         
/*      */ 
/*  735 */         if (i <= arrayOfChar.length) {
/*  736 */           return new String(arrayOfChar, 0, i);
/*      */         }
/*  738 */         arrayOfChar = new char[i];
/*      */       }
/*      */     }
/*      */     
/*  742 */     char[] arrayOfChar = new char[paramString.length() * 3];
/*      */     for (;;)
/*      */     {
/*  745 */       i = NormalizerImpl.decompose(paramString.toCharArray(), 0, paramString.length(), arrayOfChar, 0, arrayOfChar.length, paramBoolean, arrayOfInt, localUnicodeSet);
/*      */       
/*      */ 
/*  748 */       if (i <= arrayOfChar.length) {
/*  749 */         return new String(arrayOfChar, 0, i);
/*      */       }
/*  751 */       arrayOfChar = new char[i];
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
/*      */   public static int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, Mode paramMode, int paramInt5)
/*      */   {
/*  780 */     int i = paramMode.normalize(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, paramInt5);
/*      */     
/*  782 */     if (i <= paramInt4 - paramInt3) {
/*  783 */       return i;
/*      */     }
/*  785 */     throw new IndexOutOfBoundsException(Integer.toString(i));
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
/*      */   public int current()
/*      */   {
/*  799 */     if ((this.bufferPos < this.bufferLimit) || (nextNormalize())) {
/*  800 */       return getCodePointAt(this.bufferPos);
/*      */     }
/*  802 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int next()
/*      */   {
/*  814 */     if ((this.bufferPos < this.bufferLimit) || (nextNormalize())) {
/*  815 */       int i = getCodePointAt(this.bufferPos);
/*  816 */       this.bufferPos += (i > 65535 ? 2 : 1);
/*  817 */       return i;
/*      */     }
/*  819 */     return -1;
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
/*      */   public int previous()
/*      */   {
/*  832 */     if ((this.bufferPos > 0) || (previousNormalize())) {
/*  833 */       int i = getCodePointAt(this.bufferPos - 1);
/*  834 */       this.bufferPos -= (i > 65535 ? 2 : 1);
/*  835 */       return i;
/*      */     }
/*  837 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reset()
/*      */   {
/*  847 */     this.text.setIndex(0);
/*  848 */     this.currentIndex = (this.nextIndex = 0);
/*  849 */     clearBuffer();
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
/*      */   public void setIndexOnly(int paramInt)
/*      */   {
/*  862 */     this.text.setIndex(paramInt);
/*  863 */     this.currentIndex = (this.nextIndex = paramInt);
/*  864 */     clearBuffer();
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
/*      */   @Deprecated
/*      */   public int setIndex(int paramInt)
/*      */   {
/*  891 */     setIndexOnly(paramInt);
/*  892 */     return current();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public int getBeginIndex()
/*      */   {
/*  905 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public int getEndIndex()
/*      */   {
/*  918 */     return endIndex();
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
/*      */   public int getIndex()
/*      */   {
/*  937 */     if (this.bufferPos < this.bufferLimit) {
/*  938 */       return this.currentIndex;
/*      */     }
/*  940 */     return this.nextIndex;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int endIndex()
/*      */   {
/*  952 */     return this.text.getLength();
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
/*      */   public void setMode(Mode paramMode)
/*      */   {
/*  985 */     this.mode = paramMode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Mode getMode()
/*      */   {
/*  994 */     return this.mode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setText(String paramString)
/*      */   {
/* 1005 */     UCharacterIterator localUCharacterIterator = UCharacterIterator.getInstance(paramString);
/* 1006 */     if (localUCharacterIterator == null) {
/* 1007 */       throw new InternalError("Could not create a new UCharacterIterator");
/*      */     }
/* 1009 */     this.text = localUCharacterIterator;
/* 1010 */     reset();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setText(CharacterIterator paramCharacterIterator)
/*      */   {
/* 1021 */     UCharacterIterator localUCharacterIterator = UCharacterIterator.getInstance(paramCharacterIterator);
/* 1022 */     if (localUCharacterIterator == null) {
/* 1023 */       throw new InternalError("Could not create a new UCharacterIterator");
/*      */     }
/* 1025 */     this.text = localUCharacterIterator;
/* 1026 */     this.currentIndex = (this.nextIndex = 0);
/* 1027 */     clearBuffer();
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
/*      */   private static long getPrevNorm32(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar)
/*      */   {
/* 1049 */     int i = 0;
/*      */     
/* 1051 */     if ((i = paramUCharacterIterator.previous()) == -1) {
/* 1052 */       return 0L;
/*      */     }
/* 1054 */     paramArrayOfChar[0] = ((char)i);
/* 1055 */     paramArrayOfChar[1] = '\000';
/*      */     
/*      */ 
/*      */ 
/* 1059 */     if (paramArrayOfChar[0] < paramInt1)
/* 1060 */       return 0L;
/* 1061 */     if (!UTF16.isSurrogate(paramArrayOfChar[0]))
/* 1062 */       return NormalizerImpl.getNorm32(paramArrayOfChar[0]);
/* 1063 */     if ((UTF16.isLeadSurrogate(paramArrayOfChar[0])) || (paramUCharacterIterator.getIndex() == 0))
/*      */     {
/* 1065 */       paramArrayOfChar[1] = ((char)paramUCharacterIterator.current());
/* 1066 */       return 0L; }
/* 1067 */     if (UTF16.isLeadSurrogate(paramArrayOfChar[1] = (char)paramUCharacterIterator.previous())) {
/* 1068 */       long l = NormalizerImpl.getNorm32(paramArrayOfChar[1]);
/* 1069 */       if ((l & paramInt2) == 0L)
/*      */       {
/*      */ 
/* 1072 */         return 0L;
/*      */       }
/*      */       
/* 1075 */       return NormalizerImpl.getNorm32FromSurrogatePair(l, paramArrayOfChar[0]);
/*      */     }
/*      */     
/*      */ 
/* 1079 */     paramUCharacterIterator.moveIndex(1);
/* 1080 */     return 0L;
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
/*      */   private static final class IsPrevNFDSafe
/*      */     implements IsPrevBoundary
/*      */   {
/*      */     public boolean isPrevBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar)
/*      */     {
/* 1102 */       return NormalizerImpl.isNFDSafe(NormalizerBase.getPrevNorm32(paramUCharacterIterator, paramInt1, paramInt2, paramArrayOfChar), paramInt2, paramInt2 & 0x3F);
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
/*      */   private static final class IsPrevTrueStarter
/*      */     implements IsPrevBoundary
/*      */   {
/*      */     public boolean isPrevBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar)
/*      */     {
/* 1123 */       int i = paramInt2 << 2 & 0xF;
/* 1124 */       long l = NormalizerBase.getPrevNorm32(paramUCharacterIterator, paramInt1, paramInt2 | i, paramArrayOfChar);
/* 1125 */       return NormalizerImpl.isTrueStarter(l, paramInt2, i);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int findPreviousIterationBoundary(UCharacterIterator paramUCharacterIterator, IsPrevBoundary paramIsPrevBoundary, int paramInt1, int paramInt2, char[] paramArrayOfChar, int[] paramArrayOfInt)
/*      */   {
/* 1135 */     char[] arrayOfChar1 = new char[2];
/*      */     
/*      */ 
/*      */ 
/* 1139 */     paramArrayOfInt[0] = paramArrayOfChar.length;
/* 1140 */     arrayOfChar1[0] = '\000';
/* 1141 */     while ((paramUCharacterIterator.getIndex() > 0) && (arrayOfChar1[0] != '￿')) {
/* 1142 */       boolean bool = paramIsPrevBoundary.isPrevBoundary(paramUCharacterIterator, paramInt1, paramInt2, arrayOfChar1);
/*      */       
/*      */ 
/*      */ 
/* 1146 */       if (paramArrayOfInt[0] < (arrayOfChar1[1] == 0 ? 1 : 2))
/*      */       {
/*      */ 
/* 1149 */         char[] arrayOfChar2 = new char[paramArrayOfChar.length * 2];
/*      */         
/* 1151 */         System.arraycopy(paramArrayOfChar, paramArrayOfInt[0], arrayOfChar2, arrayOfChar2.length - (paramArrayOfChar.length - paramArrayOfInt[0]), paramArrayOfChar.length - paramArrayOfInt[0]);
/*      */         
/*      */ 
/*      */ 
/* 1155 */         paramArrayOfInt[0] += arrayOfChar2.length - paramArrayOfChar.length;
/*      */         
/* 1157 */         paramArrayOfChar = arrayOfChar2;
/* 1158 */         arrayOfChar2 = null;
/*      */       }
/*      */       
/*      */ 
/* 1162 */       paramArrayOfChar[(paramArrayOfInt[0] -= 1)] = arrayOfChar1[0];
/* 1163 */       if (arrayOfChar1[1] != 0) {
/* 1164 */         paramArrayOfChar[(paramArrayOfInt[0] -= 1)] = arrayOfChar1[1];
/*      */       }
/*      */       
/*      */ 
/* 1168 */       if (bool) {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1174 */     return paramArrayOfChar.length - paramArrayOfInt[0];
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
/*      */   private static int previous(UCharacterIterator paramUCharacterIterator, char[] paramArrayOfChar, int paramInt1, int paramInt2, Mode paramMode, boolean paramBoolean, boolean[] paramArrayOfBoolean, int paramInt3)
/*      */   {
/* 1190 */     int i2 = paramInt2 - paramInt1;
/* 1191 */     int i = 0;
/*      */     
/* 1193 */     if (paramArrayOfBoolean != null) {
/* 1194 */       paramArrayOfBoolean[0] = false;
/*      */     }
/* 1196 */     int i1 = (char)paramMode.getMinC();
/* 1197 */     int k = paramMode.getMask();
/* 1198 */     IsPrevBoundary localIsPrevBoundary = paramMode.getPrevBoundary();
/*      */     
/* 1200 */     if (localIsPrevBoundary == null) {
/* 1201 */       i = 0;
/* 1202 */       int m; if ((m = paramUCharacterIterator.previous()) >= 0) {
/* 1203 */         i = 1;
/* 1204 */         if (UTF16.isTrailSurrogate((char)m)) {
/* 1205 */           int n = paramUCharacterIterator.previous();
/* 1206 */           if (n != -1) {
/* 1207 */             if (UTF16.isLeadSurrogate((char)n)) {
/* 1208 */               if (i2 >= 2) {
/* 1209 */                 paramArrayOfChar[1] = ((char)m);
/* 1210 */                 i = 2;
/*      */               }
/*      */               
/* 1213 */               m = n;
/*      */             } else {
/* 1215 */               paramUCharacterIterator.moveIndex(1);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1220 */         if (i2 > 0) {
/* 1221 */           paramArrayOfChar[0] = ((char)m);
/*      */         }
/*      */       }
/* 1224 */       return i;
/*      */     }
/*      */     
/* 1227 */     char[] arrayOfChar = new char[100];
/* 1228 */     int[] arrayOfInt = new int[1];
/* 1229 */     int j = findPreviousIterationBoundary(paramUCharacterIterator, localIsPrevBoundary, i1, k, arrayOfChar, arrayOfInt);
/*      */     
/*      */ 
/*      */ 
/* 1233 */     if (j > 0) {
/* 1234 */       if (paramBoolean) {
/* 1235 */         i = normalize(arrayOfChar, arrayOfInt[0], arrayOfInt[0] + j, paramArrayOfChar, paramInt1, paramInt2, paramMode, paramInt3);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1240 */         if (paramArrayOfBoolean != null)
/*      */         {
/* 1242 */           paramArrayOfBoolean[0] = ((i != j) || (Utility.arrayRegionMatches(arrayOfChar, 0, paramArrayOfChar, paramInt1, paramInt2)) ? 1 : false);
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */       }
/* 1249 */       else if (i2 > 0) {
/* 1250 */         System.arraycopy(arrayOfChar, arrayOfInt[0], paramArrayOfChar, 0, j < i2 ? j : i2);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1259 */     return i;
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
/*      */   private static long getNextNorm32(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt)
/*      */   {
/* 1288 */     paramArrayOfInt[0] = paramUCharacterIterator.next();
/* 1289 */     paramArrayOfInt[1] = 0;
/*      */     
/* 1291 */     if (paramArrayOfInt[0] < paramInt1) {
/* 1292 */       return 0L;
/*      */     }
/*      */     
/* 1295 */     long l = NormalizerImpl.getNorm32((char)paramArrayOfInt[0]);
/* 1296 */     if (UTF16.isLeadSurrogate((char)paramArrayOfInt[0])) {
/* 1297 */       if ((paramUCharacterIterator.current() != -1) && 
/* 1298 */         (UTF16.isTrailSurrogate((char)(paramArrayOfInt[1] = paramUCharacterIterator.current())))) {
/* 1299 */         paramUCharacterIterator.moveIndex(1);
/* 1300 */         if ((l & paramInt2) == 0L)
/*      */         {
/* 1302 */           return 0L;
/*      */         }
/*      */         
/* 1305 */         return NormalizerImpl.getNorm32FromSurrogatePair(l, (char)paramArrayOfInt[1]);
/*      */       }
/*      */       
/*      */ 
/* 1309 */       return 0L;
/*      */     }
/*      */     
/* 1312 */     return l;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final class IsNextNFDSafe
/*      */     implements IsNextBoundary
/*      */   {
/*      */     public boolean isNextBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt)
/*      */     {
/* 1326 */       return NormalizerImpl.isNFDSafe(NormalizerBase.getNextNorm32(paramUCharacterIterator, paramInt1, paramInt2, paramArrayOfInt), paramInt2, paramInt2 & 0x3F);
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
/*      */   private static final class IsNextTrueStarter
/*      */     implements IsNextBoundary
/*      */   {
/*      */     public boolean isNextBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt)
/*      */     {
/* 1345 */       int i = paramInt2 << 2 & 0xF;
/* 1346 */       long l = NormalizerBase.getNextNorm32(paramUCharacterIterator, paramInt1, paramInt2 | i, paramArrayOfInt);
/* 1347 */       return NormalizerImpl.isTrueStarter(l, paramInt2, i);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int findNextIterationBoundary(UCharacterIterator paramUCharacterIterator, IsNextBoundary paramIsNextBoundary, int paramInt1, int paramInt2, char[] paramArrayOfChar)
/*      */   {
/* 1356 */     if (paramUCharacterIterator.current() == -1) {
/* 1357 */       return 0;
/*      */     }
/*      */     
/*      */ 
/* 1361 */     int[] arrayOfInt = new int[2];
/* 1362 */     arrayOfInt[0] = paramUCharacterIterator.next();
/* 1363 */     paramArrayOfChar[0] = ((char)arrayOfInt[0]);
/* 1364 */     int i = 1;
/*      */     
/* 1366 */     if ((UTF16.isLeadSurrogate((char)arrayOfInt[0])) && 
/* 1367 */       (paramUCharacterIterator.current() != -1)) {
/* 1368 */       if (UTF16.isTrailSurrogate((char)(arrayOfInt[1] = paramUCharacterIterator.next()))) {
/* 1369 */         paramArrayOfChar[(i++)] = ((char)arrayOfInt[1]);
/*      */       } else {
/* 1371 */         paramUCharacterIterator.moveIndex(-1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1378 */     while (paramUCharacterIterator.current() != -1) {
/* 1379 */       if (paramIsNextBoundary.isNextBoundary(paramUCharacterIterator, paramInt1, paramInt2, arrayOfInt))
/*      */       {
/* 1381 */         paramUCharacterIterator.moveIndex(arrayOfInt[1] == 0 ? -1 : -2);
/* 1382 */         break;
/*      */       }
/* 1384 */       if (i + (arrayOfInt[1] == 0 ? 1 : 2) <= paramArrayOfChar.length) {
/* 1385 */         paramArrayOfChar[(i++)] = ((char)arrayOfInt[0]);
/* 1386 */         if (arrayOfInt[1] != 0) {
/* 1387 */           paramArrayOfChar[(i++)] = ((char)arrayOfInt[1]);
/*      */         }
/*      */       } else {
/* 1390 */         char[] arrayOfChar = new char[paramArrayOfChar.length * 2];
/* 1391 */         System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, i);
/* 1392 */         paramArrayOfChar = arrayOfChar;
/* 1393 */         paramArrayOfChar[(i++)] = ((char)arrayOfInt[0]);
/* 1394 */         if (arrayOfInt[1] != 0) {
/* 1395 */           paramArrayOfChar[(i++)] = ((char)arrayOfInt[1]);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1402 */     return i;
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
/*      */   private static int next(UCharacterIterator paramUCharacterIterator, char[] paramArrayOfChar, int paramInt1, int paramInt2, Mode paramMode, boolean paramBoolean, boolean[] paramArrayOfBoolean, int paramInt3)
/*      */   {
/* 1417 */     int i1 = paramInt2 - paramInt1;
/* 1418 */     int i2 = 0;
/* 1419 */     if (paramArrayOfBoolean != null) {
/* 1420 */       paramArrayOfBoolean[0] = false;
/*      */     }
/*      */     
/* 1423 */     int n = (char)paramMode.getMinC();
/* 1424 */     int i = paramMode.getMask();
/* 1425 */     IsNextBoundary localIsNextBoundary = paramMode.getNextBoundary();
/*      */     
/* 1427 */     if (localIsNextBoundary == null) {
/* 1428 */       i2 = 0;
/* 1429 */       int k = paramUCharacterIterator.next();
/* 1430 */       if (k != -1) {
/* 1431 */         i2 = 1;
/* 1432 */         if (UTF16.isLeadSurrogate((char)k)) {
/* 1433 */           int m = paramUCharacterIterator.next();
/* 1434 */           if (m != -1) {
/* 1435 */             if (UTF16.isTrailSurrogate((char)m)) {
/* 1436 */               if (i1 >= 2) {
/* 1437 */                 paramArrayOfChar[1] = ((char)m);
/* 1438 */                 i2 = 2;
/*      */               }
/*      */             }
/*      */             else {
/* 1442 */               paramUCharacterIterator.moveIndex(-1);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1447 */         if (i1 > 0) {
/* 1448 */           paramArrayOfChar[0] = ((char)k);
/*      */         }
/*      */       }
/* 1451 */       return i2;
/*      */     }
/*      */     
/* 1454 */     char[] arrayOfChar = new char[100];
/* 1455 */     int[] arrayOfInt = new int[1];
/* 1456 */     int j = findNextIterationBoundary(paramUCharacterIterator, localIsNextBoundary, n, i, arrayOfChar);
/*      */     
/* 1458 */     if (j > 0) {
/* 1459 */       if (paramBoolean) {
/* 1460 */         i2 = paramMode.normalize(arrayOfChar, arrayOfInt[0], j, paramArrayOfChar, paramInt1, paramInt2, paramInt3);
/*      */         
/*      */ 
/* 1463 */         if (paramArrayOfBoolean != null)
/*      */         {
/* 1465 */           paramArrayOfBoolean[0] = ((i2 != j) || (Utility.arrayRegionMatches(arrayOfChar, arrayOfInt[0], paramArrayOfChar, paramInt1, i2)) ? 1 : false);
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/* 1471 */       else if (i1 > 0) {
/* 1472 */         System.arraycopy(arrayOfChar, 0, paramArrayOfChar, paramInt1, 
/* 1473 */           Math.min(j, i1));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1480 */     return i2;
/*      */   }
/*      */   
/*      */   private void clearBuffer() {
/* 1484 */     this.bufferLimit = (this.bufferStart = this.bufferPos = 0);
/*      */   }
/*      */   
/*      */   private boolean nextNormalize()
/*      */   {
/* 1489 */     clearBuffer();
/* 1490 */     this.currentIndex = this.nextIndex;
/* 1491 */     this.text.setIndex(this.nextIndex);
/*      */     
/* 1493 */     this.bufferLimit = next(this.text, this.buffer, this.bufferStart, this.buffer.length, this.mode, true, null, this.options);
/*      */     
/* 1495 */     this.nextIndex = this.text.getIndex();
/* 1496 */     return this.bufferLimit > 0;
/*      */   }
/*      */   
/*      */   private boolean previousNormalize()
/*      */   {
/* 1501 */     clearBuffer();
/* 1502 */     this.nextIndex = this.currentIndex;
/* 1503 */     this.text.setIndex(this.currentIndex);
/* 1504 */     this.bufferLimit = previous(this.text, this.buffer, this.bufferStart, this.buffer.length, this.mode, true, null, this.options);
/*      */     
/* 1506 */     this.currentIndex = this.text.getIndex();
/* 1507 */     this.bufferPos = this.bufferLimit;
/* 1508 */     return this.bufferLimit > 0;
/*      */   }
/*      */   
/*      */   private int getCodePointAt(int paramInt) {
/* 1512 */     if (UTF16.isSurrogate(this.buffer[paramInt])) {
/* 1513 */       if (UTF16.isLeadSurrogate(this.buffer[paramInt])) {
/* 1514 */         if ((paramInt + 1 < this.bufferLimit) && 
/* 1515 */           (UTF16.isTrailSurrogate(this.buffer[(paramInt + 1)]))) {
/* 1516 */           return UCharacterProperty.getRawSupplementary(this.buffer[paramInt], this.buffer[(paramInt + 1)]);
/*      */         }
/*      */         
/*      */ 
/*      */       }
/* 1521 */       else if ((UTF16.isTrailSurrogate(this.buffer[paramInt])) && 
/* 1522 */         (paramInt > 0) && (UTF16.isLeadSurrogate(this.buffer[(paramInt - 1)]))) {
/* 1523 */         return UCharacterProperty.getRawSupplementary(this.buffer[(paramInt - 1)], this.buffer[paramInt]);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1530 */     return this.buffer[paramInt];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isNFSkippable(int paramInt, Mode paramMode)
/*      */   {
/* 1539 */     return paramMode.isNFSkippable(paramInt);
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
/*      */   public NormalizerBase(String paramString, Mode paramMode)
/*      */   {
/* 1583 */     this(paramString, paramMode, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String normalize(String paramString, Normalizer.Form paramForm)
/*      */   {
/* 1593 */     return normalize(paramString, paramForm, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String normalize(String paramString, Normalizer.Form paramForm, int paramInt)
/*      */   {
/* 1604 */     int i = paramString.length();
/* 1605 */     int j = 1;
/* 1606 */     if (i < 80) {
/* 1607 */       for (int k = 0; k < i; k++) {
/* 1608 */         if (paramString.charAt(k) > '') {
/* 1609 */           j = 0;
/* 1610 */           break;
/*      */         }
/*      */       }
/*      */     } else {
/* 1614 */       char[] arrayOfChar = paramString.toCharArray();
/* 1615 */       for (int m = 0; m < i; m++) {
/* 1616 */         if (arrayOfChar[m] > '') {
/* 1617 */           j = 0;
/* 1618 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1623 */     switch (paramForm) {
/*      */     case NFC: 
/* 1625 */       return j != 0 ? paramString : NFC.normalize(paramString, paramInt);
/*      */     case NFD: 
/* 1627 */       return j != 0 ? paramString : NFD.normalize(paramString, paramInt);
/*      */     case NFKC: 
/* 1629 */       return j != 0 ? paramString : NFKC.normalize(paramString, paramInt);
/*      */     case NFKD: 
/* 1631 */       return j != 0 ? paramString : NFKD.normalize(paramString, paramInt);
/*      */     }
/*      */     
/* 1634 */     throw new IllegalArgumentException("Unexpected normalization form: " + paramForm);
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
/*      */   public static boolean isNormalized(String paramString, Normalizer.Form paramForm)
/*      */   {
/* 1652 */     return isNormalized(paramString, paramForm, 0);
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
/*      */   public static boolean isNormalized(String paramString, Normalizer.Form paramForm, int paramInt)
/*      */   {
/* 1669 */     switch (paramForm) {
/*      */     case NFC: 
/* 1671 */       return NFC.quickCheck(paramString.toCharArray(), 0, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES;
/*      */     case NFD: 
/* 1673 */       return NFD.quickCheck(paramString.toCharArray(), 0, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES;
/*      */     case NFKC: 
/* 1675 */       return NFKC.quickCheck(paramString.toCharArray(), 0, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES;
/*      */     case NFKD: 
/* 1677 */       return NFKD.quickCheck(paramString.toCharArray(), 0, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES;
/*      */     }
/*      */     
/* 1680 */     throw new IllegalArgumentException("Unexpected normalization form: " + paramForm);
/*      */   }
/*      */   
/*      */   private static abstract interface IsNextBoundary
/*      */   {
/*      */     public abstract boolean isNextBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt);
/*      */   }
/*      */   
/*      */   private static abstract interface IsPrevBoundary
/*      */   {
/*      */     public abstract boolean isPrevBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar);
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\text\normalizer\NormalizerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
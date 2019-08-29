/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.text.CharacterIterator;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
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
/*     */ class DictionaryBasedBreakIterator
/*     */   extends RuleBasedBreakIterator
/*     */ {
/*     */   private BreakDictionary dictionary;
/*     */   private boolean[] categoryFlags;
/*     */   private int dictionaryCharCount;
/*     */   private int[] cachedBreakPositions;
/*     */   private int positionInCache;
/*     */   
/*     */   DictionaryBasedBreakIterator(String paramString1, String paramString2)
/*     */     throws IOException
/*     */   {
/* 118 */     super(paramString1);
/* 119 */     byte[] arrayOfByte = super.getAdditionalData();
/* 120 */     if (arrayOfByte != null) {
/* 121 */       prepareCategoryFlags(arrayOfByte);
/* 122 */       super.setAdditionalData(null);
/*     */     }
/* 124 */     this.dictionary = new BreakDictionary(paramString2);
/*     */   }
/*     */   
/*     */   private void prepareCategoryFlags(byte[] paramArrayOfByte) {
/* 128 */     this.categoryFlags = new boolean[paramArrayOfByte.length];
/* 129 */     for (int i = 0; i < paramArrayOfByte.length; i++) {
/* 130 */       this.categoryFlags[i] = (paramArrayOfByte[i] == 1 ? 1 : false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setText(CharacterIterator paramCharacterIterator)
/*     */   {
/* 136 */     super.setText(paramCharacterIterator);
/* 137 */     this.cachedBreakPositions = null;
/* 138 */     this.dictionaryCharCount = 0;
/* 139 */     this.positionInCache = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int first()
/*     */   {
/* 149 */     this.cachedBreakPositions = null;
/* 150 */     this.dictionaryCharCount = 0;
/* 151 */     this.positionInCache = 0;
/* 152 */     return super.first();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int last()
/*     */   {
/* 162 */     this.cachedBreakPositions = null;
/* 163 */     this.dictionaryCharCount = 0;
/* 164 */     this.positionInCache = 0;
/* 165 */     return super.last();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int previous()
/*     */   {
/* 175 */     CharacterIterator localCharacterIterator = getText();
/*     */     
/*     */ 
/*     */ 
/* 179 */     if ((this.cachedBreakPositions != null) && (this.positionInCache > 0)) {
/* 180 */       this.positionInCache -= 1;
/* 181 */       localCharacterIterator.setIndex(this.cachedBreakPositions[this.positionInCache]);
/* 182 */       return this.cachedBreakPositions[this.positionInCache];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 189 */     this.cachedBreakPositions = null;
/* 190 */     int i = super.previous();
/* 191 */     if (this.cachedBreakPositions != null) {
/* 192 */       this.positionInCache = (this.cachedBreakPositions.length - 2);
/*     */     }
/* 194 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int preceding(int paramInt)
/*     */   {
/* 206 */     CharacterIterator localCharacterIterator = getText();
/* 207 */     checkOffset(paramInt, localCharacterIterator);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */     if ((this.cachedBreakPositions == null) || (paramInt <= this.cachedBreakPositions[0]) || (paramInt > this.cachedBreakPositions[(this.cachedBreakPositions.length - 1)]))
/*     */     {
/* 215 */       this.cachedBreakPositions = null;
/* 216 */       return super.preceding(paramInt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 223 */     this.positionInCache = 0;
/* 224 */     while ((this.positionInCache < this.cachedBreakPositions.length) && (paramInt > this.cachedBreakPositions[this.positionInCache]))
/*     */     {
/* 226 */       this.positionInCache += 1;
/*     */     }
/* 228 */     this.positionInCache -= 1;
/* 229 */     localCharacterIterator.setIndex(this.cachedBreakPositions[this.positionInCache]);
/* 230 */     return localCharacterIterator.getIndex();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int following(int paramInt)
/*     */   {
/* 242 */     CharacterIterator localCharacterIterator = getText();
/* 243 */     checkOffset(paramInt, localCharacterIterator);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 249 */     if ((this.cachedBreakPositions == null) || (paramInt < this.cachedBreakPositions[0]) || (paramInt >= this.cachedBreakPositions[(this.cachedBreakPositions.length - 1)]))
/*     */     {
/* 251 */       this.cachedBreakPositions = null;
/* 252 */       return super.following(paramInt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 259 */     this.positionInCache = 0;
/* 260 */     while ((this.positionInCache < this.cachedBreakPositions.length) && (paramInt >= this.cachedBreakPositions[this.positionInCache]))
/*     */     {
/* 262 */       this.positionInCache += 1;
/*     */     }
/* 264 */     localCharacterIterator.setIndex(this.cachedBreakPositions[this.positionInCache]);
/* 265 */     return localCharacterIterator.getIndex();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int handleNext()
/*     */   {
/* 274 */     CharacterIterator localCharacterIterator = getText();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 279 */     if ((this.cachedBreakPositions == null) || (this.positionInCache == this.cachedBreakPositions.length - 1))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 285 */       int i = localCharacterIterator.getIndex();
/* 286 */       this.dictionaryCharCount = 0;
/* 287 */       int j = super.handleNext();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 292 */       if ((this.dictionaryCharCount > 1) && (j - i > 1)) {
/* 293 */         divideUpDictionaryRange(i, j);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 299 */         this.cachedBreakPositions = null;
/* 300 */         return j;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 307 */     if (this.cachedBreakPositions != null) {
/* 308 */       this.positionInCache += 1;
/* 309 */       localCharacterIterator.setIndex(this.cachedBreakPositions[this.positionInCache]);
/* 310 */       return this.cachedBreakPositions[this.positionInCache];
/*     */     }
/* 312 */     return 55537;
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
/*     */   protected int lookupCategory(int paramInt)
/*     */   {
/* 325 */     int i = super.lookupCategory(paramInt);
/* 326 */     if ((i != -1) && (this.categoryFlags[i] != 0)) {
/* 327 */       this.dictionaryCharCount += 1;
/*     */     }
/* 329 */     return i;
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
/*     */   private void divideUpDictionaryRange(int paramInt1, int paramInt2)
/*     */   {
/* 342 */     CharacterIterator localCharacterIterator = getText();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 348 */     localCharacterIterator.setIndex(paramInt1);
/* 349 */     int i = getCurrent();
/* 350 */     int j = lookupCategory(i);
/* 351 */     while ((j == -1) || (this.categoryFlags[j] == 0)) {
/* 352 */       i = getNext();
/* 353 */       j = lookupCategory(i);
/*     */     }
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
/* 367 */     Object localObject1 = new Stack();
/* 368 */     Stack localStack = new Stack();
/* 369 */     ArrayList localArrayList = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 375 */     int k = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 384 */     int m = localCharacterIterator.getIndex();
/* 385 */     Object localObject2 = null;
/*     */     
/*     */ 
/* 388 */     i = getCurrent();
/*     */     
/*     */ 
/*     */ 
/*     */     for (;;)
/*     */     {
/* 394 */       if (this.dictionary.getNextState(k, 0) == -1) {
/* 395 */         localStack.push(Integer.valueOf(localCharacterIterator.getIndex()));
/*     */       }
/*     */       
/*     */ 
/* 399 */       k = this.dictionary.getNextStateFromCharacter(k, i);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 405 */       if (k == -1) {
/* 406 */         ((Stack)localObject1).push(Integer.valueOf(localCharacterIterator.getIndex()));
/* 407 */         break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 414 */       if ((k == 0) || (localCharacterIterator.getIndex() >= paramInt2))
/*     */       {
/*     */         Object localObject3;
/*     */         
/* 418 */         if (localCharacterIterator.getIndex() > m) {
/* 419 */           m = localCharacterIterator.getIndex();
/*     */           
/*     */ 
/* 422 */           localObject3 = (Stack)((Stack)localObject1).clone();
/*     */           
/* 424 */           localObject2 = localObject3;
/*     */         }
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
/* 440 */         while ((!localStack.isEmpty()) && 
/* 441 */           (localArrayList.contains(localStack.peek()))) {
/* 442 */           localStack.pop();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 450 */         if (localStack.isEmpty()) {
/* 451 */           if (localObject2 != null) {
/* 452 */             localObject1 = localObject2;
/* 453 */             if (m >= paramInt2) break;
/* 454 */             localCharacterIterator.setIndex(m + 1);
/*     */ 
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/* 461 */             if (((((Stack)localObject1).size() == 0) || 
/* 462 */               (((Integer)((Stack)localObject1).peek()).intValue() != localCharacterIterator.getIndex())) && 
/* 463 */               (localCharacterIterator.getIndex() != paramInt1)) {
/* 464 */               ((Stack)localObject1).push(new Integer(localCharacterIterator.getIndex()));
/*     */             }
/* 466 */             getNext();
/* 467 */             ((Stack)localObject1).push(new Integer(localCharacterIterator.getIndex()));
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 477 */           localObject3 = (Integer)localStack.pop();
/* 478 */           Integer localInteger = null;
/* 479 */           while ((!((Stack)localObject1).isEmpty()) && 
/* 480 */             (((Integer)localObject3).intValue() < ((Integer)((Stack)localObject1).peek()).intValue())) {
/* 481 */             localInteger = (Integer)((Stack)localObject1).pop();
/* 482 */             localArrayList.add(localInteger);
/*     */           }
/* 484 */           ((Stack)localObject1).push(localObject3);
/* 485 */           localCharacterIterator.setIndex(((Integer)((Stack)localObject1).peek()).intValue());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 490 */         i = getCurrent();
/* 491 */         if (localCharacterIterator.getIndex() >= paramInt2) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 499 */         i = getNext();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 507 */     if (!((Stack)localObject1).isEmpty()) {
/* 508 */       ((Stack)localObject1).pop();
/*     */     }
/* 510 */     ((Stack)localObject1).push(Integer.valueOf(paramInt2));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 517 */     this.cachedBreakPositions = new int[((Stack)localObject1).size() + 1];
/* 518 */     this.cachedBreakPositions[0] = paramInt1;
/*     */     
/* 520 */     for (int n = 0; n < ((Stack)localObject1).size(); n++) {
/* 521 */       this.cachedBreakPositions[(n + 1)] = ((Integer)((Stack)localObject1).elementAt(n)).intValue();
/*     */     }
/* 523 */     this.positionInCache = 0;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\DictionaryBasedBreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.nio.ch;
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
/*     */ class NativeThreadSet
/*     */ {
/*     */   private long[] elts;
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
/*  35 */   private int used = 0;
/*     */   private boolean waitingToEmpty;
/*     */   
/*     */   NativeThreadSet(int paramInt) {
/*  39 */     this.elts = new long[paramInt];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   int add()
/*     */   {
/*  46 */     long l = NativeThread.current();
/*     */     
/*  48 */     if (l == 0L)
/*  49 */       l = -1L;
/*  50 */     synchronized (this) {
/*  51 */       int i = 0;
/*  52 */       if (this.used >= this.elts.length) {
/*  53 */         j = this.elts.length;
/*  54 */         int k = j * 2;
/*  55 */         long[] arrayOfLong = new long[k];
/*  56 */         System.arraycopy(this.elts, 0, arrayOfLong, 0, j);
/*  57 */         this.elts = arrayOfLong;
/*  58 */         i = j;
/*     */       }
/*  60 */       for (int j = i; j < this.elts.length; j++) {
/*  61 */         if (this.elts[j] == 0L) {
/*  62 */           this.elts[j] = l;
/*  63 */           this.used += 1;
/*  64 */           return j;
/*     */         }
/*     */       }
/*  67 */       if (!$assertionsDisabled) throw new AssertionError();
/*  68 */       return -1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   void remove(int paramInt)
/*     */   {
/*  75 */     synchronized (this) {
/*  76 */       this.elts[paramInt] = 0L;
/*  77 */       this.used -= 1;
/*  78 */       if ((this.used == 0) && (this.waitingToEmpty)) {
/*  79 */         notifyAll();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   synchronized void signalAndWait()
/*     */   {
/*  86 */     int i = 0;
/*  87 */     while (this.used > 0) {
/*  88 */       int j = this.used;
/*  89 */       int k = this.elts.length;
/*  90 */       for (int m = 0; m < k; m++) {
/*  91 */         long l = this.elts[m];
/*  92 */         if (l != 0L)
/*     */         {
/*  94 */           if (l != -1L)
/*  95 */             NativeThread.signal(l);
/*  96 */           j--; if (j == 0) break;
/*     */         }
/*     */       }
/*  99 */       this.waitingToEmpty = true;
/*     */       try {
/* 101 */         wait(50L);
/*     */       } catch (InterruptedException localInterruptedException) {
/* 103 */         i = 1;
/*     */       } finally {
/* 105 */         this.waitingToEmpty = false;
/*     */       }
/*     */     }
/* 108 */     if (i != 0) {
/* 109 */       Thread.currentThread().interrupt();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\NativeThreadSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
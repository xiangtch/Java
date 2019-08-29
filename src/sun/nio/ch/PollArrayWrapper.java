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
/*     */ class PollArrayWrapper
/*     */ {
/*     */   private AllocatedNativeObject pollArray;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   long pollArrayAddress;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final short FD_OFFSET = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final short EVENT_OFFSET = 4;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  54 */   static short SIZE_POLLFD = 8;
/*     */   private int size;
/*     */   
/*     */   PollArrayWrapper(int paramInt)
/*     */   {
/*  59 */     int i = paramInt * SIZE_POLLFD;
/*  60 */     this.pollArray = new AllocatedNativeObject(i, true);
/*  61 */     this.pollArrayAddress = this.pollArray.address();
/*  62 */     this.size = paramInt;
/*     */   }
/*     */   
/*     */   void addEntry(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/*  67 */     putDescriptor(paramInt, paramSelectionKeyImpl.channel.getFDVal());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void replaceEntry(PollArrayWrapper paramPollArrayWrapper1, int paramInt1, PollArrayWrapper paramPollArrayWrapper2, int paramInt2)
/*     */   {
/*  74 */     paramPollArrayWrapper2.putDescriptor(paramInt2, paramPollArrayWrapper1.getDescriptor(paramInt1));
/*  75 */     paramPollArrayWrapper2.putEventOps(paramInt2, paramPollArrayWrapper1.getEventOps(paramInt1));
/*     */   }
/*     */   
/*     */   void grow(int paramInt)
/*     */   {
/*  80 */     PollArrayWrapper localPollArrayWrapper = new PollArrayWrapper(paramInt);
/*  81 */     for (int i = 0; i < this.size; i++)
/*  82 */       replaceEntry(this, i, localPollArrayWrapper, i);
/*  83 */     this.pollArray.free();
/*  84 */     this.pollArray = localPollArrayWrapper.pollArray;
/*  85 */     this.size = localPollArrayWrapper.size;
/*  86 */     this.pollArrayAddress = this.pollArray.address();
/*     */   }
/*     */   
/*     */   void free() {
/*  90 */     this.pollArray.free();
/*     */   }
/*     */   
/*     */   void putDescriptor(int paramInt1, int paramInt2)
/*     */   {
/*  95 */     this.pollArray.putInt(SIZE_POLLFD * paramInt1 + 0, paramInt2);
/*     */   }
/*     */   
/*     */   void putEventOps(int paramInt1, int paramInt2) {
/*  99 */     this.pollArray.putShort(SIZE_POLLFD * paramInt1 + 4, (short)paramInt2);
/*     */   }
/*     */   
/*     */   int getEventOps(int paramInt) {
/* 103 */     return this.pollArray.getShort(SIZE_POLLFD * paramInt + 4);
/*     */   }
/*     */   
/*     */   int getDescriptor(int paramInt) {
/* 107 */     return this.pollArray.getInt(SIZE_POLLFD * paramInt + 0);
/*     */   }
/*     */   
/*     */   void addWakeupSocket(int paramInt1, int paramInt2)
/*     */   {
/* 112 */     putDescriptor(paramInt2, paramInt1);
/* 113 */     putEventOps(paramInt2, Net.POLLIN);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\PollArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
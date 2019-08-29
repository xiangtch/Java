/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.nio.channels.ClosedSelectorException;
/*     */ import java.nio.channels.Pipe;
/*     */ import java.nio.channels.Pipe.SinkChannel;
/*     */ import java.nio.channels.Pipe.SourceChannel;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ final class WindowsSelectorImpl
/*     */   extends SelectorImpl
/*     */ {
/*  53 */   private final int INIT_CAP = 8;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int MAX_SELECTABLE_FDS = 1024;
/*     */   
/*     */ 
/*     */ 
/*  61 */   private SelectionKeyImpl[] channelArray = new SelectionKeyImpl[8];
/*     */   
/*     */ 
/*     */ 
/*     */   private PollArrayWrapper pollWrapper;
/*     */   
/*     */ 
/*  68 */   private int totalChannels = 1;
/*     */   
/*     */ 
/*     */ 
/*  72 */   private int threadsCount = 0;
/*     */   
/*     */ 
/*  75 */   private final List<SelectThread> threads = new ArrayList();
/*     */   
/*     */ 
/*     */   private final Pipe wakeupPipe;
/*     */   
/*     */   private final int wakeupSourceFd;
/*     */   
/*     */   private final int wakeupSinkFd;
/*     */   
/*  84 */   private Object closeLock = new Object();
/*     */   
/*     */   private static final class FdMap extends HashMap<Integer, MapEntry> {
/*     */     static final long serialVersionUID = 0L;
/*     */     
/*     */     private MapEntry get(int paramInt) {
/*  90 */       return (MapEntry)get(new Integer(paramInt));
/*     */     }
/*     */     
/*  93 */     private MapEntry put(SelectionKeyImpl paramSelectionKeyImpl) { return (MapEntry)put(new Integer(paramSelectionKeyImpl.channel.getFDVal()), new MapEntry(paramSelectionKeyImpl)); }
/*     */     
/*     */     private MapEntry remove(SelectionKeyImpl paramSelectionKeyImpl) {
/*  96 */       Integer localInteger = new Integer(paramSelectionKeyImpl.channel.getFDVal());
/*  97 */       MapEntry localMapEntry = (MapEntry)get(localInteger);
/*  98 */       if ((localMapEntry != null) && (localMapEntry.ski.channel == paramSelectionKeyImpl.channel))
/*  99 */         return (MapEntry)remove(localInteger);
/* 100 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class MapEntry
/*     */   {
/*     */     SelectionKeyImpl ski;
/* 107 */     long updateCount = 0L;
/* 108 */     long clearedCount = 0L;
/*     */     
/* 110 */     MapEntry(SelectionKeyImpl paramSelectionKeyImpl) { this.ski = paramSelectionKeyImpl; }
/*     */   }
/*     */   
/* 113 */   private final FdMap fdMap = new FdMap(null);
/*     */   
/*     */ 
/* 116 */   private final SubSelector subSelector = new SubSelector(null);
/*     */   
/*     */ 
/*     */   private long timeout;
/*     */   
/* 121 */   private final Object interruptLock = new Object();
/* 122 */   private volatile boolean interruptTriggered = false;
/*     */   
/*     */   WindowsSelectorImpl(SelectorProvider paramSelectorProvider) throws IOException {
/* 125 */     super(paramSelectorProvider);
/* 126 */     this.pollWrapper = new PollArrayWrapper(8);
/* 127 */     this.wakeupPipe = Pipe.open();
/* 128 */     this.wakeupSourceFd = ((SelChImpl)this.wakeupPipe.source()).getFDVal();
/*     */     
/*     */ 
/* 131 */     SinkChannelImpl localSinkChannelImpl = (SinkChannelImpl)this.wakeupPipe.sink();
/* 132 */     localSinkChannelImpl.sc.socket().setTcpNoDelay(true);
/* 133 */     this.wakeupSinkFd = localSinkChannelImpl.getFDVal();
/*     */     
/* 135 */     this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, 0);
/*     */   }
/*     */   
/*     */   protected int doSelect(long paramLong) throws IOException {
/* 139 */     if (this.channelArray == null)
/* 140 */       throw new ClosedSelectorException();
/* 141 */     this.timeout = paramLong;
/* 142 */     processDeregisterQueue();
/* 143 */     if (this.interruptTriggered) {
/* 144 */       resetWakeupSocket();
/* 145 */       return 0;
/*     */     }
/*     */     
/*     */ 
/* 149 */     adjustThreadsCount();
/* 150 */     this.finishLock.reset();
/*     */     
/*     */ 
/* 153 */     this.startLock.startThreads();
/*     */     
/*     */     try
/*     */     {
/* 157 */       begin();
/*     */       try {
/* 159 */         this.subSelector.poll();
/*     */       } catch (IOException localIOException) {
/* 161 */         this.finishLock.setException(localIOException);
/*     */       }
/*     */       
/* 164 */       if (this.threads.size() > 0)
/* 165 */         this.finishLock.waitForHelperThreads();
/*     */     } finally {
/* 167 */       end();
/*     */     }
/*     */     
/* 170 */     this.finishLock.checkForException();
/* 171 */     processDeregisterQueue();
/* 172 */     int i = updateSelectedKeys();
/*     */     
/* 174 */     resetWakeupSocket();
/* 175 */     return i;
/*     */   }
/*     */   
/*     */ 
/* 179 */   private final StartLock startLock = new StartLock(null);
/*     */   
/*     */   private final class StartLock
/*     */   {
/*     */     private long runsCounter;
/*     */     
/*     */     private StartLock() {}
/*     */     
/*     */     private synchronized void startThreads() {
/* 188 */       this.runsCounter += 1L;
/* 189 */       notifyAll();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private synchronized boolean waitForStart(SelectThread paramSelectThread)
/*     */     {
/* 197 */       while (this.runsCounter == paramSelectThread.lastRun) {
/*     */         try {
/* 199 */           WindowsSelectorImpl.this.startLock.wait();
/*     */         } catch (InterruptedException localInterruptedException) {
/* 201 */           Thread.currentThread().interrupt();
/*     */         }
/*     */       }
/* 204 */       if (paramSelectThread.isZombie()) {
/* 205 */         return true;
/*     */       }
/* 207 */       paramSelectThread.lastRun = this.runsCounter;
/* 208 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 216 */   private final FinishLock finishLock = new FinishLock(null);
/*     */   
/*     */ 
/*     */   private final class FinishLock
/*     */   {
/*     */     private int threadsToFinish;
/*     */     
/* 223 */     IOException exception = null;
/*     */     
/*     */     private FinishLock() {}
/*     */     
/* 227 */     private void reset() { this.threadsToFinish = WindowsSelectorImpl.this.threads.size(); }
/*     */     
/*     */ 
/*     */ 
/*     */     private synchronized void threadFinished()
/*     */     {
/* 233 */       if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size())
/*     */       {
/* 235 */         WindowsSelectorImpl.this.wakeup();
/*     */       }
/* 237 */       this.threadsToFinish -= 1;
/* 238 */       if (this.threadsToFinish == 0) {
/* 239 */         notify();
/*     */       }
/*     */     }
/*     */     
/*     */     private synchronized void waitForHelperThreads()
/*     */     {
/* 245 */       if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size())
/*     */       {
/* 247 */         WindowsSelectorImpl.this.wakeup();
/*     */       }
/* 249 */       while (this.threadsToFinish != 0) {
/*     */         try {
/* 251 */           WindowsSelectorImpl.this.finishLock.wait();
/*     */         }
/*     */         catch (InterruptedException localInterruptedException) {
/* 254 */           Thread.currentThread().interrupt();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     private synchronized void setException(IOException paramIOException)
/*     */     {
/* 261 */       this.exception = paramIOException;
/*     */     }
/*     */     
/*     */     private void checkForException()
/*     */       throws IOException
/*     */     {
/* 267 */       if (this.exception == null)
/* 268 */         return;
/* 269 */       StringBuffer localStringBuffer = new StringBuffer("An exception occurred during the execution of select(): \n");
/*     */       
/* 271 */       localStringBuffer.append(this.exception);
/* 272 */       localStringBuffer.append('\n');
/* 273 */       this.exception = null;
/* 274 */       throw new IOException(localStringBuffer.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private final class SubSelector
/*     */   {
/*     */     private final int pollArrayIndex;
/*     */     
/* 283 */     private final int[] readFds = new int['Ё'];
/* 284 */     private final int[] writeFds = new int['Ё'];
/* 285 */     private final int[] exceptFds = new int['Ё'];
/*     */     
/*     */     private SubSelector() {
/* 288 */       this.pollArrayIndex = 0;
/*     */     }
/*     */     
/*     */     private SubSelector(int paramInt) {
/* 292 */       this.pollArrayIndex = ((paramInt + 1) * 1024);
/*     */     }
/*     */     
/*     */     private int poll() throws IOException {
/* 296 */       return poll0(WindowsSelectorImpl.this.pollWrapper.pollArrayAddress, 
/* 297 */         Math.min(WindowsSelectorImpl.this.totalChannels, 1024), this.readFds, this.writeFds, this.exceptFds, 
/* 298 */         WindowsSelectorImpl.this.timeout);
/*     */     }
/*     */     
/*     */     private int poll(int paramInt) throws IOException
/*     */     {
/* 303 */       return poll0(WindowsSelectorImpl.this.pollWrapper.pollArrayAddress + this.pollArrayIndex * PollArrayWrapper.SIZE_POLLFD, 
/*     */       
/* 305 */         Math.min(1024, 
/* 306 */         WindowsSelectorImpl.this.totalChannels - (paramInt + 1) * 1024), this.readFds, this.writeFds, this.exceptFds, 
/* 307 */         WindowsSelectorImpl.this.timeout);
/*     */     }
/*     */     
/*     */     private native int poll0(long paramLong1, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, long paramLong2);
/*     */     
/*     */     private int processSelectedKeys(long paramLong)
/*     */     {
/* 314 */       int i = 0;
/* 315 */       i += processFDSet(paramLong, this.readFds, Net.POLLIN, false);
/*     */       
/*     */ 
/* 318 */       i += processFDSet(paramLong, this.writeFds, Net.POLLCONN | Net.POLLOUT, false);
/*     */       
/*     */ 
/*     */ 
/* 322 */       i += processFDSet(paramLong, this.exceptFds, Net.POLLIN | Net.POLLCONN | Net.POLLOUT, true);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 327 */       return i;
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
/*     */     private int processFDSet(long paramLong, int[] paramArrayOfInt, int paramInt, boolean paramBoolean)
/*     */     {
/* 341 */       int i = 0;
/* 342 */       for (int j = 1; j <= paramArrayOfInt[0]; j++) {
/* 343 */         int k = paramArrayOfInt[j];
/* 344 */         if (k == WindowsSelectorImpl.this.wakeupSourceFd) {
/* 345 */           synchronized (WindowsSelectorImpl.this.interruptLock) {
/* 346 */             WindowsSelectorImpl.this.interruptTriggered = true;
/*     */           }
/*     */         }
/*     */         else {
/* 350 */           ??? = WindowsSelectorImpl.this.fdMap.get(k);
/*     */           
/*     */ 
/* 353 */           if (??? != null)
/*     */           {
/* 355 */             SelectionKeyImpl localSelectionKeyImpl = ((WindowsSelectorImpl.MapEntry)???).ski;
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 360 */             if ((!paramBoolean) || 
/* 361 */               (!(localSelectionKeyImpl.channel() instanceof SocketChannelImpl)) || 
/* 362 */               (!WindowsSelectorImpl.this.discardUrgentData(k)))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 367 */               if (WindowsSelectorImpl.this.selectedKeys.contains(localSelectionKeyImpl)) {
/* 368 */                 if (((WindowsSelectorImpl.MapEntry)???).clearedCount != paramLong) {
/* 369 */                   if ((localSelectionKeyImpl.channel.translateAndSetReadyOps(paramInt, localSelectionKeyImpl)) && (((WindowsSelectorImpl.MapEntry)???).updateCount != paramLong))
/*     */                   {
/* 371 */                     ((WindowsSelectorImpl.MapEntry)???).updateCount = paramLong;
/* 372 */                     i++;
/*     */                   }
/*     */                 }
/* 375 */                 else if ((localSelectionKeyImpl.channel.translateAndUpdateReadyOps(paramInt, localSelectionKeyImpl)) && (((WindowsSelectorImpl.MapEntry)???).updateCount != paramLong))
/*     */                 {
/* 377 */                   ((WindowsSelectorImpl.MapEntry)???).updateCount = paramLong;
/* 378 */                   i++;
/*     */                 }
/*     */                 
/* 381 */                 ((WindowsSelectorImpl.MapEntry)???).clearedCount = paramLong;
/*     */               } else {
/* 383 */                 if (((WindowsSelectorImpl.MapEntry)???).clearedCount != paramLong) {
/* 384 */                   localSelectionKeyImpl.channel.translateAndSetReadyOps(paramInt, localSelectionKeyImpl);
/* 385 */                   if ((localSelectionKeyImpl.nioReadyOps() & localSelectionKeyImpl.nioInterestOps()) != 0) {
/* 386 */                     WindowsSelectorImpl.this.selectedKeys.add(localSelectionKeyImpl);
/* 387 */                     ((WindowsSelectorImpl.MapEntry)???).updateCount = paramLong;
/* 388 */                     i++;
/*     */                   }
/*     */                 } else {
/* 391 */                   localSelectionKeyImpl.channel.translateAndUpdateReadyOps(paramInt, localSelectionKeyImpl);
/* 392 */                   if ((localSelectionKeyImpl.nioReadyOps() & localSelectionKeyImpl.nioInterestOps()) != 0) {
/* 393 */                     WindowsSelectorImpl.this.selectedKeys.add(localSelectionKeyImpl);
/* 394 */                     ((WindowsSelectorImpl.MapEntry)???).updateCount = paramLong;
/* 395 */                     i++;
/*     */                   }
/*     */                 }
/* 398 */                 ((WindowsSelectorImpl.MapEntry)???).clearedCount = paramLong;
/*     */               } }
/*     */           } } }
/* 401 */       return i;
/*     */     }
/*     */   }
/*     */   
/*     */   private final class SelectThread extends Thread
/*     */   {
/*     */     private final int index;
/*     */     final SubSelector subSelector;
/* 409 */     private long lastRun = 0L;
/*     */     private volatile boolean zombie;
/*     */     
/*     */     private SelectThread(int paramInt) {
/* 413 */       this.index = paramInt;
/* 414 */       this.subSelector = new SubSelector(WindowsSelectorImpl.this, paramInt, null);
/*     */       
/* 416 */       this.lastRun = StartLock.access$2400(WindowsSelectorImpl.this.startLock);
/*     */     }
/*     */     
/* 419 */     void makeZombie() { this.zombie = true; }
/*     */     
/*     */     boolean isZombie() {
/* 422 */       return this.zombie;
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/*     */       for (;;) {
/* 428 */         if (StartLock.access$2500(WindowsSelectorImpl.this.startLock, this)) {
/* 429 */           return;
/*     */         }
/*     */         try {
/* 432 */           SubSelector.access$2600(this.subSelector, this.index);
/*     */         }
/*     */         catch (IOException localIOException) {
/* 435 */           WindowsSelectorImpl.this.finishLock.setException(localIOException);
/*     */         }
/*     */         
/*     */ 
/* 439 */         WindowsSelectorImpl.this.finishLock.threadFinished();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void adjustThreadsCount()
/*     */   {
/*     */     int i;
/* 447 */     if (this.threadsCount > this.threads.size())
/*     */     {
/* 449 */       for (i = this.threads.size(); i < this.threadsCount; i++) {
/* 450 */         SelectThread localSelectThread = new SelectThread(i, null);
/* 451 */         this.threads.add(localSelectThread);
/* 452 */         localSelectThread.setDaemon(true);
/* 453 */         localSelectThread.start();
/*     */       }
/* 455 */     } else if (this.threadsCount < this.threads.size())
/*     */     {
/* 457 */       for (i = this.threads.size() - 1; i >= this.threadsCount; i--) {
/* 458 */         ((SelectThread)this.threads.remove(i)).makeZombie();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void setWakeupSocket() {
/* 464 */     setWakeupSocket0(this.wakeupSinkFd);
/*     */   }
/*     */   
/*     */   private native void setWakeupSocket0(int paramInt);
/*     */   
/*     */   private void resetWakeupSocket() {
/* 470 */     synchronized (this.interruptLock) {
/* 471 */       if (!this.interruptTriggered)
/* 472 */         return;
/* 473 */       resetWakeupSocket0(this.wakeupSourceFd);
/* 474 */       this.interruptTriggered = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private native void resetWakeupSocket0(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 488 */   private long updateCount = 0L;
/*     */   
/*     */   private native boolean discardUrgentData(int paramInt);
/*     */   
/*     */   private int updateSelectedKeys() {
/* 493 */     this.updateCount += 1L;
/* 494 */     int i = 0;
/* 495 */     i += this.subSelector.processSelectedKeys(this.updateCount);
/* 496 */     for (SelectThread localSelectThread : this.threads) {
/* 497 */       i += localSelectThread.subSelector.processSelectedKeys(this.updateCount);
/*     */     }
/* 499 */     return i;
/*     */   }
/*     */   
/*     */   protected void implClose() throws IOException {
/* 503 */     synchronized (this.closeLock) {
/* 504 */       if ((this.channelArray != null) && 
/* 505 */         (this.pollWrapper != null))
/*     */       {
/* 507 */         synchronized (this.interruptLock) {
/* 508 */           this.interruptTriggered = true;
/*     */         }
/* 510 */         this.wakeupPipe.sink().close();
/* 511 */         this.wakeupPipe.source().close();
/* 512 */         Object localObject2; for (int i = 1; i < this.totalChannels; i++) {
/* 513 */           if (i % 1024 != 0) {
/* 514 */             deregister(this.channelArray[i]);
/* 515 */             localObject2 = this.channelArray[i].channel();
/* 516 */             if ((!((SelectableChannel)localObject2).isOpen()) && (!((SelectableChannel)localObject2).isRegistered()))
/* 517 */               ((SelChImpl)localObject2).kill();
/*     */           }
/*     */         }
/* 520 */         this.pollWrapper.free();
/* 521 */         this.pollWrapper = null;
/* 522 */         this.selectedKeys = null;
/* 523 */         this.channelArray = null;
/*     */         
/* 525 */         for (Iterator localIterator = this.threads.iterator(); localIterator.hasNext();) { localObject2 = (SelectThread)localIterator.next();
/* 526 */           ((SelectThread)localObject2).makeZombie(); }
/* 527 */         this.startLock.startThreads();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void implRegister(SelectionKeyImpl paramSelectionKeyImpl)
/*     */   {
/* 534 */     synchronized (this.closeLock) {
/* 535 */       if (this.pollWrapper == null)
/* 536 */         throw new ClosedSelectorException();
/* 537 */       growIfNeeded();
/* 538 */       this.channelArray[this.totalChannels] = paramSelectionKeyImpl;
/* 539 */       paramSelectionKeyImpl.setIndex(this.totalChannels);
/* 540 */       this.fdMap.put(paramSelectionKeyImpl);
/* 541 */       this.keys.add(paramSelectionKeyImpl);
/* 542 */       this.pollWrapper.addEntry(this.totalChannels, paramSelectionKeyImpl);
/* 543 */       this.totalChannels += 1;
/*     */     }
/*     */   }
/*     */   
/*     */   private void growIfNeeded() {
/* 548 */     if (this.channelArray.length == this.totalChannels) {
/* 549 */       int i = this.totalChannels * 2;
/* 550 */       SelectionKeyImpl[] arrayOfSelectionKeyImpl = new SelectionKeyImpl[i];
/* 551 */       System.arraycopy(this.channelArray, 1, arrayOfSelectionKeyImpl, 1, this.totalChannels - 1);
/* 552 */       this.channelArray = arrayOfSelectionKeyImpl;
/* 553 */       this.pollWrapper.grow(i);
/*     */     }
/* 555 */     if (this.totalChannels % 1024 == 0) {
/* 556 */       this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, this.totalChannels);
/* 557 */       this.totalChannels += 1;
/* 558 */       this.threadsCount += 1;
/*     */     }
/*     */   }
/*     */   
/*     */   protected void implDereg(SelectionKeyImpl paramSelectionKeyImpl) throws IOException {
/* 563 */     int i = paramSelectionKeyImpl.getIndex();
/* 564 */     assert (i >= 0);
/* 565 */     synchronized (this.closeLock) {
/* 566 */       if (i != this.totalChannels - 1)
/*     */       {
/* 568 */         SelectionKeyImpl localSelectionKeyImpl = this.channelArray[(this.totalChannels - 1)];
/* 569 */         this.channelArray[i] = localSelectionKeyImpl;
/* 570 */         localSelectionKeyImpl.setIndex(i);
/* 571 */         this.pollWrapper.replaceEntry(this.pollWrapper, this.totalChannels - 1, this.pollWrapper, i);
/*     */       }
/*     */       
/* 574 */       paramSelectionKeyImpl.setIndex(-1);
/*     */     }
/* 576 */     this.channelArray[(this.totalChannels - 1)] = null;
/* 577 */     this.totalChannels -= 1;
/* 578 */     if ((this.totalChannels != 1) && (this.totalChannels % 1024 == 1)) {
/* 579 */       this.totalChannels -= 1;
/* 580 */       this.threadsCount -= 1;
/*     */     }
/* 582 */     this.fdMap.remove(paramSelectionKeyImpl);
/* 583 */     this.keys.remove(paramSelectionKeyImpl);
/* 584 */     this.selectedKeys.remove(paramSelectionKeyImpl);
/* 585 */     deregister(paramSelectionKeyImpl);
/* 586 */     ??? = paramSelectionKeyImpl.channel();
/* 587 */     if ((!((SelectableChannel)???).isOpen()) && (!((SelectableChannel)???).isRegistered()))
/* 588 */       ((SelChImpl)???).kill();
/*     */   }
/*     */   
/*     */   public void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt) {
/* 592 */     synchronized (this.closeLock) {
/* 593 */       if (this.pollWrapper == null) {
/* 594 */         throw new ClosedSelectorException();
/*     */       }
/* 596 */       int i = paramSelectionKeyImpl.getIndex();
/* 597 */       if (i == -1)
/* 598 */         throw new CancelledKeyException();
/* 599 */       this.pollWrapper.putEventOps(i, paramInt);
/*     */     }
/*     */   }
/*     */   
/*     */   public Selector wakeup() {
/* 604 */     synchronized (this.interruptLock) {
/* 605 */       if (!this.interruptTriggered) {
/* 606 */         setWakeupSocket();
/* 607 */         this.interruptTriggered = true;
/*     */       }
/*     */     }
/* 610 */     return this;
/*     */   }
/*     */   
/*     */   static {
/* 614 */     IOUtil.load();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\WindowsSelectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
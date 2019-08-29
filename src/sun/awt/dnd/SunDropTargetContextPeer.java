/*     */ package sun.awt.dnd;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.awt.dnd.DropTarget;
/*     */ import java.awt.dnd.DropTargetContext;
/*     */ import java.awt.dnd.DropTargetDragEvent;
/*     */ import java.awt.dnd.DropTargetDropEvent;
/*     */ import java.awt.dnd.DropTargetEvent;
/*     */ import java.awt.dnd.DropTargetListener;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.awt.dnd.peer.DropTargetContextPeer;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.datatransfer.DataTransferer;
/*     */ import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
/*     */ import sun.security.util.SecurityConstants.AWT;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ import sun.util.logging.PlatformLogger.Level;
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
/*     */ public abstract class SunDropTargetContextPeer
/*     */   implements DropTargetContextPeer, Transferable
/*     */ {
/*     */   public static final boolean DISPATCH_SYNC = true;
/*     */   private DropTarget currentDT;
/*     */   private DropTargetContext currentDTC;
/*     */   private long[] currentT;
/*     */   private int currentA;
/*     */   private int currentSA;
/*     */   private int currentDA;
/*     */   private int previousDA;
/*     */   private long nativeDragContext;
/*     */   private Transferable local;
/*  92 */   private boolean dragRejected = false;
/*     */   
/*  94 */   protected int dropStatus = 0;
/*  95 */   protected boolean dropComplete = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 100 */   boolean dropInProcess = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */   protected static final Object _globalLock = new Object();
/*     */   
/* 108 */   private static final PlatformLogger dndLog = PlatformLogger.getLogger("sun.awt.dnd.SunDropTargetContextPeer");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 114 */   protected static Transferable currentJVMLocalSourceTransferable = null;
/*     */   protected static final int STATUS_NONE = 0;
/*     */   
/* 117 */   public static void setCurrentJVMLocalSourceTransferable(Transferable paramTransferable) throws InvalidDnDOperationException { synchronized (_globalLock) {
/* 118 */       if ((paramTransferable != null) && (currentJVMLocalSourceTransferable != null)) {
/* 119 */         throw new InvalidDnDOperationException();
/*     */       }
/* 121 */       currentJVMLocalSourceTransferable = paramTransferable;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Transferable getJVMLocalSourceTransferable()
/*     */   {
/* 131 */     return currentJVMLocalSourceTransferable;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static final int STATUS_WAIT = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static final int STATUS_ACCEPT = 2;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static final int STATUS_REJECT = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DropTarget getDropTarget()
/*     */   {
/* 155 */     return this.currentDT;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void setTargetActions(int paramInt)
/*     */   {
/* 162 */     this.currentA = (paramInt & 0x40000003);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getTargetActions()
/*     */   {
/* 171 */     return this.currentA;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Transferable getTransferable()
/*     */   {
/* 179 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DataFlavor[] getTransferDataFlavors()
/*     */   {
/* 189 */     Transferable localTransferable = this.local;
/*     */     
/* 191 */     if (localTransferable != null) {
/* 192 */       return localTransferable.getTransferDataFlavors();
/*     */     }
/* 194 */     return 
/* 195 */       DataTransferer.getInstance().getFlavorsForFormatsAsArray(this.currentT, 
/* 196 */       DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
/*     */   {
/* 205 */     Transferable localTransferable = this.local;
/*     */     
/* 207 */     if (localTransferable != null) {
/* 208 */       return localTransferable.isDataFlavorSupported(paramDataFlavor);
/*     */     }
/* 210 */     return 
/*     */     
/*     */ 
/* 213 */       DataTransferer.getInstance().getFlavorsForFormats(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap())).containsKey(paramDataFlavor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getTransferData(DataFlavor paramDataFlavor)
/*     */     throws UnsupportedFlavorException, IOException, InvalidDnDOperationException
/*     */   {
/* 226 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*     */     try {
/* 228 */       if ((!this.dropInProcess) && (localSecurityManager != null)) {
/* 229 */         localSecurityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
/*     */       }
/*     */     } catch (Exception localException) {
/* 232 */       localObject1 = Thread.currentThread();
/* 233 */       ((Thread)localObject1).getUncaughtExceptionHandler().uncaughtException((Thread)localObject1, localException);
/* 234 */       return null;
/*     */     }
/*     */     
/* 237 */     Long localLong = null;
/* 238 */     Object localObject1 = this.local;
/*     */     
/* 240 */     if (localObject1 != null) {
/* 241 */       return ((Transferable)localObject1).getTransferData(paramDataFlavor);
/*     */     }
/*     */     
/* 244 */     if ((this.dropStatus != 2) || (this.dropComplete)) {
/* 245 */       throw new InvalidDnDOperationException("No drop current");
/*     */     }
/*     */     
/*     */ 
/* 249 */     Map localMap = DataTransferer.getInstance().getFlavorsForFormats(this.currentT, 
/* 250 */       DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap()));
/*     */     
/* 252 */     localLong = (Long)localMap.get(paramDataFlavor);
/* 253 */     if (localLong == null) {
/* 254 */       throw new UnsupportedFlavorException(paramDataFlavor);
/*     */     }
/*     */     
/* 257 */     if ((paramDataFlavor.isRepresentationClassRemote()) && (this.currentDA != 1073741824))
/*     */     {
/* 259 */       throw new InvalidDnDOperationException("only ACTION_LINK is permissable for transfer of java.rmi.Remote objects");
/*     */     }
/*     */     
/* 262 */     long l = localLong.longValue();
/*     */     
/* 264 */     Object localObject2 = getNativeData(l);
/*     */     
/* 266 */     if ((localObject2 instanceof byte[]))
/*     */       try {
/* 268 */         return 
/* 269 */           DataTransferer.getInstance().translateBytes((byte[])localObject2, paramDataFlavor, l, this);
/*     */       } catch (IOException localIOException1) {
/* 271 */         throw new InvalidDnDOperationException(localIOException1.getMessage());
/*     */       }
/* 273 */     if ((localObject2 instanceof InputStream)) {
/*     */       try {
/* 275 */         return 
/* 276 */           DataTransferer.getInstance().translateStream((InputStream)localObject2, paramDataFlavor, l, this);
/*     */       } catch (IOException localIOException2) {
/* 278 */         throw new InvalidDnDOperationException(localIOException2.getMessage());
/*     */       }
/*     */     }
/* 281 */     throw new IOException("no native data was transfered");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract Object getNativeData(long paramLong)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */   public boolean isTransferableJVMLocal()
/*     */   {
/* 292 */     return (this.local != null) || (getJVMLocalSourceTransferable() != null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int handleEnterMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong)
/*     */   {
/* 300 */     return postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 504, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processEnterMessage(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/* 311 */     Component localComponent = (Component)paramSunDropTargetEvent.getSource();
/* 312 */     DropTarget localDropTarget = localComponent.getDropTarget();
/* 313 */     Point localPoint = paramSunDropTargetEvent.getPoint();
/*     */     
/* 315 */     this.local = getJVMLocalSourceTransferable();
/*     */     
/* 317 */     if (this.currentDTC != null) {
/* 318 */       this.currentDTC.removeNotify();
/* 319 */       this.currentDTC = null;
/*     */     }
/*     */     
/* 322 */     if ((localComponent.isShowing()) && (localDropTarget != null) && (localDropTarget.isActive())) {
/* 323 */       this.currentDT = localDropTarget;
/* 324 */       this.currentDTC = this.currentDT.getDropTargetContext();
/*     */       
/* 326 */       this.currentDTC.addNotify(this);
/*     */       
/* 328 */       this.currentA = localDropTarget.getDefaultActions();
/*     */       try
/*     */       {
/* 331 */         localDropTarget.dragEnter(new DropTargetDragEvent(this.currentDTC, localPoint, this.currentDA, this.currentSA));
/*     */ 
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 336 */         localException.printStackTrace();
/* 337 */         this.currentDA = 0;
/*     */       }
/*     */     } else {
/* 340 */       this.currentDT = null;
/* 341 */       this.currentDTC = null;
/* 342 */       this.currentDA = 0;
/* 343 */       this.currentSA = 0;
/* 344 */       this.currentA = 0;
/*     */     }
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
/*     */ 
/*     */   private void handleExitMessage(Component paramComponent, long paramLong)
/*     */   {
/* 359 */     postDropTargetEvent(paramComponent, 0, 0, 0, 0, null, paramLong, 505, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processExitMessage(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/* 370 */     Component localComponent = (Component)paramSunDropTargetEvent.getSource();
/* 371 */     DropTarget localDropTarget = localComponent.getDropTarget();
/* 372 */     DropTargetContext localDropTargetContext = null;
/*     */     
/* 374 */     if (localDropTarget == null) {
/* 375 */       this.currentDT = null;
/* 376 */       this.currentT = null;
/*     */       
/* 378 */       if (this.currentDTC != null) {
/* 379 */         this.currentDTC.removeNotify();
/*     */       }
/*     */       
/* 382 */       this.currentDTC = null;
/*     */       
/* 384 */       return;
/*     */     }
/*     */     
/* 387 */     if (localDropTarget != this.currentDT)
/*     */     {
/* 389 */       if (this.currentDTC != null) {
/* 390 */         this.currentDTC.removeNotify();
/*     */       }
/*     */       
/* 393 */       this.currentDT = localDropTarget;
/* 394 */       this.currentDTC = localDropTarget.getDropTargetContext();
/*     */       
/* 396 */       this.currentDTC.addNotify(this);
/*     */     }
/*     */     
/* 399 */     localDropTargetContext = this.currentDTC;
/*     */     
/* 401 */     if (localDropTarget.isActive()) {
/* 402 */       try { localDropTarget.dragExit(new DropTargetEvent(localDropTargetContext));
/*     */       } catch (Exception localException) {
/* 404 */         localException.printStackTrace();
/*     */       } finally {
/* 406 */         this.currentA = 0;
/* 407 */         this.currentSA = 0;
/* 408 */         this.currentDA = 0;
/* 409 */         this.currentDT = null;
/* 410 */         this.currentT = null;
/*     */         
/* 412 */         this.currentDTC.removeNotify();
/* 413 */         this.currentDTC = null;
/*     */         
/* 415 */         this.local = null;
/*     */         
/* 417 */         this.dragRejected = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int handleMotionMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong)
/*     */   {
/* 426 */     return postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 506, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processMotionMessage(SunDropTargetEvent paramSunDropTargetEvent, boolean paramBoolean)
/*     */   {
/* 438 */     Component localComponent = (Component)paramSunDropTargetEvent.getSource();
/* 439 */     Point localPoint = paramSunDropTargetEvent.getPoint();
/* 440 */     int i = paramSunDropTargetEvent.getID();
/* 441 */     DropTarget localDropTarget1 = localComponent.getDropTarget();
/* 442 */     DropTargetContext localDropTargetContext = null;
/*     */     
/* 444 */     if ((localComponent.isShowing()) && (localDropTarget1 != null) && (localDropTarget1.isActive())) {
/* 445 */       if (this.currentDT != localDropTarget1) {
/* 446 */         if (this.currentDTC != null) {
/* 447 */           this.currentDTC.removeNotify();
/*     */         }
/*     */         
/* 450 */         this.currentDT = localDropTarget1;
/* 451 */         this.currentDTC = null;
/*     */       }
/*     */       
/* 454 */       localDropTargetContext = this.currentDT.getDropTargetContext();
/* 455 */       if (localDropTargetContext != this.currentDTC) {
/* 456 */         if (this.currentDTC != null) {
/* 457 */           this.currentDTC.removeNotify();
/*     */         }
/*     */         
/* 460 */         this.currentDTC = localDropTargetContext;
/* 461 */         this.currentDTC.addNotify(this);
/*     */       }
/*     */       
/* 464 */       this.currentA = this.currentDT.getDefaultActions();
/*     */       try
/*     */       {
/* 467 */         DropTargetDragEvent localDropTargetDragEvent = new DropTargetDragEvent(localDropTargetContext, localPoint, this.currentDA, this.currentSA);
/*     */         
/*     */ 
/*     */ 
/* 471 */         DropTarget localDropTarget2 = localDropTarget1;
/* 472 */         if (paramBoolean) {
/* 473 */           localDropTarget2.dropActionChanged(localDropTargetDragEvent);
/*     */         } else {
/* 475 */           localDropTarget2.dragOver(localDropTargetDragEvent);
/*     */         }
/*     */         
/* 478 */         if (this.dragRejected) {
/* 479 */           this.currentDA = 0;
/*     */         }
/*     */       } catch (Exception localException) {
/* 482 */         localException.printStackTrace();
/* 483 */         this.currentDA = 0;
/*     */       }
/*     */     } else {
/* 486 */       this.currentDA = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void handleDropMessage(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong)
/*     */   {
/* 499 */     postDropTargetEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfLong, paramLong, 502, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processDropMessage(SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/* 510 */     Component localComponent = (Component)paramSunDropTargetEvent.getSource();
/* 511 */     Point localPoint = paramSunDropTargetEvent.getPoint();
/* 512 */     DropTarget localDropTarget = localComponent.getDropTarget();
/*     */     
/* 514 */     this.dropStatus = 1;
/* 515 */     this.dropComplete = false;
/*     */     
/* 517 */     if ((localComponent.isShowing()) && (localDropTarget != null) && (localDropTarget.isActive())) {
/* 518 */       DropTargetContext localDropTargetContext = localDropTarget.getDropTargetContext();
/*     */       
/* 520 */       this.currentDT = localDropTarget;
/*     */       
/* 522 */       if (this.currentDTC != null) {
/* 523 */         this.currentDTC.removeNotify();
/*     */       }
/*     */       
/* 526 */       this.currentDTC = localDropTargetContext;
/* 527 */       this.currentDTC.addNotify(this);
/* 528 */       this.currentA = localDropTarget.getDefaultActions();
/*     */       
/* 530 */       synchronized (_globalLock) {
/* 531 */         if ((this.local = getJVMLocalSourceTransferable()) != null) {
/* 532 */           setCurrentJVMLocalSourceTransferable(null);
/*     */         }
/*     */       }
/* 535 */       this.dropInProcess = true;
/*     */       try
/*     */       {
/* 538 */         localDropTarget.drop(new DropTargetDropEvent(localDropTargetContext, localPoint, this.currentDA, this.currentSA, this.local != null));
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/* 544 */         if (this.dropStatus == 1) {
/* 545 */           rejectDrop();
/* 546 */         } else if (!this.dropComplete) {
/* 547 */           dropComplete(false);
/*     */         }
/* 549 */         this.dropInProcess = false;
/*     */       }
/*     */     } else {
/* 552 */       rejectDrop();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int postDropTargetEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, long paramLong, int paramInt5, boolean paramBoolean)
/*     */   {
/* 564 */     AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
/*     */     
/* 566 */     EventDispatcher localEventDispatcher = new EventDispatcher(this, paramInt3, paramInt4, paramArrayOfLong, paramLong, paramBoolean);
/*     */     
/*     */ 
/*     */ 
/* 570 */     SunDropTargetEvent localSunDropTargetEvent = new SunDropTargetEvent(paramComponent, paramInt5, paramInt1, paramInt2, localEventDispatcher);
/*     */     
/*     */ 
/* 573 */     if (paramBoolean == true) {
/* 574 */       DataTransferer.getInstance().getToolkitThreadBlockedHandler().lock();
/*     */     }
/*     */     
/*     */ 
/* 578 */     SunToolkit.postEvent(localAppContext, localSunDropTargetEvent);
/*     */     
/* 580 */     eventPosted(localSunDropTargetEvent);
/*     */     
/* 582 */     if (paramBoolean == true) {
/* 583 */       while (!localEventDispatcher.isDone()) {
/* 584 */         DataTransferer.getInstance().getToolkitThreadBlockedHandler().enter();
/*     */       }
/*     */       
/* 587 */       DataTransferer.getInstance().getToolkitThreadBlockedHandler().unlock();
/*     */       
/*     */ 
/* 590 */       return localEventDispatcher.getReturnValue();
/*     */     }
/* 592 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void acceptDrag(int paramInt)
/*     */   {
/* 601 */     if (this.currentDT == null) {
/* 602 */       throw new InvalidDnDOperationException("No Drag pending");
/*     */     }
/* 604 */     this.currentDA = mapOperation(paramInt);
/* 605 */     if (this.currentDA != 0) {
/* 606 */       this.dragRejected = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void rejectDrag()
/*     */   {
/* 615 */     if (this.currentDT == null) {
/* 616 */       throw new InvalidDnDOperationException("No Drag pending");
/*     */     }
/* 618 */     this.currentDA = 0;
/* 619 */     this.dragRejected = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void acceptDrop(int paramInt)
/*     */   {
/* 627 */     if (paramInt == 0) {
/* 628 */       throw new IllegalArgumentException("invalid acceptDrop() action");
/*     */     }
/* 630 */     if ((this.dropStatus == 1) || (this.dropStatus == 2)) {
/* 631 */       this.currentDA = (this.currentA = mapOperation(paramInt & this.currentSA));
/*     */       
/* 633 */       this.dropStatus = 2;
/* 634 */       this.dropComplete = false;
/*     */     } else {
/* 636 */       throw new InvalidDnDOperationException("invalid acceptDrop()");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void rejectDrop()
/*     */   {
/* 645 */     if (this.dropStatus != 1) {
/* 646 */       throw new InvalidDnDOperationException("invalid rejectDrop()");
/*     */     }
/* 648 */     this.dropStatus = -1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 655 */     this.currentDA = 0;
/* 656 */     dropComplete(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int mapOperation(int paramInt)
/*     */   {
/* 664 */     int[] arrayOfInt = { 2, 1, 1073741824 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 669 */     int i = 0;
/*     */     
/* 671 */     for (int j = 0; j < arrayOfInt.length; j++) {
/* 672 */       if ((paramInt & arrayOfInt[j]) == arrayOfInt[j]) {
/* 673 */         i = arrayOfInt[j];
/* 674 */         break;
/*     */       }
/*     */     }
/*     */     
/* 678 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void dropComplete(boolean paramBoolean)
/*     */   {
/* 686 */     if (this.dropStatus == 0) {
/* 687 */       throw new InvalidDnDOperationException("No Drop pending");
/*     */     }
/*     */     
/* 690 */     if (this.currentDTC != null) { this.currentDTC.removeNotify();
/*     */     }
/* 692 */     this.currentDT = null;
/* 693 */     this.currentDTC = null;
/* 694 */     this.currentT = null;
/* 695 */     this.currentA = 0;
/*     */     
/* 697 */     synchronized (_globalLock) {
/* 698 */       currentJVMLocalSourceTransferable = null;
/*     */     }
/*     */     
/* 701 */     this.dropStatus = 0;
/* 702 */     this.dropComplete = true;
/*     */     try
/*     */     {
/* 705 */       doDropDone(paramBoolean, this.currentDA, this.local != null);
/*     */     } finally {
/* 707 */       this.currentDA = 0;
/*     */       
/*     */ 
/* 710 */       this.nativeDragContext = 0L;
/*     */     }
/*     */   }
/*     */   
/*     */   protected abstract void doDropDone(boolean paramBoolean1, int paramInt, boolean paramBoolean2);
/*     */   
/*     */   protected synchronized long getNativeDragContext()
/*     */   {
/* 718 */     return this.nativeDragContext;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void eventPosted(SunDropTargetEvent paramSunDropTargetEvent) {}
/*     */   
/*     */ 
/*     */   protected void eventProcessed(SunDropTargetEvent paramSunDropTargetEvent, int paramInt, boolean paramBoolean) {}
/*     */   
/*     */ 
/*     */   protected static class EventDispatcher
/*     */   {
/*     */     private final SunDropTargetContextPeer peer;
/*     */     private final int dropAction;
/*     */     private final int actions;
/*     */     private final long[] formats;
/*     */     private long nativeCtxt;
/*     */     private final boolean dispatchType;
/* 736 */     private boolean dispatcherDone = false;
/*     */     
/*     */ 
/* 739 */     private int returnValue = 0;
/*     */     
/* 741 */     private final HashSet eventSet = new HashSet(3);
/*     */     
/*     */ 
/* 744 */     static final ToolkitThreadBlockedHandler handler = DataTransferer.getInstance().getToolkitThreadBlockedHandler();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     EventDispatcher(SunDropTargetContextPeer paramSunDropTargetContextPeer, int paramInt1, int paramInt2, long[] paramArrayOfLong, long paramLong, boolean paramBoolean)
/*     */     {
/* 753 */       this.peer = paramSunDropTargetContextPeer;
/* 754 */       this.nativeCtxt = paramLong;
/* 755 */       this.dropAction = paramInt1;
/* 756 */       this.actions = paramInt2;
/*     */       
/* 758 */       this.formats = (null == paramArrayOfLong ? null : Arrays.copyOf(paramArrayOfLong, paramArrayOfLong.length));
/* 759 */       this.dispatchType = paramBoolean;
/*     */     }
/*     */     
/*     */     void dispatchEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 763 */       int i = paramSunDropTargetEvent.getID();
/*     */       
/* 765 */       switch (i) {
/*     */       case 504: 
/* 767 */         dispatchEnterEvent(paramSunDropTargetEvent);
/* 768 */         break;
/*     */       case 506: 
/* 770 */         dispatchMotionEvent(paramSunDropTargetEvent);
/* 771 */         break;
/*     */       case 505: 
/* 773 */         dispatchExitEvent(paramSunDropTargetEvent);
/* 774 */         break;
/*     */       case 502: 
/* 776 */         dispatchDropEvent(paramSunDropTargetEvent);
/* 777 */         break;
/*     */       case 503: default: 
/* 779 */         throw new InvalidDnDOperationException();
/*     */       }
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     private void dispatchEnterEvent(SunDropTargetEvent paramSunDropTargetEvent)
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   4: dup
/*     */       //   5: astore_2
/*     */       //   6: monitorenter
/*     */       //   7: aload_0
/*     */       //   8: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   11: aload_0
/*     */       //   12: getfield 175	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dropAction	I
/*     */       //   15: invokestatic 201	sun/awt/dnd/SunDropTargetContextPeer:access$002	(Lsun/awt/dnd/SunDropTargetContextPeer;I)I
/*     */       //   18: pop
/*     */       //   19: aload_0
/*     */       //   20: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   23: aload_0
/*     */       //   24: getfield 177	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:nativeCtxt	J
/*     */       //   27: invokestatic 204	sun/awt/dnd/SunDropTargetContextPeer:access$102	(Lsun/awt/dnd/SunDropTargetContextPeer;J)J
/*     */       //   30: pop2
/*     */       //   31: aload_0
/*     */       //   32: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   35: aload_0
/*     */       //   36: getfield 180	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:formats	[J
/*     */       //   39: invokestatic 205	sun/awt/dnd/SunDropTargetContextPeer:access$202	(Lsun/awt/dnd/SunDropTargetContextPeer;[J)[J
/*     */       //   42: pop
/*     */       //   43: aload_0
/*     */       //   44: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   47: aload_0
/*     */       //   48: getfield 174	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:actions	I
/*     */       //   51: invokestatic 202	sun/awt/dnd/SunDropTargetContextPeer:access$302	(Lsun/awt/dnd/SunDropTargetContextPeer;I)I
/*     */       //   54: pop
/*     */       //   55: aload_0
/*     */       //   56: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   59: aload_0
/*     */       //   60: getfield 175	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dropAction	I
/*     */       //   63: invokestatic 203	sun/awt/dnd/SunDropTargetContextPeer:access$402	(Lsun/awt/dnd/SunDropTargetContextPeer;I)I
/*     */       //   66: pop
/*     */       //   67: aload_0
/*     */       //   68: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   71: iconst_2
/*     */       //   72: putfield 172	sun/awt/dnd/SunDropTargetContextPeer:dropStatus	I
/*     */       //   75: aload_0
/*     */       //   76: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   79: iconst_0
/*     */       //   80: putfield 173	sun/awt/dnd/SunDropTargetContextPeer:dropComplete	Z
/*     */       //   83: aload_0
/*     */       //   84: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   87: aload_1
/*     */       //   88: invokevirtual 207	sun/awt/dnd/SunDropTargetContextPeer:processEnterMessage	(Lsun/awt/dnd/SunDropTargetEvent;)V
/*     */       //   91: aload_0
/*     */       //   92: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   95: iconst_0
/*     */       //   96: putfield 172	sun/awt/dnd/SunDropTargetContextPeer:dropStatus	I
/*     */       //   99: goto +14 -> 113
/*     */       //   102: astore_3
/*     */       //   103: aload_0
/*     */       //   104: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   107: iconst_0
/*     */       //   108: putfield 172	sun/awt/dnd/SunDropTargetContextPeer:dropStatus	I
/*     */       //   111: aload_3
/*     */       //   112: athrow
/*     */       //   113: aload_0
/*     */       //   114: aload_0
/*     */       //   115: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   118: invokestatic 200	sun/awt/dnd/SunDropTargetContextPeer:access$400	(Lsun/awt/dnd/SunDropTargetContextPeer;)I
/*     */       //   121: invokevirtual 212	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:setReturnValue	(I)V
/*     */       //   124: aload_2
/*     */       //   125: monitorexit
/*     */       //   126: goto +10 -> 136
/*     */       //   129: astore 4
/*     */       //   131: aload_2
/*     */       //   132: monitorexit
/*     */       //   133: aload 4
/*     */       //   135: athrow
/*     */       //   136: return
/*     */       // Line number table:
/*     */       //   Java source line #784	-> byte code offset #0
/*     */       //   Java source line #787	-> byte code offset #7
/*     */       //   Java source line #790	-> byte code offset #19
/*     */       //   Java source line #791	-> byte code offset #31
/*     */       //   Java source line #792	-> byte code offset #43
/*     */       //   Java source line #793	-> byte code offset #55
/*     */       //   Java source line #795	-> byte code offset #67
/*     */       //   Java source line #796	-> byte code offset #75
/*     */       //   Java source line #799	-> byte code offset #83
/*     */       //   Java source line #801	-> byte code offset #91
/*     */       //   Java source line #802	-> byte code offset #99
/*     */       //   Java source line #801	-> byte code offset #102
/*     */       //   Java source line #802	-> byte code offset #111
/*     */       //   Java source line #804	-> byte code offset #113
/*     */       //   Java source line #805	-> byte code offset #124
/*     */       //   Java source line #806	-> byte code offset #136
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	137	0	this	EventDispatcher
/*     */       //   0	137	1	paramSunDropTargetEvent	SunDropTargetEvent
/*     */       //   5	127	2	Ljava/lang/Object;	Object
/*     */       //   102	10	3	localObject1	Object
/*     */       //   129	5	4	localObject2	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   83	91	102	finally
/*     */       //   7	126	129	finally
/*     */       //   129	133	129	finally
/*     */     }
/*     */     
/*     */     private void dispatchMotionEvent(SunDropTargetEvent paramSunDropTargetEvent)
/*     */     {
/* 809 */       synchronized (this.peer)
/*     */       {
/* 811 */         boolean bool = this.peer.previousDA != this.dropAction;
/* 812 */         this.peer.previousDA = this.dropAction;
/*     */         
/*     */ 
/* 815 */         this.peer.nativeDragContext = this.nativeCtxt;
/* 816 */         this.peer.currentT = this.formats;
/* 817 */         this.peer.currentSA = this.actions;
/* 818 */         this.peer.currentDA = this.dropAction;
/*     */         
/* 820 */         this.peer.dropStatus = 2;
/* 821 */         this.peer.dropComplete = false;
/*     */         try
/*     */         {
/* 824 */           this.peer.processMotionMessage(paramSunDropTargetEvent, bool);
/*     */         } finally {
/* 826 */           this.peer.dropStatus = 0;
/*     */         }
/*     */         
/* 829 */         setReturnValue(this.peer.currentDA);
/*     */       }
/*     */     }
/*     */     
/*     */     private void dispatchExitEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 834 */       synchronized (this.peer)
/*     */       {
/*     */ 
/* 837 */         this.peer.nativeDragContext = this.nativeCtxt;
/*     */         
/* 839 */         this.peer.processExitMessage(paramSunDropTargetEvent);
/*     */       }
/*     */     }
/*     */     
/*     */     private void dispatchDropEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 844 */       synchronized (this.peer)
/*     */       {
/*     */ 
/* 847 */         this.peer.nativeDragContext = this.nativeCtxt;
/* 848 */         this.peer.currentT = this.formats;
/* 849 */         this.peer.currentSA = this.actions;
/* 850 */         this.peer.currentDA = this.dropAction;
/*     */         
/* 852 */         this.peer.processDropMessage(paramSunDropTargetEvent);
/*     */       }
/*     */     }
/*     */     
/*     */     void setReturnValue(int paramInt) {
/* 857 */       this.returnValue = paramInt;
/*     */     }
/*     */     
/*     */     int getReturnValue() {
/* 861 */       return this.returnValue;
/*     */     }
/*     */     
/*     */     boolean isDone() {
/* 865 */       return this.eventSet.isEmpty();
/*     */     }
/*     */     
/*     */     void registerEvent(SunDropTargetEvent paramSunDropTargetEvent) {
/* 869 */       handler.lock();
/* 870 */       if ((!this.eventSet.add(paramSunDropTargetEvent)) && (SunDropTargetContextPeer.dndLog.isLoggable(PlatformLogger.Level.FINE))) {
/* 871 */         SunDropTargetContextPeer.dndLog.fine("Event is already registered: " + paramSunDropTargetEvent);
/*     */       }
/* 873 */       handler.unlock();
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     void unregisterEvent(SunDropTargetEvent paramSunDropTargetEvent)
/*     */     {
/*     */       // Byte code:
/*     */       //   0: getstatic 182	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*     */       //   3: invokeinterface 222 1 0
/*     */       //   8: aload_0
/*     */       //   9: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:eventSet	Ljava/util/HashSet;
/*     */       //   12: aload_1
/*     */       //   13: invokevirtual 196	java/util/HashSet:remove	(Ljava/lang/Object;)Z
/*     */       //   16: ifne +12 -> 28
/*     */       //   19: getstatic 182	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*     */       //   22: invokeinterface 223 1 0
/*     */       //   27: return
/*     */       //   28: aload_0
/*     */       //   29: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:eventSet	Ljava/util/HashSet;
/*     */       //   32: invokevirtual 192	java/util/HashSet:isEmpty	()Z
/*     */       //   35: ifeq +31 -> 66
/*     */       //   38: aload_0
/*     */       //   39: getfield 179	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
/*     */       //   42: ifne +19 -> 61
/*     */       //   45: aload_0
/*     */       //   46: getfield 178	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatchType	Z
/*     */       //   49: iconst_1
/*     */       //   50: if_icmpne +11 -> 61
/*     */       //   53: getstatic 182	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*     */       //   56: invokeinterface 221 1 0
/*     */       //   61: aload_0
/*     */       //   62: iconst_1
/*     */       //   63: putfield 179	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
/*     */       //   66: getstatic 182	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*     */       //   69: invokeinterface 223 1 0
/*     */       //   74: goto +14 -> 88
/*     */       //   77: astore_2
/*     */       //   78: getstatic 182	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*     */       //   81: invokeinterface 223 1 0
/*     */       //   86: aload_2
/*     */       //   87: athrow
/*     */       //   88: aload_0
/*     */       //   89: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   92: aload_1
/*     */       //   93: aload_0
/*     */       //   94: getfield 176	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:returnValue	I
/*     */       //   97: aload_0
/*     */       //   98: getfield 179	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
/*     */       //   101: invokevirtual 209	sun/awt/dnd/SunDropTargetContextPeer:eventProcessed	(Lsun/awt/dnd/SunDropTargetEvent;IZ)V
/*     */       //   104: aload_0
/*     */       //   105: getfield 179	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
/*     */       //   108: ifeq +44 -> 152
/*     */       //   111: aload_0
/*     */       //   112: lconst_0
/*     */       //   113: putfield 177	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:nativeCtxt	J
/*     */       //   116: aload_0
/*     */       //   117: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   120: lconst_0
/*     */       //   121: invokestatic 204	sun/awt/dnd/SunDropTargetContextPeer:access$102	(Lsun/awt/dnd/SunDropTargetContextPeer;J)J
/*     */       //   124: pop2
/*     */       //   125: goto +27 -> 152
/*     */       //   128: astore_3
/*     */       //   129: aload_0
/*     */       //   130: getfield 179	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatcherDone	Z
/*     */       //   133: ifeq +17 -> 150
/*     */       //   136: aload_0
/*     */       //   137: lconst_0
/*     */       //   138: putfield 177	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:nativeCtxt	J
/*     */       //   141: aload_0
/*     */       //   142: getfield 183	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:peer	Lsun/awt/dnd/SunDropTargetContextPeer;
/*     */       //   145: lconst_0
/*     */       //   146: invokestatic 204	sun/awt/dnd/SunDropTargetContextPeer:access$102	(Lsun/awt/dnd/SunDropTargetContextPeer;J)J
/*     */       //   149: pop2
/*     */       //   150: aload_3
/*     */       //   151: athrow
/*     */       //   152: return
/*     */       // Line number table:
/*     */       //   Java source line #877	-> byte code offset #0
/*     */       //   Java source line #879	-> byte code offset #8
/*     */       //   Java source line #890	-> byte code offset #19
/*     */       //   Java source line #881	-> byte code offset #27
/*     */       //   Java source line #883	-> byte code offset #28
/*     */       //   Java source line #884	-> byte code offset #38
/*     */       //   Java source line #885	-> byte code offset #53
/*     */       //   Java source line #887	-> byte code offset #61
/*     */       //   Java source line #890	-> byte code offset #66
/*     */       //   Java source line #891	-> byte code offset #74
/*     */       //   Java source line #890	-> byte code offset #77
/*     */       //   Java source line #891	-> byte code offset #86
/*     */       //   Java source line #894	-> byte code offset #88
/*     */       //   Java source line #900	-> byte code offset #104
/*     */       //   Java source line #901	-> byte code offset #111
/*     */       //   Java source line #903	-> byte code offset #116
/*     */       //   Java source line #900	-> byte code offset #128
/*     */       //   Java source line #901	-> byte code offset #136
/*     */       //   Java source line #903	-> byte code offset #141
/*     */       //   Java source line #906	-> byte code offset #150
/*     */       //   Java source line #907	-> byte code offset #152
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	153	0	this	EventDispatcher
/*     */       //   0	153	1	paramSunDropTargetEvent	SunDropTargetEvent
/*     */       //   77	10	2	localObject1	Object
/*     */       //   128	23	3	localObject2	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   8	19	77	finally
/*     */       //   28	66	77	finally
/*     */       //   88	104	128	finally
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void unregisterAllEvents()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aconst_null
/*     */       //   1: astore_1
/*     */       //   2: getstatic 182	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*     */       //   5: invokeinterface 222 1 0
/*     */       //   10: aload_0
/*     */       //   11: getfield 181	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:eventSet	Ljava/util/HashSet;
/*     */       //   14: invokevirtual 194	java/util/HashSet:toArray	()[Ljava/lang/Object;
/*     */       //   17: astore_1
/*     */       //   18: getstatic 182	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*     */       //   21: invokeinterface 223 1 0
/*     */       //   26: goto +14 -> 40
/*     */       //   29: astore_2
/*     */       //   30: getstatic 182	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:handler	Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*     */       //   33: invokeinterface 223 1 0
/*     */       //   38: aload_2
/*     */       //   39: athrow
/*     */       //   40: aload_1
/*     */       //   41: ifnull +27 -> 68
/*     */       //   44: iconst_0
/*     */       //   45: istore_2
/*     */       //   46: iload_2
/*     */       //   47: aload_1
/*     */       //   48: arraylength
/*     */       //   49: if_icmpge +19 -> 68
/*     */       //   52: aload_0
/*     */       //   53: aload_1
/*     */       //   54: iload_2
/*     */       //   55: aaload
/*     */       //   56: checkcast 100	sun/awt/dnd/SunDropTargetEvent
/*     */       //   59: invokevirtual 217	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:unregisterEvent	(Lsun/awt/dnd/SunDropTargetEvent;)V
/*     */       //   62: iinc 2 1
/*     */       //   65: goto -19 -> 46
/*     */       //   68: return
/*     */       // Line number table:
/*     */       //   Java source line #910	-> byte code offset #0
/*     */       //   Java source line #911	-> byte code offset #2
/*     */       //   Java source line #913	-> byte code offset #10
/*     */       //   Java source line #915	-> byte code offset #18
/*     */       //   Java source line #916	-> byte code offset #26
/*     */       //   Java source line #915	-> byte code offset #29
/*     */       //   Java source line #916	-> byte code offset #38
/*     */       //   Java source line #918	-> byte code offset #40
/*     */       //   Java source line #919	-> byte code offset #44
/*     */       //   Java source line #920	-> byte code offset #52
/*     */       //   Java source line #919	-> byte code offset #62
/*     */       //   Java source line #923	-> byte code offset #68
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	69	0	this	EventDispatcher
/*     */       //   1	53	1	arrayOfObject	Object[]
/*     */       //   29	10	2	localObject	Object
/*     */       //   45	18	2	i	int
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   10	18	29	finally
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\dnd\SunDropTargetContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
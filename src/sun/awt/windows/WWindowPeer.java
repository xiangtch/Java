/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.AWTEventMulticaster;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.KeyboardFocusManager;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.Window;
/*     */ import java.awt.Window.Type;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.peer.WindowPeer;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ComponentAccessor;
/*     */ import sun.awt.AWTAccessor.WindowAccessor;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.CausedFocusEvent.Cause;
/*     */ import sun.awt.DisplayChangedListener;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.Win32GraphicsConfig;
/*     */ import sun.awt.Win32GraphicsDevice;
/*     */ import sun.awt.Win32GraphicsEnvironment;
/*     */ import sun.java2d.pipe.Region;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ import sun.util.logging.PlatformLogger.Level;
/*     */ 
/*     */ public class WWindowPeer extends WPanelPeer implements WindowPeer, DisplayChangedListener
/*     */ {
/*  46 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WWindowPeer");
/*  47 */   private static final PlatformLogger screenLog = PlatformLogger.getLogger("sun.awt.windows.screen.WWindowPeer");
/*     */   
/*     */ 
/*     */ 
/*  51 */   private WWindowPeer modalBlocker = null;
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isOpaque;
/*     */   
/*     */ 
/*     */ 
/*     */   private TranslucentWindowPainter painter;
/*     */   
/*     */ 
/*  62 */   private static final StringBuffer ACTIVE_WINDOWS_KEY = new StringBuffer("active_windows_list");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */   private static PropertyChangeListener activeWindowListener = new ActiveWindowListener(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  75 */   private static final PropertyChangeListener guiDisposedListener = new GuiDisposedListener(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private WindowListener windowListener;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  89 */     initIDs();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void disposeImpl()
/*     */   {
/*  96 */     AppContext localAppContext = SunToolkit.targetToAppContext(this.target);
/*  97 */     synchronized (localAppContext) {
/*  98 */       List localList = (List)localAppContext.get(ACTIVE_WINDOWS_KEY);
/*  99 */       if (localList != null) {
/* 100 */         localList.remove(this);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 105 */     ??? = getGraphicsConfiguration();
/* 106 */     ((Win32GraphicsDevice)((GraphicsConfiguration)???).getDevice()).removeDisplayChangedListener(this);
/*     */     
/* 108 */     synchronized (getStateLock()) {
/* 109 */       TranslucentWindowPainter localTranslucentWindowPainter = this.painter;
/* 110 */       if (localTranslucentWindowPainter != null) {
/* 111 */         localTranslucentWindowPainter.flush();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 117 */     super.disposeImpl();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void toFront()
/*     */   {
/* 124 */     updateFocusableWindowState();
/* 125 */     _toFront();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAlwaysOnTop(boolean paramBoolean)
/*     */   {
/* 135 */     if (((paramBoolean) && (((Window)this.target).isVisible())) || (!paramBoolean)) {
/* 136 */       setAlwaysOnTopNative(paramBoolean);
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateAlwaysOnTopState()
/*     */   {
/* 142 */     setAlwaysOnTop(((Window)this.target).isAlwaysOnTop());
/*     */   }
/*     */   
/*     */   public void updateFocusableWindowState()
/*     */   {
/* 147 */     setFocusableWindow(((Window)this.target).isFocusableWindow());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTitle(String paramString)
/*     */   {
/* 155 */     if (paramString == null) {
/* 156 */       paramString = "";
/*     */     }
/* 158 */     _setTitle(paramString);
/*     */   }
/*     */   
/*     */   public void setResizable(boolean paramBoolean)
/*     */   {
/* 163 */     _setResizable(paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   WWindowPeer(Window paramWindow)
/*     */   {
/* 171 */     super(paramWindow);
/*     */   }
/*     */   
/*     */   void initialize()
/*     */   {
/* 176 */     super.initialize();
/*     */     
/* 178 */     updateInsets(this.insets_);
/*     */     
/* 180 */     Font localFont = ((Window)this.target).getFont();
/* 181 */     if (localFont == null) {
/* 182 */       localFont = defaultFont;
/* 183 */       ((Window)this.target).setFont(localFont);
/* 184 */       setFont(localFont);
/*     */     }
/*     */     
/* 187 */     GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration();
/* 188 */     ((Win32GraphicsDevice)localGraphicsConfiguration.getDevice()).addDisplayChangedListener(this);
/*     */     
/* 190 */     initActiveWindowsTracking((Window)this.target);
/*     */     
/* 192 */     updateIconImages();
/*     */     
/* 194 */     Shape localShape = ((Window)this.target).getShape();
/* 195 */     if (localShape != null) {
/* 196 */       applyShape(Region.getInstance(localShape, null));
/*     */     }
/*     */     
/* 199 */     float f = ((Window)this.target).getOpacity();
/* 200 */     if (f < 1.0F) {
/* 201 */       setOpacity(f);
/*     */     }
/*     */     
/* 204 */     synchronized (getStateLock())
/*     */     {
/*     */ 
/* 207 */       this.isOpaque = true;
/* 208 */       setOpaque(((Window)this.target).isOpaque());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 214 */   private volatile Type windowType = Type.NORMAL;
/*     */   
/*     */ 
/*     */   void preCreate(WComponentPeer paramWComponentPeer)
/*     */   {
/* 219 */     this.windowType = ((Window)this.target).getType();
/*     */   }
/*     */   
/*     */   void create(WComponentPeer paramWComponentPeer)
/*     */   {
/* 224 */     preCreate(paramWComponentPeer);
/* 225 */     createAwtWindow(paramWComponentPeer);
/*     */   }
/*     */   
/*     */   final WComponentPeer getNativeParent()
/*     */   {
/* 230 */     Window localWindow = ((Window)this.target).getOwner();
/* 231 */     return (WComponentPeer)WToolkit.targetToPeer(localWindow);
/*     */   }
/*     */   
/*     */   protected void realShow()
/*     */   {
/* 236 */     super.show();
/*     */   }
/*     */   
/*     */   public void show()
/*     */   {
/* 241 */     updateFocusableWindowState();
/*     */     
/* 243 */     boolean bool = ((Window)this.target).isAlwaysOnTop();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 252 */     updateGC();
/*     */     
/* 254 */     realShow();
/* 255 */     updateMinimumSize();
/*     */     
/* 257 */     if ((((Window)this.target).isAlwaysOnTopSupported()) && (bool)) {
/* 258 */       setAlwaysOnTop(bool);
/*     */     }
/*     */     
/* 261 */     synchronized (getStateLock()) {
/* 262 */       if (!this.isOpaque) {
/* 263 */         updateWindow(true);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 268 */     ??? = getNativeParent();
/* 269 */     if ((??? != null) && (((WComponentPeer)???).isLightweightFramePeer())) {
/* 270 */       Rectangle localRectangle = getBounds();
/* 271 */       handleExpose(0, 0, localRectangle.width, localRectangle.height);
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
/*     */   public boolean requestWindowFocus(CausedFocusEvent.Cause paramCause)
/*     */   {
/* 298 */     if (!focusAllowedFor()) {
/* 299 */       return false;
/*     */     }
/* 301 */     return requestWindowFocus(paramCause == CausedFocusEvent.Cause.MOUSE_EVENT);
/*     */   }
/*     */   
/*     */   public boolean focusAllowedFor()
/*     */   {
/* 306 */     Window localWindow = (Window)this.target;
/* 307 */     if ((!localWindow.isVisible()) || 
/* 308 */       (!localWindow.isEnabled()) || 
/* 309 */       (!localWindow.isFocusableWindow()))
/*     */     {
/* 311 */       return false;
/*     */     }
/* 313 */     if (isModalBlocked()) {
/* 314 */       return false;
/*     */     }
/* 316 */     return true;
/*     */   }
/*     */   
/*     */   void hide()
/*     */   {
/* 321 */     WindowListener localWindowListener = this.windowListener;
/* 322 */     if (localWindowListener != null)
/*     */     {
/*     */ 
/* 325 */       localWindowListener.windowClosing(new WindowEvent((Window)this.target, 201));
/*     */     }
/* 327 */     super.hide();
/*     */   }
/*     */   
/*     */ 
/*     */   void preprocessPostEvent(AWTEvent paramAWTEvent)
/*     */   {
/* 333 */     if ((paramAWTEvent instanceof WindowEvent)) {
/* 334 */       WindowListener localWindowListener = this.windowListener;
/* 335 */       if (localWindowListener != null) {
/* 336 */         switch (paramAWTEvent.getID()) {
/*     */         case 201: 
/* 338 */           localWindowListener.windowClosing((WindowEvent)paramAWTEvent);
/* 339 */           break;
/*     */         case 203: 
/* 341 */           localWindowListener.windowIconified((WindowEvent)paramAWTEvent);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   synchronized void addWindowListener(WindowListener paramWindowListener)
/*     */   {
/* 349 */     this.windowListener = AWTEventMulticaster.add(this.windowListener, paramWindowListener);
/*     */   }
/*     */   
/*     */   synchronized void removeWindowListener(WindowListener paramWindowListener) {
/* 353 */     this.windowListener = AWTEventMulticaster.remove(this.windowListener, paramWindowListener);
/*     */   }
/*     */   
/*     */   public void updateMinimumSize()
/*     */   {
/* 358 */     Dimension localDimension = null;
/* 359 */     if (((Component)this.target).isMinimumSizeSet()) {
/* 360 */       localDimension = ((Component)this.target).getMinimumSize();
/*     */     }
/* 362 */     if (localDimension != null) {
/* 363 */       int i = getSysMinWidth();
/* 364 */       int j = getSysMinHeight();
/* 365 */       int k = localDimension.width >= i ? localDimension.width : i;
/* 366 */       int m = localDimension.height >= j ? localDimension.height : j;
/* 367 */       setMinSize(k, m);
/*     */     } else {
/* 369 */       setMinSize(0, 0);
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateIconImages()
/*     */   {
/* 375 */     List localList = ((Window)this.target).getIconImages();
/* 376 */     if ((localList == null) || (localList.size() == 0)) {
/* 377 */       setIconImagesData(null, 0, 0, null, 0, 0);
/*     */     } else {
/* 379 */       int i = getSysIconWidth();
/* 380 */       int j = getSysIconHeight();
/* 381 */       int k = getSysSmIconWidth();
/* 382 */       int m = getSysSmIconHeight();
/* 383 */       DataBufferInt localDataBufferInt1 = SunToolkit.getScaledIconData(localList, i, j);
/*     */       
/* 385 */       DataBufferInt localDataBufferInt2 = SunToolkit.getScaledIconData(localList, k, m);
/*     */       
/* 387 */       if ((localDataBufferInt1 != null) && (localDataBufferInt2 != null)) {
/* 388 */         setIconImagesData(localDataBufferInt1.getData(), i, j, localDataBufferInt2
/* 389 */           .getData(), k, m);
/*     */       } else {
/* 391 */         setIconImagesData(null, 0, 0, null, 0, 0);
/*     */       }
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
/*     */ 
/*     */   public boolean isModalBlocked()
/*     */   {
/* 408 */     return this.modalBlocker != null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setModalBlocked(Dialog paramDialog, boolean paramBoolean)
/*     */   {
/* 414 */     synchronized (((Component)getTarget()).getTreeLock())
/*     */     {
/*     */ 
/* 417 */       WWindowPeer localWWindowPeer = (WWindowPeer)paramDialog.getPeer();
/* 418 */       if (paramBoolean)
/*     */       {
/* 420 */         this.modalBlocker = localWWindowPeer;
/*     */         
/*     */ 
/*     */ 
/* 424 */         if ((localWWindowPeer instanceof WFileDialogPeer)) {
/* 425 */           ((WFileDialogPeer)localWWindowPeer).blockWindow(this);
/* 426 */         } else if ((localWWindowPeer instanceof WPrintDialogPeer)) {
/* 427 */           ((WPrintDialogPeer)localWWindowPeer).blockWindow(this);
/*     */         } else {
/* 429 */           modalDisable(paramDialog, localWWindowPeer.getHWnd());
/*     */         }
/*     */       } else {
/* 432 */         this.modalBlocker = null;
/* 433 */         if ((localWWindowPeer instanceof WFileDialogPeer)) {
/* 434 */           ((WFileDialogPeer)localWWindowPeer).unblockWindow(this);
/* 435 */         } else if ((localWWindowPeer instanceof WPrintDialogPeer)) {
/* 436 */           ((WPrintDialogPeer)localWWindowPeer).unblockWindow(this);
/*     */         } else {
/* 438 */           modalEnable(paramDialog);
/*     */         }
/*     */       }
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
/*     */   public static long[] getActiveWindowHandles(Component paramComponent)
/*     */   {
/* 454 */     AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
/* 455 */     if (localAppContext == null) return null;
/* 456 */     synchronized (localAppContext) {
/* 457 */       List localList = (List)localAppContext.get(ACTIVE_WINDOWS_KEY);
/* 458 */       if (localList == null) {
/* 459 */         return null;
/*     */       }
/* 461 */       long[] arrayOfLong = new long[localList.size()];
/* 462 */       for (int i = 0; i < localList.size(); i++) {
/* 463 */         arrayOfLong[i] = ((WWindowPeer)localList.get(i)).getHWnd();
/*     */       }
/* 465 */       return arrayOfLong;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void draggedToNewScreen()
/*     */   {
/* 477 */     SunToolkit.executeOnEventHandlerThread((Component)this.target, new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 481 */         WWindowPeer.this.displayChanged();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void updateGC() {
/* 487 */     int i = getScreenImOn();
/* 488 */     if (screenLog.isLoggable(PlatformLogger.Level.FINER)) {
/* 489 */       log.finer("Screen number: " + i);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 494 */     Win32GraphicsDevice localWin32GraphicsDevice1 = (Win32GraphicsDevice)this.winGraphicsConfig.getDevice();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 499 */     GraphicsDevice[] arrayOfGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
/*     */     
/*     */     Win32GraphicsDevice localWin32GraphicsDevice2;
/* 502 */     if (i >= arrayOfGraphicsDevice.length)
/*     */     {
/* 504 */       localWin32GraphicsDevice2 = (Win32GraphicsDevice)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
/*     */     } else {
/* 506 */       localWin32GraphicsDevice2 = (Win32GraphicsDevice)arrayOfGraphicsDevice[i];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 512 */     this.winGraphicsConfig = ((Win32GraphicsConfig)localWin32GraphicsDevice2.getDefaultConfiguration());
/* 513 */     if ((screenLog.isLoggable(PlatformLogger.Level.FINE)) && 
/* 514 */       (this.winGraphicsConfig == null)) {
/* 515 */       screenLog.fine("Assertion (winGraphicsConfig != null) failed");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 520 */     if (localWin32GraphicsDevice1 != localWin32GraphicsDevice2) {
/* 521 */       localWin32GraphicsDevice1.removeDisplayChangedListener(this);
/* 522 */       localWin32GraphicsDevice2.addDisplayChangedListener(this);
/*     */     }
/*     */     
/*     */ 
/* 526 */     AWTAccessor.getComponentAccessor().setGraphicsConfiguration((Component)this.target, this.winGraphicsConfig);
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
/*     */ 
/*     */ 
/*     */   public void displayChanged()
/*     */   {
/* 542 */     updateGC();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void grab()
/*     */   {
/* 563 */     nativeGrab();
/*     */   }
/*     */   
/*     */   public void ungrab() {
/* 567 */     nativeUngrab();
/*     */   }
/*     */   
/*     */ 
/*     */   private final boolean hasWarningWindow()
/*     */   {
/* 573 */     return ((Window)this.target).getWarningString() != null;
/*     */   }
/*     */   
/*     */   boolean isTargetUndecorated() {
/* 577 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 583 */   private volatile int sysX = 0;
/* 584 */   private volatile int sysY = 0;
/* 585 */   private volatile int sysW = 0;
/* 586 */   private volatile int sysH = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 593 */     this.sysX = paramInt1;
/* 594 */     this.sysY = paramInt2;
/* 595 */     this.sysW = paramInt3;
/* 596 */     this.sysH = paramInt4;
/*     */     
/* 598 */     super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void print(Graphics paramGraphics)
/*     */   {
/* 605 */     Shape localShape = AWTAccessor.getWindowAccessor().getShape((Window)this.target);
/* 606 */     if (localShape != null) {
/* 607 */       paramGraphics.setClip(localShape);
/*     */     }
/* 609 */     super.print(paramGraphics);
/*     */   }
/*     */   
/*     */   private void replaceSurfaceDataRecursively(Component paramComponent)
/*     */   {
/* 614 */     if ((paramComponent instanceof Container)) {
/* 615 */       for (Component localComponent : ((Container)paramComponent).getComponents()) {
/* 616 */         replaceSurfaceDataRecursively(localComponent);
/*     */       }
/*     */     }
/* 619 */     ??? = paramComponent.getPeer();
/* 620 */     if ((??? instanceof WComponentPeer)) {
/* 621 */       ((WComponentPeer)???).replaceSurfaceDataLater();
/*     */     }
/*     */   }
/*     */   
/*     */   public final Graphics getTranslucentGraphics() {
/* 626 */     synchronized (getStateLock()) {
/* 627 */       return this.isOpaque ? null : this.painter.getBackBuffer(false).getGraphics();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBackground(Color paramColor)
/*     */   {
/* 633 */     super.setBackground(paramColor);
/* 634 */     synchronized (getStateLock()) {
/* 635 */       if ((!this.isOpaque) && (((Window)this.target).isVisible())) {
/* 636 */         updateWindow(true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 642 */   private float opacity = 1.0F;
/*     */   
/*     */ 
/*     */   public void setOpacity(float paramFloat)
/*     */   {
/* 647 */     if (!((SunToolkit)((Window)this.target).getToolkit()).isWindowOpacitySupported())
/*     */     {
/* 649 */       return;
/*     */     }
/*     */     
/* 652 */     if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
/* 653 */       throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f].");
/*     */     }
/*     */     
/*     */ 
/* 657 */     if (((this.opacity == 1.0F) && (paramFloat < 1.0F)) || ((this.opacity < 1.0F) && (paramFloat == 1.0F)))
/*     */     {
/* 659 */       if (!Win32GraphicsEnvironment.isVistaOS())
/*     */       {
/*     */ 
/*     */ 
/* 663 */         replaceSurfaceDataRecursively((Component)getTarget());
/*     */       }
/*     */     }
/* 666 */     this.opacity = paramFloat;
/*     */     
/*     */ 
/* 669 */     int i = (int)(paramFloat * 255.0F);
/* 670 */     if (i < 0) {
/* 671 */       i = 0;
/*     */     }
/* 673 */     if (i > 255) {
/* 674 */       i = 255;
/*     */     }
/*     */     
/* 677 */     setOpacity(i);
/*     */     
/* 679 */     synchronized (getStateLock()) {
/* 680 */       if ((!this.isOpaque) && (((Window)this.target).isVisible())) {
/* 681 */         updateWindow(true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setOpaque(boolean paramBoolean)
/*     */   {
/* 690 */     synchronized (getStateLock()) {
/* 691 */       if (this.isOpaque == paramBoolean) {
/* 692 */         return;
/*     */       }
/*     */     }
/*     */     
/* 696 */     ??? = (Window)getTarget();
/*     */     
/* 698 */     if (!paramBoolean) {
/* 699 */       SunToolkit localSunToolkit = (SunToolkit)((Window)???).getToolkit();
/* 700 */       if ((!localSunToolkit.isWindowTranslucencySupported()) || 
/* 701 */         (!localSunToolkit.isTranslucencyCapable(((Window)???).getGraphicsConfiguration())))
/*     */       {
/* 703 */         return;
/*     */       }
/*     */     }
/*     */     
/* 707 */     boolean bool = Win32GraphicsEnvironment.isVistaOS();
/*     */     
/* 709 */     if ((this.isOpaque != paramBoolean) && (!bool))
/*     */     {
/*     */ 
/* 712 */       replaceSurfaceDataRecursively((Component)???);
/*     */     }
/*     */     
/* 715 */     synchronized (getStateLock()) {
/* 716 */       this.isOpaque = paramBoolean;
/* 717 */       setOpaqueImpl(paramBoolean);
/* 718 */       if (paramBoolean) {
/* 719 */         TranslucentWindowPainter localTranslucentWindowPainter = this.painter;
/* 720 */         if (localTranslucentWindowPainter != null) {
/* 721 */           localTranslucentWindowPainter.flush();
/* 722 */           this.painter = null;
/*     */         }
/*     */       } else {
/* 725 */         this.painter = TranslucentWindowPainter.createInstance(this);
/*     */       }
/*     */     }
/*     */     
/* 729 */     if (bool)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 735 */       ??? = ((Window)???).getShape();
/* 736 */       if (??? != null) {
/* 737 */         ((Window)???).setShape((Shape)???);
/*     */       }
/*     */     }
/*     */     
/* 741 */     if (((Window)???).isVisible()) {
/* 742 */       updateWindow(true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void updateWindow()
/*     */   {
/* 750 */     updateWindow(false);
/*     */   }
/*     */   
/*     */   private void updateWindow(boolean paramBoolean) {
/* 754 */     Window localWindow = (Window)this.target;
/* 755 */     synchronized (getStateLock()) {
/* 756 */       if ((this.isOpaque) || (!localWindow.isVisible()) || 
/* 757 */         (localWindow.getWidth() <= 0) || (localWindow.getHeight() <= 0))
/*     */       {
/* 759 */         return;
/*     */       }
/* 761 */       TranslucentWindowPainter localTranslucentWindowPainter = this.painter;
/* 762 */       if (localTranslucentWindowPainter != null) {
/* 763 */         localTranslucentWindowPainter.updateWindow(paramBoolean);
/* 764 */       } else if (log.isLoggable(PlatformLogger.Level.FINER)) {
/* 765 */         log.finer("Translucent window painter is null in updateWindow");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void initActiveWindowsTracking(Window paramWindow)
/*     */   {
/* 777 */     AppContext localAppContext = AppContext.getAppContext();
/* 778 */     synchronized (localAppContext) {
/* 779 */       Object localObject1 = (List)localAppContext.get(ACTIVE_WINDOWS_KEY);
/* 780 */       if (localObject1 == null) {
/* 781 */         localObject1 = new LinkedList();
/* 782 */         localAppContext.put(ACTIVE_WINDOWS_KEY, localObject1);
/* 783 */         localAppContext.addPropertyChangeListener("guidisposed", guiDisposedListener);
/*     */         
/* 785 */         KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
/* 786 */         localKeyboardFocusManager.addPropertyChangeListener("activeWindow", activeWindowListener);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static native void initIDs();
/*     */   
/*     */   private native void _toFront();
/*     */   
/*     */   public native void toBack();
/*     */   
/*     */   private static class GuiDisposedListener implements PropertyChangeListener {
/*     */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 799 */       boolean bool = ((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue();
/* 800 */       if ((bool != true) && 
/* 801 */         (WWindowPeer.log.isLoggable(PlatformLogger.Level.FINE))) {
/* 802 */         WWindowPeer.log.fine(" Assertion (newValue != true) failed for AppContext.GUI_DISPOSED ");
/*     */       }
/*     */       
/* 805 */       AppContext localAppContext = AppContext.getAppContext();
/* 806 */       synchronized (localAppContext) {
/* 807 */         localAppContext.remove(WWindowPeer.ACTIVE_WINDOWS_KEY);
/* 808 */         localAppContext.removePropertyChangeListener("guidisposed", this);
/*     */         
/* 810 */         KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
/* 811 */         localKeyboardFocusManager.removePropertyChangeListener("activeWindow", WWindowPeer.activeWindowListener);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private native void setAlwaysOnTopNative(boolean paramBoolean);
/*     */   
/*     */   native void setFocusableWindow(boolean paramBoolean);
/*     */   
/*     */   private native void _setTitle(String paramString);
/*     */   
/*     */   private native void _setResizable(boolean paramBoolean);
/*     */   
/*     */   private static class ActiveWindowListener implements PropertyChangeListener {
/* 825 */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) { Window localWindow = (Window)paramPropertyChangeEvent.getNewValue();
/* 826 */       if (localWindow == null) {
/* 827 */         return;
/*     */       }
/* 829 */       AppContext localAppContext = SunToolkit.targetToAppContext(localWindow);
/* 830 */       synchronized (localAppContext) {
/* 831 */         WWindowPeer localWWindowPeer = (WWindowPeer)localWindow.getPeer();
/*     */         
/* 833 */         List localList = (List)localAppContext.get(WWindowPeer.ACTIVE_WINDOWS_KEY);
/* 834 */         if (localList != null) {
/* 835 */           localList.remove(localWWindowPeer);
/* 836 */           localList.add(localWWindowPeer);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   native void createAwtWindow(WComponentPeer paramWComponentPeer);
/*     */   
/*     */   native void updateInsets(Insets paramInsets);
/*     */   
/*     */   static native int getSysMinWidth();
/*     */   
/*     */   static native int getSysMinHeight();
/*     */   
/*     */   static native int getSysIconWidth();
/*     */   
/*     */   static native int getSysIconHeight();
/*     */   
/*     */   static native int getSysSmIconWidth();
/*     */   
/*     */   static native int getSysSmIconHeight();
/*     */   
/*     */   native void setIconImagesData(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3, int paramInt4);
/*     */   
/*     */   synchronized native void reshapeFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */   private native boolean requestWindowFocus(boolean paramBoolean);
/*     */   
/*     */   native void setMinSize(int paramInt1, int paramInt2);
/*     */   
/*     */   native void modalDisable(Dialog paramDialog, long paramLong);
/*     */   
/*     */   native void modalEnable(Dialog paramDialog);
/*     */   
/*     */   public void paletteChanged() {}
/*     */   
/*     */   private native int getScreenImOn();
/*     */   
/*     */   public final native void setFullScreenExclusiveModeState(boolean paramBoolean);
/*     */   
/*     */   private native void nativeGrab();
/*     */   
/*     */   private native void nativeUngrab();
/*     */   
/*     */   public native void repositionSecurityWarning();
/*     */   
/*     */   private native void setOpacity(int paramInt);
/*     */   
/*     */   private native void setOpaqueImpl(boolean paramBoolean);
/*     */   
/*     */   native void updateWindowImpl(int[] paramArrayOfInt, int paramInt1, int paramInt2);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WWindowPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
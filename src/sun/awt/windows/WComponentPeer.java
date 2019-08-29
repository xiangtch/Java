/*      */ package sun.awt.windows;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.AWTException;
/*      */ import java.awt.BufferCapabilities;
/*      */ import java.awt.BufferCapabilities.FlipContents;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.Image;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.dnd.DropTarget;
/*      */ import java.awt.dnd.peer.DropTargetPeer;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.InputEvent;
/*      */ import java.awt.event.InvocationEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.event.PaintEvent;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.ImageProducer;
/*      */ import java.awt.image.VolatileImage;
/*      */ import java.awt.peer.ComponentPeer;
/*      */ import java.awt.peer.ContainerPeer;
/*      */ import sun.awt.AWTAccessor;
/*      */ import sun.awt.AWTAccessor.ComponentAccessor;
/*      */ import sun.awt.CausedFocusEvent.Cause;
/*      */ import sun.awt.GlobalCursorManager;
/*      */ import sun.awt.PaintEventDispatcher;
/*      */ import sun.awt.RepaintArea;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.awt.Win32GraphicsConfig;
/*      */ import sun.awt.Win32GraphicsEnvironment;
/*      */ import sun.awt.event.IgnorePaintEvent;
/*      */ import sun.awt.image.SunVolatileImage;
/*      */ import sun.awt.image.ToolkitImage;
/*      */ import sun.java2d.InvalidPipeException;
/*      */ import sun.java2d.ScreenUpdateManager;
/*      */ import sun.java2d.SurfaceData;
/*      */ import sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData;
/*      */ import sun.java2d.opengl.OGLSurfaceData;
/*      */ import sun.java2d.pipe.Region;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ import sun.util.logging.PlatformLogger.Level;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class WComponentPeer
/*      */   extends WObjectPeer
/*      */   implements ComponentPeer, DropTargetPeer
/*      */ {
/*      */   protected volatile long hwnd;
/*   71 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WComponentPeer");
/*   72 */   private static final PlatformLogger shapeLog = PlatformLogger.getLogger("sun.awt.windows.shape.WComponentPeer");
/*   73 */   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.windows.focus.WComponentPeer");
/*      */   
/*      */ 
/*      */   SurfaceData surfaceData;
/*      */   
/*      */   private RepaintArea paintArea;
/*      */   
/*      */   protected Win32GraphicsConfig winGraphicsConfig;
/*      */   
/*   82 */   boolean isLayouting = false;
/*   83 */   boolean paintPending = false;
/*   84 */   int oldWidth = -1;
/*   85 */   int oldHeight = -1;
/*   86 */   private int numBackBuffers = 0;
/*   87 */   private VolatileImage backBuffer = null;
/*   88 */   private BufferCapabilities backBufferCaps = null;
/*      */   private Color foreground;
/*      */   private Color background;
/*      */   private Font font;
/*      */   int nDropTargets;
/*      */   long nativeDropTargetContext;
/*      */   
/*      */   public native boolean isObscured();
/*      */   
/*      */   public boolean canDetermineObscurity()
/*      */   {
/*   99 */     return true;
/*      */   }
/*      */   
/*      */   private synchronized native void pShow();
/*      */   
/*      */   synchronized native void hide();
/*      */   
/*      */   synchronized native void enable();
/*      */   
/*      */   synchronized native void disable();
/*      */   
/*      */   public long getHWnd()
/*      */   {
/*  112 */     return this.hwnd;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public native Point getLocationOnScreen();
/*      */   
/*      */ 
/*      */   public void setVisible(boolean paramBoolean)
/*      */   {
/*  122 */     if (paramBoolean) {
/*  123 */       show();
/*      */     } else {
/*  125 */       hide();
/*      */     }
/*      */   }
/*      */   
/*      */   public void show() {
/*  130 */     Dimension localDimension = ((Component)this.target).getSize();
/*  131 */     this.oldHeight = localDimension.height;
/*  132 */     this.oldWidth = localDimension.width;
/*  133 */     pShow();
/*      */   }
/*      */   
/*      */ 
/*      */   public void setEnabled(boolean paramBoolean)
/*      */   {
/*  139 */     if (paramBoolean) {
/*  140 */       enable();
/*      */     } else {
/*  142 */       disable();
/*      */     }
/*      */   }
/*      */   
/*  146 */   public int serialNum = 0;
/*      */   
/*      */   private static final double BANDING_DIVISOR = 4.0D;
/*      */   
/*      */ 
/*      */   private native void reshapeNoCheck(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*      */   
/*      */ 
/*      */   public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  156 */     this.paintPending = ((paramInt3 != this.oldWidth) || (paramInt4 != this.oldHeight));
/*      */     
/*  158 */     if ((paramInt5 & 0x4000) != 0) {
/*  159 */       reshapeNoCheck(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     } else {
/*  161 */       reshape(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*  163 */     if ((paramInt3 != this.oldWidth) || (paramInt4 != this.oldHeight))
/*      */     {
/*      */       try
/*      */       {
/*  167 */         replaceSurfaceData();
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException) {}
/*      */       
/*  171 */       this.oldWidth = paramInt3;
/*  172 */       this.oldHeight = paramInt4;
/*      */     }
/*      */     
/*  175 */     this.serialNum += 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void dynamicallyLayoutContainer()
/*      */   {
/*  185 */     if (log.isLoggable(PlatformLogger.Level.FINE)) {
/*  186 */       localContainer = WToolkit.getNativeContainer((Component)this.target);
/*  187 */       if (localContainer != null) {
/*  188 */         log.fine("Assertion (parent == null) failed");
/*      */       }
/*      */     }
/*  191 */     final Container localContainer = (Container)this.target;
/*      */     
/*  193 */     WToolkit.executeOnEventHandlerThread(localContainer, new Runnable()
/*      */     {
/*      */       public void run()
/*      */       {
/*  197 */         localContainer.invalidate();
/*  198 */         localContainer.validate();
/*      */         
/*  200 */         if (((WComponentPeer.this.surfaceData instanceof D3DSurfaceData.D3DWindowSurfaceData)) || ((WComponentPeer.this.surfaceData instanceof OGLSurfaceData)))
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/*  208 */             WComponentPeer.this.replaceSurfaceData();
/*      */           }
/*      */           catch (InvalidPipeException localInvalidPipeException) {}
/*      */         }
/*      */       }
/*      */     });
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
/*      */   void paintDamagedAreaImmediately()
/*      */   {
/*  228 */     updateWindow();
/*      */     
/*      */ 
/*  231 */     SunToolkit.flushPendingEvents();
/*      */     
/*  233 */     this.paintArea.paint(this.target, shouldClearRectBeforePaint());
/*      */   }
/*      */   
/*      */   synchronized native void updateWindow();
/*      */   
/*      */   public void paint(Graphics paramGraphics)
/*      */   {
/*  240 */     ((Component)this.target).paint(paramGraphics);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
/*      */   
/*      */ 
/*      */   private native int[] createPrintedPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
/*      */   
/*      */ 
/*      */   public void print(Graphics paramGraphics)
/*      */   {
/*  253 */     Component localComponent = (Component)this.target;
/*      */     
/*      */ 
/*      */ 
/*  257 */     int i = localComponent.getWidth();
/*  258 */     int j = localComponent.getHeight();
/*      */     
/*  260 */     int k = (int)(j / 4.0D);
/*  261 */     if (k == 0) {
/*  262 */       k = j;
/*      */     }
/*      */     
/*  265 */     for (int m = 0; m < j; m += k) {
/*  266 */       int n = m + k - 1;
/*  267 */       if (n >= j) {
/*  268 */         n = j - 1;
/*      */       }
/*  270 */       int i1 = n - m + 1;
/*      */       
/*  272 */       Color localColor = localComponent.getBackground();
/*  273 */       int[] arrayOfInt = createPrintedPixels(0, m, i, i1, localColor == null ? 255 : localColor
/*  274 */         .getAlpha());
/*  275 */       if (arrayOfInt != null) {
/*  276 */         BufferedImage localBufferedImage = new BufferedImage(i, i1, 2);
/*      */         
/*  278 */         localBufferedImage.setRGB(0, 0, i, i1, arrayOfInt, 0, i);
/*  279 */         paramGraphics.drawImage(localBufferedImage, 0, m, null);
/*  280 */         localBufferedImage.flush();
/*      */       }
/*      */     }
/*      */     
/*  284 */     localComponent.print(paramGraphics);
/*      */   }
/*      */   
/*      */   public void coalescePaintEvent(PaintEvent paramPaintEvent)
/*      */   {
/*  289 */     Rectangle localRectangle = paramPaintEvent.getUpdateRect();
/*  290 */     if (!(paramPaintEvent instanceof IgnorePaintEvent)) {
/*  291 */       this.paintArea.add(localRectangle, paramPaintEvent.getID());
/*      */     }
/*      */     
/*  294 */     if (log.isLoggable(PlatformLogger.Level.FINEST)) {
/*  295 */       switch (paramPaintEvent.getID()) {
/*      */       case 801: 
/*  297 */         log.finest("coalescePaintEvent: UPDATE: add: x = " + localRectangle.x + ", y = " + localRectangle.y + ", width = " + localRectangle.width + ", height = " + localRectangle.height);
/*      */         
/*  299 */         return;
/*      */       case 800: 
/*  301 */         log.finest("coalescePaintEvent: PAINT: add: x = " + localRectangle.x + ", y = " + localRectangle.y + ", width = " + localRectangle.width + ", height = " + localRectangle.height);
/*      */         
/*  303 */         return;
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public synchronized native void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*      */   
/*      */ 
/*  313 */   public boolean handleJavaKeyEvent(KeyEvent paramKeyEvent) { return false; }
/*      */   
/*      */   public void handleJavaMouseEvent(MouseEvent paramMouseEvent) {
/*  316 */     switch (paramMouseEvent.getID())
/*      */     {
/*      */     case 501: 
/*  319 */       if ((this.target == paramMouseEvent.getSource()) && 
/*  320 */         (!((Component)this.target).isFocusOwner()) && 
/*  321 */         (WKeyboardFocusManagerPeer.shouldFocusOnClick((Component)this.target)))
/*      */       {
/*  323 */         WKeyboardFocusManagerPeer.requestFocusFor((Component)this.target, CausedFocusEvent.Cause.MOUSE_EVENT);
/*      */       }
/*      */       
/*      */       break;
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */   native void nativeHandleEvent(AWTEvent paramAWTEvent);
/*      */   
/*      */   public void handleEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  335 */     int i = paramAWTEvent.getID();
/*      */     
/*  337 */     if (((paramAWTEvent instanceof InputEvent)) && (!((InputEvent)paramAWTEvent).isConsumed()) && 
/*  338 */       (((Component)this.target).isEnabled()))
/*      */     {
/*  340 */       if (((paramAWTEvent instanceof MouseEvent)) && (!(paramAWTEvent instanceof MouseWheelEvent))) {
/*  341 */         handleJavaMouseEvent((MouseEvent)paramAWTEvent);
/*  342 */       } else if (((paramAWTEvent instanceof KeyEvent)) && 
/*  343 */         (handleJavaKeyEvent((KeyEvent)paramAWTEvent))) {
/*  344 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  349 */     switch (i)
/*      */     {
/*      */     case 800: 
/*  352 */       this.paintPending = false;
/*      */     
/*      */ 
/*      */ 
/*      */     case 801: 
/*  357 */       if ((!this.isLayouting) && (!this.paintPending)) {
/*  358 */         this.paintArea.paint(this.target, shouldClearRectBeforePaint());
/*      */       }
/*  360 */       return;
/*      */     case 1004: 
/*      */     case 1005: 
/*  363 */       handleJavaFocusEvent((FocusEvent)paramAWTEvent);
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*  369 */     nativeHandleEvent(paramAWTEvent);
/*      */   }
/*      */   
/*      */   void handleJavaFocusEvent(FocusEvent paramFocusEvent) {
/*  373 */     if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
/*  374 */       focusLog.finer(paramFocusEvent.toString());
/*      */     }
/*  376 */     setFocus(paramFocusEvent.getID() == 1004);
/*      */   }
/*      */   
/*      */   native void setFocus(boolean paramBoolean);
/*      */   
/*      */   public Dimension getMinimumSize()
/*      */   {
/*  383 */     return ((Component)this.target).getSize();
/*      */   }
/*      */   
/*      */   public Dimension getPreferredSize()
/*      */   {
/*  388 */     return getMinimumSize();
/*      */   }
/*      */   
/*      */ 
/*      */   public void layout() {}
/*      */   
/*      */   public Rectangle getBounds()
/*      */   {
/*  396 */     return ((Component)this.target).getBounds();
/*      */   }
/*      */   
/*      */   public boolean isFocusable()
/*      */   {
/*  401 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public GraphicsConfiguration getGraphicsConfiguration()
/*      */   {
/*  410 */     if (this.winGraphicsConfig != null) {
/*  411 */       return this.winGraphicsConfig;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  416 */     return ((Component)this.target).getGraphicsConfiguration();
/*      */   }
/*      */   
/*      */   public SurfaceData getSurfaceData()
/*      */   {
/*  421 */     return this.surfaceData;
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
/*      */   public void replaceSurfaceData()
/*      */   {
/*  434 */     replaceSurfaceData(this.numBackBuffers, this.backBufferCaps);
/*      */   }
/*      */   
/*      */   public void createScreenSurface(boolean paramBoolean)
/*      */   {
/*  439 */     Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
/*  440 */     ScreenUpdateManager localScreenUpdateManager = ScreenUpdateManager.getInstance();
/*      */     
/*  442 */     this.surfaceData = localScreenUpdateManager.createScreenSurface(localWin32GraphicsConfig, this, this.numBackBuffers, paramBoolean);
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
/*      */   public void replaceSurfaceData(int paramInt, BufferCapabilities paramBufferCapabilities)
/*      */   {
/*  455 */     SurfaceData localSurfaceData = null;
/*  456 */     VolatileImage localVolatileImage = null;
/*  457 */     synchronized (((Component)this.target).getTreeLock()) {
/*  458 */       synchronized (this) {
/*  459 */         if (this.pData == 0L) {
/*  460 */           return;
/*      */         }
/*  462 */         this.numBackBuffers = paramInt;
/*  463 */         ScreenUpdateManager localScreenUpdateManager = ScreenUpdateManager.getInstance();
/*  464 */         localSurfaceData = this.surfaceData;
/*  465 */         localScreenUpdateManager.dropScreenSurface(localSurfaceData);
/*  466 */         createScreenSurface(true);
/*  467 */         if (localSurfaceData != null) {
/*  468 */           localSurfaceData.invalidate();
/*      */         }
/*      */         
/*  471 */         localVolatileImage = this.backBuffer;
/*  472 */         if (this.numBackBuffers > 0)
/*      */         {
/*  474 */           this.backBufferCaps = paramBufferCapabilities;
/*      */           
/*  476 */           Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
/*  477 */           this.backBuffer = localWin32GraphicsConfig.createBackBuffer(this);
/*  478 */         } else if (this.backBuffer != null) {
/*  479 */           this.backBufferCaps = null;
/*  480 */           this.backBuffer = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  486 */     if (localSurfaceData != null) {
/*  487 */       localSurfaceData.flush();
/*      */       
/*  489 */       localSurfaceData = null;
/*      */     }
/*  491 */     if (localVolatileImage != null) {
/*  492 */       localVolatileImage.flush();
/*      */       
/*  494 */       localSurfaceData = null;
/*      */     }
/*      */   }
/*      */   
/*      */   public void replaceSurfaceDataLater() {
/*  499 */     Runnable local2 = new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*  505 */         if (!WComponentPeer.this.isDisposed()) {
/*      */           try {
/*  507 */             WComponentPeer.this.replaceSurfaceData();
/*      */ 
/*      */           }
/*      */           catch (InvalidPipeException localInvalidPipeException) {}
/*      */         }
/*      */       }
/*  513 */     };
/*  514 */     Component localComponent = (Component)this.target;
/*      */     
/*  516 */     if (!PaintEventDispatcher.getPaintEventDispatcher().queueSurfaceDataReplacing(localComponent, local2)) {
/*  517 */       postEvent(new InvocationEvent(localComponent, local2));
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration)
/*      */   {
/*  523 */     this.winGraphicsConfig = ((Win32GraphicsConfig)paramGraphicsConfiguration);
/*      */     try {
/*  525 */       replaceSurfaceData();
/*      */     }
/*      */     catch (InvalidPipeException localInvalidPipeException) {}
/*      */     
/*  529 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public ColorModel getColorModel()
/*      */   {
/*  535 */     GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration();
/*  536 */     if (localGraphicsConfiguration != null) {
/*  537 */       return localGraphicsConfiguration.getColorModel();
/*      */     }
/*      */     
/*  540 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ColorModel getDeviceColorModel()
/*      */   {
/*  547 */     Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
/*  548 */     if (localWin32GraphicsConfig != null) {
/*  549 */       return localWin32GraphicsConfig.getDeviceColorModel();
/*      */     }
/*      */     
/*  552 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ColorModel getColorModel(int paramInt)
/*      */   {
/*  559 */     GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration();
/*  560 */     if (localGraphicsConfiguration != null) {
/*  561 */       return localGraphicsConfiguration.getColorModel(paramInt);
/*      */     }
/*      */     
/*  564 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*  569 */   static final Font defaultFont = new Font("Dialog", 0, 12);
/*      */   private int updateX1;
/*      */   private int updateY1;
/*      */   
/*      */   public Graphics getGraphics() {
/*  574 */     if (isDisposed()) {
/*  575 */       return null;
/*      */     }
/*      */     
/*  578 */     Component localComponent = (Component)getTarget();
/*  579 */     Window localWindow = SunToolkit.getContainingWindow(localComponent);
/*  580 */     Object localObject4; if (localWindow != null)
/*      */     {
/*  582 */       localObject1 = ((WWindowPeer)localWindow.getPeer()).getTranslucentGraphics();
/*      */       
/*  584 */       if (localObject1 != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  589 */         int i = 0;int j = 0;
/*  590 */         for (localObject4 = localComponent; localObject4 != localWindow; localObject4 = ((Component)localObject4).getParent()) {
/*  591 */           i += ((Component)localObject4).getX();
/*  592 */           j += ((Component)localObject4).getY();
/*      */         }
/*      */         
/*  595 */         ((Graphics)localObject1).translate(i, j);
/*  596 */         ((Graphics)localObject1).clipRect(0, 0, localComponent.getWidth(), localComponent.getHeight());
/*      */         
/*  598 */         return (Graphics)localObject1;
/*      */       }
/*      */     }
/*      */     
/*  602 */     Object localObject1 = this.surfaceData;
/*  603 */     if (localObject1 != null)
/*      */     {
/*  605 */       Object localObject2 = this.background;
/*  606 */       if (localObject2 == null) {
/*  607 */         localObject2 = SystemColor.window;
/*      */       }
/*  609 */       Object localObject3 = this.foreground;
/*  610 */       if (localObject3 == null) {
/*  611 */         localObject3 = SystemColor.windowText;
/*      */       }
/*  613 */       localObject4 = this.font;
/*  614 */       if (localObject4 == null) {
/*  615 */         localObject4 = defaultFont;
/*      */       }
/*      */       
/*  618 */       ScreenUpdateManager localScreenUpdateManager = ScreenUpdateManager.getInstance();
/*  619 */       return localScreenUpdateManager.createGraphics((SurfaceData)localObject1, this, (Color)localObject3, (Color)localObject2, (Font)localObject4);
/*      */     }
/*      */     
/*  622 */     return null;
/*      */   }
/*      */   
/*      */   public FontMetrics getFontMetrics(Font paramFont) {
/*  626 */     return WFontMetrics.getFontMetrics(paramFont);
/*      */   }
/*      */   
/*      */   private synchronized native void _dispose();
/*      */   
/*      */   protected void disposeImpl() {
/*  632 */     SurfaceData localSurfaceData = this.surfaceData;
/*  633 */     this.surfaceData = null;
/*  634 */     ScreenUpdateManager.getInstance().dropScreenSurface(localSurfaceData);
/*  635 */     localSurfaceData.invalidate();
/*      */     
/*  637 */     WToolkit.targetDisposedPeer(this.target, this);
/*  638 */     _dispose();
/*      */   }
/*      */   
/*      */   public void disposeLater() {
/*  642 */     postEvent(new InvocationEvent(this.target, new Runnable()
/*      */     {
/*      */       public void run() {
/*  645 */         WComponentPeer.this.dispose();
/*      */       }
/*      */     }));
/*      */   }
/*      */   
/*      */   public synchronized void setForeground(Color paramColor)
/*      */   {
/*  652 */     this.foreground = paramColor;
/*  653 */     _setForeground(paramColor.getRGB());
/*      */   }
/*      */   
/*      */   public synchronized void setBackground(Color paramColor)
/*      */   {
/*  658 */     this.background = paramColor;
/*  659 */     _setBackground(paramColor.getRGB());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Color getBackgroundNoSync()
/*      */   {
/*  669 */     return this.background;
/*      */   }
/*      */   
/*      */   private native void _setForeground(int paramInt);
/*      */   
/*      */   private native void _setBackground(int paramInt);
/*      */   
/*      */   public synchronized void setFont(Font paramFont) {
/*  677 */     this.font = paramFont;
/*  678 */     _setFont(paramFont);
/*      */   }
/*      */   
/*      */   synchronized native void _setFont(Font paramFont);
/*      */   
/*  683 */   public void updateCursorImmediately() { WGlobalCursorManager.getCursorManager().updateCursorImmediately(); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
/*      */   {
/*  694 */     if (WKeyboardFocusManagerPeer.processSynchronousLightweightTransfer((Component)this.target, paramComponent, paramBoolean1, paramBoolean2, paramLong))
/*      */     {
/*      */ 
/*  697 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  701 */     int i = WKeyboardFocusManagerPeer.shouldNativelyFocusHeavyweight((Component)this.target, paramComponent, paramBoolean1, paramBoolean2, paramLong, paramCause);
/*      */     
/*      */ 
/*      */ 
/*  705 */     switch (i) {
/*      */     case 0: 
/*  707 */       return false;
/*      */     case 2: 
/*  709 */       if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
/*  710 */         focusLog.finer("Proceeding with request to " + paramComponent + " in " + this.target);
/*      */       }
/*  712 */       Window localWindow = SunToolkit.getContainingWindow((Component)this.target);
/*  713 */       if (localWindow == null) {
/*  714 */         return rejectFocusRequestHelper("WARNING: Parent window is null");
/*      */       }
/*  716 */       WWindowPeer localWWindowPeer = (WWindowPeer)localWindow.getPeer();
/*  717 */       if (localWWindowPeer == null) {
/*  718 */         return rejectFocusRequestHelper("WARNING: Parent window's peer is null");
/*      */       }
/*  720 */       boolean bool = localWWindowPeer.requestWindowFocus(paramCause);
/*      */       
/*  722 */       if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
/*  723 */         focusLog.finer("Requested window focus: " + bool);
/*      */       }
/*      */       
/*      */ 
/*  727 */       if ((!bool) || (!localWindow.isFocused())) {
/*  728 */         return rejectFocusRequestHelper("Waiting for asynchronous processing of the request");
/*      */       }
/*  730 */       return WKeyboardFocusManagerPeer.deliverFocus(paramComponent, (Component)this.target, paramBoolean1, paramBoolean2, paramLong, paramCause);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 1: 
/*  738 */       return true;
/*      */     }
/*  740 */     return false;
/*      */   }
/*      */   
/*      */   private boolean rejectFocusRequestHelper(String paramString) {
/*  744 */     if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
/*  745 */       focusLog.finer(paramString);
/*      */     }
/*  747 */     WKeyboardFocusManagerPeer.removeLastFocusRequest((Component)this.target);
/*  748 */     return false;
/*      */   }
/*      */   
/*      */   public Image createImage(ImageProducer paramImageProducer)
/*      */   {
/*  753 */     return new ToolkitImage(paramImageProducer);
/*      */   }
/*      */   
/*      */ 
/*      */   public Image createImage(int paramInt1, int paramInt2)
/*      */   {
/*  759 */     Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
/*  760 */     return localWin32GraphicsConfig.createAcceleratedImage((Component)this.target, paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */   public VolatileImage createVolatileImage(int paramInt1, int paramInt2)
/*      */   {
/*  765 */     return new SunVolatileImage((Component)this.target, paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */   public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*      */   {
/*  770 */     return Toolkit.getDefaultToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*      */   }
/*      */   
/*      */   public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*      */   {
/*  775 */     return Toolkit.getDefaultToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*      */   }
/*      */   
/*      */ 
/*      */   public String toString()
/*      */   {
/*  781 */     return getClass().getName() + "[" + this.target + "]";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   WComponentPeer(Component paramComponent)
/*      */   {
/*  789 */     this.target = paramComponent;
/*  790 */     this.paintArea = new RepaintArea();
/*  791 */     create(getNativeParent());
/*      */     
/*  793 */     checkCreation();
/*      */     
/*  795 */     createScreenSurface(false);
/*  796 */     initialize();
/*  797 */     start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   abstract void create(WComponentPeer paramWComponentPeer);
/*      */   
/*      */ 
/*      */ 
/*      */   WComponentPeer getNativeParent()
/*      */   {
/*  808 */     Container localContainer = SunToolkit.getNativeContainer((Component)this.target);
/*  809 */     return (WComponentPeer)WToolkit.targetToPeer(localContainer);
/*      */   }
/*      */   
/*      */   protected void checkCreation()
/*      */   {
/*  814 */     if ((this.hwnd == 0L) || (this.pData == 0L))
/*      */     {
/*  816 */       if (this.createError != null)
/*      */       {
/*  818 */         throw this.createError;
/*      */       }
/*      */       
/*      */ 
/*  822 */       throw new InternalError("couldn't create component peer");
/*      */     }
/*      */   }
/*      */   
/*      */   synchronized native void start();
/*      */   
/*      */   void initialize()
/*      */   {
/*  830 */     if (((Component)this.target).isVisible()) {
/*  831 */       show();
/*      */     }
/*  833 */     Color localColor = ((Component)this.target).getForeground();
/*  834 */     if (localColor != null) {
/*  835 */       setForeground(localColor);
/*      */     }
/*      */     
/*  838 */     Font localFont = ((Component)this.target).getFont();
/*  839 */     if (localFont != null) {
/*  840 */       setFont(localFont);
/*      */     }
/*  842 */     if (!((Component)this.target).isEnabled()) {
/*  843 */       disable();
/*      */     }
/*  845 */     Rectangle localRectangle = ((Component)this.target).getBounds();
/*  846 */     setBounds(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, 3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void handleRepaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void handleExpose(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  863 */     postPaintIfNecessary(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void handlePaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  874 */     postPaintIfNecessary(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */   }
/*      */   
/*      */   private void postPaintIfNecessary(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  878 */     if (!AWTAccessor.getComponentAccessor().getIgnoreRepaint((Component)this.target))
/*      */     {
/*  880 */       PaintEvent localPaintEvent = PaintEventDispatcher.getPaintEventDispatcher().createPaintEvent((Component)this.target, paramInt1, paramInt2, paramInt3, paramInt4);
/*  881 */       if (localPaintEvent != null) {
/*  882 */         postEvent(localPaintEvent);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void postEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  891 */     preprocessPostEvent(paramAWTEvent);
/*  892 */     WToolkit.postEvent(WToolkit.targetToAppContext(this.target), paramAWTEvent);
/*      */   }
/*      */   
/*      */ 
/*      */   void preprocessPostEvent(AWTEvent paramAWTEvent) {}
/*      */   
/*      */   public void beginLayout()
/*      */   {
/*  900 */     this.isLayouting = true;
/*      */   }
/*      */   
/*      */   public void endLayout() {
/*  904 */     if ((!this.paintArea.isEmpty()) && (!this.paintPending) && 
/*  905 */       (!((Component)this.target).getIgnoreRepaint()))
/*      */     {
/*  907 */       postEvent(new PaintEvent((Component)this.target, 800, new Rectangle()));
/*      */     }
/*      */     
/*  910 */     this.isLayouting = false;
/*      */   }
/*      */   
/*      */ 
/*      */   public native void beginValidate();
/*      */   
/*      */   public native void endValidate();
/*      */   
/*      */   public Dimension preferredSize()
/*      */   {
/*  920 */     return getPreferredSize();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void addDropTarget(DropTarget paramDropTarget)
/*      */   {
/*  929 */     if (this.nDropTargets == 0) {
/*  930 */       this.nativeDropTargetContext = addNativeDropTarget();
/*      */     }
/*  932 */     this.nDropTargets += 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void removeDropTarget(DropTarget paramDropTarget)
/*      */   {
/*  941 */     this.nDropTargets -= 1;
/*  942 */     if (this.nDropTargets == 0) {
/*  943 */       removeNativeDropTarget();
/*  944 */       this.nativeDropTargetContext = 0L;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   native long addNativeDropTarget();
/*      */   
/*      */ 
/*      */ 
/*      */   native void removeNativeDropTarget();
/*      */   
/*      */ 
/*      */ 
/*      */   native boolean nativeHandlesWheelScrolling();
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean handlesWheelScrolling()
/*      */   {
/*  965 */     return nativeHandlesWheelScrolling();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPaintPending()
/*      */   {
/*  971 */     return (this.paintPending) && (this.isLayouting);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int updateX2;
/*      */   
/*      */ 
/*      */   private int updateY2;
/*      */   
/*      */ 
/*      */   public void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities)
/*      */     throws AWTException
/*      */   {
/*  985 */     Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
/*  986 */     localWin32GraphicsConfig.assertOperationSupported((Component)this.target, paramInt, paramBufferCapabilities);
/*      */     
/*      */     try
/*      */     {
/*  990 */       replaceSurfaceData(paramInt - 1, paramBufferCapabilities);
/*      */     } catch (InvalidPipeException localInvalidPipeException) {
/*  992 */       throw new AWTException(localInvalidPipeException.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */   public void destroyBuffers()
/*      */   {
/*  998 */     replaceSurfaceData(0, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void flip(int paramInt1, int paramInt2, int paramInt3, int paramInt4, FlipContents paramFlipContents)
/*      */   {
/* 1005 */     VolatileImage localVolatileImage = this.backBuffer;
/* 1006 */     if (localVolatileImage == null) {
/* 1007 */       throw new IllegalStateException("Buffers have not been created");
/*      */     }
/*      */     
/* 1010 */     Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
/* 1011 */     localWin32GraphicsConfig.flip(this, (Component)this.target, localVolatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramFlipContents);
/*      */   }
/*      */   
/*      */   public synchronized Image getBackBuffer()
/*      */   {
/* 1016 */     VolatileImage localVolatileImage = this.backBuffer;
/* 1017 */     if (localVolatileImage == null) {
/* 1018 */       throw new IllegalStateException("Buffers have not been created");
/*      */     }
/* 1020 */     return localVolatileImage;
/*      */   }
/*      */   
/* 1023 */   public BufferCapabilities getBackBufferCaps() { return this.backBufferCaps; }
/*      */   
/*      */   public int getBackBuffersNum() {
/* 1026 */     return this.numBackBuffers;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean shouldClearRectBeforePaint()
/*      */   {
/* 1032 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   native void pSetParent(ComponentPeer paramComponentPeer);
/*      */   
/*      */ 
/*      */   public void reparent(ContainerPeer paramContainerPeer)
/*      */   {
/* 1042 */     pSetParent(paramContainerPeer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isReparentSupported()
/*      */   {
/* 1050 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 1056 */   private volatile boolean isAccelCapable = true;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBoundsOperation(int paramInt) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isAccelCapable()
/*      */   {
/* 1078 */     if ((!this.isAccelCapable) || 
/* 1079 */       (!isContainingTopLevelAccelCapable((Component)this.target)))
/*      */     {
/* 1081 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1085 */     boolean bool = SunToolkit.isContainingTopLevelTranslucent((Component)this.target);
/*      */     
/*      */ 
/* 1088 */     return (!bool) || (Win32GraphicsEnvironment.isVistaOS());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void disableAcceleration()
/*      */   {
/* 1095 */     this.isAccelCapable = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   native void setRectangularShape(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Region paramRegion);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final boolean isContainingTopLevelAccelCapable(Component paramComponent)
/*      */   {
/* 1109 */     while ((paramComponent != null) && (!(paramComponent instanceof WEmbeddedFrame))) {
/* 1110 */       paramComponent = paramComponent.getParent();
/*      */     }
/* 1112 */     if (paramComponent == null) {
/* 1113 */       return true;
/*      */     }
/* 1115 */     return ((WEmbeddedFramePeer)paramComponent.getPeer()).isAccelCapable();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void applyShape(Region paramRegion)
/*      */   {
/* 1125 */     if (shapeLog.isLoggable(PlatformLogger.Level.FINER)) {
/* 1126 */       shapeLog.finer("*** INFO: Setting shape: PEER: " + this + "; TARGET: " + this.target + "; SHAPE: " + paramRegion);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1131 */     if (paramRegion != null) {
/* 1132 */       setRectangularShape(paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getHiX(), paramRegion.getHiY(), paramRegion
/* 1133 */         .isRectangular() ? null : paramRegion);
/*      */     } else {
/* 1135 */       setRectangularShape(0, 0, 0, 0, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setZOrder(ComponentPeer paramComponentPeer)
/*      */   {
/* 1145 */     long l = paramComponentPeer != null ? ((WComponentPeer)paramComponentPeer).getHWnd() : 0L;
/*      */     
/* 1147 */     setZOrder(l);
/*      */   }
/*      */   
/*      */   private native void setZOrder(long paramLong);
/*      */   
/*      */   public boolean isLightweightFramePeer() {
/* 1153 */     return false;
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WComponentPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
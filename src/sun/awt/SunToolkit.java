/*      */ package sun.awt;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.Button;
/*      */ import java.awt.Canvas;
/*      */ import java.awt.Checkbox;
/*      */ import java.awt.Choice;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.Dialog.ModalExclusionType;
/*      */ import java.awt.Dialog.ModalityType;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.FileDialog;
/*      */ import java.awt.FocusTraversalPolicy;
/*      */ import java.awt.Font;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Image;
/*      */ import java.awt.KeyboardFocusManager;
/*      */ import java.awt.Label;
/*      */ import java.awt.MenuComponent;
/*      */ import java.awt.Panel;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.Robot;
/*      */ import java.awt.ScrollPane;
/*      */ import java.awt.Scrollbar;
/*      */ import java.awt.TextArea;
/*      */ import java.awt.TextField;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.dnd.DragGestureEvent;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.ImageProducer;
/*      */ import java.awt.peer.FramePeer;
/*      */ import java.awt.peer.PanelPeer;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.Permission;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Vector;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.concurrent.locks.Condition;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import sun.awt.image.FileImageSource;
/*      */ import sun.awt.image.ImageRepresentation;
/*      */ import sun.awt.image.MultiResolutionToolkitImage;
/*      */ import sun.awt.image.ToolkitImage;
/*      */ import sun.misc.SoftCache;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public abstract class SunToolkit extends Toolkit implements WindowClosingSupport, WindowClosingListener, ComponentFactory, InputMethodSupport, KeyboardFocusManagerPeerProvider
/*      */ {
/*      */   public static final int GRAB_EVENT_MASK = Integer.MIN_VALUE;
/*      */   private static final String POST_EVENT_QUEUE_KEY = "PostEventQueue";
/*      */   
/*      */   static
/*      */   {
/*   70 */     if (((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.nativedebug"))).booleanValue()) {
/*   71 */       DebugSettings.init();
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
/*   91 */   protected static int numberOfButtons = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int MAX_BUTTONS_SUPPORTED = 20;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void initEQ(AppContext paramAppContext)
/*      */   {
/*  116 */     String str = System.getProperty("AWT.EventQueueClass", "java.awt.EventQueue");
/*      */     EventQueue localEventQueue;
/*      */     try
/*      */     {
/*  120 */       localEventQueue = (EventQueue)Class.forName(str).newInstance();
/*      */     } catch (Exception localException) {
/*  122 */       localException.printStackTrace();
/*  123 */       System.err.println("Failed loading " + str + ": " + localException);
/*  124 */       localEventQueue = new EventQueue();
/*      */     }
/*  126 */     paramAppContext.put(AppContext.EVENT_QUEUE_KEY, localEventQueue);
/*      */     
/*  128 */     PostEventQueue localPostEventQueue = new PostEventQueue(localEventQueue);
/*  129 */     paramAppContext.put("PostEventQueue", localPostEventQueue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean useBufferPerWindow()
/*      */   {
/*  136 */     return false;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  249 */   private static final ReentrantLock AWT_LOCK = new ReentrantLock();
/*  250 */   private static final Condition AWT_LOCK_COND = AWT_LOCK.newCondition();
/*      */   
/*      */   public static final void awtLock() {
/*  253 */     AWT_LOCK.lock();
/*      */   }
/*      */   
/*      */   public static final boolean awtTryLock() {
/*  257 */     return AWT_LOCK.tryLock();
/*      */   }
/*      */   
/*      */   public static final void awtUnlock() {
/*  261 */     AWT_LOCK.unlock();
/*      */   }
/*      */   
/*      */   public static final void awtLockWait()
/*      */     throws InterruptedException
/*      */   {
/*  267 */     AWT_LOCK_COND.await();
/*      */   }
/*      */   
/*      */   public static final void awtLockWait(long paramLong)
/*      */     throws InterruptedException
/*      */   {
/*  273 */     AWT_LOCK_COND.await(paramLong, java.util.concurrent.TimeUnit.MILLISECONDS);
/*      */   }
/*      */   
/*      */   public static final void awtLockNotify() {
/*  277 */     AWT_LOCK_COND.signal();
/*      */   }
/*      */   
/*      */   public static final void awtLockNotifyAll() {
/*  281 */     AWT_LOCK_COND.signalAll();
/*      */   }
/*      */   
/*      */   public static final boolean isAWTLockHeldByCurrentThread() {
/*  285 */     return AWT_LOCK.isHeldByCurrentThread();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static AppContext createNewAppContext()
/*      */   {
/*  294 */     ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
/*  295 */     return createNewAppContext(localThreadGroup);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static final AppContext createNewAppContext(ThreadGroup paramThreadGroup)
/*      */   {
/*  302 */     AppContext localAppContext = new AppContext(paramThreadGroup);
/*  303 */     initEQ(localAppContext);
/*      */     
/*  305 */     return localAppContext;
/*      */   }
/*      */   
/*      */   static void wakeupEventQueue(EventQueue paramEventQueue, boolean paramBoolean) {
/*  309 */     AWTAccessor.getEventQueueAccessor().wakeup(paramEventQueue, paramBoolean);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static Object targetToPeer(Object paramObject)
/*      */   {
/*  321 */     if ((paramObject != null) && (!GraphicsEnvironment.isHeadless())) {
/*  322 */       return AWTAutoShutdown.getInstance().getPeer(paramObject);
/*      */     }
/*  324 */     return null;
/*      */   }
/*      */   
/*      */   protected static void targetCreatedPeer(Object paramObject1, Object paramObject2) {
/*  328 */     if ((paramObject1 != null) && (paramObject2 != null) && 
/*  329 */       (!GraphicsEnvironment.isHeadless()))
/*      */     {
/*  331 */       AWTAutoShutdown.getInstance().registerPeer(paramObject1, paramObject2);
/*      */     }
/*      */   }
/*      */   
/*      */   protected static void targetDisposedPeer(Object paramObject1, Object paramObject2) {
/*  336 */     if ((paramObject1 != null) && (paramObject2 != null) && 
/*  337 */       (!GraphicsEnvironment.isHeadless()))
/*      */     {
/*  339 */       AWTAutoShutdown.getInstance().unregisterPeer(paramObject1, paramObject2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  346 */   private static final Map<Object, AppContext> appContextMap = java.util.Collections.synchronizedMap(new WeakHashMap());
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean setAppContext(Object paramObject, AppContext paramAppContext)
/*      */   {
/*  354 */     if ((paramObject instanceof Component))
/*      */     {
/*  356 */       AWTAccessor.getComponentAccessor().setAppContext((Component)paramObject, paramAppContext);
/*  357 */     } else if ((paramObject instanceof MenuComponent))
/*      */     {
/*  359 */       AWTAccessor.getMenuComponentAccessor().setAppContext((MenuComponent)paramObject, paramAppContext);
/*      */     } else {
/*  361 */       return false;
/*      */     }
/*  363 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static AppContext getAppContext(Object paramObject)
/*      */   {
/*  371 */     if ((paramObject instanceof Component))
/*  372 */       return 
/*  373 */         AWTAccessor.getComponentAccessor().getAppContext((Component)paramObject);
/*  374 */     if ((paramObject instanceof MenuComponent)) {
/*  375 */       return 
/*  376 */         AWTAccessor.getMenuComponentAccessor().getAppContext((MenuComponent)paramObject);
/*      */     }
/*  378 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static AppContext targetToAppContext(Object paramObject)
/*      */   {
/*  389 */     if (paramObject == null) {
/*  390 */       return null;
/*      */     }
/*  392 */     AppContext localAppContext = getAppContext(paramObject);
/*  393 */     if (localAppContext == null)
/*      */     {
/*      */ 
/*  396 */       localAppContext = (AppContext)appContextMap.get(paramObject);
/*      */     }
/*  398 */     return localAppContext;
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
/*      */   public static void setLWRequestStatus(Window paramWindow, boolean paramBoolean)
/*      */   {
/*  427 */     AWTAccessor.getWindowAccessor().setLWRequestStatus(paramWindow, paramBoolean);
/*      */   }
/*      */   
/*      */ 
/*      */   public static void checkAndSetPolicy(Container paramContainer)
/*      */   {
/*  433 */     FocusTraversalPolicy localFocusTraversalPolicy = KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy();
/*      */     
/*  435 */     paramContainer.setFocusTraversalPolicy(localFocusTraversalPolicy);
/*      */   }
/*      */   
/*      */   private static FocusTraversalPolicy createLayoutPolicy() {
/*  439 */     FocusTraversalPolicy localFocusTraversalPolicy = null;
/*      */     try
/*      */     {
/*  442 */       Class localClass = Class.forName("javax.swing.LayoutFocusTraversalPolicy");
/*  443 */       localFocusTraversalPolicy = (FocusTraversalPolicy)localClass.newInstance();
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException) {
/*  446 */       if (!$assertionsDisabled) throw new AssertionError();
/*      */     }
/*      */     catch (InstantiationException localInstantiationException) {
/*  449 */       if (!$assertionsDisabled) throw new AssertionError();
/*      */     }
/*      */     catch (IllegalAccessException localIllegalAccessException) {
/*  452 */       if (!$assertionsDisabled) { throw new AssertionError();
/*      */       }
/*      */     }
/*  455 */     return localFocusTraversalPolicy;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void insertTargetMapping(Object paramObject, AppContext paramAppContext)
/*      */   {
/*  463 */     if (!setAppContext(paramObject, paramAppContext))
/*      */     {
/*      */ 
/*  466 */       appContextMap.put(paramObject, paramAppContext);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void postEvent(AppContext paramAppContext, AWTEvent paramAWTEvent)
/*      */   {
/*  478 */     if (paramAWTEvent == null) {
/*  479 */       throw new NullPointerException();
/*      */     }
/*      */     
/*  482 */     AWTAccessor.SequencedEventAccessor localSequencedEventAccessor = AWTAccessor.getSequencedEventAccessor();
/*  483 */     if ((localSequencedEventAccessor != null) && (localSequencedEventAccessor.isSequencedEvent(paramAWTEvent))) {
/*  484 */       localObject1 = localSequencedEventAccessor.getNested(paramAWTEvent);
/*  485 */       if ((((AWTEvent)localObject1).getID() == 208) && ((localObject1 instanceof TimedWindowEvent)))
/*      */       {
/*      */ 
/*  488 */         localObject2 = (TimedWindowEvent)localObject1;
/*  489 */         ((SunToolkit)Toolkit.getDefaultToolkit())
/*  490 */           .setWindowDeactivationTime((Window)((TimedWindowEvent)localObject2).getSource(), ((TimedWindowEvent)localObject2).getWhen());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  499 */     setSystemGenerated(paramAWTEvent);
/*  500 */     Object localObject1 = targetToAppContext(paramAWTEvent.getSource());
/*  501 */     if ((localObject1 != null) && (!localObject1.equals(paramAppContext))) {
/*  502 */       throw new RuntimeException("Event posted on wrong app context : " + paramAWTEvent);
/*      */     }
/*      */     
/*  505 */     Object localObject2 = (PostEventQueue)paramAppContext.get("PostEventQueue");
/*  506 */     if (localObject2 != null) {
/*  507 */       ((PostEventQueue)localObject2).postEvent(paramAWTEvent);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void postPriorityEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  515 */     PeerEvent localPeerEvent = new PeerEvent(Toolkit.getDefaultToolkit(), new Runnable() {
/*      */       public void run() {
/*  517 */         AWTAccessor.getAWTEventAccessor().setPosted(this.val$e);
/*  518 */         ((Component)this.val$e.getSource()).dispatchEvent(this.val$e); } }, 2L);
/*      */     
/*      */ 
/*  521 */     postEvent(targetToAppContext(paramAWTEvent.getSource()), localPeerEvent);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void flushPendingEvents()
/*      */   {
/*  529 */     AppContext localAppContext = AppContext.getAppContext();
/*  530 */     flushPendingEvents(localAppContext);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void flushPendingEvents(AppContext paramAppContext)
/*      */   {
/*  540 */     PostEventQueue localPostEventQueue = (PostEventQueue)paramAppContext.get("PostEventQueue");
/*  541 */     if (localPostEventQueue != null) {
/*  542 */       localPostEventQueue.flush();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void executeOnEventHandlerThread(Object paramObject, Runnable paramRunnable)
/*      */   {
/*  553 */     executeOnEventHandlerThread(new PeerEvent(paramObject, paramRunnable, 1L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void executeOnEventHandlerThread(Object paramObject, Runnable paramRunnable, long paramLong)
/*      */   {
/*  564 */     executeOnEventHandlerThread(new PeerEvent(paramObject, paramRunnable, 1L)
/*      */     {
/*      */       public long getWhen() {
/*  567 */         return this.val$when;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void executeOnEventHandlerThread(PeerEvent paramPeerEvent)
/*      */   {
/*  578 */     postEvent(targetToAppContext(paramPeerEvent.getSource()), paramPeerEvent);
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
/*      */   public static void invokeLaterOnAppContext(AppContext paramAppContext, Runnable paramRunnable)
/*      */   {
/*  592 */     postEvent(paramAppContext, new PeerEvent(
/*  593 */       Toolkit.getDefaultToolkit(), paramRunnable, 1L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void executeOnEDTAndWait(Object paramObject, Runnable paramRunnable)
/*      */     throws InterruptedException, java.lang.reflect.InvocationTargetException
/*      */   {
/*  605 */     if (EventQueue.isDispatchThread()) {
/*  606 */       throw new Error("Cannot call executeOnEDTAndWait from any event dispatcher thread");
/*      */     }
/*      */     
/*      */ 
/*  610 */     Object local1AWTInvocationLock = new Object() {};
/*  612 */     PeerEvent localPeerEvent = new PeerEvent(paramObject, paramRunnable, local1AWTInvocationLock, true, 1L);
/*      */     
/*  614 */     synchronized (local1AWTInvocationLock) {
/*  615 */       executeOnEventHandlerThread(localPeerEvent);
/*  616 */       while (!localPeerEvent.isDispatched()) {
/*  617 */         local1AWTInvocationLock.wait();
/*      */       }
/*      */     }
/*      */     
/*  621 */     ??? = localPeerEvent.getThrowable();
/*  622 */     if (??? != null) {
/*  623 */       throw new java.lang.reflect.InvocationTargetException((Throwable)???);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isDispatchThreadForAppContext(Object paramObject)
/*      */   {
/*  634 */     AppContext localAppContext = targetToAppContext(paramObject);
/*  635 */     EventQueue localEventQueue = (EventQueue)localAppContext.get(AppContext.EVENT_QUEUE_KEY);
/*      */     
/*  637 */     AWTAccessor.EventQueueAccessor localEventQueueAccessor = AWTAccessor.getEventQueueAccessor();
/*  638 */     return localEventQueueAccessor.isDispatchThreadImpl(localEventQueue);
/*      */   }
/*      */   
/*      */   public Dimension getScreenSize() {
/*  642 */     return new Dimension(getScreenWidth(), getScreenHeight());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public java.awt.FontMetrics getFontMetrics(Font paramFont)
/*      */   {
/*  649 */     return sun.font.FontDesignMetrics.getMetrics(paramFont);
/*      */   }
/*      */   
/*      */   public String[] getFontList()
/*      */   {
/*  654 */     String[] arrayOfString = { "Dialog", "SansSerif", "Serif", "Monospaced", "DialogInput" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  662 */     return arrayOfString;
/*      */   }
/*      */   
/*      */   public PanelPeer createPanel(Panel paramPanel) {
/*  666 */     return (PanelPeer)createComponent(paramPanel);
/*      */   }
/*      */   
/*      */   public java.awt.peer.CanvasPeer createCanvas(Canvas paramCanvas) {
/*  670 */     return (java.awt.peer.CanvasPeer)createComponent(paramCanvas);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void disableBackgroundErase(Canvas paramCanvas)
/*      */   {
/*  681 */     disableBackgroundEraseImpl(paramCanvas);
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
/*      */   public void disableBackgroundErase(Component paramComponent)
/*      */   {
/*  694 */     disableBackgroundEraseImpl(paramComponent);
/*      */   }
/*      */   
/*      */   private void disableBackgroundEraseImpl(Component paramComponent) {
/*  698 */     AWTAccessor.getComponentAccessor().setBackgroundEraseDisabled(paramComponent, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean getSunAwtNoerasebackground()
/*      */   {
/*  706 */     return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.noerasebackground"))).booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean getSunAwtErasebackgroundonresize()
/*      */   {
/*  714 */     return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.erasebackgroundonresize"))).booleanValue();
/*      */   }
/*      */   
/*      */ 
/*  718 */   static final SoftCache fileImgCache = new SoftCache();
/*      */   
/*  720 */   static final SoftCache urlImgCache = new SoftCache();
/*      */   
/*      */   static Image getImageFromHash(Toolkit paramToolkit, URL paramURL) {
/*  723 */     checkPermissions(paramURL);
/*  724 */     synchronized (urlImgCache) {
/*  725 */       String str = paramURL.toString();
/*  726 */       Image localImage = (Image)urlImgCache.get(str);
/*  727 */       if (localImage == null) {
/*      */         try {
/*  729 */           localImage = paramToolkit.createImage(new sun.awt.image.URLImageSource(paramURL));
/*  730 */           urlImgCache.put(str, localImage);
/*      */         }
/*      */         catch (Exception localException) {}
/*      */       }
/*  734 */       return localImage;
/*      */     }
/*      */   }
/*      */   
/*      */   static Image getImageFromHash(Toolkit paramToolkit, String paramString)
/*      */   {
/*  740 */     checkPermissions(paramString);
/*  741 */     synchronized (fileImgCache) {
/*  742 */       Image localImage = (Image)fileImgCache.get(paramString);
/*  743 */       if (localImage == null) {
/*      */         try {
/*  745 */           localImage = paramToolkit.createImage(new FileImageSource(paramString));
/*  746 */           fileImgCache.put(paramString, localImage);
/*      */         }
/*      */         catch (Exception localException) {}
/*      */       }
/*  750 */       return localImage;
/*      */     }
/*      */   }
/*      */   
/*      */   public Image getImage(String paramString) {
/*  755 */     return getImageFromHash(this, paramString);
/*      */   }
/*      */   
/*      */   public Image getImage(URL paramURL) {
/*  759 */     return getImageFromHash(this, paramURL);
/*      */   }
/*      */   
/*      */   protected Image getImageWithResolutionVariant(String paramString1, String paramString2)
/*      */   {
/*  764 */     synchronized (fileImgCache) {
/*  765 */       Image localImage1 = getImageFromHash(this, paramString1);
/*  766 */       if ((localImage1 instanceof sun.awt.image.MultiResolutionImage)) {
/*  767 */         return localImage1;
/*      */       }
/*  769 */       Image localImage2 = getImageFromHash(this, paramString2);
/*  770 */       localImage1 = createImageWithResolutionVariant(localImage1, localImage2);
/*  771 */       fileImgCache.put(paramString1, localImage1);
/*  772 */       return localImage1;
/*      */     }
/*      */   }
/*      */   
/*      */   protected Image getImageWithResolutionVariant(URL paramURL1, URL paramURL2)
/*      */   {
/*  778 */     synchronized (urlImgCache) {
/*  779 */       Image localImage1 = getImageFromHash(this, paramURL1);
/*  780 */       if ((localImage1 instanceof sun.awt.image.MultiResolutionImage)) {
/*  781 */         return localImage1;
/*      */       }
/*  783 */       Image localImage2 = getImageFromHash(this, paramURL2);
/*  784 */       localImage1 = createImageWithResolutionVariant(localImage1, localImage2);
/*  785 */       String str = paramURL1.toString();
/*  786 */       urlImgCache.put(str, localImage1);
/*  787 */       return localImage1;
/*      */     }
/*      */   }
/*      */   
/*      */   public Image createImage(String paramString)
/*      */   {
/*  793 */     checkPermissions(paramString);
/*  794 */     return createImage(new FileImageSource(paramString));
/*      */   }
/*      */   
/*      */   public Image createImage(URL paramURL) {
/*  798 */     checkPermissions(paramURL);
/*  799 */     return createImage(new sun.awt.image.URLImageSource(paramURL));
/*      */   }
/*      */   
/*      */   public Image createImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/*  803 */     return createImage(new sun.awt.image.ByteArrayImageSource(paramArrayOfByte, paramInt1, paramInt2));
/*      */   }
/*      */   
/*      */   public Image createImage(ImageProducer paramImageProducer) {
/*  807 */     return new ToolkitImage(paramImageProducer);
/*      */   }
/*      */   
/*      */   public static Image createImageWithResolutionVariant(Image paramImage1, Image paramImage2)
/*      */   {
/*  812 */     return new MultiResolutionToolkitImage(paramImage1, paramImage2);
/*      */   }
/*      */   
/*      */   public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
/*  816 */     if (!(paramImage instanceof ToolkitImage)) {
/*  817 */       return 32;
/*      */     }
/*      */     
/*  820 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/*      */     int i;
/*  822 */     if ((paramInt1 == 0) || (paramInt2 == 0)) {
/*  823 */       i = 32;
/*      */     } else {
/*  825 */       i = localToolkitImage.getImageRep().check(paramImageObserver);
/*      */     }
/*  827 */     return (localToolkitImage.check(paramImageObserver) | i) & checkResolutionVariant(paramImage, paramInt1, paramInt2, paramImageObserver);
/*      */   }
/*      */   
/*      */   public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
/*  831 */     if ((paramInt1 == 0) || (paramInt2 == 0)) {
/*  832 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  836 */     if (!(paramImage instanceof ToolkitImage)) {
/*  837 */       return true;
/*      */     }
/*      */     
/*  840 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/*  841 */     if (localToolkitImage.hasError()) {
/*  842 */       if (paramImageObserver != null) {
/*  843 */         paramImageObserver.imageUpdate(paramImage, 192, -1, -1, -1, -1);
/*      */       }
/*      */       
/*  846 */       return false;
/*      */     }
/*  848 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/*  849 */     return localImageRepresentation.prepare(paramImageObserver) & prepareResolutionVariant(paramImage, paramInt1, paramInt2, paramImageObserver);
/*      */   }
/*      */   
/*      */   private int checkResolutionVariant(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
/*  853 */     ToolkitImage localToolkitImage = getResolutionVariant(paramImage);
/*  854 */     int i = getRVSize(paramInt1);
/*  855 */     int j = getRVSize(paramInt2);
/*      */     
/*  857 */     return (localToolkitImage == null) || (localToolkitImage.hasError()) ? 65535 : 
/*  858 */       checkImage(localToolkitImage, i, j, 
/*  859 */       MultiResolutionToolkitImage.getResolutionVariantObserver(paramImage, paramImageObserver, paramInt1, paramInt2, i, j, true));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean prepareResolutionVariant(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*      */   {
/*  866 */     ToolkitImage localToolkitImage = getResolutionVariant(paramImage);
/*  867 */     int i = getRVSize(paramInt1);
/*  868 */     int j = getRVSize(paramInt2);
/*      */     
/*  870 */     return (localToolkitImage == null) || (localToolkitImage.hasError()) || (prepareImage(localToolkitImage, i, j, 
/*      */     
/*  872 */       MultiResolutionToolkitImage.getResolutionVariantObserver(paramImage, paramImageObserver, paramInt1, paramInt2, i, j, true)));
/*      */   }
/*      */   
/*      */   private static int getRVSize(int paramInt)
/*      */   {
/*  877 */     return paramInt == -1 ? -1 : 2 * paramInt;
/*      */   }
/*      */   
/*      */   private static ToolkitImage getResolutionVariant(Image paramImage) {
/*  881 */     if ((paramImage instanceof MultiResolutionToolkitImage))
/*      */     {
/*  883 */       Image localImage = ((MultiResolutionToolkitImage)paramImage).getResolutionVariant();
/*  884 */       if ((localImage instanceof ToolkitImage)) {
/*  885 */         return (ToolkitImage)localImage;
/*      */       }
/*      */     }
/*  888 */     return null;
/*      */   }
/*      */   
/*      */   protected static boolean imageCached(String paramString) {
/*  892 */     return fileImgCache.containsKey(paramString);
/*      */   }
/*      */   
/*      */   protected static boolean imageCached(URL paramURL) {
/*  896 */     String str = paramURL.toString();
/*  897 */     return urlImgCache.containsKey(str);
/*      */   }
/*      */   
/*      */   protected static boolean imageExists(String paramString) {
/*  901 */     if (paramString != null) {
/*  902 */       checkPermissions(paramString);
/*  903 */       return new File(paramString).exists();
/*      */     }
/*  905 */     return false;
/*      */   }
/*      */   
/*      */   protected static boolean imageExists(URL paramURL)
/*      */   {
/*  910 */     if (paramURL != null) {
/*  911 */       checkPermissions(paramURL);
/*  912 */       try { InputStream localInputStream = paramURL.openStream();Object localObject1 = null;
/*  913 */         try { return true;
/*      */         }
/*      */         catch (Throwable localThrowable1)
/*      */         {
/*  912 */           localObject1 = localThrowable1;throw localThrowable1;
/*      */         } finally {
/*  914 */           if (localInputStream != null) { if (localObject1 != null) try { localInputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else { localInputStream.close();
/*      */             }
/*      */           }
/*      */         }
/*  918 */         return false;
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  915 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static void checkPermissions(String paramString)
/*      */   {
/*  922 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  923 */     if (localSecurityManager != null) {
/*  924 */       localSecurityManager.checkRead(paramString);
/*      */     }
/*      */   }
/*      */   
/*      */   private static void checkPermissions(URL paramURL) {
/*  929 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  930 */     if (localSecurityManager != null) {
/*      */       try
/*      */       {
/*  933 */         Permission localPermission = sun.net.util.URLUtil.getConnectPermission(paramURL);
/*  934 */         if (localPermission != null) {
/*      */           try {
/*  936 */             localSecurityManager.checkPermission(localPermission);
/*      */           }
/*      */           catch (SecurityException localSecurityException)
/*      */           {
/*  940 */             if (((localPermission instanceof java.io.FilePermission)) && 
/*  941 */               (localPermission.getActions().indexOf("read") != -1)) {
/*  942 */               localSecurityManager.checkRead(localPermission.getName());
/*  943 */             } else { if ((localPermission instanceof java.net.SocketPermission))
/*      */               {
/*  945 */                 if (localPermission.getActions().indexOf("connect") != -1) {
/*  946 */                   localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort()); return;
/*      */                 } }
/*  948 */               throw localSecurityException;
/*      */             }
/*      */           }
/*      */         }
/*      */       } catch (IOException localIOException) {
/*  953 */         localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static BufferedImage getScaledIconImage(java.util.List<Image> paramList, int paramInt1, int paramInt2)
/*      */   {
/*  963 */     if ((paramInt1 == 0) || (paramInt2 == 0)) {
/*  964 */       return null;
/*      */     }
/*  966 */     Object localObject1 = null;
/*  967 */     int i = 0;
/*  968 */     int j = 0;
/*  969 */     double d1 = 3.0D;
/*  970 */     double d2 = 0.0D;
/*  971 */     for (Object localObject2 = paramList.iterator(); ((Iterator)localObject2).hasNext();)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  977 */       localObject3 = (Image)((Iterator)localObject2).next();
/*  978 */       if (localObject3 != null)
/*      */       {
/*      */ 
/*  981 */         if ((localObject3 instanceof ToolkitImage)) {
/*  982 */           ImageRepresentation localImageRepresentation = ((ToolkitImage)localObject3).getImageRep();
/*  983 */           localImageRepresentation.reconstruct(32);
/*      */         }
/*      */         
/*      */         try
/*      */         {
/*  988 */           k = ((Image)localObject3).getWidth(null);
/*  989 */           m = ((Image)localObject3).getHeight(null);
/*      */         } catch (Exception localException) {}
/*  991 */         continue;
/*      */         
/*  993 */         if ((k > 0) && (m > 0))
/*      */         {
/*  995 */           double d3 = Math.min(paramInt1 / k, paramInt2 / m);
/*      */           
/*      */ 
/*      */ 
/*  999 */           int n = 0;
/* 1000 */           int i1 = 0;
/* 1001 */           double d4 = 1.0D;
/* 1002 */           if (d3 >= 2.0D)
/*      */           {
/*      */ 
/* 1005 */             d3 = Math.floor(d3);
/* 1006 */             n = k * (int)d3;
/* 1007 */             i1 = m * (int)d3;
/* 1008 */             d4 = 1.0D - 0.5D / d3;
/* 1009 */           } else if (d3 >= 1.0D)
/*      */           {
/* 1011 */             d3 = 1.0D;
/* 1012 */             n = k;
/* 1013 */             i1 = m;
/* 1014 */             d4 = 0.0D;
/* 1015 */           } else if (d3 >= 0.75D)
/*      */           {
/* 1017 */             d3 = 0.75D;
/* 1018 */             n = k * 3 / 4;
/* 1019 */             i1 = m * 3 / 4;
/* 1020 */             d4 = 0.3D;
/* 1021 */           } else if (d3 >= 0.6666D)
/*      */           {
/* 1023 */             d3 = 0.6666D;
/* 1024 */             n = k * 2 / 3;
/* 1025 */             i1 = m * 2 / 3;
/* 1026 */             d4 = 0.33D;
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 1031 */             d5 = Math.ceil(1.0D / d3);
/* 1032 */             d3 = 1.0D / d5;
/* 1033 */             n = (int)Math.round(k / d5);
/* 1034 */             i1 = (int)Math.round(m / d5);
/* 1035 */             d4 = 1.0D - 1.0D / d5;
/*      */           }
/* 1037 */           double d5 = (paramInt1 - n) / paramInt1 + (paramInt2 - i1) / paramInt2 + d4;
/*      */           
/*      */ 
/* 1040 */           if (d5 < d1) {
/* 1041 */             d1 = d5;
/* 1042 */             d2 = d3;
/* 1043 */             localObject1 = localObject3;
/* 1044 */             i = n;
/* 1045 */             j = i1;
/*      */           }
/* 1047 */           if (d5 == 0.0D) break; } } }
/*      */     int k;
/*      */     int m;
/* 1050 */     if (localObject1 == null)
/*      */     {
/* 1052 */       return null;
/*      */     }
/* 1054 */     localObject2 = new BufferedImage(paramInt1, paramInt2, 2);
/*      */     
/* 1056 */     Object localObject3 = ((BufferedImage)localObject2).createGraphics();
/* 1057 */     ((Graphics2D)localObject3).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
/*      */     try
/*      */     {
/* 1060 */       k = (paramInt1 - i) / 2;
/* 1061 */       m = (paramInt2 - j) / 2;
/* 1062 */       ((Graphics2D)localObject3).drawImage((Image)localObject1, k, m, i, j, null);
/*      */     } finally {
/* 1064 */       ((Graphics2D)localObject3).dispose();
/*      */     }
/* 1066 */     return (BufferedImage)localObject2;
/*      */   }
/*      */   
/*      */   public static java.awt.image.DataBufferInt getScaledIconData(java.util.List<Image> paramList, int paramInt1, int paramInt2) {
/* 1070 */     BufferedImage localBufferedImage = getScaledIconImage(paramList, paramInt1, paramInt2);
/* 1071 */     if (localBufferedImage == null) {
/* 1072 */       return null;
/*      */     }
/* 1074 */     java.awt.image.WritableRaster localWritableRaster = localBufferedImage.getRaster();
/* 1075 */     java.awt.image.DataBuffer localDataBuffer = localWritableRaster.getDataBuffer();
/* 1076 */     return (java.awt.image.DataBufferInt)localDataBuffer;
/*      */   }
/*      */   
/*      */   protected EventQueue getSystemEventQueueImpl() {
/* 1080 */     return getSystemEventQueueImplPP();
/*      */   }
/*      */   
/*      */   static EventQueue getSystemEventQueueImplPP()
/*      */   {
/* 1085 */     return getSystemEventQueueImplPP(AppContext.getAppContext());
/*      */   }
/*      */   
/*      */   public static EventQueue getSystemEventQueueImplPP(AppContext paramAppContext)
/*      */   {
/* 1090 */     EventQueue localEventQueue = (EventQueue)paramAppContext.get(AppContext.EVENT_QUEUE_KEY);
/* 1091 */     return localEventQueue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Container getNativeContainer(Component paramComponent)
/*      */   {
/* 1099 */     return Toolkit.getNativeContainer(paramComponent);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Component getHeavyweightComponent(Component paramComponent)
/*      */   {
/* 1108 */     while ((paramComponent != null) && (AWTAccessor.getComponentAccessor().isLightweight(paramComponent))) {
/* 1109 */       paramComponent = AWTAccessor.getComponentAccessor().getParent(paramComponent);
/*      */     }
/* 1111 */     return paramComponent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getFocusAcceleratorKeyMask()
/*      */   {
/* 1118 */     return 8;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isPrintableCharacterModifiersMask(int paramInt)
/*      */   {
/* 1128 */     return (paramInt & 0x8) == (paramInt & 0x2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canPopupOverlapTaskBar()
/*      */   {
/* 1137 */     boolean bool = true;
/*      */     try {
/* 1139 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 1140 */       if (localSecurityManager != null) {
/* 1141 */         localSecurityManager.checkPermission(sun.security.util.SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
/*      */       }
/*      */     }
/*      */     catch (SecurityException localSecurityException)
/*      */     {
/* 1146 */       bool = false;
/*      */     }
/* 1148 */     return bool;
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
/*      */   public Window createInputMethodWindow(String paramString, sun.awt.im.InputContext paramInputContext)
/*      */   {
/* 1162 */     return new sun.awt.im.SimpleInputMethodWindow(paramString, paramInputContext);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean enableInputMethodsForTextComponent()
/*      */   {
/* 1170 */     return false;
/*      */   }
/*      */   
/* 1173 */   private static Locale startupLocale = null;
/*      */   
/*      */ 
/*      */ 
/*      */   public static Locale getStartupLocale()
/*      */   {
/* 1179 */     if (startupLocale == null)
/*      */     {
/* 1181 */       String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.language", "en"));
/*      */       
/*      */ 
/* 1184 */       String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("user.region"));
/*      */       String str3;
/* 1186 */       String str4; if (str2 != null)
/*      */       {
/* 1188 */         int i = str2.indexOf('_');
/* 1189 */         if (i >= 0) {
/* 1190 */           str3 = str2.substring(0, i);
/* 1191 */           str4 = str2.substring(i + 1);
/*      */         } else {
/* 1193 */           str3 = str2;
/* 1194 */           str4 = "";
/*      */         }
/*      */       } else {
/* 1197 */         str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.country", ""));
/*      */         
/* 1199 */         str4 = (String)AccessController.doPrivileged(new GetPropertyAction("user.variant", ""));
/*      */       }
/*      */       
/* 1202 */       startupLocale = new Locale(str1, str3, str4);
/*      */     }
/* 1204 */     return startupLocale;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Locale getDefaultKeyboardLocale()
/*      */   {
/* 1211 */     return getStartupLocale();
/*      */   }
/*      */   
/*      */ 
/* 1215 */   private transient WindowClosingListener windowClosingListener = null;
/*      */   
/*      */ 
/*      */   public WindowClosingListener getWindowClosingListener()
/*      */   {
/* 1220 */     return this.windowClosingListener;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setWindowClosingListener(WindowClosingListener paramWindowClosingListener)
/*      */   {
/* 1226 */     this.windowClosingListener = paramWindowClosingListener;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public RuntimeException windowClosingNotify(WindowEvent paramWindowEvent)
/*      */   {
/* 1233 */     if (this.windowClosingListener != null) {
/* 1234 */       return this.windowClosingListener.windowClosingNotify(paramWindowEvent);
/*      */     }
/* 1236 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public RuntimeException windowClosingDelivered(WindowEvent paramWindowEvent)
/*      */   {
/* 1243 */     if (this.windowClosingListener != null) {
/* 1244 */       return this.windowClosingListener.windowClosingDelivered(paramWindowEvent);
/*      */     }
/* 1246 */     return null;
/*      */   }
/*      */   
/*      */ 
/* 1250 */   private static DefaultMouseInfoPeer mPeer = null;
/*      */   
/*      */   protected synchronized java.awt.peer.MouseInfoPeer getMouseInfoPeer() {
/* 1253 */     if (mPeer == null) {
/* 1254 */       mPeer = new DefaultMouseInfoPeer();
/*      */     }
/* 1256 */     return mPeer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean needsXEmbed()
/*      */   {
/* 1267 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.awt.noxembed", "false"));
/* 1268 */     if ("true".equals(str)) {
/* 1269 */       return false;
/*      */     }
/*      */     
/* 1272 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1273 */     if ((localToolkit instanceof SunToolkit))
/*      */     {
/*      */ 
/* 1276 */       return ((SunToolkit)localToolkit).needsXEmbedImpl();
/*      */     }
/*      */     
/* 1279 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean needsXEmbedImpl()
/*      */   {
/* 1289 */     return false;
/*      */   }
/*      */   
/* 1292 */   private static ModalExclusionType DEFAULT_MODAL_EXCLUSION_TYPE = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean isXEmbedServerRequested()
/*      */   {
/* 1300 */     return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.xembedserver"))).booleanValue();
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
/*      */   public static boolean isModalExcludedSupported()
/*      */   {
/* 1317 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1318 */     return localToolkit.isModalExclusionTypeSupported(DEFAULT_MODAL_EXCLUSION_TYPE);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isModalExcludedSupportedImpl()
/*      */   {
/* 1330 */     return false;
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
/*      */   public static void setModalExcluded(Window paramWindow)
/*      */   {
/* 1351 */     if (DEFAULT_MODAL_EXCLUSION_TYPE == null) {
/* 1352 */       DEFAULT_MODAL_EXCLUSION_TYPE = ModalExclusionType.APPLICATION_EXCLUDE;
/*      */     }
/* 1354 */     paramWindow.setModalExclusionType(DEFAULT_MODAL_EXCLUSION_TYPE);
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
/*      */   public static boolean isModalExcluded(Window paramWindow)
/*      */   {
/* 1375 */     if (DEFAULT_MODAL_EXCLUSION_TYPE == null) {
/* 1376 */       DEFAULT_MODAL_EXCLUSION_TYPE = ModalExclusionType.APPLICATION_EXCLUDE;
/*      */     }
/* 1378 */     return paramWindow.getModalExclusionType().compareTo(DEFAULT_MODAL_EXCLUSION_TYPE) >= 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isModalityTypeSupported(ModalityType paramModalityType)
/*      */   {
/* 1385 */     return (paramModalityType == ModalityType.MODELESS) || (paramModalityType == ModalityType.APPLICATION_MODAL);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isModalExclusionTypeSupported(ModalExclusionType paramModalExclusionType)
/*      */   {
/* 1393 */     return paramModalExclusionType == ModalExclusionType.NO_EXCLUDE;
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
/* 1404 */   private ModalityListenerList modalityListeners = new ModalityListenerList();
/*      */   public static final int DEFAULT_WAIT_TIME = 10000;
/*      */   
/* 1407 */   public void addModalityListener(ModalityListener paramModalityListener) { this.modalityListeners.add(paramModalityListener); }
/*      */   
/*      */   public void removeModalityListener(ModalityListener paramModalityListener)
/*      */   {
/* 1411 */     this.modalityListeners.remove(paramModalityListener);
/*      */   }
/*      */   
/*      */   public void notifyModalityPushed(Dialog paramDialog) {
/* 1415 */     notifyModalityChange(1300, paramDialog);
/*      */   }
/*      */   
/*      */   public void notifyModalityPopped(Dialog paramDialog) {
/* 1419 */     notifyModalityChange(1301, paramDialog);
/*      */   }
/*      */   
/*      */   final void notifyModalityChange(int paramInt, Dialog paramDialog) {
/* 1423 */     ModalityEvent localModalityEvent = new ModalityEvent(paramDialog, this.modalityListeners, paramInt);
/* 1424 */     localModalityEvent.dispatch();
/*      */   }
/*      */   
/*      */   static class ModalityListenerList implements ModalityListener
/*      */   {
/* 1429 */     Vector<ModalityListener> listeners = new Vector();
/*      */     
/*      */     void add(ModalityListener paramModalityListener) {
/* 1432 */       this.listeners.addElement(paramModalityListener);
/*      */     }
/*      */     
/*      */     void remove(ModalityListener paramModalityListener) {
/* 1436 */       this.listeners.removeElement(paramModalityListener);
/*      */     }
/*      */     
/*      */     public void modalityPushed(ModalityEvent paramModalityEvent) {
/* 1440 */       Iterator localIterator = this.listeners.iterator();
/* 1441 */       while (localIterator.hasNext()) {
/* 1442 */         ((ModalityListener)localIterator.next()).modalityPushed(paramModalityEvent);
/*      */       }
/*      */     }
/*      */     
/*      */     public void modalityPopped(ModalityEvent paramModalityEvent) {
/* 1447 */       Iterator localIterator = this.listeners.iterator();
/* 1448 */       while (localIterator.hasNext()) {
/* 1449 */         ((ModalityListener)localIterator.next()).modalityPopped(paramModalityEvent);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isLightweightOrUnknown(Component paramComponent)
/*      */   {
/* 1459 */     if ((paramComponent.isLightweight()) || 
/* 1460 */       (!(getDefaultToolkit() instanceof SunToolkit)))
/*      */     {
/* 1462 */       return true;
/*      */     }
/* 1464 */     return (!(paramComponent instanceof Button)) && (!(paramComponent instanceof Canvas)) && (!(paramComponent instanceof Checkbox)) && (!(paramComponent instanceof Choice)) && (!(paramComponent instanceof Label)) && (!(paramComponent instanceof java.awt.List)) && (!(paramComponent instanceof Panel)) && (!(paramComponent instanceof Scrollbar)) && (!(paramComponent instanceof ScrollPane)) && (!(paramComponent instanceof TextArea)) && (!(paramComponent instanceof TextField)) && (!(paramComponent instanceof Window));
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
/*      */   public static class OperationTimedOut
/*      */     extends RuntimeException
/*      */   {
/*      */     public OperationTimedOut(String paramString)
/*      */     {
/* 1481 */       super();
/*      */     }
/*      */     
/*      */ 
/*      */     public OperationTimedOut() {}
/*      */   }
/*      */   
/*      */ 
/*      */   public static class IllegalThreadException
/*      */     extends RuntimeException
/*      */   {
/*      */     public IllegalThreadException(String paramString)
/*      */     {
/* 1494 */       super();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public IllegalThreadException() {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void realSync()
/*      */     throws OperationTimedOut, InfiniteLoop
/*      */   {
/* 1509 */     realSync(10000L);
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
/*      */   private static final int MAX_ITERS = 20;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int MIN_ITERS = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int MINIMAL_EDELAY = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void realSync(long paramLong)
/*      */     throws OperationTimedOut, InfiniteLoop
/*      */   {
/* 1559 */     if (EventQueue.isDispatchThread()) {
/* 1560 */       throw new IllegalThreadException("The SunToolkit.realSync() method cannot be used on the event dispatch thread (EDT).");
/*      */     }
/* 1562 */     int i = 0;
/*      */     do
/*      */     {
/* 1565 */       sync();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1572 */       int j = 0;
/* 1573 */       while (j < 0) {
/* 1574 */         syncNativeQueue(paramLong);
/* 1575 */         j++;
/*      */       }
/* 1577 */       while ((syncNativeQueue(paramLong)) && (j < 20)) {
/* 1578 */         j++;
/*      */       }
/* 1580 */       if (j >= 20) {
/* 1581 */         throw new InfiniteLoop();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1591 */       j = 0;
/* 1592 */       while (j < 0) {
/* 1593 */         waitForIdle(paramLong);
/* 1594 */         j++;
/*      */       }
/* 1596 */       while ((waitForIdle(paramLong)) && (j < 20)) {
/* 1597 */         j++;
/*      */       }
/* 1599 */       if (j >= 20) {
/* 1600 */         throw new InfiniteLoop();
/*      */       }
/*      */       
/* 1603 */       i++;
/*      */ 
/*      */ 
/*      */     }
/* 1607 */     while (((syncNativeQueue(paramLong)) || (waitForIdle(paramLong))) && (i < 20));
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
/* 1620 */   private boolean eventDispatched = false;
/* 1621 */   private boolean queueEmpty = false;
/* 1622 */   private final Object waitLock = "Wait Lock";
/*      */   private static boolean checkedSystemAAFontSettings;
/*      */   
/* 1625 */   private boolean isEQEmpty() { EventQueue localEventQueue = getSystemEventQueueImpl();
/* 1626 */     return AWTAccessor.getEventQueueAccessor().noEvents(localEventQueue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean waitForIdle(long paramLong)
/*      */   {
/* 1638 */     flushPendingEvents();
/* 1639 */     boolean bool = isEQEmpty();
/* 1640 */     this.queueEmpty = false;
/* 1641 */     this.eventDispatched = false;
/* 1642 */     synchronized (this.waitLock) {
/* 1643 */       postEvent(AppContext.getAppContext(), new PeerEvent(
/* 1644 */         getSystemEventQueueImpl(), null, 4L)
/*      */         {
/*      */ 
/*      */ 
/*      */           public void dispatch()
/*      */           {
/*      */ 
/*      */ 
/* 1651 */             int i = 0;
/* 1652 */             while (i < 0) {
/* 1653 */               SunToolkit.this.syncNativeQueue(this.val$timeout);
/* 1654 */               i++;
/*      */             }
/* 1656 */             while ((SunToolkit.this.syncNativeQueue(this.val$timeout)) && (i < 20)) {
/* 1657 */               i++;
/*      */             }
/* 1659 */             SunToolkit.flushPendingEvents();
/*      */             
/* 1661 */             synchronized (SunToolkit.this.waitLock) {
/* 1662 */               SunToolkit.this.queueEmpty = SunToolkit.this.isEQEmpty();
/* 1663 */               SunToolkit.this.eventDispatched = true;
/* 1664 */               SunToolkit.this.waitLock.notifyAll();
/*      */             }
/*      */           }
/*      */         });
/*      */       try {
/* 1669 */         while (!this.eventDispatched) {
/* 1670 */           this.waitLock.wait();
/*      */         }
/*      */       } catch (InterruptedException localInterruptedException) {
/* 1673 */         return false;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1678 */       Thread.sleep(0L);
/*      */     } catch (InterruptedException ???) {
/* 1680 */       throw new RuntimeException("Interrupted");
/*      */     }
/*      */     
/* 1683 */     flushPendingEvents();
/*      */     
/*      */ 
/* 1686 */     synchronized (this.waitLock) {
/* 1687 */       return (!this.queueEmpty) || (!isEQEmpty()) || (!bool);
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
/*      */   private static boolean useSystemAAFontSettings;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void fireDesktopFontPropertyChanges()
/*      */   {
/* 1725 */     setDesktopProperty("awt.font.desktophints", 
/* 1726 */       getDesktopFontHints());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/* 1731 */   private static boolean lastExtraCondition = true;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static RenderingHints desktopFontHints;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final String DESKTOPFONTHINTS = "awt.font.desktophints";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setAAFontSettingsCondition(boolean paramBoolean)
/*      */   {
/* 1759 */     if (paramBoolean != lastExtraCondition) {
/* 1760 */       lastExtraCondition = paramBoolean;
/* 1761 */       if (checkedSystemAAFontSettings)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1767 */         checkedSystemAAFontSettings = false;
/* 1768 */         Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1769 */         if ((localToolkit instanceof SunToolkit)) {
/* 1770 */           ((SunToolkit)localToolkit).fireDesktopFontPropertyChanges();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static RenderingHints getDesktopAAHintsByName(String paramString)
/*      */   {
/* 1781 */     Object localObject = null;
/* 1782 */     paramString = paramString.toLowerCase(Locale.ENGLISH);
/* 1783 */     if (paramString.equals("on")) {
/* 1784 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
/* 1785 */     } else if (paramString.equals("gasp")) {
/* 1786 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
/* 1787 */     } else if ((paramString.equals("lcd")) || (paramString.equals("lcd_hrgb"))) {
/* 1788 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
/* 1789 */     } else if (paramString.equals("lcd_hbgr")) {
/* 1790 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
/* 1791 */     } else if (paramString.equals("lcd_vrgb")) {
/* 1792 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
/* 1793 */     } else if (paramString.equals("lcd_vbgr")) {
/* 1794 */       localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
/*      */     }
/* 1796 */     if (localObject != null) {
/* 1797 */       RenderingHints localRenderingHints = new RenderingHints(null);
/* 1798 */       localRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, localObject);
/* 1799 */       return localRenderingHints;
/*      */     }
/* 1801 */     return null;
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
/*      */   private static boolean useSystemAAFontSettings()
/*      */   {
/* 1814 */     if (!checkedSystemAAFontSettings) {
/* 1815 */       useSystemAAFontSettings = true;
/* 1816 */       String str = null;
/* 1817 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1818 */       if ((localToolkit instanceof SunToolkit))
/*      */       {
/* 1820 */         str = (String)AccessController.doPrivileged(new GetPropertyAction("awt.useSystemAAFontSettings"));
/*      */       }
/*      */       
/* 1823 */       if (str != null)
/*      */       {
/* 1825 */         useSystemAAFontSettings = Boolean.valueOf(str).booleanValue();
/*      */         
/*      */ 
/*      */ 
/* 1829 */         if (!useSystemAAFontSettings) {
/* 1830 */           desktopFontHints = getDesktopAAHintsByName(str);
/*      */         }
/*      */       }
/*      */       
/* 1834 */       if (useSystemAAFontSettings) {
/* 1835 */         useSystemAAFontSettings = lastExtraCondition;
/*      */       }
/* 1837 */       checkedSystemAAFontSettings = true;
/*      */     }
/* 1839 */     return useSystemAAFontSettings;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected RenderingHints getDesktopAAHints()
/*      */   {
/* 1847 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static RenderingHints getDesktopFontHints()
/*      */   {
/* 1857 */     if (useSystemAAFontSettings()) {
/* 1858 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 1859 */       if ((localToolkit instanceof SunToolkit)) {
/* 1860 */         RenderingHints localRenderingHints = ((SunToolkit)localToolkit).getDesktopAAHints();
/* 1861 */         return (RenderingHints)localRenderingHints;
/*      */       }
/* 1863 */       return null;
/*      */     }
/* 1865 */     if (desktopFontHints != null)
/*      */     {
/*      */ 
/*      */ 
/* 1869 */       return (RenderingHints)desktopFontHints.clone();
/*      */     }
/* 1871 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static synchronized void consumeNextKeyTyped(java.awt.event.KeyEvent paramKeyEvent)
/*      */   {
/*      */     try
/*      */     {
/* 1884 */       AWTAccessor.getDefaultKeyboardFocusManagerAccessor().consumeNextKeyTyped(
/*      */       
/* 1886 */         (java.awt.DefaultKeyboardFocusManager)KeyboardFocusManager.getCurrentKeyboardFocusManager(), paramKeyEvent);
/*      */     }
/*      */     catch (ClassCastException localClassCastException) {
/* 1889 */       localClassCastException.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */   protected static void dumpPeers(PlatformLogger paramPlatformLogger) {
/* 1894 */     AWTAutoShutdown.getInstance().dumpPeers(paramPlatformLogger);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Window getContainingWindow(Component paramComponent)
/*      */   {
/* 1903 */     while ((paramComponent != null) && (!(paramComponent instanceof Window))) {
/* 1904 */       paramComponent = paramComponent.getParent();
/*      */     }
/* 1906 */     return (Window)paramComponent;
/*      */   }
/*      */   
/* 1909 */   private static Boolean sunAwtDisableMixing = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static synchronized boolean getSunAwtDisableMixing()
/*      */   {
/* 1916 */     if (sunAwtDisableMixing == null) {
/* 1917 */       sunAwtDisableMixing = (Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.disableMixing"));
/*      */     }
/*      */     
/* 1920 */     return sunAwtDisableMixing.booleanValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isNativeGTKAvailable()
/*      */   {
/* 1929 */     return false;
/*      */   }
/*      */   
/* 1932 */   private static final Object DEACTIVATION_TIMES_MAP_KEY = new Object();
/*      */   
/*      */   public synchronized void setWindowDeactivationTime(Window paramWindow, long paramLong) {
/* 1935 */     AppContext localAppContext = getAppContext(paramWindow);
/* 1936 */     WeakHashMap localWeakHashMap = (WeakHashMap)localAppContext.get(DEACTIVATION_TIMES_MAP_KEY);
/* 1937 */     if (localWeakHashMap == null) {
/* 1938 */       localWeakHashMap = new WeakHashMap();
/* 1939 */       localAppContext.put(DEACTIVATION_TIMES_MAP_KEY, localWeakHashMap);
/*      */     }
/* 1941 */     localWeakHashMap.put(paramWindow, Long.valueOf(paramLong));
/*      */   }
/*      */   
/*      */   public synchronized long getWindowDeactivationTime(Window paramWindow) {
/* 1945 */     AppContext localAppContext = getAppContext(paramWindow);
/* 1946 */     WeakHashMap localWeakHashMap = (WeakHashMap)localAppContext.get(DEACTIVATION_TIMES_MAP_KEY);
/* 1947 */     if (localWeakHashMap == null) {
/* 1948 */       return -1L;
/*      */     }
/* 1950 */     Long localLong = (Long)localWeakHashMap.get(paramWindow);
/* 1951 */     return localLong == null ? -1L : localLong.longValue();
/*      */   }
/*      */   
/*      */   public boolean isWindowOpacitySupported()
/*      */   {
/* 1956 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isWindowShapingSupported()
/*      */   {
/* 1961 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isWindowTranslucencySupported()
/*      */   {
/* 1966 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isTranslucencyCapable(java.awt.GraphicsConfiguration paramGraphicsConfiguration) {
/* 1970 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isSwingBackbufferTranslucencySupported()
/*      */   {
/* 1977 */     return false;
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
/*      */   public static boolean isContainingTopLevelOpaque(Component paramComponent)
/*      */   {
/* 1992 */     Window localWindow = getContainingWindow(paramComponent);
/* 1993 */     return (localWindow != null) && (localWindow.isOpaque());
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
/*      */   public static boolean isContainingTopLevelTranslucent(Component paramComponent)
/*      */   {
/* 2008 */     Window localWindow = getContainingWindow(paramComponent);
/* 2009 */     return (localWindow != null) && (localWindow.getOpacity() < 1.0F);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean needUpdateWindow()
/*      */   {
/* 2020 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getNumberOfButtons()
/*      */   {
/* 2027 */     return 3;
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
/*      */   public static boolean isInstanceOf(Object paramObject, String paramString)
/*      */   {
/* 2045 */     if (paramObject == null) return false;
/* 2046 */     if (paramString == null) { return false;
/*      */     }
/* 2048 */     return isInstanceOf(paramObject.getClass(), paramString);
/*      */   }
/*      */   
/*      */   private static boolean isInstanceOf(Class<?> paramClass, String paramString) {
/* 2052 */     if (paramClass == null) { return false;
/*      */     }
/* 2054 */     if (paramClass.getName().equals(paramString)) {
/* 2055 */       return true;
/*      */     }
/*      */     
/* 2058 */     for (Class localClass : paramClass.getInterfaces()) {
/* 2059 */       if (localClass.getName().equals(paramString)) {
/* 2060 */         return true;
/*      */       }
/*      */     }
/* 2063 */     return isInstanceOf(paramClass.getSuperclass(), paramString);
/*      */   }
/*      */   
/*      */   protected static LightweightFrame getLightweightFrame(Component paramComponent) {
/* 2067 */     for (; paramComponent != null; paramComponent = paramComponent.getParent()) {
/* 2068 */       if ((paramComponent instanceof LightweightFrame)) {
/* 2069 */         return (LightweightFrame)paramComponent;
/*      */       }
/* 2071 */       if ((paramComponent instanceof Window))
/*      */       {
/* 2073 */         return null;
/*      */       }
/*      */     }
/* 2076 */     return null;
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
/*      */   public static void setSystemGenerated(AWTEvent paramAWTEvent)
/*      */   {
/* 2090 */     AWTAccessor.getAWTEventAccessor().setSystemGenerated(paramAWTEvent);
/*      */   }
/*      */   
/*      */   public static boolean isSystemGenerated(AWTEvent paramAWTEvent) {
/* 2094 */     return AWTAccessor.getAWTEventAccessor().isSystemGenerated(paramAWTEvent);
/*      */   }
/*      */   
/*      */   public abstract java.awt.peer.WindowPeer createWindow(Window paramWindow)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract FramePeer createFrame(java.awt.Frame paramFrame)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract FramePeer createLightweightFrame(LightweightFrame paramLightweightFrame)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.DialogPeer createDialog(Dialog paramDialog)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.ButtonPeer createButton(Button paramButton)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.TextFieldPeer createTextField(TextField paramTextField)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.ChoicePeer createChoice(Choice paramChoice)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.LabelPeer createLabel(Label paramLabel)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.ListPeer createList(java.awt.List paramList)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.CheckboxPeer createCheckbox(Checkbox paramCheckbox)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.ScrollbarPeer createScrollbar(Scrollbar paramScrollbar)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.ScrollPanePeer createScrollPane(ScrollPane paramScrollPane)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.TextAreaPeer createTextArea(TextArea paramTextArea)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.FileDialogPeer createFileDialog(FileDialog paramFileDialog)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.MenuBarPeer createMenuBar(java.awt.MenuBar paramMenuBar)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.MenuPeer createMenu(java.awt.Menu paramMenu)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.PopupMenuPeer createPopupMenu(java.awt.PopupMenu paramPopupMenu)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.MenuItemPeer createMenuItem(java.awt.MenuItem paramMenuItem)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.peer.CheckboxMenuItemPeer createCheckboxMenuItem(java.awt.CheckboxMenuItem paramCheckboxMenuItem)
/*      */     throws HeadlessException;
/*      */   
/*      */   public abstract java.awt.dnd.peer.DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*      */     throws java.awt.dnd.InvalidDnDOperationException;
/*      */   
/*      */   public abstract java.awt.peer.TrayIconPeer createTrayIcon(java.awt.TrayIcon paramTrayIcon)
/*      */     throws HeadlessException, java.awt.AWTException;
/*      */   
/*      */   public abstract java.awt.peer.SystemTrayPeer createSystemTray(java.awt.SystemTray paramSystemTray);
/*      */   
/*      */   public abstract boolean isTraySupported();
/*      */   
/*      */   public abstract java.awt.peer.FontPeer getFontPeer(String paramString, int paramInt);
/*      */   
/*      */   public abstract java.awt.peer.RobotPeer createRobot(Robot paramRobot, java.awt.GraphicsDevice paramGraphicsDevice)
/*      */     throws java.awt.AWTException;
/*      */   
/*      */   public abstract java.awt.peer.KeyboardFocusManagerPeer getKeyboardFocusManagerPeer()
/*      */     throws HeadlessException;
/*      */   
/*      */   protected abstract int getScreenWidth();
/*      */   
/*      */   protected abstract int getScreenHeight();
/*      */   
/*      */   protected abstract boolean syncNativeQueue(long paramLong);
/*      */   
/*      */   public abstract void grab(Window paramWindow);
/*      */   
/*      */   public abstract void ungrab(Window paramWindow);
/*      */   
/*      */   public static native void closeSplashScreen();
/*      */   
/*      */   public abstract boolean isDesktopSupported();
/*      */   
/*      */   public static class InfiniteLoop
/*      */     extends RuntimeException
/*      */   {}
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\SunToolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
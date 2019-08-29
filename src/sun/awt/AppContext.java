/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.SystemTray;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.TrayIcon;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.InvocationEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyChangeSupport;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.function.Supplier;
/*     */ import sun.misc.JavaAWTAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.util.logging.PlatformLogger;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class AppContext
/*     */ {
/* 136 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.AppContext");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */   public static final Object EVENT_QUEUE_KEY = new StringBuffer("EventQueue");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 149 */   public static final Object EVENT_QUEUE_LOCK_KEY = new StringBuilder("EventQueue.Lock");
/* 150 */   public static final Object EVENT_QUEUE_COND_KEY = new StringBuilder("EventQueue.Condition");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 155 */   private static final Map<ThreadGroup, AppContext> threadGroup2appContext = Collections.synchronizedMap(new IdentityHashMap());
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
/* 170 */   private static volatile AppContext mainAppContext = null;
/*     */   
/*     */ 
/* 173 */   private static final Object getAppContextLock = new GetAppContextLock(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 181 */   private final Map<Object, Object> table = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final ThreadGroup threadGroup;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 193 */   private PropertyChangeSupport changeSupport = null;
/*     */   public static final String DISPOSED_PROPERTY_NAME = "disposed";
/*     */   public static final String GUI_DISPOSED = "guidisposed";
/*     */   
/*     */   private static enum State
/*     */   {
/* 199 */     VALID, 
/* 200 */     BEING_DISPOSED, 
/* 201 */     DISPOSED;
/*     */     
/*     */     private State() {} }
/* 204 */   private volatile State state = State.VALID;
/*     */   
/*     */   public boolean isDisposed() {
/* 207 */     return this.state == State.DISPOSED;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 217 */   private static final AtomicInteger numAppContexts = new AtomicInteger(0);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final ClassLoader contextClassLoader;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   AppContext(ThreadGroup paramThreadGroup)
/*     */   {
/* 239 */     numAppContexts.incrementAndGet();
/*     */     
/* 241 */     this.threadGroup = paramThreadGroup;
/* 242 */     threadGroup2appContext.put(paramThreadGroup, this);
/*     */     
/*     */ 
/* 245 */     this.contextClassLoader = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public ClassLoader run() {
/* 247 */         return Thread.currentThread().getContextClassLoader();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 252 */     }));
/* 253 */     ReentrantLock localReentrantLock = new ReentrantLock();
/* 254 */     put(EVENT_QUEUE_LOCK_KEY, localReentrantLock);
/* 255 */     Condition localCondition = localReentrantLock.newCondition();
/* 256 */     put(EVENT_QUEUE_COND_KEY, localCondition);
/*     */   }
/*     */   
/* 259 */   private static final ThreadLocal<AppContext> threadAppContext = new ThreadLocal();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final void initMainAppContext()
/*     */   {
/* 266 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 269 */         Object localObject = Thread.currentThread().getThreadGroup();
/* 270 */         ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent();
/* 271 */         while (localThreadGroup != null)
/*     */         {
/* 273 */           localObject = localThreadGroup;
/* 274 */           localThreadGroup = ((ThreadGroup)localObject).getParent();
/*     */         }
/*     */         
/* 277 */         AppContext.access$102(SunToolkit.createNewAppContext((ThreadGroup)localObject));
/* 278 */         return null;
/*     */       }
/*     */     });
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
/*     */   public static final AppContext getAppContext()
/*     */   {
/* 296 */     if ((numAppContexts.get() == 1) && (mainAppContext != null)) {
/* 297 */       return mainAppContext;
/*     */     }
/*     */     
/* 300 */     AppContext localAppContext = (AppContext)threadAppContext.get();
/*     */     
/* 302 */     if (null == localAppContext) {
/* 303 */       localAppContext = (AppContext)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */ 
/*     */ 
/*     */         public AppContext run()
/*     */         {
/*     */ 
/* 310 */           ThreadGroup localThreadGroup1 = Thread.currentThread().getThreadGroup();
/* 311 */           ThreadGroup localThreadGroup2 = localThreadGroup1;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 317 */           synchronized (AppContext.getAppContextLock) {
/* 318 */             if (AppContext.numAppContexts.get() == 0) {
/* 319 */               if ((System.getProperty("javaplugin.version") == null) && 
/* 320 */                 (System.getProperty("javawebstart.version") == null)) {
/* 321 */                 AppContext.access$400();
/* 322 */               } else if ((System.getProperty("javafx.version") != null) && 
/* 323 */                 (localThreadGroup2.getParent() != null))
/*     */               {
/* 325 */                 SunToolkit.createNewAppContext();
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 330 */           ??? = (AppContext)AppContext.threadGroup2appContext.get(localThreadGroup2);
/* 331 */           while (??? == null) {
/* 332 */             localThreadGroup2 = localThreadGroup2.getParent();
/* 333 */             if (localThreadGroup2 == null)
/*     */             {
/*     */ 
/* 336 */               localObject2 = System.getSecurityManager();
/* 337 */               if (localObject2 != null) {
/* 338 */                 ThreadGroup localThreadGroup3 = ((SecurityManager)localObject2).getThreadGroup();
/* 339 */                 if (localThreadGroup3 != null)
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 345 */                   return (AppContext)AppContext.threadGroup2appContext.get(localThreadGroup3);
/*     */                 }
/*     */               }
/* 348 */               return null;
/*     */             }
/* 350 */             ??? = (AppContext)AppContext.threadGroup2appContext.get(localThreadGroup2);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 356 */           for (Object localObject2 = localThreadGroup1; localObject2 != localThreadGroup2; localObject2 = ((ThreadGroup)localObject2).getParent()) {
/* 357 */             AppContext.threadGroup2appContext.put(localObject2, ???);
/*     */           }
/*     */           
/*     */ 
/* 361 */           AppContext.threadAppContext.set(???);
/*     */           
/* 363 */           return (AppContext)???;
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 368 */     return localAppContext;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final boolean isMainContext(AppContext paramAppContext)
/*     */   {
/* 379 */     return (paramAppContext != null) && (paramAppContext == mainAppContext);
/*     */   }
/*     */   
/*     */   private static final AppContext getExecutionAppContext() {
/* 383 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 384 */     if ((localSecurityManager != null) && ((localSecurityManager instanceof AWTSecurityManager)))
/*     */     {
/*     */ 
/* 387 */       AWTSecurityManager localAWTSecurityManager = (AWTSecurityManager)localSecurityManager;
/* 388 */       AppContext localAppContext = localAWTSecurityManager.getAppContext();
/* 389 */       return localAppContext;
/*     */     }
/* 391 */     return null;
/*     */   }
/*     */   
/* 394 */   private long DISPOSAL_TIMEOUT = 5000L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 400 */   private long THREAD_INTERRUPT_TIMEOUT = 1000L;
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
/*     */   public void dispose()
/*     */     throws IllegalThreadStateException
/*     */   {
/* 418 */     if (this.threadGroup.parentOf(Thread.currentThread().getThreadGroup())) {
/* 419 */       throw new IllegalThreadStateException("Current Thread is contained within AppContext to be disposed.");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 424 */     synchronized (this) {
/* 425 */       if (this.state != State.VALID) {
/* 426 */         return;
/*     */       }
/*     */       
/* 429 */       this.state = State.BEING_DISPOSED;
/*     */     }
/*     */     
/* 432 */     ??? = this.changeSupport;
/* 433 */     if (??? != null) {
/* 434 */       ((PropertyChangeSupport)???).firePropertyChange("disposed", false, true);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 440 */     final Object localObject2 = new Object();
/*     */     
/* 442 */     Object localObject3 = new Runnable() {
/*     */       public void run() {
/* 444 */         Window[] arrayOfWindow1 = Window.getOwnerlessWindows();
/* 445 */         for (Window localWindow : arrayOfWindow1) {
/*     */           try {
/* 447 */             localWindow.dispose();
/*     */           } catch (Throwable localThrowable) {
/* 449 */             AppContext.log.finer("exception occurred while disposing app context", localThrowable);
/*     */           }
/*     */         }
/* 452 */         AccessController.doPrivileged(new PrivilegedAction() {
/*     */           public Void run() {
/* 454 */             if ((!GraphicsEnvironment.isHeadless()) && (SystemTray.isSupported()))
/*     */             {
/* 456 */               SystemTray localSystemTray = SystemTray.getSystemTray();
/* 457 */               TrayIcon[] arrayOfTrayIcon1 = localSystemTray.getTrayIcons();
/* 458 */               for (TrayIcon localTrayIcon : arrayOfTrayIcon1) {
/* 459 */                 localSystemTray.remove(localTrayIcon);
/*     */               }
/*     */             }
/* 462 */             return null;
/*     */           }
/*     */         });
/*     */         
/* 466 */         if (Ljava/lang/Object; != null) {
/* 467 */           Ljava/lang/Object;.firePropertyChange("guidisposed", false, true);
/*     */         }
/* 469 */         synchronized (localObject2) {
/* 470 */           localObject2.notifyAll();
/*     */         }
/*     */       }
/*     */     };
/* 474 */     synchronized (localObject2) {
/* 475 */       SunToolkit.postEvent(this, new InvocationEvent(
/* 476 */         Toolkit.getDefaultToolkit(), (Runnable)localObject3));
/*     */       try {
/* 478 */         localObject2.wait(this.DISPOSAL_TIMEOUT);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException1) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 486 */     localObject3 = new Runnable() {
/* 487 */       public void run() { synchronized (localObject2) {
/* 488 */           localObject2.notifyAll();
/*     */         }
/*     */       } };
/* 491 */     synchronized (localObject2) {
/* 492 */       SunToolkit.postEvent(this, new InvocationEvent(
/* 493 */         Toolkit.getDefaultToolkit(), (Runnable)localObject3));
/*     */       try {
/* 495 */         localObject2.wait(this.DISPOSAL_TIMEOUT);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException2) {}
/*     */     }
/*     */     
/* 500 */     synchronized (this) {
/* 501 */       this.state = State.DISPOSED;
/*     */     }
/*     */     
/*     */ 
/* 505 */     this.threadGroup.interrupt();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 513 */     long l1 = System.currentTimeMillis();
/* 514 */     long l2 = l1 + this.THREAD_INTERRUPT_TIMEOUT;
/* 515 */     while ((this.threadGroup.activeCount() > 0) && 
/* 516 */       (System.currentTimeMillis() < l2)) {
/*     */       try {
/* 518 */         Thread.sleep(10L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException3) {}
/*     */     }
/*     */     
/* 523 */     this.threadGroup.stop();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 528 */     l1 = System.currentTimeMillis();
/* 529 */     l2 = l1 + this.THREAD_INTERRUPT_TIMEOUT;
/* 530 */     while ((this.threadGroup.activeCount() > 0) && 
/* 531 */       (System.currentTimeMillis() < l2)) {
/*     */       try {
/* 533 */         Thread.sleep(10L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException4) {}
/*     */     }
/*     */     
/* 538 */     int i = this.threadGroup.activeGroupCount();
/* 539 */     if (i > 0) {
/* 540 */       ThreadGroup[] arrayOfThreadGroup = new ThreadGroup[i];
/* 541 */       i = this.threadGroup.enumerate(arrayOfThreadGroup);
/* 542 */       for (int j = 0; j < i; j++) {
/* 543 */         threadGroup2appContext.remove(arrayOfThreadGroup[j]);
/*     */       }
/*     */     }
/* 546 */     threadGroup2appContext.remove(this.threadGroup);
/*     */     
/* 548 */     threadAppContext.set(null);
/*     */     
/*     */     try
/*     */     {
/* 552 */       this.threadGroup.destroy();
/*     */     }
/*     */     catch (IllegalThreadStateException localIllegalThreadStateException) {}
/*     */     
/*     */ 
/* 557 */     synchronized (this.table) {
/* 558 */       this.table.clear();
/*     */     }
/*     */     
/* 561 */     numAppContexts.decrementAndGet();
/*     */     
/* 563 */     this.mostRecentKeyValue = null;
/*     */   }
/*     */   
/*     */   static final class PostShutdownEventRunnable implements Runnable {
/*     */     private final AppContext appContext;
/*     */     
/*     */     public PostShutdownEventRunnable(AppContext paramAppContext) {
/* 570 */       this.appContext = paramAppContext;
/*     */     }
/*     */     
/*     */     public void run() {
/* 574 */       EventQueue localEventQueue = (EventQueue)this.appContext.get(AppContext.EVENT_QUEUE_KEY);
/* 575 */       if (localEventQueue != null)
/* 576 */         localEventQueue.postEvent(AWTAutoShutdown.getShutdownEvent());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class GetAppContextLock {}
/*     */   
/*     */   static final class CreateThreadAction implements PrivilegedAction<Thread> {
/*     */     private final AppContext appContext;
/*     */     private final Runnable runnable;
/*     */     
/* 586 */     public CreateThreadAction(AppContext paramAppContext, Runnable paramRunnable) { this.appContext = paramAppContext;
/* 587 */       this.runnable = paramRunnable;
/*     */     }
/*     */     
/*     */     public Thread run() {
/* 591 */       Thread localThread = new Thread(this.appContext.getThreadGroup(), this.runnable);
/* 592 */       localThread.setContextClassLoader(this.appContext.getContextClassLoader());
/* 593 */       localThread.setPriority(6);
/* 594 */       localThread.setDaemon(true);
/* 595 */       return localThread;
/*     */     }
/*     */   }
/*     */   
/*     */   static void stopEventDispatchThreads() {
/* 600 */     for (AppContext localAppContext : )
/* 601 */       if (!localAppContext.isDisposed())
/*     */       {
/*     */ 
/* 604 */         PostShutdownEventRunnable localPostShutdownEventRunnable = new PostShutdownEventRunnable(localAppContext);
/*     */         
/*     */ 
/* 607 */         if (localAppContext != getAppContext())
/*     */         {
/*     */ 
/* 610 */           CreateThreadAction localCreateThreadAction = new CreateThreadAction(localAppContext, localPostShutdownEventRunnable);
/* 611 */           Thread localThread = (Thread)AccessController.doPrivileged(localCreateThreadAction);
/* 612 */           localThread.start();
/*     */         } else {
/* 614 */           localPostShutdownEventRunnable.run();
/*     */         }
/*     */       }
/*     */   }
/*     */   
/* 619 */   private MostRecentKeyValue mostRecentKeyValue = null;
/* 620 */   private MostRecentKeyValue shadowMostRecentKeyValue = null;
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
/*     */   public Object get(Object paramObject)
/*     */   {
/* 637 */     synchronized (this.table)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 644 */       MostRecentKeyValue localMostRecentKeyValue1 = this.mostRecentKeyValue;
/* 645 */       if ((localMostRecentKeyValue1 != null) && (localMostRecentKeyValue1.key == paramObject)) {
/* 646 */         return localMostRecentKeyValue1.value;
/*     */       }
/*     */       
/* 649 */       Object localObject1 = this.table.get(paramObject);
/* 650 */       if (this.mostRecentKeyValue == null) {
/* 651 */         this.mostRecentKeyValue = new MostRecentKeyValue(paramObject, localObject1);
/* 652 */         this.shadowMostRecentKeyValue = new MostRecentKeyValue(paramObject, localObject1);
/*     */       } else {
/* 654 */         MostRecentKeyValue localMostRecentKeyValue2 = this.mostRecentKeyValue;
/* 655 */         this.shadowMostRecentKeyValue.setPair(paramObject, localObject1);
/* 656 */         this.mostRecentKeyValue = this.shadowMostRecentKeyValue;
/* 657 */         this.shadowMostRecentKeyValue = localMostRecentKeyValue2;
/*     */       }
/* 659 */       return localObject1;
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
/*     */   public Object put(Object paramObject1, Object paramObject2)
/*     */   {
/* 681 */     synchronized (this.table) {
/* 682 */       MostRecentKeyValue localMostRecentKeyValue = this.mostRecentKeyValue;
/* 683 */       if ((localMostRecentKeyValue != null) && (localMostRecentKeyValue.key == paramObject1))
/* 684 */         localMostRecentKeyValue.value = paramObject2;
/* 685 */       return this.table.put(paramObject1, paramObject2);
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
/*     */   public Object remove(Object paramObject)
/*     */   {
/* 700 */     synchronized (this.table) {
/* 701 */       MostRecentKeyValue localMostRecentKeyValue = this.mostRecentKeyValue;
/* 702 */       if ((localMostRecentKeyValue != null) && (localMostRecentKeyValue.key == paramObject))
/* 703 */         localMostRecentKeyValue.value = null;
/* 704 */       return this.table.remove(paramObject);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ThreadGroup getThreadGroup()
/*     */   {
/* 714 */     return this.threadGroup;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ClassLoader getContextClassLoader()
/*     */   {
/* 724 */     return this.contextClassLoader;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 733 */     return getClass().getName() + "[threadGroup=" + this.threadGroup.getName() + "]";
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
/*     */   public synchronized PropertyChangeListener[] getPropertyChangeListeners()
/*     */   {
/* 751 */     if (this.changeSupport == null) {
/* 752 */       return new PropertyChangeListener[0];
/*     */     }
/* 754 */     return this.changeSupport.getPropertyChangeListeners();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 783 */     if (paramPropertyChangeListener == null) {
/* 784 */       return;
/*     */     }
/* 786 */     if (this.changeSupport == null) {
/* 787 */       this.changeSupport = new PropertyChangeSupport(this);
/*     */     }
/* 789 */     this.changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
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
/*     */   public synchronized void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 809 */     if ((paramPropertyChangeListener == null) || (this.changeSupport == null)) {
/* 810 */       return;
/*     */     }
/* 812 */     this.changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
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
/*     */   public synchronized PropertyChangeListener[] getPropertyChangeListeners(String paramString)
/*     */   {
/* 830 */     if (this.changeSupport == null) {
/* 831 */       return new PropertyChangeListener[0];
/*     */     }
/* 833 */     return this.changeSupport.getPropertyChangeListeners(paramString);
/*     */   }
/*     */   
/*     */   static
/*     */   {
/* 838 */     SharedSecrets.setJavaAWTAccess(new JavaAWTAccess() {
/*     */       private boolean hasRootThreadGroup(final AppContext paramAnonymousAppContext) {
/* 840 */         ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Boolean run() {
/* 843 */             return Boolean.valueOf(paramAnonymousAppContext.threadGroup.getParent() == null);
/*     */           }
/*     */         })).booleanValue();
/*     */       }
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
/*     */       public Object getAppletContext()
/*     */       {
/* 863 */         if (AppContext.numAppContexts.get() == 0) { return null;
/*     */         }
/*     */         
/* 866 */         AppContext localAppContext = AppContext.access$900();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 876 */         if (AppContext.numAppContexts.get() > 0)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 883 */           localAppContext = localAppContext != null ? localAppContext : AppContext.getAppContext();
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
/* 895 */         int i = (localAppContext == null) || (AppContext.mainAppContext == localAppContext) || ((AppContext.mainAppContext == null) && (hasRootThreadGroup(localAppContext))) ? 1 : 0;
/*     */         
/* 897 */         return i != 0 ? null : localAppContext;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <T> T getSoftReferenceValue(Object paramObject, Supplier<T> paramSupplier)
/*     */   {
/* 906 */     AppContext localAppContext = getAppContext();
/* 907 */     SoftReference localSoftReference = (SoftReference)localAppContext.get(paramObject);
/* 908 */     if (localSoftReference != null) {
/* 909 */       localObject = localSoftReference.get();
/* 910 */       if (localObject != null) {
/* 911 */         return (T)localObject;
/*     */       }
/*     */     }
/* 914 */     Object localObject = paramSupplier.get();
/* 915 */     localSoftReference = new SoftReference(localObject);
/* 916 */     localAppContext.put(paramObject, localSoftReference);
/* 917 */     return (T)localObject;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static java.util.Set<AppContext> getAppContexts()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 386	sun/awt/AppContext:threadGroup2appContext	Ljava/util/Map;
/*     */     //   3: dup
/*     */     //   4: astore_0
/*     */     //   5: monitorenter
/*     */     //   6: new 209	java/util/HashSet
/*     */     //   9: dup
/*     */     //   10: getstatic 386	sun/awt/AppContext:threadGroup2appContext	Ljava/util/Map;
/*     */     //   13: invokeinterface 471 1 0
/*     */     //   18: invokespecial 438	java/util/HashSet:<init>	(Ljava/util/Collection;)V
/*     */     //   21: aload_0
/*     */     //   22: monitorexit
/*     */     //   23: areturn
/*     */     //   24: astore_1
/*     */     //   25: aload_0
/*     */     //   26: monitorexit
/*     */     //   27: aload_1
/*     */     //   28: athrow
/*     */     // Line number table:
/*     */     //   Java source line #161	-> byte code offset #0
/*     */     //   Java source line #162	-> byte code offset #6
/*     */     //   Java source line #163	-> byte code offset #24
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   4	22	0	Ljava/lang/Object;	Object
/*     */     //   24	4	1	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   6	23	24	finally
/*     */     //   24	27	24	finally
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\AppContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.awt.im;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.InvocationEvent;
/*     */ import java.awt.im.spi.InputMethodDescriptor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.Vector;
/*     */ import java.util.prefs.BackingStoreException;
/*     */ import java.util.prefs.Preferences;
/*     */ import sun.awt.AppContext;
/*     */ import sun.awt.InputMethodSupport;
/*     */ import sun.awt.SunToolkit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ExecutableInputMethodManager
/*     */   extends InputMethodManager
/*     */   implements Runnable
/*     */ {
/*     */   private InputContext currentInputContext;
/*     */   private String triggerMenuString;
/*     */   private InputMethodPopupMenu selectionMenu;
/*     */   private static String selectInputMethodMenuTitle;
/*     */   private InputMethodLocator hostAdapterLocator;
/*     */   private int javaInputMethodCount;
/*     */   private Vector<InputMethodLocator> javaInputMethodLocatorList;
/*     */   private Component requestComponent;
/*     */   private InputContext requestInputContext;
/*     */   private static final String preferredIMNode = "/sun/awt/im/preferredInputMethod";
/*     */   private static final String descriptorKey = "descriptor";
/*  99 */   private Hashtable<String, InputMethodLocator> preferredLocatorCache = new Hashtable();
/*     */   
/*     */   private Preferences userRoot;
/*     */   
/*     */   ExecutableInputMethodManager()
/*     */   {
/* 105 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*     */     try {
/* 107 */       if ((localToolkit instanceof InputMethodSupport))
/*     */       {
/*     */ 
/* 110 */         InputMethodDescriptor localInputMethodDescriptor = ((InputMethodSupport)localToolkit).getInputMethodAdapterDescriptor();
/* 111 */         if (localInputMethodDescriptor != null) {
/* 112 */           this.hostAdapterLocator = new InputMethodLocator(localInputMethodDescriptor, null, null);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (AWTException localAWTException) {}
/*     */     
/*     */ 
/* 119 */     this.javaInputMethodLocatorList = new Vector();
/* 120 */     initializeInputMethodLocatorList();
/*     */   }
/*     */   
/*     */   synchronized void initialize() {
/* 124 */     selectInputMethodMenuTitle = Toolkit.getProperty("AWT.InputMethodSelectionMenu", "Select Input Method");
/*     */     
/* 126 */     this.triggerMenuString = selectInputMethodMenuTitle;
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/* 131 */     while (!hasMultipleInputMethods()) {
/*     */       try {
/* 133 */         synchronized (this) {
/* 134 */           wait();
/*     */         }
/*     */       }
/*     */       catch (InterruptedException localInterruptedException1) {}
/*     */     }
/*     */     
/*     */     for (;;)
/*     */     {
/* 142 */       waitForChangeRequest();
/* 143 */       initializeInputMethodLocatorList();
/*     */       try {
/* 145 */         if (this.requestComponent != null) {
/* 146 */           showInputMethodMenuOnRequesterEDT(this.requestComponent);
/*     */         }
/*     */         else {
/* 149 */           EventQueue.invokeAndWait(new Runnable() {
/*     */             public void run() {
/* 151 */               ExecutableInputMethodManager.this.showInputMethodMenu();
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       catch (InterruptedException localInterruptedException2) {}catch (InvocationTargetException localInvocationTargetException) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void showInputMethodMenuOnRequesterEDT(Component paramComponent)
/*     */     throws InterruptedException, InvocationTargetException
/*     */   {
/* 167 */     if (paramComponent == null) {
/* 168 */       return;
/*     */     }
/*     */     
/*     */ 
/* 172 */     Object local1AWTInvocationLock = new Object() {};
/* 174 */     InvocationEvent localInvocationEvent = new InvocationEvent(paramComponent, new Runnable()
/*     */     {
/*     */ 
/*     */       public void run() {
/* 178 */         ExecutableInputMethodManager.this.showInputMethodMenu(); } }, local1AWTInvocationLock, true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 184 */     AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
/* 185 */     synchronized (local1AWTInvocationLock) {
/* 186 */       SunToolkit.postEvent(localAppContext, localInvocationEvent);
/* 187 */       while (!localInvocationEvent.isDispatched()) {
/* 188 */         local1AWTInvocationLock.wait();
/*     */       }
/*     */     }
/*     */     
/* 192 */     ??? = localInvocationEvent.getThrowable();
/* 193 */     if (??? != null) {
/* 194 */       throw new InvocationTargetException((Throwable)???);
/*     */     }
/*     */   }
/*     */   
/*     */   void setInputContext(InputContext paramInputContext) {
/* 199 */     if ((this.currentInputContext != null) && (paramInputContext != null)) {}
/*     */     
/*     */ 
/*     */ 
/* 203 */     this.currentInputContext = paramInputContext;
/*     */   }
/*     */   
/*     */   public synchronized void notifyChangeRequest(Component paramComponent) {
/* 207 */     if ((!(paramComponent instanceof Frame)) && (!(paramComponent instanceof Dialog))) {
/* 208 */       return;
/*     */     }
/*     */     
/* 211 */     if (this.requestComponent != null) {
/* 212 */       return;
/*     */     }
/* 214 */     this.requestComponent = paramComponent;
/* 215 */     notify();
/*     */   }
/*     */   
/*     */   public synchronized void notifyChangeRequestByHotKey(Component paramComponent) {
/* 219 */     while ((!(paramComponent instanceof Frame)) && (!(paramComponent instanceof Dialog))) {
/* 220 */       if (paramComponent == null)
/*     */       {
/* 222 */         return;
/*     */       }
/* 224 */       paramComponent = paramComponent.getParent();
/*     */     }
/*     */     
/* 227 */     notifyChangeRequest(paramComponent);
/*     */   }
/*     */   
/*     */   public String getTriggerMenuString() {
/* 231 */     return this.triggerMenuString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   boolean hasMultipleInputMethods()
/*     */   {
/* 238 */     return ((this.hostAdapterLocator != null) && (this.javaInputMethodCount > 0)) || (this.javaInputMethodCount > 1);
/*     */   }
/*     */   
/*     */   private synchronized void waitForChangeRequest()
/*     */   {
/*     */     try {
/* 244 */       while (this.requestComponent == null) {
/* 245 */         wait();
/*     */       }
/*     */     }
/*     */     catch (InterruptedException localInterruptedException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initializeInputMethodLocatorList()
/*     */   {
/* 256 */     synchronized (this.javaInputMethodLocatorList) {
/* 257 */       this.javaInputMethodLocatorList.clear();
/*     */       try {
/* 259 */         AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */         {
/*     */           public Object run() {
/* 262 */             for (InputMethodDescriptor localInputMethodDescriptor : ServiceLoader.loadInstalled(InputMethodDescriptor.class)) {
/* 263 */               ClassLoader localClassLoader = localInputMethodDescriptor.getClass().getClassLoader();
/* 264 */               ExecutableInputMethodManager.this.javaInputMethodLocatorList.add(new InputMethodLocator(localInputMethodDescriptor, localClassLoader, null));
/*     */             }
/* 266 */             return null;
/*     */           }
/*     */         });
/*     */       } catch (PrivilegedActionException localPrivilegedActionException) {
/* 270 */         localPrivilegedActionException.printStackTrace();
/*     */       }
/* 272 */       this.javaInputMethodCount = this.javaInputMethodLocatorList.size();
/*     */     }
/*     */     
/* 275 */     if (hasMultipleInputMethods())
/*     */     {
/* 277 */       if (this.userRoot == null) {
/* 278 */         this.userRoot = getUserRoot();
/*     */       }
/*     */     }
/*     */     else {
/* 282 */       this.triggerMenuString = null;
/*     */     }
/*     */   }
/*     */   
/*     */   private void showInputMethodMenu()
/*     */   {
/* 288 */     if (!hasMultipleInputMethods()) {
/* 289 */       this.requestComponent = null;
/* 290 */       return;
/*     */     }
/*     */     
/*     */ 
/* 294 */     this.selectionMenu = InputMethodPopupMenu.getInstance(this.requestComponent, selectInputMethodMenuTitle);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 299 */     this.selectionMenu.removeAll();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 304 */     String str = getCurrentSelection();
/*     */     
/*     */ 
/* 307 */     if (this.hostAdapterLocator != null) {
/* 308 */       this.selectionMenu.addOneInputMethodToMenu(this.hostAdapterLocator, str);
/* 309 */       this.selectionMenu.addSeparator();
/*     */     }
/*     */     
/*     */ 
/* 313 */     for (int i = 0; i < this.javaInputMethodLocatorList.size(); i++) {
/* 314 */       InputMethodLocator localInputMethodLocator = (InputMethodLocator)this.javaInputMethodLocatorList.get(i);
/* 315 */       this.selectionMenu.addOneInputMethodToMenu(localInputMethodLocator, str);
/*     */     }
/*     */     
/* 318 */     synchronized (this) {
/* 319 */       this.selectionMenu.addToComponent(this.requestComponent);
/* 320 */       this.requestInputContext = this.currentInputContext;
/* 321 */       this.selectionMenu.show(this.requestComponent, 60, 80);
/* 322 */       this.requestComponent = null;
/*     */     }
/*     */   }
/*     */   
/*     */   private String getCurrentSelection() {
/* 327 */     InputContext localInputContext = this.currentInputContext;
/* 328 */     if (localInputContext != null) {
/* 329 */       InputMethodLocator localInputMethodLocator = localInputContext.getInputMethodLocator();
/* 330 */       if (localInputMethodLocator != null) {
/* 331 */         return localInputMethodLocator.getActionCommandString();
/*     */       }
/*     */     }
/* 334 */     return null;
/*     */   }
/*     */   
/*     */   synchronized void changeInputMethod(String paramString) {
/* 338 */     Object localObject1 = null;
/*     */     
/* 340 */     String str1 = paramString;
/* 341 */     String str2 = null;
/* 342 */     int i = paramString.indexOf('\n');
/* 343 */     if (i != -1) {
/* 344 */       str2 = paramString.substring(i + 1);
/* 345 */       str1 = paramString.substring(0, i); }
/*     */     Object localObject2;
/* 347 */     String str4; if (this.hostAdapterLocator.getActionCommandString().equals(str1)) {
/* 348 */       localObject1 = this.hostAdapterLocator;
/*     */     } else {
/* 350 */       for (int j = 0; j < this.javaInputMethodLocatorList.size(); j++) {
/* 351 */         localObject2 = (InputMethodLocator)this.javaInputMethodLocatorList.get(j);
/* 352 */         str4 = ((InputMethodLocator)localObject2).getActionCommandString();
/* 353 */         if (str4.equals(str1)) {
/* 354 */           localObject1 = localObject2;
/* 355 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 360 */     if ((localObject1 != null) && (str2 != null)) {
/* 361 */       String str3 = "";localObject2 = "";str4 = "";
/* 362 */       int k = str2.indexOf('_');
/* 363 */       if (k == -1) {
/* 364 */         str3 = str2;
/*     */       } else {
/* 366 */         str3 = str2.substring(0, k);
/* 367 */         int m = k + 1;
/* 368 */         k = str2.indexOf('_', m);
/* 369 */         if (k == -1) {
/* 370 */           localObject2 = str2.substring(m);
/*     */         } else {
/* 372 */           localObject2 = str2.substring(m, k);
/* 373 */           str4 = str2.substring(k + 1);
/*     */         }
/*     */       }
/* 376 */       Locale localLocale = new Locale(str3, (String)localObject2, str4);
/* 377 */       localObject1 = ((InputMethodLocator)localObject1).deriveLocator(localLocale);
/*     */     }
/*     */     
/* 380 */     if (localObject1 == null) {
/* 381 */       return;
/*     */     }
/*     */     
/* 384 */     if (this.requestInputContext != null) {
/* 385 */       this.requestInputContext.changeInputMethod((InputMethodLocator)localObject1);
/* 386 */       this.requestInputContext = null;
/*     */       
/*     */ 
/* 389 */       putPreferredInputMethod((InputMethodLocator)localObject1);
/*     */     }
/*     */   }
/*     */   
/*     */   InputMethodLocator findInputMethod(Locale paramLocale)
/*     */   {
/* 395 */     InputMethodLocator localInputMethodLocator1 = getPreferredInputMethod(paramLocale);
/* 396 */     if (localInputMethodLocator1 != null) {
/* 397 */       return localInputMethodLocator1;
/*     */     }
/*     */     
/* 400 */     if ((this.hostAdapterLocator != null) && (this.hostAdapterLocator.isLocaleAvailable(paramLocale))) {
/* 401 */       return this.hostAdapterLocator.deriveLocator(paramLocale);
/*     */     }
/*     */     
/*     */ 
/* 405 */     initializeInputMethodLocatorList();
/*     */     
/* 407 */     for (int i = 0; i < this.javaInputMethodLocatorList.size(); i++) {
/* 408 */       InputMethodLocator localInputMethodLocator2 = (InputMethodLocator)this.javaInputMethodLocatorList.get(i);
/* 409 */       if (localInputMethodLocator2.isLocaleAvailable(paramLocale)) {
/* 410 */         return localInputMethodLocator2.deriveLocator(paramLocale);
/*     */       }
/*     */     }
/* 413 */     return null;
/*     */   }
/*     */   
/*     */   Locale getDefaultKeyboardLocale() {
/* 417 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 418 */     if ((localToolkit instanceof InputMethodSupport)) {
/* 419 */       return ((InputMethodSupport)localToolkit).getDefaultKeyboardLocale();
/*     */     }
/* 421 */     return Locale.getDefault();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized InputMethodLocator getPreferredInputMethod(Locale paramLocale)
/*     */   {
/* 432 */     InputMethodLocator localInputMethodLocator1 = null;
/*     */     
/* 434 */     if (!hasMultipleInputMethods())
/*     */     {
/* 436 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 440 */     localInputMethodLocator1 = (InputMethodLocator)this.preferredLocatorCache.get(paramLocale.toString().intern());
/* 441 */     if (localInputMethodLocator1 != null) {
/* 442 */       return localInputMethodLocator1;
/*     */     }
/*     */     
/*     */ 
/* 446 */     String str1 = findPreferredInputMethodNode(paramLocale);
/* 447 */     String str2 = readPreferredInputMethod(str1);
/*     */     
/*     */ 
/*     */ 
/* 451 */     if (str2 != null) {
/*     */       Locale localLocale;
/* 453 */       if ((this.hostAdapterLocator != null) && 
/* 454 */         (this.hostAdapterLocator.getDescriptor().getClass().getName().equals(str2))) {
/* 455 */         localLocale = getAdvertisedLocale(this.hostAdapterLocator, paramLocale);
/* 456 */         if (localLocale != null) {
/* 457 */           localInputMethodLocator1 = this.hostAdapterLocator.deriveLocator(localLocale);
/* 458 */           this.preferredLocatorCache.put(paramLocale.toString().intern(), localInputMethodLocator1);
/*     */         }
/* 460 */         return localInputMethodLocator1;
/*     */       }
/*     */       
/* 463 */       for (int i = 0; i < this.javaInputMethodLocatorList.size(); i++) {
/* 464 */         InputMethodLocator localInputMethodLocator2 = (InputMethodLocator)this.javaInputMethodLocatorList.get(i);
/* 465 */         InputMethodDescriptor localInputMethodDescriptor = localInputMethodLocator2.getDescriptor();
/* 466 */         if (localInputMethodDescriptor.getClass().getName().equals(str2)) {
/* 467 */           localLocale = getAdvertisedLocale(localInputMethodLocator2, paramLocale);
/* 468 */           if (localLocale != null) {
/* 469 */             localInputMethodLocator1 = localInputMethodLocator2.deriveLocator(localLocale);
/* 470 */             this.preferredLocatorCache.put(paramLocale.toString().intern(), localInputMethodLocator1);
/*     */           }
/* 472 */           return localInputMethodLocator1;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 477 */       writePreferredInputMethod(str1, null);
/*     */     }
/*     */     
/* 480 */     return null;
/*     */   }
/*     */   
/*     */   private String findPreferredInputMethodNode(Locale paramLocale) {
/* 484 */     if (this.userRoot == null) {
/* 485 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 489 */     String str = "/sun/awt/im/preferredInputMethod/" + createLocalePath(paramLocale);
/*     */     
/*     */ 
/* 492 */     while (!str.equals("/sun/awt/im/preferredInputMethod")) {
/*     */       try {
/* 494 */         if ((this.userRoot.nodeExists(str)) && 
/* 495 */           (readPreferredInputMethod(str) != null)) {
/* 496 */           return str;
/*     */         }
/*     */       }
/*     */       catch (BackingStoreException localBackingStoreException) {}
/*     */       
/*     */ 
/*     */ 
/* 503 */       str = str.substring(0, str.lastIndexOf('/'));
/*     */     }
/*     */     
/* 506 */     return null;
/*     */   }
/*     */   
/*     */   private String readPreferredInputMethod(String paramString) {
/* 510 */     if ((this.userRoot == null) || (paramString == null)) {
/* 511 */       return null;
/*     */     }
/*     */     
/* 514 */     return this.userRoot.node(paramString).get("descriptor", null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void putPreferredInputMethod(InputMethodLocator paramInputMethodLocator)
/*     */   {
/* 524 */     InputMethodDescriptor localInputMethodDescriptor = paramInputMethodLocator.getDescriptor();
/* 525 */     Locale localLocale = paramInputMethodLocator.getLocale();
/*     */     
/* 527 */     if (localLocale == null) {
/*     */       try
/*     */       {
/* 530 */         Locale[] arrayOfLocale = localInputMethodDescriptor.getAvailableLocales();
/* 531 */         if (arrayOfLocale.length == 1) {
/* 532 */           localLocale = arrayOfLocale[0];
/*     */         }
/*     */         else {
/* 535 */           return;
/*     */         }
/*     */       }
/*     */       catch (AWTException localAWTException) {
/* 539 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 545 */     if (localLocale.equals(Locale.JAPAN)) {
/* 546 */       localLocale = Locale.JAPANESE;
/*     */     }
/* 548 */     if (localLocale.equals(Locale.KOREA)) {
/* 549 */       localLocale = Locale.KOREAN;
/*     */     }
/* 551 */     if (localLocale.equals(new Locale("th", "TH"))) {
/* 552 */       localLocale = new Locale("th");
/*     */     }
/*     */     
/*     */ 
/* 556 */     String str = "/sun/awt/im/preferredInputMethod/" + createLocalePath(localLocale);
/*     */     
/*     */ 
/* 559 */     writePreferredInputMethod(str, localInputMethodDescriptor.getClass().getName());
/* 560 */     this.preferredLocatorCache.put(localLocale.toString().intern(), paramInputMethodLocator
/* 561 */       .deriveLocator(localLocale));
/*     */   }
/*     */   
/*     */ 
/*     */   private String createLocalePath(Locale paramLocale)
/*     */   {
/* 567 */     String str1 = paramLocale.getLanguage();
/* 568 */     String str2 = paramLocale.getCountry();
/* 569 */     String str3 = paramLocale.getVariant();
/* 570 */     String str4 = null;
/* 571 */     if (!str3.equals("")) {
/* 572 */       str4 = "_" + str1 + "/_" + str2 + "/_" + str3;
/* 573 */     } else if (!str2.equals("")) {
/* 574 */       str4 = "_" + str1 + "/_" + str2;
/*     */     } else {
/* 576 */       str4 = "_" + str1;
/*     */     }
/*     */     
/* 579 */     return str4;
/*     */   }
/*     */   
/*     */   private void writePreferredInputMethod(String paramString1, String paramString2) {
/* 583 */     if (this.userRoot != null) {
/* 584 */       Preferences localPreferences = this.userRoot.node(paramString1);
/*     */       
/*     */ 
/* 587 */       if (paramString2 != null) {
/* 588 */         localPreferences.put("descriptor", paramString2);
/*     */       } else {
/* 590 */         localPreferences.remove("descriptor");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private Preferences getUserRoot() {
/* 596 */     (Preferences)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Preferences run() {
/* 598 */         return Preferences.userRoot();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private Locale getAdvertisedLocale(InputMethodLocator paramInputMethodLocator, Locale paramLocale) {
/* 604 */     Locale localLocale = null;
/*     */     
/* 606 */     if (paramInputMethodLocator.isLocaleAvailable(paramLocale)) {
/* 607 */       localLocale = paramLocale;
/* 608 */     } else if (paramLocale.getLanguage().equals("ja"))
/*     */     {
/*     */ 
/* 611 */       if (paramInputMethodLocator.isLocaleAvailable(Locale.JAPAN)) {
/* 612 */         localLocale = Locale.JAPAN;
/* 613 */       } else if (paramInputMethodLocator.isLocaleAvailable(Locale.JAPANESE)) {
/* 614 */         localLocale = Locale.JAPANESE;
/*     */       }
/* 616 */     } else if (paramLocale.getLanguage().equals("ko")) {
/* 617 */       if (paramInputMethodLocator.isLocaleAvailable(Locale.KOREA)) {
/* 618 */         localLocale = Locale.KOREA;
/* 619 */       } else if (paramInputMethodLocator.isLocaleAvailable(Locale.KOREAN)) {
/* 620 */         localLocale = Locale.KOREAN;
/*     */       }
/* 622 */     } else if (paramLocale.getLanguage().equals("th")) {
/* 623 */       if (paramInputMethodLocator.isLocaleAvailable(new Locale("th", "TH"))) {
/* 624 */         localLocale = new Locale("th", "TH");
/* 625 */       } else if (paramInputMethodLocator.isLocaleAvailable(new Locale("th"))) {
/* 626 */         localLocale = new Locale("th");
/*     */       }
/*     */     }
/*     */     
/* 630 */     return localLocale;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\im\ExecutableInputMethodManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*      */ package sun.awt.im;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.AWTKeyStroke;
/*      */ import java.awt.Component;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.ComponentEvent;
/*      */ import java.awt.event.ComponentListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.InputEvent;
/*      */ import java.awt.event.InputMethodEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.event.WindowListener;
/*      */ import java.awt.im.InputMethodRequests;
/*      */ import java.awt.im.spi.InputMethod;
/*      */ import java.awt.im.spi.InputMethodDescriptor;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.prefs.BackingStoreException;
/*      */ import java.util.prefs.Preferences;
/*      */ import sun.awt.SunToolkit;
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
/*      */ public class InputContext
/*      */   extends java.awt.im.InputContext
/*      */   implements ComponentListener, WindowListener
/*      */ {
/*   70 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.im.InputContext");
/*      */   
/*      */ 
/*      */   private InputMethodLocator inputMethodLocator;
/*      */   
/*      */ 
/*      */   private InputMethod inputMethod;
/*      */   
/*      */ 
/*      */   private boolean inputMethodCreationFailed;
/*      */   
/*      */ 
/*      */   private HashMap<InputMethodLocator, InputMethod> usedInputMethods;
/*      */   
/*      */   private Component currentClientComponent;
/*      */   
/*      */   private Component awtFocussedComponent;
/*      */   
/*      */   private boolean isInputMethodActive;
/*      */   
/*   90 */   private Character.Subset[] characterSubsets = null;
/*      */   
/*      */ 
/*   93 */   private boolean compositionAreaHidden = false;
/*      */   
/*      */ 
/*      */ 
/*      */   private static InputContext inputMethodWindowContext;
/*      */   
/*      */ 
/*  100 */   private static InputMethod previousInputMethod = null;
/*      */   
/*      */ 
/*  103 */   private boolean clientWindowNotificationEnabled = false;
/*      */   
/*      */   private Window clientWindowListened;
/*      */   
/*  107 */   private Rectangle clientWindowLocation = null;
/*      */   
/*      */   private HashMap<InputMethod, Boolean> perInputMethodState;
/*      */   
/*      */   private static AWTKeyStroke inputMethodSelectionKey;
/*      */   
/*  113 */   private static boolean inputMethodSelectionKeyInitialized = false;
/*      */   
/*      */   private static final String inputMethodSelectionKeyPath = "/java/awt/im/selectionKey";
/*      */   
/*      */   private static final String inputMethodSelectionKeyCodeName = "keyCode";
/*      */   private static final String inputMethodSelectionKeyModifiersName = "modifiers";
/*      */   
/*      */   protected InputContext()
/*      */   {
/*  122 */     InputMethodManager localInputMethodManager = InputMethodManager.getInstance();
/*  123 */     synchronized (InputContext.class) {
/*  124 */       if (!inputMethodSelectionKeyInitialized) {
/*  125 */         inputMethodSelectionKeyInitialized = true;
/*  126 */         if (localInputMethodManager.hasMultipleInputMethods()) {
/*  127 */           initializeInputMethodSelectionKey();
/*      */         }
/*      */       }
/*      */     }
/*  131 */     selectInputMethod(localInputMethodManager.getDefaultKeyboardLocale());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized boolean selectInputMethod(Locale paramLocale)
/*      */   {
/*  139 */     if (paramLocale == null) {
/*  140 */       throw new NullPointerException();
/*      */     }
/*      */     
/*      */ 
/*  144 */     if (this.inputMethod != null) {
/*  145 */       if (this.inputMethod.setLocale(paramLocale)) {
/*  146 */         return true;
/*      */       }
/*  148 */     } else if (this.inputMethodLocator != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  153 */       if (this.inputMethodLocator.isLocaleAvailable(paramLocale)) {
/*  154 */         this.inputMethodLocator = this.inputMethodLocator.deriveLocator(paramLocale);
/*  155 */         return true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  160 */     InputMethodLocator localInputMethodLocator = InputMethodManager.getInstance().findInputMethod(paramLocale);
/*  161 */     if (localInputMethodLocator != null) {
/*  162 */       changeInputMethod(localInputMethodLocator);
/*  163 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  168 */     if ((this.inputMethod == null) && (this.inputMethodLocator != null)) {
/*  169 */       this.inputMethod = getInputMethod();
/*  170 */       if (this.inputMethod != null) {
/*  171 */         return this.inputMethod.setLocale(paramLocale);
/*      */       }
/*      */     }
/*  174 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  181 */     if (this.inputMethod != null)
/*  182 */       return this.inputMethod.getLocale();
/*  183 */     if (this.inputMethodLocator != null) {
/*  184 */       return this.inputMethodLocator.getLocale();
/*      */     }
/*  186 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCharacterSubsets(Character.Subset[] paramArrayOfSubset)
/*      */   {
/*  194 */     if (paramArrayOfSubset == null) {
/*  195 */       this.characterSubsets = null;
/*      */     } else {
/*  197 */       this.characterSubsets = new Character.Subset[paramArrayOfSubset.length];
/*  198 */       System.arraycopy(paramArrayOfSubset, 0, this.characterSubsets, 0, this.characterSubsets.length);
/*      */     }
/*      */     
/*  201 */     if (this.inputMethod != null) {
/*  202 */       this.inputMethod.setCharacterSubsets(paramArrayOfSubset);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void reconvert()
/*      */   {
/*  212 */     InputMethod localInputMethod = getInputMethod();
/*  213 */     if (localInputMethod == null) {
/*  214 */       throw new UnsupportedOperationException();
/*      */     }
/*  216 */     localInputMethod.reconvert();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void dispatchEvent(AWTEvent paramAWTEvent)
/*      */   {
/*  225 */     if ((paramAWTEvent instanceof InputMethodEvent)) {
/*  226 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  231 */     if ((paramAWTEvent instanceof FocusEvent)) {
/*  232 */       localObject = ((FocusEvent)paramAWTEvent).getOppositeComponent();
/*  233 */       if ((localObject != null) && 
/*  234 */         ((getComponentWindow((Component)localObject) instanceof InputMethodWindow)) && 
/*  235 */         (((Component)localObject).getInputContext() == this)) {
/*  236 */         return;
/*      */       }
/*      */     }
/*      */     
/*  240 */     Object localObject = getInputMethod();
/*  241 */     int i = paramAWTEvent.getID();
/*      */     
/*  243 */     switch (i) {
/*      */     case 1004: 
/*  245 */       focusGained((Component)paramAWTEvent.getSource());
/*  246 */       break;
/*      */     
/*      */     case 1005: 
/*  249 */       focusLost((Component)paramAWTEvent.getSource(), ((FocusEvent)paramAWTEvent).isTemporary());
/*  250 */       break;
/*      */     
/*      */     case 401: 
/*  253 */       if (checkInputMethodSelectionKey((KeyEvent)paramAWTEvent))
/*      */       {
/*  255 */         InputMethodManager.getInstance().notifyChangeRequestByHotKey((Component)paramAWTEvent.getSource()); }
/*  256 */       break;
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*  262 */     if ((localObject != null) && ((paramAWTEvent instanceof InputEvent))) {
/*  263 */       ((InputMethod)localObject).dispatchEvent(paramAWTEvent);
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
/*      */   private void focusGained(Component paramComponent)
/*      */   {
/*  299 */     synchronized (paramComponent.getTreeLock()) {
/*  300 */       synchronized (this) {
/*  301 */         if (!"sun.awt.im.CompositionArea".equals(paramComponent.getClass().getName()))
/*      */         {
/*  303 */           if (!(getComponentWindow(paramComponent) instanceof InputMethodWindow))
/*      */           {
/*      */ 
/*  306 */             if (!paramComponent.isDisplayable())
/*      */             {
/*  308 */               return;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  316 */             if ((this.inputMethod != null) && 
/*  317 */               (this.currentClientComponent != null) && (this.currentClientComponent != paramComponent)) {
/*  318 */               if (!this.isInputMethodActive) {
/*  319 */                 activateInputMethod(false);
/*      */               }
/*  321 */               endComposition();
/*  322 */               deactivateInputMethod(false);
/*      */             }
/*      */             
/*      */ 
/*  326 */             this.currentClientComponent = paramComponent;
/*      */           }
/*      */         }
/*  329 */         this.awtFocussedComponent = paramComponent;
/*  330 */         if ((this.inputMethod instanceof InputMethodAdapter)) {
/*  331 */           ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(paramComponent);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  337 */         if (!this.isInputMethodActive) {
/*  338 */           activateInputMethod(true);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  344 */         InputMethodContext localInputMethodContext = (InputMethodContext)this;
/*  345 */         if (!localInputMethodContext.isCompositionAreaVisible()) {
/*  346 */           InputMethodRequests localInputMethodRequests = paramComponent.getInputMethodRequests();
/*  347 */           if ((localInputMethodRequests != null) && (localInputMethodContext.useBelowTheSpotInput())) {
/*  348 */             localInputMethodContext.setCompositionAreaUndecorated(true);
/*      */           } else {
/*  350 */             localInputMethodContext.setCompositionAreaUndecorated(false);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  355 */         if (this.compositionAreaHidden == true) {
/*  356 */           ((InputMethodContext)this).setCompositionAreaVisible(true);
/*  357 */           this.compositionAreaHidden = false;
/*      */         }
/*      */       }
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
/*      */   private void activateInputMethod(boolean paramBoolean)
/*      */   {
/*  373 */     if ((inputMethodWindowContext != null) && (inputMethodWindowContext != this) && (inputMethodWindowContext.inputMethodLocator != null))
/*      */     {
/*  375 */       if ((!inputMethodWindowContext.inputMethodLocator.sameInputMethod(this.inputMethodLocator)) && (inputMethodWindowContext.inputMethod != null))
/*      */       {
/*  377 */         inputMethodWindowContext.inputMethod.hideWindows(); }
/*      */     }
/*  379 */     inputMethodWindowContext = this;
/*      */     
/*  381 */     if (this.inputMethod != null) {
/*  382 */       if ((previousInputMethod != this.inputMethod) && ((previousInputMethod instanceof InputMethodAdapter)))
/*      */       {
/*      */ 
/*      */ 
/*  386 */         ((InputMethodAdapter)previousInputMethod).stopListening();
/*      */       }
/*  388 */       previousInputMethod = null;
/*      */       
/*  390 */       if (log.isLoggable(PlatformLogger.Level.FINE)) {
/*  391 */         log.fine("Current client component " + this.currentClientComponent);
/*      */       }
/*  393 */       if ((this.inputMethod instanceof InputMethodAdapter)) {
/*  394 */         ((InputMethodAdapter)this.inputMethod).setClientComponent(this.currentClientComponent);
/*      */       }
/*  396 */       this.inputMethod.activate();
/*  397 */       this.isInputMethodActive = true;
/*      */       
/*  399 */       if (this.perInputMethodState != null) {
/*  400 */         Boolean localBoolean = (Boolean)this.perInputMethodState.remove(this.inputMethod);
/*  401 */         if (localBoolean != null) {
/*  402 */           this.clientWindowNotificationEnabled = localBoolean.booleanValue();
/*      */         }
/*      */       }
/*  405 */       if (this.clientWindowNotificationEnabled) {
/*  406 */         if (!addedClientWindowListeners()) {
/*  407 */           addClientWindowListeners();
/*      */         }
/*  409 */         synchronized (this) {
/*  410 */           if (this.clientWindowListened != null) {
/*  411 */             notifyClientWindowChange(this.clientWindowListened);
/*      */           }
/*      */         }
/*      */       }
/*  415 */       else if (addedClientWindowListeners()) {
/*  416 */         removeClientWindowListeners();
/*      */       }
/*      */     }
/*      */     
/*  420 */     InputMethodManager.getInstance().setInputContext(this);
/*      */     
/*  422 */     ((InputMethodContext)this).grabCompositionArea(paramBoolean);
/*      */   }
/*      */   
/*      */   static Window getComponentWindow(Component paramComponent) {
/*      */     for (;;) {
/*  427 */       if (paramComponent == null)
/*  428 */         return null;
/*  429 */       if ((paramComponent instanceof Window)) {
/*  430 */         return (Window)paramComponent;
/*      */       }
/*  432 */       paramComponent = paramComponent.getParent();
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
/*      */   private void focusLost(Component paramComponent, boolean paramBoolean)
/*      */   {
/*  452 */     synchronized (paramComponent.getTreeLock()) {
/*  453 */       synchronized (this)
/*      */       {
/*      */ 
/*      */ 
/*  457 */         if (this.isInputMethodActive) {
/*  458 */           deactivateInputMethod(paramBoolean);
/*      */         }
/*      */         
/*  461 */         this.awtFocussedComponent = null;
/*  462 */         if ((this.inputMethod instanceof InputMethodAdapter)) {
/*  463 */           ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(null);
/*      */         }
/*      */         
/*      */ 
/*  467 */         InputMethodContext localInputMethodContext = (InputMethodContext)this;
/*  468 */         if (localInputMethodContext.isCompositionAreaVisible()) {
/*  469 */           localInputMethodContext.setCompositionAreaVisible(false);
/*  470 */           this.compositionAreaHidden = true;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean checkInputMethodSelectionKey(KeyEvent paramKeyEvent)
/*      */   {
/*  480 */     if (inputMethodSelectionKey != null) {
/*  481 */       AWTKeyStroke localAWTKeyStroke = AWTKeyStroke.getAWTKeyStrokeForEvent(paramKeyEvent);
/*  482 */       return inputMethodSelectionKey.equals(localAWTKeyStroke);
/*      */     }
/*  484 */     return false;
/*      */   }
/*      */   
/*      */   private void deactivateInputMethod(boolean paramBoolean)
/*      */   {
/*  489 */     InputMethodManager.getInstance().setInputContext(null);
/*  490 */     if (this.inputMethod != null) {
/*  491 */       this.isInputMethodActive = false;
/*  492 */       this.inputMethod.deactivate(paramBoolean);
/*  493 */       previousInputMethod = this.inputMethod;
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
/*      */   synchronized void changeInputMethod(InputMethodLocator paramInputMethodLocator)
/*      */   {
/*  511 */     if (this.inputMethodLocator == null) {
/*  512 */       this.inputMethodLocator = paramInputMethodLocator;
/*  513 */       this.inputMethodCreationFailed = false;
/*  514 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  519 */     if (this.inputMethodLocator.sameInputMethod(paramInputMethodLocator)) {
/*  520 */       localLocale = paramInputMethodLocator.getLocale();
/*  521 */       if ((localLocale != null) && (this.inputMethodLocator.getLocale() != localLocale)) {
/*  522 */         if (this.inputMethod != null) {
/*  523 */           this.inputMethod.setLocale(localLocale);
/*      */         }
/*  525 */         this.inputMethodLocator = paramInputMethodLocator;
/*      */       }
/*  527 */       return;
/*      */     }
/*      */     
/*      */ 
/*  531 */     Locale localLocale = this.inputMethodLocator.getLocale();
/*  532 */     boolean bool1 = this.isInputMethodActive;
/*  533 */     int i = 0;
/*  534 */     boolean bool2 = false;
/*  535 */     if (this.inputMethod != null) {
/*      */       try {
/*  537 */         bool2 = this.inputMethod.isCompositionEnabled();
/*  538 */         i = 1;
/*      */       }
/*      */       catch (UnsupportedOperationException localUnsupportedOperationException1) {}
/*  541 */       if (this.currentClientComponent != null) {
/*  542 */         if (!this.isInputMethodActive) {
/*  543 */           activateInputMethod(false);
/*      */         }
/*  545 */         endComposition();
/*  546 */         deactivateInputMethod(false);
/*  547 */         if ((this.inputMethod instanceof InputMethodAdapter)) {
/*  548 */           ((InputMethodAdapter)this.inputMethod).setClientComponent(null);
/*      */         }
/*      */       }
/*  551 */       localLocale = this.inputMethod.getLocale();
/*      */       
/*      */ 
/*  554 */       if (this.usedInputMethods == null) {
/*  555 */         this.usedInputMethods = new HashMap(5);
/*      */       }
/*  557 */       if (this.perInputMethodState == null) {
/*  558 */         this.perInputMethodState = new HashMap(5);
/*      */       }
/*  560 */       this.usedInputMethods.put(this.inputMethodLocator.deriveLocator(null), this.inputMethod);
/*  561 */       this.perInputMethodState.put(this.inputMethod, 
/*  562 */         Boolean.valueOf(this.clientWindowNotificationEnabled));
/*  563 */       enableClientWindowNotification(this.inputMethod, false);
/*  564 */       if (this == inputMethodWindowContext) {
/*  565 */         this.inputMethod.hideWindows();
/*  566 */         inputMethodWindowContext = null;
/*      */       }
/*  568 */       this.inputMethodLocator = null;
/*  569 */       this.inputMethod = null;
/*  570 */       this.inputMethodCreationFailed = false;
/*      */     }
/*      */     
/*      */ 
/*  574 */     if ((paramInputMethodLocator.getLocale() == null) && (localLocale != null) && 
/*  575 */       (paramInputMethodLocator.isLocaleAvailable(localLocale))) {
/*  576 */       paramInputMethodLocator = paramInputMethodLocator.deriveLocator(localLocale);
/*      */     }
/*  578 */     this.inputMethodLocator = paramInputMethodLocator;
/*  579 */     this.inputMethodCreationFailed = false;
/*      */     
/*      */ 
/*  582 */     if (bool1) {
/*  583 */       this.inputMethod = getInputMethodInstance();
/*  584 */       if ((this.inputMethod instanceof InputMethodAdapter)) {
/*  585 */         ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(this.awtFocussedComponent);
/*      */       }
/*  587 */       activateInputMethod(true);
/*      */     }
/*      */     
/*      */ 
/*  591 */     if (i != 0) {
/*  592 */       this.inputMethod = getInputMethod();
/*  593 */       if (this.inputMethod != null) {
/*      */         try {
/*  595 */           this.inputMethod.setCompositionEnabled(bool2);
/*      */         }
/*      */         catch (UnsupportedOperationException localUnsupportedOperationException2) {}
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   Component getClientComponent()
/*      */   {
/*  605 */     return this.currentClientComponent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void removeNotify(Component paramComponent)
/*      */   {
/*  613 */     if (paramComponent == null) {
/*  614 */       throw new NullPointerException();
/*      */     }
/*      */     
/*  617 */     if (this.inputMethod == null) {
/*  618 */       if (paramComponent == this.currentClientComponent) {
/*  619 */         this.currentClientComponent = null;
/*      */       }
/*  621 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  626 */     if (paramComponent == this.awtFocussedComponent) {
/*  627 */       focusLost(paramComponent, false);
/*      */     }
/*      */     
/*  630 */     if (paramComponent == this.currentClientComponent) {
/*  631 */       if (this.isInputMethodActive)
/*      */       {
/*  633 */         deactivateInputMethod(false);
/*      */       }
/*  635 */       this.inputMethod.removeNotify();
/*  636 */       if ((this.clientWindowNotificationEnabled) && (addedClientWindowListeners())) {
/*  637 */         removeClientWindowListeners();
/*      */       }
/*  639 */       this.currentClientComponent = null;
/*  640 */       if ((this.inputMethod instanceof InputMethodAdapter)) {
/*  641 */         ((InputMethodAdapter)this.inputMethod).setClientComponent(null);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  648 */       if (EventQueue.isDispatchThread()) {
/*  649 */         ((InputMethodContext)this).releaseCompositionArea();
/*      */       } else {
/*  651 */         EventQueue.invokeLater(new Runnable() {
/*      */           public void run() {
/*  653 */             ((InputMethodContext)InputContext.this).releaseCompositionArea();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void dispose()
/*      */   {
/*  665 */     if (this.currentClientComponent != null) {
/*  666 */       throw new IllegalStateException("Can't dispose InputContext while it's active");
/*      */     }
/*  668 */     if (this.inputMethod != null) {
/*  669 */       if (this == inputMethodWindowContext) {
/*  670 */         this.inputMethod.hideWindows();
/*  671 */         inputMethodWindowContext = null;
/*      */       }
/*  673 */       if (this.inputMethod == previousInputMethod) {
/*  674 */         previousInputMethod = null;
/*      */       }
/*  676 */       if (this.clientWindowNotificationEnabled) {
/*  677 */         if (addedClientWindowListeners()) {
/*  678 */           removeClientWindowListeners();
/*      */         }
/*  680 */         this.clientWindowNotificationEnabled = false;
/*      */       }
/*  682 */       this.inputMethod.dispose();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  687 */       if (this.clientWindowNotificationEnabled) {
/*  688 */         enableClientWindowNotification(this.inputMethod, false);
/*      */       }
/*      */       
/*  691 */       this.inputMethod = null;
/*      */     }
/*  693 */     this.inputMethodLocator = null;
/*  694 */     if ((this.usedInputMethods != null) && (!this.usedInputMethods.isEmpty())) {
/*  695 */       Iterator localIterator = this.usedInputMethods.values().iterator();
/*  696 */       this.usedInputMethods = null;
/*  697 */       while (localIterator.hasNext()) {
/*  698 */         ((InputMethod)localIterator.next()).dispose();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  703 */     this.clientWindowNotificationEnabled = false;
/*  704 */     this.clientWindowListened = null;
/*  705 */     this.perInputMethodState = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public synchronized Object getInputMethodControlObject()
/*      */   {
/*  712 */     InputMethod localInputMethod = getInputMethod();
/*      */     
/*  714 */     if (localInputMethod != null) {
/*  715 */       return localInputMethod.getControlObject();
/*      */     }
/*  717 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCompositionEnabled(boolean paramBoolean)
/*      */   {
/*  726 */     InputMethod localInputMethod = getInputMethod();
/*      */     
/*  728 */     if (localInputMethod == null) {
/*  729 */       throw new UnsupportedOperationException();
/*      */     }
/*  731 */     localInputMethod.setCompositionEnabled(paramBoolean);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isCompositionEnabled()
/*      */   {
/*  739 */     InputMethod localInputMethod = getInputMethod();
/*      */     
/*  741 */     if (localInputMethod == null) {
/*  742 */       throw new UnsupportedOperationException();
/*      */     }
/*  744 */     return localInputMethod.isCompositionEnabled();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getInputMethodInfo()
/*      */   {
/*  752 */     InputMethod localInputMethod = getInputMethod();
/*      */     
/*  754 */     if (localInputMethod == null) {
/*  755 */       throw new UnsupportedOperationException("Null input method");
/*      */     }
/*      */     
/*  758 */     String str = null;
/*  759 */     if ((localInputMethod instanceof InputMethodAdapter))
/*      */     {
/*      */ 
/*  762 */       str = ((InputMethodAdapter)localInputMethod).getNativeInputMethodInfo();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  767 */     if ((str == null) && (this.inputMethodLocator != null))
/*      */     {
/*  769 */       str = this.inputMethodLocator.getDescriptor().getInputMethodDisplayName(getLocale(), 
/*  770 */         SunToolkit.getStartupLocale());
/*      */     }
/*      */     
/*  773 */     if ((str != null) && (!str.equals(""))) {
/*  774 */       return str;
/*      */     }
/*      */     
/*      */ 
/*  778 */     return localInputMethod.toString() + "-" + localInputMethod.getLocale().toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void disableNativeIM()
/*      */   {
/*  789 */     InputMethod localInputMethod = getInputMethod();
/*  790 */     if ((localInputMethod != null) && ((localInputMethod instanceof InputMethodAdapter))) {
/*  791 */       ((InputMethodAdapter)localInputMethod).stopListening();
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized InputMethod getInputMethod()
/*      */   {
/*  797 */     if (this.inputMethod != null) {
/*  798 */       return this.inputMethod;
/*      */     }
/*      */     
/*  801 */     if (this.inputMethodCreationFailed) {
/*  802 */       return null;
/*      */     }
/*      */     
/*  805 */     this.inputMethod = getInputMethodInstance();
/*  806 */     return this.inputMethod;
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
/*      */   private InputMethod getInputMethodInstance()
/*      */   {
/*  825 */     InputMethodLocator localInputMethodLocator = this.inputMethodLocator;
/*  826 */     if (localInputMethodLocator == null) {
/*  827 */       this.inputMethodCreationFailed = true;
/*  828 */       return null;
/*      */     }
/*      */     
/*  831 */     Locale localLocale = localInputMethodLocator.getLocale();
/*  832 */     InputMethod localInputMethod = null;
/*      */     
/*      */ 
/*  835 */     if (this.usedInputMethods != null) {
/*  836 */       localInputMethod = (InputMethod)this.usedInputMethods.remove(localInputMethodLocator.deriveLocator(null));
/*  837 */       if (localInputMethod != null) {
/*  838 */         if (localLocale != null) {
/*  839 */           localInputMethod.setLocale(localLocale);
/*      */         }
/*  841 */         localInputMethod.setCharacterSubsets(this.characterSubsets);
/*  842 */         Boolean localBoolean = (Boolean)this.perInputMethodState.remove(localInputMethod);
/*  843 */         if (localBoolean != null) {
/*  844 */           enableClientWindowNotification(localInputMethod, localBoolean.booleanValue());
/*      */         }
/*  846 */         ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot((!(localInputMethod instanceof InputMethodAdapter)) || 
/*      */         
/*  848 */           (((InputMethodAdapter)localInputMethod).supportsBelowTheSpot()));
/*  849 */         return localInputMethod;
/*      */       }
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  855 */       localInputMethod = localInputMethodLocator.getDescriptor().createInputMethod();
/*      */       
/*  857 */       if (localLocale != null) {
/*  858 */         localInputMethod.setLocale(localLocale);
/*      */       }
/*  860 */       localInputMethod.setInputMethodContext((InputMethodContext)this);
/*  861 */       localInputMethod.setCharacterSubsets(this.characterSubsets);
/*      */     }
/*      */     catch (Exception localException) {
/*  864 */       logCreationFailed(localException);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  869 */       this.inputMethodCreationFailed = true;
/*      */       
/*      */ 
/*      */ 
/*  873 */       if (localInputMethod != null) {
/*  874 */         localInputMethod = null;
/*      */       }
/*      */     } catch (LinkageError localLinkageError) {
/*  877 */       logCreationFailed(localLinkageError);
/*      */       
/*      */ 
/*  880 */       this.inputMethodCreationFailed = true;
/*      */     }
/*  882 */     ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot((!(localInputMethod instanceof InputMethodAdapter)) || 
/*      */     
/*  884 */       (((InputMethodAdapter)localInputMethod).supportsBelowTheSpot()));
/*  885 */     return localInputMethod;
/*      */   }
/*      */   
/*      */   private void logCreationFailed(Throwable paramThrowable) {
/*  889 */     PlatformLogger localPlatformLogger = PlatformLogger.getLogger("sun.awt.im");
/*  890 */     if (localPlatformLogger.isLoggable(PlatformLogger.Level.CONFIG)) {
/*  891 */       String str = Toolkit.getProperty("AWT.InputMethodCreationFailed", "Could not create {0}. Reason: {1}");
/*      */       
/*      */ 
/*      */ 
/*  895 */       Object[] arrayOfObject = {this.inputMethodLocator.getDescriptor().getInputMethodDisplayName(null, Locale.getDefault()), paramThrowable.getLocalizedMessage() };
/*  896 */       MessageFormat localMessageFormat = new MessageFormat(str);
/*  897 */       localPlatformLogger.config(localMessageFormat.format(arrayOfObject));
/*      */     }
/*      */   }
/*      */   
/*      */   InputMethodLocator getInputMethodLocator() {
/*  902 */     if (this.inputMethod != null) {
/*  903 */       return this.inputMethodLocator.deriveLocator(this.inputMethod.getLocale());
/*      */     }
/*  905 */     return this.inputMethodLocator;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public synchronized void endComposition()
/*      */   {
/*  912 */     if (this.inputMethod != null) {
/*  913 */       this.inputMethod.endComposition();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   synchronized void enableClientWindowNotification(InputMethod paramInputMethod, boolean paramBoolean)
/*      */   {
/*  925 */     if (paramInputMethod != this.inputMethod) {
/*  926 */       if (this.perInputMethodState == null) {
/*  927 */         this.perInputMethodState = new HashMap(5);
/*      */       }
/*  929 */       this.perInputMethodState.put(paramInputMethod, Boolean.valueOf(paramBoolean));
/*  930 */       return;
/*      */     }
/*      */     
/*  933 */     if (this.clientWindowNotificationEnabled != paramBoolean) {
/*  934 */       this.clientWindowLocation = null;
/*  935 */       this.clientWindowNotificationEnabled = paramBoolean;
/*      */     }
/*  937 */     if (this.clientWindowNotificationEnabled) {
/*  938 */       if (!addedClientWindowListeners()) {
/*  939 */         addClientWindowListeners();
/*      */       }
/*  941 */       if (this.clientWindowListened != null) {
/*  942 */         this.clientWindowLocation = null;
/*  943 */         notifyClientWindowChange(this.clientWindowListened);
/*      */       }
/*      */     }
/*  946 */     else if (addedClientWindowListeners()) {
/*  947 */       removeClientWindowListeners();
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized void notifyClientWindowChange(Window paramWindow)
/*      */   {
/*  953 */     if (this.inputMethod == null) {
/*  954 */       return;
/*      */     }
/*      */     
/*      */ 
/*  958 */     if ((!paramWindow.isVisible()) || (((paramWindow instanceof Frame)) && 
/*  959 */       (((Frame)paramWindow).getState() == 1))) {
/*  960 */       this.clientWindowLocation = null;
/*  961 */       this.inputMethod.notifyClientWindowChange(null);
/*  962 */       return;
/*      */     }
/*  964 */     Rectangle localRectangle = paramWindow.getBounds();
/*  965 */     if ((this.clientWindowLocation == null) || (!this.clientWindowLocation.equals(localRectangle))) {
/*  966 */       this.clientWindowLocation = localRectangle;
/*  967 */       this.inputMethod.notifyClientWindowChange(this.clientWindowLocation);
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized void addClientWindowListeners() {
/*  972 */     Component localComponent = getClientComponent();
/*  973 */     if (localComponent == null) {
/*  974 */       return;
/*      */     }
/*  976 */     Window localWindow = getComponentWindow(localComponent);
/*  977 */     if (localWindow == null) {
/*  978 */       return;
/*      */     }
/*  980 */     localWindow.addComponentListener(this);
/*  981 */     localWindow.addWindowListener(this);
/*  982 */     this.clientWindowListened = localWindow;
/*      */   }
/*      */   
/*      */   private synchronized void removeClientWindowListeners() {
/*  986 */     this.clientWindowListened.removeComponentListener(this);
/*  987 */     this.clientWindowListened.removeWindowListener(this);
/*  988 */     this.clientWindowListened = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean addedClientWindowListeners()
/*      */   {
/*  996 */     return this.clientWindowListened != null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void componentResized(ComponentEvent paramComponentEvent)
/*      */   {
/* 1003 */     notifyClientWindowChange((Window)paramComponentEvent.getComponent());
/*      */   }
/*      */   
/*      */   public void componentMoved(ComponentEvent paramComponentEvent) {
/* 1007 */     notifyClientWindowChange((Window)paramComponentEvent.getComponent());
/*      */   }
/*      */   
/*      */   public void componentShown(ComponentEvent paramComponentEvent) {
/* 1011 */     notifyClientWindowChange((Window)paramComponentEvent.getComponent());
/*      */   }
/*      */   
/*      */ 
/* 1015 */   public void componentHidden(ComponentEvent paramComponentEvent) { notifyClientWindowChange((Window)paramComponentEvent.getComponent()); }
/*      */   
/*      */   public void windowOpened(WindowEvent paramWindowEvent) {}
/*      */   
/*      */   public void windowClosing(WindowEvent paramWindowEvent) {}
/*      */   
/*      */   public void windowClosed(WindowEvent paramWindowEvent) {}
/*      */   
/* 1023 */   public void windowIconified(WindowEvent paramWindowEvent) { notifyClientWindowChange(paramWindowEvent.getWindow()); }
/*      */   
/*      */   public void windowDeiconified(WindowEvent paramWindowEvent)
/*      */   {
/* 1027 */     notifyClientWindowChange(paramWindowEvent.getWindow());
/*      */   }
/*      */   
/*      */ 
/*      */   public void windowActivated(WindowEvent paramWindowEvent) {}
/*      */   
/*      */   public void windowDeactivated(WindowEvent paramWindowEvent) {}
/*      */   
/*      */   private void initializeInputMethodSelectionKey()
/*      */   {
/* 1037 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/* 1040 */         Preferences localPreferences = Preferences.userRoot();
/* 1041 */         InputContext.access$002(InputContext.this.getInputMethodSelectionKeyStroke(localPreferences));
/*      */         
/* 1043 */         if (InputContext.inputMethodSelectionKey == null)
/*      */         {
/* 1045 */           localPreferences = Preferences.systemRoot();
/* 1046 */           InputContext.access$002(InputContext.this.getInputMethodSelectionKeyStroke(localPreferences));
/*      */         }
/* 1048 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private AWTKeyStroke getInputMethodSelectionKeyStroke(Preferences paramPreferences) {
/*      */     try {
/* 1055 */       if (paramPreferences.nodeExists("/java/awt/im/selectionKey")) {
/* 1056 */         Preferences localPreferences = paramPreferences.node("/java/awt/im/selectionKey");
/* 1057 */         int i = localPreferences.getInt("keyCode", 0);
/* 1058 */         if (i != 0) {
/* 1059 */           int j = localPreferences.getInt("modifiers", 0);
/* 1060 */           return AWTKeyStroke.getAWTKeyStroke(i, j);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (BackingStoreException localBackingStoreException) {}
/*      */     
/* 1066 */     return null;
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\im\InputContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
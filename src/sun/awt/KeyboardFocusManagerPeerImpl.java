/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Component;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.Window;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.awt.peer.KeyboardFocusManagerPeer;
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
/*     */ public abstract class KeyboardFocusManagerPeerImpl
/*     */   implements KeyboardFocusManagerPeer
/*     */ {
/*  46 */   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.focus.KeyboardFocusManagerPeerImpl");
/*     */   
/*     */ 
/*  49 */   private static AWTAccessor.KeyboardFocusManagerAccessor kfmAccessor = AWTAccessor.getKeyboardFocusManagerAccessor();
/*     */   
/*     */   public static final int SNFH_FAILURE = 0;
/*     */   
/*     */   public static final int SNFH_SUCCESS_HANDLED = 1;
/*     */   public static final int SNFH_SUCCESS_PROCEED = 2;
/*     */   
/*     */   public void clearGlobalFocusOwner(Window paramWindow)
/*     */   {
/*  58 */     if (paramWindow != null) {
/*  59 */       Component localComponent = paramWindow.getFocusOwner();
/*  60 */       if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
/*  61 */         focusLog.fine("Clearing global focus owner " + localComponent);
/*     */       }
/*  63 */       if (localComponent != null) {
/*  64 */         CausedFocusEvent localCausedFocusEvent = new CausedFocusEvent(localComponent, 1005, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
/*     */         
/*  66 */         SunToolkit.postPriorityEvent(localCausedFocusEvent);
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
/*     */   public static boolean shouldFocusOnClick(Component paramComponent)
/*     */   {
/*  80 */     int i = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  87 */     if (((paramComponent instanceof Canvas)) || ((paramComponent instanceof Scrollbar)))
/*     */     {
/*     */ 
/*  90 */       i = 1;
/*     */ 
/*     */     }
/*  93 */     else if ((paramComponent instanceof Panel)) {
/*  94 */       i = ((Panel)paramComponent).getComponentCount() == 0 ? 1 : 0;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*  99 */       Object localObject = paramComponent != null ? paramComponent.getPeer() : null;
/* 100 */       i = localObject != null ? ((ComponentPeer)localObject).isFocusable() : 0;
/*     */     }
/* 102 */     return (i != 0) && 
/* 103 */       (AWTAccessor.getComponentAccessor().canBeFocusOwner(paramComponent));
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
/*     */   public static boolean deliverFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause, Component paramComponent3)
/*     */   {
/* 118 */     if (paramComponent1 == null) {
/* 119 */       paramComponent1 = paramComponent2;
/*     */     }
/*     */     
/* 122 */     Component localComponent = paramComponent3;
/* 123 */     if ((localComponent != null) && (localComponent.getPeer() == null)) {
/* 124 */       localComponent = null;
/*     */     }
/* 126 */     if (localComponent != null) {
/* 127 */       localCausedFocusEvent = new CausedFocusEvent(localComponent, 1005, false, paramComponent1, paramCause);
/*     */       
/*     */ 
/* 130 */       if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
/* 131 */         focusLog.finer("Posting focus event: " + localCausedFocusEvent);
/*     */       }
/* 133 */       SunToolkit.postEvent(SunToolkit.targetToAppContext(localComponent), localCausedFocusEvent);
/*     */     }
/*     */     
/* 136 */     CausedFocusEvent localCausedFocusEvent = new CausedFocusEvent(paramComponent1, 1004, false, localComponent, paramCause);
/*     */     
/*     */ 
/* 139 */     if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
/* 140 */       focusLog.finer("Posting focus event: " + localCausedFocusEvent);
/*     */     }
/* 142 */     SunToolkit.postEvent(SunToolkit.targetToAppContext(paramComponent1), localCausedFocusEvent);
/* 143 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean requestFocusFor(Component paramComponent, CausedFocusEvent.Cause paramCause)
/*     */   {
/* 148 */     return AWTAccessor.getComponentAccessor().requestFocus(paramComponent, paramCause);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int shouldNativelyFocusHeavyweight(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
/*     */   {
/* 159 */     return kfmAccessor.shouldNativelyFocusHeavyweight(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong, paramCause);
/*     */   }
/*     */   
/*     */   public static void removeLastFocusRequest(Component paramComponent)
/*     */   {
/* 164 */     kfmAccessor.removeLastFocusRequest(paramComponent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean processSynchronousLightweightTransfer(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
/*     */   {
/* 174 */     return kfmAccessor.processSynchronousLightweightTransfer(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\KeyboardFocusManagerPeerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.awt;
/*     */ 
/*     */ import java.applet.Applet;
/*     */ import java.awt.AWTKeyStroke;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FocusTraversalPolicy;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Image;
/*     */ import java.awt.KeyEventDispatcher;
/*     */ import java.awt.KeyboardFocusManager;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.MenuComponent;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.awt.peer.FramePeer;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
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
/*     */ public abstract class EmbeddedFrame
/*     */   extends Frame
/*     */   implements KeyEventDispatcher, PropertyChangeListener
/*     */ {
/*  59 */   private boolean isCursorAllowed = true;
/*  60 */   private boolean supportsXEmbed = false;
/*     */   
/*     */   private KeyboardFocusManager appletKFM;
/*     */   
/*     */   private static final long serialVersionUID = 2967042741780317130L;
/*     */   
/*     */   protected static final boolean FORWARD = true;
/*     */   
/*     */   protected static final boolean BACKWARD = false;
/*     */   
/*     */ 
/*     */   public boolean supportsXEmbed()
/*     */   {
/*  73 */     return (this.supportsXEmbed) && (SunToolkit.needsXEmbed());
/*     */   }
/*     */   
/*     */   protected EmbeddedFrame(boolean paramBoolean) {
/*  77 */     this(0L, paramBoolean);
/*     */   }
/*     */   
/*     */   protected EmbeddedFrame()
/*     */   {
/*  82 */     this(0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected EmbeddedFrame(int paramInt)
/*     */   {
/*  90 */     this(paramInt);
/*     */   }
/*     */   
/*     */   protected EmbeddedFrame(long paramLong) {
/*  94 */     this(paramLong, false);
/*     */   }
/*     */   
/*     */   protected EmbeddedFrame(long paramLong, boolean paramBoolean) {
/*  98 */     this.supportsXEmbed = paramBoolean;
/*  99 */     registerListeners();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Container getParent()
/*     */   {
/* 106 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
/*     */   {
/* 115 */     if (!paramPropertyChangeEvent.getPropertyName().equals("managingFocus")) {
/* 116 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 121 */     if (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
/* 122 */       return;
/*     */     }
/*     */     
/*     */ 
/* 126 */     removeTraversingOutListeners((KeyboardFocusManager)paramPropertyChangeEvent.getSource());
/*     */     
/* 128 */     this.appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
/* 129 */     if (isVisible()) {
/* 130 */       addTraversingOutListeners(this.appletKFM);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void addTraversingOutListeners(KeyboardFocusManager paramKeyboardFocusManager)
/*     */   {
/* 138 */     paramKeyboardFocusManager.addKeyEventDispatcher(this);
/* 139 */     paramKeyboardFocusManager.addPropertyChangeListener("managingFocus", this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void removeTraversingOutListeners(KeyboardFocusManager paramKeyboardFocusManager)
/*     */   {
/* 146 */     paramKeyboardFocusManager.removeKeyEventDispatcher(this);
/* 147 */     paramKeyboardFocusManager.removePropertyChangeListener("managingFocus", this);
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
/*     */   public void registerListeners()
/*     */   {
/* 160 */     if (this.appletKFM != null) {
/* 161 */       removeTraversingOutListeners(this.appletKFM);
/*     */     }
/* 163 */     this.appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
/* 164 */     if (isVisible()) {
/* 165 */       addTraversingOutListeners(this.appletKFM);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/* 177 */     if (this.appletKFM != null) {
/* 178 */       addTraversingOutListeners(this.appletKFM);
/*     */     }
/* 180 */     super.show();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void hide()
/*     */   {
/* 191 */     if (this.appletKFM != null) {
/* 192 */       removeTraversingOutListeners(this.appletKFM);
/*     */     }
/* 194 */     super.hide();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
/*     */   {
/* 206 */     Container localContainer = AWTAccessor.getKeyboardFocusManagerAccessor().getCurrentFocusCycleRoot();
/*     */     
/*     */ 
/* 209 */     if (this != localContainer) {
/* 210 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 214 */     if (paramKeyEvent.getID() == 400) {
/* 215 */       return false;
/*     */     }
/*     */     
/* 218 */     if ((!getFocusTraversalKeysEnabled()) || (paramKeyEvent.isConsumed())) {
/* 219 */       return false;
/*     */     }
/*     */     
/* 222 */     AWTKeyStroke localAWTKeyStroke = AWTKeyStroke.getAWTKeyStrokeForEvent(paramKeyEvent);
/*     */     
/* 224 */     Component localComponent1 = paramKeyEvent.getComponent();
/*     */     
/* 226 */     Set localSet = getFocusTraversalKeys(0);
/* 227 */     Component localComponent2; if (localSet.contains(localAWTKeyStroke))
/*     */     {
/* 229 */       localComponent2 = getFocusTraversalPolicy().getLastComponent(this);
/* 230 */       if (((localComponent1 == localComponent2) || (localComponent2 == null)) && 
/* 231 */         (traverseOut(true))) {
/* 232 */         paramKeyEvent.consume();
/* 233 */         return true;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 238 */     localSet = getFocusTraversalKeys(1);
/* 239 */     if (localSet.contains(localAWTKeyStroke))
/*     */     {
/* 241 */       localComponent2 = getFocusTraversalPolicy().getFirstComponent(this);
/* 242 */       if (((localComponent1 == localComponent2) || (localComponent2 == null)) && 
/* 243 */         (traverseOut(false))) {
/* 244 */         paramKeyEvent.consume();
/* 245 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 249 */     return false;
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
/*     */   public boolean traverseIn(boolean paramBoolean)
/*     */   {
/* 271 */     Component localComponent = null;
/*     */     
/* 273 */     if (paramBoolean == true) {
/* 274 */       localComponent = getFocusTraversalPolicy().getFirstComponent(this);
/*     */     } else {
/* 276 */       localComponent = getFocusTraversalPolicy().getLastComponent(this);
/*     */     }
/* 278 */     if (localComponent != null)
/*     */     {
/*     */ 
/* 281 */       AWTAccessor.getKeyboardFocusManagerAccessor().setMostRecentFocusOwner(this, localComponent);
/* 282 */       synthesizeWindowActivation(true);
/*     */     }
/* 284 */     return null != localComponent;
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
/*     */   protected boolean traverseOut(boolean paramBoolean)
/*     */   {
/* 305 */     return false;
/*     */   }
/*     */   
/*     */   public void setTitle(String paramString) {}
/*     */   
/*     */   public void setIconImage(Image paramImage) {}
/*     */   
/*     */   public void setIconImages(List<? extends Image> paramList) {}
/*     */   
/*     */   public void setMenuBar(MenuBar paramMenuBar) {}
/*     */   
/*     */   public void setResizable(boolean paramBoolean) {}
/*     */   
/*     */   public void remove(MenuComponent paramMenuComponent) {}
/*     */   
/* 320 */   public boolean isResizable() { return true; }
/*     */   
/*     */ 
/*     */   public void addNotify()
/*     */   {
/* 325 */     synchronized (getTreeLock()) {
/* 326 */       if (getPeer() == null) {
/* 327 */         setPeer(new NullEmbeddedFramePeer(null));
/*     */       }
/* 329 */       super.addNotify();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setCursorAllowed(boolean paramBoolean)
/*     */   {
/* 336 */     this.isCursorAllowed = paramBoolean;
/* 337 */     getPeer().updateCursorImmediately();
/*     */   }
/*     */   
/* 340 */   public boolean isCursorAllowed() { return this.isCursorAllowed; }
/*     */   
/*     */   public Cursor getCursor() {
/* 343 */     return this.isCursorAllowed ? 
/* 344 */       super.getCursor() : 
/* 345 */       Cursor.getPredefinedCursor(0);
/*     */   }
/*     */   
/*     */   protected void setPeer(ComponentPeer paramComponentPeer)
/*     */   {
/* 350 */     AWTAccessor.getComponentAccessor().setPeer(this, paramComponentPeer);
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
/*     */   public void synthesizeWindowActivation(boolean paramBoolean) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setLocationPrivate(int paramInt1, int paramInt2)
/*     */   {
/* 390 */     Dimension localDimension = getSize();
/* 391 */     setBoundsPrivate(paramInt1, paramInt2, localDimension.width, localDimension.height);
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
/*     */   protected Point getLocationPrivate()
/*     */   {
/* 419 */     Rectangle localRectangle = getBoundsPrivate();
/* 420 */     return new Point(localRectangle.x, localRectangle.y);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 454 */     FramePeer localFramePeer = (FramePeer)getPeer();
/* 455 */     if (localFramePeer != null) {
/* 456 */       localFramePeer.setBoundsPrivate(paramInt1, paramInt2, paramInt3, paramInt4);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Rectangle getBoundsPrivate()
/*     */   {
/* 486 */     FramePeer localFramePeer = (FramePeer)getPeer();
/* 487 */     if (localFramePeer != null) {
/* 488 */       return localFramePeer.getBoundsPrivate();
/*     */     }
/*     */     
/* 491 */     return getBounds();
/*     */   }
/*     */   
/*     */ 
/*     */   public void toFront() {}
/*     */   
/*     */ 
/*     */   public void toBack() {}
/*     */   
/*     */ 
/*     */   public abstract void registerAccelerator(AWTKeyStroke paramAWTKeyStroke);
/*     */   
/*     */ 
/*     */   public abstract void unregisterAccelerator(AWTKeyStroke paramAWTKeyStroke);
/*     */   
/*     */ 
/*     */   public static Applet getAppletIfAncestorOf(Component paramComponent)
/*     */   {
/* 509 */     Container localContainer = paramComponent.getParent();
/* 510 */     Applet localApplet = null;
/* 511 */     while ((localContainer != null) && (!(localContainer instanceof EmbeddedFrame))) {
/* 512 */       if ((localContainer instanceof Applet)) {
/* 513 */         localApplet = (Applet)localContainer;
/*     */       }
/* 515 */       localContainer = localContainer.getParent();
/*     */     }
/* 517 */     return localContainer == null ? null : localApplet;
/*     */   }
/*     */   
/*     */   public void notifyModalBlocked(Dialog paramDialog, boolean paramBoolean) {}
/*     */   
/*     */   private static class NullEmbeddedFramePeer extends NullComponentPeer
/*     */     implements FramePeer {
/*     */     public void setTitle(String paramString) {}
/*     */     
/*     */     public void setIconImage(Image paramImage) {}
/*     */     
/*     */     public void updateIconImages() {}
/*     */     
/*     */     public void setMenuBar(MenuBar paramMenuBar) {}
/*     */     
/*     */     public void setResizable(boolean paramBoolean) {}
/*     */     
/*     */     public void setState(int paramInt) {}
/*     */     
/* 536 */     public int getState() { return 0; }
/*     */     public void setMaximizedBounds(Rectangle paramRectangle) {}
/*     */     public void toFront() {}
/*     */     public void toBack() {}
/*     */     public void updateFocusableWindowState() {}
/*     */     public void updateAlwaysOnTop() {}
/*     */     public void updateAlwaysOnTopState() {}
/* 543 */     public Component getGlobalHeavyweightFocusOwner() { return null; }
/*     */     
/* 545 */     public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 3); }
/*     */     
/*     */     public Rectangle getBoundsPrivate() {
/* 548 */       return getBounds();
/*     */     }
/*     */     
/*     */ 
/*     */     public void setModalBlocked(Dialog paramDialog, boolean paramBoolean) {}
/*     */     
/*     */     public void restack()
/*     */     {
/* 556 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isRestackSupported()
/*     */     {
/* 563 */       return false;
/*     */     }
/*     */     
/* 566 */     public boolean requestWindowFocus() { return false; }
/*     */     
/*     */     public void updateMinimumSize() {}
/*     */     
/*     */     public void setOpacity(float paramFloat) {}
/*     */     
/*     */     public void setOpaque(boolean paramBoolean) {}
/*     */     
/*     */     public void updateWindow() {}
/*     */     
/*     */     public void repositionSecurityWarning() {}
/*     */     
/*     */     public void emulateActivation(boolean paramBoolean) {}
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\EmbeddedFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
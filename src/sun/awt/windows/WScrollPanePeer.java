/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Adjustable;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.ScrollPane;
/*     */ import java.awt.ScrollPaneAdjustable;
/*     */ import java.awt.peer.ScrollPanePeer;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ScrollPaneAdjustableAccessor;
/*     */ import sun.awt.PeerEvent;
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
/*     */ final class WScrollPanePeer
/*     */   extends WPanelPeer
/*     */   implements ScrollPanePeer
/*     */ {
/*  38 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WScrollPanePeer");
/*     */   int scrollbarWidth;
/*     */   int scrollbarHeight;
/*     */   int prevx;
/*     */   int prevy;
/*     */   
/*     */   static
/*     */   {
/*  46 */     initIDs();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   WScrollPanePeer(Component paramComponent)
/*     */   {
/*  55 */     super(paramComponent);
/*  56 */     this.scrollbarWidth = _getVScrollbarWidth();
/*  57 */     this.scrollbarHeight = _getHScrollbarHeight();
/*     */   }
/*     */   
/*     */   void initialize()
/*     */   {
/*  62 */     super.initialize();
/*  63 */     setInsets();
/*  64 */     Insets localInsets = getInsets();
/*  65 */     setScrollPosition(-localInsets.left, -localInsets.top);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Insets insets()
/*     */   {
/*  75 */     return getInsets();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getHScrollbarHeight()
/*     */   {
/*  84 */     return this.scrollbarHeight;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVScrollbarWidth()
/*     */   {
/*  90 */     return this.scrollbarWidth;
/*     */   }
/*     */   
/*     */   public Point getScrollOffset()
/*     */   {
/*  95 */     int i = getOffset(0);
/*  96 */     int j = getOffset(1);
/*  97 */     return new Point(i, j);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void childResized(int paramInt1, int paramInt2)
/*     */   {
/* 108 */     ScrollPane localScrollPane = (ScrollPane)this.target;
/* 109 */     Dimension localDimension = localScrollPane.getSize();
/* 110 */     setSpans(localDimension.width, localDimension.height, paramInt1, paramInt2);
/* 111 */     setInsets();
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
/*     */   public void setValue(Adjustable paramAdjustable, int paramInt)
/*     */   {
/* 125 */     Component localComponent = getScrollChild();
/* 126 */     if (localComponent == null) {
/* 127 */       return;
/*     */     }
/*     */     
/* 130 */     Point localPoint = localComponent.getLocation();
/* 131 */     switch (paramAdjustable.getOrientation()) {
/*     */     case 1: 
/* 133 */       setScrollPosition(-localPoint.x, paramInt);
/* 134 */       break;
/*     */     case 0: 
/* 136 */       setScrollPosition(paramInt, -localPoint.y);
/*     */     }
/*     */   }
/*     */   
/*     */   private Component getScrollChild()
/*     */   {
/* 142 */     ScrollPane localScrollPane = (ScrollPane)this.target;
/* 143 */     Component localComponent = null;
/*     */     try {
/* 145 */       localComponent = localScrollPane.getComponent(0);
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
/*     */     
/* 149 */     return localComponent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void postScrollEvent(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
/*     */   {
/* 158 */     Adjustor localAdjustor = new Adjustor(paramInt1, paramInt2, paramInt3, paramBoolean);
/* 159 */     WToolkit.executeOnEventHandlerThread(new ScrollEvent(this.target, localAdjustor)); }
/*     */   
/*     */   static native void initIDs();
/*     */   
/*     */   native void create(WComponentPeer paramWComponentPeer);
/*     */   
/*     */   native int getOffset(int paramInt);
/*     */   
/*     */   public void setUnitIncrement(Adjustable paramAdjustable, int paramInt) {}
/*     */   
/* 169 */   class ScrollEvent extends PeerEvent { ScrollEvent(Object paramObject, Runnable paramRunnable) { super(paramRunnable, 0L); }
/*     */     
/*     */ 
/*     */     public PeerEvent coalesceEvents(PeerEvent paramPeerEvent)
/*     */     {
/* 174 */       if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINEST)) {
/* 175 */         WScrollPanePeer.log.finest("ScrollEvent coalesced: " + paramPeerEvent);
/*     */       }
/* 177 */       if ((paramPeerEvent instanceof ScrollEvent)) {
/* 178 */         return paramPeerEvent;
/*     */       }
/* 180 */       return null; } }
/*     */   
/*     */   private native void setInsets();
/*     */   
/*     */   public synchronized native void setScrollPosition(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int _getHScrollbarHeight();
/*     */   
/*     */   private native int _getVScrollbarWidth();
/*     */   
/*     */   synchronized native void setSpans(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */   class Adjustor implements Runnable { int orient;
/*     */     
/* 194 */     Adjustor(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) { this.orient = paramInt1;
/* 195 */       this.type = paramInt2;
/* 196 */       this.pos = paramInt3;
/* 197 */       this.isAdjusting = paramBoolean; }
/*     */     
/*     */     int type;
/*     */     int pos;
/*     */     boolean isAdjusting;
/* 202 */     public void run() { if (WScrollPanePeer.this.getScrollChild() == null) {
/* 203 */         return;
/*     */       }
/* 205 */       ScrollPane localScrollPane = (ScrollPane)WScrollPanePeer.this.target;
/* 206 */       ScrollPaneAdjustable localScrollPaneAdjustable = null;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */       if (this.orient == 1) {
/* 214 */         localScrollPaneAdjustable = (ScrollPaneAdjustable)localScrollPane.getVAdjustable();
/* 215 */       } else if (this.orient == 0) {
/* 216 */         localScrollPaneAdjustable = (ScrollPaneAdjustable)localScrollPane.getHAdjustable();
/*     */       }
/* 218 */       else if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) {
/* 219 */         WScrollPanePeer.log.fine("Assertion failed: unknown orient");
/*     */       }
/*     */       
/*     */ 
/* 223 */       if (localScrollPaneAdjustable == null) {
/* 224 */         return;
/*     */       }
/*     */       
/* 227 */       int i = localScrollPaneAdjustable.getValue();
/* 228 */       switch (this.type) {
/*     */       case 2: 
/* 230 */         i -= localScrollPaneAdjustable.getUnitIncrement();
/* 231 */         break;
/*     */       case 1: 
/* 233 */         i += localScrollPaneAdjustable.getUnitIncrement();
/* 234 */         break;
/*     */       case 3: 
/* 236 */         i -= localScrollPaneAdjustable.getBlockIncrement();
/* 237 */         break;
/*     */       case 4: 
/* 239 */         i += localScrollPaneAdjustable.getBlockIncrement();
/* 240 */         break;
/*     */       case 5: 
/* 242 */         i = this.pos;
/* 243 */         break;
/*     */       default: 
/* 245 */         if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) {
/* 246 */           WScrollPanePeer.log.fine("Assertion failed: unknown type");
/*     */         }
/* 248 */         return;
/*     */       }
/*     */       
/*     */       
/* 252 */       i = Math.max(localScrollPaneAdjustable.getMinimum(), i);
/* 253 */       i = Math.min(localScrollPaneAdjustable.getMaximum(), i);
/*     */       
/*     */ 
/* 256 */       localScrollPaneAdjustable.setValueIsAdjusting(this.isAdjusting);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 261 */       AWTAccessor.getScrollPaneAdjustableAccessor().setTypedValue(localScrollPaneAdjustable, i, this.type);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 267 */       Object localObject = WScrollPanePeer.this.getScrollChild();
/* 268 */       while ((localObject != null) && 
/* 269 */         (!(((Component)localObject).getPeer() instanceof WComponentPeer)))
/*     */       {
/* 271 */         localObject = ((Component)localObject).getParent();
/*     */       }
/* 273 */       if ((WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) && 
/* 274 */         (localObject == null)) {
/* 275 */         WScrollPanePeer.log.fine("Assertion (hwAncestor != null) failed, couldn't find heavyweight ancestor of scroll pane child");
/*     */       }
/*     */       
/*     */ 
/* 279 */       WComponentPeer localWComponentPeer = (WComponentPeer)((Component)localObject).getPeer();
/* 280 */       localWComponentPeer.paintDamagedAreaImmediately();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WScrollPanePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.peer.FramePeer;
/*     */ import java.security.AccessController;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.FrameAccessor;
/*     */ import sun.awt.im.InputMethodManager;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WFramePeer
/*     */   extends WWindowPeer
/*     */   implements FramePeer
/*     */ {
/*     */   private static native void initIDs();
/*     */   
/*     */   public native void setState(int paramInt);
/*     */   
/*     */   public native int getState();
/*     */   
/*     */   public void setExtendedState(int paramInt)
/*     */   {
/*  51 */     AWTAccessor.getFrameAccessor().setExtendedState((Frame)this.target, paramInt);
/*     */   }
/*     */   
/*  54 */   public int getExtendedState() { return AWTAccessor.getFrameAccessor().getExtendedState((Frame)this.target); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  62 */   private static final boolean keepOnMinimize = "true".equals(
/*  63 */     AccessController.doPrivileged(new GetPropertyAction("sun.awt.keepWorkingSetOnMinimize")));
/*     */   
/*     */   private native void setMaximizedBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */   private native void clearMaximizedBounds();
/*     */   
/*  69 */   public void setMaximizedBounds(Rectangle paramRectangle) { if (paramRectangle == null) {
/*  70 */       clearMaximizedBounds();
/*     */     } else {
/*  72 */       Rectangle localRectangle = (Rectangle)paramRectangle.clone();
/*  73 */       adjustMaximizedBounds(localRectangle);
/*  74 */       setMaximizedBounds(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
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
/*     */   private void adjustMaximizedBounds(Rectangle paramRectangle)
/*     */   {
/*  90 */     GraphicsConfiguration localGraphicsConfiguration1 = getGraphicsConfiguration();
/*     */     
/*     */ 
/*  93 */     GraphicsDevice localGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
/*  94 */     GraphicsConfiguration localGraphicsConfiguration2 = localGraphicsDevice.getDefaultConfiguration();
/*     */     
/*  96 */     if ((localGraphicsConfiguration1 != null) && (localGraphicsConfiguration1 != localGraphicsConfiguration2)) {
/*  97 */       Rectangle localRectangle1 = localGraphicsConfiguration1.getBounds();
/*  98 */       Rectangle localRectangle2 = localGraphicsConfiguration2.getBounds();
/*     */       
/* 100 */       int i = (localRectangle1.width - localRectangle2.width > 0) || (localRectangle1.height - localRectangle2.height > 0) ? 1 : 0;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */       if (i != 0) {
/* 107 */         paramRectangle.width -= localRectangle1.width - localRectangle2.width;
/* 108 */         paramRectangle.height -= localRectangle1.height - localRectangle2.height;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration)
/*     */   {
/* 115 */     boolean bool = super.updateGraphicsData(paramGraphicsConfiguration);
/*     */     
/* 117 */     Rectangle localRectangle = AWTAccessor.getFrameAccessor().getMaximizedBounds((Frame)this.target);
/* 118 */     if (localRectangle != null) {
/* 119 */       setMaximizedBounds(localRectangle);
/*     */     }
/* 121 */     return bool;
/*     */   }
/*     */   
/*     */   boolean isTargetUndecorated()
/*     */   {
/* 126 */     return ((Frame)this.target).isUndecorated();
/*     */   }
/*     */   
/*     */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 131 */     if (((Frame)this.target).isUndecorated()) {
/* 132 */       super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     } else {
/* 134 */       reshapeFrame(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */   }
/*     */   
/*     */   public Dimension getMinimumSize()
/*     */   {
/* 140 */     Dimension localDimension = new Dimension();
/* 141 */     if (!((Frame)this.target).isUndecorated()) {
/* 142 */       localDimension.setSize(getSysMinWidth(), getSysMinHeight());
/*     */     }
/* 144 */     if (((Frame)this.target).getMenuBar() != null) {
/* 145 */       localDimension.height += getSysMenuHeight();
/*     */     }
/* 147 */     return localDimension;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMenuBar(MenuBar paramMenuBar)
/*     */   {
/* 155 */     WMenuBarPeer localWMenuBarPeer = (WMenuBarPeer)WToolkit.targetToPeer(paramMenuBar);
/* 156 */     if (localWMenuBarPeer != null) {
/* 157 */       if (localWMenuBarPeer.framePeer != this) {
/* 158 */         paramMenuBar.removeNotify();
/* 159 */         paramMenuBar.addNotify();
/* 160 */         localWMenuBarPeer = (WMenuBarPeer)WToolkit.targetToPeer(paramMenuBar);
/* 161 */         if ((localWMenuBarPeer != null) && (localWMenuBarPeer.framePeer != this)) {
/* 162 */           throw new IllegalStateException("Wrong parent peer");
/*     */         }
/*     */       }
/* 165 */       if (localWMenuBarPeer != null) {
/* 166 */         addChildPeer(localWMenuBarPeer);
/*     */       }
/*     */     }
/* 169 */     setMenuBar0(localWMenuBarPeer);
/* 170 */     updateInsets(this.insets_);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private native void setMenuBar0(WMenuBarPeer paramWMenuBarPeer);
/*     */   
/*     */ 
/*     */ 
/*     */   WFramePeer(Frame paramFrame)
/*     */   {
/* 181 */     super(paramFrame);
/*     */     
/* 183 */     InputMethodManager localInputMethodManager = InputMethodManager.getInstance();
/* 184 */     String str = localInputMethodManager.getTriggerMenuString();
/* 185 */     if (str != null)
/*     */     {
/* 187 */       pSetIMMOption(str);
/*     */     }
/*     */   }
/*     */   
/*     */   native void createAwtFrame(WComponentPeer paramWComponentPeer);
/*     */   
/*     */   void create(WComponentPeer paramWComponentPeer) {
/* 194 */     preCreate(paramWComponentPeer);
/* 195 */     createAwtFrame(paramWComponentPeer);
/*     */   }
/*     */   
/*     */   void initialize()
/*     */   {
/* 200 */     super.initialize();
/*     */     
/* 202 */     Frame localFrame = (Frame)this.target;
/*     */     
/* 204 */     if (localFrame.getTitle() != null) {
/* 205 */       setTitle(localFrame.getTitle());
/*     */     }
/* 207 */     setResizable(localFrame.isResizable());
/* 208 */     setState(localFrame.getExtendedState());
/*     */   }
/*     */   
/*     */   private static native int getSysMenuHeight();
/*     */   
/*     */   native void pSetIMMOption(String paramString);
/*     */   
/* 215 */   void notifyIMMOptionChange() { InputMethodManager.getInstance().notifyChangeRequest((Component)this.target); }
/*     */   
/*     */ 
/*     */   public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 220 */     setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 3);
/*     */   }
/*     */   
/*     */   public Rectangle getBoundsPrivate() {
/* 224 */     return getBounds();
/*     */   }
/*     */   
/*     */ 
/*     */   public void emulateActivation(boolean paramBoolean)
/*     */   {
/* 230 */     synthesizeWmActivate(paramBoolean);
/*     */   }
/*     */   
/*     */   private native void synthesizeWmActivate(boolean paramBoolean);
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WFramePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
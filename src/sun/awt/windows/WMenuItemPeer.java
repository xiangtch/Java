/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Font;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.MenuShortcut;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.peer.MenuItemPeer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
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
/*     */ class WMenuItemPeer
/*     */   extends WObjectPeer
/*     */   implements MenuItemPeer
/*     */ {
/*  37 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.WMenuItemPeer");
/*     */   String shortcutLabel;
/*     */   
/*  40 */   static { initIDs(); }
/*     */   
/*     */ 
/*     */ 
/*     */   protected WMenuPeer parent;
/*     */   
/*     */ 
/*     */   private final boolean isCheckbox;
/*     */   
/*     */ 
/*     */   protected void disposeImpl()
/*     */   {
/*  52 */     WToolkit.targetDisposedPeer(this.target, this);
/*  53 */     _dispose();
/*     */   }
/*     */   
/*     */   public void setEnabled(boolean paramBoolean) {
/*  57 */     enable(paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void enable()
/*     */   {
/*  64 */     enable(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void disable()
/*     */   {
/*  71 */     enable(false);
/*     */   }
/*     */   
/*     */   private void readShortcutLabel()
/*     */   {
/*  76 */     WMenuPeer localWMenuPeer = this.parent;
/*  77 */     while ((localWMenuPeer != null) && (!(localWMenuPeer instanceof WMenuBarPeer))) {
/*  78 */       localWMenuPeer = localWMenuPeer.parent;
/*     */     }
/*  80 */     if ((localWMenuPeer instanceof WMenuBarPeer)) {
/*  81 */       MenuShortcut localMenuShortcut = ((MenuItem)this.target).getShortcut();
/*  82 */       this.shortcutLabel = (localMenuShortcut != null ? localMenuShortcut.toString() : null);
/*     */     } else {
/*  84 */       this.shortcutLabel = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLabel(String paramString)
/*     */   {
/*  90 */     readShortcutLabel();
/*  91 */     _setLabel(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected WMenuItemPeer()
/*     */   {
/* 100 */     this.isCheckbox = false;
/*     */   }
/*     */   
/* 103 */   WMenuItemPeer(MenuItem paramMenuItem) { this(paramMenuItem, false); }
/*     */   
/*     */   WMenuItemPeer(MenuItem paramMenuItem, boolean paramBoolean)
/*     */   {
/* 107 */     this.target = paramMenuItem;
/* 108 */     this.parent = ((WMenuPeer)WToolkit.targetToPeer(paramMenuItem.getParent()));
/* 109 */     this.isCheckbox = paramBoolean;
/* 110 */     this.parent.addChildPeer(this);
/* 111 */     create(this.parent);
/*     */     
/* 113 */     checkMenuCreation();
/*     */     
/* 115 */     readShortcutLabel();
/*     */   }
/*     */   
/*     */ 
/*     */   void checkMenuCreation()
/*     */   {
/* 121 */     if (this.pData == 0L)
/*     */     {
/* 123 */       if (this.createError != null)
/*     */       {
/* 125 */         throw this.createError;
/*     */       }
/*     */       
/*     */ 
/* 129 */       throw new InternalError("couldn't create menu peer");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void postEvent(AWTEvent paramAWTEvent)
/*     */   {
/* 139 */     WToolkit.postEvent(WToolkit.targetToAppContext(this.target), paramAWTEvent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void handleAction(final long paramLong, int paramInt)
/*     */   {
/* 149 */     WToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
/*     */       public void run() {
/* 151 */         WMenuItemPeer.this.postEvent(new ActionEvent(WMenuItemPeer.this.target, 1001, ((MenuItem)WMenuItemPeer.this.target)
/*     */         
/* 153 */           .getActionCommand(), paramLong, this.val$modifiers));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 162 */   private static Font defaultMenuFont = (Font)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Font run() {
/*     */       try {
/* 166 */         ResourceBundle localResourceBundle = ResourceBundle.getBundle("sun.awt.windows.awtLocalization");
/* 167 */         return Font.decode(localResourceBundle.getString("menuFont"));
/*     */       } catch (MissingResourceException localMissingResourceException) {
/* 169 */         if (WMenuItemPeer.log.isLoggable(PlatformLogger.Level.FINE))
/* 170 */           WMenuItemPeer.log.fine("WMenuItemPeer: " + localMissingResourceException.getMessage() + ". Using default MenuItem font.", localMissingResourceException);
/*     */       }
/* 172 */       return new Font("SanSerif", 0, 11);
/*     */     }
/* 162 */   });
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Font getDefaultFont()
/*     */   {
/* 179 */     return defaultMenuFont;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFont(Font paramFont)
/*     */   {
/* 190 */     _setFont(paramFont);
/*     */   }
/*     */   
/*     */   private synchronized native void _dispose();
/*     */   
/*     */   public native void _setLabel(String paramString);
/*     */   
/*     */   native void create(WMenuPeer paramWMenuPeer);
/*     */   
/*     */   native void enable(boolean paramBoolean);
/*     */   
/*     */   private static native void initIDs();
/*     */   
/*     */   private native void _setFont(Font paramFont);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WMenuItemPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
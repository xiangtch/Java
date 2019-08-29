/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.TrayIcon;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.awt.peer.TrayIconPeer;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.image.IntegerComponentRaster;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WTrayIconPeer
/*     */   extends WObjectPeer
/*     */   implements TrayIconPeer
/*     */ {
/*     */   static final int TRAY_ICON_WIDTH = 16;
/*     */   static final int TRAY_ICON_HEIGHT = 16;
/*     */   static final int TRAY_ICON_MASK_SIZE = 32;
/*  45 */   IconObserver observer = new IconObserver();
/*  46 */   boolean firstUpdate = true;
/*  47 */   Frame popupParent = new Frame("PopupMessageWindow");
/*     */   PopupMenu popup;
/*     */   
/*     */   protected void disposeImpl()
/*     */   {
/*  52 */     if (this.popupParent != null) {
/*  53 */       this.popupParent.dispose();
/*     */     }
/*  55 */     this.popupParent.dispose();
/*  56 */     _dispose();
/*  57 */     WToolkit.targetDisposedPeer(this.target, this);
/*     */   }
/*     */   
/*     */   WTrayIconPeer(TrayIcon paramTrayIcon) {
/*  61 */     this.target = paramTrayIcon;
/*  62 */     this.popupParent.addNotify();
/*  63 */     create();
/*  64 */     updateImage();
/*     */   }
/*     */   
/*     */   public void updateImage()
/*     */   {
/*  69 */     Image localImage = ((TrayIcon)this.target).getImage();
/*  70 */     if (localImage != null) {
/*  71 */       updateNativeImage(localImage);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public native void setToolTip(String paramString);
/*     */   
/*     */   public synchronized void showPopupMenu(final int paramInt1, final int paramInt2)
/*     */   {
/*  80 */     if (isDisposed()) {
/*  81 */       return;
/*     */     }
/*  83 */     SunToolkit.executeOnEventHandlerThread(this.target, new Runnable()
/*     */     {
/*     */       public void run() {
/*  86 */         PopupMenu localPopupMenu = ((TrayIcon)WTrayIconPeer.this.target).getPopupMenu();
/*  87 */         if (WTrayIconPeer.this.popup != localPopupMenu) {
/*  88 */           if (WTrayIconPeer.this.popup != null) {
/*  89 */             WTrayIconPeer.this.popupParent.remove(WTrayIconPeer.this.popup);
/*     */           }
/*  91 */           if (localPopupMenu != null) {
/*  92 */             WTrayIconPeer.this.popupParent.add(localPopupMenu);
/*     */           }
/*  94 */           WTrayIconPeer.this.popup = localPopupMenu;
/*     */         }
/*  96 */         if (WTrayIconPeer.this.popup != null) {
/*  97 */           ((WPopupMenuPeer)WTrayIconPeer.this.popup.getPeer()).show(WTrayIconPeer.this.popupParent, new Point(paramInt1, paramInt2));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void displayMessage(String paramString1, String paramString2, String paramString3)
/*     */   {
/* 106 */     if (paramString1 == null) {
/* 107 */       paramString1 = "";
/*     */     }
/* 109 */     if (paramString2 == null) {
/* 110 */       paramString2 = "";
/*     */     }
/* 112 */     _displayMessage(paramString1, paramString2, paramString3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void updateNativeImage(Image paramImage)
/*     */   {
/* 121 */     if (isDisposed()) {
/* 122 */       return;
/*     */     }
/* 124 */     boolean bool = ((TrayIcon)this.target).isImageAutoSize();
/*     */     
/* 126 */     BufferedImage localBufferedImage = new BufferedImage(16, 16, 2);
/*     */     
/* 128 */     Graphics2D localGraphics2D = localBufferedImage.createGraphics();
/* 129 */     if (localGraphics2D != null) {
/*     */       try {
/* 131 */         localGraphics2D.setPaintMode();
/*     */         
/* 133 */         localGraphics2D.drawImage(paramImage, 0, 0, bool ? 16 : paramImage.getWidth(this.observer), bool ? 16 : paramImage
/* 134 */           .getHeight(this.observer), this.observer);
/*     */         
/* 136 */         createNativeImage(localBufferedImage);
/*     */         
/* 138 */         updateNativeIcon(!this.firstUpdate);
/* 139 */         if (this.firstUpdate) this.firstUpdate = false;
/*     */       }
/*     */       finally {
/* 142 */         localGraphics2D.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   void createNativeImage(BufferedImage paramBufferedImage) {
/* 148 */     WritableRaster localWritableRaster = paramBufferedImage.getRaster();
/* 149 */     byte[] arrayOfByte = new byte[32];
/* 150 */     int[] arrayOfInt = ((DataBufferInt)localWritableRaster.getDataBuffer()).getData();
/* 151 */     int i = arrayOfInt.length;
/* 152 */     int j = localWritableRaster.getWidth();
/*     */     
/* 154 */     for (int k = 0; k < i; k++) {
/* 155 */       int m = k / 8;
/* 156 */       int n = 1 << 7 - k % 8;
/*     */       
/* 158 */       if ((arrayOfInt[k] & 0xFF000000) == 0)
/*     */       {
/* 160 */         if (m < arrayOfByte.length) {
/* 161 */           int tmp83_81 = m; byte[] tmp83_80 = arrayOfByte;tmp83_80[tmp83_81] = ((byte)(tmp83_80[tmp83_81] | n));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 166 */     if ((localWritableRaster instanceof IntegerComponentRaster)) {
/* 167 */       j = ((IntegerComponentRaster)localWritableRaster).getScanlineStride();
/*     */     }
/* 169 */     setNativeIcon(((DataBufferInt)paramBufferedImage.getRaster().getDataBuffer()).getData(), arrayOfByte, j, localWritableRaster
/* 170 */       .getWidth(), localWritableRaster.getHeight());
/*     */   }
/*     */   
/*     */   void postEvent(AWTEvent paramAWTEvent) {
/* 174 */     WToolkit.postEvent(WToolkit.targetToAppContext(this.target), paramAWTEvent);
/*     */   }
/*     */   
/*     */ 
/*     */   native void create();
/*     */   
/*     */   synchronized native void _dispose();
/*     */   
/*     */   native void updateNativeIcon(boolean paramBoolean);
/*     */   
/*     */   native void setNativeIcon(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   native void _displayMessage(String paramString1, String paramString2, String paramString3);
/*     */   
/*     */   class IconObserver
/*     */     implements ImageObserver
/*     */   {
/*     */     IconObserver() {}
/*     */     
/*     */     public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */     {
/* 195 */       if ((paramImage != ((TrayIcon)WTrayIconPeer.this.target).getImage()) || 
/* 196 */         (WTrayIconPeer.this.isDisposed()))
/*     */       {
/* 198 */         return false;
/*     */       }
/* 200 */       if ((paramInt1 & 0x33) != 0)
/*     */       {
/*     */ 
/* 203 */         WTrayIconPeer.this.updateNativeImage(paramImage);
/*     */       }
/* 205 */       return (paramInt1 & 0x20) == 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WTrayIconPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
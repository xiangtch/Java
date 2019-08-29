/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Event;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Font;
/*     */ import java.awt.Window;
/*     */ import java.awt.dnd.DropTarget;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.awt.peer.FileDialogPeer;
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.List;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Vector;
/*     */ import sun.awt.AWTAccessor;
/*     */ import sun.awt.AWTAccessor.ComponentAccessor;
/*     */ import sun.awt.AWTAccessor.FileDialogAccessor;
/*     */ import sun.awt.CausedFocusEvent.Cause;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WFileDialogPeer
/*     */   extends WWindowPeer
/*     */   implements FileDialogPeer
/*     */ {
/*     */   private WComponentPeer parent;
/*     */   private FilenameFilter fileFilter;
/*  49 */   private Vector<WWindowPeer> blockedWindows = new Vector();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFilenameFilter(FilenameFilter paramFilenameFilter)
/*     */   {
/*  56 */     this.fileFilter = paramFilenameFilter;
/*     */   }
/*     */   
/*     */   boolean checkFilenameFilter(String paramString) {
/*  60 */     FileDialog localFileDialog = (FileDialog)this.target;
/*  61 */     if (this.fileFilter == null) {
/*  62 */       return true;
/*     */     }
/*  64 */     File localFile = new File(paramString);
/*  65 */     return this.fileFilter.accept(new File(localFile.getParent()), localFile.getName());
/*     */   }
/*     */   
/*     */   WFileDialogPeer(FileDialog paramFileDialog)
/*     */   {
/*  70 */     super(paramFileDialog);
/*     */   }
/*     */   
/*     */   void create(WComponentPeer paramWComponentPeer)
/*     */   {
/*  75 */     this.parent = paramWComponentPeer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void initialize()
/*     */   {
/*  85 */     setFilenameFilter(((FileDialog)this.target).getFilenameFilter());
/*     */   }
/*     */   
/*     */ 
/*     */   protected void disposeImpl()
/*     */   {
/*  91 */     WToolkit.targetDisposedPeer(this.target, this);
/*  92 */     _dispose();
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
/*     */   public void show()
/*     */   {
/* 105 */     new Thread(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 103 */         WFileDialogPeer.this._show();
/*     */       }
/*     */     })
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 105 */       .start();
/*     */   }
/*     */   
/*     */   void hide()
/*     */   {
/* 110 */     _hide();
/*     */   }
/*     */   
/*     */   void setHWnd(long paramLong)
/*     */   {
/* 115 */     if (this.hwnd == paramLong) {
/* 116 */       return;
/*     */     }
/* 118 */     this.hwnd = paramLong;
/* 119 */     for (WWindowPeer localWWindowPeer : this.blockedWindows) {
/* 120 */       if (paramLong != 0L) {
/* 121 */         localWWindowPeer.modalDisable((Dialog)this.target, paramLong);
/*     */       } else {
/* 123 */         localWWindowPeer.modalEnable((Dialog)this.target);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void handleSelected(char[] paramArrayOfChar)
/*     */   {
/* 144 */     String[] arrayOfString = new String(paramArrayOfChar).split("\000");
/* 145 */     int i = arrayOfString.length > 1 ? 1 : 0;
/*     */     
/* 147 */     String str1 = null;
/* 148 */     String str2 = null;
/* 149 */     File[] arrayOfFile = null;
/*     */     int j;
/* 151 */     if (i != 0) {
/* 152 */       str1 = arrayOfString[0];
/* 153 */       j = arrayOfString.length - 1;
/* 154 */       arrayOfFile = new File[j];
/* 155 */       for (int k = 0; k < j; k++) {
/* 156 */         arrayOfFile[k] = new File(str1, arrayOfString[(k + 1)]);
/*     */       }
/* 158 */       str2 = arrayOfString[1];
/*     */     } else {
/* 160 */       j = arrayOfString[0].lastIndexOf(File.separatorChar);
/* 161 */       if (j == -1) {
/* 162 */         str1 = "." + File.separator;
/* 163 */         str2 = arrayOfString[0];
/*     */       } else {
/* 165 */         str1 = arrayOfString[0].substring(0, j + 1);
/* 166 */         str2 = arrayOfString[0].substring(j + 1);
/*     */       }
/* 168 */       arrayOfFile = new File[] { new File(str1, str2) };
/*     */     }
/*     */     
/* 171 */     final FileDialog localFileDialog = (FileDialog)this.target;
/* 172 */     AWTAccessor.FileDialogAccessor localFileDialogAccessor = AWTAccessor.getFileDialogAccessor();
/*     */     
/* 174 */     localFileDialogAccessor.setDirectory(localFileDialog, str1);
/* 175 */     localFileDialogAccessor.setFile(localFileDialog, str2);
/* 176 */     localFileDialogAccessor.setFiles(localFileDialog, arrayOfFile);
/*     */     
/* 178 */     WToolkit.executeOnEventHandlerThread(localFileDialog, new Runnable()
/*     */     {
/*     */       public void run() {
/* 181 */         localFileDialog.setVisible(false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   void handleCancel()
/*     */   {
/* 189 */     final FileDialog localFileDialog = (FileDialog)this.target;
/*     */     
/* 191 */     AWTAccessor.getFileDialogAccessor().setFile(localFileDialog, null);
/* 192 */     AWTAccessor.getFileDialogAccessor().setFiles(localFileDialog, null);
/* 193 */     AWTAccessor.getFileDialogAccessor().setDirectory(localFileDialog, null);
/*     */     
/* 195 */     WToolkit.executeOnEventHandlerThread(localFileDialog, new Runnable()
/*     */     {
/*     */       public void run() {
/* 198 */         localFileDialog.setVisible(false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*  43 */     initIDs();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 205 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run()
/*     */       {
/*     */         try {
/* 210 */           ResourceBundle localResourceBundle = ResourceBundle.getBundle("sun.awt.windows.awtLocalization");
/* 211 */           return localResourceBundle.getString("allFiles");
/*     */         } catch (MissingResourceException localMissingResourceException) {}
/* 213 */         return "All Files";
/*     */       }
/*     */       
/* 216 */     });
/* 217 */     setFilterString(str);
/*     */   }
/*     */   
/*     */   void blockWindow(WWindowPeer paramWWindowPeer) {
/* 221 */     this.blockedWindows.add(paramWWindowPeer);
/*     */     
/*     */ 
/* 224 */     if (this.hwnd != 0L)
/* 225 */       paramWWindowPeer.modalDisable((Dialog)this.target, this.hwnd);
/*     */   }
/*     */   
/*     */   void unblockWindow(WWindowPeer paramWWindowPeer) {
/* 229 */     this.blockedWindows.remove(paramWWindowPeer);
/*     */     
/*     */ 
/* 232 */     if (this.hwnd != 0L) {
/* 233 */       paramWWindowPeer.modalEnable((Dialog)this.target);
/*     */     }
/*     */   }
/*     */   
/*     */   public void blockWindows(List<Window> paramList)
/*     */   {
/* 239 */     for (Window localWindow : paramList) {
/* 240 */       WWindowPeer localWWindowPeer = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow);
/* 241 */       if (localWWindowPeer != null) {
/* 242 */         blockWindow(localWWindowPeer);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean handleEvent(Event paramEvent)
/*     */   {
/* 271 */     return false;
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
/*     */   public boolean requestFocus(boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 284 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
/*     */   {
/* 292 */     return false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isMultipleMode()
/*     */   {
/* 333 */     FileDialog localFileDialog = (FileDialog)this.target;
/* 334 */     return AWTAccessor.getFileDialogAccessor().isMultipleMode(localFileDialog);
/*     */   }
/*     */   
/*     */   private static native void setFilterString(String paramString);
/*     */   
/*     */   protected void checkCreation() {}
/*     */   
/*     */   private native void _dispose();
/*     */   
/*     */   private native void _show();
/*     */   
/*     */   private native void _hide();
/*     */   
/*     */   public native void toFront();
/*     */   
/*     */   public native void toBack();
/*     */   
/*     */   public void updateAlwaysOnTopState() {}
/*     */   
/*     */   public void setDirectory(String paramString) {}
/*     */   
/*     */   public void setFile(String paramString) {}
/*     */   
/*     */   public void setTitle(String paramString) {}
/*     */   
/*     */   public void setResizable(boolean paramBoolean) {}
/*     */   
/*     */   void enable() {}
/*     */   
/*     */   void disable() {}
/*     */   
/*     */   public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
/*     */   
/*     */   public void setForeground(Color paramColor) {}
/*     */   
/*     */   public void setBackground(Color paramColor) {}
/*     */   
/*     */   public void setFont(Font paramFont) {}
/*     */   
/*     */   public void updateMinimumSize() {}
/*     */   
/*     */   public void updateIconImages() {}
/*     */   
/*     */   void start() {}
/*     */   
/*     */   public void beginValidate() {}
/*     */   
/*     */   public void endValidate() {}
/*     */   
/*     */   void invalidate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
/*     */   
/*     */   public void addDropTarget(DropTarget paramDropTarget) {}
/*     */   
/*     */   public void removeDropTarget(DropTarget paramDropTarget) {}
/*     */   
/*     */   public void updateFocusableWindowState() {}
/*     */   
/*     */   public void setZOrder(ComponentPeer paramComponentPeer) {}
/*     */   
/*     */   private static native void initIDs();
/*     */   
/*     */   public void applyShape(Region paramRegion) {}
/*     */   
/*     */   public void setOpacity(float paramFloat) {}
/*     */   
/*     */   public void setOpaque(boolean paramBoolean) {}
/*     */   
/*     */   public void updateWindow(BufferedImage paramBufferedImage) {}
/*     */   
/*     */   public void createScreenSurface(boolean paramBoolean) {}
/*     */   
/*     */   public void replaceSurfaceData() {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WFileDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
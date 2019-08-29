/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.CheckboxMenuItem;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Component;
/*     */ import java.awt.Desktop;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dialog.ModalExclusionType;
/*     */ import java.awt.Dialog.ModalityType;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.HeadlessException;
/*     */ import java.awt.Image;
/*     */ import java.awt.JobAttributes;
/*     */ import java.awt.Label;
/*     */ import java.awt.List;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.PageAttributes;
/*     */ import java.awt.Point;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.PrintJob;
/*     */ import java.awt.Robot;
/*     */ import java.awt.ScrollPane;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.SystemTray;
/*     */ import java.awt.TextField;
/*     */ import java.awt.TrayIcon;
/*     */ import java.awt.Window;
/*     */ import java.awt.dnd.DragGestureEvent;
/*     */ import java.awt.dnd.DragGestureListener;
/*     */ import java.awt.dnd.DragSource;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.awt.im.InputMethodHighlight;
/*     */ import java.awt.im.spi.InputMethodDescriptor;
/*     */ import java.awt.peer.FontPeer;
/*     */ import java.awt.peer.FramePeer;
/*     */ import java.awt.peer.KeyboardFocusManagerPeer;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class HToolkit extends SunToolkit implements ComponentFactory
/*     */ {
/*  49 */   private static final KeyboardFocusManagerPeer kfmPeer = new KeyboardFocusManagerPeer() { public void setCurrentFocusedWindow(Window paramAnonymousWindow) {}
/*     */     
/*  51 */     public Window getCurrentFocusedWindow() { return null; }
/*     */     public void setCurrentFocusOwner(Component paramAnonymousComponent) {}
/*  53 */     public Component getCurrentFocusOwner() { return null; }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void clearGlobalFocusOwner(Window paramAnonymousWindow) {}
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */   public java.awt.peer.WindowPeer createWindow(Window paramWindow)
/*     */     throws HeadlessException
/*     */   {
/*  66 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public FramePeer createLightweightFrame(LightweightFrame paramLightweightFrame) throws HeadlessException
/*     */   {
/*  71 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public FramePeer createFrame(Frame paramFrame) throws HeadlessException
/*     */   {
/*  76 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.DialogPeer createDialog(Dialog paramDialog) throws HeadlessException
/*     */   {
/*  81 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ButtonPeer createButton(Button paramButton) throws HeadlessException
/*     */   {
/*  86 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.TextFieldPeer createTextField(TextField paramTextField) throws HeadlessException
/*     */   {
/*  91 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ChoicePeer createChoice(Choice paramChoice) throws HeadlessException
/*     */   {
/*  96 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.LabelPeer createLabel(Label paramLabel) throws HeadlessException
/*     */   {
/* 101 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ListPeer createList(List paramList) throws HeadlessException
/*     */   {
/* 106 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.CheckboxPeer createCheckbox(Checkbox paramCheckbox) throws HeadlessException
/*     */   {
/* 111 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) throws HeadlessException
/*     */   {
/* 116 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ScrollPanePeer createScrollPane(ScrollPane paramScrollPane) throws HeadlessException
/*     */   {
/* 121 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.TextAreaPeer createTextArea(java.awt.TextArea paramTextArea) throws HeadlessException
/*     */   {
/* 126 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.FileDialogPeer createFileDialog(FileDialog paramFileDialog) throws HeadlessException
/*     */   {
/* 131 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.MenuBarPeer createMenuBar(MenuBar paramMenuBar) throws HeadlessException
/*     */   {
/* 136 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.MenuPeer createMenu(Menu paramMenu) throws HeadlessException
/*     */   {
/* 141 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu) throws HeadlessException
/*     */   {
/* 146 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.MenuItemPeer createMenuItem(MenuItem paramMenuItem) throws HeadlessException
/*     */   {
/* 151 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem) throws HeadlessException
/*     */   {
/* 156 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.dnd.peer.DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*     */     throws InvalidDnDOperationException
/*     */   {
/* 162 */     throw new InvalidDnDOperationException("Headless environment");
/*     */   }
/*     */   
/*     */   public java.awt.peer.RobotPeer createRobot(Robot paramRobot, java.awt.GraphicsDevice paramGraphicsDevice) throws AWTException, HeadlessException
/*     */   {
/* 167 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer()
/*     */   {
/* 172 */     return kfmPeer;
/*     */   }
/*     */   
/*     */   public java.awt.peer.TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon) throws HeadlessException
/*     */   {
/* 177 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.SystemTrayPeer createSystemTray(SystemTray paramSystemTray) throws HeadlessException
/*     */   {
/* 182 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public boolean isTraySupported() {
/* 186 */     return false;
/*     */   }
/*     */   
/*     */   public sun.awt.datatransfer.DataTransferer getDataTransferer()
/*     */   {
/* 191 */     return null;
/*     */   }
/*     */   
/*     */   public GlobalCursorManager getGlobalCursorManager() throws HeadlessException
/*     */   {
/* 196 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void loadSystemColors(int[] paramArrayOfInt)
/*     */     throws HeadlessException
/*     */   {
/* 204 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.image.ColorModel getColorModel() throws HeadlessException
/*     */   {
/* 209 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public int getScreenResolution() throws HeadlessException
/*     */   {
/* 214 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.util.Map mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) throws HeadlessException
/*     */   {
/* 219 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public int getMenuShortcutKeyMask() throws HeadlessException
/*     */   {
/* 224 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public boolean getLockingKeyState(int paramInt) throws UnsupportedOperationException
/*     */   {
/* 229 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public void setLockingKeyState(int paramInt, boolean paramBoolean) throws UnsupportedOperationException
/*     */   {
/* 234 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString) throws IndexOutOfBoundsException, HeadlessException
/*     */   {
/* 239 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public Dimension getBestCursorSize(int paramInt1, int paramInt2) throws HeadlessException
/*     */   {
/* 244 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public int getMaximumCursorColors() throws HeadlessException
/*     */   {
/* 249 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
/*     */   {
/* 257 */     return null;
/*     */   }
/*     */   
/*     */   public int getScreenHeight() throws HeadlessException
/*     */   {
/* 262 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public int getScreenWidth() throws HeadlessException
/*     */   {
/* 267 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public Dimension getScreenSize() throws HeadlessException
/*     */   {
/* 272 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration) throws HeadlessException
/*     */   {
/* 277 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public void setDynamicLayout(boolean paramBoolean) throws HeadlessException
/*     */   {
/* 282 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   protected boolean isDynamicLayoutSet() throws HeadlessException
/*     */   {
/* 287 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public boolean isDynamicLayoutActive() throws HeadlessException
/*     */   {
/* 292 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.datatransfer.Clipboard getSystemClipboard() throws HeadlessException
/*     */   {
/* 297 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
/*     */   {
/* 306 */     if (paramFrame != null)
/*     */     {
/* 308 */       throw new HeadlessException();
/*     */     }
/* 310 */     throw new IllegalArgumentException("PrintJob not supported in a headless environment");
/*     */   }
/*     */   
/*     */ 
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties)
/*     */   {
/* 316 */     if (paramFrame != null)
/*     */     {
/* 318 */       throw new HeadlessException();
/*     */     }
/* 320 */     throw new IllegalArgumentException("PrintJob not supported in a headless environment");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sync() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean syncNativeQueue(long paramLong)
/*     */   {
/* 333 */     return false;
/*     */   }
/*     */   
/*     */   public void beep()
/*     */   {
/* 338 */     System.out.write(7);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public FontPeer getFontPeer(String paramString, int paramInt)
/*     */   {
/* 346 */     return (FontPeer)null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isModalityTypeSupported(ModalityType paramModalityType)
/*     */   {
/* 353 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isModalExclusionTypeSupported(ModalExclusionType paramModalExclusionType) {
/* 357 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isDesktopSupported() {
/* 361 */     return false;
/*     */   }
/*     */   
/*     */   public java.awt.peer.DesktopPeer createDesktopPeer(Desktop paramDesktop) throws HeadlessException
/*     */   {
/* 366 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public boolean isWindowOpacityControlSupported() {
/* 370 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isWindowShapingSupported() {
/* 374 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isWindowTranslucencySupported() {
/* 378 */     return false;
/*     */   }
/*     */   
/*     */   public void grab(Window paramWindow) {}
/*     */   
/*     */   public void ungrab(Window paramWindow) {}
/*     */   
/* 385 */   protected boolean syncNativeQueue() { return false; }
/*     */   
/*     */   public InputMethodDescriptor getInputMethodAdapterDescriptor()
/*     */     throws AWTException
/*     */   {
/* 390 */     return (InputMethodDescriptor)null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\HToolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
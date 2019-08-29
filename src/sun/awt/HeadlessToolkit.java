/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Component;
/*     */ import java.awt.Desktop;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dialog.ModalExclusionType;
/*     */ import java.awt.Dialog.ModalityType;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.HeadlessException;
/*     */ import java.awt.Image;
/*     */ import java.awt.Label;
/*     */ import java.awt.List;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.PageAttributes;
/*     */ import java.awt.Point;
/*     */ import java.awt.PrintJob;
/*     */ import java.awt.Robot;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.SystemTray;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.TrayIcon;
/*     */ import java.awt.Window;
/*     */ import java.awt.dnd.DragGestureEvent;
/*     */ import java.awt.event.AWTEventListener;
/*     */ import java.awt.im.InputMethodHighlight;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.image.ImageProducer;
/*     */ import java.awt.peer.CanvasPeer;
/*     */ import java.awt.peer.KeyboardFocusManagerPeer;
/*     */ import java.awt.peer.PanelPeer;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.net.URL;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class HeadlessToolkit extends Toolkit implements ComponentFactory, KeyboardFocusManagerPeerProvider
/*     */ {
/*  46 */   private static final KeyboardFocusManagerPeer kfmPeer = new KeyboardFocusManagerPeer() { public void setCurrentFocusedWindow(Window paramAnonymousWindow) {}
/*     */     
/*  48 */     public Window getCurrentFocusedWindow() { return null; }
/*     */     public void setCurrentFocusOwner(Component paramAnonymousComponent) {}
/*  50 */     public Component getCurrentFocusOwner() { return null; }
/*     */     
/*     */     public void clearGlobalFocusOwner(Window paramAnonymousWindow) {}
/*     */   };
/*     */   private Toolkit tk;
/*     */   private ComponentFactory componentFactory;
/*     */   
/*     */   public HeadlessToolkit(Toolkit paramToolkit) {
/*  58 */     this.tk = paramToolkit;
/*  59 */     if ((paramToolkit instanceof ComponentFactory)) {
/*  60 */       this.componentFactory = ((ComponentFactory)paramToolkit);
/*     */     }
/*     */   }
/*     */   
/*     */   public Toolkit getUnderlyingToolkit() {
/*  65 */     return this.tk;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CanvasPeer createCanvas(java.awt.Canvas paramCanvas)
/*     */   {
/*  75 */     return (CanvasPeer)createComponent(paramCanvas);
/*     */   }
/*     */   
/*     */   public PanelPeer createPanel(java.awt.Panel paramPanel) {
/*  79 */     return (PanelPeer)createComponent(paramPanel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public java.awt.peer.WindowPeer createWindow(Window paramWindow)
/*     */     throws HeadlessException
/*     */   {
/*  88 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.FramePeer createFrame(Frame paramFrame) throws HeadlessException
/*     */   {
/*  93 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.DialogPeer createDialog(Dialog paramDialog) throws HeadlessException
/*     */   {
/*  98 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ButtonPeer createButton(Button paramButton) throws HeadlessException
/*     */   {
/* 103 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.TextFieldPeer createTextField(java.awt.TextField paramTextField) throws HeadlessException
/*     */   {
/* 108 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ChoicePeer createChoice(Choice paramChoice) throws HeadlessException
/*     */   {
/* 113 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.LabelPeer createLabel(Label paramLabel) throws HeadlessException
/*     */   {
/* 118 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ListPeer createList(List paramList) throws HeadlessException
/*     */   {
/* 123 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.CheckboxPeer createCheckbox(Checkbox paramCheckbox) throws HeadlessException
/*     */   {
/* 128 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) throws HeadlessException
/*     */   {
/* 133 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.ScrollPanePeer createScrollPane(java.awt.ScrollPane paramScrollPane) throws HeadlessException
/*     */   {
/* 138 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.TextAreaPeer createTextArea(TextArea paramTextArea) throws HeadlessException
/*     */   {
/* 143 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.FileDialogPeer createFileDialog(FileDialog paramFileDialog) throws HeadlessException
/*     */   {
/* 148 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.MenuBarPeer createMenuBar(MenuBar paramMenuBar) throws HeadlessException
/*     */   {
/* 153 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.MenuPeer createMenu(Menu paramMenu) throws HeadlessException
/*     */   {
/* 158 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.PopupMenuPeer createPopupMenu(java.awt.PopupMenu paramPopupMenu) throws HeadlessException
/*     */   {
/* 163 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.MenuItemPeer createMenuItem(java.awt.MenuItem paramMenuItem) throws HeadlessException
/*     */   {
/* 168 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.CheckboxMenuItemPeer createCheckboxMenuItem(java.awt.CheckboxMenuItem paramCheckboxMenuItem) throws HeadlessException
/*     */   {
/* 173 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.dnd.peer.DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*     */     throws java.awt.dnd.InvalidDnDOperationException
/*     */   {
/* 179 */     throw new java.awt.dnd.InvalidDnDOperationException("Headless environment");
/*     */   }
/*     */   
/*     */   public java.awt.peer.RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice) throws java.awt.AWTException, HeadlessException
/*     */   {
/* 184 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer()
/*     */   {
/* 189 */     return kfmPeer;
/*     */   }
/*     */   
/*     */   public java.awt.peer.TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon) throws HeadlessException
/*     */   {
/* 194 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.peer.SystemTrayPeer createSystemTray(SystemTray paramSystemTray) throws HeadlessException
/*     */   {
/* 199 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public boolean isTraySupported() {
/* 203 */     return false;
/*     */   }
/*     */   
/*     */   public GlobalCursorManager getGlobalCursorManager() throws HeadlessException
/*     */   {
/* 208 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void loadSystemColors(int[] paramArrayOfInt)
/*     */     throws HeadlessException
/*     */   {
/* 216 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.image.ColorModel getColorModel() throws HeadlessException
/*     */   {
/* 221 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public int getScreenResolution() throws HeadlessException
/*     */   {
/* 226 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.util.Map mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) throws HeadlessException
/*     */   {
/* 231 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public int getMenuShortcutKeyMask() throws HeadlessException
/*     */   {
/* 236 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public boolean getLockingKeyState(int paramInt) throws UnsupportedOperationException
/*     */   {
/* 241 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public void setLockingKeyState(int paramInt, boolean paramBoolean) throws UnsupportedOperationException
/*     */   {
/* 246 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString) throws IndexOutOfBoundsException, HeadlessException
/*     */   {
/* 251 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public Dimension getBestCursorSize(int paramInt1, int paramInt2) throws HeadlessException
/*     */   {
/* 256 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public int getMaximumCursorColors() throws HeadlessException
/*     */   {
/* 261 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, java.awt.dnd.DragSource paramDragSource, Component paramComponent, int paramInt, java.awt.dnd.DragGestureListener paramDragGestureListener)
/*     */   {
/* 269 */     return null;
/*     */   }
/*     */   
/*     */   public int getScreenHeight() throws HeadlessException
/*     */   {
/* 274 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public int getScreenWidth() throws HeadlessException
/*     */   {
/* 279 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public Dimension getScreenSize() throws HeadlessException
/*     */   {
/* 284 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.Insets getScreenInsets(java.awt.GraphicsConfiguration paramGraphicsConfiguration) throws HeadlessException
/*     */   {
/* 289 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public void setDynamicLayout(boolean paramBoolean) throws HeadlessException
/*     */   {
/* 294 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   protected boolean isDynamicLayoutSet() throws HeadlessException
/*     */   {
/* 299 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public boolean isDynamicLayoutActive() throws HeadlessException
/*     */   {
/* 304 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public java.awt.datatransfer.Clipboard getSystemClipboard() throws HeadlessException
/*     */   {
/* 309 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, java.awt.JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
/*     */   {
/* 318 */     if (paramFrame != null)
/*     */     {
/* 320 */       throw new HeadlessException();
/*     */     }
/* 322 */     throw new NullPointerException("frame must not be null");
/*     */   }
/*     */   
/*     */   public PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties)
/*     */   {
/* 327 */     if (paramFrame != null)
/*     */     {
/* 329 */       throw new HeadlessException();
/*     */     }
/* 331 */     throw new NullPointerException("frame must not be null");
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
/*     */   public void beep()
/*     */   {
/* 344 */     System.out.write(7);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public java.awt.EventQueue getSystemEventQueueImpl()
/*     */   {
/* 351 */     return SunToolkit.getSystemEventQueueImplPP();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*     */   {
/* 358 */     return this.tk.checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*     */   }
/*     */   
/*     */   public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
/*     */   {
/* 363 */     return this.tk.prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
/*     */   }
/*     */   
/*     */   public Image getImage(String paramString) {
/* 367 */     return this.tk.getImage(paramString);
/*     */   }
/*     */   
/*     */   public Image getImage(URL paramURL) {
/* 371 */     return this.tk.getImage(paramURL);
/*     */   }
/*     */   
/*     */   public Image createImage(String paramString) {
/* 375 */     return this.tk.createImage(paramString);
/*     */   }
/*     */   
/*     */   public Image createImage(URL paramURL) {
/* 379 */     return this.tk.createImage(paramURL);
/*     */   }
/*     */   
/*     */   public Image createImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/* 383 */     return this.tk.createImage(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */   public Image createImage(ImageProducer paramImageProducer) {
/* 387 */     return this.tk.createImage(paramImageProducer);
/*     */   }
/*     */   
/*     */   public Image createImage(byte[] paramArrayOfByte) {
/* 391 */     return this.tk.createImage(paramArrayOfByte);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public java.awt.peer.FontPeer getFontPeer(String paramString, int paramInt)
/*     */   {
/* 400 */     if (this.componentFactory != null) {
/* 401 */       return this.componentFactory.getFontPeer(paramString, paramInt);
/*     */     }
/* 403 */     return null;
/*     */   }
/*     */   
/*     */   public sun.awt.datatransfer.DataTransferer getDataTransferer()
/*     */   {
/* 408 */     return null;
/*     */   }
/*     */   
/*     */   public java.awt.FontMetrics getFontMetrics(Font paramFont)
/*     */   {
/* 413 */     return this.tk.getFontMetrics(paramFont);
/*     */   }
/*     */   
/*     */   public String[] getFontList()
/*     */   {
/* 418 */     return this.tk.getFontList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 427 */     this.tk.addPropertyChangeListener(paramString, paramPropertyChangeListener);
/*     */   }
/*     */   
/*     */   public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
/*     */   {
/* 432 */     this.tk.removePropertyChangeListener(paramString, paramPropertyChangeListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isModalityTypeSupported(ModalityType paramModalityType)
/*     */   {
/* 439 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isModalExclusionTypeSupported(ModalExclusionType paramModalExclusionType) {
/* 443 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAlwaysOnTopSupported()
/*     */   {
/* 450 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addAWTEventListener(AWTEventListener paramAWTEventListener, long paramLong)
/*     */   {
/* 459 */     this.tk.addAWTEventListener(paramAWTEventListener, paramLong);
/*     */   }
/*     */   
/*     */   public void removeAWTEventListener(AWTEventListener paramAWTEventListener) {
/* 463 */     this.tk.removeAWTEventListener(paramAWTEventListener);
/*     */   }
/*     */   
/*     */   public AWTEventListener[] getAWTEventListeners() {
/* 467 */     return this.tk.getAWTEventListeners();
/*     */   }
/*     */   
/*     */   public AWTEventListener[] getAWTEventListeners(long paramLong) {
/* 471 */     return this.tk.getAWTEventListeners(paramLong);
/*     */   }
/*     */   
/*     */   public boolean isDesktopSupported() {
/* 475 */     return false;
/*     */   }
/*     */   
/*     */   public java.awt.peer.DesktopPeer createDesktopPeer(Desktop paramDesktop) throws HeadlessException
/*     */   {
/* 480 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
/* 484 */     throw new HeadlessException();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\HeadlessToolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
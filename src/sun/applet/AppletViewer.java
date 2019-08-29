/*      */ package sun.applet;
/*      */ 
/*      */ import java.applet.Applet;
/*      */ import java.applet.AppletContext;
/*      */ import java.applet.AudioClip;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FileDialog;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Label;
/*      */ import java.awt.Menu;
/*      */ import java.awt.MenuBar;
/*      */ import java.awt.MenuItem;
/*      */ import java.awt.Point;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.print.PageFormat;
/*      */ import java.awt.print.Printable;
/*      */ import java.awt.print.PrinterException;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Reader;
/*      */ import java.net.SocketPermission;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.security.AccessController;
/*      */ import java.security.Permission;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Vector;
/*      */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.misc.Ref;
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
/*      */ 
/*      */ public class AppletViewer
/*      */   extends Frame
/*      */   implements AppletContext, Printable
/*      */ {
/*  124 */   private static String defaultSaveFile = "Applet.ser";
/*      */   
/*      */ 
/*      */ 
/*      */   AppletViewerPanel panel;
/*      */   
/*      */ 
/*      */ 
/*      */   Label label;
/*      */   
/*      */ 
/*      */   PrintStream statusMsgStream;
/*      */   
/*      */ 
/*      */   AppletViewerFactory factory;
/*      */   
/*      */ 
/*      */ 
/*      */   private final class UserActionListener
/*      */     implements ActionListener
/*      */   {
/*      */     private UserActionListener() {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/*  151 */       AppletViewer.this.processUserAction(paramActionEvent);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public AppletViewer(int paramInt1, int paramInt2, URL paramURL, Hashtable paramHashtable, PrintStream paramPrintStream, AppletViewerFactory paramAppletViewerFactory)
/*      */   {
/*  160 */     this.factory = paramAppletViewerFactory;
/*  161 */     this.statusMsgStream = paramPrintStream;
/*  162 */     setTitle(amh.getMessage("tool.title", paramHashtable.get("code")));
/*      */     
/*  164 */     MenuBar localMenuBar = paramAppletViewerFactory.getBaseMenuBar();
/*      */     
/*  166 */     Menu localMenu = new Menu(amh.getMessage("menu.applet"));
/*      */     
/*  168 */     addMenuItem(localMenu, "menuitem.restart");
/*  169 */     addMenuItem(localMenu, "menuitem.reload");
/*  170 */     addMenuItem(localMenu, "menuitem.stop");
/*  171 */     addMenuItem(localMenu, "menuitem.save");
/*  172 */     addMenuItem(localMenu, "menuitem.start");
/*  173 */     addMenuItem(localMenu, "menuitem.clone");
/*  174 */     localMenu.add(new MenuItem("-"));
/*  175 */     addMenuItem(localMenu, "menuitem.tag");
/*  176 */     addMenuItem(localMenu, "menuitem.info");
/*  177 */     addMenuItem(localMenu, "menuitem.edit").disable();
/*  178 */     addMenuItem(localMenu, "menuitem.encoding");
/*  179 */     localMenu.add(new MenuItem("-"));
/*  180 */     addMenuItem(localMenu, "menuitem.print");
/*  181 */     localMenu.add(new MenuItem("-"));
/*  182 */     addMenuItem(localMenu, "menuitem.props");
/*  183 */     localMenu.add(new MenuItem("-"));
/*  184 */     addMenuItem(localMenu, "menuitem.close");
/*  185 */     if (paramAppletViewerFactory.isStandalone()) {
/*  186 */       addMenuItem(localMenu, "menuitem.quit");
/*      */     }
/*      */     
/*  189 */     localMenuBar.add(localMenu);
/*      */     
/*  191 */     setMenuBar(localMenuBar);
/*      */     
/*  193 */     add("Center", this.panel = new AppletViewerPanel(paramURL, paramHashtable));
/*  194 */     add("South", this.label = new Label(amh.getMessage("label.hello")));
/*  195 */     this.panel.init();
/*  196 */     appletPanels.addElement(this.panel);
/*      */     
/*  198 */     pack();
/*  199 */     move(paramInt1, paramInt2);
/*  200 */     setVisible(true);
/*      */     
/*  202 */     WindowAdapter local1 = new WindowAdapter()
/*      */     {
/*      */       public void windowClosing(WindowEvent paramAnonymousWindowEvent)
/*      */       {
/*  206 */         AppletViewer.this.appletClose();
/*      */       }
/*      */       
/*      */       public void windowIconified(WindowEvent paramAnonymousWindowEvent)
/*      */       {
/*  211 */         AppletViewer.this.appletStop();
/*      */       }
/*      */       
/*      */       public void windowDeiconified(WindowEvent paramAnonymousWindowEvent)
/*      */       {
/*  216 */         AppletViewer.this.appletStart();
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
/*      */       }
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
/*  269 */     };
/*  270 */     addWindowListener(local1);
/*  271 */     this.panel.addAppletListener(new AppletListener()
/*      */     {
/*      */       final Frame frame;
/*      */       
/*      */       public void appletStateChanged(AppletEvent paramAnonymousAppletEvent)
/*      */       {
/*  232 */         AppletPanel localAppletPanel = (AppletPanel)paramAnonymousAppletEvent.getSource();
/*      */         
/*  234 */         switch (paramAnonymousAppletEvent.getID()) {
/*      */         case 51234: 
/*  236 */           if (localAppletPanel != null) {
/*  237 */             AppletViewer.this.resize(AppletViewer.this.preferredSize());
/*  238 */             AppletViewer.this.validate();
/*      */           }
/*      */           
/*      */           break;
/*      */         case 51236: 
/*  243 */           Applet localApplet = localAppletPanel.getApplet();
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
/*  259 */           if (localApplet != null) {
/*  260 */             AppletPanel.changeFrameAppContext(this.frame, SunToolkit.targetToAppContext(localApplet));
/*      */           } else {
/*  262 */             AppletPanel.changeFrameAppContext(this.frame, AppContext.getAppContext());
/*      */           }
/*  264 */           break;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */       }
/*  273 */     });
/*  274 */     showStatus(amh.getMessage("status.start"));
/*  275 */     initEventQueue();
/*      */   }
/*      */   
/*      */   public MenuItem addMenuItem(Menu paramMenu, String paramString)
/*      */   {
/*  280 */     MenuItem localMenuItem = new MenuItem(amh.getMessage(paramString));
/*  281 */     localMenuItem.addActionListener(new UserActionListener(null));
/*  282 */     return paramMenu.add(localMenuItem);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initEventQueue()
/*      */   {
/*  293 */     String str = System.getProperty("appletviewer.send.event");
/*      */     
/*  295 */     if (str == null)
/*      */     {
/*  297 */       this.panel.sendEvent(1);
/*  298 */       this.panel.sendEvent(2);
/*  299 */       this.panel.sendEvent(3);
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*  307 */       String[] arrayOfString = splitSeparator(",", str);
/*      */       
/*  309 */       int i = 0; if (i < arrayOfString.length) {
/*  310 */         System.out.println("Adding event to queue: " + arrayOfString[i]);
/*  311 */         if (arrayOfString[i].equals("dispose")) {
/*  312 */           this.panel.sendEvent(0);
/*  313 */         } else if (arrayOfString[i].equals("load")) {
/*  314 */           this.panel.sendEvent(1);
/*  315 */         } else if (arrayOfString[i].equals("init")) {
/*  316 */           this.panel.sendEvent(2);
/*  317 */         } else if (arrayOfString[i].equals("start")) {
/*  318 */           this.panel.sendEvent(3);
/*  319 */         } else if (arrayOfString[i].equals("stop")) {
/*  320 */           this.panel.sendEvent(4);
/*  321 */         } else if (arrayOfString[i].equals("destroy")) {
/*  322 */           this.panel.sendEvent(5);
/*  323 */         } else if (arrayOfString[i].equals("quit")) {
/*  324 */           this.panel.sendEvent(6);
/*  325 */         } else if (arrayOfString[i].equals("error")) {
/*  326 */           this.panel.sendEvent(7);
/*      */         }
/*      */         else {
/*  329 */           System.out.println("Unrecognized event name: " + arrayOfString[i]);
/*      */         }
/*  309 */         i++;
/*      */       }
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
/*  332 */       while (!this.panel.emptyEventQueue()) {}
/*  333 */       appletSystemExit();
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
/*      */   private String[] splitSeparator(String paramString1, String paramString2)
/*      */   {
/*  352 */     Vector localVector = new Vector();
/*  353 */     int i = 0;
/*  354 */     int j = 0;
/*      */     
/*  356 */     while ((j = paramString2.indexOf(paramString1, i)) != -1) {
/*  357 */       localVector.addElement(paramString2.substring(i, j));
/*  358 */       i = j + 1;
/*      */     }
/*      */     
/*  361 */     localVector.addElement(paramString2.substring(i));
/*      */     
/*  363 */     String[] arrayOfString = new String[localVector.size()];
/*  364 */     localVector.copyInto(arrayOfString);
/*  365 */     return arrayOfString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  372 */   private static Map audioClips = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public AudioClip getAudioClip(URL paramURL)
/*      */   {
/*  379 */     checkConnect(paramURL);
/*  380 */     synchronized (audioClips) {
/*  381 */       Object localObject1 = (AudioClip)audioClips.get(paramURL);
/*  382 */       if (localObject1 == null) {
/*  383 */         audioClips.put(paramURL, localObject1 = new AppletAudioClip(paramURL));
/*      */       }
/*  385 */       return (AudioClip)localObject1;
/*      */     }
/*      */   }
/*      */   
/*  389 */   private static Map imageRefs = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Image getImage(URL paramURL)
/*      */   {
/*  396 */     return getCachedImage(paramURL);
/*      */   }
/*      */   
/*      */   static Image getCachedImage(URL paramURL)
/*      */   {
/*  401 */     return (Image)getCachedImageRef(paramURL).get();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static Ref getCachedImageRef(URL paramURL)
/*      */   {
/*  408 */     synchronized (imageRefs) {
/*  409 */       AppletImageRef localAppletImageRef = (AppletImageRef)imageRefs.get(paramURL);
/*  410 */       if (localAppletImageRef == null) {
/*  411 */         localAppletImageRef = new AppletImageRef(paramURL);
/*  412 */         imageRefs.put(paramURL, localAppletImageRef);
/*      */       }
/*  414 */       return localAppletImageRef;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static void flushImageCache()
/*      */   {
/*  422 */     imageRefs.clear();
/*      */   }
/*      */   
/*  425 */   static Vector appletPanels = new Vector();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Applet getApplet(String paramString)
/*      */   {
/*  432 */     AppletSecurity localAppletSecurity = (AppletSecurity)System.getSecurityManager();
/*  433 */     paramString = paramString.toLowerCase();
/*      */     
/*  435 */     SocketPermission localSocketPermission1 = new SocketPermission(this.panel.getCodeBase().getHost(), "connect");
/*  436 */     for (Enumeration localEnumeration = appletPanels.elements(); localEnumeration.hasMoreElements();) {
/*  437 */       AppletPanel localAppletPanel = (AppletPanel)localEnumeration.nextElement();
/*  438 */       String str = localAppletPanel.getParameter("name");
/*  439 */       if (str != null) {
/*  440 */         str = str.toLowerCase();
/*      */       }
/*  442 */       if ((paramString.equals(str)) && 
/*  443 */         (localAppletPanel.getDocumentBase().equals(this.panel.getDocumentBase())))
/*      */       {
/*      */ 
/*  446 */         SocketPermission localSocketPermission2 = new SocketPermission(localAppletPanel.getCodeBase().getHost(), "connect");
/*      */         
/*  448 */         if (localSocketPermission1.implies(localSocketPermission2)) {
/*  449 */           return localAppletPanel.applet;
/*      */         }
/*      */       }
/*      */     }
/*  453 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Enumeration getApplets()
/*      */   {
/*  462 */     AppletSecurity localAppletSecurity = (AppletSecurity)System.getSecurityManager();
/*  463 */     Vector localVector = new Vector();
/*      */     
/*  465 */     SocketPermission localSocketPermission1 = new SocketPermission(this.panel.getCodeBase().getHost(), "connect");
/*      */     
/*  467 */     for (Enumeration localEnumeration = appletPanels.elements(); localEnumeration.hasMoreElements();) {
/*  468 */       AppletPanel localAppletPanel = (AppletPanel)localEnumeration.nextElement();
/*  469 */       if (localAppletPanel.getDocumentBase().equals(this.panel.getDocumentBase()))
/*      */       {
/*      */ 
/*  472 */         SocketPermission localSocketPermission2 = new SocketPermission(localAppletPanel.getCodeBase().getHost(), "connect");
/*  473 */         if (localSocketPermission1.implies(localSocketPermission2)) {
/*  474 */           localVector.addElement(localAppletPanel.applet);
/*      */         }
/*      */       }
/*      */     }
/*  478 */     return localVector.elements();
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
/*      */   public void showStatus(String paramString)
/*      */   {
/*  500 */     this.label.setText(paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public InputStream getStream(String paramString)
/*      */   {
/*  511 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Iterator getStreamKeys()
/*      */   {
/*  517 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  523 */   static Hashtable systemParam = new Hashtable();
/*      */   static AppletProps props;
/*      */   
/*  526 */   static { systemParam.put("codebase", "codebase");
/*  527 */     systemParam.put("code", "code");
/*  528 */     systemParam.put("alt", "alt");
/*  529 */     systemParam.put("width", "width");
/*  530 */     systemParam.put("height", "height");
/*  531 */     systemParam.put("align", "align");
/*  532 */     systemParam.put("vspace", "vspace");
/*  533 */     systemParam.put("hspace", "hspace");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void printTag(PrintStream paramPrintStream, Hashtable paramHashtable)
/*      */   {
/*  540 */     paramPrintStream.print("<applet");
/*      */     
/*  542 */     String str1 = (String)paramHashtable.get("codebase");
/*  543 */     if (str1 != null) {
/*  544 */       paramPrintStream.print(" codebase=\"" + str1 + "\"");
/*      */     }
/*      */     
/*  547 */     str1 = (String)paramHashtable.get("code");
/*  548 */     if (str1 == null) {
/*  549 */       str1 = "applet.class";
/*      */     }
/*  551 */     paramPrintStream.print(" code=\"" + str1 + "\"");
/*  552 */     str1 = (String)paramHashtable.get("width");
/*  553 */     if (str1 == null) {
/*  554 */       str1 = "150";
/*      */     }
/*  556 */     paramPrintStream.print(" width=" + str1);
/*      */     
/*  558 */     str1 = (String)paramHashtable.get("height");
/*  559 */     if (str1 == null) {
/*  560 */       str1 = "100";
/*      */     }
/*  562 */     paramPrintStream.print(" height=" + str1);
/*      */     
/*  564 */     str1 = (String)paramHashtable.get("name");
/*  565 */     if (str1 != null) {
/*  566 */       paramPrintStream.print(" name=\"" + str1 + "\"");
/*      */     }
/*  568 */     paramPrintStream.println(">");
/*      */     
/*      */ 
/*  571 */     int i = paramHashtable.size();
/*  572 */     String[] arrayOfString = new String[i];
/*  573 */     i = 0;
/*  574 */     for (Enumeration localEnumeration = paramHashtable.keys(); localEnumeration.hasMoreElements();) {
/*  575 */       str2 = (String)localEnumeration.nextElement();
/*  576 */       for (int k = 0; 
/*  577 */           k < i; k++) {
/*  578 */         if (arrayOfString[k].compareTo(str2) >= 0) {
/*      */           break;
/*      */         }
/*      */       }
/*  582 */       System.arraycopy(arrayOfString, k, arrayOfString, k + 1, i - k);
/*  583 */       arrayOfString[k] = str2;
/*  584 */       i++;
/*      */     }
/*      */     String str2;
/*  587 */     for (int j = 0; j < i; j++) {
/*  588 */       str2 = arrayOfString[j];
/*  589 */       if (systemParam.get(str2) == null) {
/*  590 */         paramPrintStream.println("<param name=" + str2 + " value=\"" + paramHashtable
/*  591 */           .get(str2) + "\">");
/*      */       }
/*      */     }
/*  594 */     paramPrintStream.println("</applet>");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void updateAtts()
/*      */   {
/*  601 */     Dimension localDimension = this.panel.size();
/*  602 */     Insets localInsets = this.panel.insets();
/*  603 */     this.panel.atts.put("width", 
/*  604 */       Integer.toString(localDimension.width - (localInsets.left + localInsets.right)));
/*  605 */     this.panel.atts.put("height", 
/*  606 */       Integer.toString(localDimension.height - (localInsets.top + localInsets.bottom)));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void appletRestart()
/*      */   {
/*  613 */     this.panel.sendEvent(4);
/*  614 */     this.panel.sendEvent(5);
/*  615 */     this.panel.sendEvent(2);
/*  616 */     this.panel.sendEvent(3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void appletReload()
/*      */   {
/*  623 */     this.panel.sendEvent(4);
/*  624 */     this.panel.sendEvent(5);
/*  625 */     this.panel.sendEvent(0);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  632 */     AppletPanel.flushClassLoader(this.panel.getClassLoaderCacheKey());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  639 */       this.panel.joinAppletThread();
/*  640 */       this.panel.release();
/*      */     } catch (InterruptedException localInterruptedException) {
/*  642 */       return;
/*      */     }
/*      */     
/*  645 */     this.panel.createAppletThread();
/*  646 */     this.panel.sendEvent(1);
/*  647 */     this.panel.sendEvent(2);
/*  648 */     this.panel.sendEvent(3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void appletSave()
/*      */   {
/*  655 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public Object run()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  669 */         AppletViewer.this.panel.sendEvent(4);
/*      */         
/*  671 */         FileDialog localFileDialog = new FileDialog(AppletViewer.this, AppletViewer.amh.getMessage("appletsave.filedialogtitle"), 1);
/*      */         
/*      */ 
/*  674 */         localFileDialog.setDirectory(System.getProperty("user.dir"));
/*  675 */         localFileDialog.setFile(AppletViewer.defaultSaveFile);
/*  676 */         localFileDialog.show();
/*  677 */         String str1 = localFileDialog.getFile();
/*  678 */         if (str1 == null)
/*      */         {
/*  680 */           AppletViewer.this.panel.sendEvent(3);
/*  681 */           return null;
/*      */         }
/*  683 */         String str2 = localFileDialog.getDirectory();
/*  684 */         File localFile = new File(str2, str1);
/*      */         try {
/*  686 */           FileOutputStream localFileOutputStream = new FileOutputStream(localFile);Object localObject1 = null;
/*  687 */           try { BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);Object localObject2 = null;
/*  688 */             try { ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localBufferedOutputStream);Object localObject3 = null;
/*      */               try {
/*  690 */                 AppletViewer.this.showStatus(AppletViewer.amh.getMessage("appletsave.err1", AppletViewer.this.panel.applet.toString(), localFile.toString()));
/*  691 */                 localObjectOutputStream.writeObject(AppletViewer.this.panel.applet);
/*      */               }
/*      */               catch (Throwable localThrowable6)
/*      */               {
/*  686 */                 localObject3 = localThrowable6;throw localThrowable6; } finally {} } catch (Throwable localThrowable4) { localObject2 = localThrowable4;throw localThrowable4; } finally {} } catch (Throwable localThrowable2) { localObject1 = localThrowable2;throw localThrowable2;
/*      */ 
/*      */           }
/*      */           finally
/*      */           {
/*      */ 
/*  692 */             if (localFileOutputStream != null) if (localObject1 != null) try { localFileOutputStream.close(); } catch (Throwable localThrowable9) { ((Throwable)localObject1).addSuppressed(localThrowable9); } else localFileOutputStream.close();
/*  693 */           } } catch (IOException localIOException) { System.err.println(AppletViewer.amh.getMessage("appletsave.err2", localIOException));
/*      */         } finally {
/*  695 */           AppletViewer.this.panel.sendEvent(3);
/*      */         }
/*  697 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void appletClone()
/*      */   {
/*  706 */     Point localPoint = location();
/*  707 */     updateAtts();
/*  708 */     this.factory.createAppletViewer(localPoint.x + 30, localPoint.y + 30, this.panel.documentURL, 
/*  709 */       (Hashtable)this.panel.atts.clone());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void appletTag()
/*      */   {
/*  716 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*  717 */     updateAtts();
/*  718 */     printTag(new PrintStream(localByteArrayOutputStream), this.panel.atts);
/*  719 */     showStatus(amh.getMessage("applettag"));
/*      */     
/*  721 */     Point localPoint = location();
/*  722 */     new TextFrame(localPoint.x + 30, localPoint.y + 30, amh.getMessage("applettag.textframe"), localByteArrayOutputStream.toString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void appletInfo()
/*      */   {
/*  729 */     String str = this.panel.applet.getAppletInfo();
/*  730 */     if (str == null) {
/*  731 */       str = amh.getMessage("appletinfo.applet");
/*      */     }
/*  733 */     str = str + "\n\n";
/*      */     
/*  735 */     String[][] arrayOfString = this.panel.applet.getParameterInfo();
/*  736 */     if (arrayOfString != null) {
/*  737 */       for (int i = 0; i < arrayOfString.length; i++) {
/*  738 */         str = str + arrayOfString[i][0] + " -- " + arrayOfString[i][1] + " -- " + arrayOfString[i][2] + "\n";
/*      */       }
/*      */     } else {
/*  741 */       str = str + amh.getMessage("appletinfo.param");
/*      */     }
/*      */     
/*  744 */     Point localPoint = location();
/*  745 */     new TextFrame(localPoint.x + 30, localPoint.y + 30, amh.getMessage("appletinfo.textframe"), str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void appletCharacterEncoding()
/*      */   {
/*  753 */     showStatus(amh.getMessage("appletencoding", encoding));
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
/*      */   void appletPrint()
/*      */   {
/*  766 */     PrinterJob localPrinterJob = PrinterJob.getPrinterJob();
/*      */     
/*  768 */     if (localPrinterJob != null) {
/*  769 */       HashPrintRequestAttributeSet localHashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
/*  770 */       if (localPrinterJob.printDialog(localHashPrintRequestAttributeSet)) {
/*  771 */         localPrinterJob.setPrintable(this);
/*      */         try {
/*  773 */           localPrinterJob.print(localHashPrintRequestAttributeSet);
/*  774 */           this.statusMsgStream.println(amh.getMessage("appletprint.finish"));
/*      */         } catch (PrinterException localPrinterException) {
/*  776 */           this.statusMsgStream.println(amh.getMessage("appletprint.fail"));
/*      */         }
/*      */       } else {
/*  779 */         this.statusMsgStream.println(amh.getMessage("appletprint.cancel"));
/*      */       }
/*      */     } else {
/*  782 */       this.statusMsgStream.println(amh.getMessage("appletprint.fail"));
/*      */     }
/*      */   }
/*      */   
/*      */   public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt)
/*      */   {
/*  788 */     if (paramInt > 0) {
/*  789 */       return 1;
/*      */     }
/*  791 */     Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
/*  792 */     localGraphics2D.translate(paramPageFormat.getImageableX(), paramPageFormat.getImageableY());
/*  793 */     this.panel.applet.printAll(paramGraphics);
/*  794 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static synchronized void networkProperties()
/*      */   {
/*  803 */     if (props == null) {
/*  804 */       props = new AppletProps();
/*      */     }
/*  806 */     props.addNotify();
/*  807 */     props.setVisible(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void appletStart()
/*      */   {
/*  814 */     this.panel.sendEvent(3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void appletStop()
/*      */   {
/*  821 */     this.panel.sendEvent(4);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void appletShutdown(AppletPanel paramAppletPanel)
/*      */   {
/*  829 */     paramAppletPanel.sendEvent(4);
/*  830 */     paramAppletPanel.sendEvent(5);
/*  831 */     paramAppletPanel.sendEvent(0);
/*  832 */     paramAppletPanel.sendEvent(6);
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
/*      */   void appletClose()
/*      */   {
/*  847 */     final AppletViewerPanel localAppletViewerPanel = this.panel;
/*      */     
/*  849 */     new Thread(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*  854 */         AppletViewer.this.appletShutdown(localAppletViewerPanel);
/*  855 */         AppletViewer.appletPanels.removeElement(localAppletViewerPanel);
/*  856 */         AppletViewer.this.dispose();
/*      */         
/*  858 */         if (AppletViewer.countApplets() == 0) {
/*  859 */           AppletViewer.this.appletSystemExit();
/*      */         }
/*      */       }
/*      */     })
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
/*  862 */       .start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void appletSystemExit()
/*      */   {
/*  870 */     if (this.factory.isStandalone()) {
/*  871 */       System.exit(0);
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
/*      */   static int c;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void appletQuit()
/*      */   {
/*  896 */     new Thread(new Runnable()
/*      */     {
/*      */       public void run()
/*      */       {
/*  890 */         for (Enumeration localEnumeration = AppletViewer.appletPanels.elements(); localEnumeration.hasMoreElements();) {
/*  891 */           AppletPanel localAppletPanel = (AppletPanel)localEnumeration.nextElement();
/*  892 */           AppletViewer.this.appletShutdown(localAppletPanel);
/*      */         }
/*  894 */         AppletViewer.this.appletSystemExit();
/*      */       }
/*      */     })
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
/*  896 */       .start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void processUserAction(ActionEvent paramActionEvent)
/*      */   {
/*  904 */     String str = ((MenuItem)paramActionEvent.getSource()).getLabel();
/*      */     
/*  906 */     if (amh.getMessage("menuitem.restart").equals(str)) {
/*  907 */       appletRestart();
/*  908 */       return;
/*      */     }
/*      */     
/*  911 */     if (amh.getMessage("menuitem.reload").equals(str)) {
/*  912 */       appletReload();
/*  913 */       return;
/*      */     }
/*      */     
/*  916 */     if (amh.getMessage("menuitem.clone").equals(str)) {
/*  917 */       appletClone();
/*  918 */       return;
/*      */     }
/*      */     
/*  921 */     if (amh.getMessage("menuitem.stop").equals(str)) {
/*  922 */       appletStop();
/*  923 */       return;
/*      */     }
/*      */     
/*  926 */     if (amh.getMessage("menuitem.save").equals(str)) {
/*  927 */       appletSave();
/*  928 */       return;
/*      */     }
/*      */     
/*  931 */     if (amh.getMessage("menuitem.start").equals(str)) {
/*  932 */       appletStart();
/*  933 */       return;
/*      */     }
/*      */     
/*  936 */     if (amh.getMessage("menuitem.tag").equals(str)) {
/*  937 */       appletTag();
/*  938 */       return;
/*      */     }
/*      */     
/*  941 */     if (amh.getMessage("menuitem.info").equals(str)) {
/*  942 */       appletInfo();
/*  943 */       return;
/*      */     }
/*      */     
/*  946 */     if (amh.getMessage("menuitem.encoding").equals(str)) {
/*  947 */       appletCharacterEncoding();
/*  948 */       return;
/*      */     }
/*      */     
/*  951 */     if (amh.getMessage("menuitem.edit").equals(str)) {
/*  952 */       appletEdit();
/*  953 */       return;
/*      */     }
/*      */     
/*  956 */     if (amh.getMessage("menuitem.print").equals(str)) {
/*  957 */       appletPrint();
/*  958 */       return;
/*      */     }
/*      */     
/*  961 */     if (amh.getMessage("menuitem.props").equals(str)) {
/*  962 */       networkProperties();
/*  963 */       return;
/*      */     }
/*      */     
/*  966 */     if (amh.getMessage("menuitem.close").equals(str)) {
/*  967 */       appletClose();
/*  968 */       return;
/*      */     }
/*      */     
/*  971 */     if ((this.factory.isStandalone()) && (amh.getMessage("menuitem.quit").equals(str))) {
/*  972 */       appletQuit();
/*  973 */       return;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int countApplets()
/*      */   {
/*  983 */     return appletPanels.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void skipSpace(Reader paramReader)
/*      */     throws IOException
/*      */   {
/*  996 */     while ((c >= 0) && ((c == 32) || (c == 9) || (c == 10) || (c == 13)))
/*      */     {
/*  998 */       c = paramReader.read();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static String scanIdentifier(Reader paramReader)
/*      */     throws IOException
/*      */   {
/* 1006 */     StringBuffer localStringBuffer = new StringBuffer();
/*      */     
/* 1008 */     while (((c >= 97) && (c <= 122)) || ((c >= 65) && (c <= 90)) || ((c >= 48) && (c <= 57)) || (c == 95))
/*      */     {
/*      */ 
/* 1011 */       localStringBuffer.append((char)c);
/* 1012 */       c = paramReader.read();
/*      */     }
/* 1014 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Hashtable scanTag(Reader paramReader)
/*      */     throws IOException
/*      */   {
/* 1023 */     Hashtable localHashtable = new Hashtable();
/* 1024 */     skipSpace(paramReader);
/* 1025 */     for (; (c >= 0) && (c != 62); 
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
/*      */ 
/*      */ 
/*      */ 
/* 1061 */         goto 230)
/*      */     {
/* 1026 */       String str1 = scanIdentifier(paramReader);
/* 1027 */       String str2 = "";
/* 1028 */       skipSpace(paramReader);
/* 1029 */       if (c == 61) {
/* 1030 */         int i = -1;
/* 1031 */         c = paramReader.read();
/* 1032 */         skipSpace(paramReader);
/* 1033 */         if ((c == 39) || (c == 34)) {
/* 1034 */           i = c;
/* 1035 */           c = paramReader.read();
/*      */         }
/* 1037 */         StringBuffer localStringBuffer = new StringBuffer();
/* 1038 */         while ((c > 0) && (((i < 0) && (c != 32) && (c != 9) && (c != 10) && (c != 13) && (c != 62)) || ((i >= 0) && (c != i))))
/*      */         {
/*      */ 
/*      */ 
/* 1042 */           localStringBuffer.append((char)c);
/* 1043 */           c = paramReader.read();
/*      */         }
/* 1045 */         if (c == i) {
/* 1046 */           c = paramReader.read();
/*      */         }
/* 1048 */         skipSpace(paramReader);
/* 1049 */         str2 = localStringBuffer.toString();
/*      */       }
/*      */       
/* 1052 */       if (!str2.equals("")) {
/* 1053 */         localHashtable.put(str1.toLowerCase(Locale.ENGLISH), str2);
/*      */       }
/*      */       
/* 1056 */       if ((c != 62) && (c >= 0) && ((c < 97) || (c > 122)) && ((c < 65) || (c > 90)) && ((c < 48) || (c > 57)) && (c != 95))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1061 */         c = paramReader.read();
/*      */       }
/*      */     }
/*      */     
/* 1065 */     return localHashtable;
/*      */   }
/*      */   
/*      */ 
/* 1069 */   private static int x = 0;
/* 1070 */   private static int y = 0;
/*      */   
/*      */   private static final int XDELTA = 30;
/*      */   private static final int YDELTA = 30;
/* 1074 */   static String encoding = null;
/*      */   
/*      */   private static Reader makeReader(InputStream paramInputStream) {
/* 1077 */     if (encoding != null) {
/*      */       try {
/* 1079 */         return new BufferedReader(new InputStreamReader(paramInputStream, encoding));
/*      */       } catch (IOException localIOException) {}
/*      */     }
/* 1082 */     InputStreamReader localInputStreamReader = new InputStreamReader(paramInputStream);
/* 1083 */     encoding = localInputStreamReader.getEncoding();
/* 1084 */     return new BufferedReader(localInputStreamReader);
/*      */   }
/*      */   
/*      */ 
/*      */   public static void parse(URL paramURL, String paramString)
/*      */     throws IOException
/*      */   {
/* 1091 */     encoding = paramString;
/* 1092 */     parse(paramURL, System.out, new StdAppletViewerFactory());
/*      */   }
/*      */   
/*      */   public static void parse(URL paramURL) throws IOException {
/* 1096 */     parse(paramURL, System.out, new StdAppletViewerFactory());
/*      */   }
/*      */   
/*      */   public static void parse(URL paramURL, PrintStream paramPrintStream, AppletViewerFactory paramAppletViewerFactory)
/*      */     throws IOException
/*      */   {
/* 1102 */     int i = 0;
/* 1103 */     int j = 0;
/* 1104 */     int k = 0;
/*      */     
/*      */ 
/* 1107 */     String str1 = amh.getMessage("parse.warning.requiresname");
/* 1108 */     String str2 = amh.getMessage("parse.warning.paramoutside");
/* 1109 */     String str3 = amh.getMessage("parse.warning.applet.requirescode");
/* 1110 */     String str4 = amh.getMessage("parse.warning.applet.requiresheight");
/* 1111 */     String str5 = amh.getMessage("parse.warning.applet.requireswidth");
/* 1112 */     String str6 = amh.getMessage("parse.warning.object.requirescode");
/* 1113 */     String str7 = amh.getMessage("parse.warning.object.requiresheight");
/* 1114 */     String str8 = amh.getMessage("parse.warning.object.requireswidth");
/* 1115 */     String str9 = amh.getMessage("parse.warning.embed.requirescode");
/* 1116 */     String str10 = amh.getMessage("parse.warning.embed.requiresheight");
/* 1117 */     String str11 = amh.getMessage("parse.warning.embed.requireswidth");
/* 1118 */     String str12 = amh.getMessage("parse.warning.appnotLongersupported");
/*      */     
/* 1120 */     URLConnection localURLConnection = paramURL.openConnection();
/* 1121 */     Reader localReader = makeReader(localURLConnection.getInputStream());
/*      */     
/*      */ 
/*      */ 
/* 1125 */     paramURL = localURLConnection.getURL();
/*      */     
/* 1127 */     int m = 1;
/* 1128 */     Hashtable localHashtable = null;
/*      */     for (;;)
/*      */     {
/* 1131 */       c = localReader.read();
/* 1132 */       if (c == -1) {
/*      */         break;
/*      */       }
/* 1135 */       if (c == 60) {
/* 1136 */         c = localReader.read();
/* 1137 */         String str13; Object localObject; if (c == 47) {
/* 1138 */           c = localReader.read();
/* 1139 */           str13 = scanIdentifier(localReader);
/* 1140 */           if ((str13.equalsIgnoreCase("applet")) || 
/* 1141 */             (str13.equalsIgnoreCase("object")) || 
/* 1142 */             (str13.equalsIgnoreCase("embed")))
/*      */           {
/*      */ 
/*      */ 
/* 1146 */             if ((j != 0) && 
/* 1147 */               (localHashtable.get("code") == null) && (localHashtable.get("object") == null)) {
/* 1148 */               paramPrintStream.println(str6);
/* 1149 */               localHashtable = null;
/*      */             }
/*      */             
/*      */ 
/* 1153 */             if (localHashtable != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1158 */               paramAppletViewerFactory.createAppletViewer(x, y, paramURL, localHashtable);
/* 1159 */               x += 30;
/* 1160 */               y += 30;
/*      */               
/* 1162 */               localObject = Toolkit.getDefaultToolkit().getScreenSize();
/* 1163 */               if ((x > ((Dimension)localObject).width - 300) || (y > ((Dimension)localObject).height - 300)) {
/* 1164 */                 x = 0;
/* 1165 */                 y = 2 * m * 30;
/* 1166 */                 m++;
/*      */               }
/*      */             }
/* 1169 */             localHashtable = null;
/* 1170 */             i = 0;
/* 1171 */             j = 0;
/* 1172 */             k = 0;
/*      */           }
/*      */         }
/*      */         else {
/* 1176 */           str13 = scanIdentifier(localReader);
/* 1177 */           if (str13.equalsIgnoreCase("param")) {
/* 1178 */             localObject = scanTag(localReader);
/* 1179 */             String str14 = (String)((Hashtable)localObject).get("name");
/* 1180 */             if (str14 == null) {
/* 1181 */               paramPrintStream.println(str1);
/*      */             } else {
/* 1183 */               String str15 = (String)((Hashtable)localObject).get("value");
/* 1184 */               if (str15 == null) {
/* 1185 */                 paramPrintStream.println(str1);
/* 1186 */               } else if (localHashtable != null) {
/* 1187 */                 localHashtable.put(str14.toLowerCase(), str15);
/*      */               } else {
/* 1189 */                 paramPrintStream.println(str2);
/*      */               }
/*      */             }
/*      */           }
/* 1193 */           else if (str13.equalsIgnoreCase("applet")) {
/* 1194 */             i = 1;
/* 1195 */             localHashtable = scanTag(localReader);
/* 1196 */             if ((localHashtable.get("code") == null) && (localHashtable.get("object") == null)) {
/* 1197 */               paramPrintStream.println(str3);
/* 1198 */               localHashtable = null;
/* 1199 */             } else if (localHashtable.get("width") == null) {
/* 1200 */               paramPrintStream.println(str5);
/* 1201 */               localHashtable = null;
/* 1202 */             } else if (localHashtable.get("height") == null) {
/* 1203 */               paramPrintStream.println(str4);
/* 1204 */               localHashtable = null;
/*      */             }
/*      */           }
/* 1207 */           else if (str13.equalsIgnoreCase("object")) {
/* 1208 */             j = 1;
/* 1209 */             localHashtable = scanTag(localReader);
/*      */             
/*      */ 
/* 1212 */             if (localHashtable.get("codebase") != null) {
/* 1213 */               localHashtable.remove("codebase");
/*      */             }
/*      */             
/* 1216 */             if (localHashtable.get("width") == null) {
/* 1217 */               paramPrintStream.println(str8);
/* 1218 */               localHashtable = null;
/* 1219 */             } else if (localHashtable.get("height") == null) {
/* 1220 */               paramPrintStream.println(str7);
/* 1221 */               localHashtable = null;
/*      */             }
/*      */           }
/* 1224 */           else if (str13.equalsIgnoreCase("embed")) {
/* 1225 */             k = 1;
/* 1226 */             localHashtable = scanTag(localReader);
/*      */             
/* 1228 */             if ((localHashtable.get("code") == null) && (localHashtable.get("object") == null)) {
/* 1229 */               paramPrintStream.println(str9);
/* 1230 */               localHashtable = null;
/* 1231 */             } else if (localHashtable.get("width") == null) {
/* 1232 */               paramPrintStream.println(str11);
/* 1233 */               localHashtable = null;
/* 1234 */             } else if (localHashtable.get("height") == null) {
/* 1235 */               paramPrintStream.println(str10);
/* 1236 */               localHashtable = null;
/*      */             }
/*      */           }
/* 1239 */           else if (str13.equalsIgnoreCase("app")) {
/* 1240 */             paramPrintStream.println(str12);
/* 1241 */             localObject = scanTag(localReader);
/* 1242 */             str13 = (String)((Hashtable)localObject).get("class");
/* 1243 */             if (str13 != null) {
/* 1244 */               ((Hashtable)localObject).remove("class");
/* 1245 */               ((Hashtable)localObject).put("code", str13 + ".class");
/*      */             }
/* 1247 */             str13 = (String)((Hashtable)localObject).get("src");
/* 1248 */             if (str13 != null) {
/* 1249 */               ((Hashtable)localObject).remove("src");
/* 1250 */               ((Hashtable)localObject).put("codebase", str13);
/*      */             }
/* 1252 */             if (((Hashtable)localObject).get("width") == null) {
/* 1253 */               ((Hashtable)localObject).put("width", "100");
/*      */             }
/* 1255 */             if (((Hashtable)localObject).get("height") == null) {
/* 1256 */               ((Hashtable)localObject).put("height", "100");
/*      */             }
/* 1258 */             printTag(paramPrintStream, (Hashtable)localObject);
/* 1259 */             paramPrintStream.println();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1264 */     localReader.close();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static void main(String[] paramArrayOfString)
/*      */   {
/* 1275 */     Main.main(paramArrayOfString);
/*      */   }
/*      */   
/* 1278 */   private static AppletMessageHandler amh = new AppletMessageHandler("appletviewer");
/*      */   
/*      */   private static void checkConnect(URL paramURL)
/*      */   {
/* 1282 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1283 */     if (localSecurityManager != null) {
/*      */       try
/*      */       {
/* 1286 */         Permission localPermission = paramURL.openConnection().getPermission();
/* 1287 */         if (localPermission != null) {
/* 1288 */           localSecurityManager.checkPermission(localPermission);
/*      */         } else
/* 1290 */           localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*      */       } catch (IOException localIOException) {
/* 1292 */         localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void showDocument(URL paramURL) {}
/*      */   
/*      */   public void showDocument(URL paramURL, String paramString) {}
/*      */   
/*      */   public void setStream(String paramString, InputStream paramInputStream)
/*      */     throws IOException
/*      */   {}
/*      */   
/*      */   void appletEdit() {}
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\applet\AppletViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
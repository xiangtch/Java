/*      */ package sun.print;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Frame;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.io.File;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.text.DecimalFormat;
/*      */ import java.util.Locale;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Vector;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.print.DocFlavor;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.ServiceUIFactory;
/*      */ import javax.print.attribute.Attribute;
/*      */ import javax.print.attribute.AttributeSet;
/*      */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintServiceAttribute;
/*      */ import javax.print.attribute.standard.Chromaticity;
/*      */ import javax.print.attribute.standard.Copies;
/*      */ import javax.print.attribute.standard.CopiesSupported;
/*      */ import javax.print.attribute.standard.Destination;
/*      */ import javax.print.attribute.standard.JobName;
/*      */ import javax.print.attribute.standard.JobPriority;
/*      */ import javax.print.attribute.standard.JobSheets;
/*      */ import javax.print.attribute.standard.Media;
/*      */ import javax.print.attribute.standard.MediaPrintableArea;
/*      */ import javax.print.attribute.standard.MediaSize;
/*      */ import javax.print.attribute.standard.MediaSizeName;
/*      */ import javax.print.attribute.standard.MediaTray;
/*      */ import javax.print.attribute.standard.OrientationRequested;
/*      */ import javax.print.attribute.standard.PageRanges;
/*      */ import javax.print.attribute.standard.PrintQuality;
/*      */ import javax.print.attribute.standard.PrinterInfo;
/*      */ import javax.print.attribute.standard.PrinterIsAcceptingJobs;
/*      */ import javax.print.attribute.standard.PrinterMakeAndModel;
/*      */ import javax.print.attribute.standard.RequestingUserName;
/*      */ import javax.print.attribute.standard.SheetCollate;
/*      */ import javax.print.attribute.standard.Sides;
/*      */ import javax.swing.AbstractButton;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.InputMap;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JDialog;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JFormattedTextField;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRadioButton;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.JSpinner;
/*      */ import javax.swing.JSpinner.NumberEditor;
/*      */ import javax.swing.JTabbedPane;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.SpinnerNumberModel;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.event.PopupMenuEvent;
/*      */ import javax.swing.event.PopupMenuListener;
/*      */ import javax.swing.text.NumberFormatter;
/*      */ 
/*      */ public class ServiceDialog extends JDialog implements ActionListener
/*      */ {
/*      */   public static final int WAITING = 0;
/*      */   public static final int APPROVE = 1;
/*      */   public static final int CANCEL = 2;
/*      */   private static final String strBundle = "sun.print.resources.serviceui";
/*  102 */   private static final Insets panelInsets = new Insets(6, 6, 6, 6);
/*  103 */   private static final Insets compInsets = new Insets(3, 6, 3, 6);
/*      */   
/*      */   private static ResourceBundle messageRB;
/*      */   
/*      */   private JTabbedPane tpTabs;
/*      */   
/*      */   private JButton btnCancel;
/*      */   private JButton btnApprove;
/*      */   private PrintService[] services;
/*      */   private int defaultServiceIndex;
/*      */   private PrintRequestAttributeSet asOriginal;
/*      */   private HashPrintRequestAttributeSet asCurrent;
/*      */   private PrintService psCurrent;
/*      */   private DocFlavor docFlavor;
/*      */   private int status;
/*      */   private ValidatingFileChooser jfc;
/*      */   private GeneralPanel pnlGeneral;
/*      */   private PageSetupPanel pnlPageSetup;
/*      */   private AppearancePanel pnlAppearance;
/*  122 */   private boolean isAWT = false;
/*      */   
/*  124 */   static { initResource(); }
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
/*      */   public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Dialog paramDialog)
/*      */   {
/*  140 */     super(paramDialog, getMsg("dialog.printtitle"), true, paramGraphicsConfiguration);
/*  141 */     initPrintDialog(paramInt1, paramInt2, paramArrayOfPrintService, paramInt3, paramDocFlavor, paramPrintRequestAttributeSet);
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
/*      */   public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Frame paramFrame)
/*      */   {
/*  159 */     super(paramFrame, getMsg("dialog.printtitle"), true, paramGraphicsConfiguration);
/*  160 */     initPrintDialog(paramInt1, paramInt2, paramArrayOfPrintService, paramInt3, paramDocFlavor, paramPrintRequestAttributeSet);
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
/*      */   void initPrintDialog(int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/*  174 */     this.services = paramArrayOfPrintService;
/*  175 */     this.defaultServiceIndex = paramInt3;
/*  176 */     this.asOriginal = paramPrintRequestAttributeSet;
/*  177 */     this.asCurrent = new HashPrintRequestAttributeSet(paramPrintRequestAttributeSet);
/*  178 */     this.psCurrent = paramArrayOfPrintService[paramInt3];
/*  179 */     this.docFlavor = paramDocFlavor;
/*      */     
/*  181 */     SunPageSelection localSunPageSelection = (SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class);
/*  182 */     if (localSunPageSelection != null) {
/*  183 */       this.isAWT = true;
/*      */     }
/*      */     
/*  186 */     if (paramPrintRequestAttributeSet.get(DialogOnTop.class) != null) {
/*  187 */       setAlwaysOnTop(true);
/*      */     }
/*  189 */     Container localContainer = getContentPane();
/*  190 */     localContainer.setLayout(new BorderLayout());
/*      */     
/*  192 */     this.tpTabs = new JTabbedPane();
/*  193 */     this.tpTabs.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
/*      */     
/*  195 */     String str1 = getMsg("tab.general");
/*  196 */     int i = getVKMnemonic("tab.general");
/*  197 */     this.pnlGeneral = new GeneralPanel();
/*  198 */     this.tpTabs.add(str1, this.pnlGeneral);
/*  199 */     this.tpTabs.setMnemonicAt(0, i);
/*      */     
/*  201 */     String str2 = getMsg("tab.pagesetup");
/*  202 */     int j = getVKMnemonic("tab.pagesetup");
/*  203 */     this.pnlPageSetup = new PageSetupPanel();
/*  204 */     this.tpTabs.add(str2, this.pnlPageSetup);
/*  205 */     this.tpTabs.setMnemonicAt(1, j);
/*      */     
/*  207 */     String str3 = getMsg("tab.appearance");
/*  208 */     int k = getVKMnemonic("tab.appearance");
/*  209 */     this.pnlAppearance = new AppearancePanel();
/*  210 */     this.tpTabs.add(str3, this.pnlAppearance);
/*  211 */     this.tpTabs.setMnemonicAt(2, k);
/*      */     
/*  213 */     localContainer.add(this.tpTabs, "Center");
/*      */     
/*  215 */     updatePanels();
/*      */     
/*  217 */     JPanel localJPanel = new JPanel(new FlowLayout(4));
/*  218 */     this.btnApprove = createExitButton("button.print", this);
/*  219 */     localJPanel.add(this.btnApprove);
/*  220 */     getRootPane().setDefaultButton(this.btnApprove);
/*  221 */     this.btnCancel = createExitButton("button.cancel", this);
/*  222 */     handleEscKey(this.btnCancel);
/*  223 */     localJPanel.add(this.btnCancel);
/*  224 */     localContainer.add(localJPanel, "South");
/*      */     
/*  226 */     addWindowListener(new WindowAdapter() {
/*      */       public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
/*  228 */         ServiceDialog.this.dispose(2);
/*      */       }
/*      */       
/*  231 */     });
/*  232 */     getAccessibleContext().setAccessibleDescription(getMsg("dialog.printtitle"));
/*  233 */     setResizable(false);
/*  234 */     setLocation(paramInt1, paramInt2);
/*  235 */     pack();
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
/*      */   public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Dialog paramDialog)
/*      */   {
/*  248 */     super(paramDialog, getMsg("dialog.pstitle"), true, paramGraphicsConfiguration);
/*  249 */     initPageDialog(paramInt1, paramInt2, paramPrintService, paramDocFlavor, paramPrintRequestAttributeSet);
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
/*      */   public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Frame paramFrame)
/*      */   {
/*  262 */     super(paramFrame, getMsg("dialog.pstitle"), true, paramGraphicsConfiguration);
/*  263 */     initPageDialog(paramInt1, paramInt2, paramPrintService, paramDocFlavor, paramPrintRequestAttributeSet);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void initPageDialog(int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/*  275 */     this.psCurrent = paramPrintService;
/*  276 */     this.docFlavor = paramDocFlavor;
/*  277 */     this.asOriginal = paramPrintRequestAttributeSet;
/*  278 */     this.asCurrent = new HashPrintRequestAttributeSet(paramPrintRequestAttributeSet);
/*      */     
/*  280 */     if (paramPrintRequestAttributeSet.get(DialogOnTop.class) != null) {
/*  281 */       setAlwaysOnTop(true);
/*      */     }
/*      */     
/*  284 */     Container localContainer = getContentPane();
/*  285 */     localContainer.setLayout(new BorderLayout());
/*      */     
/*  287 */     this.pnlPageSetup = new PageSetupPanel();
/*  288 */     localContainer.add(this.pnlPageSetup, "Center");
/*      */     
/*  290 */     this.pnlPageSetup.updateInfo();
/*      */     
/*  292 */     JPanel localJPanel = new JPanel(new FlowLayout(4));
/*  293 */     this.btnApprove = createExitButton("button.ok", this);
/*  294 */     localJPanel.add(this.btnApprove);
/*  295 */     getRootPane().setDefaultButton(this.btnApprove);
/*  296 */     this.btnCancel = createExitButton("button.cancel", this);
/*  297 */     handleEscKey(this.btnCancel);
/*  298 */     localJPanel.add(this.btnCancel);
/*  299 */     localContainer.add(localJPanel, "South");
/*      */     
/*  301 */     addWindowListener(new WindowAdapter() {
/*      */       public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
/*  303 */         ServiceDialog.this.dispose(2);
/*      */       }
/*      */       
/*  306 */     });
/*  307 */     getAccessibleContext().setAccessibleDescription(getMsg("dialog.pstitle"));
/*  308 */     setResizable(false);
/*  309 */     setLocation(paramInt1, paramInt2);
/*  310 */     pack();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void handleEscKey(JButton paramJButton)
/*      */   {
/*  317 */     javax.swing.AbstractAction local3 = new javax.swing.AbstractAction() {
/*      */       public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/*  319 */         ServiceDialog.this.dispose(2);
/*      */       }
/*      */       
/*  322 */     };
/*  323 */     KeyStroke localKeyStroke = KeyStroke.getKeyStroke(27, 0);
/*      */     
/*  325 */     InputMap localInputMap = paramJButton.getInputMap(2);
/*  326 */     ActionMap localActionMap = paramJButton.getActionMap();
/*      */     
/*  328 */     if ((localInputMap != null) && (localActionMap != null)) {
/*  329 */       localInputMap.put(localKeyStroke, "cancel");
/*  330 */       localActionMap.put("cancel", local3);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getStatus()
/*      */   {
/*  340 */     return this.status;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PrintRequestAttributeSet getAttributes()
/*      */   {
/*  349 */     if (this.status == 1) {
/*  350 */       return this.asCurrent;
/*      */     }
/*  352 */     return this.asOriginal;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PrintService getPrintService()
/*      */   {
/*  362 */     if (this.status == 1) {
/*  363 */       return this.psCurrent;
/*      */     }
/*  365 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void dispose(int paramInt)
/*      */   {
/*  374 */     this.status = paramInt;
/*      */     
/*  376 */     super.dispose();
/*      */   }
/*      */   
/*      */   public void actionPerformed(ActionEvent paramActionEvent) {
/*  380 */     Object localObject = paramActionEvent.getSource();
/*  381 */     boolean bool = false;
/*      */     
/*  383 */     if (localObject == this.btnApprove) {
/*  384 */       bool = true;
/*      */       
/*  386 */       if (this.pnlGeneral != null) {
/*  387 */         if (this.pnlGeneral.isPrintToFileRequested()) {
/*  388 */           bool = showFileChooser();
/*      */         } else {
/*  390 */           this.asCurrent.remove(Destination.class);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  395 */     dispose(bool ? 1 : 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean showFileChooser()
/*      */   {
/*  403 */     Class localClass = Destination.class;
/*      */     
/*  405 */     Destination localDestination = (Destination)this.asCurrent.get(localClass);
/*  406 */     if (localDestination == null) {
/*  407 */       localDestination = (Destination)this.asOriginal.get(localClass);
/*  408 */       if (localDestination == null) {
/*  409 */         localDestination = (Destination)this.psCurrent.getDefaultAttributeValue(localClass);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  414 */         if (localDestination == null) {
/*      */           try {
/*  416 */             localDestination = new Destination(new URI("file:out.prn"));
/*      */           }
/*      */           catch (URISyntaxException localURISyntaxException) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     File localFile;
/*  424 */     if (localDestination != null) {
/*      */       try {
/*  426 */         localFile = new File(localDestination.getURI());
/*      */       }
/*      */       catch (Exception localException1) {
/*  429 */         localFile = new File("out.prn");
/*      */       }
/*      */     } else {
/*  432 */       localFile = new File("out.prn");
/*      */     }
/*      */     
/*  435 */     ValidatingFileChooser localValidatingFileChooser = new ValidatingFileChooser(null);
/*  436 */     localValidatingFileChooser.setApproveButtonText(getMsg("button.ok"));
/*  437 */     localValidatingFileChooser.setDialogTitle(getMsg("dialog.printtofile"));
/*  438 */     localValidatingFileChooser.setDialogType(1);
/*  439 */     localValidatingFileChooser.setSelectedFile(localFile);
/*      */     
/*  441 */     int i = localValidatingFileChooser.showDialog(this, null);
/*  442 */     if (i == 0) {
/*  443 */       localFile = localValidatingFileChooser.getSelectedFile();
/*      */       try
/*      */       {
/*  446 */         this.asCurrent.add(new Destination(localFile.toURI()));
/*      */       } catch (Exception localException2) {
/*  448 */         this.asCurrent.remove(localClass);
/*      */       }
/*      */     } else {
/*  451 */       this.asCurrent.remove(localClass);
/*      */     }
/*      */     
/*  454 */     return i == 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void updatePanels()
/*      */   {
/*  461 */     this.pnlGeneral.updateInfo();
/*  462 */     this.pnlPageSetup.updateInfo();
/*  463 */     this.pnlAppearance.updateInfo();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void initResource()
/*      */   {
/*  470 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/*      */         try {
/*  474 */           ServiceDialog.access$102(ResourceBundle.getBundle("sun.print.resources.serviceui"));
/*  475 */           return null;
/*      */         } catch (MissingResourceException localMissingResourceException) {
/*  477 */           throw new Error("Fatal: Resource for ServiceUI is missing");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getMsg(String paramString)
/*      */   {
/*      */     try
/*      */     {
/*  490 */       return removeMnemonics(messageRB.getString(paramString));
/*      */     } catch (MissingResourceException localMissingResourceException) {
/*  492 */       throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + paramString + " key in resource");
/*      */     }
/*      */   }
/*      */   
/*      */   private static String removeMnemonics(String paramString)
/*      */   {
/*  498 */     int i = paramString.indexOf('&');
/*  499 */     int j = paramString.length();
/*  500 */     if ((i < 0) || (i == j - 1)) {
/*  501 */       return paramString;
/*      */     }
/*  503 */     int k = paramString.indexOf('&', i + 1);
/*  504 */     if (k == i + 1) {
/*  505 */       if (k + 1 == j) {
/*  506 */         return paramString.substring(0, i + 1);
/*      */       }
/*  508 */       return paramString.substring(0, i + 1) + removeMnemonics(paramString.substring(k + 1));
/*      */     }
/*      */     
/*      */ 
/*  512 */     if (i == 0) {
/*  513 */       return removeMnemonics(paramString.substring(1));
/*      */     }
/*  515 */     return paramString.substring(0, i) + removeMnemonics(paramString.substring(i + 1));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static char getMnemonic(String paramString)
/*      */   {
/*  524 */     String str = messageRB.getString(paramString).replace("&&", "");
/*  525 */     int i = str.indexOf('&');
/*  526 */     if ((0 <= i) && (i < str.length() - 1)) {
/*  527 */       char c = str.charAt(i + 1);
/*  528 */       return Character.toUpperCase(c);
/*      */     }
/*  530 */     return '\000';
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  537 */   static Class _keyEventClazz = null;
/*      */   
/*  539 */   private static int getVKMnemonic(String paramString) { String str1 = String.valueOf(getMnemonic(paramString));
/*  540 */     if ((str1 == null) || (str1.length() != 1)) {
/*  541 */       return 0;
/*      */     }
/*  543 */     String str2 = "VK_" + str1.toUpperCase();
/*      */     try
/*      */     {
/*  546 */       if (_keyEventClazz == null) {
/*  547 */         _keyEventClazz = Class.forName("java.awt.event.KeyEvent", true, ServiceDialog.class
/*  548 */           .getClassLoader());
/*      */       }
/*  550 */       Field localField = _keyEventClazz.getDeclaredField(str2);
/*  551 */       return localField.getInt(null);
/*      */     }
/*      */     catch (Exception localException) {}
/*      */     
/*  555 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static URL getImageResource(String paramString)
/*      */   {
/*  562 */     URL localURL = (URL)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/*  565 */         URL localURL = ServiceDialog.class.getResource("resources/" + this.val$key);
/*      */         
/*  567 */         return localURL;
/*      */       }
/*      */     });
/*      */     
/*  571 */     if (localURL == null) {
/*  572 */       throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + paramString + " key in resource");
/*      */     }
/*      */     
/*      */ 
/*  576 */     return localURL;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static JButton createButton(String paramString, ActionListener paramActionListener)
/*      */   {
/*  583 */     JButton localJButton = new JButton(getMsg(paramString));
/*  584 */     localJButton.setMnemonic(getMnemonic(paramString));
/*  585 */     localJButton.addActionListener(paramActionListener);
/*      */     
/*  587 */     return localJButton;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static JButton createExitButton(String paramString, ActionListener paramActionListener)
/*      */   {
/*  594 */     String str = getMsg(paramString);
/*  595 */     JButton localJButton = new JButton(str);
/*  596 */     localJButton.addActionListener(paramActionListener);
/*  597 */     localJButton.getAccessibleContext().setAccessibleDescription(str);
/*  598 */     return localJButton;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static JCheckBox createCheckBox(String paramString, ActionListener paramActionListener)
/*      */   {
/*  605 */     JCheckBox localJCheckBox = new JCheckBox(getMsg(paramString));
/*  606 */     localJCheckBox.setMnemonic(getMnemonic(paramString));
/*  607 */     localJCheckBox.addActionListener(paramActionListener);
/*      */     
/*  609 */     return localJCheckBox;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static JRadioButton createRadioButton(String paramString, ActionListener paramActionListener)
/*      */   {
/*  619 */     JRadioButton localJRadioButton = new JRadioButton(getMsg(paramString));
/*  620 */     localJRadioButton.setMnemonic(getMnemonic(paramString));
/*  621 */     localJRadioButton.addActionListener(paramActionListener);
/*      */     
/*  623 */     return localJRadioButton;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void showNoPrintService(GraphicsConfiguration paramGraphicsConfiguration)
/*      */   {
/*  631 */     Frame localFrame = new Frame(paramGraphicsConfiguration);
/*  632 */     JOptionPane.showMessageDialog(localFrame, 
/*  633 */       getMsg("dialog.noprintermsg"));
/*  634 */     localFrame.dispose();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void addToGB(Component paramComponent, Container paramContainer, GridBagLayout paramGridBagLayout, GridBagConstraints paramGridBagConstraints)
/*      */   {
/*  645 */     paramGridBagLayout.setConstraints(paramComponent, paramGridBagConstraints);
/*  646 */     paramContainer.add(paramComponent);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void addToBG(AbstractButton paramAbstractButton, Container paramContainer, ButtonGroup paramButtonGroup)
/*      */   {
/*  655 */     paramButtonGroup.add(paramAbstractButton);
/*  656 */     paramContainer.add(paramAbstractButton);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class GeneralPanel
/*      */     extends JPanel
/*      */   {
/*      */     private PrintServicePanel pnlPrintService;
/*      */     
/*      */ 
/*      */     private PrintRangePanel pnlPrintRange;
/*      */     
/*      */ 
/*      */     private CopiesPanel pnlCopies;
/*      */     
/*      */ 
/*      */     public GeneralPanel()
/*      */     {
/*  675 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/*  676 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/*  678 */       setLayout(localGridBagLayout);
/*      */       
/*  680 */       localGridBagConstraints.fill = 1;
/*  681 */       localGridBagConstraints.insets = ServiceDialog.panelInsets;
/*  682 */       localGridBagConstraints.weightx = 1.0D;
/*  683 */       localGridBagConstraints.weighty = 1.0D;
/*      */       
/*  685 */       localGridBagConstraints.gridwidth = 0;
/*  686 */       this.pnlPrintService = new PrintServicePanel(ServiceDialog.this);
/*  687 */       ServiceDialog.addToGB(this.pnlPrintService, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/*  689 */       localGridBagConstraints.gridwidth = -1;
/*  690 */       this.pnlPrintRange = new PrintRangePanel(ServiceDialog.this);
/*  691 */       ServiceDialog.addToGB(this.pnlPrintRange, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/*  693 */       localGridBagConstraints.gridwidth = 0;
/*  694 */       this.pnlCopies = new CopiesPanel(ServiceDialog.this);
/*  695 */       ServiceDialog.addToGB(this.pnlCopies, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public boolean isPrintToFileRequested() {
/*  699 */       return this.pnlPrintService.isPrintToFileSelected();
/*      */     }
/*      */     
/*      */     public void updateInfo() {
/*  703 */       this.pnlPrintService.updateInfo();
/*  704 */       this.pnlPrintRange.updateInfo();
/*  705 */       this.pnlCopies.updateInfo();
/*      */     }
/*      */   }
/*      */   
/*      */   private class PrintServicePanel
/*      */     extends JPanel implements ActionListener, ItemListener, PopupMenuListener
/*      */   {
/*  712 */     private final String strTitle = ServiceDialog.getMsg("border.printservice");
/*      */     private FilePermission printToFilePermission;
/*      */     private JButton btnProperties;
/*      */     private JCheckBox cbPrintToFile;
/*      */     private JComboBox cbName;
/*      */     private JLabel lblType;
/*      */     private JLabel lblStatus;
/*  719 */     private JLabel lblInfo; private ServiceUIFactory uiFactory; private boolean changedService = false;
/*      */     
/*      */     private boolean filePermission;
/*      */     
/*      */     public PrintServicePanel()
/*      */     {
/*  725 */       this.uiFactory = ServiceDialog.this.psCurrent.getServiceUIFactory();
/*      */       
/*  727 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/*  728 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/*  730 */       setLayout(localGridBagLayout);
/*  731 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/*  733 */       String[] arrayOfString = new String[ServiceDialog.this.services.length];
/*  734 */       for (int i = 0; i < arrayOfString.length; i++) {
/*  735 */         arrayOfString[i] = ServiceDialog.this.services[i].getName();
/*      */       }
/*  737 */       this.cbName = new JComboBox(arrayOfString);
/*  738 */       this.cbName.setSelectedIndex(ServiceDialog.this.defaultServiceIndex);
/*  739 */       this.cbName.addItemListener(this);
/*  740 */       this.cbName.addPopupMenuListener(this);
/*      */       
/*  742 */       localGridBagConstraints.fill = 1;
/*  743 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/*      */       
/*  745 */       localGridBagConstraints.weightx = 0.0D;
/*  746 */       JLabel localJLabel = new JLabel(ServiceDialog.getMsg("label.psname"), 11);
/*  747 */       localJLabel.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.psname"));
/*  748 */       localJLabel.setLabelFor(this.cbName);
/*  749 */       ServiceDialog.addToGB(localJLabel, this, localGridBagLayout, localGridBagConstraints);
/*  750 */       localGridBagConstraints.weightx = 1.0D;
/*  751 */       localGridBagConstraints.gridwidth = -1;
/*  752 */       ServiceDialog.addToGB(this.cbName, this, localGridBagLayout, localGridBagConstraints);
/*  753 */       localGridBagConstraints.weightx = 0.0D;
/*  754 */       localGridBagConstraints.gridwidth = 0;
/*  755 */       this.btnProperties = ServiceDialog.createButton("button.properties", this);
/*  756 */       ServiceDialog.addToGB(this.btnProperties, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/*  758 */       localGridBagConstraints.weighty = 1.0D;
/*  759 */       this.lblStatus = addLabel(ServiceDialog.getMsg("label.status"), localGridBagLayout, localGridBagConstraints);
/*  760 */       this.lblStatus.setLabelFor(null);
/*      */       
/*  762 */       this.lblType = addLabel(ServiceDialog.getMsg("label.pstype"), localGridBagLayout, localGridBagConstraints);
/*  763 */       this.lblType.setLabelFor(null);
/*      */       
/*  765 */       localGridBagConstraints.gridwidth = 1;
/*  766 */       ServiceDialog.addToGB(new JLabel(ServiceDialog.getMsg("label.info"), 11), this, localGridBagLayout, localGridBagConstraints);
/*      */       
/*  768 */       localGridBagConstraints.gridwidth = -1;
/*  769 */       this.lblInfo = new JLabel();
/*  770 */       this.lblInfo.setLabelFor(null);
/*      */       
/*  772 */       ServiceDialog.addToGB(this.lblInfo, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/*  774 */       localGridBagConstraints.gridwidth = 0;
/*  775 */       this.cbPrintToFile = ServiceDialog.createCheckBox("checkbox.printtofile", this);
/*  776 */       ServiceDialog.addToGB(this.cbPrintToFile, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/*  778 */       this.filePermission = allowedToPrintToFile();
/*      */     }
/*      */     
/*      */     public boolean isPrintToFileSelected() {
/*  782 */       return this.cbPrintToFile.isSelected();
/*      */     }
/*      */     
/*      */ 
/*      */     private JLabel addLabel(String paramString, GridBagLayout paramGridBagLayout, GridBagConstraints paramGridBagConstraints)
/*      */     {
/*  788 */       paramGridBagConstraints.gridwidth = 1;
/*  789 */       ServiceDialog.addToGB(new JLabel(paramString, 11), this, paramGridBagLayout, paramGridBagConstraints);
/*      */       
/*  791 */       paramGridBagConstraints.gridwidth = 0;
/*  792 */       JLabel localJLabel = new JLabel();
/*  793 */       ServiceDialog.addToGB(localJLabel, this, paramGridBagLayout, paramGridBagConstraints);
/*      */       
/*  795 */       return localJLabel;
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/*  799 */       Object localObject = paramActionEvent.getSource();
/*      */       
/*  801 */       if ((localObject == this.btnProperties) && 
/*  802 */         (this.uiFactory != null)) {
/*  803 */         JDialog localJDialog = (JDialog)this.uiFactory.getUI(3, "javax.swing.JDialog");
/*      */         
/*      */ 
/*      */ 
/*  807 */         if (localJDialog != null) {
/*  808 */           localJDialog.show();
/*      */         } else {
/*  810 */           DocumentPropertiesUI localDocumentPropertiesUI = null;
/*      */           
/*      */           try
/*      */           {
/*  814 */             localDocumentPropertiesUI = (DocumentPropertiesUI)this.uiFactory.getUI(199, DocumentPropertiesUI.DOCPROPERTIESCLASSNAME);
/*      */           }
/*      */           catch (Exception localException) {}
/*      */           
/*  818 */           if (localDocumentPropertiesUI != null)
/*      */           {
/*  820 */             PrinterJobWrapper localPrinterJobWrapper = (PrinterJobWrapper)ServiceDialog.this.asCurrent.get(PrinterJobWrapper.class);
/*  821 */             if (localPrinterJobWrapper == null) {
/*  822 */               return;
/*      */             }
/*  824 */             PrinterJob localPrinterJob = localPrinterJobWrapper.getPrinterJob();
/*  825 */             if (localPrinterJob == null) {
/*  826 */               return;
/*      */             }
/*      */             
/*      */ 
/*  830 */             PrintRequestAttributeSet localPrintRequestAttributeSet = localDocumentPropertiesUI.showDocumentProperties(localPrinterJob, ServiceDialog.this, ServiceDialog.this.psCurrent, ServiceDialog.this.asCurrent);
/*  831 */             if (localPrintRequestAttributeSet != null) {
/*  832 */               ServiceDialog.this.asCurrent.addAll(localPrintRequestAttributeSet);
/*  833 */               ServiceDialog.this.updatePanels();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public void itemStateChanged(ItemEvent paramItemEvent)
/*      */     {
/*  842 */       if (paramItemEvent.getStateChange() == 1) {
/*  843 */         int i = this.cbName.getSelectedIndex();
/*      */         
/*  845 */         if ((i >= 0) && (i < ServiceDialog.this.services.length) && 
/*  846 */           (!ServiceDialog.this.services[i].equals(ServiceDialog.this.psCurrent))) {
/*  847 */           ServiceDialog.this.psCurrent = ServiceDialog.this.services[i];
/*  848 */           this.uiFactory = ServiceDialog.this.psCurrent.getServiceUIFactory();
/*  849 */           this.changedService = true;
/*      */           
/*      */ 
/*  852 */           Destination localDestination = (Destination)ServiceDialog.this.asOriginal.get(Destination.class);
/*      */           
/*  854 */           if (((localDestination != null) || (isPrintToFileSelected())) && 
/*  855 */             (ServiceDialog.this.psCurrent.isAttributeCategorySupported(Destination.class)))
/*      */           {
/*      */ 
/*  858 */             if (localDestination != null) {
/*  859 */               ServiceDialog.this.asCurrent.add(localDestination);
/*      */             }
/*      */             else {
/*  862 */               localDestination = (Destination)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Destination.class);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  867 */               if (localDestination == null) {
/*      */                 try {
/*  869 */                   localDestination = new Destination(new URI("file:out.prn"));
/*      */                 }
/*      */                 catch (URISyntaxException localURISyntaxException) {}
/*      */               }
/*      */               
/*      */ 
/*  875 */               if (localDestination != null) {
/*  876 */                 ServiceDialog.this.asCurrent.add(localDestination);
/*      */               }
/*      */             }
/*      */           } else {
/*  880 */             ServiceDialog.this.asCurrent.remove(Destination.class);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public void popupMenuWillBecomeVisible(PopupMenuEvent paramPopupMenuEvent)
/*      */     {
/*  888 */       this.changedService = false;
/*      */     }
/*      */     
/*      */     public void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent) {
/*  892 */       if (this.changedService) {
/*  893 */         this.changedService = false;
/*  894 */         ServiceDialog.this.updatePanels();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent) {}
/*      */     
/*      */ 
/*      */     private boolean allowedToPrintToFile()
/*      */     {
/*      */       try
/*      */       {
/*  906 */         throwPrintToFile();
/*  907 */         return true;
/*      */       } catch (SecurityException localSecurityException) {}
/*  909 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void throwPrintToFile()
/*      */     {
/*  919 */       SecurityManager localSecurityManager = System.getSecurityManager();
/*  920 */       if (localSecurityManager != null) {
/*  921 */         if (this.printToFilePermission == null) {
/*  922 */           this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
/*      */         }
/*      */         
/*  925 */         localSecurityManager.checkPermission(this.printToFilePermission);
/*      */       }
/*      */     }
/*      */     
/*      */     public void updateInfo() {
/*  930 */       Class localClass = Destination.class;
/*  931 */       int i = 0;
/*  932 */       int j = 0;
/*      */       
/*  934 */       int k = this.filePermission ? allowedToPrintToFile() : 0;
/*      */       
/*      */ 
/*  937 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass)) {
/*  938 */         i = 1;
/*      */       }
/*  940 */       Destination localDestination = (Destination)ServiceDialog.this.asCurrent.get(localClass);
/*  941 */       if (localDestination != null) {
/*  942 */         j = 1;
/*      */       }
/*  944 */       this.cbPrintToFile.setEnabled((i != 0) && (k != 0));
/*  945 */       this.cbPrintToFile.setSelected((j != 0) && (k != 0) && (i != 0));
/*      */       
/*      */ 
/*      */ 
/*  949 */       PrintServiceAttribute localPrintServiceAttribute1 = ServiceDialog.this.psCurrent.getAttribute(PrinterMakeAndModel.class);
/*  950 */       if (localPrintServiceAttribute1 != null) {
/*  951 */         this.lblType.setText(localPrintServiceAttribute1.toString());
/*      */       }
/*      */       
/*  954 */       PrintServiceAttribute localPrintServiceAttribute2 = ServiceDialog.this.psCurrent.getAttribute(PrinterIsAcceptingJobs.class);
/*  955 */       if (localPrintServiceAttribute2 != null) {
/*  956 */         this.lblStatus.setText(ServiceDialog.getMsg(localPrintServiceAttribute2.toString()));
/*      */       }
/*  958 */       PrintServiceAttribute localPrintServiceAttribute3 = ServiceDialog.this.psCurrent.getAttribute(PrinterInfo.class);
/*  959 */       if (localPrintServiceAttribute3 != null) {
/*  960 */         this.lblInfo.setText(localPrintServiceAttribute3.toString());
/*      */       }
/*  962 */       this.btnProperties.setEnabled(this.uiFactory != null);
/*      */     }
/*      */   }
/*      */   
/*      */   private class PrintRangePanel
/*      */     extends JPanel implements ActionListener, FocusListener
/*      */   {
/*  969 */     private final String strTitle = ServiceDialog.getMsg("border.printrange");
/*  970 */     private final PageRanges prAll = new PageRanges(1, Integer.MAX_VALUE);
/*      */     private JRadioButton rbAll;
/*      */     private JRadioButton rbPages;
/*      */     private JRadioButton rbSelect;
/*      */     private JFormattedTextField tfRangeFrom;
/*      */     private JFormattedTextField tfRangeTo;
/*      */     private JLabel lblRangeTo;
/*      */     private boolean prSupported;
/*      */     
/*  979 */     public PrintRangePanel() { GridBagLayout localGridBagLayout = new GridBagLayout();
/*  980 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/*  982 */       setLayout(localGridBagLayout);
/*  983 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/*  985 */       localGridBagConstraints.fill = 1;
/*  986 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/*  987 */       localGridBagConstraints.gridwidth = 0;
/*      */       
/*  989 */       ButtonGroup localButtonGroup = new ButtonGroup();
/*  990 */       JPanel localJPanel1 = new JPanel(new FlowLayout(3));
/*  991 */       this.rbAll = ServiceDialog.createRadioButton("radiobutton.rangeall", this);
/*  992 */       this.rbAll.setSelected(true);
/*  993 */       localButtonGroup.add(this.rbAll);
/*  994 */       localJPanel1.add(this.rbAll);
/*  995 */       ServiceDialog.addToGB(localJPanel1, this, localGridBagLayout, localGridBagConstraints);
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
/* 1010 */       JPanel localJPanel2 = new JPanel(new FlowLayout(3));
/* 1011 */       this.rbPages = ServiceDialog.createRadioButton("radiobutton.rangepages", this);
/* 1012 */       localButtonGroup.add(this.rbPages);
/* 1013 */       localJPanel2.add(this.rbPages);
/* 1014 */       DecimalFormat localDecimalFormat = new DecimalFormat("####0");
/* 1015 */       localDecimalFormat.setMinimumFractionDigits(0);
/* 1016 */       localDecimalFormat.setMaximumFractionDigits(0);
/* 1017 */       localDecimalFormat.setMinimumIntegerDigits(0);
/* 1018 */       localDecimalFormat.setMaximumIntegerDigits(5);
/* 1019 */       localDecimalFormat.setParseIntegerOnly(true);
/* 1020 */       localDecimalFormat.setDecimalSeparatorAlwaysShown(false);
/* 1021 */       NumberFormatter localNumberFormatter1 = new NumberFormatter(localDecimalFormat);
/* 1022 */       localNumberFormatter1.setMinimum(new Integer(1));
/* 1023 */       localNumberFormatter1.setMaximum(new Integer(Integer.MAX_VALUE));
/* 1024 */       localNumberFormatter1.setAllowsInvalid(true);
/* 1025 */       localNumberFormatter1.setCommitsOnValidEdit(true);
/* 1026 */       this.tfRangeFrom = new JFormattedTextField(localNumberFormatter1);
/* 1027 */       this.tfRangeFrom.setColumns(4);
/* 1028 */       this.tfRangeFrom.setEnabled(false);
/* 1029 */       this.tfRangeFrom.addActionListener(this);
/* 1030 */       this.tfRangeFrom.addFocusListener(this);
/* 1031 */       this.tfRangeFrom.setFocusLostBehavior(3);
/*      */       
/* 1033 */       this.tfRangeFrom.getAccessibleContext().setAccessibleName(
/* 1034 */         ServiceDialog.getMsg("radiobutton.rangepages"));
/* 1035 */       localJPanel2.add(this.tfRangeFrom);
/* 1036 */       this.lblRangeTo = new JLabel(ServiceDialog.getMsg("label.rangeto"));
/* 1037 */       this.lblRangeTo.setEnabled(false);
/* 1038 */       localJPanel2.add(this.lblRangeTo);
/*      */       NumberFormatter localNumberFormatter2;
/*      */       try {
/* 1041 */         localNumberFormatter2 = (NumberFormatter)localNumberFormatter1.clone();
/*      */       } catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 1043 */         localNumberFormatter2 = new NumberFormatter();
/*      */       }
/* 1045 */       this.tfRangeTo = new JFormattedTextField(localNumberFormatter2);
/* 1046 */       this.tfRangeTo.setColumns(4);
/* 1047 */       this.tfRangeTo.setEnabled(false);
/* 1048 */       this.tfRangeTo.addFocusListener(this);
/* 1049 */       this.tfRangeTo.getAccessibleContext().setAccessibleName(
/* 1050 */         ServiceDialog.getMsg("label.rangeto"));
/* 1051 */       localJPanel2.add(this.tfRangeTo);
/* 1052 */       ServiceDialog.addToGB(localJPanel2, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 1056 */       Object localObject = paramActionEvent.getSource();
/* 1057 */       SunPageSelection localSunPageSelection = SunPageSelection.ALL;
/*      */       
/* 1059 */       setupRangeWidgets();
/*      */       
/* 1061 */       if (localObject == this.rbAll) {
/* 1062 */         ServiceDialog.this.asCurrent.add(this.prAll);
/* 1063 */       } else if (localObject == this.rbSelect) {
/* 1064 */         localSunPageSelection = SunPageSelection.SELECTION;
/* 1065 */       } else if ((localObject == this.rbPages) || (localObject == this.tfRangeFrom) || (localObject == this.tfRangeTo))
/*      */       {
/*      */ 
/* 1068 */         updateRangeAttribute();
/* 1069 */         localSunPageSelection = SunPageSelection.RANGE;
/*      */       }
/*      */       
/* 1072 */       if (ServiceDialog.this.isAWT) {
/* 1073 */         ServiceDialog.this.asCurrent.add(localSunPageSelection);
/*      */       }
/*      */     }
/*      */     
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/* 1078 */       Object localObject = paramFocusEvent.getSource();
/*      */       
/* 1080 */       if ((localObject == this.tfRangeFrom) || (localObject == this.tfRangeTo)) {
/* 1081 */         updateRangeAttribute();
/*      */       }
/*      */     }
/*      */     
/*      */     public void focusGained(FocusEvent paramFocusEvent) {}
/*      */     
/*      */     private void setupRangeWidgets() {
/* 1088 */       boolean bool = (this.rbPages.isSelected()) && (this.prSupported);
/* 1089 */       this.tfRangeFrom.setEnabled(bool);
/* 1090 */       this.tfRangeTo.setEnabled(bool);
/* 1091 */       this.lblRangeTo.setEnabled(bool);
/*      */     }
/*      */     
/*      */     private void updateRangeAttribute() {
/* 1095 */       String str1 = this.tfRangeFrom.getText();
/* 1096 */       String str2 = this.tfRangeTo.getText();
/*      */       
/*      */       int i;
/*      */       
/*      */       try
/*      */       {
/* 1102 */         i = Integer.parseInt(str1);
/*      */       } catch (NumberFormatException localNumberFormatException1) {
/* 1104 */         i = 1;
/*      */       }
/*      */       int j;
/*      */       try {
/* 1108 */         j = Integer.parseInt(str2);
/*      */       } catch (NumberFormatException localNumberFormatException2) {
/* 1110 */         j = i;
/*      */       }
/*      */       
/* 1113 */       if (i < 1) {
/* 1114 */         i = 1;
/* 1115 */         this.tfRangeFrom.setValue(new Integer(1));
/*      */       }
/*      */       
/* 1118 */       if (j < i) {
/* 1119 */         j = i;
/* 1120 */         this.tfRangeTo.setValue(new Integer(i));
/*      */       }
/*      */       
/* 1123 */       PageRanges localPageRanges = new PageRanges(i, j);
/* 1124 */       ServiceDialog.this.asCurrent.add(localPageRanges);
/*      */     }
/*      */     
/*      */     public void updateInfo() {
/* 1128 */       Class localClass = PageRanges.class;
/* 1129 */       this.prSupported = false;
/*      */       
/* 1131 */       if ((ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass)) || 
/* 1132 */         (ServiceDialog.this.isAWT)) {
/* 1133 */         this.prSupported = true;
/*      */       }
/*      */       
/* 1136 */       SunPageSelection localSunPageSelection = SunPageSelection.ALL;
/* 1137 */       int i = 1;
/* 1138 */       int j = 1;
/*      */       
/* 1140 */       PageRanges localPageRanges = (PageRanges)ServiceDialog.this.asCurrent.get(localClass);
/* 1141 */       if ((localPageRanges != null) && 
/* 1142 */         (!localPageRanges.equals(this.prAll))) {
/* 1143 */         localSunPageSelection = SunPageSelection.RANGE;
/*      */         
/* 1145 */         int[][] arrayOfInt = localPageRanges.getMembers();
/* 1146 */         if ((arrayOfInt.length > 0) && (arrayOfInt[0].length > 1))
/*      */         {
/* 1148 */           i = arrayOfInt[0][0];
/* 1149 */           j = arrayOfInt[0][1];
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1154 */       if (ServiceDialog.this.isAWT) {
/* 1155 */         localSunPageSelection = (SunPageSelection)ServiceDialog.this.asCurrent.get(SunPageSelection.class);
/*      */       }
/*      */       
/*      */ 
/* 1159 */       if (localSunPageSelection == SunPageSelection.ALL) {
/* 1160 */         this.rbAll.setSelected(true);
/* 1161 */       } else if (localSunPageSelection != SunPageSelection.SELECTION)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1168 */         this.rbPages.setSelected(true);
/*      */       }
/* 1170 */       this.tfRangeFrom.setValue(new Integer(i));
/* 1171 */       this.tfRangeTo.setValue(new Integer(j));
/* 1172 */       this.rbAll.setEnabled(this.prSupported);
/* 1173 */       this.rbPages.setEnabled(this.prSupported);
/* 1174 */       setupRangeWidgets();
/*      */     }
/*      */   }
/*      */   
/*      */   private class CopiesPanel
/*      */     extends JPanel implements ActionListener, ChangeListener
/*      */   {
/* 1181 */     private final String strTitle = ServiceDialog.getMsg("border.copies");
/*      */     
/*      */     private SpinnerNumberModel snModel;
/*      */     private JSpinner spinCopies;
/*      */     private JLabel lblCopies;
/*      */     private JCheckBox cbCollate;
/*      */     private boolean scSupported;
/*      */     
/*      */     public CopiesPanel()
/*      */     {
/* 1191 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 1192 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/* 1194 */       setLayout(localGridBagLayout);
/* 1195 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/* 1197 */       localGridBagConstraints.fill = 2;
/* 1198 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/*      */       
/* 1200 */       this.lblCopies = new JLabel(ServiceDialog.getMsg("label.numcopies"), 11);
/* 1201 */       this.lblCopies.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.numcopies"));
/* 1202 */       this.lblCopies.getAccessibleContext().setAccessibleName(
/* 1203 */         ServiceDialog.getMsg("label.numcopies"));
/* 1204 */       ServiceDialog.addToGB(this.lblCopies, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1206 */       this.snModel = new SpinnerNumberModel(1, 1, 999, 1);
/* 1207 */       this.spinCopies = new JSpinner(this.snModel);
/* 1208 */       this.lblCopies.setLabelFor(this.spinCopies);
/*      */       
/* 1210 */       ((NumberEditor)this.spinCopies.getEditor()).getTextField().setColumns(3);
/* 1211 */       this.spinCopies.addChangeListener(this);
/* 1212 */       localGridBagConstraints.gridwidth = 0;
/* 1213 */       ServiceDialog.addToGB(this.spinCopies, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1215 */       this.cbCollate = ServiceDialog.createCheckBox("checkbox.collate", this);
/* 1216 */       this.cbCollate.setEnabled(false);
/* 1217 */       ServiceDialog.addToGB(this.cbCollate, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 1221 */       if (this.cbCollate.isSelected()) {
/* 1222 */         ServiceDialog.this.asCurrent.add(SheetCollate.COLLATED);
/*      */       } else {
/* 1224 */         ServiceDialog.this.asCurrent.add(SheetCollate.UNCOLLATED);
/*      */       }
/*      */     }
/*      */     
/*      */     public void stateChanged(ChangeEvent paramChangeEvent) {
/* 1229 */       updateCollateCB();
/*      */       
/* 1231 */       ServiceDialog.this.asCurrent.add(new Copies(this.snModel.getNumber().intValue()));
/*      */     }
/*      */     
/*      */     private void updateCollateCB() {
/* 1235 */       int i = this.snModel.getNumber().intValue();
/* 1236 */       if (ServiceDialog.this.isAWT) {
/* 1237 */         this.cbCollate.setEnabled(true);
/*      */       } else {
/* 1239 */         this.cbCollate.setEnabled((i > 1) && (this.scSupported));
/*      */       }
/*      */     }
/*      */     
/*      */     public void updateInfo() {
/* 1244 */       Class localClass1 = Copies.class;
/* 1245 */       Class localClass2 = CopiesSupported.class;
/* 1246 */       Class localClass3 = SheetCollate.class;
/* 1247 */       boolean bool = false;
/* 1248 */       this.scSupported = false;
/*      */       
/*      */ 
/* 1251 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass1)) {
/* 1252 */         bool = true;
/*      */       }
/*      */       
/* 1255 */       CopiesSupported localCopiesSupported = (CopiesSupported)ServiceDialog.this.psCurrent.getSupportedAttributeValues(localClass1, null, null);
/*      */       
/* 1257 */       if (localCopiesSupported == null) {
/* 1258 */         localCopiesSupported = new CopiesSupported(1, 999);
/*      */       }
/* 1260 */       Copies localCopies = (Copies)ServiceDialog.this.asCurrent.get(localClass1);
/* 1261 */       if (localCopies == null) {
/* 1262 */         localCopies = (Copies)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass1);
/* 1263 */         if (localCopies == null) {
/* 1264 */           localCopies = new Copies(1);
/*      */         }
/*      */       }
/* 1267 */       this.spinCopies.setEnabled(bool);
/* 1268 */       this.lblCopies.setEnabled(bool);
/*      */       
/* 1270 */       int[][] arrayOfInt = localCopiesSupported.getMembers();
/*      */       int i;
/* 1272 */       int j; if ((arrayOfInt.length > 0) && (arrayOfInt[0].length > 0)) {
/* 1273 */         i = arrayOfInt[0][0];
/* 1274 */         j = arrayOfInt[0][1];
/*      */       } else {
/* 1276 */         i = 1;
/* 1277 */         j = Integer.MAX_VALUE;
/*      */       }
/* 1279 */       this.snModel.setMinimum(new Integer(i));
/* 1280 */       this.snModel.setMaximum(new Integer(j));
/*      */       
/* 1282 */       int k = localCopies.getValue();
/* 1283 */       if ((k < i) || (k > j)) {
/* 1284 */         k = i;
/*      */       }
/* 1286 */       this.snModel.setValue(new Integer(k));
/*      */       
/*      */ 
/* 1289 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass3)) {
/* 1290 */         this.scSupported = true;
/*      */       }
/* 1292 */       SheetCollate localSheetCollate = (SheetCollate)ServiceDialog.this.asCurrent.get(localClass3);
/* 1293 */       if (localSheetCollate == null) {
/* 1294 */         localSheetCollate = (SheetCollate)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass3);
/* 1295 */         if (localSheetCollate == null) {
/* 1296 */           localSheetCollate = SheetCollate.UNCOLLATED;
/*      */         }
/*      */       }
/* 1299 */       this.cbCollate.setSelected(localSheetCollate == SheetCollate.COLLATED);
/* 1300 */       updateCollateCB();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class PageSetupPanel
/*      */     extends JPanel
/*      */   {
/*      */     private MediaPanel pnlMedia;
/*      */     
/*      */ 
/*      */     private OrientationPanel pnlOrientation;
/*      */     
/*      */ 
/*      */     private MarginsPanel pnlMargins;
/*      */     
/*      */ 
/*      */     public PageSetupPanel()
/*      */     {
/* 1320 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 1321 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/* 1323 */       setLayout(localGridBagLayout);
/*      */       
/* 1325 */       localGridBagConstraints.fill = 1;
/* 1326 */       localGridBagConstraints.insets = ServiceDialog.panelInsets;
/* 1327 */       localGridBagConstraints.weightx = 1.0D;
/* 1328 */       localGridBagConstraints.weighty = 1.0D;
/*      */       
/* 1330 */       localGridBagConstraints.gridwidth = 0;
/* 1331 */       this.pnlMedia = new MediaPanel(ServiceDialog.this);
/* 1332 */       ServiceDialog.addToGB(this.pnlMedia, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1334 */       this.pnlOrientation = new OrientationPanel(ServiceDialog.this);
/* 1335 */       localGridBagConstraints.gridwidth = -1;
/* 1336 */       ServiceDialog.addToGB(this.pnlOrientation, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1338 */       this.pnlMargins = new MarginsPanel(ServiceDialog.this);
/* 1339 */       this.pnlOrientation.addOrientationListener(this.pnlMargins);
/* 1340 */       this.pnlMedia.addMediaListener(this.pnlMargins);
/* 1341 */       localGridBagConstraints.gridwidth = 0;
/* 1342 */       ServiceDialog.addToGB(this.pnlMargins, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void updateInfo() {
/* 1346 */       this.pnlMedia.updateInfo();
/* 1347 */       this.pnlOrientation.updateInfo();
/* 1348 */       this.pnlMargins.updateInfo();
/*      */     }
/*      */   }
/*      */   
/*      */   private class MarginsPanel
/*      */     extends JPanel implements ActionListener, FocusListener
/*      */   {
/* 1355 */     private final String strTitle = ServiceDialog.getMsg("border.margins");
/*      */     private JFormattedTextField leftMargin;
/*      */     private JFormattedTextField rightMargin;
/*      */     private JFormattedTextField topMargin;
/* 1359 */     private JFormattedTextField bottomMargin; private JLabel lblLeft; private JLabel lblRight; private JLabel lblTop; private JLabel lblBottom; private int units = 1000;
/*      */     
/* 1361 */     private float lmVal = -1.0F; private float rmVal = -1.0F; private float tmVal = -1.0F; private float bmVal = -1.0F;
/*      */     private Float lmObj;
/*      */     private Float rmObj;
/*      */     private Float tmObj;
/*      */     private Float bmObj;
/*      */     
/*      */     public MarginsPanel() {
/* 1368 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 1369 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/* 1370 */       localGridBagConstraints.fill = 2;
/* 1371 */       localGridBagConstraints.weightx = 1.0D;
/* 1372 */       localGridBagConstraints.weighty = 0.0D;
/* 1373 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/*      */       
/* 1375 */       setLayout(localGridBagLayout);
/* 1376 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/* 1378 */       String str1 = "label.millimetres";
/* 1379 */       String str2 = Locale.getDefault().getCountry();
/* 1380 */       if ((str2 != null) && (
/* 1381 */         (str2.equals("")) || 
/* 1382 */         (str2.equals(Locale.US.getCountry())) || 
/* 1383 */         (str2.equals(Locale.CANADA.getCountry())))) {
/* 1384 */         str1 = "label.inches";
/* 1385 */         this.units = 25400;
/*      */       }
/* 1387 */       String str3 = ServiceDialog.getMsg(str1);
/*      */       
/*      */       DecimalFormat localDecimalFormat;
/* 1390 */       if (this.units == 1000) {
/* 1391 */         localDecimalFormat = new DecimalFormat("###.##");
/* 1392 */         localDecimalFormat.setMaximumIntegerDigits(3);
/*      */       } else {
/* 1394 */         localDecimalFormat = new DecimalFormat("##.##");
/* 1395 */         localDecimalFormat.setMaximumIntegerDigits(2);
/*      */       }
/*      */       
/* 1398 */       localDecimalFormat.setMinimumFractionDigits(1);
/* 1399 */       localDecimalFormat.setMaximumFractionDigits(2);
/* 1400 */       localDecimalFormat.setMinimumIntegerDigits(1);
/* 1401 */       localDecimalFormat.setParseIntegerOnly(false);
/* 1402 */       localDecimalFormat.setDecimalSeparatorAlwaysShown(true);
/* 1403 */       NumberFormatter localNumberFormatter = new NumberFormatter(localDecimalFormat);
/* 1404 */       localNumberFormatter.setMinimum(new Float(0.0F));
/* 1405 */       localNumberFormatter.setMaximum(new Float(999.0F));
/* 1406 */       localNumberFormatter.setAllowsInvalid(true);
/* 1407 */       localNumberFormatter.setCommitsOnValidEdit(true);
/*      */       
/* 1409 */       this.leftMargin = new JFormattedTextField(localNumberFormatter);
/* 1410 */       this.leftMargin.addFocusListener(this);
/* 1411 */       this.leftMargin.addActionListener(this);
/* 1412 */       this.leftMargin.getAccessibleContext().setAccessibleName(
/* 1413 */         ServiceDialog.getMsg("label.leftmargin"));
/* 1414 */       this.rightMargin = new JFormattedTextField(localNumberFormatter);
/* 1415 */       this.rightMargin.addFocusListener(this);
/* 1416 */       this.rightMargin.addActionListener(this);
/* 1417 */       this.rightMargin.getAccessibleContext().setAccessibleName(
/* 1418 */         ServiceDialog.getMsg("label.rightmargin"));
/* 1419 */       this.topMargin = new JFormattedTextField(localNumberFormatter);
/* 1420 */       this.topMargin.addFocusListener(this);
/* 1421 */       this.topMargin.addActionListener(this);
/* 1422 */       this.topMargin.getAccessibleContext().setAccessibleName(
/* 1423 */         ServiceDialog.getMsg("label.topmargin"));
/* 1424 */       this.topMargin = new JFormattedTextField(localNumberFormatter);
/* 1425 */       this.bottomMargin = new JFormattedTextField(localNumberFormatter);
/* 1426 */       this.bottomMargin.addFocusListener(this);
/* 1427 */       this.bottomMargin.addActionListener(this);
/* 1428 */       this.bottomMargin.getAccessibleContext().setAccessibleName(
/* 1429 */         ServiceDialog.getMsg("label.bottommargin"));
/* 1430 */       this.topMargin = new JFormattedTextField(localNumberFormatter);
/* 1431 */       localGridBagConstraints.gridwidth = -1;
/* 1432 */       this.lblLeft = new JLabel(ServiceDialog.getMsg("label.leftmargin") + " " + str3, 10);
/*      */       
/* 1434 */       this.lblLeft.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.leftmargin"));
/* 1435 */       this.lblLeft.setLabelFor(this.leftMargin);
/* 1436 */       ServiceDialog.addToGB(this.lblLeft, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1438 */       localGridBagConstraints.gridwidth = 0;
/* 1439 */       this.lblRight = new JLabel(ServiceDialog.getMsg("label.rightmargin") + " " + str3, 10);
/*      */       
/* 1441 */       this.lblRight.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.rightmargin"));
/* 1442 */       this.lblRight.setLabelFor(this.rightMargin);
/* 1443 */       ServiceDialog.addToGB(this.lblRight, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1445 */       localGridBagConstraints.gridwidth = -1;
/* 1446 */       ServiceDialog.addToGB(this.leftMargin, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1448 */       localGridBagConstraints.gridwidth = 0;
/* 1449 */       ServiceDialog.addToGB(this.rightMargin, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/*      */ 
/* 1452 */       ServiceDialog.addToGB(new JPanel(), this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1454 */       localGridBagConstraints.gridwidth = -1;
/* 1455 */       this.lblTop = new JLabel(ServiceDialog.getMsg("label.topmargin") + " " + str3, 10);
/*      */       
/* 1457 */       this.lblTop.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.topmargin"));
/* 1458 */       this.lblTop.setLabelFor(this.topMargin);
/* 1459 */       ServiceDialog.addToGB(this.lblTop, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1461 */       localGridBagConstraints.gridwidth = 0;
/* 1462 */       this.lblBottom = new JLabel(ServiceDialog.getMsg("label.bottommargin") + " " + str3, 10);
/*      */       
/* 1464 */       this.lblBottom.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.bottommargin"));
/* 1465 */       this.lblBottom.setLabelFor(this.bottomMargin);
/* 1466 */       ServiceDialog.addToGB(this.lblBottom, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1468 */       localGridBagConstraints.gridwidth = -1;
/* 1469 */       ServiceDialog.addToGB(this.topMargin, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1471 */       localGridBagConstraints.gridwidth = 0;
/* 1472 */       ServiceDialog.addToGB(this.bottomMargin, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 1477 */       Object localObject = paramActionEvent.getSource();
/* 1478 */       updateMargins(localObject);
/*      */     }
/*      */     
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/* 1482 */       Object localObject = paramFocusEvent.getSource();
/* 1483 */       updateMargins(localObject);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void focusGained(FocusEvent paramFocusEvent) {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void updateMargins(Object paramObject)
/*      */     {
/* 1494 */       if (!(paramObject instanceof JFormattedTextField)) {
/* 1495 */         return;
/*      */       }
/* 1497 */       Object localObject = (JFormattedTextField)paramObject;
/* 1498 */       Float localFloat1 = (Float)((JFormattedTextField)localObject).getValue();
/* 1499 */       if (localFloat1 == null) {
/* 1500 */         return;
/*      */       }
/* 1502 */       if ((localObject == this.leftMargin) && (localFloat1.equals(this.lmObj))) {
/* 1503 */         return;
/*      */       }
/* 1505 */       if ((localObject == this.rightMargin) && (localFloat1.equals(this.rmObj))) {
/* 1506 */         return;
/*      */       }
/* 1508 */       if ((localObject == this.topMargin) && (localFloat1.equals(this.tmObj))) {
/* 1509 */         return;
/*      */       }
/* 1511 */       if ((localObject == this.bottomMargin) && (localFloat1.equals(this.bmObj))) {
/* 1512 */         return;
/*      */       }
/*      */       
/*      */ 
/* 1516 */       localObject = (Float)this.leftMargin.getValue();
/* 1517 */       localFloat1 = (Float)this.rightMargin.getValue();
/* 1518 */       Float localFloat2 = (Float)this.topMargin.getValue();
/* 1519 */       Float localFloat3 = (Float)this.bottomMargin.getValue();
/*      */       
/* 1521 */       float f1 = ((Float)localObject).floatValue();
/* 1522 */       float f2 = localFloat1.floatValue();
/* 1523 */       float f3 = localFloat2.floatValue();
/* 1524 */       float f4 = localFloat3.floatValue();
/*      */       
/*      */ 
/* 1527 */       Class localClass = OrientationRequested.class;
/*      */       
/* 1529 */       OrientationRequested localOrientationRequested = (OrientationRequested)ServiceDialog.this.asCurrent.get(localClass);
/*      */       
/* 1531 */       if (localOrientationRequested == null)
/*      */       {
/* 1533 */         localOrientationRequested = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass);
/*      */       }
/*      */       
/*      */       float f5;
/* 1537 */       if (localOrientationRequested == OrientationRequested.REVERSE_PORTRAIT) {
/* 1538 */         f5 = f1;f1 = f2;f2 = f5;
/* 1539 */         f5 = f3;f3 = f4;f4 = f5;
/* 1540 */       } else if (localOrientationRequested == OrientationRequested.LANDSCAPE) {
/* 1541 */         f5 = f1;
/* 1542 */         f1 = f3;
/* 1543 */         f3 = f2;
/* 1544 */         f2 = f4;
/* 1545 */         f4 = f5;
/* 1546 */       } else if (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
/* 1547 */         f5 = f1;
/* 1548 */         f1 = f4;
/* 1549 */         f4 = f2;
/* 1550 */         f2 = f3;
/* 1551 */         f3 = f5;
/*      */       }
/*      */       MediaPrintableArea localMediaPrintableArea;
/* 1554 */       if ((localMediaPrintableArea = validateMargins(f1, f2, f3, f4)) != null) {
/* 1555 */         ServiceDialog.this.asCurrent.add(localMediaPrintableArea);
/* 1556 */         this.lmVal = f1;
/* 1557 */         this.rmVal = f2;
/* 1558 */         this.tmVal = f3;
/* 1559 */         this.bmVal = f4;
/* 1560 */         this.lmObj = ((Float)localObject);
/* 1561 */         this.rmObj = localFloat1;
/* 1562 */         this.tmObj = localFloat2;
/* 1563 */         this.bmObj = localFloat3;
/*      */       } else {
/* 1565 */         if ((this.lmObj == null) || (this.rmObj == null) || (this.tmObj == null) || (this.rmObj == null))
/*      */         {
/* 1567 */           return;
/*      */         }
/* 1569 */         this.leftMargin.setValue(this.lmObj);
/* 1570 */         this.rightMargin.setValue(this.rmObj);
/* 1571 */         this.topMargin.setValue(this.tmObj);
/* 1572 */         this.bottomMargin.setValue(this.bmObj);
/*      */       }
/*      */     }
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
/*      */     private MediaPrintableArea validateMargins(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*      */     {
/* 1591 */       Class localClass = MediaPrintableArea.class;
/*      */       
/* 1593 */       MediaPrintableArea localMediaPrintableArea = null;
/* 1594 */       MediaSize localMediaSize = null;
/*      */       
/* 1596 */       Media localMedia = (Media)ServiceDialog.this.asCurrent.get(Media.class);
/* 1597 */       if ((localMedia == null) || (!(localMedia instanceof MediaSizeName)))
/* 1598 */         localMedia = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
/*      */       Object localObject1;
/* 1600 */       if ((localMedia != null) && ((localMedia instanceof MediaSizeName))) {
/* 1601 */         localObject1 = (MediaSizeName)localMedia;
/* 1602 */         localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject1);
/*      */       }
/* 1604 */       if (localMediaSize == null) {
/* 1605 */         localMediaSize = new MediaSize(8.5F, 11.0F, 25400);
/*      */       }
/*      */       
/* 1608 */       if (localMedia != null)
/*      */       {
/* 1610 */         localObject1 = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
/* 1611 */         ((PrintRequestAttributeSet)localObject1).add(localMedia);
/*      */         
/*      */ 
/* 1614 */         Object localObject2 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localClass, 
/* 1615 */           ServiceDialog.this.docFlavor, (AttributeSet)localObject1);
/*      */         
/* 1617 */         if (((localObject2 instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject2).length > 0))
/*      */         {
/* 1619 */           localMediaPrintableArea = ((MediaPrintableArea[])(MediaPrintableArea[])localObject2)[0];
/*      */         }
/*      */       }
/*      */       
/* 1623 */       if (localMediaPrintableArea == null)
/*      */       {
/*      */ 
/* 1626 */         localMediaPrintableArea = new MediaPrintableArea(0.0F, 0.0F, localMediaSize.getX(this.units), localMediaSize.getY(this.units), this.units);
/*      */       }
/*      */       
/*      */ 
/* 1630 */       float f1 = localMediaSize.getX(this.units);
/* 1631 */       float f2 = localMediaSize.getY(this.units);
/* 1632 */       float f3 = paramFloat1;
/* 1633 */       float f4 = paramFloat3;
/* 1634 */       float f5 = f1 - paramFloat1 - paramFloat2;
/* 1635 */       float f6 = f2 - paramFloat3 - paramFloat4;
/*      */       
/* 1637 */       if ((f5 <= 0.0F) || (f6 <= 0.0F) || (f3 < 0.0F) || (f4 < 0.0F) || 
/* 1638 */         (f3 < localMediaPrintableArea.getX(this.units)) || (f5 > localMediaPrintableArea.getWidth(this.units)) || 
/* 1639 */         (f4 < localMediaPrintableArea.getY(this.units)) || (f6 > localMediaPrintableArea.getHeight(this.units))) {
/* 1640 */         return null;
/*      */       }
/* 1642 */       return new MediaPrintableArea(paramFloat1, paramFloat3, f5, f6, this.units);
/*      */     }
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
/*      */     public void updateInfo()
/*      */     {
/* 1661 */       if (ServiceDialog.this.isAWT) {
/* 1662 */         this.leftMargin.setEnabled(false);
/* 1663 */         this.rightMargin.setEnabled(false);
/* 1664 */         this.topMargin.setEnabled(false);
/* 1665 */         this.bottomMargin.setEnabled(false);
/* 1666 */         this.lblLeft.setEnabled(false);
/* 1667 */         this.lblRight.setEnabled(false);
/* 1668 */         this.lblTop.setEnabled(false);
/* 1669 */         this.lblBottom.setEnabled(false);
/* 1670 */         return;
/*      */       }
/*      */       
/* 1673 */       Class localClass1 = MediaPrintableArea.class;
/*      */       
/* 1675 */       MediaPrintableArea localMediaPrintableArea1 = (MediaPrintableArea)ServiceDialog.this.asCurrent.get(localClass1);
/* 1676 */       MediaPrintableArea localMediaPrintableArea2 = null;
/* 1677 */       MediaSize localMediaSize = null;
/*      */       
/* 1679 */       Media localMedia = (Media)ServiceDialog.this.asCurrent.get(Media.class);
/* 1680 */       if ((localMedia == null) || (!(localMedia instanceof MediaSizeName)))
/* 1681 */         localMedia = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
/*      */       Object localObject1;
/* 1683 */       if ((localMedia != null) && ((localMedia instanceof MediaSizeName))) {
/* 1684 */         localObject1 = (MediaSizeName)localMedia;
/* 1685 */         localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject1);
/*      */       }
/* 1687 */       if (localMediaSize == null) {
/* 1688 */         localMediaSize = new MediaSize(8.5F, 11.0F, 25400);
/*      */       }
/*      */       
/* 1691 */       if (localMedia != null)
/*      */       {
/* 1693 */         localObject1 = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
/* 1694 */         ((PrintRequestAttributeSet)localObject1).add(localMedia);
/*      */         
/*      */ 
/* 1697 */         Object localObject2 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localClass1, 
/* 1698 */           ServiceDialog.this.docFlavor, (AttributeSet)localObject1);
/*      */         
/* 1700 */         if (((localObject2 instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject2).length > 0))
/*      */         {
/* 1702 */           localMediaPrintableArea2 = ((MediaPrintableArea[])(MediaPrintableArea[])localObject2)[0];
/*      */         }
/* 1704 */         else if ((localObject2 instanceof MediaPrintableArea)) {
/* 1705 */           localMediaPrintableArea2 = (MediaPrintableArea)localObject2;
/*      */         }
/*      */       }
/* 1708 */       if (localMediaPrintableArea2 == null)
/*      */       {
/*      */ 
/* 1711 */         localMediaPrintableArea2 = new MediaPrintableArea(0.0F, 0.0F, localMediaSize.getX(this.units), localMediaSize.getY(this.units), this.units);
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
/* 1725 */       float f1 = localMediaSize.getX(25400);
/* 1726 */       float f2 = localMediaSize.getY(25400);
/* 1727 */       float f3 = 5.0F;
/*      */       float f4;
/* 1729 */       if (f1 > f3) {
/* 1730 */         f4 = 1.0F;
/*      */       } else
/* 1732 */         f4 = f1 / f3;
/*      */       float f5;
/* 1734 */       if (f2 > f3) {
/* 1735 */         f5 = 1.0F;
/*      */       } else {
/* 1737 */         f5 = f2 / f3;
/*      */       }
/*      */       
/* 1740 */       if (localMediaPrintableArea1 == null) {
/* 1741 */         localMediaPrintableArea1 = new MediaPrintableArea(f4, f5, f1 - 2.0F * f4, f2 - 2.0F * f5, 25400);
/*      */         
/*      */ 
/* 1744 */         ServiceDialog.this.asCurrent.add(localMediaPrintableArea1);
/*      */       }
/* 1746 */       float f6 = localMediaPrintableArea1.getX(this.units);
/* 1747 */       float f7 = localMediaPrintableArea1.getY(this.units);
/* 1748 */       float f8 = localMediaPrintableArea1.getWidth(this.units);
/* 1749 */       float f9 = localMediaPrintableArea1.getHeight(this.units);
/* 1750 */       float f10 = localMediaPrintableArea2.getX(this.units);
/* 1751 */       float f11 = localMediaPrintableArea2.getY(this.units);
/* 1752 */       float f12 = localMediaPrintableArea2.getWidth(this.units);
/* 1753 */       float f13 = localMediaPrintableArea2.getHeight(this.units);
/*      */       
/*      */ 
/* 1756 */       int i = 0;
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
/* 1771 */       f1 = localMediaSize.getX(this.units);
/* 1772 */       f2 = localMediaSize.getY(this.units);
/* 1773 */       if (this.lmVal >= 0.0F) {
/* 1774 */         i = 1;
/*      */         
/* 1776 */         if (this.lmVal + this.rmVal > f1)
/*      */         {
/* 1778 */           if (f8 > f12) {
/* 1779 */             f8 = f12;
/*      */           }
/*      */           
/* 1782 */           f6 = (f1 - f8) / 2.0F;
/*      */         } else {
/* 1784 */           f6 = this.lmVal >= f10 ? this.lmVal : f10;
/* 1785 */           f8 = f1 - f6 - this.rmVal;
/*      */         }
/* 1787 */         if (this.tmVal + this.bmVal > f2) {
/* 1788 */           if (f9 > f13) {
/* 1789 */             f9 = f13;
/*      */           }
/* 1791 */           f7 = (f2 - f9) / 2.0F;
/*      */         } else {
/* 1793 */           f7 = this.tmVal >= f11 ? this.tmVal : f11;
/* 1794 */           f9 = f2 - f7 - this.bmVal;
/*      */         }
/*      */       }
/* 1797 */       if (f6 < f10) {
/* 1798 */         i = 1;
/* 1799 */         f6 = f10;
/*      */       }
/* 1801 */       if (f7 < f11) {
/* 1802 */         i = 1;
/* 1803 */         f7 = f11;
/*      */       }
/* 1805 */       if (f8 > f12) {
/* 1806 */         i = 1;
/* 1807 */         f8 = f12;
/*      */       }
/* 1809 */       if (f9 > f13) {
/* 1810 */         i = 1;
/* 1811 */         f9 = f13;
/*      */       }
/*      */       
/* 1814 */       if ((f6 + f8 > f10 + f12) || (f8 <= 0.0F)) {
/* 1815 */         i = 1;
/* 1816 */         f6 = f10;
/* 1817 */         f8 = f12;
/*      */       }
/* 1819 */       if ((f7 + f9 > f11 + f13) || (f9 <= 0.0F)) {
/* 1820 */         i = 1;
/* 1821 */         f7 = f11;
/* 1822 */         f9 = f13;
/*      */       }
/*      */       
/* 1825 */       if (i != 0) {
/* 1826 */         localMediaPrintableArea1 = new MediaPrintableArea(f6, f7, f8, f9, this.units);
/* 1827 */         ServiceDialog.this.asCurrent.add(localMediaPrintableArea1);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1833 */       this.lmVal = f6;
/* 1834 */       this.tmVal = f7;
/* 1835 */       this.rmVal = (localMediaSize.getX(this.units) - f6 - f8);
/* 1836 */       this.bmVal = (localMediaSize.getY(this.units) - f7 - f9);
/*      */       
/* 1838 */       this.lmObj = new Float(this.lmVal);
/* 1839 */       this.rmObj = new Float(this.rmVal);
/* 1840 */       this.tmObj = new Float(this.tmVal);
/* 1841 */       this.bmObj = new Float(this.bmVal);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1847 */       Class localClass2 = OrientationRequested.class;
/*      */       
/* 1849 */       OrientationRequested localOrientationRequested = (OrientationRequested)ServiceDialog.this.asCurrent.get(localClass2);
/*      */       
/* 1851 */       if (localOrientationRequested == null)
/*      */       {
/* 1853 */         localOrientationRequested = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass2);
/*      */       }
/*      */       
/*      */       Float localFloat;
/*      */       
/* 1858 */       if (localOrientationRequested == OrientationRequested.REVERSE_PORTRAIT) {
/* 1859 */         localFloat = this.lmObj;this.lmObj = this.rmObj;this.rmObj = localFloat;
/* 1860 */         localFloat = this.tmObj;this.tmObj = this.bmObj;this.bmObj = localFloat;
/* 1861 */       } else if (localOrientationRequested == OrientationRequested.LANDSCAPE) {
/* 1862 */         localFloat = this.lmObj;
/* 1863 */         this.lmObj = this.bmObj;
/* 1864 */         this.bmObj = this.rmObj;
/* 1865 */         this.rmObj = this.tmObj;
/* 1866 */         this.tmObj = localFloat;
/* 1867 */       } else if (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
/* 1868 */         localFloat = this.lmObj;
/* 1869 */         this.lmObj = this.tmObj;
/* 1870 */         this.tmObj = this.rmObj;
/* 1871 */         this.rmObj = this.bmObj;
/* 1872 */         this.bmObj = localFloat;
/*      */       }
/*      */       
/* 1875 */       this.leftMargin.setValue(this.lmObj);
/* 1876 */       this.rightMargin.setValue(this.rmObj);
/* 1877 */       this.topMargin.setValue(this.tmObj);
/* 1878 */       this.bottomMargin.setValue(this.bmObj);
/*      */     }
/*      */   }
/*      */   
/*      */   private class MediaPanel extends JPanel implements ItemListener
/*      */   {
/* 1884 */     private final String strTitle = ServiceDialog.getMsg("border.media");
/*      */     private JLabel lblSize;
/*      */     private JLabel lblSource;
/* 1887 */     private JComboBox cbSize; private JComboBox cbSource; private Vector sizes = new Vector();
/* 1888 */     private Vector sources = new Vector();
/* 1889 */     private MarginsPanel pnlMargins = null;
/*      */     
/*      */ 
/*      */     public MediaPanel()
/*      */     {
/* 1894 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 1895 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/* 1897 */       setLayout(localGridBagLayout);
/* 1898 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/* 1900 */       this.cbSize = new JComboBox();
/* 1901 */       this.cbSource = new JComboBox();
/*      */       
/* 1903 */       localGridBagConstraints.fill = 1;
/* 1904 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/* 1905 */       localGridBagConstraints.weighty = 1.0D;
/*      */       
/* 1907 */       localGridBagConstraints.weightx = 0.0D;
/* 1908 */       this.lblSize = new JLabel(ServiceDialog.getMsg("label.size"), 11);
/* 1909 */       this.lblSize.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.size"));
/* 1910 */       this.lblSize.setLabelFor(this.cbSize);
/* 1911 */       ServiceDialog.addToGB(this.lblSize, this, localGridBagLayout, localGridBagConstraints);
/* 1912 */       localGridBagConstraints.weightx = 1.0D;
/* 1913 */       localGridBagConstraints.gridwidth = 0;
/* 1914 */       ServiceDialog.addToGB(this.cbSize, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 1916 */       localGridBagConstraints.weightx = 0.0D;
/* 1917 */       localGridBagConstraints.gridwidth = 1;
/* 1918 */       this.lblSource = new JLabel(ServiceDialog.getMsg("label.source"), 11);
/* 1919 */       this.lblSource.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.source"));
/* 1920 */       this.lblSource.setLabelFor(this.cbSource);
/* 1921 */       ServiceDialog.addToGB(this.lblSource, this, localGridBagLayout, localGridBagConstraints);
/* 1922 */       localGridBagConstraints.gridwidth = 0;
/* 1923 */       ServiceDialog.addToGB(this.cbSource, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     private String getMediaName(String paramString)
/*      */     {
/*      */       try
/*      */       {
/* 1930 */         String str = paramString.replace(' ', '-');
/* 1931 */         str = str.replace('#', 'n');
/*      */         
/* 1933 */         return ServiceDialog.messageRB.getString(str);
/*      */       } catch (MissingResourceException localMissingResourceException) {}
/* 1935 */       return paramString;
/*      */     }
/*      */     
/*      */     public void itemStateChanged(ItemEvent paramItemEvent)
/*      */     {
/* 1940 */       Object localObject1 = paramItemEvent.getSource();
/*      */       
/* 1942 */       if (paramItemEvent.getStateChange() == 1) { int i;
/* 1943 */         Object localObject2; if (localObject1 == this.cbSize) {
/* 1944 */           i = this.cbSize.getSelectedIndex();
/*      */           
/* 1946 */           if ((i >= 0) && (i < this.sizes.size())) {
/* 1947 */             if ((this.cbSource.getItemCount() > 1) && 
/* 1948 */               (this.cbSource.getSelectedIndex() >= 1))
/*      */             {
/* 1950 */               int j = this.cbSource.getSelectedIndex() - 1;
/* 1951 */               localObject2 = (MediaTray)this.sources.get(j);
/* 1952 */               ServiceDialog.this.asCurrent.add(new SunAlternateMedia((Media)localObject2));
/*      */             }
/* 1954 */             ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(i));
/*      */           }
/* 1956 */         } else if (localObject1 == this.cbSource) {
/* 1957 */           i = this.cbSource.getSelectedIndex();
/*      */           
/* 1959 */           if ((i >= 1) && (i < this.sources.size() + 1)) {
/* 1960 */             ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
/* 1961 */             MediaTray localMediaTray = (MediaTray)this.sources.get(i - 1);
/* 1962 */             localObject2 = (Media)ServiceDialog.this.asCurrent.get(Media.class);
/* 1963 */             if ((localObject2 == null) || ((localObject2 instanceof MediaTray))) {
/* 1964 */               ServiceDialog.this.asCurrent.add(localMediaTray);
/* 1965 */             } else if ((localObject2 instanceof MediaSizeName)) {
/* 1966 */               MediaSizeName localMediaSizeName = (MediaSizeName)localObject2;
/* 1967 */               Media localMedia = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
/* 1968 */               if (((localMedia instanceof MediaSizeName)) && (localMedia.equals(localMediaSizeName))) {
/* 1969 */                 ServiceDialog.this.asCurrent.add(localMediaTray);
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/* 1974 */                 ServiceDialog.this.asCurrent.add(new SunAlternateMedia(localMediaTray));
/*      */               }
/*      */             }
/* 1977 */           } else if (i == 0) {
/* 1978 */             ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
/* 1979 */             if (this.cbSize.getItemCount() > 0) {
/* 1980 */               int k = this.cbSize.getSelectedIndex();
/* 1981 */               ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(k));
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1986 */         if (this.pnlMargins != null) {
/* 1987 */           this.pnlMargins.updateInfo();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1995 */     public void addMediaListener(MarginsPanel paramMarginsPanel) { this.pnlMargins = paramMarginsPanel; }
/*      */     
/*      */     public void updateInfo() {
/* 1998 */       Class localClass1 = Media.class;
/* 1999 */       Class localClass2 = SunAlternateMedia.class;
/* 2000 */       boolean bool1 = false;
/*      */       
/* 2002 */       this.cbSize.removeItemListener(this);
/* 2003 */       this.cbSize.removeAllItems();
/* 2004 */       this.cbSource.removeItemListener(this);
/* 2005 */       this.cbSource.removeAllItems();
/* 2006 */       this.cbSource.addItem(getMediaName("auto-select"));
/*      */       
/* 2008 */       this.sizes.clear();
/* 2009 */       this.sources.clear();
/*      */       Object localObject2;
/* 2011 */       Object localObject3; if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass1)) {
/* 2012 */         bool1 = true;
/*      */         
/*      */ 
/* 2015 */         Object localObject1 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localClass1, 
/* 2016 */           ServiceDialog.this.docFlavor, 
/* 2017 */           ServiceDialog.this.asCurrent);
/*      */         
/* 2019 */         if ((localObject1 instanceof Media[])) {
/* 2020 */           localObject2 = (Media[])localObject1;
/*      */           
/* 2022 */           for (int i = 0; i < localObject2.length; i++) {
/* 2023 */             localObject3 = localObject2[i];
/*      */             
/* 2025 */             if ((localObject3 instanceof MediaSizeName)) {
/* 2026 */               this.sizes.add(localObject3);
/* 2027 */               this.cbSize.addItem(getMediaName(((Media)localObject3).toString()));
/* 2028 */             } else if ((localObject3 instanceof MediaTray)) {
/* 2029 */               this.sources.add(localObject3);
/* 2030 */               this.cbSource.addItem(getMediaName(((Media)localObject3).toString()));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2036 */       boolean bool2 = (bool1) && (this.sizes.size() > 0);
/* 2037 */       this.lblSize.setEnabled(bool2);
/* 2038 */       this.cbSize.setEnabled(bool2);
/*      */       
/* 2040 */       if (ServiceDialog.this.isAWT) {
/* 2041 */         this.cbSource.setEnabled(false);
/* 2042 */         this.lblSource.setEnabled(false);
/*      */       } else {
/* 2044 */         this.cbSource.setEnabled(bool1);
/*      */       }
/*      */       
/* 2047 */       if (bool1)
/*      */       {
/* 2049 */         localObject2 = (Media)ServiceDialog.this.asCurrent.get(localClass1);
/*      */         
/*      */ 
/* 2052 */         Media localMedia1 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass1);
/* 2053 */         if ((localMedia1 instanceof MediaSizeName)) {
/* 2054 */           this.cbSize.setSelectedIndex(this.sizes.size() > 0 ? this.sizes.indexOf(localMedia1) : -1);
/*      */         }
/*      */         
/* 2057 */         if ((localObject2 == null) || 
/* 2058 */           (!ServiceDialog.this.psCurrent.isAttributeValueSupported((Attribute)localObject2, 
/* 2059 */           ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)))
/*      */         {
/* 2061 */           localObject2 = localMedia1;
/*      */           
/* 2063 */           if ((localObject2 == null) && 
/* 2064 */             (this.sizes.size() > 0)) {
/* 2065 */             localObject2 = (Media)this.sizes.get(0);
/*      */           }
/*      */           
/* 2068 */           if (localObject2 != null) {
/* 2069 */             ServiceDialog.this.asCurrent.add((Attribute)localObject2);
/*      */           }
/*      */         }
/* 2072 */         if (localObject2 != null) {
/* 2073 */           if ((localObject2 instanceof MediaSizeName)) {
/* 2074 */             localObject3 = (MediaSizeName)localObject2;
/* 2075 */             this.cbSize.setSelectedIndex(this.sizes.indexOf(localObject3));
/* 2076 */           } else if ((localObject2 instanceof MediaTray)) {
/* 2077 */             localObject3 = (MediaTray)localObject2;
/* 2078 */             this.cbSource.setSelectedIndex(this.sources.indexOf(localObject3) + 1);
/*      */           }
/*      */         } else {
/* 2081 */           this.cbSize.setSelectedIndex(this.sizes.size() > 0 ? 0 : -1);
/* 2082 */           this.cbSource.setSelectedIndex(0);
/*      */         }
/*      */         
/* 2085 */         localObject3 = (SunAlternateMedia)ServiceDialog.this.asCurrent.get(localClass2);
/* 2086 */         MediaTray localMediaTray; if (localObject3 != null) {
/* 2087 */           Media localMedia2 = ((SunAlternateMedia)localObject3).getMedia();
/* 2088 */           if ((localMedia2 instanceof MediaTray)) {
/* 2089 */             localMediaTray = (MediaTray)localMedia2;
/* 2090 */             this.cbSource.setSelectedIndex(this.sources.indexOf(localMediaTray) + 1);
/*      */           }
/*      */         }
/*      */         
/* 2094 */         int j = this.cbSize.getSelectedIndex();
/* 2095 */         if ((j >= 0) && (j < this.sizes.size())) {
/* 2096 */           ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(j));
/*      */         }
/*      */         
/* 2099 */         j = this.cbSource.getSelectedIndex();
/* 2100 */         if ((j >= 1) && (j < this.sources.size() + 1)) {
/* 2101 */           localMediaTray = (MediaTray)this.sources.get(j - 1);
/* 2102 */           if ((localObject2 instanceof MediaTray)) {
/* 2103 */             ServiceDialog.this.asCurrent.add(localMediaTray);
/*      */           } else {
/* 2105 */             ServiceDialog.this.asCurrent.add(new SunAlternateMedia(localMediaTray));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2111 */       this.cbSize.addItemListener(this);
/* 2112 */       this.cbSource.addItemListener(this);
/*      */     }
/*      */   }
/*      */   
/*      */   private class OrientationPanel
/*      */     extends JPanel implements ActionListener
/*      */   {
/* 2119 */     private final String strTitle = ServiceDialog.getMsg("border.orientation");
/*      */     private IconRadioButton rbPortrait;
/*      */     private IconRadioButton rbLandscape;
/* 2122 */     private IconRadioButton rbRevPortrait; private IconRadioButton rbRevLandscape; private MarginsPanel pnlMargins = null;
/*      */     
/*      */ 
/*      */     public OrientationPanel()
/*      */     {
/* 2127 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2128 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/* 2130 */       setLayout(localGridBagLayout);
/* 2131 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/* 2133 */       localGridBagConstraints.fill = 1;
/* 2134 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/* 2135 */       localGridBagConstraints.weighty = 1.0D;
/* 2136 */       localGridBagConstraints.gridwidth = 0;
/*      */       
/* 2138 */       ButtonGroup localButtonGroup = new ButtonGroup();
/* 2139 */       this.rbPortrait = new IconRadioButton(ServiceDialog.this, "radiobutton.portrait", "orientPortrait.png", true, localButtonGroup, this);
/*      */       
/*      */ 
/* 2142 */       this.rbPortrait.addActionListener(this);
/* 2143 */       ServiceDialog.addToGB(this.rbPortrait, this, localGridBagLayout, localGridBagConstraints);
/* 2144 */       this.rbLandscape = new IconRadioButton(ServiceDialog.this, "radiobutton.landscape", "orientLandscape.png", false, localButtonGroup, this);
/*      */       
/*      */ 
/* 2147 */       this.rbLandscape.addActionListener(this);
/* 2148 */       ServiceDialog.addToGB(this.rbLandscape, this, localGridBagLayout, localGridBagConstraints);
/* 2149 */       this.rbRevPortrait = new IconRadioButton(ServiceDialog.this, "radiobutton.revportrait", "orientRevPortrait.png", false, localButtonGroup, this);
/*      */       
/*      */ 
/* 2152 */       this.rbRevPortrait.addActionListener(this);
/* 2153 */       ServiceDialog.addToGB(this.rbRevPortrait, this, localGridBagLayout, localGridBagConstraints);
/* 2154 */       this.rbRevLandscape = new IconRadioButton(ServiceDialog.this, "radiobutton.revlandscape", "orientRevLandscape.png", false, localButtonGroup, this);
/*      */       
/*      */ 
/* 2157 */       this.rbRevLandscape.addActionListener(this);
/* 2158 */       ServiceDialog.addToGB(this.rbRevLandscape, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2162 */       Object localObject = paramActionEvent.getSource();
/*      */       
/* 2164 */       if (this.rbPortrait.isSameAs(localObject)) {
/* 2165 */         ServiceDialog.this.asCurrent.add(OrientationRequested.PORTRAIT);
/* 2166 */       } else if (this.rbLandscape.isSameAs(localObject)) {
/* 2167 */         ServiceDialog.this.asCurrent.add(OrientationRequested.LANDSCAPE);
/* 2168 */       } else if (this.rbRevPortrait.isSameAs(localObject)) {
/* 2169 */         ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_PORTRAIT);
/* 2170 */       } else if (this.rbRevLandscape.isSameAs(localObject)) {
/* 2171 */         ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_LANDSCAPE);
/*      */       }
/*      */       
/* 2174 */       if (this.pnlMargins != null) {
/* 2175 */         this.pnlMargins.updateInfo();
/*      */       }
/*      */     }
/*      */     
/*      */     void addOrientationListener(MarginsPanel paramMarginsPanel)
/*      */     {
/* 2181 */       this.pnlMargins = paramMarginsPanel;
/*      */     }
/*      */     
/*      */     public void updateInfo() {
/* 2185 */       Class localClass = OrientationRequested.class;
/* 2186 */       boolean bool1 = false;
/* 2187 */       boolean bool2 = false;
/* 2188 */       boolean bool3 = false;
/* 2189 */       boolean bool4 = false;
/*      */       Object localObject2;
/* 2191 */       if (ServiceDialog.this.isAWT) {
/* 2192 */         bool1 = true;
/* 2193 */         bool2 = true;
/*      */       }
/* 2195 */       else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass))
/*      */       {
/* 2197 */         localObject1 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localClass, 
/* 2198 */           ServiceDialog.this.docFlavor, 
/* 2199 */           ServiceDialog.this.asCurrent);
/*      */         
/* 2201 */         if ((localObject1 instanceof OrientationRequested[])) {
/* 2202 */           localObject2 = (OrientationRequested[])localObject1;
/*      */           
/*      */ 
/* 2205 */           for (int i = 0; i < localObject2.length; i++) {
/* 2206 */             Object localObject3 = localObject2[i];
/*      */             
/* 2208 */             if (localObject3 == OrientationRequested.PORTRAIT) {
/* 2209 */               bool1 = true;
/* 2210 */             } else if (localObject3 == OrientationRequested.LANDSCAPE) {
/* 2211 */               bool2 = true;
/* 2212 */             } else if (localObject3 == OrientationRequested.REVERSE_PORTRAIT) {
/* 2213 */               bool3 = true;
/* 2214 */             } else if (localObject3 == OrientationRequested.REVERSE_LANDSCAPE) {
/* 2215 */               bool4 = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2222 */       this.rbPortrait.setEnabled(bool1);
/* 2223 */       this.rbLandscape.setEnabled(bool2);
/* 2224 */       this.rbRevPortrait.setEnabled(bool3);
/* 2225 */       this.rbRevLandscape.setEnabled(bool4);
/*      */       
/* 2227 */       Object localObject1 = (OrientationRequested)ServiceDialog.this.asCurrent.get(localClass);
/* 2228 */       if ((localObject1 == null) || 
/* 2229 */         (!ServiceDialog.this.psCurrent.isAttributeValueSupported((Attribute)localObject1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)))
/*      */       {
/* 2231 */         localObject1 = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass);
/*      */         
/* 2233 */         if ((localObject1 != null) && 
/* 2234 */           (!ServiceDialog.this.psCurrent.isAttributeValueSupported((Attribute)localObject1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent))) {
/* 2235 */           localObject1 = null;
/*      */           
/* 2237 */           localObject2 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localClass, 
/* 2238 */             ServiceDialog.this.docFlavor, 
/* 2239 */             ServiceDialog.this.asCurrent);
/* 2240 */           if ((localObject2 instanceof OrientationRequested[])) {
/* 2241 */             OrientationRequested[] arrayOfOrientationRequested = (OrientationRequested[])localObject2;
/*      */             
/* 2243 */             if (arrayOfOrientationRequested.length > 1)
/*      */             {
/* 2245 */               localObject1 = arrayOfOrientationRequested[0];
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2250 */         if (localObject1 == null) {
/* 2251 */           localObject1 = OrientationRequested.PORTRAIT;
/*      */         }
/* 2253 */         ServiceDialog.this.asCurrent.add((Attribute)localObject1);
/*      */       }
/*      */       
/* 2256 */       if (localObject1 == OrientationRequested.PORTRAIT) {
/* 2257 */         this.rbPortrait.setSelected(true);
/* 2258 */       } else if (localObject1 == OrientationRequested.LANDSCAPE) {
/* 2259 */         this.rbLandscape.setSelected(true);
/* 2260 */       } else if (localObject1 == OrientationRequested.REVERSE_PORTRAIT) {
/* 2261 */         this.rbRevPortrait.setSelected(true);
/*      */       } else {
/* 2263 */         this.rbRevLandscape.setSelected(true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class AppearancePanel
/*      */     extends JPanel
/*      */   {
/*      */     private ChromaticityPanel pnlChromaticity;
/*      */     
/*      */     private QualityPanel pnlQuality;
/*      */     
/*      */     private JobAttributesPanel pnlJobAttributes;
/*      */     
/*      */     private SidesPanel pnlSides;
/*      */     
/*      */ 
/*      */     public AppearancePanel()
/*      */     {
/* 2284 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2285 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/* 2287 */       setLayout(localGridBagLayout);
/*      */       
/* 2289 */       localGridBagConstraints.fill = 1;
/* 2290 */       localGridBagConstraints.insets = ServiceDialog.panelInsets;
/* 2291 */       localGridBagConstraints.weightx = 1.0D;
/* 2292 */       localGridBagConstraints.weighty = 1.0D;
/*      */       
/* 2294 */       localGridBagConstraints.gridwidth = -1;
/* 2295 */       this.pnlChromaticity = new ChromaticityPanel(ServiceDialog.this);
/* 2296 */       ServiceDialog.addToGB(this.pnlChromaticity, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 2298 */       localGridBagConstraints.gridwidth = 0;
/* 2299 */       this.pnlQuality = new QualityPanel(ServiceDialog.this);
/* 2300 */       ServiceDialog.addToGB(this.pnlQuality, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 2302 */       localGridBagConstraints.gridwidth = 1;
/* 2303 */       this.pnlSides = new SidesPanel(ServiceDialog.this);
/* 2304 */       ServiceDialog.addToGB(this.pnlSides, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 2306 */       localGridBagConstraints.gridwidth = 0;
/* 2307 */       this.pnlJobAttributes = new JobAttributesPanel(ServiceDialog.this);
/* 2308 */       ServiceDialog.addToGB(this.pnlJobAttributes, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void updateInfo()
/*      */     {
/* 2313 */       this.pnlChromaticity.updateInfo();
/* 2314 */       this.pnlQuality.updateInfo();
/* 2315 */       this.pnlSides.updateInfo();
/* 2316 */       this.pnlJobAttributes.updateInfo();
/*      */     }
/*      */   }
/*      */   
/*      */   private class ChromaticityPanel
/*      */     extends JPanel implements ActionListener
/*      */   {
/* 2323 */     private final String strTitle = ServiceDialog.getMsg("border.chromaticity");
/*      */     private JRadioButton rbMonochrome;
/*      */     private JRadioButton rbColor;
/*      */     
/*      */     public ChromaticityPanel()
/*      */     {
/* 2329 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2330 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/* 2332 */       setLayout(localGridBagLayout);
/* 2333 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/* 2335 */       localGridBagConstraints.fill = 1;
/* 2336 */       localGridBagConstraints.gridwidth = 0;
/* 2337 */       localGridBagConstraints.weighty = 1.0D;
/*      */       
/* 2339 */       ButtonGroup localButtonGroup = new ButtonGroup();
/* 2340 */       this.rbMonochrome = ServiceDialog.createRadioButton("radiobutton.monochrome", this);
/* 2341 */       this.rbMonochrome.setSelected(true);
/* 2342 */       localButtonGroup.add(this.rbMonochrome);
/* 2343 */       ServiceDialog.addToGB(this.rbMonochrome, this, localGridBagLayout, localGridBagConstraints);
/* 2344 */       this.rbColor = ServiceDialog.createRadioButton("radiobutton.color", this);
/* 2345 */       localButtonGroup.add(this.rbColor);
/* 2346 */       ServiceDialog.addToGB(this.rbColor, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2350 */       Object localObject = paramActionEvent.getSource();
/*      */       
/*      */ 
/* 2353 */       if (localObject == this.rbMonochrome) {
/* 2354 */         ServiceDialog.this.asCurrent.add(Chromaticity.MONOCHROME);
/* 2355 */       } else if (localObject == this.rbColor) {
/* 2356 */         ServiceDialog.this.asCurrent.add(Chromaticity.COLOR);
/*      */       }
/*      */     }
/*      */     
/*      */     public void updateInfo() {
/* 2361 */       Class localClass = Chromaticity.class;
/* 2362 */       boolean bool1 = false;
/* 2363 */       boolean bool2 = false;
/*      */       
/* 2365 */       if (ServiceDialog.this.isAWT) {
/* 2366 */         bool1 = true;
/* 2367 */         bool2 = true;
/*      */       }
/* 2369 */       else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass))
/*      */       {
/* 2371 */         localObject = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localClass, 
/* 2372 */           ServiceDialog.this.docFlavor, 
/* 2373 */           ServiceDialog.this.asCurrent);
/*      */         
/* 2375 */         if ((localObject instanceof Chromaticity[])) {
/* 2376 */           Chromaticity[] arrayOfChromaticity = (Chromaticity[])localObject;
/*      */           
/* 2378 */           for (int i = 0; i < arrayOfChromaticity.length; i++) {
/* 2379 */             Chromaticity localChromaticity = arrayOfChromaticity[i];
/*      */             
/* 2381 */             if (localChromaticity == Chromaticity.MONOCHROME) {
/* 2382 */               bool1 = true;
/* 2383 */             } else if (localChromaticity == Chromaticity.COLOR) {
/* 2384 */               bool2 = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2391 */       this.rbMonochrome.setEnabled(bool1);
/* 2392 */       this.rbColor.setEnabled(bool2);
/*      */       
/* 2394 */       Object localObject = (Chromaticity)ServiceDialog.this.asCurrent.get(localClass);
/* 2395 */       if (localObject == null) {
/* 2396 */         localObject = (Chromaticity)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass);
/* 2397 */         if (localObject == null) {
/* 2398 */           localObject = Chromaticity.MONOCHROME;
/*      */         }
/*      */       }
/*      */       
/* 2402 */       if (localObject == Chromaticity.MONOCHROME) {
/* 2403 */         this.rbMonochrome.setSelected(true);
/*      */       } else {
/* 2405 */         this.rbColor.setSelected(true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private class QualityPanel
/*      */     extends JPanel implements ActionListener
/*      */   {
/* 2413 */     private final String strTitle = ServiceDialog.getMsg("border.quality");
/*      */     private JRadioButton rbDraft;
/*      */     private JRadioButton rbNormal;
/*      */     private JRadioButton rbHigh;
/*      */     
/*      */     public QualityPanel() {
/* 2419 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2420 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/* 2422 */       setLayout(localGridBagLayout);
/* 2423 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/* 2425 */       localGridBagConstraints.fill = 1;
/* 2426 */       localGridBagConstraints.gridwidth = 0;
/* 2427 */       localGridBagConstraints.weighty = 1.0D;
/*      */       
/* 2429 */       ButtonGroup localButtonGroup = new ButtonGroup();
/* 2430 */       this.rbDraft = ServiceDialog.createRadioButton("radiobutton.draftq", this);
/* 2431 */       localButtonGroup.add(this.rbDraft);
/* 2432 */       ServiceDialog.addToGB(this.rbDraft, this, localGridBagLayout, localGridBagConstraints);
/* 2433 */       this.rbNormal = ServiceDialog.createRadioButton("radiobutton.normalq", this);
/* 2434 */       this.rbNormal.setSelected(true);
/* 2435 */       localButtonGroup.add(this.rbNormal);
/* 2436 */       ServiceDialog.addToGB(this.rbNormal, this, localGridBagLayout, localGridBagConstraints);
/* 2437 */       this.rbHigh = ServiceDialog.createRadioButton("radiobutton.highq", this);
/* 2438 */       localButtonGroup.add(this.rbHigh);
/* 2439 */       ServiceDialog.addToGB(this.rbHigh, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2443 */       Object localObject = paramActionEvent.getSource();
/*      */       
/* 2445 */       if (localObject == this.rbDraft) {
/* 2446 */         ServiceDialog.this.asCurrent.add(PrintQuality.DRAFT);
/* 2447 */       } else if (localObject == this.rbNormal) {
/* 2448 */         ServiceDialog.this.asCurrent.add(PrintQuality.NORMAL);
/* 2449 */       } else if (localObject == this.rbHigh) {
/* 2450 */         ServiceDialog.this.asCurrent.add(PrintQuality.HIGH);
/*      */       }
/*      */     }
/*      */     
/*      */     public void updateInfo() {
/* 2455 */       Class localClass = PrintQuality.class;
/* 2456 */       boolean bool1 = false;
/* 2457 */       boolean bool2 = false;
/* 2458 */       boolean bool3 = false;
/*      */       
/* 2460 */       if (ServiceDialog.this.isAWT) {
/* 2461 */         bool1 = true;
/* 2462 */         bool2 = true;
/* 2463 */         bool3 = true;
/*      */       }
/* 2465 */       else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass))
/*      */       {
/* 2467 */         localObject = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localClass, 
/* 2468 */           ServiceDialog.this.docFlavor, 
/* 2469 */           ServiceDialog.this.asCurrent);
/*      */         
/* 2471 */         if ((localObject instanceof PrintQuality[])) {
/* 2472 */           PrintQuality[] arrayOfPrintQuality = (PrintQuality[])localObject;
/*      */           
/* 2474 */           for (int i = 0; i < arrayOfPrintQuality.length; i++) {
/* 2475 */             PrintQuality localPrintQuality = arrayOfPrintQuality[i];
/*      */             
/* 2477 */             if (localPrintQuality == PrintQuality.DRAFT) {
/* 2478 */               bool1 = true;
/* 2479 */             } else if (localPrintQuality == PrintQuality.NORMAL) {
/* 2480 */               bool2 = true;
/* 2481 */             } else if (localPrintQuality == PrintQuality.HIGH) {
/* 2482 */               bool3 = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2488 */       this.rbDraft.setEnabled(bool1);
/* 2489 */       this.rbNormal.setEnabled(bool2);
/* 2490 */       this.rbHigh.setEnabled(bool3);
/*      */       
/* 2492 */       Object localObject = (PrintQuality)ServiceDialog.this.asCurrent.get(localClass);
/* 2493 */       if (localObject == null) {
/* 2494 */         localObject = (PrintQuality)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass);
/* 2495 */         if (localObject == null) {
/* 2496 */           localObject = PrintQuality.NORMAL;
/*      */         }
/*      */       }
/*      */       
/* 2500 */       if (localObject == PrintQuality.DRAFT) {
/* 2501 */         this.rbDraft.setSelected(true);
/* 2502 */       } else if (localObject == PrintQuality.NORMAL) {
/* 2503 */         this.rbNormal.setSelected(true);
/*      */       } else {
/* 2505 */         this.rbHigh.setSelected(true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private class SidesPanel
/*      */     extends JPanel
/*      */     implements ActionListener
/*      */   {
/* 2514 */     private final String strTitle = ServiceDialog.getMsg("border.sides");
/*      */     private IconRadioButton rbOneSide;
/*      */     private IconRadioButton rbTumble;
/*      */     private IconRadioButton rbDuplex;
/*      */     
/*      */     public SidesPanel() {
/* 2520 */       GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2521 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/* 2523 */       setLayout(localGridBagLayout);
/* 2524 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/* 2526 */       localGridBagConstraints.fill = 1;
/* 2527 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/* 2528 */       localGridBagConstraints.weighty = 1.0D;
/* 2529 */       localGridBagConstraints.gridwidth = 0;
/*      */       
/* 2531 */       ButtonGroup localButtonGroup = new ButtonGroup();
/* 2532 */       this.rbOneSide = new IconRadioButton(ServiceDialog.this, "radiobutton.oneside", "oneside.png", true, localButtonGroup, this);
/*      */       
/*      */ 
/* 2535 */       this.rbOneSide.addActionListener(this);
/* 2536 */       ServiceDialog.addToGB(this.rbOneSide, this, localGridBagLayout, localGridBagConstraints);
/* 2537 */       this.rbTumble = new IconRadioButton(ServiceDialog.this, "radiobutton.tumble", "tumble.png", false, localButtonGroup, this);
/*      */       
/*      */ 
/* 2540 */       this.rbTumble.addActionListener(this);
/* 2541 */       ServiceDialog.addToGB(this.rbTumble, this, localGridBagLayout, localGridBagConstraints);
/* 2542 */       this.rbDuplex = new IconRadioButton(ServiceDialog.this, "radiobutton.duplex", "duplex.png", false, localButtonGroup, this);
/*      */       
/*      */ 
/* 2545 */       this.rbDuplex.addActionListener(this);
/* 2546 */       localGridBagConstraints.gridwidth = 0;
/* 2547 */       ServiceDialog.addToGB(this.rbDuplex, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2551 */       Object localObject = paramActionEvent.getSource();
/*      */       
/* 2553 */       if (this.rbOneSide.isSameAs(localObject)) {
/* 2554 */         ServiceDialog.this.asCurrent.add(Sides.ONE_SIDED);
/* 2555 */       } else if (this.rbTumble.isSameAs(localObject)) {
/* 2556 */         ServiceDialog.this.asCurrent.add(Sides.TUMBLE);
/* 2557 */       } else if (this.rbDuplex.isSameAs(localObject)) {
/* 2558 */         ServiceDialog.this.asCurrent.add(Sides.DUPLEX);
/*      */       }
/*      */     }
/*      */     
/*      */     public void updateInfo() {
/* 2563 */       Class localClass = Sides.class;
/* 2564 */       boolean bool1 = false;
/* 2565 */       boolean bool2 = false;
/* 2566 */       boolean bool3 = false;
/*      */       
/* 2568 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass))
/*      */       {
/* 2570 */         localObject = ServiceDialog.this.psCurrent.getSupportedAttributeValues(localClass, 
/* 2571 */           ServiceDialog.this.docFlavor, 
/* 2572 */           ServiceDialog.this.asCurrent);
/*      */         
/* 2574 */         if ((localObject instanceof Sides[])) {
/* 2575 */           Sides[] arrayOfSides = (Sides[])localObject;
/*      */           
/* 2577 */           for (int i = 0; i < arrayOfSides.length; i++) {
/* 2578 */             Sides localSides = arrayOfSides[i];
/*      */             
/* 2580 */             if (localSides == Sides.ONE_SIDED) {
/* 2581 */               bool1 = true;
/* 2582 */             } else if (localSides == Sides.TUMBLE) {
/* 2583 */               bool2 = true;
/* 2584 */             } else if (localSides == Sides.DUPLEX) {
/* 2585 */               bool3 = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2590 */       this.rbOneSide.setEnabled(bool1);
/* 2591 */       this.rbTumble.setEnabled(bool2);
/* 2592 */       this.rbDuplex.setEnabled(bool3);
/*      */       
/* 2594 */       Object localObject = (Sides)ServiceDialog.this.asCurrent.get(localClass);
/* 2595 */       if (localObject == null) {
/* 2596 */         localObject = (Sides)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass);
/* 2597 */         if (localObject == null) {
/* 2598 */           localObject = Sides.ONE_SIDED;
/*      */         }
/*      */       }
/*      */       
/* 2602 */       if (localObject == Sides.ONE_SIDED) {
/* 2603 */         this.rbOneSide.setSelected(true);
/* 2604 */       } else if (localObject == Sides.TUMBLE) {
/* 2605 */         this.rbTumble.setSelected(true);
/*      */       } else {
/* 2607 */         this.rbDuplex.setSelected(true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class JobAttributesPanel
/*      */     extends JPanel
/*      */     implements ActionListener, ChangeListener, FocusListener
/*      */   {
/* 2617 */     private final String strTitle = ServiceDialog.getMsg("border.jobattributes");
/*      */     private JLabel lblPriority;
/*      */     private JLabel lblJobName;
/*      */     private JLabel lblUserName;
/*      */     private JSpinner spinPriority;
/*      */     private SpinnerNumberModel snModel;
/*      */     private JCheckBox cbJobSheets;
/*      */     private JTextField tfJobName;
/*      */     private JTextField tfUserName;
/*      */     
/* 2627 */     public JobAttributesPanel() { GridBagLayout localGridBagLayout = new GridBagLayout();
/* 2628 */       GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/*      */       
/* 2630 */       setLayout(localGridBagLayout);
/* 2631 */       setBorder(BorderFactory.createTitledBorder(this.strTitle));
/*      */       
/* 2633 */       localGridBagConstraints.fill = 0;
/* 2634 */       localGridBagConstraints.insets = ServiceDialog.compInsets;
/* 2635 */       localGridBagConstraints.weighty = 1.0D;
/*      */       
/* 2637 */       this.cbJobSheets = ServiceDialog.createCheckBox("checkbox.jobsheets", this);
/* 2638 */       localGridBagConstraints.anchor = 21;
/* 2639 */       ServiceDialog.addToGB(this.cbJobSheets, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 2641 */       JPanel localJPanel = new JPanel();
/* 2642 */       this.lblPriority = new JLabel(ServiceDialog.getMsg("label.priority"), 11);
/* 2643 */       this.lblPriority.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.priority"));
/*      */       
/* 2645 */       localJPanel.add(this.lblPriority);
/* 2646 */       this.snModel = new SpinnerNumberModel(1, 1, 100, 1);
/* 2647 */       this.spinPriority = new JSpinner(this.snModel);
/* 2648 */       this.lblPriority.setLabelFor(this.spinPriority);
/*      */       
/* 2650 */       ((NumberEditor)this.spinPriority.getEditor()).getTextField().setColumns(3);
/* 2651 */       this.spinPriority.addChangeListener(this);
/* 2652 */       localJPanel.add(this.spinPriority);
/* 2653 */       localGridBagConstraints.anchor = 22;
/* 2654 */       localGridBagConstraints.gridwidth = 0;
/* 2655 */       localJPanel.getAccessibleContext().setAccessibleName(
/* 2656 */         ServiceDialog.getMsg("label.priority"));
/* 2657 */       ServiceDialog.addToGB(localJPanel, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 2659 */       localGridBagConstraints.fill = 2;
/* 2660 */       localGridBagConstraints.anchor = 10;
/* 2661 */       localGridBagConstraints.weightx = 0.0D;
/* 2662 */       localGridBagConstraints.gridwidth = 1;
/* 2663 */       char c1 = ServiceDialog.getMnemonic("label.jobname");
/* 2664 */       this.lblJobName = new JLabel(ServiceDialog.getMsg("label.jobname"), 11);
/* 2665 */       this.lblJobName.setDisplayedMnemonic(c1);
/* 2666 */       ServiceDialog.addToGB(this.lblJobName, this, localGridBagLayout, localGridBagConstraints);
/* 2667 */       localGridBagConstraints.weightx = 1.0D;
/* 2668 */       localGridBagConstraints.gridwidth = 0;
/* 2669 */       this.tfJobName = new JTextField();
/* 2670 */       this.lblJobName.setLabelFor(this.tfJobName);
/* 2671 */       this.tfJobName.addFocusListener(this);
/* 2672 */       this.tfJobName.setFocusAccelerator(c1);
/* 2673 */       this.tfJobName.getAccessibleContext().setAccessibleName(
/* 2674 */         ServiceDialog.getMsg("label.jobname"));
/* 2675 */       ServiceDialog.addToGB(this.tfJobName, this, localGridBagLayout, localGridBagConstraints);
/*      */       
/* 2677 */       localGridBagConstraints.weightx = 0.0D;
/* 2678 */       localGridBagConstraints.gridwidth = 1;
/* 2679 */       char c2 = ServiceDialog.getMnemonic("label.username");
/* 2680 */       this.lblUserName = new JLabel(ServiceDialog.getMsg("label.username"), 11);
/* 2681 */       this.lblUserName.setDisplayedMnemonic(c2);
/* 2682 */       ServiceDialog.addToGB(this.lblUserName, this, localGridBagLayout, localGridBagConstraints);
/* 2683 */       localGridBagConstraints.gridwidth = 0;
/* 2684 */       this.tfUserName = new JTextField();
/* 2685 */       this.lblUserName.setLabelFor(this.tfUserName);
/* 2686 */       this.tfUserName.addFocusListener(this);
/* 2687 */       this.tfUserName.setFocusAccelerator(c2);
/* 2688 */       this.tfUserName.getAccessibleContext().setAccessibleName(
/* 2689 */         ServiceDialog.getMsg("label.username"));
/* 2690 */       ServiceDialog.addToGB(this.tfUserName, this, localGridBagLayout, localGridBagConstraints);
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/* 2694 */       if (this.cbJobSheets.isSelected()) {
/* 2695 */         ServiceDialog.this.asCurrent.add(JobSheets.STANDARD);
/*      */       } else {
/* 2697 */         ServiceDialog.this.asCurrent.add(JobSheets.NONE);
/*      */       }
/*      */     }
/*      */     
/*      */     public void stateChanged(ChangeEvent paramChangeEvent) {
/* 2702 */       ServiceDialog.this.asCurrent.add(new JobPriority(this.snModel.getNumber().intValue()));
/*      */     }
/*      */     
/*      */     public void focusLost(FocusEvent paramFocusEvent) {
/* 2706 */       Object localObject = paramFocusEvent.getSource();
/*      */       
/* 2708 */       if (localObject == this.tfJobName) {
/* 2709 */         ServiceDialog.this.asCurrent.add(new JobName(this.tfJobName.getText(), 
/* 2710 */           Locale.getDefault()));
/* 2711 */       } else if (localObject == this.tfUserName) {
/* 2712 */         ServiceDialog.this.asCurrent.add(new RequestingUserName(this.tfUserName.getText(), 
/* 2713 */           Locale.getDefault()));
/*      */       }
/*      */     }
/*      */     
/*      */     public void focusGained(FocusEvent paramFocusEvent) {}
/*      */     
/*      */     public void updateInfo() {
/* 2720 */       Class localClass1 = JobSheets.class;
/* 2721 */       Class localClass2 = JobPriority.class;
/* 2722 */       Class localClass3 = JobName.class;
/* 2723 */       Class localClass4 = RequestingUserName.class;
/* 2724 */       boolean bool1 = false;
/* 2725 */       boolean bool2 = false;
/* 2726 */       boolean bool3 = false;
/* 2727 */       boolean bool4 = false;
/*      */       
/*      */ 
/* 2730 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass1)) {
/* 2731 */         bool1 = true;
/*      */       }
/* 2733 */       JobSheets localJobSheets = (JobSheets)ServiceDialog.this.asCurrent.get(localClass1);
/* 2734 */       if (localJobSheets == null) {
/* 2735 */         localJobSheets = (JobSheets)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass1);
/* 2736 */         if (localJobSheets == null) {
/* 2737 */           localJobSheets = JobSheets.NONE;
/*      */         }
/*      */       }
/* 2740 */       this.cbJobSheets.setSelected(localJobSheets != JobSheets.NONE);
/* 2741 */       this.cbJobSheets.setEnabled(bool1);
/*      */       
/*      */ 
/* 2744 */       if ((!ServiceDialog.this.isAWT) && (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass2))) {
/* 2745 */         bool2 = true;
/*      */       }
/* 2747 */       JobPriority localJobPriority = (JobPriority)ServiceDialog.this.asCurrent.get(localClass2);
/* 2748 */       if (localJobPriority == null) {
/* 2749 */         localJobPriority = (JobPriority)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass2);
/* 2750 */         if (localJobPriority == null) {
/* 2751 */           localJobPriority = new JobPriority(1);
/*      */         }
/*      */       }
/* 2754 */       int i = localJobPriority.getValue();
/* 2755 */       if ((i < 1) || (i > 100)) {
/* 2756 */         i = 1;
/*      */       }
/* 2758 */       this.snModel.setValue(new Integer(i));
/* 2759 */       this.lblPriority.setEnabled(bool2);
/* 2760 */       this.spinPriority.setEnabled(bool2);
/*      */       
/*      */ 
/* 2763 */       if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass3)) {
/* 2764 */         bool3 = true;
/*      */       }
/* 2766 */       JobName localJobName = (JobName)ServiceDialog.this.asCurrent.get(localClass3);
/* 2767 */       if (localJobName == null) {
/* 2768 */         localJobName = (JobName)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass3);
/* 2769 */         if (localJobName == null) {
/* 2770 */           localJobName = new JobName("", Locale.getDefault());
/*      */         }
/*      */       }
/* 2773 */       this.tfJobName.setText(localJobName.getValue());
/* 2774 */       this.tfJobName.setEnabled(bool3);
/* 2775 */       this.lblJobName.setEnabled(bool3);
/*      */       
/*      */ 
/* 2778 */       if ((!ServiceDialog.this.isAWT) && (ServiceDialog.this.psCurrent.isAttributeCategorySupported(localClass4))) {
/* 2779 */         bool4 = true;
/*      */       }
/* 2781 */       RequestingUserName localRequestingUserName = (RequestingUserName)ServiceDialog.this.asCurrent.get(localClass4);
/* 2782 */       if (localRequestingUserName == null) {
/* 2783 */         localRequestingUserName = (RequestingUserName)ServiceDialog.this.psCurrent.getDefaultAttributeValue(localClass4);
/* 2784 */         if (localRequestingUserName == null) {
/* 2785 */           localRequestingUserName = new RequestingUserName("", Locale.getDefault());
/*      */         }
/*      */       }
/* 2788 */       this.tfUserName.setText(localRequestingUserName.getValue());
/* 2789 */       this.tfUserName.setEnabled(bool4);
/* 2790 */       this.lblUserName.setEnabled(bool4);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private class IconRadioButton
/*      */     extends JPanel
/*      */   {
/*      */     private JRadioButton rb;
/*      */     
/*      */ 
/*      */     private JLabel lbl;
/*      */     
/*      */ 
/*      */ 
/*      */     public IconRadioButton(String paramString1, String paramString2, boolean paramBoolean, ButtonGroup paramButtonGroup, ActionListener paramActionListener)
/*      */     {
/* 2809 */       super();
/* 2810 */       final URL localURL = ServiceDialog.getImageResource(paramString2);
/* 2811 */       Icon localIcon = (Icon)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run() {
/* 2814 */           ImageIcon localImageIcon = new ImageIcon(localURL);
/* 2815 */           return localImageIcon;
/*      */         }
/* 2817 */       });
/* 2818 */       this.lbl = new JLabel(localIcon);
/* 2819 */       add(this.lbl);
/*      */       
/* 2821 */       this.rb = ServiceDialog.createRadioButton(paramString1, paramActionListener);
/* 2822 */       this.rb.setSelected(paramBoolean);
/* 2823 */       ServiceDialog.addToBG(this.rb, this, paramButtonGroup);
/*      */     }
/*      */     
/*      */     public void addActionListener(ActionListener paramActionListener) {
/* 2827 */       this.rb.addActionListener(paramActionListener);
/*      */     }
/*      */     
/*      */     public boolean isSameAs(Object paramObject) {
/* 2831 */       return this.rb == paramObject;
/*      */     }
/*      */     
/*      */     public void setEnabled(boolean paramBoolean) {
/* 2835 */       this.rb.setEnabled(paramBoolean);
/* 2836 */       this.lbl.setEnabled(paramBoolean);
/*      */     }
/*      */     
/*      */     public boolean isSelected() {
/* 2840 */       return this.rb.isSelected();
/*      */     }
/*      */     
/*      */     public void setSelected(boolean paramBoolean) {
/* 2844 */       this.rb.setSelected(paramBoolean);
/*      */     }
/*      */   }
/*      */   
/*      */   private class ValidatingFileChooser
/*      */     extends JFileChooser
/*      */   {
/*      */     private ValidatingFileChooser() {}
/*      */     
/*      */     public void approveSelection()
/*      */     {
/* 2855 */       File localFile1 = getSelectedFile();
/*      */       boolean bool;
/*      */       try
/*      */       {
/* 2859 */         bool = localFile1.exists();
/*      */       } catch (SecurityException localSecurityException1) {
/* 2861 */         bool = false;
/*      */       }
/*      */       
/* 2864 */       if (bool)
/*      */       {
/* 2866 */         int i = JOptionPane.showConfirmDialog(this, 
/* 2867 */           ServiceDialog.getMsg("dialog.overwrite"), 
/* 2868 */           ServiceDialog.getMsg("dialog.owtitle"), 0);
/*      */         
/* 2870 */         if (i != 0) {
/* 2871 */           return;
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 2876 */         if (localFile1.createNewFile()) {
/* 2877 */           localFile1.delete();
/*      */         }
/*      */       } catch (IOException localIOException) {
/* 2880 */         JOptionPane.showMessageDialog(this, 
/* 2881 */           ServiceDialog.getMsg("dialog.writeerror") + " " + localFile1, 
/* 2882 */           ServiceDialog.getMsg("dialog.owtitle"), 2);
/*      */         
/* 2884 */         return;
/*      */       }
/*      */       catch (SecurityException localSecurityException2) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2891 */       File localFile2 = localFile1.getParentFile();
/* 2892 */       if ((!localFile1.exists()) || (
/* 2893 */         (localFile1.isFile()) && (localFile1.canWrite()))) { if (localFile2 != null)
/*      */         {
/* 2895 */           if ((localFile2.exists()) && ((!localFile2.exists()) || (localFile2.canWrite()))) {} }
/* 2896 */       } else { JOptionPane.showMessageDialog(this, 
/* 2897 */           ServiceDialog.getMsg("dialog.writeerror") + " " + localFile1, 
/* 2898 */           ServiceDialog.getMsg("dialog.owtitle"), 2);
/*      */         
/* 2900 */         return;
/*      */       }
/*      */       
/* 2903 */       super.approveSelection();
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\print\ServiceDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
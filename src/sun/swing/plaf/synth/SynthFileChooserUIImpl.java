/*      */ package sun.swing.plaf.synth;
/*      */ 
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.FocusAdapter;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.util.Arrays;
/*      */ import java.util.Locale;
/*      */ import java.util.Vector;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.AbstractListModel;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.ActionMap;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.ComboBoxModel;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JToggleButton;
/*      */ import javax.swing.ListCellRenderer;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ import javax.swing.filechooser.FileFilter;
/*      */ import javax.swing.filechooser.FileSystemView;
/*      */ import javax.swing.plaf.ActionMapUIResource;
/*      */ import javax.swing.plaf.basic.BasicDirectoryModel;
/*      */ import javax.swing.plaf.synth.SynthContext;
/*      */ import sun.awt.shell.ShellFolder;
/*      */ import sun.swing.FilePane;
/*      */ import sun.swing.FilePane.FileChooserUIAccessor;
/*      */ import sun.swing.SwingUtilities2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SynthFileChooserUIImpl
/*      */   extends SynthFileChooserUI
/*      */ {
/*      */   private JLabel lookInLabel;
/*      */   private JComboBox<File> directoryComboBox;
/*      */   private DirectoryComboBoxModel directoryComboBoxModel;
/*   65 */   private Action directoryComboBoxAction = new DirectoryComboBoxAction();
/*      */   
/*      */   private FilterComboBoxModel filterComboBoxModel;
/*      */   
/*      */   private JTextField fileNameTextField;
/*      */   
/*      */   private FilePane filePane;
/*      */   
/*      */   private JToggleButton listViewButton;
/*      */   
/*      */   private JToggleButton detailsViewButton;
/*      */   
/*      */   private boolean readOnly;
/*      */   
/*      */   private JPanel buttonPanel;
/*      */   private JPanel bottomPanel;
/*      */   private JComboBox<FileFilter> filterComboBox;
/*   82 */   private static final Dimension hstrut5 = new Dimension(5, 1);
/*      */   
/*   84 */   private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
/*      */   
/*      */ 
/*   87 */   private static Dimension LIST_PREF_SIZE = new Dimension(405, 135);
/*      */   
/*      */ 
/*   90 */   private int lookInLabelMnemonic = 0;
/*   91 */   private String lookInLabelText = null;
/*   92 */   private String saveInLabelText = null;
/*      */   
/*   94 */   private int fileNameLabelMnemonic = 0;
/*   95 */   private String fileNameLabelText = null;
/*   96 */   private int folderNameLabelMnemonic = 0;
/*   97 */   private String folderNameLabelText = null;
/*      */   
/*   99 */   private int filesOfTypeLabelMnemonic = 0;
/*  100 */   private String filesOfTypeLabelText = null;
/*      */   
/*  102 */   private String upFolderToolTipText = null;
/*  103 */   private String upFolderAccessibleName = null;
/*      */   
/*  105 */   private String homeFolderToolTipText = null;
/*  106 */   private String homeFolderAccessibleName = null;
/*      */   
/*  108 */   private String newFolderToolTipText = null;
/*  109 */   private String newFolderAccessibleName = null;
/*      */   
/*  111 */   private String listViewButtonToolTipText = null;
/*  112 */   private String listViewButtonAccessibleName = null;
/*      */   
/*  114 */   private String detailsViewButtonToolTipText = null;
/*  115 */   private String detailsViewButtonAccessibleName = null;
/*      */   
/*      */   private AlignedLabel fileNameLabel;
/*  118 */   private final PropertyChangeListener modeListener = new PropertyChangeListener() {
/*      */     public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
/*  120 */       if (SynthFileChooserUIImpl.this.fileNameLabel != null)
/*  121 */         SynthFileChooserUIImpl.this.populateFileNameLabel();
/*      */     }
/*      */   };
/*      */   static final int space = 10;
/*      */   
/*      */   private void populateFileNameLabel() {
/*  127 */     if (getFileChooser().getFileSelectionMode() == 1) {
/*  128 */       this.fileNameLabel.setText(this.folderNameLabelText);
/*  129 */       this.fileNameLabel.setDisplayedMnemonic(this.folderNameLabelMnemonic);
/*      */     } else {
/*  131 */       this.fileNameLabel.setText(this.fileNameLabelText);
/*  132 */       this.fileNameLabel.setDisplayedMnemonic(this.fileNameLabelMnemonic);
/*      */     }
/*      */   }
/*      */   
/*      */   public SynthFileChooserUIImpl(JFileChooser paramJFileChooser) {
/*  137 */     super(paramJFileChooser);
/*      */   }
/*      */   
/*      */   private class SynthFileChooserUIAccessor implements FilePane.FileChooserUIAccessor {
/*      */     private SynthFileChooserUIAccessor() {}
/*      */     
/*  143 */     public JFileChooser getFileChooser() { return SynthFileChooserUIImpl.this.getFileChooser(); }
/*      */     
/*      */     public BasicDirectoryModel getModel()
/*      */     {
/*  147 */       return SynthFileChooserUIImpl.this.getModel();
/*      */     }
/*      */     
/*      */     public JPanel createList() {
/*  151 */       return null;
/*      */     }
/*      */     
/*      */     public JPanel createDetailsView() {
/*  155 */       return null;
/*      */     }
/*      */     
/*      */     public boolean isDirectorySelected() {
/*  159 */       return SynthFileChooserUIImpl.this.isDirectorySelected();
/*      */     }
/*      */     
/*      */     public File getDirectory() {
/*  163 */       return SynthFileChooserUIImpl.this.getDirectory();
/*      */     }
/*      */     
/*      */     public Action getChangeToParentDirectoryAction() {
/*  167 */       return SynthFileChooserUIImpl.this.getChangeToParentDirectoryAction();
/*      */     }
/*      */     
/*      */     public Action getApproveSelectionAction() {
/*  171 */       return SynthFileChooserUIImpl.this.getApproveSelectionAction();
/*      */     }
/*      */     
/*      */     public Action getNewFolderAction() {
/*  175 */       return SynthFileChooserUIImpl.this.getNewFolderAction();
/*      */     }
/*      */     
/*      */     public MouseListener createDoubleClickListener(JList paramJList) {
/*  179 */       return SynthFileChooserUIImpl.this.createDoubleClickListener(getFileChooser(), paramJList);
/*      */     }
/*      */     
/*      */     public ListSelectionListener createListSelectionListener()
/*      */     {
/*  184 */       return SynthFileChooserUIImpl.this.createListSelectionListener(getFileChooser());
/*      */     }
/*      */   }
/*      */   
/*      */   protected void installDefaults(JFileChooser paramJFileChooser) {
/*  189 */     super.installDefaults(paramJFileChooser);
/*  190 */     this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
/*      */   }
/*      */   
/*      */   public void installComponents(JFileChooser paramJFileChooser) {
/*  194 */     super.installComponents(paramJFileChooser);
/*      */     
/*  196 */     SynthContext localSynthContext = getContext(paramJFileChooser, 1);
/*      */     
/*  198 */     paramJFileChooser.setLayout(new BorderLayout(0, 11));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  205 */     JPanel localJPanel1 = new JPanel(new BorderLayout(11, 0));
/*  206 */     JPanel localJPanel2 = new JPanel();
/*  207 */     localJPanel2.setLayout(new BoxLayout(localJPanel2, 2));
/*  208 */     localJPanel1.add(localJPanel2, "After");
/*      */     
/*      */ 
/*  211 */     paramJFileChooser.add(localJPanel1, "North");
/*      */     
/*      */ 
/*  214 */     this.lookInLabel = new JLabel(this.lookInLabelText);
/*  215 */     this.lookInLabel.setDisplayedMnemonic(this.lookInLabelMnemonic);
/*  216 */     localJPanel1.add(this.lookInLabel, "Before");
/*      */     
/*      */ 
/*  219 */     this.directoryComboBox = new JComboBox();
/*  220 */     this.directoryComboBox.getAccessibleContext().setAccessibleDescription(this.lookInLabelText);
/*  221 */     this.directoryComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
/*  222 */     this.lookInLabel.setLabelFor(this.directoryComboBox);
/*  223 */     this.directoryComboBoxModel = createDirectoryComboBoxModel(paramJFileChooser);
/*  224 */     this.directoryComboBox.setModel(this.directoryComboBoxModel);
/*  225 */     this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
/*  226 */     this.directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(paramJFileChooser));
/*  227 */     this.directoryComboBox.setAlignmentX(0.0F);
/*  228 */     this.directoryComboBox.setAlignmentY(0.0F);
/*  229 */     this.directoryComboBox.setMaximumRowCount(8);
/*  230 */     localJPanel1.add(this.directoryComboBox, "Center");
/*      */     
/*  232 */     this.filePane = new FilePane(new SynthFileChooserUIAccessor(null));
/*  233 */     paramJFileChooser.addPropertyChangeListener(this.filePane);
/*      */     
/*      */ 
/*  236 */     JPopupMenu localJPopupMenu = this.filePane.getComponentPopupMenu();
/*  237 */     if (localJPopupMenu != null) {
/*  238 */       localJPopupMenu.insert(getChangeToParentDirectoryAction(), 0);
/*  239 */       if (File.separatorChar == '/') {
/*  240 */         localJPopupMenu.insert(getGoHomeAction(), 1);
/*      */       }
/*      */     }
/*      */     
/*  244 */     FileSystemView localFileSystemView = paramJFileChooser.getFileSystemView();
/*      */     
/*      */ 
/*  247 */     JButton localJButton1 = new JButton(getChangeToParentDirectoryAction());
/*  248 */     localJButton1.setText(null);
/*  249 */     localJButton1.setIcon(this.upFolderIcon);
/*  250 */     localJButton1.setToolTipText(this.upFolderToolTipText);
/*  251 */     localJButton1.getAccessibleContext().setAccessibleName(this.upFolderAccessibleName);
/*  252 */     localJButton1.setAlignmentX(0.0F);
/*  253 */     localJButton1.setAlignmentY(0.5F);
/*  254 */     localJButton1.setMargin(shrinkwrap);
/*      */     
/*  256 */     localJPanel2.add(localJButton1);
/*  257 */     localJPanel2.add(Box.createRigidArea(hstrut5));
/*      */     
/*      */ 
/*  260 */     File localFile = localFileSystemView.getHomeDirectory();
/*  261 */     String str = this.homeFolderToolTipText;
/*      */     
/*  263 */     JButton localJButton2 = new JButton(this.homeFolderIcon);
/*  264 */     localJButton2.setToolTipText(str);
/*  265 */     localJButton2.getAccessibleContext().setAccessibleName(this.homeFolderAccessibleName);
/*  266 */     localJButton2.setAlignmentX(0.0F);
/*  267 */     localJButton2.setAlignmentY(0.5F);
/*  268 */     localJButton2.setMargin(shrinkwrap);
/*      */     
/*  270 */     localJButton2.addActionListener(getGoHomeAction());
/*  271 */     localJPanel2.add(localJButton2);
/*  272 */     localJPanel2.add(Box.createRigidArea(hstrut5));
/*      */     
/*      */ 
/*  275 */     if (!this.readOnly) {
/*  276 */       localJButton2 = new JButton(this.filePane.getNewFolderAction());
/*  277 */       localJButton2.setText(null);
/*  278 */       localJButton2.setIcon(this.newFolderIcon);
/*  279 */       localJButton2.setToolTipText(this.newFolderToolTipText);
/*  280 */       localJButton2.getAccessibleContext().setAccessibleName(this.newFolderAccessibleName);
/*  281 */       localJButton2.setAlignmentX(0.0F);
/*  282 */       localJButton2.setAlignmentY(0.5F);
/*  283 */       localJButton2.setMargin(shrinkwrap);
/*  284 */       localJPanel2.add(localJButton2);
/*  285 */       localJPanel2.add(Box.createRigidArea(hstrut5));
/*      */     }
/*      */     
/*      */ 
/*  289 */     ButtonGroup localButtonGroup = new ButtonGroup();
/*      */     
/*      */ 
/*  292 */     this.listViewButton = new JToggleButton(this.listViewIcon);
/*  293 */     this.listViewButton.setToolTipText(this.listViewButtonToolTipText);
/*  294 */     this.listViewButton.getAccessibleContext().setAccessibleName(this.listViewButtonAccessibleName);
/*  295 */     this.listViewButton.setSelected(true);
/*  296 */     this.listViewButton.setAlignmentX(0.0F);
/*  297 */     this.listViewButton.setAlignmentY(0.5F);
/*  298 */     this.listViewButton.setMargin(shrinkwrap);
/*  299 */     this.listViewButton.addActionListener(this.filePane.getViewTypeAction(0));
/*  300 */     localJPanel2.add(this.listViewButton);
/*  301 */     localButtonGroup.add(this.listViewButton);
/*      */     
/*      */ 
/*  304 */     this.detailsViewButton = new JToggleButton(this.detailsViewIcon);
/*  305 */     this.detailsViewButton.setToolTipText(this.detailsViewButtonToolTipText);
/*  306 */     this.detailsViewButton.getAccessibleContext().setAccessibleName(this.detailsViewButtonAccessibleName);
/*  307 */     this.detailsViewButton.setAlignmentX(0.0F);
/*  308 */     this.detailsViewButton.setAlignmentY(0.5F);
/*  309 */     this.detailsViewButton.setMargin(shrinkwrap);
/*  310 */     this.detailsViewButton.addActionListener(this.filePane.getViewTypeAction(1));
/*  311 */     localJPanel2.add(this.detailsViewButton);
/*  312 */     localButtonGroup.add(this.detailsViewButton);
/*      */     
/*  314 */     this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
/*  316 */         if ("viewType".equals(paramAnonymousPropertyChangeEvent.getPropertyName())) {
/*  317 */           int i = SynthFileChooserUIImpl.this.filePane.getViewType();
/*  318 */           switch (i) {
/*      */           case 0: 
/*  320 */             SynthFileChooserUIImpl.this.listViewButton.setSelected(true);
/*  321 */             break;
/*      */           case 1: 
/*  323 */             SynthFileChooserUIImpl.this.detailsViewButton.setSelected(true);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  332 */     });
/*  333 */     paramJFileChooser.add(getAccessoryPanel(), "After");
/*  334 */     JComponent localJComponent = paramJFileChooser.getAccessory();
/*  335 */     if (localJComponent != null) {
/*  336 */       getAccessoryPanel().add(localJComponent);
/*      */     }
/*  338 */     this.filePane.setPreferredSize(LIST_PREF_SIZE);
/*  339 */     paramJFileChooser.add(this.filePane, "Center");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  345 */     this.bottomPanel = new JPanel();
/*  346 */     this.bottomPanel.setLayout(new BoxLayout(this.bottomPanel, 1));
/*  347 */     paramJFileChooser.add(this.bottomPanel, "South");
/*      */     
/*      */ 
/*  350 */     JPanel localJPanel3 = new JPanel();
/*  351 */     localJPanel3.setLayout(new BoxLayout(localJPanel3, 2));
/*  352 */     this.bottomPanel.add(localJPanel3);
/*  353 */     this.bottomPanel.add(Box.createRigidArea(new Dimension(1, 5)));
/*      */     
/*  355 */     this.fileNameLabel = new AlignedLabel();
/*  356 */     populateFileNameLabel();
/*  357 */     localJPanel3.add(this.fileNameLabel);
/*      */     
/*  359 */     this.fileNameTextField = new JTextField(35) {
/*      */       public Dimension getMaximumSize() {
/*  361 */         return new Dimension(32767, super.getPreferredSize().height);
/*      */       }
/*  363 */     };
/*  364 */     localJPanel3.add(this.fileNameTextField);
/*  365 */     this.fileNameLabel.setLabelFor(this.fileNameTextField);
/*  366 */     this.fileNameTextField.addFocusListener(new FocusAdapter()
/*      */     {
/*      */       public void focusGained(FocusEvent paramAnonymousFocusEvent) {
/*  369 */         if (!SynthFileChooserUIImpl.this.getFileChooser().isMultiSelectionEnabled()) {
/*  370 */           SynthFileChooserUIImpl.this.filePane.clearSelection();
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  375 */     if (paramJFileChooser.isMultiSelectionEnabled()) {
/*  376 */       setFileName(fileNameString(paramJFileChooser.getSelectedFiles()));
/*      */     } else {
/*  378 */       setFileName(fileNameString(paramJFileChooser.getSelectedFile()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  383 */     JPanel localJPanel4 = new JPanel();
/*  384 */     localJPanel4.setLayout(new BoxLayout(localJPanel4, 2));
/*  385 */     this.bottomPanel.add(localJPanel4);
/*      */     
/*  387 */     AlignedLabel localAlignedLabel = new AlignedLabel(this.filesOfTypeLabelText);
/*  388 */     localAlignedLabel.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
/*  389 */     localJPanel4.add(localAlignedLabel);
/*      */     
/*  391 */     this.filterComboBoxModel = createFilterComboBoxModel();
/*  392 */     paramJFileChooser.addPropertyChangeListener(this.filterComboBoxModel);
/*  393 */     this.filterComboBox = new JComboBox(this.filterComboBoxModel);
/*  394 */     this.filterComboBox.getAccessibleContext().setAccessibleDescription(this.filesOfTypeLabelText);
/*  395 */     localAlignedLabel.setLabelFor(this.filterComboBox);
/*  396 */     this.filterComboBox.setRenderer(createFilterComboBoxRenderer());
/*  397 */     localJPanel4.add(this.filterComboBox);
/*      */     
/*      */ 
/*      */ 
/*  401 */     this.buttonPanel = new JPanel();
/*  402 */     this.buttonPanel.setLayout(new ButtonAreaLayout(null));
/*      */     
/*  404 */     this.buttonPanel.add(getApproveButton(paramJFileChooser));
/*  405 */     this.buttonPanel.add(getCancelButton(paramJFileChooser));
/*      */     
/*  407 */     if (paramJFileChooser.getControlButtonsAreShown()) {
/*  408 */       addControlButtons();
/*      */     }
/*      */     
/*  411 */     groupLabels(new AlignedLabel[] { this.fileNameLabel, localAlignedLabel });
/*      */   }
/*      */   
/*      */   protected void installListeners(JFileChooser paramJFileChooser) {
/*  415 */     super.installListeners(paramJFileChooser);
/*  416 */     paramJFileChooser.addPropertyChangeListener("fileSelectionChanged", this.modeListener);
/*      */   }
/*      */   
/*      */   protected void uninstallListeners(JFileChooser paramJFileChooser) {
/*  420 */     paramJFileChooser.removePropertyChangeListener("fileSelectionChanged", this.modeListener);
/*  421 */     super.uninstallListeners(paramJFileChooser);
/*      */   }
/*      */   
/*      */   private String fileNameString(File paramFile) {
/*  425 */     if (paramFile == null) {
/*  426 */       return null;
/*      */     }
/*  428 */     JFileChooser localJFileChooser = getFileChooser();
/*  429 */     if ((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled())) {
/*  430 */       return paramFile.getPath();
/*      */     }
/*  432 */     return paramFile.getName();
/*      */   }
/*      */   
/*      */ 
/*      */   private String fileNameString(File[] paramArrayOfFile)
/*      */   {
/*  438 */     StringBuffer localStringBuffer = new StringBuffer();
/*  439 */     for (int i = 0; (paramArrayOfFile != null) && (i < paramArrayOfFile.length); i++) {
/*  440 */       if (i > 0) {
/*  441 */         localStringBuffer.append(" ");
/*      */       }
/*  443 */       if (paramArrayOfFile.length > 1) {
/*  444 */         localStringBuffer.append("\"");
/*      */       }
/*  446 */       localStringBuffer.append(fileNameString(paramArrayOfFile[i]));
/*  447 */       if (paramArrayOfFile.length > 1) {
/*  448 */         localStringBuffer.append("\"");
/*      */       }
/*      */     }
/*  451 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */   public void uninstallUI(JComponent paramJComponent)
/*      */   {
/*  456 */     paramJComponent.removePropertyChangeListener(this.filterComboBoxModel);
/*  457 */     paramJComponent.removePropertyChangeListener(this.filePane);
/*      */     
/*  459 */     if (this.filePane != null) {
/*  460 */       this.filePane.uninstallUI();
/*  461 */       this.filePane = null;
/*      */     }
/*      */     
/*  464 */     super.uninstallUI(paramJComponent);
/*      */   }
/*      */   
/*      */   protected void installStrings(JFileChooser paramJFileChooser) {
/*  468 */     super.installStrings(paramJFileChooser);
/*      */     
/*  470 */     Locale localLocale = paramJFileChooser.getLocale();
/*      */     
/*  472 */     this.lookInLabelMnemonic = getMnemonic("FileChooser.lookInLabelMnemonic", localLocale);
/*  473 */     this.lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", localLocale);
/*  474 */     this.saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", localLocale);
/*      */     
/*  476 */     this.fileNameLabelMnemonic = getMnemonic("FileChooser.fileNameLabelMnemonic", localLocale);
/*  477 */     this.fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", localLocale);
/*  478 */     this.folderNameLabelMnemonic = getMnemonic("FileChooser.folderNameLabelMnemonic", localLocale);
/*  479 */     this.folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", localLocale);
/*      */     
/*  481 */     this.filesOfTypeLabelMnemonic = getMnemonic("FileChooser.filesOfTypeLabelMnemonic", localLocale);
/*  482 */     this.filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", localLocale);
/*      */     
/*  484 */     this.upFolderToolTipText = UIManager.getString("FileChooser.upFolderToolTipText", localLocale);
/*  485 */     this.upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName", localLocale);
/*      */     
/*  487 */     this.homeFolderToolTipText = UIManager.getString("FileChooser.homeFolderToolTipText", localLocale);
/*  488 */     this.homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName", localLocale);
/*      */     
/*  490 */     this.newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", localLocale);
/*  491 */     this.newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", localLocale);
/*      */     
/*  493 */     this.listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText", localLocale);
/*  494 */     this.listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName", localLocale);
/*      */     
/*  496 */     this.detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText", localLocale);
/*  497 */     this.detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName", localLocale);
/*      */   }
/*      */   
/*      */   private int getMnemonic(String paramString, Locale paramLocale) {
/*  501 */     return SwingUtilities2.getUIDefaultsInt(paramString, paramLocale);
/*      */   }
/*      */   
/*      */   public String getFileName()
/*      */   {
/*  506 */     if (this.fileNameTextField != null) {
/*  507 */       return this.fileNameTextField.getText();
/*      */     }
/*  509 */     return null;
/*      */   }
/*      */   
/*      */   public void setFileName(String paramString)
/*      */   {
/*  514 */     if (this.fileNameTextField != null) {
/*  515 */       this.fileNameTextField.setText(paramString);
/*      */     }
/*      */   }
/*      */   
/*      */   public void rescanCurrentDirectory(JFileChooser paramJFileChooser) {
/*  520 */     this.filePane.rescanCurrentDirectory();
/*      */   }
/*      */   
/*      */   protected void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  524 */     super.doSelectedFileChanged(paramPropertyChangeEvent);
/*      */     
/*  526 */     File localFile = (File)paramPropertyChangeEvent.getNewValue();
/*  527 */     JFileChooser localJFileChooser = getFileChooser();
/*  528 */     if ((localFile != null) && (
/*  529 */       ((localJFileChooser.isFileSelectionEnabled()) && (!localFile.isDirectory())) || (
/*  530 */       (localFile.isDirectory()) && (localJFileChooser.isDirectorySelectionEnabled()))))
/*      */     {
/*  532 */       setFileName(fileNameString(localFile));
/*      */     }
/*      */   }
/*      */   
/*      */   protected void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  537 */     super.doSelectedFilesChanged(paramPropertyChangeEvent);
/*      */     
/*  539 */     File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
/*  540 */     JFileChooser localJFileChooser = getFileChooser();
/*  541 */     if ((arrayOfFile != null) && (arrayOfFile.length > 0) && ((arrayOfFile.length > 1) || 
/*      */     
/*  543 */       (localJFileChooser.isDirectorySelectionEnabled()) || (!arrayOfFile[0].isDirectory()))) {
/*  544 */       setFileName(fileNameString(arrayOfFile));
/*      */     }
/*      */   }
/*      */   
/*      */   protected void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  549 */     super.doDirectoryChanged(paramPropertyChangeEvent);
/*      */     
/*  551 */     JFileChooser localJFileChooser = getFileChooser();
/*  552 */     FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
/*  553 */     File localFile = localJFileChooser.getCurrentDirectory();
/*      */     
/*  555 */     if ((!this.readOnly) && (localFile != null)) {
/*  556 */       getNewFolderAction().setEnabled(this.filePane.canWrite(localFile));
/*      */     }
/*      */     
/*  559 */     if (localFile != null) {
/*  560 */       JComponent localJComponent = getDirectoryComboBox();
/*  561 */       if ((localJComponent instanceof JComboBox)) {
/*  562 */         ComboBoxModel localComboBoxModel = ((JComboBox)localJComponent).getModel();
/*  563 */         if ((localComboBoxModel instanceof DirectoryComboBoxModel)) {
/*  564 */           ((DirectoryComboBoxModel)localComboBoxModel).addItem(localFile);
/*      */         }
/*      */       }
/*      */       
/*  568 */       if ((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled())) {
/*  569 */         if (localFileSystemView.isFileSystem(localFile)) {
/*  570 */           setFileName(localFile.getPath());
/*      */         } else {
/*  572 */           setFileName(null);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent)
/*      */   {
/*  580 */     super.doFileSelectionModeChanged(paramPropertyChangeEvent);
/*      */     
/*  582 */     JFileChooser localJFileChooser = getFileChooser();
/*  583 */     File localFile = localJFileChooser.getCurrentDirectory();
/*  584 */     if ((localFile != null) && 
/*  585 */       (localJFileChooser.isDirectorySelectionEnabled()) && 
/*  586 */       (!localJFileChooser.isFileSelectionEnabled()) && 
/*  587 */       (localJFileChooser.getFileSystemView().isFileSystem(localFile)))
/*      */     {
/*  589 */       setFileName(localFile.getPath());
/*      */     } else {
/*  591 */       setFileName(null);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  596 */     if (getAccessoryPanel() != null) {
/*  597 */       if (paramPropertyChangeEvent.getOldValue() != null) {
/*  598 */         getAccessoryPanel().remove((JComponent)paramPropertyChangeEvent.getOldValue());
/*      */       }
/*  600 */       JComponent localJComponent = (JComponent)paramPropertyChangeEvent.getNewValue();
/*  601 */       if (localJComponent != null) {
/*  602 */         getAccessoryPanel().add(localJComponent, "Center");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent) {
/*  608 */     super.doControlButtonsChanged(paramPropertyChangeEvent);
/*      */     
/*  610 */     if (getFileChooser().getControlButtonsAreShown()) {
/*  611 */       addControlButtons();
/*      */     } else {
/*  613 */       removeControlButtons();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void addControlButtons() {
/*  618 */     if (this.bottomPanel != null) {
/*  619 */       this.bottomPanel.add(this.buttonPanel);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void removeControlButtons() {
/*  624 */     if (this.bottomPanel != null) {
/*  625 */       this.bottomPanel.remove(this.buttonPanel);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ActionMap createActionMap()
/*      */   {
/*  637 */     ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
/*      */     
/*  639 */     FilePane.addActionsToMap(localActionMapUIResource, this.filePane.getActions());
/*      */     
/*  641 */     localActionMapUIResource.put("fileNameCompletion", getFileNameCompletionAction());
/*  642 */     return localActionMapUIResource;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JComponent getDirectoryComboBox()
/*      */   {
/*  650 */     return this.directoryComboBox;
/*      */   }
/*      */   
/*      */   protected Action getDirectoryComboBoxAction() {
/*  654 */     return this.directoryComboBoxAction;
/*      */   }
/*      */   
/*      */   protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser paramJFileChooser) {
/*  658 */     return new DirectoryComboBoxRenderer(this.directoryComboBox.getRenderer(), null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private class DirectoryComboBoxRenderer
/*      */     implements ListCellRenderer<File>
/*      */   {
/*      */     private ListCellRenderer<? super File> delegate;
/*      */     
/*      */ 
/*      */ 
/*  672 */     IndentIcon ii = new IndentIcon(SynthFileChooserUIImpl.this);
/*      */     
/*      */     private DirectoryComboBoxRenderer() { ListCellRenderer localListCellRenderer;
/*  675 */       this.delegate = localListCellRenderer;
/*      */     }
/*      */     
/*      */     public Component getListCellRendererComponent(JList<? extends File> paramJList, File paramFile, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/*  680 */       Component localComponent = this.delegate.getListCellRendererComponent(paramJList, paramFile, paramInt, paramBoolean1, paramBoolean2);
/*      */       
/*  682 */       assert ((localComponent instanceof JLabel));
/*  683 */       JLabel localJLabel = (JLabel)localComponent;
/*  684 */       if (paramFile == null) {
/*  685 */         localJLabel.setText("");
/*  686 */         return localJLabel;
/*      */       }
/*  688 */       localJLabel.setText(SynthFileChooserUIImpl.this.getFileChooser().getName(paramFile));
/*  689 */       Icon localIcon = SynthFileChooserUIImpl.this.getFileChooser().getIcon(paramFile);
/*  690 */       this.ii.icon = localIcon;
/*  691 */       this.ii.depth = SynthFileChooserUIImpl.this.directoryComboBoxModel.getDepth(paramInt);
/*  692 */       localJLabel.setIcon(this.ii);
/*      */       
/*  694 */       return localJLabel;
/*      */     }
/*      */   }
/*      */   
/*      */   class IndentIcon implements Icon {
/*      */     IndentIcon() {}
/*      */     
/*  701 */     Icon icon = null;
/*  702 */     int depth = 0;
/*      */     
/*      */     public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
/*  705 */       if (this.icon != null) {
/*  706 */         if (paramComponent.getComponentOrientation().isLeftToRight()) {
/*  707 */           this.icon.paintIcon(paramComponent, paramGraphics, paramInt1 + this.depth * 10, paramInt2);
/*      */         } else {
/*  709 */           this.icon.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public int getIconWidth() {
/*  715 */       return (this.icon != null ? this.icon.getIconWidth() : 0) + this.depth * 10;
/*      */     }
/*      */     
/*      */     public int getIconHeight() {
/*  719 */       return this.icon != null ? this.icon.getIconHeight() : 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser paramJFileChooser)
/*      */   {
/*  728 */     return new DirectoryComboBoxModel();
/*      */   }
/*      */   
/*      */   protected class DirectoryComboBoxModel
/*      */     extends AbstractListModel<File>
/*      */     implements ComboBoxModel<File>
/*      */   {
/*  735 */     Vector<File> directories = new Vector();
/*  736 */     int[] depths = null;
/*  737 */     File selectedDirectory = null;
/*  738 */     JFileChooser chooser = SynthFileChooserUIImpl.this.getFileChooser();
/*  739 */     FileSystemView fsv = this.chooser.getFileSystemView();
/*      */     
/*      */ 
/*      */     public DirectoryComboBoxModel()
/*      */     {
/*  744 */       File localFile = SynthFileChooserUIImpl.this.getFileChooser().getCurrentDirectory();
/*  745 */       if (localFile != null) {
/*  746 */         addItem(localFile);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addItem(File paramFile)
/*      */     {
/*  757 */       if (paramFile == null) {
/*  758 */         return;
/*      */       }
/*      */       
/*  761 */       boolean bool = FilePane.usesShellFolder(this.chooser);
/*      */       
/*  763 */       int i = this.directories.size();
/*  764 */       this.directories.clear();
/*  765 */       if (i > 0) {
/*  766 */         fireIntervalRemoved(this, 0, i);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  771 */       File[] arrayOfFile = bool ? (File[])ShellFolder.get("fileChooserComboBoxFolders") : this.fsv.getRoots();
/*  772 */       this.directories.addAll(Arrays.asList(arrayOfFile));
/*      */       
/*      */ 
/*      */       File localFile1;
/*      */       
/*      */       try
/*      */       {
/*  779 */         localFile1 = ShellFolder.getNormalizedFile(paramFile);
/*      */       }
/*      */       catch (IOException localIOException) {
/*  782 */         localFile1 = paramFile;
/*      */       }
/*      */       
/*      */       try
/*      */       {
/*  787 */         File localFile2 = bool ? ShellFolder.getShellFolder(localFile1) : localFile1;
/*      */         
/*  789 */         File localFile3 = localFile2;
/*  790 */         Vector localVector = new Vector(10);
/*      */         do {
/*  792 */           localVector.addElement(localFile3);
/*  793 */         } while ((localFile3 = localFile3.getParentFile()) != null);
/*      */         
/*  795 */         int j = localVector.size();
/*      */         
/*  797 */         for (int k = 0; k < j; k++) {
/*  798 */           localFile3 = (File)localVector.get(k);
/*  799 */           if (this.directories.contains(localFile3)) {
/*  800 */             int m = this.directories.indexOf(localFile3);
/*  801 */             for (int n = k - 1; n >= 0; n--) {
/*  802 */               this.directories.insertElementAt(localVector.get(n), m + k - n);
/*      */             }
/*  804 */             break;
/*      */           }
/*      */         }
/*  807 */         calculateDepths();
/*  808 */         setSelectedItem(localFile2);
/*      */       } catch (FileNotFoundException localFileNotFoundException) {
/*  810 */         calculateDepths();
/*      */       }
/*      */     }
/*      */     
/*      */     private void calculateDepths() {
/*  815 */       this.depths = new int[this.directories.size()];
/*  816 */       for (int i = 0; i < this.depths.length; i++) {
/*  817 */         File localFile1 = (File)this.directories.get(i);
/*  818 */         File localFile2 = localFile1.getParentFile();
/*  819 */         this.depths[i] = 0;
/*  820 */         if (localFile2 != null) {
/*  821 */           for (int j = i - 1; j >= 0; j--) {
/*  822 */             if (localFile2.equals(this.directories.get(j))) {
/*  823 */               this.depths[j] += 1;
/*  824 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public int getDepth(int paramInt) {
/*  832 */       return (this.depths != null) && (paramInt >= 0) && (paramInt < this.depths.length) ? this.depths[paramInt] : 0;
/*      */     }
/*      */     
/*      */     public void setSelectedItem(Object paramObject) {
/*  836 */       this.selectedDirectory = ((File)paramObject);
/*  837 */       fireContentsChanged(this, -1, -1);
/*      */     }
/*      */     
/*      */     public Object getSelectedItem() {
/*  841 */       return this.selectedDirectory;
/*      */     }
/*      */     
/*      */     public int getSize() {
/*  845 */       return this.directories.size();
/*      */     }
/*      */     
/*      */     public File getElementAt(int paramInt) {
/*  849 */       return (File)this.directories.elementAt(paramInt);
/*      */     }
/*      */   }
/*      */   
/*      */   protected class DirectoryComboBoxAction
/*      */     extends AbstractAction
/*      */   {
/*      */     protected DirectoryComboBoxAction()
/*      */     {
/*  858 */       super();
/*      */     }
/*      */     
/*      */     public void actionPerformed(ActionEvent paramActionEvent) {
/*  862 */       SynthFileChooserUIImpl.this.directoryComboBox.hidePopup();
/*  863 */       JComponent localJComponent = SynthFileChooserUIImpl.this.getDirectoryComboBox();
/*  864 */       if ((localJComponent instanceof JComboBox)) {
/*  865 */         File localFile = (File)((JComboBox)localJComponent).getSelectedItem();
/*  866 */         SynthFileChooserUIImpl.this.getFileChooser().setCurrentDirectory(localFile);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected FilterComboBoxRenderer createFilterComboBoxRenderer()
/*      */   {
/*  875 */     return new FilterComboBoxRenderer(this.filterComboBox.getRenderer(), null);
/*      */   }
/*      */   
/*      */   public class FilterComboBoxRenderer implements ListCellRenderer<FileFilter>
/*      */   {
/*      */     private ListCellRenderer<? super FileFilter> delegate;
/*      */     
/*      */     private FilterComboBoxRenderer() {
/*      */       ListCellRenderer localListCellRenderer;
/*  884 */       this.delegate = localListCellRenderer;
/*      */     }
/*      */     
/*      */     public Component getListCellRendererComponent(JList<? extends FileFilter> paramJList, FileFilter paramFileFilter, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/*  889 */       Component localComponent = this.delegate.getListCellRendererComponent(paramJList, paramFileFilter, paramInt, paramBoolean1, paramBoolean2);
/*      */       
/*  891 */       String str = null;
/*  892 */       if (paramFileFilter != null) {
/*  893 */         str = paramFileFilter.getDescription();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  898 */       assert ((localComponent instanceof JLabel));
/*  899 */       if (str != null) {
/*  900 */         ((JLabel)localComponent).setText(str);
/*      */       }
/*  902 */       return localComponent;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected FilterComboBoxModel createFilterComboBoxModel()
/*      */   {
/*  910 */     return new FilterComboBoxModel();
/*      */   }
/*      */   
/*      */   protected class FilterComboBoxModel
/*      */     extends AbstractListModel<FileFilter>
/*      */     implements ComboBoxModel<FileFilter>, PropertyChangeListener
/*      */   {
/*      */     protected FileFilter[] filters;
/*      */     
/*      */     protected FilterComboBoxModel()
/*      */     {
/*  921 */       this.filters = SynthFileChooserUIImpl.this.getFileChooser().getChoosableFileFilters();
/*      */     }
/*      */     
/*      */     public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/*  925 */       String str = paramPropertyChangeEvent.getPropertyName();
/*  926 */       if (str == "ChoosableFileFilterChangedProperty") {
/*  927 */         this.filters = ((FileFilter[])paramPropertyChangeEvent.getNewValue());
/*  928 */         fireContentsChanged(this, -1, -1);
/*  929 */       } else if (str == "fileFilterChanged") {
/*  930 */         fireContentsChanged(this, -1, -1);
/*      */       }
/*      */     }
/*      */     
/*      */     public void setSelectedItem(Object paramObject) {
/*  935 */       if (paramObject != null) {
/*  936 */         SynthFileChooserUIImpl.this.getFileChooser().setFileFilter((FileFilter)paramObject);
/*  937 */         fireContentsChanged(this, -1, -1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Object getSelectedItem()
/*      */     {
/*  947 */       FileFilter localFileFilter1 = SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
/*  948 */       int i = 0;
/*  949 */       if (localFileFilter1 != null) {
/*  950 */         for (FileFilter localFileFilter2 : this.filters) {
/*  951 */           if (localFileFilter2 == localFileFilter1) {
/*  952 */             i = 1;
/*      */           }
/*      */         }
/*  955 */         if (i == 0) {
/*  956 */           SynthFileChooserUIImpl.this.getFileChooser().addChoosableFileFilter(localFileFilter1);
/*      */         }
/*      */       }
/*  959 */       return SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
/*      */     }
/*      */     
/*      */     public int getSize() {
/*  963 */       if (this.filters != null) {
/*  964 */         return this.filters.length;
/*      */       }
/*  966 */       return 0;
/*      */     }
/*      */     
/*      */     public FileFilter getElementAt(int paramInt)
/*      */     {
/*  971 */       if (paramInt > getSize() - 1)
/*      */       {
/*  973 */         return SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
/*      */       }
/*  975 */       if (this.filters != null) {
/*  976 */         return this.filters[paramInt];
/*      */       }
/*  978 */       return null;
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
/*      */   private static class ButtonAreaLayout
/*      */     implements LayoutManager
/*      */   {
/*  992 */     private int hGap = 5;
/*  993 */     private int topMargin = 17;
/*      */     
/*      */     public void addLayoutComponent(String paramString, Component paramComponent) {}
/*      */     
/*      */     public void layoutContainer(Container paramContainer)
/*      */     {
/*  999 */       Component[] arrayOfComponent = paramContainer.getComponents();
/*      */       
/* 1001 */       if ((arrayOfComponent != null) && (arrayOfComponent.length > 0)) {
/* 1002 */         int i = arrayOfComponent.length;
/* 1003 */         Dimension[] arrayOfDimension = new Dimension[i];
/* 1004 */         Insets localInsets = paramContainer.getInsets();
/* 1005 */         int j = localInsets.top + this.topMargin;
/* 1006 */         int k = 0;
/*      */         
/* 1008 */         for (int m = 0; m < i; m++) {
/* 1009 */           arrayOfDimension[m] = arrayOfComponent[m].getPreferredSize();
/* 1010 */           k = Math.max(k, arrayOfDimension[m].width);
/*      */         }
/*      */         int n;
/* 1013 */         if (paramContainer.getComponentOrientation().isLeftToRight()) {
/* 1014 */           m = paramContainer.getSize().width - localInsets.left - k;
/* 1015 */           n = this.hGap + k;
/*      */         } else {
/* 1017 */           m = localInsets.left;
/* 1018 */           n = -(this.hGap + k);
/*      */         }
/* 1020 */         for (int i1 = i - 1; i1 >= 0; i1--) {
/* 1021 */           arrayOfComponent[i1].setBounds(m, j, k, arrayOfDimension[i1].height);
/*      */           
/* 1023 */           m -= n;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public Dimension minimumLayoutSize(Container paramContainer) {
/* 1029 */       if (paramContainer != null) {
/* 1030 */         Component[] arrayOfComponent = paramContainer.getComponents();
/*      */         
/* 1032 */         if ((arrayOfComponent != null) && (arrayOfComponent.length > 0)) {
/* 1033 */           int i = arrayOfComponent.length;
/* 1034 */           int j = 0;
/* 1035 */           Insets localInsets = paramContainer.getInsets();
/* 1036 */           int k = this.topMargin + localInsets.top + localInsets.bottom;
/* 1037 */           int m = localInsets.left + localInsets.right;
/* 1038 */           int n = 0;
/*      */           
/* 1040 */           for (int i1 = 0; i1 < i; i1++) {
/* 1041 */             Dimension localDimension = arrayOfComponent[i1].getPreferredSize();
/* 1042 */             j = Math.max(j, localDimension.height);
/* 1043 */             n = Math.max(n, localDimension.width);
/*      */           }
/* 1045 */           return new Dimension(m + i * n + (i - 1) * this.hGap, k + j);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1050 */       return new Dimension(0, 0);
/*      */     }
/*      */     
/*      */     public Dimension preferredLayoutSize(Container paramContainer) {
/* 1054 */       return minimumLayoutSize(paramContainer);
/*      */     }
/*      */     
/*      */     public void removeLayoutComponent(Component paramComponent) {}
/*      */   }
/*      */   
/*      */   private static void groupLabels(AlignedLabel[] paramArrayOfAlignedLabel) {
/* 1061 */     for (int i = 0; i < paramArrayOfAlignedLabel.length; i++) {
/* 1062 */       paramArrayOfAlignedLabel[i].group = paramArrayOfAlignedLabel;
/*      */     }
/*      */   }
/*      */   
/*      */   private class AlignedLabel extends JLabel {
/*      */     private AlignedLabel[] group;
/* 1068 */     private int maxWidth = 0;
/*      */     
/*      */     AlignedLabel()
/*      */     {
/* 1072 */       setAlignmentX(0.0F);
/*      */     }
/*      */     
/*      */     AlignedLabel(String paramString) {
/* 1076 */       super();
/* 1077 */       setAlignmentX(0.0F);
/*      */     }
/*      */     
/*      */     public Dimension getPreferredSize() {
/* 1081 */       Dimension localDimension = super.getPreferredSize();
/*      */       
/* 1083 */       return new Dimension(getMaxWidth() + 11, localDimension.height);
/*      */     }
/*      */     
/*      */     private int getMaxWidth() {
/* 1087 */       if ((this.maxWidth == 0) && (this.group != null)) {
/* 1088 */         int i = 0;
/* 1089 */         for (int j = 0; j < this.group.length; j++) {
/* 1090 */           i = Math.max(this.group[j].getSuperPreferredWidth(), i);
/*      */         }
/* 1092 */         for (j = 0; j < this.group.length; j++) {
/* 1093 */           this.group[j].maxWidth = i;
/*      */         }
/*      */       }
/* 1096 */       return this.maxWidth;
/*      */     }
/*      */     
/*      */     private int getSuperPreferredWidth() {
/* 1100 */       return super.getPreferredSize().width;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\swing\plaf\synth\SynthFileChooserUIImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
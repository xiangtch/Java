/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Point;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.security.AccessController;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Vector;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.swing.AbstractButton;
/*      */ import javax.swing.DefaultListModel;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JDialog;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JMenu;
/*      */ import javax.swing.JMenuBar;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JTextArea;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JViewport;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.ListModel;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import sun.security.action.GetPropertyAction;
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
/*      */ class ToolWindow
/*      */   extends JFrame
/*      */ {
/*      */   private static final long serialVersionUID = 5682568601210376777L;
/* 1020 */   static final KeyStroke escKey = KeyStroke.getKeyStroke(27, 0);
/*      */   
/*      */ 
/* 1023 */   public static final Insets TOP_PADDING = new Insets(25, 0, 0, 0);
/* 1024 */   public static final Insets BOTTOM_PADDING = new Insets(0, 0, 25, 0);
/* 1025 */   public static final Insets LITE_BOTTOM_PADDING = new Insets(0, 0, 10, 0);
/* 1026 */   public static final Insets LR_PADDING = new Insets(0, 10, 0, 10);
/* 1027 */   public static final Insets TOP_BOTTOM_PADDING = new Insets(15, 0, 15, 0);
/* 1028 */   public static final Insets L_TOP_BOTTOM_PADDING = new Insets(5, 10, 15, 0);
/* 1029 */   public static final Insets LR_TOP_BOTTOM_PADDING = new Insets(15, 4, 15, 4);
/* 1030 */   public static final Insets LR_BOTTOM_PADDING = new Insets(0, 10, 5, 10);
/* 1031 */   public static final Insets L_BOTTOM_PADDING = new Insets(0, 10, 5, 0);
/* 1032 */   public static final Insets R_BOTTOM_PADDING = new Insets(0, 0, 25, 5);
/* 1033 */   public static final Insets R_PADDING = new Insets(0, 0, 0, 5);
/*      */   
/*      */   public static final String NEW_POLICY_FILE = "New";
/*      */   
/*      */   public static final String OPEN_POLICY_FILE = "Open";
/*      */   
/*      */   public static final String SAVE_POLICY_FILE = "Save";
/*      */   
/*      */   public static final String SAVE_AS_POLICY_FILE = "Save.As";
/*      */   
/*      */   public static final String VIEW_WARNINGS = "View.Warning.Log";
/*      */   
/*      */   public static final String QUIT = "Exit";
/*      */   public static final String ADD_POLICY_ENTRY = "Add.Policy.Entry";
/*      */   public static final String EDIT_POLICY_ENTRY = "Edit.Policy.Entry";
/*      */   public static final String REMOVE_POLICY_ENTRY = "Remove.Policy.Entry";
/*      */   public static final String EDIT_KEYSTORE = "Edit";
/*      */   public static final String ADD_PUBKEY_ALIAS = "Add.Public.Key.Alias";
/*      */   public static final String REMOVE_PUBKEY_ALIAS = "Remove.Public.Key.Alias";
/*      */   public static final int MW_FILENAME_LABEL = 0;
/*      */   public static final int MW_FILENAME_TEXTFIELD = 1;
/*      */   public static final int MW_PANEL = 2;
/*      */   public static final int MW_ADD_BUTTON = 0;
/*      */   public static final int MW_EDIT_BUTTON = 1;
/*      */   public static final int MW_REMOVE_BUTTON = 2;
/*      */   public static final int MW_POLICY_LIST = 3;
/* 1059 */   static final int TEXTFIELD_HEIGHT = new JComboBox().getPreferredSize().height;
/*      */   
/*      */ 
/*      */   private PolicyTool tool;
/*      */   
/*      */ 
/*      */   ToolWindow(PolicyTool paramPolicyTool)
/*      */   {
/* 1067 */     this.tool = paramPolicyTool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Component getComponent(int paramInt)
/*      */   {
/* 1074 */     Component localComponent = getContentPane().getComponent(paramInt);
/* 1075 */     if ((localComponent instanceof JScrollPane)) {
/* 1076 */       localComponent = ((JScrollPane)localComponent).getViewport().getView();
/*      */     }
/* 1078 */     return localComponent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initWindow()
/*      */   {
/* 1086 */     setDefaultCloseOperation(0);
/*      */     
/*      */ 
/* 1089 */     JMenuBar localJMenuBar = new JMenuBar();
/*      */     
/*      */ 
/* 1092 */     JMenu localJMenu = new JMenu();
/* 1093 */     configureButton(localJMenu, "File");
/* 1094 */     Object localObject1 = new FileMenuListener(this.tool, this);
/* 1095 */     addMenuItem(localJMenu, "New", (ActionListener)localObject1, "N");
/* 1096 */     addMenuItem(localJMenu, "Open", (ActionListener)localObject1, "O");
/* 1097 */     addMenuItem(localJMenu, "Save", (ActionListener)localObject1, "S");
/* 1098 */     addMenuItem(localJMenu, "Save.As", (ActionListener)localObject1, null);
/* 1099 */     addMenuItem(localJMenu, "View.Warning.Log", (ActionListener)localObject1, null);
/* 1100 */     addMenuItem(localJMenu, "Exit", (ActionListener)localObject1, null);
/* 1101 */     localJMenuBar.add(localJMenu);
/*      */     
/*      */ 
/* 1104 */     localJMenu = new JMenu();
/* 1105 */     configureButton(localJMenu, "KeyStore");
/* 1106 */     localObject1 = new MainWindowListener(this.tool, this);
/* 1107 */     addMenuItem(localJMenu, "Edit", (ActionListener)localObject1, null);
/* 1108 */     localJMenuBar.add(localJMenu);
/* 1109 */     setJMenuBar(localJMenuBar);
/*      */     
/*      */ 
/* 1112 */     ((JPanel)getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
/*      */     
/*      */ 
/* 1115 */     JLabel localJLabel = new JLabel(PolicyTool.getMessage("Policy.File."));
/* 1116 */     addNewComponent(this, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, LR_TOP_BOTTOM_PADDING);
/*      */     
/*      */ 
/* 1119 */     JTextField localJTextField = new JTextField(50);
/* 1120 */     localJTextField.setPreferredSize(new Dimension(localJTextField.getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 1121 */     localJTextField.getAccessibleContext().setAccessibleName(
/* 1122 */       PolicyTool.getMessage("Policy.File."));
/* 1123 */     localJTextField.setEditable(false);
/* 1124 */     addNewComponent(this, localJTextField, 1, 1, 0, 1, 1, 0.0D, 0.0D, 1, LR_TOP_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1130 */     JPanel localJPanel = new JPanel();
/* 1131 */     localJPanel.setLayout(new GridBagLayout());
/*      */     
/* 1133 */     JButton localJButton = new JButton();
/* 1134 */     configureButton(localJButton, "Add.Policy.Entry");
/* 1135 */     localJButton.addActionListener(new MainWindowListener(this.tool, this));
/* 1136 */     addNewComponent(localJPanel, localJButton, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 1140 */     localJButton = new JButton();
/* 1141 */     configureButton(localJButton, "Edit.Policy.Entry");
/* 1142 */     localJButton.addActionListener(new MainWindowListener(this.tool, this));
/* 1143 */     addNewComponent(localJPanel, localJButton, 1, 1, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 1147 */     localJButton = new JButton();
/* 1148 */     configureButton(localJButton, "Remove.Policy.Entry");
/* 1149 */     localJButton.addActionListener(new MainWindowListener(this.tool, this));
/* 1150 */     addNewComponent(localJPanel, localJButton, 2, 2, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 1154 */     addNewComponent(this, localJPanel, 2, 0, 2, 2, 1, 0.0D, 0.0D, 1, BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1159 */     String str = this.tool.getPolicyFileName();
/* 1160 */     Object localObject2; if (str == null)
/*      */     {
/* 1162 */       localObject2 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
/*      */       
/* 1164 */       str = (String)localObject2 + File.separatorChar + ".java.policy";
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 1169 */       this.tool.openPolicy(str);
/*      */       
/*      */ 
/* 1172 */       localObject2 = new DefaultListModel();
/* 1173 */       localJList = new JList((ListModel)localObject2);
/* 1174 */       localJList.setVisibleRowCount(15);
/* 1175 */       localJList.setSelectionMode(0);
/* 1176 */       localJList.addMouseListener(new PolicyListListener(this.tool, this));
/* 1177 */       localObject3 = this.tool.getEntry();
/* 1178 */       if (localObject3 != null) {
/* 1179 */         for (int i = 0; i < localObject3.length; i++) {
/* 1180 */           ((DefaultListModel)localObject2).addElement(localObject3[i].headerToString());
/*      */         }
/*      */       }
/*      */       
/* 1184 */       localObject4 = (JTextField)getComponent(1);
/* 1185 */       ((JTextField)localObject4).setText(str);
/* 1186 */       initPolicyList(localJList);
/*      */     }
/*      */     catch (FileNotFoundException localFileNotFoundException)
/*      */     {
/* 1190 */       localJList = new JList(new DefaultListModel());
/* 1191 */       localJList.setVisibleRowCount(15);
/* 1192 */       localJList.setSelectionMode(0);
/* 1193 */       localJList.addMouseListener(new PolicyListListener(this.tool, this));
/* 1194 */       initPolicyList(localJList);
/* 1195 */       this.tool.setPolicyFileName(null);
/* 1196 */       this.tool.modified = false;
/*      */       
/*      */ 
/* 1199 */       this.tool.warnings.addElement(localFileNotFoundException.toString());
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 1203 */       JList localJList = new JList(new DefaultListModel());
/* 1204 */       localJList.setVisibleRowCount(15);
/* 1205 */       localJList.setSelectionMode(0);
/* 1206 */       localJList.addMouseListener(new PolicyListListener(this.tool, this));
/* 1207 */       initPolicyList(localJList);
/* 1208 */       this.tool.setPolicyFileName(null);
/* 1209 */       this.tool.modified = false;
/*      */       
/*      */ 
/*      */ 
/* 1213 */       Object localObject3 = new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString."));
/* 1214 */       Object localObject4 = { str, localException.toString() };
/* 1215 */       displayErrorDialog(null, ((MessageFormat)localObject3).format(localObject4));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/* 1221 */   private int shortCutModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
/*      */   
/*      */   private void addMenuItem(JMenu paramJMenu, String paramString1, ActionListener paramActionListener, String paramString2) {
/* 1224 */     JMenuItem localJMenuItem = new JMenuItem();
/* 1225 */     configureButton(localJMenuItem, paramString1);
/*      */     
/* 1227 */     if (PolicyTool.rb.containsKey(paramString1 + ".accelerator"))
/*      */     {
/* 1229 */       paramString2 = PolicyTool.getMessage(paramString1 + ".accelerator");
/*      */     }
/*      */     
/* 1232 */     if ((paramString2 != null) && (!paramString2.isEmpty())) {
/*      */       KeyStroke localKeyStroke;
/* 1234 */       if (paramString2.length() == 1) {
/* 1235 */         localKeyStroke = KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar(paramString2.charAt(0)), this.shortCutModifier);
/*      */       }
/*      */       else {
/* 1238 */         localKeyStroke = KeyStroke.getKeyStroke(paramString2);
/*      */       }
/* 1240 */       localJMenuItem.setAccelerator(localKeyStroke);
/*      */     }
/*      */     
/* 1243 */     localJMenuItem.addActionListener(paramActionListener);
/* 1244 */     paramJMenu.add(localJMenuItem);
/*      */   }
/*      */   
/*      */   static void configureButton(AbstractButton paramAbstractButton, String paramString) {
/* 1248 */     paramAbstractButton.setText(PolicyTool.getMessage(paramString));
/* 1249 */     paramAbstractButton.setActionCommand(paramString);
/*      */     
/* 1251 */     int i = PolicyTool.getMnemonicInt(paramString);
/* 1252 */     if (i > 0) {
/* 1253 */       paramAbstractButton.setMnemonic(i);
/* 1254 */       paramAbstractButton.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(paramString));
/*      */     }
/*      */   }
/*      */   
/*      */   static void configureLabelFor(JLabel paramJLabel, JComponent paramJComponent, String paramString) {
/* 1259 */     paramJLabel.setText(PolicyTool.getMessage(paramString));
/* 1260 */     paramJLabel.setLabelFor(paramJComponent);
/*      */     
/* 1262 */     int i = PolicyTool.getMnemonicInt(paramString);
/* 1263 */     if (i > 0) {
/* 1264 */       paramJLabel.setDisplayedMnemonic(i);
/* 1265 */       paramJLabel.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(paramString));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void addNewComponent(Container paramContainer, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, int paramInt6, Insets paramInsets)
/*      */   {
/* 1277 */     if ((paramContainer instanceof JFrame)) {
/* 1278 */       paramContainer = ((JFrame)paramContainer).getContentPane();
/* 1279 */     } else if ((paramContainer instanceof JDialog)) {
/* 1280 */       paramContainer = ((JDialog)paramContainer).getContentPane();
/*      */     }
/*      */     
/*      */ 
/* 1284 */     paramContainer.add(paramJComponent, paramInt1);
/*      */     
/*      */ 
/* 1287 */     GridBagLayout localGridBagLayout = (GridBagLayout)paramContainer.getLayout();
/* 1288 */     GridBagConstraints localGridBagConstraints = new GridBagConstraints();
/* 1289 */     localGridBagConstraints.gridx = paramInt2;
/* 1290 */     localGridBagConstraints.gridy = paramInt3;
/* 1291 */     localGridBagConstraints.gridwidth = paramInt4;
/* 1292 */     localGridBagConstraints.gridheight = paramInt5;
/* 1293 */     localGridBagConstraints.weightx = paramDouble1;
/* 1294 */     localGridBagConstraints.weighty = paramDouble2;
/* 1295 */     localGridBagConstraints.fill = paramInt6;
/* 1296 */     if (paramInsets != null) localGridBagConstraints.insets = paramInsets;
/* 1297 */     localGridBagLayout.setConstraints(paramJComponent, localGridBagConstraints);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void addNewComponent(Container paramContainer, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, int paramInt6)
/*      */   {
/* 1309 */     addNewComponent(paramContainer, paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramDouble1, paramDouble2, paramInt6, null);
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
/*      */   void initPolicyList(JList paramJList)
/*      */   {
/* 1323 */     JScrollPane localJScrollPane = new JScrollPane(paramJList);
/* 1324 */     addNewComponent(this, localJScrollPane, 3, 0, 3, 2, 1, 1.0D, 1.0D, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void replacePolicyList(JList paramJList)
/*      */   {
/* 1336 */     JList localJList = (JList)getComponent(3);
/* 1337 */     localJList.setModel(paramJList.getModel());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void displayToolWindow(String[] paramArrayOfString)
/*      */   {
/* 1345 */     setTitle(PolicyTool.getMessage("Policy.Tool"));
/* 1346 */     setResizable(true);
/* 1347 */     addWindowListener(new ToolWindowListener(this.tool, this));
/*      */     
/* 1349 */     getContentPane().setLayout(new GridBagLayout());
/*      */     
/* 1351 */     initWindow();
/* 1352 */     pack();
/* 1353 */     setLocationRelativeTo(null);
/*      */     
/*      */ 
/* 1356 */     setVisible(true);
/*      */     
/* 1358 */     if (this.tool.newWarning == true) {
/* 1359 */       displayStatusDialog(this, 
/* 1360 */         PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information."));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void displayErrorDialog(Window paramWindow, String paramString)
/*      */   {
/* 1369 */     ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Error"), this.tool, this, true);
/*      */     
/*      */ 
/*      */ 
/* 1373 */     Point localPoint = paramWindow == null ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
/*      */     
/* 1375 */     localToolDialog.setLayout(new GridBagLayout());
/*      */     
/* 1377 */     JLabel localJLabel = new JLabel(paramString);
/* 1378 */     addNewComponent(localToolDialog, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
/*      */     
/*      */ 
/* 1381 */     JButton localJButton = new JButton(PolicyTool.getMessage("OK"));
/* 1382 */     ErrorOKButtonListener localErrorOKButtonListener = new ErrorOKButtonListener(localToolDialog);
/* 1383 */     localJButton.addActionListener(localErrorOKButtonListener);
/* 1384 */     addNewComponent(localToolDialog, localJButton, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
/*      */     
/*      */ 
/* 1387 */     localToolDialog.getRootPane().setDefaultButton(localJButton);
/* 1388 */     localToolDialog.getRootPane().registerKeyboardAction(localErrorOKButtonListener, escKey, 2);
/*      */     
/* 1390 */     localToolDialog.pack();
/* 1391 */     localToolDialog.setLocationRelativeTo(paramWindow);
/* 1392 */     localToolDialog.setVisible(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void displayErrorDialog(Window paramWindow, Throwable paramThrowable)
/*      */   {
/* 1399 */     if ((paramThrowable instanceof NoDisplayException)) {
/* 1400 */       return;
/*      */     }
/* 1402 */     displayErrorDialog(paramWindow, paramThrowable.toString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void displayStatusDialog(Window paramWindow, String paramString)
/*      */   {
/* 1410 */     ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Status"), this.tool, this, true);
/*      */     
/*      */ 
/*      */ 
/* 1414 */     Point localPoint = paramWindow == null ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
/*      */     
/* 1416 */     localToolDialog.setLayout(new GridBagLayout());
/*      */     
/* 1418 */     JLabel localJLabel = new JLabel(paramString);
/* 1419 */     addNewComponent(localToolDialog, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
/*      */     
/*      */ 
/* 1422 */     JButton localJButton = new JButton(PolicyTool.getMessage("OK"));
/* 1423 */     StatusOKButtonListener localStatusOKButtonListener = new StatusOKButtonListener(localToolDialog);
/* 1424 */     localJButton.addActionListener(localStatusOKButtonListener);
/* 1425 */     addNewComponent(localToolDialog, localJButton, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
/*      */     
/*      */ 
/* 1428 */     localToolDialog.getRootPane().setDefaultButton(localJButton);
/* 1429 */     localToolDialog.getRootPane().registerKeyboardAction(localStatusOKButtonListener, escKey, 2);
/*      */     
/* 1431 */     localToolDialog.pack();
/* 1432 */     localToolDialog.setLocationRelativeTo(paramWindow);
/* 1433 */     localToolDialog.setVisible(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void displayWarningLog(Window paramWindow)
/*      */   {
/* 1442 */     ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Warning"), this.tool, this, true);
/*      */     
/*      */ 
/*      */ 
/* 1446 */     Point localPoint = paramWindow == null ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
/*      */     
/* 1448 */     localToolDialog.setLayout(new GridBagLayout());
/*      */     
/* 1450 */     JTextArea localJTextArea = new JTextArea();
/* 1451 */     localJTextArea.setEditable(false);
/* 1452 */     for (int i = 0; i < this.tool.warnings.size(); i++) {
/* 1453 */       localJTextArea.append((String)this.tool.warnings.elementAt(i));
/* 1454 */       localJTextArea.append(PolicyTool.getMessage("NEWLINE"));
/*      */     }
/* 1456 */     addNewComponent(localToolDialog, localJTextArea, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, BOTTOM_PADDING);
/*      */     
/*      */ 
/* 1459 */     localJTextArea.setFocusable(false);
/*      */     
/* 1461 */     JButton localJButton = new JButton(PolicyTool.getMessage("OK"));
/* 1462 */     CancelButtonListener localCancelButtonListener = new CancelButtonListener(localToolDialog);
/* 1463 */     localJButton.addActionListener(localCancelButtonListener);
/* 1464 */     addNewComponent(localToolDialog, localJButton, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 1468 */     localToolDialog.getRootPane().setDefaultButton(localJButton);
/* 1469 */     localToolDialog.getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
/*      */     
/* 1471 */     localToolDialog.pack();
/* 1472 */     localToolDialog.setLocationRelativeTo(paramWindow);
/* 1473 */     localToolDialog.setVisible(true);
/*      */   }
/*      */   
/*      */   char displayYesNoDialog(Window paramWindow, String paramString1, String paramString2, String paramString3, String paramString4)
/*      */   {
/* 1478 */     final ToolDialog localToolDialog = new ToolDialog(paramString1, this.tool, this, true);
/*      */     
/*      */ 
/* 1481 */     Point localPoint = paramWindow == null ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
/*      */     
/* 1483 */     localToolDialog.setLayout(new GridBagLayout());
/*      */     
/* 1485 */     JTextArea localJTextArea = new JTextArea(paramString2, 10, 50);
/* 1486 */     localJTextArea.setEditable(false);
/* 1487 */     localJTextArea.setLineWrap(true);
/* 1488 */     localJTextArea.setWrapStyleWord(true);
/* 1489 */     JScrollPane localJScrollPane = new JScrollPane(localJTextArea, 20, 31);
/*      */     
/*      */ 
/* 1492 */     addNewComponent(localToolDialog, localJScrollPane, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
/*      */     
/* 1494 */     localJTextArea.setFocusable(false);
/*      */     
/* 1496 */     JPanel localJPanel = new JPanel();
/* 1497 */     localJPanel.setLayout(new GridBagLayout());
/*      */     
/*      */ 
/* 1500 */     final StringBuffer localStringBuffer = new StringBuffer();
/*      */     
/* 1502 */     JButton localJButton = new JButton(paramString3);
/* 1503 */     localJButton.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/* 1505 */         localStringBuffer.append('Y');
/* 1506 */         localToolDialog.setVisible(false);
/* 1507 */         localToolDialog.dispose();
/*      */       }
/* 1509 */     });
/* 1510 */     addNewComponent(localJPanel, localJButton, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 1514 */     localJButton = new JButton(paramString4);
/* 1515 */     localJButton.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
/* 1517 */         localStringBuffer.append('N');
/* 1518 */         localToolDialog.setVisible(false);
/* 1519 */         localToolDialog.dispose();
/*      */       }
/* 1521 */     });
/* 1522 */     addNewComponent(localJPanel, localJButton, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 1526 */     addNewComponent(localToolDialog, localJPanel, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
/*      */     
/*      */ 
/* 1529 */     localToolDialog.pack();
/* 1530 */     localToolDialog.setLocationRelativeTo(paramWindow);
/* 1531 */     localToolDialog.setVisible(true);
/* 1532 */     if (localStringBuffer.length() > 0) {
/* 1533 */       return localStringBuffer.charAt(0);
/*      */     }
/*      */     
/* 1536 */     return 'N';
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\tools\policytool\ToolWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
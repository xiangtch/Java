/*     */ package sun.swing;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.File;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JToggleButton;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.BevelBorder;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ import sun.awt.OSInfo;
/*     */ import sun.awt.OSInfo.OSType;
/*     */ import sun.awt.OSInfo.WindowsVersion;
/*     */ import sun.awt.shell.ShellFolder;
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
/*     */ public class WindowsPlacesBar
/*     */   extends JToolBar
/*     */   implements ActionListener, PropertyChangeListener
/*     */ {
/*     */   JFileChooser fc;
/*     */   JToggleButton[] buttons;
/*     */   ButtonGroup buttonGroup;
/*     */   File[] files;
/*     */   final Dimension buttonSize;
/*     */   
/*     */   public WindowsPlacesBar(JFileChooser paramJFileChooser, boolean paramBoolean)
/*     */   {
/*  59 */     super(1);
/*  60 */     this.fc = paramJFileChooser;
/*  61 */     setFloatable(false);
/*  62 */     putClientProperty("JToolBar.isRollover", Boolean.TRUE);
/*     */     
/*     */ 
/*  65 */     int i = (OSInfo.getOSType() == OSType.WINDOWS) && (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) >= 0) ? 1 : 0;
/*     */     
/*  67 */     if (paramBoolean) {
/*  68 */       this.buttonSize = new Dimension(83, 69);
/*  69 */       putClientProperty("XPStyle.subAppName", "placesbar");
/*  70 */       setBorder(new EmptyBorder(1, 1, 1, 1));
/*     */     }
/*     */     else {
/*  73 */       this.buttonSize = new Dimension(83, i != 0 ? 65 : 54);
/*  74 */       setBorder(new BevelBorder(1, 
/*  75 */         UIManager.getColor("ToolBar.highlight"), 
/*  76 */         UIManager.getColor("ToolBar.background"), 
/*  77 */         UIManager.getColor("ToolBar.darkShadow"), 
/*  78 */         UIManager.getColor("ToolBar.shadow")));
/*     */     }
/*  80 */     Color localColor = new Color(UIManager.getColor("ToolBar.shadow").getRGB());
/*  81 */     setBackground(localColor);
/*  82 */     FileSystemView localFileSystemView = paramJFileChooser.getFileSystemView();
/*     */     
/*  84 */     this.files = ((File[])ShellFolder.get("fileChooserShortcutPanelFolders"));
/*     */     
/*  86 */     this.buttons = new JToggleButton[this.files.length];
/*  87 */     this.buttonGroup = new ButtonGroup();
/*  88 */     for (int j = 0; j < this.files.length; j++) {
/*  89 */       if (localFileSystemView.isFileSystemRoot(this.files[j]))
/*     */       {
/*  91 */         this.files[j] = localFileSystemView.createFileObject(this.files[j].getAbsolutePath());
/*     */       }
/*     */       
/*  94 */       String str = localFileSystemView.getSystemDisplayName(this.files[j]);
/*  95 */       int k = str.lastIndexOf(File.separatorChar);
/*  96 */       if ((k >= 0) && (k < str.length() - 1))
/*  97 */         str = str.substring(k + 1);
/*     */       Object localObject2;
/*     */       Object localObject1;
/* 100 */       if ((this.files[j] instanceof ShellFolder))
/*     */       {
/* 102 */         localObject2 = (ShellFolder)this.files[j];
/* 103 */         Image localImage = ((ShellFolder)localObject2).getIcon(true);
/*     */         
/* 105 */         if (localImage == null)
/*     */         {
/* 107 */           localImage = (Image)ShellFolder.get("shell32LargeIcon 1");
/*     */         }
/*     */         
/* 110 */         localObject1 = localImage == null ? null : new ImageIcon(localImage, ((ShellFolder)localObject2).getFolderType());
/*     */       } else {
/* 112 */         localObject1 = localFileSystemView.getSystemIcon(this.files[j]);
/*     */       }
/* 114 */       this.buttons[j] = new JToggleButton(str, (Icon)localObject1);
/* 115 */       if (paramBoolean) {
/* 116 */         this.buttons[j].putClientProperty("XPStyle.subAppName", "placesbar");
/*     */       } else {
/* 118 */         localObject2 = new Color(UIManager.getColor("List.selectionForeground").getRGB());
/* 119 */         this.buttons[j].setContentAreaFilled(false);
/* 120 */         this.buttons[j].setForeground((Color)localObject2);
/*     */       }
/* 122 */       this.buttons[j].setMargin(new Insets(3, 2, 1, 2));
/* 123 */       this.buttons[j].setFocusPainted(false);
/* 124 */       this.buttons[j].setIconTextGap(0);
/* 125 */       this.buttons[j].setHorizontalTextPosition(0);
/* 126 */       this.buttons[j].setVerticalTextPosition(3);
/* 127 */       this.buttons[j].setAlignmentX(0.5F);
/* 128 */       this.buttons[j].setPreferredSize(this.buttonSize);
/* 129 */       this.buttons[j].setMaximumSize(this.buttonSize);
/* 130 */       this.buttons[j].addActionListener(this);
/* 131 */       add(this.buttons[j]);
/* 132 */       if ((j < this.files.length - 1) && (paramBoolean)) {
/* 133 */         add(Box.createRigidArea(new Dimension(1, 1)));
/*     */       }
/* 135 */       this.buttonGroup.add(this.buttons[j]);
/*     */     }
/* 137 */     doDirectoryChanged(paramJFileChooser.getCurrentDirectory());
/*     */   }
/*     */   
/*     */   protected void doDirectoryChanged(File paramFile) {
/* 141 */     for (int i = 0; i < this.buttons.length; i++) {
/* 142 */       JToggleButton localJToggleButton = this.buttons[i];
/* 143 */       if (this.files[i].equals(paramFile)) {
/* 144 */         localJToggleButton.setSelected(true);
/* 145 */         break; }
/* 146 */       if (localJToggleButton.isSelected())
/*     */       {
/*     */ 
/* 149 */         this.buttonGroup.remove(localJToggleButton);
/* 150 */         localJToggleButton.setSelected(false);
/* 151 */         this.buttonGroup.add(localJToggleButton);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
/* 157 */     String str = paramPropertyChangeEvent.getPropertyName();
/* 158 */     if (str == "directoryChanged") {
/* 159 */       doDirectoryChanged(this.fc.getCurrentDirectory());
/*     */     }
/*     */   }
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 164 */     JToggleButton localJToggleButton = (JToggleButton)paramActionEvent.getSource();
/* 165 */     for (int i = 0; i < this.buttons.length; i++) {
/* 166 */       if (localJToggleButton == this.buttons[i]) {
/* 167 */         this.fc.setCurrentDirectory(this.files[i]);
/* 168 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Dimension getPreferredSize() {
/* 174 */     Dimension localDimension1 = super.getMinimumSize();
/* 175 */     Dimension localDimension2 = super.getPreferredSize();
/* 176 */     int i = localDimension1.height;
/* 177 */     if ((this.buttons != null) && (this.buttons.length > 0) && (this.buttons.length < 5)) {
/* 178 */       JToggleButton localJToggleButton = this.buttons[0];
/* 179 */       if (localJToggleButton != null) {
/* 180 */         int j = 5 * (localJToggleButton.getPreferredSize().height + 1);
/* 181 */         if (j > i) {
/* 182 */           i = j;
/*     */         }
/*     */       }
/*     */     }
/* 186 */     if (i > localDimension2.height) {
/* 187 */       localDimension2 = new Dimension(localDimension2.width, i);
/*     */     }
/* 189 */     return localDimension2;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\swing\WindowsPlacesBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
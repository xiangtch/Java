/*      */ package sun.security.tools.policytool;
/*      */ 
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FileDialog;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.Point;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.net.MalformedURLException;
/*      */ import java.security.InvalidParameterException;
/*      */ import java.security.PublicKey;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.LinkedList;
/*      */ import java.util.Vector;
/*      */ import javax.accessibility.AccessibleContext;
/*      */ import javax.swing.DefaultListModel;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JDialog;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.JViewport;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.ListModel;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import sun.security.provider.PolicyParser.GrantEntry;
/*      */ import sun.security.provider.PolicyParser.PermissionEntry;
/*      */ import sun.security.provider.PolicyParser.PrincipalEntry;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class ToolDialog
/*      */   extends JDialog
/*      */ {
/*      */   private static final long serialVersionUID = -372244357011301190L;
/* 1550 */   static final KeyStroke escKey = KeyStroke.getKeyStroke(27, 0);
/*      */   
/*      */ 
/*      */   public static final int NOACTION = 0;
/*      */   
/*      */ 
/*      */   public static final int QUIT = 1;
/*      */   
/*      */ 
/*      */   public static final int NEW = 2;
/*      */   
/*      */   public static final int OPEN = 3;
/*      */   
/*      */   public static final String ALL_PERM_CLASS = "java.security.AllPermission";
/*      */   
/*      */   public static final String FILE_PERM_CLASS = "java.io.FilePermission";
/*      */   
/*      */   public static final String X500_PRIN_CLASS = "javax.security.auth.x500.X500Principal";
/*      */   
/* 1569 */   public static final String PERM = PolicyTool.getMessage("Permission.");
/*      */   
/*      */ 
/* 1572 */   public static final String PRIN_TYPE = PolicyTool.getMessage("Principal.Type.");
/*      */   
/* 1574 */   public static final String PRIN_NAME = PolicyTool.getMessage("Principal.Name.");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 1579 */   public static final String PERM_NAME = PolicyTool.getMessage("Target.Name.");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 1584 */   public static final String PERM_ACTIONS = PolicyTool.getMessage("Actions.");
/*      */   
/*      */   public static final int PE_CODEBASE_LABEL = 0;
/*      */   
/*      */   public static final int PE_CODEBASE_TEXTFIELD = 1;
/*      */   
/*      */   public static final int PE_SIGNEDBY_LABEL = 2;
/*      */   
/*      */   public static final int PE_SIGNEDBY_TEXTFIELD = 3;
/*      */   
/*      */   public static final int PE_PANEL0 = 4;
/*      */   
/*      */   public static final int PE_ADD_PRIN_BUTTON = 0;
/*      */   
/*      */   public static final int PE_EDIT_PRIN_BUTTON = 1;
/*      */   
/*      */   public static final int PE_REMOVE_PRIN_BUTTON = 2;
/*      */   
/*      */   public static final int PE_PRIN_LABEL = 5;
/*      */   
/*      */   public static final int PE_PRIN_LIST = 6;
/*      */   
/*      */   public static final int PE_PANEL1 = 7;
/*      */   
/*      */   public static final int PE_ADD_PERM_BUTTON = 0;
/*      */   
/*      */   public static final int PE_EDIT_PERM_BUTTON = 1;
/*      */   
/*      */   public static final int PE_REMOVE_PERM_BUTTON = 2;
/*      */   
/*      */   public static final int PE_PERM_LIST = 8;
/*      */   
/*      */   public static final int PE_PANEL2 = 9;
/*      */   
/*      */   public static final int PE_CANCEL_BUTTON = 1;
/*      */   
/*      */   public static final int PE_DONE_BUTTON = 0;
/*      */   
/*      */   public static final int PRD_DESC_LABEL = 0;
/*      */   
/*      */   public static final int PRD_PRIN_CHOICE = 1;
/*      */   
/*      */   public static final int PRD_PRIN_TEXTFIELD = 2;
/*      */   
/*      */   public static final int PRD_NAME_LABEL = 3;
/*      */   
/*      */   public static final int PRD_NAME_TEXTFIELD = 4;
/*      */   public static final int PRD_CANCEL_BUTTON = 6;
/*      */   public static final int PRD_OK_BUTTON = 5;
/*      */   public static final int PD_DESC_LABEL = 0;
/*      */   public static final int PD_PERM_CHOICE = 1;
/*      */   public static final int PD_PERM_TEXTFIELD = 2;
/*      */   public static final int PD_NAME_CHOICE = 3;
/*      */   public static final int PD_NAME_TEXTFIELD = 4;
/*      */   public static final int PD_ACTIONS_CHOICE = 5;
/*      */   public static final int PD_ACTIONS_TEXTFIELD = 6;
/*      */   public static final int PD_SIGNEDBY_LABEL = 7;
/*      */   public static final int PD_SIGNEDBY_TEXTFIELD = 8;
/*      */   public static final int PD_CANCEL_BUTTON = 10;
/*      */   public static final int PD_OK_BUTTON = 9;
/*      */   public static final int EDIT_KEYSTORE = 0;
/*      */   public static final int KSD_NAME_LABEL = 0;
/*      */   public static final int KSD_NAME_TEXTFIELD = 1;
/*      */   public static final int KSD_TYPE_LABEL = 2;
/*      */   public static final int KSD_TYPE_TEXTFIELD = 3;
/*      */   public static final int KSD_PROVIDER_LABEL = 4;
/*      */   public static final int KSD_PROVIDER_TEXTFIELD = 5;
/*      */   public static final int KSD_PWD_URL_LABEL = 6;
/*      */   public static final int KSD_PWD_URL_TEXTFIELD = 7;
/*      */   public static final int KSD_CANCEL_BUTTON = 9;
/*      */   public static final int KSD_OK_BUTTON = 8;
/*      */   public static final int USC_LABEL = 0;
/*      */   public static final int USC_PANEL = 1;
/*      */   public static final int USC_YES_BUTTON = 0;
/*      */   public static final int USC_NO_BUTTON = 1;
/*      */   public static final int USC_CANCEL_BUTTON = 2;
/*      */   public static final int CRPE_LABEL1 = 0;
/*      */   public static final int CRPE_LABEL2 = 1;
/*      */   public static final int CRPE_PANEL = 2;
/*      */   public static final int CRPE_PANEL_OK = 0;
/*      */   public static final int CRPE_PANEL_CANCEL = 1;
/*      */   private static final int PERMISSION = 0;
/*      */   private static final int PERMISSION_NAME = 1;
/*      */   private static final int PERMISSION_ACTIONS = 2;
/*      */   private static final int PERMISSION_SIGNEDBY = 3;
/*      */   private static final int PRINCIPAL_TYPE = 4;
/*      */   private static final int PRINCIPAL_NAME = 5;
/* 1671 */   static final int TEXTFIELD_HEIGHT = new JComboBox().getPreferredSize().height;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1682 */   public static ArrayList<Perm> PERM_ARRAY = new ArrayList();
/* 1683 */   static { PERM_ARRAY.add(new AllPerm());
/* 1684 */     PERM_ARRAY.add(new AudioPerm());
/* 1685 */     PERM_ARRAY.add(new AuthPerm());
/* 1686 */     PERM_ARRAY.add(new AWTPerm());
/* 1687 */     PERM_ARRAY.add(new DelegationPerm());
/* 1688 */     PERM_ARRAY.add(new FilePerm());
/* 1689 */     PERM_ARRAY.add(new URLPerm());
/* 1690 */     PERM_ARRAY.add(new InqSecContextPerm());
/* 1691 */     PERM_ARRAY.add(new LogPerm());
/* 1692 */     PERM_ARRAY.add(new MgmtPerm());
/* 1693 */     PERM_ARRAY.add(new MBeanPerm());
/* 1694 */     PERM_ARRAY.add(new MBeanSvrPerm());
/* 1695 */     PERM_ARRAY.add(new MBeanTrustPerm());
/* 1696 */     PERM_ARRAY.add(new NetPerm());
/* 1697 */     PERM_ARRAY.add(new PrivCredPerm());
/* 1698 */     PERM_ARRAY.add(new PropPerm());
/* 1699 */     PERM_ARRAY.add(new ReflectPerm());
/* 1700 */     PERM_ARRAY.add(new RuntimePerm());
/* 1701 */     PERM_ARRAY.add(new SecurityPerm());
/* 1702 */     PERM_ARRAY.add(new SerialPerm());
/* 1703 */     PERM_ARRAY.add(new ServicePerm());
/* 1704 */     PERM_ARRAY.add(new SocketPerm());
/* 1705 */     PERM_ARRAY.add(new SQLPerm());
/* 1706 */     PERM_ARRAY.add(new SSLPerm());
/* 1707 */     PERM_ARRAY.add(new SubjDelegPerm());
/*      */     
/*      */ 
/*      */ 
/* 1711 */     PRIN_ARRAY = new ArrayList();
/* 1712 */     PRIN_ARRAY.add(new KrbPrin());
/* 1713 */     PRIN_ARRAY.add(new X500Prin());
/*      */   }
/*      */   
/*      */   ToolDialog(String paramString, PolicyTool paramPolicyTool, ToolWindow paramToolWindow, boolean paramBoolean) {
/* 1717 */     super(paramToolWindow, paramBoolean);
/* 1718 */     setTitle(paramString);
/* 1719 */     this.tool = paramPolicyTool;
/* 1720 */     this.tw = paramToolWindow;
/* 1721 */     addWindowListener(new ChildWindowListener(this));
/*      */     
/*      */ 
/* 1724 */     ((JPanel)getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Component getComponent(int paramInt)
/*      */   {
/* 1731 */     Component localComponent = getContentPane().getComponent(paramInt);
/* 1732 */     if ((localComponent instanceof JScrollPane)) {
/* 1733 */       localComponent = ((JScrollPane)localComponent).getViewport().getView();
/*      */     }
/* 1735 */     return localComponent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static Perm getPerm(String paramString, boolean paramBoolean)
/*      */   {
/* 1743 */     for (int i = 0; i < PERM_ARRAY.size(); i++) {
/* 1744 */       Perm localPerm = (Perm)PERM_ARRAY.get(i);
/* 1745 */       if (paramBoolean) {
/* 1746 */         if (localPerm.FULL_CLASS.equals(paramString)) {
/* 1747 */           return localPerm;
/*      */         }
/*      */       }
/* 1750 */       else if (localPerm.CLASS.equals(paramString)) {
/* 1751 */         return localPerm;
/*      */       }
/*      */     }
/*      */     
/* 1755 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static Prin getPrin(String paramString, boolean paramBoolean)
/*      */   {
/* 1763 */     for (int i = 0; i < PRIN_ARRAY.size(); i++) {
/* 1764 */       Prin localPrin = (Prin)PRIN_ARRAY.get(i);
/* 1765 */       if (paramBoolean) {
/* 1766 */         if (localPrin.FULL_CLASS.equals(paramString)) {
/* 1767 */           return localPrin;
/*      */         }
/*      */       }
/* 1770 */       else if (localPrin.CLASS.equals(paramString)) {
/* 1771 */         return localPrin;
/*      */       }
/*      */     }
/*      */     
/* 1775 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ArrayList<Prin> PRIN_ARRAY;
/*      */   
/*      */ 
/*      */   PolicyTool tool;
/*      */   
/*      */ 
/*      */   ToolWindow tw;
/*      */   
/*      */ 
/*      */   void displayPolicyEntryDialog(boolean paramBoolean)
/*      */   {
/* 1792 */     int i = 0;
/* 1793 */     PolicyEntry[] arrayOfPolicyEntry = null;
/* 1794 */     TaggedList localTaggedList1 = new TaggedList(3, false);
/* 1795 */     localTaggedList1.getAccessibleContext().setAccessibleName(
/* 1796 */       PolicyTool.getMessage("Principal.List"));
/* 1797 */     localTaggedList1
/* 1798 */       .addMouseListener(new EditPrinButtonListener(this.tool, this.tw, this, paramBoolean));
/* 1799 */     TaggedList localTaggedList2 = new TaggedList(10, false);
/* 1800 */     localTaggedList2.getAccessibleContext().setAccessibleName(
/* 1801 */       PolicyTool.getMessage("Permission.List"));
/* 1802 */     localTaggedList2
/* 1803 */       .addMouseListener(new EditPermButtonListener(this.tool, this.tw, this, paramBoolean));
/*      */     
/*      */ 
/* 1806 */     Point localPoint = this.tw.getLocationOnScreen();
/*      */     
/* 1808 */     setLayout(new GridBagLayout());
/* 1809 */     setResizable(true);
/*      */     
/* 1811 */     if (paramBoolean)
/*      */     {
/* 1813 */       arrayOfPolicyEntry = this.tool.getEntry();
/* 1814 */       localObject1 = (JList)this.tw.getComponent(3);
/* 1815 */       i = ((JList)localObject1).getSelectedIndex();
/*      */       
/*      */ 
/*      */ 
/* 1819 */       localObject2 = arrayOfPolicyEntry[i].getGrantEntry().principals;
/* 1820 */       for (int j = 0; j < ((LinkedList)localObject2).size(); j++) {
/* 1821 */         Object localObject4 = null;
/* 1822 */         localObject5 = (PolicyParser.PrincipalEntry)((LinkedList)localObject2).get(j);
/* 1823 */         localTaggedList1.addTaggedItem(PrincipalEntryToUserFriendlyString((PolicyParser.PrincipalEntry)localObject5), localObject5);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1828 */       localObject3 = arrayOfPolicyEntry[i].getGrantEntry().permissionEntries;
/* 1829 */       for (int k = 0; k < ((Vector)localObject3).size(); k++) {
/* 1830 */         localObject5 = null;
/*      */         
/* 1832 */         localObject6 = (PolicyParser.PermissionEntry)((Vector)localObject3).elementAt(k);
/* 1833 */         localTaggedList2.addTaggedItem(PermissionEntryToUserFriendlyString((PolicyParser.PermissionEntry)localObject6), localObject6);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1838 */     Object localObject1 = new JLabel();
/* 1839 */     this.tw.addNewComponent(this, (JComponent)localObject1, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1844 */     Object localObject2 = paramBoolean ? new JTextField(arrayOfPolicyEntry[i].getGrantEntry().codeBase) : new JTextField();
/*      */     
/* 1846 */     ToolWindow.configureLabelFor((JLabel)localObject1, (JComponent)localObject2, "CodeBase.");
/* 1847 */     ((JTextField)localObject2).setPreferredSize(new Dimension(((JTextField)localObject2).getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 1848 */     ((JTextField)localObject2).getAccessibleContext().setAccessibleName(
/* 1849 */       PolicyTool.getMessage("Code.Base"));
/* 1850 */     this.tw.addNewComponent(this, (JComponent)localObject2, 1, 1, 0, 1, 1, 1.0D, 0.0D, 1);
/*      */     
/*      */ 
/*      */ 
/* 1854 */     localObject1 = new JLabel();
/* 1855 */     this.tw.addNewComponent(this, (JComponent)localObject1, 2, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 1859 */     localObject2 = paramBoolean ? new JTextField(arrayOfPolicyEntry[i].getGrantEntry().signedBy) : new JTextField();
/*      */     
/* 1861 */     ToolWindow.configureLabelFor((JLabel)localObject1, (JComponent)localObject2, "SignedBy.");
/* 1862 */     ((JTextField)localObject2).setPreferredSize(new Dimension(((JTextField)localObject2).getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 1863 */     ((JTextField)localObject2).getAccessibleContext().setAccessibleName(
/* 1864 */       PolicyTool.getMessage("Signed.By."));
/* 1865 */     this.tw.addNewComponent(this, (JComponent)localObject2, 3, 1, 1, 1, 1, 1.0D, 0.0D, 1);
/*      */     
/*      */ 
/*      */ 
/* 1869 */     Object localObject3 = new JPanel();
/* 1870 */     ((JPanel)localObject3).setLayout(new GridBagLayout());
/*      */     
/* 1872 */     JButton localJButton1 = new JButton();
/* 1873 */     ToolWindow.configureButton(localJButton1, "Add.Principal");
/* 1874 */     localJButton1
/* 1875 */       .addActionListener(new AddPrinButtonListener(this.tool, this.tw, this, paramBoolean));
/* 1876 */     this.tw.addNewComponent((Container)localObject3, localJButton1, 0, 0, 0, 1, 1, 100.0D, 0.0D, 2);
/*      */     
/*      */ 
/* 1879 */     localJButton1 = new JButton();
/* 1880 */     ToolWindow.configureButton(localJButton1, "Edit.Principal");
/* 1881 */     localJButton1.addActionListener(new EditPrinButtonListener(this.tool, this.tw, this, paramBoolean));
/*      */     
/* 1883 */     this.tw.addNewComponent((Container)localObject3, localJButton1, 1, 1, 0, 1, 1, 100.0D, 0.0D, 2);
/*      */     
/*      */ 
/* 1886 */     localJButton1 = new JButton();
/* 1887 */     ToolWindow.configureButton(localJButton1, "Remove.Principal");
/* 1888 */     localJButton1.addActionListener(new RemovePrinButtonListener(this.tool, this.tw, this, paramBoolean));
/*      */     
/* 1890 */     this.tw.addNewComponent((Container)localObject3, localJButton1, 2, 2, 0, 1, 1, 100.0D, 0.0D, 2);
/*      */     
/*      */ 
/* 1893 */     this.tw.addNewComponent(this, (JComponent)localObject3, 4, 1, 2, 1, 1, 0.0D, 0.0D, 2, ToolWindow.LITE_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1898 */     localObject1 = new JLabel();
/* 1899 */     this.tw.addNewComponent(this, (JComponent)localObject1, 5, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
/*      */     
/*      */ 
/* 1902 */     Object localObject5 = new JScrollPane(localTaggedList1);
/* 1903 */     ToolWindow.configureLabelFor((JLabel)localObject1, (JComponent)localObject5, "Principals.");
/* 1904 */     this.tw.addNewComponent(this, (JComponent)localObject5, 6, 1, 3, 3, 1, 0.0D, localTaggedList1
/* 1905 */       .getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 1909 */     localObject3 = new JPanel();
/* 1910 */     ((JPanel)localObject3).setLayout(new GridBagLayout());
/*      */     
/* 1912 */     localJButton1 = new JButton();
/* 1913 */     ToolWindow.configureButton(localJButton1, ".Add.Permission");
/* 1914 */     localJButton1.addActionListener(new AddPermButtonListener(this.tool, this.tw, this, paramBoolean));
/*      */     
/* 1916 */     this.tw.addNewComponent((Container)localObject3, localJButton1, 0, 0, 0, 1, 1, 100.0D, 0.0D, 2);
/*      */     
/*      */ 
/* 1919 */     localJButton1 = new JButton();
/* 1920 */     ToolWindow.configureButton(localJButton1, ".Edit.Permission");
/* 1921 */     localJButton1.addActionListener(new EditPermButtonListener(this.tool, this.tw, this, paramBoolean));
/*      */     
/* 1923 */     this.tw.addNewComponent((Container)localObject3, localJButton1, 1, 1, 0, 1, 1, 100.0D, 0.0D, 2);
/*      */     
/*      */ 
/*      */ 
/* 1927 */     localJButton1 = new JButton();
/* 1928 */     ToolWindow.configureButton(localJButton1, "Remove.Permission");
/* 1929 */     localJButton1.addActionListener(new RemovePermButtonListener(this.tool, this.tw, this, paramBoolean));
/*      */     
/* 1931 */     this.tw.addNewComponent((Container)localObject3, localJButton1, 2, 2, 0, 1, 1, 100.0D, 0.0D, 2);
/*      */     
/*      */ 
/* 1934 */     this.tw.addNewComponent(this, (JComponent)localObject3, 7, 0, 4, 2, 1, 0.0D, 0.0D, 2, ToolWindow.LITE_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1939 */     localObject5 = new JScrollPane(localTaggedList2);
/* 1940 */     this.tw.addNewComponent(this, (JComponent)localObject5, 8, 0, 5, 3, 1, 0.0D, localTaggedList2
/* 1941 */       .getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1946 */     localObject3 = new JPanel();
/* 1947 */     ((JPanel)localObject3).setLayout(new GridBagLayout());
/*      */     
/*      */ 
/* 1950 */     Object localObject6 = new JButton(PolicyTool.getMessage("Done"));
/* 1951 */     ((JButton)localObject6)
/* 1952 */       .addActionListener(new AddEntryDoneButtonListener(this.tool, this.tw, this, paramBoolean));
/* 1953 */     this.tw.addNewComponent((Container)localObject3, (JComponent)localObject6, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1958 */     JButton localJButton2 = new JButton(PolicyTool.getMessage("Cancel"));
/* 1959 */     CancelButtonListener localCancelButtonListener = new CancelButtonListener(this);
/* 1960 */     localJButton2.addActionListener(localCancelButtonListener);
/* 1961 */     this.tw.addNewComponent((Container)localObject3, localJButton2, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1966 */     this.tw.addNewComponent(this, (JComponent)localObject3, 9, 0, 6, 2, 1, 0.0D, 0.0D, 3);
/*      */     
/*      */ 
/* 1969 */     getRootPane().setDefaultButton((JButton)localObject6);
/* 1970 */     getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
/*      */     
/* 1972 */     pack();
/* 1973 */     setLocationRelativeTo(this.tw);
/* 1974 */     setVisible(true);
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
/*      */   PolicyEntry getPolicyEntryFromDialog()
/*      */     throws InvalidParameterException, MalformedURLException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, CertificateException, IOException, Exception
/*      */   {
/* 1988 */     JTextField localJTextField = (JTextField)getComponent(1);
/* 1989 */     String str1 = null;
/* 1990 */     if (!localJTextField.getText().trim().equals("")) {
/* 1991 */       str1 = new String(localJTextField.getText().trim());
/*      */     }
/*      */     
/* 1994 */     localJTextField = (JTextField)getComponent(3);
/* 1995 */     String str2 = null;
/* 1996 */     if (!localJTextField.getText().trim().equals("")) {
/* 1997 */       str2 = new String(localJTextField.getText().trim());
/*      */     }
/*      */     
/* 2000 */     PolicyParser.GrantEntry localGrantEntry = new PolicyParser.GrantEntry(str2, str1);
/*      */     
/*      */ 
/*      */ 
/* 2004 */     LinkedList localLinkedList = new LinkedList();
/* 2005 */     TaggedList localTaggedList1 = (TaggedList)getComponent(6);
/* 2006 */     for (int i = 0; i < localTaggedList1.getModel().getSize(); i++) {
/* 2007 */       localLinkedList.add((PolicyParser.PrincipalEntry)localTaggedList1.getObject(i));
/*      */     }
/* 2009 */     localGrantEntry.principals = localLinkedList;
/*      */     
/*      */ 
/* 2012 */     Vector localVector = new Vector();
/* 2013 */     TaggedList localTaggedList2 = (TaggedList)getComponent(8);
/* 2014 */     for (int j = 0; j < localTaggedList2.getModel().getSize(); j++) {
/* 2015 */       localVector.addElement((PolicyParser.PermissionEntry)localTaggedList2.getObject(j));
/*      */     }
/* 2017 */     localGrantEntry.permissionEntries = localVector;
/*      */     
/*      */ 
/* 2020 */     PolicyEntry localPolicyEntry = new PolicyEntry(this.tool, localGrantEntry);
/*      */     
/* 2022 */     return localPolicyEntry;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void keyStoreDialog(int paramInt)
/*      */   {
/* 2031 */     Point localPoint = this.tw.getLocationOnScreen();
/*      */     
/* 2033 */     setLayout(new GridBagLayout());
/*      */     
/* 2035 */     if (paramInt == 0)
/*      */     {
/*      */ 
/* 2038 */       JLabel localJLabel = new JLabel();
/* 2039 */       this.tw.addNewComponent(this, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
/*      */       
/*      */ 
/* 2042 */       JTextField localJTextField = new JTextField(this.tool.getKeyStoreName(), 30);
/* 2043 */       ToolWindow.configureLabelFor(localJLabel, localJTextField, "KeyStore.URL.");
/* 2044 */       localJTextField.setPreferredSize(new Dimension(localJTextField.getPreferredSize().width, TEXTFIELD_HEIGHT));
/*      */       
/*      */ 
/* 2047 */       localJTextField.getAccessibleContext().setAccessibleName(
/* 2048 */         PolicyTool.getMessage("KeyStore.U.R.L."));
/* 2049 */       this.tw.addNewComponent(this, localJTextField, 1, 1, 0, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2054 */       localJLabel = new JLabel();
/* 2055 */       this.tw.addNewComponent(this, localJLabel, 2, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
/*      */       
/*      */ 
/* 2058 */       localJTextField = new JTextField(this.tool.getKeyStoreType(), 30);
/* 2059 */       ToolWindow.configureLabelFor(localJLabel, localJTextField, "KeyStore.Type.");
/* 2060 */       localJTextField.setPreferredSize(new Dimension(localJTextField.getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 2061 */       localJTextField.getAccessibleContext().setAccessibleName(
/* 2062 */         PolicyTool.getMessage("KeyStore.Type."));
/* 2063 */       this.tw.addNewComponent(this, localJTextField, 3, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2068 */       localJLabel = new JLabel();
/* 2069 */       this.tw.addNewComponent(this, localJLabel, 4, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
/*      */       
/*      */ 
/* 2072 */       localJTextField = new JTextField(this.tool.getKeyStoreProvider(), 30);
/* 2073 */       ToolWindow.configureLabelFor(localJLabel, localJTextField, "KeyStore.Provider.");
/* 2074 */       localJTextField.setPreferredSize(new Dimension(localJTextField.getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 2075 */       localJTextField.getAccessibleContext().setAccessibleName(
/* 2076 */         PolicyTool.getMessage("KeyStore.Provider."));
/* 2077 */       this.tw.addNewComponent(this, localJTextField, 5, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2082 */       localJLabel = new JLabel();
/* 2083 */       this.tw.addNewComponent(this, localJLabel, 6, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
/*      */       
/*      */ 
/* 2086 */       localJTextField = new JTextField(this.tool.getKeyStorePwdURL(), 30);
/* 2087 */       ToolWindow.configureLabelFor(localJLabel, localJTextField, "KeyStore.Password.URL.");
/* 2088 */       localJTextField.setPreferredSize(new Dimension(localJTextField.getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 2089 */       localJTextField.getAccessibleContext().setAccessibleName(
/* 2090 */         PolicyTool.getMessage("KeyStore.Password.U.R.L."));
/* 2091 */       this.tw.addNewComponent(this, localJTextField, 7, 1, 3, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2096 */       JButton localJButton1 = new JButton(PolicyTool.getMessage("OK"));
/* 2097 */       localJButton1
/* 2098 */         .addActionListener(new ChangeKeyStoreOKButtonListener(this.tool, this.tw, this));
/* 2099 */       this.tw.addNewComponent(this, localJButton1, 8, 0, 4, 1, 1, 0.0D, 0.0D, 3);
/*      */       
/*      */ 
/*      */ 
/* 2103 */       JButton localJButton2 = new JButton(PolicyTool.getMessage("Cancel"));
/* 2104 */       CancelButtonListener localCancelButtonListener = new CancelButtonListener(this);
/* 2105 */       localJButton2.addActionListener(localCancelButtonListener);
/* 2106 */       this.tw.addNewComponent(this, localJButton2, 9, 1, 4, 1, 1, 0.0D, 0.0D, 3);
/*      */       
/*      */ 
/* 2109 */       getRootPane().setDefaultButton(localJButton1);
/* 2110 */       getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
/*      */     }
/*      */     
/* 2113 */     pack();
/* 2114 */     setLocationRelativeTo(this.tw);
/* 2115 */     setVisible(true);
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
/*      */   void displayPrincipalDialog(boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 2129 */     PolicyParser.PrincipalEntry localPrincipalEntry = null;
/*      */     
/*      */ 
/* 2132 */     TaggedList localTaggedList = (TaggedList)getComponent(6);
/* 2133 */     int i = localTaggedList.getSelectedIndex();
/*      */     
/* 2135 */     if (paramBoolean2) {
/* 2136 */       localPrincipalEntry = (PolicyParser.PrincipalEntry)localTaggedList.getObject(i);
/*      */     }
/*      */     
/*      */ 
/* 2140 */     ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Principals"), this.tool, this.tw, true);
/* 2141 */     localToolDialog.addWindowListener(new ChildWindowListener(localToolDialog));
/*      */     
/*      */ 
/* 2144 */     Point localPoint = getLocationOnScreen();
/*      */     
/* 2146 */     localToolDialog.setLayout(new GridBagLayout());
/* 2147 */     localToolDialog.setResizable(true);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2152 */     JLabel localJLabel = paramBoolean2 ? new JLabel(PolicyTool.getMessage(".Edit.Principal.")) : new JLabel(PolicyTool.getMessage(".Add.New.Principal."));
/* 2153 */     this.tw.addNewComponent(localToolDialog, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.TOP_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2158 */     JComboBox localJComboBox = new JComboBox();
/* 2159 */     localJComboBox.addItem(PRIN_TYPE);
/* 2160 */     localJComboBox.getAccessibleContext().setAccessibleName(PRIN_TYPE);
/* 2161 */     for (int j = 0; j < PRIN_ARRAY.size(); j++) {
/* 2162 */       localObject2 = (Prin)PRIN_ARRAY.get(j);
/* 2163 */       localJComboBox.addItem(((Prin)localObject2).CLASS);
/*      */     }
/*      */     
/* 2166 */     if (paramBoolean2)
/*      */     {
/* 2168 */       if ("WILDCARD_PRINCIPAL_CLASS".equals(localPrincipalEntry.getPrincipalClass())) {
/* 2169 */         localJComboBox.setSelectedItem(PRIN_TYPE);
/*      */       } else {
/* 2171 */         localObject1 = getPrin(localPrincipalEntry.getPrincipalClass(), true);
/* 2172 */         if (localObject1 != null) {
/* 2173 */           localJComboBox.setSelectedItem(((Prin)localObject1).CLASS);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2178 */     localJComboBox.addItemListener(new PrincipalTypeMenuListener(localToolDialog));
/*      */     
/* 2180 */     this.tw.addNewComponent(localToolDialog, localJComboBox, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2187 */     Object localObject1 = paramBoolean2 ? new JTextField(localPrincipalEntry.getDisplayClass(), 30) : new JTextField(30);
/*      */     
/* 2189 */     ((JTextField)localObject1).setPreferredSize(new Dimension(((JTextField)localObject1).getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 2190 */     ((JTextField)localObject1).getAccessibleContext().setAccessibleName(PRIN_TYPE);
/* 2191 */     this.tw.addNewComponent(localToolDialog, (JComponent)localObject1, 2, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2196 */     localJLabel = new JLabel(PRIN_NAME);
/*      */     
/* 2198 */     localObject1 = paramBoolean2 ? new JTextField(localPrincipalEntry.getDisplayName(), 40) : new JTextField(40);
/*      */     
/* 2200 */     ((JTextField)localObject1).setPreferredSize(new Dimension(((JTextField)localObject1).getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 2201 */     ((JTextField)localObject1).getAccessibleContext().setAccessibleName(PRIN_NAME);
/*      */     
/* 2203 */     this.tw.addNewComponent(localToolDialog, localJLabel, 3, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_PADDING);
/*      */     
/*      */ 
/* 2206 */     this.tw.addNewComponent(localToolDialog, (JComponent)localObject1, 4, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2211 */     Object localObject2 = new JButton(PolicyTool.getMessage("OK"));
/* 2212 */     ((JButton)localObject2).addActionListener(new NewPolicyPrinOKButtonListener(this.tool, this.tw, this, localToolDialog, paramBoolean2));
/*      */     
/*      */ 
/* 2215 */     this.tw.addNewComponent(localToolDialog, (JComponent)localObject2, 5, 0, 3, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 2219 */     JButton localJButton = new JButton(PolicyTool.getMessage("Cancel"));
/* 2220 */     CancelButtonListener localCancelButtonListener = new CancelButtonListener(localToolDialog);
/* 2221 */     localJButton.addActionListener(localCancelButtonListener);
/* 2222 */     this.tw.addNewComponent(localToolDialog, localJButton, 6, 1, 3, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 2226 */     localToolDialog.getRootPane().setDefaultButton((JButton)localObject2);
/* 2227 */     localToolDialog.getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
/*      */     
/* 2229 */     localToolDialog.pack();
/* 2230 */     localToolDialog.setLocationRelativeTo(this.tw);
/* 2231 */     localToolDialog.setVisible(true);
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
/*      */   void displayPermissionDialog(boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 2245 */     PolicyParser.PermissionEntry localPermissionEntry = null;
/*      */     
/*      */ 
/* 2248 */     TaggedList localTaggedList = (TaggedList)getComponent(8);
/* 2249 */     int i = localTaggedList.getSelectedIndex();
/*      */     
/* 2251 */     if (paramBoolean2) {
/* 2252 */       localPermissionEntry = (PolicyParser.PermissionEntry)localTaggedList.getObject(i);
/*      */     }
/*      */     
/*      */ 
/* 2256 */     ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Permissions"), this.tool, this.tw, true);
/* 2257 */     localToolDialog.addWindowListener(new ChildWindowListener(localToolDialog));
/*      */     
/*      */ 
/* 2260 */     Point localPoint = getLocationOnScreen();
/*      */     
/* 2262 */     localToolDialog.setLayout(new GridBagLayout());
/* 2263 */     localToolDialog.setResizable(true);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2268 */     JLabel localJLabel = paramBoolean2 ? new JLabel(PolicyTool.getMessage(".Edit.Permission.")) : new JLabel(PolicyTool.getMessage(".Add.New.Permission."));
/* 2269 */     this.tw.addNewComponent(localToolDialog, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.TOP_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2274 */     JComboBox localJComboBox = new JComboBox();
/* 2275 */     localJComboBox.addItem(PERM);
/* 2276 */     localJComboBox.getAccessibleContext().setAccessibleName(PERM);
/* 2277 */     for (int j = 0; j < PERM_ARRAY.size(); j++) {
/* 2278 */       localObject = (Perm)PERM_ARRAY.get(j);
/* 2279 */       localJComboBox.addItem(((Perm)localObject).CLASS);
/*      */     }
/* 2281 */     this.tw.addNewComponent(localToolDialog, localJComboBox, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2287 */     JTextField localJTextField = paramBoolean2 ? new JTextField(localPermissionEntry.permission, 30) : new JTextField(30);
/* 2288 */     localJTextField.setPreferredSize(new Dimension(localJTextField.getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 2289 */     localJTextField.getAccessibleContext().setAccessibleName(PERM);
/* 2290 */     if (paramBoolean2) {
/* 2291 */       localObject = getPerm(localPermissionEntry.permission, true);
/* 2292 */       if (localObject != null) {
/* 2293 */         localJComboBox.setSelectedItem(((Perm)localObject).CLASS);
/*      */       }
/*      */     }
/* 2296 */     this.tw.addNewComponent(localToolDialog, localJTextField, 2, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
/*      */     
/*      */ 
/* 2299 */     localJComboBox.addItemListener(new PermissionMenuListener(localToolDialog));
/*      */     
/*      */ 
/* 2302 */     localJComboBox = new JComboBox();
/* 2303 */     localJComboBox.addItem(PERM_NAME);
/* 2304 */     localJComboBox.getAccessibleContext().setAccessibleName(PERM_NAME);
/* 2305 */     localJTextField = paramBoolean2 ? new JTextField(localPermissionEntry.name, 40) : new JTextField(40);
/* 2306 */     localJTextField.setPreferredSize(new Dimension(localJTextField.getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 2307 */     localJTextField.getAccessibleContext().setAccessibleName(PERM_NAME);
/* 2308 */     if (paramBoolean2) {
/* 2309 */       setPermissionNames(getPerm(localPermissionEntry.permission, true), localJComboBox, localJTextField);
/*      */     }
/* 2311 */     this.tw.addNewComponent(localToolDialog, localJComboBox, 3, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
/*      */     
/*      */ 
/* 2314 */     this.tw.addNewComponent(localToolDialog, localJTextField, 4, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
/*      */     
/*      */ 
/* 2317 */     localJComboBox.addItemListener(new PermissionNameMenuListener(localToolDialog));
/*      */     
/*      */ 
/* 2320 */     localJComboBox = new JComboBox();
/* 2321 */     localJComboBox.addItem(PERM_ACTIONS);
/* 2322 */     localJComboBox.getAccessibleContext().setAccessibleName(PERM_ACTIONS);
/* 2323 */     localJTextField = paramBoolean2 ? new JTextField(localPermissionEntry.action, 40) : new JTextField(40);
/* 2324 */     localJTextField.setPreferredSize(new Dimension(localJTextField.getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 2325 */     localJTextField.getAccessibleContext().setAccessibleName(PERM_ACTIONS);
/* 2326 */     if (paramBoolean2) {
/* 2327 */       setPermissionActions(getPerm(localPermissionEntry.permission, true), localJComboBox, localJTextField);
/*      */     }
/* 2329 */     this.tw.addNewComponent(localToolDialog, localJComboBox, 5, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
/*      */     
/*      */ 
/* 2332 */     this.tw.addNewComponent(localToolDialog, localJTextField, 6, 1, 3, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
/*      */     
/*      */ 
/* 2335 */     localJComboBox.addItemListener(new PermissionActionsMenuListener(localToolDialog));
/*      */     
/*      */ 
/* 2338 */     localJLabel = new JLabel(PolicyTool.getMessage("Signed.By."));
/* 2339 */     this.tw.addNewComponent(localToolDialog, localJLabel, 7, 0, 4, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
/*      */     
/*      */ 
/* 2342 */     localJTextField = paramBoolean2 ? new JTextField(localPermissionEntry.signedBy, 40) : new JTextField(40);
/* 2343 */     localJTextField.setPreferredSize(new Dimension(localJTextField.getPreferredSize().width, TEXTFIELD_HEIGHT));
/* 2344 */     localJTextField.getAccessibleContext().setAccessibleName(
/* 2345 */       PolicyTool.getMessage("Signed.By."));
/* 2346 */     this.tw.addNewComponent(localToolDialog, localJTextField, 8, 1, 4, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2351 */     Object localObject = new JButton(PolicyTool.getMessage("OK"));
/* 2352 */     ((JButton)localObject).addActionListener(new NewPolicyPermOKButtonListener(this.tool, this.tw, this, localToolDialog, paramBoolean2));
/*      */     
/*      */ 
/* 2355 */     this.tw.addNewComponent(localToolDialog, (JComponent)localObject, 9, 0, 5, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2360 */     JButton localJButton = new JButton(PolicyTool.getMessage("Cancel"));
/* 2361 */     CancelButtonListener localCancelButtonListener = new CancelButtonListener(localToolDialog);
/* 2362 */     localJButton.addActionListener(localCancelButtonListener);
/* 2363 */     this.tw.addNewComponent(localToolDialog, localJButton, 10, 1, 5, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 2367 */     localToolDialog.getRootPane().setDefaultButton((JButton)localObject);
/* 2368 */     localToolDialog.getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
/*      */     
/* 2370 */     localToolDialog.pack();
/* 2371 */     localToolDialog.setLocationRelativeTo(this.tw);
/* 2372 */     localToolDialog.setVisible(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   PolicyParser.PrincipalEntry getPrinFromDialog()
/*      */     throws Exception
/*      */   {
/* 2380 */     JTextField localJTextField = (JTextField)getComponent(2);
/* 2381 */     String str1 = new String(localJTextField.getText().trim());
/* 2382 */     localJTextField = (JTextField)getComponent(4);
/* 2383 */     String str2 = new String(localJTextField.getText().trim());
/* 2384 */     if (str1.equals("*")) {
/* 2385 */       str1 = "WILDCARD_PRINCIPAL_CLASS";
/*      */     }
/* 2387 */     if (str2.equals("*")) {
/* 2388 */       str2 = "WILDCARD_PRINCIPAL_NAME";
/*      */     }
/*      */     
/* 2391 */     Object localObject = null;
/*      */     
/* 2393 */     if ((str1.equals("WILDCARD_PRINCIPAL_CLASS")) && 
/* 2394 */       (!str2.equals("WILDCARD_PRINCIPAL_NAME")))
/*      */     {
/* 2396 */       throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name")); }
/* 2397 */     if (str2.equals(""))
/*      */     {
/* 2399 */       throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.without.a.Name")); }
/* 2400 */     if (str1.equals(""))
/*      */     {
/*      */ 
/* 2403 */       str1 = "PolicyParser.REPLACE_NAME";
/* 2404 */       this.tool.warnings.addElement("Warning: Principal name '" + str2 + "' specified without a Principal class.\n\t'" + str2 + "' will be interpreted as a key store alias.\n\tThe final principal class will be " + "javax.security.auth.x500.X500Principal" + ".\n\tThe final principal name will be determined by the following:\n\n\tIf the key store entry identified by '" + str2 + "'\n\tis a key entry, then the principal name will be\n\tthe subject distinguished name from the first\n\tcertificate in the entry's certificate chain.\n\n\tIf the key store entry identified by '" + str2 + "'\n\tis a trusted certificate entry, then the\n\tprincipal name will be the subject distinguished\n\tname from the trusted public key certificate.");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2425 */       this.tw.displayStatusDialog(this, "'" + str2 + "' will be interpreted as a key store alias.  View Warning Log for details.");
/*      */     }
/*      */     
/*      */ 
/* 2429 */     return new PolicyParser.PrincipalEntry(str1, str2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   PolicyParser.PermissionEntry getPermFromDialog()
/*      */   {
/* 2438 */     JTextField localJTextField = (JTextField)getComponent(2);
/* 2439 */     String str1 = new String(localJTextField.getText().trim());
/* 2440 */     localJTextField = (JTextField)getComponent(4);
/* 2441 */     String str2 = null;
/* 2442 */     if (!localJTextField.getText().trim().equals(""))
/* 2443 */       str2 = new String(localJTextField.getText().trim());
/* 2444 */     if ((str1.equals("")) || (
/* 2445 */       (!str1.equals("java.security.AllPermission")) && (str2 == null)))
/*      */     {
/* 2447 */       throw new InvalidParameterException(PolicyTool.getMessage("Permission.and.Target.Name.must.have.a.value"));
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
/* 2460 */     if ((str1.equals("java.io.FilePermission")) && (str2.lastIndexOf("\\\\") > 0)) {
/* 2461 */       int i = this.tw.displayYesNoDialog(this, 
/* 2462 */         PolicyTool.getMessage("Warning"), 
/* 2463 */         PolicyTool.getMessage("Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes"), 
/*      */         
/* 2465 */         PolicyTool.getMessage("Retain"), 
/* 2466 */         PolicyTool.getMessage("Edit"));
/*      */       
/* 2468 */       if (i != 89)
/*      */       {
/* 2470 */         throw new NoDisplayException();
/*      */       }
/*      */     }
/*      */     
/* 2474 */     localJTextField = (JTextField)getComponent(6);
/* 2475 */     String str3 = null;
/* 2476 */     if (!localJTextField.getText().trim().equals("")) {
/* 2477 */       str3 = new String(localJTextField.getText().trim());
/*      */     }
/*      */     
/* 2480 */     localJTextField = (JTextField)getComponent(8);
/* 2481 */     String str4 = null;
/* 2482 */     if (!localJTextField.getText().trim().equals("")) {
/* 2483 */       str4 = new String(localJTextField.getText().trim());
/*      */     }
/* 2485 */     PolicyParser.PermissionEntry localPermissionEntry = new PolicyParser.PermissionEntry(str1, str2, str3);
/*      */     
/* 2487 */     localPermissionEntry.signedBy = str4;
/*      */     
/*      */ 
/* 2490 */     if (str4 != null) {
/* 2491 */       String[] arrayOfString = this.tool.parseSigners(localPermissionEntry.signedBy);
/* 2492 */       for (int j = 0; j < arrayOfString.length; j++) {
/*      */         try {
/* 2494 */           PublicKey localPublicKey = this.tool.getPublicKeyAlias(arrayOfString[j]);
/* 2495 */           if (localPublicKey == null)
/*      */           {
/*      */ 
/* 2498 */             MessageFormat localMessageFormat = new MessageFormat(PolicyTool.getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
/* 2499 */             Object[] arrayOfObject = { arrayOfString[j] };
/* 2500 */             this.tool.warnings.addElement(localMessageFormat.format(arrayOfObject));
/* 2501 */             this.tw.displayStatusDialog(this, localMessageFormat.format(arrayOfObject));
/*      */           }
/*      */         } catch (Exception localException) {
/* 2504 */           this.tw.displayErrorDialog(this, localException);
/*      */         }
/*      */       }
/*      */     }
/* 2508 */     return localPermissionEntry;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void displayConfirmRemovePolicyEntry()
/*      */   {
/* 2517 */     JList localJList = (JList)this.tw.getComponent(3);
/* 2518 */     int i = localJList.getSelectedIndex();
/* 2519 */     PolicyEntry[] arrayOfPolicyEntry = this.tool.getEntry();
/*      */     
/*      */ 
/* 2522 */     Point localPoint = this.tw.getLocationOnScreen();
/*      */     
/* 2524 */     setLayout(new GridBagLayout());
/*      */     
/*      */ 
/*      */ 
/* 2528 */     JLabel localJLabel = new JLabel(PolicyTool.getMessage("Remove.this.Policy.Entry."));
/* 2529 */     this.tw.addNewComponent(this, localJLabel, 0, 0, 0, 2, 1, 0.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2534 */     localJLabel = new JLabel(arrayOfPolicyEntry[i].codebaseToString());
/* 2535 */     this.tw.addNewComponent(this, localJLabel, 1, 0, 1, 2, 1, 0.0D, 0.0D, 1);
/*      */     
/* 2537 */     localJLabel = new JLabel(arrayOfPolicyEntry[i].principalsToString().trim());
/* 2538 */     this.tw.addNewComponent(this, localJLabel, 2, 0, 2, 2, 1, 0.0D, 0.0D, 1);
/*      */     
/*      */ 
/* 2541 */     Vector localVector = arrayOfPolicyEntry[i].getGrantEntry().permissionEntries;
/* 2542 */     for (int j = 0; j < localVector.size(); j++) {
/* 2543 */       localObject1 = (PolicyParser.PermissionEntry)localVector.elementAt(j);
/* 2544 */       localObject2 = PermissionEntryToUserFriendlyString((PolicyParser.PermissionEntry)localObject1);
/* 2545 */       localJLabel = new JLabel("    " + (String)localObject2);
/* 2546 */       if (j == localVector.size() - 1) {
/* 2547 */         this.tw.addNewComponent(this, localJLabel, 3 + j, 1, 3 + j, 1, 1, 0.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2552 */         this.tw.addNewComponent(this, localJLabel, 3 + j, 1, 3 + j, 1, 1, 0.0D, 0.0D, 1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2560 */     JPanel localJPanel = new JPanel();
/* 2561 */     localJPanel.setLayout(new GridBagLayout());
/*      */     
/*      */ 
/* 2564 */     Object localObject1 = new JButton(PolicyTool.getMessage("OK"));
/* 2565 */     ((JButton)localObject1)
/* 2566 */       .addActionListener(new ConfirmRemovePolicyEntryOKButtonListener(this.tool, this.tw, this));
/* 2567 */     this.tw.addNewComponent(localJPanel, (JComponent)localObject1, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2572 */     Object localObject2 = new JButton(PolicyTool.getMessage("Cancel"));
/* 2573 */     CancelButtonListener localCancelButtonListener = new CancelButtonListener(this);
/* 2574 */     ((JButton)localObject2).addActionListener(localCancelButtonListener);
/* 2575 */     this.tw.addNewComponent(localJPanel, (JComponent)localObject2, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
/*      */     
/*      */ 
/*      */ 
/* 2579 */     this.tw.addNewComponent(this, localJPanel, 3 + localVector.size(), 0, 3 + localVector
/* 2580 */       .size(), 2, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
/*      */     
/*      */ 
/* 2583 */     getRootPane().setDefaultButton((JButton)localObject1);
/* 2584 */     getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
/*      */     
/* 2586 */     pack();
/* 2587 */     setLocationRelativeTo(this.tw);
/* 2588 */     setVisible(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void displaySaveAsDialog(int paramInt)
/*      */   {
/* 2598 */     FileDialog localFileDialog = new FileDialog(this.tw, PolicyTool.getMessage("Save.As"), 1);
/* 2599 */     localFileDialog.addWindowListener(new WindowAdapter() {
/*      */       public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
/* 2601 */         paramAnonymousWindowEvent.getWindow().setVisible(false);
/*      */       }
/* 2603 */     });
/* 2604 */     localFileDialog.setVisible(true);
/*      */     
/*      */ 
/* 2607 */     if ((localFileDialog.getFile() == null) || 
/* 2608 */       (localFileDialog.getFile().equals(""))) {
/* 2609 */       return;
/*      */     }
/*      */     
/* 2612 */     File localFile = new File(localFileDialog.getDirectory(), localFileDialog.getFile());
/* 2613 */     String str = localFile.getPath();
/* 2614 */     localFileDialog.dispose();
/*      */     
/*      */     try
/*      */     {
/* 2618 */       this.tool.savePolicy(str);
/*      */       
/*      */ 
/*      */ 
/* 2622 */       MessageFormat localMessageFormat = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
/* 2623 */       Object[] arrayOfObject = { str };
/* 2624 */       this.tw.displayStatusDialog(null, localMessageFormat.format(arrayOfObject));
/*      */       
/*      */ 
/*      */ 
/* 2628 */       JTextField localJTextField = (JTextField)this.tw.getComponent(1);
/* 2629 */       localJTextField.setText(str);
/* 2630 */       this.tw.setVisible(true);
/*      */       
/*      */ 
/*      */ 
/* 2634 */       userSaveContinue(this.tool, this.tw, this, paramInt);
/*      */     }
/*      */     catch (FileNotFoundException localFileNotFoundException) {
/* 2637 */       if ((str == null) || (str.equals(""))) {
/* 2638 */         this.tw.displayErrorDialog(null, new FileNotFoundException(
/* 2639 */           PolicyTool.getMessage("null.filename")));
/*      */       } else {
/* 2641 */         this.tw.displayErrorDialog(null, localFileNotFoundException);
/*      */       }
/*      */     } catch (Exception localException) {
/* 2644 */       this.tw.displayErrorDialog(null, localException);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void displayUserSave(int paramInt)
/*      */   {
/* 2653 */     if (this.tool.modified == true)
/*      */     {
/*      */ 
/* 2656 */       Point localPoint = this.tw.getLocationOnScreen();
/*      */       
/* 2658 */       setLayout(new GridBagLayout());
/*      */       
/*      */ 
/* 2661 */       JLabel localJLabel = new JLabel(PolicyTool.getMessage("Save.changes."));
/* 2662 */       this.tw.addNewComponent(this, localJLabel, 0, 0, 0, 3, 1, 0.0D, 0.0D, 1, ToolWindow.L_TOP_BOTTOM_PADDING);
/*      */       
/*      */ 
/*      */ 
/* 2666 */       JPanel localJPanel = new JPanel();
/* 2667 */       localJPanel.setLayout(new GridBagLayout());
/*      */       
/* 2669 */       JButton localJButton1 = new JButton();
/* 2670 */       ToolWindow.configureButton(localJButton1, "Yes");
/* 2671 */       localJButton1
/* 2672 */         .addActionListener(new UserSaveYesButtonListener(this, this.tool, this.tw, paramInt));
/* 2673 */       this.tw.addNewComponent(localJPanel, localJButton1, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
/*      */       
/*      */ 
/*      */ 
/* 2677 */       JButton localJButton2 = new JButton();
/* 2678 */       ToolWindow.configureButton(localJButton2, "No");
/* 2679 */       localJButton2
/* 2680 */         .addActionListener(new UserSaveNoButtonListener(this, this.tool, this.tw, paramInt));
/* 2681 */       this.tw.addNewComponent(localJPanel, localJButton2, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
/*      */       
/*      */ 
/*      */ 
/* 2685 */       JButton localJButton3 = new JButton();
/* 2686 */       ToolWindow.configureButton(localJButton3, "Cancel");
/* 2687 */       CancelButtonListener localCancelButtonListener = new CancelButtonListener(this);
/* 2688 */       localJButton3.addActionListener(localCancelButtonListener);
/* 2689 */       this.tw.addNewComponent(localJPanel, localJButton3, 2, 2, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2694 */       this.tw.addNewComponent(this, localJPanel, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1);
/*      */       
/*      */ 
/* 2697 */       getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
/*      */       
/* 2699 */       pack();
/* 2700 */       setLocationRelativeTo(this.tw);
/* 2701 */       setVisible(true);
/*      */     }
/*      */     else {
/* 2704 */       userSaveContinue(this.tool, this.tw, this, paramInt);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void userSaveContinue(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, int paramInt)
/*      */   {
/*      */     JList localJList;
/*      */     
/*      */ 
/*      */ 
/*      */     JTextField localJTextField;
/*      */     
/*      */ 
/* 2720 */     switch (paramInt)
/*      */     {
/*      */     case 1: 
/* 2723 */       paramToolWindow.setVisible(false);
/* 2724 */       paramToolWindow.dispose();
/* 2725 */       System.exit(0);
/*      */     
/*      */     case 2: 
/*      */       try
/*      */       {
/* 2730 */         paramPolicyTool.openPolicy(null);
/*      */       } catch (Exception localException1) {
/* 2732 */         paramPolicyTool.modified = false;
/* 2733 */         paramToolWindow.displayErrorDialog(null, localException1);
/*      */       }
/*      */       
/*      */ 
/* 2737 */       localJList = new JList(new DefaultListModel());
/* 2738 */       localJList.setVisibleRowCount(15);
/* 2739 */       localJList.setSelectionMode(0);
/* 2740 */       localJList.addMouseListener(new PolicyListListener(paramPolicyTool, paramToolWindow));
/* 2741 */       paramToolWindow.replacePolicyList(localJList);
/*      */       
/*      */ 
/* 2744 */       localJTextField = (JTextField)paramToolWindow.getComponent(1);
/*      */       
/* 2746 */       localJTextField.setText("");
/* 2747 */       paramToolWindow.setVisible(true);
/* 2748 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case 3: 
/* 2754 */       FileDialog localFileDialog = new FileDialog(paramToolWindow, PolicyTool.getMessage("Open"), 0);
/* 2755 */       localFileDialog.addWindowListener(new WindowAdapter() {
/*      */         public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
/* 2757 */           paramAnonymousWindowEvent.getWindow().setVisible(false);
/*      */         }
/* 2759 */       });
/* 2760 */       localFileDialog.setVisible(true);
/*      */       
/*      */ 
/* 2763 */       if ((localFileDialog.getFile() == null) || 
/* 2764 */         (localFileDialog.getFile().equals(""))) {
/* 2765 */         return;
/*      */       }
/*      */       
/* 2768 */       String str = new File(localFileDialog.getDirectory(), localFileDialog.getFile()).getPath();
/*      */       
/*      */       try
/*      */       {
/* 2772 */         paramPolicyTool.openPolicy(str);
/*      */         
/*      */ 
/* 2775 */         DefaultListModel localDefaultListModel = new DefaultListModel();
/* 2776 */         localJList = new JList(localDefaultListModel);
/* 2777 */         localJList.setVisibleRowCount(15);
/* 2778 */         localJList.setSelectionMode(0);
/* 2779 */         localJList.addMouseListener(new PolicyListListener(paramPolicyTool, paramToolWindow));
/* 2780 */         localObject = paramPolicyTool.getEntry();
/* 2781 */         if (localObject != null) {
/* 2782 */           for (int i = 0; i < localObject.length; i++) {
/* 2783 */             localDefaultListModel.addElement(localObject[i].headerToString());
/*      */           }
/*      */         }
/* 2786 */         paramToolWindow.replacePolicyList(localJList);
/* 2787 */         paramPolicyTool.modified = false;
/*      */         
/*      */ 
/* 2790 */         localJTextField = (JTextField)paramToolWindow.getComponent(1);
/*      */         
/* 2792 */         localJTextField.setText(str);
/* 2793 */         paramToolWindow.setVisible(true);
/*      */         
/*      */ 
/* 2796 */         if (paramPolicyTool.newWarning == true) {
/* 2797 */           paramToolWindow.displayStatusDialog(null, 
/* 2798 */             PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information."));
/*      */         }
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/* 2803 */         localJList = new JList(new DefaultListModel());
/* 2804 */         localJList.setVisibleRowCount(15);
/* 2805 */         localJList.setSelectionMode(0);
/* 2806 */         localJList.addMouseListener(new PolicyListListener(paramPolicyTool, paramToolWindow));
/* 2807 */         paramToolWindow.replacePolicyList(localJList);
/* 2808 */         paramPolicyTool.setPolicyFileName(null);
/* 2809 */         paramPolicyTool.modified = false;
/*      */         
/*      */ 
/* 2812 */         localJTextField = (JTextField)paramToolWindow.getComponent(1);
/*      */         
/* 2814 */         localJTextField.setText("");
/* 2815 */         paramToolWindow.setVisible(true);
/*      */         
/*      */ 
/*      */ 
/* 2819 */         Object localObject = new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString."));
/* 2820 */         Object[] arrayOfObject = { str, localException2.toString() };
/* 2821 */         paramToolWindow.displayErrorDialog(null, ((MessageFormat)localObject).format(arrayOfObject));
/*      */       }
/*      */     }
/*      */     
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
/*      */   void setPermissionNames(Perm paramPerm, JComboBox paramJComboBox, JTextField paramJTextField)
/*      */   {
/* 2839 */     paramJComboBox.removeAllItems();
/* 2840 */     paramJComboBox.addItem(PERM_NAME);
/*      */     
/* 2842 */     if (paramPerm == null)
/*      */     {
/* 2844 */       paramJTextField.setEditable(true);
/* 2845 */     } else if (paramPerm.TARGETS == null)
/*      */     {
/* 2847 */       paramJTextField.setEditable(false);
/*      */     }
/*      */     else {
/* 2850 */       paramJTextField.setEditable(true);
/* 2851 */       for (int i = 0; i < paramPerm.TARGETS.length; i++) {
/* 2852 */         paramJComboBox.addItem(paramPerm.TARGETS[i]);
/*      */       }
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
/*      */   void setPermissionActions(Perm paramPerm, JComboBox paramJComboBox, JTextField paramJTextField)
/*      */   {
/* 2869 */     paramJComboBox.removeAllItems();
/* 2870 */     paramJComboBox.addItem(PERM_ACTIONS);
/*      */     
/* 2872 */     if (paramPerm == null)
/*      */     {
/* 2874 */       paramJTextField.setEditable(true);
/* 2875 */     } else if (paramPerm.ACTIONS == null)
/*      */     {
/* 2877 */       paramJTextField.setEditable(false);
/*      */     }
/*      */     else {
/* 2880 */       paramJTextField.setEditable(true);
/* 2881 */       for (int i = 0; i < paramPerm.ACTIONS.length; i++) {
/* 2882 */         paramJComboBox.addItem(paramPerm.ACTIONS[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   static String PermissionEntryToUserFriendlyString(PolicyParser.PermissionEntry paramPermissionEntry) {
/* 2888 */     String str = paramPermissionEntry.permission;
/* 2889 */     if (paramPermissionEntry.name != null) {
/* 2890 */       str = str + " " + paramPermissionEntry.name;
/*      */     }
/* 2892 */     if (paramPermissionEntry.action != null) {
/* 2893 */       str = str + ", \"" + paramPermissionEntry.action + "\"";
/*      */     }
/* 2895 */     if (paramPermissionEntry.signedBy != null) {
/* 2896 */       str = str + ", signedBy " + paramPermissionEntry.signedBy;
/*      */     }
/* 2898 */     return str;
/*      */   }
/*      */   
/*      */   static String PrincipalEntryToUserFriendlyString(PolicyParser.PrincipalEntry paramPrincipalEntry) {
/* 2902 */     StringWriter localStringWriter = new StringWriter();
/* 2903 */     PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
/* 2904 */     paramPrincipalEntry.write(localPrintWriter);
/* 2905 */     return localStringWriter.toString();
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\tools\policytool\ToolDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
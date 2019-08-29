/*      */ package sun.awt.windows;
/*      */ 
/*      */ import java.awt.Button;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FileDialog;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Label;
/*      */ import java.awt.Panel;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.peer.ComponentPeer;
/*      */ import java.awt.print.PageFormat;
/*      */ import java.awt.print.Pageable;
/*      */ import java.awt.print.Paper;
/*      */ import java.awt.print.Printable;
/*      */ import java.awt.print.PrinterException;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.io.File;
/*      */ import java.io.FilePermission;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.PrintServiceLookup;
/*      */ import javax.print.attribute.Attribute;
/*      */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttributeSet;
/*      */ import javax.print.attribute.standard.Chromaticity;
/*      */ import javax.print.attribute.standard.Copies;
/*      */ import javax.print.attribute.standard.Destination;
/*      */ import javax.print.attribute.standard.Media;
/*      */ import javax.print.attribute.standard.MediaSize;
/*      */ import javax.print.attribute.standard.MediaSizeName;
/*      */ import javax.print.attribute.standard.MediaTray;
/*      */ import javax.print.attribute.standard.OrientationRequested;
/*      */ import javax.print.attribute.standard.PageRanges;
/*      */ import javax.print.attribute.standard.PrintQuality;
/*      */ import javax.print.attribute.standard.PrinterResolution;
/*      */ import javax.print.attribute.standard.SheetCollate;
/*      */ import javax.print.attribute.standard.Sides;
/*      */ import sun.awt.Win32FontManager;
/*      */ import sun.java2d.Disposer;
/*      */ import sun.java2d.DisposerRecord;
/*      */ import sun.java2d.DisposerTarget;
/*      */ import sun.print.DialogOwner;
/*      */ import sun.print.PeekGraphics;
/*      */ import sun.print.PeekMetrics;
/*      */ import sun.print.RasterPrinterJob;
/*      */ import sun.print.ServiceDialog;
/*      */ import sun.print.SunAlternateMedia;
/*      */ import sun.print.SunPageSelection;
/*      */ import sun.print.Win32MediaTray;
/*      */ import sun.print.Win32PrintService;
/*      */ import sun.print.Win32PrintServiceLookup;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class WPrinterJob
/*      */   extends RasterPrinterJob
/*      */   implements DisposerTarget
/*      */ {
/*      */   protected static final long PS_ENDCAP_ROUND = 0L;
/*      */   protected static final long PS_ENDCAP_SQUARE = 256L;
/*      */   protected static final long PS_ENDCAP_FLAT = 512L;
/*      */   protected static final long PS_JOIN_ROUND = 0L;
/*      */   protected static final long PS_JOIN_BEVEL = 4096L;
/*      */   protected static final long PS_JOIN_MITER = 8192L;
/*      */   protected static final int POLYFILL_ALTERNATE = 1;
/*      */   protected static final int POLYFILL_WINDING = 2;
/*      */   private static final int MAX_WCOLOR = 255;
/*      */   private static final int SET_DUP_VERTICAL = 16;
/*      */   private static final int SET_DUP_HORIZONTAL = 32;
/*      */   private static final int SET_RES_HIGH = 64;
/*      */   private static final int SET_RES_LOW = 128;
/*      */   private static final int SET_COLOR = 512;
/*      */   private static final int SET_ORIENTATION = 16384;
/*      */   private static final int SET_COLLATED = 32768;
/*      */   private static final int PD_COLLATE = 16;
/*      */   private static final int PD_PRINTTOFILE = 32;
/*      */   private static final int DM_ORIENTATION = 1;
/*      */   private static final int DM_PAPERSIZE = 2;
/*      */   private static final int DM_COPIES = 256;
/*      */   private static final int DM_DEFAULTSOURCE = 512;
/*      */   private static final int DM_PRINTQUALITY = 1024;
/*      */   private static final int DM_COLOR = 2048;
/*      */   private static final int DM_DUPLEX = 4096;
/*      */   private static final int DM_YRESOLUTION = 8192;
/*      */   private static final int DM_COLLATE = 32768;
/*      */   private static final short DMCOLLATE_FALSE = 0;
/*      */   private static final short DMCOLLATE_TRUE = 1;
/*      */   private static final short DMORIENT_PORTRAIT = 1;
/*      */   private static final short DMORIENT_LANDSCAPE = 2;
/*      */   private static final short DMCOLOR_MONOCHROME = 1;
/*      */   private static final short DMCOLOR_COLOR = 2;
/*      */   private static final short DMRES_DRAFT = -1;
/*      */   private static final short DMRES_LOW = -2;
/*      */   private static final short DMRES_MEDIUM = -3;
/*      */   private static final short DMRES_HIGH = -4;
/*      */   private static final short DMDUP_SIMPLEX = 1;
/*      */   private static final short DMDUP_VERTICAL = 2;
/*      */   private static final short DMDUP_HORIZONTAL = 3;
/*      */   private static final int MAX_UNKNOWN_PAGES = 9999;
/*  260 */   private boolean driverDoesMultipleCopies = false;
/*  261 */   private boolean driverDoesCollation = false;
/*  262 */   private boolean userRequestedCollation = false;
/*  263 */   private boolean noDefaultPrinter = false;
/*      */   
/*      */   private static final class DevModeValues {
/*      */     int dmFields;
/*      */     short copies;
/*      */     short collate;
/*      */     short color;
/*      */     short duplex;
/*      */     short orient;
/*      */     short paper;
/*      */     short bin;
/*      */     short xres_quality;
/*      */     short yres;
/*      */   }
/*      */   
/*      */   static class HandleRecord implements DisposerRecord {
/*      */     private long mPrintDC;
/*      */     private long mPrintHDevMode;
/*      */     private long mPrintHDevNames;
/*      */     
/*      */     public void dispose() {
/*  284 */       WPrinterJob.deleteDC(this.mPrintDC, this.mPrintHDevMode, this.mPrintHDevNames);
/*      */     }
/*      */   }
/*      */   
/*  288 */   private HandleRecord handleRecord = new HandleRecord();
/*      */   
/*      */ 
/*      */   private int mPrintPaperSize;
/*      */   
/*      */ 
/*      */   private int mPrintXRes;
/*      */   
/*      */ 
/*      */   private int mPrintYRes;
/*      */   
/*      */ 
/*      */   private int mPrintPhysX;
/*      */   
/*      */ 
/*      */   private int mPrintPhysY;
/*      */   
/*      */ 
/*      */   private int mPrintWidth;
/*      */   
/*      */ 
/*      */   private int mPrintHeight;
/*      */   
/*      */ 
/*      */   private int mPageWidth;
/*      */   
/*      */ 
/*      */   private int mPageHeight;
/*      */   
/*      */   private int mAttSides;
/*      */   
/*      */   private int mAttChromaticity;
/*      */   
/*      */   private int mAttXRes;
/*      */   
/*      */   private int mAttYRes;
/*      */   
/*      */   private int mAttQuality;
/*      */   
/*      */   private int mAttCollate;
/*      */   
/*      */   private int mAttCopies;
/*      */   
/*      */   private int mAttMediaSizeName;
/*      */   
/*      */   private int mAttMediaTray;
/*      */   
/*  335 */   private String mDestination = null;
/*      */   
/*      */ 
/*      */ 
/*      */   private Color mLastColor;
/*      */   
/*      */ 
/*      */   private Color mLastTextColor;
/*      */   
/*      */ 
/*      */   private String mLastFontFamily;
/*      */   
/*      */ 
/*      */   private float mLastFontSize;
/*      */   
/*      */ 
/*      */   private int mLastFontStyle;
/*      */   
/*      */ 
/*      */   private int mLastRotation;
/*      */   
/*      */ 
/*      */   private float mLastAwScale;
/*      */   
/*      */ 
/*      */   private PrinterJob pjob;
/*      */   
/*      */ 
/*  363 */   private ComponentPeer dialogOwnerPeer = null;
/*      */   
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/*  369 */     Toolkit.getDefaultToolkit();
/*      */     
/*  371 */     initIDs();
/*      */     
/*  373 */     Win32FontManager.registerJREFontsForPrinting();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public WPrinterJob()
/*      */   {
/*  380 */     Disposer.addRecord(this.disposerReferent, this.handleRecord = new HandleRecord());
/*      */     
/*  382 */     initAttributeMembers();
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
/*  393 */   private Object disposerReferent = new Object();
/*      */   
/*      */   public Object getDisposerReferent()
/*      */   {
/*  397 */     return this.disposerReferent;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PageFormat pageDialog(PageFormat paramPageFormat)
/*      */     throws HeadlessException
/*      */   {
/*  426 */     if (GraphicsEnvironment.isHeadless()) {
/*  427 */       throw new HeadlessException();
/*      */     }
/*      */     
/*  430 */     if (!(getPrintService() instanceof Win32PrintService)) {
/*  431 */       return super.pageDialog(paramPageFormat);
/*      */     }
/*      */     
/*  434 */     PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
/*  435 */     boolean bool = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  441 */     WPageDialog localWPageDialog = new WPageDialog((Frame)null, this, localPageFormat, null);
/*      */     
/*  443 */     localWPageDialog.setRetVal(false);
/*  444 */     localWPageDialog.setVisible(true);
/*  445 */     bool = localWPageDialog.getRetVal();
/*  446 */     localWPageDialog.dispose();
/*      */     
/*      */ 
/*  449 */     if ((bool) && (this.myService != null))
/*      */     {
/*      */ 
/*  452 */       String str = getNativePrintService();
/*  453 */       if (!this.myService.getName().equals(str))
/*      */       {
/*      */         try
/*      */         {
/*  457 */           setPrintService(
/*  458 */             Win32PrintServiceLookup.getWin32PrintLUS()
/*  459 */             .getPrintServiceByName(str));
/*      */         }
/*      */         catch (PrinterException localPrinterException) {}
/*      */       }
/*      */       
/*      */ 
/*  465 */       updatePageAttributes(this.myService, localPageFormat);
/*      */       
/*  467 */       return localPageFormat;
/*      */     }
/*  469 */     return paramPageFormat;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean displayNativeDialog()
/*      */   {
/*  476 */     if (this.attributes == null) {
/*  477 */       return false;
/*      */     }
/*      */     
/*  480 */     DialogOwner localDialogOwner = (DialogOwner)this.attributes.get(DialogOwner.class);
/*  481 */     Frame localFrame = localDialogOwner != null ? localDialogOwner.getOwner() : null;
/*      */     
/*  483 */     WPrintDialog localWPrintDialog = new WPrintDialog(localFrame, this);
/*  484 */     localWPrintDialog.setRetVal(false);
/*  485 */     localWPrintDialog.setVisible(true);
/*  486 */     boolean bool = localWPrintDialog.getRetVal();
/*  487 */     localWPrintDialog.dispose();
/*      */     
/*      */ 
/*  490 */     Destination localDestination = (Destination)this.attributes.get(Destination.class);
/*  491 */     if ((localDestination == null) || (!bool)) {
/*  492 */       return bool;
/*      */     }
/*  494 */     String str1 = null;
/*  495 */     String str2 = "sun.print.resources.serviceui";
/*  496 */     ResourceBundle localResourceBundle = ResourceBundle.getBundle(str2);
/*      */     try {
/*  498 */       str1 = localResourceBundle.getString("dialog.printtofile");
/*      */     }
/*      */     catch (MissingResourceException localMissingResourceException) {}
/*  501 */     FileDialog localFileDialog = new FileDialog(localFrame, str1, 1);
/*      */     
/*      */ 
/*  504 */     URI localURI = localDestination.getURI();
/*      */     
/*      */ 
/*      */ 
/*  508 */     String str3 = localURI != null ? localURI.getSchemeSpecificPart() : null;
/*  509 */     if (str3 != null) {
/*  510 */       localObject1 = new File(str3);
/*  511 */       localFileDialog.setFile(((File)localObject1).getName());
/*  512 */       localObject2 = ((File)localObject1).getParentFile();
/*  513 */       if (localObject2 != null) {
/*  514 */         localFileDialog.setDirectory(((File)localObject2).getPath());
/*      */       }
/*      */     } else {
/*  517 */       localFileDialog.setFile("out.prn");
/*      */     }
/*      */     
/*  520 */     localFileDialog.setVisible(true);
/*  521 */     Object localObject1 = localFileDialog.getFile();
/*  522 */     if (localObject1 == null) {
/*  523 */       localFileDialog.dispose();
/*  524 */       return false;
/*      */     }
/*  526 */     Object localObject2 = localFileDialog.getDirectory() + (String)localObject1;
/*  527 */     File localFile1 = new File((String)localObject2);
/*  528 */     File localFile2 = localFile1.getParentFile();
/*  529 */     for (;;) { if ((!localFile1.exists()) || (
/*  530 */         (localFile1.isFile()) && (localFile1.canWrite()))) { if (localFile2 == null)
/*      */           break;
/*  532 */         if ((localFile2.exists()) && ((!localFile2.exists()) || (localFile2.canWrite()))) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*  537 */       new PrintToFileErrorDialog(localFrame, ServiceDialog.getMsg("dialog.owtitle"), ServiceDialog.getMsg("dialog.writeerror") + " " + (String)localObject2, ServiceDialog.getMsg("button.ok")).setVisible(true);
/*      */       
/*  539 */       localFileDialog.setVisible(true);
/*  540 */       localObject1 = localFileDialog.getFile();
/*  541 */       if (localObject1 == null) {
/*  542 */         localFileDialog.dispose();
/*  543 */         return false;
/*      */       }
/*  545 */       localObject2 = localFileDialog.getDirectory() + (String)localObject1;
/*  546 */       localFile1 = new File((String)localObject2);
/*  547 */       localFile2 = localFile1.getParentFile();
/*      */     }
/*  549 */     localFileDialog.dispose();
/*  550 */     this.attributes.add(new Destination(localFile1.toURI()));
/*  551 */     return true;
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
/*      */   public boolean printDialog()
/*      */     throws HeadlessException
/*      */   {
/*  568 */     if (GraphicsEnvironment.isHeadless()) {
/*  569 */       throw new HeadlessException();
/*      */     }
/*      */     
/*      */ 
/*  573 */     if (this.attributes == null) {
/*  574 */       this.attributes = new HashPrintRequestAttributeSet();
/*      */     }
/*      */     
/*  577 */     if (!(getPrintService() instanceof Win32PrintService)) {
/*  578 */       return super.printDialog(this.attributes);
/*      */     }
/*      */     
/*  581 */     if (this.noDefaultPrinter == true) {
/*  582 */       return false;
/*      */     }
/*  584 */     return displayNativeDialog();
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
/*      */   public void setPrintService(PrintService paramPrintService)
/*      */     throws PrinterException
/*      */   {
/*  602 */     super.setPrintService(paramPrintService);
/*  603 */     if (!(paramPrintService instanceof Win32PrintService)) {
/*  604 */       return;
/*      */     }
/*  606 */     this.driverDoesMultipleCopies = false;
/*  607 */     this.driverDoesCollation = false;
/*  608 */     setNativePrintServiceIfNeeded(paramPrintService.getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  615 */   private String lastNativeService = null;
/*      */   
/*      */   private void setNativePrintServiceIfNeeded(String paramString) throws PrinterException
/*      */   {
/*  619 */     if ((paramString != null) && (!paramString.equals(this.lastNativeService))) {
/*  620 */       setNativePrintService(paramString);
/*  621 */       this.lastNativeService = paramString;
/*      */     }
/*      */   }
/*      */   
/*      */   public PrintService getPrintService()
/*      */   {
/*  627 */     if (this.myService == null) {
/*  628 */       String str = getNativePrintService();
/*      */       
/*  630 */       if (str != null)
/*      */       {
/*  632 */         this.myService = Win32PrintServiceLookup.getWin32PrintLUS().getPrintServiceByName(str);
/*      */         
/*      */ 
/*  635 */         if (this.myService != null) {
/*  636 */           return this.myService;
/*      */         }
/*      */       }
/*      */       
/*  640 */       this.myService = PrintServiceLookup.lookupDefaultPrintService();
/*  641 */       if ((this.myService instanceof Win32PrintService)) {
/*      */         try {
/*  643 */           setNativePrintServiceIfNeeded(this.myService.getName());
/*      */         } catch (Exception localException) {
/*  645 */           this.myService = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  650 */     return this.myService;
/*      */   }
/*      */   
/*      */ 
/*      */   private void initAttributeMembers()
/*      */   {
/*  656 */     this.mAttSides = 0;
/*  657 */     this.mAttChromaticity = 0;
/*  658 */     this.mAttXRes = 0;
/*  659 */     this.mAttYRes = 0;
/*  660 */     this.mAttQuality = 0;
/*  661 */     this.mAttCollate = -1;
/*  662 */     this.mAttCopies = 0;
/*  663 */     this.mAttMediaTray = 0;
/*  664 */     this.mAttMediaSizeName = 0;
/*  665 */     this.mDestination = null;
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
/*      */   protected void setAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws PrinterException
/*      */   {
/*  684 */     initAttributeMembers();
/*  685 */     super.setAttributes(paramPrintRequestAttributeSet);
/*      */     
/*  687 */     this.mAttCopies = getCopiesInt();
/*  688 */     this.mDestination = this.destinationAttr;
/*      */     
/*  690 */     if (paramPrintRequestAttributeSet == null) {
/*  691 */       return;
/*      */     }
/*  693 */     Attribute[] arrayOfAttribute = paramPrintRequestAttributeSet.toArray();
/*  694 */     for (int i = 0; i < arrayOfAttribute.length; i++) {
/*  695 */       Object localObject = arrayOfAttribute[i];
/*      */       try {
/*  697 */         if (((Attribute)localObject).getCategory() == Sides.class) {
/*  698 */           setSidesAttrib((Attribute)localObject);
/*      */         }
/*  700 */         else if (((Attribute)localObject).getCategory() == Chromaticity.class) {
/*  701 */           setColorAttrib((Attribute)localObject);
/*      */         }
/*  703 */         else if (((Attribute)localObject).getCategory() == PrinterResolution.class) {
/*  704 */           setResolutionAttrib((Attribute)localObject);
/*      */         }
/*  706 */         else if (((Attribute)localObject).getCategory() == PrintQuality.class) {
/*  707 */           setQualityAttrib((Attribute)localObject);
/*      */         }
/*  709 */         else if (((Attribute)localObject).getCategory() == SheetCollate.class) {
/*  710 */           setCollateAttrib((Attribute)localObject);
/*  711 */         } else if ((((Attribute)localObject).getCategory() == Media.class) || 
/*  712 */           (((Attribute)localObject).getCategory() == SunAlternateMedia.class))
/*      */         {
/*      */ 
/*      */ 
/*  716 */           if (((Attribute)localObject).getCategory() == SunAlternateMedia.class) {
/*  717 */             Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
/*  718 */             if ((localMedia == null) || (!(localMedia instanceof MediaTray)))
/*      */             {
/*  720 */               localObject = ((SunAlternateMedia)localObject).getMedia();
/*      */             }
/*      */           }
/*  723 */           if ((localObject instanceof MediaSizeName)) {
/*  724 */             setWin32MediaAttrib((Attribute)localObject);
/*      */           }
/*  726 */           if ((localObject instanceof MediaTray)) {
/*  727 */             setMediaTrayAttrib((Attribute)localObject);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (ClassCastException localClassCastException) {}
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
/*      */ 
/*      */   public PageFormat defaultPage(PageFormat paramPageFormat)
/*      */   {
/*  751 */     PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
/*  752 */     getDefaultPage(localPageFormat);
/*  753 */     return localPageFormat;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt)
/*      */   {
/*  783 */     PeekMetrics localPeekMetrics = paramPeekGraphics.getMetrics();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     WPathGraphics localWPathGraphics;
/*      */     
/*      */ 
/*      */ 
/*  792 */     if ((!forcePDL) && ((forceRaster == true) || 
/*  793 */       (localPeekMetrics.hasNonSolidColors()) || 
/*  794 */       (localPeekMetrics.hasCompositing())))
/*      */     {
/*  796 */       localWPathGraphics = null;
/*      */     } else {
/*  798 */       BufferedImage localBufferedImage = new BufferedImage(8, 8, 1);
/*      */       
/*  800 */       Graphics2D localGraphics2D = localBufferedImage.createGraphics();
/*      */       
/*  802 */       boolean bool = !paramPeekGraphics.getAWTDrawingOnly();
/*  803 */       localWPathGraphics = new WPathGraphics(localGraphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, bool);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  808 */     return localWPathGraphics;
/*      */   }
/*      */   
/*      */ 
/*      */   protected double getXRes()
/*      */   {
/*  814 */     if (this.mAttXRes != 0) {
/*  815 */       return this.mAttXRes;
/*      */     }
/*  817 */     return this.mPrintXRes;
/*      */   }
/*      */   
/*      */ 
/*      */   protected double getYRes()
/*      */   {
/*  823 */     if (this.mAttYRes != 0) {
/*  824 */       return this.mAttYRes;
/*      */     }
/*  826 */     return this.mPrintYRes;
/*      */   }
/*      */   
/*      */ 
/*      */   protected double getPhysicalPrintableX(Paper paramPaper)
/*      */   {
/*  832 */     return this.mPrintPhysX;
/*      */   }
/*      */   
/*      */   protected double getPhysicalPrintableY(Paper paramPaper)
/*      */   {
/*  837 */     return this.mPrintPhysY;
/*      */   }
/*      */   
/*      */   protected double getPhysicalPrintableWidth(Paper paramPaper)
/*      */   {
/*  842 */     return this.mPrintWidth;
/*      */   }
/*      */   
/*      */   protected double getPhysicalPrintableHeight(Paper paramPaper)
/*      */   {
/*  847 */     return this.mPrintHeight;
/*      */   }
/*      */   
/*      */   protected double getPhysicalPageWidth(Paper paramPaper)
/*      */   {
/*  852 */     return this.mPageWidth;
/*      */   }
/*      */   
/*      */   protected double getPhysicalPageHeight(Paper paramPaper)
/*      */   {
/*  857 */     return this.mPageHeight;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isCollated()
/*      */   {
/*  869 */     return this.userRequestedCollation;
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
/*      */   protected int getCollatedCopies()
/*      */   {
/*  882 */     debug_println("driverDoesMultipleCopies=" + this.driverDoesMultipleCopies + " driverDoesCollation=" + this.driverDoesCollation);
/*      */     
/*  884 */     if ((super.isCollated()) && (!this.driverDoesCollation))
/*      */     {
/*      */ 
/*  887 */       this.mAttCollate = 0;
/*  888 */       this.mAttCopies = 1;
/*  889 */       return getCopies();
/*      */     }
/*      */     
/*  892 */     return 1;
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
/*      */   protected int getNoncollatedCopies()
/*      */   {
/*  905 */     if ((this.driverDoesMultipleCopies) || (super.isCollated())) {
/*  906 */       return 1;
/*      */     }
/*  908 */     return getCopies();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long getPrintDC()
/*      */   {
/*  919 */     return this.handleRecord.mPrintDC;
/*      */   }
/*      */   
/*      */   private void setPrintDC(long paramLong) {
/*  923 */     this.handleRecord.mPrintDC = paramLong;
/*      */   }
/*      */   
/*      */   private long getDevMode() {
/*  927 */     return this.handleRecord.mPrintHDevMode;
/*      */   }
/*      */   
/*      */   private void setDevMode(long paramLong) {
/*  931 */     this.handleRecord.mPrintHDevMode = paramLong;
/*      */   }
/*      */   
/*      */   private long getDevNames() {
/*  935 */     return this.handleRecord.mPrintHDevNames;
/*      */   }
/*      */   
/*      */   private void setDevNames(long paramLong) {
/*  939 */     this.handleRecord.mPrintHDevNames = paramLong;
/*      */   }
/*      */   
/*      */   protected void beginPath() {
/*  943 */     beginPath(getPrintDC());
/*      */   }
/*      */   
/*      */   protected void endPath() {
/*  947 */     endPath(getPrintDC());
/*      */   }
/*      */   
/*      */   protected void closeFigure() {
/*  951 */     closeFigure(getPrintDC());
/*      */   }
/*      */   
/*      */   protected void fillPath() {
/*  955 */     fillPath(getPrintDC());
/*      */   }
/*      */   
/*      */   protected void moveTo(float paramFloat1, float paramFloat2) {
/*  959 */     moveTo(getPrintDC(), paramFloat1, paramFloat2);
/*      */   }
/*      */   
/*      */   protected void lineTo(float paramFloat1, float paramFloat2) {
/*  963 */     lineTo(getPrintDC(), paramFloat1, paramFloat2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void polyBezierTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*      */   {
/*  970 */     polyBezierTo(getPrintDC(), paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setPolyFillMode(int paramInt)
/*      */   {
/*  982 */     setPolyFillMode(getPrintDC(), paramInt);
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
/*      */   protected void selectSolidBrush(Color paramColor)
/*      */   {
/*  995 */     if (!paramColor.equals(this.mLastColor)) {
/*  996 */       this.mLastColor = paramColor;
/*  997 */       float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
/*      */       
/*  999 */       selectSolidBrush(getPrintDC(), (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
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
/*      */   protected int getPenX()
/*      */   {
/* 1012 */     return getPenX(getPrintDC());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getPenY()
/*      */   {
/* 1022 */     return getPenY(getPrintDC());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void selectClipPath()
/*      */   {
/* 1030 */     selectClipPath(getPrintDC());
/*      */   }
/*      */   
/*      */   protected void frameRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*      */   {
/* 1035 */     frameRect(getPrintDC(), paramFloat1, paramFloat2, paramFloat3, paramFloat4);
/*      */   }
/*      */   
/*      */   protected void fillRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Color paramColor)
/*      */   {
/* 1040 */     float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
/*      */     
/* 1042 */     fillRect(getPrintDC(), paramFloat1, paramFloat2, paramFloat3, paramFloat4, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void selectPen(float paramFloat, Color paramColor)
/*      */   {
/* 1051 */     float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
/*      */     
/* 1053 */     selectPen(getPrintDC(), paramFloat, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
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
/*      */   protected boolean selectStylePen(int paramInt1, int paramInt2, float paramFloat, Color paramColor)
/*      */   {
/* 1066 */     float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
/*      */     long l1;
/* 1068 */     switch (paramInt1) {
/* 1069 */     case 0:  l1 = 512L; break;
/* 1070 */     case 1:  l1 = 0L; break;
/*      */     case 2: default: 
/* 1072 */       l1 = 256L;
/*      */     }
/*      */     long l2;
/* 1075 */     switch (paramInt2) {
/* 1076 */     case 2:  l2 = 4096L; break;
/*      */     case 0: default: 
/* 1078 */       l2 = 8192L; break;
/* 1079 */     case 1:  l2 = 0L;
/*      */     }
/*      */     
/* 1082 */     return selectStylePen(getPrintDC(), l1, l2, paramFloat, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
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
/*      */   protected boolean setFont(String paramString, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2)
/*      */   {
/* 1095 */     boolean bool = true;
/*      */     
/* 1097 */     if ((!paramString.equals(this.mLastFontFamily)) || (paramFloat1 != this.mLastFontSize) || (paramInt1 != this.mLastFontStyle) || (paramInt2 != this.mLastRotation) || (paramFloat2 != this.mLastAwScale))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1103 */       bool = setFont(getPrintDC(), paramString, paramFloat1, (paramInt1 & 0x1) != 0, (paramInt1 & 0x2) != 0, paramInt2, paramFloat2);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1109 */       if (bool) {
/* 1110 */         this.mLastFontFamily = paramString;
/* 1111 */         this.mLastFontSize = paramFloat1;
/* 1112 */         this.mLastFontStyle = paramInt1;
/* 1113 */         this.mLastRotation = paramInt2;
/* 1114 */         this.mLastAwScale = paramFloat2;
/*      */       }
/*      */     }
/* 1117 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setTextColor(Color paramColor)
/*      */   {
/* 1127 */     if (!paramColor.equals(this.mLastTextColor)) {
/* 1128 */       this.mLastTextColor = paramColor;
/* 1129 */       float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
/*      */       
/* 1131 */       setTextColor(getPrintDC(), (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String removeControlChars(String paramString)
/*      */   {
/* 1143 */     return super.removeControlChars(paramString);
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
/*      */   protected void textOut(String paramString, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat)
/*      */   {
/* 1158 */     String str = removeControlChars(paramString);
/* 1159 */     assert ((paramArrayOfFloat == null) || (str.length() == paramString.length()));
/* 1160 */     if (str.length() == 0) {
/* 1161 */       return;
/*      */     }
/* 1163 */     textOut(getPrintDC(), str, str.length(), false, paramFloat1, paramFloat2, paramArrayOfFloat);
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
/*      */   protected void glyphsOut(int[] paramArrayOfInt, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat)
/*      */   {
/* 1181 */     char[] arrayOfChar = new char[paramArrayOfInt.length];
/* 1182 */     for (int i = 0; i < paramArrayOfInt.length; i++) {
/* 1183 */       arrayOfChar[i] = ((char)(paramArrayOfInt[i] & 0xFFFF));
/*      */     }
/* 1185 */     String str = new String(arrayOfChar);
/* 1186 */     textOut(getPrintDC(), str, paramArrayOfInt.length, true, paramFloat1, paramFloat2, paramArrayOfFloat);
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
/*      */   protected int getGDIAdvance(String paramString)
/*      */   {
/* 1200 */     paramString = removeControlChars(paramString);
/* 1201 */     if (paramString.length() == 0) {
/* 1202 */       return 0;
/*      */     }
/* 1204 */     return getGDIAdvance(getPrintDC(), paramString);
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
/*      */   protected void drawImage3ByteBGR(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8)
/*      */   {
/* 1226 */     drawDIBImage(getPrintDC(), paramArrayOfByte, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7, paramFloat8, 24, null);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void drawDIBImage(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt, IndexColorModel paramIndexColorModel)
/*      */   {
/* 1257 */     int i = 24;
/* 1258 */     byte[] arrayOfByte = null;
/*      */     
/* 1260 */     if (paramIndexColorModel != null) {
/* 1261 */       i = paramInt;
/* 1262 */       arrayOfByte = new byte[(1 << paramIndexColorModel.getPixelSize()) * 4];
/* 1263 */       for (int j = 0; j < paramIndexColorModel.getMapSize(); j++) {
/* 1264 */         arrayOfByte[(j * 4 + 0)] = ((byte)(paramIndexColorModel.getBlue(j) & 0xFF));
/* 1265 */         arrayOfByte[(j * 4 + 1)] = ((byte)(paramIndexColorModel.getGreen(j) & 0xFF));
/* 1266 */         arrayOfByte[(j * 4 + 2)] = ((byte)(paramIndexColorModel.getRed(j) & 0xFF));
/*      */       }
/*      */     }
/*      */     
/* 1270 */     drawDIBImage(getPrintDC(), paramArrayOfByte, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7, paramFloat8, i, arrayOfByte);
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
/*      */   protected void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean)
/*      */   {
/* 1290 */     invalidateCachedState();
/*      */     
/* 1292 */     deviceStartPage(paramPageFormat, paramPrintable, paramInt, paramBoolean);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt)
/*      */   {
/* 1302 */     deviceEndPage(paramPageFormat, paramPrintable, paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void invalidateCachedState()
/*      */   {
/* 1309 */     this.mLastColor = null;
/* 1310 */     this.mLastTextColor = null;
/* 1311 */     this.mLastFontFamily = null;
/*      */   }
/*      */   
/* 1314 */   private boolean defaultCopies = true;
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCopies(int paramInt)
/*      */   {
/* 1320 */     super.setCopies(paramInt);
/* 1321 */     this.defaultCopies = false;
/* 1322 */     this.mAttCopies = paramInt;
/* 1323 */     setNativeCopies(paramInt);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void startDoc()
/*      */     throws PrinterException
/*      */   {
/* 1363 */     if (!_startDoc(this.mDestination, getJobName())) {
/* 1364 */       cancel();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final String getPrinterAttrib()
/*      */   {
/* 1576 */     PrintService localPrintService = getPrintService();
/* 1577 */     String str = localPrintService != null ? localPrintService.getName() : null;
/* 1578 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */   private final int getCollateAttrib()
/*      */   {
/* 1584 */     return this.mAttCollate;
/*      */   }
/*      */   
/*      */   private void setCollateAttrib(Attribute paramAttribute) {
/* 1588 */     if (paramAttribute == SheetCollate.COLLATED) {
/* 1589 */       this.mAttCollate = 1;
/*      */     } else {
/* 1591 */       this.mAttCollate = 0;
/*      */     }
/*      */   }
/*      */   
/*      */   private void setCollateAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/* 1597 */     setCollateAttrib(paramAttribute);
/* 1598 */     paramPrintRequestAttributeSet.add(paramAttribute);
/*      */   }
/*      */   
/*      */ 
/*      */   private final int getOrientAttrib()
/*      */   {
/* 1604 */     int i = 1;
/*      */     
/* 1606 */     OrientationRequested localOrientationRequested = this.attributes == null ? null : (OrientationRequested)this.attributes.get(OrientationRequested.class);
/* 1607 */     if (localOrientationRequested == null)
/*      */     {
/* 1609 */       localOrientationRequested = (OrientationRequested)this.myService.getDefaultAttributeValue(OrientationRequested.class);
/*      */     }
/* 1611 */     if (localOrientationRequested != null) {
/* 1612 */       if (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
/* 1613 */         i = 2;
/* 1614 */       } else if (localOrientationRequested == OrientationRequested.LANDSCAPE) {
/* 1615 */         i = 0;
/*      */       }
/*      */     }
/*      */     
/* 1619 */     return i;
/*      */   }
/*      */   
/*      */   private void setOrientAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/* 1624 */     if (paramPrintRequestAttributeSet != null) {
/* 1625 */       paramPrintRequestAttributeSet.add(paramAttribute);
/*      */     }
/*      */   }
/*      */   
/*      */   private final int getCopiesAttrib()
/*      */   {
/* 1631 */     if (this.defaultCopies) {
/* 1632 */       return 0;
/*      */     }
/* 1634 */     return getCopiesInt();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final void setRangeCopiesAttribute(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
/*      */   {
/* 1641 */     if (this.attributes != null) {
/* 1642 */       if (paramBoolean) {
/* 1643 */         this.attributes.add(new PageRanges(paramInt1, paramInt2));
/* 1644 */         setPageRange(paramInt1, paramInt2);
/*      */       }
/* 1646 */       this.defaultCopies = false;
/* 1647 */       this.attributes.add(new Copies(paramInt3));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1652 */       super.setCopies(paramInt3);
/* 1653 */       this.mAttCopies = paramInt3;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private final boolean getDestAttrib()
/*      */   {
/* 1660 */     return this.mDestination != null;
/*      */   }
/*      */   
/*      */   private final int getQualityAttrib()
/*      */   {
/* 1665 */     return this.mAttQuality;
/*      */   }
/*      */   
/*      */   private void setQualityAttrib(Attribute paramAttribute) {
/* 1669 */     if (paramAttribute == PrintQuality.HIGH) {
/* 1670 */       this.mAttQuality = -4;
/* 1671 */     } else if (paramAttribute == PrintQuality.NORMAL) {
/* 1672 */       this.mAttQuality = -3;
/*      */     } else {
/* 1674 */       this.mAttQuality = -2;
/*      */     }
/*      */   }
/*      */   
/*      */   private void setQualityAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/* 1680 */     setQualityAttrib(paramAttribute);
/* 1681 */     paramPrintRequestAttributeSet.add(paramAttribute);
/*      */   }
/*      */   
/*      */   private final int getColorAttrib()
/*      */   {
/* 1686 */     return this.mAttChromaticity;
/*      */   }
/*      */   
/*      */   private void setColorAttrib(Attribute paramAttribute) {
/* 1690 */     if (paramAttribute == Chromaticity.COLOR) {
/* 1691 */       this.mAttChromaticity = 2;
/*      */     } else {
/* 1693 */       this.mAttChromaticity = 1;
/*      */     }
/*      */   }
/*      */   
/*      */   private void setColorAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/* 1699 */     setColorAttrib(paramAttribute);
/* 1700 */     paramPrintRequestAttributeSet.add(paramAttribute);
/*      */   }
/*      */   
/*      */   private final int getSidesAttrib()
/*      */   {
/* 1705 */     return this.mAttSides;
/*      */   }
/*      */   
/*      */   private void setSidesAttrib(Attribute paramAttribute) {
/* 1709 */     if (paramAttribute == Sides.TWO_SIDED_LONG_EDGE) {
/* 1710 */       this.mAttSides = 2;
/* 1711 */     } else if (paramAttribute == Sides.TWO_SIDED_SHORT_EDGE) {
/* 1712 */       this.mAttSides = 3;
/*      */     } else {
/* 1714 */       this.mAttSides = 1;
/*      */     }
/*      */   }
/*      */   
/*      */   private void setSidesAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/* 1720 */     setSidesAttrib(paramAttribute);
/* 1721 */     paramPrintRequestAttributeSet.add(paramAttribute);
/*      */   }
/*      */   
/*      */   private final int[] getWin32MediaAttrib()
/*      */   {
/* 1726 */     int[] arrayOfInt = { 0, 0 };
/* 1727 */     if (this.attributes != null) {
/* 1728 */       Media localMedia = (Media)this.attributes.get(Media.class);
/* 1729 */       if ((localMedia instanceof MediaSizeName)) {
/* 1730 */         MediaSizeName localMediaSizeName = (MediaSizeName)localMedia;
/* 1731 */         MediaSize localMediaSize = MediaSize.getMediaSizeForName(localMediaSizeName);
/* 1732 */         if (localMediaSize != null) {
/* 1733 */           arrayOfInt[0] = ((int)(localMediaSize.getX(25400) * 72.0D));
/* 1734 */           arrayOfInt[1] = ((int)(localMediaSize.getY(25400) * 72.0D));
/*      */         }
/*      */       }
/*      */     }
/* 1738 */     return arrayOfInt;
/*      */   }
/*      */   
/*      */   private void setWin32MediaAttrib(Attribute paramAttribute) {
/* 1742 */     if (!(paramAttribute instanceof MediaSizeName)) {
/* 1743 */       return;
/*      */     }
/* 1745 */     MediaSizeName localMediaSizeName = (MediaSizeName)paramAttribute;
/* 1746 */     this.mAttMediaSizeName = ((Win32PrintService)this.myService).findPaperID(localMediaSizeName);
/*      */   }
/*      */   
/*      */ 
/*      */   private void addPaperSize(PrintRequestAttributeSet paramPrintRequestAttributeSet, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1752 */     if (paramPrintRequestAttributeSet == null) {
/* 1753 */       return;
/*      */     }
/*      */     
/* 1756 */     MediaSizeName localMediaSizeName = ((Win32PrintService)this.myService).findWin32Media(paramInt1);
/* 1757 */     if (localMediaSizeName == null)
/*      */     {
/* 1759 */       localMediaSizeName = ((Win32PrintService)this.myService).findMatchingMediaSizeNameMM(paramInt2, paramInt3);
/*      */     }
/*      */     
/* 1762 */     if (localMediaSizeName != null) {
/* 1763 */       paramPrintRequestAttributeSet.add(localMediaSizeName);
/*      */     }
/*      */   }
/*      */   
/*      */   private void setWin32MediaAttrib(int paramInt1, int paramInt2, int paramInt3) {
/* 1768 */     addPaperSize(this.attributes, paramInt1, paramInt2, paramInt3);
/* 1769 */     this.mAttMediaSizeName = paramInt1;
/*      */   }
/*      */   
/*      */   private void setMediaTrayAttrib(Attribute paramAttribute)
/*      */   {
/* 1774 */     if (paramAttribute == MediaTray.BOTTOM) {
/* 1775 */       this.mAttMediaTray = 2;
/* 1776 */     } else if (paramAttribute == MediaTray.ENVELOPE) {
/* 1777 */       this.mAttMediaTray = 5;
/* 1778 */     } else if (paramAttribute == MediaTray.LARGE_CAPACITY) {
/* 1779 */       this.mAttMediaTray = 11;
/* 1780 */     } else if (paramAttribute == MediaTray.MAIN) {
/* 1781 */       this.mAttMediaTray = 1;
/* 1782 */     } else if (paramAttribute == MediaTray.MANUAL) {
/* 1783 */       this.mAttMediaTray = 4;
/* 1784 */     } else if (paramAttribute == MediaTray.MIDDLE) {
/* 1785 */       this.mAttMediaTray = 3;
/* 1786 */     } else if (paramAttribute == MediaTray.SIDE)
/*      */     {
/* 1788 */       this.mAttMediaTray = 7;
/* 1789 */     } else if (paramAttribute == MediaTray.TOP) {
/* 1790 */       this.mAttMediaTray = 1;
/*      */     }
/* 1792 */     else if ((paramAttribute instanceof Win32MediaTray)) {
/* 1793 */       this.mAttMediaTray = ((Win32MediaTray)paramAttribute).winID;
/*      */     } else {
/* 1795 */       this.mAttMediaTray = 1;
/*      */     }
/*      */   }
/*      */   
/*      */   private void setMediaTrayAttrib(int paramInt)
/*      */   {
/* 1801 */     this.mAttMediaTray = paramInt;
/* 1802 */     MediaTray localMediaTray = ((Win32PrintService)this.myService).findMediaTray(paramInt);
/*      */   }
/*      */   
/*      */   private int getMediaTrayAttrib() {
/* 1806 */     return this.mAttMediaTray;
/*      */   }
/*      */   
/*      */ 
/*      */   private final boolean getPrintToFileEnabled()
/*      */   {
/* 1812 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1813 */     if (localSecurityManager != null) {
/* 1814 */       FilePermission localFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
/*      */       try
/*      */       {
/* 1817 */         localSecurityManager.checkPermission(localFilePermission);
/*      */       } catch (SecurityException localSecurityException) {
/* 1819 */         return false;
/*      */       }
/*      */     }
/* 1822 */     return true;
/*      */   }
/*      */   
/*      */   private final void setNativeAttributes(int paramInt1, int paramInt2, int paramInt3) {
/* 1826 */     if (this.attributes == null)
/*      */       return;
/*      */     Object localObject;
/* 1829 */     if ((paramInt1 & 0x20) != 0) {
/* 1830 */       localObject = (Destination)this.attributes.get(Destination.class);
/*      */       
/* 1832 */       if (localObject == null) {
/*      */         try {
/* 1834 */           this.attributes.add(new Destination(new File("./out.prn")
/* 1835 */             .toURI()));
/*      */         } catch (SecurityException localSecurityException) {
/*      */           try {
/* 1838 */             this.attributes.add(new Destination(new URI("file:out.prn")));
/*      */           }
/*      */           catch (URISyntaxException localURISyntaxException) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1845 */       this.attributes.remove(Destination.class);
/*      */     }
/*      */     
/* 1848 */     if ((paramInt1 & 0x10) != 0) {
/* 1849 */       setCollateAttrib(SheetCollate.COLLATED, this.attributes);
/*      */     } else {
/* 1851 */       setCollateAttrib(SheetCollate.UNCOLLATED, this.attributes);
/*      */     }
/*      */     
/* 1854 */     if ((paramInt1 & 0x2) != 0) {
/* 1855 */       this.attributes.add(SunPageSelection.RANGE);
/* 1856 */     } else if ((paramInt1 & 0x1) != 0) {
/* 1857 */       this.attributes.add(SunPageSelection.SELECTION);
/*      */     } else {
/* 1859 */       this.attributes.add(SunPageSelection.ALL);
/*      */     }
/*      */     
/* 1862 */     if ((paramInt2 & 0x1) != 0) {
/* 1863 */       if ((paramInt3 & 0x4000) != 0) {
/* 1864 */         setOrientAttrib(OrientationRequested.LANDSCAPE, this.attributes);
/*      */       } else {
/* 1866 */         setOrientAttrib(OrientationRequested.PORTRAIT, this.attributes);
/*      */       }
/*      */     }
/*      */     
/* 1870 */     if ((paramInt2 & 0x800) != 0) {
/* 1871 */       if ((paramInt3 & 0x200) != 0) {
/* 1872 */         setColorAttrib(Chromaticity.COLOR, this.attributes);
/*      */       } else {
/* 1874 */         setColorAttrib(Chromaticity.MONOCHROME, this.attributes);
/*      */       }
/*      */     }
/*      */     
/* 1878 */     if ((paramInt2 & 0x400) != 0)
/*      */     {
/* 1880 */       if ((paramInt3 & 0x80) != 0) {
/* 1881 */         localObject = PrintQuality.DRAFT;
/* 1882 */       } else if ((paramInt2 & 0x40) != 0) {
/* 1883 */         localObject = PrintQuality.HIGH;
/*      */       } else {
/* 1885 */         localObject = PrintQuality.NORMAL;
/*      */       }
/* 1887 */       setQualityAttrib((Attribute)localObject, this.attributes);
/*      */     }
/*      */     
/* 1890 */     if ((paramInt2 & 0x1000) != 0)
/*      */     {
/* 1892 */       if ((paramInt3 & 0x10) != 0) {
/* 1893 */         localObject = Sides.TWO_SIDED_LONG_EDGE;
/* 1894 */       } else if ((paramInt3 & 0x20) != 0) {
/* 1895 */         localObject = Sides.TWO_SIDED_SHORT_EDGE;
/*      */       } else {
/* 1897 */         localObject = Sides.ONE_SIDED;
/*      */       }
/* 1899 */       setSidesAttrib((Attribute)localObject, this.attributes);
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
/*      */ 
/*      */   private void getDevModeValues(PrintRequestAttributeSet paramPrintRequestAttributeSet, DevModeValues paramDevModeValues)
/*      */   {
/* 1919 */     Copies localCopies = (Copies)paramPrintRequestAttributeSet.get(Copies.class);
/* 1920 */     if (localCopies != null) {
/* 1921 */       paramDevModeValues.dmFields |= 0x100;
/* 1922 */       paramDevModeValues.copies = ((short)localCopies.getValue());
/*      */     }
/*      */     
/* 1925 */     SheetCollate localSheetCollate = (SheetCollate)paramPrintRequestAttributeSet.get(SheetCollate.class);
/* 1926 */     if (localSheetCollate != null) {
/* 1927 */       paramDevModeValues.dmFields |= 0x8000;
/* 1928 */       paramDevModeValues.collate = (localSheetCollate == SheetCollate.COLLATED ? 1 : 0);
/*      */     }
/*      */     
/*      */ 
/* 1932 */     Chromaticity localChromaticity = (Chromaticity)paramPrintRequestAttributeSet.get(Chromaticity.class);
/* 1933 */     if (localChromaticity != null) {
/* 1934 */       paramDevModeValues.dmFields |= 0x800;
/* 1935 */       if (localChromaticity == Chromaticity.COLOR) {
/* 1936 */         paramDevModeValues.color = 2;
/*      */       } else {
/* 1938 */         paramDevModeValues.color = 1;
/*      */       }
/*      */     }
/*      */     
/* 1942 */     Sides localSides = (Sides)paramPrintRequestAttributeSet.get(Sides.class);
/* 1943 */     if (localSides != null) {
/* 1944 */       paramDevModeValues.dmFields |= 0x1000;
/* 1945 */       if (localSides == Sides.TWO_SIDED_LONG_EDGE) {
/* 1946 */         paramDevModeValues.duplex = 2;
/* 1947 */       } else if (localSides == Sides.TWO_SIDED_SHORT_EDGE) {
/* 1948 */         paramDevModeValues.duplex = 3;
/*      */       } else {
/* 1950 */         paramDevModeValues.duplex = 1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1955 */     OrientationRequested localOrientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
/* 1956 */     if (localOrientationRequested != null) {
/* 1957 */       paramDevModeValues.dmFields |= 0x1;
/* 1958 */       paramDevModeValues.orient = (localOrientationRequested == OrientationRequested.LANDSCAPE ? 2 : 1);
/*      */     }
/*      */     
/*      */ 
/* 1962 */     Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
/* 1963 */     if ((localMedia instanceof MediaSizeName)) {
/* 1964 */       paramDevModeValues.dmFields |= 0x2;
/* 1965 */       localObject1 = (MediaSizeName)localMedia;
/*      */       
/* 1967 */       paramDevModeValues.paper = ((short)((Win32PrintService)this.myService).findPaperID((MediaSizeName)localObject1));
/*      */     }
/*      */     
/* 1970 */     Object localObject1 = null;
/* 1971 */     if ((localMedia instanceof MediaTray)) {
/* 1972 */       localObject1 = (MediaTray)localMedia;
/*      */     }
/* 1974 */     if (localObject1 == null)
/*      */     {
/* 1976 */       localObject2 = (SunAlternateMedia)paramPrintRequestAttributeSet.get(SunAlternateMedia.class);
/* 1977 */       if ((localObject2 != null) && ((((SunAlternateMedia)localObject2).getMedia() instanceof MediaTray))) {
/* 1978 */         localObject1 = (MediaTray)((SunAlternateMedia)localObject2).getMedia();
/*      */       }
/*      */     }
/*      */     
/* 1982 */     if (localObject1 != null) {
/* 1983 */       paramDevModeValues.dmFields |= 0x200;
/* 1984 */       paramDevModeValues.bin = ((short)((Win32PrintService)this.myService).findTrayID((MediaTray)localObject1));
/*      */     }
/*      */     
/* 1987 */     Object localObject2 = (PrintQuality)paramPrintRequestAttributeSet.get(PrintQuality.class);
/* 1988 */     if (localObject2 != null) {
/* 1989 */       paramDevModeValues.dmFields |= 0x400;
/* 1990 */       if (localObject2 == PrintQuality.DRAFT) {
/* 1991 */         paramDevModeValues.xres_quality = -1;
/* 1992 */       } else if (localObject2 == PrintQuality.HIGH) {
/* 1993 */         paramDevModeValues.xres_quality = -4;
/*      */       } else {
/* 1995 */         paramDevModeValues.xres_quality = -3;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2000 */     PrinterResolution localPrinterResolution = (PrinterResolution)paramPrintRequestAttributeSet.get(PrinterResolution.class);
/* 2001 */     if (localPrinterResolution != null) {
/* 2002 */       paramDevModeValues.dmFields |= 0x2400;
/*      */       
/* 2004 */       paramDevModeValues.xres_quality = ((short)localPrinterResolution.getCrossFeedResolution(100));
/* 2005 */       paramDevModeValues.yres = ((short)localPrinterResolution.getFeedResolution(100));
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
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void setJobAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet, int paramInt1, int paramInt2, short paramShort1, short paramShort2, short paramShort3, short paramShort4, short paramShort5, short paramShort6, short paramShort7)
/*      */   {
/* 2027 */     if (paramPrintRequestAttributeSet == null) {
/* 2028 */       return;
/*      */     }
/*      */     
/* 2031 */     if ((paramInt1 & 0x100) != 0) {
/* 2032 */       paramPrintRequestAttributeSet.add(new Copies(paramShort1));
/*      */     }
/*      */     
/* 2035 */     if ((paramInt1 & 0x8000) != 0) {
/* 2036 */       if ((paramInt2 & 0x8000) != 0) {
/* 2037 */         paramPrintRequestAttributeSet.add(SheetCollate.COLLATED);
/*      */       } else {
/* 2039 */         paramPrintRequestAttributeSet.add(SheetCollate.UNCOLLATED);
/*      */       }
/*      */     }
/*      */     
/* 2043 */     if ((paramInt1 & 0x1) != 0) {
/* 2044 */       if ((paramInt2 & 0x4000) != 0) {
/* 2045 */         paramPrintRequestAttributeSet.add(OrientationRequested.LANDSCAPE);
/*      */       } else {
/* 2047 */         paramPrintRequestAttributeSet.add(OrientationRequested.PORTRAIT);
/*      */       }
/*      */     }
/*      */     
/* 2051 */     if ((paramInt1 & 0x800) != 0) {
/* 2052 */       if ((paramInt2 & 0x200) != 0) {
/* 2053 */         paramPrintRequestAttributeSet.add(Chromaticity.COLOR);
/*      */       } else {
/* 2055 */         paramPrintRequestAttributeSet.add(Chromaticity.MONOCHROME);
/*      */       }
/*      */     }
/*      */     Object localObject;
/* 2059 */     if ((paramInt1 & 0x400) != 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2067 */       if (paramShort6 < 0)
/*      */       {
/* 2069 */         if ((paramInt2 & 0x80) != 0) {
/* 2070 */           localObject = PrintQuality.DRAFT;
/* 2071 */         } else if ((paramInt1 & 0x40) != 0) {
/* 2072 */           localObject = PrintQuality.HIGH;
/*      */         } else {
/* 2074 */           localObject = PrintQuality.NORMAL;
/*      */         }
/* 2076 */         paramPrintRequestAttributeSet.add((Attribute)localObject);
/* 2077 */       } else if ((paramShort6 > 0) && (paramShort7 > 0)) {
/* 2078 */         paramPrintRequestAttributeSet.add(new PrinterResolution(paramShort6, paramShort7, 100));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2083 */     if ((paramInt1 & 0x1000) != 0)
/*      */     {
/* 2085 */       if ((paramInt2 & 0x10) != 0) {
/* 2086 */         localObject = Sides.TWO_SIDED_LONG_EDGE;
/* 2087 */       } else if ((paramInt2 & 0x20) != 0) {
/* 2088 */         localObject = Sides.TWO_SIDED_SHORT_EDGE;
/*      */       } else {
/* 2090 */         localObject = Sides.ONE_SIDED;
/*      */       }
/* 2092 */       paramPrintRequestAttributeSet.add((Attribute)localObject);
/*      */     }
/*      */     
/* 2095 */     if ((paramInt1 & 0x2) != 0) {
/* 2096 */       addPaperSize(paramPrintRequestAttributeSet, paramShort2, paramShort3, paramShort4);
/*      */     }
/*      */     
/* 2099 */     if ((paramInt1 & 0x200) != 0)
/*      */     {
/* 2101 */       localObject = ((Win32PrintService)this.myService).findMediaTray(paramShort5);
/* 2102 */       paramPrintRequestAttributeSet.add(new SunAlternateMedia((Media)localObject));
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public PrintRequestAttributeSet showDocumentProperties(Window paramWindow, PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/*      */     try
/*      */     {
/* 2126 */       setNativePrintServiceIfNeeded(paramPrintService.getName());
/*      */     }
/*      */     catch (PrinterException localPrinterException) {}
/* 2129 */     long l = ((WWindowPeer)paramWindow.getPeer()).getHWnd();
/* 2130 */     DevModeValues localDevModeValues = new DevModeValues(null);
/* 2131 */     getDevModeValues(paramPrintRequestAttributeSet, localDevModeValues);
/*      */     
/* 2133 */     boolean bool = showDocProperties(l, paramPrintRequestAttributeSet, localDevModeValues.dmFields, localDevModeValues.copies, localDevModeValues.collate, localDevModeValues.color, localDevModeValues.duplex, localDevModeValues.orient, localDevModeValues.paper, localDevModeValues.bin, localDevModeValues.xres_quality, localDevModeValues.yres);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2145 */     if (bool) {
/* 2146 */       return paramPrintRequestAttributeSet;
/*      */     }
/* 2148 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private final void setResolutionDPI(int paramInt1, int paramInt2)
/*      */   {
/* 2154 */     if (this.attributes != null) {
/* 2155 */       PrinterResolution localPrinterResolution = new PrinterResolution(paramInt1, paramInt2, 100);
/*      */       
/* 2157 */       this.attributes.add(localPrinterResolution);
/*      */     }
/* 2159 */     this.mAttXRes = paramInt1;
/* 2160 */     this.mAttYRes = paramInt2;
/*      */   }
/*      */   
/*      */   private void setResolutionAttrib(Attribute paramAttribute) {
/* 2164 */     PrinterResolution localPrinterResolution = (PrinterResolution)paramAttribute;
/* 2165 */     this.mAttXRes = localPrinterResolution.getCrossFeedResolution(100);
/* 2166 */     this.mAttYRes = localPrinterResolution.getFeedResolution(100);
/*      */   }
/*      */   
/*      */   private void setPrinterNameAttrib(String paramString) {
/* 2170 */     PrintService localPrintService = getPrintService();
/*      */     
/* 2172 */     if (paramString == null) {
/* 2173 */       return;
/*      */     }
/*      */     
/* 2176 */     if ((localPrintService != null) && (paramString.equals(localPrintService.getName()))) {
/* 2177 */       return;
/*      */     }
/* 2179 */     PrintService[] arrayOfPrintService = PrinterJob.lookupPrintServices();
/* 2180 */     for (int i = 0; i < arrayOfPrintService.length; i++)
/* 2181 */       if (paramString.equals(arrayOfPrintService[i].getName()))
/*      */       {
/*      */         try {
/* 2184 */           setPrintService(arrayOfPrintService[i]);
/*      */         }
/*      */         catch (PrinterException localPrinterException) {}
/* 2187 */         return;
/*      */       } }
/*      */   
/*      */   private native void setNativePrintService(String paramString) throws PrinterException;
/*      */   
/*      */   private native String getNativePrintService();
/*      */   
/*      */   private native void getDefaultPage(PageFormat paramPageFormat);
/*      */   
/*      */   protected native void validatePaper(Paper paramPaper1, Paper paramPaper2);
/*      */   
/* 2198 */   class PrintToFileErrorDialog extends Dialog implements ActionListener { public PrintToFileErrorDialog(Frame paramFrame, String paramString1, String paramString2, String paramString3) { super(paramString1, true);
/* 2199 */       init(paramFrame, paramString1, paramString2, paramString3);
/*      */     }
/*      */     
/*      */     public PrintToFileErrorDialog(Dialog paramDialog, String paramString1, String paramString2, String paramString3)
/*      */     {
/* 2204 */       super(paramString1, true);
/* 2205 */       init(paramDialog, paramString1, paramString2, paramString3);
/*      */     }
/*      */     
/*      */     private void init(Component paramComponent, String paramString1, String paramString2, String paramString3)
/*      */     {
/* 2210 */       Panel localPanel = new Panel();
/* 2211 */       add("Center", new Label(paramString2));
/* 2212 */       Button localButton = new Button(paramString3);
/* 2213 */       localButton.addActionListener(this);
/* 2214 */       localPanel.add(localButton);
/* 2215 */       add("South", localPanel);
/* 2216 */       pack();
/*      */       
/* 2218 */       Dimension localDimension = getSize();
/* 2219 */       if (paramComponent != null) {
/* 2220 */         Rectangle localRectangle = paramComponent.getBounds();
/* 2221 */         setLocation(localRectangle.x + (localRectangle.width - localDimension.width) / 2, localRectangle.y + (localRectangle.height - localDimension.height) / 2);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void actionPerformed(ActionEvent paramActionEvent)
/*      */     {
/* 2228 */       setVisible(false);
/* 2229 */       dispose();
/*      */     }
/*      */   }
/*      */   
/*      */   private native void setNativeCopies(int paramInt);
/*      */   
/*      */   private native boolean jobSetup(Pageable paramPageable, boolean paramBoolean);
/*      */   
/*      */   protected native void initPrinter();
/*      */   
/*      */   private native boolean _startDoc(String paramString1, String paramString2)
/*      */     throws PrinterException;
/*      */   
/*      */   protected native void endDoc();
/*      */   
/*      */   protected native void abortDoc();
/*      */   
/*      */   private static native void deleteDC(long paramLong1, long paramLong2, long paramLong3);
/*      */   
/*      */   protected native void deviceStartPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean);
/*      */   
/*      */   protected native void deviceEndPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt);
/*      */   
/*      */   protected native void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*      */   
/*      */   protected native void beginPath(long paramLong);
/*      */   
/*      */   protected native void endPath(long paramLong);
/*      */   
/*      */   protected native void closeFigure(long paramLong);
/*      */   
/*      */   protected native void fillPath(long paramLong);
/*      */   
/*      */   protected native void moveTo(long paramLong, float paramFloat1, float paramFloat2);
/*      */   
/*      */   protected native void lineTo(long paramLong, float paramFloat1, float paramFloat2);
/*      */   
/*      */   protected native void polyBezierTo(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);
/*      */   
/*      */   protected native void setPolyFillMode(long paramLong, int paramInt);
/*      */   
/*      */   protected native void selectSolidBrush(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*      */   
/*      */   protected native int getPenX(long paramLong);
/*      */   
/*      */   protected native int getPenY(long paramLong);
/*      */   
/*      */   protected native void selectClipPath(long paramLong);
/*      */   
/*      */   protected native void frameRect(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
/*      */   
/*      */   protected native void fillRect(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3);
/*      */   
/*      */   protected native void selectPen(long paramLong, float paramFloat, int paramInt1, int paramInt2, int paramInt3);
/*      */   
/*      */   protected native boolean selectStylePen(long paramLong1, long paramLong2, long paramLong3, float paramFloat, int paramInt1, int paramInt2, int paramInt3);
/*      */   
/*      */   protected native boolean setFont(long paramLong, String paramString, float paramFloat1, boolean paramBoolean1, boolean paramBoolean2, int paramInt, float paramFloat2);
/*      */   
/*      */   protected native void setTextColor(long paramLong, int paramInt1, int paramInt2, int paramInt3);
/*      */   
/*      */   protected native void textOut(long paramLong, String paramString, int paramInt, boolean paramBoolean, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat);
/*      */   
/*      */   private native int getGDIAdvance(long paramLong, String paramString);
/*      */   
/*      */   private native void drawDIBImage(long paramLong, byte[] paramArrayOfByte1, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt, byte[] paramArrayOfByte2);
/*      */   
/*      */   private native boolean showDocProperties(long paramLong, PrintRequestAttributeSet paramPrintRequestAttributeSet, int paramInt, short paramShort1, short paramShort2, short paramShort3, short paramShort4, short paramShort5, short paramShort6, short paramShort7, short paramShort8, short paramShort9);
/*      */   
/*      */   private static native void initIDs();
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WPrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
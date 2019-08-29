/*      */ package sun.print;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GraphicsDevice;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Point2D.Double;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.Rectangle2D.Double;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.print.Book;
/*      */ import java.awt.print.PageFormat;
/*      */ import java.awt.print.Pageable;
/*      */ import java.awt.print.Paper;
/*      */ import java.awt.print.Printable;
/*      */ import java.awt.print.PrinterAbortException;
/*      */ import java.awt.print.PrinterException;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.io.File;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.net.URI;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Locale;
/*      */ import javax.print.DocFlavor.SERVICE_FORMATTED;
/*      */ import javax.print.DocPrintJob;
/*      */ import javax.print.PrintException;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.PrintServiceLookup;
/*      */ import javax.print.ServiceUI;
/*      */ import javax.print.StreamPrintService;
/*      */ import javax.print.StreamPrintServiceFactory;
/*      */ import javax.print.attribute.Attribute;
/*      */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttributeSet;
/*      */ import javax.print.attribute.standard.Copies;
/*      */ import javax.print.attribute.standard.Destination;
/*      */ import javax.print.attribute.standard.DialogTypeSelection;
/*      */ import javax.print.attribute.standard.Fidelity;
/*      */ import javax.print.attribute.standard.JobName;
/*      */ import javax.print.attribute.standard.JobSheets;
/*      */ import javax.print.attribute.standard.Media;
/*      */ import javax.print.attribute.standard.MediaPrintableArea;
/*      */ import javax.print.attribute.standard.MediaSize;
/*      */ import javax.print.attribute.standard.MediaSize.NA;
/*      */ import javax.print.attribute.standard.MediaSizeName;
/*      */ import javax.print.attribute.standard.OrientationRequested;
/*      */ import javax.print.attribute.standard.PageRanges;
/*      */ import javax.print.attribute.standard.PrinterIsAcceptingJobs;
/*      */ import javax.print.attribute.standard.PrinterState;
/*      */ import javax.print.attribute.standard.PrinterStateReason;
/*      */ import javax.print.attribute.standard.PrinterStateReasons;
/*      */ import javax.print.attribute.standard.RequestingUserName;
/*      */ import javax.print.attribute.standard.SheetCollate;
/*      */ import javax.print.attribute.standard.Sides;
/*      */ import sun.awt.image.ByteInterleavedRaster;
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
/*      */ public abstract class RasterPrinterJob
/*      */   extends PrinterJob
/*      */ {
/*      */   protected static final int PRINTER = 0;
/*      */   protected static final int FILE = 1;
/*      */   protected static final int STREAM = 2;
/*      */   protected static final int MAX_UNKNOWN_PAGES = 9999;
/*      */   protected static final int PD_ALLPAGES = 0;
/*      */   protected static final int PD_SELECTION = 1;
/*      */   protected static final int PD_PAGENUMS = 2;
/*      */   protected static final int PD_NOSELECTION = 4;
/*      */   private static final int MAX_BAND_SIZE = 4194304;
/*      */   private static final float DPI = 72.0F;
/*      */   private static final String FORCE_PIPE_PROP = "sun.java2d.print.pipeline";
/*      */   private static final String FORCE_RASTER = "raster";
/*      */   private static final String FORCE_PDL = "pdl";
/*      */   private static final String SHAPE_TEXT_PROP = "sun.java2d.print.shapetext";
/*  174 */   public static boolean forcePDL = false;
/*  175 */   public static boolean forceRaster = false;
/*  176 */   public static boolean shapeTextProp = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/*  185 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.pipeline"));
/*      */     
/*      */ 
/*  188 */     if (str1 != null) {
/*  189 */       if (str1.equalsIgnoreCase("pdl")) {
/*  190 */         forcePDL = true;
/*  191 */       } else if (str1.equalsIgnoreCase("raster")) {
/*  192 */         forceRaster = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  197 */     String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.shapetext"));
/*      */     
/*      */ 
/*  200 */     if (str2 != null) {
/*  201 */       shapeTextProp = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  210 */   private int cachedBandWidth = 0;
/*  211 */   private int cachedBandHeight = 0;
/*  212 */   private BufferedImage cachedBand = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  217 */   private int mNumCopies = 1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  226 */   private boolean mCollate = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  236 */   private int mFirstPage = -1;
/*  237 */   private int mLastPage = -1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Paper previousPaper;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  254 */   protected Pageable mDocument = new Book();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  259 */   private String mDocName = "Java Printing";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  266 */   protected boolean performingPrinting = false;
/*      */   
/*  268 */   protected boolean userCancelled = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private FilePermission printToFilePermission;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  278 */   private ArrayList redrawList = new ArrayList();
/*      */   
/*      */   private int copiesAttr;
/*      */   
/*      */   private String jobNameAttr;
/*      */   
/*      */   private String userNameAttr;
/*      */   
/*      */   private PageRanges pageRangesAttr;
/*      */   
/*      */   protected Sides sidesAttr;
/*      */   protected String destinationAttr;
/*  290 */   protected boolean noJobSheet = false;
/*  291 */   protected int mDestType = 1;
/*  292 */   protected String mDestination = "";
/*  293 */   protected boolean collateAttReq = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  298 */   protected boolean landscapeRotates270 = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  304 */   protected PrintRequestAttributeSet attributes = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected PrintService myService;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void saveState(AffineTransform paramAffineTransform, Shape paramShape, Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2)
/*      */   {
/*  421 */     GraphicsState localGraphicsState = new GraphicsState(null);
/*  422 */     localGraphicsState.theTransform = paramAffineTransform;
/*  423 */     localGraphicsState.theClip = paramShape;
/*  424 */     localGraphicsState.region = paramRectangle2D;
/*  425 */     localGraphicsState.sx = paramDouble1;
/*  426 */     localGraphicsState.sy = paramDouble2;
/*  427 */     this.redrawList.add(localGraphicsState);
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
/*      */   protected static PrintService lookupDefaultPrintService()
/*      */   {
/*  440 */     PrintService localPrintService = PrintServiceLookup.lookupDefaultPrintService();
/*      */     
/*      */ 
/*  443 */     if ((localPrintService != null) && 
/*  444 */       (localPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE)))
/*      */     {
/*  446 */       if (localPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE))
/*      */       {
/*  448 */         return localPrintService;
/*      */       }
/*      */     }
/*  451 */     PrintService[] arrayOfPrintService = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
/*      */     
/*  453 */     if (arrayOfPrintService.length > 0) {
/*  454 */       return arrayOfPrintService[0];
/*      */     }
/*      */     
/*  457 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PrintService getPrintService()
/*      */   {
/*  468 */     if (this.myService == null) {
/*  469 */       PrintService localPrintService = PrintServiceLookup.lookupDefaultPrintService();
/*  470 */       if ((localPrintService != null) && 
/*  471 */         (localPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE))) {
/*      */         try
/*      */         {
/*  474 */           setPrintService(localPrintService);
/*  475 */           this.myService = localPrintService;
/*      */         }
/*      */         catch (PrinterException localPrinterException1) {}
/*      */       }
/*  479 */       if (this.myService == null) {
/*  480 */         PrintService[] arrayOfPrintService = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
/*      */         
/*  482 */         if (arrayOfPrintService.length > 0) {
/*      */           try {
/*  484 */             setPrintService(arrayOfPrintService[0]);
/*  485 */             this.myService = arrayOfPrintService[0];
/*      */           }
/*      */           catch (PrinterException localPrinterException2) {}
/*      */         }
/*      */       }
/*      */     }
/*  491 */     return this.myService;
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
/*      */   public void setPrintService(PrintService paramPrintService)
/*      */     throws PrinterException
/*      */   {
/*  507 */     if (paramPrintService == null)
/*  508 */       throw new PrinterException("Service cannot be null");
/*  509 */     if ((!(paramPrintService instanceof StreamPrintService)) && 
/*  510 */       (paramPrintService.getName() == null)) {
/*  511 */       throw new PrinterException("Null PrintService name.");
/*      */     }
/*      */     
/*      */ 
/*  515 */     PrinterState localPrinterState = (PrinterState)paramPrintService.getAttribute(PrinterState.class);
/*      */     
/*  517 */     if (localPrinterState == PrinterState.STOPPED)
/*      */     {
/*  519 */       PrinterStateReasons localPrinterStateReasons = (PrinterStateReasons)paramPrintService.getAttribute(PrinterStateReasons.class);
/*      */       
/*  521 */       if ((localPrinterStateReasons != null) && 
/*  522 */         (localPrinterStateReasons.containsKey(PrinterStateReason.SHUTDOWN)))
/*      */       {
/*  524 */         throw new PrinterException("PrintService is no longer available.");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  529 */     if (paramPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE))
/*      */     {
/*  531 */       if (paramPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE))
/*      */       {
/*  533 */         this.myService = paramPrintService; return;
/*      */       } }
/*  535 */     throw new PrinterException("Not a 2D print service: " + paramPrintService);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private PageFormat attributeToPageFormat(PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/*  542 */     PageFormat localPageFormat = defaultPage();
/*      */     
/*  544 */     if (paramPrintService == null) {
/*  545 */       return localPageFormat;
/*      */     }
/*      */     
/*      */ 
/*  549 */     OrientationRequested localOrientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
/*  550 */     if (localOrientationRequested == null)
/*      */     {
/*  552 */       localOrientationRequested = (OrientationRequested)paramPrintService.getDefaultAttributeValue(OrientationRequested.class);
/*      */     }
/*  554 */     if (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
/*  555 */       localPageFormat.setOrientation(2);
/*  556 */     } else if (localOrientationRequested == OrientationRequested.LANDSCAPE) {
/*  557 */       localPageFormat.setOrientation(0);
/*      */     } else {
/*  559 */       localPageFormat.setOrientation(1);
/*      */     }
/*      */     
/*  562 */     Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
/*  563 */     MediaSize localMediaSize = getMediaSize(localMedia, paramPrintService, localPageFormat);
/*      */     
/*  565 */     Paper localPaper = new Paper();
/*  566 */     float[] arrayOfFloat = localMediaSize.getSize(1);
/*  567 */     double d1 = Math.rint(arrayOfFloat[0] * 72.0D / 25400.0D);
/*  568 */     double d2 = Math.rint(arrayOfFloat[1] * 72.0D / 25400.0D);
/*  569 */     localPaper.setSize(d1, d2);
/*      */     
/*      */ 
/*  572 */     MediaPrintableArea localMediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
/*  573 */     if (localMediaPrintableArea == null) {
/*  574 */       localMediaPrintableArea = getDefaultPrintableArea(localPageFormat, d1, d2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  580 */     double d3 = Math.rint(localMediaPrintableArea
/*  581 */       .getX(25400) * 72.0F);
/*  582 */     double d5 = Math.rint(localMediaPrintableArea
/*  583 */       .getY(25400) * 72.0F);
/*  584 */     double d4 = Math.rint(localMediaPrintableArea
/*  585 */       .getWidth(25400) * 72.0F);
/*  586 */     double d6 = Math.rint(localMediaPrintableArea
/*  587 */       .getHeight(25400) * 72.0F);
/*  588 */     localPaper.setImageableArea(d3, d5, d4, d6);
/*  589 */     localPageFormat.setPaper(localPaper);
/*  590 */     return localPageFormat;
/*      */   }
/*      */   
/*      */   protected MediaSize getMediaSize(Media paramMedia, PrintService paramPrintService, PageFormat paramPageFormat) {
/*  594 */     if (paramMedia == null) {
/*  595 */       paramMedia = (Media)paramPrintService.getDefaultAttributeValue(Media.class);
/*      */     }
/*  597 */     if (!(paramMedia instanceof MediaSizeName)) {
/*  598 */       paramMedia = MediaSizeName.NA_LETTER;
/*      */     }
/*  600 */     MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)paramMedia);
/*  601 */     return localMediaSize != null ? localMediaSize : NA.LETTER;
/*      */   }
/*      */   
/*      */   protected MediaPrintableArea getDefaultPrintableArea(PageFormat paramPageFormat, double paramDouble1, double paramDouble2) {
/*      */     double d1;
/*      */     double d2;
/*  607 */     if (paramDouble1 >= 432.0D) {
/*  608 */       d1 = 72.0D;
/*  609 */       d2 = paramDouble1 - 144.0D;
/*      */     } else {
/*  611 */       d1 = paramDouble1 / 6.0D;
/*  612 */       d2 = paramDouble1 * 0.75D; }
/*      */     double d3;
/*  614 */     double d4; if (paramDouble2 >= 432.0D) {
/*  615 */       d3 = 72.0D;
/*  616 */       d4 = paramDouble2 - 144.0D;
/*      */     } else {
/*  618 */       d3 = paramDouble2 / 6.0D;
/*  619 */       d4 = paramDouble2 * 0.75D;
/*      */     }
/*      */     
/*  622 */     return new MediaPrintableArea((float)(d1 / 72.0D), (float)(d3 / 72.0D), (float)(d2 / 72.0D), (float)(d4 / 72.0D), 25400);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updatePageAttributes(PrintService paramPrintService, PageFormat paramPageFormat)
/*      */   {
/*  628 */     if (this.attributes == null) {
/*  629 */       this.attributes = new HashPrintRequestAttributeSet();
/*      */     }
/*      */     
/*  632 */     updateAttributesWithPageFormat(paramPrintService, paramPageFormat, this.attributes);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateAttributesWithPageFormat(PrintService paramPrintService, PageFormat paramPageFormat, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/*  638 */     if ((paramPrintService == null) || (paramPageFormat == null) || (paramPrintRequestAttributeSet == null)) {
/*  639 */       return;
/*      */     }
/*      */     
/*  642 */     float f1 = (float)Math.rint(paramPageFormat
/*  643 */       .getPaper().getWidth() * 25400.0D / 72.0D) / 25400.0F;
/*      */     
/*  645 */     float f2 = (float)Math.rint(paramPageFormat
/*  646 */       .getPaper().getHeight() * 25400.0D / 72.0D) / 25400.0F;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  653 */     Media[] arrayOfMedia = (Media[])paramPrintService.getSupportedAttributeValues(Media.class, null, null);
/*      */     
/*  655 */     Object localObject = null;
/*      */     try {
/*  657 */       localObject = CustomMediaSizeName.findMedia(arrayOfMedia, f1, f2, 25400);
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException1) {}
/*      */     
/*  661 */     if ((localObject == null) || 
/*  662 */       (!paramPrintService.isAttributeValueSupported((Attribute)localObject, null, null))) {
/*  663 */       localObject = (Media)paramPrintService.getDefaultAttributeValue(Media.class);
/*      */     }
/*      */     
/*      */     OrientationRequested localOrientationRequested;
/*  667 */     switch (paramPageFormat.getOrientation()) {
/*      */     case 0: 
/*  669 */       localOrientationRequested = OrientationRequested.LANDSCAPE;
/*  670 */       break;
/*      */     case 2: 
/*  672 */       localOrientationRequested = OrientationRequested.REVERSE_LANDSCAPE;
/*  673 */       break;
/*      */     default: 
/*  675 */       localOrientationRequested = OrientationRequested.PORTRAIT;
/*      */     }
/*      */     
/*  678 */     if (localObject != null) {
/*  679 */       paramPrintRequestAttributeSet.add((Attribute)localObject);
/*      */     }
/*  681 */     paramPrintRequestAttributeSet.add(localOrientationRequested);
/*      */     
/*  683 */     float f3 = (float)(paramPageFormat.getPaper().getImageableX() / 72.0D);
/*  684 */     float f4 = (float)(paramPageFormat.getPaper().getImageableWidth() / 72.0D);
/*  685 */     float f5 = (float)(paramPageFormat.getPaper().getImageableY() / 72.0D);
/*  686 */     float f6 = (float)(paramPageFormat.getPaper().getImageableHeight() / 72.0D);
/*  687 */     if (f3 < 0.0F) f3 = 0.0F; if (f5 < 0.0F) f5 = 0.0F;
/*      */     try {
/*  689 */       paramPrintRequestAttributeSet.add(new MediaPrintableArea(f3, f5, f4, f6, 25400));
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException2) {}
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
/*      */   public PageFormat pageDialog(PageFormat paramPageFormat)
/*      */     throws HeadlessException
/*      */   {
/*  719 */     if (GraphicsEnvironment.isHeadless()) {
/*  720 */       throw new HeadlessException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  725 */     final GraphicsConfiguration localGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
/*      */     
/*      */ 
/*  728 */     PrintService localPrintService = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  731 */         PrintService localPrintService = RasterPrinterJob.this.getPrintService();
/*  732 */         if (localPrintService == null) {
/*  733 */           ServiceDialog.showNoPrintService(localGraphicsConfiguration);
/*  734 */           return null;
/*      */         }
/*  736 */         return localPrintService;
/*      */       }
/*      */     });
/*      */     
/*  740 */     if (localPrintService == null) {
/*  741 */       return paramPageFormat;
/*      */     }
/*  743 */     updatePageAttributes(localPrintService, paramPageFormat);
/*      */     
/*  745 */     PageFormat localPageFormat = null;
/*      */     
/*  747 */     DialogTypeSelection localDialogTypeSelection = (DialogTypeSelection)this.attributes.get(DialogTypeSelection.class);
/*  748 */     if (localDialogTypeSelection == DialogTypeSelection.NATIVE)
/*      */     {
/*      */ 
/*  751 */       this.attributes.remove(DialogTypeSelection.class);
/*  752 */       localPageFormat = pageDialog(this.attributes);
/*      */       
/*  754 */       this.attributes.add(DialogTypeSelection.NATIVE);
/*      */     } else {
/*  756 */       localPageFormat = pageDialog(this.attributes);
/*      */     }
/*      */     
/*  759 */     if (localPageFormat == null) {
/*  760 */       return paramPageFormat;
/*      */     }
/*  762 */     return localPageFormat;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PageFormat pageDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws HeadlessException
/*      */   {
/*  772 */     if (GraphicsEnvironment.isHeadless()) {
/*  773 */       throw new HeadlessException();
/*      */     }
/*      */     
/*      */ 
/*  777 */     DialogTypeSelection localDialogTypeSelection = (DialogTypeSelection)paramPrintRequestAttributeSet.get(DialogTypeSelection.class);
/*      */     
/*      */ 
/*  780 */     if (localDialogTypeSelection == DialogTypeSelection.NATIVE) {
/*  781 */       localObject1 = getPrintService();
/*  782 */       localObject2 = attributeToPageFormat((PrintService)localObject1, paramPrintRequestAttributeSet);
/*      */       
/*  784 */       setParentWindowID(paramPrintRequestAttributeSet);
/*  785 */       PageFormat localPageFormat = pageDialog((PageFormat)localObject2);
/*  786 */       clearParentWindowID();
/*      */       
/*      */ 
/*      */ 
/*  790 */       if (localPageFormat == localObject2) {
/*  791 */         return null;
/*      */       }
/*  793 */       updateAttributesWithPageFormat((PrintService)localObject1, localPageFormat, paramPrintRequestAttributeSet);
/*  794 */       return localPageFormat;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  799 */     final Object localObject1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
/*  800 */     Object localObject2 = ((GraphicsConfiguration)localObject1).getBounds();
/*  801 */     int i = ((Rectangle)localObject2).x + ((Rectangle)localObject2).width / 3;
/*  802 */     int j = ((Rectangle)localObject2).y + ((Rectangle)localObject2).height / 3;
/*      */     
/*      */ 
/*  805 */     PrintService localPrintService = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  808 */         PrintService localPrintService = RasterPrinterJob.this.getPrintService();
/*  809 */         if (localPrintService == null) {
/*  810 */           ServiceDialog.showNoPrintService(localObject1);
/*  811 */           return null;
/*      */         }
/*  813 */         return localPrintService;
/*      */       }
/*      */     });
/*      */     
/*  817 */     if (localPrintService == null) {
/*  818 */       return null;
/*      */     }
/*      */     
/*  821 */     if (this.onTop != null) {
/*  822 */       paramPrintRequestAttributeSet.add(this.onTop);
/*      */     }
/*      */     
/*  825 */     ServiceDialog localServiceDialog = new ServiceDialog((GraphicsConfiguration)localObject1, i, j, localPrintService, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet, (Frame)null);
/*      */     
/*      */ 
/*  828 */     localServiceDialog.show();
/*      */     
/*  830 */     if (localServiceDialog.getStatus() == 1)
/*      */     {
/*  832 */       PrintRequestAttributeSet localPrintRequestAttributeSet = localServiceDialog.getAttributes();
/*  833 */       Class localClass = SunAlternateMedia.class;
/*      */       
/*  835 */       if ((paramPrintRequestAttributeSet.containsKey(localClass)) && 
/*  836 */         (!localPrintRequestAttributeSet.containsKey(localClass))) {
/*  837 */         paramPrintRequestAttributeSet.remove(localClass);
/*      */       }
/*  839 */       paramPrintRequestAttributeSet.addAll(localPrintRequestAttributeSet);
/*  840 */       return attributeToPageFormat(localPrintService, paramPrintRequestAttributeSet);
/*      */     }
/*  842 */     return null;
/*      */   }
/*      */   
/*      */   protected PageFormat getPageFormatFromAttributes()
/*      */   {
/*  847 */     if ((this.attributes == null) || (this.attributes.isEmpty())) {
/*  848 */       return null;
/*      */     }
/*      */     
/*  851 */     PageFormat localPageFormat1 = attributeToPageFormat(
/*  852 */       getPrintService(), this.attributes);
/*  853 */     PageFormat localPageFormat2 = null;
/*  854 */     Pageable localPageable = getPageable();
/*  855 */     if ((localPageable != null) && ((localPageable instanceof OpenBook)))
/*      */     {
/*  857 */       if ((localPageFormat2 = localPageable.getPageFormat(0)) != null)
/*      */       {
/*      */ 
/*      */ 
/*  861 */         if (this.attributes.get(OrientationRequested.class) == null) {
/*  862 */           localPageFormat1.setOrientation(localPageFormat2.getOrientation());
/*      */         }
/*      */         
/*  865 */         Paper localPaper1 = localPageFormat1.getPaper();
/*  866 */         Paper localPaper2 = localPageFormat2.getPaper();
/*  867 */         int i = 0;
/*  868 */         if (this.attributes.get(MediaSizeName.class) == null) {
/*  869 */           localPaper1.setSize(localPaper2.getWidth(), localPaper2.getHeight());
/*  870 */           i = 1;
/*      */         }
/*  872 */         if (this.attributes.get(MediaPrintableArea.class) == null) {
/*  873 */           localPaper1.setImageableArea(localPaper2
/*  874 */             .getImageableX(), localPaper2.getImageableY(), localPaper2
/*  875 */             .getImageableWidth(), localPaper2
/*  876 */             .getImageableHeight());
/*  877 */           i = 1;
/*      */         }
/*  879 */         if (i != 0)
/*  880 */           localPageFormat1.setPaper(localPaper1);
/*      */       }
/*      */     }
/*  883 */     return localPageFormat1;
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
/*      */   public boolean printDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws HeadlessException
/*      */   {
/*  904 */     if (GraphicsEnvironment.isHeadless()) {
/*  905 */       throw new HeadlessException();
/*      */     }
/*      */     
/*      */ 
/*  909 */     DialogTypeSelection localDialogTypeSelection = (DialogTypeSelection)paramPrintRequestAttributeSet.get(DialogTypeSelection.class);
/*      */     
/*      */ 
/*  912 */     if (localDialogTypeSelection == DialogTypeSelection.NATIVE) {
/*  913 */       this.attributes = paramPrintRequestAttributeSet;
/*      */       try {
/*  915 */         debug_println("calling setAttributes in printDialog");
/*  916 */         setAttributes(paramPrintRequestAttributeSet);
/*      */       }
/*      */       catch (PrinterException localPrinterException1) {}
/*      */       
/*      */ 
/*      */ 
/*  922 */       setParentWindowID(paramPrintRequestAttributeSet);
/*  923 */       boolean bool = printDialog();
/*  924 */       clearParentWindowID();
/*  925 */       this.attributes = paramPrintRequestAttributeSet;
/*  926 */       return bool;
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
/*  941 */     final GraphicsConfiguration localGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
/*      */     
/*      */ 
/*  944 */     PrintService localPrintService1 = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  947 */         PrintService localPrintService = RasterPrinterJob.this.getPrintService();
/*  948 */         if (localPrintService == null) {
/*  949 */           ServiceDialog.showNoPrintService(localGraphicsConfiguration);
/*  950 */           return null;
/*      */         }
/*  952 */         return localPrintService;
/*      */       }
/*      */     });
/*      */     
/*  956 */     if (localPrintService1 == null) {
/*  957 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  961 */     StreamPrintServiceFactory[] arrayOfStreamPrintServiceFactory = null;
/*  962 */     Object localObject; if ((localPrintService1 instanceof StreamPrintService)) {
/*  963 */       arrayOfStreamPrintServiceFactory = lookupStreamPrintServices(null);
/*  964 */       localObject = new StreamPrintService[arrayOfStreamPrintServiceFactory.length];
/*  965 */       for (int i = 0; i < arrayOfStreamPrintServiceFactory.length; i++) {
/*  966 */         localObject[i] = arrayOfStreamPrintServiceFactory[i].getPrintService(null);
/*      */       }
/*      */     }
/*      */     else {
/*  970 */       localObject = (PrintService[])AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run() {
/*  973 */           PrintService[] arrayOfPrintService = PrinterJob.lookupPrintServices();
/*  974 */           return arrayOfPrintService;
/*      */         }
/*      */       });
/*      */       
/*  978 */       if ((localObject == null) || (localObject.length == 0))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  983 */         localObject = new PrintService[1];
/*  984 */         localObject[0] = localPrintService1;
/*      */       }
/*      */     }
/*      */     
/*  988 */     Rectangle localRectangle = localGraphicsConfiguration.getBounds();
/*  989 */     int j = localRectangle.x + localRectangle.width / 3;
/*  990 */     int k = localRectangle.y + localRectangle.height / 3;
/*      */     
/*      */ 
/*  993 */     PrinterJobWrapper localPrinterJobWrapper = new PrinterJobWrapper(this);
/*  994 */     paramPrintRequestAttributeSet.add(localPrinterJobWrapper);
/*      */     PrintService localPrintService2;
/*      */     try {
/*  997 */       localPrintService2 = ServiceUI.printDialog(localGraphicsConfiguration, j, k, (PrintService[])localObject, localPrintService1, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
/*      */ 
/*      */     }
/*      */     catch (IllegalArgumentException localIllegalArgumentException)
/*      */     {
/* 1002 */       localPrintService2 = ServiceUI.printDialog(localGraphicsConfiguration, j, k, (PrintService[])localObject, localObject[0], DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1007 */     paramPrintRequestAttributeSet.remove(PrinterJobWrapper.class);
/*      */     
/* 1009 */     if (localPrintService2 == null) {
/* 1010 */       return false;
/*      */     }
/*      */     
/* 1013 */     if (!localPrintService1.equals(localPrintService2)) {
/*      */       try {
/* 1015 */         setPrintService(localPrintService2);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (PrinterException localPrinterException2)
/*      */       {
/*      */ 
/* 1022 */         this.myService = localPrintService2;
/*      */       }
/*      */     }
/* 1025 */     return true;
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
/*      */   public boolean printDialog()
/*      */     throws HeadlessException
/*      */   {
/* 1039 */     if (GraphicsEnvironment.isHeadless()) {
/* 1040 */       throw new HeadlessException();
/*      */     }
/*      */     
/* 1043 */     HashPrintRequestAttributeSet localHashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
/*      */     
/* 1045 */     localHashPrintRequestAttributeSet.add(new Copies(getCopies()));
/* 1046 */     localHashPrintRequestAttributeSet.add(new JobName(getJobName(), null));
/* 1047 */     boolean bool = printDialog(localHashPrintRequestAttributeSet);
/* 1048 */     if (bool) {
/* 1049 */       JobName localJobName = (JobName)localHashPrintRequestAttributeSet.get(JobName.class);
/* 1050 */       if (localJobName != null) {
/* 1051 */         setJobName(localJobName.getValue());
/*      */       }
/* 1053 */       Copies localCopies = (Copies)localHashPrintRequestAttributeSet.get(Copies.class);
/* 1054 */       if (localCopies != null) {
/* 1055 */         setCopies(localCopies.getValue());
/*      */       }
/*      */       
/* 1058 */       Destination localDestination1 = (Destination)localHashPrintRequestAttributeSet.get(Destination.class);
/*      */       
/* 1060 */       if (localDestination1 != null) {
/*      */         try {
/* 1062 */           this.mDestType = 1;
/* 1063 */           this.mDestination = new File(localDestination1.getURI()).getPath();
/*      */         } catch (Exception localException) {
/* 1065 */           this.mDestination = "out.prn";
/* 1066 */           PrintService localPrintService2 = getPrintService();
/* 1067 */           if (localPrintService2 != null)
/*      */           {
/* 1069 */             Destination localDestination2 = (Destination)localPrintService2.getDefaultAttributeValue(Destination.class);
/* 1070 */             if (localDestination2 != null) {
/* 1071 */               this.mDestination = new File(localDestination2.getURI()).getPath();
/*      */             }
/*      */           }
/*      */         }
/*      */       } else {
/* 1076 */         this.mDestType = 0;
/* 1077 */         PrintService localPrintService1 = getPrintService();
/* 1078 */         if (localPrintService1 != null) {
/* 1079 */           this.mDestination = localPrintService1.getName();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1084 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPrintable(Printable paramPrintable)
/*      */   {
/* 1094 */     setPageable(new OpenBook(defaultPage(new PageFormat()), paramPrintable));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPrintable(Printable paramPrintable, PageFormat paramPageFormat)
/*      */   {
/* 1106 */     setPageable(new OpenBook(paramPageFormat, paramPrintable));
/* 1107 */     updatePageAttributes(getPrintService(), paramPageFormat);
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
/*      */   public void setPageable(Pageable paramPageable)
/*      */     throws NullPointerException
/*      */   {
/* 1121 */     if (paramPageable != null) {
/* 1122 */       this.mDocument = paramPageable;
/*      */     }
/*      */     else {
/* 1125 */       throw new NullPointerException();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isSupportedValue(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */   {
/* 1135 */     PrintService localPrintService = getPrintService();
/* 1136 */     if ((paramAttribute != null) && (localPrintService != null)) {} return 
/*      */     
/* 1138 */       localPrintService.isAttributeValueSupported(paramAttribute, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws PrinterException
/*      */   {
/* 1149 */     setCollated(false);
/* 1150 */     this.sidesAttr = null;
/* 1151 */     this.pageRangesAttr = null;
/* 1152 */     this.copiesAttr = 0;
/* 1153 */     this.jobNameAttr = null;
/* 1154 */     this.userNameAttr = null;
/* 1155 */     this.destinationAttr = null;
/* 1156 */     this.collateAttReq = false;
/*      */     
/* 1158 */     PrintService localPrintService = getPrintService();
/* 1159 */     if ((paramPrintRequestAttributeSet == null) || (localPrintService == null)) {
/* 1160 */       return;
/*      */     }
/*      */     
/* 1163 */     int i = 0;
/* 1164 */     Fidelity localFidelity = (Fidelity)paramPrintRequestAttributeSet.get(Fidelity.class);
/* 1165 */     if ((localFidelity != null) && (localFidelity == Fidelity.FIDELITY_TRUE)) {
/* 1166 */       i = 1;
/*      */     }
/*      */     
/* 1169 */     if (i == 1)
/*      */     {
/* 1171 */       localObject1 = localPrintService.getUnsupportedAttributes(DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
/*      */       
/*      */ 
/* 1174 */       if (localObject1 != null) {
/* 1175 */         throw new PrinterException("Fidelity cannot be satisfied");
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
/* 1186 */     Object localObject1 = (SheetCollate)paramPrintRequestAttributeSet.get(SheetCollate.class);
/* 1187 */     if (isSupportedValue((Attribute)localObject1, paramPrintRequestAttributeSet)) {
/* 1188 */       setCollated(localObject1 == SheetCollate.COLLATED);
/*      */     }
/*      */     
/* 1191 */     this.sidesAttr = ((Sides)paramPrintRequestAttributeSet.get(Sides.class));
/* 1192 */     if (!isSupportedValue(this.sidesAttr, paramPrintRequestAttributeSet)) {
/* 1193 */       this.sidesAttr = Sides.ONE_SIDED;
/*      */     }
/*      */     
/* 1196 */     this.pageRangesAttr = ((PageRanges)paramPrintRequestAttributeSet.get(PageRanges.class));
/* 1197 */     if (!isSupportedValue(this.pageRangesAttr, paramPrintRequestAttributeSet)) {
/* 1198 */       this.pageRangesAttr = null;
/*      */     }
/* 1200 */     else if ((SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class) == SunPageSelection.RANGE)
/*      */     {
/*      */ 
/* 1203 */       localObject2 = this.pageRangesAttr.getMembers();
/*      */       
/* 1205 */       setPageRange(localObject2[0][0] - 1, localObject2[0][1] - 1);
/*      */     } else {
/* 1207 */       setPageRange(-1, -1);
/*      */     }
/*      */     
/*      */ 
/* 1211 */     Object localObject2 = (Copies)paramPrintRequestAttributeSet.get(Copies.class);
/* 1212 */     if ((isSupportedValue((Attribute)localObject2, paramPrintRequestAttributeSet)) || ((i == 0) && (localObject2 != null)))
/*      */     {
/* 1214 */       this.copiesAttr = ((Copies)localObject2).getValue();
/* 1215 */       setCopies(this.copiesAttr);
/*      */     } else {
/* 1217 */       this.copiesAttr = getCopies();
/*      */     }
/*      */     
/*      */ 
/* 1221 */     Destination localDestination = (Destination)paramPrintRequestAttributeSet.get(Destination.class);
/*      */     
/* 1223 */     if (isSupportedValue(localDestination, paramPrintRequestAttributeSet))
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/* 1229 */         this.destinationAttr = ("" + new File(localDestination.getURI().getSchemeSpecificPart()));
/*      */       }
/*      */       catch (Exception localException) {
/* 1232 */         localObject3 = (Destination)localPrintService.getDefaultAttributeValue(Destination.class);
/* 1233 */         if (localObject3 != null)
/*      */         {
/* 1235 */           this.destinationAttr = ("" + new File(((Destination)localObject3).getURI().getSchemeSpecificPart()));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1240 */     JobSheets localJobSheets = (JobSheets)paramPrintRequestAttributeSet.get(JobSheets.class);
/* 1241 */     if (localJobSheets != null) {
/* 1242 */       this.noJobSheet = (localJobSheets == JobSheets.NONE);
/*      */     }
/*      */     
/* 1245 */     Object localObject3 = (JobName)paramPrintRequestAttributeSet.get(JobName.class);
/* 1246 */     if ((isSupportedValue((Attribute)localObject3, paramPrintRequestAttributeSet)) || ((i == 0) && (localObject3 != null)))
/*      */     {
/* 1248 */       this.jobNameAttr = ((JobName)localObject3).getValue();
/* 1249 */       setJobName(this.jobNameAttr);
/*      */     } else {
/* 1251 */       this.jobNameAttr = getJobName();
/*      */     }
/*      */     
/*      */ 
/* 1255 */     RequestingUserName localRequestingUserName = (RequestingUserName)paramPrintRequestAttributeSet.get(RequestingUserName.class);
/* 1256 */     if ((isSupportedValue(localRequestingUserName, paramPrintRequestAttributeSet)) || ((i == 0) && (localRequestingUserName != null)))
/*      */     {
/* 1258 */       this.userNameAttr = localRequestingUserName.getValue();
/*      */     } else {
/*      */       try {
/* 1261 */         this.userNameAttr = getUserName();
/*      */       } catch (SecurityException localSecurityException) {
/* 1263 */         this.userNameAttr = "";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1270 */     Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
/*      */     
/* 1272 */     OrientationRequested localOrientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
/*      */     
/* 1274 */     MediaPrintableArea localMediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
/*      */     
/* 1276 */     if (((localOrientationRequested != null) || (localMedia != null) || (localMediaPrintableArea != null)) && 
/* 1277 */       ((getPageable() instanceof OpenBook)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1282 */       Pageable localPageable = getPageable();
/* 1283 */       Printable localPrintable = localPageable.getPrintable(0);
/* 1284 */       PageFormat localPageFormat = (PageFormat)localPageable.getPageFormat(0).clone();
/* 1285 */       Paper localPaper = localPageFormat.getPaper();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1290 */       if ((localMediaPrintableArea == null) && (localMedia != null))
/*      */       {
/* 1292 */         if (localPrintService.isAttributeCategorySupported(MediaPrintableArea.class))
/*      */         {
/* 1294 */           Object localObject4 = localPrintService.getSupportedAttributeValues(MediaPrintableArea.class, null, paramPrintRequestAttributeSet);
/*      */           
/* 1296 */           if (((localObject4 instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject4).length > 0))
/*      */           {
/* 1298 */             localMediaPrintableArea = ((MediaPrintableArea[])(MediaPrintableArea[])localObject4)[0];
/*      */           }
/*      */         }
/*      */       }
/* 1302 */       if ((isSupportedValue(localOrientationRequested, paramPrintRequestAttributeSet)) || ((i == 0) && (localOrientationRequested != null)))
/*      */       {
/*      */         int j;
/* 1305 */         if (localOrientationRequested.equals(OrientationRequested.REVERSE_LANDSCAPE)) {
/* 1306 */           j = 2;
/* 1307 */         } else if (localOrientationRequested.equals(OrientationRequested.LANDSCAPE)) {
/* 1308 */           j = 0;
/*      */         } else {
/* 1310 */           j = 1;
/*      */         }
/* 1312 */         localPageFormat.setOrientation(j);
/*      */       }
/*      */       Object localObject5;
/* 1315 */       if ((isSupportedValue(localMedia, paramPrintRequestAttributeSet)) || ((i == 0) && (localMedia != null)))
/*      */       {
/* 1317 */         if ((localMedia instanceof MediaSizeName)) {
/* 1318 */           localObject5 = (MediaSizeName)localMedia;
/* 1319 */           MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject5);
/* 1320 */           if (localMediaSize != null) {
/* 1321 */             float f1 = localMediaSize.getX(25400) * 72.0F;
/* 1322 */             float f2 = localMediaSize.getY(25400) * 72.0F;
/* 1323 */             localPaper.setSize(f1, f2);
/* 1324 */             if (localMediaPrintableArea == null) {
/* 1325 */               localPaper.setImageableArea(72.0D, 72.0D, f1 - 144.0D, f2 - 144.0D);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1333 */       if ((isSupportedValue(localMediaPrintableArea, paramPrintRequestAttributeSet)) || ((i == 0) && (localMediaPrintableArea != null)))
/*      */       {
/*      */ 
/* 1336 */         localObject5 = localMediaPrintableArea.getPrintableArea(25400);
/* 1337 */         for (int k = 0; k < localObject5.length; k++) {
/* 1338 */           localObject5[k] *= 72.0F;
/*      */         }
/* 1340 */         localPaper.setImageableArea(localObject5[0], localObject5[1], localObject5[2], localObject5[3]);
/*      */       }
/*      */       
/*      */ 
/* 1344 */       localPageFormat.setPaper(localPaper);
/* 1345 */       localPageFormat = validatePage(localPageFormat);
/* 1346 */       setPrintable(localPrintable, localPageFormat);
/*      */     }
/*      */     else
/*      */     {
/* 1350 */       this.attributes = paramPrintRequestAttributeSet;
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
/*      */   protected void spoolToService(PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws PrinterException
/*      */   {
/* 1366 */     if (paramPrintService == null) {
/* 1367 */       throw new PrinterException("No print service found.");
/*      */     }
/*      */     
/* 1370 */     DocPrintJob localDocPrintJob = paramPrintService.createPrintJob();
/* 1371 */     PageableDoc localPageableDoc = new PageableDoc(getPageable());
/* 1372 */     if (paramPrintRequestAttributeSet == null) {
/* 1373 */       paramPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
/*      */     }
/*      */     try {
/* 1376 */       localDocPrintJob.print(localPageableDoc, paramPrintRequestAttributeSet);
/*      */     } catch (PrintException localPrintException) {
/* 1378 */       throw new PrinterException(localPrintException.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void print()
/*      */     throws PrinterException
/*      */   {
/* 1391 */     print(this.attributes);
/*      */   }
/*      */   
/* 1394 */   public static boolean debugPrint = false;
/*      */   
/* 1396 */   protected void debug_println(String paramString) { if (debugPrint) {
/* 1397 */       System.out.println("RasterPrinterJob " + paramString + " " + this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int deviceWidth;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void print(PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     throws PrinterException
/*      */   {
/* 1416 */     PrintService localPrintService = getPrintService();
/* 1417 */     debug_println("psvc = " + localPrintService);
/* 1418 */     if (localPrintService == null) {
/* 1419 */       throw new PrinterException("No print service found.");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1424 */     PrinterState localPrinterState = (PrinterState)localPrintService.getAttribute(PrinterState.class);
/*      */     
/* 1426 */     if (localPrinterState == PrinterState.STOPPED)
/*      */     {
/* 1428 */       PrinterStateReasons localPrinterStateReasons = (PrinterStateReasons)localPrintService.getAttribute(PrinterStateReasons.class);
/*      */       
/* 1430 */       if ((localPrinterStateReasons != null) && 
/* 1431 */         (localPrinterStateReasons.containsKey(PrinterStateReason.SHUTDOWN)))
/*      */       {
/* 1433 */         throw new PrinterException("PrintService is no longer available.");
/*      */       }
/*      */     }
/*      */     
/* 1437 */     if ((PrinterIsAcceptingJobs)localPrintService.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS)
/*      */     {
/*      */ 
/* 1440 */       throw new PrinterException("Printer is not accepting job.");
/*      */     }
/*      */     
/* 1443 */     if (((localPrintService instanceof SunPrinterJobService)) && 
/* 1444 */       (((SunPrinterJobService)localPrintService).usesClass(getClass()))) {
/* 1445 */       setAttributes(paramPrintRequestAttributeSet);
/*      */       
/* 1447 */       if (this.destinationAttr != null) {
/* 1448 */         validateDestination(this.destinationAttr);
/*      */       }
/*      */     } else {
/* 1451 */       spoolToService(localPrintService, paramPrintRequestAttributeSet);
/* 1452 */       return;
/*      */     }
/*      */     
/*      */ 
/* 1456 */     initPrinter();
/*      */     
/* 1458 */     int i = getCollatedCopies();
/* 1459 */     int j = getNoncollatedCopies();
/* 1460 */     debug_println("getCollatedCopies()  " + i + " getNoncollatedCopies() " + j);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1468 */     int k = this.mDocument.getNumberOfPages();
/* 1469 */     if (k == 0) {
/* 1470 */       return;
/*      */     }
/*      */     
/* 1473 */     int m = getFirstPage();
/* 1474 */     int n = getLastPage();
/* 1475 */     if (n == -1) {
/* 1476 */       int i1 = this.mDocument.getNumberOfPages();
/* 1477 */       if (i1 != -1) {
/* 1478 */         n = this.mDocument.getNumberOfPages() - 1;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1483 */       synchronized (this) {
/* 1484 */         this.performingPrinting = true;
/* 1485 */         this.userCancelled = false;
/*      */       }
/*      */       
/* 1488 */       startDoc();
/* 1489 */       if (isCancelled()) {
/* 1490 */         cancelDoc();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1495 */       boolean bool = true;
/* 1496 */       if (paramPrintRequestAttributeSet != null)
/*      */       {
/* 1498 */         SunPageSelection localSunPageSelection = (SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class);
/* 1499 */         if ((localSunPageSelection != null) && (localSunPageSelection != SunPageSelection.RANGE)) {
/* 1500 */           bool = false;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1505 */       debug_println("after startDoc rangeSelected? " + bool + " numNonCollatedCopies " + j);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1522 */       for (int i2 = 0; i2 < i; i2++) {
/* 1523 */         int i3 = m;int i4 = 0;
/* 1526 */         for (; 
/*      */             
/* 1526 */             ((i3 <= n) || (n == -1)) && (i4 == 0); 
/* 1527 */             i3++)
/*      */         {
/*      */           int i5;
/* 1530 */           if ((this.pageRangesAttr != null) && (bool)) {
/* 1531 */             i5 = this.pageRangesAttr.next(i3);
/* 1532 */             if (i5 == -1)
/*      */               break;
/* 1534 */             if (i5 != i3 + 1) {}
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 1539 */             for (i5 = 0; 
/*      */                 
/* 1541 */                 (i5 < j) && (i4 == 0); 
/* 1542 */                 i5++)
/*      */             {
/* 1544 */               if (isCancelled()) {
/* 1545 */                 cancelDoc();
/*      */               }
/* 1547 */               debug_println("printPage " + i3);
/* 1548 */               i4 = printPage(this.mDocument, i3);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1554 */       if (isCancelled()) {
/* 1555 */         cancelDoc();
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1560 */       this.previousPaper = null;
/* 1561 */       synchronized (this) {
/* 1562 */         if (this.performingPrinting) {
/* 1563 */           endDoc();
/*      */         }
/* 1565 */         this.performingPrinting = false;
/* 1566 */         notify();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void validateDestination(String paramString) throws PrinterException {
/* 1572 */     if (paramString == null) {
/* 1573 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1579 */     File localFile1 = new File(paramString);
/*      */     try
/*      */     {
/* 1582 */       if (localFile1.createNewFile()) {
/* 1583 */         localFile1.delete();
/*      */       }
/*      */     } catch (IOException localIOException) {
/* 1586 */       throw new PrinterException("Cannot write to file:" + paramString);
/*      */     }
/*      */     catch (SecurityException localSecurityException) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1595 */     File localFile2 = localFile1.getParentFile();
/* 1596 */     if ((!localFile1.exists()) || (
/* 1597 */       (localFile1.isFile()) && (localFile1.canWrite()))) { if (localFile2 != null)
/*      */       {
/* 1599 */         if ((localFile2.exists()) && ((!localFile2.exists()) || (localFile2.canWrite()))) {} }
/* 1600 */     } else { throw new PrinterException("Cannot write to file:" + paramString);
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
/*      */   protected void validatePaper(Paper paramPaper1, Paper paramPaper2)
/*      */   {
/* 1615 */     if ((paramPaper1 == null) || (paramPaper2 == null)) {
/* 1616 */       return;
/*      */     }
/* 1618 */     double d1 = paramPaper1.getWidth();
/* 1619 */     double d2 = paramPaper1.getHeight();
/* 1620 */     double d3 = paramPaper1.getImageableX();
/* 1621 */     double d4 = paramPaper1.getImageableY();
/* 1622 */     double d5 = paramPaper1.getImageableWidth();
/* 1623 */     double d6 = paramPaper1.getImageableHeight();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1628 */     Paper localPaper = new Paper();
/* 1629 */     d1 = d1 > 0.0D ? d1 : localPaper.getWidth();
/* 1630 */     d2 = d2 > 0.0D ? d2 : localPaper.getHeight();
/* 1631 */     d3 = d3 > 0.0D ? d3 : localPaper.getImageableX();
/* 1632 */     d4 = d4 > 0.0D ? d4 : localPaper.getImageableY();
/* 1633 */     d5 = d5 > 0.0D ? d5 : localPaper.getImageableWidth();
/* 1634 */     d6 = d6 > 0.0D ? d6 : localPaper.getImageableHeight();
/*      */     
/*      */ 
/*      */ 
/* 1638 */     if (d5 > d1) {
/* 1639 */       d5 = d1;
/*      */     }
/* 1641 */     if (d6 > d2) {
/* 1642 */       d6 = d2;
/*      */     }
/* 1644 */     if (d3 + d5 > d1) {
/* 1645 */       d3 = d1 - d5;
/*      */     }
/* 1647 */     if (d4 + d6 > d2) {
/* 1648 */       d4 = d2 - d6;
/*      */     }
/* 1650 */     paramPaper2.setSize(d1, d2);
/* 1651 */     paramPaper2.setImageableArea(d3, d4, d5, d6);
/*      */   }
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
/* 1663 */     PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
/* 1664 */     localPageFormat.setOrientation(1);
/* 1665 */     Paper localPaper = new Paper();
/* 1666 */     double d1 = 72.0D;
/*      */     
/* 1668 */     Media localMedia = null;
/*      */     
/* 1670 */     PrintService localPrintService = getPrintService();
/* 1671 */     double d2; double d3; if (localPrintService != null)
/*      */     {
/*      */ 
/* 1674 */       localMedia = (Media)localPrintService.getDefaultAttributeValue(Media.class);
/*      */       
/* 1676 */       if (((localMedia instanceof MediaSizeName)) && 
/* 1677 */         ((localObject = MediaSize.getMediaSizeForName((MediaSizeName)localMedia)) != null))
/*      */       {
/* 1679 */         d2 = ((MediaSize)localObject).getX(25400) * d1;
/* 1680 */         d3 = ((MediaSize)localObject).getY(25400) * d1;
/* 1681 */         localPaper.setSize(d2, d3);
/* 1682 */         localPaper.setImageableArea(d1, d1, d2 - 2.0D * d1, d3 - 2.0D * d1);
/*      */         
/*      */ 
/* 1685 */         localPageFormat.setPaper(localPaper);
/* 1686 */         return localPageFormat;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1693 */     Object localObject = Locale.getDefault().getCountry();
/* 1694 */     if ((!Locale.getDefault().equals(Locale.ENGLISH)) && (localObject != null))
/*      */     {
/* 1696 */       if ((!((String)localObject).equals(Locale.US.getCountry())) && 
/* 1697 */         (!((String)localObject).equals(Locale.CANADA.getCountry())))
/*      */       {
/* 1699 */         double d4 = 25.4D;
/* 1700 */         d2 = Math.rint(210.0D * d1 / d4);
/* 1701 */         d3 = Math.rint(297.0D * d1 / d4);
/* 1702 */         localPaper.setSize(d2, d3);
/* 1703 */         localPaper.setImageableArea(d1, d1, d2 - 2.0D * d1, d3 - 2.0D * d1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1708 */     localPageFormat.setPaper(localPaper);
/*      */     
/* 1710 */     return localPageFormat;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PageFormat validatePage(PageFormat paramPageFormat)
/*      */   {
/* 1718 */     PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
/* 1719 */     Paper localPaper = new Paper();
/* 1720 */     validatePaper(localPageFormat.getPaper(), localPaper);
/* 1721 */     localPageFormat.setPaper(localPaper);
/*      */     
/* 1723 */     return localPageFormat;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCopies(int paramInt)
/*      */   {
/* 1730 */     this.mNumCopies = paramInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getCopies()
/*      */   {
/* 1737 */     return this.mNumCopies;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int getCopiesInt()
/*      */   {
/* 1744 */     return this.copiesAttr > 0 ? this.copiesAttr : getCopies();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getUserName()
/*      */   {
/* 1752 */     return System.getProperty("user.name");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getUserNameInt()
/*      */   {
/* 1759 */     if (this.userNameAttr != null) {
/* 1760 */       return this.userNameAttr;
/*      */     }
/*      */     try {
/* 1763 */       return getUserName();
/*      */     } catch (SecurityException localSecurityException) {}
/* 1765 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setJobName(String paramString)
/*      */   {
/* 1775 */     if (paramString != null) {
/* 1776 */       this.mDocName = paramString;
/*      */     } else {
/* 1778 */       throw new NullPointerException();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getJobName()
/*      */   {
/* 1786 */     return this.mDocName;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getJobNameInt()
/*      */   {
/* 1793 */     return this.jobNameAttr != null ? this.jobNameAttr : getJobName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setPageRange(int paramInt1, int paramInt2)
/*      */   {
/* 1804 */     if ((paramInt1 >= 0) && (paramInt2 >= 0)) {
/* 1805 */       this.mFirstPage = paramInt1;
/* 1806 */       this.mLastPage = paramInt2;
/* 1807 */       if (this.mLastPage < this.mFirstPage) this.mLastPage = this.mFirstPage;
/*      */     } else {
/* 1809 */       this.mFirstPage = -1;
/* 1810 */       this.mLastPage = -1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getFirstPage()
/*      */   {
/* 1819 */     return this.mFirstPage == -1 ? 0 : this.mFirstPage;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getLastPage()
/*      */   {
/* 1827 */     return this.mLastPage;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setCollated(boolean paramBoolean)
/*      */   {
/* 1839 */     this.mCollate = paramBoolean;
/* 1840 */     this.collateAttReq = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isCollated()
/*      */   {
/* 1848 */     return this.mCollate;
/*      */   }
/*      */   
/*      */   protected final int getSelectAttrib() {
/* 1852 */     if (this.attributes != null)
/*      */     {
/* 1854 */       SunPageSelection localSunPageSelection = (SunPageSelection)this.attributes.get(SunPageSelection.class);
/* 1855 */       if (localSunPageSelection == SunPageSelection.RANGE)
/* 1856 */         return 2;
/* 1857 */       if (localSunPageSelection == SunPageSelection.SELECTION)
/* 1858 */         return 1;
/* 1859 */       if (localSunPageSelection == SunPageSelection.ALL) {
/* 1860 */         return 0;
/*      */       }
/*      */     }
/* 1863 */     return 4;
/*      */   }
/*      */   
/*      */   protected final int getFromPageAttrib()
/*      */   {
/* 1868 */     if (this.attributes != null)
/*      */     {
/* 1870 */       PageRanges localPageRanges = (PageRanges)this.attributes.get(PageRanges.class);
/* 1871 */       if (localPageRanges != null) {
/* 1872 */         int[][] arrayOfInt = localPageRanges.getMembers();
/* 1873 */         return arrayOfInt[0][0];
/*      */       }
/*      */     }
/* 1876 */     return getMinPageAttrib();
/*      */   }
/*      */   
/*      */   protected final int getToPageAttrib()
/*      */   {
/* 1881 */     if (this.attributes != null)
/*      */     {
/* 1883 */       PageRanges localPageRanges = (PageRanges)this.attributes.get(PageRanges.class);
/* 1884 */       if (localPageRanges != null) {
/* 1885 */         int[][] arrayOfInt = localPageRanges.getMembers();
/* 1886 */         return arrayOfInt[(arrayOfInt.length - 1)][1];
/*      */       }
/*      */     }
/* 1889 */     return getMaxPageAttrib();
/*      */   }
/*      */   
/*      */   protected final int getMinPageAttrib() {
/* 1893 */     if (this.attributes != null)
/*      */     {
/* 1895 */       SunMinMaxPage localSunMinMaxPage = (SunMinMaxPage)this.attributes.get(SunMinMaxPage.class);
/* 1896 */       if (localSunMinMaxPage != null) {
/* 1897 */         return localSunMinMaxPage.getMin();
/*      */       }
/*      */     }
/* 1900 */     return 1;
/*      */   }
/*      */   
/*      */   protected final int getMaxPageAttrib() {
/* 1904 */     if (this.attributes != null)
/*      */     {
/* 1906 */       localObject = (SunMinMaxPage)this.attributes.get(SunMinMaxPage.class);
/* 1907 */       if (localObject != null) {
/* 1908 */         return ((SunMinMaxPage)localObject).getMax();
/*      */       }
/*      */     }
/*      */     
/* 1912 */     Object localObject = getPageable();
/* 1913 */     if (localObject != null) {
/* 1914 */       int i = ((Pageable)localObject).getNumberOfPages();
/* 1915 */       if (i <= -1) {
/* 1916 */         i = 9999;
/*      */       }
/* 1918 */       return i == 0 ? 1 : i;
/*      */     }
/*      */     
/* 1921 */     return Integer.MAX_VALUE;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int deviceHeight;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void cancelDoc()
/*      */     throws PrinterAbortException
/*      */   {
/* 1940 */     abortDoc();
/* 1941 */     synchronized (this) {
/* 1942 */       this.userCancelled = false;
/* 1943 */       this.performingPrinting = false;
/* 1944 */       notify();
/*      */     }
/* 1946 */     throw new PrinterAbortException();
/*      */   }
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
/* 1958 */     return isCollated() ? getCopiesInt() : 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getNoncollatedCopies()
/*      */   {
/* 1968 */     return isCollated() ? 1 : getCopiesInt();
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
/*      */   synchronized void setGraphicsConfigInfo(AffineTransform paramAffineTransform, double paramDouble1, double paramDouble2)
/*      */   {
/* 1983 */     Point2D.Double localDouble = new Point2D.Double(paramDouble1, paramDouble2);
/* 1984 */     paramAffineTransform.transform(localDouble, localDouble);
/*      */     
/* 1986 */     if ((this.pgConfig == null) || (this.defaultDeviceTransform == null) || 
/*      */     
/* 1988 */       (!paramAffineTransform.equals(this.defaultDeviceTransform)) || 
/* 1989 */       (this.deviceWidth != (int)localDouble.getX()) || 
/* 1990 */       (this.deviceHeight != (int)localDouble.getY()))
/*      */     {
/* 1992 */       this.deviceWidth = ((int)localDouble.getX());
/* 1993 */       this.deviceHeight = ((int)localDouble.getY());
/* 1994 */       this.defaultDeviceTransform = paramAffineTransform;
/* 1995 */       this.pgConfig = null;
/*      */     }
/*      */   }
/*      */   
/*      */   synchronized PrinterGraphicsConfig getPrinterGraphicsConfig() {
/* 2000 */     if (this.pgConfig != null) {
/* 2001 */       return this.pgConfig;
/*      */     }
/* 2003 */     String str = "Printer Device";
/* 2004 */     PrintService localPrintService = getPrintService();
/* 2005 */     if (localPrintService != null) {
/* 2006 */       str = localPrintService.toString();
/*      */     }
/* 2008 */     this.pgConfig = new PrinterGraphicsConfig(str, this.defaultDeviceTransform, this.deviceWidth, this.deviceHeight);
/*      */     
/*      */ 
/* 2011 */     return this.pgConfig;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int printPage(Pageable paramPageable, int paramInt)
/*      */     throws PrinterException
/*      */   {
/*      */     PageFormat localPageFormat2;
/*      */     
/*      */     PageFormat localPageFormat1;
/*      */     
/*      */     Printable localPrintable;
/*      */     
/*      */     try
/*      */     {
/* 2027 */       localPageFormat2 = paramPageable.getPageFormat(paramInt);
/* 2028 */       localPageFormat1 = (PageFormat)localPageFormat2.clone();
/* 2029 */       localPrintable = paramPageable.getPrintable(paramInt);
/*      */     } catch (Exception localException) {
/* 2031 */       PrinterException localPrinterException = new PrinterException("Error getting page or printable.[ " + localException + " ]");
/*      */       
/*      */ 
/* 2034 */       localPrinterException.initCause(localException);
/* 2035 */       throw localPrinterException;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2041 */     Paper localPaper1 = localPageFormat1.getPaper();
/*      */     
/* 2043 */     if ((localPageFormat1.getOrientation() != 1) && (this.landscapeRotates270))
/*      */     {
/*      */ 
/* 2046 */       d1 = localPaper1.getImageableX();
/* 2047 */       d2 = localPaper1.getImageableY();
/* 2048 */       double d3 = localPaper1.getImageableWidth();
/* 2049 */       double d4 = localPaper1.getImageableHeight();
/* 2050 */       localPaper1.setImageableArea(localPaper1.getWidth() - d1 - d3, localPaper1
/* 2051 */         .getHeight() - d2 - d4, d3, d4);
/*      */       
/* 2053 */       localPageFormat1.setPaper(localPaper1);
/* 2054 */       if (localPageFormat1.getOrientation() == 0) {
/* 2055 */         localPageFormat1.setOrientation(2);
/*      */       } else {
/* 2057 */         localPageFormat1.setOrientation(0);
/*      */       }
/*      */     }
/*      */     
/* 2061 */     double d1 = getXRes() / 72.0D;
/* 2062 */     double d2 = getYRes() / 72.0D;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2071 */     Rectangle2D.Double localDouble1 = new Rectangle2D.Double(localPaper1.getImageableX() * d1, localPaper1.getImageableY() * d2, localPaper1.getImageableWidth() * d1, localPaper1.getImageableHeight() * d2);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2077 */     AffineTransform localAffineTransform1 = new AffineTransform();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2082 */     AffineTransform localAffineTransform2 = new AffineTransform();
/* 2083 */     localAffineTransform2.scale(d1, d2);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2088 */     int i = (int)localDouble1.getWidth();
/* 2089 */     if (i % 4 != 0) {
/* 2090 */       i += 4 - i % 4;
/*      */     }
/* 2092 */     if (i <= 0) {
/* 2093 */       throw new PrinterException("Paper's imageable width is too small.");
/*      */     }
/*      */     
/* 2096 */     int j = (int)localDouble1.getHeight();
/* 2097 */     if (j <= 0) {
/* 2098 */       throw new PrinterException("Paper's imageable height is too small.");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2106 */     int k = 4194304 / i / 3;
/*      */     
/* 2108 */     int m = (int)Math.rint(localPaper1.getImageableX() * d1);
/* 2109 */     int n = (int)Math.rint(localPaper1.getImageableY() * d2);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2120 */     AffineTransform localAffineTransform3 = new AffineTransform();
/* 2121 */     localAffineTransform3.translate(-m, n);
/* 2122 */     localAffineTransform3.translate(0.0D, k);
/* 2123 */     localAffineTransform3.scale(1.0D, -1.0D);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2133 */     BufferedImage localBufferedImage = new BufferedImage(1, 1, 5);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2140 */     PeekGraphics localPeekGraphics = createPeekGraphics(localBufferedImage.createGraphics(), this);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2147 */     Rectangle2D.Double localDouble2 = new Rectangle2D.Double(localPageFormat1.getImageableX(), localPageFormat1.getImageableY(), localPageFormat1.getImageableWidth(), localPageFormat1.getImageableHeight());
/* 2148 */     localPeekGraphics.transform(localAffineTransform2);
/* 2149 */     localPeekGraphics.translate(-getPhysicalPrintableX(localPaper1) / d1, 
/* 2150 */       -getPhysicalPrintableY(localPaper1) / d2);
/* 2151 */     localPeekGraphics.transform(new AffineTransform(localPageFormat1.getMatrix()));
/* 2152 */     initPrinterGraphics(localPeekGraphics, localDouble2);
/* 2153 */     AffineTransform localAffineTransform4 = localPeekGraphics.getTransform();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2164 */     setGraphicsConfigInfo(localAffineTransform2, localPaper1
/* 2165 */       .getWidth(), localPaper1.getHeight());
/* 2166 */     int i1 = localPrintable.print(localPeekGraphics, localPageFormat2, paramInt);
/* 2167 */     debug_println("pageResult " + i1);
/* 2168 */     if (i1 == 0) {
/* 2169 */       debug_println("startPage " + paramInt);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2176 */       Paper localPaper2 = localPageFormat1.getPaper();
/*      */       
/*      */ 
/*      */ 
/* 2180 */       boolean bool = (this.previousPaper == null) || (localPaper2.getWidth() != this.previousPaper.getWidth()) || (localPaper2.getHeight() != this.previousPaper.getHeight());
/* 2181 */       this.previousPaper = localPaper2;
/*      */       
/* 2183 */       startPage(localPageFormat1, localPrintable, paramInt, bool);
/* 2184 */       Graphics2D localGraphics2D1 = createPathGraphics(localPeekGraphics, this, localPrintable, localPageFormat1, paramInt);
/*      */       
/*      */ 
/*      */ 
/*      */       Object localObject1;
/*      */       
/*      */ 
/*      */       Object localObject2;
/*      */       
/*      */ 
/* 2194 */       if (localGraphics2D1 != null) {
/* 2195 */         localGraphics2D1.transform(localAffineTransform2);
/*      */         
/* 2197 */         localGraphics2D1.translate(-getPhysicalPrintableX(localPaper1) / d1, 
/* 2198 */           -getPhysicalPrintableY(localPaper1) / d2);
/* 2199 */         localGraphics2D1.transform(new AffineTransform(localPageFormat1.getMatrix()));
/* 2200 */         initPrinterGraphics(localGraphics2D1, localDouble2);
/*      */         
/* 2202 */         this.redrawList.clear();
/*      */         
/* 2204 */         localObject1 = localGraphics2D1.getTransform();
/*      */         
/* 2206 */         localPrintable.print(localGraphics2D1, localPageFormat2, paramInt);
/*      */         
/* 2208 */         for (int i2 = 0; i2 < this.redrawList.size(); i2++) {
/* 2209 */           localObject2 = (GraphicsState)this.redrawList.get(i2);
/* 2210 */           localGraphics2D1.setTransform((AffineTransform)localObject1);
/* 2211 */           ((PathGraphics)localGraphics2D1).redrawRegion(((GraphicsState)localObject2).region, ((GraphicsState)localObject2).sx, ((GraphicsState)localObject2).sy, ((GraphicsState)localObject2).theClip, ((GraphicsState)localObject2).theTransform);
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/* 2223 */         localObject1 = this.cachedBand;
/* 2224 */         if ((this.cachedBand == null) || (i != this.cachedBandWidth) || (k != this.cachedBandHeight))
/*      */         {
/*      */ 
/* 2227 */           localObject1 = new BufferedImage(i, k, 5);
/*      */           
/* 2229 */           this.cachedBand = ((BufferedImage)localObject1);
/* 2230 */           this.cachedBandWidth = i;
/* 2231 */           this.cachedBandHeight = k;
/*      */         }
/* 2233 */         Graphics2D localGraphics2D2 = ((BufferedImage)localObject1).createGraphics();
/*      */         
/* 2235 */         localObject2 = new Rectangle2D.Double(0.0D, 0.0D, i, k);
/*      */         
/*      */ 
/* 2238 */         initPrinterGraphics(localGraphics2D2, (Rectangle2D)localObject2);
/*      */         
/* 2240 */         ProxyGraphics2D localProxyGraphics2D = new ProxyGraphics2D(localGraphics2D2, this);
/*      */         
/*      */ 
/* 2243 */         Graphics2D localGraphics2D3 = ((BufferedImage)localObject1).createGraphics();
/* 2244 */         localGraphics2D3.setColor(Color.white);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2253 */         ByteInterleavedRaster localByteInterleavedRaster = (ByteInterleavedRaster)((BufferedImage)localObject1).getRaster();
/* 2254 */         byte[] arrayOfByte = localByteInterleavedRaster.getDataStorage();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2260 */         int i3 = n + j;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2267 */         int i4 = (int)getPhysicalPrintableX(localPaper1);
/* 2268 */         int i5 = (int)getPhysicalPrintableY(localPaper1);
/*      */         
/* 2270 */         for (int i6 = 0; i6 <= j; 
/* 2271 */             i6 += k)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2277 */           localGraphics2D3.fillRect(0, 0, i, k);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2284 */           localGraphics2D2.setTransform(localAffineTransform1);
/* 2285 */           localGraphics2D2.transform(localAffineTransform3);
/* 2286 */           localAffineTransform3.translate(0.0D, -k);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 2291 */           localGraphics2D2.transform(localAffineTransform2);
/* 2292 */           localGraphics2D2.transform(new AffineTransform(localPageFormat1.getMatrix()));
/*      */           
/* 2294 */           Rectangle localRectangle = localGraphics2D2.getClipBounds();
/* 2295 */           localRectangle = localAffineTransform4.createTransformedShape(localRectangle).getBounds();
/*      */           
/* 2297 */           if ((localRectangle == null) || ((localPeekGraphics.hitsDrawingArea(localRectangle)) && (i > 0) && (k > 0)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2307 */             int i7 = m - i4;
/* 2308 */             if (i7 < 0) {
/* 2309 */               localGraphics2D2.translate(i7 / d1, 0.0D);
/* 2310 */               i7 = 0;
/*      */             }
/* 2312 */             int i8 = n + i6 - i5;
/* 2313 */             if (i8 < 0) {
/* 2314 */               localGraphics2D2.translate(0.0D, i8 / d2);
/* 2315 */               i8 = 0;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 2320 */             localProxyGraphics2D.setDelegate((Graphics2D)localGraphics2D2.create());
/* 2321 */             localPrintable.print(localProxyGraphics2D, localPageFormat2, paramInt);
/* 2322 */             localProxyGraphics2D.dispose();
/* 2323 */             printBand(arrayOfByte, i7, i8, i, k);
/*      */           }
/*      */         }
/*      */         
/* 2327 */         localGraphics2D3.dispose();
/* 2328 */         localGraphics2D2.dispose();
/*      */       }
/*      */       
/* 2331 */       debug_println("calling endPage " + paramInt);
/* 2332 */       endPage(localPageFormat1, localPrintable, paramInt);
/*      */     }
/*      */     
/* 2335 */     return i1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void cancel()
/*      */   {
/* 2346 */     synchronized (this) {
/* 2347 */       if (this.performingPrinting) {
/* 2348 */         this.userCancelled = true;
/*      */       }
/* 2350 */       notify();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isCancelled()
/*      */   {
/* 2361 */     boolean bool = false;
/*      */     
/* 2363 */     synchronized (this) {
/* 2364 */       bool = (this.performingPrinting) && (this.userCancelled);
/* 2365 */       notify();
/*      */     }
/*      */     
/* 2368 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Pageable getPageable()
/*      */   {
/* 2375 */     return this.mDocument;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private AffineTransform defaultDeviceTransform;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private PrinterGraphicsConfig pgConfig;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt)
/*      */   {
/* 2396 */     return null;
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
/*      */   protected PeekGraphics createPeekGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob)
/*      */   {
/* 2411 */     return new PeekGraphics(paramGraphics2D, paramPrinterJob);
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
/*      */   protected void initPrinterGraphics(Graphics2D paramGraphics2D, Rectangle2D paramRectangle2D)
/*      */   {
/* 2424 */     paramGraphics2D.setClip(paramRectangle2D);
/* 2425 */     paramGraphics2D.setPaint(Color.black);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean checkAllowedToPrintToFile()
/*      */   {
/*      */     try
/*      */     {
/* 2435 */       throwPrintToFile();
/* 2436 */       return true;
/*      */     } catch (SecurityException localSecurityException) {}
/* 2438 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void throwPrintToFile()
/*      */   {
/* 2448 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 2449 */     if (localSecurityManager != null) {
/* 2450 */       if (this.printToFilePermission == null) {
/* 2451 */         this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
/*      */       }
/*      */       
/* 2454 */       localSecurityManager.checkPermission(this.printToFilePermission);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String removeControlChars(String paramString)
/*      */   {
/* 2465 */     char[] arrayOfChar1 = paramString.toCharArray();
/* 2466 */     int i = arrayOfChar1.length;
/* 2467 */     char[] arrayOfChar2 = new char[i];
/* 2468 */     int j = 0;
/*      */     
/* 2470 */     for (int k = 0; k < i; k++) {
/* 2471 */       int m = arrayOfChar1[k];
/* 2472 */       if ((m > 13) || (m < 9) || (m == 11) || (m == 12)) {
/* 2473 */         arrayOfChar2[(j++)] = m;
/*      */       }
/*      */     }
/* 2476 */     if (j == i) {
/* 2477 */       return paramString;
/*      */     }
/* 2479 */     return new String(arrayOfChar2, 0, j);
/*      */   }
/*      */   
/*      */ 
/* 2483 */   private DialogOnTop onTop = null;
/*      */   
/* 2485 */   private long parentWindowID = 0L;
/*      */   
/*      */   private long getParentWindowID()
/*      */   {
/* 2489 */     return this.parentWindowID;
/*      */   }
/*      */   
/*      */   private void clearParentWindowID() {
/* 2493 */     this.parentWindowID = 0L;
/* 2494 */     this.onTop = null;
/*      */   }
/*      */   
/*      */   private void setParentWindowID(PrintRequestAttributeSet paramPrintRequestAttributeSet) {
/* 2498 */     this.parentWindowID = 0L;
/* 2499 */     this.onTop = ((DialogOnTop)paramPrintRequestAttributeSet.get(DialogOnTop.class));
/* 2500 */     if (this.onTop != null) {
/* 2501 */       this.parentWindowID = this.onTop.getID();
/*      */     }
/*      */   }
/*      */   
/*      */   protected abstract double getXRes();
/*      */   
/*      */   protected abstract double getYRes();
/*      */   
/*      */   protected abstract double getPhysicalPrintableX(Paper paramPaper);
/*      */   
/*      */   protected abstract double getPhysicalPrintableY(Paper paramPaper);
/*      */   
/*      */   protected abstract double getPhysicalPrintableWidth(Paper paramPaper);
/*      */   
/*      */   protected abstract double getPhysicalPrintableHeight(Paper paramPaper);
/*      */   
/*      */   protected abstract double getPhysicalPageWidth(Paper paramPaper);
/*      */   
/*      */   protected abstract double getPhysicalPageHeight(Paper paramPaper);
/*      */   
/*      */   protected abstract void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean)
/*      */     throws PrinterException;
/*      */   
/*      */   protected abstract void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt)
/*      */     throws PrinterException;
/*      */   
/*      */   protected abstract void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws PrinterException;
/*      */   
/*      */   protected void initPrinter() {}
/*      */   
/*      */   protected abstract void startDoc()
/*      */     throws PrinterException;
/*      */   
/*      */   protected abstract void endDoc()
/*      */     throws PrinterException;
/*      */   
/*      */   protected abstract void abortDoc();
/*      */   
/*      */   private class GraphicsState
/*      */   {
/*      */     Rectangle2D region;
/*      */     Shape theClip;
/*      */     AffineTransform theTransform;
/*      */     double sx;
/*      */     double sy;
/*      */     
/*      */     private GraphicsState() {}
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\print\RasterPrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
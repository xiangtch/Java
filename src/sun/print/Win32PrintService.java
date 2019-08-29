/*      */ package sun.print;
/*      */ 
/*      */ import java.awt.Window;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.io.File;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import javax.print.DocFlavor;
/*      */ import javax.print.DocFlavor.BYTE_ARRAY;
/*      */ import javax.print.DocFlavor.INPUT_STREAM;
/*      */ import javax.print.DocFlavor.SERVICE_FORMATTED;
/*      */ import javax.print.DocFlavor.URL;
/*      */ import javax.print.DocPrintJob;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.ServiceUIFactory;
/*      */ import javax.print.attribute.Attribute;
/*      */ import javax.print.attribute.AttributeSet;
/*      */ import javax.print.attribute.AttributeSetUtilities;
/*      */ import javax.print.attribute.HashAttributeSet;
/*      */ import javax.print.attribute.HashPrintServiceAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintServiceAttribute;
/*      */ import javax.print.attribute.PrintServiceAttributeSet;
/*      */ import javax.print.attribute.standard.Chromaticity;
/*      */ import javax.print.attribute.standard.ColorSupported;
/*      */ import javax.print.attribute.standard.Copies;
/*      */ import javax.print.attribute.standard.CopiesSupported;
/*      */ import javax.print.attribute.standard.Destination;
/*      */ import javax.print.attribute.standard.Fidelity;
/*      */ import javax.print.attribute.standard.JobName;
/*      */ import javax.print.attribute.standard.Media;
/*      */ import javax.print.attribute.standard.MediaPrintableArea;
/*      */ import javax.print.attribute.standard.MediaSize;
/*      */ import javax.print.attribute.standard.MediaSizeName;
/*      */ import javax.print.attribute.standard.MediaTray;
/*      */ import javax.print.attribute.standard.OrientationRequested;
/*      */ import javax.print.attribute.standard.PageRanges;
/*      */ import javax.print.attribute.standard.PrintQuality;
/*      */ import javax.print.attribute.standard.PrinterIsAcceptingJobs;
/*      */ import javax.print.attribute.standard.PrinterName;
/*      */ import javax.print.attribute.standard.PrinterResolution;
/*      */ import javax.print.attribute.standard.PrinterState;
/*      */ import javax.print.attribute.standard.PrinterStateReason;
/*      */ import javax.print.attribute.standard.PrinterStateReasons;
/*      */ import javax.print.attribute.standard.QueuedJobCount;
/*      */ import javax.print.attribute.standard.RequestingUserName;
/*      */ import javax.print.attribute.standard.Severity;
/*      */ import javax.print.attribute.standard.SheetCollate;
/*      */ import javax.print.attribute.standard.Sides;
/*      */ import javax.print.event.PrintServiceAttributeListener;
/*      */ import sun.awt.windows.WPrinterJob;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Win32PrintService
/*      */   implements PrintService, AttributeUpdater, SunPrinterJobService
/*      */ {
/*   80 */   public static MediaSize[] predefMedia = ;
/*      */   
/*   82 */   private static final DocFlavor[] supportedFlavors = { BYTE_ARRAY.GIF, INPUT_STREAM.GIF, URL.GIF, BYTE_ARRAY.JPEG, INPUT_STREAM.JPEG, URL.JPEG, BYTE_ARRAY.PNG, INPUT_STREAM.PNG, URL.PNG, SERVICE_FORMATTED.PAGEABLE, SERVICE_FORMATTED.PRINTABLE, BYTE_ARRAY.AUTOSENSE, URL.AUTOSENSE, INPUT_STREAM.AUTOSENSE };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  100 */   private static final Class[] serviceAttrCats = { PrinterName.class, PrinterIsAcceptingJobs.class, QueuedJobCount.class, ColorSupported.class };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  110 */   private static Class[] otherAttrCats = { JobName.class, RequestingUserName.class, Copies.class, Destination.class, OrientationRequested.class, PageRanges.class, Media.class, MediaPrintableArea.class, Fidelity.class, SheetCollate.class, SunAlternateMedia.class, Chromaticity.class };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  132 */   public static final MediaSizeName[] dmPaperToPrintService = { MediaSizeName.NA_LETTER, MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.INVOICE, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, MediaSizeName.FOLIO, MediaSizeName.QUARTO, MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.B, MediaSizeName.NA_LETTER, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, MediaSizeName.NA_NUMBER_14_ENVELOPE, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.ISO_C5, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, MediaSizeName.ISO_C6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE, MediaSizeName.NA_10X15_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, MediaSizeName.FOLIO, MediaSizeName.ISO_B4, MediaSizeName.JAPANESE_POSTCARD, MediaSizeName.NA_9X11_ENVELOPE };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  157 */   private static final MediaTray[] dmPaperBinToPrintService = { MediaTray.TOP, MediaTray.BOTTOM, MediaTray.MIDDLE, MediaTray.MANUAL, MediaTray.ENVELOPE, Win32MediaTray.ENVELOPE_MANUAL, Win32MediaTray.AUTO, Win32MediaTray.TRACTOR, Win32MediaTray.SMALL_FORMAT, Win32MediaTray.LARGE_FORMAT, MediaTray.LARGE_CAPACITY, null, null, MediaTray.MAIN, Win32MediaTray.FORMSOURCE };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  167 */   private static int DM_PAPERSIZE = 2;
/*  168 */   private static int DM_PRINTQUALITY = 1024;
/*  169 */   private static int DM_YRESOLUTION = 8192;
/*      */   
/*      */   private static final int DMRES_MEDIUM = -3;
/*      */   
/*      */   private static final int DMRES_HIGH = -4;
/*      */   
/*      */   private static final int DMORIENT_LANDSCAPE = 2;
/*      */   
/*      */   private static final int DMDUP_VERTICAL = 2;
/*      */   
/*      */   private static final int DMDUP_HORIZONTAL = 3;
/*      */   
/*      */   private static final int DMCOLLATE_TRUE = 1;
/*      */   
/*      */   private static final int DMCOLOR_MONOCHROME = 1;
/*      */   
/*      */   private static final int DMCOLOR_COLOR = 2;
/*      */   
/*      */   private static final int DMPAPER_A2 = 66;
/*      */   private static final int DMPAPER_A6 = 70;
/*      */   private static final int DMPAPER_B6_JIS = 88;
/*      */   private static final int DEVCAP_COLOR = 1;
/*      */   private static final int DEVCAP_DUPLEX = 2;
/*      */   private static final int DEVCAP_COLLATE = 4;
/*      */   private static final int DEVCAP_QUALITY = 8;
/*      */   private static final int DEVCAP_POSTSCRIPT = 16;
/*      */   private String printer;
/*      */   private PrinterName name;
/*      */   private String port;
/*      */   private transient PrintServiceAttributeSet lastSet;
/*  199 */   private transient ServiceNotifier notifier = null;
/*      */   
/*      */   private MediaSizeName[] mediaSizeNames;
/*      */   
/*      */   private MediaPrintableArea[] mediaPrintables;
/*      */   
/*      */   private MediaTray[] mediaTrays;
/*      */   private PrinterResolution[] printRes;
/*      */   private HashMap mpaMap;
/*      */   private int nCopies;
/*      */   private int prnCaps;
/*      */   private int[] defaultSettings;
/*      */   private boolean gotTrays;
/*      */   private boolean gotCopies;
/*      */   private boolean mediaInitialized;
/*      */   private boolean mpaListInitialized;
/*      */   private ArrayList idList;
/*      */   private MediaSize[] mediaSizes;
/*      */   private boolean isInvalid;
/*      */   
/*      */   Win32PrintService(String paramString)
/*      */   {
/*  221 */     if (paramString == null) {
/*  222 */       throw new IllegalArgumentException("null printer name");
/*      */     }
/*  224 */     this.printer = paramString;
/*      */     
/*      */ 
/*  227 */     this.mediaInitialized = false;
/*  228 */     this.gotTrays = false;
/*  229 */     this.gotCopies = false;
/*  230 */     this.isInvalid = false;
/*  231 */     this.printRes = null;
/*  232 */     this.prnCaps = 0;
/*  233 */     this.defaultSettings = null;
/*  234 */     this.port = null;
/*      */   }
/*      */   
/*      */   public void invalidateService() {
/*  238 */     this.isInvalid = true;
/*      */   }
/*      */   
/*      */   public String getName() {
/*  242 */     return this.printer;
/*      */   }
/*      */   
/*      */   private PrinterName getPrinterName() {
/*  246 */     if (this.name == null) {
/*  247 */       this.name = new PrinterName(this.printer, null);
/*      */     }
/*  249 */     return this.name;
/*      */   }
/*      */   
/*      */   public int findPaperID(MediaSizeName paramMediaSizeName) {
/*  253 */     if ((paramMediaSizeName instanceof Win32MediaSize)) {
/*  254 */       Win32MediaSize localWin32MediaSize = (Win32MediaSize)paramMediaSizeName;
/*  255 */       return localWin32MediaSize.getDMPaper();
/*      */     }
/*  257 */     for (int i = 0; i < dmPaperToPrintService.length; i++) {
/*  258 */       if (dmPaperToPrintService[i].equals(paramMediaSizeName)) {
/*  259 */         return i + 1;
/*      */       }
/*      */     }
/*  262 */     if (paramMediaSizeName.equals(MediaSizeName.ISO_A2)) {
/*  263 */       return 66;
/*      */     }
/*  265 */     if (paramMediaSizeName.equals(MediaSizeName.ISO_A6)) {
/*  266 */       return 70;
/*      */     }
/*  268 */     if (paramMediaSizeName.equals(MediaSizeName.JIS_B6)) {
/*  269 */       return 88;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  276 */     initMedia();
/*      */     
/*  278 */     if ((this.idList != null) && (this.mediaSizes != null) && 
/*  279 */       (this.idList.size() == this.mediaSizes.length)) {
/*  280 */       for (i = 0; i < this.idList.size(); i++) {
/*  281 */         if (this.mediaSizes[i].getMediaSizeName() == paramMediaSizeName) {
/*  282 */           return ((Integer)this.idList.get(i)).intValue();
/*      */         }
/*      */       }
/*      */     }
/*  286 */     return 0;
/*      */   }
/*      */   
/*      */   public int findTrayID(MediaTray paramMediaTray)
/*      */   {
/*  291 */     getMediaTrays();
/*      */     
/*  293 */     if ((paramMediaTray instanceof Win32MediaTray)) {
/*  294 */       Win32MediaTray localWin32MediaTray = (Win32MediaTray)paramMediaTray;
/*  295 */       return localWin32MediaTray.getDMBinID();
/*      */     }
/*  297 */     for (int i = 0; i < dmPaperBinToPrintService.length; i++) {
/*  298 */       if (paramMediaTray.equals(dmPaperBinToPrintService[i])) {
/*  299 */         return i + 1;
/*      */       }
/*      */     }
/*  302 */     return 0;
/*      */   }
/*      */   
/*      */   public MediaTray findMediaTray(int paramInt) {
/*  306 */     if ((paramInt >= 1) && (paramInt <= dmPaperBinToPrintService.length)) {
/*  307 */       return dmPaperBinToPrintService[(paramInt - 1)];
/*      */     }
/*  309 */     MediaTray[] arrayOfMediaTray = getMediaTrays();
/*  310 */     if (arrayOfMediaTray != null) {
/*  311 */       for (int i = 0; i < arrayOfMediaTray.length; i++) {
/*  312 */         if ((arrayOfMediaTray[i] instanceof Win32MediaTray)) {
/*  313 */           Win32MediaTray localWin32MediaTray = (Win32MediaTray)arrayOfMediaTray[i];
/*  314 */           if (localWin32MediaTray.winID == paramInt) {
/*  315 */             return localWin32MediaTray;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  320 */     return Win32MediaTray.AUTO;
/*      */   }
/*      */   
/*      */   public MediaSizeName findWin32Media(int paramInt) {
/*  324 */     if ((paramInt >= 1) && (paramInt <= dmPaperToPrintService.length)) {
/*  325 */       return dmPaperToPrintService[(paramInt - 1)];
/*      */     }
/*  327 */     switch (paramInt)
/*      */     {
/*      */ 
/*      */     case 66: 
/*  331 */       return MediaSizeName.ISO_A2;
/*      */     case 70: 
/*  333 */       return MediaSizeName.ISO_A6;
/*      */     case 88: 
/*  335 */       return MediaSizeName.JIS_B6; }
/*      */     
/*  337 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean addToUniqueList(ArrayList paramArrayList, MediaSizeName paramMediaSizeName)
/*      */   {
/*  343 */     for (int i = 0; i < paramArrayList.size(); i++) {
/*  344 */       MediaSizeName localMediaSizeName = (MediaSizeName)paramArrayList.get(i);
/*  345 */       if (localMediaSizeName == paramMediaSizeName) {
/*  346 */         return false;
/*      */       }
/*      */     }
/*  349 */     paramArrayList.add(paramMediaSizeName);
/*  350 */     return true;
/*      */   }
/*      */   
/*      */   private synchronized void initMedia() {
/*  354 */     if (this.mediaInitialized == true) {
/*  355 */       return;
/*      */     }
/*  357 */     this.mediaInitialized = true;
/*  358 */     int[] arrayOfInt = getAllMediaIDs(this.printer, getPort());
/*  359 */     if (arrayOfInt == null) {
/*  360 */       return;
/*      */     }
/*      */     
/*  363 */     ArrayList localArrayList1 = new ArrayList();
/*  364 */     ArrayList localArrayList2 = new ArrayList();
/*  365 */     ArrayList localArrayList3 = new ArrayList();
/*      */     
/*      */ 
/*  368 */     int i = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  378 */     this.idList = new ArrayList();
/*  379 */     for (int j = 0; j < arrayOfInt.length; j++) {
/*  380 */       this.idList.add(Integer.valueOf(arrayOfInt[j]));
/*      */     }
/*      */     
/*  383 */     ArrayList localArrayList4 = new ArrayList();
/*  384 */     this.mediaSizes = getMediaSizes(this.idList, arrayOfInt, localArrayList4);
/*  385 */     boolean bool; for (int k = 0; k < this.idList.size(); k++)
/*      */     {
/*      */ 
/*  388 */       Object localObject1 = findWin32Media(((Integer)this.idList.get(k)).intValue());
/*      */       
/*      */       Object localObject2;
/*      */       
/*  392 */       if ((localObject1 != null) && 
/*  393 */         (this.idList.size() == this.mediaSizes.length)) {
/*  394 */         MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject1);
/*  395 */         localObject2 = this.mediaSizes[k];
/*  396 */         int n = 2540;
/*  397 */         if ((Math.abs(localMediaSize.getX(1) - ((MediaSize)localObject2).getX(1)) > n) || 
/*  398 */           (Math.abs(localMediaSize.getY(1) - ((MediaSize)localObject2).getY(1)) > n))
/*      */         {
/*  400 */           localObject1 = null;
/*      */         }
/*      */       }
/*  403 */       int m = localObject1 != null ? 1 : 0;
/*      */       
/*      */ 
/*      */ 
/*  407 */       if ((localObject1 == null) && (this.idList.size() == this.mediaSizes.length)) {
/*  408 */         localObject1 = this.mediaSizes[k].getMediaSizeName();
/*      */       }
/*      */       
/*      */ 
/*  412 */       bool = false;
/*  413 */       if (localObject1 != null) {
/*  414 */         bool = addToUniqueList(localArrayList1, (MediaSizeName)localObject1);
/*      */       }
/*  416 */       if (((m == 0) || (!bool)) && (this.idList.size() == localArrayList4.size()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  421 */         localObject2 = Win32MediaSize.findMediaName((String)localArrayList4.get(k));
/*  422 */         if ((localObject2 == null) && (this.idList.size() == this.mediaSizes.length)) {
/*  423 */           localObject2 = new Win32MediaSize((String)localArrayList4.get(k), ((Integer)this.idList.get(k)).intValue());
/*  424 */           this.mediaSizes[k] = new MediaSize(this.mediaSizes[k].getX(1000), this.mediaSizes[k]
/*  425 */             .getY(1000), 1000, (MediaSizeName)localObject2);
/*      */         }
/*  427 */         if ((localObject2 != null) && (localObject2 != localObject1)) {
/*  428 */           if (!bool) {
/*  429 */             bool = addToUniqueList(localArrayList1, localObject1 = localObject2);
/*      */           } else {
/*  431 */             localArrayList2.add(localObject2);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  436 */     for (Win32MediaSize localWin32MediaSize : localArrayList2) {
/*  437 */       bool = addToUniqueList(localArrayList1, localWin32MediaSize);
/*      */     }
/*      */     
/*      */ 
/*  441 */     this.mediaSizeNames = new MediaSizeName[localArrayList1.size()];
/*  442 */     localArrayList1.toArray(this.mediaSizeNames);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private synchronized MediaPrintableArea[] getMediaPrintables(MediaSizeName paramMediaSizeName)
/*      */   {
/*      */     Object localObject1;
/*      */     
/*      */ 
/*  453 */     if (paramMediaSizeName == null) {
/*  454 */       if (this.mpaListInitialized == true) {
/*  455 */         return this.mediaPrintables;
/*      */       }
/*      */       
/*      */     }
/*  459 */     else if ((this.mpaMap != null) && (this.mpaMap.get(paramMediaSizeName) != null)) {
/*  460 */       localObject1 = new MediaPrintableArea[1];
/*  461 */       localObject1[0] = ((MediaPrintableArea)this.mpaMap.get(paramMediaSizeName));
/*  462 */       return (MediaPrintableArea[])localObject1;
/*      */     }
/*      */     
/*      */ 
/*  466 */     initMedia();
/*      */     
/*  468 */     if ((this.mediaSizeNames == null) || (this.mediaSizeNames.length == 0)) {
/*  469 */       return null;
/*      */     }
/*      */     
/*      */ 
/*  473 */     if (paramMediaSizeName != null) {
/*  474 */       localObject1 = new MediaSizeName[1];
/*  475 */       localObject1[0] = paramMediaSizeName;
/*      */     } else {
/*  477 */       localObject1 = this.mediaSizeNames;
/*      */     }
/*      */     
/*  480 */     if (this.mpaMap == null) {
/*  481 */       this.mpaMap = new HashMap();
/*      */     }
/*      */     
/*  484 */     for (int i = 0; i < localObject1.length; i++) {
/*  485 */       Object localObject2 = localObject1[i];
/*      */       
/*  487 */       if (this.mpaMap.get(localObject2) == null)
/*      */       {
/*      */ 
/*      */ 
/*  491 */         if (localObject2 != null) {
/*  492 */           int j = findPaperID((MediaSizeName)localObject2);
/*  493 */           Object localObject3 = j != 0 ? getMediaPrintableArea(this.printer, j) : null;
/*  494 */           MediaPrintableArea localMediaPrintableArea = null;
/*  495 */           if (localObject3 != null) {
/*      */             try {
/*  497 */               localMediaPrintableArea = new MediaPrintableArea(localObject3[0], localObject3[1], localObject3[2], localObject3[3], 25400);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  503 */               this.mpaMap.put(localObject2, localMediaPrintableArea);
/*      */ 
/*      */             }
/*      */             catch (IllegalArgumentException localIllegalArgumentException1) {}
/*      */           }
/*      */           else
/*      */           {
/*  510 */             MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject2);
/*      */             
/*  512 */             if (localMediaSize != null)
/*      */             {
/*      */               try
/*      */               {
/*  516 */                 localMediaPrintableArea = new MediaPrintableArea(0.0F, 0.0F, localMediaSize.getX(25400), localMediaSize.getY(25400), 25400);
/*      */                 
/*  518 */                 this.mpaMap.put(localObject2, localMediaPrintableArea);
/*      */               }
/*      */               catch (IllegalArgumentException localIllegalArgumentException2) {}
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  526 */     if (this.mpaMap.size() == 0) {
/*  527 */       return null;
/*      */     }
/*      */     
/*  530 */     if (paramMediaSizeName != null) {
/*  531 */       if (this.mpaMap.get(paramMediaSizeName) == null) {
/*  532 */         return null;
/*      */       }
/*  534 */       MediaPrintableArea[] arrayOfMediaPrintableArea = new MediaPrintableArea[1];
/*      */       
/*  536 */       arrayOfMediaPrintableArea[0] = ((MediaPrintableArea)this.mpaMap.get(paramMediaSizeName));
/*  537 */       return arrayOfMediaPrintableArea;
/*      */     }
/*  539 */     this.mediaPrintables = ((MediaPrintableArea[])this.mpaMap.values().toArray(new MediaPrintableArea[0]));
/*  540 */     this.mpaListInitialized = true;
/*  541 */     return this.mediaPrintables;
/*      */   }
/*      */   
/*      */ 
/*      */   private synchronized MediaTray[] getMediaTrays()
/*      */   {
/*  547 */     if ((this.gotTrays == true) && (this.mediaTrays != null)) {
/*  548 */       return this.mediaTrays;
/*      */     }
/*  550 */     String str = getPort();
/*  551 */     int[] arrayOfInt = getAllMediaTrays(this.printer, str);
/*  552 */     String[] arrayOfString = getAllMediaTrayNames(this.printer, str);
/*      */     
/*  554 */     if ((arrayOfInt == null) || (arrayOfString == null)) {
/*  555 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  561 */     int i = 0;
/*  562 */     for (int j = 0; j < arrayOfInt.length; j++) {
/*  563 */       if (arrayOfInt[j] > 0) { i++;
/*      */       }
/*      */     }
/*  566 */     MediaTray[] arrayOfMediaTray = new MediaTray[i];
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  575 */     int m = 0; for (int n = 0; m < Math.min(arrayOfInt.length, arrayOfString.length); m++) {
/*  576 */       int k = arrayOfInt[m];
/*  577 */       if (k > 0)
/*      */       {
/*  579 */         if ((k > dmPaperBinToPrintService.length) || (dmPaperBinToPrintService[(k - 1)] == null))
/*      */         {
/*  581 */           arrayOfMediaTray[(n++)] = new Win32MediaTray(k, arrayOfString[m]);
/*      */         } else {
/*  583 */           arrayOfMediaTray[(n++)] = dmPaperBinToPrintService[(k - 1)];
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  589 */     this.mediaTrays = arrayOfMediaTray;
/*  590 */     this.gotTrays = true;
/*  591 */     return this.mediaTrays;
/*      */   }
/*      */   
/*      */   private boolean isSameSize(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
/*  595 */     float f1 = paramFloat1 - paramFloat3;
/*  596 */     float f2 = paramFloat2 - paramFloat4;
/*      */     
/*      */ 
/*  599 */     float f3 = paramFloat1 - paramFloat4;
/*  600 */     float f4 = paramFloat2 - paramFloat3;
/*      */     
/*  602 */     if (((Math.abs(f1) <= 1.0F) && (Math.abs(f2) <= 1.0F)) || (
/*  603 */       (Math.abs(f3) <= 1.0F) && (Math.abs(f4) <= 1.0F))) {
/*  604 */       return true;
/*      */     }
/*  606 */     return false;
/*      */   }
/*      */   
/*      */   public MediaSizeName findMatchingMediaSizeNameMM(float paramFloat1, float paramFloat2)
/*      */   {
/*  611 */     if (predefMedia != null) {
/*  612 */       for (int i = 0; i < predefMedia.length; i++) {
/*  613 */         if (predefMedia[i] != null)
/*      */         {
/*      */ 
/*      */ 
/*  617 */           if (isSameSize(predefMedia[i].getX(1000), predefMedia[i]
/*  618 */             .getY(1000), paramFloat1, paramFloat2))
/*      */           {
/*  620 */             return predefMedia[i].getMediaSizeName(); }
/*      */         }
/*      */       }
/*      */     }
/*  624 */     return null;
/*      */   }
/*      */   
/*      */   private MediaSize[] getMediaSizes(ArrayList paramArrayList, int[] paramArrayOfInt, ArrayList<String> paramArrayList1)
/*      */   {
/*  629 */     if (paramArrayList1 == null) {
/*  630 */       paramArrayList1 = new ArrayList();
/*      */     }
/*      */     
/*  633 */     String str = getPort();
/*  634 */     int[] arrayOfInt = getAllMediaSizes(this.printer, str);
/*  635 */     String[] arrayOfString = getAllMediaNames(this.printer, str);
/*  636 */     MediaSizeName localMediaSizeName = null;
/*  637 */     MediaSize localMediaSize = null;
/*      */     
/*      */ 
/*  640 */     if ((arrayOfInt == null) || (arrayOfString == null)) {
/*  641 */       return null;
/*      */     }
/*      */     
/*  644 */     int i = arrayOfInt.length / 2;
/*  645 */     ArrayList localArrayList = new ArrayList();
/*      */     
/*  647 */     for (int j = 0; j < i; localMediaSize = null) {
/*  648 */       float f1 = arrayOfInt[(j * 2)] / 10.0F;
/*  649 */       float f2 = arrayOfInt[(j * 2 + 1)] / 10.0F;
/*      */       
/*      */ 
/*      */       Object localObject;
/*      */       
/*  654 */       if ((f1 <= 0.0F) || (f2 <= 0.0F))
/*      */       {
/*  656 */         if (i == paramArrayOfInt.length) {
/*  657 */           localObject = Integer.valueOf(paramArrayOfInt[j]);
/*  658 */           paramArrayList.remove(paramArrayList.indexOf(localObject));
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/*  664 */         localMediaSizeName = findMatchingMediaSizeNameMM(f1, f2);
/*  665 */         if (localMediaSizeName != null) {
/*  666 */           localMediaSize = MediaSize.getMediaSizeForName(localMediaSizeName);
/*      */         }
/*      */         
/*  669 */         if (localMediaSize != null) {
/*  670 */           localArrayList.add(localMediaSize);
/*  671 */           paramArrayList1.add(arrayOfString[j]);
/*      */         } else {
/*  673 */           localObject = Win32MediaSize.findMediaName(arrayOfString[j]);
/*  674 */           if (localObject == null) {
/*  675 */             localObject = new Win32MediaSize(arrayOfString[j], paramArrayOfInt[j]);
/*      */           }
/*      */           try {
/*  678 */             localMediaSize = new MediaSize(f1, f2, 1000, (MediaSizeName)localObject);
/*  679 */             localArrayList.add(localMediaSize);
/*  680 */             paramArrayList1.add(arrayOfString[j]);
/*      */           } catch (IllegalArgumentException localIllegalArgumentException) {
/*  682 */             if (i == paramArrayOfInt.length) {
/*  683 */               Integer localInteger = Integer.valueOf(paramArrayOfInt[j]);
/*  684 */               paramArrayList.remove(paramArrayList.indexOf(localInteger));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  647 */       j++;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  690 */     MediaSize[] arrayOfMediaSize = new MediaSize[localArrayList.size()];
/*  691 */     localArrayList.toArray(arrayOfMediaSize);
/*      */     
/*  693 */     return arrayOfMediaSize;
/*      */   }
/*      */   
/*      */   private PrinterIsAcceptingJobs getPrinterIsAcceptingJobs() {
/*  697 */     if (getJobStatus(this.printer, 2) != 1) {
/*  698 */       return PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
/*      */     }
/*      */     
/*  701 */     return PrinterIsAcceptingJobs.ACCEPTING_JOBS;
/*      */   }
/*      */   
/*      */   private PrinterState getPrinterState()
/*      */   {
/*  706 */     if (this.isInvalid) {
/*  707 */       return PrinterState.STOPPED;
/*      */     }
/*  709 */     return null;
/*      */   }
/*      */   
/*      */   private PrinterStateReasons getPrinterStateReasons()
/*      */   {
/*  714 */     if (this.isInvalid) {
/*  715 */       PrinterStateReasons localPrinterStateReasons = new PrinterStateReasons();
/*  716 */       localPrinterStateReasons.put(PrinterStateReason.SHUTDOWN, Severity.ERROR);
/*  717 */       return localPrinterStateReasons;
/*      */     }
/*  719 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private QueuedJobCount getQueuedJobCount()
/*      */   {
/*  725 */     int i = getJobStatus(this.printer, 1);
/*  726 */     if (i != -1) {
/*  727 */       return new QueuedJobCount(i);
/*      */     }
/*      */     
/*  730 */     return new QueuedJobCount(0);
/*      */   }
/*      */   
/*      */   private boolean isSupportedCopies(Copies paramCopies)
/*      */   {
/*  735 */     synchronized (this) {
/*  736 */       if (!this.gotCopies) {
/*  737 */         this.nCopies = getCopiesSupported(this.printer, getPort());
/*  738 */         this.gotCopies = true;
/*      */       }
/*      */     }
/*  741 */     int i = paramCopies.getValue();
/*  742 */     return (i > 0) && (i <= this.nCopies);
/*      */   }
/*      */   
/*      */   private boolean isSupportedMedia(MediaSizeName paramMediaSizeName)
/*      */   {
/*  747 */     initMedia();
/*      */     
/*  749 */     if (this.mediaSizeNames != null) {
/*  750 */       for (int i = 0; i < this.mediaSizeNames.length; i++) {
/*  751 */         if (paramMediaSizeName.equals(this.mediaSizeNames[i])) {
/*  752 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  756 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isSupportedMediaPrintableArea(MediaPrintableArea paramMediaPrintableArea)
/*      */   {
/*  761 */     getMediaPrintables(null);
/*      */     
/*  763 */     if (this.mediaPrintables != null) {
/*  764 */       for (int i = 0; i < this.mediaPrintables.length; i++) {
/*  765 */         if (paramMediaPrintableArea.equals(this.mediaPrintables[i])) {
/*  766 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  770 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isSupportedMediaTray(MediaTray paramMediaTray) {
/*  774 */     MediaTray[] arrayOfMediaTray = getMediaTrays();
/*      */     
/*  776 */     if (arrayOfMediaTray != null) {
/*  777 */       for (int i = 0; i < arrayOfMediaTray.length; i++) {
/*  778 */         if (paramMediaTray.equals(arrayOfMediaTray[i])) {
/*  779 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  783 */     return false;
/*      */   }
/*      */   
/*      */   private int getPrinterCapabilities() {
/*  787 */     if (this.prnCaps == 0) {
/*  788 */       this.prnCaps = getCapabilities(this.printer, getPort());
/*      */     }
/*  790 */     return this.prnCaps;
/*      */   }
/*      */   
/*      */   private String getPort() {
/*  794 */     if (this.port == null) {
/*  795 */       this.port = getPrinterPort(this.printer);
/*      */     }
/*  797 */     return this.port;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int[] getDefaultPrinterSettings()
/*      */   {
/*  804 */     if (this.defaultSettings == null) {
/*  805 */       this.defaultSettings = getDefaultSettings(this.printer, getPort());
/*      */     }
/*  807 */     return this.defaultSettings;
/*      */   }
/*      */   
/*      */   private PrinterResolution[] getPrintResolutions() {
/*  811 */     if (this.printRes == null) {
/*  812 */       int[] arrayOfInt = getAllResolutions(this.printer, getPort());
/*  813 */       if (arrayOfInt == null) {
/*  814 */         this.printRes = new PrinterResolution[0];
/*      */       } else {
/*  816 */         int i = arrayOfInt.length / 2;
/*      */         
/*  818 */         ArrayList localArrayList = new ArrayList();
/*      */         
/*      */ 
/*  821 */         for (int j = 0; j < i; j++) {
/*      */           try {
/*  823 */             PrinterResolution localPrinterResolution = new PrinterResolution(arrayOfInt[(j * 2)], arrayOfInt[(j * 2 + 1)], 100);
/*      */             
/*  825 */             localArrayList.add(localPrinterResolution);
/*      */           }
/*      */           catch (IllegalArgumentException localIllegalArgumentException) {}
/*      */         }
/*      */         
/*  830 */         this.printRes = ((PrinterResolution[])localArrayList.toArray(
/*  831 */           new PrinterResolution[localArrayList.size()]));
/*      */       }
/*      */     }
/*  834 */     return this.printRes;
/*      */   }
/*      */   
/*      */   private boolean isSupportedResolution(PrinterResolution paramPrinterResolution) {
/*  838 */     PrinterResolution[] arrayOfPrinterResolution = getPrintResolutions();
/*  839 */     if (arrayOfPrinterResolution != null) {
/*  840 */       for (int i = 0; i < arrayOfPrinterResolution.length; i++) {
/*  841 */         if (paramPrinterResolution.equals(arrayOfPrinterResolution[i])) {
/*  842 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  846 */     return false;
/*      */   }
/*      */   
/*      */   public DocPrintJob createPrintJob() {
/*  850 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  851 */     if (localSecurityManager != null) {
/*  852 */       localSecurityManager.checkPrintJobAccess();
/*      */     }
/*  854 */     return new Win32PrintJob(this);
/*      */   }
/*      */   
/*      */   private PrintServiceAttributeSet getDynamicAttributes() {
/*  858 */     HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
/*  859 */     localHashPrintServiceAttributeSet.add(getPrinterIsAcceptingJobs());
/*  860 */     localHashPrintServiceAttributeSet.add(getQueuedJobCount());
/*  861 */     return localHashPrintServiceAttributeSet;
/*      */   }
/*      */   
/*      */   public PrintServiceAttributeSet getUpdatedAttributes() {
/*  865 */     PrintServiceAttributeSet localPrintServiceAttributeSet = getDynamicAttributes();
/*  866 */     if (this.lastSet == null) {
/*  867 */       this.lastSet = localPrintServiceAttributeSet;
/*  868 */       return AttributeSetUtilities.unmodifiableView(localPrintServiceAttributeSet);
/*      */     }
/*  870 */     HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
/*      */     
/*  872 */     Attribute[] arrayOfAttribute = localPrintServiceAttributeSet.toArray();
/*  873 */     for (int i = 0; i < arrayOfAttribute.length; i++) {
/*  874 */       Attribute localAttribute = arrayOfAttribute[i];
/*  875 */       if (!this.lastSet.containsValue(localAttribute)) {
/*  876 */         localHashPrintServiceAttributeSet.add(localAttribute);
/*      */       }
/*      */     }
/*  879 */     this.lastSet = localPrintServiceAttributeSet;
/*  880 */     return AttributeSetUtilities.unmodifiableView(localHashPrintServiceAttributeSet);
/*      */   }
/*      */   
/*      */   public void wakeNotifier()
/*      */   {
/*  885 */     synchronized (this) {
/*  886 */       if (this.notifier != null) {
/*  887 */         this.notifier.wake();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void addPrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener)
/*      */   {
/*  894 */     synchronized (this) {
/*  895 */       if (paramPrintServiceAttributeListener == null) {
/*  896 */         return;
/*      */       }
/*  898 */       if (this.notifier == null) {
/*  899 */         this.notifier = new ServiceNotifier(this);
/*      */       }
/*  901 */       this.notifier.addListener(paramPrintServiceAttributeListener);
/*      */     }
/*      */   }
/*      */   
/*      */   public void removePrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener)
/*      */   {
/*  907 */     synchronized (this) {
/*  908 */       if ((paramPrintServiceAttributeListener == null) || (this.notifier == null)) {
/*  909 */         return;
/*      */       }
/*  911 */       this.notifier.removeListener(paramPrintServiceAttributeListener);
/*  912 */       if (this.notifier.isEmpty()) {
/*  913 */         this.notifier.stopNotifier();
/*  914 */         this.notifier = null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public <T extends PrintServiceAttribute> T getAttribute(Class<T> paramClass)
/*      */   {
/*  922 */     if (paramClass == null) {
/*  923 */       throw new NullPointerException("category");
/*      */     }
/*  925 */     if (!PrintServiceAttribute.class.isAssignableFrom(paramClass)) {
/*  926 */       throw new IllegalArgumentException("Not a PrintServiceAttribute");
/*      */     }
/*  928 */     if (paramClass == ColorSupported.class) {
/*  929 */       int i = getPrinterCapabilities();
/*  930 */       if ((i & 0x1) != 0) {
/*  931 */         return ColorSupported.SUPPORTED;
/*      */       }
/*  933 */       return ColorSupported.NOT_SUPPORTED;
/*      */     }
/*  935 */     if (paramClass == PrinterName.class)
/*  936 */       return getPrinterName();
/*  937 */     if (paramClass == PrinterState.class)
/*  938 */       return getPrinterState();
/*  939 */     if (paramClass == PrinterStateReasons.class)
/*  940 */       return getPrinterStateReasons();
/*  941 */     if (paramClass == QueuedJobCount.class)
/*  942 */       return getQueuedJobCount();
/*  943 */     if (paramClass == PrinterIsAcceptingJobs.class) {
/*  944 */       return getPrinterIsAcceptingJobs();
/*      */     }
/*  946 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public PrintServiceAttributeSet getAttributes()
/*      */   {
/*  952 */     HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
/*  953 */     localHashPrintServiceAttributeSet.add(getPrinterName());
/*  954 */     localHashPrintServiceAttributeSet.add(getPrinterIsAcceptingJobs());
/*  955 */     PrinterState localPrinterState = getPrinterState();
/*  956 */     if (localPrinterState != null) {
/*  957 */       localHashPrintServiceAttributeSet.add(localPrinterState);
/*      */     }
/*  959 */     PrinterStateReasons localPrinterStateReasons = getPrinterStateReasons();
/*  960 */     if (localPrinterStateReasons != null) {
/*  961 */       localHashPrintServiceAttributeSet.add(localPrinterStateReasons);
/*      */     }
/*  963 */     localHashPrintServiceAttributeSet.add(getQueuedJobCount());
/*  964 */     int i = getPrinterCapabilities();
/*  965 */     if ((i & 0x1) != 0) {
/*  966 */       localHashPrintServiceAttributeSet.add(ColorSupported.SUPPORTED);
/*      */     } else {
/*  968 */       localHashPrintServiceAttributeSet.add(ColorSupported.NOT_SUPPORTED);
/*      */     }
/*      */     
/*  971 */     return AttributeSetUtilities.unmodifiableView(localHashPrintServiceAttributeSet);
/*      */   }
/*      */   
/*      */   public DocFlavor[] getSupportedDocFlavors() {
/*  975 */     int i = supportedFlavors.length;
/*      */     
/*  977 */     int j = getPrinterCapabilities();
/*      */     
/*      */     DocFlavor[] arrayOfDocFlavor;
/*  980 */     if ((j & 0x10) != 0) {
/*  981 */       arrayOfDocFlavor = new DocFlavor[i + 3];
/*  982 */       System.arraycopy(supportedFlavors, 0, arrayOfDocFlavor, 0, i);
/*  983 */       arrayOfDocFlavor[i] = BYTE_ARRAY.POSTSCRIPT;
/*  984 */       arrayOfDocFlavor[(i + 1)] = INPUT_STREAM.POSTSCRIPT;
/*  985 */       arrayOfDocFlavor[(i + 2)] = URL.POSTSCRIPT;
/*      */     } else {
/*  987 */       arrayOfDocFlavor = new DocFlavor[i];
/*  988 */       System.arraycopy(supportedFlavors, 0, arrayOfDocFlavor, 0, i);
/*      */     }
/*  990 */     return arrayOfDocFlavor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isDocFlavorSupported(DocFlavor paramDocFlavor)
/*      */   {
/*      */     DocFlavor[] arrayOfDocFlavor;
/*      */     
/*  999 */     if (isPostScriptFlavor(paramDocFlavor)) {
/* 1000 */       arrayOfDocFlavor = getSupportedDocFlavors();
/*      */     } else {
/* 1002 */       arrayOfDocFlavor = supportedFlavors;
/*      */     }
/* 1004 */     for (int i = 0; i < arrayOfDocFlavor.length; i++) {
/* 1005 */       if (paramDocFlavor.equals(arrayOfDocFlavor[i])) {
/* 1006 */         return true;
/*      */       }
/*      */     }
/* 1009 */     return false;
/*      */   }
/*      */   
/*      */   public Class<?>[] getSupportedAttributeCategories() {
/* 1013 */     ArrayList localArrayList = new ArrayList(otherAttrCats.length + 3);
/* 1014 */     for (int i = 0; i < otherAttrCats.length; i++) {
/* 1015 */       localArrayList.add(otherAttrCats[i]);
/*      */     }
/*      */     
/* 1018 */     i = getPrinterCapabilities();
/*      */     
/* 1020 */     if ((i & 0x2) != 0) {
/* 1021 */       localArrayList.add(Sides.class);
/*      */     }
/*      */     
/* 1024 */     if ((i & 0x8) != 0) {
/* 1025 */       localObject = getDefaultPrinterSettings();
/*      */       
/* 1027 */       if ((localObject[3] >= -4) && (localObject[3] < 0)) {
/* 1028 */         localArrayList.add(PrintQuality.class);
/*      */       }
/*      */     }
/*      */     
/* 1032 */     Object localObject = getPrintResolutions();
/* 1033 */     if ((localObject != null) && (localObject.length > 0)) {
/* 1034 */       localArrayList.add(PrinterResolution.class);
/*      */     }
/*      */     
/* 1037 */     return (Class[])localArrayList.toArray(new Class[localArrayList.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isAttributeCategorySupported(Class<? extends Attribute> paramClass)
/*      */   {
/* 1044 */     if (paramClass == null) {
/* 1045 */       throw new NullPointerException("null category");
/*      */     }
/*      */     
/* 1048 */     if (!Attribute.class.isAssignableFrom(paramClass)) {
/* 1049 */       throw new IllegalArgumentException(paramClass + " is not an Attribute");
/*      */     }
/*      */     
/*      */ 
/* 1053 */     Class[] arrayOfClass = getSupportedAttributeCategories();
/* 1054 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 1055 */       if (paramClass.equals(arrayOfClass[i])) {
/* 1056 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1060 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public Object getDefaultAttributeValue(Class<? extends Attribute> paramClass)
/*      */   {
/* 1066 */     if (paramClass == null) {
/* 1067 */       throw new NullPointerException("null category");
/*      */     }
/* 1069 */     if (!Attribute.class.isAssignableFrom(paramClass)) {
/* 1070 */       throw new IllegalArgumentException(paramClass + " is not an Attribute");
/*      */     }
/*      */     
/*      */ 
/* 1074 */     if (!isAttributeCategorySupported(paramClass)) {
/* 1075 */       return null;
/*      */     }
/*      */     
/* 1078 */     int[] arrayOfInt = getDefaultPrinterSettings();
/*      */     
/* 1080 */     int i = arrayOfInt[0];
/* 1081 */     SecurityException localSecurityException1 = arrayOfInt[2];
/* 1082 */     URISyntaxException localURISyntaxException1 = arrayOfInt[3];
/* 1083 */     int j = arrayOfInt[4];
/* 1084 */     int k = arrayOfInt[5];
/* 1085 */     int m = arrayOfInt[6];
/* 1086 */     int n = arrayOfInt[7];
/* 1087 */     int i1 = arrayOfInt[8];
/*      */     
/* 1089 */     if (paramClass == Copies.class) {
/* 1090 */       if (j > 0) {
/* 1091 */         return new Copies(j);
/*      */       }
/* 1093 */       return new Copies(1);
/*      */     }
/* 1095 */     if (paramClass == Chromaticity.class) {
/* 1096 */       if (i1 == 2) {
/* 1097 */         return Chromaticity.COLOR;
/*      */       }
/* 1099 */       return Chromaticity.MONOCHROME;
/*      */     }
/* 1101 */     if (paramClass == JobName.class)
/* 1102 */       return new JobName("Java Printing", null);
/* 1103 */     if (paramClass == OrientationRequested.class) {
/* 1104 */       if (k == 2) {
/* 1105 */         return OrientationRequested.LANDSCAPE;
/*      */       }
/* 1107 */       return OrientationRequested.PORTRAIT;
/*      */     }
/* 1109 */     if (paramClass == PageRanges.class)
/* 1110 */       return new PageRanges(1, Integer.MAX_VALUE);
/* 1111 */     MediaSizeName localMediaSizeName; Object localObject; if (paramClass == Media.class) {
/* 1112 */       localMediaSizeName = findWin32Media(i);
/* 1113 */       if (localMediaSizeName != null) {
/* 1114 */         if ((!isSupportedMedia(localMediaSizeName)) && (this.mediaSizeNames != null)) {
/* 1115 */           localMediaSizeName = this.mediaSizeNames[0];
/* 1116 */           i = findPaperID(localMediaSizeName);
/*      */         }
/* 1118 */         return localMediaSizeName;
/*      */       }
/* 1120 */       initMedia();
/* 1121 */       if ((this.mediaSizeNames != null) && (this.mediaSizeNames.length > 0))
/*      */       {
/*      */ 
/* 1124 */         if ((this.idList != null) && (this.mediaSizes != null) && 
/* 1125 */           (this.idList.size() == this.mediaSizes.length)) {
/* 1126 */           localObject = Integer.valueOf(i);
/* 1127 */           int i3 = this.idList.indexOf(localObject);
/* 1128 */           if ((i3 >= 0) && (i3 < this.mediaSizes.length)) {
/* 1129 */             return this.mediaSizes[i3].getMediaSizeName();
/*      */           }
/*      */         }
/*      */         
/* 1133 */         return this.mediaSizeNames[0];
/*      */       }
/*      */     } else {
/* 1136 */       if (paramClass == MediaPrintableArea.class)
/*      */       {
/* 1138 */         localMediaSizeName = findWin32Media(i);
/* 1139 */         if ((localMediaSizeName != null) && 
/* 1140 */           (!isSupportedMedia(localMediaSizeName)) && (this.mediaSizeNames != null)) {
/* 1141 */           i = findPaperID(this.mediaSizeNames[0]);
/*      */         }
/* 1143 */         localObject = getMediaPrintableArea(this.printer, i);
/* 1144 */         if (localObject != null) {
/* 1145 */           MediaPrintableArea localMediaPrintableArea = null;
/*      */           try {
/* 1147 */             localMediaPrintableArea = new MediaPrintableArea(localObject[0], localObject[1], localObject[2], localObject[3], 25400);
/*      */           }
/*      */           catch (IllegalArgumentException localIllegalArgumentException) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1154 */           return localMediaPrintableArea;
/*      */         }
/* 1156 */         return null; }
/* 1157 */       if (paramClass == SunAlternateMedia.class)
/* 1158 */         return null;
/* 1159 */       if (paramClass == Destination.class)
/*      */         try {
/* 1161 */           return new Destination(new File("out.prn").toURI());
/*      */         } catch (SecurityException localSecurityException2) {
/*      */           try {
/* 1164 */             return new Destination(new URI("file:out.prn"));
/*      */           } catch (URISyntaxException localURISyntaxException2) {
/* 1166 */             return null;
/*      */           }
/*      */         }
/* 1169 */       if (paramClass == Sides.class) {
/* 1170 */         switch (m) {
/*      */         case 2: 
/* 1172 */           return Sides.TWO_SIDED_LONG_EDGE;
/*      */         case 3: 
/* 1174 */           return Sides.TWO_SIDED_SHORT_EDGE;
/*      */         }
/* 1176 */         return Sides.ONE_SIDED;
/*      */       }
/* 1178 */       if (paramClass == PrinterResolution.class) {
/* 1179 */         localSecurityException2 = localSecurityException1;
/* 1180 */         localURISyntaxException2 = localURISyntaxException1;
/* 1181 */         if ((localURISyntaxException2 < 0) || (localSecurityException2 < 0)) {
/* 1182 */           int i4 = localSecurityException2 > localURISyntaxException2 ? localSecurityException2 : localURISyntaxException2;
/* 1183 */           if (i4 > 0) {
/* 1184 */             return new PrinterResolution(i4, i4, 100);
/*      */           }
/*      */         }
/*      */         else {
/* 1188 */           return new PrinterResolution(localURISyntaxException2, localSecurityException2, 100);
/*      */         }
/* 1190 */       } else { if (paramClass == ColorSupported.class) {
/* 1191 */           int i2 = getPrinterCapabilities();
/* 1192 */           if ((i2 & 0x1) != 0) {
/* 1193 */             return ColorSupported.SUPPORTED;
/*      */           }
/* 1195 */           return ColorSupported.NOT_SUPPORTED;
/*      */         }
/* 1197 */         if (paramClass == PrintQuality.class) {
/* 1198 */           if ((localURISyntaxException1 < 0) && (localURISyntaxException1 >= -4)) {
/* 1199 */             switch (localURISyntaxException1) {
/*      */             case -4: 
/* 1201 */               return PrintQuality.HIGH;
/*      */             case -3: 
/* 1203 */               return PrintQuality.NORMAL;
/*      */             }
/* 1205 */             return PrintQuality.DRAFT;
/*      */           }
/*      */         } else {
/* 1208 */           if (paramClass == RequestingUserName.class) {
/* 1209 */             String str = "";
/*      */             try {
/* 1211 */               str = System.getProperty("user.name", "");
/*      */             }
/*      */             catch (SecurityException localSecurityException3) {}
/* 1214 */             return new RequestingUserName(str, null); }
/* 1215 */           if (paramClass == SheetCollate.class) {
/* 1216 */             if (n == 1) {
/* 1217 */               return SheetCollate.COLLATED;
/*      */             }
/* 1219 */             return SheetCollate.UNCOLLATED;
/*      */           }
/* 1221 */           if (paramClass == Fidelity.class)
/* 1222 */             return Fidelity.FIDELITY_FALSE;
/*      */         } } }
/* 1224 */     return null;
/*      */   }
/*      */   
/*      */   private boolean isPostScriptFlavor(DocFlavor paramDocFlavor) {
/* 1228 */     if ((paramDocFlavor.equals(BYTE_ARRAY.POSTSCRIPT)) ||
/* 1229 */       (paramDocFlavor.equals(INPUT_STREAM.POSTSCRIPT)) ||
/* 1230 */       (paramDocFlavor.equals(URL.POSTSCRIPT))) {
/* 1231 */       return true;
/*      */     }
/*      */     
/* 1234 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isPSDocAttr(Class paramClass)
/*      */   {
/* 1239 */     if ((paramClass == OrientationRequested.class) || (paramClass == Copies.class)) {
/* 1240 */       return true;
/*      */     }
/*      */     
/* 1243 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isAutoSense(DocFlavor paramDocFlavor)
/*      */   {
/* 1248 */     if ((paramDocFlavor.equals(BYTE_ARRAY.AUTOSENSE)) ||
/* 1249 */       (paramDocFlavor.equals(INPUT_STREAM.AUTOSENSE)) ||
/* 1250 */       (paramDocFlavor.equals(URL.AUTOSENSE))) {
/* 1251 */       return true;
/*      */     }
/*      */     
/* 1254 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getSupportedAttributeValues(Class<? extends Attribute> paramClass, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
/*      */   {
/* 1263 */     if (paramClass == null) {
/* 1264 */       throw new NullPointerException("null category");
/*      */     }
/* 1266 */     if (!Attribute.class.isAssignableFrom(paramClass)) {
/* 1267 */       throw new IllegalArgumentException(paramClass + " does not implement Attribute");
/*      */     }
/*      */     
/* 1270 */     if (paramDocFlavor != null) {
/* 1271 */       if (!isDocFlavorSupported(paramDocFlavor)) {
/* 1272 */         throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor");
/*      */       }
/*      */       
/*      */ 
/* 1276 */       if ((isAutoSense(paramDocFlavor)) || ((isPostScriptFlavor(paramDocFlavor)) && 
/* 1277 */         (isPSDocAttr(paramClass)))) {
/* 1278 */         return null;
/*      */       }
/*      */     }
/* 1281 */     if (!isAttributeCategorySupported(paramClass)) {
/* 1282 */       return null;
/*      */     }
/*      */     
/* 1285 */     if (paramClass == JobName.class)
/* 1286 */       return new JobName("Java Printing", null);
/* 1287 */     if (paramClass == RequestingUserName.class) {
/* 1288 */       String str = "";
/*      */       try {
/* 1290 */         str = System.getProperty("user.name", "");
/*      */       }
/*      */       catch (SecurityException localSecurityException2) {}
/* 1293 */       return new RequestingUserName(str, null); }
/* 1294 */     int i; if (paramClass == ColorSupported.class) {
/* 1295 */       i = getPrinterCapabilities();
/* 1296 */       if ((i & 0x1) != 0) {
/* 1297 */         return ColorSupported.SUPPORTED;
/*      */       }
/* 1299 */       return ColorSupported.NOT_SUPPORTED;
/*      */     }
/* 1301 */     if (paramClass == Chromaticity.class) {
/* 1302 */       if ((paramDocFlavor == null) || 
/* 1303 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PAGEABLE)) ||
/* 1304 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PRINTABLE)) ||
/* 1305 */         (paramDocFlavor.equals(BYTE_ARRAY.GIF)) ||
/* 1306 */         (paramDocFlavor.equals(INPUT_STREAM.GIF)) ||
/* 1307 */         (paramDocFlavor.equals(URL.GIF)) ||
/* 1308 */         (paramDocFlavor.equals(BYTE_ARRAY.JPEG)) ||
/* 1309 */         (paramDocFlavor.equals(INPUT_STREAM.JPEG)) ||
/* 1310 */         (paramDocFlavor.equals(URL.JPEG)) ||
/* 1311 */         (paramDocFlavor.equals(BYTE_ARRAY.PNG)) ||
/* 1312 */         (paramDocFlavor.equals(INPUT_STREAM.PNG)) ||
/* 1313 */         (paramDocFlavor.equals(URL.PNG))) {
/* 1314 */         i = getPrinterCapabilities();
/* 1315 */         if ((i & 0x1) == 0) {
/* 1316 */           arrayOfChromaticity = new Chromaticity[1];
/* 1317 */           arrayOfChromaticity[0] = Chromaticity.MONOCHROME;
/* 1318 */           return arrayOfChromaticity;
/*      */         }
/* 1320 */         Chromaticity[] arrayOfChromaticity = new Chromaticity[2];
/* 1321 */         arrayOfChromaticity[0] = Chromaticity.MONOCHROME;
/* 1322 */         arrayOfChromaticity[1] = Chromaticity.COLOR;
/* 1323 */         return arrayOfChromaticity;
/*      */       }
/*      */       
/* 1326 */       return null;
/*      */     }
/* 1328 */     if (paramClass == Destination.class)
/*      */       try {
/* 1330 */         return new Destination(new File("out.prn").toURI());
/*      */       } catch (SecurityException localSecurityException1) {
/*      */         try {
/* 1333 */           return new Destination(new URI("file:out.prn"));
/*      */         } catch (URISyntaxException localURISyntaxException) {
/* 1335 */           return null;
/*      */         }
/*      */       }
/* 1338 */     if (paramClass == OrientationRequested.class) {
/* 1339 */       if ((paramDocFlavor == null) || 
/* 1340 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PAGEABLE)) ||
/* 1341 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PRINTABLE)) ||
/* 1342 */         (paramDocFlavor.equals(INPUT_STREAM.GIF)) ||
/* 1343 */         (paramDocFlavor.equals(INPUT_STREAM.JPEG)) ||
/* 1344 */         (paramDocFlavor.equals(INPUT_STREAM.PNG)) ||
/* 1345 */         (paramDocFlavor.equals(BYTE_ARRAY.GIF)) ||
/* 1346 */         (paramDocFlavor.equals(BYTE_ARRAY.JPEG)) ||
/* 1347 */         (paramDocFlavor.equals(BYTE_ARRAY.PNG)) ||
/* 1348 */         (paramDocFlavor.equals(URL.GIF)) ||
/* 1349 */         (paramDocFlavor.equals(URL.JPEG)) ||
/* 1350 */         (paramDocFlavor.equals(URL.PNG))) {
/* 1351 */         OrientationRequested[] arrayOfOrientationRequested = new OrientationRequested[3];
/* 1352 */         arrayOfOrientationRequested[0] = OrientationRequested.PORTRAIT;
/* 1353 */         arrayOfOrientationRequested[1] = OrientationRequested.LANDSCAPE;
/* 1354 */         arrayOfOrientationRequested[2] = OrientationRequested.REVERSE_LANDSCAPE;
/* 1355 */         return arrayOfOrientationRequested;
/*      */       }
/* 1357 */       return null;
/*      */     }
/* 1359 */     if ((paramClass == Copies.class) || (paramClass == CopiesSupported.class))
/*      */     {
/* 1361 */       synchronized (this) {
/* 1362 */         if (!this.gotCopies) {
/* 1363 */           this.nCopies = getCopiesSupported(this.printer, getPort());
/* 1364 */           this.gotCopies = true;
/*      */         }
/*      */       }
/* 1367 */       return new CopiesSupported(1, this.nCopies); }
/* 1368 */     Object localObject2; Object localObject4; if (paramClass == Media.class)
/*      */     {
/* 1370 */       initMedia();
/*      */       
/* 1372 */       int j = this.mediaSizeNames == null ? 0 : this.mediaSizeNames.length;
/*      */       
/* 1374 */       localObject2 = getMediaTrays();
/*      */       
/* 1376 */       j += (localObject2 == null ? 0 : localObject2.length);
/*      */       
/* 1378 */       localObject4 = new Media[j];
/* 1379 */       if (this.mediaSizeNames != null) {
/* 1380 */         System.arraycopy(this.mediaSizeNames, 0, localObject4, 0, this.mediaSizeNames.length);
/*      */       }
/*      */       
/* 1383 */       if (localObject2 != null) {
/* 1384 */         System.arraycopy(localObject2, 0, localObject4, j - localObject2.length, localObject2.length);
/*      */       }
/*      */       
/* 1387 */       return localObject4; }
/* 1388 */     Object localObject1; if (paramClass == MediaPrintableArea.class)
/*      */     {
/* 1390 */       localObject1 = null;
/* 1391 */       if (paramAttributeSet != null)
/*      */       {
/* 1393 */         if ((localObject1 = (Media)paramAttributeSet.get(Media.class)) != null)
/*      */         {
/* 1395 */           if (!(localObject1 instanceof MediaSizeName))
/*      */           {
/*      */ 
/* 1398 */             localObject1 = null;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1403 */       localObject2 = getMediaPrintables((MediaSizeName)localObject1);
/* 1404 */       if (localObject2 != null) {
/* 1405 */         localObject4 = new MediaPrintableArea[localObject2.length];
/* 1406 */         System.arraycopy(localObject2, 0, localObject4, 0, localObject2.length);
/* 1407 */         return localObject4;
/*      */       }
/* 1409 */       return null;
/*      */     }
/* 1411 */     if (paramClass == SunAlternateMedia.class)
/* 1412 */       return new SunAlternateMedia(
/* 1413 */         (Media)getDefaultAttributeValue(Media.class));
/* 1414 */     if (paramClass == PageRanges.class) {
/* 1415 */       if ((paramDocFlavor == null) || 
/* 1416 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PAGEABLE)) ||
/* 1417 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PRINTABLE))) {
/* 1418 */         localObject1 = new PageRanges[1];
/* 1419 */         localObject1[0] = new PageRanges(1, Integer.MAX_VALUE);
/* 1420 */         return localObject1;
/*      */       }
/* 1422 */       return null;
/*      */     }
/* 1424 */     if (paramClass == PrinterResolution.class) {
/* 1425 */       localObject1 = getPrintResolutions();
/* 1426 */       if (localObject1 == null) {
/* 1427 */         return null;
/*      */       }
/* 1429 */       localObject2 = new PrinterResolution[localObject1.length];
/*      */       
/* 1431 */       System.arraycopy(localObject1, 0, localObject2, 0, localObject1.length);
/* 1432 */       return localObject2; }
/* 1433 */     if (paramClass == Sides.class) {
/* 1434 */       if ((paramDocFlavor == null) || 
/* 1435 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PAGEABLE)) ||
/* 1436 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PRINTABLE))) {
/* 1437 */         localObject1 = new Sides[3];
/* 1438 */         localObject1[0] = Sides.ONE_SIDED;
/* 1439 */         localObject1[1] = Sides.TWO_SIDED_LONG_EDGE;
/* 1440 */         localObject1[2] = Sides.TWO_SIDED_SHORT_EDGE;
/* 1441 */         return localObject1;
/*      */       }
/* 1443 */       return null;
/*      */     }
/* 1445 */     if (paramClass == PrintQuality.class) {
/* 1446 */       localObject1 = new PrintQuality[3];
/* 1447 */       localObject1[0] = PrintQuality.DRAFT;
/* 1448 */       localObject1[1] = PrintQuality.HIGH;
/* 1449 */       localObject1[2] = PrintQuality.NORMAL;
/* 1450 */       return localObject1; }
/* 1451 */     if (paramClass == SheetCollate.class) {
/* 1452 */       if ((paramDocFlavor == null) || 
/* 1453 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PAGEABLE)) ||
/* 1454 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PRINTABLE))) {
/* 1455 */         localObject1 = new SheetCollate[2];
/* 1456 */         localObject1[0] = SheetCollate.COLLATED;
/* 1457 */         localObject1[1] = SheetCollate.UNCOLLATED;
/* 1458 */         return localObject1;
/*      */       }
/* 1460 */       return null;
/*      */     }
/* 1462 */     if (paramClass == Fidelity.class) {
/* 1463 */       localObject1 = new Fidelity[2];
/* 1464 */       localObject1[0] = Fidelity.FIDELITY_FALSE;
/* 1465 */       localObject1[1] = Fidelity.FIDELITY_TRUE;
/* 1466 */       return localObject1;
/*      */     }
/* 1468 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isAttributeValueSupported(Attribute paramAttribute, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
/*      */   {
/* 1476 */     if (paramAttribute == null) {
/* 1477 */       throw new NullPointerException("null attribute");
/*      */     }
/* 1479 */     Class localClass = paramAttribute.getCategory();
/* 1480 */     if (paramDocFlavor != null) {
/* 1481 */       if (!isDocFlavorSupported(paramDocFlavor)) {
/* 1482 */         throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor");
/*      */       }
/*      */       
/*      */ 
/* 1486 */       if ((isAutoSense(paramDocFlavor)) || ((isPostScriptFlavor(paramDocFlavor)) && 
/* 1487 */         (isPSDocAttr(localClass)))) {
/* 1488 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1492 */     if (!isAttributeCategorySupported(localClass)) {
/* 1493 */       return false;
/*      */     }
/* 1495 */     if (localClass == Chromaticity.class) {
/* 1496 */       if ((paramDocFlavor == null) || 
/* 1497 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PAGEABLE)) ||
/* 1498 */         (paramDocFlavor.equals(SERVICE_FORMATTED.PRINTABLE)) ||
/* 1499 */         (paramDocFlavor.equals(BYTE_ARRAY.GIF)) ||
/* 1500 */         (paramDocFlavor.equals(INPUT_STREAM.GIF)) ||
/* 1501 */         (paramDocFlavor.equals(URL.GIF)) ||
/* 1502 */         (paramDocFlavor.equals(BYTE_ARRAY.JPEG)) ||
/* 1503 */         (paramDocFlavor.equals(INPUT_STREAM.JPEG)) ||
/* 1504 */         (paramDocFlavor.equals(URL.JPEG)) ||
/* 1505 */         (paramDocFlavor.equals(BYTE_ARRAY.PNG)) ||
/* 1506 */         (paramDocFlavor.equals(INPUT_STREAM.PNG)) ||
/* 1507 */         (paramDocFlavor.equals(URL.PNG))) {
/* 1508 */         int i = getPrinterCapabilities();
/* 1509 */         if ((i & 0x1) != 0) {
/* 1510 */           return true;
/*      */         }
/* 1512 */         return paramAttribute == Chromaticity.MONOCHROME;
/*      */       }
/*      */       
/* 1515 */       return false;
/*      */     }
/* 1517 */     if (localClass == Copies.class)
/* 1518 */       return isSupportedCopies((Copies)paramAttribute);
/*      */     Object localObject;
/* 1520 */     if (localClass == Destination.class) {
/* 1521 */       localObject = ((Destination)paramAttribute).getURI();
/* 1522 */       if (("file".equals(((URI)localObject).getScheme())) && 
/* 1523 */         (!((URI)localObject).getSchemeSpecificPart().equals(""))) {
/* 1524 */         return true;
/*      */       }
/* 1526 */       return false;
/*      */     }
/*      */     
/* 1529 */     if (localClass == Media.class) {
/* 1530 */       if ((paramAttribute instanceof MediaSizeName)) {
/* 1531 */         return isSupportedMedia((MediaSizeName)paramAttribute);
/*      */       }
/* 1533 */       if ((paramAttribute instanceof MediaTray)) {
/* 1534 */         return isSupportedMediaTray((MediaTray)paramAttribute);
/*      */       }
/*      */     } else {
/* 1537 */       if (localClass == MediaPrintableArea.class) {
/* 1538 */         return isSupportedMediaPrintableArea((MediaPrintableArea)paramAttribute);
/*      */       }
/* 1540 */       if (localClass == SunAlternateMedia.class) {
/* 1541 */         localObject = ((SunAlternateMedia)paramAttribute).getMedia();
/* 1542 */         return isAttributeValueSupported((Attribute)localObject, paramDocFlavor, paramAttributeSet);
/*      */       }
/* 1544 */       if ((localClass == PageRanges.class) || (localClass == SheetCollate.class) || (localClass == Sides.class))
/*      */       {
/*      */ 
/* 1547 */         if ((paramDocFlavor != null) && 
/* 1548 */           (!paramDocFlavor.equals(SERVICE_FORMATTED.PAGEABLE)) &&
/* 1549 */           (!paramDocFlavor.equals(SERVICE_FORMATTED.PRINTABLE))) {
/* 1550 */           return false;
/*      */         }
/* 1552 */       } else if (localClass == PrinterResolution.class) {
/* 1553 */         if ((paramAttribute instanceof PrinterResolution)) {
/* 1554 */           return isSupportedResolution((PrinterResolution)paramAttribute);
/*      */         }
/* 1556 */       } else if (localClass == OrientationRequested.class) {
/* 1557 */         if (paramAttribute != OrientationRequested.REVERSE_PORTRAIT) { if (paramDocFlavor != null)
/*      */           {
/* 1559 */             if ((paramDocFlavor.equals(SERVICE_FORMATTED.PAGEABLE)) ||
/* 1560 */               (paramDocFlavor.equals(SERVICE_FORMATTED.PRINTABLE)) ||
/* 1561 */               (paramDocFlavor.equals(INPUT_STREAM.GIF)) ||
/* 1562 */               (paramDocFlavor.equals(INPUT_STREAM.JPEG)) ||
/* 1563 */               (paramDocFlavor.equals(INPUT_STREAM.PNG)) ||
/* 1564 */               (paramDocFlavor.equals(BYTE_ARRAY.GIF)) ||
/* 1565 */               (paramDocFlavor.equals(BYTE_ARRAY.JPEG)) ||
/* 1566 */               (paramDocFlavor.equals(BYTE_ARRAY.PNG)) ||
/* 1567 */               (paramDocFlavor.equals(URL.GIF)) ||
/* 1568 */               (paramDocFlavor.equals(URL.JPEG)) ||
/* 1569 */               (paramDocFlavor.equals(URL.PNG))) {} }
/* 1570 */         } else { return false;
/*      */         }
/*      */       }
/* 1573 */       else if (localClass == ColorSupported.class) {
/* 1574 */         int j = getPrinterCapabilities();
/* 1575 */         int k = (j & 0x1) != 0 ? 1 : 0;
/* 1576 */         if (((k == 0) && (paramAttribute == ColorSupported.SUPPORTED)) || ((k != 0) && (paramAttribute == ColorSupported.NOT_SUPPORTED)))
/*      */         {
/* 1578 */           return false; }
/*      */       }
/*      */     }
/* 1581 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public AttributeSet getUnsupportedAttributes(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
/*      */   {
/* 1587 */     if ((paramDocFlavor != null) && (!isDocFlavorSupported(paramDocFlavor))) {
/* 1588 */       throw new IllegalArgumentException("flavor " + paramDocFlavor + "is not supported");
/*      */     }
/*      */     
/*      */ 
/* 1592 */     if (paramAttributeSet == null) {
/* 1593 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 1597 */     HashAttributeSet localHashAttributeSet = new HashAttributeSet();
/* 1598 */     Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
/* 1599 */     for (int i = 0; i < arrayOfAttribute.length; i++) {
/*      */       try {
/* 1601 */         Attribute localAttribute = arrayOfAttribute[i];
/* 1602 */         if (!isAttributeCategorySupported(localAttribute.getCategory())) {
/* 1603 */           localHashAttributeSet.add(localAttribute);
/*      */         }
/* 1605 */         else if (!isAttributeValueSupported(localAttribute, paramDocFlavor, paramAttributeSet)) {
/* 1606 */           localHashAttributeSet.add(localAttribute);
/*      */         }
/*      */       }
/*      */       catch (ClassCastException localClassCastException) {}
/*      */     }
/* 1611 */     if (localHashAttributeSet.isEmpty()) {
/* 1612 */       return null;
/*      */     }
/* 1614 */     return localHashAttributeSet;
/*      */   }
/*      */   
/*      */ 
/* 1618 */   private Win32DocumentPropertiesUI docPropertiesUI = null;
/*      */   
/*      */   private static class Win32DocumentPropertiesUI extends DocumentPropertiesUI
/*      */   {
/*      */     Win32PrintService service;
/*      */     
/*      */     private Win32DocumentPropertiesUI(Win32PrintService paramWin32PrintService)
/*      */     {
/* 1626 */       this.service = paramWin32PrintService;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public PrintRequestAttributeSet showDocumentProperties(PrinterJob paramPrinterJob, Window paramWindow, PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*      */     {
/* 1635 */       if (!(paramPrinterJob instanceof WPrinterJob)) {
/* 1636 */         return null;
/*      */       }
/* 1638 */       WPrinterJob localWPrinterJob = (WPrinterJob)paramPrinterJob;
/* 1639 */       return localWPrinterJob.showDocumentProperties(paramWindow, paramPrintService, paramPrintRequestAttributeSet);
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized DocumentPropertiesUI getDocumentPropertiesUI() {
/* 1644 */     return new Win32DocumentPropertiesUI(this, null);
/*      */   }
/*      */   
/*      */   private static class Win32ServiceUIFactory extends ServiceUIFactory
/*      */   {
/*      */     Win32PrintService service;
/*      */     
/*      */     Win32ServiceUIFactory(Win32PrintService paramWin32PrintService) {
/* 1652 */       this.service = paramWin32PrintService;
/*      */     }
/*      */     
/*      */     public Object getUI(int paramInt, String paramString) {
/* 1656 */       if (paramInt <= 3) {
/* 1657 */         return null;
/*      */       }
/* 1659 */       if ((paramInt == 199) && 
/* 1660 */         (DocumentPropertiesUI.DOCPROPERTIESCLASSNAME.equals(paramString)))
/*      */       {
/* 1662 */         return this.service.getDocumentPropertiesUI();
/*      */       }
/* 1664 */       throw new IllegalArgumentException("Unsupported role");
/*      */     }
/*      */     
/*      */     public String[] getUIClassNamesForRole(int paramInt)
/*      */     {
/* 1669 */       if (paramInt <= 3) {
/* 1670 */         return null;
/*      */       }
/* 1672 */       if (paramInt == 199) {
/* 1673 */         String[] arrayOfString = new String[0];
/* 1674 */         arrayOfString[0] = DocumentPropertiesUI.DOCPROPERTIESCLASSNAME;
/* 1675 */         return arrayOfString;
/*      */       }
/* 1677 */       throw new IllegalArgumentException("Unsupported role");
/*      */     }
/*      */   }
/*      */   
/* 1681 */   private Win32ServiceUIFactory uiFactory = null;
/*      */   
/*      */   public synchronized ServiceUIFactory getServiceUIFactory() {
/* 1684 */     if (this.uiFactory == null) {
/* 1685 */       this.uiFactory = new Win32ServiceUIFactory(this);
/*      */     }
/* 1687 */     return this.uiFactory;
/*      */   }
/*      */   
/*      */   public String toString() {
/* 1691 */     return "Win32 Printer : " + getName();
/*      */   }
/*      */   
/*      */   public boolean equals(Object paramObject) {
/* 1695 */     if (paramObject != this) if (!(paramObject instanceof Win32PrintService)) break label33; label33: return ((Win32PrintService)paramObject)
/*      */     
/* 1697 */       .getName().equals(getName());
/*      */   }
/*      */   
/*      */   public int hashCode() {
/* 1701 */     return getClass().hashCode() + getName().hashCode();
/*      */   }
/*      */   
/*      */   public boolean usesClass(Class paramClass) {
/* 1705 */     return paramClass == WPrinterJob.class;
/*      */   }
/*      */   
/*      */   private native int[] getAllMediaIDs(String paramString1, String paramString2);
/*      */   
/*      */   private native int[] getAllMediaSizes(String paramString1, String paramString2);
/*      */   
/*      */   private native int[] getAllMediaTrays(String paramString1, String paramString2);
/*      */   
/*      */   private native float[] getMediaPrintableArea(String paramString, int paramInt);
/*      */   
/*      */   private native String[] getAllMediaNames(String paramString1, String paramString2);
/*      */   
/*      */   private native String[] getAllMediaTrayNames(String paramString1, String paramString2);
/*      */   
/*      */   private native int getCopiesSupported(String paramString1, String paramString2);
/*      */   
/*      */   private native int[] getAllResolutions(String paramString1, String paramString2);
/*      */   
/*      */   private native int getCapabilities(String paramString1, String paramString2);
/*      */   
/*      */   private native int[] getDefaultSettings(String paramString1, String paramString2);
/*      */   
/*      */   private native int getJobStatus(String paramString, int paramInt);
/*      */   
/*      */   private native String getPrinterPort(String paramString);
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\print\Win32PrintService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
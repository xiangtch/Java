/*      */ package sun.print;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Shape;
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.print.PageFormat;
/*      */ import java.awt.print.Pageable;
/*      */ import java.awt.print.Paper;
/*      */ import java.awt.print.Printable;
/*      */ import java.awt.print.PrinterException;
/*      */ import java.awt.print.PrinterIOException;
/*      */ import java.awt.print.PrinterJob;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.lang.reflect.Method;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.CoderMalfunctionError;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.Properties;
/*      */ import javax.print.PrintService;
/*      */ import javax.print.StreamPrintService;
/*      */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintRequestAttributeSet;
/*      */ import javax.print.attribute.PrintServiceAttributeSet;
/*      */ import javax.print.attribute.standard.Copies;
/*      */ import javax.print.attribute.standard.Destination;
/*      */ import javax.print.attribute.standard.DialogTypeSelection;
/*      */ import javax.print.attribute.standard.JobName;
/*      */ import javax.print.attribute.standard.PrinterName;
/*      */ import javax.print.attribute.standard.Sides;
/*      */ import sun.awt.CharsetString;
/*      */ import sun.awt.FontConfiguration;
/*      */ import sun.awt.FontDescriptor;
/*      */ import sun.awt.PlatformFont;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.font.Font2D;
/*      */ import sun.font.FontUtilities;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PSPrinterJob
/*      */   extends RasterPrinterJob
/*      */ {
/*      */   protected static final int FILL_EVEN_ODD = 1;
/*      */   protected static final int FILL_WINDING = 2;
/*      */   private static final int MAX_PSSTR = 65535;
/*      */   private static final int RED_MASK = 16711680;
/*      */   private static final int GREEN_MASK = 65280;
/*      */   private static final int BLUE_MASK = 255;
/*      */   private static final int RED_SHIFT = 16;
/*      */   private static final int GREEN_SHIFT = 8;
/*      */   private static final int BLUE_SHIFT = 0;
/*      */   private static final int LOWNIBBLE_MASK = 15;
/*      */   private static final int HINIBBLE_MASK = 240;
/*      */   private static final int HINIBBLE_SHIFT = 4;
/*  145 */   private static final byte[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int PS_XRES = 300;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int PS_YRES = 300;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String ADOBE_PS_STR = "%!PS-Adobe-3.0";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String EOF_COMMENT = "%%EOF";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String PAGE_COMMENT = "%%Page: ";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String READIMAGEPROC = "/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String COPIES = "/#copies exch def";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String PAGE_SAVE = "/pgSave save def";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String PAGE_RESTORE = "pgSave restore";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String SHOWPAGE = "showpage";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String IMAGE_SAVE = "/imSave save def";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String IMAGE_STR = " string /imStr exch def";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String IMAGE_RESTORE = "imSave restore";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String COORD_PREP = " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String SetFontName = "F";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String DrawStringName = "S";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String EVEN_ODD_FILL_STR = "EF";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String WINDING_FILL_STR = "WF";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String EVEN_ODD_CLIP_STR = "EC";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String WINDING_CLIP_STR = "WC";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String MOVETO_STR = " M";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String LINETO_STR = " L";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String CURVETO_STR = " C";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String GRESTORE_STR = "R";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String GSAVE_STR = "G";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String NEWPATH_STR = "N";
/*      */   
/*      */ 
/*      */   private static final String CLOSEPATH_STR = "P";
/*      */   
/*      */ 
/*      */   private static final String SETRGBCOLOR_STR = " SC";
/*      */   
/*      */ 
/*      */   private static final String SETGRAY_STR = " SG";
/*      */   
/*      */ 
/*      */   private int mDestType;
/*      */   
/*      */ 
/*  264 */   private String mDestination = "lp";
/*      */   
/*  266 */   private boolean mNoJobSheet = false;
/*      */   
/*      */ 
/*      */   private String mOptions;
/*      */   
/*      */   private Font mLastFont;
/*      */   
/*      */   private Color mLastColor;
/*      */   
/*      */   private Shape mLastClip;
/*      */   
/*      */   private AffineTransform mLastTransform;
/*      */   
/*  279 */   private EPSPrinter epsPrinter = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   FontMetrics mCurMetrics;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   PrintStream mPSStream;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   File spoolFile;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  301 */   private String mFillOpStr = "WF";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  308 */   private String mClipOpStr = "WC";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  313 */   ArrayList mGStateStack = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private float mPenX;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private float mPenY;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private float mStartPathX;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private float mStartPathY;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  340 */   private static Properties mFontProps = null;
/*      */   
/*      */ 
/*      */   private static boolean isMac;
/*      */   
/*      */ 
/*      */   static
/*      */   {
/*  348 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/*  351 */         PSPrinterJob.access$002(PSPrinterJob.access$100());
/*  352 */         String str = System.getProperty("os.name");
/*  353 */         PSPrinterJob.access$202(str.startsWith("Mac"));
/*  354 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Properties initProps()
/*      */   {
/*  367 */     String str1 = System.getProperty("java.home");
/*      */     
/*  369 */     if (str1 != null) {
/*  370 */       String str2 = SunToolkit.getStartupLocale().getLanguage();
/*      */       try
/*      */       {
/*  373 */         File localFile = new File(str1 + File.separator + "lib" + File.separator + "psfontj2d.properties." + str2);
/*      */         
/*      */ 
/*      */ 
/*  377 */         if (!localFile.canRead())
/*      */         {
/*  379 */           localFile = new File(str1 + File.separator + "lib" + File.separator + "psfont.properties." + str2);
/*      */           
/*      */ 
/*  382 */           if (!localFile.canRead())
/*      */           {
/*  384 */             localFile = new File(str1 + File.separator + "lib" + File.separator + "psfontj2d.properties");
/*      */             
/*      */ 
/*  387 */             if (!localFile.canRead())
/*      */             {
/*  389 */               localFile = new File(str1 + File.separator + "lib" + File.separator + "psfont.properties");
/*      */               
/*      */ 
/*  392 */               if (!localFile.canRead()) {
/*  393 */                 return (Properties)null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  401 */         BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(localFile.getPath()));
/*  402 */         Properties localProperties = new Properties();
/*  403 */         localProperties.load(localBufferedInputStream);
/*  404 */         localBufferedInputStream.close();
/*  405 */         return localProperties;
/*      */       } catch (Exception localException) {
/*  407 */         return (Properties)null;
/*      */       }
/*      */     }
/*  410 */     return (Properties)null;
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
/*      */   public boolean printDialog()
/*      */     throws HeadlessException
/*      */   {
/*  432 */     if (GraphicsEnvironment.isHeadless()) {
/*  433 */       throw new HeadlessException();
/*      */     }
/*      */     
/*  436 */     if (this.attributes == null) {
/*  437 */       this.attributes = new HashPrintRequestAttributeSet();
/*      */     }
/*  439 */     this.attributes.add(new Copies(getCopies()));
/*  440 */     this.attributes.add(new JobName(getJobName(), null));
/*      */     
/*  442 */     boolean bool = false;
/*      */     
/*  444 */     DialogTypeSelection localDialogTypeSelection = (DialogTypeSelection)this.attributes.get(DialogTypeSelection.class);
/*  445 */     if (localDialogTypeSelection == DialogTypeSelection.NATIVE)
/*      */     {
/*      */ 
/*  448 */       this.attributes.remove(DialogTypeSelection.class);
/*  449 */       bool = printDialog(this.attributes);
/*      */       
/*  451 */       this.attributes.add(DialogTypeSelection.NATIVE);
/*      */     } else {
/*  453 */       bool = printDialog(this.attributes);
/*      */     }
/*      */     
/*  456 */     if (bool) {
/*  457 */       JobName localJobName = (JobName)this.attributes.get(JobName.class);
/*  458 */       if (localJobName != null) {
/*  459 */         setJobName(localJobName.getValue());
/*      */       }
/*  461 */       Copies localCopies = (Copies)this.attributes.get(Copies.class);
/*  462 */       if (localCopies != null) {
/*  463 */         setCopies(localCopies.getValue());
/*      */       }
/*      */       
/*  466 */       Destination localDestination = (Destination)this.attributes.get(Destination.class);
/*      */       
/*  468 */       if (localDestination != null) {
/*      */         try {
/*  470 */           this.mDestType = 1;
/*  471 */           this.mDestination = new File(localDestination.getURI()).getPath();
/*      */         } catch (Exception localException) {
/*  473 */           this.mDestination = "out.ps";
/*      */         }
/*      */       } else {
/*  476 */         this.mDestType = 0;
/*  477 */         PrintService localPrintService = getPrintService();
/*  478 */         if (localPrintService != null) {
/*  479 */           this.mDestination = localPrintService.getName();
/*  480 */           if (isMac) {
/*  481 */             PrintServiceAttributeSet localPrintServiceAttributeSet = localPrintService.getAttributes();
/*  482 */             if (localPrintServiceAttributeSet != null) {
/*  483 */               this.mDestination = localPrintServiceAttributeSet.get(PrinterName.class).toString();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  490 */     return bool;
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
/*      */   protected void startDoc()
/*      */     throws PrinterException
/*      */   {
/*  508 */     if (this.epsPrinter == null) { Object localObject;
/*  509 */       if ((getPrintService() instanceof PSStreamPrintService)) {
/*  510 */         StreamPrintService localStreamPrintService = (StreamPrintService)getPrintService();
/*  511 */         this.mDestType = 2;
/*  512 */         if (localStreamPrintService.isDisposed()) {
/*  513 */           throw new PrinterException("service is disposed");
/*      */         }
/*  515 */         localObject = localStreamPrintService.getOutputStream();
/*  516 */         if (localObject == null) {
/*  517 */           throw new PrinterException("Null output stream");
/*      */         }
/*      */       }
/*      */       else {
/*  521 */         this.mNoJobSheet = this.noJobSheet;
/*  522 */         if (this.destinationAttr != null) {
/*  523 */           this.mDestType = 1;
/*  524 */           this.mDestination = this.destinationAttr;
/*      */         }
/*  526 */         if (this.mDestType == 1) {
/*      */           try {
/*  528 */             this.spoolFile = new File(this.mDestination);
/*  529 */             localObject = new FileOutputStream(this.spoolFile);
/*      */           } catch (IOException localIOException) {
/*  531 */             throw new PrinterIOException(localIOException);
/*      */           }
/*      */         } else {
/*  534 */           PrinterOpener localPrinterOpener = new PrinterOpener(null);
/*  535 */           AccessController.doPrivileged(localPrinterOpener);
/*  536 */           if (localPrinterOpener.pex != null) {
/*  537 */             throw localPrinterOpener.pex;
/*      */           }
/*  539 */           localObject = localPrinterOpener.result;
/*      */         }
/*      */       }
/*      */       
/*  543 */       this.mPSStream = new PrintStream(new BufferedOutputStream((OutputStream)localObject));
/*  544 */       this.mPSStream.println("%!PS-Adobe-3.0");
/*      */     }
/*      */     
/*  547 */     this.mPSStream.println("%%BeginProlog");
/*  548 */     this.mPSStream.println("/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def");
/*  549 */     this.mPSStream.println("/BD {bind def} bind def");
/*  550 */     this.mPSStream.println("/D {def} BD");
/*  551 */     this.mPSStream.println("/C {curveto} BD");
/*  552 */     this.mPSStream.println("/L {lineto} BD");
/*  553 */     this.mPSStream.println("/M {moveto} BD");
/*  554 */     this.mPSStream.println("/R {grestore} BD");
/*  555 */     this.mPSStream.println("/G {gsave} BD");
/*  556 */     this.mPSStream.println("/N {newpath} BD");
/*  557 */     this.mPSStream.println("/P {closepath} BD");
/*  558 */     this.mPSStream.println("/EC {eoclip} BD");
/*  559 */     this.mPSStream.println("/WC {clip} BD");
/*  560 */     this.mPSStream.println("/EF {eofill} BD");
/*  561 */     this.mPSStream.println("/WF {fill} BD");
/*  562 */     this.mPSStream.println("/SG {setgray} BD");
/*  563 */     this.mPSStream.println("/SC {setrgbcolor} BD");
/*  564 */     this.mPSStream.println("/ISOF {");
/*  565 */     this.mPSStream.println("     dup findfont dup length 1 add dict begin {");
/*  566 */     this.mPSStream.println("             1 index /FID eq {pop pop} {D} ifelse");
/*  567 */     this.mPSStream.println("     } forall /Encoding ISOLatin1Encoding D");
/*  568 */     this.mPSStream.println("     currentdict end definefont");
/*  569 */     this.mPSStream.println("} BD");
/*  570 */     this.mPSStream.println("/NZ {dup 1 lt {pop 1} if} BD");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  579 */     this.mPSStream.println("/S {");
/*  580 */     this.mPSStream.println("     moveto 1 index stringwidth pop NZ sub");
/*  581 */     this.mPSStream.println("     1 index length 1 sub NZ div 0");
/*  582 */     this.mPSStream.println("     3 2 roll ashow newpath} BD");
/*  583 */     this.mPSStream.println("/FL [");
/*  584 */     if (mFontProps == null) {
/*  585 */       this.mPSStream.println(" /Helvetica ISOF");
/*  586 */       this.mPSStream.println(" /Helvetica-Bold ISOF");
/*  587 */       this.mPSStream.println(" /Helvetica-Oblique ISOF");
/*  588 */       this.mPSStream.println(" /Helvetica-BoldOblique ISOF");
/*  589 */       this.mPSStream.println(" /Times-Roman ISOF");
/*  590 */       this.mPSStream.println(" /Times-Bold ISOF");
/*  591 */       this.mPSStream.println(" /Times-Italic ISOF");
/*  592 */       this.mPSStream.println(" /Times-BoldItalic ISOF");
/*  593 */       this.mPSStream.println(" /Courier ISOF");
/*  594 */       this.mPSStream.println(" /Courier-Bold ISOF");
/*  595 */       this.mPSStream.println(" /Courier-Oblique ISOF");
/*  596 */       this.mPSStream.println(" /Courier-BoldOblique ISOF");
/*      */     } else {
/*  598 */       int i = Integer.parseInt(mFontProps.getProperty("font.num", "9"));
/*  599 */       for (int j = 0; j < i; j++) {
/*  600 */         this.mPSStream.println("    /" + mFontProps
/*  601 */           .getProperty(new StringBuilder().append("font.").append(String.valueOf(j)).toString(), "Courier ISOF"));
/*      */       }
/*      */     }
/*  604 */     this.mPSStream.println("] D");
/*      */     
/*  606 */     this.mPSStream.println("/F {");
/*  607 */     this.mPSStream.println("     FL exch get exch scalefont");
/*  608 */     this.mPSStream.println("     [1 0 0 -1 0 0] makefont setfont} BD");
/*      */     
/*  610 */     this.mPSStream.println("%%EndProlog");
/*      */     
/*  612 */     this.mPSStream.println("%%BeginSetup");
/*  613 */     if (this.epsPrinter == null)
/*      */     {
/*  615 */       PageFormat localPageFormat = getPageable().getPageFormat(0);
/*  616 */       double d1 = localPageFormat.getPaper().getHeight();
/*  617 */       double d2 = localPageFormat.getPaper().getWidth();
/*      */       
/*      */ 
/*      */ 
/*  621 */       this.mPSStream.print("<< /PageSize [" + d2 + " " + d1 + "]");
/*      */       
/*      */ 
/*  624 */       final PrintService localPrintService = getPrintService();
/*  625 */       Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run() {
/*      */           try {
/*  629 */             Class localClass = Class.forName("sun.print.IPPPrintService");
/*  630 */             if (localClass.isInstance(localPrintService)) {
/*  631 */               Method localMethod = localClass.getMethod("isPostscript", (Class[])null);
/*      */               
/*  633 */               return (Boolean)localMethod.invoke(localPrintService, (Object[])null);
/*      */             }
/*      */           }
/*      */           catch (Throwable localThrowable) {}
/*  637 */           return Boolean.TRUE;
/*      */         }
/*      */       });
/*      */       
/*  641 */       if (localBoolean.booleanValue()) {
/*  642 */         this.mPSStream.print(" /DeferredMediaSelection true");
/*      */       }
/*      */       
/*  645 */       this.mPSStream.print(" /ImagingBBox null /ManualFeed false");
/*  646 */       this.mPSStream.print(isCollated() ? " /Collate true" : "");
/*  647 */       this.mPSStream.print(" /NumCopies " + getCopiesInt());
/*      */       
/*  649 */       if (this.sidesAttr != Sides.ONE_SIDED) {
/*  650 */         if (this.sidesAttr == Sides.TWO_SIDED_LONG_EDGE) {
/*  651 */           this.mPSStream.print(" /Duplex true ");
/*  652 */         } else if (this.sidesAttr == Sides.TWO_SIDED_SHORT_EDGE) {
/*  653 */           this.mPSStream.print(" /Duplex true /Tumble true ");
/*      */         }
/*      */       }
/*  656 */       this.mPSStream.println(" >> setpagedevice ");
/*      */     }
/*  658 */     this.mPSStream.println("%%EndSetup");
/*      */   }
/*      */   
/*      */ 
/*      */   private class PrinterOpener
/*      */     implements PrivilegedAction
/*      */   {
/*      */     PrinterException pex;
/*      */     
/*      */     OutputStream result;
/*      */     
/*      */     private PrinterOpener() {}
/*      */     
/*      */     public Object run()
/*      */     {
/*      */       try
/*      */       {
/*  675 */         PSPrinterJob.this.spoolFile = Files.createTempFile("javaprint", ".ps", new FileAttribute[0]).toFile();
/*  676 */         PSPrinterJob.this.spoolFile.deleteOnExit();
/*      */         
/*  678 */         this.result = new FileOutputStream(PSPrinterJob.this.spoolFile);
/*  679 */         return this.result;
/*      */       }
/*      */       catch (IOException localIOException) {
/*  682 */         this.pex = new PrinterIOException(localIOException);
/*      */       }
/*  684 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private class PrinterSpooler implements PrivilegedAction
/*      */   {
/*      */     PrinterException pex;
/*      */     
/*      */     private PrinterSpooler() {}
/*      */     
/*      */     private void handleProcessFailure(Process paramProcess, String[] paramArrayOfString, int paramInt) throws IOException {
/*  695 */       StringWriter localStringWriter = new StringWriter();Object localObject1 = null;
/*  696 */       try { PrintWriter localPrintWriter = new PrintWriter(localStringWriter);Object localObject2 = null;
/*  697 */         try { localPrintWriter.append("error=").append(Integer.toString(paramInt));
/*  698 */           localPrintWriter.append(" running:");
/*  699 */           Object localObject5; for (localObject5 : paramArrayOfString)
/*  700 */             localPrintWriter.append(" '").append((CharSequence)localObject5).append("'");
/*      */           try {
/*  702 */             ??? = paramProcess.getErrorStream();Object localObject4 = null;
/*  703 */             try { InputStreamReader localInputStreamReader = new InputStreamReader((InputStream)???);localObject5 = null;
/*  704 */               try { BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);Object localObject6 = null;
/*  705 */                 try { while (localBufferedReader.ready()) {
/*  706 */                     localPrintWriter.println();
/*  707 */                     localPrintWriter.append("\t\t").append(localBufferedReader.readLine());
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable localThrowable8)
/*      */                 {
/*  702 */                   localObject6 = localThrowable8;throw localThrowable8; } finally {} } catch (Throwable localThrowable6) { localObject5 = localThrowable6;throw localThrowable6;
/*      */ 
/*      */ 
/*      */               }
/*      */               finally
/*      */               {
/*      */ 
/*  709 */                 if (localInputStreamReader != null) if (localObject5 != null) try {}catch (Throwable localThrowable10) { ((Throwable)localObject5).addSuppressed(localThrowable10);
/*      */                     }
/*      */               }
/*      */             }
/*      */             catch (Throwable localThrowable4)
/*      */             {
/*  702 */               localObject4 = localThrowable4;throw localThrowable4;
/*      */ 
/*      */ 
/*      */             }
/*      */             finally
/*      */             {
/*      */ 
/*  709 */               if (??? != null) if (localObject4 != null) try { ((InputStream)???).close(); } catch (Throwable localThrowable11) { ((Throwable)localObject4).addSuppressed(localThrowable11); } else ((InputStream)???).close();
/*      */             }
/*  711 */             throw new IOException(localStringWriter.toString());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           finally
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  710 */             localPrintWriter.flush();
/*      */           }
/*      */         }
/*      */         catch (Throwable localThrowable2)
/*      */         {
/*  695 */           localThrowable2 = 
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  711 */             localThrowable2;localObject2 = localThrowable2;throw localThrowable2;
/*      */         } finally {
/*  713 */           localObject11 = finally; if (localPrintWriter != null) if (localObject2 != null) try { localPrintWriter.close(); } catch (Throwable localThrowable12) { ((Throwable)localObject2).addSuppressed(localThrowable12); } else localPrintWriter.close(); throw ((Throwable)localObject11);
/*      */         }
/*      */       }
/*      */       catch (Throwable localThrowable1)
/*      */       {
/*  695 */         localThrowable1 = 
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  713 */           localThrowable1;localObject1 = localThrowable1;throw localThrowable1; } finally { localObject12 = finally; if (localStringWriter != null) if (localObject1 != null) try { localStringWriter.close(); } catch (Throwable localThrowable13) { ((Throwable)localObject1).addSuppressed(localThrowable13); } else localStringWriter.close(); throw ((Throwable)localObject12);
/*      */       }
/*      */     }
/*      */     
/*  717 */     public Object run() { if ((PSPrinterJob.this.spoolFile == null) || (!PSPrinterJob.this.spoolFile.exists())) {
/*  718 */         this.pex = new PrinterException("No spool file");
/*  719 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */       try
/*      */       {
/*  725 */         String str = PSPrinterJob.this.spoolFile.getAbsolutePath();
/*  726 */         String[] arrayOfString = PSPrinterJob.this.printExecCmd(PSPrinterJob.this.mDestination, PSPrinterJob.this.mOptions, 
/*  727 */           PSPrinterJob.this.mNoJobSheet, PSPrinterJob.this.getJobNameInt(), 1, str);
/*      */         
/*      */ 
/*  730 */         Process localProcess = Runtime.getRuntime().exec(arrayOfString);
/*  731 */         localProcess.waitFor();
/*  732 */         int i = localProcess.exitValue();
/*  733 */         if (0 != i) {
/*  734 */           handleProcessFailure(localProcess, arrayOfString, i);
/*      */         }
/*      */       } catch (IOException localIOException) {
/*  737 */         this.pex = new PrinterIOException(localIOException);
/*      */       } catch (InterruptedException localInterruptedException) {
/*  739 */         this.pex = new PrinterException(localInterruptedException.toString());
/*      */       } finally {
/*  741 */         PSPrinterJob.this.spoolFile.delete();
/*      */       }
/*  743 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void abortDoc()
/*      */   {
/*  752 */     if ((this.mPSStream != null) && (this.mDestType != 2)) {
/*  753 */       this.mPSStream.close();
/*      */     }
/*  755 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  759 */         if ((PSPrinterJob.this.spoolFile != null) && (PSPrinterJob.this.spoolFile.exists())) {
/*  760 */           PSPrinterJob.this.spoolFile.delete();
/*      */         }
/*  762 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void endDoc()
/*      */     throws PrinterException
/*      */   {
/*  773 */     if (this.mPSStream != null) {
/*  774 */       this.mPSStream.println("%%EOF");
/*  775 */       this.mPSStream.flush();
/*  776 */       if (this.mDestType != 2) {
/*  777 */         this.mPSStream.close();
/*      */       }
/*      */     }
/*  780 */     if (this.mDestType == 0) {
/*  781 */       PrintService localPrintService = getPrintService();
/*  782 */       if (localPrintService != null) {
/*  783 */         this.mDestination = localPrintService.getName();
/*  784 */         if (isMac) {
/*  785 */           localObject = localPrintService.getAttributes();
/*  786 */           if (localObject != null) {
/*  787 */             this.mDestination = ((PrintServiceAttributeSet)localObject).get(PrinterName.class).toString();
/*      */           }
/*      */         }
/*      */       }
/*  791 */       Object localObject = new PrinterSpooler(null);
/*  792 */       AccessController.doPrivileged((PrivilegedAction)localObject);
/*  793 */       if (((PrinterSpooler)localObject).pex != null) {
/*  794 */         throw ((PrinterSpooler)localObject).pex;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean)
/*      */     throws PrinterException
/*      */   {
/*  807 */     double d1 = paramPageFormat.getPaper().getHeight();
/*  808 */     double d2 = paramPageFormat.getPaper().getWidth();
/*  809 */     int i = paramInt + 1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  815 */     this.mGStateStack = new ArrayList();
/*  816 */     this.mGStateStack.add(new GState());
/*      */     
/*  818 */     this.mPSStream.println("%%Page: " + i + " " + i);
/*      */     
/*      */ 
/*      */ 
/*  822 */     if ((paramInt > 0) && (paramBoolean))
/*      */     {
/*  824 */       this.mPSStream.print("<< /PageSize [" + d2 + " " + d1 + "]");
/*      */       
/*      */ 
/*  827 */       final PrintService localPrintService = getPrintService();
/*      */       
/*  829 */       Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */ 
/*      */         public Object run()
/*      */         {
/*      */           try
/*      */           {
/*  835 */             Class localClass = Class.forName("sun.print.IPPPrintService");
/*  836 */             if (localClass.isInstance(localPrintService))
/*      */             {
/*  838 */               Method localMethod = localClass.getMethod("isPostscript", (Class[])null);
/*      */               
/*  840 */               return 
/*  841 */                 (Boolean)localMethod.invoke(localPrintService, (Object[])null);
/*      */             }
/*      */           }
/*      */           catch (Throwable localThrowable) {}
/*      */           
/*  846 */           return Boolean.TRUE;
/*      */         }
/*      */       });
/*      */       
/*      */ 
/*  851 */       if (localBoolean.booleanValue()) {
/*  852 */         this.mPSStream.print(" /DeferredMediaSelection true");
/*      */       }
/*  854 */       this.mPSStream.println(" >> setpagedevice");
/*      */     }
/*  856 */     this.mPSStream.println("/pgSave save def");
/*  857 */     this.mPSStream.println(d1 + " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt)
/*      */     throws PrinterException
/*      */   {
/*  868 */     this.mPSStream.println("pgSave restore");
/*  869 */     this.mPSStream.println("showpage");
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
/*      */   protected void drawImageBGR(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt1, int paramInt2)
/*      */   {
/*  893 */     setTransform(new AffineTransform());
/*  894 */     prepDrawing();
/*      */     
/*  896 */     int i = (int)paramFloat7;
/*  897 */     int j = (int)paramFloat8;
/*      */     
/*  899 */     this.mPSStream.println("/imSave save def");
/*      */     
/*      */ 
/*      */ 
/*  903 */     int k = 3 * i;
/*  904 */     while (k > 65535) {
/*  905 */       k /= 2;
/*      */     }
/*      */     
/*  908 */     this.mPSStream.println(k + " string /imStr exch def");
/*      */     
/*      */ 
/*      */ 
/*  912 */     this.mPSStream.println("[" + paramFloat3 + " 0 0 " + paramFloat4 + " " + paramFloat1 + " " + paramFloat2 + "]concat");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  919 */     this.mPSStream.println(i + " " + j + " " + 8 + "[" + i + " 0 0 " + j + " 0 " + 0 + "]/imageSrc load false 3 colorimage");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  927 */     int m = 0;
/*  928 */     byte[] arrayOfByte1 = new byte[i * 3];
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  934 */       m = (int)paramFloat6 * paramInt1;
/*      */       
/*  936 */       for (int n = 0; n < j; n++)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  941 */         m += (int)paramFloat5;
/*      */         
/*  943 */         m = swapBGRtoRGB(paramArrayOfByte, m, arrayOfByte1);
/*  944 */         byte[] arrayOfByte2 = rlEncode(arrayOfByte1);
/*  945 */         byte[] arrayOfByte3 = ascii85Encode(arrayOfByte2);
/*  946 */         this.mPSStream.write(arrayOfByte3);
/*  947 */         this.mPSStream.println("");
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  959 */     this.mPSStream.println("imSave restore");
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
/*      */   protected void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws PrinterException
/*      */   {
/*  975 */     this.mPSStream.println("/imSave save def");
/*      */     
/*      */ 
/*      */ 
/*  979 */     int i = 3 * paramInt3;
/*  980 */     while (i > 65535) {
/*  981 */       i /= 2;
/*      */     }
/*      */     
/*  984 */     this.mPSStream.println(i + " string /imStr exch def");
/*      */     
/*      */ 
/*      */ 
/*  988 */     this.mPSStream.println("[" + paramInt3 + " 0 0 " + paramInt4 + " " + paramInt1 + " " + paramInt2 + "]concat");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  995 */     this.mPSStream.println(paramInt3 + " " + paramInt4 + " " + 8 + "[" + paramInt3 + " 0 0 " + -paramInt4 + " 0 " + paramInt4 + "]/imageSrc load false 3 colorimage");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1003 */     int j = 0;
/* 1004 */     byte[] arrayOfByte1 = new byte[paramInt3 * 3];
/*      */     try
/*      */     {
/* 1007 */       for (int k = 0; k < paramInt4; k++) {
/* 1008 */         j = swapBGRtoRGB(paramArrayOfByte, j, arrayOfByte1);
/* 1009 */         byte[] arrayOfByte2 = rlEncode(arrayOfByte1);
/* 1010 */         byte[] arrayOfByte3 = ascii85Encode(arrayOfByte2);
/* 1011 */         this.mPSStream.write(arrayOfByte3);
/* 1012 */         this.mPSStream.println("");
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException) {
/* 1016 */       throw new PrinterIOException(localIOException);
/*      */     }
/*      */     
/* 1019 */     this.mPSStream.println("imSave restore");
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
/*      */   protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt)
/*      */   {
/* 1042 */     PeekMetrics localPeekMetrics = paramPeekGraphics.getMetrics();
/*      */     
/*      */ 
/*      */     PSPathGraphics localPSPathGraphics;
/*      */     
/*      */ 
/* 1048 */     if ((!forcePDL) && ((forceRaster == true) || 
/* 1049 */       (localPeekMetrics.hasNonSolidColors()) || 
/* 1050 */       (localPeekMetrics.hasCompositing())))
/*      */     {
/* 1052 */       localPSPathGraphics = null;
/*      */     }
/*      */     else {
/* 1055 */       BufferedImage localBufferedImage = new BufferedImage(8, 8, 1);
/*      */       
/* 1057 */       Graphics2D localGraphics2D = localBufferedImage.createGraphics();
/* 1058 */       boolean bool = !paramPeekGraphics.getAWTDrawingOnly();
/*      */       
/* 1060 */       localPSPathGraphics = new PSPathGraphics(localGraphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, bool);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1065 */     return localPSPathGraphics;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void selectClipPath()
/*      */   {
/* 1074 */     this.mPSStream.println(this.mClipOpStr);
/*      */   }
/*      */   
/*      */   protected void setClip(Shape paramShape)
/*      */   {
/* 1079 */     this.mLastClip = paramShape;
/*      */   }
/*      */   
/*      */   protected void setTransform(AffineTransform paramAffineTransform) {
/* 1083 */     this.mLastTransform = paramAffineTransform;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean setFont(Font paramFont)
/*      */   {
/* 1091 */     this.mLastFont = paramFont;
/* 1092 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int[] getPSFontIndexArray(Font paramFont, CharsetString[] paramArrayOfCharsetString)
/*      */   {
/* 1103 */     int[] arrayOfInt = null;
/*      */     
/* 1105 */     if (mFontProps != null) {
/* 1106 */       arrayOfInt = new int[paramArrayOfCharsetString.length];
/*      */     }
/*      */     
/* 1109 */     for (int i = 0; (i < paramArrayOfCharsetString.length) && (arrayOfInt != null); i++)
/*      */     {
/*      */ 
/*      */ 
/* 1113 */       CharsetString localCharsetString = paramArrayOfCharsetString[i];
/*      */       
/* 1115 */       CharsetEncoder localCharsetEncoder = localCharsetString.fontDescriptor.encoder;
/* 1116 */       String str1 = localCharsetString.fontDescriptor.getFontCharsetName();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1123 */       if ("Symbol".equals(str1)) {
/* 1124 */         str1 = "symbol";
/* 1125 */       } else if (("WingDings".equals(str1)) || 
/* 1126 */         ("X11Dingbats".equals(str1))) {
/* 1127 */         str1 = "dingbats";
/*      */       } else {
/* 1129 */         str1 = makeCharsetName(str1, localCharsetString.charsetChars);
/*      */       }
/*      */       
/*      */ 
/* 1133 */       int j = paramFont.getStyle() | FontUtilities.getFont2D(paramFont).getStyle();
/*      */       
/* 1135 */       String str2 = FontConfiguration.getStyleString(j);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1141 */       String str3 = paramFont.getFamily().toLowerCase(Locale.ENGLISH);
/* 1142 */       str3 = str3.replace(' ', '_');
/* 1143 */       String str4 = mFontProps.getProperty(str3, "");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1149 */       String str5 = mFontProps.getProperty(str4 + "." + str1 + "." + str2, null);
/*      */       
/*      */ 
/* 1152 */       if (str5 != null)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/* 1158 */           arrayOfInt[i] = Integer.parseInt(mFontProps.getProperty(str5));
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (NumberFormatException localNumberFormatException)
/*      */         {
/*      */ 
/*      */ 
/* 1166 */           arrayOfInt = null;
/*      */ 
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/* 1173 */         arrayOfInt = null;
/*      */       }
/*      */     }
/*      */     
/* 1177 */     return arrayOfInt;
/*      */   }
/*      */   
/*      */   private static String escapeParens(String paramString)
/*      */   {
/* 1182 */     if ((paramString.indexOf('(') == -1) && (paramString.indexOf(')') == -1)) {
/* 1183 */       return paramString;
/*      */     }
/* 1185 */     int i = 0;
/* 1186 */     int j = 0;
/* 1187 */     while ((j = paramString.indexOf('(', j)) != -1) {
/* 1188 */       i++;
/* 1189 */       j++;
/*      */     }
/* 1191 */     j = 0;
/* 1192 */     while ((j = paramString.indexOf(')', j)) != -1) {
/* 1193 */       i++;
/* 1194 */       j++;
/*      */     }
/* 1196 */     char[] arrayOfChar1 = paramString.toCharArray();
/* 1197 */     char[] arrayOfChar2 = new char[arrayOfChar1.length + i];
/* 1198 */     j = 0;
/* 1199 */     for (int k = 0; k < arrayOfChar1.length; k++) {
/* 1200 */       if ((arrayOfChar1[k] == '(') || (arrayOfChar1[k] == ')')) {
/* 1201 */         arrayOfChar2[(j++)] = '\\';
/*      */       }
/* 1203 */       arrayOfChar2[(j++)] = arrayOfChar1[k];
/*      */     }
/* 1205 */     return new String(arrayOfChar2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int platformFontCount(Font paramFont, String paramString)
/*      */   {
/* 1215 */     if (mFontProps == null) {
/* 1216 */       return 0;
/*      */     }
/*      */     
/* 1219 */     CharsetString[] arrayOfCharsetString = ((PlatformFont)paramFont.getPeer()).makeMultiCharsetString(paramString, false);
/* 1220 */     if (arrayOfCharsetString == null)
/*      */     {
/* 1222 */       return 0;
/*      */     }
/* 1224 */     int[] arrayOfInt = getPSFontIndexArray(paramFont, arrayOfCharsetString);
/* 1225 */     return arrayOfInt == null ? 0 : arrayOfInt.length;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean textOut(Graphics paramGraphics, String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3)
/*      */   {
/* 1231 */     boolean bool = true;
/*      */     
/* 1233 */     if (mFontProps == null) {
/* 1234 */       return false;
/*      */     }
/* 1236 */     prepDrawing();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1248 */     paramString = removeControlChars(paramString);
/* 1249 */     if (paramString.length() == 0) {
/* 1250 */       return true;
/*      */     }
/*      */     
/*      */ 
/* 1254 */     CharsetString[] arrayOfCharsetString = ((PlatformFont)paramFont.getPeer()).makeMultiCharsetString(paramString, false);
/* 1255 */     if (arrayOfCharsetString == null)
/*      */     {
/* 1257 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1265 */     int[] arrayOfInt = getPSFontIndexArray(paramFont, arrayOfCharsetString);
/* 1266 */     if (arrayOfInt != null)
/*      */     {
/* 1268 */       for (int i = 0; i < arrayOfCharsetString.length; i++) {
/* 1269 */         CharsetString localCharsetString = arrayOfCharsetString[i];
/* 1270 */         CharsetEncoder localCharsetEncoder = localCharsetString.fontDescriptor.encoder;
/*      */         
/* 1272 */         StringBuffer localStringBuffer = new StringBuffer();
/* 1273 */         byte[] arrayOfByte = new byte[localCharsetString.length * 2];
/* 1274 */         int j = 0;
/*      */         try {
/* 1276 */           ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
/* 1277 */           localCharsetEncoder.encode(CharBuffer.wrap(localCharsetString.charsetChars, localCharsetString.offset, localCharsetString.length), localByteBuffer, true);
/*      */           
/*      */ 
/*      */ 
/* 1281 */           localByteBuffer.flip();
/* 1282 */           j = localByteBuffer.limit();
/*      */         }
/*      */         catch (IllegalStateException localIllegalStateException)
/*      */         {
/*      */           continue;
/*      */         }
/*      */         catch (CoderMalfunctionError localCoderMalfunctionError)
/*      */         {
/*      */           continue;
/*      */         }
/*      */         
/*      */         float f;
/* 1294 */         if ((arrayOfCharsetString.length == 1) && (paramFloat3 != 0.0F)) {
/* 1295 */           f = paramFloat3;
/*      */         }
/*      */         else {
/* 1298 */           Rectangle2D localRectangle2D = paramFont.getStringBounds(localCharsetString.charsetChars, localCharsetString.offset, localCharsetString.offset + localCharsetString.length, paramFontRenderContext);
/*      */           
/*      */ 
/*      */ 
/* 1302 */           f = (float)localRectangle2D.getWidth();
/*      */         }
/*      */         
/*      */ 
/* 1306 */         if (f == 0.0F) {
/* 1307 */           return bool;
/*      */         }
/* 1309 */         localStringBuffer.append('<');
/* 1310 */         for (int k = 0; k < j; k++) {
/* 1311 */           int m = arrayOfByte[k];
/*      */           
/* 1313 */           String str = Integer.toHexString(m);
/* 1314 */           int n = str.length();
/* 1315 */           if (n > 2) {
/* 1316 */             str = str.substring(n - 2, n);
/* 1317 */           } else if (n == 1) {
/* 1318 */             str = "0" + str;
/* 1319 */           } else if (n == 0) {
/* 1320 */             str = "00";
/*      */           }
/* 1322 */           localStringBuffer.append(str);
/*      */         }
/* 1324 */         localStringBuffer.append('>');
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1329 */         getGState().emitPSFont(arrayOfInt[i], paramFont.getSize2D());
/*      */         
/*      */ 
/* 1332 */         this.mPSStream.println(localStringBuffer.toString() + " " + f + " " + paramFloat1 + " " + paramFloat2 + " " + "S");
/*      */         
/*      */ 
/* 1335 */         paramFloat1 += f;
/*      */       }
/*      */     } else {
/* 1338 */       bool = false;
/*      */     }
/*      */     
/*      */ 
/* 1342 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setFillMode(int paramInt)
/*      */   {
/* 1352 */     switch (paramInt)
/*      */     {
/*      */     case 1: 
/* 1355 */       this.mFillOpStr = "EF";
/* 1356 */       this.mClipOpStr = "EC";
/* 1357 */       break;
/*      */     
/*      */     case 2: 
/* 1360 */       this.mFillOpStr = "WF";
/* 1361 */       this.mClipOpStr = "WC";
/* 1362 */       break;
/*      */     
/*      */     default: 
/* 1365 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setColor(Color paramColor)
/*      */   {
/* 1375 */     this.mLastColor = paramColor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fillPath()
/*      */   {
/* 1384 */     this.mPSStream.println(this.mFillOpStr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void beginPath()
/*      */   {
/* 1392 */     prepDrawing();
/* 1393 */     this.mPSStream.println("N");
/*      */     
/* 1395 */     this.mPenX = 0.0F;
/* 1396 */     this.mPenY = 0.0F;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void closeSubpath()
/*      */   {
/* 1406 */     this.mPSStream.println("P");
/*      */     
/* 1408 */     this.mPenX = this.mStartPathX;
/* 1409 */     this.mPenY = this.mStartPathY;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void moveTo(float paramFloat1, float paramFloat2)
/*      */   {
/* 1419 */     this.mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " M");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1426 */     this.mStartPathX = paramFloat1;
/* 1427 */     this.mStartPathY = paramFloat2;
/*      */     
/* 1429 */     this.mPenX = paramFloat1;
/* 1430 */     this.mPenY = paramFloat2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void lineTo(float paramFloat1, float paramFloat2)
/*      */   {
/* 1438 */     this.mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " L");
/*      */     
/* 1440 */     this.mPenX = paramFloat1;
/* 1441 */     this.mPenY = paramFloat2;
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
/*      */   protected void bezierTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*      */   {
/* 1458 */     this.mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " " + 
/* 1459 */       trunc(paramFloat3) + " " + trunc(paramFloat4) + " " + 
/* 1460 */       trunc(paramFloat5) + " " + trunc(paramFloat6) + " C");
/*      */     
/*      */ 
/*      */ 
/* 1464 */     this.mPenX = paramFloat5;
/* 1465 */     this.mPenY = paramFloat6;
/*      */   }
/*      */   
/*      */   String trunc(float paramFloat) {
/* 1469 */     float f = Math.abs(paramFloat);
/* 1470 */     if ((f >= 1.0F) && (f <= 1000.0F)) {
/* 1471 */       paramFloat = Math.round(paramFloat * 1000.0F) / 1000.0F;
/*      */     }
/* 1473 */     return Float.toString(paramFloat);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected float getPenX()
/*      */   {
/* 1482 */     return this.mPenX;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected float getPenY()
/*      */   {
/* 1490 */     return this.mPenY;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected double getXRes()
/*      */   {
/* 1498 */     return 300.0D;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected double getYRes()
/*      */   {
/* 1505 */     return 300.0D;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected double getPhysicalPrintableX(Paper paramPaper)
/*      */   {
/* 1513 */     return 0.0D;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected double getPhysicalPrintableY(Paper paramPaper)
/*      */   {
/* 1522 */     return 0.0D;
/*      */   }
/*      */   
/*      */   protected double getPhysicalPrintableWidth(Paper paramPaper) {
/* 1526 */     return paramPaper.getImageableWidth();
/*      */   }
/*      */   
/*      */   protected double getPhysicalPrintableHeight(Paper paramPaper) {
/* 1530 */     return paramPaper.getImageableHeight();
/*      */   }
/*      */   
/*      */   protected double getPhysicalPageWidth(Paper paramPaper) {
/* 1534 */     return paramPaper.getWidth();
/*      */   }
/*      */   
/*      */   protected double getPhysicalPageHeight(Paper paramPaper) {
/* 1538 */     return paramPaper.getHeight();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getNoncollatedCopies()
/*      */   {
/* 1548 */     return 1;
/*      */   }
/*      */   
/*      */   protected int getCollatedCopies() {
/* 1552 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */   private String[] printExecCmd(String paramString1, String paramString2, boolean paramBoolean, String paramString3, int paramInt, String paramString4)
/*      */   {
/* 1558 */     int i = 1;
/* 1559 */     int j = 2;
/* 1560 */     int k = 4;
/* 1561 */     int m = 8;
/* 1562 */     int n = 16;
/* 1563 */     int i1 = 0;
/*      */     
/* 1565 */     int i2 = 2;
/* 1566 */     int i3 = 0;
/*      */     
/* 1568 */     if ((paramString1 != null) && (!paramString1.equals("")) && (!paramString1.equals("lp"))) {
/* 1569 */       i1 |= i;
/* 1570 */       i2++;
/*      */     }
/* 1572 */     if ((paramString2 != null) && (!paramString2.equals(""))) {
/* 1573 */       i1 |= j;
/* 1574 */       i2++;
/*      */     }
/* 1576 */     if ((paramString3 != null) && (!paramString3.equals(""))) {
/* 1577 */       i1 |= k;
/* 1578 */       i2++;
/*      */     }
/* 1580 */     if (paramInt > 1) {
/* 1581 */       i1 |= m;
/* 1582 */       i2++;
/*      */     }
/* 1584 */     if (paramBoolean) {
/* 1585 */       i1 |= n;
/* 1586 */       i2++;
/*      */     }
/*      */     
/* 1589 */     String str = System.getProperty("os.name");
/* 1590 */     String[] arrayOfString; if ((str.equals("Linux")) || (str.contains("OS X"))) {
/* 1591 */       arrayOfString = new String[i2];
/* 1592 */       arrayOfString[(i3++)] = "/usr/bin/lpr";
/* 1593 */       if ((i1 & i) != 0) {
/* 1594 */         arrayOfString[(i3++)] = ("-P" + paramString1);
/*      */       }
/* 1596 */       if ((i1 & k) != 0) {
/* 1597 */         arrayOfString[(i3++)] = ("-J" + paramString3);
/*      */       }
/* 1599 */       if ((i1 & m) != 0) {
/* 1600 */         arrayOfString[(i3++)] = ("-#" + paramInt);
/*      */       }
/* 1602 */       if ((i1 & n) != 0) {
/* 1603 */         arrayOfString[(i3++)] = "-h";
/*      */       }
/* 1605 */       if ((i1 & j) != 0) {
/* 1606 */         arrayOfString[(i3++)] = new String(paramString2);
/*      */       }
/*      */     } else {
/* 1609 */       i2++;
/* 1610 */       arrayOfString = new String[i2];
/* 1611 */       arrayOfString[(i3++)] = "/usr/bin/lp";
/* 1612 */       arrayOfString[(i3++)] = "-c";
/* 1613 */       if ((i1 & i) != 0) {
/* 1614 */         arrayOfString[(i3++)] = ("-d" + paramString1);
/*      */       }
/* 1616 */       if ((i1 & k) != 0) {
/* 1617 */         arrayOfString[(i3++)] = ("-t" + paramString3);
/*      */       }
/* 1619 */       if ((i1 & m) != 0) {
/* 1620 */         arrayOfString[(i3++)] = ("-n" + paramInt);
/*      */       }
/* 1622 */       if ((i1 & n) != 0) {
/* 1623 */         arrayOfString[(i3++)] = "-o nobanner";
/*      */       }
/* 1625 */       if ((i1 & j) != 0) {
/* 1626 */         arrayOfString[(i3++)] = ("-o" + paramString2);
/*      */       }
/*      */     }
/* 1629 */     arrayOfString[(i3++)] = paramString4;
/* 1630 */     return arrayOfString;
/*      */   }
/*      */   
/*      */   private static int swapBGRtoRGB(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2) {
/* 1634 */     int i = 0;
/* 1635 */     while ((paramInt < paramArrayOfByte1.length - 2) && (i < paramArrayOfByte2.length - 2)) {
/* 1636 */       paramArrayOfByte2[(i++)] = paramArrayOfByte1[(paramInt + 2)];
/* 1637 */       paramArrayOfByte2[(i++)] = paramArrayOfByte1[(paramInt + 1)];
/* 1638 */       paramArrayOfByte2[(i++)] = paramArrayOfByte1[(paramInt + 0)];
/* 1639 */       paramInt += 3;
/*      */     }
/* 1641 */     return paramInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String makeCharsetName(String paramString, char[] paramArrayOfChar)
/*      */   {
/* 1651 */     if ((paramString.equals("Cp1252")) || (paramString.equals("ISO8859_1")))
/* 1652 */       return "latin1";
/* 1653 */     int i; if (paramString.equals("UTF8"))
/*      */     {
/* 1655 */       for (i = 0; i < paramArrayOfChar.length; i++) {
/* 1656 */         if (paramArrayOfChar[i] > 'ÿ') {
/* 1657 */           return paramString.toLowerCase();
/*      */         }
/*      */       }
/* 1660 */       return "latin1"; }
/* 1661 */     if (paramString.startsWith("ISO8859"))
/*      */     {
/* 1663 */       for (i = 0; i < paramArrayOfChar.length; i++) {
/* 1664 */         if (paramArrayOfChar[i] > '') {
/* 1665 */           return paramString.toLowerCase();
/*      */         }
/*      */       }
/* 1668 */       return "latin1";
/*      */     }
/* 1670 */     return paramString.toLowerCase();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void prepDrawing()
/*      */   {
/* 1680 */     while ((!isOuterGState()) && (
/* 1681 */       (!getGState().canSetClip(this.mLastClip)) || 
/* 1682 */       (!getGState().mTransform.equals(this.mLastTransform))))
/*      */     {
/*      */ 
/* 1685 */       grestore();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1691 */     getGState().emitPSColor(this.mLastColor);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1697 */     if (isOuterGState()) {
/* 1698 */       gsave();
/* 1699 */       getGState().emitTransform(this.mLastTransform);
/* 1700 */       getGState().emitPSClip(this.mLastClip);
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
/*      */   private GState getGState()
/*      */   {
/* 1722 */     int i = this.mGStateStack.size();
/* 1723 */     return (GState)this.mGStateStack.get(i - 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void gsave()
/*      */   {
/* 1732 */     GState localGState = getGState();
/* 1733 */     this.mGStateStack.add(new GState(localGState));
/* 1734 */     this.mPSStream.println("G");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void grestore()
/*      */   {
/* 1743 */     int i = this.mGStateStack.size();
/* 1744 */     this.mGStateStack.remove(i - 1);
/* 1745 */     this.mPSStream.println("R");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean isOuterGState()
/*      */   {
/* 1754 */     return this.mGStateStack.size() == 1;
/*      */   }
/*      */   
/*      */ 
/*      */   private class GState
/*      */   {
/*      */     Color mColor;
/*      */     
/*      */     Shape mClip;
/*      */     
/*      */     Font mFont;
/*      */     AffineTransform mTransform;
/*      */     
/*      */     GState()
/*      */     {
/* 1769 */       this.mColor = Color.black;
/* 1770 */       this.mClip = null;
/* 1771 */       this.mFont = null;
/* 1772 */       this.mTransform = new AffineTransform();
/*      */     }
/*      */     
/*      */     GState(GState paramGState) {
/* 1776 */       this.mColor = paramGState.mColor;
/* 1777 */       this.mClip = paramGState.mClip;
/* 1778 */       this.mFont = paramGState.mFont;
/* 1779 */       this.mTransform = paramGState.mTransform;
/*      */     }
/*      */     
/*      */     boolean canSetClip(Shape paramShape)
/*      */     {
/* 1784 */       return (this.mClip == null) || (this.mClip.equals(paramShape));
/*      */     }
/*      */     
/*      */     void emitPSClip(Shape paramShape)
/*      */     {
/* 1789 */       if ((paramShape != null) && ((this.mClip == null) || 
/* 1790 */         (!this.mClip.equals(paramShape)))) {
/* 1791 */         String str1 = PSPrinterJob.this.mFillOpStr;
/* 1792 */         String str2 = PSPrinterJob.this.mClipOpStr;
/* 1793 */         PSPrinterJob.this.convertToPSPath(paramShape.getPathIterator(new AffineTransform()));
/* 1794 */         PSPrinterJob.this.selectClipPath();
/* 1795 */         this.mClip = paramShape;
/*      */         
/* 1797 */         PSPrinterJob.this.mClipOpStr = str1;
/* 1798 */         PSPrinterJob.this.mFillOpStr = str1;
/*      */       }
/*      */     }
/*      */     
/*      */     void emitTransform(AffineTransform paramAffineTransform)
/*      */     {
/* 1804 */       if ((paramAffineTransform != null) && (!paramAffineTransform.equals(this.mTransform))) {
/* 1805 */         double[] arrayOfDouble = new double[6];
/* 1806 */         paramAffineTransform.getMatrix(arrayOfDouble);
/* 1807 */         PSPrinterJob.this.mPSStream.println("[" + (float)arrayOfDouble[0] + " " + (float)arrayOfDouble[1] + " " + (float)arrayOfDouble[2] + " " + (float)arrayOfDouble[3] + " " + (float)arrayOfDouble[4] + " " + (float)arrayOfDouble[5] + "] concat");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1815 */         this.mTransform = paramAffineTransform;
/*      */       }
/*      */     }
/*      */     
/*      */     void emitPSColor(Color paramColor) {
/* 1820 */       if ((paramColor != null) && (!paramColor.equals(this.mColor))) {
/* 1821 */         float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1826 */         if ((arrayOfFloat[0] == arrayOfFloat[1]) && (arrayOfFloat[1] == arrayOfFloat[2])) {
/* 1827 */           PSPrinterJob.this.mPSStream.println(arrayOfFloat[0] + " SG");
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1832 */           PSPrinterJob.this.mPSStream.println(arrayOfFloat[0] + " " + arrayOfFloat[1] + " " + arrayOfFloat[2] + " " + " SC");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1838 */         this.mColor = paramColor;
/*      */       }
/*      */     }
/*      */     
/*      */     void emitPSFont(int paramInt, float paramFloat)
/*      */     {
/* 1844 */       PSPrinterJob.this.mPSStream.println(paramFloat + " " + paramInt + " " + "F");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void convertToPSPath(PathIterator paramPathIterator)
/*      */   {
/* 1855 */     float[] arrayOfFloat = new float[6];
/*      */     
/*      */ 
/*      */ 
/*      */     int j;
/*      */     
/*      */ 
/* 1862 */     if (paramPathIterator.getWindingRule() == 0) {
/* 1863 */       j = 1;
/*      */     } else {
/* 1865 */       j = 2;
/*      */     }
/*      */     
/* 1868 */     beginPath();
/*      */     
/* 1870 */     setFillMode(j);
/*      */     
/* 1872 */     while (!paramPathIterator.isDone()) {
/* 1873 */       int i = paramPathIterator.currentSegment(arrayOfFloat);
/*      */       
/* 1875 */       switch (i) {
/*      */       case 0: 
/* 1877 */         moveTo(arrayOfFloat[0], arrayOfFloat[1]);
/* 1878 */         break;
/*      */       
/*      */       case 1: 
/* 1881 */         lineTo(arrayOfFloat[0], arrayOfFloat[1]);
/* 1882 */         break;
/*      */       
/*      */ 
/*      */ 
/*      */       case 2: 
/* 1887 */         float f1 = getPenX();
/* 1888 */         float f2 = getPenY();
/* 1889 */         float f3 = f1 + (arrayOfFloat[0] - f1) * 2.0F / 3.0F;
/* 1890 */         float f4 = f2 + (arrayOfFloat[1] - f2) * 2.0F / 3.0F;
/* 1891 */         float f5 = arrayOfFloat[2] - (arrayOfFloat[2] - arrayOfFloat[0]) * 2.0F / 3.0F;
/* 1892 */         float f6 = arrayOfFloat[3] - (arrayOfFloat[3] - arrayOfFloat[1]) * 2.0F / 3.0F;
/* 1893 */         bezierTo(f3, f4, f5, f6, arrayOfFloat[2], arrayOfFloat[3]);
/*      */         
/*      */ 
/* 1896 */         break;
/*      */       
/*      */       case 3: 
/* 1899 */         bezierTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
/*      */         
/*      */ 
/* 1902 */         break;
/*      */       
/*      */       case 4: 
/* 1905 */         closeSubpath();
/*      */       }
/*      */       
/*      */       
/*      */ 
/* 1910 */       paramPathIterator.next();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void deviceFill(PathIterator paramPathIterator, Color paramColor, AffineTransform paramAffineTransform, Shape paramShape)
/*      */   {
/* 1922 */     setTransform(paramAffineTransform);
/* 1923 */     setClip(paramShape);
/* 1924 */     setColor(paramColor);
/* 1925 */     convertToPSPath(paramPathIterator);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1930 */     this.mPSStream.println("G");
/* 1931 */     selectClipPath();
/* 1932 */     fillPath();
/* 1933 */     this.mPSStream.println("R N");
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
/*      */   private byte[] rlEncode(byte[] paramArrayOfByte)
/*      */   {
/* 1955 */     int i = 0;
/* 1956 */     int j = 0;
/* 1957 */     int k = 0;
/* 1958 */     int m = 0;
/* 1959 */     byte[] arrayOfByte1 = new byte[paramArrayOfByte.length * 2 + 2];
/* 1960 */     while (i < paramArrayOfByte.length) {
/* 1961 */       if (m == 0) {
/* 1962 */         k = i++;
/* 1963 */         m = 1;
/*      */       }
/*      */       
/* 1966 */       while ((m < 128) && (i < paramArrayOfByte.length) && (paramArrayOfByte[i] == paramArrayOfByte[k]))
/*      */       {
/* 1968 */         m++;
/* 1969 */         i++;
/*      */       }
/*      */       
/* 1972 */       if (m > 1) {
/* 1973 */         arrayOfByte1[(j++)] = ((byte)(257 - m));
/* 1974 */         arrayOfByte1[(j++)] = paramArrayOfByte[k];
/* 1975 */         m = 0;
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1980 */         while ((m < 128) && (i < paramArrayOfByte.length) && (paramArrayOfByte[i] != paramArrayOfByte[(i - 1)]))
/*      */         {
/* 1982 */           m++;
/* 1983 */           i++;
/*      */         }
/* 1985 */         arrayOfByte1[(j++)] = ((byte)(m - 1));
/* 1986 */         for (int n = k; n < k + m; n++) {
/* 1987 */           arrayOfByte1[(j++)] = paramArrayOfByte[n];
/*      */         }
/* 1989 */         m = 0;
/*      */       } }
/* 1991 */     arrayOfByte1[(j++)] = Byte.MIN_VALUE;
/* 1992 */     byte[] arrayOfByte2 = new byte[j];
/* 1993 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, j);
/*      */     
/* 1995 */     return arrayOfByte2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private byte[] ascii85Encode(byte[] paramArrayOfByte)
/*      */   {
/* 2002 */     byte[] arrayOfByte1 = new byte[(paramArrayOfByte.length + 4) * 5 / 4 + 2];
/* 2003 */     long l1 = 85L;
/* 2004 */     long l2 = l1 * l1;
/* 2005 */     long l3 = l1 * l2;
/* 2006 */     long l4 = l1 * l3;
/* 2007 */     int i = 33;
/*      */     
/* 2009 */     int j = 0;
/* 2010 */     int k = 0;
/*      */     long l5;
/*      */     long l6;
/* 2013 */     while (j + 3 < paramArrayOfByte.length) {
/* 2014 */       l5 = ((paramArrayOfByte[(j++)] & 0xFF) << 24) + ((paramArrayOfByte[(j++)] & 0xFF) << 16) + ((paramArrayOfByte[(j++)] & 0xFF) << 8) + (paramArrayOfByte[(j++)] & 0xFF);
/*      */       
/*      */ 
/*      */ 
/* 2018 */       if (l5 == 0L) {
/* 2019 */         arrayOfByte1[(k++)] = 122;
/*      */       } else {
/* 2021 */         l6 = l5;
/* 2022 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 / l4 + i));l6 %= l4;
/* 2023 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 / l3 + i));l6 %= l3;
/* 2024 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 / l2 + i));l6 %= l2;
/* 2025 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 / l1 + i));l6 %= l1;
/* 2026 */         arrayOfByte1[(k++)] = ((byte)(int)(l6 + i));
/*      */       }
/*      */     }
/*      */     
/* 2030 */     if (j < paramArrayOfByte.length) {
/* 2031 */       int m = paramArrayOfByte.length - j;
/*      */       
/* 2033 */       l5 = 0L;
/* 2034 */       while (j < paramArrayOfByte.length) {
/* 2035 */         l5 = (l5 << 8) + (paramArrayOfByte[(j++)] & 0xFF);
/*      */       }
/*      */       
/* 2038 */       int n = 4 - m;
/* 2039 */       while (n-- > 0) {
/* 2040 */         l5 <<= 8;
/*      */       }
/* 2042 */       byte[] arrayOfByte3 = new byte[5];
/* 2043 */       l6 = l5;
/* 2044 */       arrayOfByte3[0] = ((byte)(int)(l6 / l4 + i));l6 %= l4;
/* 2045 */       arrayOfByte3[1] = ((byte)(int)(l6 / l3 + i));l6 %= l3;
/* 2046 */       arrayOfByte3[2] = ((byte)(int)(l6 / l2 + i));l6 %= l2;
/* 2047 */       arrayOfByte3[3] = ((byte)(int)(l6 / l1 + i));l6 %= l1;
/* 2048 */       arrayOfByte3[4] = ((byte)(int)(l6 + i));
/*      */       
/* 2050 */       for (int i1 = 0; i1 < m + 1; i1++) {
/* 2051 */         arrayOfByte1[(k++)] = arrayOfByte3[i1];
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2056 */     arrayOfByte1[(k++)] = 126;arrayOfByte1[(k++)] = 62;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2068 */     byte[] arrayOfByte2 = new byte[k];
/* 2069 */     System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, k);
/* 2070 */     return arrayOfByte2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class PluginPrinter
/*      */     implements Printable
/*      */   {
/*      */     private EPSPrinter epsPrinter;
/*      */     
/*      */ 
/*      */ 
/*      */     private Component applet;
/*      */     
/*      */ 
/*      */ 
/*      */     private PrintStream stream;
/*      */     
/*      */ 
/*      */ 
/*      */     private String epsTitle;
/*      */     
/*      */ 
/*      */ 
/*      */     private int bx;
/*      */     
/*      */ 
/*      */ 
/*      */     private int by;
/*      */     
/*      */ 
/*      */ 
/*      */     private int bw;
/*      */     
/*      */ 
/*      */ 
/*      */     private int bh;
/*      */     
/*      */ 
/*      */     private int width;
/*      */     
/*      */ 
/*      */     private int height;
/*      */     
/*      */ 
/*      */ 
/*      */     public PluginPrinter(Component paramComponent, PrintStream paramPrintStream, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 2119 */       this.applet = paramComponent;
/* 2120 */       this.epsTitle = "Java Plugin Applet";
/* 2121 */       this.stream = paramPrintStream;
/* 2122 */       this.bx = paramInt1;
/* 2123 */       this.by = paramInt2;
/* 2124 */       this.bw = paramInt3;
/* 2125 */       this.bh = paramInt4;
/* 2126 */       this.width = paramComponent.size().width;
/* 2127 */       this.height = paramComponent.size().height;
/* 2128 */       this.epsPrinter = new EPSPrinter(this, this.epsTitle, paramPrintStream, 0, 0, this.width, this.height);
/*      */     }
/*      */     
/*      */     public void printPluginPSHeader()
/*      */     {
/* 2133 */       this.stream.println("%%BeginDocument: JavaPluginApplet");
/*      */     }
/*      */     
/*      */     public void printPluginApplet() {
/*      */       try {
/* 2138 */         this.epsPrinter.print();
/*      */       }
/*      */       catch (PrinterException localPrinterException) {}
/*      */     }
/*      */     
/*      */     public void printPluginPSTrailer() {
/* 2144 */       this.stream.println("%%EndDocument: JavaPluginApplet");
/* 2145 */       this.stream.flush();
/*      */     }
/*      */     
/*      */     public void printAll() {
/* 2149 */       printPluginPSHeader();
/* 2150 */       printPluginApplet();
/* 2151 */       printPluginPSTrailer();
/*      */     }
/*      */     
/*      */     public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt) {
/* 2155 */       if (paramInt > 0) {
/* 2156 */         return 1;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2162 */       this.applet.printAll(paramGraphics);
/* 2163 */       return 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static class EPSPrinter
/*      */     implements Pageable
/*      */   {
/*      */     private PageFormat pf;
/*      */     
/*      */ 
/*      */     private PSPrinterJob job;
/*      */     
/*      */ 
/*      */     private int llx;
/*      */     
/*      */     private int lly;
/*      */     
/*      */     private int urx;
/*      */     
/*      */     private int ury;
/*      */     
/*      */     private Printable printable;
/*      */     
/*      */     private PrintStream stream;
/*      */     
/*      */     private String epsTitle;
/*      */     
/*      */ 
/*      */     public EPSPrinter(Printable paramPrintable, String paramString, PrintStream paramPrintStream, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     {
/* 2195 */       this.printable = paramPrintable;
/* 2196 */       this.epsTitle = paramString;
/* 2197 */       this.stream = paramPrintStream;
/* 2198 */       this.llx = paramInt1;
/* 2199 */       this.lly = paramInt2;
/* 2200 */       this.urx = (this.llx + paramInt3);
/* 2201 */       this.ury = (this.lly + paramInt4);
/*      */       
/*      */ 
/*      */ 
/* 2205 */       Paper localPaper = new Paper();
/* 2206 */       localPaper.setSize(paramInt3, paramInt4);
/* 2207 */       localPaper.setImageableArea(0.0D, 0.0D, paramInt3, paramInt4);
/* 2208 */       this.pf = new PageFormat();
/* 2209 */       this.pf.setPaper(localPaper);
/*      */     }
/*      */     
/*      */     public void print() throws PrinterException {
/* 2213 */       this.stream.println("%!PS-Adobe-3.0 EPSF-3.0");
/* 2214 */       this.stream.println("%%BoundingBox: " + this.llx + " " + this.lly + " " + this.urx + " " + this.ury);
/*      */       
/* 2216 */       this.stream.println("%%Title: " + this.epsTitle);
/* 2217 */       this.stream.println("%%Creator: Java Printing");
/* 2218 */       this.stream.println("%%CreationDate: " + new Date());
/* 2219 */       this.stream.println("%%EndComments");
/* 2220 */       this.stream.println("/pluginSave save def");
/* 2221 */       this.stream.println("mark");
/*      */       
/* 2223 */       this.job = new PSPrinterJob();
/* 2224 */       this.job.epsPrinter = this;
/* 2225 */       this.job.mPSStream = this.stream;
/* 2226 */       this.job.mDestType = 2;
/*      */       
/* 2228 */       this.job.startDoc();
/*      */       try {
/* 2230 */         this.job.printPage(this, 0);
/*      */       } catch (Throwable localThrowable) {
/* 2232 */         if ((localThrowable instanceof PrinterException)) {
/* 2233 */           throw ((PrinterException)localThrowable);
/*      */         }
/* 2235 */         throw new PrinterException(localThrowable.toString());
/*      */       }
/*      */       finally {
/* 2238 */         this.stream.println("cleartomark");
/* 2239 */         this.stream.println("pluginSave restore");
/* 2240 */         this.job.endDoc();
/*      */       }
/* 2242 */       this.stream.flush();
/*      */     }
/*      */     
/*      */     public int getNumberOfPages() {
/* 2246 */       return 1;
/*      */     }
/*      */     
/*      */     public PageFormat getPageFormat(int paramInt) {
/* 2250 */       if (paramInt > 0) {
/* 2251 */         throw new IndexOutOfBoundsException("pgIndex");
/*      */       }
/* 2253 */       return this.pf;
/*      */     }
/*      */     
/*      */     public Printable getPrintable(int paramInt)
/*      */     {
/* 2258 */       if (paramInt > 0) {
/* 2259 */         throw new IndexOutOfBoundsException("pgIndex");
/*      */       }
/* 2261 */       return this.printable;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\print\PSPrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
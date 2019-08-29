/*     */ package sun.print;
/*     */ 
/*     */ import java.awt.print.PageFormat;
/*     */ import java.awt.print.Pageable;
/*     */ import java.awt.print.Paper;
/*     */ import java.awt.print.Printable;
/*     */ import java.awt.print.PrinterException;
/*     */ import java.awt.print.PrinterJob;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.Vector;
/*     */ import javax.print.CancelablePrintJob;
/*     */ import javax.print.Doc;
/*     */ import javax.print.DocFlavor;
/*     */ import javax.print.DocFlavor.BYTE_ARRAY;
/*     */ import javax.print.DocFlavor.INPUT_STREAM;
/*     */ import javax.print.DocFlavor.URL;
/*     */ import javax.print.PrintException;
/*     */ import javax.print.PrintService;
/*     */ import javax.print.attribute.Attribute;
/*     */ import javax.print.attribute.AttributeSetUtilities;
/*     */ import javax.print.attribute.DocAttributeSet;
/*     */ import javax.print.attribute.HashPrintJobAttributeSet;
/*     */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*     */ import javax.print.attribute.PrintJobAttribute;
/*     */ import javax.print.attribute.PrintJobAttributeSet;
/*     */ import javax.print.attribute.PrintRequestAttribute;
/*     */ import javax.print.attribute.PrintRequestAttributeSet;
/*     */ import javax.print.attribute.standard.Copies;
/*     */ import javax.print.attribute.standard.Destination;
/*     */ import javax.print.attribute.standard.DocumentName;
/*     */ import javax.print.attribute.standard.Fidelity;
/*     */ import javax.print.attribute.standard.JobName;
/*     */ import javax.print.attribute.standard.JobOriginatingUserName;
/*     */ import javax.print.attribute.standard.Media;
/*     */ import javax.print.attribute.standard.MediaSize;
/*     */ import javax.print.attribute.standard.MediaSizeName;
/*     */ import javax.print.attribute.standard.OrientationRequested;
/*     */ import javax.print.attribute.standard.PrinterIsAcceptingJobs;
/*     */ import javax.print.attribute.standard.PrinterState;
/*     */ import javax.print.attribute.standard.PrinterStateReason;
/*     */ import javax.print.attribute.standard.PrinterStateReasons;
/*     */ import javax.print.attribute.standard.RequestingUserName;
/*     */ import javax.print.event.PrintJobAttributeListener;
/*     */ import javax.print.event.PrintJobEvent;
/*     */ import javax.print.event.PrintJobListener;
/*     */ import sun.awt.windows.WPrinterJob;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Win32PrintJob
/*     */   implements CancelablePrintJob
/*     */ {
/*     */   private transient Vector jobListeners;
/*     */   private transient Vector attrListeners;
/*     */   private transient Vector listenedAttributeSets;
/*     */   private Win32PrintService service;
/*     */   private boolean fidelity;
/*  88 */   private boolean printing = false;
/*  89 */   private boolean printReturned = false;
/*  90 */   private PrintRequestAttributeSet reqAttrSet = null;
/*  91 */   private PrintJobAttributeSet jobAttrSet = null;
/*     */   private PrinterJob job;
/*     */   private Doc doc;
/*  94 */   private String mDestination = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 100 */   private InputStream instream = null;
/* 101 */   private Reader reader = null;
/*     */   
/*     */ 
/* 104 */   private String jobName = "Java Printing";
/* 105 */   private int copies = 0;
/* 106 */   private MediaSizeName mediaName = null;
/* 107 */   private MediaSize mediaSize = null;
/* 108 */   private OrientationRequested orient = null;
/*     */   
/*     */   private long hPrintJob;
/*     */   
/*     */   private static final int PRINTBUFFERLEN = 8192;
/*     */   
/*     */ 
/*     */   Win32PrintJob(Win32PrintService paramWin32PrintService)
/*     */   {
/* 117 */     this.service = paramWin32PrintService;
/*     */   }
/*     */   
/*     */   public PrintService getPrintService() {
/* 121 */     return this.service;
/*     */   }
/*     */   
/*     */   public PrintJobAttributeSet getAttributes() {
/* 125 */     synchronized (this) {
/* 126 */       if (this.jobAttrSet == null)
/*     */       {
/* 128 */         HashPrintJobAttributeSet localHashPrintJobAttributeSet = new HashPrintJobAttributeSet();
/* 129 */         return AttributeSetUtilities.unmodifiableView(localHashPrintJobAttributeSet);
/*     */       }
/* 131 */       return this.jobAttrSet;
/*     */     }
/*     */   }
/*     */   
/*     */   public void addPrintJobListener(PrintJobListener paramPrintJobListener)
/*     */   {
/* 137 */     synchronized (this) {
/* 138 */       if (paramPrintJobListener == null) {
/* 139 */         return;
/*     */       }
/* 141 */       if (this.jobListeners == null) {
/* 142 */         this.jobListeners = new Vector();
/*     */       }
/* 144 */       this.jobListeners.add(paramPrintJobListener);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removePrintJobListener(PrintJobListener paramPrintJobListener) {
/* 149 */     synchronized (this) {
/* 150 */       if ((paramPrintJobListener == null) || (this.jobListeners == null)) {
/* 151 */         return;
/*     */       }
/* 153 */       this.jobListeners.remove(paramPrintJobListener);
/* 154 */       if (this.jobListeners.isEmpty()) {
/* 155 */         this.jobListeners = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private void closeDataStreams()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 537	sun/print/Win32PrintJob:doc	Ljavax/print/Doc;
/*     */     //   4: ifnonnull +4 -> 8
/*     */     //   7: return
/*     */     //   8: aconst_null
/*     */     //   9: astore_1
/*     */     //   10: aload_0
/*     */     //   11: getfield 537	sun/print/Win32PrintJob:doc	Ljavax/print/Doc;
/*     */     //   14: invokeinterface 636 1 0
/*     */     //   19: astore_1
/*     */     //   20: goto +5 -> 25
/*     */     //   23: astore_2
/*     */     //   24: return
/*     */     //   25: aload_0
/*     */     //   26: getfield 530	sun/print/Win32PrintJob:instream	Ljava/io/InputStream;
/*     */     //   29: ifnull +38 -> 67
/*     */     //   32: aload_0
/*     */     //   33: getfield 530	sun/print/Win32PrintJob:instream	Ljava/io/InputStream;
/*     */     //   36: invokevirtual 570	java/io/InputStream:close	()V
/*     */     //   39: aload_0
/*     */     //   40: aconst_null
/*     */     //   41: putfield 530	sun/print/Win32PrintJob:instream	Ljava/io/InputStream;
/*     */     //   44: goto +20 -> 64
/*     */     //   47: astore_2
/*     */     //   48: aload_0
/*     */     //   49: aconst_null
/*     */     //   50: putfield 530	sun/print/Win32PrintJob:instream	Ljava/io/InputStream;
/*     */     //   53: goto +11 -> 64
/*     */     //   56: astore_3
/*     */     //   57: aload_0
/*     */     //   58: aconst_null
/*     */     //   59: putfield 530	sun/print/Win32PrintJob:instream	Ljava/io/InputStream;
/*     */     //   62: aload_3
/*     */     //   63: athrow
/*     */     //   64: goto +86 -> 150
/*     */     //   67: aload_0
/*     */     //   68: getfield 531	sun/print/Win32PrintJob:reader	Ljava/io/Reader;
/*     */     //   71: ifnull +40 -> 111
/*     */     //   74: aload_0
/*     */     //   75: getfield 531	sun/print/Win32PrintJob:reader	Ljava/io/Reader;
/*     */     //   78: invokevirtual 572	java/io/Reader:close	()V
/*     */     //   81: aload_0
/*     */     //   82: aconst_null
/*     */     //   83: putfield 531	sun/print/Win32PrintJob:reader	Ljava/io/Reader;
/*     */     //   86: goto +22 -> 108
/*     */     //   89: astore_2
/*     */     //   90: aload_0
/*     */     //   91: aconst_null
/*     */     //   92: putfield 531	sun/print/Win32PrintJob:reader	Ljava/io/Reader;
/*     */     //   95: goto +13 -> 108
/*     */     //   98: astore 4
/*     */     //   100: aload_0
/*     */     //   101: aconst_null
/*     */     //   102: putfield 531	sun/print/Win32PrintJob:reader	Ljava/io/Reader;
/*     */     //   105: aload 4
/*     */     //   107: athrow
/*     */     //   108: goto +42 -> 150
/*     */     //   111: aload_1
/*     */     //   112: instanceof 283
/*     */     //   115: ifeq +17 -> 132
/*     */     //   118: aload_1
/*     */     //   119: checkcast 283	java/io/InputStream
/*     */     //   122: invokevirtual 570	java/io/InputStream:close	()V
/*     */     //   125: goto +25 -> 150
/*     */     //   128: astore_2
/*     */     //   129: goto +21 -> 150
/*     */     //   132: aload_1
/*     */     //   133: instanceof 284
/*     */     //   136: ifeq +14 -> 150
/*     */     //   139: aload_1
/*     */     //   140: checkcast 284	java/io/Reader
/*     */     //   143: invokevirtual 572	java/io/Reader:close	()V
/*     */     //   146: goto +4 -> 150
/*     */     //   149: astore_2
/*     */     //   150: return
/*     */     // Line number table:
/*     */     //   Java source line #169	-> byte code offset #0
/*     */     //   Java source line #170	-> byte code offset #7
/*     */     //   Java source line #173	-> byte code offset #8
/*     */     //   Java source line #176	-> byte code offset #10
/*     */     //   Java source line #179	-> byte code offset #20
/*     */     //   Java source line #177	-> byte code offset #23
/*     */     //   Java source line #178	-> byte code offset #24
/*     */     //   Java source line #181	-> byte code offset #25
/*     */     //   Java source line #183	-> byte code offset #32
/*     */     //   Java source line #186	-> byte code offset #39
/*     */     //   Java source line #187	-> byte code offset #44
/*     */     //   Java source line #184	-> byte code offset #47
/*     */     //   Java source line #186	-> byte code offset #48
/*     */     //   Java source line #187	-> byte code offset #53
/*     */     //   Java source line #186	-> byte code offset #56
/*     */     //   Java source line #187	-> byte code offset #62
/*     */     //   Java source line #189	-> byte code offset #67
/*     */     //   Java source line #191	-> byte code offset #74
/*     */     //   Java source line #194	-> byte code offset #81
/*     */     //   Java source line #195	-> byte code offset #86
/*     */     //   Java source line #192	-> byte code offset #89
/*     */     //   Java source line #194	-> byte code offset #90
/*     */     //   Java source line #195	-> byte code offset #95
/*     */     //   Java source line #194	-> byte code offset #98
/*     */     //   Java source line #195	-> byte code offset #105
/*     */     //   Java source line #197	-> byte code offset #111
/*     */     //   Java source line #199	-> byte code offset #118
/*     */     //   Java source line #201	-> byte code offset #125
/*     */     //   Java source line #200	-> byte code offset #128
/*     */     //   Java source line #201	-> byte code offset #129
/*     */     //   Java source line #203	-> byte code offset #132
/*     */     //   Java source line #205	-> byte code offset #139
/*     */     //   Java source line #207	-> byte code offset #146
/*     */     //   Java source line #206	-> byte code offset #149
/*     */     //   Java source line #209	-> byte code offset #150
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	151	0	this	Win32PrintJob
/*     */     //   9	131	1	localObject1	Object
/*     */     //   23	1	2	localIOException1	IOException
/*     */     //   47	1	2	localIOException2	IOException
/*     */     //   89	1	2	localIOException3	IOException
/*     */     //   128	1	2	localIOException4	IOException
/*     */     //   149	1	2	localIOException5	IOException
/*     */     //   56	7	3	localObject2	Object
/*     */     //   98	8	4	localObject3	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   10	20	23	java/io/IOException
/*     */     //   32	39	47	java/io/IOException
/*     */     //   32	39	56	finally
/*     */     //   74	81	89	java/io/IOException
/*     */     //   74	81	98	finally
/*     */     //   98	100	98	finally
/*     */     //   118	125	128	java/io/IOException
/*     */     //   139	146	149	java/io/IOException
/*     */   }
/*     */   
/*     */   private void notifyEvent(int paramInt)
/*     */   {
/* 216 */     switch (paramInt) {
/*     */     case 101: 
/*     */     case 102: 
/*     */     case 103: 
/*     */     case 105: 
/*     */     case 106: 
/* 222 */       closeDataStreams();
/*     */     }
/*     */     
/* 225 */     synchronized (this) {
/* 226 */       if (this.jobListeners != null)
/*     */       {
/* 228 */         PrintJobEvent localPrintJobEvent = new PrintJobEvent(this, paramInt);
/* 229 */         for (int i = 0; i < this.jobListeners.size(); i++) {
/* 230 */           PrintJobListener localPrintJobListener = (PrintJobListener)this.jobListeners.elementAt(i);
/* 231 */           switch (paramInt)
/*     */           {
/*     */           case 102: 
/* 234 */             localPrintJobListener.printJobCompleted(localPrintJobEvent);
/* 235 */             break;
/*     */           
/*     */           case 101: 
/* 238 */             localPrintJobListener.printJobCanceled(localPrintJobEvent);
/* 239 */             break;
/*     */           
/*     */           case 103: 
/* 242 */             localPrintJobListener.printJobFailed(localPrintJobEvent);
/* 243 */             break;
/*     */           
/*     */           case 106: 
/* 246 */             localPrintJobListener.printDataTransferCompleted(localPrintJobEvent);
/* 247 */             break;
/*     */           
/*     */           case 105: 
/* 250 */             localPrintJobListener.printJobNoMoreEvents(localPrintJobEvent);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener, PrintJobAttributeSet paramPrintJobAttributeSet)
/*     */   {
/* 264 */     synchronized (this) {
/* 265 */       if (paramPrintJobAttributeListener == null) {
/* 266 */         return;
/*     */       }
/* 268 */       if (this.attrListeners == null) {
/* 269 */         this.attrListeners = new Vector();
/* 270 */         this.listenedAttributeSets = new Vector();
/*     */       }
/* 272 */       this.attrListeners.add(paramPrintJobAttributeListener);
/* 273 */       if (paramPrintJobAttributeSet == null) {
/* 274 */         paramPrintJobAttributeSet = new HashPrintJobAttributeSet();
/*     */       }
/* 276 */       this.listenedAttributeSets.add(paramPrintJobAttributeSet);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removePrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener)
/*     */   {
/* 282 */     synchronized (this) {
/* 283 */       if ((paramPrintJobAttributeListener == null) || (this.attrListeners == null)) {
/* 284 */         return;
/*     */       }
/* 286 */       int i = this.attrListeners.indexOf(paramPrintJobAttributeListener);
/* 287 */       if (i == -1) {
/* 288 */         return;
/*     */       }
/* 290 */       this.attrListeners.remove(i);
/* 291 */       this.listenedAttributeSets.remove(i);
/* 292 */       if (this.attrListeners.isEmpty()) {
/* 293 */         this.attrListeners = null;
/* 294 */         this.listenedAttributeSets = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void print(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*     */     throws PrintException
/*     */   {
/* 303 */     synchronized (this) {
/* 304 */       if (this.printing) {
/* 305 */         throw new PrintException("already printing");
/*     */       }
/* 307 */       this.printing = true;
/*     */     }
/*     */     
/*     */ 
/* 311 */     ??? = (PrinterState)this.service.getAttribute(PrinterState.class);
/*     */     
/* 313 */     if (??? == PrinterState.STOPPED)
/*     */     {
/* 315 */       localObject2 = (PrinterStateReasons)this.service.getAttribute(PrinterStateReasons.class);
/*     */       
/* 317 */       if ((localObject2 != null) && 
/* 318 */         (((PrinterStateReasons)localObject2).containsKey(PrinterStateReason.SHUTDOWN)))
/*     */       {
/* 320 */         throw new PrintException("PrintService is no longer available.");
/*     */       }
/*     */     }
/*     */     
/* 324 */     if ((PrinterIsAcceptingJobs)this.service.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS)
/*     */     {
/*     */ 
/* 327 */       throw new PrintException("Printer is not accepting job.");
/*     */     }
/*     */     
/*     */ 
/* 331 */     this.doc = paramDoc;
/*     */     
/* 333 */     Object localObject2 = paramDoc.getDocFlavor();
/*     */     Object localObject3;
/*     */     try
/*     */     {
/* 337 */       localObject3 = paramDoc.getPrintData();
/*     */     } catch (IOException localIOException1) {
/* 339 */       notifyEvent(103);
/* 340 */       throw new PrintException("can't get print data: " + localIOException1.toString());
/*     */     }
/*     */     
/* 343 */     if (localObject3 == null) {
/* 344 */       throw new PrintException("Null print data.");
/*     */     }
/*     */     
/* 347 */     if ((localObject2 == null) || (!this.service.isDocFlavorSupported((DocFlavor)localObject2))) {
/* 348 */       notifyEvent(103);
/* 349 */       throw new PrintJobFlavorException("invalid flavor", (DocFlavor)localObject2);
/*     */     }
/*     */     
/* 352 */     initializeAttributeSets(paramDoc, paramPrintRequestAttributeSet);
/*     */     
/* 354 */     getAttributeValues((DocFlavor)localObject2);
/*     */     
/* 356 */     String str = ((DocFlavor)localObject2).getRepresentationClassName();
/*     */     
/* 358 */     if ((((DocFlavor)localObject2).equals(INPUT_STREAM.GIF)) ||
/* 359 */       (((DocFlavor)localObject2).equals(INPUT_STREAM.JPEG)) ||
/* 360 */       (((DocFlavor)localObject2).equals(INPUT_STREAM.PNG)) ||
/* 361 */       (((DocFlavor)localObject2).equals(BYTE_ARRAY.GIF)) ||
/* 362 */       (((DocFlavor)localObject2).equals(BYTE_ARRAY.JPEG)) ||
/* 363 */       (((DocFlavor)localObject2).equals(BYTE_ARRAY.PNG)))
/*     */       try {
/* 365 */         this.instream = paramDoc.getStreamForBytes();
/* 366 */         if (this.instream == null) {
/* 367 */           notifyEvent(103);
/* 368 */           throw new PrintException("No stream for data");
/*     */         }
/* 370 */         printableJob(new ImagePrinter(this.instream));
/* 371 */         this.service.wakeNotifier();
/* 372 */         return;
/*     */       } catch (ClassCastException localClassCastException1) {
/* 374 */         notifyEvent(103);
/* 375 */         throw new PrintException(localClassCastException1);
/*     */       } catch (IOException localIOException2) {
/* 377 */         notifyEvent(103);
/* 378 */         throw new PrintException(localIOException2);
/*     */       }
/* 380 */     if ((((DocFlavor)localObject2).equals(DocFlavor.URL.GIF)) || 
/* 381 */       (((DocFlavor)localObject2).equals(DocFlavor.URL.JPEG)) || 
/* 382 */       (((DocFlavor)localObject2).equals(DocFlavor.URL.PNG)))
/*     */       try {
/* 384 */         printableJob(new ImagePrinter((URL)localObject3));
/* 385 */         this.service.wakeNotifier();
/* 386 */         return;
/*     */       } catch (ClassCastException localClassCastException2) {
/* 388 */         notifyEvent(103);
/* 389 */         throw new PrintException(localClassCastException2);
/*     */       }
/* 391 */     if (str.equals("java.awt.print.Pageable"))
/*     */       try {
/* 393 */         pageableJob((Pageable)paramDoc.getPrintData());
/* 394 */         this.service.wakeNotifier();
/* 395 */         return;
/*     */       } catch (ClassCastException localClassCastException3) {
/* 397 */         notifyEvent(103);
/* 398 */         throw new PrintException(localClassCastException3);
/*     */       } catch (IOException localIOException3) {
/* 400 */         notifyEvent(103);
/* 401 */         throw new PrintException(localIOException3);
/*     */       }
/* 403 */     if (str.equals("java.awt.print.Printable"))
/*     */       try {
/* 405 */         printableJob((Printable)paramDoc.getPrintData());
/* 406 */         this.service.wakeNotifier();
/* 407 */         return;
/*     */       } catch (ClassCastException localClassCastException4) {
/* 409 */         notifyEvent(103);
/* 410 */         throw new PrintException(localClassCastException4);
/*     */       } catch (IOException localIOException4) {
/* 412 */         notifyEvent(103);
/* 413 */         throw new PrintException(localIOException4);
/*     */       }
/* 415 */     if ((str.equals("[B")) || 
/* 416 */       (str.equals("java.io.InputStream")) || 
/* 417 */       (str.equals("java.net.URL")))
/*     */     {
/* 419 */       if (str.equals("java.net.URL")) {
/* 420 */         URL localURL = (URL)localObject3;
/*     */         try {
/* 422 */           this.instream = localURL.openStream();
/*     */         } catch (IOException localIOException7) {
/* 424 */           notifyEvent(103);
/* 425 */           throw new PrintException(localIOException7.toString());
/*     */         }
/*     */       } else {
/*     */         try {
/* 429 */           this.instream = paramDoc.getStreamForBytes();
/*     */         } catch (IOException localIOException5) {
/* 431 */           notifyEvent(103);
/* 432 */           throw new PrintException(localIOException5.toString());
/*     */         }
/*     */       }
/*     */       
/* 436 */       if (this.instream == null) {
/* 437 */         notifyEvent(103);
/* 438 */         throw new PrintException("No stream for data");
/*     */       }
/*     */       
/* 441 */       if (this.mDestination != null) {
/*     */         try {
/* 443 */           FileOutputStream localFileOutputStream = new FileOutputStream(this.mDestination);
/* 444 */           byte[] arrayOfByte1 = new byte['Ѐ'];
/*     */           
/*     */           int j;
/* 447 */           while ((j = this.instream.read(arrayOfByte1, 0, arrayOfByte1.length)) >= 0) {
/* 448 */             localFileOutputStream.write(arrayOfByte1, 0, j);
/*     */           }
/* 450 */           localFileOutputStream.flush();
/* 451 */           localFileOutputStream.close();
/*     */         } catch (FileNotFoundException localFileNotFoundException) {
/* 453 */           notifyEvent(103);
/* 454 */           throw new PrintException(localFileNotFoundException.toString());
/*     */         } catch (IOException localIOException6) {
/* 456 */           notifyEvent(103);
/* 457 */           throw new PrintException(localIOException6.toString());
/*     */         }
/* 459 */         notifyEvent(106);
/* 460 */         notifyEvent(102);
/* 461 */         this.service.wakeNotifier();
/* 462 */         return;
/*     */       }
/*     */       
/* 465 */       if (!startPrintRawData(this.service.getName(), this.jobName)) {
/* 466 */         notifyEvent(103);
/* 467 */         throw new PrintException("Print job failed to start.");
/*     */       }
/* 469 */       BufferedInputStream localBufferedInputStream = new BufferedInputStream(this.instream);
/* 470 */       int i = 0;
/*     */       try {
/* 472 */         byte[] arrayOfByte2 = new byte[' '];
/*     */         
/* 474 */         while ((i = localBufferedInputStream.read(arrayOfByte2, 0, 8192)) >= 0) {
/* 475 */           if (!printRawData(arrayOfByte2, i)) {
/* 476 */             localBufferedInputStream.close();
/* 477 */             notifyEvent(103);
/* 478 */             throw new PrintException("Problem while spooling data");
/*     */           }
/*     */         }
/* 481 */         localBufferedInputStream.close();
/* 482 */         if (!endPrintRawData()) {
/* 483 */           notifyEvent(103);
/* 484 */           throw new PrintException("Print job failed to close properly.");
/*     */         }
/* 486 */         notifyEvent(106);
/*     */       } catch (IOException localIOException8) {
/* 488 */         notifyEvent(103);
/* 489 */         throw new PrintException(localIOException8.toString());
/*     */       } finally {
/* 491 */         notifyEvent(105);
/*     */       }
/*     */     } else {
/* 494 */       notifyEvent(103);
/* 495 */       throw new PrintException("unrecognized class: " + str);
/*     */     }
/* 497 */     this.service.wakeNotifier();
/*     */   }
/*     */   
/*     */   public void printableJob(Printable paramPrintable) throws PrintException {
/*     */     try {
/* 502 */       synchronized (this) {
/* 503 */         if (this.job != null) {
/* 504 */           throw new PrintException("already printing");
/*     */         }
/* 506 */         this.job = new WPrinterJob();
/*     */       }
/*     */       
/* 509 */       ??? = getPrintService();
/* 510 */       this.job.setPrintService((PrintService)???);
/* 511 */       if (this.copies == 0) {
/* 512 */         localObject2 = (Copies)((PrintService)???).getDefaultAttributeValue(Copies.class);
/* 513 */         this.copies = ((Copies)localObject2).getValue();
/*     */       }
/*     */       
/* 516 */       if (this.mediaName == null) {
/* 517 */         localObject2 = ((PrintService)???).getDefaultAttributeValue(Media.class);
/* 518 */         if ((localObject2 instanceof MediaSizeName)) {
/* 519 */           this.mediaName = ((MediaSizeName)localObject2);
/* 520 */           this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
/*     */         }
/*     */       }
/*     */       
/* 524 */       if (this.orient == null)
/*     */       {
/* 526 */         this.orient = ((OrientationRequested)((PrintService)???).getDefaultAttributeValue(OrientationRequested.class));
/*     */       }
/*     */       
/* 529 */       this.job.setCopies(this.copies);
/* 530 */       this.job.setJobName(this.jobName);
/* 531 */       Object localObject2 = new PageFormat();
/* 532 */       if (this.mediaSize != null) {
/* 533 */         Paper localPaper = new Paper();
/* 534 */         localPaper.setSize(this.mediaSize.getX(25400) * 72.0D, this.mediaSize
/* 535 */           .getY(25400) * 72.0D);
/* 536 */         localPaper.setImageableArea(72.0D, 72.0D, localPaper.getWidth() - 144.0D, localPaper
/* 537 */           .getHeight() - 144.0D);
/* 538 */         ((PageFormat)localObject2).setPaper(localPaper);
/*     */       }
/* 540 */       if (this.orient == OrientationRequested.REVERSE_LANDSCAPE) {
/* 541 */         ((PageFormat)localObject2).setOrientation(2);
/* 542 */       } else if (this.orient == OrientationRequested.LANDSCAPE) {
/* 543 */         ((PageFormat)localObject2).setOrientation(0);
/*     */       }
/* 545 */       this.job.setPrintable(paramPrintable, (PageFormat)localObject2);
/* 546 */       this.job.print(this.reqAttrSet);
/* 547 */       notifyEvent(106);
/* 548 */       return;
/*     */     } catch (PrinterException localPrinterException) {
/* 550 */       notifyEvent(103);
/* 551 */       throw new PrintException(localPrinterException);
/*     */     } finally {
/* 553 */       this.printReturned = true;
/* 554 */       notifyEvent(105);
/*     */     }
/*     */   }
/*     */   
/*     */   public void pageableJob(Pageable paramPageable) throws PrintException {
/*     */     try {
/* 560 */       synchronized (this) {
/* 561 */         if (this.job != null) {
/* 562 */           throw new PrintException("already printing");
/*     */         }
/* 564 */         this.job = new WPrinterJob();
/*     */       }
/*     */       
/* 567 */       ??? = getPrintService();
/* 568 */       this.job.setPrintService((PrintService)???);
/* 569 */       if (this.copies == 0) {
/* 570 */         Copies localCopies = (Copies)((PrintService)???).getDefaultAttributeValue(Copies.class);
/* 571 */         this.copies = localCopies.getValue();
/*     */       }
/* 573 */       this.job.setCopies(this.copies);
/* 574 */       this.job.setJobName(this.jobName);
/* 575 */       this.job.setPageable(paramPageable);
/* 576 */       this.job.print(this.reqAttrSet);
/* 577 */       notifyEvent(106);
/* 578 */       return;
/*     */     } catch (PrinterException localPrinterException) {
/* 580 */       notifyEvent(103);
/* 581 */       throw new PrintException(localPrinterException);
/*     */     } finally {
/* 583 */       this.printReturned = true;
/* 584 */       notifyEvent(105);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void initializeAttributeSets(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*     */   {
/* 594 */     this.reqAttrSet = new HashPrintRequestAttributeSet();
/* 595 */     this.jobAttrSet = new HashPrintJobAttributeSet();
/*     */     
/*     */     Attribute[] arrayOfAttribute;
/* 598 */     if (paramPrintRequestAttributeSet != null) {
/* 599 */       this.reqAttrSet.addAll(paramPrintRequestAttributeSet);
/* 600 */       arrayOfAttribute = paramPrintRequestAttributeSet.toArray();
/* 601 */       for (int i = 0; i < arrayOfAttribute.length; i++) {
/* 602 */         if ((arrayOfAttribute[i] instanceof PrintJobAttribute)) {
/* 603 */           this.jobAttrSet.add(arrayOfAttribute[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 608 */     DocAttributeSet localDocAttributeSet = paramDoc.getAttributes();
/* 609 */     if (localDocAttributeSet != null) {
/* 610 */       arrayOfAttribute = localDocAttributeSet.toArray();
/* 611 */       for (int j = 0; j < arrayOfAttribute.length; j++) {
/* 612 */         if ((arrayOfAttribute[j] instanceof PrintRequestAttribute)) {
/* 613 */           this.reqAttrSet.add(arrayOfAttribute[j]);
/*     */         }
/* 615 */         if ((arrayOfAttribute[j] instanceof PrintJobAttribute)) {
/* 616 */           this.jobAttrSet.add(arrayOfAttribute[j]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 622 */     String str = "";
/*     */     try {
/* 624 */       str = System.getProperty("user.name");
/*     */     }
/*     */     catch (SecurityException localSecurityException) {}
/*     */     Object localObject1;
/* 628 */     if ((str == null) || (str.equals("")))
/*     */     {
/* 630 */       localObject1 = (RequestingUserName)paramPrintRequestAttributeSet.get(RequestingUserName.class);
/* 631 */       if (localObject1 != null) {
/* 632 */         this.jobAttrSet.add(new JobOriginatingUserName(((RequestingUserName)localObject1)
/* 633 */           .getValue(), ((RequestingUserName)localObject1)
/* 634 */           .getLocale()));
/*     */       } else {
/* 636 */         this.jobAttrSet.add(new JobOriginatingUserName("", null));
/*     */       }
/*     */     } else {
/* 639 */       this.jobAttrSet.add(new JobOriginatingUserName(str, null));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 644 */     if (this.jobAttrSet.get(JobName.class) == null) {
/*     */       Object localObject2;
/* 646 */       if ((localDocAttributeSet != null) && (localDocAttributeSet.get(DocumentName.class) != null))
/*     */       {
/* 648 */         localObject2 = (DocumentName)localDocAttributeSet.get(DocumentName.class);
/* 649 */         localObject1 = new JobName(((DocumentName)localObject2).getValue(), ((DocumentName)localObject2).getLocale());
/* 650 */         this.jobAttrSet.add((Attribute)localObject1);
/*     */       } else {
/* 652 */         localObject2 = "JPS Job:" + paramDoc;
/*     */         try {
/* 654 */           Object localObject3 = paramDoc.getPrintData();
/* 655 */           if ((localObject3 instanceof URL)) {
/* 656 */             localObject2 = ((URL)paramDoc.getPrintData()).toString();
/*     */           }
/*     */         }
/*     */         catch (IOException localIOException) {}
/* 660 */         localObject1 = new JobName((String)localObject2, null);
/* 661 */         this.jobAttrSet.add((Attribute)localObject1);
/*     */       }
/*     */     }
/*     */     
/* 665 */     this.jobAttrSet = AttributeSetUtilities.unmodifiableView(this.jobAttrSet);
/*     */   }
/*     */   
/*     */   private void getAttributeValues(DocFlavor paramDocFlavor) throws PrintException
/*     */   {
/* 670 */     if (this.reqAttrSet.get(Fidelity.class) == Fidelity.FIDELITY_TRUE) {
/* 671 */       this.fidelity = true;
/*     */     } else {
/* 673 */       this.fidelity = false;
/*     */     }
/*     */     
/*     */ 
/* 677 */     Attribute[] arrayOfAttribute = this.reqAttrSet.toArray();
/* 678 */     for (int i = 0; i < arrayOfAttribute.length; i++) {
/* 679 */       Attribute localAttribute = arrayOfAttribute[i];
/* 680 */       Class localClass = localAttribute.getCategory();
/* 681 */       if (this.fidelity == true) {
/* 682 */         if (!this.service.isAttributeCategorySupported(localClass)) {
/* 683 */           notifyEvent(103);
/* 684 */           throw new PrintJobAttributeException("unsupported category: " + localClass, localClass, null);
/*     */         }
/*     */         
/* 687 */         if (!this.service.isAttributeValueSupported(localAttribute, paramDocFlavor, null)) {
/* 688 */           notifyEvent(103);
/* 689 */           throw new PrintJobAttributeException("unsupported attribute: " + localAttribute, null, localAttribute);
/*     */         }
/*     */       }
/*     */       
/* 693 */       if (localClass == Destination.class) {
/* 694 */         URI localURI = ((Destination)localAttribute).getURI();
/* 695 */         if (!"file".equals(localURI.getScheme())) {
/* 696 */           notifyEvent(103);
/* 697 */           throw new PrintException("Not a file: URI");
/*     */         }
/*     */         try {
/* 700 */           this.mDestination = new File(localURI).getPath();
/*     */         } catch (Exception localException) {
/* 702 */           throw new PrintException(localException);
/*     */         }
/*     */         
/* 705 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 706 */         if (localSecurityManager != null) {
/*     */           try {
/* 708 */             localSecurityManager.checkWrite(this.mDestination);
/*     */           } catch (SecurityException localSecurityException) {
/* 710 */             notifyEvent(103);
/* 711 */             throw new PrintException(localSecurityException);
/*     */           }
/*     */         }
/*     */       }
/* 715 */       else if (localClass == JobName.class) {
/* 716 */         this.jobName = ((JobName)localAttribute).getValue();
/* 717 */       } else if (localClass == Copies.class) {
/* 718 */         this.copies = ((Copies)localAttribute).getValue();
/* 719 */       } else if (localClass == Media.class) {
/* 720 */         if ((localAttribute instanceof MediaSizeName)) {
/* 721 */           this.mediaName = ((MediaSizeName)localAttribute);
/*     */           
/*     */ 
/*     */ 
/* 725 */           if (!this.service.isAttributeValueSupported(localAttribute, null, null)) {
/* 726 */             this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
/*     */           }
/*     */         }
/* 729 */       } else if (localClass == OrientationRequested.class) {
/* 730 */         this.orient = ((OrientationRequested)localAttribute);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private native boolean startPrintRawData(String paramString1, String paramString2);
/*     */   
/*     */   private native boolean printRawData(byte[] paramArrayOfByte, int paramInt);
/*     */   
/*     */   private native boolean endPrintRawData();
/*     */   
/*     */   public void cancel() throws PrintException {
/* 742 */     synchronized (this) {
/* 743 */       if (!this.printing)
/* 744 */         throw new PrintException("Job is not yet submitted.");
/* 745 */       if ((this.job != null) && (!this.printReturned)) {
/* 746 */         this.job.cancel();
/* 747 */         notifyEvent(101);
/* 748 */         return;
/*     */       }
/* 750 */       throw new PrintException("Job could not be cancelled.");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\print\Win32PrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
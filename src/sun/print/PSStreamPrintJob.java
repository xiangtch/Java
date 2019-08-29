/*     */ package sun.print;
/*     */ 
/*     */ import java.awt.print.PageFormat;
/*     */ import java.awt.print.Pageable;
/*     */ import java.awt.print.Paper;
/*     */ import java.awt.print.Printable;
/*     */ import java.awt.print.PrinterException;
/*     */ import java.awt.print.PrinterJob;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
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
/*     */ import javax.print.attribute.standard.DocumentName;
/*     */ import javax.print.attribute.standard.Fidelity;
/*     */ import javax.print.attribute.standard.JobName;
/*     */ import javax.print.attribute.standard.JobOriginatingUserName;
/*     */ import javax.print.attribute.standard.Media;
/*     */ import javax.print.attribute.standard.MediaSize;
/*     */ import javax.print.attribute.standard.MediaSize.NA;
/*     */ import javax.print.attribute.standard.MediaSizeName;
/*     */ import javax.print.attribute.standard.OrientationRequested;
/*     */ import javax.print.attribute.standard.RequestingUserName;
/*     */ import javax.print.event.PrintJobAttributeListener;
/*     */ import javax.print.event.PrintJobEvent;
/*     */ import javax.print.event.PrintJobListener;
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
/*     */ public class PSStreamPrintJob
/*     */   implements CancelablePrintJob
/*     */ {
/*     */   private transient Vector jobListeners;
/*     */   private transient Vector attrListeners;
/*     */   private transient Vector listenedAttributeSets;
/*     */   private PSStreamPrintService service;
/*     */   private boolean fidelity;
/*  75 */   private boolean printing = false;
/*  76 */   private boolean printReturned = false;
/*  77 */   private PrintRequestAttributeSet reqAttrSet = null;
/*  78 */   private PrintJobAttributeSet jobAttrSet = null;
/*     */   
/*     */ 
/*     */   private PrinterJob job;
/*     */   
/*     */   private Doc doc;
/*     */   
/*  85 */   private InputStream instream = null;
/*  86 */   private Reader reader = null;
/*     */   
/*     */ 
/*  89 */   private String jobName = "Java Printing";
/*  90 */   private int copies = 1;
/*  91 */   private MediaSize mediaSize = NA.LETTER;
/*  92 */   private OrientationRequested orient = OrientationRequested.PORTRAIT;
/*     */   
/*     */   PSStreamPrintJob(PSStreamPrintService paramPSStreamPrintService) {
/*  95 */     this.service = paramPSStreamPrintService;
/*     */   }
/*     */   
/*     */   public PrintService getPrintService() {
/*  99 */     return this.service;
/*     */   }
/*     */   
/*     */   public PrintJobAttributeSet getAttributes() {
/* 103 */     synchronized (this) {
/* 104 */       if (this.jobAttrSet == null)
/*     */       {
/* 106 */         HashPrintJobAttributeSet localHashPrintJobAttributeSet = new HashPrintJobAttributeSet();
/* 107 */         return AttributeSetUtilities.unmodifiableView(localHashPrintJobAttributeSet);
/*     */       }
/* 109 */       return this.jobAttrSet;
/*     */     }
/*     */   }
/*     */   
/*     */   public void addPrintJobListener(PrintJobListener paramPrintJobListener)
/*     */   {
/* 115 */     synchronized (this) {
/* 116 */       if (paramPrintJobListener == null) {
/* 117 */         return;
/*     */       }
/* 119 */       if (this.jobListeners == null) {
/* 120 */         this.jobListeners = new Vector();
/*     */       }
/* 122 */       this.jobListeners.add(paramPrintJobListener);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removePrintJobListener(PrintJobListener paramPrintJobListener) {
/* 127 */     synchronized (this) {
/* 128 */       if ((paramPrintJobListener == null) || (this.jobListeners == null)) {
/* 129 */         return;
/*     */       }
/* 131 */       this.jobListeners.remove(paramPrintJobListener);
/* 132 */       if (this.jobListeners.isEmpty()) {
/* 133 */         this.jobListeners = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private void closeDataStreams()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 424	sun/print/PSStreamPrintJob:doc	Ljavax/print/Doc;
/*     */     //   4: ifnonnull +4 -> 8
/*     */     //   7: return
/*     */     //   8: aconst_null
/*     */     //   9: astore_1
/*     */     //   10: aload_0
/*     */     //   11: getfield 424	sun/print/PSStreamPrintJob:doc	Ljavax/print/Doc;
/*     */     //   14: invokeinterface 496 1 0
/*     */     //   19: astore_1
/*     */     //   20: goto +5 -> 25
/*     */     //   23: astore_2
/*     */     //   24: return
/*     */     //   25: aload_0
/*     */     //   26: getfield 418	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
/*     */     //   29: ifnull +38 -> 67
/*     */     //   32: aload_0
/*     */     //   33: getfield 418	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
/*     */     //   36: invokevirtual 444	java/io/InputStream:close	()V
/*     */     //   39: aload_0
/*     */     //   40: aconst_null
/*     */     //   41: putfield 418	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
/*     */     //   44: goto +20 -> 64
/*     */     //   47: astore_2
/*     */     //   48: aload_0
/*     */     //   49: aconst_null
/*     */     //   50: putfield 418	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
/*     */     //   53: goto +11 -> 64
/*     */     //   56: astore_3
/*     */     //   57: aload_0
/*     */     //   58: aconst_null
/*     */     //   59: putfield 418	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
/*     */     //   62: aload_3
/*     */     //   63: athrow
/*     */     //   64: goto +86 -> 150
/*     */     //   67: aload_0
/*     */     //   68: getfield 419	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
/*     */     //   71: ifnull +40 -> 111
/*     */     //   74: aload_0
/*     */     //   75: getfield 419	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
/*     */     //   78: invokevirtual 445	java/io/Reader:close	()V
/*     */     //   81: aload_0
/*     */     //   82: aconst_null
/*     */     //   83: putfield 419	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
/*     */     //   86: goto +22 -> 108
/*     */     //   89: astore_2
/*     */     //   90: aload_0
/*     */     //   91: aconst_null
/*     */     //   92: putfield 419	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
/*     */     //   95: goto +13 -> 108
/*     */     //   98: astore 4
/*     */     //   100: aload_0
/*     */     //   101: aconst_null
/*     */     //   102: putfield 419	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
/*     */     //   105: aload 4
/*     */     //   107: athrow
/*     */     //   108: goto +42 -> 150
/*     */     //   111: aload_1
/*     */     //   112: instanceof 209
/*     */     //   115: ifeq +17 -> 132
/*     */     //   118: aload_1
/*     */     //   119: checkcast 209	java/io/InputStream
/*     */     //   122: invokevirtual 444	java/io/InputStream:close	()V
/*     */     //   125: goto +25 -> 150
/*     */     //   128: astore_2
/*     */     //   129: goto +21 -> 150
/*     */     //   132: aload_1
/*     */     //   133: instanceof 210
/*     */     //   136: ifeq +14 -> 150
/*     */     //   139: aload_1
/*     */     //   140: checkcast 210	java/io/Reader
/*     */     //   143: invokevirtual 445	java/io/Reader:close	()V
/*     */     //   146: goto +4 -> 150
/*     */     //   149: astore_2
/*     */     //   150: return
/*     */     // Line number table:
/*     */     //   Java source line #146	-> byte code offset #0
/*     */     //   Java source line #147	-> byte code offset #7
/*     */     //   Java source line #150	-> byte code offset #8
/*     */     //   Java source line #153	-> byte code offset #10
/*     */     //   Java source line #156	-> byte code offset #20
/*     */     //   Java source line #154	-> byte code offset #23
/*     */     //   Java source line #155	-> byte code offset #24
/*     */     //   Java source line #158	-> byte code offset #25
/*     */     //   Java source line #160	-> byte code offset #32
/*     */     //   Java source line #163	-> byte code offset #39
/*     */     //   Java source line #164	-> byte code offset #44
/*     */     //   Java source line #161	-> byte code offset #47
/*     */     //   Java source line #163	-> byte code offset #48
/*     */     //   Java source line #164	-> byte code offset #53
/*     */     //   Java source line #163	-> byte code offset #56
/*     */     //   Java source line #164	-> byte code offset #62
/*     */     //   Java source line #166	-> byte code offset #67
/*     */     //   Java source line #168	-> byte code offset #74
/*     */     //   Java source line #171	-> byte code offset #81
/*     */     //   Java source line #172	-> byte code offset #86
/*     */     //   Java source line #169	-> byte code offset #89
/*     */     //   Java source line #171	-> byte code offset #90
/*     */     //   Java source line #172	-> byte code offset #95
/*     */     //   Java source line #171	-> byte code offset #98
/*     */     //   Java source line #172	-> byte code offset #105
/*     */     //   Java source line #174	-> byte code offset #111
/*     */     //   Java source line #176	-> byte code offset #118
/*     */     //   Java source line #178	-> byte code offset #125
/*     */     //   Java source line #177	-> byte code offset #128
/*     */     //   Java source line #178	-> byte code offset #129
/*     */     //   Java source line #180	-> byte code offset #132
/*     */     //   Java source line #182	-> byte code offset #139
/*     */     //   Java source line #184	-> byte code offset #146
/*     */     //   Java source line #183	-> byte code offset #149
/*     */     //   Java source line #186	-> byte code offset #150
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	151	0	this	PSStreamPrintJob
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
/* 189 */     synchronized (this) {
/* 190 */       if (this.jobListeners != null)
/*     */       {
/* 192 */         PrintJobEvent localPrintJobEvent = new PrintJobEvent(this, paramInt);
/* 193 */         for (int i = 0; i < this.jobListeners.size(); i++) {
/* 194 */           PrintJobListener localPrintJobListener = (PrintJobListener)this.jobListeners.elementAt(i);
/* 195 */           switch (paramInt)
/*     */           {
/*     */           case 101: 
/* 198 */             localPrintJobListener.printJobCanceled(localPrintJobEvent);
/* 199 */             break;
/*     */           
/*     */           case 103: 
/* 202 */             localPrintJobListener.printJobFailed(localPrintJobEvent);
/* 203 */             break;
/*     */           
/*     */           case 106: 
/* 206 */             localPrintJobListener.printDataTransferCompleted(localPrintJobEvent);
/* 207 */             break;
/*     */           
/*     */           case 105: 
/* 210 */             localPrintJobListener.printJobNoMoreEvents(localPrintJobEvent);
/* 211 */             break;
/*     */           
/*     */           case 102: 
/* 214 */             localPrintJobListener.printJobCompleted(localPrintJobEvent);
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
/* 228 */     synchronized (this) {
/* 229 */       if (paramPrintJobAttributeListener == null) {
/* 230 */         return;
/*     */       }
/* 232 */       if (this.attrListeners == null) {
/* 233 */         this.attrListeners = new Vector();
/* 234 */         this.listenedAttributeSets = new Vector();
/*     */       }
/* 236 */       this.attrListeners.add(paramPrintJobAttributeListener);
/* 237 */       if (paramPrintJobAttributeSet == null) {
/* 238 */         paramPrintJobAttributeSet = new HashPrintJobAttributeSet();
/*     */       }
/* 240 */       this.listenedAttributeSets.add(paramPrintJobAttributeSet);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removePrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener)
/*     */   {
/* 246 */     synchronized (this) {
/* 247 */       if ((paramPrintJobAttributeListener == null) || (this.attrListeners == null)) {
/* 248 */         return;
/*     */       }
/* 250 */       int i = this.attrListeners.indexOf(paramPrintJobAttributeListener);
/* 251 */       if (i == -1) {
/* 252 */         return;
/*     */       }
/* 254 */       this.attrListeners.remove(i);
/* 255 */       this.listenedAttributeSets.remove(i);
/* 256 */       if (this.attrListeners.isEmpty()) {
/* 257 */         this.attrListeners = null;
/* 258 */         this.listenedAttributeSets = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void print(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*     */     throws PrintException
/*     */   {
/* 267 */     synchronized (this) {
/* 268 */       if (this.printing) {
/* 269 */         throw new PrintException("already printing");
/*     */       }
/* 271 */       this.printing = true;
/*     */     }
/*     */     
/*     */ 
/* 275 */     this.doc = paramDoc;
/*     */     
/* 277 */     ??? = paramDoc.getDocFlavor();
/*     */     Object localObject2;
/*     */     try
/*     */     {
/* 281 */       localObject2 = paramDoc.getPrintData();
/*     */     } catch (IOException localIOException1) {
/* 283 */       notifyEvent(103);
/* 284 */       throw new PrintException("can't get print data: " + localIOException1.toString());
/*     */     }
/*     */     
/* 287 */     if ((??? == null) || (!this.service.isDocFlavorSupported((DocFlavor)???))) {
/* 288 */       notifyEvent(103);
/* 289 */       throw new PrintJobFlavorException("invalid flavor", (DocFlavor)???);
/*     */     }
/*     */     
/* 292 */     initializeAttributeSets(paramDoc, paramPrintRequestAttributeSet);
/*     */     
/* 294 */     getAttributeValues((DocFlavor)???);
/*     */     
/* 296 */     String str = ((DocFlavor)???).getRepresentationClassName();
/* 297 */     if ((((DocFlavor)???).equals(INPUT_STREAM.GIF)) ||
/* 298 */       (((DocFlavor)???).equals(INPUT_STREAM.JPEG)) ||
/* 299 */       (((DocFlavor)???).equals(INPUT_STREAM.PNG)) ||
/* 300 */       (((DocFlavor)???).equals(BYTE_ARRAY.GIF)) ||
/* 301 */       (((DocFlavor)???).equals(BYTE_ARRAY.JPEG)) ||
/* 302 */       (((DocFlavor)???).equals(BYTE_ARRAY.PNG)))
/*     */       try {
/* 304 */         this.instream = paramDoc.getStreamForBytes();
/* 305 */         printableJob(new ImagePrinter(this.instream), this.reqAttrSet);
/* 306 */         return;
/*     */       } catch (ClassCastException localClassCastException1) {
/* 308 */         notifyEvent(103);
/* 309 */         throw new PrintException(localClassCastException1);
/*     */       } catch (IOException localIOException2) {
/* 311 */         notifyEvent(103);
/* 312 */         throw new PrintException(localIOException2);
/*     */       }
/* 314 */     if ((((DocFlavor)???).equals(DocFlavor.URL.GIF)) || 
/* 315 */       (((DocFlavor)???).equals(DocFlavor.URL.JPEG)) || 
/* 316 */       (((DocFlavor)???).equals(DocFlavor.URL.PNG)))
/*     */       try {
/* 318 */         printableJob(new ImagePrinter((URL)localObject2), this.reqAttrSet);
/* 319 */         return;
/*     */       } catch (ClassCastException localClassCastException2) {
/* 321 */         notifyEvent(103);
/* 322 */         throw new PrintException(localClassCastException2);
/*     */       }
/* 324 */     if (str.equals("java.awt.print.Pageable"))
/*     */       try {
/* 326 */         pageableJob((Pageable)paramDoc.getPrintData(), this.reqAttrSet);
/* 327 */         return;
/*     */       } catch (ClassCastException localClassCastException3) {
/* 329 */         notifyEvent(103);
/* 330 */         throw new PrintException(localClassCastException3);
/*     */       } catch (IOException localIOException3) {
/* 332 */         notifyEvent(103);
/* 333 */         throw new PrintException(localIOException3);
/*     */       }
/* 335 */     if (str.equals("java.awt.print.Printable")) {
/*     */       try {
/* 337 */         printableJob((Printable)paramDoc.getPrintData(), this.reqAttrSet);
/* 338 */         return;
/*     */       } catch (ClassCastException localClassCastException4) {
/* 340 */         notifyEvent(103);
/* 341 */         throw new PrintException(localClassCastException4);
/*     */       } catch (IOException localIOException4) {
/* 343 */         notifyEvent(103);
/* 344 */         throw new PrintException(localIOException4);
/*     */       }
/*     */     }
/* 347 */     notifyEvent(103);
/* 348 */     throw new PrintException("unrecognized class: " + str);
/*     */   }
/*     */   
/*     */   public void printableJob(Printable paramPrintable, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*     */     throws PrintException
/*     */   {
/*     */     try
/*     */     {
/* 356 */       synchronized (this) {
/* 357 */         if (this.job != null) {
/* 358 */           throw new PrintException("already printing");
/*     */         }
/* 360 */         this.job = new PSPrinterJob();
/*     */       }
/*     */       
/* 363 */       this.job.setPrintService(getPrintService());
/* 364 */       ??? = new PageFormat();
/* 365 */       if (this.mediaSize != null) {
/* 366 */         Paper localPaper = new Paper();
/* 367 */         localPaper.setSize(this.mediaSize.getX(25400) * 72.0D, this.mediaSize
/* 368 */           .getY(25400) * 72.0D);
/* 369 */         localPaper.setImageableArea(72.0D, 72.0D, localPaper.getWidth() - 144.0D, localPaper
/* 370 */           .getHeight() - 144.0D);
/* 371 */         ((PageFormat)???).setPaper(localPaper);
/*     */       }
/* 373 */       if (this.orient == OrientationRequested.REVERSE_LANDSCAPE) {
/* 374 */         ((PageFormat)???).setOrientation(2);
/* 375 */       } else if (this.orient == OrientationRequested.LANDSCAPE) {
/* 376 */         ((PageFormat)???).setOrientation(0);
/*     */       }
/* 378 */       this.job.setPrintable(paramPrintable, (PageFormat)???);
/* 379 */       this.job.print(paramPrintRequestAttributeSet);
/* 380 */       notifyEvent(102);
/* 381 */       return;
/*     */     } catch (PrinterException localPrinterException) {
/* 383 */       notifyEvent(103);
/* 384 */       throw new PrintException(localPrinterException);
/*     */     } finally {
/* 386 */       this.printReturned = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public void pageableJob(Pageable paramPageable, PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrintException
/*     */   {
/*     */     try
/*     */     {
/* 394 */       synchronized (this) {
/* 395 */         if (this.job != null) {
/* 396 */           throw new PrintException("already printing");
/*     */         }
/* 398 */         this.job = new PSPrinterJob();
/*     */       }
/*     */       
/* 401 */       this.job.setPrintService(getPrintService());
/* 402 */       this.job.setPageable(paramPageable);
/* 403 */       this.job.print(paramPrintRequestAttributeSet);
/* 404 */       notifyEvent(102);
/* 405 */       return;
/*     */     } catch (PrinterException localPrinterException) {
/* 407 */       notifyEvent(103);
/* 408 */       throw new PrintException(localPrinterException);
/*     */     } finally {
/* 410 */       this.printReturned = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void initializeAttributeSets(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
/*     */   {
/* 420 */     this.reqAttrSet = new HashPrintRequestAttributeSet();
/* 421 */     this.jobAttrSet = new HashPrintJobAttributeSet();
/*     */     
/*     */     Attribute[] arrayOfAttribute;
/* 424 */     if (paramPrintRequestAttributeSet != null) {
/* 425 */       this.reqAttrSet.addAll(paramPrintRequestAttributeSet);
/* 426 */       arrayOfAttribute = paramPrintRequestAttributeSet.toArray();
/* 427 */       for (int i = 0; i < arrayOfAttribute.length; i++) {
/* 428 */         if ((arrayOfAttribute[i] instanceof PrintJobAttribute)) {
/* 429 */           this.jobAttrSet.add(arrayOfAttribute[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 434 */     DocAttributeSet localDocAttributeSet = paramDoc.getAttributes();
/* 435 */     if (localDocAttributeSet != null) {
/* 436 */       arrayOfAttribute = localDocAttributeSet.toArray();
/* 437 */       for (int j = 0; j < arrayOfAttribute.length; j++) {
/* 438 */         if ((arrayOfAttribute[j] instanceof PrintRequestAttribute)) {
/* 439 */           this.reqAttrSet.add(arrayOfAttribute[j]);
/*     */         }
/* 441 */         if ((arrayOfAttribute[j] instanceof PrintJobAttribute)) {
/* 442 */           this.jobAttrSet.add(arrayOfAttribute[j]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 448 */     String str = "";
/*     */     try {
/* 450 */       str = System.getProperty("user.name");
/*     */     }
/*     */     catch (SecurityException localSecurityException) {}
/*     */     Object localObject1;
/* 454 */     if ((str == null) || (str.equals("")))
/*     */     {
/* 456 */       localObject1 = (RequestingUserName)paramPrintRequestAttributeSet.get(RequestingUserName.class);
/* 457 */       if (localObject1 != null) {
/* 458 */         this.jobAttrSet.add(new JobOriginatingUserName(((RequestingUserName)localObject1)
/* 459 */           .getValue(), ((RequestingUserName)localObject1)
/* 460 */           .getLocale()));
/*     */       } else {
/* 462 */         this.jobAttrSet.add(new JobOriginatingUserName("", null));
/*     */       }
/*     */     } else {
/* 465 */       this.jobAttrSet.add(new JobOriginatingUserName(str, null));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 470 */     if (this.jobAttrSet.get(JobName.class) == null) {
/*     */       Object localObject2;
/* 472 */       if ((localDocAttributeSet != null) && (localDocAttributeSet.get(DocumentName.class) != null))
/*     */       {
/* 474 */         localObject2 = (DocumentName)localDocAttributeSet.get(DocumentName.class);
/* 475 */         localObject1 = new JobName(((DocumentName)localObject2).getValue(), ((DocumentName)localObject2).getLocale());
/* 476 */         this.jobAttrSet.add((Attribute)localObject1);
/*     */       } else {
/* 478 */         localObject2 = "JPS Job:" + paramDoc;
/*     */         try {
/* 480 */           Object localObject3 = paramDoc.getPrintData();
/* 481 */           if ((localObject3 instanceof URL)) {
/* 482 */             localObject2 = ((URL)paramDoc.getPrintData()).toString();
/*     */           }
/*     */         }
/*     */         catch (IOException localIOException) {}
/* 486 */         localObject1 = new JobName((String)localObject2, null);
/* 487 */         this.jobAttrSet.add((Attribute)localObject1);
/*     */       }
/*     */     }
/*     */     
/* 491 */     this.jobAttrSet = AttributeSetUtilities.unmodifiableView(this.jobAttrSet);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void getAttributeValues(DocFlavor paramDocFlavor)
/*     */     throws PrintException
/*     */   {
/* 499 */     if (this.reqAttrSet.get(Fidelity.class) == Fidelity.FIDELITY_TRUE) {
/* 500 */       this.fidelity = true;
/*     */     } else {
/* 502 */       this.fidelity = false;
/*     */     }
/*     */     
/* 505 */     Attribute[] arrayOfAttribute = this.reqAttrSet.toArray();
/* 506 */     for (int i = 0; i < arrayOfAttribute.length; i++) {
/* 507 */       Attribute localAttribute = arrayOfAttribute[i];
/* 508 */       Class localClass = localAttribute.getCategory();
/* 509 */       if (this.fidelity == true) {
/* 510 */         if (!this.service.isAttributeCategorySupported(localClass)) {
/* 511 */           notifyEvent(103);
/* 512 */           throw new PrintJobAttributeException("unsupported category: " + localClass, localClass, null);
/*     */         }
/*     */         
/* 515 */         if (!this.service.isAttributeValueSupported(localAttribute, paramDocFlavor, null)) {
/* 516 */           notifyEvent(103);
/* 517 */           throw new PrintJobAttributeException("unsupported attribute: " + localAttribute, null, localAttribute);
/*     */         }
/*     */       }
/*     */       
/* 521 */       if (localClass == JobName.class) {
/* 522 */         this.jobName = ((JobName)localAttribute).getValue();
/* 523 */       } else if (localClass == Copies.class) {
/* 524 */         this.copies = ((Copies)localAttribute).getValue();
/* 525 */       } else if (localClass == Media.class) {
/* 526 */         if (((localAttribute instanceof MediaSizeName)) && 
/* 527 */           (this.service.isAttributeValueSupported(localAttribute, null, null)))
/*     */         {
/* 529 */           this.mediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localAttribute);
/*     */         }
/* 531 */       } else if (localClass == OrientationRequested.class) {
/* 532 */         this.orient = ((OrientationRequested)localAttribute);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void cancel() throws PrintException
/*     */   {
/* 539 */     synchronized (this) {
/* 540 */       if (!this.printing)
/* 541 */         throw new PrintException("Job is not yet submitted.");
/* 542 */       if ((this.job != null) && (!this.printReturned)) {
/* 543 */         this.job.cancel();
/* 544 */         notifyEvent(101);
/* 545 */         return;
/*     */       }
/* 547 */       throw new PrintException("Job could not be cancelled.");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\print\PSStreamPrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
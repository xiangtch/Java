/*     */ package sun.swing.text;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.print.PageFormat;
/*     */ import java.awt.print.Printable;
/*     */ import java.awt.print.PrinterException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.FutureTask;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.CellRendererPane;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JPasswordField;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.border.TitledBorder;
/*     */ import javax.swing.text.AbstractDocument;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.EditorKit;
/*     */ import javax.swing.text.JTextComponent;
/*     */ import javax.swing.text.html.HTML.Tag;
/*     */ import javax.swing.text.html.HTMLDocument;
/*     */ import javax.swing.text.html.HTMLDocument.Iterator;
/*     */ import sun.font.FontDesignMetrics;
/*     */ import sun.swing.text.html.FrameEditorPaneTag;
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
/*     */ public class TextComponentPrintable
/*     */   implements CountingPrintable
/*     */ {
/*     */   private static final int LIST_SIZE = 1000;
/*  95 */   private boolean isLayouted = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final JTextComponent textComponentToPrint;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 105 */   private final AtomicReference<FontRenderContext> frc = new AtomicReference(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final JTextComponent printShell;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final MessageFormat headerFormat;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final MessageFormat footerFormat;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final float HEADER_FONT_SIZE = 18.0F;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final float FOOTER_FONT_SIZE = 12.0F;
/*     */   
/*     */ 
/*     */ 
/*     */   private final Font headerFont;
/*     */   
/*     */ 
/*     */ 
/*     */   private final Font footerFont;
/*     */   
/*     */ 
/*     */ 
/*     */   private final List<IntegerSegment> rowsMetrics;
/*     */   
/*     */ 
/*     */ 
/*     */   private final List<IntegerSegment> pagesMetrics;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Printable getPrintable(JTextComponent paramJTextComponent, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2)
/*     */   {
/* 153 */     if (((paramJTextComponent instanceof JEditorPane)) && 
/* 154 */       (isFrameSetDocument(paramJTextComponent.getDocument())))
/*     */     {
/*     */ 
/* 157 */       List localList = getFrames((JEditorPane)paramJTextComponent);
/* 158 */       ArrayList localArrayList = new ArrayList();
/*     */       
/* 160 */       for (JEditorPane localJEditorPane : localList) {
/* 161 */         localArrayList.add(
/* 162 */           (CountingPrintable)getPrintable(localJEditorPane, paramMessageFormat1, paramMessageFormat2));
/*     */       }
/* 164 */       return new CompoundPrintable(localArrayList);
/*     */     }
/* 166 */     return new TextComponentPrintable(paramJTextComponent, paramMessageFormat1, paramMessageFormat2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isFrameSetDocument(Document paramDocument)
/*     */   {
/* 179 */     boolean bool = false;
/* 180 */     if ((paramDocument instanceof HTMLDocument)) {
/* 181 */       HTMLDocument localHTMLDocument = (HTMLDocument)paramDocument;
/* 182 */       if (localHTMLDocument.getIterator(HTML.Tag.FRAME).isValid()) {
/* 183 */         bool = true;
/*     */       }
/*     */     }
/* 186 */     return bool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static List<JEditorPane> getFrames(JEditorPane paramJEditorPane)
/*     */   {
/* 198 */     ArrayList localArrayList = new ArrayList();
/* 199 */     getFrames(paramJEditorPane, localArrayList);
/* 200 */     if (localArrayList.size() == 0)
/*     */     {
/*     */ 
/* 203 */       createFrames(paramJEditorPane);
/* 204 */       getFrames(paramJEditorPane, localArrayList);
/*     */     }
/* 206 */     return localArrayList;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void getFrames(Container paramContainer, List<JEditorPane> paramList)
/*     */   {
/* 219 */     for (Component localComponent : paramContainer.getComponents()) {
/* 220 */       if (((localComponent instanceof FrameEditorPaneTag)) && ((localComponent instanceof JEditorPane)))
/*     */       {
/* 222 */         paramList.add((JEditorPane)localComponent);
/*     */       }
/* 224 */       else if ((localComponent instanceof Container)) {
/* 225 */         getFrames((Container)localComponent, paramList);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void createFrames(JEditorPane paramJEditorPane)
/*     */   {
/* 237 */     Runnable local1 = new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 242 */         CellRendererPane localCellRendererPane = new CellRendererPane();
/* 243 */         localCellRendererPane.add(this.val$editor);
/*     */         
/*     */ 
/* 246 */         localCellRendererPane.setSize(500, 500);
/*     */       }
/*     */     };
/* 249 */     if (SwingUtilities.isEventDispatchThread()) {
/* 250 */       local1.run();
/*     */     } else {
/*     */       try {
/* 253 */         SwingUtilities.invokeAndWait(local1);
/*     */       } catch (Exception localException) {
/* 255 */         if ((localException instanceof RuntimeException)) {
/* 256 */           throw ((RuntimeException)localException);
/*     */         }
/* 258 */         throw new RuntimeException(localException);
/*     */       }
/*     */     }
/*     */   }
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
/*     */   private TextComponentPrintable(JTextComponent paramJTextComponent, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2)
/*     */   {
/* 275 */     this.textComponentToPrint = paramJTextComponent;
/* 276 */     this.headerFormat = paramMessageFormat1;
/* 277 */     this.footerFormat = paramMessageFormat2;
/* 278 */     this.headerFont = paramJTextComponent.getFont().deriveFont(1, 18.0F);
/*     */     
/* 280 */     this.footerFont = paramJTextComponent.getFont().deriveFont(0, 12.0F);
/*     */     
/*     */ 
/* 283 */     this.pagesMetrics = Collections.synchronizedList(new ArrayList());
/* 284 */     this.rowsMetrics = new ArrayList(1000);
/* 285 */     this.printShell = createPrintShell(paramJTextComponent);
/*     */   }
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
/*     */   private JTextComponent createPrintShell(final JTextComponent paramJTextComponent)
/*     */   {
/* 300 */     if (SwingUtilities.isEventDispatchThread()) {
/* 301 */       return createPrintShellOnEDT(paramJTextComponent);
/*     */     }
/* 303 */     FutureTask localFutureTask = new FutureTask(new Callable()
/*     */     {
/*     */       public JTextComponent call() throws Exception
/*     */       {
/* 307 */         return TextComponentPrintable.this.createPrintShellOnEDT(paramJTextComponent);
/*     */       }
/* 309 */     });
/* 310 */     SwingUtilities.invokeLater(localFutureTask);
/*     */     try {
/* 312 */       return (JTextComponent)localFutureTask.get();
/*     */     } catch (InterruptedException localInterruptedException) {
/* 314 */       throw new RuntimeException(localInterruptedException);
/*     */     } catch (ExecutionException localExecutionException) {
/* 316 */       Throwable localThrowable = localExecutionException.getCause();
/* 317 */       if ((localThrowable instanceof Error)) {
/* 318 */         throw ((Error)localThrowable);
/*     */       }
/* 320 */       if ((localThrowable instanceof RuntimeException)) {
/* 321 */         throw ((RuntimeException)localThrowable);
/*     */       }
/* 323 */       throw new AssertionError(localThrowable);
/*     */     }
/*     */   }
/*     */   
/*     */   private JTextComponent createPrintShellOnEDT(final JTextComponent paramJTextComponent) {
/* 328 */     assert (SwingUtilities.isEventDispatchThread());
/*     */     
/* 330 */     Object localObject = null;
/* 331 */     if ((paramJTextComponent instanceof JPasswordField)) {
/* 332 */       localObject = new JPasswordField()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public FontMetrics getFontMetrics(Font paramAnonymousFont)
/*     */         {
/*     */ 
/*     */ 
/* 341 */           return TextComponentPrintable.this.frc.get() == null ? 
/* 342 */             super.getFontMetrics(paramAnonymousFont) : 
/* 343 */             FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
/*     */         }
/*     */       };
/* 346 */     } else if ((paramJTextComponent instanceof JTextField)) {
/* 347 */       localObject = new JTextField()
/*     */       {
/*     */ 
/*     */ 
/*     */         public FontMetrics getFontMetrics(Font paramAnonymousFont)
/*     */         {
/*     */ 
/*     */ 
/* 355 */           return TextComponentPrintable.this.frc.get() == null ? 
/* 356 */             super.getFontMetrics(paramAnonymousFont) : 
/* 357 */             FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
/*     */         }
/*     */       };
/* 360 */     } else if ((paramJTextComponent instanceof JTextArea)) {
/* 361 */       localObject = new JTextArea()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public FontMetrics getFontMetrics(Font paramAnonymousFont)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 371 */           return TextComponentPrintable.this.frc.get() == null ? 
/* 372 */             super.getFontMetrics(paramAnonymousFont) : 
/* 373 */             FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
/*     */         }
/*     */       };
/* 376 */     } else if ((paramJTextComponent instanceof JTextPane)) {
/* 377 */       localObject = new JTextPane()
/*     */       {
/*     */         public FontMetrics getFontMetrics(Font paramAnonymousFont)
/*     */         {
/* 381 */           return TextComponentPrintable.this.frc.get() == null ? 
/* 382 */             super.getFontMetrics(paramAnonymousFont) : 
/* 383 */             FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
/*     */         }
/*     */         
/*     */         public EditorKit getEditorKit() {
/* 387 */           if (getDocument() == paramJTextComponent.getDocument()) {
/* 388 */             return ((JTextPane)paramJTextComponent).getEditorKit();
/*     */           }
/* 390 */           return super.getEditorKit();
/*     */         }
/*     */         
/*     */       };
/* 394 */     } else if ((paramJTextComponent instanceof JEditorPane)) {
/* 395 */       localObject = new JEditorPane()
/*     */       {
/*     */         public FontMetrics getFontMetrics(Font paramAnonymousFont)
/*     */         {
/* 399 */           return TextComponentPrintable.this.frc.get() == null ? 
/* 400 */             super.getFontMetrics(paramAnonymousFont) : 
/* 401 */             FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
/*     */         }
/*     */         
/*     */         public EditorKit getEditorKit() {
/* 405 */           if (getDocument() == paramJTextComponent.getDocument()) {
/* 406 */             return ((JEditorPane)paramJTextComponent).getEditorKit();
/*     */           }
/* 408 */           return super.getEditorKit();
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */ 
/* 414 */     ((JTextComponent)localObject).setBorder(null);
/*     */     
/*     */ 
/* 417 */     ((JTextComponent)localObject).setOpaque(paramJTextComponent.isOpaque());
/* 418 */     ((JTextComponent)localObject).setEditable(paramJTextComponent.isEditable());
/* 419 */     ((JTextComponent)localObject).setEnabled(paramJTextComponent.isEnabled());
/* 420 */     ((JTextComponent)localObject).setFont(paramJTextComponent.getFont());
/* 421 */     ((JTextComponent)localObject).setBackground(paramJTextComponent.getBackground());
/* 422 */     ((JTextComponent)localObject).setForeground(paramJTextComponent.getForeground());
/* 423 */     ((JTextComponent)localObject).setComponentOrientation(paramJTextComponent
/* 424 */       .getComponentOrientation());
/*     */     
/* 426 */     if ((localObject instanceof JEditorPane)) {
/* 427 */       ((JTextComponent)localObject).putClientProperty("JEditorPane.honorDisplayProperties", paramJTextComponent
/* 428 */         .getClientProperty("JEditorPane.honorDisplayProperties"));
/*     */       
/* 430 */       ((JTextComponent)localObject).putClientProperty("JEditorPane.w3cLengthUnits", paramJTextComponent
/* 431 */         .getClientProperty("JEditorPane.w3cLengthUnits"));
/* 432 */       ((JTextComponent)localObject).putClientProperty("charset", paramJTextComponent
/* 433 */         .getClientProperty("charset"));
/*     */     }
/* 435 */     ((JTextComponent)localObject).setDocument(paramJTextComponent.getDocument());
/* 436 */     return (JTextComponent)localObject;
/*     */   }
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
/*     */   public int getNumberOfPages()
/*     */   {
/* 450 */     return this.pagesMetrics.size();
/*     */   }
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
/*     */   public int print(final Graphics paramGraphics, final PageFormat paramPageFormat, final int paramInt)
/*     */     throws PrinterException
/*     */   {
/* 465 */     if (!this.isLayouted) {
/* 466 */       if ((paramGraphics instanceof Graphics2D)) {
/* 467 */         this.frc.set(((Graphics2D)paramGraphics).getFontRenderContext());
/*     */       }
/* 469 */       layout((int)Math.floor(paramPageFormat.getImageableWidth()));
/* 470 */       calculateRowsMetrics();
/*     */     }
/*     */     int i;
/* 473 */     if (!SwingUtilities.isEventDispatchThread()) {
/* 474 */       Callable local8 = new Callable() {
/*     */         public Integer call() throws Exception {
/* 476 */           return Integer.valueOf(TextComponentPrintable.this.printOnEDT(paramGraphics, paramPageFormat, paramInt));
/*     */         }
/* 478 */       };
/* 479 */       FutureTask localFutureTask = new FutureTask(local8);
/*     */       
/* 481 */       SwingUtilities.invokeLater(localFutureTask);
/*     */       try {
/* 483 */         i = ((Integer)localFutureTask.get()).intValue();
/*     */       } catch (InterruptedException localInterruptedException) {
/* 485 */         throw new RuntimeException(localInterruptedException);
/*     */       } catch (ExecutionException localExecutionException) {
/* 487 */         Throwable localThrowable = localExecutionException.getCause();
/* 488 */         if ((localThrowable instanceof PrinterException))
/* 489 */           throw ((PrinterException)localThrowable);
/* 490 */         if ((localThrowable instanceof RuntimeException))
/* 491 */           throw ((RuntimeException)localThrowable);
/* 492 */         if ((localThrowable instanceof Error)) {
/* 493 */           throw ((Error)localThrowable);
/*     */         }
/* 495 */         throw new RuntimeException(localThrowable);
/*     */       }
/*     */     }
/*     */     else {
/* 499 */       i = printOnEDT(paramGraphics, paramPageFormat, paramInt);
/*     */     }
/* 501 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int printOnEDT(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt)
/*     */     throws PrinterException
/*     */   {
/* 514 */     assert (SwingUtilities.isEventDispatchThread());
/* 515 */     Object localObject1 = BorderFactory.createEmptyBorder();
/*     */     
/* 517 */     if ((this.headerFormat != null) || (this.footerFormat != null))
/*     */     {
/* 519 */       localObject2 = new Object[] { Integer.valueOf(paramInt + 1) };
/* 520 */       if (this.headerFormat != null)
/*     */       {
/*     */ 
/*     */ 
/* 524 */         localObject1 = new TitledBorder((Border)localObject1, this.headerFormat.format(localObject2), 2, 1, this.headerFont, this.printShell.getForeground());
/*     */       }
/* 526 */       if (this.footerFormat != null)
/*     */       {
/*     */ 
/*     */ 
/* 530 */         localObject1 = new TitledBorder((Border)localObject1, this.footerFormat.format(localObject2), 2, 6, this.footerFont, this.printShell.getForeground());
/*     */       }
/*     */     }
/* 533 */     Object localObject2 = ((Border)localObject1).getBorderInsets(this.printShell);
/* 534 */     updatePagesMetrics(paramInt, 
/* 535 */       (int)Math.floor(paramPageFormat.getImageableHeight()) - ((Insets)localObject2).top - ((Insets)localObject2).bottom);
/*     */     
/*     */ 
/* 538 */     if (this.pagesMetrics.size() <= paramInt) {
/* 539 */       return 1;
/*     */     }
/*     */     
/* 542 */     Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
/*     */     
/* 544 */     localGraphics2D.translate(paramPageFormat.getImageableX(), paramPageFormat.getImageableY());
/* 545 */     ((Border)localObject1).paintBorder(this.printShell, localGraphics2D, 0, 0, 
/* 546 */       (int)Math.floor(paramPageFormat.getImageableWidth()), 
/* 547 */       (int)Math.floor(paramPageFormat.getImageableHeight()));
/*     */     
/* 549 */     localGraphics2D.translate(0, ((Insets)localObject2).top);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 554 */     Rectangle localRectangle = new Rectangle(0, 0, (int)paramPageFormat.getWidth(), ((IntegerSegment)this.pagesMetrics.get(paramInt)).end - ((IntegerSegment)this.pagesMetrics.get(paramInt)).start + 1);
/*     */     
/* 556 */     localGraphics2D.clip(localRectangle);
/* 557 */     int i = 0;
/*     */     
/* 559 */     if (ComponentOrientation.RIGHT_TO_LEFT == this.printShell.getComponentOrientation()) {
/* 560 */       i = (int)paramPageFormat.getImageableWidth() - this.printShell.getWidth();
/*     */     }
/* 562 */     localGraphics2D.translate(i, -((IntegerSegment)this.pagesMetrics.get(paramInt)).start);
/* 563 */     this.printShell.print(localGraphics2D);
/*     */     
/* 565 */     localGraphics2D.dispose();
/*     */     
/* 567 */     return 0;
/*     */   }
/*     */   
/*     */ 
/* 571 */   private boolean needReadLock = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void releaseReadLock()
/*     */   {
/* 579 */     assert (!SwingUtilities.isEventDispatchThread());
/* 580 */     Document localDocument = this.textComponentToPrint.getDocument();
/* 581 */     if ((localDocument instanceof AbstractDocument)) {
/*     */       try {
/* 583 */         ((AbstractDocument)localDocument).readUnlock();
/* 584 */         this.needReadLock = true;
/*     */       }
/*     */       catch (Error localError) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void acquireReadLock()
/*     */   {
/* 599 */     assert (!SwingUtilities.isEventDispatchThread());
/* 600 */     if (this.needReadLock)
/*     */     {
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 607 */         SwingUtilities.invokeAndWait(new Runnable()
/*     */         {
/*     */           public void run() {}
/*     */         });
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {}catch (InvocationTargetException localInvocationTargetException) {}
/*     */       
/*     */ 
/* 615 */       Document localDocument = this.textComponentToPrint.getDocument();
/* 616 */       ((AbstractDocument)localDocument).readLock();
/* 617 */       this.needReadLock = false;
/*     */     }
/*     */   }
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
/*     */   private void layout(final int paramInt)
/*     */   {
/* 638 */     if (!SwingUtilities.isEventDispatchThread()) {
/* 639 */       Callable local10 = new Callable() {
/*     */         public Object call() throws Exception {
/* 641 */           TextComponentPrintable.this.layoutOnEDT(paramInt);
/* 642 */           return null;
/*     */         }
/* 644 */       };
/* 645 */       FutureTask localFutureTask = new FutureTask(local10);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 652 */       releaseReadLock();
/* 653 */       SwingUtilities.invokeLater(localFutureTask);
/*     */       try {
/* 655 */         localFutureTask.get();
/*     */       } catch (InterruptedException localInterruptedException) {
/* 657 */         throw new RuntimeException(localInterruptedException);
/*     */       } catch (ExecutionException localExecutionException) {
/* 659 */         Throwable localThrowable = localExecutionException.getCause();
/* 660 */         if ((localThrowable instanceof RuntimeException))
/* 661 */           throw ((RuntimeException)localThrowable);
/* 662 */         if ((localThrowable instanceof Error)) {
/* 663 */           throw ((Error)localThrowable);
/*     */         }
/* 665 */         throw new RuntimeException(localThrowable);
/*     */       }
/*     */       finally {
/* 668 */         acquireReadLock();
/*     */       }
/*     */     } else {
/* 671 */       layoutOnEDT(paramInt);
/*     */     }
/*     */     
/* 674 */     this.isLayouted = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void layoutOnEDT(int paramInt)
/*     */   {
/* 683 */     assert (SwingUtilities.isEventDispatchThread());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 688 */     CellRendererPane localCellRendererPane = new CellRendererPane();
/*     */     
/*     */ 
/*     */ 
/* 692 */     JViewport localJViewport = new JViewport();
/* 693 */     localJViewport.setBorder(null);
/* 694 */     Dimension localDimension = new Dimension(paramInt, 2147482647);
/*     */     
/*     */ 
/*     */ 
/* 698 */     if ((this.printShell instanceof JTextField))
/*     */     {
/* 700 */       localDimension = new Dimension(localDimension.width, this.printShell.getPreferredSize().height);
/*     */     }
/* 702 */     this.printShell.setSize(localDimension);
/* 703 */     localJViewport.setComponentOrientation(this.printShell.getComponentOrientation());
/* 704 */     localJViewport.setSize(localDimension);
/* 705 */     localJViewport.add(this.printShell);
/* 706 */     localCellRendererPane.add(localJViewport);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updatePagesMetrics(int paramInt1, int paramInt2)
/*     */   {
/* 718 */     while ((paramInt1 >= this.pagesMetrics.size()) && (!this.rowsMetrics.isEmpty()))
/*     */     {
/* 720 */       int i = this.pagesMetrics.size() - 1;
/*     */       
/* 722 */       int j = i >= 0 ? ((IntegerSegment)this.pagesMetrics.get(i)).end + 1 : 0;
/*     */       
/*     */ 
/* 725 */       int k = 0;
/* 726 */       while ((
/* 727 */         k < this.rowsMetrics.size()) && (((IntegerSegment)this.rowsMetrics.get(k)).end - j + 1 <= paramInt2))
/*     */       {
/* 729 */         k++;
/*     */       }
/* 731 */       if (k == 0)
/*     */       {
/*     */ 
/* 734 */         this.pagesMetrics.add(new IntegerSegment(j, j + paramInt2 - 1));
/*     */       }
/*     */       else {
/* 737 */         k--;
/* 738 */         this.pagesMetrics.add(new IntegerSegment(j, 
/* 739 */           ((IntegerSegment)this.rowsMetrics.get(k)).end));
/* 740 */         for (int m = 0; m <= k; m++) {
/* 741 */           this.rowsMetrics.remove(0);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void calculateRowsMetrics()
/*     */   {
/* 756 */     int i = this.printShell.getDocument().getLength();
/* 757 */     ArrayList localArrayList = new ArrayList(1000);
/*     */     
/* 759 */     int j = 0;int k = -1; for (int m = -1; j < i; 
/* 760 */         j++) {
/*     */       try {
/* 762 */         Rectangle localRectangle = this.printShell.modelToView(j);
/* 763 */         if (localRectangle != null) {
/* 764 */           int n = (int)localRectangle.getY();
/* 765 */           int i1 = (int)localRectangle.getHeight();
/* 766 */           if ((i1 != 0) && ((n != k) || (i1 != m)))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 773 */             k = n;
/* 774 */             m = i1;
/* 775 */             localArrayList.add(new IntegerSegment(n, n + i1 - 1));
/*     */           }
/*     */         }
/*     */       } catch (BadLocationException localBadLocationException) {
/* 779 */         if (!$assertionsDisabled) { throw new AssertionError();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 785 */     Collections.sort(localArrayList);
/* 786 */     j = Integer.MIN_VALUE;
/* 787 */     k = Integer.MIN_VALUE;
/* 788 */     for (IntegerSegment localIntegerSegment : localArrayList) {
/* 789 */       if (k < localIntegerSegment.start) {
/* 790 */         if (k != Integer.MIN_VALUE) {
/* 791 */           this.rowsMetrics.add(new IntegerSegment(j, k));
/*     */         }
/* 793 */         j = localIntegerSegment.start;
/* 794 */         k = localIntegerSegment.end;
/*     */       } else {
/* 796 */         k = localIntegerSegment.end;
/*     */       }
/*     */     }
/* 799 */     if (k != Integer.MIN_VALUE) {
/* 800 */       this.rowsMetrics.add(new IntegerSegment(j, k));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class IntegerSegment
/*     */     implements Comparable<IntegerSegment>
/*     */   {
/*     */     final int start;
/*     */     
/*     */     final int end;
/*     */     
/*     */     IntegerSegment(int paramInt1, int paramInt2)
/*     */     {
/* 814 */       this.start = paramInt1;
/* 815 */       this.end = paramInt2;
/*     */     }
/*     */     
/*     */     public int compareTo(IntegerSegment paramIntegerSegment) {
/* 819 */       int i = this.start - paramIntegerSegment.start;
/* 820 */       return i != 0 ? i : this.end - paramIntegerSegment.end;
/*     */     }
/*     */     
/*     */     public boolean equals(Object paramObject)
/*     */     {
/* 825 */       if ((paramObject instanceof IntegerSegment)) {
/* 826 */         return compareTo((IntegerSegment)paramObject) == 0;
/*     */       }
/* 828 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 835 */       int i = 17;
/* 836 */       i = 37 * i + this.start;
/* 837 */       i = 37 * i + this.end;
/* 838 */       return i;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 843 */       return "IntegerSegment [" + this.start + ", " + this.end + "]";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\swing\text\TextComponentPrintable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
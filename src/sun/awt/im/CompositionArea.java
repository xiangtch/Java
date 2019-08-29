/*     */ package sun.awt.im;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.InputMethodEvent;
/*     */ import java.awt.event.InputMethodListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.font.TextHitInfo;
/*     */ import java.awt.font.TextLayout;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.im.InputMethodRequests;
/*     */ import java.text.AttributedCharacterIterator;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.border.LineBorder;
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
/*     */ public final class CompositionArea
/*     */   extends JPanel
/*     */   implements InputMethodListener
/*     */ {
/*     */   private CompositionAreaHandler handler;
/*     */   private TextLayout composedTextLayout;
/*  65 */   private TextHitInfo caret = null;
/*     */   private JFrame compositionWindow;
/*     */   private static final int TEXT_ORIGIN_X = 5;
/*     */   private static final int TEXT_ORIGIN_Y = 15;
/*     */   private static final int PASSIVE_WIDTH = 480;
/*     */   private static final int WIDTH_MARGIN = 10;
/*     */   private static final int HEIGHT_MARGIN = 3;
/*     */   private static final long serialVersionUID = -1057247068746557444L;
/*     */   
/*     */   CompositionArea() {
/*  75 */     String str = Toolkit.getProperty("AWT.CompositionWindowTitle", "Input Window");
/*     */     
/*  77 */     this.compositionWindow = ((JFrame)InputMethodContext.createInputMethodWindow(str, null, true));
/*     */     
/*  79 */     setOpaque(true);
/*  80 */     setBorder(LineBorder.createGrayLineBorder());
/*  81 */     setForeground(Color.black);
/*  82 */     setBackground(Color.white);
/*     */     
/*     */ 
/*     */ 
/*  86 */     enableInputMethods(true);
/*  87 */     enableEvents(8L);
/*     */     
/*  89 */     this.compositionWindow.getContentPane().add(this);
/*  90 */     this.compositionWindow.addWindowListener(new FrameWindowAdapter());
/*  91 */     addInputMethodListener(this);
/*  92 */     this.compositionWindow.enableInputMethods(false);
/*  93 */     this.compositionWindow.pack();
/*  94 */     Dimension localDimension1 = this.compositionWindow.getSize();
/*  95 */     Dimension localDimension2 = getToolkit().getScreenSize();
/*  96 */     this.compositionWindow.setLocation(localDimension2.width - localDimension1.width - 20, localDimension2.height - localDimension1.height - 100);
/*     */     
/*  98 */     this.compositionWindow.setVisible(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void setHandlerInfo(CompositionAreaHandler paramCompositionAreaHandler, InputContext paramInputContext)
/*     */   {
/* 106 */     this.handler = paramCompositionAreaHandler;
/* 107 */     ((InputMethodWindow)this.compositionWindow).setInputContext(paramInputContext);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputMethodRequests getInputMethodRequests()
/*     */   {
/* 114 */     return this.handler;
/*     */   }
/*     */   
/*     */   private Rectangle getCaretRectangle(TextHitInfo paramTextHitInfo)
/*     */   {
/* 119 */     int i = 0;
/* 120 */     TextLayout localTextLayout = this.composedTextLayout;
/* 121 */     if (localTextLayout != null) {
/* 122 */       i = Math.round(localTextLayout.getCaretInfo(paramTextHitInfo)[0]);
/*     */     }
/* 124 */     Graphics localGraphics = getGraphics();
/* 125 */     FontMetrics localFontMetrics = null;
/*     */     try {
/* 127 */       localFontMetrics = localGraphics.getFontMetrics();
/*     */     } finally {
/* 129 */       localGraphics.dispose();
/*     */     }
/* 131 */     return new Rectangle(5 + i, 15 - localFontMetrics
/* 132 */       .getAscent(), 0, localFontMetrics
/* 133 */       .getAscent() + localFontMetrics.getDescent());
/*     */   }
/*     */   
/*     */   public void paint(Graphics paramGraphics) {
/* 137 */     super.paint(paramGraphics);
/* 138 */     paramGraphics.setColor(getForeground());
/* 139 */     TextLayout localTextLayout = this.composedTextLayout;
/* 140 */     if (localTextLayout != null) {
/* 141 */       localTextLayout.draw((Graphics2D)paramGraphics, 5.0F, 15.0F);
/*     */     }
/* 143 */     if (this.caret != null) {
/* 144 */       Rectangle localRectangle = getCaretRectangle(this.caret);
/* 145 */       paramGraphics.setXORMode(getBackground());
/* 146 */       paramGraphics.fillRect(localRectangle.x, localRectangle.y, 1, localRectangle.height);
/* 147 */       paramGraphics.setPaintMode();
/*     */     }
/*     */   }
/*     */   
/*     */   void setCompositionAreaVisible(boolean paramBoolean)
/*     */   {
/* 153 */     this.compositionWindow.setVisible(paramBoolean);
/*     */   }
/*     */   
/*     */   boolean isCompositionAreaVisible()
/*     */   {
/* 158 */     return this.compositionWindow.isVisible();
/*     */   }
/*     */   
/*     */   class FrameWindowAdapter extends WindowAdapter {
/*     */     FrameWindowAdapter() {}
/*     */     
/* 164 */     public void windowActivated(WindowEvent paramWindowEvent) { CompositionArea.this.requestFocus(); }
/*     */   }
/*     */   
/*     */ 
/*     */   public void inputMethodTextChanged(InputMethodEvent paramInputMethodEvent)
/*     */   {
/* 170 */     this.handler.inputMethodTextChanged(paramInputMethodEvent);
/*     */   }
/*     */   
/*     */   public void caretPositionChanged(InputMethodEvent paramInputMethodEvent) {
/* 174 */     this.handler.caretPositionChanged(paramInputMethodEvent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void setText(AttributedCharacterIterator paramAttributedCharacterIterator, TextHitInfo paramTextHitInfo)
/*     */   {
/* 182 */     this.composedTextLayout = null;
/* 183 */     if (paramAttributedCharacterIterator == null)
/*     */     {
/* 185 */       this.compositionWindow.setVisible(false);
/* 186 */       this.caret = null;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 191 */       if (!this.compositionWindow.isVisible()) {
/* 192 */         this.compositionWindow.setVisible(true);
/*     */       }
/*     */       
/* 195 */       Graphics localGraphics = getGraphics();
/*     */       
/* 197 */       if (localGraphics == null) {
/* 198 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 202 */         updateWindowLocation();
/*     */         
/* 204 */         FontRenderContext localFontRenderContext = ((Graphics2D)localGraphics).getFontRenderContext();
/* 205 */         this.composedTextLayout = new TextLayout(paramAttributedCharacterIterator, localFontRenderContext);
/* 206 */         Rectangle2D localRectangle2D1 = this.composedTextLayout.getBounds();
/*     */         
/* 208 */         this.caret = paramTextHitInfo;
/*     */         
/*     */ 
/* 211 */         FontMetrics localFontMetrics = localGraphics.getFontMetrics();
/* 212 */         Rectangle2D localRectangle2D2 = localFontMetrics.getMaxCharBounds(localGraphics);
/* 213 */         int i = (int)localRectangle2D2.getHeight() + 3;
/*     */         
/* 215 */         int j = i + this.compositionWindow.getInsets().top + this.compositionWindow.getInsets().bottom;
/*     */         
/* 217 */         InputMethodRequests localInputMethodRequests = this.handler.getClientInputMethodRequests();
/* 218 */         int k = localInputMethodRequests == null ? 480 : (int)localRectangle2D1.getWidth() + 10;
/*     */         
/* 220 */         int m = k + this.compositionWindow.getInsets().left + this.compositionWindow.getInsets().right;
/* 221 */         setPreferredSize(new Dimension(k, i));
/* 222 */         this.compositionWindow.setSize(new Dimension(m, j));
/*     */         
/*     */ 
/* 225 */         paint(localGraphics);
/*     */       }
/*     */       finally {
/* 228 */         localGraphics.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   void setCaret(TextHitInfo paramTextHitInfo)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: putfield 306	sun/awt/im/CompositionArea:caret	Ljava/awt/font/TextHitInfo;
/*     */     //   5: aload_0
/*     */     //   6: getfield 308	sun/awt/im/CompositionArea:compositionWindow	Ljavax/swing/JFrame;
/*     */     //   9: invokevirtual 340	javax/swing/JFrame:isVisible	()Z
/*     */     //   12: ifeq +27 -> 39
/*     */     //   15: aload_0
/*     */     //   16: invokevirtual 363	sun/awt/im/CompositionArea:getGraphics	()Ljava/awt/Graphics;
/*     */     //   19: astore_2
/*     */     //   20: aload_0
/*     */     //   21: aload_2
/*     */     //   22: invokevirtual 364	sun/awt/im/CompositionArea:paint	(Ljava/awt/Graphics;)V
/*     */     //   25: aload_2
/*     */     //   26: invokevirtual 315	java/awt/Graphics:dispose	()V
/*     */     //   29: goto +10 -> 39
/*     */     //   32: astore_3
/*     */     //   33: aload_2
/*     */     //   34: invokevirtual 315	java/awt/Graphics:dispose	()V
/*     */     //   37: aload_3
/*     */     //   38: athrow
/*     */     //   39: return
/*     */     // Line number table:
/*     */     //   Java source line #238	-> byte code offset #0
/*     */     //   Java source line #239	-> byte code offset #5
/*     */     //   Java source line #240	-> byte code offset #15
/*     */     //   Java source line #242	-> byte code offset #20
/*     */     //   Java source line #244	-> byte code offset #25
/*     */     //   Java source line #245	-> byte code offset #29
/*     */     //   Java source line #244	-> byte code offset #32
/*     */     //   Java source line #245	-> byte code offset #37
/*     */     //   Java source line #247	-> byte code offset #39
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	40	0	this	CompositionArea
/*     */     //   0	40	1	paramTextHitInfo	TextHitInfo
/*     */     //   19	15	2	localGraphics	Graphics
/*     */     //   32	6	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   20	25	32	finally
/*     */   }
/*     */   
/*     */   void updateWindowLocation()
/*     */   {
/* 255 */     InputMethodRequests localInputMethodRequests = this.handler.getClientInputMethodRequests();
/* 256 */     if (localInputMethodRequests == null)
/*     */     {
/* 258 */       return;
/*     */     }
/*     */     
/* 261 */     Point localPoint = new Point();
/*     */     
/* 263 */     Rectangle localRectangle = localInputMethodRequests.getTextLocation(null);
/* 264 */     Dimension localDimension1 = Toolkit.getDefaultToolkit().getScreenSize();
/* 265 */     Dimension localDimension2 = this.compositionWindow.getSize();
/*     */     
/*     */ 
/* 268 */     if (localRectangle.x + localDimension2.width > localDimension1.width) {
/* 269 */       localPoint.x = (localDimension1.width - localDimension2.width);
/*     */     } else {
/* 271 */       localPoint.x = localRectangle.x;
/*     */     }
/*     */     
/* 274 */     if (localRectangle.y + localRectangle.height + 2 + localDimension2.height > localDimension1.height) {
/* 275 */       localPoint.y = (localRectangle.y - 2 - localDimension2.height);
/*     */     } else {
/* 277 */       localPoint.y = (localRectangle.y + localRectangle.height + 2);
/*     */     }
/*     */     
/* 280 */     this.compositionWindow.setLocation(localPoint);
/*     */   }
/*     */   
/*     */   Rectangle getTextLocation(TextHitInfo paramTextHitInfo)
/*     */   {
/* 285 */     Rectangle localRectangle = getCaretRectangle(paramTextHitInfo);
/* 286 */     Point localPoint = getLocationOnScreen();
/* 287 */     localRectangle.translate(localPoint.x, localPoint.y);
/* 288 */     return localRectangle;
/*     */   }
/*     */   
/*     */   TextHitInfo getLocationOffset(int paramInt1, int paramInt2) {
/* 292 */     TextLayout localTextLayout = this.composedTextLayout;
/* 293 */     if (localTextLayout == null) {
/* 294 */       return null;
/*     */     }
/* 296 */     Point localPoint = getLocationOnScreen();
/* 297 */     paramInt1 -= localPoint.x + 5;
/* 298 */     paramInt2 -= localPoint.y + 15;
/* 299 */     if (localTextLayout.getBounds().contains(paramInt1, paramInt2)) {
/* 300 */       return localTextLayout.hitTestChar(paramInt1, paramInt2);
/*     */     }
/* 302 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void setCompositionAreaUndecorated(boolean paramBoolean)
/*     */   {
/* 309 */     if (this.compositionWindow.isDisplayable()) {
/* 310 */       this.compositionWindow.removeNotify();
/*     */     }
/* 312 */     this.compositionWindow.setUndecorated(paramBoolean);
/* 313 */     this.compositionWindow.pack();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\im\CompositionArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*      */ package sun.swing;
/*      */ 
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.ComponentOrientation;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.FocusTraversalPolicy;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.Point;
/*      */ import java.awt.PrintGraphics;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.RenderingHints;
/*      */ import java.awt.Shape;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.InputEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.font.GlyphVector;
/*      */ import java.awt.font.LineBreakMeasurer;
/*      */ import java.awt.font.TextAttribute;
/*      */ import java.awt.font.TextHitInfo;
/*      */ import java.awt.font.TextLayout;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.print.PrinterGraphics;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.text.AttributedCharacterIterator;
/*      */ import java.text.AttributedString;
/*      */ import java.text.BreakIterator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.Callable;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.concurrent.FutureTask;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JTable;
/*      */ import javax.swing.ListCellRenderer;
/*      */ import javax.swing.ListModel;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIDefaults;
/*      */ import javax.swing.UIDefaults.LazyValue;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.event.TreeModelEvent;
/*      */ import javax.swing.table.TableCellRenderer;
/*      */ import javax.swing.table.TableColumn;
/*      */ import javax.swing.table.TableColumnModel;
/*      */ import javax.swing.text.DefaultCaret;
/*      */ import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
/*      */ import javax.swing.text.Highlighter.Highlight;
/*      */ import javax.swing.text.Highlighter.HighlightPainter;
/*      */ import javax.swing.text.JTextComponent;
/*      */ import javax.swing.tree.TreeModel;
/*      */ import javax.swing.tree.TreePath;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.font.FontDesignMetrics;
/*      */ import sun.font.FontUtilities;
/*      */ import sun.java2d.SunGraphicsEnvironment;
/*      */ import sun.print.ProxyPrintGraphics;
/*      */ import sun.security.util.SecurityConstants.AWT;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SwingUtilities2
/*      */ {
/*      */   public static final Object LAF_STATE_KEY;
/*      */   public static final Object MENU_SELECTION_MANAGER_LISTENER_KEY;
/*      */   
/*      */   public static class AATextInfo
/*      */   {
/*      */     Object aaHint;
/*      */     Integer lcdContrastHint;
/*      */     FontRenderContext frc;
/*      */     
/*      */     private static AATextInfo getAATextInfoFromMap(Map paramMap)
/*      */     {
/*  137 */       Object localObject1 = paramMap.get(RenderingHints.KEY_TEXT_ANTIALIASING);
/*  138 */       Object localObject2 = paramMap.get(RenderingHints.KEY_TEXT_LCD_CONTRAST);
/*      */       
/*  140 */       if ((localObject1 == null) || (localObject1 == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) || (localObject1 == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT))
/*      */       {
/*      */ 
/*  143 */         return null;
/*      */       }
/*  145 */       return new AATextInfo(localObject1, (Integer)localObject2);
/*      */     }
/*      */     
/*      */     public static AATextInfo getAATextInfo(boolean paramBoolean)
/*      */     {
/*  150 */       SunToolkit.setAAFontSettingsCondition(paramBoolean);
/*  151 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*  152 */       Object localObject = localToolkit.getDesktopProperty("awt.font.desktophints");
/*  153 */       if ((localObject instanceof Map)) {
/*  154 */         return getAATextInfoFromMap((Map)localObject);
/*      */       }
/*  156 */       return null;
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
/*      */     public AATextInfo(Object paramObject, Integer paramInteger)
/*      */     {
/*  170 */       if (paramObject == null) {
/*  171 */         throw new InternalError("null not allowed here");
/*      */       }
/*  173 */       if ((paramObject == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) || (paramObject == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT))
/*      */       {
/*  175 */         throw new InternalError("AA must be on");
/*      */       }
/*  177 */       this.aaHint = paramObject;
/*  178 */       this.lcdContrastHint = paramInteger;
/*  179 */       this.frc = new FontRenderContext(null, paramObject, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
/*      */     }
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*   82 */     LAF_STATE_KEY = new StringBuffer("LookAndFeel State");
/*      */     
/*      */ 
/*   85 */     MENU_SELECTION_MANAGER_LISTENER_KEY = new StringBuffer("MenuSelectionManager listener key");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  106 */     DEFAULT_FRC = new FontRenderContext(null, false, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  114 */     AA_TEXT_PROPERTY_KEY = new StringBuffer("AATextInfoPropertyKey");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  127 */     SKIP_CLICK_COUNT = new StringBuilder("skipClickCount");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  188 */     COMPONENT_UI_PROPERTY_KEY = new StringBuffer("ComponentUIPropertyKey");
/*      */     
/*      */ 
/*      */ 
/*  192 */     BASICMENUITEMUI_MAX_TEXT_OFFSET = new StringUIClientPropertyKey("maxTextOffset");
/*      */     
/*      */ 
/*      */ 
/*  196 */     inputEvent_CanAccessSystemClipboard_Field = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  202 */     charsBufferLock = new Object();
/*  203 */     charsBuffer = new char[100];
/*      */   }
/*      */   
/*  206 */   private static LSBCacheEntry[] fontCache = new LSBCacheEntry[6];
/*      */   private static final int CACHE_SIZE = 6;
/*      */   private static int nextIndex;
/*      */   private static LSBCacheEntry searchKey;
/*      */   private static final int MIN_CHAR_INDEX = 87;
/*      */   
/*      */   private static int syncCharsBuffer(String paramString) {
/*  213 */     int i = paramString.length();
/*  214 */     if ((charsBuffer == null) || (charsBuffer.length < i)) {
/*  215 */       charsBuffer = paramString.toCharArray();
/*      */     } else {
/*  217 */       paramString.getChars(0, i, charsBuffer, 0);
/*      */     }
/*  219 */     return i;
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
/*      */   public static final boolean isComplexLayout(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*      */   {
/*  232 */     return FontUtilities.isComplexText(paramArrayOfChar, paramInt1, paramInt2);
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
/*      */   public static AATextInfo drawTextAntialiased(JComponent paramJComponent)
/*      */   {
/*  255 */     if (paramJComponent != null)
/*      */     {
/*  257 */       return (AATextInfo)paramJComponent.getClientProperty(AA_TEXT_PROPERTY_KEY);
/*      */     }
/*      */     
/*  260 */     return null;
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
/*      */   public static int getLeftSideBearing(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString)
/*      */   {
/*  279 */     if ((paramString == null) || (paramString.length() == 0)) {
/*  280 */       return 0;
/*      */     }
/*  282 */     return getLeftSideBearing(paramJComponent, paramFontMetrics, paramString.charAt(0));
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
/*      */   public static int getLeftSideBearing(JComponent paramJComponent, FontMetrics paramFontMetrics, char paramChar)
/*      */   {
/*  295 */     int i = paramChar;
/*  296 */     if ((i < 88) && (i >= 87)) {
/*  297 */       Object localObject1 = null;
/*      */       
/*  299 */       FontRenderContext localFontRenderContext = getFontRenderContext(paramJComponent, paramFontMetrics);
/*  300 */       Font localFont = paramFontMetrics.getFont();
/*  301 */       synchronized (SwingUtilities2.class) {
/*  302 */         Object localObject2 = null;
/*  303 */         if (searchKey == null) {
/*  304 */           searchKey = new LSBCacheEntry(localFontRenderContext, localFont);
/*      */         } else {
/*  306 */           searchKey.reset(localFontRenderContext, localFont);
/*      */         }
/*      */         
/*  309 */         for (LSBCacheEntry localLSBCacheEntry : fontCache) {
/*  310 */           if (searchKey.equals(localLSBCacheEntry)) {
/*  311 */             localObject2 = localLSBCacheEntry;
/*  312 */             break;
/*      */           }
/*      */         }
/*  315 */         if (localObject2 == null)
/*      */         {
/*  317 */           localObject2 = searchKey;
/*  318 */           fontCache[nextIndex] = searchKey;
/*  319 */           searchKey = null;
/*  320 */           nextIndex = (nextIndex + 1) % 6;
/*      */         }
/*  322 */         return ((LSBCacheEntry)localObject2).getLeftSideBearing(paramChar);
/*      */       }
/*      */     }
/*  325 */     return 0;
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
/*      */   public static FontMetrics getFontMetrics(JComponent paramJComponent, Graphics paramGraphics)
/*      */   {
/*  345 */     return getFontMetrics(paramJComponent, paramGraphics, paramGraphics.getFont());
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
/*      */   public static FontMetrics getFontMetrics(JComponent paramJComponent, Graphics paramGraphics, Font paramFont)
/*      */   {
/*  368 */     if (paramJComponent != null)
/*      */     {
/*      */ 
/*      */ 
/*  372 */       return paramJComponent.getFontMetrics(paramFont);
/*      */     }
/*  374 */     return Toolkit.getDefaultToolkit().getFontMetrics(paramFont);
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
/*      */   public static int stringWidth(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString)
/*      */   {
/*  387 */     if ((paramString == null) || (paramString.equals(""))) {
/*  388 */       return 0;
/*      */     }
/*      */     
/*  391 */     boolean bool = (paramJComponent != null) && (paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null);
/*  392 */     if (bool) {
/*  393 */       synchronized (charsBufferLock) {
/*  394 */         int i = syncCharsBuffer(paramString);
/*  395 */         bool = isComplexLayout(charsBuffer, 0, i);
/*      */       }
/*      */     }
/*  398 */     if (bool) {
/*  399 */       ??? = createTextLayout(paramJComponent, paramString, paramFontMetrics
/*  400 */         .getFont(), paramFontMetrics.getFontRenderContext());
/*  401 */       return (int)((TextLayout)???).getAdvance();
/*      */     }
/*  403 */     return paramFontMetrics.stringWidth(paramString);
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
/*      */   public static String clipStringIfNecessary(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, int paramInt)
/*      */   {
/*  420 */     if ((paramString == null) || (paramString.equals(""))) {
/*  421 */       return "";
/*      */     }
/*  423 */     int i = stringWidth(paramJComponent, paramFontMetrics, paramString);
/*  424 */     if (i > paramInt) {
/*  425 */       return clipString(paramJComponent, paramFontMetrics, paramString, paramInt);
/*      */     }
/*  427 */     return paramString;
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
/*      */   public static String clipString(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, int paramInt)
/*      */   {
/*  444 */     String str = "...";
/*  445 */     paramInt -= stringWidth(paramJComponent, paramFontMetrics, str);
/*  446 */     if (paramInt <= 0)
/*      */     {
/*  448 */       return str;
/*      */     }
/*      */     
/*      */     boolean bool;
/*  452 */     synchronized (charsBufferLock) {
/*  453 */       int i = syncCharsBuffer(paramString);
/*      */       
/*  455 */       bool = isComplexLayout(charsBuffer, 0, i);
/*  456 */       if (!bool) {
/*  457 */         int j = 0;
/*  458 */         for (int k = 0; k < i; k++) {
/*  459 */           j += paramFontMetrics.charWidth(charsBuffer[k]);
/*  460 */           if (j > paramInt) {
/*  461 */             paramString = paramString.substring(0, k);
/*  462 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  467 */     if (bool) {
/*  468 */       ??? = new AttributedString(paramString);
/*  469 */       if (paramJComponent != null) {
/*  470 */         ((AttributedString)???).addAttribute(TextAttribute.NUMERIC_SHAPING, paramJComponent
/*  471 */           .getClientProperty(TextAttribute.NUMERIC_SHAPING));
/*      */       }
/*      */       
/*      */ 
/*  475 */       LineBreakMeasurer localLineBreakMeasurer = new LineBreakMeasurer(((AttributedString)???).getIterator(), BreakIterator.getCharacterInstance(), getFontRenderContext(paramJComponent, paramFontMetrics));
/*  476 */       paramString = paramString.substring(0, localLineBreakMeasurer.nextOffset(paramInt));
/*      */     }
/*      */     
/*  479 */     return paramString + str;
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
/*      */   public static void drawString(JComponent paramJComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2)
/*      */   {
/*  499 */     if ((paramString == null) || (paramString.length() <= 0)) return;
/*      */     Object localObject1;
/*      */     Object localObject2;
/*  502 */     if (isPrinting(paramGraphics)) {
/*  503 */       localObject1 = getGraphics2D(paramGraphics);
/*  504 */       if (localObject1 != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  511 */         localObject2 = trimTrailingSpaces(paramString);
/*  512 */         if (!((String)localObject2).isEmpty())
/*      */         {
/*  514 */           float f = (float)((Graphics2D)localObject1).getFont().getStringBounds((String)localObject2, DEFAULT_FRC).getWidth();
/*  515 */           TextLayout localTextLayout1 = createTextLayout(paramJComponent, paramString, ((Graphics2D)localObject1).getFont(), ((Graphics2D)localObject1)
/*  516 */             .getFontRenderContext());
/*      */           
/*  518 */           localTextLayout1 = localTextLayout1.getJustifiedLayout(f);
/*      */           
/*  520 */           Color localColor = ((Graphics2D)localObject1).getColor();
/*  521 */           if ((localColor instanceof PrintColorUIResource)) {
/*  522 */             ((Graphics2D)localObject1).setColor(((PrintColorUIResource)localColor).getPrintColor());
/*      */           }
/*      */           
/*  525 */           localTextLayout1.draw((Graphics2D)localObject1, paramInt1, paramInt2);
/*      */           
/*  527 */           ((Graphics2D)localObject1).setColor(localColor);
/*      */         }
/*      */         
/*  530 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  535 */     if ((paramGraphics instanceof Graphics2D)) {
/*  536 */       localObject1 = drawTextAntialiased(paramJComponent);
/*  537 */       localObject2 = (Graphics2D)paramGraphics;
/*      */       
/*      */ 
/*  540 */       boolean bool = (paramJComponent != null) && (paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null);
/*      */       
/*  542 */       if (bool) {
/*  543 */         synchronized (charsBufferLock) {
/*  544 */           int i = syncCharsBuffer(paramString);
/*  545 */           bool = isComplexLayout(charsBuffer, 0, i);
/*      */         }
/*      */       }
/*      */       
/*  549 */       if (localObject1 != null) {
/*  550 */         ??? = null;
/*  551 */         Object localObject3 = ((Graphics2D)localObject2).getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
/*  552 */         if (((AATextInfo)localObject1).aaHint != localObject3) {
/*  553 */           ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, ((AATextInfo)localObject1).aaHint);
/*      */         } else {
/*  555 */           localObject3 = null;
/*      */         }
/*  557 */         if (((AATextInfo)localObject1).lcdContrastHint != null) {
/*  558 */           ??? = ((Graphics2D)localObject2).getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
/*  559 */           if (((AATextInfo)localObject1).lcdContrastHint.equals(???)) {
/*  560 */             ??? = null;
/*      */           } else {
/*  562 */             ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, ((AATextInfo)localObject1).lcdContrastHint);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  567 */         if (bool) {
/*  568 */           TextLayout localTextLayout2 = createTextLayout(paramJComponent, paramString, ((Graphics2D)localObject2).getFont(), ((Graphics2D)localObject2)
/*  569 */             .getFontRenderContext());
/*  570 */           localTextLayout2.draw((Graphics2D)localObject2, paramInt1, paramInt2);
/*      */         } else {
/*  572 */           paramGraphics.drawString(paramString, paramInt1, paramInt2);
/*      */         }
/*      */         
/*  575 */         if (localObject3 != null) {
/*  576 */           ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, localObject3);
/*      */         }
/*  578 */         if (??? != null) {
/*  579 */           ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, ???);
/*      */         }
/*      */         
/*  582 */         return;
/*      */       }
/*      */       
/*  585 */       if (bool) {
/*  586 */         ??? = createTextLayout(paramJComponent, paramString, ((Graphics2D)localObject2).getFont(), ((Graphics2D)localObject2)
/*  587 */           .getFontRenderContext());
/*  588 */         ((TextLayout)???).draw((Graphics2D)localObject2, paramInt1, paramInt2);
/*  589 */         return;
/*      */       }
/*      */     }
/*      */     
/*  593 */     paramGraphics.drawString(paramString, paramInt1, paramInt2);
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
/*      */   public static void drawStringUnderlineCharAt(JComponent paramJComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  609 */     if ((paramString == null) || (paramString.length() <= 0)) {
/*  610 */       return;
/*      */     }
/*  612 */     drawString(paramJComponent, paramGraphics, paramString, paramInt2, paramInt3);
/*  613 */     int i = paramString.length();
/*  614 */     if ((paramInt1 >= 0) && (paramInt1 < i)) {
/*  615 */       int j = paramInt3;
/*  616 */       int k = 1;
/*  617 */       int m = 0;
/*  618 */       int n = 0;
/*  619 */       boolean bool1 = isPrinting(paramGraphics);
/*  620 */       boolean bool2 = bool1;
/*  621 */       if (!bool2) {
/*  622 */         synchronized (charsBufferLock) {
/*  623 */           syncCharsBuffer(paramString);
/*      */           
/*  625 */           bool2 = isComplexLayout(charsBuffer, 0, i);
/*      */         }
/*      */       }
/*  628 */       if (!bool2) {
/*  629 */         ??? = paramGraphics.getFontMetrics();
/*      */         
/*  631 */         m = paramInt2 + stringWidth(paramJComponent, (FontMetrics)???, paramString
/*  632 */           .substring(0, paramInt1));
/*  633 */         n = ((FontMetrics)???).charWidth(paramString
/*  634 */           .charAt(paramInt1));
/*      */       } else {
/*  636 */         ??? = getGraphics2D(paramGraphics);
/*  637 */         if (??? != null)
/*      */         {
/*  639 */           TextLayout localTextLayout = createTextLayout(paramJComponent, paramString, ((Graphics2D)???).getFont(), ((Graphics2D)???)
/*  640 */             .getFontRenderContext());
/*  641 */           if (bool1)
/*      */           {
/*  643 */             float f = (float)((Graphics2D)???).getFont().getStringBounds(paramString, DEFAULT_FRC).getWidth();
/*  644 */             localTextLayout = localTextLayout.getJustifiedLayout(f);
/*      */           }
/*      */           
/*  647 */           TextHitInfo localTextHitInfo1 = TextHitInfo.leading(paramInt1);
/*      */           
/*  649 */           TextHitInfo localTextHitInfo2 = TextHitInfo.trailing(paramInt1);
/*      */           
/*  651 */           Shape localShape = localTextLayout.getVisualHighlightShape(localTextHitInfo1, localTextHitInfo2);
/*  652 */           Rectangle localRectangle = localShape.getBounds();
/*  653 */           m = paramInt2 + localRectangle.x;
/*  654 */           n = localRectangle.width;
/*      */         }
/*      */       }
/*  657 */       paramGraphics.fillRect(m, j + 1, n, k);
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
/*      */   public static int loc2IndexFileList(JList paramJList, Point paramPoint)
/*      */   {
/*  671 */     int i = paramJList.locationToIndex(paramPoint);
/*  672 */     if (i != -1) {
/*  673 */       Object localObject = paramJList.getClientProperty("List.isFileList");
/*  674 */       if (((localObject instanceof Boolean)) && (((Boolean)localObject).booleanValue()) && 
/*  675 */         (!pointIsInActualBounds(paramJList, i, paramPoint))) {
/*  676 */         i = -1;
/*      */       }
/*      */     }
/*  679 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean pointIsInActualBounds(JList paramJList, int paramInt, Point paramPoint)
/*      */   {
/*  689 */     ListCellRenderer localListCellRenderer = paramJList.getCellRenderer();
/*  690 */     ListModel localListModel = paramJList.getModel();
/*  691 */     Object localObject = localListModel.getElementAt(paramInt);
/*  692 */     Component localComponent = localListCellRenderer.getListCellRendererComponent(paramJList, localObject, paramInt, false, false);
/*      */     
/*  694 */     Dimension localDimension = localComponent.getPreferredSize();
/*  695 */     Rectangle localRectangle = paramJList.getCellBounds(paramInt, paramInt);
/*  696 */     if (!localComponent.getComponentOrientation().isLeftToRight()) {
/*  697 */       localRectangle.x += localRectangle.width - localDimension.width;
/*      */     }
/*  699 */     localRectangle.width = localDimension.width;
/*      */     
/*  701 */     return localRectangle.contains(paramPoint);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean pointOutsidePrefSize(JTable paramJTable, int paramInt1, int paramInt2, Point paramPoint)
/*      */   {
/*  713 */     if ((paramJTable.convertColumnIndexToModel(paramInt2) != 0) || (paramInt1 == -1)) {
/*  714 */       return true;
/*      */     }
/*  716 */     TableCellRenderer localTableCellRenderer = paramJTable.getCellRenderer(paramInt1, paramInt2);
/*  717 */     Object localObject = paramJTable.getValueAt(paramInt1, paramInt2);
/*  718 */     Component localComponent = localTableCellRenderer.getTableCellRendererComponent(paramJTable, localObject, false, false, paramInt1, paramInt2);
/*      */     
/*  720 */     Dimension localDimension = localComponent.getPreferredSize();
/*  721 */     Rectangle localRectangle = paramJTable.getCellRect(paramInt1, paramInt2, false);
/*  722 */     localRectangle.width = localDimension.width;
/*  723 */     localRectangle.height = localDimension.height;
/*      */     
/*      */ 
/*      */ 
/*  727 */     assert ((paramPoint.x >= localRectangle.x) && (paramPoint.y >= localRectangle.y));
/*  728 */     return (paramPoint.x > localRectangle.x + localRectangle.width) || (paramPoint.y > localRectangle.y + localRectangle.height);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setLeadAnchorWithoutSelection(ListSelectionModel paramListSelectionModel, int paramInt1, int paramInt2)
/*      */   {
/*  737 */     if (paramInt2 == -1) {
/*  738 */       paramInt2 = paramInt1;
/*      */     }
/*  740 */     if (paramInt1 == -1) {
/*  741 */       paramListSelectionModel.setAnchorSelectionIndex(-1);
/*  742 */       paramListSelectionModel.setLeadSelectionIndex(-1);
/*      */     } else {
/*  744 */       if (paramListSelectionModel.isSelectedIndex(paramInt1)) {
/*  745 */         paramListSelectionModel.addSelectionInterval(paramInt1, paramInt1);
/*      */       } else {
/*  747 */         paramListSelectionModel.removeSelectionInterval(paramInt1, paramInt1);
/*      */       }
/*  749 */       paramListSelectionModel.setAnchorSelectionIndex(paramInt2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean shouldIgnore(MouseEvent paramMouseEvent, JComponent paramJComponent)
/*      */   {
/*  759 */     return (paramJComponent == null) || (!paramJComponent.isEnabled()) || 
/*  760 */       (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) || 
/*  761 */       (paramMouseEvent.isConsumed());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void adjustFocus(JComponent paramJComponent)
/*      */   {
/*  769 */     if ((!paramJComponent.hasFocus()) && (paramJComponent.isRequestFocusEnabled())) {
/*  770 */       paramJComponent.requestFocus();
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
/*      */   public static int drawChars(JComponent paramJComponent, Graphics paramGraphics, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  786 */     if (paramInt2 <= 0) {
/*  787 */       return paramInt3;
/*      */     }
/*  789 */     int i = paramInt3 + getFontMetrics(paramJComponent, paramGraphics).charsWidth(paramArrayOfChar, paramInt1, paramInt2);
/*  790 */     Object localObject2; Object localObject3; Object localObject4; if (isPrinting(paramGraphics)) {
/*  791 */       localObject1 = getGraphics2D(paramGraphics);
/*  792 */       if (localObject1 != null)
/*      */       {
/*  794 */         localObject2 = ((Graphics2D)localObject1).getFontRenderContext();
/*  795 */         localObject3 = getFontRenderContext(paramJComponent);
/*  796 */         if (localObject3 != null)
/*      */         {
/*  798 */           if (!isFontRenderContextPrintCompatible((FontRenderContext)localObject2, (FontRenderContext)localObject3))
/*      */           {
/*  800 */             localObject4 = new String(paramArrayOfChar, paramInt1, paramInt2);
/*  801 */             TextLayout localTextLayout = new TextLayout((String)localObject4, ((Graphics2D)localObject1).getFont(), (FontRenderContext)localObject2);
/*      */             
/*  803 */             String str = trimTrailingSpaces((String)localObject4);
/*  804 */             if (!str.isEmpty())
/*      */             {
/*  806 */               float f = (float)((Graphics2D)localObject1).getFont().getStringBounds(str, (FontRenderContext)localObject3).getWidth();
/*  807 */               localTextLayout = localTextLayout.getJustifiedLayout(f);
/*      */               
/*      */ 
/*  810 */               Color localColor = ((Graphics2D)localObject1).getColor();
/*  811 */               if ((localColor instanceof PrintColorUIResource)) {
/*  812 */                 ((Graphics2D)localObject1).setColor(((PrintColorUIResource)localColor).getPrintColor());
/*      */               }
/*      */               
/*  815 */               localTextLayout.draw((Graphics2D)localObject1, paramInt3, paramInt4);
/*      */               
/*  817 */               ((Graphics2D)localObject1).setColor(localColor);
/*      */             }
/*      */             
/*  820 */             return i;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  826 */     Object localObject1 = drawTextAntialiased(paramJComponent);
/*  827 */     if ((localObject1 != null) && ((paramGraphics instanceof Graphics2D))) {
/*  828 */       localObject2 = (Graphics2D)paramGraphics;
/*      */       
/*  830 */       localObject3 = null;
/*  831 */       localObject4 = ((Graphics2D)localObject2).getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
/*  832 */       if ((((AATextInfo)localObject1).aaHint != null) && (((AATextInfo)localObject1).aaHint != localObject4)) {
/*  833 */         ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, ((AATextInfo)localObject1).aaHint);
/*      */       } else {
/*  835 */         localObject4 = null;
/*      */       }
/*  837 */       if (((AATextInfo)localObject1).lcdContrastHint != null) {
/*  838 */         localObject3 = ((Graphics2D)localObject2).getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
/*  839 */         if (((AATextInfo)localObject1).lcdContrastHint.equals(localObject3)) {
/*  840 */           localObject3 = null;
/*      */         } else {
/*  842 */           ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, ((AATextInfo)localObject1).lcdContrastHint);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  847 */       paramGraphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       
/*  849 */       if (localObject4 != null) {
/*  850 */         ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, localObject4);
/*      */       }
/*  852 */       if (localObject3 != null) {
/*  853 */         ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, localObject3);
/*      */       }
/*      */     }
/*      */     else {
/*  857 */       paramGraphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*  859 */     return i;
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
/*      */   public static float drawString(JComponent paramJComponent, Graphics paramGraphics, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
/*      */   {
/*  872 */     boolean bool = isPrinting(paramGraphics);
/*  873 */     Color localColor = paramGraphics.getColor();
/*      */     
/*  875 */     if (bool)
/*      */     {
/*  877 */       if ((localColor instanceof PrintColorUIResource)) {
/*  878 */         paramGraphics.setColor(((PrintColorUIResource)localColor).getPrintColor());
/*      */       }
/*      */     }
/*      */     
/*  882 */     Graphics2D localGraphics2D = getGraphics2D(paramGraphics);
/*  883 */     float f1; if (localGraphics2D == null) {
/*  884 */       paramGraphics.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
/*      */       
/*  886 */       f1 = paramInt1;
/*      */     }
/*      */     else {
/*      */       FontRenderContext localFontRenderContext1;
/*  890 */       if (bool) {
/*  891 */         localFontRenderContext1 = getFontRenderContext(paramJComponent);
/*  892 */         if ((localFontRenderContext1.isAntiAliased()) || (localFontRenderContext1.usesFractionalMetrics())) {
/*  893 */           localFontRenderContext1 = new FontRenderContext(localFontRenderContext1.getTransform(), false, false);
/*      */         }
/*  895 */       } else if ((localFontRenderContext1 = getFRCProperty(paramJComponent)) == null)
/*      */       {
/*      */ 
/*  898 */         localFontRenderContext1 = localGraphics2D.getFontRenderContext();
/*      */       }
/*      */       TextLayout localTextLayout;
/*  901 */       if (bool) {
/*  902 */         FontRenderContext localFontRenderContext2 = localGraphics2D.getFontRenderContext();
/*  903 */         if (!isFontRenderContextPrintCompatible(localFontRenderContext1, localFontRenderContext2)) {
/*  904 */           localTextLayout = new TextLayout(paramAttributedCharacterIterator, localFontRenderContext2);
/*      */           
/*  906 */           AttributedCharacterIterator localAttributedCharacterIterator = getTrimmedTrailingSpacesIterator(paramAttributedCharacterIterator);
/*  907 */           if (localAttributedCharacterIterator != null)
/*      */           {
/*  909 */             float f2 = new TextLayout(localAttributedCharacterIterator, localFontRenderContext1).getAdvance();
/*  910 */             localTextLayout = localTextLayout.getJustifiedLayout(f2);
/*      */           }
/*      */         } else {
/*  913 */           localTextLayout = new TextLayout(paramAttributedCharacterIterator, localFontRenderContext1);
/*      */         }
/*      */       } else {
/*  916 */         localTextLayout = new TextLayout(paramAttributedCharacterIterator, localFontRenderContext1);
/*      */       }
/*  918 */       localTextLayout.draw(localGraphics2D, paramInt1, paramInt2);
/*  919 */       f1 = localTextLayout.getAdvance();
/*      */     }
/*      */     
/*  922 */     if (bool) {
/*  923 */       paramGraphics.setColor(localColor);
/*      */     }
/*      */     
/*  926 */     return f1;
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
/*      */   public static void drawVLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  941 */     if (paramInt3 < paramInt2) {
/*  942 */       int i = paramInt3;
/*  943 */       paramInt3 = paramInt2;
/*  944 */       paramInt2 = i;
/*      */     }
/*  946 */     paramGraphics.fillRect(paramInt1, paramInt2, 1, paramInt3 - paramInt2 + 1);
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
/*      */   public static void drawHLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  961 */     if (paramInt2 < paramInt1) {
/*  962 */       int i = paramInt2;
/*  963 */       paramInt2 = paramInt1;
/*  964 */       paramInt1 = i;
/*      */     }
/*  966 */     paramGraphics.fillRect(paramInt1, paramInt3, paramInt2 - paramInt1 + 1, 1);
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
/*      */   public static void drawRect(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  986 */     if ((paramInt3 < 0) || (paramInt4 < 0)) {
/*  987 */       return;
/*      */     }
/*      */     
/*  990 */     if ((paramInt4 == 0) || (paramInt3 == 0)) {
/*  991 */       paramGraphics.fillRect(paramInt1, paramInt2, paramInt3 + 1, paramInt4 + 1);
/*      */     } else {
/*  993 */       paramGraphics.fillRect(paramInt1, paramInt2, paramInt3, 1);
/*  994 */       paramGraphics.fillRect(paramInt1 + paramInt3, paramInt2, 1, paramInt4);
/*  995 */       paramGraphics.fillRect(paramInt1 + 1, paramInt2 + paramInt4, paramInt3, 1);
/*  996 */       paramGraphics.fillRect(paramInt1, paramInt2 + 1, 1, paramInt4);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static TextLayout createTextLayout(JComponent paramJComponent, String paramString, Font paramFont, FontRenderContext paramFontRenderContext)
/*      */   {
/* 1003 */     Object localObject = paramJComponent == null ? null : paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING);
/* 1004 */     if (localObject == null) {
/* 1005 */       return new TextLayout(paramString, paramFont, paramFontRenderContext);
/*      */     }
/* 1007 */     HashMap localHashMap = new HashMap();
/* 1008 */     localHashMap.put(TextAttribute.FONT, paramFont);
/* 1009 */     localHashMap.put(TextAttribute.NUMERIC_SHAPING, localObject);
/* 1010 */     return new TextLayout(paramString, localHashMap, paramFontRenderContext);
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
/*      */   private static boolean isFontRenderContextPrintCompatible(FontRenderContext paramFontRenderContext1, FontRenderContext paramFontRenderContext2)
/*      */   {
/* 1027 */     if (paramFontRenderContext1 == paramFontRenderContext2) {
/* 1028 */       return true;
/*      */     }
/*      */     
/* 1031 */     if ((paramFontRenderContext1 == null) || (paramFontRenderContext2 == null)) {
/* 1032 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1036 */     if (paramFontRenderContext1.getFractionalMetricsHint() != paramFontRenderContext2.getFractionalMetricsHint()) {
/* 1037 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1041 */     if ((!paramFontRenderContext1.isTransformed()) && (!paramFontRenderContext2.isTransformed())) {
/* 1042 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1049 */     double[] arrayOfDouble1 = new double[4];
/* 1050 */     double[] arrayOfDouble2 = new double[4];
/* 1051 */     paramFontRenderContext1.getTransform().getMatrix(arrayOfDouble1);
/* 1052 */     paramFontRenderContext2.getTransform().getMatrix(arrayOfDouble2);
/* 1053 */     return (arrayOfDouble1[0] == arrayOfDouble2[0]) && (arrayOfDouble1[1] == arrayOfDouble2[1]) && (arrayOfDouble1[2] == arrayOfDouble2[2]) && (arrayOfDouble1[3] == arrayOfDouble2[3]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Graphics2D getGraphics2D(Graphics paramGraphics)
/*      */   {
/* 1065 */     if ((paramGraphics instanceof Graphics2D))
/* 1066 */       return (Graphics2D)paramGraphics;
/* 1067 */     if ((paramGraphics instanceof ProxyPrintGraphics)) {
/* 1068 */       return (Graphics2D)((ProxyPrintGraphics)paramGraphics).getGraphics();
/*      */     }
/* 1070 */     return null;
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
/*      */   public static FontRenderContext getFontRenderContext(Component paramComponent)
/*      */   {
/* 1083 */     assert (paramComponent != null);
/* 1084 */     if (paramComponent == null) {
/* 1085 */       return DEFAULT_FRC;
/*      */     }
/* 1087 */     return paramComponent.getFontMetrics(paramComponent.getFont()).getFontRenderContext();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static FontRenderContext getFontRenderContext(Component paramComponent, FontMetrics paramFontMetrics)
/*      */   {
/* 1097 */     assert ((paramFontMetrics != null) || (paramComponent != null));
/* 1098 */     return paramFontMetrics != null ? paramFontMetrics.getFontRenderContext() : 
/* 1099 */       getFontRenderContext(paramComponent);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static FontMetrics getFontMetrics(JComponent paramJComponent, Font paramFont)
/*      */   {
/* 1109 */     FontRenderContext localFontRenderContext = getFRCProperty(paramJComponent);
/* 1110 */     if (localFontRenderContext == null) {
/* 1111 */       localFontRenderContext = DEFAULT_FRC;
/*      */     }
/* 1113 */     return FontDesignMetrics.getMetrics(paramFont, localFontRenderContext);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static FontRenderContext getFRCProperty(JComponent paramJComponent)
/*      */   {
/* 1121 */     if (paramJComponent != null)
/*      */     {
/* 1123 */       AATextInfo localAATextInfo = (AATextInfo)paramJComponent.getClientProperty(AA_TEXT_PROPERTY_KEY);
/* 1124 */       if (localAATextInfo != null) {
/* 1125 */         return localAATextInfo.frc;
/*      */       }
/*      */     }
/* 1128 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static boolean isPrinting(Graphics paramGraphics)
/*      */   {
/* 1136 */     return ((paramGraphics instanceof PrinterGraphics)) || ((paramGraphics instanceof PrintGraphics));
/*      */   }
/*      */   
/*      */   private static String trimTrailingSpaces(String paramString) {
/* 1140 */     int i = paramString.length() - 1;
/* 1141 */     while ((i >= 0) && (Character.isWhitespace(paramString.charAt(i)))) {
/* 1142 */       i--;
/*      */     }
/* 1144 */     return paramString.substring(0, i + 1);
/*      */   }
/*      */   
/*      */   private static AttributedCharacterIterator getTrimmedTrailingSpacesIterator(AttributedCharacterIterator paramAttributedCharacterIterator)
/*      */   {
/* 1149 */     int i = paramAttributedCharacterIterator.getIndex();
/*      */     
/* 1151 */     int j = paramAttributedCharacterIterator.last();
/* 1152 */     int k; while ((j != 65535) && (Character.isWhitespace(j))) {
/* 1153 */       k = paramAttributedCharacterIterator.previous();
/*      */     }
/*      */     
/* 1156 */     if (k != 65535) {
/* 1157 */       int m = paramAttributedCharacterIterator.getIndex();
/*      */       
/* 1159 */       if (m == paramAttributedCharacterIterator.getEndIndex() - 1) {
/* 1160 */         paramAttributedCharacterIterator.setIndex(i);
/* 1161 */         return paramAttributedCharacterIterator;
/*      */       }
/*      */       
/* 1164 */       AttributedString localAttributedString = new AttributedString(paramAttributedCharacterIterator, paramAttributedCharacterIterator.getBeginIndex(), m + 1);
/* 1165 */       return localAttributedString.getIterator();
/*      */     }
/*      */     
/* 1168 */     return null;
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
/*      */   public static boolean useSelectedTextColor(Highlighter.Highlight paramHighlight, JTextComponent paramJTextComponent)
/*      */   {
/* 1184 */     Highlighter.HighlightPainter localHighlightPainter = paramHighlight.getPainter();
/* 1185 */     String str = localHighlightPainter.getClass().getName();
/* 1186 */     if ((str.indexOf("javax.swing.text.DefaultHighlighter") != 0) && 
/* 1187 */       (str.indexOf("com.sun.java.swing.plaf.windows.WindowsTextUI") != 0)) {
/* 1188 */       return false;
/*      */     }
/*      */     try {
/* 1191 */       DefaultHighlighter.DefaultHighlightPainter localDefaultHighlightPainter = (DefaultHighlighter.DefaultHighlightPainter)localHighlightPainter;
/*      */       
/* 1193 */       if ((localDefaultHighlightPainter.getColor() != null) && 
/* 1194 */         (!localDefaultHighlightPainter.getColor().equals(paramJTextComponent.getSelectionColor()))) {
/* 1195 */         return false;
/*      */       }
/*      */     } catch (ClassCastException localClassCastException) {
/* 1198 */       return false;
/*      */     }
/* 1200 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class LSBCacheEntry
/*      */   {
/*      */     private static final byte UNSET = 127;
/*      */     
/*      */ 
/*      */ 
/* 1213 */     private static final char[] oneChar = new char[1];
/*      */     
/*      */     private byte[] lsbCache;
/*      */     private Font font;
/*      */     private FontRenderContext frc;
/*      */     
/*      */     public LSBCacheEntry(FontRenderContext paramFontRenderContext, Font paramFont)
/*      */     {
/* 1221 */       this.lsbCache = new byte[1];
/* 1222 */       reset(paramFontRenderContext, paramFont);
/*      */     }
/*      */     
/*      */     public void reset(FontRenderContext paramFontRenderContext, Font paramFont)
/*      */     {
/* 1227 */       this.font = paramFont;
/* 1228 */       this.frc = paramFontRenderContext;
/* 1229 */       for (int i = this.lsbCache.length - 1; i >= 0; i--) {
/* 1230 */         this.lsbCache[i] = Byte.MAX_VALUE;
/*      */       }
/*      */     }
/*      */     
/*      */     public int getLeftSideBearing(char paramChar) {
/* 1235 */       int i = paramChar - 'W';
/* 1236 */       assert ((i >= 0) && (i < 1));
/* 1237 */       int j = this.lsbCache[i];
/* 1238 */       if (j == 127) {
/* 1239 */         oneChar[0] = paramChar;
/* 1240 */         GlyphVector localGlyphVector = this.font.createGlyphVector(this.frc, oneChar);
/* 1241 */         j = (byte)localGlyphVector.getGlyphPixelBounds(0, this.frc, 0.0F, 0.0F).x;
/* 1242 */         if (j < 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1249 */           Object localObject = this.frc.getAntiAliasingHint();
/* 1250 */           if ((localObject == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB) || (localObject == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR))
/*      */           {
/* 1252 */             j = (byte)(j + 1);
/*      */           }
/*      */         }
/* 1255 */         this.lsbCache[i] = j;
/*      */       }
/* 1257 */       return j;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean equals(Object paramObject)
/*      */     {
/* 1263 */       if (paramObject == this) {
/* 1264 */         return true;
/*      */       }
/* 1266 */       if (!(paramObject instanceof LSBCacheEntry)) {
/* 1267 */         return false;
/*      */       }
/* 1269 */       LSBCacheEntry localLSBCacheEntry = (LSBCacheEntry)paramObject;
/* 1270 */       return (this.font.equals(localLSBCacheEntry.font)) && 
/* 1271 */         (this.frc.equals(localLSBCacheEntry.frc));
/*      */     }
/*      */     
/*      */     public int hashCode() {
/* 1275 */       int i = 17;
/* 1276 */       if (this.font != null) {
/* 1277 */         i = 37 * i + this.font.hashCode();
/*      */       }
/* 1279 */       if (this.frc != null) {
/* 1280 */         i = 37 * i + this.frc.hashCode();
/*      */       }
/* 1282 */       return i;
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
/*      */   public static boolean canAccessSystemClipboard()
/*      */   {
/* 1302 */     boolean bool = false;
/* 1303 */     if (!GraphicsEnvironment.isHeadless()) {
/* 1304 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 1305 */       if (localSecurityManager == null) {
/* 1306 */         bool = true;
/*      */       } else {
/*      */         try {
/* 1309 */           localSecurityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
/* 1310 */           bool = true;
/*      */         }
/*      */         catch (SecurityException localSecurityException) {}
/* 1313 */         if ((bool) && (!isTrustedContext())) {
/* 1314 */           bool = canCurrentEventAccessSystemClipboard(true);
/*      */         }
/*      */       }
/*      */     }
/* 1318 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean canCurrentEventAccessSystemClipboard()
/*      */   {
/* 1325 */     return (isTrustedContext()) || 
/* 1326 */       (canCurrentEventAccessSystemClipboard(false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean canEventAccessSystemClipboard(AWTEvent paramAWTEvent)
/*      */   {
/* 1336 */     return (isTrustedContext()) || 
/* 1337 */       (canEventAccessSystemClipboard(paramAWTEvent, false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static synchronized boolean inputEvent_canAccessSystemClipboard(InputEvent paramInputEvent)
/*      */   {
/* 1346 */     if (inputEvent_CanAccessSystemClipboard_Field == null)
/*      */     {
/* 1348 */       inputEvent_CanAccessSystemClipboard_Field = (Field)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Field run()
/*      */         {
/*      */           try {
/* 1353 */             Field localField = InputEvent.class.getDeclaredField("canAccessSystemClipboard");
/* 1354 */             localField.setAccessible(true);
/* 1355 */             return localField;
/*      */           }
/*      */           catch (SecurityException localSecurityException) {}catch (NoSuchFieldException localNoSuchFieldException) {}
/*      */           
/* 1359 */           return null;
/*      */         }
/*      */       });
/*      */     }
/* 1363 */     if (inputEvent_CanAccessSystemClipboard_Field == null) {
/* 1364 */       return false;
/*      */     }
/* 1366 */     boolean bool = false;
/*      */     try
/*      */     {
/* 1369 */       bool = inputEvent_CanAccessSystemClipboard_Field.getBoolean(paramInputEvent);
/*      */     }
/*      */     catch (IllegalAccessException localIllegalAccessException) {}
/* 1372 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean isAccessClipboardGesture(InputEvent paramInputEvent)
/*      */   {
/* 1383 */     boolean bool = false;
/* 1384 */     if ((paramInputEvent instanceof KeyEvent)) {
/* 1385 */       KeyEvent localKeyEvent = (KeyEvent)paramInputEvent;
/* 1386 */       int i = localKeyEvent.getKeyCode();
/* 1387 */       int j = localKeyEvent.getModifiers();
/* 1388 */       switch (i) {
/*      */       case 67: 
/*      */       case 86: 
/*      */       case 88: 
/* 1392 */         bool = j == 2;
/* 1393 */         break;
/*      */       case 155: 
/* 1395 */         bool = (j == 2) || (j == 1);
/*      */         
/* 1397 */         break;
/*      */       case 65485: 
/*      */       case 65487: 
/*      */       case 65489: 
/* 1401 */         bool = true;
/* 1402 */         break;
/*      */       case 127: 
/* 1404 */         bool = j == 1;
/*      */       }
/*      */       
/*      */     }
/* 1408 */     return bool;
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
/*      */   private static boolean canEventAccessSystemClipboard(AWTEvent paramAWTEvent, boolean paramBoolean)
/*      */   {
/* 1421 */     if (EventQueue.isDispatchThread())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1426 */       if (((paramAWTEvent instanceof InputEvent)) && ((!paramBoolean) || 
/* 1427 */         (isAccessClipboardGesture((InputEvent)paramAWTEvent)))) {
/* 1428 */         return inputEvent_canAccessSystemClipboard((InputEvent)paramAWTEvent);
/*      */       }
/* 1430 */       return false;
/*      */     }
/*      */     
/* 1433 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void checkAccess(int paramInt)
/*      */   {
/* 1444 */     if ((System.getSecurityManager() != null) && 
/* 1445 */       (!Modifier.isPublic(paramInt))) {
/* 1446 */       throw new SecurityException("Resource is not accessible");
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
/*      */   private static boolean canCurrentEventAccessSystemClipboard(boolean paramBoolean)
/*      */   {
/* 1459 */     AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
/* 1460 */     return canEventAccessSystemClipboard(localAWTEvent, paramBoolean);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean isTrustedContext()
/*      */   {
/* 1469 */     return (System.getSecurityManager() == null) || 
/*      */     
/* 1471 */       (AppContext.getAppContext().get("UNTRUSTED_CLIPBOARD_ACCESS_KEY") == null);
/*      */   }
/*      */   
/*      */   public static String displayPropertiesToCSS(Font paramFont, Color paramColor) {
/* 1475 */     StringBuffer localStringBuffer = new StringBuffer("body {");
/* 1476 */     if (paramFont != null) {
/* 1477 */       localStringBuffer.append(" font-family: ");
/* 1478 */       localStringBuffer.append(paramFont.getFamily());
/* 1479 */       localStringBuffer.append(" ; ");
/* 1480 */       localStringBuffer.append(" font-size: ");
/* 1481 */       localStringBuffer.append(paramFont.getSize());
/* 1482 */       localStringBuffer.append("pt ;");
/* 1483 */       if (paramFont.isBold()) {
/* 1484 */         localStringBuffer.append(" font-weight: 700 ; ");
/*      */       }
/* 1486 */       if (paramFont.isItalic()) {
/* 1487 */         localStringBuffer.append(" font-style: italic ; ");
/*      */       }
/*      */     }
/* 1490 */     if (paramColor != null) {
/* 1491 */       localStringBuffer.append(" color: #");
/* 1492 */       if (paramColor.getRed() < 16) {
/* 1493 */         localStringBuffer.append('0');
/*      */       }
/* 1495 */       localStringBuffer.append(Integer.toHexString(paramColor.getRed()));
/* 1496 */       if (paramColor.getGreen() < 16) {
/* 1497 */         localStringBuffer.append('0');
/*      */       }
/* 1499 */       localStringBuffer.append(Integer.toHexString(paramColor.getGreen()));
/* 1500 */       if (paramColor.getBlue() < 16) {
/* 1501 */         localStringBuffer.append('0');
/*      */       }
/* 1503 */       localStringBuffer.append(Integer.toHexString(paramColor.getBlue()));
/* 1504 */       localStringBuffer.append(" ; ");
/*      */     }
/* 1506 */     localStringBuffer.append(" }");
/* 1507 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int MAX_CHAR_INDEX = 88;
/*      */   
/*      */ 
/*      */ 
/*      */   public static final FontRenderContext DEFAULT_FRC;
/*      */   
/*      */ 
/*      */ 
/*      */   public static final Object AA_TEXT_PROPERTY_KEY;
/*      */   
/*      */ 
/*      */ 
/*      */   public static final String IMPLIED_CR = "CR";
/*      */   
/*      */ 
/*      */ 
/*      */   public static Object makeIcon(Class<?> paramClass1, final Class<?> paramClass2, final String paramString)
/*      */   {
/* 1531 */     new LazyValue()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public Object createValue(UIDefaults paramAnonymousUIDefaults)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1541 */         byte[] arrayOfByte = (byte[])AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public byte[] run() {
/*      */             try {
/* 1545 */               InputStream localInputStream = null;
/* 1546 */               Class localClass = SwingUtilities2.2.this.val$baseClass;
/*      */               
/* 1548 */               while (localClass != null) {
/* 1549 */                 localInputStream = localClass.getResourceAsStream(SwingUtilities2.2.this.val$imageFile);
/*      */                 
/* 1551 */                 if ((localInputStream != null) || (localClass == SwingUtilities2.2.this.val$rootClass)) {
/*      */                   break;
/*      */                 }
/*      */                 
/* 1555 */                 localClass = localClass.getSuperclass();
/*      */               }
/*      */               
/* 1558 */               if (localInputStream == null) {
/* 1559 */                 return null;
/*      */               }
/*      */               
/* 1562 */               BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream);
/*      */               
/* 1564 */               ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1024);
/*      */               
/* 1566 */               byte[] arrayOfByte = new byte['Ѐ'];
/*      */               int i;
/* 1568 */               while ((i = localBufferedInputStream.read(arrayOfByte)) > 0) {
/* 1569 */                 localByteArrayOutputStream.write(arrayOfByte, 0, i);
/*      */               }
/* 1571 */               localBufferedInputStream.close();
/* 1572 */               localByteArrayOutputStream.flush();
/* 1573 */               return localByteArrayOutputStream.toByteArray();
/*      */             } catch (IOException localIOException) {
/* 1575 */               System.err.println(localIOException.toString());
/*      */             }
/* 1577 */             return null;
/*      */           }
/*      */         });
/*      */         
/* 1581 */         if (arrayOfByte == null) {
/* 1582 */           return null;
/*      */         }
/* 1584 */         if (arrayOfByte.length == 0) {
/* 1585 */           System.err.println("warning: " + paramString + " is zero-length");
/*      */           
/* 1587 */           return null;
/*      */         }
/*      */         
/* 1590 */         return new ImageIconUIResource(arrayOfByte);
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isLocalDisplay()
/*      */   {
/* 1604 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 1605 */     boolean bool; if ((localGraphicsEnvironment instanceof SunGraphicsEnvironment)) {
/* 1606 */       bool = ((SunGraphicsEnvironment)localGraphicsEnvironment).isDisplayLocal();
/*      */     } else {
/* 1608 */       bool = true;
/*      */     }
/* 1610 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int getUIDefaultsInt(Object paramObject)
/*      */   {
/* 1622 */     return getUIDefaultsInt(paramObject, 0);
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
/*      */   public static int getUIDefaultsInt(Object paramObject, Locale paramLocale)
/*      */   {
/* 1637 */     return getUIDefaultsInt(paramObject, paramLocale, 0);
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
/*      */   public static int getUIDefaultsInt(Object paramObject, int paramInt)
/*      */   {
/* 1653 */     return getUIDefaultsInt(paramObject, null, paramInt);
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
/*      */   public static int getUIDefaultsInt(Object paramObject, Locale paramLocale, int paramInt)
/*      */   {
/* 1670 */     Object localObject = UIManager.get(paramObject, paramLocale);
/*      */     
/* 1672 */     if ((localObject instanceof Integer)) {
/* 1673 */       return ((Integer)localObject).intValue();
/*      */     }
/* 1675 */     if ((localObject instanceof String)) {
/*      */       try {
/* 1677 */         return Integer.parseInt((String)localObject);
/*      */       } catch (NumberFormatException localNumberFormatException) {}
/*      */     }
/* 1680 */     return paramInt;
/*      */   }
/*      */   
/*      */ 
/*      */   public static Component compositeRequestFocus(Component paramComponent)
/*      */   {
/* 1686 */     if ((paramComponent instanceof Container)) {
/* 1687 */       Container localContainer = (Container)paramComponent;
/* 1688 */       Object localObject2; if (localContainer.isFocusCycleRoot()) {
/* 1689 */         localObject1 = localContainer.getFocusTraversalPolicy();
/* 1690 */         localObject2 = ((FocusTraversalPolicy)localObject1).getDefaultComponent(localContainer);
/* 1691 */         if (localObject2 != null) {
/* 1692 */           ((Component)localObject2).requestFocus();
/* 1693 */           return (Component)localObject2;
/*      */         }
/*      */       }
/* 1696 */       Object localObject1 = localContainer.getFocusCycleRootAncestor();
/* 1697 */       if (localObject1 != null) {
/* 1698 */         localObject2 = ((Container)localObject1).getFocusTraversalPolicy();
/* 1699 */         Component localComponent = ((FocusTraversalPolicy)localObject2).getComponentAfter((Container)localObject1, localContainer);
/*      */         
/* 1701 */         if ((localComponent != null) && (SwingUtilities.isDescendingFrom(localComponent, localContainer))) {
/* 1702 */           localComponent.requestFocus();
/* 1703 */           return localComponent;
/*      */         }
/*      */       }
/*      */     }
/* 1707 */     if (paramComponent.isFocusable()) {
/* 1708 */       paramComponent.requestFocus();
/* 1709 */       return paramComponent;
/*      */     }
/* 1711 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean tabbedPaneChangeFocusTo(Component paramComponent)
/*      */   {
/* 1720 */     if (paramComponent != null) {
/* 1721 */       if (paramComponent.isFocusTraversable()) {
/* 1722 */         compositeRequestFocus(paramComponent);
/* 1723 */         return true; }
/* 1724 */       if (((paramComponent instanceof JComponent)) && 
/* 1725 */         (((JComponent)paramComponent).requestDefaultFocus()))
/*      */       {
/* 1727 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1731 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <V> Future<V> submit(Callable<V> paramCallable)
/*      */   {
/* 1743 */     if (paramCallable == null) {
/* 1744 */       throw new NullPointerException();
/*      */     }
/* 1746 */     FutureTask localFutureTask = new FutureTask(paramCallable);
/* 1747 */     execute(localFutureTask);
/* 1748 */     return localFutureTask;
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
/*      */   public static <V> Future<V> submit(Runnable paramRunnable, V paramV)
/*      */   {
/* 1763 */     if (paramRunnable == null) {
/* 1764 */       throw new NullPointerException();
/*      */     }
/* 1766 */     FutureTask localFutureTask = new FutureTask(paramRunnable, paramV);
/* 1767 */     execute(localFutureTask);
/* 1768 */     return localFutureTask;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void execute(Runnable paramRunnable)
/*      */   {
/* 1775 */     SwingUtilities.invokeLater(paramRunnable);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setSkipClickCount(Component paramComponent, int paramInt)
/*      */   {
/* 1786 */     if (((paramComponent instanceof JTextComponent)) && 
/* 1787 */       ((((JTextComponent)paramComponent).getCaret() instanceof DefaultCaret)))
/*      */     {
/* 1789 */       ((JTextComponent)paramComponent).putClientProperty(SKIP_CLICK_COUNT, Integer.valueOf(paramInt));
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
/*      */   public static int getAdjustedClickCount(JTextComponent paramJTextComponent, MouseEvent paramMouseEvent)
/*      */   {
/* 1802 */     int i = paramMouseEvent.getClickCount();
/*      */     
/* 1804 */     if (i == 1) {
/* 1805 */       paramJTextComponent.putClientProperty(SKIP_CLICK_COUNT, null);
/*      */     } else {
/* 1807 */       Integer localInteger = (Integer)paramJTextComponent.getClientProperty(SKIP_CLICK_COUNT);
/* 1808 */       if (localInteger != null) {
/* 1809 */         return i - localInteger.intValue();
/*      */       }
/*      */     }
/*      */     
/* 1813 */     return i;
/*      */   }
/*      */   
/*      */ 
/*      */   public static abstract interface RepaintListener
/*      */   {
/*      */     public abstract void repaintPerformed(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*      */   }
/*      */   
/*      */ 
/*      */   public static enum Section
/*      */   {
/* 1825 */     LEADING, 
/*      */     
/*      */ 
/* 1828 */     MIDDLE, 
/*      */     
/*      */ 
/* 1831 */     TRAILING;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private Section() {}
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
/*      */   private static Section liesIn(Rectangle paramRectangle, Point paramPoint, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*      */   {
/*      */     int i;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     int j;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     int k;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean bool;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1889 */     if (paramBoolean1) {
/* 1890 */       i = paramRectangle.x;
/* 1891 */       j = paramPoint.x;
/* 1892 */       k = paramRectangle.width;
/* 1893 */       bool = paramBoolean2;
/*      */     } else {
/* 1895 */       i = paramRectangle.y;
/* 1896 */       j = paramPoint.y;
/* 1897 */       k = paramRectangle.height;
/* 1898 */       bool = true;
/*      */     }
/*      */     
/* 1901 */     if (paramBoolean3) {
/* 1902 */       m = k >= 30 ? 10 : k / 3;
/*      */       
/* 1904 */       if (j < i + m)
/* 1905 */         return bool ? Section.LEADING : Section.TRAILING;
/* 1906 */       if (j >= i + k - m) {
/* 1907 */         return bool ? Section.TRAILING : Section.LEADING;
/*      */       }
/*      */       
/* 1910 */       return Section.MIDDLE;
/*      */     }
/* 1912 */     int m = i + k / 2;
/* 1913 */     if (bool) {
/* 1914 */       return j >= m ? Section.TRAILING : Section.LEADING;
/*      */     }
/* 1916 */     return j < m ? Section.TRAILING : Section.LEADING;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static final StringBuilder SKIP_CLICK_COUNT;
/*      */   
/*      */ 
/*      */   public static final Object COMPONENT_UI_PROPERTY_KEY;
/*      */   
/*      */ 
/*      */   public static final StringUIClientPropertyKey BASICMENUITEMUI_MAX_TEXT_OFFSET;
/*      */   
/*      */ 
/*      */   private static Field inputEvent_CanAccessSystemClipboard_Field;
/*      */   
/*      */ 
/*      */   private static final String UntrustedClipboardAccess = "UNTRUSTED_CLIPBOARD_ACCESS_KEY";
/*      */   
/*      */   private static final int CHAR_BUFFER_SIZE = 100;
/*      */   
/*      */   private static final Object charsBufferLock;
/*      */   
/*      */   private static char[] charsBuffer;
/*      */   
/*      */   public static Section liesInHorizontal(Rectangle paramRectangle, Point paramPoint, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 1943 */     return liesIn(paramRectangle, paramPoint, true, paramBoolean1, paramBoolean2);
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
/*      */   public static Section liesInVertical(Rectangle paramRectangle, Point paramPoint, boolean paramBoolean)
/*      */   {
/* 1966 */     return liesIn(paramRectangle, paramPoint, false, false, paramBoolean);
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
/*      */   public static int convertColumnIndexToModel(TableColumnModel paramTableColumnModel, int paramInt)
/*      */   {
/* 1985 */     if (paramInt < 0) {
/* 1986 */       return paramInt;
/*      */     }
/* 1988 */     return paramTableColumnModel.getColumn(paramInt).getModelIndex();
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
/*      */   public static int convertColumnIndexToView(TableColumnModel paramTableColumnModel, int paramInt)
/*      */   {
/* 2008 */     if (paramInt < 0) {
/* 2009 */       return paramInt;
/*      */     }
/* 2011 */     for (int i = 0; i < paramTableColumnModel.getColumnCount(); i++) {
/* 2012 */       if (paramTableColumnModel.getColumn(i).getModelIndex() == paramInt) {
/* 2013 */         return i;
/*      */       }
/*      */     }
/* 2016 */     return -1;
/*      */   }
/*      */   
/*      */   public static int getSystemMnemonicKeyMask() {
/* 2020 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 2021 */     if ((localToolkit instanceof SunToolkit)) {
/* 2022 */       return ((SunToolkit)localToolkit).getFocusAcceleratorKeyMask();
/*      */     }
/* 2024 */     return 8;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static TreePath getTreePath(TreeModelEvent paramTreeModelEvent, TreeModel paramTreeModel)
/*      */   {
/* 2035 */     TreePath localTreePath = paramTreeModelEvent.getTreePath();
/* 2036 */     if ((localTreePath == null) && (paramTreeModel != null)) {
/* 2037 */       Object localObject = paramTreeModel.getRoot();
/* 2038 */       if (localObject != null) {
/* 2039 */         localTreePath = new TreePath(localObject);
/*      */       }
/*      */     }
/* 2042 */     return localTreePath;
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\swing\SwingUtilities2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
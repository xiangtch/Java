/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.InputMethodEvent;
/*     */ import java.awt.event.InvocationEvent;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.awt.font.TextHitInfo;
/*     */ import java.awt.im.InputMethodHighlight;
/*     */ import java.awt.im.InputSubset;
/*     */ import java.awt.im.spi.InputMethodContext;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.awt.peer.LightweightPeer;
/*     */ import java.text.Annotation;
/*     */ import java.text.AttributedCharacterIterator;
/*     */ import java.text.AttributedCharacterIterator.Attribute;
/*     */ import java.text.AttributedString;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import sun.awt.im.InputMethodAdapter;
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
/*     */ final class WInputMethod
/*     */   extends InputMethodAdapter
/*     */ {
/*     */   private InputMethodContext inputContext;
/*     */   private Component awtFocussedComponent;
/*  55 */   private WComponentPeer awtFocussedComponentPeer = null;
/*  56 */   private WComponentPeer lastFocussedComponentPeer = null;
/*  57 */   private boolean isLastFocussedActiveClient = false;
/*     */   
/*     */   private boolean isActive;
/*     */   private int context;
/*     */   private boolean open;
/*     */   private int cmode;
/*     */   private Locale currentLocale;
/*  64 */   private boolean statusWindowHidden = false;
/*     */   
/*     */   public static final byte ATTR_INPUT = 0;
/*     */   
/*     */   public static final byte ATTR_TARGET_CONVERTED = 1;
/*     */   
/*     */   public static final byte ATTR_CONVERTED = 2;
/*     */   
/*     */   public static final byte ATTR_TARGET_NOTCONVERTED = 3;
/*     */   
/*     */   public static final byte ATTR_INPUT_ERROR = 4;
/*     */   
/*     */   public static final int IME_CMODE_ALPHANUMERIC = 0;
/*     */   public static final int IME_CMODE_NATIVE = 1;
/*     */   public static final int IME_CMODE_KATAKANA = 2;
/*     */   public static final int IME_CMODE_LANGUAGE = 3;
/*     */   public static final int IME_CMODE_FULLSHAPE = 8;
/*     */   public static final int IME_CMODE_HANJACONVERT = 64;
/*     */   public static final int IME_CMODE_ROMAN = 16;
/*     */   private static final boolean COMMIT_INPUT = true;
/*     */   private static final boolean DISCARD_INPUT = false;
/*     */   private static Map<TextAttribute, Object>[] highlightStyles;
/*     */   
/*     */   static
/*     */   {
/*  89 */     Map[] arrayOfMap = new Map[4];
/*     */     
/*     */ 
/*     */ 
/*  93 */     HashMap localHashMap = new HashMap(1);
/*  94 */     localHashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
/*  95 */     arrayOfMap[0] = Collections.unmodifiableMap(localHashMap);
/*     */     
/*     */ 
/*  98 */     localHashMap = new HashMap(1);
/*  99 */     localHashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
/* 100 */     arrayOfMap[1] = Collections.unmodifiableMap(localHashMap);
/*     */     
/*     */ 
/* 103 */     localHashMap = new HashMap(1);
/* 104 */     localHashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
/* 105 */     arrayOfMap[2] = Collections.unmodifiableMap(localHashMap);
/*     */     
/*     */ 
/* 108 */     localHashMap = new HashMap(4);
/* 109 */     Color localColor = new Color(0, 0, 128);
/* 110 */     localHashMap.put(TextAttribute.FOREGROUND, localColor);
/* 111 */     localHashMap.put(TextAttribute.BACKGROUND, Color.white);
/* 112 */     localHashMap.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
/* 113 */     localHashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
/* 114 */     arrayOfMap[3] = Collections.unmodifiableMap(localHashMap);
/*     */     
/* 116 */     highlightStyles = arrayOfMap;
/*     */   }
/*     */   
/*     */   public WInputMethod()
/*     */   {
/* 121 */     this.context = createNativeContext();
/* 122 */     this.cmode = getConversionStatus(this.context);
/* 123 */     this.open = getOpenStatus(this.context);
/* 124 */     this.currentLocale = getNativeLocale();
/* 125 */     if (this.currentLocale == null) {
/* 126 */       this.currentLocale = Locale.getDefault();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/* 134 */     if (this.context != 0) {
/* 135 */       destroyNativeContext(this.context);
/* 136 */       this.context = 0;
/*     */     }
/* 138 */     super.finalize();
/*     */   }
/*     */   
/*     */   public synchronized void setInputMethodContext(InputMethodContext paramInputMethodContext)
/*     */   {
/* 143 */     this.inputContext = paramInputMethodContext;
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
/*     */   public Object getControlObject()
/*     */   {
/* 160 */     return null;
/*     */   }
/*     */   
/*     */   public boolean setLocale(Locale paramLocale)
/*     */   {
/* 165 */     return setLocale(paramLocale, false);
/*     */   }
/*     */   
/*     */   private boolean setLocale(Locale paramLocale, boolean paramBoolean) {
/* 169 */     Locale[] arrayOfLocale = WInputMethodDescriptor.getAvailableLocalesInternal();
/* 170 */     for (int i = 0; i < arrayOfLocale.length; i++) {
/* 171 */       Locale localLocale = arrayOfLocale[i];
/* 172 */       if ((paramLocale.equals(localLocale)) || 
/*     */       
/* 174 */         ((localLocale.equals(Locale.JAPAN)) && (paramLocale.equals(Locale.JAPANESE))) || (
/* 175 */         (localLocale.equals(Locale.KOREA)) && (paramLocale.equals(Locale.KOREAN)))) {
/* 176 */         if (this.isActive) {
/* 177 */           setNativeLocale(localLocale.toLanguageTag(), paramBoolean);
/*     */         }
/* 179 */         this.currentLocale = localLocale;
/* 180 */         return true;
/*     */       }
/*     */     }
/* 183 */     return false;
/*     */   }
/*     */   
/*     */   public Locale getLocale()
/*     */   {
/* 188 */     if (this.isActive) {
/* 189 */       this.currentLocale = getNativeLocale();
/* 190 */       if (this.currentLocale == null) {
/* 191 */         this.currentLocale = Locale.getDefault();
/*     */       }
/*     */     }
/* 194 */     return this.currentLocale;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCharacterSubsets(Character.Subset[] paramArrayOfSubset)
/*     */   {
/* 204 */     if (paramArrayOfSubset == null) {
/* 205 */       setConversionStatus(this.context, this.cmode);
/* 206 */       setOpenStatus(this.context, this.open);
/* 207 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 212 */     Character.Subset localSubset = paramArrayOfSubset[0];
/*     */     
/* 214 */     Locale localLocale = getNativeLocale();
/*     */     
/*     */ 
/* 217 */     if (localLocale == null) {
/*     */       return;
/*     */     }
/*     */     int i;
/* 221 */     if (localLocale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
/* 222 */       if ((localSubset == Character.UnicodeBlock.BASIC_LATIN) || (localSubset == InputSubset.LATIN_DIGITS)) {
/* 223 */         setOpenStatus(this.context, false);
/*     */       } else {
/* 225 */         if ((localSubset == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || (localSubset == InputSubset.KANJI) || (localSubset == Character.UnicodeBlock.HIRAGANA))
/*     */         {
/*     */ 
/* 228 */           i = 9;
/* 229 */         } else if (localSubset == Character.UnicodeBlock.KATAKANA) {
/* 230 */           i = 11;
/* 231 */         } else if (localSubset == InputSubset.HALFWIDTH_KATAKANA) {
/* 232 */           i = 3;
/* 233 */         } else if (localSubset == InputSubset.FULLWIDTH_LATIN) {
/* 234 */           i = 8;
/*     */         } else
/* 236 */           return;
/* 237 */         setOpenStatus(this.context, true);
/* 238 */         i |= getConversionStatus(this.context) & 0x10;
/* 239 */         setConversionStatus(this.context, i);
/*     */       }
/* 241 */     } else if (localLocale.getLanguage().equals(Locale.KOREAN.getLanguage())) {
/* 242 */       if ((localSubset == Character.UnicodeBlock.BASIC_LATIN) || (localSubset == InputSubset.LATIN_DIGITS)) {
/* 243 */         setOpenStatus(this.context, false);
/*     */       } else {
/* 245 */         if ((localSubset == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || (localSubset == InputSubset.HANJA) || (localSubset == Character.UnicodeBlock.HANGUL_SYLLABLES) || (localSubset == Character.UnicodeBlock.HANGUL_JAMO) || (localSubset == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 250 */           i = 1;
/* 251 */         } else if (localSubset == InputSubset.FULLWIDTH_LATIN) {
/* 252 */           i = 8;
/*     */         } else
/* 254 */           return;
/* 255 */         setOpenStatus(this.context, true);
/* 256 */         setConversionStatus(this.context, i);
/*     */       }
/* 258 */     } else if (localLocale.getLanguage().equals(Locale.CHINESE.getLanguage())) {
/* 259 */       if ((localSubset == Character.UnicodeBlock.BASIC_LATIN) || (localSubset == InputSubset.LATIN_DIGITS)) {
/* 260 */         setOpenStatus(this.context, false);
/*     */       } else {
/* 262 */         if ((localSubset == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || (localSubset == InputSubset.TRADITIONAL_HANZI) || (localSubset == InputSubset.SIMPLIFIED_HANZI))
/*     */         {
/*     */ 
/* 265 */           i = 1;
/* 266 */         } else if (localSubset == InputSubset.FULLWIDTH_LATIN) {
/* 267 */           i = 8;
/*     */         } else
/* 269 */           return;
/* 270 */         setOpenStatus(this.context, true);
/* 271 */         setConversionStatus(this.context, i);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispatchEvent(AWTEvent paramAWTEvent)
/*     */   {
/* 278 */     if ((paramAWTEvent instanceof ComponentEvent)) {
/* 279 */       Component localComponent = ((ComponentEvent)paramAWTEvent).getComponent();
/* 280 */       if (localComponent == this.awtFocussedComponent) {
/* 281 */         if ((this.awtFocussedComponentPeer == null) || 
/* 282 */           (this.awtFocussedComponentPeer.isDisposed())) {
/* 283 */           this.awtFocussedComponentPeer = getNearestNativePeer(localComponent);
/*     */         }
/* 285 */         if (this.awtFocussedComponentPeer != null) {
/* 286 */           handleNativeIMEEvent(this.awtFocussedComponentPeer, paramAWTEvent);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void activate()
/*     */   {
/* 294 */     boolean bool = haveActiveClient();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 300 */     if ((this.lastFocussedComponentPeer != this.awtFocussedComponentPeer) || (this.isLastFocussedActiveClient != bool))
/*     */     {
/* 302 */       if (this.lastFocussedComponentPeer != null) {
/* 303 */         disableNativeIME(this.lastFocussedComponentPeer);
/*     */       }
/* 305 */       if (this.awtFocussedComponentPeer != null) {
/* 306 */         enableNativeIME(this.awtFocussedComponentPeer, this.context, !bool);
/*     */       }
/* 308 */       this.lastFocussedComponentPeer = this.awtFocussedComponentPeer;
/* 309 */       this.isLastFocussedActiveClient = bool;
/*     */     }
/* 311 */     this.isActive = true;
/* 312 */     if (this.currentLocale != null) {
/* 313 */       setLocale(this.currentLocale, true);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 322 */     if (this.statusWindowHidden) {
/* 323 */       setStatusWindowVisible(this.awtFocussedComponentPeer, true);
/* 324 */       this.statusWindowHidden = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deactivate(boolean paramBoolean)
/*     */   {
/* 334 */     getLocale();
/*     */     
/*     */ 
/*     */ 
/* 338 */     if (this.awtFocussedComponentPeer != null) {
/* 339 */       this.lastFocussedComponentPeer = this.awtFocussedComponentPeer;
/* 340 */       this.isLastFocussedActiveClient = haveActiveClient();
/*     */     }
/* 342 */     this.isActive = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void disableInputMethod()
/*     */   {
/* 351 */     if (this.lastFocussedComponentPeer != null) {
/* 352 */       disableNativeIME(this.lastFocussedComponentPeer);
/* 353 */       this.lastFocussedComponentPeer = null;
/* 354 */       this.isLastFocussedActiveClient = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getNativeInputMethodInfo()
/*     */   {
/* 364 */     return getNativeIMMDescription();
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
/*     */   protected void stopListening()
/*     */   {
/* 378 */     disableInputMethod();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setAWTFocussedComponent(Component paramComponent)
/*     */   {
/* 384 */     if (paramComponent == null) {
/* 385 */       return;
/*     */     }
/* 387 */     WComponentPeer localWComponentPeer = getNearestNativePeer(paramComponent);
/* 388 */     if (this.isActive)
/*     */     {
/*     */ 
/* 391 */       if (this.awtFocussedComponentPeer != null) {
/* 392 */         disableNativeIME(this.awtFocussedComponentPeer);
/*     */       }
/* 394 */       if (localWComponentPeer != null) {
/* 395 */         enableNativeIME(localWComponentPeer, this.context, !haveActiveClient());
/*     */       }
/*     */     }
/* 398 */     this.awtFocussedComponent = paramComponent;
/* 399 */     this.awtFocussedComponentPeer = localWComponentPeer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void hideWindows()
/*     */   {
/* 405 */     if (this.awtFocussedComponentPeer != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 411 */       setStatusWindowVisible(this.awtFocussedComponentPeer, false);
/* 412 */       this.statusWindowHidden = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeNotify()
/*     */   {
/* 421 */     endCompositionNative(this.context, false);
/* 422 */     this.awtFocussedComponent = null;
/* 423 */     this.awtFocussedComponentPeer = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight)
/*     */   {
/* 431 */     int j = paramInputMethodHighlight.getState();
/* 432 */     int i; if (j == 0) {
/* 433 */       i = 0;
/* 434 */     } else if (j == 1) {
/* 435 */       i = 2;
/*     */     } else {
/* 437 */       return null;
/*     */     }
/* 439 */     if (paramInputMethodHighlight.isSelected()) {
/* 440 */       i++;
/*     */     }
/* 442 */     return highlightStyles[i];
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean supportsBelowTheSpot()
/*     */   {
/* 448 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void endComposition()
/*     */   {
/* 456 */     endCompositionNative(this.context, 
/* 457 */       haveActiveClient());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCompositionEnabled(boolean paramBoolean)
/*     */   {
/* 465 */     setOpenStatus(this.context, paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isCompositionEnabled()
/*     */   {
/* 473 */     return getOpenStatus(this.context);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendInputMethodEvent(int paramInt1, long paramLong, String paramString, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 482 */     AttributedCharacterIterator localAttributedCharacterIterator = null;
/*     */     
/* 484 */     if (paramString != null)
/*     */     {
/*     */ 
/* 487 */       localObject = new AttributedString(paramString);
/*     */       
/*     */ 
/* 490 */       ((AttributedString)localObject).addAttribute(Attribute.LANGUAGE,
/* 491 */         Locale.getDefault(), 0, paramString.length());
/*     */       
/*     */       int i;
/* 494 */       if ((paramArrayOfInt1 != null) && (paramArrayOfString != null) && (paramArrayOfString.length != 0) && (paramArrayOfInt1.length == paramArrayOfString.length + 1) && (paramArrayOfInt1[0] == 0))
/*     */       {
/* 496 */         if (paramArrayOfInt1[paramArrayOfString.length] == paramString.length())
/*     */         {
/* 498 */           for (i = 0; i < paramArrayOfInt1.length - 1; i++) {
/* 499 */             ((AttributedString)localObject).addAttribute(Attribute.INPUT_METHOD_SEGMENT, new Annotation(null), paramArrayOfInt1[i], paramArrayOfInt1[(i + 1)]);
/*     */             
/* 501 */             ((AttributedString)localObject).addAttribute(Attribute.READING, new Annotation(paramArrayOfString[i]), paramArrayOfInt1[i], paramArrayOfInt1[(i + 1)]);
/*     */           }
/*     */           
/*     */           break label211;
/*     */         }
/*     */       }
/*     */       
/* 508 */       ((AttributedString)localObject).addAttribute(Attribute.INPUT_METHOD_SEGMENT, new Annotation(null), 0, paramString
/* 509 */         .length());
/* 510 */       ((AttributedString)localObject).addAttribute(Attribute.READING, new Annotation(""), 0, paramString
/* 511 */         .length());
/*     */       
/*     */       label211:
/*     */       
/* 515 */       if ((paramArrayOfInt2 != null) && (paramArrayOfByte != null) && (paramArrayOfByte.length != 0) && (paramArrayOfInt2.length == paramArrayOfByte.length + 1) && (paramArrayOfInt2[0] == 0))
/*     */       {
/* 517 */         if (paramArrayOfInt2[paramArrayOfByte.length] == paramString.length())
/*     */         {
/* 519 */           for (i = 0; i < paramArrayOfInt2.length - 1; i++) {
/*     */             InputMethodHighlight localInputMethodHighlight;
/* 521 */             switch (paramArrayOfByte[i]) {
/*     */             case 1: 
/* 523 */               localInputMethodHighlight = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
/* 524 */               break;
/*     */             case 2: 
/* 526 */               localInputMethodHighlight = InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
/* 527 */               break;
/*     */             case 3: 
/* 529 */               localInputMethodHighlight = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
/* 530 */               break;
/*     */             case 0: 
/*     */             case 4: 
/*     */             default: 
/* 534 */               localInputMethodHighlight = InputMethodHighlight.UNSELECTED_RAW_TEXT_HIGHLIGHT;
/*     */             }
/*     */             
/* 537 */             ((AttributedString)localObject).addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, localInputMethodHighlight, paramArrayOfInt2[i], paramArrayOfInt2[(i + 1)]);
/*     */           }
/*     */           
/*     */ 
/*     */           break label389;
/*     */         }
/*     */       }
/*     */       
/* 545 */       ((AttributedString)localObject).addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT, 0, paramString
/*     */       
/* 547 */         .length());
/*     */       
/*     */       label389:
/*     */       
/* 551 */       localAttributedCharacterIterator = ((AttributedString)localObject).getIterator();
/*     */     }
/*     */     
/*     */ 
/* 555 */     Object localObject = getClientComponent();
/* 556 */     if (localObject == null) {
/* 557 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 565 */     InputMethodEvent localInputMethodEvent = new InputMethodEvent((Component)localObject, paramInt1, paramLong, localAttributedCharacterIterator, paramInt2, TextHitInfo.leading(paramInt3), TextHitInfo.leading(paramInt4));
/* 566 */     WToolkit.postEvent(WToolkit.targetToAppContext(localObject), localInputMethodEvent);
/*     */   }
/*     */   
/*     */   public void inquireCandidatePosition()
/*     */   {
/* 571 */     Component localComponent = getClientComponent();
/* 572 */     if (localComponent == null) {
/* 573 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 579 */     Runnable local1 = new Runnable()
/*     */     {
/*     */       public void run() {
/* 582 */         int i = 0;
/* 583 */         int j = 0;
/* 584 */         Component localComponent = WInputMethod.this.getClientComponent();
/*     */         
/* 586 */         if (localComponent != null) {
/* 587 */           if (!localComponent.isShowing())
/*     */             return;
/*     */           Object localObject;
/* 590 */           if (WInputMethod.this.haveActiveClient()) {
/* 591 */             localObject = WInputMethod.this.inputContext.getTextLocation(TextHitInfo.leading(0));
/* 592 */             i = ((Rectangle)localObject).x;
/* 593 */             j = ((Rectangle)localObject).y + ((Rectangle)localObject).height;
/*     */           } else {
/* 595 */             localObject = localComponent.getLocationOnScreen();
/* 596 */             Dimension localDimension = localComponent.getSize();
/* 597 */             i = ((Point)localObject).x;
/* 598 */             j = ((Point)localObject).y + localDimension.height;
/*     */           }
/*     */         }
/*     */         
/* 602 */         WInputMethod.this.openCandidateWindow(WInputMethod.this.awtFocussedComponentPeer, i, j);
/*     */       }
/* 604 */     };
/* 605 */     WToolkit.postEvent(WToolkit.targetToAppContext(localComponent), new InvocationEvent(localComponent, local1));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private WComponentPeer getNearestNativePeer(Component paramComponent)
/*     */   {
/* 613 */     if (paramComponent == null) { return null;
/*     */     }
/* 615 */     ComponentPeer localComponentPeer = paramComponent.getPeer();
/* 616 */     if (localComponentPeer == null) { return null;
/*     */     }
/* 618 */     while ((localComponentPeer instanceof LightweightPeer)) {
/* 619 */       paramComponent = paramComponent.getParent();
/* 620 */       if (paramComponent == null) return null;
/* 621 */       localComponentPeer = paramComponent.getPeer();
/* 622 */       if (localComponentPeer == null) { return null;
/*     */       }
/*     */     }
/* 625 */     if ((localComponentPeer instanceof WComponentPeer)) {
/* 626 */       return (WComponentPeer)localComponentPeer;
/*     */     }
/* 628 */     return null;
/*     */   }
/*     */   
/*     */   public final void dispose() {}
/*     */   
/*     */   private native int createNativeContext();
/*     */   
/*     */   private native void destroyNativeContext(int paramInt);
/*     */   
/*     */   private native void enableNativeIME(WComponentPeer paramWComponentPeer, int paramInt, boolean paramBoolean);
/*     */   
/*     */   private native void disableNativeIME(WComponentPeer paramWComponentPeer);
/*     */   
/*     */   private native void handleNativeIMEEvent(WComponentPeer paramWComponentPeer, AWTEvent paramAWTEvent);
/*     */   
/*     */   private native void endCompositionNative(int paramInt, boolean paramBoolean);
/*     */   
/*     */   private native void setConversionStatus(int paramInt1, int paramInt2);
/*     */   
/*     */   private native int getConversionStatus(int paramInt);
/*     */   
/*     */   private native void setOpenStatus(int paramInt, boolean paramBoolean);
/*     */   
/*     */   private native boolean getOpenStatus(int paramInt);
/*     */   
/*     */   private native void setStatusWindowVisible(WComponentPeer paramWComponentPeer, boolean paramBoolean);
/*     */   
/*     */   private native String getNativeIMMDescription();
/*     */   
/*     */   static native Locale getNativeLocale();
/*     */   
/*     */   static native boolean setNativeLocale(String paramString, boolean paramBoolean);
/*     */   
/*     */   private native void openCandidateWindow(WComponentPeer paramWComponentPeer, int paramInt1, int paramInt2);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WInputMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
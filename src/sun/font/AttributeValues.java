/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.Paint;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.font.GraphicAttribute;
/*     */ import java.awt.font.NumericShaper;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.awt.font.TransformAttribute;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.NoninvertibleTransformException;
/*     */ import java.awt.geom.Point2D.Double;
/*     */ import java.awt.im.InputMethodHighlight;
/*     */ import java.io.Serializable;
/*     */ import java.text.Annotation;
/*     */ import java.text.AttributedCharacterIterator.Attribute;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
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
/*     */ public final class AttributeValues
/*     */   implements Cloneable
/*     */ {
/*     */   private int defined;
/*     */   private int nondefault;
/*  64 */   private String family = "Default";
/*  65 */   private float weight = 1.0F;
/*  66 */   private float width = 1.0F;
/*     */   private float posture;
/*  68 */   private float size = 12.0F;
/*     */   private float tracking;
/*     */   private NumericShaper numericShaping;
/*     */   private AffineTransform transform;
/*     */   private GraphicAttribute charReplacement;
/*     */   private Paint foreground;
/*     */   private Paint background;
/*  75 */   private float justification = 1.0F;
/*     */   
/*     */   private Object imHighlight;
/*     */   private Font font;
/*  79 */   private byte imUnderline = -1;
/*     */   private byte superscript;
/*  81 */   private byte underline = -1;
/*  82 */   private byte runDirection = -2;
/*     */   
/*     */   private byte bidiEmbedding;
/*     */   
/*     */   private byte kerning;
/*     */   private byte ligatures;
/*     */   private boolean strikethrough;
/*     */   private boolean swapColors;
/*     */   private AffineTransform baselineTransform;
/*     */   private AffineTransform charTransform;
/*  92 */   private static final AttributeValues DEFAULT = new AttributeValues();
/*     */   
/*     */ 
/*  95 */   public String getFamily() { return this.family; }
/*  96 */   public void setFamily(String paramString) { this.family = paramString;update(EAttribute.EFAMILY); }
/*     */   
/*  98 */   public float getWeight() { return this.weight; }
/*  99 */   public void setWeight(float paramFloat) { this.weight = paramFloat;update(EAttribute.EWEIGHT); }
/*     */   
/* 101 */   public float getWidth() { return this.width; }
/* 102 */   public void setWidth(float paramFloat) { this.width = paramFloat;update(EAttribute.EWIDTH); }
/*     */   
/* 104 */   public float getPosture() { return this.posture; }
/* 105 */   public void setPosture(float paramFloat) { this.posture = paramFloat;update(EAttribute.EPOSTURE); }
/*     */   
/* 107 */   public float getSize() { return this.size; }
/* 108 */   public void setSize(float paramFloat) { this.size = paramFloat;update(EAttribute.ESIZE); }
/*     */   
/* 110 */   public AffineTransform getTransform() { return this.transform; }
/*     */   
/* 112 */   public void setTransform(AffineTransform paramAffineTransform) { this.transform = ((paramAffineTransform == null) || (paramAffineTransform.isIdentity()) ? DEFAULT.transform : new AffineTransform(paramAffineTransform));
/*     */     
/*     */ 
/* 115 */     updateDerivedTransforms();
/* 116 */     update(EAttribute.ETRANSFORM);
/*     */   }
/*     */   
/*     */   public void setTransform(TransformAttribute paramTransformAttribute)
/*     */   {
/* 121 */     this.transform = ((paramTransformAttribute == null) || (paramTransformAttribute.isIdentity()) ? DEFAULT.transform : paramTransformAttribute.getTransform());
/* 122 */     updateDerivedTransforms();
/* 123 */     update(EAttribute.ETRANSFORM);
/*     */   }
/*     */   
/* 126 */   public int getSuperscript() { return this.superscript; }
/*     */   
/* 128 */   public void setSuperscript(int paramInt) { this.superscript = ((byte)paramInt);update(EAttribute.ESUPERSCRIPT); }
/*     */   
/* 130 */   public Font getFont() { return this.font; }
/* 131 */   public void setFont(Font paramFont) { this.font = paramFont;update(EAttribute.EFONT); }
/*     */   
/* 133 */   public GraphicAttribute getCharReplacement() { return this.charReplacement; }
/*     */   
/* 135 */   public void setCharReplacement(GraphicAttribute paramGraphicAttribute) { this.charReplacement = paramGraphicAttribute;update(EAttribute.ECHAR_REPLACEMENT); }
/*     */   
/* 137 */   public Paint getForeground() { return this.foreground; }
/*     */   
/* 139 */   public void setForeground(Paint paramPaint) { this.foreground = paramPaint;update(EAttribute.EFOREGROUND); }
/*     */   
/* 141 */   public Paint getBackground() { return this.background; }
/*     */   
/* 143 */   public void setBackground(Paint paramPaint) { this.background = paramPaint;update(EAttribute.EBACKGROUND); }
/*     */   
/* 145 */   public int getUnderline() { return this.underline; }
/*     */   
/* 147 */   public void setUnderline(int paramInt) { this.underline = ((byte)paramInt);update(EAttribute.EUNDERLINE); }
/*     */   
/* 149 */   public boolean getStrikethrough() { return this.strikethrough; }
/*     */   
/* 151 */   public void setStrikethrough(boolean paramBoolean) { this.strikethrough = paramBoolean;update(EAttribute.ESTRIKETHROUGH); }
/*     */   
/* 153 */   public int getRunDirection() { return this.runDirection; }
/*     */   
/* 155 */   public void setRunDirection(int paramInt) { this.runDirection = ((byte)paramInt);update(EAttribute.ERUN_DIRECTION); }
/*     */   
/* 157 */   public int getBidiEmbedding() { return this.bidiEmbedding; }
/*     */   
/* 159 */   public void setBidiEmbedding(int paramInt) { this.bidiEmbedding = ((byte)paramInt);update(EAttribute.EBIDI_EMBEDDING); }
/*     */   
/* 161 */   public float getJustification() { return this.justification; }
/*     */   
/* 163 */   public void setJustification(float paramFloat) { this.justification = paramFloat;update(EAttribute.EJUSTIFICATION); }
/*     */   
/* 165 */   public Object getInputMethodHighlight() { return this.imHighlight; }
/*     */   
/* 167 */   public void setInputMethodHighlight(Annotation paramAnnotation) { this.imHighlight = paramAnnotation;update(EAttribute.EINPUT_METHOD_HIGHLIGHT); }
/*     */   
/* 169 */   public void setInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) { this.imHighlight = paramInputMethodHighlight;update(EAttribute.EINPUT_METHOD_HIGHLIGHT); }
/*     */   
/* 171 */   public int getInputMethodUnderline() { return this.imUnderline; }
/*     */   
/* 173 */   public void setInputMethodUnderline(int paramInt) { this.imUnderline = ((byte)paramInt);update(EAttribute.EINPUT_METHOD_UNDERLINE); }
/*     */   
/* 175 */   public boolean getSwapColors() { return this.swapColors; }
/*     */   
/* 177 */   public void setSwapColors(boolean paramBoolean) { this.swapColors = paramBoolean;update(EAttribute.ESWAP_COLORS); }
/*     */   
/* 179 */   public NumericShaper getNumericShaping() { return this.numericShaping; }
/*     */   
/* 181 */   public void setNumericShaping(NumericShaper paramNumericShaper) { this.numericShaping = paramNumericShaper;update(EAttribute.ENUMERIC_SHAPING); }
/*     */   
/* 183 */   public int getKerning() { return this.kerning; }
/*     */   
/* 185 */   public void setKerning(int paramInt) { this.kerning = ((byte)paramInt);update(EAttribute.EKERNING); }
/*     */   
/* 187 */   public float getTracking() { return this.tracking; }
/*     */   
/* 189 */   public void setTracking(float paramFloat) { this.tracking = ((byte)(int)paramFloat);update(EAttribute.ETRACKING); }
/*     */   
/* 191 */   public int getLigatures() { return this.ligatures; }
/*     */   
/* 193 */   public void setLigatures(int paramInt) { this.ligatures = ((byte)paramInt);update(EAttribute.ELIGATURES);
/*     */   }
/*     */   
/* 196 */   public AffineTransform getBaselineTransform() { return this.baselineTransform; }
/* 197 */   public AffineTransform getCharTransform() { return this.charTransform; }
/*     */   
/*     */ 
/*     */   public static int getMask(EAttribute paramEAttribute)
/*     */   {
/* 202 */     return paramEAttribute.mask;
/*     */   }
/*     */   
/*     */   public static int getMask(EAttribute... paramVarArgs) {
/* 206 */     int i = 0;
/* 207 */     for (EAttribute localEAttribute : paramVarArgs) {
/* 208 */       i |= localEAttribute.mask;
/*     */     }
/* 210 */     return i;
/*     */   }
/*     */   
/*     */ 
/* 214 */   public static final int MASK_ALL = getMask((EAttribute[])EAttribute.class.getEnumConstants());
/*     */   private static final String DEFINED_KEY = "sun.font.attributevalues.defined_key";
/*     */   
/* 217 */   public void unsetDefault() { this.defined &= this.nondefault; }
/*     */   
/*     */   public void defineAll(int paramInt)
/*     */   {
/* 221 */     this.defined |= paramInt;
/* 222 */     if ((this.defined & EAttribute.EBASELINE_TRANSFORM.mask) != 0) {
/* 223 */       throw new InternalError("can't define derived attribute");
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean allDefined(int paramInt) {
/* 228 */     return (this.defined & paramInt) == paramInt;
/*     */   }
/*     */   
/*     */   public boolean anyDefined(int paramInt) {
/* 232 */     return (this.defined & paramInt) != 0;
/*     */   }
/*     */   
/*     */   public boolean anyNonDefault(int paramInt) {
/* 236 */     return (this.nondefault & paramInt) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDefined(EAttribute paramEAttribute)
/*     */   {
/* 242 */     return (this.defined & paramEAttribute.mask) != 0;
/*     */   }
/*     */   
/*     */   public boolean isNonDefault(EAttribute paramEAttribute) {
/* 246 */     return (this.nondefault & paramEAttribute.mask) != 0;
/*     */   }
/*     */   
/*     */   public void setDefault(EAttribute paramEAttribute) {
/* 250 */     if (paramEAttribute.att == null) {
/* 251 */       throw new InternalError("can't set default derived attribute: " + paramEAttribute);
/*     */     }
/* 253 */     i_set(paramEAttribute, DEFAULT);
/* 254 */     this.defined |= paramEAttribute.mask;
/* 255 */     this.nondefault &= (paramEAttribute.mask ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */   public void unset(EAttribute paramEAttribute) {
/* 259 */     if (paramEAttribute.att == null) {
/* 260 */       throw new InternalError("can't unset derived attribute: " + paramEAttribute);
/*     */     }
/* 262 */     i_set(paramEAttribute, DEFAULT);
/* 263 */     this.defined &= (paramEAttribute.mask ^ 0xFFFFFFFF);
/* 264 */     this.nondefault &= (paramEAttribute.mask ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */   public void set(EAttribute paramEAttribute, AttributeValues paramAttributeValues) {
/* 268 */     if (paramEAttribute.att == null) {
/* 269 */       throw new InternalError("can't set derived attribute: " + paramEAttribute);
/*     */     }
/* 271 */     if ((paramAttributeValues == null) || (paramAttributeValues == DEFAULT)) {
/* 272 */       setDefault(paramEAttribute);
/*     */     }
/* 274 */     else if ((paramAttributeValues.defined & paramEAttribute.mask) != 0) {
/* 275 */       i_set(paramEAttribute, paramAttributeValues);
/* 276 */       update(paramEAttribute);
/*     */     }
/*     */   }
/*     */   
/*     */   public void set(EAttribute paramEAttribute, Object paramObject)
/*     */   {
/* 282 */     if (paramEAttribute.att == null) {
/* 283 */       throw new InternalError("can't set derived attribute: " + paramEAttribute);
/*     */     }
/* 285 */     if (paramObject != null) {
/*     */       try {
/* 287 */         i_set(paramEAttribute, paramObject);
/* 288 */         update(paramEAttribute);
/* 289 */         return;
/*     */       }
/*     */       catch (Exception localException) {}
/*     */     }
/* 293 */     setDefault(paramEAttribute);
/*     */   }
/*     */   
/*     */   public Object get(EAttribute paramEAttribute) {
/* 297 */     if (paramEAttribute.att == null) {
/* 298 */       throw new InternalError("can't get derived attribute: " + paramEAttribute);
/*     */     }
/* 300 */     if ((this.nondefault & paramEAttribute.mask) != 0) {
/* 301 */       return i_get(paramEAttribute);
/*     */     }
/* 303 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public AttributeValues merge(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
/*     */   {
/* 309 */     return merge(paramMap, MASK_ALL);
/*     */   }
/*     */   
/*     */   public AttributeValues merge(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt)
/*     */   {
/* 314 */     if (((paramMap instanceof AttributeMap)) && 
/* 315 */       (((AttributeMap)paramMap).getValues() != null)) {
/* 316 */       merge(((AttributeMap)paramMap).getValues(), paramInt);
/* 317 */     } else if ((paramMap != null) && (!paramMap.isEmpty())) {
/* 318 */       for (Entry localEntry : paramMap.entrySet()) {
/*     */         try {
/* 320 */           EAttribute localEAttribute = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)localEntry.getKey());
/* 321 */           if ((localEAttribute != null) && ((paramInt & localEAttribute.mask) != 0)) {
/* 322 */             set(localEAttribute, localEntry.getValue());
/*     */           }
/*     */         }
/*     */         catch (ClassCastException localClassCastException) {}
/*     */       }
/*     */     }
/*     */     
/* 329 */     return this;
/*     */   }
/*     */   
/*     */   public AttributeValues merge(AttributeValues paramAttributeValues) {
/* 333 */     return merge(paramAttributeValues, MASK_ALL);
/*     */   }
/*     */   
/*     */   public AttributeValues merge(AttributeValues paramAttributeValues, int paramInt) {
/* 337 */     int i = paramInt & paramAttributeValues.defined;
/* 338 */     for (EAttribute localEAttribute : EAttribute.atts) {
/* 339 */       if (i == 0) {
/*     */         break;
/*     */       }
/* 342 */       if ((i & localEAttribute.mask) != 0) {
/* 343 */         i &= (localEAttribute.mask ^ 0xFFFFFFFF);
/* 344 */         i_set(localEAttribute, paramAttributeValues);
/* 345 */         update(localEAttribute);
/*     */       }
/*     */     }
/* 348 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public static AttributeValues fromMap(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
/*     */   {
/* 354 */     return fromMap(paramMap, MASK_ALL);
/*     */   }
/*     */   
/*     */   public static AttributeValues fromMap(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt)
/*     */   {
/* 359 */     return new AttributeValues().merge(paramMap, paramInt);
/*     */   }
/*     */   
/*     */   public Map<TextAttribute, Object> toMap(Map<TextAttribute, Object> paramMap) {
/* 363 */     if (paramMap == null) {
/* 364 */       paramMap = new HashMap();
/*     */     }
/*     */     
/* 367 */     int i = this.defined; for (int j = 0; i != 0; j++) {
/* 368 */       EAttribute localEAttribute = EAttribute.atts[j];
/* 369 */       if ((i & localEAttribute.mask) != 0) {
/* 370 */         i &= (localEAttribute.mask ^ 0xFFFFFFFF);
/* 371 */         paramMap.put(localEAttribute.att, get(localEAttribute));
/*     */       }
/*     */     }
/*     */     
/* 375 */     return paramMap;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean is16Hashtable(Hashtable<Object, Object> paramHashtable)
/*     */   {
/* 383 */     return paramHashtable.containsKey("sun.font.attributevalues.defined_key");
/*     */   }
/*     */   
/*     */ 
/*     */   public static AttributeValues fromSerializableHashtable(Hashtable<Object, Object> paramHashtable)
/*     */   {
/* 389 */     AttributeValues localAttributeValues = new AttributeValues();
/* 390 */     if ((paramHashtable != null) && (!paramHashtable.isEmpty())) {
/* 391 */       for (Entry localEntry : paramHashtable.entrySet()) {
/* 392 */         Object localObject1 = localEntry.getKey();
/* 393 */         Object localObject2 = localEntry.getValue();
/* 394 */         if (localObject1.equals("sun.font.attributevalues.defined_key")) {
/* 395 */           localAttributeValues.defineAll(((Integer)localObject2).intValue());
/*     */         } else {
/*     */           try
/*     */           {
/* 399 */             EAttribute localEAttribute = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)localObject1);
/* 400 */             if (localEAttribute != null) {
/* 401 */               localAttributeValues.set(localEAttribute, localObject2);
/*     */             }
/*     */           }
/*     */           catch (ClassCastException localClassCastException) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 409 */     return localAttributeValues;
/*     */   }
/*     */   
/*     */   public Hashtable<Object, Object> toSerializableHashtable() {
/* 413 */     Hashtable localHashtable = new Hashtable();
/* 414 */     int i = this.defined;
/* 415 */     int j = this.defined; for (int k = 0; j != 0; k++) {
/* 416 */       EAttribute localEAttribute = EAttribute.atts[k];
/* 417 */       if ((j & localEAttribute.mask) != 0) {
/* 418 */         j &= (localEAttribute.mask ^ 0xFFFFFFFF);
/* 419 */         Object localObject = get(localEAttribute);
/* 420 */         if (localObject != null)
/*     */         {
/* 422 */           if ((localObject instanceof Serializable)) {
/* 423 */             localHashtable.put(localEAttribute.att, localObject);
/*     */           } else
/* 425 */             i &= (localEAttribute.mask ^ 0xFFFFFFFF);
/*     */         }
/*     */       }
/*     */     }
/* 429 */     localHashtable.put("sun.font.attributevalues.defined_key", Integer.valueOf(i));
/*     */     
/* 431 */     return localHashtable;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 436 */     return this.defined << 8 ^ this.nondefault;
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/*     */     try {
/* 441 */       return equals((AttributeValues)paramObject);
/*     */     }
/*     */     catch (ClassCastException localClassCastException) {}
/*     */     
/* 445 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(AttributeValues paramAttributeValues)
/*     */   {
/* 453 */     if (paramAttributeValues == null) return false;
/* 454 */     if (paramAttributeValues == this) { return true;
/*     */     }
/* 456 */     if ((this.defined == paramAttributeValues.defined) && (this.nondefault == paramAttributeValues.nondefault) && (this.underline == paramAttributeValues.underline) && (this.strikethrough == paramAttributeValues.strikethrough) && (this.superscript == paramAttributeValues.superscript) && (this.width == paramAttributeValues.width) && (this.kerning == paramAttributeValues.kerning) && (this.tracking == paramAttributeValues.tracking) && (this.ligatures == paramAttributeValues.ligatures) && (this.runDirection == paramAttributeValues.runDirection) && (this.bidiEmbedding == paramAttributeValues.bidiEmbedding) && (this.swapColors == paramAttributeValues.swapColors))
/*     */     {
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
/* 468 */       if ((equals(this.transform, paramAttributeValues.transform)) && 
/* 469 */         (equals(this.foreground, paramAttributeValues.foreground)) && 
/* 470 */         (equals(this.background, paramAttributeValues.background)) && 
/* 471 */         (equals(this.numericShaping, paramAttributeValues.numericShaping)) && 
/* 472 */         (equals(Float.valueOf(this.justification), Float.valueOf(paramAttributeValues.justification))) && 
/* 473 */         (equals(this.charReplacement, paramAttributeValues.charReplacement)) && (this.size == paramAttributeValues.size) && (this.weight == paramAttributeValues.weight) && (this.posture == paramAttributeValues.posture))
/*     */       {
/*     */ 
/*     */ 
/* 477 */         if ((!equals(this.family, paramAttributeValues.family)) || 
/* 478 */           (!equals(this.font, paramAttributeValues.font)) || (this.imUnderline != paramAttributeValues.imUnderline)) {}
/*     */       }
/*     */     }
/* 456 */     return 
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
/* 480 */       equals(this.imHighlight, paramAttributeValues.imHighlight);
/*     */   }
/*     */   
/*     */   public AttributeValues clone() {
/*     */     try {
/* 485 */       AttributeValues localAttributeValues = (AttributeValues)super.clone();
/* 486 */       if (this.transform != null) {
/* 487 */         localAttributeValues.transform = new AffineTransform(this.transform);
/* 488 */         localAttributeValues.updateDerivedTransforms();
/*     */       }
/*     */       
/*     */ 
/* 492 */       return localAttributeValues;
/*     */     }
/*     */     catch (CloneNotSupportedException localCloneNotSupportedException) {}
/*     */     
/* 496 */     return null;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 501 */     StringBuilder localStringBuilder = new StringBuilder();
/* 502 */     localStringBuilder.append('{');
/* 503 */     int i = this.defined; for (int j = 0; i != 0; j++) {
/* 504 */       EAttribute localEAttribute = EAttribute.atts[j];
/* 505 */       if ((i & localEAttribute.mask) != 0) {
/* 506 */         i &= (localEAttribute.mask ^ 0xFFFFFFFF);
/* 507 */         if (localStringBuilder.length() > 1) {
/* 508 */           localStringBuilder.append(", ");
/*     */         }
/* 510 */         localStringBuilder.append(localEAttribute);
/* 511 */         localStringBuilder.append('=');
/* 512 */         switch (localEAttribute) {
/* 513 */         case EFAMILY:  localStringBuilder.append('"');
/* 514 */           localStringBuilder.append(this.family);
/* 515 */           localStringBuilder.append('"'); break;
/* 516 */         case EWEIGHT:  localStringBuilder.append(this.weight); break;
/* 517 */         case EWIDTH:  localStringBuilder.append(this.width); break;
/* 518 */         case EPOSTURE:  localStringBuilder.append(this.posture); break;
/* 519 */         case ESIZE:  localStringBuilder.append(this.size); break;
/* 520 */         case ETRANSFORM:  localStringBuilder.append(this.transform); break;
/* 521 */         case ESUPERSCRIPT:  localStringBuilder.append(this.superscript); break;
/* 522 */         case EFONT:  localStringBuilder.append(this.font); break;
/* 523 */         case ECHAR_REPLACEMENT:  localStringBuilder.append(this.charReplacement); break;
/* 524 */         case EFOREGROUND:  localStringBuilder.append(this.foreground); break;
/* 525 */         case EBACKGROUND:  localStringBuilder.append(this.background); break;
/* 526 */         case EUNDERLINE:  localStringBuilder.append(this.underline); break;
/* 527 */         case ESTRIKETHROUGH:  localStringBuilder.append(this.strikethrough); break;
/* 528 */         case ERUN_DIRECTION:  localStringBuilder.append(this.runDirection); break;
/* 529 */         case EBIDI_EMBEDDING:  localStringBuilder.append(this.bidiEmbedding); break;
/* 530 */         case EJUSTIFICATION:  localStringBuilder.append(this.justification); break;
/* 531 */         case EINPUT_METHOD_HIGHLIGHT:  localStringBuilder.append(this.imHighlight); break;
/* 532 */         case EINPUT_METHOD_UNDERLINE:  localStringBuilder.append(this.imUnderline); break;
/* 533 */         case ESWAP_COLORS:  localStringBuilder.append(this.swapColors); break;
/* 534 */         case ENUMERIC_SHAPING:  localStringBuilder.append(this.numericShaping); break;
/* 535 */         case EKERNING:  localStringBuilder.append(this.kerning); break;
/* 536 */         case ELIGATURES:  localStringBuilder.append(this.ligatures); break;
/* 537 */         case ETRACKING:  localStringBuilder.append(this.tracking); break;
/* 538 */         default:  throw new InternalError();
/*     */         }
/* 540 */         if ((this.nondefault & localEAttribute.mask) == 0) {
/* 541 */           localStringBuilder.append('*');
/*     */         }
/*     */       }
/*     */     }
/* 545 */     localStringBuilder.append("[btx=" + this.baselineTransform + ", ctx=" + this.charTransform + "]");
/* 546 */     localStringBuilder.append('}');
/* 547 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   private static boolean equals(Object paramObject1, Object paramObject2)
/*     */   {
/* 553 */     return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
/*     */   }
/*     */   
/*     */   private void update(EAttribute paramEAttribute) {
/* 557 */     this.defined |= paramEAttribute.mask;
/* 558 */     if (i_validate(paramEAttribute)) {
/* 559 */       if (i_equals(paramEAttribute, DEFAULT)) {
/* 560 */         this.nondefault &= (paramEAttribute.mask ^ 0xFFFFFFFF);
/*     */       } else {
/* 562 */         this.nondefault |= paramEAttribute.mask;
/*     */       }
/*     */     } else {
/* 565 */       setDefault(paramEAttribute);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void i_set(EAttribute paramEAttribute, AttributeValues paramAttributeValues)
/*     */   {
/* 572 */     switch (paramEAttribute) {
/* 573 */     case EFAMILY:  this.family = paramAttributeValues.family; break;
/* 574 */     case EWEIGHT:  this.weight = paramAttributeValues.weight; break;
/* 575 */     case EWIDTH:  this.width = paramAttributeValues.width; break;
/* 576 */     case EPOSTURE:  this.posture = paramAttributeValues.posture; break;
/* 577 */     case ESIZE:  this.size = paramAttributeValues.size; break;
/* 578 */     case ETRANSFORM:  this.transform = paramAttributeValues.transform;updateDerivedTransforms(); break;
/* 579 */     case ESUPERSCRIPT:  this.superscript = paramAttributeValues.superscript; break;
/* 580 */     case EFONT:  this.font = paramAttributeValues.font; break;
/* 581 */     case ECHAR_REPLACEMENT:  this.charReplacement = paramAttributeValues.charReplacement; break;
/* 582 */     case EFOREGROUND:  this.foreground = paramAttributeValues.foreground; break;
/* 583 */     case EBACKGROUND:  this.background = paramAttributeValues.background; break;
/* 584 */     case EUNDERLINE:  this.underline = paramAttributeValues.underline; break;
/* 585 */     case ESTRIKETHROUGH:  this.strikethrough = paramAttributeValues.strikethrough; break;
/* 586 */     case ERUN_DIRECTION:  this.runDirection = paramAttributeValues.runDirection; break;
/* 587 */     case EBIDI_EMBEDDING:  this.bidiEmbedding = paramAttributeValues.bidiEmbedding; break;
/* 588 */     case EJUSTIFICATION:  this.justification = paramAttributeValues.justification; break;
/* 589 */     case EINPUT_METHOD_HIGHLIGHT:  this.imHighlight = paramAttributeValues.imHighlight; break;
/* 590 */     case EINPUT_METHOD_UNDERLINE:  this.imUnderline = paramAttributeValues.imUnderline; break;
/* 591 */     case ESWAP_COLORS:  this.swapColors = paramAttributeValues.swapColors; break;
/* 592 */     case ENUMERIC_SHAPING:  this.numericShaping = paramAttributeValues.numericShaping; break;
/* 593 */     case EKERNING:  this.kerning = paramAttributeValues.kerning; break;
/* 594 */     case ELIGATURES:  this.ligatures = paramAttributeValues.ligatures; break;
/* 595 */     case ETRACKING:  this.tracking = paramAttributeValues.tracking; break;
/* 596 */     default:  throw new InternalError();
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean i_equals(EAttribute paramEAttribute, AttributeValues paramAttributeValues) {
/* 601 */     switch (paramEAttribute) {
/* 602 */     case EFAMILY:  return equals(this.family, paramAttributeValues.family);
/* 603 */     case EWEIGHT:  return this.weight == paramAttributeValues.weight;
/* 604 */     case EWIDTH:  return this.width == paramAttributeValues.width;
/* 605 */     case EPOSTURE:  return this.posture == paramAttributeValues.posture;
/* 606 */     case ESIZE:  return this.size == paramAttributeValues.size;
/* 607 */     case ETRANSFORM:  return equals(this.transform, paramAttributeValues.transform);
/* 608 */     case ESUPERSCRIPT:  return this.superscript == paramAttributeValues.superscript;
/* 609 */     case EFONT:  return equals(this.font, paramAttributeValues.font);
/* 610 */     case ECHAR_REPLACEMENT:  return equals(this.charReplacement, paramAttributeValues.charReplacement);
/* 611 */     case EFOREGROUND:  return equals(this.foreground, paramAttributeValues.foreground);
/* 612 */     case EBACKGROUND:  return equals(this.background, paramAttributeValues.background);
/* 613 */     case EUNDERLINE:  return this.underline == paramAttributeValues.underline;
/* 614 */     case ESTRIKETHROUGH:  return this.strikethrough == paramAttributeValues.strikethrough;
/* 615 */     case ERUN_DIRECTION:  return this.runDirection == paramAttributeValues.runDirection;
/* 616 */     case EBIDI_EMBEDDING:  return this.bidiEmbedding == paramAttributeValues.bidiEmbedding;
/* 617 */     case EJUSTIFICATION:  return this.justification == paramAttributeValues.justification;
/* 618 */     case EINPUT_METHOD_HIGHLIGHT:  return equals(this.imHighlight, paramAttributeValues.imHighlight);
/* 619 */     case EINPUT_METHOD_UNDERLINE:  return this.imUnderline == paramAttributeValues.imUnderline;
/* 620 */     case ESWAP_COLORS:  return this.swapColors == paramAttributeValues.swapColors;
/* 621 */     case ENUMERIC_SHAPING:  return equals(this.numericShaping, paramAttributeValues.numericShaping);
/* 622 */     case EKERNING:  return this.kerning == paramAttributeValues.kerning;
/* 623 */     case ELIGATURES:  return this.ligatures == paramAttributeValues.ligatures;
/* 624 */     case ETRACKING:  return this.tracking == paramAttributeValues.tracking; }
/* 625 */     throw new InternalError();
/*     */   }
/*     */   
/*     */   private void i_set(EAttribute paramEAttribute, Object paramObject) {
/*     */     Object localObject;
/* 630 */     switch (paramEAttribute) {
/* 631 */     case EFAMILY:  this.family = ((String)paramObject).trim(); break;
/* 632 */     case EWEIGHT:  this.weight = ((Number)paramObject).floatValue(); break;
/* 633 */     case EWIDTH:  this.width = ((Number)paramObject).floatValue(); break;
/* 634 */     case EPOSTURE:  this.posture = ((Number)paramObject).floatValue(); break;
/* 635 */     case ESIZE:  this.size = ((Number)paramObject).floatValue(); break;
/*     */     case ETRANSFORM: 
/* 637 */       if ((paramObject instanceof TransformAttribute)) {
/* 638 */         localObject = (TransformAttribute)paramObject;
/* 639 */         if (((TransformAttribute)localObject).isIdentity()) {
/* 640 */           this.transform = null;
/*     */         } else {
/* 642 */           this.transform = ((TransformAttribute)localObject).getTransform();
/*     */         }
/*     */       } else {
/* 645 */         this.transform = new AffineTransform((AffineTransform)paramObject);
/*     */       }
/* 647 */       updateDerivedTransforms();
/* 648 */       break;
/* 649 */     case ESUPERSCRIPT:  this.superscript = ((byte)((Integer)paramObject).intValue()); break;
/* 650 */     case EFONT:  this.font = ((Font)paramObject); break;
/* 651 */     case ECHAR_REPLACEMENT:  this.charReplacement = ((GraphicAttribute)paramObject); break;
/* 652 */     case EFOREGROUND:  this.foreground = ((Paint)paramObject); break;
/* 653 */     case EBACKGROUND:  this.background = ((Paint)paramObject); break;
/* 654 */     case EUNDERLINE:  this.underline = ((byte)((Integer)paramObject).intValue()); break;
/* 655 */     case ESTRIKETHROUGH:  this.strikethrough = ((Boolean)paramObject).booleanValue(); break;
/*     */     case ERUN_DIRECTION: 
/* 657 */       if ((paramObject instanceof Boolean)) {
/* 658 */         this.runDirection = ((byte)(TextAttribute.RUN_DIRECTION_LTR.equals(paramObject) ? 0 : 1));
/*     */       } else {
/* 660 */         this.runDirection = ((byte)((Integer)paramObject).intValue());
/*     */       }
/* 662 */       break;
/* 663 */     case EBIDI_EMBEDDING:  this.bidiEmbedding = ((byte)((Integer)paramObject).intValue()); break;
/* 664 */     case EJUSTIFICATION:  this.justification = ((Number)paramObject).floatValue(); break;
/*     */     case EINPUT_METHOD_HIGHLIGHT: 
/* 666 */       if ((paramObject instanceof Annotation)) {
/* 667 */         localObject = (Annotation)paramObject;
/* 668 */         this.imHighlight = ((InputMethodHighlight)((Annotation)localObject).getValue());
/*     */       } else {
/* 670 */         this.imHighlight = ((InputMethodHighlight)paramObject);
/*     */       }
/* 672 */       break;
/* 673 */     case EINPUT_METHOD_UNDERLINE:  this.imUnderline = ((byte)((Integer)paramObject).intValue());
/* 674 */       break;
/* 675 */     case ESWAP_COLORS:  this.swapColors = ((Boolean)paramObject).booleanValue(); break;
/* 676 */     case ENUMERIC_SHAPING:  this.numericShaping = ((NumericShaper)paramObject); break;
/* 677 */     case EKERNING:  this.kerning = ((byte)((Integer)paramObject).intValue()); break;
/* 678 */     case ELIGATURES:  this.ligatures = ((byte)((Integer)paramObject).intValue()); break;
/* 679 */     case ETRACKING:  this.tracking = ((Number)paramObject).floatValue(); break;
/* 680 */     default:  throw new InternalError();
/*     */     }
/*     */   }
/*     */   
/*     */   private Object i_get(EAttribute paramEAttribute) {
/* 685 */     switch (paramEAttribute) {
/* 686 */     case EFAMILY:  return this.family;
/* 687 */     case EWEIGHT:  return Float.valueOf(this.weight);
/* 688 */     case EWIDTH:  return Float.valueOf(this.width);
/* 689 */     case EPOSTURE:  return Float.valueOf(this.posture);
/* 690 */     case ESIZE:  return Float.valueOf(this.size);
/*     */     case ETRANSFORM: 
/* 692 */       return this.transform == null ? TransformAttribute.IDENTITY : new TransformAttribute(this.transform);
/*     */     
/*     */     case ESUPERSCRIPT: 
/* 695 */       return Integer.valueOf(this.superscript);
/* 696 */     case EFONT:  return this.font;
/* 697 */     case ECHAR_REPLACEMENT:  return this.charReplacement;
/* 698 */     case EFOREGROUND:  return this.foreground;
/* 699 */     case EBACKGROUND:  return this.background;
/* 700 */     case EUNDERLINE:  return Integer.valueOf(this.underline);
/* 701 */     case ESTRIKETHROUGH:  return Boolean.valueOf(this.strikethrough);
/*     */     case ERUN_DIRECTION: 
/* 703 */       switch (this.runDirection)
/*     */       {
/*     */       case 0: 
/* 706 */         return TextAttribute.RUN_DIRECTION_LTR;
/* 707 */       case 1:  return TextAttribute.RUN_DIRECTION_RTL; }
/* 708 */       return null;
/*     */     
/*     */     case EBIDI_EMBEDDING: 
/* 711 */       return Integer.valueOf(this.bidiEmbedding);
/* 712 */     case EJUSTIFICATION:  return Float.valueOf(this.justification);
/* 713 */     case EINPUT_METHOD_HIGHLIGHT:  return this.imHighlight;
/* 714 */     case EINPUT_METHOD_UNDERLINE:  return Integer.valueOf(this.imUnderline);
/* 715 */     case ESWAP_COLORS:  return Boolean.valueOf(this.swapColors);
/* 716 */     case ENUMERIC_SHAPING:  return this.numericShaping;
/* 717 */     case EKERNING:  return Integer.valueOf(this.kerning);
/* 718 */     case ELIGATURES:  return Integer.valueOf(this.ligatures);
/* 719 */     case ETRACKING:  return Float.valueOf(this.tracking); }
/* 720 */     throw new InternalError();
/*     */   }
/*     */   
/*     */   private boolean i_validate(EAttribute paramEAttribute)
/*     */   {
/* 725 */     switch (paramEAttribute) {
/* 726 */     case EFAMILY:  if ((this.family == null) || (this.family.length() == 0))
/* 727 */         this.family = DEFAULT.family; return true;
/* 728 */     case EWEIGHT:  return (this.weight > 0.0F) && (this.weight < 10.0F);
/* 729 */     case EWIDTH:  return (this.width >= 0.5F) && (this.width < 10.0F);
/* 730 */     case EPOSTURE:  return (this.posture >= -1.0F) && (this.posture <= 1.0F);
/* 731 */     case ESIZE:  return this.size >= 0.0F;
/* 732 */     case ETRANSFORM:  if ((this.transform != null) && (this.transform.isIdentity()))
/* 733 */         this.transform = DEFAULT.transform; return true;
/* 734 */     case ESUPERSCRIPT:  return (this.superscript >= -7) && (this.superscript <= 7);
/* 735 */     case EFONT:  return true;
/* 736 */     case ECHAR_REPLACEMENT:  return true;
/* 737 */     case EFOREGROUND:  return true;
/* 738 */     case EBACKGROUND:  return true;
/* 739 */     case EUNDERLINE:  return (this.underline >= -1) && (this.underline < 6);
/* 740 */     case ESTRIKETHROUGH:  return true;
/* 741 */     case ERUN_DIRECTION:  return (this.runDirection >= -2) && (this.runDirection <= 1);
/* 742 */     case EBIDI_EMBEDDING:  return (this.bidiEmbedding >= -61) && (this.bidiEmbedding < 62);
/* 743 */     case EJUSTIFICATION:  this.justification = Math.max(0.0F, Math.min(this.justification, 1.0F));
/* 744 */       return true;
/* 745 */     case EINPUT_METHOD_HIGHLIGHT:  return true;
/* 746 */     case EINPUT_METHOD_UNDERLINE:  return (this.imUnderline >= -1) && (this.imUnderline < 6);
/* 747 */     case ESWAP_COLORS:  return true;
/* 748 */     case ENUMERIC_SHAPING:  return true;
/* 749 */     case EKERNING:  return (this.kerning >= 0) && (this.kerning <= 1);
/* 750 */     case ELIGATURES:  return (this.ligatures >= 0) && (this.ligatures <= 1);
/* 751 */     case ETRACKING:  return (this.tracking >= -1.0F) && (this.tracking <= 10.0F); }
/* 752 */     throw new InternalError("unknown attribute: " + paramEAttribute);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static float getJustification(Map<?, ?> paramMap)
/*     */   {
/* 761 */     if (paramMap != null) {
/* 762 */       if (((paramMap instanceof AttributeMap)) && 
/* 763 */         (((AttributeMap)paramMap).getValues() != null)) {
/* 764 */         return ((AttributeMap)paramMap).getValues().justification;
/*     */       }
/* 766 */       Object localObject = paramMap.get(TextAttribute.JUSTIFICATION);
/* 767 */       if ((localObject != null) && ((localObject instanceof Number))) {
/* 768 */         return Math.max(0.0F, Math.min(1.0F, ((Number)localObject).floatValue()));
/*     */       }
/*     */     }
/* 771 */     return DEFAULT.justification;
/*     */   }
/*     */   
/*     */   public static NumericShaper getNumericShaping(Map<?, ?> paramMap) {
/* 775 */     if (paramMap != null) {
/* 776 */       if (((paramMap instanceof AttributeMap)) && 
/* 777 */         (((AttributeMap)paramMap).getValues() != null)) {
/* 778 */         return ((AttributeMap)paramMap).getValues().numericShaping;
/*     */       }
/* 780 */       Object localObject = paramMap.get(TextAttribute.NUMERIC_SHAPING);
/* 781 */       if ((localObject != null) && ((localObject instanceof NumericShaper))) {
/* 782 */         return (NumericShaper)localObject;
/*     */       }
/*     */     }
/* 785 */     return DEFAULT.numericShaping;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AttributeValues applyIMHighlight()
/*     */   {
/* 793 */     if (this.imHighlight != null) {
/* 794 */       InputMethodHighlight localInputMethodHighlight = null;
/* 795 */       if ((this.imHighlight instanceof InputMethodHighlight)) {
/* 796 */         localInputMethodHighlight = (InputMethodHighlight)this.imHighlight;
/*     */       } else {
/* 798 */         localInputMethodHighlight = (InputMethodHighlight)((Annotation)this.imHighlight).getValue();
/*     */       }
/*     */       
/* 801 */       Map localMap = localInputMethodHighlight.getStyle();
/* 802 */       if (localMap == null) {
/* 803 */         Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 804 */         localMap = localToolkit.mapInputMethodHighlight(localInputMethodHighlight);
/*     */       }
/*     */       
/* 807 */       if (localMap != null) {
/* 808 */         return clone().merge(localMap);
/*     */       }
/*     */     }
/*     */     
/* 812 */     return this;
/*     */   }
/*     */   
/*     */   public static AffineTransform getBaselineTransform(Map<?, ?> paramMap) {
/* 816 */     if (paramMap != null) {
/* 817 */       AttributeValues localAttributeValues = null;
/* 818 */       if (((paramMap instanceof AttributeMap)) && 
/* 819 */         (((AttributeMap)paramMap).getValues() != null)) {
/* 820 */         localAttributeValues = ((AttributeMap)paramMap).getValues();
/* 821 */       } else if (paramMap.get(TextAttribute.TRANSFORM) != null) {
/* 822 */         localAttributeValues = fromMap(paramMap);
/*     */       }
/* 824 */       if (localAttributeValues != null) {
/* 825 */         return localAttributeValues.baselineTransform;
/*     */       }
/*     */     }
/* 828 */     return null;
/*     */   }
/*     */   
/*     */   public static AffineTransform getCharTransform(Map<?, ?> paramMap) {
/* 832 */     if (paramMap != null) {
/* 833 */       AttributeValues localAttributeValues = null;
/* 834 */       if (((paramMap instanceof AttributeMap)) && 
/* 835 */         (((AttributeMap)paramMap).getValues() != null)) {
/* 836 */         localAttributeValues = ((AttributeMap)paramMap).getValues();
/* 837 */       } else if (paramMap.get(TextAttribute.TRANSFORM) != null) {
/* 838 */         localAttributeValues = fromMap(paramMap);
/*     */       }
/* 840 */       if (localAttributeValues != null) {
/* 841 */         return localAttributeValues.charTransform;
/*     */       }
/*     */     }
/* 844 */     return null;
/*     */   }
/*     */   
/*     */   public void updateDerivedTransforms()
/*     */   {
/* 849 */     if (this.transform == null) {
/* 850 */       this.baselineTransform = null;
/* 851 */       this.charTransform = null;
/*     */     } else {
/* 853 */       this.charTransform = new AffineTransform(this.transform);
/* 854 */       this.baselineTransform = extractXRotation(this.charTransform, true);
/*     */       
/* 856 */       if (this.charTransform.isIdentity()) {
/* 857 */         this.charTransform = null;
/*     */       }
/*     */       
/* 860 */       if (this.baselineTransform.isIdentity()) {
/* 861 */         this.baselineTransform = null;
/*     */       }
/*     */     }
/*     */     
/* 865 */     if (this.baselineTransform == null) {
/* 866 */       this.nondefault &= (EAttribute.EBASELINE_TRANSFORM.mask ^ 0xFFFFFFFF);
/*     */     } else {
/* 868 */       this.nondefault |= EAttribute.EBASELINE_TRANSFORM.mask;
/*     */     }
/*     */   }
/*     */   
/*     */   public static AffineTransform extractXRotation(AffineTransform paramAffineTransform, boolean paramBoolean)
/*     */   {
/* 874 */     return extractRotation(new Point2D.Double(1.0D, 0.0D), paramAffineTransform, paramBoolean);
/*     */   }
/*     */   
/*     */   public static AffineTransform extractYRotation(AffineTransform paramAffineTransform, boolean paramBoolean)
/*     */   {
/* 879 */     return extractRotation(new Point2D.Double(0.0D, 1.0D), paramAffineTransform, paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */   private static AffineTransform extractRotation(Point2D.Double paramDouble, AffineTransform paramAffineTransform, boolean paramBoolean)
/*     */   {
/* 885 */     paramAffineTransform.deltaTransform(paramDouble, paramDouble);
/* 886 */     AffineTransform localAffineTransform1 = AffineTransform.getRotateInstance(paramDouble.x, paramDouble.y);
/*     */     try
/*     */     {
/* 889 */       AffineTransform localAffineTransform2 = localAffineTransform1.createInverse();
/* 890 */       double d1 = paramAffineTransform.getTranslateX();
/* 891 */       double d2 = paramAffineTransform.getTranslateY();
/* 892 */       paramAffineTransform.preConcatenate(localAffineTransform2);
/* 893 */       if ((paramBoolean) && (
/* 894 */         (d1 != 0.0D) || (d2 != 0.0D))) {
/* 895 */         paramAffineTransform.setTransform(paramAffineTransform.getScaleX(), paramAffineTransform.getShearY(), paramAffineTransform
/* 896 */           .getShearX(), paramAffineTransform.getScaleY(), 0.0D, 0.0D);
/* 897 */         localAffineTransform1.setTransform(localAffineTransform1.getScaleX(), localAffineTransform1.getShearY(), localAffineTransform1
/* 898 */           .getShearX(), localAffineTransform1.getScaleY(), d1, d2);
/*     */       }
/*     */     }
/*     */     catch (NoninvertibleTransformException localNoninvertibleTransformException)
/*     */     {
/* 903 */       return null;
/*     */     }
/* 905 */     return localAffineTransform1;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\font\AttributeValues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
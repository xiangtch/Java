/*     */ package sun.util.locale;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class LanguageTag
/*     */ {
/*     */   public static final String SEP = "-";
/*     */   public static final String PRIVATEUSE = "x";
/*     */   public static final String UNDETERMINED = "und";
/*     */   public static final String PRIVUSE_VARIANT_PREFIX = "lvariant";
/*  53 */   private String language = "";
/*  54 */   private String script = "";
/*  55 */   private String region = "";
/*  56 */   private String privateuse = "";
/*     */   
/*  58 */   private List<String> extlangs = Collections.emptyList();
/*  59 */   private List<String> variants = Collections.emptyList();
/*  60 */   private List<String> extensions = Collections.emptyList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  65 */   private static final Map<String, String[]> GRANDFATHERED = new HashMap();
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
/*     */   static
/*     */   {
/*  99 */     String[][] arrayOfString1 = { { "art-lojban", "jbo" }, { "cel-gaulish", "xtg-x-cel-gaulish" }, { "en-GB-oed", "en-GB-x-oed" }, { "i-ami", "ami" }, { "i-bnn", "bnn" }, { "i-default", "en-x-i-default" }, { "i-enochian", "und-x-i-enochian" }, { "i-hak", "hak" }, { "i-klingon", "tlh" }, { "i-lux", "lb" }, { "i-mingo", "see-x-i-mingo" }, { "i-navajo", "nv" }, { "i-pwn", "pwn" }, { "i-tao", "tao" }, { "i-tay", "tay" }, { "i-tsu", "tsu" }, { "no-bok", "nb" }, { "no-nyn", "nn" }, { "sgn-BE-FR", "sfb" }, { "sgn-BE-NL", "vgt" }, { "sgn-CH-DE", "sgg" }, { "zh-guoyu", "cmn" }, { "zh-hakka", "hak" }, { "zh-min", "nan-x-zh-min" }, { "zh-min-nan", "nan" }, { "zh-xiang", "hsn" } };
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
/* 128 */     for (String[] arrayOfString : arrayOfString1) {
/* 129 */       GRANDFATHERED.put(LocaleUtils.toLowerString(arrayOfString[0]), arrayOfString);
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
/*     */   public static LanguageTag parse(String paramString, ParseStatus paramParseStatus)
/*     */   {
/* 182 */     if (paramParseStatus == null) {
/* 183 */       paramParseStatus = new ParseStatus();
/*     */     } else {
/* 185 */       paramParseStatus.reset();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 191 */     String[] arrayOfString = (String[])GRANDFATHERED.get(LocaleUtils.toLowerString(paramString));
/* 192 */     StringTokenIterator localStringTokenIterator; if (arrayOfString != null)
/*     */     {
/* 194 */       localStringTokenIterator = new StringTokenIterator(arrayOfString[1], "-");
/*     */     } else {
/* 196 */       localStringTokenIterator = new StringTokenIterator(paramString, "-");
/*     */     }
/*     */     
/* 199 */     LanguageTag localLanguageTag = new LanguageTag();
/*     */     
/*     */ 
/* 202 */     if (localLanguageTag.parseLanguage(localStringTokenIterator, paramParseStatus)) {
/* 203 */       localLanguageTag.parseExtlangs(localStringTokenIterator, paramParseStatus);
/* 204 */       localLanguageTag.parseScript(localStringTokenIterator, paramParseStatus);
/* 205 */       localLanguageTag.parseRegion(localStringTokenIterator, paramParseStatus);
/* 206 */       localLanguageTag.parseVariants(localStringTokenIterator, paramParseStatus);
/* 207 */       localLanguageTag.parseExtensions(localStringTokenIterator, paramParseStatus);
/*     */     }
/* 209 */     localLanguageTag.parsePrivateuse(localStringTokenIterator, paramParseStatus);
/*     */     
/* 211 */     if ((!localStringTokenIterator.isDone()) && (!paramParseStatus.isError())) {
/* 212 */       String str = localStringTokenIterator.current();
/* 213 */       paramParseStatus.errorIndex = localStringTokenIterator.currentStart();
/* 214 */       if (str.length() == 0) {
/* 215 */         paramParseStatus.errorMsg = "Empty subtag";
/*     */       } else {
/* 217 */         paramParseStatus.errorMsg = ("Invalid subtag: " + str);
/*     */       }
/*     */     }
/*     */     
/* 221 */     return localLanguageTag;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean parseLanguage(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus)
/*     */   {
/* 229 */     if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
/* 230 */       return false;
/*     */     }
/*     */     
/* 233 */     boolean bool = false;
/*     */     
/* 235 */     String str = paramStringTokenIterator.current();
/* 236 */     if (isLanguage(str)) {
/* 237 */       bool = true;
/* 238 */       this.language = str;
/* 239 */       paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
/* 240 */       paramStringTokenIterator.next();
/*     */     }
/*     */     
/* 243 */     return bool;
/*     */   }
/*     */   
/*     */   private boolean parseExtlangs(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
/* 247 */     if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
/* 248 */       return false;
/*     */     }
/*     */     
/* 251 */     boolean bool = false;
/*     */     
/* 253 */     while (!paramStringTokenIterator.isDone()) {
/* 254 */       String str = paramStringTokenIterator.current();
/* 255 */       if (!isExtlang(str)) {
/*     */         break;
/*     */       }
/* 258 */       bool = true;
/* 259 */       if (this.extlangs.isEmpty()) {
/* 260 */         this.extlangs = new ArrayList(3);
/*     */       }
/* 262 */       this.extlangs.add(str);
/* 263 */       paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
/* 264 */       paramStringTokenIterator.next();
/*     */       
/* 266 */       if (this.extlangs.size() == 3) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 272 */     return bool;
/*     */   }
/*     */   
/*     */   private boolean parseScript(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
/* 276 */     if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
/* 277 */       return false;
/*     */     }
/*     */     
/* 280 */     boolean bool = false;
/*     */     
/* 282 */     String str = paramStringTokenIterator.current();
/* 283 */     if (isScript(str)) {
/* 284 */       bool = true;
/* 285 */       this.script = str;
/* 286 */       paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
/* 287 */       paramStringTokenIterator.next();
/*     */     }
/*     */     
/* 290 */     return bool;
/*     */   }
/*     */   
/*     */   private boolean parseRegion(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
/* 294 */     if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
/* 295 */       return false;
/*     */     }
/*     */     
/* 298 */     boolean bool = false;
/*     */     
/* 300 */     String str = paramStringTokenIterator.current();
/* 301 */     if (isRegion(str)) {
/* 302 */       bool = true;
/* 303 */       this.region = str;
/* 304 */       paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
/* 305 */       paramStringTokenIterator.next();
/*     */     }
/*     */     
/* 308 */     return bool;
/*     */   }
/*     */   
/*     */   private boolean parseVariants(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
/* 312 */     if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
/* 313 */       return false;
/*     */     }
/*     */     
/* 316 */     boolean bool = false;
/*     */     
/* 318 */     while (!paramStringTokenIterator.isDone()) {
/* 319 */       String str = paramStringTokenIterator.current();
/* 320 */       if (!isVariant(str)) {
/*     */         break;
/*     */       }
/* 323 */       bool = true;
/* 324 */       if (this.variants.isEmpty()) {
/* 325 */         this.variants = new ArrayList(3);
/*     */       }
/* 327 */       this.variants.add(str);
/* 328 */       paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
/* 329 */       paramStringTokenIterator.next();
/*     */     }
/*     */     
/* 332 */     return bool;
/*     */   }
/*     */   
/*     */   private boolean parseExtensions(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
/* 336 */     if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
/* 337 */       return false;
/*     */     }
/*     */     
/* 340 */     boolean bool = false;
/*     */     
/* 342 */     while (!paramStringTokenIterator.isDone()) {
/* 343 */       String str1 = paramStringTokenIterator.current();
/* 344 */       if (!isExtensionSingleton(str1)) break;
/* 345 */       int i = paramStringTokenIterator.currentStart();
/* 346 */       String str2 = str1;
/* 347 */       StringBuilder localStringBuilder = new StringBuilder(str2);
/*     */       
/* 349 */       paramStringTokenIterator.next();
/* 350 */       while (!paramStringTokenIterator.isDone()) {
/* 351 */         str1 = paramStringTokenIterator.current();
/* 352 */         if (!isExtensionSubtag(str1)) break;
/* 353 */         localStringBuilder.append("-").append(str1);
/* 354 */         paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
/*     */         
/*     */ 
/*     */ 
/* 358 */         paramStringTokenIterator.next();
/*     */       }
/*     */       
/* 361 */       if (paramParseStatus.parseLength <= i) {
/* 362 */         paramParseStatus.errorIndex = i;
/* 363 */         paramParseStatus.errorMsg = ("Incomplete extension '" + str2 + "'");
/* 364 */         break;
/*     */       }
/*     */       
/* 367 */       if (this.extensions.isEmpty()) {
/* 368 */         this.extensions = new ArrayList(4);
/*     */       }
/* 370 */       this.extensions.add(localStringBuilder.toString());
/* 371 */       bool = true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 376 */     return bool;
/*     */   }
/*     */   
/*     */   private boolean parsePrivateuse(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
/* 380 */     if ((paramStringTokenIterator.isDone()) || (paramParseStatus.isError())) {
/* 381 */       return false;
/*     */     }
/*     */     
/* 384 */     boolean bool = false;
/*     */     
/* 386 */     String str = paramStringTokenIterator.current();
/* 387 */     if (isPrivateusePrefix(str)) {
/* 388 */       int i = paramStringTokenIterator.currentStart();
/* 389 */       StringBuilder localStringBuilder = new StringBuilder(str);
/*     */       
/* 391 */       paramStringTokenIterator.next();
/* 392 */       while (!paramStringTokenIterator.isDone()) {
/* 393 */         str = paramStringTokenIterator.current();
/* 394 */         if (!isPrivateuseSubtag(str)) {
/*     */           break;
/*     */         }
/* 397 */         localStringBuilder.append("-").append(str);
/* 398 */         paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
/*     */         
/* 400 */         paramStringTokenIterator.next();
/*     */       }
/*     */       
/* 403 */       if (paramParseStatus.parseLength <= i)
/*     */       {
/* 405 */         paramParseStatus.errorIndex = i;
/* 406 */         paramParseStatus.errorMsg = "Incomplete privateuse";
/*     */       } else {
/* 408 */         this.privateuse = localStringBuilder.toString();
/* 409 */         bool = true;
/*     */       }
/*     */     }
/*     */     
/* 413 */     return bool;
/*     */   }
/*     */   
/*     */   public static LanguageTag parseLocale(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions) {
/* 417 */     LanguageTag localLanguageTag = new LanguageTag();
/*     */     
/* 419 */     String str1 = paramBaseLocale.getLanguage();
/* 420 */     String str2 = paramBaseLocale.getScript();
/* 421 */     String str3 = paramBaseLocale.getRegion();
/* 422 */     String str4 = paramBaseLocale.getVariant();
/*     */     
/* 424 */     int i = 0;
/*     */     
/* 426 */     String str5 = null;
/*     */     
/* 428 */     if (isLanguage(str1))
/*     */     {
/* 430 */       if (str1.equals("iw")) {
/* 431 */         str1 = "he";
/* 432 */       } else if (str1.equals("ji")) {
/* 433 */         str1 = "yi";
/* 434 */       } else if (str1.equals("in")) {
/* 435 */         str1 = "id";
/*     */       }
/* 437 */       localLanguageTag.language = str1;
/*     */     }
/*     */     
/* 440 */     if (isScript(str2)) {
/* 441 */       localLanguageTag.script = canonicalizeScript(str2);
/* 442 */       i = 1;
/*     */     }
/*     */     
/* 445 */     if (isRegion(str3)) {
/* 446 */       localLanguageTag.region = canonicalizeRegion(str3);
/* 447 */       i = 1;
/*     */     }
/*     */     
/*     */ 
/* 451 */     if ((localLanguageTag.language.equals("no")) && (localLanguageTag.region.equals("NO")) && (str4.equals("NY"))) {
/* 452 */       localLanguageTag.language = "nn";
/* 453 */       str4 = ""; }
/*     */     Object localObject2;
/*     */     Object localObject3;
/* 456 */     if (str4.length() > 0) {
/* 457 */       localArrayList = null;
/* 458 */       localObject1 = new StringTokenIterator(str4, "_");
/* 459 */       while (!((StringTokenIterator)localObject1).isDone()) {
/* 460 */         localObject2 = ((StringTokenIterator)localObject1).current();
/* 461 */         if (!isVariant((String)localObject2)) {
/*     */           break;
/*     */         }
/* 464 */         if (localArrayList == null) {
/* 465 */           localArrayList = new ArrayList();
/*     */         }
/* 467 */         localArrayList.add(localObject2);
/* 468 */         ((StringTokenIterator)localObject1).next();
/*     */       }
/* 470 */       if (localArrayList != null) {
/* 471 */         localLanguageTag.variants = localArrayList;
/* 472 */         i = 1;
/*     */       }
/* 474 */       if (!((StringTokenIterator)localObject1).isDone())
/*     */       {
/* 476 */         localObject2 = new StringBuilder();
/* 477 */         while (!((StringTokenIterator)localObject1).isDone()) {
/* 478 */           localObject3 = ((StringTokenIterator)localObject1).current();
/* 479 */           if (!isPrivateuseSubtag((String)localObject3)) {
/*     */             break;
/*     */           }
/*     */           
/* 483 */           if (((StringBuilder)localObject2).length() > 0) {
/* 484 */             ((StringBuilder)localObject2).append("-");
/*     */           }
/* 486 */           ((StringBuilder)localObject2).append((String)localObject3);
/* 487 */           ((StringTokenIterator)localObject1).next();
/*     */         }
/* 489 */         if (((StringBuilder)localObject2).length() > 0) {
/* 490 */           str5 = ((StringBuilder)localObject2).toString();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 495 */     ArrayList localArrayList = null;
/* 496 */     Object localObject1 = null;
/*     */     
/* 498 */     if (paramLocaleExtensions != null) {
/* 499 */       localObject2 = paramLocaleExtensions.getKeys();
/* 500 */       for (localObject3 = ((Set)localObject2).iterator(); ((Iterator)localObject3).hasNext();) { Character localCharacter = (Character)((Iterator)localObject3).next();
/* 501 */         Extension localExtension = paramLocaleExtensions.getExtension(localCharacter);
/* 502 */         if (isPrivateusePrefixChar(localCharacter.charValue())) {
/* 503 */           localObject1 = localExtension.getValue();
/*     */         } else {
/* 505 */           if (localArrayList == null) {
/* 506 */             localArrayList = new ArrayList();
/*     */           }
/* 508 */           localArrayList.add(localCharacter.toString() + "-" + localExtension.getValue());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 513 */     if (localArrayList != null) {
/* 514 */       localLanguageTag.extensions = localArrayList;
/* 515 */       i = 1;
/*     */     }
/*     */     
/*     */ 
/* 519 */     if (str5 != null) {
/* 520 */       if (localObject1 == null) {
/* 521 */         localObject1 = "lvariant-" + str5;
/*     */       }
/*     */       else {
/* 524 */         localObject1 = (String)localObject1 + "-" + "lvariant" + "-" + str5.replace("_", "-");
/*     */       }
/*     */     }
/*     */     
/* 528 */     if (localObject1 != null) {
/* 529 */       localLanguageTag.privateuse = ((String)localObject1);
/*     */     }
/*     */     
/* 532 */     if ((localLanguageTag.language.length() == 0) && ((i != 0) || (localObject1 == null)))
/*     */     {
/*     */ 
/*     */ 
/* 536 */       localLanguageTag.language = "und";
/*     */     }
/*     */     
/* 539 */     return localLanguageTag;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLanguage()
/*     */   {
/* 547 */     return this.language;
/*     */   }
/*     */   
/*     */   public List<String> getExtlangs() {
/* 551 */     if (this.extlangs.isEmpty()) {
/* 552 */       return Collections.emptyList();
/*     */     }
/* 554 */     return Collections.unmodifiableList(this.extlangs);
/*     */   }
/*     */   
/*     */   public String getScript() {
/* 558 */     return this.script;
/*     */   }
/*     */   
/*     */   public String getRegion() {
/* 562 */     return this.region;
/*     */   }
/*     */   
/*     */   public List<String> getVariants() {
/* 566 */     if (this.variants.isEmpty()) {
/* 567 */       return Collections.emptyList();
/*     */     }
/* 569 */     return Collections.unmodifiableList(this.variants);
/*     */   }
/*     */   
/*     */   public List<String> getExtensions() {
/* 573 */     if (this.extensions.isEmpty()) {
/* 574 */       return Collections.emptyList();
/*     */     }
/* 576 */     return Collections.unmodifiableList(this.extensions);
/*     */   }
/*     */   
/*     */   public String getPrivateuse() {
/* 580 */     return this.privateuse;
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
/*     */   public static boolean isLanguage(String paramString)
/*     */   {
/* 593 */     int i = paramString.length();
/* 594 */     return (i >= 2) && (i <= 8) && (LocaleUtils.isAlphaString(paramString));
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isExtlang(String paramString)
/*     */   {
/* 600 */     return (paramString.length() == 3) && (LocaleUtils.isAlphaString(paramString));
/*     */   }
/*     */   
/*     */   public static boolean isScript(String paramString)
/*     */   {
/* 605 */     return (paramString.length() == 4) && (LocaleUtils.isAlphaString(paramString));
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isRegion(String paramString)
/*     */   {
/* 611 */     return ((paramString.length() == 2) && (LocaleUtils.isAlphaString(paramString))) || (
/* 612 */       (paramString.length() == 3) && (LocaleUtils.isNumericString(paramString)));
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isVariant(String paramString)
/*     */   {
/* 618 */     int i = paramString.length();
/* 619 */     if ((i >= 5) && (i <= 8)) {
/* 620 */       return LocaleUtils.isAlphaNumericString(paramString);
/*     */     }
/* 622 */     if (i == 4) {
/* 623 */       return (LocaleUtils.isNumeric(paramString.charAt(0))) && 
/* 624 */         (LocaleUtils.isAlphaNumeric(paramString.charAt(1))) && 
/* 625 */         (LocaleUtils.isAlphaNumeric(paramString.charAt(2))) && 
/* 626 */         (LocaleUtils.isAlphaNumeric(paramString.charAt(3)));
/*     */     }
/* 628 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isExtensionSingleton(String paramString)
/*     */   {
/* 638 */     return (paramString.length() == 1) && 
/* 639 */       (LocaleUtils.isAlphaString(paramString)) && 
/* 640 */       (!LocaleUtils.caseIgnoreMatch("x", paramString));
/*     */   }
/*     */   
/*     */   public static boolean isExtensionSingletonChar(char paramChar) {
/* 644 */     return isExtensionSingleton(String.valueOf(paramChar));
/*     */   }
/*     */   
/*     */   public static boolean isExtensionSubtag(String paramString)
/*     */   {
/* 649 */     int i = paramString.length();
/* 650 */     return (i >= 2) && (i <= 8) && (LocaleUtils.isAlphaNumericString(paramString));
/*     */   }
/*     */   
/*     */   public static boolean isPrivateusePrefix(String paramString)
/*     */   {
/* 655 */     return (paramString.length() == 1) && 
/* 656 */       (LocaleUtils.caseIgnoreMatch("x", paramString));
/*     */   }
/*     */   
/*     */   public static boolean isPrivateusePrefixChar(char paramChar) {
/* 660 */     return LocaleUtils.caseIgnoreMatch("x", String.valueOf(paramChar));
/*     */   }
/*     */   
/*     */   public static boolean isPrivateuseSubtag(String paramString)
/*     */   {
/* 665 */     int i = paramString.length();
/* 666 */     return (i >= 1) && (i <= 8) && (LocaleUtils.isAlphaNumericString(paramString));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String canonicalizeLanguage(String paramString)
/*     */   {
/* 674 */     return LocaleUtils.toLowerString(paramString);
/*     */   }
/*     */   
/*     */   public static String canonicalizeExtlang(String paramString) {
/* 678 */     return LocaleUtils.toLowerString(paramString);
/*     */   }
/*     */   
/*     */   public static String canonicalizeScript(String paramString) {
/* 682 */     return LocaleUtils.toTitleString(paramString);
/*     */   }
/*     */   
/*     */   public static String canonicalizeRegion(String paramString) {
/* 686 */     return LocaleUtils.toUpperString(paramString);
/*     */   }
/*     */   
/*     */   public static String canonicalizeVariant(String paramString) {
/* 690 */     return LocaleUtils.toLowerString(paramString);
/*     */   }
/*     */   
/*     */   public static String canonicalizeExtension(String paramString) {
/* 694 */     return LocaleUtils.toLowerString(paramString);
/*     */   }
/*     */   
/*     */   public static String canonicalizeExtensionSingleton(String paramString) {
/* 698 */     return LocaleUtils.toLowerString(paramString);
/*     */   }
/*     */   
/*     */   public static String canonicalizeExtensionSubtag(String paramString) {
/* 702 */     return LocaleUtils.toLowerString(paramString);
/*     */   }
/*     */   
/*     */   public static String canonicalizePrivateuse(String paramString) {
/* 706 */     return LocaleUtils.toLowerString(paramString);
/*     */   }
/*     */   
/*     */   public static String canonicalizePrivateuseSubtag(String paramString) {
/* 710 */     return LocaleUtils.toLowerString(paramString);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 715 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     Iterator localIterator;
/* 717 */     String str; if (this.language.length() > 0) {
/* 718 */       localStringBuilder.append(this.language);
/*     */       
/* 720 */       for (localIterator = this.extlangs.iterator(); localIterator.hasNext();) { str = (String)localIterator.next();
/* 721 */         localStringBuilder.append("-").append(str);
/*     */       }
/*     */       
/* 724 */       if (this.script.length() > 0) {
/* 725 */         localStringBuilder.append("-").append(this.script);
/*     */       }
/*     */       
/* 728 */       if (this.region.length() > 0) {
/* 729 */         localStringBuilder.append("-").append(this.region);
/*     */       }
/*     */       
/* 732 */       for (localIterator = this.variants.iterator(); localIterator.hasNext();) { str = (String)localIterator.next();
/* 733 */         localStringBuilder.append("-").append(str);
/*     */       }
/*     */       
/* 736 */       for (localIterator = this.extensions.iterator(); localIterator.hasNext();) { str = (String)localIterator.next();
/* 737 */         localStringBuilder.append("-").append(str);
/*     */       }
/*     */     }
/* 740 */     if (this.privateuse.length() > 0) {
/* 741 */       if (localStringBuilder.length() > 0) {
/* 742 */         localStringBuilder.append("-");
/*     */       }
/* 744 */       localStringBuilder.append(this.privateuse);
/*     */     }
/*     */     
/* 747 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\LanguageTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
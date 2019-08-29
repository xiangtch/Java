/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.text.DateFormat;
/*     */ import java.text.DateFormatSymbols;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.text.spi.DateFormatProvider;
/*     */ import java.text.spi.DateFormatSymbolsProvider;
/*     */ import java.text.spi.DecimalFormatSymbolsProvider;
/*     */ import java.text.spi.NumberFormatProvider;
/*     */ import java.util.Calendar;
/*     */ import java.util.Calendar.Builder;
/*     */ import java.util.Collections;
/*     */ import java.util.Currency;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Locale.Builder;
/*     */ import java.util.ResourceBundle.Control;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ import java.util.spi.CalendarDataProvider;
/*     */ import java.util.spi.CurrencyNameProvider;
/*     */ import java.util.spi.LocaleNameProvider;
/*     */ import sun.util.spi.CalendarProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HostLocaleProviderAdapterImpl
/*     */ {
/*     */   private static final int CAT_DISPLAY = 0;
/*     */   private static final int CAT_FORMAT = 1;
/*     */   private static final int NF_NUMBER = 0;
/*     */   private static final int NF_CURRENCY = 1;
/*     */   private static final int NF_PERCENT = 2;
/*     */   private static final int NF_INTEGER = 3;
/*     */   private static final int NF_MAX = 3;
/*     */   private static final int CD_FIRSTDAYOFWEEK = 0;
/*     */   private static final int CD_MINIMALDAYSINFIRSTWEEK = 1;
/*     */   private static final int DN_CURRENCY_NAME = 0;
/*     */   private static final int DN_CURRENCY_SYMBOL = 1;
/*     */   private static final int DN_LOCALE_LANGUAGE = 2;
/*     */   private static final int DN_LOCALE_SCRIPT = 3;
/*     */   private static final int DN_LOCALE_REGION = 4;
/*     */   private static final int DN_LOCALE_VARIANT = 5;
/*  86 */   private static final String[] calIDToLDML = { "", "gregory", "gregory_en-US", "japanese", "roc", "", "islamic", "buddhist", "hebrew", "gregory_fr", "gregory_ar", "gregory_en", "gregory_fr" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */   private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> dateFormatCache = new ConcurrentHashMap();
/* 104 */   private static ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> dateFormatSymbolsCache = new ConcurrentHashMap();
/* 105 */   private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> numberFormatCache = new ConcurrentHashMap();
/* 106 */   private static ConcurrentMap<Locale, SoftReference<DecimalFormatSymbols>> decimalFormatSymbolsCache = new ConcurrentHashMap();
/*     */   private static final Set<Locale> supportedLocaleSet;
/*     */   private static final String nativeDisplayLanguage;
/*     */   
/*     */   static {
/* 111 */     HashSet localHashSet = new HashSet();
/* 112 */     if (initialize())
/*     */     {
/*     */ 
/* 115 */       ResourceBundle.Control localControl = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
/* 116 */       String str1 = getDefaultLocale(0);
/* 117 */       Locale localLocale = Locale.forLanguageTag(str1.replace('_', '-'));
/* 118 */       localHashSet.addAll(localControl.getCandidateLocales("", localLocale));
/* 119 */       nativeDisplayLanguage = localLocale.getLanguage();
/*     */       
/* 121 */       String str2 = getDefaultLocale(1);
/* 122 */       if (!str2.equals(str1)) {
/* 123 */         localLocale = Locale.forLanguageTag(str2.replace('_', '-'));
/* 124 */         localHashSet.addAll(localControl.getCandidateLocales("", localLocale));
/*     */       }
/*     */     } else {
/* 127 */       nativeDisplayLanguage = "";
/*     */     }
/* 129 */     supportedLocaleSet = Collections.unmodifiableSet(localHashSet); }
/*     */   
/* 131 */   private static final Locale[] supportedLocale = (Locale[])supportedLocaleSet.toArray(new Locale[0]);
/*     */   
/*     */   public static DateFormatProvider getDateFormatProvider() {
/* 134 */     new DateFormatProvider()
/*     */     {
/*     */       public Locale[] getAvailableLocales() {
/* 137 */         return HostLocaleProviderAdapterImpl.access$000();
/*     */       }
/*     */       
/*     */       public boolean isSupportedLocale(Locale paramAnonymousLocale)
/*     */       {
/* 142 */         return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(paramAnonymousLocale);
/*     */       }
/*     */       
/*     */       public DateFormat getDateInstance(int paramAnonymousInt, Locale paramAnonymousLocale)
/*     */       {
/* 147 */         AtomicReferenceArray localAtomicReferenceArray = getDateTimePatterns(paramAnonymousLocale);
/* 148 */         return new SimpleDateFormat((String)localAtomicReferenceArray.get(paramAnonymousInt / 2), 
/* 149 */           HostLocaleProviderAdapterImpl.getCalendarLocale(paramAnonymousLocale));
/*     */       }
/*     */       
/*     */       public DateFormat getTimeInstance(int paramAnonymousInt, Locale paramAnonymousLocale)
/*     */       {
/* 154 */         AtomicReferenceArray localAtomicReferenceArray = getDateTimePatterns(paramAnonymousLocale);
/* 155 */         return new SimpleDateFormat((String)localAtomicReferenceArray.get(paramAnonymousInt / 2 + 2), 
/* 156 */           HostLocaleProviderAdapterImpl.getCalendarLocale(paramAnonymousLocale));
/*     */       }
/*     */       
/*     */ 
/*     */       public DateFormat getDateTimeInstance(int paramAnonymousInt1, int paramAnonymousInt2, Locale paramAnonymousLocale)
/*     */       {
/* 162 */         AtomicReferenceArray localAtomicReferenceArray = getDateTimePatterns(paramAnonymousLocale);
/*     */         
/*     */ 
/*     */ 
/* 166 */         String str = (String)localAtomicReferenceArray.get(paramAnonymousInt1 / 2) + " " + (String)localAtomicReferenceArray.get(paramAnonymousInt2 / 2 + 2);
/* 167 */         return new SimpleDateFormat(str, HostLocaleProviderAdapterImpl.getCalendarLocale(paramAnonymousLocale));
/*     */       }
/*     */       
/*     */       private AtomicReferenceArray<String> getDateTimePatterns(Locale paramAnonymousLocale)
/*     */       {
/* 172 */         SoftReference localSoftReference = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatCache.get(paramAnonymousLocale);
/*     */         AtomicReferenceArray localAtomicReferenceArray;
/* 174 */         if ((localSoftReference == null) || ((localAtomicReferenceArray = (AtomicReferenceArray)localSoftReference.get()) == null)) {
/* 175 */           String str = HostLocaleProviderAdapterImpl.removeExtensions(paramAnonymousLocale).toLanguageTag();
/* 176 */           localAtomicReferenceArray = new AtomicReferenceArray(4);
/* 177 */           localAtomicReferenceArray.compareAndSet(0, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(
/* 178 */             HostLocaleProviderAdapterImpl.access$500(1, -1, str)));
/* 179 */           localAtomicReferenceArray.compareAndSet(1, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(
/* 180 */             HostLocaleProviderAdapterImpl.access$500(3, -1, str)));
/* 181 */           localAtomicReferenceArray.compareAndSet(2, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(
/* 182 */             HostLocaleProviderAdapterImpl.access$500(-1, 1, str)));
/* 183 */           localAtomicReferenceArray.compareAndSet(3, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(
/* 184 */             HostLocaleProviderAdapterImpl.access$500(-1, 3, str)));
/* 185 */           localSoftReference = new SoftReference(localAtomicReferenceArray);
/* 186 */           HostLocaleProviderAdapterImpl.dateFormatCache.put(paramAnonymousLocale, localSoftReference);
/*     */         }
/*     */         
/* 189 */         return localAtomicReferenceArray;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
/* 195 */     new DateFormatSymbolsProvider()
/*     */     {
/*     */       public Locale[] getAvailableLocales()
/*     */       {
/* 199 */         return HostLocaleProviderAdapterImpl.access$000();
/*     */       }
/*     */       
/*     */       public boolean isSupportedLocale(Locale paramAnonymousLocale)
/*     */       {
/* 204 */         return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(paramAnonymousLocale);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public DateFormatSymbols getInstance(Locale paramAnonymousLocale)
/*     */       {
/* 211 */         SoftReference localSoftReference = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatSymbolsCache.get(paramAnonymousLocale);
/*     */         DateFormatSymbols localDateFormatSymbols;
/* 213 */         if ((localSoftReference == null) || ((localDateFormatSymbols = (DateFormatSymbols)localSoftReference.get()) == null)) {
/* 214 */           localDateFormatSymbols = new DateFormatSymbols(paramAnonymousLocale);
/* 215 */           String str = HostLocaleProviderAdapterImpl.removeExtensions(paramAnonymousLocale).toLanguageTag();
/*     */           
/* 217 */           localDateFormatSymbols.setAmPmStrings(HostLocaleProviderAdapterImpl.getAmPmStrings(str, localDateFormatSymbols.getAmPmStrings()));
/* 218 */           localDateFormatSymbols.setEras(HostLocaleProviderAdapterImpl.getEras(str, localDateFormatSymbols.getEras()));
/* 219 */           localDateFormatSymbols.setMonths(HostLocaleProviderAdapterImpl.getMonths(str, localDateFormatSymbols.getMonths()));
/* 220 */           localDateFormatSymbols.setShortMonths(HostLocaleProviderAdapterImpl.getShortMonths(str, localDateFormatSymbols.getShortMonths()));
/* 221 */           localDateFormatSymbols.setWeekdays(HostLocaleProviderAdapterImpl.getWeekdays(str, localDateFormatSymbols.getWeekdays()));
/* 222 */           localDateFormatSymbols.setShortWeekdays(HostLocaleProviderAdapterImpl.getShortWeekdays(str, localDateFormatSymbols.getShortWeekdays()));
/* 223 */           localSoftReference = new SoftReference(localDateFormatSymbols);
/* 224 */           HostLocaleProviderAdapterImpl.dateFormatSymbolsCache.put(paramAnonymousLocale, localSoftReference);
/*     */         }
/* 226 */         return (DateFormatSymbols)localDateFormatSymbols.clone();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static NumberFormatProvider getNumberFormatProvider() {
/* 232 */     new NumberFormatProvider()
/*     */     {
/*     */       public Locale[] getAvailableLocales()
/*     */       {
/* 236 */         return HostLocaleProviderAdapterImpl.access$1400();
/*     */       }
/*     */       
/*     */       public boolean isSupportedLocale(Locale paramAnonymousLocale)
/*     */       {
/* 241 */         return HostLocaleProviderAdapterImpl.isSupportedNativeDigitLocale(paramAnonymousLocale);
/*     */       }
/*     */       
/*     */       public NumberFormat getCurrencyInstance(Locale paramAnonymousLocale)
/*     */       {
/* 246 */         AtomicReferenceArray localAtomicReferenceArray = getNumberPatterns(paramAnonymousLocale);
/* 247 */         return new DecimalFormat((String)localAtomicReferenceArray.get(1), 
/* 248 */           DecimalFormatSymbols.getInstance(paramAnonymousLocale));
/*     */       }
/*     */       
/*     */       public NumberFormat getIntegerInstance(Locale paramAnonymousLocale)
/*     */       {
/* 253 */         AtomicReferenceArray localAtomicReferenceArray = getNumberPatterns(paramAnonymousLocale);
/* 254 */         return new DecimalFormat((String)localAtomicReferenceArray.get(3), 
/* 255 */           DecimalFormatSymbols.getInstance(paramAnonymousLocale));
/*     */       }
/*     */       
/*     */       public NumberFormat getNumberInstance(Locale paramAnonymousLocale)
/*     */       {
/* 260 */         AtomicReferenceArray localAtomicReferenceArray = getNumberPatterns(paramAnonymousLocale);
/* 261 */         return new DecimalFormat((String)localAtomicReferenceArray.get(0), 
/* 262 */           DecimalFormatSymbols.getInstance(paramAnonymousLocale));
/*     */       }
/*     */       
/*     */       public NumberFormat getPercentInstance(Locale paramAnonymousLocale)
/*     */       {
/* 267 */         AtomicReferenceArray localAtomicReferenceArray = getNumberPatterns(paramAnonymousLocale);
/* 268 */         return new DecimalFormat((String)localAtomicReferenceArray.get(2), 
/* 269 */           DecimalFormatSymbols.getInstance(paramAnonymousLocale));
/*     */       }
/*     */       
/*     */       private AtomicReferenceArray<String> getNumberPatterns(Locale paramAnonymousLocale)
/*     */       {
/* 274 */         SoftReference localSoftReference = (SoftReference)HostLocaleProviderAdapterImpl.numberFormatCache.get(paramAnonymousLocale);
/*     */         AtomicReferenceArray localAtomicReferenceArray;
/* 276 */         if ((localSoftReference == null) || ((localAtomicReferenceArray = (AtomicReferenceArray)localSoftReference.get()) == null)) {
/* 277 */           String str = paramAnonymousLocale.toLanguageTag();
/* 278 */           localAtomicReferenceArray = new AtomicReferenceArray(4);
/* 279 */           for (int i = 0; i <= 3; i++) {
/* 280 */             localAtomicReferenceArray.compareAndSet(i, null, HostLocaleProviderAdapterImpl.getNumberPattern(i, str));
/*     */           }
/* 282 */           localSoftReference = new SoftReference(localAtomicReferenceArray);
/* 283 */           HostLocaleProviderAdapterImpl.numberFormatCache.put(paramAnonymousLocale, localSoftReference);
/*     */         }
/* 285 */         return localAtomicReferenceArray;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
/* 291 */     new DecimalFormatSymbolsProvider()
/*     */     {
/*     */       public Locale[] getAvailableLocales()
/*     */       {
/* 295 */         return HostLocaleProviderAdapterImpl.access$1400();
/*     */       }
/*     */       
/*     */       public boolean isSupportedLocale(Locale paramAnonymousLocale)
/*     */       {
/* 300 */         return HostLocaleProviderAdapterImpl.isSupportedNativeDigitLocale(paramAnonymousLocale);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public DecimalFormatSymbols getInstance(Locale paramAnonymousLocale)
/*     */       {
/* 307 */         SoftReference localSoftReference = (SoftReference)HostLocaleProviderAdapterImpl.decimalFormatSymbolsCache.get(paramAnonymousLocale);
/*     */         DecimalFormatSymbols localDecimalFormatSymbols;
/* 309 */         if ((localSoftReference == null) || ((localDecimalFormatSymbols = (DecimalFormatSymbols)localSoftReference.get()) == null)) {
/* 310 */           localDecimalFormatSymbols = new DecimalFormatSymbols(HostLocaleProviderAdapterImpl.getNumberLocale(paramAnonymousLocale));
/* 311 */           String str = HostLocaleProviderAdapterImpl.removeExtensions(paramAnonymousLocale).toLanguageTag();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 316 */           localDecimalFormatSymbols.setInternationalCurrencySymbol(HostLocaleProviderAdapterImpl.getInternationalCurrencySymbol(str, localDecimalFormatSymbols.getInternationalCurrencySymbol()));
/* 317 */           localDecimalFormatSymbols.setCurrencySymbol(HostLocaleProviderAdapterImpl.getCurrencySymbol(str, localDecimalFormatSymbols.getCurrencySymbol()));
/* 318 */           localDecimalFormatSymbols.setDecimalSeparator(HostLocaleProviderAdapterImpl.getDecimalSeparator(str, localDecimalFormatSymbols.getDecimalSeparator()));
/* 319 */           localDecimalFormatSymbols.setGroupingSeparator(HostLocaleProviderAdapterImpl.getGroupingSeparator(str, localDecimalFormatSymbols.getGroupingSeparator()));
/* 320 */           localDecimalFormatSymbols.setInfinity(HostLocaleProviderAdapterImpl.getInfinity(str, localDecimalFormatSymbols.getInfinity()));
/* 321 */           localDecimalFormatSymbols.setMinusSign(HostLocaleProviderAdapterImpl.getMinusSign(str, localDecimalFormatSymbols.getMinusSign()));
/* 322 */           localDecimalFormatSymbols.setMonetaryDecimalSeparator(HostLocaleProviderAdapterImpl.getMonetaryDecimalSeparator(str, localDecimalFormatSymbols.getMonetaryDecimalSeparator()));
/* 323 */           localDecimalFormatSymbols.setNaN(HostLocaleProviderAdapterImpl.getNaN(str, localDecimalFormatSymbols.getNaN()));
/* 324 */           localDecimalFormatSymbols.setPercent(HostLocaleProviderAdapterImpl.getPercent(str, localDecimalFormatSymbols.getPercent()));
/* 325 */           localDecimalFormatSymbols.setPerMill(HostLocaleProviderAdapterImpl.getPerMill(str, localDecimalFormatSymbols.getPerMill()));
/* 326 */           localDecimalFormatSymbols.setZeroDigit(HostLocaleProviderAdapterImpl.getZeroDigit(str, localDecimalFormatSymbols.getZeroDigit()));
/* 327 */           localSoftReference = new SoftReference(localDecimalFormatSymbols);
/* 328 */           HostLocaleProviderAdapterImpl.decimalFormatSymbolsCache.put(paramAnonymousLocale, localSoftReference);
/*     */         }
/* 330 */         return (DecimalFormatSymbols)localDecimalFormatSymbols.clone();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static CalendarDataProvider getCalendarDataProvider() {
/* 336 */     new CalendarDataProvider()
/*     */     {
/*     */       public Locale[] getAvailableLocales() {
/* 339 */         return HostLocaleProviderAdapterImpl.access$000();
/*     */       }
/*     */       
/*     */       public boolean isSupportedLocale(Locale paramAnonymousLocale)
/*     */       {
/* 344 */         return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(paramAnonymousLocale);
/*     */       }
/*     */       
/*     */       public int getFirstDayOfWeek(Locale paramAnonymousLocale)
/*     */       {
/* 349 */         int i = HostLocaleProviderAdapterImpl.getCalendarDataValue(
/* 350 */           HostLocaleProviderAdapterImpl.access$400(paramAnonymousLocale).toLanguageTag(), 0);
/*     */         
/* 352 */         if (i != -1) {
/* 353 */           return (i + 1) % 7 + 1;
/*     */         }
/* 355 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getMinimalDaysInFirstWeek(Locale paramAnonymousLocale)
/*     */       {
/* 361 */         return 0;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static CalendarProvider getCalendarProvider() {
/* 367 */     new CalendarProvider()
/*     */     {
/*     */       public Locale[] getAvailableLocales() {
/* 370 */         return HostLocaleProviderAdapterImpl.access$000();
/*     */       }
/*     */       
/*     */       public boolean isSupportedLocale(Locale paramAnonymousLocale)
/*     */       {
/* 375 */         return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(paramAnonymousLocale);
/*     */       }
/*     */       
/*     */       public Calendar getInstance(TimeZone paramAnonymousTimeZone, Locale paramAnonymousLocale)
/*     */       {
/* 380 */         return 
/*     */         
/*     */ 
/*     */ 
/* 384 */           new Calendar.Builder().setLocale(HostLocaleProviderAdapterImpl.getCalendarLocale(paramAnonymousLocale)).setTimeZone(paramAnonymousTimeZone).setInstant(System.currentTimeMillis()).build();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static CurrencyNameProvider getCurrencyNameProvider() {
/* 390 */     new CurrencyNameProvider()
/*     */     {
/*     */       public Locale[] getAvailableLocales() {
/* 393 */         return HostLocaleProviderAdapterImpl.supportedLocale;
/*     */       }
/*     */       
/*     */ 
/*     */       public boolean isSupportedLocale(Locale paramAnonymousLocale)
/*     */       {
/* 399 */         return (HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(paramAnonymousLocale.stripExtensions())) && 
/* 400 */           (paramAnonymousLocale.getLanguage().equals(HostLocaleProviderAdapterImpl.nativeDisplayLanguage));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public String getSymbol(String paramAnonymousString, Locale paramAnonymousLocale)
/*     */       {
/*     */         try
/*     */         {
/* 411 */           if (Currency.getInstance(paramAnonymousLocale).getCurrencyCode().equals(paramAnonymousString)) {
/* 412 */             return HostLocaleProviderAdapterImpl.getDisplayString(paramAnonymousLocale.toLanguageTag(), 1, paramAnonymousString);
/*     */           }
/*     */         }
/*     */         catch (IllegalArgumentException localIllegalArgumentException) {}
/* 416 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public String getDisplayName(String paramAnonymousString, Locale paramAnonymousLocale)
/*     */       {
/*     */         try
/*     */         {
/* 427 */           if (Currency.getInstance(paramAnonymousLocale).getCurrencyCode().equals(paramAnonymousString)) {
/* 428 */             return HostLocaleProviderAdapterImpl.getDisplayString(paramAnonymousLocale.toLanguageTag(), 0, paramAnonymousString);
/*     */           }
/*     */         }
/*     */         catch (IllegalArgumentException localIllegalArgumentException) {}
/* 432 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static LocaleNameProvider getLocaleNameProvider() {
/* 438 */     new LocaleNameProvider()
/*     */     {
/*     */       public Locale[] getAvailableLocales() {
/* 441 */         return HostLocaleProviderAdapterImpl.supportedLocale;
/*     */       }
/*     */       
/*     */       public boolean isSupportedLocale(Locale paramAnonymousLocale)
/*     */       {
/* 446 */         return (HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(paramAnonymousLocale.stripExtensions())) && 
/* 447 */           (paramAnonymousLocale.getLanguage().equals(HostLocaleProviderAdapterImpl.nativeDisplayLanguage));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public String getDisplayLanguage(String paramAnonymousString, Locale paramAnonymousLocale)
/*     */       {
/* 454 */         return HostLocaleProviderAdapterImpl.getDisplayString(paramAnonymousLocale.toLanguageTag(), 2, paramAnonymousString);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public String getDisplayCountry(String paramAnonymousString, Locale paramAnonymousLocale)
/*     */       {
/* 462 */         return HostLocaleProviderAdapterImpl.getDisplayString(paramAnonymousLocale.toLanguageTag(), 4, 
/* 463 */           HostLocaleProviderAdapterImpl.nativeDisplayLanguage + "-" + paramAnonymousString);
/*     */       }
/*     */       
/*     */       public String getDisplayScript(String paramAnonymousString, Locale paramAnonymousLocale)
/*     */       {
/* 468 */         return null;
/*     */       }
/*     */       
/*     */       public String getDisplayVariant(String paramAnonymousString, Locale paramAnonymousLocale)
/*     */       {
/* 473 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   private static String convertDateTimePattern(String paramString)
/*     */   {
/* 480 */     String str = paramString.replaceAll("dddd", "EEEE");
/* 481 */     str = str.replaceAll("ddd", "EEE");
/* 482 */     str = str.replaceAll("tt", "aa");
/* 483 */     str = str.replaceAll("g", "GG");
/* 484 */     return str;
/*     */   }
/*     */   
/*     */   private static Locale[] getSupportedCalendarLocales() {
/* 488 */     if ((supportedLocale.length != 0) && 
/* 489 */       (supportedLocaleSet.contains(Locale.JAPAN)) && 
/* 490 */       (isJapaneseCalendar())) {
/* 491 */       Locale[] arrayOfLocale = new Locale[supportedLocale.length + 1];
/* 492 */       arrayOfLocale[0] = JRELocaleConstants.JA_JP_JP;
/* 493 */       System.arraycopy(supportedLocale, 0, arrayOfLocale, 1, supportedLocale.length);
/* 494 */       return arrayOfLocale;
/*     */     }
/* 496 */     return supportedLocale;
/*     */   }
/*     */   
/*     */   private static boolean isSupportedCalendarLocale(Locale paramLocale) {
/* 500 */     Locale localLocale = paramLocale;
/*     */     
/* 502 */     if ((localLocale.hasExtensions()) || (localLocale.getVariant() != ""))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 507 */       localLocale = new Locale.Builder().setLocale(paramLocale).clearExtensions().build();
/*     */     }
/*     */     
/* 510 */     if (!supportedLocaleSet.contains(localLocale)) {
/* 511 */       return false;
/*     */     }
/*     */     
/* 514 */     int i = getCalendarID(localLocale.toLanguageTag());
/* 515 */     if ((i <= 0) || (i >= calIDToLDML.length)) {
/* 516 */       return false;
/*     */     }
/*     */     
/* 519 */     String str1 = paramLocale.getUnicodeLocaleType("ca");
/*     */     
/* 521 */     String str2 = calIDToLDML[i].replaceFirst("_.*", "");
/*     */     
/* 523 */     if (str1 == null) {
/* 524 */       return Calendar.getAvailableCalendarTypes().contains(str2);
/*     */     }
/* 526 */     return str1.equals(str2);
/*     */   }
/*     */   
/*     */   private static Locale[] getSupportedNativeDigitLocales()
/*     */   {
/* 531 */     if ((supportedLocale.length != 0) && 
/* 532 */       (supportedLocaleSet.contains(JRELocaleConstants.TH_TH)) && 
/* 533 */       (isNativeDigit("th-TH"))) {
/* 534 */       Locale[] arrayOfLocale = new Locale[supportedLocale.length + 1];
/* 535 */       arrayOfLocale[0] = JRELocaleConstants.TH_TH_TH;
/* 536 */       System.arraycopy(supportedLocale, 0, arrayOfLocale, 1, supportedLocale.length);
/* 537 */       return arrayOfLocale;
/*     */     }
/* 539 */     return supportedLocale;
/*     */   }
/*     */   
/*     */   private static boolean isSupportedNativeDigitLocale(Locale paramLocale)
/*     */   {
/* 544 */     if (JRELocaleConstants.TH_TH_TH.equals(paramLocale)) {
/* 545 */       return isNativeDigit("th-TH");
/*     */     }
/*     */     
/* 548 */     String str = null;
/* 549 */     Locale localLocale = paramLocale;
/* 550 */     if (paramLocale.hasExtensions()) {
/* 551 */       str = paramLocale.getUnicodeLocaleType("nu");
/* 552 */       localLocale = paramLocale.stripExtensions();
/*     */     }
/*     */     
/* 555 */     if (supportedLocaleSet.contains(localLocale))
/*     */     {
/* 557 */       if ((str == null) || (str.equals("latn")))
/* 558 */         return true;
/* 559 */       if (paramLocale.getLanguage().equals("th")) {
/* 560 */         return ("thai".equals(str)) && 
/* 561 */           (isNativeDigit(paramLocale.toLanguageTag()));
/*     */       }
/*     */     }
/*     */     
/* 565 */     return false;
/*     */   }
/*     */   
/*     */   private static Locale removeExtensions(Locale paramLocale) {
/* 569 */     return new Locale.Builder().setLocale(paramLocale).clearExtensions().build();
/*     */   }
/*     */   
/*     */   private static boolean isJapaneseCalendar() {
/* 573 */     return getCalendarID("ja-JP") == 3;
/*     */   }
/*     */   
/*     */   private static Locale getCalendarLocale(Locale paramLocale) {
/* 577 */     int i = getCalendarID(paramLocale.toLanguageTag());
/* 578 */     if ((i > 0) && (i < calIDToLDML.length)) {
/* 579 */       Locale.Builder localBuilder = new Locale.Builder();
/* 580 */       String[] arrayOfString = calIDToLDML[i].split("_");
/* 581 */       if (arrayOfString.length > 1) {
/* 582 */         localBuilder.setLocale(Locale.forLanguageTag(arrayOfString[1]));
/*     */       } else {
/* 584 */         localBuilder.setLocale(paramLocale);
/*     */       }
/* 586 */       localBuilder.setUnicodeLocaleKeyword("ca", arrayOfString[0]);
/* 587 */       return localBuilder.build();
/*     */     }
/*     */     
/* 590 */     return paramLocale;
/*     */   }
/*     */   
/*     */   private static Locale getNumberLocale(Locale paramLocale) {
/* 594 */     if ((JRELocaleConstants.TH_TH.equals(paramLocale)) && 
/* 595 */       (isNativeDigit("th-TH"))) {
/* 596 */       Locale.Builder localBuilder = new Locale.Builder().setLocale(paramLocale);
/* 597 */       localBuilder.setUnicodeLocaleKeyword("nu", "thai");
/* 598 */       return localBuilder.build();
/*     */     }
/*     */     
/*     */ 
/* 602 */     return paramLocale;
/*     */   }
/*     */   
/*     */   private static native boolean initialize();
/*     */   
/*     */   private static native String getDefaultLocale(int paramInt);
/*     */   
/*     */   private static native String getDateTimePattern(int paramInt1, int paramInt2, String paramString);
/*     */   
/*     */   private static native int getCalendarID(String paramString);
/*     */   
/*     */   private static native String[] getAmPmStrings(String paramString, String[] paramArrayOfString);
/*     */   
/*     */   private static native String[] getEras(String paramString, String[] paramArrayOfString);
/*     */   
/*     */   private static native String[] getMonths(String paramString, String[] paramArrayOfString);
/*     */   
/*     */   private static native String[] getShortMonths(String paramString, String[] paramArrayOfString);
/*     */   
/*     */   private static native String[] getWeekdays(String paramString, String[] paramArrayOfString);
/*     */   
/*     */   private static native String[] getShortWeekdays(String paramString, String[] paramArrayOfString);
/*     */   
/*     */   private static native String getNumberPattern(int paramInt, String paramString);
/*     */   
/*     */   private static native boolean isNativeDigit(String paramString);
/*     */   
/*     */   private static native String getCurrencySymbol(String paramString1, String paramString2);
/*     */   
/*     */   private static native char getDecimalSeparator(String paramString, char paramChar);
/*     */   
/*     */   private static native char getGroupingSeparator(String paramString, char paramChar);
/*     */   
/*     */   private static native String getInfinity(String paramString1, String paramString2);
/*     */   
/*     */   private static native String getInternationalCurrencySymbol(String paramString1, String paramString2);
/*     */   
/*     */   private static native char getMinusSign(String paramString, char paramChar);
/*     */   
/*     */   private static native char getMonetaryDecimalSeparator(String paramString, char paramChar);
/*     */   
/*     */   private static native String getNaN(String paramString1, String paramString2);
/*     */   
/*     */   private static native char getPercent(String paramString, char paramChar);
/*     */   
/*     */   private static native char getPerMill(String paramString, char paramChar);
/*     */   
/*     */   private static native char getZeroDigit(String paramString, char paramChar);
/*     */   
/*     */   private static native int getCalendarDataValue(String paramString, int paramInt);
/*     */   
/*     */   private static native String getDisplayString(String paramString1, int paramInt, String paramString2);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\HostLocaleProviderAdapterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
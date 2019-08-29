/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.spi.BreakIteratorProvider;
/*     */ import java.text.spi.CollatorProvider;
/*     */ import java.text.spi.DateFormatProvider;
/*     */ import java.text.spi.DateFormatSymbolsProvider;
/*     */ import java.text.spi.DecimalFormatSymbolsProvider;
/*     */ import java.text.spi.NumberFormatProvider;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.spi.CalendarDataProvider;
/*     */ import java.util.spi.CalendarNameProvider;
/*     */ import java.util.spi.CurrencyNameProvider;
/*     */ import java.util.spi.LocaleNameProvider;
/*     */ import java.util.spi.LocaleServiceProvider;
/*     */ import java.util.spi.TimeZoneNameProvider;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.util.resources.LocaleData;
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
/*     */ public class JRELocaleProviderAdapter
/*     */   extends LocaleProviderAdapter
/*     */   implements ResourceBundleBasedAdapter
/*     */ {
/*     */   private static final String LOCALE_DATA_JAR_NAME = "localedata.jar";
/*  63 */   private final ConcurrentMap<String, Set<String>> langtagSets = new ConcurrentHashMap();
/*     */   
/*     */ 
/*  66 */   private final ConcurrentMap<Locale, LocaleResources> localeResourcesMap = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile LocaleData localeData;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Type getAdapterType()
/*     */   {
/*  77 */     return Type.JRE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass)
/*     */   {
/*  86 */     switch (paramClass.getSimpleName()) {
/*     */     case "BreakIteratorProvider": 
/*  88 */       return getBreakIteratorProvider();
/*     */     case "CollatorProvider": 
/*  90 */       return getCollatorProvider();
/*     */     case "DateFormatProvider": 
/*  92 */       return getDateFormatProvider();
/*     */     case "DateFormatSymbolsProvider": 
/*  94 */       return getDateFormatSymbolsProvider();
/*     */     case "DecimalFormatSymbolsProvider": 
/*  96 */       return getDecimalFormatSymbolsProvider();
/*     */     case "NumberFormatProvider": 
/*  98 */       return getNumberFormatProvider();
/*     */     case "CurrencyNameProvider": 
/* 100 */       return getCurrencyNameProvider();
/*     */     case "LocaleNameProvider": 
/* 102 */       return getLocaleNameProvider();
/*     */     case "TimeZoneNameProvider": 
/* 104 */       return getTimeZoneNameProvider();
/*     */     case "CalendarDataProvider": 
/* 106 */       return getCalendarDataProvider();
/*     */     case "CalendarNameProvider": 
/* 108 */       return getCalendarNameProvider();
/*     */     case "CalendarProvider": 
/* 110 */       return getCalendarProvider();
/*     */     }
/* 112 */     throw new InternalError("should not come down here");
/*     */   }
/*     */   
/*     */ 
/* 116 */   private volatile BreakIteratorProvider breakIteratorProvider = null;
/* 117 */   private volatile CollatorProvider collatorProvider = null;
/* 118 */   private volatile DateFormatProvider dateFormatProvider = null;
/* 119 */   private volatile DateFormatSymbolsProvider dateFormatSymbolsProvider = null;
/* 120 */   private volatile DecimalFormatSymbolsProvider decimalFormatSymbolsProvider = null;
/* 121 */   private volatile NumberFormatProvider numberFormatProvider = null;
/*     */   
/* 123 */   private volatile CurrencyNameProvider currencyNameProvider = null;
/* 124 */   private volatile LocaleNameProvider localeNameProvider = null;
/* 125 */   private volatile TimeZoneNameProvider timeZoneNameProvider = null;
/* 126 */   private volatile CalendarDataProvider calendarDataProvider = null;
/* 127 */   private volatile CalendarNameProvider calendarNameProvider = null;
/*     */   
/* 129 */   private volatile CalendarProvider calendarProvider = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BreakIteratorProvider getBreakIteratorProvider()
/*     */   {
/* 136 */     if (this.breakIteratorProvider == null)
/*     */     {
/* 138 */       BreakIteratorProviderImpl localBreakIteratorProviderImpl = new BreakIteratorProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
/* 139 */       synchronized (this) {
/* 140 */         if (this.breakIteratorProvider == null) {
/* 141 */           this.breakIteratorProvider = localBreakIteratorProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 145 */     return this.breakIteratorProvider;
/*     */   }
/*     */   
/*     */   public CollatorProvider getCollatorProvider()
/*     */   {
/* 150 */     if (this.collatorProvider == null)
/*     */     {
/* 152 */       CollatorProviderImpl localCollatorProviderImpl = new CollatorProviderImpl(getAdapterType(), getLanguageTagSet("CollationData"));
/* 153 */       synchronized (this) {
/* 154 */         if (this.collatorProvider == null) {
/* 155 */           this.collatorProvider = localCollatorProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 159 */     return this.collatorProvider;
/*     */   }
/*     */   
/*     */   public DateFormatProvider getDateFormatProvider()
/*     */   {
/* 164 */     if (this.dateFormatProvider == null)
/*     */     {
/* 166 */       DateFormatProviderImpl localDateFormatProviderImpl = new DateFormatProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
/* 167 */       synchronized (this) {
/* 168 */         if (this.dateFormatProvider == null) {
/* 169 */           this.dateFormatProvider = localDateFormatProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 173 */     return this.dateFormatProvider;
/*     */   }
/*     */   
/*     */   public DateFormatSymbolsProvider getDateFormatSymbolsProvider()
/*     */   {
/* 178 */     if (this.dateFormatSymbolsProvider == null)
/*     */     {
/* 180 */       DateFormatSymbolsProviderImpl localDateFormatSymbolsProviderImpl = new DateFormatSymbolsProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
/* 181 */       synchronized (this) {
/* 182 */         if (this.dateFormatSymbolsProvider == null) {
/* 183 */           this.dateFormatSymbolsProvider = localDateFormatSymbolsProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 187 */     return this.dateFormatSymbolsProvider;
/*     */   }
/*     */   
/*     */   public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider()
/*     */   {
/* 192 */     if (this.decimalFormatSymbolsProvider == null) {
/* 193 */       DecimalFormatSymbolsProviderImpl localDecimalFormatSymbolsProviderImpl = new DecimalFormatSymbolsProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
/* 194 */       synchronized (this) {
/* 195 */         if (this.decimalFormatSymbolsProvider == null) {
/* 196 */           this.decimalFormatSymbolsProvider = localDecimalFormatSymbolsProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 200 */     return this.decimalFormatSymbolsProvider;
/*     */   }
/*     */   
/*     */   public NumberFormatProvider getNumberFormatProvider()
/*     */   {
/* 205 */     if (this.numberFormatProvider == null)
/*     */     {
/* 207 */       NumberFormatProviderImpl localNumberFormatProviderImpl = new NumberFormatProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
/* 208 */       synchronized (this) {
/* 209 */         if (this.numberFormatProvider == null) {
/* 210 */           this.numberFormatProvider = localNumberFormatProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 214 */     return this.numberFormatProvider;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CurrencyNameProvider getCurrencyNameProvider()
/*     */   {
/* 222 */     if (this.currencyNameProvider == null)
/*     */     {
/* 224 */       CurrencyNameProviderImpl localCurrencyNameProviderImpl = new CurrencyNameProviderImpl(getAdapterType(), getLanguageTagSet("CurrencyNames"));
/* 225 */       synchronized (this) {
/* 226 */         if (this.currencyNameProvider == null) {
/* 227 */           this.currencyNameProvider = localCurrencyNameProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 231 */     return this.currencyNameProvider;
/*     */   }
/*     */   
/*     */   public LocaleNameProvider getLocaleNameProvider()
/*     */   {
/* 236 */     if (this.localeNameProvider == null)
/*     */     {
/* 238 */       LocaleNameProviderImpl localLocaleNameProviderImpl = new LocaleNameProviderImpl(getAdapterType(), getLanguageTagSet("LocaleNames"));
/* 239 */       synchronized (this) {
/* 240 */         if (this.localeNameProvider == null) {
/* 241 */           this.localeNameProvider = localLocaleNameProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 245 */     return this.localeNameProvider;
/*     */   }
/*     */   
/*     */   public TimeZoneNameProvider getTimeZoneNameProvider()
/*     */   {
/* 250 */     if (this.timeZoneNameProvider == null)
/*     */     {
/* 252 */       TimeZoneNameProviderImpl localTimeZoneNameProviderImpl = new TimeZoneNameProviderImpl(getAdapterType(), getLanguageTagSet("TimeZoneNames"));
/* 253 */       synchronized (this) {
/* 254 */         if (this.timeZoneNameProvider == null) {
/* 255 */           this.timeZoneNameProvider = localTimeZoneNameProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 259 */     return this.timeZoneNameProvider;
/*     */   }
/*     */   
/*     */   public CalendarDataProvider getCalendarDataProvider()
/*     */   {
/* 264 */     if (this.calendarDataProvider == null)
/*     */     {
/*     */ 
/* 267 */       CalendarDataProviderImpl localCalendarDataProviderImpl = new CalendarDataProviderImpl(getAdapterType(), getLanguageTagSet("CalendarData"));
/* 268 */       synchronized (this) {
/* 269 */         if (this.calendarDataProvider == null) {
/* 270 */           this.calendarDataProvider = localCalendarDataProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 274 */     return this.calendarDataProvider;
/*     */   }
/*     */   
/*     */   public CalendarNameProvider getCalendarNameProvider()
/*     */   {
/* 279 */     if (this.calendarNameProvider == null)
/*     */     {
/*     */ 
/* 282 */       CalendarNameProviderImpl localCalendarNameProviderImpl = new CalendarNameProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
/* 283 */       synchronized (this) {
/* 284 */         if (this.calendarNameProvider == null) {
/* 285 */           this.calendarNameProvider = localCalendarNameProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 289 */     return this.calendarNameProvider;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CalendarProvider getCalendarProvider()
/*     */   {
/* 297 */     if (this.calendarProvider == null)
/*     */     {
/* 299 */       CalendarProviderImpl localCalendarProviderImpl = new CalendarProviderImpl(getAdapterType(), getLanguageTagSet("CalendarData"));
/* 300 */       synchronized (this) {
/* 301 */         if (this.calendarProvider == null) {
/* 302 */           this.calendarProvider = localCalendarProviderImpl;
/*     */         }
/*     */       }
/*     */     }
/* 306 */     return this.calendarProvider;
/*     */   }
/*     */   
/*     */   public LocaleResources getLocaleResources(Locale paramLocale)
/*     */   {
/* 311 */     Object localObject = (LocaleResources)this.localeResourcesMap.get(paramLocale);
/* 312 */     if (localObject == null) {
/* 313 */       localObject = new LocaleResources(this, paramLocale);
/* 314 */       LocaleResources localLocaleResources = (LocaleResources)this.localeResourcesMap.putIfAbsent(paramLocale, localObject);
/* 315 */       if (localLocaleResources != null) {
/* 316 */         localObject = localLocaleResources;
/*     */       }
/*     */     }
/* 319 */     return (LocaleResources)localObject;
/*     */   }
/*     */   
/*     */ 
/*     */   public LocaleData getLocaleData()
/*     */   {
/* 325 */     if (this.localeData == null) {
/* 326 */       synchronized (this) {
/* 327 */         if (this.localeData == null) {
/* 328 */           this.localeData = new LocaleData(getAdapterType());
/*     */         }
/*     */       }
/*     */     }
/* 332 */     return this.localeData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Locale[] getAvailableLocales()
/*     */   {
/* 343 */     return (Locale[])AvailableJRELocales.localeList.clone();
/*     */   }
/*     */   
/*     */   public Set<String> getLanguageTagSet(String paramString) {
/* 347 */     Object localObject = (Set)this.langtagSets.get(paramString);
/* 348 */     if (localObject == null) {
/* 349 */       localObject = createLanguageTagSet(paramString);
/* 350 */       Set localSet = (Set)this.langtagSets.putIfAbsent(paramString, localObject);
/* 351 */       if (localSet != null) {
/* 352 */         localObject = localSet;
/*     */       }
/*     */     }
/* 355 */     return (Set<String>)localObject;
/*     */   }
/*     */   
/*     */   protected Set<String> createLanguageTagSet(String paramString) {
/* 359 */     String str1 = LocaleDataMetaInfo.getSupportedLocaleString(paramString);
/* 360 */     if (str1 == null) {
/* 361 */       return Collections.emptySet();
/*     */     }
/* 363 */     HashSet localHashSet = new HashSet();
/* 364 */     StringTokenizer localStringTokenizer = new StringTokenizer(str1);
/* 365 */     while (localStringTokenizer.hasMoreTokens()) {
/* 366 */       String str2 = localStringTokenizer.nextToken();
/* 367 */       if (str2.equals("|")) {
/* 368 */         if (!isNonENLangSupported()) {
/*     */           break;
/*     */         }
/*     */         
/*     */       } else {
/* 373 */         localHashSet.add(str2);
/*     */       }
/*     */     }
/* 376 */     return localHashSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class AvailableJRELocales
/*     */   {
/* 383 */     private static final Locale[] localeList = ;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Locale[] createAvailableLocales()
/*     */   {
/* 394 */     String str1 = LocaleDataMetaInfo.getSupportedLocaleString("AvailableLocales");
/*     */     
/* 396 */     if (str1.length() == 0) {
/* 397 */       throw new InternalError("No available locales for JRE");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 403 */     int i = str1.indexOf('|');
/*     */     StringTokenizer localStringTokenizer;
/* 405 */     if (isNonENLangSupported())
/*     */     {
/* 407 */       localStringTokenizer = new StringTokenizer(str1.substring(0, i) + str1.substring(i + 1));
/*     */     } else {
/* 409 */       localStringTokenizer = new StringTokenizer(str1.substring(0, i));
/*     */     }
/*     */     
/* 412 */     int j = localStringTokenizer.countTokens();
/* 413 */     Locale[] arrayOfLocale = new Locale[j + 1];
/* 414 */     arrayOfLocale[0] = Locale.ROOT;
/* 415 */     for (int k = 1; k <= j; k++) {
/* 416 */       String str2 = localStringTokenizer.nextToken();
/* 417 */       switch (str2) {
/*     */       case "ja-JP-JP": 
/* 419 */         arrayOfLocale[k] = JRELocaleConstants.JA_JP_JP;
/* 420 */         break;
/*     */       case "no-NO-NY": 
/* 422 */         arrayOfLocale[k] = JRELocaleConstants.NO_NO_NY;
/* 423 */         break;
/*     */       case "th-TH-TH": 
/* 425 */         arrayOfLocale[k] = JRELocaleConstants.TH_TH_TH;
/* 426 */         break;
/*     */       default: 
/* 428 */         arrayOfLocale[k] = Locale.forLanguageTag(str2);
/*     */       }
/*     */     }
/* 431 */     return arrayOfLocale;
/*     */   }
/*     */   
/* 434 */   private static volatile Boolean isNonENSupported = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isNonENLangSupported()
/*     */   {
/* 442 */     if (isNonENSupported == null) {
/* 443 */       synchronized (JRELocaleProviderAdapter.class) {
/* 444 */         if (isNonENSupported == null) {
/* 445 */           String str1 = File.separator;
/*     */           
/* 447 */           String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + str1 + "lib" + str1 + "ext" + str1 + "localedata.jar";
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 455 */           File localFile = new File(str2);
/*     */           
/* 457 */           isNonENSupported = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */           {
/*     */             public Boolean run()
/*     */             {
/* 460 */               return Boolean.valueOf(this.val$f.exists());
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     }
/* 466 */     return isNonENSupported.booleanValue();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\JRELocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
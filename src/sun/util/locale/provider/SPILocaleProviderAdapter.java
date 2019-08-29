/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.text.BreakIterator;
/*     */ import java.text.Collator;
/*     */ import java.text.DateFormat;
/*     */ import java.text.DateFormatSymbols;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.spi.BreakIteratorProvider;
/*     */ import java.text.spi.CollatorProvider;
/*     */ import java.text.spi.DateFormatProvider;
/*     */ import java.text.spi.DateFormatSymbolsProvider;
/*     */ import java.text.spi.DecimalFormatSymbolsProvider;
/*     */ import java.text.spi.NumberFormatProvider;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.spi.CalendarDataProvider;
/*     */ import java.util.spi.CalendarNameProvider;
/*     */ import java.util.spi.CurrencyNameProvider;
/*     */ import java.util.spi.LocaleNameProvider;
/*     */ import java.util.spi.LocaleServiceProvider;
/*     */ import java.util.spi.TimeZoneNameProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SPILocaleProviderAdapter
/*     */   extends AuxLocaleProviderAdapter
/*     */ {
/*     */   public Type getAdapterType()
/*     */   {
/*  68 */     return Type.SPI;
/*     */   }
/*     */   
/*     */   protected <P extends LocaleServiceProvider> P findInstalledProvider(final Class<P> paramClass)
/*     */   {
/*     */     try {
/*  74 */       (LocaleServiceProvider)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public P run()
/*     */         {
/*  78 */           LocaleServiceProvider localLocaleServiceProvider1 = null;
/*     */           
/*  80 */           for (LocaleServiceProvider localLocaleServiceProvider2 : ServiceLoader.loadInstalled(paramClass)) {
/*  81 */             if (localLocaleServiceProvider1 == null)
/*     */             {
/*     */ 
/*     */               try
/*     */               {
/*     */ 
/*     */ 
/*  88 */                 localLocaleServiceProvider1 = (LocaleServiceProvider)Class.forName(SPILocaleProviderAdapter.class.getCanonicalName() + "$" + paramClass.getSimpleName() + "Delegate").newInstance();
/*     */               }
/*     */               catch (ClassNotFoundException|InstantiationException|IllegalAccessException localClassNotFoundException)
/*     */               {
/*  92 */                 LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, localClassNotFoundException.toString());
/*  93 */                 return null;
/*     */               }
/*     */             }
/*     */             
/*  97 */             ((Delegate)localLocaleServiceProvider1).addImpl(localLocaleServiceProvider2);
/*     */           }
/*  99 */           return localLocaleServiceProvider1;
/*     */         }
/*     */       });
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 103 */       LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, localPrivilegedActionException.toString());
/*     */     }
/* 105 */     return null;
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
/*     */   private static <P extends LocaleServiceProvider> P getImpl(Map<Locale, P> paramMap, Locale paramLocale)
/*     */   {
/* 121 */     for (Locale localLocale : LocaleServiceProviderPool.getLookupLocales(paramLocale)) {
/* 122 */       LocaleServiceProvider localLocaleServiceProvider = (LocaleServiceProvider)paramMap.get(localLocale);
/* 123 */       if (localLocaleServiceProvider != null) {
/* 124 */         return localLocaleServiceProvider;
/*     */       }
/*     */     }
/* 127 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   static class BreakIteratorProviderDelegate
/*     */     extends BreakIteratorProvider
/*     */     implements Delegate<BreakIteratorProvider>
/*     */   {
/* 135 */     private ConcurrentMap<Locale, BreakIteratorProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(BreakIteratorProvider paramBreakIteratorProvider)
/*     */     {
/* 139 */       for (Locale localLocale : paramBreakIteratorProvider.getAvailableLocales()) {
/* 140 */         this.map.putIfAbsent(localLocale, paramBreakIteratorProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public BreakIteratorProvider getImpl(Locale paramLocale)
/*     */     {
/* 146 */       return (BreakIteratorProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 151 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 156 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public BreakIterator getWordInstance(Locale paramLocale)
/*     */     {
/* 161 */       BreakIteratorProvider localBreakIteratorProvider = getImpl(paramLocale);
/* 162 */       assert (localBreakIteratorProvider != null);
/* 163 */       return localBreakIteratorProvider.getWordInstance(paramLocale);
/*     */     }
/*     */     
/*     */     public BreakIterator getLineInstance(Locale paramLocale)
/*     */     {
/* 168 */       BreakIteratorProvider localBreakIteratorProvider = getImpl(paramLocale);
/* 169 */       assert (localBreakIteratorProvider != null);
/* 170 */       return localBreakIteratorProvider.getLineInstance(paramLocale);
/*     */     }
/*     */     
/*     */     public BreakIterator getCharacterInstance(Locale paramLocale)
/*     */     {
/* 175 */       BreakIteratorProvider localBreakIteratorProvider = getImpl(paramLocale);
/* 176 */       assert (localBreakIteratorProvider != null);
/* 177 */       return localBreakIteratorProvider.getCharacterInstance(paramLocale);
/*     */     }
/*     */     
/*     */     public BreakIterator getSentenceInstance(Locale paramLocale)
/*     */     {
/* 182 */       BreakIteratorProvider localBreakIteratorProvider = getImpl(paramLocale);
/* 183 */       assert (localBreakIteratorProvider != null);
/* 184 */       return localBreakIteratorProvider.getSentenceInstance(paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class CollatorProviderDelegate extends CollatorProvider implements Delegate<CollatorProvider>
/*     */   {
/* 190 */     private ConcurrentMap<Locale, CollatorProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(CollatorProvider paramCollatorProvider)
/*     */     {
/* 194 */       for (Locale localLocale : paramCollatorProvider.getAvailableLocales()) {
/* 195 */         this.map.putIfAbsent(localLocale, paramCollatorProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public CollatorProvider getImpl(Locale paramLocale)
/*     */     {
/* 201 */       return (CollatorProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 206 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 211 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public Collator getInstance(Locale paramLocale)
/*     */     {
/* 216 */       CollatorProvider localCollatorProvider = getImpl(paramLocale);
/* 217 */       assert (localCollatorProvider != null);
/* 218 */       return localCollatorProvider.getInstance(paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class DateFormatProviderDelegate extends DateFormatProvider implements Delegate<DateFormatProvider>
/*     */   {
/* 224 */     private ConcurrentMap<Locale, DateFormatProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(DateFormatProvider paramDateFormatProvider)
/*     */     {
/* 228 */       for (Locale localLocale : paramDateFormatProvider.getAvailableLocales()) {
/* 229 */         this.map.putIfAbsent(localLocale, paramDateFormatProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public DateFormatProvider getImpl(Locale paramLocale)
/*     */     {
/* 235 */       return (DateFormatProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 240 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 245 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public DateFormat getTimeInstance(int paramInt, Locale paramLocale)
/*     */     {
/* 250 */       DateFormatProvider localDateFormatProvider = getImpl(paramLocale);
/* 251 */       assert (localDateFormatProvider != null);
/* 252 */       return localDateFormatProvider.getTimeInstance(paramInt, paramLocale);
/*     */     }
/*     */     
/*     */     public DateFormat getDateInstance(int paramInt, Locale paramLocale)
/*     */     {
/* 257 */       DateFormatProvider localDateFormatProvider = getImpl(paramLocale);
/* 258 */       assert (localDateFormatProvider != null);
/* 259 */       return localDateFormatProvider.getDateInstance(paramInt, paramLocale);
/*     */     }
/*     */     
/*     */     public DateFormat getDateTimeInstance(int paramInt1, int paramInt2, Locale paramLocale)
/*     */     {
/* 264 */       DateFormatProvider localDateFormatProvider = getImpl(paramLocale);
/* 265 */       assert (localDateFormatProvider != null);
/* 266 */       return localDateFormatProvider.getDateTimeInstance(paramInt1, paramInt2, paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class DateFormatSymbolsProviderDelegate extends DateFormatSymbolsProvider implements Delegate<DateFormatSymbolsProvider>
/*     */   {
/* 272 */     private ConcurrentMap<Locale, DateFormatSymbolsProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(DateFormatSymbolsProvider paramDateFormatSymbolsProvider)
/*     */     {
/* 276 */       for (Locale localLocale : paramDateFormatSymbolsProvider.getAvailableLocales()) {
/* 277 */         this.map.putIfAbsent(localLocale, paramDateFormatSymbolsProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public DateFormatSymbolsProvider getImpl(Locale paramLocale)
/*     */     {
/* 283 */       return (DateFormatSymbolsProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 288 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 293 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public DateFormatSymbols getInstance(Locale paramLocale)
/*     */     {
/* 298 */       DateFormatSymbolsProvider localDateFormatSymbolsProvider = getImpl(paramLocale);
/* 299 */       assert (localDateFormatSymbolsProvider != null);
/* 300 */       return localDateFormatSymbolsProvider.getInstance(paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class DecimalFormatSymbolsProviderDelegate extends DecimalFormatSymbolsProvider implements Delegate<DecimalFormatSymbolsProvider>
/*     */   {
/* 306 */     private ConcurrentMap<Locale, DecimalFormatSymbolsProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(DecimalFormatSymbolsProvider paramDecimalFormatSymbolsProvider)
/*     */     {
/* 310 */       for (Locale localLocale : paramDecimalFormatSymbolsProvider.getAvailableLocales()) {
/* 311 */         this.map.putIfAbsent(localLocale, paramDecimalFormatSymbolsProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public DecimalFormatSymbolsProvider getImpl(Locale paramLocale)
/*     */     {
/* 317 */       return (DecimalFormatSymbolsProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 322 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 327 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public DecimalFormatSymbols getInstance(Locale paramLocale)
/*     */     {
/* 332 */       DecimalFormatSymbolsProvider localDecimalFormatSymbolsProvider = getImpl(paramLocale);
/* 333 */       assert (localDecimalFormatSymbolsProvider != null);
/* 334 */       return localDecimalFormatSymbolsProvider.getInstance(paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class NumberFormatProviderDelegate extends NumberFormatProvider implements Delegate<NumberFormatProvider>
/*     */   {
/* 340 */     private ConcurrentMap<Locale, NumberFormatProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(NumberFormatProvider paramNumberFormatProvider)
/*     */     {
/* 344 */       for (Locale localLocale : paramNumberFormatProvider.getAvailableLocales()) {
/* 345 */         this.map.putIfAbsent(localLocale, paramNumberFormatProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public NumberFormatProvider getImpl(Locale paramLocale)
/*     */     {
/* 351 */       return (NumberFormatProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 356 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 361 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public NumberFormat getCurrencyInstance(Locale paramLocale)
/*     */     {
/* 366 */       NumberFormatProvider localNumberFormatProvider = getImpl(paramLocale);
/* 367 */       assert (localNumberFormatProvider != null);
/* 368 */       return localNumberFormatProvider.getCurrencyInstance(paramLocale);
/*     */     }
/*     */     
/*     */     public NumberFormat getIntegerInstance(Locale paramLocale)
/*     */     {
/* 373 */       NumberFormatProvider localNumberFormatProvider = getImpl(paramLocale);
/* 374 */       assert (localNumberFormatProvider != null);
/* 375 */       return localNumberFormatProvider.getIntegerInstance(paramLocale);
/*     */     }
/*     */     
/*     */     public NumberFormat getNumberInstance(Locale paramLocale)
/*     */     {
/* 380 */       NumberFormatProvider localNumberFormatProvider = getImpl(paramLocale);
/* 381 */       assert (localNumberFormatProvider != null);
/* 382 */       return localNumberFormatProvider.getNumberInstance(paramLocale);
/*     */     }
/*     */     
/*     */     public NumberFormat getPercentInstance(Locale paramLocale)
/*     */     {
/* 387 */       NumberFormatProvider localNumberFormatProvider = getImpl(paramLocale);
/* 388 */       assert (localNumberFormatProvider != null);
/* 389 */       return localNumberFormatProvider.getPercentInstance(paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class CalendarDataProviderDelegate extends CalendarDataProvider implements Delegate<CalendarDataProvider>
/*     */   {
/* 395 */     private ConcurrentMap<Locale, CalendarDataProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(CalendarDataProvider paramCalendarDataProvider)
/*     */     {
/* 399 */       for (Locale localLocale : paramCalendarDataProvider.getAvailableLocales()) {
/* 400 */         this.map.putIfAbsent(localLocale, paramCalendarDataProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public CalendarDataProvider getImpl(Locale paramLocale)
/*     */     {
/* 406 */       return (CalendarDataProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 411 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 416 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public int getFirstDayOfWeek(Locale paramLocale)
/*     */     {
/* 421 */       CalendarDataProvider localCalendarDataProvider = getImpl(paramLocale);
/* 422 */       assert (localCalendarDataProvider != null);
/* 423 */       return localCalendarDataProvider.getFirstDayOfWeek(paramLocale);
/*     */     }
/*     */     
/*     */     public int getMinimalDaysInFirstWeek(Locale paramLocale)
/*     */     {
/* 428 */       CalendarDataProvider localCalendarDataProvider = getImpl(paramLocale);
/* 429 */       assert (localCalendarDataProvider != null);
/* 430 */       return localCalendarDataProvider.getMinimalDaysInFirstWeek(paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class CalendarNameProviderDelegate extends CalendarNameProvider implements Delegate<CalendarNameProvider>
/*     */   {
/* 436 */     private ConcurrentMap<Locale, CalendarNameProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(CalendarNameProvider paramCalendarNameProvider)
/*     */     {
/* 440 */       for (Locale localLocale : paramCalendarNameProvider.getAvailableLocales()) {
/* 441 */         this.map.putIfAbsent(localLocale, paramCalendarNameProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public CalendarNameProvider getImpl(Locale paramLocale)
/*     */     {
/* 447 */       return (CalendarNameProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 452 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 457 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getDisplayName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale)
/*     */     {
/* 464 */       CalendarNameProvider localCalendarNameProvider = getImpl(paramLocale);
/* 465 */       assert (localCalendarNameProvider != null);
/* 466 */       return localCalendarNameProvider.getDisplayName(paramString, paramInt1, paramInt2, paramInt3, paramLocale);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Map<String, Integer> getDisplayNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
/*     */     {
/* 473 */       CalendarNameProvider localCalendarNameProvider = getImpl(paramLocale);
/* 474 */       assert (localCalendarNameProvider != null);
/* 475 */       return localCalendarNameProvider.getDisplayNames(paramString, paramInt1, paramInt2, paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class CurrencyNameProviderDelegate extends CurrencyNameProvider implements Delegate<CurrencyNameProvider>
/*     */   {
/* 481 */     private ConcurrentMap<Locale, CurrencyNameProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(CurrencyNameProvider paramCurrencyNameProvider)
/*     */     {
/* 485 */       for (Locale localLocale : paramCurrencyNameProvider.getAvailableLocales()) {
/* 486 */         this.map.putIfAbsent(localLocale, paramCurrencyNameProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public CurrencyNameProvider getImpl(Locale paramLocale)
/*     */     {
/* 492 */       return (CurrencyNameProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 497 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 502 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public String getSymbol(String paramString, Locale paramLocale)
/*     */     {
/* 507 */       CurrencyNameProvider localCurrencyNameProvider = getImpl(paramLocale);
/* 508 */       assert (localCurrencyNameProvider != null);
/* 509 */       return localCurrencyNameProvider.getSymbol(paramString, paramLocale);
/*     */     }
/*     */     
/*     */     public String getDisplayName(String paramString, Locale paramLocale)
/*     */     {
/* 514 */       CurrencyNameProvider localCurrencyNameProvider = getImpl(paramLocale);
/* 515 */       assert (localCurrencyNameProvider != null);
/* 516 */       return localCurrencyNameProvider.getDisplayName(paramString, paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class LocaleNameProviderDelegate extends LocaleNameProvider implements Delegate<LocaleNameProvider>
/*     */   {
/* 522 */     private ConcurrentMap<Locale, LocaleNameProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(LocaleNameProvider paramLocaleNameProvider)
/*     */     {
/* 526 */       for (Locale localLocale : paramLocaleNameProvider.getAvailableLocales()) {
/* 527 */         this.map.putIfAbsent(localLocale, paramLocaleNameProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public LocaleNameProvider getImpl(Locale paramLocale)
/*     */     {
/* 533 */       return (LocaleNameProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 538 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 543 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public String getDisplayLanguage(String paramString, Locale paramLocale)
/*     */     {
/* 548 */       LocaleNameProvider localLocaleNameProvider = getImpl(paramLocale);
/* 549 */       assert (localLocaleNameProvider != null);
/* 550 */       return localLocaleNameProvider.getDisplayLanguage(paramString, paramLocale);
/*     */     }
/*     */     
/*     */     public String getDisplayScript(String paramString, Locale paramLocale)
/*     */     {
/* 555 */       LocaleNameProvider localLocaleNameProvider = getImpl(paramLocale);
/* 556 */       assert (localLocaleNameProvider != null);
/* 557 */       return localLocaleNameProvider.getDisplayScript(paramString, paramLocale);
/*     */     }
/*     */     
/*     */     public String getDisplayCountry(String paramString, Locale paramLocale)
/*     */     {
/* 562 */       LocaleNameProvider localLocaleNameProvider = getImpl(paramLocale);
/* 563 */       assert (localLocaleNameProvider != null);
/* 564 */       return localLocaleNameProvider.getDisplayCountry(paramString, paramLocale);
/*     */     }
/*     */     
/*     */     public String getDisplayVariant(String paramString, Locale paramLocale)
/*     */     {
/* 569 */       LocaleNameProvider localLocaleNameProvider = getImpl(paramLocale);
/* 570 */       assert (localLocaleNameProvider != null);
/* 571 */       return localLocaleNameProvider.getDisplayVariant(paramString, paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static class TimeZoneNameProviderDelegate extends TimeZoneNameProvider implements Delegate<TimeZoneNameProvider>
/*     */   {
/* 577 */     private ConcurrentMap<Locale, TimeZoneNameProvider> map = new ConcurrentHashMap();
/*     */     
/*     */     public void addImpl(TimeZoneNameProvider paramTimeZoneNameProvider)
/*     */     {
/* 581 */       for (Locale localLocale : paramTimeZoneNameProvider.getAvailableLocales()) {
/* 582 */         this.map.putIfAbsent(localLocale, paramTimeZoneNameProvider);
/*     */       }
/*     */     }
/*     */     
/*     */     public TimeZoneNameProvider getImpl(Locale paramLocale)
/*     */     {
/* 588 */       return (TimeZoneNameProvider)SPILocaleProviderAdapter.getImpl(this.map, paramLocale);
/*     */     }
/*     */     
/*     */     public Locale[] getAvailableLocales()
/*     */     {
/* 593 */       return (Locale[])this.map.keySet().toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */     public boolean isSupportedLocale(Locale paramLocale)
/*     */     {
/* 598 */       return this.map.containsKey(paramLocale);
/*     */     }
/*     */     
/*     */     public String getDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale)
/*     */     {
/* 603 */       TimeZoneNameProvider localTimeZoneNameProvider = getImpl(paramLocale);
/* 604 */       assert (localTimeZoneNameProvider != null);
/* 605 */       return localTimeZoneNameProvider.getDisplayName(paramString, paramBoolean, paramInt, paramLocale);
/*     */     }
/*     */     
/*     */     public String getGenericDisplayName(String paramString, int paramInt, Locale paramLocale)
/*     */     {
/* 610 */       TimeZoneNameProvider localTimeZoneNameProvider = getImpl(paramLocale);
/* 611 */       assert (localTimeZoneNameProvider != null);
/* 612 */       return localTimeZoneNameProvider.getGenericDisplayName(paramString, paramInt, paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract interface Delegate<P extends LocaleServiceProvider>
/*     */   {
/*     */     public abstract void addImpl(P paramP);
/*     */     
/*     */     public abstract P getImpl(Locale paramLocale);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\SPILocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
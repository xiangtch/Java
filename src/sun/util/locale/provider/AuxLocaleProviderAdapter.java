/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.text.spi.BreakIteratorProvider;
/*     */ import java.text.spi.CollatorProvider;
/*     */ import java.text.spi.DateFormatProvider;
/*     */ import java.text.spi.DateFormatSymbolsProvider;
/*     */ import java.text.spi.DecimalFormatSymbolsProvider;
/*     */ import java.text.spi.NumberFormatProvider;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.spi.CalendarDataProvider;
/*     */ import java.util.spi.CalendarNameProvider;
/*     */ import java.util.spi.CurrencyNameProvider;
/*     */ import java.util.spi.LocaleNameProvider;
/*     */ import java.util.spi.LocaleServiceProvider;
/*     */ import java.util.spi.TimeZoneNameProvider;
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
/*     */ public abstract class AuxLocaleProviderAdapter
/*     */   extends LocaleProviderAdapter
/*     */ {
/*  59 */   private ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProvider> providersMap = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass)
/*     */   {
/*  68 */     LocaleServiceProvider localLocaleServiceProvider = (LocaleServiceProvider)this.providersMap.get(paramClass);
/*  69 */     if (localLocaleServiceProvider == null) {
/*  70 */       localLocaleServiceProvider = findInstalledProvider(paramClass);
/*  71 */       this.providersMap.putIfAbsent(paramClass, localLocaleServiceProvider == null ? NULL_PROVIDER : localLocaleServiceProvider);
/*     */     }
/*     */     
/*  74 */     return localLocaleServiceProvider;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract <P extends LocaleServiceProvider> P findInstalledProvider(Class<P> paramClass);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BreakIteratorProvider getBreakIteratorProvider()
/*     */   {
/*  88 */     return (BreakIteratorProvider)getLocaleServiceProvider(BreakIteratorProvider.class);
/*     */   }
/*     */   
/*     */   public CollatorProvider getCollatorProvider()
/*     */   {
/*  93 */     return (CollatorProvider)getLocaleServiceProvider(CollatorProvider.class);
/*     */   }
/*     */   
/*     */   public DateFormatProvider getDateFormatProvider()
/*     */   {
/*  98 */     return (DateFormatProvider)getLocaleServiceProvider(DateFormatProvider.class);
/*     */   }
/*     */   
/*     */   public DateFormatSymbolsProvider getDateFormatSymbolsProvider()
/*     */   {
/* 103 */     return (DateFormatSymbolsProvider)getLocaleServiceProvider(DateFormatSymbolsProvider.class);
/*     */   }
/*     */   
/*     */   public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider()
/*     */   {
/* 108 */     return (DecimalFormatSymbolsProvider)getLocaleServiceProvider(DecimalFormatSymbolsProvider.class);
/*     */   }
/*     */   
/*     */   public NumberFormatProvider getNumberFormatProvider()
/*     */   {
/* 113 */     return (NumberFormatProvider)getLocaleServiceProvider(NumberFormatProvider.class);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CurrencyNameProvider getCurrencyNameProvider()
/*     */   {
/* 121 */     return (CurrencyNameProvider)getLocaleServiceProvider(CurrencyNameProvider.class);
/*     */   }
/*     */   
/*     */   public LocaleNameProvider getLocaleNameProvider()
/*     */   {
/* 126 */     return (LocaleNameProvider)getLocaleServiceProvider(LocaleNameProvider.class);
/*     */   }
/*     */   
/*     */   public TimeZoneNameProvider getTimeZoneNameProvider()
/*     */   {
/* 131 */     return (TimeZoneNameProvider)getLocaleServiceProvider(TimeZoneNameProvider.class);
/*     */   }
/*     */   
/*     */   public CalendarDataProvider getCalendarDataProvider()
/*     */   {
/* 136 */     return (CalendarDataProvider)getLocaleServiceProvider(CalendarDataProvider.class);
/*     */   }
/*     */   
/*     */   public CalendarNameProvider getCalendarNameProvider()
/*     */   {
/* 141 */     return (CalendarNameProvider)getLocaleServiceProvider(CalendarNameProvider.class);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CalendarProvider getCalendarProvider()
/*     */   {
/* 149 */     return (CalendarProvider)getLocaleServiceProvider(CalendarProvider.class);
/*     */   }
/*     */   
/*     */   public LocaleResources getLocaleResources(Locale paramLocale)
/*     */   {
/* 154 */     return null;
/*     */   }
/*     */   
/* 157 */   private static Locale[] availableLocales = null;
/*     */   
/*     */   public Locale[] getAvailableLocales()
/*     */   {
/* 161 */     if (availableLocales == null) {
/* 162 */       HashSet localHashSet = new HashSet();
/*     */       
/* 164 */       for (Class localClass : LocaleServiceProviderPool.spiClasses) {
/* 165 */         LocaleServiceProvider localLocaleServiceProvider = getLocaleServiceProvider(localClass);
/* 166 */         if (localLocaleServiceProvider != null) {
/* 167 */           localHashSet.addAll(Arrays.asList(localLocaleServiceProvider.getAvailableLocales()));
/*     */         }
/*     */       }
/* 170 */       availableLocales = (Locale[])localHashSet.toArray(new Locale[0]);
/*     */     }
/*     */     
/*     */ 
/* 174 */     return availableLocales;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 181 */   private static NullProvider NULL_PROVIDER = new NullProvider(null);
/*     */   
/*     */   private static class NullProvider extends LocaleServiceProvider {
/*     */     public Locale[] getAvailableLocales() {
/* 185 */       return new Locale[0];
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\AuxLocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
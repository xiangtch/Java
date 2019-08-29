/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.text.spi.BreakIteratorProvider;
/*     */ import java.text.spi.CollatorProvider;
/*     */ import java.text.spi.DateFormatProvider;
/*     */ import java.text.spi.DateFormatSymbolsProvider;
/*     */ import java.text.spi.DecimalFormatSymbolsProvider;
/*     */ import java.text.spi.NumberFormatProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.IllformedLocaleException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Locale.Builder;
/*     */ import java.util.ResourceBundle.Control;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.spi.CalendarDataProvider;
/*     */ import java.util.spi.CurrencyNameProvider;
/*     */ import java.util.spi.LocaleNameProvider;
/*     */ import java.util.spi.LocaleServiceProvider;
/*     */ import java.util.spi.TimeZoneNameProvider;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class LocaleServiceProviderPool
/*     */ {
/*  56 */   private static ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProviderPool> poolOfPools = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  63 */   private ConcurrentMap<LocaleProviderAdapter.Type, LocaleServiceProvider> providers = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */   private ConcurrentMap<Locale, List<LocaleProviderAdapter.Type>> providersCache = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */   private Set<Locale> availableLocales = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Class<? extends LocaleServiceProvider> providerClass;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  91 */   static final Class<LocaleServiceProvider>[] spiClasses = (Class[])new Class[] { BreakIteratorProvider.class, CollatorProvider.class, DateFormatProvider.class, DateFormatSymbolsProvider.class, DecimalFormatSymbolsProvider.class, NumberFormatProvider.class, CurrencyNameProvider.class, LocaleNameProvider.class, TimeZoneNameProvider.class, CalendarDataProvider.class };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static LocaleServiceProviderPool getPool(Class<? extends LocaleServiceProvider> paramClass)
/*     */   {
/* 109 */     Object localObject = (LocaleServiceProviderPool)poolOfPools.get(paramClass);
/* 110 */     if (localObject == null) {
/* 111 */       LocaleServiceProviderPool localLocaleServiceProviderPool = new LocaleServiceProviderPool(paramClass);
/*     */       
/* 113 */       localObject = (LocaleServiceProviderPool)poolOfPools.putIfAbsent(paramClass, localLocaleServiceProviderPool);
/* 114 */       if (localObject == null) {
/* 115 */         localObject = localLocaleServiceProviderPool;
/*     */       }
/*     */     }
/*     */     
/* 119 */     return (LocaleServiceProviderPool)localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private LocaleServiceProviderPool(Class<? extends LocaleServiceProvider> paramClass)
/*     */   {
/* 128 */     this.providerClass = paramClass;
/*     */     
/* 130 */     for (LocaleProviderAdapter.Type localType : LocaleProviderAdapter.getAdapterPreference()) {
/* 131 */       LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.forType(localType);
/* 132 */       if (localLocaleProviderAdapter != null) {
/* 133 */         LocaleServiceProvider localLocaleServiceProvider = localLocaleProviderAdapter.getLocaleServiceProvider(paramClass);
/* 134 */         if (localLocaleServiceProvider != null) {
/* 135 */           this.providers.putIfAbsent(localType, localLocaleServiceProvider);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static void config(Class<? extends Object> paramClass, String paramString) {
/* 142 */     PlatformLogger localPlatformLogger = PlatformLogger.getLogger(paramClass.getCanonicalName());
/* 143 */     localPlatformLogger.config(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class AllAvailableLocales
/*     */   {
/*     */     static final Locale[] allAvailableLocales;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     static
/*     */     {
/* 158 */       HashSet localHashSet = new HashSet();
/* 159 */       for (Class localClass : LocaleServiceProviderPool.spiClasses)
/*     */       {
/* 161 */         LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(localClass);
/* 162 */         localHashSet.addAll(localLocaleServiceProviderPool.getAvailableLocaleSet());
/*     */       }
/*     */       
/* 165 */       allAvailableLocales = (Locale[])localHashSet.toArray(new Locale[0]);
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
/*     */   public static Locale[] getAllAvailableLocales()
/*     */   {
/* 181 */     return (Locale[])AllAvailableLocales.allAvailableLocales.clone();
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
/* 192 */     HashSet localHashSet = new HashSet();
/* 193 */     localHashSet.addAll(getAvailableLocaleSet());
/*     */     
/* 195 */     localHashSet.addAll(Arrays.asList(LocaleProviderAdapter.forJRE().getAvailableLocales()));
/* 196 */     Locale[] arrayOfLocale = new Locale[localHashSet.size()];
/* 197 */     localHashSet.toArray(arrayOfLocale);
/* 198 */     return arrayOfLocale;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized Set<Locale> getAvailableLocaleSet()
/*     */   {
/* 209 */     if (this.availableLocales == null) {
/* 210 */       this.availableLocales = new HashSet();
/* 211 */       for (LocaleServiceProvider localLocaleServiceProvider : this.providers.values()) {
/* 212 */         Locale[] arrayOfLocale1 = localLocaleServiceProvider.getAvailableLocales();
/* 213 */         for (Locale localLocale : arrayOfLocale1) {
/* 214 */           this.availableLocales.add(getLookupLocale(localLocale));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 219 */     return this.availableLocales;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean hasProviders()
/*     */   {
/* 229 */     return (this.providers.size() != 1) || (
/* 230 */       (this.providers.get(LocaleProviderAdapter.Type.JRE) == null) && 
/* 231 */       (this.providers.get(LocaleProviderAdapter.Type.FALLBACK) == null));
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
/*     */   public <P extends LocaleServiceProvider, S> S getLocalizedObject(LocalizedObjectGetter<P, S> paramLocalizedObjectGetter, Locale paramLocale, Object... paramVarArgs)
/*     */   {
/* 247 */     return (S)getLocalizedObjectImpl(paramLocalizedObjectGetter, paramLocale, true, null, paramVarArgs);
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
/*     */   public <P extends LocaleServiceProvider, S> S getLocalizedObject(LocalizedObjectGetter<P, S> paramLocalizedObjectGetter, Locale paramLocale, String paramString, Object... paramVarArgs)
/*     */   {
/* 265 */     return (S)getLocalizedObjectImpl(paramLocalizedObjectGetter, paramLocale, false, paramString, paramVarArgs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private <P extends LocaleServiceProvider, S> S getLocalizedObjectImpl(LocalizedObjectGetter<P, S> paramLocalizedObjectGetter, Locale paramLocale, boolean paramBoolean, String paramString, Object... paramVarArgs)
/*     */   {
/* 274 */     if (paramLocale == null) {
/* 275 */       throw new NullPointerException();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 280 */     if (!hasProviders()) {
/* 281 */       return (S)paramLocalizedObjectGetter.getObject((LocaleServiceProvider)this.providers.get(LocaleProviderAdapter.defaultLocaleProviderAdapter), paramLocale, paramString, paramVarArgs);
/*     */     }
/*     */     
/*     */ 
/* 285 */     List localList = getLookupLocales(paramLocale);
/*     */     
/* 287 */     Set localSet = getAvailableLocaleSet();
/* 288 */     for (Locale localLocale : localList) {
/* 289 */       if (localSet.contains(localLocale))
/*     */       {
/*     */ 
/* 292 */         for (LocaleProviderAdapter.Type localType : findProviders(localLocale)) {
/* 293 */           LocaleServiceProvider localLocaleServiceProvider = (LocaleServiceProvider)this.providers.get(localType);
/* 294 */           Object localObject = paramLocalizedObjectGetter.getObject(localLocaleServiceProvider, paramLocale, paramString, paramVarArgs);
/* 295 */           if (localObject != null)
/* 296 */             return (S)localObject;
/* 297 */           if (paramBoolean) {
/* 298 */             config(LocaleServiceProviderPool.class, "A locale sensitive service provider returned null for a localized objects,  which should not happen.  provider: " + localLocaleServiceProvider + " locale: " + paramLocale);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 307 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private List<LocaleProviderAdapter.Type> findProviders(Locale paramLocale)
/*     */   {
/* 318 */     Object localObject1 = (List)this.providersCache.get(paramLocale);
/* 319 */     if (localObject1 == null) {
/* 320 */       for (Object localObject2 = LocaleProviderAdapter.getAdapterPreference().iterator(); ((Iterator)localObject2).hasNext();) { LocaleProviderAdapter.Type localType = (LocaleProviderAdapter.Type)((Iterator)localObject2).next();
/* 321 */         LocaleServiceProvider localLocaleServiceProvider = (LocaleServiceProvider)this.providers.get(localType);
/* 322 */         if ((localLocaleServiceProvider != null) && 
/* 323 */           (localLocaleServiceProvider.isSupportedLocale(paramLocale))) {
/* 324 */           if (localObject1 == null) {
/* 325 */             localObject1 = new ArrayList(2);
/*     */           }
/* 327 */           ((List)localObject1).add(localType);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 332 */       if (localObject1 == null) {
/* 333 */         localObject1 = NULL_LIST;
/*     */       }
/* 335 */       localObject2 = (List)this.providersCache.putIfAbsent(paramLocale, localObject1);
/* 336 */       if (localObject2 != null) {
/* 337 */         localObject1 = localObject2;
/*     */       }
/*     */     }
/* 340 */     return (List<LocaleProviderAdapter.Type>)localObject1;
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
/*     */   static List<Locale> getLookupLocales(Locale paramLocale)
/*     */   {
/* 354 */     List localList = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", paramLocale);
/* 355 */     return localList;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Locale getLookupLocale(Locale paramLocale)
/*     */   {
/* 367 */     Locale localLocale = paramLocale;
/* 368 */     if ((paramLocale.hasExtensions()) && 
/* 369 */       (!paramLocale.equals(JRELocaleConstants.JA_JP_JP)) && 
/* 370 */       (!paramLocale.equals(JRELocaleConstants.TH_TH_TH)))
/*     */     {
/* 372 */       Builder localBuilder = new Builder();
/*     */       try {
/* 374 */         localBuilder.setLocale(paramLocale);
/* 375 */         localBuilder.clearExtensions();
/* 376 */         localLocale = localBuilder.build();
/*     */ 
/*     */       }
/*     */       catch (IllformedLocaleException localIllformedLocaleException)
/*     */       {
/*     */ 
/* 382 */         config(LocaleServiceProviderPool.class, "A locale(" + paramLocale + ") has non-empty extensions, but has illformed fields.");
/*     */         
/*     */ 
/*     */ 
/* 386 */         localLocale = new Locale(paramLocale.getLanguage(), paramLocale.getCountry(), paramLocale.getVariant());
/*     */       }
/*     */     }
/* 389 */     return localLocale;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 397 */   private static List<LocaleProviderAdapter.Type> NULL_LIST = Collections.emptyList();
/*     */   
/*     */   public static abstract interface LocalizedObjectGetter<P extends LocaleServiceProvider, S>
/*     */   {
/*     */     public abstract S getObject(P paramP, Locale paramLocale, String paramString, Object... paramVarArgs);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\LocaleServiceProviderPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
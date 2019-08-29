/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.text.spi.BreakIteratorProvider;
/*     */ import java.text.spi.CollatorProvider;
/*     */ import java.text.spi.DateFormatProvider;
/*     */ import java.text.spi.DateFormatSymbolsProvider;
/*     */ import java.text.spi.DecimalFormatSymbolsProvider;
/*     */ import java.text.spi.NumberFormatProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.ResourceBundle.Control;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.spi.CalendarDataProvider;
/*     */ import java.util.spi.CalendarNameProvider;
/*     */ import java.util.spi.CurrencyNameProvider;
/*     */ import java.util.spi.LocaleNameProvider;
/*     */ import java.util.spi.LocaleServiceProvider;
/*     */ import java.util.spi.TimeZoneNameProvider;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.util.cldr.CLDRLocaleProviderAdapter;
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
/*     */ public abstract class LocaleProviderAdapter
/*     */ {
/*     */   private static final List<Type> adapterPreference;
/*     */   private static LocaleProviderAdapter jreLocaleProviderAdapter;
/*     */   private static LocaleProviderAdapter spiLocaleProviderAdapter;
/*     */   private static LocaleProviderAdapter cldrLocaleProviderAdapter;
/*     */   private static LocaleProviderAdapter hostLocaleProviderAdapter;
/*     */   private static LocaleProviderAdapter fallbackLocaleProviderAdapter;
/*     */   static Type defaultLocaleProviderAdapter;
/*     */   private static ConcurrentMap<Class<? extends LocaleServiceProvider>, ConcurrentMap<Locale, LocaleProviderAdapter>> adapterCache;
/*     */   
/*     */   public static enum Type
/*     */   {
/*  63 */     JRE("sun.util.resources", "sun.text.resources"), 
/*  64 */     CLDR("sun.util.resources.cldr", "sun.text.resources.cldr"), 
/*  65 */     SPI, 
/*  66 */     HOST, 
/*  67 */     FALLBACK("sun.util.resources", "sun.text.resources");
/*     */     
/*     */     private final String UTIL_RESOURCES_PACKAGE;
/*     */     private final String TEXT_RESOURCES_PACKAGE;
/*     */     
/*     */     private Type() {
/*  73 */       this(null, null);
/*     */     }
/*     */     
/*     */     private Type(String paramString1, String paramString2) {
/*  77 */       this.UTIL_RESOURCES_PACKAGE = paramString1;
/*  78 */       this.TEXT_RESOURCES_PACKAGE = paramString2;
/*     */     }
/*     */     
/*     */     public String getUtilResourcesPackage() {
/*  82 */       return this.UTIL_RESOURCES_PACKAGE;
/*     */     }
/*     */     
/*     */     public String getTextResourcesPackage() {
/*  86 */       return this.TEXT_RESOURCES_PACKAGE;
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
/*     */   static
/*     */   {
/*  99 */     jreLocaleProviderAdapter = new JRELocaleProviderAdapter();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 104 */     spiLocaleProviderAdapter = new SPILocaleProviderAdapter();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 109 */     cldrLocaleProviderAdapter = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 114 */     hostLocaleProviderAdapter = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 120 */     fallbackLocaleProviderAdapter = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 126 */     defaultLocaleProviderAdapter = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 132 */     adapterCache = new ConcurrentHashMap();
/*     */     
/*     */ 
/* 135 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("java.locale.providers"));
/*     */     
/* 137 */     ArrayList localArrayList = new ArrayList();
/*     */     
/*     */ 
/* 140 */     if ((str1 != null) && (str1.length() != 0)) {
/* 141 */       String[] arrayOfString1 = str1.split(",");
/* 142 */       for (String str2 : arrayOfString1) {
/*     */         try {
/* 144 */           Type localType = Type.valueOf(str2.trim().toUpperCase(Locale.ROOT));
/*     */           
/*     */ 
/* 147 */           switch (localType) {
/*     */           case CLDR: 
/* 149 */             if (cldrLocaleProviderAdapter == null) {
/* 150 */               cldrLocaleProviderAdapter = new CLDRLocaleProviderAdapter();
/*     */             }
/*     */             break;
/*     */           case HOST: 
/* 154 */             if (hostLocaleProviderAdapter == null) {
/* 155 */               hostLocaleProviderAdapter = new HostLocaleProviderAdapter();
/*     */             }
/*     */             break;
/*     */           }
/* 159 */           if (!localArrayList.contains(localType)) {
/* 160 */             localArrayList.add(localType);
/*     */           }
/*     */         }
/*     */         catch (IllegalArgumentException|UnsupportedOperationException localIllegalArgumentException)
/*     */         {
/* 165 */           LocaleServiceProviderPool.config(LocaleProviderAdapter.class, localIllegalArgumentException.toString());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 170 */     if (!localArrayList.isEmpty()) {
/* 171 */       if (!localArrayList.contains(Type.JRE))
/*     */       {
/* 173 */         fallbackLocaleProviderAdapter = new FallbackLocaleProviderAdapter();
/* 174 */         localArrayList.add(Type.FALLBACK);
/* 175 */         defaultLocaleProviderAdapter = Type.FALLBACK;
/*     */       } else {
/* 177 */         defaultLocaleProviderAdapter = Type.JRE;
/*     */       }
/*     */     }
/*     */     else {
/* 181 */       localArrayList.add(Type.JRE);
/* 182 */       localArrayList.add(Type.SPI);
/* 183 */       defaultLocaleProviderAdapter = Type.JRE;
/*     */     }
/*     */     
/* 186 */     adapterPreference = Collections.unmodifiableList(localArrayList);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static LocaleProviderAdapter forType(Type paramType)
/*     */   {
/* 193 */     switch (paramType) {
/*     */     case JRE: 
/* 195 */       return jreLocaleProviderAdapter;
/*     */     case CLDR: 
/* 197 */       return cldrLocaleProviderAdapter;
/*     */     case SPI: 
/* 199 */       return spiLocaleProviderAdapter;
/*     */     case HOST: 
/* 201 */       return hostLocaleProviderAdapter;
/*     */     case FALLBACK: 
/* 203 */       return fallbackLocaleProviderAdapter;
/*     */     }
/* 205 */     throw new InternalError("unknown locale data adapter type");
/*     */   }
/*     */   
/*     */   public static LocaleProviderAdapter forJRE()
/*     */   {
/* 210 */     return jreLocaleProviderAdapter;
/*     */   }
/*     */   
/*     */   public static LocaleProviderAdapter getResourceBundleBased() {
/* 214 */     for (Type localType : ) {
/* 215 */       if ((localType == Type.JRE) || (localType == Type.CLDR) || (localType == Type.FALLBACK)) {
/* 216 */         return forType(localType);
/*     */       }
/*     */     }
/*     */     
/* 220 */     throw new InternalError();
/*     */   }
/*     */   
/*     */ 
/*     */   public static List<Type> getAdapterPreference()
/*     */   {
/* 226 */     return adapterPreference;
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
/*     */   public static LocaleProviderAdapter getAdapter(Class<? extends LocaleServiceProvider> paramClass, Locale paramLocale)
/*     */   {
/* 243 */     Object localObject = (ConcurrentMap)adapterCache.get(paramClass);
/* 244 */     if (localObject != null) {
/* 245 */       if ((localLocaleProviderAdapter = (LocaleProviderAdapter)((ConcurrentMap)localObject).get(paramLocale)) != null) {
/* 246 */         return localLocaleProviderAdapter;
/*     */       }
/*     */     } else {
/* 249 */       localObject = new ConcurrentHashMap();
/* 250 */       adapterCache.putIfAbsent(paramClass, localObject);
/*     */     }
/*     */     
/*     */ 
/* 254 */     LocaleProviderAdapter localLocaleProviderAdapter = findAdapter(paramClass, paramLocale);
/* 255 */     if (localLocaleProviderAdapter != null) {
/* 256 */       ((ConcurrentMap)localObject).putIfAbsent(paramLocale, localLocaleProviderAdapter);
/* 257 */       return localLocaleProviderAdapter;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 262 */     List localList = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", paramLocale);
/* 263 */     for (Locale localLocale : localList) {
/* 264 */       if (!localLocale.equals(paramLocale))
/*     */       {
/*     */ 
/*     */ 
/* 268 */         localLocaleProviderAdapter = findAdapter(paramClass, localLocale);
/* 269 */         if (localLocaleProviderAdapter != null) {
/* 270 */           ((ConcurrentMap)localObject).putIfAbsent(paramLocale, localLocaleProviderAdapter);
/* 271 */           return localLocaleProviderAdapter;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 276 */     ((ConcurrentMap)localObject).putIfAbsent(paramLocale, fallbackLocaleProviderAdapter);
/* 277 */     return fallbackLocaleProviderAdapter;
/*     */   }
/*     */   
/*     */   private static LocaleProviderAdapter findAdapter(Class<? extends LocaleServiceProvider> paramClass, Locale paramLocale)
/*     */   {
/* 282 */     for (Type localType : ) {
/* 283 */       LocaleProviderAdapter localLocaleProviderAdapter = forType(localType);
/* 284 */       LocaleServiceProvider localLocaleServiceProvider = localLocaleProviderAdapter.getLocaleServiceProvider(paramClass);
/* 285 */       if ((localLocaleServiceProvider != null) && 
/* 286 */         (localLocaleServiceProvider.isSupportedLocale(paramLocale))) {
/* 287 */         return localLocaleProviderAdapter;
/*     */       }
/*     */     }
/*     */     
/* 291 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isSupportedLocale(Locale paramLocale, Type paramType, Set<String> paramSet)
/*     */   {
/* 299 */     assert ((paramType == Type.JRE) || (paramType == Type.CLDR) || (paramType == Type.FALLBACK));
/* 300 */     if (Locale.ROOT.equals(paramLocale)) {
/* 301 */       return true;
/*     */     }
/*     */     
/* 304 */     if (paramType == Type.FALLBACK)
/*     */     {
/* 306 */       return false;
/*     */     }
/*     */     
/* 309 */     paramLocale = paramLocale.stripExtensions();
/* 310 */     if (paramSet.contains(paramLocale.toLanguageTag())) {
/* 311 */       return true;
/*     */     }
/* 313 */     if (paramType == Type.JRE) {
/* 314 */       String str = paramLocale.toString().replace('_', '-');
/* 315 */       return (paramSet.contains(str)) || 
/* 316 */         ("ja-JP-JP".equals(str)) || 
/* 317 */         ("th-TH-TH".equals(str)) || 
/* 318 */         ("no-NO-NY".equals(str));
/*     */     }
/* 320 */     return false;
/*     */   }
/*     */   
/*     */   public static Locale[] toLocaleArray(Set<String> paramSet) {
/* 324 */     Locale[] arrayOfLocale = new Locale[paramSet.size() + 1];
/* 325 */     int i = 0;
/* 326 */     arrayOfLocale[(i++)] = Locale.ROOT;
/* 327 */     for (String str1 : paramSet) {
/* 328 */       switch (str1) {
/*     */       case "ja-JP-JP": 
/* 330 */         arrayOfLocale[(i++)] = JRELocaleConstants.JA_JP_JP;
/* 331 */         break;
/*     */       case "th-TH-TH": 
/* 333 */         arrayOfLocale[(i++)] = JRELocaleConstants.TH_TH_TH;
/* 334 */         break;
/*     */       default: 
/* 336 */         arrayOfLocale[(i++)] = Locale.forLanguageTag(str1);
/*     */       }
/*     */       
/*     */     }
/* 340 */     return arrayOfLocale;
/*     */   }
/*     */   
/*     */   public abstract Type getAdapterType();
/*     */   
/*     */   public abstract <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass);
/*     */   
/*     */   public abstract BreakIteratorProvider getBreakIteratorProvider();
/*     */   
/*     */   public abstract CollatorProvider getCollatorProvider();
/*     */   
/*     */   public abstract DateFormatProvider getDateFormatProvider();
/*     */   
/*     */   public abstract DateFormatSymbolsProvider getDateFormatSymbolsProvider();
/*     */   
/*     */   public abstract DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider();
/*     */   
/*     */   public abstract NumberFormatProvider getNumberFormatProvider();
/*     */   
/*     */   public abstract CurrencyNameProvider getCurrencyNameProvider();
/*     */   
/*     */   public abstract LocaleNameProvider getLocaleNameProvider();
/*     */   
/*     */   public abstract TimeZoneNameProvider getTimeZoneNameProvider();
/*     */   
/*     */   public abstract CalendarDataProvider getCalendarDataProvider();
/*     */   
/*     */   public abstract CalendarNameProvider getCalendarNameProvider();
/*     */   
/*     */   public abstract CalendarProvider getCalendarProvider();
/*     */   
/*     */   public abstract LocaleResources getLocaleResources(Locale paramLocale);
/*     */   
/*     */   public abstract Locale[] getAvailableLocales();
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\LocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
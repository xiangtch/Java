/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import sun.util.calendar.ZoneInfo;
/*     */ import sun.util.resources.LocaleData;
/*     */ import sun.util.resources.OpenListResourceBundle;
/*     */ import sun.util.resources.ParallelListResourceBundle;
/*     */ import sun.util.resources.TimeZoneNamesBundle;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocaleResources
/*     */ {
/*     */   private final Locale locale;
/*     */   private final LocaleData localeData;
/*     */   private final LocaleProviderAdapter.Type type;
/*  74 */   private ConcurrentMap<String, ResourceReference> cache = new ConcurrentHashMap();
/*  75 */   private ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
/*     */   
/*     */   private static final String BREAK_ITERATOR_INFO = "BII.";
/*     */   
/*     */   private static final String CALENDAR_DATA = "CALD.";
/*     */   
/*     */   private static final String COLLATION_DATA_CACHEKEY = "COLD";
/*     */   
/*     */   private static final String DECIMAL_FORMAT_SYMBOLS_DATA_CACHEKEY = "DFSD";
/*     */   private static final String CURRENCY_NAMES = "CN.";
/*     */   private static final String LOCALE_NAMES = "LN.";
/*     */   private static final String TIME_ZONE_NAMES = "TZN.";
/*     */   private static final String ZONE_IDS_CACHEKEY = "ZID";
/*     */   private static final String CALENDAR_NAMES = "CALN.";
/*     */   private static final String NUMBER_PATTERNS_CACHEKEY = "NP";
/*     */   private static final String DATE_TIME_PATTERN = "DTP.";
/*  91 */   private static final Object NULLOBJECT = new Object();
/*     */   
/*     */   LocaleResources(ResourceBundleBasedAdapter paramResourceBundleBasedAdapter, Locale paramLocale) {
/*  94 */     this.locale = paramLocale;
/*  95 */     this.localeData = paramResourceBundleBasedAdapter.getLocaleData();
/*  96 */     this.type = ((LocaleProviderAdapter)paramResourceBundleBasedAdapter).getAdapterType();
/*     */   }
/*     */   
/*     */   private void removeEmptyReferences() {
/*     */     Reference localReference;
/* 101 */     while ((localReference = this.referenceQueue.poll()) != null) {
/* 102 */       this.cache.remove(((ResourceReference)localReference).getCacheKey());
/*     */     }
/*     */   }
/*     */   
/*     */   Object getBreakIteratorInfo(String paramString)
/*     */   {
/* 108 */     String str = "BII." + paramString;
/*     */     
/* 110 */     removeEmptyReferences();
/* 111 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
/* 112 */     Object localObject; if ((localResourceReference == null) || ((localObject = localResourceReference.get()) == null)) {
/* 113 */       localObject = this.localeData.getBreakIteratorInfo(this.locale).getObject(paramString);
/* 114 */       this.cache.put(str, new ResourceReference(str, localObject, this.referenceQueue));
/*     */     }
/*     */     
/* 117 */     return localObject;
/*     */   }
/*     */   
/*     */   int getCalendarData(String paramString)
/*     */   {
/* 122 */     String str = "CALD." + paramString;
/*     */     
/* 124 */     removeEmptyReferences();
/*     */     
/* 126 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
/* 127 */     Integer localInteger; if ((localResourceReference == null) || ((localInteger = (Integer)localResourceReference.get()) == null)) {
/* 128 */       ResourceBundle localResourceBundle = this.localeData.getCalendarData(this.locale);
/* 129 */       if (localResourceBundle.containsKey(paramString)) {
/* 130 */         localInteger = Integer.valueOf(Integer.parseInt(localResourceBundle.getString(paramString)));
/*     */       } else {
/* 132 */         localInteger = Integer.valueOf(0);
/*     */       }
/*     */       
/* 135 */       this.cache.put(str, new ResourceReference(str, localInteger, this.referenceQueue));
/*     */     }
/*     */     
/*     */ 
/* 139 */     return localInteger.intValue();
/*     */   }
/*     */   
/*     */   public String getCollationData() {
/* 143 */     String str1 = "Rule";
/* 144 */     String str2 = "";
/*     */     
/* 146 */     removeEmptyReferences();
/* 147 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get("COLD");
/* 148 */     if ((localResourceReference == null) || ((str2 = (String)localResourceReference.get()) == null)) {
/* 149 */       ResourceBundle localResourceBundle = this.localeData.getCollationData(this.locale);
/* 150 */       if (localResourceBundle.containsKey(str1)) {
/* 151 */         str2 = localResourceBundle.getString(str1);
/*     */       }
/* 153 */       this.cache.put("COLD", new ResourceReference("COLD", str2, this.referenceQueue));
/*     */     }
/*     */     
/*     */ 
/* 157 */     return str2;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object[] getDecimalFormatSymbolsData()
/*     */   {
/* 163 */     removeEmptyReferences();
/* 164 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get("DFSD");
/* 165 */     Object[] arrayOfObject; if ((localResourceReference == null) || ((arrayOfObject = (Object[])localResourceReference.get()) == null))
/*     */     {
/*     */ 
/* 168 */       ResourceBundle localResourceBundle = this.localeData.getNumberFormatData(this.locale);
/* 169 */       arrayOfObject = new Object[3];
/*     */       
/*     */ 
/*     */ 
/* 173 */       String str2 = this.locale.getUnicodeLocaleType("nu");
/* 174 */       String str1; if (str2 != null) {
/* 175 */         str1 = str2 + ".NumberElements";
/* 176 */         if (localResourceBundle.containsKey(str1)) {
/* 177 */           arrayOfObject[0] = localResourceBundle.getStringArray(str1);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 182 */       if ((arrayOfObject[0] == null) && (localResourceBundle.containsKey("DefaultNumberingSystem"))) {
/* 183 */         str1 = localResourceBundle.getString("DefaultNumberingSystem") + ".NumberElements";
/* 184 */         if (localResourceBundle.containsKey(str1)) {
/* 185 */           arrayOfObject[0] = localResourceBundle.getStringArray(str1);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 191 */       if (arrayOfObject[0] == null) {
/* 192 */         arrayOfObject[0] = localResourceBundle.getStringArray("NumberElements");
/*     */       }
/*     */       
/* 195 */       this.cache.put("DFSD", new ResourceReference("DFSD", arrayOfObject, this.referenceQueue));
/*     */     }
/*     */     
/*     */ 
/* 199 */     return arrayOfObject;
/*     */   }
/*     */   
/*     */   public String getCurrencyName(String paramString) {
/* 203 */     Object localObject = null;
/* 204 */     String str = "CN." + paramString;
/*     */     
/* 206 */     removeEmptyReferences();
/* 207 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
/*     */     
/* 209 */     if ((localResourceReference != null) && ((localObject = localResourceReference.get()) != null)) {
/* 210 */       if (localObject.equals(NULLOBJECT)) {
/* 211 */         localObject = null;
/*     */       }
/*     */       
/* 214 */       return (String)localObject;
/*     */     }
/*     */     
/* 217 */     OpenListResourceBundle localOpenListResourceBundle = this.localeData.getCurrencyNames(this.locale);
/*     */     
/* 219 */     if (localOpenListResourceBundle.containsKey(paramString)) {
/* 220 */       localObject = localOpenListResourceBundle.getObject(paramString);
/* 221 */       this.cache.put(str, new ResourceReference(str, localObject, this.referenceQueue));
/*     */     }
/*     */     
/*     */ 
/* 225 */     return (String)localObject;
/*     */   }
/*     */   
/*     */   public String getLocaleName(String paramString) {
/* 229 */     Object localObject = null;
/* 230 */     String str = "LN." + paramString;
/*     */     
/* 232 */     removeEmptyReferences();
/* 233 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
/*     */     
/* 235 */     if ((localResourceReference != null) && ((localObject = localResourceReference.get()) != null)) {
/* 236 */       if (localObject.equals(NULLOBJECT)) {
/* 237 */         localObject = null;
/*     */       }
/*     */       
/* 240 */       return (String)localObject;
/*     */     }
/*     */     
/* 243 */     OpenListResourceBundle localOpenListResourceBundle = this.localeData.getLocaleNames(this.locale);
/*     */     
/* 245 */     if (localOpenListResourceBundle.containsKey(paramString)) {
/* 246 */       localObject = localOpenListResourceBundle.getObject(paramString);
/* 247 */       this.cache.put(str, new ResourceReference(str, localObject, this.referenceQueue));
/*     */     }
/*     */     
/*     */ 
/* 251 */     return (String)localObject;
/*     */   }
/*     */   
/*     */   String[] getTimeZoneNames(String paramString) {
/* 255 */     String[] arrayOfString = null;
/* 256 */     String str = "TZN.." + paramString;
/*     */     
/* 258 */     removeEmptyReferences();
/* 259 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
/*     */     
/* 261 */     if ((Objects.isNull(localResourceReference)) || (Objects.isNull(arrayOfString = (String[])localResourceReference.get()))) {
/* 262 */       TimeZoneNamesBundle localTimeZoneNamesBundle = this.localeData.getTimeZoneNames(this.locale);
/* 263 */       if (localTimeZoneNamesBundle.containsKey(paramString)) {
/* 264 */         arrayOfString = localTimeZoneNamesBundle.getStringArray(paramString);
/* 265 */         this.cache.put(str, new ResourceReference(str, arrayOfString, this.referenceQueue));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 270 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   Set<String> getZoneIDs()
/*     */   {
/* 275 */     Set localSet = null;
/*     */     
/* 277 */     removeEmptyReferences();
/* 278 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get("ZID");
/* 279 */     if ((localResourceReference == null) || ((localSet = (Set)localResourceReference.get()) == null)) {
/* 280 */       TimeZoneNamesBundle localTimeZoneNamesBundle = this.localeData.getTimeZoneNames(this.locale);
/* 281 */       localSet = localTimeZoneNamesBundle.keySet();
/* 282 */       this.cache.put("ZID", new ResourceReference("ZID", localSet, this.referenceQueue));
/*     */     }
/*     */     
/*     */ 
/* 286 */     return localSet;
/*     */   }
/*     */   
/*     */   String[][] getZoneStrings()
/*     */   {
/* 291 */     TimeZoneNamesBundle localTimeZoneNamesBundle = this.localeData.getTimeZoneNames(this.locale);
/* 292 */     Set localSet = getZoneIDs();
/*     */     
/* 294 */     LinkedHashSet localLinkedHashSet = new LinkedHashSet();
/* 295 */     for (Object localObject1 = localSet.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (String)((Iterator)localObject1).next();
/* 296 */       localLinkedHashSet.add(localTimeZoneNamesBundle.getStringArray((String)localObject2));
/*     */     }
/*     */     
/*     */     Object localObject2;
/* 300 */     if (this.type == LocaleProviderAdapter.Type.CLDR)
/*     */     {
/* 302 */       localObject1 = ZoneInfo.getAliasTable();
/* 303 */       for (localObject2 = ((Map)localObject1).keySet().iterator(); ((Iterator)localObject2).hasNext();) { String str1 = (String)((Iterator)localObject2).next();
/* 304 */         if (!localSet.contains(str1)) {
/* 305 */           String str2 = (String)((Map)localObject1).get(str1);
/* 306 */           if (localSet.contains(str2)) {
/* 307 */             String[] arrayOfString = localTimeZoneNamesBundle.getStringArray(str2);
/* 308 */             arrayOfString[0] = str1;
/* 309 */             localLinkedHashSet.add(arrayOfString);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 314 */     return (String[][])localLinkedHashSet.toArray(new String[0][]);
/*     */   }
/*     */   
/*     */   String[] getCalendarNames(String paramString) {
/* 318 */     String[] arrayOfString = null;
/* 319 */     String str = "CALN." + paramString;
/*     */     
/* 321 */     removeEmptyReferences();
/* 322 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
/*     */     
/* 324 */     if ((localResourceReference == null) || ((arrayOfString = (String[])localResourceReference.get()) == null)) {
/* 325 */       ResourceBundle localResourceBundle = this.localeData.getDateFormatData(this.locale);
/* 326 */       if (localResourceBundle.containsKey(paramString)) {
/* 327 */         arrayOfString = localResourceBundle.getStringArray(paramString);
/* 328 */         this.cache.put(str, new ResourceReference(str, arrayOfString, this.referenceQueue));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 333 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   String[] getJavaTimeNames(String paramString) {
/* 337 */     String[] arrayOfString = null;
/* 338 */     String str = "CALN." + paramString;
/*     */     
/* 340 */     removeEmptyReferences();
/* 341 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get(str);
/*     */     
/* 343 */     if ((localResourceReference == null) || ((arrayOfString = (String[])localResourceReference.get()) == null)) {
/* 344 */       ResourceBundle localResourceBundle = getJavaTimeFormatData();
/* 345 */       if (localResourceBundle.containsKey(paramString)) {
/* 346 */         arrayOfString = localResourceBundle.getStringArray(paramString);
/* 347 */         this.cache.put(str, new ResourceReference(str, arrayOfString, this.referenceQueue));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 352 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   public String getDateTimePattern(int paramInt1, int paramInt2, Calendar paramCalendar) {
/* 356 */     if (paramCalendar == null) {
/* 357 */       paramCalendar = Calendar.getInstance(this.locale);
/*     */     }
/* 359 */     return getDateTimePattern(null, paramInt1, paramInt2, paramCalendar.getCalendarType());
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
/*     */   public String getJavaTimeDateTimePattern(int paramInt1, int paramInt2, String paramString)
/*     */   {
/* 372 */     paramString = CalendarDataUtility.normalizeCalendarType(paramString);
/*     */     
/* 374 */     String str = getDateTimePattern("java.time.", paramInt1, paramInt2, paramString);
/* 375 */     if (str == null) {
/* 376 */       str = getDateTimePattern(null, paramInt1, paramInt2, paramString);
/*     */     }
/* 378 */     return str;
/*     */   }
/*     */   
/*     */   private String getDateTimePattern(String paramString1, int paramInt1, int paramInt2, String paramString2)
/*     */   {
/* 383 */     String str2 = null;
/* 384 */     String str3 = null;
/*     */     
/* 386 */     if (paramInt1 >= 0) {
/* 387 */       if (paramString1 != null) {
/* 388 */         str2 = getDateTimePattern(paramString1, "TimePatterns", paramInt1, paramString2);
/*     */       }
/* 390 */       if (str2 == null) {
/* 391 */         str2 = getDateTimePattern(null, "TimePatterns", paramInt1, paramString2);
/*     */       }
/*     */     }
/* 394 */     if (paramInt2 >= 0) {
/* 395 */       if (paramString1 != null) {
/* 396 */         str3 = getDateTimePattern(paramString1, "DatePatterns", paramInt2, paramString2);
/*     */       }
/* 398 */       if (str3 == null)
/* 399 */         str3 = getDateTimePattern(null, "DatePatterns", paramInt2, paramString2);
/*     */     }
/*     */     String str1;
/* 402 */     if (paramInt1 >= 0) {
/* 403 */       if (paramInt2 >= 0) {
/* 404 */         String str4 = null;
/* 405 */         if (paramString1 != null) {
/* 406 */           str4 = getDateTimePattern(paramString1, "DateTimePatterns", 0, paramString2);
/*     */         }
/* 408 */         if (str4 == null) {
/* 409 */           str4 = getDateTimePattern(null, "DateTimePatterns", 0, paramString2);
/*     */         }
/* 411 */         switch (str4) {
/*     */         case "{1} {0}": 
/* 413 */           str1 = str3 + " " + str2;
/* 414 */           break;
/*     */         case "{0} {1}": 
/* 416 */           str1 = str2 + " " + str3;
/* 417 */           break;
/*     */         default: 
/* 419 */           str1 = MessageFormat.format(str4, new Object[] { str2, str3 });
/*     */         }
/*     */       }
/*     */       else {
/* 423 */         str1 = str2;
/*     */       }
/* 425 */     } else if (paramInt2 >= 0) {
/* 426 */       str1 = str3;
/*     */     } else {
/* 428 */       throw new IllegalArgumentException("No date or time style specified");
/*     */     }
/* 430 */     return str1;
/*     */   }
/*     */   
/*     */   public String[] getNumberPatterns() {
/* 434 */     String[] arrayOfString = null;
/*     */     
/* 436 */     removeEmptyReferences();
/* 437 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get("NP");
/*     */     
/* 439 */     if ((localResourceReference == null) || ((arrayOfString = (String[])localResourceReference.get()) == null)) {
/* 440 */       ResourceBundle localResourceBundle = this.localeData.getNumberFormatData(this.locale);
/* 441 */       arrayOfString = localResourceBundle.getStringArray("NumberPatterns");
/* 442 */       this.cache.put("NP", new ResourceReference("NP", arrayOfString, this.referenceQueue));
/*     */     }
/*     */     
/*     */ 
/* 446 */     return arrayOfString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceBundle getJavaTimeFormatData()
/*     */   {
/* 455 */     ResourceBundle localResourceBundle = this.localeData.getDateFormatData(this.locale);
/* 456 */     if ((localResourceBundle instanceof ParallelListResourceBundle)) {
/* 457 */       this.localeData.setSupplementary((ParallelListResourceBundle)localResourceBundle);
/*     */     }
/* 459 */     return localResourceBundle;
/*     */   }
/*     */   
/*     */   private String getDateTimePattern(String paramString1, String paramString2, int paramInt, String paramString3) {
/* 463 */     StringBuilder localStringBuilder = new StringBuilder();
/* 464 */     if (paramString1 != null) {
/* 465 */       localStringBuilder.append(paramString1);
/*     */     }
/* 467 */     if (!"gregory".equals(paramString3)) {
/* 468 */       localStringBuilder.append(paramString3).append('.');
/*     */     }
/* 470 */     localStringBuilder.append(paramString2);
/* 471 */     String str1 = localStringBuilder.toString();
/* 472 */     String str2 = localStringBuilder.insert(0, "DTP.").toString();
/*     */     
/* 474 */     removeEmptyReferences();
/* 475 */     ResourceReference localResourceReference = (ResourceReference)this.cache.get(str2);
/* 476 */     Object localObject = NULLOBJECT;
/*     */     
/* 478 */     if ((localResourceReference == null) || ((localObject = localResourceReference.get()) == null)) {
/* 479 */       ResourceBundle localResourceBundle = paramString1 != null ? getJavaTimeFormatData() : this.localeData.getDateFormatData(this.locale);
/* 480 */       if (localResourceBundle.containsKey(str1)) {
/* 481 */         localObject = localResourceBundle.getStringArray(str1);
/*     */       } else {
/* 483 */         assert (!str1.equals(paramString2));
/* 484 */         if (localResourceBundle.containsKey(paramString2)) {
/* 485 */           localObject = localResourceBundle.getStringArray(paramString2);
/*     */         }
/*     */       }
/* 488 */       this.cache.put(str2, new ResourceReference(str2, localObject, this.referenceQueue));
/*     */     }
/*     */     
/* 491 */     if (localObject == NULLOBJECT) {
/* 492 */       assert (paramString1 != null);
/* 493 */       return null;
/*     */     }
/* 495 */     return ((String[])(String[])localObject)[paramInt];
/*     */   }
/*     */   
/*     */   private static class ResourceReference extends SoftReference<Object> {
/*     */     private final String cacheKey;
/*     */     
/*     */     ResourceReference(String paramString, Object paramObject, ReferenceQueue<Object> paramReferenceQueue) {
/* 502 */       super(paramReferenceQueue);
/* 503 */       this.cacheKey = paramString;
/*     */     }
/*     */     
/*     */     String getCacheKey() {
/* 507 */       return this.cacheKey;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\LocaleResources.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
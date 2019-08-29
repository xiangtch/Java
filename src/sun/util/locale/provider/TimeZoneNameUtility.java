/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.spi.TimeZoneNameProvider;
/*     */ import sun.util.calendar.ZoneInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TimeZoneNameUtility
/*     */ {
/*  50 */   private static ConcurrentHashMap<Locale, SoftReference<String[][]>> cachedZoneData = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  58 */   private static final Map<String, SoftReference<Map<Locale, String[]>>> cachedDisplayNames = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String[][] getZoneStrings(Locale paramLocale)
/*     */   {
/*  66 */     SoftReference localSoftReference = (SoftReference)cachedZoneData.get(paramLocale);
/*     */     String[][] arrayOfString;
/*  68 */     if ((localSoftReference == null) || ((arrayOfString = (String[][])localSoftReference.get()) == null)) {
/*  69 */       arrayOfString = loadZoneStrings(paramLocale);
/*  70 */       localSoftReference = new SoftReference(arrayOfString);
/*  71 */       cachedZoneData.put(paramLocale, localSoftReference);
/*     */     }
/*     */     
/*  74 */     return arrayOfString;
/*     */   }
/*     */   
/*     */ 
/*     */   private static String[][] loadZoneStrings(Locale paramLocale)
/*     */   {
/*  80 */     LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(TimeZoneNameProvider.class, paramLocale);
/*  81 */     TimeZoneNameProvider localTimeZoneNameProvider = localLocaleProviderAdapter.getTimeZoneNameProvider();
/*  82 */     if ((localTimeZoneNameProvider instanceof TimeZoneNameProviderImpl)) {
/*  83 */       return ((TimeZoneNameProviderImpl)localTimeZoneNameProvider).getZoneStrings(paramLocale);
/*     */     }
/*     */     
/*     */ 
/*  87 */     Set localSet = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale).getZoneIDs();
/*  88 */     LinkedList localLinkedList = new LinkedList();
/*  89 */     for (Object localObject = localSet.iterator(); ((Iterator)localObject).hasNext();) { String str = (String)((Iterator)localObject).next();
/*  90 */       String[] arrayOfString = retrieveDisplayNamesImpl(str, paramLocale);
/*  91 */       if (arrayOfString != null) {
/*  92 */         localLinkedList.add(arrayOfString);
/*     */       }
/*     */     }
/*     */     
/*  96 */     localObject = new String[localLinkedList.size()][];
/*  97 */     return (String[][])localLinkedList.toArray((Object[])localObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String[] retrieveDisplayNames(String paramString, Locale paramLocale)
/*     */   {
/* 104 */     Objects.requireNonNull(paramString);
/* 105 */     Objects.requireNonNull(paramLocale);
/*     */     
/* 107 */     return retrieveDisplayNamesImpl(paramString, paramLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String retrieveGenericDisplayName(String paramString, int paramInt, Locale paramLocale)
/*     */   {
/* 119 */     String[] arrayOfString = retrieveDisplayNamesImpl(paramString, paramLocale);
/* 120 */     if (Objects.nonNull(arrayOfString)) {
/* 121 */       return arrayOfString[(6 - paramInt)];
/*     */     }
/* 123 */     return null;
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
/*     */   public static String retrieveDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale)
/*     */   {
/* 137 */     String[] arrayOfString = retrieveDisplayNamesImpl(paramString, paramLocale);
/* 138 */     if (Objects.nonNull(arrayOfString)) {
/* 139 */       return arrayOfString[(2 - paramInt)];
/*     */     }
/* 141 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private static String[] retrieveDisplayNamesImpl(String paramString, Locale paramLocale)
/*     */   {
/* 147 */     LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(TimeZoneNameProvider.class);
/*     */     
/* 149 */     Object localObject = null;
/*     */     
/* 151 */     SoftReference localSoftReference = (SoftReference)cachedDisplayNames.get(paramString);
/* 152 */     if (Objects.nonNull(localSoftReference)) {
/* 153 */       localObject = (Map)localSoftReference.get();
/* 154 */       if (Objects.nonNull(localObject)) {
/* 155 */         arrayOfString = (String[])((Map)localObject).get(paramLocale);
/* 156 */         if (Objects.nonNull(arrayOfString)) {
/* 157 */           return arrayOfString;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 163 */     String[] arrayOfString = new String[7];
/* 164 */     arrayOfString[0] = paramString;
/* 165 */     for (int i = 1; i <= 6; i++) {
/* 166 */       arrayOfString[i] = ((String)localLocaleServiceProviderPool.getLocalizedObject(TimeZoneNameGetter.INSTANCE, paramLocale, i < 5 ? "dst" : i < 3 ? "std" : "generic", new Object[] {
/* 167 */         Integer.valueOf(i % 2), paramString }));
/*     */     }
/*     */     
/* 170 */     if (Objects.isNull(localObject)) {
/* 171 */       localObject = new ConcurrentHashMap();
/*     */     }
/* 173 */     ((Map)localObject).put(paramLocale, arrayOfString);
/* 174 */     localSoftReference = new SoftReference(localObject);
/* 175 */     cachedDisplayNames.put(paramString, localSoftReference);
/* 176 */     return arrayOfString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class TimeZoneNameGetter
/*     */     implements LocaleServiceProviderPool.LocalizedObjectGetter<TimeZoneNameProvider, String>
/*     */   {
/* 187 */     private static final TimeZoneNameGetter INSTANCE = new TimeZoneNameGetter();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getObject(TimeZoneNameProvider paramTimeZoneNameProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
/*     */     {
/* 195 */       assert (paramVarArgs.length == 2);
/* 196 */       int i = ((Integer)paramVarArgs[0]).intValue();
/* 197 */       String str1 = (String)paramVarArgs[1];
/* 198 */       String str2 = getName(paramTimeZoneNameProvider, paramLocale, paramString, i, str1);
/* 199 */       if (str2 == null) {
/* 200 */         Map localMap = ZoneInfo.getAliasTable();
/* 201 */         if (localMap != null) {
/* 202 */           String str3 = (String)localMap.get(str1);
/* 203 */           if (str3 != null) {
/* 204 */             str2 = getName(paramTimeZoneNameProvider, paramLocale, paramString, i, str3);
/*     */           }
/* 206 */           if (str2 == null) {
/* 207 */             str2 = examineAliases(paramTimeZoneNameProvider, paramLocale, paramString, str3 != null ? str3 : str1, i, localMap);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 213 */       return str2;
/*     */     }
/*     */     
/*     */ 
/*     */     private static String examineAliases(TimeZoneNameProvider paramTimeZoneNameProvider, Locale paramLocale, String paramString1, String paramString2, int paramInt, Map<String, String> paramMap)
/*     */     {
/* 219 */       for (Entry localEntry : paramMap.entrySet()) {
/* 220 */         if (((String)localEntry.getValue()).equals(paramString2)) {
/* 221 */           String str1 = (String)localEntry.getKey();
/* 222 */           String str2 = getName(paramTimeZoneNameProvider, paramLocale, paramString1, paramInt, str1);
/* 223 */           if (str2 != null) {
/* 224 */             return str2;
/*     */           }
/* 226 */           str2 = examineAliases(paramTimeZoneNameProvider, paramLocale, paramString1, str1, paramInt, paramMap);
/* 227 */           if (str2 != null) {
/* 228 */             return str2;
/*     */           }
/*     */         }
/*     */       }
/* 232 */       return null;
/*     */     }
/*     */     
/*     */     private static String getName(TimeZoneNameProvider paramTimeZoneNameProvider, Locale paramLocale, String paramString1, int paramInt, String paramString2)
/*     */     {
/* 237 */       String str1 = null;
/* 238 */       switch (paramString1) {
/*     */       case "std": 
/* 240 */         str1 = paramTimeZoneNameProvider.getDisplayName(paramString2, false, paramInt, paramLocale);
/* 241 */         break;
/*     */       case "dst": 
/* 243 */         str1 = paramTimeZoneNameProvider.getDisplayName(paramString2, true, paramInt, paramLocale);
/* 244 */         break;
/*     */       case "generic": 
/* 246 */         str1 = paramTimeZoneNameProvider.getGenericDisplayName(paramString2, paramInt, paramLocale);
/*     */       }
/*     */       
/* 249 */       return str1;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\TimeZoneNameUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.spi.CalendarDataProvider;
/*     */ import java.util.spi.CalendarNameProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CalendarDataUtility
/*     */ {
/*     */   public static final String FIRST_DAY_OF_WEEK = "firstDayOfWeek";
/*     */   public static final String MINIMAL_DAYS_IN_FIRST_WEEK = "minimalDaysInFirstWeek";
/*     */   
/*     */   public static int retrieveFirstDayOfWeek(Locale paramLocale)
/*     */   {
/*  51 */     LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarDataProvider.class);
/*  52 */     Integer localInteger = (Integer)localLocaleServiceProviderPool.getLocalizedObject(CalendarWeekParameterGetter.INSTANCE, paramLocale, "firstDayOfWeek", new Object[0]);
/*     */     
/*  54 */     return (localInteger != null) && (localInteger.intValue() >= 1) && (localInteger.intValue() <= 7) ? localInteger.intValue() : 1;
/*     */   }
/*     */   
/*     */   public static int retrieveMinimalDaysInFirstWeek(Locale paramLocale)
/*     */   {
/*  59 */     LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarDataProvider.class);
/*  60 */     Integer localInteger = (Integer)localLocaleServiceProviderPool.getLocalizedObject(CalendarWeekParameterGetter.INSTANCE, paramLocale, "minimalDaysInFirstWeek", new Object[0]);
/*     */     
/*  62 */     return (localInteger != null) && (localInteger.intValue() >= 1) && (localInteger.intValue() <= 7) ? localInteger.intValue() : 1;
/*     */   }
/*     */   
/*     */   public static String retrieveFieldValueName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale)
/*     */   {
/*  67 */     LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
/*  68 */     return (String)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNameGetter.INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] {
/*  69 */       Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(false) });
/*     */   }
/*     */   
/*     */   public static String retrieveJavaTimeFieldValueName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale)
/*     */   {
/*  74 */     LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
/*     */     
/*  76 */     String str = (String)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNameGetter.INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] {
/*  77 */       Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(true) });
/*  78 */     if (str == null) {
/*  79 */       str = (String)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNameGetter.INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] {
/*  80 */         Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(false) });
/*     */     }
/*  82 */     return str;
/*     */   }
/*     */   
/*     */   public static Map<String, Integer> retrieveFieldValueNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
/*     */   {
/*  87 */     LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
/*  88 */     return (Map)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNamesMapGetter.INSTANCE, paramLocale, 
/*  89 */       normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(false) });
/*     */   }
/*     */   
/*     */   public static Map<String, Integer> retrieveJavaTimeFieldValueNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
/*     */   {
/*  94 */     LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
/*     */     
/*  96 */     Map localMap = (Map)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNamesMapGetter.INSTANCE, paramLocale, 
/*  97 */       normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(true) });
/*  98 */     if (localMap == null) {
/*  99 */       localMap = (Map)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNamesMapGetter.INSTANCE, paramLocale, 
/* 100 */         normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(false) });
/*     */     }
/* 102 */     return localMap;
/*     */   }
/*     */   
/*     */   static String normalizeCalendarType(String paramString) {
/*     */     String str;
/* 107 */     if ((paramString.equals("gregorian")) || (paramString.equals("iso8601"))) {
/* 108 */       str = "gregory";
/* 109 */     } else if (paramString.startsWith("islamic")) {
/* 110 */       str = "islamic";
/*     */     } else {
/* 112 */       str = paramString;
/*     */     }
/* 114 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class CalendarFieldValueNameGetter
/*     */     implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, String>
/*     */   {
/* 124 */     private static final CalendarFieldValueNameGetter INSTANCE = new CalendarFieldValueNameGetter();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getObject(CalendarNameProvider paramCalendarNameProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
/*     */     {
/* 132 */       assert (paramVarArgs.length == 4);
/* 133 */       int i = ((Integer)paramVarArgs[0]).intValue();
/* 134 */       int j = ((Integer)paramVarArgs[1]).intValue();
/* 135 */       int k = ((Integer)paramVarArgs[2]).intValue();
/* 136 */       boolean bool = ((Boolean)paramVarArgs[3]).booleanValue();
/*     */       
/*     */ 
/*     */ 
/* 140 */       if ((bool) && ((paramCalendarNameProvider instanceof CalendarNameProviderImpl)))
/*     */       {
/*     */ 
/* 143 */         String str = ((CalendarNameProviderImpl)paramCalendarNameProvider).getJavaTimeDisplayName(paramString, i, j, k, paramLocale);
/* 144 */         return str;
/*     */       }
/* 146 */       return paramCalendarNameProvider.getDisplayName(paramString, i, j, k, paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class CalendarFieldValueNamesMapGetter
/*     */     implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, Map<String, Integer>>
/*     */   {
/* 157 */     private static final CalendarFieldValueNamesMapGetter INSTANCE = new CalendarFieldValueNamesMapGetter();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Map<String, Integer> getObject(CalendarNameProvider paramCalendarNameProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
/*     */     {
/* 165 */       assert (paramVarArgs.length == 3);
/* 166 */       int i = ((Integer)paramVarArgs[0]).intValue();
/* 167 */       int j = ((Integer)paramVarArgs[1]).intValue();
/* 168 */       boolean bool = ((Boolean)paramVarArgs[2]).booleanValue();
/*     */       
/*     */ 
/*     */ 
/* 172 */       if ((bool) && ((paramCalendarNameProvider instanceof CalendarNameProviderImpl)))
/*     */       {
/*     */ 
/* 175 */         Map localMap = ((CalendarNameProviderImpl)paramCalendarNameProvider).getJavaTimeDisplayNames(paramString, i, j, paramLocale);
/* 176 */         return localMap;
/*     */       }
/* 178 */       return paramCalendarNameProvider.getDisplayNames(paramString, i, j, paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class CalendarWeekParameterGetter
/*     */     implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarDataProvider, Integer>
/*     */   {
/* 185 */     private static final CalendarWeekParameterGetter INSTANCE = new CalendarWeekParameterGetter();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Integer getObject(CalendarDataProvider paramCalendarDataProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
/*     */     {
/* 193 */       assert (paramVarArgs.length == 0);
/*     */       int i;
/* 195 */       switch (paramString) {
/*     */       case "firstDayOfWeek": 
/* 197 */         i = paramCalendarDataProvider.getFirstDayOfWeek(paramLocale);
/* 198 */         break;
/*     */       case "minimalDaysInFirstWeek": 
/* 200 */         i = paramCalendarDataProvider.getMinimalDaysInFirstWeek(paramLocale);
/* 201 */         break;
/*     */       default: 
/* 203 */         throw new InternalError("invalid requestID: " + paramString);
/*     */       }
/* 205 */       return i != 0 ? Integer.valueOf(i) : null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\CalendarDataUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
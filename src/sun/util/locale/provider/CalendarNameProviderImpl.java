/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.spi.CalendarNameProvider;
/*     */ import sun.util.calendar.CalendarSystem;
/*     */ import sun.util.calendar.Era;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CalendarNameProviderImpl
/*     */   extends CalendarNameProvider
/*     */   implements AvailableLanguageTags
/*     */ {
/*     */   private final LocaleProviderAdapter.Type type;
/*     */   private final Set<String> langtags;
/*     */   
/*     */   public CalendarNameProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
/*     */   {
/*  49 */     this.type = paramType;
/*  50 */     this.langtags = paramSet;
/*     */   }
/*     */   
/*     */   public String getDisplayName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale)
/*     */   {
/*  55 */     return getDisplayNameImpl(paramString, paramInt1, paramInt2, paramInt3, paramLocale, false);
/*     */   }
/*     */   
/*     */   public String getJavaTimeDisplayName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale) {
/*  59 */     return getDisplayNameImpl(paramString, paramInt1, paramInt2, paramInt3, paramLocale, true);
/*     */   }
/*     */   
/*     */   public String getDisplayNameImpl(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale, boolean paramBoolean) {
/*  63 */     String str1 = null;
/*  64 */     String str2 = getResourceKey(paramString, paramInt1, paramInt3, paramBoolean);
/*  65 */     if (str2 != null) {
/*  66 */       LocaleResources localLocaleResources = LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale);
/*  67 */       String[] arrayOfString = paramBoolean ? localLocaleResources.getJavaTimeNames(str2) : localLocaleResources.getCalendarNames(str2);
/*  68 */       if ((arrayOfString != null) && (arrayOfString.length > 0)) {
/*  69 */         if ((paramInt1 == 7) || (paramInt1 == 1)) {
/*  70 */           paramInt2--;
/*     */         }
/*  72 */         if ((paramInt2 < 0) || (paramInt2 > arrayOfString.length))
/*  73 */           return null;
/*  74 */         if (paramInt2 == arrayOfString.length) {
/*  75 */           if ((paramInt1 == 0) && ("japanese".equals(paramString)))
/*     */           {
/*     */ 
/*     */ 
/*  79 */             Era[] arrayOfEra = CalendarSystem.forName("japanese").getEras();
/*  80 */             if (arrayOfEra.length == paramInt2) {
/*  81 */               Era localEra = arrayOfEra[(paramInt2 - 1)];
/*  82 */               return paramInt3 == 2 ? localEra
/*  83 */                 .getName() : localEra
/*  84 */                 .getAbbreviation();
/*     */             }
/*     */           }
/*  87 */           return null;
/*     */         }
/*  89 */         str1 = arrayOfString[paramInt2];
/*     */         
/*  91 */         if ((str1.length() == 0) && ((paramInt3 == 32769) || (paramInt3 == 32770) || (paramInt3 == 32772)))
/*     */         {
/*     */ 
/*  94 */           str1 = getDisplayName(paramString, paramInt1, paramInt2, 
/*  95 */             getBaseStyle(paramInt3), paramLocale);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 100 */     return str1;
/*     */   }
/*     */   
/* 103 */   private static int[] REST_OF_STYLES = { 32769, 2, 32770, 4, 32772 };
/*     */   
/*     */ 
/*     */ 
/*     */   public Map<String, Integer> getDisplayNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
/*     */   {
/*     */     Map localMap;
/*     */     
/* 111 */     if (paramInt2 == 0) {
/* 112 */       localMap = getDisplayNamesImpl(paramString, paramInt1, 1, paramLocale, false);
/* 113 */       for (int k : REST_OF_STYLES) {
/* 114 */         localMap.putAll(getDisplayNamesImpl(paramString, paramInt1, k, paramLocale, false));
/*     */       }
/*     */     }
/*     */     else {
/* 118 */       localMap = getDisplayNamesImpl(paramString, paramInt1, paramInt2, paramLocale, false);
/*     */     }
/* 120 */     return localMap.isEmpty() ? null : localMap;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Integer> getJavaTimeDisplayNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
/*     */   {
/* 126 */     Map localMap = getDisplayNamesImpl(paramString, paramInt1, paramInt2, paramLocale, true);
/* 127 */     return localMap.isEmpty() ? null : localMap;
/*     */   }
/*     */   
/*     */   private Map<String, Integer> getDisplayNamesImpl(String paramString, int paramInt1, int paramInt2, Locale paramLocale, boolean paramBoolean)
/*     */   {
/* 132 */     String str1 = getResourceKey(paramString, paramInt1, paramInt2, paramBoolean);
/* 133 */     TreeMap localTreeMap = new TreeMap(LengthBasedComparator.INSTANCE);
/* 134 */     if (str1 != null) {
/* 135 */       LocaleResources localLocaleResources = LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale);
/* 136 */       String[] arrayOfString = paramBoolean ? localLocaleResources.getJavaTimeNames(str1) : localLocaleResources.getCalendarNames(str1);
/* 137 */       if ((arrayOfString != null) && 
/* 138 */         (!hasDuplicates(arrayOfString))) {
/* 139 */         if (paramInt1 == 1) {
/* 140 */           if (arrayOfString.length > 0) {
/* 141 */             localTreeMap.put(arrayOfString[0], Integer.valueOf(1));
/*     */           }
/*     */         } else {
/* 144 */           int i = paramInt1 == 7 ? 1 : 0;
/* 145 */           for (int j = 0; j < arrayOfString.length; j++) {
/* 146 */             String str2 = arrayOfString[j];
/*     */             
/*     */ 
/* 149 */             if (str2.length() != 0)
/*     */             {
/*     */ 
/* 152 */               localTreeMap.put(str2, Integer.valueOf(i + j));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 158 */     return localTreeMap;
/*     */   }
/*     */   
/*     */   private int getBaseStyle(int paramInt) {
/* 162 */     return paramInt & 0xFFFF7FFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class LengthBasedComparator
/*     */     implements Comparator<String>
/*     */   {
/* 170 */     private static final LengthBasedComparator INSTANCE = new LengthBasedComparator();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public int compare(String paramString1, String paramString2)
/*     */     {
/* 177 */       int i = paramString2.length() - paramString1.length();
/* 178 */       return i == 0 ? paramString1.compareTo(paramString2) : i;
/*     */     }
/*     */   }
/*     */   
/*     */   public Locale[] getAvailableLocales()
/*     */   {
/* 184 */     return LocaleProviderAdapter.toLocaleArray(this.langtags);
/*     */   }
/*     */   
/*     */   public boolean isSupportedLocale(Locale paramLocale)
/*     */   {
/* 189 */     if (Locale.ROOT.equals(paramLocale)) {
/* 190 */       return true;
/*     */     }
/* 192 */     String str1 = null;
/* 193 */     if (paramLocale.hasExtensions()) {
/* 194 */       str1 = paramLocale.getUnicodeLocaleType("ca");
/* 195 */       paramLocale = paramLocale.stripExtensions();
/*     */     }
/*     */     
/* 198 */     if (str1 != null) {
/* 199 */       switch (str1)
/*     */       {
/*     */       case "buddhist": 
/*     */       case "gregory": 
/*     */       case "islamic": 
/*     */       case "japanese": 
/*     */       case "roc": 
/*     */         break;
/*     */       default: 
/* 208 */         return false;
/*     */       }
/*     */     }
/* 211 */     if (this.langtags.contains(paramLocale.toLanguageTag())) {
/* 212 */       return true;
/*     */     }
/* 214 */     if (this.type == LocaleProviderAdapter.Type.JRE) {
/* 215 */       ??? = paramLocale.toString().replace('_', '-');
/* 216 */       return this.langtags.contains(???);
/*     */     }
/* 218 */     return false;
/*     */   }
/*     */   
/*     */   public Set<String> getAvailableLanguageTags()
/*     */   {
/* 223 */     return this.langtags;
/*     */   }
/*     */   
/*     */   private boolean hasDuplicates(String[] paramArrayOfString) {
/* 227 */     int i = paramArrayOfString.length;
/* 228 */     for (int j = 0; j < i - 1; j++) {
/* 229 */       String str = paramArrayOfString[j];
/* 230 */       if (str != null) {
/* 231 */         for (int k = j + 1; k < i; k++) {
/* 232 */           if (str.equals(paramArrayOfString[k])) {
/* 233 */             return true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 238 */     return false;
/*     */   }
/*     */   
/*     */   private String getResourceKey(String paramString, int paramInt1, int paramInt2, boolean paramBoolean) {
/* 242 */     int i = getBaseStyle(paramInt2);
/* 243 */     int j = paramInt2 != i ? 1 : 0;
/*     */     
/* 245 */     if ("gregory".equals(paramString)) {
/* 246 */       paramString = null;
/*     */     }
/* 248 */     int k = i == 4 ? 1 : 0;
/* 249 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     
/* 251 */     if (paramBoolean) {
/* 252 */       localStringBuilder.append("java.time.");
/*     */     }
/* 254 */     switch (paramInt1) {
/*     */     case 0: 
/* 256 */       if (paramString != null) {
/* 257 */         localStringBuilder.append(paramString).append('.');
/*     */       }
/* 259 */       if (k != 0) {
/* 260 */         localStringBuilder.append("narrow.");
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 265 */       else if (this.type == LocaleProviderAdapter.Type.JRE) {
/* 266 */         if ((paramBoolean) && 
/* 267 */           (i == 2)) {
/* 268 */           localStringBuilder.append("long.");
/*     */         }
/*     */         
/* 271 */         if (i == 1) {
/* 272 */           localStringBuilder.append("short.");
/*     */         }
/*     */       }
/* 275 */       else if (i == 2) {
/* 276 */         localStringBuilder.append("long.");
/*     */       }
/*     */       
/*     */ 
/* 280 */       localStringBuilder.append("Eras");
/* 281 */       break;
/*     */     
/*     */     case 1: 
/* 284 */       if (k == 0) {
/* 285 */         localStringBuilder.append(paramString).append(".FirstYear");
/*     */       }
/*     */       
/*     */       break;
/*     */     case 2: 
/* 290 */       if ("islamic".equals(paramString)) {
/* 291 */         localStringBuilder.append(paramString).append('.');
/*     */       }
/* 293 */       if (j != 0) {
/* 294 */         localStringBuilder.append("standalone.");
/*     */       }
/* 296 */       localStringBuilder.append("Month").append(toStyleName(i));
/* 297 */       break;
/*     */     
/*     */ 
/*     */     case 7: 
/* 301 */       if ((j != 0) && (k != 0)) {
/* 302 */         localStringBuilder.append("standalone.");
/*     */       }
/* 304 */       localStringBuilder.append("Day").append(toStyleName(i));
/* 305 */       break;
/*     */     
/*     */     case 9: 
/* 308 */       if (k != 0) {
/* 309 */         localStringBuilder.append("narrow.");
/*     */       }
/* 311 */       localStringBuilder.append("AmPmMarkers");
/*     */     }
/*     */     
/* 314 */     return localStringBuilder.length() > 0 ? localStringBuilder.toString() : null;
/*     */   }
/*     */   
/*     */   private String toStyleName(int paramInt) {
/* 318 */     switch (paramInt) {
/*     */     case 1: 
/* 320 */       return "Abbreviations";
/*     */     case 4: 
/* 322 */       return "Narrows";
/*     */     }
/* 324 */     return "Names";
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\CalendarNameProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
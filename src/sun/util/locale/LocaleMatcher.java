/*     */ package sun.util.locale;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Locale.FilteringMode;
/*     */ import java.util.Locale.LanguageRange;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class LocaleMatcher
/*     */ {
/*     */   public static List<Locale> filter(List<LanguageRange> paramList, Collection<Locale> paramCollection, FilteringMode paramFilteringMode)
/*     */   {
/*  51 */     if ((paramList.isEmpty()) || (paramCollection.isEmpty())) {
/*  52 */       return new ArrayList();
/*     */     }
/*     */     
/*     */ 
/*  56 */     ArrayList localArrayList = new ArrayList();
/*  57 */     for (Object localObject1 = paramCollection.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (Locale)((Iterator)localObject1).next();
/*  58 */       localArrayList.add(((Locale)localObject2).toLanguageTag());
/*     */     }
/*     */     
/*     */ 
/*  62 */     localObject1 = filterTags(paramList, localArrayList, paramFilteringMode);
/*     */     
/*     */ 
/*  65 */     Object localObject2 = new ArrayList(((List)localObject1).size());
/*  66 */     for (String str : (List)localObject1) {
/*  67 */       ((List)localObject2).add(Locale.forLanguageTag(str));
/*     */     }
/*     */     
/*  70 */     return (List<Locale>)localObject2;
/*     */   }
/*     */   
/*     */ 
/*     */   public static List<String> filterTags(List<LanguageRange> paramList, Collection<String> paramCollection, FilteringMode paramFilteringMode)
/*     */   {
/*  76 */     if ((paramList.isEmpty()) || (paramCollection.isEmpty())) {
/*  77 */       return new ArrayList();
/*     */     }
/*     */     
/*     */ 
/*  81 */     if (paramFilteringMode == FilteringMode.EXTENDED_FILTERING) {
/*  82 */       return filterExtended(paramList, paramCollection);
/*     */     }
/*  84 */     ArrayList localArrayList = new ArrayList();
/*  85 */     for (LanguageRange localLanguageRange : paramList) {
/*  86 */       String str = localLanguageRange.getRange();
/*  87 */       if ((str.startsWith("*-")) || 
/*  88 */         (str.indexOf("-*") != -1)) {
/*  89 */         if (paramFilteringMode == FilteringMode.AUTOSELECT_FILTERING)
/*  90 */           return filterExtended(paramList, paramCollection);
/*  91 */         if (paramFilteringMode == FilteringMode.MAP_EXTENDED_RANGES) {
/*  92 */           if (str.charAt(0) == '*') {
/*  93 */             str = "*";
/*     */           } else {
/*  95 */             str = str.replaceAll("-[*]", "");
/*     */           }
/*  97 */           localArrayList.add(new LanguageRange(str, localLanguageRange.getWeight()));
/*  98 */         } else if (paramFilteringMode == FilteringMode.REJECT_EXTENDED_RANGES) {
/*  99 */           throw new IllegalArgumentException("An extended range \"" + str + "\" found in REJECT_EXTENDED_RANGES mode.");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 104 */         localArrayList.add(localLanguageRange);
/*     */       }
/*     */     }
/*     */     
/* 108 */     return filterBasic(localArrayList, paramCollection);
/*     */   }
/*     */   
/*     */ 
/*     */   private static List<String> filterBasic(List<LanguageRange> paramList, Collection<String> paramCollection)
/*     */   {
/* 114 */     ArrayList localArrayList = new ArrayList();
/* 115 */     for (LanguageRange localLanguageRange : paramList) {
/* 116 */       str1 = localLanguageRange.getRange();
/* 117 */       if (str1.equals("*")) {
/* 118 */         return new ArrayList(paramCollection);
/*     */       }
/* 120 */       for (String str2 : paramCollection) {
/* 121 */         str2 = str2.toLowerCase();
/* 122 */         if (str2.startsWith(str1)) {
/* 123 */           int i = str1.length();
/* 124 */           if (((str2.length() == i) || (str2.charAt(i) == '-')) && 
/* 125 */             (!localArrayList.contains(str2))) {
/* 126 */             localArrayList.add(str2);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     String str1;
/* 133 */     return localArrayList;
/*     */   }
/*     */   
/*     */   private static List<String> filterExtended(List<LanguageRange> paramList, Collection<String> paramCollection)
/*     */   {
/* 138 */     ArrayList localArrayList = new ArrayList();
/* 139 */     for (LanguageRange localLanguageRange : paramList) {
/* 140 */       String str1 = localLanguageRange.getRange();
/* 141 */       if (str1.equals("*")) {
/* 142 */         return new ArrayList(paramCollection);
/*     */       }
/* 144 */       arrayOfString1 = str1.split("-");
/* 145 */       for (String str2 : paramCollection) {
/* 146 */         str2 = str2.toLowerCase();
/* 147 */         String[] arrayOfString2 = str2.split("-");
/* 148 */         if ((arrayOfString1[0].equals(arrayOfString2[0])) || 
/* 149 */           (arrayOfString1[0].equals("*")))
/*     */         {
/*     */ 
/*     */ 
/* 153 */           int i = 1;
/* 154 */           int j = 1;
/*     */           
/* 156 */           while ((i < arrayOfString1.length) && (j < arrayOfString2.length))
/*     */           {
/* 158 */             if (arrayOfString1[i].equals("*")) {
/* 159 */               i++;
/* 160 */             } else if (arrayOfString1[i].equals(arrayOfString2[j])) {
/* 161 */               i++;
/* 162 */               j++;
/* 163 */             } else { if ((arrayOfString2[j].length() == 1) && 
/* 164 */                 (!arrayOfString2[j].equals("*"))) {
/*     */                 break;
/*     */               }
/* 167 */               j++;
/*     */             }
/*     */           }
/*     */           
/* 171 */           if ((arrayOfString1.length == i) && (!localArrayList.contains(str2)))
/* 172 */             localArrayList.add(str2);
/*     */         }
/*     */       }
/*     */     }
/*     */     String[] arrayOfString1;
/* 177 */     return localArrayList;
/*     */   }
/*     */   
/*     */   public static Locale lookup(List<LanguageRange> paramList, Collection<Locale> paramCollection)
/*     */   {
/* 182 */     if ((paramList.isEmpty()) || (paramCollection.isEmpty())) {
/* 183 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 187 */     ArrayList localArrayList = new ArrayList();
/* 188 */     for (Object localObject = paramCollection.iterator(); ((Iterator)localObject).hasNext();) { Locale localLocale = (Locale)((Iterator)localObject).next();
/* 189 */       localArrayList.add(localLocale.toLanguageTag());
/*     */     }
/*     */     
/*     */ 
/* 193 */     localObject = lookupTag(paramList, localArrayList);
/*     */     
/* 195 */     if (localObject == null) {
/* 196 */       return null;
/*     */     }
/* 198 */     return Locale.forLanguageTag((String)localObject);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String lookupTag(List<LanguageRange> paramList, Collection<String> paramCollection)
/*     */   {
/* 204 */     if ((paramList.isEmpty()) || (paramCollection.isEmpty())) {
/* 205 */       return null;
/*     */     }
/*     */     
/* 208 */     for (LanguageRange localLanguageRange : paramList) {
/* 209 */       String str1 = localLanguageRange.getRange();
/*     */       
/*     */ 
/* 212 */       if (!str1.equals("*"))
/*     */       {
/*     */ 
/*     */ 
/* 216 */         String str2 = str1.replaceAll("\\x2A", "\\\\p{Alnum}*");
/* 217 */         while (str2.length() > 0) {
/* 218 */           for (String str3 : paramCollection) {
/* 219 */             str3 = str3.toLowerCase();
/* 220 */             if (str3.matches(str2)) {
/* 221 */               return str3;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 226 */           int i = str2.lastIndexOf('-');
/* 227 */           if (i >= 0) {
/* 228 */             str2 = str2.substring(0, i);
/*     */             
/*     */ 
/* 231 */             if (str2.lastIndexOf('-') == str2.length() - 2)
/*     */             {
/* 233 */               str2 = str2.substring(0, str2.length() - 2);
/*     */             }
/*     */           } else {
/* 236 */             str2 = "";
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 241 */     return null;
/*     */   }
/*     */   
/*     */   public static List<LanguageRange> parse(String paramString) {
/* 245 */     paramString = paramString.replaceAll(" ", "").toLowerCase();
/* 246 */     if (paramString.startsWith("accept-language:")) {
/* 247 */       paramString = paramString.substring(16);
/*     */     }
/*     */     
/* 250 */     String[] arrayOfString1 = paramString.split(",");
/* 251 */     ArrayList localArrayList1 = new ArrayList(arrayOfString1.length);
/* 252 */     ArrayList localArrayList2 = new ArrayList();
/* 253 */     int i = 0;
/*     */     
/* 255 */     for (String str1 : arrayOfString1)
/*     */     {
/*     */       int m;
/*     */       String str2;
/*     */       double d;
/* 260 */       if ((m = str1.indexOf(";q=")) == -1) {
/* 261 */         str2 = str1;
/* 262 */         d = 1.0D;
/*     */       } else {
/* 264 */         str2 = str1.substring(0, m);
/* 265 */         m += 3;
/*     */         try {
/* 267 */           d = Double.parseDouble(str1.substring(m));
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/* 271 */           throw new IllegalArgumentException("weight=\"" + str1.substring(m) + "\" for language range \"" + str2 + "\"");
/*     */         }
/*     */         
/*     */ 
/* 275 */         if ((d < 0.0D) || (d > 1.0D)) {
/* 276 */           throw new IllegalArgumentException("weight=" + d + " for language range \"" + str2 + "\". It must be between " + 0.0D + " and " + 1.0D + ".");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 283 */       if (!localArrayList2.contains(str2)) {
/* 284 */         LanguageRange localLanguageRange = new LanguageRange(str2, d);
/* 285 */         m = i;
/* 286 */         for (int n = 0; n < i; n++) {
/* 287 */           if (((LanguageRange)localArrayList1.get(n)).getWeight() < d) {
/* 288 */             m = n;
/* 289 */             break;
/*     */           }
/*     */         }
/* 292 */         localArrayList1.add(m, localLanguageRange);
/* 293 */         i++;
/* 294 */         localArrayList2.add(str2);
/*     */         
/*     */ 
/*     */ 
/*     */         String str3;
/*     */         
/*     */ 
/* 301 */         if (((str3 = getEquivalentForRegionAndVariant(str2)) != null) && 
/* 302 */           (!localArrayList2.contains(str3))) {
/* 303 */           localArrayList1.add(m + 1, new LanguageRange(str3, d));
/* 304 */           i++;
/* 305 */           localArrayList2.add(str3);
/*     */         }
/*     */         
/*     */         String[] arrayOfString3;
/* 309 */         if ((arrayOfString3 = getEquivalentsForLanguage(str2)) != null) {
/* 310 */           for (String str4 : arrayOfString3)
/*     */           {
/* 312 */             if (!localArrayList2.contains(str4)) {
/* 313 */               localArrayList1.add(m + 1, new LanguageRange(str4, d));
/* 314 */               i++;
/* 315 */               localArrayList2.add(str4);
/*     */             }
/*     */             
/*     */ 
/* 319 */             str3 = getEquivalentForRegionAndVariant(str4);
/* 320 */             if ((str3 != null) && 
/* 321 */               (!localArrayList2.contains(str3))) {
/* 322 */               localArrayList1.add(m + 1, new LanguageRange(str3, d));
/* 323 */               i++;
/* 324 */               localArrayList2.add(str3);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 331 */     return localArrayList1;
/*     */   }
/*     */   
/*     */   private static String[] getEquivalentsForLanguage(String paramString) {
/* 335 */     String str = paramString;
/*     */     
/* 337 */     while (str.length() > 0) { Object localObject;
/* 338 */       if (LocaleEquivalentMaps.singleEquivMap.containsKey(str)) {
/* 339 */         localObject = (String)LocaleEquivalentMaps.singleEquivMap.get(str);
/*     */         
/*     */ 
/* 342 */         return new String[] { paramString.replaceFirst(str, (String)localObject) }; }
/* 343 */       if (LocaleEquivalentMaps.multiEquivsMap.containsKey(str)) {
/* 344 */         localObject = (String[])LocaleEquivalentMaps.multiEquivsMap.get(str);
/* 345 */         for (int j = 0; j < localObject.length; j++) {
/* 346 */           localObject[j] = paramString.replaceFirst(str, localObject[j]);
/*     */         }
/* 348 */         return (String[])localObject;
/*     */       }
/*     */       
/*     */ 
/* 352 */       int i = str.lastIndexOf('-');
/* 353 */       if (i == -1) {
/*     */         break;
/*     */       }
/* 356 */       str = str.substring(0, i);
/*     */     }
/*     */     
/* 359 */     return null;
/*     */   }
/*     */   
/*     */   private static String getEquivalentForRegionAndVariant(String paramString) {
/* 363 */     int i = getExtentionKeyIndex(paramString);
/*     */     
/* 365 */     for (String str : LocaleEquivalentMaps.regionVariantEquivMap.keySet()) {
/*     */       int j;
/* 367 */       if ((j = paramString.indexOf(str)) != -1)
/*     */       {
/* 369 */         if ((i == Integer.MIN_VALUE) || (j <= i))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 374 */           int k = j + str.length();
/* 375 */           if ((paramString.length() == k) || (paramString.charAt(k) == '-')) {
/* 376 */             return paramString.replaceFirst(str, (String)LocaleEquivalentMaps.regionVariantEquivMap.get(str));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 381 */     return null;
/*     */   }
/*     */   
/*     */   private static int getExtentionKeyIndex(String paramString) {
/* 385 */     char[] arrayOfChar = paramString.toCharArray();
/* 386 */     int i = Integer.MIN_VALUE;
/* 387 */     for (int j = 1; j < arrayOfChar.length; j++) {
/* 388 */       if (arrayOfChar[j] == '-') {
/* 389 */         if (j - i == 2) {
/* 390 */           return i;
/*     */         }
/* 392 */         i = j;
/*     */       }
/*     */     }
/*     */     
/* 396 */     return Integer.MIN_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */   public static List<LanguageRange> mapEquivalents(List<LanguageRange> paramList, Map<String, List<String>> paramMap)
/*     */   {
/* 402 */     if (paramList.isEmpty()) {
/* 403 */       return new ArrayList();
/*     */     }
/* 405 */     if ((paramMap == null) || (paramMap.isEmpty())) {
/* 406 */       return new ArrayList(paramList);
/*     */     }
/*     */     
/*     */ 
/* 410 */     HashMap localHashMap = new HashMap();
/* 411 */     for (Object localObject1 = paramMap.keySet().iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (String)((Iterator)localObject1).next();
/* 412 */       localHashMap.put(((String)localObject2).toLowerCase(), localObject2);
/*     */     }
/*     */     
/* 415 */     localObject1 = new ArrayList();
/* 416 */     for (Object localObject2 = paramList.iterator(); ((Iterator)localObject2).hasNext();) { LanguageRange localLanguageRange = (LanguageRange)((Iterator)localObject2).next();
/* 417 */       String str1 = localLanguageRange.getRange();
/* 418 */       String str2 = str1;
/* 419 */       int i = 0;
/*     */       
/* 421 */       while (str2.length() > 0) {
/* 422 */         if (localHashMap.containsKey(str2)) {
/* 423 */           i = 1;
/* 424 */           List localList = (List)paramMap.get(localHashMap.get(str2));
/* 425 */           if (localList == null) break;
/* 426 */           int k = str2.length();
/* 427 */           for (String str3 : localList) {
/* 428 */             ((List)localObject1).add(new LanguageRange(str3.toLowerCase() + str1
/* 429 */               .substring(k), localLanguageRange
/* 430 */               .getWeight()));
/*     */           }
/* 432 */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 438 */         int j = str2.lastIndexOf('-');
/* 439 */         if (j == -1) {
/*     */           break;
/*     */         }
/* 442 */         str2 = str2.substring(0, j);
/*     */       }
/*     */       
/* 445 */       if (i == 0) {
/* 446 */         ((List)localObject1).add(localLanguageRange);
/*     */       }
/*     */     }
/*     */     
/* 450 */     return (List<LanguageRange>)localObject1;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\LocaleMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
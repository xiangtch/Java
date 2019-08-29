/*     */ package sun.util.resources;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.ResourceBundle.Control;
/*     */ import java.util.Set;
/*     */ import sun.util.locale.provider.JRELocaleProviderAdapter;
/*     */ import sun.util.locale.provider.LocaleProviderAdapter;
/*     */ import sun.util.locale.provider.LocaleProviderAdapter.Type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocaleData
/*     */ {
/*     */   private final Type type;
/*     */   
/*     */   public LocaleData(Type paramType)
/*     */   {
/*  71 */     this.type = paramType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceBundle getCalendarData(Locale paramLocale)
/*     */   {
/*  79 */     return getBundle(this.type.getUtilResourcesPackage() + ".CalendarData", paramLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public OpenListResourceBundle getCurrencyNames(Locale paramLocale)
/*     */   {
/*  87 */     return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".CurrencyNames", paramLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public OpenListResourceBundle getLocaleNames(Locale paramLocale)
/*     */   {
/*  95 */     return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".LocaleNames", paramLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TimeZoneNamesBundle getTimeZoneNames(Locale paramLocale)
/*     */   {
/* 103 */     return (TimeZoneNamesBundle)getBundle(this.type.getUtilResourcesPackage() + ".TimeZoneNames", paramLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceBundle getBreakIteratorInfo(Locale paramLocale)
/*     */   {
/* 111 */     return getBundle(this.type.getTextResourcesPackage() + ".BreakIteratorInfo", paramLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceBundle getCollationData(Locale paramLocale)
/*     */   {
/* 119 */     return getBundle(this.type.getTextResourcesPackage() + ".CollationData", paramLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceBundle getDateFormatData(Locale paramLocale)
/*     */   {
/* 127 */     return getBundle(this.type.getTextResourcesPackage() + ".FormatData", paramLocale);
/*     */   }
/*     */   
/*     */   public void setSupplementary(ParallelListResourceBundle paramParallelListResourceBundle) {
/* 131 */     if (!paramParallelListResourceBundle.areParallelContentsComplete()) {
/* 132 */       String str = this.type.getTextResourcesPackage() + ".JavaTimeSupplementary";
/* 133 */       setSupplementary(str, paramParallelListResourceBundle);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean setSupplementary(String paramString, ParallelListResourceBundle paramParallelListResourceBundle) {
/* 138 */     ParallelListResourceBundle localParallelListResourceBundle = (ParallelListResourceBundle)paramParallelListResourceBundle.getParent();
/* 139 */     boolean bool = false;
/* 140 */     if (localParallelListResourceBundle != null) {
/* 141 */       bool = setSupplementary(paramString, localParallelListResourceBundle);
/*     */     }
/* 143 */     OpenListResourceBundle localOpenListResourceBundle = getSupplementary(paramString, paramParallelListResourceBundle.getLocale());
/* 144 */     paramParallelListResourceBundle.setParallelContents(localOpenListResourceBundle);
/* 145 */     bool |= localOpenListResourceBundle != null;
/*     */     
/*     */ 
/* 148 */     if (bool) {
/* 149 */       paramParallelListResourceBundle.resetKeySet();
/*     */     }
/* 151 */     return bool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceBundle getNumberFormatData(Locale paramLocale)
/*     */   {
/* 159 */     return getBundle(this.type.getTextResourcesPackage() + ".FormatData", paramLocale);
/*     */   }
/*     */   
/*     */   public static ResourceBundle getBundle(String paramString, final Locale paramLocale) {
/* 163 */     (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public ResourceBundle run() {
/* 166 */         return 
/* 167 */           ResourceBundle.getBundle(this.val$baseName, paramLocale, LocaleDataResourceBundleControl.access$000());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static OpenListResourceBundle getSupplementary(String paramString, final Locale paramLocale) {
/* 173 */     (OpenListResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public OpenListResourceBundle run() {
/* 176 */         OpenListResourceBundle localOpenListResourceBundle = null;
/*     */         try {
/* 178 */           localOpenListResourceBundle = (OpenListResourceBundle)ResourceBundle.getBundle(this.val$baseName, paramLocale, 
/* 179 */             SupplementaryResourceBundleControl.access$100());
/*     */         }
/*     */         catch (MissingResourceException localMissingResourceException) {}
/*     */         
/*     */ 
/* 184 */         return localOpenListResourceBundle;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static class LocaleDataResourceBundleControl extends Control
/*     */   {
/* 191 */     private static final LocaleDataResourceBundleControl INSTANCE = new LocaleDataResourceBundleControl();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static final String DOTCLDR = ".cldr";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public List<Locale> getCandidateLocales(String paramString, Locale paramLocale)
/*     */     {
/* 209 */       List localList = super.getCandidateLocales(paramString, paramLocale);
/*     */       
/* 211 */       int i = paramString.lastIndexOf('.');
/* 212 */       String str = i >= 0 ? paramString.substring(i + 1) : paramString;
/* 213 */       Type localType = paramString.contains(".cldr") ? Type.CLDR : Type.JRE;
/* 214 */       LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.forType(localType);
/* 215 */       Set localSet = ((JRELocaleProviderAdapter)localLocaleProviderAdapter).getLanguageTagSet(str);
/* 216 */       Iterator localIterator; if (!localSet.isEmpty()) {
/* 217 */         for (localIterator = localList.iterator(); localIterator.hasNext();) {
/* 218 */           if (!LocaleProviderAdapter.isSupportedLocale((Locale)localIterator.next(), localType, localSet)) {
/* 219 */             localIterator.remove();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 225 */       if ((paramLocale.getLanguage() != "en") && (localType == Type.CLDR) &&
/* 226 */         (str.equals("TimeZoneNames"))) {
/* 227 */         localList.add(localList.size() - 1, Locale.ENGLISH);
/*     */       }
/* 229 */       return localList;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Locale getFallbackLocale(String paramString, Locale paramLocale)
/*     */     {
/* 242 */       if ((paramString == null) || (paramLocale == null)) {
/* 243 */         throw new NullPointerException();
/*     */       }
/* 245 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String toBundleName(String paramString, Locale paramLocale)
/*     */     {
/* 260 */       String str1 = paramString;
/* 261 */       String str2 = paramLocale.getLanguage();
/* 262 */       if ((str2.length() > 0) && (
/* 263 */         (paramString.startsWith(Type.JRE.getUtilResourcesPackage())) ||
/* 264 */         (paramString.startsWith(Type.JRE.getTextResourcesPackage()))))
/*     */       {
/* 266 */         assert (Type.JRE.getUtilResourcesPackage().length() == Type.JRE
/* 267 */           .getTextResourcesPackage().length());
/* 268 */         int i = Type.JRE.getUtilResourcesPackage().length();
/* 269 */         if (paramString.indexOf(".cldr", i) > 0) {
/* 270 */           i += ".cldr".length();
/*     */         }
/*     */         
/* 273 */         str1 = paramString.substring(0, i + 1) + str2 + paramString.substring(i);
/*     */       }
/*     */       
/* 276 */       return super.toBundleName(str1, paramLocale);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SupplementaryResourceBundleControl extends LocaleDataResourceBundleControl {
/* 281 */     private static final SupplementaryResourceBundleControl INSTANCE = new SupplementaryResourceBundleControl();
/*     */     
/*     */     private SupplementaryResourceBundleControl() {
/* 284 */       super();
/*     */     }
/*     */     
/*     */ 
/*     */     public List<Locale> getCandidateLocales(String paramString, Locale paramLocale)
/*     */     {
/* 290 */       return Arrays.asList(new Locale[] { paramLocale });
/*     */     }
/*     */     
/*     */     public long getTimeToLive(String paramString, Locale paramLocale)
/*     */     {
/* 295 */       assert (paramString.contains("JavaTimeSupplementary"));
/* 296 */       return -1L;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\resources\LocaleData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
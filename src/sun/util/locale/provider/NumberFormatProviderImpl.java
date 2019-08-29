/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.spi.NumberFormatProvider;
/*     */ import java.util.Currency;
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NumberFormatProviderImpl
/*     */   extends NumberFormatProvider
/*     */   implements AvailableLanguageTags
/*     */ {
/*     */   private static final int NUMBERSTYLE = 0;
/*     */   private static final int CURRENCYSTYLE = 1;
/*     */   private static final int PERCENTSTYLE = 2;
/*     */   private static final int SCIENTIFICSTYLE = 3;
/*     */   private static final int INTEGERSTYLE = 4;
/*     */   private final LocaleProviderAdapter.Type type;
/*     */   private final Set<String> langtags;
/*     */   
/*     */   public NumberFormatProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
/*     */   {
/*  71 */     this.type = paramType;
/*  72 */     this.langtags = paramSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Locale[] getAvailableLocales()
/*     */   {
/*  84 */     return LocaleProviderAdapter.forType(this.type).getAvailableLocales();
/*     */   }
/*     */   
/*     */   public boolean isSupportedLocale(Locale paramLocale)
/*     */   {
/*  89 */     return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags);
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
/*     */   public NumberFormat getCurrencyInstance(Locale paramLocale)
/*     */   {
/* 107 */     return getInstance(paramLocale, 1);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NumberFormat getIntegerInstance(Locale paramLocale)
/*     */   {
/* 131 */     return getInstance(paramLocale, 4);
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
/*     */   public NumberFormat getNumberInstance(Locale paramLocale)
/*     */   {
/* 149 */     return getInstance(paramLocale, 0);
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
/*     */   public NumberFormat getPercentInstance(Locale paramLocale)
/*     */   {
/* 167 */     return getInstance(paramLocale, 2);
/*     */   }
/*     */   
/*     */   private NumberFormat getInstance(Locale paramLocale, int paramInt)
/*     */   {
/* 172 */     if (paramLocale == null) {
/* 173 */       throw new NullPointerException();
/*     */     }
/*     */     
/* 176 */     LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.forType(this.type);
/* 177 */     String[] arrayOfString = localLocaleProviderAdapter.getLocaleResources(paramLocale).getNumberPatterns();
/* 178 */     DecimalFormatSymbols localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
/* 179 */     int i = paramInt == 4 ? 0 : paramInt;
/* 180 */     DecimalFormat localDecimalFormat = new DecimalFormat(arrayOfString[i], localDecimalFormatSymbols);
/*     */     
/* 182 */     if (paramInt == 4) {
/* 183 */       localDecimalFormat.setMaximumFractionDigits(0);
/* 184 */       localDecimalFormat.setDecimalSeparatorAlwaysShown(false);
/* 185 */       localDecimalFormat.setParseIntegerOnly(true);
/* 186 */     } else if (paramInt == 1) {
/* 187 */       adjustForCurrencyDefaultFractionDigits(localDecimalFormat, localDecimalFormatSymbols);
/*     */     }
/*     */     
/* 190 */     return localDecimalFormat;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void adjustForCurrencyDefaultFractionDigits(DecimalFormat paramDecimalFormat, DecimalFormatSymbols paramDecimalFormatSymbols)
/*     */   {
/* 199 */     Currency localCurrency = paramDecimalFormatSymbols.getCurrency();
/* 200 */     if (localCurrency == null) {
/*     */       try {
/* 202 */         localCurrency = Currency.getInstance(paramDecimalFormatSymbols.getInternationalCurrencySymbol());
/*     */       }
/*     */       catch (IllegalArgumentException localIllegalArgumentException) {}
/*     */     }
/* 206 */     if (localCurrency != null) {
/* 207 */       int i = localCurrency.getDefaultFractionDigits();
/* 208 */       if (i != -1) {
/* 209 */         int j = paramDecimalFormat.getMinimumFractionDigits();
/*     */         
/*     */ 
/* 212 */         if (j == paramDecimalFormat.getMaximumFractionDigits()) {
/* 213 */           paramDecimalFormat.setMinimumFractionDigits(i);
/* 214 */           paramDecimalFormat.setMaximumFractionDigits(i);
/*     */         } else {
/* 216 */           paramDecimalFormat.setMinimumFractionDigits(Math.min(i, j));
/* 217 */           paramDecimalFormat.setMaximumFractionDigits(i);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Set<String> getAvailableLanguageTags()
/*     */   {
/* 225 */     return this.langtags;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\NumberFormatProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
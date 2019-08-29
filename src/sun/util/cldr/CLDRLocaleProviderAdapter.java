/*     */ package sun.util.cldr;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.spi.BreakIteratorProvider;
/*     */ import java.text.spi.CollatorProvider;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ import sun.util.locale.provider.JRELocaleProviderAdapter;
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
/*     */ public class CLDRLocaleProviderAdapter
/*     */   extends JRELocaleProviderAdapter
/*     */ {
/*     */   private static final String LOCALE_DATA_JAR_NAME = "cldrdata.jar";
/*     */   
/*     */   public CLDRLocaleProviderAdapter()
/*     */   {
/*  53 */     String str1 = File.separator;
/*  54 */     String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + str1 + "lib" + str1 + "ext" + str1 + "cldrdata.jar";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  60 */     final File localFile = new File(str2);
/*  61 */     boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Boolean run()
/*     */       {
/*  65 */         return Boolean.valueOf(localFile.exists());
/*     */       }
/*     */     })).booleanValue();
/*  68 */     if (!bool) {
/*  69 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LocaleProviderAdapter.Type getAdapterType()
/*     */   {
/*  79 */     return LocaleProviderAdapter.Type.CLDR;
/*     */   }
/*     */   
/*     */   public BreakIteratorProvider getBreakIteratorProvider()
/*     */   {
/*  84 */     return null;
/*     */   }
/*     */   
/*     */   public CollatorProvider getCollatorProvider()
/*     */   {
/*  89 */     return null;
/*     */   }
/*     */   
/*     */   public Locale[] getAvailableLocales()
/*     */   {
/*  94 */     Set localSet = createLanguageTagSet("All");
/*  95 */     Locale[] arrayOfLocale = new Locale[localSet.size()];
/*  96 */     int i = 0;
/*  97 */     for (String str : localSet) {
/*  98 */       arrayOfLocale[(i++)] = Locale.forLanguageTag(str);
/*     */     }
/* 100 */     return arrayOfLocale;
/*     */   }
/*     */   
/*     */   protected Set<String> createLanguageTagSet(String paramString)
/*     */   {
/* 105 */     ResourceBundle localResourceBundle = ResourceBundle.getBundle("sun.util.cldr.CLDRLocaleDataMetaInfo", Locale.ROOT);
/* 106 */     String str = localResourceBundle.getString(paramString);
/* 107 */     if (str == null) {
/* 108 */       return Collections.emptySet();
/*     */     }
/* 110 */     HashSet localHashSet = new HashSet();
/* 111 */     StringTokenizer localStringTokenizer = new StringTokenizer(str);
/* 112 */     while (localStringTokenizer.hasMoreTokens()) {
/* 113 */       localHashSet.add(localStringTokenizer.nextToken());
/*     */     }
/* 115 */     return localHashSet;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\cldr\CLDRLocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
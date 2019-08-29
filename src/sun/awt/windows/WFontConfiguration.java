/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import sun.awt.FontConfiguration;
/*     */ import sun.awt.FontDescriptor;
/*     */ import sun.font.SunFontManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class WFontConfiguration
/*     */   extends FontConfiguration
/*     */ {
/*     */   private boolean useCompatibilityFallbacks;
/*     */   
/*     */   public WFontConfiguration(SunFontManager paramSunFontManager)
/*     */   {
/*  41 */     super(paramSunFontManager);
/*  42 */     this.useCompatibilityFallbacks = "windows-1252".equals(encoding);
/*  43 */     initTables(encoding);
/*     */   }
/*     */   
/*     */ 
/*     */   public WFontConfiguration(SunFontManager paramSunFontManager, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/*  49 */     super(paramSunFontManager, paramBoolean1, paramBoolean2);
/*  50 */     this.useCompatibilityFallbacks = "windows-1252".equals(encoding);
/*     */   }
/*     */   
/*     */   protected void initReorderMap()
/*     */   {
/*  55 */     if (encoding.equalsIgnoreCase("windows-31j")) {
/*  56 */       localeMap = new Hashtable();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  65 */       localeMap.put("dialoginput.plain.japanese", "MS Mincho");
/*  66 */       localeMap.put("dialoginput.bold.japanese", "MS Mincho");
/*  67 */       localeMap.put("dialoginput.italic.japanese", "MS Mincho");
/*  68 */       localeMap.put("dialoginput.bolditalic.japanese", "MS Mincho");
/*     */     }
/*  70 */     this.reorderMap = new HashMap();
/*  71 */     this.reorderMap.put("UTF-8.hi", "devanagari");
/*  72 */     this.reorderMap.put("windows-1255", "hebrew");
/*  73 */     this.reorderMap.put("x-windows-874", "thai");
/*  74 */     this.reorderMap.put("windows-31j", "japanese");
/*  75 */     this.reorderMap.put("x-windows-949", "korean");
/*  76 */     this.reorderMap.put("GBK", "chinese-ms936");
/*  77 */     this.reorderMap.put("GB18030", "chinese-gb18030");
/*  78 */     this.reorderMap.put("x-windows-950", "chinese-ms950");
/*  79 */     this.reorderMap.put("x-MS950-HKSCS", split("chinese-ms950,chinese-hkscs"));
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setOsNameAndVersion()
/*     */   {
/*  85 */     super.setOsNameAndVersion();
/*  86 */     if (osName.startsWith("Windows"))
/*     */     {
/*  88 */       int i = osName.indexOf(' ');
/*  89 */       if (i == -1) {
/*  90 */         osName = null;
/*     */       }
/*     */       else {
/*  93 */         int j = osName.indexOf(' ', i + 1);
/*  94 */         if (j == -1) {
/*  95 */           osName = osName.substring(i + 1);
/*     */         }
/*     */         else {
/*  98 */           osName = osName.substring(i + 1, j);
/*     */         }
/*     */       }
/* 101 */       osVersion = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getFallbackFamilyName(String paramString1, String paramString2)
/*     */   {
/* 110 */     if (this.useCompatibilityFallbacks) {
/* 111 */       String str = getCompatibilityFamilyName(paramString1);
/* 112 */       if (str != null) {
/* 113 */         return str;
/*     */       }
/*     */     }
/* 116 */     return paramString2;
/*     */   }
/*     */   
/*     */   protected String makeAWTFontName(String paramString1, String paramString2)
/*     */   {
/* 121 */     String str = (String)subsetCharsetMap.get(paramString2);
/* 122 */     if (str == null) {
/* 123 */       str = "DEFAULT_CHARSET";
/*     */     }
/* 125 */     return paramString1 + "," + str;
/*     */   }
/*     */   
/*     */   protected String getEncoding(String paramString1, String paramString2)
/*     */   {
/* 130 */     String str = (String)subsetEncodingMap.get(paramString2);
/* 131 */     if (str == null) {
/* 132 */       str = "default";
/*     */     }
/* 134 */     return str;
/*     */   }
/*     */   
/*     */   protected Charset getDefaultFontCharset(String paramString)
/*     */   {
/* 139 */     return new WDefaultFontCharset(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFaceNameFromComponentFontName(String paramString)
/*     */   {
/* 145 */     return paramString;
/*     */   }
/*     */   
/*     */   protected String getFileNameFromComponentFontName(String paramString)
/*     */   {
/* 150 */     return getFileNameFromPlatformName(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTextComponentFontName(String paramString, int paramInt)
/*     */   {
/* 158 */     FontDescriptor[] arrayOfFontDescriptor = getFontDescriptors(paramString, paramInt);
/* 159 */     String str = findFontWithCharset(arrayOfFontDescriptor, textInputCharset);
/* 160 */     if (str == null) {
/* 161 */       str = findFontWithCharset(arrayOfFontDescriptor, "DEFAULT_CHARSET");
/*     */     }
/* 163 */     return str;
/*     */   }
/*     */   
/*     */   private String findFontWithCharset(FontDescriptor[] paramArrayOfFontDescriptor, String paramString) {
/* 167 */     Object localObject = null;
/* 168 */     for (int i = 0; i < paramArrayOfFontDescriptor.length; i++) {
/* 169 */       String str = paramArrayOfFontDescriptor[i].getNativeName();
/* 170 */       if (str.endsWith(paramString)) {
/* 171 */         localObject = str;
/*     */       }
/*     */     }
/* 174 */     return (String)localObject;
/*     */   }
/*     */   
/* 177 */   private static HashMap subsetCharsetMap = new HashMap();
/* 178 */   private static HashMap subsetEncodingMap = new HashMap();
/*     */   private static String textInputCharset;
/*     */   
/*     */   private void initTables(String paramString) {
/* 182 */     subsetCharsetMap.put("alphabetic", "ANSI_CHARSET");
/* 183 */     subsetCharsetMap.put("alphabetic/1252", "ANSI_CHARSET");
/* 184 */     subsetCharsetMap.put("alphabetic/default", "DEFAULT_CHARSET");
/* 185 */     subsetCharsetMap.put("arabic", "ARABIC_CHARSET");
/* 186 */     subsetCharsetMap.put("chinese-ms936", "GB2312_CHARSET");
/* 187 */     subsetCharsetMap.put("chinese-gb18030", "GB2312_CHARSET");
/* 188 */     subsetCharsetMap.put("chinese-ms950", "CHINESEBIG5_CHARSET");
/* 189 */     subsetCharsetMap.put("chinese-hkscs", "CHINESEBIG5_CHARSET");
/* 190 */     subsetCharsetMap.put("cyrillic", "RUSSIAN_CHARSET");
/* 191 */     subsetCharsetMap.put("devanagari", "DEFAULT_CHARSET");
/* 192 */     subsetCharsetMap.put("dingbats", "SYMBOL_CHARSET");
/* 193 */     subsetCharsetMap.put("greek", "GREEK_CHARSET");
/* 194 */     subsetCharsetMap.put("hebrew", "HEBREW_CHARSET");
/* 195 */     subsetCharsetMap.put("japanese", "SHIFTJIS_CHARSET");
/* 196 */     subsetCharsetMap.put("korean", "HANGEUL_CHARSET");
/* 197 */     subsetCharsetMap.put("latin", "ANSI_CHARSET");
/* 198 */     subsetCharsetMap.put("symbol", "SYMBOL_CHARSET");
/* 199 */     subsetCharsetMap.put("thai", "THAI_CHARSET");
/*     */     
/* 201 */     subsetEncodingMap.put("alphabetic", "default");
/* 202 */     subsetEncodingMap.put("alphabetic/1252", "windows-1252");
/* 203 */     subsetEncodingMap.put("alphabetic/default", paramString);
/* 204 */     subsetEncodingMap.put("arabic", "windows-1256");
/* 205 */     subsetEncodingMap.put("chinese-ms936", "GBK");
/* 206 */     subsetEncodingMap.put("chinese-gb18030", "GB18030");
/* 207 */     if ("x-MS950-HKSCS".equals(paramString)) {
/* 208 */       subsetEncodingMap.put("chinese-ms950", "x-MS950-HKSCS");
/*     */     } else {
/* 210 */       subsetEncodingMap.put("chinese-ms950", "x-windows-950");
/*     */     }
/* 212 */     subsetEncodingMap.put("chinese-hkscs", "sun.awt.HKSCS");
/* 213 */     subsetEncodingMap.put("cyrillic", "windows-1251");
/* 214 */     subsetEncodingMap.put("devanagari", "UTF-16LE");
/* 215 */     subsetEncodingMap.put("dingbats", "sun.awt.windows.WingDings");
/* 216 */     subsetEncodingMap.put("greek", "windows-1253");
/* 217 */     subsetEncodingMap.put("hebrew", "windows-1255");
/* 218 */     subsetEncodingMap.put("japanese", "windows-31j");
/* 219 */     subsetEncodingMap.put("korean", "x-windows-949");
/* 220 */     subsetEncodingMap.put("latin", "windows-1252");
/* 221 */     subsetEncodingMap.put("symbol", "sun.awt.Symbol");
/* 222 */     subsetEncodingMap.put("thai", "x-windows-874");
/*     */     
/* 224 */     if ("windows-1256".equals(paramString)) {
/* 225 */       textInputCharset = "ARABIC_CHARSET";
/* 226 */     } else if ("GBK".equals(paramString)) {
/* 227 */       textInputCharset = "GB2312_CHARSET";
/* 228 */     } else if ("GB18030".equals(paramString)) {
/* 229 */       textInputCharset = "GB2312_CHARSET";
/* 230 */     } else if ("x-windows-950".equals(paramString)) {
/* 231 */       textInputCharset = "CHINESEBIG5_CHARSET";
/* 232 */     } else if ("x-MS950-HKSCS".equals(paramString)) {
/* 233 */       textInputCharset = "CHINESEBIG5_CHARSET";
/* 234 */     } else if ("windows-1251".equals(paramString)) {
/* 235 */       textInputCharset = "RUSSIAN_CHARSET";
/* 236 */     } else if ("UTF-8".equals(paramString)) {
/* 237 */       textInputCharset = "DEFAULT_CHARSET";
/* 238 */     } else if ("windows-1253".equals(paramString)) {
/* 239 */       textInputCharset = "GREEK_CHARSET";
/* 240 */     } else if ("windows-1255".equals(paramString)) {
/* 241 */       textInputCharset = "HEBREW_CHARSET";
/* 242 */     } else if ("windows-31j".equals(paramString)) {
/* 243 */       textInputCharset = "SHIFTJIS_CHARSET";
/* 244 */     } else if ("x-windows-949".equals(paramString)) {
/* 245 */       textInputCharset = "HANGEUL_CHARSET";
/* 246 */     } else if ("x-windows-874".equals(paramString)) {
/* 247 */       textInputCharset = "THAI_CHARSET";
/*     */     } else {
/* 249 */       textInputCharset = "DEFAULT_CHARSET";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WFontConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
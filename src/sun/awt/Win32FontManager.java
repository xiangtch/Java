/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.FontFormatException;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ import sun.awt.windows.WFontConfiguration;
/*     */ import sun.font.SunFontManager;
/*     */ import sun.font.SunFontManager.FamilyDescription;
/*     */ import sun.font.TrueTypeFont;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Win32FontManager
/*     */   extends SunFontManager
/*     */ {
/*     */   private static TrueTypeFont eudcFont;
/*     */   
/*     */   static
/*     */   {
/*  56 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*  59 */         String str = Win32FontManager.access$000();
/*  60 */         if (str != null)
/*     */         {
/*     */           try
/*     */           {
/*     */ 
/*  65 */             Win32FontManager.access$102(new TrueTypeFont(str, null, 0, true, false));
/*     */           }
/*     */           catch (FontFormatException localFontFormatException) {}
/*     */         }
/*     */         
/*  70 */         return null;
/*     */       }
/*     */     });
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
/*     */   public TrueTypeFont getEUDCFont()
/*     */   {
/*  85 */     return eudcFont;
/*     */   }
/*     */   
/*     */   public Win32FontManager()
/*     */   {
/*  90 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */ 
/*     */ 
/*     */       public Object run()
/*     */       {
/*     */ 
/*  97 */         Win32FontManager.this.registerJREFontsWithPlatform(SunFontManager.jreFontDirName);
/*  98 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean useAbsoluteFontFileNames()
/*     */   {
/* 108 */     return false;
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
/*     */   protected void registerFontFile(String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
/*     */   {
/* 121 */     if (this.registeredFontFiles.contains(paramString)) {
/* 122 */       return;
/*     */     }
/* 124 */     this.registeredFontFiles.add(paramString);
/*     */     
/*     */     int i;
/* 127 */     if (getTrueTypeFilter().accept(null, paramString)) {
/* 128 */       i = 0;
/* 129 */     } else if (getType1Filter().accept(null, paramString)) {
/* 130 */       i = 1;
/*     */     }
/*     */     else {
/* 133 */       return;
/*     */     }
/*     */     
/* 136 */     if (this.fontPath == null) {
/* 137 */       this.fontPath = getPlatformFontPath(noType1Font);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */     String str1 = jreFontDirName + File.pathSeparator + this.fontPath;
/* 145 */     StringTokenizer localStringTokenizer = new StringTokenizer(str1, File.pathSeparator);
/*     */     
/*     */ 
/* 148 */     int j = 0;
/*     */     try {
/* 150 */       while ((j == 0) && (localStringTokenizer.hasMoreTokens())) {
/* 151 */         String str2 = localStringTokenizer.nextToken();
/* 152 */         boolean bool = str2.equals(jreFontDirName);
/* 153 */         File localFile = new File(str2, paramString);
/* 154 */         if (localFile.canRead()) {
/* 155 */           j = 1;
/* 156 */           String str3 = localFile.getAbsolutePath();
/* 157 */           if (paramBoolean) {
/* 158 */             registerDeferredFont(paramString, str3, paramArrayOfString, i, bool, paramInt); break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 163 */           registerFontFile(str3, paramArrayOfString, i, bool, paramInt);
/*     */           
/*     */ 
/*     */ 
/* 167 */           break;
/*     */         }
/*     */       }
/*     */     } catch (NoSuchElementException localNoSuchElementException) {
/* 171 */       System.err.println(localNoSuchElementException);
/*     */     }
/* 173 */     if (j == 0) {
/* 174 */       addToMissingFontFileList(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected FontConfiguration createFontConfiguration()
/*     */   {
/* 181 */     WFontConfiguration localWFontConfiguration = new WFontConfiguration(this);
/* 182 */     localWFontConfiguration.init();
/* 183 */     return localWFontConfiguration;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 190 */     return new WFontConfiguration(this, paramBoolean1, paramBoolean2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void populateFontFileNameMap(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap, Locale paramLocale)
/*     */   {
/* 201 */     populateFontFileNameMap0(paramHashMap1, paramHashMap2, paramHashMap, paramLocale);
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
/*     */   protected String[] getDefaultPlatformFont()
/*     */   {
/* 217 */     String[] arrayOfString1 = new String[2];
/* 218 */     arrayOfString1[0] = "Arial";
/* 219 */     arrayOfString1[1] = "c:\\windows\\fonts";
/* 220 */     final String[] arrayOfString2 = getPlatformFontDirs(true);
/* 221 */     if (arrayOfString2.length > 1)
/*     */     {
/* 223 */       String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public Object run() {
/* 225 */           for (int i = 0; i < arrayOfString2.length; i++) {
/* 226 */             String str = arrayOfString2[i] + File.separator + "arial.ttf";
/*     */             
/* 228 */             File localFile = new File(str);
/* 229 */             if (localFile.exists()) {
/* 230 */               return arrayOfString2[i];
/*     */             }
/*     */           }
/* 233 */           return null;
/*     */         }
/*     */       });
/* 236 */       if (str != null) {
/* 237 */         arrayOfString1[1] = str;
/*     */       }
/*     */     } else {
/* 240 */       arrayOfString1[1] = arrayOfString2[0];
/*     */     }
/* 242 */     arrayOfString1[1] = (arrayOfString1[1] + File.separator + "arial.ttf");
/* 243 */     return arrayOfString1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 251 */   static String fontsForPrinting = null;
/*     */   
/* 253 */   protected void registerJREFontsWithPlatform(String paramString) { fontsForPrinting = paramString; }
/*     */   
/*     */   public static void registerJREFontsForPrinting()
/*     */   {
/*     */     String str;
/* 258 */     synchronized (Win32GraphicsEnvironment.class) {
/* 259 */       GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 260 */       if (fontsForPrinting == null) {
/* 261 */         return;
/*     */       }
/* 263 */       str = fontsForPrinting;
/* 264 */       fontsForPrinting = null;
/*     */     }
/* 266 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 269 */         File localFile1 = new File(this.val$pathName);
/* 270 */         String[] arrayOfString = localFile1.list(SunFontManager.getInstance()
/* 271 */           .getTrueTypeFilter());
/* 272 */         if (arrayOfString == null) {
/* 273 */           return null;
/*     */         }
/* 275 */         for (int i = 0; i < arrayOfString.length; i++) {
/* 276 */           File localFile2 = new File(localFile1, arrayOfString[i]);
/* 277 */           Win32FontManager.registerFontWithPlatform(localFile2.getAbsolutePath());
/*     */         }
/* 279 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HashMap<String, SunFontManager.FamilyDescription> populateHardcodedFileNameMap()
/*     */   {
/* 293 */     HashMap localHashMap = new HashMap();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 302 */     SunFontManager.FamilyDescription localFamilyDescription = new SunFontManager.FamilyDescription();
/* 303 */     localFamilyDescription.familyName = "Segoe UI";
/* 304 */     localFamilyDescription.plainFullName = "Segoe UI";
/* 305 */     localFamilyDescription.plainFileName = "segoeui.ttf";
/* 306 */     localFamilyDescription.boldFullName = "Segoe UI Bold";
/* 307 */     localFamilyDescription.boldFileName = "segoeuib.ttf";
/* 308 */     localFamilyDescription.italicFullName = "Segoe UI Italic";
/* 309 */     localFamilyDescription.italicFileName = "segoeuii.ttf";
/* 310 */     localFamilyDescription.boldItalicFullName = "Segoe UI Bold Italic";
/* 311 */     localFamilyDescription.boldItalicFileName = "segoeuiz.ttf";
/* 312 */     localHashMap.put("segoe", localFamilyDescription);
/*     */     
/* 314 */     localFamilyDescription = new SunFontManager.FamilyDescription();
/* 315 */     localFamilyDescription.familyName = "Tahoma";
/* 316 */     localFamilyDescription.plainFullName = "Tahoma";
/* 317 */     localFamilyDescription.plainFileName = "tahoma.ttf";
/* 318 */     localFamilyDescription.boldFullName = "Tahoma Bold";
/* 319 */     localFamilyDescription.boldFileName = "tahomabd.ttf";
/* 320 */     localHashMap.put("tahoma", localFamilyDescription);
/*     */     
/* 322 */     localFamilyDescription = new SunFontManager.FamilyDescription();
/* 323 */     localFamilyDescription.familyName = "Verdana";
/* 324 */     localFamilyDescription.plainFullName = "Verdana";
/* 325 */     localFamilyDescription.plainFileName = "verdana.TTF";
/* 326 */     localFamilyDescription.boldFullName = "Verdana Bold";
/* 327 */     localFamilyDescription.boldFileName = "verdanab.TTF";
/* 328 */     localFamilyDescription.italicFullName = "Verdana Italic";
/* 329 */     localFamilyDescription.italicFileName = "verdanai.TTF";
/* 330 */     localFamilyDescription.boldItalicFullName = "Verdana Bold Italic";
/* 331 */     localFamilyDescription.boldItalicFileName = "verdanaz.TTF";
/* 332 */     localHashMap.put("verdana", localFamilyDescription);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 337 */     localFamilyDescription = new SunFontManager.FamilyDescription();
/* 338 */     localFamilyDescription.familyName = "Arial";
/* 339 */     localFamilyDescription.plainFullName = "Arial";
/* 340 */     localFamilyDescription.plainFileName = "ARIAL.TTF";
/* 341 */     localFamilyDescription.boldFullName = "Arial Bold";
/* 342 */     localFamilyDescription.boldFileName = "ARIALBD.TTF";
/* 343 */     localFamilyDescription.italicFullName = "Arial Italic";
/* 344 */     localFamilyDescription.italicFileName = "ARIALI.TTF";
/* 345 */     localFamilyDescription.boldItalicFullName = "Arial Bold Italic";
/* 346 */     localFamilyDescription.boldItalicFileName = "ARIALBI.TTF";
/* 347 */     localHashMap.put("arial", localFamilyDescription);
/*     */     
/* 349 */     localFamilyDescription = new SunFontManager.FamilyDescription();
/* 350 */     localFamilyDescription.familyName = "Symbol";
/* 351 */     localFamilyDescription.plainFullName = "Symbol";
/* 352 */     localFamilyDescription.plainFileName = "Symbol.TTF";
/* 353 */     localHashMap.put("symbol", localFamilyDescription);
/*     */     
/* 355 */     localFamilyDescription = new SunFontManager.FamilyDescription();
/* 356 */     localFamilyDescription.familyName = "WingDings";
/* 357 */     localFamilyDescription.plainFullName = "WingDings";
/* 358 */     localFamilyDescription.plainFileName = "WINGDING.TTF";
/* 359 */     localHashMap.put("wingdings", localFamilyDescription);
/*     */     
/* 361 */     return localHashMap;
/*     */   }
/*     */   
/*     */   private static native String getEUDCFontFile();
/*     */   
/*     */   private static native void populateFontFileNameMap0(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap, Locale paramLocale);
/*     */   
/*     */   protected synchronized native String getFontPath(boolean paramBoolean);
/*     */   
/*     */   protected static native void registerFontWithPlatform(String paramString);
/*     */   
/*     */   protected static native void deRegisterFontWithPlatform(String paramString);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\Win32FontManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
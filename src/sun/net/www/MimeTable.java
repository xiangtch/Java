/*     */ package sun.net.www;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.FileNameMap;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MimeTable
/*     */   implements FileNameMap
/*     */ {
/*  36 */   private Hashtable<String, MimeEntry> entries = new Hashtable();
/*     */   
/*     */ 
/*     */ 
/*  40 */   private Hashtable<String, MimeEntry> extensionMap = new Hashtable();
/*     */   private static String tempFileTemplate;
/*     */   private static final String filePreamble = "sun.net.www MIME content-types table";
/*     */   private static final String fileMagic = "#sun.net.www MIME content-types table";
/*     */   protected static String[] mailcapLocations;
/*     */   
/*     */   static {
/*  47 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/*  50 */         MimeTable.access$002(
/*  51 */           System.getProperty("content.types.temp.file.template", "/tmp/%s"));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  60 */         MimeTable.mailcapLocations = new String[] {System.getProperty("user.mailcap"), System.getProperty("user.home") + "/.mailcap", "/etc/mailcap", "/usr/etc/mailcap", "/usr/local/etc/mailcap", System.getProperty("hotjava.home", "/usr/local/hotjava") + "/lib/mailcap" };
/*     */         
/*     */ 
/*     */ 
/*  64 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   MimeTable()
/*     */   {
/*  74 */     load();
/*     */   }
/*     */   
/*     */   private static class DefaultInstanceHolder {
/*  78 */     static final MimeTable defaultInstance = ;
/*     */     
/*     */     static MimeTable getDefaultInstance() {
/*  81 */       (MimeTable)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public MimeTable run() {
/*  84 */           MimeTable localMimeTable = new MimeTable();
/*  85 */           URLConnection.setFileNameMap(localMimeTable);
/*  86 */           return localMimeTable;
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static MimeTable getDefaultTable()
/*     */   {
/*  97 */     return DefaultInstanceHolder.defaultInstance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static FileNameMap loadTable()
/*     */   {
/* 104 */     MimeTable localMimeTable = getDefaultTable();
/* 105 */     return localMimeTable;
/*     */   }
/*     */   
/*     */   public synchronized int getSize() {
/* 109 */     return this.entries.size();
/*     */   }
/*     */   
/*     */   public synchronized String getContentTypeFor(String paramString) {
/* 113 */     MimeEntry localMimeEntry = findByFileName(paramString);
/* 114 */     if (localMimeEntry != null) {
/* 115 */       return localMimeEntry.getType();
/*     */     }
/* 117 */     return null;
/*     */   }
/*     */   
/*     */   public synchronized void add(MimeEntry paramMimeEntry)
/*     */   {
/* 122 */     this.entries.put(paramMimeEntry.getType(), paramMimeEntry);
/*     */     
/* 124 */     String[] arrayOfString = paramMimeEntry.getExtensions();
/* 125 */     if (arrayOfString == null) {
/* 126 */       return;
/*     */     }
/*     */     
/* 129 */     for (int i = 0; i < arrayOfString.length; i++) {
/* 130 */       this.extensionMap.put(arrayOfString[i], paramMimeEntry);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized MimeEntry remove(String paramString) {
/* 135 */     MimeEntry localMimeEntry = (MimeEntry)this.entries.get(paramString);
/* 136 */     return remove(localMimeEntry);
/*     */   }
/*     */   
/*     */   public synchronized MimeEntry remove(MimeEntry paramMimeEntry) {
/* 140 */     String[] arrayOfString = paramMimeEntry.getExtensions();
/* 141 */     if (arrayOfString != null) {
/* 142 */       for (int i = 0; i < arrayOfString.length; i++) {
/* 143 */         this.extensionMap.remove(arrayOfString[i]);
/*     */       }
/*     */     }
/*     */     
/* 147 */     return (MimeEntry)this.entries.remove(paramMimeEntry.getType());
/*     */   }
/*     */   
/*     */   public synchronized MimeEntry find(String paramString) {
/* 151 */     MimeEntry localMimeEntry1 = (MimeEntry)this.entries.get(paramString);
/* 152 */     if (localMimeEntry1 == null)
/*     */     {
/* 154 */       Enumeration localEnumeration = this.entries.elements();
/* 155 */       while (localEnumeration.hasMoreElements()) {
/* 156 */         MimeEntry localMimeEntry2 = (MimeEntry)localEnumeration.nextElement();
/* 157 */         if (localMimeEntry2.matches(paramString)) {
/* 158 */           return localMimeEntry2;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 163 */     return localMimeEntry1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MimeEntry findByFileName(String paramString)
/*     */   {
/* 171 */     String str = "";
/*     */     
/* 173 */     int i = paramString.lastIndexOf('#');
/*     */     
/* 175 */     if (i > 0) {
/* 176 */       paramString = paramString.substring(0, i - 1);
/*     */     }
/*     */     
/* 179 */     i = paramString.lastIndexOf('.');
/*     */     
/* 181 */     i = Math.max(i, paramString.lastIndexOf('/'));
/* 182 */     i = Math.max(i, paramString.lastIndexOf('?'));
/*     */     
/* 184 */     if ((i != -1) && (paramString.charAt(i) == '.')) {
/* 185 */       str = paramString.substring(i).toLowerCase();
/*     */     }
/*     */     
/* 188 */     return findByExt(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized MimeEntry findByExt(String paramString)
/*     */   {
/* 196 */     return (MimeEntry)this.extensionMap.get(paramString);
/*     */   }
/*     */   
/*     */   public synchronized MimeEntry findByDescription(String paramString) {
/* 200 */     Enumeration localEnumeration = elements();
/* 201 */     while (localEnumeration.hasMoreElements()) {
/* 202 */       MimeEntry localMimeEntry = (MimeEntry)localEnumeration.nextElement();
/* 203 */       if (paramString.equals(localMimeEntry.getDescription())) {
/* 204 */         return localMimeEntry;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 209 */     return find(paramString);
/*     */   }
/*     */   
/*     */   String getTempFileTemplate() {
/* 213 */     return tempFileTemplate;
/*     */   }
/*     */   
/*     */   public synchronized Enumeration<MimeEntry> elements() {
/* 217 */     return this.entries.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void load()
/*     */   {
/* 226 */     Properties localProperties = new Properties();
/* 227 */     File localFile = null;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 232 */       String str = System.getProperty("content.types.user.table");
/* 233 */       if (str != null) {
/* 234 */         localFile = new File(str);
/* 235 */         if (!localFile.exists())
/*     */         {
/* 237 */           localFile = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "content-types.properties");
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 246 */         localFile = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "content-types.properties");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 253 */       BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(localFile));
/* 254 */       localProperties.load(localBufferedInputStream);
/* 255 */       localBufferedInputStream.close();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 258 */       System.err.println("Warning: default mime table not found: " + localFile
/* 259 */         .getPath());
/* 260 */       return;
/*     */     }
/* 262 */     parse(localProperties);
/*     */   }
/*     */   
/*     */   void parse(Properties paramProperties)
/*     */   {
/* 267 */     String str1 = (String)paramProperties.get("temp.file.template");
/* 268 */     if (str1 != null) {
/* 269 */       paramProperties.remove("temp.file.template");
/* 270 */       tempFileTemplate = str1;
/*     */     }
/*     */     
/*     */ 
/* 274 */     Enumeration localEnumeration = paramProperties.propertyNames();
/* 275 */     while (localEnumeration.hasMoreElements()) {
/* 276 */       String str2 = (String)localEnumeration.nextElement();
/* 277 */       String str3 = paramProperties.getProperty(str2);
/* 278 */       parse(str2, str3);
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void parse(String paramString1, String paramString2)
/*     */   {
/* 310 */     MimeEntry localMimeEntry = new MimeEntry(paramString1);
/*     */     
/*     */ 
/* 313 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString2, ";");
/* 314 */     while (localStringTokenizer.hasMoreTokens()) {
/* 315 */       String str = localStringTokenizer.nextToken();
/* 316 */       parse(str, localMimeEntry);
/*     */     }
/*     */     
/* 319 */     add(localMimeEntry);
/*     */   }
/*     */   
/*     */   void parse(String paramString, MimeEntry paramMimeEntry)
/*     */   {
/* 324 */     String str1 = null;
/* 325 */     String str2 = null;
/*     */     
/* 327 */     int i = 0;
/* 328 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "=");
/* 329 */     while (localStringTokenizer.hasMoreTokens()) {
/* 330 */       if (i != 0) {
/* 331 */         str2 = localStringTokenizer.nextToken().trim();
/*     */       }
/*     */       else {
/* 334 */         str1 = localStringTokenizer.nextToken().trim();
/* 335 */         i = 1;
/*     */       }
/*     */     }
/*     */     
/* 339 */     fill(paramMimeEntry, str1, str2);
/*     */   }
/*     */   
/*     */   void fill(MimeEntry paramMimeEntry, String paramString1, String paramString2) {
/* 343 */     if ("description".equalsIgnoreCase(paramString1)) {
/* 344 */       paramMimeEntry.setDescription(paramString2);
/*     */     }
/* 346 */     else if ("action".equalsIgnoreCase(paramString1)) {
/* 347 */       paramMimeEntry.setAction(getActionCode(paramString2));
/*     */     }
/* 349 */     else if ("application".equalsIgnoreCase(paramString1)) {
/* 350 */       paramMimeEntry.setCommand(paramString2);
/*     */     }
/* 352 */     else if ("icon".equalsIgnoreCase(paramString1)) {
/* 353 */       paramMimeEntry.setImageFileName(paramString2);
/*     */     }
/* 355 */     else if ("file_extensions".equalsIgnoreCase(paramString1)) {
/* 356 */       paramMimeEntry.setExtensions(paramString2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   String[] getExtensions(String paramString)
/*     */   {
/* 363 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
/* 364 */     int i = localStringTokenizer.countTokens();
/* 365 */     String[] arrayOfString = new String[i];
/* 366 */     for (int j = 0; j < i; j++) {
/* 367 */       arrayOfString[j] = localStringTokenizer.nextToken();
/*     */     }
/*     */     
/* 370 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   int getActionCode(String paramString) {
/* 374 */     for (int i = 0; i < MimeEntry.actionKeywords.length; i++) {
/* 375 */       if (paramString.equalsIgnoreCase(MimeEntry.actionKeywords[i])) {
/* 376 */         return i;
/*     */       }
/*     */     }
/*     */     
/* 380 */     return 0;
/*     */   }
/*     */   
/*     */   public synchronized boolean save(String paramString) {
/* 384 */     if (paramString == null) {
/* 385 */       paramString = System.getProperty("user.home" + File.separator + "lib" + File.separator + "content-types.properties");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 392 */     return saveAsProperties(new File(paramString));
/*     */   }
/*     */   
/*     */   public Properties getAsProperties() {
/* 396 */     Properties localProperties = new Properties();
/* 397 */     Enumeration localEnumeration = elements();
/* 398 */     while (localEnumeration.hasMoreElements()) {
/* 399 */       MimeEntry localMimeEntry = (MimeEntry)localEnumeration.nextElement();
/* 400 */       localProperties.put(localMimeEntry.getType(), localMimeEntry.toProperty());
/*     */     }
/*     */     
/* 403 */     return localProperties;
/*     */   }
/*     */   
/*     */   protected boolean saveAsProperties(File paramFile) {
/* 407 */     FileOutputStream localFileOutputStream = null;
/*     */     try {
/* 409 */       localFileOutputStream = new FileOutputStream(paramFile);
/* 410 */       Properties localProperties = getAsProperties();
/* 411 */       localProperties.put("temp.file.template", tempFileTemplate);
/*     */       
/* 413 */       String str2 = System.getProperty("user.name");
/* 414 */       if (str2 != null) {
/* 415 */         String str1 = "; customized for " + str2;
/* 416 */         localProperties.store(localFileOutputStream, "sun.net.www MIME content-types table" + str1);
/*     */       }
/*     */       else {
/* 419 */         localProperties.store(localFileOutputStream, "sun.net.www MIME content-types table");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 432 */       return true;
/*     */     }
/*     */     catch (IOException localIOException2)
/*     */     {
/* 423 */       localIOException2.printStackTrace();
/* 424 */       return false;
/*     */     }
/*     */     finally {
/* 427 */       if (localFileOutputStream != null) {
/* 428 */         try { localFileOutputStream.close();
/*     */         }
/*     */         catch (IOException localIOException4) {}
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\MimeTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
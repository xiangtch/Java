/*     */ package sun.awt;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.StringBufferInputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ import sun.util.logging.PlatformLogger.Level;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DebugSettings
/*     */ {
/*  75 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.debug.DebugSettings");
/*     */   
/*     */ 
/*     */   static final String PREFIX = "awtdebug";
/*     */   
/*     */   static final String PROP_FILE = "properties";
/*     */   
/*  82 */   private static final String[] DEFAULT_PROPS = { "awtdebug.assert=true", "awtdebug.trace=false", "awtdebug.on=true", "awtdebug.ctrace=false" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */   private static DebugSettings instance = null;
/*     */   
/*  92 */   private Properties props = new Properties();
/*     */   private static final String PROP_CTRACE = "ctrace";
/*     */   
/*  95 */   static void init() { if (instance != null) {
/*  96 */       return;
/*     */     }
/*     */     
/*  99 */     NativeLibLoader.loadLibraries();
/* 100 */     instance = new DebugSettings();
/* 101 */     instance.loadNativeSettings();
/*     */   }
/*     */   
/*     */   private DebugSettings() {
/* 105 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 108 */         DebugSettings.this.loadProperties();
/* 109 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void loadProperties()
/*     */   {
/* 120 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 123 */         DebugSettings.this.loadDefaultProperties();
/* 124 */         DebugSettings.this.loadFileProperties();
/* 125 */         DebugSettings.this.loadSystemProperties();
/* 126 */         return null;
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 131 */     if (log.isLoggable(PlatformLogger.Level.FINE)) {
/* 132 */       log.fine("DebugSettings:\n{0}", new Object[] { this });
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 137 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 138 */     PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
/* 139 */     for (String str1 : this.props.stringPropertyNames()) {
/* 140 */       String str2 = this.props.getProperty(str1, "");
/* 141 */       localPrintStream.println(str1 + " = " + str2);
/*     */     }
/* 143 */     return new String(localByteArrayOutputStream.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loadDefaultProperties()
/*     */   {
/*     */     try
/*     */     {
/* 153 */       for (int i = 0; i < DEFAULT_PROPS.length; i++) {
/* 154 */         StringBufferInputStream localStringBufferInputStream = new StringBufferInputStream(DEFAULT_PROPS[i]);
/* 155 */         this.props.load(localStringBufferInputStream);
/* 156 */         localStringBufferInputStream.close();
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loadFileProperties()
/*     */   {
/* 170 */     String str = System.getProperty("awtdebug.properties", "");
/* 171 */     if (str.equals(""))
/*     */     {
/* 173 */       str = System.getProperty("user.home", "") + File.separator + "awtdebug" + "." + "properties";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 178 */     File localFile = new File(str);
/*     */     try {
/* 180 */       println("Reading debug settings from '" + localFile.getCanonicalPath() + "'...");
/* 181 */       FileInputStream localFileInputStream = new FileInputStream(localFile);
/* 182 */       this.props.load(localFileInputStream);
/* 183 */       localFileInputStream.close();
/*     */     } catch (FileNotFoundException localFileNotFoundException) {
/* 185 */       println("Did not find settings file.");
/*     */     } catch (IOException localIOException) {
/* 187 */       println("Problem reading settings, IOException: " + localIOException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loadSystemProperties()
/*     */   {
/* 197 */     Properties localProperties = System.getProperties();
/* 198 */     for (String str1 : localProperties.stringPropertyNames()) {
/* 199 */       String str2 = localProperties.getProperty(str1, "");
/*     */       
/* 201 */       if (str1.startsWith("awtdebug")) {
/* 202 */         this.props.setProperty(str1, str2);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized boolean getBoolean(String paramString, boolean paramBoolean)
/*     */   {
/* 214 */     String str = getString(paramString, String.valueOf(paramBoolean));
/* 215 */     return str.equalsIgnoreCase("true");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized int getInt(String paramString, int paramInt)
/*     */   {
/* 225 */     String str = getString(paramString, String.valueOf(paramInt));
/* 226 */     return Integer.parseInt(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized String getString(String paramString1, String paramString2)
/*     */   {
/* 236 */     String str1 = "awtdebug." + paramString1;
/* 237 */     String str2 = this.props.getProperty(str1, paramString2);
/*     */     
/* 239 */     return str2;
/*     */   }
/*     */   
/*     */   private synchronized List<String> getPropertyNames() {
/* 243 */     LinkedList localLinkedList = new LinkedList();
/*     */     
/* 245 */     for (String str : this.props.stringPropertyNames()) {
/* 246 */       str = str.substring("awtdebug".length() + 1);
/* 247 */       localLinkedList.add(str);
/*     */     }
/* 249 */     return localLinkedList;
/*     */   }
/*     */   
/*     */   private void println(Object paramObject) {
/* 253 */     if (log.isLoggable(PlatformLogger.Level.FINER)) {
/* 254 */       log.finer(paramObject.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 259 */   private static final int PROP_CTRACE_LEN = "ctrace".length();
/*     */   
/*     */   private synchronized native void setCTracingOn(boolean paramBoolean);
/*     */   
/*     */   private synchronized native void setCTracingOn(boolean paramBoolean, String paramString);
/*     */   
/*     */   private synchronized native void setCTracingOn(boolean paramBoolean, String paramString, int paramInt);
/*     */   
/*     */   private void loadNativeSettings() {
/* 268 */     boolean bool1 = getBoolean("ctrace", false);
/* 269 */     setCTracingOn(bool1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 274 */     LinkedList localLinkedList = new LinkedList();
/*     */     
/* 276 */     for (Iterator localIterator = getPropertyNames().iterator(); localIterator.hasNext();) { str1 = (String)localIterator.next();
/* 277 */       if ((str1.startsWith("ctrace")) && (str1.length() > PROP_CTRACE_LEN)) {
/* 278 */         localLinkedList.add(str1);
/*     */       }
/*     */     }
/*     */     
/*     */     String str1;
/* 283 */     Collections.sort(localLinkedList);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 288 */     for (localIterator = localLinkedList.iterator(); localIterator.hasNext();) { str1 = (String)localIterator.next();
/* 289 */       String str2 = str1.substring(PROP_CTRACE_LEN + 1);
/*     */       
/*     */ 
/* 292 */       int i = str2.indexOf('@');
/*     */       
/*     */ 
/*     */ 
/* 296 */       String str3 = i != -1 ? str2.substring(0, i) : str2;
/* 297 */       String str4 = i != -1 ? str2.substring(i + 1) : "";
/* 298 */       boolean bool2 = getBoolean(str1, false);
/*     */       
/*     */ 
/* 301 */       if (str4.length() == 0)
/*     */       {
/* 303 */         setCTracingOn(bool2, str3);
/*     */       }
/*     */       else {
/* 306 */         int j = Integer.parseInt(str4, 10);
/* 307 */         setCTracingOn(bool2, str3, j);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\DebugSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
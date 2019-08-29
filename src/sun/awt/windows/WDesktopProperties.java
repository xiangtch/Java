/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.RenderingHints;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import sun.awt.SunToolkit;
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
/*     */ final class WDesktopProperties
/*     */ {
/*     */   private static final PlatformLogger log;
/*     */   private static final String PREFIX = "win.";
/*     */   private static final String FILE_PREFIX = "awt.file.";
/*     */   private static final String PROP_NAMES = "win.propNames";
/*     */   private long pData;
/*     */   private WToolkit wToolkit;
/*  69 */   private HashMap<String, Object> map = new HashMap();
/*     */   
/*     */ 
/*     */   static HashMap<String, String> fontNameMap;
/*     */   
/*     */ 
/*     */   static boolean isWindowsProperty(String paramString)
/*     */   {
/*  77 */     return (paramString.startsWith("win.")) || (paramString.startsWith("awt.file.")) || 
/*  78 */       (paramString.equals("awt.font.desktophints"));
/*     */   }
/*     */   
/*     */   WDesktopProperties(WToolkit paramWToolkit) {
/*  82 */     this.wToolkit = paramWToolkit;
/*  83 */     init();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String[] getKeyNames()
/*     */   {
/*  92 */     Object[] arrayOfObject = this.map.keySet().toArray();
/*  93 */     String[] arrayOfString = new String[arrayOfObject.length];
/*     */     
/*  95 */     for (int i = 0; i < arrayOfObject.length; i++) {
/*  96 */       arrayOfString[i] = arrayOfObject[i].toString();
/*     */     }
/*  98 */     Arrays.sort(arrayOfString);
/*  99 */     return arrayOfString;
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
/*     */   private synchronized void setBooleanProperty(String paramString, boolean paramBoolean)
/*     */   {
/* 112 */     assert (paramString != null);
/* 113 */     if (log.isLoggable(PlatformLogger.Level.FINE)) {
/* 114 */       log.fine(paramString + "=" + String.valueOf(paramBoolean));
/*     */     }
/* 116 */     this.map.put(paramString, Boolean.valueOf(paramBoolean));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private synchronized void setIntegerProperty(String paramString, int paramInt)
/*     */   {
/* 123 */     assert (paramString != null);
/* 124 */     if (log.isLoggable(PlatformLogger.Level.FINE)) {
/* 125 */       log.fine(paramString + "=" + String.valueOf(paramInt));
/*     */     }
/* 127 */     this.map.put(paramString, Integer.valueOf(paramInt));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private synchronized void setStringProperty(String paramString1, String paramString2)
/*     */   {
/* 134 */     assert (paramString1 != null);
/* 135 */     if (log.isLoggable(PlatformLogger.Level.FINE)) {
/* 136 */       log.fine(paramString1 + "=" + paramString2);
/*     */     }
/* 138 */     this.map.put(paramString1, paramString2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private synchronized void setColorProperty(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 145 */     assert ((paramString != null) && (paramInt1 <= 255) && (paramInt2 <= 255) && (paramInt3 <= 255));
/* 146 */     Color localColor = new Color(paramInt1, paramInt2, paramInt3);
/* 147 */     if (log.isLoggable(PlatformLogger.Level.FINE)) {
/* 148 */       log.fine(paramString + "=" + localColor);
/*     */     }
/* 150 */     this.map.put(paramString, localColor);
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*  56 */     log = PlatformLogger.getLogger("sun.awt.windows.WDesktopProperties");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  64 */     initIDs();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 156 */     fontNameMap = new HashMap();
/* 157 */     fontNameMap.put("Courier", "Monospaced");
/* 158 */     fontNameMap.put("MS Serif", "Microsoft Serif");
/* 159 */     fontNameMap.put("MS Sans Serif", "Microsoft Sans Serif");
/* 160 */     fontNameMap.put("Terminal", "Dialog");
/* 161 */     fontNameMap.put("FixedSys", "Monospaced");
/* 162 */     fontNameMap.put("System", "Dialog");
/*     */   }
/*     */   
/*     */ 
/*     */   private synchronized void setFontProperty(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*     */   {
/* 168 */     assert ((paramString1 != null) && (paramInt1 <= 3) && (paramInt2 >= 0));
/*     */     
/* 170 */     String str1 = (String)fontNameMap.get(paramString2);
/* 171 */     if (str1 != null) {
/* 172 */       paramString2 = str1;
/*     */     }
/* 174 */     Font localFont = new Font(paramString2, paramInt1, paramInt2);
/* 175 */     if (log.isLoggable(PlatformLogger.Level.FINE)) {
/* 176 */       log.fine(paramString1 + "=" + localFont);
/*     */     }
/* 178 */     this.map.put(paramString1, localFont);
/*     */     
/* 180 */     String str2 = paramString1 + ".height";
/* 181 */     Integer localInteger = Integer.valueOf(paramInt2);
/* 182 */     if (log.isLoggable(PlatformLogger.Level.FINE)) {
/* 183 */       log.fine(str2 + "=" + localInteger);
/*     */     }
/* 185 */     this.map.put(str2, localInteger);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private synchronized void setSoundProperty(String paramString1, String paramString2)
/*     */   {
/* 192 */     assert ((paramString1 != null) && (paramString2 != null));
/*     */     
/* 194 */     WinPlaySound localWinPlaySound = new WinPlaySound(paramString2);
/* 195 */     if (log.isLoggable(PlatformLogger.Level.FINE)) {
/* 196 */       log.fine(paramString1 + "=" + localWinPlaySound);
/*     */     }
/* 198 */     this.map.put(paramString1, localWinPlaySound);
/*     */   }
/*     */   
/*     */ 
/*     */   class WinPlaySound
/*     */     implements Runnable
/*     */   {
/*     */     String winEventName;
/*     */     
/*     */ 
/*     */     WinPlaySound(String paramString)
/*     */     {
/* 210 */       this.winEventName = paramString;
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/* 215 */       WDesktopProperties.this.playWindowsSound(this.winEventName);
/*     */     }
/*     */     
/*     */     public String toString() {
/* 219 */       return "WinPlaySound(" + this.winEventName + ")";
/*     */     }
/*     */     
/*     */     public boolean equals(Object paramObject) {
/* 223 */       if (paramObject == this) {
/* 224 */         return true;
/*     */       }
/*     */       try {
/* 227 */         return this.winEventName.equals(((WinPlaySound)paramObject).winEventName);
/*     */       } catch (Exception localException) {}
/* 229 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 234 */       return this.winEventName.hashCode();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized Map<String, Object> getProperties()
/*     */   {
/* 244 */     ThemeReader.flush();
/*     */     
/*     */ 
/* 247 */     this.map = new HashMap();
/* 248 */     getWindowsParameters();
/* 249 */     this.map.put("awt.font.desktophints", SunToolkit.getDesktopFontHints());
/* 250 */     this.map.put("win.propNames", getKeyNames());
/*     */     
/*     */ 
/* 253 */     this.map.put("DnD.Autoscroll.cursorHysteresis", this.map.get("win.drag.x"));
/*     */     
/* 255 */     return (Map)this.map.clone();
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
/*     */   synchronized RenderingHints getDesktopAAHints()
/*     */   {
/* 271 */     Object localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
/* 272 */     Integer localInteger1 = null;
/*     */     
/* 274 */     Boolean localBoolean = (Boolean)this.map.get("win.text.fontSmoothingOn");
/*     */     
/* 276 */     if ((localBoolean != null) && (localBoolean.equals(Boolean.TRUE))) {
/* 277 */       localObject2 = (Integer)this.map.get("win.text.fontSmoothingType");
/*     */       
/*     */ 
/*     */ 
/* 281 */       if ((localObject2 == null) || (((Integer)localObject2).intValue() <= 1) || 
/* 282 */         (((Integer)localObject2).intValue() > 2)) {
/* 283 */         localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 289 */         Integer localInteger2 = (Integer)this.map.get("win.text.fontSmoothingOrientation");
/*     */         
/* 291 */         if ((localInteger2 == null) || (localInteger2.intValue() != 0)) {
/* 292 */           localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
/*     */         } else {
/* 294 */           localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
/*     */         }
/*     */         
/*     */ 
/* 298 */         localInteger1 = (Integer)this.map.get("win.text.fontSmoothingContrast");
/* 299 */         if (localInteger1 == null) {
/* 300 */           localInteger1 = Integer.valueOf(140);
/*     */         }
/*     */         else
/*     */         {
/* 304 */           localInteger1 = Integer.valueOf(localInteger1.intValue() / 10);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 309 */     Object localObject2 = new RenderingHints(null);
/* 310 */     ((RenderingHints)localObject2).put(RenderingHints.KEY_TEXT_ANTIALIASING, localObject1);
/* 311 */     if (localInteger1 != null) {
/* 312 */       ((RenderingHints)localObject2).put(RenderingHints.KEY_TEXT_LCD_CONTRAST, localInteger1);
/*     */     }
/* 314 */     return (RenderingHints)localObject2;
/*     */   }
/*     */   
/*     */   private static native void initIDs();
/*     */   
/*     */   private native void init();
/*     */   
/*     */   private native void getWindowsParameters();
/*     */   
/*     */   private native void playWindowsSound(String paramString);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WDesktopProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
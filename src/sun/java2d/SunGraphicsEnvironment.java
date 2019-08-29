/*     */ package sun.java2d;
/*     */ 
/*     */ import java.awt.AWTError;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import sun.awt.DisplayChangedListener;
/*     */ import sun.awt.SunDisplayChanger;
/*     */ import sun.font.FontManager;
/*     */ import sun.font.FontManagerFactory;
/*     */ import sun.font.FontManagerForSGE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SunGraphicsEnvironment
/*     */   extends GraphicsEnvironment
/*     */   implements DisplayChangedListener
/*     */ {
/*     */   public static boolean isOpenSolaris;
/*     */   private static Font defaultFont;
/*     */   protected GraphicsDevice[] screens;
/*     */   
/*     */   public SunGraphicsEnvironment()
/*     */   {
/*  84 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*  87 */         String str1 = System.getProperty("os.version", "0.0");
/*     */         try {
/*  89 */           float f = Float.parseFloat(str1);
/*  90 */           if (f > 5.1F) {
/*  91 */             File localFile1 = new File("/etc/release");
/*  92 */             FileInputStream localFileInputStream = new FileInputStream(localFile1);
/*  93 */             InputStreamReader localInputStreamReader = new InputStreamReader(localFileInputStream, "ISO-8859-1");
/*     */             
/*  95 */             BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
/*  96 */             String str2 = localBufferedReader.readLine();
/*  97 */             if (str2.indexOf("OpenSolaris") >= 0) {
/*  98 */               SunGraphicsEnvironment.isOpenSolaris = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 111 */               String str3 = "/usr/openwin/lib/X11/fonts/TrueType/CourierNew.ttf";
/*     */               
/* 113 */               File localFile2 = new File(str3);
/* 114 */               SunGraphicsEnvironment.isOpenSolaris = !localFile2.exists();
/*     */             }
/* 116 */             localFileInputStream.close();
/*     */           }
/*     */         }
/*     */         catch (Exception localException) {}
/*     */         
/*     */ 
/* 122 */         SunGraphicsEnvironment.access$002(new Font("Dialog", 0, 12));
/*     */         
/* 124 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized GraphicsDevice[] getScreenDevices()
/*     */   {
/* 135 */     GraphicsDevice[] arrayOfGraphicsDevice = this.screens;
/* 136 */     if (arrayOfGraphicsDevice == null) {
/* 137 */       int i = getNumScreens();
/* 138 */       arrayOfGraphicsDevice = new GraphicsDevice[i];
/* 139 */       for (int j = 0; j < i; j++) {
/* 140 */         arrayOfGraphicsDevice[j] = makeScreenDevice(j);
/*     */       }
/* 142 */       this.screens = arrayOfGraphicsDevice;
/*     */     }
/* 144 */     return arrayOfGraphicsDevice;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract int getNumScreens();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract GraphicsDevice makeScreenDevice(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GraphicsDevice getDefaultScreenDevice()
/*     */   {
/* 169 */     GraphicsDevice[] arrayOfGraphicsDevice = getScreenDevices();
/* 170 */     if (arrayOfGraphicsDevice.length == 0) {
/* 171 */       throw new AWTError("no screen devices");
/*     */     }
/* 173 */     return arrayOfGraphicsDevice[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Graphics2D createGraphics(BufferedImage paramBufferedImage)
/*     */   {
/* 182 */     if (paramBufferedImage == null) {
/* 183 */       throw new NullPointerException("BufferedImage cannot be null");
/*     */     }
/* 185 */     SurfaceData localSurfaceData = SurfaceData.getPrimarySurfaceData(paramBufferedImage);
/* 186 */     return new SunGraphics2D(localSurfaceData, Color.white, Color.black, defaultFont);
/*     */   }
/*     */   
/*     */   public static FontManagerForSGE getFontManagerForSGE() {
/* 190 */     FontManager localFontManager = FontManagerFactory.getInstance();
/* 191 */     return (FontManagerForSGE)localFontManager;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void useAlternateFontforJALocales()
/*     */   {
/* 202 */     getFontManagerForSGE().useAlternateFontforJALocales();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Font[] getAllFonts()
/*     */   {
/* 209 */     FontManagerForSGE localFontManagerForSGE = getFontManagerForSGE();
/* 210 */     Font[] arrayOfFont1 = localFontManagerForSGE.getAllInstalledFonts();
/* 211 */     Font[] arrayOfFont2 = localFontManagerForSGE.getCreatedFonts();
/* 212 */     if ((arrayOfFont2 == null) || (arrayOfFont2.length == 0)) {
/* 213 */       return arrayOfFont1;
/*     */     }
/* 215 */     int i = arrayOfFont1.length + arrayOfFont2.length;
/* 216 */     Font[] arrayOfFont3 = (Font[])Arrays.copyOf(arrayOfFont1, i);
/* 217 */     System.arraycopy(arrayOfFont2, 0, arrayOfFont3, arrayOfFont1.length, arrayOfFont2.length);
/*     */     
/* 219 */     return arrayOfFont3;
/*     */   }
/*     */   
/*     */   public String[] getAvailableFontFamilyNames(Locale paramLocale)
/*     */   {
/* 224 */     FontManagerForSGE localFontManagerForSGE = getFontManagerForSGE();
/* 225 */     String[] arrayOfString1 = localFontManagerForSGE.getInstalledFontFamilyNames(paramLocale);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 234 */     TreeMap localTreeMap = localFontManagerForSGE.getCreatedFontFamilyNames();
/* 235 */     if ((localTreeMap == null) || (localTreeMap.size() == 0)) {
/* 236 */       return arrayOfString1;
/*     */     }
/* 238 */     for (int i = 0; i < arrayOfString1.length; i++) {
/* 239 */       localTreeMap.put(arrayOfString1[i].toLowerCase(paramLocale), arrayOfString1[i]);
/*     */     }
/*     */     
/* 242 */     String[] arrayOfString2 = new String[localTreeMap.size()];
/* 243 */     Object[] arrayOfObject = localTreeMap.keySet().toArray();
/* 244 */     for (int j = 0; j < arrayOfObject.length; j++) {
/* 245 */       arrayOfString2[j] = ((String)localTreeMap.get(arrayOfObject[j]));
/*     */     }
/* 247 */     return arrayOfString2;
/*     */   }
/*     */   
/*     */   public String[] getAvailableFontFamilyNames()
/*     */   {
/* 252 */     return getAvailableFontFamilyNames(Locale.getDefault());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Rectangle getUsableBounds(GraphicsDevice paramGraphicsDevice)
/*     */   {
/* 260 */     GraphicsConfiguration localGraphicsConfiguration = paramGraphicsDevice.getDefaultConfiguration();
/* 261 */     Insets localInsets = Toolkit.getDefaultToolkit().getScreenInsets(localGraphicsConfiguration);
/* 262 */     Rectangle localRectangle = localGraphicsConfiguration.getBounds();
/*     */     
/* 264 */     localRectangle.x += localInsets.left;
/* 265 */     localRectangle.y += localInsets.top;
/* 266 */     localRectangle.width -= localInsets.left + localInsets.right;
/* 267 */     localRectangle.height -= localInsets.top + localInsets.bottom;
/*     */     
/* 269 */     return localRectangle;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void displayChanged()
/*     */   {
/* 278 */     for (GraphicsDevice localGraphicsDevice : getScreenDevices()) {
/* 279 */       if ((localGraphicsDevice instanceof DisplayChangedListener)) {
/* 280 */         ((DisplayChangedListener)localGraphicsDevice).displayChanged();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 286 */     this.displayChanger.notifyListeners();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void paletteChanged()
/*     */   {
/* 294 */     this.displayChanger.notifyPaletteChanged();
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
/* 308 */   protected SunDisplayChanger displayChanger = new SunDisplayChanger();
/*     */   
/*     */ 
/*     */   public abstract boolean isDisplayLocal();
/*     */   
/*     */   public void addDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener)
/*     */   {
/* 315 */     this.displayChanger.add(paramDisplayChangedListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener)
/*     */   {
/* 322 */     this.displayChanger.remove(paramDisplayChangedListener);
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
/*     */   public boolean isFlipStrategyPreferred(ComponentPeer paramComponentPeer)
/*     */   {
/* 340 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\SunGraphicsEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
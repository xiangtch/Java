/*     */ package sun.java2d.windows;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.awt.windows.WToolkit;
/*     */ import sun.java2d.opengl.WGLGraphicsConfig;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WindowsFlags
/*     */ {
/*     */   private static boolean gdiBlitEnabled;
/*     */   private static boolean d3dEnabled;
/*     */   private static boolean d3dVerbose;
/*     */   private static boolean d3dSet;
/*     */   private static boolean d3dOnScreenEnabled;
/*     */   private static boolean oglEnabled;
/*     */   private static boolean oglVerbose;
/*     */   private static boolean offscreenSharingEnabled;
/*     */   private static boolean accelReset;
/*     */   private static boolean checkRegistry;
/*     */   private static boolean disableRegistry;
/*     */   private static boolean magPresent;
/*     */   private static boolean setHighDPIAware;
/*     */   private static String javaVersion;
/*     */   
/*     */   static
/*     */   {
/* 135 */     WToolkit.loadLibraries();
/*     */     
/* 137 */     initJavaFlags();
/*     */     
/*     */ 
/*     */ 
/* 141 */     initNativeFlags();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean getBooleanProp(String paramString, boolean paramBoolean)
/*     */   {
/* 153 */     String str = System.getProperty(paramString);
/* 154 */     boolean bool = paramBoolean;
/* 155 */     if (str != null) {
/* 156 */       if ((str.equals("true")) || 
/* 157 */         (str.equals("t")) || 
/* 158 */         (str.equals("True")) || 
/* 159 */         (str.equals("T")) || 
/* 160 */         (str.equals("")))
/*     */       {
/* 162 */         bool = true;
/* 163 */       } else if ((str.equals("false")) || 
/* 164 */         (str.equals("f")) || 
/* 165 */         (str.equals("False")) || 
/* 166 */         (str.equals("F")))
/*     */       {
/* 168 */         bool = false;
/*     */       }
/*     */     }
/* 171 */     return bool;
/*     */   }
/*     */   
/*     */   private static boolean isBooleanPropTrueVerbose(String paramString) {
/* 175 */     String str = System.getProperty(paramString);
/* 176 */     if ((str != null) && (
/* 177 */       (str.equals("True")) || 
/* 178 */       (str.equals("T"))))
/*     */     {
/* 180 */       return true;
/*     */     }
/*     */     
/* 183 */     return false;
/*     */   }
/*     */   
/*     */   private static int getIntProp(String paramString, int paramInt) {
/* 187 */     String str = System.getProperty(paramString);
/* 188 */     int i = paramInt;
/* 189 */     if (str != null) {
/*     */       try {
/* 191 */         i = Integer.parseInt(str);
/*     */       } catch (NumberFormatException localNumberFormatException) {}
/*     */     }
/* 194 */     return i;
/*     */   }
/*     */   
/*     */   private static boolean getPropertySet(String paramString) {
/* 198 */     String str = System.getProperty(paramString);
/* 199 */     return str != null;
/*     */   }
/*     */   
/*     */   private static void initJavaFlags() {
/* 203 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/* 207 */         WindowsFlags.access$002(WindowsFlags.getBooleanProp("javax.accessibility.screen_magnifier_present", false));
/*     */         
/*     */ 
/* 210 */         boolean bool1 = !WindowsFlags.getBooleanProp("sun.java2d.noddraw", WindowsFlags.magPresent);
/*     */         
/* 212 */         boolean bool2 = WindowsFlags.getBooleanProp("sun.java2d.ddoffscreen", bool1);
/* 213 */         WindowsFlags.access$202(WindowsFlags.getBooleanProp("sun.java2d.d3d", (bool1) && (bool2)));
/*     */         
/* 215 */         WindowsFlags.access$302(
/* 216 */           WindowsFlags.getBooleanProp("sun.java2d.d3d.onscreen", WindowsFlags.d3dEnabled));
/* 217 */         WindowsFlags.access$402(WindowsFlags.getBooleanProp("sun.java2d.opengl", false));
/* 218 */         if (WindowsFlags.oglEnabled) {
/* 219 */           WindowsFlags.access$502(WindowsFlags.isBooleanPropTrueVerbose("sun.java2d.opengl"));
/* 220 */           if (WGLGraphicsConfig.isWGLAvailable()) {
/* 221 */             WindowsFlags.access$202(false);
/*     */           } else {
/* 223 */             if (WindowsFlags.oglVerbose) {
/* 224 */               System.out.println("Could not enable OpenGL pipeline (WGL not available)");
/*     */             }
/*     */             
/*     */ 
/* 228 */             WindowsFlags.access$402(false);
/*     */           }
/*     */         }
/* 231 */         WindowsFlags.access$702(WindowsFlags.getBooleanProp("sun.java2d.gdiBlit", true));
/* 232 */         WindowsFlags.access$802(WindowsFlags.getPropertySet("sun.java2d.d3d"));
/* 233 */         if (WindowsFlags.d3dSet) {
/* 234 */           WindowsFlags.access$1002(WindowsFlags.isBooleanPropTrueVerbose("sun.java2d.d3d"));
/*     */         }
/* 236 */         WindowsFlags.access$1102(
/* 237 */           WindowsFlags.getBooleanProp("sun.java2d.offscreenSharing", false));
/* 238 */         WindowsFlags.access$1202(WindowsFlags.getBooleanProp("sun.java2d.accelReset", false));
/* 239 */         WindowsFlags.access$1302(
/* 240 */           WindowsFlags.getBooleanProp("sun.java2d.checkRegistry", false));
/* 241 */         WindowsFlags.access$1402(
/* 242 */           WindowsFlags.getBooleanProp("sun.java2d.disableRegistry", false));
/* 243 */         WindowsFlags.access$1502(System.getProperty("java.version"));
/* 244 */         if (WindowsFlags.javaVersion == null)
/*     */         {
/* 246 */           WindowsFlags.access$1502("default");
/*     */         } else {
/* 248 */           int i = WindowsFlags.javaVersion.indexOf('-');
/* 249 */           if (i >= 0)
/*     */           {
/* 251 */             WindowsFlags.access$1502(WindowsFlags.javaVersion.substring(0, i));
/*     */           }
/*     */         }
/* 254 */         String str1 = System.getProperty("sun.java2d.dpiaware");
/* 255 */         if (str1 != null) {
/* 256 */           WindowsFlags.access$1602(str1.equalsIgnoreCase("true"));
/*     */         }
/*     */         else {
/* 259 */           String str2 = System.getProperty("sun.java.launcher", "unknown");
/* 260 */           WindowsFlags.access$1602(str2
/* 261 */             .equalsIgnoreCase("SUN_STANDARD"));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 273 */         return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isD3DEnabled()
/*     */   {
/* 300 */     return d3dEnabled;
/*     */   }
/*     */   
/*     */   public static boolean isD3DSet() {
/* 304 */     return d3dSet;
/*     */   }
/*     */   
/*     */   public static boolean isD3DOnScreenEnabled() {
/* 308 */     return d3dOnScreenEnabled;
/*     */   }
/*     */   
/*     */   public static boolean isD3DVerbose() {
/* 312 */     return d3dVerbose;
/*     */   }
/*     */   
/*     */   public static boolean isGdiBlitEnabled() {
/* 316 */     return gdiBlitEnabled;
/*     */   }
/*     */   
/*     */   public static boolean isTranslucentAccelerationEnabled() {
/* 320 */     return d3dEnabled;
/*     */   }
/*     */   
/*     */   public static boolean isOffscreenSharingEnabled() {
/* 324 */     return offscreenSharingEnabled;
/*     */   }
/*     */   
/*     */   public static boolean isMagPresent() {
/* 328 */     return magPresent;
/*     */   }
/*     */   
/*     */   public static boolean isOGLEnabled() {
/* 332 */     return oglEnabled;
/*     */   }
/*     */   
/*     */   public static boolean isOGLVerbose() {
/* 336 */     return oglVerbose;
/*     */   }
/*     */   
/*     */   private static native boolean initNativeFlags();
/*     */   
/*     */   public static void initFlags() {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\windows\WindowsFlags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
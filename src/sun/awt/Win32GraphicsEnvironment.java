/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.AWTError;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.peer.ComponentPeer;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.ListIterator;
/*     */ import sun.awt.windows.WToolkit;
/*     */ import sun.java2d.SunGraphicsEnvironment;
/*     */ import sun.java2d.SurfaceManagerFactory;
/*     */ import sun.java2d.WindowsSurfaceManagerFactory;
/*     */ import sun.java2d.d3d.D3DGraphicsDevice;
/*     */ import sun.java2d.windows.WindowsFlags;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Win32GraphicsEnvironment
/*     */   extends SunGraphicsEnvironment
/*     */ {
/*     */   private static boolean displayInitialized;
/*     */   private ArrayList<WeakReference<Win32GraphicsDevice>> oldDevices;
/*     */   private static volatile boolean isDWMCompositionEnabled;
/*     */   
/*     */   static
/*     */   {
/*  66 */     WToolkit.loadLibraries();
/*     */     
/*  68 */     WindowsFlags.initFlags();
/*  69 */     initDisplayWrapper();
/*     */     
/*     */ 
/*  72 */     SurfaceManagerFactory.setInstance(new WindowsSurfaceManagerFactory());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void initDisplayWrapper()
/*     */   {
/*  84 */     if (!displayInitialized) {
/*  85 */       displayInitialized = true;
/*  86 */       initDisplay();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GraphicsDevice getDefaultScreenDevice()
/*     */   {
/*  97 */     GraphicsDevice[] arrayOfGraphicsDevice = getScreenDevices();
/*  98 */     if (arrayOfGraphicsDevice.length == 0) {
/*  99 */       throw new AWTError("no screen devices");
/*     */     }
/* 101 */     int i = getDefaultScreen();
/* 102 */     return arrayOfGraphicsDevice[0];
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
/*     */ 
/*     */ 
/*     */   public void displayChanged()
/*     */   {
/* 135 */     GraphicsDevice[] arrayOfGraphicsDevice1 = new GraphicsDevice[getNumScreens()];
/* 136 */     GraphicsDevice[] arrayOfGraphicsDevice2 = this.screens;
/*     */     
/*     */ 
/* 139 */     if (arrayOfGraphicsDevice2 != null) {
/* 140 */       for (i = 0; i < arrayOfGraphicsDevice2.length; i++)
/* 141 */         if (!(this.screens[i] instanceof Win32GraphicsDevice))
/*     */         {
/* 143 */           if (!$assertionsDisabled) throw new AssertionError(arrayOfGraphicsDevice2[i]);
/*     */         }
/*     */         else {
/* 146 */           Win32GraphicsDevice localWin32GraphicsDevice1 = (Win32GraphicsDevice)arrayOfGraphicsDevice2[i];
/*     */           
/*     */ 
/*     */ 
/* 150 */           if (!localWin32GraphicsDevice1.isValid()) {
/* 151 */             if (this.oldDevices == null) {
/* 152 */               this.oldDevices = new ArrayList();
/*     */             }
/*     */             
/* 155 */             this.oldDevices.add(new WeakReference(localWin32GraphicsDevice1));
/* 156 */           } else if (i < arrayOfGraphicsDevice1.length)
/*     */           {
/* 158 */             arrayOfGraphicsDevice1[i] = localWin32GraphicsDevice1;
/*     */           }
/*     */         }
/* 161 */       arrayOfGraphicsDevice2 = null;
/*     */     }
/*     */     
/* 164 */     for (int i = 0; i < arrayOfGraphicsDevice1.length; i++) {
/* 165 */       if (arrayOfGraphicsDevice1[i] == null) {
/* 166 */         arrayOfGraphicsDevice1[i] = makeScreenDevice(i);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 172 */     this.screens = arrayOfGraphicsDevice1;
/* 173 */     for (GraphicsDevice localGraphicsDevice : this.screens) {
/* 174 */       if ((localGraphicsDevice instanceof DisplayChangedListener)) {
/* 175 */         ((DisplayChangedListener)localGraphicsDevice).displayChanged();
/*     */       }
/*     */     }
/*     */     
/*     */     int j;
/*     */     
/*     */     ListIterator localListIterator;
/* 182 */     if (this.oldDevices != null) {
/* 183 */       j = getDefaultScreen();
/*     */       
/* 185 */       for (localListIterator = this.oldDevices.listIterator(); localListIterator.hasNext();)
/*     */       {
/* 187 */         Win32GraphicsDevice localWin32GraphicsDevice2 = (Win32GraphicsDevice)((WeakReference)localListIterator.next()).get();
/* 188 */         if (localWin32GraphicsDevice2 != null) {
/* 189 */           localWin32GraphicsDevice2.invalidate(j);
/* 190 */           localWin32GraphicsDevice2.displayChanged();
/*     */         }
/*     */         else {
/* 193 */           localListIterator.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 198 */     WToolkit.resetGC();
/*     */     
/*     */ 
/*     */ 
/* 202 */     this.displayChanger.notifyListeners();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected GraphicsDevice makeScreenDevice(int paramInt)
/*     */   {
/* 212 */     Object localObject = null;
/* 213 */     if (WindowsFlags.isD3DEnabled()) {
/* 214 */       localObject = D3DGraphicsDevice.createDevice(paramInt);
/*     */     }
/* 216 */     if (localObject == null) {
/* 217 */       localObject = new Win32GraphicsDevice(paramInt);
/*     */     }
/* 219 */     return (GraphicsDevice)localObject;
/*     */   }
/*     */   
/*     */   public boolean isDisplayLocal() {
/* 223 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isFlipStrategyPreferred(ComponentPeer paramComponentPeer)
/*     */   {
/*     */     GraphicsConfiguration localGraphicsConfiguration;
/* 229 */     if ((paramComponentPeer != null) && ((localGraphicsConfiguration = paramComponentPeer.getGraphicsConfiguration()) != null)) {
/* 230 */       GraphicsDevice localGraphicsDevice = localGraphicsConfiguration.getDevice();
/* 231 */       if ((localGraphicsDevice instanceof D3DGraphicsDevice)) {
/* 232 */         return ((D3DGraphicsDevice)localGraphicsDevice).isD3DEnabledOnDevice();
/*     */       }
/*     */     }
/* 235 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isDWMCompositionEnabled()
/*     */   {
/* 245 */     return isDWMCompositionEnabled;
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
/*     */   private static void dwmCompositionChanged(boolean paramBoolean)
/*     */   {
/* 258 */     isDWMCompositionEnabled = paramBoolean;
/*     */   }
/*     */   
/*     */   private static native void initDisplay();
/*     */   
/*     */   protected native int getNumScreens();
/*     */   
/*     */   protected native int getDefaultScreen();
/*     */   
/*     */   public native int getXResolution();
/*     */   
/*     */   public native int getYResolution();
/*     */   
/*     */   public static native boolean isVistaOS();
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\Win32GraphicsEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.java2d;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import sun.awt.DisplayChangedListener;
/*     */ import sun.awt.image.SurfaceManager.FlushableCacheData;
/*     */ import sun.java2d.loops.Blit;
/*     */ import sun.java2d.loops.BlitBg;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SurfaceDataProxy
/*     */   implements DisplayChangedListener, SurfaceManager.FlushableCacheData
/*     */ {
/*  73 */   private static boolean cachingAllowed = true;
/*  74 */   static { String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.managedimages"));
/*     */     
/*  76 */     if ((str1 != null) && (str1.equals("false"))) {
/*  77 */       cachingAllowed = false;
/*  78 */       System.out.println("Disabling managed images");
/*     */     }
/*     */     
/*  81 */     defaultThreshold = 1;
/*  82 */     String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.accthreshold"));
/*     */     
/*  84 */     if (str2 != null) {
/*     */       try {
/*  86 */         int i = Integer.parseInt(str2);
/*  87 */         if (i >= 0) {
/*  88 */           defaultThreshold = i;
/*  89 */           System.out.println("New Default Acceleration Threshold: " + defaultThreshold);
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException) {
/*  93 */         System.err.println("Error setting new threshold:" + localNumberFormatException);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isCachingAllowed() {
/*  99 */     return cachingAllowed;
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
/*     */   private static int defaultThreshold;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StateTracker getRetryTracker(SurfaceData paramSurfaceData)
/*     */   {
/* 137 */     return new CountdownTracker(this.threshold);
/*     */   }
/*     */   
/*     */   public static class CountdownTracker implements StateTracker {
/*     */     private int countdown;
/*     */     
/*     */     public CountdownTracker(int paramInt) {
/* 144 */       this.countdown = paramInt;
/*     */     }
/*     */     
/*     */     public synchronized boolean isCurrent() {
/* 148 */       return --this.countdown >= 0;
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
/* 162 */   public static SurfaceDataProxy UNCACHED = new SurfaceDataProxy(0)
/*     */   {
/*     */     public boolean isAccelerated() {
/* 165 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isSupportedOperation(SurfaceData paramAnonymousSurfaceData, int paramAnonymousInt, CompositeType paramAnonymousCompositeType, Color paramAnonymousColor)
/*     */     {
/* 174 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public SurfaceData validateSurfaceData(SurfaceData paramAnonymousSurfaceData1, SurfaceData paramAnonymousSurfaceData2, int paramAnonymousInt1, int paramAnonymousInt2)
/*     */     {
/* 182 */       throw new InternalError("UNCACHED should never validate SDs");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public SurfaceData replaceData(SurfaceData paramAnonymousSurfaceData, int paramAnonymousInt, CompositeType paramAnonymousCompositeType, Color paramAnonymousColor)
/*     */     {
/* 192 */       return paramAnonymousSurfaceData;
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int threshold;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private StateTracker srcTracker;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int numtries;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SurfaceData cachedSD;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private StateTracker cacheTracker;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean valid;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SurfaceDataProxy()
/*     */   {
/* 243 */     this(defaultThreshold);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SurfaceDataProxy(int paramInt)
/*     */   {
/* 253 */     this.threshold = paramInt;
/*     */     
/* 255 */     this.srcTracker = StateTracker.NEVER_CURRENT;
/*     */     
/* 257 */     this.cacheTracker = StateTracker.NEVER_CURRENT;
/*     */     
/* 259 */     this.valid = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/* 268 */     return this.valid;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void invalidate()
/*     */   {
/* 277 */     this.valid = false;
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
/*     */   public boolean flush(boolean paramBoolean)
/*     */   {
/* 290 */     if (paramBoolean) {
/* 291 */       invalidate();
/*     */     }
/* 293 */     flush();
/* 294 */     return !isValid();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void flush()
/*     */   {
/* 302 */     SurfaceData localSurfaceData = this.cachedSD;
/* 303 */     this.cachedSD = null;
/* 304 */     this.cacheTracker = StateTracker.NEVER_CURRENT;
/* 305 */     if (localSurfaceData != null) {
/* 306 */       localSurfaceData.flush();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isAccelerated()
/*     */   {
/* 316 */     return (isValid()) && 
/* 317 */       (this.srcTracker.isCurrent()) && 
/* 318 */       (this.cacheTracker.isCurrent());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void activateDisplayListener()
/*     */   {
/* 328 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 333 */     if ((localGraphicsEnvironment instanceof SunGraphicsEnvironment)) {
/* 334 */       ((SunGraphicsEnvironment)localGraphicsEnvironment).addDisplayChangedListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void displayChanged()
/*     */   {
/* 343 */     flush();
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
/*     */   public void paletteChanged()
/*     */   {
/* 362 */     this.srcTracker = StateTracker.NEVER_CURRENT;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public SurfaceData replaceData(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor)
/*     */   {
/* 398 */     if (isSupportedOperation(paramSurfaceData, paramInt, paramCompositeType, paramColor))
/*     */     {
/* 400 */       if (!this.srcTracker.isCurrent()) {
/* 401 */         synchronized (this) {
/* 402 */           this.numtries = this.threshold;
/* 403 */           this.srcTracker = paramSurfaceData.getStateTracker();
/* 404 */           this.cacheTracker = StateTracker.NEVER_CURRENT;
/*     */         }
/*     */         
/* 407 */         if (!this.srcTracker.isCurrent())
/*     */         {
/* 409 */           if (paramSurfaceData.getState() == StateTrackable.State.UNTRACKABLE)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 414 */             invalidate();
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 419 */             flush();
/*     */           }
/* 421 */           return paramSurfaceData;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 426 */       ??? = this.cachedSD;
/* 427 */       if (!this.cacheTracker.isCurrent())
/*     */       {
/* 429 */         synchronized (this) {
/* 430 */           if (this.numtries > 0) {
/* 431 */             this.numtries -= 1;
/* 432 */             return paramSurfaceData;
/*     */           }
/*     */         }
/*     */         
/* 436 */         ??? = paramSurfaceData.getBounds();
/* 437 */         int i = ((Rectangle)???).width;
/* 438 */         int j = ((Rectangle)???).height;
/*     */         
/*     */ 
/*     */ 
/* 442 */         StateTracker localStateTracker = this.srcTracker;
/*     */         
/* 444 */         ??? = validateSurfaceData(paramSurfaceData, (SurfaceData)???, i, j);
/* 445 */         if (??? == null) {
/* 446 */           synchronized (this) {
/* 447 */             if (localStateTracker == this.srcTracker) {
/* 448 */               this.cacheTracker = getRetryTracker(paramSurfaceData);
/* 449 */               this.cachedSD = null;
/*     */             }
/*     */           }
/* 452 */           return paramSurfaceData;
/*     */         }
/*     */         
/* 455 */         updateSurfaceData(paramSurfaceData, (SurfaceData)???, i, j);
/* 456 */         if (!((SurfaceData)???).isValid()) {
/* 457 */           return paramSurfaceData;
/*     */         }
/*     */         
/* 460 */         synchronized (this)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 466 */           if ((localStateTracker == this.srcTracker) && (localStateTracker.isCurrent())) {
/* 467 */             this.cacheTracker = ((SurfaceData)???).getStateTracker();
/* 468 */             this.cachedSD = ((SurfaceData)???);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 473 */       if (??? != null) {
/* 474 */         return (SurfaceData)???;
/*     */       }
/*     */     }
/*     */     
/* 478 */     return paramSurfaceData;
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
/*     */   public void updateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2)
/*     */   {
/* 493 */     SurfaceType localSurfaceType1 = paramSurfaceData1.getSurfaceType();
/* 494 */     SurfaceType localSurfaceType2 = paramSurfaceData2.getSurfaceType();
/* 495 */     Blit localBlit = Blit.getFromCache(localSurfaceType1, CompositeType.SrcNoEa, localSurfaceType2);
/*     */     
/*     */ 
/* 498 */     localBlit.Blit(paramSurfaceData1, paramSurfaceData2, AlphaComposite.Src, null, 0, 0, 0, 0, paramInt1, paramInt2);
/*     */     
/*     */ 
/* 501 */     paramSurfaceData2.markDirty();
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
/*     */   public void updateSurfaceDataBg(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2, Color paramColor)
/*     */   {
/* 518 */     SurfaceType localSurfaceType1 = paramSurfaceData1.getSurfaceType();
/* 519 */     SurfaceType localSurfaceType2 = paramSurfaceData2.getSurfaceType();
/* 520 */     BlitBg localBlitBg = BlitBg.getFromCache(localSurfaceType1, CompositeType.SrcNoEa, localSurfaceType2);
/*     */     
/*     */ 
/* 523 */     localBlitBg.BlitBg(paramSurfaceData1, paramSurfaceData2, AlphaComposite.Src, null, paramColor
/* 524 */       .getRGB(), 0, 0, 0, 0, paramInt1, paramInt2);
/*     */     
/* 526 */     paramSurfaceData2.markDirty();
/*     */   }
/*     */   
/*     */   public abstract boolean isSupportedOperation(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor);
/*     */   
/*     */   public abstract SurfaceData validateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\SurfaceDataProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
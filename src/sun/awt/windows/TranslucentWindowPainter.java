/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Image;
/*     */ import java.awt.Window;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.VolatileImage;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.security.AccessController;
/*     */ import sun.awt.image.BufImgSurfaceData;
/*     */ import sun.java2d.DestSurfaceProvider;
/*     */ import sun.java2d.InvalidPipeException;
/*     */ import sun.java2d.Surface;
/*     */ import sun.java2d.d3d.D3DSurfaceData;
/*     */ import sun.java2d.opengl.WGLSurfaceData;
/*     */ import sun.java2d.pipe.BufferedContext;
/*     */ import sun.java2d.pipe.RenderQueue;
/*     */ import sun.java2d.pipe.hw.AccelGraphicsConfig;
/*     */ import sun.java2d.pipe.hw.AccelSurface;
/*     */ import sun.java2d.pipe.hw.ContextCapabilities;
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
/*     */ abstract class TranslucentWindowPainter
/*     */ {
/*     */   protected Window window;
/*     */   protected WWindowPeer peer;
/*  67 */   private static final boolean forceOpt = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.twp.forceopt", "false"))).booleanValue();
/*     */   
/*     */ 
/*  70 */   private static final boolean forceSW = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.twp.forcesw", "false"))).booleanValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TranslucentWindowPainter createInstance(WWindowPeer paramWWindowPeer)
/*     */   {
/*  77 */     GraphicsConfiguration localGraphicsConfiguration = paramWWindowPeer.getGraphicsConfiguration();
/*  78 */     if ((!forceSW) && ((localGraphicsConfiguration instanceof AccelGraphicsConfig))) {
/*  79 */       String str = localGraphicsConfiguration.getClass().getSimpleName();
/*  80 */       AccelGraphicsConfig localAccelGraphicsConfig = (AccelGraphicsConfig)localGraphicsConfiguration;
/*     */       
/*     */ 
/*  83 */       if (((localAccelGraphicsConfig.getContextCapabilities().getCaps() & 0x100) != 0) || (forceOpt))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  88 */         if (str.startsWith("D3D"))
/*  89 */           return new VIOptD3DWindowPainter(paramWWindowPeer);
/*  90 */         if ((forceOpt) && (str.startsWith("WGL")))
/*     */         {
/*     */ 
/*     */ 
/*  94 */           return new VIOptWGLWindowPainter(paramWWindowPeer);
/*     */         }
/*     */       }
/*     */     }
/*  98 */     return new BIWindowPainter(paramWWindowPeer);
/*     */   }
/*     */   
/*     */   protected TranslucentWindowPainter(WWindowPeer paramWWindowPeer) {
/* 102 */     this.peer = paramWWindowPeer;
/* 103 */     this.window = ((Window)paramWWindowPeer.getTarget());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract Image getBackBuffer(boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract boolean update(Image paramImage);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void flush();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateWindow(boolean paramBoolean)
/*     */   {
/* 132 */     boolean bool = false;
/* 133 */     Image localImage = getBackBuffer(paramBoolean);
/* 134 */     while (!bool) {
/* 135 */       if (paramBoolean) {
/* 136 */         Graphics2D localGraphics2D = (Graphics2D)localImage.getGraphics();
/*     */         try {
/* 138 */           this.window.paintAll(localGraphics2D);
/*     */         } finally {
/* 140 */           localGraphics2D.dispose();
/*     */         }
/*     */       }
/*     */       
/* 144 */       bool = update(localImage);
/* 145 */       if (!bool) {
/* 146 */         paramBoolean = true;
/* 147 */         localImage = getBackBuffer(true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static final Image clearImage(Image paramImage) {
/* 153 */     Graphics2D localGraphics2D = (Graphics2D)paramImage.getGraphics();
/* 154 */     int i = paramImage.getWidth(null);
/* 155 */     int j = paramImage.getHeight(null);
/*     */     
/* 157 */     localGraphics2D.setComposite(AlphaComposite.Src);
/* 158 */     localGraphics2D.setColor(new Color(0, 0, 0, 0));
/* 159 */     localGraphics2D.fillRect(0, 0, i, j);
/*     */     
/* 161 */     return paramImage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class BIWindowPainter
/*     */     extends TranslucentWindowPainter
/*     */   {
/*     */     private BufferedImage backBuffer;
/*     */     
/*     */ 
/*     */ 
/*     */     protected BIWindowPainter(WWindowPeer paramWWindowPeer)
/*     */     {
/* 176 */       super();
/*     */     }
/*     */     
/*     */     protected Image getBackBuffer(boolean paramBoolean)
/*     */     {
/* 181 */       int i = this.window.getWidth();
/* 182 */       int j = this.window.getHeight();
/* 183 */       if ((this.backBuffer == null) || 
/* 184 */         (this.backBuffer.getWidth() != i) || 
/* 185 */         (this.backBuffer.getHeight() != j))
/*     */       {
/* 187 */         flush();
/* 188 */         this.backBuffer = new BufferedImage(i, j, 3);
/*     */       }
/* 190 */       return paramBoolean ? (BufferedImage)TranslucentWindowPainter.clearImage(this.backBuffer) : this.backBuffer;
/*     */     }
/*     */     
/*     */     protected boolean update(Image paramImage)
/*     */     {
/* 195 */       VolatileImage localVolatileImage = null;
/*     */       
/* 197 */       if ((paramImage instanceof BufferedImage)) {
/* 198 */         localObject = (BufferedImage)paramImage;
/*     */         
/* 200 */         int[] arrayOfInt1 = ((DataBufferInt)((BufferedImage)localObject).getRaster().getDataBuffer()).getData();
/* 201 */         this.peer.updateWindowImpl(arrayOfInt1, ((BufferedImage)localObject).getWidth(), ((BufferedImage)localObject).getHeight());
/* 202 */         return true; }
/* 203 */       if ((paramImage instanceof VolatileImage)) {
/* 204 */         localVolatileImage = (VolatileImage)paramImage;
/* 205 */         if ((paramImage instanceof DestSurfaceProvider)) {
/* 206 */           localObject = ((DestSurfaceProvider)paramImage).getDestSurface();
/* 207 */           if ((localObject instanceof BufImgSurfaceData))
/*     */           {
/*     */ 
/*     */ 
/* 211 */             int i = localVolatileImage.getWidth();
/* 212 */             int j = localVolatileImage.getHeight();
/* 213 */             BufImgSurfaceData localBufImgSurfaceData = (BufImgSurfaceData)localObject;
/*     */             
/* 215 */             int[] arrayOfInt3 = ((DataBufferInt)localBufImgSurfaceData.getRaster(0, 0, i, j).getDataBuffer()).getData();
/* 216 */             this.peer.updateWindowImpl(arrayOfInt3, i, j);
/* 217 */             return true;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 223 */       Object localObject = (BufferedImage)TranslucentWindowPainter.clearImage(this.backBuffer);
/*     */       
/*     */ 
/* 226 */       int[] arrayOfInt2 = ((DataBufferInt)((BufferedImage)localObject).getRaster().getDataBuffer()).getData();
/* 227 */       this.peer.updateWindowImpl(arrayOfInt2, ((BufferedImage)localObject).getWidth(), ((BufferedImage)localObject).getHeight());
/*     */       
/* 229 */       return !localVolatileImage.contentsLost();
/*     */     }
/*     */     
/*     */     public void flush()
/*     */     {
/* 234 */       if (this.backBuffer != null) {
/* 235 */         this.backBuffer.flush();
/* 236 */         this.backBuffer = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class VIWindowPainter
/*     */     extends BIWindowPainter
/*     */   {
/*     */     private VolatileImage viBB;
/*     */     
/*     */ 
/*     */     protected VIWindowPainter(WWindowPeer paramWWindowPeer)
/*     */     {
/* 250 */       super();
/*     */     }
/*     */     
/*     */     protected Image getBackBuffer(boolean paramBoolean)
/*     */     {
/* 255 */       int i = this.window.getWidth();
/* 256 */       int j = this.window.getHeight();
/* 257 */       GraphicsConfiguration localGraphicsConfiguration = this.peer.getGraphicsConfiguration();
/*     */       
/* 259 */       if ((this.viBB == null) || (this.viBB.getWidth() != i) || (this.viBB.getHeight() != j) || 
/* 260 */         (this.viBB.validate(localGraphicsConfiguration) == 2))
/*     */       {
/* 262 */         flush();
/*     */         
/* 264 */         if ((localGraphicsConfiguration instanceof AccelGraphicsConfig)) {
/* 265 */           AccelGraphicsConfig localAccelGraphicsConfig = (AccelGraphicsConfig)localGraphicsConfiguration;
/* 266 */           this.viBB = localAccelGraphicsConfig.createCompatibleVolatileImage(i, j, 3, 2);
/*     */         }
/*     */         
/*     */ 
/* 270 */         if (this.viBB == null) {
/* 271 */           this.viBB = localGraphicsConfiguration.createCompatibleVolatileImage(i, j, 3);
/*     */         }
/* 273 */         this.viBB.validate(localGraphicsConfiguration);
/*     */       }
/*     */       
/* 276 */       return paramBoolean ? TranslucentWindowPainter.clearImage(this.viBB) : this.viBB;
/*     */     }
/*     */     
/*     */     public void flush()
/*     */     {
/* 281 */       if (this.viBB != null) {
/* 282 */         this.viBB.flush();
/* 283 */         this.viBB = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static abstract class VIOptWindowPainter
/*     */     extends VIWindowPainter
/*     */   {
/*     */     protected VIOptWindowPainter(WWindowPeer paramWWindowPeer)
/*     */     {
/* 296 */       super();
/*     */     }
/*     */     
/*     */     protected abstract boolean updateWindowAccel(long paramLong, int paramInt1, int paramInt2);
/*     */     
/*     */     protected boolean update(Image paramImage)
/*     */     {
/* 303 */       if ((paramImage instanceof DestSurfaceProvider)) {
/* 304 */         Surface localSurface = ((DestSurfaceProvider)paramImage).getDestSurface();
/* 305 */         if ((localSurface instanceof AccelSurface)) {
/* 306 */           final int i = paramImage.getWidth(null);
/* 307 */           final int j = paramImage.getHeight(null);
/* 308 */           final boolean[] arrayOfBoolean = { false };
/* 309 */           final AccelSurface localAccelSurface = (AccelSurface)localSurface;
/* 310 */           RenderQueue localRenderQueue = localAccelSurface.getContext().getRenderQueue();
/* 311 */           localRenderQueue.lock();
/*     */           try {
/* 313 */             BufferedContext.validateContext(localAccelSurface);
/* 314 */             localRenderQueue.flushAndInvokeNow(new Runnable()
/*     */             {
/*     */               public void run() {
/* 317 */                 long l = localAccelSurface.getNativeOps();
/* 318 */                 arrayOfBoolean[0] = VIOptWindowPainter.this.updateWindowAccel(l, i, j);
/*     */               }
/*     */             });
/*     */           }
/*     */           catch (InvalidPipeException localInvalidPipeException) {}finally
/*     */           {
/* 324 */             localRenderQueue.unlock();
/*     */           }
/* 326 */           return arrayOfBoolean[0];
/*     */         }
/*     */       }
/* 329 */       return super.update(paramImage);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class VIOptD3DWindowPainter extends VIOptWindowPainter
/*     */   {
/*     */     protected VIOptD3DWindowPainter(WWindowPeer paramWWindowPeer) {
/* 336 */       super();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected boolean updateWindowAccel(long paramLong, int paramInt1, int paramInt2)
/*     */     {
/* 343 */       return 
/* 344 */         D3DSurfaceData.updateWindowAccelImpl(paramLong, this.peer.getData(), paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class VIOptWGLWindowPainter extends VIOptWindowPainter
/*     */   {
/*     */     protected VIOptWGLWindowPainter(WWindowPeer paramWWindowPeer) {
/* 351 */       super();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected boolean updateWindowAccel(long paramLong, int paramInt1, int paramInt2)
/*     */     {
/* 358 */       return 
/* 359 */         WGLSurfaceData.updateWindowAccelImpl(paramLong, this.peer, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\TranslucentWindowPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
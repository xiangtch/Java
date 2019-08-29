/*     */ package sun.swing;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.VolatileImage;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class CachedPainter
/*     */ {
/*  56 */   private static final Map<Object, ImageCache> cacheMap = new HashMap();
/*     */   
/*     */   private static ImageCache getCache(Object paramObject) {
/*  59 */     synchronized (CachedPainter.class) {
/*  60 */       ImageCache localImageCache = (ImageCache)cacheMap.get(paramObject);
/*  61 */       if (localImageCache == null) {
/*  62 */         localImageCache = new ImageCache(1);
/*  63 */         cacheMap.put(paramObject, localImageCache);
/*     */       }
/*  65 */       return localImageCache;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CachedPainter(int paramInt)
/*     */   {
/*  76 */     getCache(getClass()).setMaxCount(paramInt);
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
/*     */   public void paint(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object... paramVarArgs)
/*     */   {
/*  94 */     if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
/*  95 */       return;
/*     */     }
/*  97 */     synchronized (CachedPainter.class) {
/*  98 */       paint0(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramVarArgs);
/*     */     }
/*     */   }
/*     */   
/*     */   private void paint0(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object... paramVarArgs)
/*     */   {
/* 104 */     Class localClass = getClass();
/* 105 */     GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration(paramComponent);
/* 106 */     ImageCache localImageCache = getCache(localClass);
/* 107 */     Image localImage = localImageCache.getImage(localClass, localGraphicsConfiguration, paramInt3, paramInt4, paramVarArgs);
/* 108 */     int i = 0;
/*     */     do {
/* 110 */       int j = 0;
/* 111 */       if ((localImage instanceof VolatileImage))
/*     */       {
/* 113 */         switch (((VolatileImage)localImage).validate(localGraphicsConfiguration)) {
/*     */         case 2: 
/* 115 */           ((VolatileImage)localImage).flush();
/* 116 */           localImage = null;
/* 117 */           break;
/*     */         case 1: 
/* 119 */           j = 1;
/*     */         }
/*     */         
/*     */       }
/* 123 */       if (localImage == null)
/*     */       {
/* 125 */         localImage = createImage(paramComponent, paramInt3, paramInt4, localGraphicsConfiguration, paramVarArgs);
/* 126 */         localImageCache.setImage(localClass, localGraphicsConfiguration, paramInt3, paramInt4, paramVarArgs, localImage);
/* 127 */         j = 1;
/*     */       }
/* 129 */       if (j != 0)
/*     */       {
/* 131 */         Graphics localGraphics = localImage.getGraphics();
/* 132 */         paintToImage(paramComponent, localImage, localGraphics, paramInt3, paramInt4, paramVarArgs);
/* 133 */         localGraphics.dispose();
/*     */       }
/*     */       
/*     */ 
/* 137 */       paintImage(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, localImage, paramVarArgs);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 142 */       if ((!(localImage instanceof VolatileImage)) || 
/* 143 */         (!((VolatileImage)localImage).contentsLost())) break; i++; } while (i < 3);
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
/*     */   protected abstract void paintToImage(Component paramComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, Object[] paramArrayOfObject);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void paintImage(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Image paramImage, Object[] paramArrayOfObject)
/*     */   {
/* 175 */     paramGraphics.drawImage(paramImage, paramInt1, paramInt2, null);
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
/*     */   protected Image createImage(Component paramComponent, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, Object[] paramArrayOfObject)
/*     */   {
/* 192 */     if (paramGraphicsConfiguration == null) {
/* 193 */       return new BufferedImage(paramInt1, paramInt2, 1);
/*     */     }
/* 195 */     return paramGraphicsConfiguration.createCompatibleVolatileImage(paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void flush()
/*     */   {
/* 202 */     synchronized (CachedPainter.class) {
/* 203 */       getCache(getClass()).flush();
/*     */     }
/*     */   }
/*     */   
/*     */   private GraphicsConfiguration getGraphicsConfiguration(Component paramComponent) {
/* 208 */     if (paramComponent == null) {
/* 209 */       return null;
/*     */     }
/* 211 */     return paramComponent.getGraphicsConfiguration();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\swing\CachedPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
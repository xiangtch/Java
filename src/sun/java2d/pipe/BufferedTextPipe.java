/*     */ package sun.java2d.pipe;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Composite;
/*     */ import sun.font.GlyphList;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BufferedTextPipe
/*     */   extends GlyphListPipe
/*     */ {
/*     */   private static final int BYTES_PER_GLYPH_IMAGE = 8;
/*     */   private static final int BYTES_PER_GLYPH_POSITION = 8;
/*     */   private static final int OFFSET_CONTRAST = 8;
/*     */   private static final int OFFSET_RGBORDER = 2;
/*     */   private static final int OFFSET_SUBPIXPOS = 1;
/*     */   private static final int OFFSET_POSITIONS = 0;
/*     */   protected final RenderQueue rq;
/*     */   
/*     */   private static int createPackedParams(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
/*     */   {
/*  60 */     return 
/*     */     
/*     */ 
/*  63 */       (paramGlyphList.usePositions() ? 1 : 0) << 0 | (paramGlyphList.isSubPixPos() ? 1 : 0) << 1 | (paramGlyphList.isRGBOrder() ? 1 : 0) << 2 | (paramSunGraphics2D.lcdTextContrast & 0xFF) << 8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected BufferedTextPipe(RenderQueue paramRenderQueue)
/*     */   {
/*  70 */     this.rq = paramRenderQueue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
/*     */   {
/*  80 */     Object localObject1 = paramSunGraphics2D.composite;
/*  81 */     if (localObject1 == AlphaComposite.Src)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */       localObject1 = AlphaComposite.SrcOver;
/*     */     }
/*     */     
/*  93 */     this.rq.lock();
/*     */     try {
/*  95 */       validateContext(paramSunGraphics2D, (Composite)localObject1);
/*  96 */       enqueueGlyphList(paramSunGraphics2D, paramGlyphList);
/*     */     } finally {
/*  98 */       this.rq.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void enqueueGlyphList(final SunGraphics2D paramSunGraphics2D, final GlyphList paramGlyphList)
/*     */   {
/* 106 */     RenderBuffer localRenderBuffer = this.rq.getBuffer();
/* 107 */     final int i = paramGlyphList.getNumGlyphs();
/* 108 */     int j = i * 8;
/*     */     
/* 110 */     int k = paramGlyphList.usePositions() ? i * 8 : 0;
/* 111 */     int m = 24 + j + k;
/*     */     
/* 113 */     final long[] arrayOfLong = paramGlyphList.getImages();
/* 114 */     final float f1 = paramGlyphList.getX() + 0.5F;
/* 115 */     final float f2 = paramGlyphList.getY() + 0.5F;
/*     */     
/*     */ 
/*     */ 
/* 119 */     this.rq.addReference(paramGlyphList.getStrike());
/*     */     
/* 121 */     if (m <= localRenderBuffer.capacity()) {
/* 122 */       if (m > localRenderBuffer.remaining())
/*     */       {
/* 124 */         this.rq.flushNow();
/*     */       }
/* 126 */       this.rq.ensureAlignment(20);
/* 127 */       localRenderBuffer.putInt(40);
/*     */       
/* 129 */       localRenderBuffer.putInt(i);
/* 130 */       localRenderBuffer.putInt(createPackedParams(paramSunGraphics2D, paramGlyphList));
/* 131 */       localRenderBuffer.putFloat(f1);
/* 132 */       localRenderBuffer.putFloat(f2);
/*     */       
/* 134 */       localRenderBuffer.put(arrayOfLong, 0, i);
/* 135 */       if (paramGlyphList.usePositions()) {
/* 136 */         float[] arrayOfFloat = paramGlyphList.getPositions();
/* 137 */         localRenderBuffer.put(arrayOfFloat, 0, 2 * i);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 142 */       this.rq.flushAndInvokeNow(new Runnable() {
/*     */         public void run() {
/* 144 */           BufferedTextPipe.this.drawGlyphList(i, paramGlyphList.usePositions(), paramGlyphList
/* 145 */             .isSubPixPos(), paramGlyphList.isRGBOrder(), paramSunGraphics2D.lcdTextContrast, f1, f2, arrayOfLong, paramGlyphList
/*     */             
/*     */ 
/* 148 */             .getPositions());
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   protected abstract void drawGlyphList(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, float paramFloat1, float paramFloat2, long[] paramArrayOfLong, float[] paramArrayOfFloat);
/*     */   
/*     */   protected abstract void validateContext(SunGraphics2D paramSunGraphics2D, Composite paramComposite);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\BufferedTextPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
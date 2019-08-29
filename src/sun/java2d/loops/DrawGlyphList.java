/*     */ package sun.java2d.loops;
/*     */ 
/*     */ import sun.font.GlyphList;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DrawGlyphList
/*     */   extends GraphicsPrimitive
/*     */ {
/*  42 */   public static final String methodSignature = "DrawGlyphList(...)".toString();
/*     */   
/*  44 */   public static final int primTypeID = makePrimTypeID();
/*     */   
/*     */ 
/*     */ 
/*     */   public static DrawGlyphList locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  50 */     return 
/*  51 */       (DrawGlyphList)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DrawGlyphList(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  59 */     super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DrawGlyphList(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  67 */     super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  76 */     GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphList(null, null, null));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  83 */     return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */   private static class General
/*     */     extends DrawGlyphList
/*     */   {
/*     */     MaskFill maskop;
/*     */     
/*     */     public General(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */     {
/*  93 */       super(paramCompositeType, paramSurfaceType2);
/*  94 */       this.maskop = MaskFill.locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */     }
/*     */     
/*     */ 
/*     */     public void DrawGlyphList(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList)
/*     */     {
/* 100 */       int[] arrayOfInt1 = paramGlyphList.getBounds();
/* 101 */       int i = paramGlyphList.getNumGlyphs();
/* 102 */       Region localRegion = paramSunGraphics2D.getCompClip();
/* 103 */       int j = localRegion.getLoX();
/* 104 */       int k = localRegion.getLoY();
/* 105 */       int m = localRegion.getHiX();
/* 106 */       int n = localRegion.getHiY();
/* 107 */       for (int i1 = 0; i1 < i; i1++) {
/* 108 */         paramGlyphList.setGlyphIndex(i1);
/* 109 */         int[] arrayOfInt2 = paramGlyphList.getMetrics();
/* 110 */         int i2 = arrayOfInt2[0];
/* 111 */         int i3 = arrayOfInt2[1];
/* 112 */         int i4 = arrayOfInt2[2];
/* 113 */         int i5 = i2 + i4;
/* 114 */         int i6 = i3 + arrayOfInt2[3];
/* 115 */         int i7 = 0;
/* 116 */         if (i2 < j) {
/* 117 */           i7 = j - i2;
/* 118 */           i2 = j;
/*     */         }
/* 120 */         if (i3 < k) {
/* 121 */           i7 += (k - i3) * i4;
/* 122 */           i3 = k;
/*     */         }
/* 124 */         if (i5 > m) i5 = m;
/* 125 */         if (i6 > n) i6 = n;
/* 126 */         if ((i5 > i2) && (i6 > i3)) {
/* 127 */           byte[] arrayOfByte = paramGlyphList.getGrayBits();
/* 128 */           this.maskop.MaskFill(paramSunGraphics2D, paramSurfaceData, paramSunGraphics2D.composite, i2, i3, i5 - i2, i6 - i3, arrayOfByte, i7, i4);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 137 */   public GraphicsPrimitive traceWrap() { return new TraceDrawGlyphList(this); }
/*     */   
/*     */   public native void DrawGlyphList(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList);
/*     */   
/*     */   private static class TraceDrawGlyphList extends DrawGlyphList {
/*     */     DrawGlyphList target;
/*     */     
/* 144 */     public TraceDrawGlyphList(DrawGlyphList paramDrawGlyphList) { super(paramDrawGlyphList
/* 145 */         .getCompositeType(), paramDrawGlyphList
/* 146 */         .getDestType());
/* 147 */       this.target = paramDrawGlyphList;
/*     */     }
/*     */     
/*     */     public GraphicsPrimitive traceWrap() {
/* 151 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */     public void DrawGlyphList(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList)
/*     */     {
/* 157 */       tracePrimitive(this.target);
/* 158 */       this.target.DrawGlyphList(paramSunGraphics2D, paramSurfaceData, paramGlyphList);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\loops\DrawGlyphList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
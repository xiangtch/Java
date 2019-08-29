/*     */ package sun.java2d.loops;
/*     */ 
/*     */ import sun.font.GlyphList;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DrawGlyphListLCD
/*     */   extends GraphicsPrimitive
/*     */ {
/*  43 */   public static final String methodSignature = "DrawGlyphListLCD(...)".toString();
/*     */   
/*  45 */   public static final int primTypeID = makePrimTypeID();
/*     */   
/*     */ 
/*     */ 
/*     */   public static DrawGlyphListLCD locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  51 */     return 
/*  52 */       (DrawGlyphListLCD)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DrawGlyphListLCD(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  60 */     super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DrawGlyphListLCD(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  68 */     super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  76 */     GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphListLCD(null, null, null));
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
/*     */   public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  89 */     return null;
/*     */   }
/*     */   
/*     */ 
/*  93 */   public GraphicsPrimitive traceWrap() { return new TraceDrawGlyphListLCD(this); }
/*     */   
/*     */   public native void DrawGlyphListLCD(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList);
/*     */   
/*     */   private static class TraceDrawGlyphListLCD extends DrawGlyphListLCD {
/*     */     DrawGlyphListLCD target;
/*     */     
/* 100 */     public TraceDrawGlyphListLCD(DrawGlyphListLCD paramDrawGlyphListLCD) { super(paramDrawGlyphListLCD
/* 101 */         .getCompositeType(), paramDrawGlyphListLCD
/* 102 */         .getDestType());
/* 103 */       this.target = paramDrawGlyphListLCD;
/*     */     }
/*     */     
/*     */     public GraphicsPrimitive traceWrap() {
/* 107 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */     public void DrawGlyphListLCD(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList)
/*     */     {
/* 113 */       tracePrimitive(this.target);
/* 114 */       this.target.DrawGlyphListLCD(paramSunGraphics2D, paramSurfaceData, paramGlyphList);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\loops\DrawGlyphListLCD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
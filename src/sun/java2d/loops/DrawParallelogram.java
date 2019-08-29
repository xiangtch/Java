/*     */ package sun.java2d.loops;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DrawParallelogram
/*     */   extends GraphicsPrimitive
/*     */ {
/*  45 */   public static final String methodSignature = "DrawParallelogram(...)"
/*  46 */     .toString();
/*     */   
/*  48 */   public static final int primTypeID = makePrimTypeID();
/*     */   
/*     */ 
/*     */ 
/*     */   public static DrawParallelogram locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  54 */     return 
/*  55 */       (DrawParallelogram)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DrawParallelogram(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  63 */     super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DrawParallelogram(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  72 */     super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public native void DrawParallelogram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  90 */     throw new InternalError("DrawParallelogram not implemented for " + paramSurfaceType1 + " with " + paramCompositeType);
/*     */   }
/*     */   
/*     */   public GraphicsPrimitive traceWrap()
/*     */   {
/*  95 */     return new TraceDrawParallelogram(this);
/*     */   }
/*     */   
/*     */   private static class TraceDrawParallelogram extends DrawParallelogram {
/*     */     DrawParallelogram target;
/*     */     
/*     */     public TraceDrawParallelogram(DrawParallelogram paramDrawParallelogram) {
/* 102 */       super(paramDrawParallelogram
/* 103 */         .getCompositeType(), paramDrawParallelogram
/* 104 */         .getDestType());
/* 105 */       this.target = paramDrawParallelogram;
/*     */     }
/*     */     
/*     */     public GraphicsPrimitive traceWrap() {
/* 109 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void DrawParallelogram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8)
/*     */     {
/* 118 */       tracePrimitive(this.target);
/* 119 */       this.target.DrawParallelogram(paramSunGraphics2D, paramSurfaceData, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\loops\DrawParallelogram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
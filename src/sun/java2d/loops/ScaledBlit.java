/*     */ package sun.java2d.loops;
/*     */ 
/*     */ import java.awt.Composite;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ScaledBlit
/*     */   extends GraphicsPrimitive
/*     */ {
/*  48 */   public static final String methodSignature = "ScaledBlit(...)".toString();
/*     */   
/*  50 */   public static final int primTypeID = makePrimTypeID();
/*     */   
/*  52 */   private static RenderCache blitcache = new RenderCache(20);
/*     */   
/*     */ 
/*     */ 
/*     */   public static ScaledBlit locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  58 */     return 
/*  59 */       (ScaledBlit)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ScaledBlit getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  67 */     Object localObject = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*  68 */     if (localObject != null) {
/*  69 */       return (ScaledBlit)localObject;
/*     */     }
/*  71 */     ScaledBlit localScaledBlit = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*  72 */     if (localScaledBlit != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  80 */       blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, localScaledBlit);
/*     */     }
/*  82 */     return localScaledBlit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ScaledBlit(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  89 */     super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ScaledBlit(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  97 */     super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/* 109 */     GraphicsPrimitiveMgr.registerGeneral(new ScaledBlit(null, null, null));
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
/* 122 */     return null;
/*     */   }
/*     */   
/*     */ 
/* 126 */   public GraphicsPrimitive traceWrap() { return new TraceScaledBlit(this); }
/*     */   
/*     */   public native void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
/*     */   
/*     */   private static class TraceScaledBlit extends ScaledBlit {
/*     */     ScaledBlit target;
/*     */     
/* 133 */     public TraceScaledBlit(ScaledBlit paramScaledBlit) { super(paramScaledBlit
/* 134 */         .getCompositeType(), paramScaledBlit
/* 135 */         .getDestType());
/* 136 */       this.target = paramScaledBlit;
/*     */     }
/*     */     
/*     */     public GraphicsPrimitive traceWrap() {
/* 140 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
/*     */     {
/* 150 */       tracePrimitive(this.target);
/* 151 */       this.target.Scale(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\loops\ScaledBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.java2d.pipe;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Shape;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Arc2D.Float;
/*     */ import java.awt.geom.Ellipse2D.Float;
/*     */ import java.awt.geom.Path2D.Float;
/*     */ import java.awt.geom.RoundRectangle2D.Float;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.DrawLine;
/*     */ import sun.java2d.loops.DrawParallelogram;
/*     */ import sun.java2d.loops.DrawPath;
/*     */ import sun.java2d.loops.DrawPolygons;
/*     */ import sun.java2d.loops.DrawRect;
/*     */ import sun.java2d.loops.FillParallelogram;
/*     */ import sun.java2d.loops.FillPath;
/*     */ import sun.java2d.loops.FillRect;
/*     */ import sun.java2d.loops.FillSpans;
/*     */ import sun.java2d.loops.RenderLoops;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LoopPipe
/*     */   implements PixelDrawPipe, PixelFillPipe, ParallelogramPipe, ShapeDrawPipe, LoopBasedPipe
/*     */ {
/*  55 */   static final RenderingEngine RenderEngine = ;
/*     */   
/*     */ 
/*     */   public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  60 */     int i = paramSunGraphics2D.transX;
/*  61 */     int j = paramSunGraphics2D.transY;
/*  62 */     paramSunGraphics2D.loops.drawLineLoop.DrawLine(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramInt1 + i, paramInt2 + j, paramInt3 + i, paramInt4 + j);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  70 */     paramSunGraphics2D.loops.drawRectLoop.DrawRect(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/*  80 */     paramSunGraphics2D.shapepipe.draw(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  88 */     paramSunGraphics2D.shapepipe.draw(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/*  95 */     paramSunGraphics2D.shapepipe.draw(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*     */   {
/* 104 */     int[] arrayOfInt = { paramInt };
/* 105 */     paramSunGraphics2D.loops.drawPolygonsLoop.DrawPolygons(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramArrayOfInt1, paramArrayOfInt2, arrayOfInt, 1, paramSunGraphics2D.transX, paramSunGraphics2D.transY, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*     */   {
/* 116 */     int[] arrayOfInt = { paramInt };
/* 117 */     paramSunGraphics2D.loops.drawPolygonsLoop.DrawPolygons(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramArrayOfInt1, paramArrayOfInt2, arrayOfInt, 1, paramSunGraphics2D.transX, paramSunGraphics2D.transY, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 127 */     paramSunGraphics2D.loops.fillRectLoop.FillRect(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 137 */     paramSunGraphics2D.shapepipe.fill(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 145 */     paramSunGraphics2D.shapepipe.fill(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/* 152 */     paramSunGraphics2D.shapepipe.fill(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
/*     */   {
/* 161 */     ShapeSpanIterator localShapeSpanIterator = getFillSSI(paramSunGraphics2D);
/*     */     try
/*     */     {
/* 164 */       localShapeSpanIterator.setOutputArea(paramSunGraphics2D.getCompClip());
/* 165 */       localShapeSpanIterator.appendPoly(paramArrayOfInt1, paramArrayOfInt2, paramInt, paramSunGraphics2D.transX, paramSunGraphics2D.transY);
/* 166 */       fillSpans(paramSunGraphics2D, localShapeSpanIterator);
/*     */     } finally {
/* 168 */       localShapeSpanIterator.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
/*     */   {
/* 174 */     if (paramSunGraphics2D.strokeState == 0)
/*     */     {
/*     */       int i;
/*     */       int j;
/* 178 */       if (paramSunGraphics2D.transformState <= 1) {
/* 179 */         if ((paramShape instanceof Path2D.Float)) {
/* 180 */           localObject1 = (Path2D.Float)paramShape;
/*     */         } else {
/* 182 */           localObject1 = new Path2D.Float(paramShape);
/*     */         }
/* 184 */         i = paramSunGraphics2D.transX;
/* 185 */         j = paramSunGraphics2D.transY;
/*     */       } else {
/* 187 */         localObject1 = new Path2D.Float(paramShape, paramSunGraphics2D.transform);
/* 188 */         i = 0;
/* 189 */         j = 0;
/*     */       }
/* 191 */       paramSunGraphics2D.loops.drawPathLoop.DrawPath(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), i, j, (Path2D.Float)localObject1);
/*     */       
/* 193 */       return;
/*     */     }
/*     */     
/* 196 */     if (paramSunGraphics2D.strokeState == 3) {
/* 197 */       fill(paramSunGraphics2D, paramSunGraphics2D.stroke.createStrokedShape(paramShape));
/* 198 */       return;
/*     */     }
/*     */     
/* 201 */     Object localObject1 = getStrokeSpans(paramSunGraphics2D, paramShape);
/*     */     try
/*     */     {
/* 204 */       fillSpans(paramSunGraphics2D, (SpanIterator)localObject1);
/*     */     } finally {
/* 206 */       ((ShapeSpanIterator)localObject1).dispose();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ShapeSpanIterator getFillSSI(SunGraphics2D paramSunGraphics2D)
/*     */   {
/* 232 */     boolean bool = ((paramSunGraphics2D.stroke instanceof BasicStroke)) && (paramSunGraphics2D.strokeHint != 2);
/*     */     
/* 234 */     return new ShapeSpanIterator(bool);
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
/*     */   public static ShapeSpanIterator getStrokeSpans(SunGraphics2D paramSunGraphics2D, Shape paramShape)
/*     */   {
/* 267 */     ShapeSpanIterator localShapeSpanIterator = new ShapeSpanIterator(false);
/*     */     try
/*     */     {
/* 270 */       localShapeSpanIterator.setOutputArea(paramSunGraphics2D.getCompClip());
/* 271 */       localShapeSpanIterator.setRule(1);
/*     */       
/* 273 */       BasicStroke localBasicStroke = (BasicStroke)paramSunGraphics2D.stroke;
/* 274 */       boolean bool1 = paramSunGraphics2D.strokeState <= 1;
/* 275 */       boolean bool2 = paramSunGraphics2D.strokeHint != 2;
/*     */       
/*     */ 
/* 278 */       RenderEngine.strokeTo(paramShape, paramSunGraphics2D.transform, localBasicStroke, bool1, bool2, false, localShapeSpanIterator);
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/* 282 */       localShapeSpanIterator.dispose();
/* 283 */       localShapeSpanIterator = null;
/*     */       
/* 285 */       throw new InternalError("Unable to Stroke shape (" + localThrowable.getMessage() + ")", localThrowable);
/*     */     }
/* 287 */     return localShapeSpanIterator;
/*     */   }
/*     */   
/*     */   public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
/* 291 */     if (paramSunGraphics2D.strokeState == 0)
/*     */     {
/*     */       int i;
/*     */       int j;
/* 295 */       if (paramSunGraphics2D.transformState <= 1) {
/* 296 */         if ((paramShape instanceof Path2D.Float)) {
/* 297 */           localObject1 = (Path2D.Float)paramShape;
/*     */         } else {
/* 299 */           localObject1 = new Path2D.Float(paramShape);
/*     */         }
/* 301 */         i = paramSunGraphics2D.transX;
/* 302 */         j = paramSunGraphics2D.transY;
/*     */       } else {
/* 304 */         localObject1 = new Path2D.Float(paramShape, paramSunGraphics2D.transform);
/* 305 */         i = 0;
/* 306 */         j = 0;
/*     */       }
/* 308 */       paramSunGraphics2D.loops.fillPathLoop.FillPath(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), i, j, (Path2D.Float)localObject1);
/*     */       
/* 310 */       return;
/*     */     }
/*     */     
/* 313 */     Object localObject1 = getFillSSI(paramSunGraphics2D);
/*     */     try {
/* 315 */       ((ShapeSpanIterator)localObject1).setOutputArea(paramSunGraphics2D.getCompClip());
/* 316 */       AffineTransform localAffineTransform = paramSunGraphics2D.transformState == 0 ? null : paramSunGraphics2D.transform;
/*     */       
/*     */ 
/*     */ 
/* 320 */       ((ShapeSpanIterator)localObject1).appendPath(paramShape.getPathIterator(localAffineTransform));
/* 321 */       fillSpans(paramSunGraphics2D, (SpanIterator)localObject1);
/*     */     } finally {
/* 323 */       ((ShapeSpanIterator)localObject1).dispose();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void fillSpans(SunGraphics2D paramSunGraphics2D, SpanIterator paramSpanIterator)
/*     */   {
/* 331 */     if (paramSunGraphics2D.clipState == 2) {
/* 332 */       paramSpanIterator = paramSunGraphics2D.clipRegion.filter(paramSpanIterator);
/*     */     }
/*     */     else
/*     */     {
/* 336 */       localObject = paramSunGraphics2D.loops.fillSpansLoop;
/* 337 */       if (localObject != null) {
/* 338 */         ((FillSpans)localObject).FillSpans(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramSpanIterator);
/* 339 */         return;
/*     */       }
/*     */     }
/* 342 */     Object localObject = new int[4];
/* 343 */     SurfaceData localSurfaceData = paramSunGraphics2D.getSurfaceData();
/* 344 */     while (paramSpanIterator.nextSpan((int[])localObject)) {
/* 345 */       int i = localObject[0];
/* 346 */       int j = localObject[1];
/* 347 */       int k = localObject[2] - i;
/* 348 */       int m = localObject[3] - j;
/* 349 */       paramSunGraphics2D.loops.fillRectLoop.FillRect(paramSunGraphics2D, localSurfaceData, i, j, k, m);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10)
/*     */   {
/* 360 */     FillParallelogram localFillParallelogram = paramSunGraphics2D.loops.fillParallelogramLoop;
/* 361 */     localFillParallelogram.FillParallelogram(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12)
/*     */   {
/* 373 */     DrawParallelogram localDrawParallelogram = paramSunGraphics2D.loops.drawParallelogramLoop;
/* 374 */     localDrawParallelogram.DrawParallelogram(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, paramDouble11, paramDouble12);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\LoopPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
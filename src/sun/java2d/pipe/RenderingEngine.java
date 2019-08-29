/*     */ package sun.java2d.pipe;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.PathIterator;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ServiceLoader;
/*     */ import sun.awt.geom.PathConsumer2D;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class RenderingEngine
/*     */ {
/*     */   private static RenderingEngine reImpl;
/*     */   
/*     */   public static synchronized RenderingEngine getInstance()
/*     */   {
/* 116 */     if (reImpl != null) {
/* 117 */       return reImpl;
/*     */     }
/*     */     
/*     */ 
/* 121 */     reImpl = (RenderingEngine)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public RenderingEngine run()
/*     */       {
/* 125 */         String str = System.getProperty("sun.java2d.renderer", "sun.dc.DuctusRenderingEngine");
/* 126 */         if (str.equals("sun.dc.DuctusRenderingEngine")) {
/*     */           try {
/* 128 */             Class localClass = Class.forName("sun.dc.DuctusRenderingEngine");
/* 129 */             return (RenderingEngine)localClass.newInstance();
/*     */           }
/*     */           catch (ReflectiveOperationException localReflectiveOperationException) {}
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 136 */         ServiceLoader localServiceLoader = ServiceLoader.loadInstalled(RenderingEngine.class);
/*     */         
/* 138 */         Object localObject = null;
/*     */         
/* 140 */         for (RenderingEngine localRenderingEngine : localServiceLoader) {
/* 141 */           localObject = localRenderingEngine;
/* 142 */           if (localRenderingEngine.getClass().getName().equals(str)) {
/*     */             break;
/*     */           }
/*     */         }
/* 146 */         return (RenderingEngine)localObject;
/*     */       }
/*     */     });
/*     */     
/* 150 */     if (reImpl == null) {
/* 151 */       throw new InternalError("No RenderingEngine module found");
/*     */     }
/*     */     
/* 154 */     GetPropertyAction localGetPropertyAction = new GetPropertyAction("sun.java2d.renderer.trace");
/*     */     
/* 156 */     String str = (String)AccessController.doPrivileged(localGetPropertyAction);
/* 157 */     if (str != null) {
/* 158 */       reImpl = new Tracer(reImpl);
/*     */     }
/*     */     
/* 161 */     return reImpl;
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
/*     */   public abstract Shape createStrokedShape(Shape paramShape, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PathConsumer2D paramPathConsumer2D);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AATileGenerator getAATileGenerator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Region paramRegion, int[] paramArrayOfInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract float getMinimumAAPenSize();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void feedConsumer(PathIterator paramPathIterator, PathConsumer2D paramPathConsumer2D)
/*     */   {
/* 359 */     float[] arrayOfFloat = new float[6];
/* 360 */     while (!paramPathIterator.isDone()) {
/* 361 */       switch (paramPathIterator.currentSegment(arrayOfFloat)) {
/*     */       case 0: 
/* 363 */         paramPathConsumer2D.moveTo(arrayOfFloat[0], arrayOfFloat[1]);
/* 364 */         break;
/*     */       case 1: 
/* 366 */         paramPathConsumer2D.lineTo(arrayOfFloat[0], arrayOfFloat[1]);
/* 367 */         break;
/*     */       case 2: 
/* 369 */         paramPathConsumer2D.quadTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
/*     */         
/* 371 */         break;
/*     */       case 3: 
/* 373 */         paramPathConsumer2D.curveTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
/*     */         
/*     */ 
/* 376 */         break;
/*     */       case 4: 
/* 378 */         paramPathConsumer2D.closePath();
/*     */       }
/*     */       
/* 381 */       paramPathIterator.next();
/*     */     }
/*     */   }
/*     */   
/*     */   static class Tracer extends RenderingEngine {
/*     */     RenderingEngine target;
/*     */     String name;
/*     */     
/*     */     public Tracer(RenderingEngine paramRenderingEngine) {
/* 390 */       this.target = paramRenderingEngine;
/* 391 */       this.name = paramRenderingEngine.getClass().getName();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Shape createStrokedShape(Shape paramShape, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3)
/*     */     {
/* 402 */       System.out.println(this.name + ".createStrokedShape(" + paramShape
/* 403 */         .getClass().getName() + ", width = " + paramFloat1 + ", caps = " + paramInt1 + ", join = " + paramInt2 + ", miter = " + paramFloat2 + ", dashes = " + paramArrayOfFloat + ", dashphase = " + paramFloat3 + ")");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 410 */       return this.target.createStrokedShape(paramShape, paramFloat1, paramInt1, paramInt2, paramFloat2, paramArrayOfFloat, paramFloat3);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PathConsumer2D paramPathConsumer2D)
/*     */     {
/* 423 */       System.out.println(this.name + ".strokeTo(" + paramShape
/* 424 */         .getClass().getName() + ", " + paramAffineTransform + ", " + paramBasicStroke + ", " + (paramBoolean1 ? "thin" : "wide") + ", " + (paramBoolean2 ? "normalized" : "pure") + ", " + (paramBoolean3 ? "AA" : "non-AA") + ", " + paramPathConsumer2D
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 430 */         .getClass().getName() + ")");
/* 431 */       this.target.strokeTo(paramShape, paramAffineTransform, paramBasicStroke, paramBoolean1, paramBoolean2, paramBoolean3, paramPathConsumer2D);
/*     */     }
/*     */     
/*     */     public float getMinimumAAPenSize() {
/* 435 */       System.out.println(this.name + ".getMinimumAAPenSize()");
/* 436 */       return this.target.getMinimumAAPenSize();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt)
/*     */     {
/* 447 */       System.out.println(this.name + ".getAATileGenerator(" + paramShape
/* 448 */         .getClass().getName() + ", " + paramAffineTransform + ", " + paramRegion + ", " + paramBasicStroke + ", " + (paramBoolean1 ? "thin" : "wide") + ", " + (paramBoolean2 ? "normalized" : "pure") + ")");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 454 */       return this.target.getAATileGenerator(paramShape, paramAffineTransform, paramRegion, paramBasicStroke, paramBoolean1, paramBoolean2, paramArrayOfInt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public AATileGenerator getAATileGenerator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Region paramRegion, int[] paramArrayOfInt)
/*     */     {
/* 465 */       System.out.println(this.name + ".getAATileGenerator(" + paramDouble1 + ", " + paramDouble2 + ", " + paramDouble3 + ", " + paramDouble4 + ", " + paramDouble5 + ", " + paramDouble6 + ", " + paramDouble7 + ", " + paramDouble8 + ", " + paramRegion + ")");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 471 */       return this.target.getAATileGenerator(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramRegion, paramArrayOfInt);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\RenderingEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.awt.image;
/*    */ 
/*    */ import sun.java2d.SurfaceData;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BufImgVolatileSurfaceManager
/*    */   extends VolatileSurfaceManager
/*    */ {
/*    */   public BufImgVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
/*    */   {
/* 47 */     super(paramSunVolatileImage, paramObject);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected boolean isAccelerationEnabled()
/*    */   {
/* 55 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected SurfaceData initAcceleratedSurface()
/*    */   {
/* 66 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\image\BufImgVolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
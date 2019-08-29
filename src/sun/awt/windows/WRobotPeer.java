/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.GraphicsDevice;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.peer.RobotPeer;
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
/*    */ final class WRobotPeer
/*    */   extends WObjectPeer
/*    */   implements RobotPeer
/*    */ {
/*    */   WRobotPeer()
/*    */   {
/* 34 */     create();
/*    */   }
/*    */   
/* 37 */   WRobotPeer(GraphicsDevice paramGraphicsDevice) { create(); }
/*    */   
/*    */ 
/*    */   private synchronized native void _dispose();
/*    */   
/*    */   protected void disposeImpl()
/*    */   {
/* 44 */     _dispose();
/*    */   }
/*    */   
/*    */   public native void create();
/*    */   
/*    */   public native void mouseMoveImpl(int paramInt1, int paramInt2);
/*    */   
/* 51 */   public void mouseMove(int paramInt1, int paramInt2) { mouseMoveImpl(paramInt1, paramInt2); }
/*    */   
/*    */ 
/*    */   public native void mousePress(int paramInt);
/*    */   
/*    */ 
/*    */   public native void mouseRelease(int paramInt);
/*    */   
/*    */ 
/*    */   public native void mouseWheel(int paramInt);
/*    */   
/*    */   public native void keyPress(int paramInt);
/*    */   
/*    */   public native void keyRelease(int paramInt);
/*    */   
/*    */   public int getRGBPixel(int paramInt1, int paramInt2)
/*    */   {
/* 68 */     return getRGBPixels(new Rectangle(paramInt1, paramInt2, 1, 1))[0];
/*    */   }
/*    */   
/*    */   public int[] getRGBPixels(Rectangle paramRectangle)
/*    */   {
/* 73 */     int[] arrayOfInt = new int[paramRectangle.width * paramRectangle.height];
/* 74 */     getRGBPixels(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, arrayOfInt);
/* 75 */     return arrayOfInt;
/*    */   }
/*    */   
/*    */   private native void getRGBPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt);
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WRobotPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
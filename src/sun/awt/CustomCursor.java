/*    */ package sun.awt;
/*    */ 
/*    */ import java.awt.Canvas;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Image;
/*    */ import java.awt.MediaTracker;
/*    */ import java.awt.Point;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.image.ImageProducer;
/*    */ import java.awt.image.PixelGrabber;
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
/*    */ public abstract class CustomCursor
/*    */   extends Cursor
/*    */ {
/*    */   protected Image image;
/*    */   
/*    */   public CustomCursor(Image paramImage, Point paramPoint, String paramString)
/*    */     throws IndexOutOfBoundsException
/*    */   {
/* 42 */     super(paramString);
/* 43 */     this.image = paramImage;
/* 44 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*    */     
/*    */ 
/* 47 */     Canvas localCanvas = new Canvas();
/* 48 */     MediaTracker localMediaTracker = new MediaTracker(localCanvas);
/* 49 */     localMediaTracker.addImage(paramImage, 0);
/*    */     try {
/* 51 */       localMediaTracker.waitForAll();
/*    */     }
/*    */     catch (InterruptedException localInterruptedException1) {}
/* 54 */     int i = paramImage.getWidth(localCanvas);
/* 55 */     int j = paramImage.getHeight(localCanvas);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 62 */     if ((localMediaTracker.isErrorAny()) || (i < 0) || (j < 0)) {
/* 63 */       paramPoint.x = (paramPoint.y = 0);
/*    */     }
/*    */     
/*    */ 
/* 67 */     Dimension localDimension = localToolkit.getBestCursorSize(i, j);
/* 68 */     if (((localDimension.width != i) || (localDimension.height != j)) && (localDimension.width != 0) && (localDimension.height != 0))
/*    */     {
/* 70 */       paramImage = paramImage.getScaledInstance(localDimension.width, localDimension.height, 1);
/*    */       
/*    */ 
/* 73 */       i = localDimension.width;
/* 74 */       j = localDimension.height;
/*    */     }
/*    */     
/*    */ 
/* 78 */     if ((paramPoint.x >= i) || (paramPoint.y >= j) || (paramPoint.x < 0) || (paramPoint.y < 0)) {
/* 79 */       throw new IndexOutOfBoundsException("invalid hotSpot");
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 89 */     int[] arrayOfInt = new int[i * j];
/* 90 */     ImageProducer localImageProducer = paramImage.getSource();
/* 91 */     PixelGrabber localPixelGrabber = new PixelGrabber(localImageProducer, 0, 0, i, j, arrayOfInt, 0, i);
/*    */     try
/*    */     {
/* 94 */       localPixelGrabber.grabPixels();
/*    */     }
/*    */     catch (InterruptedException localInterruptedException2) {}
/*    */     
/* 98 */     createNativeCursor(this.image, arrayOfInt, i, j, paramPoint.x, paramPoint.y);
/*    */   }
/*    */   
/*    */   protected abstract void createNativeCursor(Image paramImage, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\CustomCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
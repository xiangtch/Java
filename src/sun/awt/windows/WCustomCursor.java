/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Image;
/*    */ import java.awt.Point;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.DataBuffer;
/*    */ import java.awt.image.DataBufferInt;
/*    */ import java.awt.image.Raster;
/*    */ import java.awt.image.WritableRaster;
/*    */ import sun.awt.CustomCursor;
/*    */ import sun.awt.image.ImageRepresentation;
/*    */ import sun.awt.image.IntegerComponentRaster;
/*    */ import sun.awt.image.ToolkitImage;
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
/*    */ final class WCustomCursor
/*    */   extends CustomCursor
/*    */ {
/*    */   WCustomCursor(Image paramImage, Point paramPoint, String paramString)
/*    */     throws IndexOutOfBoundsException
/*    */   {
/* 45 */     super(paramImage, paramPoint, paramString);
/*    */   }
/*    */   
/*    */ 
/*    */   protected void createNativeCursor(Image paramImage, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*    */   {
/* 51 */     BufferedImage localBufferedImage = new BufferedImage(paramInt1, paramInt2, 1);
/*    */     
/* 53 */     Graphics localGraphics = localBufferedImage.getGraphics();
/*    */     try {
/* 55 */       if ((paramImage instanceof ToolkitImage)) {
/* 56 */         localObject1 = ((ToolkitImage)paramImage).getImageRep();
/* 57 */         ((ImageRepresentation)localObject1).reconstruct(32);
/*    */       }
/* 59 */       localGraphics.drawImage(paramImage, 0, 0, paramInt1, paramInt2, null);
/*    */     } finally {
/* 61 */       localGraphics.dispose();
/*    */     }
/* 63 */     Object localObject1 = localBufferedImage.getRaster();
/* 64 */     DataBuffer localDataBuffer = ((Raster)localObject1).getDataBuffer();
/*    */     
/* 66 */     int[] arrayOfInt = ((DataBufferInt)localDataBuffer).getData();
/*    */     
/* 68 */     byte[] arrayOfByte = new byte[paramInt1 * paramInt2 / 8];
/* 69 */     int i = paramArrayOfInt.length;
/* 70 */     for (int j = 0; j < i; j++) {
/* 71 */       int k = j / 8;
/* 72 */       int m = 1 << 7 - j % 8;
/* 73 */       if ((paramArrayOfInt[j] & 0xFF000000) == 0)
/*    */       {
/* 75 */         int tmp156_154 = k; byte[] tmp156_152 = arrayOfByte;tmp156_152[tmp156_154] = ((byte)(tmp156_152[tmp156_154] | m));
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 80 */     j = ((Raster)localObject1).getWidth();
/* 81 */     if ((localObject1 instanceof IntegerComponentRaster)) {
/* 82 */       j = ((IntegerComponentRaster)localObject1).getScanlineStride();
/*    */     }
/* 84 */     createCursorIndirect(
/* 85 */       ((DataBufferInt)localBufferedImage.getRaster().getDataBuffer()).getData(), arrayOfByte, j, ((Raster)localObject1)
/* 86 */       .getWidth(), ((Raster)localObject1).getHeight(), paramInt3, paramInt4);
/*    */   }
/*    */   
/*    */   private native void createCursorIndirect(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
/*    */   
/*    */   static native int getCursorWidth();
/*    */   
/*    */   static native int getCursorHeight();
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WCustomCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
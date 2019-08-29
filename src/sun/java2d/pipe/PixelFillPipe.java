package sun.java2d.pipe;

import sun.java2d.SunGraphics2D;

public abstract interface PixelFillPipe
{
  public abstract void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt);
}


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\PixelFillPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
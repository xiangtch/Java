package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public abstract interface CompositePipe
{
  public abstract Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt);
  
  public abstract boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void skipTile(Object paramObject, int paramInt1, int paramInt2);
  
  public abstract void endSequence(Object paramObject);
}


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\CompositePipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
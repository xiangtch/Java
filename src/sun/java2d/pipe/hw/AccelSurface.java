package sun.java2d.pipe.hw;

import java.awt.Rectangle;
import sun.java2d.Surface;

public abstract interface AccelSurface
  extends BufferedContextProvider, Surface
{
  public static final int UNDEFINED = 0;
  public static final int WINDOW = 1;
  public static final int RT_PLAIN = 2;
  public static final int TEXTURE = 3;
  public static final int FLIP_BACKBUFFER = 4;
  public static final int RT_TEXTURE = 5;
  
  public abstract int getType();
  
  public abstract long getNativeOps();
  
  public abstract long getNativeResource(int paramInt);
  
  public abstract void markDirty();
  
  public abstract boolean isValid();
  
  public abstract boolean isSurfaceLost();
  
  public abstract Rectangle getBounds();
  
  public abstract Rectangle getNativeBounds();
}


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\hw\AccelSurface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
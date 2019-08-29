package sun.java2d.cmm;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public abstract interface ColorTransform
{
  public static final int Any = -1;
  public static final int In = 1;
  public static final int Out = 2;
  public static final int Gamut = 3;
  public static final int Simulation = 4;
  
  public abstract int getNumInComponents();
  
  public abstract int getNumOutComponents();
  
  public abstract void colorConvert(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2);
  
  public abstract void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4);
  
  public abstract void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster);
  
  public abstract short[] colorConvert(short[] paramArrayOfShort1, short[] paramArrayOfShort2);
  
  public abstract byte[] colorConvert(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
}


/* Location:              E:\java_source\rt.jar!\sun\java2d\cmm\ColorTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
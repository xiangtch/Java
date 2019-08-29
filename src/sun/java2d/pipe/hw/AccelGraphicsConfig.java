package sun.java2d.pipe.hw;

import java.awt.image.VolatileImage;

public abstract interface AccelGraphicsConfig
  extends BufferedContextProvider
{
  public abstract VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract ContextCapabilities getContextCapabilities();
  
  public abstract void addDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener);
  
  public abstract void removeDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener);
}


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\hw\AccelGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
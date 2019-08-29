package sun.awt;

import java.util.EventListener;

public abstract interface DisplayChangedListener
  extends EventListener
{
  public abstract void displayChanged();
  
  public abstract void paletteChanged();
}


/* Location:              E:\java_source\rt.jar!\sun\awt\DisplayChangedListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
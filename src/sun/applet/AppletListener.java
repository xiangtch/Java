package sun.applet;

import java.util.EventListener;

public abstract interface AppletListener
  extends EventListener
{
  public abstract void appletStateChanged(AppletEvent paramAppletEvent);
}


/* Location:              E:\java_source\rt.jar!\sun\applet\AppletListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
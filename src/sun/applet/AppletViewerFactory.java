package sun.applet;

import java.awt.MenuBar;
import java.net.URL;
import java.util.Hashtable;

public abstract interface AppletViewerFactory
{
  public abstract AppletViewer createAppletViewer(int paramInt1, int paramInt2, URL paramURL, Hashtable paramHashtable);
  
  public abstract MenuBar getBaseMenuBar();
  
  public abstract boolean isStandalone();
}


/* Location:              E:\java_source\rt.jar!\sun\applet\AppletViewerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
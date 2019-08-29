package sun.awt;

import java.awt.event.WindowEvent;

public abstract interface WindowClosingListener
{
  public abstract RuntimeException windowClosingNotify(WindowEvent paramWindowEvent);
  
  public abstract RuntimeException windowClosingDelivered(WindowEvent paramWindowEvent);
}


/* Location:              E:\java_source\rt.jar!\sun\awt\WindowClosingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
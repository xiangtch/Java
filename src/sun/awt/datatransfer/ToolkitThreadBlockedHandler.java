package sun.awt.datatransfer;

public abstract interface ToolkitThreadBlockedHandler
{
  public abstract void lock();
  
  public abstract void unlock();
  
  public abstract void enter();
  
  public abstract void exit();
}


/* Location:              E:\java_source\rt.jar!\sun\awt\datatransfer\ToolkitThreadBlockedHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
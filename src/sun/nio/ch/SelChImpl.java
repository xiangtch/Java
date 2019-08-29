package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.Channel;

public abstract interface SelChImpl
  extends Channel
{
  public abstract FileDescriptor getFD();
  
  public abstract int getFDVal();
  
  public abstract boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl);
  
  public abstract boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl);
  
  public abstract void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl);
  
  public abstract int validOps();
  
  public abstract void kill()
    throws IOException;
}


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\SelChImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.rmi.server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.server.RemoteCall;

public abstract interface Dispatcher
{
  public abstract void dispatch(Remote paramRemote, RemoteCall paramRemoteCall)
    throws IOException;
}


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\Dispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
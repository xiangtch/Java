package sun.rmi.transport;

import java.rmi.RemoteException;

public abstract interface Channel
{
  public abstract Connection newConnection()
    throws RemoteException;
  
  public abstract Endpoint getEndpoint();
  
  public abstract void free(Connection paramConnection, boolean paramBoolean)
    throws RemoteException;
}


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\Channel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
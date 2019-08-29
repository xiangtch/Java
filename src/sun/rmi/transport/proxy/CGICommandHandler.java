package sun.rmi.transport.proxy;

abstract interface CGICommandHandler
{
  public abstract String getName();
  
  public abstract void execute(String paramString)
    throws CGIClientException, CGIServerException;
}


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\proxy\CGICommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
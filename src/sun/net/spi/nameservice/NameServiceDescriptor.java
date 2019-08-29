package sun.net.spi.nameservice;

public abstract interface NameServiceDescriptor
{
  public abstract NameService createNameService()
    throws Exception;
  
  public abstract String getProviderName();
  
  public abstract String getType();
}


/* Location:              E:\java_source\rt.jar!\sun\net\spi\nameservice\NameServiceDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
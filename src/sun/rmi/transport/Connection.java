package sun.rmi.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface Connection
{
  public abstract InputStream getInputStream()
    throws IOException;
  
  public abstract void releaseInputStream()
    throws IOException;
  
  public abstract OutputStream getOutputStream()
    throws IOException;
  
  public abstract void releaseOutputStream()
    throws IOException;
  
  public abstract boolean isReusable();
  
  public abstract void close()
    throws IOException;
  
  public abstract Channel getChannel();
}


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
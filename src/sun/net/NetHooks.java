package sun.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;

public final class NetHooks
{
  public static void beforeTcpBind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
    throws IOException
  {}
  
  public static void beforeTcpConnect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
    throws IOException
  {}
}


/* Location:              E:\java_source\rt.jar!\sun\net\NetHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
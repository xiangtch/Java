package sun.misc;

import java.net.InetAddress;
import java.net.URLClassLoader;

public abstract interface JavaNetAccess
{
  public abstract URLClassPath getURLClassPath(URLClassLoader paramURLClassLoader);
  
  public abstract String getOriginalHostName(InetAddress paramInetAddress);
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaNetAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
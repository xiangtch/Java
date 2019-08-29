package sun.rmi.server;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;

public abstract interface DeserializationChecker
{
  public abstract void check(Method paramMethod, ObjectStreamClass paramObjectStreamClass, int paramInt1, int paramInt2);
  
  public abstract void checkProxyClass(Method paramMethod, String[] paramArrayOfString, int paramInt1, int paramInt2);
  
  public void end(int paramInt) {}
}


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\DeserializationChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
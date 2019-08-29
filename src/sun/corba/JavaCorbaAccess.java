package sun.corba;

import com.sun.corba.se.impl.io.ValueHandlerImpl;

public abstract interface JavaCorbaAccess
{
  public abstract ValueHandlerImpl newValueHandlerImpl();
  
  public abstract Class<?> loadClass(String paramString)
    throws ClassNotFoundException;
}


/* Location:              E:\java_source\rt.jar!\sun\corba\JavaCorbaAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
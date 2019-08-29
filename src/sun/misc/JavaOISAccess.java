package sun.misc;

import java.io.InvalidClassException;
import java.io.ObjectInputStream;

public abstract interface JavaOISAccess
{
  public abstract void setObjectInputFilter(ObjectInputStream paramObjectInputStream, ObjectInputFilter paramObjectInputFilter);
  
  public abstract ObjectInputFilter getObjectInputFilter(ObjectInputStream paramObjectInputStream);
  
  public abstract void checkArray(ObjectInputStream paramObjectInputStream, Class<?> paramClass, int paramInt)
    throws InvalidClassException;
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaOISAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
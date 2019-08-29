package sun.reflect;

import java.lang.reflect.InvocationTargetException;

public abstract interface ConstructorAccessor
{
  public abstract Object newInstance(Object[] paramArrayOfObject)
    throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}


/* Location:              E:\java_source\rt.jar!\sun\reflect\ConstructorAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.reflect;

import java.lang.reflect.InvocationTargetException;

abstract class MethodAccessorImpl
  extends MagicAccessorImpl
  implements MethodAccessor
{
  public abstract Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws IllegalArgumentException, InvocationTargetException;
}


/* Location:              E:\java_source\rt.jar!\sun\reflect\MethodAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
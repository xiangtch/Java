package sun.invoke;

import java.lang.invoke.MethodHandle;

public abstract interface WrapperInstance
{
  public abstract MethodHandle getWrapperInstanceTarget();
  
  public abstract Class<?> getWrapperInstanceType();
}


/* Location:              E:\java_source\rt.jar!\sun\invoke\WrapperInstance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
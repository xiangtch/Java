package sun.reflect.annotation;

import java.io.Serializable;

public abstract class ExceptionProxy
  implements Serializable
{
  protected abstract RuntimeException generateException();
}


/* Location:              E:\java_source\rt.jar!\sun\reflect\annotation\ExceptionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
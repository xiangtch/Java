package sun.dc.path;

public abstract interface FastPathProducer
{
  public abstract void getBox(float[] paramArrayOfFloat)
    throws PathError;
  
  public abstract void sendTo(PathConsumer paramPathConsumer)
    throws PathError, PathException;
}


/* Location:              E:\java_source\rt.jar!\sun\dc\path\FastPathProducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
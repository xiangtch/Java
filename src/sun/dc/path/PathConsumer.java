package sun.dc.path;

public abstract interface PathConsumer
{
  public abstract void beginPath()
    throws PathError;
  
  public abstract void beginSubpath(float paramFloat1, float paramFloat2)
    throws PathError;
  
  public abstract void appendLine(float paramFloat1, float paramFloat2)
    throws PathError;
  
  public abstract void appendQuadratic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    throws PathError;
  
  public abstract void appendCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    throws PathError;
  
  public abstract void closedSubpath()
    throws PathError;
  
  public abstract void endPath()
    throws PathError, PathException;
  
  public abstract void useProxy(FastPathProducer paramFastPathProducer)
    throws PathError, PathException;
  
  public abstract long getCPathConsumer();
  
  public abstract void dispose();
  
  public abstract PathConsumer getConsumer();
}


/* Location:              E:\java_source\rt.jar!\sun\dc\path\PathConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
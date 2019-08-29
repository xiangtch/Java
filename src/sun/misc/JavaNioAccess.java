package sun.misc;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract interface JavaNioAccess
{
  public abstract BufferPool getDirectBufferPool();
  
  public abstract ByteBuffer newDirectByteBuffer(long paramLong, int paramInt, Object paramObject);
  
  public abstract void truncate(Buffer paramBuffer);
  
  public static abstract interface BufferPool
  {
    public abstract String getName();
    
    public abstract long getCount();
    
    public abstract long getTotalCapacity();
    
    public abstract long getMemoryUsed();
  }
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaNioAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
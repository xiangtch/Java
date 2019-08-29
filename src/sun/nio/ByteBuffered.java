package sun.nio;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface ByteBuffered
{
  public abstract ByteBuffer getByteBuffer()
    throws IOException;
}


/* Location:              E:\java_source\rt.jar!\sun\nio\ByteBuffered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
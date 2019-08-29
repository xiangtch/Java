package sun.nio.ch;

import sun.misc.Cleaner;

public abstract interface DirectBuffer
{
  public abstract long address();
  
  public abstract Object attachment();
  
  public abstract Cleaner cleaner();
}


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\DirectBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.java2d.pipe;

public abstract interface SpanIterator
{
  public abstract void getPathBox(int[] paramArrayOfInt);
  
  public abstract void intersectClipBox(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract boolean nextSpan(int[] paramArrayOfInt);
  
  public abstract void skipDownTo(int paramInt);
  
  public abstract long getNativeIterator();
}


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\SpanIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
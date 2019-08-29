package sun.management.counter;

public abstract interface LongArrayCounter
  extends Counter
{
  public abstract long[] longArrayValue();
  
  public abstract long longAt(int paramInt);
}


/* Location:              E:\java_source\rt.jar!\sun\management\counter\LongArrayCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
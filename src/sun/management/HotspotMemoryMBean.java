package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public abstract interface HotspotMemoryMBean
{
  public abstract List<Counter> getInternalMemoryCounters();
}


/* Location:              E:\java_source\rt.jar!\sun\management\HotspotMemoryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
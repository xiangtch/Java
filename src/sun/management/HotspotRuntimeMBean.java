package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public abstract interface HotspotRuntimeMBean
{
  public abstract long getSafepointCount();
  
  public abstract long getTotalSafepointTime();
  
  public abstract long getSafepointSyncTime();
  
  public abstract List<Counter> getInternalRuntimeCounters();
}


/* Location:              E:\java_source\rt.jar!\sun\management\HotspotRuntimeMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
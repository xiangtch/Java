package sun.management;

import java.util.List;
import java.util.Map;
import sun.management.counter.Counter;

public abstract interface HotspotThreadMBean
{
  public abstract int getInternalThreadCount();
  
  public abstract Map<String, Long> getInternalThreadCpuTimes();
  
  public abstract List<Counter> getInternalThreadingCounters();
}


/* Location:              E:\java_source\rt.jar!\sun\management\HotspotThreadMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
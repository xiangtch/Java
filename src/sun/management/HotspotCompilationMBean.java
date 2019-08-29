package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public abstract interface HotspotCompilationMBean
{
  public abstract int getCompilerThreadCount();
  
  public abstract List<CompilerThreadStat> getCompilerThreadStats();
  
  public abstract long getTotalCompileCount();
  
  public abstract long getBailoutCompileCount();
  
  public abstract long getInvalidatedCompileCount();
  
  public abstract MethodInfo getLastCompile();
  
  public abstract MethodInfo getFailedCompile();
  
  public abstract MethodInfo getInvalidatedCompile();
  
  public abstract long getCompiledMethodCodeSize();
  
  public abstract long getCompiledMethodSize();
  
  public abstract List<Counter> getInternalCompilerCounters();
}


/* Location:              E:\java_source\rt.jar!\sun\management\HotspotCompilationMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmThreadingMBean
{
  public abstract EnumJvmThreadCpuTimeMonitoring getJvmThreadCpuTimeMonitoring()
    throws SnmpStatusException;
  
  public abstract void setJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring paramEnumJvmThreadCpuTimeMonitoring)
    throws SnmpStatusException;
  
  public abstract void checkJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring paramEnumJvmThreadCpuTimeMonitoring)
    throws SnmpStatusException;
  
  public abstract EnumJvmThreadContentionMonitoring getJvmThreadContentionMonitoring()
    throws SnmpStatusException;
  
  public abstract void setJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring paramEnumJvmThreadContentionMonitoring)
    throws SnmpStatusException;
  
  public abstract void checkJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring paramEnumJvmThreadContentionMonitoring)
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadTotalStartedCount()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadPeakCount()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadDaemonCount()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadCount()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadPeakCountReset()
    throws SnmpStatusException;
  
  public abstract void setJvmThreadPeakCountReset(Long paramLong)
    throws SnmpStatusException;
  
  public abstract void checkJvmThreadPeakCountReset(Long paramLong)
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmThreadingMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
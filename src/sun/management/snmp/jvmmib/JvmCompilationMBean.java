package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmCompilationMBean
{
  public abstract EnumJvmJITCompilerTimeMonitoring getJvmJITCompilerTimeMonitoring()
    throws SnmpStatusException;
  
  public abstract Long getJvmJITCompilerTimeMs()
    throws SnmpStatusException;
  
  public abstract String getJvmJITCompilerName()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmCompilationMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
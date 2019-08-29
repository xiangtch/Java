package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmOSMBean
{
  public abstract Integer getJvmOSProcessorCount()
    throws SnmpStatusException;
  
  public abstract String getJvmOSVersion()
    throws SnmpStatusException;
  
  public abstract String getJvmOSArch()
    throws SnmpStatusException;
  
  public abstract String getJvmOSName()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmOSMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTClassPathEntryMBean
{
  public abstract String getJvmRTClassPathItem()
    throws SnmpStatusException;
  
  public abstract Integer getJvmRTClassPathIndex()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmRTClassPathEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTBootClassPathEntryMBean
{
  public abstract String getJvmRTBootClassPathItem()
    throws SnmpStatusException;
  
  public abstract Integer getJvmRTBootClassPathIndex()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmRTBootClassPathEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
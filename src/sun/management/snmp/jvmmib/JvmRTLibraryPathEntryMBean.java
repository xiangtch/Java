package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTLibraryPathEntryMBean
{
  public abstract String getJvmRTLibraryPathItem()
    throws SnmpStatusException;
  
  public abstract Integer getJvmRTLibraryPathIndex()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmRTLibraryPathEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
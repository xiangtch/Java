package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmMemGCEntryMBean
{
  public abstract Long getJvmMemGCTimeMs()
    throws SnmpStatusException;
  
  public abstract Long getJvmMemGCCount()
    throws SnmpStatusException;
  
  public abstract Integer getJvmMemManagerIndex()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmMemGCEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
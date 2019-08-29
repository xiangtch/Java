package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmMemManagerEntryMBean
{
  public abstract EnumJvmMemManagerState getJvmMemManagerState()
    throws SnmpStatusException;
  
  public abstract String getJvmMemManagerName()
    throws SnmpStatusException;
  
  public abstract Integer getJvmMemManagerIndex()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmMemManagerEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
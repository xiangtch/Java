package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTInputArgsEntryMBean
{
  public abstract String getJvmRTInputArgsItem()
    throws SnmpStatusException;
  
  public abstract Integer getJvmRTInputArgsIndex()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmRTInputArgsEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
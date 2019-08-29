package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmMemMgrPoolRelEntryMBean
{
  public abstract String getJvmMemMgrRelPoolName()
    throws SnmpStatusException;
  
  public abstract String getJvmMemMgrRelManagerName()
    throws SnmpStatusException;
  
  public abstract Integer getJvmMemManagerIndex()
    throws SnmpStatusException;
  
  public abstract Integer getJvmMemPoolIndex()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmMemMgrPoolRelEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
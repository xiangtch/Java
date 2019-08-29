package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmClassLoadingMBean
{
  public abstract EnumJvmClassesVerboseLevel getJvmClassesVerboseLevel()
    throws SnmpStatusException;
  
  public abstract void setJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel paramEnumJvmClassesVerboseLevel)
    throws SnmpStatusException;
  
  public abstract void checkJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel paramEnumJvmClassesVerboseLevel)
    throws SnmpStatusException;
  
  public abstract Long getJvmClassesUnloadedCount()
    throws SnmpStatusException;
  
  public abstract Long getJvmClassesTotalLoadedCount()
    throws SnmpStatusException;
  
  public abstract Long getJvmClassesLoadedCount()
    throws SnmpStatusException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvmmib\JvmClassLoadingMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
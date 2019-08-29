package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;

public abstract interface SnmpTableHandler
{
  public abstract Object getData(SnmpOid paramSnmpOid);
  
  public abstract SnmpOid getNext(SnmpOid paramSnmpOid);
  
  public abstract boolean contains(SnmpOid paramSnmpOid);
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\util\SnmpTableHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
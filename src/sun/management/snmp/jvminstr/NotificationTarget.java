package sun.management.snmp.jvminstr;

import java.net.InetAddress;

public abstract interface NotificationTarget
{
  public abstract InetAddress getAddress();
  
  public abstract int getPort();
  
  public abstract String getCommunity();
}


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\jvminstr\NotificationTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
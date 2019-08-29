package sun.security.util;

import java.security.Permission;

public abstract interface PermissionFactory<T extends Permission>
{
  public abstract T newPermission(String paramString);
}


/* Location:              E:\java_source\rt.jar!\sun\security\util\PermissionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
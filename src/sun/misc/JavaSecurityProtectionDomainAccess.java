package sun.misc;

import java.security.PermissionCollection;
import java.security.ProtectionDomain;

public abstract interface JavaSecurityProtectionDomainAccess
{
  public abstract ProtectionDomainCache getProtectionDomainCache();
  
  public abstract boolean getStaticPermissionsField(ProtectionDomain paramProtectionDomain);
  
  public static abstract interface ProtectionDomainCache
  {
    public abstract void put(ProtectionDomain paramProtectionDomain, PermissionCollection paramPermissionCollection);
    
    public abstract PermissionCollection get(ProtectionDomain paramProtectionDomain);
  }
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaSecurityProtectionDomainAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
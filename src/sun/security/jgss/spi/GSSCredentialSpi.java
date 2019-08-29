package sun.security.jgss.spi;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public abstract interface GSSCredentialSpi
{
  public abstract Provider getProvider();
  
  public abstract void dispose()
    throws GSSException;
  
  public abstract GSSNameSpi getName()
    throws GSSException;
  
  public abstract int getInitLifetime()
    throws GSSException;
  
  public abstract int getAcceptLifetime()
    throws GSSException;
  
  public abstract boolean isInitiatorCredential()
    throws GSSException;
  
  public abstract boolean isAcceptorCredential()
    throws GSSException;
  
  public abstract Oid getMechanism();
  
  public abstract GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi)
    throws GSSException;
}


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\spi\GSSCredentialSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
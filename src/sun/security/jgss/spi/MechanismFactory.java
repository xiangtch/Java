package sun.security.jgss.spi;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public abstract interface MechanismFactory
{
  public abstract Oid getMechanismOid();
  
  public abstract Provider getProvider();
  
  public abstract Oid[] getNameTypes()
    throws GSSException;
  
  public abstract GSSCredentialSpi getCredentialElement(GSSNameSpi paramGSSNameSpi, int paramInt1, int paramInt2, int paramInt3)
    throws GSSException;
  
  public abstract GSSNameSpi getNameElement(String paramString, Oid paramOid)
    throws GSSException;
  
  public abstract GSSNameSpi getNameElement(byte[] paramArrayOfByte, Oid paramOid)
    throws GSSException;
  
  public abstract GSSContextSpi getMechanismContext(GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt)
    throws GSSException;
  
  public abstract GSSContextSpi getMechanismContext(GSSCredentialSpi paramGSSCredentialSpi)
    throws GSSException;
  
  public abstract GSSContextSpi getMechanismContext(byte[] paramArrayOfByte)
    throws GSSException;
}


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\spi\MechanismFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
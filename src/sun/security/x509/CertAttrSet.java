package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public abstract interface CertAttrSet<T>
{
  public abstract String toString();
  
  public abstract void encode(OutputStream paramOutputStream)
    throws CertificateException, IOException;
  
  public abstract void set(String paramString, Object paramObject)
    throws CertificateException, IOException;
  
  public abstract Object get(String paramString)
    throws CertificateException, IOException;
  
  public abstract void delete(String paramString)
    throws CertificateException, IOException;
  
  public abstract Enumeration<T> getElements();
  
  public abstract String getName();
}


/* Location:              E:\java_source\rt.jar!\sun\security\x509\CertAttrSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
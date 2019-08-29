package sun.net.www.protocol.http;

import java.net.URL;

@Deprecated
public abstract interface HttpAuthenticator
{
  public abstract boolean schemeSupported(String paramString);
  
  public abstract String authString(URL paramURL, String paramString1, String paramString2);
}


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\HttpAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
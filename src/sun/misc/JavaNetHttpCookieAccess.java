package sun.misc;

import java.net.HttpCookie;
import java.util.List;

public abstract interface JavaNetHttpCookieAccess
{
  public abstract List<HttpCookie> parse(String paramString);
  
  public abstract String header(HttpCookie paramHttpCookie);
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaNetHttpCookieAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
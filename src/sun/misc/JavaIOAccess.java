package sun.misc;

import java.io.Console;
import java.nio.charset.Charset;

public abstract interface JavaIOAccess
{
  public abstract Console console();
  
  public abstract Charset charset();
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaIOAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
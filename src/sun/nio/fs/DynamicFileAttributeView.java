package sun.nio.fs;

import java.io.IOException;
import java.util.Map;

abstract interface DynamicFileAttributeView
{
  public abstract void setAttribute(String paramString, Object paramObject)
    throws IOException;
  
  public abstract Map<String, Object> readAttributes(String[] paramArrayOfString)
    throws IOException;
}


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\DynamicFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
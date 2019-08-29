package sun.nio.fs;

import java.nio.file.attribute.BasicFileAttributes;

public abstract interface BasicFileAttributesHolder
{
  public abstract BasicFileAttributes get();
  
  public abstract void invalidate();
}


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\BasicFileAttributesHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
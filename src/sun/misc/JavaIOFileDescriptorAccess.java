package sun.misc;

import java.io.FileDescriptor;

public abstract interface JavaIOFileDescriptorAccess
{
  public abstract void set(FileDescriptor paramFileDescriptor, int paramInt);
  
  public abstract int get(FileDescriptor paramFileDescriptor);
  
  public abstract void setHandle(FileDescriptor paramFileDescriptor, long paramLong);
  
  public abstract long getHandle(FileDescriptor paramFileDescriptor);
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaIOFileDescriptorAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
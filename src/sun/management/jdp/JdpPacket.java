package sun.management.jdp;

import java.io.IOException;

public abstract interface JdpPacket
{
  public abstract byte[] getPacketData()
    throws IOException;
}


/* Location:              E:\java_source\rt.jar!\sun\management\jdp\JdpPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.security.timestamp;

import java.io.IOException;

public abstract interface Timestamper
{
  public abstract TSResponse generateTimestamp(TSRequest paramTSRequest)
    throws IOException;
}


/* Location:              E:\java_source\rt.jar!\sun\security\timestamp\Timestamper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
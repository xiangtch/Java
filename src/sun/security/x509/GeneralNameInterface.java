package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;

public abstract interface GeneralNameInterface
{
  public static final int NAME_ANY = 0;
  public static final int NAME_RFC822 = 1;
  public static final int NAME_DNS = 2;
  public static final int NAME_X400 = 3;
  public static final int NAME_DIRECTORY = 4;
  public static final int NAME_EDI = 5;
  public static final int NAME_URI = 6;
  public static final int NAME_IP = 7;
  public static final int NAME_OID = 8;
  public static final int NAME_DIFF_TYPE = -1;
  public static final int NAME_MATCH = 0;
  public static final int NAME_NARROWS = 1;
  public static final int NAME_WIDENS = 2;
  public static final int NAME_SAME_TYPE = 3;
  
  public abstract int getType();
  
  public abstract void encode(DerOutputStream paramDerOutputStream)
    throws IOException;
  
  public abstract int constrains(GeneralNameInterface paramGeneralNameInterface)
    throws UnsupportedOperationException;
  
  public abstract int subtreeDepth()
    throws UnsupportedOperationException;
}


/* Location:              E:\java_source\rt.jar!\sun\security\x509\GeneralNameInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
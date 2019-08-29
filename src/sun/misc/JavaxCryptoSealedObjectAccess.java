package sun.misc;

import java.io.IOException;
import java.io.ObjectInputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

public abstract interface JavaxCryptoSealedObjectAccess
{
  public abstract ObjectInputStream getExtObjectInputStream(SealedObject paramSealedObject, Cipher paramCipher)
    throws BadPaddingException, IllegalBlockSizeException, IOException;
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaxCryptoSealedObjectAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.misc;

public abstract interface ExtensionInstallationProvider
{
  public abstract boolean installExtension(ExtensionInfo paramExtensionInfo1, ExtensionInfo paramExtensionInfo2)
    throws ExtensionInstallationException;
}


/* Location:              E:\java_source\rt.jar!\sun\misc\ExtensionInstallationProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
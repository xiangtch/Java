package sun.misc;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract interface JavaUtilJarAccess
{
  public abstract boolean jarFileHasClassPathAttribute(JarFile paramJarFile)
    throws IOException;
  
  public abstract CodeSource[] getCodeSources(JarFile paramJarFile, URL paramURL);
  
  public abstract CodeSource getCodeSource(JarFile paramJarFile, URL paramURL, String paramString);
  
  public abstract Enumeration<String> entryNames(JarFile paramJarFile, CodeSource[] paramArrayOfCodeSource);
  
  public abstract Enumeration<JarEntry> entries2(JarFile paramJarFile);
  
  public abstract void setEagerValidation(JarFile paramJarFile, boolean paramBoolean);
  
  public abstract List<Object> getManifestDigests(JarFile paramJarFile);
}


/* Location:              E:\java_source\rt.jar!\sun\misc\JavaUtilJarAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
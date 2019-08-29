package sun.font;

import java.awt.Font;
import java.util.Locale;
import java.util.TreeMap;

public abstract interface FontManagerForSGE
  extends FontManager
{
  public abstract Font[] getCreatedFonts();
  
  public abstract TreeMap<String, String> getCreatedFontFamilyNames();
  
  public abstract Font[] getAllInstalledFonts();
  
  public abstract String[] getInstalledFontFamilyNames(Locale paramLocale);
  
  public abstract void useAlternateFontforJALocales();
}


/* Location:              E:\java_source\rt.jar!\sun\font\FontManagerForSGE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
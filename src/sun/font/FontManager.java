package sun.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;

public abstract interface FontManager
{
  public static final int NO_FALLBACK = 0;
  public static final int PHYSICAL_FALLBACK = 1;
  public static final int LOGICAL_FALLBACK = 2;
  
  public abstract boolean registerFont(Font paramFont);
  
  public abstract void deRegisterBadFont(Font2D paramFont2D);
  
  public abstract Font2D findFont2D(String paramString, int paramInt1, int paramInt2);
  
  public abstract Font2D createFont2D(File paramFile, int paramInt, boolean paramBoolean, CreatedFontTracker paramCreatedFontTracker)
    throws FontFormatException;
  
  public abstract boolean usingPerAppContextComposites();
  
  public abstract Font2DHandle getNewComposite(String paramString, int paramInt, Font2DHandle paramFont2DHandle);
  
  public abstract void preferLocaleFonts();
  
  public abstract void preferProportionalFonts();
}


/* Location:              E:\java_source\rt.jar!\sun\font\FontManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
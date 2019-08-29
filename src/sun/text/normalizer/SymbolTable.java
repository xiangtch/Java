package sun.text.normalizer;

import java.text.ParsePosition;

@Deprecated
public abstract interface SymbolTable
{
  @Deprecated
  public static final char SYMBOL_REF = '$';
  
  @Deprecated
  public abstract char[] lookup(String paramString);
  
  @Deprecated
  public abstract UnicodeMatcher lookupMatcher(int paramInt);
  
  @Deprecated
  public abstract String parseReference(String paramString, ParsePosition paramParsePosition, int paramInt);
}


/* Location:              E:\java_source\rt.jar!\sun\text\normalizer\SymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
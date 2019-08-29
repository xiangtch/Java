package sun.swing.text;

import java.awt.print.Printable;

public abstract interface CountingPrintable
  extends Printable
{
  public abstract int getNumberOfPages();
}


/* Location:              E:\java_source\rt.jar!\sun\swing\text\CountingPrintable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.swing;

import javax.swing.Icon;
import javax.swing.JMenuItem;

public abstract interface MenuItemCheckIconFactory
{
  public abstract Icon getIcon(JMenuItem paramJMenuItem);
  
  public abstract boolean isCompatible(Object paramObject, String paramString);
}


/* Location:              E:\java_source\rt.jar!\sun\swing\MenuItemCheckIconFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
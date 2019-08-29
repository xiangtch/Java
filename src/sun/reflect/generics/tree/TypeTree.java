package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public abstract interface TypeTree
  extends Tree
{
  public abstract void accept(TypeTreeVisitor<?> paramTypeTreeVisitor);
}


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\tree\TypeTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
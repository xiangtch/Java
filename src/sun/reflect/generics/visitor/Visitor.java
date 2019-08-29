package sun.reflect.generics.visitor;

import sun.reflect.generics.tree.ClassSignature;
import sun.reflect.generics.tree.MethodTypeSignature;

public abstract interface Visitor<T>
  extends TypeTreeVisitor<T>
{
  public abstract void visitClassSignature(ClassSignature paramClassSignature);
  
  public abstract void visitMethodTypeSignature(MethodTypeSignature paramMethodTypeSignature);
}


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\visitor\Visitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
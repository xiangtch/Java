package sun.reflect.generics.factory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import sun.reflect.generics.tree.FieldTypeSignature;

public abstract interface GenericsFactory
{
  public abstract TypeVariable<?> makeTypeVariable(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature);
  
  public abstract ParameterizedType makeParameterizedType(Type paramType1, Type[] paramArrayOfType, Type paramType2);
  
  public abstract TypeVariable<?> findTypeVariable(String paramString);
  
  public abstract WildcardType makeWildcard(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2);
  
  public abstract Type makeNamedType(String paramString);
  
  public abstract Type makeArrayType(Type paramType);
  
  public abstract Type makeByte();
  
  public abstract Type makeBool();
  
  public abstract Type makeShort();
  
  public abstract Type makeChar();
  
  public abstract Type makeInt();
  
  public abstract Type makeLong();
  
  public abstract Type makeFloat();
  
  public abstract Type makeDouble();
  
  public abstract Type makeVoid();
}


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\factory\GenericsFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.reflect.generics.reflectiveObjects;
/*     */ 
/*     */ import java.lang.reflect.MalformedParameterizedTypeException;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParameterizedTypeImpl
/*     */   implements ParameterizedType
/*     */ {
/*     */   private final Type[] actualTypeArguments;
/*     */   private final Class<?> rawType;
/*     */   private final Type ownerType;
/*     */   
/*     */   private ParameterizedTypeImpl(Class<?> paramClass, Type[] paramArrayOfType, Type paramType)
/*     */   {
/*  48 */     this.actualTypeArguments = paramArrayOfType;
/*  49 */     this.rawType = paramClass;
/*  50 */     this.ownerType = (paramType != null ? paramType : paramClass.getDeclaringClass());
/*  51 */     validateConstructorArguments();
/*     */   }
/*     */   
/*     */   private void validateConstructorArguments() {
/*  55 */     TypeVariable[] arrayOfTypeVariable = this.rawType.getTypeParameters();
/*     */     
/*  57 */     if (arrayOfTypeVariable.length != this.actualTypeArguments.length) {
/*  58 */       throw new MalformedParameterizedTypeException();
/*     */     }
/*  60 */     for (int i = 0; i < this.actualTypeArguments.length; i++) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ParameterizedTypeImpl make(Class<?> paramClass, Type[] paramArrayOfType, Type paramType)
/*     */   {
/*  92 */     return new ParameterizedTypeImpl(paramClass, paramArrayOfType, paramType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Type[] getActualTypeArguments()
/*     */   {
/* 115 */     return (Type[])this.actualTypeArguments.clone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Class<?> getRawType()
/*     */   {
/* 126 */     return this.rawType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Type getOwnerType()
/*     */   {
/* 148 */     return this.ownerType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 160 */     if ((paramObject instanceof ParameterizedType))
/*     */     {
/* 162 */       ParameterizedType localParameterizedType = (ParameterizedType)paramObject;
/*     */       
/* 164 */       if (this == localParameterizedType) {
/* 165 */         return true;
/*     */       }
/* 167 */       Type localType1 = localParameterizedType.getOwnerType();
/* 168 */       Type localType2 = localParameterizedType.getRawType();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 189 */       return 
/* 190 */         (Objects.equals(this.ownerType, localType1)) && 
/* 191 */         (Objects.equals(this.rawType, localType2)) && 
/* 192 */         (Arrays.equals(this.actualTypeArguments, localParameterizedType
/* 193 */         .getActualTypeArguments()));
/*     */     }
/* 195 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 200 */     return 
/*     */     
/*     */ 
/* 203 */       Arrays.hashCode(this.actualTypeArguments) ^ Objects.hashCode(this.ownerType) ^ Objects.hashCode(this.rawType);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 207 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     
/* 209 */     if (this.ownerType != null) {
/* 210 */       if ((this.ownerType instanceof Class)) {
/* 211 */         localStringBuilder.append(((Class)this.ownerType).getName());
/*     */       } else {
/* 213 */         localStringBuilder.append(this.ownerType.toString());
/*     */       }
/* 215 */       localStringBuilder.append("$");
/*     */       
/* 217 */       if ((this.ownerType instanceof ParameterizedTypeImpl))
/*     */       {
/*     */ 
/* 220 */         localStringBuilder.append(this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", ""));
/*     */       }
/*     */       else
/* 223 */         localStringBuilder.append(this.rawType.getSimpleName());
/*     */     } else {
/* 225 */       localStringBuilder.append(this.rawType.getName());
/*     */     }
/* 227 */     if ((this.actualTypeArguments != null) && (this.actualTypeArguments.length > 0))
/*     */     {
/* 229 */       localStringBuilder.append("<");
/* 230 */       int i = 1;
/* 231 */       for (Type localType : this.actualTypeArguments) {
/* 232 */         if (i == 0)
/* 233 */           localStringBuilder.append(", ");
/* 234 */         localStringBuilder.append(localType.getTypeName());
/* 235 */         i = 0;
/*     */       }
/* 237 */       localStringBuilder.append(">");
/*     */     }
/*     */     
/* 240 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\reflectiveObjects\ParameterizedTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
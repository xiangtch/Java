/*    */ package sun.reflect.generics.reflectiveObjects;
/*    */ 
/*    */ import java.lang.reflect.GenericArrayType;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GenericArrayTypeImpl
/*    */   implements GenericArrayType
/*    */ {
/*    */   private final Type genericComponentType;
/*    */   
/*    */   private GenericArrayTypeImpl(Type paramType)
/*    */   {
/* 41 */     this.genericComponentType = paramType;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static GenericArrayTypeImpl make(Type paramType)
/*    */   {
/* 51 */     return new GenericArrayTypeImpl(paramType);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Type getGenericComponentType()
/*    */   {
/* 64 */     return this.genericComponentType;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 68 */     Type localType = getGenericComponentType();
/* 69 */     StringBuilder localStringBuilder = new StringBuilder();
/*    */     
/* 71 */     if ((localType instanceof Class)) {
/* 72 */       localStringBuilder.append(((Class)localType).getName());
/*    */     } else
/* 74 */       localStringBuilder.append(localType.toString());
/* 75 */     localStringBuilder.append("[]");
/* 76 */     return localStringBuilder.toString();
/*    */   }
/*    */   
/*    */   public boolean equals(Object paramObject)
/*    */   {
/* 81 */     if ((paramObject instanceof GenericArrayType)) {
/* 82 */       GenericArrayType localGenericArrayType = (GenericArrayType)paramObject;
/*    */       
/* 84 */       return Objects.equals(this.genericComponentType, localGenericArrayType.getGenericComponentType());
/*    */     }
/* 86 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 91 */     return Objects.hashCode(this.genericComponentType);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\reflectiveObjects\GenericArrayTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
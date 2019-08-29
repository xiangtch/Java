/*     */ package sun.reflect.generics.reflectiveObjects;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Arrays;
/*     */ import sun.reflect.generics.factory.GenericsFactory;
/*     */ import sun.reflect.generics.tree.FieldTypeSignature;
/*     */ import sun.reflect.generics.visitor.Reifier;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WildcardTypeImpl
/*     */   extends LazyReflectiveObjectGenerator
/*     */   implements WildcardType
/*     */ {
/*     */   private Type[] upperBounds;
/*     */   private Type[] lowerBounds;
/*     */   private FieldTypeSignature[] upperBoundASTs;
/*     */   private FieldTypeSignature[] lowerBoundASTs;
/*     */   
/*     */   private WildcardTypeImpl(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2, GenericsFactory paramGenericsFactory)
/*     */   {
/*  58 */     super(paramGenericsFactory);
/*  59 */     this.upperBoundASTs = paramArrayOfFieldTypeSignature1;
/*  60 */     this.lowerBoundASTs = paramArrayOfFieldTypeSignature2;
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
/*     */   public static WildcardTypeImpl make(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2, GenericsFactory paramGenericsFactory)
/*     */   {
/*  76 */     return new WildcardTypeImpl(paramArrayOfFieldTypeSignature1, paramArrayOfFieldTypeSignature2, paramGenericsFactory);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private FieldTypeSignature[] getUpperBoundASTs()
/*     */   {
/*  86 */     assert (this.upperBounds == null);
/*  87 */     return this.upperBoundASTs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private FieldTypeSignature[] getLowerBoundASTs()
/*     */   {
/*  94 */     assert (this.lowerBounds == null);
/*  95 */     return this.lowerBoundASTs;
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
/*     */   public Type[] getUpperBounds()
/*     */   {
/* 121 */     if (this.upperBounds == null) {
/* 122 */       FieldTypeSignature[] arrayOfFieldTypeSignature = getUpperBoundASTs();
/*     */       
/*     */ 
/*     */ 
/* 126 */       Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
/*     */       
/* 128 */       for (int i = 0; i < arrayOfFieldTypeSignature.length; i++) {
/* 129 */         Reifier localReifier = getReifier();
/* 130 */         arrayOfFieldTypeSignature[i].accept(localReifier);
/* 131 */         arrayOfType[i] = localReifier.getResult();
/*     */       }
/*     */       
/* 134 */       this.upperBounds = arrayOfType;
/*     */     }
/*     */     
/* 137 */     return (Type[])this.upperBounds.clone();
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
/*     */   public Type[] getLowerBounds()
/*     */   {
/* 164 */     if (this.lowerBounds == null) {
/* 165 */       FieldTypeSignature[] arrayOfFieldTypeSignature = getLowerBoundASTs();
/*     */       
/*     */ 
/* 168 */       Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
/*     */       
/* 170 */       for (int i = 0; i < arrayOfFieldTypeSignature.length; i++) {
/* 171 */         Reifier localReifier = getReifier();
/* 172 */         arrayOfFieldTypeSignature[i].accept(localReifier);
/* 173 */         arrayOfType[i] = localReifier.getResult();
/*     */       }
/*     */       
/* 176 */       this.lowerBounds = arrayOfType;
/*     */     }
/*     */     
/* 179 */     return (Type[])this.lowerBounds.clone();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 183 */     Type[] arrayOfType1 = getLowerBounds();
/* 184 */     Object localObject1 = arrayOfType1;
/* 185 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     
/* 187 */     if (arrayOfType1.length > 0) {
/* 188 */       localStringBuilder.append("? super ");
/*     */     } else {
/* 190 */       Type[] arrayOfType2 = getUpperBounds();
/* 191 */       if ((arrayOfType2.length > 0) && (!arrayOfType2[0].equals(Object.class))) {
/* 192 */         localObject1 = arrayOfType2;
/* 193 */         localStringBuilder.append("? extends ");
/*     */       } else {
/* 195 */         return "?";
/*     */       }
/*     */     }
/* 198 */     assert (localObject1.length > 0);
/*     */     
/* 200 */     int i = 1;
/* 201 */     for (Object localObject3 : localObject1) {
/* 202 */       if (i == 0) {
/* 203 */         localStringBuilder.append(" & ");
/*     */       }
/* 205 */       i = 0;
/* 206 */       localStringBuilder.append(((Type)localObject3).getTypeName());
/*     */     }
/* 208 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 213 */     if ((paramObject instanceof WildcardType)) {
/* 214 */       WildcardType localWildcardType = (WildcardType)paramObject;
/*     */       
/* 216 */       if (Arrays.equals(getLowerBounds(), localWildcardType
/* 217 */         .getLowerBounds())) {}
/* 215 */       return 
/*     */       
/*     */ 
/* 218 */         Arrays.equals(getUpperBounds(), localWildcardType
/* 219 */         .getUpperBounds());
/*     */     }
/* 221 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 226 */     Type[] arrayOfType1 = getLowerBounds();
/* 227 */     Type[] arrayOfType2 = getUpperBounds();
/*     */     
/* 229 */     return Arrays.hashCode(arrayOfType1) ^ Arrays.hashCode(arrayOfType2);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\reflectiveObjects\WildcardTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
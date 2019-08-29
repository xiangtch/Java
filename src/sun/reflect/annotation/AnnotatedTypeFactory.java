/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedArrayType;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.AnnotatedParameterizedType;
/*     */ import java.lang.reflect.AnnotatedType;
/*     */ import java.lang.reflect.AnnotatedTypeVariable;
/*     */ import java.lang.reflect.AnnotatedWildcardType;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public final class AnnotatedTypeFactory
/*     */ {
/*     */   public static AnnotatedType buildAnnotatedType(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
/*     */   {
/*  54 */     if (paramType == null) {
/*  55 */       return EMPTY_ANNOTATED_TYPE;
/*     */     }
/*  57 */     if (isArray(paramType)) {
/*  58 */       return new AnnotatedArrayTypeImpl(paramType, paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  63 */     if ((paramType instanceof Class)) {
/*  64 */       return new AnnotatedTypeBaseImpl(paramType, 
/*  65 */         addNesting(paramType, paramLocationInfo), paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
/*     */     }
/*     */     
/*     */ 
/*  69 */     if ((paramType instanceof TypeVariable)) {
/*  70 */       return new AnnotatedTypeVariableImpl((TypeVariable)paramType, paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  75 */     if ((paramType instanceof ParameterizedType)) {
/*  76 */       return new AnnotatedParameterizedTypeImpl((ParameterizedType)paramType, 
/*  77 */         addNesting(paramType, paramLocationInfo), paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
/*     */     }
/*     */     
/*     */ 
/*  81 */     if ((paramType instanceof WildcardType)) {
/*  82 */       return new AnnotatedWildcardTypeImpl((WildcardType)paramType, paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  88 */     throw new AssertionError("Unknown instance of Type: " + paramType + "\nThis should not happen.");
/*     */   }
/*     */   
/*     */   private static TypeAnnotation.LocationInfo addNesting(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo) {
/*  92 */     if (isArray(paramType))
/*  93 */       return paramLocationInfo;
/*  94 */     Object localObject; if ((paramType instanceof Class)) {
/*  95 */       localObject = (Class)paramType;
/*  96 */       if (((Class)localObject).getEnclosingClass() == null)
/*  97 */         return paramLocationInfo;
/*  98 */       if (Modifier.isStatic(((Class)localObject).getModifiers()))
/*  99 */         return addNesting(((Class)localObject).getEnclosingClass(), paramLocationInfo);
/* 100 */       return addNesting(((Class)localObject).getEnclosingClass(), paramLocationInfo.pushInner()); }
/* 101 */     if ((paramType instanceof ParameterizedType)) {
/* 102 */       localObject = (ParameterizedType)paramType;
/* 103 */       if (((ParameterizedType)localObject).getOwnerType() == null)
/* 104 */         return paramLocationInfo;
/* 105 */       return addNesting(((ParameterizedType)localObject).getOwnerType(), paramLocationInfo.pushInner());
/*     */     }
/* 107 */     return paramLocationInfo;
/*     */   }
/*     */   
/*     */   private static boolean isArray(Type paramType) {
/* 111 */     if ((paramType instanceof Class)) {
/* 112 */       Class localClass = (Class)paramType;
/* 113 */       if (localClass.isArray())
/* 114 */         return true;
/* 115 */     } else if ((paramType instanceof GenericArrayType)) {
/* 116 */       return true;
/*     */     }
/* 118 */     return false;
/*     */   }
/*     */   
/* 121 */   static final AnnotatedType EMPTY_ANNOTATED_TYPE = new AnnotatedTypeBaseImpl(null, TypeAnnotation.LocationInfo.BASE_LOCATION, new TypeAnnotation[0], new TypeAnnotation[0], null);
/*     */   
/* 123 */   static final AnnotatedType[] EMPTY_ANNOTATED_TYPE_ARRAY = new AnnotatedType[0];
/*     */   
/*     */   private static class AnnotatedTypeBaseImpl implements AnnotatedType
/*     */   {
/*     */     private final Type type;
/*     */     private final AnnotatedElement decl;
/*     */     private final TypeAnnotation.LocationInfo location;
/*     */     private final TypeAnnotation[] allOnSameTargetTypeAnnotations;
/*     */     private final Map<Class<? extends Annotation>, Annotation> annotations;
/*     */     
/*     */     AnnotatedTypeBaseImpl(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
/*     */     {
/* 135 */       this.type = paramType;
/* 136 */       this.decl = paramAnnotatedElement;
/* 137 */       this.location = paramLocationInfo;
/* 138 */       this.allOnSameTargetTypeAnnotations = paramArrayOfTypeAnnotation2;
/* 139 */       this.annotations = TypeAnnotationParser.mapTypeAnnotations(paramLocationInfo.filter(paramArrayOfTypeAnnotation1));
/*     */     }
/*     */     
/*     */ 
/*     */     public final Annotation[] getAnnotations()
/*     */     {
/* 145 */       return getDeclaredAnnotations();
/*     */     }
/*     */     
/*     */     public final <T extends Annotation> T getAnnotation(Class<T> paramClass)
/*     */     {
/* 150 */       return getDeclaredAnnotation(paramClass);
/*     */     }
/*     */     
/*     */     public final <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass)
/*     */     {
/* 155 */       return getDeclaredAnnotationsByType(paramClass);
/*     */     }
/*     */     
/*     */     public final Annotation[] getDeclaredAnnotations()
/*     */     {
/* 160 */       return (Annotation[])this.annotations.values().toArray(new Annotation[0]);
/*     */     }
/*     */     
/*     */ 
/*     */     public final <T extends Annotation> T getDeclaredAnnotation(Class<T> paramClass)
/*     */     {
/* 166 */       return (Annotation)this.annotations.get(paramClass);
/*     */     }
/*     */     
/*     */     public final <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> paramClass)
/*     */     {
/* 171 */       return AnnotationSupport.getDirectlyAndIndirectlyPresent(this.annotations, paramClass);
/*     */     }
/*     */     
/*     */ 
/*     */     public final Type getType()
/*     */     {
/* 177 */       return this.type;
/*     */     }
/*     */     
/*     */     final TypeAnnotation.LocationInfo getLocation()
/*     */     {
/* 182 */       return this.location;
/*     */     }
/*     */     
/* 185 */     final TypeAnnotation[] getTypeAnnotations() { return this.allOnSameTargetTypeAnnotations; }
/*     */     
/*     */     final AnnotatedElement getDecl() {
/* 188 */       return this.decl;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class AnnotatedArrayTypeImpl extends AnnotatedTypeBaseImpl implements AnnotatedArrayType
/*     */   {
/*     */     AnnotatedArrayTypeImpl(Type paramType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
/*     */     {
/* 196 */       super(paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
/*     */     }
/*     */     
/*     */     public AnnotatedType getAnnotatedGenericComponentType()
/*     */     {
/* 201 */       return AnnotatedTypeFactory.buildAnnotatedType(getComponentType(), 
/* 202 */         getLocation().pushArray(), 
/* 203 */         getTypeAnnotations(), 
/* 204 */         getTypeAnnotations(), 
/* 205 */         getDecl());
/*     */     }
/*     */     
/*     */     private Type getComponentType() {
/* 209 */       Type localType = getType();
/* 210 */       if ((localType instanceof Class)) {
/* 211 */         Class localClass = (Class)localType;
/* 212 */         return localClass.getComponentType();
/*     */       }
/* 214 */       return ((GenericArrayType)localType).getGenericComponentType();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class AnnotatedTypeVariableImpl extends AnnotatedTypeBaseImpl implements AnnotatedTypeVariable
/*     */   {
/*     */     AnnotatedTypeVariableImpl(TypeVariable<?> paramTypeVariable, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
/*     */     {
/* 222 */       super(paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
/*     */     }
/*     */     
/*     */     public AnnotatedType[] getAnnotatedBounds()
/*     */     {
/* 227 */       return getTypeVariable().getAnnotatedBounds();
/*     */     }
/*     */     
/*     */     private TypeVariable<?> getTypeVariable() {
/* 231 */       return (TypeVariable)getType();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class AnnotatedParameterizedTypeImpl
/*     */     extends AnnotatedTypeBaseImpl implements AnnotatedParameterizedType
/*     */   {
/*     */     AnnotatedParameterizedTypeImpl(ParameterizedType paramParameterizedType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement)
/*     */     {
/* 240 */       super(paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
/*     */     }
/*     */     
/*     */     public AnnotatedType[] getAnnotatedActualTypeArguments()
/*     */     {
/* 245 */       Type[] arrayOfType = getParameterizedType().getActualTypeArguments();
/* 246 */       AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[arrayOfType.length];
/* 247 */       Arrays.fill(arrayOfAnnotatedType, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
/* 248 */       int i = getTypeAnnotations().length;
/* 249 */       for (int j = 0; j < arrayOfAnnotatedType.length; j++) {
/* 250 */         ArrayList localArrayList = new ArrayList(i);
/* 251 */         TypeAnnotation.LocationInfo localLocationInfo = getLocation().pushTypeArg((short)(byte)j);
/* 252 */         for (TypeAnnotation localTypeAnnotation : getTypeAnnotations())
/* 253 */           if (localTypeAnnotation.getLocationInfo().isSameLocationInfo(localLocationInfo))
/* 254 */             localArrayList.add(localTypeAnnotation);
/* 255 */         arrayOfAnnotatedType[j] = AnnotatedTypeFactory.buildAnnotatedType(arrayOfType[j], localLocationInfo, 
/*     */         
/* 257 */           (TypeAnnotation[])localArrayList.toArray(new TypeAnnotation[0]), 
/* 258 */           getTypeAnnotations(), 
/* 259 */           getDecl());
/*     */       }
/* 261 */       return arrayOfAnnotatedType;
/*     */     }
/*     */     
/*     */     private ParameterizedType getParameterizedType() {
/* 265 */       return (ParameterizedType)getType();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class AnnotatedWildcardTypeImpl extends AnnotatedTypeBaseImpl implements AnnotatedWildcardType
/*     */   {
/*     */     private final boolean hasUpperBounds;
/*     */     
/*     */     AnnotatedWildcardTypeImpl(WildcardType paramWildcardType, TypeAnnotation.LocationInfo paramLocationInfo, TypeAnnotation[] paramArrayOfTypeAnnotation1, TypeAnnotation[] paramArrayOfTypeAnnotation2, AnnotatedElement paramAnnotatedElement) {
/* 274 */       super(paramLocationInfo, paramArrayOfTypeAnnotation1, paramArrayOfTypeAnnotation2, paramAnnotatedElement);
/* 275 */       this.hasUpperBounds = (paramWildcardType.getLowerBounds().length == 0);
/*     */     }
/*     */     
/*     */     public AnnotatedType[] getAnnotatedUpperBounds()
/*     */     {
/* 280 */       if (!hasUpperBounds())
/* 281 */         return new AnnotatedType[0];
/* 282 */       return getAnnotatedBounds(getWildcardType().getUpperBounds());
/*     */     }
/*     */     
/*     */     public AnnotatedType[] getAnnotatedLowerBounds()
/*     */     {
/* 287 */       if (this.hasUpperBounds)
/* 288 */         return new AnnotatedType[0];
/* 289 */       return getAnnotatedBounds(getWildcardType().getLowerBounds());
/*     */     }
/*     */     
/*     */     private AnnotatedType[] getAnnotatedBounds(Type[] paramArrayOfType) {
/* 293 */       AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[paramArrayOfType.length];
/* 294 */       Arrays.fill(arrayOfAnnotatedType, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
/* 295 */       TypeAnnotation.LocationInfo localLocationInfo = getLocation().pushWildcard();
/* 296 */       int i = getTypeAnnotations().length;
/* 297 */       for (int j = 0; j < arrayOfAnnotatedType.length; j++) {
/* 298 */         ArrayList localArrayList = new ArrayList(i);
/* 299 */         for (TypeAnnotation localTypeAnnotation : getTypeAnnotations())
/* 300 */           if (localTypeAnnotation.getLocationInfo().isSameLocationInfo(localLocationInfo))
/* 301 */             localArrayList.add(localTypeAnnotation);
/* 302 */         arrayOfAnnotatedType[j] = AnnotatedTypeFactory.buildAnnotatedType(paramArrayOfType[j], localLocationInfo, 
/*     */         
/* 304 */           (TypeAnnotation[])localArrayList.toArray(new TypeAnnotation[0]), 
/* 305 */           getTypeAnnotations(), 
/* 306 */           getDecl());
/*     */       }
/* 308 */       return arrayOfAnnotatedType;
/*     */     }
/*     */     
/*     */     private WildcardType getWildcardType() {
/* 312 */       return (WildcardType)getType();
/*     */     }
/*     */     
/*     */     private boolean hasUpperBounds() {
/* 316 */       return this.hasUpperBounds;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\annotation\AnnotatedTypeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
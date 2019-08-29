/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.AnnotationFormatError;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.AnnotatedType;
/*     */ import java.lang.reflect.Executable;
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.Type;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import sun.misc.JavaLangAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.reflect.ConstantPool;
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
/*     */ public final class TypeAnnotationParser
/*     */ {
/*  47 */   private static final TypeAnnotation[] EMPTY_TYPE_ANNOTATION_ARRAY = new TypeAnnotation[0];
/*     */   
/*     */   private static final byte CLASS_TYPE_PARAMETER = 0;
/*     */   
/*     */   private static final byte METHOD_TYPE_PARAMETER = 1;
/*     */   
/*     */   private static final byte CLASS_EXTENDS = 16;
/*     */   
/*     */   private static final byte CLASS_TYPE_PARAMETER_BOUND = 17;
/*     */   
/*     */   private static final byte METHOD_TYPE_PARAMETER_BOUND = 18;
/*     */   
/*     */   private static final byte FIELD = 19;
/*     */   private static final byte METHOD_RETURN = 20;
/*     */   private static final byte METHOD_RECEIVER = 21;
/*     */   private static final byte METHOD_FORMAL_PARAMETER = 22;
/*     */   private static final byte THROWS = 23;
/*     */   private static final byte LOCAL_VARIABLE = 64;
/*     */   
/*     */   public static AnnotatedType buildAnnotatedType(byte[] paramArrayOfByte, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass, Type paramType, TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget)
/*     */   {
/*  68 */     TypeAnnotation[] arrayOfTypeAnnotation1 = parseTypeAnnotations(paramArrayOfByte, paramConstantPool, paramAnnotatedElement, paramClass);
/*     */     
/*     */ 
/*     */ 
/*  72 */     ArrayList localArrayList = new ArrayList(arrayOfTypeAnnotation1.length);
/*  73 */     for (TypeAnnotation localTypeAnnotation : arrayOfTypeAnnotation1) {
/*  74 */       TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo = localTypeAnnotation.getTargetInfo();
/*  75 */       if (localTypeAnnotationTargetInfo.getTarget() == paramTypeAnnotationTarget)
/*  76 */         localArrayList.add(localTypeAnnotation);
/*     */     }
/*  78 */     ??? = (TypeAnnotation[])localArrayList.toArray(EMPTY_TYPE_ANNOTATION_ARRAY);
/*  79 */     return AnnotatedTypeFactory.buildAnnotatedType(paramType, TypeAnnotation.LocationInfo.BASE_LOCATION, ???, ???, paramAnnotatedElement);
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
/*     */   public static AnnotatedType[] buildAnnotatedTypes(byte[] paramArrayOfByte, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass, Type[] paramArrayOfType, TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget)
/*     */   {
/* 105 */     int i = paramArrayOfType.length;
/* 106 */     AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[i];
/* 107 */     Arrays.fill(arrayOfAnnotatedType, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
/*     */     
/* 109 */     ArrayList[] arrayOfArrayList = new ArrayList[i];
/*     */     
/* 111 */     TypeAnnotation[] arrayOfTypeAnnotation1 = parseTypeAnnotations(paramArrayOfByte, paramConstantPool, paramAnnotatedElement, paramClass);
/*     */     
/*     */ 
/*     */ 
/* 115 */     for (TypeAnnotation localTypeAnnotation : arrayOfTypeAnnotation1) {
/* 116 */       TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo = localTypeAnnotation.getTargetInfo();
/* 117 */       if (localTypeAnnotationTargetInfo.getTarget() == paramTypeAnnotationTarget) {
/* 118 */         int n = localTypeAnnotationTargetInfo.getCount();
/* 119 */         if (arrayOfArrayList[n] == null) {
/* 120 */           localArrayList2 = new ArrayList(arrayOfTypeAnnotation1.length);
/* 121 */           arrayOfArrayList[n] = localArrayList2;
/*     */         }
/*     */         
/* 124 */         ArrayList localArrayList2 = arrayOfArrayList[n];
/* 125 */         localArrayList2.add(localTypeAnnotation);
/*     */       }
/*     */     }
/* 128 */     for (int j = 0; j < i; j++)
/*     */     {
/* 130 */       ArrayList localArrayList1 = arrayOfArrayList[j];
/*     */       TypeAnnotation[] arrayOfTypeAnnotation3;
/* 132 */       if (localArrayList1 != null) {
/* 133 */         arrayOfTypeAnnotation3 = (TypeAnnotation[])localArrayList1.toArray(new TypeAnnotation[localArrayList1.size()]);
/*     */       } else {
/* 135 */         arrayOfTypeAnnotation3 = EMPTY_TYPE_ANNOTATION_ARRAY;
/*     */       }
/* 137 */       arrayOfAnnotatedType[j] = AnnotatedTypeFactory.buildAnnotatedType(paramArrayOfType[j], TypeAnnotation.LocationInfo.BASE_LOCATION, arrayOfTypeAnnotation3, arrayOfTypeAnnotation3, paramAnnotatedElement);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */     return arrayOfAnnotatedType;
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
/*     */   public static AnnotatedType buildAnnotatedSuperclass(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/* 159 */     Type localType = paramClass.getGenericSuperclass();
/* 160 */     if (localType == null)
/* 161 */       return AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE;
/* 162 */     return buildAnnotatedType(paramArrayOfByte, paramConstantPool, paramClass, paramClass, localType, TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS);
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
/*     */   public static AnnotatedType[] buildAnnotatedInterfaces(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/* 181 */     if ((paramClass == Object.class) || 
/* 182 */       (paramClass.isArray()) || 
/* 183 */       (paramClass.isPrimitive()) || (paramClass == Void.TYPE))
/*     */     {
/* 185 */       return AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE_ARRAY; }
/* 186 */     return buildAnnotatedTypes(paramArrayOfByte, paramConstantPool, paramClass, paramClass, paramClass
/*     */     
/*     */ 
/*     */ 
/* 190 */       .getGenericInterfaces(), TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <D extends GenericDeclaration> Annotation[] parseTypeVariableAnnotations(D paramD, int paramInt)
/*     */   {
/*     */     Object localObject;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     TypeAnnotation.TypeAnnotationTarget localTypeAnnotationTarget;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 209 */     if ((paramD instanceof Class)) {
/* 210 */       localObject = (Class)paramD;
/* 211 */       localTypeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER;
/* 212 */     } else if ((paramD instanceof Executable)) {
/* 213 */       localObject = (Executable)paramD;
/* 214 */       localTypeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER;
/*     */     } else {
/* 216 */       throw new AssertionError("Unknown GenericDeclaration " + paramD + "\nthis should not happen.");
/*     */     }
/* 218 */     List localList = TypeAnnotation.filter(parseAllTypeAnnotations((AnnotatedElement)localObject), localTypeAnnotationTarget);
/*     */     
/* 220 */     ArrayList localArrayList = new ArrayList(localList.size());
/* 221 */     for (TypeAnnotation localTypeAnnotation : localList)
/* 222 */       if (localTypeAnnotation.getTargetInfo().getCount() == paramInt)
/* 223 */         localArrayList.add(localTypeAnnotation.getAnnotation());
/* 224 */     return (Annotation[])localArrayList.toArray(new Annotation[0]);
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
/*     */   public static <D extends GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(Type[] paramArrayOfType, D paramD, int paramInt)
/*     */   {
/* 237 */     return parseAnnotatedBounds(paramArrayOfType, paramD, paramInt, TypeAnnotation.LocationInfo.BASE_LOCATION);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static <D extends GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(Type[] paramArrayOfType, D paramD, int paramInt, TypeAnnotation.LocationInfo paramLocationInfo)
/*     */   {
/* 244 */     List localList = fetchBounds(paramD);
/* 245 */     if (paramArrayOfType != null) {
/* 246 */       int i = 0;
/* 247 */       AnnotatedType[] arrayOfAnnotatedType = new AnnotatedType[paramArrayOfType.length];
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       Object localObject;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 258 */       if (paramArrayOfType.length > 0) {
/* 259 */         Type localType = paramArrayOfType[0];
/* 260 */         if (!(localType instanceof Class)) {
/* 261 */           i = 1;
/*     */         } else {
/* 263 */           localObject = (Class)localType;
/* 264 */           if (((Class)localObject).isInterface()) {
/* 265 */             i = 1;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 270 */       for (int j = 0; j < paramArrayOfType.length; j++) {
/* 271 */         localObject = new ArrayList(localList.size());
/* 272 */         for (TypeAnnotation localTypeAnnotation : localList) {
/* 273 */           TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo = localTypeAnnotation.getTargetInfo();
/* 274 */           if ((localTypeAnnotationTargetInfo.getSecondaryIndex() == j + i) && 
/* 275 */             (localTypeAnnotationTargetInfo.getCount() == paramInt)) {
/* 276 */             ((List)localObject).add(localTypeAnnotation);
/*     */           }
/*     */         }
/* 279 */         arrayOfAnnotatedType[j] = AnnotatedTypeFactory.buildAnnotatedType(paramArrayOfType[j], paramLocationInfo, 
/*     */         
/* 281 */           (TypeAnnotation[])((List)localObject).toArray(EMPTY_TYPE_ANNOTATION_ARRAY), 
/* 282 */           (TypeAnnotation[])localList.toArray(EMPTY_TYPE_ANNOTATION_ARRAY), paramD);
/*     */       }
/*     */       
/* 285 */       return arrayOfAnnotatedType;
/*     */     }
/* 287 */     return new AnnotatedType[0];
/*     */   }
/*     */   
/*     */   private static <D extends GenericDeclaration> List<TypeAnnotation> fetchBounds(D paramD) { TypeAnnotation.TypeAnnotationTarget localTypeAnnotationTarget;
/*     */     Object localObject;
/* 292 */     if ((paramD instanceof Class)) {
/* 293 */       localTypeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND;
/* 294 */       localObject = (Class)paramD;
/*     */     } else {
/* 296 */       localTypeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND;
/* 297 */       localObject = (Executable)paramD;
/*     */     }
/* 299 */     return TypeAnnotation.filter(parseAllTypeAnnotations((AnnotatedElement)localObject), localTypeAnnotationTarget);
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
/*     */   static TypeAnnotation[] parseAllTypeAnnotations(AnnotatedElement paramAnnotatedElement)
/*     */   {
/* 312 */     JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
/* 313 */     Class localClass; byte[] arrayOfByte; if ((paramAnnotatedElement instanceof Class)) {
/* 314 */       localClass = (Class)paramAnnotatedElement;
/* 315 */       arrayOfByte = localJavaLangAccess.getRawClassTypeAnnotations(localClass);
/* 316 */     } else if ((paramAnnotatedElement instanceof Executable)) {
/* 317 */       localClass = ((Executable)paramAnnotatedElement).getDeclaringClass();
/* 318 */       arrayOfByte = localJavaLangAccess.getRawExecutableTypeAnnotations((Executable)paramAnnotatedElement);
/*     */     }
/*     */     else {
/* 321 */       return EMPTY_TYPE_ANNOTATION_ARRAY;
/*     */     }
/* 323 */     return parseTypeAnnotations(arrayOfByte, localJavaLangAccess.getConstantPool(localClass), paramAnnotatedElement, localClass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static TypeAnnotation[] parseTypeAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass)
/*     */   {
/* 332 */     if (paramArrayOfByte == null) {
/* 333 */       return EMPTY_TYPE_ANNOTATION_ARRAY;
/*     */     }
/* 335 */     ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
/* 336 */     int i = localByteBuffer.getShort() & 0xFFFF;
/* 337 */     ArrayList localArrayList = new ArrayList(i);
/*     */     
/*     */ 
/* 340 */     for (int j = 0; j < i; j++) {
/* 341 */       TypeAnnotation localTypeAnnotation = parseTypeAnnotation(localByteBuffer, paramConstantPool, paramAnnotatedElement, paramClass);
/* 342 */       if (localTypeAnnotation != null) {
/* 343 */         localArrayList.add(localTypeAnnotation);
/*     */       }
/*     */     }
/* 346 */     return (TypeAnnotation[])localArrayList.toArray(EMPTY_TYPE_ANNOTATION_ARRAY);
/*     */   }
/*     */   
/*     */ 
/*     */   static Map<Class<? extends Annotation>, Annotation> mapTypeAnnotations(TypeAnnotation[] paramArrayOfTypeAnnotation)
/*     */   {
/* 352 */     LinkedHashMap localLinkedHashMap = new LinkedHashMap();
/*     */     
/* 354 */     for (TypeAnnotation localTypeAnnotation : paramArrayOfTypeAnnotation) {
/* 355 */       Annotation localAnnotation = localTypeAnnotation.getAnnotation();
/* 356 */       Class localClass = localAnnotation.annotationType();
/* 357 */       AnnotationType localAnnotationType = AnnotationType.getInstance(localClass);
/* 358 */       if ((localAnnotationType.retention() == RetentionPolicy.RUNTIME) && 
/* 359 */         (localLinkedHashMap.put(localClass, localAnnotation) != null))
/* 360 */         throw new AnnotationFormatError("Duplicate annotation for class: " + localClass + ": " + localAnnotation);
/*     */     }
/* 362 */     return localLinkedHashMap;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final byte RESOURCE_VARIABLE = 65;
/*     */   
/*     */ 
/*     */   private static final byte EXCEPTION_PARAMETER = 66;
/*     */   
/*     */ 
/*     */   private static final byte INSTANCEOF = 67;
/*     */   
/*     */ 
/*     */   private static final byte NEW = 68;
/*     */   
/*     */ 
/*     */   private static final byte CONSTRUCTOR_REFERENCE = 69;
/*     */   
/*     */   private static final byte METHOD_REFERENCE = 70;
/*     */   
/*     */   private static final byte CAST = 71;
/*     */   
/*     */   private static final byte CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 72;
/*     */   
/*     */   private static final byte METHOD_INVOCATION_TYPE_ARGUMENT = 73;
/*     */   
/*     */   private static final byte CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 74;
/*     */   
/*     */   private static final byte METHOD_REFERENCE_TYPE_ARGUMENT = 75;
/*     */   
/*     */   private static TypeAnnotation parseTypeAnnotation(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, AnnotatedElement paramAnnotatedElement, Class<?> paramClass)
/*     */   {
/*     */     try
/*     */     {
/* 397 */       TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo = parseTargetInfo(paramByteBuffer);
/* 398 */       TypeAnnotation.LocationInfo localLocationInfo = TypeAnnotation.LocationInfo.parseLocationInfo(paramByteBuffer);
/* 399 */       Annotation localAnnotation = AnnotationParser.parseAnnotation(paramByteBuffer, paramConstantPool, paramClass, false);
/* 400 */       if (localTypeAnnotationTargetInfo == null)
/* 401 */         return null;
/* 402 */       return new TypeAnnotation(localTypeAnnotationTargetInfo, localLocationInfo, localAnnotation, paramAnnotatedElement);
/*     */     }
/*     */     catch (IllegalArgumentException|BufferUnderflowException localIllegalArgumentException) {
/* 405 */       throw new AnnotationFormatError(localIllegalArgumentException);
/*     */     }
/*     */   }
/*     */   
/*     */   private static TypeAnnotation.TypeAnnotationTargetInfo parseTargetInfo(ByteBuffer paramByteBuffer) {
/* 410 */     int i = paramByteBuffer.get() & 0xFF;
/* 411 */     int j; TypeAnnotation.TypeAnnotationTargetInfo localTypeAnnotationTargetInfo; int k; int m; switch (i) {
/*     */     case 0: 
/*     */     case 1: 
/* 414 */       j = paramByteBuffer.get() & 0xFF;
/*     */       
/* 416 */       if (i == 0) {
/* 417 */         localTypeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER, j);
/*     */       }
/*     */       else {
/* 420 */         localTypeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER, j);
/*     */       }
/* 422 */       return localTypeAnnotationTargetInfo;
/*     */     
/*     */     case 16: 
/* 425 */       j = paramByteBuffer.getShort();
/* 426 */       if (j == -1)
/* 427 */         return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS);
/* 428 */       if (j >= 0) {
/* 429 */         localTypeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS, j);
/*     */         
/* 431 */         return localTypeAnnotationTargetInfo; }
/* 432 */       break;
/*     */     case 17: 
/* 434 */       return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND, paramByteBuffer);
/*     */     case 18: 
/* 436 */       return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND, paramByteBuffer);
/*     */     case 19: 
/* 438 */       return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.FIELD);
/*     */     case 20: 
/* 440 */       return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN);
/*     */     case 21: 
/* 442 */       return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
/*     */     case 22: 
/* 444 */       j = paramByteBuffer.get() & 0xFF;
/* 445 */       return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER, j);
/*     */     
/*     */ 
/*     */     case 23: 
/* 449 */       return parseShortTarget(TypeAnnotation.TypeAnnotationTarget.THROWS, paramByteBuffer);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     case 64: 
/*     */     case 65: 
/* 457 */       j = paramByteBuffer.getShort();
/* 458 */       for (k = 0; k < j; k++) {
/* 459 */         m = paramByteBuffer.getShort();
/* 460 */         int n = paramByteBuffer.getShort();
/* 461 */         int i1 = paramByteBuffer.getShort();
/*     */       }
/* 463 */       return null;
/*     */     case 66: 
/* 465 */       k = paramByteBuffer.get();
/*     */       
/* 467 */       return null;
/*     */     case 67: 
/*     */     case 68: 
/*     */     case 69: 
/*     */     case 70: 
/* 472 */       k = paramByteBuffer.getShort();
/*     */       
/* 474 */       return null;
/*     */     case 71: 
/*     */     case 72: 
/*     */     case 73: 
/*     */     case 74: 
/*     */     case 75: 
/* 480 */       k = paramByteBuffer.getShort();
/* 481 */       m = paramByteBuffer.get();
/*     */       
/* 483 */       return null;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 489 */     throw new AnnotationFormatError("Could not parse bytes for type annotations");
/*     */   }
/*     */   
/*     */   private static TypeAnnotation.TypeAnnotationTargetInfo parseShortTarget(TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget, ByteBuffer paramByteBuffer) {
/* 493 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 494 */     return new TypeAnnotation.TypeAnnotationTargetInfo(paramTypeAnnotationTarget, i);
/*     */   }
/*     */   
/* 497 */   private static TypeAnnotation.TypeAnnotationTargetInfo parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget, ByteBuffer paramByteBuffer) { int i = paramByteBuffer.get() & 0xFF;
/* 498 */     int j = paramByteBuffer.get() & 0xFF;
/* 499 */     return new TypeAnnotation.TypeAnnotationTargetInfo(paramTypeAnnotationTarget, i, j);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\annotation\TypeAnnotationParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
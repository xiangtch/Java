/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.AnnotationFormatError;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.lang.reflect.Type;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import sun.reflect.ConstantPool;
/*     */ import sun.reflect.generics.factory.CoreReflectionFactory;
/*     */ import sun.reflect.generics.parser.SignatureParser;
/*     */ import sun.reflect.generics.scope.ClassScope;
/*     */ import sun.reflect.generics.tree.TypeSignature;
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
/*     */ public class AnnotationParser
/*     */ {
/*     */   public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/*  68 */     if (paramArrayOfByte == null) {
/*  69 */       return Collections.emptyMap();
/*     */     }
/*     */     try {
/*  72 */       return parseAnnotations2(paramArrayOfByte, paramConstantPool, paramClass, null);
/*     */     } catch (BufferUnderflowException localBufferUnderflowException) {
/*  74 */       throw new AnnotationFormatError("Unexpected end of annotations.");
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/*  77 */       throw new AnnotationFormatError(localIllegalArgumentException);
/*     */     }
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
/*     */   @SafeVarargs
/*     */   static Map<Class<? extends Annotation>, Annotation> parseSelectAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass, Class<? extends Annotation>... paramVarArgs)
/*     */   {
/*  97 */     if (paramArrayOfByte == null) {
/*  98 */       return Collections.emptyMap();
/*     */     }
/*     */     try {
/* 101 */       return parseAnnotations2(paramArrayOfByte, paramConstantPool, paramClass, paramVarArgs);
/*     */     } catch (BufferUnderflowException localBufferUnderflowException) {
/* 103 */       throw new AnnotationFormatError("Unexpected end of annotations.");
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/* 106 */       throw new AnnotationFormatError(localIllegalArgumentException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Map<Class<? extends Annotation>, Annotation> parseAnnotations2(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass, Class<? extends Annotation>[] paramArrayOfClass)
/*     */   {
/* 115 */     LinkedHashMap localLinkedHashMap = new LinkedHashMap();
/*     */     
/* 117 */     ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
/* 118 */     int i = localByteBuffer.getShort() & 0xFFFF;
/* 119 */     for (int j = 0; j < i; j++) {
/* 120 */       Annotation localAnnotation = parseAnnotation2(localByteBuffer, paramConstantPool, paramClass, false, paramArrayOfClass);
/* 121 */       if (localAnnotation != null) {
/* 122 */         Class localClass = localAnnotation.annotationType();
/* 123 */         if ((AnnotationType.getInstance(localClass).retention() == RetentionPolicy.RUNTIME) && 
/* 124 */           (localLinkedHashMap.put(localClass, localAnnotation) != null)) {
/* 125 */           throw new AnnotationFormatError("Duplicate annotation for class: " + localClass + ": " + localAnnotation);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 130 */     return localLinkedHashMap;
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
/*     */   public static Annotation[][] parseParameterAnnotations(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/*     */     try
/*     */     {
/* 161 */       return parseParameterAnnotations2(paramArrayOfByte, paramConstantPool, paramClass);
/*     */     } catch (BufferUnderflowException localBufferUnderflowException) {
/* 163 */       throw new AnnotationFormatError("Unexpected end of parameter annotations.");
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException)
/*     */     {
/* 167 */       throw new AnnotationFormatError(localIllegalArgumentException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static Annotation[][] parseParameterAnnotations2(byte[] paramArrayOfByte, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/* 175 */     ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
/* 176 */     int i = localByteBuffer.get() & 0xFF;
/* 177 */     Annotation[][] arrayOfAnnotation = new Annotation[i][];
/*     */     
/* 179 */     for (int j = 0; j < i; j++) {
/* 180 */       int k = localByteBuffer.getShort() & 0xFFFF;
/* 181 */       ArrayList localArrayList = new ArrayList(k);
/*     */       
/* 183 */       for (int m = 0; m < k; m++) {
/* 184 */         Annotation localAnnotation = parseAnnotation(localByteBuffer, paramConstantPool, paramClass, false);
/* 185 */         if (localAnnotation != null) {
/* 186 */           AnnotationType localAnnotationType = AnnotationType.getInstance(localAnnotation
/* 187 */             .annotationType());
/* 188 */           if (localAnnotationType.retention() == RetentionPolicy.RUNTIME)
/* 189 */             localArrayList.add(localAnnotation);
/*     */         }
/*     */       }
/* 192 */       arrayOfAnnotation[j] = ((Annotation[])localArrayList.toArray(EMPTY_ANNOTATIONS_ARRAY));
/*     */     }
/* 194 */     return arrayOfAnnotation;
/*     */   }
/*     */   
/* 197 */   private static final Annotation[] EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];
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
/*     */   static Annotation parseAnnotation(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass, boolean paramBoolean)
/*     */   {
/* 226 */     return parseAnnotation2(paramByteBuffer, paramConstantPool, paramClass, paramBoolean, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Annotation parseAnnotation2(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass, boolean paramBoolean, Class<? extends Annotation>[] paramArrayOfClass)
/*     */   {
/* 235 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 236 */     Class localClass1 = null;
/* 237 */     String str1 = "[unknown]";
/*     */     try {
/*     */       try {
/* 240 */         str1 = paramConstantPool.getUTF8At(i);
/* 241 */         localClass1 = parseSig(str1, paramClass);
/*     */       }
/*     */       catch (IllegalArgumentException localIllegalArgumentException1) {
/* 244 */         localClass1 = paramConstantPool.getClassAt(i);
/*     */       }
/*     */     } catch (NoClassDefFoundError localNoClassDefFoundError) {
/* 247 */       if (paramBoolean)
/*     */       {
/*     */ 
/* 250 */         throw new TypeNotPresentException(str1, localNoClassDefFoundError); }
/* 251 */       skipAnnotation(paramByteBuffer, false);
/* 252 */       return null;
/*     */     }
/*     */     catch (TypeNotPresentException localTypeNotPresentException) {
/* 255 */       if (paramBoolean)
/* 256 */         throw localTypeNotPresentException;
/* 257 */       skipAnnotation(paramByteBuffer, false);
/* 258 */       return null;
/*     */     }
/* 260 */     if ((paramArrayOfClass != null) && (!contains(paramArrayOfClass, localClass1))) {
/* 261 */       skipAnnotation(paramByteBuffer, false);
/* 262 */       return null;
/*     */     }
/* 264 */     AnnotationType localAnnotationType = null;
/*     */     try {
/* 266 */       localAnnotationType = AnnotationType.getInstance(localClass1);
/*     */     } catch (IllegalArgumentException localIllegalArgumentException2) {
/* 268 */       skipAnnotation(paramByteBuffer, false);
/* 269 */       return null;
/*     */     }
/*     */     
/* 272 */     Map localMap = localAnnotationType.memberTypes();
/*     */     
/* 274 */     LinkedHashMap localLinkedHashMap = new LinkedHashMap(localAnnotationType.memberDefaults());
/*     */     
/* 276 */     int j = paramByteBuffer.getShort() & 0xFFFF;
/* 277 */     for (int k = 0; k < j; k++) {
/* 278 */       int m = paramByteBuffer.getShort() & 0xFFFF;
/* 279 */       String str2 = paramConstantPool.getUTF8At(m);
/* 280 */       Class localClass2 = (Class)localMap.get(str2);
/*     */       
/* 282 */       if (localClass2 == null)
/*     */       {
/* 284 */         skipMemberValue(paramByteBuffer);
/*     */       } else {
/* 286 */         Object localObject = parseMemberValue(localClass2, paramByteBuffer, paramConstantPool, paramClass);
/* 287 */         if ((localObject instanceof AnnotationTypeMismatchExceptionProxy))
/*     */         {
/* 289 */           ((AnnotationTypeMismatchExceptionProxy)localObject).setMember((Method)localAnnotationType.members().get(str2)); }
/* 290 */         localLinkedHashMap.put(str2, localObject);
/*     */       }
/*     */     }
/* 293 */     return annotationForMap(localClass1, localLinkedHashMap);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Annotation annotationForMap(Class<? extends Annotation> paramClass, final Map<String, Object> paramMap)
/*     */   {
/* 303 */     (Annotation)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Annotation run() {
/* 305 */         return (Annotation)Proxy.newProxyInstance(this.val$type
/* 306 */           .getClassLoader(), new Class[] { this.val$type }, new AnnotationInvocationHandler(this.val$type, paramMap));
/*     */       }
/*     */     });
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object parseMemberValue(Class<?> paramClass1, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass2)
/*     */   {
/* 343 */     Object localObject = null;
/* 344 */     int i = paramByteBuffer.get();
/* 345 */     switch (i) {
/*     */     case 101: 
/* 347 */       return parseEnumValue(paramClass1, paramByteBuffer, paramConstantPool, paramClass2);
/*     */     case 99: 
/* 349 */       localObject = parseClassValue(paramByteBuffer, paramConstantPool, paramClass2);
/* 350 */       break;
/*     */     case 64: 
/* 352 */       localObject = parseAnnotation(paramByteBuffer, paramConstantPool, paramClass2, true);
/* 353 */       break;
/*     */     case 91: 
/* 355 */       return parseArray(paramClass1, paramByteBuffer, paramConstantPool, paramClass2);
/*     */     default: 
/* 357 */       localObject = parseConst(i, paramByteBuffer, paramConstantPool);
/*     */     }
/*     */     
/* 360 */     if ((!(localObject instanceof ExceptionProxy)) && 
/* 361 */       (!paramClass1.isInstance(localObject)))
/*     */     {
/* 363 */       localObject = new AnnotationTypeMismatchExceptionProxy(localObject.getClass() + "[" + localObject + "]"); }
/* 364 */     return localObject;
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
/*     */   private static Object parseConst(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 379 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 380 */     switch (paramInt) {
/*     */     case 66: 
/* 382 */       return Byte.valueOf((byte)paramConstantPool.getIntAt(i));
/*     */     case 67: 
/* 384 */       return Character.valueOf((char)paramConstantPool.getIntAt(i));
/*     */     case 68: 
/* 386 */       return Double.valueOf(paramConstantPool.getDoubleAt(i));
/*     */     case 70: 
/* 388 */       return Float.valueOf(paramConstantPool.getFloatAt(i));
/*     */     case 73: 
/* 390 */       return Integer.valueOf(paramConstantPool.getIntAt(i));
/*     */     case 74: 
/* 392 */       return Long.valueOf(paramConstantPool.getLongAt(i));
/*     */     case 83: 
/* 394 */       return Short.valueOf((short)paramConstantPool.getIntAt(i));
/*     */     case 90: 
/* 396 */       return Boolean.valueOf(paramConstantPool.getIntAt(i) != 0);
/*     */     case 115: 
/* 398 */       return paramConstantPool.getUTF8At(i);
/*     */     }
/* 400 */     throw new AnnotationFormatError("Invalid member-value tag in annotation: " + paramInt);
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
/*     */   private static Object parseClassValue(ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/* 416 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/*     */     try
/*     */     {
/* 419 */       String str = paramConstantPool.getUTF8At(i);
/* 420 */       return parseSig(str, paramClass);
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/* 423 */       return paramConstantPool.getClassAt(i);
/*     */     }
/*     */     catch (NoClassDefFoundError localNoClassDefFoundError) {
/* 426 */       return new TypeNotPresentExceptionProxy("[unknown]", localNoClassDefFoundError);
/*     */     }
/*     */     catch (TypeNotPresentException localTypeNotPresentException) {
/* 429 */       return new TypeNotPresentExceptionProxy(localTypeNotPresentException.typeName(), localTypeNotPresentException.getCause());
/*     */     }
/*     */   }
/*     */   
/*     */   private static Class<?> parseSig(String paramString, Class<?> paramClass) {
/* 434 */     if (paramString.equals("V")) return Void.TYPE;
/* 435 */     SignatureParser localSignatureParser = SignatureParser.make();
/* 436 */     TypeSignature localTypeSignature = localSignatureParser.parseTypeSig(paramString);
/* 437 */     CoreReflectionFactory localCoreReflectionFactory = CoreReflectionFactory.make(paramClass, ClassScope.make(paramClass));
/* 438 */     Reifier localReifier = Reifier.make(localCoreReflectionFactory);
/* 439 */     localTypeSignature.accept(localReifier);
/* 440 */     Type localType = localReifier.getResult();
/* 441 */     return toClass(localType);
/*     */   }
/*     */   
/* 444 */   static Class<?> toClass(Type paramType) { if ((paramType instanceof GenericArrayType))
/* 445 */       return 
/*     */       
/* 447 */         Array.newInstance(toClass(((GenericArrayType)paramType).getGenericComponentType()), 0).getClass();
/* 448 */     return (Class)paramType;
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
/*     */   private static Object parseEnumValue(Class<? extends Enum> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1)
/*     */   {
/* 467 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 468 */     String str1 = paramConstantPool.getUTF8At(i);
/* 469 */     int j = paramByteBuffer.getShort() & 0xFFFF;
/* 470 */     String str2 = paramConstantPool.getUTF8At(j);
/*     */     
/* 472 */     if (!str1.endsWith(";"))
/*     */     {
/* 474 */       if (!paramClass.getName().equals(str1)) {
/* 475 */         return new AnnotationTypeMismatchExceptionProxy(str1 + "." + str2);
/*     */       }
/* 477 */     } else if (paramClass != parseSig(str1, paramClass1)) {
/* 478 */       return new AnnotationTypeMismatchExceptionProxy(str1 + "." + str2);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 483 */       return Enum.valueOf(paramClass, str2);
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {}
/* 485 */     return new EnumConstantNotPresentExceptionProxy(paramClass, str2);
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
/*     */   private static Object parseArray(Class<?> paramClass1, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass2)
/*     */   {
/* 509 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 510 */     Class localClass = paramClass1.getComponentType();
/*     */     
/* 512 */     if (localClass == Byte.TYPE)
/* 513 */       return parseByteArray(i, paramByteBuffer, paramConstantPool);
/* 514 */     if (localClass == Character.TYPE)
/* 515 */       return parseCharArray(i, paramByteBuffer, paramConstantPool);
/* 516 */     if (localClass == Double.TYPE)
/* 517 */       return parseDoubleArray(i, paramByteBuffer, paramConstantPool);
/* 518 */     if (localClass == Float.TYPE)
/* 519 */       return parseFloatArray(i, paramByteBuffer, paramConstantPool);
/* 520 */     if (localClass == Integer.TYPE)
/* 521 */       return parseIntArray(i, paramByteBuffer, paramConstantPool);
/* 522 */     if (localClass == Long.TYPE)
/* 523 */       return parseLongArray(i, paramByteBuffer, paramConstantPool);
/* 524 */     if (localClass == Short.TYPE)
/* 525 */       return parseShortArray(i, paramByteBuffer, paramConstantPool);
/* 526 */     if (localClass == Boolean.TYPE)
/* 527 */       return parseBooleanArray(i, paramByteBuffer, paramConstantPool);
/* 528 */     if (localClass == String.class)
/* 529 */       return parseStringArray(i, paramByteBuffer, paramConstantPool);
/* 530 */     if (localClass == Class.class)
/* 531 */       return parseClassArray(i, paramByteBuffer, paramConstantPool, paramClass2);
/* 532 */     if (localClass.isEnum()) {
/* 533 */       return parseEnumArray(i, localClass, paramByteBuffer, paramConstantPool, paramClass2);
/*     */     }
/*     */     
/* 536 */     assert (localClass.isAnnotation());
/* 537 */     return parseAnnotationArray(i, localClass, paramByteBuffer, paramConstantPool, paramClass2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static Object parseByteArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 544 */     byte[] arrayOfByte = new byte[paramInt];
/* 545 */     int i = 0;
/* 546 */     int j = 0;
/*     */     
/* 548 */     for (int k = 0; k < paramInt; k++) {
/* 549 */       j = paramByteBuffer.get();
/* 550 */       if (j == 66) {
/* 551 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 552 */         arrayOfByte[k] = ((byte)paramConstantPool.getIntAt(m));
/*     */       } else {
/* 554 */         skipMemberValue(j, paramByteBuffer);
/* 555 */         i = 1;
/*     */       }
/*     */     }
/* 558 */     return i != 0 ? exceptionProxy(j) : arrayOfByte;
/*     */   }
/*     */   
/*     */   private static Object parseCharArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 563 */     char[] arrayOfChar = new char[paramInt];
/* 564 */     int i = 0;
/* 565 */     int j = 0;
/*     */     
/* 567 */     for (int k = 0; k < paramInt; k++) {
/* 568 */       j = paramByteBuffer.get();
/* 569 */       if (j == 67) {
/* 570 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 571 */         arrayOfChar[k] = ((char)paramConstantPool.getIntAt(m));
/*     */       } else {
/* 573 */         skipMemberValue(j, paramByteBuffer);
/* 574 */         i = 1;
/*     */       }
/*     */     }
/* 577 */     return i != 0 ? exceptionProxy(j) : arrayOfChar;
/*     */   }
/*     */   
/*     */   private static Object parseDoubleArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 582 */     double[] arrayOfDouble = new double[paramInt];
/* 583 */     int i = 0;
/* 584 */     int j = 0;
/*     */     
/* 586 */     for (int k = 0; k < paramInt; k++) {
/* 587 */       j = paramByteBuffer.get();
/* 588 */       if (j == 68) {
/* 589 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 590 */         arrayOfDouble[k] = paramConstantPool.getDoubleAt(m);
/*     */       } else {
/* 592 */         skipMemberValue(j, paramByteBuffer);
/* 593 */         i = 1;
/*     */       }
/*     */     }
/* 596 */     return i != 0 ? exceptionProxy(j) : arrayOfDouble;
/*     */   }
/*     */   
/*     */   private static Object parseFloatArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 601 */     float[] arrayOfFloat = new float[paramInt];
/* 602 */     int i = 0;
/* 603 */     int j = 0;
/*     */     
/* 605 */     for (int k = 0; k < paramInt; k++) {
/* 606 */       j = paramByteBuffer.get();
/* 607 */       if (j == 70) {
/* 608 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 609 */         arrayOfFloat[k] = paramConstantPool.getFloatAt(m);
/*     */       } else {
/* 611 */         skipMemberValue(j, paramByteBuffer);
/* 612 */         i = 1;
/*     */       }
/*     */     }
/* 615 */     return i != 0 ? exceptionProxy(j) : arrayOfFloat;
/*     */   }
/*     */   
/*     */   private static Object parseIntArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 620 */     int[] arrayOfInt = new int[paramInt];
/* 621 */     int i = 0;
/* 622 */     int j = 0;
/*     */     
/* 624 */     for (int k = 0; k < paramInt; k++) {
/* 625 */       j = paramByteBuffer.get();
/* 626 */       if (j == 73) {
/* 627 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 628 */         arrayOfInt[k] = paramConstantPool.getIntAt(m);
/*     */       } else {
/* 630 */         skipMemberValue(j, paramByteBuffer);
/* 631 */         i = 1;
/*     */       }
/*     */     }
/* 634 */     return i != 0 ? exceptionProxy(j) : arrayOfInt;
/*     */   }
/*     */   
/*     */   private static Object parseLongArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 639 */     long[] arrayOfLong = new long[paramInt];
/* 640 */     int i = 0;
/* 641 */     int j = 0;
/*     */     
/* 643 */     for (int k = 0; k < paramInt; k++) {
/* 644 */       j = paramByteBuffer.get();
/* 645 */       if (j == 74) {
/* 646 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 647 */         arrayOfLong[k] = paramConstantPool.getLongAt(m);
/*     */       } else {
/* 649 */         skipMemberValue(j, paramByteBuffer);
/* 650 */         i = 1;
/*     */       }
/*     */     }
/* 653 */     return i != 0 ? exceptionProxy(j) : arrayOfLong;
/*     */   }
/*     */   
/*     */   private static Object parseShortArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 658 */     short[] arrayOfShort = new short[paramInt];
/* 659 */     int i = 0;
/* 660 */     int j = 0;
/*     */     
/* 662 */     for (int k = 0; k < paramInt; k++) {
/* 663 */       j = paramByteBuffer.get();
/* 664 */       if (j == 83) {
/* 665 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 666 */         arrayOfShort[k] = ((short)paramConstantPool.getIntAt(m));
/*     */       } else {
/* 668 */         skipMemberValue(j, paramByteBuffer);
/* 669 */         i = 1;
/*     */       }
/*     */     }
/* 672 */     return i != 0 ? exceptionProxy(j) : arrayOfShort;
/*     */   }
/*     */   
/*     */   private static Object parseBooleanArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 677 */     boolean[] arrayOfBoolean = new boolean[paramInt];
/* 678 */     int i = 0;
/* 679 */     int j = 0;
/*     */     
/* 681 */     for (int k = 0; k < paramInt; k++) {
/* 682 */       j = paramByteBuffer.get();
/* 683 */       if (j == 90) {
/* 684 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 685 */         arrayOfBoolean[k] = (paramConstantPool.getIntAt(m) != 0 ? 1 : false);
/*     */       } else {
/* 687 */         skipMemberValue(j, paramByteBuffer);
/* 688 */         i = 1;
/*     */       }
/*     */     }
/* 691 */     return i != 0 ? exceptionProxy(j) : arrayOfBoolean;
/*     */   }
/*     */   
/*     */   private static Object parseStringArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool)
/*     */   {
/* 696 */     String[] arrayOfString = new String[paramInt];
/* 697 */     int i = 0;
/* 698 */     int j = 0;
/*     */     
/* 700 */     for (int k = 0; k < paramInt; k++) {
/* 701 */       j = paramByteBuffer.get();
/* 702 */       if (j == 115) {
/* 703 */         int m = paramByteBuffer.getShort() & 0xFFFF;
/* 704 */         arrayOfString[k] = paramConstantPool.getUTF8At(m);
/*     */       } else {
/* 706 */         skipMemberValue(j, paramByteBuffer);
/* 707 */         i = 1;
/*     */       }
/*     */     }
/* 710 */     return i != 0 ? exceptionProxy(j) : arrayOfString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static Object parseClassArray(int paramInt, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass)
/*     */   {
/* 717 */     Class[] arrayOfClass = new Class[paramInt];
/* 718 */     int i = 0;
/* 719 */     int j = 0;
/*     */     
/* 721 */     for (int k = 0; k < paramInt; k++) {
/* 722 */       j = paramByteBuffer.get();
/* 723 */       if (j == 99) {
/* 724 */         arrayOfClass[k] = parseClassValue(paramByteBuffer, paramConstantPool, paramClass);
/*     */       } else {
/* 726 */         skipMemberValue(j, paramByteBuffer);
/* 727 */         i = 1;
/*     */       }
/*     */     }
/* 730 */     return i != 0 ? exceptionProxy(j) : arrayOfClass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static Object parseEnumArray(int paramInt, Class<? extends Enum<?>> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1)
/*     */   {
/* 737 */     Object[] arrayOfObject = (Object[])Array.newInstance(paramClass, paramInt);
/* 738 */     int i = 0;
/* 739 */     int j = 0;
/*     */     
/* 741 */     for (int k = 0; k < paramInt; k++) {
/* 742 */       j = paramByteBuffer.get();
/* 743 */       if (j == 101) {
/* 744 */         arrayOfObject[k] = parseEnumValue(paramClass, paramByteBuffer, paramConstantPool, paramClass1);
/*     */       } else {
/* 746 */         skipMemberValue(j, paramByteBuffer);
/* 747 */         i = 1;
/*     */       }
/*     */     }
/* 750 */     return i != 0 ? exceptionProxy(j) : arrayOfObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Object parseAnnotationArray(int paramInt, Class<? extends Annotation> paramClass, ByteBuffer paramByteBuffer, ConstantPool paramConstantPool, Class<?> paramClass1)
/*     */   {
/* 758 */     Object[] arrayOfObject = (Object[])Array.newInstance(paramClass, paramInt);
/* 759 */     int i = 0;
/* 760 */     int j = 0;
/*     */     
/* 762 */     for (int k = 0; k < paramInt; k++) {
/* 763 */       j = paramByteBuffer.get();
/* 764 */       if (j == 64) {
/* 765 */         arrayOfObject[k] = parseAnnotation(paramByteBuffer, paramConstantPool, paramClass1, true);
/*     */       } else {
/* 767 */         skipMemberValue(j, paramByteBuffer);
/* 768 */         i = 1;
/*     */       }
/*     */     }
/* 771 */     return i != 0 ? exceptionProxy(j) : arrayOfObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static ExceptionProxy exceptionProxy(int paramInt)
/*     */   {
/* 779 */     return new AnnotationTypeMismatchExceptionProxy("Array with component tag: " + paramInt);
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
/*     */   private static void skipAnnotation(ByteBuffer paramByteBuffer, boolean paramBoolean)
/*     */   {
/* 793 */     if (paramBoolean)
/* 794 */       paramByteBuffer.getShort();
/* 795 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 796 */     for (int j = 0; j < i; j++) {
/* 797 */       paramByteBuffer.getShort();
/* 798 */       skipMemberValue(paramByteBuffer);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void skipMemberValue(ByteBuffer paramByteBuffer)
/*     */   {
/* 808 */     int i = paramByteBuffer.get();
/* 809 */     skipMemberValue(i, paramByteBuffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void skipMemberValue(int paramInt, ByteBuffer paramByteBuffer)
/*     */   {
/* 818 */     switch (paramInt) {
/*     */     case 101: 
/* 820 */       paramByteBuffer.getInt();
/* 821 */       break;
/*     */     case 64: 
/* 823 */       skipAnnotation(paramByteBuffer, true);
/* 824 */       break;
/*     */     case 91: 
/* 826 */       skipArray(paramByteBuffer);
/* 827 */       break;
/*     */     
/*     */     default: 
/* 830 */       paramByteBuffer.getShort();
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void skipArray(ByteBuffer paramByteBuffer)
/*     */   {
/* 840 */     int i = paramByteBuffer.getShort() & 0xFFFF;
/* 841 */     for (int j = 0; j < i; j++) {
/* 842 */       skipMemberValue(paramByteBuffer);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean contains(Object[] paramArrayOfObject, Object paramObject)
/*     */   {
/* 850 */     for (Object localObject : paramArrayOfObject)
/* 851 */       if (localObject == paramObject)
/* 852 */         return true;
/* 853 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 863 */   private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
/*     */   
/* 865 */   public static Annotation[] toArray(Map<Class<? extends Annotation>, Annotation> paramMap) { return (Annotation[])paramMap.values().toArray(EMPTY_ANNOTATION_ARRAY); }
/*     */   
/*     */   static Annotation[] getEmptyAnnotationArray() {
/* 868 */     return EMPTY_ANNOTATION_ARRAY;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\annotation\AnnotationParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
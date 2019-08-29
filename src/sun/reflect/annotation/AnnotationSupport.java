/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.AnnotationFormatError;
/*     */ import java.lang.annotation.Repeatable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import sun.misc.JavaLangAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class AnnotationSupport
/*     */ {
/*  40 */   private static final JavaLangAccess LANG_ACCESS = ;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <A extends Annotation> A[] getDirectlyAndIndirectlyPresent(Map<Class<? extends Annotation>, Annotation> paramMap, Class<A> paramClass)
/*     */   {
/*  65 */     ArrayList localArrayList = new ArrayList();
/*     */     
/*     */ 
/*  68 */     Annotation localAnnotation = (Annotation)paramMap.get(paramClass);
/*  69 */     if (localAnnotation != null) {
/*  70 */       localArrayList.add(localAnnotation);
/*     */     }
/*  72 */     Annotation[] arrayOfAnnotation1 = getIndirectlyPresent(paramMap, paramClass);
/*  73 */     if ((arrayOfAnnotation1 != null) && (arrayOfAnnotation1.length != 0))
/*     */     {
/*  75 */       int i = (localAnnotation == null) || (containerBeforeContainee(paramMap, paramClass)) ? 1 : 0;
/*     */       
/*  77 */       localArrayList.addAll(i != 0 ? 0 : 1, Arrays.asList(arrayOfAnnotation1));
/*     */     }
/*     */     
/*     */ 
/*  81 */     Annotation[] arrayOfAnnotation2 = (Annotation[])Array.newInstance(paramClass, localArrayList.size());
/*  82 */     return (Annotation[])localArrayList.toArray(arrayOfAnnotation2);
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
/*     */   private static <A extends Annotation> A[] getIndirectlyPresent(Map<Class<? extends Annotation>, Annotation> paramMap, Class<A> paramClass)
/*     */   {
/*  99 */     Repeatable localRepeatable = (Repeatable)paramClass.getDeclaredAnnotation(Repeatable.class);
/* 100 */     if (localRepeatable == null) {
/* 101 */       return null;
/*     */     }
/* 103 */     Class localClass = localRepeatable.value();
/*     */     
/* 105 */     Annotation localAnnotation = (Annotation)paramMap.get(localClass);
/* 106 */     if (localAnnotation == null) {
/* 107 */       return null;
/*     */     }
/*     */     
/* 110 */     Annotation[] arrayOfAnnotation = getValueArray(localAnnotation);
/* 111 */     checkTypes(arrayOfAnnotation, localAnnotation, paramClass);
/*     */     
/* 113 */     return arrayOfAnnotation;
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
/*     */   private static <A extends Annotation> boolean containerBeforeContainee(Map<Class<? extends Annotation>, Annotation> paramMap, Class<A> paramClass)
/*     */   {
/* 129 */     Class localClass1 = ((Repeatable)paramClass.getDeclaredAnnotation(Repeatable.class)).value();
/*     */     
/* 131 */     for (Class localClass2 : paramMap.keySet()) {
/* 132 */       if (localClass2 == localClass1) return true;
/* 133 */       if (localClass2 == paramClass) { return false;
/*     */       }
/*     */     }
/*     */     
/* 137 */     return false;
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
/*     */   public static <A extends Annotation> A[] getAssociatedAnnotations(Map<Class<? extends Annotation>, Annotation> paramMap, Class<?> paramClass, Class<A> paramClass1)
/*     */   {
/* 160 */     Objects.requireNonNull(paramClass);
/*     */     
/*     */ 
/* 163 */     Annotation[] arrayOfAnnotation = getDirectlyAndIndirectlyPresent(paramMap, paramClass1);
/*     */     
/*     */ 
/* 166 */     if (AnnotationType.getInstance(paramClass1).isInherited()) {
/* 167 */       Class localClass = paramClass.getSuperclass();
/* 168 */       while ((arrayOfAnnotation.length == 0) && (localClass != null)) {
/* 169 */         arrayOfAnnotation = getDirectlyAndIndirectlyPresent(LANG_ACCESS.getDeclaredAnnotationMap(localClass), paramClass1);
/* 170 */         localClass = localClass.getSuperclass();
/*     */       }
/*     */     }
/*     */     
/* 174 */     return arrayOfAnnotation;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static <A extends Annotation> A[] getValueArray(Annotation paramAnnotation)
/*     */   {
/*     */     try
/*     */     {
/* 187 */       Class localClass = paramAnnotation.annotationType();
/* 188 */       AnnotationType localAnnotationType = AnnotationType.getInstance(localClass);
/* 189 */       if (localAnnotationType == null) {
/* 190 */         throw invalidContainerException(paramAnnotation, null);
/*     */       }
/* 192 */       Method localMethod = (Method)localAnnotationType.members().get("value");
/* 193 */       if (localMethod == null) {
/* 194 */         throw invalidContainerException(paramAnnotation, null);
/*     */       }
/* 196 */       localMethod.setAccessible(true);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 201 */       return (Annotation[])localMethod.invoke(paramAnnotation, new Object[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|ClassCastException localIllegalAccessException)
/*     */     {
/*     */ 
/*     */ 
/* 210 */       throw invalidContainerException(paramAnnotation, localIllegalAccessException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static AnnotationFormatError invalidContainerException(Annotation paramAnnotation, Throwable paramThrowable)
/*     */   {
/* 218 */     return new AnnotationFormatError(paramAnnotation + " is an invalid container for repeating annotations", paramThrowable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static <A extends Annotation> void checkTypes(A[] paramArrayOfA, Annotation paramAnnotation, Class<A> paramClass)
/*     */   {
/* 230 */     for (A ? : paramArrayOfA) {
/* 231 */       if (!paramClass.isInstance(?))
/*     */       {
/* 233 */         throw new AnnotationFormatError(String.format("%s is an invalid container for repeating annotations of type: %s", new Object[] { paramAnnotation, paramClass }));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\annotation\AnnotationSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
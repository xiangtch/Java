/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.Inherited;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AnnotationType
/*     */ {
/*     */   private final Map<String, Class<?>> memberTypes;
/*     */   private final Map<String, Object> memberDefaults;
/*     */   private final Map<String, Method> members;
/*     */   private final RetentionPolicy retention;
/*     */   private final boolean inherited;
/*     */   
/*     */   public static AnnotationType getInstance(Class<? extends Annotation> paramClass)
/*     */   {
/*  82 */     JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
/*  83 */     AnnotationType localAnnotationType = localJavaLangAccess.getAnnotationType(paramClass);
/*  84 */     if (localAnnotationType == null) {
/*  85 */       localAnnotationType = new AnnotationType(paramClass);
/*     */       
/*  87 */       if (!localJavaLangAccess.casAnnotationType(paramClass, null, localAnnotationType))
/*     */       {
/*  89 */         localAnnotationType = localJavaLangAccess.getAnnotationType(paramClass);
/*  90 */         assert (localAnnotationType != null);
/*     */       }
/*     */     }
/*     */     
/*  94 */     return localAnnotationType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private AnnotationType(final Class<? extends Annotation> paramClass)
/*     */   {
/* 105 */     if (!paramClass.isAnnotation()) {
/* 106 */       throw new IllegalArgumentException("Not an annotation type");
/*     */     }
/*     */     
/* 109 */     Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Method[] run() {
/* 112 */         return paramClass.getDeclaredMethods();
/*     */       }
/*     */       
/* 115 */     });
/* 116 */     this.memberTypes = new HashMap(arrayOfMethod.length + 1, 1.0F);
/* 117 */     this.memberDefaults = new HashMap(0);
/* 118 */     this.members = new HashMap(arrayOfMethod.length + 1, 1.0F);
/*     */     
/* 120 */     for (Object localObject2 : arrayOfMethod) {
/* 121 */       if ((Modifier.isPublic(((Method)localObject2).getModifiers())) && 
/* 122 */         (Modifier.isAbstract(((Method)localObject2).getModifiers())) && 
/* 123 */         (!((Method)localObject2).isSynthetic())) {
/* 124 */         if (((Method)localObject2).getParameterTypes().length != 0) {
/* 125 */           throw new IllegalArgumentException(localObject2 + " has params");
/*     */         }
/* 127 */         String str = ((Method)localObject2).getName();
/* 128 */         Class localClass = ((Method)localObject2).getReturnType();
/* 129 */         this.memberTypes.put(str, invocationHandlerReturnType(localClass));
/* 130 */         this.members.put(str, localObject2);
/*     */         
/* 132 */         Object localObject3 = ((Method)localObject2).getDefaultValue();
/* 133 */         if (localObject3 != null) {
/* 134 */           this.memberDefaults.put(str, localObject3);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 141 */     if ((paramClass != Retention.class) && (paramClass != Inherited.class))
/*     */     {
/* 143 */       ??? = SharedSecrets.getJavaLangAccess();
/*     */       
/* 145 */       Map localMap = AnnotationParser.parseSelectAnnotations(((JavaLangAccess)???)
/* 146 */         .getRawClassAnnotations(paramClass), ((JavaLangAccess)???)
/* 147 */         .getConstantPool(paramClass), paramClass, new Class[] { Retention.class, Inherited.class });
/*     */       
/*     */ 
/*     */ 
/* 151 */       Retention localRetention = (Retention)localMap.get(Retention.class);
/* 152 */       this.retention = (localRetention == null ? RetentionPolicy.CLASS : localRetention.value());
/* 153 */       this.inherited = localMap.containsKey(Inherited.class);
/*     */     }
/*     */     else {
/* 156 */       this.retention = RetentionPolicy.RUNTIME;
/* 157 */       this.inherited = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Class<?> invocationHandlerReturnType(Class<?> paramClass)
/*     */   {
/* 169 */     if (paramClass == Byte.TYPE)
/* 170 */       return Byte.class;
/* 171 */     if (paramClass == Character.TYPE)
/* 172 */       return Character.class;
/* 173 */     if (paramClass == Double.TYPE)
/* 174 */       return Double.class;
/* 175 */     if (paramClass == Float.TYPE)
/* 176 */       return Float.class;
/* 177 */     if (paramClass == Integer.TYPE)
/* 178 */       return Integer.class;
/* 179 */     if (paramClass == Long.TYPE)
/* 180 */       return Long.class;
/* 181 */     if (paramClass == Short.TYPE)
/* 182 */       return Short.class;
/* 183 */     if (paramClass == Boolean.TYPE) {
/* 184 */       return Boolean.class;
/*     */     }
/*     */     
/* 187 */     return paramClass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, Class<?>> memberTypes()
/*     */   {
/* 195 */     return this.memberTypes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, Method> members()
/*     */   {
/* 203 */     return this.members;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, Object> memberDefaults()
/*     */   {
/* 211 */     return this.memberDefaults;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RetentionPolicy retention()
/*     */   {
/* 218 */     return this.retention;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInherited()
/*     */   {
/* 225 */     return this.inherited;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 232 */     return "Annotation Type:\n   Member types: " + this.memberTypes + "\n   Member defaults: " + this.memberDefaults + "\n   Retention policy: " + this.retention + "\n   Inherited: " + this.inherited;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\annotation\AnnotationType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
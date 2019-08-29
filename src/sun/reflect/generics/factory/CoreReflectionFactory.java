/*     */ package sun.reflect.generics.factory;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
/*     */ import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
/*     */ import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;
/*     */ import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;
/*     */ import sun.reflect.generics.scope.Scope;
/*     */ import sun.reflect.generics.tree.FieldTypeSignature;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CoreReflectionFactory
/*     */   implements GenericsFactory
/*     */ {
/*     */   private final GenericDeclaration decl;
/*     */   private final Scope scope;
/*     */   
/*     */   private CoreReflectionFactory(GenericDeclaration paramGenericDeclaration, Scope paramScope)
/*     */   {
/*  52 */     this.decl = paramGenericDeclaration;
/*  53 */     this.scope = paramScope;
/*     */   }
/*     */   
/*  56 */   private GenericDeclaration getDecl() { return this.decl; }
/*     */   
/*  58 */   private Scope getScope() { return this.scope; }
/*     */   
/*     */   private ClassLoader getDeclsLoader()
/*     */   {
/*  62 */     if ((this.decl instanceof Class)) return ((Class)this.decl).getClassLoader();
/*  63 */     if ((this.decl instanceof Method)) {
/*  64 */       return ((Method)this.decl).getDeclaringClass().getClassLoader();
/*     */     }
/*  66 */     assert ((this.decl instanceof Constructor)) : "Constructor expected";
/*  67 */     return ((Constructor)this.decl).getDeclaringClass().getClassLoader();
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
/*     */   public static CoreReflectionFactory make(GenericDeclaration paramGenericDeclaration, Scope paramScope)
/*     */   {
/*  89 */     return new CoreReflectionFactory(paramGenericDeclaration, paramScope);
/*     */   }
/*     */   
/*     */   public TypeVariable<?> makeTypeVariable(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature)
/*     */   {
/*  94 */     return TypeVariableImpl.make(getDecl(), paramString, paramArrayOfFieldTypeSignature, this);
/*     */   }
/*     */   
/*     */   public WildcardType makeWildcard(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2)
/*     */   {
/*  99 */     return WildcardTypeImpl.make(paramArrayOfFieldTypeSignature1, paramArrayOfFieldTypeSignature2, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public ParameterizedType makeParameterizedType(Type paramType1, Type[] paramArrayOfType, Type paramType2)
/*     */   {
/* 105 */     return ParameterizedTypeImpl.make((Class)paramType1, paramArrayOfType, paramType2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 110 */   public TypeVariable<?> findTypeVariable(String paramString) { return getScope().lookup(paramString); }
/*     */   
/*     */   public Type makeNamedType(String paramString) {
/*     */     try {
/* 114 */       return Class.forName(paramString, false, 
/* 115 */         getDeclsLoader());
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 117 */       throw new TypeNotPresentException(paramString, localClassNotFoundException);
/*     */     }
/*     */   }
/*     */   
/*     */   public Type makeArrayType(Type paramType) {
/* 122 */     if ((paramType instanceof Class)) {
/* 123 */       return Array.newInstance((Class)paramType, 0).getClass();
/*     */     }
/* 125 */     return GenericArrayTypeImpl.make(paramType);
/*     */   }
/*     */   
/* 128 */   public Type makeByte() { return Byte.TYPE; }
/* 129 */   public Type makeBool() { return Boolean.TYPE; }
/* 130 */   public Type makeShort() { return Short.TYPE; }
/* 131 */   public Type makeChar() { return Character.TYPE; }
/* 132 */   public Type makeInt() { return Integer.TYPE; }
/* 133 */   public Type makeLong() { return Long.TYPE; }
/* 134 */   public Type makeFloat() { return Float.TYPE; }
/* 135 */   public Type makeDouble() { return Double.TYPE; }
/*     */   
/* 137 */   public Type makeVoid() { return Void.TYPE; }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\factory\CoreReflectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.tracing.dtrace;
/*     */ 
/*     */ import com.sun.tracing.ProbeName;
/*     */ import com.sun.tracing.Provider;
/*     */ import com.sun.tracing.dtrace.Attributes;
/*     */ import com.sun.tracing.dtrace.DependencyClass;
/*     */ import com.sun.tracing.dtrace.FunctionName;
/*     */ import com.sun.tracing.dtrace.ModuleName;
/*     */ import com.sun.tracing.dtrace.StabilityLevel;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import sun.misc.ProxyGenerator;
/*     */ import sun.tracing.ProbeSkeleton;
/*     */ import sun.tracing.ProviderSkeleton;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class DTraceProvider
/*     */   extends ProviderSkeleton
/*     */ {
/*     */   private Activation activation;
/*     */   private Object proxy;
/*  53 */   private static final Class[] constructorParams = { InvocationHandler.class };
/*  54 */   private final String proxyClassNamePrefix = "$DTraceTracingProxy";
/*     */   
/*     */   static final String DEFAULT_MODULE = "java_tracing";
/*     */   
/*     */   static final String DEFAULT_FUNCTION = "unspecified";
/*  59 */   private static long nextUniqueNumber = 0L;
/*     */   
/*  61 */   private static synchronized long getUniqueNumber() { return nextUniqueNumber++; }
/*     */   
/*     */   protected ProbeSkeleton createProbe(Method paramMethod)
/*     */   {
/*  65 */     return new DTraceProbe(this.proxy, paramMethod);
/*     */   }
/*     */   
/*     */   DTraceProvider(Class<? extends Provider> paramClass) {
/*  69 */     super(paramClass);
/*     */   }
/*     */   
/*     */   void setProxy(Object paramObject) {
/*  73 */     this.proxy = paramObject;
/*     */   }
/*     */   
/*     */   void setActivation(Activation paramActivation) {
/*  77 */     this.activation = paramActivation;
/*     */   }
/*     */   
/*     */   public void dispose() {
/*  81 */     if (this.activation != null) {
/*  82 */       this.activation.disposeProvider(this);
/*  83 */       this.activation = null;
/*     */     }
/*  85 */     super.dispose();
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
/*     */   public <T extends Provider> T newProxyInstance()
/*     */   {
/* 102 */     long l = getUniqueNumber();
/*     */     
/* 104 */     String str1 = "";
/* 105 */     if (!Modifier.isPublic(this.providerType.getModifiers())) {
/* 106 */       str2 = this.providerType.getName();
/* 107 */       int i = str2.lastIndexOf('.');
/* 108 */       str1 = i == -1 ? "" : str2.substring(0, i + 1);
/*     */     }
/*     */     
/* 111 */     String str2 = str1 + "$DTraceTracingProxy" + l;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 116 */     Class localClass = null;
/* 117 */     byte[] arrayOfByte = ProxyGenerator.generateProxyClass(str2, new Class[] { this.providerType });
/*     */     try
/*     */     {
/* 120 */       localClass = JVM.defineClass(this.providerType
/* 121 */         .getClassLoader(), str2, arrayOfByte, 0, arrayOfByte.length);
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (ClassFormatError localClassFormatError)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 131 */       throw new IllegalArgumentException(localClassFormatError.toString());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 138 */       Constructor localConstructor = localClass.getConstructor(constructorParams);
/* 139 */       return (Provider)localConstructor.newInstance(new Object[] { this });
/*     */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 141 */       throw new InternalError(localReflectiveOperationException.toString(), localReflectiveOperationException);
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
/*     */   protected void triggerProbe(Method paramMethod, Object[] paramArrayOfObject)
/*     */   {
/* 155 */     if (!$assertionsDisabled) throw new AssertionError("This method should have been overridden by the JVM");
/*     */   }
/*     */   
/*     */   public String getProviderName() {
/* 159 */     return super.getProviderName();
/*     */   }
/*     */   
/*     */   String getModuleName() {
/* 163 */     return getAnnotationString(this.providerType, ModuleName.class, "java_tracing");
/*     */   }
/*     */   
/*     */   static String getProbeName(Method paramMethod)
/*     */   {
/* 168 */     return getAnnotationString(paramMethod, ProbeName.class, paramMethod
/* 169 */       .getName());
/*     */   }
/*     */   
/*     */   static String getFunctionName(Method paramMethod) {
/* 173 */     return getAnnotationString(paramMethod, FunctionName.class, "unspecified");
/*     */   }
/*     */   
/*     */   DTraceProbe[] getProbes()
/*     */   {
/* 178 */     return (DTraceProbe[])this.probes.values().toArray(new DTraceProbe[0]);
/*     */   }
/*     */   
/*     */   StabilityLevel getNameStabilityFor(Class<? extends Annotation> paramClass) {
/* 182 */     Attributes localAttributes = (Attributes)getAnnotationValue(this.providerType, paramClass, "value", null);
/*     */     
/* 184 */     if (localAttributes == null) {
/* 185 */       return StabilityLevel.PRIVATE;
/*     */     }
/* 187 */     return localAttributes.name();
/*     */   }
/*     */   
/*     */   StabilityLevel getDataStabilityFor(Class<? extends Annotation> paramClass)
/*     */   {
/* 192 */     Attributes localAttributes = (Attributes)getAnnotationValue(this.providerType, paramClass, "value", null);
/*     */     
/* 194 */     if (localAttributes == null) {
/* 195 */       return StabilityLevel.PRIVATE;
/*     */     }
/* 197 */     return localAttributes.data();
/*     */   }
/*     */   
/*     */   DependencyClass getDependencyClassFor(Class<? extends Annotation> paramClass)
/*     */   {
/* 202 */     Attributes localAttributes = (Attributes)getAnnotationValue(this.providerType, paramClass, "value", null);
/*     */     
/* 204 */     if (localAttributes == null) {
/* 205 */       return DependencyClass.UNKNOWN;
/*     */     }
/* 207 */     return localAttributes.dependency();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\dtrace\DTraceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
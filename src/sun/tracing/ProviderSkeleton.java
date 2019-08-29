/*     */ package sun.tracing;
/*     */ 
/*     */ import com.sun.tracing.Probe;
/*     */ import com.sun.tracing.Provider;
/*     */ import com.sun.tracing.ProviderName;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ProviderSkeleton
/*     */   implements InvocationHandler, Provider
/*     */ {
/*     */   protected boolean active;
/*     */   protected Class<? extends Provider> providerType;
/*     */   protected HashMap<Method, ProbeSkeleton> probes;
/*     */   
/*     */   protected abstract ProbeSkeleton createProbe(Method paramMethod);
/*     */   
/*     */   protected ProviderSkeleton(Class<? extends Provider> paramClass)
/*     */   {
/*  92 */     this.active = false;
/*  93 */     this.providerType = paramClass;
/*  94 */     this.probes = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init()
/*     */   {
/* 104 */     Method[] arrayOfMethod1 = (Method[])AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Method[] run() {
/* 106 */         return ProviderSkeleton.this.providerType.getDeclaredMethods();
/*     */       }
/*     */     });
/*     */     
/* 110 */     for (Method localMethod : arrayOfMethod1) {
/* 111 */       if (localMethod.getReturnType() != Void.TYPE) {
/* 112 */         throw new IllegalArgumentException("Return value of method is not void");
/*     */       }
/*     */       
/* 115 */       this.probes.put(localMethod, createProbe(localMethod));
/*     */     }
/*     */     
/* 118 */     this.active = true;
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
/*     */   public <T extends Provider> T newProxyInstance()
/*     */   {
/* 133 */     final ProviderSkeleton localProviderSkeleton = this;
/* 134 */     (Provider)AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public T run() {
/* 136 */         return (Provider)Proxy.newProxyInstance(ProviderSkeleton.this.providerType.getClassLoader(), new Class[] { ProviderSkeleton.this.providerType }, localProviderSkeleton);
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
/*     */   public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
/*     */   {
/* 157 */     Class localClass = paramMethod.getDeclaringClass();
/*     */     
/* 159 */     if (localClass != this.providerType)
/*     */     {
/*     */       try
/*     */       {
/* 163 */         if ((localClass == Provider.class) || (localClass == Object.class))
/*     */         {
/* 165 */           return paramMethod.invoke(this, paramArrayOfObject);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 170 */         throw new SecurityException();
/*     */       }
/*     */       catch (IllegalAccessException localIllegalAccessException) {
/* 173 */         if (!$assertionsDisabled) throw new AssertionError();
/*     */       } catch (InvocationTargetException localInvocationTargetException) {
/* 175 */         if (!$assertionsDisabled) throw new AssertionError();
/*     */       }
/*     */     }
/* 178 */     triggerProbe(paramMethod, paramArrayOfObject);
/*     */     
/* 180 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Probe getProbe(Method paramMethod)
/*     */   {
/* 190 */     return this.active ? (Probe)this.probes.get(paramMethod) : null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 199 */     this.active = false;
/* 200 */     this.probes.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getProviderName()
/*     */   {
/* 211 */     return getAnnotationString(this.providerType, ProviderName.class, this.providerType
/* 212 */       .getSimpleName());
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
/*     */   protected static String getAnnotationString(AnnotatedElement paramAnnotatedElement, Class<? extends Annotation> paramClass, String paramString)
/*     */   {
/* 228 */     String str = (String)getAnnotationValue(paramAnnotatedElement, paramClass, "value", paramString);
/*     */     
/* 230 */     return str.isEmpty() ? paramString : str;
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
/*     */   protected static Object getAnnotationValue(AnnotatedElement paramAnnotatedElement, Class<? extends Annotation> paramClass, String paramString, Object paramObject)
/*     */   {
/* 247 */     Object localObject = paramObject;
/*     */     try {
/* 249 */       Method localMethod = paramClass.getMethod(paramString, new Class[0]);
/* 250 */       Annotation localAnnotation = paramAnnotatedElement.getAnnotation(paramClass);
/* 251 */       localObject = localMethod.invoke(localAnnotation, new Object[0]);
/*     */     } catch (NoSuchMethodException localNoSuchMethodException) {
/* 253 */       if (!$assertionsDisabled) throw new AssertionError();
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 255 */       if (!$assertionsDisabled) throw new AssertionError();
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 257 */       if (!$assertionsDisabled) throw new AssertionError();
/*     */     } catch (NullPointerException localNullPointerException) {
/* 259 */       if (!$assertionsDisabled) throw new AssertionError();
/*     */     }
/* 261 */     return localObject;
/*     */   }
/*     */   
/*     */   protected void triggerProbe(Method paramMethod, Object[] paramArrayOfObject) {
/* 265 */     if (this.active) {
/* 266 */       ProbeSkeleton localProbeSkeleton = (ProbeSkeleton)this.probes.get(paramMethod);
/* 267 */       if (localProbeSkeleton != null)
/*     */       {
/* 269 */         localProbeSkeleton.uncheckedTrigger(paramArrayOfObject);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\ProviderSkeleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
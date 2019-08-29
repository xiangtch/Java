/*     */ package sun.tracing;
/*     */ 
/*     */ import com.sun.tracing.Probe;
/*     */ import com.sun.tracing.Provider;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class MultiplexProbe
/*     */   extends ProbeSkeleton
/*     */ {
/*     */   private Set<Probe> probes;
/*     */   
/*     */   MultiplexProbe(Method paramMethod, Set<Provider> paramSet)
/*     */   {
/*  98 */     super(paramMethod.getParameterTypes());
/*  99 */     this.probes = new HashSet();
/* 100 */     for (Provider localProvider : paramSet) {
/* 101 */       Probe localProbe = localProvider.getProbe(paramMethod);
/* 102 */       if (localProbe != null) {
/* 103 */         this.probes.add(localProbe);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isEnabled() {
/* 109 */     for (Probe localProbe : this.probes) {
/* 110 */       if (localProbe.isEnabled()) {
/* 111 */         return true;
/*     */       }
/*     */     }
/* 114 */     return false;
/*     */   }
/*     */   
/*     */   public void uncheckedTrigger(Object[] paramArrayOfObject) {
/* 118 */     for (Probe localProbe : this.probes) {
/*     */       try
/*     */       {
/* 121 */         ProbeSkeleton localProbeSkeleton = (ProbeSkeleton)localProbe;
/* 122 */         localProbeSkeleton.uncheckedTrigger(paramArrayOfObject);
/*     */       }
/*     */       catch (ClassCastException localClassCastException)
/*     */       {
/*     */         try {
/* 127 */           Method localMethod = Probe.class.getMethod("trigger", new Class[] {
/* 128 */             Class.forName("[java.lang.Object") });
/* 129 */           localMethod.invoke(localProbe, paramArrayOfObject);
/*     */         } catch (Exception localException) {
/* 131 */           if (!$assertionsDisabled) throw new AssertionError();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\MultiplexProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package sun.tracing.dtrace;
/*    */ 
/*    */ import java.security.Permission;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class Activation
/*    */ {
/*    */   private SystemResource resource;
/*    */   private int referenceCount;
/*    */   
/*    */   Activation(String paramString, DTraceProvider[] paramArrayOfDTraceProvider)
/*    */   {
/* 38 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 39 */     Object localObject1; if (localSecurityManager != null) {
/* 40 */       localObject1 = new RuntimePermission("com.sun.tracing.dtrace.createProvider");
/*    */       
/* 42 */       localSecurityManager.checkPermission((Permission)localObject1);
/*    */     }
/* 44 */     this.referenceCount = paramArrayOfDTraceProvider.length;
/* 45 */     for (Object localObject2 : paramArrayOfDTraceProvider) {
/* 46 */       ((DTraceProvider)localObject2).setActivation(this);
/*    */     }
/*    */     
/* 49 */     this.resource = new SystemResource(this, JVM.activate(paramString, paramArrayOfDTraceProvider));
/*    */   }
/*    */   
/*    */   void disposeProvider(DTraceProvider paramDTraceProvider) {
/* 53 */     if (--this.referenceCount == 0) {
/* 54 */       this.resource.dispose();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\dtrace\Activation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
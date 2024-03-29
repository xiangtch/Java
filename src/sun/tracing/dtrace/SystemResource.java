/*     */ package sun.tracing.dtrace;
/*     */ 
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.HashSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SystemResource
/*     */   extends WeakReference<Activation>
/*     */ {
/*     */   private long handle;
/*  81 */   private static ReferenceQueue<Activation> referenceQueue = referenceQueue = new ReferenceQueue();
/*     */   
/*  83 */   static HashSet<SystemResource> resources = new HashSet();
/*     */   
/*     */   SystemResource(Activation paramActivation, long paramLong) {
/*  86 */     super(paramActivation, referenceQueue);
/*  87 */     this.handle = paramLong;
/*  88 */     flush();
/*  89 */     resources.add(this);
/*     */   }
/*     */   
/*     */   void dispose() {
/*  93 */     JVM.dispose(this.handle);
/*  94 */     resources.remove(this);
/*  95 */     this.handle = 0L;
/*     */   }
/*     */   
/*     */   static void flush() {
/*  99 */     SystemResource localSystemResource = null;
/* 100 */     while ((localSystemResource = (SystemResource)referenceQueue.poll()) != null) {
/* 101 */       if (localSystemResource.handle != 0L) {
/* 102 */         localSystemResource.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\dtrace\SystemResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.misc;
/*     */ 
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.ProtectionDomain;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class InnocuousThread
/*     */   extends Thread
/*     */ {
/*     */   private static final Unsafe UNSAFE;
/*     */   private static final ThreadGroup THREADGROUP;
/*     */   private static final AccessControlContext ACC;
/*     */   private static final long THREADLOCALS;
/*     */   private static final long INHERITABLETHREADLOCALS;
/*     */   private static final long INHERITEDACCESSCONTROLCONTEXT;
/*     */   private volatile boolean hasRun;
/*     */   
/*     */   public InnocuousThread(Runnable paramRunnable)
/*     */   {
/*  46 */     super(THREADGROUP, paramRunnable, "anInnocuousThread");
/*  47 */     UNSAFE.putOrderedObject(this, INHERITEDACCESSCONTROLCONTEXT, ACC);
/*  48 */     eraseThreadLocals();
/*     */   }
/*     */   
/*     */ 
/*     */   public ClassLoader getContextClassLoader()
/*     */   {
/*  54 */     return ClassLoader.getSystemClassLoader();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUncaughtExceptionHandler(UncaughtExceptionHandler paramUncaughtExceptionHandler) {}
/*     */   
/*     */ 
/*     */   public void setContextClassLoader(ClassLoader paramClassLoader)
/*     */   {
/*  64 */     throw new SecurityException("setContextClassLoader");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void run()
/*     */   {
/*  72 */     if ((Thread.currentThread() == this) && (!this.hasRun)) {
/*  73 */       this.hasRun = true;
/*  74 */       super.run();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void eraseThreadLocals()
/*     */   {
/*  82 */     UNSAFE.putObject(this, THREADLOCALS, null);
/*  83 */     UNSAFE.putObject(this, INHERITABLETHREADLOCALS, null);
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*     */     try {
/*  89 */       ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  94 */       UNSAFE = Unsafe.getUnsafe();
/*  95 */       Class localClass1 = Thread.class;
/*  96 */       Class localClass2 = ThreadGroup.class;
/*     */       
/*     */ 
/*  99 */       THREADLOCALS = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("threadLocals"));
/*     */       
/* 101 */       INHERITABLETHREADLOCALS = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("inheritableThreadLocals"));
/*     */       
/* 103 */       INHERITEDACCESSCONTROLCONTEXT = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("inheritedAccessControlContext"));
/*     */       
/* 105 */       long l1 = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("group"));
/* 106 */       long l2 = UNSAFE.objectFieldOffset(localClass2.getDeclaredField("parent"));
/*     */       
/* 108 */       Object localObject = (ThreadGroup)UNSAFE.getObject(Thread.currentThread(), l1);
/*     */       
/* 110 */       while (localObject != null) {
/* 111 */         ThreadGroup localThreadGroup = (ThreadGroup)UNSAFE.getObject(localObject, l2);
/* 112 */         if (localThreadGroup == null)
/*     */           break;
/* 114 */         localObject = localThreadGroup;
/*     */       }
/* 116 */       THREADGROUP = new ThreadGroup((ThreadGroup)localObject, "InnocuousThreadGroup");
/*     */     } catch (Exception localException) {
/* 118 */       throw new Error(localException);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\InnocuousThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
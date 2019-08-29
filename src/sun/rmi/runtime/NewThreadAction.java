/*     */ package sun.rmi.runtime;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.security.util.SecurityConstants;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class NewThreadAction
/*     */   implements PrivilegedAction<Thread>
/*     */ {
/*  55 */   static final ThreadGroup systemThreadGroup = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public ThreadGroup run() {
/*  57 */       Object localObject = Thread.currentThread().getThreadGroup();
/*     */       ThreadGroup localThreadGroup;
/*  59 */       while ((localThreadGroup = ((ThreadGroup)localObject).getParent()) != null) {
/*  60 */         localObject = localThreadGroup;
/*     */       }
/*  62 */       return (ThreadGroup)localObject;
/*     */     }
/*  55 */   });
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  72 */   static final ThreadGroup userThreadGroup = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public ThreadGroup run() {
/*  74 */       return new ThreadGroup(NewThreadAction.systemThreadGroup, "RMI Runtime");
/*     */     }
/*  72 */   });
/*     */   
/*     */ 
/*     */   private final ThreadGroup group;
/*     */   
/*     */   private final Runnable runnable;
/*     */   
/*     */   private final String name;
/*     */   
/*     */   private final boolean daemon;
/*     */   
/*     */ 
/*     */   NewThreadAction(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, boolean paramBoolean)
/*     */   {
/*  86 */     this.group = paramThreadGroup;
/*  87 */     this.runnable = paramRunnable;
/*  88 */     this.name = paramString;
/*  89 */     this.daemon = paramBoolean;
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
/*     */   public NewThreadAction(Runnable paramRunnable, String paramString, boolean paramBoolean)
/*     */   {
/* 104 */     this(systemThreadGroup, paramRunnable, paramString, paramBoolean);
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
/*     */   public NewThreadAction(Runnable paramRunnable, String paramString, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 124 */     this(paramBoolean2 ? userThreadGroup : systemThreadGroup, paramRunnable, paramString, paramBoolean1);
/*     */   }
/*     */   
/*     */   public Thread run()
/*     */   {
/* 129 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 130 */     if (localSecurityManager != null) {
/* 131 */       localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
/*     */     }
/* 133 */     Thread localThread = new Thread(this.group, this.runnable, "RMI " + this.name);
/* 134 */     localThread.setContextClassLoader(ClassLoader.getSystemClassLoader());
/* 135 */     localThread.setDaemon(this.daemon);
/* 136 */     return localThread;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\runtime\NewThreadAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
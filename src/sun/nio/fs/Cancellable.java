/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class Cancellable
/*     */   implements Runnable
/*     */ {
/*  39 */   private static final Unsafe unsafe = ;
/*     */   
/*     */   private final long pollingAddress;
/*  42 */   private final Object lock = new Object();
/*     */   
/*     */   private boolean completed;
/*     */   private Throwable exception;
/*     */   
/*     */   protected Cancellable()
/*     */   {
/*  49 */     this.pollingAddress = unsafe.allocateMemory(4L);
/*  50 */     unsafe.putIntVolatile(null, this.pollingAddress, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected long addressToPollForCancel()
/*     */   {
/*  58 */     return this.pollingAddress;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int cancelValue()
/*     */   {
/*  67 */     return Integer.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   final void cancel()
/*     */   {
/*  75 */     synchronized (this.lock) {
/*  76 */       if (!this.completed) {
/*  77 */         unsafe.putIntVolatile(null, this.pollingAddress, cancelValue());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private Throwable exception()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 88	sun/nio/fs/Cancellable:lock	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 89	sun/nio/fs/Cancellable:exception	Ljava/lang/Throwable;
/*     */     //   11: aload_1
/*     */     //   12: monitorexit
/*     */     //   13: areturn
/*     */     //   14: astore_2
/*     */     //   15: aload_1
/*     */     //   16: monitorexit
/*     */     //   17: aload_2
/*     */     //   18: athrow
/*     */     // Line number table:
/*     */     //   Java source line #87	-> byte code offset #0
/*     */     //   Java source line #88	-> byte code offset #7
/*     */     //   Java source line #89	-> byte code offset #14
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	19	0	this	Cancellable
/*     */     //   5	11	1	Ljava/lang/Object;	Object
/*     */     //   14	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	13	14	finally
/*     */     //   14	17	14	finally
/*     */   }
/*     */   
/*     */   public final void run()
/*     */   {
/*     */     try
/*     */     {
/*  95 */       implRun();
/*     */     } catch (Throwable ???) {
/*  97 */       synchronized (this.lock) {
/*  98 */         this.exception = ((Throwable)???);
/*     */       }
/*     */     } finally {
/* 101 */       synchronized (this.lock) {
/* 102 */         this.completed = true;
/* 103 */         unsafe.freeMemory(this.pollingAddress);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract void implRun()
/*     */     throws Throwable;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void runInterruptibly(Cancellable paramCancellable)
/*     */     throws ExecutionException
/*     */   {
/* 120 */     Thread localThread = new Thread(paramCancellable);
/* 121 */     localThread.start();
/* 122 */     int i = 0;
/* 123 */     while (localThread.isAlive()) {
/*     */       try {
/* 125 */         localThread.join();
/*     */       } catch (InterruptedException localInterruptedException) {
/* 127 */         i = 1;
/* 128 */         paramCancellable.cancel();
/*     */       }
/*     */     }
/* 131 */     if (i != 0)
/* 132 */       Thread.currentThread().interrupt();
/* 133 */     Throwable localThrowable = paramCancellable.exception();
/* 134 */     if (localThrowable != null) {
/* 135 */       throw new ExecutionException(localThrowable);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\Cancellable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.misc;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GC
/*     */ {
/*     */   private static final long NO_TARGET = Long.MAX_VALUE;
/*  52 */   private static long latencyTarget = Long.MAX_VALUE;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  57 */   private static Thread daemon = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  64 */   private static Object lock = new LatencyLock(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static native long maxObjectInspectionAge();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class Daemon
/*     */     extends Thread
/*     */   {
/*     */     public void run()
/*     */     {
/*     */       for (;;)
/*     */       {
/*  91 */         synchronized (GC.lock)
/*     */         {
/*  93 */           long l1 = GC.latencyTarget;
/*  94 */           if (l1 == Long.MAX_VALUE)
/*     */           {
/*  96 */             GC.access$302(null);
/*  97 */             return;
/*     */           }
/*     */           
/* 100 */           long l2 = GC.maxObjectInspectionAge();
/* 101 */           if (l2 >= l1)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 109 */             System.gc();
/* 110 */             l2 = 0L;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 117 */             GC.lock.wait(l1 - l2);
/*     */           }
/*     */           catch (InterruptedException localInterruptedException) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     private Daemon(ThreadGroup paramThreadGroup)
/*     */     {
/* 126 */       super("GC Daemon");
/*     */     }
/*     */     
/*     */     public static void create()
/*     */     {
/* 131 */       PrivilegedAction local1 = new PrivilegedAction() {
/*     */         public Void run() {
/* 133 */           Object localObject1 = Thread.currentThread().getThreadGroup();
/* 134 */           for (Object localObject2 = localObject1; 
/* 135 */               localObject2 != null; 
/* 136 */               localObject2 = ((ThreadGroup)localObject1).getParent()) localObject1 = localObject2;
/* 137 */           localObject2 = new Daemon((ThreadGroup)localObject1, null);
/* 138 */           ((Daemon)localObject2).setDaemon(true);
/* 139 */           ((Daemon)localObject2).setPriority(2);
/* 140 */           ((Daemon)localObject2).start();
/* 141 */           GC.access$302((Thread)localObject2);
/* 142 */           return null;
/* 143 */         } };
/* 144 */       AccessController.doPrivileged(local1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void setLatencyTarget(long paramLong)
/*     */   {
/* 154 */     latencyTarget = paramLong;
/* 155 */     if (daemon == null)
/*     */     {
/* 157 */       Daemon.create();
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 162 */       lock.notify();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class LatencyLock {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class LatencyRequest
/*     */     implements Comparable<LatencyRequest>
/*     */   {
/* 177 */     private static long counter = 0L;
/*     */     
/*     */ 
/* 180 */     private static SortedSet<LatencyRequest> requests = null;
/*     */     private long latency;
/*     */     private long id;
/*     */     
/*     */     private static void adjustLatencyIfNeeded()
/*     */     {
/* 186 */       if ((requests == null) || (requests.isEmpty())) {
/* 187 */         if (GC.latencyTarget != Long.MAX_VALUE) {
/* 188 */           GC.setLatencyTarget(Long.MAX_VALUE);
/*     */         }
/*     */       } else {
/* 191 */         LatencyRequest localLatencyRequest = (LatencyRequest)requests.first();
/* 192 */         if (localLatencyRequest.latency != GC.latencyTarget) {
/* 193 */           GC.setLatencyTarget(localLatencyRequest.latency);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private LatencyRequest(long paramLong)
/*     */     {
/* 207 */       if (paramLong <= 0L) {
/* 208 */         throw new IllegalArgumentException("Non-positive latency: " + paramLong);
/*     */       }
/*     */       
/* 211 */       this.latency = paramLong;
/* 212 */       synchronized (GC.lock) {
/* 213 */         this.id = (++counter);
/* 214 */         if (requests == null) {
/* 215 */           requests = new TreeSet();
/*     */         }
/* 217 */         requests.add(this);
/* 218 */         adjustLatencyIfNeeded();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void cancel()
/*     */     {
/* 229 */       synchronized (GC.lock) {
/* 230 */         if (this.latency == Long.MAX_VALUE) {
/* 231 */           throw new IllegalStateException("Request already cancelled");
/*     */         }
/*     */         
/* 234 */         if (!requests.remove(this)) {
/* 235 */           throw new InternalError("Latency request " + this + " not found");
/*     */         }
/*     */         
/* 238 */         if (requests.isEmpty()) requests = null;
/* 239 */         this.latency = Long.MAX_VALUE;
/* 240 */         adjustLatencyIfNeeded();
/*     */       }
/*     */     }
/*     */     
/*     */     public int compareTo(LatencyRequest paramLatencyRequest) {
/* 245 */       long l = this.latency - paramLatencyRequest.latency;
/* 246 */       if (l == 0L) l = this.id - paramLatencyRequest.id;
/* 247 */       return l > 0L ? 1 : l < 0L ? -1 : 0;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 251 */       return LatencyRequest.class.getName() + "[" + this.latency + "," + this.id + "]";
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static LatencyRequest requestLatency(long paramLong)
/*     */   {
/* 271 */     return new LatencyRequest(paramLong, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long currentLatencyTarget()
/*     */   {
/* 280 */     long l = latencyTarget;
/* 281 */     return l == Long.MAX_VALUE ? 0L : l;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\GC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
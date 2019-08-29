/*     */ package sun.rmi.transport;
/*     */ 
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.rmi.NoSuchObjectException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.dgc.VMID;
/*     */ import java.rmi.server.ExportException;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import sun.misc.GC;
/*     */ import sun.misc.GC.LatencyRequest;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.runtime.NewThreadAction;
/*     */ import sun.security.action.GetLongAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ObjectTable
/*     */ {
/*  54 */   private static final long gcInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.server.gcInterval", 3600000L))).longValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  61 */   private static final Object tableLock = new Object();
/*     */   
/*     */ 
/*  64 */   private static final Map<ObjectEndpoint, Target> objTable = new HashMap();
/*     */   
/*  66 */   private static final Map<WeakRef, Target> implTable = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */   private static final Object keepAliveLock = new Object();
/*     */   
/*     */ 
/*  76 */   private static int keepAliveCount = 0;
/*     */   
/*     */ 
/*  79 */   private static Thread reaper = null;
/*     */   
/*     */ 
/*  82 */   static final ReferenceQueue<Object> reapQueue = new ReferenceQueue();
/*     */   
/*     */ 
/*  85 */   private static GC.LatencyRequest gcLatencyRequest = null;
/*     */   
/*     */   /* Error */
/*     */   static Target getTarget(ObjectEndpoint paramObjectEndpoint)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 220	sun/rmi/transport/ObjectTable:tableLock	Ljava/lang/Object;
/*     */     //   3: dup
/*     */     //   4: astore_1
/*     */     //   5: monitorenter
/*     */     //   6: getstatic 224	sun/rmi/transport/ObjectTable:objTable	Ljava/util/Map;
/*     */     //   9: aload_0
/*     */     //   10: invokeinterface 263 2 0
/*     */     //   15: checkcast 130	sun/rmi/transport/Target
/*     */     //   18: aload_1
/*     */     //   19: monitorexit
/*     */     //   20: areturn
/*     */     //   21: astore_2
/*     */     //   22: aload_1
/*     */     //   23: monitorexit
/*     */     //   24: aload_2
/*     */     //   25: athrow
/*     */     // Line number table:
/*     */     //   Java source line #96	-> byte code offset #0
/*     */     //   Java source line #97	-> byte code offset #6
/*     */     //   Java source line #98	-> byte code offset #21
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	26	0	paramObjectEndpoint	ObjectEndpoint
/*     */     //   4	19	1	Ljava/lang/Object;	Object
/*     */     //   21	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   6	20	21	finally
/*     */     //   21	24	21	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static Target getTarget(Remote paramRemote)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 220	sun/rmi/transport/ObjectTable:tableLock	Ljava/lang/Object;
/*     */     //   3: dup
/*     */     //   4: astore_1
/*     */     //   5: monitorenter
/*     */     //   6: getstatic 223	sun/rmi/transport/ObjectTable:implTable	Ljava/util/Map;
/*     */     //   9: new 132	sun/rmi/transport/WeakRef
/*     */     //   12: dup
/*     */     //   13: aload_0
/*     */     //   14: invokespecial 260	sun/rmi/transport/WeakRef:<init>	(Ljava/lang/Object;)V
/*     */     //   17: invokeinterface 263 2 0
/*     */     //   22: checkcast 130	sun/rmi/transport/Target
/*     */     //   25: aload_1
/*     */     //   26: monitorexit
/*     */     //   27: areturn
/*     */     //   28: astore_2
/*     */     //   29: aload_1
/*     */     //   30: monitorexit
/*     */     //   31: aload_2
/*     */     //   32: athrow
/*     */     // Line number table:
/*     */     //   Java source line #105	-> byte code offset #0
/*     */     //   Java source line #106	-> byte code offset #6
/*     */     //   Java source line #107	-> byte code offset #28
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	33	0	paramRemote	Remote
/*     */     //   4	26	1	Ljava/lang/Object;	Object
/*     */     //   28	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   6	27	28	finally
/*     */     //   28	31	28	finally
/*     */   }
/*     */   
/*     */   public static Remote getStub(Remote paramRemote)
/*     */     throws NoSuchObjectException
/*     */   {
/* 122 */     Target localTarget = getTarget(paramRemote);
/* 123 */     if (localTarget == null) {
/* 124 */       throw new NoSuchObjectException("object not exported");
/*     */     }
/* 126 */     return localTarget.getStub();
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
/*     */ 
/*     */   public static boolean unexportObject(Remote paramRemote, boolean paramBoolean)
/*     */     throws NoSuchObjectException
/*     */   {
/* 150 */     synchronized (tableLock) {
/* 151 */       Target localTarget = getTarget(paramRemote);
/* 152 */       if (localTarget == null) {
/* 153 */         throw new NoSuchObjectException("object not exported");
/*     */       }
/* 155 */       if (localTarget.unexport(paramBoolean)) {
/* 156 */         removeTarget(localTarget);
/* 157 */         return true;
/*     */       }
/* 159 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void putTarget(Target paramTarget)
/*     */     throws ExportException
/*     */   {
/* 171 */     ObjectEndpoint localObjectEndpoint = paramTarget.getObjectEndpoint();
/* 172 */     WeakRef localWeakRef = paramTarget.getWeakImpl();
/*     */     
/* 174 */     if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
/* 175 */       DGCImpl.dgcLog.log(Log.VERBOSE, "add object " + localObjectEndpoint);
/*     */     }
/*     */     
/* 178 */     synchronized (tableLock)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 184 */       if (paramTarget.getImpl() != null) {
/* 185 */         if (objTable.containsKey(localObjectEndpoint)) {
/* 186 */           throw new ExportException("internal error: ObjID already in use");
/*     */         }
/* 188 */         if (implTable.containsKey(localWeakRef)) {
/* 189 */           throw new ExportException("object already exported");
/*     */         }
/*     */         
/* 192 */         objTable.put(localObjectEndpoint, paramTarget);
/* 193 */         implTable.put(localWeakRef, paramTarget);
/*     */         
/* 195 */         if (!paramTarget.isPermanent()) {
/* 196 */           incrementKeepAliveCount();
/*     */         }
/*     */       }
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
/*     */   private static void removeTarget(Target paramTarget)
/*     */   {
/* 211 */     ObjectEndpoint localObjectEndpoint = paramTarget.getObjectEndpoint();
/* 212 */     WeakRef localWeakRef = paramTarget.getWeakImpl();
/*     */     
/* 214 */     if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
/* 215 */       DGCImpl.dgcLog.log(Log.VERBOSE, "remove object " + localObjectEndpoint);
/*     */     }
/*     */     
/* 218 */     objTable.remove(localObjectEndpoint);
/* 219 */     implTable.remove(localWeakRef);
/*     */     
/* 221 */     paramTarget.markRemoved();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void referenced(ObjID paramObjID, long paramLong, VMID paramVMID)
/*     */   {
/* 230 */     synchronized (tableLock)
/*     */     {
/* 232 */       ObjectEndpoint localObjectEndpoint = new ObjectEndpoint(paramObjID, Transport.currentTransport());
/* 233 */       Target localTarget = (Target)objTable.get(localObjectEndpoint);
/* 234 */       if (localTarget != null) {
/* 235 */         localTarget.referenced(paramLong, paramVMID);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void unreferenced(ObjID paramObjID, long paramLong, VMID paramVMID, boolean paramBoolean)
/*     */   {
/* 248 */     synchronized (tableLock)
/*     */     {
/* 250 */       ObjectEndpoint localObjectEndpoint = new ObjectEndpoint(paramObjID, Transport.currentTransport());
/* 251 */       Target localTarget = (Target)objTable.get(localObjectEndpoint);
/* 252 */       if (localTarget != null) {
/* 253 */         localTarget.unreferenced(paramLong, paramVMID, paramBoolean);
/*     */       }
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
/*     */ 
/*     */   static void incrementKeepAliveCount()
/*     */   {
/* 275 */     synchronized (keepAliveLock) {
/* 276 */       keepAliveCount += 1;
/*     */       
/* 278 */       if (reaper == null) {
/* 279 */         reaper = (Thread)AccessController.doPrivileged(new NewThreadAction(new Reaper(null), "Reaper", false));
/*     */         
/* 281 */         reaper.start();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 291 */       if (gcLatencyRequest == null) {
/* 292 */         gcLatencyRequest = GC.requestLatency(gcInterval);
/*     */       }
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
/*     */   static void decrementKeepAliveCount()
/*     */   {
/* 311 */     synchronized (keepAliveLock) {
/* 312 */       keepAliveCount -= 1;
/*     */       
/* 314 */       if (keepAliveCount == 0) {
/* 315 */         if (reaper == null) throw new AssertionError();
/* 316 */         AccessController.doPrivileged(new PrivilegedAction() {
/*     */           public Void run() {
/* 318 */             ObjectTable.reaper.interrupt();
/* 319 */             return null;
/*     */           }
/* 321 */         });
/* 322 */         reaper = null;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 329 */         gcLatencyRequest.cancel();
/* 330 */         gcLatencyRequest = null;
/*     */       }
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
/*     */   private static class Reaper
/*     */     implements Runnable
/*     */   {
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*     */         do
/*     */         {
/* 351 */           WeakRef localWeakRef = (WeakRef)ObjectTable.reapQueue.remove();
/*     */           
/* 353 */           synchronized (ObjectTable.tableLock) {
/* 354 */             Target localTarget = (Target)ObjectTable.implTable.get(localWeakRef);
/* 355 */             if (localTarget != null) {
/* 356 */               if (!localTarget.isEmpty()) {
/* 357 */                 throw new Error("object with known references collected");
/*     */               }
/* 359 */               if (localTarget.isPermanent()) {
/* 360 */                 throw new Error("permanent object collected");
/*     */               }
/* 362 */               ObjectTable.removeTarget(localTarget);
/*     */             }
/*     */           }
/* 365 */         } while (!Thread.interrupted());
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\ObjectTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
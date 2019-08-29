/*     */ package sun.rmi.transport;
/*     */ 
/*     */ import java.io.InvalidClassException;
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.net.SocketPermission;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.UnmarshalException;
/*     */ import java.rmi.dgc.DGC;
/*     */ import java.rmi.dgc.Lease;
/*     */ import java.rmi.dgc.VMID;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.Permissions;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import sun.misc.GC;
/*     */ import sun.misc.GC.LatencyRequest;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.runtime.NewThreadAction;
/*     */ import sun.rmi.server.UnicastRef;
/*     */ import sun.rmi.server.Util;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DGCClient
/*     */ {
/*  90 */   private static long nextSequenceNum = Long.MIN_VALUE;
/*     */   
/*     */ 
/*  93 */   private static VMID vmid = new VMID();
/*     */   
/*     */ 
/*     */ 
/*  97 */   private static final long leaseValue = ((Long)AccessController.doPrivileged(new GetLongAction("java.rmi.dgc.leaseValue", 600000L)))
/*     */   
/*  99 */     .longValue();
/*     */   
/*     */ 
/*     */ 
/* 103 */   private static final long cleanInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.cleanInterval", 180000L)))
/*     */   
/* 105 */     .longValue();
/*     */   
/*     */ 
/*     */ 
/* 109 */   private static final long gcInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.client.gcInterval", 3600000L)))
/*     */   
/* 111 */     .longValue();
/*     */   
/*     */ 
/*     */   private static final int dirtyFailureRetries = 5;
/*     */   
/*     */ 
/*     */   private static final int cleanFailureRetries = 5;
/*     */   
/*     */ 
/* 120 */   private static final ObjID[] emptyObjIDArray = new ObjID[0];
/*     */   
/*     */ 
/* 123 */   private static final ObjID dgcID = new ObjID(2);
/*     */   
/*     */ 
/*     */   private static final AccessControlContext SOCKET_ACC;
/*     */   
/*     */ 
/*     */   static
/*     */   {
/* 131 */     Permissions localPermissions = new Permissions();
/* 132 */     localPermissions.add(new SocketPermission("*", "connect,resolve"));
/* 133 */     ProtectionDomain[] arrayOfProtectionDomain = { new ProtectionDomain(null, localPermissions) };
/* 134 */     SOCKET_ACC = new AccessControlContext(arrayOfProtectionDomain);
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
/*     */   static void registerRefs(Endpoint paramEndpoint, List<LiveRef> paramList)
/*     */   {
/*     */     EndpointEntry localEndpointEntry;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     do
/*     */     {
/* 159 */       localEndpointEntry = EndpointEntry.lookup(paramEndpoint);
/* 160 */     } while (!localEndpointEntry.registerRefs(paramList));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static synchronized long getNextSequenceNum()
/*     */   {
/* 170 */     return nextSequenceNum++;
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
/*     */   private static long computeRenewTime(long paramLong1, long paramLong2)
/*     */   {
/* 183 */     return paramLong1 + paramLong2 / 2L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class EndpointEntry
/*     */   {
/*     */     private Endpoint endpoint;
/*     */     
/*     */ 
/*     */ 
/*     */     private DGC dgc;
/*     */     
/*     */ 
/*     */ 
/* 200 */     private Map<LiveRef, RefEntry> refTable = new HashMap(5);
/*     */     
/* 202 */     private Set<RefEntry> invalidRefs = new HashSet(5);
/*     */     
/*     */ 
/* 205 */     private boolean removed = false;
/*     */     
/*     */ 
/* 208 */     private long renewTime = Long.MAX_VALUE;
/*     */     
/* 210 */     private long expirationTime = Long.MIN_VALUE;
/*     */     
/* 212 */     private int dirtyFailures = 0;
/*     */     
/*     */ 
/*     */     private long dirtyFailureStartTime;
/*     */     
/*     */     private long dirtyFailureDuration;
/*     */     
/*     */     private Thread renewCleanThread;
/*     */     
/* 221 */     private boolean interruptible = false;
/*     */     
/*     */ 
/* 224 */     private ReferenceQueue<LiveRef> refQueue = new ReferenceQueue();
/*     */     
/* 226 */     private Set<CleanRequest> pendingCleans = new HashSet(5);
/*     */     
/*     */ 
/* 229 */     private static Map<Endpoint, EndpointEntry> endpointTable = new HashMap(5);
/*     */     
/* 231 */     private static GC.LatencyRequest gcLatencyRequest = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public static EndpointEntry lookup(Endpoint paramEndpoint)
/*     */     {
/* 238 */       synchronized (endpointTable) {
/* 239 */         EndpointEntry localEndpointEntry = (EndpointEntry)endpointTable.get(paramEndpoint);
/* 240 */         if (localEndpointEntry == null) {
/* 241 */           localEndpointEntry = new EndpointEntry(paramEndpoint);
/* 242 */           endpointTable.put(paramEndpoint, localEndpointEntry);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 250 */           if (gcLatencyRequest == null) {
/* 251 */             gcLatencyRequest = GC.requestLatency(DGCClient.gcInterval);
/*     */           }
/*     */         }
/* 254 */         return localEndpointEntry;
/*     */       }
/*     */     }
/*     */     
/*     */     private EndpointEntry(Endpoint paramEndpoint) {
/* 259 */       this.endpoint = paramEndpoint;
/*     */       try {
/* 261 */         LiveRef localLiveRef = new LiveRef(DGCClient.dgcID, paramEndpoint, false);
/* 262 */         this.dgc = ((DGC)Util.createProxy(DGCImpl.class, new UnicastRef(localLiveRef), true));
/*     */       }
/*     */       catch (RemoteException localRemoteException) {
/* 265 */         throw new Error("internal error creating DGC stub");
/*     */       }
/* 267 */       this.renewCleanThread = ((Thread)AccessController.doPrivileged(new NewThreadAction(new RenewCleanThread(null), "RenewClean-" + paramEndpoint, true)));
/*     */       
/*     */ 
/* 270 */       this.renewCleanThread.start();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean registerRefs(List<LiveRef> paramList)
/*     */     {
/* 285 */       assert (!Thread.holdsLock(this));
/*     */       
/* 287 */       HashSet localHashSet = null;
/*     */       
/*     */       long l;
/* 290 */       synchronized (this) {
/* 291 */         if (this.removed) {
/* 292 */           return false;
/*     */         }
/*     */         
/* 295 */         Iterator localIterator = paramList.iterator();
/* 296 */         while (localIterator.hasNext()) {
/* 297 */           LiveRef localLiveRef1 = (LiveRef)localIterator.next();
/* 298 */           assert (localLiveRef1.getEndpoint().equals(this.endpoint));
/*     */           
/* 300 */           RefEntry localRefEntry = (RefEntry)this.refTable.get(localLiveRef1);
/* 301 */           if (localRefEntry == null) {
/* 302 */             LiveRef localLiveRef2 = (LiveRef)localLiveRef1.clone();
/* 303 */             localRefEntry = new RefEntry(localLiveRef2);
/* 304 */             this.refTable.put(localLiveRef2, localRefEntry);
/* 305 */             if (localHashSet == null) {
/* 306 */               localHashSet = new HashSet(5);
/*     */             }
/* 308 */             localHashSet.add(localRefEntry);
/*     */           }
/*     */           
/* 311 */           localRefEntry.addInstanceToRefSet(localLiveRef1);
/*     */         }
/*     */         
/* 314 */         if (localHashSet == null) {
/* 315 */           return true;
/*     */         }
/*     */         
/* 318 */         localHashSet.addAll(this.invalidRefs);
/* 319 */         this.invalidRefs.clear();
/*     */         
/* 321 */         l = DGCClient.access$300();
/*     */       }
/*     */       
/* 324 */       makeDirtyCall(localHashSet, l);
/* 325 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void removeRefEntry(RefEntry paramRefEntry)
/*     */     {
/* 336 */       assert (Thread.holdsLock(this));
/* 337 */       assert (!this.removed);
/* 338 */       assert (this.refTable.containsKey(paramRefEntry.getRef()));
/*     */       
/* 340 */       this.refTable.remove(paramRefEntry.getRef());
/* 341 */       this.invalidRefs.remove(paramRefEntry);
/* 342 */       if (this.refTable.isEmpty()) {
/* 343 */         synchronized (endpointTable) {
/* 344 */           endpointTable.remove(this.endpoint);
/* 345 */           Transport localTransport = this.endpoint.getOutboundTransport();
/* 346 */           localTransport.free(this.endpoint);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 352 */           if (endpointTable.isEmpty()) {
/* 353 */             assert (gcLatencyRequest != null);
/* 354 */             gcLatencyRequest.cancel();
/* 355 */             gcLatencyRequest = null;
/*     */           }
/* 357 */           this.removed = true;
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
/*     */     private void makeDirtyCall(Set<RefEntry> paramSet, long paramLong)
/*     */     {
/* 370 */       assert (!Thread.holdsLock(this));
/*     */       
/*     */       ObjID[] arrayOfObjID;
/* 373 */       if (paramSet != null) {
/* 374 */         arrayOfObjID = createObjIDArray(paramSet);
/*     */       } else {
/* 376 */         arrayOfObjID = DGCClient.emptyObjIDArray;
/*     */       }
/*     */       
/* 379 */       long l1 = System.currentTimeMillis();
/*     */       try
/*     */       {
/* 382 */         Lease localLease = this.dgc.dirty(arrayOfObjID, paramLong, new Lease(DGCClient.vmid, DGCClient.leaseValue));
/* 383 */         l2 = localLease.getValue();
/*     */         
/* 385 */         long l3 = DGCClient.computeRenewTime(l1, l2);
/* 386 */         l4 = l1 + l2;
/*     */         
/* 388 */         synchronized (this) {
/* 389 */           this.dirtyFailures = 0;
/* 390 */           setRenewTime(l3);
/* 391 */           this.expirationTime = l4;
/*     */         }
/*     */       } catch (Exception localException) {
/*     */         long l4;
/* 395 */         long l2 = System.currentTimeMillis();
/*     */         
/* 397 */         synchronized (this) {
/* 398 */           this.dirtyFailures += 1;
/*     */           
/* 400 */           if (((localException instanceof UnmarshalException)) && 
/* 401 */             ((localException.getCause() instanceof InvalidClassException))) {
/* 402 */             DGCImpl.dgcLog.log(Log.BRIEF, "InvalidClassException exception in DGC dirty call", localException);
/* 403 */             return;
/*     */           }
/*     */           
/* 406 */           if (this.dirtyFailures == 1)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 414 */             this.dirtyFailureStartTime = l1;
/* 415 */             this.dirtyFailureDuration = (l2 - l1);
/* 416 */             setRenewTime(l2);
/*     */ 
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/* 423 */             int i = this.dirtyFailures - 2;
/* 424 */             if (i == 0)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 432 */               this.dirtyFailureDuration = Math.max(this.dirtyFailureDuration + (l2 - l1) >> 1, 1000L);
/*     */             }
/*     */             
/* 435 */             l4 = l2 + (this.dirtyFailureDuration << i);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 444 */             if ((l4 < this.expirationTime) || (this.dirtyFailures < 5) || 
/*     */             
/* 446 */               (l4 < this.dirtyFailureStartTime + DGCClient.leaseValue))
/*     */             {
/* 448 */               setRenewTime(l4);
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 454 */               setRenewTime(Long.MAX_VALUE);
/*     */             }
/*     */           }
/*     */           
/* 458 */           if (paramSet != null)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 466 */             this.invalidRefs.addAll(paramSet);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 473 */             Iterator localIterator = paramSet.iterator();
/* 474 */             while (localIterator.hasNext()) {
/* 475 */               RefEntry localRefEntry = (RefEntry)localIterator.next();
/* 476 */               localRefEntry.markDirtyFailed();
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 484 */           if (this.renewTime >= this.expirationTime) {
/* 485 */             this.invalidRefs.addAll(this.refTable.values());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void setRenewTime(long paramLong)
/*     */     {
/* 498 */       assert (Thread.holdsLock(this));
/*     */       
/* 500 */       if (paramLong < this.renewTime) {
/* 501 */         this.renewTime = paramLong;
/* 502 */         if (this.interruptible) {
/* 503 */           AccessController.doPrivileged(new PrivilegedAction()
/*     */           {
/*     */             public Void run() {
/* 506 */               EndpointEntry.this.renewCleanThread.interrupt();
/* 507 */               return null;
/*     */             }
/*     */           });
/*     */         }
/*     */       } else {
/* 512 */         this.renewTime = paramLong;
/*     */       }
/*     */     }
/*     */     
/*     */     private class RenewCleanThread
/*     */       implements Runnable
/*     */     {
/*     */       private RenewCleanThread() {}
/*     */       
/*     */       public void run()
/*     */       {
/*     */         do
/*     */         {
/* 525 */           RefEntry.PhantomLiveRef localPhantomLiveRef = null;
/* 526 */           Object localObject1 = 0;
/* 527 */           Set localSet1 = null;
/* 528 */           long l2 = Long.MIN_VALUE;
/*     */           long l3;
/* 530 */           long l1; synchronized (EndpointEntry.this)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 542 */             l3 = EndpointEntry.this.renewTime - System.currentTimeMillis();
/* 543 */             l1 = Math.max(l3, 1L);
/* 544 */             if (!EndpointEntry.this.pendingCleans.isEmpty()) {
/* 545 */               l1 = Math.min(l1, DGCClient.cleanInterval);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 554 */             EndpointEntry.this.interruptible = true;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 563 */             localPhantomLiveRef = (RefEntry.PhantomLiveRef)EndpointEntry.this.refQueue.remove(l1);
/*     */           }
/*     */           catch (InterruptedException ???) {}
/*     */           
/* 567 */           synchronized (EndpointEntry.this)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 574 */             EndpointEntry.this.interruptible = false;
/* 575 */             Thread.interrupted();
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 582 */             if (localPhantomLiveRef != null) {
/* 583 */               EndpointEntry.this.processPhantomRefs(localPhantomLiveRef);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 589 */             l3 = System.currentTimeMillis();
/* 590 */             if (l3 > EndpointEntry.this.renewTime) {
/* 591 */               localObject1 = 1;
/* 592 */               if (!EndpointEntry.this.invalidRefs.isEmpty()) {
/* 593 */                 localSet1 = EndpointEntry.this.invalidRefs;
/* 594 */                 EndpointEntry.this.invalidRefs = new HashSet(5);
/*     */               }
/* 596 */               l2 = DGCClient.access$300();
/*     */             }
/*     */           }
/*     */           
/* 600 */           ??? = localObject1;
/* 601 */           final Set localSet2 = localSet1;
/* 602 */           final long l4 = l2;
/* 603 */           AccessController.doPrivileged(new PrivilegedAction() {
/*     */             public Void run() {
/* 605 */               if (Ljava/lang/Object;) {
/* 606 */                 EndpointEntry.this.makeDirtyCall(localSet2, l4);
/*     */               }
/*     */               
/* 609 */               if (!EndpointEntry.this.pendingCleans.isEmpty()) {
/* 610 */                 EndpointEntry.this.makeCleanCalls();
/*     */               }
/* 612 */               return null;
/* 613 */             } }, DGCClient.SOCKET_ACC);
/* 614 */         } while ((!EndpointEntry.this.removed) || (!EndpointEntry.this.pendingCleans.isEmpty()));
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
/*     */ 
/*     */ 
/*     */     private void processPhantomRefs(RefEntry.PhantomLiveRef paramPhantomLiveRef)
/*     */     {
/* 629 */       assert (Thread.holdsLock(this));
/*     */       
/* 631 */       HashSet localHashSet1 = null;
/* 632 */       HashSet localHashSet2 = null;
/*     */       do
/*     */       {
/* 635 */         RefEntry localRefEntry = paramPhantomLiveRef.getRefEntry();
/* 636 */         localRefEntry.removeInstanceFromRefSet(paramPhantomLiveRef);
/* 637 */         if (localRefEntry.isRefSetEmpty()) {
/* 638 */           if (localRefEntry.hasDirtyFailed()) {
/* 639 */             if (localHashSet1 == null) {
/* 640 */               localHashSet1 = new HashSet(5);
/*     */             }
/* 642 */             localHashSet1.add(localRefEntry);
/*     */           } else {
/* 644 */             if (localHashSet2 == null) {
/* 645 */               localHashSet2 = new HashSet(5);
/*     */             }
/* 647 */             localHashSet2.add(localRefEntry);
/*     */           }
/* 649 */           removeRefEntry(localRefEntry);
/*     */         }
/*     */         
/* 652 */       } while ((paramPhantomLiveRef = (RefEntry.PhantomLiveRef)this.refQueue.poll()) != null);
/*     */       
/* 654 */       if (localHashSet1 != null) {
/* 655 */         this.pendingCleans.add(new CleanRequest(
/* 656 */           createObjIDArray(localHashSet1), 
/* 657 */           DGCClient.access$300(), true));
/*     */       }
/* 659 */       if (localHashSet2 != null) {
/* 660 */         this.pendingCleans.add(new CleanRequest(
/* 661 */           createObjIDArray(localHashSet2), 
/* 662 */           DGCClient.access$300(), false));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static class CleanRequest
/*     */     {
/*     */       final ObjID[] objIDs;
/*     */       
/*     */ 
/*     */       final long sequenceNum;
/*     */       
/*     */       final boolean strong;
/*     */       
/* 677 */       int failures = 0;
/*     */       
/*     */       CleanRequest(ObjID[] paramArrayOfObjID, long paramLong, boolean paramBoolean) {
/* 680 */         this.objIDs = paramArrayOfObjID;
/* 681 */         this.sequenceNum = paramLong;
/* 682 */         this.strong = paramBoolean;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void makeCleanCalls()
/*     */     {
/* 694 */       assert (!Thread.holdsLock(this));
/*     */       
/* 696 */       Iterator localIterator = this.pendingCleans.iterator();
/* 697 */       while (localIterator.hasNext()) {
/* 698 */         CleanRequest localCleanRequest = (CleanRequest)localIterator.next();
/*     */         try {
/* 700 */           this.dgc.clean(localCleanRequest.objIDs, localCleanRequest.sequenceNum, DGCClient.vmid, localCleanRequest.strong);
/*     */           
/* 702 */           localIterator.remove();
/*     */ 
/*     */ 
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/*     */ 
/* 709 */           if (++localCleanRequest.failures >= 5) {
/* 710 */             localIterator.remove();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private static ObjID[] createObjIDArray(Set<RefEntry> paramSet)
/*     */     {
/* 721 */       ObjID[] arrayOfObjID = new ObjID[paramSet.size()];
/* 722 */       Iterator localIterator = paramSet.iterator();
/* 723 */       for (int i = 0; i < arrayOfObjID.length; i++) {
/* 724 */         arrayOfObjID[i] = ((RefEntry)localIterator.next()).getRef().getObjID();
/*     */       }
/* 726 */       return arrayOfObjID;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private class RefEntry
/*     */     {
/*     */       private LiveRef ref;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 741 */       private Set<PhantomLiveRef> refSet = new HashSet(5);
/*     */       
/* 743 */       private boolean dirtyFailed = false;
/*     */       
/*     */       public RefEntry(LiveRef paramLiveRef) {
/* 746 */         this.ref = paramLiveRef;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public LiveRef getRef()
/*     */       {
/* 754 */         return this.ref;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void addInstanceToRefSet(LiveRef paramLiveRef)
/*     */       {
/* 764 */         assert (Thread.holdsLock(EndpointEntry.this));
/* 765 */         assert (paramLiveRef.equals(this.ref));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 772 */         this.refSet.add(new PhantomLiveRef(paramLiveRef));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void removeInstanceFromRefSet(PhantomLiveRef paramPhantomLiveRef)
/*     */       {
/* 782 */         assert (Thread.holdsLock(EndpointEntry.this));
/* 783 */         assert (this.refSet.contains(paramPhantomLiveRef));
/* 784 */         this.refSet.remove(paramPhantomLiveRef);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean isRefSetEmpty()
/*     */       {
/* 795 */         assert (Thread.holdsLock(EndpointEntry.this));
/* 796 */         return this.refSet.size() == 0;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void markDirtyFailed()
/*     */       {
/* 807 */         assert (Thread.holdsLock(EndpointEntry.this));
/* 808 */         this.dirtyFailed = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean hasDirtyFailed()
/*     */       {
/* 820 */         assert (Thread.holdsLock(EndpointEntry.this));
/* 821 */         return this.dirtyFailed;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       private class PhantomLiveRef
/*     */         extends PhantomReference<LiveRef>
/*     */       {
/*     */         public PhantomLiveRef(LiveRef paramLiveRef)
/*     */         {
/* 832 */           super(EndpointEntry.this.refQueue);
/*     */         }
/*     */         
/*     */         public RefEntry getRefEntry() {
/* 836 */           return RefEntry.this;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\DGCClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class WObjectPeer
/*     */ {
/*     */   volatile long pData;
/*     */   private volatile boolean destroyed;
/*     */   volatile Object target;
/*     */   private volatile boolean disposed;
/*  46 */   volatile Error createError = null;
/*     */   
/*     */ 
/*  49 */   private final Object stateLock = new Object();
/*     */   private volatile Map<WObjectPeer, WObjectPeer> childPeers;
/*     */   
/*     */   public static WObjectPeer getPeerForTarget(Object paramObject)
/*     */   {
/*  54 */     WObjectPeer localWObjectPeer = (WObjectPeer)WToolkit.targetToPeer(paramObject);
/*  55 */     return localWObjectPeer;
/*     */   }
/*     */   
/*     */   public long getData() {
/*  59 */     return this.pData;
/*     */   }
/*     */   
/*     */   public Object getTarget() {
/*  63 */     return this.target;
/*     */   }
/*     */   
/*     */   public final Object getStateLock() {
/*  67 */     return this.stateLock;
/*     */   }
/*     */   
/*     */ 
/*     */   protected abstract void disposeImpl();
/*     */   
/*     */ 
/*     */   public final void dispose()
/*     */   {
/*  76 */     int i = 0;
/*     */     
/*  78 */     synchronized (this) {
/*  79 */       if (!this.disposed) {
/*  80 */         this.disposed = (i = 1);
/*     */       }
/*     */     }
/*     */     
/*  84 */     if (i != 0) {
/*  85 */       if (this.childPeers != null) {
/*  86 */         disposeChildPeers();
/*     */       }
/*  88 */       disposeImpl();
/*     */     }
/*     */   }
/*     */   
/*  92 */   protected final boolean isDisposed() { return this.disposed; }
/*     */   
/*     */ 
/*     */ 
/*     */   private static native void initIDs();
/*     */   
/*     */ 
/*     */ 
/*     */   final void addChildPeer(WObjectPeer paramWObjectPeer)
/*     */   {
/* 102 */     synchronized (getStateLock()) {
/* 103 */       if (this.childPeers == null) {
/* 104 */         this.childPeers = new WeakHashMap();
/*     */       }
/* 106 */       if (isDisposed()) {
/* 107 */         throw new IllegalStateException("Parent peer is disposed");
/*     */       }
/* 109 */       this.childPeers.put(paramWObjectPeer, this);
/*     */     }
/*     */   }
/*     */   
/*     */   private void disposeChildPeers()
/*     */   {
/* 115 */     synchronized (getStateLock()) {
/* 116 */       for (WObjectPeer localWObjectPeer : this.childPeers.keySet()) {
/* 117 */         if (localWObjectPeer != null) {
/*     */           try {
/* 119 */             localWObjectPeer.dispose();
/*     */           }
/*     */           catch (Exception localException) {}
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WObjectPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
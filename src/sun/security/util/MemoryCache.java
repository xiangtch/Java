/*     */ package sun.security.util;
/*     */ 
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class MemoryCache<K, V>
/*     */   extends Cache<K, V>
/*     */ {
/*     */   private static final float LOAD_FACTOR = 0.75F;
/*     */   private static final boolean DEBUG = false;
/*     */   private final Map<K, CacheEntry<K, V>> cacheMap;
/*     */   private int maxSize;
/*     */   private long lifetime;
/*     */   private final ReferenceQueue<V> queue;
/*     */   
/*     */   public MemoryCache(boolean paramBoolean, int paramInt)
/*     */   {
/* 261 */     this(paramBoolean, paramInt, 0);
/*     */   }
/*     */   
/*     */   public MemoryCache(boolean paramBoolean, int paramInt1, int paramInt2) {
/* 265 */     this.maxSize = paramInt1;
/* 266 */     this.lifetime = (paramInt2 * 1000);
/* 267 */     if (paramBoolean) {
/* 268 */       this.queue = new ReferenceQueue();
/*     */     } else {
/* 270 */       this.queue = null;
/*     */     }
/* 272 */     int i = (int)(paramInt1 / 0.75F) + 1;
/* 273 */     this.cacheMap = new LinkedHashMap(i, 0.75F, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void emptyQueue()
/*     */   {
/* 284 */     if (this.queue == null) {
/* 285 */       return;
/*     */     }
/* 287 */     int i = this.cacheMap.size();
/*     */     for (;;)
/*     */     {
/* 290 */       CacheEntry localCacheEntry1 = (CacheEntry)this.queue.poll();
/* 291 */       if (localCacheEntry1 == null) {
/*     */         break;
/*     */       }
/* 294 */       Object localObject = localCacheEntry1.getKey();
/* 295 */       if (localObject != null)
/*     */       {
/*     */ 
/*     */ 
/* 299 */         CacheEntry localCacheEntry2 = (CacheEntry)this.cacheMap.remove(localObject);
/*     */         
/*     */ 
/* 302 */         if ((localCacheEntry2 != null) && (localCacheEntry1 != localCacheEntry2)) {
/* 303 */           this.cacheMap.put(localObject, localCacheEntry2);
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
/*     */ 
/*     */   private void expungeExpiredEntries()
/*     */   {
/* 319 */     emptyQueue();
/* 320 */     if (this.lifetime == 0L) {
/* 321 */       return;
/*     */     }
/* 323 */     int i = 0;
/* 324 */     long l = System.currentTimeMillis();
/* 325 */     Iterator localIterator = this.cacheMap.values().iterator();
/* 326 */     while (localIterator.hasNext()) {
/* 327 */       CacheEntry localCacheEntry = (CacheEntry)localIterator.next();
/* 328 */       if (!localCacheEntry.isValid(l)) {
/* 329 */         localIterator.remove();
/* 330 */         i++;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized int size()
/*     */   {
/* 342 */     expungeExpiredEntries();
/* 343 */     return this.cacheMap.size();
/*     */   }
/*     */   
/*     */   public synchronized void clear() {
/* 347 */     if (this.queue != null)
/*     */     {
/*     */ 
/* 350 */       for (CacheEntry localCacheEntry : this.cacheMap.values()) {
/* 351 */         localCacheEntry.invalidate();
/*     */       }
/* 353 */       while (this.queue.poll() != null) {}
/*     */     }
/*     */     
/*     */ 
/* 357 */     this.cacheMap.clear();
/*     */   }
/*     */   
/*     */   public synchronized void put(K paramK, V paramV) {
/* 361 */     emptyQueue();
/*     */     
/* 363 */     long l = this.lifetime == 0L ? 0L : System.currentTimeMillis() + this.lifetime;
/* 364 */     CacheEntry localCacheEntry1 = newEntry(paramK, paramV, l, this.queue);
/* 365 */     CacheEntry localCacheEntry2 = (CacheEntry)this.cacheMap.put(paramK, localCacheEntry1);
/* 366 */     if (localCacheEntry2 != null) {
/* 367 */       localCacheEntry2.invalidate();
/* 368 */       return;
/*     */     }
/* 370 */     if ((this.maxSize > 0) && (this.cacheMap.size() > this.maxSize)) {
/* 371 */       expungeExpiredEntries();
/* 372 */       if (this.cacheMap.size() > this.maxSize) {
/* 373 */         Iterator localIterator = this.cacheMap.values().iterator();
/* 374 */         CacheEntry localCacheEntry3 = (CacheEntry)localIterator.next();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 379 */         localIterator.remove();
/* 380 */         localCacheEntry3.invalidate();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized V get(Object paramObject) {
/* 386 */     emptyQueue();
/* 387 */     CacheEntry localCacheEntry = (CacheEntry)this.cacheMap.get(paramObject);
/* 388 */     if (localCacheEntry == null) {
/* 389 */       return null;
/*     */     }
/* 391 */     long l = this.lifetime == 0L ? 0L : System.currentTimeMillis();
/* 392 */     if (!localCacheEntry.isValid(l))
/*     */     {
/*     */ 
/*     */ 
/* 396 */       this.cacheMap.remove(paramObject);
/* 397 */       return null;
/*     */     }
/* 399 */     return (V)localCacheEntry.getValue();
/*     */   }
/*     */   
/*     */   public synchronized void remove(Object paramObject) {
/* 403 */     emptyQueue();
/* 404 */     CacheEntry localCacheEntry = (CacheEntry)this.cacheMap.remove(paramObject);
/* 405 */     if (localCacheEntry != null) {
/* 406 */       localCacheEntry.invalidate();
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void setCapacity(int paramInt) {
/* 411 */     expungeExpiredEntries();
/* 412 */     if ((paramInt > 0) && (this.cacheMap.size() > paramInt)) {
/* 413 */       Iterator localIterator = this.cacheMap.values().iterator();
/* 414 */       for (int i = this.cacheMap.size() - paramInt; i > 0; i--) {
/* 415 */         CacheEntry localCacheEntry = (CacheEntry)localIterator.next();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 420 */         localIterator.remove();
/* 421 */         localCacheEntry.invalidate();
/*     */       }
/*     */     }
/*     */     
/* 425 */     this.maxSize = (paramInt > 0 ? paramInt : 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void setTimeout(int paramInt)
/*     */   {
/* 433 */     emptyQueue();
/* 434 */     this.lifetime = (paramInt > 0 ? paramInt * 1000L : 0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void accept(CacheVisitor<K, V> paramCacheVisitor)
/*     */   {
/* 443 */     expungeExpiredEntries();
/* 444 */     Map localMap = getCachedEntries();
/*     */     
/* 446 */     paramCacheVisitor.visit(localMap);
/*     */   }
/*     */   
/*     */   private Map<K, V> getCachedEntries() {
/* 450 */     HashMap localHashMap = new HashMap(this.cacheMap.size());
/*     */     
/* 452 */     for (CacheEntry localCacheEntry : this.cacheMap.values()) {
/* 453 */       localHashMap.put(localCacheEntry.getKey(), localCacheEntry.getValue());
/*     */     }
/*     */     
/* 456 */     return localHashMap;
/*     */   }
/*     */   
/*     */   protected CacheEntry<K, V> newEntry(K paramK, V paramV, long paramLong, ReferenceQueue<V> paramReferenceQueue)
/*     */   {
/* 461 */     if (paramReferenceQueue != null) {
/* 462 */       return new SoftCacheEntry(paramK, paramV, paramLong, paramReferenceQueue);
/*     */     }
/* 464 */     return new HardCacheEntry(paramK, paramV, paramLong);
/*     */   }
/*     */   
/*     */   private static abstract interface CacheEntry<K, V>
/*     */   {
/*     */     public abstract boolean isValid(long paramLong);
/*     */     
/*     */     public abstract void invalidate();
/*     */     
/*     */     public abstract K getKey();
/*     */     
/*     */     public abstract V getValue();
/*     */   }
/*     */   
/*     */   private static class HardCacheEntry<K, V>
/*     */     implements CacheEntry<K, V>
/*     */   {
/*     */     private K key;
/*     */     private V value;
/*     */     private long expirationTime;
/*     */     
/*     */     HardCacheEntry(K paramK, V paramV, long paramLong)
/*     */     {
/* 487 */       this.key = paramK;
/* 488 */       this.value = paramV;
/* 489 */       this.expirationTime = paramLong;
/*     */     }
/*     */     
/*     */     public K getKey() {
/* 493 */       return (K)this.key;
/*     */     }
/*     */     
/*     */     public V getValue() {
/* 497 */       return (V)this.value;
/*     */     }
/*     */     
/*     */     public boolean isValid(long paramLong) {
/* 501 */       boolean bool = paramLong <= this.expirationTime;
/* 502 */       if (!bool) {
/* 503 */         invalidate();
/*     */       }
/* 505 */       return bool;
/*     */     }
/*     */     
/*     */     public void invalidate() {
/* 509 */       this.key = null;
/* 510 */       this.value = null;
/* 511 */       this.expirationTime = -1L;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SoftCacheEntry<K, V>
/*     */     extends SoftReference<V>
/*     */     implements CacheEntry<K, V>
/*     */   {
/*     */     private K key;
/*     */     private long expirationTime;
/*     */     
/*     */     SoftCacheEntry(K paramK, V paramV, long paramLong, ReferenceQueue<V> paramReferenceQueue)
/*     */     {
/* 524 */       super(paramReferenceQueue);
/* 525 */       this.key = paramK;
/* 526 */       this.expirationTime = paramLong;
/*     */     }
/*     */     
/*     */     public K getKey() {
/* 530 */       return (K)this.key;
/*     */     }
/*     */     
/*     */     public V getValue() {
/* 534 */       return (V)get();
/*     */     }
/*     */     
/*     */     public boolean isValid(long paramLong) {
/* 538 */       boolean bool = (paramLong <= this.expirationTime) && (get() != null);
/* 539 */       if (!bool) {
/* 540 */         invalidate();
/*     */       }
/* 542 */       return bool;
/*     */     }
/*     */     
/*     */     public void invalidate() {
/* 546 */       clear();
/* 547 */       this.key = null;
/* 548 */       this.expirationTime = -1L;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\MemoryCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
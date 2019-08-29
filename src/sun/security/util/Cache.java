/*     */ package sun.security.util;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ public abstract class Cache<K, V>
/*     */ {
/*     */   public abstract int size();
/*     */   
/*     */   public abstract void clear();
/*     */   
/*     */   public abstract void put(K paramK, V paramV);
/*     */   
/*     */   public abstract V get(Object paramObject);
/*     */   
/*     */   public abstract void remove(Object paramObject);
/*     */   
/*     */   public abstract void setCapacity(int paramInt);
/*     */   
/*     */   public abstract void setTimeout(int paramInt);
/*     */   
/*     */   public abstract void accept(CacheVisitor<K, V> paramCacheVisitor);
/*     */   
/*     */   public static <K, V> Cache<K, V> newSoftMemoryCache(int paramInt)
/*     */   {
/* 123 */     return new MemoryCache(true, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> Cache<K, V> newSoftMemoryCache(int paramInt1, int paramInt2)
/*     */   {
/* 132 */     return new MemoryCache(true, paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> Cache<K, V> newHardMemoryCache(int paramInt)
/*     */   {
/* 140 */     return new MemoryCache(false, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> Cache<K, V> newNullCache()
/*     */   {
/* 148 */     return NullCache.INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> Cache<K, V> newHardMemoryCache(int paramInt1, int paramInt2)
/*     */   {
/* 157 */     return new MemoryCache(false, paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */   public static abstract interface CacheVisitor<K, V>
/*     */   {
/*     */     public abstract void visit(Map<K, V> paramMap);
/*     */   }
/*     */   
/*     */   public static class EqualByteArray {
/*     */     private final byte[] b;
/*     */     private volatile int hash;
/*     */     
/*     */     public EqualByteArray(byte[] paramArrayOfByte) {
/* 170 */       this.b = paramArrayOfByte;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 174 */       int i = this.hash;
/* 175 */       if (i == 0) {
/* 176 */         i = this.b.length + 1;
/* 177 */         for (int j = 0; j < this.b.length; j++) {
/* 178 */           i += (this.b[j] & 0xFF) * 37;
/*     */         }
/* 180 */         this.hash = i;
/*     */       }
/* 182 */       return i;
/*     */     }
/*     */     
/*     */     public boolean equals(Object paramObject) {
/* 186 */       if (this == paramObject) {
/* 187 */         return true;
/*     */       }
/* 189 */       if (!(paramObject instanceof EqualByteArray)) {
/* 190 */         return false;
/*     */       }
/* 192 */       EqualByteArray localEqualByteArray = (EqualByteArray)paramObject;
/* 193 */       return Arrays.equals(this.b, localEqualByteArray.b);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\Cache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.java2d.loops;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RenderCache
/*     */ {
/*     */   private Entry[] entries;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final class Entry
/*     */   {
/*     */     private SurfaceType src;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private CompositeType comp;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private SurfaceType dst;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private Object value;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Entry(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2, Object paramObject)
/*     */     {
/*  40 */       this.src = paramSurfaceType1;
/*  41 */       this.comp = paramCompositeType;
/*  42 */       this.dst = paramSurfaceType2;
/*  43 */       this.value = paramObject;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean matches(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */     {
/*  54 */       return (this.src == paramSurfaceType1) && (this.comp == paramCompositeType) && (this.dst == paramSurfaceType2);
/*     */     }
/*     */     
/*     */ 
/*     */     public Object getValue()
/*     */     {
/*  60 */       return this.value;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public RenderCache(int paramInt)
/*     */   {
/*  67 */     this.entries = new Entry[paramInt];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized Object get(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
/*     */   {
/*  74 */     int i = this.entries.length - 1;
/*  75 */     for (int j = i; j >= 0; j--) {
/*  76 */       Entry localEntry = this.entries[j];
/*  77 */       if (localEntry == null) {
/*     */         break;
/*     */       }
/*  80 */       if (localEntry.matches(paramSurfaceType1, paramCompositeType, paramSurfaceType2)) {
/*  81 */         if (j < i - 4) {
/*  82 */           System.arraycopy(this.entries, j + 1, this.entries, j, i - j);
/*  83 */           this.entries[i] = localEntry;
/*     */         }
/*  85 */         return localEntry.getValue();
/*     */       }
/*     */     }
/*     */     
/*  89 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void put(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2, Object paramObject)
/*     */   {
/*  97 */     Entry localEntry = new Entry(paramSurfaceType1, paramCompositeType, paramSurfaceType2, paramObject);
/*     */     
/*  99 */     int i = this.entries.length;
/* 100 */     System.arraycopy(this.entries, 1, this.entries, 0, i - 1);
/* 101 */     this.entries[(i - 1)] = localEntry;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\loops\RenderCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
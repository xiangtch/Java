/*     */ package sun.misc;
/*     */ 
/*     */ import java.lang.ref.SoftReference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class Ref
/*     */ {
/*  51 */   private SoftReference soft = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Object get()
/*     */   {
/*  62 */     Object localObject = check();
/*  63 */     if (localObject == null) {
/*  64 */       localObject = reconstitute();
/*  65 */       setThing(localObject);
/*     */     }
/*  67 */     return localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Object reconstitute();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void flush()
/*     */   {
/*  87 */     SoftReference localSoftReference = this.soft;
/*  88 */     if (localSoftReference != null) localSoftReference.clear();
/*  89 */     this.soft = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void setThing(Object paramObject)
/*     */   {
/*  97 */     flush();
/*  98 */     this.soft = new SoftReference(paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized Object check()
/*     */   {
/* 105 */     SoftReference localSoftReference = this.soft;
/* 106 */     if (localSoftReference == null) return null;
/* 107 */     return localSoftReference.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Ref() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public Ref(Object paramObject)
/*     */   {
/* 119 */     setThing(paramObject);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\Ref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
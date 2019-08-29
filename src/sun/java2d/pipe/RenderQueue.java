/*     */ package sun.java2d.pipe;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import sun.awt.SunToolkit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class RenderQueue
/*     */ {
/*     */   private static final int BUFFER_SIZE = 32000;
/*     */   protected RenderBuffer buf;
/*     */   protected Set refSet;
/*     */   
/*     */   protected RenderQueue()
/*     */   {
/*  87 */     this.refSet = new HashSet();
/*  88 */     this.buf = RenderBuffer.allocate(32000);
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
/*     */   public final void lock() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean tryLock()
/*     */   {
/* 121 */     return SunToolkit.awtTryLock();
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
/*     */   public final void unlock() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void addReference(Object paramObject)
/*     */   {
/* 143 */     this.refSet.add(paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final RenderBuffer getBuffer()
/*     */   {
/* 150 */     return this.buf;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void ensureCapacity(int paramInt)
/*     */   {
/* 162 */     if (this.buf.remaining() < paramInt) {
/* 163 */       flushNow();
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
/*     */   public final void ensureCapacityAndAlignment(int paramInt1, int paramInt2)
/*     */   {
/* 180 */     ensureCapacity(paramInt1 + 4);
/* 181 */     ensureAlignment(paramInt2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void ensureAlignment(int paramInt)
/*     */   {
/* 193 */     int i = this.buf.position() + paramInt;
/* 194 */     if ((i & 0x7) != 0) {
/* 195 */       this.buf.putInt(90);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void flushNow();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void flushAndInvokeNow(Runnable paramRunnable);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void flushNow(int paramInt)
/*     */   {
/* 220 */     this.buf.position(paramInt);
/* 221 */     flushNow();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\RenderQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
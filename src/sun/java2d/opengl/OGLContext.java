/*     */ package sun.java2d.opengl;
/*     */ 
/*     */ import sun.java2d.pipe.BufferedContext;
/*     */ import sun.java2d.pipe.RenderBuffer;
/*     */ import sun.java2d.pipe.RenderQueue;
/*     */ import sun.java2d.pipe.hw.ContextCapabilities;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OGLContext
/*     */   extends BufferedContext
/*     */ {
/*     */   private final OGLGraphicsConfig config;
/*     */   
/*     */   OGLContext(RenderQueue paramRenderQueue, OGLGraphicsConfig paramOGLGraphicsConfig)
/*     */   {
/*  46 */     super(paramRenderQueue);
/*  47 */     this.config = paramOGLGraphicsConfig;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static void setScratchSurface(OGLGraphicsConfig paramOGLGraphicsConfig)
/*     */   {
/*  54 */     setScratchSurface(paramOGLGraphicsConfig.getNativeConfigInfo());
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
/*     */ 
/*     */   static void setScratchSurface(long paramLong)
/*     */   {
/*  78 */     currentContext = null;
/*     */     
/*     */ 
/*  81 */     OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/*  82 */     RenderBuffer localRenderBuffer = localOGLRenderQueue.getBuffer();
/*  83 */     localOGLRenderQueue.ensureCapacityAndAlignment(12, 4);
/*  84 */     localRenderBuffer.putInt(71);
/*  85 */     localRenderBuffer.putLong(paramLong);
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
/*     */   static void invalidateCurrentContext()
/*     */   {
/* 100 */     if (currentContext != null) {
/* 101 */       currentContext.invalidateContext();
/* 102 */       currentContext = null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 108 */     OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/* 109 */     localOGLRenderQueue.ensureCapacity(4);
/* 110 */     localOGLRenderQueue.getBuffer().putInt(75);
/* 111 */     localOGLRenderQueue.flushNow();
/*     */   }
/*     */   
/*     */   public RenderQueue getRenderQueue() {
/* 115 */     return OGLRenderQueue.getInstance();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final native String getOGLIdString();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void saveState()
/*     */   {
/* 131 */     invalidateContext();
/* 132 */     invalidateCurrentContext();
/*     */     
/* 134 */     setScratchSurface(this.config);
/*     */     
/*     */ 
/* 137 */     this.rq.ensureCapacity(4);
/* 138 */     this.buf.putInt(78);
/* 139 */     this.rq.flushNow();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void restoreState()
/*     */   {
/* 147 */     invalidateContext();
/* 148 */     invalidateCurrentContext();
/*     */     
/* 150 */     setScratchSurface(this.config);
/*     */     
/*     */ 
/* 153 */     this.rq.ensureCapacity(4);
/* 154 */     this.buf.putInt(79);
/* 155 */     this.rq.flushNow();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static class OGLContextCaps
/*     */     extends ContextCapabilities
/*     */   {
/*     */     static final int CAPS_EXT_FBOBJECT = 12;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     static final int CAPS_STORED_ALPHA = 2;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     static final int CAPS_DOUBLEBUFFERED = 65536;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     static final int CAPS_EXT_LCD_SHADER = 131072;
/*     */     
/*     */ 
/*     */ 
/*     */     static final int CAPS_EXT_BIOP_SHADER = 262144;
/*     */     
/*     */ 
/*     */ 
/*     */     static final int CAPS_EXT_GRAD_SHADER = 524288;
/*     */     
/*     */ 
/*     */ 
/*     */     static final int CAPS_EXT_TEXRECT = 1048576;
/*     */     
/*     */ 
/*     */ 
/*     */     static final int CAPS_EXT_TEXBARRIER = 2097152;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     OGLContextCaps(int paramInt, String paramString)
/*     */     {
/* 203 */       super(paramString);
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 208 */       StringBuffer localStringBuffer = new StringBuffer(super.toString());
/* 209 */       if ((this.caps & 0xC) != 0) {
/* 210 */         localStringBuffer.append("CAPS_EXT_FBOBJECT|");
/*     */       }
/* 212 */       if ((this.caps & 0x2) != 0) {
/* 213 */         localStringBuffer.append("CAPS_STORED_ALPHA|");
/*     */       }
/* 215 */       if ((this.caps & 0x10000) != 0) {
/* 216 */         localStringBuffer.append("CAPS_DOUBLEBUFFERED|");
/*     */       }
/* 218 */       if ((this.caps & 0x20000) != 0) {
/* 219 */         localStringBuffer.append("CAPS_EXT_LCD_SHADER|");
/*     */       }
/* 221 */       if ((this.caps & 0x40000) != 0) {
/* 222 */         localStringBuffer.append("CAPS_BIOP_SHADER|");
/*     */       }
/* 224 */       if ((this.caps & 0x80000) != 0) {
/* 225 */         localStringBuffer.append("CAPS_EXT_GRAD_SHADER|");
/*     */       }
/* 227 */       if ((this.caps & 0x100000) != 0) {
/* 228 */         localStringBuffer.append("CAPS_EXT_TEXRECT|");
/*     */       }
/* 230 */       if ((this.caps & 0x200000) != 0) {
/* 231 */         localStringBuffer.append("CAPS_EXT_TEXBARRIER|");
/*     */       }
/* 233 */       return localStringBuffer.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\opengl\OGLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
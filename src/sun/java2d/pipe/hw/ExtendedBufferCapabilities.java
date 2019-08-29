/*     */ package sun.java2d.pipe.hw;
/*     */ 
/*     */ import java.awt.BufferCapabilities;
/*     */ import java.awt.BufferCapabilities.FlipContents;
/*     */ import java.awt.ImageCapabilities;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExtendedBufferCapabilities
/*     */   extends BufferCapabilities
/*     */ {
/*     */   private VSyncType vsync;
/*     */   
/*     */   public static enum VSyncType
/*     */   {
/*  58 */     VSYNC_DEFAULT(0), 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  63 */     VSYNC_ON(1), 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  68 */     VSYNC_OFF(2);
/*     */     
/*     */ 
/*     */     private int id;
/*     */     
/*     */     public int id()
/*     */     {
/*  75 */       return this.id;
/*     */     }
/*     */     
/*     */     private VSyncType(int paramInt) {
/*  79 */       this.id = paramInt;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExtendedBufferCapabilities(BufferCapabilities paramBufferCapabilities)
/*     */   {
/*  91 */     super(paramBufferCapabilities.getFrontBufferCapabilities(), paramBufferCapabilities
/*  92 */       .getBackBufferCapabilities(), paramBufferCapabilities
/*  93 */       .getFlipContents());
/*     */     
/*  95 */     this.vsync = VSyncType.VSYNC_DEFAULT;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExtendedBufferCapabilities(ImageCapabilities paramImageCapabilities1, ImageCapabilities paramImageCapabilities2, FlipContents paramFlipContents)
/*     */   {
/* 105 */     super(paramImageCapabilities1, paramImageCapabilities2, paramFlipContents);
/*     */     
/* 107 */     this.vsync = VSyncType.VSYNC_DEFAULT;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExtendedBufferCapabilities(ImageCapabilities paramImageCapabilities1, ImageCapabilities paramImageCapabilities2, FlipContents paramFlipContents, VSyncType paramVSyncType)
/*     */   {
/* 118 */     super(paramImageCapabilities1, paramImageCapabilities2, paramFlipContents);
/*     */     
/* 120 */     this.vsync = paramVSyncType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExtendedBufferCapabilities(BufferCapabilities paramBufferCapabilities, VSyncType paramVSyncType)
/*     */   {
/* 128 */     super(paramBufferCapabilities.getFrontBufferCapabilities(), paramBufferCapabilities
/* 129 */       .getBackBufferCapabilities(), paramBufferCapabilities
/* 130 */       .getFlipContents());
/*     */     
/* 132 */     this.vsync = paramVSyncType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExtendedBufferCapabilities derive(VSyncType paramVSyncType)
/*     */   {
/* 140 */     return new ExtendedBufferCapabilities(this, paramVSyncType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public VSyncType getVSync()
/*     */   {
/* 147 */     return this.vsync;
/*     */   }
/*     */   
/*     */   public final boolean isPageFlipping()
/*     */   {
/* 152 */     return true;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\hw\ExtendedBufferCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
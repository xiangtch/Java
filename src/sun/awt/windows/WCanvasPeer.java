/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.peer.CanvasPeer;
/*     */ import sun.awt.Graphics2Delegate;
/*     */ import sun.awt.PaintEventDispatcher;
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
/*     */ class WCanvasPeer
/*     */   extends WComponentPeer
/*     */   implements CanvasPeer
/*     */ {
/*     */   private boolean eraseBackground;
/*     */   
/*     */   WCanvasPeer(Component paramComponent)
/*     */   {
/*  45 */     super(paramComponent);
/*     */   }
/*     */   
/*     */ 
/*     */   native void create(WComponentPeer paramWComponentPeer);
/*     */   
/*     */   void initialize()
/*     */   {
/*  53 */     this.eraseBackground = (!SunToolkit.getSunAwtNoerasebackground());
/*  54 */     boolean bool = SunToolkit.getSunAwtErasebackgroundonresize();
/*     */     
/*     */ 
/*     */ 
/*  58 */     if (!PaintEventDispatcher.getPaintEventDispatcher().shouldDoNativeBackgroundErase((Component)this.target)) {
/*  59 */       this.eraseBackground = false;
/*     */     }
/*  61 */     setNativeBackgroundErase(this.eraseBackground, bool);
/*  62 */     super.initialize();
/*  63 */     Color localColor = ((Component)this.target).getBackground();
/*  64 */     if (localColor != null) {
/*  65 */       setBackground(localColor);
/*     */     }
/*     */   }
/*     */   
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/*  71 */     Dimension localDimension = ((Component)this.target).getSize();
/*  72 */     if (((paramGraphics instanceof Graphics2D)) || ((paramGraphics instanceof Graphics2Delegate)))
/*     */     {
/*     */ 
/*  75 */       paramGraphics.clearRect(0, 0, localDimension.width, localDimension.height);
/*     */     }
/*     */     else {
/*  78 */       paramGraphics.setColor(((Component)this.target).getBackground());
/*  79 */       paramGraphics.fillRect(0, 0, localDimension.width, localDimension.height);
/*  80 */       paramGraphics.setColor(((Component)this.target).getForeground());
/*     */     }
/*  82 */     super.paint(paramGraphics);
/*     */   }
/*     */   
/*     */   public boolean shouldClearRectBeforePaint()
/*     */   {
/*  87 */     return this.eraseBackground;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void disableBackgroundErase()
/*     */   {
/*  95 */     this.eraseBackground = false;
/*  96 */     setNativeBackgroundErase(false, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private native void setNativeBackgroundErase(boolean paramBoolean1, boolean paramBoolean2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration)
/*     */   {
/* 114 */     return paramGraphicsConfiguration;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WCanvasPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.peer.PanelPeer;
/*     */ import sun.awt.SunGraphicsCallback.PaintHeavyweightComponentsCallback;
/*     */ import sun.awt.SunGraphicsCallback.PrintHeavyweightComponentsCallback;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WPanelPeer
/*     */   extends WCanvasPeer
/*     */   implements PanelPeer
/*     */ {
/*     */   Insets insets_;
/*     */   
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/*  38 */     super.paint(paramGraphics);
/*  39 */     SunGraphicsCallback.PaintHeavyweightComponentsCallback.getInstance()
/*  40 */       .runComponents(((Container)this.target).getComponents(), paramGraphics, 3);
/*     */   }
/*     */   
/*     */ 
/*     */   public void print(Graphics paramGraphics)
/*     */   {
/*  46 */     super.print(paramGraphics);
/*  47 */     SunGraphicsCallback.PrintHeavyweightComponentsCallback.getInstance()
/*  48 */       .runComponents(((Container)this.target).getComponents(), paramGraphics, 3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Insets getInsets()
/*     */   {
/*  57 */     return this.insets_;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static native void initIDs();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   WPanelPeer(Component paramComponent)
/*     */   {
/*  74 */     super(paramComponent);
/*     */   }
/*     */   
/*     */   void initialize()
/*     */   {
/*  79 */     super.initialize();
/*  80 */     this.insets_ = new Insets(0, 0, 0, 0);
/*     */     
/*  82 */     Color localColor = ((Component)this.target).getBackground();
/*  83 */     if (localColor == null) {
/*  84 */       localColor = WColor.getDefaultColor(1);
/*  85 */       ((Component)this.target).setBackground(localColor);
/*  86 */       setBackground(localColor);
/*     */     }
/*  88 */     localColor = ((Component)this.target).getForeground();
/*  89 */     if (localColor == null) {
/*  90 */       localColor = WColor.getDefaultColor(2);
/*  91 */       ((Component)this.target).setForeground(localColor);
/*  92 */       setForeground(localColor);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Insets insets()
/*     */   {
/* 100 */     return getInsets();
/*     */   }
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WPanelPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.java2d;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.HeadlessException;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HeadlessGraphicsEnvironment
/*     */   extends GraphicsEnvironment
/*     */ {
/*     */   private GraphicsEnvironment ge;
/*     */   
/*     */   public HeadlessGraphicsEnvironment(GraphicsEnvironment paramGraphicsEnvironment)
/*     */   {
/*  67 */     this.ge = paramGraphicsEnvironment;
/*     */   }
/*     */   
/*     */   public GraphicsDevice[] getScreenDevices() throws HeadlessException
/*     */   {
/*  72 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public GraphicsDevice getDefaultScreenDevice() throws HeadlessException
/*     */   {
/*  77 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public Point getCenterPoint() throws HeadlessException {
/*  81 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */   public Rectangle getMaximumWindowBounds() throws HeadlessException {
/*  85 */     throw new HeadlessException();
/*     */   }
/*     */   
/*     */ 
/*  89 */   public Graphics2D createGraphics(BufferedImage paramBufferedImage) { return this.ge.createGraphics(paramBufferedImage); }
/*     */   
/*  91 */   public Font[] getAllFonts() { return this.ge.getAllFonts(); }
/*     */   
/*     */   public String[] getAvailableFontFamilyNames() {
/*  94 */     return this.ge.getAvailableFontFamilyNames();
/*     */   }
/*     */   
/*  97 */   public String[] getAvailableFontFamilyNames(Locale paramLocale) { return this.ge.getAvailableFontFamilyNames(paramLocale); }
/*     */   
/*     */   public GraphicsEnvironment getSunGraphicsEnvironment()
/*     */   {
/* 101 */     return this.ge;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\HeadlessGraphicsEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
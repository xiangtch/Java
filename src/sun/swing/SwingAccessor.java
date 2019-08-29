/*     */ package sun.swing;
/*     */ 
/*     */ import java.awt.Point;
/*     */ import javax.swing.RepaintManager;
/*     */ import javax.swing.TransferHandler.DropLocation;
/*     */ import javax.swing.text.JTextComponent;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SwingAccessor
/*     */ {
/*  44 */   private static final Unsafe unsafe = ;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static JTextComponentAccessor jtextComponentAccessor;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static JLightweightFrameAccessor jLightweightFrameAccessor;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static RepaintManagerAccessor repaintManagerAccessor;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setJTextComponentAccessor(JTextComponentAccessor paramJTextComponentAccessor)
/*     */   {
/* 102 */     jtextComponentAccessor = paramJTextComponentAccessor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JTextComponentAccessor getJTextComponentAccessor()
/*     */   {
/* 109 */     if (jtextComponentAccessor == null) {
/* 110 */       unsafe.ensureClassInitialized(JTextComponent.class);
/*     */     }
/*     */     
/* 113 */     return jtextComponentAccessor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setJLightweightFrameAccessor(JLightweightFrameAccessor paramJLightweightFrameAccessor)
/*     */   {
/* 125 */     jLightweightFrameAccessor = paramJLightweightFrameAccessor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JLightweightFrameAccessor getJLightweightFrameAccessor()
/*     */   {
/* 132 */     if (jLightweightFrameAccessor == null) {
/* 133 */       unsafe.ensureClassInitialized(JLightweightFrame.class);
/*     */     }
/* 135 */     return jLightweightFrameAccessor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setRepaintManagerAccessor(RepaintManagerAccessor paramRepaintManagerAccessor)
/*     */   {
/* 147 */     repaintManagerAccessor = paramRepaintManagerAccessor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static RepaintManagerAccessor getRepaintManagerAccessor()
/*     */   {
/* 154 */     if (repaintManagerAccessor == null) {
/* 155 */       unsafe.ensureClassInitialized(RepaintManager.class);
/*     */     }
/* 157 */     return repaintManagerAccessor;
/*     */   }
/*     */   
/*     */   public static abstract interface JLightweightFrameAccessor
/*     */   {
/*     */     public abstract void updateCursor(JLightweightFrame paramJLightweightFrame);
/*     */   }
/*     */   
/*     */   public static abstract interface JTextComponentAccessor
/*     */   {
/*     */     public abstract TransferHandler.DropLocation dropLocationForPoint(JTextComponent paramJTextComponent, Point paramPoint);
/*     */     
/*     */     public abstract Object setDropLocation(JTextComponent paramJTextComponent, TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean);
/*     */   }
/*     */   
/*     */   public static abstract interface RepaintManagerAccessor
/*     */   {
/*     */     public abstract void addRepaintListener(RepaintManager paramRepaintManager, SwingUtilities2.RepaintListener paramRepaintListener);
/*     */     
/*     */     public abstract void removeRepaintListener(RepaintManager paramRepaintManager, SwingUtilities2.RepaintListener paramRepaintListener);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\swing\SwingAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
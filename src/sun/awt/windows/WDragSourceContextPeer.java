/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.dnd.DragGestureEvent;
/*     */ import java.awt.dnd.DragGestureRecognizer;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.awt.event.InputEvent;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.Raster;
/*     */ import java.util.Map;
/*     */ import sun.awt.dnd.SunDragSourceContextPeer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WDragSourceContextPeer
/*     */   extends SunDragSourceContextPeer
/*     */ {
/*  63 */   private static final WDragSourceContextPeer theInstance = new WDragSourceContextPeer(null);
/*     */   
/*     */   public void startSecondaryEventLoop() {}
/*     */   
/*     */   public void quitSecondaryEventLoop() {}
/*     */   
/*     */   private WDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*     */   {
/*  71 */     super(paramDragGestureEvent);
/*     */   }
/*     */   
/*     */   static WDragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException {
/*  75 */     theInstance.setTrigger(paramDragGestureEvent);
/*  76 */     return theInstance;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void startDrag(Transferable paramTransferable, long[] paramArrayOfLong, Map paramMap)
/*     */   {
/*  82 */     long l = 0L;
/*     */     
/*  84 */     l = createDragSource(getTrigger().getComponent(), paramTransferable, 
/*     */     
/*  86 */       getTrigger().getTriggerEvent(), 
/*  87 */       getTrigger().getSourceAsDragGestureRecognizer().getSourceActions(), paramArrayOfLong, paramMap);
/*     */     
/*     */ 
/*     */ 
/*  91 */     if (l == 0L) {
/*  92 */       throw new InvalidDnDOperationException("failed to create native peer");
/*     */     }
/*     */     
/*  95 */     int[] arrayOfInt = null;
/*  96 */     Point localPoint = null;
/*     */     
/*  98 */     Image localImage = getDragImage();
/*  99 */     int i = -1;
/* 100 */     int j = -1;
/* 101 */     if (localImage != null) {
/*     */       try
/*     */       {
/* 104 */         i = localImage.getWidth(null);
/* 105 */         j = localImage.getHeight(null);
/* 106 */         if ((i < 0) || (j < 0)) {
/* 107 */           throw new InvalidDnDOperationException("drag image is not ready");
/*     */         }
/*     */         
/*     */ 
/* 111 */         localPoint = getDragImageOffset();
/* 112 */         BufferedImage localBufferedImage = new BufferedImage(i, j, 2);
/*     */         
/*     */ 
/*     */ 
/* 116 */         localBufferedImage.getGraphics().drawImage(localImage, 0, 0, null);
/*     */         
/*     */ 
/* 119 */         arrayOfInt = ((DataBufferInt)localBufferedImage.getData().getDataBuffer()).getData();
/*     */       } catch (Throwable localThrowable) {
/* 121 */         throw new InvalidDnDOperationException("drag image creation problem: " + localThrowable.getMessage());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 127 */     setNativeContext(l);
/* 128 */     WDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(paramTransferable);
/*     */     
/* 130 */     if (arrayOfInt != null) {
/* 131 */       doDragDrop(
/* 132 */         getNativeContext(), 
/* 133 */         getCursor(), arrayOfInt, i, j, localPoint.x, localPoint.y);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 138 */       doDragDrop(
/* 139 */         getNativeContext(), 
/* 140 */         getCursor(), null, -1, -1, 0, 0);
/*     */     }
/*     */   }
/*     */   
/*     */   native long createDragSource(Component paramComponent, Transferable paramTransferable, InputEvent paramInputEvent, int paramInt, long[] paramArrayOfLong, Map paramMap);
/*     */   
/*     */   native void doDragDrop(long paramLong, Cursor paramCursor, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */   protected native void setNativeCursor(long paramLong, Cursor paramCursor, int paramInt);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WDragSourceContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
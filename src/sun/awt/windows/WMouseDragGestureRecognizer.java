/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.dnd.DragGestureListener;
/*     */ import java.awt.dnd.DragSource;
/*     */ import java.awt.dnd.MouseDragGestureRecognizer;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WMouseDragGestureRecognizer
/*     */   extends MouseDragGestureRecognizer
/*     */ {
/*     */   private static final long serialVersionUID = -3527844310018033570L;
/*     */   protected static int motionThreshold;
/*     */   protected static final int ButtonMask = 7168;
/*     */   
/*     */   protected WMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
/*     */   {
/*  78 */     super(paramDragSource, paramComponent, paramInt, paramDragGestureListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected WMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt)
/*     */   {
/*  90 */     this(paramDragSource, paramComponent, paramInt, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected WMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent)
/*     */   {
/* 101 */     this(paramDragSource, paramComponent, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected WMouseDragGestureRecognizer(DragSource paramDragSource)
/*     */   {
/* 111 */     this(paramDragSource, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int mapDragOperationFromModifiers(MouseEvent paramMouseEvent)
/*     */   {
/* 119 */     int i = paramMouseEvent.getModifiersEx();
/* 120 */     int j = i & 0x1C00;
/*     */     
/*     */ 
/* 123 */     if ((j != 1024) && (j != 2048) && (j != 4096))
/*     */     {
/*     */ 
/* 126 */       return 0;
/*     */     }
/*     */     
/* 129 */     return 
/* 130 */       SunDragSourceContextPeer.convertModifiersToDropAction(i, 
/* 131 */       getSourceActions());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseClicked(MouseEvent paramMouseEvent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mousePressed(MouseEvent paramMouseEvent)
/*     */   {
/* 149 */     this.events.clear();
/*     */     
/* 151 */     if (mapDragOperationFromModifiers(paramMouseEvent) != 0) {
/*     */       try {
/* 153 */         motionThreshold = DragSource.getDragThreshold();
/*     */       } catch (Exception localException) {
/* 155 */         motionThreshold = 5;
/*     */       }
/* 157 */       appendEvent(paramMouseEvent);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseReleased(MouseEvent paramMouseEvent)
/*     */   {
/* 167 */     this.events.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseEntered(MouseEvent paramMouseEvent)
/*     */   {
/* 176 */     this.events.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseExited(MouseEvent paramMouseEvent)
/*     */   {
/* 186 */     if (!this.events.isEmpty()) {
/* 187 */       int i = mapDragOperationFromModifiers(paramMouseEvent);
/*     */       
/* 189 */       if (i == 0) {
/* 190 */         this.events.clear();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mouseDragged(MouseEvent paramMouseEvent)
/*     */   {
/* 201 */     if (!this.events.isEmpty()) {
/* 202 */       int i = mapDragOperationFromModifiers(paramMouseEvent);
/*     */       
/* 204 */       if (i == 0) {
/* 205 */         return;
/*     */       }
/*     */       
/* 208 */       MouseEvent localMouseEvent = (MouseEvent)this.events.get(0);
/*     */       
/*     */ 
/* 211 */       Point localPoint1 = localMouseEvent.getPoint();
/* 212 */       Point localPoint2 = paramMouseEvent.getPoint();
/*     */       
/* 214 */       int j = Math.abs(localPoint1.x - localPoint2.x);
/* 215 */       int k = Math.abs(localPoint1.y - localPoint2.y);
/*     */       
/* 217 */       if ((j > motionThreshold) || (k > motionThreshold)) {
/* 218 */         fireDragGestureRecognized(i, ((MouseEvent)getTriggerEvent()).getPoint());
/*     */       } else {
/* 220 */         appendEvent(paramMouseEvent);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void mouseMoved(MouseEvent paramMouseEvent) {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WMouseDragGestureRecognizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
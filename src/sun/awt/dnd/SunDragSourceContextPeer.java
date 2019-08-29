/*     */ package sun.awt.dnd;
/*     */ 
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.dnd.DragGestureEvent;
/*     */ import java.awt.dnd.DragSource;
/*     */ import java.awt.dnd.DragSourceContext;
/*     */ import java.awt.dnd.DragSourceDragEvent;
/*     */ import java.awt.dnd.DragSourceDropEvent;
/*     */ import java.awt.dnd.DragSourceEvent;
/*     */ import java.awt.dnd.InvalidDnDOperationException;
/*     */ import java.awt.dnd.peer.DragSourceContextPeer;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.Map;
/*     */ import java.util.SortedMap;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.datatransfer.DataTransferer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SunDragSourceContextPeer
/*     */   implements DragSourceContextPeer
/*     */ {
/*     */   private DragGestureEvent trigger;
/*     */   private Component component;
/*     */   private Cursor cursor;
/*     */   private Image dragImage;
/*     */   private Point dragImageOffset;
/*     */   private long nativeCtxt;
/*     */   private DragSourceContext dragSourceContext;
/*     */   private int sourceActions;
/*  77 */   private static boolean dragDropInProgress = false;
/*  78 */   private static boolean discardingMouseEvents = false;
/*     */   
/*     */ 
/*     */   protected static final int DISPATCH_ENTER = 1;
/*     */   
/*     */   protected static final int DISPATCH_MOTION = 2;
/*     */   
/*     */   protected static final int DISPATCH_CHANGED = 3;
/*     */   
/*     */   protected static final int DISPATCH_EXIT = 4;
/*     */   
/*     */   protected static final int DISPATCH_FINISH = 5;
/*     */   
/*     */   protected static final int DISPATCH_MOUSE_MOVED = 6;
/*     */   
/*     */ 
/*     */   public SunDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
/*     */   {
/*  96 */     this.trigger = paramDragGestureEvent;
/*  97 */     if (this.trigger != null) {
/*  98 */       this.component = this.trigger.getComponent();
/*     */     } else {
/* 100 */       this.component = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startSecondaryEventLoop() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void quitSecondaryEventLoop() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void startDrag(DragSourceContext paramDragSourceContext, Cursor paramCursor, Image paramImage, Point paramPoint)
/*     */     throws InvalidDnDOperationException
/*     */   {
/* 119 */     if (getTrigger().getTriggerEvent() == null) {
/* 120 */       throw new InvalidDnDOperationException("DragGestureEvent has a null trigger");
/*     */     }
/*     */     
/* 123 */     this.dragSourceContext = paramDragSourceContext;
/* 124 */     this.cursor = paramCursor;
/* 125 */     this.sourceActions = getDragSourceContext().getSourceActions();
/* 126 */     this.dragImage = paramImage;
/* 127 */     this.dragImageOffset = paramPoint;
/*     */     
/* 129 */     Transferable localTransferable = getDragSourceContext().getTransferable();
/*     */     
/* 131 */     SortedMap localSortedMap = DataTransferer.getInstance().getFormatsForTransferable(localTransferable, 
/* 132 */       DataTransferer.adaptFlavorMap(getTrigger().getDragSource().getFlavorMap()));
/* 133 */     DataTransferer.getInstance();
/* 134 */     long[] arrayOfLong = DataTransferer.keysToLongArray(localSortedMap);
/* 135 */     startDrag(localTransferable, arrayOfLong, localSortedMap);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 141 */     discardingMouseEvents = true;
/* 142 */     EventQueue.invokeLater(new Runnable() {
/*     */       public void run() {
/* 144 */         SunDragSourceContextPeer.access$002(false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void startDrag(Transferable paramTransferable, long[] paramArrayOfLong, Map paramMap);
/*     */   
/*     */ 
/*     */   public void setCursor(Cursor paramCursor)
/*     */     throws InvalidDnDOperationException
/*     */   {
/* 157 */     synchronized (this) {
/* 158 */       if ((this.cursor == null) || (!this.cursor.equals(paramCursor))) {
/* 159 */         this.cursor = paramCursor;
/*     */         
/*     */ 
/* 162 */         setNativeCursor(getNativeContext(), paramCursor, paramCursor != null ? paramCursor
/* 163 */           .getType() : 0);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Cursor getCursor()
/*     */   {
/* 173 */     return this.cursor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Image getDragImage()
/*     */   {
/* 183 */     return this.dragImage;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Point getDragImageOffset()
/*     */   {
/* 195 */     if (this.dragImageOffset == null) {
/* 196 */       return new Point(0, 0);
/*     */     }
/* 198 */     return new Point(this.dragImageOffset);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void setNativeCursor(long paramLong, Cursor paramCursor, int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */   protected synchronized void setTrigger(DragGestureEvent paramDragGestureEvent)
/*     */   {
/* 210 */     this.trigger = paramDragGestureEvent;
/* 211 */     if (this.trigger != null) {
/* 212 */       this.component = this.trigger.getComponent();
/*     */     } else {
/* 214 */       this.component = null;
/*     */     }
/*     */   }
/*     */   
/*     */   protected DragGestureEvent getTrigger() {
/* 219 */     return this.trigger;
/*     */   }
/*     */   
/*     */   protected Component getComponent() {
/* 223 */     return this.component;
/*     */   }
/*     */   
/*     */   protected synchronized void setNativeContext(long paramLong) {
/* 227 */     this.nativeCtxt = paramLong;
/*     */   }
/*     */   
/*     */   protected synchronized long getNativeContext() {
/* 231 */     return this.nativeCtxt;
/*     */   }
/*     */   
/*     */   protected DragSourceContext getDragSourceContext() {
/* 235 */     return this.dragSourceContext;
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
/*     */   public void transferablesFlavorsChanged() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void postDragSourceDragEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 258 */     int i = convertModifiersToDropAction(paramInt2, this.sourceActions);
/*     */     
/*     */ 
/*     */ 
/* 262 */     DragSourceDragEvent localDragSourceDragEvent = new DragSourceDragEvent(getDragSourceContext(), i, paramInt1 & this.sourceActions, paramInt2, paramInt3, paramInt4);
/*     */     
/*     */ 
/*     */ 
/* 266 */     EventDispatcher localEventDispatcher = new EventDispatcher(paramInt5, localDragSourceDragEvent);
/*     */     
/* 268 */     SunToolkit.invokeLaterOnAppContext(
/* 269 */       SunToolkit.targetToAppContext(getComponent()), localEventDispatcher);
/*     */     
/* 271 */     startSecondaryEventLoop();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void dragEnter(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 281 */     postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void dragMotion(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 291 */     postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void operationChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 301 */     postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void dragExit(int paramInt1, int paramInt2)
/*     */   {
/* 310 */     DragSourceEvent localDragSourceEvent = new DragSourceEvent(getDragSourceContext(), paramInt1, paramInt2);
/* 311 */     EventDispatcher localEventDispatcher = new EventDispatcher(4, localDragSourceEvent);
/*     */     
/*     */ 
/* 314 */     SunToolkit.invokeLaterOnAppContext(
/* 315 */       SunToolkit.targetToAppContext(getComponent()), localEventDispatcher);
/*     */     
/* 317 */     startSecondaryEventLoop();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void dragMouseMoved(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 327 */     postDragSourceDragEvent(paramInt1, paramInt2, paramInt3, paramInt4, 6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void dragDropFinished(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 339 */     DragSourceDropEvent localDragSourceDropEvent = new DragSourceDropEvent(getDragSourceContext(), paramInt1 & this.sourceActions, paramBoolean, paramInt2, paramInt3);
/*     */     
/*     */ 
/* 342 */     EventDispatcher localEventDispatcher = new EventDispatcher(5, localDragSourceDropEvent);
/*     */     
/*     */ 
/* 345 */     SunToolkit.invokeLaterOnAppContext(
/* 346 */       SunToolkit.targetToAppContext(getComponent()), localEventDispatcher);
/*     */     
/* 348 */     startSecondaryEventLoop();
/* 349 */     setNativeContext(0L);
/* 350 */     this.dragImage = null;
/* 351 */     this.dragImageOffset = null;
/*     */   }
/*     */   
/*     */   public static void setDragDropInProgress(boolean paramBoolean) throws InvalidDnDOperationException
/*     */   {
/* 356 */     synchronized (SunDragSourceContextPeer.class) {
/* 357 */       if (dragDropInProgress == paramBoolean) {
/* 358 */         throw new InvalidDnDOperationException(getExceptionMessage(paramBoolean));
/*     */       }
/* 360 */       dragDropInProgress = paramBoolean;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean checkEvent(AWTEvent paramAWTEvent)
/*     */   {
/* 369 */     if ((discardingMouseEvents) && ((paramAWTEvent instanceof MouseEvent))) {
/* 370 */       MouseEvent localMouseEvent = (MouseEvent)paramAWTEvent;
/* 371 */       if (!(localMouseEvent instanceof SunDropTargetEvent)) {
/* 372 */         return false;
/*     */       }
/*     */     }
/* 375 */     return true;
/*     */   }
/*     */   
/*     */   public static void checkDragDropInProgress() throws InvalidDnDOperationException
/*     */   {
/* 380 */     if (dragDropInProgress) {
/* 381 */       throw new InvalidDnDOperationException(getExceptionMessage(true));
/*     */     }
/*     */   }
/*     */   
/*     */   private static String getExceptionMessage(boolean paramBoolean) {
/* 386 */     return paramBoolean ? "Drag and drop in progress" : "No drag in progress";
/*     */   }
/*     */   
/*     */   public static int convertModifiersToDropAction(int paramInt1, int paramInt2)
/*     */   {
/* 391 */     int i = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 404 */     switch (paramInt1 & 0xC0)
/*     */     {
/*     */     case 192: 
/* 407 */       i = 1073741824; break;
/*     */     case 128: 
/* 409 */       i = 1; break;
/*     */     case 64: 
/* 411 */       i = 2; break;
/*     */     default: 
/* 413 */       if ((paramInt2 & 0x2) != 0) {
/* 414 */         i = 2;
/* 415 */       } else if ((paramInt2 & 0x1) != 0) {
/* 416 */         i = 1;
/* 417 */       } else if ((paramInt2 & 0x40000000) != 0) {
/* 418 */         i = 1073741824;
/*     */       }
/*     */       break;
/*     */     }
/* 422 */     return i & paramInt2;
/*     */   }
/*     */   
/*     */   private void cleanup() {
/* 426 */     this.trigger = null;
/* 427 */     this.component = null;
/* 428 */     this.cursor = null;
/* 429 */     this.dragSourceContext = null;
/* 430 */     SunDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(null);
/* 431 */     setDragDropInProgress(false);
/*     */   }
/*     */   
/*     */   private class EventDispatcher implements Runnable
/*     */   {
/*     */     private final int dispatchType;
/*     */     private final DragSourceEvent event;
/*     */     
/*     */     EventDispatcher(int paramInt, DragSourceEvent paramDragSourceEvent)
/*     */     {
/* 441 */       switch (paramInt) {
/*     */       case 1: 
/*     */       case 2: 
/*     */       case 3: 
/*     */       case 6: 
/* 446 */         if (!(paramDragSourceEvent instanceof DragSourceDragEvent)) {
/* 447 */           throw new IllegalArgumentException("Event: " + paramDragSourceEvent);
/*     */         }
/*     */         break;
/*     */       case 4: 
/*     */         break;
/*     */       case 5: 
/* 453 */         if (!(paramDragSourceEvent instanceof DragSourceDropEvent)) {
/* 454 */           throw new IllegalArgumentException("Event: " + paramDragSourceEvent);
/*     */         }
/*     */         break;
/*     */       default: 
/* 458 */         throw new IllegalArgumentException("Dispatch type: " + paramInt);
/*     */       }
/*     */       
/*     */       
/* 462 */       this.dispatchType = paramInt;
/* 463 */       this.event = paramDragSourceEvent;
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void run()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 85	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
/*     */       //   4: invokevirtual 101	sun/awt/dnd/SunDragSourceContextPeer:getDragSourceContext	()Ljava/awt/dnd/DragSourceContext;
/*     */       //   7: astore_1
/*     */       //   8: aload_0
/*     */       //   9: getfield 83	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:dispatchType	I
/*     */       //   12: tableswitch	default:+141->153, 1:+40->52, 2:+54->66, 3:+68->80, 4:+82->94, 5:+107->119, 6:+93->105
/*     */       //   52: aload_1
/*     */       //   53: aload_0
/*     */       //   54: getfield 84	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
/*     */       //   57: checkcast 43	java/awt/dnd/DragSourceDragEvent
/*     */       //   60: invokevirtual 86	java/awt/dnd/DragSourceContext:dragEnter	(Ljava/awt/dnd/DragSourceDragEvent;)V
/*     */       //   63: goto +120 -> 183
/*     */       //   66: aload_1
/*     */       //   67: aload_0
/*     */       //   68: getfield 84	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
/*     */       //   71: checkcast 43	java/awt/dnd/DragSourceDragEvent
/*     */       //   74: invokevirtual 88	java/awt/dnd/DragSourceContext:dragOver	(Ljava/awt/dnd/DragSourceDragEvent;)V
/*     */       //   77: goto +106 -> 183
/*     */       //   80: aload_1
/*     */       //   81: aload_0
/*     */       //   82: getfield 84	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
/*     */       //   85: checkcast 43	java/awt/dnd/DragSourceDragEvent
/*     */       //   88: invokevirtual 89	java/awt/dnd/DragSourceContext:dropActionChanged	(Ljava/awt/dnd/DragSourceDragEvent;)V
/*     */       //   91: goto +92 -> 183
/*     */       //   94: aload_1
/*     */       //   95: aload_0
/*     */       //   96: getfield 84	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
/*     */       //   99: invokevirtual 91	java/awt/dnd/DragSourceContext:dragExit	(Ljava/awt/dnd/DragSourceEvent;)V
/*     */       //   102: goto +81 -> 183
/*     */       //   105: aload_1
/*     */       //   106: aload_0
/*     */       //   107: getfield 84	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
/*     */       //   110: checkcast 43	java/awt/dnd/DragSourceDragEvent
/*     */       //   113: invokevirtual 87	java/awt/dnd/DragSourceContext:dragMouseMoved	(Ljava/awt/dnd/DragSourceDragEvent;)V
/*     */       //   116: goto +67 -> 183
/*     */       //   119: aload_1
/*     */       //   120: aload_0
/*     */       //   121: getfield 84	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:event	Ljava/awt/dnd/DragSourceEvent;
/*     */       //   124: checkcast 44	java/awt/dnd/DragSourceDropEvent
/*     */       //   127: invokevirtual 90	java/awt/dnd/DragSourceContext:dragDropEnd	(Ljava/awt/dnd/DragSourceDropEvent;)V
/*     */       //   130: aload_0
/*     */       //   131: getfield 85	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
/*     */       //   134: invokestatic 102	sun/awt/dnd/SunDragSourceContextPeer:access$100	(Lsun/awt/dnd/SunDragSourceContextPeer;)V
/*     */       //   137: goto +13 -> 150
/*     */       //   140: astore_2
/*     */       //   141: aload_0
/*     */       //   142: getfield 85	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
/*     */       //   145: invokestatic 102	sun/awt/dnd/SunDragSourceContextPeer:access$100	(Lsun/awt/dnd/SunDragSourceContextPeer;)V
/*     */       //   148: aload_2
/*     */       //   149: athrow
/*     */       //   150: goto +33 -> 183
/*     */       //   153: new 47	java/lang/IllegalStateException
/*     */       //   156: dup
/*     */       //   157: new 50	java/lang/StringBuilder
/*     */       //   160: dup
/*     */       //   161: invokespecial 95	java/lang/StringBuilder:<init>	()V
/*     */       //   164: ldc 1
/*     */       //   166: invokevirtual 99	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   169: aload_0
/*     */       //   170: getfield 83	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:dispatchType	I
/*     */       //   173: invokevirtual 97	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */       //   176: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */       //   179: invokespecial 93	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
/*     */       //   182: athrow
/*     */       //   183: aload_0
/*     */       //   184: getfield 85	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
/*     */       //   187: invokevirtual 100	sun/awt/dnd/SunDragSourceContextPeer:quitSecondaryEventLoop	()V
/*     */       //   190: goto +13 -> 203
/*     */       //   193: astore_3
/*     */       //   194: aload_0
/*     */       //   195: getfield 85	sun/awt/dnd/SunDragSourceContextPeer$EventDispatcher:this$0	Lsun/awt/dnd/SunDragSourceContextPeer;
/*     */       //   198: invokevirtual 100	sun/awt/dnd/SunDragSourceContextPeer:quitSecondaryEventLoop	()V
/*     */       //   201: aload_3
/*     */       //   202: athrow
/*     */       //   203: return
/*     */       // Line number table:
/*     */       //   Java source line #467	-> byte code offset #0
/*     */       //   Java source line #468	-> byte code offset #4
/*     */       //   Java source line #470	-> byte code offset #8
/*     */       //   Java source line #472	-> byte code offset #52
/*     */       //   Java source line #473	-> byte code offset #63
/*     */       //   Java source line #475	-> byte code offset #66
/*     */       //   Java source line #476	-> byte code offset #77
/*     */       //   Java source line #478	-> byte code offset #80
/*     */       //   Java source line #479	-> byte code offset #91
/*     */       //   Java source line #481	-> byte code offset #94
/*     */       //   Java source line #482	-> byte code offset #102
/*     */       //   Java source line #484	-> byte code offset #105
/*     */       //   Java source line #485	-> byte code offset #116
/*     */       //   Java source line #488	-> byte code offset #119
/*     */       //   Java source line #490	-> byte code offset #130
/*     */       //   Java source line #491	-> byte code offset #137
/*     */       //   Java source line #490	-> byte code offset #140
/*     */       //   Java source line #491	-> byte code offset #148
/*     */       //   Java source line #492	-> byte code offset #150
/*     */       //   Java source line #494	-> byte code offset #153
/*     */       //   Java source line #498	-> byte code offset #183
/*     */       //   Java source line #499	-> byte code offset #190
/*     */       //   Java source line #498	-> byte code offset #193
/*     */       //   Java source line #499	-> byte code offset #201
/*     */       //   Java source line #500	-> byte code offset #203
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	204	0	this	EventDispatcher
/*     */       //   7	113	1	localDragSourceContext	DragSourceContext
/*     */       //   140	9	2	localObject1	Object
/*     */       //   193	9	3	localObject2	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   119	130	140	finally
/*     */       //   8	183	193	finally
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\dnd\SunDragSourceContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
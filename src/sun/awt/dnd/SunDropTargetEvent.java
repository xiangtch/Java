/*    */ package sun.awt.dnd;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.event.MouseEvent;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SunDropTargetEvent
/*    */   extends MouseEvent
/*    */ {
/*    */   public static final int MOUSE_DROPPED = 502;
/*    */   private final SunDropTargetContextPeer.EventDispatcher dispatcher;
/*    */   
/*    */   public SunDropTargetEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, SunDropTargetContextPeer.EventDispatcher paramEventDispatcher)
/*    */   {
/* 40 */     super(paramComponent, paramInt1, System.currentTimeMillis(), 0, paramInt2, paramInt3, 0, 0, 0, false, 0);
/*    */     
/* 42 */     this.dispatcher = paramEventDispatcher;
/* 43 */     this.dispatcher.registerEvent(this);
/*    */   }
/*    */   
/*    */   /* Error */
/*    */   public void dispatch()
/*    */   {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: getfield 78	sun/awt/dnd/SunDropTargetEvent:dispatcher	Lsun/awt/dnd/SunDropTargetContextPeer$EventDispatcher;
/*    */     //   4: aload_0
/*    */     //   5: invokevirtual 87	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatchEvent	(Lsun/awt/dnd/SunDropTargetEvent;)V
/*    */     //   8: aload_0
/*    */     //   9: getfield 78	sun/awt/dnd/SunDropTargetEvent:dispatcher	Lsun/awt/dnd/SunDropTargetContextPeer$EventDispatcher;
/*    */     //   12: aload_0
/*    */     //   13: invokevirtual 89	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:unregisterEvent	(Lsun/awt/dnd/SunDropTargetEvent;)V
/*    */     //   16: goto +14 -> 30
/*    */     //   19: astore_1
/*    */     //   20: aload_0
/*    */     //   21: getfield 78	sun/awt/dnd/SunDropTargetEvent:dispatcher	Lsun/awt/dnd/SunDropTargetContextPeer$EventDispatcher;
/*    */     //   24: aload_0
/*    */     //   25: invokevirtual 89	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:unregisterEvent	(Lsun/awt/dnd/SunDropTargetEvent;)V
/*    */     //   28: aload_1
/*    */     //   29: athrow
/*    */     //   30: return
/*    */     // Line number table:
/*    */     //   Java source line #48	-> byte code offset #0
/*    */     //   Java source line #50	-> byte code offset #8
/*    */     //   Java source line #51	-> byte code offset #16
/*    */     //   Java source line #50	-> byte code offset #19
/*    */     //   Java source line #51	-> byte code offset #28
/*    */     //   Java source line #52	-> byte code offset #30
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	31	0	this	SunDropTargetEvent
/*    */     //   19	10	1	localObject	Object
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   0	8	19	finally
/*    */   }
/*    */   
/*    */   public void consume()
/*    */   {
/* 55 */     boolean bool = isConsumed();
/* 56 */     super.consume();
/* 57 */     if ((!bool) && (isConsumed())) {
/* 58 */       this.dispatcher.unregisterEvent(this);
/*    */     }
/*    */   }
/*    */   
/*    */   public SunDropTargetContextPeer.EventDispatcher getDispatcher() {
/* 63 */     return this.dispatcher;
/*    */   }
/*    */   
/*    */   public String paramString() {
/* 67 */     String str = null;
/*    */     
/* 69 */     switch (this.id) {
/*    */     case 502: 
/* 71 */       str = "MOUSE_DROPPED"; break;
/*    */     default: 
/* 73 */       return super.paramString();
/*    */     }
/* 75 */     return str + ",(" + getX() + "," + getY() + ")";
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\dnd\SunDropTargetEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
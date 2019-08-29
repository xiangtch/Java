/*     */ package sun.java2d.d3d;
/*     */ 
/*     */ import java.util.Set;
/*     */ import sun.java2d.pipe.RenderBuffer;
/*     */ import sun.java2d.pipe.RenderQueue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class D3DRenderQueue
/*     */   extends RenderQueue
/*     */ {
/*     */   private static D3DRenderQueue theInstance;
/*     */   private static Thread rqThread;
/*     */   
/*     */   public static synchronized D3DRenderQueue getInstance()
/*     */   {
/*  50 */     if (theInstance == null) {
/*  51 */       theInstance = new D3DRenderQueue();
/*     */       
/*  53 */       theInstance.flushAndInvokeNow(new Runnable() {
/*     */         public void run() {
/*  55 */           D3DRenderQueue.access$002(Thread.currentThread());
/*     */         }
/*     */       });
/*     */     }
/*  59 */     return theInstance;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static void sync()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 95	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
/*     */     //   3: ifnull +63 -> 66
/*     */     //   6: invokestatic 98	sun/java2d/ScreenUpdateManager:getInstance	()Lsun/java2d/ScreenUpdateManager;
/*     */     //   9: checkcast 54	sun/java2d/d3d/D3DScreenUpdateManager
/*     */     //   12: astore_0
/*     */     //   13: aload_0
/*     */     //   14: invokevirtual 111	sun/java2d/d3d/D3DScreenUpdateManager:runUpdateNow	()V
/*     */     //   17: getstatic 95	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
/*     */     //   20: invokevirtual 101	sun/java2d/d3d/D3DRenderQueue:lock	()V
/*     */     //   23: getstatic 95	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
/*     */     //   26: iconst_4
/*     */     //   27: invokevirtual 103	sun/java2d/d3d/D3DRenderQueue:ensureCapacity	(I)V
/*     */     //   30: getstatic 95	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
/*     */     //   33: invokevirtual 109	sun/java2d/d3d/D3DRenderQueue:getBuffer	()Lsun/java2d/pipe/RenderBuffer;
/*     */     //   36: bipush 76
/*     */     //   38: invokevirtual 115	sun/java2d/pipe/RenderBuffer:putInt	(I)Lsun/java2d/pipe/RenderBuffer;
/*     */     //   41: pop
/*     */     //   42: getstatic 95	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
/*     */     //   45: invokevirtual 100	sun/java2d/d3d/D3DRenderQueue:flushNow	()V
/*     */     //   48: getstatic 95	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
/*     */     //   51: invokevirtual 102	sun/java2d/d3d/D3DRenderQueue:unlock	()V
/*     */     //   54: goto +12 -> 66
/*     */     //   57: astore_1
/*     */     //   58: getstatic 95	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
/*     */     //   61: invokevirtual 102	sun/java2d/d3d/D3DRenderQueue:unlock	()V
/*     */     //   64: aload_1
/*     */     //   65: athrow
/*     */     //   66: return
/*     */     // Line number table:
/*     */     //   Java source line #72	-> byte code offset #0
/*     */     //   Java source line #76	-> byte code offset #6
/*     */     //   Java source line #77	-> byte code offset #13
/*     */     //   Java source line #79	-> byte code offset #17
/*     */     //   Java source line #81	-> byte code offset #23
/*     */     //   Java source line #82	-> byte code offset #30
/*     */     //   Java source line #83	-> byte code offset #42
/*     */     //   Java source line #85	-> byte code offset #48
/*     */     //   Java source line #86	-> byte code offset #54
/*     */     //   Java source line #85	-> byte code offset #57
/*     */     //   Java source line #86	-> byte code offset #64
/*     */     //   Java source line #88	-> byte code offset #66
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   12	2	0	localD3DScreenUpdateManager	D3DScreenUpdateManager
/*     */     //   57	8	1	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   23	48	57	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static void restoreDevices()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: invokestatic 108	sun/java2d/d3d/D3DRenderQueue:getInstance	()Lsun/java2d/d3d/D3DRenderQueue;
/*     */     //   3: astore_0
/*     */     //   4: aload_0
/*     */     //   5: invokevirtual 101	sun/java2d/d3d/D3DRenderQueue:lock	()V
/*     */     //   8: aload_0
/*     */     //   9: iconst_4
/*     */     //   10: invokevirtual 103	sun/java2d/d3d/D3DRenderQueue:ensureCapacity	(I)V
/*     */     //   13: aload_0
/*     */     //   14: invokevirtual 109	sun/java2d/d3d/D3DRenderQueue:getBuffer	()Lsun/java2d/pipe/RenderBuffer;
/*     */     //   17: bipush 77
/*     */     //   19: invokevirtual 115	sun/java2d/pipe/RenderBuffer:putInt	(I)Lsun/java2d/pipe/RenderBuffer;
/*     */     //   22: pop
/*     */     //   23: aload_0
/*     */     //   24: invokevirtual 100	sun/java2d/d3d/D3DRenderQueue:flushNow	()V
/*     */     //   27: aload_0
/*     */     //   28: invokevirtual 102	sun/java2d/d3d/D3DRenderQueue:unlock	()V
/*     */     //   31: goto +10 -> 41
/*     */     //   34: astore_1
/*     */     //   35: aload_0
/*     */     //   36: invokevirtual 102	sun/java2d/d3d/D3DRenderQueue:unlock	()V
/*     */     //   39: aload_1
/*     */     //   40: athrow
/*     */     //   41: return
/*     */     // Line number table:
/*     */     //   Java source line #95	-> byte code offset #0
/*     */     //   Java source line #96	-> byte code offset #4
/*     */     //   Java source line #98	-> byte code offset #8
/*     */     //   Java source line #99	-> byte code offset #13
/*     */     //   Java source line #100	-> byte code offset #23
/*     */     //   Java source line #102	-> byte code offset #27
/*     */     //   Java source line #103	-> byte code offset #31
/*     */     //   Java source line #102	-> byte code offset #34
/*     */     //   Java source line #103	-> byte code offset #39
/*     */     //   Java source line #104	-> byte code offset #41
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   3	33	0	localD3DRenderQueue	D3DRenderQueue
/*     */     //   34	6	1	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   8	27	34	finally
/*     */   }
/*     */   
/*     */   public static boolean isRenderQueueThread()
/*     */   {
/* 111 */     return Thread.currentThread() == rqThread;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void disposeGraphicsConfig(long paramLong)
/*     */   {
/* 119 */     D3DRenderQueue localD3DRenderQueue = getInstance();
/* 120 */     localD3DRenderQueue.lock();
/*     */     try
/*     */     {
/* 123 */       RenderBuffer localRenderBuffer = localD3DRenderQueue.getBuffer();
/* 124 */       localD3DRenderQueue.ensureCapacityAndAlignment(12, 4);
/* 125 */       localRenderBuffer.putInt(74);
/* 126 */       localRenderBuffer.putLong(paramLong);
/*     */       
/*     */ 
/* 129 */       localD3DRenderQueue.flushNow();
/*     */     } finally {
/* 131 */       localD3DRenderQueue.unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   public void flushNow()
/*     */   {
/* 137 */     flushBuffer(null);
/*     */   }
/*     */   
/*     */   public void flushAndInvokeNow(Runnable paramRunnable)
/*     */   {
/* 142 */     flushBuffer(paramRunnable);
/*     */   }
/*     */   
/*     */   private native void flushBuffer(long paramLong, int paramInt, Runnable paramRunnable);
/*     */   
/*     */   private void flushBuffer(Runnable paramRunnable)
/*     */   {
/* 149 */     int i = this.buf.position();
/* 150 */     if ((i > 0) || (paramRunnable != null))
/*     */     {
/* 152 */       flushBuffer(this.buf.getAddress(), i, paramRunnable);
/*     */     }
/*     */     
/* 155 */     this.buf.clear();
/*     */     
/* 157 */     this.refSet.clear();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\d3d\D3DRenderQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
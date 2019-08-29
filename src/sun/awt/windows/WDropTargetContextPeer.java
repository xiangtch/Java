/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import sun.awt.PeerEvent;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.dnd.SunDropTargetContextPeer;
/*     */ import sun.awt.dnd.SunDropTargetContextPeer.EventDispatcher;
/*     */ import sun.awt.dnd.SunDropTargetEvent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WDropTargetContextPeer
/*     */   extends SunDropTargetContextPeer
/*     */ {
/*     */   static WDropTargetContextPeer getWDropTargetContextPeer()
/*     */   {
/*  53 */     return new WDropTargetContextPeer();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static FileInputStream getFileStream(String paramString, long paramLong)
/*     */     throws IOException
/*     */   {
/*  71 */     return new WDropTargetContextPeerFileStream(paramString, paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static Object getIStream(long paramLong)
/*     */     throws IOException
/*     */   {
/*  79 */     return new WDropTargetContextPeerIStream(paramLong);
/*     */   }
/*     */   
/*     */   protected Object getNativeData(long paramLong)
/*     */   {
/*  84 */     return getData(getNativeDragContext(), paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void doDropDone(boolean paramBoolean1, int paramInt, boolean paramBoolean2)
/*     */   {
/*  94 */     dropDone(getNativeDragContext(), paramBoolean1, paramInt);
/*     */   }
/*     */   
/*     */   protected void eventPosted(final SunDropTargetEvent paramSunDropTargetEvent)
/*     */   {
/*  99 */     if (paramSunDropTargetEvent.getID() != 502) {
/* 100 */       Runnable local1 = new Runnable()
/*     */       {
/*     */         public void run() {
/* 103 */           paramSunDropTargetEvent.getDispatcher().unregisterAllEvents();
/*     */ 
/*     */         }
/*     */         
/*     */ 
/* 108 */       };
/* 109 */       PeerEvent localPeerEvent = new PeerEvent(paramSunDropTargetEvent.getSource(), local1, 0L);
/* 110 */       SunToolkit.executeOnEventHandlerThread(localPeerEvent);
/*     */     }
/*     */   }
/*     */   
/*     */   private native Object getData(long paramLong1, long paramLong2);
/*     */   
/*     */   private native void dropDone(long paramLong, boolean paramBoolean, int paramInt);
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WDropTargetContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.java2d.pipe.hw;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AccelDeviceEventNotifier
/*     */ {
/*     */   private static AccelDeviceEventNotifier theInstance;
/*     */   public static final int DEVICE_RESET = 0;
/*     */   public static final int DEVICE_DISPOSED = 1;
/*     */   private final Map<AccelDeviceEventListener, Integer> listeners;
/*     */   
/*     */   private AccelDeviceEventNotifier()
/*     */   {
/*  60 */     this.listeners = Collections.synchronizedMap(new HashMap(1));
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
/*     */   private static synchronized AccelDeviceEventNotifier getInstance(boolean paramBoolean)
/*     */   {
/*  77 */     if ((theInstance == null) && (paramBoolean)) {
/*  78 */       theInstance = new AccelDeviceEventNotifier();
/*     */     }
/*  80 */     return theInstance;
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
/*     */   public static final void eventOccured(int paramInt1, int paramInt2)
/*     */   {
/*  95 */     AccelDeviceEventNotifier localAccelDeviceEventNotifier = getInstance(false);
/*  96 */     if (localAccelDeviceEventNotifier != null) {
/*  97 */       localAccelDeviceEventNotifier.notifyListeners(paramInt2, paramInt1);
/*     */     }
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
/*     */   public static final void addListener(AccelDeviceEventListener paramAccelDeviceEventListener, int paramInt)
/*     */   {
/* 112 */     getInstance(true).add(paramAccelDeviceEventListener, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void removeListener(AccelDeviceEventListener paramAccelDeviceEventListener)
/*     */   {
/* 121 */     getInstance(true).remove(paramAccelDeviceEventListener);
/*     */   }
/*     */   
/*     */   private final void add(AccelDeviceEventListener paramAccelDeviceEventListener, int paramInt) {
/* 125 */     this.listeners.put(paramAccelDeviceEventListener, Integer.valueOf(paramInt));
/*     */   }
/*     */   
/* 128 */   private final void remove(AccelDeviceEventListener paramAccelDeviceEventListener) { this.listeners.remove(paramAccelDeviceEventListener); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final void notifyListeners(int paramInt1, int paramInt2)
/*     */   {
/*     */     HashMap localHashMap;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 148 */     synchronized (this.listeners) {
/* 149 */       localHashMap = new HashMap(this.listeners);
/*     */     }
/*     */     
/*     */ 
/* 153 */     Set localSet = localHashMap.keySet();
/* 154 */     ??? = localSet.iterator();
/* 155 */     while (((Iterator)???).hasNext()) {
/* 156 */       AccelDeviceEventListener localAccelDeviceEventListener = (AccelDeviceEventListener)((Iterator)???).next();
/* 157 */       Integer localInteger = (Integer)localHashMap.get(localAccelDeviceEventListener);
/*     */       
/* 159 */       if ((localInteger == null) || (localInteger.intValue() == paramInt2))
/*     */       {
/*     */ 
/* 162 */         if (paramInt1 == 0) {
/* 163 */           localAccelDeviceEventListener.onDeviceReset();
/* 164 */         } else if (paramInt1 == 1) {
/* 165 */           localAccelDeviceEventListener.onDeviceDispose();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\pipe\hw\AccelDeviceEventNotifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.awt;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.EventListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EventListenerAggregate
/*     */ {
/*     */   private EventListener[] listenerList;
/*     */   
/*     */   public EventListenerAggregate(Class<? extends EventListener> paramClass)
/*     */   {
/*  57 */     if (paramClass == null) {
/*  58 */       throw new NullPointerException("listener class is null");
/*     */     }
/*     */     
/*  61 */     this.listenerList = ((EventListener[])Array.newInstance(paramClass, 0));
/*     */   }
/*     */   
/*     */   private Class<?> getListenerClass() {
/*  65 */     return this.listenerList.getClass().getComponentType();
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
/*     */   public synchronized void add(EventListener paramEventListener)
/*     */   {
/*  78 */     Class localClass = getListenerClass();
/*     */     
/*  80 */     if (!localClass.isInstance(paramEventListener)) {
/*  81 */       throw new ClassCastException("listener " + paramEventListener + " is not an instance of listener class " + localClass);
/*     */     }
/*     */     
/*     */ 
/*  85 */     EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(localClass, this.listenerList.length + 1);
/*  86 */     System.arraycopy(this.listenerList, 0, arrayOfEventListener, 0, this.listenerList.length);
/*  87 */     arrayOfEventListener[this.listenerList.length] = paramEventListener;
/*  88 */     this.listenerList = arrayOfEventListener;
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
/*     */   public synchronized boolean remove(EventListener paramEventListener)
/*     */   {
/* 105 */     Class localClass = getListenerClass();
/*     */     
/* 107 */     if (!localClass.isInstance(paramEventListener)) {
/* 108 */       throw new ClassCastException("listener " + paramEventListener + " is not an instance of listener class " + localClass);
/*     */     }
/*     */     
/*     */ 
/* 112 */     for (int i = 0; i < this.listenerList.length; i++) {
/* 113 */       if (this.listenerList[i].equals(paramEventListener)) {
/* 114 */         EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(localClass, this.listenerList.length - 1);
/*     */         
/* 116 */         System.arraycopy(this.listenerList, 0, arrayOfEventListener, 0, i);
/* 117 */         System.arraycopy(this.listenerList, i + 1, arrayOfEventListener, i, this.listenerList.length - i - 1);
/* 118 */         this.listenerList = arrayOfEventListener;
/*     */         
/* 120 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 124 */     return false;
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
/*     */   public synchronized EventListener[] getListenersInternal()
/*     */   {
/* 138 */     return this.listenerList;
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
/*     */   public synchronized EventListener[] getListenersCopy()
/*     */   {
/* 153 */     return this.listenerList.length == 0 ? this.listenerList : (EventListener[])this.listenerList.clone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized int size()
/*     */   {
/* 162 */     return this.listenerList.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized boolean isEmpty()
/*     */   {
/* 173 */     return this.listenerList.length == 0;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\EventListenerAggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
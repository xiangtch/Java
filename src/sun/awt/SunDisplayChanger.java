/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.IllegalComponentStateException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ import sun.util.logging.PlatformLogger.Level;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SunDisplayChanger
/*     */ {
/*  59 */   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.multiscreen.SunDisplayChanger");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  65 */   private Map<DisplayChangedListener, Void> listeners = Collections.synchronizedMap(new WeakHashMap(1));
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(DisplayChangedListener paramDisplayChangedListener)
/*     */   {
/*  74 */     if ((log.isLoggable(PlatformLogger.Level.FINE)) && 
/*  75 */       (paramDisplayChangedListener == null)) {
/*  76 */       log.fine("Assertion (theListener != null) failed");
/*     */     }
/*     */     
/*  79 */     if (log.isLoggable(PlatformLogger.Level.FINER)) {
/*  80 */       log.finer("Adding listener: " + paramDisplayChangedListener);
/*     */     }
/*  82 */     this.listeners.put(paramDisplayChangedListener, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove(DisplayChangedListener paramDisplayChangedListener)
/*     */   {
/*  89 */     if ((log.isLoggable(PlatformLogger.Level.FINE)) && 
/*  90 */       (paramDisplayChangedListener == null)) {
/*  91 */       log.fine("Assertion (theListener != null) failed");
/*     */     }
/*     */     
/*  94 */     if (log.isLoggable(PlatformLogger.Level.FINER)) {
/*  95 */       log.finer("Removing listener: " + paramDisplayChangedListener);
/*     */     }
/*  97 */     this.listeners.remove(paramDisplayChangedListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void notifyListeners()
/*     */   {
/* 105 */     if (log.isLoggable(PlatformLogger.Level.FINEST)) {
/* 106 */       log.finest("notifyListeners");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     HashSet localHashSet;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 121 */     synchronized (this.listeners) {
/* 122 */       localHashSet = new HashSet(this.listeners.keySet());
/*     */     }
/*     */     
/* 125 */     ??? = localHashSet.iterator();
/* 126 */     while (((Iterator)???).hasNext()) {
/* 127 */       DisplayChangedListener localDisplayChangedListener = (DisplayChangedListener)((Iterator)???).next();
/*     */       try {
/* 129 */         if (log.isLoggable(PlatformLogger.Level.FINEST)) {
/* 130 */           log.finest("displayChanged for listener: " + localDisplayChangedListener);
/*     */         }
/* 132 */         localDisplayChangedListener.displayChanged();
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (IllegalComponentStateException localIllegalComponentStateException)
/*     */       {
/*     */ 
/* 139 */         this.listeners.remove(localDisplayChangedListener);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void notifyPaletteChanged()
/*     */   {
/* 149 */     if (log.isLoggable(PlatformLogger.Level.FINEST)) {
/* 150 */       log.finest("notifyPaletteChanged");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     HashSet localHashSet;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 165 */     synchronized (this.listeners) {
/* 166 */       localHashSet = new HashSet(this.listeners.keySet());
/*     */     }
/* 168 */     ??? = localHashSet.iterator();
/* 169 */     while (((Iterator)???).hasNext()) {
/* 170 */       DisplayChangedListener localDisplayChangedListener = (DisplayChangedListener)((Iterator)???).next();
/*     */       try {
/* 172 */         if (log.isLoggable(PlatformLogger.Level.FINEST)) {
/* 173 */           log.finest("paletteChanged for listener: " + localDisplayChangedListener);
/*     */         }
/* 175 */         localDisplayChangedListener.paletteChanged();
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (IllegalComponentStateException localIllegalComponentStateException)
/*     */       {
/*     */ 
/* 182 */         this.listeners.remove(localDisplayChangedListener);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\SunDisplayChanger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
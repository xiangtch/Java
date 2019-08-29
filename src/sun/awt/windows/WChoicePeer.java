/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Choice;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.awt.peer.ChoicePeer;
/*     */ import sun.awt.SunToolkit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WChoicePeer
/*     */   extends WComponentPeer
/*     */   implements ChoicePeer
/*     */ {
/*     */   private WindowListener windowListener;
/*     */   
/*     */   public Dimension getMinimumSize()
/*     */   {
/*  41 */     FontMetrics localFontMetrics = getFontMetrics(((Choice)this.target).getFont());
/*  42 */     Choice localChoice = (Choice)this.target;
/*  43 */     int i = 0;
/*  44 */     for (int j = localChoice.getItemCount(); j-- > 0;) {
/*  45 */       i = Math.max(localFontMetrics.stringWidth(localChoice.getItem(j)), i);
/*     */     }
/*  47 */     return new Dimension(28 + i, Math.max(localFontMetrics.getHeight() + 6, 15));
/*     */   }
/*     */   
/*     */   public boolean isFocusable() {
/*  51 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public native void select(int paramInt);
/*     */   
/*     */ 
/*     */   public void add(String paramString, int paramInt)
/*     */   {
/*  61 */     addItem(paramString, paramInt);
/*     */   }
/*     */   
/*     */   public boolean shouldClearRectBeforePaint()
/*     */   {
/*  66 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public native void removeAll();
/*     */   
/*     */ 
/*     */   public native void remove(int paramInt);
/*     */   
/*     */ 
/*     */   public void addItem(String paramString, int paramInt)
/*     */   {
/*  78 */     addItems(new String[] { paramString }, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */   public native void addItems(String[] paramArrayOfString, int paramInt);
/*     */   
/*     */ 
/*     */   public synchronized native void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */   
/*     */ 
/*     */   WChoicePeer(Choice paramChoice)
/*     */   {
/*  90 */     super(paramChoice);
/*     */   }
/*     */   
/*     */ 
/*     */   native void create(WComponentPeer paramWComponentPeer);
/*     */   
/*     */ 
/*     */   void initialize()
/*     */   {
/*  99 */     Choice localChoice = (Choice)this.target;
/* 100 */     int i = localChoice.getItemCount();
/* 101 */     if (i > 0) {
/* 102 */       localObject = new String[i];
/* 103 */       for (int j = 0; j < i; j++) {
/* 104 */         localObject[j] = localChoice.getItem(j);
/*     */       }
/* 106 */       addItems((String[])localObject, 0);
/* 107 */       if (localChoice.getSelectedIndex() >= 0) {
/* 108 */         select(localChoice.getSelectedIndex());
/*     */       }
/*     */     }
/*     */     
/* 112 */     Object localObject = SunToolkit.getContainingWindow((Component)this.target);
/* 113 */     if (localObject != null) {
/* 114 */       WWindowPeer localWWindowPeer = (WWindowPeer)((Window)localObject).getPeer();
/* 115 */       if (localWWindowPeer != null) {
/* 116 */         this.windowListener = new WindowAdapter()
/*     */         {
/*     */           public void windowIconified(WindowEvent paramAnonymousWindowEvent) {
/* 119 */             WChoicePeer.this.closeList();
/*     */           }
/*     */           
/*     */           public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
/* 123 */             WChoicePeer.this.closeList();
/*     */           }
/* 125 */         };
/* 126 */         localWWindowPeer.addWindowListener(this.windowListener);
/*     */       }
/*     */     }
/* 129 */     super.initialize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void disposeImpl()
/*     */   {
/* 137 */     Window localWindow = SunToolkit.getContainingWindow((Component)this.target);
/* 138 */     if (localWindow != null) {
/* 139 */       WWindowPeer localWWindowPeer = (WWindowPeer)localWindow.getPeer();
/* 140 */       if (localWWindowPeer != null) {
/* 141 */         localWWindowPeer.removeWindowListener(this.windowListener);
/*     */       }
/*     */     }
/* 144 */     super.disposeImpl();
/*     */   }
/*     */   
/*     */ 
/*     */   void handleAction(final int paramInt)
/*     */   {
/* 150 */     final Choice localChoice = (Choice)this.target;
/* 151 */     WToolkit.executeOnEventHandlerThread(localChoice, new Runnable()
/*     */     {
/*     */       public void run() {
/* 154 */         localChoice.select(paramInt);
/* 155 */         WChoicePeer.this.postEvent(new ItemEvent(localChoice, 701, localChoice
/* 156 */           .getItem(paramInt), 1));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   int getDropDownHeight() {
/* 162 */     Choice localChoice = (Choice)this.target;
/* 163 */     FontMetrics localFontMetrics = getFontMetrics(localChoice.getFont());
/* 164 */     int i = Math.min(localChoice.getItemCount(), 8);
/* 165 */     return localFontMetrics.getHeight() * i;
/*     */   }
/*     */   
/*     */   native void closeList();
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WChoicePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.awt.windows;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.List;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.peer.ListPeer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class WListPeer
/*     */   extends WComponentPeer
/*     */   implements ListPeer
/*     */ {
/*     */   private FontMetrics fm;
/*     */   
/*     */   public boolean isFocusable()
/*     */   {
/*  36 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int[] getSelectedIndexes()
/*     */   {
/*  43 */     List localList = (List)this.target;
/*  44 */     int i = localList.countItems();
/*  45 */     int[] arrayOfInt1 = new int[i];
/*  46 */     int j = 0;
/*  47 */     for (int k = 0; k < i; k++) {
/*  48 */       if (isSelected(k)) {
/*  49 */         arrayOfInt1[(j++)] = k;
/*     */       }
/*     */     }
/*  52 */     int[] arrayOfInt2 = new int[j];
/*  53 */     System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, j);
/*  54 */     return arrayOfInt2;
/*     */   }
/*     */   
/*     */ 
/*     */   public void add(String paramString, int paramInt)
/*     */   {
/*  60 */     addItem(paramString, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeAll()
/*     */   {
/*  66 */     clear();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setMultipleMode(boolean paramBoolean)
/*     */   {
/*  72 */     setMultipleSelections(paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */   public Dimension getPreferredSize(int paramInt)
/*     */   {
/*  78 */     return preferredSize(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */   public Dimension getMinimumSize(int paramInt)
/*     */   {
/*  84 */     return minimumSize(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  89 */   public void addItem(String paramString, int paramInt) { addItems(new String[] { paramString }, paramInt, this.fm.stringWidth(paramString)); }
/*     */   
/*     */   native void addItems(String[] paramArrayOfString, int paramInt1, int paramInt2);
/*     */   
/*     */   public native void delItems(int paramInt1, int paramInt2);
/*     */   
/*     */   public void clear() {
/*  96 */     List localList = (List)this.target;
/*  97 */     delItems(0, localList.countItems()); }
/*     */   
/*     */   public native void select(int paramInt);
/*     */   
/*     */   public native void deselect(int paramInt);
/*     */   
/*     */   public native void makeVisible(int paramInt);
/*     */   
/*     */   public native void setMultipleSelections(boolean paramBoolean);
/*     */   
/*     */   public native int getMaxWidth();
/*     */   
/* 109 */   public Dimension preferredSize(int paramInt) { if (this.fm == null) {
/* 110 */       localObject = (List)this.target;
/* 111 */       this.fm = getFontMetrics(((List)localObject).getFont());
/*     */     }
/* 113 */     Object localObject = minimumSize(paramInt);
/* 114 */     ((Dimension)localObject).width = Math.max(((Dimension)localObject).width, getMaxWidth() + 20);
/* 115 */     return (Dimension)localObject;
/*     */   }
/*     */   
/* 118 */   public Dimension minimumSize(int paramInt) { return new Dimension(20 + this.fm.stringWidth("0123456789abcde"), this.fm
/* 119 */       .getHeight() * paramInt + 4);
/*     */   }
/*     */   
/*     */ 
/*     */   WListPeer(List paramList)
/*     */   {
/* 125 */     super(paramList);
/*     */   }
/*     */   
/*     */ 
/*     */   native void create(WComponentPeer paramWComponentPeer);
/*     */   
/*     */   void initialize()
/*     */   {
/* 133 */     List localList = (List)this.target;
/*     */     
/* 135 */     this.fm = getFontMetrics(localList.getFont());
/*     */     
/*     */ 
/* 138 */     Font localFont = localList.getFont();
/* 139 */     if (localFont != null) {
/* 140 */       setFont(localFont);
/*     */     }
/*     */     
/*     */ 
/* 144 */     int i = localList.countItems();
/* 145 */     if (i > 0) {
/* 146 */       localObject = new String[i];
/* 147 */       j = 0;
/* 148 */       int k = 0;
/* 149 */       for (int m = 0; m < i; m++) {
/* 150 */         localObject[m] = localList.getItem(m);
/* 151 */         k = this.fm.stringWidth(localObject[m]);
/* 152 */         if (k > j) {
/* 153 */           j = k;
/*     */         }
/*     */       }
/* 156 */       addItems((String[])localObject, 0, j);
/*     */     }
/*     */     
/*     */ 
/* 160 */     setMultipleSelections(localList.allowsMultipleSelections());
/*     */     
/*     */ 
/* 163 */     Object localObject = localList.getSelectedIndexes();
/* 164 */     for (int j = 0; j < localObject.length; j++) {
/* 165 */       select(localObject[j]);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 174 */     j = localList.getVisibleIndex();
/* 175 */     if ((j < 0) && (localObject.length > 0)) {
/* 176 */       j = localObject[0];
/*     */     }
/* 178 */     if (j >= 0) {
/* 179 */       makeVisible(j);
/*     */     }
/*     */     
/* 182 */     super.initialize();
/*     */   }
/*     */   
/*     */   public boolean shouldClearRectBeforePaint()
/*     */   {
/* 187 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private native void updateMaxItemWidth();
/*     */   
/*     */ 
/*     */   native boolean isSelected(int paramInt);
/*     */   
/*     */   synchronized void _setFont(Font paramFont)
/*     */   {
/* 198 */     super._setFont(paramFont);
/* 199 */     this.fm = getFontMetrics(((List)this.target).getFont());
/* 200 */     updateMaxItemWidth();
/*     */   }
/*     */   
/*     */ 
/*     */   void handleAction(final int paramInt1, final long paramLong, int paramInt2)
/*     */   {
/* 206 */     final List localList = (List)this.target;
/* 207 */     WToolkit.executeOnEventHandlerThread(localList, new Runnable()
/*     */     {
/*     */       public void run() {
/* 210 */         localList.select(paramInt1);
/* 211 */         WListPeer.this.postEvent(new ActionEvent(WListPeer.this.target, 1001, localList
/* 212 */           .getItem(paramInt1), paramLong, this.val$modifiers));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   void handleListChanged(final int paramInt) {
/* 218 */     final List localList = (List)this.target;
/* 219 */     WToolkit.executeOnEventHandlerThread(localList, new Runnable()
/*     */     {
/*     */       public void run() {
/* 222 */         WListPeer.this.postEvent(new ItemEvent(localList, 701, 
/* 223 */           Integer.valueOf(paramInt), WListPeer.this
/* 224 */           .isSelected(paramInt) ? 1 : 2));
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WListPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.java2d;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class StateTrackableDelegate
/*     */   implements StateTrackable
/*     */ {
/*  47 */   public static final StateTrackableDelegate UNTRACKABLE_DELEGATE = new StateTrackableDelegate(State.UNTRACKABLE);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  55 */   public static final StateTrackableDelegate IMMUTABLE_DELEGATE = new StateTrackableDelegate(State.IMMUTABLE);
/*     */   
/*     */ 
/*     */   private State theState;
/*     */   
/*     */ 
/*     */   StateTracker theTracker;
/*     */   
/*     */ 
/*     */   private int numDynamicAgents;
/*     */   
/*     */ 
/*     */   public static StateTrackableDelegate createInstance(State paramState)
/*     */   {
/*  69 */     switch (paramState) {
/*     */     case UNTRACKABLE: 
/*  71 */       return UNTRACKABLE_DELEGATE;
/*     */     case STABLE: 
/*  73 */       return new StateTrackableDelegate(State.STABLE);
/*     */     case DYNAMIC: 
/*  75 */       return new StateTrackableDelegate(State.DYNAMIC);
/*     */     case IMMUTABLE: 
/*  77 */       return IMMUTABLE_DELEGATE;
/*     */     }
/*  79 */     throw new InternalError("unknown state");
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
/*     */   private StateTrackableDelegate(State paramState)
/*     */   {
/*  92 */     this.theState = paramState;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public State getState()
/*     */   {
/* 100 */     return this.theState;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized StateTracker getStateTracker()
/*     */   {
/* 108 */     Object localObject = this.theTracker;
/* 109 */     if (localObject == null) {
/* 110 */       switch (this.theState) {
/*     */       case IMMUTABLE: 
/* 112 */         localObject = StateTracker.ALWAYS_CURRENT;
/* 113 */         break;
/*     */       case STABLE: 
/* 115 */         localObject = new StateTracker() {
/*     */           public boolean isCurrent() {
/* 117 */             return StateTrackableDelegate.this.theTracker == this;
/*     */           }
/* 119 */         };
/* 120 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case UNTRACKABLE: 
/*     */       case DYNAMIC: 
/* 126 */         localObject = StateTracker.NEVER_CURRENT;
/*     */       }
/*     */       
/* 129 */       this.theTracker = ((StateTracker)localObject);
/*     */     }
/* 131 */     return (StateTracker)localObject;
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
/*     */   public synchronized void setImmutable()
/*     */   {
/* 144 */     if ((this.theState == State.UNTRACKABLE) || (this.theState == State.DYNAMIC)) {
/* 145 */       throw new IllegalStateException("UNTRACKABLE or DYNAMIC objects cannot become IMMUTABLE");
/*     */     }
/*     */     
/* 148 */     this.theState = State.IMMUTABLE;
/* 149 */     this.theTracker = null;
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
/*     */   public synchronized void setUntrackable()
/*     */   {
/* 164 */     if (this.theState == State.IMMUTABLE) {
/* 165 */       throw new IllegalStateException("IMMUTABLE objects cannot become UNTRACKABLE");
/*     */     }
/*     */     
/* 168 */     this.theState = State.UNTRACKABLE;
/* 169 */     this.theTracker = null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void addDynamicAgent()
/*     */   {
/* 195 */     if (this.theState == State.IMMUTABLE) {
/* 196 */       throw new IllegalStateException("Cannot change state from IMMUTABLE");
/*     */     }
/*     */     
/* 199 */     this.numDynamicAgents += 1;
/* 200 */     if (this.theState == State.STABLE) {
/* 201 */       this.theState = State.DYNAMIC;
/* 202 */       this.theTracker = null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected synchronized void removeDynamicAgent()
/*     */   {
/* 230 */     if ((--this.numDynamicAgents == 0) && (this.theState == State.DYNAMIC)) {
/* 231 */       this.theState = State.STABLE;
/* 232 */       this.theTracker = null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void markDirty()
/*     */   {
/* 254 */     this.theTracker = null;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\java2d\StateTrackableDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
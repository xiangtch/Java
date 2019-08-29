/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketException;
/*     */ import java.nio.channels.ClosedSelectorException;
/*     */ import java.nio.channels.IllegalSelectorException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.spi.AbstractSelectableChannel;
/*     */ import java.nio.channels.spi.AbstractSelector;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
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
/*     */ public abstract class SelectorImpl
/*     */   extends AbstractSelector
/*     */ {
/*     */   protected Set<SelectionKey> selectedKeys;
/*     */   protected HashSet<SelectionKey> keys;
/*     */   private Set<SelectionKey> publicKeys;
/*     */   private Set<SelectionKey> publicSelectedKeys;
/*     */   
/*     */   protected SelectorImpl(SelectorProvider paramSelectorProvider)
/*     */   {
/*  54 */     super(paramSelectorProvider);
/*  55 */     this.keys = new HashSet();
/*  56 */     this.selectedKeys = new HashSet();
/*  57 */     if (Util.atBugLevel("1.4")) {
/*  58 */       this.publicKeys = this.keys;
/*  59 */       this.publicSelectedKeys = this.selectedKeys;
/*     */     } else {
/*  61 */       this.publicKeys = Collections.unmodifiableSet(this.keys);
/*  62 */       this.publicSelectedKeys = Util.ungrowableSet(this.selectedKeys);
/*     */     }
/*     */   }
/*     */   
/*     */   public Set<SelectionKey> keys() {
/*  67 */     if ((!isOpen()) && (!Util.atBugLevel("1.4")))
/*  68 */       throw new ClosedSelectorException();
/*  69 */     return this.publicKeys;
/*     */   }
/*     */   
/*     */   public Set<SelectionKey> selectedKeys() {
/*  73 */     if ((!isOpen()) && (!Util.atBugLevel("1.4")))
/*  74 */       throw new ClosedSelectorException();
/*  75 */     return this.publicSelectedKeys;
/*     */   }
/*     */   
/*     */   protected abstract int doSelect(long paramLong)
/*     */     throws IOException;
/*     */   
/*     */   /* Error */
/*     */   private int lockAndDoSelect(long paramLong)
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_3
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: invokevirtual 147	sun/nio/ch/SelectorImpl:isOpen	()Z
/*     */     //   8: ifne +11 -> 19
/*     */     //   11: new 75	java/nio/channels/ClosedSelectorException
/*     */     //   14: dup
/*     */     //   15: invokespecial 138	java/nio/channels/ClosedSelectorException:<init>	()V
/*     */     //   18: athrow
/*     */     //   19: aload_0
/*     */     //   20: getfield 133	sun/nio/ch/SelectorImpl:publicKeys	Ljava/util/Set;
/*     */     //   23: dup
/*     */     //   24: astore 4
/*     */     //   26: monitorenter
/*     */     //   27: aload_0
/*     */     //   28: getfield 134	sun/nio/ch/SelectorImpl:publicSelectedKeys	Ljava/util/Set;
/*     */     //   31: dup
/*     */     //   32: astore 5
/*     */     //   34: monitorenter
/*     */     //   35: aload_0
/*     */     //   36: lload_1
/*     */     //   37: invokevirtual 148	sun/nio/ch/SelectorImpl:doSelect	(J)I
/*     */     //   40: aload 5
/*     */     //   42: monitorexit
/*     */     //   43: aload 4
/*     */     //   45: monitorexit
/*     */     //   46: aload_3
/*     */     //   47: monitorexit
/*     */     //   48: ireturn
/*     */     //   49: astore 6
/*     */     //   51: aload 5
/*     */     //   53: monitorexit
/*     */     //   54: aload 6
/*     */     //   56: athrow
/*     */     //   57: astore 7
/*     */     //   59: aload 4
/*     */     //   61: monitorexit
/*     */     //   62: aload 7
/*     */     //   64: athrow
/*     */     //   65: astore 8
/*     */     //   67: aload_3
/*     */     //   68: monitorexit
/*     */     //   69: aload 8
/*     */     //   71: athrow
/*     */     // Line number table:
/*     */     //   Java source line #81	-> byte code offset #0
/*     */     //   Java source line #82	-> byte code offset #4
/*     */     //   Java source line #83	-> byte code offset #11
/*     */     //   Java source line #84	-> byte code offset #19
/*     */     //   Java source line #85	-> byte code offset #27
/*     */     //   Java source line #86	-> byte code offset #35
/*     */     //   Java source line #87	-> byte code offset #49
/*     */     //   Java source line #88	-> byte code offset #57
/*     */     //   Java source line #89	-> byte code offset #65
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	72	0	this	SelectorImpl
/*     */     //   0	72	1	paramLong	long
/*     */     //   2	66	3	Ljava/lang/Object;	Object
/*     */     //   24	36	4	Ljava/lang/Object;	Object
/*     */     //   32	20	5	Ljava/lang/Object;	Object
/*     */     //   49	6	6	localObject1	Object
/*     */     //   57	6	7	localObject2	Object
/*     */     //   65	5	8	localObject3	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   35	43	49	finally
/*     */     //   49	54	49	finally
/*     */     //   27	46	57	finally
/*     */     //   49	62	57	finally
/*     */     //   4	48	65	finally
/*     */     //   49	69	65	finally
/*     */   }
/*     */   
/*     */   public int select(long paramLong)
/*     */     throws IOException
/*     */   {
/*  95 */     if (paramLong < 0L)
/*  96 */       throw new IllegalArgumentException("Negative timeout");
/*  97 */     return lockAndDoSelect(paramLong == 0L ? -1L : paramLong);
/*     */   }
/*     */   
/*     */   public int select() throws IOException {
/* 101 */     return select(0L);
/*     */   }
/*     */   
/*     */   public int selectNow() throws IOException {
/* 105 */     return lockAndDoSelect(0L);
/*     */   }
/*     */   
/*     */   public void implCloseSelector() throws IOException {
/* 109 */     wakeup();
/* 110 */     synchronized (this) {
/* 111 */       synchronized (this.publicKeys) {
/* 112 */         synchronized (this.publicSelectedKeys) {
/* 113 */           implClose();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected abstract void implClose()
/*     */     throws IOException;
/*     */   
/*     */   public void putEventOps(SelectionKeyImpl paramSelectionKeyImpl, int paramInt) {}
/*     */   
/*     */   protected final SelectionKey register(AbstractSelectableChannel paramAbstractSelectableChannel, int paramInt, Object paramObject)
/*     */   {
/* 127 */     if (!(paramAbstractSelectableChannel instanceof SelChImpl))
/* 128 */       throw new IllegalSelectorException();
/* 129 */     SelectionKeyImpl localSelectionKeyImpl = new SelectionKeyImpl((SelChImpl)paramAbstractSelectableChannel, this);
/* 130 */     localSelectionKeyImpl.attach(paramObject);
/* 131 */     synchronized (this.publicKeys) {
/* 132 */       implRegister(localSelectionKeyImpl);
/*     */     }
/* 134 */     localSelectionKeyImpl.interestOps(paramInt);
/* 135 */     return localSelectionKeyImpl;
/*     */   }
/*     */   
/*     */   protected abstract void implRegister(SelectionKeyImpl paramSelectionKeyImpl);
/*     */   
/*     */   void processDeregisterQueue() throws IOException
/*     */   {
/* 142 */     Set localSet = cancelledKeys();
/* 143 */     synchronized (localSet) {
/* 144 */       if (!localSet.isEmpty()) {
/* 145 */         Iterator localIterator = localSet.iterator();
/* 146 */         while (localIterator.hasNext()) {
/* 147 */           SelectionKeyImpl localSelectionKeyImpl = (SelectionKeyImpl)localIterator.next();
/*     */           try {
/* 149 */             implDereg(localSelectionKeyImpl);
/*     */           } catch (SocketException localSocketException) {
/* 151 */             throw new IOException("Error deregistering key", localSocketException);
/*     */           } finally {
/* 153 */             localIterator.remove();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected abstract void implDereg(SelectionKeyImpl paramSelectionKeyImpl)
/*     */     throws IOException;
/*     */   
/*     */   public abstract Selector wakeup();
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\SelectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
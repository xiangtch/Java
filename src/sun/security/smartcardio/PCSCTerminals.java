/*     */ package sun.security.smartcardio;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.smartcardio.CardException;
/*     */ import javax.smartcardio.CardTerminal;
/*     */ import javax.smartcardio.CardTerminals;
/*     */ import javax.smartcardio.CardTerminals.State;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class PCSCTerminals
/*     */   extends CardTerminals
/*     */ {
/*     */   private static long contextId;
/*     */   private Map<String, ReaderState> stateMap;
/*     */   
/*     */   static synchronized void initContext()
/*     */     throws PCSCException
/*     */   {
/*  55 */     if (contextId == 0L) {
/*  56 */       contextId = PCSC.SCardEstablishContext(0);
/*     */     }
/*     */   }
/*     */   
/*  60 */   private static final Map<String, Reference<TerminalImpl>> terminals = new HashMap();
/*     */   
/*     */   private static synchronized TerminalImpl implGetTerminal(String paramString)
/*     */   {
/*  64 */     Reference localReference = (Reference)terminals.get(paramString);
/*  65 */     TerminalImpl localTerminalImpl = localReference != null ? (TerminalImpl)localReference.get() : null;
/*  66 */     if (localTerminalImpl != null) {
/*  67 */       return localTerminalImpl;
/*     */     }
/*  69 */     localTerminalImpl = new TerminalImpl(contextId, paramString);
/*  70 */     terminals.put(paramString, new WeakReference(localTerminalImpl));
/*  71 */     return localTerminalImpl;
/*     */   }
/*     */   
/*     */   public synchronized List<CardTerminal> list(CardTerminals.State paramState) throws CardException
/*     */   {
/*  76 */     if (paramState == null) {
/*  77 */       throw new NullPointerException();
/*     */     }
/*     */     try {
/*  80 */       String[] arrayOfString1 = PCSC.SCardListReaders(contextId);
/*  81 */       ArrayList localArrayList = new ArrayList(arrayOfString1.length);
/*  82 */       if (this.stateMap == null)
/*     */       {
/*     */ 
/*  85 */         if (paramState == CardTerminals.State.CARD_INSERTION) {
/*  86 */           paramState = CardTerminals.State.CARD_PRESENT;
/*  87 */         } else if (paramState == CardTerminals.State.CARD_REMOVAL) {
/*  88 */           paramState = CardTerminals.State.CARD_ABSENT;
/*     */         }
/*     */       }
/*  91 */       for (String str : arrayOfString1) {
/*  92 */         TerminalImpl localTerminalImpl = implGetTerminal(str);
/*     */         ReaderState localReaderState;
/*  94 */         switch (paramState) {
/*     */         case ALL: 
/*  96 */           localArrayList.add(localTerminalImpl);
/*  97 */           break;
/*     */         case CARD_PRESENT: 
/*  99 */           if (localTerminalImpl.isCardPresent()) {
/* 100 */             localArrayList.add(localTerminalImpl);
/*     */           }
/*     */           break;
/*     */         case CARD_ABSENT: 
/* 104 */           if (!localTerminalImpl.isCardPresent()) {
/* 105 */             localArrayList.add(localTerminalImpl);
/*     */           }
/*     */           break;
/*     */         case CARD_INSERTION: 
/* 109 */           localReaderState = (ReaderState)this.stateMap.get(str);
/* 110 */           if ((localReaderState != null) && (localReaderState.isInsertion())) {
/* 111 */             localArrayList.add(localTerminalImpl);
/*     */           }
/*     */           break;
/*     */         case CARD_REMOVAL: 
/* 115 */           localReaderState = (ReaderState)this.stateMap.get(str);
/* 116 */           if ((localReaderState != null) && (localReaderState.isRemoval())) {
/* 117 */             localArrayList.add(localTerminalImpl);
/*     */           }
/*     */           break;
/*     */         default: 
/* 121 */           throw new CardException("Unknown state: " + paramState);
/*     */         }
/*     */       }
/* 124 */       return Collections.unmodifiableList(localArrayList);
/*     */     } catch (PCSCException localPCSCException) {
/* 126 */       throw new CardException("list() failed", localPCSCException);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ReaderState { private int current;
/*     */     private int previous;
/*     */     
/* 133 */     ReaderState() { this.current = 0;
/* 134 */       this.previous = 0;
/*     */     }
/*     */     
/* 137 */     int get() { return this.current; }
/*     */     
/*     */     void update(int paramInt) {
/* 140 */       this.previous = this.current;
/* 141 */       this.current = paramInt;
/*     */     }
/*     */     
/* 144 */     boolean isInsertion() { return (!present(this.previous)) && (present(this.current)); }
/*     */     
/*     */     boolean isRemoval() {
/* 147 */       return (present(this.previous)) && (!present(this.current));
/*     */     }
/*     */     
/* 150 */     static boolean present(int paramInt) { return (paramInt & 0x20) != 0; }
/*     */   }
/*     */   
/*     */   public synchronized boolean waitForChange(long paramLong) throws CardException
/*     */   {
/* 155 */     if (paramLong < 0L) {
/* 156 */       throw new IllegalArgumentException("Timeout must not be negative: " + paramLong);
/*     */     }
/*     */     
/* 159 */     if (this.stateMap == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 164 */       this.stateMap = new HashMap();
/* 165 */       waitForChange(0L);
/*     */     }
/* 167 */     if (paramLong == 0L) {
/* 168 */       paramLong = -1L;
/*     */     }
/*     */     try {
/* 171 */       String[] arrayOfString = PCSC.SCardListReaders(contextId);
/* 172 */       int i = arrayOfString.length;
/* 173 */       if (i == 0) {
/* 174 */         throw new IllegalStateException("No terminals available");
/*     */       }
/* 176 */       int[] arrayOfInt = new int[i];
/* 177 */       ReaderState[] arrayOfReaderState = new ReaderState[i];
/* 178 */       Object localObject; for (int j = 0; j < arrayOfString.length; j++) {
/* 179 */         localObject = arrayOfString[j];
/* 180 */         ReaderState localReaderState = (ReaderState)this.stateMap.get(localObject);
/* 181 */         if (localReaderState == null) {
/* 182 */           localReaderState = new ReaderState();
/*     */         }
/* 184 */         arrayOfReaderState[j] = localReaderState;
/* 185 */         arrayOfInt[j] = localReaderState.get();
/*     */       }
/* 187 */       arrayOfInt = PCSC.SCardGetStatusChange(contextId, paramLong, arrayOfInt, arrayOfString);
/* 188 */       this.stateMap.clear();
/* 189 */       for (j = 0; j < i; j++) {
/* 190 */         localObject = arrayOfReaderState[j];
/* 191 */         ((ReaderState)localObject).update(arrayOfInt[j]);
/* 192 */         this.stateMap.put(arrayOfString[j], localObject);
/*     */       }
/* 194 */       return true;
/*     */     } catch (PCSCException localPCSCException) {
/* 196 */       if (localPCSCException.code == -2146435062) {
/* 197 */         return false;
/*     */       }
/* 199 */       throw new CardException("waitForChange() failed", localPCSCException);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   static List<CardTerminal> waitForCards(List<? extends CardTerminal> paramList, long paramLong, boolean paramBoolean)
/*     */     throws CardException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: lload_1
/*     */     //   1: lconst_0
/*     */     //   2: lcmp
/*     */     //   3: ifne +15 -> 18
/*     */     //   6: ldc2_w 108
/*     */     //   9: lstore_1
/*     */     //   10: ldc2_w 108
/*     */     //   13: lstore 4
/*     */     //   15: goto +6 -> 21
/*     */     //   18: lconst_0
/*     */     //   19: lstore 4
/*     */     //   21: aload_0
/*     */     //   22: invokeinterface 252 1 0
/*     */     //   27: anewarray 118	java/lang/String
/*     */     //   30: astore 6
/*     */     //   32: iconst_0
/*     */     //   33: istore 7
/*     */     //   35: aload_0
/*     */     //   36: invokeinterface 254 1 0
/*     */     //   41: astore 8
/*     */     //   43: aload 8
/*     */     //   45: invokeinterface 250 1 0
/*     */     //   50: ifeq +80 -> 130
/*     */     //   53: aload 8
/*     */     //   55: invokeinterface 251 1 0
/*     */     //   60: checkcast 130	javax/smartcardio/CardTerminal
/*     */     //   63: astore 9
/*     */     //   65: aload 9
/*     */     //   67: instanceof 138
/*     */     //   70: ifne +37 -> 107
/*     */     //   73: new 114	java/lang/IllegalArgumentException
/*     */     //   76: dup
/*     */     //   77: new 119	java/lang/StringBuilder
/*     */     //   80: dup
/*     */     //   81: invokespecial 221	java/lang/StringBuilder:<init>	()V
/*     */     //   84: ldc 2
/*     */     //   86: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   89: aload 9
/*     */     //   91: invokevirtual 220	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */     //   94: invokevirtual 216	java/lang/Class:getName	()Ljava/lang/String;
/*     */     //   97: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   100: invokevirtual 222	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   103: invokespecial 217	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
/*     */     //   106: athrow
/*     */     //   107: aload 9
/*     */     //   109: checkcast 138	sun/security/smartcardio/TerminalImpl
/*     */     //   112: astore 10
/*     */     //   114: aload 6
/*     */     //   116: iload 7
/*     */     //   118: iinc 7 1
/*     */     //   121: aload 10
/*     */     //   123: getfield 215	sun/security/smartcardio/TerminalImpl:name	Ljava/lang/String;
/*     */     //   126: aastore
/*     */     //   127: goto -84 -> 43
/*     */     //   130: aload 6
/*     */     //   132: arraylength
/*     */     //   133: newarray <illegal type>
/*     */     //   135: astore 8
/*     */     //   137: aload 8
/*     */     //   139: iconst_0
/*     */     //   140: invokestatic 230	java/util/Arrays:fill	([II)V
/*     */     //   143: getstatic 211	sun/security/smartcardio/PCSCTerminals:contextId	J
/*     */     //   146: lload 4
/*     */     //   148: aload 8
/*     */     //   150: aload 6
/*     */     //   152: invokestatic 241	sun/security/smartcardio/PCSC:SCardGetStatusChange	(JJ[I[Ljava/lang/String;)[I
/*     */     //   155: astore 8
/*     */     //   157: lload_1
/*     */     //   158: lstore 4
/*     */     //   160: aconst_null
/*     */     //   161: astore 9
/*     */     //   163: iconst_0
/*     */     //   164: istore 7
/*     */     //   166: iload 7
/*     */     //   168: aload 6
/*     */     //   170: arraylength
/*     */     //   171: if_icmpge +63 -> 234
/*     */     //   174: aload 8
/*     */     //   176: iload 7
/*     */     //   178: iaload
/*     */     //   179: bipush 32
/*     */     //   181: iand
/*     */     //   182: ifeq +7 -> 189
/*     */     //   185: iconst_1
/*     */     //   186: goto +4 -> 190
/*     */     //   189: iconst_0
/*     */     //   190: istore 10
/*     */     //   192: iload 10
/*     */     //   194: iload_3
/*     */     //   195: if_icmpne +33 -> 228
/*     */     //   198: aload 9
/*     */     //   200: ifnonnull +12 -> 212
/*     */     //   203: new 122	java/util/ArrayList
/*     */     //   206: dup
/*     */     //   207: invokespecial 228	java/util/ArrayList:<init>	()V
/*     */     //   210: astore 9
/*     */     //   212: aload 9
/*     */     //   214: aload 6
/*     */     //   216: iload 7
/*     */     //   218: aaload
/*     */     //   219: invokestatic 243	sun/security/smartcardio/PCSCTerminals:implGetTerminal	(Ljava/lang/String;)Lsun/security/smartcardio/TerminalImpl;
/*     */     //   222: invokeinterface 253 2 0
/*     */     //   227: pop
/*     */     //   228: iinc 7 1
/*     */     //   231: goto -65 -> 166
/*     */     //   234: aload 9
/*     */     //   236: ifnull +9 -> 245
/*     */     //   239: aload 9
/*     */     //   241: invokestatic 232	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
/*     */     //   244: areturn
/*     */     //   245: goto -102 -> 143
/*     */     //   248: astore 9
/*     */     //   250: aload 9
/*     */     //   252: getfield 210	sun/security/smartcardio/PCSCException:code	I
/*     */     //   255: ldc 1
/*     */     //   257: if_icmpne +7 -> 264
/*     */     //   260: invokestatic 231	java/util/Collections:emptyList	()Ljava/util/List;
/*     */     //   263: areturn
/*     */     //   264: new 129	javax/smartcardio/CardException
/*     */     //   267: dup
/*     */     //   268: ldc 7
/*     */     //   270: aload 9
/*     */     //   272: invokespecial 235	javax/smartcardio/CardException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   275: athrow
/*     */     // Line number table:
/*     */     //   Java source line #210	-> byte code offset #0
/*     */     //   Java source line #211	-> byte code offset #6
/*     */     //   Java source line #212	-> byte code offset #10
/*     */     //   Java source line #217	-> byte code offset #18
/*     */     //   Java source line #220	-> byte code offset #21
/*     */     //   Java source line #221	-> byte code offset #32
/*     */     //   Java source line #222	-> byte code offset #35
/*     */     //   Java source line #223	-> byte code offset #65
/*     */     //   Java source line #224	-> byte code offset #73
/*     */     //   Java source line #225	-> byte code offset #91
/*     */     //   Java source line #227	-> byte code offset #107
/*     */     //   Java source line #228	-> byte code offset #114
/*     */     //   Java source line #229	-> byte code offset #127
/*     */     //   Java source line #231	-> byte code offset #130
/*     */     //   Java source line #232	-> byte code offset #137
/*     */     //   Java source line #240	-> byte code offset #143
/*     */     //   Java source line #241	-> byte code offset #157
/*     */     //   Java source line #243	-> byte code offset #160
/*     */     //   Java source line #244	-> byte code offset #163
/*     */     //   Java source line #245	-> byte code offset #174
/*     */     //   Java source line #246	-> byte code offset #192
/*     */     //   Java source line #247	-> byte code offset #198
/*     */     //   Java source line #248	-> byte code offset #203
/*     */     //   Java source line #250	-> byte code offset #212
/*     */     //   Java source line #244	-> byte code offset #228
/*     */     //   Java source line #254	-> byte code offset #234
/*     */     //   Java source line #255	-> byte code offset #239
/*     */     //   Java source line #257	-> byte code offset #245
/*     */     //   Java source line #258	-> byte code offset #248
/*     */     //   Java source line #259	-> byte code offset #250
/*     */     //   Java source line #260	-> byte code offset #260
/*     */     //   Java source line #262	-> byte code offset #264
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	276	0	paramList	List<? extends CardTerminal>
/*     */     //   0	276	1	paramLong	long
/*     */     //   0	276	3	paramBoolean	boolean
/*     */     //   13	146	4	l	long
/*     */     //   30	185	6	arrayOfString	String[]
/*     */     //   33	196	7	i	int
/*     */     //   41	134	8	localObject1	Object
/*     */     //   63	177	9	localObject2	Object
/*     */     //   248	23	9	localPCSCException	PCSCException
/*     */     //   112	10	10	localTerminalImpl	TerminalImpl
/*     */     //   190	6	10	bool	boolean
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   143	244	248	sun/security/smartcardio/PCSCException
/*     */     //   245	248	248	sun/security/smartcardio/PCSCException
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\smartcardio\PCSCTerminals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
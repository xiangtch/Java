/*     */ package sun.rmi.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.rmi.server.Operation;
/*     */ import java.rmi.server.RemoteCall;
/*     */ import java.rmi.server.RemoteObject;
/*     */ import java.rmi.server.RemoteRef;
/*     */ import java.security.AccessController;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.transport.Channel;
/*     */ import sun.rmi.transport.Connection;
/*     */ import sun.rmi.transport.LiveRef;
/*     */ import sun.rmi.transport.StreamRemoteCall;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UnicastRef
/*     */   implements RemoteRef
/*     */ {
/*  59 */   public static final Log clientRefLog = Log.getLog("sun.rmi.client.ref", "transport", Util.logLevel);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  65 */   public static final Log clientCallLog = Log.getLog("sun.rmi.client.call", "RMI", 
/*  66 */     ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.client.logCalls"))).booleanValue());
/*     */   
/*     */ 
/*     */   private static final long serialVersionUID = 8258372400816541186L;
/*     */   
/*     */ 
/*     */   protected LiveRef ref;
/*     */   
/*     */ 
/*     */ 
/*     */   public UnicastRef() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public UnicastRef(LiveRef paramLiveRef)
/*     */   {
/*  82 */     this.ref = paramLiveRef;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LiveRef getLiveRef()
/*     */   {
/*  94 */     return this.ref;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public Object invoke(java.rmi.Remote paramRemote, java.lang.reflect.Method paramMethod, Object[] paramArrayOfObject, long paramLong)
/*     */     throws Exception
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   3: getstatic 386	sun/rmi/runtime/Log:VERBOSE	Ljava/util/logging/Level;
/*     */     //   6: invokevirtual 425	sun/rmi/runtime/Log:isLoggable	(Ljava/util/logging/Level;)Z
/*     */     //   9: ifeq +31 -> 40
/*     */     //   12: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   15: getstatic 386	sun/rmi/runtime/Log:VERBOSE	Ljava/util/logging/Level;
/*     */     //   18: new 223	java/lang/StringBuilder
/*     */     //   21: dup
/*     */     //   22: invokespecial 412	java/lang/StringBuilder:<init>	()V
/*     */     //   25: ldc 21
/*     */     //   27: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   30: aload_2
/*     */     //   31: invokevirtual 416	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   34: invokevirtual 413	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   37: invokevirtual 426	sun/rmi/runtime/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;)V
/*     */     //   40: getstatic 387	sun/rmi/server/UnicastRef:clientCallLog	Lsun/rmi/runtime/Log;
/*     */     //   43: getstatic 386	sun/rmi/runtime/Log:VERBOSE	Ljava/util/logging/Level;
/*     */     //   46: invokevirtual 425	sun/rmi/runtime/Log:isLoggable	(Ljava/util/logging/Level;)Z
/*     */     //   49: ifeq +9 -> 58
/*     */     //   52: aload_0
/*     */     //   53: aload_1
/*     */     //   54: aload_2
/*     */     //   55: invokevirtual 432	sun/rmi/server/UnicastRef:logClientCall	(Ljava/lang/Object;Ljava/lang/Object;)V
/*     */     //   58: aload_0
/*     */     //   59: getfield 389	sun/rmi/server/UnicastRef:ref	Lsun/rmi/transport/LiveRef;
/*     */     //   62: invokevirtual 440	sun/rmi/transport/LiveRef:getChannel	()Lsun/rmi/transport/Channel;
/*     */     //   65: invokeinterface 469 1 0
/*     */     //   70: astore 6
/*     */     //   72: aconst_null
/*     */     //   73: astore 7
/*     */     //   75: iconst_1
/*     */     //   76: istore 8
/*     */     //   78: iconst_0
/*     */     //   79: istore 9
/*     */     //   81: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   84: getstatic 386	sun/rmi/runtime/Log:VERBOSE	Ljava/util/logging/Level;
/*     */     //   87: invokevirtual 425	sun/rmi/runtime/Log:isLoggable	(Ljava/util/logging/Level;)Z
/*     */     //   90: ifeq +32 -> 122
/*     */     //   93: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   96: getstatic 386	sun/rmi/runtime/Log:VERBOSE	Ljava/util/logging/Level;
/*     */     //   99: new 223	java/lang/StringBuilder
/*     */     //   102: dup
/*     */     //   103: invokespecial 412	java/lang/StringBuilder:<init>	()V
/*     */     //   106: ldc 22
/*     */     //   108: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   111: lload 4
/*     */     //   113: invokevirtual 414	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
/*     */     //   116: invokevirtual 413	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   119: invokevirtual 426	sun/rmi/runtime/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;)V
/*     */     //   122: new 242	sun/rmi/transport/StreamRemoteCall
/*     */     //   125: dup
/*     */     //   126: aload 6
/*     */     //   128: aload_0
/*     */     //   129: getfield 389	sun/rmi/server/UnicastRef:ref	Lsun/rmi/transport/LiveRef;
/*     */     //   132: invokevirtual 439	sun/rmi/transport/LiveRef:getObjID	()Ljava/rmi/server/ObjID;
/*     */     //   135: iconst_m1
/*     */     //   136: lload 4
/*     */     //   138: invokespecial 445	sun/rmi/transport/StreamRemoteCall:<init>	(Lsun/rmi/transport/Connection;Ljava/rmi/server/ObjID;IJ)V
/*     */     //   141: astore 7
/*     */     //   143: aload 7
/*     */     //   145: invokeinterface 468 1 0
/*     */     //   150: astore 10
/*     */     //   152: aload_0
/*     */     //   153: aload 10
/*     */     //   155: invokevirtual 430	sun/rmi/server/UnicastRef:marshalCustomCallData	(Ljava/io/ObjectOutput;)V
/*     */     //   158: aload_2
/*     */     //   159: invokevirtual 419	java/lang/reflect/Method:getParameterTypes	()[Ljava/lang/Class;
/*     */     //   162: astore 11
/*     */     //   164: iconst_0
/*     */     //   165: istore 12
/*     */     //   167: iload 12
/*     */     //   169: aload 11
/*     */     //   171: arraylength
/*     */     //   172: if_icmpge +23 -> 195
/*     */     //   175: aload 11
/*     */     //   177: iload 12
/*     */     //   179: aaload
/*     */     //   180: aload_3
/*     */     //   181: iload 12
/*     */     //   183: aaload
/*     */     //   184: aload 10
/*     */     //   186: invokestatic 433	sun/rmi/server/UnicastRef:marshalValue	(Ljava/lang/Class;Ljava/lang/Object;Ljava/io/ObjectOutput;)V
/*     */     //   189: iinc 12 1
/*     */     //   192: goto -25 -> 167
/*     */     //   195: goto +30 -> 225
/*     */     //   198: astore 10
/*     */     //   200: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   203: getstatic 385	sun/rmi/runtime/Log:BRIEF	Ljava/util/logging/Level;
/*     */     //   206: ldc 6
/*     */     //   208: aload 10
/*     */     //   210: invokevirtual 427	sun/rmi/runtime/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   213: new 227	java/rmi/MarshalException
/*     */     //   216: dup
/*     */     //   217: ldc 13
/*     */     //   219: aload 10
/*     */     //   221: invokespecial 421	java/rmi/MarshalException:<init>	(Ljava/lang/String;Ljava/lang/Exception;)V
/*     */     //   224: athrow
/*     */     //   225: aload 7
/*     */     //   227: invokeinterface 466 1 0
/*     */     //   232: aload_2
/*     */     //   233: invokevirtual 418	java/lang/reflect/Method:getReturnType	()Ljava/lang/Class;
/*     */     //   236: astore 10
/*     */     //   238: aload 10
/*     */     //   240: getstatic 384	java/lang/Void:TYPE	Ljava/lang/Class;
/*     */     //   243: if_acmpne +91 -> 334
/*     */     //   246: aconst_null
/*     */     //   247: astore 11
/*     */     //   249: aload 7
/*     */     //   251: invokeinterface 465 1 0
/*     */     //   256: goto +8 -> 264
/*     */     //   259: astore 12
/*     */     //   261: iconst_0
/*     */     //   262: istore 8
/*     */     //   264: iload 9
/*     */     //   266: ifne +65 -> 331
/*     */     //   269: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   272: getstatic 385	sun/rmi/runtime/Log:BRIEF	Ljava/util/logging/Level;
/*     */     //   275: invokevirtual 425	sun/rmi/runtime/Log:isLoggable	(Ljava/util/logging/Level;)Z
/*     */     //   278: ifeq +37 -> 315
/*     */     //   281: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   284: getstatic 385	sun/rmi/runtime/Log:BRIEF	Ljava/util/logging/Level;
/*     */     //   287: new 223	java/lang/StringBuilder
/*     */     //   290: dup
/*     */     //   291: invokespecial 412	java/lang/StringBuilder:<init>	()V
/*     */     //   294: ldc 18
/*     */     //   296: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   299: iload 8
/*     */     //   301: invokevirtual 415	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*     */     //   304: ldc 4
/*     */     //   306: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   309: invokevirtual 413	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   312: invokevirtual 426	sun/rmi/runtime/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;)V
/*     */     //   315: aload_0
/*     */     //   316: getfield 389	sun/rmi/server/UnicastRef:ref	Lsun/rmi/transport/LiveRef;
/*     */     //   319: invokevirtual 440	sun/rmi/transport/LiveRef:getChannel	()Lsun/rmi/transport/Channel;
/*     */     //   322: aload 6
/*     */     //   324: iload 8
/*     */     //   326: invokeinterface 470 3 0
/*     */     //   331: aload 11
/*     */     //   333: areturn
/*     */     //   334: aload 7
/*     */     //   336: invokeinterface 467 1 0
/*     */     //   341: astore 11
/*     */     //   343: aload 10
/*     */     //   345: aload 11
/*     */     //   347: invokestatic 434	sun/rmi/server/UnicastRef:unmarshalValue	(Ljava/lang/Class;Ljava/io/ObjectInput;)Ljava/lang/Object;
/*     */     //   350: astore 12
/*     */     //   352: iconst_1
/*     */     //   353: istore 9
/*     */     //   355: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   358: getstatic 385	sun/rmi/runtime/Log:BRIEF	Ljava/util/logging/Level;
/*     */     //   361: ldc 19
/*     */     //   363: invokevirtual 426	sun/rmi/runtime/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;)V
/*     */     //   366: aload_0
/*     */     //   367: getfield 389	sun/rmi/server/UnicastRef:ref	Lsun/rmi/transport/LiveRef;
/*     */     //   370: invokevirtual 440	sun/rmi/transport/LiveRef:getChannel	()Lsun/rmi/transport/Channel;
/*     */     //   373: aload 6
/*     */     //   375: iconst_1
/*     */     //   376: invokeinterface 470 3 0
/*     */     //   381: aload 12
/*     */     //   383: astore 13
/*     */     //   385: aload 7
/*     */     //   387: invokeinterface 465 1 0
/*     */     //   392: goto +8 -> 400
/*     */     //   395: astore 14
/*     */     //   397: iconst_0
/*     */     //   398: istore 8
/*     */     //   400: iload 9
/*     */     //   402: ifne +65 -> 467
/*     */     //   405: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   408: getstatic 385	sun/rmi/runtime/Log:BRIEF	Ljava/util/logging/Level;
/*     */     //   411: invokevirtual 425	sun/rmi/runtime/Log:isLoggable	(Ljava/util/logging/Level;)Z
/*     */     //   414: ifeq +37 -> 451
/*     */     //   417: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   420: getstatic 385	sun/rmi/runtime/Log:BRIEF	Ljava/util/logging/Level;
/*     */     //   423: new 223	java/lang/StringBuilder
/*     */     //   426: dup
/*     */     //   427: invokespecial 412	java/lang/StringBuilder:<init>	()V
/*     */     //   430: ldc 18
/*     */     //   432: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   435: iload 8
/*     */     //   437: invokevirtual 415	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*     */     //   440: ldc 4
/*     */     //   442: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   445: invokevirtual 413	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   448: invokevirtual 426	sun/rmi/runtime/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;)V
/*     */     //   451: aload_0
/*     */     //   452: getfield 389	sun/rmi/server/UnicastRef:ref	Lsun/rmi/transport/LiveRef;
/*     */     //   455: invokevirtual 440	sun/rmi/transport/LiveRef:getChannel	()Lsun/rmi/transport/Channel;
/*     */     //   458: aload 6
/*     */     //   460: iload 8
/*     */     //   462: invokeinterface 470 3 0
/*     */     //   467: aload 13
/*     */     //   469: areturn
/*     */     //   470: astore 10
/*     */     //   472: aload 7
/*     */     //   474: checkcast 242	sun/rmi/transport/StreamRemoteCall
/*     */     //   477: invokevirtual 442	sun/rmi/transport/StreamRemoteCall:discardPendingRefs	()V
/*     */     //   480: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   483: getstatic 385	sun/rmi/runtime/Log:BRIEF	Ljava/util/logging/Level;
/*     */     //   486: new 223	java/lang/StringBuilder
/*     */     //   489: dup
/*     */     //   490: invokespecial 412	java/lang/StringBuilder:<init>	()V
/*     */     //   493: aload 10
/*     */     //   495: invokevirtual 409	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */     //   498: invokevirtual 398	java/lang/Class:getName	()Ljava/lang/String;
/*     */     //   501: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   504: ldc 3
/*     */     //   506: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   509: invokevirtual 413	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   512: aload 10
/*     */     //   514: invokevirtual 427	sun/rmi/runtime/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   517: new 230	java/rmi/UnmarshalException
/*     */     //   520: dup
/*     */     //   521: ldc 14
/*     */     //   523: aload 10
/*     */     //   525: invokespecial 422	java/rmi/UnmarshalException:<init>	(Ljava/lang/String;Ljava/lang/Exception;)V
/*     */     //   528: athrow
/*     */     //   529: astore 15
/*     */     //   531: aload 7
/*     */     //   533: invokeinterface 465 1 0
/*     */     //   538: goto +8 -> 546
/*     */     //   541: astore 16
/*     */     //   543: iconst_0
/*     */     //   544: istore 8
/*     */     //   546: aload 15
/*     */     //   548: athrow
/*     */     //   549: astore 10
/*     */     //   551: aload 7
/*     */     //   553: ifnull +16 -> 569
/*     */     //   556: aload 7
/*     */     //   558: checkcast 242	sun/rmi/transport/StreamRemoteCall
/*     */     //   561: invokevirtual 443	sun/rmi/transport/StreamRemoteCall:getServerException	()Ljava/lang/Exception;
/*     */     //   564: aload 10
/*     */     //   566: if_acmpeq +6 -> 572
/*     */     //   569: iconst_0
/*     */     //   570: istore 8
/*     */     //   572: aload 10
/*     */     //   574: athrow
/*     */     //   575: astore 10
/*     */     //   577: iconst_0
/*     */     //   578: istore 8
/*     */     //   580: aload 10
/*     */     //   582: athrow
/*     */     //   583: astore 10
/*     */     //   585: iconst_0
/*     */     //   586: istore 8
/*     */     //   588: aload 10
/*     */     //   590: athrow
/*     */     //   591: astore 17
/*     */     //   593: iload 9
/*     */     //   595: ifne +65 -> 660
/*     */     //   598: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   601: getstatic 385	sun/rmi/runtime/Log:BRIEF	Ljava/util/logging/Level;
/*     */     //   604: invokevirtual 425	sun/rmi/runtime/Log:isLoggable	(Ljava/util/logging/Level;)Z
/*     */     //   607: ifeq +37 -> 644
/*     */     //   610: getstatic 388	sun/rmi/server/UnicastRef:clientRefLog	Lsun/rmi/runtime/Log;
/*     */     //   613: getstatic 385	sun/rmi/runtime/Log:BRIEF	Ljava/util/logging/Level;
/*     */     //   616: new 223	java/lang/StringBuilder
/*     */     //   619: dup
/*     */     //   620: invokespecial 412	java/lang/StringBuilder:<init>	()V
/*     */     //   623: ldc 18
/*     */     //   625: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   628: iload 8
/*     */     //   630: invokevirtual 415	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*     */     //   633: ldc 4
/*     */     //   635: invokevirtual 417	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   638: invokevirtual 413	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   641: invokevirtual 426	sun/rmi/runtime/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;)V
/*     */     //   644: aload_0
/*     */     //   645: getfield 389	sun/rmi/server/UnicastRef:ref	Lsun/rmi/transport/LiveRef;
/*     */     //   648: invokevirtual 440	sun/rmi/transport/LiveRef:getChannel	()Lsun/rmi/transport/Channel;
/*     */     //   651: aload 6
/*     */     //   653: iload 8
/*     */     //   655: invokeinterface 470 3 0
/*     */     //   660: aload 17
/*     */     //   662: athrow
/*     */     // Line number table:
/*     */     //   Java source line #121	-> byte code offset #0
/*     */     //   Java source line #122	-> byte code offset #12
/*     */     //   Java source line #125	-> byte code offset #40
/*     */     //   Java source line #126	-> byte code offset #52
/*     */     //   Java source line #129	-> byte code offset #58
/*     */     //   Java source line #130	-> byte code offset #72
/*     */     //   Java source line #131	-> byte code offset #75
/*     */     //   Java source line #136	-> byte code offset #78
/*     */     //   Java source line #139	-> byte code offset #81
/*     */     //   Java source line #140	-> byte code offset #93
/*     */     //   Java source line #144	-> byte code offset #122
/*     */     //   Java source line #148	-> byte code offset #143
/*     */     //   Java source line #149	-> byte code offset #152
/*     */     //   Java source line #150	-> byte code offset #158
/*     */     //   Java source line #151	-> byte code offset #164
/*     */     //   Java source line #152	-> byte code offset #175
/*     */     //   Java source line #151	-> byte code offset #189
/*     */     //   Java source line #158	-> byte code offset #195
/*     */     //   Java source line #154	-> byte code offset #198
/*     */     //   Java source line #155	-> byte code offset #200
/*     */     //   Java source line #157	-> byte code offset #213
/*     */     //   Java source line #161	-> byte code offset #225
/*     */     //   Java source line #164	-> byte code offset #232
/*     */     //   Java source line #165	-> byte code offset #238
/*     */     //   Java source line #166	-> byte code offset #246
/*     */     //   Java source line #197	-> byte code offset #249
/*     */     //   Java source line #206	-> byte code offset #256
/*     */     //   Java source line #198	-> byte code offset #259
/*     */     //   Java source line #205	-> byte code offset #261
/*     */     //   Java source line #248	-> byte code offset #264
/*     */     //   Java source line #249	-> byte code offset #269
/*     */     //   Java source line #250	-> byte code offset #281
/*     */     //   Java source line #253	-> byte code offset #315
/*     */     //   Java source line #166	-> byte code offset #331
/*     */     //   Java source line #167	-> byte code offset #334
/*     */     //   Java source line #174	-> byte code offset #343
/*     */     //   Java source line #179	-> byte code offset #352
/*     */     //   Java source line #182	-> byte code offset #355
/*     */     //   Java source line #185	-> byte code offset #366
/*     */     //   Java source line #187	-> byte code offset #381
/*     */     //   Java source line #197	-> byte code offset #385
/*     */     //   Java source line #206	-> byte code offset #392
/*     */     //   Java source line #198	-> byte code offset #395
/*     */     //   Java source line #205	-> byte code offset #397
/*     */     //   Java source line #248	-> byte code offset #400
/*     */     //   Java source line #249	-> byte code offset #405
/*     */     //   Java source line #250	-> byte code offset #417
/*     */     //   Java source line #253	-> byte code offset #451
/*     */     //   Java source line #187	-> byte code offset #467
/*     */     //   Java source line #189	-> byte code offset #470
/*     */     //   Java source line #191	-> byte code offset #472
/*     */     //   Java source line #192	-> byte code offset #480
/*     */     //   Java source line #193	-> byte code offset #495
/*     */     //   Java source line #192	-> byte code offset #514
/*     */     //   Java source line #194	-> byte code offset #517
/*     */     //   Java source line #196	-> byte code offset #529
/*     */     //   Java source line #197	-> byte code offset #531
/*     */     //   Java source line #206	-> byte code offset #538
/*     */     //   Java source line #198	-> byte code offset #541
/*     */     //   Java source line #205	-> byte code offset #543
/*     */     //   Java source line #207	-> byte code offset #546
/*     */     //   Java source line #209	-> byte code offset #549
/*     */     //   Java source line #218	-> byte code offset #551
/*     */     //   Java source line #219	-> byte code offset #561
/*     */     //   Java source line #221	-> byte code offset #569
/*     */     //   Java source line #223	-> byte code offset #572
/*     */     //   Java source line #225	-> byte code offset #575
/*     */     //   Java source line #233	-> byte code offset #577
/*     */     //   Java source line #234	-> byte code offset #580
/*     */     //   Java source line #236	-> byte code offset #583
/*     */     //   Java source line #240	-> byte code offset #585
/*     */     //   Java source line #241	-> byte code offset #588
/*     */     //   Java source line #248	-> byte code offset #591
/*     */     //   Java source line #249	-> byte code offset #598
/*     */     //   Java source line #250	-> byte code offset #610
/*     */     //   Java source line #253	-> byte code offset #644
/*     */     //   Java source line #255	-> byte code offset #660
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	663	0	this	UnicastRef
/*     */     //   0	663	1	paramRemote	java.rmi.Remote
/*     */     //   0	663	2	paramMethod	java.lang.reflect.Method
/*     */     //   0	663	3	paramArrayOfObject	Object[]
/*     */     //   0	663	4	paramLong	long
/*     */     //   70	582	6	localConnection	Connection
/*     */     //   73	484	7	localStreamRemoteCall	StreamRemoteCall
/*     */     //   76	578	8	bool	boolean
/*     */     //   79	515	9	i	int
/*     */     //   150	35	10	localObjectOutput	ObjectOutput
/*     */     //   198	22	10	localIOException1	IOException
/*     */     //   236	108	10	localClass	Class
/*     */     //   470	54	10	localIOException2	IOException
/*     */     //   549	24	10	localRuntimeException	RuntimeException
/*     */     //   575	6	10	localRemoteException	RemoteException
/*     */     //   583	6	10	localError	Error
/*     */     //   162	184	11	localObject1	Object
/*     */     //   165	25	12	j	int
/*     */     //   259	1	12	localIOException3	IOException
/*     */     //   350	32	12	localObject2	Object
/*     */     //   383	85	13	localObject3	Object
/*     */     //   395	1	14	localIOException4	IOException
/*     */     //   529	18	15	localObject4	Object
/*     */     //   541	1	16	localIOException5	IOException
/*     */     //   591	70	17	localObject5	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   143	195	198	java/io/IOException
/*     */     //   249	256	259	java/io/IOException
/*     */     //   385	392	395	java/io/IOException
/*     */     //   232	249	470	java/io/IOException
/*     */     //   232	249	470	java/lang/ClassNotFoundException
/*     */     //   334	385	470	java/io/IOException
/*     */     //   334	385	470	java/lang/ClassNotFoundException
/*     */     //   232	249	529	finally
/*     */     //   334	385	529	finally
/*     */     //   470	531	529	finally
/*     */     //   531	538	541	java/io/IOException
/*     */     //   81	264	549	java/lang/RuntimeException
/*     */     //   334	400	549	java/lang/RuntimeException
/*     */     //   470	549	549	java/lang/RuntimeException
/*     */     //   81	264	575	java/rmi/RemoteException
/*     */     //   334	400	575	java/rmi/RemoteException
/*     */     //   470	549	575	java/rmi/RemoteException
/*     */     //   81	264	583	java/lang/Error
/*     */     //   334	400	583	java/lang/Error
/*     */     //   470	549	583	java/lang/Error
/*     */     //   81	264	591	finally
/*     */     //   334	400	591	finally
/*     */     //   470	593	591	finally
/*     */   }
/*     */   
/*     */   protected void marshalCustomCallData(ObjectOutput paramObjectOutput)
/*     */     throws IOException
/*     */   {}
/*     */   
/*     */   protected static void marshalValue(Class<?> paramClass, Object paramObject, ObjectOutput paramObjectOutput)
/*     */     throws IOException
/*     */   {
/* 269 */     if (paramClass.isPrimitive()) {
/* 270 */       if (paramClass == Integer.TYPE) {
/* 271 */         paramObjectOutput.writeInt(((Integer)paramObject).intValue());
/* 272 */       } else if (paramClass == Boolean.TYPE) {
/* 273 */         paramObjectOutput.writeBoolean(((Boolean)paramObject).booleanValue());
/* 274 */       } else if (paramClass == Byte.TYPE) {
/* 275 */         paramObjectOutput.writeByte(((Byte)paramObject).byteValue());
/* 276 */       } else if (paramClass == Character.TYPE) {
/* 277 */         paramObjectOutput.writeChar(((Character)paramObject).charValue());
/* 278 */       } else if (paramClass == Short.TYPE) {
/* 279 */         paramObjectOutput.writeShort(((Short)paramObject).shortValue());
/* 280 */       } else if (paramClass == Long.TYPE) {
/* 281 */         paramObjectOutput.writeLong(((Long)paramObject).longValue());
/* 282 */       } else if (paramClass == Float.TYPE) {
/* 283 */         paramObjectOutput.writeFloat(((Float)paramObject).floatValue());
/* 284 */       } else if (paramClass == Double.TYPE) {
/* 285 */         paramObjectOutput.writeDouble(((Double)paramObject).doubleValue());
/*     */       } else {
/* 287 */         throw new Error("Unrecognized primitive type: " + paramClass);
/*     */       }
/*     */     } else {
/* 290 */       paramObjectOutput.writeObject(paramObject);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static Object unmarshalValue(Class<?> paramClass, ObjectInput paramObjectInput)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 301 */     if (paramClass.isPrimitive()) {
/* 302 */       if (paramClass == Integer.TYPE)
/* 303 */         return Integer.valueOf(paramObjectInput.readInt());
/* 304 */       if (paramClass == Boolean.TYPE)
/* 305 */         return Boolean.valueOf(paramObjectInput.readBoolean());
/* 306 */       if (paramClass == Byte.TYPE)
/* 307 */         return Byte.valueOf(paramObjectInput.readByte());
/* 308 */       if (paramClass == Character.TYPE)
/* 309 */         return Character.valueOf(paramObjectInput.readChar());
/* 310 */       if (paramClass == Short.TYPE)
/* 311 */         return Short.valueOf(paramObjectInput.readShort());
/* 312 */       if (paramClass == Long.TYPE)
/* 313 */         return Long.valueOf(paramObjectInput.readLong());
/* 314 */       if (paramClass == Float.TYPE)
/* 315 */         return Float.valueOf(paramObjectInput.readFloat());
/* 316 */       if (paramClass == Double.TYPE) {
/* 317 */         return Double.valueOf(paramObjectInput.readDouble());
/*     */       }
/* 319 */       throw new Error("Unrecognized primitive type: " + paramClass);
/*     */     }
/*     */     
/* 322 */     return paramObjectInput.readObject();
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
/*     */   public RemoteCall newCall(RemoteObject paramRemoteObject, Operation[] paramArrayOfOperation, int paramInt, long paramLong)
/*     */     throws RemoteException
/*     */   {
/* 336 */     clientRefLog.log(Log.BRIEF, "get connection");
/*     */     
/* 338 */     Connection localConnection = this.ref.getChannel().newConnection();
/*     */     try {
/* 340 */       clientRefLog.log(Log.VERBOSE, "create call context");
/*     */       
/*     */ 
/* 343 */       if (clientCallLog.isLoggable(Log.VERBOSE)) {
/* 344 */         logClientCall(paramRemoteObject, paramArrayOfOperation[paramInt]);
/*     */       }
/*     */       
/*     */ 
/* 348 */       StreamRemoteCall localStreamRemoteCall = new StreamRemoteCall(localConnection, this.ref.getObjID(), paramInt, paramLong);
/*     */       try {
/* 350 */         marshalCustomCallData(localStreamRemoteCall.getOutputStream());
/*     */       } catch (IOException localIOException) {
/* 352 */         throw new MarshalException("error marshaling custom call data");
/*     */       }
/*     */       
/* 355 */       return localStreamRemoteCall;
/*     */     } catch (RemoteException localRemoteException) {
/* 357 */       this.ref.getChannel().free(localConnection, false);
/* 358 */       throw localRemoteException;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void invoke(RemoteCall paramRemoteCall)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 373 */       clientRefLog.log(Log.VERBOSE, "execute call");
/*     */       
/* 375 */       paramRemoteCall.executeCall();
/*     */ 
/*     */     }
/*     */     catch (RemoteException localRemoteException)
/*     */     {
/*     */ 
/* 381 */       clientRefLog.log(Log.BRIEF, "exception: ", localRemoteException);
/* 382 */       free(paramRemoteCall, false);
/* 383 */       throw localRemoteException;
/*     */ 
/*     */     }
/*     */     catch (Error localError)
/*     */     {
/*     */ 
/* 389 */       clientRefLog.log(Log.BRIEF, "error: ", localError);
/* 390 */       free(paramRemoteCall, false);
/* 391 */       throw localError;
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (RuntimeException localRuntimeException)
/*     */     {
/*     */ 
/*     */ 
/* 399 */       clientRefLog.log(Log.BRIEF, "exception: ", localRuntimeException);
/* 400 */       free(paramRemoteCall, false);
/* 401 */       throw localRuntimeException;
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */ 
/* 408 */       clientRefLog.log(Log.BRIEF, "exception: ", localException);
/* 409 */       free(paramRemoteCall, true);
/*     */       
/* 411 */       throw localException;
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
/*     */   private void free(RemoteCall paramRemoteCall, boolean paramBoolean)
/*     */     throws RemoteException
/*     */   {
/* 426 */     Connection localConnection = ((StreamRemoteCall)paramRemoteCall).getConnection();
/* 427 */     this.ref.getChannel().free(localConnection, paramBoolean);
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
/*     */   public void done(RemoteCall paramRemoteCall)
/*     */     throws RemoteException
/*     */   {
/* 441 */     clientRefLog.log(Log.BRIEF, "free connection (reuse = true)");
/*     */     
/*     */ 
/* 444 */     free(paramRemoteCall, true);
/*     */     try
/*     */     {
/* 447 */       paramRemoteCall.done();
/*     */     }
/*     */     catch (IOException localIOException) {}
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
/*     */   void logClientCall(Object paramObject1, Object paramObject2)
/*     */   {
/* 462 */     clientCallLog.log(Log.VERBOSE, "outbound call: " + this.ref + " : " + paramObject1
/* 463 */       .getClass().getName() + this.ref
/* 464 */       .getObjID().toString() + ": " + paramObject2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getRefClass(ObjectOutput paramObjectOutput)
/*     */   {
/* 471 */     return "UnicastRef";
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeExternal(ObjectOutput paramObjectOutput)
/*     */     throws IOException
/*     */   {
/* 478 */     this.ref.write(paramObjectOutput, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void readExternal(ObjectInput paramObjectInput)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 489 */     this.ref = LiveRef.read(paramObjectInput, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String remoteToString()
/*     */   {
/* 497 */     return Util.getUnqualifiedName(getClass()) + " [liveRef: " + this.ref + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int remoteHashCode()
/*     */   {
/* 504 */     return this.ref.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean remoteEquals(RemoteRef paramRemoteRef)
/*     */   {
/* 510 */     if ((paramRemoteRef instanceof UnicastRef))
/* 511 */       return this.ref.remoteEquals(((UnicastRef)paramRemoteRef).ref);
/* 512 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\UnicastRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
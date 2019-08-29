/*     */ package sun.rmi.transport;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StreamCorruptedException;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.UnmarshalException;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.rmi.server.RemoteCall;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.server.UnicastRef;
/*     */ import sun.rmi.transport.tcp.TCPEndpoint;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StreamRemoteCall
/*     */   implements RemoteCall
/*     */ {
/*  50 */   private ConnectionInputStream in = null;
/*  51 */   private ConnectionOutputStream out = null;
/*     */   private Connection conn;
/*  53 */   private boolean resultStarted = false;
/*  54 */   private Exception serverException = null;
/*     */   
/*     */   public StreamRemoteCall(Connection paramConnection) {
/*  57 */     this.conn = paramConnection;
/*     */   }
/*     */   
/*     */   public StreamRemoteCall(Connection paramConnection, ObjID paramObjID, int paramInt, long paramLong) throws RemoteException
/*     */   {
/*     */     try
/*     */     {
/*  64 */       this.conn = paramConnection;
/*  65 */       Transport.transportLog.log(Log.VERBOSE, "write remote call header...");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  70 */       this.conn.getOutputStream().write(80);
/*  71 */       getOutputStream();
/*  72 */       paramObjID.write(this.out);
/*     */       
/*  74 */       this.out.writeInt(paramInt);
/*  75 */       this.out.writeLong(paramLong);
/*     */     } catch (IOException localIOException) {
/*  77 */       throw new MarshalException("Error marshaling call header", localIOException);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Connection getConnection()
/*     */   {
/*  85 */     return this.conn;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ObjectOutput getOutputStream()
/*     */     throws IOException
/*     */   {
/*  93 */     return getOutputStream(false);
/*     */   }
/*     */   
/*     */   private ObjectOutput getOutputStream(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  99 */     if (this.out == null) {
/* 100 */       Transport.transportLog.log(Log.VERBOSE, "getting output stream");
/*     */       
/* 102 */       this.out = new ConnectionOutputStream(this.conn, paramBoolean);
/*     */     }
/* 104 */     return this.out;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void releaseOutputStream()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 237	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
/*     */     //   4: ifnull +30 -> 34
/*     */     //   7: aload_0
/*     */     //   8: getfield 237	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
/*     */     //   11: invokevirtual 271	sun/rmi/transport/ConnectionOutputStream:flush	()V
/*     */     //   14: aload_0
/*     */     //   15: getfield 237	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
/*     */     //   18: invokevirtual 270	sun/rmi/transport/ConnectionOutputStream:done	()V
/*     */     //   21: goto +13 -> 34
/*     */     //   24: astore_1
/*     */     //   25: aload_0
/*     */     //   26: getfield 237	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
/*     */     //   29: invokevirtual 270	sun/rmi/transport/ConnectionOutputStream:done	()V
/*     */     //   32: aload_1
/*     */     //   33: athrow
/*     */     //   34: aload_0
/*     */     //   35: getfield 235	sun/rmi/transport/StreamRemoteCall:conn	Lsun/rmi/transport/Connection;
/*     */     //   38: invokeinterface 289 1 0
/*     */     //   43: aload_0
/*     */     //   44: aconst_null
/*     */     //   45: putfield 237	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
/*     */     //   48: goto +11 -> 59
/*     */     //   51: astore_2
/*     */     //   52: aload_0
/*     */     //   53: aconst_null
/*     */     //   54: putfield 237	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
/*     */     //   57: aload_2
/*     */     //   58: athrow
/*     */     //   59: return
/*     */     // Line number table:
/*     */     //   Java source line #113	-> byte code offset #0
/*     */     //   Java source line #115	-> byte code offset #7
/*     */     //   Java source line #117	-> byte code offset #14
/*     */     //   Java source line #118	-> byte code offset #21
/*     */     //   Java source line #117	-> byte code offset #24
/*     */     //   Java source line #118	-> byte code offset #32
/*     */     //   Java source line #120	-> byte code offset #34
/*     */     //   Java source line #122	-> byte code offset #43
/*     */     //   Java source line #123	-> byte code offset #48
/*     */     //   Java source line #122	-> byte code offset #51
/*     */     //   Java source line #123	-> byte code offset #57
/*     */     //   Java source line #124	-> byte code offset #59
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	60	0	this	StreamRemoteCall
/*     */     //   24	9	1	localObject1	Object
/*     */     //   51	7	2	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	14	24	finally
/*     */     //   0	43	51	finally
/*     */   }
/*     */   
/*     */   public ObjectInput getInputStream()
/*     */     throws IOException
/*     */   {
/* 131 */     if (this.in == null) {
/* 132 */       Transport.transportLog.log(Log.VERBOSE, "getting input stream");
/*     */       
/* 134 */       this.in = new ConnectionInputStream(this.conn.getInputStream());
/*     */     }
/* 136 */     return this.in;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void releaseInputStream()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 236	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
/*     */     //   4: ifnull +32 -> 36
/*     */     //   7: aload_0
/*     */     //   8: getfield 236	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
/*     */     //   11: invokevirtual 264	sun/rmi/transport/ConnectionInputStream:done	()V
/*     */     //   14: goto +4 -> 18
/*     */     //   17: astore_1
/*     */     //   18: aload_0
/*     */     //   19: getfield 236	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
/*     */     //   22: invokevirtual 266	sun/rmi/transport/ConnectionInputStream:registerRefs	()V
/*     */     //   25: aload_0
/*     */     //   26: getfield 236	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
/*     */     //   29: aload_0
/*     */     //   30: getfield 235	sun/rmi/transport/StreamRemoteCall:conn	Lsun/rmi/transport/Connection;
/*     */     //   33: invokevirtual 269	sun/rmi/transport/ConnectionInputStream:done	(Lsun/rmi/transport/Connection;)V
/*     */     //   36: aload_0
/*     */     //   37: getfield 235	sun/rmi/transport/StreamRemoteCall:conn	Lsun/rmi/transport/Connection;
/*     */     //   40: invokeinterface 288 1 0
/*     */     //   45: aload_0
/*     */     //   46: aconst_null
/*     */     //   47: putfield 236	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
/*     */     //   50: goto +11 -> 61
/*     */     //   53: astore_2
/*     */     //   54: aload_0
/*     */     //   55: aconst_null
/*     */     //   56: putfield 236	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
/*     */     //   59: aload_2
/*     */     //   60: athrow
/*     */     //   61: return
/*     */     // Line number table:
/*     */     //   Java source line #149	-> byte code offset #0
/*     */     //   Java source line #152	-> byte code offset #7
/*     */     //   Java source line #154	-> byte code offset #14
/*     */     //   Java source line #153	-> byte code offset #17
/*     */     //   Java source line #157	-> byte code offset #18
/*     */     //   Java source line #162	-> byte code offset #25
/*     */     //   Java source line #164	-> byte code offset #36
/*     */     //   Java source line #166	-> byte code offset #45
/*     */     //   Java source line #167	-> byte code offset #50
/*     */     //   Java source line #166	-> byte code offset #53
/*     */     //   Java source line #167	-> byte code offset #59
/*     */     //   Java source line #168	-> byte code offset #61
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	62	0	this	StreamRemoteCall
/*     */     //   17	1	1	localRuntimeException	RuntimeException
/*     */     //   53	7	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	14	17	java/lang/RuntimeException
/*     */     //   0	45	53	finally
/*     */   }
/*     */   
/*     */   public void discardPendingRefs()
/*     */   {
/* 174 */     this.in.discardRefs();
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
/*     */   public ObjectOutput getResultStream(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 188 */     if (this.resultStarted) {
/* 189 */       throw new StreamCorruptedException("result already in progress");
/*     */     }
/* 191 */     this.resultStarted = true;
/*     */     
/*     */ 
/*     */ 
/* 195 */     DataOutputStream localDataOutputStream = new DataOutputStream(this.conn.getOutputStream());
/* 196 */     localDataOutputStream.writeByte(81);
/* 197 */     getOutputStream(true);
/*     */     
/* 199 */     if (paramBoolean) {
/* 200 */       this.out.writeByte(1);
/*     */     } else
/* 202 */       this.out.writeByte(2);
/* 203 */     this.out.writeID();
/* 204 */     return this.out;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void executeCall()
/*     */     throws Exception
/*     */   {
/* 215 */     DGCAckHandler localDGCAckHandler = null;
/*     */     int i;
/* 217 */     try { if (this.out != null) {
/* 218 */         localDGCAckHandler = this.out.getDGCAckHandler();
/*     */       }
/* 220 */       releaseOutputStream();
/* 221 */       DataInputStream localDataInputStream = new DataInputStream(this.conn.getInputStream());
/* 222 */       int j = localDataInputStream.readByte();
/* 223 */       if (j != 81) {
/* 224 */         if (Transport.transportLog.isLoggable(Log.BRIEF)) {
/* 225 */           Transport.transportLog.log(Log.BRIEF, "transport return code invalid: " + j);
/*     */         }
/*     */         
/* 228 */         throw new UnmarshalException("Transport return code invalid");
/*     */       }
/* 230 */       getInputStream();
/* 231 */       i = this.in.readByte();
/* 232 */       this.in.readID();
/*     */     } catch (UnmarshalException localUnmarshalException) {
/* 234 */       throw localUnmarshalException;
/*     */     } catch (IOException localIOException) {
/* 236 */       throw new UnmarshalException("Error unmarshaling return header", localIOException);
/*     */     }
/*     */     finally {
/* 239 */       if (localDGCAckHandler != null) {
/* 240 */         localDGCAckHandler.release();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 245 */     switch (i)
/*     */     {
/*     */     case 1: 
/*     */       break;
/*     */     case 2: 
/*     */       Object localObject1;
/*     */       try {
/* 252 */         localObject1 = this.in.readObject();
/*     */       } catch (Exception localException) {
/* 254 */         throw new UnmarshalException("Error unmarshaling return", localException);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 259 */       if ((localObject1 instanceof Exception)) {
/* 260 */         exceptionReceivedFromServer((Exception)localObject1);
/*     */       } else {
/* 262 */         throw new UnmarshalException("Return type not Exception");
/*     */       }
/*     */       break;
/*     */     }
/* 266 */     if (Transport.transportLog.isLoggable(Log.BRIEF)) {
/* 267 */       Transport.transportLog.log(Log.BRIEF, "return code invalid: " + i);
/*     */     }
/*     */     
/* 270 */     throw new UnmarshalException("Return code invalid");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void exceptionReceivedFromServer(Exception paramException)
/*     */     throws Exception
/*     */   {
/* 280 */     this.serverException = paramException;
/*     */     
/* 282 */     StackTraceElement[] arrayOfStackTraceElement1 = paramException.getStackTrace();
/* 283 */     StackTraceElement[] arrayOfStackTraceElement2 = new Throwable().getStackTrace();
/* 284 */     StackTraceElement[] arrayOfStackTraceElement3 = new StackTraceElement[arrayOfStackTraceElement1.length + arrayOfStackTraceElement2.length];
/*     */     
/* 286 */     System.arraycopy(arrayOfStackTraceElement1, 0, arrayOfStackTraceElement3, 0, arrayOfStackTraceElement1.length);
/*     */     
/* 288 */     System.arraycopy(arrayOfStackTraceElement2, 0, arrayOfStackTraceElement3, arrayOfStackTraceElement1.length, arrayOfStackTraceElement2.length);
/*     */     
/* 290 */     paramException.setStackTrace(arrayOfStackTraceElement3);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 296 */     if (UnicastRef.clientCallLog.isLoggable(Log.BRIEF))
/*     */     {
/* 298 */       TCPEndpoint localTCPEndpoint = (TCPEndpoint)this.conn.getChannel().getEndpoint();
/* 299 */       UnicastRef.clientCallLog.log(Log.BRIEF, "outbound call received exception: [" + localTCPEndpoint
/* 300 */         .getHost() + ":" + localTCPEndpoint
/* 301 */         .getPort() + "] exception: ", paramException);
/*     */     }
/*     */     
/* 304 */     throw paramException;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Exception getServerException()
/*     */   {
/* 312 */     return this.serverException;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void done()
/*     */     throws IOException
/*     */   {
/* 320 */     releaseInputStream();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\StreamRemoteCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
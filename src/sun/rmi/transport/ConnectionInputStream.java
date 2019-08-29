/*     */ package sun.rmi.transport;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.UID;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.server.MarshalInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ConnectionInputStream
/*     */   extends MarshalInputStream
/*     */ {
/*  43 */   private boolean dgcAckNeeded = false;
/*     */   
/*     */ 
/*  46 */   private Map<Endpoint, List<LiveRef>> incomingRefTable = new HashMap(5);
/*     */   
/*     */ 
/*     */   private UID ackID;
/*     */   
/*     */ 
/*     */ 
/*     */   ConnectionInputStream(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*  56 */     super(paramInputStream);
/*     */   }
/*     */   
/*     */   void readID() throws IOException {
/*  60 */     this.ackID = UID.read(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void saveRef(LiveRef paramLiveRef)
/*     */   {
/*  70 */     Endpoint localEndpoint = paramLiveRef.getEndpoint();
/*     */     
/*     */ 
/*  73 */     Object localObject = (List)this.incomingRefTable.get(localEndpoint);
/*     */     
/*  75 */     if (localObject == null) {
/*  76 */       localObject = new ArrayList();
/*  77 */       this.incomingRefTable.put(localEndpoint, localObject);
/*     */     }
/*     */     
/*     */ 
/*  81 */     ((List)localObject).add(paramLiveRef);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void discardRefs()
/*     */   {
/*  89 */     this.incomingRefTable.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void registerRefs()
/*     */     throws IOException
/*     */   {
/*  99 */     if (!this.incomingRefTable.isEmpty())
/*     */     {
/* 101 */       for (Entry localEntry : this.incomingRefTable.entrySet()) {
/* 102 */         DGCClient.registerRefs((Endpoint)localEntry.getKey(), (List)localEntry.getValue());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void setAckNeeded()
/*     */   {
/* 112 */     this.dgcAckNeeded = true;
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
/*     */   void done(Connection paramConnection)
/*     */   {
/* 125 */     if (this.dgcAckNeeded) {
/* 126 */       Connection localConnection = null;
/* 127 */       Channel localChannel = null;
/* 128 */       boolean bool = true;
/*     */       
/* 130 */       DGCImpl.dgcLog.log(Log.VERBOSE, "send ack");
/*     */       try
/*     */       {
/* 133 */         localChannel = paramConnection.getChannel();
/* 134 */         localConnection = localChannel.newConnection();
/*     */         
/* 136 */         DataOutputStream localDataOutputStream = new DataOutputStream(localConnection.getOutputStream());
/* 137 */         localDataOutputStream.writeByte(84);
/* 138 */         if (this.ackID == null) {
/* 139 */           this.ackID = new UID();
/*     */         }
/* 141 */         this.ackID.write(localDataOutputStream);
/* 142 */         localConnection.releaseOutputStream();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 151 */         localConnection.getInputStream().available();
/* 152 */         localConnection.releaseInputStream();
/*     */       } catch (RemoteException localRemoteException1) {
/* 154 */         bool = false;
/*     */       } catch (IOException localIOException) {
/* 156 */         bool = false;
/*     */       }
/*     */       try {
/* 159 */         if (localConnection != null) {
/* 160 */           localChannel.free(localConnection, bool);
/*     */         }
/*     */       }
/*     */       catch (RemoteException localRemoteException2) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\ConnectionInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
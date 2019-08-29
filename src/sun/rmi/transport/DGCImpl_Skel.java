/*     */ package sun.rmi.transport;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.UnmarshalException;
/*     */ import java.rmi.dgc.Lease;
/*     */ import java.rmi.dgc.VMID;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.rmi.server.Operation;
/*     */ import java.rmi.server.RemoteCall;
/*     */ import java.rmi.server.Skeleton;
/*     */ import java.rmi.server.SkeletonMismatchException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DGCImpl_Skel
/*     */   implements Skeleton
/*     */ {
/*  35 */   private static final Operation[] operations = { new Operation("void clean(java.rmi.server.ObjID[], long, java.rmi.dgc.VMID, boolean)"), new Operation("java.rmi.dgc.Lease dirty(java.rmi.server.ObjID[], long, java.rmi.dgc.Lease)") };
/*     */   
/*     */ 
/*     */   private static final long interfaceHash = -669196253586618813L;
/*     */   
/*     */ 
/*     */   public Operation[] getOperations()
/*     */   {
/*  43 */     return (Operation[])operations.clone();
/*     */   }
/*     */   
/*     */   public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong) throws Exception
/*     */   {
/*  48 */     if (paramLong != -669196253586618813L) {
/*  49 */       throw new SkeletonMismatchException("interface hash mismatch");
/*     */     }
/*  51 */     DGCImpl localDGCImpl = (DGCImpl)paramRemote;
/*  52 */     ObjID[] arrayOfObjID; long l; Object localObject1; switch (paramInt)
/*     */     {
/*     */     case 0: 
/*     */       boolean bool;
/*     */       
/*     */ 
/*     */       try
/*     */       {
/*  60 */         ObjectInput localObjectInput2 = paramRemoteCall.getInputStream();
/*  61 */         arrayOfObjID = (ObjID[])localObjectInput2.readObject();
/*  62 */         l = localObjectInput2.readLong();
/*  63 */         localObject1 = (VMID)localObjectInput2.readObject();
/*  64 */         bool = localObjectInput2.readBoolean();
/*     */       } catch (IOException localIOException2) {
/*  66 */         throw new UnmarshalException("error unmarshalling arguments", localIOException2);
/*     */       } catch (ClassNotFoundException localClassNotFoundException2) {
/*  68 */         throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException2);
/*     */       } finally {
/*  70 */         paramRemoteCall.releaseInputStream();
/*     */       }
/*  72 */       localDGCImpl.clean(arrayOfObjID, l, (VMID)localObject1, bool);
/*     */       try {
/*  74 */         paramRemoteCall.getResultStream(true);
/*     */       } catch (IOException localIOException3) {
/*  76 */         throw new MarshalException("error marshalling return", localIOException3);
/*     */       }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     case 1: 
/*     */       try
/*     */       {
/*  87 */         ObjectInput localObjectInput1 = paramRemoteCall.getInputStream();
/*  88 */         arrayOfObjID = (ObjID[])localObjectInput1.readObject();
/*  89 */         l = localObjectInput1.readLong();
/*  90 */         localObject1 = (Lease)localObjectInput1.readObject();
/*     */       } catch (IOException localIOException1) {
/*  92 */         throw new UnmarshalException("error unmarshalling arguments", localIOException1);
/*     */       } catch (ClassNotFoundException localClassNotFoundException1) {
/*  94 */         throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException1);
/*     */       } finally {
/*  96 */         paramRemoteCall.releaseInputStream();
/*     */       }
/*  98 */       Lease localLease = localDGCImpl.dirty(arrayOfObjID, l, (Lease)localObject1);
/*     */       try {
/* 100 */         ObjectOutput localObjectOutput = paramRemoteCall.getResultStream(true);
/* 101 */         localObjectOutput.writeObject(localLease);
/*     */       } catch (IOException localIOException4) {
/* 103 */         throw new MarshalException("error marshalling return", localIOException4);
/*     */       }
/*     */     
/*     */ 
/*     */ 
/*     */     default: 
/* 109 */       throw new UnmarshalException("invalid method number");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\DGCImpl_Skel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
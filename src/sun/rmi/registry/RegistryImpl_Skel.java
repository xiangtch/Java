/*     */ package sun.rmi.registry;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.UnmarshalException;
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
/*     */ public final class RegistryImpl_Skel
/*     */   implements Skeleton
/*     */ {
/*  45 */   private static final Operation[] operations = { new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)") };
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long interfaceHash = 4905912898345647071L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Operation[] getOperations()
/*     */   {
/*  56 */     return (Operation[])operations.clone();
/*     */   }
/*     */   
/*     */   public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong) throws Exception
/*     */   {
/*  61 */     if (paramLong != 4905912898345647071L) {
/*  62 */       throw new SkeletonMismatchException("interface hash mismatch");
/*     */     }
/*  64 */     RegistryImpl localRegistryImpl = (RegistryImpl)paramRemote;
/*  65 */     Object localObject1; Object localObject2; Object localObject3; switch (paramInt)
/*     */     {
/*     */ 
/*     */     case 0: 
/*  69 */       RegistryImpl.checkAccess("Registry.bind");
/*     */       
/*     */ 
/*     */       try
/*     */       {
/*  74 */         ObjectInput localObjectInput2 = paramRemoteCall.getInputStream();
/*  75 */         localObject1 = (String)localObjectInput2.readObject();
/*  76 */         localObject2 = (Remote)localObjectInput2.readObject();
/*     */       } catch (IOException|ClassNotFoundException localIOException5) {
/*  78 */         throw new UnmarshalException("error unmarshalling arguments", localIOException5);
/*     */       } finally {
/*  80 */         paramRemoteCall.releaseInputStream();
/*     */       }
/*  82 */       localRegistryImpl.bind((String)localObject1, (Remote)localObject2);
/*     */       try {
/*  84 */         paramRemoteCall.getResultStream(true);
/*     */       } catch (IOException localIOException6) {
/*  86 */         throw new MarshalException("error marshalling return", localIOException6);
/*     */       }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     case 1: 
/*  93 */       paramRemoteCall.releaseInputStream();
/*  94 */       localObject1 = localRegistryImpl.list();
/*     */       try {
/*  96 */         localObject2 = paramRemoteCall.getResultStream(true);
/*  97 */         ((ObjectOutput)localObject2).writeObject(localObject1);
/*     */       } catch (IOException localIOException1) {
/*  99 */         throw new MarshalException("error marshalling return", localIOException1);
/*     */       }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     case 2: 
/*     */       try
/*     */       {
/* 108 */         ObjectInput localObjectInput1 = paramRemoteCall.getInputStream();
/* 109 */         localObject1 = (String)localObjectInput1.readObject();
/*     */       } catch (IOException|ClassNotFoundException localIOException2) {
/* 111 */         throw new UnmarshalException("error unmarshalling arguments", localIOException2);
/*     */       } finally {
/* 113 */         paramRemoteCall.releaseInputStream();
/*     */       }
/* 115 */       localObject3 = localRegistryImpl.lookup((String)localObject1);
/*     */       try {
/* 117 */         ObjectOutput localObjectOutput = paramRemoteCall.getResultStream(true);
/* 118 */         localObjectOutput.writeObject(localObject3);
/*     */       } catch (IOException localIOException7) {
/* 120 */         throw new MarshalException("error marshalling return", localIOException7);
/*     */       }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     case 3: 
/* 128 */       RegistryImpl.checkAccess("Registry.rebind");
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 133 */         ObjectInput localObjectInput3 = paramRemoteCall.getInputStream();
/* 134 */         localObject1 = (String)localObjectInput3.readObject();
/* 135 */         localObject3 = (Remote)localObjectInput3.readObject();
/*     */       } catch (IOException|ClassNotFoundException localIOException8) {
/* 137 */         throw new UnmarshalException("error unmarshalling arguments", localIOException8);
/*     */       } finally {
/* 139 */         paramRemoteCall.releaseInputStream();
/*     */       }
/* 141 */       localRegistryImpl.rebind((String)localObject1, (Remote)localObject3);
/*     */       try {
/* 143 */         paramRemoteCall.getResultStream(true);
/*     */       } catch (IOException localIOException9) {
/* 145 */         throw new MarshalException("error marshalling return", localIOException9);
/*     */       }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     case 4: 
/* 153 */       RegistryImpl.checkAccess("Registry.unbind");
/*     */       
/*     */       try
/*     */       {
/* 157 */         localObject3 = paramRemoteCall.getInputStream();
/* 158 */         localObject1 = (String)((ObjectInput)localObject3).readObject();
/*     */       } catch (IOException|ClassNotFoundException localIOException3) {
/* 160 */         throw new UnmarshalException("error unmarshalling arguments", localIOException3);
/*     */       } finally {
/* 162 */         paramRemoteCall.releaseInputStream();
/*     */       }
/* 164 */       localRegistryImpl.unbind((String)localObject1);
/*     */       try {
/* 166 */         paramRemoteCall.getResultStream(true);
/*     */       } catch (IOException localIOException4) {
/* 168 */         throw new MarshalException("error marshalling return", localIOException4);
/*     */       }
/*     */     
/*     */ 
/*     */ 
/*     */     default: 
/* 174 */       throw new UnmarshalException("invalid method number");
/*     */     }
/*     */     
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\registry\RegistryImpl_Skel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
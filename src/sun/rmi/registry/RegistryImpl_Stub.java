/*     */ package sun.rmi.registry;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.rmi.AccessException;
/*     */ import java.rmi.AlreadyBoundException;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.NotBoundException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.UnexpectedException;
/*     */ import java.rmi.UnmarshalException;
/*     */ import java.rmi.registry.Registry;
/*     */ import java.rmi.server.Operation;
/*     */ import java.rmi.server.RemoteCall;
/*     */ import java.rmi.server.RemoteRef;
/*     */ import java.rmi.server.RemoteStub;
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
/*     */ public final class RegistryImpl_Stub
/*     */   extends RemoteStub
/*     */   implements Registry, Remote
/*     */ {
/*  35 */   private static final Operation[] operations = { new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)") };
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long interfaceHash = 4905912898345647071L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RegistryImpl_Stub() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RegistryImpl_Stub(RemoteRef paramRemoteRef)
/*     */   {
/*  51 */     super(paramRemoteRef);
/*     */   }
/*     */   
/*     */ 
/*     */   public void bind(String paramString, Remote paramRemote)
/*     */     throws AccessException, AlreadyBoundException, RemoteException
/*     */   {
/*     */     try
/*     */     {
/*  60 */       RemoteCall localRemoteCall = this.ref.newCall(this, operations, 0, 4905912898345647071L);
/*     */       try {
/*  62 */         ObjectOutput localObjectOutput = localRemoteCall.getOutputStream();
/*  63 */         localObjectOutput.writeObject(paramString);
/*  64 */         localObjectOutput.writeObject(paramRemote);
/*     */       } catch (IOException localIOException) {
/*  66 */         throw new MarshalException("error marshalling arguments", localIOException);
/*     */       }
/*  68 */       this.ref.invoke(localRemoteCall);
/*  69 */       this.ref.done(localRemoteCall);
/*     */     } catch (RuntimeException localRuntimeException) {
/*  71 */       throw localRuntimeException;
/*     */     } catch (RemoteException localRemoteException) {
/*  73 */       throw localRemoteException;
/*     */     } catch (AlreadyBoundException localAlreadyBoundException) {
/*  75 */       throw localAlreadyBoundException;
/*     */     } catch (Exception localException) {
/*  77 */       throw new UnexpectedException("undeclared checked exception", localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] list() throws AccessException, RemoteException
/*     */   {
/*     */     try
/*     */     {
/*  85 */       RemoteCall localRemoteCall = this.ref.newCall(this, operations, 1, 4905912898345647071L);
/*  86 */       this.ref.invoke(localRemoteCall);
/*     */       String[] arrayOfString;
/*     */       try {
/*  89 */         ObjectInput localObjectInput = localRemoteCall.getInputStream();
/*  90 */         arrayOfString = (String[])localObjectInput.readObject();
/*     */       } catch (IOException localIOException) {
/*  92 */         throw new UnmarshalException("error unmarshalling return", localIOException);
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/*  94 */         throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
/*     */       } finally {
/*  96 */         this.ref.done(localRemoteCall);
/*     */       }
/*  98 */       return arrayOfString;
/*     */     } catch (RuntimeException localRuntimeException) {
/* 100 */       throw localRuntimeException;
/*     */     } catch (RemoteException localRemoteException) {
/* 102 */       throw localRemoteException;
/*     */     } catch (Exception localException) {
/* 104 */       throw new UnexpectedException("undeclared checked exception", localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public Remote lookup(String paramString) throws AccessException, NotBoundException, RemoteException
/*     */   {
/*     */     try
/*     */     {
/* 112 */       RemoteCall localRemoteCall = this.ref.newCall(this, operations, 2, 4905912898345647071L);
/*     */       try {
/* 114 */         ObjectOutput localObjectOutput = localRemoteCall.getOutputStream();
/* 115 */         localObjectOutput.writeObject(paramString);
/*     */       } catch (IOException localIOException1) {
/* 117 */         throw new MarshalException("error marshalling arguments", localIOException1);
/*     */       }
/* 119 */       this.ref.invoke(localRemoteCall);
/*     */       Remote localRemote;
/*     */       try {
/* 122 */         ObjectInput localObjectInput = localRemoteCall.getInputStream();
/* 123 */         localRemote = (Remote)localObjectInput.readObject();
/*     */       } catch (IOException localIOException2) {
/* 125 */         throw new UnmarshalException("error unmarshalling return", localIOException2);
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/* 127 */         throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
/*     */       } finally {
/* 129 */         this.ref.done(localRemoteCall);
/*     */       }
/* 131 */       return localRemote;
/*     */     } catch (RuntimeException localRuntimeException) {
/* 133 */       throw localRuntimeException;
/*     */     } catch (RemoteException localRemoteException) {
/* 135 */       throw localRemoteException;
/*     */     } catch (NotBoundException localNotBoundException) {
/* 137 */       throw localNotBoundException;
/*     */     } catch (Exception localException) {
/* 139 */       throw new UnexpectedException("undeclared checked exception", localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public void rebind(String paramString, Remote paramRemote) throws AccessException, RemoteException
/*     */   {
/*     */     try
/*     */     {
/* 147 */       RemoteCall localRemoteCall = this.ref.newCall(this, operations, 3, 4905912898345647071L);
/*     */       try {
/* 149 */         ObjectOutput localObjectOutput = localRemoteCall.getOutputStream();
/* 150 */         localObjectOutput.writeObject(paramString);
/* 151 */         localObjectOutput.writeObject(paramRemote);
/*     */       } catch (IOException localIOException) {
/* 153 */         throw new MarshalException("error marshalling arguments", localIOException);
/*     */       }
/* 155 */       this.ref.invoke(localRemoteCall);
/* 156 */       this.ref.done(localRemoteCall);
/*     */     } catch (RuntimeException localRuntimeException) {
/* 158 */       throw localRuntimeException;
/*     */     } catch (RemoteException localRemoteException) {
/* 160 */       throw localRemoteException;
/*     */     } catch (Exception localException) {
/* 162 */       throw new UnexpectedException("undeclared checked exception", localException);
/*     */     }
/*     */   }
/*     */   
/*     */   public void unbind(String paramString) throws AccessException, NotBoundException, RemoteException
/*     */   {
/*     */     try
/*     */     {
/* 170 */       RemoteCall localRemoteCall = this.ref.newCall(this, operations, 4, 4905912898345647071L);
/*     */       try {
/* 172 */         ObjectOutput localObjectOutput = localRemoteCall.getOutputStream();
/* 173 */         localObjectOutput.writeObject(paramString);
/*     */       } catch (IOException localIOException) {
/* 175 */         throw new MarshalException("error marshalling arguments", localIOException);
/*     */       }
/* 177 */       this.ref.invoke(localRemoteCall);
/* 178 */       this.ref.done(localRemoteCall);
/*     */     } catch (RuntimeException localRuntimeException) {
/* 180 */       throw localRuntimeException;
/*     */     } catch (RemoteException localRemoteException) {
/* 182 */       throw localRemoteException;
/*     */     } catch (NotBoundException localNotBoundException) {
/* 184 */       throw localNotBoundException;
/*     */     } catch (Exception localException) {
/* 186 */       throw new UnexpectedException("undeclared checked exception", localException);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\registry\RegistryImpl_Stub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
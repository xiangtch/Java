/*     */ package sun.rmi.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.net.MalformedURLException;
/*     */ import java.rmi.ConnectException;
/*     */ import java.rmi.ConnectIOException;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.NoSuchObjectException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.ServerError;
/*     */ import java.rmi.ServerException;
/*     */ import java.rmi.StubNotFoundException;
/*     */ import java.rmi.UnknownHostException;
/*     */ import java.rmi.UnmarshalException;
/*     */ import java.rmi.activation.ActivateFailedException;
/*     */ import java.rmi.activation.ActivationDesc;
/*     */ import java.rmi.activation.ActivationException;
/*     */ import java.rmi.activation.ActivationID;
/*     */ import java.rmi.activation.UnknownObjectException;
/*     */ import java.rmi.server.Operation;
/*     */ import java.rmi.server.RMIClassLoader;
/*     */ import java.rmi.server.RemoteCall;
/*     */ import java.rmi.server.RemoteObject;
/*     */ import java.rmi.server.RemoteObjectInvocationHandler;
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
/*     */ 
/*     */ public class ActivatableRef
/*     */   implements RemoteRef
/*     */ {
/*     */   private static final long serialVersionUID = 7579060052569229166L;
/*     */   protected ActivationID id;
/*     */   protected RemoteRef ref;
/*  51 */   transient boolean force = false;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int MAX_RETRIES = 3;
/*     */   
/*     */ 
/*     */   private static final String versionComplaint = "activation requires 1.2 stubs";
/*     */   
/*     */ 
/*     */ 
/*     */   public ActivatableRef() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public ActivatableRef(ActivationID paramActivationID, RemoteRef paramRemoteRef)
/*     */   {
/*  68 */     this.id = paramActivationID;
/*  69 */     this.ref = paramRemoteRef;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Remote getStub(ActivationDesc paramActivationDesc, ActivationID paramActivationID)
/*     */     throws StubNotFoundException
/*     */   {
/*  81 */     String str = paramActivationDesc.getClassName();
/*     */     
/*     */     try
/*     */     {
/*  85 */       Class localClass = RMIClassLoader.loadClass(paramActivationDesc.getLocation(), str);
/*  86 */       ActivatableRef localActivatableRef = new ActivatableRef(paramActivationID, null);
/*  87 */       return Util.createProxy(localClass, localActivatableRef, false);
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/*  90 */       throw new StubNotFoundException("class implements an illegal remote interface", localIllegalArgumentException);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException)
/*     */     {
/*  94 */       throw new StubNotFoundException("unable to load class: " + str, localClassNotFoundException);
/*     */     }
/*     */     catch (MalformedURLException localMalformedURLException) {
/*  97 */       throw new StubNotFoundException("malformed URL", localMalformedURLException);
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
/*     */   public Object invoke(Remote paramRemote, Method paramMethod, Object[] paramArrayOfObject, long paramLong)
/*     */     throws Exception
/*     */   {
/* 123 */     boolean bool = false;
/*     */     
/* 125 */     Object localObject2 = null;
/*     */     
/*     */ 
/*     */     Object localObject1;
/*     */     
/*     */ 
/* 131 */     synchronized (this) {
/* 132 */       if (this.ref == null) {
/* 133 */         localObject1 = activate(bool);
/* 134 */         bool = true;
/*     */       } else {
/* 136 */         localObject1 = this.ref;
/*     */       }
/*     */     }
/*     */     
/* 140 */     for (int i = 3; i > 0; i--)
/*     */     {
/*     */       try {
/* 143 */         return ((RemoteRef)localObject1).invoke(paramRemote, paramMethod, paramArrayOfObject, paramLong);
/*     */ 
/*     */       }
/*     */       catch (NoSuchObjectException localNoSuchObjectException)
/*     */       {
/* 148 */         localObject2 = localNoSuchObjectException;
/*     */ 
/*     */       }
/*     */       catch (ConnectException localConnectException)
/*     */       {
/* 153 */         localObject2 = localConnectException;
/*     */ 
/*     */       }
/*     */       catch (UnknownHostException localUnknownHostException)
/*     */       {
/* 158 */         localObject2 = localUnknownHostException;
/*     */ 
/*     */       }
/*     */       catch (ConnectIOException localConnectIOException)
/*     */       {
/*     */ 
/* 164 */         localObject2 = localConnectIOException;
/*     */ 
/*     */       }
/*     */       catch (MarshalException localMarshalException)
/*     */       {
/*     */ 
/* 170 */         throw localMarshalException;
/*     */ 
/*     */       }
/*     */       catch (ServerError localServerError)
/*     */       {
/* 175 */         throw localServerError;
/*     */ 
/*     */       }
/*     */       catch (ServerException localServerException)
/*     */       {
/* 180 */         throw localServerException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (RemoteException localRemoteException)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 195 */         synchronized (this) {
/* 196 */           if (localObject1 == this.ref) {
/* 197 */             this.ref = null;
/*     */           }
/*     */         }
/*     */         
/* 201 */         throw localRemoteException;
/*     */       }
/*     */       
/* 204 */       if (i > 1)
/*     */       {
/*     */ 
/*     */ 
/* 208 */         synchronized (this) {
/* 209 */           if ((((RemoteRef)localObject1).remoteEquals(this.ref)) || (this.ref == null)) {
/* 210 */             ??? = activate(bool);
/*     */             
/* 212 */             if ((((RemoteRef)???).remoteEquals((RemoteRef)localObject1)) && ((localObject2 instanceof NoSuchObjectException)) && (!bool))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 220 */               ??? = activate(true);
/*     */             }
/*     */             
/* 223 */             localObject1 = ???;
/* 224 */             bool = true;
/*     */           } else {
/* 226 */             localObject1 = this.ref;
/* 227 */             bool = false;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 236 */     throw ((Throwable)localObject2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized RemoteRef getRef()
/*     */     throws RemoteException
/*     */   {
/* 245 */     if (this.ref == null) {
/* 246 */       this.ref = activate(false);
/*     */     }
/*     */     
/* 249 */     return this.ref;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private RemoteRef activate(boolean paramBoolean)
/*     */     throws RemoteException
/*     */   {
/* 261 */     assert (Thread.holdsLock(this));
/*     */     
/* 263 */     this.ref = null;
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
/*     */     try
/*     */     {
/* 277 */       Remote localRemote = this.id.activate(paramBoolean);
/* 278 */       ActivatableRef localActivatableRef = null;
/*     */       
/* 280 */       if ((localRemote instanceof RemoteStub)) {
/* 281 */         localActivatableRef = (ActivatableRef)((RemoteStub)localRemote).getRef();
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 291 */         RemoteObjectInvocationHandler localRemoteObjectInvocationHandler = (RemoteObjectInvocationHandler)Proxy.getInvocationHandler(localRemote);
/* 292 */         localActivatableRef = (ActivatableRef)localRemoteObjectInvocationHandler.getRef();
/*     */       }
/* 294 */       this.ref = localActivatableRef.ref;
/* 295 */       return this.ref;
/*     */     }
/*     */     catch (ConnectException localConnectException) {
/* 298 */       throw new ConnectException("activation failed", localConnectException);
/*     */     } catch (RemoteException localRemoteException) {
/* 300 */       throw new ConnectIOException("activation failed", localRemoteException);
/*     */     } catch (UnknownObjectException localUnknownObjectException) {
/* 302 */       throw new NoSuchObjectException("object not registered");
/*     */     } catch (ActivationException localActivationException) {
/* 304 */       throw new ActivateFailedException("activation failed", localActivationException);
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
/*     */   public synchronized RemoteCall newCall(RemoteObject paramRemoteObject, Operation[] paramArrayOfOperation, int paramInt, long paramLong)
/*     */     throws RemoteException
/*     */   {
/* 318 */     throw new UnsupportedOperationException("activation requires 1.2 stubs");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void invoke(RemoteCall paramRemoteCall)
/*     */     throws Exception
/*     */   {
/* 327 */     throw new UnsupportedOperationException("activation requires 1.2 stubs");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void done(RemoteCall paramRemoteCall)
/*     */     throws RemoteException
/*     */   {
/* 335 */     throw new UnsupportedOperationException("activation requires 1.2 stubs");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getRefClass(ObjectOutput paramObjectOutput)
/*     */   {
/* 343 */     return "ActivatableRef";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void writeExternal(ObjectOutput paramObjectOutput)
/*     */     throws IOException
/*     */   {
/* 351 */     RemoteRef localRemoteRef = this.ref;
/*     */     
/* 353 */     paramObjectOutput.writeObject(this.id);
/* 354 */     if (localRemoteRef == null) {
/* 355 */       paramObjectOutput.writeUTF("");
/*     */     } else {
/* 357 */       paramObjectOutput.writeUTF(localRemoteRef.getRefClass(paramObjectOutput));
/* 358 */       localRemoteRef.writeExternal(paramObjectOutput);
/*     */     }
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
/* 370 */     this.id = ((ActivationID)paramObjectInput.readObject());
/* 371 */     this.ref = null;
/* 372 */     String str = paramObjectInput.readUTF();
/*     */     
/* 374 */     if (str.equals("")) return;
/*     */     try
/*     */     {
/* 377 */       Class localClass = Class.forName("sun.rmi.server." + str);
/*     */       
/* 379 */       this.ref = ((RemoteRef)localClass.newInstance());
/* 380 */       this.ref.readExternal(paramObjectInput);
/*     */     } catch (InstantiationException localInstantiationException) {
/* 382 */       throw new UnmarshalException("Unable to create remote reference", localInstantiationException);
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 385 */       throw new UnmarshalException("Illegal access creating remote reference");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String remoteToString()
/*     */   {
/* 394 */     return Util.getUnqualifiedName(getClass()) + " [remoteRef: " + this.ref + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int remoteHashCode()
/*     */   {
/* 402 */     return this.id.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean remoteEquals(RemoteRef paramRemoteRef)
/*     */   {
/* 408 */     if ((paramRemoteRef instanceof ActivatableRef))
/* 409 */       return this.id.equals(((ActivatableRef)paramRemoteRef).id);
/* 410 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\ActivatableRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
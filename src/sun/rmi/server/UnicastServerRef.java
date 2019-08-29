/*     */ package sun.rmi.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutput;
/*     */ import java.io.ObjectStreamClass;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.rmi.AccessException;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.ServerError;
/*     */ import java.rmi.ServerException;
/*     */ import java.rmi.UnmarshalException;
/*     */ import java.rmi.server.ExportException;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.rmi.server.RemoteCall;
/*     */ import java.rmi.server.RemoteRef;
/*     */ import java.rmi.server.RemoteStub;
/*     */ import java.rmi.server.ServerNotActiveException;
/*     */ import java.rmi.server.ServerRef;
/*     */ import java.rmi.server.Skeleton;
/*     */ import java.rmi.server.SkeletonNotFoundException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import sun.misc.ObjectInputFilter;
/*     */ import sun.misc.ObjectInputFilter.Config;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.transport.LiveRef;
/*     */ import sun.rmi.transport.StreamRemoteCall;
/*     */ import sun.rmi.transport.Target;
/*     */ import sun.rmi.transport.tcp.TCPTransport;
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
/*     */ 
/*     */ public class UnicastServerRef
/*     */   extends UnicastRef
/*     */   implements ServerRef, Dispatcher
/*     */ {
/*  84 */   public static final boolean logCalls = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.rmi.server.logCalls"))).booleanValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  89 */   public static final Log callLog = Log.getLog("sun.rmi.server.call", "RMI", logCalls);
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = -7384275867073752268L;
/*     */   
/*     */ 
/*  96 */   private static final boolean wantExceptionLog = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.exceptionTrace"))).booleanValue();
/*     */   
/*     */ 
/*  99 */   private boolean forceStubUse = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */   private static final boolean suppressStackTraces = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.suppressStackTraces"))).booleanValue();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private transient Skeleton skel;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final transient ObjectInputFilter filter;
/*     */   
/*     */ 
/*     */ 
/* 120 */   private transient Map<Long, Method> hashToMethod_Map = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 126 */   private static final WeakClassHashMap<Map<Long, Method>> hashToMethod_Maps = new HashToMethod_Maps();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 131 */   private static final Map<Class<?>, ?> withoutSkeletons = Collections.synchronizedMap(new WeakHashMap());
/*     */   
/* 133 */   private final AtomicInteger methodCallIDCount = new AtomicInteger(0);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UnicastServerRef()
/*     */   {
/* 140 */     this.filter = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UnicastServerRef(LiveRef paramLiveRef)
/*     */   {
/* 149 */     super(paramLiveRef);
/* 150 */     this.filter = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UnicastServerRef(LiveRef paramLiveRef, ObjectInputFilter paramObjectInputFilter)
/*     */   {
/* 158 */     super(paramLiveRef);
/* 159 */     this.filter = paramObjectInputFilter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UnicastServerRef(int paramInt)
/*     */   {
/* 167 */     super(new LiveRef(paramInt));
/* 168 */     this.filter = null;
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
/*     */   public UnicastServerRef(boolean paramBoolean)
/*     */   {
/* 185 */     this(0);
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
/*     */   public RemoteStub exportObject(Remote paramRemote, Object paramObject)
/*     */     throws RemoteException
/*     */   {
/* 206 */     this.forceStubUse = true;
/* 207 */     return (RemoteStub)exportObject(paramRemote, paramObject, false);
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
/*     */   public Remote exportObject(Remote paramRemote, Object paramObject, boolean paramBoolean)
/*     */     throws RemoteException
/*     */   {
/* 221 */     Class localClass = paramRemote.getClass();
/*     */     Remote localRemote;
/*     */     try
/*     */     {
/* 225 */       localRemote = Util.createProxy(localClass, getClientRef(), this.forceStubUse);
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 227 */       throw new ExportException("remote object implements illegal remote interface", localIllegalArgumentException);
/*     */     }
/*     */     
/* 230 */     if ((localRemote instanceof RemoteStub)) {
/* 231 */       setSkeleton(paramRemote);
/*     */     }
/*     */     
/*     */ 
/* 235 */     Target localTarget = new Target(paramRemote, this, localRemote, this.ref.getObjID(), paramBoolean);
/* 236 */     this.ref.exportObject(localTarget);
/* 237 */     this.hashToMethod_Map = ((Map)hashToMethod_Maps.get(localClass));
/* 238 */     return localRemote;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getClientHost()
/*     */     throws ServerNotActiveException
/*     */   {
/* 249 */     return TCPTransport.getClientHost();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSkeleton(Remote paramRemote)
/*     */     throws RemoteException
/*     */   {
/* 256 */     if (!withoutSkeletons.containsKey(paramRemote.getClass())) {
/*     */       try {
/* 258 */         this.skel = Util.createSkeleton(paramRemote);
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (SkeletonNotFoundException localSkeletonNotFoundException)
/*     */       {
/*     */ 
/*     */ 
/* 266 */         withoutSkeletons.put(paramRemote.getClass(), null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*     */       ObjectInput localObjectInput;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       int i;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 292 */         localObjectInput = paramRemoteCall.getInputStream();
/* 293 */         i = localObjectInput.readInt();
/*     */       } catch (Exception localException1) {
/* 295 */         throw new UnmarshalException("error unmarshalling call header", localException1);
/*     */       }
/*     */       
/* 298 */       if (i >= 0) {
/* 299 */         if (this.skel != null) {
/* 300 */           oldDispatch(paramRemote, paramRemoteCall, i);
/* 301 */           return;
/*     */         }
/* 303 */         throw new UnmarshalException("skeleton class not found but required for client version");
/*     */       }
/*     */       
/*     */       long l;
/*     */       try
/*     */       {
/* 309 */         l = localObjectInput.readLong();
/*     */       } catch (Exception localException2) {
/* 311 */         throw new UnmarshalException("error unmarshalling call header", localException2);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 322 */       localObject2 = (MarshalInputStream)localObjectInput;
/* 323 */       ((MarshalInputStream)localObject2).skipDefaultResolveClass();
/*     */       
/* 325 */       localObject3 = (Method)this.hashToMethod_Map.get(Long.valueOf(l));
/* 326 */       if (localObject3 == null) {
/* 327 */         throw new UnmarshalException("unrecognized method hash: method not supported by remote object");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 332 */       logCall(paramRemote, localObject3);
/*     */       
/*     */ 
/* 335 */       Object[] arrayOfObject = null;
/*     */       try {
/* 337 */         unmarshalCustomCallData(localObjectInput);
/* 338 */         arrayOfObject = unmarshalParameters(paramRemote, (Method)localObject3, (MarshalInputStream)localObject2);
/*     */ 
/*     */       }
/*     */       catch (AccessException localAccessException)
/*     */       {
/* 343 */         ((StreamRemoteCall)paramRemoteCall).discardPendingRefs();
/* 344 */         throw localAccessException;
/*     */       }
/*     */       catch (IOException|ClassNotFoundException localIOException1) {
/* 347 */         ((StreamRemoteCall)paramRemoteCall).discardPendingRefs();
/* 348 */         throw new UnmarshalException("error unmarshalling arguments", localIOException1);
/*     */       }
/*     */       finally {
/* 351 */         paramRemoteCall.releaseInputStream();
/*     */       }
/*     */       
/*     */       Object localObject4;
/*     */       try
/*     */       {
/* 357 */         localObject4 = ((Method)localObject3).invoke(paramRemote, arrayOfObject);
/*     */       } catch (InvocationTargetException localInvocationTargetException) {
/* 359 */         throw localInvocationTargetException.getTargetException();
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 364 */         ObjectOutput localObjectOutput = paramRemoteCall.getResultStream(true);
/* 365 */         Class localClass = ((Method)localObject3).getReturnType();
/* 366 */         if (localClass != Void.TYPE) {
/* 367 */           marshalValue(localClass, localObject4, localObjectOutput);
/*     */         }
/*     */       } catch (IOException localIOException2) {
/* 370 */         throw new MarshalException("error marshalling return", localIOException2);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/*     */ 
/*     */ 
/* 381 */       Object localObject2 = localThrowable;
/* 382 */       logCallException(localThrowable);
/*     */       
/* 384 */       Object localObject3 = paramRemoteCall.getResultStream(false);
/* 385 */       Object localObject1; if ((localThrowable instanceof Error)) {
/* 386 */         localObject1 = new ServerError("Error occurred in server thread", (Error)localThrowable);
/*     */       }
/* 388 */       else if ((localObject1 instanceof RemoteException)) {
/* 389 */         localObject1 = new ServerException("RemoteException occurred in server thread", (Exception)localObject1);
/*     */       }
/*     */       
/*     */ 
/* 393 */       if (suppressStackTraces) {
/* 394 */         clearStackTraces((Throwable)localObject1);
/*     */       }
/* 396 */       ((ObjectOutput)localObject3).writeObject(localObject1);
/*     */       
/*     */ 
/*     */ 
/* 400 */       if ((localObject2 instanceof AccessException)) {
/* 401 */         throw new IOException("Connection is not reusable", (Throwable)localObject2);
/*     */       }
/*     */     } finally {
/* 404 */       paramRemoteCall.releaseInputStream();
/* 405 */       paramRemoteCall.releaseOutputStream();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void unmarshalCustomCallData(ObjectInput paramObjectInput)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 415 */     if ((this.filter != null) && ((paramObjectInput instanceof ObjectInputStream)))
/*     */     {
/*     */ 
/* 418 */       final ObjectInputStream localObjectInputStream = (ObjectInputStream)paramObjectInput;
/*     */       
/* 420 */       AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Void run() {
/* 423 */           ObjectInputFilter.Config.setObjectInputFilter(localObjectInputStream, UnicastServerRef.this.filter);
/* 424 */           return null;
/*     */         }
/*     */       });
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
/*     */   private void oldDispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt)
/*     */     throws Exception
/*     */   {
/* 450 */     ObjectInput localObjectInput = paramRemoteCall.getInputStream();
/*     */     try {
/* 452 */       Class localClass = Class.forName("sun.rmi.transport.DGCImpl_Skel");
/* 453 */       if (localClass.isAssignableFrom(this.skel.getClass())) {
/* 454 */         ((MarshalInputStream)localObjectInput).useCodebaseOnly();
/*     */       }
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {}
/*     */     long l;
/*     */     try {
/* 459 */       l = localObjectInput.readLong();
/*     */     } catch (Exception localException) {
/* 461 */       throw new UnmarshalException("error unmarshalling call header", localException);
/*     */     }
/*     */     
/*     */ 
/* 465 */     logCall(paramRemote, this.skel.getOperations()[paramInt]);
/* 466 */     unmarshalCustomCallData(localObjectInput);
/*     */     
/* 468 */     this.skel.dispatch(paramRemote, paramRemoteCall, paramInt, l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void clearStackTraces(Throwable paramThrowable)
/*     */   {
/* 477 */     StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[0];
/* 478 */     while (paramThrowable != null) {
/* 479 */       paramThrowable.setStackTrace(arrayOfStackTraceElement);
/* 480 */       paramThrowable = paramThrowable.getCause();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void logCall(Remote paramRemote, Object paramObject)
/*     */   {
/* 489 */     if (callLog.isLoggable(Log.VERBOSE)) {
/*     */       String str;
/*     */       try {
/* 492 */         str = getClientHost();
/*     */       } catch (ServerNotActiveException localServerNotActiveException) {
/* 494 */         str = "(local)";
/*     */       }
/* 496 */       callLog.log(Log.VERBOSE, "[" + str + ": " + paramRemote
/* 497 */         .getClass().getName() + this.ref
/* 498 */         .getObjID().toString() + ": " + paramObject + "]");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void logCallException(Throwable paramThrowable)
/*     */   {
/*     */     Object localObject1;
/*     */     
/* 508 */     if (callLog.isLoggable(Log.BRIEF)) {
/* 509 */       localObject1 = "";
/*     */       try {
/* 511 */         localObject1 = "[" + getClientHost() + "] ";
/*     */       }
/*     */       catch (ServerNotActiveException localServerNotActiveException) {}
/* 514 */       callLog.log(Log.BRIEF, (String)localObject1 + "exception: ", paramThrowable);
/*     */     }
/*     */     
/*     */ 
/* 518 */     if (wantExceptionLog) {
/* 519 */       localObject1 = System.err;
/* 520 */       synchronized (localObject1) {
/* 521 */         ((PrintStream)localObject1).println();
/* 522 */         ((PrintStream)localObject1).println("Exception dispatching call to " + this.ref
/* 523 */           .getObjID() + " in thread \"" + 
/* 524 */           Thread.currentThread().getName() + "\" at " + new Date() + ":");
/*     */         
/* 526 */         paramThrowable.printStackTrace((PrintStream)localObject1);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getRefClass(ObjectOutput paramObjectOutput)
/*     */   {
/* 535 */     return "UnicastServerRef";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected RemoteRef getClientRef()
/*     */   {
/* 545 */     return new UnicastRef(this.ref);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeExternal(ObjectOutput paramObjectOutput)
/*     */     throws IOException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void readExternal(ObjectInput paramObjectInput)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 563 */     this.ref = null;
/* 564 */     this.skel = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class HashToMethod_Maps
/*     */     extends WeakClassHashMap<Map<Long, Method>>
/*     */   {
/*     */     protected Map<Long, Method> computeValue(Class<?> paramClass)
/*     */     {
/* 578 */       HashMap localHashMap = new HashMap();
/* 579 */       for (Object localObject = paramClass; 
/* 580 */           localObject != null; 
/* 581 */           localObject = ((Class)localObject).getSuperclass())
/*     */       {
/* 583 */         for (Class localClass : ((Class)localObject).getInterfaces()) {
/* 584 */           if (Remote.class.isAssignableFrom(localClass)) {
/* 585 */             for (Method localMethod1 : localClass.getMethods()) {
/* 586 */               final Method localMethod2 = localMethod1;
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 592 */               AccessController.doPrivileged(new PrivilegedAction()
/*     */               {
/*     */                 public Void run() {
/* 595 */                   localMethod2.setAccessible(true);
/* 596 */                   return null;
/*     */                 }
/* 598 */               });
/* 599 */               localHashMap.put(Long.valueOf(Util.computeMethodHash(localMethod2)), localMethod2);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 604 */       return localHashMap;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object[] unmarshalParameters(Object paramObject, Method paramMethod, MarshalInputStream paramMarshalInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 614 */     return (paramObject instanceof DeserializationChecker) ? 
/* 615 */       unmarshalParametersChecked((DeserializationChecker)paramObject, paramMethod, paramMarshalInputStream) : 
/* 616 */       unmarshalParametersUnchecked(paramMethod, paramMarshalInputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object[] unmarshalParametersUnchecked(Method paramMethod, ObjectInput paramObjectInput)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 625 */     Class[] arrayOfClass = paramMethod.getParameterTypes();
/* 626 */     Object[] arrayOfObject = new Object[arrayOfClass.length];
/* 627 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 628 */       arrayOfObject[i] = unmarshalValue(arrayOfClass[i], paramObjectInput);
/*     */     }
/* 630 */     return arrayOfObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object[] unmarshalParametersChecked(DeserializationChecker paramDeserializationChecker, Method paramMethod, MarshalInputStream paramMarshalInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 641 */     int i = this.methodCallIDCount.getAndIncrement();
/* 642 */     MyChecker localMyChecker = new MyChecker(paramDeserializationChecker, paramMethod, i);
/* 643 */     paramMarshalInputStream.setStreamChecker(localMyChecker);
/*     */     try {
/* 645 */       Class[] arrayOfClass = paramMethod.getParameterTypes();
/* 646 */       Object[] arrayOfObject1 = new Object[arrayOfClass.length];
/* 647 */       for (int j = 0; j < arrayOfClass.length; j++) {
/* 648 */         localMyChecker.setIndex(j);
/* 649 */         arrayOfObject1[j] = unmarshalValue(arrayOfClass[j], paramMarshalInputStream);
/*     */       }
/* 651 */       localMyChecker.end(i);
/* 652 */       return arrayOfObject1;
/*     */     } finally {
/* 654 */       paramMarshalInputStream.setStreamChecker(null);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class MyChecker implements MarshalInputStream.StreamChecker {
/*     */     private final DeserializationChecker descriptorCheck;
/*     */     private final Method method;
/*     */     private final int callID;
/*     */     private int parameterIndex;
/*     */     
/*     */     MyChecker(DeserializationChecker paramDeserializationChecker, Method paramMethod, int paramInt) {
/* 665 */       this.descriptorCheck = paramDeserializationChecker;
/* 666 */       this.method = paramMethod;
/* 667 */       this.callID = paramInt;
/*     */     }
/*     */     
/*     */     public void validateDescriptor(ObjectStreamClass paramObjectStreamClass)
/*     */     {
/* 672 */       this.descriptorCheck.check(this.method, paramObjectStreamClass, this.parameterIndex, this.callID);
/*     */     }
/*     */     
/*     */     public void checkProxyInterfaceNames(String[] paramArrayOfString)
/*     */     {
/* 677 */       this.descriptorCheck.checkProxyClass(this.method, paramArrayOfString, this.parameterIndex, this.callID);
/*     */     }
/*     */     
/*     */     void setIndex(int paramInt) {
/* 681 */       this.parameterIndex = paramInt;
/*     */     }
/*     */     
/*     */     void end(int paramInt) {
/* 685 */       this.descriptorCheck.end(paramInt);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\UnicastServerRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.ClosedWatchServiceException;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardWatchEventKinds;
/*     */ import java.nio.file.WatchEvent.Kind;
/*     */ import java.nio.file.WatchEvent.Modifier;
/*     */ import java.nio.file.WatchKey;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
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
/*     */ abstract class AbstractPoller
/*     */   implements Runnable
/*     */ {
/*     */   private final LinkedList<Request> requestList;
/*     */   private boolean shutdown;
/*     */   
/*     */   protected AbstractPoller()
/*     */   {
/*  50 */     this.requestList = new LinkedList();
/*  51 */     this.shutdown = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void start()
/*     */   {
/*  58 */     final AbstractPoller localAbstractPoller = this;
/*  59 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/*  62 */         Thread localThread = new Thread(localAbstractPoller);
/*  63 */         localThread.setDaemon(true);
/*  64 */         localThread.start();
/*  65 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract void wakeup()
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract Object implRegister(Path paramPath, Set<? extends WatchEvent.Kind<?>> paramSet, WatchEvent.Modifier... paramVarArgs);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract void implCancelKey(WatchKey paramWatchKey);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract void implCloseAll();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   final WatchKey register(Path paramPath, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs)
/*     */     throws IOException
/*     */   {
/* 101 */     if (paramPath == null)
/* 102 */       throw new NullPointerException();
/* 103 */     HashSet localHashSet = new HashSet(paramArrayOfKind.length);
/* 104 */     for (WatchEvent.Kind<?> localKind : paramArrayOfKind)
/*     */     {
/* 106 */       if ((localKind == StandardWatchEventKinds.ENTRY_CREATE) || (localKind == StandardWatchEventKinds.ENTRY_MODIFY) || (localKind == StandardWatchEventKinds.ENTRY_DELETE))
/*     */       {
/*     */ 
/*     */ 
/* 110 */         localHashSet.add(localKind);
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 115 */       else if (localKind != StandardWatchEventKinds.OVERFLOW)
/*     */       {
/*     */ 
/*     */ 
/* 119 */         if (localKind == null)
/* 120 */           throw new NullPointerException("An element in event set is 'null'");
/* 121 */         throw new UnsupportedOperationException(localKind.name());
/*     */       } }
/* 123 */     if (localHashSet.isEmpty())
/* 124 */       throw new IllegalArgumentException("No events to register");
/* 125 */     return (WatchKey)invoke(RequestType.REGISTER, new Object[] { paramPath, localHashSet, paramVarArgs });
/*     */   }
/*     */   
/*     */ 
/*     */   final void cancel(WatchKey paramWatchKey)
/*     */   {
/*     */     try
/*     */     {
/* 133 */       invoke(RequestType.CANCEL, new Object[] { paramWatchKey });
/*     */     }
/*     */     catch (IOException localIOException) {
/* 136 */       throw new AssertionError(localIOException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   final void close()
/*     */     throws IOException
/*     */   {
/* 144 */     invoke(RequestType.CLOSE, new Object[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static enum RequestType
/*     */   {
/* 151 */     REGISTER, 
/* 152 */     CANCEL, 
/* 153 */     CLOSE;
/*     */     
/*     */ 
/*     */     private RequestType() {}
/*     */   }
/*     */   
/*     */   private static class Request
/*     */   {
/*     */     private final RequestType type;
/*     */     private final Object[] params;
/* 163 */     private boolean completed = false;
/* 164 */     private Object result = null;
/*     */     
/*     */     Request(RequestType paramRequestType, Object... paramVarArgs) {
/* 167 */       this.type = paramRequestType;
/* 168 */       this.params = paramVarArgs;
/*     */     }
/*     */     
/*     */     RequestType type() {
/* 172 */       return this.type;
/*     */     }
/*     */     
/*     */     Object[] parameters() {
/* 176 */       return this.params;
/*     */     }
/*     */     
/*     */     void release(Object paramObject) {
/* 180 */       synchronized (this) {
/* 181 */         this.completed = true;
/* 182 */         this.result = paramObject;
/* 183 */         notifyAll();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     Object awaitResult()
/*     */     {
/* 192 */       int i = 0;
/* 193 */       synchronized (this) {
/* 194 */         while (!this.completed) {
/*     */           try {
/* 196 */             wait();
/*     */           } catch (InterruptedException localInterruptedException) {
/* 198 */             i = 1;
/*     */           }
/*     */         }
/* 201 */         if (i != 0)
/* 202 */           Thread.currentThread().interrupt();
/* 203 */         return this.result;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object invoke(RequestType paramRequestType, Object... paramVarArgs)
/*     */     throws IOException
/*     */   {
/* 213 */     Request localRequest = new Request(paramRequestType, paramVarArgs);
/* 214 */     synchronized (this.requestList) {
/* 215 */       if (this.shutdown) {
/* 216 */         throw new ClosedWatchServiceException();
/*     */       }
/* 218 */       this.requestList.add(localRequest);
/*     */     }
/*     */     
/*     */ 
/* 222 */     wakeup();
/*     */     
/*     */ 
/* 225 */     ??? = localRequest.awaitResult();
/*     */     
/* 227 */     if ((??? instanceof RuntimeException))
/* 228 */       throw ((RuntimeException)???);
/* 229 */     if ((??? instanceof IOException))
/* 230 */       throw ((IOException)???);
/* 231 */     return ???;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean processRequests()
/*     */   {
/* 241 */     synchronized (this.requestList) {
/*     */       Request localRequest;
/* 243 */       while ((localRequest = (Request)this.requestList.poll()) != null)
/*     */       {
/* 245 */         if (this.shutdown)
/* 246 */           localRequest.release(new ClosedWatchServiceException());
/*     */         Object[] arrayOfObject;
/*     */         Object localObject1;
/* 249 */         switch (localRequest.type())
/*     */         {
/*     */ 
/*     */ 
/*     */         case REGISTER: 
/* 254 */           arrayOfObject = localRequest.parameters();
/* 255 */           localObject1 = (Path)arrayOfObject[0];
/* 256 */           Set localSet = (Set)arrayOfObject[1];
/*     */           
/* 258 */           WatchEvent.Modifier[] arrayOfModifier = (WatchEvent.Modifier[])arrayOfObject[2];
/*     */           
/* 260 */           localRequest.release(implRegister((Path)localObject1, localSet, arrayOfModifier));
/* 261 */           break;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         case CANCEL: 
/* 267 */           arrayOfObject = localRequest.parameters();
/* 268 */           localObject1 = (WatchKey)arrayOfObject[0];
/* 269 */           implCancelKey((WatchKey)localObject1);
/* 270 */           localRequest.release(null);
/* 271 */           break;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         case CLOSE: 
/* 277 */           implCloseAll();
/* 278 */           localRequest.release(null);
/* 279 */           this.shutdown = true;
/* 280 */           break;
/*     */         
/*     */ 
/*     */         default: 
/* 284 */           localRequest.release(new IOException("request not recognized")); }
/*     */         
/*     */       }
/*     */     }
/* 288 */     return this.shutdown;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\fs\AbstractPoller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
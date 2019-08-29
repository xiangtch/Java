/*     */ package sun.rmi.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectStreamClass;
/*     */ import java.io.StreamCorruptedException;
/*     */ import java.rmi.server.RMIClassLoader;
/*     */ import java.security.AccessControlException;
/*     */ import java.security.AccessController;
/*     */ import java.security.Permission;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import sun.misc.JavaObjectInputStreamAccess;
/*     */ import sun.misc.ObjectStreamClassValidator;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.misc.VM;
/*     */ import sun.security.action.GetPropertyAction;
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
/*     */ public class MarshalInputStream
/*     */   extends ObjectInputStream
/*     */ {
/*  60 */   private volatile StreamChecker streamChecker = null;
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
/*  75 */   private static final boolean useCodebaseOnlyProperty = !((String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.useCodebaseOnly", "true"))).equalsIgnoreCase("false");
/*     */   
/*     */ 
/*  78 */   protected static Map<String, Class<?>> permittedSunClasses = new HashMap(3);
/*     */   
/*     */ 
/*     */ 
/*  82 */   private boolean skipDefaultResolveClass = false;
/*     */   
/*     */ 
/*  85 */   private final Map<Object, Runnable> doneCallbacks = new HashMap(3);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  92 */   private boolean useCodebaseOnly = useCodebaseOnlyProperty;
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
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 110 */       String str1 = "sun.rmi.server.Activation$ActivationSystemImpl_Stub";
/*     */       
/* 112 */       String str2 = "sun.rmi.registry.RegistryImpl_Stub";
/*     */       
/* 114 */       permittedSunClasses.put(str1, Class.forName(str1));
/* 115 */       permittedSunClasses.put(str2, Class.forName(str2));
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException)
/*     */     {
/* 119 */       throw new NoClassDefFoundError("Missing system class: " + localClassNotFoundException.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MarshalInputStream(InputStream paramInputStream)
/*     */     throws IOException, StreamCorruptedException
/*     */   {
/* 129 */     super(paramInputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Runnable getDoneCallback(Object paramObject)
/*     */   {
/* 138 */     return (Runnable)this.doneCallbacks.get(paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDoneCallback(Object paramObject, Runnable paramRunnable)
/*     */   {
/* 148 */     this.doneCallbacks.put(paramObject, paramRunnable);
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
/*     */   public void done()
/*     */   {
/* 161 */     Iterator localIterator = this.doneCallbacks.values().iterator();
/* 162 */     while (localIterator.hasNext()) {
/* 163 */       Runnable localRunnable = (Runnable)localIterator.next();
/* 164 */       localRunnable.run();
/*     */     }
/* 166 */     this.doneCallbacks.clear();
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 173 */     done();
/* 174 */     super.close();
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
/*     */   protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 189 */     Object localObject = readLocation();
/*     */     
/* 191 */     String str1 = paramObjectStreamClass.getName();
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
/* 204 */     ClassLoader localClassLoader = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */     String str2 = null;
/* 214 */     if ((!this.useCodebaseOnly) && ((localObject instanceof String))) {
/* 215 */       str2 = (String)localObject;
/*     */     }
/*     */     try
/*     */     {
/* 219 */       return RMIClassLoader.loadClass(str2, str1, localClassLoader);
/*     */     }
/*     */     catch (AccessControlException localAccessControlException) {
/* 222 */       return checkSunClass(str1, localAccessControlException);
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException1)
/*     */     {
/*     */       try
/*     */       {
/* 229 */         if ((Character.isLowerCase(str1.charAt(0))) && 
/* 230 */           (str1.indexOf('.') == -1))
/*     */         {
/* 232 */           return super.resolveClass(paramObjectStreamClass);
/*     */         }
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException2) {}
/* 236 */       throw localClassNotFoundException1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Class<?> resolveProxyClass(String[] paramArrayOfString)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 247 */     StreamChecker localStreamChecker = this.streamChecker;
/* 248 */     if (localStreamChecker != null) {
/* 249 */       localStreamChecker.checkProxyInterfaceNames(paramArrayOfString);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 255 */     Object localObject = readLocation();
/*     */     
/*     */ 
/* 258 */     ClassLoader localClassLoader = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
/*     */     
/* 260 */     String str = null;
/* 261 */     if ((!this.useCodebaseOnly) && ((localObject instanceof String))) {
/* 262 */       str = (String)localObject;
/*     */     }
/*     */     
/* 265 */     return RMIClassLoader.loadProxyClass(str, paramArrayOfString, localClassLoader);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static ClassLoader latestUserDefinedLoader()
/*     */   {
/* 276 */     return VM.latestUserDefinedLoader();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Class<?> checkSunClass(String paramString, AccessControlException paramAccessControlException)
/*     */     throws AccessControlException
/*     */   {
/* 287 */     Permission localPermission = paramAccessControlException.getPermission();
/* 288 */     String str = null;
/* 289 */     if (localPermission != null) {
/* 290 */       str = localPermission.getName();
/*     */     }
/*     */     
/* 293 */     Class localClass = (Class)permittedSunClasses.get(paramString);
/*     */     
/*     */ 
/* 296 */     if ((str == null) || (localClass == null) || (
/*     */     
/* 298 */       (!str.equals("accessClassInPackage.sun.rmi.server")) && 
/* 299 */       (!str.equals("accessClassInPackage.sun.rmi.registry"))))
/*     */     {
/* 301 */       throw paramAccessControlException;
/*     */     }
/*     */     
/* 304 */     return localClass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object readLocation()
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 315 */     return readObject();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void skipDefaultResolveClass()
/*     */   {
/* 323 */     this.skipDefaultResolveClass = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void useCodebaseOnly()
/*     */   {
/* 331 */     this.useCodebaseOnly = true;
/*     */   }
/*     */   
/*     */   synchronized void setStreamChecker(StreamChecker paramStreamChecker) {
/* 335 */     this.streamChecker = paramStreamChecker;
/* 336 */     SharedSecrets.getJavaObjectInputStreamAccess().setValidator(this, paramStreamChecker);
/*     */   }
/*     */   
/*     */   protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException
/*     */   {
/* 341 */     ObjectStreamClass localObjectStreamClass = super.readClassDescriptor();
/*     */     
/* 343 */     validateDesc(localObjectStreamClass);
/*     */     
/* 345 */     return localObjectStreamClass;
/*     */   }
/*     */   
/*     */   private void validateDesc(ObjectStreamClass paramObjectStreamClass) {
/*     */     StreamChecker localStreamChecker;
/* 350 */     synchronized (this) {
/* 351 */       localStreamChecker = this.streamChecker;
/*     */     }
/* 353 */     if (localStreamChecker != null) {
/* 354 */       localStreamChecker.validateDescriptor(paramObjectStreamClass);
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract interface StreamChecker
/*     */     extends ObjectStreamClassValidator
/*     */   {
/*     */     public abstract void checkProxyInterfaceNames(String[] paramArrayOfString);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\MarshalInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
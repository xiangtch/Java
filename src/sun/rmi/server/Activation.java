/*      */ package sun.rmi.server;
/*      */ 
/*      */ import com.sun.rmi.rmid.ExecOptionPermission;
/*      */ import com.sun.rmi.rmid.ExecPermission;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInput;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.InetAddress;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketAddress;
/*      */ import java.net.SocketException;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.channels.ServerSocketChannel;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.rmi.AccessException;
/*      */ import java.rmi.AlreadyBoundException;
/*      */ import java.rmi.ConnectException;
/*      */ import java.rmi.ConnectIOException;
/*      */ import java.rmi.MarshalledObject;
/*      */ import java.rmi.NoSuchObjectException;
/*      */ import java.rmi.NotBoundException;
/*      */ import java.rmi.Remote;
/*      */ import java.rmi.RemoteException;
/*      */ import java.rmi.activation.ActivationDesc;
/*      */ import java.rmi.activation.ActivationException;
/*      */ import java.rmi.activation.ActivationGroup;
/*      */ import java.rmi.activation.ActivationGroupDesc;
/*      */ import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
/*      */ import java.rmi.activation.ActivationGroupID;
/*      */ import java.rmi.activation.ActivationID;
/*      */ import java.rmi.activation.ActivationInstantiator;
/*      */ import java.rmi.activation.ActivationMonitor;
/*      */ import java.rmi.activation.ActivationSystem;
/*      */ import java.rmi.activation.Activator;
/*      */ import java.rmi.activation.UnknownGroupException;
/*      */ import java.rmi.activation.UnknownObjectException;
/*      */ import java.rmi.registry.Registry;
/*      */ import java.rmi.server.ObjID;
/*      */ import java.rmi.server.RMIClassLoader;
/*      */ import java.rmi.server.RMIClientSocketFactory;
/*      */ import java.rmi.server.RMIServerSocketFactory;
/*      */ import java.rmi.server.RemoteObject;
/*      */ import java.rmi.server.RemoteServer;
/*      */ import java.rmi.server.UnicastRemoteObject;
/*      */ import java.security.AccessControlException;
/*      */ import java.security.AccessController;
/*      */ import java.security.AllPermission;
/*      */ import java.security.CodeSource;
/*      */ import java.security.Permission;
/*      */ import java.security.PermissionCollection;
/*      */ import java.security.Permissions;
/*      */ import java.security.Policy;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.cert.Certificate;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.Properties;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import sun.rmi.log.LogHandler;
/*      */ import sun.rmi.log.ReliableLog;
/*      */ import sun.rmi.registry.RegistryImpl;
/*      */ import sun.rmi.transport.LiveRef;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.action.GetIntegerAction;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.security.provider.PolicyFile;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Activation
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 2921265612698155191L;
/*      */   private static final byte MAJOR_VERSION = 1;
/*      */   private static final byte MINOR_VERSION = 0;
/*      */   private static Object execPolicy;
/*      */   private static Method execPolicyMethod;
/*      */   private static boolean debugExec;
/*  151 */   private Map<ActivationID, ActivationGroupID> idTable = new ConcurrentHashMap();
/*      */   
/*      */ 
/*  154 */   private Map<ActivationGroupID, GroupEntry> groupTable = new ConcurrentHashMap();
/*      */   
/*      */ 
/*  157 */   private byte majorVersion = 1;
/*  158 */   private byte minorVersion = 0;
/*      */   
/*      */ 
/*      */   private transient int groupSemaphore;
/*      */   
/*      */ 
/*      */   private transient int groupCounter;
/*      */   
/*      */ 
/*      */   private transient ReliableLog log;
/*      */   
/*      */ 
/*      */   private transient int numUpdates;
/*      */   
/*      */   private transient String[] command;
/*      */   
/*  174 */   private static final long groupTimeout = getInt("sun.rmi.activation.groupTimeout", 60000);
/*      */   
/*      */ 
/*  177 */   private static final int snapshotInterval = getInt("sun.rmi.activation.snapshotInterval", 200);
/*      */   
/*      */ 
/*  180 */   private static final long execTimeout = getInt("sun.rmi.activation.execTimeout", 30000);
/*      */   
/*  182 */   private static final Object initLock = new Object();
/*  183 */   private static boolean initDone = false;
/*      */   private transient Activator activator;
/*      */   
/*      */   private static int getInt(String paramString, int paramInt) {
/*  187 */     return ((Integer)AccessController.doPrivileged(new GetIntegerAction(paramString, paramInt))).intValue();
/*      */   }
/*      */   
/*      */ 
/*      */   private transient Activator activatorStub;
/*      */   private transient ActivationSystem system;
/*      */   private transient ActivationSystem systemStub;
/*      */   private transient ActivationMonitor monitor;
/*      */   private transient Registry registry;
/*  196 */   private volatile transient boolean shuttingDown = false;
/*      */   
/*      */   private volatile transient Object startupLock;
/*      */   private transient Thread shutdownHook;
/*  200 */   private static ResourceBundle resources = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void startActivation(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory, String paramString, String[] paramArrayOfString)
/*      */     throws Exception
/*      */   {
/*  219 */     ReliableLog localReliableLog = new ReliableLog(paramString, new ActLogHandler());
/*  220 */     Activation localActivation = (Activation)localReliableLog.recover();
/*  221 */     localActivation.init(paramInt, paramRMIServerSocketFactory, localReliableLog, paramArrayOfString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void init(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory, ReliableLog paramReliableLog, String[] paramArrayOfString)
/*      */     throws Exception
/*      */   {
/*  235 */     this.log = paramReliableLog;
/*  236 */     this.numUpdates = 0;
/*  237 */     this.shutdownHook = new ShutdownHook();
/*  238 */     this.groupSemaphore = getInt("sun.rmi.activation.groupThrottle", 3);
/*  239 */     this.groupCounter = 0;
/*  240 */     Runtime.getRuntime().addShutdownHook(this.shutdownHook);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  245 */     ActivationGroupID[] arrayOfActivationGroupID = (ActivationGroupID[])this.groupTable.keySet().toArray(new ActivationGroupID[0]);
/*      */     
/*  247 */     synchronized (this.startupLock = new Object())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  252 */       this.activator = new ActivatorImpl(paramInt, paramRMIServerSocketFactory);
/*  253 */       this.activatorStub = ((Activator)RemoteObject.toStub(this.activator));
/*  254 */       this.system = new ActivationSystemImpl(paramInt, paramRMIServerSocketFactory);
/*  255 */       this.systemStub = ((ActivationSystem)RemoteObject.toStub(this.system));
/*  256 */       this.monitor = new ActivationMonitorImpl(paramInt, paramRMIServerSocketFactory);
/*  257 */       initCommand(paramArrayOfString);
/*  258 */       this.registry = new SystemRegistryImpl(paramInt, null, paramRMIServerSocketFactory, this.systemStub);
/*      */       
/*  260 */       if (paramRMIServerSocketFactory != null) {
/*  261 */         synchronized (initLock) {
/*  262 */           initDone = true;
/*  263 */           initLock.notifyAll();
/*      */         }
/*      */       }
/*      */     }
/*  267 */     this.startupLock = null;
/*      */     
/*      */ 
/*  270 */     int i = arrayOfActivationGroupID.length; for (;;) { i--; if (i < 0)
/*      */         break;
/*  272 */       try { getGroupEntry(arrayOfActivationGroupID[i]).restartServices();
/*      */       } catch (UnknownGroupException localUnknownGroupException) {
/*  274 */         System.err.println(
/*  275 */           getTextResource("rmid.restart.group.warning"));
/*  276 */         localUnknownGroupException.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  289 */     paramObjectInputStream.defaultReadObject();
/*  290 */     if (!(this.groupTable instanceof ConcurrentHashMap)) {
/*  291 */       this.groupTable = new ConcurrentHashMap(this.groupTable);
/*      */     }
/*  293 */     if (!(this.idTable instanceof ConcurrentHashMap)) {
/*  294 */       this.idTable = new ConcurrentHashMap(this.idTable);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private void checkShutdown()
/*      */     throws ActivationException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 667	sun/rmi/server/Activation:startupLock	Ljava/lang/Object;
/*      */     //   4: astore_1
/*      */     //   5: aload_1
/*      */     //   6: ifnull +17 -> 23
/*      */     //   9: aload_1
/*      */     //   10: dup
/*      */     //   11: astore_2
/*      */     //   12: monitorenter
/*      */     //   13: aload_2
/*      */     //   14: monitorexit
/*      */     //   15: goto +8 -> 23
/*      */     //   18: astore_3
/*      */     //   19: aload_2
/*      */     //   20: monitorexit
/*      */     //   21: aload_3
/*      */     //   22: athrow
/*      */     //   23: aload_0
/*      */     //   24: getfield 664	sun/rmi/server/Activation:shuttingDown	Z
/*      */     //   27: iconst_1
/*      */     //   28: if_icmpne +13 -> 41
/*      */     //   31: new 399	java/rmi/activation/ActivationException
/*      */     //   34: dup
/*      */     //   35: ldc 15
/*      */     //   37: invokespecial 717	java/rmi/activation/ActivationException:<init>	(Ljava/lang/String;)V
/*      */     //   40: athrow
/*      */     //   41: return
/*      */     // Line number table:
/*      */     //   Java source line #651	-> byte code offset #0
/*      */     //   Java source line #652	-> byte code offset #5
/*      */     //   Java source line #653	-> byte code offset #9
/*      */     //   Java source line #655	-> byte code offset #13
/*      */     //   Java source line #658	-> byte code offset #23
/*      */     //   Java source line #659	-> byte code offset #31
/*      */     //   Java source line #662	-> byte code offset #41
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	42	0	this	Activation
/*      */     //   4	6	1	localObject1	Object
/*      */     //   11	9	2	Ljava/lang/Object;	Object
/*      */     //   18	4	3	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   13	15	18	finally
/*      */     //   18	21	18	finally
/*      */   }
/*      */   
/*      */   private static class SystemRegistryImpl
/*      */     extends RegistryImpl
/*      */   {
/*  300 */     private static final String NAME = ActivationSystem.class.getName();
/*      */     
/*      */     private static final long serialVersionUID = 4877330021609408794L;
/*      */     
/*      */     private final ActivationSystem systemStub;
/*      */     
/*      */ 
/*      */     SystemRegistryImpl(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory, ActivationSystem paramActivationSystem)
/*      */       throws RemoteException
/*      */     {
/*  310 */       super(paramRMIClientSocketFactory, paramRMIServerSocketFactory);
/*  311 */       this.systemStub = paramActivationSystem;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Remote lookup(String paramString)
/*      */       throws RemoteException, NotBoundException
/*      */     {
/*  323 */       if (paramString.equals(NAME)) {
/*  324 */         return this.systemStub;
/*      */       }
/*  326 */       return super.lookup(paramString);
/*      */     }
/*      */     
/*      */     public String[] list() throws RemoteException
/*      */     {
/*  331 */       String[] arrayOfString1 = super.list();
/*  332 */       int i = arrayOfString1.length;
/*  333 */       String[] arrayOfString2 = new String[i + 1];
/*  334 */       if (i > 0) {
/*  335 */         System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, i);
/*      */       }
/*  337 */       arrayOfString2[i] = NAME;
/*  338 */       return arrayOfString2;
/*      */     }
/*      */     
/*      */     public void bind(String paramString, Remote paramRemote)
/*      */       throws RemoteException, AlreadyBoundException, AccessException
/*      */     {
/*  344 */       if (paramString.equals(NAME)) {
/*  345 */         throw new AccessException("binding ActivationSystem is disallowed");
/*      */       }
/*      */       
/*  348 */       RegistryImpl.checkAccess("ActivationSystem.bind");
/*  349 */       super.bind(paramString, paramRemote);
/*      */     }
/*      */     
/*      */ 
/*      */     public void unbind(String paramString)
/*      */       throws RemoteException, NotBoundException, AccessException
/*      */     {
/*  356 */       if (paramString.equals(NAME)) {
/*  357 */         throw new AccessException("unbinding ActivationSystem is disallowed");
/*      */       }
/*      */       
/*  360 */       RegistryImpl.checkAccess("ActivationSystem.unbind");
/*  361 */       super.unbind(paramString);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void rebind(String paramString, Remote paramRemote)
/*      */       throws RemoteException, AccessException
/*      */     {
/*  369 */       if (paramString.equals(NAME)) {
/*  370 */         throw new AccessException("binding ActivationSystem is disallowed");
/*      */       }
/*      */       
/*  373 */       RegistryImpl.checkAccess("ActivationSystem.rebind");
/*  374 */       super.rebind(paramString, paramRemote);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   class ActivatorImpl
/*      */     extends RemoteServer
/*      */     implements Activator
/*      */   {
/*      */     private static final long serialVersionUID = -3654244726254566136L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     ActivatorImpl(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory)
/*      */       throws RemoteException
/*      */     {
/*  397 */       LiveRef localLiveRef = new LiveRef(new ObjID(1), paramInt, null, paramRMIServerSocketFactory);
/*      */       
/*  399 */       UnicastServerRef localUnicastServerRef = new UnicastServerRef(localLiveRef);
/*  400 */       this.ref = localUnicastServerRef;
/*  401 */       localUnicastServerRef.exportObject(this, null, false);
/*      */     }
/*      */     
/*      */ 
/*      */     public MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean)
/*      */       throws ActivationException, UnknownObjectException, RemoteException
/*      */     {
/*  408 */       Activation.this.checkShutdown();
/*  409 */       return Activation.this.getGroupEntry(paramActivationID).activate(paramActivationID, paramBoolean);
/*      */     }
/*      */   }
/*      */   
/*      */   class ActivationMonitorImpl
/*      */     extends UnicastRemoteObject
/*      */     implements ActivationMonitor
/*      */   {
/*      */     private static final long serialVersionUID = -6214940464757948867L;
/*      */     
/*      */     ActivationMonitorImpl(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory) throws RemoteException
/*      */     {
/*  421 */       super(null, paramRMIServerSocketFactory);
/*      */     }
/*      */     
/*      */     public void inactiveObject(ActivationID paramActivationID) throws UnknownObjectException, RemoteException
/*      */     {
/*      */       try
/*      */       {
/*  428 */         Activation.this.checkShutdown();
/*      */       } catch (ActivationException localActivationException) {
/*  430 */         return;
/*      */       }
/*  432 */       RegistryImpl.checkAccess("Activator.inactiveObject");
/*  433 */       Activation.this.getGroupEntry(paramActivationID).inactiveObject(paramActivationID);
/*      */     }
/*      */     
/*      */     public void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
/*      */       throws UnknownObjectException, RemoteException
/*      */     {
/*      */       try
/*      */       {
/*  441 */         Activation.this.checkShutdown();
/*      */       } catch (ActivationException localActivationException) {
/*  443 */         return;
/*      */       }
/*  445 */       RegistryImpl.checkAccess("ActivationSystem.activeObject");
/*  446 */       Activation.this.getGroupEntry(paramActivationID).activeObject(paramActivationID, paramMarshalledObject);
/*      */     }
/*      */     
/*      */     public void inactiveGroup(ActivationGroupID paramActivationGroupID, long paramLong)
/*      */       throws UnknownGroupException, RemoteException
/*      */     {
/*      */       try
/*      */       {
/*  454 */         Activation.this.checkShutdown();
/*      */       } catch (ActivationException localActivationException) {
/*  456 */         return;
/*      */       }
/*  458 */       RegistryImpl.checkAccess("ActivationMonitor.inactiveGroup");
/*  459 */       Activation.this.getGroupEntry(paramActivationGroupID).inactiveGroup(paramLong, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static class SameHostOnlyServerRef
/*      */     extends UnicastServerRef
/*      */   {
/*      */     private static final long serialVersionUID = 1234L;
/*      */     
/*      */ 
/*      */ 
/*      */     private String accessKind;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     SameHostOnlyServerRef(LiveRef paramLiveRef, String paramString)
/*      */     {
/*  480 */       super();
/*  481 */       this.accessKind = paramString;
/*      */     }
/*      */     
/*      */     protected void unmarshalCustomCallData(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException
/*      */     {
/*  486 */       RegistryImpl.checkAccess(this.accessKind);
/*  487 */       super.unmarshalCustomCallData(paramObjectInput);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   class ActivationSystemImpl
/*      */     extends RemoteServer
/*      */     implements ActivationSystem
/*      */   {
/*      */     private static final long serialVersionUID = 9100152600327688967L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     ActivationSystemImpl(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory)
/*      */       throws RemoteException
/*      */     {
/*  507 */       LiveRef localLiveRef = new LiveRef(new ObjID(4), paramInt, null, paramRMIServerSocketFactory);
/*  508 */       SameHostOnlyServerRef localSameHostOnlyServerRef = new SameHostOnlyServerRef(localLiveRef, "ActivationSystem.nonLocalAccess");
/*      */       
/*  510 */       this.ref = localSameHostOnlyServerRef;
/*  511 */       localSameHostOnlyServerRef.exportObject(this, null);
/*      */     }
/*      */     
/*      */     public ActivationID registerObject(ActivationDesc paramActivationDesc)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  517 */       Activation.this.checkShutdown();
/*      */       
/*      */ 
/*  520 */       ActivationGroupID localActivationGroupID = paramActivationDesc.getGroupID();
/*  521 */       ActivationID localActivationID = new ActivationID(Activation.this.activatorStub);
/*  522 */       Activation.this.getGroupEntry(localActivationGroupID).registerObject(localActivationID, paramActivationDesc, true);
/*  523 */       return localActivationID;
/*      */     }
/*      */     
/*      */     public void unregisterObject(ActivationID paramActivationID)
/*      */       throws ActivationException, UnknownObjectException, RemoteException
/*      */     {
/*  529 */       Activation.this.checkShutdown();
/*      */       
/*      */ 
/*  532 */       Activation.this.getGroupEntry(paramActivationID).unregisterObject(paramActivationID, true);
/*      */     }
/*      */     
/*      */     public ActivationGroupID registerGroup(ActivationGroupDesc paramActivationGroupDesc)
/*      */       throws ActivationException, RemoteException
/*      */     {
/*  538 */       Thread.dumpStack();
/*  539 */       Activation.this.checkShutdown();
/*      */       
/*      */ 
/*  542 */       Activation.this.checkArgs(paramActivationGroupDesc, null);
/*      */       
/*  544 */       ActivationGroupID localActivationGroupID = new ActivationGroupID(Activation.this.systemStub);
/*  545 */       GroupEntry localGroupEntry = new GroupEntry(Activation.this, localActivationGroupID, paramActivationGroupDesc);
/*      */       
/*  547 */       Activation.this.groupTable.put(localActivationGroupID, localGroupEntry);
/*  548 */       Activation.this.addLogRecord(new LogRegisterGroup(localActivationGroupID, paramActivationGroupDesc));
/*  549 */       return localActivationGroupID;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public ActivationMonitor activeGroup(ActivationGroupID paramActivationGroupID, ActivationInstantiator paramActivationInstantiator, long paramLong)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  557 */       Activation.this.checkShutdown();
/*      */       
/*      */ 
/*      */ 
/*  561 */       Activation.this.getGroupEntry(paramActivationGroupID).activeGroup(paramActivationInstantiator, paramLong);
/*  562 */       return Activation.this.monitor;
/*      */     }
/*      */     
/*      */     public void unregisterGroup(ActivationGroupID paramActivationGroupID)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  568 */       Activation.this.checkShutdown();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  574 */       Activation.this.removeGroupEntry(paramActivationGroupID).unregisterGroup(true);
/*      */     }
/*      */     
/*      */ 
/*      */     public ActivationDesc setActivationDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
/*      */       throws ActivationException, UnknownObjectException, RemoteException
/*      */     {
/*  581 */       Activation.this.checkShutdown();
/*      */       
/*      */ 
/*      */ 
/*  585 */       if (!Activation.this.getGroupID(paramActivationID).equals(paramActivationDesc.getGroupID())) {
/*  586 */         throw new ActivationException("ActivationDesc contains wrong group");
/*      */       }
/*      */       
/*  589 */       return Activation.this.getGroupEntry(paramActivationID).setActivationDesc(paramActivationID, paramActivationDesc, true);
/*      */     }
/*      */     
/*      */ 
/*      */     public ActivationGroupDesc setActivationGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  596 */       Activation.this.checkShutdown();
/*      */       
/*      */ 
/*      */ 
/*  600 */       Activation.this.checkArgs(paramActivationGroupDesc, null);
/*  601 */       return Activation.this.getGroupEntry(paramActivationGroupID).setActivationGroupDesc(paramActivationGroupID, paramActivationGroupDesc, true);
/*      */     }
/*      */     
/*      */     public ActivationDesc getActivationDesc(ActivationID paramActivationID)
/*      */       throws ActivationException, UnknownObjectException, RemoteException
/*      */     {
/*  607 */       Activation.this.checkShutdown();
/*      */       
/*      */ 
/*      */ 
/*  611 */       return Activation.this.getGroupEntry(paramActivationID).getActivationDesc(paramActivationID);
/*      */     }
/*      */     
/*      */     public ActivationGroupDesc getActivationGroupDesc(ActivationGroupID paramActivationGroupID)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  617 */       Activation.this.checkShutdown();
/*      */       
/*      */ 
/*      */ 
/*  621 */       return Activation.this.getGroupEntry(paramActivationGroupID).desc;
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public void shutdown()
/*      */       throws AccessException
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 185	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
/*      */       //   4: invokestatic 198	sun/rmi/server/Activation:access$1100	(Lsun/rmi/server/Activation;)Ljava/lang/Object;
/*      */       //   7: astore_1
/*      */       //   8: aload_1
/*      */       //   9: ifnull +17 -> 26
/*      */       //   12: aload_1
/*      */       //   13: dup
/*      */       //   14: astore_2
/*      */       //   15: monitorenter
/*      */       //   16: aload_2
/*      */       //   17: monitorexit
/*      */       //   18: goto +8 -> 26
/*      */       //   21: astore_3
/*      */       //   22: aload_2
/*      */       //   23: monitorexit
/*      */       //   24: aload_3
/*      */       //   25: athrow
/*      */       //   26: aload_0
/*      */       //   27: getfield 185	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
/*      */       //   30: dup
/*      */       //   31: astore_2
/*      */       //   32: monitorenter
/*      */       //   33: aload_0
/*      */       //   34: getfield 185	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
/*      */       //   37: invokestatic 196	sun/rmi/server/Activation:access$1200	(Lsun/rmi/server/Activation;)Z
/*      */       //   40: ifne +26 -> 66
/*      */       //   43: aload_0
/*      */       //   44: getfield 185	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
/*      */       //   47: iconst_1
/*      */       //   48: invokestatic 197	sun/rmi/server/Activation:access$1202	(Lsun/rmi/server/Activation;Z)Z
/*      */       //   51: pop
/*      */       //   52: new 101	sun/rmi/server/Activation$Shutdown
/*      */       //   55: dup
/*      */       //   56: aload_0
/*      */       //   57: getfield 185	sun/rmi/server/Activation$ActivationSystemImpl:this$0	Lsun/rmi/server/Activation;
/*      */       //   60: invokespecial 220	sun/rmi/server/Activation$Shutdown:<init>	(Lsun/rmi/server/Activation;)V
/*      */       //   63: invokevirtual 219	sun/rmi/server/Activation$Shutdown:start	()V
/*      */       //   66: aload_2
/*      */       //   67: monitorexit
/*      */       //   68: goto +10 -> 78
/*      */       //   71: astore 4
/*      */       //   73: aload_2
/*      */       //   74: monitorexit
/*      */       //   75: aload 4
/*      */       //   77: athrow
/*      */       //   78: return
/*      */       // Line number table:
/*      */       //   Java source line #632	-> byte code offset #0
/*      */       //   Java source line #633	-> byte code offset #8
/*      */       //   Java source line #634	-> byte code offset #12
/*      */       //   Java source line #636	-> byte code offset #16
/*      */       //   Java source line #639	-> byte code offset #26
/*      */       //   Java source line #640	-> byte code offset #33
/*      */       //   Java source line #641	-> byte code offset #43
/*      */       //   Java source line #642	-> byte code offset #52
/*      */       //   Java source line #644	-> byte code offset #66
/*      */       //   Java source line #645	-> byte code offset #78
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	79	0	this	ActivationSystemImpl
/*      */       //   7	6	1	localObject1	Object
/*      */       //   21	4	3	localObject2	Object
/*      */       //   71	5	4	localObject3	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   16	18	21	finally
/*      */       //   21	24	21	finally
/*      */       //   33	68	71	finally
/*      */       //   71	75	71	finally
/*      */     }
/*      */   }
/*      */   
/*      */   private static void unexport(Remote paramRemote)
/*      */   {
/*      */     try
/*      */     {
/*  667 */       while (UnicastRemoteObject.unexportObject(paramRemote, false) != true)
/*      */       {
/*      */ 
/*  670 */         Thread.sleep(100L);
/*      */       }
/*      */     }
/*      */     catch (Exception localException) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class Shutdown
/*      */     extends Thread
/*      */   {
/*      */     Shutdown()
/*      */     {
/*  683 */       super();
/*      */     }
/*      */     
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       try
/*      */       {
/*  691 */         Activation.unexport(Activation.this.activator);
/*  692 */         Activation.unexport(Activation.this.system);
/*      */         
/*      */ 
/*  695 */         for (GroupEntry localGroupEntry : Activation.this.groupTable.values()) {
/*  696 */           localGroupEntry.shutdown();
/*      */         }
/*      */         
/*  699 */         Runtime.getRuntime().removeShutdownHook(Activation.this.shutdownHook);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  704 */         Activation.unexport(Activation.this.monitor);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  715 */           synchronized (Activation.this.log) {
/*  716 */             Activation.this.log.close();
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         catch (IOException localIOException) {}
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*      */ 
/*  728 */         System.err.println(Activation.getTextResource("rmid.daemon.shutdown"));
/*  729 */         System.exit(0);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private class ShutdownHook extends Thread
/*      */   {
/*      */     ShutdownHook() {
/*  737 */       super();
/*      */     }
/*      */     
/*      */     public void run() {
/*  741 */       synchronized (Activation.this) {
/*  742 */         Activation.this.shuttingDown = true;
/*      */       }
/*      */       
/*      */ 
/*  746 */       for (??? = Activation.this.groupTable.values().iterator(); ((Iterator)???).hasNext();) { GroupEntry localGroupEntry = (GroupEntry)((Iterator)???).next();
/*  747 */         localGroupEntry.shutdownFast();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private ActivationGroupID getGroupID(ActivationID paramActivationID)
/*      */     throws UnknownObjectException
/*      */   {
/*  759 */     ActivationGroupID localActivationGroupID = (ActivationGroupID)this.idTable.get(paramActivationID);
/*  760 */     if (localActivationGroupID != null) {
/*  761 */       return localActivationGroupID;
/*      */     }
/*  763 */     throw new UnknownObjectException("unknown object: " + paramActivationID);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private GroupEntry getGroupEntry(ActivationGroupID paramActivationGroupID, boolean paramBoolean)
/*      */     throws UnknownGroupException
/*      */   {
/*  773 */     if (paramActivationGroupID.getClass() == ActivationGroupID.class) {
/*      */       GroupEntry localGroupEntry;
/*  775 */       if (paramBoolean) {
/*  776 */         localGroupEntry = (GroupEntry)this.groupTable.remove(paramActivationGroupID);
/*      */       } else {
/*  778 */         localGroupEntry = (GroupEntry)this.groupTable.get(paramActivationGroupID);
/*      */       }
/*  780 */       if ((localGroupEntry != null) && (!localGroupEntry.removed)) {
/*  781 */         return localGroupEntry;
/*      */       }
/*      */     }
/*  784 */     throw new UnknownGroupException("group unknown");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private GroupEntry getGroupEntry(ActivationGroupID paramActivationGroupID)
/*      */     throws UnknownGroupException
/*      */   {
/*  794 */     return getGroupEntry(paramActivationGroupID, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private GroupEntry removeGroupEntry(ActivationGroupID paramActivationGroupID)
/*      */     throws UnknownGroupException
/*      */   {
/*  804 */     return getGroupEntry(paramActivationGroupID, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private GroupEntry getGroupEntry(ActivationID paramActivationID)
/*      */     throws UnknownObjectException
/*      */   {
/*  815 */     ActivationGroupID localActivationGroupID = getGroupID(paramActivationID);
/*  816 */     GroupEntry localGroupEntry = (GroupEntry)this.groupTable.get(localActivationGroupID);
/*  817 */     if ((localGroupEntry != null) && (!localGroupEntry.removed)) {
/*  818 */       return localGroupEntry;
/*      */     }
/*  820 */     throw new UnknownObjectException("object's group removed");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class GroupEntry
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 7222464070032993304L;
/*      */     
/*      */ 
/*      */     private static final int MAX_TRIES = 2;
/*      */     
/*      */ 
/*      */     private static final int NORMAL = 0;
/*      */     
/*      */ 
/*      */     private static final int CREATING = 1;
/*      */     
/*      */ 
/*      */     private static final int TERMINATE = 2;
/*      */     
/*      */     private static final int TERMINATING = 3;
/*      */     
/*  844 */     ActivationGroupDesc desc = null;
/*  845 */     ActivationGroupID groupID = null;
/*  846 */     long incarnation = 0L;
/*  847 */     Map<ActivationID, ObjectEntry> objects = new HashMap();
/*  848 */     Set<ActivationID> restartSet = new HashSet();
/*      */     
/*  850 */     transient ActivationInstantiator group = null;
/*  851 */     transient int status = 0;
/*  852 */     transient long waitTime = 0L;
/*  853 */     transient String groupName = null;
/*  854 */     transient Process child = null;
/*  855 */     transient boolean removed = false;
/*  856 */     transient Watchdog watchdog = null;
/*      */     
/*      */     GroupEntry(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc) {
/*  859 */       this.groupID = paramActivationGroupID;
/*  860 */       this.desc = paramActivationGroupDesc;
/*      */     }
/*      */     
/*      */     void restartServices() {
/*  864 */       Iterator localIterator = null;
/*      */       
/*  866 */       synchronized (this) {
/*  867 */         if (this.restartSet.isEmpty()) {
/*  868 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  877 */         localIterator = new HashSet(this.restartSet).iterator();
/*      */       }
/*      */       
/*  880 */       while (localIterator.hasNext()) {
/*  881 */         ??? = (ActivationID)localIterator.next();
/*      */         try {
/*  883 */           activate((ActivationID)???, true);
/*      */         } catch (Exception localException) {
/*  885 */           if (Activation.this.shuttingDown) {
/*  886 */             return;
/*      */           }
/*  888 */           System.err.println(
/*  889 */             Activation.getTextResource("rmid.restart.service.warning"));
/*  890 */           localException.printStackTrace();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     synchronized void activeGroup(ActivationInstantiator paramActivationInstantiator, long paramLong)
/*      */       throws ActivationException, UnknownGroupException
/*      */     {
/*  899 */       if (this.incarnation != paramLong) {
/*  900 */         throw new ActivationException("invalid incarnation");
/*      */       }
/*      */       
/*  903 */       if (this.group != null) {
/*  904 */         if (this.group.equals(paramActivationInstantiator)) {
/*  905 */           return;
/*      */         }
/*  907 */         throw new ActivationException("group already active");
/*      */       }
/*      */       
/*      */ 
/*  911 */       if ((this.child != null) && (this.status != 1)) {
/*  912 */         throw new ActivationException("group not being created");
/*      */       }
/*      */       
/*  915 */       this.group = paramActivationInstantiator;
/*  916 */       this.status = 0;
/*  917 */       notifyAll();
/*      */     }
/*      */     
/*      */     private void checkRemoved() throws UnknownGroupException {
/*  921 */       if (this.removed) {
/*  922 */         throw new UnknownGroupException("group removed");
/*      */       }
/*      */     }
/*      */     
/*      */     private ObjectEntry getObjectEntry(ActivationID paramActivationID)
/*      */       throws UnknownObjectException
/*      */     {
/*  929 */       if (this.removed) {
/*  930 */         throw new UnknownObjectException("object's group removed");
/*      */       }
/*  932 */       ObjectEntry localObjectEntry = (ObjectEntry)this.objects.get(paramActivationID);
/*  933 */       if (localObjectEntry == null) {
/*  934 */         throw new UnknownObjectException("object unknown");
/*      */       }
/*  936 */       return localObjectEntry;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     synchronized void registerObject(ActivationID paramActivationID, ActivationDesc paramActivationDesc, boolean paramBoolean)
/*      */       throws UnknownGroupException, ActivationException
/*      */     {
/*  944 */       checkRemoved();
/*  945 */       this.objects.put(paramActivationID, new ObjectEntry(paramActivationDesc));
/*  946 */       if (paramActivationDesc.getRestartMode() == true) {
/*  947 */         this.restartSet.add(paramActivationID);
/*      */       }
/*      */       
/*      */ 
/*  951 */       Activation.this.idTable.put(paramActivationID, this.groupID);
/*      */       
/*  953 */       if (paramBoolean) {
/*  954 */         Activation.this.addLogRecord(new LogRegisterObject(paramActivationID, paramActivationDesc));
/*      */       }
/*      */     }
/*      */     
/*      */     synchronized void unregisterObject(ActivationID paramActivationID, boolean paramBoolean)
/*      */       throws UnknownGroupException, ActivationException
/*      */     {
/*  961 */       ObjectEntry localObjectEntry = getObjectEntry(paramActivationID);
/*  962 */       localObjectEntry.removed = true;
/*  963 */       this.objects.remove(paramActivationID);
/*  964 */       if (localObjectEntry.desc.getRestartMode() == true) {
/*  965 */         this.restartSet.remove(paramActivationID);
/*      */       }
/*      */       
/*      */ 
/*  969 */       Activation.this.idTable.remove(paramActivationID);
/*  970 */       if (paramBoolean) {
/*  971 */         Activation.this.addLogRecord(new LogUnregisterObject(paramActivationID));
/*      */       }
/*      */     }
/*      */     
/*      */     synchronized void unregisterGroup(boolean paramBoolean)
/*      */       throws UnknownGroupException, ActivationException
/*      */     {
/*  978 */       checkRemoved();
/*  979 */       this.removed = true;
/*      */       
/*  981 */       for (Entry localEntry : this.objects.entrySet())
/*      */       {
/*  983 */         ActivationID localActivationID = (ActivationID)localEntry.getKey();
/*  984 */         Activation.this.idTable.remove(localActivationID);
/*  985 */         ObjectEntry localObjectEntry = (ObjectEntry)localEntry.getValue();
/*  986 */         localObjectEntry.removed = true;
/*      */       }
/*  988 */       this.objects.clear();
/*  989 */       this.restartSet.clear();
/*  990 */       reset();
/*  991 */       childGone();
/*      */       
/*      */ 
/*  994 */       if (paramBoolean) {
/*  995 */         Activation.this.addLogRecord(new LogUnregisterGroup(this.groupID));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     synchronized ActivationDesc setActivationDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc, boolean paramBoolean)
/*      */       throws UnknownObjectException, UnknownGroupException, ActivationException
/*      */     {
/* 1005 */       ObjectEntry localObjectEntry = getObjectEntry(paramActivationID);
/* 1006 */       ActivationDesc localActivationDesc = localObjectEntry.desc;
/* 1007 */       localObjectEntry.desc = paramActivationDesc;
/* 1008 */       if (paramActivationDesc.getRestartMode() == true) {
/* 1009 */         this.restartSet.add(paramActivationID);
/*      */       } else {
/* 1011 */         this.restartSet.remove(paramActivationID);
/*      */       }
/*      */       
/* 1014 */       if (paramBoolean) {
/* 1015 */         Activation.this.addLogRecord(new LogUpdateDesc(paramActivationID, paramActivationDesc));
/*      */       }
/*      */       
/* 1018 */       return localActivationDesc;
/*      */     }
/*      */     
/*      */     synchronized ActivationDesc getActivationDesc(ActivationID paramActivationID)
/*      */       throws UnknownObjectException, UnknownGroupException
/*      */     {
/* 1024 */       return getObjectEntry(paramActivationID).desc;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     synchronized ActivationGroupDesc setActivationGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc, boolean paramBoolean)
/*      */       throws UnknownGroupException, ActivationException
/*      */     {
/* 1033 */       checkRemoved();
/* 1034 */       ActivationGroupDesc localActivationGroupDesc = this.desc;
/* 1035 */       this.desc = paramActivationGroupDesc;
/*      */       
/* 1037 */       if (paramBoolean) {
/* 1038 */         Activation.this.addLogRecord(new LogUpdateGroupDesc(paramActivationGroupID, paramActivationGroupDesc));
/*      */       }
/* 1040 */       return localActivationGroupDesc;
/*      */     }
/*      */     
/*      */     synchronized void inactiveGroup(long paramLong, boolean paramBoolean)
/*      */       throws UnknownGroupException
/*      */     {
/* 1046 */       checkRemoved();
/* 1047 */       if (this.incarnation != paramLong) {
/* 1048 */         throw new UnknownGroupException("invalid incarnation");
/*      */       }
/*      */       
/* 1051 */       reset();
/* 1052 */       if (paramBoolean) {
/* 1053 */         terminate();
/* 1054 */       } else if ((this.child != null) && (this.status == 0)) {
/* 1055 */         this.status = 2;
/* 1056 */         this.watchdog.noRestart();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     synchronized void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
/*      */       throws UnknownObjectException
/*      */     {
/* 1064 */       getObjectEntry(paramActivationID).stub = paramMarshalledObject;
/*      */     }
/*      */     
/*      */     synchronized void inactiveObject(ActivationID paramActivationID)
/*      */       throws UnknownObjectException
/*      */     {
/* 1070 */       getObjectEntry(paramActivationID).reset();
/*      */     }
/*      */     
/*      */     private synchronized void reset() {
/* 1074 */       this.group = null;
/* 1075 */       for (ObjectEntry localObjectEntry : this.objects.values()) {
/* 1076 */         localObjectEntry.reset();
/*      */       }
/*      */     }
/*      */     
/*      */     private void childGone() {
/* 1081 */       if (this.child != null) {
/* 1082 */         this.child = null;
/* 1083 */         this.watchdog.dispose();
/* 1084 */         this.watchdog = null;
/* 1085 */         this.status = 0;
/* 1086 */         notifyAll();
/*      */       }
/*      */     }
/*      */     
/*      */     private void terminate() {
/* 1091 */       if ((this.child != null) && (this.status != 3)) {
/* 1092 */         this.child.destroy();
/* 1093 */         this.status = 3;
/* 1094 */         this.waitTime = (System.currentTimeMillis() + Activation.groupTimeout);
/* 1095 */         notifyAll();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void await()
/*      */     {
/*      */       for (;;)
/*      */       {
/* 1106 */         switch (this.status) {
/*      */         case 0: 
/* 1108 */           return;
/*      */         case 2: 
/* 1110 */           terminate();
/*      */         case 3: 
/*      */           try {
/* 1113 */             this.child.exitValue();
/*      */           } catch (IllegalThreadStateException localIllegalThreadStateException) {
/* 1115 */             long l = System.currentTimeMillis();
/* 1116 */             if (this.waitTime > l) {
/*      */               try {
/* 1118 */                 wait(this.waitTime - l);
/*      */               }
/*      */               catch (InterruptedException localInterruptedException2) {}
/* 1121 */               continue;
/*      */             }
/*      */           }
/*      */           
/* 1125 */           childGone();
/* 1126 */           return;
/*      */         case 1: 
/*      */           try {
/* 1129 */             wait();
/*      */           }
/*      */           catch (InterruptedException localInterruptedException1) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     void shutdownFast()
/*      */     {
/* 1138 */       Process localProcess = this.child;
/* 1139 */       if (localProcess != null) {
/* 1140 */         localProcess.destroy();
/*      */       }
/*      */     }
/*      */     
/*      */     synchronized void shutdown() {
/* 1145 */       reset();
/* 1146 */       terminate();
/* 1147 */       await();
/*      */     }
/*      */     
/*      */ 
/*      */     MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean)
/*      */       throws ActivationException
/*      */     {
/* 1154 */       Object localObject1 = null;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1160 */       for (int i = 2; i > 0; i--)
/*      */       {
/*      */         ObjectEntry localObjectEntry;
/*      */         
/*      */         ActivationInstantiator localActivationInstantiator;
/*      */         long l;
/* 1166 */         synchronized (this) {
/* 1167 */           localObjectEntry = getObjectEntry(paramActivationID);
/*      */           
/* 1169 */           if ((!paramBoolean) && (localObjectEntry.stub != null)) {
/* 1170 */             return localObjectEntry.stub;
/*      */           }
/* 1172 */           localActivationInstantiator = getInstantiator(this.groupID);
/* 1173 */           l = this.incarnation;
/*      */         }
/*      */         
/* 1176 */         int j = 0;
/* 1177 */         boolean bool = false;
/*      */         try
/*      */         {
/* 1180 */           return localObjectEntry.activate(paramActivationID, paramBoolean, localActivationInstantiator);
/*      */         } catch (NoSuchObjectException localNoSuchObjectException) {
/* 1182 */           j = 1;
/* 1183 */           localObject1 = localNoSuchObjectException;
/*      */         } catch (ConnectException localConnectException) {
/* 1185 */           j = 1;
/* 1186 */           bool = true;
/* 1187 */           localObject1 = localConnectException;
/*      */         } catch (ConnectIOException localConnectIOException) {
/* 1189 */           j = 1;
/* 1190 */           bool = true;
/* 1191 */           localObject1 = localConnectIOException;
/*      */         } catch (InactiveGroupException localInactiveGroupException) {
/* 1193 */           j = 1;
/* 1194 */           localObject1 = localInactiveGroupException;
/*      */         }
/*      */         catch (RemoteException localRemoteException) {
/* 1197 */           if (localObject1 == null) {
/* 1198 */             localObject1 = localRemoteException;
/*      */           }
/*      */         }
/*      */         
/* 1202 */         if (j != 0) {
/*      */           try
/*      */           {
/* 1205 */             System.err.println(
/* 1206 */               MessageFormat.format(
/* 1207 */               Activation.getTextResource("rmid.group.inactive"), new Object[] {((Exception)localObject1)
/* 1208 */               .toString() }));
/* 1209 */             ((Exception)localObject1).printStackTrace();
/* 1210 */             Activation.this.getGroupEntry(this.groupID)
/* 1211 */               .inactiveGroup(l, bool);
/*      */           }
/*      */           catch (UnknownGroupException localUnknownGroupException) {}
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1223 */       throw new ActivationException("object activation failed after 2 tries", (Throwable)localObject1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private ActivationInstantiator getInstantiator(ActivationGroupID paramActivationGroupID)
/*      */       throws ActivationException
/*      */     {
/* 1235 */       assert (Thread.holdsLock(this));
/*      */       
/* 1237 */       await();
/* 1238 */       if (this.group != null) {
/* 1239 */         return this.group;
/*      */       }
/* 1241 */       checkRemoved();
/* 1242 */       int i = 0;
/*      */       try
/*      */       {
/* 1245 */         this.groupName = Activation.this.Pstartgroup();
/* 1246 */         i = 1;
/* 1247 */         String[] arrayOfString = Activation.this.activationArgs(this.desc);
/* 1248 */         Activation.this.checkArgs(this.desc, arrayOfString);
/*      */         Object localObject1;
/* 1250 */         if (Activation.debugExec) {
/* 1251 */           localObject1 = new StringBuffer(arrayOfString[0]);
/*      */           
/* 1253 */           for (int j = 1; j < arrayOfString.length; j++) {
/* 1254 */             ((StringBuffer)localObject1).append(' ');
/* 1255 */             ((StringBuffer)localObject1).append(arrayOfString[j]);
/*      */           }
/* 1257 */           System.err.println(
/* 1258 */             MessageFormat.format(
/* 1259 */             Activation.getTextResource("rmid.exec.command"), new Object[] {((StringBuffer)localObject1)
/* 1260 */             .toString() }));
/*      */         }
/*      */         try
/*      */         {
/* 1264 */           this.child = Runtime.getRuntime().exec(arrayOfString);
/* 1265 */           this.status = 1;
/* 1266 */           this.incarnation += 1L;
/* 1267 */           this.watchdog = new Watchdog();
/* 1268 */           this.watchdog.start();
/* 1269 */           Activation.this.addLogRecord(new LogGroupIncarnation(paramActivationGroupID, this.incarnation));
/*      */           
/*      */ 
/*      */ 
/* 1273 */           PipeWriter.plugTogetherPair(this.child.getInputStream(), System.out, this.child
/* 1274 */             .getErrorStream(), System.err);
/*      */           
/* 1276 */           localObject1 = new MarshalOutputStream(this.child.getOutputStream());Object localObject2 = null;
/*      */           try {
/* 1277 */             ((MarshalOutputStream)localObject1).writeObject(paramActivationGroupID);
/* 1278 */             ((MarshalOutputStream)localObject1).writeObject(this.desc);
/* 1279 */             ((MarshalOutputStream)localObject1).writeLong(this.incarnation);
/* 1280 */             ((MarshalOutputStream)localObject1).flush();
/*      */           }
/*      */           catch (Throwable localThrowable2)
/*      */           {
/* 1275 */             localObject2 = localThrowable2;throw localThrowable2;
/*      */ 
/*      */           }
/*      */           finally
/*      */           {
/*      */ 
/* 1281 */             if (localObject1 != null) if (localObject2 != null) try { ((MarshalOutputStream)localObject1).close(); } catch (Throwable localThrowable3) { ((Throwable)localObject2).addSuppressed(localThrowable3); } else ((MarshalOutputStream)localObject1).close();
/*      */           }
/*      */         }
/*      */         catch (IOException localIOException) {
/* 1285 */           terminate();
/* 1286 */           throw new ActivationException("unable to create activation group", localIOException);
/*      */         }
/*      */         
/*      */         try
/*      */         {
/* 1291 */           long l1 = System.currentTimeMillis();
/* 1292 */           long l2 = l1 + Activation.execTimeout;
/*      */           do {
/* 1294 */             wait(l2 - l1);
/* 1295 */             if (this.group != null) {
/* 1296 */               return this.group;
/*      */             }
/* 1298 */             l1 = System.currentTimeMillis();
/* 1299 */             if (this.status != 1) break; } while (l1 < l2);
/*      */         }
/*      */         catch (InterruptedException localInterruptedException) {}
/*      */         
/* 1303 */         terminate();
/* 1304 */         throw new ActivationException(this.removed ? "activation group unregistered" : "timeout creating child process");
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 1309 */         if (i != 0) {
/* 1310 */           Activation.this.Vstartgroup();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private class Watchdog
/*      */       extends Thread
/*      */     {
/* 1319 */       private final Process groupProcess = GroupEntry.this.child;
/* 1320 */       private final long groupIncarnation = GroupEntry.this.incarnation;
/* 1321 */       private boolean canInterrupt = true;
/* 1322 */       private boolean shouldQuit = false;
/* 1323 */       private boolean shouldRestart = true;
/*      */       
/*      */       Watchdog() {
/* 1326 */         super();
/* 1327 */         setDaemon(true);
/*      */       }
/*      */       
/*      */       public void run()
/*      */       {
/* 1332 */         if (this.shouldQuit) {
/* 1333 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/* 1340 */           this.groupProcess.waitFor();
/*      */         } catch (InterruptedException localInterruptedException) {
/* 1342 */           return;
/*      */         }
/*      */         
/* 1345 */         int i = 0;
/* 1346 */         synchronized (GroupEntry.this) {
/* 1347 */           if (this.shouldQuit) {
/* 1348 */             return;
/*      */           }
/* 1350 */           this.canInterrupt = false;
/* 1351 */           interrupted();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1356 */           if (this.groupIncarnation == GroupEntry.this.incarnation) {
/* 1357 */             i = (this.shouldRestart) && (!Activation.this.shuttingDown) ? 1 : 0;
/* 1358 */             GroupEntry.this.reset();
/* 1359 */             GroupEntry.this.childGone();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1367 */         if (i != 0) {
/* 1368 */           GroupEntry.this.restartServices();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       void dispose()
/*      */       {
/* 1378 */         this.shouldQuit = true;
/* 1379 */         if (this.canInterrupt) {
/* 1380 */           interrupt();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       void noRestart()
/*      */       {
/* 1388 */         this.shouldRestart = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private String[] activationArgs(ActivationGroupDesc paramActivationGroupDesc)
/*      */   {
/* 1395 */     CommandEnvironment localCommandEnvironment = paramActivationGroupDesc.getCommandEnvironment();
/*      */     
/*      */ 
/* 1398 */     ArrayList localArrayList = new ArrayList();
/*      */     
/*      */ 
/* 1401 */     localArrayList.add((localCommandEnvironment != null) && (localCommandEnvironment.getCommandPath() != null) ? localCommandEnvironment
/* 1402 */       .getCommandPath() : this.command[0]);
/*      */     
/*      */ 
/*      */ 
/* 1406 */     if ((localCommandEnvironment != null) && (localCommandEnvironment.getCommandOptions() != null)) {
/* 1407 */       localArrayList.addAll(Arrays.asList(localCommandEnvironment.getCommandOptions()));
/*      */     }
/*      */     
/*      */ 
/* 1411 */     Properties localProperties = paramActivationGroupDesc.getPropertyOverrides();
/* 1412 */     if (localProperties != null) {
/* 1413 */       Enumeration localEnumeration = localProperties.propertyNames();
/* 1414 */       while (localEnumeration.hasMoreElements())
/*      */       {
/* 1416 */         String str = (String)localEnumeration.nextElement();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1422 */         localArrayList.add("-D" + str + "=" + localProperties.getProperty(str));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1429 */     for (int i = 1; i < this.command.length; i++) {
/* 1430 */       localArrayList.add(this.command[i]);
/*      */     }
/*      */     
/* 1433 */     String[] arrayOfString = new String[localArrayList.size()];
/* 1434 */     System.arraycopy(localArrayList.toArray(), 0, arrayOfString, 0, arrayOfString.length);
/*      */     
/* 1436 */     return arrayOfString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkArgs(ActivationGroupDesc paramActivationGroupDesc, String[] paramArrayOfString)
/*      */     throws SecurityException, ActivationException
/*      */   {
/* 1445 */     if (execPolicyMethod != null) {
/* 1446 */       if (paramArrayOfString == null) {
/* 1447 */         paramArrayOfString = activationArgs(paramActivationGroupDesc);
/*      */       }
/*      */       try {
/* 1450 */         execPolicyMethod.invoke(execPolicy, new Object[] { paramActivationGroupDesc, paramArrayOfString });
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/* 1452 */         Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 1453 */         if ((localThrowable instanceof SecurityException)) {
/* 1454 */           throw ((SecurityException)localThrowable);
/*      */         }
/*      */         
/* 1457 */         throw new ActivationException(execPolicyMethod.getName() + ": unexpected exception", localInvocationTargetException);
/*      */ 
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/* 1462 */         throw new ActivationException(execPolicyMethod.getName() + ": unexpected exception", localException);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class ObjectEntry
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = -5500114225321357856L;
/*      */     
/*      */     ActivationDesc desc;
/* 1474 */     volatile transient MarshalledObject<? extends Remote> stub = null;
/* 1475 */     volatile transient boolean removed = false;
/*      */     
/*      */     ObjectEntry(ActivationDesc paramActivationDesc) {
/* 1478 */       this.desc = paramActivationDesc;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     synchronized MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean, ActivationInstantiator paramActivationInstantiator)
/*      */       throws RemoteException, ActivationException
/*      */     {
/* 1487 */       MarshalledObject localMarshalledObject = this.stub;
/* 1488 */       if (this.removed)
/* 1489 */         throw new UnknownObjectException("object removed");
/* 1490 */       if ((!paramBoolean) && (localMarshalledObject != null)) {
/* 1491 */         return localMarshalledObject;
/*      */       }
/*      */       
/* 1494 */       localMarshalledObject = paramActivationInstantiator.newInstance(paramActivationID, this.desc);
/* 1495 */       this.stub = localMarshalledObject;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1500 */       return localMarshalledObject;
/*      */     }
/*      */     
/*      */     void reset() {
/* 1504 */       this.stub = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void addLogRecord(LogRecord paramLogRecord)
/*      */     throws ActivationException
/*      */   {
/* 1513 */     synchronized (this.log) {
/* 1514 */       checkShutdown();
/*      */       try {
/* 1516 */         this.log.update(paramLogRecord, true);
/*      */       } catch (Exception localException1) {
/* 1518 */         this.numUpdates = snapshotInterval;
/* 1519 */         System.err.println(getTextResource("rmid.log.update.warning"));
/* 1520 */         localException1.printStackTrace();
/*      */       }
/* 1522 */       if (++this.numUpdates < snapshotInterval) {
/* 1523 */         return;
/*      */       }
/*      */       try {
/* 1526 */         this.log.snapshot(this);
/* 1527 */         this.numUpdates = 0;
/*      */       } catch (Exception localException2) {
/* 1529 */         System.err.println(
/* 1530 */           getTextResource("rmid.log.snapshot.warning"));
/* 1531 */         localException2.printStackTrace();
/*      */         try
/*      */         {
/* 1534 */           this.system.shutdown();
/*      */         }
/*      */         catch (RemoteException localRemoteException) {}
/*      */         
/*      */ 
/* 1539 */         throw new ActivationException("log snapshot failed", localException2);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class ActLogHandler
/*      */     extends LogHandler
/*      */   {
/*      */     public Object initialSnapshot()
/*      */     {
/* 1559 */       return new Activation(null);
/*      */     }
/*      */     
/*      */     public Object applyUpdate(Object paramObject1, Object paramObject2)
/*      */       throws Exception
/*      */     {
/* 1565 */       return ((LogRecord)paramObject1).apply(paramObject2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class LogRegisterObject
/*      */     extends LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = -6280336276146085143L;
/*      */     
/*      */ 
/*      */ 
/*      */     private ActivationID id;
/*      */     
/*      */ 
/*      */ 
/*      */     private ActivationDesc desc;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     LogRegisterObject(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
/*      */     {
/* 1591 */       super();
/* 1592 */       this.id = paramActivationID;
/* 1593 */       this.desc = paramActivationDesc;
/*      */     }
/*      */     
/*      */     Object apply(Object paramObject)
/*      */     {
/*      */       try {
/* 1599 */         ((Activation)paramObject).getGroupEntry(this.desc.getGroupID()).registerObject(this.id, this.desc, false);
/*      */       } catch (Exception localException) {
/* 1601 */         System.err.println(
/* 1602 */           MessageFormat.format(
/* 1603 */           Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogRegisterObject" }));
/*      */         
/* 1605 */         localException.printStackTrace();
/*      */       }
/* 1607 */       return paramObject;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class LogUnregisterObject
/*      */     extends LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = 6269824097396935501L;
/*      */     private ActivationID id;
/*      */     
/*      */     LogUnregisterObject(ActivationID paramActivationID)
/*      */     {
/* 1619 */       super();
/* 1620 */       this.id = paramActivationID;
/*      */     }
/*      */     
/*      */     Object apply(Object paramObject)
/*      */     {
/*      */       try {
/* 1626 */         ((Activation)paramObject).getGroupEntry(this.id).unregisterObject(this.id, false);
/*      */       } catch (Exception localException) {
/* 1628 */         System.err.println(
/* 1629 */           MessageFormat.format(
/* 1630 */           Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUnregisterObject" }));
/*      */         
/* 1632 */         localException.printStackTrace();
/*      */       }
/* 1634 */       return paramObject;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class LogRegisterGroup
/*      */     extends LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = -1966827458515403625L;
/*      */     private ActivationGroupID id;
/*      */     private ActivationGroupDesc desc;
/*      */     
/*      */     LogRegisterGroup(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc)
/*      */     {
/* 1647 */       super();
/* 1648 */       this.id = paramActivationGroupID;
/* 1649 */       this.desc = paramActivationGroupDesc;
/*      */     }
/*      */     
/*      */ 
/*      */     Object apply(Object paramObject)
/*      */     {
/* 1655 */       Activation tmp19_16 = ((Activation)paramObject);tmp19_16.getClass();((Activation)paramObject).groupTable.put(this.id, new GroupEntry(tmp19_16, this.id, this.desc));
/*      */       
/* 1657 */       return paramObject;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class LogUpdateDesc
/*      */     extends LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = 545511539051179885L;
/*      */     private ActivationID id;
/*      */     private ActivationDesc desc;
/*      */     
/*      */     LogUpdateDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
/*      */     {
/* 1671 */       super();
/* 1672 */       this.id = paramActivationID;
/* 1673 */       this.desc = paramActivationDesc;
/*      */     }
/*      */     
/*      */     Object apply(Object paramObject)
/*      */     {
/*      */       try {
/* 1679 */         ((Activation)paramObject).getGroupEntry(this.id).setActivationDesc(this.id, this.desc, false);
/*      */       } catch (Exception localException) {
/* 1681 */         System.err.println(
/* 1682 */           MessageFormat.format(
/* 1683 */           Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUpdateDesc" }));
/*      */         
/* 1685 */         localException.printStackTrace();
/*      */       }
/* 1687 */       return paramObject;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class LogUpdateGroupDesc
/*      */     extends LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = -1271300989218424337L;
/*      */     private ActivationGroupID id;
/*      */     private ActivationGroupDesc desc;
/*      */     
/*      */     LogUpdateGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc)
/*      */     {
/* 1700 */       super();
/* 1701 */       this.id = paramActivationGroupID;
/* 1702 */       this.desc = paramActivationGroupDesc;
/*      */     }
/*      */     
/*      */     Object apply(Object paramObject)
/*      */     {
/*      */       try {
/* 1708 */         ((Activation)paramObject).getGroupEntry(this.id).setActivationGroupDesc(this.id, this.desc, false);
/*      */       } catch (Exception localException) {
/* 1710 */         System.err.println(
/* 1711 */           MessageFormat.format(
/* 1712 */           Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUpdateGroupDesc" }));
/*      */         
/* 1714 */         localException.printStackTrace();
/*      */       }
/* 1716 */       return paramObject;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class LogUnregisterGroup
/*      */     extends LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = -3356306586522147344L;
/*      */     private ActivationGroupID id;
/*      */     
/*      */     LogUnregisterGroup(ActivationGroupID paramActivationGroupID)
/*      */     {
/* 1728 */       super();
/* 1729 */       this.id = paramActivationGroupID;
/*      */     }
/*      */     
/*      */     Object apply(Object paramObject) {
/* 1733 */       GroupEntry localGroupEntry = (GroupEntry)((Activation)paramObject).groupTable.remove(this.id);
/*      */       try {
/* 1735 */         localGroupEntry.unregisterGroup(false);
/*      */       } catch (Exception localException) {
/* 1737 */         System.err.println(
/* 1738 */           MessageFormat.format(
/* 1739 */           Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUnregisterGroup" }));
/*      */         
/* 1741 */         localException.printStackTrace();
/*      */       }
/* 1743 */       return paramObject;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class LogGroupIncarnation
/*      */     extends LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = 4146872747377631897L;
/*      */     private ActivationGroupID id;
/*      */     private long inc;
/*      */     
/*      */     LogGroupIncarnation(ActivationGroupID paramActivationGroupID, long paramLong)
/*      */     {
/* 1756 */       super();
/* 1757 */       this.id = paramActivationGroupID;
/* 1758 */       this.inc = paramLong;
/*      */     }
/*      */     
/*      */     Object apply(Object paramObject) {
/*      */       try {
/* 1763 */         GroupEntry localGroupEntry = ((Activation)paramObject).getGroupEntry(this.id);
/* 1764 */         localGroupEntry.incarnation = this.inc;
/*      */       } catch (Exception localException) {
/* 1766 */         System.err.println(
/* 1767 */           MessageFormat.format(
/* 1768 */           Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogGroupIncarnation" }));
/*      */         
/* 1770 */         localException.printStackTrace();
/*      */       }
/* 1772 */       return paramObject;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void initCommand(String[] paramArrayOfString)
/*      */   {
/* 1780 */     this.command = new String[paramArrayOfString.length + 2];
/* 1781 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/*      */         try {
/* 1784 */           Activation.this.command[0] = (System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
/*      */         }
/*      */         catch (Exception localException) {
/* 1787 */           System.err.println(
/* 1788 */             Activation.getTextResource("rmid.unfound.java.home.property"));
/* 1789 */           Activation.this.command[0] = "java";
/*      */         }
/* 1791 */         return null;
/*      */       }
/* 1793 */     });
/* 1794 */     System.arraycopy(paramArrayOfString, 0, this.command, 1, paramArrayOfString.length);
/* 1795 */     this.command[(this.command.length - 1)] = "sun.rmi.server.ActivationGroupInit";
/*      */   }
/*      */   
/*      */   private static void bomb(String paramString) {
/* 1799 */     System.err.println("rmid: " + paramString);
/* 1800 */     System.err.println(MessageFormat.format(getTextResource("rmid.usage"), new Object[] { "rmid" }));
/*      */     
/* 1802 */     System.exit(1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class DefaultExecPolicy
/*      */   {
/*      */     public void checkExecCommand(ActivationGroupDesc paramActivationGroupDesc, String[] paramArrayOfString)
/*      */       throws SecurityException
/*      */     {
/* 1815 */       PermissionCollection localPermissionCollection = getExecPermissions();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1820 */       Properties localProperties = paramActivationGroupDesc.getPropertyOverrides();
/* 1821 */       String str1; Object localObject3; if (localProperties != null) {
/* 1822 */         localObject1 = localProperties.propertyNames();
/* 1823 */         while (((Enumeration)localObject1).hasMoreElements()) {
/* 1824 */           localObject2 = (String)((Enumeration)localObject1).nextElement();
/* 1825 */           str1 = localProperties.getProperty((String)localObject2);
/* 1826 */           localObject3 = "-D" + (String)localObject2 + "=" + str1;
/*      */           try {
/* 1828 */             checkPermission(localPermissionCollection, new ExecOptionPermission((String)localObject3));
/*      */           }
/*      */           catch (AccessControlException localAccessControlException) {
/* 1831 */             if (str1.equals("")) {
/* 1832 */               checkPermission(localPermissionCollection, new ExecOptionPermission("-D" + (String)localObject2));
/*      */             }
/*      */             else {
/* 1835 */               throw localAccessControlException;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1845 */       Object localObject1 = paramActivationGroupDesc.getClassName();
/* 1846 */       if (((localObject1 != null) && 
/* 1847 */         (!((String)localObject1).equals(ActivationGroupImpl.class
/* 1848 */         .getName()))) || 
/* 1849 */         (paramActivationGroupDesc.getLocation() != null) || 
/* 1850 */         (paramActivationGroupDesc.getData() != null))
/*      */       {
/* 1852 */         throw new AccessControlException("access denied (custom group implementation not allowed)");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1861 */       Object localObject2 = paramActivationGroupDesc.getCommandEnvironment();
/* 1862 */       if (localObject2 != null) {
/* 1863 */         str1 = ((CommandEnvironment)localObject2).getCommandPath();
/* 1864 */         if (str1 != null) {
/* 1865 */           checkPermission(localPermissionCollection, new ExecPermission(str1));
/*      */         }
/*      */         
/* 1868 */         localObject3 = ((CommandEnvironment)localObject2).getCommandOptions();
/* 1869 */         if (localObject3 != null) {
/* 1870 */           for (String str2 : localObject3) {
/* 1871 */             checkPermission(localPermissionCollection, new ExecOptionPermission(str2));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     static void checkConfiguration()
/*      */     {
/* 1885 */       Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Policy run() {
/* 1887 */           return Policy.getPolicy();
/*      */         }
/*      */       });
/* 1890 */       if (!(localPolicy instanceof PolicyFile)) {
/* 1891 */         return;
/*      */       }
/* 1893 */       PermissionCollection localPermissionCollection = getExecPermissions();
/* 1894 */       Enumeration localEnumeration = localPermissionCollection.elements();
/* 1895 */       while (localEnumeration.hasMoreElements())
/*      */       {
/* 1897 */         Permission localPermission = (Permission)localEnumeration.nextElement();
/* 1898 */         if (((localPermission instanceof AllPermission)) || ((localPermission instanceof ExecPermission)) || ((localPermission instanceof ExecOptionPermission)))
/*      */         {
/*      */ 
/*      */ 
/* 1902 */           return;
/*      */         }
/*      */       }
/* 1905 */       System.err.println(Activation.getTextResource("rmid.exec.perms.inadequate"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private static PermissionCollection getExecPermissions()
/*      */     {
/* 1916 */       PermissionCollection localPermissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public PermissionCollection run() {
/* 1919 */           CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
/*      */           
/* 1921 */           Policy localPolicy = Policy.getPolicy();
/* 1922 */           if (localPolicy != null) {
/* 1923 */             return localPolicy.getPermissions(localCodeSource);
/*      */           }
/* 1925 */           return new Permissions();
/*      */         }
/*      */         
/*      */ 
/* 1929 */       });
/* 1930 */       return localPermissionCollection;
/*      */     }
/*      */     
/*      */ 
/*      */     private static void checkPermission(PermissionCollection paramPermissionCollection, Permission paramPermission)
/*      */       throws AccessControlException
/*      */     {
/* 1937 */       if (!paramPermissionCollection.implies(paramPermission))
/*      */       {
/* 1939 */         throw new AccessControlException("access denied " + paramPermission.toString());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void main(String[] paramArrayOfString)
/*      */   {
/* 1949 */     int i = 0;
/*      */     
/*      */ 
/*      */ 
/* 1953 */     if (System.getSecurityManager() == null) {
/* 1954 */       System.setSecurityManager(new SecurityManager());
/*      */     }
/*      */     try
/*      */     {
/* 1958 */       Exception localException1 = 1098;
/* 1959 */       ActivationServerSocketFactory localActivationServerSocketFactory = null;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1966 */       Channel localChannel = (Channel)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Channel run() throws IOException {
/* 1969 */           return System.inheritedChannel();
/*      */         }
/*      */       });
/*      */       
/* 1973 */       if ((localChannel != null) && ((localChannel instanceof ServerSocketChannel)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1979 */         AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */         {
/*      */           public Void run() throws IOException
/*      */           {
/* 1983 */             File localFile = Files.createTempFile("rmid-err", null, new FileAttribute[0]).toFile();
/* 1984 */             PrintStream localPrintStream = new PrintStream(new FileOutputStream(localFile));
/*      */             
/* 1986 */             System.setErr(localPrintStream);
/* 1987 */             return null;
/*      */           }
/*      */           
/*      */ 
/* 1991 */         });
/* 1992 */         localObject = ((ServerSocketChannel)localChannel).socket();
/* 1993 */         localException1 = ((ServerSocket)localObject).getLocalPort();
/* 1994 */         localActivationServerSocketFactory = new ActivationServerSocketFactory((ServerSocket)localObject);
/*      */         
/* 1996 */         System.err.println(new Date());
/* 1997 */         System.err.println(getTextResource("rmid.inherited.channel.info") + ": " + localChannel);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2002 */       Object localObject = null;
/* 2003 */       ArrayList localArrayList = new ArrayList();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2008 */       for (int j = 0; j < paramArrayOfString.length; j++) {
/* 2009 */         if (paramArrayOfString[j].equals("-port")) {
/* 2010 */           if (localActivationServerSocketFactory != null) {
/* 2011 */             bomb(getTextResource("rmid.syntax.port.badarg"));
/*      */           }
/* 2013 */           if (j + 1 < paramArrayOfString.length) {
/*      */             try {
/* 2015 */               localException1 = Integer.parseInt(paramArrayOfString[(++j)]);
/*      */             } catch (NumberFormatException localNumberFormatException) {
/* 2017 */               bomb(getTextResource("rmid.syntax.port.badnumber"));
/*      */             }
/*      */           } else {
/* 2020 */             bomb(getTextResource("rmid.syntax.port.missing"));
/*      */           }
/*      */         }
/* 2023 */         else if (paramArrayOfString[j].equals("-log")) {
/* 2024 */           if (j + 1 < paramArrayOfString.length) {
/* 2025 */             localObject = paramArrayOfString[(++j)];
/*      */           } else {
/* 2027 */             bomb(getTextResource("rmid.syntax.log.missing"));
/*      */           }
/*      */         }
/* 2030 */         else if (paramArrayOfString[j].equals("-stop")) {
/* 2031 */           i = 1;
/*      */         }
/* 2033 */         else if (paramArrayOfString[j].startsWith("-C")) {
/* 2034 */           localArrayList.add(paramArrayOfString[j].substring(2));
/*      */         }
/*      */         else {
/* 2037 */           bomb(MessageFormat.format(
/* 2038 */             getTextResource("rmid.syntax.illegal.option"), new Object[] { paramArrayOfString[j] }));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2043 */       if (localObject == null) {
/* 2044 */         if (localActivationServerSocketFactory != null) {
/* 2045 */           bomb(getTextResource("rmid.syntax.log.required"));
/*      */         } else {
/* 2047 */           localObject = "log";
/*      */         }
/*      */       }
/*      */       
/* 2051 */       debugExec = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.activation.debugExec"))).booleanValue();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2057 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.activation.execPolicy", null));
/*      */       
/* 2059 */       if (str == null) {
/* 2060 */         if (i == 0) {
/* 2061 */           DefaultExecPolicy.checkConfiguration();
/*      */         }
/* 2063 */         str = "default";
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2069 */       if (!str.equals("none")) {
/* 2070 */         if ((str.equals("")) || 
/* 2071 */           (str.equals("default")))
/*      */         {
/* 2073 */           str = DefaultExecPolicy.class.getName();
/*      */         }
/*      */         try
/*      */         {
/* 2077 */           Class localClass = getRMIClass(str);
/* 2078 */           execPolicy = localClass.newInstance();
/*      */           
/* 2080 */           execPolicyMethod = localClass.getMethod("checkExecCommand", new Class[] { ActivationGroupDesc.class, String[].class });
/*      */         }
/*      */         catch (Exception localException3)
/*      */         {
/* 2084 */           if (debugExec) {
/* 2085 */             System.err.println(
/* 2086 */               getTextResource("rmid.exec.policy.exception"));
/* 2087 */             localException3.printStackTrace();
/*      */           }
/* 2089 */           bomb(getTextResource("rmid.exec.policy.invalid"));
/*      */         }
/*      */       }
/*      */       
/* 2093 */       if (i == 1) {
/* 2094 */         localException3 = localException1;
/* 2095 */         AccessController.doPrivileged(new PrivilegedAction() {
/*      */           public Void run() {
/* 2097 */             System.setProperty("java.rmi.activation.port", 
/* 2098 */               Integer.toString(this.val$finalPort));
/* 2099 */             return null;
/*      */           }
/* 2101 */         });
/* 2102 */         ActivationSystem localActivationSystem = ActivationGroup.getSystem();
/* 2103 */         localActivationSystem.shutdown();
/* 2104 */         System.exit(0);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2120 */       startActivation(localException1, localActivationServerSocketFactory, (String)localObject, 
/* 2121 */         (String[])localArrayList.toArray(new String[localArrayList.size()]));
/*      */       try
/*      */       {
/*      */         for (;;)
/*      */         {
/* 2126 */           Thread.sleep(Long.MAX_VALUE);
/*      */         }
/*      */       }
/*      */       catch (InterruptedException localInterruptedException) {}
/*      */     } catch (Exception localException2) {
/* 2131 */       System.err.println(
/* 2132 */         MessageFormat.format(
/* 2133 */         getTextResource("rmid.unexpected.exception"), new Object[] { localException2 }));
/* 2134 */       localException2.printStackTrace();
/*      */       
/* 2136 */       System.exit(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static String getTextResource(String paramString)
/*      */   {
/* 2143 */     if (resources == null) {
/*      */       try {
/* 2145 */         resources = ResourceBundle.getBundle("sun.rmi.server.resources.rmid");
/*      */       }
/*      */       catch (MissingResourceException localMissingResourceException1) {}
/*      */       
/* 2149 */       if (resources == null)
/*      */       {
/* 2151 */         return "[missing resource file: " + paramString + "]";
/*      */       }
/*      */     }
/*      */     
/* 2155 */     String str = null;
/*      */     try {
/* 2157 */       str = resources.getString(paramString);
/*      */     }
/*      */     catch (MissingResourceException localMissingResourceException2) {}
/*      */     
/* 2161 */     if (str == null) {
/* 2162 */       return "[missing resource: " + paramString + "]";
/*      */     }
/* 2164 */     return str;
/*      */   }
/*      */   
/*      */   private static Class<?> getRMIClass(String paramString)
/*      */     throws Exception
/*      */   {
/* 2170 */     return RMIClassLoader.loadClass(paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private synchronized String Pstartgroup()
/*      */     throws ActivationException
/*      */   {
/*      */     for (;;)
/*      */     {
/* 2185 */       checkShutdown();
/*      */       
/* 2187 */       if (this.groupSemaphore > 0) {
/* 2188 */         this.groupSemaphore -= 1;
/* 2189 */         return "Group-" + this.groupCounter++;
/*      */       }
/*      */       try
/*      */       {
/* 2193 */         wait();
/*      */       }
/*      */       catch (InterruptedException localInterruptedException) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private synchronized void Vstartgroup()
/*      */   {
/* 2206 */     this.groupSemaphore += 1;
/* 2207 */     notifyAll();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class ActivationServerSocketFactory
/*      */     implements RMIServerSocketFactory
/*      */   {
/*      */     private final ServerSocket serverSocket;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     ActivationServerSocketFactory(ServerSocket paramServerSocket)
/*      */     {
/* 2230 */       this.serverSocket = paramServerSocket;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public ServerSocket createServerSocket(int paramInt)
/*      */       throws IOException
/*      */     {
/* 2240 */       return new DelayedAcceptServerSocket(this.serverSocket);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class DelayedAcceptServerSocket
/*      */     extends ServerSocket
/*      */   {
/*      */     private final ServerSocket serverSocket;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     DelayedAcceptServerSocket(ServerSocket paramServerSocket)
/*      */       throws IOException
/*      */     {
/* 2258 */       this.serverSocket = paramServerSocket;
/*      */     }
/*      */     
/*      */     public void bind(SocketAddress paramSocketAddress) throws IOException {
/* 2262 */       this.serverSocket.bind(paramSocketAddress);
/*      */     }
/*      */     
/*      */     public void bind(SocketAddress paramSocketAddress, int paramInt)
/*      */       throws IOException
/*      */     {
/* 2268 */       this.serverSocket.bind(paramSocketAddress, paramInt);
/*      */     }
/*      */     
/*      */     public InetAddress getInetAddress() {
/* 2272 */       (InetAddress)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public InetAddress run()
/*      */         {
/* 2276 */           return DelayedAcceptServerSocket.this.serverSocket.getInetAddress();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */     public int getLocalPort() {
/* 2282 */       return this.serverSocket.getLocalPort();
/*      */     }
/*      */     
/*      */     public SocketAddress getLocalSocketAddress() {
/* 2286 */       (SocketAddress)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public SocketAddress run()
/*      */         {
/* 2290 */           return DelayedAcceptServerSocket.this.serverSocket.getLocalSocketAddress();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Socket accept()
/*      */       throws IOException
/*      */     {
/* 2300 */       synchronized (Activation.initLock) {
/*      */         try {
/* 2302 */           while (!Activation.initDone) {
/* 2303 */             Activation.initLock.wait();
/*      */           }
/*      */         } catch (InterruptedException localInterruptedException) {
/* 2306 */           throw new AssertionError(localInterruptedException);
/*      */         }
/*      */       }
/* 2309 */       return this.serverSocket.accept();
/*      */     }
/*      */     
/*      */     public void close() throws IOException {
/* 2313 */       this.serverSocket.close();
/*      */     }
/*      */     
/*      */     public ServerSocketChannel getChannel() {
/* 2317 */       return this.serverSocket.getChannel();
/*      */     }
/*      */     
/*      */     public boolean isBound() {
/* 2321 */       return this.serverSocket.isBound();
/*      */     }
/*      */     
/*      */     public boolean isClosed() {
/* 2325 */       return this.serverSocket.isClosed();
/*      */     }
/*      */     
/*      */     public void setSoTimeout(int paramInt)
/*      */       throws SocketException
/*      */     {
/* 2331 */       this.serverSocket.setSoTimeout(paramInt);
/*      */     }
/*      */     
/*      */     public int getSoTimeout() throws IOException {
/* 2335 */       return this.serverSocket.getSoTimeout();
/*      */     }
/*      */     
/*      */     public void setReuseAddress(boolean paramBoolean) throws SocketException {
/* 2339 */       this.serverSocket.setReuseAddress(paramBoolean);
/*      */     }
/*      */     
/*      */     public boolean getReuseAddress() throws SocketException {
/* 2343 */       return this.serverSocket.getReuseAddress();
/*      */     }
/*      */     
/*      */     public String toString() {
/* 2347 */       return this.serverSocket.toString();
/*      */     }
/*      */     
/*      */     public void setReceiveBufferSize(int paramInt)
/*      */       throws SocketException
/*      */     {
/* 2353 */       this.serverSocket.setReceiveBufferSize(paramInt);
/*      */     }
/*      */     
/*      */     public int getReceiveBufferSize()
/*      */       throws SocketException
/*      */     {
/* 2359 */       return this.serverSocket.getReceiveBufferSize();
/*      */     }
/*      */   }
/*      */   
/*      */   private static abstract class LogRecord
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 8395140512322687529L;
/*      */     
/*      */     abstract Object apply(Object paramObject)
/*      */       throws Exception;
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\Activation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
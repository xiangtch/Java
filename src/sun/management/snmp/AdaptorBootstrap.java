/*     */ package sun.management.snmp;
/*     */ 
/*     */ import com.sun.jmx.snmp.IPAcl.SnmpAcl;
/*     */ import com.sun.jmx.snmp.InetAddressAcl;
/*     */ import com.sun.jmx.snmp.daemon.CommunicationException;
/*     */ import com.sun.jmx.snmp.daemon.SnmpAdaptorServer;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import sun.management.Agent;
/*     */ import sun.management.AgentConfigurationError;
/*     */ import sun.management.FileSystem;
/*     */ import sun.management.snmp.jvminstr.JVM_MANAGEMENT_MIB_IMPL;
/*     */ import sun.management.snmp.jvminstr.NotificationTarget;
/*     */ import sun.management.snmp.jvminstr.NotificationTargetImpl;
/*     */ import sun.management.snmp.util.JvmContextFactory;
/*     */ import sun.management.snmp.util.MibLogger;
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
/*     */ public final class AdaptorBootstrap
/*     */ {
/*  61 */   private static final MibLogger log = new MibLogger(AdaptorBootstrap.class);
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
/*     */   private SnmpAdaptorServer adaptor;
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
/*     */   private JVM_MANAGEMENT_MIB_IMPL jvmmib;
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
/*     */   private AdaptorBootstrap(SnmpAdaptorServer paramSnmpAdaptorServer, JVM_MANAGEMENT_MIB_IMPL paramJVM_MANAGEMENT_MIB_IMPL)
/*     */   {
/* 103 */     this.jvmmib = paramJVM_MANAGEMENT_MIB_IMPL;
/* 104 */     this.adaptor = paramSnmpAdaptorServer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getDefaultFileName(String paramString)
/*     */   {
/* 113 */     String str = File.separator;
/* 114 */     return System.getProperty("java.home") + str + "lib" + str + "management" + str + paramString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static List<NotificationTarget> getTargetList(InetAddressAcl paramInetAddressAcl, int paramInt)
/*     */   {
/* 124 */     ArrayList localArrayList = new ArrayList();
/*     */     
/* 126 */     if (paramInetAddressAcl != null) {
/* 127 */       if (log.isDebugOn()) {
/* 128 */         log.debug("getTargetList", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.processing"));
/*     */       }
/* 130 */       Enumeration localEnumeration1 = paramInetAddressAcl.getTrapDestinations();
/* 131 */       while (localEnumeration1.hasMoreElements()) {
/* 132 */         InetAddress localInetAddress = (InetAddress)localEnumeration1.nextElement();
/*     */         
/* 134 */         Enumeration localEnumeration2 = paramInetAddressAcl.getTrapCommunities(localInetAddress);
/* 135 */         while (localEnumeration2.hasMoreElements()) {
/* 136 */           String str = (String)localEnumeration2.nextElement();
/* 137 */           NotificationTargetImpl localNotificationTargetImpl = new NotificationTargetImpl(localInetAddress, paramInt, str);
/*     */           
/*     */ 
/*     */ 
/* 141 */           if (log.isDebugOn())
/* 142 */             log.debug("getTargetList", 
/* 143 */               Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.adding", new String[] {localNotificationTargetImpl
/* 144 */               .toString() }));
/* 145 */           localArrayList.add(localNotificationTargetImpl);
/*     */         }
/*     */       }
/*     */     }
/* 149 */     return localArrayList;
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
/*     */   public static synchronized AdaptorBootstrap initialize()
/*     */   {
/* 162 */     Properties localProperties = Agent.loadManagementProperties();
/* 163 */     if (localProperties == null) { return null;
/*     */     }
/* 165 */     String str = localProperties.getProperty("com.sun.management.snmp.port");
/*     */     
/* 167 */     return initialize(str, localProperties);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized AdaptorBootstrap initialize(String paramString, Properties paramProperties)
/*     */   {
/* 177 */     if (paramString.length() == 0) paramString = "161";
/*     */     int i;
/*     */     try {
/* 180 */       i = Integer.parseInt(paramString);
/*     */     } catch (NumberFormatException localNumberFormatException1) {
/* 182 */       throw new AgentConfigurationError("agent.err.invalid.snmp.port", localNumberFormatException1, new String[] { paramString });
/*     */     }
/*     */     
/* 185 */     if (i < 0) {
/* 186 */       throw new AgentConfigurationError("agent.err.invalid.snmp.port", new String[] { paramString });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 191 */     String str1 = paramProperties.getProperty("com.sun.management.snmp.trap", "162");
/*     */     
/*     */     int j;
/*     */     try
/*     */     {
/* 196 */       j = Integer.parseInt(str1);
/*     */     } catch (NumberFormatException localNumberFormatException2) {
/* 198 */       throw new AgentConfigurationError("agent.err.invalid.snmp.trap.port", localNumberFormatException2, new String[] { str1 });
/*     */     }
/*     */     
/* 201 */     if (j < 0) {
/* 202 */       throw new AgentConfigurationError("agent.err.invalid.snmp.trap.port", new String[] { str1 });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 207 */     String str2 = paramProperties.getProperty("com.sun.management.snmp.interface", "localhost");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 212 */     String str3 = getDefaultFileName("snmp.acl");
/*     */     
/* 214 */     String str4 = paramProperties.getProperty("com.sun.management.snmp.acl.file", str3);
/*     */     
/*     */ 
/* 217 */     String str5 = paramProperties.getProperty("com.sun.management.snmp.acl", "true");
/*     */     
/* 219 */     boolean bool = Boolean.valueOf(str5).booleanValue();
/*     */     
/* 221 */     if (bool) { checkAclFile(str4);
/*     */     }
/* 223 */     AdaptorBootstrap localAdaptorBootstrap = null;
/*     */     try {
/* 225 */       localAdaptorBootstrap = getAdaptorBootstrap(i, j, str2, bool, str4);
/*     */     }
/*     */     catch (Exception localException) {
/* 228 */       throw new AgentConfigurationError("agent.err.exception", localException, new String[] { localException.getMessage() });
/*     */     }
/* 230 */     return localAdaptorBootstrap;
/*     */   }
/*     */   
/*     */ 
/*     */   private static AdaptorBootstrap getAdaptorBootstrap(int paramInt1, int paramInt2, String paramString1, boolean paramBoolean, String paramString2)
/*     */   {
/*     */     InetAddress localInetAddress;
/*     */     try
/*     */     {
/* 239 */       localInetAddress = InetAddress.getByName(paramString1);
/*     */     } catch (UnknownHostException localUnknownHostException1) {
/* 241 */       throw new AgentConfigurationError("agent.err.unknown.snmp.interface", localUnknownHostException1, new String[] { paramString1 });
/*     */     }
/* 243 */     if (log.isDebugOn()) {
/* 244 */       log.debug("initialize", 
/* 245 */         Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.starting\n\tcom.sun.management.snmp.port=" + paramInt1 + "\n\t" + "com.sun.management.snmp.trap" + "=" + paramInt2 + "\n\t" + "com.sun.management.snmp.interface" + "=" + localInetAddress + (paramBoolean ? "\n\tcom.sun.management.snmp.acl.file=" + paramString2 : "\n\tNo ACL") + ""));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     InetAddressAcl localInetAddressAcl;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 256 */       localInetAddressAcl = paramBoolean ? new SnmpAcl(System.getProperty("user.name"), paramString2) : null;
/*     */     }
/*     */     catch (UnknownHostException localUnknownHostException2) {
/* 259 */       throw new AgentConfigurationError("agent.err.unknown.snmp.interface", localUnknownHostException2, new String[] { localUnknownHostException2.getMessage() });
/*     */     }
/*     */     
/*     */ 
/* 263 */     SnmpAdaptorServer localSnmpAdaptorServer = new SnmpAdaptorServer(localInetAddressAcl, paramInt1, localInetAddress);
/*     */     
/* 265 */     localSnmpAdaptorServer.setUserDataFactory(new JvmContextFactory());
/* 266 */     localSnmpAdaptorServer.setTrapPort(paramInt2);
/*     */     
/*     */ 
/*     */ 
/* 270 */     JVM_MANAGEMENT_MIB_IMPL localJVM_MANAGEMENT_MIB_IMPL = new JVM_MANAGEMENT_MIB_IMPL();
/*     */     try {
/* 272 */       localJVM_MANAGEMENT_MIB_IMPL.init();
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 274 */       throw new AgentConfigurationError("agent.err.snmp.mib.init.failed", localIllegalAccessException, new String[] { localIllegalAccessException.getMessage() });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 279 */     localJVM_MANAGEMENT_MIB_IMPL.addTargets(getTargetList(localInetAddressAcl, paramInt2));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 289 */       localSnmpAdaptorServer.start(Long.MAX_VALUE);
/*     */     } catch (Exception localException) {
/* 291 */       Object localObject = localException;
/* 292 */       if ((localException instanceof CommunicationException)) {
/* 293 */         Throwable localThrowable = ((Throwable)localObject).getCause();
/* 294 */         if (localThrowable != null) { localObject = localThrowable;
/*     */         }
/*     */       }
/*     */       
/* 298 */       throw new AgentConfigurationError("agent.err.snmp.adaptor.start.failed", (Throwable)localObject, new String[] { localInetAddress + ":" + paramInt1, "(" + ((Throwable)localObject).getMessage() + ")" });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 304 */     if (!localSnmpAdaptorServer.isActive()) {
/* 305 */       throw new AgentConfigurationError("agent.err.snmp.adaptor.start.failed", new String[] { localInetAddress + ":" + paramInt1 });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 312 */       localSnmpAdaptorServer.addMib(localJVM_MANAGEMENT_MIB_IMPL);
/*     */       
/*     */ 
/*     */ 
/* 316 */       localJVM_MANAGEMENT_MIB_IMPL.setSnmpAdaptor(localSnmpAdaptorServer);
/*     */     } catch (RuntimeException localRuntimeException) {
/* 318 */       new AdaptorBootstrap(localSnmpAdaptorServer, localJVM_MANAGEMENT_MIB_IMPL).terminate();
/* 319 */       throw localRuntimeException;
/*     */     }
/*     */     
/* 322 */     log.debug("initialize", 
/* 323 */       Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.initialize1"));
/* 324 */     log.config("initialize", 
/* 325 */       Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.initialize2", new String[] {localInetAddress
/* 326 */       .toString(), Integer.toString(localSnmpAdaptorServer.getPort()) }));
/* 327 */     return new AdaptorBootstrap(localSnmpAdaptorServer, localJVM_MANAGEMENT_MIB_IMPL);
/*     */   }
/*     */   
/*     */   private static void checkAclFile(String paramString) {
/* 331 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 332 */       throw new AgentConfigurationError("agent.err.acl.file.notset");
/*     */     }
/* 334 */     File localFile = new File(paramString);
/* 335 */     if (!localFile.exists()) {
/* 336 */       throw new AgentConfigurationError("agent.err.acl.file.notfound", new String[] { paramString });
/*     */     }
/* 338 */     if (!localFile.canRead()) {
/* 339 */       throw new AgentConfigurationError("agent.err.acl.file.not.readable", new String[] { paramString });
/*     */     }
/*     */     
/* 342 */     FileSystem localFileSystem = FileSystem.open();
/*     */     try {
/* 344 */       if ((localFileSystem.supportsFileSecurity(localFile)) && 
/* 345 */         (!localFileSystem.isAccessUserOnly(localFile))) {
/* 346 */         throw new AgentConfigurationError("agent.err.acl.file.access.notrestricted", new String[] { paramString });
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 351 */       throw new AgentConfigurationError("agent.err.acl.file.read.failed", new String[] { paramString });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized int getPort()
/*     */   {
/* 363 */     if (this.adaptor != null) return this.adaptor.getPort();
/* 364 */     return 0;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public synchronized void terminate()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 307	sun/management/snmp/AdaptorBootstrap:adaptor	Lcom/sun/jmx/snmp/daemon/SnmpAdaptorServer;
/*     */     //   4: ifnonnull +4 -> 8
/*     */     //   7: return
/*     */     //   8: aload_0
/*     */     //   9: getfield 308	sun/management/snmp/AdaptorBootstrap:jvmmib	Lsun/management/snmp/jvminstr/JVM_MANAGEMENT_MIB_IMPL;
/*     */     //   12: invokevirtual 365	sun/management/snmp/jvminstr/JVM_MANAGEMENT_MIB_IMPL:terminate	()V
/*     */     //   15: aload_0
/*     */     //   16: aconst_null
/*     */     //   17: putfield 308	sun/management/snmp/AdaptorBootstrap:jvmmib	Lsun/management/snmp/jvminstr/JVM_MANAGEMENT_MIB_IMPL;
/*     */     //   20: goto +32 -> 52
/*     */     //   23: astore_1
/*     */     //   24: getstatic 309	sun/management/snmp/AdaptorBootstrap:log	Lsun/management/snmp/util/MibLogger;
/*     */     //   27: ldc 35
/*     */     //   29: aload_1
/*     */     //   30: invokevirtual 325	java/lang/Exception:toString	()Ljava/lang/String;
/*     */     //   33: invokevirtual 373	sun/management/snmp/util/MibLogger:debug	(Ljava/lang/String;Ljava/lang/String;)V
/*     */     //   36: aload_0
/*     */     //   37: aconst_null
/*     */     //   38: putfield 308	sun/management/snmp/AdaptorBootstrap:jvmmib	Lsun/management/snmp/jvminstr/JVM_MANAGEMENT_MIB_IMPL;
/*     */     //   41: goto +11 -> 52
/*     */     //   44: astore_2
/*     */     //   45: aload_0
/*     */     //   46: aconst_null
/*     */     //   47: putfield 308	sun/management/snmp/AdaptorBootstrap:jvmmib	Lsun/management/snmp/jvminstr/JVM_MANAGEMENT_MIB_IMPL;
/*     */     //   50: aload_2
/*     */     //   51: athrow
/*     */     //   52: aload_0
/*     */     //   53: getfield 307	sun/management/snmp/AdaptorBootstrap:adaptor	Lcom/sun/jmx/snmp/daemon/SnmpAdaptorServer;
/*     */     //   56: invokevirtual 312	com/sun/jmx/snmp/daemon/SnmpAdaptorServer:stop	()V
/*     */     //   59: aload_0
/*     */     //   60: aconst_null
/*     */     //   61: putfield 307	sun/management/snmp/AdaptorBootstrap:adaptor	Lcom/sun/jmx/snmp/daemon/SnmpAdaptorServer;
/*     */     //   64: goto +11 -> 75
/*     */     //   67: astore_3
/*     */     //   68: aload_0
/*     */     //   69: aconst_null
/*     */     //   70: putfield 307	sun/management/snmp/AdaptorBootstrap:adaptor	Lcom/sun/jmx/snmp/daemon/SnmpAdaptorServer;
/*     */     //   73: aload_3
/*     */     //   74: athrow
/*     */     //   75: return
/*     */     // Line number table:
/*     */     //   Java source line #371	-> byte code offset #0
/*     */     //   Java source line #377	-> byte code offset #8
/*     */     //   Java source line #384	-> byte code offset #15
/*     */     //   Java source line #385	-> byte code offset #20
/*     */     //   Java source line #378	-> byte code offset #23
/*     */     //   Java source line #381	-> byte code offset #24
/*     */     //   Java source line #382	-> byte code offset #30
/*     */     //   Java source line #381	-> byte code offset #33
/*     */     //   Java source line #384	-> byte code offset #36
/*     */     //   Java source line #385	-> byte code offset #41
/*     */     //   Java source line #384	-> byte code offset #44
/*     */     //   Java source line #385	-> byte code offset #50
/*     */     //   Java source line #390	-> byte code offset #52
/*     */     //   Java source line #392	-> byte code offset #59
/*     */     //   Java source line #393	-> byte code offset #64
/*     */     //   Java source line #392	-> byte code offset #67
/*     */     //   Java source line #393	-> byte code offset #73
/*     */     //   Java source line #394	-> byte code offset #75
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	76	0	this	AdaptorBootstrap
/*     */     //   23	7	1	localException	Exception
/*     */     //   44	7	2	localObject1	Object
/*     */     //   67	7	3	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   8	15	23	java/lang/Exception
/*     */     //   8	15	44	finally
/*     */     //   23	36	44	finally
/*     */     //   52	59	67	finally
/*     */   }
/*     */   
/*     */   public static abstract interface DefaultValues
/*     */   {
/*     */     public static final String PORT = "161";
/*     */     public static final String CONFIG_FILE_NAME = "management.properties";
/*     */     public static final String TRAP_PORT = "162";
/*     */     public static final String USE_ACL = "true";
/*     */     public static final String ACL_FILE_NAME = "snmp.acl";
/*     */     public static final String BIND_ADDRESS = "localhost";
/*     */   }
/*     */   
/*     */   public static abstract interface PropertyNames
/*     */   {
/*     */     public static final String PORT = "com.sun.management.snmp.port";
/*     */     public static final String CONFIG_FILE_NAME = "com.sun.management.config.file";
/*     */     public static final String TRAP_PORT = "com.sun.management.snmp.trap";
/*     */     public static final String USE_ACL = "com.sun.management.snmp.acl";
/*     */     public static final String ACL_FILE_NAME = "com.sun.management.snmp.acl.file";
/*     */     public static final String BIND_ADDRESS = "com.sun.management.snmp.interface";
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\snmp\AdaptorBootstrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */